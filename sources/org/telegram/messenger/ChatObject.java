package org.telegram.messenger;

import android.os.SystemClock;
import android.util.SparseArray;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$GroupCall;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_channel;
import org.telegram.tgnet.TLRPC$TL_channelForbidden;
import org.telegram.tgnet.TLRPC$TL_channel_layer48;
import org.telegram.tgnet.TLRPC$TL_channel_layer67;
import org.telegram.tgnet.TLRPC$TL_channel_layer72;
import org.telegram.tgnet.TLRPC$TL_channel_layer77;
import org.telegram.tgnet.TLRPC$TL_channel_layer92;
import org.telegram.tgnet.TLRPC$TL_channel_old;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatEmpty;
import org.telegram.tgnet.TLRPC$TL_chatForbidden;
import org.telegram.tgnet.TLRPC$TL_chat_layer92;
import org.telegram.tgnet.TLRPC$TL_chat_old;
import org.telegram.tgnet.TLRPC$TL_chat_old2;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_inputGroupCall;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_phone_editGroupCallTitle;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupParticipants;
import org.telegram.tgnet.TLRPC$TL_phone_groupCall;
import org.telegram.tgnet.TLRPC$TL_phone_groupParticipants;
import org.telegram.tgnet.TLRPC$TL_phone_toggleGroupCallRecord;
import org.telegram.tgnet.TLRPC$TL_updateGroupCall;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants;
import org.telegram.tgnet.TLRPC$Updates;

public class ChatObject {
    public static final int ACTION_ADD_ADMINS = 4;
    public static final int ACTION_BLOCK_USERS = 2;
    public static final int ACTION_CHANGE_INFO = 1;
    public static final int ACTION_DELETE_MESSAGES = 13;
    public static final int ACTION_EDIT_MESSAGES = 12;
    public static final int ACTION_EMBED_LINKS = 9;
    public static final int ACTION_INVITE = 3;
    public static final int ACTION_MANAGE_CALLS = 14;
    public static final int ACTION_PIN = 0;
    public static final int ACTION_POST = 5;
    public static final int ACTION_SEND = 6;
    public static final int ACTION_SEND_MEDIA = 7;
    public static final int ACTION_SEND_POLLS = 10;
    public static final int ACTION_SEND_STICKERS = 8;
    public static final int ACTION_VIEW = 11;
    public static final int CHAT_TYPE_CHANNEL = 2;
    public static final int CHAT_TYPE_CHAT = 0;
    public static final int CHAT_TYPE_MEGAGROUP = 4;
    public static final int CHAT_TYPE_USER = 3;

    private static boolean isAdminAction(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 12 || i == 13;
    }

    private static boolean isBannableAction(int i) {
        if (!(i == 0 || i == 1 || i == 3)) {
            switch (i) {
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public static class Call {
        public TLRPC$GroupCall call;
        public int chatId;
        private Runnable checkQueueRunnable;
        public AccountInstance currentAccount;
        public ArrayList<Integer> invitedUsers = new ArrayList<>();
        public HashSet<Integer> invitedUsersMap = new HashSet<>();
        private int lastLoadGuid;
        private HashSet<Integer> loadingGuids = new HashSet<>();
        public boolean loadingMembers;
        private HashSet<Integer> loadingSsrcs = new HashSet<>();
        private HashSet<Integer> loadingUids = new HashSet<>();
        public boolean membersLoadEndReached;
        private String nextLoadOffset;
        public SparseArray<TLRPC$TL_groupCallParticipant> participants = new SparseArray<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsBySources = new SparseArray<>();
        public boolean recording;
        public boolean reloadingMembers;
        public TLRPC$Peer selfPeer;
        public ArrayList<TLRPC$TL_groupCallParticipant> sortedParticipants = new ArrayList<>();
        public int speakingMembersCount;
        private Runnable typingUpdateRunnable = new Runnable() {
            public final void run() {
                ChatObject.Call.this.lambda$new$0$ChatObject$Call();
            }
        };
        private boolean typingUpdateRunnableScheduled;
        private ArrayList<TLRPC$TL_updateGroupCallParticipants> updatesQueue = new ArrayList<>();
        private long updatesStartWaitTime;

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ChatObject$Call() {
            this.typingUpdateRunnableScheduled = false;
            checkOnlineParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallTypingsUpdated, new Object[0]);
        }

        public void setCall(AccountInstance accountInstance, int i, TLRPC$TL_phone_groupCall tLRPC$TL_phone_groupCall) {
            this.chatId = i;
            this.currentAccount = accountInstance;
            TLRPC$GroupCall tLRPC$GroupCall = tLRPC$TL_phone_groupCall.call;
            this.call = tLRPC$GroupCall;
            this.recording = tLRPC$GroupCall.record_start_date != 0;
            int i2 = Integer.MAX_VALUE;
            int size = tLRPC$TL_phone_groupCall.participants.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_phone_groupCall.participants.get(i3);
                this.participants.put(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer), tLRPC$TL_groupCallParticipant);
                this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                int i4 = tLRPC$TL_groupCallParticipant.source;
                if (i4 != 0) {
                    this.participantsBySources.put(i4, tLRPC$TL_groupCallParticipant);
                }
                i2 = Math.min(i2, tLRPC$TL_groupCallParticipant.date);
            }
            sortParticipants();
            this.nextLoadOffset = tLRPC$TL_phone_groupCall.participants_next_offset;
            loadMembers(1);
        }

        public void migrateToChat(TLRPC$Chat tLRPC$Chat) {
            this.chatId = tLRPC$Chat.id;
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance != null && sharedInstance.getAccount() == this.currentAccount.getCurrentAccount() && sharedInstance.getChat() != null && sharedInstance.getChat().id == (-this.chatId)) {
                sharedInstance.migrateToChat(tLRPC$Chat);
            }
        }

        public void loadMembers(int i) {
            if (i != 0) {
                if (!this.reloadingMembers) {
                    this.membersLoadEndReached = false;
                    this.nextLoadOffset = null;
                } else {
                    return;
                }
            }
            if (!this.membersLoadEndReached) {
                if (i != 0) {
                    this.reloadingMembers = true;
                }
                this.loadingMembers = true;
                TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants = new TLRPC$TL_phone_getGroupParticipants();
                tLRPC$TL_phone_getGroupParticipants.call = getInputGroupCall();
                String str = this.nextLoadOffset;
                if (str == null) {
                    str = "";
                }
                tLRPC$TL_phone_getGroupParticipants.offset = str;
                tLRPC$TL_phone_getGroupParticipants.limit = 20;
                this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new RequestDelegate(i, tLRPC$TL_phone_getGroupParticipants) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ TLRPC$TL_phone_getGroupParticipants f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        ChatObject.Call.this.lambda$loadMembers$2$ChatObject$Call(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$loadMembers$2 */
        public /* synthetic */ void lambda$loadMembers$2$ChatObject$Call(int i, TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(i, tLObject, tLRPC$TL_phone_getGroupParticipants) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ TLObject f$2;
                public final /* synthetic */ TLRPC$TL_phone_getGroupParticipants f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ChatObject.Call.this.lambda$null$1$ChatObject$Call(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x014a  */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x014f A[SYNTHETIC] */
        /* renamed from: lambda$null$1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$1$ChatObject$Call(int r18, org.telegram.tgnet.TLObject r19, org.telegram.tgnet.TLRPC$TL_phone_getGroupParticipants r20) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                r2 = r20
                r3 = 0
                r0.loadingMembers = r3
                if (r1 == 0) goto L_0x000d
                r0.reloadingMembers = r3
            L_0x000d:
                if (r19 == 0) goto L_0x0194
                r4 = r19
                org.telegram.tgnet.TLRPC$TL_phone_groupParticipants r4 = (org.telegram.tgnet.TLRPC$TL_phone_groupParticipants) r4
                org.telegram.messenger.AccountInstance r5 = r0.currentAccount
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r6 = r4.users
                r5.putUsers(r6, r3)
                org.telegram.messenger.AccountInstance r5 = r0.currentAccount
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r4.chats
                r5.putChats(r6, r3)
                r5 = 0
                org.telegram.tgnet.TLRPC$Peer r6 = r0.selfPeer
                if (r6 == 0) goto L_0x0033
                int r6 = org.telegram.messenger.MessageObject.getPeerId(r6)
                goto L_0x003d
            L_0x0033:
                org.telegram.messenger.AccountInstance r6 = r0.currentAccount
                org.telegram.messenger.UserConfig r6 = r6.getUserConfig()
                int r6 = r6.getClientUserId()
            L_0x003d:
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r7 = r0.participants
                java.lang.Object r6 = r7.get(r6)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r6
                java.lang.String r7 = r2.offset
                boolean r7 = android.text.TextUtils.isEmpty(r7)
                if (r7 == 0) goto L_0x0073
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r7 = r0.participants
                int r7 = r7.size()
                if (r7 == 0) goto L_0x005f
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r5 = r0.participants
                android.util.SparseArray r7 = new android.util.SparseArray
                r7.<init>()
                r0.participants = r7
                goto L_0x0064
            L_0x005f:
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r7 = r0.participants
                r7.clear()
            L_0x0064:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r7 = r0.sortedParticipants
                r7.clear()
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r7 = r0.participantsBySources
                r7.clear()
                java.util.HashSet<java.lang.Integer> r7 = r0.loadingGuids
                r7.clear()
            L_0x0073:
                java.lang.String r7 = r4.next_offset
                r0.nextLoadOffset = r7
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r7 = r4.participants
                boolean r7 = r7.isEmpty()
                r8 = 1
                if (r7 != 0) goto L_0x0088
                java.lang.String r7 = r0.nextLoadOffset
                boolean r7 = android.text.TextUtils.isEmpty(r7)
                if (r7 == 0) goto L_0x008a
            L_0x0088:
                r0.membersLoadEndReached = r8
            L_0x008a:
                java.lang.String r2 = r2.offset
                boolean r2 = android.text.TextUtils.isEmpty(r2)
                if (r2 == 0) goto L_0x009c
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                int r7 = r4.version
                r2.version = r7
                int r7 = r4.count
                r2.participants_count = r7
            L_0x009c:
                long r9 = android.os.SystemClock.elapsedRealtime()
                org.telegram.messenger.AccountInstance r2 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
                int r7 = org.telegram.messenger.NotificationCenter.applyGroupCallVisibleParticipants
                java.lang.Object[] r11 = new java.lang.Object[r8]
                java.lang.Long r12 = java.lang.Long.valueOf(r9)
                r11[r3] = r12
                r2.postNotificationName(r7, r11)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r4.participants
                int r2 = r2.size()
                r7 = 0
                r11 = 0
            L_0x00bb:
                r12 = 2
                if (r7 > r2) goto L_0x0156
                if (r7 != r2) goto L_0x00cc
                if (r1 != r12) goto L_0x00c8
                if (r6 == 0) goto L_0x00c8
                if (r11 != 0) goto L_0x00c8
                r12 = r6
                goto L_0x00d9
            L_0x00c8:
                r19 = r4
                goto L_0x014f
            L_0x00cc:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r12 = r4.participants
                java.lang.Object r12 = r12.get(r7)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r12
                boolean r13 = r12.self
                if (r13 == 0) goto L_0x00d9
                r11 = 1
            L_0x00d9:
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r13 = r0.participants
                org.telegram.tgnet.TLRPC$Peer r14 = r12.peer
                int r14 = org.telegram.messenger.MessageObject.getPeerId(r14)
                java.lang.Object r13 = r13.get(r14)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r13 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r13
                if (r13 == 0) goto L_0x010a
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r14 = r0.sortedParticipants
                r14.remove(r13)
                int r14 = r13.source
                if (r14 == 0) goto L_0x00f7
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r15 = r0.participantsBySources
                r15.remove(r14)
            L_0x00f7:
                int r14 = r12.active_date
                int r13 = r13.active_date
                int r13 = java.lang.Math.max(r14, r13)
                r12.lastTypingDate = r13
                long r14 = r12.lastVisibleDate
                int r16 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
                if (r16 == 0) goto L_0x0134
                r12.active_date = r13
                goto L_0x0134
            L_0x010a:
                if (r5 == 0) goto L_0x0134
                org.telegram.tgnet.TLRPC$Peer r13 = r12.peer
                int r13 = org.telegram.messenger.MessageObject.getPeerId(r13)
                java.lang.Object r13 = r5.get(r13)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r13 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r13
                if (r13 == 0) goto L_0x0134
                int r14 = r12.active_date
                int r15 = r13.active_date
                int r14 = java.lang.Math.max(r14, r15)
                r12.lastTypingDate = r14
                r19 = r4
                long r3 = r12.lastVisibleDate
                int r16 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
                if (r16 == 0) goto L_0x012f
                r12.active_date = r14
                goto L_0x0136
            L_0x012f:
                int r3 = r13.active_date
                r12.active_date = r3
                goto L_0x0136
            L_0x0134:
                r19 = r4
            L_0x0136:
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r3 = r0.participants
                org.telegram.tgnet.TLRPC$Peer r4 = r12.peer
                int r4 = org.telegram.messenger.MessageObject.getPeerId(r4)
                r3.put(r4, r12)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r3 = r0.sortedParticipants
                r3.add(r12)
                int r3 = r12.source
                if (r3 == 0) goto L_0x014f
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r4 = r0.participantsBySources
                r4.put(r3, r12)
            L_0x014f:
                int r7 = r7 + 1
                r4 = r19
                r3 = 0
                goto L_0x00bb
            L_0x0156:
                org.telegram.tgnet.TLRPC$GroupCall r1 = r0.call
                int r1 = r1.participants_count
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.participants
                int r2 = r2.size()
                if (r1 >= r2) goto L_0x016c
                org.telegram.tgnet.TLRPC$GroupCall r1 = r0.call
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.participants
                int r2 = r2.size()
                r1.participants_count = r2
            L_0x016c:
                r17.sortParticipants()
                org.telegram.messenger.AccountInstance r1 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
                int r2 = org.telegram.messenger.NotificationCenter.groupCallUpdated
                r3 = 3
                java.lang.Object[] r3 = new java.lang.Object[r3]
                int r4 = r0.chatId
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r5 = 0
                r3[r5] = r4
                org.telegram.tgnet.TLRPC$GroupCall r4 = r0.call
                long r4 = r4.id
                java.lang.Long r4 = java.lang.Long.valueOf(r4)
                r3[r8] = r4
                java.lang.Boolean r4 = java.lang.Boolean.FALSE
                r3[r12] = r4
                r1.postNotificationName(r2, r3)
            L_0x0194:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.lambda$null$1$ChatObject$Call(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_phone_getGroupParticipants):void");
        }

        public void setTitle(String str) {
            TLRPC$TL_phone_editGroupCallTitle tLRPC$TL_phone_editGroupCallTitle = new TLRPC$TL_phone_editGroupCallTitle();
            tLRPC$TL_phone_editGroupCallTitle.call = getInputGroupCall();
            tLRPC$TL_phone_editGroupCallTitle.title = str;
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_editGroupCallTitle, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatObject.Call.this.lambda$setTitle$3$ChatObject$Call(tLObject, tLRPC$TL_error);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$setTitle$3 */
        public /* synthetic */ void lambda$setTitle$3$ChatObject$Call(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                this.currentAccount.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            }
        }

        public void addInvitedUser(int i) {
            if (this.participants.get(i) == null && !this.invitedUsersMap.contains(Integer.valueOf(i))) {
                this.invitedUsersMap.add(Integer.valueOf(i));
                this.invitedUsers.add(Integer.valueOf(i));
            }
        }

        public void processTypingsUpdate(AccountInstance accountInstance, ArrayList<Integer> arrayList, int i) {
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(SystemClock.elapsedRealtime()));
            int size = arrayList.size();
            ArrayList arrayList2 = null;
            boolean z = false;
            for (int i2 = 0; i2 < size; i2++) {
                Integer num = arrayList.get(i2);
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.participants.get(num.intValue());
                if (tLRPC$TL_groupCallParticipant == null) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(num);
                } else if (i - tLRPC$TL_groupCallParticipant.lastTypingDate > 10) {
                    if (tLRPC$TL_groupCallParticipant.lastVisibleDate != ((long) i)) {
                        tLRPC$TL_groupCallParticipant.active_date = i;
                    }
                    tLRPC$TL_groupCallParticipant.lastTypingDate = i;
                    z = true;
                }
            }
            if (arrayList2 != null) {
                loadUnknownParticipants(arrayList2, true, (Runnable) null);
            }
            if (z) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        private void loadUnknownParticipants(ArrayList<Integer> arrayList, boolean z, Runnable runnable) {
            HashSet<Integer> hashSet = z ? this.loadingUids : this.loadingSsrcs;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                if (hashSet.contains(arrayList.get(i))) {
                    arrayList.remove(i);
                    i--;
                    size--;
                }
                i++;
            }
            if (!arrayList.isEmpty()) {
                int i2 = this.lastLoadGuid + 1;
                this.lastLoadGuid = i2;
                this.loadingGuids.add(Integer.valueOf(i2));
                hashSet.addAll(arrayList);
                TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants = new TLRPC$TL_phone_getGroupParticipants();
                tLRPC$TL_phone_getGroupParticipants.call = getInputGroupCall();
                if (z) {
                    int size2 = arrayList.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        Integer num = arrayList.get(i3);
                        if (num.intValue() > 0) {
                            TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
                            tLRPC$TL_inputPeerUser.user_id = num.intValue();
                            tLRPC$TL_phone_getGroupParticipants.ids.add(tLRPC$TL_inputPeerUser);
                        } else {
                            TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
                            tLRPC$TL_inputPeerChannel.channel_id = -num.intValue();
                            tLRPC$TL_phone_getGroupParticipants.ids.add(tLRPC$TL_inputPeerChannel);
                        }
                    }
                } else {
                    tLRPC$TL_phone_getGroupParticipants.sources = arrayList;
                }
                tLRPC$TL_phone_getGroupParticipants.offset = "";
                tLRPC$TL_phone_getGroupParticipants.limit = 100;
                this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new RequestDelegate(i2, runnable) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ Runnable f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        ChatObject.Call.this.lambda$loadUnknownParticipants$5$ChatObject$Call(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$loadUnknownParticipants$5 */
        public /* synthetic */ void lambda$loadUnknownParticipants$5$ChatObject$Call(int i, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(i, tLObject, runnable) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ TLObject f$2;
                public final /* synthetic */ Runnable f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ChatObject.Call.this.lambda$null$4$ChatObject$Call(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$4 */
        public /* synthetic */ void lambda$null$4$ChatObject$Call(int i, TLObject tLObject, Runnable runnable) {
            if (this.loadingGuids.remove(Integer.valueOf(i)) && tLObject != null) {
                TLRPC$TL_phone_groupParticipants tLRPC$TL_phone_groupParticipants = (TLRPC$TL_phone_groupParticipants) tLObject;
                this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupParticipants.users, false);
                this.currentAccount.getMessagesController().putChats(tLRPC$TL_phone_groupParticipants.chats, false);
                int size = tLRPC$TL_phone_groupParticipants.participants.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_phone_groupParticipants.participants.get(i2);
                    int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.participants.get(peerId);
                    if (tLRPC$TL_groupCallParticipant2 != null) {
                        this.sortedParticipants.remove(tLRPC$TL_groupCallParticipant2);
                        int i3 = tLRPC$TL_groupCallParticipant2.source;
                        if (i3 != 0) {
                            this.participantsBySources.remove(i3);
                        }
                    }
                    this.participants.put(peerId, tLRPC$TL_groupCallParticipant);
                    this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                    int i4 = tLRPC$TL_groupCallParticipant.source;
                    if (i4 != 0) {
                        this.participantsBySources.put(i4, tLRPC$TL_groupCallParticipant);
                    }
                    if (this.invitedUsersMap.contains(Integer.valueOf(peerId))) {
                        Integer valueOf = Integer.valueOf(peerId);
                        this.invitedUsersMap.remove(valueOf);
                        this.invitedUsers.remove(valueOf);
                    }
                }
                if (this.call.participants_count < this.participants.size()) {
                    this.call.participants_count = this.participants.size();
                }
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
                if (runnable != null) {
                    runnable.run();
                }
            }
        }

        public void processVoiceLevelsUpdate(int[] iArr, float[] fArr, boolean[] zArr) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
            int i;
            int currentTime = this.currentAccount.getConnectionsManager().getCurrentTime();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(elapsedRealtime));
            ArrayList arrayList = null;
            boolean z = false;
            for (int i2 = 0; i2 < iArr.length; i2++) {
                if (iArr[i2] == 0) {
                    TLRPC$Peer tLRPC$Peer = this.selfPeer;
                    if (tLRPC$Peer != null) {
                        i = MessageObject.getPeerId(tLRPC$Peer);
                    } else {
                        i = this.currentAccount.getUserConfig().getClientUserId();
                    }
                    tLRPC$TL_groupCallParticipant = this.participants.get(i);
                } else {
                    tLRPC$TL_groupCallParticipant = this.participantsBySources.get(iArr[i2]);
                }
                if (tLRPC$TL_groupCallParticipant != null) {
                    tLRPC$TL_groupCallParticipant.hasVoice = zArr[i2];
                    if (fArr[i2] > 0.1f) {
                        if (zArr[i2] && tLRPC$TL_groupCallParticipant.lastTypingDate + 1 < currentTime) {
                            if (elapsedRealtime != tLRPC$TL_groupCallParticipant.lastVisibleDate) {
                                tLRPC$TL_groupCallParticipant.active_date = currentTime;
                            }
                            tLRPC$TL_groupCallParticipant.lastTypingDate = currentTime;
                            z = true;
                        }
                        tLRPC$TL_groupCallParticipant.lastSpeakTime = SystemClock.uptimeMillis();
                        tLRPC$TL_groupCallParticipant.amplitude = fArr[i2];
                    } else {
                        tLRPC$TL_groupCallParticipant.amplitude = 0.0f;
                    }
                } else {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(iArr[i2]));
                }
            }
            if (arrayList != null) {
                loadUnknownParticipants(arrayList, false, (Runnable) null);
            }
            if (z) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        public void processUnknownVideoParticipants(int[] iArr, Runnable runnable) {
            ArrayList arrayList = null;
            for (int i = 0; i < iArr.length; i++) {
                if (this.participantsBySources.get(iArr[i]) == null) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(iArr[i]));
                }
            }
            if (arrayList != null) {
                loadUnknownParticipants(arrayList, false, runnable);
            }
        }

        private int isValidUpdate(TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants) {
            int i = this.call.version;
            int i2 = i + 1;
            int i3 = tLRPC$TL_updateGroupCallParticipants.version;
            if (i2 == i3 || i == i3) {
                return 0;
            }
            return i < i3 ? 1 : 2;
        }

        public void setSelfPeer(TLRPC$InputPeer tLRPC$InputPeer) {
            if (tLRPC$InputPeer == null) {
                this.selfPeer = null;
            } else if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerUser) {
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                this.selfPeer = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = tLRPC$InputPeer.user_id;
            } else {
                TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                this.selfPeer = tLRPC$TL_peerChannel;
                tLRPC$TL_peerChannel.channel_id = tLRPC$InputPeer.channel_id;
            }
        }

        private void processUpdatesQueue() {
            Collections.sort(this.updatesQueue, $$Lambda$ChatObject$Call$7O3T0NEnDcpjg_DSOcu2AQIl_I.INSTANCE);
            ArrayList<TLRPC$TL_updateGroupCallParticipants> arrayList = this.updatesQueue;
            if (arrayList != null && !arrayList.isEmpty()) {
                boolean z = false;
                while (this.updatesQueue.size() > 0) {
                    TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants = this.updatesQueue.get(0);
                    int isValidUpdate = isValidUpdate(tLRPC$TL_updateGroupCallParticipants);
                    if (isValidUpdate == 0) {
                        processParticipantsUpdate(tLRPC$TL_updateGroupCallParticipants, true);
                        this.updatesQueue.remove(0);
                        z = true;
                    } else if (isValidUpdate != 1) {
                        this.updatesQueue.remove(0);
                    } else if (this.updatesStartWaitTime == 0 || (!z && Math.abs(System.currentTimeMillis() - this.updatesStartWaitTime) > 1500)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN GROUP CALL UPDATES QUEUE - reload participants");
                        }
                        this.updatesStartWaitTime = 0;
                        this.updatesQueue.clear();
                        this.nextLoadOffset = null;
                        loadMembers(1);
                        return;
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN GROUP CALL UPDATES QUEUE - will wait more time");
                        }
                        if (z) {
                            this.updatesStartWaitTime = System.currentTimeMillis();
                            return;
                        }
                        return;
                    }
                }
                this.updatesQueue.clear();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("GROUP CALL UPDATES QUEUE PROCEED - OK");
                }
            }
            this.updatesStartWaitTime = 0;
        }

        /* access modifiers changed from: private */
        public void checkQueue() {
            this.checkQueueRunnable = null;
            if (this.updatesStartWaitTime != 0 && System.currentTimeMillis() - this.updatesStartWaitTime >= 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("QUEUE GROUP CALL UPDATES WAIT TIMEOUT - CHECK QUEUE");
                }
                processUpdatesQueue();
            }
            if (!this.updatesQueue.isEmpty()) {
                $$Lambda$ChatObject$Call$A9Iyjy8qm5XwdmVhsjek1P93bY r0 = new Runnable() {
                    public final void run() {
                        ChatObject.Call.this.checkQueue();
                    }
                };
                this.checkQueueRunnable = r0;
                AndroidUtilities.runOnUIThread(r0, 1000);
            }
        }

        public void processParticipantsUpdate(TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants, boolean z) {
            int i;
            long j;
            boolean z2;
            TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants2 = tLRPC$TL_updateGroupCallParticipants;
            int i2 = 0;
            int i3 = 1;
            if (!z) {
                int size = tLRPC$TL_updateGroupCallParticipants2.participants.size();
                int i4 = 0;
                while (true) {
                    if (i4 >= size) {
                        z2 = false;
                        break;
                    } else if (tLRPC$TL_updateGroupCallParticipants2.participants.get(i4).versioned) {
                        z2 = true;
                        break;
                    } else {
                        i4++;
                    }
                }
                if (!z2 || this.call.version + 1 >= tLRPC$TL_updateGroupCallParticipants2.version) {
                    if (z2 && tLRPC$TL_updateGroupCallParticipants2.version < this.call.version) {
                        return;
                    }
                } else if (this.reloadingMembers || this.updatesStartWaitTime == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTime) <= 1500) {
                    if (this.updatesStartWaitTime == 0) {
                        this.updatesStartWaitTime = System.currentTimeMillis();
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("add TL_updateGroupCallParticipants to queue " + tLRPC$TL_updateGroupCallParticipants2.version);
                    }
                    this.updatesQueue.add(tLRPC$TL_updateGroupCallParticipants2);
                    if (this.checkQueueRunnable == null) {
                        $$Lambda$ChatObject$Call$A9Iyjy8qm5XwdmVhsjek1P93bY r1 = new Runnable() {
                            public final void run() {
                                ChatObject.Call.this.checkQueue();
                            }
                        };
                        this.checkQueueRunnable = r1;
                        AndroidUtilities.runOnUIThread(r1, 1500);
                        return;
                    }
                    return;
                } else {
                    this.nextLoadOffset = null;
                    loadMembers(1);
                    return;
                }
            }
            TLRPC$Peer tLRPC$Peer = this.selfPeer;
            if (tLRPC$Peer != null) {
                i = MessageObject.getPeerId(tLRPC$Peer);
            } else {
                i = this.currentAccount.getUserConfig().getClientUserId();
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(elapsedRealtime));
            int size2 = tLRPC$TL_updateGroupCallParticipants2.participants.size();
            int i5 = 0;
            boolean z3 = false;
            boolean z4 = false;
            while (i5 < size2) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_updateGroupCallParticipants2.participants.get(i5);
                int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.participants.get(peerId);
                if (!tLRPC$TL_groupCallParticipant.left) {
                    if (this.invitedUsersMap.contains(Integer.valueOf(peerId))) {
                        Integer valueOf = Integer.valueOf(peerId);
                        this.invitedUsersMap.remove(valueOf);
                        this.invitedUsers.remove(valueOf);
                    }
                    if (tLRPC$TL_groupCallParticipant2 != null) {
                        tLRPC$TL_groupCallParticipant2.muted = tLRPC$TL_groupCallParticipant.muted;
                        tLRPC$TL_groupCallParticipant2.muted_by_you = tLRPC$TL_groupCallParticipant.muted_by_you;
                        if (!tLRPC$TL_groupCallParticipant.min) {
                            tLRPC$TL_groupCallParticipant2.volume = tLRPC$TL_groupCallParticipant.volume;
                        } else {
                            int i6 = tLRPC$TL_groupCallParticipant.flags;
                            if ((i6 & 128) != 0 && (tLRPC$TL_groupCallParticipant2.flags & 128) == 0) {
                                tLRPC$TL_groupCallParticipant.flags = i6 & -129;
                            }
                        }
                        tLRPC$TL_groupCallParticipant2.flags = tLRPC$TL_groupCallParticipant.flags;
                        tLRPC$TL_groupCallParticipant2.can_self_unmute = tLRPC$TL_groupCallParticipant.can_self_unmute;
                        if (tLRPC$TL_groupCallParticipant2.raise_hand_rating == 0 && tLRPC$TL_groupCallParticipant.raise_hand_rating != 0) {
                            tLRPC$TL_groupCallParticipant2.lastRaiseHandDate = SystemClock.elapsedRealtime();
                        }
                        tLRPC$TL_groupCallParticipant2.raise_hand_rating = tLRPC$TL_groupCallParticipant.raise_hand_rating;
                        tLRPC$TL_groupCallParticipant2.date = tLRPC$TL_groupCallParticipant.date;
                        int max = Math.max(tLRPC$TL_groupCallParticipant2.active_date, tLRPC$TL_groupCallParticipant.active_date);
                        tLRPC$TL_groupCallParticipant2.lastTypingDate = max;
                        if (elapsedRealtime != tLRPC$TL_groupCallParticipant2.lastVisibleDate) {
                            tLRPC$TL_groupCallParticipant2.active_date = max;
                        }
                        int i7 = tLRPC$TL_groupCallParticipant2.source;
                        if (i7 != tLRPC$TL_groupCallParticipant.source) {
                            if (i7 != 0) {
                                this.participantsBySources.remove(i7);
                            }
                            int i8 = tLRPC$TL_groupCallParticipant.source;
                            tLRPC$TL_groupCallParticipant2.source = i8;
                            if (i8 != 0) {
                                this.participantsBySources.put(i8, tLRPC$TL_groupCallParticipant2);
                            }
                        }
                        j = 0;
                    } else {
                        if (tLRPC$TL_groupCallParticipant.just_joined) {
                            int i9 = tLRPC$TL_updateGroupCallParticipants2.version;
                            TLRPC$GroupCall tLRPC$GroupCall = this.call;
                            if (i9 != tLRPC$GroupCall.version) {
                                tLRPC$GroupCall.participants_count++;
                            }
                        }
                        j = 0;
                        if (tLRPC$TL_groupCallParticipant.raise_hand_rating != 0) {
                            tLRPC$TL_groupCallParticipant.lastRaiseHandDate = SystemClock.elapsedRealtime();
                        }
                        this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                        this.participants.put(peerId, tLRPC$TL_groupCallParticipant);
                        int i10 = tLRPC$TL_groupCallParticipant.source;
                        if (i10 != 0) {
                            this.participantsBySources.put(i10, tLRPC$TL_groupCallParticipant);
                        }
                    }
                    if (peerId == i && tLRPC$TL_groupCallParticipant.active_date == 0) {
                        tLRPC$TL_groupCallParticipant.active_date = this.currentAccount.getConnectionsManager().getCurrentTime();
                    }
                } else if (tLRPC$TL_groupCallParticipant2 == null && tLRPC$TL_updateGroupCallParticipants2.version == this.call.version) {
                    j = 0;
                    i5++;
                    long j2 = j;
                    i2 = 0;
                    i3 = 1;
                } else {
                    if (tLRPC$TL_groupCallParticipant2 != null) {
                        this.participants.remove(peerId);
                        int i11 = tLRPC$TL_groupCallParticipant.source;
                        if (i11 != 0) {
                            this.participantsBySources.remove(i11);
                        }
                        this.sortedParticipants.remove(tLRPC$TL_groupCallParticipant2);
                    }
                    TLRPC$GroupCall tLRPC$GroupCall2 = this.call;
                    int i12 = tLRPC$GroupCall2.participants_count - i3;
                    tLRPC$GroupCall2.participants_count = i12;
                    if (i12 < 0) {
                        tLRPC$GroupCall2.participants_count = i2;
                    }
                    j = 0;
                }
                z3 = true;
                if (peerId == i) {
                    z4 = true;
                }
                i5++;
                long j22 = j;
                i2 = 0;
                i3 = 1;
            }
            int i13 = tLRPC$TL_updateGroupCallParticipants2.version;
            TLRPC$GroupCall tLRPC$GroupCall3 = this.call;
            if (i13 > tLRPC$GroupCall3.version) {
                tLRPC$GroupCall3.version = i13;
                if (!z) {
                    processUpdatesQueue();
                }
            }
            if (this.call.participants_count < this.participants.size()) {
                this.call.participants_count = this.participants.size();
            }
            if (z3) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.valueOf(z4));
            }
        }

        public void processGroupCallUpdate(AccountInstance accountInstance, TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall) {
            if (this.call.version < tLRPC$TL_updateGroupCall.call.version) {
                this.nextLoadOffset = null;
                loadMembers(1);
            }
            TLRPC$GroupCall tLRPC$GroupCall = tLRPC$TL_updateGroupCall.call;
            this.call = tLRPC$GroupCall;
            this.recording = tLRPC$GroupCall.record_start_date != 0;
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
        }

        public TLRPC$TL_inputGroupCall getInputGroupCall() {
            TLRPC$TL_inputGroupCall tLRPC$TL_inputGroupCall = new TLRPC$TL_inputGroupCall();
            TLRPC$GroupCall tLRPC$GroupCall = this.call;
            tLRPC$TL_inputGroupCall.id = tLRPC$GroupCall.id;
            tLRPC$TL_inputGroupCall.access_hash = tLRPC$GroupCall.access_hash;
            return tLRPC$TL_inputGroupCall;
        }

        private void sortParticipants() {
            Collections.sort(this.sortedParticipants, new Object(ChatObject.canManageCalls(this.currentAccount.getMessagesController().getChat(Integer.valueOf(this.chatId)))) {
                public final /* synthetic */ boolean f$0;

                {
                    this.f$0 = r1;
                }

                public final int compare(Object obj, Object obj2) {
                    return ChatObject.Call.lambda$sortParticipants$7(this.f$0, (TLRPC$TL_groupCallParticipant) obj, (TLRPC$TL_groupCallParticipant) obj2);
                }

                public /* synthetic */ Comparator reversed() {
                    return Comparator.CC.$default$reversed(this);
                }

                public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                    return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                }

                public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                    return Comparator.CC.$default$thenComparing(this, function, comparator);
                }

                public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                    return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                }

                public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                    return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                }

                public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                    return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                }

                public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                    return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
                }
            });
            checkOnlineParticipants();
        }

        static /* synthetic */ int lambda$sortParticipants$7(boolean z, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2) {
            int i;
            int i2 = tLRPC$TL_groupCallParticipant.active_date;
            if (i2 != 0 && (i = tLRPC$TL_groupCallParticipant2.active_date) != 0) {
                return C$r8$backportedMethods$utility$Integer$2$compare.compare(i, i2);
            }
            if (i2 != 0) {
                return -1;
            }
            if (tLRPC$TL_groupCallParticipant2.active_date != 0) {
                return 1;
            }
            if (z) {
                long j = tLRPC$TL_groupCallParticipant.raise_hand_rating;
                if (j != 0) {
                    long j2 = tLRPC$TL_groupCallParticipant2.raise_hand_rating;
                    if (j2 != 0) {
                        return (j2 > j ? 1 : (j2 == j ? 0 : -1));
                    }
                }
                if (j != 0) {
                    return -1;
                }
                if (tLRPC$TL_groupCallParticipant2.raise_hand_rating != 0) {
                    return 1;
                }
            }
            return C$r8$backportedMethods$utility$Integer$2$compare.compare(tLRPC$TL_groupCallParticipant2.date, tLRPC$TL_groupCallParticipant.date);
        }

        public void saveActiveDates() {
            int size = this.sortedParticipants.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.sortedParticipants.get(i);
                tLRPC$TL_groupCallParticipant.lastActiveDate = (long) tLRPC$TL_groupCallParticipant.active_date;
            }
        }

        private void checkOnlineParticipants() {
            if (this.typingUpdateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.typingUpdateRunnable);
                this.typingUpdateRunnableScheduled = false;
            }
            this.speakingMembersCount = 0;
            int currentTime = this.currentAccount.getConnectionsManager().getCurrentTime();
            int size = this.sortedParticipants.size();
            int i = Integer.MAX_VALUE;
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.sortedParticipants.get(i2);
                int i3 = currentTime - tLRPC$TL_groupCallParticipant.active_date;
                if (i3 < 5) {
                    this.speakingMembersCount++;
                    i = Math.min(i3, i);
                }
                if (Math.max(tLRPC$TL_groupCallParticipant.date, tLRPC$TL_groupCallParticipant.active_date) <= currentTime - 5) {
                    break;
                }
            }
            if (i != Integer.MAX_VALUE) {
                AndroidUtilities.runOnUIThread(this.typingUpdateRunnable, (long) (i * 1000));
                this.typingUpdateRunnableScheduled = true;
            }
        }

        public void toggleRecord(String str) {
            this.recording = !this.recording;
            TLRPC$TL_phone_toggleGroupCallRecord tLRPC$TL_phone_toggleGroupCallRecord = new TLRPC$TL_phone_toggleGroupCallRecord();
            tLRPC$TL_phone_toggleGroupCallRecord.call = getInputGroupCall();
            tLRPC$TL_phone_toggleGroupCallRecord.start = this.recording;
            if (str != null) {
                tLRPC$TL_phone_toggleGroupCallRecord.title = str;
                tLRPC$TL_phone_toggleGroupCallRecord.flags |= 2;
            }
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_toggleGroupCallRecord, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatObject.Call.this.lambda$toggleRecord$8$ChatObject$Call(tLObject, tLRPC$TL_error);
                }
            });
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$toggleRecord$8 */
        public /* synthetic */ void lambda$toggleRecord$8$ChatObject$Call(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                this.currentAccount.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            }
        }
    }

    public static int getParticipantVolume(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant) {
        if ((tLRPC$TL_groupCallParticipant.flags & 128) != 0) {
            return tLRPC$TL_groupCallParticipant.volume;
        }
        return 10000;
    }

    private static boolean getBannedRight(TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, int i) {
        if (tLRPC$TL_chatBannedRights == null) {
            return false;
        }
        if (i == 0) {
            return tLRPC$TL_chatBannedRights.pin_messages;
        }
        if (i == 1) {
            return tLRPC$TL_chatBannedRights.change_info;
        }
        if (i == 3) {
            return tLRPC$TL_chatBannedRights.invite_users;
        }
        switch (i) {
            case 6:
                return tLRPC$TL_chatBannedRights.send_messages;
            case 7:
                return tLRPC$TL_chatBannedRights.send_media;
            case 8:
                return tLRPC$TL_chatBannedRights.send_stickers;
            case 9:
                return tLRPC$TL_chatBannedRights.embed_links;
            case 10:
                return tLRPC$TL_chatBannedRights.send_polls;
            case 11:
                return tLRPC$TL_chatBannedRights.view_messages;
            default:
                return false;
        }
    }

    public static boolean isActionBannedByDefault(TLRPC$Chat tLRPC$Chat, int i) {
        if (getBannedRight(tLRPC$Chat.banned_rights, i)) {
            return false;
        }
        return getBannedRight(tLRPC$Chat.default_banned_rights, i);
    }

    public static boolean isActionBanned(TLRPC$Chat tLRPC$Chat, int i) {
        return tLRPC$Chat != null && (getBannedRight(tLRPC$Chat.banned_rights, i) || getBannedRight(tLRPC$Chat.default_banned_rights, i));
    }

    public static boolean canUserDoAdminAction(TLRPC$Chat tLRPC$Chat, int i) {
        boolean z;
        if (tLRPC$Chat == null) {
            return false;
        }
        if (tLRPC$Chat.creator) {
            return true;
        }
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights;
        if (tLRPC$TL_chatAdminRights != null) {
            if (i == 0) {
                z = tLRPC$TL_chatAdminRights.pin_messages;
            } else if (i == 1) {
                z = tLRPC$TL_chatAdminRights.change_info;
            } else if (i == 2) {
                z = tLRPC$TL_chatAdminRights.ban_users;
            } else if (i == 3) {
                z = tLRPC$TL_chatAdminRights.invite_users;
            } else if (i == 4) {
                z = tLRPC$TL_chatAdminRights.add_admins;
            } else if (i != 5) {
                switch (i) {
                    case 12:
                        z = tLRPC$TL_chatAdminRights.edit_messages;
                        break;
                    case 13:
                        z = tLRPC$TL_chatAdminRights.delete_messages;
                        break;
                    case 14:
                        z = tLRPC$TL_chatAdminRights.manage_call;
                        break;
                    default:
                        z = false;
                        break;
                }
            } else {
                z = tLRPC$TL_chatAdminRights.post_messages;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    public static boolean canUserDoAction(TLRPC$Chat tLRPC$Chat, int i) {
        if (tLRPC$Chat == null || canUserDoAdminAction(tLRPC$Chat, i)) {
            return true;
        }
        if (!getBannedRight(tLRPC$Chat.banned_rights, i) && isBannableAction(i)) {
            if (tLRPC$Chat.admin_rights != null && !isAdminAction(i)) {
                return true;
            }
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat.default_banned_rights;
            if (tLRPC$TL_chatBannedRights == null && ((tLRPC$Chat instanceof TLRPC$TL_chat_layer92) || (tLRPC$Chat instanceof TLRPC$TL_chat_old) || (tLRPC$Chat instanceof TLRPC$TL_chat_old2) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer92) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer77) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer72) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer67) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer48) || (tLRPC$Chat instanceof TLRPC$TL_channel_old))) {
                return true;
            }
            if (tLRPC$TL_chatBannedRights == null || getBannedRight(tLRPC$TL_chatBannedRights, i)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isLeftFromChat(TLRPC$Chat tLRPC$Chat) {
        return tLRPC$Chat == null || (tLRPC$Chat instanceof TLRPC$TL_chatEmpty) || (tLRPC$Chat instanceof TLRPC$TL_chatForbidden) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden) || tLRPC$Chat.left || tLRPC$Chat.deactivated;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0016, code lost:
        r1 = r1.banned_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isKickedFromChat(org.telegram.tgnet.TLRPC$Chat r1) {
        /*
            if (r1 == 0) goto L_0x0021
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_chatEmpty
            if (r0 != 0) goto L_0x0021
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_chatForbidden
            if (r0 != 0) goto L_0x0021
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelForbidden
            if (r0 != 0) goto L_0x0021
            boolean r0 = r1.kicked
            if (r0 != 0) goto L_0x0021
            boolean r0 = r1.deactivated
            if (r0 != 0) goto L_0x0021
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r1.banned_rights
            if (r1 == 0) goto L_0x001f
            boolean r1 = r1.view_messages
            if (r1 == 0) goto L_0x001f
            goto L_0x0021
        L_0x001f:
            r1 = 0
            goto L_0x0022
        L_0x0021:
            r1 = 1
        L_0x0022:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.isKickedFromChat(org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static boolean isNotInChat(TLRPC$Chat tLRPC$Chat) {
        return tLRPC$Chat == null || (tLRPC$Chat instanceof TLRPC$TL_chatEmpty) || (tLRPC$Chat instanceof TLRPC$TL_chatForbidden) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden) || tLRPC$Chat.left || tLRPC$Chat.kicked || tLRPC$Chat.deactivated;
    }

    public static boolean isChannel(TLRPC$Chat tLRPC$Chat) {
        return (tLRPC$Chat instanceof TLRPC$TL_channel) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden);
    }

    public static boolean isMegagroup(TLRPC$Chat tLRPC$Chat) {
        return ((tLRPC$Chat instanceof TLRPC$TL_channel) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden)) && tLRPC$Chat.megagroup;
    }

    public static boolean isMegagroup(int i, int i2) {
        TLRPC$Chat chat = MessagesController.getInstance(i).getChat(Integer.valueOf(i2));
        return isChannel(chat) && chat.megagroup;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0006, code lost:
        r1 = r1.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean hasAdminRights(org.telegram.tgnet.TLRPC$Chat r1) {
        /*
            if (r1 == 0) goto L_0x0010
            boolean r0 = r1.creator
            if (r0 != 0) goto L_0x000e
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r1.admin_rights
            if (r1 == 0) goto L_0x0010
            int r1 = r1.flags
            if (r1 == 0) goto L_0x0010
        L_0x000e:
            r1 = 1
            goto L_0x0011
        L_0x0010:
            r1 = 0
        L_0x0011:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.hasAdminRights(org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static boolean canChangeChatInfo(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 1);
    }

    public static boolean canAddAdmins(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 4);
    }

    public static boolean canBlockUsers(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 2);
    }

    public static boolean canManageCalls(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 14);
    }

    public static boolean canSendStickers(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 8);
    }

    public static boolean canSendEmbed(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 9);
    }

    public static boolean canSendMedia(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 7);
    }

    public static boolean canSendPolls(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 10);
    }

    public static boolean canSendMessages(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 6);
    }

    public static boolean canPost(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 5);
    }

    public static boolean canAddUsers(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 3);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = r0.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean shouldSendAnonymously(org.telegram.tgnet.TLRPC$Chat r0) {
        /*
            if (r0 == 0) goto L_0x000c
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r0.admin_rights
            if (r0 == 0) goto L_0x000c
            boolean r0 = r0.anonymous
            if (r0 == 0) goto L_0x000c
            r0 = 1
            goto L_0x000d
        L_0x000c:
            r0 = 0
        L_0x000d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.shouldSendAnonymously(org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static boolean canAddBotsToChat(TLRPC$Chat tLRPC$Chat) {
        if (isChannel(tLRPC$Chat)) {
            if (!tLRPC$Chat.megagroup) {
                return false;
            }
            TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights;
            if ((tLRPC$TL_chatAdminRights == null || (!tLRPC$TL_chatAdminRights.post_messages && !tLRPC$TL_chatAdminRights.add_admins)) && !tLRPC$Chat.creator) {
                return false;
            }
            return true;
        } else if (tLRPC$Chat.migrated_to == null) {
            return true;
        } else {
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0011, code lost:
        r2 = r2.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean canPinMessages(org.telegram.tgnet.TLRPC$Chat r2) {
        /*
            r0 = 0
            boolean r1 = canUserDoAction(r2, r0)
            if (r1 != 0) goto L_0x0019
            boolean r1 = isChannel(r2)
            if (r1 == 0) goto L_0x001a
            boolean r1 = r2.megagroup
            if (r1 != 0) goto L_0x001a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x001a
            boolean r2 = r2.edit_messages
            if (r2 == 0) goto L_0x001a
        L_0x0019:
            r0 = 1
        L_0x001a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.canPinMessages(org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static boolean isChannel(int i, int i2) {
        TLRPC$Chat chat = MessagesController.getInstance(i2).getChat(Integer.valueOf(i));
        return (chat instanceof TLRPC$TL_channel) || (chat instanceof TLRPC$TL_channelForbidden);
    }

    public static boolean isCanWriteToChannel(int i, int i2) {
        TLRPC$Chat chat = MessagesController.getInstance(i2).getChat(Integer.valueOf(i));
        return canSendMessages(chat) || chat.megagroup;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
        r0 = r1.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean canWriteToChat(org.telegram.tgnet.TLRPC$Chat r1) {
        /*
            boolean r0 = isChannel(r1)
            if (r0 == 0) goto L_0x0027
            boolean r0 = r1.creator
            if (r0 != 0) goto L_0x0027
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r1.admin_rights
            if (r0 == 0) goto L_0x0012
            boolean r0 = r0.post_messages
            if (r0 != 0) goto L_0x0027
        L_0x0012:
            boolean r0 = r1.broadcast
            if (r0 != 0) goto L_0x001a
            boolean r0 = r1.gigagroup
            if (r0 == 0) goto L_0x0027
        L_0x001a:
            boolean r0 = r1.gigagroup
            if (r0 == 0) goto L_0x0025
            boolean r1 = hasAdminRights(r1)
            if (r1 == 0) goto L_0x0025
            goto L_0x0027
        L_0x0025:
            r1 = 0
            goto L_0x0028
        L_0x0027:
            r1 = 1
        L_0x0028:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.canWriteToChat(org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static String getBannedRightsString(TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights) {
        return (((((((((((("" + (tLRPC$TL_chatBannedRights.view_messages ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_messages ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_media ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_stickers ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_gifs ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_games ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_inline ? 1 : 0)) + (tLRPC$TL_chatBannedRights.embed_links ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_polls ? 1 : 0)) + (tLRPC$TL_chatBannedRights.invite_users ? 1 : 0)) + (tLRPC$TL_chatBannedRights.change_info ? 1 : 0)) + (tLRPC$TL_chatBannedRights.pin_messages ? 1 : 0)) + tLRPC$TL_chatBannedRights.until_date;
    }

    public static TLRPC$Chat getChatByDialog(long j, int i) {
        int i2 = (int) j;
        if (i2 < 0) {
            return MessagesController.getInstance(i).getChat(Integer.valueOf(-i2));
        }
        return null;
    }
}
