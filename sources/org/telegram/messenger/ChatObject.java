package org.telegram.messenger;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$GroupCall;
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
import org.telegram.tgnet.TLRPC$TL_phone_getGroupParticipants;
import org.telegram.tgnet.TLRPC$TL_phone_groupCall;
import org.telegram.tgnet.TLRPC$TL_phone_groupParticipants;
import org.telegram.tgnet.TLRPC$TL_updateGroupCall;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants;

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
        public boolean membersLoadEndReached;
        private String nextLoadOffset;
        public SparseArray<TLRPC$TL_groupCallParticipant> participants = new SparseArray<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsBySources = new SparseArray<>();
        public boolean reloadingMembers;
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
            this.call = tLRPC$TL_phone_groupCall.call;
            int size = tLRPC$TL_phone_groupCall.participants.size();
            int i2 = Integer.MAX_VALUE;
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_phone_groupCall.participants.get(i3);
                this.participants.put(tLRPC$TL_groupCallParticipant.user_id, tLRPC$TL_groupCallParticipant);
                this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                this.participantsBySources.put(tLRPC$TL_groupCallParticipant.source, tLRPC$TL_groupCallParticipant);
                i2 = Math.min(i2, tLRPC$TL_groupCallParticipant.date);
            }
            sortParticipants();
            this.nextLoadOffset = tLRPC$TL_phone_groupCall.participants_next_offset;
            loadMembers(true);
        }

        public void migrateToChat(TLRPC$Chat tLRPC$Chat) {
            this.chatId = tLRPC$Chat.id;
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance != null && sharedInstance.getAccount() == this.currentAccount.getCurrentAccount() && sharedInstance.getChat() != null && sharedInstance.getChat().id == (-this.chatId)) {
                sharedInstance.migrateToChat(tLRPC$Chat);
            }
        }

        public void loadMembers(boolean z) {
            if (z) {
                if (!this.reloadingMembers) {
                    this.membersLoadEndReached = false;
                    this.nextLoadOffset = null;
                } else {
                    return;
                }
            }
            if (!this.membersLoadEndReached) {
                if (z) {
                    this.reloadingMembers = true;
                }
                this.loadingMembers = true;
                TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants = new TLRPC$TL_phone_getGroupParticipants();
                TLRPC$TL_inputGroupCall tLRPC$TL_inputGroupCall = new TLRPC$TL_inputGroupCall();
                tLRPC$TL_phone_getGroupParticipants.call = tLRPC$TL_inputGroupCall;
                TLRPC$GroupCall tLRPC$GroupCall = this.call;
                tLRPC$TL_inputGroupCall.id = tLRPC$GroupCall.id;
                tLRPC$TL_inputGroupCall.access_hash = tLRPC$GroupCall.access_hash;
                String str = this.nextLoadOffset;
                if (str == null) {
                    str = "";
                }
                tLRPC$TL_phone_getGroupParticipants.offset = str;
                tLRPC$TL_phone_getGroupParticipants.limit = 20;
                this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new RequestDelegate(z, tLRPC$TL_phone_getGroupParticipants) {
                    public final /* synthetic */ boolean f$1;
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
        public /* synthetic */ void lambda$loadMembers$2$ChatObject$Call(boolean z, TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(z, tLObject, tLRPC$TL_phone_getGroupParticipants) {
                public final /* synthetic */ boolean f$1;
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
        /* renamed from: lambda$null$1 */
        public /* synthetic */ void lambda$null$1$ChatObject$Call(boolean z, TLObject tLObject, TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
            this.loadingMembers = false;
            if (z) {
                this.reloadingMembers = false;
            }
            if (tLObject != null) {
                TLRPC$TL_phone_groupParticipants tLRPC$TL_phone_groupParticipants = (TLRPC$TL_phone_groupParticipants) tLObject;
                this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupParticipants.users, false);
                SparseArray<TLRPC$TL_groupCallParticipant> sparseArray = null;
                if (TextUtils.isEmpty(tLRPC$TL_phone_getGroupParticipants.offset)) {
                    if (this.participants.size() != 0) {
                        sparseArray = this.participants;
                        this.participants = new SparseArray<>();
                    } else {
                        this.participants.clear();
                    }
                    this.sortedParticipants.clear();
                    this.participantsBySources.clear();
                    this.loadingGuids.clear();
                }
                this.nextLoadOffset = tLRPC$TL_phone_groupParticipants.next_offset;
                if (tLRPC$TL_phone_groupParticipants.participants.isEmpty() || TextUtils.isEmpty(this.nextLoadOffset)) {
                    this.membersLoadEndReached = true;
                }
                if (TextUtils.isEmpty(tLRPC$TL_phone_getGroupParticipants.offset)) {
                    TLRPC$GroupCall tLRPC$GroupCall = this.call;
                    tLRPC$GroupCall.version = tLRPC$TL_phone_groupParticipants.version;
                    tLRPC$GroupCall.participants_count = tLRPC$TL_phone_groupParticipants.count;
                }
                int size = tLRPC$TL_phone_groupParticipants.participants.size();
                for (int i = 0; i < size; i++) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = tLRPC$TL_phone_groupParticipants.participants.get(i);
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant3 = this.participants.get(tLRPC$TL_groupCallParticipant2.user_id);
                    if (tLRPC$TL_groupCallParticipant3 != null) {
                        this.sortedParticipants.remove(tLRPC$TL_groupCallParticipant3);
                        this.participantsBySources.remove(tLRPC$TL_groupCallParticipant3.source);
                        tLRPC$TL_groupCallParticipant2.active_date = Math.max(tLRPC$TL_groupCallParticipant2.active_date, tLRPC$TL_groupCallParticipant3.active_date);
                    } else if (!(sparseArray == null || (tLRPC$TL_groupCallParticipant = sparseArray.get(tLRPC$TL_groupCallParticipant2.user_id)) == null)) {
                        tLRPC$TL_groupCallParticipant2.active_date = Math.max(tLRPC$TL_groupCallParticipant2.active_date, tLRPC$TL_groupCallParticipant.active_date);
                    }
                    this.participants.put(tLRPC$TL_groupCallParticipant2.user_id, tLRPC$TL_groupCallParticipant2);
                    this.sortedParticipants.add(tLRPC$TL_groupCallParticipant2);
                    this.participantsBySources.put(tLRPC$TL_groupCallParticipant2.source, tLRPC$TL_groupCallParticipant2);
                }
                if (this.call.participants_count < this.participants.size()) {
                    this.call.participants_count = this.participants.size();
                }
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        public void addInvitedUser(int i) {
            if (this.participants.get(i) == null && !this.invitedUsersMap.contains(Integer.valueOf(i))) {
                this.invitedUsersMap.add(Integer.valueOf(i));
                this.invitedUsers.add(Integer.valueOf(i));
            }
        }

        public void processTypingsUpdate(AccountInstance accountInstance, ArrayList<Integer> arrayList, int i) {
            int size = arrayList.size();
            ArrayList arrayList2 = null;
            boolean z = false;
            for (int i2 = 0; i2 < size; i2++) {
                Integer num = arrayList.get(i2);
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.participants.get(num.intValue());
                if (tLRPC$TL_groupCallParticipant != null) {
                    tLRPC$TL_groupCallParticipant.active_date = i;
                    z = true;
                } else {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(num);
                }
            }
            if (arrayList2 != null) {
                loadUnknownParticipants(arrayList2, true);
            }
            if (z) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        private void loadUnknownParticipants(ArrayList<Integer> arrayList, boolean z) {
            int i = this.lastLoadGuid + 1;
            this.lastLoadGuid = i;
            this.loadingGuids.add(Integer.valueOf(i));
            TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants = new TLRPC$TL_phone_getGroupParticipants();
            TLRPC$TL_inputGroupCall tLRPC$TL_inputGroupCall = new TLRPC$TL_inputGroupCall();
            tLRPC$TL_phone_getGroupParticipants.call = tLRPC$TL_inputGroupCall;
            TLRPC$GroupCall tLRPC$GroupCall = this.call;
            tLRPC$TL_inputGroupCall.id = tLRPC$GroupCall.id;
            tLRPC$TL_inputGroupCall.access_hash = tLRPC$GroupCall.access_hash;
            if (z) {
                tLRPC$TL_phone_getGroupParticipants.ids = arrayList;
            } else {
                tLRPC$TL_phone_getGroupParticipants.sources = arrayList;
            }
            tLRPC$TL_phone_getGroupParticipants.offset = "";
            tLRPC$TL_phone_getGroupParticipants.limit = 100;
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new RequestDelegate(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatObject.Call.this.lambda$loadUnknownParticipants$4$ChatObject$Call(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$loadUnknownParticipants$4 */
        public /* synthetic */ void lambda$loadUnknownParticipants$4$ChatObject$Call(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(i, tLObject) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatObject.Call.this.lambda$null$3$ChatObject$Call(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$3 */
        public /* synthetic */ void lambda$null$3$ChatObject$Call(int i, TLObject tLObject) {
            if (this.loadingGuids.remove(Integer.valueOf(i)) && tLObject != null) {
                TLRPC$TL_phone_groupParticipants tLRPC$TL_phone_groupParticipants = (TLRPC$TL_phone_groupParticipants) tLObject;
                this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupParticipants.users, false);
                int size = tLRPC$TL_phone_groupParticipants.participants.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_phone_groupParticipants.participants.get(i2);
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.participants.get(tLRPC$TL_groupCallParticipant.user_id);
                    if (tLRPC$TL_groupCallParticipant2 != null) {
                        this.sortedParticipants.remove(tLRPC$TL_groupCallParticipant2);
                        this.participantsBySources.remove(tLRPC$TL_groupCallParticipant2.source);
                    }
                    this.participants.put(tLRPC$TL_groupCallParticipant.user_id, tLRPC$TL_groupCallParticipant);
                    this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                    this.participantsBySources.put(tLRPC$TL_groupCallParticipant.source, tLRPC$TL_groupCallParticipant);
                    if (this.invitedUsersMap.contains(Integer.valueOf(tLRPC$TL_groupCallParticipant.user_id))) {
                        Integer valueOf = Integer.valueOf(tLRPC$TL_groupCallParticipant.user_id);
                        this.invitedUsersMap.remove(valueOf);
                        this.invitedUsers.remove(valueOf);
                    }
                }
                if (this.call.participants_count < this.participants.size()) {
                    this.call.participants_count = this.participants.size();
                }
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        public void processVoiceLevelsUpdate(int[] iArr, float[] fArr, boolean[] zArr) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
            int currentTime = this.currentAccount.getConnectionsManager().getCurrentTime();
            ArrayList arrayList = null;
            boolean z = false;
            for (int i = 0; i < iArr.length; i++) {
                if (iArr[i] == 0) {
                    tLRPC$TL_groupCallParticipant = this.participants.get(this.currentAccount.getUserConfig().getClientUserId());
                } else {
                    tLRPC$TL_groupCallParticipant = this.participantsBySources.get(iArr[i]);
                }
                if (tLRPC$TL_groupCallParticipant != null) {
                    tLRPC$TL_groupCallParticipant.hasVoice = zArr[i];
                    if (fArr[i] > 0.1f) {
                        if (zArr[i] && tLRPC$TL_groupCallParticipant.active_date + 1 < currentTime) {
                            tLRPC$TL_groupCallParticipant.active_date = currentTime;
                            z = true;
                        }
                        tLRPC$TL_groupCallParticipant.lastSpeakTime = SystemClock.uptimeMillis();
                        tLRPC$TL_groupCallParticipant.amplitude = fArr[i];
                    } else {
                        tLRPC$TL_groupCallParticipant.amplitude = 0.0f;
                    }
                } else {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(iArr[i]));
                }
            }
            if (arrayList != null) {
                loadUnknownParticipants(arrayList, false);
            }
            if (z) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
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

        private void processUpdatesQueue() {
            Collections.sort(this.updatesQueue, $$Lambda$ChatObject$Call$uS_RgiC4ZvgT1MSrUyzF7dHZQHQ.INSTANCE);
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
                        loadMembers(true);
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
            boolean z2;
            if (!z) {
                int size = tLRPC$TL_updateGroupCallParticipants.participants.size();
                int i = 0;
                while (true) {
                    if (i >= size) {
                        z2 = false;
                        break;
                    } else if (tLRPC$TL_updateGroupCallParticipants.participants.get(i).versioned) {
                        z2 = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (!z2 || this.call.version + 1 >= tLRPC$TL_updateGroupCallParticipants.version) {
                    if (z2 && tLRPC$TL_updateGroupCallParticipants.version < this.call.version) {
                        return;
                    }
                } else if (this.reloadingMembers || this.updatesStartWaitTime == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTime) <= 1500) {
                    if (this.updatesStartWaitTime == 0) {
                        this.updatesStartWaitTime = System.currentTimeMillis();
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("add TL_updateGroupCallParticipants to queue " + tLRPC$TL_updateGroupCallParticipants.version);
                    }
                    this.updatesQueue.add(tLRPC$TL_updateGroupCallParticipants);
                    if (this.checkQueueRunnable == null) {
                        $$Lambda$ChatObject$Call$A9Iyjy8qm5XwdmVhsjek1P93bY r12 = new Runnable() {
                            public final void run() {
                                ChatObject.Call.this.checkQueue();
                            }
                        };
                        this.checkQueueRunnable = r12;
                        AndroidUtilities.runOnUIThread(r12, 1500);
                        return;
                    }
                    return;
                } else {
                    this.nextLoadOffset = null;
                    loadMembers(true);
                    return;
                }
            }
            int clientUserId = this.currentAccount.getUserConfig().getClientUserId();
            int size2 = tLRPC$TL_updateGroupCallParticipants.participants.size();
            boolean z3 = false;
            boolean z4 = false;
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_updateGroupCallParticipants.participants.get(i2);
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.participants.get(tLRPC$TL_groupCallParticipant.user_id);
                if (!tLRPC$TL_groupCallParticipant.left) {
                    if (this.invitedUsersMap.contains(Integer.valueOf(tLRPC$TL_groupCallParticipant.user_id))) {
                        Integer valueOf = Integer.valueOf(tLRPC$TL_groupCallParticipant.user_id);
                        this.invitedUsersMap.remove(valueOf);
                        this.invitedUsers.remove(valueOf);
                    }
                    if (tLRPC$TL_groupCallParticipant2 != null) {
                        tLRPC$TL_groupCallParticipant2.flags = tLRPC$TL_groupCallParticipant.flags;
                        tLRPC$TL_groupCallParticipant2.muted = tLRPC$TL_groupCallParticipant.muted;
                        tLRPC$TL_groupCallParticipant2.can_self_unmute = tLRPC$TL_groupCallParticipant.can_self_unmute;
                        tLRPC$TL_groupCallParticipant2.date = tLRPC$TL_groupCallParticipant.date;
                        tLRPC$TL_groupCallParticipant2.active_date = Math.max(tLRPC$TL_groupCallParticipant2.active_date, tLRPC$TL_groupCallParticipant.active_date);
                        int i3 = tLRPC$TL_groupCallParticipant2.source;
                        if (i3 != tLRPC$TL_groupCallParticipant.source) {
                            this.participantsBySources.remove(i3);
                            int i4 = tLRPC$TL_groupCallParticipant.source;
                            tLRPC$TL_groupCallParticipant2.source = i4;
                            this.participantsBySources.put(i4, tLRPC$TL_groupCallParticipant2);
                        }
                    } else {
                        if (tLRPC$TL_groupCallParticipant.just_joined) {
                            int i5 = tLRPC$TL_updateGroupCallParticipants.version;
                            TLRPC$GroupCall tLRPC$GroupCall = this.call;
                            if (i5 != tLRPC$GroupCall.version) {
                                tLRPC$GroupCall.participants_count++;
                            }
                        }
                        this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                        this.participants.put(tLRPC$TL_groupCallParticipant.user_id, tLRPC$TL_groupCallParticipant);
                        this.participantsBySources.put(tLRPC$TL_groupCallParticipant.source, tLRPC$TL_groupCallParticipant);
                    }
                    if (tLRPC$TL_groupCallParticipant.user_id == clientUserId && tLRPC$TL_groupCallParticipant.active_date == 0) {
                        tLRPC$TL_groupCallParticipant.active_date = this.currentAccount.getConnectionsManager().getCurrentTime();
                    }
                } else if (tLRPC$TL_updateGroupCallParticipants.version != this.call.version) {
                    if (tLRPC$TL_groupCallParticipant2 != null) {
                        this.participants.remove(tLRPC$TL_groupCallParticipant.user_id);
                        this.participantsBySources.remove(tLRPC$TL_groupCallParticipant.source);
                        this.sortedParticipants.remove(tLRPC$TL_groupCallParticipant2);
                    }
                    TLRPC$GroupCall tLRPC$GroupCall2 = this.call;
                    int i6 = tLRPC$GroupCall2.participants_count - 1;
                    tLRPC$GroupCall2.participants_count = i6;
                    if (i6 < 0) {
                        tLRPC$GroupCall2.participants_count = 0;
                    }
                }
                if (tLRPC$TL_groupCallParticipant.user_id == clientUserId) {
                    z3 = true;
                    z4 = true;
                } else {
                    z3 = true;
                }
            }
            int i7 = tLRPC$TL_updateGroupCallParticipants.version;
            TLRPC$GroupCall tLRPC$GroupCall3 = this.call;
            if (i7 > tLRPC$GroupCall3.version) {
                tLRPC$GroupCall3.version = i7;
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
                loadMembers(true);
            }
            this.call = tLRPC$TL_updateGroupCall.call;
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
            Collections.sort(this.sortedParticipants, $$Lambda$ChatObject$Call$pEUnGLfTv7izabDhbw_GRqZb_aQ.INSTANCE);
            checkOnlineParticipants();
        }

        static /* synthetic */ int lambda$sortParticipants$6(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2) {
            int i;
            int i2 = tLRPC$TL_groupCallParticipant.active_date;
            if (i2 != 0 && (i = tLRPC$TL_groupCallParticipant2.active_date) != 0) {
                return C$r8$backportedMethods$utility$Integer$2$compare.compare(i, i2);
            }
            if (i2 != 0 && tLRPC$TL_groupCallParticipant2.active_date == 0) {
                return -1;
            }
            if (i2 != 0 || tLRPC$TL_groupCallParticipant2.active_date == 0) {
                return C$r8$backportedMethods$utility$Integer$2$compare.compare(tLRPC$TL_groupCallParticipant2.date, tLRPC$TL_groupCallParticipant.date);
            }
            return 1;
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
            if (r0 == 0) goto L_0x0019
            boolean r0 = r1.creator
            if (r0 != 0) goto L_0x0019
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r1.admin_rights
            if (r0 == 0) goto L_0x0012
            boolean r0 = r0.post_messages
            if (r0 != 0) goto L_0x0019
        L_0x0012:
            boolean r1 = r1.broadcast
            if (r1 != 0) goto L_0x0017
            goto L_0x0019
        L_0x0017:
            r1 = 0
            goto L_0x001a
        L_0x0019:
            r1 = 1
        L_0x001a:
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
