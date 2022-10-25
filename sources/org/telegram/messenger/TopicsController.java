package org.telegram.messenger;

import android.content.SharedPreferences;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import j$.util.Comparator$CC;
import j$.util.function.Consumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_channels_deleteTopicHistory;
import org.telegram.tgnet.TLRPC$TL_channels_editForumTopic;
import org.telegram.tgnet.TLRPC$TL_channels_getForumTopics;
import org.telegram.tgnet.TLRPC$TL_channels_getForumTopicsByID;
import org.telegram.tgnet.TLRPC$TL_channels_updatePinnedForumTopic;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionTopicCreate;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_messages_affectedHistory;
import org.telegram.tgnet.TLRPC$TL_messages_forumTopics;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.ui.Components.Forum.ForumUtilities;
/* loaded from: classes.dex */
public class TopicsController extends BaseController {
    public static final int LOAD_TYPE_LOAD_NEXT = 1;
    public static final int LOAD_TYPE_LOAD_UNKNOWN = 2;
    public static final int LOAD_TYPE_PRELOAD = 0;
    private static final int MAX_PRELOAD_COUNT = 20;
    public static final int TOPIC_FLAG_CLOSE = 8;
    public static final int TOPIC_FLAG_ICON = 2;
    public static final int TOPIC_FLAG_PIN = 4;
    public static final int TOPIC_FLAG_TITLE = 1;
    private static final int[] countsTmp = new int[4];
    LongSparseIntArray currentOpenTopicsCounter;
    LongSparseIntArray endIsReached;
    LongSparseArray<ArrayList<TLRPC$TL_forumTopic>> topicsByChatId;
    LongSparseIntArray topicsIsLoading;
    LongSparseArray<LongSparseArray<TLRPC$TL_forumTopic>> topicsMapByChatId;

    /* loaded from: classes.dex */
    public static class TopicUpdate {
        long dialogId;
        boolean reloadTopic;
        TLRPC$Message topMessage;
        int topMessageId;
        int topicId;
        int unreadCount;
        int unreadMentions;
    }

    public TopicsController(int i) {
        super(i);
        this.topicsByChatId = new LongSparseArray<>();
        this.topicsMapByChatId = new LongSparseArray<>();
        this.topicsIsLoading = new LongSparseIntArray();
        this.endIsReached = new LongSparseIntArray();
        this.currentOpenTopicsCounter = new LongSparseIntArray();
    }

    public void preloadTopics(long j) {
        loadTopics(j, true, 0);
    }

    public void loadTopics(long j) {
        loadTopics(j, false, 1);
    }

    private void loadTopics(final long j, final boolean z, final int i) {
        if (this.topicsIsLoading.get(j, 0) != 0) {
            return;
        }
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            FileLog.d("load topics " + j + " fromCache=" + z + " loadType=" + i);
        }
        if (z) {
            getMessagesStorage().loadTopics(-j, new Consumer() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda14
                @Override // j$.util.function.Consumer
                public final void accept(Object obj) {
                    TopicsController.this.lambda$loadTopics$1(j, z, i, (ArrayList) obj);
                }

                @Override // j$.util.function.Consumer
                public /* synthetic */ Consumer andThen(Consumer consumer) {
                    return consumer.getClass();
                }
            });
            return;
        }
        this.topicsIsLoading.put(j, 1);
        TLRPC$TL_channels_getForumTopics tLRPC$TL_channels_getForumTopics = new TLRPC$TL_channels_getForumTopics();
        tLRPC$TL_channels_getForumTopics.channel = getMessagesController().getInputChannel(j);
        if (i == 0) {
            tLRPC$TL_channels_getForumTopics.limit = 20;
        } else if (i == 1) {
            tLRPC$TL_channels_getForumTopics.limit = 100;
            TopicsLoadOffset loadOffset = getLoadOffset(j);
            tLRPC$TL_channels_getForumTopics.offset_date = loadOffset.lastMessageDate;
            tLRPC$TL_channels_getForumTopics.offset_id = loadOffset.lastMessageId;
            tLRPC$TL_channels_getForumTopics.offset_topic = loadOffset.lastTopicId;
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                FileLog.d("offset_date=" + loadOffset.lastMessageDate + " offset_id=" + loadOffset.lastMessageId + " offset_topic=" + loadOffset.lastTopicId);
            }
        }
        getConnectionsManager().sendRequest(tLRPC$TL_channels_getForumTopics, new RequestDelegate() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda17
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TopicsController.this.lambda$loadTopics$4(j, i, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadTopics$1(final long j, final boolean z, final int i, final ArrayList arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$loadTopics$0(j, arrayList, z, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadTopics$0(long j, ArrayList arrayList, boolean z, int i) {
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            StringBuilder sb = new StringBuilder();
            sb.append("loaded from cache ");
            sb.append(j);
            sb.append(" topics_count=");
            sb.append(arrayList == null ? 0 : arrayList.size());
            FileLog.d(sb.toString());
        }
        this.topicsIsLoading.put(j, 0);
        processTopics(j, arrayList, null, z, i, -1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadTopics$4(final long j, final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            final SparseArray sparseArray = new SparseArray();
            final TLRPC$TL_messages_forumTopics tLRPC$TL_messages_forumTopics = (TLRPC$TL_messages_forumTopics) tLObject;
            for (int i2 = 0; i2 < tLRPC$TL_messages_forumTopics.messages.size(); i2++) {
                sparseArray.put(tLRPC$TL_messages_forumTopics.messages.get(i2).id, tLRPC$TL_messages_forumTopics.messages.get(i2));
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    TopicsController.this.lambda$loadTopics$2(tLObject, j, tLRPC$TL_messages_forumTopics, sparseArray, i);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$loadTopics$3(j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadTopics$2(TLObject tLObject, long j, TLRPC$TL_messages_forumTopics tLRPC$TL_messages_forumTopics, SparseArray sparseArray, int i) {
        TLRPC$TL_messages_forumTopics tLRPC$TL_messages_forumTopics2 = (TLRPC$TL_messages_forumTopics) tLObject;
        getMessagesController().putUsers(tLRPC$TL_messages_forumTopics2.users, false);
        getMessagesController().putChats(tLRPC$TL_messages_forumTopics2.chats, false);
        this.topicsIsLoading.put(j, 0);
        processTopics(j, tLRPC$TL_messages_forumTopics.topics, sparseArray, false, i, tLRPC$TL_messages_forumTopics2.count);
        getMessagesStorage().putMessages(tLRPC$TL_messages_forumTopics.messages, false, true, false, 0, false, 0);
        getMessagesStorage().saveTopics(-j, this.topicsByChatId.get(j), true, true);
        if (!tLRPC$TL_messages_forumTopics.topics.isEmpty()) {
            ArrayList<TLRPC$TL_forumTopic> arrayList = tLRPC$TL_messages_forumTopics.topics;
            TLRPC$TL_forumTopic tLRPC$TL_forumTopic = arrayList.get(arrayList.size() - 1);
            saveLoadOffset(j, tLRPC$TL_forumTopic.top_message, ((TLRPC$Message) sparseArray.get(tLRPC$TL_forumTopic.top_message)).date, tLRPC$TL_forumTopic.id);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadTopics$3(long j) {
        this.topicsIsLoading.put(j, 0);
        getNotificationCenter().postNotificationName(NotificationCenter.topicsDidLoaded, Long.valueOf(j));
    }

    public void processTopics(long j, ArrayList<TLRPC$TL_forumTopic> arrayList, SparseArray<TLRPC$Message> sparseArray, boolean z, int i, int i2) {
        boolean z2;
        int i3;
        ArrayList<TLRPC$TL_forumTopic> arrayList2 = arrayList;
        SparseArray<TLRPC$Message> sparseArray2 = sparseArray;
        ArrayList<TLRPC$TL_forumTopic> arrayList3 = this.topicsByChatId.get(j);
        LongSparseArray<TLRPC$TL_forumTopic> longSparseArray = this.topicsMapByChatId.get(j);
        if (arrayList3 == null) {
            arrayList3 = new ArrayList<>();
            this.topicsByChatId.put(j, arrayList3);
        }
        if (longSparseArray == null) {
            longSparseArray = new LongSparseArray<>();
            this.topicsMapByChatId.put(j, longSparseArray);
        }
        ArrayList<TLRPC$TL_forumTopic> arrayList4 = null;
        if (arrayList2 != null) {
            int i4 = 0;
            z2 = false;
            while (i4 < arrayList.size()) {
                TLRPC$TL_forumTopic tLRPC$TL_forumTopic = arrayList2.get(i4);
                if (!longSparseArray.containsKey(tLRPC$TL_forumTopic.id)) {
                    if (sparseArray2 != null) {
                        tLRPC$TL_forumTopic.topMessage = sparseArray2.get(tLRPC$TL_forumTopic.top_message);
                        tLRPC$TL_forumTopic.topicStartMessage = sparseArray2.get(tLRPC$TL_forumTopic.id);
                    }
                    if (tLRPC$TL_forumTopic.topMessage == null) {
                        if (arrayList4 == null) {
                            arrayList4 = new ArrayList<>();
                        }
                        arrayList4.add(tLRPC$TL_forumTopic);
                    } else {
                        if (tLRPC$TL_forumTopic.topicStartMessage == null) {
                            TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                            tLRPC$TL_forumTopic.topicStartMessage = tLRPC$TL_message;
                            tLRPC$TL_message.message = "";
                            tLRPC$TL_message.id = tLRPC$TL_forumTopic.id;
                            tLRPC$TL_message.peer_id = getMessagesController().getPeer(-j);
                            tLRPC$TL_forumTopic.topicStartMessage.action = new TLRPC$TL_messageActionTopicCreate();
                            tLRPC$TL_forumTopic.topicStartMessage.action.title = tLRPC$TL_forumTopic.title;
                        }
                        arrayList3.add(tLRPC$TL_forumTopic);
                        longSparseArray.put(tLRPC$TL_forumTopic.id, tLRPC$TL_forumTopic);
                        z2 = true;
                    }
                }
                i4++;
                arrayList2 = arrayList;
                sparseArray2 = sparseArray;
            }
        } else {
            z2 = false;
        }
        if (z2) {
            sortTopics(j);
        }
        if (arrayList4 != null) {
            reloadTopics(j, arrayList4);
            i3 = 1;
        } else {
            i3 = 1;
            if (i == 1 && arrayList3.size() >= i2 && i2 >= 0) {
                this.endIsReached.put(j, 1);
            }
        }
        NotificationCenter notificationCenter = getNotificationCenter();
        int i5 = NotificationCenter.topicsDidLoaded;
        Object[] objArr = new Object[i3];
        objArr[0] = Long.valueOf(j);
        notificationCenter.postNotificationName(i5, objArr);
        if ((i == 0 || (i == 0 && !z)) && z && this.topicsByChatId.get(j).isEmpty()) {
            loadTopics(j, false, 0);
        }
    }

    public ArrayList<TLRPC$TL_forumTopic> getTopics(long j) {
        return this.topicsByChatId.get(j);
    }

    private void sortTopics(long j) {
        sortTopics(j, true);
    }

    private void sortTopics(long j, boolean z) {
        ArrayList<TLRPC$TL_forumTopic> arrayList = this.topicsByChatId.get(j);
        if (arrayList != null) {
            Collections.sort(arrayList, Comparator$CC.comparingInt(TopicsController$$ExternalSyntheticLambda15.INSTANCE));
            if (!z) {
                return;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.topicsDidLoaded, Long.valueOf(j));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$sortTopics$5(TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
        return -tLRPC$TL_forumTopic.topMessage.date;
    }

    public void updateTopicsWithDeletedMessages(final long j, final ArrayList<Integer> arrayList) {
        if (j > 0) {
            return;
        }
        final long j2 = -j;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$updateTopicsWithDeletedMessages$8(j, arrayList, j2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateTopicsWithDeletedMessages$8(final long j, final ArrayList arrayList, final long j2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$updateTopicsWithDeletedMessages$7(j, arrayList, j2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateTopicsWithDeletedMessages$7(long j, ArrayList arrayList, final long j2) {
        NativeByteBuffer byteBufferValue;
        final ArrayList arrayList2 = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT topic_id FROM topics WHERE did = %d AND top_message IN (%s)", Long.valueOf(j), TextUtils.join(",", arrayList)), new Object[0]);
            while (queryFinalized.next()) {
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                }
                TLRPC$TL_forumTopic tLRPC$TL_forumTopic = new TLRPC$TL_forumTopic();
                tLRPC$TL_forumTopic.id = queryFinalized.intValue(0);
                tLRPC$TL_forumTopic.top_message = -1;
                tLRPC$TL_forumTopic.from_id = getMessagesController().getPeer(getUserConfig().clientUserId);
                tLRPC$TL_forumTopic.notify_settings = new TLRPC$TL_peerNotifySettings();
                arrayList2.add(tLRPC$TL_forumTopic);
            }
            queryFinalized.dispose();
            if (arrayList2 != null) {
                for (int i = 0; i < arrayList2.size(); i++) {
                    TLRPC$TL_forumTopic tLRPC$TL_forumTopic2 = (TLRPC$TL_forumTopic) arrayList2.get(i);
                    SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid, data FROM messages_topics WHERE uid = %d AND topic_id = %d ORDER BY mid DESC LIMIT 1", Long.valueOf(j), Integer.valueOf(tLRPC$TL_forumTopic2.id)), new Object[0]);
                    if (queryFinalized2.next() && (byteBufferValue = queryFinalized2.byteBufferValue(1)) != null) {
                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                        byteBufferValue.reuse();
                        tLRPC$TL_forumTopic2.top_message = TLdeserialize.id;
                        tLRPC$TL_forumTopic2.topMessage = TLdeserialize;
                    }
                    queryFinalized2.dispose();
                }
                for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                    getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "UPDATE topics SET top_message = %d WHERE did = %d AND topic_id = %d", Integer.valueOf(((TLRPC$TL_forumTopic) arrayList2.get(i2)).top_message), Long.valueOf(j), Integer.valueOf(((TLRPC$TL_forumTopic) arrayList2.get(i2)).id))).stepThis().dispose();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (arrayList2 != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    TopicsController.this.lambda$updateTopicsWithDeletedMessages$6(arrayList2, j2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateTopicsWithDeletedMessages$6(ArrayList arrayList, long j) {
        ArrayList<TLRPC$TL_forumTopic> arrayList2 = null;
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$TL_forumTopic tLRPC$TL_forumTopic = (TLRPC$TL_forumTopic) arrayList.get(i);
            LongSparseArray<TLRPC$TL_forumTopic> longSparseArray = this.topicsMapByChatId.get(j);
            if (longSparseArray != null) {
                TLRPC$TL_forumTopic tLRPC$TL_forumTopic2 = longSparseArray.get(tLRPC$TL_forumTopic.id);
                if (tLRPC$TL_forumTopic2 != null && tLRPC$TL_forumTopic.top_message != -1) {
                    TLRPC$Message tLRPC$Message = tLRPC$TL_forumTopic.topMessage;
                    tLRPC$TL_forumTopic2.top_message = tLRPC$Message.id;
                    tLRPC$TL_forumTopic2.topMessage = tLRPC$Message;
                    z = true;
                } else if (tLRPC$TL_forumTopic.top_message == -1) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList<>();
                    }
                    arrayList2.add(tLRPC$TL_forumTopic);
                }
            }
        }
        if (z) {
            sortTopics(j);
        }
        if (arrayList2 != null) {
            reloadTopics(j, arrayList2);
        }
    }

    private void reloadTopics(final long j, ArrayList<TLRPC$TL_forumTopic> arrayList) {
        TLRPC$TL_channels_getForumTopicsByID tLRPC$TL_channels_getForumTopicsByID = new TLRPC$TL_channels_getForumTopicsByID();
        for (int i = 0; i < arrayList.size(); i++) {
            tLRPC$TL_channels_getForumTopicsByID.topics.add(Integer.valueOf(arrayList.get(i).id));
        }
        tLRPC$TL_channels_getForumTopicsByID.channel = getMessagesController().getInputChannel(j);
        getConnectionsManager().sendRequest(tLRPC$TL_channels_getForumTopicsByID, new RequestDelegate() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda16
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TopicsController.this.lambda$reloadTopics$11(j, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reloadTopics$11(final long j, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$reloadTopics$10(tLObject, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reloadTopics$10(final TLObject tLObject, final long j) {
        if (tLObject != null) {
            final SparseArray sparseArray = new SparseArray();
            final TLRPC$TL_messages_forumTopics tLRPC$TL_messages_forumTopics = (TLRPC$TL_messages_forumTopics) tLObject;
            for (int i = 0; i < tLRPC$TL_messages_forumTopics.messages.size(); i++) {
                sparseArray.put(tLRPC$TL_messages_forumTopics.messages.get(i).id, tLRPC$TL_messages_forumTopics.messages.get(i));
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    TopicsController.this.lambda$reloadTopics$9(tLObject, j, tLRPC$TL_messages_forumTopics, sparseArray);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reloadTopics$9(TLObject tLObject, long j, TLRPC$TL_messages_forumTopics tLRPC$TL_messages_forumTopics, SparseArray sparseArray) {
        TLRPC$TL_messages_forumTopics tLRPC$TL_messages_forumTopics2 = (TLRPC$TL_messages_forumTopics) tLObject;
        getMessagesController().putUsers(tLRPC$TL_messages_forumTopics2.users, false);
        getMessagesController().putChats(tLRPC$TL_messages_forumTopics2.chats, false);
        processTopics(j, tLRPC$TL_messages_forumTopics.topics, sparseArray, false, 2, -1);
        getMessagesStorage().putMessages(tLRPC$TL_messages_forumTopics.messages, false, true, false, 0, false, 0);
        getMessagesStorage().saveTopics(-j, this.topicsByChatId.get(j), true, true);
    }

    public void updateMaxReadId(long j, int i, int i2, int i3) {
        TLRPC$TL_forumTopic findTopic = findTopic(j, i);
        if (findTopic != null) {
            findTopic.read_inbox_max_id = i2;
            findTopic.unread_count = i3;
            sortTopics(j);
        }
    }

    public TLRPC$TL_forumTopic findTopic(long j, int i) {
        LongSparseArray<TLRPC$TL_forumTopic> longSparseArray = this.topicsMapByChatId.get(j);
        if (longSparseArray != null) {
            return longSparseArray.get(i);
        }
        return null;
    }

    public String getTopicName(TLRPC$Chat tLRPC$Chat, MessageObject messageObject) {
        TLRPC$TL_forumTopic findTopic;
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = messageObject.messageOwner.reply_to;
        if (tLRPC$TL_messageReplyHeader == null) {
            return null;
        }
        int i = tLRPC$TL_messageReplyHeader.reply_to_top_id;
        if (i == 0) {
            i = tLRPC$TL_messageReplyHeader.reply_to_msg_id;
        }
        return (i == 0 || (findTopic = findTopic(tLRPC$Chat.id, i)) == null) ? "" : findTopic.title;
    }

    public CharSequence getTopicIconName(TLRPC$Chat tLRPC$Chat, MessageObject messageObject, TextPaint textPaint) {
        TLRPC$TL_forumTopic findTopic;
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = messageObject.messageOwner.reply_to;
        if (tLRPC$TL_messageReplyHeader == null) {
            return null;
        }
        int i = tLRPC$TL_messageReplyHeader.reply_to_top_id;
        if (i == 0) {
            i = tLRPC$TL_messageReplyHeader.reply_to_msg_id;
        }
        if (i != 0 && (findTopic = findTopic(tLRPC$Chat.id, i)) != null) {
            return ForumUtilities.getForumSpannedName(findTopic, textPaint);
        }
        return null;
    }

    public int[] getForumUnreadCount(long j) {
        ArrayList<TLRPC$TL_forumTopic> arrayList = this.topicsByChatId.get(j);
        Arrays.fill(countsTmp, 0);
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$TL_forumTopic tLRPC$TL_forumTopic = arrayList.get(i);
                int[] iArr = countsTmp;
                int i2 = 1;
                iArr[0] = iArr[0] + (tLRPC$TL_forumTopic.unread_count > 0 ? 1 : 0);
                iArr[1] = iArr[1] + (tLRPC$TL_forumTopic.unread_mentions_count > 0 ? 1 : 0);
                int i3 = iArr[2];
                if (tLRPC$TL_forumTopic.unread_reactions_count <= 0) {
                    i2 = 0;
                }
                iArr[2] = i3 + i2;
                if (!getMessagesController().isDialogMuted(-j, tLRPC$TL_forumTopic.id)) {
                    iArr[3] = iArr[3] + tLRPC$TL_forumTopic.unread_count;
                }
            }
        }
        return countsTmp;
    }

    public void onTopicCreated(long j, TLRPC$TL_forumTopic tLRPC$TL_forumTopic, boolean z) {
        long j2 = -j;
        LongSparseArray<TLRPC$TL_forumTopic> longSparseArray = this.topicsMapByChatId.get(j2);
        if (findTopic(j2, tLRPC$TL_forumTopic.id) != null) {
            return;
        }
        if (longSparseArray == null) {
            longSparseArray = new LongSparseArray<>();
            this.topicsMapByChatId.put(j2, longSparseArray);
        }
        ArrayList<TLRPC$TL_forumTopic> arrayList = this.topicsByChatId.get(j2);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.topicsByChatId.put(j2, arrayList);
        }
        longSparseArray.put(tLRPC$TL_forumTopic.id, tLRPC$TL_forumTopic);
        arrayList.add(tLRPC$TL_forumTopic);
        if (z) {
            getMessagesStorage().saveTopics(j, Collections.singletonList(tLRPC$TL_forumTopic), false, true);
        }
        sortTopics(j2, true);
    }

    public void onTopicEdited(long j, TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
        getMessagesStorage().updateTopicData(j, tLRPC$TL_forumTopic, 3);
        sortTopics(-j);
    }

    public void deleteTopics(long j, ArrayList<Integer> arrayList) {
        ArrayList<TLRPC$TL_forumTopic> arrayList2 = this.topicsByChatId.get(j);
        LongSparseArray<TLRPC$TL_forumTopic> longSparseArray = this.topicsMapByChatId.get(j);
        if (longSparseArray != null && arrayList2 != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                long intValue = arrayList.get(i).intValue();
                TLRPC$TL_forumTopic tLRPC$TL_forumTopic = longSparseArray.get(intValue);
                longSparseArray.remove(intValue);
                if (tLRPC$TL_forumTopic != null) {
                    arrayList2.remove(tLRPC$TL_forumTopic);
                }
            }
            sortTopics(j);
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            deleteTopic(j, arrayList.get(i2).intValue(), 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteTopic(final long j, final int i, int i2) {
        TLRPC$TL_channels_deleteTopicHistory tLRPC$TL_channels_deleteTopicHistory = new TLRPC$TL_channels_deleteTopicHistory();
        tLRPC$TL_channels_deleteTopicHistory.channel = getMessagesController().getInputChannel(j);
        tLRPC$TL_channels_deleteTopicHistory.top_msg_id = i;
        if (i2 == 0) {
            getMessagesStorage().removeTopic(-j, i);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_deleteTopicHistory, new RequestDelegate() { // from class: org.telegram.messenger.TopicsController.1
            @Override // org.telegram.tgnet.RequestDelegate
            public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                if (tLRPC$TL_error == null) {
                    TLRPC$TL_messages_affectedHistory tLRPC$TL_messages_affectedHistory = (TLRPC$TL_messages_affectedHistory) tLObject;
                    TopicsController.this.getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_messages_affectedHistory.pts, tLRPC$TL_messages_affectedHistory.pts_count, j);
                    int i3 = tLRPC$TL_messages_affectedHistory.offset;
                    if (i3 <= 0) {
                        return;
                    }
                    TopicsController.this.deleteTopic(j, i, i3);
                }
            }
        });
    }

    public void toggleCloseTopic(long j, int i, boolean z) {
        TLRPC$TL_forumTopic tLRPC$TL_forumTopic;
        TLRPC$TL_channels_editForumTopic tLRPC$TL_channels_editForumTopic = new TLRPC$TL_channels_editForumTopic();
        tLRPC$TL_channels_editForumTopic.channel = getMessagesController().getInputChannel(j);
        tLRPC$TL_channels_editForumTopic.topic_id = i;
        tLRPC$TL_channels_editForumTopic.flags |= 4;
        tLRPC$TL_channels_editForumTopic.closed = z;
        LongSparseArray<TLRPC$TL_forumTopic> longSparseArray = this.topicsMapByChatId.get(j);
        if (longSparseArray != null && (tLRPC$TL_forumTopic = longSparseArray.get(i)) != null) {
            tLRPC$TL_forumTopic.closed = z;
            getMessagesStorage().updateTopicData(-j, tLRPC$TL_forumTopic, 8);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_editForumTopic, new RequestDelegate() { // from class: org.telegram.messenger.TopicsController.2
            @Override // org.telegram.tgnet.RequestDelegate
            public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            }
        });
    }

    public void pinTopic(long j, int i, boolean z) {
        TLRPC$TL_channels_updatePinnedForumTopic tLRPC$TL_channels_updatePinnedForumTopic = new TLRPC$TL_channels_updatePinnedForumTopic();
        tLRPC$TL_channels_updatePinnedForumTopic.channel = getMessagesController().getInputChannel(j);
        tLRPC$TL_channels_updatePinnedForumTopic.topic_id = i;
        tLRPC$TL_channels_updatePinnedForumTopic.pinned = z;
        TLRPC$TL_forumTopic findTopic = findTopic(j, i);
        if (findTopic != null) {
            int i2 = findTopic.id;
            boolean z2 = true;
            if ((i == i2 && z) != findTopic.pinned) {
                if (i != i2 || !z) {
                    z2 = false;
                }
                findTopic.pinned = z2;
                int i3 = findTopic.flags;
                findTopic.flags = z2 ? i3 | 8 : i3 & (-9);
                getMessagesStorage().updateTopicData(-j, findTopic, 4);
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_updatePinnedForumTopic, TopicsController$$ExternalSyntheticLambda18.INSTANCE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pinTopic$12(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        boolean z = tLObject instanceof TLRPC$Updates;
    }

    public void updateMentionsUnread(final long j, final int i, final int i2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$updateMentionsUnread$13(j, i, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMentionsUnread$13(long j, int i, int i2) {
        long j2 = -j;
        TLRPC$TL_forumTopic findTopic = findTopic(j2, i);
        if (findTopic != null) {
            findTopic.unread_mentions_count = i2;
            sortTopics(j2, true);
        }
    }

    public int updateReactionsUnread(long j, int i, int i2, boolean z) {
        long j2 = -j;
        TLRPC$TL_forumTopic findTopic = findTopic(j2, i);
        if (findTopic != null) {
            if (z) {
                int i3 = findTopic.unread_reactions_count + i2;
                findTopic.unread_reactions_count = i3;
                if (i3 < 0) {
                    findTopic.unread_reactions_count = 0;
                }
            } else {
                findTopic.unread_reactions_count = i2;
            }
            int i4 = findTopic.unread_reactions_count;
            sortTopics(j2, true);
            return i4;
        }
        return -1;
    }

    public void markAllReactionsAsRead(long j, int i) {
        TLRPC$TL_forumTopic findTopic = findTopic(j, i);
        if (findTopic == null || findTopic.unread_reactions_count <= 0) {
            return;
        }
        findTopic.unread_reactions_count = 0;
        sortTopics(j);
    }

    public TLRPC$Message getLastMessage(long j) {
        ArrayList<TLRPC$TL_forumTopic> arrayList = this.topicsByChatId.get(j);
        if (!arrayList.isEmpty()) {
            return arrayList.get(0).topMessage;
        }
        return null;
    }

    public TopicsLoadOffset getLoadOffset(long j) {
        SharedPreferences preferences = getUserConfig().getPreferences();
        TopicsLoadOffset topicsLoadOffset = new TopicsLoadOffset();
        topicsLoadOffset.lastMessageId = preferences.getInt("topics_load_offset_message_id_" + j, 0);
        topicsLoadOffset.lastMessageDate = preferences.getInt("topics_load_offset_date_" + j, 0);
        topicsLoadOffset.lastMessageId = preferences.getInt("topics_load_offset_topic_id_" + j, 0);
        return topicsLoadOffset;
    }

    public void saveLoadOffset(long j, int i, int i2, int i3) {
        SharedPreferences.Editor edit = getUserConfig().getPreferences().edit();
        edit.putInt("topics_load_offset_message_id_" + j, i);
        edit.putInt("topics_load_offset_date_" + j, i2);
        edit.putInt("topics_load_offset_topic_id_" + j, i3);
        edit.apply();
    }

    public void clearLoadingOffset(long j) {
        SharedPreferences.Editor edit = getUserConfig().getPreferences().edit();
        edit.remove("topics_load_offset_message_id_" + j);
        edit.remove("topics_load_offset_date_" + j);
        edit.remove("topics_load_offset_topic_id_" + j);
        edit.apply();
    }

    public boolean endIsReached(long j) {
        return this.endIsReached.get(j, 0) == 1;
    }

    public void processUpdate(final ArrayList<TopicUpdate> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$processUpdate$14(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processUpdate$14(ArrayList arrayList) {
        HashSet hashSet = new HashSet();
        LongSparseArray longSparseArray = null;
        for (int i = 0; i < arrayList.size(); i++) {
            TopicUpdate topicUpdate = (TopicUpdate) arrayList.get(i);
            if (topicUpdate.reloadTopic) {
                if (longSparseArray == null) {
                    longSparseArray = new LongSparseArray();
                }
                ArrayList arrayList2 = (ArrayList) longSparseArray.get(topicUpdate.dialogId);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    longSparseArray.put(topicUpdate.dialogId, arrayList2);
                }
                TLRPC$TL_forumTopic tLRPC$TL_forumTopic = new TLRPC$TL_forumTopic();
                tLRPC$TL_forumTopic.id = topicUpdate.topicId;
                arrayList2.add(tLRPC$TL_forumTopic);
            } else {
                TLRPC$TL_forumTopic findTopic = findTopic(-topicUpdate.dialogId, topicUpdate.topicId);
                if (findTopic != null) {
                    findTopic.topMessage = topicUpdate.topMessage;
                    findTopic.top_message = topicUpdate.topMessageId;
                    findTopic.unread_count = topicUpdate.unreadCount;
                    findTopic.unread_mentions_count = topicUpdate.unreadMentions;
                    hashSet.add(Long.valueOf(-topicUpdate.dialogId));
                }
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            sortTopics(((Long) it.next()).longValue(), true);
        }
        if (longSparseArray != null) {
            for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                reloadTopics(-longSparseArray.keyAt(i2), (ArrayList) longSparseArray.valueAt(i2));
            }
        }
    }

    public boolean isLoading(long j) {
        if (this.topicsIsLoading.get(j, 0) == 1) {
            return this.topicsByChatId.get(j) == null || this.topicsByChatId.get(j).isEmpty();
        }
        return false;
    }

    public void removeTopics(final ArrayList<MessagesStorage.TopicKey> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$removeTopics$15(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeTopics$15(ArrayList arrayList) {
        HashSet hashSet = new HashSet();
        for (int i = 0; i < arrayList.size(); i++) {
            MessagesStorage.TopicKey topicKey = (MessagesStorage.TopicKey) arrayList.get(i);
            long j = -topicKey.dialogId;
            LongSparseArray<TLRPC$TL_forumTopic> longSparseArray = this.topicsMapByChatId.get(j);
            if (longSparseArray != null) {
                longSparseArray.remove(topicKey.topicId);
            }
            ArrayList<TLRPC$TL_forumTopic> arrayList2 = this.topicsByChatId.get(j);
            if (arrayList2 != null) {
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList2.size()) {
                        break;
                    } else if (arrayList2.get(i2).id == topicKey.topicId) {
                        arrayList2.remove(i2);
                        hashSet.add(Long.valueOf(j));
                        break;
                    } else {
                        i2++;
                    }
                }
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            sortTopics(((Long) it.next()).longValue(), true);
        }
    }

    public void reloadTopics(final long j) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$reloadTopics$16(j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reloadTopics$16(long j) {
        this.topicsByChatId.remove(j);
        this.topicsMapByChatId.remove(j);
        this.endIsReached.delete(j);
        clearLoadingOffset(j);
        TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(j));
        if (chat == null || !chat.forum) {
            return;
        }
        preloadTopics(j);
    }

    public void databaseCleared() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$databaseCleared$17();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$databaseCleared$17() {
        this.topicsByChatId.clear();
        this.topicsMapByChatId.clear();
        this.endIsReached.clear();
        SharedPreferences.Editor edit = getUserConfig().getPreferences().edit();
        for (String str : getUserConfig().getPreferences().getAll().keySet()) {
            if (str.startsWith("topics_load_offset_message_id_")) {
                edit.remove(str);
            }
            if (str.startsWith("topics_load_offset_date_")) {
                edit.remove(str);
            }
            if (str.startsWith("topics_load_offset_topic_id_")) {
                edit.remove(str);
            }
        }
        edit.apply();
    }

    public void updateReadOutbox(final HashMap<MessagesStorage.TopicKey, Integer> hashMap) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.TopicsController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                TopicsController.this.lambda$updateReadOutbox$18(hashMap);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateReadOutbox$18(HashMap hashMap) {
        HashSet hashSet = new HashSet();
        for (MessagesStorage.TopicKey topicKey : hashMap.keySet()) {
            int intValue = ((Integer) hashMap.get(topicKey)).intValue();
            TLRPC$TL_forumTopic findTopic = findTopic(-topicKey.dialogId, topicKey.topicId);
            if (findTopic != null) {
                findTopic.read_outbox_max_id = Math.max(findTopic.read_outbox_max_id, intValue);
                hashSet.add(Long.valueOf(-topicKey.dialogId));
                int i = findTopic.read_outbox_max_id;
                TLRPC$Message tLRPC$Message = findTopic.topMessage;
                if (i >= tLRPC$Message.id) {
                    tLRPC$Message.unread = false;
                }
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.topicsDidLoaded, (Long) it.next());
        }
    }

    public void updateTopicInUi(long j, TLRPC$TL_forumTopic tLRPC$TL_forumTopic, int i) {
        long j2 = -j;
        TLRPC$TL_forumTopic findTopic = findTopic(j2, tLRPC$TL_forumTopic.id);
        if (findTopic != null) {
            if ((i & 1) != 0) {
                findTopic.title = tLRPC$TL_forumTopic.title;
            }
            if ((i & 2) != 0) {
                findTopic.icon_emoji_id = tLRPC$TL_forumTopic.icon_emoji_id;
            }
            if ((i & 8) != 0) {
                findTopic.closed = tLRPC$TL_forumTopic.closed;
            }
            if ((i & 4) != 0) {
                findTopic.pinned = tLRPC$TL_forumTopic.pinned;
            }
            sortTopics(j2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TopicsLoadOffset {
        int lastMessageDate;
        int lastMessageId;
        int lastTopicId;

        private TopicsLoadOffset() {
        }
    }
}