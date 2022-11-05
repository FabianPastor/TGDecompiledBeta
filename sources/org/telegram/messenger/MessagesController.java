package org.telegram.messenger;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import androidx.core.util.Consumer;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.support.LongSparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$ChatParticipants;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$DialogPeer;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DraftMessage;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputChannel;
import org.telegram.tgnet.TLRPC$InputChatPhoto;
import org.telegram.tgnet.TLRPC$InputDialogPeer;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputPhoto;
import org.telegram.tgnet.TLRPC$InputUser;
import org.telegram.tgnet.TLRPC$JSONValue;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageReplies;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$PeerNotifySettings;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$Poll;
import org.telegram.tgnet.TLRPC$RecentMeUrl;
import org.telegram.tgnet.TLRPC$ReportReason;
import org.telegram.tgnet.TLRPC$SendMessageAction;
import org.telegram.tgnet.TLRPC$TL_account_createTheme;
import org.telegram.tgnet.TLRPC$TL_account_getNotifySettings;
import org.telegram.tgnet.TLRPC$TL_account_installTheme;
import org.telegram.tgnet.TLRPC$TL_account_installWallPaper;
import org.telegram.tgnet.TLRPC$TL_account_registerDevice;
import org.telegram.tgnet.TLRPC$TL_account_reportPeer;
import org.telegram.tgnet.TLRPC$TL_account_saveTheme;
import org.telegram.tgnet.TLRPC$TL_account_unregisterDevice;
import org.telegram.tgnet.TLRPC$TL_account_updateStatus;
import org.telegram.tgnet.TLRPC$TL_account_updateTheme;
import org.telegram.tgnet.TLRPC$TL_account_uploadTheme;
import org.telegram.tgnet.TLRPC$TL_account_uploadWallPaper;
import org.telegram.tgnet.TLRPC$TL_auth_loggedOut;
import org.telegram.tgnet.TLRPC$TL_boolFalse;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_botInfo;
import org.telegram.tgnet.TLRPC$TL_channel;
import org.telegram.tgnet.TLRPC$TL_channelForbidden;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_convertToGigagroup;
import org.telegram.tgnet.TLRPC$TL_channels_createChannel;
import org.telegram.tgnet.TLRPC$TL_channels_deleteChannel;
import org.telegram.tgnet.TLRPC$TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC$TL_channels_deleteParticipantHistory;
import org.telegram.tgnet.TLRPC$TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC$TL_channels_editBanned;
import org.telegram.tgnet.TLRPC$TL_channels_editPhoto;
import org.telegram.tgnet.TLRPC$TL_channels_editTitle;
import org.telegram.tgnet.TLRPC$TL_channels_getChannels;
import org.telegram.tgnet.TLRPC$TL_channels_getForumTopicsByID;
import org.telegram.tgnet.TLRPC$TL_channels_getMessages;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getSendAs;
import org.telegram.tgnet.TLRPC$TL_channels_getSponsoredMessages;
import org.telegram.tgnet.TLRPC$TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC$TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC$TL_channels_leaveChannel;
import org.telegram.tgnet.TLRPC$TL_channels_readHistory;
import org.telegram.tgnet.TLRPC$TL_channels_readMessageContents;
import org.telegram.tgnet.TLRPC$TL_channels_sendAsPeers;
import org.telegram.tgnet.TLRPC$TL_channels_toggleForum;
import org.telegram.tgnet.TLRPC$TL_channels_toggleJoinRequest;
import org.telegram.tgnet.TLRPC$TL_channels_toggleJoinToSend;
import org.telegram.tgnet.TLRPC$TL_channels_togglePreHistoryHidden;
import org.telegram.tgnet.TLRPC$TL_channels_toggleSignatures;
import org.telegram.tgnet.TLRPC$TL_channels_toggleSlowMode;
import org.telegram.tgnet.TLRPC$TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatFull;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_chatOnlines;
import org.telegram.tgnet.TLRPC$TL_chatReactionsAll;
import org.telegram.tgnet.TLRPC$TL_chatReactionsNone;
import org.telegram.tgnet.TLRPC$TL_chatReactionsSome;
import org.telegram.tgnet.TLRPC$TL_config;
import org.telegram.tgnet.TLRPC$TL_contacts_block;
import org.telegram.tgnet.TLRPC$TL_contacts_getBlocked;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_contacts_unblock;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_dialogFilterSuggested;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;
import org.telegram.tgnet.TLRPC$TL_dialogPeer;
import org.telegram.tgnet.TLRPC$TL_documentEmpty;
import org.telegram.tgnet.TLRPC$TL_draftMessage;
import org.telegram.tgnet.TLRPC$TL_emojiStatusUntil;
import org.telegram.tgnet.TLRPC$TL_encryptedChat;
import org.telegram.tgnet.TLRPC$TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_folder;
import org.telegram.tgnet.TLRPC$TL_folderPeer;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_groupCallDiscarded;
import org.telegram.tgnet.TLRPC$TL_help_dismissSuggestion;
import org.telegram.tgnet.TLRPC$TL_help_getAppChangelog;
import org.telegram.tgnet.TLRPC$TL_help_getRecentMeUrls;
import org.telegram.tgnet.TLRPC$TL_help_hidePromoData;
import org.telegram.tgnet.TLRPC$TL_help_promoData;
import org.telegram.tgnet.TLRPC$TL_help_recentMeUrls;
import org.telegram.tgnet.TLRPC$TL_help_termsOfServiceUpdate;
import org.telegram.tgnet.TLRPC$TL_help_termsOfServiceUpdateEmpty;
import org.telegram.tgnet.TLRPC$TL_inputChannel;
import org.telegram.tgnet.TLRPC$TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC$TL_inputChatPhoto;
import org.telegram.tgnet.TLRPC$TL_inputChatUploadedPhoto;
import org.telegram.tgnet.TLRPC$TL_inputDialogPeer;
import org.telegram.tgnet.TLRPC$TL_inputDocument;
import org.telegram.tgnet.TLRPC$TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC$TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC$TL_inputGroupCall;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC$TL_inputNotifyBroadcasts;
import org.telegram.tgnet.TLRPC$TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC$TL_inputNotifyUsers;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_inputPeerSelf;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputPhotoEmpty;
import org.telegram.tgnet.TLRPC$TL_inputTheme;
import org.telegram.tgnet.TLRPC$TL_inputThemeSettings;
import org.telegram.tgnet.TLRPC$TL_inputUser;
import org.telegram.tgnet.TLRPC$TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC$TL_inputUserSelf;
import org.telegram.tgnet.TLRPC$TL_inputWallPaperNoFile;
import org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug;
import org.telegram.tgnet.TLRPC$TL_jsonArray;
import org.telegram.tgnet.TLRPC$TL_jsonBool;
import org.telegram.tgnet.TLRPC$TL_jsonNumber;
import org.telegram.tgnet.TLRPC$TL_jsonObject;
import org.telegram.tgnet.TLRPC$TL_jsonObjectValue;
import org.telegram.tgnet.TLRPC$TL_jsonString;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_messageViews;
import org.telegram.tgnet.TLRPC$TL_messages_addChatUser;
import org.telegram.tgnet.TLRPC$TL_messages_affectedHistory;
import org.telegram.tgnet.TLRPC$TL_messages_affectedMessages;
import org.telegram.tgnet.TLRPC$TL_messages_chatFull;
import org.telegram.tgnet.TLRPC$TL_messages_chats;
import org.telegram.tgnet.TLRPC$TL_messages_createChat;
import org.telegram.tgnet.TLRPC$TL_messages_deleteChat;
import org.telegram.tgnet.TLRPC$TL_messages_deleteChatUser;
import org.telegram.tgnet.TLRPC$TL_messages_deleteHistory;
import org.telegram.tgnet.TLRPC$TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC$TL_messages_deleteScheduledMessages;
import org.telegram.tgnet.TLRPC$TL_messages_dialogs;
import org.telegram.tgnet.TLRPC$TL_messages_editChatAbout;
import org.telegram.tgnet.TLRPC$TL_messages_editChatAdmin;
import org.telegram.tgnet.TLRPC$TL_messages_editChatDefaultBannedRights;
import org.telegram.tgnet.TLRPC$TL_messages_editChatPhoto;
import org.telegram.tgnet.TLRPC$TL_messages_editChatTitle;
import org.telegram.tgnet.TLRPC$TL_messages_forumTopics;
import org.telegram.tgnet.TLRPC$TL_messages_getChats;
import org.telegram.tgnet.TLRPC$TL_messages_getDialogs;
import org.telegram.tgnet.TLRPC$TL_messages_getExtendedMedia;
import org.telegram.tgnet.TLRPC$TL_messages_getHistory;
import org.telegram.tgnet.TLRPC$TL_messages_getMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getMessagesReactions;
import org.telegram.tgnet.TLRPC$TL_messages_getMessagesViews;
import org.telegram.tgnet.TLRPC$TL_messages_getOnlines;
import org.telegram.tgnet.TLRPC$TL_messages_getPeerDialogs;
import org.telegram.tgnet.TLRPC$TL_messages_getPeerSettings;
import org.telegram.tgnet.TLRPC$TL_messages_getPinnedDialogs;
import org.telegram.tgnet.TLRPC$TL_messages_getReplies;
import org.telegram.tgnet.TLRPC$TL_messages_getScheduledHistory;
import org.telegram.tgnet.TLRPC$TL_messages_getUnreadMentions;
import org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC$TL_messages_hidePeerSettingsBar;
import org.telegram.tgnet.TLRPC$TL_messages_markDialogUnread;
import org.telegram.tgnet.TLRPC$TL_messages_messageViews;
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$TL_messages_messagesNotModified;
import org.telegram.tgnet.TLRPC$TL_messages_migrateChat;
import org.telegram.tgnet.TLRPC$TL_messages_peerDialogs;
import org.telegram.tgnet.TLRPC$TL_messages_peerSettings;
import org.telegram.tgnet.TLRPC$TL_messages_readDiscussion;
import org.telegram.tgnet.TLRPC$TL_messages_readEncryptedHistory;
import org.telegram.tgnet.TLRPC$TL_messages_readHistory;
import org.telegram.tgnet.TLRPC$TL_messages_readMentions;
import org.telegram.tgnet.TLRPC$TL_messages_readMessageContents;
import org.telegram.tgnet.TLRPC$TL_messages_readReactions;
import org.telegram.tgnet.TLRPC$TL_messages_reorderPinnedDialogs;
import org.telegram.tgnet.TLRPC$TL_messages_reportEncryptedSpam;
import org.telegram.tgnet.TLRPC$TL_messages_reportSpam;
import org.telegram.tgnet.TLRPC$TL_messages_saveDefaultSendAs;
import org.telegram.tgnet.TLRPC$TL_messages_saveGif;
import org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_messages_setChatAvailableReactions;
import org.telegram.tgnet.TLRPC$TL_messages_setEncryptedTyping;
import org.telegram.tgnet.TLRPC$TL_messages_setHistoryTTL;
import org.telegram.tgnet.TLRPC$TL_messages_setTyping;
import org.telegram.tgnet.TLRPC$TL_messages_sponsoredMessages;
import org.telegram.tgnet.TLRPC$TL_messages_startBot;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_toggleDialogPin;
import org.telegram.tgnet.TLRPC$TL_messages_toggleNoForwards;
import org.telegram.tgnet.TLRPC$TL_messages_unpinAllMessages;
import org.telegram.tgnet.TLRPC$TL_messages_updatePinnedMessage;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_peerSettings;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_groupCall;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_photos_deletePhotos;
import org.telegram.tgnet.TLRPC$TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC$TL_photos_photo;
import org.telegram.tgnet.TLRPC$TL_photos_photos;
import org.telegram.tgnet.TLRPC$TL_photos_updateProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_reactionEmoji;
import org.telegram.tgnet.TLRPC$TL_restrictionReason;
import org.telegram.tgnet.TLRPC$TL_sendMessageCancelAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageChooseContactAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageChooseStickerAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageEmojiInteraction;
import org.telegram.tgnet.TLRPC$TL_sendMessageEmojiInteractionSeen;
import org.telegram.tgnet.TLRPC$TL_sendMessageGamePlayAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageGeoLocationAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageRecordAudioAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageRecordRoundAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageRecordVideoAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageUploadAudioAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageUploadDocumentAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageUploadPhotoAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageUploadRoundAction;
import org.telegram.tgnet.TLRPC$TL_sendMessageUploadVideoAction;
import org.telegram.tgnet.TLRPC$TL_sponsoredMessage;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_updateChannel;
import org.telegram.tgnet.TLRPC$TL_updateChannelAvailableMessages;
import org.telegram.tgnet.TLRPC$TL_updateChannelMessageForwards;
import org.telegram.tgnet.TLRPC$TL_updateChannelMessageViews;
import org.telegram.tgnet.TLRPC$TL_updateChannelPinnedTopic;
import org.telegram.tgnet.TLRPC$TL_updateChannelReadMessagesContents;
import org.telegram.tgnet.TLRPC$TL_updateChannelTooLong;
import org.telegram.tgnet.TLRPC$TL_updateChannelUserTyping;
import org.telegram.tgnet.TLRPC$TL_updateChannelWebPage;
import org.telegram.tgnet.TLRPC$TL_updateChatUserTyping;
import org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages;
import org.telegram.tgnet.TLRPC$TL_updateDeleteMessages;
import org.telegram.tgnet.TLRPC$TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC$TL_updateEditMessage;
import org.telegram.tgnet.TLRPC$TL_updateFolderPeers;
import org.telegram.tgnet.TLRPC$TL_updateLangPack;
import org.telegram.tgnet.TLRPC$TL_updateMessageID;
import org.telegram.tgnet.TLRPC$TL_updateMessagePoll;
import org.telegram.tgnet.TLRPC$TL_updateMessageReactions;
import org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC$TL_updateNewEncryptedMessage;
import org.telegram.tgnet.TLRPC$TL_updateNewMessage;
import org.telegram.tgnet.TLRPC$TL_updatePeerBlocked;
import org.telegram.tgnet.TLRPC$TL_updatePinnedChannelMessages;
import org.telegram.tgnet.TLRPC$TL_updatePinnedMessages;
import org.telegram.tgnet.TLRPC$TL_updateReadChannelDiscussionInbox;
import org.telegram.tgnet.TLRPC$TL_updateReadChannelDiscussionOutbox;
import org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox;
import org.telegram.tgnet.TLRPC$TL_updateReadChannelOutbox;
import org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox;
import org.telegram.tgnet.TLRPC$TL_updateReadHistoryOutbox;
import org.telegram.tgnet.TLRPC$TL_updateReadMessagesContents;
import org.telegram.tgnet.TLRPC$TL_updateServiceNotification;
import org.telegram.tgnet.TLRPC$TL_updateUserTyping;
import org.telegram.tgnet.TLRPC$TL_updateWebPage;
import org.telegram.tgnet.TLRPC$TL_updatesCombined;
import org.telegram.tgnet.TLRPC$TL_updates_differenceSlice;
import org.telegram.tgnet.TLRPC$TL_updates_differenceTooLong;
import org.telegram.tgnet.TLRPC$TL_updates_getDifference;
import org.telegram.tgnet.TLRPC$TL_updates_state;
import org.telegram.tgnet.TLRPC$TL_userForeign_old2;
import org.telegram.tgnet.TLRPC$TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC$TL_username;
import org.telegram.tgnet.TLRPC$TL_users_getFullUser;
import org.telegram.tgnet.TLRPC$TL_users_getUsers;
import org.telegram.tgnet.TLRPC$TL_users_userFull;
import org.telegram.tgnet.TLRPC$TL_wallPaperSettings;
import org.telegram.tgnet.TLRPC$TL_webPage;
import org.telegram.tgnet.TLRPC$TL_webPageEmpty;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.tgnet.TLRPC$WallPaperSettings;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$contacts_Blocked;
import org.telegram.tgnet.TLRPC$messages_Dialogs;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.tgnet.TLRPC$messages_SponsoredMessages;
import org.telegram.tgnet.TLRPC$photos_Photos;
import org.telegram.tgnet.TLRPC$updates_ChannelDifference;
import org.telegram.tgnet.TLRPC$updates_Difference;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.TopicsFragment;
/* loaded from: classes.dex */
public class MessagesController extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    public static int DIALOG_FILTER_FLAG_BOTS = 16;
    public static int DIALOG_FILTER_FLAG_CHANNELS = 8;
    public static int DIALOG_FILTER_FLAG_CONTACTS = 1;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED = 128;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_MUTED = 32;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_READ = 64;
    public static int DIALOG_FILTER_FLAG_GROUPS = 4;
    public static int DIALOG_FILTER_FLAG_NON_CONTACTS = 2;
    public static int DIALOG_FILTER_FLAG_ONLY_ARCHIVED = 256;
    public static int PROMO_TYPE_OTHER = 2;
    public static int PROMO_TYPE_PROXY = 0;
    public static int PROMO_TYPE_PSA = 1;
    public static int UPDATE_MASK_AVATAR = 2;
    public static int UPDATE_MASK_CHAT = 8192;
    public static int UPDATE_MASK_CHAT_AVATAR = 8;
    public static int UPDATE_MASK_CHAT_MEMBERS = 32;
    public static int UPDATE_MASK_CHAT_NAME = 16;
    public static int UPDATE_MASK_CHECK = 65536;
    public static int UPDATE_MASK_EMOJI_INTERACTIONS = 262144;
    public static int UPDATE_MASK_EMOJI_STATUS = 524288;
    public static int UPDATE_MASK_MESSAGE_TEXT = 32768;
    public static int UPDATE_MASK_NAME = 1;
    public static int UPDATE_MASK_NEW_MESSAGE = 2048;
    public static int UPDATE_MASK_PHONE = 1024;
    public static int UPDATE_MASK_REACTIONS_READ = 1048576;
    public static int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
    public static int UPDATE_MASK_REORDER = 131072;
    public static int UPDATE_MASK_SELECT_DIALOG = 512;
    public static int UPDATE_MASK_SEND_STATE = 4096;
    public static int UPDATE_MASK_STATUS = 4;
    public static int UPDATE_MASK_USER_PHONE = 128;
    public static int UPDATE_MASK_USER_PRINT = 64;
    private static volatile long lastPasswordCheckTime;
    private static volatile long lastThemeCheckTime;
    private int DIALOGS_LOAD_TYPE_CACHE;
    private int DIALOGS_LOAD_TYPE_CHANNEL;
    private int DIALOGS_LOAD_TYPE_UNKNOWN;
    public int aboutLengthLimitDefault;
    public int aboutLengthLimitPremium;
    private HashMap<Long, TLRPC$Chat> activeVoiceChatsMap;
    protected ArrayList<TLRPC$Dialog> allDialogs;
    public float animatedEmojisZoom;
    public Set<String> authDomains;
    public boolean autoarchiveAvailable;
    public Set<String> autologinDomains;
    public String autologinToken;
    public int availableMapProviders;
    public boolean backgroundConnection;
    public LongSparseIntArray blockePeers;
    public boolean blockedCountry;
    public boolean blockedEndReached;
    public int callConnectTimeout;
    public int callPacketTimeout;
    public int callReceiveTimeout;
    public int callRingTimeout;
    public boolean canRevokePmInbox;
    public int captionLengthLimitDefault;
    public int captionLengthLimitPremium;
    private LongSparseArray<LongSparseArray<TLRPC$ChannelParticipant>> channelAdmins;
    private LongSparseArray<ArrayList<Integer>> channelViewsToSend;
    public int channelsLimitDefault;
    public int channelsLimitPremium;
    private LongSparseIntArray channelsPts;
    public int chatReadMarkExpirePeriod;
    public int chatReadMarkSizeThreshold;
    private ConcurrentHashMap<Long, TLRPC$Chat> chats;
    private LongSparseArray<Boolean> checkingLastMessagesDialogs;
    private boolean checkingPromoInfo;
    private int checkingPromoInfoRequestId;
    private boolean checkingTosUpdate;
    private LongSparseArray<TLRPC$Dialog> clearingHistoryDialogs;
    private ArrayList<Long> createdDialogIds;
    private ArrayList<Long> createdDialogMainThreadIds;
    private ArrayList<Long> createdScheduledDialogIds;
    private Runnable currentDeleteTaskRunnable;
    private LongSparseArray<ArrayList<Integer>> currentDeletingTaskMediaMids;
    private LongSparseArray<ArrayList<Integer>> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    public String dcDomainName;
    public LongSparseIntArray deletedHistory;
    private LongSparseArray<TLRPC$Dialog> deletingDialogs;
    private Comparator<TLRPC$Dialog> dialogComparator;
    private Comparator<TLRPC$Dialog> dialogDateComparator;
    public ArrayList<DialogFilter> dialogFilters;
    public SparseArray<DialogFilter> dialogFiltersById;
    public int dialogFiltersChatsLimitDefault;
    public int dialogFiltersChatsLimitPremium;
    public int dialogFiltersLimitDefault;
    public int dialogFiltersLimitPremium;
    public boolean dialogFiltersLoaded;
    public int dialogFiltersPinnedLimitDefault;
    public int dialogFiltersPinnedLimitPremium;
    public LongSparseArray<ArrayList<MessageObject>> dialogMessage;
    public SparseArray<MessageObject> dialogMessagesByIds;
    public LongSparseArray<MessageObject> dialogMessagesByRandomIds;
    public SparseArray<ArrayList<TLRPC$Dialog>> dialogsByFolder;
    public ArrayList<TLRPC$Dialog> dialogsCanAddUsers;
    public ArrayList<TLRPC$Dialog> dialogsChannelsOnly;
    private SparseBooleanArray dialogsEndReached;
    public ArrayList<TLRPC$Dialog> dialogsForBlock;
    public ArrayList<TLRPC$Dialog> dialogsForward;
    public ArrayList<TLRPC$Dialog> dialogsGroupsOnly;
    private boolean dialogsInTransaction;
    public boolean dialogsLoaded;
    private int dialogsLoadedTillDate;
    public ArrayList<TLRPC$Dialog> dialogsMyChannels;
    public ArrayList<TLRPC$Dialog> dialogsMyGroups;
    public ArrayList<TLRPC$Dialog> dialogsServerOnly;
    public ArrayList<TLRPC$Dialog> dialogsUsersOnly;
    public LongSparseArray<TLRPC$Dialog> dialogs_dict;
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max;
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max;
    public HashSet<String> diceEmojies;
    public HashMap<String, DiceFrameSuccess> diceSuccess;
    public List<String> directPaymentsCurrency;
    public HashMap<Long, ArrayList<TLRPC$TL_sendMessageEmojiInteraction>> emojiInteractions;
    private SharedPreferences emojiPreferences;
    public HashMap<String, EmojiSound> emojiSounds;
    private LongSparseArray<Integer> emojiStatusUntilValues;
    public boolean enableJoined;
    private ConcurrentHashMap<Integer, TLRPC$EncryptedChat> encryptedChats;
    public Set<String> exportGroupUri;
    public Set<String> exportPrivateUri;
    public Set<String> exportUri;
    private LongSparseArray<TLRPC$TL_chatInviteExported> exportedChats;
    public ArrayList<FaqSearchResult> faqSearchArray;
    public TLRPC$WebPage faqWebPage;
    public boolean filtersEnabled;
    public boolean firstGettingTask;
    public int forumUpgradeParticipantsMin;
    private LongSparseArray<TLRPC$ChatFull> fullChats;
    private LongSparseArray<TLRPC$UserFull> fullUsers;
    private boolean getDifferenceFirstSync;
    public boolean getfileExperimentalParams;
    private boolean gettingAppChangelog;
    private LongSparseArray<Boolean> gettingChatInviters;
    public boolean gettingDifference;
    private LongSparseArray<Boolean> gettingDifferenceChannels;
    private boolean gettingNewDeleteTask;
    private LongSparseArray<Boolean> gettingUnknownChannels;
    private LongSparseArray<Boolean> gettingUnknownDialogs;
    public String gifSearchBot;
    public ArrayList<String> gifSearchEmojies;
    public int groupCallVideoMaxParticipants;
    private LongSparseArray<ChatObject.Call> groupCalls;
    private LongSparseArray<ChatObject.Call> groupCallsByChatId;
    public ArrayList<TLRPC$RecentMeUrl> hintDialogs;
    public volatile boolean ignoreSetOnline;
    public String imageSearchBot;
    private String installReferer;
    private boolean isLeftPromoChannel;
    private ArrayList<Long> joiningToChannels;
    public boolean keepAliveService;
    private int lastCheckPromoId;
    private int lastPrintingStringCount;
    private long lastPushRegisterSendTime;
    private LongSparseArray<Long> lastScheduledServerQueryTime;
    private LongSparseArray<Long> lastServerQueryTime;
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    public String linkPrefix;
    private ArrayList<Long> loadedFullChats;
    private ArrayList<Long> loadedFullParticipants;
    private ArrayList<Long> loadedFullUsers;
    private boolean loadingAppConfig;
    public boolean loadingBlockedPeers;
    private LongSparseIntArray loadingChannelAdmins;
    private SparseBooleanArray loadingDialogs;
    private ArrayList<Long> loadingFullChats;
    private ArrayList<Long> loadingFullParticipants;
    private ArrayList<Long> loadingFullUsers;
    private ArrayList<Long> loadingGroupCalls;
    private int loadingNotificationSettings;
    private boolean loadingNotificationSignUpSettings;
    private LongSparseArray<Boolean> loadingPeerSettings;
    private SparseIntArray loadingPinnedDialogs;
    private boolean loadingRemoteFilters;
    private boolean loadingSuggestedFilters;
    private boolean loadingUnreadDialogs;
    private SharedPreferences mainPreferences;
    public String mapKey;
    public int mapProvider;
    public int maxBroadcastCount;
    public int maxCaptionLength;
    public int maxEditTime;
    public int maxFaveStickersCount;
    public int maxFolderPinnedDialogsCount;
    public int maxGroupCount;
    public int maxMegagroupCount;
    public int maxMessageLength;
    public int maxPinnedDialogsCount;
    public int maxRecentGifsCount;
    public int maxRecentStickersCount;
    private SparseIntArray migratedChats;
    private boolean migratingDialogs;
    public int minGroupConvertSize;
    private LongSparseArray<ArrayList<Integer>> needShortPollChannels;
    private LongSparseArray<ArrayList<Integer>> needShortPollOnlines;
    public NewMessageCallback newMessageCallback;
    private SparseIntArray nextDialogsCacheOffset;
    private int nextPromoInfoCheckTime;
    private int nextTosCheckTime;
    private SharedPreferences notificationsPreferences;
    private ConcurrentHashMap<String, TLObject> objectsByUsernames;
    private boolean offlineSent;
    public ConcurrentHashMap<Long, Integer> onlinePrivacy;
    private Runnable passwordCheckRunnable;
    public Set<String> pendingSuggestions;
    private LongSparseArray<SparseArray<MessageObject>> pollsToCheck;
    private int pollsToCheckSize;
    public boolean preloadFeaturedStickers;
    public String premiumBotUsername;
    public SparseIntArray premiumFeaturesTypesToPosition;
    public String premiumInvoiceSlug;
    public boolean premiumLocked;
    public LongSparseArray<SparseArray<CharSequence>> printingStrings;
    public LongSparseArray<SparseArray<Integer>> printingStringsTypes;
    public ConcurrentHashMap<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>> printingUsers;
    private TLRPC$Dialog promoDialog;
    private long promoDialogId;
    public int promoDialogType;
    public String promoPsaMessage;
    public String promoPsaType;
    private String proxyDialogAddress;
    public int publicLinksLimitDefault;
    public int publicLinksLimitPremium;
    public boolean qrLoginCamera;
    public int ratingDecay;
    public int reactionsInChatMax;
    public int reactionsUserMaxDefault;
    public int reactionsUserMaxPremium;
    private ArrayList<ReadTask> readTasks;
    private LongSparseArray<ReadTask> readTasksMap;
    private Runnable recentEmojiStatusUpdateRunnable;
    private long recentEmojiStatusUpdateRunnableTime;
    private long recentEmojiStatusUpdateRunnableTimeout;
    public boolean registeringForPush;
    private LongSparseArray<ArrayList<Integer>> reloadingMessages;
    private HashMap<String, ArrayList<MessageObject>> reloadingScheduledWebpages;
    private LongSparseArray<ArrayList<MessageObject>> reloadingScheduledWebpagesPending;
    private HashMap<String, ArrayList<MessageObject>> reloadingWebpages;
    private LongSparseArray<ArrayList<MessageObject>> reloadingWebpagesPending;
    public boolean remoteConfigLoaded;
    private ArrayList<ReadTask> repliesReadTasks;
    private TLRPC$messages_Dialogs resetDialogsAll;
    private TLRPC$TL_messages_peerDialogs resetDialogsPinned;
    private boolean resetingDialogs;
    public int revokeTimeLimit;
    public int revokeTimePmLimit;
    public int ringtoneDurationMax;
    public int ringtoneSizeMax;
    public int roundAudioBitrate;
    public int roundVideoBitrate;
    public int roundVideoSize;
    public boolean saveGifsWithStickers;
    public int savedGifsLimitDefault;
    public int savedGifsLimitPremium;
    public int secretWebpagePreview;
    public DialogFilter[] selectedDialogFilter;
    private LongSparseArray<SendAsPeersInfo> sendAsPeers;
    public LongSparseArray<SparseArray<Boolean>>[] sendingTypings;
    private SparseBooleanArray serverDialogsEndReached;
    private LongSparseIntArray shortPollChannels;
    private LongSparseIntArray shortPollOnlines;
    public boolean showFiltersTooltip;
    private DialogFilter sortingDialogFilter;
    private LongSparseArray<SponsoredMessagesInfo> sponsoredMessages;
    private int statusRequest;
    private int statusSettingState;
    public int stickersFavedLimitDefault;
    public int stickersFavedLimitPremium;
    public boolean suggestContacts;
    public boolean suggestStickersApiOnly;
    public ArrayList<TLRPC$TL_dialogFilterSuggested> suggestedFilters;
    public String suggestedLangCode;
    private Runnable themeCheckRunnable;
    private HashMap<String, ReadTask> threadsReadTasksMap;
    private TopicsController topicsController;
    public int totalBlockedCount;
    public int transcribeButtonPressed;
    public int unreadUnmutedDialogs;
    public int updateCheckDelay;
    private Comparator<TLRPC$Update> updatesComparator;
    private LongSparseArray<ArrayList<TLRPC$Updates>> updatesQueueChannels;
    private ArrayList<TLRPC$Updates> updatesQueuePts;
    private ArrayList<TLRPC$Updates> updatesQueueQts;
    private ArrayList<TLRPC$Updates> updatesQueueSeq;
    private LongSparseLongArray updatesStartWaitTimeChannels;
    private long updatesStartWaitTimePts;
    private long updatesStartWaitTimeQts;
    private long updatesStartWaitTimeSeq;
    public boolean updatingState;
    public int uploadMaxFileParts;
    public int uploadMaxFilePartsPremium;
    private String uploadingAvatar;
    private HashMap<String, Object> uploadingThemes;
    private String uploadingWallpaper;
    private Theme.OverrideWallpaperInfo uploadingWallpaperInfo;
    private ConcurrentHashMap<Long, TLRPC$User> users;
    public String venueSearchBot;
    private ArrayList<Long> visibleDialogMainThreadIds;
    private ArrayList<Long> visibleScheduledDialogMainThreadIds;
    public int webFileDatacenterId;
    public String youtubePipType;
    public static int UPDATE_MASK_ALL = (((((((((2 | 4) | 1) | 8) | 16) | 32) | 64) | 128) | 256) | 1024) | 1048576;
    public static int DIALOG_FILTER_FLAG_ALL_CHATS = (((1 | 2) | 4) | 8) | 16;
    private static volatile MessagesController[] Instance = new MessagesController[4];
    private static final Object[] lockObjects = new Object[4];

    /* loaded from: classes.dex */
    public interface ErrorDelegate {
        boolean run(TLRPC$TL_error tLRPC$TL_error);
    }

    /* loaded from: classes.dex */
    public interface IsInChatCheckedCallback {
        void run(boolean z, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str);
    }

    /* loaded from: classes.dex */
    public interface MessagesLoadedCallback {
        void onError();

        void onMessagesLoaded(boolean z);
    }

    /* loaded from: classes.dex */
    public interface NewMessageCallback {
        boolean onMessageReceived(TLRPC$Message tLRPC$Message);
    }

    /* loaded from: classes.dex */
    public static class PrintingUser {
        public TLRPC$SendMessageAction action;
        public long lastTime;
        public long userId;
    }

    public static /* synthetic */ void lambda$blockPeer$71(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$completeReadTask$200(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$completeReadTask$202(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$deleteParticipantFromChat$251(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$deleteUserPhoto$95(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$hidePeerSettingsBar$56(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$hidePromoDialog$110(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$installTheme$98(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$installTheme$99(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$markMentionMessageAsRead$196(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$markMentionsAsRead$204(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$markMessageContentAsRead$194(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$markReactionsAsRead$344(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$processUpdates$311(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$removeSuggestion$24(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$reportSpam$57(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$reportSpam$58(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$reportSpam$59(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$saveTheme$97(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$unblockPeer$90(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$unregistedPush$257(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public void markSponsoredAsRead(long j, MessageObject messageObject) {
    }

    public /* synthetic */ void lambda$new$0() {
        getUserConfig().checkSavedPassword();
    }

    public void getNextReactionMention(final long j, final int i, final int i2, final Consumer<Integer> consumer) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda50
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getNextReactionMention$5(i, j, consumer, i2);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x0079  */
    /* JADX WARN: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getNextReactionMention$5(int r10, long r11, final androidx.core.util.Consumer r13, int r14) {
        /*
            r9 = this;
            r0 = 1
            r1 = 0
            if (r10 == 0) goto L2a
            org.telegram.messenger.MessagesStorage r2 = r9.getMessagesStorage()     // Catch: org.telegram.SQLite.SQLiteException -> L72
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.util.Locale r3 = java.util.Locale.US     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.lang.String r4 = "SELECT message_id FROM reaction_mentions_topics WHERE state = 1 AND dialog_id = %d AND topic_id = %d LIMIT 1"
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.lang.Long r6 = java.lang.Long.valueOf(r11)     // Catch: org.telegram.SQLite.SQLiteException -> L72
            r5[r1] = r6     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.lang.Integer r6 = java.lang.Integer.valueOf(r10)     // Catch: org.telegram.SQLite.SQLiteException -> L72
            r5[r0] = r6     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.lang.String r3 = java.lang.String.format(r3, r4, r5)     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.lang.Object[] r4 = new java.lang.Object[r1]     // Catch: org.telegram.SQLite.SQLiteException -> L72
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch: org.telegram.SQLite.SQLiteException -> L72
            goto L48
        L2a:
            org.telegram.messenger.MessagesStorage r2 = r9.getMessagesStorage()     // Catch: org.telegram.SQLite.SQLiteException -> L72
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.util.Locale r3 = java.util.Locale.US     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.lang.String r4 = "SELECT message_id FROM reaction_mentions WHERE state = 1 AND dialog_id = %d LIMIT 1"
            java.lang.Object[] r5 = new java.lang.Object[r0]     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.lang.Long r6 = java.lang.Long.valueOf(r11)     // Catch: org.telegram.SQLite.SQLiteException -> L72
            r5[r1] = r6     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.lang.String r3 = java.lang.String.format(r3, r4, r5)     // Catch: org.telegram.SQLite.SQLiteException -> L72
            java.lang.Object[] r4 = new java.lang.Object[r1]     // Catch: org.telegram.SQLite.SQLiteException -> L72
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch: org.telegram.SQLite.SQLiteException -> L72
        L48:
            boolean r3 = r2.next()     // Catch: org.telegram.SQLite.SQLiteException -> L72
            if (r3 == 0) goto L55
            int r3 = r2.intValue(r1)     // Catch: org.telegram.SQLite.SQLiteException -> L72
            r7 = r3
            r8 = 0
            goto L57
        L55:
            r7 = 0
            r8 = 1
        L57:
            r2.dispose()     // Catch: org.telegram.SQLite.SQLiteException -> L70
            if (r7 == 0) goto L77
            org.telegram.messenger.MessagesStorage r1 = r9.getMessagesStorage()     // Catch: org.telegram.SQLite.SQLiteException -> L70
            r6 = 0
            r2 = r11
            r4 = r10
            r5 = r7
            r1.markMessageReactionsAsRead(r2, r4, r5, r6)     // Catch: org.telegram.SQLite.SQLiteException -> L70
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda6 r10 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda6     // Catch: org.telegram.SQLite.SQLiteException -> L70
            r10.<init>()     // Catch: org.telegram.SQLite.SQLiteException -> L70
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r10)     // Catch: org.telegram.SQLite.SQLiteException -> L70
            goto L77
        L70:
            r10 = move-exception
            goto L74
        L72:
            r10 = move-exception
            r8 = 1
        L74:
            r10.printStackTrace()
        L77:
            if (r8 == 0) goto L99
            org.telegram.tgnet.TLRPC$TL_messages_getUnreadReactions r10 = new org.telegram.tgnet.TLRPC$TL_messages_getUnreadReactions
            r10.<init>()
            org.telegram.messenger.MessagesController r1 = r9.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r11 = r1.getInputPeer(r11)
            r10.peer = r11
            r10.limit = r0
            int r14 = r14 - r0
            r10.add_offset = r14
            org.telegram.tgnet.ConnectionsManager r11 = r9.getConnectionsManager()
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda236 r12 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda236
            r12.<init>()
            r11.sendRequest(r10, r12)
        L99:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$getNextReactionMention$5(int, long, androidx.core.util.Consumer, int):void");
    }

    public static /* synthetic */ void lambda$getNextReactionMention$1(Consumer consumer, int i) {
        consumer.accept(Integer.valueOf(i));
    }

    public static /* synthetic */ void lambda$getNextReactionMention$4(final Consumer consumer, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda223
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.lambda$getNextReactionMention$3(TLObject.this, tLRPC$TL_error, consumer);
            }
        });
    }

    public static /* synthetic */ void lambda$getNextReactionMention$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, final Consumer consumer) {
        ArrayList<TLRPC$Message> arrayList;
        TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
        final int i = 0;
        if (tLRPC$TL_error != null && tLRPC$messages_Messages != null && (arrayList = tLRPC$messages_Messages.messages) != null && !arrayList.isEmpty()) {
            i = tLRPC$messages_Messages.messages.get(0).id;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.lambda$getNextReactionMention$2(Consumer.this, i);
            }
        });
    }

    public static /* synthetic */ void lambda$getNextReactionMention$2(Consumer consumer, int i) {
        consumer.accept(Integer.valueOf(i));
    }

    public void updatePremium(boolean z) {
        if (this.dialogFilters.isEmpty()) {
            return;
        }
        if (!z) {
            if (!this.dialogFilters.get(0).isDefault()) {
                int i = 1;
                while (true) {
                    if (i >= this.dialogFilters.size()) {
                        break;
                    } else if (this.dialogFilters.get(i).isDefault()) {
                        this.dialogFilters.add(0, this.dialogFilters.remove(i));
                        break;
                    } else {
                        i++;
                    }
                }
            }
            lockFiltersInternal();
        } else {
            for (int i2 = 0; i2 < this.dialogFilters.size(); i2++) {
                this.dialogFilters.get(i2).locked = false;
            }
        }
        getMessagesStorage().saveDialogFiltersOrder();
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
    }

    public void lockFiltersInternal() {
        boolean z;
        if (getUserConfig().isPremium() || this.dialogFilters.size() - 1 <= this.dialogFiltersLimitDefault) {
            z = false;
        } else {
            int size = (this.dialogFilters.size() - 1) - this.dialogFiltersLimitDefault;
            ArrayList arrayList = new ArrayList(this.dialogFilters);
            Collections.reverse(arrayList);
            z = false;
            for (int i = 0; i < arrayList.size(); i++) {
                if (i < size) {
                    if (!((DialogFilter) arrayList.get(i)).locked) {
                        z = true;
                    }
                    ((DialogFilter) arrayList.get(i)).locked = true;
                } else {
                    if (((DialogFilter) arrayList.get(i)).locked) {
                        z = true;
                    }
                    ((DialogFilter) arrayList.get(i)).locked = false;
                }
            }
        }
        if (z) {
            getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        }
    }

    public int getCaptionMaxLengthLimit() {
        return getUserConfig().isPremium() ? this.captionLengthLimitPremium : this.captionLengthLimitDefault;
    }

    public int getAboutLimit() {
        return getUserConfig().isPremium() ? this.aboutLengthLimitPremium : this.aboutLengthLimitDefault;
    }

    public int getMaxUserReactionsCount() {
        return getUserConfig().isPremium() ? this.reactionsUserMaxPremium : this.reactionsUserMaxDefault;
    }

    public int getChatReactionsCount() {
        if (getUserConfig().isPremium()) {
            return this.reactionsInChatMax;
        }
        return 1;
    }

    public boolean isPremiumUser(TLRPC$User tLRPC$User) {
        return !this.premiumLocked && tLRPC$User.premium;
    }

    public boolean didPressTranscribeButtonEnough() {
        return this.transcribeButtonPressed >= 2;
    }

    public void pressTranscribeButton() {
        int i = this.transcribeButtonPressed;
        if (i < 2) {
            this.transcribeButtonPressed = i + 1;
            SharedPreferences sharedPreferences = this.mainPreferences;
            if (sharedPreferences == null) {
                return;
            }
            sharedPreferences.edit().putInt("transcribeButtonPressed", this.transcribeButtonPressed).apply();
        }
    }

    public ArrayList<TLRPC$TL_messages_stickerSet> filterPremiumStickers(ArrayList<TLRPC$TL_messages_stickerSet> arrayList) {
        if (!this.premiumLocked) {
            return arrayList;
        }
        int i = 0;
        while (i < arrayList.size()) {
            TLRPC$TL_messages_stickerSet filterPremiumStickers = getInstance(this.currentAccount).filterPremiumStickers(arrayList.get(i));
            if (filterPremiumStickers == null) {
                arrayList.remove(i);
                i--;
            } else {
                arrayList.set(i, filterPremiumStickers);
            }
            i++;
        }
        return arrayList;
    }

    public TLRPC$TL_messages_stickerSet filterPremiumStickers(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        boolean z;
        if (!this.premiumLocked || tLRPC$TL_messages_stickerSet == null) {
            return tLRPC$TL_messages_stickerSet;
        }
        int i = 0;
        int i2 = 0;
        while (true) {
            try {
                if (i2 >= tLRPC$TL_messages_stickerSet.documents.size()) {
                    z = false;
                    break;
                } else if (MessageObject.isPremiumSticker(tLRPC$TL_messages_stickerSet.documents.get(i2))) {
                    z = true;
                    break;
                } else {
                    i2++;
                }
            } catch (Exception e) {
                e = e;
            }
        }
        if (!z) {
            return tLRPC$TL_messages_stickerSet;
        }
        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_messages_stickerSet.getObjectSize());
        tLRPC$TL_messages_stickerSet.serializeToStream(nativeByteBuffer);
        nativeByteBuffer.position(0);
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = new TLRPC$TL_messages_stickerSet();
        nativeByteBuffer.readInt32(true);
        tLRPC$TL_messages_stickerSet2.readParams(nativeByteBuffer, true);
        nativeByteBuffer.reuse();
        while (i < tLRPC$TL_messages_stickerSet2.documents.size()) {
            try {
                if (MessageObject.isPremiumSticker(tLRPC$TL_messages_stickerSet2.documents.get(i))) {
                    tLRPC$TL_messages_stickerSet2.documents.remove(i);
                    tLRPC$TL_messages_stickerSet2.packs.remove(i);
                    i--;
                    if (tLRPC$TL_messages_stickerSet2.documents.isEmpty()) {
                        return null;
                    }
                }
                i++;
            } catch (Exception e2) {
                e = e2;
                tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet2;
                e.printStackTrace();
                return tLRPC$TL_messages_stickerSet;
            }
        }
        return tLRPC$TL_messages_stickerSet2;
    }

    public TopicsController getTopicsController() {
        return this.topicsController;
    }

    public boolean isForum(long j) {
        TLRPC$Chat chat = getChat(Long.valueOf(-j));
        return chat != null && chat.forum;
    }

    public void markAllTopicsAsRead(final long j) {
        getMessagesStorage().loadTopics(j, new j$.util.function.Consumer() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda233
            @Override // j$.util.function.Consumer
            public final void accept(Object obj) {
                MessagesController.this.lambda$markAllTopicsAsRead$7(j, (ArrayList) obj);
            }

            @Override // j$.util.function.Consumer
            public /* synthetic */ j$.util.function.Consumer andThen(j$.util.function.Consumer consumer) {
                return consumer.getClass();
            }
        });
    }

    public /* synthetic */ void lambda$markAllTopicsAsRead$7(final long j, final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda118
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$markAllTopicsAsRead$6(arrayList, j);
            }
        });
    }

    public /* synthetic */ void lambda$markAllTopicsAsRead$6(ArrayList arrayList, long j) {
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$TL_forumTopic tLRPC$TL_forumTopic = (TLRPC$TL_forumTopic) arrayList.get(i);
                MessagesController messagesController = getMessagesController();
                int i2 = tLRPC$TL_forumTopic.top_message;
                TLRPC$Message tLRPC$Message = tLRPC$TL_forumTopic.topMessage;
                messagesController.markDialogAsRead(j, i2, 0, tLRPC$Message != null ? tLRPC$Message.date : 0, false, tLRPC$TL_forumTopic.id, 0, true, 0);
                getMessagesStorage().updateRepliesMaxReadId(-j, tLRPC$TL_forumTopic.id, tLRPC$TL_forumTopic.top_message, 0, true);
            }
        }
    }

    /* loaded from: classes.dex */
    public class SponsoredMessagesInfo {
        public long loadTime;
        public boolean loading;
        public ArrayList<MessageObject> messages;
        public Integer posts_between;

        public SponsoredMessagesInfo() {
            MessagesController.this = r1;
        }
    }

    /* loaded from: classes.dex */
    public class SendAsPeersInfo {
        private long loadTime;
        private boolean loading;
        private TLRPC$TL_channels_sendAsPeers sendAsPeers;

        private SendAsPeersInfo() {
            MessagesController.this = r1;
        }
    }

    /* loaded from: classes.dex */
    public static class FaqSearchResult {
        public int num;
        public String[] path;
        public String title;
        public String url;

        public FaqSearchResult(String str, String[] strArr, String str2) {
            this.title = str;
            this.path = strArr;
            this.url = str2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof FaqSearchResult)) {
                return false;
            }
            return this.title.equals(((FaqSearchResult) obj).title);
        }

        public String toString() {
            SerializedData serializedData = new SerializedData();
            serializedData.writeInt32(this.num);
            int i = 0;
            serializedData.writeInt32(0);
            serializedData.writeString(this.title);
            String[] strArr = this.path;
            serializedData.writeInt32(strArr != null ? strArr.length : 0);
            if (this.path != null) {
                while (true) {
                    String[] strArr2 = this.path;
                    if (i >= strArr2.length) {
                        break;
                    }
                    serializedData.writeString(strArr2[i]);
                    i++;
                }
            }
            serializedData.writeString(this.url);
            return Utilities.bytesToHex(serializedData.toByteArray());
        }
    }

    /* loaded from: classes.dex */
    public static class EmojiSound {
        public long accessHash;
        public byte[] fileReference;
        public long id;

        public EmojiSound(long j, long j2, String str) {
            this.id = j;
            this.accessHash = j2;
            this.fileReference = Base64.decode(str, 8);
        }

        public EmojiSound(long j, long j2, byte[] bArr) {
            this.id = j;
            this.accessHash = j2;
            this.fileReference = bArr;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof EmojiSound)) {
                return false;
            }
            EmojiSound emojiSound = (EmojiSound) obj;
            return this.id == emojiSound.id && this.accessHash == emojiSound.accessHash && Arrays.equals(this.fileReference, emojiSound.fileReference);
        }
    }

    public void clearQueryTime() {
        this.lastServerQueryTime.clear();
        this.lastScheduledServerQueryTime.clear();
    }

    /* loaded from: classes.dex */
    public static class DiceFrameSuccess {
        public int frame;
        public int num;

        public DiceFrameSuccess(int i, int i2) {
            this.frame = i;
            this.num = i2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof DiceFrameSuccess)) {
                return false;
            }
            DiceFrameSuccess diceFrameSuccess = (DiceFrameSuccess) obj;
            return this.frame == diceFrameSuccess.frame && this.num == diceFrameSuccess.num;
        }
    }

    /* loaded from: classes.dex */
    public static class UserActionUpdatesSeq extends TLRPC$Updates {
        private UserActionUpdatesSeq() {
        }
    }

    /* loaded from: classes.dex */
    public static class UserActionUpdatesPts extends TLRPC$Updates {
        private UserActionUpdatesPts() {
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    /* loaded from: classes.dex */
    public static class ReadTask {
        public long dialogId;
        public int maxDate;
        public int maxId;
        public long replyId;
        public long sendRequestTime;

        private ReadTask() {
        }
    }

    /* loaded from: classes.dex */
    public static class DialogFilter {
        private static int dialogFilterPointer = 10;
        public int flags;
        public int id;
        public int localId;
        public boolean locked;
        public String name;
        public int order;
        public volatile int pendingUnreadCount;
        public int unreadCount;
        public ArrayList<Long> alwaysShow = new ArrayList<>();
        public ArrayList<Long> neverShow = new ArrayList<>();
        public LongSparseIntArray pinnedDialogs = new LongSparseIntArray();
        public ArrayList<TLRPC$Dialog> dialogs = new ArrayList<>();

        public DialogFilter() {
            int i = dialogFilterPointer;
            dialogFilterPointer = i + 1;
            this.localId = i;
        }

        public boolean includesDialog(AccountInstance accountInstance, long j) {
            TLRPC$Dialog tLRPC$Dialog = accountInstance.getMessagesController().dialogs_dict.get(j);
            if (tLRPC$Dialog == null) {
                return false;
            }
            return includesDialog(accountInstance, j, tLRPC$Dialog);
        }

        public boolean includesDialog(AccountInstance accountInstance, long j, TLRPC$Dialog tLRPC$Dialog) {
            TLRPC$Chat chat;
            if (this.neverShow.contains(Long.valueOf(j))) {
                return false;
            }
            if (this.alwaysShow.contains(Long.valueOf(j))) {
                return true;
            }
            if (tLRPC$Dialog.folder_id != 0 && (this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
                return false;
            }
            MessagesController messagesController = accountInstance.getMessagesController();
            ContactsController contactsController = accountInstance.getContactsController();
            if (((this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0 && messagesController.isDialogMuted(tLRPC$Dialog.id, 0) && tLRPC$Dialog.unread_mentions_count == 0) || ((this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0 && messagesController.getDialogUnreadCount(tLRPC$Dialog) == 0 && !tLRPC$Dialog.unread_mark && tLRPC$Dialog.unread_mentions_count == 0)) {
                return false;
            }
            if (j > 0) {
                TLRPC$User user = messagesController.getUser(Long.valueOf(j));
                if (user != null) {
                    if (!user.bot) {
                        if (user.self || user.contact || contactsController.isContact(j)) {
                            if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
                                return true;
                            }
                        } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
                            return true;
                        }
                    } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
                        return true;
                    }
                }
            } else if (j < 0 && (chat = messagesController.getChat(Long.valueOf(-j))) != null) {
                if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
                        return true;
                    }
                } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
                    return true;
                }
            }
            return false;
        }

        public boolean alwaysShow(int i, TLRPC$Dialog tLRPC$Dialog) {
            TLRPC$EncryptedChat encryptedChat;
            if (tLRPC$Dialog == null) {
                return false;
            }
            long j = tLRPC$Dialog.id;
            if (DialogObject.isEncryptedDialog(j) && (encryptedChat = MessagesController.getInstance(i).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j)))) != null) {
                j = encryptedChat.user_id;
            }
            return this.alwaysShow.contains(Long.valueOf(j));
        }

        public boolean isDefault() {
            return this.id == 0;
        }
    }

    public /* synthetic */ int lambda$new$8(TLRPC$Dialog tLRPC$Dialog, TLRPC$Dialog tLRPC$Dialog2) {
        int i = this.sortingDialogFilter.pinnedDialogs.get(tLRPC$Dialog.id, Integer.MIN_VALUE);
        int i2 = this.sortingDialogFilter.pinnedDialogs.get(tLRPC$Dialog2.id, Integer.MIN_VALUE);
        boolean z = tLRPC$Dialog instanceof TLRPC$TL_dialogFolder;
        if (!z || (tLRPC$Dialog2 instanceof TLRPC$TL_dialogFolder)) {
            if (!z && (tLRPC$Dialog2 instanceof TLRPC$TL_dialogFolder)) {
                return 1;
            }
            if (i == Integer.MIN_VALUE && i2 != Integer.MIN_VALUE) {
                return 1;
            }
            if (i != Integer.MIN_VALUE && i2 == Integer.MIN_VALUE) {
                return -1;
            }
            if (i != Integer.MIN_VALUE) {
                if (i > i2) {
                    return 1;
                }
                return i < i2 ? -1 : 0;
            }
            MediaDataController mediaDataController = getMediaDataController();
            long lastMessageOrDraftDate = DialogObject.getLastMessageOrDraftDate(tLRPC$Dialog, mediaDataController.getDraft(tLRPC$Dialog.id, 0));
            long lastMessageOrDraftDate2 = DialogObject.getLastMessageOrDraftDate(tLRPC$Dialog2, mediaDataController.getDraft(tLRPC$Dialog2.id, 0));
            if (lastMessageOrDraftDate < lastMessageOrDraftDate2) {
                return 1;
            }
            return lastMessageOrDraftDate > lastMessageOrDraftDate2 ? -1 : 0;
        }
        return -1;
    }

    public /* synthetic */ int lambda$new$9(TLRPC$Dialog tLRPC$Dialog, TLRPC$Dialog tLRPC$Dialog2) {
        boolean z = tLRPC$Dialog instanceof TLRPC$TL_dialogFolder;
        if (!z || (tLRPC$Dialog2 instanceof TLRPC$TL_dialogFolder)) {
            if (!z && (tLRPC$Dialog2 instanceof TLRPC$TL_dialogFolder)) {
                return 1;
            }
            boolean z2 = tLRPC$Dialog.pinned;
            if (!z2 && tLRPC$Dialog2.pinned) {
                return 1;
            }
            if (z2 && !tLRPC$Dialog2.pinned) {
                return -1;
            }
            if (z2) {
                int i = tLRPC$Dialog.pinnedNum;
                int i2 = tLRPC$Dialog2.pinnedNum;
                if (i < i2) {
                    return 1;
                }
                return i > i2 ? -1 : 0;
            }
            MediaDataController mediaDataController = getMediaDataController();
            long lastMessageOrDraftDate = DialogObject.getLastMessageOrDraftDate(tLRPC$Dialog, mediaDataController.getDraft(tLRPC$Dialog.id, 0));
            long lastMessageOrDraftDate2 = DialogObject.getLastMessageOrDraftDate(tLRPC$Dialog2, mediaDataController.getDraft(tLRPC$Dialog2.id, 0));
            if (lastMessageOrDraftDate < lastMessageOrDraftDate2) {
                return 1;
            }
            return lastMessageOrDraftDate > lastMessageOrDraftDate2 ? -1 : 0;
        }
        return -1;
    }

    public /* synthetic */ int lambda$new$10(TLRPC$Update tLRPC$Update, TLRPC$Update tLRPC$Update2) {
        int updateType = getUpdateType(tLRPC$Update);
        int updateType2 = getUpdateType(tLRPC$Update2);
        if (updateType != updateType2) {
            return AndroidUtilities.compare(updateType, updateType2);
        }
        if (updateType == 0) {
            return AndroidUtilities.compare(getUpdatePts(tLRPC$Update), getUpdatePts(tLRPC$Update2));
        }
        if (updateType == 1) {
            return AndroidUtilities.compare(getUpdateQts(tLRPC$Update), getUpdateQts(tLRPC$Update2));
        }
        if (updateType != 2) {
            return 0;
        }
        long updateChannelId = getUpdateChannelId(tLRPC$Update);
        long updateChannelId2 = getUpdateChannelId(tLRPC$Update2);
        if (updateChannelId == updateChannelId2) {
            return AndroidUtilities.compare(getUpdatePts(tLRPC$Update), getUpdatePts(tLRPC$Update2));
        }
        return AndroidUtilities.compare(updateChannelId, updateChannelId2);
    }

    public static MessagesController getInstance(int i) {
        MessagesController messagesController = Instance[i];
        if (messagesController == null) {
            synchronized (lockObjects[i]) {
                messagesController = Instance[i];
                if (messagesController == null) {
                    MessagesController[] messagesControllerArr = Instance;
                    MessagesController messagesController2 = new MessagesController(i);
                    messagesControllerArr[i] = messagesController2;
                    messagesController = messagesController2;
                }
            }
        }
        return messagesController;
    }

    public static SharedPreferences getNotificationsSettings(int i) {
        return getInstance(i).notificationsPreferences;
    }

    public static SharedPreferences getGlobalNotificationsSettings() {
        return getInstance(0).notificationsPreferences;
    }

    public static SharedPreferences getMainSettings(int i) {
        return getInstance(i).mainPreferences;
    }

    public static SharedPreferences getGlobalMainSettings() {
        return getInstance(0).mainPreferences;
    }

    public static SharedPreferences getEmojiSettings(int i) {
        return getInstance(i).emojiPreferences;
    }

    public static SharedPreferences getGlobalEmojiSettings() {
        return getInstance(0).emojiPreferences;
    }

    public MessagesController(int i) {
        super(i);
        this.chats = new ConcurrentHashMap<>(100, 1.0f, 2);
        this.encryptedChats = new ConcurrentHashMap<>(10, 1.0f, 2);
        this.users = new ConcurrentHashMap<>(100, 1.0f, 2);
        this.objectsByUsernames = new ConcurrentHashMap<>(100, 1.0f, 2);
        this.activeVoiceChatsMap = new HashMap<>();
        this.joiningToChannels = new ArrayList<>();
        this.exportedChats = new LongSparseArray<>();
        this.hintDialogs = new ArrayList<>();
        this.dialogsByFolder = new SparseArray<>();
        this.allDialogs = new ArrayList<>();
        this.dialogsForward = new ArrayList<>();
        this.dialogsServerOnly = new ArrayList<>();
        this.dialogsCanAddUsers = new ArrayList<>();
        this.dialogsMyChannels = new ArrayList<>();
        this.dialogsMyGroups = new ArrayList<>();
        this.dialogsChannelsOnly = new ArrayList<>();
        this.dialogsUsersOnly = new ArrayList<>();
        this.dialogsForBlock = new ArrayList<>();
        this.dialogsGroupsOnly = new ArrayList<>();
        this.selectedDialogFilter = new DialogFilter[2];
        this.dialogsLoadedTillDate = Integer.MAX_VALUE;
        this.dialogs_read_inbox_max = new ConcurrentHashMap<>(100, 1.0f, 2);
        this.dialogs_read_outbox_max = new ConcurrentHashMap<>(100, 1.0f, 2);
        this.dialogs_dict = new LongSparseArray<>();
        this.dialogMessage = new LongSparseArray<>();
        this.dialogMessagesByRandomIds = new LongSparseArray<>();
        this.deletedHistory = new LongSparseIntArray();
        this.dialogMessagesByIds = new SparseArray<>();
        this.printingUsers = new ConcurrentHashMap<>(20, 1.0f, 2);
        this.printingStrings = new LongSparseArray<>();
        this.printingStringsTypes = new LongSparseArray<>();
        this.sendingTypings = new LongSparseArray[12];
        this.onlinePrivacy = new ConcurrentHashMap<>(20, 1.0f, 2);
        this.loadingPeerSettings = new LongSparseArray<>();
        this.createdDialogIds = new ArrayList<>();
        this.createdScheduledDialogIds = new ArrayList<>();
        this.createdDialogMainThreadIds = new ArrayList<>();
        this.visibleDialogMainThreadIds = new ArrayList<>();
        this.visibleScheduledDialogMainThreadIds = new ArrayList<>();
        this.shortPollChannels = new LongSparseIntArray();
        this.needShortPollChannels = new LongSparseArray<>();
        this.shortPollOnlines = new LongSparseIntArray();
        this.needShortPollOnlines = new LongSparseArray<>();
        this.deletingDialogs = new LongSparseArray<>();
        this.clearingHistoryDialogs = new LongSparseArray<>();
        this.loadingBlockedPeers = false;
        this.blockePeers = new LongSparseIntArray();
        this.totalBlockedCount = -1;
        this.channelViewsToSend = new LongSparseArray<>();
        this.pollsToCheck = new LongSparseArray<>();
        this.premiumFeaturesTypesToPosition = new SparseIntArray();
        this.dialogFilters = new ArrayList<>();
        this.dialogFiltersById = new SparseArray<>();
        this.suggestedFilters = new ArrayList<>();
        this.updatesQueueChannels = new LongSparseArray<>();
        this.updatesStartWaitTimeChannels = new LongSparseLongArray();
        this.channelsPts = new LongSparseIntArray();
        this.gettingDifferenceChannels = new LongSparseArray<>();
        this.gettingChatInviters = new LongSparseArray<>();
        this.gettingUnknownChannels = new LongSparseArray<>();
        this.gettingUnknownDialogs = new LongSparseArray<>();
        this.checkingLastMessagesDialogs = new LongSparseArray<>();
        this.updatesQueueSeq = new ArrayList<>();
        this.updatesQueuePts = new ArrayList<>();
        this.updatesQueueQts = new ArrayList<>();
        this.fullUsers = new LongSparseArray<>();
        this.fullChats = new LongSparseArray<>();
        this.groupCalls = new LongSparseArray<>();
        this.groupCallsByChatId = new LongSparseArray<>();
        this.loadingFullUsers = new ArrayList<>();
        this.loadedFullUsers = new ArrayList<>();
        this.loadingFullChats = new ArrayList<>();
        this.loadingGroupCalls = new ArrayList<>();
        this.loadingFullParticipants = new ArrayList<>();
        this.loadedFullParticipants = new ArrayList<>();
        this.loadedFullChats = new ArrayList<>();
        this.channelAdmins = new LongSparseArray<>();
        this.loadingChannelAdmins = new LongSparseIntArray();
        this.migratedChats = new SparseIntArray();
        this.sponsoredMessages = new LongSparseArray<>();
        this.sendAsPeers = new LongSparseArray<>();
        this.reloadingWebpages = new HashMap<>();
        this.reloadingWebpagesPending = new LongSparseArray<>();
        this.reloadingScheduledWebpages = new HashMap<>();
        this.reloadingScheduledWebpagesPending = new LongSparseArray<>();
        this.lastScheduledServerQueryTime = new LongSparseArray<>();
        this.lastServerQueryTime = new LongSparseArray<>();
        this.reloadingMessages = new LongSparseArray<>();
        this.readTasks = new ArrayList<>();
        this.readTasksMap = new LongSparseArray<>();
        this.repliesReadTasks = new ArrayList<>();
        this.threadsReadTasksMap = new HashMap<>();
        this.nextDialogsCacheOffset = new SparseIntArray();
        this.loadingDialogs = new SparseBooleanArray();
        this.dialogsEndReached = new SparseBooleanArray();
        this.serverDialogsEndReached = new SparseBooleanArray();
        this.getDifferenceFirstSync = true;
        this.loadingPinnedDialogs = new SparseIntArray();
        this.faqSearchArray = new ArrayList<>();
        this.suggestContacts = true;
        this.themeCheckRunnable = MessagesController$$ExternalSyntheticLambda224.INSTANCE;
        this.passwordCheckRunnable = new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$new$0();
            }
        };
        this.uploadingThemes = new HashMap<>();
        this.maxBroadcastCount = 100;
        this.minGroupConvertSize = 200;
        this.gifSearchEmojies = new ArrayList<>();
        this.diceSuccess = new HashMap<>();
        this.emojiSounds = new HashMap<>();
        this.emojiInteractions = new HashMap<>();
        this.directPaymentsCurrency = new ArrayList();
        this.emojiStatusUntilValues = new LongSparseArray<>();
        this.dialogDateComparator = new Comparator() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda225
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$new$8;
                lambda$new$8 = MessagesController.this.lambda$new$8((TLRPC$Dialog) obj, (TLRPC$Dialog) obj2);
                return lambda$new$8;
            }
        };
        this.dialogComparator = new Comparator() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda226
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$new$9;
                lambda$new$9 = MessagesController.this.lambda$new$9((TLRPC$Dialog) obj, (TLRPC$Dialog) obj2);
                return lambda$new$9;
            }
        };
        this.updatesComparator = new Comparator() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda227
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$new$10;
                lambda$new$10 = MessagesController.this.lambda$new$10((TLRPC$Update) obj, (TLRPC$Update) obj2);
                return lambda$new$10;
            }
        };
        this.DIALOGS_LOAD_TYPE_CACHE = 1;
        this.DIALOGS_LOAD_TYPE_CHANNEL = 2;
        this.DIALOGS_LOAD_TYPE_UNKNOWN = 3;
        ImageLoader.getInstance();
        getMessagesStorage();
        getLocationController();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$new$11();
            }
        });
        addSupportUser();
        if (this.currentAccount == 0) {
            this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            this.emojiPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            this.notificationsPreferences = context.getSharedPreferences("Notifications" + this.currentAccount, 0);
            Context context2 = ApplicationLoader.applicationContext;
            this.mainPreferences = context2.getSharedPreferences("mainconfig" + this.currentAccount, 0);
            Context context3 = ApplicationLoader.applicationContext;
            this.emojiPreferences = context3.getSharedPreferences("emoji" + this.currentAccount, 0);
        }
        this.enableJoined = this.notificationsPreferences.getBoolean("EnableContactJoined", true);
        this.remoteConfigLoaded = this.mainPreferences.getBoolean("remoteConfigLoaded", false);
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
        this.updateCheckDelay = this.mainPreferences.getInt("updateCheckDelay", 86400);
        this.maxPinnedDialogsCount = this.mainPreferences.getInt("maxPinnedDialogsCount", 5);
        this.maxFolderPinnedDialogsCount = this.mainPreferences.getInt("maxFolderPinnedDialogsCount", 100);
        this.maxMessageLength = this.mainPreferences.getInt("maxMessageLength", 4096);
        this.maxCaptionLength = this.mainPreferences.getInt("maxCaptionLength", 1024);
        this.mapProvider = this.mainPreferences.getInt("mapProvider", 0);
        this.availableMapProviders = this.mainPreferences.getInt("availableMapProviders", 3);
        this.mapKey = this.mainPreferences.getString("pk", null);
        this.installReferer = this.mainPreferences.getString("installReferer", null);
        this.revokeTimeLimit = this.mainPreferences.getInt("revokeTimeLimit", this.revokeTimeLimit);
        this.revokeTimePmLimit = this.mainPreferences.getInt("revokeTimePmLimit", this.revokeTimePmLimit);
        this.canRevokePmInbox = this.mainPreferences.getBoolean("canRevokePmInbox", this.canRevokePmInbox);
        this.preloadFeaturedStickers = this.mainPreferences.getBoolean("preloadFeaturedStickers", false);
        this.youtubePipType = this.mainPreferences.getString("youtubePipType", "disabled");
        this.keepAliveService = this.mainPreferences.getBoolean("keepAliveService", false);
        this.backgroundConnection = this.mainPreferences.getBoolean("keepAliveService", false);
        this.promoDialogId = this.mainPreferences.getLong("proxy_dialog", 0L);
        this.nextPromoInfoCheckTime = this.mainPreferences.getInt("nextPromoInfoCheckTime", 0);
        this.promoDialogType = this.mainPreferences.getInt("promo_dialog_type", 0);
        this.promoPsaMessage = this.mainPreferences.getString("promo_psa_message", null);
        this.promoPsaType = this.mainPreferences.getString("promo_psa_type", null);
        this.proxyDialogAddress = this.mainPreferences.getString("proxyDialogAddress", null);
        this.nextTosCheckTime = this.notificationsPreferences.getInt("nextTosCheckTime", 0);
        this.venueSearchBot = this.mainPreferences.getString("venueSearchBot", "foursquare");
        this.gifSearchBot = this.mainPreferences.getString("gifSearchBot", "gif");
        this.imageSearchBot = this.mainPreferences.getString("imageSearchBot", "pic");
        this.blockedCountry = this.mainPreferences.getBoolean("blockedCountry", false);
        this.dcDomainName = this.mainPreferences.getString("dcDomainName2", ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv3.stel.com" : "apv3.stel.com");
        this.webFileDatacenterId = this.mainPreferences.getInt("webFileDatacenterId", ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? 2 : 4);
        this.suggestedLangCode = this.mainPreferences.getString("suggestedLangCode", "en");
        this.animatedEmojisZoom = this.mainPreferences.getFloat("animatedEmojisZoom", 0.625f);
        this.qrLoginCamera = this.mainPreferences.getBoolean("qrLoginCamera", false);
        this.saveGifsWithStickers = this.mainPreferences.getBoolean("saveGifsWithStickers", false);
        this.filtersEnabled = this.mainPreferences.getBoolean("filtersEnabled", false);
        this.getfileExperimentalParams = this.mainPreferences.getBoolean("getfileExperimentalParams", false);
        this.showFiltersTooltip = this.mainPreferences.getBoolean("showFiltersTooltip", false);
        this.autoarchiveAvailable = this.mainPreferences.getBoolean("autoarchiveAvailable", false);
        this.groupCallVideoMaxParticipants = this.mainPreferences.getInt("groipCallVideoMaxParticipants", 30);
        this.chatReadMarkSizeThreshold = this.mainPreferences.getInt("chatReadMarkSizeThreshold", 100);
        this.chatReadMarkExpirePeriod = this.mainPreferences.getInt("chatReadMarkExpirePeriod", 604800);
        this.ringtoneDurationMax = this.mainPreferences.getInt("ringtoneDurationMax", 5);
        this.ringtoneSizeMax = this.mainPreferences.getInt("ringtoneSizeMax", 102400);
        this.chatReadMarkExpirePeriod = this.mainPreferences.getInt("chatReadMarkExpirePeriod", 604800);
        this.suggestStickersApiOnly = this.mainPreferences.getBoolean("suggestStickersApiOnly", false);
        this.roundVideoSize = this.mainPreferences.getInt("roundVideoSize", 384);
        this.roundVideoBitrate = this.mainPreferences.getInt("roundVideoBitrate", 1000);
        this.roundAudioBitrate = this.mainPreferences.getInt("roundAudioBitrate", 64);
        this.pendingSuggestions = this.mainPreferences.getStringSet("pendingSuggestions", null);
        int i2 = this.mainPreferences.getInt("channelsLimitDefault", 500);
        this.channelsLimitDefault = i2;
        this.channelsLimitPremium = this.mainPreferences.getInt("channelsLimitPremium", i2 * 2);
        this.savedGifsLimitDefault = this.mainPreferences.getInt("savedGifsLimitDefault", 200);
        this.savedGifsLimitPremium = this.mainPreferences.getInt("savedGifsLimitPremium", 400);
        this.stickersFavedLimitDefault = this.mainPreferences.getInt("stickersFavedLimitDefault", 5);
        this.stickersFavedLimitPremium = this.mainPreferences.getInt("stickersFavedLimitPremium", 200);
        this.dialogFiltersLimitDefault = this.mainPreferences.getInt("dialogFiltersLimitDefault", 10);
        this.dialogFiltersLimitPremium = this.mainPreferences.getInt("dialogFiltersLimitPremium", 20);
        this.dialogFiltersChatsLimitDefault = this.mainPreferences.getInt("dialogFiltersChatsLimitDefault", 100);
        this.dialogFiltersChatsLimitPremium = this.mainPreferences.getInt("dialogFiltersChatsLimitPremium", 200);
        this.dialogFiltersPinnedLimitDefault = this.mainPreferences.getInt("dialogFiltersPinnedLimitDefault", 5);
        this.dialogFiltersPinnedLimitPremium = this.mainPreferences.getInt("dialogFiltersPinnedLimitPremium", 10);
        this.publicLinksLimitDefault = this.mainPreferences.getInt("publicLinksLimitDefault", 10);
        this.publicLinksLimitPremium = this.mainPreferences.getInt("publicLinksLimitPremium", 20);
        this.captionLengthLimitDefault = this.mainPreferences.getInt("captionLengthLimitDefault", 1024);
        this.captionLengthLimitPremium = this.mainPreferences.getInt("captionLengthLimitPremium", 4096);
        this.aboutLengthLimitDefault = this.mainPreferences.getInt("aboutLengthLimitDefault", 70);
        this.aboutLengthLimitPremium = this.mainPreferences.getInt("aboutLengthLimitPremium", 140);
        this.reactionsUserMaxDefault = this.mainPreferences.getInt("reactionsUserMaxDefault", 1);
        this.reactionsUserMaxPremium = this.mainPreferences.getInt("reactionsUserMaxPremium", 3);
        this.reactionsInChatMax = this.mainPreferences.getInt("reactionsInChatMax", 3);
        int i3 = this.mainPreferences.getInt("uploadMaxFileParts", 4000);
        this.uploadMaxFileParts = i3;
        this.uploadMaxFilePartsPremium = this.mainPreferences.getInt("uploadMaxFilePartsPremium", i3 * 2);
        this.premiumInvoiceSlug = this.mainPreferences.getString("premiumInvoiceSlug", null);
        this.premiumBotUsername = this.mainPreferences.getString("premiumBotUsername", null);
        this.premiumLocked = this.mainPreferences.getBoolean("premiumLocked", false);
        this.transcribeButtonPressed = this.mainPreferences.getInt("transcribeButtonPressed", 0);
        this.forumUpgradeParticipantsMin = this.mainPreferences.getInt("forumUpgradeParticipantsMin", 200);
        BuildVars.GOOGLE_AUTH_CLIENT_ID = this.mainPreferences.getString("googleAuthClientId", BuildVars.GOOGLE_AUTH_CLIENT_ID);
        Set<String> stringSet = this.mainPreferences.getStringSet("directPaymentsCurrency", null);
        if (stringSet != null) {
            this.directPaymentsCurrency.clear();
            this.directPaymentsCurrency.addAll(stringSet);
        }
        loadPremiumFeaturesPreviewOrder(this.mainPreferences.getString("premiumFeaturesTypesToPosition", null));
        if (this.pendingSuggestions != null) {
            this.pendingSuggestions = new HashSet(this.pendingSuggestions);
        } else {
            this.pendingSuggestions = new HashSet();
        }
        Set<String> stringSet2 = this.mainPreferences.getStringSet("exportUri2", null);
        this.exportUri = stringSet2;
        if (stringSet2 != null) {
            this.exportUri = new HashSet(this.exportUri);
        } else {
            HashSet hashSet = new HashSet();
            this.exportUri = hashSet;
            hashSet.add("content://(\\d+@)?com\\.whatsapp\\.provider\\.media/export_chat/");
            this.exportUri.add("content://(\\d+@)?com\\.whatsapp\\.w4b\\.provider\\.media/export_chat/");
            this.exportUri.add("content://jp\\.naver\\.line\\.android\\.line\\.common\\.FileProvider/export-chat/");
            this.exportUri.add(".*WhatsApp.*\\.txt$");
        }
        Set<String> stringSet3 = this.mainPreferences.getStringSet("exportGroupUri", null);
        this.exportGroupUri = stringSet3;
        if (stringSet3 != null) {
            this.exportGroupUri = new HashSet(this.exportGroupUri);
        } else {
            HashSet hashSet2 = new HashSet();
            this.exportGroupUri = hashSet2;
            hashSet2.add("@g.us/");
        }
        Set<String> stringSet4 = this.mainPreferences.getStringSet("exportPrivateUri", null);
        this.exportPrivateUri = stringSet4;
        if (stringSet4 != null) {
            this.exportPrivateUri = new HashSet(this.exportPrivateUri);
        } else {
            HashSet hashSet3 = new HashSet();
            this.exportPrivateUri = hashSet3;
            hashSet3.add("@s.whatsapp.net/");
        }
        Set<String> stringSet5 = this.mainPreferences.getStringSet("autologinDomains", null);
        this.autologinDomains = stringSet5;
        if (stringSet5 != null) {
            this.autologinDomains = new HashSet(this.autologinDomains);
        } else {
            this.autologinDomains = new HashSet();
        }
        Set<String> stringSet6 = this.mainPreferences.getStringSet("authDomains", null);
        this.authDomains = stringSet6;
        if (stringSet6 != null) {
            this.authDomains = new HashSet(this.authDomains);
        } else {
            this.authDomains = new HashSet();
        }
        this.autologinToken = this.mainPreferences.getString("autologinToken", null);
        Set<String> stringSet7 = this.mainPreferences.getStringSet("diceEmojies", null);
        if (stringSet7 == null) {
            HashSet<String> hashSet4 = new HashSet<>();
            this.diceEmojies = hashSet4;
            hashSet4.add("🎲");
            this.diceEmojies.add("🎯");
        } else {
            this.diceEmojies = new HashSet<>(stringSet7);
        }
        String string = this.mainPreferences.getString("diceSuccess", null);
        if (string == null) {
            this.diceSuccess.put("🎯", new DiceFrameSuccess(62, 6));
        } else {
            try {
                byte[] decode = Base64.decode(string, 0);
                if (decode != null) {
                    SerializedData serializedData = new SerializedData(decode);
                    int readInt32 = serializedData.readInt32(true);
                    for (int i4 = 0; i4 < readInt32; i4++) {
                        this.diceSuccess.put(serializedData.readString(true), new DiceFrameSuccess(serializedData.readInt32(true), serializedData.readInt32(true)));
                    }
                    serializedData.cleanup();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        String string2 = this.mainPreferences.getString("emojiSounds", null);
        if (string2 != null) {
            try {
                byte[] decode2 = Base64.decode(string2, 0);
                if (decode2 != null) {
                    SerializedData serializedData2 = new SerializedData(decode2);
                    int readInt322 = serializedData2.readInt32(true);
                    for (int i5 = 0; i5 < readInt322; i5++) {
                        this.emojiSounds.put(serializedData2.readString(true), new EmojiSound(serializedData2.readInt64(true), serializedData2.readInt64(true), serializedData2.readByteArray(true)));
                    }
                    serializedData2.cleanup();
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        String string3 = this.mainPreferences.getString("gifSearchEmojies", null);
        if (string3 == null) {
            this.gifSearchEmojies.add("👍");
            this.gifSearchEmojies.add("👎");
            this.gifSearchEmojies.add("😍");
            this.gifSearchEmojies.add("😂");
            this.gifSearchEmojies.add("😮");
            this.gifSearchEmojies.add("\u1var_");
            this.gifSearchEmojies.add("😥");
            this.gifSearchEmojies.add("😡");
            this.gifSearchEmojies.add("\u1var_");
            this.gifSearchEmojies.add("😎");
        } else {
            try {
                byte[] decode3 = Base64.decode(string3, 0);
                if (decode3 != null) {
                    SerializedData serializedData3 = new SerializedData(decode3);
                    int readInt323 = serializedData3.readInt32(true);
                    for (int i6 = 0; i6 < readInt323; i6++) {
                        this.gifSearchEmojies.add(serializedData3.readString(true));
                    }
                    serializedData3.cleanup();
                }
            } catch (Exception e3) {
                FileLog.e(e3);
            }
        }
        if (BuildVars.DEBUG_VERSION) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$new$12();
                }
            }, 2000L);
        }
        this.topicsController = new TopicsController(i);
    }

    public /* synthetic */ void lambda$new$11() {
        MessagesController messagesController = getMessagesController();
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileUploaded);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileUploadFailed);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileLoaded);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileLoadFailed);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.messageReceivedByServer);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.updateMessageMedia);
    }

    private void sendLoadPeersRequest(final TLObject tLObject, final ArrayList<TLObject> arrayList, final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, final ArrayList<TLRPC$User> arrayList2, final ArrayList<TLRPC$Chat> arrayList3, final ArrayList<DialogFilter> arrayList4, final SparseArray<DialogFilter> sparseArray, final ArrayList<Integer> arrayList5, final HashMap<Integer, HashSet<Long>> hashMap, final HashMap<Integer, HashSet<Long>> hashMap2, final HashSet<Integer> hashSet) {
        getConnectionsManager().sendRequest(tLObject, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda326
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$sendLoadPeersRequest$13(arrayList3, arrayList2, tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, tLObject, arrayList4, sparseArray, arrayList5, hashMap, hashMap2, hashSet, tLObject2, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$sendLoadPeersRequest$13(ArrayList arrayList, ArrayList arrayList2, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, ArrayList arrayList3, TLObject tLObject, ArrayList arrayList4, SparseArray sparseArray, ArrayList arrayList5, HashMap hashMap, HashMap hashMap2, HashSet hashSet, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject2 instanceof TLRPC$TL_messages_chats) {
            arrayList.addAll(((TLRPC$TL_messages_chats) tLObject2).chats);
        } else if (tLObject2 instanceof TLRPC$Vector) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject2;
            int size = tLRPC$Vector.objects.size();
            for (int i = 0; i < size; i++) {
                arrayList2.add((TLRPC$User) tLRPC$Vector.objects.get(i));
            }
        } else if (tLObject2 instanceof TLRPC$TL_messages_peerDialogs) {
            TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs = (TLRPC$TL_messages_peerDialogs) tLObject2;
            tLRPC$messages_Dialogs.dialogs.addAll(tLRPC$TL_messages_peerDialogs.dialogs);
            tLRPC$messages_Dialogs.messages.addAll(tLRPC$TL_messages_peerDialogs.messages);
            tLRPC$messages_Dialogs2.dialogs.addAll(tLRPC$TL_messages_peerDialogs.dialogs);
            tLRPC$messages_Dialogs2.messages.addAll(tLRPC$TL_messages_peerDialogs.messages);
            arrayList2.addAll(tLRPC$TL_messages_peerDialogs.users);
            arrayList.addAll(tLRPC$TL_messages_peerDialogs.chats);
        }
        arrayList3.remove(tLObject);
        if (arrayList3.isEmpty()) {
            getMessagesStorage().processLoadedFilterPeers(tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList2, arrayList, arrayList4, sparseArray, arrayList5, hashMap, hashMap2, hashSet);
        }
    }

    public void loadFilterPeers(final HashMap<Long, TLRPC$InputPeer> hashMap, final HashMap<Long, TLRPC$InputPeer> hashMap2, final HashMap<Long, TLRPC$InputPeer> hashMap3, final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, final ArrayList<TLRPC$User> arrayList, final ArrayList<TLRPC$Chat> arrayList2, final ArrayList<DialogFilter> arrayList3, final SparseArray<DialogFilter> sparseArray, final ArrayList<Integer> arrayList4, final HashMap<Integer, HashSet<Long>> hashMap4, final HashMap<Integer, HashSet<Long>> hashMap5, final HashSet<Integer> hashSet) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda130
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadFilterPeers$14(hashMap2, tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap4, hashMap5, hashSet, hashMap3, hashMap);
            }
        });
    }

    public /* synthetic */ void lambda$loadFilterPeers$14(HashMap hashMap, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray, ArrayList arrayList4, HashMap hashMap2, HashMap hashMap3, HashSet hashSet, HashMap hashMap4, HashMap hashMap5) {
        TLRPC$TL_users_getUsers tLRPC$TL_users_getUsers;
        int i;
        TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs;
        TLRPC$TL_messages_getChats tLRPC$TL_messages_getChats;
        TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels;
        ArrayList<TLObject> arrayList5 = new ArrayList<>();
        loop0: while (true) {
            tLRPC$TL_users_getUsers = null;
            for (Map.Entry entry : hashMap.entrySet()) {
                if (tLRPC$TL_users_getUsers == null) {
                    tLRPC$TL_users_getUsers = new TLRPC$TL_users_getUsers();
                    arrayList5.add(tLRPC$TL_users_getUsers);
                }
                tLRPC$TL_users_getUsers.id.add(getInputUser((TLRPC$InputPeer) entry.getValue()));
                if (tLRPC$TL_users_getUsers.id.size() == 100) {
                    break;
                }
            }
            sendLoadPeersRequest(tLRPC$TL_users_getUsers, arrayList5, tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashMap3, hashSet);
        }
        if (tLRPC$TL_users_getUsers != null) {
            i = 100;
            sendLoadPeersRequest(tLRPC$TL_users_getUsers, arrayList5, tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashMap3, hashSet);
        } else {
            i = 100;
        }
        TLObject tLObject = null;
        TLObject tLObject2 = null;
        for (Map.Entry entry2 : hashMap4.entrySet()) {
            TLRPC$InputPeer tLRPC$InputPeer = (TLRPC$InputPeer) entry2.getValue();
            if (tLRPC$InputPeer.chat_id != 0) {
                if (tLObject == null) {
                    tLRPC$TL_messages_getChats = new TLRPC$TL_messages_getChats();
                    arrayList5.add(tLRPC$TL_messages_getChats);
                } else {
                    tLRPC$TL_messages_getChats = tLObject;
                }
                tLRPC$TL_messages_getChats.id.add((Long) entry2.getKey());
                if (tLRPC$TL_messages_getChats.id.size() == i) {
                    sendLoadPeersRequest(tLRPC$TL_messages_getChats, arrayList5, tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashMap3, hashSet);
                    tLObject = null;
                } else {
                    tLObject = tLRPC$TL_messages_getChats;
                }
            } else if (tLRPC$InputPeer.channel_id != 0) {
                if (tLObject2 == null) {
                    TLObject tLRPC$TL_channels_getChannels2 = new TLRPC$TL_channels_getChannels();
                    arrayList5.add(tLRPC$TL_channels_getChannels2);
                    tLRPC$TL_channels_getChannels = tLRPC$TL_channels_getChannels2;
                } else {
                    tLRPC$TL_channels_getChannels = tLObject2;
                }
                tLRPC$TL_channels_getChannels.id.add(getInputChannel(tLRPC$InputPeer));
                if (tLRPC$TL_channels_getChannels.id.size() == i) {
                    sendLoadPeersRequest(tLRPC$TL_channels_getChannels, arrayList5, tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashMap3, hashSet);
                    tLObject2 = null;
                } else {
                    tLObject2 = tLRPC$TL_channels_getChannels;
                }
            }
        }
        if (tLObject != null) {
            sendLoadPeersRequest(tLObject, arrayList5, tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashMap3, hashSet);
        }
        if (tLObject2 != null) {
            sendLoadPeersRequest(tLObject2, arrayList5, tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashMap3, hashSet);
        }
        loop3: while (true) {
            tLRPC$TL_messages_getPeerDialogs = null;
            for (Map.Entry entry3 : hashMap5.entrySet()) {
                if (tLRPC$TL_messages_getPeerDialogs == null) {
                    tLRPC$TL_messages_getPeerDialogs = new TLRPC$TL_messages_getPeerDialogs();
                    arrayList5.add(tLRPC$TL_messages_getPeerDialogs);
                }
                TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
                tLRPC$TL_inputDialogPeer.peer = (TLRPC$InputPeer) entry3.getValue();
                tLRPC$TL_messages_getPeerDialogs.peers.add(tLRPC$TL_inputDialogPeer);
                if (tLRPC$TL_messages_getPeerDialogs.peers.size() == i) {
                    break;
                }
            }
            sendLoadPeersRequest(tLRPC$TL_messages_getPeerDialogs, arrayList5, tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashMap3, hashSet);
        }
        if (tLRPC$TL_messages_getPeerDialogs != null) {
            sendLoadPeersRequest(tLRPC$TL_messages_getPeerDialogs, arrayList5, tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap2, hashMap3, hashSet);
        }
    }

    public void processLoadedDialogFilters(final ArrayList<DialogFilter> arrayList, final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, final ArrayList<TLRPC$User> arrayList2, final ArrayList<TLRPC$Chat> arrayList3, final ArrayList<TLRPC$EncryptedChat> arrayList4, final int i) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda198
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processLoadedDialogFilters$17(tLRPC$messages_Dialogs, arrayList4, tLRPC$messages_Dialogs2, i, arrayList, arrayList2, arrayList3);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:186:0x00ab, code lost:
        if (r14 == (-r3.id)) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:193:0x00c4, code lost:
        if (r3.migrated_to != null) goto L29;
     */
    /* JADX WARN: Removed duplicated region for block: B:197:0x00ed  */
    /* JADX WARN: Removed duplicated region for block: B:198:0x00f7  */
    /* JADX WARN: Removed duplicated region for block: B:255:0x01e4  */
    /* JADX WARN: Removed duplicated region for block: B:258:0x020f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processLoadedDialogFilters$17(org.telegram.tgnet.TLRPC$messages_Dialogs r23, final java.util.ArrayList r24, final org.telegram.tgnet.TLRPC$messages_Dialogs r25, final int r26, final java.util.ArrayList r27, final java.util.ArrayList r28, final java.util.ArrayList r29) {
        /*
            Method dump skipped, instructions count: 741
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedDialogFilters$17(org.telegram.tgnet.TLRPC$messages_Dialogs, java.util.ArrayList, org.telegram.tgnet.TLRPC$messages_Dialogs, int, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList):void");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v3 */
    public /* synthetic */ void lambda$processLoadedDialogFilters$16(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, ArrayList arrayList4, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        int i2;
        boolean z;
        if (i != 2) {
            this.dialogFilters = arrayList;
            this.dialogFiltersById.clear();
            int size = this.dialogFilters.size();
            for (int i3 = 0; i3 < size; i3++) {
                DialogFilter dialogFilter = this.dialogFilters.get(i3);
                this.dialogFiltersById.put(dialogFilter.id, dialogFilter);
            }
            Collections.sort(this.dialogFilters, MessagesController$$ExternalSyntheticLambda229.INSTANCE);
            putUsers(arrayList2, true);
            putChats(arrayList3, true);
            this.dialogFiltersLoaded = true;
            getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
            if (i == 0) {
                loadRemoteFilters(false);
            }
            if (tLRPC$messages_Dialogs != null && !tLRPC$messages_Dialogs.dialogs.isEmpty()) {
                applyDialogsNotificationsSettings(tLRPC$messages_Dialogs.dialogs);
            }
            if (arrayList4 != null) {
                for (int i4 = 0; i4 < arrayList4.size(); i4++) {
                    TLRPC$EncryptedChat tLRPC$EncryptedChat = (TLRPC$EncryptedChat) arrayList4.get(i4);
                    if ((tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(tLRPC$EncryptedChat.layer) < SecretChatHelper.CURRENT_SECRET_CHAT_LAYER) {
                        getSecretChatHelper().sendNotifyLayerMessage(tLRPC$EncryptedChat, null);
                    }
                    putEncryptedChat(tLRPC$EncryptedChat, true);
                }
            }
            for (int i5 = 0; i5 < longSparseArray.size(); i5++) {
                long keyAt = longSparseArray.keyAt(i5);
                TLRPC$Dialog tLRPC$Dialog = (TLRPC$Dialog) longSparseArray.valueAt(i5);
                TLRPC$Dialog tLRPC$Dialog2 = this.dialogs_dict.get(keyAt);
                if (tLRPC$messages_Dialogs != null && tLRPC$messages_Dialogs.dialogs.contains(tLRPC$Dialog)) {
                    if (tLRPC$Dialog.draft instanceof TLRPC$TL_draftMessage) {
                        getMediaDataController().saveDraft(tLRPC$Dialog.id, 0, tLRPC$Dialog.draft, null, false);
                    }
                    if (tLRPC$Dialog2 != null) {
                        tLRPC$Dialog2.notify_settings = tLRPC$Dialog.notify_settings;
                    }
                }
                ArrayList<MessageObject> arrayList5 = (ArrayList) longSparseArray2.get(tLRPC$Dialog.id);
                if (tLRPC$Dialog2 == null) {
                    this.dialogs_dict.put(keyAt, tLRPC$Dialog);
                    this.dialogMessage.put(keyAt, arrayList5);
                    if (arrayList5 != null) {
                        for (int i6 = 0; i6 < arrayList5.size(); i6++) {
                            MessageObject messageObject = arrayList5.get(i6);
                            if (messageObject != null && messageObject.messageOwner.peer_id.channel_id == 0) {
                                this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                long j = messageObject.messageOwner.random_id;
                                if (j != 0) {
                                    this.dialogMessagesByRandomIds.put(j, messageObject);
                                }
                            }
                        }
                    }
                } else {
                    tLRPC$Dialog2.pinned = tLRPC$Dialog.pinned;
                    tLRPC$Dialog2.pinnedNum = tLRPC$Dialog.pinnedNum;
                    ArrayList<MessageObject> arrayList6 = this.dialogMessage.get(keyAt);
                    for (int i7 = 0; arrayList6 != null && i7 < arrayList6.size(); i7++) {
                        if (arrayList6.get(i7) != null && arrayList6.get(i7).deleted) {
                            z = true;
                            break;
                        }
                    }
                    z = false;
                    if (z || arrayList6 == null || tLRPC$Dialog2.top_message > 0) {
                        if (tLRPC$Dialog.top_message < tLRPC$Dialog2.top_message) {
                            if ((arrayList6 == null) == (arrayList5 == null)) {
                                if (arrayList6 != null) {
                                    if (arrayList5 != null) {
                                        if (arrayList6.size() == arrayList5.size()) {
                                        }
                                    }
                                }
                            }
                        }
                        this.dialogs_dict.put(keyAt, tLRPC$Dialog);
                        this.dialogMessage.put(keyAt, arrayList5);
                        if (arrayList6 != null) {
                            for (int i8 = 0; i8 < arrayList6.size(); i8++) {
                                MessageObject messageObject2 = arrayList6.get(i8);
                                if (messageObject2 != null) {
                                    if (messageObject2.messageOwner.peer_id.channel_id == 0) {
                                        this.dialogMessagesByIds.remove(messageObject2.getId());
                                    }
                                    long j2 = messageObject2.messageOwner.random_id;
                                    if (j2 != 0) {
                                        this.dialogMessagesByRandomIds.remove(j2);
                                    }
                                }
                            }
                        }
                        if (arrayList5 != null) {
                            for (int i9 = 0; i9 < arrayList5.size(); i9++) {
                                MessageObject messageObject3 = arrayList5.get(i9);
                                if (messageObject3 != null && messageObject3.messageOwner.peer_id.channel_id == 0) {
                                    int i10 = 0;
                                    while (true) {
                                        if (arrayList6 == null || i10 >= arrayList6.size()) {
                                            break;
                                        }
                                        MessageObject messageObject4 = arrayList6.get(i10);
                                        if (messageObject4 != null && messageObject4.getId() == messageObject3.getId()) {
                                            messageObject3.deleted = messageObject4.deleted;
                                            break;
                                        }
                                        i10++;
                                    }
                                    this.dialogMessagesByIds.put(messageObject3.getId(), messageObject3);
                                    long j3 = messageObject3.messageOwner.random_id;
                                    if (j3 != 0) {
                                        this.dialogMessagesByRandomIds.put(j3, messageObject3);
                                    }
                                }
                            }
                        }
                    } else {
                        this.dialogs_dict.put(keyAt, tLRPC$Dialog);
                        this.dialogMessage.put(keyAt, arrayList5);
                        for (int i11 = 0; i11 < arrayList6.size(); i11++) {
                            MessageObject messageObject5 = arrayList6.get(i11);
                            if (messageObject5 != null) {
                                if (messageObject5.messageOwner.peer_id.channel_id == 0) {
                                    this.dialogMessagesByIds.remove(messageObject5.getId());
                                }
                                long j4 = messageObject5.messageOwner.random_id;
                                if (j4 != 0) {
                                    this.dialogMessagesByRandomIds.remove(j4);
                                }
                            }
                        }
                        if (arrayList5 != null) {
                            for (int i12 = 0; i12 < arrayList5.size(); i12++) {
                                MessageObject messageObject6 = arrayList5.get(i12);
                                if (messageObject6 != null && messageObject6.messageOwner.peer_id.channel_id == 0) {
                                    int i13 = 0;
                                    while (true) {
                                        if (i13 >= arrayList6.size()) {
                                            break;
                                        }
                                        MessageObject messageObject7 = arrayList6.get(i13);
                                        if (messageObject7 != null && messageObject7.getId() == messageObject6.getId()) {
                                            messageObject6.deleted = messageObject7.deleted;
                                            break;
                                        }
                                        i13++;
                                    }
                                    this.dialogMessagesByIds.put(messageObject6.getId(), messageObject6);
                                    long j5 = messageObject6.messageOwner.random_id;
                                    if (j5 != 0) {
                                        this.dialogMessagesByRandomIds.put(j5, messageObject6);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.allDialogs.clear();
            int size2 = this.dialogs_dict.size();
            for (int i14 = 0; i14 < size2; i14++) {
                TLRPC$Dialog valueAt = this.dialogs_dict.valueAt(i14);
                if (this.deletingDialogs.indexOfKey(valueAt.id) < 0) {
                    this.allDialogs.add(valueAt);
                }
            }
            sortDialogs(null);
            i2 = 0;
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        } else {
            i2 = 0;
        }
        if (i != 0) {
            getUserConfig().filtersLoaded = true;
            getUserConfig().saveConfig(i2);
            this.loadingRemoteFilters = i2;
            getNotificationCenter().postNotificationName(NotificationCenter.filterSettingsUpdated, new Object[i2]);
        }
        lockFiltersInternal();
    }

    public static /* synthetic */ int lambda$processLoadedDialogFilters$15(DialogFilter dialogFilter, DialogFilter dialogFilter2) {
        int i = dialogFilter.order;
        int i2 = dialogFilter2.order;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void loadSuggestedFilters() {
        if (this.loadingSuggestedFilters) {
            return;
        }
        this.loadingSuggestedFilters = true;
        getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_messages_getSuggestedDialogFilters
            public static int constructor = -NUM;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
                TLRPC$Vector tLRPC$Vector = new TLRPC$Vector();
                int readInt32 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt32; i2++) {
                    TLRPC$TL_dialogFilterSuggested TLdeserialize = TLRPC$TL_dialogFilterSuggested.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return tLRPC$Vector;
                    }
                    tLRPC$Vector.objects.add(TLdeserialize);
                }
                return tLRPC$Vector;
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda242
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadSuggestedFilters$19(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadSuggestedFilters$19(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda144
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadSuggestedFilters$18(tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$loadSuggestedFilters$18(TLObject tLObject) {
        this.loadingSuggestedFilters = false;
        this.suggestedFilters.clear();
        if (tLObject instanceof TLRPC$Vector) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            int size = tLRPC$Vector.objects.size();
            for (int i = 0; i < size; i++) {
                this.suggestedFilters.add((TLRPC$TL_dialogFilterSuggested) tLRPC$Vector.objects.get(i));
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.suggestedFiltersLoaded, new Object[0]);
    }

    public void loadRemoteFilters(boolean z) {
        if (this.loadingRemoteFilters || !getUserConfig().isClientActivated()) {
            return;
        }
        if (!z && getUserConfig().filtersLoaded) {
            return;
        }
        if (z) {
            getUserConfig().filtersLoaded = false;
            getUserConfig().saveConfig(false);
        }
        getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_messages_getDialogFilters
            public static int constructor = -NUM;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z2) {
                TLRPC$Vector tLRPC$Vector = new TLRPC$Vector();
                int readInt32 = abstractSerializedData.readInt32(z2);
                for (int i2 = 0; i2 < readInt32; i2++) {
                    TLRPC$DialogFilter TLdeserialize = TLRPC$DialogFilter.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z2), z2);
                    if (TLdeserialize == null) {
                        return tLRPC$Vector;
                    }
                    tLRPC$Vector.objects.add(TLdeserialize);
                }
                return tLRPC$Vector;
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda259
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadRemoteFilters$21(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadRemoteFilters$21(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$Vector) {
            getMessagesStorage().checkLoadedRemoteFilters((TLRPC$Vector) tLObject);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$loadRemoteFilters$20();
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadRemoteFilters$20() {
        this.loadingRemoteFilters = false;
    }

    public void selectDialogFilter(DialogFilter dialogFilter, int i) {
        DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
        if (dialogFilterArr[i] == dialogFilter) {
            return;
        }
        DialogFilter dialogFilter2 = dialogFilterArr[i];
        dialogFilterArr[i] = dialogFilter;
        char c = 1;
        if (dialogFilterArr[i == 0 ? (char) 1 : (char) 0] == dialogFilter) {
            if (i != 0) {
                c = 0;
            }
            dialogFilterArr[c] = null;
        }
        if (dialogFilterArr[i] != null) {
            sortDialogs(null);
        } else if (dialogFilter2 == null) {
        } else {
            dialogFilter2.dialogs.clear();
        }
    }

    public void onFilterUpdate(DialogFilter dialogFilter) {
        for (int i = 0; i < 2; i++) {
            if (this.selectedDialogFilter[i] == dialogFilter) {
                sortDialogs(null);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
                return;
            }
        }
    }

    public void addFilter(DialogFilter dialogFilter, boolean z) {
        if (z) {
            int i = 254;
            int size = this.dialogFilters.size();
            for (int i2 = 0; i2 < size; i2++) {
                i = Math.min(i, this.dialogFilters.get(i2).order);
            }
            dialogFilter.order = i - 1;
            if (this.dialogFilters.get(0).isDefault() && !getUserConfig().isPremium()) {
                this.dialogFilters.add(1, dialogFilter);
            } else {
                this.dialogFilters.add(0, dialogFilter);
            }
        } else {
            int size2 = this.dialogFilters.size();
            int i3 = 0;
            for (int i4 = 0; i4 < size2; i4++) {
                i3 = Math.max(i3, this.dialogFilters.get(i4).order);
            }
            dialogFilter.order = i3 + 1;
            this.dialogFilters.add(dialogFilter);
        }
        this.dialogFiltersById.put(dialogFilter.id, dialogFilter);
        if (this.dialogFilters.size() == 1 && SharedConfig.getChatSwipeAction(this.currentAccount) != 5) {
            SharedConfig.updateChatListSwipeSetting(5);
        }
        lockFiltersInternal();
    }

    public void removeFilter(DialogFilter dialogFilter) {
        this.dialogFilters.remove(dialogFilter);
        this.dialogFiltersById.remove(dialogFilter.id);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
    }

    /* renamed from: loadAppConfig */
    public void lambda$new$12() {
        if (this.loadingAppConfig) {
            return;
        }
        this.loadingAppConfig = true;
        getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_help_getAppConfig
            public static int constructor = -NUM;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
                return TLRPC$JSONValue.TLdeserialize(abstractSerializedData, i, z);
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda247
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadAppConfig$23(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadAppConfig$23(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda143
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadAppConfig$22(tLObject);
            }
        });
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public /* synthetic */ void lambda$loadAppConfig$22(TLObject tLObject) {
        char c;
        TLRPC$TL_jsonObject tLRPC$TL_jsonObject;
        int i;
        TLRPC$TL_jsonObject tLRPC$TL_jsonObject2;
        TLRPC$TL_jsonObject tLRPC$TL_jsonObject3;
        int i2;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        TLRPC$TL_jsonObject tLRPC$TL_jsonObject4;
        int i3;
        int i4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        int intValue;
        char c2;
        boolean z9;
        if (tLObject instanceof TLRPC$TL_jsonObject) {
            SharedPreferences.Editor edit = this.mainPreferences.edit();
            resetAppConfig();
            TLRPC$TL_jsonObject tLRPC$TL_jsonObject5 = (TLRPC$TL_jsonObject) tLObject;
            int size = tLRPC$TL_jsonObject5.value.size();
            int i5 = 0;
            boolean z10 = false;
            boolean z11 = false;
            while (i5 < size) {
                TLRPC$TL_jsonObjectValue tLRPC$TL_jsonObjectValue = tLRPC$TL_jsonObject5.value.get(i5);
                String str = tLRPC$TL_jsonObjectValue.key;
                str.hashCode();
                switch (str.hashCode()) {
                    case -2086426873:
                        if (str.equals("dialog_filters_pinned_limit_premium")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1906216435:
                        if (str.equals("upload_max_fileparts_default")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1688620344:
                        if (str.equals("dialog_filters_tooltip")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1683918311:
                        if (str.equals("qr_login_camera")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1391086521:
                        if (str.equals("pending_suggestions")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1385240692:
                        if (str.equals("channels_public_limit_premium")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1287877531:
                        if (str.equals("stickers_faved_limit_premium")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1056002991:
                        if (str.equals("chat_read_mark_expire_period")) {
                            c = 7;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1032177933:
                        if (str.equals("emojies_send_dice")) {
                            c = '\b';
                            break;
                        }
                        c = 65535;
                        break;
                    case -980397720:
                        if (str.equals("url_auth_domains")) {
                            c = '\t';
                            break;
                        }
                        c = 65535;
                        break;
                    case -896467099:
                        if (str.equals("saved_gifs_limit_default")) {
                            c = '\n';
                            break;
                        }
                        c = 65535;
                        break;
                    case -581904190:
                        if (str.equals("dialog_filters_limit_default")) {
                            c = 11;
                            break;
                        }
                        c = 65535;
                        break;
                    case -561040027:
                        if (str.equals("premium_invoice_slug")) {
                            c = '\f';
                            break;
                        }
                        c = 65535;
                        break;
                    case -515715076:
                        if (str.equals("export_regex")) {
                            c = '\r';
                            break;
                        }
                        c = 65535;
                        break;
                    case -488472170:
                        if (str.equals("about_length_limit_default")) {
                            c = 14;
                            break;
                        }
                        c = 65535;
                        break;
                    case -473866179:
                        if (str.equals("reactions_user_max_premium")) {
                            c = 15;
                            break;
                        }
                        c = 65535;
                        break;
                    case -416504589:
                        if (str.equals("caption_length_limit_premium")) {
                            c = 16;
                            break;
                        }
                        c = 65535;
                        break;
                    case -404170231:
                        if (str.equals("keep_alive_service")) {
                            c = 17;
                            break;
                        }
                        c = 65535;
                        break;
                    case -381432266:
                        if (str.equals("premium_promo_order")) {
                            c = 18;
                            break;
                        }
                        c = 65535;
                        break;
                    case -377047005:
                        if (str.equals("ringtone_size_max")) {
                            c = 19;
                            break;
                        }
                        c = 65535;
                        break;
                    case -350971916:
                        if (str.equals("dialog_filters_chats_limit_premium")) {
                            c = 20;
                            break;
                        }
                        c = 65535;
                        break;
                    case -256319580:
                        if (str.equals("channels_limit_premium")) {
                            c = 21;
                            break;
                        }
                        c = 65535;
                        break;
                    case -253815153:
                        if (str.equals("background_connection")) {
                            c = 22;
                            break;
                        }
                        c = 65535;
                        break;
                    case -232883529:
                        if (str.equals("emojies_send_dice_success")) {
                            c = 23;
                            break;
                        }
                        c = 65535;
                        break;
                    case -223170831:
                        if (str.equals("dialog_filters_pinned_limit_default")) {
                            c = 24;
                            break;
                        }
                        c = 65535;
                        break;
                    case -111779186:
                        if (str.equals("autoarchive_setting_available")) {
                            c = 25;
                            break;
                        }
                        c = 65535;
                        break;
                    case -76561797:
                        if (str.equals("youtube_pip")) {
                            c = 26;
                            break;
                        }
                        c = 65535;
                        break;
                    case -24016028:
                        if (str.equals("emojies_animated_zoom")) {
                            c = 27;
                            break;
                        }
                        c = 65535;
                        break;
                    case 169095108:
                        if (str.equals("stickers_emoji_suggest_only_api")) {
                            c = 28;
                            break;
                        }
                        c = 65535;
                        break;
                    case 222975416:
                        if (str.equals("gif_search_emojies")) {
                            c = 29;
                            break;
                        }
                        c = 65535;
                        break;
                    case 227342346:
                        if (str.equals("autologin_domains")) {
                            c = 30;
                            break;
                        }
                        c = 65535;
                        break;
                    case 246778895:
                        if (str.equals("export_group_urls")) {
                            c = 31;
                            break;
                        }
                        c = 65535;
                        break;
                    case 314452116:
                        if (str.equals("autologin_token")) {
                            c = ' ';
                            break;
                        }
                        c = 65535;
                        break;
                    case 396402384:
                        if (str.equals("getfile_experimental_params")) {
                            c = '!';
                            break;
                        }
                        c = 65535;
                        break;
                    case 428604605:
                        if (str.equals("premium_playmarket_direct_currency_list")) {
                            c = '\"';
                            break;
                        }
                        c = 65535;
                        break;
                    case 478015350:
                        if (str.equals("channels_public_limit_default")) {
                            c = '#';
                            break;
                        }
                        c = 65535;
                        break;
                    case 525494819:
                        if (str.equals("upload_max_fileparts_premium")) {
                            c = '$';
                            break;
                        }
                        c = 65535;
                        break;
                    case 575378511:
                        if (str.equals("stickers_faved_limit_default")) {
                            c = '%';
                            break;
                        }
                        c = 65535;
                        break;
                    case 676199595:
                        if (str.equals("groupcall_video_participants_max")) {
                            c = '&';
                            break;
                        }
                        c = 65535;
                        break;
                    case 684764449:
                        if (str.equals("save_gifs_with_stickers")) {
                            c = '\'';
                            break;
                        }
                        c = 65535;
                        break;
                    case 812427767:
                        if (str.equals("login_google_oauth_client_id")) {
                            c = '(';
                            break;
                        }
                        c = 65535;
                        break;
                    case 917364150:
                        if (str.equals("ringtone_duration_max")) {
                            c = ')';
                            break;
                        }
                        c = 65535;
                        break;
                    case 992898905:
                        if (str.equals("inapp_update_check_delay")) {
                            c = '*';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1052355894:
                        if (str.equals("premium_bot_username")) {
                            c = '+';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1085221270:
                        if (str.equals("premium_purchase_blocked")) {
                            c = ',';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1322701672:
                        if (str.equals("round_video_encoding")) {
                            c = '-';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1389389863:
                        if (str.equals("reactions_user_max_default")) {
                            c = '.';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1446751453:
                        if (str.equals("caption_length_limit_default")) {
                            c = '/';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1508636733:
                        if (str.equals("chat_read_mark_size_threshold")) {
                            c = '0';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1512284126:
                        if (str.equals("dialog_filters_chats_limit_default")) {
                            c = '1';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1535244155:
                        if (str.equals("saved_gifs_limit_premium")) {
                            c = '2';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1606936462:
                        if (str.equals("channels_limit_default")) {
                            c = '3';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1785205890:
                        if (str.equals("reactions_in_chat_max")) {
                            c = '4';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1849807064:
                        if (str.equals("dialog_filters_limit_premium")) {
                            c = '5';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1850325103:
                        if (str.equals("emojies_sounds")) {
                            c = '6';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1943239084:
                        if (str.equals("about_length_limit_premium")) {
                            c = '7';
                            break;
                        }
                        c = 65535;
                        break;
                    case 2074702027:
                        if (str.equals("export_private_urls")) {
                            c = '8';
                            break;
                        }
                        c = 65535;
                        break;
                    case 2130452052:
                        if (str.equals("forum_upgrade_participants_min")) {
                            c = '9';
                            break;
                        }
                        c = 65535;
                        break;
                    case 2136829446:
                        if (str.equals("dialog_filters_enabled")) {
                            c = ':';
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue instanceof TLRPC$TL_jsonNumber) {
                            double d = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue).value;
                            if (d != this.dialogFiltersPinnedLimitPremium) {
                                int i6 = (int) d;
                                this.dialogFiltersPinnedLimitPremium = i6;
                                edit.putInt("dialogFiltersPinnedLimitPremium", i6);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 1:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue2 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue2 instanceof TLRPC$TL_jsonNumber) {
                            double d2 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue2).value;
                            if (d2 != this.uploadMaxFileParts) {
                                int i7 = (int) d2;
                                this.uploadMaxFileParts = i7;
                                edit.putInt("uploadMaxFileParts", i7);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 2:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue3 = tLRPC$TL_jsonObjectValue.value;
                        if ((tLRPC$JSONValue3 instanceof TLRPC$TL_jsonBool) && (z = ((TLRPC$TL_jsonBool) tLRPC$JSONValue3).value) != this.showFiltersTooltip) {
                            this.showFiltersTooltip = z;
                            edit.putBoolean("showFiltersTooltip", z);
                            getNotificationCenter().postNotificationName(NotificationCenter.filterSettingsUpdated, new Object[0]);
                            z10 = true;
                            break;
                        }
                        break;
                    case 3:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue4 = tLRPC$TL_jsonObjectValue.value;
                        if ((tLRPC$JSONValue4 instanceof TLRPC$TL_jsonBool) && (z2 = ((TLRPC$TL_jsonBool) tLRPC$JSONValue4).value) != this.qrLoginCamera) {
                            this.qrLoginCamera = z2;
                            edit.putBoolean("qrLoginCamera", z2);
                            z10 = true;
                            break;
                        }
                        break;
                    case 4:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        HashSet hashSet = new HashSet();
                        TLRPC$JSONValue tLRPC$JSONValue5 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue5 instanceof TLRPC$TL_jsonArray) {
                            TLRPC$TL_jsonArray tLRPC$TL_jsonArray = (TLRPC$TL_jsonArray) tLRPC$JSONValue5;
                            int size2 = tLRPC$TL_jsonArray.value.size();
                            for (int i8 = 0; i8 < size2; i8++) {
                                TLRPC$JSONValue tLRPC$JSONValue6 = tLRPC$TL_jsonArray.value.get(i8);
                                if (tLRPC$JSONValue6 instanceof TLRPC$TL_jsonString) {
                                    hashSet.add(((TLRPC$TL_jsonString) tLRPC$JSONValue6).value);
                                }
                            }
                        }
                        if (!this.pendingSuggestions.equals(hashSet)) {
                            this.pendingSuggestions = hashSet;
                            edit.putStringSet("pendingSuggestions", hashSet);
                            getNotificationCenter().postNotificationName(NotificationCenter.newSuggestionsAvailable, new Object[0]);
                            z10 = true;
                            break;
                        } else {
                            break;
                        }
                    case 5:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue7 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue7 instanceof TLRPC$TL_jsonNumber) {
                            double d3 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue7).value;
                            if (d3 != this.publicLinksLimitPremium) {
                                int i9 = (int) d3;
                                this.publicLinksLimitPremium = i9;
                                edit.putInt("publicLinksLimitPremium", i9);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 6:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue8 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue8 instanceof TLRPC$TL_jsonNumber) {
                            double d4 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue8).value;
                            if (d4 != this.stickersFavedLimitPremium) {
                                int i10 = (int) d4;
                                this.stickersFavedLimitPremium = i10;
                                edit.putInt("stickersFavedLimitPremium", i10);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 7:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue9 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue9 instanceof TLRPC$TL_jsonNumber) {
                            double d5 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue9).value;
                            if (d5 != this.chatReadMarkExpirePeriod) {
                                int i11 = (int) d5;
                                this.chatReadMarkExpirePeriod = i11;
                                edit.putInt("chatReadMarkExpirePeriod", i11);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '\b':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        HashSet<String> hashSet2 = new HashSet<>();
                        TLRPC$JSONValue tLRPC$JSONValue10 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue10 instanceof TLRPC$TL_jsonArray) {
                            TLRPC$TL_jsonArray tLRPC$TL_jsonArray2 = (TLRPC$TL_jsonArray) tLRPC$JSONValue10;
                            int size3 = tLRPC$TL_jsonArray2.value.size();
                            for (int i12 = 0; i12 < size3; i12++) {
                                TLRPC$JSONValue tLRPC$JSONValue11 = tLRPC$TL_jsonArray2.value.get(i12);
                                if (tLRPC$JSONValue11 instanceof TLRPC$TL_jsonString) {
                                    hashSet2.add(((TLRPC$TL_jsonString) tLRPC$JSONValue11).value.replace("️", ""));
                                }
                            }
                        }
                        if (!this.diceEmojies.equals(hashSet2)) {
                            this.diceEmojies = hashSet2;
                            edit.putStringSet("diceEmojies", hashSet2);
                            z10 = true;
                            break;
                        } else {
                            break;
                        }
                    case '\t':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        HashSet hashSet3 = new HashSet();
                        TLRPC$JSONValue tLRPC$JSONValue12 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue12 instanceof TLRPC$TL_jsonArray) {
                            TLRPC$TL_jsonArray tLRPC$TL_jsonArray3 = (TLRPC$TL_jsonArray) tLRPC$JSONValue12;
                            int size4 = tLRPC$TL_jsonArray3.value.size();
                            for (int i13 = 0; i13 < size4; i13++) {
                                TLRPC$JSONValue tLRPC$JSONValue13 = tLRPC$TL_jsonArray3.value.get(i13);
                                if (tLRPC$JSONValue13 instanceof TLRPC$TL_jsonString) {
                                    hashSet3.add(((TLRPC$TL_jsonString) tLRPC$JSONValue13).value);
                                }
                            }
                        }
                        if (!this.authDomains.equals(hashSet3)) {
                            this.authDomains = hashSet3;
                            edit.putStringSet("authDomains", hashSet3);
                            z10 = true;
                            break;
                        } else {
                            break;
                        }
                    case '\n':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue14 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue14 instanceof TLRPC$TL_jsonNumber) {
                            double d6 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue14).value;
                            if (d6 != this.savedGifsLimitDefault) {
                                int i14 = (int) d6;
                                this.savedGifsLimitDefault = i14;
                                edit.putInt("savedGifsLimitDefault", i14);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 11:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue15 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue15 instanceof TLRPC$TL_jsonNumber) {
                            double d7 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue15).value;
                            if (d7 != this.dialogFiltersLimitDefault) {
                                int i15 = (int) d7;
                                this.dialogFiltersLimitDefault = i15;
                                edit.putInt("dialogFiltersLimitDefault", i15);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '\f':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue16 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue16 instanceof TLRPC$TL_jsonString) {
                            String str2 = ((TLRPC$TL_jsonString) tLRPC$JSONValue16).value;
                            if (!str2.equals(this.premiumInvoiceSlug)) {
                                this.premiumInvoiceSlug = str2;
                                edit.putString("premiumInvoiceSlug", str2);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '\r':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        HashSet hashSet4 = new HashSet();
                        TLRPC$JSONValue tLRPC$JSONValue17 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue17 instanceof TLRPC$TL_jsonArray) {
                            TLRPC$TL_jsonArray tLRPC$TL_jsonArray4 = (TLRPC$TL_jsonArray) tLRPC$JSONValue17;
                            int size5 = tLRPC$TL_jsonArray4.value.size();
                            for (int i16 = 0; i16 < size5; i16++) {
                                TLRPC$JSONValue tLRPC$JSONValue18 = tLRPC$TL_jsonArray4.value.get(i16);
                                if (tLRPC$JSONValue18 instanceof TLRPC$TL_jsonString) {
                                    hashSet4.add(((TLRPC$TL_jsonString) tLRPC$JSONValue18).value);
                                }
                            }
                        }
                        if (!this.exportUri.equals(hashSet4)) {
                            this.exportUri = hashSet4;
                            edit.putStringSet("exportUri2", hashSet4);
                            z10 = true;
                            break;
                        } else {
                            break;
                        }
                    case 14:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue19 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue19 instanceof TLRPC$TL_jsonNumber) {
                            double d8 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue19).value;
                            if (d8 != this.aboutLengthLimitDefault) {
                                int i17 = (int) d8;
                                this.aboutLengthLimitDefault = i17;
                                edit.putInt("aboutLengthLimitDefault", i17);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 15:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue20 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue20 instanceof TLRPC$TL_jsonNumber) {
                            double d9 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue20).value;
                            if (d9 != this.reactionsUserMaxPremium) {
                                int i18 = (int) d9;
                                this.reactionsUserMaxPremium = i18;
                                edit.putInt("reactionsUserMaxPremium", i18);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 16:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue21 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue21 instanceof TLRPC$TL_jsonNumber) {
                            double d10 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue21).value;
                            if (d10 != this.captionLengthLimitPremium) {
                                int i19 = (int) d10;
                                this.captionLengthLimitPremium = i19;
                                edit.putInt("captionLengthLimitPremium", i19);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 17:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue22 = tLRPC$TL_jsonObjectValue.value;
                        if ((tLRPC$JSONValue22 instanceof TLRPC$TL_jsonBool) && (z3 = ((TLRPC$TL_jsonBool) tLRPC$JSONValue22).value) != this.keepAliveService) {
                            this.keepAliveService = z3;
                            edit.putBoolean("keepAliveService", z3);
                            z10 = true;
                            z11 = true;
                            break;
                        }
                        break;
                    case 18:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue23 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue23 instanceof TLRPC$TL_jsonArray) {
                            z10 = savePremiumFeaturesPreviewOrder(edit, ((TLRPC$TL_jsonArray) tLRPC$JSONValue23).value);
                            break;
                        } else {
                            break;
                        }
                    case 19:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue24 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue24 instanceof TLRPC$TL_jsonNumber) {
                            double d11 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue24).value;
                            if (d11 != this.ringtoneSizeMax) {
                                int i20 = (int) d11;
                                this.ringtoneSizeMax = i20;
                                edit.putInt("ringtoneSizeMax", i20);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 20:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue25 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue25 instanceof TLRPC$TL_jsonNumber) {
                            double d12 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue25).value;
                            if (d12 != this.dialogFiltersChatsLimitPremium) {
                                int i21 = (int) d12;
                                this.dialogFiltersChatsLimitPremium = i21;
                                edit.putInt("dialogFiltersChatsLimitPremium", i21);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 21:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue26 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue26 instanceof TLRPC$TL_jsonNumber) {
                            double d13 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue26).value;
                            if (d13 != this.channelsLimitPremium) {
                                int i22 = (int) d13;
                                this.channelsLimitPremium = i22;
                                edit.putInt("channelsLimitPremium", i22);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 22:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue27 = tLRPC$TL_jsonObjectValue.value;
                        if ((tLRPC$JSONValue27 instanceof TLRPC$TL_jsonBool) && (z4 = ((TLRPC$TL_jsonBool) tLRPC$JSONValue27).value) != this.backgroundConnection) {
                            this.backgroundConnection = z4;
                            edit.putBoolean("backgroundConnection", z4);
                            z10 = true;
                            z11 = true;
                            break;
                        }
                        break;
                    case 23:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        try {
                            HashMap<String, DiceFrameSuccess> hashMap = new HashMap<>();
                            TLRPC$JSONValue tLRPC$JSONValue28 = tLRPC$TL_jsonObjectValue.value;
                            if (tLRPC$JSONValue28 instanceof TLRPC$TL_jsonObject) {
                                TLRPC$TL_jsonObject tLRPC$TL_jsonObject6 = (TLRPC$TL_jsonObject) tLRPC$JSONValue28;
                                int size6 = tLRPC$TL_jsonObject6.value.size();
                                int i23 = 0;
                                while (i23 < size6) {
                                    TLRPC$TL_jsonObjectValue tLRPC$TL_jsonObjectValue2 = tLRPC$TL_jsonObject6.value.get(i23);
                                    TLRPC$JSONValue tLRPC$JSONValue29 = tLRPC$TL_jsonObjectValue2.value;
                                    if (tLRPC$JSONValue29 instanceof TLRPC$TL_jsonObject) {
                                        TLRPC$TL_jsonObject tLRPC$TL_jsonObject7 = (TLRPC$TL_jsonObject) tLRPC$JSONValue29;
                                        int size7 = tLRPC$TL_jsonObject7.value.size();
                                        tLRPC$TL_jsonObject4 = tLRPC$TL_jsonObject6;
                                        int i24 = Integer.MAX_VALUE;
                                        int i25 = Integer.MAX_VALUE;
                                        int i26 = 0;
                                        while (i26 < size7) {
                                            int i27 = size6;
                                            TLRPC$TL_jsonObjectValue tLRPC$TL_jsonObjectValue3 = tLRPC$TL_jsonObject7.value.get(i26);
                                            TLRPC$TL_jsonObject tLRPC$TL_jsonObject8 = tLRPC$TL_jsonObject7;
                                            if (tLRPC$TL_jsonObjectValue3.value instanceof TLRPC$TL_jsonNumber) {
                                                i4 = size7;
                                                if ("value".equals(tLRPC$TL_jsonObjectValue3.key)) {
                                                    i24 = (int) ((TLRPC$TL_jsonNumber) tLRPC$TL_jsonObjectValue3.value).value;
                                                } else if ("frame_start".equals(tLRPC$TL_jsonObjectValue3.key)) {
                                                    i25 = (int) ((TLRPC$TL_jsonNumber) tLRPC$TL_jsonObjectValue3.value).value;
                                                }
                                            } else {
                                                i4 = size7;
                                            }
                                            i26++;
                                            size6 = i27;
                                            tLRPC$TL_jsonObject7 = tLRPC$TL_jsonObject8;
                                            size7 = i4;
                                        }
                                        i3 = size6;
                                        if (i25 != Integer.MAX_VALUE && i24 != Integer.MAX_VALUE) {
                                            hashMap.put(tLRPC$TL_jsonObjectValue2.key.replace("️", ""), new DiceFrameSuccess(i25, i24));
                                        }
                                    } else {
                                        tLRPC$TL_jsonObject4 = tLRPC$TL_jsonObject6;
                                        i3 = size6;
                                    }
                                    i23++;
                                    tLRPC$TL_jsonObject6 = tLRPC$TL_jsonObject4;
                                    size6 = i3;
                                }
                            }
                            if (!this.diceSuccess.equals(hashMap)) {
                                this.diceSuccess = hashMap;
                                SerializedData serializedData = new SerializedData();
                                serializedData.writeInt32(this.diceSuccess.size());
                                for (Map.Entry<String, DiceFrameSuccess> entry : this.diceSuccess.entrySet()) {
                                    serializedData.writeString(entry.getKey());
                                    DiceFrameSuccess value = entry.getValue();
                                    serializedData.writeInt32(value.frame);
                                    serializedData.writeInt32(value.num);
                                }
                                edit.putString("diceSuccess", Base64.encodeToString(serializedData.toByteArray(), 0));
                                serializedData.cleanup();
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                            break;
                        }
                        break;
                    case 24:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue30 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue30 instanceof TLRPC$TL_jsonNumber) {
                            double d14 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue30).value;
                            if (d14 != this.dialogFiltersPinnedLimitDefault) {
                                int i28 = (int) d14;
                                this.dialogFiltersPinnedLimitDefault = i28;
                                edit.putInt("dialogFiltersPinnedLimitDefault", i28);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 25:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue31 = tLRPC$TL_jsonObjectValue.value;
                        if ((tLRPC$JSONValue31 instanceof TLRPC$TL_jsonBool) && (z5 = ((TLRPC$TL_jsonBool) tLRPC$JSONValue31).value) != this.autoarchiveAvailable) {
                            this.autoarchiveAvailable = z5;
                            edit.putBoolean("autoarchiveAvailable", z5);
                            z10 = true;
                            break;
                        }
                        break;
                    case 26:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue32 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue32 instanceof TLRPC$TL_jsonString) {
                            TLRPC$TL_jsonString tLRPC$TL_jsonString = (TLRPC$TL_jsonString) tLRPC$JSONValue32;
                            if (!tLRPC$TL_jsonString.value.equals(this.youtubePipType)) {
                                String str3 = tLRPC$TL_jsonString.value;
                                this.youtubePipType = str3;
                                edit.putString("youtubePipType", str3);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 27:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue33 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue33 instanceof TLRPC$TL_jsonNumber) {
                            double d15 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue33).value;
                            if (this.animatedEmojisZoom != d15) {
                                float f = (float) d15;
                                this.animatedEmojisZoom = f;
                                edit.putFloat("animatedEmojisZoom", f);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 28:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue34 = tLRPC$TL_jsonObjectValue.value;
                        if ((tLRPC$JSONValue34 instanceof TLRPC$TL_jsonBool) && (z6 = ((TLRPC$TL_jsonBool) tLRPC$JSONValue34).value) != this.suggestStickersApiOnly) {
                            this.suggestStickersApiOnly = z6;
                            edit.putBoolean("suggestStickersApiOnly", z6);
                            z10 = true;
                            break;
                        }
                        break;
                    case 29:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        ArrayList<String> arrayList = new ArrayList<>();
                        TLRPC$JSONValue tLRPC$JSONValue35 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue35 instanceof TLRPC$TL_jsonArray) {
                            TLRPC$TL_jsonArray tLRPC$TL_jsonArray5 = (TLRPC$TL_jsonArray) tLRPC$JSONValue35;
                            int size8 = tLRPC$TL_jsonArray5.value.size();
                            for (int i29 = 0; i29 < size8; i29++) {
                                TLRPC$JSONValue tLRPC$JSONValue36 = tLRPC$TL_jsonArray5.value.get(i29);
                                if (tLRPC$JSONValue36 instanceof TLRPC$TL_jsonString) {
                                    arrayList.add(((TLRPC$TL_jsonString) tLRPC$JSONValue36).value.replace("️", ""));
                                }
                            }
                        }
                        if (!this.gifSearchEmojies.equals(arrayList)) {
                            this.gifSearchEmojies = arrayList;
                            SerializedData serializedData2 = new SerializedData();
                            serializedData2.writeInt32(this.gifSearchEmojies.size());
                            int size9 = this.gifSearchEmojies.size();
                            for (int i30 = 0; i30 < size9; i30++) {
                                serializedData2.writeString(this.gifSearchEmojies.get(i30));
                            }
                            edit.putString("gifSearchEmojies", Base64.encodeToString(serializedData2.toByteArray(), 0));
                            serializedData2.cleanup();
                            z10 = true;
                            break;
                        } else {
                            break;
                        }
                    case 30:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        HashSet hashSet5 = new HashSet();
                        TLRPC$JSONValue tLRPC$JSONValue37 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue37 instanceof TLRPC$TL_jsonArray) {
                            TLRPC$TL_jsonArray tLRPC$TL_jsonArray6 = (TLRPC$TL_jsonArray) tLRPC$JSONValue37;
                            int size10 = tLRPC$TL_jsonArray6.value.size();
                            for (int i31 = 0; i31 < size10; i31++) {
                                TLRPC$JSONValue tLRPC$JSONValue38 = tLRPC$TL_jsonArray6.value.get(i31);
                                if (tLRPC$JSONValue38 instanceof TLRPC$TL_jsonString) {
                                    hashSet5.add(((TLRPC$TL_jsonString) tLRPC$JSONValue38).value);
                                }
                            }
                        }
                        if (!this.autologinDomains.equals(hashSet5)) {
                            this.autologinDomains = hashSet5;
                            edit.putStringSet("autologinDomains", hashSet5);
                            z10 = true;
                            break;
                        } else {
                            break;
                        }
                    case 31:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        HashSet hashSet6 = new HashSet();
                        TLRPC$JSONValue tLRPC$JSONValue39 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue39 instanceof TLRPC$TL_jsonArray) {
                            TLRPC$TL_jsonArray tLRPC$TL_jsonArray7 = (TLRPC$TL_jsonArray) tLRPC$JSONValue39;
                            int size11 = tLRPC$TL_jsonArray7.value.size();
                            for (int i32 = 0; i32 < size11; i32++) {
                                TLRPC$JSONValue tLRPC$JSONValue40 = tLRPC$TL_jsonArray7.value.get(i32);
                                if (tLRPC$JSONValue40 instanceof TLRPC$TL_jsonString) {
                                    hashSet6.add(((TLRPC$TL_jsonString) tLRPC$JSONValue40).value);
                                }
                            }
                        }
                        if (!this.exportGroupUri.equals(hashSet6)) {
                            this.exportGroupUri = hashSet6;
                            edit.putStringSet("exportGroupUri", hashSet6);
                            z10 = true;
                            break;
                        } else {
                            break;
                        }
                    case ' ':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue41 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue41 instanceof TLRPC$TL_jsonString) {
                            TLRPC$TL_jsonString tLRPC$TL_jsonString2 = (TLRPC$TL_jsonString) tLRPC$JSONValue41;
                            if (!tLRPC$TL_jsonString2.value.equals(this.autologinToken)) {
                                String str4 = tLRPC$TL_jsonString2.value;
                                this.autologinToken = str4;
                                edit.putString("autologinToken", str4);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '!':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue42 = tLRPC$TL_jsonObjectValue.value;
                        if ((tLRPC$JSONValue42 instanceof TLRPC$TL_jsonBool) && (z7 = ((TLRPC$TL_jsonBool) tLRPC$JSONValue42).value) != this.getfileExperimentalParams) {
                            this.getfileExperimentalParams = z7;
                            edit.putBoolean("getfileExperimentalParams", z7);
                            z10 = true;
                            break;
                        }
                        break;
                    case '\"':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue43 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue43 instanceof TLRPC$TL_jsonArray) {
                            HashSet hashSet7 = new HashSet();
                            Iterator<TLRPC$JSONValue> it = ((TLRPC$TL_jsonArray) tLRPC$JSONValue43).value.iterator();
                            while (it.hasNext()) {
                                TLRPC$JSONValue next = it.next();
                                if (next instanceof TLRPC$TL_jsonString) {
                                    hashSet7.add(((TLRPC$TL_jsonString) next).value);
                                }
                            }
                            if (!this.directPaymentsCurrency.containsAll(hashSet7) || !hashSet7.containsAll(this.directPaymentsCurrency)) {
                                this.directPaymentsCurrency.clear();
                                this.directPaymentsCurrency.addAll(hashSet7);
                                edit.putStringSet("directPaymentsCurrency", hashSet7);
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.billingProductDetailsUpdated, new Object[0]);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                        break;
                    case '#':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue44 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue44 instanceof TLRPC$TL_jsonNumber) {
                            double d16 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue44).value;
                            if (d16 != this.publicLinksLimitDefault) {
                                int i33 = (int) d16;
                                this.publicLinksLimitDefault = i33;
                                edit.putInt("publicLinksLimit", i33);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '$':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue45 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue45 instanceof TLRPC$TL_jsonNumber) {
                            double d17 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue45).value;
                            if (d17 != this.uploadMaxFilePartsPremium) {
                                int i34 = (int) d17;
                                this.uploadMaxFilePartsPremium = i34;
                                edit.putInt("uploadMaxFilePartsPremium", i34);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '%':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue46 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue46 instanceof TLRPC$TL_jsonNumber) {
                            double d18 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue46).value;
                            if (d18 != this.stickersFavedLimitDefault) {
                                int i35 = (int) d18;
                                this.stickersFavedLimitDefault = i35;
                                edit.putInt("stickersFavedLimitDefault", i35);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '&':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue47 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue47 instanceof TLRPC$TL_jsonNumber) {
                            double d19 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue47).value;
                            if (d19 != this.groupCallVideoMaxParticipants) {
                                int i36 = (int) d19;
                                this.groupCallVideoMaxParticipants = i36;
                                edit.putInt("groipCallVideoMaxParticipants", i36);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '\'':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue48 = tLRPC$TL_jsonObjectValue.value;
                        if ((tLRPC$JSONValue48 instanceof TLRPC$TL_jsonBool) && (z8 = ((TLRPC$TL_jsonBool) tLRPC$JSONValue48).value) != this.saveGifsWithStickers) {
                            this.saveGifsWithStickers = z8;
                            edit.putBoolean("saveGifsWithStickers", z8);
                            z10 = true;
                            break;
                        }
                        break;
                    case '(':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue49 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue49 instanceof TLRPC$TL_jsonString) {
                            String str5 = ((TLRPC$TL_jsonString) tLRPC$JSONValue49).value;
                            if (!ObjectsCompat$$ExternalSyntheticBackport0.m(BuildVars.GOOGLE_AUTH_CLIENT_ID, str5)) {
                                BuildVars.GOOGLE_AUTH_CLIENT_ID = str5;
                                edit.putString("googleAuthClientId", str5);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case ')':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue50 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue50 instanceof TLRPC$TL_jsonNumber) {
                            double d20 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue50).value;
                            if (d20 != this.ringtoneDurationMax) {
                                int i37 = (int) d20;
                                this.ringtoneDurationMax = i37;
                                edit.putInt("ringtoneDurationMax", i37);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '*':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue51 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue51 instanceof TLRPC$TL_jsonNumber) {
                            double d21 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue51).value;
                            if (d21 == this.updateCheckDelay) {
                                break;
                            } else {
                                int i38 = (int) d21;
                                this.updateCheckDelay = i38;
                                edit.putInt("updateCheckDelay", i38);
                            }
                        } else if ((tLRPC$JSONValue51 instanceof TLRPC$TL_jsonString) && (intValue = Utilities.parseInt((CharSequence) ((TLRPC$TL_jsonString) tLRPC$JSONValue51).value).intValue()) != this.updateCheckDelay) {
                            this.updateCheckDelay = intValue;
                            edit.putInt("updateCheckDelay", intValue);
                        }
                        z10 = true;
                        break;
                    case '+':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue52 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue52 instanceof TLRPC$TL_jsonString) {
                            String str6 = ((TLRPC$TL_jsonString) tLRPC$JSONValue52).value;
                            if (!str6.equals(this.premiumBotUsername)) {
                                this.premiumBotUsername = str6;
                                edit.putString("premiumBotUsername", str6);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case ',':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue53 = tLRPC$TL_jsonObjectValue.value;
                        if ((tLRPC$JSONValue53 instanceof TLRPC$TL_jsonBool) && this.premiumLocked != ((TLRPC$TL_jsonBool) tLRPC$JSONValue53).value) {
                            boolean z12 = ((TLRPC$TL_jsonBool) tLRPC$JSONValue53).value;
                            this.premiumLocked = z12;
                            edit.putBoolean("premiumLocked", z12);
                            z10 = true;
                            break;
                        }
                        break;
                    case '-':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue54 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue54 instanceof TLRPC$TL_jsonObject) {
                            TLRPC$TL_jsonObject tLRPC$TL_jsonObject9 = (TLRPC$TL_jsonObject) tLRPC$JSONValue54;
                            int size12 = tLRPC$TL_jsonObject9.value.size();
                            for (int i39 = 0; i39 < size12; i39++) {
                                TLRPC$TL_jsonObjectValue tLRPC$TL_jsonObjectValue4 = tLRPC$TL_jsonObject9.value.get(i39);
                                String str7 = tLRPC$TL_jsonObjectValue4.key;
                                str7.hashCode();
                                switch (str7.hashCode()) {
                                    case -233204595:
                                        if (str7.equals("diameter")) {
                                            c2 = 0;
                                            break;
                                        }
                                        c2 = 65535;
                                        break;
                                    case 258902020:
                                        if (str7.equals("audio_bitrate")) {
                                            c2 = 1;
                                            break;
                                        }
                                        c2 = 65535;
                                        break;
                                    case 1924434857:
                                        if (str7.equals("video_bitrate")) {
                                            c2 = 2;
                                            break;
                                        }
                                        c2 = 65535;
                                        break;
                                    default:
                                        c2 = 65535;
                                        break;
                                }
                                switch (c2) {
                                    case 0:
                                        TLRPC$JSONValue tLRPC$JSONValue55 = tLRPC$TL_jsonObjectValue4.value;
                                        if (tLRPC$JSONValue55 instanceof TLRPC$TL_jsonNumber) {
                                            double d22 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue55).value;
                                            if (d22 != this.roundVideoSize) {
                                                int i40 = (int) d22;
                                                this.roundVideoSize = i40;
                                                edit.putInt("roundVideoSize", i40);
                                                z10 = true;
                                                break;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    case 1:
                                        TLRPC$JSONValue tLRPC$JSONValue56 = tLRPC$TL_jsonObjectValue4.value;
                                        if (tLRPC$JSONValue56 instanceof TLRPC$TL_jsonNumber) {
                                            double d23 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue56).value;
                                            if (d23 != this.roundAudioBitrate) {
                                                int i41 = (int) d23;
                                                this.roundAudioBitrate = i41;
                                                edit.putInt("roundAudioBitrate", i41);
                                                z10 = true;
                                                break;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    case 2:
                                        TLRPC$JSONValue tLRPC$JSONValue57 = tLRPC$TL_jsonObjectValue4.value;
                                        if (tLRPC$JSONValue57 instanceof TLRPC$TL_jsonNumber) {
                                            double d24 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue57).value;
                                            if (d24 != this.roundVideoBitrate) {
                                                int i42 = (int) d24;
                                                this.roundVideoBitrate = i42;
                                                edit.putInt("roundVideoBitrate", i42);
                                                z10 = true;
                                                break;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                }
                            }
                            break;
                        } else {
                            break;
                        }
                    case '.':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue58 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue58 instanceof TLRPC$TL_jsonNumber) {
                            double d25 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue58).value;
                            if (d25 != this.reactionsUserMaxDefault) {
                                int i43 = (int) d25;
                                this.reactionsUserMaxDefault = i43;
                                edit.putInt("reactionsUserMaxDefault", i43);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '/':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue59 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue59 instanceof TLRPC$TL_jsonNumber) {
                            double d26 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue59).value;
                            if (d26 != this.captionLengthLimitDefault) {
                                int i44 = (int) d26;
                                this.captionLengthLimitDefault = i44;
                                edit.putInt("captionLengthLimitDefault", i44);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '0':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue60 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue60 instanceof TLRPC$TL_jsonNumber) {
                            double d27 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue60).value;
                            if (d27 != this.chatReadMarkSizeThreshold) {
                                int i45 = (int) d27;
                                this.chatReadMarkSizeThreshold = i45;
                                edit.putInt("chatReadMarkSizeThreshold", i45);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '1':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue61 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue61 instanceof TLRPC$TL_jsonNumber) {
                            double d28 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue61).value;
                            if (d28 != this.dialogFiltersChatsLimitDefault) {
                                int i46 = (int) d28;
                                this.dialogFiltersChatsLimitDefault = i46;
                                edit.putInt("dialogFiltersChatsLimitDefault", i46);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '2':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue62 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue62 instanceof TLRPC$TL_jsonNumber) {
                            double d29 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue62).value;
                            if (d29 != this.savedGifsLimitPremium) {
                                int i47 = (int) d29;
                                this.savedGifsLimitPremium = i47;
                                edit.putInt("savedGifsLimitPremium", i47);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '3':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue63 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue63 instanceof TLRPC$TL_jsonNumber) {
                            double d30 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue63).value;
                            if (d30 != this.channelsLimitDefault) {
                                int i48 = (int) d30;
                                this.channelsLimitDefault = i48;
                                edit.putInt("channelsLimitDefault", i48);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '4':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue64 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue64 instanceof TLRPC$TL_jsonNumber) {
                            double d31 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue64).value;
                            if (d31 != this.reactionsInChatMax) {
                                int i49 = (int) d31;
                                this.reactionsInChatMax = i49;
                                edit.putInt("reactionsInChatMax", i49);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '5':
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        TLRPC$JSONValue tLRPC$JSONValue65 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue65 instanceof TLRPC$TL_jsonNumber) {
                            double d32 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue65).value;
                            if (d32 != this.dialogFiltersLimitPremium) {
                                int i50 = (int) d32;
                                this.dialogFiltersLimitPremium = i50;
                                edit.putInt("dialogFiltersLimitPremium", i50);
                                z10 = true;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case '6':
                        try {
                            HashMap<String, EmojiSound> hashMap2 = new HashMap<>();
                            TLRPC$JSONValue tLRPC$JSONValue66 = tLRPC$TL_jsonObjectValue.value;
                            if (tLRPC$JSONValue66 instanceof TLRPC$TL_jsonObject) {
                                TLRPC$TL_jsonObject tLRPC$TL_jsonObject10 = (TLRPC$TL_jsonObject) tLRPC$JSONValue66;
                                int size13 = tLRPC$TL_jsonObject10.value.size();
                                int i51 = 0;
                                while (i51 < size13) {
                                    TLRPC$TL_jsonObjectValue tLRPC$TL_jsonObjectValue5 = tLRPC$TL_jsonObject10.value.get(i51);
                                    TLRPC$JSONValue tLRPC$JSONValue67 = tLRPC$TL_jsonObjectValue5.value;
                                    if (tLRPC$JSONValue67 instanceof TLRPC$TL_jsonObject) {
                                        TLRPC$TL_jsonObject tLRPC$TL_jsonObject11 = (TLRPC$TL_jsonObject) tLRPC$JSONValue67;
                                        int size14 = tLRPC$TL_jsonObject11.value.size();
                                        tLRPC$TL_jsonObject2 = tLRPC$TL_jsonObject10;
                                        long j = 0;
                                        long j2 = 0;
                                        int i52 = 0;
                                        String str8 = null;
                                        while (i52 < size14) {
                                            int i53 = size14;
                                            TLRPC$TL_jsonObjectValue tLRPC$TL_jsonObjectValue6 = tLRPC$TL_jsonObject11.value.get(i52);
                                            tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                                            try {
                                                if (tLRPC$TL_jsonObjectValue6.value instanceof TLRPC$TL_jsonString) {
                                                    i = size;
                                                    try {
                                                        if ("id".equals(tLRPC$TL_jsonObjectValue6.key)) {
                                                            j = Utilities.parseLong(((TLRPC$TL_jsonString) tLRPC$TL_jsonObjectValue6.value).value).longValue();
                                                        } else if ("access_hash".equals(tLRPC$TL_jsonObjectValue6.key)) {
                                                            j2 = Utilities.parseLong(((TLRPC$TL_jsonString) tLRPC$TL_jsonObjectValue6.value).value).longValue();
                                                        } else if ("file_reference_base64".equals(tLRPC$TL_jsonObjectValue6.key)) {
                                                            str8 = ((TLRPC$TL_jsonString) tLRPC$TL_jsonObjectValue6.value).value;
                                                        }
                                                    } catch (Exception e2) {
                                                        e = e2;
                                                        FileLog.e(e);
                                                        i5++;
                                                        tLRPC$TL_jsonObject5 = tLRPC$TL_jsonObject;
                                                        size = i;
                                                    }
                                                } else {
                                                    i = size;
                                                }
                                                i52++;
                                                size14 = i53;
                                                tLRPC$TL_jsonObject5 = tLRPC$TL_jsonObject;
                                                size = i;
                                            } catch (Exception e3) {
                                                e = e3;
                                                i = size;
                                                FileLog.e(e);
                                                i5++;
                                                tLRPC$TL_jsonObject5 = tLRPC$TL_jsonObject;
                                                size = i;
                                            }
                                        }
                                        tLRPC$TL_jsonObject3 = tLRPC$TL_jsonObject5;
                                        i2 = size;
                                        if (j != 0 && j2 != 0 && str8 != null) {
                                            hashMap2.put(tLRPC$TL_jsonObjectValue5.key.replace("️", ""), new EmojiSound(j, j2, str8));
                                        }
                                    } else {
                                        tLRPC$TL_jsonObject2 = tLRPC$TL_jsonObject10;
                                        tLRPC$TL_jsonObject3 = tLRPC$TL_jsonObject5;
                                        i2 = size;
                                    }
                                    i51++;
                                    tLRPC$TL_jsonObject10 = tLRPC$TL_jsonObject2;
                                    tLRPC$TL_jsonObject5 = tLRPC$TL_jsonObject3;
                                    size = i2;
                                }
                            }
                            tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                            i = size;
                            if (!this.emojiSounds.equals(hashMap2)) {
                                this.emojiSounds = hashMap2;
                                SerializedData serializedData3 = new SerializedData();
                                serializedData3.writeInt32(this.emojiSounds.size());
                                for (Map.Entry<String, EmojiSound> entry2 : this.emojiSounds.entrySet()) {
                                    serializedData3.writeString(entry2.getKey());
                                    EmojiSound value2 = entry2.getValue();
                                    serializedData3.writeInt64(value2.id);
                                    serializedData3.writeInt64(value2.accessHash);
                                    serializedData3.writeByteArray(value2.fileReference);
                                }
                                edit.putString("emojiSounds", Base64.encodeToString(serializedData3.toByteArray(), 0));
                                serializedData3.cleanup();
                                z10 = true;
                            }
                        } catch (Exception e4) {
                            e = e4;
                            tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        }
                        break;
                    case '7':
                        TLRPC$JSONValue tLRPC$JSONValue68 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue68 instanceof TLRPC$TL_jsonNumber) {
                            double d33 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue68).value;
                            if (d33 != this.aboutLengthLimitPremium) {
                                int i54 = (int) d33;
                                this.aboutLengthLimitPremium = i54;
                                edit.putInt("aboutLengthLimitPremium", i54);
                                z10 = true;
                            }
                        }
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        break;
                    case '8':
                        HashSet hashSet8 = new HashSet();
                        TLRPC$JSONValue tLRPC$JSONValue69 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue69 instanceof TLRPC$TL_jsonArray) {
                            TLRPC$TL_jsonArray tLRPC$TL_jsonArray8 = (TLRPC$TL_jsonArray) tLRPC$JSONValue69;
                            int size15 = tLRPC$TL_jsonArray8.value.size();
                            for (int i55 = 0; i55 < size15; i55++) {
                                TLRPC$JSONValue tLRPC$JSONValue70 = tLRPC$TL_jsonArray8.value.get(i55);
                                if (tLRPC$JSONValue70 instanceof TLRPC$TL_jsonString) {
                                    hashSet8.add(((TLRPC$TL_jsonString) tLRPC$JSONValue70).value);
                                }
                            }
                        }
                        if (!this.exportPrivateUri.equals(hashSet8)) {
                            this.exportPrivateUri = hashSet8;
                            edit.putStringSet("exportPrivateUri", hashSet8);
                            tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                            i = size;
                            z10 = true;
                            break;
                        }
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        break;
                    case '9':
                        TLRPC$JSONValue tLRPC$JSONValue71 = tLRPC$TL_jsonObjectValue.value;
                        if (tLRPC$JSONValue71 instanceof TLRPC$TL_jsonNumber) {
                            double d34 = ((TLRPC$TL_jsonNumber) tLRPC$JSONValue71).value;
                            if (d34 != this.forumUpgradeParticipantsMin) {
                                int i56 = (int) d34;
                                this.forumUpgradeParticipantsMin = i56;
                                edit.putInt("forumUpgradeParticipantsMin", i56);
                                tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                                i = size;
                                z10 = true;
                                break;
                            }
                        }
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        break;
                    case ':':
                        TLRPC$JSONValue tLRPC$JSONValue72 = tLRPC$TL_jsonObjectValue.value;
                        if ((tLRPC$JSONValue72 instanceof TLRPC$TL_jsonBool) && (z9 = ((TLRPC$TL_jsonBool) tLRPC$JSONValue72).value) != this.filtersEnabled) {
                            this.filtersEnabled = z9;
                            edit.putBoolean("filtersEnabled", z9);
                            z10 = true;
                        }
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        break;
                    default:
                        tLRPC$TL_jsonObject = tLRPC$TL_jsonObject5;
                        i = size;
                        break;
                }
                i5++;
                tLRPC$TL_jsonObject5 = tLRPC$TL_jsonObject;
                size = i;
            }
            if (z10) {
                edit.apply();
            }
            if (z11) {
                ApplicationLoader.startPushService();
                ConnectionsManager connectionsManager = getConnectionsManager();
                connectionsManager.setPushConnectionEnabled(connectionsManager.isPushConnectionEnabled());
            }
        }
        this.loadingAppConfig = false;
    }

    private void resetAppConfig() {
        this.getfileExperimentalParams = false;
        this.mainPreferences.edit().remove("getfileExperimentalParams");
    }

    private boolean savePremiumFeaturesPreviewOrder(SharedPreferences.Editor editor, ArrayList<TLRPC$JSONValue> arrayList) {
        int serverStringToFeatureType;
        StringBuilder sb = new StringBuilder();
        this.premiumFeaturesTypesToPosition.clear();
        boolean z = false;
        int i = 0;
        while (true) {
            String str = null;
            if (i >= arrayList.size()) {
                break;
            }
            if (arrayList.get(i) instanceof TLRPC$TL_jsonString) {
                str = ((TLRPC$TL_jsonString) arrayList.get(i)).value;
            }
            if (str != null && (serverStringToFeatureType = PremiumPreviewFragment.serverStringToFeatureType(str)) >= 0) {
                this.premiumFeaturesTypesToPosition.put(serverStringToFeatureType, i);
                if (sb.length() > 0) {
                    sb.append('_');
                }
                sb.append(serverStringToFeatureType);
            }
            i++;
        }
        if (sb.length() > 0) {
            String sb2 = sb.toString();
            boolean z2 = !sb2.equals(this.mainPreferences.getString("premiumFeaturesTypesToPosition", null));
            editor.putString("premiumFeaturesTypesToPosition", sb2);
            return z2;
        }
        editor.remove("premiumFeaturesTypesToPosition");
        if (this.mainPreferences.getString("premiumFeaturesTypesToPosition", null) != null) {
            z = true;
        }
        return z;
    }

    private void loadPremiumFeaturesPreviewOrder(String str) {
        this.premiumFeaturesTypesToPosition.clear();
        if (str != null) {
            String[] split = str.split("_");
            for (int i = 0; i < split.length; i++) {
                this.premiumFeaturesTypesToPosition.put(Integer.parseInt(split[i]), i);
            }
        }
    }

    public void removeSuggestion(long j, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (j == 0) {
            if (!this.pendingSuggestions.remove(str)) {
                return;
            }
            SharedPreferences.Editor edit = this.mainPreferences.edit();
            edit.putStringSet("pendingSuggestions", this.pendingSuggestions);
            edit.commit();
            getNotificationCenter().postNotificationName(NotificationCenter.newSuggestionsAvailable, new Object[0]);
        }
        TLRPC$TL_help_dismissSuggestion tLRPC$TL_help_dismissSuggestion = new TLRPC$TL_help_dismissSuggestion();
        tLRPC$TL_help_dismissSuggestion.suggestion = str;
        if (j == 0) {
            tLRPC$TL_help_dismissSuggestion.peer = new TLRPC$TL_inputPeerEmpty();
        } else {
            tLRPC$TL_help_dismissSuggestion.peer = getInputPeer(j);
        }
        getConnectionsManager().sendRequest(tLRPC$TL_help_dismissSuggestion, MessagesController$$ExternalSyntheticLambda357.INSTANCE);
    }

    public void updateConfig(final TLRPC$TL_config tLRPC$TL_config) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda166
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$updateConfig$25(tLRPC$TL_config);
            }
        });
    }

    public /* synthetic */ void lambda$updateConfig$25(TLRPC$TL_config tLRPC$TL_config) {
        getDownloadController().loadAutoDownloadConfig(false);
        lambda$new$12();
        this.remoteConfigLoaded = true;
        this.maxMegagroupCount = tLRPC$TL_config.megagroup_size_max;
        this.maxGroupCount = tLRPC$TL_config.chat_size_max;
        this.maxEditTime = tLRPC$TL_config.edit_time_limit;
        this.ratingDecay = tLRPC$TL_config.rating_e_decay;
        this.maxRecentGifsCount = tLRPC$TL_config.saved_gifs_limit;
        this.maxRecentStickersCount = tLRPC$TL_config.stickers_recent_limit;
        this.maxFaveStickersCount = tLRPC$TL_config.stickers_faved_limit;
        this.revokeTimeLimit = tLRPC$TL_config.revoke_time_limit;
        this.revokeTimePmLimit = tLRPC$TL_config.revoke_pm_time_limit;
        this.canRevokePmInbox = tLRPC$TL_config.revoke_pm_inbox;
        String str = tLRPC$TL_config.me_url_prefix;
        this.linkPrefix = str;
        boolean z = tLRPC$TL_config.force_try_ipv6;
        if (str.endsWith("/")) {
            String str2 = this.linkPrefix;
            this.linkPrefix = str2.substring(0, str2.length() - 1);
        }
        if (this.linkPrefix.startsWith("https://")) {
            this.linkPrefix = this.linkPrefix.substring(8);
        } else if (this.linkPrefix.startsWith("http://")) {
            this.linkPrefix = this.linkPrefix.substring(7);
        }
        this.callReceiveTimeout = tLRPC$TL_config.call_receive_timeout_ms;
        this.callRingTimeout = tLRPC$TL_config.call_ring_timeout_ms;
        this.callConnectTimeout = tLRPC$TL_config.call_connect_timeout_ms;
        this.callPacketTimeout = tLRPC$TL_config.call_packet_timeout_ms;
        this.maxPinnedDialogsCount = tLRPC$TL_config.pinned_dialogs_count_max;
        this.maxFolderPinnedDialogsCount = tLRPC$TL_config.pinned_infolder_count_max;
        this.maxMessageLength = tLRPC$TL_config.message_length_max;
        this.maxCaptionLength = tLRPC$TL_config.caption_length_max;
        this.preloadFeaturedStickers = tLRPC$TL_config.preload_featured_stickers;
        String str3 = tLRPC$TL_config.venue_search_username;
        if (str3 != null) {
            this.venueSearchBot = str3;
        }
        String str4 = tLRPC$TL_config.gif_search_username;
        if (str4 != null) {
            this.gifSearchBot = str4;
        }
        if (this.imageSearchBot != null) {
            this.imageSearchBot = tLRPC$TL_config.img_search_username;
        }
        this.blockedCountry = tLRPC$TL_config.blocked_mode;
        this.dcDomainName = tLRPC$TL_config.dc_txt_domain_name;
        this.webFileDatacenterId = tLRPC$TL_config.webfile_dc_id;
        String str5 = tLRPC$TL_config.suggested_lang_code;
        if (str5 != null) {
            String str6 = this.suggestedLangCode;
            boolean z2 = str6 == null || !str6.equals(str5);
            this.suggestedLangCode = tLRPC$TL_config.suggested_lang_code;
            if (z2) {
                LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
            }
        }
        Theme.loadRemoteThemes(this.currentAccount, false);
        Theme.checkCurrentRemoteTheme(false);
        if (tLRPC$TL_config.static_maps_provider == null) {
            tLRPC$TL_config.static_maps_provider = "telegram";
        }
        this.mapKey = null;
        this.mapProvider = 2;
        this.availableMapProviders = 0;
        FileLog.d("map providers = " + tLRPC$TL_config.static_maps_provider);
        String[] split = tLRPC$TL_config.static_maps_provider.split(",");
        for (int i = 0; i < split.length; i++) {
            String[] split2 = split[i].split("\\+");
            if (split2.length > 0) {
                String[] split3 = split2[0].split(":");
                if (split3.length > 0) {
                    if ("yandex".equals(split3[0])) {
                        if (i == 0) {
                            if (split2.length > 1) {
                                this.mapProvider = 3;
                            } else {
                                this.mapProvider = 1;
                            }
                        }
                        this.availableMapProviders |= 4;
                    } else if ("google".equals(split3[0])) {
                        if (i == 0 && split2.length > 1) {
                            this.mapProvider = 4;
                        }
                        this.availableMapProviders |= 1;
                    } else if ("telegram".equals(split3[0])) {
                        if (i == 0) {
                            this.mapProvider = 2;
                        }
                        this.availableMapProviders |= 2;
                    }
                    if (split3.length > 1) {
                        this.mapKey = split3[1];
                    }
                }
            }
        }
        SharedPreferences.Editor edit = this.mainPreferences.edit();
        edit.putBoolean("remoteConfigLoaded", this.remoteConfigLoaded);
        edit.putInt("maxGroupCount", this.maxGroupCount);
        edit.putInt("maxMegagroupCount", this.maxMegagroupCount);
        edit.putInt("maxEditTime", this.maxEditTime);
        edit.putInt("ratingDecay", this.ratingDecay);
        edit.putInt("maxRecentGifsCount", this.maxRecentGifsCount);
        edit.putInt("maxRecentStickersCount", this.maxRecentStickersCount);
        edit.putInt("maxFaveStickersCount", this.maxFaveStickersCount);
        edit.putInt("callReceiveTimeout", this.callReceiveTimeout);
        edit.putInt("callRingTimeout", this.callRingTimeout);
        edit.putInt("callConnectTimeout", this.callConnectTimeout);
        edit.putInt("callPacketTimeout", this.callPacketTimeout);
        edit.putString("linkPrefix", this.linkPrefix);
        edit.putInt("maxPinnedDialogsCount", this.maxPinnedDialogsCount);
        edit.putInt("maxFolderPinnedDialogsCount", this.maxFolderPinnedDialogsCount);
        edit.putInt("maxMessageLength", this.maxMessageLength);
        edit.putInt("maxCaptionLength", this.maxCaptionLength);
        edit.putBoolean("preloadFeaturedStickers", this.preloadFeaturedStickers);
        edit.putInt("revokeTimeLimit", this.revokeTimeLimit);
        edit.putInt("revokeTimePmLimit", this.revokeTimePmLimit);
        edit.putInt("mapProvider", this.mapProvider);
        String str7 = this.mapKey;
        if (str7 != null) {
            edit.putString("pk", str7);
        } else {
            edit.remove("pk");
        }
        edit.putBoolean("canRevokePmInbox", this.canRevokePmInbox);
        edit.putBoolean("blockedCountry", this.blockedCountry);
        edit.putString("venueSearchBot", this.venueSearchBot);
        edit.putString("gifSearchBot", this.gifSearchBot);
        edit.putString("imageSearchBot", this.imageSearchBot);
        edit.putString("dcDomainName2", this.dcDomainName);
        edit.putInt("webFileDatacenterId", this.webFileDatacenterId);
        edit.putString("suggestedLangCode", this.suggestedLangCode);
        edit.putBoolean("forceTryIpV6", z);
        edit.commit();
        getConnectionsManager().setForceTryIpV6(z);
        LocaleController.getInstance().checkUpdateForCurrentRemoteLocale(this.currentAccount, tLRPC$TL_config.lang_pack_version, tLRPC$TL_config.base_lang_pack_version);
        getNotificationCenter().postNotificationName(NotificationCenter.configLoaded, new Object[0]);
    }

    public void addSupportUser() {
        TLRPC$TL_userForeign_old2 tLRPC$TL_userForeign_old2 = new TLRPC$TL_userForeign_old2();
        tLRPC$TL_userForeign_old2.phone = "333";
        tLRPC$TL_userForeign_old2.id = 333000L;
        tLRPC$TL_userForeign_old2.first_name = "Telegram";
        tLRPC$TL_userForeign_old2.last_name = "";
        tLRPC$TL_userForeign_old2.status = null;
        tLRPC$TL_userForeign_old2.photo = new TLRPC$TL_userProfilePhotoEmpty();
        putUser(tLRPC$TL_userForeign_old2, true);
        TLRPC$TL_userForeign_old2 tLRPC$TL_userForeign_old22 = new TLRPC$TL_userForeign_old2();
        tLRPC$TL_userForeign_old22.phone = "42777";
        tLRPC$TL_userForeign_old22.id = 777000L;
        tLRPC$TL_userForeign_old22.verified = true;
        tLRPC$TL_userForeign_old22.first_name = "Telegram";
        tLRPC$TL_userForeign_old22.last_name = "Notifications";
        tLRPC$TL_userForeign_old22.status = null;
        tLRPC$TL_userForeign_old22.photo = new TLRPC$TL_userProfilePhotoEmpty();
        putUser(tLRPC$TL_userForeign_old22, true);
    }

    public TLRPC$InputUser getInputUser(TLRPC$User tLRPC$User) {
        if (tLRPC$User == null) {
            return new TLRPC$TL_inputUserEmpty();
        }
        if (tLRPC$User.id == getUserConfig().getClientUserId()) {
            return new TLRPC$TL_inputUserSelf();
        }
        TLRPC$TL_inputUser tLRPC$TL_inputUser = new TLRPC$TL_inputUser();
        tLRPC$TL_inputUser.user_id = tLRPC$User.id;
        tLRPC$TL_inputUser.access_hash = tLRPC$User.access_hash;
        return tLRPC$TL_inputUser;
    }

    public TLRPC$InputUser getInputUser(TLRPC$InputPeer tLRPC$InputPeer) {
        if (tLRPC$InputPeer == null) {
            return new TLRPC$TL_inputUserEmpty();
        }
        if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerSelf) {
            return new TLRPC$TL_inputUserSelf();
        }
        TLRPC$TL_inputUser tLRPC$TL_inputUser = new TLRPC$TL_inputUser();
        tLRPC$TL_inputUser.user_id = tLRPC$InputPeer.user_id;
        tLRPC$TL_inputUser.access_hash = tLRPC$InputPeer.access_hash;
        return tLRPC$TL_inputUser;
    }

    public TLRPC$InputUser getInputUser(long j) {
        return getInputUser(getUser(Long.valueOf(j)));
    }

    public static TLRPC$InputChannel getInputChannel(TLRPC$Chat tLRPC$Chat) {
        if ((tLRPC$Chat instanceof TLRPC$TL_channel) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden)) {
            TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
            tLRPC$TL_inputChannel.channel_id = tLRPC$Chat.id;
            tLRPC$TL_inputChannel.access_hash = tLRPC$Chat.access_hash;
            return tLRPC$TL_inputChannel;
        }
        return new TLRPC$TL_inputChannelEmpty();
    }

    public static TLRPC$InputChannel getInputChannel(TLRPC$InputPeer tLRPC$InputPeer) {
        TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
        tLRPC$TL_inputChannel.channel_id = tLRPC$InputPeer.channel_id;
        tLRPC$TL_inputChannel.access_hash = tLRPC$InputPeer.access_hash;
        return tLRPC$TL_inputChannel;
    }

    public TLRPC$InputChannel getInputChannel(long j) {
        return getInputChannel(getChat(Long.valueOf(j)));
    }

    public TLRPC$InputPeer getInputPeer(TLRPC$Peer tLRPC$Peer) {
        if (tLRPC$Peer instanceof TLRPC$TL_peerChat) {
            TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
            tLRPC$TL_inputPeerChat.chat_id = tLRPC$Peer.chat_id;
            return tLRPC$TL_inputPeerChat;
        } else if (tLRPC$Peer instanceof TLRPC$TL_peerChannel) {
            TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
            long j = tLRPC$Peer.channel_id;
            tLRPC$TL_inputPeerChannel.channel_id = j;
            TLRPC$Chat chat = getChat(Long.valueOf(j));
            if (chat == null) {
                return tLRPC$TL_inputPeerChannel;
            }
            tLRPC$TL_inputPeerChannel.access_hash = chat.access_hash;
            return tLRPC$TL_inputPeerChannel;
        } else {
            TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
            long j2 = tLRPC$Peer.user_id;
            tLRPC$TL_inputPeerUser.user_id = j2;
            TLRPC$User user = getUser(Long.valueOf(j2));
            if (user == null) {
                return tLRPC$TL_inputPeerUser;
            }
            tLRPC$TL_inputPeerUser.access_hash = user.access_hash;
            return tLRPC$TL_inputPeerUser;
        }
    }

    public TLRPC$InputPeer getInputPeer(long j) {
        if (j < 0) {
            long j2 = -j;
            TLRPC$Chat chat = getChat(Long.valueOf(j2));
            if (ChatObject.isChannel(chat)) {
                TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
                tLRPC$TL_inputPeerChannel.channel_id = j2;
                tLRPC$TL_inputPeerChannel.access_hash = chat.access_hash;
                return tLRPC$TL_inputPeerChannel;
            }
            TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
            tLRPC$TL_inputPeerChat.chat_id = j2;
            return tLRPC$TL_inputPeerChat;
        }
        TLRPC$User user = getUser(Long.valueOf(j));
        TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
        tLRPC$TL_inputPeerUser.user_id = j;
        if (user == null) {
            return tLRPC$TL_inputPeerUser;
        }
        tLRPC$TL_inputPeerUser.access_hash = user.access_hash;
        return tLRPC$TL_inputPeerUser;
    }

    public static TLRPC$InputPeer getInputPeer(TLRPC$Chat tLRPC$Chat) {
        if (ChatObject.isChannel(tLRPC$Chat)) {
            TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
            tLRPC$TL_inputPeerChannel.channel_id = tLRPC$Chat.id;
            tLRPC$TL_inputPeerChannel.access_hash = tLRPC$Chat.access_hash;
            return tLRPC$TL_inputPeerChannel;
        }
        TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
        tLRPC$TL_inputPeerChat.chat_id = tLRPC$Chat.id;
        return tLRPC$TL_inputPeerChat;
    }

    public static TLRPC$InputPeer getInputPeer(TLRPC$User tLRPC$User) {
        TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
        tLRPC$TL_inputPeerUser.user_id = tLRPC$User.id;
        tLRPC$TL_inputPeerUser.access_hash = tLRPC$User.access_hash;
        return tLRPC$TL_inputPeerUser;
    }

    public TLRPC$Peer getPeer(long j) {
        if (j < 0) {
            long j2 = -j;
            TLRPC$Chat chat = getChat(Long.valueOf(j2));
            if ((chat instanceof TLRPC$TL_channel) || (chat instanceof TLRPC$TL_channelForbidden)) {
                TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                tLRPC$TL_peerChannel.channel_id = j2;
                return tLRPC$TL_peerChannel;
            }
            TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
            tLRPC$TL_peerChat.chat_id = j2;
            return tLRPC$TL_peerChat;
        }
        getUser(Long.valueOf(j));
        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
        tLRPC$TL_peerUser.user_id = j;
        return tLRPC$TL_peerUser;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageObject messageObject;
        TLRPC$InputFile tLRPC$InputFile;
        TLRPC$InputFile tLRPC$InputFile2;
        final Theme.ThemeInfo themeInfo;
        final Theme.ThemeAccent themeAccent;
        TLRPC$TL_theme tLRPC$TL_theme;
        TLRPC$TL_inputThemeSettings tLRPC$TL_inputThemeSettings = null;
        if (i == NotificationCenter.fileUploaded) {
            String str = (String) objArr[0];
            TLRPC$InputFile tLRPC$InputFile3 = (TLRPC$InputFile) objArr[1];
            String str2 = this.uploadingAvatar;
            if (str2 != null && str2.equals(str)) {
                TLRPC$TL_photos_uploadProfilePhoto tLRPC$TL_photos_uploadProfilePhoto = new TLRPC$TL_photos_uploadProfilePhoto();
                tLRPC$TL_photos_uploadProfilePhoto.file = tLRPC$InputFile3;
                tLRPC$TL_photos_uploadProfilePhoto.flags |= 1;
                getConnectionsManager().sendRequest(tLRPC$TL_photos_uploadProfilePhoto, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda252
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$didReceivedNotification$27(tLObject, tLRPC$TL_error);
                    }
                });
                return;
            }
            String str3 = this.uploadingWallpaper;
            if (str3 != null && str3.equals(str)) {
                TLRPC$TL_account_uploadWallPaper tLRPC$TL_account_uploadWallPaper = new TLRPC$TL_account_uploadWallPaper();
                tLRPC$TL_account_uploadWallPaper.file = tLRPC$InputFile3;
                tLRPC$TL_account_uploadWallPaper.mime_type = "image/jpeg";
                final Theme.OverrideWallpaperInfo overrideWallpaperInfo = this.uploadingWallpaperInfo;
                final TLRPC$TL_wallPaperSettings tLRPC$TL_wallPaperSettings = new TLRPC$TL_wallPaperSettings();
                tLRPC$TL_wallPaperSettings.blur = overrideWallpaperInfo.isBlurred;
                tLRPC$TL_wallPaperSettings.motion = overrideWallpaperInfo.isMotion;
                tLRPC$TL_account_uploadWallPaper.settings = tLRPC$TL_wallPaperSettings;
                getConnectionsManager().sendRequest(tLRPC$TL_account_uploadWallPaper, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda342
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$didReceivedNotification$29(overrideWallpaperInfo, tLRPC$TL_wallPaperSettings, tLObject, tLRPC$TL_error);
                    }
                });
                return;
            }
            Object obj = this.uploadingThemes.get(str);
            if (obj instanceof Theme.ThemeInfo) {
                Theme.ThemeInfo themeInfo2 = (Theme.ThemeInfo) obj;
                if (str.equals(themeInfo2.uploadingThumb)) {
                    themeInfo2.uploadedThumb = tLRPC$InputFile3;
                    themeInfo2.uploadingThumb = null;
                } else if (str.equals(themeInfo2.uploadingFile)) {
                    themeInfo2.uploadedFile = tLRPC$InputFile3;
                    themeInfo2.uploadingFile = null;
                }
                tLRPC$InputFile = themeInfo2.uploadedThumb;
                tLRPC$InputFile2 = themeInfo2.uploadedFile;
                themeInfo = themeInfo2;
                themeAccent = null;
            } else if (obj instanceof Theme.ThemeAccent) {
                Theme.ThemeAccent themeAccent2 = (Theme.ThemeAccent) obj;
                if (str.equals(themeAccent2.uploadingThumb)) {
                    themeAccent2.uploadedThumb = tLRPC$InputFile3;
                    themeAccent2.uploadingThumb = null;
                } else if (str.equals(themeAccent2.uploadingFile)) {
                    themeAccent2.uploadedFile = tLRPC$InputFile3;
                    themeAccent2.uploadingFile = null;
                }
                themeInfo = themeAccent2.parentTheme;
                themeAccent = themeAccent2;
                tLRPC$InputFile = themeAccent2.uploadedThumb;
                tLRPC$InputFile2 = themeAccent2.uploadedFile;
            } else {
                tLRPC$InputFile = null;
                tLRPC$InputFile2 = null;
                themeInfo = null;
                themeAccent = null;
            }
            this.uploadingThemes.remove(str);
            if (tLRPC$InputFile2 == null || tLRPC$InputFile == null) {
                return;
            }
            new File(str);
            TLRPC$TL_account_uploadTheme tLRPC$TL_account_uploadTheme = new TLRPC$TL_account_uploadTheme();
            tLRPC$TL_account_uploadTheme.mime_type = "application/x-tgtheme-android";
            tLRPC$TL_account_uploadTheme.file_name = "theme.attheme";
            tLRPC$TL_account_uploadTheme.file = tLRPC$InputFile2;
            tLRPC$InputFile2.name = "theme.attheme";
            tLRPC$TL_account_uploadTheme.thumb = tLRPC$InputFile;
            tLRPC$InputFile.name = "theme-preview.jpg";
            tLRPC$TL_account_uploadTheme.flags |= 1;
            if (themeAccent != null) {
                themeAccent.uploadedFile = null;
                themeAccent.uploadedThumb = null;
                tLRPC$TL_theme = themeAccent.info;
                tLRPC$TL_inputThemeSettings = new TLRPC$TL_inputThemeSettings();
                tLRPC$TL_inputThemeSettings.base_theme = Theme.getBaseThemeByKey(themeInfo.name);
                tLRPC$TL_inputThemeSettings.accent_color = themeAccent.accentColor;
                int i3 = themeAccent.accentColor2;
                if (i3 != 0) {
                    tLRPC$TL_inputThemeSettings.flags |= 8;
                    tLRPC$TL_inputThemeSettings.outbox_accent_color = i3;
                }
                int i4 = themeAccent.myMessagesAccentColor;
                if (i4 != 0) {
                    tLRPC$TL_inputThemeSettings.message_colors.add(Integer.valueOf(i4));
                    tLRPC$TL_inputThemeSettings.flags |= 1;
                    int i5 = themeAccent.myMessagesGradientAccentColor1;
                    if (i5 != 0) {
                        tLRPC$TL_inputThemeSettings.message_colors.add(Integer.valueOf(i5));
                        int i6 = themeAccent.myMessagesGradientAccentColor2;
                        if (i6 != 0) {
                            tLRPC$TL_inputThemeSettings.message_colors.add(Integer.valueOf(i6));
                            int i7 = themeAccent.myMessagesGradientAccentColor3;
                            if (i7 != 0) {
                                tLRPC$TL_inputThemeSettings.message_colors.add(Integer.valueOf(i7));
                            }
                        }
                    }
                    tLRPC$TL_inputThemeSettings.message_colors_animated = themeAccent.myMessagesAnimated;
                }
                tLRPC$TL_inputThemeSettings.flags |= 2;
                tLRPC$TL_inputThemeSettings.wallpaper_settings = new TLRPC$TL_wallPaperSettings();
                if (!TextUtils.isEmpty(themeAccent.patternSlug)) {
                    TLRPC$TL_inputWallPaperSlug tLRPC$TL_inputWallPaperSlug = new TLRPC$TL_inputWallPaperSlug();
                    tLRPC$TL_inputWallPaperSlug.slug = themeAccent.patternSlug;
                    tLRPC$TL_inputThemeSettings.wallpaper = tLRPC$TL_inputWallPaperSlug;
                    TLRPC$WallPaperSettings tLRPC$WallPaperSettings = tLRPC$TL_inputThemeSettings.wallpaper_settings;
                    tLRPC$WallPaperSettings.intensity = (int) (themeAccent.patternIntensity * 100.0f);
                    tLRPC$WallPaperSettings.flags |= 8;
                } else {
                    TLRPC$TL_inputWallPaperNoFile tLRPC$TL_inputWallPaperNoFile = new TLRPC$TL_inputWallPaperNoFile();
                    tLRPC$TL_inputWallPaperNoFile.id = 0L;
                    tLRPC$TL_inputThemeSettings.wallpaper = tLRPC$TL_inputWallPaperNoFile;
                }
                TLRPC$WallPaperSettings tLRPC$WallPaperSettings2 = tLRPC$TL_inputThemeSettings.wallpaper_settings;
                tLRPC$WallPaperSettings2.motion = themeAccent.patternMotion;
                long j = themeAccent.backgroundOverrideColor;
                if (j != 0) {
                    tLRPC$WallPaperSettings2.background_color = (int) j;
                    tLRPC$WallPaperSettings2.flags |= 1;
                }
                long j2 = themeAccent.backgroundGradientOverrideColor1;
                if (j2 != 0) {
                    tLRPC$WallPaperSettings2.second_background_color = (int) j2;
                    tLRPC$WallPaperSettings2.flags |= 16;
                    tLRPC$WallPaperSettings2.rotation = AndroidUtilities.getWallpaperRotation(themeAccent.backgroundRotation, true);
                }
                long j3 = themeAccent.backgroundGradientOverrideColor2;
                if (j3 != 0) {
                    TLRPC$WallPaperSettings tLRPC$WallPaperSettings3 = tLRPC$TL_inputThemeSettings.wallpaper_settings;
                    tLRPC$WallPaperSettings3.third_background_color = (int) j3;
                    tLRPC$WallPaperSettings3.flags |= 32;
                }
                long j4 = themeAccent.backgroundGradientOverrideColor3;
                if (j4 != 0) {
                    TLRPC$WallPaperSettings tLRPC$WallPaperSettings4 = tLRPC$TL_inputThemeSettings.wallpaper_settings;
                    tLRPC$WallPaperSettings4.fourth_background_color = (int) j4;
                    tLRPC$WallPaperSettings4.flags |= 64;
                }
            } else {
                themeInfo.uploadedFile = null;
                themeInfo.uploadedThumb = null;
                tLRPC$TL_theme = themeInfo.info;
            }
            final TLRPC$TL_theme tLRPC$TL_theme2 = tLRPC$TL_theme;
            final TLRPC$TL_inputThemeSettings tLRPC$TL_inputThemeSettings2 = tLRPC$TL_inputThemeSettings;
            getConnectionsManager().sendRequest(tLRPC$TL_account_uploadTheme, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda336
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$didReceivedNotification$35(tLRPC$TL_theme2, themeInfo, tLRPC$TL_inputThemeSettings2, themeAccent, tLObject, tLRPC$TL_error);
                }
            });
        } else if (i == NotificationCenter.fileUploadFailed) {
            String str4 = (String) objArr[0];
            String str5 = this.uploadingAvatar;
            if (str5 != null && str5.equals(str4)) {
                this.uploadingAvatar = null;
                return;
            }
            String str6 = this.uploadingWallpaper;
            if (str6 != null && str6.equals(str4)) {
                this.uploadingWallpaper = null;
                this.uploadingWallpaperInfo = null;
                return;
            }
            Object remove = this.uploadingThemes.remove(str4);
            if (remove instanceof Theme.ThemeInfo) {
                Theme.ThemeInfo themeInfo3 = (Theme.ThemeInfo) remove;
                themeInfo3.uploadedFile = null;
                themeInfo3.uploadedThumb = null;
                getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo3, 0);
            } else if (remove instanceof Theme.ThemeAccent) {
                Theme.ThemeAccent themeAccent3 = (Theme.ThemeAccent) remove;
                themeAccent3.uploadingThumb = null;
                getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeAccent3.parentTheme, themeAccent3);
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            if (((Boolean) objArr[6]).booleanValue()) {
                return;
            }
            Integer num = (Integer) objArr[0];
            Integer num2 = (Integer) objArr[1];
            Long l = (Long) objArr[3];
            ArrayList<MessageObject> arrayList = this.dialogMessage.get(l.longValue());
            for (int i8 = 0; arrayList != null && i8 < arrayList.size(); i8++) {
                MessageObject messageObject2 = arrayList.get(i8);
                if (messageObject2 != null && (messageObject2.getId() == num.intValue() || messageObject2.messageOwner.local_id == num.intValue())) {
                    messageObject2.messageOwner.id = num2.intValue();
                    messageObject2.messageOwner.send_state = 0;
                }
                MessageObject messageObject3 = this.dialogMessagesByIds.get(num.intValue());
                if (messageObject3 != null) {
                    this.dialogMessagesByIds.remove(num.intValue());
                    this.dialogMessagesByIds.put(num2.intValue(), messageObject3);
                }
            }
            TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(l.longValue());
            if (tLRPC$Dialog != null && tLRPC$Dialog.top_message == num.intValue()) {
                tLRPC$Dialog.top_message = num2.intValue();
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            if (!DialogObject.isChatDialog(l.longValue())) {
                return;
            }
            TLRPC$ChatFull tLRPC$ChatFull = this.fullChats.get(-l.longValue());
            TLRPC$Chat chat = getChat(Long.valueOf(-l.longValue()));
            if (chat == null || ChatObject.hasAdminRights(chat) || tLRPC$ChatFull == null || tLRPC$ChatFull.slowmode_seconds == 0) {
                return;
            }
            tLRPC$ChatFull.slowmode_next_send_date = getConnectionsManager().getCurrentTime() + tLRPC$ChatFull.slowmode_seconds;
            tLRPC$ChatFull.flags |= 262144;
            getMessagesStorage().updateChatInfo(tLRPC$ChatFull, false);
        } else if (i == NotificationCenter.updateMessageMedia) {
            TLRPC$Message tLRPC$Message = (TLRPC$Message) objArr[0];
            if (tLRPC$Message.peer_id.channel_id != 0 || (messageObject = this.dialogMessagesByIds.get(tLRPC$Message.id)) == null) {
                return;
            }
            messageObject.messageOwner.media = MessageObject.getMedia(tLRPC$Message);
            if (MessageObject.getMedia(tLRPC$Message).ttl_seconds == 0) {
                return;
            }
            if (!(MessageObject.getMedia(tLRPC$Message).photo instanceof TLRPC$TL_photoEmpty) && !(MessageObject.getMedia(tLRPC$Message).document instanceof TLRPC$TL_documentEmpty)) {
                return;
            }
            messageObject.setType();
            getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$27(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$User user = getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                putUser(user, true);
            } else {
                getUserConfig().setCurrentUser(user);
            }
            if (user == null) {
                return;
            }
            TLRPC$TL_photos_photo tLRPC$TL_photos_photo = (TLRPC$TL_photos_photo) tLObject;
            ArrayList<TLRPC$PhotoSize> arrayList = tLRPC$TL_photos_photo.photo.sizes;
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 100);
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 1000);
            TLRPC$TL_userProfilePhoto tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto();
            user.photo = tLRPC$TL_userProfilePhoto;
            tLRPC$TL_userProfilePhoto.photo_id = tLRPC$TL_photos_photo.photo.id;
            if (closestPhotoSizeWithSize != null) {
                tLRPC$TL_userProfilePhoto.photo_small = closestPhotoSizeWithSize.location;
            }
            if (closestPhotoSizeWithSize2 != null) {
                tLRPC$TL_userProfilePhoto.photo_big = closestPhotoSizeWithSize2.location;
            }
            getMessagesStorage().clearUserPhotos(user.id);
            ArrayList<TLRPC$User> arrayList2 = new ArrayList<>();
            arrayList2.add(user);
            getMessagesStorage().putUsersAndChats(arrayList2, null, false, true);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$didReceivedNotification$26();
                }
            });
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$26() {
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
        getUserConfig().saveConfig(true);
    }

    public /* synthetic */ void lambda$didReceivedNotification$29(final Theme.OverrideWallpaperInfo overrideWallpaperInfo, final TLRPC$TL_wallPaperSettings tLRPC$TL_wallPaperSettings, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        final TLRPC$WallPaper tLRPC$WallPaper = (TLRPC$WallPaper) tLObject;
        final File file = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.originalFileName);
        if (tLRPC$WallPaper != null) {
            try {
                AndroidUtilities.copyFile(file, getFileLoader().getPathToAttach(tLRPC$WallPaper.document, true));
            } catch (Exception unused) {
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda193
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$didReceivedNotification$28(tLRPC$WallPaper, tLRPC$TL_wallPaperSettings, overrideWallpaperInfo, file);
            }
        });
    }

    public /* synthetic */ void lambda$didReceivedNotification$28(TLRPC$WallPaper tLRPC$WallPaper, TLRPC$TL_wallPaperSettings tLRPC$TL_wallPaperSettings, Theme.OverrideWallpaperInfo overrideWallpaperInfo, File file) {
        if (this.uploadingWallpaper == null || tLRPC$WallPaper == null) {
            return;
        }
        tLRPC$WallPaper.settings = tLRPC$TL_wallPaperSettings;
        tLRPC$WallPaper.flags |= 4;
        overrideWallpaperInfo.slug = tLRPC$WallPaper.slug;
        overrideWallpaperInfo.saveOverrideWallpaper();
        ArrayList<TLRPC$WallPaper> arrayList = new ArrayList<>();
        arrayList.add(tLRPC$WallPaper);
        getMessagesStorage().putWallpapers(arrayList, 2);
        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$WallPaper.document.thumbs, 320);
        if (closestPhotoSizeWithSize != null) {
            ImageLoader.getInstance().replaceImageInCache(Utilities.MD5(file.getAbsolutePath()) + "@100_100", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$WallPaper.document), false);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersNeedReload, tLRPC$WallPaper.slug);
    }

    public /* synthetic */ void lambda$didReceivedNotification$35(TLRPC$TL_theme tLRPC$TL_theme, final Theme.ThemeInfo themeInfo, TLRPC$TL_inputThemeSettings tLRPC$TL_inputThemeSettings, final Theme.ThemeAccent themeAccent, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        String name = tLRPC$TL_theme != null ? tLRPC$TL_theme.title : themeInfo.getName();
        int lastIndexOf = name.lastIndexOf(".attheme");
        if (lastIndexOf > 0) {
            name = name.substring(0, lastIndexOf);
        }
        if (tLObject != null) {
            TLRPC$Document tLRPC$Document = (TLRPC$Document) tLObject;
            TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
            tLRPC$TL_inputDocument.access_hash = tLRPC$Document.access_hash;
            tLRPC$TL_inputDocument.id = tLRPC$Document.id;
            tLRPC$TL_inputDocument.file_reference = tLRPC$Document.file_reference;
            if (tLRPC$TL_theme == null || !tLRPC$TL_theme.creator) {
                TLRPC$TL_account_createTheme tLRPC$TL_account_createTheme = new TLRPC$TL_account_createTheme();
                tLRPC$TL_account_createTheme.document = tLRPC$TL_inputDocument;
                tLRPC$TL_account_createTheme.flags |= 4;
                tLRPC$TL_account_createTheme.slug = (tLRPC$TL_theme == null || TextUtils.isEmpty(tLRPC$TL_theme.slug)) ? "" : tLRPC$TL_theme.slug;
                tLRPC$TL_account_createTheme.title = name;
                if (tLRPC$TL_inputThemeSettings != null) {
                    tLRPC$TL_account_createTheme.settings = tLRPC$TL_inputThemeSettings;
                    tLRPC$TL_account_createTheme.flags |= 8;
                }
                getConnectionsManager().sendRequest(tLRPC$TL_account_createTheme, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda343
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error2) {
                        MessagesController.this.lambda$didReceivedNotification$31(themeInfo, themeAccent, tLObject2, tLRPC$TL_error2);
                    }
                });
                return;
            }
            TLRPC$TL_account_updateTheme tLRPC$TL_account_updateTheme = new TLRPC$TL_account_updateTheme();
            TLRPC$TL_inputTheme tLRPC$TL_inputTheme = new TLRPC$TL_inputTheme();
            tLRPC$TL_inputTheme.id = tLRPC$TL_theme.id;
            tLRPC$TL_inputTheme.access_hash = tLRPC$TL_theme.access_hash;
            tLRPC$TL_account_updateTheme.theme = tLRPC$TL_inputTheme;
            tLRPC$TL_account_updateTheme.slug = tLRPC$TL_theme.slug;
            int i = tLRPC$TL_account_updateTheme.flags | 1;
            tLRPC$TL_account_updateTheme.flags = i;
            tLRPC$TL_account_updateTheme.title = name;
            int i2 = i | 2;
            tLRPC$TL_account_updateTheme.flags = i2;
            tLRPC$TL_account_updateTheme.document = tLRPC$TL_inputDocument;
            int i3 = i2 | 4;
            tLRPC$TL_account_updateTheme.flags = i3;
            if (tLRPC$TL_inputThemeSettings != null) {
                tLRPC$TL_account_updateTheme.settings = tLRPC$TL_inputThemeSettings;
                tLRPC$TL_account_updateTheme.flags = i3 | 8;
            }
            tLRPC$TL_account_updateTheme.format = "android";
            getConnectionsManager().sendRequest(tLRPC$TL_account_updateTheme, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda344
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error2) {
                    MessagesController.this.lambda$didReceivedNotification$33(themeInfo, themeAccent, tLObject2, tLRPC$TL_error2);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda210
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$didReceivedNotification$34(themeInfo, themeAccent);
            }
        });
    }

    public /* synthetic */ void lambda$didReceivedNotification$31(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent themeAccent, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda147
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$didReceivedNotification$30(tLObject, themeInfo, themeAccent);
            }
        });
    }

    public /* synthetic */ void lambda$didReceivedNotification$30(TLObject tLObject, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        if (tLObject instanceof TLRPC$TL_theme) {
            Theme.setThemeUploadInfo(themeInfo, themeAccent, (TLRPC$TL_theme) tLObject, this.currentAccount, false);
            installTheme(themeInfo, themeAccent, themeInfo == Theme.getCurrentNightTheme());
            getNotificationCenter().postNotificationName(NotificationCenter.themeUploadedToServer, themeInfo, themeAccent);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, themeAccent);
    }

    public /* synthetic */ void lambda$didReceivedNotification$33(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent themeAccent, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda148
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$didReceivedNotification$32(tLObject, themeInfo, themeAccent);
            }
        });
    }

    public /* synthetic */ void lambda$didReceivedNotification$32(TLObject tLObject, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        if (tLObject instanceof TLRPC$TL_theme) {
            Theme.setThemeUploadInfo(themeInfo, themeAccent, (TLRPC$TL_theme) tLObject, this.currentAccount, false);
            getNotificationCenter().postNotificationName(NotificationCenter.themeUploadedToServer, themeInfo, themeAccent);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, themeAccent);
    }

    public /* synthetic */ void lambda$didReceivedNotification$34(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, themeAccent);
    }

    public void cleanup() {
        getContactsController().cleanup();
        MediaController.getInstance().cleanup();
        getNotificationsController().cleanup();
        getSendMessagesHelper().cleanup();
        getSecretChatHelper().cleanup();
        getLocationController().cleanup();
        getMediaDataController().cleanup();
        this.showFiltersTooltip = false;
        DialogsActivity.dialogsLoaded[this.currentAccount] = false;
        this.notificationsPreferences.edit().clear().commit();
        this.emojiPreferences.edit().putLong("lastGifLoadTime", 0L).putLong("lastStickersLoadTime", 0L).putLong("lastStickersLoadTimeMask", 0L).putLong("lastStickersLoadTimeFavs", 0L).commit();
        this.mainPreferences.edit().remove("archivehint").remove("proximityhint").remove("archivehint_l").remove("gifhint").remove("reminderhint").remove("soundHint").remove("dcDomainName2").remove("webFileDatacenterId").remove("themehint").remove("showFiltersTooltip").commit();
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("shortcut_widget", 0);
        SharedPreferences.Editor editor = null;
        ArrayList arrayList = null;
        ArrayList arrayList2 = null;
        AppWidgetManager appWidgetManager = null;
        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("account") && ((Integer) entry.getValue()).intValue() == this.currentAccount) {
                int intValue = Utilities.parseInt((CharSequence) key).intValue();
                if (editor == null) {
                    editor = sharedPreferences.edit();
                    appWidgetManager = AppWidgetManager.getInstance(ApplicationLoader.applicationContext);
                }
                editor.putBoolean("deleted" + intValue, true);
                if (sharedPreferences.getInt("type" + intValue, 0) == 0) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(intValue));
                } else {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(Integer.valueOf(intValue));
                }
            }
        }
        if (editor != null) {
            editor.commit();
        }
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ChatsWidgetProvider.updateWidget(ApplicationLoader.applicationContext, appWidgetManager, ((Integer) arrayList.get(i)).intValue());
            }
        }
        if (arrayList2 != null) {
            int size2 = arrayList2.size();
            for (int i2 = 0; i2 < size2; i2++) {
                ContactsWidgetProvider.updateWidget(ApplicationLoader.applicationContext, appWidgetManager, ((Integer) arrayList2.get(i2)).intValue());
            }
        }
        this.lastScheduledServerQueryTime.clear();
        this.lastServerQueryTime.clear();
        this.reloadingWebpages.clear();
        this.reloadingWebpagesPending.clear();
        this.reloadingScheduledWebpages.clear();
        this.reloadingScheduledWebpagesPending.clear();
        this.sponsoredMessages.clear();
        this.sendAsPeers.clear();
        this.dialogs_dict.clear();
        this.dialogs_read_inbox_max.clear();
        this.loadingPinnedDialogs.clear();
        this.dialogs_read_outbox_max.clear();
        this.exportedChats.clear();
        this.fullUsers.clear();
        this.fullChats.clear();
        this.activeVoiceChatsMap.clear();
        this.loadingGroupCalls.clear();
        this.groupCallsByChatId.clear();
        this.dialogsByFolder.clear();
        this.unreadUnmutedDialogs = 0;
        this.joiningToChannels.clear();
        this.migratedChats.clear();
        this.channelViewsToSend.clear();
        this.pollsToCheck.clear();
        this.pollsToCheckSize = 0;
        this.dialogsServerOnly.clear();
        this.dialogsForward.clear();
        this.allDialogs.clear();
        this.dialogsLoadedTillDate = Integer.MAX_VALUE;
        this.dialogsCanAddUsers.clear();
        this.dialogsMyChannels.clear();
        this.dialogsMyGroups.clear();
        this.dialogsChannelsOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsUsersOnly.clear();
        this.dialogsForBlock.clear();
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
        DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
        dialogFilterArr[1] = null;
        dialogFilterArr[0] = null;
        this.dialogFilters.clear();
        this.dialogFiltersById.clear();
        this.loadingSuggestedFilters = false;
        this.loadingRemoteFilters = false;
        this.suggestedFilters.clear();
        this.gettingAppChangelog = false;
        this.dialogFiltersLoaded = false;
        this.ignoreSetOnline = false;
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$cleanup$36();
            }
        });
        this.createdDialogMainThreadIds.clear();
        this.visibleDialogMainThreadIds.clear();
        this.visibleScheduledDialogMainThreadIds.clear();
        this.blockePeers.clear();
        int i3 = 0;
        while (true) {
            LongSparseArray<SparseArray<Boolean>>[] longSparseArrayArr = this.sendingTypings;
            if (i3 >= longSparseArrayArr.length) {
                break;
            }
            if (longSparseArrayArr[i3] != null) {
                longSparseArrayArr[i3].clear();
            }
            i3++;
        }
        this.loadingFullUsers.clear();
        this.loadedFullUsers.clear();
        this.reloadingMessages.clear();
        this.loadingFullChats.clear();
        this.loadingFullParticipants.clear();
        this.loadedFullParticipants.clear();
        this.loadedFullChats.clear();
        this.dialogsLoaded = false;
        this.nextDialogsCacheOffset.clear();
        this.loadingDialogs.clear();
        this.dialogsEndReached.clear();
        this.serverDialogsEndReached.clear();
        this.loadingAppConfig = false;
        this.checkingTosUpdate = false;
        this.nextTosCheckTime = 0;
        this.nextPromoInfoCheckTime = 0;
        this.checkingPromoInfo = false;
        this.loadingUnreadDialogs = false;
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskMediaMids = null;
        this.gettingNewDeleteTask = false;
        this.loadingBlockedPeers = false;
        this.totalBlockedCount = -1;
        this.blockedEndReached = false;
        this.firstGettingTask = false;
        this.updatingState = false;
        this.resetingDialogs = false;
        this.lastStatusUpdateTime = 0L;
        this.offlineSent = false;
        this.registeringForPush = false;
        this.getDifferenceFirstSync = true;
        this.uploadingAvatar = null;
        this.uploadingWallpaper = null;
        this.uploadingWallpaperInfo = null;
        this.uploadingThemes.clear();
        this.gettingChatInviters.clear();
        this.statusRequest = 0;
        this.statusSettingState = 0;
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$cleanup$37();
            }
        });
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        addSupportUser();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$cleanup$38();
            }
        });
    }

    public /* synthetic */ void lambda$cleanup$36() {
        this.readTasks.clear();
        this.readTasksMap.clear();
        this.repliesReadTasks.clear();
        this.threadsReadTasksMap.clear();
        this.updatesQueueSeq.clear();
        this.updatesQueuePts.clear();
        this.updatesQueueQts.clear();
        this.gettingUnknownChannels.clear();
        this.gettingUnknownDialogs.clear();
        this.updatesStartWaitTimeSeq = 0L;
        this.updatesStartWaitTimePts = 0L;
        this.updatesStartWaitTimeQts = 0L;
        this.createdDialogIds.clear();
        this.createdScheduledDialogIds.clear();
        this.gettingDifference = false;
        this.resetDialogsPinned = null;
        this.resetDialogsAll = null;
    }

    public /* synthetic */ void lambda$cleanup$37() {
        getConnectionsManager().setIsUpdating(false);
        this.updatesQueueChannels.clear();
        this.updatesStartWaitTimeChannels.clear();
        this.gettingDifferenceChannels.clear();
        this.channelsPts.clear();
        this.shortPollChannels.clear();
        this.needShortPollChannels.clear();
        this.shortPollOnlines.clear();
        this.needShortPollOnlines.clear();
    }

    public /* synthetic */ void lambda$cleanup$38() {
        getNotificationCenter().postNotificationName(NotificationCenter.suggestedFiltersLoaded, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public boolean isChatNoForwards(TLRPC$Chat tLRPC$Chat) {
        TLRPC$Chat chat;
        if (tLRPC$Chat == null) {
            return false;
        }
        TLRPC$InputChannel tLRPC$InputChannel = tLRPC$Chat.migrated_to;
        if (tLRPC$InputChannel != null && (chat = getChat(Long.valueOf(tLRPC$InputChannel.channel_id))) != null) {
            return chat.noforwards;
        }
        return tLRPC$Chat.noforwards;
    }

    public boolean isChatNoForwards(long j) {
        return isChatNoForwards(getChat(Long.valueOf(j)));
    }

    public TLRPC$User getUser(Long l) {
        if (l.longValue() == 0) {
            return UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        return this.users.get(l);
    }

    public TLObject getUserOrChat(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        return this.objectsByUsernames.get(str.toLowerCase());
    }

    public ConcurrentHashMap<Long, TLRPC$User> getUsers() {
        return this.users;
    }

    public ConcurrentHashMap<Long, TLRPC$Chat> getChats() {
        return this.chats;
    }

    public TLRPC$Chat getChat(Long l) {
        return this.chats.get(l);
    }

    public TLRPC$EncryptedChat getEncryptedChat(Integer num) {
        return this.encryptedChats.get(num);
    }

    public TLRPC$EncryptedChat getEncryptedChatDB(int i, boolean z) {
        TLRPC$EncryptedChat tLRPC$EncryptedChat = this.encryptedChats.get(Integer.valueOf(i));
        if (tLRPC$EncryptedChat != null) {
            if (!z) {
                return tLRPC$EncryptedChat;
            }
            if (!(tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChatWaiting) && !(tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChatRequested)) {
                return tLRPC$EncryptedChat;
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ArrayList<TLObject> arrayList = new ArrayList<>();
        getMessagesStorage().getEncryptedChat(i, countDownLatch, arrayList);
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (arrayList.size() == 2) {
            TLRPC$EncryptedChat tLRPC$EncryptedChat2 = (TLRPC$EncryptedChat) arrayList.get(0);
            putEncryptedChat(tLRPC$EncryptedChat2, false);
            putUser((TLRPC$User) arrayList.get(1), true);
            return tLRPC$EncryptedChat2;
        }
        return tLRPC$EncryptedChat;
    }

    public boolean isDialogVisible(long j, boolean z) {
        return (z ? this.visibleScheduledDialogMainThreadIds : this.visibleDialogMainThreadIds).contains(Long.valueOf(j));
    }

    public void setLastVisibleDialogId(long j, boolean z, boolean z2) {
        ArrayList<Long> arrayList = z ? this.visibleScheduledDialogMainThreadIds : this.visibleDialogMainThreadIds;
        if (z2) {
            if (arrayList.contains(Long.valueOf(j))) {
                return;
            }
            arrayList.add(Long.valueOf(j));
            return;
        }
        arrayList.remove(Long.valueOf(j));
    }

    public void setLastCreatedDialogId(final long j, final boolean z, final boolean z2) {
        if (!z) {
            ArrayList<Long> arrayList = this.createdDialogMainThreadIds;
            if (z2) {
                if (arrayList.contains(Long.valueOf(j))) {
                    return;
                }
                arrayList.add(Long.valueOf(j));
            } else {
                arrayList.remove(Long.valueOf(j));
                SparseArray<MessageObject> sparseArray = this.pollsToCheck.get(j);
                if (sparseArray != null) {
                    int size = sparseArray.size();
                    for (int i = 0; i < size; i++) {
                        sparseArray.valueAt(i).pollVisibleOnScreen = false;
                    }
                }
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda217
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$setLastCreatedDialogId$39(z, z2, j);
            }
        });
    }

    public /* synthetic */ void lambda$setLastCreatedDialogId$39(boolean z, boolean z2, long j) {
        ArrayList<Long> arrayList = z ? this.createdScheduledDialogIds : this.createdDialogIds;
        if (z2) {
            if (arrayList.contains(Long.valueOf(j))) {
                return;
            }
            arrayList.add(Long.valueOf(j));
            return;
        }
        arrayList.remove(Long.valueOf(j));
    }

    public TLRPC$TL_chatInviteExported getExportedInvite(long j) {
        return this.exportedChats.get(j);
    }

    public boolean putUser(TLRPC$User tLRPC$User, boolean z) {
        return putUser(tLRPC$User, z, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:132:0x0054  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x0076  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x0085  */
    /* JADX WARN: Removed duplicated region for block: B:156:0x00b8  */
    /* JADX WARN: Removed duplicated region for block: B:171:0x00fe  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean putUser(org.telegram.tgnet.TLRPC$User r9, boolean r10, boolean r11) {
        /*
            Method dump skipped, instructions count: 392
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.putUser(org.telegram.tgnet.TLRPC$User, boolean, boolean):boolean");
    }

    public void putUsers(ArrayList<TLRPC$User> arrayList, boolean z) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        int size = arrayList.size();
        boolean z2 = false;
        for (int i = 0; i < size; i++) {
            if (putUser(arrayList.get(i), z)) {
                z2 = true;
            }
        }
        if (!z2) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$putUsers$40();
            }
        });
    }

    public /* synthetic */ void lambda$putUsers$40() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_STATUS));
    }

    public void putChat(final TLRPC$Chat tLRPC$Chat, boolean z) {
        TLRPC$Chat tLRPC$Chat2;
        if (tLRPC$Chat == null || (tLRPC$Chat2 = this.chats.get(Long.valueOf(tLRPC$Chat.id))) == tLRPC$Chat) {
            return;
        }
        if (tLRPC$Chat2 != null && !TextUtils.isEmpty(tLRPC$Chat2.username)) {
            this.objectsByUsernames.remove(tLRPC$Chat2.username.toLowerCase());
        }
        if (tLRPC$Chat2 != null && tLRPC$Chat2.usernames != null) {
            for (int i = 0; i < tLRPC$Chat2.usernames.size(); i++) {
                TLRPC$TL_username tLRPC$TL_username = tLRPC$Chat2.usernames.get(i);
                if (tLRPC$TL_username != null && !TextUtils.isEmpty(tLRPC$TL_username.username)) {
                    this.objectsByUsernames.remove(tLRPC$TL_username.username.toLowerCase());
                }
            }
        }
        if (!TextUtils.isEmpty(tLRPC$Chat.username)) {
            this.objectsByUsernames.put(tLRPC$Chat.username.toLowerCase(), tLRPC$Chat);
        }
        if (tLRPC$Chat.usernames != null) {
            for (int i2 = 0; i2 < tLRPC$Chat.usernames.size(); i2++) {
                TLRPC$TL_username tLRPC$TL_username2 = tLRPC$Chat.usernames.get(i2);
                if (tLRPC$TL_username2 != null && !TextUtils.isEmpty(tLRPC$TL_username2.username) && tLRPC$TL_username2.active) {
                    this.objectsByUsernames.put(tLRPC$TL_username2.username.toLowerCase(), tLRPC$Chat);
                }
            }
        }
        if (!tLRPC$Chat.min) {
            if (!z) {
                if (tLRPC$Chat2 != null) {
                    if (tLRPC$Chat.version != tLRPC$Chat2.version) {
                        this.loadedFullChats.remove(Long.valueOf(tLRPC$Chat.id));
                    }
                    int i3 = tLRPC$Chat2.participants_count;
                    if (i3 != 0 && tLRPC$Chat.participants_count == 0) {
                        tLRPC$Chat.participants_count = i3;
                        tLRPC$Chat.flags |= 131072;
                    }
                    TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat2.banned_rights;
                    int i4 = tLRPC$TL_chatBannedRights != null ? tLRPC$TL_chatBannedRights.flags : 0;
                    TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2 = tLRPC$Chat.banned_rights;
                    int i5 = tLRPC$TL_chatBannedRights2 != null ? tLRPC$TL_chatBannedRights2.flags : 0;
                    TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights3 = tLRPC$Chat2.default_banned_rights;
                    int i6 = tLRPC$TL_chatBannedRights3 != null ? tLRPC$TL_chatBannedRights3.flags : 0;
                    TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights4 = tLRPC$Chat.default_banned_rights;
                    int i7 = tLRPC$TL_chatBannedRights4 != null ? tLRPC$TL_chatBannedRights4.flags : 0;
                    tLRPC$Chat2.default_banned_rights = tLRPC$TL_chatBannedRights4;
                    if (tLRPC$TL_chatBannedRights4 == null) {
                        tLRPC$Chat2.flags &= -262145;
                    } else {
                        tLRPC$Chat2.flags = 262144 | tLRPC$Chat2.flags;
                    }
                    tLRPC$Chat2.banned_rights = tLRPC$TL_chatBannedRights2;
                    if (tLRPC$TL_chatBannedRights2 == null) {
                        tLRPC$Chat2.flags &= -32769;
                    } else {
                        tLRPC$Chat2.flags |= 32768;
                    }
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights;
                    tLRPC$Chat2.admin_rights = tLRPC$TL_chatAdminRights;
                    if (tLRPC$TL_chatAdminRights == null) {
                        tLRPC$Chat2.flags &= -16385;
                    } else {
                        tLRPC$Chat2.flags |= 16384;
                    }
                    if (i4 != i5 || i6 != i7) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda150
                            @Override // java.lang.Runnable
                            public final void run() {
                                MessagesController.this.lambda$putChat$41(tLRPC$Chat);
                            }
                        });
                    }
                }
                this.chats.put(Long.valueOf(tLRPC$Chat.id), tLRPC$Chat);
            } else if (tLRPC$Chat2 == null) {
                this.chats.put(Long.valueOf(tLRPC$Chat.id), tLRPC$Chat);
            } else if (tLRPC$Chat2.min) {
                tLRPC$Chat.title = tLRPC$Chat2.title;
                tLRPC$Chat.photo = tLRPC$Chat2.photo;
                tLRPC$Chat.broadcast = tLRPC$Chat2.broadcast;
                tLRPC$Chat.verified = tLRPC$Chat2.verified;
                tLRPC$Chat.megagroup = tLRPC$Chat2.megagroup;
                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights5 = tLRPC$Chat2.default_banned_rights;
                if (tLRPC$TL_chatBannedRights5 != null) {
                    tLRPC$Chat.default_banned_rights = tLRPC$TL_chatBannedRights5;
                    tLRPC$Chat.flags |= 262144;
                }
                TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2 = tLRPC$Chat2.admin_rights;
                if (tLRPC$TL_chatAdminRights2 != null) {
                    tLRPC$Chat.admin_rights = tLRPC$TL_chatAdminRights2;
                    tLRPC$Chat.flags |= 16384;
                }
                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights6 = tLRPC$Chat2.banned_rights;
                if (tLRPC$TL_chatBannedRights6 != null) {
                    tLRPC$Chat.banned_rights = tLRPC$TL_chatBannedRights6;
                    tLRPC$Chat.flags |= 32768;
                }
                String str = tLRPC$Chat2.username;
                if (str != null) {
                    tLRPC$Chat.username = str;
                    tLRPC$Chat.flags |= 64;
                } else {
                    tLRPC$Chat.flags &= -65;
                    tLRPC$Chat.username = null;
                }
                int i8 = tLRPC$Chat2.participants_count;
                if (i8 != 0 && tLRPC$Chat.participants_count == 0) {
                    tLRPC$Chat.participants_count = i8;
                    tLRPC$Chat.flags |= 131072;
                }
                this.chats.put(Long.valueOf(tLRPC$Chat.id), tLRPC$Chat);
            }
            addOrRemoveActiveVoiceChat(tLRPC$Chat);
        } else if (tLRPC$Chat2 == null) {
            this.chats.put(Long.valueOf(tLRPC$Chat.id), tLRPC$Chat);
            addOrRemoveActiveVoiceChat(tLRPC$Chat);
        } else if (!z) {
            tLRPC$Chat2.title = tLRPC$Chat.title;
            tLRPC$Chat2.photo = tLRPC$Chat.photo;
            tLRPC$Chat2.broadcast = tLRPC$Chat.broadcast;
            tLRPC$Chat2.verified = tLRPC$Chat.verified;
            tLRPC$Chat2.megagroup = tLRPC$Chat.megagroup;
            tLRPC$Chat2.call_not_empty = tLRPC$Chat.call_not_empty;
            tLRPC$Chat2.call_active = tLRPC$Chat.call_active;
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights7 = tLRPC$Chat.default_banned_rights;
            if (tLRPC$TL_chatBannedRights7 != null) {
                tLRPC$Chat2.default_banned_rights = tLRPC$TL_chatBannedRights7;
                tLRPC$Chat2.flags |= 262144;
            }
            TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights3 = tLRPC$Chat.admin_rights;
            if (tLRPC$TL_chatAdminRights3 != null) {
                tLRPC$Chat2.admin_rights = tLRPC$TL_chatAdminRights3;
                tLRPC$Chat2.flags |= 16384;
            }
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights8 = tLRPC$Chat.banned_rights;
            if (tLRPC$TL_chatBannedRights8 != null) {
                tLRPC$Chat2.banned_rights = tLRPC$TL_chatBannedRights8;
                tLRPC$Chat2.flags |= 32768;
            }
            String str2 = tLRPC$Chat.username;
            if (str2 != null) {
                tLRPC$Chat2.username = str2;
                tLRPC$Chat2.flags |= 64;
            } else {
                tLRPC$Chat2.flags &= -65;
                tLRPC$Chat2.username = null;
            }
            int i9 = tLRPC$Chat.participants_count;
            if (i9 != 0) {
                tLRPC$Chat2.participants_count = i9;
            }
            addOrRemoveActiveVoiceChat(tLRPC$Chat2);
            boolean z2 = tLRPC$Chat2.forum;
            boolean z3 = tLRPC$Chat.forum;
            if (z2 != z3) {
                tLRPC$Chat2.forum = z3;
                if (z3) {
                    tLRPC$Chat2.flags |= NUM;
                } else {
                    tLRPC$Chat2.flags &= -NUM;
                }
                getNotificationCenter().postNotificationName(NotificationCenter.chatSwithcedToForum, Long.valueOf(tLRPC$Chat.id));
            }
        }
        if (tLRPC$Chat2 == null || tLRPC$Chat2.forum == tLRPC$Chat.forum) {
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.chatSwithcedToForum, Long.valueOf(tLRPC$Chat.id));
    }

    public /* synthetic */ void lambda$putChat$41(TLRPC$Chat tLRPC$Chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.channelRightsUpdated, tLRPC$Chat);
    }

    public void putChats(ArrayList<TLRPC$Chat> arrayList, boolean z) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            putChat(arrayList.get(i), z);
        }
    }

    private void addOrRemoveActiveVoiceChat(final TLRPC$Chat tLRPC$Chat) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda153
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$addOrRemoveActiveVoiceChat$42(tLRPC$Chat);
                }
            });
        } else {
            lambda$addOrRemoveActiveVoiceChat$42(tLRPC$Chat);
        }
    }

    /* renamed from: addOrRemoveActiveVoiceChatInternal */
    public void lambda$addOrRemoveActiveVoiceChat$42(TLRPC$Chat tLRPC$Chat) {
        TLRPC$Chat tLRPC$Chat2 = this.activeVoiceChatsMap.get(Long.valueOf(tLRPC$Chat.id));
        if (!tLRPC$Chat.call_active || !tLRPC$Chat.call_not_empty || tLRPC$Chat.migrated_to != null || ChatObject.isNotInChat(tLRPC$Chat)) {
            if (tLRPC$Chat2 == null) {
                return;
            }
            this.activeVoiceChatsMap.remove(Long.valueOf(tLRPC$Chat.id));
            getNotificationCenter().postNotificationName(NotificationCenter.activeGroupCallsUpdated, new Object[0]);
        } else if (tLRPC$Chat2 != null) {
        } else {
            this.activeVoiceChatsMap.put(Long.valueOf(tLRPC$Chat.id), tLRPC$Chat);
            getNotificationCenter().postNotificationName(NotificationCenter.activeGroupCallsUpdated, new Object[0]);
        }
    }

    public ArrayList<Long> getActiveGroupCalls() {
        return new ArrayList<>(this.activeVoiceChatsMap.keySet());
    }

    public void setReferer(String str) {
        if (str == null) {
            return;
        }
        this.installReferer = str;
        this.mainPreferences.edit().putString("installReferer", str).commit();
    }

    public void putEncryptedChat(TLRPC$EncryptedChat tLRPC$EncryptedChat, boolean z) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        if (z) {
            this.encryptedChats.putIfAbsent(Integer.valueOf(tLRPC$EncryptedChat.id), tLRPC$EncryptedChat);
        } else {
            this.encryptedChats.put(Integer.valueOf(tLRPC$EncryptedChat.id), tLRPC$EncryptedChat);
        }
    }

    public void putEncryptedChats(ArrayList<TLRPC$EncryptedChat> arrayList, boolean z) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            putEncryptedChat(arrayList.get(i), z);
        }
    }

    public TLRPC$UserFull getUserFull(long j) {
        return this.fullUsers.get(j);
    }

    public TLRPC$ChatFull getChatFull(long j) {
        return this.fullChats.get(j);
    }

    public void putGroupCall(long j, ChatObject.Call call) {
        this.groupCalls.put(call.call.id, call);
        this.groupCallsByChatId.put(j, call);
        TLRPC$ChatFull chatFull = getChatFull(j);
        if (chatFull != null) {
            chatFull.call = call.getInputGroupCall();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(j), Long.valueOf(call.call.id), Boolean.FALSE);
        loadFullChat(j, 0, true);
    }

    public ChatObject.Call getGroupCall(long j, boolean z) {
        return getGroupCall(j, z, null);
    }

    public ChatObject.Call getGroupCall(final long j, boolean z, final Runnable runnable) {
        TLRPC$TL_inputGroupCall tLRPC$TL_inputGroupCall;
        TLRPC$ChatFull chatFull = getChatFull(j);
        if (chatFull == null || (tLRPC$TL_inputGroupCall = chatFull.call) == null) {
            return null;
        }
        ChatObject.Call call = this.groupCalls.get(tLRPC$TL_inputGroupCall.id);
        if (call == null && z && !this.loadingGroupCalls.contains(Long.valueOf(j))) {
            this.loadingGroupCalls.add(Long.valueOf(j));
            if (chatFull.call != null) {
                TLRPC$TL_phone_getGroupCall tLRPC$TL_phone_getGroupCall = new TLRPC$TL_phone_getGroupCall();
                tLRPC$TL_phone_getGroupCall.call = chatFull.call;
                tLRPC$TL_phone_getGroupCall.limit = 20;
                getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCall, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda307
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$getGroupCall$44(j, runnable, tLObject, tLRPC$TL_error);
                    }
                });
            }
        }
        if (call != null && (call.call instanceof TLRPC$TL_groupCallDiscarded)) {
            return null;
        }
        return call;
    }

    public /* synthetic */ void lambda$getGroupCall$44(final long j, final Runnable runnable, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda146
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getGroupCall$43(tLObject, j, runnable);
            }
        });
    }

    public /* synthetic */ void lambda$getGroupCall$43(TLObject tLObject, long j, Runnable runnable) {
        if (tLObject != null) {
            TLRPC$TL_phone_groupCall tLRPC$TL_phone_groupCall = (TLRPC$TL_phone_groupCall) tLObject;
            putUsers(tLRPC$TL_phone_groupCall.users, false);
            putChats(tLRPC$TL_phone_groupCall.chats, false);
            ChatObject.Call call = new ChatObject.Call();
            call.setCall(getAccountInstance(), j, tLRPC$TL_phone_groupCall);
            this.groupCalls.put(tLRPC$TL_phone_groupCall.call.id, call);
            this.groupCallsByChatId.put(j, call);
            getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(j), Long.valueOf(tLRPC$TL_phone_groupCall.call.id), Boolean.FALSE);
            if (runnable != null) {
                runnable.run();
            }
        }
        this.loadingGroupCalls.remove(Long.valueOf(j));
    }

    public void cancelLoadFullUser(long j) {
        this.loadingFullUsers.remove(Long.valueOf(j));
    }

    public void cancelLoadFullChat(long j) {
        this.loadingFullChats.remove(Long.valueOf(j));
    }

    public void clearFullUsers() {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
    }

    private void reloadDialogsReadValue(ArrayList<TLRPC$Dialog> arrayList, long j) {
        if (j != 0 || (arrayList != null && !arrayList.isEmpty())) {
            TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs = new TLRPC$TL_messages_getPeerDialogs();
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    TLRPC$InputPeer inputPeer = getInputPeer(arrayList.get(i).id);
                    if (!(inputPeer instanceof TLRPC$TL_inputPeerChannel) || inputPeer.access_hash != 0) {
                        TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
                        tLRPC$TL_inputDialogPeer.peer = inputPeer;
                        tLRPC$TL_messages_getPeerDialogs.peers.add(tLRPC$TL_inputDialogPeer);
                    }
                }
            } else {
                TLRPC$InputPeer inputPeer2 = getInputPeer(j);
                if ((inputPeer2 instanceof TLRPC$TL_inputPeerChannel) && inputPeer2.access_hash == 0) {
                    return;
                }
                TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer2 = new TLRPC$TL_inputDialogPeer();
                tLRPC$TL_inputDialogPeer2.peer = inputPeer2;
                tLRPC$TL_messages_getPeerDialogs.peers.add(tLRPC$TL_inputDialogPeer2);
            }
            if (tLRPC$TL_messages_getPeerDialogs.peers.isEmpty()) {
                return;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getPeerDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda256
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$reloadDialogsReadValue$45(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$reloadDialogsReadValue$45(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs = (TLRPC$TL_messages_peerDialogs) tLObject;
            ArrayList<TLRPC$Update> arrayList = new ArrayList<>();
            for (int i = 0; i < tLRPC$TL_messages_peerDialogs.dialogs.size(); i++) {
                TLRPC$Dialog tLRPC$Dialog = tLRPC$TL_messages_peerDialogs.dialogs.get(i);
                DialogObject.initDialog(tLRPC$Dialog);
                Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(tLRPC$Dialog.id));
                if (num == null) {
                    num = 0;
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(tLRPC$Dialog.id), Integer.valueOf(Math.max(tLRPC$Dialog.read_inbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    if (tLRPC$Dialog.peer.channel_id != 0) {
                        TLRPC$TL_updateReadChannelInbox tLRPC$TL_updateReadChannelInbox = new TLRPC$TL_updateReadChannelInbox();
                        tLRPC$TL_updateReadChannelInbox.channel_id = tLRPC$Dialog.peer.channel_id;
                        tLRPC$TL_updateReadChannelInbox.max_id = tLRPC$Dialog.read_inbox_max_id;
                        tLRPC$TL_updateReadChannelInbox.still_unread_count = tLRPC$Dialog.unread_count;
                        arrayList.add(tLRPC$TL_updateReadChannelInbox);
                    } else {
                        TLRPC$TL_updateReadHistoryInbox tLRPC$TL_updateReadHistoryInbox = new TLRPC$TL_updateReadHistoryInbox();
                        tLRPC$TL_updateReadHistoryInbox.peer = tLRPC$Dialog.peer;
                        tLRPC$TL_updateReadHistoryInbox.max_id = tLRPC$Dialog.read_inbox_max_id;
                        arrayList.add(tLRPC$TL_updateReadHistoryInbox);
                    }
                }
                Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(tLRPC$Dialog.id));
                if (num2 == null) {
                    num2 = 0;
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(tLRPC$Dialog.id), Integer.valueOf(Math.max(tLRPC$Dialog.read_outbox_max_id, num2.intValue())));
                if (tLRPC$Dialog.read_outbox_max_id > num2.intValue()) {
                    if (tLRPC$Dialog.peer.channel_id != 0) {
                        TLRPC$TL_updateReadChannelOutbox tLRPC$TL_updateReadChannelOutbox = new TLRPC$TL_updateReadChannelOutbox();
                        tLRPC$TL_updateReadChannelOutbox.channel_id = tLRPC$Dialog.peer.channel_id;
                        tLRPC$TL_updateReadChannelOutbox.max_id = tLRPC$Dialog.read_outbox_max_id;
                        arrayList.add(tLRPC$TL_updateReadChannelOutbox);
                    } else {
                        TLRPC$TL_updateReadHistoryOutbox tLRPC$TL_updateReadHistoryOutbox = new TLRPC$TL_updateReadHistoryOutbox();
                        tLRPC$TL_updateReadHistoryOutbox.peer = tLRPC$Dialog.peer;
                        tLRPC$TL_updateReadHistoryOutbox.max_id = tLRPC$Dialog.read_outbox_max_id;
                        arrayList.add(tLRPC$TL_updateReadHistoryOutbox);
                    }
                }
            }
            if (arrayList.isEmpty()) {
                return;
            }
            processUpdateArray(arrayList, null, null, false, 0);
        }
    }

    public TLRPC$ChannelParticipant getAdminInChannel(long j, long j2) {
        LongSparseArray<TLRPC$ChannelParticipant> longSparseArray = this.channelAdmins.get(j2);
        if (longSparseArray == null) {
            return null;
        }
        return longSparseArray.get(j);
    }

    public String getAdminRank(long j, long j2) {
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant;
        if (j == j2) {
            return "";
        }
        LongSparseArray<TLRPC$ChannelParticipant> longSparseArray = this.channelAdmins.get(j);
        if (longSparseArray == null || (tLRPC$ChannelParticipant = longSparseArray.get(j2)) == null) {
            return null;
        }
        String str = tLRPC$ChannelParticipant.rank;
        return str != null ? str : "";
    }

    public boolean isChannelAdminsLoaded(long j) {
        return this.channelAdmins.get(j) != null;
    }

    public void loadChannelAdmins(final long j, boolean z) {
        if (SystemClock.elapsedRealtime() - this.loadingChannelAdmins.get(j) < 60) {
            return;
        }
        this.loadingChannelAdmins.put(j, (int) (SystemClock.elapsedRealtime() / 1000));
        if (z) {
            getMessagesStorage().loadChannelAdmins(j);
            return;
        }
        TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
        tLRPC$TL_channels_getParticipants.channel = getInputChannel(j);
        tLRPC$TL_channels_getParticipants.limit = 100;
        tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsAdmins();
        getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipants, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda282
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadChannelAdmins$46(j, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadChannelAdmins$46(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_channels_channelParticipants) {
            processLoadedAdminsResponse(j, (TLRPC$TL_channels_channelParticipants) tLObject);
        }
    }

    public void processLoadedAdminsResponse(long j, TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants) {
        LongSparseArray<TLRPC$ChannelParticipant> longSparseArray = new LongSparseArray<>(tLRPC$TL_channels_channelParticipants.participants.size());
        for (int i = 0; i < tLRPC$TL_channels_channelParticipants.participants.size(); i++) {
            TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipants.participants.get(i);
            longSparseArray.put(MessageObject.getPeerId(tLRPC$ChannelParticipant.peer), tLRPC$ChannelParticipant);
        }
        processLoadedChannelAdmins(longSparseArray, j, false);
    }

    public void processLoadedChannelAdmins(final LongSparseArray<TLRPC$ChannelParticipant> longSparseArray, final long j, final boolean z) {
        if (!z) {
            getMessagesStorage().putChannelAdmins(j, longSparseArray);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda80
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processLoadedChannelAdmins$47(j, longSparseArray, z);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedChannelAdmins$47(long j, LongSparseArray longSparseArray, boolean z) {
        this.channelAdmins.put(j, longSparseArray);
        if (z) {
            this.loadingChannelAdmins.delete(j);
            loadChannelAdmins(j, false);
            getNotificationCenter().postNotificationName(NotificationCenter.didLoadChatAdmins, Long.valueOf(j));
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x0050, code lost:
        if (r10.dialogs_read_outbox_max.get(java.lang.Long.valueOf(r5)) == null) goto L14;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void loadFullChat(final long r11, final int r13, boolean r14) {
        /*
            r10 = this;
            java.util.ArrayList<java.lang.Long> r14 = r10.loadedFullChats
            java.lang.Long r0 = java.lang.Long.valueOf(r11)
            boolean r14 = r14.contains(r0)
            java.util.ArrayList<java.lang.Long> r0 = r10.loadingFullChats
            java.lang.Long r1 = java.lang.Long.valueOf(r11)
            r0.add(r1)
            long r5 = -r11
            java.lang.Long r0 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r4 = r10.getChat(r0)
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r0 == 0) goto L33
            org.telegram.tgnet.TLRPC$TL_channels_getFullChannel r0 = new org.telegram.tgnet.TLRPC$TL_channels_getFullChannel
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r1 = getInputChannel(r4)
            r0.channel = r1
            r14 = r14 ^ 1
            r10.loadChannelAdmins(r11, r14)
            goto L56
        L33:
            org.telegram.tgnet.TLRPC$TL_messages_getFullChat r0 = new org.telegram.tgnet.TLRPC$TL_messages_getFullChat
            r0.<init>()
            r0.chat_id = r11
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r14 = r10.dialogs_read_inbox_max
            java.lang.Long r1 = java.lang.Long.valueOf(r5)
            java.lang.Object r14 = r14.get(r1)
            if (r14 == 0) goto L52
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r14 = r10.dialogs_read_outbox_max
            java.lang.Long r1 = java.lang.Long.valueOf(r5)
            java.lang.Object r14 = r14.get(r1)
            if (r14 != 0) goto L56
        L52:
            r14 = 0
            r10.reloadDialogsReadValue(r14, r5)
        L56:
            org.telegram.tgnet.ConnectionsManager r14 = r10.getConnectionsManager()
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda328 r1 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda328
            r2 = r1
            r3 = r10
            r7 = r11
            r9 = r13
            r2.<init>()
            int r11 = r14.sendRequest(r0, r1)
            if (r13 == 0) goto L70
            org.telegram.tgnet.ConnectionsManager r12 = r10.getConnectionsManager()
            r12.bindRequestToGuid(r11, r13)
        L70:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.loadFullChat(long, int, boolean):void");
    }

    public /* synthetic */ void lambda$loadFullChat$50(TLRPC$Chat tLRPC$Chat, long j, final long j2, final int i, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            final TLRPC$TL_messages_chatFull tLRPC$TL_messages_chatFull = (TLRPC$TL_messages_chatFull) tLObject;
            getMessagesStorage().putUsersAndChats(tLRPC$TL_messages_chatFull.users, tLRPC$TL_messages_chatFull.chats, true, true);
            getMessagesStorage().updateChatInfo(tLRPC$TL_messages_chatFull.full_chat, false);
            if (ChatObject.isChannel(tLRPC$Chat)) {
                Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(j));
                if (num == null) {
                    num = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, j));
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tLRPC$TL_messages_chatFull.full_chat.read_inbox_max_id, num.intValue())));
                if (tLRPC$TL_messages_chatFull.full_chat.read_inbox_max_id > num.intValue()) {
                    ArrayList<TLRPC$Update> arrayList = new ArrayList<>();
                    TLRPC$TL_updateReadChannelInbox tLRPC$TL_updateReadChannelInbox = new TLRPC$TL_updateReadChannelInbox();
                    tLRPC$TL_updateReadChannelInbox.channel_id = j2;
                    TLRPC$ChatFull tLRPC$ChatFull = tLRPC$TL_messages_chatFull.full_chat;
                    tLRPC$TL_updateReadChannelInbox.max_id = tLRPC$ChatFull.read_inbox_max_id;
                    tLRPC$TL_updateReadChannelInbox.still_unread_count = tLRPC$ChatFull.unread_count;
                    arrayList.add(tLRPC$TL_updateReadChannelInbox);
                    processUpdateArray(arrayList, null, null, false, 0);
                }
                Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(j));
                if (num2 == null) {
                    num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, j));
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tLRPC$TL_messages_chatFull.full_chat.read_outbox_max_id, num2.intValue())));
                if (tLRPC$TL_messages_chatFull.full_chat.read_outbox_max_id > num2.intValue()) {
                    ArrayList<TLRPC$Update> arrayList2 = new ArrayList<>();
                    TLRPC$TL_updateReadChannelOutbox tLRPC$TL_updateReadChannelOutbox = new TLRPC$TL_updateReadChannelOutbox();
                    tLRPC$TL_updateReadChannelOutbox.channel_id = j2;
                    tLRPC$TL_updateReadChannelOutbox.max_id = tLRPC$TL_messages_chatFull.full_chat.read_outbox_max_id;
                    arrayList2.add(tLRPC$TL_updateReadChannelOutbox);
                    processUpdateArray(arrayList2, null, null, false, 0);
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda93
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$loadFullChat$48(j2, tLRPC$TL_messages_chatFull, i);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda167
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadFullChat$49(tLRPC$TL_error, j2);
            }
        });
    }

    public /* synthetic */ void lambda$loadFullChat$48(long j, TLRPC$TL_messages_chatFull tLRPC$TL_messages_chatFull, int i) {
        TLRPC$Dialog tLRPC$Dialog;
        TLRPC$ChatFull tLRPC$ChatFull = this.fullChats.get(j);
        if (tLRPC$ChatFull != null) {
            tLRPC$TL_messages_chatFull.full_chat.inviterId = tLRPC$ChatFull.inviterId;
        }
        this.fullChats.put(j, tLRPC$TL_messages_chatFull.full_chat);
        long j2 = -j;
        applyDialogNotificationsSettings(j2, 0, tLRPC$TL_messages_chatFull.full_chat.notify_settings);
        for (int i2 = 0; i2 < tLRPC$TL_messages_chatFull.full_chat.bot_info.size(); i2++) {
            getMediaDataController().putBotInfo(j2, tLRPC$TL_messages_chatFull.full_chat.bot_info.get(i2));
        }
        int indexOfKey = this.blockePeers.indexOfKey(j2);
        if (tLRPC$TL_messages_chatFull.full_chat.blocked) {
            if (indexOfKey < 0) {
                this.blockePeers.put(j2, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            }
        } else if (indexOfKey >= 0) {
            this.blockePeers.removeAt(indexOfKey);
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
        this.exportedChats.put(j, tLRPC$TL_messages_chatFull.full_chat.exported_invite);
        this.loadingFullChats.remove(Long.valueOf(j));
        this.loadedFullChats.add(Long.valueOf(j));
        putUsers(tLRPC$TL_messages_chatFull.users, false);
        putChats(tLRPC$TL_messages_chatFull.chats, false);
        if (tLRPC$TL_messages_chatFull.full_chat.stickerset != null) {
            getMediaDataController().getGroupStickerSetById(tLRPC$TL_messages_chatFull.full_chat.stickerset);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, tLRPC$TL_messages_chatFull.full_chat, Integer.valueOf(i), Boolean.FALSE, Boolean.TRUE);
        if ((tLRPC$TL_messages_chatFull.full_chat.flags & 2048) == 0 || (tLRPC$Dialog = this.dialogs_dict.get(j2)) == null) {
            return;
        }
        int i3 = tLRPC$Dialog.folder_id;
        int i4 = tLRPC$TL_messages_chatFull.full_chat.folder_id;
        if (i3 == i4) {
            return;
        }
        tLRPC$Dialog.folder_id = i4;
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$loadFullChat$49(TLRPC$TL_error tLRPC$TL_error, long j) {
        checkChannelError(tLRPC$TL_error.text, j);
        this.loadingFullChats.remove(Long.valueOf(j));
    }

    public void loadFullUser(final TLRPC$User tLRPC$User, final int i, boolean z) {
        if (tLRPC$User == null || this.loadingFullUsers.contains(Long.valueOf(tLRPC$User.id))) {
            return;
        }
        if (!z && this.loadedFullUsers.contains(Long.valueOf(tLRPC$User.id))) {
            return;
        }
        this.loadingFullUsers.add(Long.valueOf(tLRPC$User.id));
        TLRPC$TL_users_getFullUser tLRPC$TL_users_getFullUser = new TLRPC$TL_users_getFullUser();
        tLRPC$TL_users_getFullUser.id = getInputUser(tLRPC$User);
        long j = tLRPC$User.id;
        if (this.dialogs_read_inbox_max.get(Long.valueOf(j)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(j)) == null) {
            reloadDialogsReadValue(null, j);
        }
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_users_getFullUser, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda337
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadFullUser$53(tLRPC$User, i, tLObject, tLRPC$TL_error);
            }
        }), i);
    }

    public /* synthetic */ void lambda$loadFullUser$53(final TLRPC$User tLRPC$User, final int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_users_userFull tLRPC$TL_users_userFull = (TLRPC$TL_users_userFull) tLObject;
            final TLRPC$UserFull tLRPC$UserFull = tLRPC$TL_users_userFull.full_user;
            putUsers(tLRPC$TL_users_userFull.users, false);
            putChats(tLRPC$TL_users_userFull.chats, false);
            TLRPC$UserFull tLRPC$UserFull2 = tLRPC$TL_users_userFull.full_user;
            tLRPC$UserFull2.user = getUser(Long.valueOf(tLRPC$UserFull2.id));
            getMessagesStorage().updateUserInfo(tLRPC$UserFull, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda192
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$loadFullUser$51(tLRPC$UserFull, tLRPC$User, i);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda191
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadFullUser$52(tLRPC$User);
            }
        });
    }

    public /* synthetic */ void lambda$loadFullUser$51(TLRPC$UserFull tLRPC$UserFull, TLRPC$User tLRPC$User, int i) {
        TLRPC$Dialog tLRPC$Dialog;
        savePeerSettings(tLRPC$UserFull.user.id, tLRPC$UserFull.settings, false);
        applyDialogNotificationsSettings(tLRPC$User.id, 0, tLRPC$UserFull.notify_settings);
        TLRPC$BotInfo tLRPC$BotInfo = tLRPC$UserFull.bot_info;
        if (tLRPC$BotInfo instanceof TLRPC$TL_botInfo) {
            tLRPC$BotInfo.user_id = tLRPC$User.id;
            getMediaDataController().putBotInfo(tLRPC$User.id, tLRPC$UserFull.bot_info);
        }
        int indexOfKey = this.blockePeers.indexOfKey(tLRPC$User.id);
        if (tLRPC$UserFull.blocked) {
            if (indexOfKey < 0) {
                this.blockePeers.put(tLRPC$User.id, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            }
        } else if (indexOfKey >= 0) {
            this.blockePeers.removeAt(indexOfKey);
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
        this.fullUsers.put(tLRPC$User.id, tLRPC$UserFull);
        this.loadingFullUsers.remove(Long.valueOf(tLRPC$User.id));
        this.loadedFullUsers.add(Long.valueOf(tLRPC$User.id));
        String str = tLRPC$User.first_name + tLRPC$User.last_name + UserObject.getPublicUsername(tLRPC$User);
        ArrayList<TLRPC$User> arrayList = new ArrayList<>();
        arrayList.add(tLRPC$UserFull.user);
        putUsers(arrayList, false);
        getMessagesStorage().putUsersAndChats(arrayList, null, false, true);
        if (!str.equals(tLRPC$UserFull.user.first_name + tLRPC$UserFull.user.last_name + UserObject.getPublicUsername(tLRPC$UserFull.user))) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_NAME));
        }
        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$UserFull.user.photo;
        if (tLRPC$UserProfilePhoto != null && tLRPC$UserProfilePhoto.has_video) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
        }
        TLRPC$BotInfo tLRPC$BotInfo2 = tLRPC$UserFull.bot_info;
        if (tLRPC$BotInfo2 instanceof TLRPC$TL_botInfo) {
            tLRPC$BotInfo2.user_id = tLRPC$UserFull.id;
            getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, tLRPC$UserFull.bot_info, Integer.valueOf(i));
        }
        getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Long.valueOf(tLRPC$User.id), tLRPC$UserFull);
        if ((tLRPC$UserFull.flags & 2048) == 0 || (tLRPC$Dialog = this.dialogs_dict.get(tLRPC$User.id)) == null) {
            return;
        }
        int i2 = tLRPC$Dialog.folder_id;
        int i3 = tLRPC$UserFull.folder_id;
        if (i2 == i3) {
            return;
        }
        tLRPC$Dialog.folder_id = i3;
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$loadFullUser$52(TLRPC$User tLRPC$User) {
        this.loadingFullUsers.remove(Long.valueOf(tLRPC$User.id));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void reloadMessages(ArrayList<Integer> arrayList, final long j, final boolean z) {
        TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages;
        if (arrayList.isEmpty()) {
            return;
        }
        final ArrayList<Integer> arrayList2 = new ArrayList<>();
        TLRPC$Chat chat = DialogObject.isChatDialog(j) ? getChat(Long.valueOf(-j)) : null;
        if (ChatObject.isChannel(chat)) {
            TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
            tLRPC$TL_channels_getMessages.channel = getInputChannel(chat);
            tLRPC$TL_channels_getMessages.id = arrayList2;
            tLRPC$TL_messages_getMessages = tLRPC$TL_channels_getMessages;
        } else {
            TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages2 = new TLRPC$TL_messages_getMessages();
            tLRPC$TL_messages_getMessages2.id = arrayList2;
            tLRPC$TL_messages_getMessages = tLRPC$TL_messages_getMessages2;
        }
        TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages3 = tLRPC$TL_messages_getMessages;
        ArrayList<Integer> arrayList3 = this.reloadingMessages.get(j);
        for (int i = 0; i < arrayList.size(); i++) {
            Integer num = arrayList.get(i);
            if (arrayList3 == null || !arrayList3.contains(num)) {
                arrayList2.add(num);
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        if (arrayList3 == null) {
            arrayList3 = new ArrayList<>();
            this.reloadingMessages.put(j, arrayList3);
        }
        arrayList3.addAll(arrayList2);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages3, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda318
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$reloadMessages$55(j, z, arrayList2, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$reloadMessages$55(final long j, boolean z, final ArrayList arrayList, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i = 0; i < tLRPC$messages_Messages.users.size(); i++) {
                TLRPC$User tLRPC$User = tLRPC$messages_Messages.users.get(i);
                longSparseArray.put(tLRPC$User.id, tLRPC$User);
            }
            LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i2 = 0; i2 < tLRPC$messages_Messages.chats.size(); i2++) {
                TLRPC$Chat tLRPC$Chat = tLRPC$messages_Messages.chats.get(i2);
                longSparseArray2.put(tLRPC$Chat.id, tLRPC$Chat);
            }
            Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(j));
            if (num == null) {
                num = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, j));
                this.dialogs_read_inbox_max.put(Long.valueOf(j), num);
            }
            Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(j));
            if (num2 == null) {
                num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, j));
                this.dialogs_read_outbox_max.put(Long.valueOf(j), num2);
            }
            ArrayList arrayList2 = new ArrayList();
            int i3 = 0;
            while (i3 < tLRPC$messages_Messages.messages.size()) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i3);
                tLRPC$Message.dialog_id = j;
                if (!z) {
                    tLRPC$Message.unread = (tLRPC$Message.out ? num2 : num).intValue() < tLRPC$Message.id;
                }
                Integer num3 = num;
                ArrayList arrayList3 = arrayList2;
                arrayList3.add(new MessageObject(this.currentAccount, tLRPC$Message, (LongSparseArray<TLRPC$User>) longSparseArray, (LongSparseArray<TLRPC$Chat>) longSparseArray2, true, true));
                i3++;
                arrayList2 = arrayList3;
                num = num3;
            }
            final ArrayList arrayList4 = arrayList2;
            ImageLoader.saveMessagesThumbs(tLRPC$messages_Messages.messages);
            getMessagesStorage().putMessages(tLRPC$messages_Messages, j, -1, 0, false, z, 0);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda88
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$reloadMessages$54(j, arrayList, arrayList4);
                }
            });
        }
    }

    public /* synthetic */ void lambda$reloadMessages$54(long j, ArrayList arrayList, ArrayList arrayList2) {
        ArrayList<Integer> arrayList3 = this.reloadingMessages.get(j);
        if (arrayList3 != null) {
            arrayList3.removeAll(arrayList);
            if (arrayList3.isEmpty()) {
                this.reloadingMessages.remove(j);
            }
        }
        ArrayList<MessageObject> arrayList4 = this.dialogMessage.get(j);
        if (arrayList4 != null) {
            for (int i = 0; i < arrayList4.size(); i++) {
                MessageObject messageObject = arrayList4.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 < arrayList2.size()) {
                        MessageObject messageObject2 = (MessageObject) arrayList2.get(i2);
                        if (messageObject.getId() == messageObject2.getId()) {
                            arrayList4.set(i, messageObject2);
                            if (messageObject2.messageOwner.peer_id.channel_id == 0) {
                                MessageObject messageObject3 = this.dialogMessagesByIds.get(messageObject2.getId());
                                this.dialogMessagesByIds.remove(messageObject2.getId());
                                if (messageObject3 != null) {
                                    this.dialogMessagesByIds.put(messageObject3.getId(), messageObject3);
                                }
                            }
                            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        } else {
                            i2++;
                        }
                    }
                }
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList2);
    }

    public void hidePeerSettingsBar(long j, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$User == null && tLRPC$Chat == null) {
            return;
        }
        SharedPreferences.Editor edit = this.notificationsPreferences.edit();
        edit.putInt("dialog_bar_vis3" + j, 3);
        edit.remove("dialog_bar_invite" + j);
        edit.commit();
        if (DialogObject.isEncryptedDialog(j)) {
            return;
        }
        TLRPC$TL_messages_hidePeerSettingsBar tLRPC$TL_messages_hidePeerSettingsBar = new TLRPC$TL_messages_hidePeerSettingsBar();
        if (tLRPC$User != null) {
            tLRPC$TL_messages_hidePeerSettingsBar.peer = getInputPeer(tLRPC$User.id);
        } else {
            tLRPC$TL_messages_hidePeerSettingsBar.peer = getInputPeer(-tLRPC$Chat.id);
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_hidePeerSettingsBar, MessagesController$$ExternalSyntheticLambda364.INSTANCE);
    }

    public void reportSpam(long j, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, TLRPC$EncryptedChat tLRPC$EncryptedChat, boolean z) {
        if (tLRPC$User == null && tLRPC$Chat == null && tLRPC$EncryptedChat == null) {
            return;
        }
        SharedPreferences.Editor edit = this.notificationsPreferences.edit();
        edit.putInt("dialog_bar_vis3" + j, 3);
        edit.commit();
        if (DialogObject.isEncryptedDialog(j)) {
            if (tLRPC$EncryptedChat == null || tLRPC$EncryptedChat.access_hash == 0) {
                return;
            }
            TLRPC$TL_messages_reportEncryptedSpam tLRPC$TL_messages_reportEncryptedSpam = new TLRPC$TL_messages_reportEncryptedSpam();
            TLRPC$TL_inputEncryptedChat tLRPC$TL_inputEncryptedChat = new TLRPC$TL_inputEncryptedChat();
            tLRPC$TL_messages_reportEncryptedSpam.peer = tLRPC$TL_inputEncryptedChat;
            tLRPC$TL_inputEncryptedChat.chat_id = tLRPC$EncryptedChat.id;
            tLRPC$TL_inputEncryptedChat.access_hash = tLRPC$EncryptedChat.access_hash;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_reportEncryptedSpam, MessagesController$$ExternalSyntheticLambda352.INSTANCE, 2);
        } else if (z) {
            TLRPC$TL_account_reportPeer tLRPC$TL_account_reportPeer = new TLRPC$TL_account_reportPeer();
            if (tLRPC$Chat != null) {
                tLRPC$TL_account_reportPeer.peer = getInputPeer(-tLRPC$Chat.id);
            } else if (tLRPC$User != null) {
                tLRPC$TL_account_reportPeer.peer = getInputPeer(tLRPC$User.id);
            }
            tLRPC$TL_account_reportPeer.message = "";
            tLRPC$TL_account_reportPeer.reason = new TLRPC$ReportReason() { // from class: org.telegram.tgnet.TLRPC$TL_inputReportReasonGeoIrrelevant
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                    abstractSerializedData.writeInt32(constructor);
                }
            };
            getConnectionsManager().sendRequest(tLRPC$TL_account_reportPeer, MessagesController$$ExternalSyntheticLambda350.INSTANCE, 2);
        } else {
            TLRPC$TL_messages_reportSpam tLRPC$TL_messages_reportSpam = new TLRPC$TL_messages_reportSpam();
            if (tLRPC$Chat != null) {
                tLRPC$TL_messages_reportSpam.peer = getInputPeer(-tLRPC$Chat.id);
            } else if (tLRPC$User != null) {
                tLRPC$TL_messages_reportSpam.peer = getInputPeer(tLRPC$User.id);
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_reportSpam, MessagesController$$ExternalSyntheticLambda366.INSTANCE, 2);
        }
    }

    private void savePeerSettings(long j, TLRPC$TL_peerSettings tLRPC$TL_peerSettings, boolean z) {
        if (tLRPC$TL_peerSettings != null) {
            SharedPreferences sharedPreferences = this.notificationsPreferences;
            if (sharedPreferences.getInt("dialog_bar_vis3" + j, 0) == 3) {
                return;
            }
            SharedPreferences.Editor edit = this.notificationsPreferences.edit();
            boolean z2 = !tLRPC$TL_peerSettings.report_spam && !tLRPC$TL_peerSettings.add_contact && !tLRPC$TL_peerSettings.block_contact && !tLRPC$TL_peerSettings.share_contact && !tLRPC$TL_peerSettings.report_geo && !tLRPC$TL_peerSettings.invite_members;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("peer settings loaded for " + j + " add = " + tLRPC$TL_peerSettings.add_contact + " block = " + tLRPC$TL_peerSettings.block_contact + " spam = " + tLRPC$TL_peerSettings.report_spam + " share = " + tLRPC$TL_peerSettings.share_contact + " geo = " + tLRPC$TL_peerSettings.report_geo + " hide = " + z2 + " distance = " + tLRPC$TL_peerSettings.geo_distance + " invite = " + tLRPC$TL_peerSettings.invite_members);
            }
            edit.putInt("dialog_bar_vis3" + j, z2 ? 1 : 2);
            edit.putBoolean("dialog_bar_share" + j, tLRPC$TL_peerSettings.share_contact);
            edit.putBoolean("dialog_bar_report" + j, tLRPC$TL_peerSettings.report_spam);
            edit.putBoolean("dialog_bar_add" + j, tLRPC$TL_peerSettings.add_contact);
            edit.putBoolean("dialog_bar_block" + j, tLRPC$TL_peerSettings.block_contact);
            edit.putBoolean("dialog_bar_exception" + j, tLRPC$TL_peerSettings.need_contacts_exception);
            edit.putBoolean("dialog_bar_location" + j, tLRPC$TL_peerSettings.report_geo);
            edit.putBoolean("dialog_bar_archived" + j, tLRPC$TL_peerSettings.autoarchived);
            edit.putBoolean("dialog_bar_invite" + j, tLRPC$TL_peerSettings.invite_members);
            edit.putString("dialog_bar_chat_with_admin_title" + j, tLRPC$TL_peerSettings.request_chat_title);
            edit.putBoolean("dialog_bar_chat_with_channel" + j, tLRPC$TL_peerSettings.request_chat_broadcast);
            edit.putInt("dialog_bar_chat_with_date" + j, tLRPC$TL_peerSettings.request_chat_date);
            SharedPreferences sharedPreferences2 = this.notificationsPreferences;
            if (sharedPreferences2.getInt("dialog_bar_distance" + j, -1) != -2) {
                if ((tLRPC$TL_peerSettings.flags & 64) != 0) {
                    edit.putInt("dialog_bar_distance" + j, tLRPC$TL_peerSettings.geo_distance);
                } else {
                    edit.remove("dialog_bar_distance" + j);
                }
            }
            edit.apply();
            getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf(j));
        }
    }

    public void loadPeerSettings(TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat) {
        final long j;
        if (tLRPC$User == null && tLRPC$Chat == null) {
            return;
        }
        if (tLRPC$User != null) {
            j = tLRPC$User.id;
        } else {
            j = -tLRPC$Chat.id;
        }
        if (this.loadingPeerSettings.indexOfKey(j) >= 0) {
            return;
        }
        this.loadingPeerSettings.put(j, Boolean.TRUE);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("request spam button for " + j);
        }
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        int i = sharedPreferences.getInt("dialog_bar_vis3" + j, 0);
        if (i == 1 || i == 3) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.d("dialog bar already hidden for " + j);
            return;
        }
        TLRPC$TL_messages_getPeerSettings tLRPC$TL_messages_getPeerSettings = new TLRPC$TL_messages_getPeerSettings();
        if (tLRPC$User != null) {
            tLRPC$TL_messages_getPeerSettings.peer = getInputPeer(tLRPC$User.id);
        } else {
            tLRPC$TL_messages_getPeerSettings.peer = getInputPeer(-tLRPC$Chat.id);
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getPeerSettings, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda285
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadPeerSettings$61(j, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadPeerSettings$61(final long j, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda90
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadPeerSettings$60(j, tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$loadPeerSettings$60(long j, TLObject tLObject) {
        this.loadingPeerSettings.remove(j);
        if (tLObject != null) {
            TLRPC$TL_messages_peerSettings tLRPC$TL_messages_peerSettings = (TLRPC$TL_messages_peerSettings) tLObject;
            TLRPC$TL_peerSettings tLRPC$TL_peerSettings = tLRPC$TL_messages_peerSettings.settings;
            putUsers(tLRPC$TL_messages_peerSettings.users, false);
            putChats(tLRPC$TL_messages_peerSettings.chats, false);
            savePeerSettings(j, tLRPC$TL_peerSettings, false);
        }
    }

    public void processNewChannelDifferenceParams(int i, int i2, long j) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processNewChannelDifferenceParams pts = " + i + " pts_count = " + i2 + " channeldId = " + j);
        }
        int i3 = this.channelsPts.get(j);
        if (i3 == 0) {
            i3 = getMessagesStorage().getChannelPtsSync(j);
            if (i3 == 0) {
                i3 = 1;
            }
            this.channelsPts.put(j, i3);
        }
        if (i3 + i2 == i) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("APPLY CHANNEL PTS");
            }
            this.channelsPts.put(j, i);
            getMessagesStorage().saveChannelPts(j, i);
        } else if (i3 == i) {
        } else {
            long j2 = this.updatesStartWaitTimeChannels.get(j);
            if (this.gettingDifferenceChannels.get(j, Boolean.FALSE).booleanValue() || j2 == 0 || Math.abs(System.currentTimeMillis() - j2) <= 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("ADD CHANNEL UPDATE TO QUEUE pts = " + i + " pts_count = " + i2);
                }
                if (j2 == 0) {
                    this.updatesStartWaitTimeChannels.put(j, System.currentTimeMillis());
                }
                UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts();
                userActionUpdatesPts.pts = i;
                userActionUpdatesPts.pts_count = i2;
                userActionUpdatesPts.chat_id = j;
                ArrayList<TLRPC$Updates> arrayList = this.updatesQueueChannels.get(j);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    this.updatesQueueChannels.put(j, arrayList);
                }
                arrayList.add(userActionUpdatesPts);
                return;
            }
            getChannelDifference(j);
        }
    }

    public void processNewDifferenceParams(int i, int i2, int i3, int i4) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processNewDifferenceParams seq = " + i + " pts = " + i2 + " date = " + i3 + " pts_count = " + i4);
        }
        if (i2 != -1) {
            if (getMessagesStorage().getLastPtsValue() + i4 == i2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("APPLY PTS");
                }
                getMessagesStorage().setLastPtsValue(i2);
                getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
            } else if (getMessagesStorage().getLastPtsValue() != i2) {
                if (this.gettingDifference || this.updatesStartWaitTimePts == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("ADD UPDATE TO QUEUE pts = " + i2 + " pts_count = " + i4);
                    }
                    if (this.updatesStartWaitTimePts == 0) {
                        this.updatesStartWaitTimePts = System.currentTimeMillis();
                    }
                    UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts();
                    userActionUpdatesPts.pts = i2;
                    userActionUpdatesPts.pts_count = i4;
                    this.updatesQueuePts.add(userActionUpdatesPts);
                } else {
                    getDifference();
                }
            }
        }
        if (i != -1) {
            if (getMessagesStorage().getLastSeqValue() + 1 == i) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("APPLY SEQ");
                }
                getMessagesStorage().setLastSeqValue(i);
                if (i3 != -1) {
                    getMessagesStorage().setLastDateValue(i3);
                }
                getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
            } else if (getMessagesStorage().getLastSeqValue() == i) {
            } else {
                if (this.gettingDifference || this.updatesStartWaitTimeSeq == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) <= 1500) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("ADD UPDATE TO QUEUE seq = " + i);
                    }
                    if (this.updatesStartWaitTimeSeq == 0) {
                        this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                    }
                    UserActionUpdatesSeq userActionUpdatesSeq = new UserActionUpdatesSeq();
                    userActionUpdatesSeq.seq = i;
                    this.updatesQueueSeq.add(userActionUpdatesSeq);
                    return;
                }
                getDifference();
            }
        }
    }

    public void didAddedNewTask(final int i, final long j, final SparseArray<ArrayList<Integer>> sparseArray) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$didAddedNewTask$62(i);
            }
        });
        if (sparseArray != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda79
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$didAddedNewTask$63(j, sparseArray);
                }
            });
        }
    }

    public /* synthetic */ void lambda$didAddedNewTask$62(int i) {
        int i2;
        if (!(this.currentDeletingTaskMids == null && this.currentDeletingTaskMediaMids == null && !this.gettingNewDeleteTask) && ((i2 = this.currentDeletingTaskTime) == 0 || i >= i2)) {
            return;
        }
        getNewDeleteTask(null, null);
    }

    public /* synthetic */ void lambda$didAddedNewTask$63(long j, SparseArray sparseArray) {
        getNotificationCenter().postNotificationName(NotificationCenter.didCreatedNewDeleteTask, Long.valueOf(j), sparseArray);
    }

    public void getNewDeleteTask(final LongSparseArray<ArrayList<Integer>> longSparseArray, final LongSparseArray<ArrayList<Integer>> longSparseArray2) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda100
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getNewDeleteTask$64(longSparseArray, longSparseArray2);
            }
        });
    }

    public /* synthetic */ void lambda$getNewDeleteTask$64(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.gettingNewDeleteTask = true;
        getMessagesStorage().getNewTask(longSparseArray, longSparseArray2);
    }

    private boolean checkDeletingTask(boolean z) {
        int i;
        int currentTime = getConnectionsManager().getCurrentTime();
        if (!(this.currentDeletingTaskMids == null && this.currentDeletingTaskMediaMids == null) && (z || ((i = this.currentDeletingTaskTime) != 0 && i <= currentTime))) {
            this.currentDeletingTaskTime = 0;
            if (this.currentDeleteTaskRunnable != null && !z) {
                Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            }
            final LongSparseArray<ArrayList<Integer>> longSparseArray = null;
            this.currentDeleteTaskRunnable = null;
            LongSparseArray<ArrayList<Integer>> longSparseArray2 = this.currentDeletingTaskMids;
            final LongSparseArray<ArrayList<Integer>> m2clone = longSparseArray2 != null ? longSparseArray2.m2clone() : null;
            LongSparseArray<ArrayList<Integer>> longSparseArray3 = this.currentDeletingTaskMediaMids;
            if (longSparseArray3 != null) {
                longSparseArray = longSparseArray3.m2clone();
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda102
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$checkDeletingTask$66(m2clone, longSparseArray);
                }
            });
            return true;
        }
        return false;
    }

    public /* synthetic */ void lambda$checkDeletingTask$66(final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2) {
        if (longSparseArray != null) {
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                ArrayList<Integer> arrayList = (ArrayList) longSparseArray.valueAt(i);
                deleteMessages(arrayList, null, null, longSparseArray.keyAt(i), true, false, !arrayList.isEmpty() && arrayList.get(0).intValue() > 0);
            }
        }
        if (longSparseArray2 != null) {
            int size2 = longSparseArray2.size();
            for (int i2 = 0; i2 < size2; i2++) {
                getMessagesStorage().emptyMessagesMedia(longSparseArray2.keyAt(i2), (ArrayList) longSparseArray2.valueAt(i2));
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda101
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$checkDeletingTask$65(longSparseArray, longSparseArray2);
            }
        });
    }

    public /* synthetic */ void lambda$checkDeletingTask$65(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        getNewDeleteTask(longSparseArray, longSparseArray2);
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskMediaMids = null;
    }

    public void processLoadedDeleteTask(final int i, final LongSparseArray<ArrayList<Integer>> longSparseArray, final LongSparseArray<ArrayList<Integer>> longSparseArray2) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda104
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processLoadedDeleteTask$68(longSparseArray, longSparseArray2, i);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedDeleteTask$68(LongSparseArray longSparseArray, LongSparseArray longSparseArray2, int i) {
        this.gettingNewDeleteTask = false;
        if (longSparseArray != null || longSparseArray2 != null) {
            this.currentDeletingTaskTime = i;
            this.currentDeletingTaskMids = longSparseArray;
            this.currentDeletingTaskMediaMids = longSparseArray2;
            if (this.currentDeleteTaskRunnable != null) {
                Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
                this.currentDeleteTaskRunnable = null;
            }
            if (checkDeletingTask(false)) {
                return;
            }
            this.currentDeleteTaskRunnable = new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$processLoadedDeleteTask$67();
                }
            };
            Utilities.stageQueue.postRunnable(this.currentDeleteTaskRunnable, Math.abs(getConnectionsManager().getCurrentTime() - this.currentDeletingTaskTime) * 1000);
            return;
        }
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskMediaMids = null;
    }

    public /* synthetic */ void lambda$processLoadedDeleteTask$67() {
        checkDeletingTask(true);
    }

    public void loadDialogPhotos(final long j, final int i, final int i2, boolean z, final int i3) {
        if (z) {
            getMessagesStorage().getDialogPhotos(j, i, i2, i3);
        } else if (j > 0) {
            TLRPC$User user = getUser(Long.valueOf(j));
            if (user == null) {
                return;
            }
            TLRPC$TL_photos_getUserPhotos tLRPC$TL_photos_getUserPhotos = new TLRPC$TL_photos_getUserPhotos();
            tLRPC$TL_photos_getUserPhotos.limit = i;
            tLRPC$TL_photos_getUserPhotos.offset = 0;
            tLRPC$TL_photos_getUserPhotos.max_id = i2;
            tLRPC$TL_photos_getUserPhotos.user_id = getInputUser(user);
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_photos_getUserPhotos, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda297
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$loadDialogPhotos$69(j, i, i2, i3, tLObject, tLRPC$TL_error);
                }
            }), i3);
        } else if (j >= 0) {
        } else {
            TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
            tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterChatPhotos();
            tLRPC$TL_messages_search.limit = i;
            tLRPC$TL_messages_search.offset_id = i2;
            tLRPC$TL_messages_search.q = "";
            tLRPC$TL_messages_search.peer = getInputPeer(j);
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda296
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$loadDialogPhotos$70(j, i, i2, i3, tLObject, tLRPC$TL_error);
                }
            }), i3);
        }
    }

    public /* synthetic */ void lambda$loadDialogPhotos$69(long j, int i, int i2, int i3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            processLoadedUserPhotos((TLRPC$photos_Photos) tLObject, null, j, i, i2, false, i3);
        }
    }

    public /* synthetic */ void lambda$loadDialogPhotos$70(long j, int i, int i2, int i3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$Photo tLRPC$Photo;
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            TLRPC$TL_photos_photos tLRPC$TL_photos_photos = new TLRPC$TL_photos_photos();
            ArrayList<TLRPC$Message> arrayList = new ArrayList<>();
            tLRPC$TL_photos_photos.count = tLRPC$messages_Messages.count;
            tLRPC$TL_photos_photos.users.addAll(tLRPC$messages_Messages.users);
            for (int i4 = 0; i4 < tLRPC$messages_Messages.messages.size(); i4++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i4);
                TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
                if (tLRPC$MessageAction != null && (tLRPC$Photo = tLRPC$MessageAction.photo) != null) {
                    tLRPC$TL_photos_photos.photos.add(tLRPC$Photo);
                    arrayList.add(tLRPC$Message);
                }
            }
            processLoadedUserPhotos(tLRPC$TL_photos_photos, arrayList, j, i, i2, false, i3);
        }
    }

    public void blockPeer(long j) {
        TLRPC$Chat chat;
        TLRPC$User tLRPC$User = null;
        if (j > 0) {
            TLRPC$User user = getUser(Long.valueOf(j));
            if (user == null) {
                return;
            }
            chat = null;
            tLRPC$User = user;
        } else {
            chat = getChat(Long.valueOf(-j));
            if (chat == null) {
                return;
            }
        }
        if (this.blockePeers.indexOfKey(j) >= 0) {
            return;
        }
        this.blockePeers.put(j, 1);
        if (tLRPC$User != null) {
            if (tLRPC$User.bot) {
                getMediaDataController().removeInline(j);
            } else {
                getMediaDataController().removePeer(j);
            }
        }
        int i = this.totalBlockedCount;
        if (i >= 0) {
            this.totalBlockedCount = i + 1;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        TLRPC$TL_contacts_block tLRPC$TL_contacts_block = new TLRPC$TL_contacts_block();
        if (tLRPC$User != null) {
            tLRPC$TL_contacts_block.id = getInputPeer(tLRPC$User);
        } else {
            tLRPC$TL_contacts_block.id = getInputPeer(chat);
        }
        getConnectionsManager().sendRequest(tLRPC$TL_contacts_block, MessagesController$$ExternalSyntheticLambda370.INSTANCE);
    }

    public void setParticipantBannedRole(final long j, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, final boolean z, final BaseFragment baseFragment) {
        if ((tLRPC$User == null && tLRPC$Chat == null) || tLRPC$TL_chatBannedRights == null) {
            return;
        }
        final TLRPC$TL_channels_editBanned tLRPC$TL_channels_editBanned = new TLRPC$TL_channels_editBanned();
        tLRPC$TL_channels_editBanned.channel = getInputChannel(j);
        if (tLRPC$User != null) {
            tLRPC$TL_channels_editBanned.participant = getInputPeer(tLRPC$User);
        } else {
            tLRPC$TL_channels_editBanned.participant = getInputPeer(tLRPC$Chat);
        }
        tLRPC$TL_channels_editBanned.banned_rights = tLRPC$TL_chatBannedRights;
        getConnectionsManager().sendRequest(tLRPC$TL_channels_editBanned, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda316
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$setParticipantBannedRole$74(j, baseFragment, tLRPC$TL_channels_editBanned, z, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$setParticipantBannedRole$74(final long j, final BaseFragment baseFragment, final TLRPC$TL_channels_editBanned tLRPC$TL_channels_editBanned, final boolean z, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda68
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$setParticipantBannedRole$72(j);
                }
            }, 1000L);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda172
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$setParticipantBannedRole$73(tLRPC$TL_error, baseFragment, tLRPC$TL_channels_editBanned, z);
            }
        });
    }

    public /* synthetic */ void lambda$setParticipantBannedRole$72(long j) {
        loadFullChat(j, 0, true);
    }

    public /* synthetic */ void lambda$setParticipantBannedRole$73(TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_channels_editBanned tLRPC$TL_channels_editBanned, boolean z) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_channels_editBanned, Boolean.valueOf(z));
    }

    public void setChannelSlowMode(final long j, int i) {
        TLRPC$TL_channels_toggleSlowMode tLRPC$TL_channels_toggleSlowMode = new TLRPC$TL_channels_toggleSlowMode();
        tLRPC$TL_channels_toggleSlowMode.seconds = i;
        tLRPC$TL_channels_toggleSlowMode.channel = getInputChannel(j);
        getConnectionsManager().sendRequest(tLRPC$TL_channels_toggleSlowMode, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda293
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$setChannelSlowMode$76(j, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$setChannelSlowMode$76(final long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda63
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$setChannelSlowMode$75(j);
                }
            }, 1000L);
        }
    }

    public /* synthetic */ void lambda$setChannelSlowMode$75(long j) {
        loadFullChat(j, 0, true);
    }

    public void setDefaultBannedRole(final long j, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, final boolean z, final BaseFragment baseFragment) {
        if (tLRPC$TL_chatBannedRights == null) {
            return;
        }
        final TLRPC$TL_messages_editChatDefaultBannedRights tLRPC$TL_messages_editChatDefaultBannedRights = new TLRPC$TL_messages_editChatDefaultBannedRights();
        tLRPC$TL_messages_editChatDefaultBannedRights.peer = getInputPeer(-j);
        tLRPC$TL_messages_editChatDefaultBannedRights.banned_rights = tLRPC$TL_chatBannedRights;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_editChatDefaultBannedRights, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda317
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$setDefaultBannedRole$79(j, baseFragment, tLRPC$TL_messages_editChatDefaultBannedRights, z, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$setDefaultBannedRole$79(final long j, final BaseFragment baseFragment, final TLRPC$TL_messages_editChatDefaultBannedRights tLRPC$TL_messages_editChatDefaultBannedRights, final boolean z, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda62
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$setDefaultBannedRole$77(j);
                }
            }, 1000L);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda176
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$setDefaultBannedRole$78(tLRPC$TL_error, baseFragment, tLRPC$TL_messages_editChatDefaultBannedRights, z);
            }
        });
    }

    public /* synthetic */ void lambda$setDefaultBannedRole$77(long j) {
        loadFullChat(j, 0, true);
    }

    public /* synthetic */ void lambda$setDefaultBannedRole$78(TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_messages_editChatDefaultBannedRights tLRPC$TL_messages_editChatDefaultBannedRights, boolean z) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_messages_editChatDefaultBannedRights, Boolean.valueOf(z));
    }

    public void setUserAdminRole(long j, TLRPC$User tLRPC$User, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str, boolean z, BaseFragment baseFragment, boolean z2, boolean z3, String str2, Runnable runnable) {
        setUserAdminRole(j, tLRPC$User, tLRPC$TL_chatAdminRights, str, z, baseFragment, z2, z3, str2, runnable, null);
    }

    public void setUserAdminRole(final long j, TLRPC$User tLRPC$User, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str, final boolean z, final BaseFragment baseFragment, boolean z2, boolean z3, String str2, final Runnable runnable, final ErrorDelegate errorDelegate) {
        if (tLRPC$User == null || tLRPC$TL_chatAdminRights == null) {
            return;
        }
        TLRPC$Chat chat = getChat(Long.valueOf(j));
        if (ChatObject.isChannel(chat)) {
            final TLRPC$TL_channels_editAdmin tLRPC$TL_channels_editAdmin = new TLRPC$TL_channels_editAdmin();
            tLRPC$TL_channels_editAdmin.channel = getInputChannel(chat);
            tLRPC$TL_channels_editAdmin.user_id = getInputUser(tLRPC$User);
            tLRPC$TL_channels_editAdmin.admin_rights = tLRPC$TL_chatAdminRights;
            tLRPC$TL_channels_editAdmin.rank = str;
            final RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda308
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$setUserAdminRole$83(j, runnable, baseFragment, tLRPC$TL_channels_editAdmin, z, errorDelegate, tLObject, tLRPC$TL_error);
                }
            };
            if ((chat.megagroup && z2) || !TextUtils.isEmpty(str2)) {
                addUserToChat(j, tLRPC$User, 0, str2, baseFragment, true, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda163
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$setUserAdminRole$84(tLRPC$TL_channels_editAdmin, requestDelegate);
                    }
                }, errorDelegate);
                return;
            } else {
                getConnectionsManager().sendRequest(tLRPC$TL_channels_editAdmin, requestDelegate);
                return;
            }
        }
        final TLRPC$TL_messages_editChatAdmin tLRPC$TL_messages_editChatAdmin = new TLRPC$TL_messages_editChatAdmin();
        tLRPC$TL_messages_editChatAdmin.chat_id = j;
        tLRPC$TL_messages_editChatAdmin.user_id = getInputUser(tLRPC$User);
        tLRPC$TL_messages_editChatAdmin.is_admin = z3 || tLRPC$TL_chatAdminRights.change_info || tLRPC$TL_chatAdminRights.delete_messages || tLRPC$TL_chatAdminRights.ban_users || tLRPC$TL_chatAdminRights.invite_users || tLRPC$TL_chatAdminRights.pin_messages || tLRPC$TL_chatAdminRights.add_admins || tLRPC$TL_chatAdminRights.manage_call;
        final RequestDelegate requestDelegate2 = new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda309
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$setUserAdminRole$88(j, runnable, baseFragment, tLRPC$TL_messages_editChatAdmin, errorDelegate, tLObject, tLRPC$TL_error);
            }
        };
        if (tLRPC$TL_messages_editChatAdmin.is_admin || z2 || !TextUtils.isEmpty(str2)) {
            addUserToChat(j, tLRPC$User, 0, str2, baseFragment, true, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda179
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$setUserAdminRole$89(tLRPC$TL_messages_editChatAdmin, requestDelegate2);
                }
            }, errorDelegate);
        } else {
            getConnectionsManager().sendRequest(tLRPC$TL_messages_editChatAdmin, requestDelegate2);
        }
    }

    public /* synthetic */ void lambda$setUserAdminRole$83(final long j, final Runnable runnable, final BaseFragment baseFragment, final TLRPC$TL_channels_editAdmin tLRPC$TL_channels_editAdmin, final boolean z, final ErrorDelegate errorDelegate, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda81
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$setUserAdminRole$80(j, runnable);
                }
            }, 1000L);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda171
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$setUserAdminRole$81(tLRPC$TL_error, baseFragment, tLRPC$TL_channels_editAdmin, z);
            }
        });
        if (errorDelegate == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.ErrorDelegate.this.run(tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$setUserAdminRole$80(long j, Runnable runnable) {
        loadFullChat(j, 0, true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$setUserAdminRole$81(TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_channels_editAdmin tLRPC$TL_channels_editAdmin, boolean z) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_channels_editAdmin, Boolean.valueOf(z));
    }

    public /* synthetic */ void lambda$setUserAdminRole$84(TLRPC$TL_channels_editAdmin tLRPC$TL_channels_editAdmin, RequestDelegate requestDelegate) {
        getConnectionsManager().sendRequest(tLRPC$TL_channels_editAdmin, requestDelegate);
    }

    public /* synthetic */ void lambda$setUserAdminRole$88(final long j, final Runnable runnable, final BaseFragment baseFragment, final TLRPC$TL_messages_editChatAdmin tLRPC$TL_messages_editChatAdmin, final ErrorDelegate errorDelegate, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda82
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$setUserAdminRole$85(j, runnable);
                }
            }, 1000L);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda175
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$setUserAdminRole$86(tLRPC$TL_error, baseFragment, tLRPC$TL_messages_editChatAdmin);
            }
        });
        if (errorDelegate == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.ErrorDelegate.this.run(tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$setUserAdminRole$85(long j, Runnable runnable) {
        loadFullChat(j, 0, true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$setUserAdminRole$86(TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_messages_editChatAdmin tLRPC$TL_messages_editChatAdmin) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_messages_editChatAdmin, Boolean.FALSE);
    }

    public /* synthetic */ void lambda$setUserAdminRole$89(TLRPC$TL_messages_editChatAdmin tLRPC$TL_messages_editChatAdmin, RequestDelegate requestDelegate) {
        getConnectionsManager().sendRequest(tLRPC$TL_messages_editChatAdmin, requestDelegate);
    }

    public void unblockPeer(long j) {
        TLRPC$Chat chat;
        TLRPC$TL_contacts_unblock tLRPC$TL_contacts_unblock = new TLRPC$TL_contacts_unblock();
        TLRPC$User tLRPC$User = null;
        if (j > 0) {
            TLRPC$User user = getUser(Long.valueOf(j));
            if (user == null) {
                return;
            }
            chat = null;
            tLRPC$User = user;
        } else {
            chat = getChat(Long.valueOf(-j));
            if (chat == null) {
                return;
            }
        }
        this.totalBlockedCount--;
        this.blockePeers.delete(j);
        if (tLRPC$User != null) {
            tLRPC$TL_contacts_unblock.id = getInputPeer(tLRPC$User);
        } else {
            tLRPC$TL_contacts_unblock.id = getInputPeer(chat);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        getConnectionsManager().sendRequest(tLRPC$TL_contacts_unblock, MessagesController$$ExternalSyntheticLambda356.INSTANCE);
    }

    public void getBlockedPeers(final boolean z) {
        if (!getUserConfig().isClientActivated() || this.loadingBlockedPeers) {
            return;
        }
        this.loadingBlockedPeers = true;
        final TLRPC$TL_contacts_getBlocked tLRPC$TL_contacts_getBlocked = new TLRPC$TL_contacts_getBlocked();
        tLRPC$TL_contacts_getBlocked.offset = z ? 0 : this.blockePeers.size();
        tLRPC$TL_contacts_getBlocked.limit = z ? 20 : 100;
        getConnectionsManager().sendRequest(tLRPC$TL_contacts_getBlocked, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda347
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$getBlockedPeers$92(z, tLRPC$TL_contacts_getBlocked, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$getBlockedPeers$92(final boolean z, final TLRPC$TL_contacts_getBlocked tLRPC$TL_contacts_getBlocked, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda149
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getBlockedPeers$91(tLObject, z, tLRPC$TL_contacts_getBlocked);
            }
        });
    }

    public /* synthetic */ void lambda$getBlockedPeers$91(TLObject tLObject, boolean z, TLRPC$TL_contacts_getBlocked tLRPC$TL_contacts_getBlocked) {
        if (tLObject != null) {
            TLRPC$contacts_Blocked tLRPC$contacts_Blocked = (TLRPC$contacts_Blocked) tLObject;
            putUsers(tLRPC$contacts_Blocked.users, false);
            putChats(tLRPC$contacts_Blocked.chats, false);
            getMessagesStorage().putUsersAndChats(tLRPC$contacts_Blocked.users, tLRPC$contacts_Blocked.chats, true, true);
            if (z) {
                this.blockePeers.clear();
            }
            this.totalBlockedCount = Math.max(tLRPC$contacts_Blocked.count, tLRPC$contacts_Blocked.blocked.size());
            this.blockedEndReached = tLRPC$contacts_Blocked.blocked.size() < tLRPC$TL_contacts_getBlocked.limit;
            int size = tLRPC$contacts_Blocked.blocked.size();
            for (int i = 0; i < size; i++) {
                this.blockePeers.put(MessageObject.getPeerId(tLRPC$contacts_Blocked.blocked.get(i).peer_id), 1);
            }
            this.loadingBlockedPeers = false;
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
    }

    public void deleteUserPhoto(TLRPC$InputPhoto tLRPC$InputPhoto) {
        if (tLRPC$InputPhoto == null) {
            TLRPC$TL_photos_updateProfilePhoto tLRPC$TL_photos_updateProfilePhoto = new TLRPC$TL_photos_updateProfilePhoto();
            tLRPC$TL_photos_updateProfilePhoto.id = new TLRPC$TL_inputPhotoEmpty();
            getUserConfig().getCurrentUser().photo = new TLRPC$TL_userProfilePhotoEmpty();
            TLRPC$User user = getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
            }
            if (user == null) {
                return;
            }
            user.photo = getUserConfig().getCurrentUser().photo;
            getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_ALL));
            getConnectionsManager().sendRequest(tLRPC$TL_photos_updateProfilePhoto, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda255
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$deleteUserPhoto$94(tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        TLRPC$TL_photos_deletePhotos tLRPC$TL_photos_deletePhotos = new TLRPC$TL_photos_deletePhotos();
        tLRPC$TL_photos_deletePhotos.id.add(tLRPC$InputPhoto);
        getConnectionsManager().sendRequest(tLRPC$TL_photos_deletePhotos, MessagesController$$ExternalSyntheticLambda355.INSTANCE);
    }

    public /* synthetic */ void lambda$deleteUserPhoto$94(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_photos_photo tLRPC$TL_photos_photo = (TLRPC$TL_photos_photo) tLObject;
            TLRPC$User user = getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                putUser(user, false);
            } else {
                getUserConfig().setCurrentUser(user);
            }
            if (user == null) {
                return;
            }
            getMessagesStorage().clearUserPhotos(user.id);
            ArrayList<TLRPC$User> arrayList = new ArrayList<>();
            arrayList.add(user);
            getMessagesStorage().putUsersAndChats(arrayList, null, false, true);
            if (tLRPC$TL_photos_photo.photo instanceof TLRPC$TL_photo) {
                TLRPC$TL_userProfilePhoto tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto();
                user.photo = tLRPC$TL_userProfilePhoto;
                tLRPC$TL_userProfilePhoto.has_video = !tLRPC$TL_photos_photo.photo.video_sizes.isEmpty();
                TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = user.photo;
                TLRPC$Photo tLRPC$Photo = tLRPC$TL_photos_photo.photo;
                tLRPC$UserProfilePhoto.photo_id = tLRPC$Photo.id;
                tLRPC$UserProfilePhoto.photo_small = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, 150).location;
                user.photo.photo_big = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_photos_photo.photo.sizes, 800).location;
                user.photo.dc_id = tLRPC$TL_photos_photo.photo.dc_id;
            } else {
                user.photo = new TLRPC$TL_userProfilePhotoEmpty();
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$deleteUserPhoto$93();
                }
            });
        }
    }

    public /* synthetic */ void lambda$deleteUserPhoto$93() {
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_ALL));
        getUserConfig().saveConfig(true);
    }

    public void processLoadedUserPhotos(final TLRPC$photos_Photos tLRPC$photos_Photos, final ArrayList<TLRPC$Message> arrayList, final long j, final int i, int i2, final boolean z, final int i3) {
        if (!z) {
            getMessagesStorage().putUsersAndChats(tLRPC$photos_Photos.users, null, true, true);
            getMessagesStorage().putDialogPhotos(j, tLRPC$photos_Photos, arrayList);
        } else if (tLRPC$photos_Photos == null || tLRPC$photos_Photos.photos.isEmpty()) {
            loadDialogPhotos(j, i, i2, false, i3);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda202
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processLoadedUserPhotos$96(tLRPC$photos_Photos, z, j, i, i3, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedUserPhotos$96(TLRPC$photos_Photos tLRPC$photos_Photos, boolean z, long j, int i, int i2, ArrayList arrayList) {
        putUsers(tLRPC$photos_Photos.users, z);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogPhotosLoaded, Long.valueOf(j), Integer.valueOf(i), Boolean.valueOf(z), Integer.valueOf(i2), tLRPC$photos_Photos.photos, arrayList);
    }

    public void uploadAndApplyUserAvatar(TLRPC$FileLocation tLRPC$FileLocation) {
        if (tLRPC$FileLocation == null) {
            return;
        }
        this.uploadingAvatar = FileLoader.getDirectory(4) + "/" + tLRPC$FileLocation.volume_id + "_" + tLRPC$FileLocation.local_id + ".jpg";
        getFileLoader().uploadFile(this.uploadingAvatar, false, true, 16777216);
    }

    public void saveTheme(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, boolean z, boolean z2) {
        TLRPC$TL_theme tLRPC$TL_theme = themeAccent != null ? themeAccent.info : themeInfo.info;
        if (tLRPC$TL_theme != null) {
            TLRPC$TL_account_saveTheme tLRPC$TL_account_saveTheme = new TLRPC$TL_account_saveTheme();
            TLRPC$TL_inputTheme tLRPC$TL_inputTheme = new TLRPC$TL_inputTheme();
            tLRPC$TL_inputTheme.id = tLRPC$TL_theme.id;
            tLRPC$TL_inputTheme.access_hash = tLRPC$TL_theme.access_hash;
            tLRPC$TL_account_saveTheme.theme = tLRPC$TL_inputTheme;
            tLRPC$TL_account_saveTheme.unsave = z2;
            getConnectionsManager().sendRequest(tLRPC$TL_account_saveTheme, MessagesController$$ExternalSyntheticLambda368.INSTANCE);
            getConnectionsManager().resumeNetworkMaybe();
        }
        if (!z2) {
            installTheme(themeInfo, themeAccent, z);
        }
    }

    public void installTheme(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, boolean z) {
        TLRPC$TL_theme tLRPC$TL_theme = themeAccent != null ? themeAccent.info : themeInfo.info;
        String str = themeAccent != null ? themeAccent.patternSlug : themeInfo.slug;
        boolean z2 = themeAccent == null && themeInfo.isBlured;
        boolean z3 = themeAccent != null ? themeAccent.patternMotion : themeInfo.isMotion;
        TLRPC$TL_account_installTheme tLRPC$TL_account_installTheme = new TLRPC$TL_account_installTheme();
        tLRPC$TL_account_installTheme.dark = z;
        if (tLRPC$TL_theme != null) {
            tLRPC$TL_account_installTheme.format = "android";
            TLRPC$TL_inputTheme tLRPC$TL_inputTheme = new TLRPC$TL_inputTheme();
            tLRPC$TL_inputTheme.id = tLRPC$TL_theme.id;
            tLRPC$TL_inputTheme.access_hash = tLRPC$TL_theme.access_hash;
            tLRPC$TL_account_installTheme.theme = tLRPC$TL_inputTheme;
            tLRPC$TL_account_installTheme.flags |= 2;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_account_installTheme, MessagesController$$ExternalSyntheticLambda365.INSTANCE);
        if (!TextUtils.isEmpty(str)) {
            TLRPC$TL_account_installWallPaper tLRPC$TL_account_installWallPaper = new TLRPC$TL_account_installWallPaper();
            TLRPC$TL_inputWallPaperSlug tLRPC$TL_inputWallPaperSlug = new TLRPC$TL_inputWallPaperSlug();
            tLRPC$TL_inputWallPaperSlug.slug = str;
            tLRPC$TL_account_installWallPaper.wallpaper = tLRPC$TL_inputWallPaperSlug;
            TLRPC$TL_wallPaperSettings tLRPC$TL_wallPaperSettings = new TLRPC$TL_wallPaperSettings();
            tLRPC$TL_account_installWallPaper.settings = tLRPC$TL_wallPaperSettings;
            tLRPC$TL_wallPaperSettings.blur = z2;
            tLRPC$TL_wallPaperSettings.motion = z3;
            getConnectionsManager().sendRequest(tLRPC$TL_account_installWallPaper, MessagesController$$ExternalSyntheticLambda369.INSTANCE);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void saveThemeToServer(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent themeAccent) {
        String str;
        File file;
        if (themeInfo == null) {
            return;
        }
        if (themeAccent != 0) {
            str = themeAccent.saveToFile().getAbsolutePath();
            file = themeAccent.getPathToWallpaper();
        } else {
            str = themeInfo.pathToFile;
            file = null;
        }
        final String str2 = str;
        final File file2 = file;
        if (str2 == null || this.uploadingThemes.containsKey(str2)) {
            return;
        }
        this.uploadingThemes.put(str2, themeAccent != 0 ? themeAccent : themeInfo);
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda106
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$saveThemeToServer$101(str2, file2, themeAccent, themeInfo);
            }
        });
    }

    public /* synthetic */ void lambda$saveThemeToServer$101(final String str, File file, final Theme.ThemeAccent themeAccent, final Theme.ThemeInfo themeInfo) {
        final String createThemePreviewImage = Theme.createThemePreviewImage(str, file != null ? file.getAbsolutePath() : null, themeAccent);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda107
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$saveThemeToServer$100(createThemePreviewImage, str, themeAccent, themeInfo);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$saveThemeToServer$100(String str, String str2, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo) {
        if (str == null) {
            this.uploadingThemes.remove(str2);
            return;
        }
        this.uploadingThemes.put(str, themeAccent != null ? themeAccent : themeInfo);
        if (themeAccent == null) {
            themeInfo.uploadingFile = str2;
            themeInfo.uploadingThumb = str;
        } else {
            themeAccent.uploadingFile = str2;
            themeAccent.uploadingThumb = str;
        }
        getFileLoader().uploadFile(str2, false, true, 67108864);
        getFileLoader().uploadFile(str, false, true, 16777216);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:156:0x0168  */
    /* JADX WARN: Removed duplicated region for block: B:157:0x016e  */
    /* JADX WARN: Removed duplicated region for block: B:160:0x0184  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x0194  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x01bd  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x01d3  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x01e9  */
    /* JADX WARN: Removed duplicated region for block: B:175:0x01f7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void saveWallpaperToServer(java.io.File r10, org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo r11, boolean r12, final long r13) {
        /*
            Method dump skipped, instructions count: 537
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.saveWallpaperToServer(java.io.File, org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo, boolean, long):void");
    }

    public /* synthetic */ void lambda$saveWallpaperToServer$102(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        getMessagesStorage().removePendingTask(j);
    }

    public void markDialogMessageAsDeleted(long j, ArrayList<Integer> arrayList) {
        ArrayList<MessageObject> arrayList2 = this.dialogMessage.get(j);
        if (arrayList2 != null) {
            for (int i = 0; i < arrayList2.size(); i++) {
                MessageObject messageObject = arrayList2.get(i);
                if (messageObject != null) {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= arrayList.size()) {
                            break;
                        } else if (messageObject.getId() == arrayList.get(i2).intValue()) {
                            messageObject.deleted = true;
                            break;
                        } else {
                            i2++;
                        }
                    }
                }
            }
        }
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, TLRPC$EncryptedChat tLRPC$EncryptedChat, long j, boolean z, boolean z2) {
        deleteMessages(arrayList, arrayList2, tLRPC$EncryptedChat, j, z, z2, false, 0L, null);
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, TLRPC$EncryptedChat tLRPC$EncryptedChat, long j, boolean z, boolean z2, boolean z3) {
        deleteMessages(arrayList, arrayList2, tLRPC$EncryptedChat, j, z, z2, z3, 0L, null);
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, TLRPC$EncryptedChat tLRPC$EncryptedChat, long j, boolean z, boolean z2, boolean z3, long j2, TLObject tLObject) {
        long j3;
        ArrayList<Integer> arrayList3;
        NativeByteBuffer nativeByteBuffer;
        NativeByteBuffer nativeByteBuffer2;
        final long createPendingTask;
        TLRPC$TL_messages_deleteMessages tLRPC$TL_messages_deleteMessages;
        NativeByteBuffer nativeByteBuffer3;
        long createPendingTask2;
        TLRPC$TL_channels_deleteMessages tLRPC$TL_channels_deleteMessages;
        NativeByteBuffer nativeByteBuffer4;
        final long createPendingTask3;
        TLRPC$TL_messages_deleteScheduledMessages tLRPC$TL_messages_deleteScheduledMessages;
        long j4;
        ArrayList<Integer> arrayList4;
        char c;
        if ((arrayList == null || arrayList.isEmpty()) && j2 == 0) {
            return;
        }
        if (j2 == 0) {
            if (j == 0 || !DialogObject.isChatDialog(j)) {
                j4 = 0;
            } else {
                TLRPC$Chat chat = getChat(Long.valueOf(-j));
                j4 = ChatObject.isChannel(chat) ? chat.id : 0L;
            }
            if (!z3) {
                ArrayList<Integer> arrayList5 = new ArrayList<>();
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    Integer num = arrayList.get(i);
                    if (num.intValue() > 0) {
                        arrayList5.add(num);
                    }
                }
                arrayList4 = arrayList5;
            } else {
                arrayList4 = null;
            }
            if (z2) {
                getMessagesStorage().markMessagesAsDeleted(j, arrayList, true, false, true);
                c = 1;
            } else {
                if (j4 == 0) {
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        MessageObject messageObject = this.dialogMessagesByIds.get(arrayList.get(i2).intValue());
                        if (messageObject != null) {
                            messageObject.deleted = true;
                        }
                    }
                } else {
                    markDialogMessageAsDeleted(j, arrayList);
                }
                getMessagesStorage().markMessagesAsDeleted(j, arrayList, true, z, false);
                c = 1;
                getMessagesStorage().updateDialogsWithDeletedMessages(j, j4, arrayList, null, true);
            }
            NotificationCenter notificationCenter = getNotificationCenter();
            int i3 = NotificationCenter.messagesDeleted;
            Object[] objArr = new Object[3];
            objArr[0] = arrayList;
            objArr[c] = Long.valueOf(j4);
            objArr[2] = Boolean.valueOf(z2);
            notificationCenter.postNotificationName(i3, objArr);
            j3 = j4;
            arrayList3 = arrayList4;
        } else {
            j3 = tLObject instanceof TLRPC$TL_channels_deleteMessages ? ((TLRPC$TL_channels_deleteMessages) tLObject).channel.channel_id : 0L;
            arrayList3 = null;
        }
        if (z3) {
            return;
        }
        if (z2) {
            if (tLObject instanceof TLRPC$TL_messages_deleteScheduledMessages) {
                tLRPC$TL_messages_deleteScheduledMessages = (TLRPC$TL_messages_deleteScheduledMessages) tLObject;
                createPendingTask3 = j2;
            } else {
                TLRPC$TL_messages_deleteScheduledMessages tLRPC$TL_messages_deleteScheduledMessages2 = new TLRPC$TL_messages_deleteScheduledMessages();
                tLRPC$TL_messages_deleteScheduledMessages2.id = arrayList3;
                tLRPC$TL_messages_deleteScheduledMessages2.peer = getInputPeer(j);
                try {
                    nativeByteBuffer4 = new NativeByteBuffer(tLRPC$TL_messages_deleteScheduledMessages2.getObjectSize() + 12);
                } catch (Exception e) {
                    e = e;
                    nativeByteBuffer4 = null;
                }
                try {
                    nativeByteBuffer4.writeInt32(24);
                    nativeByteBuffer4.writeInt64(j);
                    tLRPC$TL_messages_deleteScheduledMessages2.serializeToStream(nativeByteBuffer4);
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                    createPendingTask3 = getMessagesStorage().createPendingTask(nativeByteBuffer4);
                    tLRPC$TL_messages_deleteScheduledMessages = tLRPC$TL_messages_deleteScheduledMessages2;
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteScheduledMessages, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda288
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$deleteMessages$103(createPendingTask3, tLObject2, tLRPC$TL_error);
                        }
                    });
                }
                createPendingTask3 = getMessagesStorage().createPendingTask(nativeByteBuffer4);
                tLRPC$TL_messages_deleteScheduledMessages = tLRPC$TL_messages_deleteScheduledMessages2;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteScheduledMessages, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda288
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$deleteMessages$103(createPendingTask3, tLObject2, tLRPC$TL_error);
                }
            });
        } else if (j3 != 0) {
            if (tLObject != null) {
                tLRPC$TL_channels_deleteMessages = (TLRPC$TL_channels_deleteMessages) tLObject;
                createPendingTask2 = j2;
            } else {
                TLRPC$TL_channels_deleteMessages tLRPC$TL_channels_deleteMessages2 = new TLRPC$TL_channels_deleteMessages();
                tLRPC$TL_channels_deleteMessages2.id = arrayList3;
                tLRPC$TL_channels_deleteMessages2.channel = getInputChannel(j3);
                try {
                    nativeByteBuffer3 = new NativeByteBuffer(tLRPC$TL_channels_deleteMessages2.getObjectSize() + 12);
                } catch (Exception e3) {
                    e = e3;
                    nativeByteBuffer3 = null;
                }
                try {
                    nativeByteBuffer3.writeInt32(24);
                    nativeByteBuffer3.writeInt64(j);
                    tLRPC$TL_channels_deleteMessages2.serializeToStream(nativeByteBuffer3);
                } catch (Exception e4) {
                    e = e4;
                    FileLog.e(e);
                    createPendingTask2 = getMessagesStorage().createPendingTask(nativeByteBuffer3);
                    tLRPC$TL_channels_deleteMessages = tLRPC$TL_channels_deleteMessages2;
                    final long j5 = j3;
                    final long j6 = createPendingTask2;
                    getConnectionsManager().sendRequest(tLRPC$TL_channels_deleteMessages, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda304
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$deleteMessages$104(j5, j6, tLObject2, tLRPC$TL_error);
                        }
                    });
                }
                createPendingTask2 = getMessagesStorage().createPendingTask(nativeByteBuffer3);
                tLRPC$TL_channels_deleteMessages = tLRPC$TL_channels_deleteMessages2;
            }
            final long j52 = j3;
            final long j62 = createPendingTask2;
            getConnectionsManager().sendRequest(tLRPC$TL_channels_deleteMessages, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda304
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$deleteMessages$104(j52, j62, tLObject2, tLRPC$TL_error);
                }
            });
        } else {
            if (arrayList2 == null || tLRPC$EncryptedChat == null || arrayList2.isEmpty()) {
                nativeByteBuffer = null;
            } else {
                nativeByteBuffer = null;
                getSecretChatHelper().sendMessagesDeleteMessage(tLRPC$EncryptedChat, arrayList2, null);
            }
            if (tLObject instanceof TLRPC$TL_messages_deleteMessages) {
                tLRPC$TL_messages_deleteMessages = (TLRPC$TL_messages_deleteMessages) tLObject;
                createPendingTask = j2;
            } else {
                TLRPC$TL_messages_deleteMessages tLRPC$TL_messages_deleteMessages2 = new TLRPC$TL_messages_deleteMessages();
                tLRPC$TL_messages_deleteMessages2.id = arrayList3;
                tLRPC$TL_messages_deleteMessages2.revoke = z;
                try {
                    nativeByteBuffer2 = new NativeByteBuffer(tLRPC$TL_messages_deleteMessages2.getObjectSize() + 12);
                } catch (Exception e5) {
                    e = e5;
                    nativeByteBuffer2 = nativeByteBuffer;
                }
                try {
                    nativeByteBuffer2.writeInt32(24);
                    nativeByteBuffer2.writeInt64(j);
                    tLRPC$TL_messages_deleteMessages2.serializeToStream(nativeByteBuffer2);
                } catch (Exception e6) {
                    e = e6;
                    FileLog.e(e);
                    createPendingTask = getMessagesStorage().createPendingTask(nativeByteBuffer2);
                    tLRPC$TL_messages_deleteMessages = tLRPC$TL_messages_deleteMessages2;
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteMessages, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda283
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$deleteMessages$105(createPendingTask, tLObject2, tLRPC$TL_error);
                        }
                    });
                }
                createPendingTask = getMessagesStorage().createPendingTask(nativeByteBuffer2);
                tLRPC$TL_messages_deleteMessages = tLRPC$TL_messages_deleteMessages2;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteMessages, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda283
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$deleteMessages$105(createPendingTask, tLObject2, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$deleteMessages$103(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            processUpdates((TLRPC$Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$deleteMessages$104(long j, long j2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_affectedMessages tLRPC$TL_messages_affectedMessages = (TLRPC$TL_messages_affectedMessages) tLObject;
            processNewChannelDifferenceParams(tLRPC$TL_messages_affectedMessages.pts, tLRPC$TL_messages_affectedMessages.pts_count, j);
        }
        if (j2 != 0) {
            getMessagesStorage().removePendingTask(j2);
        }
    }

    public /* synthetic */ void lambda$deleteMessages$105(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_affectedMessages tLRPC$TL_messages_affectedMessages = (TLRPC$TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tLRPC$TL_messages_affectedMessages.pts, -1, tLRPC$TL_messages_affectedMessages.pts_count);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void unpinAllMessages(final TLRPC$Chat tLRPC$Chat, final TLRPC$User tLRPC$User) {
        if (tLRPC$Chat == null && tLRPC$User == null) {
            return;
        }
        TLRPC$TL_messages_unpinAllMessages tLRPC$TL_messages_unpinAllMessages = new TLRPC$TL_messages_unpinAllMessages();
        tLRPC$TL_messages_unpinAllMessages.peer = getInputPeer(tLRPC$Chat != null ? -tLRPC$Chat.id : tLRPC$User.id);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_unpinAllMessages, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda329
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$unpinAllMessages$106(tLRPC$Chat, tLRPC$User, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$unpinAllMessages$106(TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_messages_affectedHistory tLRPC$TL_messages_affectedHistory = (TLRPC$TL_messages_affectedHistory) tLObject;
            if (ChatObject.isChannel(tLRPC$Chat)) {
                processNewChannelDifferenceParams(tLRPC$TL_messages_affectedHistory.pts, tLRPC$TL_messages_affectedHistory.pts_count, tLRPC$Chat.id);
            } else {
                processNewDifferenceParams(-1, tLRPC$TL_messages_affectedHistory.pts, -1, tLRPC$TL_messages_affectedHistory.pts_count);
            }
            new ArrayList();
            getMessagesStorage().updatePinnedMessages(tLRPC$Chat != null ? -tLRPC$Chat.id : tLRPC$User.id, null, false, 0, 0, false, null);
        }
    }

    public void pinMessage(final TLRPC$Chat tLRPC$Chat, final TLRPC$User tLRPC$User, final int i, final boolean z, boolean z2, boolean z3) {
        if (tLRPC$Chat == null && tLRPC$User == null) {
            return;
        }
        TLRPC$TL_messages_updatePinnedMessage tLRPC$TL_messages_updatePinnedMessage = new TLRPC$TL_messages_updatePinnedMessage();
        tLRPC$TL_messages_updatePinnedMessage.peer = getInputPeer(tLRPC$Chat != null ? -tLRPC$Chat.id : tLRPC$User.id);
        tLRPC$TL_messages_updatePinnedMessage.id = i;
        tLRPC$TL_messages_updatePinnedMessage.unpin = z;
        tLRPC$TL_messages_updatePinnedMessage.silent = !z3;
        tLRPC$TL_messages_updatePinnedMessage.pm_oneside = z2;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_updatePinnedMessage, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda279
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$pinMessage$107(i, tLRPC$Chat, tLRPC$User, z, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$pinMessage$107(int i, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            arrayList.add(Integer.valueOf(i));
            getMessagesStorage().updatePinnedMessages(tLRPC$Chat != null ? -tLRPC$Chat.id : tLRPC$User.id, arrayList, !z, -1, 0, false, null);
            processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public void deleteUserChannelHistory(final TLRPC$Chat tLRPC$Chat, final TLRPC$User tLRPC$User, final TLRPC$Chat tLRPC$Chat2, int i) {
        long j;
        if (tLRPC$User != null) {
            j = tLRPC$User.id;
        } else {
            j = tLRPC$Chat2 != null ? tLRPC$Chat2.id : 0L;
        }
        if (i == 0) {
            getMessagesStorage().deleteUserChatHistory(-tLRPC$Chat.id, j);
        }
        TLRPC$TL_channels_deleteParticipantHistory tLRPC$TL_channels_deleteParticipantHistory = new TLRPC$TL_channels_deleteParticipantHistory();
        tLRPC$TL_channels_deleteParticipantHistory.channel = getInputChannel(tLRPC$Chat);
        tLRPC$TL_channels_deleteParticipantHistory.participant = tLRPC$User != null ? getInputPeer(tLRPC$User) : getInputPeer(tLRPC$Chat2);
        getConnectionsManager().sendRequest(tLRPC$TL_channels_deleteParticipantHistory, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda330
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$deleteUserChannelHistory$108(tLRPC$Chat, tLRPC$User, tLRPC$Chat2, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$deleteUserChannelHistory$108(TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_affectedHistory tLRPC$TL_messages_affectedHistory = (TLRPC$TL_messages_affectedHistory) tLObject;
            int i = tLRPC$TL_messages_affectedHistory.offset;
            if (i > 0) {
                deleteUserChannelHistory(tLRPC$Chat, tLRPC$User, tLRPC$Chat2, i);
            }
            processNewChannelDifferenceParams(tLRPC$TL_messages_affectedHistory.pts, tLRPC$TL_messages_affectedHistory.pts_count, tLRPC$Chat.id);
        }
    }

    public ArrayList<TLRPC$Dialog> getAllDialogs() {
        return this.allDialogs;
    }

    public void putDialogsEndReachedAfterRegistration() {
        this.dialogsEndReached.put(0, true);
        this.serverDialogsEndReached.put(0, true);
    }

    public boolean isDialogsEndReached(int i) {
        return this.dialogsEndReached.get(i);
    }

    public boolean isLoadingDialogs(int i) {
        return this.loadingDialogs.get(i);
    }

    public boolean isServerDialogsEndReached(int i) {
        return this.serverDialogsEndReached.get(i);
    }

    public boolean hasHiddenArchive() {
        return SharedConfig.archiveHidden && this.dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null;
    }

    public ArrayList<TLRPC$Dialog> getDialogs(int i) {
        ArrayList<TLRPC$Dialog> arrayList = this.dialogsByFolder.get(i);
        return arrayList == null ? new ArrayList<>() : arrayList;
    }

    public int getAllFoldersDialogsCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.dialogsByFolder.size(); i2++) {
            SparseArray<ArrayList<TLRPC$Dialog>> sparseArray = this.dialogsByFolder;
            ArrayList<TLRPC$Dialog> arrayList = sparseArray.get(sparseArray.keyAt(i2));
            if (arrayList != null) {
                i += arrayList.size();
            }
        }
        return i;
    }

    public int getTotalDialogsCount() {
        ArrayList<TLRPC$Dialog> arrayList = this.dialogsByFolder.get(0);
        if (arrayList != null) {
            return 0 + arrayList.size();
        }
        return 0;
    }

    public void putAllNeededDraftDialogs() {
        LongSparseArray<SparseArray<TLRPC$DraftMessage>> drafts = getMediaDataController().getDrafts();
        int size = drafts.size();
        for (int i = 0; i < size; i++) {
            TLRPC$DraftMessage tLRPC$DraftMessage = drafts.valueAt(i).get(0);
            if (tLRPC$DraftMessage != null) {
                putDraftDialogIfNeed(drafts.keyAt(i), tLRPC$DraftMessage);
            }
        }
    }

    public void putDraftDialogIfNeed(long j, TLRPC$DraftMessage tLRPC$DraftMessage) {
        if (this.dialogs_dict.indexOfKey(j) < 0) {
            MediaDataController mediaDataController = getMediaDataController();
            int size = this.allDialogs.size();
            int i = 0;
            if (size > 0) {
                TLRPC$Dialog tLRPC$Dialog = this.allDialogs.get(size - 1);
                if (tLRPC$DraftMessage.date < DialogObject.getLastMessageOrDraftDate(tLRPC$Dialog, mediaDataController.getDraft(tLRPC$Dialog.id, 0))) {
                    return;
                }
            }
            TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
            tLRPC$TL_dialog.id = j;
            tLRPC$TL_dialog.draft = tLRPC$DraftMessage;
            tLRPC$TL_dialog.folder_id = mediaDataController.getDraftFolderId(j);
            if (j < 0 && ChatObject.isChannel(getChat(Long.valueOf(-j)))) {
                i = 1;
            }
            tLRPC$TL_dialog.flags = i;
            this.dialogs_dict.put(j, tLRPC$TL_dialog);
            this.allDialogs.add(tLRPC$TL_dialog);
            sortDialogs(null);
        }
    }

    public void removeDraftDialogIfNeed(long j) {
        TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
        if (tLRPC$Dialog == null || tLRPC$Dialog.top_message != 0) {
            return;
        }
        this.dialogs_dict.remove(tLRPC$Dialog.id);
        this.allDialogs.remove(tLRPC$Dialog);
    }

    private void removeDialog(TLRPC$Dialog tLRPC$Dialog) {
        if (tLRPC$Dialog == null) {
            return;
        }
        final long j = tLRPC$Dialog.id;
        if (this.dialogsServerOnly.remove(tLRPC$Dialog) && DialogObject.isChannel(tLRPC$Dialog)) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda66
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$removeDialog$109(j);
                }
            });
        }
        this.allDialogs.remove(tLRPC$Dialog);
        this.dialogsMyChannels.remove(tLRPC$Dialog);
        this.dialogsMyGroups.remove(tLRPC$Dialog);
        this.dialogsCanAddUsers.remove(tLRPC$Dialog);
        this.dialogsChannelsOnly.remove(tLRPC$Dialog);
        this.dialogsGroupsOnly.remove(tLRPC$Dialog);
        this.dialogsUsersOnly.remove(tLRPC$Dialog);
        this.dialogsForBlock.remove(tLRPC$Dialog);
        this.dialogsForward.remove(tLRPC$Dialog);
        int i = 0;
        while (true) {
            DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
            if (i >= dialogFilterArr.length) {
                break;
            }
            if (dialogFilterArr[i] != null) {
                dialogFilterArr[i].dialogs.remove(tLRPC$Dialog);
            }
            i++;
        }
        this.dialogs_dict.remove(j);
        ArrayList<TLRPC$Dialog> arrayList = this.dialogsByFolder.get(tLRPC$Dialog.folder_id);
        if (arrayList == null) {
            return;
        }
        arrayList.remove(tLRPC$Dialog);
    }

    public /* synthetic */ void lambda$removeDialog$109(long j) {
        long j2 = -j;
        this.channelsPts.delete(j2);
        this.shortPollChannels.delete(j2);
        this.needShortPollChannels.delete(j2);
        this.shortPollOnlines.delete(j2);
        this.needShortPollOnlines.delete(j2);
    }

    public void hidePromoDialog() {
        if (this.promoDialog == null) {
            return;
        }
        TLRPC$TL_help_hidePromoData tLRPC$TL_help_hidePromoData = new TLRPC$TL_help_hidePromoData();
        tLRPC$TL_help_hidePromoData.peer = getInputPeer(this.promoDialog.id);
        getConnectionsManager().sendRequest(tLRPC$TL_help_hidePromoData, MessagesController$$ExternalSyntheticLambda354.INSTANCE);
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$hidePromoDialog$111();
            }
        });
        removePromoDialog();
    }

    public /* synthetic */ void lambda$hidePromoDialog$111() {
        this.promoDialogId = 0L;
        this.proxyDialogAddress = null;
        this.nextPromoInfoCheckTime = getConnectionsManager().getCurrentTime() + 3600;
        getGlobalMainSettings().edit().putLong("proxy_dialog", this.promoDialogId).remove("proxyDialogAddress").putInt("nextPromoInfoCheckTime", this.nextPromoInfoCheckTime).commit();
    }

    public void deleteDialog(long j, int i) {
        deleteDialog(j, i, false);
    }

    public void deleteDialog(long j, int i, boolean z) {
        deleteDialog(j, 1, i, 0, z, null, 0L);
    }

    public void setDialogHistoryTTL(long j, int i) {
        TLRPC$UserFull tLRPC$UserFull;
        TLRPC$TL_messages_setHistoryTTL tLRPC$TL_messages_setHistoryTTL = new TLRPC$TL_messages_setHistoryTTL();
        tLRPC$TL_messages_setHistoryTTL.peer = getInputPeer(j);
        tLRPC$TL_messages_setHistoryTTL.period = i;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_setHistoryTTL, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda258
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$setDialogHistoryTTL$112(tLObject, tLRPC$TL_error);
            }
        });
        TLRPC$ChatFull tLRPC$ChatFull = null;
        if (j > 0) {
            tLRPC$UserFull = getUserFull(j);
            if (tLRPC$UserFull == null) {
                return;
            }
            tLRPC$UserFull.ttl_period = i;
            tLRPC$UserFull.flags |= 16384;
        } else {
            TLRPC$ChatFull chatFull = getChatFull(-j);
            if (chatFull == null) {
                return;
            }
            chatFull.ttl_period = i;
            if (chatFull instanceof TLRPC$TL_channelFull) {
                chatFull.flags |= 16777216;
            } else {
                chatFull.flags |= 16384;
            }
            tLRPC$UserFull = null;
            tLRPC$ChatFull = chatFull;
        }
        if (tLRPC$ChatFull != null) {
            NotificationCenter notificationCenter = getNotificationCenter();
            int i2 = NotificationCenter.chatInfoDidLoad;
            Boolean bool = Boolean.FALSE;
            notificationCenter.postNotificationName(i2, tLRPC$ChatFull, 0, bool, bool);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Long.valueOf(j), tLRPC$UserFull);
    }

    public /* synthetic */ void lambda$setDialogHistoryTTL$112(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public void setDialogsInTransaction(boolean z) {
        this.dialogsInTransaction = z;
        if (!z) {
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:411:0x03e0  */
    /* JADX WARN: Removed duplicated region for block: B:421:0x0419  */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v12, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r2v59 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void deleteDialog(final long r26, int r28, final int r29, int r30, final boolean r31, final org.telegram.tgnet.TLRPC$InputPeer r32, final long r33) {
        /*
            Method dump skipped, instructions count: 1137
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.deleteDialog(long, int, int, int, boolean, org.telegram.tgnet.TLRPC$InputPeer, long):void");
    }

    public /* synthetic */ void lambda$deleteDialog$113(long j, int i, boolean z, TLRPC$InputPeer tLRPC$InputPeer, long j2, int i2) {
        deleteDialog(j, 2, i, Math.max(0, i2), z, tLRPC$InputPeer, j2);
        checkIfFolderEmpty(1);
    }

    public /* synthetic */ void lambda$deleteDialog$114(long j) {
        getNotificationsController().removeNotificationsForDialog(j);
    }

    public /* synthetic */ void lambda$deleteDialog$115(final long j) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda65
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$deleteDialog$114(j);
            }
        });
    }

    public /* synthetic */ void lambda$deleteDialog$116(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        if (tLObject != null) {
            processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$deleteDialog$117(long j, long j2, int i, int i2, boolean z, TLRPC$InputPeer tLRPC$InputPeer, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_affectedHistory tLRPC$TL_messages_affectedHistory = (TLRPC$TL_messages_affectedHistory) tLObject;
            if (tLRPC$TL_messages_affectedHistory.offset > 0) {
                deleteDialog(j2, 0, i, i2, z, tLRPC$InputPeer, 0L);
            }
            processNewDifferenceParams(-1, tLRPC$TL_messages_affectedHistory.pts, -1, tLRPC$TL_messages_affectedHistory.pts_count);
            getMessagesStorage().onDeleteQueryComplete(j2);
        }
    }

    public void saveGif(final Object obj, TLRPC$Document tLRPC$Document) {
        if (obj == null || !MessageObject.isGifDocument(tLRPC$Document)) {
            return;
        }
        final TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif = new TLRPC$TL_messages_saveGif();
        TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
        tLRPC$TL_messages_saveGif.id = tLRPC$TL_inputDocument;
        tLRPC$TL_inputDocument.id = tLRPC$Document.id;
        tLRPC$TL_inputDocument.access_hash = tLRPC$Document.access_hash;
        byte[] bArr = tLRPC$Document.file_reference;
        tLRPC$TL_inputDocument.file_reference = bArr;
        if (bArr == null) {
            tLRPC$TL_inputDocument.file_reference = new byte[0];
        }
        tLRPC$TL_messages_saveGif.unsave = false;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_saveGif, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda322
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$saveGif$118(obj, tLRPC$TL_messages_saveGif, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$saveGif$118(Object obj, TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null || !FileRefController.isFileRefError(tLRPC$TL_error.text)) {
            return;
        }
        getFileRefController().requestReference(obj, tLRPC$TL_messages_saveGif);
    }

    public void saveRecentSticker(final Object obj, TLRPC$Document tLRPC$Document, boolean z) {
        if (obj == null || tLRPC$Document == null) {
            return;
        }
        final TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker = new TLRPC$TL_messages_saveRecentSticker();
        TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
        tLRPC$TL_messages_saveRecentSticker.id = tLRPC$TL_inputDocument;
        tLRPC$TL_inputDocument.id = tLRPC$Document.id;
        tLRPC$TL_inputDocument.access_hash = tLRPC$Document.access_hash;
        byte[] bArr = tLRPC$Document.file_reference;
        tLRPC$TL_inputDocument.file_reference = bArr;
        if (bArr == null) {
            tLRPC$TL_inputDocument.file_reference = new byte[0];
        }
        tLRPC$TL_messages_saveRecentSticker.unsave = false;
        tLRPC$TL_messages_saveRecentSticker.attached = z;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_saveRecentSticker, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda323
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$saveRecentSticker$119(obj, tLRPC$TL_messages_saveRecentSticker, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$saveRecentSticker$119(Object obj, TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null || !FileRefController.isFileRefError(tLRPC$TL_error.text)) {
            return;
        }
        getFileRefController().requestReference(obj, tLRPC$TL_messages_saveRecentSticker);
    }

    public void loadChannelParticipants(final Long l) {
        if (this.loadingFullParticipants.contains(l) || this.loadedFullParticipants.contains(l)) {
            return;
        }
        this.loadingFullParticipants.add(l);
        TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
        tLRPC$TL_channels_getParticipants.channel = getInputChannel(l.longValue());
        tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
        tLRPC$TL_channels_getParticipants.offset = 0;
        tLRPC$TL_channels_getParticipants.limit = 32;
        getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipants, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda321
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadChannelParticipants$121(l, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadChannelParticipants$121(final Long l, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda169
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadChannelParticipants$120(tLRPC$TL_error, tLObject, l);
            }
        });
    }

    public /* synthetic */ void lambda$loadChannelParticipants$120(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, Long l) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            putUsers(tLRPC$TL_channels_channelParticipants.users, false);
            putChats(tLRPC$TL_channels_channelParticipants.chats, false);
            getMessagesStorage().putUsersAndChats(tLRPC$TL_channels_channelParticipants.users, tLRPC$TL_channels_channelParticipants.chats, true, true);
            getMessagesStorage().updateChannelUsers(l.longValue(), tLRPC$TL_channels_channelParticipants.participants);
            this.loadedFullParticipants.add(l);
        }
        this.loadingFullParticipants.remove(l);
    }

    public void putChatFull(TLRPC$ChatFull tLRPC$ChatFull) {
        this.fullChats.put(tLRPC$ChatFull.id, tLRPC$ChatFull);
    }

    public void processChatInfo(final long j, final TLRPC$ChatFull tLRPC$ChatFull, final ArrayList<TLRPC$User> arrayList, final boolean z, final boolean z2, final boolean z3, final ArrayList<Integer> arrayList2, final HashMap<Integer, MessageObject> hashMap, final int i, final boolean z4) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda214
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processChatInfo$122(z, j, z3, z2, tLRPC$ChatFull, arrayList, arrayList2, hashMap, i, z4);
            }
        });
    }

    public /* synthetic */ void lambda$processChatInfo$122(boolean z, long j, boolean z2, boolean z3, TLRPC$ChatFull tLRPC$ChatFull, ArrayList arrayList, ArrayList arrayList2, HashMap hashMap, int i, boolean z4) {
        if (z && j > 0 && !z2) {
            loadFullChat(j, 0, z3);
        }
        if (tLRPC$ChatFull != null) {
            if (this.fullChats.get(j) == null) {
                this.fullChats.put(j, tLRPC$ChatFull);
            }
            putUsers(arrayList, z);
            if (tLRPC$ChatFull.stickerset != null) {
                getMediaDataController().getGroupStickerSetById(tLRPC$ChatFull.stickerset);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, tLRPC$ChatFull, 0, Boolean.valueOf(z2), Boolean.FALSE);
        }
        if (arrayList2 != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.pinnedInfoDidLoad, Long.valueOf(-j), arrayList2, hashMap, Integer.valueOf(i), Boolean.valueOf(z4));
        }
    }

    public void loadUserInfo(TLRPC$User tLRPC$User, boolean z, int i) {
        loadUserInfo(tLRPC$User, z, i, 0);
    }

    public void loadUserInfo(TLRPC$User tLRPC$User, boolean z, int i, int i2) {
        getMessagesStorage().loadUserInfo(tLRPC$User, z, i, i2);
    }

    public void updateUsernameActiveness(TLObject tLObject, String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.objectsByUsernames.remove(str);
        if (!z) {
            return;
        }
        this.objectsByUsernames.put(str.toLowerCase(), tLObject);
    }

    public void processUserInfo(final TLRPC$User tLRPC$User, final TLRPC$UserFull tLRPC$UserFull, final boolean z, final boolean z2, final int i, final ArrayList<Integer> arrayList, final HashMap<Integer, MessageObject> hashMap, final int i2, final boolean z3) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda216
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processUserInfo$123(z, tLRPC$User, i, z2, tLRPC$UserFull, arrayList, hashMap, i2, z3);
            }
        });
    }

    public /* synthetic */ void lambda$processUserInfo$123(boolean z, TLRPC$User tLRPC$User, int i, boolean z2, TLRPC$UserFull tLRPC$UserFull, ArrayList arrayList, HashMap hashMap, int i2, boolean z3) {
        if (z) {
            loadFullUser(tLRPC$User, i, z2);
        }
        if (tLRPC$UserFull != null) {
            if (this.fullUsers.get(tLRPC$User.id) == null) {
                this.fullUsers.put(tLRPC$User.id, tLRPC$UserFull);
                int indexOfKey = this.blockePeers.indexOfKey(tLRPC$User.id);
                if (tLRPC$UserFull.blocked) {
                    if (indexOfKey < 0) {
                        this.blockePeers.put(tLRPC$User.id, 1);
                        getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
                    }
                } else if (indexOfKey >= 0) {
                    this.blockePeers.removeAt(indexOfKey);
                    getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Long.valueOf(tLRPC$User.id), tLRPC$UserFull);
        }
        if (arrayList != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.pinnedInfoDidLoad, Long.valueOf(tLRPC$User.id), arrayList, hashMap, Integer.valueOf(i2), Boolean.valueOf(z3));
        }
    }

    public void updateTimerProc() {
        int i;
        ArrayList arrayList;
        int i2;
        int i3;
        long currentTimeMillis = System.currentTimeMillis();
        int i4 = 0;
        checkDeletingTask(false);
        checkReadTasks();
        if (getUserConfig().isClientActivated()) {
            if (!this.ignoreSetOnline && getConnectionsManager().getPauseTime() == 0 && ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePausedStageQueue) {
                if (ApplicationLoader.mainInterfacePausedStageQueueTime != 0 && Math.abs(ApplicationLoader.mainInterfacePausedStageQueueTime - System.currentTimeMillis()) > 1000 && this.statusSettingState != 1 && (this.lastStatusUpdateTime == 0 || Math.abs(System.currentTimeMillis() - this.lastStatusUpdateTime) >= 55000 || this.offlineSent)) {
                    this.statusSettingState = 1;
                    if (this.statusRequest != 0) {
                        getConnectionsManager().cancelRequest(this.statusRequest, true);
                    }
                    TLRPC$TL_account_updateStatus tLRPC$TL_account_updateStatus = new TLRPC$TL_account_updateStatus();
                    tLRPC$TL_account_updateStatus.offline = false;
                    this.statusRequest = getConnectionsManager().sendRequest(tLRPC$TL_account_updateStatus, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda261
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$updateTimerProc$124(tLObject, tLRPC$TL_error);
                        }
                    });
                }
            } else if (this.statusSettingState != 2 && !this.offlineSent && Math.abs(System.currentTimeMillis() - getConnectionsManager().getPauseTime()) >= 2000) {
                this.statusSettingState = 2;
                if (this.statusRequest != 0) {
                    getConnectionsManager().cancelRequest(this.statusRequest, true);
                }
                TLRPC$TL_account_updateStatus tLRPC$TL_account_updateStatus2 = new TLRPC$TL_account_updateStatus();
                tLRPC$TL_account_updateStatus2.offline = true;
                this.statusRequest = getConnectionsManager().sendRequest(tLRPC$TL_account_updateStatus2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda244
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$updateTimerProc$125(tLObject, tLRPC$TL_error);
                    }
                });
            }
            if (this.updatesQueueChannels.size() != 0) {
                for (int i5 = 0; i5 < this.updatesQueueChannels.size(); i5++) {
                    long keyAt = this.updatesQueueChannels.keyAt(i5);
                    if (Math.abs(currentTimeMillis - this.updatesStartWaitTimeChannels.valueAt(i5)) >= 1500) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("QUEUE CHANNEL " + keyAt + " UPDATES WAIT TIMEOUT - CHECK QUEUE");
                        }
                        processChannelsUpdatesQueue(keyAt, 0);
                    }
                }
            }
            for (int i6 = 0; i6 < 3; i6++) {
                if (getUpdatesStartTime(i6) != 0 && Math.abs(currentTimeMillis - getUpdatesStartTime(i6)) >= 1500) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d(i6 + " QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
                    }
                    processUpdatesQueue(i6, 0);
                }
            }
        }
        final int currentTime = getConnectionsManager().getCurrentTime();
        if (Math.abs(System.currentTimeMillis() - this.lastViewsCheckTime) >= 5000) {
            this.lastViewsCheckTime = System.currentTimeMillis();
            if (this.channelViewsToSend.size() != 0) {
                int i7 = 0;
                while (i7 < this.channelViewsToSend.size()) {
                    final long keyAt2 = this.channelViewsToSend.keyAt(i7);
                    final TLRPC$TL_messages_getMessagesViews tLRPC$TL_messages_getMessagesViews = new TLRPC$TL_messages_getMessagesViews();
                    tLRPC$TL_messages_getMessagesViews.peer = getInputPeer(keyAt2);
                    tLRPC$TL_messages_getMessagesViews.id = this.channelViewsToSend.valueAt(i7);
                    tLRPC$TL_messages_getMessagesViews.increment = i7 == 0;
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessagesViews, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda314
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$updateTimerProc$127(keyAt2, tLRPC$TL_messages_getMessagesViews, tLObject, tLRPC$TL_error);
                        }
                    });
                    i7++;
                }
                this.channelViewsToSend.clear();
            }
            if (this.pollsToCheckSize > 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda43
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$updateTimerProc$129(currentTime);
                    }
                });
            }
        }
        if (!this.onlinePrivacy.isEmpty()) {
            ArrayList arrayList2 = null;
            for (Map.Entry<Long, Integer> entry : this.onlinePrivacy.entrySet()) {
                if (entry.getValue().intValue() < currentTime - 30) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(entry.getKey());
                }
            }
            if (arrayList2 != null) {
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    this.onlinePrivacy.remove((Long) it.next());
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda38
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$updateTimerProc$130();
                    }
                });
            }
        }
        if (this.shortPollChannels.size() != 0) {
            int i8 = 0;
            while (i8 < this.shortPollChannels.size()) {
                long keyAt3 = this.shortPollChannels.keyAt(i8);
                if (this.shortPollChannels.valueAt(i8) < System.currentTimeMillis() / 1000) {
                    this.shortPollChannels.delete(keyAt3);
                    i8--;
                    if (this.needShortPollChannels.indexOfKey(keyAt3) >= 0) {
                        getChannelDifference(keyAt3);
                    }
                }
                i8++;
            }
        }
        if (this.shortPollOnlines.size() != 0) {
            long elapsedRealtime = SystemClock.elapsedRealtime() / 1000;
            int i9 = 0;
            while (i9 < this.shortPollOnlines.size()) {
                final long keyAt4 = this.shortPollOnlines.keyAt(i9);
                if (this.shortPollOnlines.valueAt(i9) < elapsedRealtime) {
                    if (this.needShortPollChannels.indexOfKey(keyAt4) >= 0) {
                        this.shortPollOnlines.put(keyAt4, (int) (300 + elapsedRealtime));
                    } else {
                        this.shortPollOnlines.delete(keyAt4);
                        i9--;
                    }
                    TLRPC$TL_messages_getOnlines tLRPC$TL_messages_getOnlines = new TLRPC$TL_messages_getOnlines();
                    tLRPC$TL_messages_getOnlines.peer = getInputPeer(-keyAt4);
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_getOnlines, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda291
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$updateTimerProc$132(keyAt4, tLObject, tLRPC$TL_error);
                        }
                    });
                }
                i9++;
            }
        }
        if (!this.printingUsers.isEmpty() || this.lastPrintingStringCount != this.printingUsers.size()) {
            ArrayList arrayList3 = new ArrayList(this.printingUsers.keySet());
            int i10 = 0;
            boolean z = false;
            while (i10 < arrayList3.size()) {
                Long l = (Long) arrayList3.get(i10);
                ConcurrentHashMap<Integer, ArrayList<PrintingUser>> concurrentHashMap = this.printingUsers.get(l);
                if (concurrentHashMap != null) {
                    ArrayList arrayList4 = new ArrayList(concurrentHashMap.keySet());
                    int i11 = 0;
                    while (i11 < arrayList4.size()) {
                        Integer num = (Integer) arrayList4.get(i11);
                        ArrayList<PrintingUser> arrayList5 = concurrentHashMap.get(num);
                        if (arrayList5 != null) {
                            while (i4 < arrayList5.size()) {
                                PrintingUser printingUser = arrayList5.get(i4);
                                Long l2 = l;
                                ArrayList arrayList6 = arrayList3;
                                int i12 = i10;
                                if (printingUser.lastTime + (printingUser.action instanceof TLRPC$TL_sendMessageGamePlayAction ? 30000 : 5900) < currentTimeMillis) {
                                    arrayList5.remove(printingUser);
                                    i4--;
                                    i3 = 1;
                                    z = true;
                                } else {
                                    i3 = 1;
                                }
                                i4 += i3;
                                l = l2;
                                arrayList3 = arrayList6;
                                i10 = i12;
                            }
                        }
                        ArrayList arrayList7 = arrayList3;
                        int i13 = i10;
                        Long l3 = l;
                        if (arrayList5 == null || arrayList5.isEmpty()) {
                            concurrentHashMap.remove(num);
                            arrayList4.remove(i11);
                            i11--;
                        }
                        i11++;
                        l = l3;
                        arrayList3 = arrayList7;
                        i10 = i13;
                        i4 = 0;
                    }
                }
                ArrayList arrayList8 = arrayList3;
                int i14 = i10;
                Long l4 = l;
                if (concurrentHashMap == null || concurrentHashMap.isEmpty()) {
                    this.printingUsers.remove(l4);
                    arrayList = arrayList8;
                    arrayList.remove(i14);
                    i2 = i14 - 1;
                } else {
                    arrayList = arrayList8;
                    i2 = i14;
                }
                i10 = i2 + 1;
                arrayList3 = arrayList;
                i4 = 0;
            }
            i = 1;
            updatePrintingStrings();
            if (z) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda27
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$updateTimerProc$133();
                    }
                });
            }
        } else {
            i = 1;
        }
        if (Theme.selectedAutoNightType == i && Math.abs(currentTimeMillis - lastThemeCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.themeCheckRunnable);
            lastThemeCheckTime = currentTimeMillis;
        }
        if (getUserConfig().savedPasswordHash != null && Math.abs(currentTimeMillis - lastPasswordCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.passwordCheckRunnable);
            lastPasswordCheckTime = currentTimeMillis;
        }
        if (this.lastPushRegisterSendTime != 0 && Math.abs(SystemClock.elapsedRealtime() - this.lastPushRegisterSendTime) >= 10800000) {
            PushListenerController.sendRegistrationToServer(SharedConfig.pushType, SharedConfig.pushString);
        }
        getLocationController().update();
        lambda$checkPromoInfo$136(false);
        checkTosUpdate();
    }

    public /* synthetic */ void lambda$updateTimerProc$124(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            this.lastStatusUpdateTime = System.currentTimeMillis();
            this.offlineSent = false;
            this.statusSettingState = 0;
        } else {
            long j = this.lastStatusUpdateTime;
            if (j != 0) {
                this.lastStatusUpdateTime = j + 5000;
            }
        }
        this.statusRequest = 0;
    }

    public /* synthetic */ void lambda$updateTimerProc$125(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            this.offlineSent = true;
        } else {
            long j = this.lastStatusUpdateTime;
            if (j != 0) {
                this.lastStatusUpdateTime = j + 5000;
            }
        }
        this.statusRequest = 0;
    }

    public /* synthetic */ void lambda$updateTimerProc$127(long j, TLRPC$TL_messages_getMessagesViews tLRPC$TL_messages_getMessagesViews, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            final TLRPC$TL_messages_messageViews tLRPC$TL_messages_messageViews = (TLRPC$TL_messages_messageViews) tLObject;
            final LongSparseArray<SparseIntArray> longSparseArray = new LongSparseArray<>();
            final LongSparseArray<SparseIntArray> longSparseArray2 = new LongSparseArray<>();
            final LongSparseArray<SparseArray<TLRPC$MessageReplies>> longSparseArray3 = new LongSparseArray<>();
            SparseIntArray sparseIntArray = longSparseArray.get(j);
            SparseIntArray sparseIntArray2 = longSparseArray2.get(j);
            SparseArray<TLRPC$MessageReplies> sparseArray = longSparseArray3.get(j);
            for (int i = 0; i < tLRPC$TL_messages_getMessagesViews.id.size() && i < tLRPC$TL_messages_messageViews.views.size(); i++) {
                TLRPC$TL_messageViews tLRPC$TL_messageViews = tLRPC$TL_messages_messageViews.views.get(i);
                if ((1 & tLRPC$TL_messageViews.flags) != 0) {
                    if (sparseIntArray == null) {
                        sparseIntArray = new SparseIntArray();
                        longSparseArray.put(j, sparseIntArray);
                    }
                    sparseIntArray.put(tLRPC$TL_messages_getMessagesViews.id.get(i).intValue(), tLRPC$TL_messageViews.views);
                }
                if ((tLRPC$TL_messageViews.flags & 2) != 0) {
                    if (sparseIntArray2 == null) {
                        sparseIntArray2 = new SparseIntArray();
                        longSparseArray2.put(j, sparseIntArray2);
                    }
                    sparseIntArray2.put(tLRPC$TL_messages_getMessagesViews.id.get(i).intValue(), tLRPC$TL_messageViews.forwards);
                }
                if ((tLRPC$TL_messageViews.flags & 4) != 0) {
                    if (sparseArray == null) {
                        sparseArray = new SparseArray<>();
                        longSparseArray3.put(j, sparseArray);
                    }
                    sparseArray.put(tLRPC$TL_messages_getMessagesViews.id.get(i).intValue(), tLRPC$TL_messageViews.replies);
                }
            }
            getMessagesStorage().putUsersAndChats(tLRPC$TL_messages_messageViews.users, tLRPC$TL_messages_messageViews.chats, true, true);
            getMessagesStorage().putChannelViews(longSparseArray, longSparseArray2, longSparseArray3, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda180
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$updateTimerProc$126(tLRPC$TL_messages_messageViews, longSparseArray, longSparseArray2, longSparseArray3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$updateTimerProc$126(TLRPC$TL_messages_messageViews tLRPC$TL_messages_messageViews, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3) {
        putUsers(tLRPC$TL_messages_messageViews.users, false);
        putChats(tLRPC$TL_messages_messageViews.chats, false);
        getNotificationCenter().postNotificationName(NotificationCenter.didUpdateMessagesViews, longSparseArray, longSparseArray2, longSparseArray3, Boolean.FALSE);
    }

    /* JADX WARN: Removed duplicated region for block: B:66:0x0063  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0075  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$updateTimerProc$129(int r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            long r2 = android.os.SystemClock.elapsedRealtime()
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r4 = r0.pollsToCheck
            int r4 = r4.size()
            r6 = 2147483647(0x7fffffff, float:NaN)
            r7 = 0
        L12:
            if (r7 >= r4) goto Lcb
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r8 = r0.pollsToCheck
            java.lang.Object r8 = r8.valueAt(r7)
            android.util.SparseArray r8 = (android.util.SparseArray) r8
            if (r8 != 0) goto L21
        L1e:
            r5 = 1
            goto Lc8
        L21:
            int r10 = r8.size()
            r11 = 0
        L26:
            r12 = 1000(0x3e8, float:1.401E-42)
            if (r11 >= r10) goto L9e
            java.lang.Object r13 = r8.valueAt(r11)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            org.telegram.tgnet.TLRPC$Message r14 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r14 = r14.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r14 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r14
            r15 = 30000(0x7530, float:4.2039E-41)
            org.telegram.tgnet.TLRPC$Poll r14 = r14.poll
            int r5 = r14.close_date
            if (r5 == 0) goto L44
            boolean r14 = r14.closed
            if (r14 != 0) goto L44
            r14 = 1
            goto L45
        L44:
            r14 = 0
        L45:
            if (r14 == 0) goto L51
            if (r5 > r1) goto L4b
            r15 = r10
            goto L54
        L4b:
            int r5 = r5 - r1
            int r5 = java.lang.Math.min(r6, r5)
            r6 = r5
        L51:
            r15 = r10
            r12 = 30000(0x7530, float:4.2039E-41)
        L54:
            long r9 = r13.pollLastCheckTime
            long r9 = r2 - r9
            long r9 = java.lang.Math.abs(r9)
            r16 = r6
            long r5 = (long) r12
            int r12 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r12 >= 0) goto L75
            boolean r5 = r13.pollVisibleOnScreen
            if (r5 != 0) goto L98
            if (r14 != 0) goto L98
            int r5 = r13.getId()
            r8.remove(r5)
            int r10 = r15 + (-1)
            int r11 = r11 + (-1)
            goto L99
        L75:
            r13.pollLastCheckTime = r2
            org.telegram.tgnet.TLRPC$TL_messages_getPollResults r5 = new org.telegram.tgnet.TLRPC$TL_messages_getPollResults
            r5.<init>()
            long r9 = r13.getDialogId()
            org.telegram.tgnet.TLRPC$InputPeer r6 = r0.getInputPeer(r9)
            r5.peer = r6
            int r6 = r13.getId()
            r5.msg_id = r6
            org.telegram.tgnet.ConnectionsManager r6 = r17.getConnectionsManager()
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda345 r9 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda345
            r9.<init>()
            r6.sendRequest(r5, r9)
        L98:
            r10 = r15
        L99:
            r5 = 1
            int r11 = r11 + r5
            r6 = r16
            goto L26
        L9e:
            r9 = 5
            if (r6 >= r9) goto Lb3
            long r9 = r0.lastViewsCheckTime
            long r13 = java.lang.System.currentTimeMillis()
            int r11 = 5 - r6
            int r11 = r11 * 1000
            long r11 = (long) r11
            long r13 = r13 - r11
            long r9 = java.lang.Math.min(r9, r13)
            r0.lastViewsCheckTime = r9
        Lb3:
            int r8 = r8.size()
            if (r8 != 0) goto L1e
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r8 = r0.pollsToCheck
            long r9 = r8.keyAt(r7)
            r8.remove(r9)
            int r4 = r4 + (-1)
            int r7 = r7 + (-1)
            goto L1e
        Lc8:
            int r7 = r7 + r5
            goto L12
        Lcb:
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r1 = r0.pollsToCheck
            int r1 = r1.size()
            r0.pollsToCheckSize = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$updateTimerProc$129(int):void");
    }

    public /* synthetic */ void lambda$updateTimerProc$128(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$Poll tLRPC$Poll;
        if (tLRPC$TL_error == null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            if (z) {
                for (int i = 0; i < tLRPC$Updates.updates.size(); i++) {
                    TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
                    if ((tLRPC$Update instanceof TLRPC$TL_updateMessagePoll) && (tLRPC$Poll = ((TLRPC$TL_updateMessagePoll) tLRPC$Update).poll) != null && !tLRPC$Poll.closed) {
                        this.lastViewsCheckTime = System.currentTimeMillis() - 4000;
                    }
                }
            }
            processUpdates(tLRPC$Updates, false);
        }
    }

    public /* synthetic */ void lambda$updateTimerProc$130() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_STATUS));
    }

    public /* synthetic */ void lambda$updateTimerProc$132(final long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            final TLRPC$TL_chatOnlines tLRPC$TL_chatOnlines = (TLRPC$TL_chatOnlines) tLObject;
            getMessagesStorage().updateChatOnlineCount(j, tLRPC$TL_chatOnlines.onlines);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda91
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$updateTimerProc$131(j, tLRPC$TL_chatOnlines);
                }
            });
        }
    }

    public /* synthetic */ void lambda$updateTimerProc$131(long j, TLRPC$TL_chatOnlines tLRPC$TL_chatOnlines) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatOnlineCountDidLoad, Long.valueOf(j), Integer.valueOf(tLRPC$TL_chatOnlines.onlines));
    }

    public /* synthetic */ void lambda$updateTimerProc$133() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_USER_PRINT));
    }

    private void checkTosUpdate() {
        if (this.nextTosCheckTime > getConnectionsManager().getCurrentTime() || this.checkingTosUpdate || !getUserConfig().isClientActivated()) {
            return;
        }
        this.checkingTosUpdate = true;
        getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_help_getTermsOfServiceUpdate
            public static int constructor = NUM;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
                return TLRPC$help_TermsOfServiceUpdate.TLdeserialize(abstractSerializedData, i, z);
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda251
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$checkTosUpdate$135(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$checkTosUpdate$135(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.checkingTosUpdate = false;
        if (tLObject instanceof TLRPC$TL_help_termsOfServiceUpdateEmpty) {
            this.nextTosCheckTime = ((TLRPC$TL_help_termsOfServiceUpdateEmpty) tLObject).expires;
        } else if (tLObject instanceof TLRPC$TL_help_termsOfServiceUpdate) {
            final TLRPC$TL_help_termsOfServiceUpdate tLRPC$TL_help_termsOfServiceUpdate = (TLRPC$TL_help_termsOfServiceUpdate) tLObject;
            this.nextTosCheckTime = tLRPC$TL_help_termsOfServiceUpdate.expires;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda178
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$checkTosUpdate$134(tLRPC$TL_help_termsOfServiceUpdate);
                }
            });
        } else {
            this.nextTosCheckTime = getConnectionsManager().getCurrentTime() + 3600;
        }
        this.notificationsPreferences.edit().putInt("nextTosCheckTime", this.nextTosCheckTime).commit();
    }

    public /* synthetic */ void lambda$checkTosUpdate$134(TLRPC$TL_help_termsOfServiceUpdate tLRPC$TL_help_termsOfServiceUpdate) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, 4, tLRPC$TL_help_termsOfServiceUpdate.terms_of_service);
    }

    public void checkPromoInfo(final boolean z) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda211
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$checkPromoInfo$136(z);
            }
        });
    }

    /* renamed from: checkPromoInfoInternal */
    public void lambda$checkPromoInfo$136(boolean z) {
        String str;
        boolean z2 = false;
        if (z && this.checkingPromoInfo) {
            this.checkingPromoInfo = false;
        }
        if ((z || this.nextPromoInfoCheckTime <= getConnectionsManager().getCurrentTime()) && !this.checkingPromoInfo) {
            if (this.checkingPromoInfoRequestId != 0) {
                getConnectionsManager().cancelRequest(this.checkingPromoInfoRequestId, true);
                this.checkingPromoInfoRequestId = 0;
            }
            SharedPreferences globalMainSettings = getGlobalMainSettings();
            globalMainSettings.getBoolean("proxy_enabled", false);
            final String string = globalMainSettings.getString("proxy_ip", "");
            final String string2 = globalMainSettings.getString("proxy_secret", "");
            if (this.promoDialogId != 0 && this.promoDialogType == PROMO_TYPE_PROXY && (str = this.proxyDialogAddress) != null) {
                if (!str.equals(string + string2)) {
                    z2 = true;
                }
            }
            final int i = this.lastCheckPromoId + 1;
            this.lastCheckPromoId = i;
            this.checkingPromoInfo = true;
            this.checkingPromoInfoRequestId = getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_help_getPromoData
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i2, boolean z3) {
                    return TLRPC$help_PromoData.TLdeserialize(abstractSerializedData, i2, z3);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                    abstractSerializedData.writeInt32(constructor);
                }
            }, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda278
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$checkPromoInfoInternal$141(i, string, string2, tLObject, tLRPC$TL_error);
                }
            });
            if (!z2) {
                return;
            }
            this.promoDialogId = 0L;
            this.proxyDialogAddress = null;
            this.nextPromoInfoCheckTime = getConnectionsManager().getCurrentTime() + 3600;
            getGlobalMainSettings().edit().putLong("proxy_dialog", this.promoDialogId).remove("proxyDialogAddress").putInt("nextPromoInfoCheckTime", this.nextPromoInfoCheckTime).commit();
            AndroidUtilities.runOnUIThread(new MessagesController$$ExternalSyntheticLambda13(this));
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:98:0x0065, code lost:
        r2 = false;
     */
    /* JADX WARN: Removed duplicated region for block: B:114:0x009d  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x00a2  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x00f3  */
    /* JADX WARN: Removed duplicated region for block: B:122:0x00f7  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x0100  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x0104  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0111  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x0130  */
    /* JADX WARN: Removed duplicated region for block: B:139:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$checkPromoInfoInternal$141(final int r17, java.lang.String r18, java.lang.String r19, org.telegram.tgnet.TLObject r20, org.telegram.tgnet.TLRPC$TL_error r21) {
        /*
            Method dump skipped, instructions count: 348
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$checkPromoInfoInternal$141(int, java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$checkPromoInfoInternal$140(final long j, final TLRPC$TL_help_promoData tLRPC$TL_help_promoData, final int i) {
        TLRPC$Dialog tLRPC$Dialog = this.promoDialog;
        if (tLRPC$Dialog != null && j != tLRPC$Dialog.id) {
            removePromoDialog();
        }
        TLRPC$Dialog tLRPC$Dialog2 = this.dialogs_dict.get(j);
        this.promoDialog = tLRPC$Dialog2;
        if (tLRPC$Dialog2 != null) {
            this.checkingPromoInfo = false;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
            return;
        }
        LongSparseArray longSparseArray = new LongSparseArray();
        LongSparseArray longSparseArray2 = new LongSparseArray();
        for (int i2 = 0; i2 < tLRPC$TL_help_promoData.users.size(); i2++) {
            TLRPC$User tLRPC$User = tLRPC$TL_help_promoData.users.get(i2);
            longSparseArray.put(tLRPC$User.id, tLRPC$User);
        }
        for (int i3 = 0; i3 < tLRPC$TL_help_promoData.chats.size(); i3++) {
            TLRPC$Chat tLRPC$Chat = tLRPC$TL_help_promoData.chats.get(i3);
            longSparseArray2.put(tLRPC$Chat.id, tLRPC$Chat);
        }
        TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs = new TLRPC$TL_messages_getPeerDialogs();
        TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
        TLRPC$Peer tLRPC$Peer = tLRPC$TL_help_promoData.peer;
        if (tLRPC$Peer.user_id != 0) {
            TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
            tLRPC$TL_inputDialogPeer.peer = tLRPC$TL_inputPeerUser;
            long j2 = tLRPC$TL_help_promoData.peer.user_id;
            tLRPC$TL_inputPeerUser.user_id = j2;
            TLRPC$User tLRPC$User2 = (TLRPC$User) longSparseArray.get(j2);
            if (tLRPC$User2 != null) {
                tLRPC$TL_inputDialogPeer.peer.access_hash = tLRPC$User2.access_hash;
            }
        } else if (tLRPC$Peer.chat_id != 0) {
            TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
            tLRPC$TL_inputDialogPeer.peer = tLRPC$TL_inputPeerChat;
            long j3 = tLRPC$TL_help_promoData.peer.chat_id;
            tLRPC$TL_inputPeerChat.chat_id = j3;
            TLRPC$Chat tLRPC$Chat2 = (TLRPC$Chat) longSparseArray2.get(j3);
            if (tLRPC$Chat2 != null) {
                tLRPC$TL_inputDialogPeer.peer.access_hash = tLRPC$Chat2.access_hash;
            }
        } else {
            TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
            tLRPC$TL_inputDialogPeer.peer = tLRPC$TL_inputPeerChannel;
            long j4 = tLRPC$TL_help_promoData.peer.channel_id;
            tLRPC$TL_inputPeerChannel.channel_id = j4;
            TLRPC$Chat tLRPC$Chat3 = (TLRPC$Chat) longSparseArray2.get(j4);
            if (tLRPC$Chat3 != null) {
                tLRPC$TL_inputDialogPeer.peer.access_hash = tLRPC$Chat3.access_hash;
            }
        }
        tLRPC$TL_messages_getPeerDialogs.peers.add(tLRPC$TL_inputDialogPeer);
        this.checkingPromoInfoRequestId = getConnectionsManager().sendRequest(tLRPC$TL_messages_getPeerDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda280
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$checkPromoInfoInternal$139(i, tLRPC$TL_help_promoData, j, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$checkPromoInfoInternal$139(int i, final TLRPC$TL_help_promoData tLRPC$TL_help_promoData, final long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (i != this.lastCheckPromoId) {
            return;
        }
        this.checkingPromoInfoRequestId = 0;
        final TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs = (TLRPC$TL_messages_peerDialogs) tLObject;
        if (tLRPC$TL_messages_peerDialogs != null && !tLRPC$TL_messages_peerDialogs.dialogs.isEmpty()) {
            getMessagesStorage().putUsersAndChats(tLRPC$TL_help_promoData.users, tLRPC$TL_help_promoData.chats, true, true);
            TLRPC$TL_messages_dialogs tLRPC$TL_messages_dialogs = new TLRPC$TL_messages_dialogs();
            tLRPC$TL_messages_dialogs.chats = tLRPC$TL_messages_peerDialogs.chats;
            tLRPC$TL_messages_dialogs.users = tLRPC$TL_messages_peerDialogs.users;
            tLRPC$TL_messages_dialogs.dialogs = tLRPC$TL_messages_peerDialogs.dialogs;
            tLRPC$TL_messages_dialogs.messages = tLRPC$TL_messages_peerDialogs.messages;
            getMessagesStorage().putDialogs(tLRPC$TL_messages_dialogs, 2);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda177
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$checkPromoInfoInternal$137(tLRPC$TL_help_promoData, tLRPC$TL_messages_peerDialogs, j);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$checkPromoInfoInternal$138();
                }
            });
        }
        this.checkingPromoInfo = false;
    }

    public /* synthetic */ void lambda$checkPromoInfoInternal$137(TLRPC$TL_help_promoData tLRPC$TL_help_promoData, TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs, long j) {
        Integer num = 0;
        putUsers(tLRPC$TL_help_promoData.users, false);
        putChats(tLRPC$TL_help_promoData.chats, false);
        putUsers(tLRPC$TL_messages_peerDialogs.users, false);
        putChats(tLRPC$TL_messages_peerDialogs.chats, false);
        TLRPC$Dialog tLRPC$Dialog = this.promoDialog;
        if (tLRPC$Dialog != null) {
            long j2 = tLRPC$Dialog.id;
            if (j2 < 0) {
                TLRPC$Chat chat = getChat(Long.valueOf(-j2));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.promoDialog);
                }
            } else {
                removeDialog(tLRPC$Dialog);
            }
        }
        TLRPC$Dialog tLRPC$Dialog2 = tLRPC$TL_messages_peerDialogs.dialogs.get(0);
        this.promoDialog = tLRPC$Dialog2;
        tLRPC$Dialog2.id = j;
        tLRPC$Dialog2.folder_id = 0;
        if (DialogObject.isChannel(tLRPC$Dialog2)) {
            LongSparseIntArray longSparseIntArray = this.channelsPts;
            TLRPC$Dialog tLRPC$Dialog3 = this.promoDialog;
            longSparseIntArray.put(-tLRPC$Dialog3.id, tLRPC$Dialog3.pts);
        }
        Integer num2 = this.dialogs_read_inbox_max.get(Long.valueOf(this.promoDialog.id));
        if (num2 == null) {
            num2 = num;
        }
        this.dialogs_read_inbox_max.put(Long.valueOf(this.promoDialog.id), Integer.valueOf(Math.max(num2.intValue(), this.promoDialog.read_inbox_max_id)));
        Integer num3 = this.dialogs_read_outbox_max.get(Long.valueOf(this.promoDialog.id));
        if (num3 != null) {
            num = num3;
        }
        this.dialogs_read_outbox_max.put(Long.valueOf(this.promoDialog.id), Integer.valueOf(Math.max(num.intValue(), this.promoDialog.read_outbox_max_id)));
        this.dialogs_dict.put(j, this.promoDialog);
        if (!tLRPC$TL_messages_peerDialogs.messages.isEmpty()) {
            LongSparseArray longSparseArray = new LongSparseArray();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i = 0; i < tLRPC$TL_messages_peerDialogs.users.size(); i++) {
                TLRPC$User tLRPC$User = tLRPC$TL_messages_peerDialogs.users.get(i);
                longSparseArray.put(tLRPC$User.id, tLRPC$User);
            }
            for (int i2 = 0; i2 < tLRPC$TL_messages_peerDialogs.chats.size(); i2++) {
                TLRPC$Chat tLRPC$Chat = tLRPC$TL_messages_peerDialogs.chats.get(i2);
                longSparseArray2.put(tLRPC$Chat.id, tLRPC$Chat);
            }
            MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$TL_messages_peerDialogs.messages.get(0), (LongSparseArray<TLRPC$User>) longSparseArray, (LongSparseArray<TLRPC$Chat>) longSparseArray2, false, true);
            ArrayList<MessageObject> arrayList = this.dialogMessage.get(j);
            if (arrayList == null) {
                arrayList = new ArrayList<>(1);
            }
            if (arrayList.size() > 0 && arrayList.get(0) != null && arrayList.get(0).hasValidGroupId() && arrayList.get(0).getGroupIdForUse() != messageObject.getGroupIdForUse()) {
                arrayList.clear();
            }
            arrayList.add(messageObject);
            this.dialogMessage.put(j, arrayList);
            TLRPC$Dialog tLRPC$Dialog4 = this.promoDialog;
            if (tLRPC$Dialog4.last_message_date == 0) {
                tLRPC$Dialog4.last_message_date = messageObject.messageOwner.date;
            }
        }
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public /* synthetic */ void lambda$checkPromoInfoInternal$138() {
        TLRPC$Dialog tLRPC$Dialog = this.promoDialog;
        if (tLRPC$Dialog != null) {
            long j = tLRPC$Dialog.id;
            if (j < 0) {
                TLRPC$Chat chat = getChat(Long.valueOf(-j));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.promoDialog);
                }
            } else {
                removeDialog(tLRPC$Dialog);
            }
            this.promoDialog = null;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void removePromoDialog() {
        TLRPC$Dialog tLRPC$Dialog = this.promoDialog;
        if (tLRPC$Dialog == null) {
            return;
        }
        long j = tLRPC$Dialog.id;
        if (j < 0) {
            TLRPC$Chat chat = getChat(Long.valueOf(-j));
            if (ChatObject.isNotInChat(chat) || chat.restricted) {
                removeDialog(this.promoDialog);
            }
        } else {
            removeDialog(tLRPC$Dialog);
        }
        this.promoDialog = null;
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public boolean isPromoDialog(long j, boolean z) {
        TLRPC$Dialog tLRPC$Dialog = this.promoDialog;
        return tLRPC$Dialog != null && tLRPC$Dialog.id == j && (!z || this.isLeftPromoChannel);
    }

    private String getUserNameForTyping(TLRPC$User tLRPC$User) {
        if (tLRPC$User == null) {
            return "";
        }
        String str = tLRPC$User.first_name;
        if (str != null && str.length() > 0) {
            return tLRPC$User.first_name;
        }
        String str2 = tLRPC$User.last_name;
        return (str2 == null || str2.length() <= 0) ? "" : tLRPC$User.last_name;
    }

    private void updatePrintingStrings() {
        Iterator<Map.Entry<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>>> it;
        Iterator<Map.Entry<Integer, ArrayList<PrintingUser>>> it2;
        String formatString;
        int i;
        final LongSparseArray longSparseArray = new LongSparseArray();
        final LongSparseArray longSparseArray2 = new LongSparseArray();
        Iterator<Map.Entry<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>>> it3 = this.printingUsers.entrySet().iterator();
        while (it3.hasNext()) {
            Map.Entry<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>> next = it3.next();
            Long key = next.getKey();
            boolean isEncryptedDialog = DialogObject.isEncryptedDialog(key.longValue());
            Iterator<Map.Entry<Integer, ArrayList<PrintingUser>>> it4 = next.getValue().entrySet().iterator();
            while (it4.hasNext()) {
                Map.Entry<Integer, ArrayList<PrintingUser>> next2 = it4.next();
                Integer key2 = next2.getKey();
                ArrayList<PrintingUser> value = next2.getValue();
                SparseArray sparseArray = new SparseArray();
                SparseArray sparseArray2 = new SparseArray();
                longSparseArray.put(key.longValue(), sparseArray);
                longSparseArray2.put(key.longValue(), sparseArray2);
                if (key.longValue() > 0 || isEncryptedDialog || value.size() == 1) {
                    it = it3;
                    it2 = it4;
                    PrintingUser printingUser = value.get(0);
                    TLRPC$User user = getUser(Long.valueOf(printingUser.userId));
                    if (user != null) {
                        TLRPC$SendMessageAction tLRPC$SendMessageAction = printingUser.action;
                        if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageRecordAudioAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsRecordingAudio", R.string.IsRecordingAudio, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("RecordingAudio", R.string.RecordingAudio));
                            }
                            sparseArray2.put(key2.intValue(), 1);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageRecordRoundAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsRecordingRound", R.string.IsRecordingRound, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("RecordingRound", R.string.RecordingRound));
                            }
                            sparseArray2.put(key2.intValue(), 4);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageUploadRoundAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsSendingVideo", R.string.IsSendingVideo, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("SendingVideoStatus", R.string.SendingVideoStatus));
                            }
                            sparseArray2.put(key2.intValue(), 4);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageUploadAudioAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsSendingAudio", R.string.IsSendingAudio, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("SendingAudio", R.string.SendingAudio));
                            }
                            sparseArray2.put(key2.intValue(), 2);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageUploadVideoAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsSendingVideo", R.string.IsSendingVideo, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("SendingVideoStatus", R.string.SendingVideoStatus));
                            }
                            sparseArray2.put(key2.intValue(), 2);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageRecordVideoAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsRecordingVideo", R.string.IsRecordingVideo, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("RecordingVideoStatus", R.string.RecordingVideoStatus));
                            }
                            sparseArray2.put(key2.intValue(), 2);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageUploadDocumentAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsSendingFile", R.string.IsSendingFile, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("SendingFile", R.string.SendingFile));
                            }
                            sparseArray2.put(key2.intValue(), 2);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageUploadPhotoAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsSendingPhoto", R.string.IsSendingPhoto, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("SendingPhoto", R.string.SendingPhoto));
                            }
                            sparseArray2.put(key2.intValue(), 2);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageGamePlayAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsSendingGame", R.string.IsSendingGame, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("SendingGame", R.string.SendingGame));
                            }
                            sparseArray2.put(key2.intValue(), 3);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageGeoLocationAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsSelectingLocation", R.string.IsSelectingLocation, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("SelectingLocation", R.string.SelectingLocation));
                            }
                            sparseArray2.put(key2.intValue(), 0);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageChooseContactAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsSelectingContact", R.string.IsSelectingContact, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("SelectingContact", R.string.SelectingContact));
                            }
                            sparseArray2.put(key2.intValue(), 0);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageEmojiInteractionSeen) {
                            String str = ((TLRPC$TL_sendMessageEmojiInteractionSeen) tLRPC$SendMessageAction).emoticon;
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                formatString = LocaleController.formatString("IsEnjoyngAnimations", R.string.IsEnjoyngAnimations, getUserNameForTyping(user), str);
                            } else {
                                formatString = LocaleController.formatString("EnjoyngAnimations", R.string.EnjoyngAnimations, str);
                            }
                            sparseArray.put(key2.intValue(), formatString);
                            sparseArray2.put(key2.intValue(), 5);
                        } else if (tLRPC$SendMessageAction instanceof TLRPC$TL_sendMessageChooseStickerAction) {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsChoosingSticker", R.string.IsChoosingSticker, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("ChoosingSticker", R.string.ChoosingSticker));
                            }
                            sparseArray2.put(key2.intValue(), 5);
                        } else {
                            if (key.longValue() < 0 && !isEncryptedDialog) {
                                sparseArray.put(key2.intValue(), LocaleController.formatString("IsTypingGroup", R.string.IsTypingGroup, getUserNameForTyping(user)));
                            } else {
                                sparseArray.put(key2.intValue(), LocaleController.getString("Typing", R.string.Typing));
                            }
                            sparseArray2.put(key2.intValue(), 0);
                        }
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    Iterator<PrintingUser> it5 = value.iterator();
                    int i2 = 0;
                    while (true) {
                        if (!it5.hasNext()) {
                            it = it3;
                            it2 = it4;
                            break;
                        }
                        it = it3;
                        it2 = it4;
                        TLRPC$User user2 = getUser(Long.valueOf(it5.next().userId));
                        if (user2 != null) {
                            if (sb.length() != 0) {
                                sb.append(", ");
                            }
                            sb.append(getUserNameForTyping(user2));
                            i2++;
                        }
                        if (i2 == 2) {
                            break;
                        }
                        it3 = it;
                        it4 = it2;
                    }
                    if (sb.length() != 0) {
                        if (i2 == 1) {
                            sparseArray.put(key2.intValue(), LocaleController.formatString("IsTypingGroup", R.string.IsTypingGroup, sb.toString()));
                        } else if (value.size() > 2) {
                            try {
                                sparseArray.put(key2.intValue(), String.format(LocaleController.getPluralString("AndMoreTypingGroup", value.size() - 2), sb.toString(), Integer.valueOf(value.size() - 2)));
                            } catch (Exception unused) {
                                sparseArray.put(key2.intValue(), "LOC_ERR: AndMoreTypingGroup");
                            }
                        } else {
                            i = 0;
                            sparseArray.put(key2.intValue(), LocaleController.formatString("AreTypingGroup", R.string.AreTypingGroup, sb.toString()));
                            sparseArray2.put(key2.intValue(), Integer.valueOf(i));
                        }
                        i = 0;
                        sparseArray2.put(key2.intValue(), Integer.valueOf(i));
                    }
                }
                it3 = it;
                it4 = it2;
            }
        }
        this.lastPrintingStringCount = longSparseArray.size();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda103
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$updatePrintingStrings$142(longSparseArray, longSparseArray2);
            }
        });
    }

    public /* synthetic */ void lambda$updatePrintingStrings$142(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.printingStrings = longSparseArray;
        this.printingStringsTypes = longSparseArray2;
    }

    /* renamed from: cancelTyping */
    public void lambda$sendTyping$145(int i, long j, int i2) {
        LongSparseArray<SparseArray<Boolean>> longSparseArray;
        SparseArray<Boolean> sparseArray;
        if (i >= 0) {
            LongSparseArray<SparseArray<Boolean>>[] longSparseArrayArr = this.sendingTypings;
            if (i >= longSparseArrayArr.length || longSparseArrayArr[i] == null || (sparseArray = (longSparseArray = longSparseArrayArr[i]).get(j)) == null) {
                return;
            }
            sparseArray.remove(i2);
            if (sparseArray.size() != 0) {
                return;
            }
            longSparseArray.remove(j);
        }
    }

    public boolean sendTyping(long j, int i, int i2, int i3) {
        return sendTyping(j, i, i2, null, i3);
    }

    public boolean sendTyping(final long j, final int i, final int i2, String str, int i3) {
        TLRPC$Chat chat;
        if (i2 < 0 || i2 >= this.sendingTypings.length || j == 0) {
            return false;
        }
        if (j < 0) {
            long j2 = -j;
            if (ChatObject.getSendAsPeerId(getChat(Long.valueOf(j2)), getChatFull(j2)) != UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()) {
                return false;
            }
        } else {
            TLRPC$User user = getUser(Long.valueOf(j));
            if (user != null) {
                if (user.id == getUserConfig().getClientUserId()) {
                    return false;
                }
                TLRPC$UserStatus tLRPC$UserStatus = user.status;
                if (tLRPC$UserStatus != null && tLRPC$UserStatus.expires != -100 && !this.onlinePrivacy.containsKey(Long.valueOf(user.id))) {
                    if (user.status.expires <= getConnectionsManager().getCurrentTime() - 30) {
                        return false;
                    }
                }
            }
        }
        LongSparseArray<SparseArray<Boolean>>[] longSparseArrayArr = this.sendingTypings;
        LongSparseArray<SparseArray<Boolean>> longSparseArray = longSparseArrayArr[i2];
        if (longSparseArray == null) {
            longSparseArray = new LongSparseArray<>();
            longSparseArrayArr[i2] = longSparseArray;
        }
        SparseArray<Boolean> sparseArray = longSparseArray.get(j);
        if (sparseArray == null) {
            sparseArray = new SparseArray<>();
            longSparseArray.put(j, sparseArray);
        }
        if (sparseArray.get(i) != null) {
            return false;
        }
        if (!DialogObject.isEncryptedDialog(j)) {
            TLRPC$TL_messages_setTyping tLRPC$TL_messages_setTyping = new TLRPC$TL_messages_setTyping();
            if (i != 0) {
                tLRPC$TL_messages_setTyping.top_msg_id = i;
                tLRPC$TL_messages_setTyping.flags |= 1;
            }
            TLRPC$InputPeer inputPeer = getInputPeer(j);
            tLRPC$TL_messages_setTyping.peer = inputPeer;
            if (((inputPeer instanceof TLRPC$TL_inputPeerChannel) && ((chat = getChat(Long.valueOf(inputPeer.channel_id))) == null || !chat.megagroup)) || tLRPC$TL_messages_setTyping.peer == null) {
                return false;
            }
            if (i2 == 0) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageTypingAction();
            } else if (i2 == 1) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageRecordAudioAction();
            } else if (i2 == 2) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageCancelAction();
            } else if (i2 == 3) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageUploadDocumentAction();
            } else if (i2 == 4) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageUploadPhotoAction();
            } else if (i2 == 5) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageUploadVideoAction();
            } else if (i2 == 6) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageGamePlayAction();
            } else if (i2 == 7) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageRecordRoundAction();
            } else if (i2 == 8) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageUploadRoundAction();
            } else if (i2 == 9) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageUploadAudioAction();
            } else if (i2 == 10) {
                tLRPC$TL_messages_setTyping.action = new TLRPC$TL_sendMessageChooseStickerAction();
            } else if (i2 == 11) {
                TLRPC$TL_sendMessageEmojiInteractionSeen tLRPC$TL_sendMessageEmojiInteractionSeen = new TLRPC$TL_sendMessageEmojiInteractionSeen();
                tLRPC$TL_sendMessageEmojiInteractionSeen.emoticon = str;
                tLRPC$TL_messages_setTyping.action = tLRPC$TL_sendMessageEmojiInteractionSeen;
            }
            sparseArray.put(i, Boolean.TRUE);
            int sendRequest = getConnectionsManager().sendRequest(tLRPC$TL_messages_setTyping, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda276
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$sendTyping$144(i2, j, i, tLObject, tLRPC$TL_error);
                }
            }, 2);
            if (i3 != 0) {
                getConnectionsManager().bindRequestToGuid(sendRequest, i3);
            }
        } else if (i2 != 0) {
            return false;
        } else {
            TLRPC$EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j)));
            byte[] bArr = encryptedChat.auth_key;
            if (bArr != null && bArr.length > 1 && (encryptedChat instanceof TLRPC$TL_encryptedChat)) {
                TLRPC$TL_messages_setEncryptedTyping tLRPC$TL_messages_setEncryptedTyping = new TLRPC$TL_messages_setEncryptedTyping();
                TLRPC$TL_inputEncryptedChat tLRPC$TL_inputEncryptedChat = new TLRPC$TL_inputEncryptedChat();
                tLRPC$TL_messages_setEncryptedTyping.peer = tLRPC$TL_inputEncryptedChat;
                tLRPC$TL_inputEncryptedChat.chat_id = encryptedChat.id;
                tLRPC$TL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
                tLRPC$TL_messages_setEncryptedTyping.typing = true;
                sparseArray.put(i, Boolean.TRUE);
                int sendRequest2 = getConnectionsManager().sendRequest(tLRPC$TL_messages_setEncryptedTyping, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda275
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$sendTyping$146(i2, j, i, tLObject, tLRPC$TL_error);
                    }
                }, 2);
                if (i3 != 0) {
                    getConnectionsManager().bindRequestToGuid(sendRequest2, i3);
                }
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$sendTyping$144(final int i, final long j, final int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda49
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$sendTyping$143(i, j, i2);
            }
        });
    }

    public /* synthetic */ void lambda$sendTyping$146(final int i, final long j, final int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$sendTyping$145(i, j, i2);
            }
        });
    }

    public void removeDeletedMessagesFromArray(long j, ArrayList<TLRPC$Message> arrayList) {
        int i = 0;
        int i2 = this.deletedHistory.get(j, 0);
        if (i2 == 0) {
            return;
        }
        int size = arrayList.size();
        while (i < size) {
            if (arrayList.get(i).id <= i2) {
                arrayList.remove(i);
                i--;
                size--;
            }
            i++;
        }
    }

    public void loadMessages(long j, long j2, boolean z, int i, int i2, int i3, boolean z2, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, boolean z3) {
        loadMessages(j, j2, z, i, i2, i3, z2, i4, i5, i6, i7, i8, i9, i11, i9 != 0 ? i10 : 0, 0, 0, false, 0, z3);
    }

    public void loadMessages(long j, long j2, boolean z, int i, int i2, int i3, boolean z2, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, boolean z3, int i14, boolean z4) {
        loadMessagesInternal(j, j2, z, i, i2, i3, z2, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, z3, i14, true, true, z4);
    }

    public void loadMessagesInternal(final long j, final long j2, final boolean z, final int i, final int i2, final int i3, boolean z2, final int i4, final int i5, final int i6, final int i7, final int i8, final int i9, final int i10, final int i11, final int i12, final int i13, final boolean z3, final int i14, boolean z4, final boolean z5, final boolean z6) {
        MessagesController messagesController;
        TLRPC$TL_forumTopic findTopic;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load messages in chat " + j + " topic_id " + i9 + " count " + i + " max_id " + i2 + " cache " + z2 + " mindate = " + i4 + " guid " + i5 + " load_type " + i6 + " last_message_id " + i7 + " mode " + i8 + " index " + i10 + " firstUnread " + i11 + " unread_count " + i12 + " last_date " + i13 + " queryFromServer " + z3 + " isTopic " + z6);
        }
        if ((i9 == 0 || z6) && i8 != 2 && (z2 || DialogObject.isEncryptedDialog(j))) {
            getMessagesStorage().getMessages(j, j2, z, i, i2, i3, i4, i5, i6, i8 == 1, i9, i10, z5, z6);
        } else if (i9 != 0) {
            if (z4 && z6 && i6 == 2 && i7 == 0 && (findTopic = this.topicsController.findTopic(-j, i9)) != null) {
                loadMessagesInternal(j, j2, z, i, i2, i3, false, i4, i5, i6, findTopic.top_message, 0, i9, i10, i11, findTopic.unread_count, i13, z3, findTopic.unread_mentions_count, false, z5, z6);
            } else if (i8 != 0) {
            } else {
                final TLRPC$TL_messages_getReplies tLRPC$TL_messages_getReplies = new TLRPC$TL_messages_getReplies();
                tLRPC$TL_messages_getReplies.peer = getInputPeer(j);
                tLRPC$TL_messages_getReplies.msg_id = i9;
                tLRPC$TL_messages_getReplies.offset_date = i3;
                if (i6 == 4) {
                    tLRPC$TL_messages_getReplies.add_offset = (-i) + 5;
                } else if (i6 == 3) {
                    tLRPC$TL_messages_getReplies.add_offset = (-i) / 2;
                } else if (i6 == 1) {
                    tLRPC$TL_messages_getReplies.add_offset = (-i) - 1;
                } else if (i6 == 2 && i2 != 0) {
                    tLRPC$TL_messages_getReplies.add_offset = (-i) + 10;
                } else if (j < 0 && i2 != 0) {
                    messagesController = this;
                    if (ChatObject.isChannel(messagesController.getChat(Long.valueOf(-j)))) {
                        tLRPC$TL_messages_getReplies.add_offset = -1;
                        tLRPC$TL_messages_getReplies.limit++;
                    }
                    tLRPC$TL_messages_getReplies.limit = i;
                    tLRPC$TL_messages_getReplies.offset_id = i2;
                    getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getReplies, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda272
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$loadMessagesInternal$148(i, i2, i3, i11, i6, j, j2, i5, i7, i12, i13, i9, i10, z3, i14, z5, z6, tLRPC$TL_messages_getReplies, tLObject, tLRPC$TL_error);
                        }
                    }), i5);
                }
                messagesController = this;
                tLRPC$TL_messages_getReplies.limit = i;
                tLRPC$TL_messages_getReplies.offset_id = i2;
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getReplies, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda272
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$loadMessagesInternal$148(i, i2, i3, i11, i6, j, j2, i5, i7, i12, i13, i9, i10, z3, i14, z5, z6, tLRPC$TL_messages_getReplies, tLObject, tLRPC$TL_error);
                    }
                }), i5);
            }
        } else if (i8 == 2) {
        } else {
            if (i8 == 1) {
                TLRPC$TL_messages_getScheduledHistory tLRPC$TL_messages_getScheduledHistory = new TLRPC$TL_messages_getScheduledHistory();
                tLRPC$TL_messages_getScheduledHistory.peer = getInputPeer(j);
                tLRPC$TL_messages_getScheduledHistory.hash = i4;
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getScheduledHistory, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda273
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$loadMessagesInternal$149(i2, i3, j, j2, i, i5, i11, i7, i12, i13, i6, i8, i9, i10, z3, i14, z5, z6, tLObject, tLRPC$TL_error);
                    }
                }), i5);
            } else if (z4 && ((i6 == 3 || i6 == 2) && i7 == 0)) {
                final TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs = new TLRPC$TL_messages_getPeerDialogs();
                TLRPC$InputPeer inputPeer = getInputPeer(j);
                TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
                tLRPC$TL_inputDialogPeer.peer = inputPeer;
                tLRPC$TL_messages_getPeerDialogs.peers.add(tLRPC$TL_inputDialogPeer);
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getPeerDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda306
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$loadMessagesInternal$151(j, j2, z, i, i2, i3, i4, i5, i6, i9, i10, i11, i13, z3, z5, z6, tLRPC$TL_messages_getPeerDialogs, tLObject, tLRPC$TL_error);
                    }
                });
            } else {
                final TLRPC$TL_messages_getHistory tLRPC$TL_messages_getHistory = new TLRPC$TL_messages_getHistory();
                tLRPC$TL_messages_getHistory.peer = getInputPeer(j);
                if (i6 == 4) {
                    tLRPC$TL_messages_getHistory.add_offset = (-i) + 5;
                } else if (i6 == 3) {
                    tLRPC$TL_messages_getHistory.add_offset = (-i) / 2;
                } else if (i6 == 1) {
                    tLRPC$TL_messages_getHistory.add_offset = (-i) - 1;
                } else if (i6 == 2 && i2 != 0) {
                    tLRPC$TL_messages_getHistory.add_offset = (-i) + 6;
                } else if (j < 0 && i2 != 0) {
                    if (ChatObject.isChannel(getChat(Long.valueOf(-j)))) {
                        tLRPC$TL_messages_getHistory.add_offset = -1;
                        tLRPC$TL_messages_getHistory.limit++;
                    }
                    tLRPC$TL_messages_getHistory.limit = i;
                    tLRPC$TL_messages_getHistory.offset_id = i2;
                    tLRPC$TL_messages_getHistory.offset_date = i3;
                    getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getHistory, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda298
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$loadMessagesInternal$153(j, i, i2, i3, j2, i5, i11, i7, i12, i13, i6, i9, i10, z3, i14, z5, z6, tLRPC$TL_messages_getHistory, tLObject, tLRPC$TL_error);
                        }
                    }), i5);
                }
                tLRPC$TL_messages_getHistory.limit = i;
                tLRPC$TL_messages_getHistory.offset_id = i2;
                tLRPC$TL_messages_getHistory.offset_date = i3;
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getHistory, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda298
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$loadMessagesInternal$153(j, i, i2, i3, j2, i5, i11, i7, i12, i13, i6, i9, i10, z3, i14, z5, z6, tLRPC$TL_messages_getHistory, tLObject, tLRPC$TL_error);
                    }
                }), i5);
            }
        }
    }

    public /* synthetic */ void lambda$loadMessagesInternal$148(int i, int i2, int i3, int i4, int i5, long j, long j2, final int i6, int i7, int i8, int i9, int i10, int i11, boolean z, int i12, boolean z2, boolean z3, final TLRPC$TL_messages_getReplies tLRPC$TL_messages_getReplies, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        int i13;
        int i14;
        ArrayList<TLRPC$Message> arrayList;
        if (tLObject != null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            if (tLRPC$messages_Messages.messages.size() > i) {
                tLRPC$messages_Messages.messages.remove(0);
            }
            if (!tLRPC$messages_Messages.messages.isEmpty()) {
                if (i3 != 0) {
                    int i15 = tLRPC$messages_Messages.messages.get(arrayList.size() - 1).id;
                    int size = tLRPC$messages_Messages.messages.size() - 1;
                    while (true) {
                        if (size < 0) {
                            break;
                        }
                        TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(size);
                        if (tLRPC$Message.date > i3) {
                            i15 = tLRPC$Message.id;
                            break;
                        }
                        size--;
                    }
                    i13 = i15;
                    i14 = 0;
                    processLoadedMessages(tLRPC$messages_Messages, tLRPC$messages_Messages.messages.size(), j, j2, i, i13, i3, false, i6, i14, i7, i8, i9, i5, false, 0, i10, i11, z, i12, z2, z3);
                    return;
                } else if (i4 != 0) {
                    if (i5 == 2 && i2 > 0) {
                        for (int size2 = tLRPC$messages_Messages.messages.size() - 1; size2 >= 0; size2--) {
                            TLRPC$Message tLRPC$Message2 = tLRPC$messages_Messages.messages.get(size2);
                            int i16 = tLRPC$Message2.id;
                            if (i16 > i4 && !tLRPC$Message2.out) {
                                i13 = i2;
                                i14 = i16;
                                break;
                            }
                        }
                    }
                    i13 = i2;
                    i14 = 0;
                    processLoadedMessages(tLRPC$messages_Messages, tLRPC$messages_Messages.messages.size(), j, j2, i, i13, i3, false, i6, i14, i7, i8, i9, i5, false, 0, i10, i11, z, i12, z2, z3);
                    return;
                }
            }
            i13 = i2;
            i14 = 0;
            processLoadedMessages(tLRPC$messages_Messages, tLRPC$messages_Messages.messages.size(), j, j2, i, i13, i3, false, i6, i14, i7, i8, i9, i5, false, 0, i10, i11, z, i12, z2, z3);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda58
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadMessagesInternal$147(i6, tLRPC$TL_messages_getReplies, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadMessagesInternal$147(int i, TLRPC$TL_messages_getReplies tLRPC$TL_messages_getReplies, TLRPC$TL_error tLRPC$TL_error) {
        getNotificationCenter().postNotificationName(NotificationCenter.loadingMessagesFailed, Integer.valueOf(i), tLRPC$TL_messages_getReplies, tLRPC$TL_error);
    }

    public /* synthetic */ void lambda$loadMessagesInternal$149(int i, int i2, long j, long j2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, boolean z, int i13, boolean z2, boolean z3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int i14;
        ArrayList<TLRPC$Message> arrayList;
        if (tLObject != null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            if (tLRPC$messages_Messages instanceof TLRPC$TL_messages_messagesNotModified) {
                return;
            }
            if (i2 == 0 || tLRPC$messages_Messages.messages.isEmpty()) {
                i14 = i;
            } else {
                int i15 = tLRPC$messages_Messages.messages.get(arrayList.size() - 1).id;
                int size = tLRPC$messages_Messages.messages.size() - 1;
                while (true) {
                    if (size < 0) {
                        break;
                    }
                    TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(size);
                    if (tLRPC$Message.date > i2) {
                        i15 = tLRPC$Message.id;
                        break;
                    }
                    size--;
                }
                i14 = i15;
            }
            processLoadedMessages(tLRPC$messages_Messages, tLRPC$messages_Messages.messages.size(), j, j2, i3, i14, i2, false, i4, i5, i6, i7, i8, i9, false, i10, i11, i12, z, i13, z2, z3);
        }
    }

    public /* synthetic */ void lambda$loadMessagesInternal$151(long j, long j2, boolean z, int i, int i2, int i3, int i4, final int i5, int i6, int i7, int i8, int i9, int i10, boolean z2, boolean z3, boolean z4, final TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs = (TLRPC$TL_messages_peerDialogs) tLObject;
            if (!tLRPC$TL_messages_peerDialogs.dialogs.isEmpty()) {
                TLRPC$Dialog tLRPC$Dialog = tLRPC$TL_messages_peerDialogs.dialogs.get(0);
                if (tLRPC$Dialog.top_message != 0) {
                    TLRPC$TL_messages_dialogs tLRPC$TL_messages_dialogs = new TLRPC$TL_messages_dialogs();
                    tLRPC$TL_messages_dialogs.chats = tLRPC$TL_messages_peerDialogs.chats;
                    tLRPC$TL_messages_dialogs.users = tLRPC$TL_messages_peerDialogs.users;
                    tLRPC$TL_messages_dialogs.dialogs = tLRPC$TL_messages_peerDialogs.dialogs;
                    tLRPC$TL_messages_dialogs.messages = tLRPC$TL_messages_peerDialogs.messages;
                    getMessagesStorage().putDialogs(tLRPC$TL_messages_dialogs, 2);
                }
                loadMessagesInternal(j, j2, z, i, i2, i3, false, i4, i5, i6, tLRPC$Dialog.top_message, 0, i7, i8, i9, tLRPC$Dialog.unread_count, i10, z2, tLRPC$Dialog.unread_mentions_count, false, z3, z4);
            }
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda57
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadMessagesInternal$150(i5, tLRPC$TL_messages_getPeerDialogs, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadMessagesInternal$150(int i, TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs, TLRPC$TL_error tLRPC$TL_error) {
        getNotificationCenter().postNotificationName(NotificationCenter.loadingMessagesFailed, Integer.valueOf(i), tLRPC$TL_messages_getPeerDialogs, tLRPC$TL_error);
    }

    public /* synthetic */ void lambda$loadMessagesInternal$153(long j, int i, int i2, int i3, long j2, final int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, boolean z, int i12, boolean z2, boolean z3, final TLRPC$TL_messages_getHistory tLRPC$TL_messages_getHistory, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        int i13;
        ArrayList<TLRPC$Message> arrayList;
        if (tLObject != null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            removeDeletedMessagesFromArray(j, tLRPC$messages_Messages.messages);
            if (tLRPC$messages_Messages.messages.size() > i) {
                tLRPC$messages_Messages.messages.remove(0);
            }
            if (i3 == 0 || tLRPC$messages_Messages.messages.isEmpty()) {
                i13 = i2;
            } else {
                int i14 = tLRPC$messages_Messages.messages.get(arrayList.size() - 1).id;
                int size = tLRPC$messages_Messages.messages.size() - 1;
                while (true) {
                    if (size < 0) {
                        break;
                    }
                    TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(size);
                    if (tLRPC$Message.date > i3) {
                        i14 = tLRPC$Message.id;
                        break;
                    }
                    size--;
                }
                i13 = i14;
            }
            processLoadedMessages(tLRPC$messages_Messages, tLRPC$messages_Messages.messages.size(), j, j2, i, i13, i3, false, i4, i5, i6, i7, i8, i9, false, 0, i10, i11, z, i12, z2, z3);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda56
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadMessagesInternal$152(i4, tLRPC$TL_messages_getHistory, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadMessagesInternal$152(int i, TLRPC$TL_messages_getHistory tLRPC$TL_messages_getHistory, TLRPC$TL_error tLRPC$TL_error) {
        getNotificationCenter().postNotificationName(NotificationCenter.loadingMessagesFailed, Integer.valueOf(i), tLRPC$TL_messages_getHistory, tLRPC$TL_error);
    }

    public void reloadWebPages(final long j, HashMap<String, ArrayList<MessageObject>> hashMap, final boolean z) {
        final HashMap<String, ArrayList<MessageObject>> hashMap2 = z ? this.reloadingScheduledWebpages : this.reloadingWebpages;
        final LongSparseArray<ArrayList<MessageObject>> longSparseArray = z ? this.reloadingScheduledWebpagesPending : this.reloadingWebpagesPending;
        for (Map.Entry<String, ArrayList<MessageObject>> entry : hashMap.entrySet()) {
            final String key = entry.getKey();
            ArrayList<MessageObject> value = entry.getValue();
            ArrayList<MessageObject> arrayList = hashMap2.get(key);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                hashMap2.put(key, arrayList);
            }
            arrayList.addAll(value);
            TLRPC$TL_messages_getWebPagePreview tLRPC$TL_messages_getWebPagePreview = new TLRPC$TL_messages_getWebPagePreview();
            tLRPC$TL_messages_getWebPagePreview.message = key;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getWebPagePreview, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda327
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$reloadWebPages$155(hashMap2, key, longSparseArray, j, z, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$reloadWebPages$155(final HashMap hashMap, final String str, final LongSparseArray longSparseArray, final long j, final boolean z, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda129
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$reloadWebPages$154(hashMap, str, tLObject, longSparseArray, j, z);
            }
        });
    }

    public /* synthetic */ void lambda$reloadWebPages$154(HashMap hashMap, String str, TLObject tLObject, LongSparseArray longSparseArray, long j, boolean z) {
        ArrayList arrayList = (ArrayList) hashMap.remove(str);
        if (arrayList == null) {
            return;
        }
        TLRPC$TL_messages_messages tLRPC$TL_messages_messages = new TLRPC$TL_messages_messages();
        if (!(tLObject instanceof TLRPC$TL_messageMediaWebPage)) {
            for (int i = 0; i < arrayList.size(); i++) {
                ((MessageObject) arrayList.get(i)).messageOwner.media.webpage = new TLRPC$TL_webPageEmpty();
                tLRPC$TL_messages_messages.messages.add(((MessageObject) arrayList.get(i)).messageOwner);
            }
        } else {
            TLRPC$TL_messageMediaWebPage tLRPC$TL_messageMediaWebPage = (TLRPC$TL_messageMediaWebPage) tLObject;
            TLRPC$WebPage tLRPC$WebPage = tLRPC$TL_messageMediaWebPage.webpage;
            if ((tLRPC$WebPage instanceof TLRPC$TL_webPage) || (tLRPC$WebPage instanceof TLRPC$TL_webPageEmpty)) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    ((MessageObject) arrayList.get(i2)).messageOwner.media.webpage = tLRPC$TL_messageMediaWebPage.webpage;
                    if (i2 == 0) {
                        ImageLoader.saveMessageThumbs(((MessageObject) arrayList.get(i2)).messageOwner);
                    }
                    tLRPC$TL_messages_messages.messages.add(((MessageObject) arrayList.get(i2)).messageOwner);
                }
            } else {
                longSparseArray.put(tLRPC$WebPage.id, arrayList);
            }
        }
        if (tLRPC$TL_messages_messages.messages.isEmpty()) {
            return;
        }
        getMessagesStorage().putMessages((TLRPC$messages_Messages) tLRPC$TL_messages_messages, j, -2, 0, false, z, 0);
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList);
    }

    /* JADX WARN: Removed duplicated region for block: B:223:0x010e  */
    /* JADX WARN: Removed duplicated region for block: B:230:0x0121  */
    /* JADX WARN: Removed duplicated region for block: B:234:0x013e  */
    /* JADX WARN: Removed duplicated region for block: B:254:0x019c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:271:0x0232 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:276:0x0247 A[LOOP:0: B:274:0x023f->B:276:0x0247, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:280:0x0260 A[LOOP:1: B:278:0x0258->B:280:0x0260, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:283:0x027a  */
    /* JADX WARN: Removed duplicated region for block: B:328:0x034b  */
    /* JADX WARN: Removed duplicated region for block: B:331:0x0351  */
    /* JADX WARN: Removed duplicated region for block: B:337:0x0382  */
    /* JADX WARN: Removed duplicated region for block: B:379:0x04a5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void processLoadedMessages(final org.telegram.tgnet.TLRPC$messages_Messages r33, final int r34, final long r35, final long r37, final int r39, final int r40, final int r41, final boolean r42, final int r43, final int r44, final int r45, final int r46, final int r47, final int r48, final boolean r49, final int r50, final int r51, final int r52, final boolean r53, final int r54, final boolean r55, final boolean r56) {
        /*
            Method dump skipped, instructions count: 1287
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processLoadedMessages(org.telegram.tgnet.TLRPC$messages_Messages, int, long, long, int, int, int, boolean, int, int, int, int, int, int, boolean, int, int, int, boolean, int, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$processLoadedMessages$156(long j, long j2, int i, int i2, boolean z, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, boolean z2, boolean z3) {
        loadMessagesInternal(j, j2, false, i, (i2 != 2 || !z) ? i4 : i3, i5, false, i6, i7, i2, i8, i9, i10, i11, i3, i12, i13, z, i14, true, z2, z3);
    }

    public /* synthetic */ void lambda$processLoadedMessages$157(int i, TLRPC$messages_Messages tLRPC$messages_Messages, boolean z, boolean z2, int i2) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoadWithoutProcess, Integer.valueOf(i), Integer.valueOf(tLRPC$messages_Messages.messages.size()), Boolean.valueOf(z), Boolean.valueOf(z2), Integer.valueOf(i2));
    }

    public /* synthetic */ void lambda$processLoadedMessages$159(TLRPC$messages_Messages tLRPC$messages_Messages, final boolean z, final boolean z2, final int i, boolean z3, final int i2, int i3, final int i4, final long j, final ArrayList arrayList, int i5, final int i6, final int i7, final boolean z4, final int i8, final int i9, final int i10, final int i11, final int i12, final int i13, ArrayList arrayList2, HashMap hashMap) {
        int i14;
        int i15;
        int i16;
        int i17;
        boolean z5;
        MessagesController messagesController;
        int i18;
        long j2;
        HashMap hashMap2;
        putUsers(tLRPC$messages_Messages.users, z);
        putChats(tLRPC$messages_Messages.chats, z);
        if (tLRPC$messages_Messages.animatedEmoji != null && z2) {
            AnimatedEmojiDrawable.getDocumentFetcher(this.currentAccount).processDocuments(tLRPC$messages_Messages.animatedEmoji);
        }
        if (i == 1) {
            i16 = 0;
        } else {
            if (!z3 || i2 != 2) {
                i14 = i3;
                i15 = Integer.MAX_VALUE;
            } else {
                i15 = Integer.MAX_VALUE;
                for (int i19 = 0; i19 < tLRPC$messages_Messages.messages.size(); i19++) {
                    TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i19);
                    if ((!tLRPC$Message.out || tLRPC$Message.from_scheduled) && (i17 = tLRPC$Message.id) > i3 && i17 < i15) {
                        i15 = i17;
                    }
                }
                i14 = i3;
            }
            i16 = i15 == Integer.MAX_VALUE ? i14 : i15;
        }
        if (i == 1 && i4 == 1) {
            getNotificationCenter().postNotificationName(NotificationCenter.scheduledMessagesUpdated, Long.valueOf(j), Integer.valueOf(arrayList.size()));
        }
        if (!DialogObject.isEncryptedDialog(j)) {
            final int i20 = i16;
            getMediaDataController().loadReplyMessagesForMessages(arrayList, j, i == 1, i5, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda212
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$processLoadedMessages$158(z2, i6, i7, z, z4, i8, j, i4, arrayList, i20, i9, i10, i2, i11, i12, i13, i);
                }
            });
            z5 = true;
        } else {
            z5 = true;
            getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoad, Long.valueOf(j), Integer.valueOf(i4), arrayList, Boolean.valueOf(z), Integer.valueOf(i16), Integer.valueOf(i8), Integer.valueOf(i9), Integer.valueOf(i10), Integer.valueOf(i2), Boolean.valueOf(z4), Integer.valueOf(i6), Integer.valueOf(i11), Integer.valueOf(i12), Integer.valueOf(i13), Integer.valueOf(i));
        }
        if (!arrayList2.isEmpty()) {
            i18 = i;
            messagesController = this;
            j2 = j;
            messagesController.reloadMessages(arrayList2, j2, i18 == z5);
        } else {
            messagesController = this;
            i18 = i;
            j2 = j;
        }
        if (!hashMap.isEmpty()) {
            if (i18 == z5) {
                hashMap2 = hashMap;
            } else {
                hashMap2 = hashMap;
                z5 = false;
            }
            messagesController.reloadWebPages(j2, hashMap2, z5);
        }
    }

    public /* synthetic */ void lambda$processLoadedMessages$158(boolean z, int i, int i2, boolean z2, boolean z3, int i3, long j, int i4, ArrayList arrayList, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12) {
        if (!z) {
            getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoadWithoutProcess, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z2), Boolean.valueOf(z3), Integer.valueOf(i3));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoad, Long.valueOf(j), Integer.valueOf(i4), arrayList, Boolean.valueOf(z2), Integer.valueOf(i5), Integer.valueOf(i3), Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8), Boolean.valueOf(z3), Integer.valueOf(i), Integer.valueOf(i9), Integer.valueOf(i10), Integer.valueOf(i11), Integer.valueOf(i12));
        }
    }

    public void loadHintDialogs() {
        if (!this.hintDialogs.isEmpty() || TextUtils.isEmpty(this.installReferer)) {
            return;
        }
        TLRPC$TL_help_getRecentMeUrls tLRPC$TL_help_getRecentMeUrls = new TLRPC$TL_help_getRecentMeUrls();
        tLRPC$TL_help_getRecentMeUrls.referer = this.installReferer;
        getConnectionsManager().sendRequest(tLRPC$TL_help_getRecentMeUrls, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda254
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadHintDialogs$161(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadHintDialogs$161(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda140
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$loadHintDialogs$160(tLObject);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadHintDialogs$160(TLObject tLObject) {
        TLRPC$TL_help_recentMeUrls tLRPC$TL_help_recentMeUrls = (TLRPC$TL_help_recentMeUrls) tLObject;
        putUsers(tLRPC$TL_help_recentMeUrls.users, false);
        putChats(tLRPC$TL_help_recentMeUrls.chats, false);
        this.hintDialogs.clear();
        this.hintDialogs.addAll(tLRPC$TL_help_recentMeUrls.urls);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private TLRPC$TL_dialogFolder ensureFolderDialogExists(int i, boolean[] zArr) {
        if (i == 0) {
            return null;
        }
        long makeFolderDialogId = DialogObject.makeFolderDialogId(i);
        TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(makeFolderDialogId);
        if (tLRPC$Dialog instanceof TLRPC$TL_dialogFolder) {
            if (zArr != null) {
                zArr[0] = false;
            }
            return (TLRPC$TL_dialogFolder) tLRPC$Dialog;
        }
        if (zArr != null) {
            zArr[0] = true;
        }
        TLRPC$TL_dialogFolder tLRPC$TL_dialogFolder = new TLRPC$TL_dialogFolder();
        tLRPC$TL_dialogFolder.id = makeFolderDialogId;
        tLRPC$TL_dialogFolder.peer = new TLRPC$TL_peerUser();
        TLRPC$TL_folder tLRPC$TL_folder = new TLRPC$TL_folder();
        tLRPC$TL_dialogFolder.folder = tLRPC$TL_folder;
        tLRPC$TL_folder.id = i;
        tLRPC$TL_folder.title = LocaleController.getString("ArchivedChats", R.string.ArchivedChats);
        tLRPC$TL_dialogFolder.pinned = true;
        int i2 = 0;
        for (int i3 = 0; i3 < this.allDialogs.size(); i3++) {
            TLRPC$Dialog tLRPC$Dialog2 = this.allDialogs.get(i3);
            if (!tLRPC$Dialog2.pinned) {
                if (tLRPC$Dialog2.id != this.promoDialogId) {
                    break;
                }
            } else {
                i2 = Math.max(tLRPC$Dialog2.pinnedNum, i2);
            }
        }
        tLRPC$TL_dialogFolder.pinnedNum = i2 + 1;
        TLRPC$TL_messages_dialogs tLRPC$TL_messages_dialogs = new TLRPC$TL_messages_dialogs();
        tLRPC$TL_messages_dialogs.dialogs.add(tLRPC$TL_dialogFolder);
        getMessagesStorage().putDialogs(tLRPC$TL_messages_dialogs, 1);
        this.dialogs_dict.put(makeFolderDialogId, tLRPC$TL_dialogFolder);
        this.allDialogs.add(0, tLRPC$TL_dialogFolder);
        return tLRPC$TL_dialogFolder;
    }

    /* renamed from: removeFolder */
    public void lambda$onFolderEmpty$162(int i) {
        long makeFolderDialogId = DialogObject.makeFolderDialogId(i);
        TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(makeFolderDialogId);
        if (tLRPC$Dialog == null) {
            return;
        }
        this.dialogs_dict.remove(makeFolderDialogId);
        this.allDialogs.remove(tLRPC$Dialog);
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.folderBecomeEmpty, Integer.valueOf(i));
    }

    public void onFolderEmpty(final int i) {
        if (getUserConfig().getDialogLoadOffsets(i)[0] == 2147483647L) {
            lambda$onFolderEmpty$162(i);
        } else {
            loadDialogs(i, 0, 10, false, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda41
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$onFolderEmpty$162(i);
                }
            });
        }
    }

    public void checkIfFolderEmpty(int i) {
        if (i == 0) {
            return;
        }
        getMessagesStorage().checkIfFolderEmpty(i);
    }

    public int addDialogToFolder(long j, int i, int i2, long j2) {
        ArrayList<Long> arrayList = new ArrayList<>(1);
        arrayList.add(Long.valueOf(j));
        return addDialogToFolder(arrayList, i, i2, null, j2);
    }

    /* JADX WARN: Removed duplicated region for block: B:131:0x0129  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x0145 A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:134:0x0147  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int addDialogToFolder(java.util.ArrayList<java.lang.Long> r24, int r25, int r26, java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputFolderPeer> r27, long r28) {
        /*
            Method dump skipped, instructions count: 333
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.addDialogToFolder(java.util.ArrayList, int, int, java.util.ArrayList, long):int");
    }

    public /* synthetic */ void lambda$addDialogToFolder$163(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            processUpdates((TLRPC$Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void loadDialogs(int i, int i2, int i3, boolean z) {
        loadDialogs(i, i2, i3, z, null);
    }

    public void loadDialogs(final int i, int i2, final int i3, boolean z, final Runnable runnable) {
        long j;
        if (this.loadingDialogs.get(i) || this.resetingDialogs) {
            return;
        }
        boolean z2 = true;
        this.loadingDialogs.put(i, true);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i4 = NotificationCenter.dialogsNeedReload;
        notificationCenter.postNotificationName(i4, new Object[0]);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("folderId = " + i + " load cacheOffset = " + i2 + " count = " + i3 + " cache = " + z);
        }
        if (z) {
            MessagesStorage messagesStorage = getMessagesStorage();
            int i5 = i2 == 0 ? 0 : this.nextDialogsCacheOffset.get(i, 0);
            if (i != 0 || i2 != 0) {
                z2 = false;
            }
            messagesStorage.getDialogs(i, i5, i3, z2);
            return;
        }
        TLRPC$TL_messages_getDialogs tLRPC$TL_messages_getDialogs = new TLRPC$TL_messages_getDialogs();
        tLRPC$TL_messages_getDialogs.limit = i3;
        tLRPC$TL_messages_getDialogs.exclude_pinned = true;
        if (i != 0) {
            tLRPC$TL_messages_getDialogs.flags |= 2;
            tLRPC$TL_messages_getDialogs.folder_id = i;
        }
        long[] dialogLoadOffsets = getUserConfig().getDialogLoadOffsets(i);
        if (dialogLoadOffsets[0] != -1) {
            if (dialogLoadOffsets[0] == 2147483647L) {
                this.dialogsEndReached.put(i, true);
                this.serverDialogsEndReached.put(i, true);
                this.loadingDialogs.put(i, false);
                getNotificationCenter().postNotificationName(i4, new Object[0]);
                return;
            }
            int i6 = (int) dialogLoadOffsets[0];
            tLRPC$TL_messages_getDialogs.offset_id = i6;
            tLRPC$TL_messages_getDialogs.offset_date = (int) dialogLoadOffsets[1];
            if (i6 == 0) {
                tLRPC$TL_messages_getDialogs.offset_peer = new TLRPC$TL_inputPeerEmpty();
            } else {
                if (dialogLoadOffsets[4] != 0) {
                    TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
                    tLRPC$TL_messages_getDialogs.offset_peer = tLRPC$TL_inputPeerChannel;
                    tLRPC$TL_inputPeerChannel.channel_id = dialogLoadOffsets[4];
                } else if (dialogLoadOffsets[2] != 0) {
                    TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
                    tLRPC$TL_messages_getDialogs.offset_peer = tLRPC$TL_inputPeerUser;
                    tLRPC$TL_inputPeerUser.user_id = dialogLoadOffsets[2];
                } else {
                    TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
                    tLRPC$TL_messages_getDialogs.offset_peer = tLRPC$TL_inputPeerChat;
                    tLRPC$TL_inputPeerChat.chat_id = dialogLoadOffsets[3];
                }
                tLRPC$TL_messages_getDialogs.offset_peer.access_hash = dialogLoadOffsets[5];
            }
        } else {
            ArrayList<TLRPC$Dialog> dialogs = getDialogs(i);
            int size = dialogs.size() - 1;
            while (true) {
                if (size < 0) {
                    z2 = false;
                    break;
                }
                TLRPC$Dialog tLRPC$Dialog = dialogs.get(size);
                if (!tLRPC$Dialog.pinned && !DialogObject.isEncryptedDialog(tLRPC$Dialog.id) && tLRPC$Dialog.top_message > 0) {
                    ArrayList<MessageObject> arrayList = this.dialogMessage.get(tLRPC$Dialog.id);
                    MessageObject messageObject = null;
                    if (arrayList != null) {
                        for (int i7 = 0; i7 < arrayList.size(); i7++) {
                            MessageObject messageObject2 = arrayList.get(i7);
                            if (messageObject2 != null && (messageObject == null || messageObject2.getId() > messageObject.getId())) {
                                messageObject = arrayList.get(i7);
                            }
                        }
                    }
                    if (messageObject != null && messageObject.getId() > 0) {
                        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
                        tLRPC$TL_messages_getDialogs.offset_date = tLRPC$Message.date;
                        tLRPC$TL_messages_getDialogs.offset_id = tLRPC$Message.id;
                        TLRPC$Peer tLRPC$Peer = tLRPC$Message.peer_id;
                        long j2 = tLRPC$Peer.channel_id;
                        if (j2 == 0) {
                            j2 = tLRPC$Peer.chat_id;
                            if (j2 == 0) {
                                j = tLRPC$Peer.user_id;
                                tLRPC$TL_messages_getDialogs.offset_peer = getInputPeer(j);
                            }
                        }
                        j = -j2;
                        tLRPC$TL_messages_getDialogs.offset_peer = getInputPeer(j);
                    }
                }
                size--;
            }
            if (!z2) {
                tLRPC$TL_messages_getDialogs.offset_peer = new TLRPC$TL_inputPeerEmpty();
            }
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda274
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadDialogs$164(i, i3, runnable, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadDialogs$164(int i, int i2, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Dialogs tLRPC$messages_Dialogs = (TLRPC$messages_Dialogs) tLObject;
            processLoadedDialogs(tLRPC$messages_Dialogs, null, i, 0, i2, 0, false, false, false);
            if (runnable == null || !tLRPC$messages_Dialogs.dialogs.isEmpty()) {
                return;
            }
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public void loadGlobalNotificationsSettings() {
        if (this.loadingNotificationSettings == 0 && !getUserConfig().notificationsSettingsLoaded) {
            SharedPreferences notificationsSettings = getNotificationsSettings(this.currentAccount);
            SharedPreferences.Editor editor = null;
            if (notificationsSettings.contains("EnableGroup")) {
                boolean z = notificationsSettings.getBoolean("EnableGroup", true);
                SharedPreferences.Editor edit = notificationsSettings.edit();
                if (!z) {
                    edit.putInt("EnableGroup2", Integer.MAX_VALUE);
                    edit.putInt("EnableChannel2", Integer.MAX_VALUE);
                }
                edit.remove("EnableGroup").commit();
                editor = edit;
            }
            if (notificationsSettings.contains("EnableAll")) {
                boolean z2 = notificationsSettings.getBoolean("EnableAll", true);
                if (editor == null) {
                    editor = notificationsSettings.edit();
                }
                if (!z2) {
                    editor.putInt("EnableAll2", Integer.MAX_VALUE);
                }
                editor.remove("EnableAll").commit();
            }
            if (editor != null) {
                editor.commit();
            }
            this.loadingNotificationSettings = 3;
            for (final int i = 0; i < 3; i++) {
                TLRPC$TL_account_getNotifySettings tLRPC$TL_account_getNotifySettings = new TLRPC$TL_account_getNotifySettings();
                if (i == 0) {
                    tLRPC$TL_account_getNotifySettings.peer = new TLRPC$TL_inputNotifyChats();
                } else if (i == 1) {
                    tLRPC$TL_account_getNotifySettings.peer = new TLRPC$TL_inputNotifyUsers();
                } else {
                    tLRPC$TL_account_getNotifySettings.peer = new TLRPC$TL_inputNotifyBroadcasts();
                }
                getConnectionsManager().sendRequest(tLRPC$TL_account_getNotifySettings, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda268
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$loadGlobalNotificationsSettings$166(i, tLObject, tLRPC$TL_error);
                    }
                });
            }
        }
        if (!getUserConfig().notificationsSignUpSettingsLoaded) {
            loadSignUpNotificationsSettings();
        }
    }

    public /* synthetic */ void lambda$loadGlobalNotificationsSettings$166(final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda145
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadGlobalNotificationsSettings$165(tLObject, i);
            }
        });
    }

    public /* synthetic */ void lambda$loadGlobalNotificationsSettings$165(TLObject tLObject, int i) {
        if (tLObject != null) {
            this.loadingNotificationSettings--;
            TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = (TLRPC$TL_peerNotifySettings) tLObject;
            SharedPreferences.Editor edit = this.notificationsPreferences.edit();
            if (i == 0) {
                if ((tLRPC$TL_peerNotifySettings.flags & 1) != 0) {
                    edit.putBoolean("EnablePreviewGroup", tLRPC$TL_peerNotifySettings.show_previews);
                }
                if ((tLRPC$TL_peerNotifySettings.flags & 4) != 0) {
                    edit.putInt("EnableGroup2", tLRPC$TL_peerNotifySettings.mute_until);
                }
            } else if (i == 1) {
                if ((tLRPC$TL_peerNotifySettings.flags & 1) != 0) {
                    edit.putBoolean("EnablePreviewAll", tLRPC$TL_peerNotifySettings.show_previews);
                }
                if ((tLRPC$TL_peerNotifySettings.flags & 4) != 0) {
                    edit.putInt("EnableAll2", tLRPC$TL_peerNotifySettings.mute_until);
                }
            } else {
                if ((tLRPC$TL_peerNotifySettings.flags & 1) != 0) {
                    edit.putBoolean("EnablePreviewChannel", tLRPC$TL_peerNotifySettings.show_previews);
                }
                if ((tLRPC$TL_peerNotifySettings.flags & 4) != 0) {
                    edit.putInt("EnableChannel2", tLRPC$TL_peerNotifySettings.mute_until);
                }
            }
            getNotificationsController().getNotificationsSettingsFacade().applySoundSettings(tLRPC$TL_peerNotifySettings.android_sound, edit, 0L, 0, i, false);
            edit.commit();
            if (this.loadingNotificationSettings != 0) {
                return;
            }
            getUserConfig().notificationsSettingsLoaded = true;
            getUserConfig().saveConfig(false);
        }
    }

    public void loadSignUpNotificationsSettings() {
        if (!this.loadingNotificationSignUpSettings) {
            this.loadingNotificationSignUpSettings = true;
            getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_account_getContactSignUpNotification
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
                    return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                    abstractSerializedData.writeInt32(constructor);
                }
            }, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda248
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$loadSignUpNotificationsSettings$168(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadSignUpNotificationsSettings$168(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda141
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadSignUpNotificationsSettings$167(tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$loadSignUpNotificationsSettings$167(TLObject tLObject) {
        this.loadingNotificationSignUpSettings = false;
        SharedPreferences.Editor edit = this.notificationsPreferences.edit();
        boolean z = tLObject instanceof TLRPC$TL_boolFalse;
        this.enableJoined = z;
        edit.putBoolean("EnableContactJoined", z);
        edit.commit();
        getUserConfig().notificationsSignUpSettingsLoaded = true;
        getUserConfig().saveConfig(false);
    }

    public void forceResetDialogs() {
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        getNotificationsController().deleteAllNotificationChannels();
    }

    public void loadUnknownDialog(TLRPC$InputPeer tLRPC$InputPeer, long j) {
        NativeByteBuffer nativeByteBuffer;
        if (tLRPC$InputPeer == null) {
            return;
        }
        final long peerDialogId = DialogObject.getPeerDialogId(tLRPC$InputPeer);
        if (this.gettingUnknownDialogs.indexOfKey(peerDialogId) >= 0) {
            return;
        }
        this.gettingUnknownDialogs.put(peerDialogId, Boolean.TRUE);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load unknown dialog " + peerDialogId);
        }
        TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs = new TLRPC$TL_messages_getPeerDialogs();
        TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
        tLRPC$TL_inputDialogPeer.peer = tLRPC$InputPeer;
        tLRPC$TL_messages_getPeerDialogs.peers.add(tLRPC$TL_inputDialogPeer);
        if (j == 0) {
            NativeByteBuffer nativeByteBuffer2 = null;
            try {
                nativeByteBuffer = new NativeByteBuffer(tLRPC$InputPeer.getObjectSize() + 4);
            } catch (Exception e) {
                e = e;
            }
            try {
                nativeByteBuffer.writeInt32(15);
                tLRPC$InputPeer.serializeToStream(nativeByteBuffer);
            } catch (Exception e2) {
                e = e2;
                nativeByteBuffer2 = nativeByteBuffer;
                FileLog.e(e);
                nativeByteBuffer = nativeByteBuffer2;
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                final long j2 = j;
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getPeerDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda303
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$loadUnknownDialog$169(j2, peerDialogId, tLObject, tLRPC$TL_error);
                    }
                });
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        final long j22 = j;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getPeerDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda303
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadUnknownDialog$169(j22, peerDialogId, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadUnknownDialog$169(long j, long j2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs = (TLRPC$TL_messages_peerDialogs) tLObject;
            if (!tLRPC$TL_messages_peerDialogs.dialogs.isEmpty()) {
                TLRPC$TL_messages_dialogs tLRPC$TL_messages_dialogs = new TLRPC$TL_messages_dialogs();
                tLRPC$TL_messages_dialogs.dialogs.addAll(tLRPC$TL_messages_peerDialogs.dialogs);
                tLRPC$TL_messages_dialogs.messages.addAll(tLRPC$TL_messages_peerDialogs.messages);
                tLRPC$TL_messages_dialogs.users.addAll(tLRPC$TL_messages_peerDialogs.users);
                tLRPC$TL_messages_dialogs.chats.addAll(tLRPC$TL_messages_peerDialogs.chats);
                processLoadedDialogs(tLRPC$TL_messages_dialogs, null, ((TLRPC$TL_dialog) tLRPC$TL_messages_peerDialogs.dialogs.get(0)).folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_UNKNOWN, false, false, false);
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        this.gettingUnknownDialogs.delete(j2);
    }

    private void fetchFolderInLoadedPinnedDialogs(TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs) {
        TLRPC$InputPeer tLRPC$TL_inputPeerUser;
        int size = tLRPC$TL_messages_peerDialogs.dialogs.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC$Dialog tLRPC$Dialog = tLRPC$TL_messages_peerDialogs.dialogs.get(i2);
            if (tLRPC$Dialog instanceof TLRPC$TL_dialogFolder) {
                TLRPC$TL_dialogFolder tLRPC$TL_dialogFolder = (TLRPC$TL_dialogFolder) tLRPC$Dialog;
                long peerDialogId = DialogObject.getPeerDialogId(tLRPC$Dialog.peer);
                if (tLRPC$TL_dialogFolder.top_message == 0 || peerDialogId == 0) {
                    tLRPC$TL_messages_peerDialogs.dialogs.remove(tLRPC$TL_dialogFolder);
                } else {
                    int size2 = tLRPC$TL_messages_peerDialogs.messages.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        TLRPC$Message tLRPC$Message = tLRPC$TL_messages_peerDialogs.messages.get(i3);
                        if (peerDialogId == MessageObject.getDialogId(tLRPC$Message) && tLRPC$Dialog.top_message == tLRPC$Message.id) {
                            TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
                            tLRPC$TL_dialog.peer = tLRPC$Dialog.peer;
                            tLRPC$TL_dialog.top_message = tLRPC$Dialog.top_message;
                            tLRPC$TL_dialog.folder_id = tLRPC$TL_dialogFolder.folder.id;
                            tLRPC$TL_dialog.flags |= 16;
                            tLRPC$TL_messages_peerDialogs.dialogs.add(tLRPC$TL_dialog);
                            TLRPC$Peer tLRPC$Peer = tLRPC$Dialog.peer;
                            if (tLRPC$Peer instanceof TLRPC$TL_peerChannel) {
                                tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerChannel();
                                tLRPC$TL_inputPeerUser.channel_id = tLRPC$Dialog.peer.channel_id;
                                int size3 = tLRPC$TL_messages_peerDialogs.chats.size();
                                while (true) {
                                    if (i >= size3) {
                                        break;
                                    }
                                    TLRPC$Chat tLRPC$Chat = tLRPC$TL_messages_peerDialogs.chats.get(i);
                                    if (tLRPC$Chat.id == tLRPC$TL_inputPeerUser.channel_id) {
                                        tLRPC$TL_inputPeerUser.access_hash = tLRPC$Chat.access_hash;
                                        break;
                                    }
                                    i++;
                                }
                            } else if (tLRPC$Peer instanceof TLRPC$TL_peerChat) {
                                tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerChat();
                                tLRPC$TL_inputPeerUser.chat_id = tLRPC$Dialog.peer.chat_id;
                            } else {
                                tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
                                tLRPC$TL_inputPeerUser.user_id = tLRPC$Dialog.peer.user_id;
                                int size4 = tLRPC$TL_messages_peerDialogs.users.size();
                                while (true) {
                                    if (i >= size4) {
                                        break;
                                    }
                                    TLRPC$User tLRPC$User = tLRPC$TL_messages_peerDialogs.users.get(i);
                                    if (tLRPC$User.id == tLRPC$TL_inputPeerUser.user_id) {
                                        tLRPC$TL_inputPeerUser.access_hash = tLRPC$User.access_hash;
                                        break;
                                    }
                                    i++;
                                }
                            }
                            loadUnknownDialog(tLRPC$TL_inputPeerUser, 0L);
                            return;
                        }
                    }
                    return;
                }
            }
        }
    }

    public /* synthetic */ void lambda$resetDialogs$170(int i, int i2, int i3, int i4, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            this.resetDialogsPinned = (TLRPC$TL_messages_peerDialogs) tLObject;
            for (int i5 = 0; i5 < this.resetDialogsPinned.dialogs.size(); i5++) {
                this.resetDialogsPinned.dialogs.get(i5).pinned = true;
            }
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    public /* synthetic */ void lambda$resetDialogs$171(int i, int i2, int i3, int i4, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            this.resetDialogsAll = (TLRPC$messages_Dialogs) tLObject;
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:192:0x0157  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x0206  */
    /* JADX WARN: Removed duplicated region for block: B:238:0x0230  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void resetDialogs(boolean r27, final int r28, final int r29, final int r30, final int r31) {
        /*
            Method dump skipped, instructions count: 765
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.resetDialogs(boolean, int, int, int, int):void");
    }

    public void completeDialogsReset(final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, int i, int i2, final int i3, final int i4, final int i5, final LongSparseArray<TLRPC$Dialog> longSparseArray, final LongSparseArray<ArrayList<MessageObject>> longSparseArray2, TLRPC$Message tLRPC$Message) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda44
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$completeDialogsReset$173(i3, i4, i5, tLRPC$messages_Dialogs, longSparseArray, longSparseArray2);
            }
        });
    }

    public /* synthetic */ void lambda$completeDialogsReset$173(int i, int i2, int i3, final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2) {
        this.gettingDifference = false;
        getMessagesStorage().setLastPtsValue(i);
        getMessagesStorage().setLastDateValue(i2);
        getMessagesStorage().setLastQtsValue(i3);
        getDifference();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda196
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$completeDialogsReset$172(tLRPC$messages_Dialogs, longSparseArray, longSparseArray2);
            }
        });
    }

    public /* synthetic */ void lambda$completeDialogsReset$172(TLRPC$messages_Dialogs tLRPC$messages_Dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        TLRPC$Dialog tLRPC$Dialog;
        this.resetingDialogs = false;
        applyDialogsNotificationsSettings(tLRPC$messages_Dialogs.dialogs);
        MediaDataController mediaDataController = getMediaDataController();
        mediaDataController.clearAllDrafts(false);
        mediaDataController.loadDraftsIfNeed();
        putUsers(tLRPC$messages_Dialogs.users, false);
        putChats(tLRPC$messages_Dialogs.chats, false);
        for (int i = 0; i < this.allDialogs.size(); i++) {
            TLRPC$Dialog tLRPC$Dialog2 = this.allDialogs.get(i);
            if (!DialogObject.isEncryptedDialog(tLRPC$Dialog2.id)) {
                this.dialogs_dict.remove(tLRPC$Dialog2.id);
                ArrayList<MessageObject> arrayList = this.dialogMessage.get(tLRPC$Dialog2.id);
                this.dialogMessage.remove(tLRPC$Dialog2.id);
                if (arrayList != null) {
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        MessageObject messageObject = arrayList.get(i2);
                        if (messageObject != null) {
                            if (messageObject.messageOwner.peer_id.channel_id == 0) {
                                this.dialogMessagesByIds.remove(messageObject.getId());
                            }
                            long j = messageObject.messageOwner.random_id;
                            if (j != 0) {
                                this.dialogMessagesByRandomIds.remove(j);
                            }
                        }
                    }
                }
            }
        }
        for (int i3 = 0; i3 < longSparseArray.size(); i3++) {
            long keyAt = longSparseArray.keyAt(i3);
            TLRPC$Dialog tLRPC$Dialog3 = (TLRPC$Dialog) longSparseArray.valueAt(i3);
            TLRPC$DraftMessage tLRPC$DraftMessage = tLRPC$Dialog3.draft;
            if (tLRPC$DraftMessage instanceof TLRPC$TL_draftMessage) {
                tLRPC$Dialog = tLRPC$Dialog3;
                mediaDataController.saveDraft(tLRPC$Dialog3.id, 0, tLRPC$DraftMessage, null, false);
            } else {
                tLRPC$Dialog = tLRPC$Dialog3;
            }
            this.dialogs_dict.put(keyAt, tLRPC$Dialog);
            ArrayList<MessageObject> arrayList2 = (ArrayList) longSparseArray2.get(tLRPC$Dialog.id);
            this.dialogMessage.put(keyAt, arrayList2);
            if (arrayList2 != null) {
                for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                    MessageObject messageObject2 = arrayList2.get(i4);
                    if (messageObject2 != null && messageObject2.messageOwner.peer_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                        this.dialogsLoadedTillDate = Math.min(this.dialogsLoadedTillDate, messageObject2.messageOwner.date);
                        long j2 = messageObject2.messageOwner.random_id;
                        if (j2 != 0) {
                            this.dialogMessagesByRandomIds.put(j2, messageObject2);
                        }
                    }
                }
            }
        }
        this.allDialogs.clear();
        int size = this.dialogs_dict.size();
        for (int i5 = 0; i5 < size; i5++) {
            TLRPC$Dialog valueAt = this.dialogs_dict.valueAt(i5);
            if (this.deletingDialogs.indexOfKey(valueAt.id) < 0) {
                this.allDialogs.add(valueAt);
            }
        }
        sortDialogs(null);
        this.dialogsEndReached.put(0, true);
        this.serverDialogsEndReached.put(0, false);
        this.dialogsEndReached.put(1, true);
        this.serverDialogsEndReached.put(1, false);
        int totalDialogsCount = getUserConfig().getTotalDialogsCount(0);
        long[] dialogLoadOffsets = getUserConfig().getDialogLoadOffsets(0);
        if (totalDialogsCount < 400 && dialogLoadOffsets[0] != -1 && dialogLoadOffsets[0] != 2147483647L) {
            loadDialogs(0, 0, 100, false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private void migrateDialogs(final int i, int i2, long j, long j2, long j3, long j4) {
        if (this.migratingDialogs || i == -1) {
            return;
        }
        this.migratingDialogs = true;
        TLRPC$TL_messages_getDialogs tLRPC$TL_messages_getDialogs = new TLRPC$TL_messages_getDialogs();
        tLRPC$TL_messages_getDialogs.exclude_pinned = true;
        tLRPC$TL_messages_getDialogs.limit = 100;
        tLRPC$TL_messages_getDialogs.offset_id = i;
        tLRPC$TL_messages_getDialogs.offset_date = i2;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start migrate with id " + i + " date " + LocaleController.getInstance().formatterStats.format(i2 * 1000));
        }
        if (i == 0) {
            tLRPC$TL_messages_getDialogs.offset_peer = new TLRPC$TL_inputPeerEmpty();
        } else {
            if (j3 != 0) {
                TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
                tLRPC$TL_messages_getDialogs.offset_peer = tLRPC$TL_inputPeerChannel;
                tLRPC$TL_inputPeerChannel.channel_id = j3;
            } else if (j != 0) {
                TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
                tLRPC$TL_messages_getDialogs.offset_peer = tLRPC$TL_inputPeerUser;
                tLRPC$TL_inputPeerUser.user_id = j;
            } else {
                TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
                tLRPC$TL_messages_getDialogs.offset_peer = tLRPC$TL_inputPeerChat;
                tLRPC$TL_inputPeerChat.chat_id = j2;
            }
            tLRPC$TL_messages_getDialogs.offset_peer.access_hash = j4;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda266
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$migrateDialogs$177(i, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$migrateDialogs$177(final int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            final TLRPC$messages_Dialogs tLRPC$messages_Dialogs = (TLRPC$messages_Dialogs) tLObject;
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda194
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$migrateDialogs$175(tLRPC$messages_Dialogs, i);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$migrateDialogs$176();
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:261:0x0344 A[Catch: Exception -> 0x041f, TryCatch #0 {Exception -> 0x041f, blocks: (B:165:0x0004, B:166:0x001d, B:169:0x0029, B:171:0x0035, B:173:0x0062, B:176:0x0069, B:177:0x006c, B:179:0x0070, B:180:0x009b, B:182:0x00a7, B:190:0x00f1, B:191:0x0104, B:193:0x010c, B:195:0x011d, B:196:0x0122, B:197:0x012f, B:198:0x014e, B:200:0x0154, B:202:0x0164, B:205:0x0169, B:206:0x016f, B:208:0x0177, B:214:0x0198, B:211:0x0188, B:213:0x0195, B:215:0x019a, B:216:0x019e, B:218:0x01a5, B:219:0x01bf, B:221:0x01d5, B:222:0x01e1, B:225:0x01eb, B:231:0x01fd, B:232:0x024c, B:234:0x0256, B:237:0x027f, B:239:0x0295, B:241:0x029d, B:244:0x02ad, B:250:0x02bc, B:251:0x02f9, B:253:0x0301, B:259:0x032f, B:261:0x0344, B:262:0x035b, B:264:0x0363, B:266:0x0377, B:267:0x0381, B:286:0x040e, B:268:0x0384, B:270:0x038a, B:271:0x03a1, B:273:0x03a9, B:275:0x03bd, B:276:0x03c6, B:277:0x03c9, B:279:0x03cf, B:280:0x03e6, B:282:0x03ee, B:284:0x0402, B:285:0x040b, B:183:0x00aa, B:185:0x00ae, B:188:0x00b6), top: B:291:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:268:0x0384 A[Catch: Exception -> 0x041f, TryCatch #0 {Exception -> 0x041f, blocks: (B:165:0x0004, B:166:0x001d, B:169:0x0029, B:171:0x0035, B:173:0x0062, B:176:0x0069, B:177:0x006c, B:179:0x0070, B:180:0x009b, B:182:0x00a7, B:190:0x00f1, B:191:0x0104, B:193:0x010c, B:195:0x011d, B:196:0x0122, B:197:0x012f, B:198:0x014e, B:200:0x0154, B:202:0x0164, B:205:0x0169, B:206:0x016f, B:208:0x0177, B:214:0x0198, B:211:0x0188, B:213:0x0195, B:215:0x019a, B:216:0x019e, B:218:0x01a5, B:219:0x01bf, B:221:0x01d5, B:222:0x01e1, B:225:0x01eb, B:231:0x01fd, B:232:0x024c, B:234:0x0256, B:237:0x027f, B:239:0x0295, B:241:0x029d, B:244:0x02ad, B:250:0x02bc, B:251:0x02f9, B:253:0x0301, B:259:0x032f, B:261:0x0344, B:262:0x035b, B:264:0x0363, B:266:0x0377, B:267:0x0381, B:286:0x040e, B:268:0x0384, B:270:0x038a, B:271:0x03a1, B:273:0x03a9, B:275:0x03bd, B:276:0x03c6, B:277:0x03c9, B:279:0x03cf, B:280:0x03e6, B:282:0x03ee, B:284:0x0402, B:285:0x040b, B:183:0x00aa, B:185:0x00ae, B:188:0x00b6), top: B:291:0x0004 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$migrateDialogs$175(org.telegram.tgnet.TLRPC$messages_Dialogs r36, int r37) {
        /*
            Method dump skipped, instructions count: 1070
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$migrateDialogs$175(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void");
    }

    public /* synthetic */ void lambda$migrateDialogs$174() {
        this.migratingDialogs = false;
    }

    public /* synthetic */ void lambda$migrateDialogs$176() {
        this.migratingDialogs = false;
    }

    public void processLoadedDialogs(final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final ArrayList<TLRPC$EncryptedChat> arrayList, final int i, final int i2, final int i3, final int i4, final boolean z, final boolean z2, final boolean z3) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda45
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processLoadedDialogs$181(i, i4, tLRPC$messages_Dialogs, z, i3, arrayList, i2, z3, z2);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:299:0x0100, code lost:
        if (r2.date < r5.date) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:312:0x0127, code lost:
        if (r10 == (-r4.id)) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:320:0x0140, code lost:
        if (r4.migrated_to != null) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:424:0x0358, code lost:
        if (r5 == r0.id) goto L147;
     */
    /* JADX WARN: Removed duplicated region for block: B:324:0x0167  */
    /* JADX WARN: Removed duplicated region for block: B:325:0x016e  */
    /* JADX WARN: Removed duplicated region for block: B:441:0x0398  */
    /* JADX WARN: Removed duplicated region for block: B:450:0x03af  */
    /* JADX WARN: Removed duplicated region for block: B:453:0x03c2  */
    /* JADX WARN: Removed duplicated region for block: B:456:0x03ef  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processLoadedDialogs$181(final int r31, final int r32, final org.telegram.tgnet.TLRPC$messages_Dialogs r33, final boolean r34, final int r35, final java.util.ArrayList r36, final int r37, final boolean r38, final boolean r39) {
        /*
            Method dump skipped, instructions count: 1284
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedDialogs$181(int, int, org.telegram.tgnet.TLRPC$messages_Dialogs, boolean, int, java.util.ArrayList, int, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$processLoadedDialogs$178(TLRPC$messages_Dialogs tLRPC$messages_Dialogs, int i, boolean z, long[] jArr, int i2) {
        putUsers(tLRPC$messages_Dialogs.users, true);
        this.loadingDialogs.put(i, false);
        if (z) {
            this.dialogsEndReached.put(i, false);
            this.serverDialogsEndReached.put(i, false);
        } else if (jArr[0] == 2147483647L) {
            this.dialogsEndReached.put(i, true);
            this.serverDialogsEndReached.put(i, true);
        } else {
            loadDialogs(i, 0, i2, false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$processLoadedDialogs$179(TLRPC$Chat tLRPC$Chat) {
        checkChatInviter(tLRPC$Chat.id, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:451:0x024b, code lost:
        if (r8.size() == r7.size()) goto L147;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v1 */
    /* JADX WARN: Type inference failed for: r13v29 */
    /* JADX WARN: Type inference failed for: r13v30 */
    /* JADX WARN: Type inference failed for: r13v5 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processLoadedDialogs$180(org.telegram.tgnet.TLRPC$Message r26, int r27, org.telegram.tgnet.TLRPC$messages_Dialogs r28, java.util.ArrayList r29, boolean r30, int r31, androidx.collection.LongSparseArray r32, androidx.collection.LongSparseArray r33, androidx.collection.LongSparseArray r34, int r35, boolean r36, int r37, java.util.ArrayList r38) {
        /*
            Method dump skipped, instructions count: 1128
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processLoadedDialogs$180(org.telegram.tgnet.TLRPC$Message, int, org.telegram.tgnet.TLRPC$messages_Dialogs, java.util.ArrayList, boolean, int, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, int, boolean, int, java.util.ArrayList):void");
    }

    private void applyDialogNotificationsSettings(long j, int i, TLRPC$PeerNotifySettings tLRPC$PeerNotifySettings) {
        getNotificationsController().getNotificationsSettingsFacade().applyDialogNotificationsSettings(j, i, tLRPC$PeerNotifySettings);
    }

    private void applyDialogsNotificationsSettings(ArrayList<TLRPC$Dialog> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$Dialog tLRPC$Dialog = arrayList.get(i);
            if (tLRPC$Dialog.peer != null && (tLRPC$Dialog.notify_settings instanceof TLRPC$TL_peerNotifySettings)) {
                getNotificationsController().getNotificationsSettingsFacade().setSettingsForDialog(tLRPC$Dialog, tLRPC$Dialog.notify_settings);
            }
        }
    }

    public void reloadMentionsCountForChannel(final TLRPC$InputPeer tLRPC$InputPeer, final long j) {
        NativeByteBuffer nativeByteBuffer;
        Exception e;
        if (j == 0) {
            try {
                nativeByteBuffer = new NativeByteBuffer(tLRPC$InputPeer.getObjectSize() + 4);
            } catch (Exception e2) {
                nativeByteBuffer = null;
                e = e2;
            }
            try {
                nativeByteBuffer.writeInt32(22);
                tLRPC$InputPeer.serializeToStream(nativeByteBuffer);
            } catch (Exception e3) {
                e = e3;
                FileLog.e(e);
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                TLRPC$TL_messages_getUnreadMentions tLRPC$TL_messages_getUnreadMentions = new TLRPC$TL_messages_getUnreadMentions();
                tLRPC$TL_messages_getUnreadMentions.peer = tLRPC$InputPeer;
                tLRPC$TL_messages_getUnreadMentions.limit = 1;
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getUnreadMentions, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda334
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$reloadMentionsCountForChannel$182(tLRPC$InputPeer, j, tLObject, tLRPC$TL_error);
                    }
                });
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        TLRPC$TL_messages_getUnreadMentions tLRPC$TL_messages_getUnreadMentions2 = new TLRPC$TL_messages_getUnreadMentions();
        tLRPC$TL_messages_getUnreadMentions2.peer = tLRPC$InputPeer;
        tLRPC$TL_messages_getUnreadMentions2.limit = 1;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getUnreadMentions2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda334
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$reloadMentionsCountForChannel$182(tLRPC$InputPeer, j, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$reloadMentionsCountForChannel$182(TLRPC$InputPeer tLRPC$InputPeer, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
        if (tLRPC$messages_Messages != null) {
            int i = tLRPC$messages_Messages.count;
            if (i == 0) {
                i = tLRPC$messages_Messages.messages.size();
            }
            getMessagesStorage().resetMentionsCount(-tLRPC$InputPeer.channel_id, 0, i);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void reloadMentionsCountForChannels(final ArrayList<Long> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda117
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$reloadMentionsCountForChannels$183(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$reloadMentionsCountForChannels$183(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            reloadMentionsCountForChannel(getInputPeer(-((Long) arrayList.get(i)).longValue()), 0L);
        }
    }

    public void processDialogsUpdateRead(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda137
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processDialogsUpdateRead$184(longSparseIntArray, longSparseIntArray2);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:197:0x0164, code lost:
        r3 = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processDialogsUpdateRead$184(org.telegram.messenger.support.LongSparseIntArray r12, org.telegram.messenger.support.LongSparseIntArray r13) {
        /*
            Method dump skipped, instructions count: 410
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processDialogsUpdateRead$184(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray):void");
    }

    public void checkLastDialogMessage(final TLRPC$Dialog tLRPC$Dialog, TLRPC$InputPeer tLRPC$InputPeer, final long j) {
        NativeByteBuffer nativeByteBuffer;
        Exception e;
        if (DialogObject.isEncryptedDialog(tLRPC$Dialog.id) || this.checkingLastMessagesDialogs.indexOfKey(tLRPC$Dialog.id) >= 0) {
            return;
        }
        TLRPC$TL_messages_getHistory tLRPC$TL_messages_getHistory = new TLRPC$TL_messages_getHistory();
        if (tLRPC$InputPeer == null) {
            tLRPC$InputPeer = getInputPeer(tLRPC$Dialog.id);
        }
        tLRPC$TL_messages_getHistory.peer = tLRPC$InputPeer;
        if (tLRPC$InputPeer == null) {
            return;
        }
        tLRPC$TL_messages_getHistory.limit = 1;
        this.checkingLastMessagesDialogs.put(tLRPC$Dialog.id, Boolean.TRUE);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("checkLastDialogMessage for " + tLRPC$Dialog.id);
        }
        if (j == 0) {
            try {
                nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_messages_getHistory.peer.getObjectSize() + 60);
            } catch (Exception e2) {
                nativeByteBuffer = null;
                e = e2;
            }
            try {
                nativeByteBuffer.writeInt32(14);
                nativeByteBuffer.writeInt64(tLRPC$Dialog.id);
                nativeByteBuffer.writeInt32(tLRPC$Dialog.top_message);
                nativeByteBuffer.writeInt32(tLRPC$Dialog.read_inbox_max_id);
                nativeByteBuffer.writeInt32(tLRPC$Dialog.read_outbox_max_id);
                nativeByteBuffer.writeInt32(tLRPC$Dialog.unread_count);
                nativeByteBuffer.writeInt32(tLRPC$Dialog.last_message_date);
                nativeByteBuffer.writeInt32(tLRPC$Dialog.pts);
                nativeByteBuffer.writeInt32(tLRPC$Dialog.flags);
                nativeByteBuffer.writeBool(tLRPC$Dialog.pinned);
                nativeByteBuffer.writeInt32(tLRPC$Dialog.pinnedNum);
                nativeByteBuffer.writeInt32(tLRPC$Dialog.unread_mentions_count);
                nativeByteBuffer.writeBool(tLRPC$Dialog.unread_mark);
                nativeByteBuffer.writeInt32(tLRPC$Dialog.folder_id);
                tLRPC$TL_messages_getHistory.peer.serializeToStream(nativeByteBuffer);
            } catch (Exception e3) {
                e = e3;
                FileLog.e(e);
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getHistory, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda333
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MessagesController.this.lambda$checkLastDialogMessage$188(tLRPC$Dialog, j, tLObject, tLRPC$TL_error);
                    }
                });
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getHistory, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda333
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$checkLastDialogMessage$188(tLRPC$Dialog, j, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$checkLastDialogMessage$188(final TLRPC$Dialog tLRPC$Dialog, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            removeDeletedMessagesFromArray(tLRPC$Dialog.id, tLRPC$messages_Messages.messages);
            if (!tLRPC$messages_Messages.messages.isEmpty()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("checkLastDialogMessage for " + tLRPC$Dialog.id + " has message");
                }
                TLRPC$TL_messages_dialogs tLRPC$TL_messages_dialogs = new TLRPC$TL_messages_dialogs();
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(0);
                TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
                tLRPC$TL_dialog.flags = tLRPC$Dialog.flags;
                tLRPC$TL_dialog.top_message = tLRPC$Message.id;
                tLRPC$TL_dialog.last_message_date = tLRPC$Message.date;
                tLRPC$TL_dialog.notify_settings = tLRPC$Dialog.notify_settings;
                tLRPC$TL_dialog.pts = tLRPC$Dialog.pts;
                tLRPC$TL_dialog.unread_count = tLRPC$Dialog.unread_count;
                tLRPC$TL_dialog.unread_mark = tLRPC$Dialog.unread_mark;
                tLRPC$TL_dialog.unread_mentions_count = tLRPC$Dialog.unread_mentions_count;
                tLRPC$TL_dialog.unread_reactions_count = tLRPC$Dialog.unread_reactions_count;
                tLRPC$TL_dialog.read_inbox_max_id = tLRPC$Dialog.read_inbox_max_id;
                tLRPC$TL_dialog.read_outbox_max_id = tLRPC$Dialog.read_outbox_max_id;
                tLRPC$TL_dialog.pinned = tLRPC$Dialog.pinned;
                tLRPC$TL_dialog.pinnedNum = tLRPC$Dialog.pinnedNum;
                tLRPC$TL_dialog.folder_id = tLRPC$Dialog.folder_id;
                long j2 = tLRPC$Dialog.id;
                tLRPC$TL_dialog.id = j2;
                tLRPC$Message.dialog_id = j2;
                tLRPC$TL_messages_dialogs.users.addAll(tLRPC$messages_Messages.users);
                tLRPC$TL_messages_dialogs.chats.addAll(tLRPC$messages_Messages.chats);
                tLRPC$TL_messages_dialogs.dialogs.add(tLRPC$TL_dialog);
                tLRPC$TL_messages_dialogs.messages.addAll(tLRPC$messages_Messages.messages);
                tLRPC$TL_messages_dialogs.count = 1;
                processDialogsUpdate(tLRPC$TL_messages_dialogs, null, false);
                getMessagesStorage().putMessages(tLRPC$messages_Messages.messages, true, true, false, getDownloadController().getAutodownloadMask(), true, false, 0);
            } else {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda158
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$checkLastDialogMessage$186(tLRPC$Dialog);
                    }
                });
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda159
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$checkLastDialogMessage$187(tLRPC$Dialog);
            }
        });
    }

    public /* synthetic */ void lambda$checkLastDialogMessage$186(final TLRPC$Dialog tLRPC$Dialog) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("checkLastDialogMessage for " + tLRPC$Dialog.id + " has not message");
        }
        if (getMediaDataController().getDraft(tLRPC$Dialog.id, 0) == null) {
            TLRPC$Dialog tLRPC$Dialog2 = this.dialogs_dict.get(tLRPC$Dialog.id);
            if (tLRPC$Dialog2 == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("checkLastDialogMessage for " + tLRPC$Dialog.id + " current dialog not found");
                }
                getMessagesStorage().isDialogHasTopMessage(tLRPC$Dialog.id, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda157
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$checkLastDialogMessage$185(tLRPC$Dialog);
                    }
                });
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("checkLastDialogMessage for " + tLRPC$Dialog.id + " current dialog top message " + tLRPC$Dialog2.top_message);
            }
            if (tLRPC$Dialog2.top_message != 0) {
                return;
            }
            deleteDialog(tLRPC$Dialog.id, 3);
        }
    }

    public /* synthetic */ void lambda$checkLastDialogMessage$185(TLRPC$Dialog tLRPC$Dialog) {
        deleteDialog(tLRPC$Dialog.id, 3);
    }

    public /* synthetic */ void lambda$checkLastDialogMessage$187(TLRPC$Dialog tLRPC$Dialog) {
        this.checkingLastMessagesDialogs.delete(tLRPC$Dialog.id);
    }

    public void processDialogsUpdate(final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, ArrayList<TLRPC$EncryptedChat> arrayList, final boolean z) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda199
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processDialogsUpdate$190(tLRPC$messages_Dialogs, z);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:129:0x009d, code lost:
        if (org.telegram.messenger.ChatObject.isNotInChat(r5) != false) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:138:0x00b8, code lost:
        if (org.telegram.messenger.ChatObject.isNotInChat(r5) != false) goto L30;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processDialogsUpdate$190(final org.telegram.tgnet.TLRPC$messages_Dialogs r20, final boolean r21) {
        /*
            Method dump skipped, instructions count: 538
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processDialogsUpdate$190(org.telegram.tgnet.TLRPC$messages_Dialogs, boolean):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:349:0x02c7, code lost:
        if (r8.size() == r12.size()) goto L142;
     */
    /* JADX WARN: Removed duplicated region for block: B:387:0x037e  */
    /* JADX WARN: Removed duplicated region for block: B:405:0x0383 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processDialogsUpdate$189(org.telegram.tgnet.TLRPC$messages_Dialogs r20, androidx.collection.LongSparseArray r21, androidx.collection.LongSparseArray r22, boolean r23, org.telegram.messenger.support.LongSparseIntArray r24) {
        /*
            Method dump skipped, instructions count: 970
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processDialogsUpdate$189(org.telegram.tgnet.TLRPC$messages_Dialogs, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, boolean, org.telegram.messenger.support.LongSparseIntArray):void");
    }

    private int messagesMaxDate(ArrayList<MessageObject> arrayList) {
        TLRPC$Message tLRPC$Message;
        int i;
        int i2 = Integer.MIN_VALUE;
        for (int i3 = 0; arrayList != null && i3 < arrayList.size(); i3++) {
            MessageObject messageObject = arrayList.get(i3);
            if (messageObject != null && (tLRPC$Message = messageObject.messageOwner) != null && (i = tLRPC$Message.date) > i2) {
                i2 = i;
            }
        }
        return i2;
    }

    public void addToViewsQueue(final MessageObject messageObject) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda131
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$addToViewsQueue$191(messageObject);
            }
        });
    }

    public /* synthetic */ void lambda$addToViewsQueue$191(MessageObject messageObject) {
        long dialogId = messageObject.getDialogId();
        int id = messageObject.getId();
        ArrayList<Integer> arrayList = this.channelViewsToSend.get(dialogId);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.channelViewsToSend.put(dialogId, arrayList);
        }
        if (!arrayList.contains(Integer.valueOf(id))) {
            arrayList.add(Integer.valueOf(id));
        }
    }

    public void loadExtendedMediaForMessages(long j, ArrayList<MessageObject> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        TLRPC$TL_messages_getExtendedMedia tLRPC$TL_messages_getExtendedMedia = new TLRPC$TL_messages_getExtendedMedia();
        tLRPC$TL_messages_getExtendedMedia.peer = getInputPeer(j);
        for (int i = 0; i < arrayList.size(); i++) {
            tLRPC$TL_messages_getExtendedMedia.id.add(Integer.valueOf(arrayList.get(i).getId()));
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getExtendedMedia, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda238
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadExtendedMediaForMessages$192(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadExtendedMediaForMessages$192(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public void loadReactionsForMessages(long j, ArrayList<MessageObject> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        TLRPC$TL_messages_getMessagesReactions tLRPC$TL_messages_getMessagesReactions = new TLRPC$TL_messages_getMessagesReactions();
        tLRPC$TL_messages_getMessagesReactions.peer = getInputPeer(j);
        for (int i = 0; i < arrayList.size(); i++) {
            tLRPC$TL_messages_getMessagesReactions.id.add(Integer.valueOf(arrayList.get(i).getId()));
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessagesReactions, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda265
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadReactionsForMessages$193(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadReactionsForMessages$193(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            for (int i = 0; i < tLRPC$Updates.updates.size(); i++) {
                if (tLRPC$Updates.updates.get(i) instanceof TLRPC$TL_updateMessageReactions) {
                    ((TLRPC$TL_updateMessageReactions) tLRPC$Updates.updates.get(i)).updateUnreadState = false;
                }
            }
            processUpdates(tLRPC$Updates, false);
        }
    }

    public void addToPollsQueue(long j, ArrayList<MessageObject> arrayList) {
        int i;
        SparseArray<MessageObject> sparseArray = this.pollsToCheck.get(j);
        if (sparseArray == null) {
            sparseArray = new SparseArray<>();
            this.pollsToCheck.put(j, sparseArray);
            this.pollsToCheckSize++;
        }
        int size = sparseArray.size();
        for (int i2 = 0; i2 < size; i2++) {
            sparseArray.valueAt(i2).pollVisibleOnScreen = false;
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        int size2 = arrayList.size();
        boolean z = false;
        int i3 = Integer.MAX_VALUE;
        for (int i4 = 0; i4 < size2; i4++) {
            MessageObject messageObject = arrayList.get(i4);
            if (messageObject.type == 17) {
                TLRPC$Poll tLRPC$Poll = ((TLRPC$TL_messageMediaPoll) messageObject.messageOwner.media).poll;
                if (!tLRPC$Poll.closed && (i = tLRPC$Poll.close_date) != 0) {
                    if (i <= currentTime) {
                        z = true;
                    } else {
                        i3 = Math.min(i3, i - currentTime);
                    }
                }
                int id = messageObject.getId();
                MessageObject messageObject2 = sparseArray.get(id);
                if (messageObject2 != null) {
                    messageObject2.pollVisibleOnScreen = true;
                } else {
                    sparseArray.put(id, messageObject);
                }
            }
        }
        if (z) {
            this.lastViewsCheckTime = 0L;
        } else if (i3 < 5) {
            this.lastViewsCheckTime = Math.min(this.lastViewsCheckTime, System.currentTimeMillis() - ((5 - i3) * 1000));
        }
    }

    public void markMessageContentAsRead(MessageObject messageObject) {
        if (messageObject.scheduled) {
            return;
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (messageObject.messageOwner.mentioned) {
            getMessagesStorage().markMentionMessageAsRead(-messageObject.messageOwner.peer_id.channel_id, messageObject.getId(), messageObject.getDialogId());
        }
        arrayList.add(Integer.valueOf(messageObject.getId()));
        long dialogId = messageObject.getDialogId();
        getMessagesStorage().markMessagesContentAsRead(dialogId, arrayList, 0);
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, Long.valueOf(dialogId), arrayList);
        if (messageObject.getId() < 0) {
            markMessageAsRead(messageObject.getDialogId(), messageObject.messageOwner.random_id, Integer.MIN_VALUE);
        } else if (messageObject.messageOwner.peer_id.channel_id != 0) {
            TLRPC$TL_channels_readMessageContents tLRPC$TL_channels_readMessageContents = new TLRPC$TL_channels_readMessageContents();
            TLRPC$InputChannel inputChannel = getInputChannel(messageObject.messageOwner.peer_id.channel_id);
            tLRPC$TL_channels_readMessageContents.channel = inputChannel;
            if (inputChannel == null) {
                return;
            }
            tLRPC$TL_channels_readMessageContents.id.add(Integer.valueOf(messageObject.getId()));
            getConnectionsManager().sendRequest(tLRPC$TL_channels_readMessageContents, MessagesController$$ExternalSyntheticLambda363.INSTANCE);
        } else {
            TLRPC$TL_messages_readMessageContents tLRPC$TL_messages_readMessageContents = new TLRPC$TL_messages_readMessageContents();
            tLRPC$TL_messages_readMessageContents.id.add(Integer.valueOf(messageObject.getId()));
            getConnectionsManager().sendRequest(tLRPC$TL_messages_readMessageContents, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda250
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$markMessageContentAsRead$195(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$markMessageContentAsRead$195(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_affectedMessages tLRPC$TL_messages_affectedMessages = (TLRPC$TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tLRPC$TL_messages_affectedMessages.pts, -1, tLRPC$TL_messages_affectedMessages.pts_count);
        }
    }

    public void markMentionMessageAsRead(int i, long j, long j2) {
        getMessagesStorage().markMentionMessageAsRead(-j, i, j2);
        if (j != 0) {
            TLRPC$TL_channels_readMessageContents tLRPC$TL_channels_readMessageContents = new TLRPC$TL_channels_readMessageContents();
            TLRPC$InputChannel inputChannel = getInputChannel(j);
            tLRPC$TL_channels_readMessageContents.channel = inputChannel;
            if (inputChannel == null) {
                return;
            }
            tLRPC$TL_channels_readMessageContents.id.add(Integer.valueOf(i));
            getConnectionsManager().sendRequest(tLRPC$TL_channels_readMessageContents, MessagesController$$ExternalSyntheticLambda367.INSTANCE);
            return;
        }
        TLRPC$TL_messages_readMessageContents tLRPC$TL_messages_readMessageContents = new TLRPC$TL_messages_readMessageContents();
        tLRPC$TL_messages_readMessageContents.id.add(Integer.valueOf(i));
        getConnectionsManager().sendRequest(tLRPC$TL_messages_readMessageContents, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda264
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$markMentionMessageAsRead$197(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$markMentionMessageAsRead$197(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_affectedMessages tLRPC$TL_messages_affectedMessages = (TLRPC$TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tLRPC$TL_messages_affectedMessages.pts, -1, tLRPC$TL_messages_affectedMessages.pts_count);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:94:0x007b  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0098  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void markMessageAsRead2(long r15, int r17, org.telegram.tgnet.TLRPC$InputChannel r18, int r19, long r20) {
        /*
            r14 = this;
            r1 = r14
            r10 = r17
            r8 = r19
            if (r10 == 0) goto Lb2
            if (r8 > 0) goto Lb
            goto Lb2
        Lb:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r15)
            if (r0 == 0) goto L1c
            if (r18 != 0) goto L1c
            org.telegram.tgnet.TLRPC$InputChannel r0 = r14.getInputChannel(r15)
            if (r0 != 0) goto L1a
            return
        L1a:
            r11 = r0
            goto L1e
        L1c:
            r11 = r18
        L1e:
            r2 = 0
            int r0 = (r20 > r2 ? 1 : (r20 == r2 ? 0 : -1))
            if (r0 != 0) goto L60
            r2 = 0
            org.telegram.tgnet.NativeByteBuffer r3 = new org.telegram.tgnet.NativeByteBuffer     // Catch: java.lang.Exception -> L50
            r0 = 20
            if (r11 == 0) goto L30
            int r4 = r11.getObjectSize()     // Catch: java.lang.Exception -> L50
            goto L31
        L30:
            r4 = 0
        L31:
            int r0 = r0 + r4
            r3.<init>(r0)     // Catch: java.lang.Exception -> L50
            r0 = 23
            r3.writeInt32(r0)     // Catch: java.lang.Exception -> L4c
            r4 = r15
            r3.writeInt64(r4)     // Catch: java.lang.Exception -> L4a
            r3.writeInt32(r10)     // Catch: java.lang.Exception -> L4a
            r3.writeInt32(r8)     // Catch: java.lang.Exception -> L4a
            if (r11 == 0) goto L56
            r11.serializeToStream(r3)     // Catch: java.lang.Exception -> L4a
            goto L56
        L4a:
            r0 = move-exception
            goto L4e
        L4c:
            r0 = move-exception
            r4 = r15
        L4e:
            r2 = r3
            goto L52
        L50:
            r0 = move-exception
            r4 = r15
        L52:
            org.telegram.messenger.FileLog.e(r0)
            r3 = r2
        L56:
            org.telegram.messenger.MessagesStorage r0 = r14.getMessagesStorage()
            long r2 = r0.createPendingTask(r3)
            r12 = r2
            goto L63
        L60:
            r4 = r15
            r12 = r20
        L63:
            org.telegram.tgnet.ConnectionsManager r0 = r14.getConnectionsManager()
            int r7 = r0.getCurrentTime()
            org.telegram.messenger.MessagesStorage r2 = r14.getMessagesStorage()
            r9 = 0
            r3 = r15
            r5 = r17
            r6 = r7
            r8 = r19
            r2.createTaskForMid(r3, r5, r6, r7, r8, r9)
            if (r11 == 0) goto L98
            org.telegram.tgnet.TLRPC$TL_channels_readMessageContents r0 = new org.telegram.tgnet.TLRPC$TL_channels_readMessageContents
            r0.<init>()
            r0.channel = r11
            java.util.ArrayList<java.lang.Integer> r2 = r0.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r17)
            r2.add(r3)
            org.telegram.tgnet.ConnectionsManager r2 = r14.getConnectionsManager()
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda290 r3 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda290
            r3.<init>()
            r2.sendRequest(r0, r3)
            goto Lb2
        L98:
            org.telegram.tgnet.TLRPC$TL_messages_readMessageContents r0 = new org.telegram.tgnet.TLRPC$TL_messages_readMessageContents
            r0.<init>()
            java.util.ArrayList<java.lang.Integer> r2 = r0.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r17)
            r2.add(r3)
            org.telegram.tgnet.ConnectionsManager r2 = r14.getConnectionsManager()
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda289 r3 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda289
            r3.<init>()
            r2.sendRequest(r0, r3)
        Lb2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markMessageAsRead2(long, int, org.telegram.tgnet.TLRPC$InputChannel, int, long):void");
    }

    public /* synthetic */ void lambda$markMessageAsRead2$198(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public /* synthetic */ void lambda$markMessageAsRead2$199(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_affectedMessages tLRPC$TL_messages_affectedMessages = (TLRPC$TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tLRPC$TL_messages_affectedMessages.pts, -1, tLRPC$TL_messages_affectedMessages.pts_count);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void markMessageAsRead(long j, long j2, int i) {
        TLRPC$EncryptedChat encryptedChat;
        if (j2 == 0 || j == 0) {
            return;
        }
        if ((i <= 0 && i != Integer.MIN_VALUE) || !DialogObject.isEncryptedDialog(j) || (encryptedChat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j)))) == null) {
            return;
        }
        ArrayList<Long> arrayList = new ArrayList<>();
        arrayList.add(Long.valueOf(j2));
        getSecretChatHelper().sendMessagesReadMessage(encryptedChat, arrayList, null);
        if (i <= 0) {
            return;
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        getMessagesStorage().createTaskForSecretChat(encryptedChat.id, currentTime, currentTime, 0, arrayList);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void completeReadTask(ReadTask readTask) {
        TLRPC$TL_messages_readHistory tLRPC$TL_messages_readHistory;
        if (readTask.replyId != 0) {
            TLRPC$TL_messages_readDiscussion tLRPC$TL_messages_readDiscussion = new TLRPC$TL_messages_readDiscussion();
            tLRPC$TL_messages_readDiscussion.msg_id = (int) readTask.replyId;
            tLRPC$TL_messages_readDiscussion.peer = getInputPeer(readTask.dialogId);
            tLRPC$TL_messages_readDiscussion.read_max_id = readTask.maxId;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_readDiscussion, MessagesController$$ExternalSyntheticLambda361.INSTANCE);
        } else if (!DialogObject.isEncryptedDialog(readTask.dialogId)) {
            TLRPC$InputPeer inputPeer = getInputPeer(readTask.dialogId);
            if (inputPeer instanceof TLRPC$TL_inputPeerChannel) {
                TLRPC$TL_channels_readHistory tLRPC$TL_channels_readHistory = new TLRPC$TL_channels_readHistory();
                tLRPC$TL_channels_readHistory.channel = getInputChannel(-readTask.dialogId);
                tLRPC$TL_channels_readHistory.max_id = readTask.maxId;
                tLRPC$TL_messages_readHistory = tLRPC$TL_channels_readHistory;
            } else {
                TLRPC$TL_messages_readHistory tLRPC$TL_messages_readHistory2 = new TLRPC$TL_messages_readHistory();
                tLRPC$TL_messages_readHistory2.peer = inputPeer;
                tLRPC$TL_messages_readHistory2.max_id = readTask.maxId;
                tLRPC$TL_messages_readHistory = tLRPC$TL_messages_readHistory2;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_readHistory, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda239
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$completeReadTask$201(tLObject, tLRPC$TL_error);
                }
            });
        } else {
            TLRPC$EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(readTask.dialogId)));
            byte[] bArr = encryptedChat.auth_key;
            if (bArr == null || bArr.length <= 1 || !(encryptedChat instanceof TLRPC$TL_encryptedChat)) {
                return;
            }
            TLRPC$TL_messages_readEncryptedHistory tLRPC$TL_messages_readEncryptedHistory = new TLRPC$TL_messages_readEncryptedHistory();
            TLRPC$TL_inputEncryptedChat tLRPC$TL_inputEncryptedChat = new TLRPC$TL_inputEncryptedChat();
            tLRPC$TL_messages_readEncryptedHistory.peer = tLRPC$TL_inputEncryptedChat;
            tLRPC$TL_inputEncryptedChat.chat_id = encryptedChat.id;
            tLRPC$TL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
            tLRPC$TL_messages_readEncryptedHistory.max_date = readTask.maxDate;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_readEncryptedHistory, MessagesController$$ExternalSyntheticLambda351.INSTANCE);
        }
    }

    public /* synthetic */ void lambda$completeReadTask$201(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null || !(tLObject instanceof TLRPC$TL_messages_affectedMessages)) {
            return;
        }
        TLRPC$TL_messages_affectedMessages tLRPC$TL_messages_affectedMessages = (TLRPC$TL_messages_affectedMessages) tLObject;
        processNewDifferenceParams(-1, tLRPC$TL_messages_affectedMessages.pts, -1, tLRPC$TL_messages_affectedMessages.pts_count);
    }

    private void checkReadTasks() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int size = this.readTasks.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            ReadTask readTask = this.readTasks.get(i2);
            if (readTask.sendRequestTime <= elapsedRealtime) {
                completeReadTask(readTask);
                this.readTasks.remove(i2);
                this.readTasksMap.remove(readTask.dialogId);
                i2--;
                size--;
            }
            i2++;
        }
        int size2 = this.repliesReadTasks.size();
        while (i < size2) {
            ReadTask readTask2 = this.repliesReadTasks.get(i);
            if (readTask2.sendRequestTime <= elapsedRealtime) {
                completeReadTask(readTask2);
                this.repliesReadTasks.remove(i);
                this.threadsReadTasksMap.remove(readTask2.dialogId + "_" + readTask2.replyId);
                i += -1;
                size2 += -1;
            }
            i++;
        }
    }

    public void markDialogAsReadNow(final long j, final int i) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$markDialogAsReadNow$203(i, j);
            }
        });
    }

    public /* synthetic */ void lambda$markDialogAsReadNow$203(int i, long j) {
        if (i != 0) {
            String str = j + "_" + i;
            ReadTask readTask = this.threadsReadTasksMap.get(str);
            if (readTask == null) {
                return;
            }
            completeReadTask(readTask);
            this.repliesReadTasks.remove(readTask);
            this.threadsReadTasksMap.remove(str);
            return;
        }
        ReadTask readTask2 = this.readTasksMap.get(j);
        if (readTask2 == null) {
            return;
        }
        completeReadTask(readTask2);
        this.readTasks.remove(readTask2);
        this.readTasksMap.remove(j);
    }

    public void markMentionsAsRead(long j, int i) {
        if (DialogObject.isEncryptedDialog(j)) {
            return;
        }
        getMessagesStorage().resetMentionsCount(j, i, 0);
        TLRPC$TL_messages_readMentions tLRPC$TL_messages_readMentions = new TLRPC$TL_messages_readMentions();
        tLRPC$TL_messages_readMentions.peer = getInputPeer(j);
        if (i != 0) {
            tLRPC$TL_messages_readMentions.top_msg_id = i;
            tLRPC$TL_messages_readMentions.flags |= 1;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_readMentions, MessagesController$$ExternalSyntheticLambda362.INSTANCE);
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x000c, code lost:
        if (r18 != Integer.MAX_VALUE) goto L5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0010, code lost:
        r11 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0072, code lost:
        if (r18 != Integer.MAX_VALUE) goto L5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void markDialogAsRead(final long r16, final int r18, final int r19, final int r20, final boolean r21, final int r22, final int r23, final boolean r24, int r25) {
        /*
            r15 = this;
            r8 = r15
            r9 = r18
            r10 = r20
            r7 = 2147483647(0x7fffffff, float:NaN)
            r11 = 1
            r12 = 0
            if (r22 == 0) goto L13
            if (r9 == r7) goto L10
            goto Lce
        L10:
            r11 = 0
            goto Lce
        L13:
            org.telegram.messenger.NotificationsController r0 = r15.getNotificationsController()
            boolean r0 = r0.showBadgeMessages
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r16)
            if (r0 != 0) goto L75
            if (r9 != 0) goto L22
            return
        L22:
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r8.dialogs_read_inbox_max
            java.lang.Long r1 = java.lang.Long.valueOf(r16)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L34
            java.lang.Integer r0 = java.lang.Integer.valueOf(r12)
        L34:
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r8.dialogs_read_inbox_max
            java.lang.Long r2 = java.lang.Long.valueOf(r16)
            int r0 = r0.intValue()
            int r0 = java.lang.Math.max(r0, r9)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r1.put(r2, r0)
            org.telegram.messenger.MessagesStorage r0 = r15.getMessagesStorage()
            r1 = r16
            r3 = r18
            r4 = r19
            r5 = r25
            r0.processPendingRead(r1, r3, r4, r5)
            org.telegram.messenger.MessagesStorage r0 = r15.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r13 = r0.getStorageQueue()
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda75 r14 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda75
            r0 = r14
            r1 = r15
            r2 = r16
            r4 = r23
            r5 = r18
            r6 = r21
            r0.<init>()
            r13.postRunnable(r14)
            if (r9 == r7) goto L10
            goto Lce
        L75:
            if (r10 != 0) goto L78
            return
        L78:
            int r0 = org.telegram.messenger.DialogObject.getEncryptedChatId(r16)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r12 = r15.getEncryptedChat(r0)
            org.telegram.messenger.MessagesStorage r0 = r15.getMessagesStorage()
            r1 = r16
            r3 = r18
            r4 = r19
            r5 = r25
            r0.processPendingRead(r1, r3, r4, r5)
            org.telegram.messenger.MessagesStorage r0 = r15.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r13 = r0.getStorageQueue()
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda77 r14 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda77
            r0 = r14
            r1 = r15
            r2 = r16
            r4 = r20
            r5 = r21
            r6 = r23
            r7 = r19
            r0.<init>()
            r13.postRunnable(r14)
            if (r12 == 0) goto Lce
            int r0 = r12.ttl
            if (r0 <= 0) goto Lce
            org.telegram.tgnet.ConnectionsManager r0 = r15.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            int r3 = java.lang.Math.max(r0, r10)
            org.telegram.messenger.MessagesStorage r0 = r15.getMessagesStorage()
            int r1 = r12.id
            r4 = 0
            r5 = 0
            r2 = r20
            r0.createTaskForSecretChat(r1, r2, r3, r4, r5)
        Lce:
            if (r11 == 0) goto Le6
            org.telegram.messenger.DispatchQueue r11 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda51 r12 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda51
            r0 = r12
            r1 = r15
            r2 = r22
            r3 = r16
            r5 = r24
            r6 = r20
            r7 = r18
            r0.<init>()
            r11.postRunnable(r12)
        Le6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markDialogAsRead(long, int, int, int, boolean, int, int, boolean, int):void");
    }

    public /* synthetic */ void lambda$markDialogAsRead$206(final long j, final int i, final int i2, final boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda74
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$markDialogAsRead$205(j, i, i2, z);
            }
        });
    }

    public /* synthetic */ void lambda$markDialogAsRead$205(long j, int i, int i2, boolean z) {
        TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
        if (tLRPC$Dialog != null) {
            int i3 = tLRPC$Dialog.unread_count;
            if (i == 0 || i2 >= tLRPC$Dialog.top_message) {
                tLRPC$Dialog.unread_count = 0;
            } else {
                int max = Math.max(i3 - i, 0);
                tLRPC$Dialog.unread_count = max;
                if (i2 != Integer.MIN_VALUE) {
                    int i4 = tLRPC$Dialog.top_message;
                    if (max > i4 - i2) {
                        tLRPC$Dialog.unread_count = i4 - i2;
                    }
                }
            }
            boolean z2 = tLRPC$Dialog.unread_mark;
            if (z2) {
                tLRPC$Dialog.unread_mark = false;
                getMessagesStorage().setDialogUnread(tLRPC$Dialog.id, false);
            }
            if ((i3 != 0 || z2) && tLRPC$Dialog.unread_count == 0) {
                if (!isDialogMuted(j, 0)) {
                    this.unreadUnmutedDialogs--;
                }
                int i5 = 0;
                while (true) {
                    DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                    if (i5 < dialogFilterArr.length) {
                        if (dialogFilterArr[i5] != null && (dialogFilterArr[i5].flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                            sortDialogs(null);
                            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            break;
                        }
                        i5++;
                    } else {
                        break;
                    }
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
        }
        if (!z) {
            getNotificationsController().processReadMessages(null, j, 0, i2, false);
            LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
            longSparseIntArray.put(j, 0);
            getNotificationsController().processDialogsUpdateRead(longSparseIntArray);
            return;
        }
        getNotificationsController().processReadMessages(null, j, 0, i2, true);
        LongSparseIntArray longSparseIntArray2 = new LongSparseIntArray(1);
        longSparseIntArray2.put(j, -1);
        getNotificationsController().processDialogsUpdateRead(longSparseIntArray2);
    }

    public /* synthetic */ void lambda$markDialogAsRead$208(final long j, final int i, final boolean z, final int i2, final int i3) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda76
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$markDialogAsRead$207(j, i, z, i2, i3);
            }
        });
    }

    public /* synthetic */ void lambda$markDialogAsRead$207(long j, int i, boolean z, int i2, int i3) {
        getNotificationsController().processReadMessages(null, j, i, 0, z);
        TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
        if (tLRPC$Dialog != null) {
            int i4 = tLRPC$Dialog.unread_count;
            if (i2 == 0 || i3 <= tLRPC$Dialog.top_message) {
                tLRPC$Dialog.unread_count = 0;
            } else {
                int max = Math.max(i4 - i2, 0);
                tLRPC$Dialog.unread_count = max;
                if (i3 != Integer.MAX_VALUE) {
                    int i5 = tLRPC$Dialog.top_message;
                    if (max > i3 - i5) {
                        tLRPC$Dialog.unread_count = i3 - i5;
                    }
                }
            }
            boolean z2 = tLRPC$Dialog.unread_mark;
            if (z2) {
                tLRPC$Dialog.unread_mark = false;
                getMessagesStorage().setDialogUnread(tLRPC$Dialog.id, false);
            }
            if ((i4 != 0 || z2) && tLRPC$Dialog.unread_count == 0) {
                if (!isDialogMuted(j, 0)) {
                    this.unreadUnmutedDialogs--;
                }
                int i6 = 0;
                while (true) {
                    DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                    if (i6 < dialogFilterArr.length) {
                        if (dialogFilterArr[i6] != null && (dialogFilterArr[i6].flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                            sortDialogs(null);
                            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            break;
                        }
                        i6++;
                    } else {
                        break;
                    }
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
        }
        LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
        longSparseIntArray.put(j, 0);
        getNotificationsController().processDialogsUpdateRead(longSparseIntArray);
    }

    public /* synthetic */ void lambda$markDialogAsRead$209(int i, long j, boolean z, int i2, int i3) {
        ReadTask readTask;
        if (i != 0) {
            HashMap<String, ReadTask> hashMap = this.threadsReadTasksMap;
            readTask = hashMap.get(j + "_" + i);
        } else {
            readTask = this.readTasksMap.get(j);
        }
        if (readTask == null) {
            readTask = new ReadTask();
            readTask.dialogId = j;
            readTask.replyId = i;
            readTask.sendRequestTime = SystemClock.elapsedRealtime() + 5000;
            if (!z) {
                if (i != 0) {
                    HashMap<String, ReadTask> hashMap2 = this.threadsReadTasksMap;
                    hashMap2.put(j + "_" + i, readTask);
                    this.repliesReadTasks.add(readTask);
                } else {
                    this.readTasksMap.put(j, readTask);
                    this.readTasks.add(readTask);
                }
            }
        }
        readTask.maxDate = i2;
        readTask.maxId = i3;
        if (z) {
            completeReadTask(readTask);
        }
    }

    public int createChat(String str, ArrayList<Long> arrayList, String str2, int i, boolean z, Location location, String str3, final BaseFragment baseFragment) {
        if (i == 0 && !z) {
            final TLRPC$TL_messages_createChat tLRPC$TL_messages_createChat = new TLRPC$TL_messages_createChat();
            tLRPC$TL_messages_createChat.title = str;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC$User user = getUser(arrayList.get(i2));
                if (user != null) {
                    tLRPC$TL_messages_createChat.users.add(getInputUser(user));
                }
            }
            return getConnectionsManager().sendRequest(tLRPC$TL_messages_createChat, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda341
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$createChat$212(baseFragment, tLRPC$TL_messages_createChat, tLObject, tLRPC$TL_error);
                }
            }, 2);
        } else if (!z && i != 2 && i != 4) {
            return 0;
        } else {
            final TLRPC$TL_channels_createChannel tLRPC$TL_channels_createChannel = new TLRPC$TL_channels_createChannel();
            tLRPC$TL_channels_createChannel.title = str;
            if (str2 == null) {
                str2 = "";
            }
            tLRPC$TL_channels_createChannel.about = str2;
            tLRPC$TL_channels_createChannel.for_import = z;
            if (z || i == 4) {
                tLRPC$TL_channels_createChannel.megagroup = true;
            } else {
                tLRPC$TL_channels_createChannel.broadcast = true;
            }
            if (location != null) {
                TLRPC$TL_inputGeoPoint tLRPC$TL_inputGeoPoint = new TLRPC$TL_inputGeoPoint();
                tLRPC$TL_channels_createChannel.geo_point = tLRPC$TL_inputGeoPoint;
                tLRPC$TL_inputGeoPoint.lat = location.getLatitude();
                tLRPC$TL_channels_createChannel.geo_point._long = location.getLongitude();
                tLRPC$TL_channels_createChannel.address = str3;
                tLRPC$TL_channels_createChannel.flags |= 4;
            }
            return getConnectionsManager().sendRequest(tLRPC$TL_channels_createChannel, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda339
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$createChat$215(baseFragment, tLRPC$TL_channels_createChannel, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    public /* synthetic */ void lambda$createChat$212(final BaseFragment baseFragment, final TLRPC$TL_messages_createChat tLRPC$TL_messages_createChat, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda174
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$createChat$210(tLRPC$TL_error, baseFragment, tLRPC$TL_messages_createChat);
                }
            });
            return;
        }
        final TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
        processUpdates(tLRPC$Updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda188
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$createChat$211(tLRPC$Updates);
            }
        });
    }

    public /* synthetic */ void lambda$createChat$210(TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_messages_createChat tLRPC$TL_messages_createChat) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_messages_createChat, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public /* synthetic */ void lambda$createChat$211(TLRPC$Updates tLRPC$Updates) {
        putUsers(tLRPC$Updates.users, false);
        putChats(tLRPC$Updates.chats, false);
        ArrayList<TLRPC$Chat> arrayList = tLRPC$Updates.chats;
        if (arrayList != null && !arrayList.isEmpty()) {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidCreated, Long.valueOf(tLRPC$Updates.chats.get(0).id));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
        }
    }

    public /* synthetic */ void lambda$createChat$215(final BaseFragment baseFragment, final TLRPC$TL_channels_createChannel tLRPC$TL_channels_createChannel, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda170
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$createChat$213(tLRPC$TL_error, baseFragment, tLRPC$TL_channels_createChannel);
                }
            });
            return;
        }
        final TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
        processUpdates(tLRPC$Updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda189
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$createChat$214(tLRPC$Updates);
            }
        });
    }

    public /* synthetic */ void lambda$createChat$213(TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_channels_createChannel tLRPC$TL_channels_createChannel) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_channels_createChannel, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public /* synthetic */ void lambda$createChat$214(TLRPC$Updates tLRPC$Updates) {
        putUsers(tLRPC$Updates.users, false);
        putChats(tLRPC$Updates.chats, false);
        ArrayList<TLRPC$Chat> arrayList = tLRPC$Updates.chats;
        if (arrayList != null && !arrayList.isEmpty()) {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidCreated, Long.valueOf(tLRPC$Updates.chats.get(0).id));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
        }
    }

    public void convertToMegaGroup(Context context, long j, BaseFragment baseFragment, MessagesStorage.LongCallback longCallback) {
        convertToMegaGroup(context, j, baseFragment, longCallback, null);
    }

    public void convertToMegaGroup(final Context context, long j, final BaseFragment baseFragment, final MessagesStorage.LongCallback longCallback, final Runnable runnable) {
        final TLRPC$TL_messages_migrateChat tLRPC$TL_messages_migrateChat = new TLRPC$TL_messages_migrateChat();
        tLRPC$TL_messages_migrateChat.chat_id = j;
        final AlertDialog alertDialog = context != null ? new AlertDialog(context, 3) : null;
        final int sendRequest = getConnectionsManager().sendRequest(tLRPC$TL_messages_migrateChat, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda320
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$convertToMegaGroup$219(context, alertDialog, longCallback, runnable, baseFragment, tLRPC$TL_messages_migrateChat, tLObject, tLRPC$TL_error);
            }
        });
        if (alertDialog != null) {
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    MessagesController.this.lambda$convertToMegaGroup$220(sendRequest, dialogInterface);
                }
            });
            try {
                alertDialog.show();
            } catch (Exception unused) {
            }
        }
    }

    public /* synthetic */ void lambda$convertToMegaGroup$219(final Context context, final AlertDialog alertDialog, final MessagesStorage.LongCallback longCallback, Runnable runnable, final BaseFragment baseFragment, final TLRPC$TL_messages_migrateChat tLRPC$TL_messages_migrateChat, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            if (context != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.lambda$convertToMegaGroup$216(context, alertDialog);
                    }
                });
            }
            final TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            processUpdates(tLRPC$Updates, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda221
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.lambda$convertToMegaGroup$217(MessagesStorage.LongCallback.this, tLRPC$Updates);
                }
            });
            return;
        }
        if (runnable != null) {
            runnable.run();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda135
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$convertToMegaGroup$218(longCallback, context, alertDialog, tLRPC$TL_error, baseFragment, tLRPC$TL_messages_migrateChat);
            }
        });
    }

    public static /* synthetic */ void lambda$convertToMegaGroup$216(Context context, AlertDialog alertDialog) {
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static /* synthetic */ void lambda$convertToMegaGroup$217(MessagesStorage.LongCallback longCallback, TLRPC$Updates tLRPC$Updates) {
        if (longCallback != null) {
            for (int i = 0; i < tLRPC$Updates.chats.size(); i++) {
                TLRPC$Chat tLRPC$Chat = tLRPC$Updates.chats.get(i);
                if (ChatObject.isChannel(tLRPC$Chat)) {
                    longCallback.run(tLRPC$Chat.id);
                    return;
                }
            }
        }
    }

    public /* synthetic */ void lambda$convertToMegaGroup$218(MessagesStorage.LongCallback longCallback, Context context, AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_messages_migrateChat tLRPC$TL_messages_migrateChat) {
        if (longCallback != null) {
            longCallback.run(0L);
        }
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_messages_migrateChat, Boolean.FALSE);
    }

    public /* synthetic */ void lambda$convertToMegaGroup$220(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public void convertToGigaGroup(final Context context, TLRPC$Chat tLRPC$Chat, final BaseFragment baseFragment, final MessagesStorage.BooleanCallback booleanCallback) {
        final TLRPC$TL_channels_convertToGigagroup tLRPC$TL_channels_convertToGigagroup = new TLRPC$TL_channels_convertToGigagroup();
        tLRPC$TL_channels_convertToGigagroup.channel = getInputChannel(tLRPC$Chat);
        AlertDialog alertDialog = context != null ? new AlertDialog(context, 3) : null;
        final AlertDialog alertDialog2 = alertDialog;
        final int sendRequest = getConnectionsManager().sendRequest(tLRPC$TL_channels_convertToGigagroup, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda319
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$convertToGigaGroup$224(context, alertDialog2, booleanCallback, baseFragment, tLRPC$TL_channels_convertToGigagroup, tLObject, tLRPC$TL_error);
            }
        });
        if (alertDialog != null) {
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    MessagesController.this.lambda$convertToGigaGroup$225(sendRequest, dialogInterface);
                }
            });
            try {
                alertDialog.showDelayed(400L);
            } catch (Exception unused) {
            }
        }
    }

    public /* synthetic */ void lambda$convertToGigaGroup$224(final Context context, final AlertDialog alertDialog, final MessagesStorage.BooleanCallback booleanCallback, final BaseFragment baseFragment, final TLRPC$TL_channels_convertToGigagroup tLRPC$TL_channels_convertToGigagroup, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            if (context != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.lambda$convertToGigaGroup$221(context, alertDialog);
                    }
                });
            }
            processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda220
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.lambda$convertToGigaGroup$222(MessagesStorage.BooleanCallback.this);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda134
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$convertToGigaGroup$223(booleanCallback, context, alertDialog, tLRPC$TL_error, baseFragment, tLRPC$TL_channels_convertToGigagroup);
            }
        });
    }

    public static /* synthetic */ void lambda$convertToGigaGroup$221(Context context, AlertDialog alertDialog) {
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static /* synthetic */ void lambda$convertToGigaGroup$222(MessagesStorage.BooleanCallback booleanCallback) {
        if (booleanCallback != null) {
            booleanCallback.run(true);
        }
    }

    public /* synthetic */ void lambda$convertToGigaGroup$223(MessagesStorage.BooleanCallback booleanCallback, Context context, AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_channels_convertToGigagroup tLRPC$TL_channels_convertToGigagroup) {
        if (booleanCallback != null) {
            booleanCallback.run(false);
        }
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_channels_convertToGigagroup, Boolean.FALSE);
    }

    public /* synthetic */ void lambda$convertToGigaGroup$225(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public void addUsersToChannel(long j, ArrayList<TLRPC$InputUser> arrayList, final BaseFragment baseFragment) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        final TLRPC$TL_channels_inviteToChannel tLRPC$TL_channels_inviteToChannel = new TLRPC$TL_channels_inviteToChannel();
        tLRPC$TL_channels_inviteToChannel.channel = getInputChannel(j);
        tLRPC$TL_channels_inviteToChannel.users = arrayList;
        getConnectionsManager().sendRequest(tLRPC$TL_channels_inviteToChannel, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda340
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$addUsersToChannel$227(baseFragment, tLRPC$TL_channels_inviteToChannel, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$addUsersToChannel$226(TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_channels_inviteToChannel tLRPC$TL_channels_inviteToChannel) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_channels_inviteToChannel, Boolean.TRUE);
    }

    public /* synthetic */ void lambda$addUsersToChannel$227(final BaseFragment baseFragment, final TLRPC$TL_channels_inviteToChannel tLRPC$TL_channels_inviteToChannel, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda173
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$addUsersToChannel$226(tLRPC$TL_error, baseFragment, tLRPC$TL_channels_inviteToChannel);
                }
            });
        } else {
            processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public void setDefaultSendAs(final long j, long j2) {
        TLRPC$ChatFull chatFull = getChatFull(-j);
        if (chatFull != null) {
            chatFull.default_send_as = getPeer(j2);
            getMessagesStorage().updateChatInfo(chatFull, false);
            getNotificationCenter().postNotificationName(NotificationCenter.updateDefaultSendAsPeer, Long.valueOf(j), chatFull.default_send_as);
        }
        TLRPC$TL_messages_saveDefaultSendAs tLRPC$TL_messages_saveDefaultSendAs = new TLRPC$TL_messages_saveDefaultSendAs();
        tLRPC$TL_messages_saveDefaultSendAs.peer = getInputPeer(j);
        tLRPC$TL_messages_saveDefaultSendAs.send_as = getInputPeer(j2);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_saveDefaultSendAs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda286
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$setDefaultSendAs$228(j, tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$setDefaultSendAs$228(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            long j2 = -j;
            if (getChatFull(j2) != null) {
                return;
            }
            loadFullChat(j2, 0, true);
        } else if (tLRPC$TL_error == null || tLRPC$TL_error.code != 400) {
        } else {
            loadFullChat(-j, 0, true);
        }
    }

    public void toggleChatNoForwards(long j, boolean z) {
        TLRPC$TL_messages_toggleNoForwards tLRPC$TL_messages_toggleNoForwards = new TLRPC$TL_messages_toggleNoForwards();
        tLRPC$TL_messages_toggleNoForwards.peer = getInputPeer(-j);
        tLRPC$TL_messages_toggleNoForwards.enabled = z;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_toggleNoForwards, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda246
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$toggleChatNoForwards$230(tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$toggleChatNoForwards$230(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$toggleChatNoForwards$229();
                }
            });
        }
    }

    public /* synthetic */ void lambda$toggleChatNoForwards$229() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void toggleChatJoinToSend(long j, boolean z, final Runnable runnable, final Runnable runnable2) {
        TLRPC$TL_channels_toggleJoinToSend tLRPC$TL_channels_toggleJoinToSend = new TLRPC$TL_channels_toggleJoinToSend();
        tLRPC$TL_channels_toggleJoinToSend.channel = getInputChannel(j);
        tLRPC$TL_channels_toggleJoinToSend.enabled = z;
        getConnectionsManager().sendRequest(tLRPC$TL_channels_toggleJoinToSend, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda325
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$toggleChatJoinToSend$232(runnable, runnable2, tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$toggleChatJoinToSend$232(Runnable runnable, Runnable runnable2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$toggleChatJoinToSend$231();
                }
            });
            if (runnable == null) {
                return;
            }
            runnable.run();
        } else if (tLRPC$TL_error == null || "CHAT_NOT_MODIFIED".equals(tLRPC$TL_error.text)) {
            if (runnable == null) {
                return;
            }
            runnable.run();
        } else if (runnable2 == null) {
        } else {
            runnable2.run();
        }
    }

    public /* synthetic */ void lambda$toggleChatJoinToSend$231() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void toggleChatJoinRequest(long j, boolean z, final Runnable runnable, final Runnable runnable2) {
        TLRPC$TL_channels_toggleJoinRequest tLRPC$TL_channels_toggleJoinRequest = new TLRPC$TL_channels_toggleJoinRequest();
        tLRPC$TL_channels_toggleJoinRequest.channel = getInputChannel(j);
        tLRPC$TL_channels_toggleJoinRequest.enabled = z;
        getConnectionsManager().sendRequest(tLRPC$TL_channels_toggleJoinRequest, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda324
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$toggleChatJoinRequest$234(runnable, runnable2, tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$toggleChatJoinRequest$234(Runnable runnable, Runnable runnable2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$toggleChatJoinRequest$233();
                }
            });
            if (runnable == null) {
                return;
            }
            runnable.run();
        } else if (tLRPC$TL_error == null || "CHAT_NOT_MODIFIED".equals(tLRPC$TL_error.text)) {
            if (runnable == null) {
                return;
            }
            runnable.run();
        } else if (runnable2 == null) {
        } else {
            runnable2.run();
        }
    }

    public /* synthetic */ void lambda$toggleChatJoinRequest$233() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void toggleChannelSignatures(long j, boolean z) {
        TLRPC$TL_channels_toggleSignatures tLRPC$TL_channels_toggleSignatures = new TLRPC$TL_channels_toggleSignatures();
        tLRPC$TL_channels_toggleSignatures.channel = getInputChannel(j);
        tLRPC$TL_channels_toggleSignatures.enabled = z;
        getConnectionsManager().sendRequest(tLRPC$TL_channels_toggleSignatures, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda245
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$toggleChannelSignatures$236(tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$toggleChannelSignatures$236(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$toggleChannelSignatures$235();
                }
            });
        }
    }

    public /* synthetic */ void lambda$toggleChannelSignatures$235() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void toggleChannelForum(long j, boolean z) {
        TLRPC$TL_channels_toggleForum tLRPC$TL_channels_toggleForum = new TLRPC$TL_channels_toggleForum();
        tLRPC$TL_channels_toggleForum.channel = getInputChannel(j);
        tLRPC$TL_channels_toggleForum.enabled = z;
        getConnectionsManager().sendRequest(tLRPC$TL_channels_toggleForum, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda253
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$toggleChannelForum$238(tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$toggleChannelForum$238(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda37
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$toggleChannelForum$237();
                }
            });
        }
    }

    public /* synthetic */ void lambda$toggleChannelForum$237() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void toggleChannelInvitesHistory(long j, boolean z) {
        TLRPC$TL_channels_togglePreHistoryHidden tLRPC$TL_channels_togglePreHistoryHidden = new TLRPC$TL_channels_togglePreHistoryHidden();
        tLRPC$TL_channels_togglePreHistoryHidden.channel = getInputChannel(j);
        tLRPC$TL_channels_togglePreHistoryHidden.enabled = z;
        getConnectionsManager().sendRequest(tLRPC$TL_channels_togglePreHistoryHidden, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda263
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$toggleChannelInvitesHistory$240(tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$toggleChannelInvitesHistory$240(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda36
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$toggleChannelInvitesHistory$239();
                }
            });
        }
    }

    public /* synthetic */ void lambda$toggleChannelInvitesHistory$239() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void updateChatAbout(long j, final String str, final TLRPC$ChatFull tLRPC$ChatFull) {
        TLRPC$TL_messages_editChatAbout tLRPC$TL_messages_editChatAbout = new TLRPC$TL_messages_editChatAbout();
        tLRPC$TL_messages_editChatAbout.peer = getInputPeer(-j);
        tLRPC$TL_messages_editChatAbout.about = str;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_editChatAbout, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda332
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$updateChatAbout$242(tLRPC$ChatFull, str, tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$updateChatAbout$242(final TLRPC$ChatFull tLRPC$ChatFull, final String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (!(tLObject instanceof TLRPC$TL_boolTrue) || tLRPC$ChatFull == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda156
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$updateChatAbout$241(tLRPC$ChatFull, str);
            }
        });
    }

    public /* synthetic */ void lambda$updateChatAbout$241(TLRPC$ChatFull tLRPC$ChatFull, String str) {
        tLRPC$ChatFull.about = str;
        getMessagesStorage().updateChatInfo(tLRPC$ChatFull, false);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.postNotificationName(i, tLRPC$ChatFull, 0, bool, bool);
    }

    public void updateChannelUserName(final BaseFragment baseFragment, final long j, final String str, final Runnable runnable, final Runnable runnable2) {
        final TLRPC$TL_channels_updateUsername tLRPC$TL_channels_updateUsername = new TLRPC$TL_channels_updateUsername();
        tLRPC$TL_channels_updateUsername.channel = getInputChannel(j);
        tLRPC$TL_channels_updateUsername.username = str;
        getConnectionsManager().sendRequest(tLRPC$TL_channels_updateUsername, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda310
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$updateChannelUserName$244(j, str, runnable, baseFragment, tLRPC$TL_channels_updateUsername, runnable2, tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$updateChannelUserName$244(final long j, final String str, final Runnable runnable, BaseFragment baseFragment, TLRPC$TL_channels_updateUsername tLRPC$TL_channels_updateUsername, Runnable runnable2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if ((tLObject instanceof TLRPC$TL_boolTrue) || (tLRPC$TL_error != null && "USERNAME_NOT_MODIFIED".equals(tLRPC$TL_error.text))) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda83
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$updateChannelUserName$243(j, str, runnable);
                }
            });
            return;
        }
        AlertsCreator.processError(UserConfig.selectedAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_channels_updateUsername, new Object[0]);
        if (runnable2 == null) {
            return;
        }
        runnable2.run();
    }

    public /* synthetic */ void lambda$updateChannelUserName$243(long j, String str, Runnable runnable) {
        boolean z;
        TLRPC$Chat chat = getChat(Long.valueOf(j));
        if (chat.usernames != null) {
            for (int i = 0; i < chat.usernames.size(); i++) {
                TLRPC$TL_username tLRPC$TL_username = chat.usernames.get(i);
                if (tLRPC$TL_username != null && tLRPC$TL_username.editable) {
                    tLRPC$TL_username.username = str;
                    z = true;
                    break;
                }
            }
        }
        z = false;
        if (!z) {
            ArrayList<TLRPC$TL_username> arrayList = chat.usernames;
            if (arrayList == null || arrayList.size() >= 1) {
                if (chat.usernames == null) {
                    chat.usernames = new ArrayList<>();
                }
                TLRPC$TL_username tLRPC$TL_username2 = new TLRPC$TL_username();
                tLRPC$TL_username2.username = str;
                tLRPC$TL_username2.active = true;
                tLRPC$TL_username2.editable = true;
                chat.usernames.add(0, tLRPC$TL_username2);
            } else {
                chat.username = str;
            }
        }
        ArrayList<TLRPC$Chat> arrayList2 = new ArrayList<>();
        arrayList2.add(chat);
        getMessagesStorage().putUsersAndChats(null, arrayList2, true, true);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
        if (runnable != null) {
            runnable.run();
        }
    }

    public void sendBotStart(TLRPC$User tLRPC$User, String str) {
        if (tLRPC$User == null) {
            return;
        }
        TLRPC$TL_messages_startBot tLRPC$TL_messages_startBot = new TLRPC$TL_messages_startBot();
        tLRPC$TL_messages_startBot.bot = getInputUser(tLRPC$User);
        tLRPC$TL_messages_startBot.peer = getInputPeer(tLRPC$User.id);
        tLRPC$TL_messages_startBot.start_param = str;
        tLRPC$TL_messages_startBot.random_id = Utilities.random.nextLong();
        getConnectionsManager().sendRequest(tLRPC$TL_messages_startBot, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda241
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$sendBotStart$245(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$sendBotStart$245(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            return;
        }
        processUpdates((TLRPC$Updates) tLObject, false);
    }

    public boolean isJoiningChannel(long j) {
        return this.joiningToChannels.contains(Long.valueOf(j));
    }

    public void addUserToChat(long j, TLRPC$User tLRPC$User, int i, String str, BaseFragment baseFragment, Runnable runnable) {
        addUserToChat(j, tLRPC$User, i, str, baseFragment, false, runnable, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void addUserToChat(final long j, TLRPC$User tLRPC$User, int i, String str, final BaseFragment baseFragment, final boolean z, final Runnable runnable, final ErrorDelegate errorDelegate) {
        TLRPC$TL_messages_addChatUser tLRPC$TL_messages_addChatUser;
        if (tLRPC$User == null) {
            if (errorDelegate == null) {
                return;
            }
            errorDelegate.run(null);
            return;
        }
        final boolean isChannel = ChatObject.isChannel(j, this.currentAccount);
        final boolean z2 = isChannel && getChat(Long.valueOf(j)).megagroup;
        final TLRPC$InputUser inputUser = getInputUser(tLRPC$User);
        if (str != null && (!isChannel || z2)) {
            TLRPC$TL_messages_startBot tLRPC$TL_messages_startBot = new TLRPC$TL_messages_startBot();
            tLRPC$TL_messages_startBot.bot = inputUser;
            if (isChannel) {
                tLRPC$TL_messages_startBot.peer = getInputPeer(-j);
            } else {
                TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
                tLRPC$TL_messages_startBot.peer = tLRPC$TL_inputPeerChat;
                tLRPC$TL_inputPeerChat.chat_id = j;
            }
            tLRPC$TL_messages_startBot.start_param = str;
            tLRPC$TL_messages_startBot.random_id = Utilities.random.nextLong();
            tLRPC$TL_messages_addChatUser = tLRPC$TL_messages_startBot;
        } else if (isChannel) {
            if (inputUser instanceof TLRPC$TL_inputUserSelf) {
                if (this.joiningToChannels.contains(Long.valueOf(j))) {
                    if (errorDelegate == null) {
                        return;
                    }
                    errorDelegate.run(null);
                    return;
                }
                TLRPC$TL_channels_joinChannel tLRPC$TL_channels_joinChannel = new TLRPC$TL_channels_joinChannel();
                tLRPC$TL_channels_joinChannel.channel = getInputChannel(j);
                this.joiningToChannels.add(Long.valueOf(j));
                tLRPC$TL_messages_addChatUser = tLRPC$TL_channels_joinChannel;
            } else {
                TLRPC$TL_channels_inviteToChannel tLRPC$TL_channels_inviteToChannel = new TLRPC$TL_channels_inviteToChannel();
                tLRPC$TL_channels_inviteToChannel.channel = getInputChannel(j);
                tLRPC$TL_channels_inviteToChannel.users.add(inputUser);
                tLRPC$TL_messages_addChatUser = tLRPC$TL_channels_inviteToChannel;
            }
        } else {
            TLRPC$TL_messages_addChatUser tLRPC$TL_messages_addChatUser2 = new TLRPC$TL_messages_addChatUser();
            tLRPC$TL_messages_addChatUser2.chat_id = j;
            tLRPC$TL_messages_addChatUser2.fwd_limit = i;
            tLRPC$TL_messages_addChatUser2.user_id = inputUser;
            tLRPC$TL_messages_addChatUser = tLRPC$TL_messages_addChatUser2;
        }
        final TLRPC$TL_messages_addChatUser tLRPC$TL_messages_addChatUser3 = tLRPC$TL_messages_addChatUser;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_addChatUser3, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda346
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$addUserToChat$250(isChannel, inputUser, j, z, runnable, errorDelegate, baseFragment, tLRPC$TL_messages_addChatUser3, z2, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$addUserToChat$250(final boolean z, final TLRPC$InputUser tLRPC$InputUser, final long j, boolean z2, Runnable runnable, final ErrorDelegate errorDelegate, final BaseFragment baseFragment, final TLObject tLObject, final boolean z3, TLObject tLObject2, final TLRPC$TL_error tLRPC$TL_error) {
        boolean z4;
        if (z && (tLRPC$InputUser instanceof TLRPC$TL_inputUserSelf)) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda64
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$addUserToChat$246(j);
                }
            });
        }
        if (tLRPC$TL_error != null) {
            if (!"USER_ALREADY_PARTICIPANT".equals(tLRPC$TL_error.text) || !z2) {
                if (errorDelegate != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda132
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagesController.this.lambda$addUserToChat$247(errorDelegate, tLRPC$TL_error, baseFragment, tLObject, z, z3);
                        }
                    });
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda133
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$addUserToChat$248(errorDelegate, tLRPC$TL_error, baseFragment, tLObject, z, z3, tLRPC$InputUser);
                    }
                });
                return;
            } else if (runnable == null) {
                return;
            } else {
                AndroidUtilities.runOnUIThread(runnable);
                return;
            }
        }
        TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject2;
        int i = 0;
        while (true) {
            if (i >= tLRPC$Updates.updates.size()) {
                z4 = false;
                break;
            }
            TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
            if ((tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) && (((TLRPC$TL_updateNewChannelMessage) tLRPC$Update).message.action instanceof TLRPC$TL_messageActionChatAddUser)) {
                z4 = true;
                break;
            }
            i++;
        }
        processUpdates(tLRPC$Updates, false);
        if (z) {
            if (!z4 && (tLRPC$InputUser instanceof TLRPC$TL_inputUserSelf)) {
                generateJoinMessage(j, true);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda61
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$addUserToChat$249(j);
                }
            }, 1000L);
        }
        if (z && (tLRPC$InputUser instanceof TLRPC$TL_inputUserSelf)) {
            getMessagesStorage().updateDialogsWithDeletedMessages(-j, j, new ArrayList<>(), null, true);
        }
        if (runnable == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public /* synthetic */ void lambda$addUserToChat$246(long j) {
        this.joiningToChannels.remove(Long.valueOf(j));
    }

    public /* synthetic */ void lambda$addUserToChat$247(ErrorDelegate errorDelegate, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLObject tLObject, boolean z, boolean z2) {
        if (errorDelegate.run(tLRPC$TL_error)) {
            int i = this.currentAccount;
            boolean z3 = true;
            Object[] objArr = new Object[1];
            if (!z || z2) {
                z3 = false;
            }
            objArr[0] = Boolean.valueOf(z3);
            AlertsCreator.processError(i, tLRPC$TL_error, baseFragment, tLObject, objArr);
        }
    }

    public /* synthetic */ void lambda$addUserToChat$248(ErrorDelegate errorDelegate, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLObject tLObject, boolean z, boolean z2, TLRPC$InputUser tLRPC$InputUser) {
        if (errorDelegate == null) {
            int i = this.currentAccount;
            Object[] objArr = new Object[1];
            objArr[0] = Boolean.valueOf(z && !z2);
            AlertsCreator.processError(i, tLRPC$TL_error, baseFragment, tLObject, objArr);
        }
        if (!z || !(tLRPC$InputUser instanceof TLRPC$TL_inputUserSelf)) {
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public /* synthetic */ void lambda$addUserToChat$249(long j) {
        loadFullChat(j, 0, true);
    }

    public void deleteParticipantFromChat(long j, TLRPC$User tLRPC$User) {
        deleteParticipantFromChat(j, tLRPC$User, null, false, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void deleteParticipantFromChat(final long j, final TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean z, boolean z2) {
        TLRPC$InputPeer inputPeer;
        TLRPC$TL_messages_deleteChatUser tLRPC$TL_messages_deleteChatUser;
        if (tLRPC$User == null && tLRPC$Chat == null) {
            return;
        }
        if (tLRPC$User != null) {
            inputPeer = getInputPeer(tLRPC$User);
        } else {
            inputPeer = getInputPeer(tLRPC$Chat);
        }
        TLRPC$Chat chat = getChat(Long.valueOf(j));
        final boolean isChannel = ChatObject.isChannel(chat);
        if (isChannel) {
            if (UserObject.isUserSelf(tLRPC$User)) {
                if (chat.creator && z) {
                    TLRPC$TL_channels_deleteChannel tLRPC$TL_channels_deleteChannel = new TLRPC$TL_channels_deleteChannel();
                    tLRPC$TL_channels_deleteChannel.channel = getInputChannel(chat);
                    tLRPC$TL_messages_deleteChatUser = tLRPC$TL_channels_deleteChannel;
                } else {
                    TLRPC$TL_channels_leaveChannel tLRPC$TL_channels_leaveChannel = new TLRPC$TL_channels_leaveChannel();
                    tLRPC$TL_channels_leaveChannel.channel = getInputChannel(chat);
                    tLRPC$TL_messages_deleteChatUser = tLRPC$TL_channels_leaveChannel;
                }
            } else {
                TLRPC$TL_channels_editBanned tLRPC$TL_channels_editBanned = new TLRPC$TL_channels_editBanned();
                tLRPC$TL_channels_editBanned.channel = getInputChannel(chat);
                tLRPC$TL_channels_editBanned.participant = inputPeer;
                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = new TLRPC$TL_chatBannedRights();
                tLRPC$TL_channels_editBanned.banned_rights = tLRPC$TL_chatBannedRights;
                tLRPC$TL_chatBannedRights.view_messages = true;
                tLRPC$TL_chatBannedRights.send_media = true;
                tLRPC$TL_chatBannedRights.send_messages = true;
                tLRPC$TL_chatBannedRights.send_stickers = true;
                tLRPC$TL_chatBannedRights.send_gifs = true;
                tLRPC$TL_chatBannedRights.send_games = true;
                tLRPC$TL_chatBannedRights.send_inline = true;
                tLRPC$TL_chatBannedRights.embed_links = true;
                tLRPC$TL_chatBannedRights.pin_messages = true;
                tLRPC$TL_chatBannedRights.send_polls = true;
                tLRPC$TL_chatBannedRights.invite_users = true;
                tLRPC$TL_chatBannedRights.change_info = true;
                tLRPC$TL_messages_deleteChatUser = tLRPC$TL_channels_editBanned;
            }
        } else if (z) {
            TLRPC$TL_messages_deleteChat tLRPC$TL_messages_deleteChat = new TLRPC$TL_messages_deleteChat();
            tLRPC$TL_messages_deleteChat.chat_id = j;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteChat, MessagesController$$ExternalSyntheticLambda359.INSTANCE);
            return;
        } else {
            TLRPC$TL_messages_deleteChatUser tLRPC$TL_messages_deleteChatUser2 = new TLRPC$TL_messages_deleteChatUser();
            tLRPC$TL_messages_deleteChatUser2.chat_id = j;
            tLRPC$TL_messages_deleteChatUser2.user_id = getInputUser(tLRPC$User);
            tLRPC$TL_messages_deleteChatUser2.revoke_history = true;
            tLRPC$TL_messages_deleteChatUser = tLRPC$TL_messages_deleteChatUser2;
        }
        if (UserObject.isUserSelf(tLRPC$User)) {
            deleteDialog(-j, 0, z2);
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteChatUser, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda348
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$deleteParticipantFromChat$253(isChannel, tLRPC$User, j, tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$deleteParticipantFromChat$253(boolean z, TLRPC$User tLRPC$User, final long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            return;
        }
        processUpdates((TLRPC$Updates) tLObject, false);
        if (!z || UserObject.isUserSelf(tLRPC$User)) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda60
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$deleteParticipantFromChat$252(j);
            }
        }, 1000L);
    }

    public /* synthetic */ void lambda$deleteParticipantFromChat$252(long j) {
        loadFullChat(j, 0, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void changeChatTitle(long j, String str) {
        TLRPC$TL_messages_editChatTitle tLRPC$TL_messages_editChatTitle;
        if (ChatObject.isChannel(j, this.currentAccount)) {
            TLRPC$TL_channels_editTitle tLRPC$TL_channels_editTitle = new TLRPC$TL_channels_editTitle();
            tLRPC$TL_channels_editTitle.channel = getInputChannel(j);
            tLRPC$TL_channels_editTitle.title = str;
            tLRPC$TL_messages_editChatTitle = tLRPC$TL_channels_editTitle;
        } else {
            TLRPC$TL_messages_editChatTitle tLRPC$TL_messages_editChatTitle2 = new TLRPC$TL_messages_editChatTitle();
            tLRPC$TL_messages_editChatTitle2.chat_id = j;
            tLRPC$TL_messages_editChatTitle2.title = str;
            tLRPC$TL_messages_editChatTitle = tLRPC$TL_messages_editChatTitle2;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_editChatTitle, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda240
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$changeChatTitle$254(tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$changeChatTitle$254(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            return;
        }
        processUpdates((TLRPC$Updates) tLObject, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void changeChatAvatar(long j, final TLRPC$TL_inputChatPhoto tLRPC$TL_inputChatPhoto, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, final String str, final TLRPC$FileLocation tLRPC$FileLocation, final TLRPC$FileLocation tLRPC$FileLocation2, final Runnable runnable) {
        MessagesController messagesController;
        TLRPC$InputChatPhoto tLRPC$InputChatPhoto;
        TLRPC$TL_messages_editChatPhoto tLRPC$TL_messages_editChatPhoto;
        if (tLRPC$TL_inputChatPhoto != null) {
            messagesController = this;
            tLRPC$InputChatPhoto = tLRPC$TL_inputChatPhoto;
        } else if (tLRPC$InputFile != null || tLRPC$InputFile2 != null) {
            TLRPC$TL_inputChatUploadedPhoto tLRPC$TL_inputChatUploadedPhoto = new TLRPC$TL_inputChatUploadedPhoto();
            if (tLRPC$InputFile != null) {
                tLRPC$TL_inputChatUploadedPhoto.file = tLRPC$InputFile;
                tLRPC$TL_inputChatUploadedPhoto.flags |= 1;
            }
            if (tLRPC$InputFile2 != null) {
                tLRPC$TL_inputChatUploadedPhoto.video = tLRPC$InputFile2;
                int i = tLRPC$TL_inputChatUploadedPhoto.flags | 2;
                tLRPC$TL_inputChatUploadedPhoto.flags = i;
                tLRPC$TL_inputChatUploadedPhoto.video_start_ts = d;
                tLRPC$TL_inputChatUploadedPhoto.flags = i | 4;
            }
            messagesController = this;
            tLRPC$InputChatPhoto = tLRPC$TL_inputChatUploadedPhoto;
        } else {
            tLRPC$InputChatPhoto = new TLRPC$InputChatPhoto() { // from class: org.telegram.tgnet.TLRPC$TL_inputChatPhotoEmpty
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                    abstractSerializedData.writeInt32(constructor);
                }
            };
            messagesController = this;
        }
        if (ChatObject.isChannel(j, messagesController.currentAccount)) {
            TLRPC$TL_channels_editPhoto tLRPC$TL_channels_editPhoto = new TLRPC$TL_channels_editPhoto();
            tLRPC$TL_channels_editPhoto.channel = getInputChannel(j);
            tLRPC$TL_channels_editPhoto.photo = tLRPC$InputChatPhoto;
            tLRPC$TL_messages_editChatPhoto = tLRPC$TL_channels_editPhoto;
        } else {
            TLRPC$TL_messages_editChatPhoto tLRPC$TL_messages_editChatPhoto2 = new TLRPC$TL_messages_editChatPhoto();
            tLRPC$TL_messages_editChatPhoto2.chat_id = j;
            tLRPC$TL_messages_editChatPhoto2.photo = tLRPC$InputChatPhoto;
            tLRPC$TL_messages_editChatPhoto = tLRPC$TL_messages_editChatPhoto2;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_editChatPhoto, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda335
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$changeChatAvatar$256(tLRPC$TL_inputChatPhoto, tLRPC$FileLocation, tLRPC$FileLocation2, str, runnable, tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$changeChatAvatar$256(TLRPC$TL_inputChatPhoto tLRPC$TL_inputChatPhoto, TLRPC$FileLocation tLRPC$FileLocation, TLRPC$FileLocation tLRPC$FileLocation2, String str, final Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$VideoSize tLRPC$VideoSize;
        TLRPC$Photo tLRPC$Photo;
        if (tLRPC$TL_error != null) {
            return;
        }
        TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
        if (tLRPC$TL_inputChatPhoto == null) {
            int size = tLRPC$Updates.updates.size();
            int i = 0;
            while (true) {
                tLRPC$VideoSize = null;
                if (i >= size) {
                    tLRPC$Photo = null;
                    break;
                }
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) {
                    TLRPC$MessageAction tLRPC$MessageAction = ((TLRPC$TL_updateNewChannelMessage) tLRPC$Update).message.action;
                    if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatEditPhoto) {
                        tLRPC$Photo = tLRPC$MessageAction.photo;
                        if (tLRPC$Photo instanceof TLRPC$TL_photo) {
                            break;
                        }
                    } else {
                        continue;
                    }
                    i++;
                } else {
                    if (tLRPC$Update instanceof TLRPC$TL_updateNewMessage) {
                        TLRPC$MessageAction tLRPC$MessageAction2 = ((TLRPC$TL_updateNewMessage) tLRPC$Update).message.action;
                        if (tLRPC$MessageAction2 instanceof TLRPC$TL_messageActionChatEditPhoto) {
                            tLRPC$Photo = tLRPC$MessageAction2.photo;
                            if (tLRPC$Photo instanceof TLRPC$TL_photo) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                    i++;
                }
            }
            if (tLRPC$Photo != null) {
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, 150);
                if (!tLRPC$Photo.video_sizes.isEmpty()) {
                    tLRPC$VideoSize = tLRPC$Photo.video_sizes.get(0);
                }
                if (closestPhotoSizeWithSize != null && tLRPC$FileLocation != null) {
                    getFileLoader().getPathToAttach(tLRPC$FileLocation, true).renameTo(getFileLoader().getPathToAttach(closestPhotoSizeWithSize, true));
                    ImageLoader.getInstance().replaceImageInCache(tLRPC$FileLocation.volume_id + "_" + tLRPC$FileLocation.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForPhoto(closestPhotoSizeWithSize, tLRPC$Photo), true);
                }
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, 800);
                if (closestPhotoSizeWithSize2 != null && tLRPC$FileLocation2 != null) {
                    getFileLoader().getPathToAttach(tLRPC$FileLocation2, true).renameTo(getFileLoader().getPathToAttach(closestPhotoSizeWithSize2, true));
                }
                if (tLRPC$VideoSize != null && str != null) {
                    new File(str).renameTo(getFileLoader().getPathToAttach(tLRPC$VideoSize, "mp4", true));
                }
            }
        }
        processUpdates(tLRPC$Updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda105
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$changeChatAvatar$255(runnable);
            }
        });
    }

    public /* synthetic */ void lambda$changeChatAvatar$255(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
    }

    public void unregistedPush() {
        if (!getUserConfig().registeredForPush || SharedConfig.pushString.length() != 0) {
            return;
        }
        TLRPC$TL_account_unregisterDevice tLRPC$TL_account_unregisterDevice = new TLRPC$TL_account_unregisterDevice();
        tLRPC$TL_account_unregisterDevice.token = SharedConfig.pushString;
        tLRPC$TL_account_unregisterDevice.token_type = SharedConfig.pushType;
        for (int i = 0; i < 4; i++) {
            UserConfig userConfig = UserConfig.getInstance(i);
            if (i != this.currentAccount && userConfig.isClientActivated()) {
                tLRPC$TL_account_unregisterDevice.other_uids.add(Long.valueOf(userConfig.getClientUserId()));
            }
        }
        getConnectionsManager().sendRequest(tLRPC$TL_account_unregisterDevice, MessagesController$$ExternalSyntheticLambda353.INSTANCE);
    }

    public void performLogout(int i) {
        boolean z = true;
        if (i == 1) {
            unregistedPush();
            getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_auth_logOut
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i2, boolean z2) {
                    return TLRPC$TL_auth_loggedOut.TLdeserialize(abstractSerializedData, i2, z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                    abstractSerializedData.writeInt32(constructor);
                }
            }, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda260
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$performLogout$259(tLObject, tLRPC$TL_error);
                }
            });
        } else {
            getConnectionsManager().cleanup(i == 2);
        }
        getUserConfig().clearConfig();
        SharedPrefsHelper.cleanupAccount(this.currentAccount);
        ArrayList<NotificationCenter.NotificationCenterDelegate> observers = getNotificationCenter().getObservers(NotificationCenter.appDidLogout);
        if (observers != null) {
            int size = observers.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    break;
                } else if (observers.get(i2) instanceof LaunchActivity) {
                    z = false;
                    break;
                } else {
                    i2++;
                }
            }
        }
        if (z && UserConfig.selectedAccount == this.currentAccount) {
            int i3 = 0;
            while (true) {
                if (i3 >= 4) {
                    i3 = -1;
                    break;
                } else if (UserConfig.getInstance(i3).isClientActivated()) {
                    break;
                } else {
                    i3++;
                }
            }
            if (i3 != -1) {
                UserConfig.selectedAccount = i3;
                UserConfig.getInstance(0).saveConfig(false);
                LaunchActivity.clearFragments();
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
        getMessagesStorage().cleanup(false);
        cleanup();
        getContactsController().deleteUnknownAppAccounts();
    }

    public /* synthetic */ void lambda$performLogout$259(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        getConnectionsManager().cleanup(false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda222
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.lambda$performLogout$258(TLObject.this);
            }
        });
    }

    public static /* synthetic */ void lambda$performLogout$258(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_auth_loggedOut) {
            TLRPC$TL_auth_loggedOut tLRPC$TL_auth_loggedOut = (TLRPC$TL_auth_loggedOut) tLObject;
            if (tLRPC$TL_auth_loggedOut.future_auth_token == null) {
                return;
            }
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("saved_tokens", 0);
            int i = sharedPreferences.getInt("count", 0);
            SerializedData serializedData = new SerializedData(tLObject.getObjectSize());
            tLRPC$TL_auth_loggedOut.serializeToStream(serializedData);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("log_out_token_" + i, Utilities.bytesToHex(serializedData.toByteArray())).putInt("count", i + 1).apply();
        }
    }

    public static ArrayList<TLRPC$TL_auth_loggedOut> getSavedLogOutTokens() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("saved_tokens", 0);
        int i = sharedPreferences.getInt("count", 0);
        if (i == 0) {
            return null;
        }
        ArrayList<TLRPC$TL_auth_loggedOut> arrayList = new ArrayList<>();
        for (int i2 = 0; i2 < i; i2++) {
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(sharedPreferences.getString("log_out_token_" + i2, "")));
            TLRPC$TL_auth_loggedOut TLdeserialize = TLRPC$TL_auth_loggedOut.TLdeserialize(serializedData, serializedData.readInt32(true), true);
            if (TLdeserialize != null) {
                arrayList.add(TLdeserialize);
            }
        }
        return arrayList;
    }

    public static void saveLogOutTokens(ArrayList<TLRPC$TL_auth_loggedOut> arrayList) {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("saved_tokens", 0);
        ArrayList arrayList2 = new ArrayList();
        sharedPreferences.edit().clear().apply();
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        for (int i = 0; i < Math.min(20, arrayList.size()); i++) {
            arrayList2.add(arrayList.get(i));
        }
        if (arrayList2.size() > 0) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt("count", arrayList2.size());
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                SerializedData serializedData = new SerializedData(((TLRPC$TL_auth_loggedOut) arrayList2.get(i2)).getObjectSize());
                ((TLRPC$TL_auth_loggedOut) arrayList2.get(i2)).serializeToStream(serializedData);
                edit.putString("log_out_token_" + i2, Utilities.bytesToHex(serializedData.toByteArray()));
            }
            edit.apply();
        }
    }

    public void generateUpdateMessage() {
        String str;
        if (this.gettingAppChangelog || BuildVars.DEBUG_VERSION || (str = SharedConfig.lastUpdateVersion) == null || str.equals(BuildVars.BUILD_VERSION_STRING)) {
            return;
        }
        this.gettingAppChangelog = true;
        TLRPC$TL_help_getAppChangelog tLRPC$TL_help_getAppChangelog = new TLRPC$TL_help_getAppChangelog();
        tLRPC$TL_help_getAppChangelog.prev_app_version = SharedConfig.lastUpdateVersion;
        getConnectionsManager().sendRequest(tLRPC$TL_help_getAppChangelog, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda257
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$generateUpdateMessage$260(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$generateUpdateMessage$260(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            SharedConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
            SharedConfig.saveConfig();
        }
        if (tLObject instanceof TLRPC$Updates) {
            processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public void registerForPush(final int i, final String str) {
        if (TextUtils.isEmpty(str) || this.registeringForPush || getUserConfig().getClientUserId() == 0) {
            return;
        }
        if (getUserConfig().registeredForPush && str.equals(SharedConfig.pushString)) {
            return;
        }
        this.registeringForPush = true;
        this.lastPushRegisterSendTime = SystemClock.elapsedRealtime();
        if (SharedConfig.pushAuthKey == null) {
            SharedConfig.pushAuthKey = new byte[256];
            Utilities.random.nextBytes(SharedConfig.pushAuthKey);
            SharedConfig.saveConfig();
        }
        TLRPC$TL_account_registerDevice tLRPC$TL_account_registerDevice = new TLRPC$TL_account_registerDevice();
        tLRPC$TL_account_registerDevice.token_type = i;
        tLRPC$TL_account_registerDevice.token = str;
        tLRPC$TL_account_registerDevice.no_muted = false;
        tLRPC$TL_account_registerDevice.secret = SharedConfig.pushAuthKey;
        for (int i2 = 0; i2 < 4; i2++) {
            UserConfig userConfig = UserConfig.getInstance(i2);
            if (i2 != this.currentAccount && userConfig.isClientActivated()) {
                long clientUserId = userConfig.getClientUserId();
                tLRPC$TL_account_registerDevice.other_uids.add(Long.valueOf(clientUserId));
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("add other uid = " + clientUserId + " for account " + this.currentAccount);
                }
            }
        }
        getConnectionsManager().sendRequest(tLRPC$TL_account_registerDevice, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda277
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$registerForPush$262(i, str, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$registerForPush$262(int i, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("account " + this.currentAccount + " registered for push, push type: " + i);
            }
            getUserConfig().registeredForPush = true;
            SharedConfig.pushString = str;
            SharedConfig.pushType = i;
            getUserConfig().saveConfig(false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$registerForPush$261();
            }
        });
    }

    public /* synthetic */ void lambda$registerForPush$261() {
        this.registeringForPush = false;
    }

    public void loadCurrentState() {
        if (this.updatingState) {
            return;
        }
        this.updatingState = true;
        getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_updates_getState
            public static int constructor = -NUM;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
                return TLRPC$TL_updates_state.TLdeserialize(abstractSerializedData, i, z);
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda243
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadCurrentState$263(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadCurrentState$263(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.updatingState = false;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_updates_state tLRPC$TL_updates_state = (TLRPC$TL_updates_state) tLObject;
            getMessagesStorage().setLastDateValue(tLRPC$TL_updates_state.date);
            getMessagesStorage().setLastPtsValue(tLRPC$TL_updates_state.pts);
            getMessagesStorage().setLastSeqValue(tLRPC$TL_updates_state.seq);
            getMessagesStorage().setLastQtsValue(tLRPC$TL_updates_state.qts);
            for (int i = 0; i < 3; i++) {
                processUpdatesQueue(i, 2);
            }
            getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        } else if (tLRPC$TL_error.code != 401) {
            loadCurrentState();
        }
    }

    private int getUpdateSeq(TLRPC$Updates tLRPC$Updates) {
        if (tLRPC$Updates instanceof TLRPC$TL_updatesCombined) {
            return tLRPC$Updates.seq_start;
        }
        return tLRPC$Updates.seq;
    }

    private void setUpdatesStartTime(int i, long j) {
        if (i == 0) {
            this.updatesStartWaitTimeSeq = j;
        } else if (i == 1) {
            this.updatesStartWaitTimePts = j;
        } else if (i != 2) {
        } else {
            this.updatesStartWaitTimeQts = j;
        }
    }

    public long getUpdatesStartTime(int i) {
        if (i == 0) {
            return this.updatesStartWaitTimeSeq;
        }
        if (i == 1) {
            return this.updatesStartWaitTimePts;
        }
        if (i != 2) {
            return 0L;
        }
        return this.updatesStartWaitTimeQts;
    }

    private int isValidUpdate(TLRPC$Updates tLRPC$Updates, int i) {
        if (i == 0) {
            int updateSeq = getUpdateSeq(tLRPC$Updates);
            if (getMessagesStorage().getLastSeqValue() + 1 == updateSeq || getMessagesStorage().getLastSeqValue() == updateSeq) {
                return 0;
            }
            return getMessagesStorage().getLastSeqValue() < updateSeq ? 1 : 2;
        } else if (i == 1) {
            if (tLRPC$Updates.pts <= getMessagesStorage().getLastPtsValue()) {
                return 2;
            }
            return getMessagesStorage().getLastPtsValue() + tLRPC$Updates.pts_count == tLRPC$Updates.pts ? 0 : 1;
        } else if (i != 2) {
            return 0;
        } else {
            if (tLRPC$Updates.pts <= getMessagesStorage().getLastQtsValue()) {
                return 2;
            }
            return getMessagesStorage().getLastQtsValue() + tLRPC$Updates.updates.size() == tLRPC$Updates.pts ? 0 : 1;
        }
    }

    private void processChannelsUpdatesQueue(long j, int i) {
        char c;
        ArrayList<TLRPC$Updates> arrayList = this.updatesQueueChannels.get(j);
        if (arrayList == null) {
            return;
        }
        int i2 = this.channelsPts.get(j);
        if (arrayList.isEmpty() || i2 == 0) {
            this.updatesQueueChannels.remove(j);
            return;
        }
        Collections.sort(arrayList, MessagesController$$ExternalSyntheticLambda230.INSTANCE);
        if (i == 2) {
            this.channelsPts.put(j, arrayList.get(0).pts);
        }
        boolean z = false;
        while (arrayList.size() > 0) {
            TLRPC$Updates tLRPC$Updates = arrayList.get(0);
            int i3 = tLRPC$Updates.pts;
            if (i3 <= i2) {
                c = 2;
            } else {
                c = tLRPC$Updates.pts_count + i2 == i3 ? (char) 0 : (char) 1;
            }
            if (c == 0) {
                processUpdates(tLRPC$Updates, true);
                arrayList.remove(0);
                z = true;
            } else if (c == 1) {
                long j2 = this.updatesStartWaitTimeChannels.get(j);
                if (j2 != 0 && (z || Math.abs(System.currentTimeMillis() - j2) <= 1500)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN CHANNEL " + j + " UPDATES QUEUE - will wait more time");
                    }
                    if (!z) {
                        return;
                    }
                    this.updatesStartWaitTimeChannels.put(j, System.currentTimeMillis());
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("HOLE IN CHANNEL " + j + " UPDATES QUEUE - getChannelDifference ");
                }
                this.updatesStartWaitTimeChannels.delete(j);
                this.updatesQueueChannels.remove(j);
                getChannelDifference(j);
                return;
            } else {
                arrayList.remove(0);
            }
        }
        this.updatesQueueChannels.remove(j);
        this.updatesStartWaitTimeChannels.delete(j);
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        FileLog.d("UPDATES CHANNEL " + j + " QUEUE PROCEED - OK");
    }

    public static /* synthetic */ int lambda$processChannelsUpdatesQueue$264(TLRPC$Updates tLRPC$Updates, TLRPC$Updates tLRPC$Updates2) {
        return AndroidUtilities.compare(tLRPC$Updates.pts, tLRPC$Updates2.pts);
    }

    private void processUpdatesQueue(int i, int i2) {
        ArrayList<TLRPC$Updates> arrayList;
        if (i == 0) {
            arrayList = this.updatesQueueSeq;
            Collections.sort(arrayList, new Comparator() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda228
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$processUpdatesQueue$265;
                    lambda$processUpdatesQueue$265 = MessagesController.this.lambda$processUpdatesQueue$265((TLRPC$Updates) obj, (TLRPC$Updates) obj2);
                    return lambda$processUpdatesQueue$265;
                }
            });
        } else if (i == 1) {
            arrayList = this.updatesQueuePts;
            Collections.sort(arrayList, MessagesController$$ExternalSyntheticLambda231.INSTANCE);
        } else if (i == 2) {
            arrayList = this.updatesQueueQts;
            Collections.sort(arrayList, MessagesController$$ExternalSyntheticLambda232.INSTANCE);
        } else {
            arrayList = null;
        }
        if (arrayList != null && !arrayList.isEmpty()) {
            if (i2 == 2) {
                TLRPC$Updates tLRPC$Updates = arrayList.get(0);
                if (i == 0) {
                    getMessagesStorage().setLastSeqValue(getUpdateSeq(tLRPC$Updates));
                } else if (i == 1) {
                    getMessagesStorage().setLastPtsValue(tLRPC$Updates.pts);
                } else {
                    getMessagesStorage().setLastQtsValue(tLRPC$Updates.pts);
                }
            }
            boolean z = false;
            while (arrayList.size() > 0) {
                TLRPC$Updates tLRPC$Updates2 = arrayList.get(0);
                int isValidUpdate = isValidUpdate(tLRPC$Updates2, i);
                if (isValidUpdate == 0) {
                    processUpdates(tLRPC$Updates2, true);
                    arrayList.remove(0);
                    z = true;
                } else if (isValidUpdate == 1) {
                    if (getUpdatesStartTime(i) != 0 && (z || Math.abs(System.currentTimeMillis() - getUpdatesStartTime(i)) <= 1500)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN UPDATES QUEUE - will wait more time");
                        }
                        if (!z) {
                            return;
                        }
                        setUpdatesStartTime(i, System.currentTimeMillis());
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN UPDATES QUEUE - getDifference");
                    }
                    setUpdatesStartTime(i, 0L);
                    arrayList.clear();
                    getDifference();
                    return;
                } else {
                    arrayList.remove(0);
                }
            }
            arrayList.clear();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("UPDATES QUEUE PROCEED - OK");
            }
        }
        setUpdatesStartTime(i, 0L);
    }

    public /* synthetic */ int lambda$processUpdatesQueue$265(TLRPC$Updates tLRPC$Updates, TLRPC$Updates tLRPC$Updates2) {
        return AndroidUtilities.compare(getUpdateSeq(tLRPC$Updates), getUpdateSeq(tLRPC$Updates2));
    }

    public static /* synthetic */ int lambda$processUpdatesQueue$266(TLRPC$Updates tLRPC$Updates, TLRPC$Updates tLRPC$Updates2) {
        return AndroidUtilities.compare(tLRPC$Updates.pts, tLRPC$Updates2.pts);
    }

    public static /* synthetic */ int lambda$processUpdatesQueue$267(TLRPC$Updates tLRPC$Updates, TLRPC$Updates tLRPC$Updates2) {
        return AndroidUtilities.compare(tLRPC$Updates.pts, tLRPC$Updates2.pts);
    }

    public void loadUnknownChannel(final TLRPC$Chat tLRPC$Chat, final long j) {
        NativeByteBuffer nativeByteBuffer;
        Exception e;
        if (!(tLRPC$Chat instanceof TLRPC$TL_channel) || this.gettingUnknownChannels.indexOfKey(tLRPC$Chat.id) >= 0) {
            return;
        }
        if (tLRPC$Chat.access_hash == 0) {
            if (j == 0) {
                return;
            }
            getMessagesStorage().removePendingTask(j);
            return;
        }
        TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
        long j2 = tLRPC$Chat.id;
        tLRPC$TL_inputPeerChannel.channel_id = j2;
        tLRPC$TL_inputPeerChannel.access_hash = tLRPC$Chat.access_hash;
        this.gettingUnknownChannels.put(j2, Boolean.TRUE);
        TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs = new TLRPC$TL_messages_getPeerDialogs();
        TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
        tLRPC$TL_inputDialogPeer.peer = tLRPC$TL_inputPeerChannel;
        tLRPC$TL_messages_getPeerDialogs.peers.add(tLRPC$TL_inputDialogPeer);
        if (j == 0) {
            try {
                nativeByteBuffer = new NativeByteBuffer(tLRPC$Chat.getObjectSize() + 4);
                try {
                    nativeByteBuffer.writeInt32(0);
                    tLRPC$Chat.serializeToStream(nativeByteBuffer);
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_getPeerDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda313
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$loadUnknownChannel$268(j, tLRPC$Chat, tLObject, tLRPC$TL_error);
                        }
                    });
                }
            } catch (Exception e3) {
                nativeByteBuffer = null;
                e = e3;
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getPeerDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda313
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadUnknownChannel$268(j, tLRPC$Chat, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadUnknownChannel$268(long j, TLRPC$Chat tLRPC$Chat, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs = (TLRPC$TL_messages_peerDialogs) tLObject;
            if (!tLRPC$TL_messages_peerDialogs.dialogs.isEmpty() && !tLRPC$TL_messages_peerDialogs.chats.isEmpty()) {
                TLRPC$TL_messages_dialogs tLRPC$TL_messages_dialogs = new TLRPC$TL_messages_dialogs();
                tLRPC$TL_messages_dialogs.dialogs.addAll(tLRPC$TL_messages_peerDialogs.dialogs);
                tLRPC$TL_messages_dialogs.messages.addAll(tLRPC$TL_messages_peerDialogs.messages);
                tLRPC$TL_messages_dialogs.users.addAll(tLRPC$TL_messages_peerDialogs.users);
                tLRPC$TL_messages_dialogs.chats.addAll(tLRPC$TL_messages_peerDialogs.chats);
                processLoadedDialogs(tLRPC$TL_messages_dialogs, null, ((TLRPC$TL_dialog) tLRPC$TL_messages_peerDialogs.dialogs.get(0)).folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_CHANNEL, false, false, false);
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        this.gettingUnknownChannels.delete(tLRPC$Chat.id);
    }

    public void startShortPoll(final TLRPC$Chat tLRPC$Chat, final int i, final boolean z) {
        if (tLRPC$Chat == null) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda155
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$startShortPoll$269(tLRPC$Chat, z, i);
            }
        });
    }

    public /* synthetic */ void lambda$startShortPoll$269(TLRPC$Chat tLRPC$Chat, boolean z, int i) {
        ArrayList<Integer> arrayList = this.needShortPollChannels.get(tLRPC$Chat.id);
        ArrayList<Integer> arrayList2 = this.needShortPollOnlines.get(tLRPC$Chat.id);
        if (z) {
            if (arrayList != null) {
                arrayList.remove(Integer.valueOf(i));
            }
            if (arrayList == null || arrayList.isEmpty()) {
                this.needShortPollChannels.delete(tLRPC$Chat.id);
            }
            if (!tLRPC$Chat.megagroup) {
                return;
            }
            if (arrayList2 != null) {
                arrayList2.remove(Integer.valueOf(i));
            }
            if (arrayList2 != null && !arrayList2.isEmpty()) {
                return;
            }
            this.needShortPollOnlines.delete(tLRPC$Chat.id);
            return;
        }
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.needShortPollChannels.put(tLRPC$Chat.id, arrayList);
        }
        if (!arrayList.contains(Integer.valueOf(i))) {
            arrayList.add(Integer.valueOf(i));
        }
        if (this.shortPollChannels.indexOfKey(tLRPC$Chat.id) < 0) {
            getChannelDifference(tLRPC$Chat.id, 3, 0L, null);
        }
        if (!tLRPC$Chat.megagroup) {
            return;
        }
        if (arrayList2 == null) {
            arrayList2 = new ArrayList<>();
            this.needShortPollOnlines.put(tLRPC$Chat.id, arrayList2);
        }
        if (!arrayList2.contains(Integer.valueOf(i))) {
            arrayList2.add(Integer.valueOf(i));
        }
        if (this.shortPollOnlines.indexOfKey(tLRPC$Chat.id) >= 0) {
            return;
        }
        this.shortPollOnlines.put(tLRPC$Chat.id, 0);
    }

    private void getChannelDifference(long j) {
        getChannelDifference(j, 0, 0L, null);
    }

    public static boolean isSupportUser(TLRPC$User tLRPC$User) {
        if (tLRPC$User != null) {
            if (!tLRPC$User.support) {
                long j = tLRPC$User.id;
                if (j == 777000 || j == 333000 || j == 4240000 || j == 4244000 || j == 4245000 || j == 4246000 || j == 410000 || j == 420000 || j == 431000 || j == NUM || j == 434000 || j == 4243000 || j == 439000 || j == 449000 || j == 450000 || j == 452000 || j == 454000 || j == 4254000 || j == 455000 || j == 460000 || j == 470000 || j == 479000 || j == 796000 || j == 482000 || j == 490000 || j == 496000 || j == 497000 || j == 498000 || j == 4298000) {
                }
            }
            return true;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:116:0x00cd  */
    /* JADX WARN: Removed duplicated region for block: B:119:0x00d4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void getChannelDifference(final long r17, final int r19, long r20, org.telegram.tgnet.TLRPC$InputChannel r22) {
        /*
            Method dump skipped, instructions count: 261
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.getChannelDifference(long, int, long, org.telegram.tgnet.TLRPC$InputChannel):void");
    }

    public /* synthetic */ void lambda$getChannelDifference$278(final long j, final int i, final long j2, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        final TLRPC$Chat tLRPC$Chat;
        if (tLObject == null) {
            if (tLRPC$TL_error == null) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda168
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$getChannelDifference$277(tLRPC$TL_error, j);
                }
            });
            this.gettingDifferenceChannels.delete(j);
            if (j2 == 0) {
                return;
            }
            getMessagesStorage().removePendingTask(j2);
            return;
        }
        final TLRPC$updates_ChannelDifference tLRPC$updates_ChannelDifference = (TLRPC$updates_ChannelDifference) tLObject;
        final LongSparseArray longSparseArray = new LongSparseArray();
        int i2 = 0;
        for (int i3 = 0; i3 < tLRPC$updates_ChannelDifference.users.size(); i3++) {
            TLRPC$User tLRPC$User = tLRPC$updates_ChannelDifference.users.get(i3);
            longSparseArray.put(tLRPC$User.id, tLRPC$User);
        }
        int i4 = 0;
        while (true) {
            if (i4 >= tLRPC$updates_ChannelDifference.chats.size()) {
                tLRPC$Chat = null;
                break;
            }
            tLRPC$Chat = tLRPC$updates_ChannelDifference.chats.get(i4);
            if (tLRPC$Chat.id == j) {
                break;
            }
            i4++;
        }
        final ArrayList arrayList = new ArrayList();
        if (!tLRPC$updates_ChannelDifference.other_updates.isEmpty()) {
            while (i2 < tLRPC$updates_ChannelDifference.other_updates.size()) {
                TLRPC$Update tLRPC$Update = tLRPC$updates_ChannelDifference.other_updates.get(i2);
                if (tLRPC$Update instanceof TLRPC$TL_updateMessageID) {
                    arrayList.add((TLRPC$TL_updateMessageID) tLRPC$Update);
                    tLRPC$updates_ChannelDifference.other_updates.remove(i2);
                    i2--;
                }
                i2++;
            }
        }
        getMessagesStorage().putUsersAndChats(tLRPC$updates_ChannelDifference.users, tLRPC$updates_ChannelDifference.chats, true, true);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda203
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getChannelDifference$270(tLRPC$updates_ChannelDifference);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda123
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getChannelDifference$276(arrayList, j, tLRPC$updates_ChannelDifference, tLRPC$Chat, longSparseArray, i, j2);
            }
        });
    }

    public /* synthetic */ void lambda$getChannelDifference$270(TLRPC$updates_ChannelDifference tLRPC$updates_ChannelDifference) {
        putUsers(tLRPC$updates_ChannelDifference.users, false);
        putChats(tLRPC$updates_ChannelDifference.chats, false);
    }

    public /* synthetic */ void lambda$getChannelDifference$276(ArrayList arrayList, final long j, final TLRPC$updates_ChannelDifference tLRPC$updates_ChannelDifference, final TLRPC$Chat tLRPC$Chat, final LongSparseArray longSparseArray, final int i, final long j2) {
        if (!arrayList.isEmpty()) {
            final SparseArray sparseArray = new SparseArray();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                TLRPC$TL_updateMessageID tLRPC$TL_updateMessageID = (TLRPC$TL_updateMessageID) it.next();
                long[] updateMessageStateAndId = getMessagesStorage().updateMessageStateAndId(tLRPC$TL_updateMessageID.random_id, -j, null, tLRPC$TL_updateMessageID.id, 0, false, -1);
                if (updateMessageStateAndId != null) {
                    sparseArray.put(tLRPC$TL_updateMessageID.id, updateMessageStateAndId);
                }
            }
            if (sparseArray.size() != 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda94
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$getChannelDifference$271(sparseArray);
                    }
                });
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda204
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getChannelDifference$275(tLRPC$updates_ChannelDifference, j, tLRPC$Chat, longSparseArray, i, j2);
            }
        });
    }

    public /* synthetic */ void lambda$getChannelDifference$271(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            long[] jArr = (long[]) sparseArray.valueAt(i);
            getSendMessagesHelper().processSentMessage((int) jArr[1]);
            getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf((int) jArr[1]), Integer.valueOf(keyAt), null, Long.valueOf(jArr[0]), 0L, -1, Boolean.FALSE);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:166:0x0173  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getChannelDifference$275(final org.telegram.tgnet.TLRPC$updates_ChannelDifference r23, long r24, org.telegram.tgnet.TLRPC$Chat r26, androidx.collection.LongSparseArray r27, int r28, long r29) {
        /*
            Method dump skipped, instructions count: 617
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$getChannelDifference$275(org.telegram.tgnet.TLRPC$updates_ChannelDifference, long, org.telegram.tgnet.TLRPC$Chat, androidx.collection.LongSparseArray, int, long):void");
    }

    public /* synthetic */ void lambda$getChannelDifference$272(LongSparseArray longSparseArray) {
        for (int i = 0; i < longSparseArray.size(); i++) {
            updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i), false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$getChannelDifference$274(final ArrayList arrayList, TLRPC$updates_ChannelDifference tLRPC$updates_ChannelDifference) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda115
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$getChannelDifference$273(arrayList);
                }
            });
        }
        getMessagesStorage().putMessages(tLRPC$updates_ChannelDifference.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), false, 0);
    }

    public /* synthetic */ void lambda$getChannelDifference$273(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$getChannelDifference$277(TLRPC$TL_error tLRPC$TL_error, long j) {
        checkChannelError(tLRPC$TL_error.text, j);
    }

    private void checkChannelError(String str, long j) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1809401834:
                if (str.equals("USER_BANNED_IN_CHANNEL")) {
                    c = 0;
                    break;
                }
                break;
            case -795226617:
                if (str.equals("CHANNEL_PRIVATE")) {
                    c = 1;
                    break;
                }
                break;
            case -471086771:
                if (str.equals("CHANNEL_PUBLIC_GROUP_NA")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                getNotificationCenter().postNotificationName(NotificationCenter.chatInfoCantLoad, Long.valueOf(j), 2);
                return;
            case 1:
                getNotificationCenter().postNotificationName(NotificationCenter.chatInfoCantLoad, Long.valueOf(j), 0);
                return;
            case 2:
                getNotificationCenter().postNotificationName(NotificationCenter.chatInfoCantLoad, Long.valueOf(j), 1);
                return;
            default:
                return;
        }
    }

    public void getDifference() {
        getDifference(getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue(), false);
    }

    public void getDifference(int i, final int i2, final int i3, boolean z) {
        registerForPush(SharedConfig.pushType, SharedConfig.pushString);
        if (getMessagesStorage().getLastPtsValue() == 0) {
            loadCurrentState();
        } else if (!z && this.gettingDifference) {
        } else {
            this.gettingDifference = true;
            TLRPC$TL_updates_getDifference tLRPC$TL_updates_getDifference = new TLRPC$TL_updates_getDifference();
            tLRPC$TL_updates_getDifference.pts = i;
            tLRPC$TL_updates_getDifference.date = i2;
            tLRPC$TL_updates_getDifference.qts = i3;
            if (this.getDifferenceFirstSync) {
                tLRPC$TL_updates_getDifference.flags |= 1;
                if (ApplicationLoader.isConnectedOrConnectingToWiFi()) {
                    tLRPC$TL_updates_getDifference.pts_total_limit = 5000;
                } else {
                    tLRPC$TL_updates_getDifference.pts_total_limit = 1000;
                }
                this.getDifferenceFirstSync = false;
            }
            if (tLRPC$TL_updates_getDifference.date == 0) {
                tLRPC$TL_updates_getDifference.date = getConnectionsManager().getCurrentTime();
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start getDifference with date = " + i2 + " pts = " + i + " qts = " + i3);
            }
            getConnectionsManager().setIsUpdating(true);
            getConnectionsManager().sendRequest(tLRPC$TL_updates_getDifference, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda269
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$getDifference$288(i2, i3, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$getDifference$288(final int i, final int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int i3 = 0;
        if (tLRPC$TL_error == null) {
            final TLRPC$updates_Difference tLRPC$updates_Difference = (TLRPC$updates_Difference) tLObject;
            if (tLRPC$updates_Difference instanceof TLRPC$TL_updates_differenceTooLong) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda206
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$getDifference$279(tLRPC$updates_Difference, i, i2);
                    }
                });
                return;
            }
            if (tLRPC$updates_Difference instanceof TLRPC$TL_updates_differenceSlice) {
                TLRPC$TL_updates_state tLRPC$TL_updates_state = tLRPC$updates_Difference.intermediate_state;
                getDifference(tLRPC$TL_updates_state.pts, tLRPC$TL_updates_state.date, tLRPC$TL_updates_state.qts, true);
            }
            final LongSparseArray longSparseArray = new LongSparseArray();
            final LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i4 = 0; i4 < tLRPC$updates_Difference.users.size(); i4++) {
                TLRPC$User tLRPC$User = tLRPC$updates_Difference.users.get(i4);
                longSparseArray.put(tLRPC$User.id, tLRPC$User);
            }
            for (int i5 = 0; i5 < tLRPC$updates_Difference.chats.size(); i5++) {
                TLRPC$Chat tLRPC$Chat = tLRPC$updates_Difference.chats.get(i5);
                longSparseArray2.put(tLRPC$Chat.id, tLRPC$Chat);
            }
            final ArrayList arrayList = new ArrayList();
            if (!tLRPC$updates_Difference.other_updates.isEmpty()) {
                while (i3 < tLRPC$updates_Difference.other_updates.size()) {
                    TLRPC$Update tLRPC$Update = tLRPC$updates_Difference.other_updates.get(i3);
                    if (tLRPC$Update instanceof TLRPC$TL_updateMessageID) {
                        arrayList.add((TLRPC$TL_updateMessageID) tLRPC$Update);
                        tLRPC$updates_Difference.other_updates.remove(i3);
                    } else {
                        if (getUpdateType(tLRPC$Update) == 2) {
                            long updateChannelId = getUpdateChannelId(tLRPC$Update);
                            int i6 = this.channelsPts.get(updateChannelId);
                            if (i6 == 0 && (i6 = getMessagesStorage().getChannelPtsSync(updateChannelId)) != 0) {
                                this.channelsPts.put(updateChannelId, i6);
                            }
                            if (i6 != 0 && getUpdatePts(tLRPC$Update) <= i6) {
                                tLRPC$updates_Difference.other_updates.remove(i3);
                            }
                        }
                        i3++;
                    }
                    i3--;
                    i3++;
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda205
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$getDifference$280(tLRPC$updates_Difference);
                }
            });
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda208
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$getDifference$287(tLRPC$updates_Difference, arrayList, longSparseArray, longSparseArray2);
                }
            });
            return;
        }
        this.gettingDifference = false;
        getConnectionsManager().setIsUpdating(false);
    }

    public /* synthetic */ void lambda$getDifference$279(TLRPC$updates_Difference tLRPC$updates_Difference, int i, int i2) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), tLRPC$updates_Difference.pts, i, i2);
    }

    public /* synthetic */ void lambda$getDifference$280(TLRPC$updates_Difference tLRPC$updates_Difference) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        putUsers(tLRPC$updates_Difference.users, false);
        putChats(tLRPC$updates_Difference.chats, false);
    }

    public /* synthetic */ void lambda$getDifference$287(final TLRPC$updates_Difference tLRPC$updates_Difference, ArrayList arrayList, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2) {
        getMessagesStorage().putUsersAndChats(tLRPC$updates_Difference.users, tLRPC$updates_Difference.chats, true, false);
        if (!arrayList.isEmpty()) {
            final SparseArray sparseArray = new SparseArray();
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$TL_updateMessageID tLRPC$TL_updateMessageID = (TLRPC$TL_updateMessageID) arrayList.get(i);
                long[] updateMessageStateAndId = getMessagesStorage().updateMessageStateAndId(tLRPC$TL_updateMessageID.random_id, 0L, null, tLRPC$TL_updateMessageID.id, 0, false, -1);
                if (updateMessageStateAndId != null) {
                    sparseArray.put(tLRPC$TL_updateMessageID.id, updateMessageStateAndId);
                }
            }
            if (sparseArray.size() != 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda95
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$getDifference$281(sparseArray);
                    }
                });
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda207
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getDifference$286(tLRPC$updates_Difference, longSparseArray, longSparseArray2);
            }
        });
    }

    public /* synthetic */ void lambda$getDifference$281(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            long[] jArr = (long[]) sparseArray.valueAt(i);
            getSendMessagesHelper().processSentMessage((int) jArr[1]);
            getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf((int) jArr[1]), Integer.valueOf(keyAt), null, Long.valueOf(jArr[0]), 0L, -1, Boolean.FALSE);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:154:0x0121, code lost:
        if (r10.messageOwner.from_scheduled != false) goto L83;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getDifference$286(final org.telegram.tgnet.TLRPC$updates_Difference r19, androidx.collection.LongSparseArray r20, androidx.collection.LongSparseArray r21) {
        /*
            Method dump skipped, instructions count: 677
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$getDifference$286(org.telegram.tgnet.TLRPC$updates_Difference, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    public /* synthetic */ void lambda$getDifference$285(final ArrayList arrayList, final TLRPC$updates_Difference tLRPC$updates_Difference, LongSparseArray longSparseArray) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda127
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$getDifference$282(arrayList, tLRPC$updates_Difference);
                }
            });
        }
        getMessagesStorage().putMessages(tLRPC$updates_Difference.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), false, 0);
        for (int i = 0; i < longSparseArray.size(); i++) {
            final long keyAt = longSparseArray.keyAt(i);
            final ArrayList<MessageObject> arrayList2 = (ArrayList) longSparseArray.valueAt(i);
            getMediaDataController().loadReplyMessagesForMessages(arrayList2, keyAt, false, 0, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda86
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$getDifference$284(keyAt, arrayList2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$getDifference$282(ArrayList arrayList, TLRPC$updates_Difference tLRPC$updates_Difference) {
        getNotificationsController().processNewMessages(arrayList, !(tLRPC$updates_Difference instanceof TLRPC$TL_updates_differenceSlice), false, null);
    }

    public /* synthetic */ void lambda$getDifference$284(final long j, final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda85
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getDifference$283(j, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$getDifference$283(long j, ArrayList arrayList) {
        updateInterfaceWithMessages(j, arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void markDialogAsUnread(long j, TLRPC$InputPeer tLRPC$InputPeer, final long j2) {
        NativeByteBuffer nativeByteBuffer;
        TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
        NativeByteBuffer nativeByteBuffer2 = null;
        if (tLRPC$Dialog != null) {
            tLRPC$Dialog.unread_mark = true;
            if (tLRPC$Dialog.unread_count == 0 && !isDialogMuted(j, 0)) {
                this.unreadUnmutedDialogs++;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
            getMessagesStorage().setDialogUnread(j, true);
            int i = 0;
            while (true) {
                DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                if (i < dialogFilterArr.length) {
                    if (dialogFilterArr[i] != null && (dialogFilterArr[i].flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                        sortDialogs(null);
                        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
        }
        if (!DialogObject.isEncryptedDialog(j)) {
            TLRPC$TL_messages_markDialogUnread tLRPC$TL_messages_markDialogUnread = new TLRPC$TL_messages_markDialogUnread();
            tLRPC$TL_messages_markDialogUnread.unread = true;
            if (tLRPC$InputPeer == null) {
                tLRPC$InputPeer = getInputPeer(j);
            }
            if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerEmpty) {
                return;
            }
            TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
            tLRPC$TL_inputDialogPeer.peer = tLRPC$InputPeer;
            tLRPC$TL_messages_markDialogUnread.peer = tLRPC$TL_inputDialogPeer;
            if (j2 == 0) {
                try {
                    nativeByteBuffer = new NativeByteBuffer(tLRPC$InputPeer.getObjectSize() + 12);
                } catch (Exception e) {
                    e = e;
                }
                try {
                    nativeByteBuffer.writeInt32(9);
                    nativeByteBuffer.writeInt64(j);
                    tLRPC$InputPeer.serializeToStream(nativeByteBuffer);
                } catch (Exception e2) {
                    e = e2;
                    nativeByteBuffer2 = nativeByteBuffer;
                    FileLog.e(e);
                    nativeByteBuffer = nativeByteBuffer2;
                    j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_markDialogUnread, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda287
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$markDialogAsUnread$289(j2, tLObject, tLRPC$TL_error);
                        }
                    });
                }
                j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_markDialogUnread, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda287
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$markDialogAsUnread$289(j2, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$markDialogAsUnread$289(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void loadUnreadDialogs() {
        if (this.loadingUnreadDialogs || getUserConfig().unreadDialogsLoaded) {
            return;
        }
        this.loadingUnreadDialogs = true;
        getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_messages_getDialogUnreadMarks
            public static int constructor = NUM;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
                TLRPC$Vector tLRPC$Vector = new TLRPC$Vector();
                int readInt32 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt32; i2++) {
                    TLRPC$DialogPeer TLdeserialize = TLRPC$DialogPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return tLRPC$Vector;
                    }
                    tLRPC$Vector.objects.add(TLdeserialize);
                }
                return tLRPC$Vector;
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda262
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadUnreadDialogs$291(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadUnreadDialogs$291(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda142
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadUnreadDialogs$290(tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$loadUnreadDialogs$290(TLObject tLObject) {
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            int size = tLRPC$Vector.objects.size();
            for (int i = 0; i < size; i++) {
                TLRPC$DialogPeer tLRPC$DialogPeer = (TLRPC$DialogPeer) tLRPC$Vector.objects.get(i);
                if (tLRPC$DialogPeer instanceof TLRPC$TL_dialogPeer) {
                    TLRPC$Peer tLRPC$Peer = ((TLRPC$TL_dialogPeer) tLRPC$DialogPeer).peer;
                    long j = tLRPC$Peer.user_id;
                    if (j == 0) {
                        long j2 = tLRPC$Peer.chat_id;
                        j = j2 != 0 ? -j2 : -tLRPC$Peer.channel_id;
                    }
                    getMessagesStorage().setDialogUnread(j, true);
                    TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
                    if (tLRPC$Dialog != null && !tLRPC$Dialog.unread_mark) {
                        tLRPC$Dialog.unread_mark = true;
                        if (tLRPC$Dialog.unread_count == 0 && !isDialogMuted(j, 0)) {
                            this.unreadUnmutedDialogs++;
                        }
                    }
                }
            }
            getUserConfig().unreadDialogsLoaded = true;
            getUserConfig().saveConfig(false);
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
            this.loadingUnreadDialogs = false;
        }
    }

    public void reorderPinnedDialogs(int i, ArrayList<TLRPC$InputDialogPeer> arrayList, final long j) {
        NativeByteBuffer nativeByteBuffer;
        TLRPC$TL_messages_reorderPinnedDialogs tLRPC$TL_messages_reorderPinnedDialogs = new TLRPC$TL_messages_reorderPinnedDialogs();
        tLRPC$TL_messages_reorderPinnedDialogs.folder_id = i;
        tLRPC$TL_messages_reorderPinnedDialogs.force = true;
        if (j == 0) {
            ArrayList<TLRPC$Dialog> dialogs = getDialogs(i);
            if (dialogs.isEmpty()) {
                return;
            }
            ArrayList<Long> arrayList2 = new ArrayList<>();
            ArrayList<Integer> arrayList3 = new ArrayList<>();
            int size = dialogs.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC$Dialog tLRPC$Dialog = dialogs.get(i3);
                if (!(tLRPC$Dialog instanceof TLRPC$TL_dialogFolder)) {
                    if (!tLRPC$Dialog.pinned) {
                        if (tLRPC$Dialog.id != this.promoDialogId) {
                            break;
                        }
                    } else {
                        arrayList2.add(Long.valueOf(tLRPC$Dialog.id));
                        arrayList3.add(Integer.valueOf(tLRPC$Dialog.pinnedNum));
                        if (!DialogObject.isEncryptedDialog(tLRPC$Dialog.id)) {
                            TLRPC$InputPeer inputPeer = getInputPeer(tLRPC$Dialog.id);
                            TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
                            tLRPC$TL_inputDialogPeer.peer = inputPeer;
                            tLRPC$TL_messages_reorderPinnedDialogs.order.add(tLRPC$TL_inputDialogPeer);
                            i2 += tLRPC$TL_inputDialogPeer.getObjectSize();
                        }
                    }
                }
            }
            getMessagesStorage().setDialogsPinned(arrayList2, arrayList3);
            NativeByteBuffer nativeByteBuffer2 = null;
            try {
                nativeByteBuffer = new NativeByteBuffer(i2 + 12);
                try {
                    nativeByteBuffer.writeInt32(16);
                    nativeByteBuffer.writeInt32(i);
                    nativeByteBuffer.writeInt32(tLRPC$TL_messages_reorderPinnedDialogs.order.size());
                    int size2 = tLRPC$TL_messages_reorderPinnedDialogs.order.size();
                    for (int i4 = 0; i4 < size2; i4++) {
                        tLRPC$TL_messages_reorderPinnedDialogs.order.get(i4).serializeToStream(nativeByteBuffer);
                    }
                } catch (Exception e) {
                    e = e;
                    nativeByteBuffer2 = nativeByteBuffer;
                    FileLog.e(e);
                    nativeByteBuffer = nativeByteBuffer2;
                    j = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_reorderPinnedDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda294
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$reorderPinnedDialogs$292(j, tLObject, tLRPC$TL_error);
                        }
                    });
                }
            } catch (Exception e2) {
                e = e2;
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        } else {
            tLRPC$TL_messages_reorderPinnedDialogs.order = arrayList;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_reorderPinnedDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda294
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$reorderPinnedDialogs$292(j, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$reorderPinnedDialogs$292(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public boolean pinDialog(long j, boolean z, TLRPC$InputPeer tLRPC$InputPeer, long j2) {
        NativeByteBuffer nativeByteBuffer;
        final long createPendingTask;
        TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
        if (tLRPC$Dialog == null || tLRPC$Dialog.pinned == z) {
            return tLRPC$Dialog != null;
        }
        int i = tLRPC$Dialog.folder_id;
        ArrayList<TLRPC$Dialog> dialogs = getDialogs(i);
        tLRPC$Dialog.pinned = z;
        if (z) {
            int i2 = 0;
            for (int i3 = 0; i3 < dialogs.size(); i3++) {
                TLRPC$Dialog tLRPC$Dialog2 = dialogs.get(i3);
                if (!(tLRPC$Dialog2 instanceof TLRPC$TL_dialogFolder)) {
                    if (!tLRPC$Dialog2.pinned) {
                        if (tLRPC$Dialog2.id != this.promoDialogId) {
                            break;
                        }
                    } else {
                        i2 = Math.max(tLRPC$Dialog2.pinnedNum, i2);
                    }
                }
            }
            tLRPC$Dialog.pinnedNum = i2 + 1;
        } else {
            tLRPC$Dialog.pinnedNum = 0;
        }
        NativeByteBuffer nativeByteBuffer2 = null;
        sortDialogs(null);
        if (!z && !dialogs.isEmpty() && dialogs.get(dialogs.size() - 1) == tLRPC$Dialog && !this.dialogsEndReached.get(i)) {
            dialogs.remove(dialogs.size() - 1);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        if (!DialogObject.isEncryptedDialog(j) && j2 != -1) {
            TLRPC$TL_messages_toggleDialogPin tLRPC$TL_messages_toggleDialogPin = new TLRPC$TL_messages_toggleDialogPin();
            tLRPC$TL_messages_toggleDialogPin.pinned = z;
            TLRPC$InputPeer inputPeer = tLRPC$InputPeer == null ? getInputPeer(j) : tLRPC$InputPeer;
            if (inputPeer instanceof TLRPC$TL_inputPeerEmpty) {
                return false;
            }
            TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
            tLRPC$TL_inputDialogPeer.peer = inputPeer;
            tLRPC$TL_messages_toggleDialogPin.peer = tLRPC$TL_inputDialogPeer;
            if (j2 == 0) {
                try {
                    nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 16);
                } catch (Exception e) {
                    e = e;
                }
                try {
                    nativeByteBuffer.writeInt32(4);
                    nativeByteBuffer.writeInt64(j);
                    nativeByteBuffer.writeBool(z);
                    inputPeer.serializeToStream(nativeByteBuffer);
                } catch (Exception e2) {
                    e = e2;
                    nativeByteBuffer2 = nativeByteBuffer;
                    FileLog.e(e);
                    nativeByteBuffer = nativeByteBuffer2;
                    createPendingTask = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_toggleDialogPin, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda292
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$pinDialog$293(createPendingTask, tLObject, tLRPC$TL_error);
                        }
                    });
                    getMessagesStorage().setDialogPinned(j, tLRPC$Dialog.pinnedNum);
                    return true;
                }
                createPendingTask = getMessagesStorage().createPendingTask(nativeByteBuffer);
            } else {
                createPendingTask = j2;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_toggleDialogPin, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda292
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$pinDialog$293(createPendingTask, tLObject, tLRPC$TL_error);
                }
            });
        }
        getMessagesStorage().setDialogPinned(j, tLRPC$Dialog.pinnedNum);
        return true;
    }

    public /* synthetic */ void lambda$pinDialog$293(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void loadPinnedDialogs(final int i, long j, ArrayList<Long> arrayList) {
        if (this.loadingPinnedDialogs.indexOfKey(i) >= 0 || getUserConfig().isPinnedDialogsLoaded(i)) {
            return;
        }
        this.loadingPinnedDialogs.put(i, 1);
        TLRPC$TL_messages_getPinnedDialogs tLRPC$TL_messages_getPinnedDialogs = new TLRPC$TL_messages_getPinnedDialogs();
        tLRPC$TL_messages_getPinnedDialogs.folder_id = i;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getPinnedDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda267
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$loadPinnedDialogs$296(i, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:117:0x009f, code lost:
        if (r9.left != false) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:118:0x00a1, code lost:
        r16 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:124:0x00b4, code lost:
        if (r9.migrated_to != null) goto L22;
     */
    /* JADX WARN: Removed duplicated region for block: B:128:0x00db  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadPinnedDialogs$296(final int r19, org.telegram.tgnet.TLObject r20, org.telegram.tgnet.TLRPC$TL_error r21) {
        /*
            Method dump skipped, instructions count: 492
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$loadPinnedDialogs$296(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$loadPinnedDialogs$295(final int i, final ArrayList arrayList, final boolean z, final TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs, final LongSparseArray longSparseArray, final TLRPC$TL_messages_dialogs tLRPC$TL_messages_dialogs) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda54
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$loadPinnedDialogs$294(i, arrayList, z, tLRPC$TL_messages_peerDialogs, longSparseArray, tLRPC$TL_messages_dialogs);
            }
        });
    }

    public /* synthetic */ void lambda$loadPinnedDialogs$294(int i, ArrayList arrayList, boolean z, TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs, LongSparseArray longSparseArray, TLRPC$TL_messages_dialogs tLRPC$TL_messages_dialogs) {
        boolean z2;
        boolean z3;
        boolean z4;
        ArrayList<Integer> arrayList2;
        ArrayList<MessageObject> arrayList3;
        this.loadingPinnedDialogs.delete(i);
        applyDialogsNotificationsSettings(arrayList);
        ArrayList<TLRPC$Dialog> dialogs = getDialogs(i);
        int i2 = z ? 1 : 0;
        int i3 = 0;
        int i4 = 0;
        boolean z5 = false;
        while (true) {
            z2 = true;
            if (i3 >= dialogs.size()) {
                break;
            }
            TLRPC$Dialog tLRPC$Dialog = dialogs.get(i3);
            if (!(tLRPC$Dialog instanceof TLRPC$TL_dialogFolder)) {
                if (DialogObject.isEncryptedDialog(tLRPC$Dialog.id)) {
                    if (i2 < arrayList.size()) {
                        arrayList.add(i2, tLRPC$Dialog);
                    } else {
                        arrayList.add(tLRPC$Dialog);
                    }
                    i2++;
                } else if (!tLRPC$Dialog.pinned) {
                    if (tLRPC$Dialog.id != this.promoDialogId) {
                        break;
                    }
                } else {
                    i4 = Math.max(tLRPC$Dialog.pinnedNum, i4);
                    tLRPC$Dialog.pinned = false;
                    tLRPC$Dialog.pinnedNum = 0;
                    i2++;
                    z5 = true;
                }
            }
            i3++;
        }
        ArrayList<Long> arrayList4 = new ArrayList<>();
        if (!arrayList.isEmpty()) {
            putUsers(tLRPC$TL_messages_peerDialogs.users, false);
            putChats(tLRPC$TL_messages_peerDialogs.chats, false);
            ArrayList<Long> arrayList5 = new ArrayList<>();
            ArrayList<Integer> arrayList6 = new ArrayList<>();
            int size = arrayList.size();
            int i5 = 0;
            z3 = false;
            while (i5 < size) {
                TLRPC$Dialog tLRPC$Dialog2 = (TLRPC$Dialog) arrayList.get(i5);
                tLRPC$Dialog2.pinnedNum = (size - i5) + i4;
                arrayList4.add(Long.valueOf(tLRPC$Dialog2.id));
                TLRPC$Dialog tLRPC$Dialog3 = this.dialogs_dict.get(tLRPC$Dialog2.id);
                if (tLRPC$Dialog3 != null) {
                    tLRPC$Dialog3.pinned = z2;
                    tLRPC$Dialog3.pinnedNum = tLRPC$Dialog2.pinnedNum;
                    arrayList5.add(Long.valueOf(tLRPC$Dialog2.id));
                    arrayList6.add(Integer.valueOf(tLRPC$Dialog2.pinnedNum));
                    arrayList2 = arrayList6;
                } else {
                    this.dialogs_dict.put(tLRPC$Dialog2.id, tLRPC$Dialog2);
                    ArrayList<MessageObject> arrayList7 = (ArrayList) longSparseArray.get(tLRPC$Dialog2.id);
                    arrayList2 = arrayList6;
                    this.dialogMessage.put(tLRPC$Dialog2.id, arrayList7);
                    if (arrayList7 != null) {
                        int i6 = 0;
                        while (i6 < arrayList7.size()) {
                            MessageObject messageObject = arrayList7.get(i6);
                            if (messageObject != null) {
                                arrayList3 = arrayList7;
                                if (messageObject.messageOwner.peer_id.channel_id == 0) {
                                    this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                    this.dialogsLoadedTillDate = Math.min(this.dialogsLoadedTillDate, messageObject.messageOwner.date);
                                    long j = messageObject.messageOwner.random_id;
                                    if (j != 0) {
                                        this.dialogMessagesByRandomIds.put(j, messageObject);
                                    }
                                }
                            } else {
                                arrayList3 = arrayList7;
                            }
                            i6++;
                            arrayList7 = arrayList3;
                        }
                    }
                    z3 = true;
                }
                i5++;
                arrayList6 = arrayList2;
                z5 = true;
                z2 = true;
            }
            getMessagesStorage().setDialogsPinned(arrayList5, arrayList6);
        } else {
            z3 = false;
        }
        if (z5) {
            if (z3) {
                this.allDialogs.clear();
                int size2 = this.dialogs_dict.size();
                for (int i7 = 0; i7 < size2; i7++) {
                    TLRPC$Dialog valueAt = this.dialogs_dict.valueAt(i7);
                    if (this.deletingDialogs.indexOfKey(valueAt.id) < 0) {
                        this.allDialogs.add(valueAt);
                    }
                }
            }
            sortDialogs(null);
            z4 = false;
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        } else {
            z4 = false;
        }
        getMessagesStorage().unpinAllDialogsExceptNew(arrayList4, i);
        getMessagesStorage().putDialogs(tLRPC$TL_messages_dialogs, 1);
        getUserConfig().setPinnedDialogsLoaded(i, true);
        getUserConfig().saveConfig(z4);
    }

    public void generateJoinMessage(final long j, boolean z) {
        TLRPC$Chat chat = getChat(Long.valueOf(j));
        if (chat == null || !ChatObject.isChannel(j, this.currentAccount)) {
            return;
        }
        if ((chat.left || chat.kicked) && !z) {
            return;
        }
        TLRPC$TL_messageService tLRPC$TL_messageService = new TLRPC$TL_messageService();
        tLRPC$TL_messageService.flags = 256;
        int newMessageId = getUserConfig().getNewMessageId();
        tLRPC$TL_messageService.id = newMessageId;
        tLRPC$TL_messageService.local_id = newMessageId;
        tLRPC$TL_messageService.date = getConnectionsManager().getCurrentTime();
        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
        tLRPC$TL_messageService.from_id = tLRPC$TL_peerUser;
        tLRPC$TL_peerUser.user_id = getUserConfig().getClientUserId();
        TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
        tLRPC$TL_messageService.peer_id = tLRPC$TL_peerChannel;
        tLRPC$TL_peerChannel.channel_id = j;
        tLRPC$TL_messageService.dialog_id = -j;
        tLRPC$TL_messageService.post = true;
        TLRPC$TL_messageActionChatAddUser tLRPC$TL_messageActionChatAddUser = new TLRPC$TL_messageActionChatAddUser();
        tLRPC$TL_messageService.action = tLRPC$TL_messageActionChatAddUser;
        tLRPC$TL_messageActionChatAddUser.users.add(Long.valueOf(getUserConfig().getClientUserId()));
        getUserConfig().saveConfig(false);
        final ArrayList arrayList = new ArrayList();
        ArrayList<TLRPC$Message> arrayList2 = new ArrayList<>();
        arrayList2.add(tLRPC$TL_messageService);
        arrayList.add(new MessageObject(this.currentAccount, tLRPC$TL_messageService, true, false));
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda113
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$generateJoinMessage$298(arrayList);
            }
        });
        getMessagesStorage().putMessages(arrayList2, true, true, false, 0, false, 0);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda87
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$generateJoinMessage$299(j, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$generateJoinMessage$297(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$generateJoinMessage$298(final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda111
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$generateJoinMessage$297(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$generateJoinMessage$299(long j, ArrayList arrayList) {
        updateInterfaceWithMessages(-j, arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void deleteMessagesByPush(final long j, final ArrayList<Integer> arrayList, final long j2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda120
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$deleteMessagesByPush$301(arrayList, j2, j);
            }
        });
    }

    public /* synthetic */ void lambda$deleteMessagesByPush$301(final ArrayList arrayList, final long j, long j2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda119
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$deleteMessagesByPush$300(arrayList, j);
            }
        });
        getMessagesStorage().deletePushMessages(j2, arrayList);
        getMessagesStorage().updateDialogsWithDeletedMessages(j2, j, arrayList, getMessagesStorage().markMessagesAsDeleted(j2, arrayList, false, true, false), false);
    }

    public /* synthetic */ void lambda$deleteMessagesByPush$300(ArrayList arrayList, long j) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList, Long.valueOf(j), Boolean.FALSE);
        if (j == 0) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                MessageObject messageObject = this.dialogMessagesByIds.get(((Integer) arrayList.get(i)).intValue());
                if (messageObject != null) {
                    messageObject.deleted = true;
                }
            }
            return;
        }
        ArrayList<MessageObject> arrayList2 = this.dialogMessage.get(-j);
        if (arrayList2 == null) {
            return;
        }
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            MessageObject messageObject2 = arrayList2.get(i2);
            int size2 = arrayList.size();
            int i3 = 0;
            while (true) {
                if (i3 >= size2) {
                    break;
                } else if (messageObject2.getId() == ((Integer) arrayList.get(i3)).intValue()) {
                    messageObject2.deleted = true;
                    break;
                } else {
                    i3++;
                }
            }
        }
    }

    public void checkChatInviter(final long j, final boolean z) {
        final TLRPC$Chat chat = getChat(Long.valueOf(j));
        if (!ChatObject.isChannel(chat) || chat.creator || this.gettingChatInviters.indexOfKey(j) >= 0) {
            return;
        }
        this.gettingChatInviters.put(j, Boolean.TRUE);
        TLRPC$TL_channels_getParticipant tLRPC$TL_channels_getParticipant = new TLRPC$TL_channels_getParticipant();
        tLRPC$TL_channels_getParticipant.channel = getInputChannel(j);
        tLRPC$TL_channels_getParticipant.participant = getInputPeer(getUserConfig().getClientUserId());
        getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipant, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda331
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$checkChatInviter$306(chat, z, j, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$checkChatInviter$306(TLRPC$Chat tLRPC$Chat, boolean z, final long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        final ArrayList arrayList;
        final TLRPC$TL_channels_channelParticipant tLRPC$TL_channels_channelParticipant = (TLRPC$TL_channels_channelParticipant) tLObject;
        if (tLRPC$TL_channels_channelParticipant != null) {
            TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipant.participant;
            if (!(tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantSelf)) {
                return;
            }
            TLRPC$TL_channelParticipantSelf tLRPC$TL_channelParticipantSelf = (TLRPC$TL_channelParticipantSelf) tLRPC$ChannelParticipant;
            if (tLRPC$TL_channelParticipantSelf.inviter_id == getUserConfig().getClientUserId() && !tLRPC$TL_channelParticipantSelf.via_invite) {
                return;
            }
            if (tLRPC$Chat.megagroup && getMessagesStorage().isMigratedChat(tLRPC$Chat.id)) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda162
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$checkChatInviter$302(tLRPC$TL_channels_channelParticipant);
                }
            });
            getMessagesStorage().putUsersAndChats(tLRPC$TL_channels_channelParticipant.users, tLRPC$TL_channels_channelParticipant.chats, true, true);
            if (!z || Math.abs(getConnectionsManager().getCurrentTime() - tLRPC$TL_channels_channelParticipant.participant.date) >= 86400 || getMessagesStorage().hasInviteMeMessage(j)) {
                arrayList = null;
            } else {
                TLRPC$TL_messageService tLRPC$TL_messageService = new TLRPC$TL_messageService();
                tLRPC$TL_messageService.media_unread = true;
                tLRPC$TL_messageService.unread = true;
                tLRPC$TL_messageService.flags = 256;
                tLRPC$TL_messageService.post = true;
                int newMessageId = getUserConfig().getNewMessageId();
                tLRPC$TL_messageService.id = newMessageId;
                tLRPC$TL_messageService.local_id = newMessageId;
                tLRPC$TL_messageService.date = tLRPC$TL_channels_channelParticipant.participant.date;
                if (tLRPC$TL_channelParticipantSelf.inviter_id != getUserConfig().getClientUserId()) {
                    tLRPC$TL_messageService.action = new TLRPC$TL_messageActionChatAddUser();
                } else if (tLRPC$TL_channelParticipantSelf.via_invite) {
                    tLRPC$TL_messageService.action = new TLRPC$TL_messageActionChatJoinedByRequest();
                }
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_messageService.from_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = tLRPC$TL_channels_channelParticipant.participant.inviter_id;
                tLRPC$TL_messageService.action.users.add(Long.valueOf(getUserConfig().getClientUserId()));
                TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                tLRPC$TL_messageService.peer_id = tLRPC$TL_peerChannel;
                tLRPC$TL_peerChannel.channel_id = j;
                tLRPC$TL_messageService.dialog_id = -j;
                getUserConfig().saveConfig(false);
                arrayList = new ArrayList();
                ArrayList<TLRPC$Message> arrayList2 = new ArrayList<>();
                ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
                for (int i = 0; i < tLRPC$TL_channels_channelParticipant.users.size(); i++) {
                    TLRPC$User tLRPC$User = tLRPC$TL_channels_channelParticipant.users.get(i);
                    concurrentHashMap.put(Long.valueOf(tLRPC$User.id), tLRPC$User);
                }
                arrayList2.add(tLRPC$TL_messageService);
                arrayList.add(new MessageObject(this.currentAccount, (TLRPC$Message) tLRPC$TL_messageService, (AbstractMap<Long, TLRPC$User>) concurrentHashMap, true, false));
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda112
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$checkChatInviter$304(arrayList);
                    }
                });
                getMessagesStorage().putMessages(arrayList2, true, true, false, 0, false, 0);
            }
            final ArrayList arrayList3 = arrayList;
            getMessagesStorage().saveChatInviter(j, tLRPC$TL_channels_channelParticipant.participant.inviter_id);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda89
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$checkChatInviter$305(j, arrayList3, tLRPC$TL_channels_channelParticipant);
                }
            });
        }
    }

    public /* synthetic */ void lambda$checkChatInviter$302(TLRPC$TL_channels_channelParticipant tLRPC$TL_channels_channelParticipant) {
        putUsers(tLRPC$TL_channels_channelParticipant.users, false);
        putChats(tLRPC$TL_channels_channelParticipant.chats, false);
    }

    public /* synthetic */ void lambda$checkChatInviter$303(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$checkChatInviter$304(final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda110
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$checkChatInviter$303(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$checkChatInviter$305(long j, ArrayList arrayList, TLRPC$TL_channels_channelParticipant tLRPC$TL_channels_channelParticipant) {
        this.gettingChatInviters.delete(j);
        if (arrayList != null) {
            updateInterfaceWithMessages(-j, arrayList, false);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadChatInviter, Long.valueOf(j), Long.valueOf(tLRPC$TL_channels_channelParticipant.participant.inviter_id));
    }

    private int getUpdateType(TLRPC$Update tLRPC$Update) {
        if ((tLRPC$Update instanceof TLRPC$TL_updateNewMessage) || (tLRPC$Update instanceof TLRPC$TL_updateReadMessagesContents) || (tLRPC$Update instanceof TLRPC$TL_updateReadHistoryInbox) || (tLRPC$Update instanceof TLRPC$TL_updateReadHistoryOutbox) || (tLRPC$Update instanceof TLRPC$TL_updateDeleteMessages) || (tLRPC$Update instanceof TLRPC$TL_updateWebPage) || (tLRPC$Update instanceof TLRPC$TL_updateEditMessage) || (tLRPC$Update instanceof TLRPC$TL_updateFolderPeers) || (tLRPC$Update instanceof TLRPC$TL_updatePinnedMessages)) {
            return 0;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateNewEncryptedMessage) {
            return 1;
        }
        return ((tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) || (tLRPC$Update instanceof TLRPC$TL_updateDeleteChannelMessages) || (tLRPC$Update instanceof TLRPC$TL_updateEditChannelMessage) || (tLRPC$Update instanceof TLRPC$TL_updateChannelWebPage) || (tLRPC$Update instanceof TLRPC$TL_updatePinnedChannelMessages)) ? 2 : 3;
    }

    private static int getUpdatePts(TLRPC$Update tLRPC$Update) {
        if (tLRPC$Update instanceof TLRPC$TL_updateDeleteMessages) {
            return ((TLRPC$TL_updateDeleteMessages) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) {
            return ((TLRPC$TL_updateNewChannelMessage) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateReadHistoryOutbox) {
            return ((TLRPC$TL_updateReadHistoryOutbox) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateNewMessage) {
            return ((TLRPC$TL_updateNewMessage) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateEditMessage) {
            return ((TLRPC$TL_updateEditMessage) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateWebPage) {
            return ((TLRPC$TL_updateWebPage) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateReadHistoryInbox) {
            return ((TLRPC$TL_updateReadHistoryInbox) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelWebPage) {
            return ((TLRPC$TL_updateChannelWebPage) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateDeleteChannelMessages) {
            return ((TLRPC$TL_updateDeleteChannelMessages) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateEditChannelMessage) {
            return ((TLRPC$TL_updateEditChannelMessage) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateReadMessagesContents) {
            return ((TLRPC$TL_updateReadMessagesContents) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelTooLong) {
            return ((TLRPC$TL_updateChannelTooLong) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateFolderPeers) {
            return ((TLRPC$TL_updateFolderPeers) tLRPC$Update).pts;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updatePinnedChannelMessages) {
            return ((TLRPC$TL_updatePinnedChannelMessages) tLRPC$Update).pts;
        }
        if (!(tLRPC$Update instanceof TLRPC$TL_updatePinnedMessages)) {
            return 0;
        }
        return ((TLRPC$TL_updatePinnedMessages) tLRPC$Update).pts;
    }

    private static int getUpdatePtsCount(TLRPC$Update tLRPC$Update) {
        if (tLRPC$Update instanceof TLRPC$TL_updateDeleteMessages) {
            return ((TLRPC$TL_updateDeleteMessages) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) {
            return ((TLRPC$TL_updateNewChannelMessage) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateReadHistoryOutbox) {
            return ((TLRPC$TL_updateReadHistoryOutbox) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateNewMessage) {
            return ((TLRPC$TL_updateNewMessage) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateEditMessage) {
            return ((TLRPC$TL_updateEditMessage) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateWebPage) {
            return ((TLRPC$TL_updateWebPage) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateReadHistoryInbox) {
            return ((TLRPC$TL_updateReadHistoryInbox) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelWebPage) {
            return ((TLRPC$TL_updateChannelWebPage) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateDeleteChannelMessages) {
            return ((TLRPC$TL_updateDeleteChannelMessages) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateEditChannelMessage) {
            return ((TLRPC$TL_updateEditChannelMessage) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateReadMessagesContents) {
            return ((TLRPC$TL_updateReadMessagesContents) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateFolderPeers) {
            return ((TLRPC$TL_updateFolderPeers) tLRPC$Update).pts_count;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updatePinnedChannelMessages) {
            return ((TLRPC$TL_updatePinnedChannelMessages) tLRPC$Update).pts_count;
        }
        if (!(tLRPC$Update instanceof TLRPC$TL_updatePinnedMessages)) {
            return 0;
        }
        return ((TLRPC$TL_updatePinnedMessages) tLRPC$Update).pts_count;
    }

    private static int getUpdateQts(TLRPC$Update tLRPC$Update) {
        if (tLRPC$Update instanceof TLRPC$TL_updateNewEncryptedMessage) {
            return ((TLRPC$TL_updateNewEncryptedMessage) tLRPC$Update).qts;
        }
        return 0;
    }

    public static long getUpdateChannelId(TLRPC$Update tLRPC$Update) {
        if (tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) {
            return ((TLRPC$TL_updateNewChannelMessage) tLRPC$Update).message.peer_id.channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateEditChannelMessage) {
            return ((TLRPC$TL_updateEditChannelMessage) tLRPC$Update).message.peer_id.channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateReadChannelOutbox) {
            return ((TLRPC$TL_updateReadChannelOutbox) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelMessageViews) {
            return ((TLRPC$TL_updateChannelMessageViews) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelMessageForwards) {
            return ((TLRPC$TL_updateChannelMessageForwards) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelTooLong) {
            return ((TLRPC$TL_updateChannelTooLong) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelReadMessagesContents) {
            return ((TLRPC$TL_updateChannelReadMessagesContents) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelAvailableMessages) {
            return ((TLRPC$TL_updateChannelAvailableMessages) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannel) {
            return ((TLRPC$TL_updateChannel) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelWebPage) {
            return ((TLRPC$TL_updateChannelWebPage) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateDeleteChannelMessages) {
            return ((TLRPC$TL_updateDeleteChannelMessages) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateReadChannelInbox) {
            return ((TLRPC$TL_updateReadChannelInbox) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelPinnedTopic) {
            return ((TLRPC$TL_updateChannelPinnedTopic) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateReadChannelDiscussionInbox) {
            return ((TLRPC$TL_updateReadChannelDiscussionInbox) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateReadChannelDiscussionOutbox) {
            return ((TLRPC$TL_updateReadChannelDiscussionOutbox) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updateChannelUserTyping) {
            return ((TLRPC$TL_updateChannelUserTyping) tLRPC$Update).channel_id;
        }
        if (tLRPC$Update instanceof TLRPC$TL_updatePinnedChannelMessages) {
            return ((TLRPC$TL_updatePinnedChannelMessages) tLRPC$Update).channel_id;
        }
        if (!BuildVars.LOGS_ENABLED) {
            return 0L;
        }
        FileLog.e("trying to get unknown update channel_id for " + tLRPC$Update);
        return 0L;
    }

    /* JADX WARN: Code restructure failed: missing block: B:649:0x03ff, code lost:
        r2 = r10.pts_count + r1;
        r3 = r10.pts;
     */
    /* JADX WARN: Code restructure failed: missing block: B:650:0x0404, code lost:
        if (r2 != r3) goto L220;
     */
    /* JADX WARN: Code restructure failed: missing block: B:652:0x0418, code lost:
        if (processUpdateArray(r10.updates, r27.users, r27.chats, false, r27.date) != false) goto L219;
     */
    /* JADX WARN: Code restructure failed: missing block: B:654:0x041c, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L211;
     */
    /* JADX WARN: Code restructure failed: missing block: B:655:0x041e, code lost:
        org.telegram.messenger.FileLog.d("need get channel diff inner TL_updates, channel_id = " + r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:656:0x0432, code lost:
        if (r9 != null) goto L215;
     */
    /* JADX WARN: Code restructure failed: missing block: B:657:0x0434, code lost:
        r5 = new java.util.ArrayList();
     */
    /* JADX WARN: Code restructure failed: missing block: B:659:0x0444, code lost:
        if (r9.contains(java.lang.Long.valueOf(r4)) != false) goto L218;
     */
    /* JADX WARN: Code restructure failed: missing block: B:660:0x0446, code lost:
        r9.add(java.lang.Long.valueOf(r4));
     */
    /* JADX WARN: Code restructure failed: missing block: B:661:0x044f, code lost:
        r26.channelsPts.put(r4, r10.pts);
        getMessagesStorage().saveChannelPts(r4, r10.pts);
     */
    /* JADX WARN: Code restructure failed: missing block: B:663:0x0462, code lost:
        if (r1 == r3) goto L218;
     */
    /* JADX WARN: Code restructure failed: missing block: B:665:0x0466, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L225;
     */
    /* JADX WARN: Code restructure failed: missing block: B:666:0x0468, code lost:
        org.telegram.messenger.FileLog.d(r0 + " need get channel diff, pts: " + r1 + " " + r10.pts + " count = " + r10.pts_count + " channelId = " + r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:667:0x0497, code lost:
        r0 = r26.updatesStartWaitTimeChannels.get(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:668:0x04ab, code lost:
        if (r26.gettingDifferenceChannels.get(r4, java.lang.Boolean.FALSE).booleanValue() != false) goto L236;
     */
    /* JADX WARN: Code restructure failed: missing block: B:670:0x04b1, code lost:
        if (r0 == 0) goto L236;
     */
    /* JADX WARN: Code restructure failed: missing block: B:672:0x04c0, code lost:
        if (java.lang.Math.abs(java.lang.System.currentTimeMillis() - r0) > 1500) goto L231;
     */
    /* JADX WARN: Code restructure failed: missing block: B:674:0x04c3, code lost:
        if (r9 != null) goto L233;
     */
    /* JADX WARN: Code restructure failed: missing block: B:675:0x04c5, code lost:
        r5 = new java.util.ArrayList();
     */
    /* JADX WARN: Code restructure failed: missing block: B:677:0x04d3, code lost:
        if (r9.contains(java.lang.Long.valueOf(r4)) != false) goto L218;
     */
    /* JADX WARN: Code restructure failed: missing block: B:678:0x04d5, code lost:
        r9.add(java.lang.Long.valueOf(r4));
     */
    /* JADX WARN: Code restructure failed: missing block: B:680:0x04e1, code lost:
        if (r0 != 0) goto L239;
     */
    /* JADX WARN: Code restructure failed: missing block: B:681:0x04e3, code lost:
        r26.updatesStartWaitTimeChannels.put(r4, java.lang.System.currentTimeMillis());
     */
    /* JADX WARN: Code restructure failed: missing block: B:683:0x04ee, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L242;
     */
    /* JADX WARN: Code restructure failed: missing block: B:684:0x04f0, code lost:
        org.telegram.messenger.FileLog.d(r23);
     */
    /* JADX WARN: Code restructure failed: missing block: B:685:0x04f3, code lost:
        r0 = r26.updatesQueueChannels.get(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:686:0x04fb, code lost:
        if (r0 != null) goto L245;
     */
    /* JADX WARN: Code restructure failed: missing block: B:687:0x04fd, code lost:
        r0 = new java.util.ArrayList<>();
        r26.updatesQueueChannels.put(r4, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:688:0x0507, code lost:
        r0.add(r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:701:0x0552, code lost:
        if (getMessagesStorage().getLastSeqValue() != r27.seq_start) goto L263;
     */
    /* JADX WARN: Code restructure failed: missing block: B:703:0x0555, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:709:0x0571, code lost:
        if (r2 != getMessagesStorage().getLastSeqValue()) goto L263;
     */
    /* JADX WARN: Code restructure failed: missing block: B:788:0x071c, code lost:
        if (r2 != null) goto L334;
     */
    /* JADX WARN: Code restructure failed: missing block: B:790:0x0720, code lost:
        if (r9 != null) goto L336;
     */
    /* JADX WARN: Code restructure failed: missing block: B:803:0x074f, code lost:
        if (r2 != null) goto L450;
     */
    /* JADX WARN: Code restructure failed: missing block: B:805:0x0753, code lost:
        if (r16 != null) goto L336;
     */
    /* JADX WARN: Removed duplicated region for block: B:547:0x0117  */
    /* JADX WARN: Removed duplicated region for block: B:739:0x0630  */
    /* JADX WARN: Removed duplicated region for block: B:778:0x06f6  */
    /* JADX WARN: Removed duplicated region for block: B:782:0x0710  */
    /* JADX WARN: Removed duplicated region for block: B:785:0x0716  */
    /* JADX WARN: Removed duplicated region for block: B:795:0x0728  */
    /* JADX WARN: Removed duplicated region for block: B:813:0x0769  */
    /* JADX WARN: Removed duplicated region for block: B:841:0x07db  */
    /* JADX WARN: Removed duplicated region for block: B:907:0x09c6  */
    /* JADX WARN: Removed duplicated region for block: B:925:0x0a00  */
    /* JADX WARN: Removed duplicated region for block: B:927:0x0a1a  */
    /* JADX WARN: Removed duplicated region for block: B:959:0x07a1 A[EDGE_INSN: B:959:0x07a1->B:828:0x07a1 ?: BREAK  , SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void processUpdates(final org.telegram.tgnet.TLRPC$Updates r27, boolean r28) {
        /*
            Method dump skipped, instructions count: 2634
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdates(org.telegram.tgnet.TLRPC$Updates, boolean):void");
    }

    public /* synthetic */ void lambda$processUpdates$307(boolean z, long j, ArrayList arrayList) {
        if (z) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_USER_PRINT));
        }
        updateInterfaceWithMessages(j, arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdates$308(boolean z, TLRPC$Updates tLRPC$Updates, ArrayList arrayList) {
        if (z) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_USER_PRINT));
        }
        updateInterfaceWithMessages(-tLRPC$Updates.chat_id, arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdates$309(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$processUpdates$310(final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda109
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processUpdates$309(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$processUpdates$312() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_STATUS));
    }

    private boolean applyFoldersUpdates(ArrayList<TLRPC$TL_updateFolderPeers> arrayList) {
        if (arrayList == null) {
            return false;
        }
        int size = arrayList.size();
        int i = 0;
        boolean z = false;
        while (i < size) {
            TLRPC$TL_updateFolderPeers tLRPC$TL_updateFolderPeers = arrayList.get(i);
            int size2 = tLRPC$TL_updateFolderPeers.folder_peers.size();
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC$TL_folderPeer tLRPC$TL_folderPeer = tLRPC$TL_updateFolderPeers.folder_peers.get(i2);
                TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(DialogObject.getPeerDialogId(tLRPC$TL_folderPeer.peer));
                if (tLRPC$Dialog != null) {
                    int i3 = tLRPC$Dialog.folder_id;
                    int i4 = tLRPC$TL_folderPeer.folder_id;
                    if (i3 != i4) {
                        tLRPC$Dialog.pinned = false;
                        tLRPC$Dialog.pinnedNum = 0;
                        tLRPC$Dialog.folder_id = i4;
                        ensureFolderDialogExists(i4, null);
                    }
                }
            }
            getMessagesStorage().setDialogsFolderId(arrayList.get(i).folder_peers, null, 0L, 0);
            i++;
            z = true;
        }
        return z;
    }

    /* JADX WARN: Code restructure failed: missing block: B:2165:0x137f, code lost:
        if (r0.action.user_id == r31) goto L912;
     */
    /* JADX WARN: Removed duplicated region for block: B:2030:0x1113  */
    /* JADX WARN: Removed duplicated region for block: B:2070:0x1214  */
    /* JADX WARN: Removed duplicated region for block: B:2080:0x1240  */
    /* JADX WARN: Removed duplicated region for block: B:2082:0x1243  */
    /* JADX WARN: Removed duplicated region for block: B:2118:0x12b8  */
    /* JADX WARN: Removed duplicated region for block: B:2155:0x134d  */
    /* JADX WARN: Removed duplicated region for block: B:2198:0x1401  */
    /* JADX WARN: Removed duplicated region for block: B:2199:0x1407  */
    /* JADX WARN: Removed duplicated region for block: B:2203:0x1412  */
    /* JADX WARN: Removed duplicated region for block: B:2214:0x1467  */
    /* JADX WARN: Removed duplicated region for block: B:2237:0x14c9  */
    /* JADX WARN: Removed duplicated region for block: B:2244:0x14fc  */
    /* JADX WARN: Removed duplicated region for block: B:2246:0x1501  */
    /* JADX WARN: Removed duplicated region for block: B:2250:0x150a  */
    /* JADX WARN: Removed duplicated region for block: B:2253:0x151a  */
    /* JADX WARN: Removed duplicated region for block: B:2256:0x152d  */
    /* JADX WARN: Removed duplicated region for block: B:2266:0x1547  */
    /* JADX WARN: Removed duplicated region for block: B:2267:0x154d  */
    /* JADX WARN: Removed duplicated region for block: B:2274:0x1573  */
    /* JADX WARN: Removed duplicated region for block: B:2280:0x1588  */
    /* JADX WARN: Removed duplicated region for block: B:2515:0x1b0c  */
    /* JADX WARN: Removed duplicated region for block: B:2521:0x1b3f  */
    /* JADX WARN: Removed duplicated region for block: B:2523:0x1b4f  */
    /* JADX WARN: Removed duplicated region for block: B:2528:0x1b76  */
    /* JADX WARN: Removed duplicated region for block: B:2533:0x1ba2  */
    /* JADX WARN: Removed duplicated region for block: B:2538:0x1bc9  */
    /* JADX WARN: Removed duplicated region for block: B:2543:0x1bef  */
    /* JADX WARN: Removed duplicated region for block: B:2572:0x1342 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean processUpdateArray(java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r80, final java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r81, final java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r82, boolean r83, final int r84) {
        /*
            Method dump skipped, instructions count: 7189
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdateArray(java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, boolean, int):boolean");
    }

    public /* synthetic */ void lambda$processUpdateArray$313(ArrayList arrayList, ArrayList arrayList2) {
        putUsers(arrayList, false);
        putChats(arrayList2, false);
    }

    public /* synthetic */ void lambda$processUpdateArray$314(ArrayList arrayList, ArrayList arrayList2) {
        putUsers(arrayList, false);
        putChats(arrayList2, false);
    }

    public /* synthetic */ void lambda$processUpdateArray$315(TLRPC$TL_updateUserTyping tLRPC$TL_updateUserTyping) {
        getNotificationCenter().postNotificationName(NotificationCenter.onEmojiInteractionsReceived, Long.valueOf(tLRPC$TL_updateUserTyping.user_id), tLRPC$TL_updateUserTyping.action);
    }

    public /* synthetic */ void lambda$processUpdateArray$316(TLRPC$TL_updateChatUserTyping tLRPC$TL_updateChatUserTyping) {
        getNotificationCenter().postNotificationName(NotificationCenter.onEmojiInteractionsReceived, Long.valueOf(-tLRPC$TL_updateChatUserTyping.chat_id), tLRPC$TL_updateChatUserTyping.action);
    }

    public /* synthetic */ void lambda$processUpdateArray$318(final TLRPC$TL_updatePeerBlocked tLRPC$TL_updatePeerBlocked) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda184
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processUpdateArray$317(tLRPC$TL_updatePeerBlocked);
            }
        });
    }

    public /* synthetic */ void lambda$processUpdateArray$317(TLRPC$TL_updatePeerBlocked tLRPC$TL_updatePeerBlocked) {
        long peerId = MessageObject.getPeerId(tLRPC$TL_updatePeerBlocked.peer_id);
        if (tLRPC$TL_updatePeerBlocked.blocked) {
            if (this.blockePeers.indexOfKey(peerId) < 0) {
                this.blockePeers.put(peerId, 1);
            }
        } else {
            this.blockePeers.delete(peerId);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdateArray$319(TLRPC$TL_updateServiceNotification tLRPC$TL_updateServiceNotification) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, 2, tLRPC$TL_updateServiceNotification.message, tLRPC$TL_updateServiceNotification.type);
    }

    public /* synthetic */ void lambda$processUpdateArray$320(TLRPC$Message tLRPC$Message) {
        getSendMessagesHelper().onMessageEdited(tLRPC$Message);
    }

    public /* synthetic */ void lambda$processUpdateArray$321(TLRPC$TL_updateLangPack tLRPC$TL_updateLangPack) {
        LocaleController.getInstance().saveRemoteLocaleStringsForCurrentLocale(tLRPC$TL_updateLangPack.difference, this.currentAccount);
    }

    public /* synthetic */ void lambda$processUpdateArray$322(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public /* synthetic */ void lambda$processUpdateArray$323(final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda108
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processUpdateArray$322(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$processUpdateArray$325(final LongSparseArray longSparseArray) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda98
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processUpdateArray$324(longSparseArray);
            }
        });
    }

    public /* synthetic */ void lambda$processUpdateArray$324(LongSparseArray longSparseArray) {
        getNotificationsController().processEditedMessages(longSparseArray);
        getTopicsController().processEditedMessages(longSparseArray);
    }

    public /* synthetic */ void lambda$processUpdateArray$326(TLRPC$User tLRPC$User) {
        getContactsController().addContactToPhoneBook(tLRPC$User, true);
    }

    public /* synthetic */ void lambda$processUpdateArray$327() {
        getNotificationsController().deleteNotificationChannelGlobal(0);
    }

    public /* synthetic */ void lambda$processUpdateArray$328() {
        getNotificationsController().deleteNotificationChannelGlobal(1);
    }

    public /* synthetic */ void lambda$processUpdateArray$329() {
        getNotificationsController().deleteNotificationChannelGlobal(2);
    }

    public /* synthetic */ void lambda$processUpdateArray$330(TLRPC$TL_updateChannel tLRPC$TL_updateChannel) {
        getChannelDifference(tLRPC$TL_updateChannel.channel_id, 1, 0L, null);
    }

    public /* synthetic */ void lambda$processUpdateArray$331(TLRPC$Chat tLRPC$Chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.channelRightsUpdated, tLRPC$Chat);
    }

    public /* synthetic */ void lambda$processUpdateArray$332(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:1494:0x0var_  */
    /* JADX WARN: Removed duplicated region for block: B:1499:0x0var_  */
    /* JADX WARN: Removed duplicated region for block: B:1543:0x1077  */
    /* JADX WARN: Removed duplicated region for block: B:1545:0x1085  */
    /* JADX WARN: Removed duplicated region for block: B:1547:0x108b  */
    /* JADX WARN: Removed duplicated region for block: B:1550:0x1097  */
    /* JADX WARN: Removed duplicated region for block: B:1560:0x10d3  */
    /* JADX WARN: Removed duplicated region for block: B:1563:0x10e9  */
    /* JADX WARN: Removed duplicated region for block: B:1649:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processUpdateArray$333(int r46, java.util.ArrayList r47, androidx.collection.LongSparseArray r48, int r49, org.telegram.messenger.support.LongSparseIntArray r50, androidx.collection.LongSparseArray r51, androidx.collection.LongSparseArray r52, java.util.ArrayList r53, androidx.collection.LongSparseArray r54, androidx.collection.LongSparseArray r55, boolean r56, java.util.ArrayList r57, java.util.ArrayList r58, androidx.collection.LongSparseArray r59, androidx.collection.LongSparseArray r60, androidx.collection.LongSparseArray r61, java.util.ArrayList r62) {
        /*
            Method dump skipped, instructions count: 4337
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processUpdateArray$333(int, java.util.ArrayList, androidx.collection.LongSparseArray, int, org.telegram.messenger.support.LongSparseIntArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, java.util.ArrayList, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, boolean, java.util.ArrayList, java.util.ArrayList, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, java.util.ArrayList):void");
    }

    public /* synthetic */ void lambda$processUpdateArray$335(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2, final SparseIntArray sparseIntArray, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2, final LongSparseArray longSparseArray3, final LongSparseIntArray longSparseIntArray3) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda139
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$processUpdateArray$334(longSparseIntArray, longSparseIntArray2, sparseIntArray, longSparseArray, longSparseArray2, longSparseArray3, longSparseIntArray3);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:315:0x02be, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(getChat(java.lang.Long.valueOf(r3))) != false) goto L91;
     */
    /* JADX WARN: Type inference failed for: r14v10 */
    /* JADX WARN: Type inference failed for: r14v8 */
    /* JADX WARN: Type inference failed for: r14v9, types: [boolean] */
    /* JADX WARN: Type inference failed for: r15v6 */
    /* JADX WARN: Type inference failed for: r15v8 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processUpdateArray$334(org.telegram.messenger.support.LongSparseIntArray r19, org.telegram.messenger.support.LongSparseIntArray r20, android.util.SparseIntArray r21, androidx.collection.LongSparseArray r22, androidx.collection.LongSparseArray r23, androidx.collection.LongSparseArray r24, org.telegram.messenger.support.LongSparseIntArray r25) {
        /*
            Method dump skipped, instructions count: 835
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processUpdateArray$334(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, android.util.SparseIntArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, org.telegram.messenger.support.LongSparseIntArray):void");
    }

    public /* synthetic */ void lambda$processUpdateArray$336(long j, ArrayList arrayList) {
        getMessagesStorage().updateDialogsWithDeletedMessages(j, -j, arrayList, getMessagesStorage().markMessagesAsDeleted(j, arrayList, false, true, false), false);
    }

    public /* synthetic */ void lambda$processUpdateArray$337(long j, int i) {
        getMessagesStorage().updateDialogsWithDeletedMessages(j, -j, new ArrayList<>(), getMessagesStorage().markMessagesAsDeleted(j, i, false, true), false);
    }

    public void checkUnreadReactions(final long j, final int i, final SparseBooleanArray sparseBooleanArray) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda96
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$checkUnreadReactions$343(sparseBooleanArray, i, j);
            }
        });
    }

    public /* synthetic */ void lambda$checkUnreadReactions$343(SparseBooleanArray sparseBooleanArray, final int i, final long j) {
        int i2;
        SQLiteCursor queryFinalized;
        final ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        for (int i3 = 0; i3 < sparseBooleanArray.size(); i3++) {
            int keyAt = sparseBooleanArray.keyAt(i3);
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(keyAt);
        }
        SparseBooleanArray sparseBooleanArray2 = new SparseBooleanArray();
        try {
            if (i != 0) {
                queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT message_id, state FROM reaction_mentions WHERE message_id IN (%s) AND dialog_id = %d", sb, Long.valueOf(j)), new Object[0]);
            } else {
                queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT message_id, state FROM reaction_mentions_topics WHERE message_id IN (%s) AND dialog_id = %d AND topic_id = %d", sb, Long.valueOf(j), Integer.valueOf(i)), new Object[0]);
            }
            while (queryFinalized.next()) {
                sparseBooleanArray2.put(queryFinalized.intValue(0), queryFinalized.intValue(1) == 1);
            }
            queryFinalized.dispose();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        int i4 = 0;
        int i5 = 0;
        boolean z = false;
        boolean z2 = false;
        while (i5 < sparseBooleanArray.size()) {
            int keyAt2 = sparseBooleanArray.keyAt(i5);
            boolean valueAt = sparseBooleanArray.valueAt(i5);
            if (sparseBooleanArray2.indexOfKey(keyAt2) < 0) {
                i2 = i4;
                z = true;
            } else if (sparseBooleanArray2.get(keyAt2) != valueAt) {
                i2 = i4 + (valueAt ? 1 : -1);
                z2 = true;
            } else {
                i2 = i4;
            }
            if (valueAt) {
                arrayList.add(Integer.valueOf(keyAt2));
            }
            if (i == 0) {
                try {
                    try {
                        SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO reaction_mentions VALUES(?, ?, ?)");
                        executeFast.requery();
                        executeFast.bindInteger(1, keyAt2);
                        executeFast.bindInteger(2, valueAt ? 1 : 0);
                        try {
                            executeFast.bindLong(3, j);
                            executeFast.step();
                            executeFast.dispose();
                        } catch (SQLiteException e2) {
                            e = e2;
                            e.printStackTrace();
                            i5++;
                            i4 = i2;
                        }
                    } catch (SQLiteException e3) {
                        e = e3;
                    }
                } catch (SQLiteException e4) {
                    e = e4;
                }
            } else {
                SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO reaction_mentions_topics VALUES(?, ?, ?, ?)");
                executeFast2.requery();
                executeFast2.bindInteger(1, keyAt2);
                executeFast2.bindInteger(2, valueAt ? 1 : 0);
                try {
                    executeFast2.bindLong(3, j);
                    executeFast2.bindInteger(4, i);
                    executeFast2.step();
                    executeFast2.dispose();
                } catch (SQLiteException e5) {
                    e = e5;
                    e.printStackTrace();
                    i5++;
                    i4 = i2;
                }
            }
            i5++;
            i4 = i2;
        }
        if (!z) {
            if (!z2) {
                return;
            }
            final int i6 = i4;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda48
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$checkUnreadReactions$342(i, j, i6);
                }
            });
        } else if (i == 0) {
            TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs = new TLRPC$TL_messages_getPeerDialogs();
            TLRPC$TL_inputDialogPeer tLRPC$TL_inputDialogPeer = new TLRPC$TL_inputDialogPeer();
            tLRPC$TL_inputDialogPeer.peer = getInputPeer(j);
            tLRPC$TL_messages_getPeerDialogs.peers.add(tLRPC$TL_inputDialogPeer);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getPeerDialogs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda301
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$checkUnreadReactions$339(j, i, arrayList, tLObject, tLRPC$TL_error);
                }
            });
        } else {
            TLRPC$TL_channels_getForumTopicsByID tLRPC$TL_channels_getForumTopicsByID = new TLRPC$TL_channels_getForumTopicsByID();
            tLRPC$TL_channels_getForumTopicsByID.topics.add(Integer.valueOf(i));
            tLRPC$TL_channels_getForumTopicsByID.channel = getMessagesController().getInputChannel(-j);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getForumTopicsByID, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda302
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$checkUnreadReactions$341(j, i, arrayList, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$checkUnreadReactions$339(final long j, final int i, final ArrayList arrayList, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs = (TLRPC$TL_messages_peerDialogs) tLObject;
            final int i2 = tLRPC$TL_messages_peerDialogs.dialogs.size() == 0 ? 0 : tLRPC$TL_messages_peerDialogs.dialogs.get(0).unread_reactions_count;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda73
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$checkUnreadReactions$338(j, i2, i, arrayList);
                }
            });
        }
    }

    public /* synthetic */ void lambda$checkUnreadReactions$338(long j, int i, int i2, ArrayList arrayList) {
        TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
        if (tLRPC$Dialog == null) {
            getMessagesStorage().updateDialogUnreadReactions(j, 0, i, false);
            return;
        }
        tLRPC$Dialog.unread_reactions_count = i;
        getMessagesStorage().updateUnreadReactionsCount(j, i2, i);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadReactionsCounterChanged, Long.valueOf(j), Integer.valueOf(i2), Integer.valueOf(i), arrayList);
    }

    public /* synthetic */ void lambda$checkUnreadReactions$341(final long j, final int i, final ArrayList arrayList, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_messages_forumTopics tLRPC$TL_messages_forumTopics = (TLRPC$TL_messages_forumTopics) tLObject;
            final int i2 = tLRPC$TL_messages_forumTopics.topics.size() == 0 ? 0 : tLRPC$TL_messages_forumTopics.topics.get(0).unread_reactions_count;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda72
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$checkUnreadReactions$340(j, i, i2, arrayList);
                }
            });
        }
    }

    public /* synthetic */ void lambda$checkUnreadReactions$340(long j, int i, int i2, ArrayList arrayList) {
        getMessagesController().getTopicsController().updateReactionsUnread(j, i, i2, false);
        getMessagesStorage().updateUnreadReactionsCount(j, i, i2);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadReactionsCounterChanged, Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), arrayList);
    }

    public /* synthetic */ void lambda$checkUnreadReactions$342(int i, long j, int i2) {
        if (i == 0) {
            TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
            if (tLRPC$Dialog == null) {
                getMessagesStorage().updateDialogUnreadReactions(j, 0, i2, true);
                return;
            }
            int i3 = tLRPC$Dialog.unread_reactions_count + i2;
            tLRPC$Dialog.unread_reactions_count = i3;
            if (i3 < 0) {
                tLRPC$Dialog.unread_reactions_count = 0;
            }
            getMessagesStorage().updateUnreadReactionsCount(j, 0, tLRPC$Dialog.unread_reactions_count);
            return;
        }
        int updateReactionsUnread = getMessagesController().getTopicsController().updateReactionsUnread(j, i, i2, true);
        if (updateReactionsUnread < 0) {
            return;
        }
        getMessagesStorage().updateUnreadReactionsCount(j, i, updateReactionsUnread, true);
    }

    public boolean isDialogMuted(long j, int i) {
        return isDialogMuted(j, i, null);
    }

    public boolean isDialogNotificationsSoundEnabled(long j, int i) {
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        return sharedPreferences.getBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(j, i), true);
    }

    public boolean isDialogMuted(long j, int i, TLRPC$Chat tLRPC$Chat) {
        Boolean bool;
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        int i2 = sharedPreferences.getInt("notify2_" + NotificationsController.getSharedPrefKey(j, i), -1);
        if (i2 == -1) {
            if (tLRPC$Chat != null) {
                bool = Boolean.valueOf(ChatObject.isChannel(tLRPC$Chat) && !tLRPC$Chat.megagroup);
            } else {
                bool = null;
            }
            if (i != 0) {
                return isDialogMuted(j, 0, tLRPC$Chat);
            }
            return !getNotificationsController().isGlobalNotificationsEnabled(j, bool);
        } else if (i2 == 2) {
            return true;
        } else {
            if (i2 == 3) {
                SharedPreferences sharedPreferences2 = this.notificationsPreferences;
                if (sharedPreferences2.getInt("notifyuntil_" + NotificationsController.getSharedPrefKey(j, i), 0) >= getConnectionsManager().getCurrentTime()) {
                    return true;
                }
            }
            return false;
        }
    }

    public void markReactionsAsRead(long j, int i) {
        if (i == 0) {
            TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
            if (tLRPC$Dialog != null) {
                tLRPC$Dialog.unread_reactions_count = 0;
            }
        } else {
            this.topicsController.markAllReactionsAsRead(-j, i);
        }
        getMessagesStorage().updateUnreadReactionsCount(j, i, 0);
        TLRPC$TL_messages_readReactions tLRPC$TL_messages_readReactions = new TLRPC$TL_messages_readReactions();
        tLRPC$TL_messages_readReactions.peer = getInputPeer(j);
        if (i != 0) {
            tLRPC$TL_messages_readReactions.top_msg_id = i;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_readReactions, MessagesController$$ExternalSyntheticLambda360.INSTANCE);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_REACTIONS_READ));
    }

    public SponsoredMessagesInfo getSponsoredMessages(final long j) {
        SponsoredMessagesInfo sponsoredMessagesInfo = this.sponsoredMessages.get(j);
        if (sponsoredMessagesInfo == null || (!sponsoredMessagesInfo.loading && Math.abs(SystemClock.elapsedRealtime() - sponsoredMessagesInfo.loadTime) > 300000)) {
            TLRPC$Chat chat = getChat(Long.valueOf(-j));
            if (!ChatObject.isChannel(chat)) {
                return null;
            }
            final SponsoredMessagesInfo sponsoredMessagesInfo2 = new SponsoredMessagesInfo();
            sponsoredMessagesInfo2.loading = true;
            this.sponsoredMessages.put(j, sponsoredMessagesInfo2);
            TLRPC$TL_channels_getSponsoredMessages tLRPC$TL_channels_getSponsoredMessages = new TLRPC$TL_channels_getSponsoredMessages();
            tLRPC$TL_channels_getSponsoredMessages.channel = getInputChannel(chat);
            getConnectionsManager().sendRequest(tLRPC$TL_channels_getSponsoredMessages, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda312
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$getSponsoredMessages$347(j, sponsoredMessagesInfo2, tLObject, tLRPC$TL_error);
                }
            });
            return null;
        }
        return sponsoredMessagesInfo;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v9, types: [java.lang.Integer] */
    /* JADX WARN: Type inference failed for: r6v0 */
    /* JADX WARN: Type inference failed for: r6v1, types: [java.lang.Integer] */
    /* JADX WARN: Type inference failed for: r6v12 */
    public /* synthetic */ void lambda$getSponsoredMessages$347(final long j, final SponsoredMessagesInfo sponsoredMessagesInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        final ?? r6;
        ArrayList arrayList;
        final ArrayList arrayList2 = null;
        if (tLObject instanceof TLRPC$messages_SponsoredMessages) {
            final TLRPC$messages_SponsoredMessages tLRPC$messages_SponsoredMessages = (TLRPC$messages_SponsoredMessages) tLObject;
            if (tLRPC$messages_SponsoredMessages.messages.isEmpty()) {
                arrayList = null;
            } else {
                if ((tLRPC$messages_SponsoredMessages instanceof TLRPC$TL_messages_sponsoredMessages) && (tLRPC$messages_SponsoredMessages.flags & 1) > 0) {
                    arrayList2 = Integer.valueOf(tLRPC$messages_SponsoredMessages.posts_between);
                }
                ArrayList arrayList3 = new ArrayList();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda201
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$getSponsoredMessages$345(tLRPC$messages_SponsoredMessages);
                    }
                });
                LongSparseArray longSparseArray = new LongSparseArray();
                LongSparseArray longSparseArray2 = new LongSparseArray();
                int i = 0;
                for (int i2 = 0; i2 < tLRPC$messages_SponsoredMessages.users.size(); i2++) {
                    TLRPC$User tLRPC$User = tLRPC$messages_SponsoredMessages.users.get(i2);
                    longSparseArray.put(tLRPC$User.id, tLRPC$User);
                }
                for (int i3 = 0; i3 < tLRPC$messages_SponsoredMessages.chats.size(); i3++) {
                    TLRPC$Chat tLRPC$Chat = tLRPC$messages_SponsoredMessages.chats.get(i3);
                    longSparseArray2.put(tLRPC$Chat.id, tLRPC$Chat);
                }
                int i4 = -10000000;
                int size = tLRPC$messages_SponsoredMessages.messages.size();
                while (i < size) {
                    TLRPC$TL_sponsoredMessage tLRPC$TL_sponsoredMessage = tLRPC$messages_SponsoredMessages.messages.get(i);
                    TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                    tLRPC$TL_message.message = tLRPC$TL_sponsoredMessage.message;
                    if (!tLRPC$TL_sponsoredMessage.entities.isEmpty()) {
                        tLRPC$TL_message.entities = tLRPC$TL_sponsoredMessage.entities;
                        tLRPC$TL_message.flags |= 128;
                    }
                    tLRPC$TL_message.peer_id = getPeer(j);
                    tLRPC$TL_message.from_id = tLRPC$TL_sponsoredMessage.from_id;
                    tLRPC$TL_message.flags |= 256;
                    tLRPC$TL_message.date = getConnectionsManager().getCurrentTime();
                    int i5 = i4 - 1;
                    tLRPC$TL_message.id = i4;
                    MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC$Message) tLRPC$TL_message, (LongSparseArray<TLRPC$User>) longSparseArray, (LongSparseArray<TLRPC$Chat>) longSparseArray2, true, true);
                    messageObject.sponsoredId = tLRPC$TL_sponsoredMessage.random_id;
                    messageObject.botStartParam = tLRPC$TL_sponsoredMessage.start_param;
                    messageObject.sponsoredChannelPost = tLRPC$TL_sponsoredMessage.channel_post;
                    messageObject.sponsoredChatInvite = tLRPC$TL_sponsoredMessage.chat_invite;
                    messageObject.sponsoredChatInviteHash = tLRPC$TL_sponsoredMessage.chat_invite_hash;
                    messageObject.sponsoredRecommended = tLRPC$TL_sponsoredMessage.recommended;
                    messageObject.sponsoredShowPeerPhoto = tLRPC$TL_sponsoredMessage.show_peer_photo;
                    arrayList3.add(messageObject);
                    i++;
                    tLRPC$messages_SponsoredMessages = tLRPC$messages_SponsoredMessages;
                    i4 = i5;
                }
                arrayList = arrayList2;
                arrayList2 = arrayList3;
            }
            r6 = arrayList;
        } else {
            r6 = 0;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda122
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getSponsoredMessages$346(arrayList2, j, sponsoredMessagesInfo, r6);
            }
        });
    }

    public /* synthetic */ void lambda$getSponsoredMessages$345(TLRPC$messages_SponsoredMessages tLRPC$messages_SponsoredMessages) {
        putUsers(tLRPC$messages_SponsoredMessages.users, false);
        putChats(tLRPC$messages_SponsoredMessages.chats, false);
    }

    public /* synthetic */ void lambda$getSponsoredMessages$346(ArrayList arrayList, long j, SponsoredMessagesInfo sponsoredMessagesInfo, Integer num) {
        if (arrayList == null) {
            this.sponsoredMessages.remove(j);
            return;
        }
        sponsoredMessagesInfo.loadTime = SystemClock.elapsedRealtime();
        sponsoredMessagesInfo.messages = arrayList;
        sponsoredMessagesInfo.posts_between = num;
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadSponsoredMessages, Long.valueOf(j), arrayList);
    }

    public void clearSendAsPeers() {
        this.sendAsPeers.clear();
    }

    public TLRPC$TL_channels_sendAsPeers getSendAsPeers(final long j) {
        SendAsPeersInfo sendAsPeersInfo = this.sendAsPeers.get(j);
        if (sendAsPeersInfo != null && (sendAsPeersInfo.loading || Math.abs(SystemClock.elapsedRealtime() - sendAsPeersInfo.loadTime) <= 300000)) {
            return sendAsPeersInfo.sendAsPeers;
        }
        TLRPC$Chat chat = getChat(Long.valueOf(-j));
        if (chat != null && ChatObject.canSendAsPeers(chat)) {
            final SendAsPeersInfo sendAsPeersInfo2 = new SendAsPeersInfo();
            sendAsPeersInfo2.loading = true;
            this.sendAsPeers.put(j, sendAsPeersInfo2);
            TLRPC$TL_channels_getSendAs tLRPC$TL_channels_getSendAs = new TLRPC$TL_channels_getSendAs();
            tLRPC$TL_channels_getSendAs.peer = getInputPeer(j);
            getConnectionsManager().sendRequest(tLRPC$TL_channels_getSendAs, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda311
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$getSendAsPeers$350(j, sendAsPeersInfo2, tLObject, tLRPC$TL_error);
                }
            });
        }
        return null;
    }

    public /* synthetic */ void lambda$getSendAsPeers$350(final long j, final SendAsPeersInfo sendAsPeersInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$TL_channels_sendAsPeers tLRPC$TL_channels_sendAsPeers = null;
        if (tLObject != null) {
            final TLRPC$TL_channels_sendAsPeers tLRPC$TL_channels_sendAsPeers2 = (TLRPC$TL_channels_sendAsPeers) tLObject;
            if (!tLRPC$TL_channels_sendAsPeers2.peers.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda164
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.lambda$getSendAsPeers$348(tLRPC$TL_channels_sendAsPeers2);
                    }
                });
                LongSparseArray longSparseArray = new LongSparseArray();
                LongSparseArray longSparseArray2 = new LongSparseArray();
                for (int i = 0; i < tLRPC$TL_channels_sendAsPeers2.users.size(); i++) {
                    TLRPC$User tLRPC$User = tLRPC$TL_channels_sendAsPeers2.users.get(i);
                    longSparseArray.put(tLRPC$User.id, tLRPC$User);
                }
                for (int i2 = 0; i2 < tLRPC$TL_channels_sendAsPeers2.chats.size(); i2++) {
                    TLRPC$Chat tLRPC$Chat = tLRPC$TL_channels_sendAsPeers2.chats.get(i2);
                    longSparseArray2.put(tLRPC$Chat.id, tLRPC$Chat);
                }
                tLRPC$TL_channels_sendAsPeers = tLRPC$TL_channels_sendAsPeers2;
            }
        }
        final TLRPC$TL_channels_sendAsPeers tLRPC$TL_channels_sendAsPeers3 = tLRPC$TL_channels_sendAsPeers;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda165
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$getSendAsPeers$349(tLRPC$TL_channels_sendAsPeers3, j, sendAsPeersInfo);
            }
        });
    }

    public /* synthetic */ void lambda$getSendAsPeers$348(TLRPC$TL_channels_sendAsPeers tLRPC$TL_channels_sendAsPeers) {
        putUsers(tLRPC$TL_channels_sendAsPeers.users, false);
        putChats(tLRPC$TL_channels_sendAsPeers.chats, false);
    }

    public /* synthetic */ void lambda$getSendAsPeers$349(TLRPC$TL_channels_sendAsPeers tLRPC$TL_channels_sendAsPeers, long j, SendAsPeersInfo sendAsPeersInfo) {
        if (tLRPC$TL_channels_sendAsPeers != null) {
            sendAsPeersInfo.loadTime = SystemClock.elapsedRealtime();
            sendAsPeersInfo.sendAsPeers = tLRPC$TL_channels_sendAsPeers;
            getNotificationCenter().postNotificationName(NotificationCenter.didLoadSendAsPeers, Long.valueOf(j), tLRPC$TL_channels_sendAsPeers);
            return;
        }
        this.sendAsPeers.remove(j);
    }

    public CharSequence getPrintingString(long j, int i, boolean z) {
        SparseArray<CharSequence> sparseArray;
        TLRPC$User user;
        TLRPC$UserStatus tLRPC$UserStatus;
        if ((!z || !DialogObject.isUserDialog(j) || (user = getUser(Long.valueOf(j))) == null || (tLRPC$UserStatus = user.status) == null || tLRPC$UserStatus.expires >= 0) && (sparseArray = this.printingStrings.get(j)) != null) {
            return sparseArray.get(i);
        }
        return null;
    }

    public Integer getPrintingStringType(long j, int i) {
        SparseArray<Integer> sparseArray = this.printingStringsTypes.get(j);
        if (sparseArray == null) {
            return null;
        }
        return sparseArray.get(i);
    }

    private boolean updatePrintingUsersWithNewMessages(long j, ArrayList<MessageObject> arrayList) {
        boolean z;
        if (j > 0) {
            if (this.printingUsers.get(Long.valueOf(j)) != null) {
                this.printingUsers.remove(Long.valueOf(j));
                return true;
            }
        } else if (j < 0) {
            ArrayList arrayList2 = new ArrayList();
            Iterator<MessageObject> it = arrayList.iterator();
            while (it.hasNext()) {
                MessageObject next = it.next();
                if (next.isFromUser() && !arrayList2.contains(Long.valueOf(next.messageOwner.from_id.user_id))) {
                    arrayList2.add(Long.valueOf(next.messageOwner.from_id.user_id));
                }
            }
            ConcurrentHashMap<Integer, ArrayList<PrintingUser>> concurrentHashMap = this.printingUsers.get(Long.valueOf(j));
            if (concurrentHashMap != null) {
                ArrayList arrayList3 = null;
                z = false;
                for (Map.Entry<Integer, ArrayList<PrintingUser>> entry : concurrentHashMap.entrySet()) {
                    Integer key = entry.getKey();
                    ArrayList<PrintingUser> value = entry.getValue();
                    int i = 0;
                    while (i < value.size()) {
                        if (arrayList2.contains(Long.valueOf(value.get(i).userId))) {
                            value.remove(i);
                            i--;
                            if (value.isEmpty()) {
                                if (arrayList3 == null) {
                                    arrayList3 = new ArrayList();
                                }
                                arrayList3.add(key);
                            }
                            z = true;
                        }
                        i++;
                    }
                }
                if (arrayList3 != null) {
                    int size = arrayList3.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        concurrentHashMap.remove(arrayList3.get(i2));
                    }
                    if (concurrentHashMap.isEmpty()) {
                        this.printingUsers.remove(Long.valueOf(j));
                    }
                }
            } else {
                z = false;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:442:0x042f  */
    /* JADX WARN: Removed duplicated region for block: B:444:0x0434  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean updateInterfaceWithMessages(final long r25, java.util.ArrayList<org.telegram.messenger.MessageObject> r27, boolean r28) {
        /*
            Method dump skipped, instructions count: 1088
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.updateInterfaceWithMessages(long, java.util.ArrayList, boolean):boolean");
    }

    public /* synthetic */ void lambda$updateInterfaceWithMessages$351(TLRPC$Dialog tLRPC$Dialog, int i, long j, int i2) {
        if (i2 == -1) {
            if (i <= 0 || DialogObject.isEncryptedDialog(j)) {
                return;
            }
            loadUnknownDialog(getInputPeer(j), 0L);
        } else if (i2 == 0) {
        } else {
            tLRPC$Dialog.folder_id = i2;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
        }
    }

    public void addDialogAction(long j, boolean z) {
        TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
        if (tLRPC$Dialog == null) {
            return;
        }
        if (z) {
            this.clearingHistoryDialogs.put(j, tLRPC$Dialog);
        } else {
            this.deletingDialogs.put(j, tLRPC$Dialog);
            this.allDialogs.remove(tLRPC$Dialog);
            sortDialogs(null);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public void removeDialogAction(long j, boolean z, boolean z2) {
        TLRPC$Dialog tLRPC$Dialog = this.dialogs_dict.get(j);
        if (tLRPC$Dialog == null) {
            return;
        }
        if (z) {
            this.clearingHistoryDialogs.remove(j);
        } else {
            this.deletingDialogs.remove(j);
            if (!z2) {
                this.allDialogs.add(tLRPC$Dialog);
                sortDialogs(null);
            }
        }
        if (z2) {
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public boolean isClearingDialog(long j) {
        return this.clearingHistoryDialogs.get(j) != null;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(21:1|(2:2|(3:4|(2:6|7)(1:9)|8)(1:10))|11|(4:14|(2:16|17)(1:19)|18|12)|20|21|(15:191|(2:192|(3:194|(1:214)(3:196|(3:198|(4:200|(1:204)|205|(2:207|208)(1:210))(1:211)|209)|212)|213)(0))|26|27|(2:29|(1:35))|36|(6:38|(6:40|(4:42|(4:45|(2:51|52)|53|43)|57|(2:61|62))|63|(2:65|(4:67|(1:(2:97|(1:99)(1:100))(1:96))|(3:116|(1:118)|108)(2:104|(1:106))|(2:112|(1:114)(1:115)))(2:120|(1:(3:130|(2:138|(1:140)(1:141))|144)(4:127|128|129|62))(2:145|(2:147|(1:149)))))|150|(3:110|112|(0)(0)))|151|(1:166)|155|(3:162|163|62)(4:161|128|129|62))|167|168|(1:172)|(1:176)|177|(4:180|(2:182|183)(1:185)|184|178)|186|187)(0)|25|26|27|(0)|36|(0)|167|168|(2:170|172)|(2:174|176)|177|(1:178)|186|187) */
    /* JADX WARN: Code restructure failed: missing block: B:339:0x01be, code lost:
        if (org.telegram.messenger.ChatObject.hasAdminRights(r7) == false) goto L108;
     */
    /* JADX WARN: Code restructure failed: missing block: B:345:0x01d4, code lost:
        if (org.telegram.messenger.ChatObject.canPost(r7) != false) goto L150;
     */
    /* JADX WARN: Removed duplicated region for block: B:266:0x00df  */
    /* JADX WARN: Removed duplicated region for block: B:275:0x0100  */
    /* JADX WARN: Removed duplicated region for block: B:381:0x0248  */
    /* JADX WARN: Removed duplicated region for block: B:382:0x024f  */
    /* JADX WARN: Removed duplicated region for block: B:411:0x02d5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void sortDialogs(androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Chat> r17) {
        /*
            Method dump skipped, instructions count: 754
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.sortDialogs(androidx.collection.LongSparseArray):void");
    }

    private void addDialogToItsFolder(int i, TLRPC$Dialog tLRPC$Dialog) {
        int i2 = tLRPC$Dialog instanceof TLRPC$TL_dialogFolder ? 0 : tLRPC$Dialog.folder_id;
        ArrayList<TLRPC$Dialog> arrayList = this.dialogsByFolder.get(i2);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.dialogsByFolder.put(i2, arrayList);
        }
        if (i == -1) {
            arrayList.add(tLRPC$Dialog);
        } else if (i == -2) {
            if (arrayList.isEmpty() || !(arrayList.get(0) instanceof TLRPC$TL_dialogFolder)) {
                arrayList.add(0, tLRPC$Dialog);
            } else {
                arrayList.add(1, tLRPC$Dialog);
            }
        } else {
            arrayList.add(i, tLRPC$Dialog);
        }
    }

    public static String getRestrictionReason(ArrayList<TLRPC$TL_restrictionReason> arrayList) {
        if (arrayList.isEmpty()) {
            return null;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC$TL_restrictionReason tLRPC$TL_restrictionReason = arrayList.get(i);
            if ("all".equals(tLRPC$TL_restrictionReason.platform) || (!BuildVars.isStandaloneApp() && !BuildVars.isBetaApp() && "android".equals(tLRPC$TL_restrictionReason.platform))) {
                return tLRPC$TL_restrictionReason.text;
            }
        }
        return null;
    }

    public static void showCantOpenAlert(BaseFragment baseFragment, String str) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        builder.setTitle(LocaleController.getString(R.string.DialogNotAvailable));
        HashMap hashMap = new HashMap();
        hashMap.put("info1.**", Integer.valueOf(baseFragment.getThemedColor("dialogTopBackground")));
        hashMap.put("info2.**", Integer.valueOf(baseFragment.getThemedColor("dialogTopBackground")));
        builder.setTopAnimation(R.raw.not_available, 52, false, baseFragment.getThemedColor("dialogTopBackground"), hashMap);
        builder.setTopAnimationIsNew(true);
        builder.setPositiveButton(LocaleController.getString(R.string.Close), null);
        builder.setMessage(str);
        baseFragment.showDialog(builder.create());
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment baseFragment) {
        return checkCanOpenChat(bundle, baseFragment, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean checkCanOpenChat(final Bundle bundle, final BaseFragment baseFragment, MessageObject messageObject) {
        TLRPC$Chat chat;
        String restrictionReason;
        TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages;
        if (bundle != null && baseFragment != null) {
            long j = bundle.getLong("user_id", 0L);
            long j2 = bundle.getLong("chat_id", 0L);
            int i = bundle.getInt("message_id", 0);
            TLRPC$User tLRPC$User = null;
            if (j != 0) {
                tLRPC$User = getUser(Long.valueOf(j));
                chat = null;
            } else {
                chat = j2 != 0 ? getChat(Long.valueOf(j2)) : null;
            }
            if (tLRPC$User == null && chat == null) {
                return true;
            }
            if (chat != null) {
                restrictionReason = getRestrictionReason(chat.restriction_reason);
            } else {
                restrictionReason = getRestrictionReason(tLRPC$User.restriction_reason);
            }
            if (restrictionReason != null) {
                showCantOpenAlert(baseFragment, restrictionReason);
                return false;
            } else if (i != 0 && messageObject != null && chat != null && chat.access_hash == 0) {
                long dialogId = messageObject.getDialogId();
                if (!DialogObject.isEncryptedDialog(dialogId)) {
                    final AlertDialog alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
                    if (dialogId < 0) {
                        chat = getChat(Long.valueOf(-dialogId));
                    }
                    if (dialogId > 0 || !ChatObject.isChannel(chat)) {
                        TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages2 = new TLRPC$TL_messages_getMessages();
                        tLRPC$TL_messages_getMessages2.id.add(Integer.valueOf(messageObject.getId()));
                        tLRPC$TL_messages_getMessages = tLRPC$TL_messages_getMessages2;
                    } else {
                        TLRPC$Chat chat2 = getChat(Long.valueOf(-dialogId));
                        TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
                        tLRPC$TL_channels_getMessages.channel = getInputChannel(chat2);
                        tLRPC$TL_channels_getMessages.id.add(Integer.valueOf(messageObject.getId()));
                        tLRPC$TL_messages_getMessages = tLRPC$TL_channels_getMessages;
                    }
                    final int sendRequest = getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda338
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MessagesController.this.lambda$checkCanOpenChat$353(alertDialog, baseFragment, bundle, tLObject, tLRPC$TL_error);
                        }
                    });
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda3
                        @Override // android.content.DialogInterface.OnCancelListener
                        public final void onCancel(DialogInterface dialogInterface) {
                            MessagesController.this.lambda$checkCanOpenChat$354(sendRequest, baseFragment, dialogInterface);
                        }
                    });
                    baseFragment.setVisibleDialog(alertDialog);
                    alertDialog.show();
                    return false;
                }
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$checkCanOpenChat$353(final AlertDialog alertDialog, final BaseFragment baseFragment, final Bundle bundle, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda209
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$checkCanOpenChat$352(alertDialog, tLObject, baseFragment, bundle);
                }
            });
        }
    }

    public /* synthetic */ void lambda$checkCanOpenChat$352(AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, Bundle bundle) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
        putUsers(tLRPC$messages_Messages.users, false);
        putChats(tLRPC$messages_Messages.chats, false);
        getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
        baseFragment.presentFragment(new ChatActivity(bundle), true);
    }

    public /* synthetic */ void lambda$checkCanOpenChat$354(int i, BaseFragment baseFragment, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
        baseFragment.setVisibleDialog(null);
    }

    public static void openChatOrProfileWith(TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, BaseFragment baseFragment, int i, boolean z) {
        String restrictionReason;
        if ((tLRPC$User == null && tLRPC$Chat == null) || baseFragment == null) {
            return;
        }
        if (tLRPC$Chat != null) {
            restrictionReason = getRestrictionReason(tLRPC$Chat.restriction_reason);
        } else {
            restrictionReason = getRestrictionReason(tLRPC$User.restriction_reason);
            if (i != 3 && tLRPC$User.bot) {
                i = 1;
                z = true;
            }
        }
        if (restrictionReason != null) {
            showCantOpenAlert(baseFragment, restrictionReason);
            return;
        }
        Bundle bundle = new Bundle();
        if (tLRPC$Chat != null) {
            bundle.putLong("chat_id", tLRPC$Chat.id);
        } else {
            bundle.putLong("user_id", tLRPC$User.id);
        }
        if (i == 0) {
            baseFragment.presentFragment(new ProfileActivity(bundle));
        } else if (i == 2) {
            if (ChatObject.isForum(tLRPC$Chat)) {
                baseFragment.presentFragment(new TopicsFragment(bundle), true, true);
            } else {
                baseFragment.presentFragment(new ChatActivity(bundle), true, true);
            }
        } else if (ChatObject.isForum(tLRPC$Chat)) {
            baseFragment.presentFragment(new TopicsFragment(bundle), z);
        } else {
            baseFragment.presentFragment(new ChatActivity(bundle), z);
        }
    }

    public void openByUserName(String str, final BaseFragment baseFragment, final int i) {
        TLRPC$Chat tLRPC$Chat;
        TLRPC$User tLRPC$User;
        if (str == null || baseFragment == null) {
            return;
        }
        TLObject userOrChat = getUserOrChat(str);
        if (userOrChat instanceof TLRPC$User) {
            tLRPC$User = (TLRPC$User) userOrChat;
            if (!tLRPC$User.min) {
                tLRPC$Chat = null;
            }
            tLRPC$User = null;
            tLRPC$Chat = null;
        } else {
            if (userOrChat instanceof TLRPC$Chat) {
                TLRPC$Chat tLRPC$Chat2 = (TLRPC$Chat) userOrChat;
                if (!tLRPC$Chat2.min) {
                    tLRPC$Chat = tLRPC$Chat2;
                    tLRPC$User = null;
                }
            }
            tLRPC$User = null;
            tLRPC$Chat = null;
        }
        if (tLRPC$User != null) {
            openChatOrProfileWith(tLRPC$User, null, baseFragment, i, false);
        } else if (tLRPC$Chat != null) {
            openChatOrProfileWith(null, tLRPC$Chat, baseFragment, 1, false);
        } else if (baseFragment.getParentActivity() == null) {
        } else {
            final AlertDialog[] alertDialogArr = {new AlertDialog(baseFragment.getParentActivity(), 3)};
            TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
            tLRPC$TL_contacts_resolveUsername.username = str;
            final int sendRequest = getConnectionsManager().sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda349
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.this.lambda$openByUserName$356(alertDialogArr, baseFragment, i, tLObject, tLRPC$TL_error);
                }
            });
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda218
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$openByUserName$358(alertDialogArr, sendRequest, baseFragment);
                }
            }, 500L);
        }
    }

    public /* synthetic */ void lambda$openByUserName$356(final AlertDialog[] alertDialogArr, final BaseFragment baseFragment, final int i, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda219
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$openByUserName$355(alertDialogArr, baseFragment, tLRPC$TL_error, tLObject, i);
            }
        });
    }

    public /* synthetic */ void lambda$openByUserName$355(AlertDialog[] alertDialogArr, BaseFragment baseFragment, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Exception unused) {
        }
        alertDialogArr[0] = null;
        baseFragment.setVisibleDialog(null);
        if (tLRPC$TL_error == null) {
            TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
            putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
            putChats(tLRPC$TL_contacts_resolvedPeer.chats, false);
            getMessagesStorage().putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, tLRPC$TL_contacts_resolvedPeer.chats, false, true);
            if (!tLRPC$TL_contacts_resolvedPeer.chats.isEmpty()) {
                openChatOrProfileWith(null, tLRPC$TL_contacts_resolvedPeer.chats.get(0), baseFragment, 1, false);
            } else if (tLRPC$TL_contacts_resolvedPeer.users.isEmpty()) {
            } else {
                openChatOrProfileWith(tLRPC$TL_contacts_resolvedPeer.users.get(0), null, baseFragment, i, false);
            }
        } else if (baseFragment.getParentActivity() == null) {
        } else {
            try {
                if (baseFragment instanceof ChatActivity) {
                    ((ChatActivity) baseFragment).shakeContent();
                }
                BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString("NoUsernameFound", R.string.NoUsernameFound)).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$openByUserName$358(AlertDialog[] alertDialogArr, final int i, BaseFragment baseFragment) {
        if (alertDialogArr[0] == null) {
            return;
        }
        alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                MessagesController.this.lambda$openByUserName$357(i, dialogInterface);
            }
        });
        baseFragment.showDialog(alertDialogArr[0]);
    }

    public /* synthetic */ void lambda$openByUserName$357(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public void ensureMessagesLoaded(final long j, int i, final MessagesLoadedCallback messagesLoadedCallback) {
        int i2;
        SharedPreferences notificationsSettings = getNotificationsSettings(this.currentAccount);
        if (i == 0) {
            i2 = notificationsSettings.getInt("diditem" + j, 0);
        } else {
            i2 = i;
        }
        final int generateClassGuid = ConnectionsManager.generateClassGuid();
        final long j2 = DialogObject.isChatDialog(j) ? -j : 0L;
        if (j2 != 0 && getMessagesController().getChat(Long.valueOf(j2)) == null) {
            final MessagesStorage messagesStorage = getMessagesStorage();
            final int i3 = i2;
            messagesStorage.getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda136
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$ensureMessagesLoaded$360(messagesStorage, j2, j, i3, messagesLoadedCallback);
                }
            });
            return;
        }
        int i4 = AndroidUtilities.isTablet() ? 30 : 20;
        final int i5 = i4;
        final int i6 = i2;
        NotificationCenter.NotificationCenterDelegate notificationCenterDelegate = new NotificationCenter.NotificationCenterDelegate() { // from class: org.telegram.messenger.MessagesController.1
            {
                MessagesController.this = this;
            }

            @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
            public void didReceivedNotification(int i7, int i8, Object... objArr) {
                int i9 = NotificationCenter.messagesDidLoadWithoutProcess;
                if (i7 == i9 && ((Integer) objArr[0]).intValue() == generateClassGuid) {
                    int intValue = ((Integer) objArr[1]).intValue();
                    boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
                    boolean booleanValue2 = ((Boolean) objArr[3]).booleanValue();
                    int intValue2 = ((Integer) objArr[4]).intValue();
                    int i10 = i5;
                    if (intValue < i10 / 2 && !booleanValue2 && booleanValue) {
                        int i11 = i6;
                        if (i11 != 0) {
                            MessagesController.this.loadMessagesInternal(j, 0L, false, i10, i11, 0, false, 0, generateClassGuid, 3, intValue2, 0, 0, -1, 0, 0, 0, false, 0, true, false, false);
                            return;
                        } else {
                            MessagesController.this.loadMessagesInternal(j, 0L, false, i10, i11, 0, false, 0, generateClassGuid, 2, intValue2, 0, 0, -1, 0, 0, 0, false, 0, true, false, false);
                            return;
                        }
                    }
                    MessagesController.this.getNotificationCenter().removeObserver(this, i9);
                    MessagesController.this.getNotificationCenter().removeObserver(this, NotificationCenter.loadingMessagesFailed);
                    MessagesLoadedCallback messagesLoadedCallback2 = messagesLoadedCallback;
                    if (messagesLoadedCallback2 == null) {
                        return;
                    }
                    messagesLoadedCallback2.onMessagesLoaded(booleanValue);
                    return;
                }
                int i12 = NotificationCenter.loadingMessagesFailed;
                if (i7 != i12 || ((Integer) objArr[0]).intValue() != generateClassGuid) {
                    return;
                }
                MessagesController.this.getNotificationCenter().removeObserver(this, i9);
                MessagesController.this.getNotificationCenter().removeObserver(this, i12);
                MessagesLoadedCallback messagesLoadedCallback3 = messagesLoadedCallback;
                if (messagesLoadedCallback3 == null) {
                    return;
                }
                messagesLoadedCallback3.onError();
            }
        };
        getNotificationCenter().addObserver(notificationCenterDelegate, NotificationCenter.messagesDidLoadWithoutProcess);
        getNotificationCenter().addObserver(notificationCenterDelegate, NotificationCenter.loadingMessagesFailed);
        if (i2 != 0) {
            loadMessagesInternal(j, 0L, true, i4, i2, 0, true, 0, generateClassGuid, 3, 0, 0, 0, -1, 0, 0, 0, false, 0, true, false, false);
        } else {
            loadMessagesInternal(j, 0L, true, i4, i2, 0, true, 0, generateClassGuid, 2, 0, 0, 0, -1, 0, 0, 0, false, 0, true, false, false);
        }
    }

    public /* synthetic */ void lambda$ensureMessagesLoaded$360(MessagesStorage messagesStorage, long j, final long j2, final int i, final MessagesLoadedCallback messagesLoadedCallback) {
        final TLRPC$Chat chat = messagesStorage.getChat(j);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda154
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$ensureMessagesLoaded$359(chat, j2, i, messagesLoadedCallback);
            }
        });
    }

    public /* synthetic */ void lambda$ensureMessagesLoaded$359(TLRPC$Chat tLRPC$Chat, long j, int i, MessagesLoadedCallback messagesLoadedCallback) {
        if (tLRPC$Chat != null) {
            getMessagesController().putChat(tLRPC$Chat, true);
            ensureMessagesLoaded(j, i, messagesLoadedCallback);
        } else if (messagesLoadedCallback == null) {
        } else {
            messagesLoadedCallback.onError();
        }
    }

    public int getChatPendingRequestsOnClosed(long j) {
        SharedPreferences sharedPreferences = this.mainPreferences;
        return sharedPreferences.getInt("chatPendingRequests" + j, 0);
    }

    public void setChatPendingRequestsOnClose(long j, int i) {
        SharedPreferences.Editor edit = this.mainPreferences.edit();
        edit.putInt("chatPendingRequests" + j, i).apply();
    }

    public void deleteMessagesRange(final long j, final long j2, final int i, final int i2, boolean z, final Runnable runnable) {
        TLRPC$TL_messages_deleteHistory tLRPC$TL_messages_deleteHistory = new TLRPC$TL_messages_deleteHistory();
        tLRPC$TL_messages_deleteHistory.peer = getInputPeer(j);
        tLRPC$TL_messages_deleteHistory.flags = 12;
        tLRPC$TL_messages_deleteHistory.min_date = i;
        tLRPC$TL_messages_deleteHistory.max_date = i2;
        tLRPC$TL_messages_deleteHistory.revoke = z;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteHistory, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda299
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$deleteMessagesRange$364(j, i, i2, j2, runnable, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$deleteMessagesRange$364(final long j, final int i, final int i2, final long j2, final Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_affectedHistory tLRPC$TL_messages_affectedHistory = (TLRPC$TL_messages_affectedHistory) tLObject;
            processNewDifferenceParams(-1, tLRPC$TL_messages_affectedHistory.pts, -1, tLRPC$TL_messages_affectedHistory.pts_count);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda71
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$deleteMessagesRange$362(j, i, i2, j2, runnable);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                runnable.run();
            }
        });
    }

    public /* synthetic */ void lambda$deleteMessagesRange$362(long j, int i, int i2, final long j2, final Runnable runnable) {
        final ArrayList<Integer> cachedMessagesInRange = getMessagesStorage().getCachedMessagesInRange(j, i, i2);
        getMessagesStorage().markMessagesAsDeleted(j, cachedMessagesInRange, false, true, false);
        getMessagesStorage().updateDialogsWithDeletedMessages(j, 0L, cachedMessagesInRange, null, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda121
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.lambda$deleteMessagesRange$361(cachedMessagesInRange, j2, runnable);
            }
        });
    }

    public /* synthetic */ void lambda$deleteMessagesRange$361(ArrayList arrayList, long j, Runnable runnable) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList, Long.valueOf(j), Boolean.FALSE);
        runnable.run();
    }

    public void setChatReactions(final long j, int i, List<String> list) {
        final TLRPC$TL_messages_setChatAvailableReactions tLRPC$TL_messages_setChatAvailableReactions = new TLRPC$TL_messages_setChatAvailableReactions();
        tLRPC$TL_messages_setChatAvailableReactions.peer = getInputPeer(-j);
        if (i == 2) {
            tLRPC$TL_messages_setChatAvailableReactions.available_reactions = new TLRPC$TL_chatReactionsNone();
        } else if (i == 0) {
            tLRPC$TL_messages_setChatAvailableReactions.available_reactions = new TLRPC$TL_chatReactionsAll();
        } else {
            TLRPC$TL_chatReactionsSome tLRPC$TL_chatReactionsSome = new TLRPC$TL_chatReactionsSome();
            tLRPC$TL_messages_setChatAvailableReactions.available_reactions = tLRPC$TL_chatReactionsSome;
            for (int i2 = 0; i2 < list.size(); i2++) {
                TLRPC$TL_reactionEmoji tLRPC$TL_reactionEmoji = new TLRPC$TL_reactionEmoji();
                tLRPC$TL_reactionEmoji.emoticon = list.get(i2);
                tLRPC$TL_chatReactionsSome.reactions.add(tLRPC$TL_reactionEmoji);
            }
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_setChatAvailableReactions, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda315
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MessagesController.this.lambda$setChatReactions$366(j, tLRPC$TL_messages_setChatAvailableReactions, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$setChatReactions$366(final long j, TLRPC$TL_messages_setChatAvailableReactions tLRPC$TL_messages_setChatAvailableReactions, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC$Updates) tLObject, false);
            TLRPC$ChatFull chatFull = getChatFull(j);
            if (chatFull != null) {
                if (chatFull instanceof TLRPC$TL_chatFull) {
                    chatFull.flags |= 262144;
                }
                if (chatFull instanceof TLRPC$TL_channelFull) {
                    chatFull.flags |= NUM;
                }
                chatFull.available_reactions = tLRPC$TL_messages_setChatAvailableReactions.available_reactions;
                getMessagesStorage().updateChatInfo(chatFull, false);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda69
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$setChatReactions$365(j);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setChatReactions$365(long j) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatAvailableReactionsUpdated, Long.valueOf(j), 0);
    }

    public void checkIsInChat(TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, final IsInChatCheckedCallback isInChatCheckedCallback) {
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
        ArrayList<TLRPC$ChatParticipant> arrayList;
        boolean z = false;
        if (tLRPC$Chat == null || tLRPC$User == null) {
            if (isInChatCheckedCallback == null) {
                return;
            }
            isInChatCheckedCallback.run(false, null, null);
        } else if (tLRPC$Chat.megagroup || ChatObject.isChannel(tLRPC$Chat)) {
            TLRPC$TL_channels_getParticipant tLRPC$TL_channels_getParticipant = new TLRPC$TL_channels_getParticipant();
            tLRPC$TL_channels_getParticipant.channel = getInputChannel(tLRPC$Chat.id);
            tLRPC$TL_channels_getParticipant.participant = getInputPeer(tLRPC$User);
            getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipant, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda237
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MessagesController.lambda$checkIsInChat$367(MessagesController.IsInChatCheckedCallback.this, tLObject, tLRPC$TL_error);
                }
            });
        } else {
            TLRPC$ChatFull chatFull = getChatFull(tLRPC$Chat.id);
            if (chatFull == null) {
                if (isInChatCheckedCallback == null) {
                    return;
                }
                isInChatCheckedCallback.run(false, null, null);
                return;
            }
            TLRPC$ChatParticipants tLRPC$ChatParticipants = chatFull.participants;
            if (tLRPC$ChatParticipants != null && (arrayList = tLRPC$ChatParticipants.participants) != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    tLRPC$ChatParticipant = chatFull.participants.participants.get(i);
                    if (tLRPC$ChatParticipant != null && tLRPC$ChatParticipant.user_id == tLRPC$User.id) {
                        break;
                    }
                }
            }
            tLRPC$ChatParticipant = null;
            if (isInChatCheckedCallback == null) {
                return;
            }
            if (tLRPC$ChatParticipant != null) {
                z = true;
            }
            TLRPC$ChatParticipants tLRPC$ChatParticipants2 = chatFull.participants;
            isInChatCheckedCallback.run(z, (tLRPC$ChatParticipants2 == null || tLRPC$ChatParticipants2.admin_id != tLRPC$User.id) ? null : ChatRightsEditActivity.emptyAdminRights(true), null);
        }
    }

    public static /* synthetic */ void lambda$checkIsInChat$367(IsInChatCheckedCallback isInChatCheckedCallback, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (isInChatCheckedCallback != null) {
            String str = null;
            TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLObject instanceof TLRPC$TL_channels_channelParticipant ? ((TLRPC$TL_channels_channelParticipant) tLObject).participant : null;
            boolean z = tLRPC$TL_error == null && tLRPC$ChannelParticipant != null && !tLRPC$ChannelParticipant.left;
            TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = tLRPC$ChannelParticipant != null ? tLRPC$ChannelParticipant.admin_rights : null;
            if (tLRPC$ChannelParticipant != null) {
                str = tLRPC$ChannelParticipant.rank;
            }
            isInChatCheckedCallback.run(z, tLRPC$TL_chatAdminRights, str);
        }
    }

    public void updateEmojiStatusUntilUpdate(long j, TLRPC$EmojiStatus tLRPC$EmojiStatus) {
        if (tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatusUntil) {
            this.emojiStatusUntilValues.put(j, Integer.valueOf(((TLRPC$TL_emojiStatusUntil) tLRPC$EmojiStatus).until));
        } else if (!this.emojiStatusUntilValues.containsKey(j)) {
            return;
        } else {
            this.emojiStatusUntilValues.remove(j);
        }
        updateEmojiStatusUntil();
    }

    public void updateEmojiStatusUntil() {
        int intValue;
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        Long l = null;
        int i = 0;
        while (i < this.emojiStatusUntilValues.size()) {
            if (this.emojiStatusUntilValues.valueAt(i).intValue() > currentTimeMillis) {
                l = Long.valueOf(Math.min(l == null ? Long.MAX_VALUE : l.longValue(), intValue - currentTimeMillis));
            } else {
                this.emojiStatusUntilValues.removeAt(i);
                i--;
            }
            i++;
        }
        if (l != null) {
            Long valueOf = Long.valueOf(l.longValue() + 2);
            long j = currentTimeMillis;
            if (valueOf.longValue() + j == this.recentEmojiStatusUpdateRunnableTime + this.recentEmojiStatusUpdateRunnableTimeout) {
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.recentEmojiStatusUpdateRunnable);
            this.recentEmojiStatusUpdateRunnableTime = j;
            this.recentEmojiStatusUpdateRunnableTimeout = valueOf.longValue();
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda40
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.lambda$updateEmojiStatusUntil$368();
                }
            };
            this.recentEmojiStatusUpdateRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, valueOf.longValue() * 1000);
            return;
        }
        Runnable runnable2 = this.recentEmojiStatusUpdateRunnable;
        if (runnable2 == null) {
            return;
        }
        this.recentEmojiStatusUpdateRunnableTime = -1L;
        this.recentEmojiStatusUpdateRunnableTimeout = -1L;
        AndroidUtilities.cancelRunOnUIThread(runnable2);
    }

    public /* synthetic */ void lambda$updateEmojiStatusUntil$368() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_EMOJI_STATUS));
        updateEmojiStatusUntil();
    }

    public String getMutedString(long j, int i) {
        if (getMessagesController().isDialogMuted(j, i)) {
            SharedPreferences sharedPreferences = this.notificationsPreferences;
            int i2 = sharedPreferences.getInt("notifyuntil_" + NotificationsController.getSharedPrefKey(j, i), 0);
            return i2 >= getConnectionsManager().getCurrentTime() ? LocaleController.formatString("NotificationsMutedForHint", R.string.NotificationsMutedForHint, LocaleController.formatTTLString(i2)) : LocaleController.getString(R.string.NotificationsMuted);
        }
        return LocaleController.getString(R.string.NotificationsUnmuted);
    }

    public int getDialogUnreadCount(TLRPC$Dialog tLRPC$Dialog) {
        if (tLRPC$Dialog == null) {
            return 0;
        }
        int i = tLRPC$Dialog.unread_count;
        TLRPC$Chat chat = getChat(Long.valueOf(-tLRPC$Dialog.id));
        return (chat == null || !chat.forum) ? i : this.topicsController.getForumUnreadCount(-tLRPC$Dialog.id)[0];
    }
}
