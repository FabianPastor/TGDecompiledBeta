package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_folderPeer;
import org.telegram.tgnet.TLRPC.TL_inputFolderPeer;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported_old;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_botResults;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty_layer77;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_poll;
import org.telegram.tgnet.TLRPC.TL_pollResults;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Dialogs;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.tgnet.TLRPC.photos_Photos;

public class MessagesStorage {
    private static volatile MessagesStorage[] Instance = new MessagesStorage[3];
    private static final int LAST_DB_VERSION = 60;
    private File cacheFile;
    private int currentAccount;
    private SQLiteDatabase database;
    private int lastDateValue = 0;
    private int lastPtsValue = 0;
    private int lastQtsValue = 0;
    private int lastSavedDate = 0;
    private int lastSavedPts = 0;
    private int lastSavedQts = 0;
    private int lastSavedSeq = 0;
    private int lastSecretVersion = 0;
    private int lastSeqValue = 0;
    private AtomicLong lastTaskId = new AtomicLong(System.currentTimeMillis());
    private CountDownLatch openSync = new CountDownLatch(1);
    private int secretG = 0;
    private byte[] secretPBytes = null;
    private File shmCacheFile;
    private DispatchQueue storageQueue = new DispatchQueue("storageQueue");
    private File walCacheFile;

    public interface BooleanCallback {
        void run(boolean z);
    }

    private class Hole {
        public int end;
        public int start;
        public int type;

        public Hole(int i, int i2) {
            this.start = i;
            this.end = i2;
        }

        public Hole(int i, int i2, int i3) {
            this.type = i;
            this.start = i2;
            this.end = i3;
        }
    }

    public interface IntCallback {
        void run(int i);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:99:0x02a7 in {9, 13, 14, 18, 19, 20, 27, 28, 38, 39, 40, 41, 44, 45, 48, 49, 52, 53, 58, 59, 60, 67, 71, 74, 77, 78, 81, 84, 87, 91, 93, 95, 96, 98} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    private void updateDialogsWithDeletedMessagesInternal(java.util.ArrayList<java.lang.Integer> r18, java.util.ArrayList<java.lang.Long> r19, int r20) {
        /*
        r17 = this;
        r1 = r17;
        r0 = r19;
        r2 = r20;
        r3 = java.lang.Thread.currentThread();
        r3 = r3.getId();
        r5 = r1.storageQueue;
        r5 = r5.getId();
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 != 0) goto L_0x029e;
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0299 }
        r3.<init>();	 Catch:{ Exception -> 0x0299 }
        r4 = r18.isEmpty();	 Catch:{ Exception -> 0x0299 }
        r5 = 3;
        r6 = 2;
        r7 = ",";
        r8 = 1;
        r9 = 0;
        if (r4 != 0) goto L_0x00a5;
        if (r2 == 0) goto L_0x003d;
        r4 = -r2;
        r10 = (long) r4;
        r4 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x0299 }
        r3.add(r4);	 Catch:{ Exception -> 0x0299 }
        r4 = r1.database;	 Catch:{ Exception -> 0x0299 }
        r10 = "UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ?)) WHERE did = ?";	 Catch:{ Exception -> 0x0299 }
        r4 = r4.executeFast(r10);	 Catch:{ Exception -> 0x0299 }
        goto L_0x0074;	 Catch:{ Exception -> 0x0299 }
        r4 = r18;	 Catch:{ Exception -> 0x0299 }
        r4 = android.text.TextUtils.join(r7, r4);	 Catch:{ Exception -> 0x0299 }
        r10 = r1.database;	 Catch:{ Exception -> 0x0299 }
        r11 = java.util.Locale.US;	 Catch:{ Exception -> 0x0299 }
        r12 = "SELECT did FROM dialogs WHERE last_mid IN(%s)";	 Catch:{ Exception -> 0x0299 }
        r13 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0299 }
        r13[r9] = r4;	 Catch:{ Exception -> 0x0299 }
        r4 = java.lang.String.format(r11, r12, r13);	 Catch:{ Exception -> 0x0299 }
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0299 }
        r4 = r10.queryFinalized(r4, r11);	 Catch:{ Exception -> 0x0299 }
        r10 = r4.next();	 Catch:{ Exception -> 0x0299 }
        if (r10 == 0) goto L_0x0069;	 Catch:{ Exception -> 0x0299 }
        r10 = r4.longValue(r9);	 Catch:{ Exception -> 0x0299 }
        r10 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x0299 }
        r3.add(r10);	 Catch:{ Exception -> 0x0299 }
        goto L_0x0057;	 Catch:{ Exception -> 0x0299 }
        r4.dispose();	 Catch:{ Exception -> 0x0299 }
        r4 = r1.database;	 Catch:{ Exception -> 0x0299 }
        r10 = "UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? AND date != 0)) WHERE did = ?";	 Catch:{ Exception -> 0x0299 }
        r4 = r4.executeFast(r10);	 Catch:{ Exception -> 0x0299 }
        r10 = r1.database;	 Catch:{ Exception -> 0x0299 }
        r10.beginTransaction();	 Catch:{ Exception -> 0x0299 }
        r10 = 0;	 Catch:{ Exception -> 0x0299 }
        r11 = r3.size();	 Catch:{ Exception -> 0x0299 }
        if (r10 >= r11) goto L_0x009c;	 Catch:{ Exception -> 0x0299 }
        r11 = r3.get(r10);	 Catch:{ Exception -> 0x0299 }
        r11 = (java.lang.Long) r11;	 Catch:{ Exception -> 0x0299 }
        r11 = r11.longValue();	 Catch:{ Exception -> 0x0299 }
        r4.requery();	 Catch:{ Exception -> 0x0299 }
        r4.bindLong(r8, r11);	 Catch:{ Exception -> 0x0299 }
        r4.bindLong(r6, r11);	 Catch:{ Exception -> 0x0299 }
        r4.bindLong(r5, r11);	 Catch:{ Exception -> 0x0299 }
        r4.step();	 Catch:{ Exception -> 0x0299 }
        r10 = r10 + 1;	 Catch:{ Exception -> 0x0299 }
        goto L_0x007a;	 Catch:{ Exception -> 0x0299 }
        r4.dispose();	 Catch:{ Exception -> 0x0299 }
        r4 = r1.database;	 Catch:{ Exception -> 0x0299 }
        r4.commitTransaction();	 Catch:{ Exception -> 0x0299 }
        goto L_0x00ae;	 Catch:{ Exception -> 0x0299 }
        r4 = -r2;	 Catch:{ Exception -> 0x0299 }
        r10 = (long) r4;	 Catch:{ Exception -> 0x0299 }
        r4 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x0299 }
        r3.add(r4);	 Catch:{ Exception -> 0x0299 }
        if (r0 == 0) goto L_0x00c9;	 Catch:{ Exception -> 0x0299 }
        r4 = 0;	 Catch:{ Exception -> 0x0299 }
        r10 = r19.size();	 Catch:{ Exception -> 0x0299 }
        if (r4 >= r10) goto L_0x00c9;	 Catch:{ Exception -> 0x0299 }
        r10 = r0.get(r4);	 Catch:{ Exception -> 0x0299 }
        r10 = (java.lang.Long) r10;	 Catch:{ Exception -> 0x0299 }
        r11 = r3.contains(r10);	 Catch:{ Exception -> 0x0299 }
        if (r11 != 0) goto L_0x00c6;	 Catch:{ Exception -> 0x0299 }
        r3.add(r10);	 Catch:{ Exception -> 0x0299 }
        r4 = r4 + 1;	 Catch:{ Exception -> 0x0299 }
        goto L_0x00b1;	 Catch:{ Exception -> 0x0299 }
        r0 = android.text.TextUtils.join(r7, r3);	 Catch:{ Exception -> 0x0299 }
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_dialogs;	 Catch:{ Exception -> 0x0299 }
        r3.<init>();	 Catch:{ Exception -> 0x0299 }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0299 }
        r4.<init>();	 Catch:{ Exception -> 0x0299 }
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0299 }
        r10.<init>();	 Catch:{ Exception -> 0x0299 }
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0299 }
        r11.<init>();	 Catch:{ Exception -> 0x0299 }
        r12 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0299 }
        r12.<init>();	 Catch:{ Exception -> 0x0299 }
        r13 = r1.database;	 Catch:{ Exception -> 0x0299 }
        r14 = java.util.Locale.US;	 Catch:{ Exception -> 0x0299 }
        r15 = "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.pts, d.inbox_max, d.outbox_max, d.pinned, d.unread_count_i, d.flags, d.folder_id, d.data FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid WHERE d.did IN(%s)";	 Catch:{ Exception -> 0x0299 }
        r5 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0299 }
        r5[r9] = r0;	 Catch:{ Exception -> 0x0299 }
        r0 = java.lang.String.format(r14, r15, r5);	 Catch:{ Exception -> 0x0299 }
        r5 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0299 }
        r0 = r13.queryFinalized(r0, r5);	 Catch:{ Exception -> 0x0299 }
        r5 = r0.next();	 Catch:{ Exception -> 0x0299 }
        if (r5 == 0) goto L_0x0253;	 Catch:{ Exception -> 0x0299 }
        r13 = r0.longValue(r9);	 Catch:{ Exception -> 0x0299 }
        r5 = org.telegram.messenger.DialogObject.isFolderDialogId(r13);	 Catch:{ Exception -> 0x0299 }
        r15 = 15;	 Catch:{ Exception -> 0x0299 }
        if (r5 == 0) goto L_0x013d;	 Catch:{ Exception -> 0x0299 }
        r5 = new org.telegram.tgnet.TLRPC$TL_dialogFolder;	 Catch:{ Exception -> 0x0299 }
        r5.<init>();	 Catch:{ Exception -> 0x0299 }
        r6 = 16;	 Catch:{ Exception -> 0x0299 }
        r16 = r0.isNull(r6);	 Catch:{ Exception -> 0x0299 }
        if (r16 != 0) goto L_0x0142;	 Catch:{ Exception -> 0x0299 }
        r6 = r0.byteBufferValue(r6);	 Catch:{ Exception -> 0x0299 }
        if (r6 == 0) goto L_0x012a;	 Catch:{ Exception -> 0x0299 }
        r8 = r6.readInt32(r9);	 Catch:{ Exception -> 0x0299 }
        r8 = org.telegram.tgnet.TLRPC.TL_folder.TLdeserialize(r6, r8, r9);	 Catch:{ Exception -> 0x0299 }
        r5.folder = r8;	 Catch:{ Exception -> 0x0299 }
        goto L_0x0139;	 Catch:{ Exception -> 0x0299 }
        r8 = new org.telegram.tgnet.TLRPC$TL_folder;	 Catch:{ Exception -> 0x0299 }
        r8.<init>();	 Catch:{ Exception -> 0x0299 }
        r5.folder = r8;	 Catch:{ Exception -> 0x0299 }
        r8 = r5.folder;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.intValue(r15);	 Catch:{ Exception -> 0x0299 }
        r8.id = r9;	 Catch:{ Exception -> 0x0299 }
        r6.reuse();	 Catch:{ Exception -> 0x0299 }
        goto L_0x0142;	 Catch:{ Exception -> 0x0299 }
        r5 = new org.telegram.tgnet.TLRPC$TL_dialog;	 Catch:{ Exception -> 0x0299 }
        r5.<init>();	 Catch:{ Exception -> 0x0299 }
        r5.id = r13;	 Catch:{ Exception -> 0x0299 }
        r6 = 1;	 Catch:{ Exception -> 0x0299 }
        r8 = r0.intValue(r6);	 Catch:{ Exception -> 0x0299 }
        r5.top_message = r8;	 Catch:{ Exception -> 0x0299 }
        r6 = 10;	 Catch:{ Exception -> 0x0299 }
        r6 = r0.intValue(r6);	 Catch:{ Exception -> 0x0299 }
        r5.read_inbox_max_id = r6;	 Catch:{ Exception -> 0x0299 }
        r6 = 11;	 Catch:{ Exception -> 0x0299 }
        r6 = r0.intValue(r6);	 Catch:{ Exception -> 0x0299 }
        r5.read_outbox_max_id = r6;	 Catch:{ Exception -> 0x0299 }
        r6 = 2;	 Catch:{ Exception -> 0x0299 }
        r8 = r0.intValue(r6);	 Catch:{ Exception -> 0x0299 }
        r5.unread_count = r8;	 Catch:{ Exception -> 0x0299 }
        r8 = 13;	 Catch:{ Exception -> 0x0299 }
        r8 = r0.intValue(r8);	 Catch:{ Exception -> 0x0299 }
        r5.unread_mentions_count = r8;	 Catch:{ Exception -> 0x0299 }
        r8 = 3;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.intValue(r8);	 Catch:{ Exception -> 0x0299 }
        r5.last_message_date = r9;	 Catch:{ Exception -> 0x0299 }
        r9 = 9;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.intValue(r9);	 Catch:{ Exception -> 0x0299 }
        r5.pts = r9;	 Catch:{ Exception -> 0x0299 }
        if (r2 != 0) goto L_0x017d;	 Catch:{ Exception -> 0x0299 }
        r9 = 0;	 Catch:{ Exception -> 0x0299 }
        goto L_0x017e;	 Catch:{ Exception -> 0x0299 }
        r9 = 1;	 Catch:{ Exception -> 0x0299 }
        r5.flags = r9;	 Catch:{ Exception -> 0x0299 }
        r9 = 12;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.intValue(r9);	 Catch:{ Exception -> 0x0299 }
        r5.pinnedNum = r9;	 Catch:{ Exception -> 0x0299 }
        r9 = r5.pinnedNum;	 Catch:{ Exception -> 0x0299 }
        if (r9 == 0) goto L_0x018e;	 Catch:{ Exception -> 0x0299 }
        r9 = 1;	 Catch:{ Exception -> 0x0299 }
        goto L_0x018f;	 Catch:{ Exception -> 0x0299 }
        r9 = 0;	 Catch:{ Exception -> 0x0299 }
        r5.pinned = r9;	 Catch:{ Exception -> 0x0299 }
        r9 = 14;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.intValue(r9);	 Catch:{ Exception -> 0x0299 }
        r13 = 1;	 Catch:{ Exception -> 0x0299 }
        r9 = r9 & r13;	 Catch:{ Exception -> 0x0299 }
        if (r9 == 0) goto L_0x019d;	 Catch:{ Exception -> 0x0299 }
        r9 = 1;	 Catch:{ Exception -> 0x0299 }
        goto L_0x019e;	 Catch:{ Exception -> 0x0299 }
        r9 = 0;	 Catch:{ Exception -> 0x0299 }
        r5.unread_mark = r9;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.intValue(r15);	 Catch:{ Exception -> 0x0299 }
        r5.folder_id = r9;	 Catch:{ Exception -> 0x0299 }
        r9 = r3.dialogs;	 Catch:{ Exception -> 0x0299 }
        r9.add(r5);	 Catch:{ Exception -> 0x0299 }
        r9 = 4;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.byteBufferValue(r9);	 Catch:{ Exception -> 0x0299 }
        if (r9 == 0) goto L_0x01f6;	 Catch:{ Exception -> 0x0299 }
        r13 = 0;	 Catch:{ Exception -> 0x0299 }
        r14 = r9.readInt32(r13);	 Catch:{ Exception -> 0x0299 }
        r14 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r9, r14, r13);	 Catch:{ Exception -> 0x0299 }
        r15 = r1.currentAccount;	 Catch:{ Exception -> 0x0299 }
        r15 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ Exception -> 0x0299 }
        r15 = r15.clientUserId;	 Catch:{ Exception -> 0x0299 }
        r14.readAttachPath(r9, r15);	 Catch:{ Exception -> 0x0299 }
        r9.reuse();	 Catch:{ Exception -> 0x0299 }
        r9 = 5;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.intValue(r9);	 Catch:{ Exception -> 0x0299 }
        org.telegram.messenger.MessageObject.setUnreadFlags(r14, r9);	 Catch:{ Exception -> 0x0299 }
        r9 = 6;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.intValue(r9);	 Catch:{ Exception -> 0x0299 }
        r14.id = r9;	 Catch:{ Exception -> 0x0299 }
        r9 = 7;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.intValue(r9);	 Catch:{ Exception -> 0x0299 }
        r14.send_state = r9;	 Catch:{ Exception -> 0x0299 }
        r9 = 8;	 Catch:{ Exception -> 0x0299 }
        r9 = r0.intValue(r9);	 Catch:{ Exception -> 0x0299 }
        if (r9 == 0) goto L_0x01e9;	 Catch:{ Exception -> 0x0299 }
        r5.last_message_date = r9;	 Catch:{ Exception -> 0x0299 }
        r8 = r5.id;	 Catch:{ Exception -> 0x0299 }
        r14.dialog_id = r8;	 Catch:{ Exception -> 0x0299 }
        r8 = r3.messages;	 Catch:{ Exception -> 0x0299 }
        r8.add(r14);	 Catch:{ Exception -> 0x0299 }
        addUsersAndChatsFromMessage(r14, r10, r11);	 Catch:{ Exception -> 0x0299 }
        goto L_0x01f7;	 Catch:{ Exception -> 0x0299 }
        r13 = 0;	 Catch:{ Exception -> 0x0299 }
        r8 = r5.id;	 Catch:{ Exception -> 0x0299 }
        r9 = (int) r8;	 Catch:{ Exception -> 0x0299 }
        r14 = r5.id;	 Catch:{ Exception -> 0x0299 }
        r5 = 32;	 Catch:{ Exception -> 0x0299 }
        r14 = r14 >> r5;	 Catch:{ Exception -> 0x0299 }
        r5 = (int) r14;	 Catch:{ Exception -> 0x0299 }
        if (r9 == 0) goto L_0x023e;	 Catch:{ Exception -> 0x0299 }
        r8 = 1;	 Catch:{ Exception -> 0x0299 }
        if (r5 != r8) goto L_0x0217;	 Catch:{ Exception -> 0x0299 }
        r5 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0299 }
        r5 = r11.contains(r5);	 Catch:{ Exception -> 0x0299 }
        if (r5 != 0) goto L_0x0250;	 Catch:{ Exception -> 0x0299 }
        r5 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0299 }
        r11.add(r5);	 Catch:{ Exception -> 0x0299 }
        goto L_0x0250;	 Catch:{ Exception -> 0x0299 }
        if (r9 <= 0) goto L_0x022b;	 Catch:{ Exception -> 0x0299 }
        r5 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0299 }
        r5 = r10.contains(r5);	 Catch:{ Exception -> 0x0299 }
        if (r5 != 0) goto L_0x0250;	 Catch:{ Exception -> 0x0299 }
        r5 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0299 }
        r10.add(r5);	 Catch:{ Exception -> 0x0299 }
        goto L_0x0250;	 Catch:{ Exception -> 0x0299 }
        r5 = -r9;	 Catch:{ Exception -> 0x0299 }
        r9 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0299 }
        r9 = r11.contains(r9);	 Catch:{ Exception -> 0x0299 }
        if (r9 != 0) goto L_0x0250;	 Catch:{ Exception -> 0x0299 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0299 }
        r11.add(r5);	 Catch:{ Exception -> 0x0299 }
        goto L_0x0250;	 Catch:{ Exception -> 0x0299 }
        r8 = 1;	 Catch:{ Exception -> 0x0299 }
        r9 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0299 }
        r9 = r12.contains(r9);	 Catch:{ Exception -> 0x0299 }
        if (r9 != 0) goto L_0x0250;	 Catch:{ Exception -> 0x0299 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0299 }
        r12.add(r5);	 Catch:{ Exception -> 0x0299 }
        r9 = 0;	 Catch:{ Exception -> 0x0299 }
        goto L_0x00fa;	 Catch:{ Exception -> 0x0299 }
        r0.dispose();	 Catch:{ Exception -> 0x0299 }
        r0 = r12.isEmpty();	 Catch:{ Exception -> 0x0299 }
        if (r0 != 0) goto L_0x0263;	 Catch:{ Exception -> 0x0299 }
        r0 = android.text.TextUtils.join(r7, r12);	 Catch:{ Exception -> 0x0299 }
        r1.getEncryptedChatsInternal(r0, r4, r10);	 Catch:{ Exception -> 0x0299 }
        r0 = r11.isEmpty();	 Catch:{ Exception -> 0x0299 }
        if (r0 != 0) goto L_0x0272;	 Catch:{ Exception -> 0x0299 }
        r0 = android.text.TextUtils.join(r7, r11);	 Catch:{ Exception -> 0x0299 }
        r2 = r3.chats;	 Catch:{ Exception -> 0x0299 }
        r1.getChatsInternal(r0, r2);	 Catch:{ Exception -> 0x0299 }
        r0 = r10.isEmpty();	 Catch:{ Exception -> 0x0299 }
        if (r0 != 0) goto L_0x0281;	 Catch:{ Exception -> 0x0299 }
        r0 = android.text.TextUtils.join(r7, r10);	 Catch:{ Exception -> 0x0299 }
        r2 = r3.users;	 Catch:{ Exception -> 0x0299 }
        r1.getUsersInternal(r0, r2);	 Catch:{ Exception -> 0x0299 }
        r0 = r3.dialogs;	 Catch:{ Exception -> 0x0299 }
        r0 = r0.isEmpty();	 Catch:{ Exception -> 0x0299 }
        if (r0 == 0) goto L_0x028f;	 Catch:{ Exception -> 0x0299 }
        r0 = r4.isEmpty();	 Catch:{ Exception -> 0x0299 }
        if (r0 != 0) goto L_0x029d;	 Catch:{ Exception -> 0x0299 }
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x0299 }
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);	 Catch:{ Exception -> 0x0299 }
        r0.processDialogsUpdate(r3, r4);	 Catch:{ Exception -> 0x0299 }
        goto L_0x029d;
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        return;
        r0 = new java.lang.RuntimeException;
        r2 = "wrong db thread";
        r0.<init>(r2);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateDialogsWithDeletedMessagesInternal(java.util.ArrayList, java.util.ArrayList, int):void");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:55:0x0102 in {6, 11, 12, 13, 16, 21, 22, 33, 34, 37, 40, 41, 45, 49, 51, 52, 54} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    private void updateUsersInternal(java.util.ArrayList<org.telegram.tgnet.TLRPC.User> r7, boolean r8, boolean r9) {
        /*
        r6 = this;
        r0 = java.lang.Thread.currentThread();
        r0 = r0.getId();
        r2 = r6.storageQueue;
        r2 = r2.getId();
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 != 0) goto L_0x00f9;
        r0 = 0;
        if (r8 == 0) goto L_0x005c;
        if (r9 == 0) goto L_0x001c;
        r8 = r6.database;	 Catch:{ Exception -> 0x00f4 }
        r8.beginTransaction();	 Catch:{ Exception -> 0x00f4 }
        r8 = r6.database;	 Catch:{ Exception -> 0x00f4 }
        r1 = "UPDATE users SET status = ? WHERE uid = ?";	 Catch:{ Exception -> 0x00f4 }
        r8 = r8.executeFast(r1);	 Catch:{ Exception -> 0x00f4 }
        r1 = r7.size();	 Catch:{ Exception -> 0x00f4 }
        r2 = 0;	 Catch:{ Exception -> 0x00f4 }
        if (r2 >= r1) goto L_0x0050;	 Catch:{ Exception -> 0x00f4 }
        r3 = r7.get(r2);	 Catch:{ Exception -> 0x00f4 }
        r3 = (org.telegram.tgnet.TLRPC.User) r3;	 Catch:{ Exception -> 0x00f4 }
        r8.requery();	 Catch:{ Exception -> 0x00f4 }
        r4 = r3.status;	 Catch:{ Exception -> 0x00f4 }
        r5 = 1;	 Catch:{ Exception -> 0x00f4 }
        if (r4 == 0) goto L_0x0041;	 Catch:{ Exception -> 0x00f4 }
        r4 = r3.status;	 Catch:{ Exception -> 0x00f4 }
        r4 = r4.expires;	 Catch:{ Exception -> 0x00f4 }
        r8.bindInteger(r5, r4);	 Catch:{ Exception -> 0x00f4 }
        goto L_0x0044;	 Catch:{ Exception -> 0x00f4 }
        r8.bindInteger(r5, r0);	 Catch:{ Exception -> 0x00f4 }
        r4 = 2;	 Catch:{ Exception -> 0x00f4 }
        r3 = r3.id;	 Catch:{ Exception -> 0x00f4 }
        r8.bindInteger(r4, r3);	 Catch:{ Exception -> 0x00f4 }
        r8.step();	 Catch:{ Exception -> 0x00f4 }
        r2 = r2 + 1;	 Catch:{ Exception -> 0x00f4 }
        goto L_0x0029;	 Catch:{ Exception -> 0x00f4 }
        r8.dispose();	 Catch:{ Exception -> 0x00f4 }
        if (r9 == 0) goto L_0x00f8;	 Catch:{ Exception -> 0x00f4 }
        r7 = r6.database;	 Catch:{ Exception -> 0x00f4 }
        r7.commitTransaction();	 Catch:{ Exception -> 0x00f4 }
        goto L_0x00f8;	 Catch:{ Exception -> 0x00f4 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00f4 }
        r8.<init>();	 Catch:{ Exception -> 0x00f4 }
        r1 = new android.util.SparseArray;	 Catch:{ Exception -> 0x00f4 }
        r1.<init>();	 Catch:{ Exception -> 0x00f4 }
        r2 = r7.size();	 Catch:{ Exception -> 0x00f4 }
        r3 = 0;	 Catch:{ Exception -> 0x00f4 }
        if (r3 >= r2) goto L_0x008b;	 Catch:{ Exception -> 0x00f4 }
        r4 = r7.get(r3);	 Catch:{ Exception -> 0x00f4 }
        r4 = (org.telegram.tgnet.TLRPC.User) r4;	 Catch:{ Exception -> 0x00f4 }
        r5 = r8.length();	 Catch:{ Exception -> 0x00f4 }
        if (r5 == 0) goto L_0x007e;	 Catch:{ Exception -> 0x00f4 }
        r5 = ",";	 Catch:{ Exception -> 0x00f4 }
        r8.append(r5);	 Catch:{ Exception -> 0x00f4 }
        r5 = r4.id;	 Catch:{ Exception -> 0x00f4 }
        r8.append(r5);	 Catch:{ Exception -> 0x00f4 }
        r5 = r4.id;	 Catch:{ Exception -> 0x00f4 }
        r1.put(r5, r4);	 Catch:{ Exception -> 0x00f4 }
        r3 = r3 + 1;	 Catch:{ Exception -> 0x00f4 }
        goto L_0x006b;	 Catch:{ Exception -> 0x00f4 }
        r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00f4 }
        r7.<init>();	 Catch:{ Exception -> 0x00f4 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x00f4 }
        r6.getUsersInternal(r8, r7);	 Catch:{ Exception -> 0x00f4 }
        r8 = r7.size();	 Catch:{ Exception -> 0x00f4 }
        if (r0 >= r8) goto L_0x00dc;	 Catch:{ Exception -> 0x00f4 }
        r2 = r7.get(r0);	 Catch:{ Exception -> 0x00f4 }
        r2 = (org.telegram.tgnet.TLRPC.User) r2;	 Catch:{ Exception -> 0x00f4 }
        r3 = r2.id;	 Catch:{ Exception -> 0x00f4 }
        r3 = r1.get(r3);	 Catch:{ Exception -> 0x00f4 }
        r3 = (org.telegram.tgnet.TLRPC.User) r3;	 Catch:{ Exception -> 0x00f4 }
        if (r3 == 0) goto L_0x00d9;	 Catch:{ Exception -> 0x00f4 }
        r4 = r3.first_name;	 Catch:{ Exception -> 0x00f4 }
        if (r4 == 0) goto L_0x00c8;	 Catch:{ Exception -> 0x00f4 }
        r4 = r3.last_name;	 Catch:{ Exception -> 0x00f4 }
        if (r4 == 0) goto L_0x00c8;	 Catch:{ Exception -> 0x00f4 }
        r4 = org.telegram.messenger.UserObject.isContact(r2);	 Catch:{ Exception -> 0x00f4 }
        if (r4 != 0) goto L_0x00c3;	 Catch:{ Exception -> 0x00f4 }
        r4 = r3.first_name;	 Catch:{ Exception -> 0x00f4 }
        r2.first_name = r4;	 Catch:{ Exception -> 0x00f4 }
        r4 = r3.last_name;	 Catch:{ Exception -> 0x00f4 }
        r2.last_name = r4;	 Catch:{ Exception -> 0x00f4 }
        r3 = r3.username;	 Catch:{ Exception -> 0x00f4 }
        r2.username = r3;	 Catch:{ Exception -> 0x00f4 }
        goto L_0x00d9;	 Catch:{ Exception -> 0x00f4 }
        r4 = r3.photo;	 Catch:{ Exception -> 0x00f4 }
        if (r4 == 0) goto L_0x00d1;	 Catch:{ Exception -> 0x00f4 }
        r3 = r3.photo;	 Catch:{ Exception -> 0x00f4 }
        r2.photo = r3;	 Catch:{ Exception -> 0x00f4 }
        goto L_0x00d9;	 Catch:{ Exception -> 0x00f4 }
        r4 = r3.phone;	 Catch:{ Exception -> 0x00f4 }
        if (r4 == 0) goto L_0x00d9;	 Catch:{ Exception -> 0x00f4 }
        r3 = r3.phone;	 Catch:{ Exception -> 0x00f4 }
        r2.phone = r3;	 Catch:{ Exception -> 0x00f4 }
        r0 = r0 + 1;	 Catch:{ Exception -> 0x00f4 }
        goto L_0x009b;	 Catch:{ Exception -> 0x00f4 }
        r8 = r7.isEmpty();	 Catch:{ Exception -> 0x00f4 }
        if (r8 != 0) goto L_0x00f8;	 Catch:{ Exception -> 0x00f4 }
        if (r9 == 0) goto L_0x00e9;	 Catch:{ Exception -> 0x00f4 }
        r8 = r6.database;	 Catch:{ Exception -> 0x00f4 }
        r8.beginTransaction();	 Catch:{ Exception -> 0x00f4 }
        r6.putUsersInternal(r7);	 Catch:{ Exception -> 0x00f4 }
        if (r9 == 0) goto L_0x00f8;	 Catch:{ Exception -> 0x00f4 }
        r7 = r6.database;	 Catch:{ Exception -> 0x00f4 }
        r7.commitTransaction();	 Catch:{ Exception -> 0x00f4 }
        goto L_0x00f8;
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
        return;
        r7 = new java.lang.RuntimeException;
        r8 = "wrong db thread";
        r7.<init>(r8);
        throw r7;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateUsersInternal(java.util.ArrayList, boolean, boolean):void");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:154:0x0257 in {11, 12, 21, 24, 25, 28, 31, 36, 39, 40, 44, 46, 47, 49, 51, 53, 55, 59, 60, 70, 71, 75, 76, 78, 80, 81, 83, 84, 86, 88, 90, 91, 92, 93, 97, 101, 102, 111, 114, 115, 118, 121, 126, 128, 130, 132, 134, 136, 137, 141, 143, 145, 146, 148, 149, 150, 152, 153} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$getCachedPhoneBook$88$MessagesStorage(boolean r27) {
        /*
        r26 = this;
        r1 = r26;
        r2 = "";
        r3 = 6;
        r4 = 3;
        r5 = 2;
        r6 = 5;
        r7 = 4;
        r9 = 1;
        r11 = 8;
        r12 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r13 = 0;
        r0 = r1.database;	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        r14 = "SELECT name FROM sqlite_master WHERE type='table' AND name='user_contacts_v6'";	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        r15 = new java.lang.Object[r13];	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        r14 = r0.queryFinalized(r14, r15);	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        r0 = r14.next();	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r14.dispose();	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        if (r0 == 0) goto L_0x00db;
        r0 = r1.database;	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        r14 = "SELECT COUNT(uid) FROM user_contacts_v6 WHERE 1";	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        r15 = new java.lang.Object[r13];	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        r14 = r0.queryFinalized(r14, r15);	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        r0 = r14.next();	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        if (r0 == 0) goto L_0x003b;	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r0 = r14.intValue(r13);	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r0 = java.lang.Math.min(r12, r0);	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        goto L_0x003d;	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r0 = 16;	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r14.dispose();	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r15 = new android.util.SparseArray;	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r15.<init>(r0);	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r0 = r1.database;	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r8 = "SELECT us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v6 as us LEFT JOIN user_phones_v6 as up ON us.uid = up.uid WHERE 1";	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r10 = new java.lang.Object[r13];	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r10 = r0.queryFinalized(r8, r10);	 Catch:{ Throwable -> 0x00e2, all -> 0x00de }
        r0 = r10.next();	 Catch:{ Throwable -> 0x00d9 }
        if (r0 == 0) goto L_0x00cc;	 Catch:{ Throwable -> 0x00d9 }
        r0 = r10.intValue(r13);	 Catch:{ Throwable -> 0x00d9 }
        r8 = r15.get(r0);	 Catch:{ Throwable -> 0x00d9 }
        r8 = (org.telegram.messenger.ContactsController.Contact) r8;	 Catch:{ Throwable -> 0x00d9 }
        if (r8 != 0) goto L_0x0089;	 Catch:{ Throwable -> 0x00d9 }
        r8 = new org.telegram.messenger.ContactsController$Contact;	 Catch:{ Throwable -> 0x00d9 }
        r8.<init>();	 Catch:{ Throwable -> 0x00d9 }
        r14 = r10.stringValue(r9);	 Catch:{ Throwable -> 0x00d9 }
        r8.first_name = r14;	 Catch:{ Throwable -> 0x00d9 }
        r14 = r10.stringValue(r5);	 Catch:{ Throwable -> 0x00d9 }
        r8.last_name = r14;	 Catch:{ Throwable -> 0x00d9 }
        r14 = r10.intValue(r3);	 Catch:{ Throwable -> 0x00d9 }
        r8.imported = r14;	 Catch:{ Throwable -> 0x00d9 }
        r14 = r8.first_name;	 Catch:{ Throwable -> 0x00d9 }
        if (r14 != 0) goto L_0x007e;	 Catch:{ Throwable -> 0x00d9 }
        r8.first_name = r2;	 Catch:{ Throwable -> 0x00d9 }
        r14 = r8.last_name;	 Catch:{ Throwable -> 0x00d9 }
        if (r14 != 0) goto L_0x0084;	 Catch:{ Throwable -> 0x00d9 }
        r8.last_name = r2;	 Catch:{ Throwable -> 0x00d9 }
        r8.contact_id = r0;	 Catch:{ Throwable -> 0x00d9 }
        r15.put(r0, r8);	 Catch:{ Throwable -> 0x00d9 }
        r0 = r10.stringValue(r4);	 Catch:{ Throwable -> 0x00d9 }
        if (r0 != 0) goto L_0x0090;	 Catch:{ Throwable -> 0x00d9 }
        goto L_0x004f;	 Catch:{ Throwable -> 0x00d9 }
        r14 = r8.phones;	 Catch:{ Throwable -> 0x00d9 }
        r14.add(r0);	 Catch:{ Throwable -> 0x00d9 }
        r14 = r10.stringValue(r7);	 Catch:{ Throwable -> 0x00d9 }
        if (r14 != 0) goto L_0x009c;	 Catch:{ Throwable -> 0x00d9 }
        goto L_0x004f;	 Catch:{ Throwable -> 0x00d9 }
        r3 = r14.length();	 Catch:{ Throwable -> 0x00d9 }
        if (r3 != r11) goto L_0x00ac;	 Catch:{ Throwable -> 0x00d9 }
        r3 = r0.length();	 Catch:{ Throwable -> 0x00d9 }
        if (r3 == r11) goto L_0x00ac;	 Catch:{ Throwable -> 0x00d9 }
        r14 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0);	 Catch:{ Throwable -> 0x00d9 }
        r0 = r8.shortPhones;	 Catch:{ Throwable -> 0x00d9 }
        r0.add(r14);	 Catch:{ Throwable -> 0x00d9 }
        r0 = r8.phoneDeleted;	 Catch:{ Throwable -> 0x00d9 }
        r3 = r10.intValue(r6);	 Catch:{ Throwable -> 0x00d9 }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Throwable -> 0x00d9 }
        r0.add(r3);	 Catch:{ Throwable -> 0x00d9 }
        r0 = r8.phoneTypes;	 Catch:{ Throwable -> 0x00d9 }
        r0.add(r2);	 Catch:{ Throwable -> 0x00d9 }
        r0 = r15.size();	 Catch:{ Throwable -> 0x00d9 }
        if (r0 != r12) goto L_0x00ca;	 Catch:{ Throwable -> 0x00d9 }
        goto L_0x00cc;	 Catch:{ Throwable -> 0x00d9 }
        r3 = 6;	 Catch:{ Throwable -> 0x00d9 }
        goto L_0x004f;	 Catch:{ Throwable -> 0x00d9 }
        r10.dispose();	 Catch:{ Throwable -> 0x00d9 }
        r0 = r1.currentAccount;	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        r0 = org.telegram.messenger.ContactsController.getInstance(r0);	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        r0.migratePhoneBookToV7(r15);	 Catch:{ Throwable -> 0x00e9, all -> 0x00e5 }
        return;
        r0 = move-exception;
        goto L_0x00eb;
        r17 = 0;
        goto L_0x00f5;
        r0 = move-exception;
        r10 = r14;
        goto L_0x0251;
        r0 = move-exception;
        r10 = r14;
        goto L_0x00eb;
        r0 = move-exception;
        r10 = 0;
        goto L_0x0251;
        r0 = move-exception;
        r10 = 0;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0250 }
        if (r10 == 0) goto L_0x00f3;
        r10.dispose();
        r17 = r10;
        r0 = r1.database;	 Catch:{ Throwable -> 0x0156 }
        r3 = "SELECT COUNT(key) FROM user_contacts_v7 WHERE 1";	 Catch:{ Throwable -> 0x0156 }
        r8 = new java.lang.Object[r13];	 Catch:{ Throwable -> 0x0156 }
        r3 = r0.queryFinalized(r3, r8);	 Catch:{ Throwable -> 0x0156 }
        r0 = r3.next();	 Catch:{ Throwable -> 0x014d, all -> 0x014a }
        if (r0 == 0) goto L_0x013e;	 Catch:{ Throwable -> 0x014d, all -> 0x014a }
        r8 = r3.intValue(r13);	 Catch:{ Throwable -> 0x014d, all -> 0x014a }
        r10 = java.lang.Math.min(r12, r8);	 Catch:{ Throwable -> 0x013a, all -> 0x014a }
        if (r8 <= r12) goto L_0x0113;
        r0 = r8 + -5000;
        r14 = r0;
        goto L_0x0114;
        r14 = 0;
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        if (r0 == 0) goto L_0x0131;	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        r0 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        r0.<init>();	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        r15 = r1.currentAccount;	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        r0.append(r15);	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        r15 = " current cached contacts count = ";	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        r0.append(r15);	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        r0.append(r8);	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        r0 = r0.toString();	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        org.telegram.messenger.FileLog.d(r0);	 Catch:{ Throwable -> 0x0134, all -> 0x014a }
        r16 = r10;
        goto L_0x0142;
        r0 = move-exception;
        r17 = r3;
        r16 = r10;
        goto L_0x015b;
        r0 = move-exception;
        r17 = r3;
        goto L_0x0158;
        r8 = 0;
        r14 = 0;
        r16 = 16;
        if (r3 == 0) goto L_0x0147;
        r3.dispose();
        r17 = r3;
        goto L_0x0163;
        r0 = move-exception;
        goto L_0x024a;
        r0 = move-exception;
        r17 = r3;
        goto L_0x0157;
        r0 = move-exception;
        r3 = r17;
        goto L_0x024a;
        r0 = move-exception;
        r8 = 0;
        r14 = 0;
        r16 = 16;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0151 }
        if (r17 == 0) goto L_0x0163;
        r17.dispose();
        r0 = r16;
        r3 = new java.util.HashMap;
        r3.<init>(r0);
        if (r14 == 0) goto L_0x0186;
        r0 = r1.database;	 Catch:{ Exception -> 0x0220 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0220 }
        r10.<init>();	 Catch:{ Exception -> 0x0220 }
        r14 = "SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1 LIMIT 0,";	 Catch:{ Exception -> 0x0220 }
        r10.append(r14);	 Catch:{ Exception -> 0x0220 }
        r10.append(r8);	 Catch:{ Exception -> 0x0220 }
        r8 = r10.toString();	 Catch:{ Exception -> 0x0220 }
        r10 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0220 }
        r0 = r0.queryFinalized(r8, r10);	 Catch:{ Exception -> 0x0220 }
        goto L_0x0190;	 Catch:{ Exception -> 0x0220 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0220 }
        r8 = "SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1";	 Catch:{ Exception -> 0x0220 }
        r10 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0220 }
        r0 = r0.queryFinalized(r8, r10);	 Catch:{ Exception -> 0x0220 }
        r8 = r0;
        r0 = r8.next();	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        if (r0 == 0) goto L_0x0212;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r0 = r8.stringValue(r13);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r10 = r3.get(r0);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r10 = (org.telegram.messenger.ContactsController.Contact) r10;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        if (r10 != 0) goto L_0x01d0;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r10 = new org.telegram.messenger.ContactsController$Contact;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r10.<init>();	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = r8.intValue(r9);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r10.contact_id = r14;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = r8.stringValue(r5);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r10.first_name = r14;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = r8.stringValue(r4);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r10.last_name = r14;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = 7;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = r8.intValue(r14);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r10.imported = r14;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = r10.first_name;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        if (r14 != 0) goto L_0x01c7;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r10.first_name = r2;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = r10.last_name;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        if (r14 != 0) goto L_0x01cd;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r10.last_name = r2;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r3.put(r0, r10);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r0 = r8.stringValue(r7);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        if (r0 != 0) goto L_0x01d8;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = 6;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        goto L_0x0191;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = r10.phones;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14.add(r0);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = r8.stringValue(r6);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        if (r14 != 0) goto L_0x01e4;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        goto L_0x01d6;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r15 = r14.length();	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        if (r15 != r11) goto L_0x01f4;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r15 = r0.length();	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        if (r15 == r11) goto L_0x01f4;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r0 = r10.shortPhones;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r0.add(r14);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r0 = r10.phoneDeleted;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r14 = 6;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r15 = r8.intValue(r14);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r15 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r0.add(r15);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r0 = r10.phoneTypes;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r0.add(r2);	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r0 = r3.size();	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        if (r0 != r12) goto L_0x0191;	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        r8.dispose();	 Catch:{ Exception -> 0x0218, all -> 0x0216 }
        goto L_0x022c;
        r0 = move-exception;
        goto L_0x0244;
        r0 = move-exception;
        r17 = r8;
        goto L_0x0221;
        r0 = move-exception;
        r8 = r17;
        goto L_0x0244;
        r0 = move-exception;
        r3.clear();	 Catch:{ all -> 0x021c }
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x021c }
        if (r17 == 0) goto L_0x022c;
        r17.dispose();
        r0 = r1.currentAccount;
        r18 = org.telegram.messenger.ContactsController.getInstance(r0);
        r20 = 1;
        r21 = 1;
        r22 = 0;
        r23 = 0;
        r24 = r27 ^ 1;
        r25 = 0;
        r19 = r3;
        r18.performSyncPhoneBook(r19, r20, r21, r22, r23, r24, r25);
        return;
        if (r8 == 0) goto L_0x0249;
        r8.dispose();
        throw r0;
        if (r3 == 0) goto L_0x024f;
        r3.dispose();
        throw r0;
        r0 = move-exception;
        if (r10 == 0) goto L_0x0256;
        r10.dispose();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getCachedPhoneBook$88$MessagesStorage(boolean):void");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:960:0x156d in {2, 3, 7, 10, 11, 35, 37, 38, 40, 41, 43, 44, 46, 47, 49, 50, 51, 52, 54, 55, 56, 57, 58, 61, 63, 65, 67, 69, 95, 96, 97, 99, 101, 103, 105, 107, 109, 110, 127, 129, 131, 132, 141, 143, 144, 145, 147, 149, 151, 153, 155, 156, 158, 159, 167, 168, 178, 179, 180, 181, 183, 184, 185, 186, 188, 190, 193, 194, 202, 203, 205, 207, 209, 211, 212, 213, 215, 216, 217, 219, 221, 222, 225, 226, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 251, 252, 255, 257, 258, 260, 261, 263, 265, 272, 275, 282, 283, 291, 292, 295, 296, 304, 305, 308, 309, 310, 311, 315, 316, 322, 323, 326, 327, 328, 330, 332, 338, 339, 353, 355, 357, 358, 365, 366, 371, 374, 379, 386, 387, 389, 391, 393, 394, 395, 397, 398, 399, 400, 401, 403, 404, 412, 413, 415, 417, 418, 424, 425, 430, 431, 436, 437, 442, 443, 444, 446, 448, 456, 457, 464, 466, 468, 469, 470, 472, 474, 475, 476, 477, 479, 480, 481, 483, 484, 485, 487, 489, 491, 493, 495, 496, 497, 498, 499, 501, 502, 503, 504, 505, 513, 514, 519, 520, 528, 529, 531, 533, 535, 536, 538, 539, 541, 542, 544, 545, 547, 548, 550, 552, 553, 554, 558, 561, 562, 563, 565, 567, 570, 571, 577, 578, 582, 587, 588, 589, 592, 593, 596, 597, 598, 599, 601, 603, 609, 610, 618, 642, 644, 645, 647, 648, 654, 656, 658, 659, 664, 672, 676, 683, 692, 693, 695, 697, 709, 710, 719, 720, 723, 726, 727, 730, 733, 734, 735, 740, 741, 754, 756, 758, 760, 762, 765, 766, 767, 769, 771, 773, 774, 776, 777, 779, 781, 783, 785, 786, 787, 789, 791, 793, 794, 795, 796, 797, 799, 800, 801, 802, 804, 805, 806, 807, 808, 809, 821, 828, 829, 832, 835, 843, 844, 845, 859, 860, 861, 869, 870, 871, 872, 873, 874, 884, 885, 893, 894, 895, 898, 900, 902, 903, 907, 910, 911, 913, 915, 917, 918, 920, 921, 923, 925, 927, 928, 930, 931, 933, 934, 935, 936, 938, 939, 940, 941, 943, 944, 945, 947, 948, 949, 950, 953, 955, 957, 959} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$getMessages$96$MessagesStorage(int r38, int r39, boolean r40, long r41, int r43, int r44, int r45, int r46, int r47) {
        /*
        r37 = this;
        r1 = r37;
        r2 = r38;
        r3 = r39;
        r4 = r41;
        r15 = r43;
        r6 = r1.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.clientUserId;
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_messages;
        r7.<init>();
        r8 = (long) r3;
        if (r40 == 0) goto L_0x001d;
        r11 = (int) r4;
        r11 = -r11;
        goto L_0x001e;
        r11 = 0;
        r12 = 32;
        r13 = 0;
        r16 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1));
        if (r16 == 0) goto L_0x002b;
        if (r11 == 0) goto L_0x002b;
        r13 = (long) r11;
        r13 = r13 << r12;
        r8 = r8 | r13;
        r13 = r8;
        r8 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r19 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r19 != 0) goto L_0x0036;
        r8 = 10;
        goto L_0x0037;
        r8 = 1;
        r9 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r9.<init>();	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r12 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r12.<init>();	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r10.<init>();	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r21 = r10;	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r10 = new android.util.SparseArray;	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r10.<init>();	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r22 = r10;	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r10 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r10.<init>();	 Catch:{ Exception -> 0x1512, all -> 0x14fe }
        r23 = r10;
        r10 = (int) r4;
        r24 = r9;
        if (r10 == 0) goto L_0x0bb7;
        r9 = "SELECT inbox_max, unread_count, date, unread_count_i FROM dialogs WHERE did = ";
        r26 = r12;
        r12 = 3;
        if (r15 != r12) goto L_0x0150;
        if (r44 != 0) goto L_0x0150;
        r8 = r1.database;	 Catch:{ Exception -> 0x013e, all -> 0x012b }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x013e, all -> 0x012b }
        r12.<init>();	 Catch:{ Exception -> 0x013e, all -> 0x012b }
        r12.append(r9);	 Catch:{ Exception -> 0x013e, all -> 0x012b }
        r12.append(r4);	 Catch:{ Exception -> 0x013e, all -> 0x012b }
        r9 = r12.toString();	 Catch:{ Exception -> 0x013e, all -> 0x012b }
        r27 = r7;
        r12 = 0;
        r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r7 = r8.queryFinalized(r9, r7);	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r8 = r7.next();	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        if (r8 == 0) goto L_0x00e8;	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r8 = r7.intValue(r12);	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r9 = 1;
        r8 = r8 + r9;
        r12 = r7.intValue(r9);	 Catch:{ Exception -> 0x00da, all -> 0x00ca }
        r9 = 2;
        r28 = r7.intValue(r9);	 Catch:{ Exception -> 0x00c0, all -> 0x00b4 }
        r9 = 3;
        r29 = r7.intValue(r9);	 Catch:{ Exception -> 0x00a8, all -> 0x009a }
        goto L_0x00ee;
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
        r14 = r28;
        r12 = 0;
        goto L_0x00d6;
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
        r14 = r28;
        r12 = 0;
        goto L_0x00e4;
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
        r12 = 0;
        goto L_0x00d5;
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
        r12 = 0;
        goto L_0x00e3;
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r11 = r27;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
        goto L_0x0c8b;
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r11 = r27;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
        goto L_0x0c9e;
        r8 = 0;
        r12 = 0;
        r28 = 0;
        r29 = 0;
        r7.dispose();	 Catch:{ Exception -> 0x0118, all -> 0x0103 }
        r31 = r3;
        r36 = r6;
        r33 = r8;
        r30 = r29;
        r6 = 0;
        r32 = 0;
        r29 = r28;
        r28 = r10;
        r10 = r12;
        goto L_0x0425;
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
        r14 = r28;
        r20 = r29;
        r12 = 0;
        r17 = 0;
        r19 = 0;
        goto L_0x1553;
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
        r14 = r28;
        r20 = r29;
        r12 = 0;
        r17 = 0;
        r19 = 0;
        goto L_0x1523;
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r11 = r7;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
        r19 = 0;
        r20 = 0;
        r33 = 0;
        r7 = r3;
        goto L_0x1553;
        r0 = move-exception;
        r6 = r2;
        r11 = r7;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
        r19 = 0;
        r20 = 0;
        r33 = 0;
        r2 = r0;
        r7 = r3;
        goto L_0x1524;
        r27 = r7;
        r7 = 1;
        if (r15 == r7) goto L_0x0415;
        r7 = 3;
        if (r15 == r7) goto L_0x0415;
        r7 = 4;
        if (r15 == r7) goto L_0x0415;
        if (r44 != 0) goto L_0x0415;
        r7 = 2;
        if (r15 != r7) goto L_0x03df;
        r7 = r1.database;	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r12.<init>();	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r12.append(r9);	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r12.append(r4);	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r9 = r12.toString();	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r28 = r10;	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r12 = 0;	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r10 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r7 = r7.queryFinalized(r9, r10);	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r9 = r7.next();	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        if (r9 == 0) goto L_0x01fd;	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r9 = r7.intValue(r12);	 Catch:{ Exception -> 0x03d8, all -> 0x03cf }
        r13 = (long) r9;
        r10 = 1;
        r12 = r7.intValue(r10);	 Catch:{ Exception -> 0x01f2, all -> 0x01e5 }
        r10 = 2;
        r29 = r7.intValue(r10);	 Catch:{ Exception -> 0x01d9, all -> 0x01cb }
        r10 = 3;
        r30 = r7.intValue(r10);	 Catch:{ Exception -> 0x01bd, all -> 0x01ad }
        r16 = 0;
        r10 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1));
        if (r10 == 0) goto L_0x01a5;
        if (r11 == 0) goto L_0x01a5;
        r31 = r9;
        r9 = (long) r11;
        r18 = 32;
        r9 = r9 << r18;
        r13 = r13 | r9;
        goto L_0x01a7;
        r31 = r9;
        r10 = r12;
        r12 = r31;
        r9 = 1;
        goto L_0x0206;
        r0 = move-exception;
        r31 = r9;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r33 = r31;
        goto L_0x00a6;
        r0 = move-exception;
        r31 = r9;
        r6 = r2;
        r7 = r3;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r33 = r31;
        goto L_0x00b2;
        r0 = move-exception;
        r31 = r9;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r13 = r12;
        r11 = r27;
        r33 = r31;
        goto L_0x00be;
        r0 = move-exception;
        r31 = r9;
        r6 = r2;
        r7 = r3;
        r13 = r12;
        r11 = r27;
        r33 = r31;
        goto L_0x00c8;
        r0 = move-exception;
        r31 = r9;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r11 = r27;
        r33 = r31;
        goto L_0x00d3;
        r0 = move-exception;
        r31 = r9;
        r6 = r2;
        r7 = r3;
        r11 = r27;
        r33 = r31;
        goto L_0x00e1;
        r31 = r3;
        r9 = 0;
        r10 = 0;
        r12 = 0;
        r29 = 0;
        r30 = 0;
        r7.dispose();	 Catch:{ Exception -> 0x03c5, all -> 0x03b9 }
        if (r9 != 0) goto L_0x02ab;
        r7 = r1.database;	 Catch:{ Exception -> 0x02a4, all -> 0x029d }
        r32 = r9;
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x029b, all -> 0x0299 }
        r33 = r12;
        r12 = "SELECT min(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0";	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r34 = r13;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r13 = 1;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r13 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r36 = r6;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r6 = 0;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r14[r6] = r13;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r9 = java.lang.String.format(r9, r12, r14);	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r12 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r7 = r7.queryFinalized(r9, r12);	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r9 = r7.next();	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        if (r9 == 0) goto L_0x024f;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r9 = r7.intValue(r6);	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r6 = 1;
        r12 = r7.intValue(r6);	 Catch:{ Exception -> 0x0248, all -> 0x023f }
        r33 = r9;
        goto L_0x0251;
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r33 = r9;
        goto L_0x040c;
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r33 = r9;
        goto L_0x0412;
        r12 = r29;
        r7.dispose();	 Catch:{ Exception -> 0x0294, all -> 0x028f }
        if (r33 == 0) goto L_0x0289;	 Catch:{ Exception -> 0x0294, all -> 0x028f }
        r6 = r1.database;	 Catch:{ Exception -> 0x0294, all -> 0x028f }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0294, all -> 0x028f }
        r9 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid >= %d AND out = 0 AND read_state IN(0,2)";	 Catch:{ Exception -> 0x0294, all -> 0x028f }
        r13 = 2;	 Catch:{ Exception -> 0x0294, all -> 0x028f }
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0294, all -> 0x028f }
        r13 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0294, all -> 0x028f }
        r29 = r12;
        r12 = 0;
        r14[r12] = r13;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r13 = java.lang.Integer.valueOf(r33);	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r19 = 1;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r14[r19] = r13;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r7 = java.lang.String.format(r7, r9, r14);	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r9 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r6 = r6.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r7 = r6.next();	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        if (r7 == 0) goto L_0x0285;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r7 = r6.intValue(r12);	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r10 = r7;	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        r6.dispose();	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        goto L_0x028b;
        r29 = r12;
        r13 = r34;
        goto L_0x03ee;
        r0 = move-exception;
        r29 = r12;
        goto L_0x0408;
        r0 = move-exception;
        r29 = r12;
        goto L_0x0410;
        r0 = move-exception;
        goto L_0x02a0;
        r0 = move-exception;
        goto L_0x02a7;
        r0 = move-exception;
        r32 = r9;
        r33 = r12;
        goto L_0x0408;
        r0 = move-exception;
        r32 = r9;
        r33 = r12;
        goto L_0x0410;
        r36 = r6;
        r32 = r9;
        r33 = r12;
        r34 = r13;
        if (r31 != 0) goto L_0x0336;
        r6 = r1.database;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r9 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid > 0 AND out = 0 AND read_state IN(0,2)";	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r12 = 1;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r14 = 0;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r13[r14] = r12;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r7 = java.lang.String.format(r7, r9, r13);	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r9 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r6 = r6.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r7 = r6.next();	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        if (r7 == 0) goto L_0x02da;
        r7 = r6.intValue(r14);	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        goto L_0x02db;
        r7 = 0;
        r6.dispose();	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        if (r7 != r10) goto L_0x0324;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r6 = r1.database;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r9 = "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0";	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r12 = 1;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r14 = 0;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r13[r14] = r12;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r7 = java.lang.String.format(r7, r9, r13);	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r9 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r6 = r6.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r7 = r6.next();	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        if (r7 == 0) goto L_0x0319;	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r7 = r6.intValue(r14);	 Catch:{ Exception -> 0x0333, all -> 0x0330 }
        r13 = (long) r7;
        r16 = 0;
        r9 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1));
        if (r9 == 0) goto L_0x0315;
        if (r11 == 0) goto L_0x0315;
        r12 = r10;
        r9 = (long) r11;
        r18 = 32;
        r9 = r9 << r18;
        r13 = r13 | r9;
        goto L_0x0316;
        r12 = r10;
        r33 = r7;
        goto L_0x031e;
        r12 = r10;
        r7 = r31;
        r13 = r34;
        r6.dispose();	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r34 = r13;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        goto L_0x0327;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r12 = r10;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = r31;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r10 = r33;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r31 = r7;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r33 = r10;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r10 = r12;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        goto L_0x028b;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r0 = move-exception;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        goto L_0x03be;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r0 = move-exception;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        goto L_0x03ca;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r12 = r10;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r6 = r1.database;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r9 = "SELECT start, end FROM messages_holes WHERE uid = %d AND start < %d AND end > %d";	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r10 = 3;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r13 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r10 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r14 = 0;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r13[r14] = r10;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r10 = java.lang.Integer.valueOf(r31);	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r14 = 1;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r13[r14] = r10;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r10 = java.lang.Integer.valueOf(r31);	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r14 = 2;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r13[r14] = r10;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = java.lang.String.format(r7, r9, r13);	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r9 = 0;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r6 = r6.queryFinalized(r7, r10);	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = r6.next();	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        if (r7 != 0) goto L_0x0368;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = 1;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        goto L_0x0369;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = 0;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r6.dispose();	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        if (r7 == 0) goto L_0x032d;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r6 = r1.database;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r9 = "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > %d";	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r10 = 2;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r13 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r10 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r14 = 0;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r13[r14] = r10;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r10 = java.lang.Integer.valueOf(r31);	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r19 = 1;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r13[r19] = r10;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = java.lang.String.format(r7, r9, r13);	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r9 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r6 = r6.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = r6.next();	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        if (r7 == 0) goto L_0x03aa;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = r6.intValue(r14);	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r13 = (long) r7;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r9 = 0;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r31 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1));	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        if (r31 == 0) goto L_0x03ae;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        if (r11 == 0) goto L_0x03ae;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r9 = (long) r11;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r18 = 32;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r9 = r9 << r18;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r13 = r13 | r9;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        goto L_0x03ae;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r7 = r31;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r13 = r34;	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r6.dispose();	 Catch:{ Exception -> 0x03b7, all -> 0x03b5 }
        r31 = r7;
        r10 = r12;
        goto L_0x03ee;
        r0 = move-exception;
        goto L_0x03bf;
        r0 = move-exception;
        goto L_0x03cb;
        r0 = move-exception;
        r32 = r9;
        r33 = r12;
        r12 = r10;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        goto L_0x0b96;
        r0 = move-exception;
        r32 = r9;
        r33 = r12;
        r12 = r10;
        r6 = r2;
        r7 = r3;
        goto L_0x0ba9;
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r11 = r27;
        goto L_0x1506;
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r11 = r27;
        goto L_0x1518;
        r36 = r6;
        r28 = r10;
        r31 = r3;
        r10 = 0;
        r29 = 0;
        r30 = 0;
        r32 = 0;
        r33 = 0;
        if (r2 > r10) goto L_0x03f8;
        if (r10 >= r8) goto L_0x03f3;
        goto L_0x03f8;
        r6 = r10 - r2;
        r2 = r2 + 10;
        goto L_0x0425;
        r6 = r10 + 10;
        r2 = java.lang.Math.max(r2, r6);	 Catch:{ Exception -> 0x040f, all -> 0x0407 }
        if (r10 >= r8) goto L_0x0405;
        r6 = 0;
        r10 = 0;
        r13 = 0;
        goto L_0x0421;
        r6 = 0;
        goto L_0x0425;
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r13 = r10;
        goto L_0x0b97;
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r13 = r10;
        goto L_0x0baa;
        r36 = r6;
        r28 = r10;
        r31 = r3;
        r6 = 0;
        r10 = 0;
        r29 = 0;
        r30 = 0;
        r32 = 0;
        r33 = 0;
        r7 = r1.database;	 Catch:{ Exception -> 0x0ba4, all -> 0x0b8f }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0ba4, all -> 0x0b8f }
        r9 = "SELECT start FROM messages_holes WHERE uid = %d AND start IN (0, 1)";	 Catch:{ Exception -> 0x0ba4, all -> 0x0b8f }
        r12 = 1;	 Catch:{ Exception -> 0x0ba4, all -> 0x0b8f }
        r3 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0ba4, all -> 0x0b8f }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0ba4, all -> 0x0b8f }
        r34 = r10;
        r10 = 0;
        r3[r10] = r12;	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r3 = java.lang.String.format(r8, r9, r3);	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r8 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r3 = r7.queryFinalized(r3, r8);	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r7 = r3.next();	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        if (r7 == 0) goto L_0x04a1;
        r7 = r3.intValue(r10);	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r8 = 1;
        if (r7 != r8) goto L_0x0450;
        r10 = 1;
        goto L_0x0451;
        r10 = 0;
        r3.dispose();	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = 3;
        goto L_0x04ed;
        r0 = move-exception;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        r12 = 0;
        goto L_0x1553;
        r0 = move-exception;
        r7 = r39;
        r6 = r2;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        r12 = 0;
        goto L_0x1523;
        r0 = move-exception;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x0b9f;
        r0 = move-exception;
        r7 = r39;
        r6 = r2;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x0bb2;
        r3.dispose();	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r3 = r1.database;	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r8 = "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0";	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r9 = 1;	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r12 = 0;	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r10[r12] = r9;	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r7 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r8 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r3 = r3.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r7 = r3.next();	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        if (r7 == 0) goto L_0x04e8;
        r7 = r3.intValue(r12);	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        if (r7 == 0) goto L_0x04e8;	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r8 = r1.database;	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r9 = "REPLACE INTO messages_holes VALUES(?, ?, ?)";	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r8 = r8.executeFast(r9);	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r8.requery();	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r9 = 1;	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r8.bindLong(r9, r4);	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r9 = 2;	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r10 = 0;	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r8.bindInteger(r9, r10);	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r9 = 3;	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r8.bindInteger(r9, r7);	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r8.step();	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r8.dispose();	 Catch:{ Exception -> 0x0491, all -> 0x047f }
        r3.dispose();	 Catch:{ Exception -> 0x0b8b, all -> 0x0b87 }
        r3 = 3;
        r10 = 0;
        if (r15 == r3) goto L_0x0792;
        r3 = 4;
        if (r15 == r3) goto L_0x0792;
        if (r32 == 0) goto L_0x04f9;
        r3 = 2;
        if (r15 != r3) goto L_0x04fa;
        goto L_0x0792;
        r3 = 2;
        r7 = 1;
        if (r15 != r7) goto L_0x05a9;
        r6 = r1.database;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = "SELECT start, end FROM messages_holes WHERE uid = %d AND start >= %d AND start != 1 AND end != 1 ORDER BY start ASC LIMIT 1";	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r12] = r3;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = java.lang.Integer.valueOf(r39);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r19 = 1;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r19] = r3;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = java.lang.String.format(r7, r8, r9);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r6.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = r3.next();	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        if (r6 == 0) goto L_0x0531;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = r3.intValue(r12);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = (long) r6;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        if (r11 == 0) goto L_0x0533;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = (long) r11;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 32;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = r8 << r11;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = r6 | r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        goto L_0x0533;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3.dispose();	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        if (r3 == 0) goto L_0x0577;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r1.database;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d";	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 5;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r20 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r20] = r11;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r19 = 1;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r19] = r11;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r13 = 2;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r13] = r11;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = 3;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r7] = r6;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = 4;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r7] = r6;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.String.format(r8, r9, r12);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        goto L_0x068c;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r1.database;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d";	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = 4;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 1;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 2;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 3;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        goto L_0x068c;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        if (r44 == 0) goto L_0x069f;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1));	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        if (r3 == 0) goto L_0x065c;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r1.database;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = 2;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r12] = r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.lang.Integer.valueOf(r39);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r19 = 1;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r19] = r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r3.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = r3.next();	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        if (r6 == 0) goto L_0x05e6;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = r3.intValue(r12);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = (long) r6;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        if (r11 == 0) goto L_0x05e8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = (long) r11;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 32;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = r8 << r11;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = r6 | r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        goto L_0x05e8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3.dispose();	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        if (r3 == 0) goto L_0x062b;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r1.database;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d";	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 5;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r20 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r20] = r11;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r19 = 1;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r19] = r11;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r13 = 2;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r13] = r11;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = 3;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r7] = r6;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = 4;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r7] = r6;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.String.format(r8, r9, r12);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        goto L_0x068c;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r1.database;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d ORDER BY m.date DESC, m.mid DESC LIMIT %d";	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = 4;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 1;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 2;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = 3;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        goto L_0x068c;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r1.database;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = 4;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11[r12] = r9;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12 = 1;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11[r12] = r9;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = 2;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11[r9] = r6;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = 3;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11[r9] = r6;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = java.lang.String.format(r7, r8, r11);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = r39;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r6 = r2;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r35 = r10;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r14 = r29;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r2 = r30;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = r31;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r11 = r33;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r13 = r34;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r10 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        goto L_0x0ed2;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r1.database;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0";	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = 1;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r13 = 0;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r12[r13] = r9;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = java.lang.String.format(r7, r8, r12);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r8 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r3 = r3.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = r3.next();	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        if (r7 == 0) goto L_0x06c4;	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        r7 = r3.intValue(r13);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        goto L_0x06c5;
        r7 = 0;
        r3.dispose();	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r3 = r1.database;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r9 = "SELECT max(end) FROM messages_holes WHERE uid = %d";	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12 = 1;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r14 = 0;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r13[r14] = r12;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = java.lang.String.format(r8, r9, r13);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r9 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r3 = r3.queryFinalized(r8, r9);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = r3.next();	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        if (r8 == 0) goto L_0x06f5;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = r3.intValue(r14);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r13 = (long) r8;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        if (r11 == 0) goto L_0x06f7;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = (long) r11;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r11 = 32;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = r8 << r11;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r13 = r13 | r8;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        goto L_0x06f7;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r13 = 0;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r3.dispose();	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = 0;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r3 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        if (r3 == 0) goto L_0x0732;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r3 = r1.database;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r9 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r11 = 4;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r20 = 0;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12[r20] = r11;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r13 = 1;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12[r13] = r11;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r11 = 2;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12[r11] = r6;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r11 = 3;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12[r11] = r6;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r6 = java.lang.String.format(r8, r9, r12);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = 0;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        goto L_0x075b;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r3 = r1.database;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r9 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r11 = 3;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r13 = 0;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12[r13] = r11;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r11 = 1;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12[r11] = r6;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r11 = 2;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r12[r11] = r6;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r6 = java.lang.String.format(r8, r9, r12);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r8 = 0;	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0781, all -> 0x076e }
        r6 = r2;
        r12 = r7;
        r35 = r10;
        r14 = r29;
        r2 = r30;
        r8 = r31;
        r11 = r33;
        r13 = r34;
        r10 = 0;
        r7 = r39;
        goto L_0x0ed2;
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r12 = r7;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x14e6;
        r0 = move-exception;
        r6 = r2;
        r12 = r7;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x14fb;
        r3 = r1.database;	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r7 = "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0";	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r8 = 1;	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r12 = 0;	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r9[r12] = r8;	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r6 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r3 = r3.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        r6 = r3.next();	 Catch:{ Exception -> 0x0b72, all -> 0x0b5b }
        if (r6 == 0) goto L_0x07b7;
        r6 = r3.intValue(r12);	 Catch:{ Exception -> 0x046c, all -> 0x0457 }
        goto L_0x07b8;
        r6 = 0;
        r3.dispose();	 Catch:{ Exception -> 0x0b42, all -> 0x0b27 }
        r3 = 4;
        if (r15 != r3) goto L_0x090d;
        if (r45 == 0) goto L_0x090d;
        r3 = r1.database;	 Catch:{ Exception -> 0x08f7, all -> 0x08df }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x08f7, all -> 0x08df }
        r8 = "SELECT max(mid) FROM messages WHERE uid = %d AND date <= %d AND mid > 0";	 Catch:{ Exception -> 0x08f7, all -> 0x08df }
        r9 = 2;	 Catch:{ Exception -> 0x08f7, all -> 0x08df }
        r12 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x08f7, all -> 0x08df }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x08f7, all -> 0x08df }
        r38 = r6;
        r6 = 0;
        r12[r6] = r9;	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r9 = java.lang.Integer.valueOf(r45);	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r19 = 1;	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r12[r19] = r9;	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r7 = java.lang.String.format(r7, r8, r12);	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r8 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r3 = r3.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r7 = r3.next();	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        if (r7 == 0) goto L_0x0819;
        r7 = r3.intValue(r6);	 Catch:{ Exception -> 0x0805, all -> 0x07ef }
        goto L_0x081a;
        r0 = move-exception;
        r12 = r38;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x1553;
        r0 = move-exception;
        r12 = r38;
        r7 = r39;
        r6 = r2;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x1523;
        r7 = -1;
        r3.dispose();	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r3 = r1.database;	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r9 = "SELECT min(mid) FROM messages WHERE uid = %d AND date >= %d AND mid > 0";	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r12 = 2;	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r8 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x08dd, all -> 0x08db }
        r35 = r10;
        r10 = 0;
        r8[r10] = r12;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = java.lang.Integer.valueOf(r45);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r19 = 1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r8[r19] = r12;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.lang.String.format(r6, r9, r8);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r8 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = r3.next();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r6 == 0) goto L_0x084c;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r8 = r3.intValue(r10);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        goto L_0x084d;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r8 = -1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3.dispose();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = -1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r7 == r3) goto L_0x0911;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r8 == r3) goto L_0x0911;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r7 != r8) goto L_0x085c;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r39;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r8 = r7;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        goto L_0x0915;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d";	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10 = 3;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r20 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12[r20] = r10;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r19 = 1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12[r19] = r10;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r25 = 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12[r25] = r10;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.lang.String.format(r6, r9, r12);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r3.queryFinalized(r6, r10);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = r3.next();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r6 == 0) goto L_0x088f;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7 = -1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3.dispose();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = -1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r7 == r3) goto L_0x0911;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7 = "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d";	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 3;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10[r12] = r9;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = 1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10[r12] = r9;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10[r12] = r9;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.lang.String.format(r6, r7, r10);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = r3.next();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r6 == 0) goto L_0x08c5;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r8 = -1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3.dispose();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = -1;
        if (r8 == r3) goto L_0x0911;
        r13 = (long) r8;
        r6 = 0;
        r3 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1));
        if (r3 == 0) goto L_0x08d9;
        if (r11 == 0) goto L_0x08d9;
        r6 = (long) r11;
        r3 = 32;
        r6 = r6 << r3;
        r13 = r13 | r6;
        r3 = r8;
        goto L_0x0915;
        r0 = move-exception;
        goto L_0x08e2;
        r0 = move-exception;
        goto L_0x08fa;
        r0 = move-exception;
        r38 = r6;
        r35 = r10;
        r12 = r38;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x0f1e;
        r0 = move-exception;
        r38 = r6;
        r35 = r10;
        r12 = r38;
        r7 = r39;
        r6 = r2;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x0var_;
        r38 = r6;
        r35 = r10;
        r3 = r39;
        r8 = r31;
        if (r8 == 0) goto L_0x0919;
        r10 = 1;
        goto L_0x091a;
        r10 = 0;
        if (r10 == 0) goto L_0x095d;
        r6 = r1.database;	 Catch:{ Exception -> 0x0959, all -> 0x0955 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0959, all -> 0x0955 }
        r9 = "SELECT start FROM messages_holes WHERE uid = %d AND start < %d AND end > %d";	 Catch:{ Exception -> 0x0959, all -> 0x0955 }
        r39 = r3;
        r12 = 3;
        r3 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r20 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3[r20] = r12;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r19 = 1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3[r19] = r12;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r25 = 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3[r25] = r12;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = java.lang.String.format(r7, r9, r3);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r6.queryFinalized(r3, r9);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = r3.next();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r6 == 0) goto L_0x0951;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3.dispose();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        goto L_0x095f;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r0 = move-exception;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r39 = r3;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        goto L_0x08e4;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r0 = move-exception;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r39 = r3;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        goto L_0x08fc;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r39 = r3;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r10 == 0) goto L_0x0a92;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10 = "SELECT start FROM messages_holes WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1";	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6[r12] = r7;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r19 = 1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6[r19] = r7;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.lang.String.format(r9, r10, r6);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r3.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = r3.next();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r6 == 0) goto L_0x0996;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = r3.intValue(r12);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = (long) r6;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r11 == 0) goto L_0x0998;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = (long) r11;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = 32;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = r9 << r12;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = r6 | r9;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        goto L_0x0998;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3.dispose();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10 = "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4[r12] = r5;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r19 = 1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4[r19] = r5;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = java.lang.String.format(r9, r10, r4);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r3.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = r3.next();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r4 == 0) goto L_0x09d0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = r3.intValue(r12);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = (long) r4;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r11 == 0) goto L_0x09d2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = (long) r11;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = 32;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = r9 << r12;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = r4 | r9;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        goto L_0x09d2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = 1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3.dispose();	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1));	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r3 != 0) goto L_0x0a25;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1));	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r3 == 0) goto L_0x09e2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        goto L_0x0a25;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)";	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = 6;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = r2 / 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 3;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 4;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = r2 / 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 5;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = java.lang.String.format(r4, r5, r7);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r3.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        goto L_0x0a88;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1));	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r3 != 0) goto L_0x0a35;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = NUM; // 0x3b9aca00 float:0.NUM double:4.94065646E-315;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        if (r11 == 0) goto L_0x0a35;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = (long) r11;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = 32;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = r9 << r3;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = r6 | r9;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r10 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND (m.mid <= %d OR m.mid < 0) ORDER BY m.date ASC, m.mid ASC LIMIT %d)";	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r11 = 8;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r11 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r20 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r11[r20] = r12;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r19 = 1;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r11[r19] = r12;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r11[r5] = r4;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = r2 / 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = 3;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r11[r5] = r4;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = 4;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r11[r5] = r4;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = 5;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r11[r5] = r4;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = 6;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r11[r5] = r4;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = 7;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = r2 / 2;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r11[r4] = r5;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r4 = java.lang.String.format(r9, r10, r11);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r5 = 0;	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r3 = r3.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        r12 = r34;
        goto L_0x0b18;
        r0 = move-exception;
        goto L_0x08e4;
        r0 = move-exception;
        goto L_0x08fc;
        r3 = 2;
        if (r15 != r3) goto L_0x0b15;
        r3 = r1.database;	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r5 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid != 0 AND out = 0 AND read_state IN(0,2)";	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r6 = 1;	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r9 = 0;	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r4 = java.lang.String.format(r4, r5, r7);	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r5 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r3 = r3.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r4 = r3.next();	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        if (r4 == 0) goto L_0x0aba;
        r10 = r3.intValue(r9);	 Catch:{ Exception -> 0x0a8f, all -> 0x0a8c }
        goto L_0x0abb;
        r10 = 0;
        r3.dispose();	 Catch:{ Exception -> 0x0b13, all -> 0x0b11 }
        r12 = r34;
        if (r10 != r12) goto L_0x0b0b;
        r3 = r1.database;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r5 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)";	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r6 = 6;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r9 = 0;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r6 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r9 = 1;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r6 = r2 / 2;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r9 = 2;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r9 = 3;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r6 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r9 = 4;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r6 = r2 / 2;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r9 = 5;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r4 = java.lang.String.format(r4, r5, r7);	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r5 = 0;	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r3 = r3.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0b09, all -> 0x0b07 }
        r4 = r3;
        r3 = 1;
        goto L_0x0b0e;
        r0 = move-exception;
        goto L_0x0b2e;
        r0 = move-exception;
        goto L_0x0b49;
        r3 = 0;
        r4 = r3;
        r3 = 0;
        r10 = r3;
        r3 = r4;
        goto L_0x0b19;
        r0 = move-exception;
        goto L_0x0b2c;
        r0 = move-exception;
        goto L_0x0b47;
        r12 = r34;
        r3 = 0;
        r10 = 0;
        r7 = r39;
        r6 = r2;
        r13 = r12;
        r14 = r29;
        r2 = r30;
        r11 = r33;
        r12 = r38;
        goto L_0x0ed2;
        r0 = move-exception;
        r38 = r6;
        r35 = r10;
        r12 = r34;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r17 = r35;
        r12 = r38;
        goto L_0x1553;
        r0 = move-exception;
        r38 = r6;
        r35 = r10;
        r12 = r34;
        r7 = r39;
        r6 = r2;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r17 = r35;
        r12 = r38;
        goto L_0x1523;
        r0 = move-exception;
        r35 = r10;
        r12 = r34;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r17 = r35;
        goto L_0x0469;
        r0 = move-exception;
        r35 = r10;
        r12 = r34;
        r7 = r39;
        r6 = r2;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r17 = r35;
        goto L_0x047c;
        r0 = move-exception;
        r12 = r34;
        goto L_0x0b91;
        r0 = move-exception;
        r12 = r34;
        goto L_0x0ba6;
        r0 = move-exception;
        r12 = r10;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r12 = 0;
        r17 = 0;
        goto L_0x1553;
        r0 = move-exception;
        r12 = r10;
        r7 = r39;
        r6 = r2;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r12 = 0;
        r17 = 0;
        goto L_0x1523;
        r36 = r6;
        r27 = r7;
        r28 = r10;
        r26 = r12;
        r3 = "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0";
        r4 = 3;
        if (r15 != r4) goto L_0x0cc0;
        if (r44 != 0) goto L_0x0cc0;
        r4 = r1.database;	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r6 = 1;	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r9 = 0;	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r7[r9] = r6;	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r5 = java.lang.String.format(r5, r3, r7);	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r6 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r4 = r4.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r5 = r4.next();	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        if (r5 == 0) goto L_0x0be9;	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        r10 = r4.intValue(r9);	 Catch:{ Exception -> 0x0cb3, all -> 0x0ca4 }
        goto L_0x0bea;
        r10 = 0;
        r4.dispose();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r4 = r1.database;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r6 = "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0";	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r7 = 1;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r9 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r7 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r11 = 0;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r9[r11] = r7;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r5 = java.lang.String.format(r5, r6, r9);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r6 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r4 = r4.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r5 = r4.next();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        if (r5 == 0) goto L_0x0CLASSNAME;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r5 = r4.intValue(r11);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r6 = 1;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        r7 = r4.intValue(r6);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c7c }
        goto L_0x0CLASSNAME;
        r5 = 0;
        r7 = 0;
        r4.dispose();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        if (r5 == 0) goto L_0x0CLASSNAME;
        r4 = r1.database;	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r9 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)";	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r10 = 2;	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r10 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r12 = 0;	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r11[r12] = r10;	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r10 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r19 = 1;	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r11[r19] = r10;	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r6 = java.lang.String.format(r6, r9, r11);	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r9 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r4 = r4.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r6 = r4.next();	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        if (r6 == 0) goto L_0x0c4b;	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        r10 = r4.intValue(r12);	 Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
        goto L_0x0c4c;
        r10 = 0;
        r4.dispose();	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = 3;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x0cc4;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r0 = move-exception;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r21 = r0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r33 = r5;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = r7;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x0c6b;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r0 = move-exception;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r33 = r5;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = r7;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x0CLASSNAME;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r5 = r10;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = 3;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x0cc3;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r0 = move-exception;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r21 = r0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = r7;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r33 = r10;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = r27;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r13 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x14e0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r0 = move-exception;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = r7;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r33 = r10;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = r27;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r13 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x14f5;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r0 = move-exception;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r7 = r39;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r21 = r0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r33 = r10;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = r27;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r13 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r17 = 1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r19 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r20 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x1553;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r0 = move-exception;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r7 = r39;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r33 = r10;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = r27;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r13 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r17 = 1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r19 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r20 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x1523;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r0 = move-exception;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r7 = r39;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r21 = r0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = r27;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r13 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r17 = 1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x150b;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r0 = move-exception;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r7 = r39;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = r27;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r13 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r17 = 1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x151d;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = 3;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r5 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r7 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r10 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        if (r15 == r4) goto L_0x0e67;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = 4;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        if (r15 != r6) goto L_0x0ccb;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x0e67;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = 1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        if (r15 != r6) goto L_0x0d1c;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r3 = r1.database;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d ORDER BY m.mid DESC LIMIT %d";	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9[r11] = r4;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = java.lang.Integer.valueOf(r39);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = 1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9[r11] = r4;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = java.lang.Integer.valueOf(r38);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = 2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9[r11] = r4;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = java.lang.String.format(r6, r8, r9);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r3 = r3.queryFinalized(r4, r8);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = r39;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = r5;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = r7;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r13 = r10;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r2 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r10 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r32 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r35 = 1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r7 = r8;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x0ed2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r0 = move-exception;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r21 = r0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r33 = r5;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = r7;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r13 = r10;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = r27;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x14df;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r0 = move-exception;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = r2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r33 = r5;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r14 = r7;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r13 = r10;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = r27;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x14f4;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        if (r44 == 0) goto L_0x0d7b;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        if (r39 == 0) goto L_0x0d4a;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r3 = r1.database;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d";	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = 3;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = java.lang.Integer.valueOf(r39);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = 1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = java.lang.Integer.valueOf(r38);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = 2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = java.lang.String.format(r4, r6, r9);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r3 = r3.queryFinalized(r4, r8);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x0cf6;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r3 = r1.database;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.mid ASC LIMIT %d,%d";	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = 4;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9[r12] = r8;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9[r12] = r8;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r8 = java.lang.Integer.valueOf(r38);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 3;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9[r12] = r8;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = java.lang.String.format(r4, r6, r9);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r3 = r3.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x0cf6;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = 2;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        if (r15 != r4) goto L_0x0e02;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = r1.database;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9 = 1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r12 = 0;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r11[r12] = r9;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r3 = java.lang.String.format(r6, r3, r11);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r6 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r3 = r4.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = r3.next();	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        if (r4 == 0) goto L_0x0da1;	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        r4 = r3.intValue(r12);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x0da2;
        r4 = 0;
        r3.dispose();	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = r1.database;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r9 = "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0";	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r11 = 1;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r13 = 0;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r12[r13] = r11;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r6 = java.lang.String.format(r6, r9, r12);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r9 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r6 = r3.next();	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        if (r6 == 0) goto L_0x0dce;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r5 = r3.intValue(r13);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r6 = 1;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r7 = r3.intValue(r6);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3.dispose();	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        if (r5 == 0) goto L_0x0e03;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = r1.database;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r9 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)";	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r11 = 2;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r13 = 0;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r12[r13] = r11;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r11 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r14 = 1;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r12[r14] = r11;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r6 = java.lang.String.format(r6, r9, r12);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r9 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r6 = r3.next();	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        if (r6 == 0) goto L_0x0dfe;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r10 = r3.intValue(r13);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3.dispose();	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        goto L_0x0e03;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r4 = 0;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        if (r2 > r10) goto L_0x0e0d;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        if (r10 >= r8) goto L_0x0e08;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        goto L_0x0e0d;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = r10 - r2;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r2 = r2 + 10;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        goto L_0x0e1b;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = r10 + 10;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r2 = java.lang.Math.max(r2, r3);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        if (r10 >= r8) goto L_0x0e1a;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = 0;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r4 = 0;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r5 = 0;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r10 = 0;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        goto L_0x0e1b;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = 0;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r6 = r1.database;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r9 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.mid ASC LIMIT %d,%d";	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r11 = 3;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r13 = 0;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r12[r13] = r11;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r11 = 1;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r12[r11] = r3;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r11 = 2;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r12[r11] = r3;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = java.lang.String.format(r8, r9, r12);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r8 = 0;	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r3 = r6.queryFinalized(r3, r9);	 Catch:{ Exception -> 0x0e5c, all -> 0x0e4f }
        r8 = r39;
        r6 = r2;
        r12 = r4;
        r11 = r5;
        r14 = r7;
        r13 = r10;
        r2 = 0;
        r10 = 0;
        goto L_0x0cff;
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r12 = r4;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        r11 = r27;
        goto L_0x14e0;
        r0 = move-exception;
        r6 = r2;
        r12 = r4;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        r11 = r27;
        goto L_0x14f5;
        r4 = r1.database;	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        r8 = 1;	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        r11 = 0;	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        r9[r11] = r8;	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        r3 = java.lang.String.format(r6, r3, r9);	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        r6 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        r3 = r4.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        r4 = r3.next();	 Catch:{ Exception -> 0x14ea, all -> 0x14d3 }
        if (r4 == 0) goto L_0x0e8a;
        r4 = r3.intValue(r11);	 Catch:{ Exception -> 0x0d12, all -> 0x0d06 }
        goto L_0x0e8b;
        r4 = 0;
        r3.dispose();	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r3 = r1.database;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r8 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d)";	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r9 = 6;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r12 = 0;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r9 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r12 = 1;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r9 = r2 / 2;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r12 = 2;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r12 = 3;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r9 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r12 = 4;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r9 = r2 / 2;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r12 = 5;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r6 = java.lang.String.format(r6, r8, r11);	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        r8 = 0;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x14b4, all -> 0x14af }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x14c7, all -> 0x14b9 }
        goto L_0x0e44;
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r3 == 0) goto L_0x1259;
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r29 = r3.next();	 Catch:{ Exception -> 0x123d, all -> 0x1220 }
        if (r29 == 0) goto L_0x11ec;
        r39 = r14;
        r5 = 1;
        r14 = r3.byteBufferValue(r5);	 Catch:{ Exception -> 0x11d9, all -> 0x11c6 }
        if (r14 == 0) goto L_0x1190;
        r44 = r13;
        r5 = 0;
        r13 = r14.readInt32(r5);	 Catch:{ Exception -> 0x117d, all -> 0x116a }
        r13 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r14, r13, r5);	 Catch:{ Exception -> 0x117d, all -> 0x116a }
        r29 = r12;
        r5 = 2;
        r12 = r3.intValue(r5);	 Catch:{ Exception -> 0x1154, all -> 0x113d }
        r13.send_state = r12;	 Catch:{ Exception -> 0x1154, all -> 0x113d }
        r5 = r13.id;	 Catch:{ Exception -> 0x1154, all -> 0x113d }
        if (r5 <= 0) goto L_0x0var_;
        r5 = r13.send_state;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0d }
        if (r5 == 0) goto L_0x0var_;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0d }
        r5 = r13.send_state;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0d }
        r12 = 3;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0d }
        if (r5 == r12) goto L_0x0var_;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0d }
        r5 = 0;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0d }
        r13.send_state = r5;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0d }
        goto L_0x0var_;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r2;
        r33 = r11;
        r11 = r27;
        r12 = r29;
        r19 = r32;
        r17 = r35;
        goto L_0x1553;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r20 = r2;
        r33 = r11;
        r11 = r27;
        r12 = r29;
        r19 = r32;
        r17 = r35;
        goto L_0x1523;
        r30 = r11;
        r5 = r36;
        r11 = (long) r5;
        r33 = r6;
        r31 = r7;
        r6 = r41;
        r34 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r34 != 0) goto L_0x0f6b;
        r11 = 1;
        r13.out = r11;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        goto L_0x0f6b;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r2;
        r11 = r27;
        goto L_0x1491;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r20 = r2;
        r11 = r27;
        r12 = r29;
        r7 = r31;
        r19 = r32;
        r6 = r33;
        r17 = r35;
        r2 = r0;
        goto L_0x1255;
        r13.readAttachPath(r14, r5);	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r14.reuse();	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r11 = 0;	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r12 = r3.intValue(r11);	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        org.telegram.messenger.MessageObject.setUnreadFlags(r13, r12);	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r11 = 3;	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r12 = r3.intValue(r11);	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r13.id = r12;	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r11 = r13.id;	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        if (r11 <= 0) goto L_0x0var_;
        r11 = r13.id;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r4 = java.lang.Math.min(r11, r4);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r11 = r13.id;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r9 = java.lang.Math.max(r11, r9);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r11 = 4;
        r12 = r3.intValue(r11);	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r13.date = r12;	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r13.dialog_id = r6;	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r11 = r13.flags;	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        r11 = r11 & 1024;
        if (r11 == 0) goto L_0x0fa6;
        r11 = 7;
        r11 = r3.intValue(r11);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r13.views = r11;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        if (r28 == 0) goto L_0x0fb4;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r11 = r13.ttl;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        if (r11 != 0) goto L_0x0fb4;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r11 = 8;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r11 = r3.intValue(r11);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r13.ttl = r11;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r11 = 9;
        r11 = r3.intValue(r11);	 Catch:{ Exception -> 0x1136, all -> 0x112f }
        if (r11 == 0) goto L_0x0fbf;
        r11 = 1;
        r13.mentioned = r11;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r11 = r27;
        r12 = r11.messages;	 Catch:{ Exception -> 0x112d, all -> 0x112b }
        r12.add(r13);	 Catch:{ Exception -> 0x112d, all -> 0x112b }
        r12 = r24;	 Catch:{ Exception -> 0x112d, all -> 0x112b }
        r14 = r26;	 Catch:{ Exception -> 0x112d, all -> 0x112b }
        addUsersAndChatsFromMessage(r13, r12, r14);	 Catch:{ Exception -> 0x112d, all -> 0x112b }
        r24 = r4;	 Catch:{ Exception -> 0x112d, all -> 0x112b }
        r4 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x112d, all -> 0x112b }
        if (r4 != 0) goto L_0x0ffc;
        r6 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r16 = 0;
        r4 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1));
        if (r4 == 0) goto L_0x0fdc;
        goto L_0x0ffc;
        r36 = r5;
        r5 = r21;
        r4 = r23;
        r18 = 32;
        r21 = r8;
        goto L_0x10c2;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r2;
        goto L_0x1491;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r20 = r2;
        goto L_0x0f5e;
        r6 = 6;
        r4 = r3.isNull(r6);	 Catch:{ Exception -> 0x112d, all -> 0x112b }
        if (r4 != 0) goto L_0x1034;
        r4 = r3.byteBufferValue(r6);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r4 == 0) goto L_0x1034;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r7 = 0;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = r4.readInt32(r7);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r4, r6, r7);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r13.replyMessage = r6;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = r13.replyMessage;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6.readAttachPath(r4, r5);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4.reuse();	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r13.replyMessage;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r4 == 0) goto L_0x1034;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = org.telegram.messenger.MessageObject.isMegagroup(r13);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r4 == 0) goto L_0x102f;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r13.replyMessage;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = r4.flags;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = r6 | r7;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4.flags = r6;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r13.replyMessage;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        addUsersAndChatsFromMessage(r4, r12, r14);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r13.replyMessage;	 Catch:{ Exception -> 0x112d, all -> 0x112b }
        if (r4 != 0) goto L_0x0fdc;
        r4 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r4 == 0) goto L_0x1089;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = (long) r4;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r13.to_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r4.channel_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r4 == 0) goto L_0x1052;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r13.to_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r4.channel_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r36 = r5;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = (long) r4;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r18 = 32;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r4 << r18;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = r6 | r4;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        goto L_0x1056;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r36 = r5;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r18 = 32;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r5 = r21;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r5.contains(r4);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r4 != 0) goto L_0x1069;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r5.add(r4);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = r22;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r6.get(r4);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = (java.util.ArrayList) r4;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r4 != 0) goto L_0x107f;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4.<init>();	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r7 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6.put(r7, r4);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4.add(r13);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r22 = r6;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r21 = r8;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r23;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        goto L_0x10c2;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r36 = r5;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r5 = r21;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r18 = 32;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r5.contains(r4);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r4 != 0) goto L_0x10a4;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r5.add(r4);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4 = r23;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = r4.get(r6);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = (java.util.ArrayList) r6;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r6 != 0) goto L_0x10bd;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6.<init>();	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r21 = r8;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r7 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r4.put(r7, r6);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        goto L_0x10bf;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r21 = r8;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6.add(r13);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r28 != 0) goto L_0x10d2;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r6 = 5;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r7 = r3.isNull(r6);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        if (r7 != 0) goto L_0x10d3;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r7 = r3.longValue(r6);	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        r13.random_id = r7;	 Catch:{ Exception -> 0x0ff3, all -> 0x0fe8 }
        goto L_0x10d3;
        r6 = 5;
        r7 = org.telegram.messenger.MessageObject.isSecretPhotoOrVideo(r13);	 Catch:{ Exception -> 0x112d, all -> 0x112b }
        if (r7 == 0) goto L_0x111d;
        r7 = r1.database;	 Catch:{ Exception -> 0x1111, all -> 0x112b }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x1111, all -> 0x112b }
        r6 = "SELECT date FROM enc_tasks_v2 WHERE mid = %d";	 Catch:{ Exception -> 0x1111, all -> 0x112b }
        r23 = r4;
        r26 = r9;
        r4 = 1;
        r9 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x110d, all -> 0x112b }
        r4 = r13.id;	 Catch:{ Exception -> 0x110d, all -> 0x112b }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x110d, all -> 0x112b }
        r27 = r2;
        r2 = 0;
        r9[r2] = r4;	 Catch:{ Exception -> 0x110b, all -> 0x120b }
        r4 = java.lang.String.format(r8, r6, r9);	 Catch:{ Exception -> 0x110b, all -> 0x120b }
        r6 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x110b, all -> 0x120b }
        r4 = r7.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x110b, all -> 0x120b }
        r6 = r4.next();	 Catch:{ Exception -> 0x110b, all -> 0x120b }
        if (r6 == 0) goto L_0x1107;	 Catch:{ Exception -> 0x110b, all -> 0x120b }
        r6 = r4.intValue(r2);	 Catch:{ Exception -> 0x110b, all -> 0x120b }
        r13.destroyTime = r6;	 Catch:{ Exception -> 0x110b, all -> 0x120b }
        r4.dispose();	 Catch:{ Exception -> 0x110b, all -> 0x120b }
        goto L_0x1123;
        r0 = move-exception;
        goto L_0x1118;
        r0 = move-exception;
        r27 = r2;
        goto L_0x1118;
        r0 = move-exception;
        r27 = r2;
        r23 = r4;
        r26 = r9;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1123;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r23 = r4;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r26 = r9;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r2 = r23;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r4 = r24;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r9 = r26;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x11aa;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r0 = move-exception;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1132;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r0 = move-exception;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1139;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r0 = move-exception;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x120c;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r0 = move-exception;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1217;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r0 = move-exception;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r33 = r6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r31 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r30 = r11;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r14 = r39;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r13 = r44;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r21 = r0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r20 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r12 = r29;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1235;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r0 = move-exception;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r33 = r6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r31 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r30 = r11;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r14 = r39;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r13 = r44;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r2 = r0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r20 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r12 = r29;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1251;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r0 = move-exception;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r33 = r6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r31 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r30 = r11;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r29 = r12;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r14 = r39;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r13 = r44;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1231;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r0 = move-exception;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r33 = r6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r31 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r30 = r11;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r29 = r12;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r14 = r39;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r13 = r44;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x124e;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r33 = r6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r31 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r30 = r11;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r29 = r12;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r44 = r13;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = r21;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r12 = r24;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r14 = r26;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r18 = 32;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r21 = r8;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r2 = r23;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r13 = r44;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r23 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r24 = r12;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r26 = r14;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r21;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r2 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r12 = r29;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = r31;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6 = r33;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r14 = r39;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r21 = r5;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r11;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r30;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x0ed9;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r0 = move-exception;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r33 = r6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r31 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r30 = r11;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r29 = r12;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r44 = r13;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r14 = r39;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1231;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r0 = move-exception;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r33 = r6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r31 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r30 = r11;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r29 = r12;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r44 = r13;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r14 = r39;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x124e;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r33 = r6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r31 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r30 = r11;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r29 = r12;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r44 = r13;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r39 = r14;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = r21;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r12 = r24;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r14 = r26;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r11 = r27;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r27 = r2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r21 = r8;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r2 = r23;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3.dispose();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1275;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r27;
        goto L_0x1491;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r2 = r0;
        r20 = r27;
        goto L_0x14a5;
        r0 = move-exception;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r44 = r13;
        r39 = r14;
        r11 = r27;
        r27 = r2;
        r21 = r0;
        r20 = r27;
        r19 = r32;
        r17 = r35;
        r33 = r30;
        goto L_0x1553;
        r0 = move-exception;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r44 = r13;
        r39 = r14;
        r11 = r27;
        r27 = r2;
        r2 = r0;
        r20 = r27;
        r19 = r32;
        r17 = r35;
        r33 = r30;
        goto L_0x1524;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r44 = r13;
        r39 = r14;
        r5 = r21;
        r12 = r24;
        r14 = r26;
        r11 = r27;
        r27 = r2;
        r21 = r8;
        r2 = r23;
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = r11.messages;	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r6 = org.telegram.messenger.-$$Lambda$MessagesStorage$IVEB2BaRntSrHR6xrRir0pCFvl0.INSTANCE;	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        java.util.Collections.sort(r3, r6);	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        if (r28 == 0) goto L_0x12be;
        r3 = 3;
        if (r15 == r3) goto L_0x128e;
        r3 = 4;
        if (r15 == r3) goto L_0x128e;
        r3 = 2;
        if (r15 != r3) goto L_0x128c;
        if (r32 == 0) goto L_0x128c;
        if (r10 != 0) goto L_0x128c;
        goto L_0x128e;
        r3 = 4;
        goto L_0x12ab;
        r3 = r11.messages;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r3.isEmpty();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r3 != 0) goto L_0x128c;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r21;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r4 > r8) goto L_0x129c;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r9 >= r8) goto L_0x128c;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5.clear();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r12.clear();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r14.clear();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r11.messages;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3.clear();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x128c;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r15 == r3) goto L_0x12b0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = 3;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r15 != r3) goto L_0x12be;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r11.messages;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r3.size();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r4 = 1;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r3 != r4) goto L_0x12be;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r11.messages;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3.clear();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r5.isEmpty();	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r4 = ",";
        if (r3 != 0) goto L_0x13e2;
        r3 = r22.size();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r3 <= 0) goto L_0x12e8;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r1.database;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = "SELECT data, mid, date FROM messages WHERE mid IN(%s)";	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = 1;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = android.text.TextUtils.join(r4, r5);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = 0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r9[r8] = r5;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r3.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = 0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1302;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r1.database;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)";	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = 1;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = android.text.TextUtils.join(r4, r5);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = 0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r9[r8] = r5;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r3.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = r3.next();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r5 == 0) goto L_0x13b3;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = r3.byteBufferValue(r8);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r5 == 0) goto L_0x13aa;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6 = r5.readInt32(r8);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r5, r6, r8);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = r36;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6.readAttachPath(r5, r7);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5.reuse();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = 1;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r3.intValue(r5);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6.id = r8;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = 2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r3.intValue(r5);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6.date = r8;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r41;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6.dialog_id = r8;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        addUsersAndChatsFromMessage(r6, r12, r14);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r10 = r22.size();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r10 <= 0) goto L_0x136d;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r10 = r6.id;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r13 = r22;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r10 = r13.get(r10);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r10 = (java.util.ArrayList) r10;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r10 == 0) goto L_0x136a;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r36 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = 0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = r10.size();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r5 >= r7) goto L_0x13ac;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = r10.get(r5);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = (org.telegram.tgnet.TLRPC.Message) r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7.replyMessage = r6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r18 = org.telegram.messenger.MessageObject.isMegagroup(r7);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r18 == 0) goto L_0x1365;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = r7.replyMessage;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r7.flags;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r8 | r9;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7.flags = r8;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = r5 + 1;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r41;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1348;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r36 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x13ac;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r36 = r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r13 = r22;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = 3;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = r3.longValue(r5);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r9 = r2.get(r7);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r9 = (java.util.ArrayList) r9;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r2.remove(r7);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r9 == 0) goto L_0x13ad;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = 0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r9.size();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r7 >= r8) goto L_0x13ad;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r9.get(r7);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = (org.telegram.tgnet.TLRPC.Message) r8;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8.replyMessage = r6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r10 = r6.id;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8.reply_to_msg_id = r10;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r10 = org.telegram.messenger.MessageObject.isMegagroup(r8);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r10 == 0) goto L_0x13a5;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = r8.replyMessage;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r10 = r8.flags;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r18 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r10 = r10 | r18;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8.flags = r10;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x13a7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r18 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = r7 + 1;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x1382;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r13 = r22;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = 3;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r18 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r22 = r13;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        goto L_0x12e6;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3.dispose();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = r2.size();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r3 <= 0) goto L_0x13e2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r3 = 0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = r2.size();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r3 >= r5) goto L_0x13e2;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = r2.valueAt(r3);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r5 = (java.util.ArrayList) r5;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6 = 0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = r5.size();	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        if (r6 >= r7) goto L_0x13dd;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = r5.get(r6);	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7 = (org.telegram.tgnet.TLRPC.Message) r7;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r8 = 0;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r7.reply_to_random_id = r8;	 Catch:{ Exception -> 0x1216, all -> 0x120b }
        r6 = r6 + 1;
        goto L_0x13ca;
        r8 = 0;
        r3 = r3 + 1;
        goto L_0x13bd;
        if (r27 == 0) goto L_0x142f;
        r2 = r1.database;	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r5 = "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)";	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r6 = 1;	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r6 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r7 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r8 = 0;	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r6[r8] = r7;	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r3 = java.lang.String.format(r3, r5, r6);	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r5 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r2 = r2.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r3 = r2.next();	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        if (r3 == 0) goto L_0x1411;	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r3 = r2.intValue(r8);	 Catch:{ Exception -> 0x149b, all -> 0x1486 }
        r5 = r27;
        if (r5 == r3) goto L_0x140f;
        r3 = r5 * -1;
        goto L_0x1414;
        r3 = r5;
        goto L_0x1414;
        r5 = r27;
        goto L_0x140c;
        r2.dispose();	 Catch:{ Exception -> 0x1425, all -> 0x141a }
        r20 = r3;
        goto L_0x1433;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r3;
        goto L_0x1491;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r2 = r0;
        r20 = r3;
        goto L_0x14a5;
        r5 = r27;
        r20 = r5;
        r2 = r12.isEmpty();	 Catch:{ Exception -> 0x147f, all -> 0x1477 }
        if (r2 != 0) goto L_0x1442;	 Catch:{ Exception -> 0x147f, all -> 0x1477 }
        r2 = android.text.TextUtils.join(r4, r12);	 Catch:{ Exception -> 0x147f, all -> 0x1477 }
        r3 = r11.users;	 Catch:{ Exception -> 0x147f, all -> 0x1477 }
        r1.getUsersInternal(r2, r3);	 Catch:{ Exception -> 0x147f, all -> 0x1477 }
        r2 = r14.isEmpty();	 Catch:{ Exception -> 0x147f, all -> 0x1477 }
        if (r2 != 0) goto L_0x1451;	 Catch:{ Exception -> 0x147f, all -> 0x1477 }
        r2 = android.text.TextUtils.join(r4, r14);	 Catch:{ Exception -> 0x147f, all -> 0x1477 }
        r3 = r11.chats;	 Catch:{ Exception -> 0x147f, all -> 0x1477 }
        r1.getChatsInternal(r2, r3);	 Catch:{ Exception -> 0x147f, all -> 0x1477 }
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r9 = 1;
        r3 = r11;
        r4 = r41;
        r6 = r33;
        r7 = r31;
        r8 = r45;
        r10 = r46;
        r11 = r30;
        r12 = r29;
        r13 = r44;
        r14 = r39;
        r15 = r43;
        r16 = r40;
        r17 = r35;
        r18 = r47;
        r19 = r32;
        goto L_0x154c;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        goto L_0x1491;
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r2 = r0;
        goto L_0x14a5;
        r0 = move-exception;
        r5 = r27;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r5;
        r12 = r29;
        r7 = r31;
        r19 = r32;
        r6 = r33;
        goto L_0x1237;
        r0 = move-exception;
        r5 = r27;
        r14 = r39;
        r13 = r44;
        r2 = r0;
        r20 = r5;
        r12 = r29;
        r7 = r31;
        r19 = r32;
        r6 = r33;
        goto L_0x1253;
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        goto L_0x14be;
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        goto L_0x14cc;
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        r8 = 0;
        r21 = r0;
        r6 = r2;
        r12 = r4;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        goto L_0x14e0;
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        r8 = 0;
        r6 = r2;
        r12 = r4;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        goto L_0x14f5;
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        r8 = 0;
        r21 = r0;
        r6 = r2;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        r12 = 0;
        r17 = 1;
        r19 = 0;
        r20 = 0;
        r7 = r39;
        goto L_0x1553;
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        r8 = 0;
        r6 = r2;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        r12 = 0;
        r17 = 1;
        r19 = 0;
        r20 = 0;
        r7 = r39;
        goto L_0x1523;
        r0 = move-exception;
        r11 = r7;
        r8 = 0;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
        r19 = 0;
        r20 = 0;
        r33 = 0;
        goto L_0x1553;
        r0 = move-exception;
        r11 = r7;
        r8 = 0;
        r7 = r39;
        r6 = r2;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
        r19 = 0;
        r20 = 0;
        r33 = 0;
        r2 = r0;
        r3 = r11.messages;	 Catch:{ all -> 0x1550 }
        r3.clear();	 Catch:{ all -> 0x1550 }
        r3 = r11.chats;	 Catch:{ all -> 0x1550 }
        r3.clear();	 Catch:{ all -> 0x1550 }
        r3 = r11.users;	 Catch:{ all -> 0x1550 }
        r3.clear();	 Catch:{ all -> 0x1550 }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x1550 }
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r9 = 1;
        r3 = r11;
        r4 = r41;
        r8 = r45;
        r10 = r46;
        r11 = r33;
        r15 = r43;
        r16 = r40;
        r18 = r47;
        r2.processLoadedMessages(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        return;
        r0 = move-exception;
        r21 = r0;
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r9 = 1;
        r3 = r11;
        r4 = r41;
        r8 = r45;
        r10 = r46;
        r11 = r33;
        r15 = r43;
        r16 = r40;
        r18 = r47;
        r2.processLoadedMessages(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        throw r21;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getMessages$96$MessagesStorage(int, int, boolean, long, int, int, int, int, int):void");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:27:0x006c in {9, 12, 15, 16, 23, 24, 26} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$getSentFile$98$MessagesStorage(java.lang.String r6, int r7, java.lang.Object[] r8, java.util.concurrent.CountDownLatch r9) {
        /*
        r5 = this;
        r6 = org.telegram.messenger.Utilities.MD5(r6);	 Catch:{ Exception -> 0x0062 }
        if (r6 == 0) goto L_0x005c;	 Catch:{ Exception -> 0x0062 }
        r0 = r5.database;	 Catch:{ Exception -> 0x0062 }
        r1 = java.util.Locale.US;	 Catch:{ Exception -> 0x0062 }
        r2 = "SELECT data, parent FROM sent_files_v2 WHERE uid = '%s' AND type = %d";	 Catch:{ Exception -> 0x0062 }
        r3 = 2;	 Catch:{ Exception -> 0x0062 }
        r3 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x0062 }
        r4 = 0;	 Catch:{ Exception -> 0x0062 }
        r3[r4] = r6;	 Catch:{ Exception -> 0x0062 }
        r6 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0062 }
        r7 = 1;	 Catch:{ Exception -> 0x0062 }
        r3[r7] = r6;	 Catch:{ Exception -> 0x0062 }
        r6 = java.lang.String.format(r1, r2, r3);	 Catch:{ Exception -> 0x0062 }
        r1 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0062 }
        r6 = r0.queryFinalized(r6, r1);	 Catch:{ Exception -> 0x0062 }
        r0 = r6.next();	 Catch:{ Exception -> 0x0062 }
        if (r0 == 0) goto L_0x0059;	 Catch:{ Exception -> 0x0062 }
        r0 = r6.byteBufferValue(r4);	 Catch:{ Exception -> 0x0062 }
        if (r0 == 0) goto L_0x0059;	 Catch:{ Exception -> 0x0062 }
        r1 = r0.readInt32(r4);	 Catch:{ Exception -> 0x0062 }
        r1 = org.telegram.tgnet.TLRPC.MessageMedia.TLdeserialize(r0, r1, r4);	 Catch:{ Exception -> 0x0062 }
        r0.reuse();	 Catch:{ Exception -> 0x0062 }
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0062 }
        if (r0 == 0) goto L_0x0045;	 Catch:{ Exception -> 0x0062 }
        r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaDocument) r1;	 Catch:{ Exception -> 0x0062 }
        r0 = r1.document;	 Catch:{ Exception -> 0x0062 }
        r8[r4] = r0;	 Catch:{ Exception -> 0x0062 }
        goto L_0x004f;	 Catch:{ Exception -> 0x0062 }
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0062 }
        if (r0 == 0) goto L_0x004f;	 Catch:{ Exception -> 0x0062 }
        r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaPhoto) r1;	 Catch:{ Exception -> 0x0062 }
        r0 = r1.photo;	 Catch:{ Exception -> 0x0062 }
        r8[r4] = r0;	 Catch:{ Exception -> 0x0062 }
        r0 = r8[r4];	 Catch:{ Exception -> 0x0062 }
        if (r0 == 0) goto L_0x0059;	 Catch:{ Exception -> 0x0062 }
        r0 = r6.stringValue(r7);	 Catch:{ Exception -> 0x0062 }
        r8[r7] = r0;	 Catch:{ Exception -> 0x0062 }
        r6.dispose();	 Catch:{ Exception -> 0x0062 }
        r9.countDown();
        goto L_0x0067;
        r6 = move-exception;
        goto L_0x0068;
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ all -> 0x0060 }
        goto L_0x005c;
        return;
        r9.countDown();
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getSentFile$98$MessagesStorage(java.lang.String, int, java.lang.Object[], java.util.concurrent.CountDownLatch):void");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x004d in {9, 12, 18, 19, 20, 22, 23} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$getWallpapers$30$MessagesStorage() {
        /*
        r5 = this;
        r0 = 0;
        r1 = r5.database;	 Catch:{ Exception -> 0x003d }
        r2 = "SELECT data FROM wallpapers2 WHERE 1 ORDER BY num ASC";	 Catch:{ Exception -> 0x003d }
        r3 = 0;	 Catch:{ Exception -> 0x003d }
        r4 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x003d }
        r0 = r1.queryFinalized(r2, r4);	 Catch:{ Exception -> 0x003d }
        r1 = new java.util.ArrayList;	 Catch:{ Exception -> 0x003d }
        r1.<init>();	 Catch:{ Exception -> 0x003d }
        r2 = r0.next();	 Catch:{ Exception -> 0x003d }
        if (r2 == 0) goto L_0x0030;	 Catch:{ Exception -> 0x003d }
        r2 = r0.byteBufferValue(r3);	 Catch:{ Exception -> 0x003d }
        if (r2 == 0) goto L_0x0011;	 Catch:{ Exception -> 0x003d }
        r4 = r2.readInt32(r3);	 Catch:{ Exception -> 0x003d }
        r4 = org.telegram.tgnet.TLRPC.WallPaper.TLdeserialize(r2, r4, r3);	 Catch:{ Exception -> 0x003d }
        r4 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r4;	 Catch:{ Exception -> 0x003d }
        r2.reuse();	 Catch:{ Exception -> 0x003d }
        if (r4 == 0) goto L_0x0011;	 Catch:{ Exception -> 0x003d }
        r1.add(r4);	 Catch:{ Exception -> 0x003d }
        goto L_0x0011;	 Catch:{ Exception -> 0x003d }
        r2 = new org.telegram.messenger.-$$Lambda$MessagesStorage$S7W-DgAv36sZyvYJWNclnqj5a_4;	 Catch:{ Exception -> 0x003d }
        r2.<init>(r1);	 Catch:{ Exception -> 0x003d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x003d }
        if (r0 == 0) goto L_0x0046;
        goto L_0x0043;
        r1 = move-exception;
        goto L_0x0047;
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x003b }
        if (r0 == 0) goto L_0x0046;
        r0.dispose();
        return;
        if (r0 == 0) goto L_0x004c;
        r0.dispose();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getWallpapers$30$MessagesStorage():void");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:95:0x01cc in {9, 11, 13, 14, 25, 26, 29, 38, 39, 42, 43, 48, 50, 53, 59, 60, 63, 65, 71, 72, 74, 77, 79, 81, 83, 87, 88, 89, 90, 93, 94} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$loadChatInfo$82$MessagesStorage(int r17, java.util.concurrent.CountDownLatch r18, boolean r19, boolean r20) {
        /*
        r16 = this;
        r1 = r16;
        r3 = r17;
        r5 = new java.util.ArrayList;
        r5.<init>();
        r2 = 0;
        r9 = 0;
        r0 = r1.database;	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r4.<init>();	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r6 = "SELECT info, pinned, online FROM chat_settings_v2 WHERE uid = ";	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r4.append(r6);	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r4.append(r3);	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r6 = 0;	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r0 = r0.queryFinalized(r4, r7);	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r4 = r0.next();	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r7 = 2;	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r8 = 1;	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        if (r4 == 0) goto L_0x0054;	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r4 = r0.byteBufferValue(r6);	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        if (r4 == 0) goto L_0x0054;	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r10 = r4.readInt32(r6);	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r10 = org.telegram.tgnet.TLRPC.ChatFull.TLdeserialize(r4, r10, r6);	 Catch:{ Exception -> 0x0199, all -> 0x0196 }
        r4.reuse();	 Catch:{ Exception -> 0x0050, all -> 0x004c }
        r4 = r0.intValue(r8);	 Catch:{ Exception -> 0x0050, all -> 0x004c }
        r10.pinned_msg_id = r4;	 Catch:{ Exception -> 0x0050, all -> 0x004c }
        r4 = r0.intValue(r7);	 Catch:{ Exception -> 0x0050, all -> 0x004c }
        r10.online_count = r4;	 Catch:{ Exception -> 0x0050, all -> 0x004c }
        r4 = r10;
        goto L_0x0055;
        r0 = move-exception;
        r4 = r10;
        goto L_0x01b5;
        r0 = move-exception;
        r4 = r10;
        goto L_0x019b;
        r4 = r2;
        r0.dispose();	 Catch:{ Exception -> 0x0194 }
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_chatFull;	 Catch:{ Exception -> 0x0194 }
        r10 = ",";
        if (r0 == 0) goto L_0x0098;
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0194 }
        r0.<init>();	 Catch:{ Exception -> 0x0194 }
        r2 = 0;	 Catch:{ Exception -> 0x0194 }
        r7 = r4.participants;	 Catch:{ Exception -> 0x0194 }
        r7 = r7.participants;	 Catch:{ Exception -> 0x0194 }
        r7 = r7.size();	 Catch:{ Exception -> 0x0194 }
        if (r2 >= r7) goto L_0x0089;	 Catch:{ Exception -> 0x0194 }
        r7 = r4.participants;	 Catch:{ Exception -> 0x0194 }
        r7 = r7.participants;	 Catch:{ Exception -> 0x0194 }
        r7 = r7.get(r2);	 Catch:{ Exception -> 0x0194 }
        r7 = (org.telegram.tgnet.TLRPC.ChatParticipant) r7;	 Catch:{ Exception -> 0x0194 }
        r8 = r0.length();	 Catch:{ Exception -> 0x0194 }
        if (r8 == 0) goto L_0x0081;	 Catch:{ Exception -> 0x0194 }
        r0.append(r10);	 Catch:{ Exception -> 0x0194 }
        r7 = r7.user_id;	 Catch:{ Exception -> 0x0194 }
        r0.append(r7);	 Catch:{ Exception -> 0x0194 }
        r2 = r2 + 1;	 Catch:{ Exception -> 0x0194 }
        goto L_0x0064;	 Catch:{ Exception -> 0x0194 }
        r2 = r0.length();	 Catch:{ Exception -> 0x0194 }
        if (r2 == 0) goto L_0x015f;	 Catch:{ Exception -> 0x0194 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0194 }
        r1.getUsersInternal(r0, r5);	 Catch:{ Exception -> 0x0194 }
        goto L_0x015f;	 Catch:{ Exception -> 0x0194 }
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_channelFull;	 Catch:{ Exception -> 0x0194 }
        if (r0 == 0) goto L_0x015f;	 Catch:{ Exception -> 0x0194 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0194 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0194 }
        r11.<init>();	 Catch:{ Exception -> 0x0194 }
        r12 = "SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = ";	 Catch:{ Exception -> 0x0194 }
        r11.append(r12);	 Catch:{ Exception -> 0x0194 }
        r12 = -r3;	 Catch:{ Exception -> 0x0194 }
        r11.append(r12);	 Catch:{ Exception -> 0x0194 }
        r12 = " ORDER BY cu.date DESC";	 Catch:{ Exception -> 0x0194 }
        r11.append(r12);	 Catch:{ Exception -> 0x0194 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0194 }
        r12 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0194 }
        r11 = r0.queryFinalized(r11, r12);	 Catch:{ Exception -> 0x0194 }
        r0 = new org.telegram.tgnet.TLRPC$TL_chatParticipants;	 Catch:{ Exception -> 0x0194 }
        r0.<init>();	 Catch:{ Exception -> 0x0194 }
        r4.participants = r0;	 Catch:{ Exception -> 0x0194 }
        r0 = r11.next();	 Catch:{ Exception -> 0x0194 }
        if (r0 == 0) goto L_0x0128;
        r0 = r11.byteBufferValue(r6);	 Catch:{ Exception -> 0x0123 }
        if (r0 == 0) goto L_0x00da;	 Catch:{ Exception -> 0x0123 }
        r12 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0123 }
        r12 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r0, r12, r6);	 Catch:{ Exception -> 0x0123 }
        r0.reuse();	 Catch:{ Exception -> 0x0123 }
        goto L_0x00db;	 Catch:{ Exception -> 0x0123 }
        r12 = r2;	 Catch:{ Exception -> 0x0123 }
        r0 = r11.byteBufferValue(r7);	 Catch:{ Exception -> 0x0123 }
        if (r0 == 0) goto L_0x00ed;	 Catch:{ Exception -> 0x0123 }
        r13 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0123 }
        r13 = org.telegram.tgnet.TLRPC.ChannelParticipant.TLdeserialize(r0, r13, r6);	 Catch:{ Exception -> 0x0123 }
        r0.reuse();	 Catch:{ Exception -> 0x0123 }
        goto L_0x00ee;	 Catch:{ Exception -> 0x0123 }
        r13 = r2;	 Catch:{ Exception -> 0x0123 }
        if (r12 == 0) goto L_0x00c2;	 Catch:{ Exception -> 0x0123 }
        if (r13 == 0) goto L_0x00c2;	 Catch:{ Exception -> 0x0123 }
        r0 = r12.status;	 Catch:{ Exception -> 0x0123 }
        if (r0 == 0) goto L_0x00fe;	 Catch:{ Exception -> 0x0123 }
        r0 = r12.status;	 Catch:{ Exception -> 0x0123 }
        r14 = r11.intValue(r8);	 Catch:{ Exception -> 0x0123 }
        r0.expires = r14;	 Catch:{ Exception -> 0x0123 }
        r5.add(r12);	 Catch:{ Exception -> 0x0123 }
        r0 = 3;	 Catch:{ Exception -> 0x0123 }
        r0 = r11.intValue(r0);	 Catch:{ Exception -> 0x0123 }
        r13.date = r0;	 Catch:{ Exception -> 0x0123 }
        r0 = new org.telegram.tgnet.TLRPC$TL_chatChannelParticipant;	 Catch:{ Exception -> 0x0123 }
        r0.<init>();	 Catch:{ Exception -> 0x0123 }
        r12 = r13.user_id;	 Catch:{ Exception -> 0x0123 }
        r0.user_id = r12;	 Catch:{ Exception -> 0x0123 }
        r12 = r13.date;	 Catch:{ Exception -> 0x0123 }
        r0.date = r12;	 Catch:{ Exception -> 0x0123 }
        r12 = r13.inviter_id;	 Catch:{ Exception -> 0x0123 }
        r0.inviter_id = r12;	 Catch:{ Exception -> 0x0123 }
        r0.channelParticipant = r13;	 Catch:{ Exception -> 0x0123 }
        r12 = r4.participants;	 Catch:{ Exception -> 0x0123 }
        r12 = r12.participants;	 Catch:{ Exception -> 0x0123 }
        r12.add(r0);	 Catch:{ Exception -> 0x0123 }
        goto L_0x00c2;
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0194 }
        goto L_0x00c2;	 Catch:{ Exception -> 0x0194 }
        r11.dispose();	 Catch:{ Exception -> 0x0194 }
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0194 }
        r0.<init>();	 Catch:{ Exception -> 0x0194 }
        r2 = 0;	 Catch:{ Exception -> 0x0194 }
        r7 = r4.bot_info;	 Catch:{ Exception -> 0x0194 }
        r7 = r7.size();	 Catch:{ Exception -> 0x0194 }
        if (r2 >= r7) goto L_0x0152;	 Catch:{ Exception -> 0x0194 }
        r7 = r4.bot_info;	 Catch:{ Exception -> 0x0194 }
        r7 = r7.get(r2);	 Catch:{ Exception -> 0x0194 }
        r7 = (org.telegram.tgnet.TLRPC.BotInfo) r7;	 Catch:{ Exception -> 0x0194 }
        r8 = r0.length();	 Catch:{ Exception -> 0x0194 }
        if (r8 == 0) goto L_0x014a;	 Catch:{ Exception -> 0x0194 }
        r0.append(r10);	 Catch:{ Exception -> 0x0194 }
        r7 = r7.user_id;	 Catch:{ Exception -> 0x0194 }
        r0.append(r7);	 Catch:{ Exception -> 0x0194 }
        r2 = r2 + 1;	 Catch:{ Exception -> 0x0194 }
        goto L_0x0131;	 Catch:{ Exception -> 0x0194 }
        r2 = r0.length();	 Catch:{ Exception -> 0x0194 }
        if (r2 == 0) goto L_0x015f;	 Catch:{ Exception -> 0x0194 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0194 }
        r1.getUsersInternal(r0, r5);	 Catch:{ Exception -> 0x0194 }
        if (r18 == 0) goto L_0x0164;	 Catch:{ Exception -> 0x0194 }
        r18.countDown();	 Catch:{ Exception -> 0x0194 }
        if (r4 == 0) goto L_0x0181;	 Catch:{ Exception -> 0x0194 }
        r0 = r4.pinned_msg_id;	 Catch:{ Exception -> 0x0194 }
        if (r0 == 0) goto L_0x0181;	 Catch:{ Exception -> 0x0194 }
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x0194 }
        r10 = org.telegram.messenger.DataQuery.getInstance(r0);	 Catch:{ Exception -> 0x0194 }
        r0 = -r3;	 Catch:{ Exception -> 0x0194 }
        r11 = (long) r0;	 Catch:{ Exception -> 0x0194 }
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_channelFull;	 Catch:{ Exception -> 0x0194 }
        if (r0 == 0) goto L_0x0178;	 Catch:{ Exception -> 0x0194 }
        r13 = r3;	 Catch:{ Exception -> 0x0194 }
        goto L_0x0179;	 Catch:{ Exception -> 0x0194 }
        r13 = 0;	 Catch:{ Exception -> 0x0194 }
        r14 = r4.pinned_msg_id;	 Catch:{ Exception -> 0x0194 }
        r15 = 0;	 Catch:{ Exception -> 0x0194 }
        r0 = r10.loadPinnedMessage(r11, r13, r14, r15);	 Catch:{ Exception -> 0x0194 }
        r9 = r0;
        r0 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = 1;
        r3 = r17;
        r7 = r19;
        r8 = r20;
        r2.processChatInfo(r3, r4, r5, r6, r7, r8, r9);
        if (r18 == 0) goto L_0x01b3;
        goto L_0x01b0;
        r0 = move-exception;
        goto L_0x019b;
        r0 = move-exception;
        r4 = r2;
        goto L_0x01b5;
        r0 = move-exception;
        r4 = r2;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x01b4 }
        r0 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = 1;
        r3 = r17;
        r7 = r19;
        r8 = r20;
        r2.processChatInfo(r3, r4, r5, r6, r7, r8, r9);
        if (r18 == 0) goto L_0x01b3;
        r18.countDown();
        return;
        r0 = move-exception;
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r6 = 1;
        r9 = 0;
        r3 = r17;
        r7 = r19;
        r8 = r20;
        r2.processChatInfo(r3, r4, r5, r6, r7, r8, r9);
        if (r18 == 0) goto L_0x01cb;
        r18.countDown();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadChatInfo$82$MessagesStorage(int, java.util.concurrent.CountDownLatch, boolean, boolean):void");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:27:0x0074 in {6, 9, 10, 12, 13, 14, 15, 22, 23, 25, 26} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$putSentFile$99$MessagesStorage(java.lang.String r5, org.telegram.tgnet.TLObject r6, int r7, java.lang.String r8) {
        /*
        r4 = this;
        r0 = 0;
        r5 = org.telegram.messenger.Utilities.MD5(r5);	 Catch:{ Exception -> 0x0066 }
        if (r5 == 0) goto L_0x005e;	 Catch:{ Exception -> 0x0066 }
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.Photo;	 Catch:{ Exception -> 0x0066 }
        r2 = 1;	 Catch:{ Exception -> 0x0066 }
        if (r1 == 0) goto L_0x001b;	 Catch:{ Exception -> 0x0066 }
        r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0066 }
        r1.<init>();	 Catch:{ Exception -> 0x0066 }
        r6 = (org.telegram.tgnet.TLRPC.Photo) r6;	 Catch:{ Exception -> 0x0066 }
        r1.photo = r6;	 Catch:{ Exception -> 0x0066 }
        r6 = r1.flags;	 Catch:{ Exception -> 0x0066 }
        r6 = r6 | r2;	 Catch:{ Exception -> 0x0066 }
        r1.flags = r6;	 Catch:{ Exception -> 0x0066 }
        goto L_0x002f;	 Catch:{ Exception -> 0x0066 }
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.Document;	 Catch:{ Exception -> 0x0066 }
        if (r1 == 0) goto L_0x002e;	 Catch:{ Exception -> 0x0066 }
        r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0066 }
        r1.<init>();	 Catch:{ Exception -> 0x0066 }
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;	 Catch:{ Exception -> 0x0066 }
        r1.document = r6;	 Catch:{ Exception -> 0x0066 }
        r6 = r1.flags;	 Catch:{ Exception -> 0x0066 }
        r6 = r6 | r2;	 Catch:{ Exception -> 0x0066 }
        r1.flags = r6;	 Catch:{ Exception -> 0x0066 }
        goto L_0x002f;	 Catch:{ Exception -> 0x0066 }
        r1 = r0;	 Catch:{ Exception -> 0x0066 }
        if (r1 != 0) goto L_0x0032;	 Catch:{ Exception -> 0x0066 }
        return;	 Catch:{ Exception -> 0x0066 }
        r6 = r4.database;	 Catch:{ Exception -> 0x0066 }
        r3 = "REPLACE INTO sent_files_v2 VALUES(?, ?, ?, ?)";	 Catch:{ Exception -> 0x0066 }
        r0 = r6.executeFast(r3);	 Catch:{ Exception -> 0x0066 }
        r0.requery();	 Catch:{ Exception -> 0x0066 }
        r6 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0066 }
        r3 = r1.getObjectSize();	 Catch:{ Exception -> 0x0066 }
        r6.<init>(r3);	 Catch:{ Exception -> 0x0066 }
        r1.serializeToStream(r6);	 Catch:{ Exception -> 0x0066 }
        r0.bindString(r2, r5);	 Catch:{ Exception -> 0x0066 }
        r5 = 2;	 Catch:{ Exception -> 0x0066 }
        r0.bindInteger(r5, r7);	 Catch:{ Exception -> 0x0066 }
        r5 = 3;	 Catch:{ Exception -> 0x0066 }
        r0.bindByteBuffer(r5, r6);	 Catch:{ Exception -> 0x0066 }
        r5 = 4;	 Catch:{ Exception -> 0x0066 }
        r0.bindString(r5, r8);	 Catch:{ Exception -> 0x0066 }
        r0.step();	 Catch:{ Exception -> 0x0066 }
        r6.reuse();	 Catch:{ Exception -> 0x0066 }
        if (r0 == 0) goto L_0x006d;
        r0.dispose();
        goto L_0x006d;
        r5 = move-exception;
        goto L_0x006e;
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x0064 }
        if (r0 == 0) goto L_0x006d;
        goto L_0x0060;
        return;
        if (r0 == 0) goto L_0x0073;
        r0.dispose();
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putSentFile$99$MessagesStorage(java.lang.String, org.telegram.tgnet.TLObject, int, java.lang.String):void");
    }

    public static MessagesStorage getInstance(int i) {
        MessagesStorage messagesStorage = Instance[i];
        if (messagesStorage == null) {
            synchronized (MessagesStorage.class) {
                messagesStorage = Instance[i];
                if (messagesStorage == null) {
                    MessagesStorage[] messagesStorageArr = Instance;
                    MessagesStorage messagesStorage2 = new MessagesStorage(i);
                    messagesStorageArr[i] = messagesStorage2;
                    messagesStorage = messagesStorage2;
                }
            }
        }
        return messagesStorage;
    }

    private void ensureOpened() {
        try {
            this.openSync.await();
        } catch (Throwable unused) {
        }
    }

    public int getLastDateValue() {
        ensureOpened();
        return this.lastDateValue;
    }

    public void setLastDateValue(int i) {
        ensureOpened();
        this.lastDateValue = i;
    }

    public int getLastPtsValue() {
        ensureOpened();
        return this.lastPtsValue;
    }

    public void setLastPtsValue(int i) {
        ensureOpened();
        this.lastPtsValue = i;
    }

    public int getLastQtsValue() {
        ensureOpened();
        return this.lastQtsValue;
    }

    public void setLastQtsValue(int i) {
        ensureOpened();
        this.lastQtsValue = i;
    }

    public int getLastSeqValue() {
        ensureOpened();
        return this.lastSeqValue;
    }

    public void setLastSeqValue(int i) {
        ensureOpened();
        this.lastSeqValue = i;
    }

    public int getLastSecretVersion() {
        ensureOpened();
        return this.lastSecretVersion;
    }

    public void setLastSecretVersion(int i) {
        ensureOpened();
        this.lastSecretVersion = i;
    }

    public byte[] getSecretPBytes() {
        ensureOpened();
        return this.secretPBytes;
    }

    public void setSecretPBytes(byte[] bArr) {
        ensureOpened();
        this.secretPBytes = bArr;
    }

    public int getSecretG() {
        ensureOpened();
        return this.secretG;
    }

    public void setSecretG(int i) {
        ensureOpened();
        this.secretG = i;
    }

    public MessagesStorage(int i) {
        this.currentAccount = i;
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$l_EMBf4E_fIloSDlPGVY7ULYtZw(this));
    }

    public /* synthetic */ void lambda$new$0$MessagesStorage() {
        openDatabase(1);
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public DispatchQueue getStorageQueue() {
        return this.storageQueue;
    }

    public long getDatabaseSize() {
        File file = this.cacheFile;
        long j = 0;
        if (file != null) {
            j = 0 + file.length();
        }
        file = this.shmCacheFile;
        return file != null ? j + file.length() : j;
    }

    public void openDatabase(int i) {
        int i2 = i;
        String str = "malformed";
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("account");
            stringBuilder.append(this.currentAccount);
            stringBuilder.append("/");
            File file = new File(filesDirFixed, stringBuilder.toString());
            file.mkdirs();
            filesDirFixed = file;
        }
        this.cacheFile = new File(filesDirFixed, "cache4.db");
        this.walCacheFile = new File(filesDirFixed, "cache4.db-wal");
        this.shmCacheFile = new File(filesDirFixed, "cache4.db-shm");
        int exists = this.cacheFile.exists() ^ 1;
        int i3 = 3;
        try {
            this.database = new SQLiteDatabase(this.cacheFile.getPath());
            this.database.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = 1").stepThis().dispose();
            this.database.executeFast("PRAGMA journal_mode = WAL").stepThis().dispose();
            String str2 = "INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)";
            if (exists != 0) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("create new database");
                }
                this.database.executeFast("CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages(mid INTEGER PRIMARY KEY, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_idx_messages ON messages(uid, mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER, pinned INTEGER, flags INTEGER, folder_id INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE randoms(random_id INTEGER, mid INTEGER, PRIMARY KEY (random_id, mid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                this.database.executeFast(str2).stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER, online INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB, in_seq_no INTEGER, admin_id INTEGER, mtproto_seq INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE channel_admins(did INTEGER, uid INTEGER, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, document BLOB, PRIMARY KEY (id, type));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, old INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE polls(mid INTEGER PRIMARY KEY, id INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id ON polls(id);").stepThis().dispose();
                this.database.executeFast("PRAGMA user_version = 60").stepThis().dispose();
                loadUnreadMessages();
                loadPendingTasks();
                try {
                    this.openSync.countDown();
                    return;
                } catch (Throwable unused) {
                    return;
                }
            }
            int intValue = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("current db version = ");
                stringBuilder2.append(intValue);
                FileLog.d(stringBuilder2.toString());
            }
            if (intValue != 0) {
                try {
                    SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1", new Object[0]);
                    if (queryFinalized.next()) {
                        this.lastSeqValue = queryFinalized.intValue(0);
                        this.lastPtsValue = queryFinalized.intValue(1);
                        this.lastDateValue = queryFinalized.intValue(2);
                        this.lastQtsValue = queryFinalized.intValue(3);
                        this.lastSecretVersion = queryFinalized.intValue(4);
                        this.secretG = queryFinalized.intValue(5);
                        if (queryFinalized.isNull(6)) {
                            this.secretPBytes = null;
                        } else {
                            this.secretPBytes = queryFinalized.byteArrayValue(6);
                            if (this.secretPBytes != null && this.secretPBytes.length == 1) {
                                this.secretPBytes = null;
                            }
                        }
                    }
                    queryFinalized.dispose();
                } catch (Exception e) {
                    FileLog.e(e);
                    try {
                        this.database.executeFast("CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                        this.database.executeFast(str2).stepThis().dispose();
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
                if (intValue < 60) {
                    updateDbToLastVersion(intValue);
                }
                loadUnreadMessages();
                loadPendingTasks();
                this.openSync.countDown();
                return;
            }
            throw new Exception(str);
        } catch (Exception e22) {
            FileLog.e(e22);
            if (i2 < 3 && e22.getMessage().contains(str)) {
                if (i2 == 2) {
                    cleanupInternal(true);
                    for (exists = 0; exists < 2; exists++) {
                        UserConfig.getInstance(this.currentAccount).setDialogsLoadOffset(exists, 0, 0, 0, 0, 0, 0);
                        UserConfig.getInstance(this.currentAccount).setTotalDialogsCount(exists, 0);
                    }
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                } else {
                    cleanupInternal(false);
                }
                if (i2 == 1) {
                    i3 = 2;
                }
                openDatabase(i3);
            }
        }
    }

    private void updateDbToLastVersion(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$UniaOWI4GiF_wLDJANnyTaeI1I0(this, i));
    }

    public /* synthetic */ void lambda$updateDbToLastVersion$1$MessagesStorage(int i) {
        SQLiteCursor queryFinalized;
        if (i < 4) {
            try {
                this.database.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                this.database.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
                this.database.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
                this.database.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
                fixNotificationSettings();
                this.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
                i = 4;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        if (i == 4) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
            this.database.beginTransaction();
            queryFinalized = this.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            if (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(0);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    int limit = byteBufferValue.limit();
                    for (int i2 = 0; i2 < limit / 4; i2++) {
                        executeFast.requery();
                        executeFast.bindInteger(1, byteBufferValue.readInt32(false));
                        executeFast.bindInteger(2, intValue);
                        executeFast.step();
                    }
                    byteBufferValue.reuse();
                }
            }
            executeFast.dispose();
            queryFinalized.dispose();
            this.database.commitTransaction();
            this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
            this.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
            i = 6;
        }
        if (i == 6) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
            i = 7;
        }
        if (i == 7 || i == 8 || i == 9) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
            i = 10;
        }
        if (i == 10) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
            i = 11;
        }
        if (i == 11 || i == 12) {
            this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 13").stepThis().dispose();
            i = 13;
        }
        if (i == 13) {
            this.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
            i = 14;
        }
        if (i == 14) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
            i = 15;
        }
        if (i == 15) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
            i = 16;
        }
        if (i == 16) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
            i = 17;
        }
        if (i == 17) {
            this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
            i = 18;
        }
        if (i == 18) {
            this.database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
            i = 19;
        }
        if (i == 19) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
            i = 20;
        }
        if (i == 20) {
            this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
            i = 21;
        }
        if (i == 21) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            queryFinalized = this.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
            SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
            while (queryFinalized.next()) {
                int intValue2 = queryFinalized.intValue(0);
                NativeByteBuffer byteBufferValue2 = queryFinalized.byteBufferValue(1);
                if (byteBufferValue2 != null) {
                    ChatParticipants TLdeserialize = ChatParticipants.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                    byteBufferValue2.reuse();
                    if (TLdeserialize != null) {
                        TL_chatFull tL_chatFull = new TL_chatFull();
                        tL_chatFull.id = intValue2;
                        tL_chatFull.chat_photo = new TL_photoEmpty();
                        tL_chatFull.notify_settings = new TL_peerNotifySettingsEmpty_layer77();
                        tL_chatFull.exported_invite = new TL_chatInviteEmpty();
                        tL_chatFull.participants = TLdeserialize;
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_chatFull.getObjectSize());
                        tL_chatFull.serializeToStream(nativeByteBuffer);
                        executeFast2.requery();
                        executeFast2.bindInteger(1, intValue2);
                        executeFast2.bindByteBuffer(2, nativeByteBuffer);
                        executeFast2.step();
                        nativeByteBuffer.reuse();
                    }
                }
            }
            executeFast2.dispose();
            queryFinalized.dispose();
            this.database.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
            this.database.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 22").stepThis().dispose();
            i = 22;
        }
        if (i == 22) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
            i = 23;
        }
        if (i == 23 || i == 24) {
            this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
            i = 25;
        }
        if (i == 25 || i == 26) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
            i = 27;
        }
        if (i == 27) {
            this.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
            i = 28;
        }
        if (i == 28 || i == 29) {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
            i = 30;
        }
        if (i == 30) {
            this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
            i = 31;
        }
        if (i == 31) {
            this.database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
            i = 32;
        }
        if (i == 32) {
            this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
            i = 33;
        }
        if (i == 33) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
            i = 34;
        }
        if (i == 34) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
            i = 35;
        }
        if (i == 35) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
            i = 36;
        }
        if (i == 36) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
            i = 37;
        }
        if (i == 37) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 38").stepThis().dispose();
            i = 38;
        }
        if (i == 38) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 39").stepThis().dispose();
            i = 39;
        }
        if (i == 39) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN admin_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 40").stepThis().dispose();
            i = 40;
        }
        if (i == 40) {
            fixNotificationSettings();
            this.database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
            i = 41;
        }
        if (i == 41) {
            this.database.executeFast("ALTER TABLE messages ADD COLUMN mention INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE user_contacts_v6 ADD COLUMN imported INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 42").stepThis().dispose();
            i = 42;
        }
        if (i == 42) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 43").stepThis().dispose();
            i = 43;
        }
        if (i == 43) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_admins(did INTEGER, uid INTEGER, PRIMARY KEY(did, uid))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 44").stepThis().dispose();
            i = 44;
        }
        if (i == 44) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 45").stepThis().dispose();
            i = 45;
        }
        if (i == 45) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN mtproto_seq INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 46").stepThis().dispose();
            i = 46;
        }
        if (i == 46) {
            this.database.executeFast("DELETE FROM botcache WHERE 1").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 47").stepThis().dispose();
            i = 47;
        }
        if (i == 47) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN flags INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 48").stepThis().dispose();
            i = 48;
        }
        if (i == 48) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 49").stepThis().dispose();
            i = 49;
        }
        if (i == 49) {
            this.database.executeFast("DELETE FROM chat_pinned WHERE uid = 1").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 50").stepThis().dispose();
            i = 50;
        }
        if (i == 50) {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            this.database.executeFast("ALTER TABLE sent_files_v2 ADD COLUMN parent TEXT").stepThis().dispose();
            this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            this.database.executeFast("ALTER TABLE download_queue ADD COLUMN parent TEXT").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 51").stepThis().dispose();
            i = 51;
        }
        if (i == 51) {
            this.database.executeFast("ALTER TABLE media_counts_v2 ADD COLUMN old INTEGER").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 52").stepThis().dispose();
            i = 52;
        }
        if (i == 52) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS polls(mid INTEGER PRIMARY KEY, id INTEGER);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id ON polls(id);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 53").stepThis().dispose();
            i = 53;
        }
        if (i == 53) {
            this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN online INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 54").stepThis().dispose();
            i = 54;
        }
        if (i == 54) {
            this.database.executeFast("DROP TABLE IF EXISTS wallpapers;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 55").stepThis().dispose();
            i = 55;
        }
        if (i == 55) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 56").stepThis().dispose();
            i = 56;
        }
        if (i == 56 || i == 57) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 58").stepThis().dispose();
            i = 58;
        }
        if (i == 58) {
            this.database.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
            this.database.executeFast("ALTER TABLE emoji_keywords_info_v2 ADD COLUMN date INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 59").stepThis().dispose();
            i = 59;
        }
        if (i == 59) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN folder_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN data BLOB default NULL").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 60").stepThis().dispose();
        }
    }

    private void cleanupInternal(boolean z) {
        this.lastDateValue = 0;
        this.lastSeqValue = 0;
        this.lastPtsValue = 0;
        this.lastQtsValue = 0;
        this.lastSecretVersion = 0;
        this.lastSavedSeq = 0;
        this.lastSavedPts = 0;
        this.lastSavedDate = 0;
        this.lastSavedQts = 0;
        this.secretPBytes = null;
        this.secretG = 0;
        SQLiteDatabase sQLiteDatabase = this.database;
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
            this.database = null;
        }
        if (z) {
            File file = this.cacheFile;
            if (file != null) {
                file.delete();
                this.cacheFile = null;
            }
            file = this.walCacheFile;
            if (file != null) {
                file.delete();
                this.walCacheFile = null;
            }
            file = this.shmCacheFile;
            if (file != null) {
                file.delete();
                this.shmCacheFile = null;
            }
        }
    }

    public void cleanup(boolean z) {
        if (!z) {
            this.storageQueue.cleanupQueue();
        }
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ekOTDfJDLwKOj7vRXd-V4MXUJZc(this, z));
    }

    public /* synthetic */ void lambda$cleanup$3$MessagesStorage(boolean z) {
        cleanupInternal(true);
        openDatabase(1);
        if (z) {
            Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesStorage$UEupzbErlOb7ycnNPm1lBZ9MmX8(this));
        }
    }

    public /* synthetic */ void lambda$null$2$MessagesStorage() {
        MessagesController.getInstance(this.currentAccount).getDifference();
    }

    public void saveSecretParams(int i, int i2, byte[] bArr) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$NtpBYQeXRJpUfal8CNuiE9yNR3Y(this, i, i2, bArr));
    }

    public /* synthetic */ void lambda$saveSecretParams$4$MessagesStorage(int i, int i2, byte[] bArr) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
            int i3 = 1;
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            if (bArr != null) {
                i3 = bArr.length;
            }
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
            if (bArr != null) {
                nativeByteBuffer.writeBytes(bArr);
            }
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void fixNotificationSettings() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$H7ZjmdIrsBPnbbqD1cn5Lti86iY(this));
    }

    public /* synthetic */ void lambda$fixNotificationSettings$5$MessagesStorage() {
        String str = "notify2_";
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            Map all = MessagesController.getNotificationsSettings(this.currentAccount).getAll();
            for (Entry entry : all.entrySet()) {
                String str2 = (String) entry.getKey();
                if (str2.startsWith(str)) {
                    Integer num = (Integer) entry.getValue();
                    if (num.intValue() == 2 || num.intValue() == 3) {
                        str2 = str2.replace(str, "");
                        long j = 1;
                        if (num.intValue() != 2) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("notifyuntil_");
                            stringBuilder.append(str2);
                            num = (Integer) all.get(stringBuilder.toString());
                            if (num != null) {
                                j = 1 | (((long) num.intValue()) << 32);
                            }
                        }
                        longSparseArray.put(Long.parseLong(str2), Long.valueOf(j));
                    }
                }
            }
            try {
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                for (int i = 0; i < longSparseArray.size(); i++) {
                    executeFast.requery();
                    executeFast.bindLong(1, longSparseArray.keyAt(i));
                    executeFast.bindLong(2, ((Long) longSparseArray.valueAt(i)).longValue());
                    executeFast.step();
                }
                executeFast.dispose();
                this.database.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        } catch (Throwable e3) {
            FileLog.e(e3);
        }
    }

    public long createPendingTask(NativeByteBuffer nativeByteBuffer) {
        if (nativeByteBuffer == null) {
            return 0;
        }
        long andAdd = this.lastTaskId.getAndAdd(1);
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$TxzHvDLT8O_Bs3_-p-EFkrwY3ws(this, andAdd, nativeByteBuffer));
        return andAdd;
    }

    public /* synthetic */ void lambda$createPendingTask$6$MessagesStorage(long j, NativeByteBuffer nativeByteBuffer) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
            executeFast.bindLong(1, j);
            executeFast.bindByteBuffer(2, nativeByteBuffer);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        } catch (Throwable th) {
            nativeByteBuffer.reuse();
        }
        nativeByteBuffer.reuse();
    }

    public void removePendingTask(long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$bLsQ9Zez-Ef0tQJtoMR54iM-bHA(this, j));
    }

    public /* synthetic */ void lambda$removePendingTask$7$MessagesStorage(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM pending_tasks WHERE id = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void loadPendingTasks() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Mh1_3ksiRZzSQICRWjWNRIrSkV4(this));
    }

    /* JADX WARNING: Missing block: B:9:0x0025, code skipped:
            r15 = r10;
     */
    /* JADX WARNING: Missing block: B:56:0x0273, code skipped:
            r15.reuse();
     */
    public /* synthetic */ void lambda$loadPendingTasks$21$MessagesStorage() {
        /*
        r17 = this;
        r14 = r17;
        r0 = r14.database;	 Catch:{ Exception -> 0x027f }
        r1 = "SELECT id, data FROM pending_tasks WHERE 1";
        r15 = 0;
        r2 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x027f }
        r0 = r0.queryFinalized(r1, r2);	 Catch:{ Exception -> 0x027f }
    L_0x000d:
        r1 = r0.next();	 Catch:{ Exception -> 0x027f }
        if (r1 == 0) goto L_0x027b;
    L_0x0013:
        r12 = r0.longValue(r15);	 Catch:{ Exception -> 0x027f }
        r1 = 1;
        r10 = r0.byteBufferValue(r1);	 Catch:{ Exception -> 0x027f }
        if (r10 == 0) goto L_0x0277;
    L_0x001e:
        r1 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        switch(r1) {
            case 0: goto L_0x025d;
            case 1: goto L_0x0243;
            case 2: goto L_0x01c4;
            case 3: goto L_0x019f;
            case 4: goto L_0x017f;
            case 5: goto L_0x01c4;
            case 6: goto L_0x015d;
            case 7: goto L_0x0131;
            case 8: goto L_0x01c4;
            case 9: goto L_0x0115;
            case 10: goto L_0x01c4;
            case 11: goto L_0x00eb;
            case 12: goto L_0x00be;
            case 13: goto L_0x0092;
            case 14: goto L_0x01c4;
            case 15: goto L_0x007f;
            case 16: goto L_0x0054;
            case 17: goto L_0x0029;
            default: goto L_0x0025;
        };	 Catch:{ Exception -> 0x027f }
    L_0x0025:
        r15 = r10;
    L_0x0026:
        r1 = 0;
        goto L_0x0273;
    L_0x0029:
        r3 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        r1 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x027f }
        r4.<init>();	 Catch:{ Exception -> 0x027f }
        r2 = 0;
    L_0x0037:
        if (r2 >= r1) goto L_0x0047;
    L_0x0039:
        r5 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        r5 = org.telegram.tgnet.TLRPC.TL_inputFolderPeer.TLdeserialize(r10, r5, r15);	 Catch:{ Exception -> 0x027f }
        r4.add(r5);	 Catch:{ Exception -> 0x027f }
        r2 = r2 + 1;
        goto L_0x0037;
    L_0x0047:
        r7 = new org.telegram.messenger.-$$Lambda$MessagesStorage$LtM1spYCOokckYn9GalfKw21eVw;	 Catch:{ Exception -> 0x027f }
        r1 = r7;
        r2 = r17;
        r5 = r12;
        r1.<init>(r2, r3, r4, r5);	 Catch:{ Exception -> 0x027f }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);	 Catch:{ Exception -> 0x027f }
        goto L_0x0025;
    L_0x0054:
        r3 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        r1 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x027f }
        r4.<init>();	 Catch:{ Exception -> 0x027f }
        r2 = 0;
    L_0x0062:
        if (r2 >= r1) goto L_0x0072;
    L_0x0064:
        r5 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        r5 = org.telegram.tgnet.TLRPC.InputDialogPeer.TLdeserialize(r10, r5, r15);	 Catch:{ Exception -> 0x027f }
        r4.add(r5);	 Catch:{ Exception -> 0x027f }
        r2 = r2 + 1;
        goto L_0x0062;
    L_0x0072:
        r7 = new org.telegram.messenger.-$$Lambda$MessagesStorage$KOMsQ0FDjAutn-YKgmf9ukUIQZQ;	 Catch:{ Exception -> 0x027f }
        r1 = r7;
        r2 = r17;
        r5 = r12;
        r1.<init>(r2, r3, r4, r5);	 Catch:{ Exception -> 0x027f }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);	 Catch:{ Exception -> 0x027f }
        goto L_0x0025;
    L_0x007f:
        r1 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        r1 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r10, r1, r15);	 Catch:{ Exception -> 0x027f }
        r2 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Exception -> 0x027f }
        r3 = new org.telegram.messenger.-$$Lambda$MessagesStorage$tZ31OdkMW8PKLVCYnMiDI_DfRTs;	 Catch:{ Exception -> 0x027f }
        r3.<init>(r14, r1, r12);	 Catch:{ Exception -> 0x027f }
        r2.postRunnable(r3);	 Catch:{ Exception -> 0x027f }
        goto L_0x0025;
    L_0x0092:
        r3 = r10.readInt64(r15);	 Catch:{ Exception -> 0x027f }
        r5 = r10.readBool(r15);	 Catch:{ Exception -> 0x027f }
        r6 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        r7 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        r8 = r10.readBool(r15);	 Catch:{ Exception -> 0x027f }
        r1 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027f }
        r9 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r10, r1, r15);	 Catch:{ Exception -> 0x027f }
        r16 = new org.telegram.messenger.-$$Lambda$MessagesStorage$gZU7uqq34_u60ckwe65AH41ydVA;	 Catch:{ Exception -> 0x027f }
        r1 = r16;
        r2 = r17;
        r15 = r10;
        r10 = r12;
        r1.<init>(r2, r3, r5, r6, r7, r8, r9, r10);	 Catch:{ Exception -> 0x027f }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x00be:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt64(r1);	 Catch:{ Exception -> 0x027f }
        r5 = r15.readInt64(r1);	 Catch:{ Exception -> 0x027f }
        r7 = r15.readBool(r1);	 Catch:{ Exception -> 0x027f }
        r8 = r15.readBool(r1);	 Catch:{ Exception -> 0x027f }
        r9 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r10 = r15.readDouble(r1);	 Catch:{ Exception -> 0x027f }
        r10 = (float) r10;	 Catch:{ Exception -> 0x027f }
        r11 = r15.readBool(r1);	 Catch:{ Exception -> 0x027f }
        r16 = new org.telegram.messenger.-$$Lambda$MessagesStorage$86hFXP2JtkqWJ8sHKPYBQGuj0fs;	 Catch:{ Exception -> 0x027f }
        r1 = r16;
        r2 = r17;
        r1.<init>(r2, r3, r5, r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x027f }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x00eb:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r6 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        if (r4 == 0) goto L_0x0105;
    L_0x00fb:
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r2 = org.telegram.tgnet.TLRPC.InputChannel.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027f }
        r5 = r2;
        goto L_0x0107;
    L_0x0105:
        r1 = 0;
        r5 = r1;
    L_0x0107:
        r9 = new org.telegram.messenger.-$$Lambda$MessagesStorage$TqvcAvYPfh-Cio-d66SYceSFjMQ;	 Catch:{ Exception -> 0x027f }
        r1 = r9;
        r2 = r17;
        r7 = r12;
        r1.<init>(r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x027f }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r9);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x0115:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt64(r1);	 Catch:{ Exception -> 0x027f }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r5 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027f }
        r8 = new org.telegram.messenger.-$$Lambda$MessagesStorage$OTCbnyNXoirwhvbsu8TFVflzodM;	 Catch:{ Exception -> 0x027f }
        r1 = r8;
        r2 = r17;
        r6 = r12;
        r1.<init>(r2, r3, r5, r6);	 Catch:{ Exception -> 0x027f }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r8);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x0131:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r4 = org.telegram.tgnet.TLRPC.TL_messages_deleteMessages.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027f }
        if (r4 != 0) goto L_0x0147;
    L_0x0141:
        r2 = org.telegram.tgnet.TLRPC.TL_channels_deleteMessages.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027f }
        r6 = r2;
        goto L_0x0148;
    L_0x0147:
        r6 = r4;
    L_0x0148:
        if (r6 != 0) goto L_0x014f;
    L_0x014a:
        r14.removePendingTask(r12);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x014f:
        r7 = new org.telegram.messenger.-$$Lambda$MessagesStorage$EuuPlYsIg_jeReoMzIbm6stO0ag;	 Catch:{ Exception -> 0x027f }
        r1 = r7;
        r2 = r17;
        r4 = r12;
        r1.<init>(r2, r3, r4, r6);	 Catch:{ Exception -> 0x027f }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x015d:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r7 = org.telegram.tgnet.TLRPC.InputChannel.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027f }
        r8 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Exception -> 0x027f }
        r9 = new org.telegram.messenger.-$$Lambda$MessagesStorage$veltQ-QzWYSSmgAGGDUTY-jvHoM;	 Catch:{ Exception -> 0x027f }
        r1 = r9;
        r2 = r17;
        r5 = r12;
        r1.<init>(r2, r3, r4, r5, r7);	 Catch:{ Exception -> 0x027f }
        r8.postRunnable(r9);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x017f:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt64(r1);	 Catch:{ Exception -> 0x027f }
        r5 = r15.readBool(r1);	 Catch:{ Exception -> 0x027f }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r6 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027f }
        r9 = new org.telegram.messenger.-$$Lambda$MessagesStorage$AuH1gRs2iXmoSCC0c3xvFDAEwcM;	 Catch:{ Exception -> 0x027f }
        r1 = r9;
        r2 = r17;
        r7 = r12;
        r1.<init>(r2, r3, r5, r6, r7);	 Catch:{ Exception -> 0x027f }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r9);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x019f:
        r15 = r10;
        r1 = 0;
        r5 = r15.readInt64(r1);	 Catch:{ Exception -> 0x027f }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r3 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027f }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r2 = org.telegram.tgnet.TLRPC.InputMedia.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027f }
        r4 = r2;
        r4 = (org.telegram.tgnet.TLRPC.TL_inputMediaGame) r4;	 Catch:{ Exception -> 0x027f }
        r1 = r14.currentAccount;	 Catch:{ Exception -> 0x027f }
        r2 = org.telegram.messenger.SendMessagesHelper.getInstance(r1);	 Catch:{ Exception -> 0x027f }
        r7 = r12;
        r2.sendGame(r3, r4, r5, r7);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x01c4:
        r15 = r10;
        r3 = new org.telegram.tgnet.TLRPC$TL_dialog;	 Catch:{ Exception -> 0x027f }
        r3.<init>();	 Catch:{ Exception -> 0x027f }
        r2 = 0;
        r4 = r15.readInt64(r2);	 Catch:{ Exception -> 0x027f }
        r3.id = r4;	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027f }
        r3.top_message = r4;	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027f }
        r3.read_inbox_max_id = r4;	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027f }
        r3.read_outbox_max_id = r4;	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027f }
        r3.unread_count = r4;	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027f }
        r3.last_message_date = r4;	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027f }
        r3.pts = r4;	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027f }
        r3.flags = r4;	 Catch:{ Exception -> 0x027f }
        r4 = 5;
        if (r1 < r4) goto L_0x020a;
    L_0x01fe:
        r4 = r15.readBool(r2);	 Catch:{ Exception -> 0x027f }
        r3.pinned = r4;	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027f }
        r3.pinnedNum = r4;	 Catch:{ Exception -> 0x027f }
    L_0x020a:
        r2 = 8;
        if (r1 < r2) goto L_0x0215;
    L_0x020e:
        r2 = 0;
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027f }
        r3.unread_mentions_count = r4;	 Catch:{ Exception -> 0x027f }
    L_0x0215:
        r2 = 10;
        if (r1 < r2) goto L_0x0220;
    L_0x0219:
        r2 = 0;
        r4 = r15.readBool(r2);	 Catch:{ Exception -> 0x027f }
        r3.unread_mark = r4;	 Catch:{ Exception -> 0x027f }
    L_0x0220:
        r2 = 14;
        if (r1 < r2) goto L_0x022c;
    L_0x0224:
        r1 = 0;
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r3.folder_id = r2;	 Catch:{ Exception -> 0x027f }
        goto L_0x022d;
    L_0x022c:
        r1 = 0;
    L_0x022d:
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r4 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027f }
        r7 = new org.telegram.messenger.-$$Lambda$MessagesStorage$uwK8wtI_v95kHeLcf4gXEkrDfLc;	 Catch:{ Exception -> 0x027f }
        r1 = r7;
        r2 = r17;
        r5 = r12;
        r1.<init>(r2, r3, r4, r5);	 Catch:{ Exception -> 0x027f }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x0243:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r4 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r7 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Exception -> 0x027f }
        r8 = new org.telegram.messenger.-$$Lambda$MessagesStorage$0pEDcAvar_KSwzKWD9sU7FMqhzD0;	 Catch:{ Exception -> 0x027f }
        r1 = r8;
        r2 = r17;
        r5 = r12;
        r1.<init>(r2, r3, r4, r5);	 Catch:{ Exception -> 0x027f }
        r7.postRunnable(r8);	 Catch:{ Exception -> 0x027f }
        goto L_0x0026;
    L_0x025d:
        r15 = r10;
        r1 = 0;
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027f }
        r2 = org.telegram.tgnet.TLRPC.Chat.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027f }
        if (r2 == 0) goto L_0x0273;
    L_0x0269:
        r3 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Exception -> 0x027f }
        r4 = new org.telegram.messenger.-$$Lambda$MessagesStorage$X8wmcVmkWlOOmT7hyCwq34C_3HA;	 Catch:{ Exception -> 0x027f }
        r4.<init>(r14, r2, r12);	 Catch:{ Exception -> 0x027f }
        r3.postRunnable(r4);	 Catch:{ Exception -> 0x027f }
    L_0x0273:
        r15.reuse();	 Catch:{ Exception -> 0x027f }
        goto L_0x0278;
    L_0x0277:
        r1 = 0;
    L_0x0278:
        r15 = 0;
        goto L_0x000d;
    L_0x027b:
        r0.dispose();	 Catch:{ Exception -> 0x027f }
        goto L_0x0283;
    L_0x027f:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0283:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadPendingTasks$21$MessagesStorage():void");
    }

    public /* synthetic */ void lambda$null$8$MessagesStorage(Chat chat, long j) {
        MessagesController.getInstance(this.currentAccount).loadUnknownChannel(chat, j);
    }

    public /* synthetic */ void lambda$null$9$MessagesStorage(int i, int i2, long j) {
        MessagesController.getInstance(this.currentAccount).getChannelDifference(i, i2, j, null);
    }

    public /* synthetic */ void lambda$null$10$MessagesStorage(Dialog dialog, InputPeer inputPeer, long j) {
        MessagesController.getInstance(this.currentAccount).checkLastDialogMessage(dialog, inputPeer, j);
    }

    public /* synthetic */ void lambda$null$11$MessagesStorage(long j, boolean z, InputPeer inputPeer, long j2) {
        MessagesController.getInstance(this.currentAccount).pinDialog(j, z, inputPeer, j2);
    }

    public /* synthetic */ void lambda$null$12$MessagesStorage(int i, int i2, long j, InputChannel inputChannel) {
        MessagesController.getInstance(this.currentAccount).getChannelDifference(i, i2, j, inputChannel);
    }

    public /* synthetic */ void lambda$null$13$MessagesStorage(int i, long j, TLObject tLObject) {
        MessagesController.getInstance(this.currentAccount).deleteMessages(null, null, null, i, true, j, tLObject);
    }

    public /* synthetic */ void lambda$null$14$MessagesStorage(long j, InputPeer inputPeer, long j2) {
        MessagesController.getInstance(this.currentAccount).markDialogAsUnread(j, inputPeer, j2);
    }

    public /* synthetic */ void lambda$null$15$MessagesStorage(int i, int i2, InputChannel inputChannel, int i3, long j) {
        MessagesController.getInstance(this.currentAccount).markMessageAsRead(i, i2, inputChannel, i3, j);
    }

    public /* synthetic */ void lambda$null$16$MessagesStorage(long j, long j2, boolean z, boolean z2, int i, float f, boolean z3, long j3) {
        MessagesController.getInstance(this.currentAccount).saveWallpaperToServer(null, j, j2, z, z2, i, f, z3, j3);
    }

    public /* synthetic */ void lambda$null$17$MessagesStorage(long j, boolean z, int i, int i2, boolean z2, InputPeer inputPeer, long j2) {
        MessagesController.getInstance(this.currentAccount).deleteDialog(j, z, i, i2, z2, inputPeer, j2);
    }

    public /* synthetic */ void lambda$null$18$MessagesStorage(InputPeer inputPeer, long j) {
        MessagesController.getInstance(this.currentAccount).loadUnknownDialog(inputPeer, j);
    }

    public /* synthetic */ void lambda$null$19$MessagesStorage(int i, ArrayList arrayList, long j) {
        MessagesController.getInstance(this.currentAccount).reorderPinnedDialogs(i, arrayList, j);
    }

    public /* synthetic */ void lambda$null$20$MessagesStorage(int i, ArrayList arrayList, long j) {
        MessagesController.getInstance(this.currentAccount).addDialogToFolder(null, i, -1, arrayList, j);
    }

    public void saveChannelPts(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$bE1TJhfMynV6-iw8er6RBG5GCDM(this, i2, i));
    }

    public /* synthetic */ void lambda$saveChannelPts$22$MessagesStorage(int i, int i2) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, -i2);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void saveDiffParamsInternal(int i, int i2, int i3, int i4) {
        try {
            if (this.lastSavedSeq != i || this.lastSavedPts != i2 || this.lastSavedDate != i3 || this.lastQtsValue != i4) {
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
                executeFast.bindInteger(1, i);
                executeFast.bindInteger(2, i2);
                executeFast.bindInteger(3, i3);
                executeFast.bindInteger(4, i4);
                executeFast.step();
                executeFast.dispose();
                this.lastSavedSeq = i;
                this.lastSavedPts = i2;
                this.lastSavedDate = i3;
                this.lastSavedQts = i4;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$saveDiffParams$23$MessagesStorage(int i, int i2, int i3, int i4) {
        saveDiffParamsInternal(i, i2, i3, i4);
    }

    public void saveDiffParams(int i, int i2, int i3, int i4) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$_bpF9_lI_H0oz-1CAiE5SbDJe_c(this, i, i2, i3, i4));
    }

    public void setDialogFlags(long j, long j2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$uZgJttXUWPBitEWEPJJQPgaQByA(this, j, j2));
    }

    public /* synthetic */ void lambda$setDialogFlags$24$MessagesStorage(long j, long j2) {
        try {
            this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", new Object[]{Long.valueOf(j), Long.valueOf(j2)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putPushMessage(MessageObject messageObject) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$lVMMHudh0dN8CKFv0xYu7ddEmi8(this, messageObject));
    }

    public /* synthetic */ void lambda$putPushMessage$25$MessagesStorage(MessageObject messageObject) {
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(nativeByteBuffer);
            long id = (long) messageObject.getId();
            if (messageObject.messageOwner.to_id.channel_id != 0) {
                id |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
            }
            int i = 0;
            if (messageObject.localType == 2) {
                i = 1;
            }
            if (messageObject.localChannel) {
                i |= 2;
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO unread_push_messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindLong(1, messageObject.getDialogId());
            executeFast.bindLong(2, id);
            executeFast.bindLong(3, messageObject.messageOwner.random_id);
            executeFast.bindInteger(4, messageObject.messageOwner.date);
            executeFast.bindByteBuffer(5, nativeByteBuffer);
            if (messageObject.messageText == null) {
                executeFast.bindNull(6);
            } else {
                executeFast.bindString(6, messageObject.messageText.toString());
            }
            if (messageObject.localName == null) {
                executeFast.bindNull(7);
            } else {
                executeFast.bindString(7, messageObject.localName);
            }
            if (messageObject.localUserName == null) {
                executeFast.bindNull(8);
            } else {
                executeFast.bindString(8, messageObject.localUserName);
            }
            executeFast.bindInteger(9, i);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$3lZvYkjMpWdt2EobzUTr7hIbksw(this));
    }

    public /* synthetic */ void lambda$loadUnreadMessages$27$MessagesStorage() {
        ArrayList arrayList;
        ArrayList arrayList2;
        String str;
        int max;
        ArrayList arrayList3;
        ArrayList arrayList4;
        Throwable e;
        try {
            String str2;
            int i;
            ArrayList arrayList5;
            LongSparseArray longSparseArray;
            List list;
            ArrayList arrayList6 = new ArrayList();
            ArrayList arrayList7 = new ArrayList();
            ArrayList arrayList8 = new ArrayList();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count != 0", new Object[0]);
            StringBuilder stringBuilder = new StringBuilder();
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            while (true) {
                str2 = ",";
                if (!queryFinalized.next()) {
                    break;
                }
                long longValue = queryFinalized.longValue(2);
                Object obj = (longValue & 1) != 0 ? 1 : null;
                int i2 = (int) (longValue >> 32);
                if (queryFinalized.isNull(2) || obj == null || (i2 != 0 && i2 < currentTime)) {
                    longValue = queryFinalized.longValue(0);
                    longSparseArray2.put(longValue, Integer.valueOf(queryFinalized.intValue(1)));
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append(str2);
                    }
                    stringBuilder.append(longValue);
                    int i3 = (int) longValue;
                    i = (int) (longValue >> 32);
                    if (i3 != 0) {
                        if (i3 < 0) {
                            i3 = -i3;
                            if (!arrayList7.contains(Integer.valueOf(i3))) {
                                arrayList7.add(Integer.valueOf(i3));
                            }
                        } else if (!arrayList6.contains(Integer.valueOf(i3))) {
                            arrayList6.add(Integer.valueOf(i3));
                        }
                    } else if (!arrayList8.contains(Integer.valueOf(i))) {
                        arrayList8.add(Integer.valueOf(i));
                    }
                }
            }
            queryFinalized.dispose();
            ArrayList arrayList9 = new ArrayList();
            SparseArray sparseArray = new SparseArray();
            ArrayList arrayList10 = new ArrayList();
            ArrayList arrayList11 = new ArrayList();
            ArrayList arrayList12 = new ArrayList();
            ArrayList arrayList13 = new ArrayList();
            ArrayList arrayList14 = new ArrayList();
            if (stringBuilder.length() > 0) {
                Message TLdeserialize;
                NativeByteBuffer byteBufferValue;
                SQLiteDatabase sQLiteDatabase = this.database;
                StringBuilder stringBuilder2 = new StringBuilder();
                LongSparseArray longSparseArray3 = longSparseArray2;
                stringBuilder2.append("SELECT read_state, data, send_state, mid, date, uid, replydata FROM messages WHERE uid IN (");
                stringBuilder2.append(stringBuilder.toString());
                stringBuilder2.append(") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50");
                SQLiteCursor queryFinalized2 = sQLiteDatabase.queryFinalized(stringBuilder2.toString(), new Object[0]);
                int i4 = 0;
                while (queryFinalized2.next()) {
                    NativeByteBuffer byteBufferValue2 = queryFinalized2.byteBufferValue(1);
                    if (byteBufferValue2 != null) {
                        arrayList = arrayList13;
                        TLdeserialize = Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                        TLdeserialize.readAttachPath(byteBufferValue2, UserConfig.getInstance(this.currentAccount).clientUserId);
                        byteBufferValue2.reuse();
                        MessageObject.setUnreadFlags(TLdeserialize, queryFinalized2.intValue(0));
                        TLdeserialize.id = queryFinalized2.intValue(3);
                        TLdeserialize.date = queryFinalized2.intValue(4);
                        arrayList2 = arrayList12;
                        str = str2;
                        TLdeserialize.dialog_id = queryFinalized2.longValue(5);
                        arrayList10.add(TLdeserialize);
                        max = Math.max(i4, TLdeserialize.date);
                        i4 = (int) TLdeserialize.dialog_id;
                        addUsersAndChatsFromMessage(TLdeserialize, arrayList6, arrayList7);
                        TLdeserialize.send_state = queryFinalized2.intValue(2);
                        if (!(TLdeserialize.to_id.channel_id != 0 || MessageObject.isUnread(TLdeserialize) || i4 == 0) || TLdeserialize.id > 0) {
                            TLdeserialize.send_state = 0;
                        }
                        if (i4 == 0 && !queryFinalized2.isNull(5)) {
                            TLdeserialize.random_id = queryFinalized2.longValue(5);
                        }
                        try {
                            if (TLdeserialize.reply_to_msg_id != 0 && ((TLdeserialize.action instanceof TL_messageActionPinMessage) || (TLdeserialize.action instanceof TL_messageActionPaymentSent) || (TLdeserialize.action instanceof TL_messageActionGameScore))) {
                                if (!queryFinalized2.isNull(6)) {
                                    NativeByteBuffer byteBufferValue3 = queryFinalized2.byteBufferValue(6);
                                    if (byteBufferValue3 != null) {
                                        TLdeserialize.replyMessage = Message.TLdeserialize(byteBufferValue3, byteBufferValue3.readInt32(false), false);
                                        TLdeserialize.replyMessage.readAttachPath(byteBufferValue3, UserConfig.getInstance(this.currentAccount).clientUserId);
                                        byteBufferValue3.reuse();
                                        if (TLdeserialize.replyMessage != null) {
                                            if (MessageObject.isMegagroup(TLdeserialize)) {
                                                Message message = TLdeserialize.replyMessage;
                                                message.flags |= Integer.MIN_VALUE;
                                            }
                                            addUsersAndChatsFromMessage(TLdeserialize.replyMessage, arrayList6, arrayList7);
                                        }
                                    }
                                }
                                if (TLdeserialize.replyMessage == null) {
                                    long j = (long) TLdeserialize.reply_to_msg_id;
                                    if (TLdeserialize.to_id.channel_id != 0) {
                                        arrayList3 = arrayList14;
                                        arrayList4 = arrayList10;
                                        j |= ((long) TLdeserialize.to_id.channel_id) << 32;
                                    } else {
                                        arrayList3 = arrayList14;
                                        arrayList4 = arrayList10;
                                    }
                                    try {
                                        if (!arrayList9.contains(Long.valueOf(j))) {
                                            arrayList9.add(Long.valueOf(j));
                                        }
                                        ArrayList arrayList15 = (ArrayList) sparseArray.get(TLdeserialize.reply_to_msg_id);
                                        if (arrayList15 == null) {
                                            arrayList15 = new ArrayList();
                                            sparseArray.put(TLdeserialize.reply_to_msg_id, arrayList15);
                                        }
                                        arrayList15.add(TLdeserialize);
                                    } catch (Exception e2) {
                                        e = e2;
                                        FileLog.e(e);
                                        i4 = max;
                                        str2 = str;
                                        arrayList13 = arrayList;
                                        arrayList12 = arrayList2;
                                        arrayList10 = arrayList4;
                                        arrayList14 = arrayList3;
                                    }
                                    i4 = max;
                                }
                            }
                            arrayList3 = arrayList14;
                            arrayList4 = arrayList10;
                        } catch (Exception e3) {
                            e = e3;
                            arrayList3 = arrayList14;
                            arrayList4 = arrayList10;
                            FileLog.e(e);
                            i4 = max;
                            str2 = str;
                            arrayList13 = arrayList;
                            arrayList12 = arrayList2;
                            arrayList10 = arrayList4;
                            arrayList14 = arrayList3;
                        }
                        i4 = max;
                    } else {
                        arrayList2 = arrayList12;
                        str = str2;
                        arrayList = arrayList13;
                        arrayList3 = arrayList14;
                        arrayList4 = arrayList10;
                    }
                    str2 = str;
                    arrayList13 = arrayList;
                    arrayList12 = arrayList2;
                    arrayList10 = arrayList4;
                    arrayList14 = arrayList3;
                }
                arrayList2 = arrayList12;
                str = str2;
                arrayList = arrayList13;
                arrayList3 = arrayList14;
                arrayList4 = arrayList10;
                queryFinalized2.dispose();
                SQLiteDatabase sQLiteDatabase2 = this.database;
                stringBuilder = new StringBuilder();
                stringBuilder.append("DELETE FROM unread_push_messages WHERE date <= ");
                stringBuilder.append(i4);
                sQLiteDatabase2.executeFast(stringBuilder.toString()).stepThis().dispose();
                max = 0;
                queryFinalized = this.database.queryFinalized("SELECT data, mid, date, uid, random, fm, name, uname, flags FROM unread_push_messages WHERE 1 ORDER BY date DESC LIMIT 50", new Object[0]);
                while (queryFinalized.next()) {
                    byteBufferValue = queryFinalized.byteBufferValue(max);
                    if (byteBufferValue != null) {
                        Message TLdeserialize2 = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(max), max);
                        byteBufferValue.reuse();
                        TLdeserialize2.id = queryFinalized.intValue(1);
                        TLdeserialize2.date = queryFinalized.intValue(2);
                        TLdeserialize2.dialog_id = queryFinalized.longValue(3);
                        TLdeserialize2.random_id = queryFinalized.longValue(4);
                        String str3 = null;
                        String stringValue = queryFinalized.isNull(5) ? null : queryFinalized.stringValue(5);
                        String stringValue2 = queryFinalized.isNull(6) ? null : queryFinalized.stringValue(6);
                        if (!queryFinalized.isNull(7)) {
                            str3 = queryFinalized.stringValue(7);
                        }
                        String str4 = str3;
                        int intValue = queryFinalized.intValue(8);
                        arrayList11.add(new MessageObject(this.currentAccount, TLdeserialize2, stringValue, stringValue2, str4, (intValue & 1) != 0, (intValue & 2) != 0));
                        addUsersAndChatsFromMessage(TLdeserialize2, arrayList6, arrayList7);
                    }
                    max = 0;
                }
                queryFinalized.dispose();
                if (!arrayList9.isEmpty()) {
                    sQLiteDatabase = this.database;
                    Object[] objArr = new Object[1];
                    int i5 = 0;
                    objArr[0] = TextUtils.join(str, arrayList9);
                    queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", objArr), new Object[0]);
                    while (queryFinalized.next()) {
                        byteBufferValue = queryFinalized.byteBufferValue(i5);
                        if (byteBufferValue != null) {
                            Message TLdeserialize3 = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(i5), i5);
                            TLdeserialize3.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                            byteBufferValue.reuse();
                            TLdeserialize3.id = queryFinalized.intValue(1);
                            TLdeserialize3.date = queryFinalized.intValue(2);
                            TLdeserialize3.dialog_id = queryFinalized.longValue(3);
                            addUsersAndChatsFromMessage(TLdeserialize3, arrayList6, arrayList7);
                            arrayList12 = (ArrayList) sparseArray.get(TLdeserialize3.id);
                            if (arrayList12 != null) {
                                for (i = 0; i < arrayList12.size(); i++) {
                                    TLdeserialize = (Message) arrayList12.get(i);
                                    TLdeserialize.replyMessage = TLdeserialize3;
                                    if (MessageObject.isMegagroup(TLdeserialize)) {
                                        TLdeserialize = TLdeserialize.replyMessage;
                                        TLdeserialize.flags |= Integer.MIN_VALUE;
                                    }
                                }
                            }
                        }
                        i5 = 0;
                    }
                    queryFinalized.dispose();
                }
                if (arrayList8.isEmpty()) {
                    arrayList5 = arrayList3;
                } else {
                    arrayList5 = arrayList3;
                    getEncryptedChatsInternal(TextUtils.join(str, arrayList8), arrayList5, arrayList6);
                }
                if (arrayList6.isEmpty()) {
                    arrayList9 = arrayList2;
                } else {
                    arrayList9 = arrayList2;
                    getUsersInternal(TextUtils.join(str, arrayList6), arrayList9);
                }
                if (arrayList7.isEmpty()) {
                    longSparseArray = longSparseArray3;
                    arrayList12 = arrayList;
                } else {
                    arrayList12 = arrayList;
                    getChatsInternal(TextUtils.join(str, arrayList7), arrayList12);
                    i4 = 0;
                    while (i4 < arrayList12.size()) {
                        ArrayList arrayList16;
                        Chat chat = (Chat) arrayList12.get(i4);
                        if (chat == null || (!chat.left && chat.migrated_to == null)) {
                            longSparseArray = longSparseArray3;
                            arrayList16 = arrayList4;
                        } else {
                            long j2 = (long) (-chat.id);
                            sQLiteDatabase2 = this.database;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("UPDATE dialogs SET unread_count = 0 WHERE did = ");
                            stringBuilder.append(j2);
                            sQLiteDatabase2.executeFast(stringBuilder.toString()).stepThis().dispose();
                            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", new Object[]{Long.valueOf(j2)})).stepThis().dispose();
                            arrayList12.remove(i4);
                            i4--;
                            longSparseArray = longSparseArray3;
                            longSparseArray.remove((long) (-chat.id));
                            int i6 = 0;
                            while (i6 < arrayList4.size()) {
                                arrayList16 = arrayList4;
                                if (((Message) arrayList16.get(i6)).dialog_id == ((long) (-chat.id))) {
                                    arrayList16.remove(i6);
                                    i6--;
                                }
                                i6++;
                                arrayList4 = arrayList16;
                            }
                            arrayList16 = arrayList4;
                        }
                        i4++;
                        arrayList4 = arrayList16;
                        longSparseArray3 = longSparseArray;
                    }
                    longSparseArray = longSparseArray3;
                }
                list = arrayList4;
            } else {
                longSparseArray = longSparseArray2;
                arrayList9 = arrayList12;
                arrayList12 = arrayList13;
                arrayList5 = arrayList14;
                list = arrayList10;
            }
            Collections.reverse(list);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$eKBv4VqPoAycvjAdJDsv7Eh2-yk(this, longSparseArray, list, arrayList11, arrayList9, arrayList12, arrayList5));
        } catch (Exception e4) {
            FileLog.e(e4);
        }
    }

    public /* synthetic */ void lambda$null$26$MessagesStorage(LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        NotificationsController.getInstance(this.currentAccount).processLoadedUnreadMessages(longSparseArray, arrayList, arrayList2, arrayList3, arrayList4, arrayList5);
    }

    public void putWallpapers(ArrayList<WallPaper> arrayList, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$a3ybOZTZvY__H1rauHuSb1uqowU(this, i, arrayList));
    }

    public /* synthetic */ void lambda$putWallpapers$28$MessagesStorage(int i, ArrayList arrayList) {
        SQLitePreparedStatement executeFast;
        if (i == 1) {
            try {
                this.database.executeFast("DELETE FROM wallpapers2 WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.beginTransaction();
        if (i != 0) {
            executeFast = this.database.executeFast("REPLACE INTO wallpapers2 VALUES(?, ?, ?)");
        } else {
            executeFast = this.database.executeFast("UPDATE wallpapers2 SET data = ? WHERE uid = ?");
        }
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) arrayList.get(i2);
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_wallPaper.getObjectSize());
            tL_wallPaper.serializeToStream(nativeByteBuffer);
            if (i != 0) {
                executeFast.bindLong(1, tL_wallPaper.id);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, i == 2 ? -1 : i2);
            } else {
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindLong(2, tL_wallPaper.id);
            }
            executeFast.step();
            nativeByteBuffer.reuse();
        }
        executeFast.dispose();
        this.database.commitTransaction();
    }

    public void getWallpapers() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$YaITESoJj2ihTJkXSbcZlXFJrok(this));
    }

    public void loadWebRecent(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$OPX7zm983PZo8OBYdr_PE4lsw_Q(this, i));
    }

    public /* synthetic */ void lambda$loadWebRecent$32$MessagesStorage(int i) {
        try {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$bkuglSYYF1geLDpPAJAV53PKaLY(this, i, new ArrayList()));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$null$31$MessagesStorage(int i, ArrayList arrayList) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentImagesDidLoad, Integer.valueOf(i), arrayList);
    }

    public void addRecentLocalFile(String str, String str2, Document document) {
        if (str != null && str.length() != 0) {
            if ((str2 != null && str2.length() != 0) || document != null) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$N9rKAuyK7fDFD28Fvg87jln1QSM(this, document, str, str2));
            }
        }
    }

    public /* synthetic */ void lambda$addRecentLocalFile$33$MessagesStorage(Document document, String str, String str2) {
        if (document != null) {
            try {
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(document.getObjectSize());
                document.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindString(2, str);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
        executeFast2.requery();
        executeFast2.bindString(1, str2);
        executeFast2.bindString(2, str);
        executeFast2.step();
        executeFast2.dispose();
    }

    public void clearWebRecent(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$H0BBmvar_caejTvJ4VE1QXPBsD1A(this, i));
    }

    public /* synthetic */ void lambda$clearWebRecent$34$MessagesStorage(int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM web_recent_v3 WHERE type = ");
            stringBuilder.append(i);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putWebRecent(ArrayList<SearchImage> arrayList) {
        if (!arrayList.isEmpty() && arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$_49a2AU5Ybbbu2NAvh2QLg8duZQ(this, arrayList));
        }
    }

    public /* synthetic */ void lambda$putWebRecent$35$MessagesStorage(ArrayList arrayList) {
        try {
            int i;
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int i2 = 0;
            while (true) {
                i = 200;
                if (i2 >= arrayList.size()) {
                    break;
                } else if (i2 == 200) {
                    break;
                } else {
                    SearchImage searchImage = (SearchImage) arrayList.get(i2);
                    executeFast.requery();
                    executeFast.bindString(1, searchImage.id);
                    executeFast.bindInteger(2, searchImage.type);
                    String str = "";
                    executeFast.bindString(3, searchImage.imageUrl != null ? searchImage.imageUrl : str);
                    executeFast.bindString(4, searchImage.thumbUrl != null ? searchImage.thumbUrl : str);
                    if (searchImage.localUrl != null) {
                        str = searchImage.localUrl;
                    }
                    executeFast.bindString(5, str);
                    executeFast.bindInteger(6, searchImage.width);
                    executeFast.bindInteger(7, searchImage.height);
                    executeFast.bindInteger(8, searchImage.size);
                    executeFast.bindInteger(9, searchImage.date);
                    NativeByteBuffer nativeByteBuffer = null;
                    if (searchImage.photo != null) {
                        nativeByteBuffer = new NativeByteBuffer(searchImage.photo.getObjectSize());
                        searchImage.photo.serializeToStream(nativeByteBuffer);
                        executeFast.bindByteBuffer(10, nativeByteBuffer);
                    } else if (searchImage.document != null) {
                        nativeByteBuffer = new NativeByteBuffer(searchImage.document.getObjectSize());
                        searchImage.document.serializeToStream(nativeByteBuffer);
                        executeFast.bindByteBuffer(10, nativeByteBuffer);
                    } else {
                        executeFast.bindNull(10);
                    }
                    executeFast.step();
                    if (nativeByteBuffer != null) {
                        nativeByteBuffer.reuse();
                    }
                    i2++;
                }
            }
            executeFast.dispose();
            this.database.commitTransaction();
            if (arrayList.size() >= 200) {
                this.database.beginTransaction();
                while (i < arrayList.size()) {
                    SQLiteDatabase sQLiteDatabase = this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
                    stringBuilder.append(((SearchImage) arrayList.get(i)).id);
                    stringBuilder.append("'");
                    sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
                    i++;
                }
                this.database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getBlockedUsers() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$BEupDy5c3BGF-KcFfcjl6ARzaOY(this));
    }

    public /* synthetic */ void lambda$getBlockedUsers$36$MessagesStorage() {
        try {
            SparseIntArray sparseIntArray = new SparseIntArray();
            ArrayList arrayList = new ArrayList();
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT * FROM blocked_users WHERE 1", new Object[0]);
            StringBuilder stringBuilder = new StringBuilder();
            while (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(0);
                sparseIntArray.put(intValue, 1);
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(intValue);
            }
            queryFinalized.dispose();
            if (stringBuilder.length() != 0) {
                getUsersInternal(stringBuilder.toString(), arrayList);
            }
            MessagesController.getInstance(this.currentAccount).processLoadedBlockedUsers(sparseIntArray, arrayList, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void deleteBlockedUser(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$5j8V0ENnXcKkdTvPgbUlHPahtOs(this, i));
    }

    public /* synthetic */ void lambda$deleteBlockedUser$37$MessagesStorage(int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM blocked_users WHERE uid = ");
            stringBuilder.append(i);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putBlockedUsers(SparseIntArray sparseIntArray, boolean z) {
        if (sparseIntArray != null && sparseIntArray.size() != 0) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ax880v1yupc3z_pPYHRH5vTJMHA(this, z, sparseIntArray));
        }
    }

    public /* synthetic */ void lambda$putBlockedUsers$38$MessagesStorage(boolean z, SparseIntArray sparseIntArray) {
        if (z) {
            try {
                this.database.executeFast("DELETE FROM blocked_users WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.beginTransaction();
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO blocked_users VALUES(?)");
        int size = sparseIntArray.size();
        for (int i = 0; i < size; i++) {
            executeFast.requery();
            executeFast.bindInteger(1, sparseIntArray.keyAt(i));
            executeFast.step();
        }
        executeFast.dispose();
        this.database.commitTransaction();
    }

    public void deleteUserChannelHistory(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$rSm_0FjKoFMC7lovq2hmPi4Ov50(this, i, i2));
    }

    public /* synthetic */ void lambda$deleteUserChannelHistory$41$MessagesStorage(int i, int i2) {
        long j = (long) (-i);
        try {
            ArrayList arrayList = new ArrayList();
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT data FROM messages WHERE uid = ");
            stringBuilder.append(j);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            while (queryFinalized.next()) {
                try {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                        byteBufferValue.reuse();
                        if (!(TLdeserialize == null || TLdeserialize.from_id != i2 || TLdeserialize.id == 1)) {
                            arrayList.add(Integer.valueOf(TLdeserialize.id));
                            int size;
                            int i3;
                            File pathToAttach;
                            if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                                size = TLdeserialize.media.photo.sizes.size();
                                for (i3 = 0; i3 < size; i3++) {
                                    pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.photo.sizes.get(i3));
                                    if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                        arrayList2.add(pathToAttach);
                                    }
                                }
                            } else if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                File pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                    arrayList2.add(pathToAttach2);
                                }
                                size = TLdeserialize.media.document.thumbs.size();
                                for (i3 = 0; i3 < size; i3++) {
                                    pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.document.thumbs.get(i3));
                                    if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                        arrayList2.add(pathToAttach);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$v2uVbmQ0FBcQTbwwzD5Ge9t6SKQ(this, arrayList, i));
            markMessagesAsDeletedInternal(arrayList, i);
            updateDialogsWithDeletedMessagesInternal(arrayList, null, i);
            FileLoader.getInstance(this.currentAccount).deleteFiles(arrayList2, 0);
            if (!arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$8ZxgsqOEGgBoyGRrPirgyMu9kKY(this, arrayList, i));
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public /* synthetic */ void lambda$null$39$MessagesStorage(ArrayList arrayList, int i) {
        MessagesController.getInstance(this.currentAccount).markChannelDialogMessageAsDeleted(arrayList, i);
    }

    public /* synthetic */ void lambda$null$40$MessagesStorage(ArrayList arrayList, int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(i));
    }

    public void deleteDialog(long j, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$JHZIn_b0-cS8U7AzFp5ZfxUFSio(this, i, j));
    }

    /* JADX WARNING: Removed duplicated region for block: B:85:0x029e A:{Catch:{ Exception -> 0x0037 }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x029e A:{Catch:{ Exception -> 0x0037 }} */
    public /* synthetic */ void lambda$deleteDialog$43$MessagesStorage(int r21, long r22) {
        /*
        r20 = this;
        r1 = r20;
        r2 = r21;
        r3 = r22;
        r5 = " AND mid != ";
        r6 = 3;
        r8 = 0;
        if (r2 != r6) goto L_0x003a;
    L_0x000c:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r9.<init>();	 Catch:{ Exception -> 0x0037 }
        r10 = "SELECT last_mid FROM dialogs WHERE did = ";
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9.append(r3);	 Catch:{ Exception -> 0x0037 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0037 }
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0037 }
        r0 = r0.queryFinalized(r9, r10);	 Catch:{ Exception -> 0x0037 }
        r9 = r0.next();	 Catch:{ Exception -> 0x0037 }
        if (r9 == 0) goto L_0x0030;
    L_0x002b:
        r9 = r0.intValue(r8);	 Catch:{ Exception -> 0x0037 }
        goto L_0x0031;
    L_0x0030:
        r9 = -1;
    L_0x0031:
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        if (r9 == 0) goto L_0x003a;
    L_0x0036:
        return;
    L_0x0037:
        r0 = move-exception;
        goto L_0x046b;
    L_0x003a:
        r9 = (int) r3;
        r10 = "SELECT data FROM messages WHERE uid = ";
        r11 = 2;
        if (r9 == 0) goto L_0x0042;
    L_0x0040:
        if (r2 != r11) goto L_0x0117;
    L_0x0042:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r12.<init>();	 Catch:{ Exception -> 0x0037 }
        r12.append(r10);	 Catch:{ Exception -> 0x0037 }
        r12.append(r3);	 Catch:{ Exception -> 0x0037 }
        r12 = r12.toString();	 Catch:{ Exception -> 0x0037 }
        r13 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0037 }
        r12 = r0.queryFinalized(r12, r13);	 Catch:{ Exception -> 0x0037 }
        r13 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0037 }
        r13.<init>();	 Catch:{ Exception -> 0x0037 }
    L_0x005e:
        r0 = r12.next();	 Catch:{ Exception -> 0x0107 }
        if (r0 == 0) goto L_0x010b;
    L_0x0064:
        r0 = r12.byteBufferValue(r8);	 Catch:{ Exception -> 0x0107 }
        if (r0 == 0) goto L_0x005e;
    L_0x006a:
        r14 = r0.readInt32(r8);	 Catch:{ Exception -> 0x0107 }
        r14 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r14, r8);	 Catch:{ Exception -> 0x0107 }
        r15 = r1.currentAccount;	 Catch:{ Exception -> 0x0107 }
        r15 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ Exception -> 0x0107 }
        r15 = r15.clientUserId;	 Catch:{ Exception -> 0x0107 }
        r14.readAttachPath(r0, r15);	 Catch:{ Exception -> 0x0107 }
        r0.reuse();	 Catch:{ Exception -> 0x0107 }
        if (r14 == 0) goto L_0x005e;
    L_0x0082:
        r0 = r14.media;	 Catch:{ Exception -> 0x0107 }
        if (r0 == 0) goto L_0x005e;
    L_0x0086:
        r0 = r14.media;	 Catch:{ Exception -> 0x0107 }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0107 }
        if (r0 == 0) goto L_0x00bb;
    L_0x008c:
        r0 = r14.media;	 Catch:{ Exception -> 0x0107 }
        r0 = r0.photo;	 Catch:{ Exception -> 0x0107 }
        r0 = r0.sizes;	 Catch:{ Exception -> 0x0107 }
        r0 = r0.size();	 Catch:{ Exception -> 0x0107 }
        r15 = 0;
    L_0x0097:
        if (r15 >= r0) goto L_0x005e;
    L_0x0099:
        r7 = r14.media;	 Catch:{ Exception -> 0x0107 }
        r7 = r7.photo;	 Catch:{ Exception -> 0x0107 }
        r7 = r7.sizes;	 Catch:{ Exception -> 0x0107 }
        r7 = r7.get(r15);	 Catch:{ Exception -> 0x0107 }
        r7 = (org.telegram.tgnet.TLRPC.PhotoSize) r7;	 Catch:{ Exception -> 0x0107 }
        r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7);	 Catch:{ Exception -> 0x0107 }
        if (r7 == 0) goto L_0x00b8;
    L_0x00ab:
        r16 = r7.toString();	 Catch:{ Exception -> 0x0107 }
        r16 = r16.length();	 Catch:{ Exception -> 0x0107 }
        if (r16 <= 0) goto L_0x00b8;
    L_0x00b5:
        r13.add(r7);	 Catch:{ Exception -> 0x0107 }
    L_0x00b8:
        r15 = r15 + 1;
        goto L_0x0097;
    L_0x00bb:
        r0 = r14.media;	 Catch:{ Exception -> 0x0107 }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0107 }
        if (r0 == 0) goto L_0x005e;
    L_0x00c1:
        r0 = r14.media;	 Catch:{ Exception -> 0x0107 }
        r0 = r0.document;	 Catch:{ Exception -> 0x0107 }
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0);	 Catch:{ Exception -> 0x0107 }
        if (r0 == 0) goto L_0x00d8;
    L_0x00cb:
        r7 = r0.toString();	 Catch:{ Exception -> 0x0107 }
        r7 = r7.length();	 Catch:{ Exception -> 0x0107 }
        if (r7 <= 0) goto L_0x00d8;
    L_0x00d5:
        r13.add(r0);	 Catch:{ Exception -> 0x0107 }
    L_0x00d8:
        r0 = r14.media;	 Catch:{ Exception -> 0x0107 }
        r0 = r0.document;	 Catch:{ Exception -> 0x0107 }
        r0 = r0.thumbs;	 Catch:{ Exception -> 0x0107 }
        r0 = r0.size();	 Catch:{ Exception -> 0x0107 }
        r7 = 0;
    L_0x00e3:
        if (r7 >= r0) goto L_0x005e;
    L_0x00e5:
        r15 = r14.media;	 Catch:{ Exception -> 0x0107 }
        r15 = r15.document;	 Catch:{ Exception -> 0x0107 }
        r15 = r15.thumbs;	 Catch:{ Exception -> 0x0107 }
        r15 = r15.get(r7);	 Catch:{ Exception -> 0x0107 }
        r15 = (org.telegram.tgnet.TLRPC.PhotoSize) r15;	 Catch:{ Exception -> 0x0107 }
        r15 = org.telegram.messenger.FileLoader.getPathToAttach(r15);	 Catch:{ Exception -> 0x0107 }
        if (r15 == 0) goto L_0x0104;
    L_0x00f7:
        r16 = r15.toString();	 Catch:{ Exception -> 0x0107 }
        r16 = r16.length();	 Catch:{ Exception -> 0x0107 }
        if (r16 <= 0) goto L_0x0104;
    L_0x0101:
        r13.add(r15);	 Catch:{ Exception -> 0x0107 }
    L_0x0104:
        r7 = r7 + 1;
        goto L_0x00e3;
    L_0x0107:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0037 }
    L_0x010b:
        r12.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x0037 }
        r0 = org.telegram.messenger.FileLoader.getInstance(r0);	 Catch:{ Exception -> 0x0037 }
        r0.deleteFiles(r13, r2);	 Catch:{ Exception -> 0x0037 }
    L_0x0117:
        r12 = "DELETE FROM media_holes_v2 WHERE uid = ";
        r13 = "DELETE FROM messages_holes WHERE uid = ";
        r14 = "DELETE FROM media_v2 WHERE uid = ";
        r15 = "DELETE FROM media_counts_v2 WHERE uid = ";
        r7 = "DELETE FROM bot_keyboard WHERE uid = ";
        r8 = "DELETE FROM messages WHERE uid = ";
        r0 = 1;
        if (r2 == 0) goto L_0x02b3;
    L_0x0126:
        if (r2 != r6) goto L_0x012a;
    L_0x0128:
        goto L_0x02b3;
    L_0x012a:
        if (r2 != r11) goto L_0x02ae;
    L_0x012c:
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r6.<init>();	 Catch:{ Exception -> 0x0037 }
        r9 = "SELECT last_mid_i, last_mid FROM dialogs WHERE did = ";
        r6.append(r9);	 Catch:{ Exception -> 0x0037 }
        r6.append(r3);	 Catch:{ Exception -> 0x0037 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0037 }
        r9 = 0;
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0037 }
        r2 = r2.queryFinalized(r6, r11);	 Catch:{ Exception -> 0x0037 }
        r6 = r2.next();	 Catch:{ Exception -> 0x0037 }
        if (r6 == 0) goto L_0x02a8;
    L_0x014c:
        r6 = r12;
        r11 = r2.longValue(r9);	 Catch:{ Exception -> 0x0037 }
        r18 = r14;
        r19 = r15;
        r14 = r2.longValue(r0);	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r9.<init>();	 Catch:{ Exception -> 0x0037 }
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9.append(r3);	 Catch:{ Exception -> 0x0037 }
        r10 = " AND mid IN (";
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9.append(r11);	 Catch:{ Exception -> 0x0037 }
        r10 = ",";
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9.append(r14);	 Catch:{ Exception -> 0x0037 }
        r10 = ")";
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0037 }
        r21 = r2;
        r10 = 0;
        r2 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0037 }
        r2 = r0.queryFinalized(r9, r2);	 Catch:{ Exception -> 0x0037 }
        r9 = -1;
    L_0x0189:
        r0 = r2.next();	 Catch:{ Exception -> 0x01be }
        if (r0 == 0) goto L_0x01bb;
    L_0x018f:
        r0 = r2.byteBufferValue(r10);	 Catch:{ Exception -> 0x01be }
        if (r0 == 0) goto L_0x01b5;
    L_0x0195:
        r17 = r9;
        r9 = r0.readInt32(r10);	 Catch:{ Exception -> 0x01b3 }
        r9 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r9, r10);	 Catch:{ Exception -> 0x01b3 }
        r10 = r1.currentAccount;	 Catch:{ Exception -> 0x01b3 }
        r10 = org.telegram.messenger.UserConfig.getInstance(r10);	 Catch:{ Exception -> 0x01b3 }
        r10 = r10.clientUserId;	 Catch:{ Exception -> 0x01b3 }
        r9.readAttachPath(r0, r10);	 Catch:{ Exception -> 0x01b3 }
        r0.reuse();	 Catch:{ Exception -> 0x01b3 }
        if (r9 == 0) goto L_0x01b7;
    L_0x01af:
        r0 = r9.id;	 Catch:{ Exception -> 0x01b3 }
        r9 = r0;
        goto L_0x01b9;
    L_0x01b3:
        r0 = move-exception;
        goto L_0x01c1;
    L_0x01b5:
        r17 = r9;
    L_0x01b7:
        r9 = r17;
    L_0x01b9:
        r10 = 0;
        goto L_0x0189;
    L_0x01bb:
        r17 = r9;
        goto L_0x01c4;
    L_0x01be:
        r0 = move-exception;
        r17 = r9;
    L_0x01c1:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0037 }
    L_0x01c4:
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r8);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r11);	 Catch:{ Exception -> 0x0037 }
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r14);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r13);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r7);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r5 = r19;
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r10 = r18;
        r2.append(r10);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r6);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x0037 }
        r0 = org.telegram.messenger.DataQuery.getInstance(r0);	 Catch:{ Exception -> 0x0037 }
        r2 = 0;
        r0.clearBotKeyboard(r3, r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = "REPLACE INTO messages_holes VALUES(?, ?, ?)";
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r5 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)";
        r2 = r2.executeFast(r5);	 Catch:{ Exception -> 0x0037 }
        r9 = r17;
        r5 = -1;
        if (r9 == r5) goto L_0x02a1;
    L_0x029e:
        createFirstHoles(r3, r0, r2, r9);	 Catch:{ Exception -> 0x0037 }
    L_0x02a1:
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        goto L_0x02aa;
    L_0x02a8:
        r21 = r2;
    L_0x02aa:
        r21.dispose();	 Catch:{ Exception -> 0x0037 }
        return;
    L_0x02ae:
        r6 = r12;
        r10 = r14;
        r5 = r15;
        goto L_0x0392;
    L_0x02b3:
        r6 = r12;
        r10 = r14;
        r5 = r15;
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r11.<init>();	 Catch:{ Exception -> 0x0037 }
        r12 = "DELETE FROM dialogs WHERE did = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0037 }
        r11.append(r3);	 Catch:{ Exception -> 0x0037 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0037 }
        r2 = r2.executeFast(r11);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r11.<init>();	 Catch:{ Exception -> 0x0037 }
        r12 = "DELETE FROM chat_settings_v2 WHERE uid = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0037 }
        r11.append(r3);	 Catch:{ Exception -> 0x0037 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0037 }
        r2 = r2.executeFast(r11);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r11.<init>();	 Catch:{ Exception -> 0x0037 }
        r12 = "DELETE FROM chat_pinned WHERE uid = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0037 }
        r11.append(r3);	 Catch:{ Exception -> 0x0037 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0037 }
        r2 = r2.executeFast(r11);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r11.<init>();	 Catch:{ Exception -> 0x0037 }
        r12 = "DELETE FROM channel_users_v2 WHERE did = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0037 }
        r11.append(r3);	 Catch:{ Exception -> 0x0037 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0037 }
        r2 = r2.executeFast(r11);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r11.<init>();	 Catch:{ Exception -> 0x0037 }
        r12 = "DELETE FROM search_recent WHERE did = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0037 }
        r11.append(r3);	 Catch:{ Exception -> 0x0037 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0037 }
        r2 = r2.executeFast(r11);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r2 = 32;
        r11 = r3 >> r2;
        r2 = (int) r11;	 Catch:{ Exception -> 0x0037 }
        if (r9 == 0) goto L_0x0374;
    L_0x0353:
        if (r2 != r0) goto L_0x0392;
    L_0x0355:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r11 = "DELETE FROM chats WHERE uid = ";
        r2.append(r11);	 Catch:{ Exception -> 0x0037 }
        r2.append(r9);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        goto L_0x0392;
    L_0x0374:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r9.<init>();	 Catch:{ Exception -> 0x0037 }
        r11 = "DELETE FROM enc_chats WHERE uid = ";
        r9.append(r11);	 Catch:{ Exception -> 0x0037 }
        r9.append(r2);	 Catch:{ Exception -> 0x0037 }
        r2 = r9.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
    L_0x0392:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r9 = "UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = ";
        r2.append(r9);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r8);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r7);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r10);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r13);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r6);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x0037 }
        r0 = org.telegram.messenger.DataQuery.getInstance(r0);	 Catch:{ Exception -> 0x0037 }
        r2 = 0;
        r0.clearBotKeyboard(r3, r2);	 Catch:{ Exception -> 0x0037 }
        r0 = new org.telegram.messenger.-$$Lambda$MessagesStorage$uufN6C3qM3gEul3YY7SEzeuJJSo;	 Catch:{ Exception -> 0x0037 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0037 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x0037 }
        goto L_0x046e;
    L_0x046b:
        org.telegram.messenger.FileLog.e(r0);
    L_0x046e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$deleteDialog$43$MessagesStorage(int, long):void");
    }

    public /* synthetic */ void lambda$null$42$MessagesStorage() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
    }

    public void onDeleteQueryComplete(long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$A7k4sc1a2ORoZ72BrFZX2UvSDew(this, j));
    }

    public /* synthetic */ void lambda$onDeleteQueryComplete$44$MessagesStorage(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM media_counts_v2 WHERE uid = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDialogPhotos(int i, int i2, long j, int i3) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$8_E0e9g6gd6Tt2YgTNR0X0Hg2c0(this, j, i, i2, i3));
    }

    public /* synthetic */ void lambda$getDialogPhotos$46$MessagesStorage(long j, int i, int i2, int i3) {
        SQLiteCursor queryFinalized;
        if (j != 0) {
            try {
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY rowid ASC LIMIT %d", new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(i2)}), new Object[0]);
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY rowid ASC LIMIT %d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        TL_photos_photos tL_photos_photos = new TL_photos_photos();
        while (queryFinalized.next()) {
            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
            if (byteBufferValue != null) {
                Photo TLdeserialize = Photo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
                tL_photos_photos.photos.add(TLdeserialize);
            }
        }
        queryFinalized.dispose();
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesStorage$AUGvJb95Vbn4YIDhDnP0kPL96qE(this, tL_photos_photos, i, i2, j, i3));
    }

    public /* synthetic */ void lambda$null$45$MessagesStorage(photos_Photos photos_photos, int i, int i2, long j, int i3) {
        MessagesController.getInstance(this.currentAccount).processLoadedUserPhotos(photos_photos, i, i2, j, true, i3);
    }

    public void clearUserPhotos(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Pbo7BY13-wIDhXQpqgrlfs6wO7g(this, i));
    }

    public /* synthetic */ void lambda$clearUserPhotos$47$MessagesStorage(int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM user_photos WHERE uid = ");
            stringBuilder.append(i);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearUserPhoto(int i, long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$0xVuEDwNuvaAk1dx0tCZ41B3cFo(this, i, j));
    }

    public /* synthetic */ void lambda$clearUserPhoto$48$MessagesStorage(int i, long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM user_photos WHERE uid = ");
            stringBuilder.append(i);
            stringBuilder.append(" AND id = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetDialogs(messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, LongSparseArray<Dialog> longSparseArray, LongSparseArray<MessageObject> longSparseArray2, Message message, int i6) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$5OdwD90GU58z-A-YkFUYQG3gt8E(this, messages_dialogs, i6, i2, i3, i4, i5, message, i, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$resetDialogs$50$MessagesStorage(messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, Message message, int i6, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        messages_Dialogs messages_dialogs2 = messages_dialogs;
        Message message2 = message;
        try {
            long longValue;
            int intValue;
            int i7;
            int i8;
            int i9;
            long j;
            int i10;
            ArrayList arrayList = new ArrayList();
            int size = messages_dialogs2.dialogs.size() - i;
            LongSparseArray longSparseArray3 = new LongSparseArray();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            for (int i11 = i; i11 < messages_dialogs2.dialogs.size(); i11++) {
                arrayList3.add(Long.valueOf(((Dialog) messages_dialogs2.dialogs.get(i11)).id));
            }
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT did, pinned FROM dialogs WHERE 1", new Object[0]);
            int i12 = 0;
            while (queryFinalized.next()) {
                longValue = queryFinalized.longValue(0);
                intValue = queryFinalized.intValue(1);
                i7 = (int) longValue;
                if (i7 != 0) {
                    arrayList.add(Integer.valueOf(i7));
                    if (intValue > 0) {
                        i12 = Math.max(intValue, i12);
                        longSparseArray3.put(longValue, Integer.valueOf(intValue));
                        arrayList2.add(Long.valueOf(longValue));
                    }
                }
            }
            Collections.sort(arrayList2, new -$$Lambda$MessagesStorage$zIYBa_S5rmrWs9lV8u_RVY80bs8(longSparseArray3));
            while (arrayList2.size() < size) {
                arrayList2.add(0, Long.valueOf(0));
            }
            queryFinalized.dispose();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(");
            stringBuilder.append(TextUtils.join(",", arrayList));
            stringBuilder.append(")");
            String stringBuilder2 = stringBuilder.toString();
            this.database.beginTransaction();
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM dialogs WHERE did IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            sQLiteDatabase = this.database;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM messages WHERE uid IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            this.database.executeFast("DELETE FROM polls WHERE 1").stepThis().dispose();
            sQLiteDatabase = this.database;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM bot_keyboard WHERE uid IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            sQLiteDatabase = this.database;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM media_v2 WHERE uid IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            sQLiteDatabase = this.database;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM messages_holes WHERE uid IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            sQLiteDatabase = this.database;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM media_holes_v2 WHERE uid IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            this.database.commitTransaction();
            for (i8 = 0; i8 < size; i8++) {
                Dialog dialog = (Dialog) messages_dialogs2.dialogs.get(i + i8);
                if (!(dialog instanceof TL_dialog) || dialog.pinned) {
                    intValue = arrayList2.indexOf(Long.valueOf(dialog.id));
                    i7 = arrayList3.indexOf(Long.valueOf(dialog.id));
                    if (!(intValue == -1 || i7 == -1)) {
                        Integer num;
                        if (intValue == i7) {
                            num = (Integer) longSparseArray3.get(dialog.id);
                            if (num != null) {
                                dialog.pinnedNum = num.intValue();
                            }
                        } else {
                            num = (Integer) longSparseArray3.get(((Long) arrayList2.get(i7)).longValue());
                            if (num != null) {
                                dialog.pinnedNum = num.intValue();
                            }
                        }
                    }
                    if (dialog.pinnedNum == 0) {
                        dialog.pinnedNum = (size - i8) + i12;
                    }
                }
            }
            putDialogsInternal(messages_dialogs2, 0);
            saveDiffParamsInternal(i2, i3, i4, i5);
            i8 = UserConfig.getInstance(this.currentAccount).getTotalDialogsCount(0);
            UserConfig.getInstance(this.currentAccount).getDialogLoadOffsets(0);
            i8 += messages_dialogs2.dialogs.size();
            size = message2.id;
            int i13 = message2.date;
            if (message2.to_id.channel_id == 0) {
                if (message2.to_id.chat_id != 0) {
                    intValue = message2.to_id.chat_id;
                    for (i9 = 0; i9 < messages_dialogs2.chats.size(); i9++) {
                        Chat chat = (Chat) messages_dialogs2.chats.get(i9);
                        if (chat.id == intValue) {
                            longValue = chat.access_hash;
                            break;
                        }
                    }
                    longValue = 0;
                    j = longValue;
                    i10 = 0;
                    i9 = intValue;
                } else {
                    if (message2.to_id.user_id != 0) {
                        intValue = message2.to_id.user_id;
                        i9 = 0;
                        while (i9 < messages_dialogs2.users.size()) {
                            User user = (User) messages_dialogs2.users.get(i9);
                            if (user.id == intValue) {
                                j = user.access_hash;
                                i9 = 0;
                                i10 = intValue;
                            } else {
                                i9++;
                            }
                        }
                        i10 = intValue;
                        intValue = 0;
                        i9 = 0;
                    } else {
                        intValue = 0;
                        i9 = 0;
                        i10 = 0;
                    }
                    j = 0;
                }
                intValue = 0;
                break;
            }
            intValue = message2.to_id.channel_id;
            for (i7 = 0; i7 < messages_dialogs2.chats.size(); i7++) {
                Chat chat2 = (Chat) messages_dialogs2.chats.get(i7);
                if (chat2.id == intValue) {
                    longValue = chat2.access_hash;
                    break;
                }
            }
            longValue = 0;
            j = longValue;
            i9 = 0;
            i10 = 0;
            int i14 = 0;
            while (i14 < 2) {
                i = i14;
                UserConfig.getInstance(this.currentAccount).setDialogsLoadOffset(i14, size, i13, i10, i9, intValue, j);
                i14 = i;
                UserConfig.getInstance(this.currentAccount).setTotalDialogsCount(i14, i8);
                i14++;
            }
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            MessagesController.getInstance(this.currentAccount).completeDialogsReset(messages_dialogs, i6, i2, i3, i4, i5, longSparseArray, longSparseArray2, message);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    static /* synthetic */ int lambda$null$49(LongSparseArray longSparseArray, Long l, Long l2) {
        Integer num = (Integer) longSparseArray.get(l.longValue());
        Integer num2 = (Integer) longSparseArray.get(l2.longValue());
        if (num.intValue() < num2.intValue()) {
            return 1;
        }
        return num.intValue() > num2.intValue() ? -1 : 0;
    }

    public void putDialogPhotos(int i, photos_Photos photos_photos) {
        if (photos_photos != null && !photos_photos.photos.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$7hQNqEdO4L9DfD5FN4oqpslMtOE(this, i, photos_photos));
        }
    }

    public /* synthetic */ void lambda$putDialogPhotos$51$MessagesStorage(int i, photos_Photos photos_photos) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM user_photos WHERE uid = ");
            stringBuilder.append(i);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
            int size = photos_photos.photos.size();
            for (int i2 = 0; i2 < size; i2++) {
                Photo photo = (Photo) photos_photos.photos.get(i2);
                if (!(photo instanceof TL_photoEmpty)) {
                    executeFast.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(photo.getObjectSize());
                    photo.serializeToStream(nativeByteBuffer);
                    executeFast.bindInteger(1, i);
                    executeFast.bindLong(2, photo.id);
                    executeFast.bindByteBuffer(3, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                }
            }
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void emptyMessagesMedia(ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$cxqbo7mHWSo0fNJM_X4GL6eYcVI(this, arrayList));
    }

    public /* synthetic */ void lambda$emptyMessagesMedia$53$MessagesStorage(ArrayList arrayList) {
        try {
            Message TLdeserialize;
            int size;
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN (%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                    byteBufferValue.reuse();
                    if (TLdeserialize.media != null) {
                        int i;
                        File pathToAttach;
                        if (TLdeserialize.media.document != null) {
                            File pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document, true);
                            if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                arrayList2.add(pathToAttach2);
                            }
                            size = TLdeserialize.media.document.thumbs.size();
                            for (i = 0; i < size; i++) {
                                pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.document.thumbs.get(i));
                                if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                    arrayList2.add(pathToAttach);
                                }
                            }
                            TLdeserialize.media.document = new TL_documentEmpty();
                        } else if (TLdeserialize.media.photo != null) {
                            size = TLdeserialize.media.photo.sizes.size();
                            for (i = 0; i < size; i++) {
                                pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.photo.sizes.get(i));
                                if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                    arrayList2.add(pathToAttach);
                                }
                            }
                            TLdeserialize.media.photo = new TL_photoEmpty();
                        }
                        TLdeserialize.media.flags &= -2;
                        TLdeserialize.id = queryFinalized.intValue(1);
                        TLdeserialize.date = queryFinalized.intValue(2);
                        TLdeserialize.dialog_id = queryFinalized.longValue(3);
                        arrayList3.add(TLdeserialize);
                    }
                }
            }
            queryFinalized.dispose();
            if (!arrayList3.isEmpty()) {
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
                for (size = 0; size < arrayList3.size(); size++) {
                    TLdeserialize = (Message) arrayList3.get(size);
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(TLdeserialize.getObjectSize());
                    TLdeserialize.serializeToStream(nativeByteBuffer);
                    executeFast.requery();
                    executeFast.bindLong(1, (long) TLdeserialize.id);
                    executeFast.bindLong(2, TLdeserialize.dialog_id);
                    executeFast.bindInteger(3, MessageObject.getUnreadFlags(TLdeserialize));
                    executeFast.bindInteger(4, TLdeserialize.send_state);
                    executeFast.bindInteger(5, TLdeserialize.date);
                    executeFast.bindByteBuffer(6, nativeByteBuffer);
                    executeFast.bindInteger(7, MessageObject.isOut(TLdeserialize) ? 1 : 0);
                    executeFast.bindInteger(8, TLdeserialize.ttl);
                    if ((TLdeserialize.flags & 1024) != 0) {
                        executeFast.bindInteger(9, TLdeserialize.views);
                    } else {
                        executeFast.bindInteger(9, getMessageMediaType(TLdeserialize));
                    }
                    executeFast.bindInteger(10, 0);
                    executeFast.bindInteger(11, TLdeserialize.mentioned ? 1 : 0);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                }
                executeFast.dispose();
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$72wTv-LNhymdYFtfN_3iFK6abDA(this, arrayList3));
            }
            FileLoader.getInstance(this.currentAccount).deleteFiles(arrayList2, 0);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$52$MessagesStorage(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, arrayList.get(i));
        }
    }

    public void updateMessagePollResults(long j, TL_poll tL_poll, TL_pollResults tL_pollResults) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$MeVYE7yH6nKl9CFisUBtLcCys5M(this, j, tL_poll, tL_pollResults));
    }

    public /* synthetic */ void lambda$updateMessagePollResults$54$MessagesStorage(long j, TL_poll tL_poll, TL_pollResults tL_pollResults) {
        ArrayList arrayList = null;
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM polls WHERE id = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            while (queryFinalized.next()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(Long.valueOf(queryFinalized.longValue(0)));
            }
            queryFinalized.dispose();
            if (arrayList != null) {
                this.database.beginTransaction();
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    Long l = (Long) arrayList.get(i);
                    SQLiteCursor queryFinalized2 = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{l}), new Object[0]);
                    if (queryFinalized2.next()) {
                        NativeByteBuffer byteBufferValue = queryFinalized2.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                            byteBufferValue.reuse();
                            if (TLdeserialize.media instanceof TL_messageMediaPoll) {
                                TL_messageMediaPoll tL_messageMediaPoll = (TL_messageMediaPoll) TLdeserialize.media;
                                if (tL_poll != null) {
                                    tL_messageMediaPoll.poll = tL_poll;
                                }
                                if (tL_pollResults != null) {
                                    MessageObject.updatePollResults(tL_messageMediaPoll, tL_pollResults);
                                }
                                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
                                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(TLdeserialize.getObjectSize());
                                TLdeserialize.serializeToStream(nativeByteBuffer);
                                executeFast.requery();
                                executeFast.bindByteBuffer(1, nativeByteBuffer);
                                executeFast.bindLong(2, l.longValue());
                                executeFast.step();
                                nativeByteBuffer.reuse();
                                executeFast.dispose();
                            }
                        }
                    } else {
                        this.database.executeFast(String.format(Locale.US, "DELETE FROM polls WHERE mid = %d", new Object[]{l})).stepThis().dispose();
                    }
                    queryFinalized2.dispose();
                }
                this.database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getNewTask(ArrayList<Integer> arrayList, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Qd8bxWSFc8a6uFGP3l6LN94LJX0(this, arrayList));
    }

    public /* synthetic */ void lambda$getNewTask$55$MessagesStorage(ArrayList arrayList) {
        if (arrayList != null) {
            try {
                String join = TextUtils.join(",", arrayList);
                this.database.executeFast(String.format(Locale.US, "DELETE FROM enc_tasks_v2 WHERE mid IN(%s)", new Object[]{join})).stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT mid, date FROM enc_tasks_v2 WHERE date = (SELECT min(date) FROM enc_tasks_v2)", new Object[0]);
        ArrayList arrayList2 = null;
        int i = -1;
        int i2 = 0;
        while (queryFinalized.next()) {
            long longValue = queryFinalized.longValue(0);
            if (i == -1) {
                i = (int) (longValue >> 32);
                if (i < 0) {
                    i = 0;
                }
            }
            i2 = queryFinalized.intValue(1);
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
            }
            arrayList2.add(Integer.valueOf((int) longValue));
        }
        queryFinalized.dispose();
        MessagesController.getInstance(this.currentAccount).processLoadedDeleteTask(i2, arrayList2, i);
    }

    public void markMentionMessageAsRead(int i, int i2, long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$fHxvn4_KZR89I8OEoFY1Nrw_q8Q(this, i, i2, j));
    }

    public /* synthetic */ void lambda$markMentionMessageAsRead$56$MessagesStorage(int i, int i2, long j) {
        long j2 = (long) i;
        if (i2 != 0) {
            j2 |= ((long) i2) << 32;
        }
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid = %d", new Object[]{Long.valueOf(j2)})).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT unread_count_i FROM dialogs WHERE did = ");
            stringBuilder.append(j);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            i2 = queryFinalized.next() ? Math.max(0, queryFinalized.intValue(0) - 1) : 0;
            queryFinalized.dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[]{Integer.valueOf(i2), Long.valueOf(j)})).stepThis().dispose();
            LongSparseArray longSparseArray = new LongSparseArray(1);
            longSparseArray.put(j, Integer.valueOf(i2));
            MessagesController.getInstance(this.currentAccount).processDialogsUpdateRead(null, longSparseArray);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void markMessageAsMention(long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$THY0KdKsakXsZOC5t34TKVGAUow(this, j));
    }

    public /* synthetic */ void lambda$markMessageAsMention$57$MessagesStorage(long j) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET mention = 1, read_state = read_state & ~2 WHERE mid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetMentionsCount(long j, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$M6hqucUGJrTL18vYRO931fjYJmM(this, i, j));
    }

    public /* synthetic */ void lambda$resetMentionsCount$58$MessagesStorage(int i, long j) {
        if (i == 0) {
            try {
                this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j)})).stepThis().dispose();
        LongSparseArray longSparseArray = new LongSparseArray(1);
        longSparseArray.put(j, Integer.valueOf(i));
        MessagesController.getInstance(this.currentAccount).processDialogsUpdateRead(null, longSparseArray);
    }

    public void createTaskForMid(int i, int i2, int i3, int i4, int i5, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$0waFL5dZVG1t1eBO3lcDmjMKa1Q(this, i3, i4, i5, i, i2, z));
    }

    public /* synthetic */ void lambda$createTaskForMid$60$MessagesStorage(int i, int i2, int i3, int i4, int i5, boolean z) {
        if (i <= i2) {
            i = i2;
        }
        i += i3;
        try {
            SparseArray sparseArray = new SparseArray();
            ArrayList arrayList = new ArrayList();
            long j = (long) i4;
            if (i5 != 0) {
                j |= ((long) i5) << 32;
            }
            arrayList.add(Long.valueOf(j));
            sparseArray.put(i, arrayList);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$ImIoTjisPqqQIoE7gWxgZD0OpFw(this, z, arrayList));
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            for (i5 = 0; i5 < sparseArray.size(); i5++) {
                int keyAt = sparseArray.keyAt(i5);
                ArrayList arrayList2 = (ArrayList) sparseArray.get(keyAt);
                for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                    executeFast.requery();
                    executeFast.bindLong(1, ((Long) arrayList2.get(i6)).longValue());
                    executeFast.bindInteger(2, keyAt);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            MessagesController.getInstance(this.currentAccount).didAddedNewTask(i, sparseArray);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$59$MessagesStorage(boolean z, ArrayList arrayList) {
        if (!z) {
            markMessagesContentAsRead(arrayList, 0);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, arrayList);
    }

    public void createTaskForSecretChat(int i, int i2, int i3, int i4, ArrayList<Long> arrayList) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$jXGTw_HLtNgcBqeJswHrKtxRdl8(this, arrayList, i, i4, i2, i3));
    }

    public /* synthetic */ void lambda$createTaskForSecretChat$62$MessagesStorage(ArrayList arrayList, int i, int i2, int i3, int i4) {
        ArrayList arrayList2 = arrayList;
        int i5 = Integer.MAX_VALUE;
        try {
            SQLiteCursor queryFinalized;
            ArrayList arrayList3;
            SparseArray sparseArray = new SparseArray();
            ArrayList arrayList4 = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            String str = ",";
            int i6 = 0;
            if (arrayList2 == null) {
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE uid = %d AND out = %d AND read_state != 0 AND ttl > 0 AND date <= %d AND send_state = 0 AND media != 1", new Object[]{Long.valueOf(((long) i) << 32), Integer.valueOf(i2), Integer.valueOf(i3)}), new Object[0]);
            } else {
                String join = TextUtils.join(str, arrayList2);
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT m.mid, m.ttl FROM messages as m INNER JOIN randoms as r ON m.mid = r.mid WHERE r.random_id IN (%s)", new Object[]{join}), new Object[0]);
            }
            while (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(1);
                long intValue2 = (long) queryFinalized.intValue(i6);
                if (arrayList2 != null) {
                    arrayList4.add(Long.valueOf(intValue2));
                }
                if (intValue > 0) {
                    int i7 = i3;
                    int i8 = i4;
                    intValue = (i7 > i8 ? i7 : i8) + intValue;
                    i5 = Math.min(i5, intValue);
                    ArrayList arrayList5 = (ArrayList) sparseArray.get(intValue);
                    if (arrayList5 == null) {
                        arrayList3 = new ArrayList();
                        sparseArray.put(intValue, arrayList3);
                    } else {
                        arrayList3 = arrayList5;
                    }
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append(str);
                    }
                    stringBuilder.append(intValue2);
                    arrayList3.add(Long.valueOf(intValue2));
                    i6 = 0;
                }
            }
            queryFinalized.dispose();
            if (arrayList2 != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$QNdiEtcoLlKNU5K8OMatI_Azv9A(this, arrayList4));
            }
            if (sparseArray.size() != 0) {
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
                for (int i9 = 0; i9 < sparseArray.size(); i9++) {
                    int keyAt = sparseArray.keyAt(i9);
                    arrayList3 = (ArrayList) sparseArray.get(keyAt);
                    for (int i10 = 0; i10 < arrayList3.size(); i10++) {
                        executeFast.requery();
                        executeFast.bindLong(1, ((Long) arrayList3.get(i10)).longValue());
                        executeFast.bindInteger(2, keyAt);
                        executeFast.step();
                    }
                }
                executeFast.dispose();
                this.database.commitTransaction();
                this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid IN(%s)", new Object[]{stringBuilder.toString()})).stepThis().dispose();
                MessagesController.getInstance(this.currentAccount).didAddedNewTask(i5, sparseArray);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$61$MessagesStorage(ArrayList arrayList) {
        markMessagesContentAsRead(arrayList, 0);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, arrayList);
    }

    private void updateDialogsWithReadMessagesInternal(ArrayList<Integer> arrayList, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList<Long> arrayList2) {
        SparseLongArray sparseLongArray3 = sparseLongArray;
        SparseLongArray sparseLongArray4 = sparseLongArray2;
        ArrayList<Long> arrayList3 = arrayList2;
        try {
            SQLitePreparedStatement executeFast;
            int i;
            LongSparseArray longSparseArray = new LongSparseArray();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            ArrayList arrayList4 = new ArrayList();
            String str = ",";
            int i2 = 2;
            SQLiteCursor queryFinalized;
            if (isEmpty((List) arrayList)) {
                long longValue;
                int i3;
                if (!isEmpty(sparseLongArray)) {
                    int i4 = 0;
                    while (i4 < sparseLongArray.size()) {
                        int keyAt = sparseLongArray3.keyAt(i4);
                        long j = sparseLongArray3.get(keyAt);
                        SQLiteDatabase sQLiteDatabase = this.database;
                        Object[] objArr = new Object[i2];
                        objArr[0] = Integer.valueOf(keyAt);
                        objArr[1] = Long.valueOf(j);
                        queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mid > %d AND read_state IN(0,2) AND out = 0", objArr), new Object[0]);
                        if (queryFinalized.next()) {
                            longSparseArray.put((long) keyAt, Integer.valueOf(queryFinalized.intValue(0)));
                        }
                        queryFinalized.dispose();
                        executeFast = this.database.executeFast("UPDATE dialogs SET inbox_max = max((SELECT inbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        executeFast.requery();
                        long j2 = (long) keyAt;
                        executeFast.bindLong(1, j2);
                        executeFast.bindInteger(2, (int) j);
                        executeFast.bindLong(3, j2);
                        executeFast.step();
                        executeFast.dispose();
                        i4++;
                        sparseLongArray3 = sparseLongArray;
                        i2 = 2;
                    }
                }
                if (isEmpty((List) arrayList2)) {
                    i = 0;
                } else {
                    ArrayList arrayList5 = new ArrayList(arrayList3);
                    String join = TextUtils.join(str, arrayList3);
                    SQLiteDatabase sQLiteDatabase2 = this.database;
                    Object[] objArr2 = new Object[1];
                    i = 0;
                    objArr2[0] = join;
                    SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out, mention, mid FROM messages WHERE mid IN(%s)", objArr2), new Object[0]);
                    while (queryFinalized2.next()) {
                        longValue = queryFinalized2.longValue(i);
                        arrayList5.remove(Long.valueOf(queryFinalized2.longValue(4)));
                        if (queryFinalized2.intValue(1) < 2 && queryFinalized2.intValue(2) == 0 && queryFinalized2.intValue(3) == 1) {
                            Integer num = (Integer) longSparseArray2.get(longValue);
                            if (num == null) {
                                SQLiteDatabase sQLiteDatabase3 = this.database;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("SELECT unread_count_i FROM dialogs WHERE did = ");
                                stringBuilder.append(longValue);
                                SQLiteCursor queryFinalized3 = sQLiteDatabase3.queryFinalized(stringBuilder.toString(), new Object[0]);
                                i = queryFinalized3.next() ? queryFinalized3.intValue(0) : 0;
                                queryFinalized3.dispose();
                                longSparseArray2.put(longValue, Integer.valueOf(Math.max(0, i - 1)));
                            } else {
                                i = 0;
                                longSparseArray2.put(longValue, Integer.valueOf(Math.max(0, num.intValue() - 1)));
                            }
                        }
                        i = 0;
                    }
                    queryFinalized2.dispose();
                    for (i3 = 0; i3 < arrayList5.size(); i3++) {
                        int longValue2 = (int) (((Long) arrayList5.get(i3)).longValue() >> 32);
                        if (longValue2 > 0 && !arrayList4.contains(Integer.valueOf(longValue2))) {
                            arrayList4.add(Integer.valueOf(longValue2));
                        }
                    }
                }
                if (!isEmpty(sparseLongArray2)) {
                    for (int i5 = 0; i5 < sparseLongArray2.size(); i5++) {
                        i3 = sparseLongArray4.keyAt(i5);
                        longValue = sparseLongArray4.get(i3);
                        SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE dialogs SET outbox_max = max((SELECT outbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        executeFast2.requery();
                        long j3 = (long) i3;
                        executeFast2.bindLong(1, j3);
                        executeFast2.bindInteger(2, (int) longValue);
                        executeFast2.bindLong(3, j3);
                        executeFast2.step();
                        executeFast2.dispose();
                    }
                }
            } else {
                String join2 = TextUtils.join(str, arrayList);
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out FROM messages WHERE mid IN(%s)", new Object[]{join2}), new Object[0]);
                while (queryFinalized.next()) {
                    if (queryFinalized.intValue(2) == 0) {
                        if (queryFinalized.intValue(1) == 0) {
                            long longValue3 = queryFinalized.longValue(0);
                            Integer num2 = (Integer) longSparseArray.get(longValue3);
                            if (num2 == null) {
                                longSparseArray.put(longValue3, Integer.valueOf(1));
                            } else {
                                longSparseArray.put(longValue3, Integer.valueOf(num2.intValue() + 1));
                            }
                        }
                    }
                }
                queryFinalized.dispose();
                i = 0;
            }
            if (longSparseArray.size() > 0 || longSparseArray2.size() > 0) {
                this.database.beginTransaction();
                if (longSparseArray.size() > 0) {
                    executeFast = this.database.executeFast("UPDATE dialogs SET unread_count = ? WHERE did = ?");
                    for (int i6 = 0; i6 < longSparseArray.size(); i6++) {
                        executeFast.requery();
                        executeFast.bindInteger(1, ((Integer) longSparseArray.valueAt(i6)).intValue());
                        executeFast.bindLong(2, longSparseArray.keyAt(i6));
                        executeFast.step();
                    }
                    executeFast.dispose();
                }
                if (longSparseArray2.size() > 0) {
                    executeFast = this.database.executeFast("UPDATE dialogs SET unread_count_i = ? WHERE did = ?");
                    for (i = 
/*
Method generation error in method: org.telegram.messenger.MessagesStorage.updateDialogsWithReadMessagesInternal(java.util.ArrayList, org.telegram.messenger.support.SparseLongArray, org.telegram.messenger.support.SparseLongArray, java.util.ArrayList):void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r10_26 'i' int) = (r10_1 'i' int), (r10_25 'i' int), (r10_25 'i' int) binds: {(r10_1 'i' int)=B:20:0x0071, (r10_25 'i' int)=B:61:0x01c0, (r10_25 'i' int)=B:107:0x01f3} in method: org.telegram.messenger.MessagesStorage.updateDialogsWithReadMessagesInternal(java.util.ArrayList, org.telegram.messenger.support.SparseLongArray, org.telegram.messenger.support.SparseLongArray, java.util.ArrayList):void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:280)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 30 more

*/

    private static boolean isEmpty(SparseArray<?> sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    private static boolean isEmpty(SparseLongArray sparseLongArray) {
        return sparseLongArray == null || sparseLongArray.size() == 0;
    }

    private static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private static boolean isEmpty(SparseIntArray sparseIntArray) {
        return sparseIntArray == null || sparseIntArray.size() == 0;
    }

    private static boolean isEmpty(LongSparseArray<?> longSparseArray) {
        return longSparseArray == null || longSparseArray.size() == 0;
    }

    public void updateDialogsWithReadMessages(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList<Long> arrayList, boolean z) {
        if (!isEmpty(sparseLongArray) || !isEmpty((List) arrayList)) {
            if (z) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$HuVw-9modXG1PvWRi5M39U1c5Io(this, sparseLongArray, sparseLongArray2, arrayList));
            } else {
                updateDialogsWithReadMessagesInternal(null, sparseLongArray, sparseLongArray2, arrayList);
            }
        }
    }

    public /* synthetic */ void lambda$updateDialogsWithReadMessages$63$MessagesStorage(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList arrayList) {
        updateDialogsWithReadMessagesInternal(null, sparseLongArray, sparseLongArray2, arrayList);
    }

    public void updateChatParticipants(ChatParticipants chatParticipants) {
        if (chatParticipants != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$WNrK3eaGJN_z5azqahmhmIWkE3A(this, chatParticipants));
        }
    }

    public /* synthetic */ void lambda$updateChatParticipants$65$MessagesStorage(ChatParticipants chatParticipants) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info, pinned, online FROM chat_settings_v2 WHERE uid = ");
            stringBuilder.append(chatParticipants.chat_id);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ChatFull chatFull = null;
            ArrayList arrayList = new ArrayList();
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    chatFull.pinned_msg_id = queryFinalized.intValue(1);
                    chatFull.online_count = queryFinalized.intValue(2);
                }
            }
            queryFinalized.dispose();
            if (chatFull instanceof TL_chatFull) {
                chatFull.participants = chatParticipants;
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$ulejQAFlC9hooRV1YvKCqgXzO6M(this, chatFull));
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                chatFull.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, chatFull.id);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, chatFull.pinned_msg_id);
                executeFast.bindInteger(4, chatFull.online_count);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$64$MessagesStorage(ChatFull chatFull) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void loadChannelAdmins(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$vkLplr1iiZfKPDR5BeiI0z_ix78(this, i));
    }

    public /* synthetic */ void lambda$loadChannelAdmins$66$MessagesStorage(int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT uid FROM channel_admins WHERE did = ");
            stringBuilder.append(i);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ArrayList arrayList = new ArrayList();
            while (queryFinalized.next()) {
                arrayList.add(Integer.valueOf(queryFinalized.intValue(0)));
            }
            queryFinalized.dispose();
            MessagesController.getInstance(this.currentAccount).processLoadedChannelAdmins(arrayList, i, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putChannelAdmins(int i, ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$h7UF1mHo8_cgez5pMGGWfVLEDNE(this, i, arrayList));
    }

    public /* synthetic */ void lambda$putChannelAdmins$67$MessagesStorage(int i, ArrayList arrayList) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM channel_admins WHERE did = ");
            stringBuilder.append(i);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO channel_admins VALUES(?, ?)");
            long currentTimeMillis = System.currentTimeMillis() / 1000;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                executeFast.requery();
                executeFast.bindInteger(1, i);
                executeFast.bindInteger(2, ((Integer) arrayList.get(i2)).intValue());
                executeFast.step();
            }
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateChannelUsers(int i, ArrayList<ChannelParticipant> arrayList) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$PKDY81gEI7WckgRlUMDcenCv2B0(this, i, arrayList));
    }

    public /* synthetic */ void lambda$updateChannelUsers$68$MessagesStorage(int i, ArrayList arrayList) {
        long j = (long) (-i);
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM channel_users_v2 WHERE did = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                ChannelParticipant channelParticipant = (ChannelParticipant) arrayList.get(i2);
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, channelParticipant.user_id);
                executeFast.bindInteger(3, currentTimeMillis);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(channelParticipant.getObjectSize());
                channelParticipant.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(4, nativeByteBuffer);
                nativeByteBuffer.reuse();
                executeFast.step();
                currentTimeMillis--;
            }
            executeFast.dispose();
            this.database.commitTransaction();
            loadChatInfo(i, null, false, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveBotCache(String str, TLObject tLObject) {
        if (tLObject != null && !TextUtils.isEmpty(str)) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$5oH6yJStIOcRh20JIGlIl-XiXB8(this, tLObject, str));
        }
    }

    public /* synthetic */ void lambda$saveBotCache$69$MessagesStorage(TLObject tLObject, String str) {
        try {
            int i;
            SQLitePreparedStatement executeFast;
            NativeByteBuffer nativeByteBuffer;
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (tLObject instanceof TL_messages_botCallbackAnswer) {
                i = ((TL_messages_botCallbackAnswer) tLObject).cache_time;
            } else {
                if (tLObject instanceof TL_messages_botResults) {
                    i = ((TL_messages_botResults) tLObject).cache_time;
                }
                executeFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
                nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                tLObject.serializeToStream(nativeByteBuffer);
                executeFast.bindString(1, str);
                executeFast.bindInteger(2, currentTime);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
            currentTime += i;
            executeFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
            nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
            tLObject.serializeToStream(nativeByteBuffer);
            executeFast.bindString(1, str);
            executeFast.bindInteger(2, currentTime);
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getBotCache(String str, RequestDelegate requestDelegate) {
        if (str != null && requestDelegate != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$N5D8l6tew30SJwDTZFybvHu5l2Q(this, ConnectionsManager.getInstance(this.currentAccount).getCurrentTime(), str, requestDelegate));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x005d A:{Splitter:B:1:0x0001, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:21:0x005d, code skipped:
            r5 = th;
     */
    /* JADX WARNING: Missing block: B:22:0x005e, code skipped:
            r1 = null;
     */
    /* JADX WARNING: Missing block: B:23:0x0060, code skipped:
            r5 = e;
     */
    /* JADX WARNING: Missing block: B:24:0x0061, code skipped:
            r1 = null;
     */
    public /* synthetic */ void lambda$getBotCache$70$MessagesStorage(int r5, java.lang.String r6, org.telegram.tgnet.RequestDelegate r7) {
        /*
        r4 = this;
        r0 = 0;
        r1 = r4.database;	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r2.<init>();	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r3 = "DELETE FROM botcache WHERE date < ";
        r2.append(r3);	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r2.append(r5);	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5 = r2.toString();	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5 = r1.executeFast(r5);	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5 = r5.stepThis();	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5.dispose();	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5 = r4.database;	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r1 = "SELECT data FROM botcache WHERE id = ?";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r3 = 0;
        r2[r3] = r6;	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5 = r5.queryFinalized(r1, r2);	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r6 = r5.next();	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        if (r6 == 0) goto L_0x0056;
    L_0x0033:
        r6 = r5.byteBufferValue(r3);	 Catch:{ Exception -> 0x0050, all -> 0x005d }
        if (r6 == 0) goto L_0x0056;
    L_0x0039:
        r1 = r6.readInt32(r3);	 Catch:{ Exception -> 0x0050, all -> 0x005d }
        r2 = org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer.constructor;	 Catch:{ Exception -> 0x0050, all -> 0x005d }
        if (r1 != r2) goto L_0x0046;
    L_0x0041:
        r1 = org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer.TLdeserialize(r6, r1, r3);	 Catch:{ Exception -> 0x0050, all -> 0x005d }
        goto L_0x004a;
    L_0x0046:
        r1 = org.telegram.tgnet.TLRPC.messages_BotResults.TLdeserialize(r6, r1, r3);	 Catch:{ Exception -> 0x0050, all -> 0x005d }
    L_0x004a:
        r6.reuse();	 Catch:{ Exception -> 0x004e }
        goto L_0x0057;
    L_0x004e:
        r6 = move-exception;
        goto L_0x0052;
    L_0x0050:
        r6 = move-exception;
        r1 = r0;
    L_0x0052:
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ Exception -> 0x005b }
        goto L_0x0057;
    L_0x0056:
        r1 = r0;
    L_0x0057:
        r5.dispose();	 Catch:{ Exception -> 0x005b }
        goto L_0x0065;
    L_0x005b:
        r5 = move-exception;
        goto L_0x0062;
    L_0x005d:
        r5 = move-exception;
        r1 = r0;
        goto L_0x006a;
    L_0x0060:
        r5 = move-exception;
        r1 = r0;
    L_0x0062:
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x0069 }
    L_0x0065:
        r7.run(r1, r0);
        return;
    L_0x0069:
        r5 = move-exception;
    L_0x006a:
        r7.run(r1, r0);
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getBotCache$70$MessagesStorage(int, java.lang.String, org.telegram.tgnet.RequestDelegate):void");
    }

    public void loadUserInfo(User user, boolean z, int i) {
        if (user != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$je5IAym-d6BxB08yKs5wVUpyc2Y(this, user, z, i));
        }
    }

    public /* synthetic */ void lambda$loadUserInfo$71$MessagesStorage(User user, boolean z, int i) {
        UserFull userFull;
        Throwable e;
        User user2 = user;
        MessageObject messageObject = null;
        UserFull userFull2 = null;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info, pinned FROM user_settings WHERE uid = ");
            stringBuilder.append(user2.id);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    userFull2 = UserFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    userFull2.pinned_msg_id = queryFinalized.intValue(1);
                }
            }
            userFull = userFull2;
            try {
                queryFinalized.dispose();
                if (!(userFull == null || userFull.pinned_msg_id == 0)) {
                    messageObject = DataQuery.getInstance(this.currentAccount).loadPinnedMessage((long) user2.id, 0, userFull.pinned_msg_id, false);
                }
            } catch (Exception e2) {
                e = e2;
                try {
                    FileLog.e(e);
                    MessagesController.getInstance(this.currentAccount).processUserInfo(user, userFull, true, z, messageObject, i);
                } catch (Throwable th) {
                    e = th;
                    MessagesController.getInstance(this.currentAccount).processUserInfo(user, userFull, true, z, null, i);
                    throw e;
                }
            }
        } catch (Exception e3) {
            e = e3;
            userFull = null;
            FileLog.e(e);
            MessagesController.getInstance(this.currentAccount).processUserInfo(user, userFull, true, z, messageObject, i);
        } catch (Throwable th2) {
            e = th2;
            userFull = null;
            MessagesController.getInstance(this.currentAccount).processUserInfo(user, userFull, true, z, null, i);
            throw e;
        }
        MessagesController.getInstance(this.currentAccount).processUserInfo(user, userFull, true, z, messageObject, i);
    }

    public void updateUserInfo(UserFull userFull, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ZJSE8_08_CFvIl9ceM5uNr9LJWE(this, z, userFull));
    }

    public /* synthetic */ void lambda$updateUserInfo$72$MessagesStorage(boolean z, UserFull userFull) {
        if (z) {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT uid FROM user_settings WHERE uid = ");
                stringBuilder.append(userFull.user.id);
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
                boolean next = queryFinalized.next();
                queryFinalized.dispose();
                if (!next) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_settings VALUES(?, ?, ?)");
        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(userFull.getObjectSize());
        userFull.serializeToStream(nativeByteBuffer);
        executeFast.bindInteger(1, userFull.user.id);
        executeFast.bindByteBuffer(2, nativeByteBuffer);
        executeFast.bindInteger(3, userFull.pinned_msg_id);
        executeFast.step();
        executeFast.dispose();
        nativeByteBuffer.reuse();
    }

    public void updateChatInfo(ChatFull chatFull, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$wQRFqP7iAa-lqSrkonSQi4_6Mhg(this, chatFull, z));
    }

    public /* synthetic */ void lambda$updateChatInfo$73$MessagesStorage(ChatFull chatFull, boolean z) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT online FROM chat_settings_v2 WHERE uid = ");
            stringBuilder.append(chatFull.id);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
            queryFinalized.dispose();
            if (!z || intValue != -1) {
                if (intValue >= 0 && (chatFull.flags & 8192) == 0) {
                    chatFull.online_count = intValue;
                }
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                chatFull.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, chatFull.id);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, chatFull.pinned_msg_id);
                executeFast.bindInteger(4, chatFull.online_count);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
                if (chatFull instanceof TL_channelFull) {
                    SQLiteDatabase sQLiteDatabase2 = this.database;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("SELECT inbox_max, outbox_max FROM dialogs WHERE did = ");
                    stringBuilder2.append(-chatFull.id);
                    SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized(stringBuilder2.toString(), new Object[0]);
                    if (queryFinalized2.next() && queryFinalized2.intValue(0) < chatFull.read_inbox_max_id) {
                        int intValue2 = queryFinalized2.intValue(1);
                        SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ?, outbox_max = ? WHERE did = ?");
                        executeFast2.bindInteger(1, chatFull.unread_count);
                        executeFast2.bindInteger(2, chatFull.read_inbox_max_id);
                        executeFast2.bindInteger(3, Math.max(intValue2, chatFull.read_outbox_max_id));
                        executeFast2.bindLong(4, (long) (-chatFull.id));
                        executeFast2.step();
                        executeFast2.dispose();
                    }
                    queryFinalized2.dispose();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateUserPinnedMessage(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$GOAby97sA7x3NfX65Nt357VQJ-g(this, i, i2));
    }

    public /* synthetic */ void lambda$updateUserPinnedMessage$75$MessagesStorage(int i, int i2) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info, pinned FROM user_settings WHERE uid = ");
            stringBuilder.append(i);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            UserFull userFull = null;
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    userFull = UserFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    userFull.pinned_msg_id = queryFinalized.intValue(1);
                }
            }
            queryFinalized.dispose();
            if (userFull instanceof UserFull) {
                userFull.pinned_msg_id = i2;
                userFull.flags |= 64;
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$Qr07bGhSmJZ843tjyoHiZXyO8Lk(this, i, userFull));
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_settings VALUES(?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(userFull.getObjectSize());
                userFull.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, i);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, userFull.pinned_msg_id);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$74$MessagesStorage(int i, UserFull userFull) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(i), userFull, null);
    }

    public void updateChatOnlineCount(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$fdvWMLDG46Aj--3rOxRXECTLJ08(this, i2, i));
    }

    public /* synthetic */ void lambda$updateChatOnlineCount$76$MessagesStorage(int i, int i2) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE chat_settings_v2 SET online = ? WHERE uid = ?");
            executeFast.requery();
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateChatPinnedMessage(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$gmPa2uogjhkJ3CVxeIi9nnAtL0Q(this, i, i2));
    }

    public /* synthetic */ void lambda$updateChatPinnedMessage$78$MessagesStorage(int i, int i2) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info, pinned, online FROM chat_settings_v2 WHERE uid = ");
            stringBuilder.append(i);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ChatFull chatFull = null;
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    chatFull.pinned_msg_id = queryFinalized.intValue(1);
                    chatFull.online_count = queryFinalized.intValue(2);
                }
            }
            queryFinalized.dispose();
            if (chatFull != null) {
                if (chatFull instanceof TL_channelFull) {
                    chatFull.pinned_msg_id = i2;
                    chatFull.flags |= 32;
                } else if (chatFull instanceof TL_chatFull) {
                    chatFull.pinned_msg_id = i2;
                    chatFull.flags |= 64;
                }
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$fQ807BQVZXWSAidobcvjp7VNdkA(this, chatFull));
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                chatFull.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, i);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, chatFull.pinned_msg_id);
                executeFast.bindInteger(4, chatFull.online_count);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$77$MessagesStorage(ChatFull chatFull) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void updateChatInfo(int i, int i2, int i3, int i4, int i5) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$R9-zlyqJL94nJ_qdQsRCquC7vPA(this, i, i3, i2, i4, i5));
    }

    public /* synthetic */ void lambda$updateChatInfo$80$MessagesStorage(int i, int i2, int i3, int i4, int i5) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info, pinned, online FROM chat_settings_v2 WHERE uid = ");
            stringBuilder.append(i);
            int i6 = 0;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ChatFull chatFull = null;
            ArrayList arrayList = new ArrayList();
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    chatFull.pinned_msg_id = queryFinalized.intValue(1);
                    chatFull.online_count = queryFinalized.intValue(2);
                }
            }
            queryFinalized.dispose();
            if (chatFull instanceof TL_chatFull) {
                if (i2 == 1) {
                    while (i6 < chatFull.participants.participants.size()) {
                        if (((ChatParticipant) chatFull.participants.participants.get(i6)).user_id == i3) {
                            chatFull.participants.participants.remove(i6);
                            break;
                        }
                        i6++;
                    }
                } else if (i2 == 0) {
                    Iterator it = chatFull.participants.participants.iterator();
                    while (it.hasNext()) {
                        if (((ChatParticipant) it.next()).user_id == i3) {
                            return;
                        }
                    }
                    TL_chatParticipant tL_chatParticipant = new TL_chatParticipant();
                    tL_chatParticipant.user_id = i3;
                    tL_chatParticipant.inviter_id = i4;
                    tL_chatParticipant.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                    chatFull.participants.participants.add(tL_chatParticipant);
                } else if (i2 == 2) {
                    while (i6 < chatFull.participants.participants.size()) {
                        ChatParticipant chatParticipant = (ChatParticipant) chatFull.participants.participants.get(i6);
                        if (chatParticipant.user_id == i3) {
                            Object tL_chatParticipantAdmin;
                            if (i4 == 1) {
                                tL_chatParticipantAdmin = new TL_chatParticipantAdmin();
                                tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                                tL_chatParticipantAdmin.date = chatParticipant.date;
                                tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                            } else {
                                tL_chatParticipantAdmin = new TL_chatParticipant();
                                tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                                tL_chatParticipantAdmin.date = chatParticipant.date;
                                tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                            }
                            chatFull.participants.participants.set(i6, tL_chatParticipantAdmin);
                        } else {
                            i6++;
                        }
                    }
                }
                chatFull.participants.version = i5;
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$NxZU0dQWFf1s1Ei7lgvtohw5Sqw(this, chatFull));
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                chatFull.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, i);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, chatFull.pinned_msg_id);
                executeFast.bindInteger(4, chatFull.online_count);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$79$MessagesStorage(ChatFull chatFull) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public boolean isMigratedChat(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$mE1-UK0oqooqsB707p49sMXA2eo(this, i, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$isMigratedChat$81$MessagesStorage(int i, boolean[] zArr, CountDownLatch countDownLatch) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info FROM chat_settings_v2 WHERE uid = ");
            stringBuilder.append(i);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ChatFull chatFull = null;
            ArrayList arrayList = new ArrayList();
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            boolean z = (chatFull instanceof TL_channelFull) && chatFull.migrated_from_chat_id != 0;
            zArr[0] = z;
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            if (countDownLatch == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (countDownLatch == null) {
                return;
            }
        } catch (Throwable e2) {
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            throw e2;
        }
        countDownLatch.countDown();
    }

    public void loadChatInfo(int i, CountDownLatch countDownLatch, boolean z, boolean z2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$0uAjSDWjYqGf4fkJ0zE63NanRe0(this, i, countDownLatch, z, z2));
    }

    public void processPendingRead(long j, long j2, long j3, int i, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$aN4DBYn4X-NQIiJUQfB_SvktwTk(this, j, j2, z, j3));
    }

    public /* synthetic */ void lambda$processPendingRead$83$MessagesStorage(long j, long j2, boolean z, long j3) {
        long j4 = j;
        try {
            int intValue;
            long longValue;
            SQLitePreparedStatement executeFast;
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT unread_count, inbox_max, last_mid FROM dialogs WHERE did = ");
            stringBuilder.append(j4);
            int i = 0;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            long j5 = 0;
            if (queryFinalized.next()) {
                intValue = queryFinalized.intValue(0);
                j5 = (long) queryFinalized.intValue(1);
                longValue = queryFinalized.longValue(2);
            } else {
                longValue = 0;
                intValue = 0;
            }
            queryFinalized.dispose();
            this.database.beginTransaction();
            int i2 = (int) j4;
            String str = "SELECT changes()";
            int intValue2;
            if (i2 != 0) {
                j5 = Math.max(j5, (long) ((int) j2));
                if (z) {
                    j5 |= ((long) (-i2)) << 32;
                }
                executeFast = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
                executeFast.requery();
                executeFast.bindLong(1, j4);
                executeFast.bindLong(2, j5);
                executeFast.step();
                executeFast.dispose();
                if (j5 < longValue) {
                    queryFinalized = this.database.queryFinalized(str, new Object[0]);
                    intValue2 = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
                    queryFinalized.dispose();
                    i = Math.max(0, intValue - intValue2);
                }
                executeFast = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid <= ?");
                executeFast.requery();
                executeFast.bindLong(1, j4);
                executeFast.bindLong(2, j5);
                executeFast.step();
                executeFast.dispose();
            } else {
                j5 = (long) ((int) j3);
                executeFast = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid >= ? AND read_state IN(0,2) AND out = 0");
                executeFast.requery();
                executeFast.bindLong(1, j4);
                executeFast.bindLong(2, j5);
                executeFast.step();
                executeFast.dispose();
                if (j5 > longValue) {
                    queryFinalized = this.database.queryFinalized(str, new Object[0]);
                    intValue2 = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
                    queryFinalized.dispose();
                    i = Math.max(0, intValue - intValue2);
                }
                executeFast = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid >= ?");
                executeFast.requery();
                executeFast.bindLong(1, j4);
                executeFast.bindLong(2, j5);
                executeFast.step();
                executeFast.dispose();
            }
            executeFast = this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ? WHERE did = ?");
            executeFast.requery();
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, (int) j5);
            executeFast.bindLong(3, j4);
            executeFast.step();
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putContacts(ArrayList<TL_contact> arrayList, boolean z) {
        if (!arrayList.isEmpty() || z) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$HcaX9dRNfoCWz9JypBQKVvGyzlc(this, z, new ArrayList(arrayList)));
        }
    }

    public /* synthetic */ void lambda$putContacts$84$MessagesStorage(boolean z, ArrayList arrayList) {
        if (z) {
            try {
                this.database.executeFast("DELETE FROM contacts WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.beginTransaction();
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO contacts VALUES(?, ?)");
        for (int i = 0; i < arrayList.size(); i++) {
            TL_contact tL_contact = (TL_contact) arrayList.get(i);
            executeFast.requery();
            int i2 = 1;
            executeFast.bindInteger(1, tL_contact.user_id);
            if (!tL_contact.mutual) {
                i2 = 0;
            }
            executeFast.bindInteger(2, i2);
            executeFast.step();
        }
        executeFast.dispose();
        this.database.commitTransaction();
    }

    public void deleteContacts(ArrayList<Integer> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$uSee1vUIX506LCuuoBQenODlCTM(this, arrayList));
        }
    }

    public /* synthetic */ void lambda$deleteContacts$85$MessagesStorage(ArrayList arrayList) {
        try {
            String join = TextUtils.join(",", arrayList);
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM contacts WHERE uid IN(");
            stringBuilder.append(join);
            stringBuilder.append(")");
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void applyPhoneBookUpdates(String str, String str2) {
        if (str.length() != 0 || str2.length() != 0) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$WFFD4MdCB_TGVpHvcoTFvegKH40(this, str, str2));
        }
    }

    public /* synthetic */ void lambda$applyPhoneBookUpdates$86$MessagesStorage(String str, String str2) {
        try {
            if (str.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 0 WHERE sphone IN(%s)", new Object[]{str})).stepThis().dispose();
            }
            if (str2.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 1 WHERE sphone IN(%s)", new Object[]{str2})).stepThis().dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putCachedPhoneBook(HashMap<String, Contact> hashMap, boolean z, boolean z2) {
        if (hashMap == null) {
            return;
        }
        if (!hashMap.isEmpty() || z || z2) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$6U7Ip4iN7a0tKqqSoHjtmcq0tZI(this, hashMap, z));
        }
    }

    public /* synthetic */ void lambda$putCachedPhoneBook$87$MessagesStorage(HashMap hashMap, boolean z) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.currentAccount);
                stringBuilder.append(" save contacts to db ");
                stringBuilder.append(hashMap.size());
                FileLog.d(stringBuilder.toString());
            }
            this.database.executeFast("DELETE FROM user_contacts_v7 WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM user_phones_v7 WHERE 1").stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_contacts_v7 VALUES(?, ?, ?, ?, ?)");
            SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO user_phones_v7 VALUES(?, ?, ?, ?)");
            Iterator it = hashMap.entrySet().iterator();
            while (true) {
                int i = 0;
                if (!it.hasNext()) {
                    break;
                }
                Contact contact = (Contact) ((Entry) it.next()).getValue();
                if (!contact.phones.isEmpty()) {
                    if (!contact.shortPhones.isEmpty()) {
                        executeFast.requery();
                        executeFast.bindString(1, contact.key);
                        executeFast.bindInteger(2, contact.contact_id);
                        executeFast.bindString(3, contact.first_name);
                        executeFast.bindString(4, contact.last_name);
                        executeFast.bindInteger(5, contact.imported);
                        executeFast.step();
                        while (i < contact.phones.size()) {
                            executeFast2.requery();
                            executeFast2.bindString(1, contact.key);
                            executeFast2.bindString(2, (String) contact.phones.get(i));
                            executeFast2.bindString(3, (String) contact.shortPhones.get(i));
                            executeFast2.bindInteger(4, ((Integer) contact.phoneDeleted.get(i)).intValue());
                            executeFast2.step();
                            i++;
                        }
                    }
                }
            }
            executeFast.dispose();
            executeFast2.dispose();
            this.database.commitTransaction();
            if (z) {
                this.database.executeFast("DROP TABLE IF EXISTS user_contacts_v6;").stepThis().dispose();
                this.database.executeFast("DROP TABLE IF EXISTS user_phones_v6;").stepThis().dispose();
                getCachedPhoneBook(false);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getCachedPhoneBook(boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$78MpUcaUc7Kvwq4yzDM50Ob5JQA(this, z));
    }

    public void getContacts() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$mZ4JmBtl7S3ynBIPluDBPLkwlOk(this));
    }

    public /* synthetic */ void lambda$getContacts$89$MessagesStorage() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
            StringBuilder stringBuilder = new StringBuilder();
            while (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(0);
                TL_contact tL_contact = new TL_contact();
                tL_contact.user_id = intValue;
                tL_contact.mutual = queryFinalized.intValue(1) == 1;
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(",");
                }
                arrayList.add(tL_contact);
                stringBuilder.append(tL_contact.user_id);
            }
            queryFinalized.dispose();
            if (stringBuilder.length() != 0) {
                getUsersInternal(stringBuilder.toString(), arrayList2);
            }
        } catch (Exception e) {
            arrayList.clear();
            arrayList2.clear();
            FileLog.e(e);
        }
        ContactsController.getInstance(this.currentAccount).processLoadedContacts(arrayList, arrayList2, 1);
    }

    public void getUnsentMessages(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$_BB9IdA3icWPJG1ZHsvdbHwqMgc(this, i));
    }

    public /* synthetic */ void lambda$getUnsentMessages$90$MessagesStorage(int i) {
        try {
            int i2;
            SparseArray sparseArray = new SparseArray();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            ArrayList arrayList7 = new ArrayList();
            ArrayList arrayList8 = new ArrayList();
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE (m.mid < 0 AND m.send_state = 1) OR (m.mid > 0 AND m.send_state = 3) ORDER BY m.mid DESC LIMIT ");
            stringBuilder.append(i);
            boolean z = false;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z), z);
                    TLdeserialize.send_state = queryFinalized.intValue(2);
                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                    byteBufferValue.reuse();
                    if (sparseArray.indexOfKey(TLdeserialize.id) < 0) {
                        MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(z));
                        TLdeserialize.id = queryFinalized.intValue(3);
                        TLdeserialize.date = queryFinalized.intValue(4);
                        if (!queryFinalized.isNull(5)) {
                            TLdeserialize.random_id = queryFinalized.longValue(5);
                        }
                        TLdeserialize.dialog_id = queryFinalized.longValue(6);
                        TLdeserialize.seq_in = queryFinalized.intValue(7);
                        TLdeserialize.seq_out = queryFinalized.intValue(8);
                        TLdeserialize.ttl = queryFinalized.intValue(9);
                        arrayList.add(TLdeserialize);
                        sparseArray.put(TLdeserialize.id, TLdeserialize);
                        int i3 = (int) TLdeserialize.dialog_id;
                        int i4 = (int) (TLdeserialize.dialog_id >> 32);
                        if (i3 != 0) {
                            if (i4 == 1) {
                                if (!arrayList7.contains(Integer.valueOf(i3))) {
                                    arrayList7.add(Integer.valueOf(i3));
                                }
                            } else if (i3 < 0) {
                                i2 = -i3;
                                if (!arrayList6.contains(Integer.valueOf(i2))) {
                                    arrayList6.add(Integer.valueOf(i2));
                                }
                            } else if (!arrayList5.contains(Integer.valueOf(i3))) {
                                arrayList5.add(Integer.valueOf(i3));
                            }
                        } else if (!arrayList8.contains(Integer.valueOf(i4))) {
                            arrayList8.add(Integer.valueOf(i4));
                        }
                        addUsersAndChatsFromMessage(TLdeserialize, arrayList5, arrayList6);
                        if (TLdeserialize.send_state != 3 && ((TLdeserialize.to_id.channel_id == 0 && !MessageObject.isUnread(TLdeserialize) && i3 != 0) || TLdeserialize.id > 0)) {
                            TLdeserialize.send_state = 0;
                        }
                        if (i3 == 0 && !queryFinalized.isNull(5)) {
                            TLdeserialize.random_id = queryFinalized.longValue(5);
                        }
                        z = false;
                    }
                }
                z = false;
            }
            queryFinalized.dispose();
            String str = ",";
            if (!arrayList8.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(str, arrayList8), arrayList4, arrayList5);
            }
            if (!arrayList5.isEmpty()) {
                getUsersInternal(TextUtils.join(str, arrayList5), arrayList2);
            }
            if (!(arrayList6.isEmpty() && arrayList7.isEmpty())) {
                StringBuilder stringBuilder2 = new StringBuilder();
                for (int i5 = 0; i5 < arrayList6.size(); i5++) {
                    Integer num = (Integer) arrayList6.get(i5);
                    if (stringBuilder2.length() != 0) {
                        stringBuilder2.append(str);
                    }
                    stringBuilder2.append(num);
                }
                for (i2 = 0; i2 < arrayList7.size(); i2++) {
                    Integer num2 = (Integer) arrayList7.get(i2);
                    if (stringBuilder2.length() != 0) {
                        stringBuilder2.append(str);
                    }
                    stringBuilder2.append(-num2.intValue());
                }
                getChatsInternal(stringBuilder2.toString(), arrayList3);
            }
            SendMessagesHelper.getInstance(this.currentAccount).processUnsentMessages(arrayList, arrayList2, arrayList3, arrayList4);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean checkMessageByRandomId(long j) {
        boolean[] zArr = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$8KXuMUbO-_2E0sdNf0qxL08t6QA(this, j, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /*  JADX ERROR: NullPointerException in pass: ProcessVariables
        java.lang.NullPointerException
        	at jadx.core.dex.visitors.regions.ProcessVariables.addToUsageMap(ProcessVariables.java:278)
        	at jadx.core.dex.visitors.regions.ProcessVariables.access$000(ProcessVariables.java:31)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processInsn(ProcessVariables.java:163)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processBlockTraced(ProcessVariables.java:129)
        	at jadx.core.dex.visitors.regions.TracedRegionVisitor.processBlock(TracedRegionVisitor.java:23)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:53)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
        	at jadx.core.dex.visitors.regions.ProcessVariables.visit(ProcessVariables.java:183)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$checkMessageByRandomId$91$MessagesStorage(long r7, boolean[] r9, java.util.concurrent.CountDownLatch r10) {
        /*
        r6 = this;
        r0 = 0;
        r1 = r6.database;	 Catch:{ Exception -> 0x0028 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x0028 }
        r3 = "SELECT random_id FROM randoms WHERE random_id = %d";	 Catch:{ Exception -> 0x0028 }
        r4 = 1;	 Catch:{ Exception -> 0x0028 }
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0028 }
        r7 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x0028 }
        r8 = 0;	 Catch:{ Exception -> 0x0028 }
        r5[r8] = r7;	 Catch:{ Exception -> 0x0028 }
        r7 = java.lang.String.format(r2, r3, r5);	 Catch:{ Exception -> 0x0028 }
        r2 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0028 }
        r0 = r1.queryFinalized(r7, r2);	 Catch:{ Exception -> 0x0028 }
        r7 = r0.next();	 Catch:{ Exception -> 0x0028 }
        if (r7 == 0) goto L_0x0023;	 Catch:{ Exception -> 0x0028 }
    L_0x0021:
        r9[r8] = r4;	 Catch:{ Exception -> 0x0028 }
    L_0x0023:
        if (r0 == 0) goto L_0x0031;
    L_0x0025:
        goto L_0x002e;
    L_0x0026:
        r7 = move-exception;
        goto L_0x0035;
    L_0x0028:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);	 Catch:{ all -> 0x0026 }
        if (r0 == 0) goto L_0x0031;
    L_0x002e:
        r0.dispose();
    L_0x0031:
        r10.countDown();
        return;
    L_0x0035:
        if (r0 == 0) goto L_0x003a;
    L_0x0037:
        r0.dispose();
    L_0x003a:
        throw r7;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageByRandomId$91$MessagesStorage(long, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public boolean checkMessageId(long j, int i) {
        boolean[] zArr = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ieE3QCLlzTc2C0Bdl_7mP9oOo20(this, j, i, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /*  JADX ERROR: NullPointerException in pass: ProcessVariables
        java.lang.NullPointerException
        	at jadx.core.dex.visitors.regions.ProcessVariables.addToUsageMap(ProcessVariables.java:278)
        	at jadx.core.dex.visitors.regions.ProcessVariables.access$000(ProcessVariables.java:31)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processInsn(ProcessVariables.java:163)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processBlockTraced(ProcessVariables.java:129)
        	at jadx.core.dex.visitors.regions.TracedRegionVisitor.processBlock(TracedRegionVisitor.java:23)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:53)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
        	at jadx.core.dex.visitors.regions.ProcessVariables.visit(ProcessVariables.java:183)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$checkMessageId$92$MessagesStorage(long r6, int r8, boolean[] r9, java.util.concurrent.CountDownLatch r10) {
        /*
        r5 = this;
        r0 = 0;
        r1 = r5.database;	 Catch:{ Exception -> 0x002f }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x002f }
        r3 = "SELECT mid FROM messages WHERE uid = %d AND mid = %d";	 Catch:{ Exception -> 0x002f }
        r4 = 2;	 Catch:{ Exception -> 0x002f }
        r4 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x002f }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x002f }
        r7 = 0;	 Catch:{ Exception -> 0x002f }
        r4[r7] = r6;	 Catch:{ Exception -> 0x002f }
        r6 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x002f }
        r8 = 1;	 Catch:{ Exception -> 0x002f }
        r4[r8] = r6;	 Catch:{ Exception -> 0x002f }
        r6 = java.lang.String.format(r2, r3, r4);	 Catch:{ Exception -> 0x002f }
        r2 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x002f }
        r0 = r1.queryFinalized(r6, r2);	 Catch:{ Exception -> 0x002f }
        r6 = r0.next();	 Catch:{ Exception -> 0x002f }
        if (r6 == 0) goto L_0x002a;	 Catch:{ Exception -> 0x002f }
    L_0x0028:
        r9[r7] = r8;	 Catch:{ Exception -> 0x002f }
    L_0x002a:
        if (r0 == 0) goto L_0x0038;
    L_0x002c:
        goto L_0x0035;
    L_0x002d:
        r6 = move-exception;
        goto L_0x003c;
    L_0x002f:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ all -> 0x002d }
        if (r0 == 0) goto L_0x0038;
    L_0x0035:
        r0.dispose();
    L_0x0038:
        r10.countDown();
        return;
    L_0x003c:
        if (r0 == 0) goto L_0x0041;
    L_0x003e:
        r0.dispose();
    L_0x0041:
        throw r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageId$92$MessagesStorage(long, int, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void getUnreadMention(long j, IntCallback intCallback) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$J7fUkAue-8RtoiXj7OHNfkx3IQQ(this, j, intCallback));
    }

    public /* synthetic */ void lambda$getUnreadMention$94$MessagesStorage(long j, IntCallback intCallback) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            Object[] objArr = new Object[1];
            int i = 0;
            objArr[0] = Long.valueOf(j);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", objArr), new Object[0]);
            if (queryFinalized.next()) {
                i = queryFinalized.intValue(0);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$OU4ggCaHVMLLI5DqY7_ewEcCU68(intCallback, i));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getMessages(long j, int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$T7pTNNmcP1BzePZ9DYWRZWDCVcI(this, i, i2, z, j, i6, i4, i3, i5, i7));
    }

    static /* synthetic */ int lambda$null$95(Message message, Message message2) {
        int i;
        int i2 = message.id;
        if (i2 > 0) {
            i = message2.id;
            if (i > 0) {
                if (i2 > i) {
                    return -1;
                }
                if (i2 < i) {
                    return 1;
                }
                return 0;
            }
        }
        i2 = message.id;
        if (i2 < 0) {
            i = message2.id;
            if (i < 0) {
                if (i2 < i) {
                    return -1;
                }
                if (i2 > i) {
                    return 1;
                }
                return 0;
            }
        }
        int i3 = message.date;
        int i4 = message2.date;
        if (i3 > i4) {
            return -1;
        }
        if (i3 < i4) {
            return 1;
        }
        return 0;
    }

    public void clearSentMedia() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$RRDsYlIQYVK88IFnP726jIWVW3o(this));
    }

    public /* synthetic */ void lambda$clearSentMedia$97$MessagesStorage() {
        try {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public Object[] getSentFile(String str, int i) {
        if (str == null || str.toLowerCase().endsWith("attheme")) {
            return null;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Object[] objArr = new Object[2];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Mapyxi_AR1_6Me8RqPE00u8k5H4(this, str, i, objArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (objArr[0] != null) {
            return objArr;
        }
        return null;
    }

    public void putSentFile(String str, TLObject tLObject, int i, String str2) {
        if (str != null && tLObject != null && str2 != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$n2GPnAOG5aN55gasvKHgSzWbYCo(this, str, tLObject, i, str2));
        }
    }

    public void updateEncryptedChatSeq(EncryptedChat encryptedChat, boolean z) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$WjmzaNMoaj5gwo5VkkK5s4gBFCs(this, encryptedChat, z));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChatSeq$100$MessagesStorage(EncryptedChat encryptedChat, boolean z) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ?, mtproto_seq = ? WHERE uid = ?");
            sQLitePreparedStatement.bindInteger(1, encryptedChat.seq_in);
            sQLitePreparedStatement.bindInteger(2, encryptedChat.seq_out);
            sQLitePreparedStatement.bindInteger(3, (encryptedChat.key_use_count_in << 16) | encryptedChat.key_use_count_out);
            sQLitePreparedStatement.bindInteger(4, encryptedChat.in_seq_no);
            sQLitePreparedStatement.bindInteger(5, encryptedChat.mtproto_seq);
            sQLitePreparedStatement.bindInteger(6, encryptedChat.id);
            sQLitePreparedStatement.step();
            if (z) {
                long j = ((long) encryptedChat.id) << 32;
                this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN (SELECT m.mid FROM messages as m LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.uid = %d AND m.date = 0 AND m.mid < 0 AND s.seq_out <= %d)", new Object[]{Long.valueOf(j), Integer.valueOf(encryptedChat.in_seq_no)})).stepThis().dispose();
            }
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Throwable e2) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw e2;
        }
        sQLitePreparedStatement.dispose();
    }

    public void updateEncryptedChatTTL(EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$mQdvR6EOb9ClWzyxmBrmOTkuADk(this, encryptedChat));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChatTTL$101$MessagesStorage(EncryptedChat encryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
            sQLitePreparedStatement.bindInteger(1, encryptedChat.ttl);
            sQLitePreparedStatement.bindInteger(2, encryptedChat.id);
            sQLitePreparedStatement.step();
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Throwable e2) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw e2;
        }
        sQLitePreparedStatement.dispose();
    }

    public void updateEncryptedChatLayer(EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$pw95lLpBGLX8Gr5I4jENKfeSc9w(this, encryptedChat));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChatLayer$102$MessagesStorage(EncryptedChat encryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
            sQLitePreparedStatement.bindInteger(1, encryptedChat.layer);
            sQLitePreparedStatement.bindInteger(2, encryptedChat.id);
            sQLitePreparedStatement.step();
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Throwable e2) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw e2;
        }
        sQLitePreparedStatement.dispose();
    }

    public void updateEncryptedChat(EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$4HFs78OJm5kLRBfncJYDCN_t7T0(this, encryptedChat));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChat$103$MessagesStorage(EncryptedChat encryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            if ((encryptedChat.key_hash == null || encryptedChat.key_hash.length < 16) && encryptedChat.auth_key != null) {
                encryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key);
            }
            sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ?, in_seq_no = ?, admin_id = ?, mtproto_seq = ? WHERE uid = ?");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(encryptedChat.getObjectSize());
            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(encryptedChat.a_or_b != null ? encryptedChat.a_or_b.length : 1);
            NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(encryptedChat.auth_key != null ? encryptedChat.auth_key.length : 1);
            NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(encryptedChat.future_auth_key != null ? encryptedChat.future_auth_key.length : 1);
            NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(encryptedChat.key_hash != null ? encryptedChat.key_hash.length : 1);
            encryptedChat.serializeToStream(nativeByteBuffer);
            sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
            if (encryptedChat.a_or_b != null) {
                nativeByteBuffer2.writeBytes(encryptedChat.a_or_b);
            }
            if (encryptedChat.auth_key != null) {
                nativeByteBuffer3.writeBytes(encryptedChat.auth_key);
            }
            if (encryptedChat.future_auth_key != null) {
                nativeByteBuffer4.writeBytes(encryptedChat.future_auth_key);
            }
            if (encryptedChat.key_hash != null) {
                nativeByteBuffer5.writeBytes(encryptedChat.key_hash);
            }
            sQLitePreparedStatement.bindByteBuffer(2, nativeByteBuffer2);
            sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer3);
            sQLitePreparedStatement.bindInteger(4, encryptedChat.ttl);
            sQLitePreparedStatement.bindInteger(5, encryptedChat.layer);
            sQLitePreparedStatement.bindInteger(6, encryptedChat.seq_in);
            sQLitePreparedStatement.bindInteger(7, encryptedChat.seq_out);
            sQLitePreparedStatement.bindInteger(8, (encryptedChat.key_use_count_in << 16) | encryptedChat.key_use_count_out);
            sQLitePreparedStatement.bindLong(9, encryptedChat.exchange_id);
            sQLitePreparedStatement.bindInteger(10, encryptedChat.key_create_date);
            sQLitePreparedStatement.bindLong(11, encryptedChat.future_key_fingerprint);
            sQLitePreparedStatement.bindByteBuffer(12, nativeByteBuffer4);
            sQLitePreparedStatement.bindByteBuffer(13, nativeByteBuffer5);
            sQLitePreparedStatement.bindInteger(14, encryptedChat.in_seq_no);
            sQLitePreparedStatement.bindInteger(15, encryptedChat.admin_id);
            sQLitePreparedStatement.bindInteger(16, encryptedChat.mtproto_seq);
            sQLitePreparedStatement.bindInteger(17, encryptedChat.id);
            sQLitePreparedStatement.step();
            nativeByteBuffer.reuse();
            nativeByteBuffer2.reuse();
            nativeByteBuffer3.reuse();
            nativeByteBuffer4.reuse();
            nativeByteBuffer5.reuse();
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Throwable e2) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw e2;
        }
        sQLitePreparedStatement.dispose();
    }

    public boolean isDialogHasMessages(long j) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$h1Bw0HprJIOeWxh9cCzoLdi49Hw(this, j, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$isDialogHasMessages$104$MessagesStorage(long j, boolean[] zArr, CountDownLatch countDownLatch) {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d LIMIT 1", new Object[]{Long.valueOf(j)}), new Object[0]);
            zArr[0] = queryFinalized.next();
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        } catch (Throwable th) {
            countDownLatch.countDown();
        }
        countDownLatch.countDown();
    }

    public boolean hasAuthMessage(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$SMHL34yzMiR4bQx0GChvua7uU5A(this, i, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$hasAuthMessage$105$MessagesStorage(int i, boolean[] zArr, CountDownLatch countDownLatch) {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1", new Object[]{Integer.valueOf(i)}), new Object[0]);
            zArr[0] = queryFinalized.next();
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        } catch (Throwable th) {
            countDownLatch.countDown();
        }
        countDownLatch.countDown();
    }

    public void getEncryptedChat(int i, CountDownLatch countDownLatch, ArrayList<TLObject> arrayList) {
        if (countDownLatch != null && arrayList != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$BjE3V3-8ar7ElqXgCcUdKDaYDB0(this, i, arrayList, countDownLatch));
        }
    }

    public /* synthetic */ void lambda$getEncryptedChat$106$MessagesStorage(int i, ArrayList arrayList, CountDownLatch countDownLatch) {
        try {
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i);
            getEncryptedChatsInternal(stringBuilder.toString(), arrayList3, arrayList2);
            if (!(arrayList3.isEmpty() || arrayList2.isEmpty())) {
                ArrayList arrayList4 = new ArrayList();
                getUsersInternal(TextUtils.join(",", arrayList2), arrayList4);
                if (!arrayList4.isEmpty()) {
                    arrayList.add(arrayList3.get(0));
                    arrayList.add(arrayList4.get(0));
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        } catch (Throwable th) {
            countDownLatch.countDown();
        }
        countDownLatch.countDown();
    }

    public void putEncryptedChat(EncryptedChat encryptedChat, User user, Dialog dialog) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$1NL8audQAfKp14xOqQVaaTVhyOI(this, encryptedChat, user, dialog));
        }
    }

    public /* synthetic */ void lambda$putEncryptedChat$107$MessagesStorage(EncryptedChat encryptedChat, User user, Dialog dialog) {
        EncryptedChat encryptedChat2 = encryptedChat;
        User user2 = user;
        Dialog dialog2 = dialog;
        try {
            if ((encryptedChat2.key_hash == null || encryptedChat2.key_hash.length < 16) && encryptedChat2.auth_key != null) {
                encryptedChat2.key_hash = AndroidUtilities.calcAuthKeyHash(encryptedChat2.auth_key);
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_chats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(encryptedChat.getObjectSize());
            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(encryptedChat2.a_or_b != null ? encryptedChat2.a_or_b.length : 1);
            NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(encryptedChat2.auth_key != null ? encryptedChat2.auth_key.length : 1);
            NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(encryptedChat2.future_auth_key != null ? encryptedChat2.future_auth_key.length : 1);
            NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(encryptedChat2.key_hash != null ? encryptedChat2.key_hash.length : 1);
            encryptedChat2.serializeToStream(nativeByteBuffer);
            executeFast.bindInteger(1, encryptedChat2.id);
            executeFast.bindInteger(2, user2.id);
            executeFast.bindString(3, formatUserSearchName(user2));
            executeFast.bindByteBuffer(4, nativeByteBuffer);
            if (encryptedChat2.a_or_b != null) {
                nativeByteBuffer2.writeBytes(encryptedChat2.a_or_b);
            }
            if (encryptedChat2.auth_key != null) {
                nativeByteBuffer3.writeBytes(encryptedChat2.auth_key);
            }
            if (encryptedChat2.future_auth_key != null) {
                nativeByteBuffer4.writeBytes(encryptedChat2.future_auth_key);
            }
            if (encryptedChat2.key_hash != null) {
                nativeByteBuffer5.writeBytes(encryptedChat2.key_hash);
            }
            executeFast.bindByteBuffer(5, nativeByteBuffer2);
            executeFast.bindByteBuffer(6, nativeByteBuffer3);
            executeFast.bindInteger(7, encryptedChat2.ttl);
            executeFast.bindInteger(8, encryptedChat2.layer);
            executeFast.bindInteger(9, encryptedChat2.seq_in);
            executeFast.bindInteger(10, encryptedChat2.seq_out);
            executeFast.bindInteger(11, encryptedChat2.key_use_count_out | (encryptedChat2.key_use_count_in << 16));
            executeFast.bindLong(12, encryptedChat2.exchange_id);
            executeFast.bindInteger(13, encryptedChat2.key_create_date);
            executeFast.bindLong(14, encryptedChat2.future_key_fingerprint);
            executeFast.bindByteBuffer(15, nativeByteBuffer4);
            executeFast.bindByteBuffer(16, nativeByteBuffer5);
            executeFast.bindInteger(17, encryptedChat2.in_seq_no);
            executeFast.bindInteger(18, encryptedChat2.admin_id);
            executeFast.bindInteger(19, encryptedChat2.mtproto_seq);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
            nativeByteBuffer2.reuse();
            nativeByteBuffer3.reuse();
            nativeByteBuffer4.reuse();
            nativeByteBuffer5.reuse();
            if (dialog2 != null) {
                SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                executeFast2.bindLong(1, dialog2.id);
                executeFast2.bindInteger(2, dialog2.last_message_date);
                executeFast2.bindInteger(3, dialog2.unread_count);
                executeFast2.bindInteger(4, dialog2.top_message);
                executeFast2.bindInteger(5, dialog2.read_inbox_max_id);
                executeFast2.bindInteger(6, dialog2.read_outbox_max_id);
                executeFast2.bindInteger(7, 0);
                executeFast2.bindInteger(8, dialog2.unread_mentions_count);
                executeFast2.bindInteger(9, dialog2.pts);
                executeFast2.bindInteger(10, 0);
                executeFast2.bindInteger(11, dialog2.pinnedNum);
                executeFast2.bindInteger(12, dialog2.flags);
                executeFast2.bindInteger(13, dialog2.folder_id);
                executeFast2.bindNull(14);
                executeFast2.step();
                executeFast2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private String formatUserSearchName(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        String str = user.first_name;
        if (str != null && str.length() > 0) {
            stringBuilder.append(user.first_name);
        }
        str = user.last_name;
        if (str != null && str.length() > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(user.last_name);
        }
        stringBuilder.append(";;;");
        str = user.username;
        if (str != null && str.length() > 0) {
            stringBuilder.append(user.username);
        }
        return stringBuilder.toString().toLowerCase();
    }

    private void putUsersInternal(ArrayList<User> arrayList) throws Exception {
        if (arrayList != null && !arrayList.isEmpty()) {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
            for (int i = 0; i < arrayList.size(); i++) {
                TLObject tLObject = (User) arrayList.get(i);
                if (tLObject.min) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", new Object[]{Integer.valueOf(tLObject.id)}), new Object[0]);
                    if (queryFinalized.next()) {
                        try {
                            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                TLObject TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                if (TLdeserialize != null) {
                                    if (tLObject.username != null) {
                                        TLdeserialize.username = tLObject.username;
                                        TLdeserialize.flags |= 8;
                                    } else {
                                        TLdeserialize.username = null;
                                        TLdeserialize.flags &= -9;
                                    }
                                    if (tLObject.photo != null) {
                                        TLdeserialize.photo = tLObject.photo;
                                        TLdeserialize.flags |= 32;
                                    } else {
                                        TLdeserialize.photo = null;
                                        TLdeserialize.flags &= -33;
                                    }
                                    tLObject = TLdeserialize;
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    queryFinalized.dispose();
                }
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                tLObject.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, tLObject.id);
                executeFast.bindString(2, formatUserSearchName(tLObject));
                UserStatus userStatus = tLObject.status;
                if (userStatus != null) {
                    if (userStatus instanceof TL_userStatusRecently) {
                        userStatus.expires = -100;
                    } else if (userStatus instanceof TL_userStatusLastWeek) {
                        userStatus.expires = -101;
                    } else if (userStatus instanceof TL_userStatusLastMonth) {
                        userStatus.expires = -102;
                    }
                    executeFast.bindInteger(3, tLObject.status.expires);
                } else {
                    executeFast.bindInteger(3, 0);
                }
                executeFast.bindByteBuffer(4, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
        }
    }

    public void updateChatDefaultBannedRights(int i, TL_chatBannedRights tL_chatBannedRights, int i2) {
        if (tL_chatBannedRights != null && i != 0) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$L6td2oD5vdHBd8qQuG7QApRukkQ(this, i, i2, tL_chatBannedRights));
        }
    }

    public /* synthetic */ void lambda$updateChatDefaultBannedRights$108$MessagesStorage(int i, int i2, TL_chatBannedRights tL_chatBannedRights) {
        Chat chat = null;
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    chat = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            if (chat != null) {
                if (chat.default_banned_rights == null || i2 >= chat.version) {
                    chat.default_banned_rights = tL_chatBannedRights;
                    chat.flags |= 262144;
                    chat.version = i2;
                    SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE chats SET data = ? WHERE uid = ?");
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chat.getObjectSize());
                    chat.serializeToStream(nativeByteBuffer);
                    executeFast.bindByteBuffer(1, nativeByteBuffer);
                    executeFast.bindInteger(2, chat.id);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    executeFast.dispose();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void putChatsInternal(ArrayList<Chat> arrayList) throws Exception {
        if (arrayList != null && !arrayList.isEmpty()) {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
            for (int i = 0; i < arrayList.size(); i++) {
                TLObject tLObject = (Chat) arrayList.get(i);
                if (tLObject.min) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", new Object[]{Integer.valueOf(tLObject.id)}), new Object[0]);
                    if (queryFinalized.next()) {
                        try {
                            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                TLObject TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                if (TLdeserialize != null) {
                                    TLdeserialize.title = tLObject.title;
                                    TLdeserialize.photo = tLObject.photo;
                                    TLdeserialize.broadcast = tLObject.broadcast;
                                    TLdeserialize.verified = tLObject.verified;
                                    TLdeserialize.megagroup = tLObject.megagroup;
                                    if (tLObject.default_banned_rights != null) {
                                        TLdeserialize.default_banned_rights = tLObject.default_banned_rights;
                                        TLdeserialize.flags |= 262144;
                                    }
                                    if (tLObject.admin_rights != null) {
                                        TLdeserialize.admin_rights = tLObject.admin_rights;
                                        TLdeserialize.flags |= 16384;
                                    }
                                    if (tLObject.banned_rights != null) {
                                        TLdeserialize.banned_rights = tLObject.banned_rights;
                                        TLdeserialize.flags |= 32768;
                                    }
                                    if (tLObject.username != null) {
                                        TLdeserialize.username = tLObject.username;
                                        TLdeserialize.flags |= 64;
                                    } else {
                                        TLdeserialize.username = null;
                                        TLdeserialize.flags &= -65;
                                    }
                                    tLObject = TLdeserialize;
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    queryFinalized.dispose();
                }
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                tLObject.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, tLObject.id);
                String str = tLObject.title;
                if (str != null) {
                    executeFast.bindString(2, str.toLowerCase());
                } else {
                    executeFast.bindString(2, "");
                }
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
        }
    }

    public void getUsersInternal(String str, ArrayList<User> arrayList) throws Exception {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        User TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            if (TLdeserialize.status != null) {
                                TLdeserialize.status.expires = queryFinalized.intValue(1);
                            }
                            arrayList.add(TLdeserialize);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            queryFinalized.dispose();
        }
    }

    public void getChatsInternal(String str, ArrayList<Chat> arrayList) throws Exception {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Chat TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            arrayList.add(TLdeserialize);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            queryFinalized.dispose();
        }
    }

    public void getEncryptedChatsInternal(String str, ArrayList<EncryptedChat> arrayList, ArrayList<Integer> arrayList2) throws Exception {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no, admin_id, mtproto_seq FROM enc_chats WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        EncryptedChat TLdeserialize = EncryptedChat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            TLdeserialize.user_id = queryFinalized.intValue(1);
                            if (!(arrayList2 == null || arrayList2.contains(Integer.valueOf(TLdeserialize.user_id)))) {
                                arrayList2.add(Integer.valueOf(TLdeserialize.user_id));
                            }
                            TLdeserialize.a_or_b = queryFinalized.byteArrayValue(2);
                            TLdeserialize.auth_key = queryFinalized.byteArrayValue(3);
                            TLdeserialize.ttl = queryFinalized.intValue(4);
                            TLdeserialize.layer = queryFinalized.intValue(5);
                            TLdeserialize.seq_in = queryFinalized.intValue(6);
                            TLdeserialize.seq_out = queryFinalized.intValue(7);
                            int intValue = queryFinalized.intValue(8);
                            TLdeserialize.key_use_count_in = (short) (intValue >> 16);
                            TLdeserialize.key_use_count_out = (short) intValue;
                            TLdeserialize.exchange_id = queryFinalized.longValue(9);
                            TLdeserialize.key_create_date = queryFinalized.intValue(10);
                            TLdeserialize.future_key_fingerprint = queryFinalized.longValue(11);
                            TLdeserialize.future_auth_key = queryFinalized.byteArrayValue(12);
                            TLdeserialize.key_hash = queryFinalized.byteArrayValue(13);
                            TLdeserialize.in_seq_no = queryFinalized.intValue(14);
                            intValue = queryFinalized.intValue(15);
                            if (intValue != 0) {
                                TLdeserialize.admin_id = intValue;
                            }
                            TLdeserialize.mtproto_seq = queryFinalized.intValue(16);
                            arrayList.add(TLdeserialize);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            queryFinalized.dispose();
        }
    }

    private void putUsersAndChatsInternal(ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z) {
        if (z) {
            try {
                this.database.beginTransaction();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        putUsersInternal(arrayList);
        putChatsInternal(arrayList2);
        if (z) {
            this.database.commitTransaction();
        }
    }

    public void putUsersAndChats(ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z, boolean z2) {
        if (arrayList == null || !arrayList.isEmpty() || arrayList2 == null || !arrayList2.isEmpty()) {
            if (z2) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$kd_Go8VSaz51-uU3hGCeTfBlBOc(this, arrayList, arrayList2, z));
            } else {
                putUsersAndChatsInternal(arrayList, arrayList2, z);
            }
        }
    }

    public /* synthetic */ void lambda$putUsersAndChats$109$MessagesStorage(ArrayList arrayList, ArrayList arrayList2, boolean z) {
        putUsersAndChatsInternal(arrayList, arrayList2, z);
    }

    public void removeFromDownloadQueue(long j, int i, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$988hzz_MAzmrda-BTvkl20ZcV48(this, z, i, j));
    }

    public /* synthetic */ void lambda$removeFromDownloadQueue$110$MessagesStorage(boolean z, int i, long j) {
        if (z) {
            try {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT min(date) FROM download_queue WHERE type = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
                int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
                queryFinalized.dispose();
                if (intValue != -1) {
                    this.database.executeFast(String.format(Locale.US, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", new Object[]{Integer.valueOf(intValue - 1), Long.valueOf(j), Integer.valueOf(i)})).stepThis().dispose();
                    return;
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i)})).stepThis().dispose();
    }

    public void clearDownloadQueue(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$uN9mNUMelivu2G0y23cwatUUHQaY(this, i));
    }

    public /* synthetic */ void lambda$clearDownloadQueue$111$MessagesStorage(int i) {
        if (i == 0) {
            try {
                this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", new Object[]{Integer.valueOf(i)})).stepThis().dispose();
    }

    public void getDownloadQueue(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$H-j7oWVB9AqievohQ7jsDyVmkxE(this, i));
    }

    public /* synthetic */ void lambda$getDownloadQueue$113$MessagesStorage(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data, parent FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", new Object[]{Integer.valueOf(i)}), new Object[0]);
            while (queryFinalized.next()) {
                DownloadObject downloadObject = new DownloadObject();
                downloadObject.type = queryFinalized.intValue(1);
                downloadObject.id = queryFinalized.longValue(0);
                downloadObject.parent = queryFinalized.stringValue(3);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(2);
                if (byteBufferValue != null) {
                    MessageMedia TLdeserialize = MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize.document != null) {
                        downloadObject.object = TLdeserialize.document;
                    } else if (TLdeserialize.photo != null) {
                        downloadObject.object = TLdeserialize.photo;
                    }
                    downloadObject.secret = TLdeserialize.ttl_seconds != 0;
                    downloadObject.forceCache = (TLdeserialize.flags & Integer.MIN_VALUE) != 0;
                }
                arrayList.add(downloadObject);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$XDBHUfmsnenbp0l1djF4P8_UzLM(this, i, arrayList));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$112$MessagesStorage(int i, ArrayList arrayList) {
        DownloadController.getInstance(this.currentAccount).processDownloadObjects(i, arrayList);
    }

    /* JADX WARNING: Missing block: B:9:0x0018, code skipped:
            if (r0 <= 60) goto L_0x003a;
     */
    private int getMessageMediaType(org.telegram.tgnet.TLRPC.Message r5) {
        /*
        r4 = this;
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_message_secret;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x003b;
    L_0x0006:
        r0 = r5.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x0012;
    L_0x000c:
        r0 = org.telegram.messenger.MessageObject.isGifMessage(r5);
        if (r0 == 0) goto L_0x001a;
    L_0x0012:
        r0 = r5.ttl;
        if (r0 <= 0) goto L_0x001a;
    L_0x0016:
        r3 = 60;
        if (r0 <= r3) goto L_0x003a;
    L_0x001a:
        r0 = org.telegram.messenger.MessageObject.isVoiceMessage(r5);
        if (r0 != 0) goto L_0x003a;
    L_0x0020:
        r0 = org.telegram.messenger.MessageObject.isVideoMessage(r5);
        if (r0 != 0) goto L_0x003a;
    L_0x0026:
        r0 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r5);
        if (r0 == 0) goto L_0x002d;
    L_0x002c:
        goto L_0x003a;
    L_0x002d:
        r0 = r5.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x0039;
    L_0x0033:
        r5 = org.telegram.messenger.MessageObject.isVideoMessage(r5);
        if (r5 == 0) goto L_0x005d;
    L_0x0039:
        return r2;
    L_0x003a:
        return r1;
    L_0x003b:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_message;
        if (r0 == 0) goto L_0x0050;
    L_0x003f:
        r0 = r5.media;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r3 != 0) goto L_0x0049;
    L_0x0045:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r0 == 0) goto L_0x0050;
    L_0x0049:
        r0 = r5.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0050;
    L_0x004f:
        return r1;
    L_0x0050:
        r0 = r5.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x005f;
    L_0x0056:
        r5 = org.telegram.messenger.MessageObject.isVideoMessage(r5);
        if (r5 == 0) goto L_0x005d;
    L_0x005c:
        goto L_0x005f;
    L_0x005d:
        r5 = -1;
        return r5;
    L_0x005f:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.getMessageMediaType(org.telegram.tgnet.TLRPC$Message):int");
    }

    public void putWebPages(LongSparseArray<WebPage> longSparseArray) {
        if (!isEmpty((LongSparseArray) longSparseArray)) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$7LavNWbqJ0Tm6RS1mX0i3MgUDks(this, longSparseArray));
        }
    }

    public /* synthetic */ void lambda$putWebPages$115$MessagesStorage(LongSparseArray longSparseArray) {
        try {
            ArrayList arrayList = new ArrayList();
            int i = 0;
            for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                SQLiteDatabase sQLiteDatabase = this.database;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT mid FROM webpage_pending WHERE id = ");
                stringBuilder.append(longSparseArray.keyAt(i2));
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
                ArrayList arrayList2 = new ArrayList();
                while (queryFinalized.next()) {
                    arrayList2.add(Long.valueOf(queryFinalized.longValue(0)));
                }
                queryFinalized.dispose();
                if (!arrayList2.isEmpty()) {
                    queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data FROM messages WHERE mid IN (%s)", new Object[]{TextUtils.join(",", arrayList2)}), new Object[0]);
                    while (queryFinalized.next()) {
                        int intValue = queryFinalized.intValue(0);
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                            byteBufferValue.reuse();
                            if (TLdeserialize.media instanceof TL_messageMediaWebPage) {
                                TLdeserialize.id = intValue;
                                TLdeserialize.media.webpage = (WebPage) longSparseArray.valueAt(i2);
                                arrayList.add(TLdeserialize);
                            }
                        }
                    }
                    queryFinalized.dispose();
                }
            }
            if (!arrayList.isEmpty()) {
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
                SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE media_v2 SET data = ? WHERE mid = ?");
                while (i < arrayList.size()) {
                    Message message = (Message) arrayList.get(i);
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(nativeByteBuffer);
                    long j = (long) message.id;
                    if (message.to_id.channel_id != 0) {
                        j |= ((long) message.to_id.channel_id) << 32;
                    }
                    executeFast.requery();
                    executeFast.bindByteBuffer(1, nativeByteBuffer);
                    executeFast.bindLong(2, j);
                    executeFast.step();
                    executeFast2.requery();
                    executeFast2.bindByteBuffer(1, nativeByteBuffer);
                    executeFast2.bindLong(2, j);
                    executeFast2.step();
                    nativeByteBuffer.reuse();
                    i++;
                }
                executeFast.dispose();
                executeFast2.dispose();
                this.database.commitTransaction();
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$ObGK1Qcbe_0VX1LaMzuez9l5N2U(this, arrayList));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$114$MessagesStorage(ArrayList arrayList) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didReceivedWebpages, arrayList);
    }

    public void overwriteChannel(int i, TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$hiRx0kE3qTDWeC2XH8-QvasEOrg(this, i, i2, tL_updates_channelDifferenceTooLong));
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0117 A:{Catch:{ Exception -> 0x014c }} */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0115 A:{Catch:{ Exception -> 0x014c }} */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0136 A:{Catch:{ Exception -> 0x014c }} */
    public /* synthetic */ void lambda$overwriteChannel$117$MessagesStorage(int r11, int r12, org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong r13) {
        /*
        r10 = this;
        r0 = -r11;
        r0 = (long) r0;
        r2 = r10.database;	 Catch:{ Exception -> 0x014c }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014c }
        r3.<init>();	 Catch:{ Exception -> 0x014c }
        r4 = "SELECT pinned FROM dialogs WHERE did = ";
        r3.append(r4);	 Catch:{ Exception -> 0x014c }
        r3.append(r0);	 Catch:{ Exception -> 0x014c }
        r3 = r3.toString();	 Catch:{ Exception -> 0x014c }
        r4 = 0;
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x014c }
        r2 = r2.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x014c }
        r3 = r2.next();	 Catch:{ Exception -> 0x014c }
        r5 = 1;
        if (r3 != 0) goto L_0x002b;
    L_0x0023:
        if (r12 == 0) goto L_0x0028;
    L_0x0025:
        r3 = 0;
        r6 = 1;
        goto L_0x0030;
    L_0x0028:
        r3 = 0;
    L_0x0029:
        r6 = 0;
        goto L_0x0030;
    L_0x002b:
        r3 = r2.intValue(r4);	 Catch:{ Exception -> 0x014c }
        goto L_0x0029;
    L_0x0030:
        r2.dispose();	 Catch:{ Exception -> 0x014c }
        r2 = r10.database;	 Catch:{ Exception -> 0x014c }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014c }
        r7.<init>();	 Catch:{ Exception -> 0x014c }
        r8 = "DELETE FROM messages WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x014c }
        r7.append(r0);	 Catch:{ Exception -> 0x014c }
        r7 = r7.toString();	 Catch:{ Exception -> 0x014c }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x014c }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x014c }
        r2.dispose();	 Catch:{ Exception -> 0x014c }
        r2 = r10.database;	 Catch:{ Exception -> 0x014c }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014c }
        r7.<init>();	 Catch:{ Exception -> 0x014c }
        r8 = "DELETE FROM bot_keyboard WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x014c }
        r7.append(r0);	 Catch:{ Exception -> 0x014c }
        r7 = r7.toString();	 Catch:{ Exception -> 0x014c }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x014c }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x014c }
        r2.dispose();	 Catch:{ Exception -> 0x014c }
        r2 = r10.database;	 Catch:{ Exception -> 0x014c }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014c }
        r7.<init>();	 Catch:{ Exception -> 0x014c }
        r8 = "UPDATE media_counts_v2 SET old = 1 WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x014c }
        r7.append(r0);	 Catch:{ Exception -> 0x014c }
        r7 = r7.toString();	 Catch:{ Exception -> 0x014c }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x014c }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x014c }
        r2.dispose();	 Catch:{ Exception -> 0x014c }
        r2 = r10.database;	 Catch:{ Exception -> 0x014c }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014c }
        r7.<init>();	 Catch:{ Exception -> 0x014c }
        r8 = "DELETE FROM media_v2 WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x014c }
        r7.append(r0);	 Catch:{ Exception -> 0x014c }
        r7 = r7.toString();	 Catch:{ Exception -> 0x014c }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x014c }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x014c }
        r2.dispose();	 Catch:{ Exception -> 0x014c }
        r2 = r10.database;	 Catch:{ Exception -> 0x014c }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014c }
        r7.<init>();	 Catch:{ Exception -> 0x014c }
        r8 = "DELETE FROM messages_holes WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x014c }
        r7.append(r0);	 Catch:{ Exception -> 0x014c }
        r7 = r7.toString();	 Catch:{ Exception -> 0x014c }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x014c }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x014c }
        r2.dispose();	 Catch:{ Exception -> 0x014c }
        r2 = r10.database;	 Catch:{ Exception -> 0x014c }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014c }
        r7.<init>();	 Catch:{ Exception -> 0x014c }
        r8 = "DELETE FROM media_holes_v2 WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x014c }
        r7.append(r0);	 Catch:{ Exception -> 0x014c }
        r7 = r7.toString();	 Catch:{ Exception -> 0x014c }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x014c }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x014c }
        r2.dispose();	 Catch:{ Exception -> 0x014c }
        r2 = r10.currentAccount;	 Catch:{ Exception -> 0x014c }
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);	 Catch:{ Exception -> 0x014c }
        r7 = 0;
        r2.clearBotKeyboard(r0, r7);	 Catch:{ Exception -> 0x014c }
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_dialogs;	 Catch:{ Exception -> 0x014c }
        r2.<init>();	 Catch:{ Exception -> 0x014c }
        r8 = r2.chats;	 Catch:{ Exception -> 0x014c }
        r9 = r13.chats;	 Catch:{ Exception -> 0x014c }
        r8.addAll(r9);	 Catch:{ Exception -> 0x014c }
        r8 = r2.users;	 Catch:{ Exception -> 0x014c }
        r9 = r13.users;	 Catch:{ Exception -> 0x014c }
        r8.addAll(r9);	 Catch:{ Exception -> 0x014c }
        r8 = r2.messages;	 Catch:{ Exception -> 0x014c }
        r9 = r13.messages;	 Catch:{ Exception -> 0x014c }
        r8.addAll(r9);	 Catch:{ Exception -> 0x014c }
        r13 = r13.dialog;	 Catch:{ Exception -> 0x014c }
        r13.id = r0;	 Catch:{ Exception -> 0x014c }
        r13.flags = r5;	 Catch:{ Exception -> 0x014c }
        r13.notify_settings = r7;	 Catch:{ Exception -> 0x014c }
        if (r3 == 0) goto L_0x0117;
    L_0x0115:
        r8 = 1;
        goto L_0x0118;
    L_0x0117:
        r8 = 0;
    L_0x0118:
        r13.pinned = r8;	 Catch:{ Exception -> 0x014c }
        r13.pinnedNum = r3;	 Catch:{ Exception -> 0x014c }
        r3 = r2.dialogs;	 Catch:{ Exception -> 0x014c }
        r3.add(r13);	 Catch:{ Exception -> 0x014c }
        r10.putDialogsInternal(r2, r4);	 Catch:{ Exception -> 0x014c }
        r13 = new java.util.ArrayList;	 Catch:{ Exception -> 0x014c }
        r13.<init>();	 Catch:{ Exception -> 0x014c }
        r10.updateDialogsWithDeletedMessages(r13, r7, r4, r11);	 Catch:{ Exception -> 0x014c }
        r13 = new org.telegram.messenger.-$$Lambda$MessagesStorage$Wb5AhXJSME14hTBqV6-I8T7JrAg;	 Catch:{ Exception -> 0x014c }
        r13.<init>(r10, r0);	 Catch:{ Exception -> 0x014c }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r13);	 Catch:{ Exception -> 0x014c }
        if (r6 == 0) goto L_0x0150;
    L_0x0136:
        if (r12 != r5) goto L_0x0142;
    L_0x0138:
        r12 = r10.currentAccount;	 Catch:{ Exception -> 0x014c }
        r12 = org.telegram.messenger.MessagesController.getInstance(r12);	 Catch:{ Exception -> 0x014c }
        r12.checkChannelInviter(r11);	 Catch:{ Exception -> 0x014c }
        goto L_0x0150;
    L_0x0142:
        r12 = r10.currentAccount;	 Catch:{ Exception -> 0x014c }
        r12 = org.telegram.messenger.MessagesController.getInstance(r12);	 Catch:{ Exception -> 0x014c }
        r12.generateJoinMessage(r11, r4);	 Catch:{ Exception -> 0x014c }
        goto L_0x0150;
    L_0x014c:
        r11 = move-exception;
        org.telegram.messenger.FileLog.e(r11);
    L_0x0150:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$overwriteChannel$117$MessagesStorage(int, int, org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong):void");
    }

    public /* synthetic */ void lambda$null$116$MessagesStorage(long j) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.valueOf(true));
    }

    public void putChannelViews(SparseArray<SparseIntArray> sparseArray, boolean z) {
        if (!isEmpty((SparseArray) sparseArray)) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$uuLM05KdtpS7ns6Dw29DcaBFO24(this, sparseArray, z));
        }
    }

    public /* synthetic */ void lambda$putChannelViews$118$MessagesStorage(SparseArray sparseArray, boolean z) {
        try {
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages SET media = max((SELECT media FROM messages WHERE mid = ?), ?) WHERE mid = ?");
            for (int i = 0; i < sparseArray.size(); i++) {
                int keyAt = sparseArray.keyAt(i);
                SparseIntArray sparseIntArray = (SparseIntArray) sparseArray.get(keyAt);
                for (int i2 = 0; i2 < sparseIntArray.size(); i2++) {
                    int i3 = sparseIntArray.get(sparseIntArray.keyAt(i2));
                    long keyAt2 = (long) sparseIntArray.keyAt(i2);
                    if (z) {
                        keyAt2 |= ((long) (-keyAt)) << 32;
                    }
                    executeFast.requery();
                    executeFast.bindLong(1, keyAt2);
                    executeFast.bindInteger(2, i3);
                    executeFast.bindLong(3, keyAt2);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean isValidKeyboardToSave(Message message) {
        ReplyMarkup replyMarkup = message.reply_markup;
        return (replyMarkup == null || (replyMarkup instanceof TL_replyInlineMarkup) || (replyMarkup.selective && !message.mentioned)) ? false : true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:67:0x01b9 A:{Catch:{ Exception -> 0x0045 }} */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x017c A:{Catch:{ Exception -> 0x0045 }} */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x01d7 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01c2 A:{Catch:{ Exception -> 0x0045 }} */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x05ae A:{Catch:{ Exception -> 0x0045 }} */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x059c A:{Catch:{ Exception -> 0x0045 }} */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x062e A:{Catch:{ Exception -> 0x0045 }} */
    private void putMessagesInternal(java.util.ArrayList<org.telegram.tgnet.TLRPC.Message> r31, boolean r32, boolean r33, int r34, boolean r35) {
        /*
        r30 = this;
        r1 = r30;
        r2 = r31;
        r4 = 0;
        r6 = 0;
        if (r35 == 0) goto L_0x0049;
    L_0x0009:
        r7 = r2.get(r6);	 Catch:{ Exception -> 0x0045 }
        r7 = (org.telegram.tgnet.TLRPC.Message) r7;	 Catch:{ Exception -> 0x0045 }
        r8 = r7.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r10 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r10 != 0) goto L_0x0018;
    L_0x0015:
        org.telegram.messenger.MessageObject.getDialogId(r7);	 Catch:{ Exception -> 0x0045 }
    L_0x0018:
        r8 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r9.<init>();	 Catch:{ Exception -> 0x0045 }
        r10 = "SELECT last_mid FROM dialogs WHERE did = ";
        r9.append(r10);	 Catch:{ Exception -> 0x0045 }
        r10 = r7.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r9.append(r10);	 Catch:{ Exception -> 0x0045 }
        r7 = r9.toString();	 Catch:{ Exception -> 0x0045 }
        r9 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0045 }
        r7 = r8.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x0045 }
        r8 = r7.next();	 Catch:{ Exception -> 0x0045 }
        if (r8 == 0) goto L_0x003e;
    L_0x0039:
        r8 = r7.intValue(r6);	 Catch:{ Exception -> 0x0045 }
        goto L_0x003f;
    L_0x003e:
        r8 = -1;
    L_0x003f:
        r7.dispose();	 Catch:{ Exception -> 0x0045 }
        if (r8 == 0) goto L_0x0049;
    L_0x0044:
        return;
    L_0x0045:
        r0 = move-exception;
    L_0x0046:
        r2 = r0;
        goto L_0x0954;
    L_0x0049:
        if (r32 == 0) goto L_0x0050;
    L_0x004b:
        r7 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r7.beginTransaction();	 Catch:{ Exception -> 0x0045 }
    L_0x0050:
        r7 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r7.<init>();	 Catch:{ Exception -> 0x0045 }
        r8 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r8.<init>();	 Catch:{ Exception -> 0x0045 }
        r9 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r9.<init>();	 Catch:{ Exception -> 0x0045 }
        r10 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r10.<init>();	 Catch:{ Exception -> 0x0045 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r11.<init>();	 Catch:{ Exception -> 0x0045 }
        r12 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r12.<init>();	 Catch:{ Exception -> 0x0045 }
        r13 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r13.<init>();	 Catch:{ Exception -> 0x0045 }
        r14 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r14.<init>();	 Catch:{ Exception -> 0x0045 }
        r15 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r3 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r3 = r15.executeFast(r3);	 Catch:{ Exception -> 0x0045 }
        r15 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r6 = "REPLACE INTO randoms VALUES(?, ?)";
        r6 = r15.executeFast(r6);	 Catch:{ Exception -> 0x0045 }
        r15 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r4 = "REPLACE INTO download_queue VALUES(?, ?, ?, ?, ?)";
        r4 = r15.executeFast(r4);	 Catch:{ Exception -> 0x0045 }
        r5 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r15 = "REPLACE INTO webpage_pending VALUES(?, ?)";
        r5 = r5.executeFast(r15);	 Catch:{ Exception -> 0x0045 }
        r20 = r4;
        r21 = r5;
        r22 = r6;
        r4 = 0;
        r5 = 0;
        r15 = 0;
        r19 = 0;
    L_0x00a3:
        r6 = r31.size();	 Catch:{ Exception -> 0x0045 }
        r23 = 32;
        if (r15 >= r6) goto L_0x01e6;
    L_0x00ab:
        r6 = r2.get(r15);	 Catch:{ Exception -> 0x0045 }
        r6 = (org.telegram.tgnet.TLRPC.Message) r6;	 Catch:{ Exception -> 0x0045 }
        r24 = r7;
        r7 = r6.id;	 Catch:{ Exception -> 0x0045 }
        r25 = r3;
        r2 = (long) r7;	 Catch:{ Exception -> 0x0045 }
        r26 = r8;
        r7 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r17 = 0;
        r27 = (r7 > r17 ? 1 : (r7 == r17 ? 0 : -1));
        if (r27 != 0) goto L_0x00c5;
    L_0x00c2:
        org.telegram.messenger.MessageObject.getDialogId(r6);	 Catch:{ Exception -> 0x0045 }
    L_0x00c5:
        r7 = r6.to_id;	 Catch:{ Exception -> 0x0045 }
        r7 = r7.channel_id;	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x00d3;
    L_0x00cb:
        r7 = r6.to_id;	 Catch:{ Exception -> 0x0045 }
        r7 = r7.channel_id;	 Catch:{ Exception -> 0x0045 }
        r7 = (long) r7;	 Catch:{ Exception -> 0x0045 }
        r7 = r7 << r23;
        r2 = r2 | r7;
    L_0x00d3:
        r7 = r6.mentioned;	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x00e4;
    L_0x00d7:
        r7 = r6.media_unread;	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x00e4;
    L_0x00db:
        r7 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r7 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x0045 }
        r14.put(r2, r7);	 Catch:{ Exception -> 0x0045 }
    L_0x00e4:
        r7 = r6.action;	 Catch:{ Exception -> 0x0045 }
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;	 Catch:{ Exception -> 0x0045 }
        r8 = ",";
        if (r7 != 0) goto L_0x016e;
    L_0x00ec:
        r7 = org.telegram.messenger.MessageObject.isOut(r6);	 Catch:{ Exception -> 0x0045 }
        if (r7 != 0) goto L_0x016e;
    L_0x00f2:
        r7 = r6.id;	 Catch:{ Exception -> 0x0045 }
        if (r7 > 0) goto L_0x00fc;
    L_0x00f6:
        r7 = org.telegram.messenger.MessageObject.isUnread(r6);	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x016e;
    L_0x00fc:
        r7 = r4;
        r27 = r5;
        r4 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r4 = r12.get(r4);	 Catch:{ Exception -> 0x0045 }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0045 }
        if (r4 != 0) goto L_0x0147;
    L_0x0109:
        r4 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r5.<init>();	 Catch:{ Exception -> 0x0045 }
        r28 = r9;
        r9 = "SELECT inbox_max FROM dialogs WHERE did = ";
        r5.append(r9);	 Catch:{ Exception -> 0x0045 }
        r9 = r14;
        r29 = r15;
        r14 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r5.append(r14);	 Catch:{ Exception -> 0x0045 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0045 }
        r14 = 0;
        r15 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0045 }
        r4 = r4.queryFinalized(r5, r15);	 Catch:{ Exception -> 0x0045 }
        r5 = r4.next();	 Catch:{ Exception -> 0x0045 }
        if (r5 == 0) goto L_0x0139;
    L_0x0130:
        r5 = r4.intValue(r14);	 Catch:{ Exception -> 0x0045 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0045 }
        goto L_0x013d;
    L_0x0139:
        r5 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0045 }
    L_0x013d:
        r4.dispose();	 Catch:{ Exception -> 0x0045 }
        r14 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r12.put(r14, r5);	 Catch:{ Exception -> 0x0045 }
        r4 = r5;
        goto L_0x014c;
    L_0x0147:
        r28 = r9;
        r9 = r14;
        r29 = r15;
    L_0x014c:
        r5 = r6.id;	 Catch:{ Exception -> 0x0045 }
        if (r5 < 0) goto L_0x0158;
    L_0x0150:
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0045 }
        r5 = r6.id;	 Catch:{ Exception -> 0x0045 }
        if (r4 >= r5) goto L_0x0176;
    L_0x0158:
        r4 = r11.length();	 Catch:{ Exception -> 0x0045 }
        if (r4 <= 0) goto L_0x0161;
    L_0x015e:
        r11.append(r8);	 Catch:{ Exception -> 0x0045 }
    L_0x0161:
        r11.append(r2);	 Catch:{ Exception -> 0x0045 }
        r4 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0045 }
        r13.put(r2, r4);	 Catch:{ Exception -> 0x0045 }
        goto L_0x0176;
    L_0x016e:
        r7 = r4;
        r27 = r5;
        r28 = r9;
        r9 = r14;
        r29 = r15;
    L_0x0176:
        r4 = org.telegram.messenger.DataQuery.canAddMessageToMedia(r6);	 Catch:{ Exception -> 0x0045 }
        if (r4 == 0) goto L_0x01b9;
    L_0x017c:
        if (r19 != 0) goto L_0x0191;
    L_0x017e:
        r19 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r19.<init>();	 Catch:{ Exception -> 0x0045 }
        r4 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r4.<init>();	 Catch:{ Exception -> 0x0045 }
        r5 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r5.<init>();	 Catch:{ Exception -> 0x0045 }
        r7 = r4;
        r4 = r19;
        goto L_0x0195;
    L_0x0191:
        r4 = r19;
        r5 = r27;
    L_0x0195:
        r14 = r4.length();	 Catch:{ Exception -> 0x0045 }
        if (r14 <= 0) goto L_0x019e;
    L_0x019b:
        r4.append(r8);	 Catch:{ Exception -> 0x0045 }
    L_0x019e:
        r4.append(r2);	 Catch:{ Exception -> 0x0045 }
        r14 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r8 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0045 }
        r7.put(r2, r8);	 Catch:{ Exception -> 0x0045 }
        r8 = org.telegram.messenger.DataQuery.getMediaType(r6);	 Catch:{ Exception -> 0x0045 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
        r5.put(r2, r8);	 Catch:{ Exception -> 0x0045 }
        r19 = r4;
        r4 = r7;
        goto L_0x01bc;
    L_0x01b9:
        r4 = r7;
        r5 = r27;
    L_0x01bc:
        r2 = r1.isValidKeyboardToSave(r6);	 Catch:{ Exception -> 0x0045 }
        if (r2 == 0) goto L_0x01d7;
    L_0x01c2:
        r2 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r2 = r10.get(r2);	 Catch:{ Exception -> 0x0045 }
        r2 = (org.telegram.tgnet.TLRPC.Message) r2;	 Catch:{ Exception -> 0x0045 }
        if (r2 == 0) goto L_0x01d2;
    L_0x01cc:
        r2 = r2.id;	 Catch:{ Exception -> 0x0045 }
        r3 = r6.id;	 Catch:{ Exception -> 0x0045 }
        if (r2 >= r3) goto L_0x01d7;
    L_0x01d2:
        r2 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r10.put(r2, r6);	 Catch:{ Exception -> 0x0045 }
    L_0x01d7:
        r15 = r29 + 1;
        r2 = r31;
        r14 = r9;
        r7 = r24;
        r3 = r25;
        r8 = r26;
        r9 = r28;
        goto L_0x00a3;
    L_0x01e6:
        r25 = r3;
        r27 = r5;
        r24 = r7;
        r26 = r8;
        r28 = r9;
        r9 = r14;
        r7 = r4;
        r2 = 0;
    L_0x01f3:
        r3 = r10.size();	 Catch:{ Exception -> 0x0045 }
        if (r2 >= r3) goto L_0x020f;
    L_0x01f9:
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x0045 }
        r3 = org.telegram.messenger.DataQuery.getInstance(r3);	 Catch:{ Exception -> 0x0045 }
        r4 = r10.keyAt(r2);	 Catch:{ Exception -> 0x0045 }
        r6 = r10.valueAt(r2);	 Catch:{ Exception -> 0x0045 }
        r6 = (org.telegram.tgnet.TLRPC.Message) r6;	 Catch:{ Exception -> 0x0045 }
        r3.putBotKeyboard(r4, r6);	 Catch:{ Exception -> 0x0045 }
        r2 = r2 + 1;
        goto L_0x01f3;
    L_0x020f:
        r2 = ")";
        r6 = 1;
        if (r19 == 0) goto L_0x031d;
    L_0x0214:
        r3 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r4.<init>();	 Catch:{ Exception -> 0x0045 }
        r5 = "SELECT mid, type FROM media_v2 WHERE mid IN(";
        r4.append(r5);	 Catch:{ Exception -> 0x0045 }
        r5 = r19.toString();	 Catch:{ Exception -> 0x0045 }
        r4.append(r5);	 Catch:{ Exception -> 0x0045 }
        r4.append(r2);	 Catch:{ Exception -> 0x0045 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0045 }
        r5 = 0;
        r8 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0045 }
        r3 = r3.queryFinalized(r4, r8);	 Catch:{ Exception -> 0x0045 }
        r4 = 0;
    L_0x0236:
        r8 = r3.next();	 Catch:{ Exception -> 0x0045 }
        if (r8 == 0) goto L_0x0268;
    L_0x023c:
        r14 = r3.longValue(r5);	 Catch:{ Exception -> 0x0045 }
        r5 = r3.intValue(r6);	 Catch:{ Exception -> 0x0045 }
        r8 = r27;
        r10 = r8.get(r14);	 Catch:{ Exception -> 0x0045 }
        r10 = (java.lang.Integer) r10;	 Catch:{ Exception -> 0x0045 }
        r10 = r10.intValue();	 Catch:{ Exception -> 0x0045 }
        if (r5 != r10) goto L_0x0256;
    L_0x0252:
        r7.remove(r14);	 Catch:{ Exception -> 0x0045 }
        goto L_0x0264;
    L_0x0256:
        if (r4 != 0) goto L_0x025d;
    L_0x0258:
        r4 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r4.<init>();	 Catch:{ Exception -> 0x0045 }
    L_0x025d:
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0045 }
        r4.put(r14, r5);	 Catch:{ Exception -> 0x0045 }
    L_0x0264:
        r27 = r8;
        r5 = 0;
        goto L_0x0236;
    L_0x0268:
        r8 = r27;
        r3.dispose();	 Catch:{ Exception -> 0x0045 }
        r15 = new android.util.SparseArray;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r3 = 0;
    L_0x0273:
        r5 = r7.size();	 Catch:{ Exception -> 0x0045 }
        if (r3 >= r5) goto L_0x031b;
    L_0x0279:
        r5 = r9;
        r9 = r7.keyAt(r3);	 Catch:{ Exception -> 0x0045 }
        r12 = r7.valueAt(r3);	 Catch:{ Exception -> 0x0045 }
        r12 = (java.lang.Long) r12;	 Catch:{ Exception -> 0x0045 }
        r14 = r7;
        r6 = r12.longValue();	 Catch:{ Exception -> 0x0045 }
        r12 = r8.get(r9);	 Catch:{ Exception -> 0x0045 }
        r12 = (java.lang.Integer) r12;	 Catch:{ Exception -> 0x0045 }
        r27 = r8;
        r8 = r12.intValue();	 Catch:{ Exception -> 0x0045 }
        r8 = r15.get(r8);	 Catch:{ Exception -> 0x0045 }
        r8 = (android.util.LongSparseArray) r8;	 Catch:{ Exception -> 0x0045 }
        if (r8 != 0) goto L_0x02b0;
    L_0x029d:
        r8 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r8.<init>();	 Catch:{ Exception -> 0x0045 }
        r16 = 0;
        r29 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x0045 }
        r12 = r12.intValue();	 Catch:{ Exception -> 0x0045 }
        r15.put(r12, r8);	 Catch:{ Exception -> 0x0045 }
        goto L_0x02b8;
    L_0x02b0:
        r12 = r8.get(r6);	 Catch:{ Exception -> 0x0045 }
        r29 = r12;
        r29 = (java.lang.Integer) r29;	 Catch:{ Exception -> 0x0045 }
    L_0x02b8:
        if (r29 != 0) goto L_0x02bf;
    L_0x02ba:
        r12 = 0;
        r29 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x0045 }
    L_0x02bf:
        r12 = r29.intValue();	 Catch:{ Exception -> 0x0045 }
        r19 = 1;
        r12 = r12 + 1;
        r12 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x0045 }
        r8.put(r6, r12);	 Catch:{ Exception -> 0x0045 }
        if (r4 == 0) goto L_0x0312;
    L_0x02d0:
        r8 = -1;
        r12 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
        r8 = r4.get(r9, r12);	 Catch:{ Exception -> 0x0045 }
        r8 = (java.lang.Integer) r8;	 Catch:{ Exception -> 0x0045 }
        r8 = r8.intValue();	 Catch:{ Exception -> 0x0045 }
        if (r8 < 0) goto L_0x0312;
    L_0x02e1:
        r9 = r15.get(r8);	 Catch:{ Exception -> 0x0045 }
        r9 = (android.util.LongSparseArray) r9;	 Catch:{ Exception -> 0x0045 }
        if (r9 != 0) goto L_0x02f7;
    L_0x02e9:
        r9 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r9.<init>();	 Catch:{ Exception -> 0x0045 }
        r10 = 0;
        r12 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0045 }
        r15.put(r8, r9);	 Catch:{ Exception -> 0x0045 }
        goto L_0x02fe;
    L_0x02f7:
        r8 = r9.get(r6);	 Catch:{ Exception -> 0x0045 }
        r12 = r8;
        r12 = (java.lang.Integer) r12;	 Catch:{ Exception -> 0x0045 }
    L_0x02fe:
        if (r12 != 0) goto L_0x0305;
    L_0x0300:
        r8 = 0;
        r12 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
    L_0x0305:
        r8 = r12.intValue();	 Catch:{ Exception -> 0x0045 }
        r10 = 1;
        r8 = r8 - r10;
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
        r9.put(r6, r8);	 Catch:{ Exception -> 0x0045 }
    L_0x0312:
        r3 = r3 + 1;
        r9 = r5;
        r7 = r14;
        r8 = r27;
        r6 = 1;
        goto L_0x0273;
    L_0x031b:
        r5 = r9;
        goto L_0x031f;
    L_0x031d:
        r5 = r9;
        r15 = 0;
    L_0x031f:
        r3 = r11.length();	 Catch:{ Exception -> 0x0045 }
        if (r3 <= 0) goto L_0x03c1;
    L_0x0325:
        r3 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r4.<init>();	 Catch:{ Exception -> 0x0045 }
        r6 = "SELECT mid FROM messages WHERE mid IN(";
        r4.append(r6);	 Catch:{ Exception -> 0x0045 }
        r6 = r11.toString();	 Catch:{ Exception -> 0x0045 }
        r4.append(r6);	 Catch:{ Exception -> 0x0045 }
        r4.append(r2);	 Catch:{ Exception -> 0x0045 }
        r2 = r4.toString();	 Catch:{ Exception -> 0x0045 }
        r4 = 0;
        r6 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0045 }
        r2 = r3.queryFinalized(r2, r6);	 Catch:{ Exception -> 0x0045 }
    L_0x0346:
        r3 = r2.next();	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x0358;
    L_0x034c:
        r6 = r2.longValue(r4);	 Catch:{ Exception -> 0x0045 }
        r13.remove(r6);	 Catch:{ Exception -> 0x0045 }
        r5.remove(r6);	 Catch:{ Exception -> 0x0045 }
        r4 = 0;
        goto L_0x0346;
    L_0x0358:
        r2.dispose();	 Catch:{ Exception -> 0x0045 }
        r2 = 0;
    L_0x035c:
        r3 = r13.size();	 Catch:{ Exception -> 0x0045 }
        if (r2 >= r3) goto L_0x038d;
    L_0x0362:
        r3 = r13.valueAt(r2);	 Catch:{ Exception -> 0x0045 }
        r3 = (java.lang.Long) r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r3.longValue();	 Catch:{ Exception -> 0x0045 }
        r6 = r26;
        r7 = r6.get(r3);	 Catch:{ Exception -> 0x0045 }
        r7 = (java.lang.Integer) r7;	 Catch:{ Exception -> 0x0045 }
        if (r7 != 0) goto L_0x037b;
    L_0x0376:
        r8 = 0;
        r7 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
    L_0x037b:
        r7 = r7.intValue();	 Catch:{ Exception -> 0x0045 }
        r8 = 1;
        r7 = r7 + r8;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0045 }
        r6.put(r3, r7);	 Catch:{ Exception -> 0x0045 }
        r2 = r2 + 1;
        r26 = r6;
        goto L_0x035c;
    L_0x038d:
        r6 = r26;
        r2 = 0;
    L_0x0390:
        r3 = r5.size();	 Catch:{ Exception -> 0x0045 }
        if (r2 >= r3) goto L_0x03c3;
    L_0x0396:
        r3 = r5.valueAt(r2);	 Catch:{ Exception -> 0x0045 }
        r3 = (java.lang.Long) r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r3.longValue();	 Catch:{ Exception -> 0x0045 }
        r7 = r28;
        r8 = r7.get(r3);	 Catch:{ Exception -> 0x0045 }
        r8 = (java.lang.Integer) r8;	 Catch:{ Exception -> 0x0045 }
        if (r8 != 0) goto L_0x03af;
    L_0x03aa:
        r9 = 0;
        r8 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0045 }
    L_0x03af:
        r8 = r8.intValue();	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r8 = r8 + r9;
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
        r7.put(r3, r8);	 Catch:{ Exception -> 0x0045 }
        r2 = r2 + 1;
        r28 = r7;
        goto L_0x0390;
    L_0x03c1:
        r6 = r26;
    L_0x03c3:
        r7 = r28;
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
    L_0x03c9:
        r8 = r31.size();	 Catch:{ Exception -> 0x0045 }
        if (r2 >= r8) goto L_0x06ac;
    L_0x03cf:
        r8 = r31;
        r27 = r8.get(r2);	 Catch:{ Exception -> 0x0045 }
        r11 = r27;
        r11 = (org.telegram.tgnet.TLRPC.Message) r11;	 Catch:{ Exception -> 0x0045 }
        r1.fixUnsupportedMedia(r11);	 Catch:{ Exception -> 0x0045 }
        r25.requery();	 Catch:{ Exception -> 0x0045 }
        r9 = r11.id;	 Catch:{ Exception -> 0x0045 }
        r29 = r15;
        r14 = (long) r9;	 Catch:{ Exception -> 0x0045 }
        r9 = r11.local_id;	 Catch:{ Exception -> 0x0045 }
        if (r9 == 0) goto L_0x03eb;
    L_0x03e8:
        r9 = r11.local_id;	 Catch:{ Exception -> 0x0045 }
        r14 = (long) r9;	 Catch:{ Exception -> 0x0045 }
    L_0x03eb:
        r9 = r11.to_id;	 Catch:{ Exception -> 0x0045 }
        r9 = r9.channel_id;	 Catch:{ Exception -> 0x0045 }
        if (r9 == 0) goto L_0x03f9;
    L_0x03f1:
        r9 = r11.to_id;	 Catch:{ Exception -> 0x0045 }
        r9 = r9.channel_id;	 Catch:{ Exception -> 0x0045 }
        r12 = (long) r9;	 Catch:{ Exception -> 0x0045 }
        r12 = r12 << r23;
        r14 = r14 | r12;
    L_0x03f9:
        r9 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0045 }
        r12 = r11.getObjectSize();	 Catch:{ Exception -> 0x0045 }
        r9.<init>(r12);	 Catch:{ Exception -> 0x0045 }
        r11.serializeToStream(r9);	 Catch:{ Exception -> 0x0045 }
        r12 = r11.action;	 Catch:{ Exception -> 0x0045 }
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x041d;
    L_0x040b:
        r12 = r11.action;	 Catch:{ Exception -> 0x0045 }
        r12 = r12.encryptedAction;	 Catch:{ Exception -> 0x0045 }
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;	 Catch:{ Exception -> 0x0045 }
        if (r12 != 0) goto L_0x041d;
    L_0x0413:
        r12 = r11.action;	 Catch:{ Exception -> 0x0045 }
        r12 = r12.encryptedAction;	 Catch:{ Exception -> 0x0045 }
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;	 Catch:{ Exception -> 0x0045 }
        if (r12 != 0) goto L_0x041d;
    L_0x041b:
        r12 = 0;
        goto L_0x041e;
    L_0x041d:
        r12 = 1;
    L_0x041e:
        if (r12 == 0) goto L_0x044c;
    L_0x0420:
        r12 = r11.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r10 = r24;
        r12 = r10.get(r12);	 Catch:{ Exception -> 0x0045 }
        r12 = (org.telegram.tgnet.TLRPC.Message) r12;	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x0446;
    L_0x042c:
        r13 = r11.date;	 Catch:{ Exception -> 0x0045 }
        r8 = r12.date;	 Catch:{ Exception -> 0x0045 }
        if (r13 > r8) goto L_0x0446;
    L_0x0432:
        r8 = r12.id;	 Catch:{ Exception -> 0x0045 }
        if (r8 <= 0) goto L_0x043c;
    L_0x0436:
        r8 = r11.id;	 Catch:{ Exception -> 0x0045 }
        r13 = r12.id;	 Catch:{ Exception -> 0x0045 }
        if (r8 > r13) goto L_0x0446;
    L_0x043c:
        r8 = r12.id;	 Catch:{ Exception -> 0x0045 }
        if (r8 >= 0) goto L_0x044e;
    L_0x0440:
        r8 = r11.id;	 Catch:{ Exception -> 0x0045 }
        r12 = r12.id;	 Catch:{ Exception -> 0x0045 }
        if (r8 >= r12) goto L_0x044e;
    L_0x0446:
        r12 = r11.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r10.put(r12, r11);	 Catch:{ Exception -> 0x0045 }
        goto L_0x044e;
    L_0x044c:
        r10 = r24;
    L_0x044e:
        r8 = r25;
        r12 = 1;
        r8.bindLong(r12, r14);	 Catch:{ Exception -> 0x0045 }
        r12 = r11.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r24 = r6;
        r6 = 2;
        r8.bindLong(r6, r12);	 Catch:{ Exception -> 0x0045 }
        r6 = org.telegram.messenger.MessageObject.getUnreadFlags(r11);	 Catch:{ Exception -> 0x0045 }
        r12 = 3;
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
        r6 = r11.send_state;	 Catch:{ Exception -> 0x0045 }
        r12 = 4;
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
        r6 = r11.date;	 Catch:{ Exception -> 0x0045 }
        r12 = 5;
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
        r6 = 6;
        r8.bindByteBuffer(r6, r9);	 Catch:{ Exception -> 0x0045 }
        r6 = 7;
        r12 = org.telegram.messenger.MessageObject.isOut(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x047d;
    L_0x047b:
        r12 = 1;
        goto L_0x047e;
    L_0x047d:
        r12 = 0;
    L_0x047e:
        r8.bindInteger(r6, r12);	 Catch:{ Exception -> 0x0045 }
        r6 = r11.ttl;	 Catch:{ Exception -> 0x0045 }
        r12 = 8;
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
        r6 = r11.flags;	 Catch:{ Exception -> 0x0045 }
        r6 = r6 & 1024;
        if (r6 == 0) goto L_0x0496;
    L_0x048e:
        r6 = r11.views;	 Catch:{ Exception -> 0x0045 }
        r12 = 9;
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
        goto L_0x049f;
    L_0x0496:
        r12 = 9;
        r6 = r1.getMessageMediaType(r11);	 Catch:{ Exception -> 0x0045 }
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
    L_0x049f:
        r6 = 10;
        r12 = 0;
        r8.bindInteger(r6, r12);	 Catch:{ Exception -> 0x0045 }
        r6 = 11;
        r12 = r11.mentioned;	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x04ad;
    L_0x04ab:
        r12 = 1;
        goto L_0x04ae;
    L_0x04ad:
        r12 = 0;
    L_0x04ae:
        r8.bindInteger(r6, r12);	 Catch:{ Exception -> 0x0045 }
        r8.step();	 Catch:{ Exception -> 0x0045 }
        r12 = r11.random_id;	 Catch:{ Exception -> 0x0045 }
        r17 = 0;
        r6 = (r12 > r17 ? 1 : (r12 == r17 ? 0 : -1));
        if (r6 == 0) goto L_0x04d1;
    L_0x04bc:
        r22.requery();	 Catch:{ Exception -> 0x0045 }
        r12 = r11.random_id;	 Catch:{ Exception -> 0x0045 }
        r6 = r22;
        r22 = r7;
        r7 = 1;
        r6.bindLong(r7, r12);	 Catch:{ Exception -> 0x0045 }
        r7 = 2;
        r6.bindLong(r7, r14);	 Catch:{ Exception -> 0x0045 }
        r6.step();	 Catch:{ Exception -> 0x0045 }
        goto L_0x04d5;
    L_0x04d1:
        r6 = r22;
        r22 = r7;
    L_0x04d5:
        r7 = org.telegram.messenger.DataQuery.canAddMessageToMedia(r11);	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x0507;
    L_0x04db:
        if (r3 != 0) goto L_0x04e5;
    L_0x04dd:
        r3 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r7 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r3 = r3.executeFast(r7);	 Catch:{ Exception -> 0x0045 }
    L_0x04e5:
        r3.requery();	 Catch:{ Exception -> 0x0045 }
        r7 = 1;
        r3.bindLong(r7, r14);	 Catch:{ Exception -> 0x0045 }
        r12 = r11.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r7 = 2;
        r3.bindLong(r7, r12);	 Catch:{ Exception -> 0x0045 }
        r7 = r11.date;	 Catch:{ Exception -> 0x0045 }
        r12 = 3;
        r3.bindInteger(r12, r7);	 Catch:{ Exception -> 0x0045 }
        r7 = org.telegram.messenger.DataQuery.getMediaType(r11);	 Catch:{ Exception -> 0x0045 }
        r12 = 4;
        r3.bindInteger(r12, r7);	 Catch:{ Exception -> 0x0045 }
        r7 = 5;
        r3.bindByteBuffer(r7, r9);	 Catch:{ Exception -> 0x0045 }
        r3.step();	 Catch:{ Exception -> 0x0045 }
    L_0x0507:
        r7 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x0532;
    L_0x050d:
        if (r4 != 0) goto L_0x0517;
    L_0x050f:
        r4 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r7 = "REPLACE INTO polls VALUES(?, ?)";
        r4 = r4.executeFast(r7);	 Catch:{ Exception -> 0x0045 }
    L_0x0517:
        r7 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r7 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r7;	 Catch:{ Exception -> 0x0045 }
        r4.requery();	 Catch:{ Exception -> 0x0045 }
        r12 = 1;
        r4.bindLong(r12, r14);	 Catch:{ Exception -> 0x0045 }
        r7 = r7.poll;	 Catch:{ Exception -> 0x0045 }
        r12 = r7.id;	 Catch:{ Exception -> 0x0045 }
        r7 = 2;
        r4.bindLong(r7, r12);	 Catch:{ Exception -> 0x0045 }
        r4.step();	 Catch:{ Exception -> 0x0045 }
    L_0x052d:
        r7 = r21;
        r21 = r3;
        goto L_0x0550;
    L_0x0532:
        r7 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x052d;
    L_0x0538:
        r21.requery();	 Catch:{ Exception -> 0x0045 }
        r7 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r7 = r7.webpage;	 Catch:{ Exception -> 0x0045 }
        r12 = r7.id;	 Catch:{ Exception -> 0x0045 }
        r7 = r21;
        r21 = r3;
        r3 = 1;
        r7.bindLong(r3, r12);	 Catch:{ Exception -> 0x0045 }
        r3 = 2;
        r7.bindLong(r3, r14);	 Catch:{ Exception -> 0x0045 }
        r7.step();	 Catch:{ Exception -> 0x0045 }
    L_0x0550:
        r9.reuse();	 Catch:{ Exception -> 0x0045 }
        if (r34 == 0) goto L_0x0694;
    L_0x0555:
        r3 = r11.to_id;	 Catch:{ Exception -> 0x0045 }
        r3 = r3.channel_id;	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x055f;
    L_0x055b:
        r3 = r11.post;	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x0694;
    L_0x055f:
        r3 = r11.date;	 Catch:{ Exception -> 0x0045 }
        r9 = r1.currentAccount;	 Catch:{ Exception -> 0x0045 }
        r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r9);	 Catch:{ Exception -> 0x0045 }
        r9 = r9.getCurrentTime();	 Catch:{ Exception -> 0x0045 }
        r9 = r9 + -3600;
        if (r3 < r9) goto L_0x0694;
    L_0x056f:
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x0045 }
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);	 Catch:{ Exception -> 0x0045 }
        r3 = r3.canDownloadMedia(r11);	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        if (r3 != r9) goto L_0x0694;
    L_0x057c:
        r3 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0045 }
        if (r3 != 0) goto L_0x058e;
    L_0x0582:
        r3 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0045 }
        if (r3 != 0) goto L_0x058e;
    L_0x0588:
        r3 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x0694;
    L_0x058e:
        r3 = org.telegram.messenger.MessageObject.getDocument(r11);	 Catch:{ Exception -> 0x0045 }
        r9 = org.telegram.messenger.MessageObject.getPhoto(r11);	 Catch:{ Exception -> 0x0045 }
        r12 = org.telegram.messenger.MessageObject.isVoiceMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x05ae;
    L_0x059c:
        r12 = r3.id;	 Catch:{ Exception -> 0x0045 }
        r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r15.document = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = 2;
        goto L_0x062c;
    L_0x05ae:
        r12 = org.telegram.messenger.MessageObject.isStickerMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x05c5;
    L_0x05b4:
        r12 = r3.id;	 Catch:{ Exception -> 0x0045 }
        r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r15.document = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
    L_0x05c3:
        r3 = 1;
        goto L_0x062c;
    L_0x05c5:
        r12 = org.telegram.messenger.MessageObject.isVideoMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 != 0) goto L_0x061c;
    L_0x05cb:
        r12 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 != 0) goto L_0x061c;
    L_0x05d1:
        r12 = org.telegram.messenger.MessageObject.isGifMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x05d8;
    L_0x05d7:
        goto L_0x061c;
    L_0x05d8:
        if (r3 == 0) goto L_0x05ec;
    L_0x05da:
        r12 = r3.id;	 Catch:{ Exception -> 0x0045 }
        r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r15.document = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = 8;
        goto L_0x062c;
    L_0x05ec:
        if (r9 == 0) goto L_0x0617;
    L_0x05ee:
        r3 = r9.sizes;	 Catch:{ Exception -> 0x0045 }
        r12 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0045 }
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r12);	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x0617;
    L_0x05fa:
        r12 = r9.id;	 Catch:{ Exception -> 0x0045 }
        r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r15.photo = r9;	 Catch:{ Exception -> 0x0045 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x05c3;
    L_0x060f:
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
        goto L_0x05c3;
    L_0x0617:
        r3 = 0;
        r12 = 0;
        r15 = 0;
        goto L_0x062c;
    L_0x061c:
        r12 = r3.id;	 Catch:{ Exception -> 0x0045 }
        r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r15.document = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = 4;
    L_0x062c:
        if (r15 == 0) goto L_0x0694;
    L_0x062e:
        r9 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r9 = r9.ttl_seconds;	 Catch:{ Exception -> 0x0045 }
        if (r9 == 0) goto L_0x0640;
    L_0x0634:
        r9 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r9 = r9.ttl_seconds;	 Catch:{ Exception -> 0x0045 }
        r15.ttl_seconds = r9;	 Catch:{ Exception -> 0x0045 }
        r9 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r14 = 4;
        r9 = r9 | r14;
        r15.flags = r9;	 Catch:{ Exception -> 0x0045 }
    L_0x0640:
        r5 = r5 | r3;
        r20.requery();	 Catch:{ Exception -> 0x0045 }
        r9 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0045 }
        r14 = r15.getObjectSize();	 Catch:{ Exception -> 0x0045 }
        r9.<init>(r14);	 Catch:{ Exception -> 0x0045 }
        r15.serializeToStream(r9);	 Catch:{ Exception -> 0x0045 }
        r14 = r20;
        r15 = 1;
        r14.bindLong(r15, r12);	 Catch:{ Exception -> 0x0045 }
        r12 = 2;
        r14.bindInteger(r12, r3);	 Catch:{ Exception -> 0x0045 }
        r3 = r11.date;	 Catch:{ Exception -> 0x0045 }
        r12 = 3;
        r14.bindInteger(r12, r3);	 Catch:{ Exception -> 0x0045 }
        r3 = 4;
        r14.bindByteBuffer(r3, r9);	 Catch:{ Exception -> 0x0045 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r3.<init>();	 Catch:{ Exception -> 0x0045 }
        r12 = "sent_";
        r3.append(r12);	 Catch:{ Exception -> 0x0045 }
        r12 = r11.to_id;	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x0677;
    L_0x0672:
        r12 = r11.to_id;	 Catch:{ Exception -> 0x0045 }
        r12 = r12.channel_id;	 Catch:{ Exception -> 0x0045 }
        goto L_0x0678;
    L_0x0677:
        r12 = 0;
    L_0x0678:
        r3.append(r12);	 Catch:{ Exception -> 0x0045 }
        r12 = "_";
        r3.append(r12);	 Catch:{ Exception -> 0x0045 }
        r11 = r11.id;	 Catch:{ Exception -> 0x0045 }
        r3.append(r11);	 Catch:{ Exception -> 0x0045 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0045 }
        r11 = 5;
        r14.bindString(r11, r3);	 Catch:{ Exception -> 0x0045 }
        r14.step();	 Catch:{ Exception -> 0x0045 }
        r9.reuse();	 Catch:{ Exception -> 0x0045 }
        goto L_0x0696;
    L_0x0694:
        r14 = r20;
    L_0x0696:
        r2 = r2 + 1;
        r25 = r8;
        r20 = r14;
        r3 = r21;
        r15 = r29;
        r21 = r7;
        r7 = r22;
        r22 = r6;
        r6 = r24;
        r24 = r10;
        goto L_0x03c9;
    L_0x06ac:
        r29 = r15;
        r14 = r20;
        r10 = r24;
        r8 = r25;
        r24 = r6;
        r6 = r22;
        r22 = r7;
        r7 = r21;
        r8.dispose();	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x06c4;
    L_0x06c1:
        r3.dispose();	 Catch:{ Exception -> 0x0045 }
    L_0x06c4:
        if (r4 == 0) goto L_0x06c9;
    L_0x06c6:
        r4.dispose();	 Catch:{ Exception -> 0x0045 }
    L_0x06c9:
        r6.dispose();	 Catch:{ Exception -> 0x0045 }
        r14.dispose();	 Catch:{ Exception -> 0x0045 }
        r7.dispose();	 Catch:{ Exception -> 0x0045 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r3 = "REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x0045 }
        r3 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r4 = "UPDATE dialogs SET date = ?, unread_count = ?, last_mid = ?, unread_count_i = ? WHERE did = ?";
        r3 = r3.executeFast(r4);	 Catch:{ Exception -> 0x0045 }
        r4 = 0;
    L_0x06e3:
        r6 = r10.size();	 Catch:{ Exception -> 0x0045 }
        if (r4 >= r6) goto L_0x087a;
    L_0x06e9:
        r6 = r10.keyAt(r4);	 Catch:{ Exception -> 0x094f }
        r8 = 0;
        r11 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r11 != 0) goto L_0x0708;
    L_0x06f3:
        r1 = r2;
        r35 = r5;
        r20 = r10;
        r11 = r22;
        r25 = r24;
        r2 = 5;
        r5 = 6;
        r6 = 0;
        r9 = 9;
        r24 = r4;
        r4 = 8;
        goto L_0x086a;
    L_0x0708:
        r8 = r10.valueAt(r4);	 Catch:{ Exception -> 0x094f }
        r8 = (org.telegram.tgnet.TLRPC.Message) r8;	 Catch:{ Exception -> 0x094f }
        if (r8 == 0) goto L_0x0715;
    L_0x0710:
        r9 = r8.to_id;	 Catch:{ Exception -> 0x0045 }
        r9 = r9.channel_id;	 Catch:{ Exception -> 0x0045 }
        goto L_0x0716;
    L_0x0715:
        r9 = 0;
    L_0x0716:
        r11 = r1.database;	 Catch:{ Exception -> 0x094f }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x094f }
        r12.<init>();	 Catch:{ Exception -> 0x094f }
        r13 = "SELECT date, unread_count, last_mid, unread_count_i FROM dialogs WHERE did = ";
        r12.append(r13);	 Catch:{ Exception -> 0x094f }
        r12.append(r6);	 Catch:{ Exception -> 0x094f }
        r12 = r12.toString();	 Catch:{ Exception -> 0x094f }
        r13 = 0;
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x094f }
        r11 = r11.queryFinalized(r12, r14);	 Catch:{ Exception -> 0x094f }
        r12 = r11.next();	 Catch:{ Exception -> 0x094f }
        if (r12 == 0) goto L_0x075c;
    L_0x0736:
        r14 = r11.intValue(r13);	 Catch:{ Exception -> 0x0045 }
        r20 = r10;
        r15 = 1;
        r10 = r11.intValue(r15);	 Catch:{ Exception -> 0x0045 }
        r10 = java.lang.Math.max(r13, r10);	 Catch:{ Exception -> 0x0045 }
        r15 = 2;
        r21 = r11.intValue(r15);	 Catch:{ Exception -> 0x0045 }
        r31 = r10;
        r15 = 3;
        r10 = r11.intValue(r15);	 Catch:{ Exception -> 0x0045 }
        r10 = java.lang.Math.max(r13, r10);	 Catch:{ Exception -> 0x0045 }
        r13 = r10;
        r15 = r14;
        r14 = r21;
        r10 = r31;
        goto L_0x076d;
    L_0x075c:
        r20 = r10;
        if (r9 == 0) goto L_0x0769;
    L_0x0760:
        r10 = r1.currentAccount;	 Catch:{ Exception -> 0x0045 }
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);	 Catch:{ Exception -> 0x0045 }
        r10.checkChannelInviter(r9);	 Catch:{ Exception -> 0x0045 }
    L_0x0769:
        r10 = 0;
        r13 = 0;
        r14 = 0;
        r15 = 0;
    L_0x076d:
        r11.dispose();	 Catch:{ Exception -> 0x094f }
        r11 = r22;
        r21 = r11.get(r6);	 Catch:{ Exception -> 0x094f }
        r21 = (java.lang.Integer) r21;	 Catch:{ Exception -> 0x094f }
        r35 = r5;
        r5 = r24;
        r22 = r5.get(r6);	 Catch:{ Exception -> 0x094f }
        r22 = (java.lang.Integer) r22;	 Catch:{ Exception -> 0x094f }
        if (r22 != 0) goto L_0x078b;
    L_0x0784:
        r16 = 0;
        r22 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x0045 }
        goto L_0x0798;
    L_0x078b:
        r24 = r22.intValue();	 Catch:{ Exception -> 0x094f }
        r24 = r24 + r10;
        r1 = java.lang.Integer.valueOf(r24);	 Catch:{ Exception -> 0x094f }
        r5.put(r6, r1);	 Catch:{ Exception -> 0x094f }
    L_0x0798:
        if (r21 != 0) goto L_0x07a0;
    L_0x079a:
        r1 = 0;
        r21 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x094f }
        goto L_0x07ac;
    L_0x07a0:
        r1 = r21.intValue();	 Catch:{ Exception -> 0x094f }
        r1 = r1 + r13;
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x094f }
        r11.put(r6, r1);	 Catch:{ Exception -> 0x094f }
    L_0x07ac:
        if (r8 == 0) goto L_0x07b6;
    L_0x07ae:
        r1 = r8.id;	 Catch:{ Exception -> 0x094f }
        r24 = r4;
        r25 = r5;
        r4 = (long) r1;	 Catch:{ Exception -> 0x094f }
        goto L_0x07bb;
    L_0x07b6:
        r24 = r4;
        r25 = r5;
        r4 = (long) r14;	 Catch:{ Exception -> 0x094f }
    L_0x07bb:
        if (r8 == 0) goto L_0x07c4;
    L_0x07bd:
        r1 = r8.local_id;	 Catch:{ Exception -> 0x094f }
        if (r1 == 0) goto L_0x07c4;
    L_0x07c1:
        r1 = r8.local_id;	 Catch:{ Exception -> 0x094f }
        r4 = (long) r1;	 Catch:{ Exception -> 0x094f }
    L_0x07c4:
        if (r9 == 0) goto L_0x07cd;
    L_0x07c6:
        r31 = r2;
        r1 = (long) r9;	 Catch:{ Exception -> 0x094f }
        r1 = r1 << r23;
        r4 = r4 | r1;
        goto L_0x07cf;
    L_0x07cd:
        r31 = r2;
    L_0x07cf:
        if (r12 == 0) goto L_0x0808;
    L_0x07d1:
        r3.requery();	 Catch:{ Exception -> 0x094f }
        if (r8 == 0) goto L_0x07dc;
    L_0x07d6:
        if (r33 == 0) goto L_0x07da;
    L_0x07d8:
        if (r15 != 0) goto L_0x07dc;
    L_0x07da:
        r15 = r8.date;	 Catch:{ Exception -> 0x094f }
    L_0x07dc:
        r1 = 1;
        r3.bindInteger(r1, r15);	 Catch:{ Exception -> 0x094f }
        r1 = r22.intValue();	 Catch:{ Exception -> 0x094f }
        r10 = r10 + r1;
        r1 = 2;
        r3.bindInteger(r1, r10);	 Catch:{ Exception -> 0x094f }
        r1 = 3;
        r3.bindLong(r1, r4);	 Catch:{ Exception -> 0x094f }
        r1 = r21.intValue();	 Catch:{ Exception -> 0x094f }
        r13 = r13 + r1;
        r1 = 4;
        r3.bindInteger(r1, r13);	 Catch:{ Exception -> 0x094f }
        r1 = 5;
        r3.bindLong(r1, r6);	 Catch:{ Exception -> 0x094f }
        r3.step();	 Catch:{ Exception -> 0x094f }
        r1 = r31;
        r2 = 5;
        r4 = 8;
        r5 = 6;
        r6 = 0;
        r9 = 9;
        goto L_0x086a;
    L_0x0808:
        r31.requery();	 Catch:{ Exception -> 0x094f }
        r1 = r31;
        r2 = 1;
        r1.bindLong(r2, r6);	 Catch:{ Exception -> 0x094f }
        if (r8 == 0) goto L_0x0819;
    L_0x0813:
        if (r33 == 0) goto L_0x0817;
    L_0x0815:
        if (r15 != 0) goto L_0x0819;
    L_0x0817:
        r15 = r8.date;	 Catch:{ Exception -> 0x094f }
    L_0x0819:
        r2 = 2;
        r1.bindInteger(r2, r15);	 Catch:{ Exception -> 0x094f }
        r2 = r22.intValue();	 Catch:{ Exception -> 0x094f }
        r10 = r10 + r2;
        r2 = 3;
        r1.bindInteger(r2, r10);	 Catch:{ Exception -> 0x094f }
        r2 = 4;
        r1.bindLong(r2, r4);	 Catch:{ Exception -> 0x094f }
        r2 = 5;
        r4 = 0;
        r1.bindInteger(r2, r4);	 Catch:{ Exception -> 0x094f }
        r5 = 6;
        r1.bindInteger(r5, r4);	 Catch:{ Exception -> 0x094f }
        r4 = 7;
        r6 = 0;
        r1.bindLong(r4, r6);	 Catch:{ Exception -> 0x094f }
        r4 = r21.intValue();	 Catch:{ Exception -> 0x094f }
        r13 = r13 + r4;
        r4 = 8;
        r1.bindInteger(r4, r13);	 Catch:{ Exception -> 0x094f }
        if (r9 == 0) goto L_0x0847;
    L_0x0845:
        r8 = 1;
        goto L_0x0848;
    L_0x0847:
        r8 = 0;
    L_0x0848:
        r9 = 9;
        r1.bindInteger(r9, r8);	 Catch:{ Exception -> 0x094f }
        r8 = 10;
        r10 = 0;
        r1.bindInteger(r8, r10);	 Catch:{ Exception -> 0x094f }
        r8 = 11;
        r1.bindInteger(r8, r10);	 Catch:{ Exception -> 0x094f }
        r8 = 12;
        r1.bindInteger(r8, r10);	 Catch:{ Exception -> 0x094f }
        r8 = 13;
        r1.bindInteger(r8, r10);	 Catch:{ Exception -> 0x094f }
        r8 = 14;
        r1.bindNull(r8);	 Catch:{ Exception -> 0x094f }
        r1.step();	 Catch:{ Exception -> 0x094f }
    L_0x086a:
        r8 = r24 + 1;
        r5 = r35;
        r2 = r1;
        r4 = r8;
        r22 = r11;
        r10 = r20;
        r24 = r25;
        r1 = r30;
        goto L_0x06e3;
    L_0x087a:
        r1 = r2;
        r35 = r5;
        r11 = r22;
        r25 = r24;
        r3.dispose();	 Catch:{ Exception -> 0x094f }
        r1.dispose();	 Catch:{ Exception -> 0x094f }
        if (r29 == 0) goto L_0x092e;
    L_0x0889:
        r1 = r30;
        r2 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r3 = "REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x0045 }
        r3 = 0;
    L_0x0894:
        r4 = r29.size();	 Catch:{ Exception -> 0x0045 }
        if (r3 >= r4) goto L_0x092a;
    L_0x089a:
        r15 = r29;
        r4 = r15.keyAt(r3);	 Catch:{ Exception -> 0x0045 }
        r5 = r15.valueAt(r3);	 Catch:{ Exception -> 0x0045 }
        r5 = (android.util.LongSparseArray) r5;	 Catch:{ Exception -> 0x0045 }
        r6 = 0;
    L_0x08a7:
        r7 = r5.size();	 Catch:{ Exception -> 0x0045 }
        if (r6 >= r7) goto L_0x091e;
    L_0x08ad:
        r7 = r5.keyAt(r6);	 Catch:{ Exception -> 0x0045 }
        r9 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x0045 }
        r12 = "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1";
        r13 = 2;
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0045 }
        r13 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x0045 }
        r29 = r15;
        r15 = 0;
        r14[r15] = r13;	 Catch:{ Exception -> 0x0045 }
        r13 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0045 }
        r16 = 1;
        r14[r16] = r13;	 Catch:{ Exception -> 0x0045 }
        r10 = java.lang.String.format(r10, r12, r14);	 Catch:{ Exception -> 0x0045 }
        r12 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x0045 }
        r9 = r9.queryFinalized(r10, r12);	 Catch:{ Exception -> 0x0045 }
        r10 = r9.next();	 Catch:{ Exception -> 0x0045 }
        if (r10 == 0) goto L_0x08e5;
    L_0x08db:
        r10 = r9.intValue(r15);	 Catch:{ Exception -> 0x0045 }
        r12 = 1;
        r13 = r9.intValue(r12);	 Catch:{ Exception -> 0x0045 }
        goto L_0x08e7;
    L_0x08e5:
        r10 = -1;
        r13 = 0;
    L_0x08e7:
        r9.dispose();	 Catch:{ Exception -> 0x0045 }
        r9 = -1;
        if (r10 == r9) goto L_0x0914;
    L_0x08ed:
        r2.requery();	 Catch:{ Exception -> 0x0045 }
        r12 = r5.valueAt(r6);	 Catch:{ Exception -> 0x0045 }
        r12 = (java.lang.Integer) r12;	 Catch:{ Exception -> 0x0045 }
        r12 = r12.intValue();	 Catch:{ Exception -> 0x0045 }
        r10 = r10 + r12;
        r12 = 1;
        r2.bindLong(r12, r7);	 Catch:{ Exception -> 0x0045 }
        r7 = 2;
        r2.bindInteger(r7, r4);	 Catch:{ Exception -> 0x0045 }
        r8 = 0;
        r10 = java.lang.Math.max(r8, r10);	 Catch:{ Exception -> 0x0045 }
        r14 = 3;
        r2.bindInteger(r14, r10);	 Catch:{ Exception -> 0x0045 }
        r10 = 4;
        r2.bindInteger(r10, r13);	 Catch:{ Exception -> 0x0045 }
        r2.step();	 Catch:{ Exception -> 0x0045 }
        goto L_0x0919;
    L_0x0914:
        r7 = 2;
        r8 = 0;
        r10 = 4;
        r12 = 1;
        r14 = 3;
    L_0x0919:
        r6 = r6 + 1;
        r15 = r29;
        goto L_0x08a7;
    L_0x091e:
        r29 = r15;
        r7 = 2;
        r8 = 0;
        r9 = -1;
        r10 = 4;
        r12 = 1;
        r14 = 3;
        r3 = r3 + 1;
        goto L_0x0894;
    L_0x092a:
        r2.dispose();	 Catch:{ Exception -> 0x0045 }
        goto L_0x0930;
    L_0x092e:
        r1 = r30;
    L_0x0930:
        if (r32 == 0) goto L_0x0937;
    L_0x0932:
        r2 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r2.commitTransaction();	 Catch:{ Exception -> 0x0045 }
    L_0x0937:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x0045 }
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);	 Catch:{ Exception -> 0x0045 }
        r3 = r25;
        r2.processDialogsUpdateRead(r3, r11);	 Catch:{ Exception -> 0x0045 }
        if (r35 == 0) goto L_0x0957;
    L_0x0944:
        r2 = new org.telegram.messenger.-$$Lambda$MessagesStorage$hdskr9sBcEtVOZp5sVxQkcTGMHk;	 Catch:{ Exception -> 0x0045 }
        r5 = r35;
        r2.<init>(r1, r5);	 Catch:{ Exception -> 0x0045 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x0045 }
        goto L_0x0957;
    L_0x094f:
        r0 = move-exception;
        r1 = r30;
        goto L_0x0046;
    L_0x0954:
        org.telegram.messenger.FileLog.e(r2);
    L_0x0957:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.putMessagesInternal(java.util.ArrayList, boolean, boolean, int, boolean):void");
    }

    public /* synthetic */ void lambda$putMessagesInternal$119$MessagesStorage(int i) {
        DownloadController.getInstance(this.currentAccount).newDownloadObjectsAvailable(i);
    }

    public void putMessages(ArrayList<Message> arrayList, boolean z, boolean z2, boolean z3, int i) {
        putMessages(arrayList, z, z2, z3, i, false);
    }

    public void putMessages(ArrayList<Message> arrayList, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        if (arrayList.size() != 0) {
            if (z2) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Qn4Hipii9AUzOU-hRSbCs5JbBWU(this, arrayList, z, z3, i, z4));
            } else {
                putMessagesInternal(arrayList, z, z3, i, z4);
            }
        }
    }

    public /* synthetic */ void lambda$putMessages$120$MessagesStorage(ArrayList arrayList, boolean z, boolean z2, int i, boolean z3) {
        putMessagesInternal(arrayList, z, z2, i, z3);
    }

    public void markMessageAsSendError(Message message) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$3K5zEdu7IXL2Byt7jgk1XcfVdFQ(this, message));
    }

    public /* synthetic */ void lambda$markMessageAsSendError$121$MessagesStorage(Message message) {
        try {
            long j = (long) message.id;
            if (message.to_id.channel_id != 0) {
                j |= ((long) message.to_id.channel_id) << 32;
            }
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("UPDATE messages SET send_state = 2 WHERE mid = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setMessageSeq(int i, int i2, int i3) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$k_7mnpoBHeRcexHoPn--JboXbYE(this, i, i2, i3));
    }

    public /* synthetic */ void lambda$setMessageSeq$122$MessagesStorage(int i, int i2, int i3) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
            executeFast.requery();
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            executeFast.bindInteger(3, i3);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x004f A:{RETURN} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:77:0x00fb */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x01ba  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:89:0x0159 */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x004f A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0057  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:56:0x00c7, code skipped:
            if (r3 != null) goto L_0x00d2;
     */
    /* JADX WARNING: Missing block: B:61:0x00d0, code skipped:
            if (r3 == null) goto L_0x00d5;
     */
    /* JADX WARNING: Missing block: B:62:0x00d2, code skipped:
            r3.dispose();
     */
    /* JADX WARNING: Missing block: B:64:0x00db, code skipped:
            return new long[]{r15, r4};
     */
    /* JADX WARNING: Missing block: B:72:0x00f3, code skipped:
            if (r2 != null) goto L_0x013f;
     */
    /* JADX WARNING: Missing block: B:78:?, code skipped:
            r1.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM messages WHERE mid = %d", new java.lang.Object[]{java.lang.Long.valueOf(r11)})).stepThis().dispose();
            r1.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM messages_seq WHERE mid = %d", new java.lang.Object[]{java.lang.Long.valueOf(r11)})).stepThis().dispose();
     */
    /* JADX WARNING: Missing block: B:79:0x0136, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:80:0x0139, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:82:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:83:0x013d, code skipped:
            if (r2 == null) goto L_0x0143;
     */
    /* JADX WARNING: Missing block: B:84:0x013f, code skipped:
            r2.dispose();
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:86:?, code skipped:
            r2 = r1.database.executeFast("UPDATE media_v2 SET mid = ? WHERE mid = ?");
            r2.bindLong(1, r13);
            r2.bindLong(2, r11);
            r2.step();
     */
    /* JADX WARNING: Missing block: B:87:0x0154, code skipped:
            if (r2 == null) goto L_0x0181;
     */
    /* JADX WARNING: Missing block: B:88:0x0157, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:90:?, code skipped:
            r1.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM media_v2 WHERE mid = %d", new java.lang.Object[]{java.lang.Long.valueOf(r11)})).stepThis().dispose();
     */
    /* JADX WARNING: Missing block: B:91:0x0177, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:93:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:94:0x017b, code skipped:
            if (r2 == null) goto L_0x0181;
     */
    /* JADX WARNING: Missing block: B:95:0x017d, code skipped:
            r2.dispose();
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:97:?, code skipped:
            r2 = r1.database.executeFast("UPDATE dialogs SET last_mid = ? WHERE last_mid = ?");
            r2.bindLong(1, r13);
            r2.bindLong(2, r11);
            r2.step();
     */
    /* JADX WARNING: Missing block: B:98:0x0192, code skipped:
            if (r2 == null) goto L_0x01a0;
     */
    /* JADX WARNING: Missing block: B:99:0x0195, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:100:0x0197, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:102:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:103:0x019b, code skipped:
            if (r2 == null) goto L_0x01a0;
     */
    /* JADX WARNING: Missing block: B:104:0x019d, code skipped:
            r2.dispose();
     */
    /* JADX WARNING: Missing block: B:106:0x01ab, code skipped:
            return new long[]{r15, (long) r9.intValue()};
     */
    /* JADX WARNING: Missing block: B:107:0x01ac, code skipped:
            if (r2 != null) goto L_0x01ae;
     */
    /* JADX WARNING: Missing block: B:108:0x01ae, code skipped:
            r2.dispose();
     */
    /* JADX WARNING: Missing block: B:109:0x01b1, code skipped:
            throw r0;
     */
    /* JADX WARNING: Missing block: B:110:0x01b2, code skipped:
            if (r2 != null) goto L_0x01b4;
     */
    /* JADX WARNING: Missing block: B:111:0x01b4, code skipped:
            r2.dispose();
     */
    /* JADX WARNING: Missing block: B:112:0x01b7, code skipped:
            throw r0;
     */
    /* JADX WARNING: Missing block: B:114:0x01ba, code skipped:
            r2.dispose();
     */
    private long[] updateMessageStateAndIdInternal(long r20, java.lang.Integer r22, int r23, int r24, int r25) {
        /*
        r19 = this;
        r1 = r19;
        r2 = r24;
        r0 = r23;
        r3 = r25;
        r4 = (long) r0;
        r6 = 0;
        r7 = 0;
        r8 = 1;
        if (r22 != 0) goto L_0x005b;
    L_0x000e:
        r0 = r1.database;	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r10 = "SELECT mid FROM randoms WHERE random_id = %d LIMIT 1";
        r11 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r12 = java.lang.Long.valueOf(r20);	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r11[r7] = r12;	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r9 = java.lang.String.format(r9, r10, r11);	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r10 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r9 = r0.queryFinalized(r9, r10);	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r0 = r9.next();	 Catch:{ Exception -> 0x003d }
        if (r0 == 0) goto L_0x0035;
    L_0x002c:
        r0 = r9.intValue(r7);	 Catch:{ Exception -> 0x003d }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x003d }
        goto L_0x0037;
    L_0x0035:
        r0 = r22;
    L_0x0037:
        if (r9 == 0) goto L_0x004d;
    L_0x0039:
        r9.dispose();
        goto L_0x004d;
    L_0x003d:
        r0 = move-exception;
        goto L_0x0043;
    L_0x003f:
        r0 = move-exception;
        goto L_0x0055;
    L_0x0041:
        r0 = move-exception;
        r9 = r6;
    L_0x0043:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0053 }
        if (r9 == 0) goto L_0x004b;
    L_0x0048:
        r9.dispose();
    L_0x004b:
        r0 = r22;
    L_0x004d:
        if (r0 != 0) goto L_0x0050;
    L_0x004f:
        return r6;
    L_0x0050:
        r10 = r9;
        r9 = r0;
        goto L_0x005e;
    L_0x0053:
        r0 = move-exception;
        r6 = r9;
    L_0x0055:
        if (r6 == 0) goto L_0x005a;
    L_0x0057:
        r6.dispose();
    L_0x005a:
        throw r0;
    L_0x005b:
        r9 = r22;
        r10 = r6;
    L_0x005e:
        r0 = r9.intValue();
        r11 = (long) r0;
        if (r3 == 0) goto L_0x006c;
    L_0x0065:
        r13 = (long) r3;
        r0 = 32;
        r13 = r13 << r0;
        r11 = r11 | r13;
        r13 = r13 | r4;
        goto L_0x006d;
    L_0x006c:
        r13 = r4;
    L_0x006d:
        r0 = r1.database;	 Catch:{ Exception -> 0x009b }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x009b }
        r6 = "SELECT uid FROM messages WHERE mid = %d LIMIT 1";
        r15 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x009b }
        r16 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x009b }
        r15[r7] = r16;	 Catch:{ Exception -> 0x009b }
        r3 = java.lang.String.format(r3, r6, r15);	 Catch:{ Exception -> 0x009b }
        r6 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x009b }
        r10 = r0.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x009b }
        r0 = r10.next();	 Catch:{ Exception -> 0x009b }
        if (r0 == 0) goto L_0x0090;
    L_0x008b:
        r15 = r10.longValue(r7);	 Catch:{ Exception -> 0x009b }
        goto L_0x0092;
    L_0x0090:
        r15 = 0;
    L_0x0092:
        if (r10 == 0) goto L_0x00a6;
    L_0x0094:
        r10.dispose();
        goto L_0x00a6;
    L_0x0098:
        r0 = move-exception;
        goto L_0x01be;
    L_0x009b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0098 }
        if (r10 == 0) goto L_0x00a4;
    L_0x00a1:
        r10.dispose();
    L_0x00a4:
        r15 = 0;
    L_0x00a6:
        r17 = 0;
        r0 = (r15 > r17 ? 1 : (r15 == r17 ? 0 : -1));
        if (r0 != 0) goto L_0x00ae;
    L_0x00ac:
        r3 = 0;
        return r3;
    L_0x00ae:
        r3 = 0;
        r6 = 2;
        r0 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r0 != 0) goto L_0x00e2;
    L_0x00b4:
        if (r2 == 0) goto L_0x00e2;
    L_0x00b6:
        r0 = r1.database;	 Catch:{ Exception -> 0x00cc }
        r9 = "UPDATE messages SET send_state = 0, date = ? WHERE mid = ?";
        r3 = r0.executeFast(r9);	 Catch:{ Exception -> 0x00cc }
        r3.bindInteger(r8, r2);	 Catch:{ Exception -> 0x00cc }
        r3.bindLong(r6, r13);	 Catch:{ Exception -> 0x00cc }
        r3.step();	 Catch:{ Exception -> 0x00cc }
        if (r3 == 0) goto L_0x00d5;
    L_0x00c9:
        goto L_0x00d2;
    L_0x00ca:
        r0 = move-exception;
        goto L_0x00dc;
    L_0x00cc:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x00ca }
        if (r3 == 0) goto L_0x00d5;
    L_0x00d2:
        r3.dispose();
    L_0x00d5:
        r0 = new long[r6];
        r0[r7] = r15;
        r0[r8] = r4;
        return r0;
    L_0x00dc:
        if (r3 == 0) goto L_0x00e1;
    L_0x00de:
        r3.dispose();
    L_0x00e1:
        throw r0;
    L_0x00e2:
        r0 = r1.database;	 Catch:{ Exception -> 0x00fa, all -> 0x00f6 }
        r2 = "UPDATE messages SET mid = ?, send_state = 0 WHERE mid = ?";
        r2 = r0.executeFast(r2);	 Catch:{ Exception -> 0x00fa, all -> 0x00f6 }
        r2.bindLong(r8, r13);	 Catch:{ Exception -> 0x00fb }
        r2.bindLong(r6, r11);	 Catch:{ Exception -> 0x00fb }
        r2.step();	 Catch:{ Exception -> 0x00fb }
        if (r2 == 0) goto L_0x0143;
    L_0x00f5:
        goto L_0x013f;
    L_0x00f6:
        r0 = move-exception;
        r2 = r3;
        goto L_0x01b8;
    L_0x00fa:
        r2 = r3;
    L_0x00fb:
        r0 = r1.database;	 Catch:{ Exception -> 0x0139 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0139 }
        r5 = "DELETE FROM messages WHERE mid = %d";
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0139 }
        r17 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0139 }
        r10[r7] = r17;	 Catch:{ Exception -> 0x0139 }
        r4 = java.lang.String.format(r4, r5, r10);	 Catch:{ Exception -> 0x0139 }
        r0 = r0.executeFast(r4);	 Catch:{ Exception -> 0x0139 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0139 }
        r0.dispose();	 Catch:{ Exception -> 0x0139 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0139 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0139 }
        r5 = "DELETE FROM messages_seq WHERE mid = %d";
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0139 }
        r17 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0139 }
        r10[r7] = r17;	 Catch:{ Exception -> 0x0139 }
        r4 = java.lang.String.format(r4, r5, r10);	 Catch:{ Exception -> 0x0139 }
        r0 = r0.executeFast(r4);	 Catch:{ Exception -> 0x0139 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0139 }
        r0.dispose();	 Catch:{ Exception -> 0x0139 }
        goto L_0x013d;
    L_0x0136:
        r0 = move-exception;
        goto L_0x01b8;
    L_0x0139:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0136 }
    L_0x013d:
        if (r2 == 0) goto L_0x0143;
    L_0x013f:
        r2.dispose();
        r2 = r3;
    L_0x0143:
        r0 = r1.database;	 Catch:{ Exception -> 0x0159 }
        r4 = "UPDATE media_v2 SET mid = ? WHERE mid = ?";
        r2 = r0.executeFast(r4);	 Catch:{ Exception -> 0x0159 }
        r2.bindLong(r8, r13);	 Catch:{ Exception -> 0x0159 }
        r2.bindLong(r6, r11);	 Catch:{ Exception -> 0x0159 }
        r2.step();	 Catch:{ Exception -> 0x0159 }
        if (r2 == 0) goto L_0x0181;
    L_0x0156:
        goto L_0x017d;
    L_0x0157:
        r0 = move-exception;
        goto L_0x01b2;
    L_0x0159:
        r0 = r1.database;	 Catch:{ Exception -> 0x0177 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0177 }
        r5 = "DELETE FROM media_v2 WHERE mid = %d";
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0177 }
        r17 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0177 }
        r10[r7] = r17;	 Catch:{ Exception -> 0x0177 }
        r4 = java.lang.String.format(r4, r5, r10);	 Catch:{ Exception -> 0x0177 }
        r0 = r0.executeFast(r4);	 Catch:{ Exception -> 0x0177 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0177 }
        r0.dispose();	 Catch:{ Exception -> 0x0177 }
        goto L_0x017b;
    L_0x0177:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0157 }
    L_0x017b:
        if (r2 == 0) goto L_0x0181;
    L_0x017d:
        r2.dispose();
        r2 = r3;
    L_0x0181:
        r0 = r1.database;	 Catch:{ Exception -> 0x0197 }
        r3 = "UPDATE dialogs SET last_mid = ? WHERE last_mid = ?";
        r2 = r0.executeFast(r3);	 Catch:{ Exception -> 0x0197 }
        r2.bindLong(r8, r13);	 Catch:{ Exception -> 0x0197 }
        r2.bindLong(r6, r11);	 Catch:{ Exception -> 0x0197 }
        r2.step();	 Catch:{ Exception -> 0x0197 }
        if (r2 == 0) goto L_0x01a0;
    L_0x0194:
        goto L_0x019d;
    L_0x0195:
        r0 = move-exception;
        goto L_0x01ac;
    L_0x0197:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0195 }
        if (r2 == 0) goto L_0x01a0;
    L_0x019d:
        r2.dispose();
    L_0x01a0:
        r0 = new long[r6];
        r0[r7] = r15;
        r2 = r9.intValue();
        r2 = (long) r2;
        r0[r8] = r2;
        return r0;
    L_0x01ac:
        if (r2 == 0) goto L_0x01b1;
    L_0x01ae:
        r2.dispose();
    L_0x01b1:
        throw r0;
    L_0x01b2:
        if (r2 == 0) goto L_0x01b7;
    L_0x01b4:
        r2.dispose();
    L_0x01b7:
        throw r0;
    L_0x01b8:
        if (r2 == 0) goto L_0x01bd;
    L_0x01ba:
        r2.dispose();
    L_0x01bd:
        throw r0;
    L_0x01be:
        if (r10 == 0) goto L_0x01c3;
    L_0x01c0:
        r10.dispose();
    L_0x01c3:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateMessageStateAndIdInternal(long, java.lang.Integer, int, int, int):long[]");
    }

    public /* synthetic */ void lambda$updateMessageStateAndId$123$MessagesStorage(long j, Integer num, int i, int i2, int i3) {
        updateMessageStateAndIdInternal(j, num, i, i2, i3);
    }

    public long[] updateMessageStateAndId(long j, Integer num, int i, int i2, boolean z, int i3) {
        if (z) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$pXoPgZbfv5myiOz2LSTctrSYxWA(this, j, num, i, i2, i3));
            return null;
        }
        return updateMessageStateAndIdInternal(j, num, i, i2, i3);
    }

    public void updateUsers(ArrayList<User> arrayList, boolean z, boolean z2, boolean z3) {
        if (arrayList != null && !arrayList.isEmpty()) {
            if (z3) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Pc7K6ecECusx3bBFN6ZUjPQn8fM(this, arrayList, z, z2));
            } else {
                updateUsersInternal(arrayList, z, z2);
            }
        }
    }

    public /* synthetic */ void lambda$updateUsers$124$MessagesStorage(ArrayList arrayList, boolean z, boolean z2) {
        updateUsersInternal(arrayList, z, z2);
    }

    private void markMessagesAsReadInternal(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray) {
        SparseLongArray sparseLongArray3 = sparseLongArray;
        SparseLongArray sparseLongArray4 = sparseLongArray2;
        SparseIntArray sparseIntArray2 = sparseIntArray;
        try {
            int i;
            long j;
            int i2 = 0;
            if (!isEmpty(sparseLongArray)) {
                SQLitePreparedStatement executeFast = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid <= ?");
                for (int i3 = 0; i3 < sparseLongArray.size(); i3++) {
                    int keyAt = sparseLongArray3.keyAt(i3);
                    long j2 = sparseLongArray3.get(keyAt);
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 0", new Object[]{Integer.valueOf(keyAt), Long.valueOf(j2)})).stepThis().dispose();
                    executeFast.requery();
                    executeFast.bindLong(1, (long) keyAt);
                    executeFast.bindLong(2, j2);
                    executeFast.step();
                }
                executeFast.dispose();
            }
            if (!isEmpty(sparseLongArray2)) {
                for (i = 0; i < sparseLongArray2.size(); i++) {
                    j = sparseLongArray4.get(sparseLongArray4.keyAt(i));
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 1", new Object[]{Integer.valueOf(r4), Long.valueOf(j)})).stepThis().dispose();
                }
            }
            if (sparseIntArray2 != null && !isEmpty(sparseIntArray)) {
                while (i2 < sparseIntArray.size()) {
                    j = ((long) sparseIntArray2.keyAt(i2)) << 32;
                    i = sparseIntArray2.valueAt(i2);
                    SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 1");
                    executeFast2.requery();
                    executeFast2.bindLong(1, j);
                    executeFast2.bindInteger(2, i);
                    executeFast2.step();
                    executeFast2.dispose();
                    i2++;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void markMessagesContentAsRead(ArrayList<Long> arrayList, int i) {
        if (!isEmpty((List) arrayList)) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$KgHBeDaUYfoI-bfK3rwupoQoDPw(this, arrayList, i));
        }
    }

    public /* synthetic */ void lambda$markMessagesContentAsRead$125$MessagesStorage(ArrayList arrayList, int i) {
        try {
            String join = TextUtils.join(",", arrayList);
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid IN (%s)", new Object[]{join})).stepThis().dispose();
            if (i != 0) {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE mid IN (%s) AND ttl > 0", new Object[]{join}), new Object[0]);
                ArrayList arrayList2 = null;
                while (queryFinalized.next()) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
                }
                if (arrayList2 != null) {
                    emptyMessagesMedia(arrayList2);
                }
                queryFinalized.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$markMessagesAsRead$126$MessagesStorage(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray) {
        markMessagesAsReadInternal(sparseLongArray, sparseLongArray2, sparseIntArray);
    }

    public void markMessagesAsRead(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$PcegnchEvcNCEGCLASSNAMEiFs3Y8vTQ(this, sparseLongArray, sparseLongArray2, sparseIntArray));
        } else {
            markMessagesAsReadInternal(sparseLongArray, sparseLongArray2, sparseIntArray);
        }
    }

    public void markMessagesAsDeletedByRandoms(ArrayList<Long> arrayList) {
        if (!arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$SJbmNXX7tfVOVouAzoCxJdR34sc(this, arrayList));
        }
    }

    public /* synthetic */ void lambda$markMessagesAsDeletedByRandoms$128$MessagesStorage(ArrayList arrayList) {
        try {
            String join = TextUtils.join(",", arrayList);
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id IN(%s)", new Object[]{join}), new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            while (queryFinalized.next()) {
                arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
            }
            queryFinalized.dispose();
            if (!arrayList2.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$QctQM0bunyPsU-cfW6RijoRzGfk(this, arrayList2));
                updateDialogsWithReadMessagesInternal(arrayList2, null, null, null);
                markMessagesAsDeletedInternal(arrayList2, 0);
                updateDialogsWithDeletedMessagesInternal(arrayList2, null, 0);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$127$MessagesStorage(ArrayList arrayList) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(0));
    }

    /* Access modifiers changed, original: protected */
    public void deletePushMessages(long j, ArrayList<Integer> arrayList) {
        try {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM unread_push_messages WHERE uid = %d AND mid IN(%s)", new Object[]{Long.valueOf(j), TextUtils.join(",", arrayList)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:74:0x01b2 A:{Catch:{ Exception -> 0x03f2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0399 A:{Catch:{ Exception -> 0x03f2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x029b A:{Catch:{ Exception -> 0x03f2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01b2 A:{Catch:{ Exception -> 0x03f2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x029b A:{Catch:{ Exception -> 0x03f2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0399 A:{Catch:{ Exception -> 0x03f2 }} */
    private java.util.ArrayList<java.lang.Long> markMessagesAsDeletedInternal(java.util.ArrayList<java.lang.Integer> r21, int r22) {
        /*
        r20 = this;
        r1 = r20;
        r2 = r21;
        r3 = r22;
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03f2 }
        r5.<init>(r2);	 Catch:{ Exception -> 0x03f2 }
        r6 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03f2 }
        r6.<init>();	 Catch:{ Exception -> 0x03f2 }
        r7 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x03f2 }
        r7.<init>();	 Catch:{ Exception -> 0x03f2 }
        r8 = 0;
        if (r3 == 0) goto L_0x004e;
    L_0x0018:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03f2 }
        r9 = r21.size();	 Catch:{ Exception -> 0x03f2 }
        r0.<init>(r9);	 Catch:{ Exception -> 0x03f2 }
        r9 = 0;
    L_0x0022:
        r10 = r21.size();	 Catch:{ Exception -> 0x03f2 }
        if (r9 >= r10) goto L_0x0049;
    L_0x0028:
        r10 = r2.get(r9);	 Catch:{ Exception -> 0x03f2 }
        r10 = (java.lang.Integer) r10;	 Catch:{ Exception -> 0x03f2 }
        r10 = r10.intValue();	 Catch:{ Exception -> 0x03f2 }
        r10 = (long) r10;	 Catch:{ Exception -> 0x03f2 }
        r12 = (long) r3;	 Catch:{ Exception -> 0x03f2 }
        r14 = 32;
        r12 = r12 << r14;
        r10 = r10 | r12;
        r12 = r0.length();	 Catch:{ Exception -> 0x03f2 }
        if (r12 <= 0) goto L_0x0043;
    L_0x003e:
        r12 = 44;
        r0.append(r12);	 Catch:{ Exception -> 0x03f2 }
    L_0x0043:
        r0.append(r10);	 Catch:{ Exception -> 0x03f2 }
        r9 = r9 + 1;
        goto L_0x0022;
    L_0x0049:
        r0 = r0.toString();	 Catch:{ Exception -> 0x03f2 }
        goto L_0x0054;
    L_0x004e:
        r0 = ",";
        r0 = android.text.TextUtils.join(r0, r2);	 Catch:{ Exception -> 0x03f2 }
    L_0x0054:
        r9 = r0;
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03f2 }
        r10.<init>();	 Catch:{ Exception -> 0x03f2 }
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x03f2 }
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.getClientUserId();	 Catch:{ Exception -> 0x03f2 }
        r11 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x03f2 }
        r13 = "SELECT uid, data, read_state, out, mention, mid FROM messages WHERE mid IN(%s)";
        r14 = 1;
        r15 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x03f2 }
        r15[r8] = r9;	 Catch:{ Exception -> 0x03f2 }
        r12 = java.lang.String.format(r12, r13, r15);	 Catch:{ Exception -> 0x03f2 }
        r13 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x03f2 }
        r11 = r11.queryFinalized(r12, r13);	 Catch:{ Exception -> 0x03f2 }
    L_0x0079:
        r12 = 3;
        r13 = 2;
        r15 = r11.next();	 Catch:{ Exception -> 0x0198 }
        if (r15 == 0) goto L_0x0195;
    L_0x0081:
        r14 = r11.longValue(r8);	 Catch:{ Exception -> 0x0198 }
        r4 = 5;
        r4 = r11.intValue(r4);	 Catch:{ Exception -> 0x0198 }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0198 }
        r5.remove(r4);	 Catch:{ Exception -> 0x0198 }
        r17 = r9;
        r8 = (long) r0;
        r18 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1));
        if (r18 != 0) goto L_0x009d;
    L_0x0098:
        r9 = r17;
        r8 = 0;
        r14 = 1;
        goto L_0x0079;
    L_0x009d:
        r8 = r11.intValue(r13);	 Catch:{ Exception -> 0x0193 }
        r9 = r11.intValue(r12);	 Catch:{ Exception -> 0x0193 }
        if (r9 != 0) goto L_0x00ee;
    L_0x00a7:
        r9 = r7.get(r14);	 Catch:{ Exception -> 0x0193 }
        r9 = (java.lang.Integer[]) r9;	 Catch:{ Exception -> 0x0193 }
        if (r9 != 0) goto L_0x00c3;
    L_0x00af:
        r9 = new java.lang.Integer[r13];	 Catch:{ Exception -> 0x0193 }
        r4 = 0;
        r18 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0193 }
        r9[r4] = r18;	 Catch:{ Exception -> 0x0193 }
        r18 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0193 }
        r16 = 1;
        r9[r16] = r18;	 Catch:{ Exception -> 0x0193 }
        r7.put(r14, r9);	 Catch:{ Exception -> 0x0193 }
    L_0x00c3:
        if (r8 >= r13) goto L_0x00d7;
    L_0x00c5:
        r16 = 1;
        r18 = r9[r16];	 Catch:{ Exception -> 0x0193 }
        r18 = r9[r16];	 Catch:{ Exception -> 0x0193 }
        r18 = r18.intValue();	 Catch:{ Exception -> 0x0193 }
        r18 = r18 + 1;
        r18 = java.lang.Integer.valueOf(r18);	 Catch:{ Exception -> 0x0193 }
        r9[r16] = r18;	 Catch:{ Exception -> 0x0193 }
    L_0x00d7:
        if (r8 == 0) goto L_0x00db;
    L_0x00d9:
        if (r8 != r13) goto L_0x00ee;
    L_0x00db:
        r4 = 0;
        r8 = r9[r4];	 Catch:{ Exception -> 0x0193 }
        r8 = r9[r4];	 Catch:{ Exception -> 0x0193 }
        r8 = r8.intValue();	 Catch:{ Exception -> 0x0193 }
        r16 = 1;
        r8 = r8 + 1;
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0193 }
        r9[r4] = r8;	 Catch:{ Exception -> 0x0193 }
    L_0x00ee:
        r8 = (int) r14;	 Catch:{ Exception -> 0x0193 }
        if (r8 == 0) goto L_0x00f2;
    L_0x00f1:
        goto L_0x0098;
    L_0x00f2:
        r8 = 1;
        r9 = r11.byteBufferValue(r8);	 Catch:{ Exception -> 0x0193 }
        if (r9 == 0) goto L_0x0098;
    L_0x00f9:
        r4 = 0;
        r8 = r9.readInt32(r4);	 Catch:{ Exception -> 0x0193 }
        r8 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r9, r8, r4);	 Catch:{ Exception -> 0x0193 }
        r14 = r1.currentAccount;	 Catch:{ Exception -> 0x0193 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ Exception -> 0x0193 }
        r14 = r14.clientUserId;	 Catch:{ Exception -> 0x0193 }
        r8.readAttachPath(r9, r14);	 Catch:{ Exception -> 0x0193 }
        r9.reuse();	 Catch:{ Exception -> 0x0193 }
        if (r8 == 0) goto L_0x0098;
    L_0x0112:
        r9 = r8.media;	 Catch:{ Exception -> 0x0193 }
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0193 }
        if (r9 == 0) goto L_0x0147;
    L_0x0118:
        r9 = r8.media;	 Catch:{ Exception -> 0x0193 }
        r9 = r9.photo;	 Catch:{ Exception -> 0x0193 }
        r9 = r9.sizes;	 Catch:{ Exception -> 0x0193 }
        r9 = r9.size();	 Catch:{ Exception -> 0x0193 }
        r14 = 0;
    L_0x0123:
        if (r14 >= r9) goto L_0x0098;
    L_0x0125:
        r15 = r8.media;	 Catch:{ Exception -> 0x0193 }
        r15 = r15.photo;	 Catch:{ Exception -> 0x0193 }
        r15 = r15.sizes;	 Catch:{ Exception -> 0x0193 }
        r15 = r15.get(r14);	 Catch:{ Exception -> 0x0193 }
        r15 = (org.telegram.tgnet.TLRPC.PhotoSize) r15;	 Catch:{ Exception -> 0x0193 }
        r15 = org.telegram.messenger.FileLoader.getPathToAttach(r15);	 Catch:{ Exception -> 0x0193 }
        if (r15 == 0) goto L_0x0144;
    L_0x0137:
        r18 = r15.toString();	 Catch:{ Exception -> 0x0193 }
        r18 = r18.length();	 Catch:{ Exception -> 0x0193 }
        if (r18 <= 0) goto L_0x0144;
    L_0x0141:
        r10.add(r15);	 Catch:{ Exception -> 0x0193 }
    L_0x0144:
        r14 = r14 + 1;
        goto L_0x0123;
    L_0x0147:
        r9 = r8.media;	 Catch:{ Exception -> 0x0193 }
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0193 }
        if (r9 == 0) goto L_0x0098;
    L_0x014d:
        r9 = r8.media;	 Catch:{ Exception -> 0x0193 }
        r9 = r9.document;	 Catch:{ Exception -> 0x0193 }
        r9 = org.telegram.messenger.FileLoader.getPathToAttach(r9);	 Catch:{ Exception -> 0x0193 }
        if (r9 == 0) goto L_0x0164;
    L_0x0157:
        r14 = r9.toString();	 Catch:{ Exception -> 0x0193 }
        r14 = r14.length();	 Catch:{ Exception -> 0x0193 }
        if (r14 <= 0) goto L_0x0164;
    L_0x0161:
        r10.add(r9);	 Catch:{ Exception -> 0x0193 }
    L_0x0164:
        r9 = r8.media;	 Catch:{ Exception -> 0x0193 }
        r9 = r9.document;	 Catch:{ Exception -> 0x0193 }
        r9 = r9.thumbs;	 Catch:{ Exception -> 0x0193 }
        r9 = r9.size();	 Catch:{ Exception -> 0x0193 }
        r14 = 0;
    L_0x016f:
        if (r14 >= r9) goto L_0x0098;
    L_0x0171:
        r15 = r8.media;	 Catch:{ Exception -> 0x0193 }
        r15 = r15.document;	 Catch:{ Exception -> 0x0193 }
        r15 = r15.thumbs;	 Catch:{ Exception -> 0x0193 }
        r15 = r15.get(r14);	 Catch:{ Exception -> 0x0193 }
        r15 = (org.telegram.tgnet.TLRPC.PhotoSize) r15;	 Catch:{ Exception -> 0x0193 }
        r15 = org.telegram.messenger.FileLoader.getPathToAttach(r15);	 Catch:{ Exception -> 0x0193 }
        if (r15 == 0) goto L_0x0190;
    L_0x0183:
        r18 = r15.toString();	 Catch:{ Exception -> 0x0193 }
        r18 = r18.length();	 Catch:{ Exception -> 0x0193 }
        if (r18 <= 0) goto L_0x0190;
    L_0x018d:
        r10.add(r15);	 Catch:{ Exception -> 0x0193 }
    L_0x0190:
        r14 = r14 + 1;
        goto L_0x016f;
    L_0x0193:
        r0 = move-exception;
        goto L_0x019b;
    L_0x0195:
        r17 = r9;
        goto L_0x019e;
    L_0x0198:
        r0 = move-exception;
        r17 = r9;
    L_0x019b:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x03f2 }
    L_0x019e:
        r11.dispose();	 Catch:{ Exception -> 0x03f2 }
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x03f2 }
        r0 = org.telegram.messenger.FileLoader.getInstance(r0);	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r0.deleteFiles(r10, r4);	 Catch:{ Exception -> 0x03f2 }
        r0 = 0;
    L_0x01ac:
        r8 = r7.size();	 Catch:{ Exception -> 0x03f2 }
        if (r0 >= r8) goto L_0x0229;
    L_0x01b2:
        r8 = r7.keyAt(r0);	 Catch:{ Exception -> 0x03f2 }
        r10 = r7.valueAt(r0);	 Catch:{ Exception -> 0x03f2 }
        r10 = (java.lang.Integer[]) r10;	 Catch:{ Exception -> 0x03f2 }
        r11 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03f2 }
        r14.<init>();	 Catch:{ Exception -> 0x03f2 }
        r15 = "SELECT unread_count, unread_count_i FROM dialogs WHERE did = ";
        r14.append(r15);	 Catch:{ Exception -> 0x03f2 }
        r14.append(r8);	 Catch:{ Exception -> 0x03f2 }
        r14 = r14.toString();	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r15 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x03f2 }
        r11 = r11.queryFinalized(r14, r15);	 Catch:{ Exception -> 0x03f2 }
        r14 = r11.next();	 Catch:{ Exception -> 0x03f2 }
        if (r14 == 0) goto L_0x01e6;
    L_0x01dc:
        r14 = r11.intValue(r4);	 Catch:{ Exception -> 0x03f2 }
        r15 = 1;
        r18 = r11.intValue(r15);	 Catch:{ Exception -> 0x03f2 }
        goto L_0x01e9;
    L_0x01e6:
        r14 = 0;
        r18 = 0;
    L_0x01e9:
        r11.dispose();	 Catch:{ Exception -> 0x03f2 }
        r11 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x03f2 }
        r6.add(r11);	 Catch:{ Exception -> 0x03f2 }
        r11 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r15 = "UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?";
        r11 = r11.executeFast(r15);	 Catch:{ Exception -> 0x03f2 }
        r11.requery();	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r15 = r10[r4];	 Catch:{ Exception -> 0x03f2 }
        r15 = r15.intValue();	 Catch:{ Exception -> 0x03f2 }
        r14 = r14 - r15;
        r14 = java.lang.Math.max(r4, r14);	 Catch:{ Exception -> 0x03f2 }
        r15 = 1;
        r11.bindInteger(r15, r14);	 Catch:{ Exception -> 0x03f2 }
        r10 = r10[r15];	 Catch:{ Exception -> 0x03f2 }
        r10 = r10.intValue();	 Catch:{ Exception -> 0x03f2 }
        r10 = r18 - r10;
        r10 = java.lang.Math.max(r4, r10);	 Catch:{ Exception -> 0x03f2 }
        r11.bindInteger(r13, r10);	 Catch:{ Exception -> 0x03f2 }
        r11.bindLong(r12, r8);	 Catch:{ Exception -> 0x03f2 }
        r11.step();	 Catch:{ Exception -> 0x03f2 }
        r11.dispose();	 Catch:{ Exception -> 0x03f2 }
        r0 = r0 + 1;
        goto L_0x01ac;
    L_0x0229:
        r0 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03f2 }
        r8 = "DELETE FROM messages WHERE mid IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r10[r4] = r17;	 Catch:{ Exception -> 0x03f2 }
        r7 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.executeFast(r7);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03f2 }
        r0.dispose();	 Catch:{ Exception -> 0x03f2 }
        r0 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03f2 }
        r8 = "DELETE FROM polls WHERE mid IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r10[r4] = r17;	 Catch:{ Exception -> 0x03f2 }
        r7 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.executeFast(r7);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03f2 }
        r0.dispose();	 Catch:{ Exception -> 0x03f2 }
        r0 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03f2 }
        r8 = "DELETE FROM bot_keyboard WHERE mid IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r10[r4] = r17;	 Catch:{ Exception -> 0x03f2 }
        r7 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.executeFast(r7);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03f2 }
        r0.dispose();	 Catch:{ Exception -> 0x03f2 }
        r0 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03f2 }
        r8 = "DELETE FROM messages_seq WHERE mid IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r10[r4] = r17;	 Catch:{ Exception -> 0x03f2 }
        r7 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.executeFast(r7);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03f2 }
        r0.dispose();	 Catch:{ Exception -> 0x03f2 }
        r0 = r5.isEmpty();	 Catch:{ Exception -> 0x03f2 }
        if (r0 == 0) goto L_0x0399;
    L_0x029b:
        r0 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x03f2 }
        r5 = "SELECT uid, type FROM media_v2 WHERE mid IN(%s)";
        r7 = 1;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r8[r4] = r17;	 Catch:{ Exception -> 0x03f2 }
        r3 = java.lang.String.format(r3, r5, r8);	 Catch:{ Exception -> 0x03f2 }
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x03f2 }
        r3 = 0;
    L_0x02b2:
        r5 = r0.next();	 Catch:{ Exception -> 0x03f2 }
        if (r5 == 0) goto L_0x02fb;
    L_0x02b8:
        r7 = r0.longValue(r4);	 Catch:{ Exception -> 0x03f2 }
        r5 = 1;
        r9 = r0.intValue(r5);	 Catch:{ Exception -> 0x03f2 }
        if (r3 != 0) goto L_0x02c8;
    L_0x02c3:
        r3 = new android.util.SparseArray;	 Catch:{ Exception -> 0x03f2 }
        r3.<init>();	 Catch:{ Exception -> 0x03f2 }
    L_0x02c8:
        r5 = r3.get(r9);	 Catch:{ Exception -> 0x03f2 }
        r5 = (android.util.LongSparseArray) r5;	 Catch:{ Exception -> 0x03f2 }
        if (r5 != 0) goto L_0x02de;
    L_0x02d0:
        r5 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x03f2 }
        r5.<init>();	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r10 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x03f2 }
        r3.put(r9, r5);	 Catch:{ Exception -> 0x03f2 }
        goto L_0x02e5;
    L_0x02de:
        r9 = r5.get(r7);	 Catch:{ Exception -> 0x03f2 }
        r10 = r9;
        r10 = (java.lang.Integer) r10;	 Catch:{ Exception -> 0x03f2 }
    L_0x02e5:
        if (r10 != 0) goto L_0x02ec;
    L_0x02e7:
        r4 = 0;
        r10 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x03f2 }
    L_0x02ec:
        r9 = r10.intValue();	 Catch:{ Exception -> 0x03f2 }
        r10 = 1;
        r9 = r9 + r10;
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x03f2 }
        r5.put(r7, r9);	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        goto L_0x02b2;
    L_0x02fb:
        r0.dispose();	 Catch:{ Exception -> 0x03f2 }
        if (r3 == 0) goto L_0x03cb;
    L_0x0300:
        r0 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r5 = "REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)";
        r0 = r0.executeFast(r5);	 Catch:{ Exception -> 0x03f2 }
        r5 = 0;
    L_0x0309:
        r7 = r3.size();	 Catch:{ Exception -> 0x03f2 }
        if (r5 >= r7) goto L_0x0395;
    L_0x030f:
        r7 = r3.keyAt(r5);	 Catch:{ Exception -> 0x03f2 }
        r8 = r3.valueAt(r5);	 Catch:{ Exception -> 0x03f2 }
        r8 = (android.util.LongSparseArray) r8;	 Catch:{ Exception -> 0x03f2 }
        r9 = 0;
    L_0x031a:
        r10 = r8.size();	 Catch:{ Exception -> 0x03f2 }
        if (r9 >= r10) goto L_0x038d;
    L_0x0320:
        r10 = r8.keyAt(r9);	 Catch:{ Exception -> 0x03f2 }
        r14 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r15 = java.util.Locale.US;	 Catch:{ Exception -> 0x03f2 }
        r4 = "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1";
        r12 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x03f2 }
        r19 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x03f2 }
        r13 = 0;
        r12[r13] = r19;	 Catch:{ Exception -> 0x03f2 }
        r19 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x03f2 }
        r16 = 1;
        r12[r16] = r19;	 Catch:{ Exception -> 0x03f2 }
        r4 = java.lang.String.format(r15, r4, r12);	 Catch:{ Exception -> 0x03f2 }
        r12 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x03f2 }
        r12 = r14.queryFinalized(r4, r12);	 Catch:{ Exception -> 0x03f2 }
        r4 = r12.next();	 Catch:{ Exception -> 0x03f2 }
        r14 = -1;
        if (r4 == 0) goto L_0x0358;
    L_0x034c:
        r15 = r12.intValue(r13);	 Catch:{ Exception -> 0x03f2 }
        r13 = 1;
        r19 = r12.intValue(r13);	 Catch:{ Exception -> 0x03f2 }
        r13 = r19;
        goto L_0x035a;
    L_0x0358:
        r13 = 0;
        r15 = -1;
    L_0x035a:
        r12.dispose();	 Catch:{ Exception -> 0x03f2 }
        if (r15 == r14) goto L_0x0386;
    L_0x035f:
        r0.requery();	 Catch:{ Exception -> 0x03f2 }
        r12 = r8.valueAt(r9);	 Catch:{ Exception -> 0x03f2 }
        r12 = (java.lang.Integer) r12;	 Catch:{ Exception -> 0x03f2 }
        r12 = r12.intValue();	 Catch:{ Exception -> 0x03f2 }
        r15 = r15 - r12;
        r4 = 0;
        r12 = java.lang.Math.max(r4, r15);	 Catch:{ Exception -> 0x03f2 }
        r14 = 1;
        r0.bindLong(r14, r10);	 Catch:{ Exception -> 0x03f2 }
        r10 = 2;
        r0.bindInteger(r10, r7);	 Catch:{ Exception -> 0x03f2 }
        r11 = 3;
        r0.bindInteger(r11, r12);	 Catch:{ Exception -> 0x03f2 }
        r12 = 4;
        r0.bindInteger(r12, r13);	 Catch:{ Exception -> 0x03f2 }
        r0.step();	 Catch:{ Exception -> 0x03f2 }
        goto L_0x0388;
    L_0x0386:
        r10 = 2;
        r11 = 3;
    L_0x0388:
        r9 = r9 + 1;
        r12 = 3;
        r13 = 2;
        goto L_0x031a;
    L_0x038d:
        r10 = 2;
        r11 = 3;
        r5 = r5 + 1;
        r12 = 3;
        r13 = 2;
        goto L_0x0309;
    L_0x0395:
        r0.dispose();	 Catch:{ Exception -> 0x03f2 }
        goto L_0x03cb;
    L_0x0399:
        if (r3 != 0) goto L_0x03ab;
    L_0x039b:
        r0 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r3 = "UPDATE media_counts_v2 SET old = 1 WHERE 1";
        r0 = r0.executeFast(r3);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03f2 }
        r0.dispose();	 Catch:{ Exception -> 0x03f2 }
        goto L_0x03cb;
    L_0x03ab:
        r0 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x03f2 }
        r7 = "UPDATE media_counts_v2 SET old = 1 WHERE uid = %d";
        r8 = 1;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x03f2 }
        r3 = -r3;
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r9[r4] = r3;	 Catch:{ Exception -> 0x03f2 }
        r3 = java.lang.String.format(r5, r7, r9);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.executeFast(r3);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03f2 }
        r0.dispose();	 Catch:{ Exception -> 0x03f2 }
    L_0x03cb:
        r0 = r1.database;	 Catch:{ Exception -> 0x03f2 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x03f2 }
        r5 = "DELETE FROM media_v2 WHERE mid IN(%s)";
        r7 = 1;
        r7 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x03f2 }
        r4 = 0;
        r7[r4] = r17;	 Catch:{ Exception -> 0x03f2 }
        r3 = java.lang.String.format(r3, r5, r7);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.executeFast(r3);	 Catch:{ Exception -> 0x03f2 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03f2 }
        r0.dispose();	 Catch:{ Exception -> 0x03f2 }
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x03f2 }
        r0 = org.telegram.messenger.DataQuery.getInstance(r0);	 Catch:{ Exception -> 0x03f2 }
        r3 = 0;
        r0.clearBotKeyboard(r3, r2);	 Catch:{ Exception -> 0x03f2 }
        return r6;
    L_0x03f2:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r2 = 0;
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.markMessagesAsDeletedInternal(java.util.ArrayList, int):java.util.ArrayList");
    }

    public void updateDialogsWithDeletedMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, boolean z, int i) {
        if (!arrayList.isEmpty() || i != 0) {
            if (z) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$iK8A-SAdCdfVOMqr20ENyrWEKkc(this, arrayList, arrayList2, i));
            } else {
                updateDialogsWithDeletedMessagesInternal(arrayList, arrayList2, i);
            }
        }
    }

    public /* synthetic */ void lambda$updateDialogsWithDeletedMessages$129$MessagesStorage(ArrayList arrayList, ArrayList arrayList2, int i) {
        updateDialogsWithDeletedMessagesInternal(arrayList, arrayList2, i);
    }

    public ArrayList<Long> markMessagesAsDeleted(ArrayList<Integer> arrayList, boolean z, int i) {
        if (arrayList.isEmpty()) {
            return null;
        }
        if (!z) {
            return markMessagesAsDeletedInternal((ArrayList) arrayList, i);
        }
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Pxk4Wx5BriRS9OZob1_nCkjyIGM(this, arrayList, i));
        return null;
    }

    public /* synthetic */ void lambda$markMessagesAsDeleted$130$MessagesStorage(ArrayList arrayList, int i) {
        markMessagesAsDeletedInternal(arrayList, i);
    }

    private ArrayList<Long> markMessagesAsDeletedInternal(int i, int i2) {
        int i3 = i;
        try {
            ArrayList arrayList = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            long j = ((long) i2) | (((long) i3) << 32);
            ArrayList arrayList2 = new ArrayList();
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            SQLiteDatabase sQLiteDatabase = this.database;
            Object[] objArr = new Object[2];
            int i4 = 0;
            objArr[0] = Integer.valueOf(-i3);
            objArr[1] = Long.valueOf(j);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state, out, mention FROM messages WHERE uid = %d AND mid <= %d", objArr), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    long longValue = queryFinalized.longValue(i4);
                    if (longValue != ((long) clientUserId)) {
                        i3 = queryFinalized.intValue(2);
                        if (queryFinalized.intValue(3) == 0) {
                            Integer[] numArr = (Integer[]) longSparseArray.get(longValue);
                            if (numArr == null) {
                                numArr = new Integer[]{Integer.valueOf(0), Integer.valueOf(0)};
                                longSparseArray.put(longValue, numArr);
                            }
                            if (i3 < 2) {
                                Integer num = numArr[1];
                                numArr[1] = Integer.valueOf(numArr[1].intValue() + 1);
                            }
                            if (i3 == 0 || i3 == 2) {
                                Integer num2 = numArr[0];
                                numArr[0] = Integer.valueOf(numArr[0].intValue() + 1);
                            }
                        }
                        if (((int) longValue) == 0) {
                            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                            if (byteBufferValue != null) {
                                Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                                byteBufferValue.reuse();
                                if (TLdeserialize != null) {
                                    int i5;
                                    File pathToAttach;
                                    if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                                        i3 = TLdeserialize.media.photo.sizes.size();
                                        for (i5 = 0; i5 < i3; i5++) {
                                            pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.photo.sizes.get(i5));
                                            if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                                arrayList2.add(pathToAttach);
                                            }
                                        }
                                    } else if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                        File pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                        if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                            arrayList2.add(pathToAttach2);
                                        }
                                        i3 = TLdeserialize.media.document.thumbs.size();
                                        for (i5 = 0; i5 < i3; i5++) {
                                            pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.document.thumbs.get(i5));
                                            if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                                arrayList2.add(pathToAttach);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    i4 = 0;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            queryFinalized.dispose();
            FileLoader.getInstance(this.currentAccount).deleteFiles(arrayList2, 0);
            for (i3 = 0; i3 < longSparseArray.size(); i3++) {
                int intValue;
                long keyAt = longSparseArray.keyAt(i3);
                Integer[] numArr2 = (Integer[]) longSparseArray.valueAt(i3);
                SQLiteDatabase sQLiteDatabase2 = this.database;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT unread_count, unread_count_i FROM dialogs WHERE did = ");
                stringBuilder.append(keyAt);
                SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized(stringBuilder.toString(), new Object[0]);
                if (queryFinalized2.next()) {
                    intValue = queryFinalized2.intValue(0);
                    i4 = queryFinalized2.intValue(1);
                } else {
                    intValue = 0;
                    i4 = 0;
                }
                queryFinalized2.dispose();
                arrayList.add(Long.valueOf(keyAt));
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
                executeFast.requery();
                executeFast.bindInteger(1, Math.max(0, intValue - numArr2[0].intValue()));
                executeFast.bindInteger(2, Math.max(0, i4 - numArr2[1].intValue()));
                executeFast.bindLong(3, keyAt);
                executeFast.step();
                executeFast.dispose();
            }
            this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE uid = %d AND mid <= %d", new Object[]{Integer.valueOf(r13), Long.valueOf(j)})).stepThis().dispose();
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE uid = %d AND mid <= %d", new Object[]{Integer.valueOf(r13), Long.valueOf(j)})).stepThis().dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE media_counts_v2 SET old = 1 WHERE uid = %d", new Object[]{Integer.valueOf(r13)})).stepThis().dispose();
            return arrayList;
        } catch (Exception e2) {
            FileLog.e(e2);
            return null;
        }
    }

    public /* synthetic */ void lambda$markMessagesAsDeleted$131$MessagesStorage(int i, int i2) {
        markMessagesAsDeletedInternal(i, i2);
    }

    public ArrayList<Long> markMessagesAsDeleted(int i, int i2, boolean z) {
        if (!z) {
            return markMessagesAsDeletedInternal(i, i2);
        }
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$v05sVENuVPndVz0Y4GYR32FJFV4(this, i, i2));
        return null;
    }

    private void fixUnsupportedMedia(Message message) {
        if (message != null) {
            MessageMedia messageMedia = message.media;
            if (messageMedia instanceof TL_messageMediaUnsupported_old) {
                if (messageMedia.bytes.length == 0) {
                    messageMedia.bytes = new byte[1];
                    messageMedia.bytes[0] = (byte) 99;
                }
            } else if (messageMedia instanceof TL_messageMediaUnsupported) {
                message.media = new TL_messageMediaUnsupported_old();
                messageMedia = message.media;
                messageMedia.bytes = new byte[1];
                messageMedia.bytes[0] = (byte) 99;
                message.flags |= 512;
            }
        }
    }

    private void doneHolesInTable(String str, long j, int i) throws Exception {
        SQLiteDatabase sQLiteDatabase;
        String str2 = "DELETE FROM ";
        Locale locale;
        StringBuilder stringBuilder;
        if (i == 0) {
            sQLiteDatabase = this.database;
            locale = Locale.US;
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(str);
            stringBuilder.append(" WHERE uid = %d");
            sQLiteDatabase.executeFast(String.format(locale, stringBuilder.toString(), new Object[]{Long.valueOf(j)})).stepThis().dispose();
        } else {
            sQLiteDatabase = this.database;
            locale = Locale.US;
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(str);
            stringBuilder.append(" WHERE uid = %d AND start = 0");
            sQLiteDatabase.executeFast(String.format(locale, stringBuilder.toString(), new Object[]{Long.valueOf(j)})).stepThis().dispose();
        }
        sQLiteDatabase = this.database;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("REPLACE INTO ");
        stringBuilder2.append(str);
        stringBuilder2.append(" VALUES(?, ?, ?)");
        SQLitePreparedStatement executeFast = sQLiteDatabase.executeFast(stringBuilder2.toString());
        executeFast.requery();
        executeFast.bindLong(1, j);
        executeFast.bindInteger(2, 1);
        executeFast.bindInteger(3, 1);
        executeFast.step();
        executeFast.dispose();
    }

    public void doneHolesInMedia(long j, int i, int i2) throws Exception {
        String str = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)";
        int i3 = 0;
        SQLitePreparedStatement executeFast;
        if (i2 == -1) {
            if (i == 0) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            }
            executeFast = this.database.executeFast(str);
            while (i3 < 5) {
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, i3);
                executeFast.bindInteger(3, 1);
                executeFast.bindInteger(4, 1);
                executeFast.step();
                i3++;
            }
            executeFast.dispose();
            return;
        }
        if (i == 0) {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2)})).stepThis().dispose();
        } else {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", new Object[]{Long.valueOf(j), Integer.valueOf(i2)})).stepThis().dispose();
        }
        executeFast = this.database.executeFast(str);
        executeFast.requery();
        executeFast.bindLong(1, j);
        executeFast.bindInteger(2, i2);
        executeFast.bindInteger(3, 1);
        executeFast.bindInteger(4, 1);
        executeFast.step();
        executeFast.dispose();
    }

    public void closeHolesInMedia(long j, int i, int i2, int i3) {
        SQLiteCursor queryFinalized;
        int intValue;
        long j2 = j;
        int i4 = i;
        int i5 = i2;
        int i6 = 4;
        if (i3 < 0) {
            try {
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        ArrayList arrayList = null;
        while (queryFinalized.next()) {
            if (arrayList == null) {
                arrayList = new ArrayList();
            }
            intValue = queryFinalized.intValue(0);
            int intValue2 = queryFinalized.intValue(1);
            int intValue3 = queryFinalized.intValue(2);
            if (intValue2 != intValue3 || intValue2 != 1) {
                arrayList.add(new Hole(intValue, intValue2, intValue3));
            }
        }
        queryFinalized.dispose();
        if (arrayList != null) {
            intValue = 0;
            while (intValue < arrayList.size()) {
                Hole hole = (Hole) arrayList.get(intValue);
                String str = "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d";
                if (i5 >= hole.end - 1) {
                    if (i4 <= hole.start + 1) {
                        SQLiteDatabase sQLiteDatabase = this.database;
                        Locale locale = Locale.US;
                        Object[] objArr = new Object[i6];
                        objArr[0] = Long.valueOf(j);
                        objArr[1] = Integer.valueOf(hole.type);
                        objArr[2] = Integer.valueOf(hole.start);
                        objArr[3] = Integer.valueOf(hole.end);
                        sQLiteDatabase.executeFast(String.format(locale, str, objArr)).stepThis().dispose();
                        i6 = 4;
                        intValue++;
                    }
                }
                if (i5 >= hole.end - 1) {
                    if (hole.end != i4) {
                        try {
                            this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                    i6 = 4;
                    intValue++;
                } else if (i4 <= hole.start + 1) {
                    if (hole.start != i5) {
                        try {
                            this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        } catch (Exception e22) {
                            FileLog.e(e22);
                        }
                    }
                    i6 = 4;
                    intValue++;
                } else {
                    this.database.executeFast(String.format(Locale.US, str, new Object[]{Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                    executeFast.requery();
                    executeFast.bindLong(1, j2);
                    executeFast.bindInteger(2, hole.type);
                    executeFast.bindInteger(3, hole.start);
                    executeFast.bindInteger(4, i4);
                    executeFast.step();
                    executeFast.requery();
                    executeFast.bindLong(1, j2);
                    executeFast.bindInteger(2, hole.type);
                    executeFast.bindInteger(3, i5);
                    i6 = 4;
                    executeFast.bindInteger(4, hole.end);
                    executeFast.step();
                    executeFast.dispose();
                    intValue++;
                }
            }
        }
    }

    private void closeHolesInTable(String str, long j, int i, int i2) {
        String str2 = str;
        long j2 = j;
        int i3 = i;
        int i4 = i2;
        try {
            int intValue;
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT start, end FROM ");
            stringBuilder.append(str2);
            stringBuilder.append(" WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))");
            String stringBuilder2 = stringBuilder.toString();
            r9 = new Object[9];
            int i5 = 1;
            r9[1] = Integer.valueOf(i);
            r9[2] = Integer.valueOf(i2);
            r9[3] = Integer.valueOf(i);
            r9[4] = Integer.valueOf(i2);
            r9[5] = Integer.valueOf(i);
            r9[6] = Integer.valueOf(i2);
            r9[7] = Integer.valueOf(i);
            r9[8] = Integer.valueOf(i2);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, stringBuilder2, r9), new Object[0]);
            ArrayList arrayList = null;
            while (queryFinalized.next()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                intValue = queryFinalized.intValue(0);
                int intValue2 = queryFinalized.intValue(1);
                if (intValue != intValue2 || intValue != 1) {
                    arrayList.add(new Hole(intValue, intValue2));
                }
            }
            queryFinalized.dispose();
            if (arrayList != null) {
                intValue = 0;
                while (intValue < arrayList.size()) {
                    SQLiteDatabase sQLiteDatabase2;
                    Hole hole = (Hole) arrayList.get(intValue);
                    String str3 = " WHERE uid = %d AND start = %d AND end = %d";
                    String str4 = "DELETE FROM ";
                    if (i4 >= hole.end - i5) {
                        if (i3 <= hole.start + i5) {
                            sQLiteDatabase2 = this.database;
                            Locale locale2 = Locale.US;
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(str4);
                            stringBuilder3.append(str2);
                            stringBuilder3.append(str3);
                            sQLiteDatabase2.executeFast(String.format(locale2, stringBuilder3.toString(), new Object[]{Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                            intValue++;
                            i5 = 1;
                        }
                    }
                    String str5 = "UPDATE ";
                    Locale locale3;
                    StringBuilder stringBuilder4;
                    if (i4 >= hole.end - 1) {
                        if (hole.end != i3) {
                            try {
                                sQLiteDatabase2 = this.database;
                                locale3 = Locale.US;
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(str5);
                                stringBuilder4.append(str2);
                                stringBuilder4.append(" SET end = %d WHERE uid = %d AND start = %d AND end = %d");
                                sQLiteDatabase2.executeFast(String.format(locale3, stringBuilder4.toString(), new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                        intValue++;
                        i5 = 1;
                    } else if (i3 <= hole.start + 1) {
                        if (hole.start != i4) {
                            try {
                                sQLiteDatabase2 = this.database;
                                locale3 = Locale.US;
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(str5);
                                stringBuilder4.append(str2);
                                stringBuilder4.append(" SET start = %d WHERE uid = %d AND start = %d AND end = %d");
                                sQLiteDatabase2.executeFast(String.format(locale3, stringBuilder4.toString(), new Object[]{Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                            } catch (Exception e2) {
                                FileLog.e(e2);
                            }
                        }
                        intValue++;
                        i5 = 1;
                    } else {
                        sQLiteDatabase2 = this.database;
                        Locale locale4 = Locale.US;
                        stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(str4);
                        stringBuilder4.append(str2);
                        stringBuilder4.append(str3);
                        sQLiteDatabase2.executeFast(String.format(locale4, stringBuilder4.toString(), new Object[]{Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        sQLiteDatabase2 = this.database;
                        StringBuilder stringBuilder5 = new StringBuilder();
                        stringBuilder5.append("REPLACE INTO ");
                        stringBuilder5.append(str2);
                        stringBuilder5.append(" VALUES(?, ?, ?)");
                        SQLitePreparedStatement executeFast = sQLiteDatabase2.executeFast(stringBuilder5.toString());
                        executeFast.requery();
                        executeFast.bindLong(1, j2);
                        executeFast.bindInteger(2, hole.start);
                        executeFast.bindInteger(3, i3);
                        executeFast.step();
                        executeFast.requery();
                        executeFast.bindLong(1, j2);
                        executeFast.bindInteger(2, i4);
                        executeFast.bindInteger(3, hole.end);
                        executeFast.step();
                        executeFast.dispose();
                        intValue++;
                        i5 = 1;
                    }
                }
            }
        } catch (Exception e22) {
            FileLog.e(e22);
        }
    }

    public void replaceMessageIfExists(Message message, int i, ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z) {
        if (message != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$7WTSHIQPfcUMYxwWAQonwMJHQII(this, message, z, arrayList, arrayList2, i));
        }
    }

    /* JADX WARNING: Missing block: B:13:0x003c, code skipped:
            if (r5 != null) goto L_0x003e;
     */
    /* JADX WARNING: Missing block: B:14:0x003e, code skipped:
            r5.dispose();
     */
    /* JADX WARNING: Missing block: B:19:0x0049, code skipped:
            if (r5 == null) goto L_0x004c;
     */
    /* JADX WARNING: Missing block: B:21:?, code skipped:
            r1.database.beginTransaction();
            r0 = r1.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
            r5 = r1.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
     */
    /* JADX WARNING: Missing block: B:22:0x0067, code skipped:
            if (r4.dialog_id != 0) goto L_0x006c;
     */
    /* JADX WARNING: Missing block: B:23:0x0069, code skipped:
            org.telegram.messenger.MessageObject.getDialogId(r16);
     */
    /* JADX WARNING: Missing block: B:24:0x006c, code skipped:
            fixUnsupportedMedia(r16);
            r0.requery();
            r8 = new org.telegram.tgnet.NativeByteBuffer(r16.getObjectSize());
            r4.serializeToStream(r8);
            r0.bindLong(1, r2);
            r0.bindLong(2, r4.dialog_id);
            r0.bindInteger(3, org.telegram.messenger.MessageObject.getUnreadFlags(r16));
            r0.bindInteger(4, r4.send_state);
            r0.bindInteger(5, r4.date);
            r0.bindByteBuffer(6, r8);
     */
    /* JADX WARNING: Missing block: B:25:0x00a4, code skipped:
            if (org.telegram.messenger.MessageObject.isOut(r16) == false) goto L_0x00a8;
     */
    /* JADX WARNING: Missing block: B:26:0x00a6, code skipped:
            r14 = 1;
     */
    /* JADX WARNING: Missing block: B:27:0x00a8, code skipped:
            r14 = 0;
     */
    /* JADX WARNING: Missing block: B:28:0x00a9, code skipped:
            r0.bindInteger(7, r14);
            r0.bindInteger(8, r4.ttl);
     */
    /* JADX WARNING: Missing block: B:29:0x00b9, code skipped:
            if ((r4.flags & 1024) == 0) goto L_0x00c1;
     */
    /* JADX WARNING: Missing block: B:30:0x00bb, code skipped:
            r0.bindInteger(9, r4.views);
     */
    /* JADX WARNING: Missing block: B:31:0x00c1, code skipped:
            r0.bindInteger(9, getMessageMediaType(r16));
     */
    /* JADX WARNING: Missing block: B:32:0x00c8, code skipped:
            r0.bindInteger(10, 0);
     */
    /* JADX WARNING: Missing block: B:33:0x00d1, code skipped:
            if (r4.mentioned == false) goto L_0x00d5;
     */
    /* JADX WARNING: Missing block: B:34:0x00d3, code skipped:
            r14 = 1;
     */
    /* JADX WARNING: Missing block: B:35:0x00d5, code skipped:
            r14 = 0;
     */
    /* JADX WARNING: Missing block: B:36:0x00d6, code skipped:
            r0.bindInteger(11, r14);
            r0.step();
     */
    /* JADX WARNING: Missing block: B:37:0x00e0, code skipped:
            if (org.telegram.messenger.DataQuery.canAddMessageToMedia(r16) == false) goto L_0x00ff;
     */
    /* JADX WARNING: Missing block: B:38:0x00e2, code skipped:
            r5.requery();
            r5.bindLong(1, r2);
            r5.bindLong(2, r4.dialog_id);
            r5.bindInteger(3, r4.date);
            r5.bindInteger(4, org.telegram.messenger.DataQuery.getMediaType(r16));
            r5.bindByteBuffer(5, r8);
            r5.step();
     */
    /* JADX WARNING: Missing block: B:39:0x00ff, code skipped:
            r8.reuse();
            r0.dispose();
            r5.dispose();
            r1.database.commitTransaction();
     */
    /* JADX WARNING: Missing block: B:40:0x010d, code skipped:
            if (r17 == false) goto L_0x0177;
     */
    /* JADX WARNING: Missing block: B:41:0x010f, code skipped:
            r5 = new java.util.HashMap();
            r0 = new java.util.HashMap();
            r2 = 0;
     */
    /* JADX WARNING: Missing block: B:43:0x011e, code skipped:
            if (r2 >= r18.size()) goto L_0x0134;
     */
    /* JADX WARNING: Missing block: B:44:0x0120, code skipped:
            r7 = (org.telegram.tgnet.TLRPC.User) r18.get(r2);
            r5.put(java.lang.Integer.valueOf(r7.id), r7);
            r2 = r2 + 1;
     */
    /* JADX WARNING: Missing block: B:46:0x0138, code skipped:
            if (r6 >= r19.size()) goto L_0x014e;
     */
    /* JADX WARNING: Missing block: B:47:0x013a, code skipped:
            r3 = (org.telegram.tgnet.TLRPC.Chat) r19.get(r6);
            r0.put(java.lang.Integer.valueOf(r3.id), r3);
            r6 = r6 + 1;
     */
    /* JADX WARNING: Missing block: B:48:0x014e, code skipped:
            r2 = new org.telegram.messenger.MessageObject(r20, r16, r5, r0, true);
            r0 = new java.util.ArrayList();
            r0.add(r2);
            org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.-$$Lambda$MessagesStorage$EEZ7jUcGCXJQOkKomd0Qq_16PT8(r20, r2, r0));
     */
    public /* synthetic */ void lambda$replaceMessageIfExists$133$MessagesStorage(org.telegram.tgnet.TLRPC.Message r16, boolean r17, java.util.ArrayList r18, java.util.ArrayList r19, int r20) {
        /*
        r15 = this;
        r1 = r15;
        r4 = r16;
        r0 = r4.id;	 Catch:{ Exception -> 0x0173 }
        r2 = (long) r0;	 Catch:{ Exception -> 0x0173 }
        r0 = r4.to_id;	 Catch:{ Exception -> 0x0173 }
        r0 = r0.channel_id;	 Catch:{ Exception -> 0x0173 }
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0173 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0173 }
        if (r5 == 0) goto L_0x0015;
    L_0x0010:
        r5 = (long) r0;
        r0 = 32;
        r5 = r5 << r0;
        r2 = r2 | r5;
    L_0x0015:
        r5 = 0;
        r6 = 0;
        r7 = 1;
        r0 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0045 }
        r9 = "SELECT uid FROM messages WHERE mid = %d LIMIT 1";
        r10 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0045 }
        r11 = java.lang.Long.valueOf(r2);	 Catch:{ Exception -> 0x0045 }
        r10[r6] = r11;	 Catch:{ Exception -> 0x0045 }
        r8 = java.lang.String.format(r8, r9, r10);	 Catch:{ Exception -> 0x0045 }
        r9 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0045 }
        r5 = r0.queryFinalized(r8, r9);	 Catch:{ Exception -> 0x0045 }
        r0 = r5.next();	 Catch:{ Exception -> 0x0045 }
        if (r0 != 0) goto L_0x003c;
    L_0x0036:
        if (r5 == 0) goto L_0x003b;
    L_0x0038:
        r5.dispose();	 Catch:{ Exception -> 0x0173 }
    L_0x003b:
        return;
    L_0x003c:
        if (r5 == 0) goto L_0x004c;
    L_0x003e:
        r5.dispose();	 Catch:{ Exception -> 0x0173 }
        goto L_0x004c;
    L_0x0042:
        r0 = move-exception;
        goto L_0x016d;
    L_0x0045:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0042 }
        if (r5 == 0) goto L_0x004c;
    L_0x004b:
        goto L_0x003e;
    L_0x004c:
        r0 = r1.database;	 Catch:{ Exception -> 0x0173 }
        r0.beginTransaction();	 Catch:{ Exception -> 0x0173 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0173 }
        r5 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r0 = r0.executeFast(r5);	 Catch:{ Exception -> 0x0173 }
        r5 = r1.database;	 Catch:{ Exception -> 0x0173 }
        r8 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r5 = r5.executeFast(r8);	 Catch:{ Exception -> 0x0173 }
        r8 = r4.dialog_id;	 Catch:{ Exception -> 0x0173 }
        r10 = 0;
        r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x006c;
    L_0x0069:
        org.telegram.messenger.MessageObject.getDialogId(r16);	 Catch:{ Exception -> 0x0173 }
    L_0x006c:
        r15.fixUnsupportedMedia(r16);	 Catch:{ Exception -> 0x0173 }
        r0.requery();	 Catch:{ Exception -> 0x0173 }
        r8 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0173 }
        r9 = r16.getObjectSize();	 Catch:{ Exception -> 0x0173 }
        r8.<init>(r9);	 Catch:{ Exception -> 0x0173 }
        r4.serializeToStream(r8);	 Catch:{ Exception -> 0x0173 }
        r0.bindLong(r7, r2);	 Catch:{ Exception -> 0x0173 }
        r9 = r4.dialog_id;	 Catch:{ Exception -> 0x0173 }
        r11 = 2;
        r0.bindLong(r11, r9);	 Catch:{ Exception -> 0x0173 }
        r9 = org.telegram.messenger.MessageObject.getUnreadFlags(r16);	 Catch:{ Exception -> 0x0173 }
        r10 = 3;
        r0.bindInteger(r10, r9);	 Catch:{ Exception -> 0x0173 }
        r9 = r4.send_state;	 Catch:{ Exception -> 0x0173 }
        r12 = 4;
        r0.bindInteger(r12, r9);	 Catch:{ Exception -> 0x0173 }
        r9 = r4.date;	 Catch:{ Exception -> 0x0173 }
        r13 = 5;
        r0.bindInteger(r13, r9);	 Catch:{ Exception -> 0x0173 }
        r9 = 6;
        r0.bindByteBuffer(r9, r8);	 Catch:{ Exception -> 0x0173 }
        r9 = 7;
        r14 = org.telegram.messenger.MessageObject.isOut(r16);	 Catch:{ Exception -> 0x0173 }
        if (r14 == 0) goto L_0x00a8;
    L_0x00a6:
        r14 = 1;
        goto L_0x00a9;
    L_0x00a8:
        r14 = 0;
    L_0x00a9:
        r0.bindInteger(r9, r14);	 Catch:{ Exception -> 0x0173 }
        r9 = 8;
        r14 = r4.ttl;	 Catch:{ Exception -> 0x0173 }
        r0.bindInteger(r9, r14);	 Catch:{ Exception -> 0x0173 }
        r9 = r4.flags;	 Catch:{ Exception -> 0x0173 }
        r9 = r9 & 1024;
        r14 = 9;
        if (r9 == 0) goto L_0x00c1;
    L_0x00bb:
        r9 = r4.views;	 Catch:{ Exception -> 0x0173 }
        r0.bindInteger(r14, r9);	 Catch:{ Exception -> 0x0173 }
        goto L_0x00c8;
    L_0x00c1:
        r9 = r15.getMessageMediaType(r16);	 Catch:{ Exception -> 0x0173 }
        r0.bindInteger(r14, r9);	 Catch:{ Exception -> 0x0173 }
    L_0x00c8:
        r9 = 10;
        r0.bindInteger(r9, r6);	 Catch:{ Exception -> 0x0173 }
        r9 = 11;
        r14 = r4.mentioned;	 Catch:{ Exception -> 0x0173 }
        if (r14 == 0) goto L_0x00d5;
    L_0x00d3:
        r14 = 1;
        goto L_0x00d6;
    L_0x00d5:
        r14 = 0;
    L_0x00d6:
        r0.bindInteger(r9, r14);	 Catch:{ Exception -> 0x0173 }
        r0.step();	 Catch:{ Exception -> 0x0173 }
        r9 = org.telegram.messenger.DataQuery.canAddMessageToMedia(r16);	 Catch:{ Exception -> 0x0173 }
        if (r9 == 0) goto L_0x00ff;
    L_0x00e2:
        r5.requery();	 Catch:{ Exception -> 0x0173 }
        r5.bindLong(r7, r2);	 Catch:{ Exception -> 0x0173 }
        r2 = r4.dialog_id;	 Catch:{ Exception -> 0x0173 }
        r5.bindLong(r11, r2);	 Catch:{ Exception -> 0x0173 }
        r2 = r4.date;	 Catch:{ Exception -> 0x0173 }
        r5.bindInteger(r10, r2);	 Catch:{ Exception -> 0x0173 }
        r2 = org.telegram.messenger.DataQuery.getMediaType(r16);	 Catch:{ Exception -> 0x0173 }
        r5.bindInteger(r12, r2);	 Catch:{ Exception -> 0x0173 }
        r5.bindByteBuffer(r13, r8);	 Catch:{ Exception -> 0x0173 }
        r5.step();	 Catch:{ Exception -> 0x0173 }
    L_0x00ff:
        r8.reuse();	 Catch:{ Exception -> 0x0173 }
        r0.dispose();	 Catch:{ Exception -> 0x0173 }
        r5.dispose();	 Catch:{ Exception -> 0x0173 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0173 }
        r0.commitTransaction();	 Catch:{ Exception -> 0x0173 }
        if (r17 == 0) goto L_0x0177;
    L_0x010f:
        r5 = new java.util.HashMap;	 Catch:{ Exception -> 0x0173 }
        r5.<init>();	 Catch:{ Exception -> 0x0173 }
        r0 = new java.util.HashMap;	 Catch:{ Exception -> 0x0173 }
        r0.<init>();	 Catch:{ Exception -> 0x0173 }
        r2 = 0;
    L_0x011a:
        r3 = r18.size();	 Catch:{ Exception -> 0x0173 }
        if (r2 >= r3) goto L_0x0134;
    L_0x0120:
        r3 = r18;
        r7 = r3.get(r2);	 Catch:{ Exception -> 0x0173 }
        r7 = (org.telegram.tgnet.TLRPC.User) r7;	 Catch:{ Exception -> 0x0173 }
        r8 = r7.id;	 Catch:{ Exception -> 0x0173 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0173 }
        r5.put(r8, r7);	 Catch:{ Exception -> 0x0173 }
        r2 = r2 + 1;
        goto L_0x011a;
    L_0x0134:
        r2 = r19.size();	 Catch:{ Exception -> 0x0173 }
        if (r6 >= r2) goto L_0x014e;
    L_0x013a:
        r2 = r19;
        r3 = r2.get(r6);	 Catch:{ Exception -> 0x0173 }
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;	 Catch:{ Exception -> 0x0173 }
        r7 = r3.id;	 Catch:{ Exception -> 0x0173 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0173 }
        r0.put(r7, r3);	 Catch:{ Exception -> 0x0173 }
        r6 = r6 + 1;
        goto L_0x0134;
    L_0x014e:
        r8 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x0173 }
        r7 = 1;
        r2 = r8;
        r3 = r20;
        r4 = r16;
        r6 = r0;
        r2.<init>(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0173 }
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0173 }
        r0.<init>();	 Catch:{ Exception -> 0x0173 }
        r0.add(r8);	 Catch:{ Exception -> 0x0173 }
        r2 = new org.telegram.messenger.-$$Lambda$MessagesStorage$EEZ7jUcGCXJQOkKomd0Qq_16PT8;	 Catch:{ Exception -> 0x0173 }
        r3 = r20;
        r2.<init>(r3, r8, r0);	 Catch:{ Exception -> 0x0173 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x0173 }
        goto L_0x0177;
    L_0x016d:
        if (r5 == 0) goto L_0x0172;
    L_0x016f:
        r5.dispose();	 Catch:{ Exception -> 0x0173 }
    L_0x0172:
        throw r0;	 Catch:{ Exception -> 0x0173 }
    L_0x0173:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0177:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$replaceMessageIfExists$133$MessagesStorage(org.telegram.tgnet.TLRPC$Message, boolean, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    public void putMessages(messages_Messages messages_messages, long j, int i, int i2, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$3R9cvfcpxQrdzImhd--RA4e3lWg(this, messages_messages, i, j, i2, z));
    }

    /* JADX WARNING: Removed duplicated region for block: B:67:0x01ce A:{Catch:{ Exception -> 0x0450 }} */
    public /* synthetic */ void lambda$putMessages$134$MessagesStorage(org.telegram.tgnet.TLRPC.messages_Messages r30, int r31, long r32, int r34, boolean r35) {
        /*
        r29 = this;
        r7 = r29;
        r0 = r30;
        r8 = r31;
        r9 = r32;
        r11 = r34;
        r1 = r0.messages;	 Catch:{ Exception -> 0x0450 }
        r1 = r1.isEmpty();	 Catch:{ Exception -> 0x0450 }
        if (r1 == 0) goto L_0x001e;
    L_0x0012:
        if (r8 != 0) goto L_0x001d;
    L_0x0014:
        r0 = "messages_holes";
        r7.doneHolesInTable(r0, r9, r11);	 Catch:{ Exception -> 0x0450 }
        r0 = -1;
        r7.doneHolesInMedia(r9, r11, r0);	 Catch:{ Exception -> 0x0450 }
    L_0x001d:
        return;
    L_0x001e:
        r1 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r1.beginTransaction();	 Catch:{ Exception -> 0x0450 }
        r14 = 3;
        r15 = 2;
        r6 = 1;
        r5 = 0;
        if (r8 != 0) goto L_0x0059;
    L_0x0029:
        r1 = r0.messages;	 Catch:{ Exception -> 0x0450 }
        r2 = r0.messages;	 Catch:{ Exception -> 0x0450 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0450 }
        r2 = r2 - r6;
        r1 = r1.get(r2);	 Catch:{ Exception -> 0x0450 }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x0450 }
        r3 = r1.id;	 Catch:{ Exception -> 0x0450 }
        r2 = "messages_holes";
        r1 = r29;
        r16 = r3;
        r3 = r32;
        r12 = 0;
        r5 = r16;
        r13 = 1;
        r6 = r34;
        r1.closeHolesInTable(r2, r3, r5, r6);	 Catch:{ Exception -> 0x0450 }
        r6 = -1;
        r1 = r29;
        r2 = r32;
        r4 = r16;
        r5 = r34;
        r1.closeHolesInMedia(r2, r4, r5, r6);	 Catch:{ Exception -> 0x0450 }
        goto L_0x00c6;
    L_0x0059:
        r12 = 0;
        r13 = 1;
        if (r8 != r13) goto L_0x0081;
    L_0x005d:
        r1 = r0.messages;	 Catch:{ Exception -> 0x0450 }
        r1 = r1.get(r12);	 Catch:{ Exception -> 0x0450 }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x0450 }
        r6 = r1.id;	 Catch:{ Exception -> 0x0450 }
        r2 = "messages_holes";
        r1 = r29;
        r3 = r32;
        r5 = r34;
        r16 = r6;
        r1.closeHolesInTable(r2, r3, r5, r6);	 Catch:{ Exception -> 0x0450 }
        r6 = -1;
        r1 = r29;
        r2 = r32;
        r4 = r34;
        r5 = r16;
        r1.closeHolesInMedia(r2, r4, r5, r6);	 Catch:{ Exception -> 0x0450 }
        goto L_0x00c6;
    L_0x0081:
        if (r8 == r14) goto L_0x0089;
    L_0x0083:
        if (r8 == r15) goto L_0x0089;
    L_0x0085:
        r1 = 4;
        if (r8 != r1) goto L_0x00c6;
    L_0x0088:
        goto L_0x008a;
    L_0x0089:
        r1 = 4;
    L_0x008a:
        if (r11 != 0) goto L_0x0092;
    L_0x008c:
        if (r8 == r1) goto L_0x0092;
    L_0x008e:
        r11 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        goto L_0x009d;
    L_0x0092:
        r1 = r0.messages;	 Catch:{ Exception -> 0x0450 }
        r1 = r1.get(r12);	 Catch:{ Exception -> 0x0450 }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x0450 }
        r1 = r1.id;	 Catch:{ Exception -> 0x0450 }
        r11 = r1;
    L_0x009d:
        r1 = r0.messages;	 Catch:{ Exception -> 0x0450 }
        r2 = r0.messages;	 Catch:{ Exception -> 0x0450 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0450 }
        r2 = r2 - r13;
        r1 = r1.get(r2);	 Catch:{ Exception -> 0x0450 }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x0450 }
        r6 = r1.id;	 Catch:{ Exception -> 0x0450 }
        r2 = "messages_holes";
        r1 = r29;
        r3 = r32;
        r5 = r6;
        r16 = r6;
        r6 = r11;
        r1.closeHolesInTable(r2, r3, r5, r6);	 Catch:{ Exception -> 0x0450 }
        r6 = -1;
        r1 = r29;
        r2 = r32;
        r4 = r16;
        r5 = r11;
        r1.closeHolesInMedia(r2, r4, r5, r6);	 Catch:{ Exception -> 0x0450 }
    L_0x00c6:
        r1 = r0.messages;	 Catch:{ Exception -> 0x0450 }
        r1 = r1.size();	 Catch:{ Exception -> 0x0450 }
        r2 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r3 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x0450 }
        r3 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r4 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r3 = r3.executeFast(r4);	 Catch:{ Exception -> 0x0450 }
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r11 = 0;
        r17 = 0;
        r18 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x00e5:
        if (r5 >= r1) goto L_0x03ca;
    L_0x00e7:
        r15 = r0.messages;	 Catch:{ Exception -> 0x0450 }
        r15 = r15.get(r5);	 Catch:{ Exception -> 0x0450 }
        r15 = (org.telegram.tgnet.TLRPC.Message) r15;	 Catch:{ Exception -> 0x0450 }
        r14 = r15.id;	 Catch:{ Exception -> 0x0450 }
        r12 = (long) r14;	 Catch:{ Exception -> 0x0450 }
        if (r4 != 0) goto L_0x00f8;
    L_0x00f4:
        r4 = r15.to_id;	 Catch:{ Exception -> 0x0450 }
        r4 = r4.channel_id;	 Catch:{ Exception -> 0x0450 }
    L_0x00f8:
        r14 = r15.to_id;	 Catch:{ Exception -> 0x0450 }
        r14 = r14.channel_id;	 Catch:{ Exception -> 0x0450 }
        if (r14 == 0) goto L_0x0108;
    L_0x00fe:
        r14 = r1;
        r19 = r2;
        r1 = (long) r4;	 Catch:{ Exception -> 0x0450 }
        r20 = 32;
        r1 = r1 << r20;
        r12 = r12 | r1;
        goto L_0x010b;
    L_0x0108:
        r14 = r1;
        r19 = r2;
    L_0x010b:
        r1 = -2;
        if (r8 != r1) goto L_0x01d7;
    L_0x010e:
        r1 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x0450 }
        r21 = r4;
        r4 = "SELECT mid, data, ttl, mention, read_state, send_state FROM messages WHERE mid = %d";
        r22 = r14;
        r14 = 1;
        r8 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0450 }
        r14 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0450 }
        r23 = r6;
        r6 = 0;
        r8[r6] = r14;	 Catch:{ Exception -> 0x0450 }
        r2 = java.lang.String.format(r2, r4, r8);	 Catch:{ Exception -> 0x0450 }
        r4 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0450 }
        r1 = r1.queryFinalized(r2, r4);	 Catch:{ Exception -> 0x0450 }
        r2 = r1.next();	 Catch:{ Exception -> 0x0450 }
        if (r2 == 0) goto L_0x01c5;
    L_0x0134:
        r4 = 1;
        r8 = r1.byteBufferValue(r4);	 Catch:{ Exception -> 0x0450 }
        if (r8 == 0) goto L_0x0166;
    L_0x013b:
        r4 = r8.readInt32(r6);	 Catch:{ Exception -> 0x0450 }
        r4 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r8, r4, r6);	 Catch:{ Exception -> 0x0450 }
        r6 = r7.currentAccount;	 Catch:{ Exception -> 0x0450 }
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);	 Catch:{ Exception -> 0x0450 }
        r6 = r6.clientUserId;	 Catch:{ Exception -> 0x0450 }
        r4.readAttachPath(r8, r6);	 Catch:{ Exception -> 0x0450 }
        r8.reuse();	 Catch:{ Exception -> 0x0450 }
        r6 = 5;
        r8 = r1.intValue(r6);	 Catch:{ Exception -> 0x0450 }
        if (r4 == 0) goto L_0x0166;
    L_0x0158:
        r6 = 3;
        if (r8 == r6) goto L_0x0166;
    L_0x015b:
        r4 = r4.attachPath;	 Catch:{ Exception -> 0x0450 }
        r15.attachPath = r4;	 Catch:{ Exception -> 0x0450 }
        r4 = 2;
        r6 = r1.intValue(r4);	 Catch:{ Exception -> 0x0450 }
        r15.ttl = r6;	 Catch:{ Exception -> 0x0450 }
    L_0x0166:
        r4 = 3;
        r6 = r1.intValue(r4);	 Catch:{ Exception -> 0x0450 }
        if (r6 == 0) goto L_0x016f;
    L_0x016d:
        r4 = 1;
        goto L_0x0170;
    L_0x016f:
        r4 = 0;
    L_0x0170:
        r6 = 4;
        r8 = r1.intValue(r6);	 Catch:{ Exception -> 0x0450 }
        r6 = r15.mentioned;	 Catch:{ Exception -> 0x0450 }
        if (r4 == r6) goto L_0x01c5;
    L_0x0179:
        r6 = r18;
        r14 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r6 != r14) goto L_0x01b0;
    L_0x0180:
        r14 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r18 = r6;
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0450 }
        r6.<init>();	 Catch:{ Exception -> 0x0450 }
        r24 = r11;
        r11 = "SELECT unread_count_i FROM dialogs WHERE did = ";
        r6.append(r11);	 Catch:{ Exception -> 0x0450 }
        r6.append(r9);	 Catch:{ Exception -> 0x0450 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0450 }
        r25 = r3;
        r11 = 0;
        r3 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0450 }
        r3 = r14.queryFinalized(r6, r3);	 Catch:{ Exception -> 0x0450 }
        r6 = r3.next();	 Catch:{ Exception -> 0x0450 }
        if (r6 == 0) goto L_0x01ac;
    L_0x01a6:
        r6 = r3.intValue(r11);	 Catch:{ Exception -> 0x0450 }
        r18 = r6;
    L_0x01ac:
        r3.dispose();	 Catch:{ Exception -> 0x0450 }
        goto L_0x01b6;
    L_0x01b0:
        r25 = r3;
        r18 = r6;
        r24 = r11;
    L_0x01b6:
        if (r4 == 0) goto L_0x01be;
    L_0x01b8:
        r3 = 1;
        if (r8 > r3) goto L_0x01c9;
    L_0x01bb:
        r18 = r18 + -1;
        goto L_0x01c9;
    L_0x01be:
        r3 = r15.media_unread;	 Catch:{ Exception -> 0x0450 }
        if (r3 == 0) goto L_0x01c9;
    L_0x01c2:
        r18 = r18 + 1;
        goto L_0x01c9;
    L_0x01c5:
        r25 = r3;
        r24 = r11;
    L_0x01c9:
        r1.dispose();	 Catch:{ Exception -> 0x0450 }
        if (r2 != 0) goto L_0x01e1;
    L_0x01ce:
        r2 = r19;
        r11 = r24;
        r3 = r25;
        r6 = 3;
        goto L_0x03b8;
    L_0x01d7:
        r25 = r3;
        r21 = r4;
        r23 = r6;
        r24 = r11;
        r22 = r14;
    L_0x01e1:
        r6 = 7;
        r8 = 6;
        if (r5 != 0) goto L_0x02a6;
    L_0x01e5:
        if (r35 == 0) goto L_0x02a6;
    L_0x01e7:
        r11 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0450 }
        r14.<init>();	 Catch:{ Exception -> 0x0450 }
        r1 = "SELECT pinned, unread_count_i, flags FROM dialogs WHERE did = ";
        r14.append(r1);	 Catch:{ Exception -> 0x0450 }
        r14.append(r9);	 Catch:{ Exception -> 0x0450 }
        r1 = r14.toString();	 Catch:{ Exception -> 0x0450 }
        r14 = 0;
        r2 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0450 }
        r1 = r11.queryFinalized(r1, r2);	 Catch:{ Exception -> 0x0450 }
        r2 = r1.next();	 Catch:{ Exception -> 0x0450 }
        if (r2 == 0) goto L_0x021b;
    L_0x0207:
        r11 = r1.intValue(r14);	 Catch:{ Exception -> 0x0450 }
        r14 = 1;
        r26 = r1.intValue(r14);	 Catch:{ Exception -> 0x0450 }
        r14 = 2;
        r27 = r1.intValue(r14);	 Catch:{ Exception -> 0x0450 }
        r14 = r11;
        r11 = r26;
        r28 = r27;
        goto L_0x021f;
    L_0x021b:
        r11 = 0;
        r14 = 0;
        r28 = 0;
    L_0x021f:
        r1.dispose();	 Catch:{ Exception -> 0x0450 }
        if (r2 == 0) goto L_0x024f;
    L_0x0224:
        r1 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r2 = "UPDATE dialogs SET date = ?, last_mid = ?, inbox_max = ?, last_mid_i = ?, pts = ?, date_i = ? WHERE did = ?";
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x0450 }
        r2 = r15.date;	 Catch:{ Exception -> 0x0450 }
        r11 = 1;
        r1.bindInteger(r11, r2);	 Catch:{ Exception -> 0x0450 }
        r2 = 2;
        r1.bindLong(r2, r12);	 Catch:{ Exception -> 0x0450 }
        r2 = r15.id;	 Catch:{ Exception -> 0x0450 }
        r11 = 3;
        r1.bindInteger(r11, r2);	 Catch:{ Exception -> 0x0450 }
        r2 = 4;
        r1.bindLong(r2, r12);	 Catch:{ Exception -> 0x0450 }
        r2 = r0.pts;	 Catch:{ Exception -> 0x0450 }
        r11 = 5;
        r1.bindInteger(r11, r2);	 Catch:{ Exception -> 0x0450 }
        r2 = r15.date;	 Catch:{ Exception -> 0x0450 }
        r1.bindInteger(r8, r2);	 Catch:{ Exception -> 0x0450 }
        r1.bindLong(r6, r9);	 Catch:{ Exception -> 0x0450 }
        goto L_0x02a0;
    L_0x024f:
        r1 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r2 = "REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x0450 }
        r2 = 1;
        r1.bindLong(r2, r9);	 Catch:{ Exception -> 0x0450 }
        r2 = r15.date;	 Catch:{ Exception -> 0x0450 }
        r4 = 2;
        r1.bindInteger(r4, r2);	 Catch:{ Exception -> 0x0450 }
        r2 = 3;
        r4 = 0;
        r1.bindInteger(r2, r4);	 Catch:{ Exception -> 0x0450 }
        r2 = 4;
        r1.bindLong(r2, r12);	 Catch:{ Exception -> 0x0450 }
        r2 = r15.id;	 Catch:{ Exception -> 0x0450 }
        r3 = 5;
        r1.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0450 }
        r1.bindInteger(r8, r4);	 Catch:{ Exception -> 0x0450 }
        r1.bindLong(r6, r12);	 Catch:{ Exception -> 0x0450 }
        r2 = 8;
        r1.bindInteger(r2, r11);	 Catch:{ Exception -> 0x0450 }
        r2 = r0.pts;	 Catch:{ Exception -> 0x0450 }
        r3 = 9;
        r1.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0450 }
        r2 = r15.date;	 Catch:{ Exception -> 0x0450 }
        r3 = 10;
        r1.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0450 }
        r2 = 11;
        r1.bindInteger(r2, r14);	 Catch:{ Exception -> 0x0450 }
        r2 = 12;
        r3 = r28;
        r1.bindInteger(r2, r3);	 Catch:{ Exception -> 0x0450 }
        r2 = 13;
        r3 = 0;
        r1.bindInteger(r2, r3);	 Catch:{ Exception -> 0x0450 }
        r2 = 14;
        r1.bindNull(r2);	 Catch:{ Exception -> 0x0450 }
    L_0x02a0:
        r1.step();	 Catch:{ Exception -> 0x0450 }
        r1.dispose();	 Catch:{ Exception -> 0x0450 }
    L_0x02a6:
        r7.fixUnsupportedMedia(r15);	 Catch:{ Exception -> 0x0450 }
        r19.requery();	 Catch:{ Exception -> 0x0450 }
        r1 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0450 }
        r2 = r15.getObjectSize();	 Catch:{ Exception -> 0x0450 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0450 }
        r15.serializeToStream(r1);	 Catch:{ Exception -> 0x0450 }
        r2 = r19;
        r3 = 1;
        r2.bindLong(r3, r12);	 Catch:{ Exception -> 0x0450 }
        r3 = 2;
        r2.bindLong(r3, r9);	 Catch:{ Exception -> 0x0450 }
        r3 = org.telegram.messenger.MessageObject.getUnreadFlags(r15);	 Catch:{ Exception -> 0x0450 }
        r4 = 3;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x0450 }
        r3 = r15.send_state;	 Catch:{ Exception -> 0x0450 }
        r4 = 4;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x0450 }
        r3 = r15.date;	 Catch:{ Exception -> 0x0450 }
        r4 = 5;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x0450 }
        r2.bindByteBuffer(r8, r1);	 Catch:{ Exception -> 0x0450 }
        r3 = org.telegram.messenger.MessageObject.isOut(r15);	 Catch:{ Exception -> 0x0450 }
        if (r3 == 0) goto L_0x02e1;
    L_0x02df:
        r3 = 1;
        goto L_0x02e2;
    L_0x02e1:
        r3 = 0;
    L_0x02e2:
        r2.bindInteger(r6, r3);	 Catch:{ Exception -> 0x0450 }
        r3 = r15.ttl;	 Catch:{ Exception -> 0x0450 }
        r4 = 8;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x0450 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0450 }
        r3 = r3 & 1024;
        if (r3 == 0) goto L_0x02fa;
    L_0x02f2:
        r3 = r15.views;	 Catch:{ Exception -> 0x0450 }
        r4 = 9;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x0450 }
        goto L_0x0303;
    L_0x02fa:
        r4 = 9;
        r3 = r7.getMessageMediaType(r15);	 Catch:{ Exception -> 0x0450 }
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x0450 }
    L_0x0303:
        r3 = 10;
        r4 = 0;
        r2.bindInteger(r3, r4);	 Catch:{ Exception -> 0x0450 }
        r3 = r15.mentioned;	 Catch:{ Exception -> 0x0450 }
        if (r3 == 0) goto L_0x030f;
    L_0x030d:
        r3 = 1;
        goto L_0x0310;
    L_0x030f:
        r3 = 0;
    L_0x0310:
        r4 = 11;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x0450 }
        r2.step();	 Catch:{ Exception -> 0x0450 }
        r3 = org.telegram.messenger.DataQuery.canAddMessageToMedia(r15);	 Catch:{ Exception -> 0x0450 }
        if (r3 == 0) goto L_0x0341;
    L_0x031e:
        r25.requery();	 Catch:{ Exception -> 0x0450 }
        r3 = r25;
        r4 = 1;
        r3.bindLong(r4, r12);	 Catch:{ Exception -> 0x0450 }
        r4 = 2;
        r3.bindLong(r4, r9);	 Catch:{ Exception -> 0x0450 }
        r4 = r15.date;	 Catch:{ Exception -> 0x0450 }
        r6 = 3;
        r3.bindInteger(r6, r4);	 Catch:{ Exception -> 0x0450 }
        r4 = org.telegram.messenger.DataQuery.getMediaType(r15);	 Catch:{ Exception -> 0x0450 }
        r8 = 4;
        r3.bindInteger(r8, r4);	 Catch:{ Exception -> 0x0450 }
        r4 = 5;
        r3.bindByteBuffer(r4, r1);	 Catch:{ Exception -> 0x0450 }
        r3.step();	 Catch:{ Exception -> 0x0450 }
        goto L_0x0345;
    L_0x0341:
        r3 = r25;
        r6 = 3;
        r8 = 4;
    L_0x0345:
        r1.reuse();	 Catch:{ Exception -> 0x0450 }
        r1 = r15.media;	 Catch:{ Exception -> 0x0450 }
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;	 Catch:{ Exception -> 0x0450 }
        if (r1 == 0) goto L_0x0374;
    L_0x034e:
        if (r24 != 0) goto L_0x0359;
    L_0x0350:
        r1 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r4 = "REPLACE INTO polls VALUES(?, ?)";
        r11 = r1.executeFast(r4);	 Catch:{ Exception -> 0x0450 }
        goto L_0x035b;
    L_0x0359:
        r11 = r24;
    L_0x035b:
        r1 = r15.media;	 Catch:{ Exception -> 0x0450 }
        r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r1;	 Catch:{ Exception -> 0x0450 }
        r11.requery();	 Catch:{ Exception -> 0x0450 }
        r4 = 1;
        r11.bindLong(r4, r12);	 Catch:{ Exception -> 0x0450 }
        r1 = r1.poll;	 Catch:{ Exception -> 0x0450 }
        r12 = r1.id;	 Catch:{ Exception -> 0x0450 }
        r1 = 2;
        r11.bindLong(r1, r12);	 Catch:{ Exception -> 0x0450 }
        r11.step();	 Catch:{ Exception -> 0x0450 }
        r24 = r11;
        goto L_0x039d;
    L_0x0374:
        r1 = r15.media;	 Catch:{ Exception -> 0x0450 }
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0450 }
        if (r1 == 0) goto L_0x039d;
    L_0x037a:
        if (r23 != 0) goto L_0x0385;
    L_0x037c:
        r1 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r4 = "REPLACE INTO webpage_pending VALUES(?, ?)";
        r1 = r1.executeFast(r4);	 Catch:{ Exception -> 0x0450 }
        goto L_0x0387;
    L_0x0385:
        r1 = r23;
    L_0x0387:
        r1.requery();	 Catch:{ Exception -> 0x0450 }
        r4 = r15.media;	 Catch:{ Exception -> 0x0450 }
        r4 = r4.webpage;	 Catch:{ Exception -> 0x0450 }
        r8 = r4.id;	 Catch:{ Exception -> 0x0450 }
        r4 = 1;
        r1.bindLong(r4, r8);	 Catch:{ Exception -> 0x0450 }
        r4 = 2;
        r1.bindLong(r4, r12);	 Catch:{ Exception -> 0x0450 }
        r1.step();	 Catch:{ Exception -> 0x0450 }
        r23 = r1;
    L_0x039d:
        if (r31 != 0) goto L_0x03b2;
    L_0x039f:
        r1 = r7.isValidKeyboardToSave(r15);	 Catch:{ Exception -> 0x0450 }
        if (r1 == 0) goto L_0x03b2;
    L_0x03a5:
        r1 = r17;
        if (r1 == 0) goto L_0x03af;
    L_0x03a9:
        r4 = r1.id;	 Catch:{ Exception -> 0x0450 }
        r8 = r15.id;	 Catch:{ Exception -> 0x0450 }
        if (r4 >= r8) goto L_0x03b4;
    L_0x03af:
        r17 = r15;
        goto L_0x03b6;
    L_0x03b2:
        r1 = r17;
    L_0x03b4:
        r17 = r1;
    L_0x03b6:
        r11 = r24;
    L_0x03b8:
        r5 = r5 + 1;
        r8 = r31;
        r9 = r32;
        r4 = r21;
        r1 = r22;
        r6 = r23;
        r12 = 0;
        r13 = 1;
        r14 = 3;
        r15 = 2;
        goto L_0x00e5;
    L_0x03ca:
        r23 = r6;
        r24 = r11;
        r1 = r17;
        r2.dispose();	 Catch:{ Exception -> 0x0450 }
        r3.dispose();	 Catch:{ Exception -> 0x0450 }
        if (r23 == 0) goto L_0x03db;
    L_0x03d8:
        r23.dispose();	 Catch:{ Exception -> 0x0450 }
    L_0x03db:
        if (r24 == 0) goto L_0x03e0;
    L_0x03dd:
        r24.dispose();	 Catch:{ Exception -> 0x0450 }
    L_0x03e0:
        if (r1 == 0) goto L_0x03ee;
    L_0x03e2:
        r2 = r7.currentAccount;	 Catch:{ Exception -> 0x0450 }
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);	 Catch:{ Exception -> 0x0450 }
        r5 = r32;
        r2.putBotKeyboard(r5, r1);	 Catch:{ Exception -> 0x0450 }
        goto L_0x03f0;
    L_0x03ee:
        r5 = r32;
    L_0x03f0:
        r1 = r0.users;	 Catch:{ Exception -> 0x0450 }
        r7.putUsersInternal(r1);	 Catch:{ Exception -> 0x0450 }
        r0 = r0.chats;	 Catch:{ Exception -> 0x0450 }
        r7.putChatsInternal(r0);	 Catch:{ Exception -> 0x0450 }
        r0 = r18;
        r1 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r0 == r1) goto L_0x043e;
    L_0x0401:
        r1 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x0450 }
        r3 = "UPDATE dialogs SET unread_count_i = %d WHERE did = %d";
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0450 }
        r9 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0450 }
        r10 = 0;
        r8[r10] = r9;	 Catch:{ Exception -> 0x0450 }
        r9 = java.lang.Long.valueOf(r32);	 Catch:{ Exception -> 0x0450 }
        r10 = 1;
        r8[r10] = r9;	 Catch:{ Exception -> 0x0450 }
        r2 = java.lang.String.format(r2, r3, r8);	 Catch:{ Exception -> 0x0450 }
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x0450 }
        r1 = r1.stepThis();	 Catch:{ Exception -> 0x0450 }
        r1.dispose();	 Catch:{ Exception -> 0x0450 }
        r1 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0450 }
        r2 = 1;
        r1.<init>(r2);	 Catch:{ Exception -> 0x0450 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0450 }
        r1.put(r5, r0);	 Catch:{ Exception -> 0x0450 }
        r0 = r7.currentAccount;	 Catch:{ Exception -> 0x0450 }
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);	 Catch:{ Exception -> 0x0450 }
        r2 = 0;
        r0.processDialogsUpdateRead(r2, r1);	 Catch:{ Exception -> 0x0450 }
    L_0x043e:
        r0 = r7.database;	 Catch:{ Exception -> 0x0450 }
        r0.commitTransaction();	 Catch:{ Exception -> 0x0450 }
        if (r35 == 0) goto L_0x0454;
    L_0x0445:
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0450 }
        r0.<init>();	 Catch:{ Exception -> 0x0450 }
        r1 = 0;
        r2 = 0;
        r7.updateDialogsWithDeletedMessages(r0, r1, r2, r4);	 Catch:{ Exception -> 0x0450 }
        goto L_0x0454;
    L_0x0450:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0454:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$134$MessagesStorage(org.telegram.tgnet.TLRPC$messages_Messages, int, long, int, boolean):void");
    }

    public static void addUsersAndChatsFromMessage(Message message, ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2) {
        int i;
        int i2;
        int i3 = message.from_id;
        if (i3 != 0) {
            if (i3 > 0) {
                if (!arrayList.contains(Integer.valueOf(i3))) {
                    arrayList.add(Integer.valueOf(message.from_id));
                }
            } else if (!arrayList2.contains(Integer.valueOf(-i3))) {
                arrayList2.add(Integer.valueOf(-message.from_id));
            }
        }
        i3 = message.via_bot_id;
        if (!(i3 == 0 || arrayList.contains(Integer.valueOf(i3)))) {
            arrayList.add(Integer.valueOf(message.via_bot_id));
        }
        MessageAction messageAction = message.action;
        if (messageAction != null) {
            i3 = messageAction.user_id;
            if (!(i3 == 0 || arrayList.contains(Integer.valueOf(i3)))) {
                arrayList.add(Integer.valueOf(message.action.user_id));
            }
            i3 = message.action.channel_id;
            if (!(i3 == 0 || arrayList2.contains(Integer.valueOf(i3)))) {
                arrayList2.add(Integer.valueOf(message.action.channel_id));
            }
            i3 = message.action.chat_id;
            if (!(i3 == 0 || arrayList2.contains(Integer.valueOf(i3)))) {
                arrayList2.add(Integer.valueOf(message.action.chat_id));
            }
            if (!message.action.users.isEmpty()) {
                for (i3 = 0; i3 < message.action.users.size(); i3++) {
                    Integer num = (Integer) message.action.users.get(i3);
                    if (!arrayList.contains(num)) {
                        arrayList.add(num);
                    }
                }
            }
        }
        if (!message.entities.isEmpty()) {
            for (i = 0; i < message.entities.size(); i++) {
                MessageEntity messageEntity = (MessageEntity) message.entities.get(i);
                if (messageEntity instanceof TL_messageEntityMentionName) {
                    arrayList.add(Integer.valueOf(((TL_messageEntityMentionName) messageEntity).user_id));
                } else if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                    arrayList.add(Integer.valueOf(((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id));
                }
            }
        }
        MessageMedia messageMedia = message.media;
        if (messageMedia != null) {
            i3 = messageMedia.user_id;
            if (!(i3 == 0 || arrayList.contains(Integer.valueOf(i3)))) {
                arrayList.add(Integer.valueOf(message.media.user_id));
            }
        }
        MessageFwdHeader messageFwdHeader = message.fwd_from;
        if (messageFwdHeader != null) {
            i3 = messageFwdHeader.from_id;
            if (!(i3 == 0 || arrayList.contains(Integer.valueOf(i3)))) {
                arrayList.add(Integer.valueOf(message.fwd_from.from_id));
            }
            i3 = message.fwd_from.channel_id;
            if (!(i3 == 0 || arrayList2.contains(Integer.valueOf(i3)))) {
                arrayList2.add(Integer.valueOf(message.fwd_from.channel_id));
            }
            Peer peer = message.fwd_from.saved_from_peer;
            if (peer != null) {
                i = peer.user_id;
                if (i == 0) {
                    i2 = peer.channel_id;
                    if (i2 == 0) {
                        i2 = peer.chat_id;
                        if (!(i2 == 0 || arrayList2.contains(Integer.valueOf(i2)))) {
                            arrayList2.add(Integer.valueOf(message.fwd_from.saved_from_peer.chat_id));
                        }
                    } else if (!arrayList2.contains(Integer.valueOf(i2))) {
                        arrayList2.add(Integer.valueOf(message.fwd_from.saved_from_peer.channel_id));
                    }
                } else if (!arrayList2.contains(Integer.valueOf(i))) {
                    arrayList.add(Integer.valueOf(message.fwd_from.saved_from_peer.user_id));
                }
            }
        }
        i2 = message.ttl;
        if (i2 < 0 && !arrayList2.contains(Integer.valueOf(-i2))) {
            arrayList2.add(Integer.valueOf(-message.ttl));
        }
    }

    public void getDialogs(int i, int i2, int i3) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$0JY8_kHD_Z7G1jlg1xDEw7zjjU0(this, i, i2, i3));
    }

    /* JADX WARNING: Removed duplicated region for block: B:128:0x02a7 A:{Catch:{ Exception -> 0x03b4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x026b A:{Catch:{ Exception -> 0x03b4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x013a A:{Catch:{ Exception -> 0x02c1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0138 A:{Catch:{ Exception -> 0x02c1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0151 A:{Catch:{ Exception -> 0x02c1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x014f A:{Catch:{ Exception -> 0x02c1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0168 A:{SYNTHETIC, Splitter:B:54:0x0168} */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x018f A:{Catch:{ Exception -> 0x02c1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x026b A:{Catch:{ Exception -> 0x03b4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02a7 A:{Catch:{ Exception -> 0x03b4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02a7 A:{Catch:{ Exception -> 0x03b4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x026b A:{Catch:{ Exception -> 0x03b4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x026b A:{Catch:{ Exception -> 0x03b4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02a7 A:{Catch:{ Exception -> 0x03b4 }} */
    public /* synthetic */ void lambda$getDialogs$135$MessagesStorage(int r20, int r21, int r22) {
        /*
        r19 = this;
        r1 = r19;
        r12 = new org.telegram.tgnet.TLRPC$TL_messages_dialogs;
        r12.<init>();
        r13 = new java.util.ArrayList;
        r13.<init>();
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03ba }
        r2.<init>();	 Catch:{ Exception -> 0x03ba }
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x03ba }
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);	 Catch:{ Exception -> 0x03ba }
        r0 = r0.getClientUserId();	 Catch:{ Exception -> 0x03ba }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x03ba }
        r2.add(r0);	 Catch:{ Exception -> 0x03ba }
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03ba }
        r3.<init>();	 Catch:{ Exception -> 0x03ba }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03ba }
        r4.<init>();	 Catch:{ Exception -> 0x03ba }
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03ba }
        r5.<init>();	 Catch:{ Exception -> 0x03ba }
        r6 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x03ba }
        r6.<init>();	 Catch:{ Exception -> 0x03ba }
        r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03ba }
        r8 = 2;
        r7.<init>(r8);	 Catch:{ Exception -> 0x03ba }
        r0 = java.lang.Integer.valueOf(r20);	 Catch:{ Exception -> 0x03ba }
        r7.add(r0);	 Catch:{ Exception -> 0x03ba }
        r10 = 0;
    L_0x0044:
        r0 = r7.size();	 Catch:{ Exception -> 0x03ba }
        r14 = 3;
        if (r10 >= r0) goto L_0x02dd;
    L_0x004b:
        r0 = r7.get(r10);	 Catch:{ Exception -> 0x02d6 }
        r0 = (java.lang.Integer) r0;	 Catch:{ Exception -> 0x02d6 }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x02d6 }
        if (r10 != 0) goto L_0x005c;
    L_0x0057:
        r16 = r21;
        r17 = r22;
        goto L_0x0062;
    L_0x005c:
        r16 = 50;
        r16 = 0;
        r17 = 50;
    L_0x0062:
        r11 = r1.database;	 Catch:{ Exception -> 0x02d6 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x02d6 }
        r15 = "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, s.flags, m.date, d.pts, d.inbox_max, d.outbox_max, m.replydata, d.pinned, d.unread_count_i, d.flags, d.folder_id, d.data FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.folder_id = %d ORDER BY d.pinned DESC, d.date DESC LIMIT %d,%d";
        r9 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x02d6 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x02d6 }
        r18 = 0;
        r9[r18] = r0;	 Catch:{ Exception -> 0x02d6 }
        r0 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x02d6 }
        r16 = 1;
        r9[r16] = r0;	 Catch:{ Exception -> 0x02d6 }
        r0 = java.lang.Integer.valueOf(r17);	 Catch:{ Exception -> 0x02d6 }
        r16 = 2;
        r9[r16] = r0;	 Catch:{ Exception -> 0x02d6 }
        r0 = java.lang.String.format(r8, r15, r9);	 Catch:{ Exception -> 0x02d6 }
        r8 = 0;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x02d6 }
        r9 = r11.queryFinalized(r0, r9);	 Catch:{ Exception -> 0x02d6 }
    L_0x008d:
        r0 = r9.next();	 Catch:{ Exception -> 0x02d6 }
        if (r0 == 0) goto L_0x02c6;
    L_0x0093:
        r14 = r9.longValue(r8);	 Catch:{ Exception -> 0x02d6 }
        r0 = org.telegram.messenger.DialogObject.isFolderDialogId(r14);	 Catch:{ Exception -> 0x02d6 }
        if (r0 == 0) goto L_0x00e6;
    L_0x009d:
        r0 = new org.telegram.tgnet.TLRPC$TL_dialogFolder;	 Catch:{ Exception -> 0x03ba }
        r0.<init>();	 Catch:{ Exception -> 0x03ba }
        r8 = 18;
        r16 = r9.isNull(r8);	 Catch:{ Exception -> 0x03ba }
        if (r16 != 0) goto L_0x00d0;
    L_0x00aa:
        r8 = r9.byteBufferValue(r8);	 Catch:{ Exception -> 0x03ba }
        if (r8 == 0) goto L_0x00be;
    L_0x00b0:
        r17 = r13;
        r11 = 0;
        r13 = r8.readInt32(r11);	 Catch:{ Exception -> 0x00e0 }
        r13 = org.telegram.tgnet.TLRPC.TL_folder.TLdeserialize(r8, r13, r11);	 Catch:{ Exception -> 0x00e0 }
        r0.folder = r13;	 Catch:{ Exception -> 0x00e0 }
        goto L_0x00cc;
    L_0x00be:
        r17 = r13;
        r11 = new org.telegram.tgnet.TLRPC$TL_folder;	 Catch:{ Exception -> 0x00e0 }
        r11.<init>();	 Catch:{ Exception -> 0x00e0 }
        r0.folder = r11;	 Catch:{ Exception -> 0x00e0 }
        r11 = r0.folder;	 Catch:{ Exception -> 0x00e0 }
        r13 = (int) r14;	 Catch:{ Exception -> 0x00e0 }
        r11.id = r13;	 Catch:{ Exception -> 0x00e0 }
    L_0x00cc:
        r8.reuse();	 Catch:{ Exception -> 0x00e0 }
        goto L_0x00d2;
    L_0x00d0:
        r17 = r13;
    L_0x00d2:
        if (r10 != 0) goto L_0x00ed;
    L_0x00d4:
        r8 = r0.folder;	 Catch:{ Exception -> 0x00e0 }
        r8 = r8.id;	 Catch:{ Exception -> 0x00e0 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x00e0 }
        r7.add(r8);	 Catch:{ Exception -> 0x00e0 }
        goto L_0x00ed;
    L_0x00e0:
        r0 = move-exception;
        r13 = r12;
        r14 = r17;
        goto L_0x03bd;
    L_0x00e6:
        r17 = r13;
        r0 = new org.telegram.tgnet.TLRPC$TL_dialog;	 Catch:{ Exception -> 0x02c1 }
        r0.<init>();	 Catch:{ Exception -> 0x02c1 }
    L_0x00ed:
        r8 = r0;
        r8.id = r14;	 Catch:{ Exception -> 0x02c1 }
        r11 = 1;
        r0 = r9.intValue(r11);	 Catch:{ Exception -> 0x02c1 }
        r8.top_message = r0;	 Catch:{ Exception -> 0x02c1 }
        r11 = 2;
        r0 = r9.intValue(r11);	 Catch:{ Exception -> 0x02c1 }
        r8.unread_count = r0;	 Catch:{ Exception -> 0x02c1 }
        r11 = 3;
        r0 = r9.intValue(r11);	 Catch:{ Exception -> 0x02c1 }
        r8.last_message_date = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = 10;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        r8.pts = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = r8.pts;	 Catch:{ Exception -> 0x02c1 }
        if (r0 == 0) goto L_0x0119;
    L_0x0111:
        r13 = r8.id;	 Catch:{ Exception -> 0x00e0 }
        r0 = (int) r13;
        if (r0 <= 0) goto L_0x0117;
    L_0x0116:
        goto L_0x0119;
    L_0x0117:
        r0 = 1;
        goto L_0x011a;
    L_0x0119:
        r0 = 0;
    L_0x011a:
        r8.flags = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = 11;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        r8.read_inbox_max_id = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = 12;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        r8.read_outbox_max_id = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = 14;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        r8.pinnedNum = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = r8.pinnedNum;	 Catch:{ Exception -> 0x02c1 }
        if (r0 == 0) goto L_0x013a;
    L_0x0138:
        r0 = 1;
        goto L_0x013b;
    L_0x013a:
        r0 = 0;
    L_0x013b:
        r8.pinned = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = 15;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        r8.unread_mentions_count = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = 16;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        r13 = 1;
        r0 = r0 & r13;
        if (r0 == 0) goto L_0x0151;
    L_0x014f:
        r0 = 1;
        goto L_0x0152;
    L_0x0151:
        r0 = 0;
    L_0x0152:
        r8.unread_mark = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = 8;
        r13 = r9.longValue(r0);	 Catch:{ Exception -> 0x02c1 }
        r0 = (int) r13;	 Catch:{ Exception -> 0x02c1 }
        r15 = new org.telegram.tgnet.TLRPC$TL_peerNotifySettings;	 Catch:{ Exception -> 0x02c1 }
        r15.<init>();	 Catch:{ Exception -> 0x02c1 }
        r8.notify_settings = r15;	 Catch:{ Exception -> 0x02c1 }
        r15 = 1;
        r0 = r0 & r15;
        r15 = 32;
        if (r0 == 0) goto L_0x017b;
    L_0x0168:
        r0 = r8.notify_settings;	 Catch:{ Exception -> 0x00e0 }
        r13 = r13 >> r15;
        r14 = (int) r13;	 Catch:{ Exception -> 0x00e0 }
        r0.mute_until = r14;	 Catch:{ Exception -> 0x00e0 }
        r0 = r8.notify_settings;	 Catch:{ Exception -> 0x00e0 }
        r0 = r0.mute_until;	 Catch:{ Exception -> 0x00e0 }
        if (r0 != 0) goto L_0x017b;
    L_0x0174:
        r0 = r8.notify_settings;	 Catch:{ Exception -> 0x00e0 }
        r13 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0.mute_until = r13;	 Catch:{ Exception -> 0x00e0 }
    L_0x017b:
        r0 = 17;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        r8.folder_id = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = r12.dialogs;	 Catch:{ Exception -> 0x02c1 }
        r0.add(r8);	 Catch:{ Exception -> 0x02c1 }
        r0 = 4;
        r0 = r9.byteBufferValue(r0);	 Catch:{ Exception -> 0x02c1 }
        if (r0 == 0) goto L_0x0261;
    L_0x018f:
        r13 = 0;
        r14 = r0.readInt32(r13);	 Catch:{ Exception -> 0x02c1 }
        r14 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r14, r13);	 Catch:{ Exception -> 0x02c1 }
        r13 = r1.currentAccount;	 Catch:{ Exception -> 0x02c1 }
        r13 = org.telegram.messenger.UserConfig.getInstance(r13);	 Catch:{ Exception -> 0x02c1 }
        r13 = r13.clientUserId;	 Catch:{ Exception -> 0x02c1 }
        r14.readAttachPath(r0, r13);	 Catch:{ Exception -> 0x02c1 }
        r0.reuse();	 Catch:{ Exception -> 0x02c1 }
        if (r14 == 0) goto L_0x0261;
    L_0x01a8:
        r0 = 5;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        org.telegram.messenger.MessageObject.setUnreadFlags(r14, r0);	 Catch:{ Exception -> 0x02c1 }
        r0 = 6;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        r14.id = r0;	 Catch:{ Exception -> 0x02c1 }
        r0 = 9;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        if (r0 == 0) goto L_0x01c1;
    L_0x01bf:
        r8.last_message_date = r0;	 Catch:{ Exception -> 0x00e0 }
    L_0x01c1:
        r0 = 7;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02c1 }
        r14.send_state = r0;	 Catch:{ Exception -> 0x02c1 }
        r13 = r12;
        r11 = r8.id;	 Catch:{ Exception -> 0x03b4 }
        r14.dialog_id = r11;	 Catch:{ Exception -> 0x03b4 }
        r12 = r13;
        r0 = r12.messages;	 Catch:{ Exception -> 0x02c1 }
        r0.add(r14);	 Catch:{ Exception -> 0x02c1 }
        addUsersAndChatsFromMessage(r14, r2, r3);	 Catch:{ Exception -> 0x02c1 }
        r0 = r14.reply_to_msg_id;	 Catch:{ Exception -> 0x025b }
        if (r0 == 0) goto L_0x0261;
    L_0x01da:
        r0 = r14.action;	 Catch:{ Exception -> 0x025b }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;	 Catch:{ Exception -> 0x025b }
        if (r0 != 0) goto L_0x01ec;
    L_0x01e0:
        r0 = r14.action;	 Catch:{ Exception -> 0x025b }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;	 Catch:{ Exception -> 0x025b }
        if (r0 != 0) goto L_0x01ec;
    L_0x01e6:
        r0 = r14.action;	 Catch:{ Exception -> 0x025b }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;	 Catch:{ Exception -> 0x025b }
        if (r0 == 0) goto L_0x0261;
    L_0x01ec:
        r0 = 13;
        r11 = r9.isNull(r0);	 Catch:{ Exception -> 0x025b }
        if (r11 != 0) goto L_0x022d;
    L_0x01f4:
        r0 = r9.byteBufferValue(r0);	 Catch:{ Exception -> 0x025b }
        if (r0 == 0) goto L_0x022d;
    L_0x01fa:
        r11 = 0;
        r13 = r0.readInt32(r11);	 Catch:{ Exception -> 0x025b }
        r13 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r13, r11);	 Catch:{ Exception -> 0x025b }
        r14.replyMessage = r13;	 Catch:{ Exception -> 0x025b }
        r11 = r14.replyMessage;	 Catch:{ Exception -> 0x025b }
        r13 = r1.currentAccount;	 Catch:{ Exception -> 0x025b }
        r13 = org.telegram.messenger.UserConfig.getInstance(r13);	 Catch:{ Exception -> 0x025b }
        r13 = r13.clientUserId;	 Catch:{ Exception -> 0x025b }
        r11.readAttachPath(r0, r13);	 Catch:{ Exception -> 0x025b }
        r0.reuse();	 Catch:{ Exception -> 0x025b }
        r0 = r14.replyMessage;	 Catch:{ Exception -> 0x025b }
        if (r0 == 0) goto L_0x022d;
    L_0x0219:
        r0 = org.telegram.messenger.MessageObject.isMegagroup(r14);	 Catch:{ Exception -> 0x025b }
        if (r0 == 0) goto L_0x0228;
    L_0x021f:
        r0 = r14.replyMessage;	 Catch:{ Exception -> 0x025b }
        r11 = r0.flags;	 Catch:{ Exception -> 0x025b }
        r13 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r11 = r11 | r13;
        r0.flags = r11;	 Catch:{ Exception -> 0x025b }
    L_0x0228:
        r0 = r14.replyMessage;	 Catch:{ Exception -> 0x025b }
        addUsersAndChatsFromMessage(r0, r2, r3);	 Catch:{ Exception -> 0x025b }
    L_0x022d:
        r0 = r14.replyMessage;	 Catch:{ Exception -> 0x025b }
        if (r0 != 0) goto L_0x0261;
    L_0x0231:
        r0 = r14.reply_to_msg_id;	 Catch:{ Exception -> 0x025b }
        r13 = r12;
        r11 = (long) r0;
        r0 = r14.to_id;	 Catch:{ Exception -> 0x0259 }
        r0 = r0.channel_id;	 Catch:{ Exception -> 0x0259 }
        if (r0 == 0) goto L_0x0242;
    L_0x023b:
        r0 = r14.to_id;	 Catch:{ Exception -> 0x0259 }
        r0 = r0.channel_id;	 Catch:{ Exception -> 0x0259 }
        r0 = (long) r0;	 Catch:{ Exception -> 0x0259 }
        r0 = r0 << r15;
        r11 = r11 | r0;
    L_0x0242:
        r0 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0259 }
        r0 = r5.contains(r0);	 Catch:{ Exception -> 0x0259 }
        if (r0 != 0) goto L_0x0253;
    L_0x024c:
        r0 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0259 }
        r5.add(r0);	 Catch:{ Exception -> 0x0259 }
    L_0x0253:
        r0 = r8.id;	 Catch:{ Exception -> 0x0259 }
        r6.put(r0, r14);	 Catch:{ Exception -> 0x0259 }
        goto L_0x0262;
    L_0x0259:
        r0 = move-exception;
        goto L_0x025d;
    L_0x025b:
        r0 = move-exception;
        r13 = r12;
    L_0x025d:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x03b4 }
        goto L_0x0262;
    L_0x0261:
        r13 = r12;
    L_0x0262:
        r0 = r8.id;	 Catch:{ Exception -> 0x03b4 }
        r1 = (int) r0;	 Catch:{ Exception -> 0x03b4 }
        r11 = r8.id;	 Catch:{ Exception -> 0x03b4 }
        r11 = r11 >> r15;
        r0 = (int) r11;	 Catch:{ Exception -> 0x03b4 }
        if (r1 == 0) goto L_0x02a7;
    L_0x026b:
        r8 = 1;
        if (r0 != r8) goto L_0x0280;
    L_0x026e:
        r0 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x03b4 }
        r0 = r3.contains(r0);	 Catch:{ Exception -> 0x03b4 }
        if (r0 != 0) goto L_0x02b8;
    L_0x0278:
        r0 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x03b4 }
        r3.add(r0);	 Catch:{ Exception -> 0x03b4 }
        goto L_0x02b8;
    L_0x0280:
        if (r1 <= 0) goto L_0x0294;
    L_0x0282:
        r0 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x03b4 }
        r0 = r2.contains(r0);	 Catch:{ Exception -> 0x03b4 }
        if (r0 != 0) goto L_0x02b8;
    L_0x028c:
        r0 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x03b4 }
        r2.add(r0);	 Catch:{ Exception -> 0x03b4 }
        goto L_0x02b8;
    L_0x0294:
        r0 = -r1;
        r1 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x03b4 }
        r1 = r3.contains(r1);	 Catch:{ Exception -> 0x03b4 }
        if (r1 != 0) goto L_0x02b8;
    L_0x029f:
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x03b4 }
        r3.add(r0);	 Catch:{ Exception -> 0x03b4 }
        goto L_0x02b8;
    L_0x02a7:
        r1 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x03b4 }
        r1 = r4.contains(r1);	 Catch:{ Exception -> 0x03b4 }
        if (r1 != 0) goto L_0x02b8;
    L_0x02b1:
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x03b4 }
        r4.add(r0);	 Catch:{ Exception -> 0x03b4 }
    L_0x02b8:
        r1 = r19;
        r12 = r13;
        r13 = r17;
        r8 = 0;
        r14 = 3;
        goto L_0x008d;
    L_0x02c1:
        r0 = move-exception;
        r13 = r12;
        r14 = r17;
        goto L_0x02d9;
    L_0x02c6:
        r17 = r13;
        r13 = r12;
        r9.dispose();	 Catch:{ Exception -> 0x03b4 }
        r10 = r10 + 1;
        r1 = r19;
        r12 = r13;
        r13 = r17;
        r8 = 2;
        goto L_0x0044;
    L_0x02d6:
        r0 = move-exception;
        r14 = r13;
        r13 = r12;
    L_0x02d9:
        r12 = r19;
        goto L_0x03be;
    L_0x02dd:
        r17 = r13;
        r13 = r12;
        r0 = r5.isEmpty();	 Catch:{ Exception -> 0x03b4 }
        r1 = ",";
        if (r0 != 0) goto L_0x0368;
    L_0x02e8:
        r12 = r19;
        r0 = r12.database;	 Catch:{ Exception -> 0x03b2 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03b2 }
        r8 = "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03b2 }
        r5 = android.text.TextUtils.join(r1, r5);	 Catch:{ Exception -> 0x03b2 }
        r9 = 0;
        r10[r9] = r5;	 Catch:{ Exception -> 0x03b2 }
        r5 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x03b2 }
        r7 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03b2 }
        r0 = r0.queryFinalized(r5, r7);	 Catch:{ Exception -> 0x03b2 }
    L_0x0304:
        r5 = r0.next();	 Catch:{ Exception -> 0x03b2 }
        if (r5 == 0) goto L_0x0364;
    L_0x030a:
        r5 = r0.byteBufferValue(r9);	 Catch:{ Exception -> 0x03b2 }
        if (r5 == 0) goto L_0x035e;
    L_0x0310:
        r7 = r5.readInt32(r9);	 Catch:{ Exception -> 0x03b2 }
        r7 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r5, r7, r9);	 Catch:{ Exception -> 0x03b2 }
        r8 = r12.currentAccount;	 Catch:{ Exception -> 0x03b2 }
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);	 Catch:{ Exception -> 0x03b2 }
        r8 = r8.clientUserId;	 Catch:{ Exception -> 0x03b2 }
        r7.readAttachPath(r5, r8);	 Catch:{ Exception -> 0x03b2 }
        r5.reuse();	 Catch:{ Exception -> 0x03b2 }
        r5 = 1;
        r8 = r0.intValue(r5);	 Catch:{ Exception -> 0x03b2 }
        r7.id = r8;	 Catch:{ Exception -> 0x03b2 }
        r8 = 2;
        r10 = r0.intValue(r8);	 Catch:{ Exception -> 0x03b2 }
        r7.date = r10;	 Catch:{ Exception -> 0x03b2 }
        r10 = 3;
        r14 = r0.longValue(r10);	 Catch:{ Exception -> 0x03b2 }
        r7.dialog_id = r14;	 Catch:{ Exception -> 0x03b2 }
        addUsersAndChatsFromMessage(r7, r2, r3);	 Catch:{ Exception -> 0x03b2 }
        r14 = r7.dialog_id;	 Catch:{ Exception -> 0x03b2 }
        r11 = r6.get(r14);	 Catch:{ Exception -> 0x03b2 }
        r11 = (org.telegram.tgnet.TLRPC.Message) r11;	 Catch:{ Exception -> 0x03b2 }
        if (r11 == 0) goto L_0x0361;
    L_0x0348:
        r11.replyMessage = r7;	 Catch:{ Exception -> 0x03b2 }
        r14 = r11.dialog_id;	 Catch:{ Exception -> 0x03b2 }
        r7.dialog_id = r14;	 Catch:{ Exception -> 0x03b2 }
        r7 = org.telegram.messenger.MessageObject.isMegagroup(r11);	 Catch:{ Exception -> 0x03b2 }
        if (r7 == 0) goto L_0x0361;
    L_0x0354:
        r7 = r11.replyMessage;	 Catch:{ Exception -> 0x03b2 }
        r11 = r7.flags;	 Catch:{ Exception -> 0x03b2 }
        r14 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r11 = r11 | r14;
        r7.flags = r11;	 Catch:{ Exception -> 0x03b2 }
        goto L_0x0304;
    L_0x035e:
        r5 = 1;
        r8 = 2;
        r10 = 3;
    L_0x0361:
        r14 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        goto L_0x0304;
    L_0x0364:
        r0.dispose();	 Catch:{ Exception -> 0x03b2 }
        goto L_0x036a;
    L_0x0368:
        r12 = r19;
    L_0x036a:
        r0 = r4.isEmpty();	 Catch:{ Exception -> 0x03b2 }
        if (r0 != 0) goto L_0x037a;
    L_0x0370:
        r0 = android.text.TextUtils.join(r1, r4);	 Catch:{ Exception -> 0x03b2 }
        r14 = r17;
        r12.getEncryptedChatsInternal(r0, r14, r2);	 Catch:{ Exception -> 0x03b0 }
        goto L_0x037c;
    L_0x037a:
        r14 = r17;
    L_0x037c:
        r0 = r3.isEmpty();	 Catch:{ Exception -> 0x03b0 }
        if (r0 != 0) goto L_0x038b;
    L_0x0382:
        r0 = android.text.TextUtils.join(r1, r3);	 Catch:{ Exception -> 0x03b0 }
        r3 = r13.chats;	 Catch:{ Exception -> 0x03b0 }
        r12.getChatsInternal(r0, r3);	 Catch:{ Exception -> 0x03b0 }
    L_0x038b:
        r0 = r2.isEmpty();	 Catch:{ Exception -> 0x03b0 }
        if (r0 != 0) goto L_0x039a;
    L_0x0391:
        r0 = android.text.TextUtils.join(r1, r2);	 Catch:{ Exception -> 0x03b0 }
        r1 = r13.users;	 Catch:{ Exception -> 0x03b0 }
        r12.getUsersInternal(r0, r1);	 Catch:{ Exception -> 0x03b0 }
    L_0x039a:
        r0 = r12.currentAccount;	 Catch:{ Exception -> 0x03b0 }
        r2 = org.telegram.messenger.MessagesController.getInstance(r0);	 Catch:{ Exception -> 0x03b0 }
        r8 = 1;
        r9 = 0;
        r10 = 0;
        r11 = 1;
        r3 = r13;
        r4 = r14;
        r5 = r20;
        r6 = r21;
        r7 = r22;
        r2.processLoadedDialogs(r3, r4, r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x03b0 }
        goto L_0x03e7;
    L_0x03b0:
        r0 = move-exception;
        goto L_0x03be;
    L_0x03b2:
        r0 = move-exception;
        goto L_0x03b7;
    L_0x03b4:
        r0 = move-exception;
        r12 = r19;
    L_0x03b7:
        r14 = r17;
        goto L_0x03be;
    L_0x03ba:
        r0 = move-exception;
        r14 = r13;
        r13 = r12;
    L_0x03bd:
        r12 = r1;
    L_0x03be:
        r1 = r13.dialogs;
        r1.clear();
        r1 = r13.users;
        r1.clear();
        r1 = r13.chats;
        r1.clear();
        r14.clear();
        org.telegram.messenger.FileLog.e(r0);
        r0 = r12.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = 0;
        r7 = 100;
        r8 = 1;
        r9 = 1;
        r10 = 0;
        r11 = 1;
        r3 = r13;
        r4 = r14;
        r5 = r20;
        r2.processLoadedDialogs(r3, r4, r5, r6, r7, r8, r9, r10, r11);
    L_0x03e7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogs$135$MessagesStorage(int, int, int):void");
    }

    public static void createFirstHoles(long j, SQLitePreparedStatement sQLitePreparedStatement, SQLitePreparedStatement sQLitePreparedStatement2, int i) throws Exception {
        sQLitePreparedStatement.requery();
        sQLitePreparedStatement.bindLong(1, j);
        sQLitePreparedStatement.bindInteger(2, i == 1 ? 1 : 0);
        sQLitePreparedStatement.bindInteger(3, i);
        sQLitePreparedStatement.step();
        for (int i2 = 0; i2 < 5; i2++) {
            sQLitePreparedStatement2.requery();
            sQLitePreparedStatement2.bindLong(1, j);
            sQLitePreparedStatement2.bindInteger(2, i2);
            sQLitePreparedStatement2.bindInteger(3, i == 1 ? 1 : 0);
            sQLitePreparedStatement2.bindInteger(4, i);
            sQLitePreparedStatement2.step();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:68:0x01f9  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00f5 A:{SYNTHETIC, Splitter:B:35:0x00f5} */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x025f A:{Catch:{ Exception -> 0x02ee }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x025d A:{Catch:{ Exception -> 0x02ee }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0289 A:{Catch:{ Exception -> 0x02ee }} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0272 A:{Catch:{ Exception -> 0x02ee }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0292 A:{Catch:{ Exception -> 0x02ee }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02b1 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0299 A:{Catch:{ Exception -> 0x02ee }} */
    private void putDialogsInternal(org.telegram.tgnet.TLRPC.messages_Dialogs r22, int r23) {
        /*
        r21 = this;
        r1 = r21;
        r0 = r22;
        r2 = r23;
        r3 = r1.database;	 Catch:{ Exception -> 0x02f2 }
        r3.beginTransaction();	 Catch:{ Exception -> 0x02f2 }
        r3 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x02f2 }
        r4 = r0.messages;	 Catch:{ Exception -> 0x02f2 }
        r4 = r4.size();	 Catch:{ Exception -> 0x02f2 }
        r3.<init>(r4);	 Catch:{ Exception -> 0x02f2 }
        r5 = 0;
    L_0x0017:
        r6 = r0.messages;	 Catch:{ Exception -> 0x02f2 }
        r6 = r6.size();	 Catch:{ Exception -> 0x02f2 }
        if (r5 >= r6) goto L_0x0031;
    L_0x001f:
        r6 = r0.messages;	 Catch:{ Exception -> 0x02f2 }
        r6 = r6.get(r5);	 Catch:{ Exception -> 0x02f2 }
        r6 = (org.telegram.tgnet.TLRPC.Message) r6;	 Catch:{ Exception -> 0x02f2 }
        r7 = org.telegram.messenger.MessageObject.getDialogId(r6);	 Catch:{ Exception -> 0x02f2 }
        r3.put(r7, r6);	 Catch:{ Exception -> 0x02f2 }
        r5 = r5 + 1;
        goto L_0x0017;
    L_0x0031:
        r5 = r0.dialogs;	 Catch:{ Exception -> 0x02f2 }
        r5 = r5.isEmpty();	 Catch:{ Exception -> 0x02f2 }
        if (r5 != 0) goto L_0x02d8;
    L_0x0039:
        r5 = r1.database;	 Catch:{ Exception -> 0x02ee }
        r6 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r5 = r5.executeFast(r6);	 Catch:{ Exception -> 0x02ee }
        r6 = r1.database;	 Catch:{ Exception -> 0x02ee }
        r7 = "REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        r6 = r6.executeFast(r7);	 Catch:{ Exception -> 0x02ee }
        r7 = r1.database;	 Catch:{ Exception -> 0x02ee }
        r8 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r7 = r7.executeFast(r8);	 Catch:{ Exception -> 0x02ee }
        r8 = r1.database;	 Catch:{ Exception -> 0x02ee }
        r9 = "REPLACE INTO dialog_settings VALUES(?, ?)";
        r8 = r8.executeFast(r9);	 Catch:{ Exception -> 0x02ee }
        r9 = r1.database;	 Catch:{ Exception -> 0x02ee }
        r10 = "REPLACE INTO messages_holes VALUES(?, ?, ?)";
        r9 = r9.executeFast(r10);	 Catch:{ Exception -> 0x02ee }
        r10 = r1.database;	 Catch:{ Exception -> 0x02ee }
        r11 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)";
        r10 = r10.executeFast(r11);	 Catch:{ Exception -> 0x02ee }
        r12 = 0;
        r13 = 0;
    L_0x006b:
        r14 = r0.dialogs;	 Catch:{ Exception -> 0x02ee }
        r14 = r14.size();	 Catch:{ Exception -> 0x02ee }
        if (r12 >= r14) goto L_0x02be;
    L_0x0073:
        r14 = r0.dialogs;	 Catch:{ Exception -> 0x02ee }
        r14 = r14.get(r12);	 Catch:{ Exception -> 0x02ee }
        r14 = (org.telegram.tgnet.TLRPC.Dialog) r14;	 Catch:{ Exception -> 0x02ee }
        org.telegram.messenger.DialogObject.initDialog(r14);	 Catch:{ Exception -> 0x02ee }
        r11 = 1;
        if (r2 != r11) goto L_0x00b1;
    L_0x0081:
        r11 = r1.database;	 Catch:{ Exception -> 0x02f2 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02f2 }
        r15.<init>();	 Catch:{ Exception -> 0x02f2 }
        r4 = "SELECT did FROM dialogs WHERE did = ";
        r15.append(r4);	 Catch:{ Exception -> 0x02f2 }
        r4 = r8;
        r16 = r9;
        r8 = r14.id;	 Catch:{ Exception -> 0x02f2 }
        r15.append(r8);	 Catch:{ Exception -> 0x02f2 }
        r8 = r15.toString();	 Catch:{ Exception -> 0x02f2 }
        r9 = 0;
        r15 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x02f2 }
        r8 = r11.queryFinalized(r8, r15);	 Catch:{ Exception -> 0x02f2 }
        r9 = r8.next();	 Catch:{ Exception -> 0x02f2 }
        r8.dispose();	 Catch:{ Exception -> 0x02f2 }
        if (r9 == 0) goto L_0x00e8;
    L_0x00a9:
        r19 = r3;
        r15 = r12;
        r9 = r16;
        r1 = 0;
        goto L_0x02b1;
    L_0x00b1:
        r4 = r8;
        r16 = r9;
        r8 = r14.pinned;	 Catch:{ Exception -> 0x02ee }
        if (r8 == 0) goto L_0x00e8;
    L_0x00b8:
        r8 = 2;
        if (r2 != r8) goto L_0x00e8;
    L_0x00bb:
        r8 = r1.database;	 Catch:{ Exception -> 0x02f2 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02f2 }
        r9.<init>();	 Catch:{ Exception -> 0x02f2 }
        r11 = "SELECT pinned FROM dialogs WHERE did = ";
        r9.append(r11);	 Catch:{ Exception -> 0x02f2 }
        r15 = r12;
        r11 = r14.id;	 Catch:{ Exception -> 0x02f2 }
        r9.append(r11);	 Catch:{ Exception -> 0x02f2 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x02f2 }
        r11 = 0;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x02f2 }
        r8 = r8.queryFinalized(r9, r12);	 Catch:{ Exception -> 0x02f2 }
        r9 = r8.next();	 Catch:{ Exception -> 0x02f2 }
        if (r9 == 0) goto L_0x00e4;
    L_0x00de:
        r9 = r8.intValue(r11);	 Catch:{ Exception -> 0x02f2 }
        r14.pinnedNum = r9;	 Catch:{ Exception -> 0x02f2 }
    L_0x00e4:
        r8.dispose();	 Catch:{ Exception -> 0x02f2 }
        goto L_0x00e9;
    L_0x00e8:
        r15 = r12;
    L_0x00e9:
        r8 = r14.id;	 Catch:{ Exception -> 0x02ee }
        r8 = r3.get(r8);	 Catch:{ Exception -> 0x02ee }
        r8 = (org.telegram.tgnet.TLRPC.Message) r8;	 Catch:{ Exception -> 0x02ee }
        r17 = 32;
        if (r8 == 0) goto L_0x01f9;
    L_0x00f5:
        r9 = r8.date;	 Catch:{ Exception -> 0x02f2 }
        r11 = 0;
        r9 = java.lang.Math.max(r9, r11);	 Catch:{ Exception -> 0x02f2 }
        r11 = r1.isValidKeyboardToSave(r8);	 Catch:{ Exception -> 0x02f2 }
        if (r11 == 0) goto L_0x0110;
    L_0x0102:
        r11 = r1.currentAccount;	 Catch:{ Exception -> 0x02f2 }
        r11 = org.telegram.messenger.DataQuery.getInstance(r11);	 Catch:{ Exception -> 0x02f2 }
        r18 = r13;
        r12 = r14.id;	 Catch:{ Exception -> 0x02f2 }
        r11.putBotKeyboard(r12, r8);	 Catch:{ Exception -> 0x02f2 }
        goto L_0x0112;
    L_0x0110:
        r18 = r13;
    L_0x0112:
        r1.fixUnsupportedMedia(r8);	 Catch:{ Exception -> 0x02f2 }
        r11 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x02f2 }
        r12 = r8.getObjectSize();	 Catch:{ Exception -> 0x02f2 }
        r11.<init>(r12);	 Catch:{ Exception -> 0x02f2 }
        r8.serializeToStream(r11);	 Catch:{ Exception -> 0x02f2 }
        r12 = r8.id;	 Catch:{ Exception -> 0x02f2 }
        r12 = (long) r12;	 Catch:{ Exception -> 0x02f2 }
        r2 = r8.to_id;	 Catch:{ Exception -> 0x02f2 }
        r2 = r2.channel_id;	 Catch:{ Exception -> 0x02f2 }
        if (r2 == 0) goto L_0x0135;
    L_0x012a:
        r2 = r8.to_id;	 Catch:{ Exception -> 0x02f2 }
        r2 = r2.channel_id;	 Catch:{ Exception -> 0x02f2 }
        r19 = r3;
        r2 = (long) r2;	 Catch:{ Exception -> 0x02f2 }
        r2 = r2 << r17;
        r12 = r12 | r2;
        goto L_0x0137;
    L_0x0135:
        r19 = r3;
    L_0x0137:
        r5.requery();	 Catch:{ Exception -> 0x02f2 }
        r2 = 1;
        r5.bindLong(r2, r12);	 Catch:{ Exception -> 0x02f2 }
        r2 = r14.id;	 Catch:{ Exception -> 0x02f2 }
        r20 = r9;
        r9 = 2;
        r5.bindLong(r9, r2);	 Catch:{ Exception -> 0x02f2 }
        r2 = org.telegram.messenger.MessageObject.getUnreadFlags(r8);	 Catch:{ Exception -> 0x02f2 }
        r3 = 3;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f2 }
        r2 = r8.send_state;	 Catch:{ Exception -> 0x02f2 }
        r3 = 4;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f2 }
        r2 = r8.date;	 Catch:{ Exception -> 0x02f2 }
        r3 = 5;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f2 }
        r2 = 6;
        r5.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x02f2 }
        r2 = org.telegram.messenger.MessageObject.isOut(r8);	 Catch:{ Exception -> 0x02f2 }
        if (r2 == 0) goto L_0x0166;
    L_0x0164:
        r2 = 1;
        goto L_0x0167;
    L_0x0166:
        r2 = 0;
    L_0x0167:
        r3 = 7;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f2 }
        r2 = 8;
        r3 = 0;
        r5.bindInteger(r2, r3);	 Catch:{ Exception -> 0x02f2 }
        r2 = r8.flags;	 Catch:{ Exception -> 0x02f2 }
        r2 = r2 & 1024;
        if (r2 == 0) goto L_0x017a;
    L_0x0177:
        r2 = r8.views;	 Catch:{ Exception -> 0x02f2 }
        goto L_0x017b;
    L_0x017a:
        r2 = 0;
    L_0x017b:
        r3 = 9;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f2 }
        r2 = 10;
        r3 = 0;
        r5.bindInteger(r2, r3);	 Catch:{ Exception -> 0x02f2 }
        r2 = r8.mentioned;	 Catch:{ Exception -> 0x02f2 }
        if (r2 == 0) goto L_0x018c;
    L_0x018a:
        r2 = 1;
        goto L_0x018d;
    L_0x018c:
        r2 = 0;
    L_0x018d:
        r3 = 11;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f2 }
        r5.step();	 Catch:{ Exception -> 0x02f2 }
        r2 = org.telegram.messenger.DataQuery.canAddMessageToMedia(r8);	 Catch:{ Exception -> 0x02f2 }
        if (r2 == 0) goto L_0x01bd;
    L_0x019b:
        r7.requery();	 Catch:{ Exception -> 0x02f2 }
        r2 = 1;
        r7.bindLong(r2, r12);	 Catch:{ Exception -> 0x02f2 }
        r2 = r14.id;	 Catch:{ Exception -> 0x02f2 }
        r9 = 2;
        r7.bindLong(r9, r2);	 Catch:{ Exception -> 0x02f2 }
        r2 = r8.date;	 Catch:{ Exception -> 0x02f2 }
        r3 = 3;
        r7.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f2 }
        r2 = org.telegram.messenger.DataQuery.getMediaType(r8);	 Catch:{ Exception -> 0x02f2 }
        r3 = 4;
        r7.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f2 }
        r2 = 5;
        r7.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x02f2 }
        r7.step();	 Catch:{ Exception -> 0x02f2 }
    L_0x01bd:
        r11.reuse();	 Catch:{ Exception -> 0x02f2 }
        r2 = r8.media;	 Catch:{ Exception -> 0x02f2 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;	 Catch:{ Exception -> 0x02f2 }
        if (r2 == 0) goto L_0x01eb;
    L_0x01c6:
        if (r18 != 0) goto L_0x01d1;
    L_0x01c8:
        r2 = r1.database;	 Catch:{ Exception -> 0x02f2 }
        r3 = "REPLACE INTO polls VALUES(?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x02f2 }
        goto L_0x01d3;
    L_0x01d1:
        r2 = r18;
    L_0x01d3:
        r3 = r8.media;	 Catch:{ Exception -> 0x02f2 }
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3;	 Catch:{ Exception -> 0x02f2 }
        r2.requery();	 Catch:{ Exception -> 0x02f2 }
        r9 = 1;
        r2.bindLong(r9, r12);	 Catch:{ Exception -> 0x02f2 }
        r3 = r3.poll;	 Catch:{ Exception -> 0x02f2 }
        r11 = r3.id;	 Catch:{ Exception -> 0x02f2 }
        r3 = 2;
        r2.bindLong(r3, r11);	 Catch:{ Exception -> 0x02f2 }
        r2.step();	 Catch:{ Exception -> 0x02f2 }
        r13 = r2;
        goto L_0x01ed;
    L_0x01eb:
        r13 = r18;
    L_0x01ed:
        r2 = r14.id;	 Catch:{ Exception -> 0x02f2 }
        r8 = r8.id;	 Catch:{ Exception -> 0x02f2 }
        r9 = r16;
        createFirstHoles(r2, r9, r10, r8);	 Catch:{ Exception -> 0x02f2 }
        r2 = r20;
        goto L_0x0200;
    L_0x01f9:
        r19 = r3;
        r18 = r13;
        r9 = r16;
        r2 = 0;
    L_0x0200:
        r3 = r14.top_message;	 Catch:{ Exception -> 0x02ee }
        r11 = (long) r3;	 Catch:{ Exception -> 0x02ee }
        r3 = r14.peer;	 Catch:{ Exception -> 0x02ee }
        if (r3 == 0) goto L_0x0215;
    L_0x0207:
        r3 = r14.peer;	 Catch:{ Exception -> 0x02ee }
        r3 = r3.channel_id;	 Catch:{ Exception -> 0x02ee }
        if (r3 == 0) goto L_0x0215;
    L_0x020d:
        r3 = r14.peer;	 Catch:{ Exception -> 0x02ee }
        r3 = r3.channel_id;	 Catch:{ Exception -> 0x02ee }
        r0 = (long) r3;	 Catch:{ Exception -> 0x02ee }
        r0 = r0 << r17;
        r11 = r11 | r0;
    L_0x0215:
        r6.requery();	 Catch:{ Exception -> 0x02ee }
        r0 = r14.id;	 Catch:{ Exception -> 0x02ee }
        r3 = 1;
        r6.bindLong(r3, r0);	 Catch:{ Exception -> 0x02ee }
        r0 = 2;
        r6.bindInteger(r0, r2);	 Catch:{ Exception -> 0x02ee }
        r0 = r14.unread_count;	 Catch:{ Exception -> 0x02ee }
        r1 = 3;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02ee }
        r0 = 4;
        r6.bindLong(r0, r11);	 Catch:{ Exception -> 0x02ee }
        r0 = r14.read_inbox_max_id;	 Catch:{ Exception -> 0x02ee }
        r1 = 5;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02ee }
        r0 = r14.read_outbox_max_id;	 Catch:{ Exception -> 0x02ee }
        r1 = 6;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02ee }
        r0 = 0;
        r2 = 7;
        r6.bindLong(r2, r0);	 Catch:{ Exception -> 0x02ee }
        r0 = r14.unread_mentions_count;	 Catch:{ Exception -> 0x02ee }
        r1 = 8;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02ee }
        r0 = r14.pts;	 Catch:{ Exception -> 0x02ee }
        r1 = 9;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02ee }
        r0 = 10;
        r1 = 0;
        r6.bindInteger(r0, r1);	 Catch:{ Exception -> 0x02ee }
        r0 = r14.pinnedNum;	 Catch:{ Exception -> 0x02ee }
        r2 = 11;
        r6.bindInteger(r2, r0);	 Catch:{ Exception -> 0x02ee }
        r0 = r14.unread_mark;	 Catch:{ Exception -> 0x02ee }
        if (r0 == 0) goto L_0x025f;
    L_0x025d:
        r0 = 1;
        goto L_0x0260;
    L_0x025f:
        r0 = 0;
    L_0x0260:
        r2 = 12;
        r6.bindInteger(r2, r0);	 Catch:{ Exception -> 0x02ee }
        r0 = 13;
        r2 = r14.folder_id;	 Catch:{ Exception -> 0x02ee }
        r6.bindInteger(r0, r2);	 Catch:{ Exception -> 0x02ee }
        r0 = r14 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder;	 Catch:{ Exception -> 0x02ee }
        r2 = 14;
        if (r0 == 0) goto L_0x0289;
    L_0x0272:
        r0 = r14;
        r0 = (org.telegram.tgnet.TLRPC.TL_dialogFolder) r0;	 Catch:{ Exception -> 0x02ee }
        r11 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x02ee }
        r3 = r0.folder;	 Catch:{ Exception -> 0x02ee }
        r3 = r3.getObjectSize();	 Catch:{ Exception -> 0x02ee }
        r11.<init>(r3);	 Catch:{ Exception -> 0x02ee }
        r0 = r0.folder;	 Catch:{ Exception -> 0x02ee }
        r0.serializeToStream(r11);	 Catch:{ Exception -> 0x02ee }
        r6.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x02ee }
        goto L_0x028d;
    L_0x0289:
        r6.bindNull(r2);	 Catch:{ Exception -> 0x02ee }
        r11 = 0;
    L_0x028d:
        r6.step();	 Catch:{ Exception -> 0x02ee }
        if (r11 == 0) goto L_0x0295;
    L_0x0292:
        r11.reuse();	 Catch:{ Exception -> 0x02ee }
    L_0x0295:
        r0 = r14.notify_settings;	 Catch:{ Exception -> 0x02ee }
        if (r0 == 0) goto L_0x02b1;
    L_0x0299:
        r4.requery();	 Catch:{ Exception -> 0x02ee }
        r2 = r14.id;	 Catch:{ Exception -> 0x02ee }
        r0 = 1;
        r4.bindLong(r0, r2);	 Catch:{ Exception -> 0x02ee }
        r2 = r14.notify_settings;	 Catch:{ Exception -> 0x02ee }
        r2 = r2.mute_until;	 Catch:{ Exception -> 0x02ee }
        if (r2 == 0) goto L_0x02a9;
    L_0x02a8:
        goto L_0x02aa;
    L_0x02a9:
        r0 = 0;
    L_0x02aa:
        r2 = 2;
        r4.bindInteger(r2, r0);	 Catch:{ Exception -> 0x02ee }
        r4.step();	 Catch:{ Exception -> 0x02ee }
    L_0x02b1:
        r12 = r15 + 1;
        r1 = r21;
        r0 = r22;
        r2 = r23;
        r8 = r4;
        r3 = r19;
        goto L_0x006b;
    L_0x02be:
        r4 = r8;
        r18 = r13;
        r5.dispose();	 Catch:{ Exception -> 0x02ee }
        r6.dispose();	 Catch:{ Exception -> 0x02ee }
        r7.dispose();	 Catch:{ Exception -> 0x02ee }
        r4.dispose();	 Catch:{ Exception -> 0x02ee }
        r9.dispose();	 Catch:{ Exception -> 0x02ee }
        r10.dispose();	 Catch:{ Exception -> 0x02ee }
        if (r18 == 0) goto L_0x02d8;
    L_0x02d5:
        r18.dispose();	 Catch:{ Exception -> 0x02ee }
    L_0x02d8:
        r0 = r22;
        r1 = r0.users;	 Catch:{ Exception -> 0x02ee }
        r2 = r21;
        r2.putUsersInternal(r1);	 Catch:{ Exception -> 0x02ec }
        r0 = r0.chats;	 Catch:{ Exception -> 0x02ec }
        r2.putChatsInternal(r0);	 Catch:{ Exception -> 0x02ec }
        r0 = r2.database;	 Catch:{ Exception -> 0x02ec }
        r0.commitTransaction();	 Catch:{ Exception -> 0x02ec }
        goto L_0x02f7;
    L_0x02ec:
        r0 = move-exception;
        goto L_0x02f4;
    L_0x02ee:
        r0 = move-exception;
        r2 = r21;
        goto L_0x02f4;
    L_0x02f2:
        r0 = move-exception;
        r2 = r1;
    L_0x02f4:
        org.telegram.messenger.FileLog.e(r0);
    L_0x02f7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.putDialogsInternal(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void");
    }

    public void getDialogFolderId(long j, IntCallback intCallback) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ewHcRLSZPnikCOi0fKu1m_NClQI(this, j, intCallback));
    }

    public /* synthetic */ void lambda$getDialogFolderId$137$MessagesStorage(long j, IntCallback intCallback) {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT folder_id FROM dialogs WHERE did = ?", Long.valueOf(j));
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$ldjdkm--KTEvj7b3Kw6L1hvd8lo(intCallback, intValue));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setDialogsFolderId(ArrayList<TL_folderPeer> arrayList, ArrayList<TL_inputFolderPeer> arrayList2, long j, int i) {
        if (arrayList != null || arrayList2 != null || j != 0) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$R773_K8RtbzYzQJ4L6bSJxmWu-I(this, arrayList, arrayList2, i, j));
        }
    }

    public /* synthetic */ void lambda$setDialogsFolderId$139$MessagesStorage(ArrayList arrayList, ArrayList arrayList2, int i, long j) {
        try {
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET folder_id = ? WHERE did = ?");
            long peerDialogId;
            if (arrayList != null) {
                int size = arrayList.size();
                for (i = 0; i < size; i++) {
                    TL_folderPeer tL_folderPeer = (TL_folderPeer) arrayList.get(i);
                    peerDialogId = DialogObject.getPeerDialogId(tL_folderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tL_folderPeer.folder_id);
                    executeFast.bindLong(2, peerDialogId);
                    executeFast.step();
                }
            } else if (arrayList2 != null) {
                int size2 = arrayList2.size();
                for (i = 0; i < size2; i++) {
                    TL_inputFolderPeer tL_inputFolderPeer = (TL_inputFolderPeer) arrayList2.get(i);
                    peerDialogId = DialogObject.getPeerDialogId(tL_inputFolderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tL_inputFolderPeer.folder_id);
                    executeFast.bindLong(2, peerDialogId);
                    executeFast.step();
                }
            } else {
                executeFast.requery();
                executeFast.bindInteger(1, i);
                executeFast.bindLong(2, j);
                executeFast.step();
            }
            executeFast.dispose();
            this.database.commitTransaction();
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = ?", Integer.valueOf(1));
            if (!queryFinalized.next()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$0kSZduOOK002Z9ObwoRQYgrzY0A(this));
                SQLiteDatabase sQLiteDatabase = this.database;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("DELETE FROM dialogs WHERE did = ");
                stringBuilder.append(DialogObject.makeFolderDialogId(1));
                sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$138$MessagesStorage() {
        MessagesController.getInstance(this.currentAccount).onFolderEmpty(1);
    }

    public void unpinAllDialogsExceptNew(ArrayList<Long> arrayList, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$7r-cvm7jReUjKGWZi-pAFOesIxk(this, arrayList, i));
    }

    public /* synthetic */ void lambda$unpinAllDialogsExceptNew$140$MessagesStorage(ArrayList arrayList, int i) {
        try {
            long longValue;
            ArrayList arrayList2 = new ArrayList();
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did, folder_id FROM dialogs WHERE pinned != 0 AND did NOT IN (%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
            while (queryFinalized.next()) {
                longValue = queryFinalized.longValue(0);
                if (!(queryFinalized.intValue(1) != i || ((int) longValue) == 0 || DialogObject.isFolderDialogId(longValue))) {
                    arrayList2.add(Long.valueOf(queryFinalized.longValue(0)));
                }
            }
            queryFinalized.dispose();
            if (!arrayList2.isEmpty()) {
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                for (i = 0; i < arrayList2.size(); i++) {
                    longValue = ((Long) arrayList2.get(i)).longValue();
                    executeFast.requery();
                    executeFast.bindInteger(1, 0);
                    executeFast.bindLong(2, longValue);
                    executeFast.step();
                }
                executeFast.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setDialogUnread(long j, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$KHNqRZb2PzoIoyy0xgTrJAaClN4(this, j, z));
    }

    /* JADX WARNING: Missing block: B:5:0x0025, code skipped:
            if (r0 != null) goto L_0x0027;
     */
    /* JADX WARNING: Missing block: B:7:?, code skipped:
            r0.dispose();
     */
    /* JADX WARNING: Missing block: B:12:0x0031, code skipped:
            if (r0 == null) goto L_0x0034;
     */
    /* JADX WARNING: Missing block: B:14:0x0035, code skipped:
            if (r8 == false) goto L_0x003a;
     */
    /* JADX WARNING: Missing block: B:15:0x0037, code skipped:
            r8 = r1 | 1;
     */
    /* JADX WARNING: Missing block: B:16:0x003a, code skipped:
            r8 = r1 & -2;
     */
    /* JADX WARNING: Missing block: B:18:?, code skipped:
            r1 = r5.database.executeFast("UPDATE dialogs SET flags = ? WHERE did = ?");
            r1.bindInteger(1, r8);
            r1.bindLong(2, r6);
            r1.step();
            r1.dispose();
     */
    /* JADX WARNING: Missing block: B:25:?, code skipped:
            return;
     */
    public /* synthetic */ void lambda$setDialogUnread$141$MessagesStorage(long r6, boolean r8) {
        /*
        r5 = this;
        r0 = 0;
        r1 = 0;
        r2 = r5.database;	 Catch:{ Exception -> 0x002d }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x002d }
        r3.<init>();	 Catch:{ Exception -> 0x002d }
        r4 = "SELECT flags FROM dialogs WHERE did = ";
        r3.append(r4);	 Catch:{ Exception -> 0x002d }
        r3.append(r6);	 Catch:{ Exception -> 0x002d }
        r3 = r3.toString();	 Catch:{ Exception -> 0x002d }
        r4 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x002d }
        r0 = r2.queryFinalized(r3, r4);	 Catch:{ Exception -> 0x002d }
        r2 = r0.next();	 Catch:{ Exception -> 0x002d }
        if (r2 == 0) goto L_0x0025;
    L_0x0021:
        r1 = r0.intValue(r1);	 Catch:{ Exception -> 0x002d }
    L_0x0025:
        if (r0 == 0) goto L_0x0034;
    L_0x0027:
        r0.dispose();	 Catch:{ Exception -> 0x0058 }
        goto L_0x0034;
    L_0x002b:
        r6 = move-exception;
        goto L_0x0052;
    L_0x002d:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x002b }
        if (r0 == 0) goto L_0x0034;
    L_0x0033:
        goto L_0x0027;
    L_0x0034:
        r0 = 1;
        if (r8 == 0) goto L_0x003a;
    L_0x0037:
        r8 = r1 | 1;
        goto L_0x003c;
    L_0x003a:
        r8 = r1 & -2;
    L_0x003c:
        r1 = r5.database;	 Catch:{ Exception -> 0x0058 }
        r2 = "UPDATE dialogs SET flags = ? WHERE did = ?";
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x0058 }
        r1.bindInteger(r0, r8);	 Catch:{ Exception -> 0x0058 }
        r8 = 2;
        r1.bindLong(r8, r6);	 Catch:{ Exception -> 0x0058 }
        r1.step();	 Catch:{ Exception -> 0x0058 }
        r1.dispose();	 Catch:{ Exception -> 0x0058 }
        goto L_0x005c;
    L_0x0052:
        if (r0 == 0) goto L_0x0057;
    L_0x0054:
        r0.dispose();	 Catch:{ Exception -> 0x0058 }
    L_0x0057:
        throw r6;	 Catch:{ Exception -> 0x0058 }
    L_0x0058:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
    L_0x005c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$setDialogUnread$141$MessagesStorage(long, boolean):void");
    }

    public void setDialogPinned(long j, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$oBdUa83v4mqDlU6OEBn8Ae-paMQ(this, i, j));
    }

    public /* synthetic */ void lambda$setDialogPinned$142$MessagesStorage(int i, long j) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
            executeFast.bindInteger(1, i);
            executeFast.bindLong(2, j);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putDialogs(messages_Dialogs messages_dialogs, int i) {
        if (!messages_dialogs.dialogs.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$iAnrRvLMH-KuGYxN6OT7166Ee-c(this, messages_dialogs, i));
        }
    }

    public /* synthetic */ void lambda$putDialogs$143$MessagesStorage(messages_Dialogs messages_dialogs, int i) {
        putDialogsInternal(messages_dialogs, i);
        try {
            loadUnreadMessages();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public int getDialogReadMax(boolean z, long j) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Integer[] numArr = new Integer[]{Integer.valueOf(0)};
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$FOq6Q-IXeDd_eqXntttlf2bW6ao(this, z, j, numArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return numArr[0].intValue();
    }

    /*  JADX ERROR: NullPointerException in pass: ProcessVariables
        java.lang.NullPointerException
        	at jadx.core.dex.visitors.regions.ProcessVariables.addToUsageMap(ProcessVariables.java:278)
        	at jadx.core.dex.visitors.regions.ProcessVariables.access$000(ProcessVariables.java:31)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processInsn(ProcessVariables.java:163)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processBlockTraced(ProcessVariables.java:129)
        	at jadx.core.dex.visitors.regions.TracedRegionVisitor.processBlock(TracedRegionVisitor.java:23)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:53)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
        	at jadx.core.dex.visitors.regions.ProcessVariables.visit(ProcessVariables.java:183)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$getDialogReadMax$144$MessagesStorage(boolean r5, long r6, java.lang.Integer[] r8, java.util.concurrent.CountDownLatch r9) {
        /*
        r4 = this;
        r0 = 0;
        r1 = 0;
        if (r5 == 0) goto L_0x001e;
    L_0x0004:
        r5 = r4.database;	 Catch:{ Exception -> 0x004d }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x004d }
        r2.<init>();	 Catch:{ Exception -> 0x004d }
        r3 = "SELECT outbox_max FROM dialogs WHERE did = ";	 Catch:{ Exception -> 0x004d }
        r2.append(r3);	 Catch:{ Exception -> 0x004d }
        r2.append(r6);	 Catch:{ Exception -> 0x004d }
        r6 = r2.toString();	 Catch:{ Exception -> 0x004d }
        r7 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x004d }
        r5 = r5.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x004d }
        goto L_0x0037;	 Catch:{ Exception -> 0x004d }
    L_0x001e:
        r5 = r4.database;	 Catch:{ Exception -> 0x004d }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x004d }
        r2.<init>();	 Catch:{ Exception -> 0x004d }
        r3 = "SELECT inbox_max FROM dialogs WHERE did = ";	 Catch:{ Exception -> 0x004d }
        r2.append(r3);	 Catch:{ Exception -> 0x004d }
        r2.append(r6);	 Catch:{ Exception -> 0x004d }
        r6 = r2.toString();	 Catch:{ Exception -> 0x004d }
        r7 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x004d }
        r5 = r5.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x004d }
    L_0x0037:
        r1 = r5;	 Catch:{ Exception -> 0x004d }
        r5 = r1.next();	 Catch:{ Exception -> 0x004d }
        if (r5 == 0) goto L_0x0048;	 Catch:{ Exception -> 0x004d }
    L_0x003e:
        r5 = r1.intValue(r0);	 Catch:{ Exception -> 0x004d }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x004d }
        r8[r0] = r5;	 Catch:{ Exception -> 0x004d }
    L_0x0048:
        if (r1 == 0) goto L_0x0056;
    L_0x004a:
        goto L_0x0053;
    L_0x004b:
        r5 = move-exception;
        goto L_0x005a;
    L_0x004d:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x004b }
        if (r1 == 0) goto L_0x0056;
    L_0x0053:
        r1.dispose();
    L_0x0056:
        r9.countDown();
        return;
    L_0x005a:
        if (r1 == 0) goto L_0x005f;
    L_0x005c:
        r1.dispose();
    L_0x005f:
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogReadMax$144$MessagesStorage(boolean, long, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public int getChannelPtsSync(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Integer[] numArr = new Integer[]{Integer.valueOf(0)};
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$XDZcxDWtc9YJXWwwuHEAiouKFOo(this, i, numArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return numArr[0].intValue();
    }

    /*  JADX ERROR: NullPointerException in pass: ProcessVariables
        java.lang.NullPointerException
        	at jadx.core.dex.visitors.regions.ProcessVariables.addToUsageMap(ProcessVariables.java:278)
        	at jadx.core.dex.visitors.regions.ProcessVariables.access$000(ProcessVariables.java:31)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processInsn(ProcessVariables.java:163)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processBlockTraced(ProcessVariables.java:129)
        	at jadx.core.dex.visitors.regions.TracedRegionVisitor.processBlock(TracedRegionVisitor.java:23)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:53)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
        	at jadx.core.dex.visitors.regions.ProcessVariables.visit(ProcessVariables.java:183)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x003c A:{SYNTHETIC, Splitter:B:11:0x003c} */
    /* JADX WARNING: Removed duplicated region for block: B:18:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x003c A:{SYNTHETIC, Splitter:B:11:0x003c} */
    /* JADX WARNING: Missing block: B:12:?, code skipped:
            r7.countDown();
     */
    /* JADX WARNING: Missing block: B:13:0x0040, code skipped:
            r5 = move-exception;
     */
    /* JADX WARNING: Missing block: B:14:0x0041, code skipped:
            org.telegram.messenger.FileLog.e(r5);
     */
    /* JADX WARNING: Missing block: B:17:?, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:19:?, code skipped:
            return;
     */
    public /* synthetic */ void lambda$getChannelPtsSync$145$MessagesStorage(int r5, java.lang.Integer[] r6, java.util.concurrent.CountDownLatch r7) {
        /*
        r4 = this;
        r0 = 0;
        r1 = r4.database;	 Catch:{ Exception -> 0x0031 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0031 }
        r2.<init>();	 Catch:{ Exception -> 0x0031 }
        r3 = "SELECT pts FROM dialogs WHERE did = ";	 Catch:{ Exception -> 0x0031 }
        r2.append(r3);	 Catch:{ Exception -> 0x0031 }
        r5 = -r5;	 Catch:{ Exception -> 0x0031 }
        r2.append(r5);	 Catch:{ Exception -> 0x0031 }
        r5 = r2.toString();	 Catch:{ Exception -> 0x0031 }
        r2 = 0;	 Catch:{ Exception -> 0x0031 }
        r3 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x0031 }
        r0 = r1.queryFinalized(r5, r3);	 Catch:{ Exception -> 0x0031 }
        r5 = r0.next();	 Catch:{ Exception -> 0x0031 }
        if (r5 == 0) goto L_0x002c;	 Catch:{ Exception -> 0x0031 }
    L_0x0022:
        r5 = r0.intValue(r2);	 Catch:{ Exception -> 0x0031 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0031 }
        r6[r2] = r5;	 Catch:{ Exception -> 0x0031 }
    L_0x002c:
        if (r0 == 0) goto L_0x003a;
    L_0x002e:
        goto L_0x0037;
    L_0x002f:
        r5 = move-exception;
        goto L_0x0045;
    L_0x0031:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x002f }
        if (r0 == 0) goto L_0x003a;
    L_0x0037:
        r0.dispose();
    L_0x003a:
        if (r7 == 0) goto L_0x0044;
    L_0x003c:
        r7.countDown();	 Catch:{ Exception -> 0x0040 }
        goto L_0x0044;
    L_0x0040:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);
    L_0x0044:
        return;
    L_0x0045:
        if (r0 == 0) goto L_0x004a;
    L_0x0047:
        r0.dispose();
    L_0x004a:
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getChannelPtsSync$145$MessagesStorage(int, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public User getUserSync(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        User[] userArr = new User[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$mrY-LvUy85XJlnRw0PoJ-ykrU_M(this, userArr, i, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return userArr[0];
    }

    public /* synthetic */ void lambda$getUserSync$146$MessagesStorage(User[] userArr, int i, CountDownLatch countDownLatch) {
        userArr[0] = getUser(i);
        countDownLatch.countDown();
    }

    public Chat getChatSync(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Chat[] chatArr = new Chat[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$znwT4txtFnJ52cmieDvgCTJdlp0(this, chatArr, i, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return chatArr[0];
    }

    public /* synthetic */ void lambda$getChatSync$147$MessagesStorage(Chat[] chatArr, int i, CountDownLatch countDownLatch) {
        chatArr[0] = getChat(i);
        countDownLatch.countDown();
    }

    public User getUser(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i);
            getUsersInternal(stringBuilder.toString(), arrayList);
            if (arrayList.isEmpty()) {
                return null;
            }
            return (User) arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public ArrayList<User> getUsers(ArrayList<Integer> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        try {
            getUsersInternal(TextUtils.join(",", arrayList), arrayList2);
        } catch (Exception e) {
            arrayList2.clear();
            FileLog.e(e);
        }
        return arrayList2;
    }

    public Chat getChat(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i);
            getChatsInternal(stringBuilder.toString(), arrayList);
            if (arrayList.isEmpty()) {
                return null;
            }
            return (Chat) arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public EncryptedChat getEncryptedChat(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i);
            getEncryptedChatsInternal(stringBuilder.toString(), arrayList, null);
            if (arrayList.isEmpty()) {
                return null;
            }
            return (EncryptedChat) arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }
}
