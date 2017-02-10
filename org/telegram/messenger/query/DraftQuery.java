package org.telegram.messenger.query;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_draftMessage;
import org.telegram.tgnet.TLRPC.TL_draftMessageEmpty;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getAllDrafts;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_saveDraft;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;

public class DraftQuery {
    private static HashMap<Long, Message> draftMessages = new HashMap();
    private static HashMap<Long, DraftMessage> drafts = new HashMap();
    private static boolean inTransaction;
    private static boolean loadingDrafts;
    private static SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);

    static {
        for (Entry<String, ?> entry : preferences.getAll().entrySet()) {
            try {
                String key = (String) entry.getKey();
                long did = Utilities.parseLong(key).longValue();
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes((String) entry.getValue()));
                if (key.startsWith("r_")) {
                    Message message = Message.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    if (message != null) {
                        draftMessages.put(Long.valueOf(did), message);
                    }
                } else {
                    DraftMessage draftMessage = DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    if (draftMessage != null) {
                        drafts.put(Long.valueOf(did), draftMessage);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public static void loadDrafts() {
        if (!UserConfig.draftsLoaded && !loadingDrafts) {
            loadingDrafts = true;
            ConnectionsManager.getInstance().sendRequest(new TL_messages_getAllDrafts(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesController.getInstance().processUpdates((Updates) response, false);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                UserConfig.draftsLoaded = true;
                                DraftQuery.loadingDrafts = false;
                                UserConfig.saveConfig(false);
                            }
                        });
                    }
                }
            });
        }
    }

    public static void cleanup() {
        drafts.clear();
        draftMessages.clear();
        preferences.edit().clear().commit();
    }

    public static DraftMessage getDraft(long did) {
        return (DraftMessage) drafts.get(Long.valueOf(did));
    }

    public static Message getDraftMessage(long did) {
        return (Message) draftMessages.get(Long.valueOf(did));
    }

    public static void saveDraft(long did, CharSequence message, ArrayList<MessageEntity> entities, Message replyToMessage, boolean noWebpage) {
        saveDraft(did, message, entities, replyToMessage, noWebpage, false);
    }

    public static void saveDraft(long did, CharSequence message, ArrayList<MessageEntity> entities, Message replyToMessage, boolean noWebpage, boolean clean) {
        DraftMessage draftMessage;
        if (TextUtils.isEmpty(message) && replyToMessage == null) {
            draftMessage = new TL_draftMessageEmpty();
        } else {
            draftMessage = new TL_draftMessage();
        }
        draftMessage.date = (int) (System.currentTimeMillis() / 1000);
        draftMessage.message = message == null ? "" : message.toString();
        draftMessage.no_webpage = noWebpage;
        if (replyToMessage != null) {
            draftMessage.reply_to_msg_id = replyToMessage.id;
            draftMessage.flags |= 1;
        }
        if (!(entities == null || entities.isEmpty())) {
            draftMessage.entities = entities;
            draftMessage.flags |= 8;
        }
        DraftMessage currentDraft = (DraftMessage) drafts.get(Long.valueOf(did));
        if (!clean) {
            if (currentDraft == null || !currentDraft.message.equals(draftMessage.message) || currentDraft.reply_to_msg_id != draftMessage.reply_to_msg_id || currentDraft.no_webpage != draftMessage.no_webpage) {
                if (currentDraft == null && TextUtils.isEmpty(draftMessage.message) && draftMessage.reply_to_msg_id == 0) {
                    return;
                }
            }
            return;
        }
        saveDraft(did, draftMessage, replyToMessage, false);
        int lower_id = (int) did;
        if (lower_id != 0) {
            TL_messages_saveDraft req = new TL_messages_saveDraft();
            req.peer = MessagesController.getInputPeer(lower_id);
            if (req.peer != null) {
                req.message = draftMessage.message;
                req.no_webpage = draftMessage.no_webpage;
                req.reply_to_msg_id = draftMessage.reply_to_msg_id;
                req.entities = draftMessage.entities;
                req.flags = draftMessage.flags;
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
            } else {
                return;
            }
        }
        MessagesController.getInstance().sortDialogs(null);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public static void saveDraft(long did, DraftMessage draft, Message replyToMessage, boolean fromServer) {
        Editor editor = preferences.edit();
        if (draft == null || (draft instanceof TL_draftMessageEmpty)) {
            drafts.remove(Long.valueOf(did));
            draftMessages.remove(Long.valueOf(did));
            preferences.edit().remove("" + did).remove("r_" + did).commit();
        } else {
            drafts.put(Long.valueOf(did), draft);
            try {
                SerializedData serializedData = new SerializedData(draft.getObjectSize());
                draft.serializeToStream(serializedData);
                editor.putString("" + did, Utilities.bytesToHex(serializedData.toByteArray()));
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        if (replyToMessage == null) {
            draftMessages.remove(Long.valueOf(did));
            editor.remove("r_" + did);
        } else {
            draftMessages.put(Long.valueOf(did), replyToMessage);
            serializedData = new SerializedData(replyToMessage.getObjectSize());
            replyToMessage.serializeToStream(serializedData);
            editor.putString("r_" + did, Utilities.bytesToHex(serializedData.toByteArray()));
        }
        editor.commit();
        if (fromServer) {
            if (draft.reply_to_msg_id != 0 && replyToMessage == null) {
                int lower_id = (int) did;
                User user = null;
                Chat chat = null;
                if (lower_id > 0) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
                } else {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                }
                if (!(user == null && chat == null)) {
                    int channelIdFinal;
                    long messageId = (long) draft.reply_to_msg_id;
                    if (ChatObject.isChannel(chat)) {
                        messageId |= ((long) chat.id) << 32;
                        channelIdFinal = chat.id;
                    } else {
                        channelIdFinal = 0;
                    }
                    final long messageIdFinal = messageId;
                    final long j = did;
                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                        public void run() {
                            Message message = null;
                            try {
                                SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(messageIdFinal)}), new Object[0]);
                                if (cursor.next()) {
                                    NativeByteBuffer data = cursor.byteBufferValue(0);
                                    if (data != null) {
                                        message = Message.TLdeserialize(data, data.readInt32(false), false);
                                        data.reuse();
                                    }
                                }
                                cursor.dispose();
                                if (message != null) {
                                    DraftQuery.saveDraftReplyMessage(j, message);
                                } else if (channelIdFinal != 0) {
                                    TL_channels_getMessages req = new TL_channels_getMessages();
                                    req.channel = MessagesController.getInputChannel(channelIdFinal);
                                    req.id.add(Integer.valueOf((int) messageIdFinal));
                                    ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                        public void run(TLObject response, TL_error error) {
                                            if (error == null) {
                                                messages_Messages messagesRes = (messages_Messages) response;
                                                if (!messagesRes.messages.isEmpty()) {
                                                    DraftQuery.saveDraftReplyMessage(j, (Message) messagesRes.messages.get(0));
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    TL_messages_getMessages req2 = new TL_messages_getMessages();
                                    req2.id.add(Integer.valueOf((int) messageIdFinal));
                                    ConnectionsManager.getInstance().sendRequest(req2, new RequestDelegate() {
                                        public void run(TLObject response, TL_error error) {
                                            if (error == null) {
                                                messages_Messages messagesRes = (messages_Messages) response;
                                                if (!messagesRes.messages.isEmpty()) {
                                                    DraftQuery.saveDraftReplyMessage(j, (Message) messagesRes.messages.get(0));
                                                }
                                            }
                                        }
                                    });
                                }
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    });
                }
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
        }
    }

    private static void saveDraftReplyMessage(final long did, final Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    DraftMessage draftMessage = (DraftMessage) DraftQuery.drafts.get(Long.valueOf(did));
                    if (draftMessage != null && draftMessage.reply_to_msg_id == message.id) {
                        DraftQuery.draftMessages.put(Long.valueOf(did), message);
                        SerializedData serializedData = new SerializedData(message.getObjectSize());
                        message.serializeToStream(serializedData);
                        DraftQuery.preferences.edit().putString("r_" + did, Utilities.bytesToHex(serializedData.toByteArray())).commit();
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
                    }
                }
            });
        }
    }

    public static void cleanDraft(long did, boolean replyOnly) {
        DraftMessage draftMessage = (DraftMessage) drafts.get(Long.valueOf(did));
        if (draftMessage != null) {
            if (!replyOnly) {
                drafts.remove(Long.valueOf(did));
                draftMessages.remove(Long.valueOf(did));
                preferences.edit().remove("" + did).remove("r_" + did).commit();
                MessagesController.getInstance().sortDialogs(null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            } else if (draftMessage.reply_to_msg_id != 0) {
                draftMessage.reply_to_msg_id = 0;
                draftMessage.flags &= -2;
                saveDraft(did, draftMessage.message, draftMessage.entities, null, draftMessage.no_webpage, true);
            }
        }
    }

    public static void beginTransaction() {
        inTransaction = true;
    }

    public static void endTransaction() {
        inTransaction = false;
    }
}
