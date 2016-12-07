package org.telegram.messenger.query;

import android.text.TextUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.messages_Messages;

public class MessagesSearchQuery {
    private static long lastMergeDialogId;
    private static int lastReqId;
    private static int lastReturnedNum;
    private static String lastSearchQuery;
    private static int mergeReqId;
    private static int[] messagesSearchCount = new int[]{0, 0};
    private static boolean[] messagesSearchEndReached = new boolean[]{false, false};
    private static int reqId;
    private static ArrayList<MessageObject> searchResultMessages = new ArrayList();

    private static int getMask() {
        int mask = 0;
        if (!(lastReturnedNum >= searchResultMessages.size() - 1 && messagesSearchEndReached[0] && messagesSearchEndReached[1])) {
            mask = 0 | 1;
        }
        if (lastReturnedNum > 0) {
            return mask | 2;
        }
        return mask;
    }

    public static void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction) {
        searchMessagesInChat(query, dialog_id, mergeDialogId, guid, direction, false);
    }

    private static void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction, boolean internal) {
        final TL_messages_search req;
        int max_id = 0;
        long queryWithDialog = dialog_id;
        boolean firstQuery = !internal;
        if (reqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(reqId, true);
            reqId = 0;
        }
        if (mergeReqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(mergeReqId, true);
            mergeReqId = 0;
        }
        if (TextUtils.isEmpty(query)) {
            if (!searchResultMessages.isEmpty()) {
                MessageObject messageObject;
                if (direction == 1) {
                    lastReturnedNum++;
                    if (lastReturnedNum < searchResultMessages.size()) {
                        messageObject = (MessageObject) searchResultMessages.get(lastReturnedNum);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(lastReturnedNum), Integer.valueOf(messagesSearchCount[0] + messagesSearchCount[1]));
                        return;
                    } else if (messagesSearchEndReached[0] && mergeDialogId == 0 && messagesSearchEndReached[1]) {
                        lastReturnedNum--;
                        return;
                    } else {
                        firstQuery = false;
                        query = lastSearchQuery;
                        messageObject = (MessageObject) searchResultMessages.get(searchResultMessages.size() - 1);
                        if (messageObject.getDialogId() != dialog_id || messagesSearchEndReached[0]) {
                            if (messageObject.getDialogId() == mergeDialogId) {
                                max_id = messageObject.getId();
                            }
                            queryWithDialog = mergeDialogId;
                            messagesSearchEndReached[1] = false;
                        } else {
                            max_id = messageObject.getId();
                            queryWithDialog = dialog_id;
                        }
                    }
                } else if (direction == 2) {
                    lastReturnedNum--;
                    if (lastReturnedNum < 0) {
                        lastReturnedNum = 0;
                        return;
                    }
                    if (lastReturnedNum >= searchResultMessages.size()) {
                        lastReturnedNum = searchResultMessages.size() - 1;
                    }
                    messageObject = (MessageObject) searchResultMessages.get(lastReturnedNum);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(lastReturnedNum), Integer.valueOf(messagesSearchCount[0] + messagesSearchCount[1]));
                    return;
                } else {
                    return;
                }
            }
            return;
        } else if (firstQuery) {
            boolean[] zArr = messagesSearchEndReached;
            messagesSearchEndReached[1] = false;
            zArr[0] = false;
            int[] iArr = messagesSearchCount;
            messagesSearchCount[1] = 0;
            iArr[0] = 0;
            searchResultMessages.clear();
        }
        if (!(!messagesSearchEndReached[0] || messagesSearchEndReached[1] || mergeDialogId == 0)) {
            queryWithDialog = mergeDialogId;
        }
        if (queryWithDialog == dialog_id && firstQuery) {
            if (mergeDialogId != 0) {
                InputPeer inputPeer = MessagesController.getInputPeer((int) mergeDialogId);
                if (inputPeer != null) {
                    req = new TL_messages_search();
                    req.peer = inputPeer;
                    lastMergeDialogId = mergeDialogId;
                    req.limit = 1;
                    req.q = query;
                    req.filter = new TL_inputMessagesFilterEmpty();
                    final long j = mergeDialogId;
                    final long j2 = dialog_id;
                    final int i = guid;
                    final int i2 = direction;
                    mergeReqId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (MessagesSearchQuery.lastMergeDialogId == j) {
                                        MessagesSearchQuery.mergeReqId = 0;
                                        if (response != null) {
                                            messages_Messages res = response;
                                            MessagesSearchQuery.messagesSearchEndReached[1] = res.messages.isEmpty();
                                            MessagesSearchQuery.messagesSearchCount[1] = res instanceof TL_messages_messagesSlice ? res.count : res.messages.size();
                                            MessagesSearchQuery.searchMessagesInChat(req.q, j2, j, i, i2, true);
                                        }
                                    }
                                }
                            });
                        }
                    }, 2);
                    return;
                }
                return;
            }
            lastMergeDialogId = 0;
            messagesSearchEndReached[1] = true;
            messagesSearchCount[1] = 0;
        }
        req = new TL_messages_search();
        req.peer = MessagesController.getInputPeer((int) queryWithDialog);
        if (req.peer != null) {
            req.limit = 21;
            req.q = query;
            req.max_id = max_id;
            req.filter = new TL_inputMessagesFilterEmpty();
            final int currentReqId = lastReqId + 1;
            lastReqId = currentReqId;
            lastSearchQuery = query;
            j2 = queryWithDialog;
            final long j3 = dialog_id;
            final int i3 = guid;
            final long j4 = mergeDialogId;
            reqId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (currentReqId == MessagesSearchQuery.lastReqId) {
                                MessagesSearchQuery.reqId = 0;
                                if (response != null) {
                                    messages_Messages res = response;
                                    MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, true, true);
                                    MessagesController.getInstance().putUsers(res.users, false);
                                    MessagesController.getInstance().putChats(res.chats, false);
                                    if (req.max_id == 0 && j2 == j3) {
                                        MessagesSearchQuery.lastReturnedNum = 0;
                                        MessagesSearchQuery.searchResultMessages.clear();
                                        MessagesSearchQuery.messagesSearchCount[0] = 0;
                                    }
                                    boolean added = false;
                                    for (int a = 0; a < Math.min(res.messages.size(), 20); a++) {
                                        added = true;
                                        MessagesSearchQuery.searchResultMessages.add(new MessageObject((Message) res.messages.get(a), null, false));
                                    }
                                    MessagesSearchQuery.messagesSearchEndReached[j2 == j3 ? 0 : 1] = res.messages.size() != 21;
                                    MessagesSearchQuery.messagesSearchCount[j2 == j3 ? 0 : 1] = res instanceof TL_messages_messagesSlice ? res.count : res.messages.size();
                                    if (MessagesSearchQuery.searchResultMessages.isEmpty()) {
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i3), Integer.valueOf(0), Integer.valueOf(MessagesSearchQuery.getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                                    } else if (added) {
                                        if (MessagesSearchQuery.lastReturnedNum >= MessagesSearchQuery.searchResultMessages.size()) {
                                            MessagesSearchQuery.lastReturnedNum = MessagesSearchQuery.searchResultMessages.size() - 1;
                                        }
                                        MessageObject messageObject = (MessageObject) MessagesSearchQuery.searchResultMessages.get(MessagesSearchQuery.lastReturnedNum);
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i3), Integer.valueOf(messageObject.getId()), Integer.valueOf(MessagesSearchQuery.getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(MessagesSearchQuery.lastReturnedNum), Integer.valueOf(MessagesSearchQuery.messagesSearchCount[0] + MessagesSearchQuery.messagesSearchCount[1]));
                                    }
                                    if (j2 == j3 && MessagesSearchQuery.messagesSearchEndReached[0] && j4 != 0 && !MessagesSearchQuery.messagesSearchEndReached[1]) {
                                        MessagesSearchQuery.searchMessagesInChat(MessagesSearchQuery.lastSearchQuery, j3, j4, i3, 0, true);
                                    }
                                }
                            }
                        }
                    });
                }
            }, 2);
        }
    }

    public static String getLastSearchQuery() {
        return lastSearchQuery;
    }
}
