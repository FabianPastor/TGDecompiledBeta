package org.telegram.messenger;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatPhoto;
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
import org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideoSourceGroup;
import org.telegram.tgnet.TLRPC$TL_inputGroupCall;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_phone_editGroupCallTitle;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupParticipants;
import org.telegram.tgnet.TLRPC$TL_phone_groupCall;
import org.telegram.tgnet.TLRPC$TL_phone_groupParticipants;
import org.telegram.tgnet.TLRPC$TL_phone_toggleGroupCallRecord;
import org.telegram.tgnet.TLRPC$TL_updateGroupCall;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.ui.GroupCallActivity;

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
    private static final int MAX_PARTICIPANTS_COUNT = 5000;
    public static final int VIDEO_FRAME_HAS_FRAME = 2;
    public static final int VIDEO_FRAME_NO_FRAME = 0;
    public static final int VIDEO_FRAME_REQUESTING = 1;

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
        private static int videoPointer;
        public int activeVideos;
        public TLRPC$GroupCall call;
        public boolean canStreamVideo;
        public int chatId;
        private Runnable checkQueueRunnable;
        public AccountInstance currentAccount;
        public final SparseArray<TLRPC$TL_groupCallParticipant> currentSpeakingPeers = new SparseArray<>();
        public ArrayList<Integer> invitedUsers = new ArrayList<>();
        public HashSet<Integer> invitedUsersMap = new HashSet<>();
        private long lastGroupCallReloadTime;
        private int lastLoadGuid;
        private boolean loadingGroupCall;
        private HashSet<Integer> loadingGuids = new HashSet<>();
        public boolean loadingMembers;
        private HashSet<Integer> loadingSsrcs = new HashSet<>();
        private HashSet<Integer> loadingUids = new HashSet<>();
        public boolean membersLoadEndReached;
        private String nextLoadOffset;
        public SparseArray<TLRPC$TL_groupCallParticipant> participants = new SparseArray<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsByPresentationSources = new SparseArray<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsBySources = new SparseArray<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsByVideoSources = new SparseArray<>();
        public boolean recording;
        public boolean reloadingMembers;
        public TLRPC$Peer selfPeer;
        public final ArrayList<TLRPC$TL_groupCallParticipant> sortedParticipants = new ArrayList<>();
        public int speakingMembersCount;
        public final HashMap<String, Bitmap> thumbs = new HashMap<>();
        private Runnable typingUpdateRunnable = new Runnable() {
            public final void run() {
                ChatObject.Call.this.lambda$new$0$ChatObject$Call();
            }
        };
        private boolean typingUpdateRunnableScheduled;
        /* access modifiers changed from: private */
        public final Runnable updateCurrentSpeakingRunnable = new Runnable() {
            public void run() {
                long uptimeMillis = SystemClock.uptimeMillis();
                int i = 0;
                boolean z = false;
                while (i < Call.this.currentSpeakingPeers.size()) {
                    int keyAt = Call.this.currentSpeakingPeers.keyAt(i);
                    if (uptimeMillis - Call.this.currentSpeakingPeers.get(keyAt).lastSpeakTime >= 500) {
                        Call.this.currentSpeakingPeers.remove(keyAt);
                        i--;
                        z = true;
                    }
                    i++;
                }
                if (Call.this.currentSpeakingPeers.size() > 0) {
                    AndroidUtilities.runOnUIThread(Call.this.updateCurrentSpeakingRunnable, 550);
                }
                if (z) {
                    Call.this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallSpeakingUsersUpdated, Integer.valueOf(Call.this.chatId), Long.valueOf(Call.this.call.id), Boolean.FALSE);
                }
            }
        };
        private ArrayList<TLRPC$TL_updateGroupCallParticipants> updatesQueue = new ArrayList<>();
        private long updatesStartWaitTime;
        public VideoParticipant videoNotAvailableParticipant;
        private final HashMap<String, VideoParticipant> videoParticipantsCache = new HashMap<>();
        public final ArrayList<TLRPC$TL_groupCallParticipant> visibleParticipants = new ArrayList<>();
        public final ArrayList<VideoParticipant> visibleVideoParticipants = new ArrayList<>();

        public interface OnParticipantsLoad {
            void onLoad(ArrayList<Integer> arrayList);
        }

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
                processAllSources(tLRPC$TL_groupCallParticipant, true);
                i2 = Math.min(i2, tLRPC$TL_groupCallParticipant.date);
            }
            sortParticipants();
            this.nextLoadOffset = tLRPC$TL_phone_groupCall.participants_next_offset;
            loadMembers(true);
            createNoVideoParticipant();
        }

        public void createNoVideoParticipant() {
            if (this.videoNotAvailableParticipant == null) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = new TLRPC$TL_groupCallParticipant();
                TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                tLRPC$TL_groupCallParticipant.peer = tLRPC$TL_peerChannel;
                tLRPC$TL_peerChannel.channel_id = this.chatId;
                tLRPC$TL_groupCallParticipant.muted = true;
                TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo = new TLRPC$TL_groupCallParticipantVideo();
                tLRPC$TL_groupCallParticipant.video = tLRPC$TL_groupCallParticipantVideo;
                tLRPC$TL_groupCallParticipantVideo.paused = true;
                tLRPC$TL_groupCallParticipantVideo.endpoint = "";
                this.videoNotAvailableParticipant = new VideoParticipant(tLRPC$TL_groupCallParticipant, false, false);
            }
        }

        public void addSelfDummyParticipant(boolean z) {
            int selfId = getSelfId();
            if (this.participants.indexOfKey(selfId) < 0) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = new TLRPC$TL_groupCallParticipant();
                tLRPC$TL_groupCallParticipant.peer = this.selfPeer;
                tLRPC$TL_groupCallParticipant.muted = true;
                tLRPC$TL_groupCallParticipant.self = true;
                tLRPC$TL_groupCallParticipant.video_joined = this.call.can_start_video;
                TLRPC$Chat chat = this.currentAccount.getMessagesController().getChat(Integer.valueOf(this.chatId));
                tLRPC$TL_groupCallParticipant.can_self_unmute = !this.call.join_muted || ChatObject.canManageCalls(chat);
                tLRPC$TL_groupCallParticipant.date = this.currentAccount.getConnectionsManager().getCurrentTime();
                if (ChatObject.canManageCalls(chat) || !ChatObject.isChannel(chat) || chat.megagroup || tLRPC$TL_groupCallParticipant.can_self_unmute) {
                    tLRPC$TL_groupCallParticipant.active_date = this.currentAccount.getConnectionsManager().getCurrentTime();
                }
                if (selfId > 0) {
                    TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount.getCurrentAccount()).getUserFull(selfId);
                    if (userFull != null) {
                        tLRPC$TL_groupCallParticipant.about = userFull.about;
                    }
                } else {
                    TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount.getCurrentAccount()).getChatFull(-selfId);
                    if (chatFull != null) {
                        tLRPC$TL_groupCallParticipant.about = chatFull.about;
                    }
                }
                this.participants.put(selfId, tLRPC$TL_groupCallParticipant);
                this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                sortParticipants();
                if (z) {
                    this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
                }
            }
        }

        public void migrateToChat(TLRPC$Chat tLRPC$Chat) {
            this.chatId = tLRPC$Chat.id;
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance != null && sharedInstance.getAccount() == this.currentAccount.getCurrentAccount() && sharedInstance.getChat() != null && sharedInstance.getChat().id == (-this.chatId)) {
                sharedInstance.migrateToChat(tLRPC$Chat);
            }
        }

        public boolean shouldShowPanel() {
            return this.call.participants_count > 0 || isScheduled();
        }

        public boolean isScheduled() {
            return (this.call.flags & 128) != 0;
        }

        private int getSelfId() {
            TLRPC$Peer tLRPC$Peer = this.selfPeer;
            if (tLRPC$Peer != null) {
                return MessageObject.getPeerId(tLRPC$Peer);
            }
            return this.currentAccount.getUserConfig().getClientUserId();
        }

        private void onParticipantsLoad(ArrayList<TLRPC$TL_groupCallParticipant> arrayList, boolean z, String str, String str2, int i, int i2) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2;
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant3 = this.participants.get(getSelfId());
            SparseArray<TLRPC$TL_groupCallParticipant> sparseArray = null;
            if (TextUtils.isEmpty(str)) {
                if (this.participants.size() != 0) {
                    sparseArray = this.participants;
                    this.participants = new SparseArray<>();
                } else {
                    this.participants.clear();
                }
                this.sortedParticipants.clear();
                this.participantsBySources.clear();
                this.participantsByVideoSources.clear();
                this.participantsByPresentationSources.clear();
                this.loadingGuids.clear();
            }
            this.nextLoadOffset = str2;
            if (arrayList.isEmpty() || TextUtils.isEmpty(this.nextLoadOffset)) {
                this.membersLoadEndReached = true;
            }
            if (TextUtils.isEmpty(str)) {
                TLRPC$GroupCall tLRPC$GroupCall = this.call;
                tLRPC$GroupCall.version = i;
                tLRPC$GroupCall.participants_count = i2;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("new participants count " + this.call.participants_count);
                }
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(elapsedRealtime));
            int size = arrayList.size();
            boolean z2 = false;
            for (int i3 = 0; i3 <= size; i3++) {
                if (i3 != size) {
                    tLRPC$TL_groupCallParticipant = arrayList.get(i3);
                    if (tLRPC$TL_groupCallParticipant.self) {
                        z2 = true;
                    }
                } else if (!z || tLRPC$TL_groupCallParticipant3 == null || z2) {
                    ArrayList<TLRPC$TL_groupCallParticipant> arrayList2 = arrayList;
                } else {
                    ArrayList<TLRPC$TL_groupCallParticipant> arrayList3 = arrayList;
                    tLRPC$TL_groupCallParticipant = tLRPC$TL_groupCallParticipant3;
                }
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant4 = this.participants.get(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer));
                if (tLRPC$TL_groupCallParticipant4 != null) {
                    this.sortedParticipants.remove(tLRPC$TL_groupCallParticipant4);
                    processAllSources(tLRPC$TL_groupCallParticipant4, false);
                    if (tLRPC$TL_groupCallParticipant4.self) {
                        tLRPC$TL_groupCallParticipant.lastTypingDate = tLRPC$TL_groupCallParticipant4.active_date;
                    } else {
                        tLRPC$TL_groupCallParticipant.lastTypingDate = Math.max(tLRPC$TL_groupCallParticipant.active_date, tLRPC$TL_groupCallParticipant4.active_date);
                    }
                    if (elapsedRealtime != tLRPC$TL_groupCallParticipant.lastVisibleDate) {
                        tLRPC$TL_groupCallParticipant.active_date = tLRPC$TL_groupCallParticipant.lastTypingDate;
                    }
                } else if (!(sparseArray == null || (tLRPC$TL_groupCallParticipant2 = sparseArray.get(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer))) == null)) {
                    if (tLRPC$TL_groupCallParticipant2.self) {
                        tLRPC$TL_groupCallParticipant.lastTypingDate = tLRPC$TL_groupCallParticipant2.active_date;
                    } else {
                        tLRPC$TL_groupCallParticipant.lastTypingDate = Math.max(tLRPC$TL_groupCallParticipant.active_date, tLRPC$TL_groupCallParticipant2.active_date);
                    }
                    if (elapsedRealtime != tLRPC$TL_groupCallParticipant.lastVisibleDate) {
                        tLRPC$TL_groupCallParticipant.active_date = tLRPC$TL_groupCallParticipant.lastTypingDate;
                    } else {
                        tLRPC$TL_groupCallParticipant.active_date = tLRPC$TL_groupCallParticipant2.active_date;
                    }
                }
                this.participants.put(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer), tLRPC$TL_groupCallParticipant);
                this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                processAllSources(tLRPC$TL_groupCallParticipant, true);
            }
            if (this.call.participants_count < this.participants.size()) {
                this.call.participants_count = this.participants.size();
            }
            sortParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            setParticiapantsVolume();
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
            if (!this.membersLoadEndReached && this.sortedParticipants.size() <= 5000) {
                if (z) {
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
                    ChatObject.Call.this.lambda$loadMembers$1$ChatObject$Call(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$loadMembers$1 */
        public /* synthetic */ void lambda$loadMembers$1$ChatObject$Call(boolean z, TLObject tLObject, TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants) {
            this.loadingMembers = false;
            if (z) {
                this.reloadingMembers = false;
            }
            if (tLObject != null) {
                TLRPC$TL_phone_groupParticipants tLRPC$TL_phone_groupParticipants = (TLRPC$TL_phone_groupParticipants) tLObject;
                this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupParticipants.users, false);
                this.currentAccount.getMessagesController().putChats(tLRPC$TL_phone_groupParticipants.chats, false);
                onParticipantsLoad(tLRPC$TL_phone_groupParticipants.participants, z, tLRPC$TL_phone_getGroupParticipants.offset, tLRPC$TL_phone_groupParticipants.next_offset, tLRPC$TL_phone_groupParticipants.version, tLRPC$TL_phone_groupParticipants.count);
            }
        }

        private void setParticiapantsVolume() {
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance != null && sharedInstance.getAccount() == this.currentAccount.getCurrentAccount() && sharedInstance.getChat() != null && sharedInstance.getChat().id == (-this.chatId)) {
                sharedInstance.setParticipantsVolume();
            }
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
                loadUnknownParticipants(arrayList2, true, (OnParticipantsLoad) null);
            }
            if (z) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        private void loadUnknownParticipants(ArrayList<Integer> arrayList, boolean z, OnParticipantsLoad onParticipantsLoad) {
            TLRPC$InputPeer tLRPC$InputPeer;
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
                            TLRPC$Chat chat = this.currentAccount.getMessagesController().getChat(Integer.valueOf(-num.intValue()));
                            if (chat == null || ChatObject.isChannel(chat)) {
                                tLRPC$InputPeer = new TLRPC$TL_inputPeerChannel();
                                tLRPC$InputPeer.channel_id = -num.intValue();
                            } else {
                                tLRPC$InputPeer = new TLRPC$TL_inputPeerChat();
                                tLRPC$InputPeer.chat_id = -num.intValue();
                            }
                            tLRPC$TL_phone_getGroupParticipants.ids.add(tLRPC$InputPeer);
                        }
                    }
                } else {
                    tLRPC$TL_phone_getGroupParticipants.sources = arrayList;
                }
                tLRPC$TL_phone_getGroupParticipants.offset = "";
                tLRPC$TL_phone_getGroupParticipants.limit = 100;
                this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new RequestDelegate(i2, onParticipantsLoad, arrayList, hashSet) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ ChatObject.Call.OnParticipantsLoad f$2;
                    public final /* synthetic */ ArrayList f$3;
                    public final /* synthetic */ HashSet f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        ChatObject.Call.this.lambda$loadUnknownParticipants$5$ChatObject$Call(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$loadUnknownParticipants$5 */
        public /* synthetic */ void lambda$loadUnknownParticipants$5$ChatObject$Call(int i, OnParticipantsLoad onParticipantsLoad, ArrayList arrayList, HashSet hashSet, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(i, tLObject, onParticipantsLoad, arrayList, hashSet) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ TLObject f$2;
                public final /* synthetic */ ChatObject.Call.OnParticipantsLoad f$3;
                public final /* synthetic */ ArrayList f$4;
                public final /* synthetic */ HashSet f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    ChatObject.Call.this.lambda$loadUnknownParticipants$4$ChatObject$Call(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$loadUnknownParticipants$4 */
        public /* synthetic */ void lambda$loadUnknownParticipants$4$ChatObject$Call(int i, TLObject tLObject, OnParticipantsLoad onParticipantsLoad, ArrayList arrayList, HashSet hashSet) {
            if (this.loadingGuids.remove(Integer.valueOf(i))) {
                if (tLObject != null) {
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
                            processAllSources(tLRPC$TL_groupCallParticipant2, false);
                        }
                        this.participants.put(peerId, tLRPC$TL_groupCallParticipant);
                        this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                        processAllSources(tLRPC$TL_groupCallParticipant, true);
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
                    if (onParticipantsLoad != null) {
                        onParticipantsLoad.onLoad(arrayList);
                    } else {
                        setParticiapantsVolume();
                    }
                }
                hashSet.removeAll(arrayList);
            }
        }

        private void processAllSources(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z) {
            int i;
            int i2 = tLRPC$TL_groupCallParticipant.source;
            if (i2 != 0) {
                if (z) {
                    this.participantsBySources.put(i2, tLRPC$TL_groupCallParticipant);
                } else {
                    this.participantsBySources.remove(i2);
                }
            }
            int i3 = 0;
            while (i3 < 2) {
                TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo = i3 == 0 ? tLRPC$TL_groupCallParticipant.video : tLRPC$TL_groupCallParticipant.presentation;
                if (tLRPC$TL_groupCallParticipantVideo != null) {
                    if (!((2 & tLRPC$TL_groupCallParticipantVideo.flags) == 0 || (i = tLRPC$TL_groupCallParticipantVideo.audio_source) == 0)) {
                        if (z) {
                            this.participantsBySources.put(i, tLRPC$TL_groupCallParticipant);
                        } else {
                            this.participantsBySources.remove(i);
                        }
                    }
                    SparseArray<TLRPC$TL_groupCallParticipant> sparseArray = i3 == 0 ? this.participantsByVideoSources : this.participantsByPresentationSources;
                    int size = tLRPC$TL_groupCallParticipantVideo.source_groups.size();
                    for (int i4 = 0; i4 < size; i4++) {
                        TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup = tLRPC$TL_groupCallParticipantVideo.source_groups.get(i4);
                        int size2 = tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.size();
                        for (int i5 = 0; i5 < size2; i5++) {
                            int intValue = tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.get(i5).intValue();
                            if (z) {
                                sparseArray.put(intValue, tLRPC$TL_groupCallParticipant);
                            } else {
                                sparseArray.remove(intValue);
                            }
                        }
                    }
                    if (z) {
                        if (i3 == 0) {
                            tLRPC$TL_groupCallParticipant.videoEndpoint = tLRPC$TL_groupCallParticipantVideo.endpoint;
                        } else {
                            tLRPC$TL_groupCallParticipant.presentationEndpoint = tLRPC$TL_groupCallParticipantVideo.endpoint;
                        }
                    } else if (i3 == 0) {
                        tLRPC$TL_groupCallParticipant.videoEndpoint = null;
                    } else {
                        tLRPC$TL_groupCallParticipant.presentationEndpoint = null;
                    }
                }
                i3++;
            }
        }

        public void processVoiceLevelsUpdate(int[] iArr, float[] fArr, boolean[] zArr) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
            boolean z;
            ArrayList arrayList;
            int[] iArr2 = iArr;
            int currentTime = this.currentAccount.getConnectionsManager().getCurrentTime();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptimeMillis = SystemClock.uptimeMillis();
            int i = 1;
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(elapsedRealtime));
            ArrayList arrayList2 = null;
            int i2 = 0;
            boolean z2 = false;
            boolean z3 = false;
            while (i2 < iArr2.length) {
                if (iArr2[i2] == 0) {
                    tLRPC$TL_groupCallParticipant = this.participants.get(getSelfId());
                } else {
                    tLRPC$TL_groupCallParticipant = this.participantsBySources.get(iArr2[i2]);
                }
                if (tLRPC$TL_groupCallParticipant != null) {
                    tLRPC$TL_groupCallParticipant.hasVoice = zArr[i2];
                    z = z3;
                    if (zArr[i2] || elapsedRealtime - tLRPC$TL_groupCallParticipant.lastVoiceUpdateTime > 500) {
                        tLRPC$TL_groupCallParticipant.hasVoiceDelayed = zArr[i2];
                        tLRPC$TL_groupCallParticipant.lastVoiceUpdateTime = elapsedRealtime;
                    }
                    int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                    if (fArr[i2] > 0.1f) {
                        if (!zArr[i2] || tLRPC$TL_groupCallParticipant.lastTypingDate + i >= currentTime) {
                            arrayList = arrayList2;
                        } else {
                            arrayList = arrayList2;
                            if (elapsedRealtime != tLRPC$TL_groupCallParticipant.lastVisibleDate) {
                                tLRPC$TL_groupCallParticipant.active_date = currentTime;
                            }
                            tLRPC$TL_groupCallParticipant.lastTypingDate = currentTime;
                            z2 = true;
                        }
                        tLRPC$TL_groupCallParticipant.lastSpeakTime = uptimeMillis;
                        tLRPC$TL_groupCallParticipant.amplitude = fArr[i2];
                        if (this.currentSpeakingPeers.get(peerId, (Object) null) == null) {
                            this.currentSpeakingPeers.put(peerId, tLRPC$TL_groupCallParticipant);
                            z = true;
                        }
                    } else {
                        arrayList = arrayList2;
                        if (uptimeMillis - tLRPC$TL_groupCallParticipant.lastSpeakTime >= 500 && this.currentSpeakingPeers.get(peerId, (Object) null) != null) {
                            this.currentSpeakingPeers.remove(peerId);
                            z = true;
                        }
                        tLRPC$TL_groupCallParticipant.amplitude = 0.0f;
                    }
                } else {
                    arrayList = arrayList2;
                    z = z3;
                    if (iArr2[i2] != 0) {
                        arrayList2 = arrayList == null ? new ArrayList() : arrayList;
                        arrayList2.add(Integer.valueOf(iArr2[i2]));
                        i2++;
                        z3 = z;
                        i = 1;
                    }
                }
                arrayList2 = arrayList;
                i2++;
                z3 = z;
                i = 1;
            }
            ArrayList arrayList3 = arrayList2;
            boolean z4 = z3;
            if (arrayList3 != null) {
                loadUnknownParticipants(arrayList3, false, (OnParticipantsLoad) null);
            }
            if (z2) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
            if (z4) {
                if (this.currentSpeakingPeers.size() > 0) {
                    AndroidUtilities.cancelRunOnUIThread(this.updateCurrentSpeakingRunnable);
                    AndroidUtilities.runOnUIThread(this.updateCurrentSpeakingRunnable, 550);
                }
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallSpeakingUsersUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        public void updateVisibleParticipants() {
            sortParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE, 0L);
        }

        public void clearVideFramesInfo() {
            for (int i = 0; i < this.sortedParticipants.size(); i++) {
                this.sortedParticipants.get(i).hasCameraFrame = 0;
                this.sortedParticipants.get(i).hasPresentationFrame = 0;
                this.sortedParticipants.get(i).videoIndex = 0;
            }
            sortParticipants();
        }

        public void processUnknownVideoParticipants(int[] iArr, OnParticipantsLoad onParticipantsLoad) {
            ArrayList arrayList = null;
            for (int i = 0; i < iArr.length; i++) {
                if (this.participantsBySources.get(iArr[i]) == null && this.participantsByVideoSources.get(iArr[i]) == null && this.participantsByPresentationSources.get(iArr[i]) == null) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(iArr[i]));
                }
            }
            if (arrayList != null) {
                loadUnknownParticipants(arrayList, false, onParticipantsLoad);
            } else {
                onParticipantsLoad.onLoad((ArrayList<Integer>) null);
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
            } else if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChat) {
                TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                this.selfPeer = tLRPC$TL_peerChat;
                tLRPC$TL_peerChat.chat_id = tLRPC$InputPeer.chat_id;
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

        public void reloadGroupCall() {
            TLRPC$TL_phone_getGroupCall tLRPC$TL_phone_getGroupCall = new TLRPC$TL_phone_getGroupCall();
            tLRPC$TL_phone_getGroupCall.call = getInputGroupCall();
            tLRPC$TL_phone_getGroupCall.limit = 100;
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCall, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatObject.Call.this.lambda$reloadGroupCall$8$ChatObject$Call(tLObject, tLRPC$TL_error);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$reloadGroupCall$8 */
        public /* synthetic */ void lambda$reloadGroupCall$8$ChatObject$Call(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                public final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatObject.Call.this.lambda$reloadGroupCall$7$ChatObject$Call(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$reloadGroupCall$7 */
        public /* synthetic */ void lambda$reloadGroupCall$7$ChatObject$Call(TLObject tLObject) {
            if (tLObject instanceof TLRPC$TL_phone_groupCall) {
                TLRPC$TL_phone_groupCall tLRPC$TL_phone_groupCall = (TLRPC$TL_phone_groupCall) tLObject;
                this.call = tLRPC$TL_phone_groupCall.call;
                this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupCall.users, false);
                this.currentAccount.getMessagesController().putChats(tLRPC$TL_phone_groupCall.chats, false);
                ArrayList<TLRPC$TL_groupCallParticipant> arrayList = tLRPC$TL_phone_groupCall.participants;
                String str = tLRPC$TL_phone_groupCall.participants_next_offset;
                TLRPC$GroupCall tLRPC$GroupCall = tLRPC$TL_phone_groupCall.call;
                onParticipantsLoad(arrayList, true, "", str, tLRPC$GroupCall.version, tLRPC$GroupCall.participants_count);
            }
        }

        private void loadGroupCall() {
            if (!this.loadingGroupCall && SystemClock.elapsedRealtime() - this.lastGroupCallReloadTime >= 30000) {
                this.loadingGroupCall = true;
                TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants = new TLRPC$TL_phone_getGroupParticipants();
                tLRPC$TL_phone_getGroupParticipants.call = getInputGroupCall();
                tLRPC$TL_phone_getGroupParticipants.offset = "";
                tLRPC$TL_phone_getGroupParticipants.limit = 1;
                this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        ChatObject.Call.this.lambda$loadGroupCall$10$ChatObject$Call(tLObject, tLRPC$TL_error);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$loadGroupCall$10 */
        public /* synthetic */ void lambda$loadGroupCall$10$ChatObject$Call(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                public final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatObject.Call.this.lambda$loadGroupCall$9$ChatObject$Call(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$loadGroupCall$9 */
        public /* synthetic */ void lambda$loadGroupCall$9$ChatObject$Call(TLObject tLObject) {
            this.lastGroupCallReloadTime = SystemClock.elapsedRealtime();
            this.loadingGroupCall = false;
            if (tLObject != null) {
                TLRPC$TL_phone_groupParticipants tLRPC$TL_phone_groupParticipants = (TLRPC$TL_phone_groupParticipants) tLObject;
                this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupParticipants.users, false);
                this.currentAccount.getMessagesController().putChats(tLRPC$TL_phone_groupParticipants.chats, false);
                TLRPC$GroupCall tLRPC$GroupCall = this.call;
                int i = tLRPC$GroupCall.participants_count;
                int i2 = tLRPC$TL_phone_groupParticipants.count;
                if (i != i2) {
                    tLRPC$GroupCall.participants_count = i2;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("new participants reload count " + this.call.participants_count);
                    }
                    this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
                }
            }
        }

        public void processParticipantsUpdate(TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants, boolean z) {
            int i;
            long j;
            int i2;
            TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo;
            boolean z2;
            TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants2 = tLRPC$TL_updateGroupCallParticipants;
            Object obj = null;
            boolean z3 = false;
            if (!z) {
                int size = tLRPC$TL_updateGroupCallParticipants2.participants.size();
                int i3 = 0;
                while (true) {
                    if (i3 >= size) {
                        z2 = false;
                        break;
                    } else if (tLRPC$TL_updateGroupCallParticipants2.participants.get(i3).versioned) {
                        z2 = true;
                        break;
                    } else {
                        i3++;
                    }
                }
                if (!z2 || this.call.version + 1 >= tLRPC$TL_updateGroupCallParticipants2.version) {
                    if (z2 && tLRPC$TL_updateGroupCallParticipants2.version < this.call.version) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("ignore processParticipantsUpdate because of version");
                            return;
                        }
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
                    loadMembers(true);
                    return;
                }
            }
            int selfId = getSelfId();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (!this.sortedParticipants.isEmpty()) {
                ArrayList<TLRPC$TL_groupCallParticipant> arrayList = this.sortedParticipants;
                i = arrayList.get(arrayList.size() - 1).date;
            } else {
                i = 0;
            }
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(elapsedRealtime));
            int size2 = tLRPC$TL_updateGroupCallParticipants2.participants.size();
            long j2 = 0;
            int i4 = 0;
            boolean z4 = false;
            boolean z5 = false;
            boolean z6 = false;
            boolean z7 = false;
            boolean z8 = false;
            while (i4 < size2) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_updateGroupCallParticipants2.participants.get(i4);
                int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("process participant " + peerId + " left = " + tLRPC$TL_groupCallParticipant.left + " versioned " + tLRPC$TL_groupCallParticipant.versioned + " flags = " + tLRPC$TL_groupCallParticipant.flags + " self = " + selfId + " volume = " + tLRPC$TL_groupCallParticipant.volume);
                }
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.participants.get(peerId);
                if (tLRPC$TL_groupCallParticipant.left) {
                    if (tLRPC$TL_groupCallParticipant2 == null && tLRPC$TL_updateGroupCallParticipants2.version == this.call.version) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("unknowd participant left, reload call");
                        }
                        z4 = true;
                    }
                    if (tLRPC$TL_groupCallParticipant2 != null) {
                        this.participants.remove(peerId);
                        processAllSources(tLRPC$TL_groupCallParticipant2, z3);
                        this.sortedParticipants.remove(tLRPC$TL_groupCallParticipant2);
                        this.visibleParticipants.remove(tLRPC$TL_groupCallParticipant2);
                        if (this.currentSpeakingPeers.get(peerId, obj) != null) {
                            this.currentSpeakingPeers.remove(peerId);
                            z7 = true;
                        }
                        int i5 = 0;
                        while (i5 < this.visibleVideoParticipants.size()) {
                            if (MessageObject.getPeerId(this.visibleVideoParticipants.get(i5).participant.peer) == MessageObject.getPeerId(tLRPC$TL_groupCallParticipant2.peer)) {
                                this.visibleVideoParticipants.remove(i5);
                                i5--;
                            }
                            i5++;
                        }
                    }
                    TLRPC$GroupCall tLRPC$GroupCall = this.call;
                    int i6 = tLRPC$GroupCall.participants_count - 1;
                    tLRPC$GroupCall.participants_count = i6;
                    if (i6 < 0) {
                        tLRPC$GroupCall.participants_count = z3 ? 1 : 0;
                    }
                    i2 = selfId;
                    j = 0;
                } else {
                    if (this.invitedUsersMap.contains(Integer.valueOf(peerId))) {
                        Integer valueOf = Integer.valueOf(peerId);
                        this.invitedUsersMap.remove(valueOf);
                        this.invitedUsers.remove(valueOf);
                    }
                    if (tLRPC$TL_groupCallParticipant2 != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("new participant, update old");
                        }
                        tLRPC$TL_groupCallParticipant2.muted = tLRPC$TL_groupCallParticipant.muted;
                        if (tLRPC$TL_groupCallParticipant.muted) {
                            if (this.currentSpeakingPeers.get(peerId, (Object) null) != null) {
                                this.currentSpeakingPeers.remove(peerId);
                                z7 = true;
                            }
                        }
                        if (!tLRPC$TL_groupCallParticipant.min) {
                            tLRPC$TL_groupCallParticipant2.volume = tLRPC$TL_groupCallParticipant.volume;
                            tLRPC$TL_groupCallParticipant2.muted_by_you = tLRPC$TL_groupCallParticipant.muted_by_you;
                        } else {
                            int i7 = tLRPC$TL_groupCallParticipant.flags;
                            if ((i7 & 128) != 0 && (tLRPC$TL_groupCallParticipant2.flags & 128) == 0) {
                                tLRPC$TL_groupCallParticipant.flags = i7 & -129;
                            }
                            if (tLRPC$TL_groupCallParticipant.volume_by_admin && tLRPC$TL_groupCallParticipant2.volume_by_admin) {
                                tLRPC$TL_groupCallParticipant2.volume = tLRPC$TL_groupCallParticipant.volume;
                            }
                        }
                        tLRPC$TL_groupCallParticipant2.flags = tLRPC$TL_groupCallParticipant.flags;
                        tLRPC$TL_groupCallParticipant2.can_self_unmute = tLRPC$TL_groupCallParticipant.can_self_unmute;
                        tLRPC$TL_groupCallParticipant2.video_joined = tLRPC$TL_groupCallParticipant.video_joined;
                        i2 = selfId;
                        if (tLRPC$TL_groupCallParticipant2.raise_hand_rating == 0 && tLRPC$TL_groupCallParticipant.raise_hand_rating != 0) {
                            tLRPC$TL_groupCallParticipant2.lastRaiseHandDate = SystemClock.elapsedRealtime();
                        }
                        tLRPC$TL_groupCallParticipant2.raise_hand_rating = tLRPC$TL_groupCallParticipant.raise_hand_rating;
                        tLRPC$TL_groupCallParticipant2.date = tLRPC$TL_groupCallParticipant.date;
                        int max = Math.max(tLRPC$TL_groupCallParticipant2.active_date, tLRPC$TL_groupCallParticipant.active_date);
                        tLRPC$TL_groupCallParticipant2.lastTypingDate = max;
                        int i8 = max;
                        if (elapsedRealtime != tLRPC$TL_groupCallParticipant2.lastVisibleDate) {
                            tLRPC$TL_groupCallParticipant2.active_date = i8;
                        }
                        if (tLRPC$TL_groupCallParticipant2.source != tLRPC$TL_groupCallParticipant.source || !isSameVideo(tLRPC$TL_groupCallParticipant2.video, tLRPC$TL_groupCallParticipant.video) || !isSameVideo(tLRPC$TL_groupCallParticipant2.presentation, tLRPC$TL_groupCallParticipant.presentation)) {
                            processAllSources(tLRPC$TL_groupCallParticipant2, false);
                            tLRPC$TL_groupCallParticipant2.video = tLRPC$TL_groupCallParticipant.video;
                            tLRPC$TL_groupCallParticipant2.presentation = tLRPC$TL_groupCallParticipant.presentation;
                            tLRPC$TL_groupCallParticipant2.source = tLRPC$TL_groupCallParticipant.source;
                            processAllSources(tLRPC$TL_groupCallParticipant2, true);
                            tLRPC$TL_groupCallParticipant.presentationEndpoint = tLRPC$TL_groupCallParticipant2.presentationEndpoint;
                            tLRPC$TL_groupCallParticipant.videoEndpoint = tLRPC$TL_groupCallParticipant2.videoEndpoint;
                            tLRPC$TL_groupCallParticipant.videoIndex = tLRPC$TL_groupCallParticipant2.videoIndex;
                        } else {
                            TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo2 = tLRPC$TL_groupCallParticipant2.video;
                            if (!(tLRPC$TL_groupCallParticipantVideo2 == null || (tLRPC$TL_groupCallParticipantVideo = tLRPC$TL_groupCallParticipant.video) == null)) {
                                tLRPC$TL_groupCallParticipantVideo2.paused = tLRPC$TL_groupCallParticipantVideo.paused;
                            }
                        }
                        j = 0;
                    } else {
                        i2 = selfId;
                        if (tLRPC$TL_groupCallParticipant.just_joined) {
                            if (peerId != i2) {
                                j2 = (long) peerId;
                            }
                            TLRPC$GroupCall tLRPC$GroupCall2 = this.call;
                            tLRPC$GroupCall2.participants_count++;
                            if (tLRPC$TL_updateGroupCallParticipants2.version == tLRPC$GroupCall2.version) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("new participant, just joined, reload call");
                                }
                                z4 = true;
                            } else if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("new participant, just joined");
                            }
                        }
                        j = 0;
                        if (tLRPC$TL_groupCallParticipant.raise_hand_rating != 0) {
                            tLRPC$TL_groupCallParticipant.lastRaiseHandDate = SystemClock.elapsedRealtime();
                        }
                        if (peerId == i2 || this.sortedParticipants.size() < 20 || tLRPC$TL_groupCallParticipant.date <= i || tLRPC$TL_groupCallParticipant.active_date != 0 || tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted || !tLRPC$TL_groupCallParticipant.min || this.membersLoadEndReached) {
                            this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                        }
                        this.participants.put(peerId, tLRPC$TL_groupCallParticipant);
                        processAllSources(tLRPC$TL_groupCallParticipant, true);
                    }
                    if (peerId == i2 && tLRPC$TL_groupCallParticipant.active_date == 0 && (tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted)) {
                        tLRPC$TL_groupCallParticipant.active_date = this.currentAccount.getConnectionsManager().getCurrentTime();
                    }
                    z6 = true;
                }
                if (peerId == i2) {
                    z8 = true;
                }
                i4++;
                selfId = i2;
                long j3 = j;
                obj = null;
                z3 = false;
                z5 = true;
            }
            int i9 = tLRPC$TL_updateGroupCallParticipants2.version;
            TLRPC$GroupCall tLRPC$GroupCall3 = this.call;
            if (i9 > tLRPC$GroupCall3.version) {
                tLRPC$GroupCall3.version = i9;
                if (!z) {
                    processUpdatesQueue();
                }
            }
            if (this.call.participants_count < this.participants.size()) {
                this.call.participants_count = this.participants.size();
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("new participants count after update " + this.call.participants_count);
            }
            if (z4) {
                loadGroupCall();
            }
            if (z5) {
                if (z6) {
                    sortParticipants();
                }
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.valueOf(z8), Long.valueOf(j2));
            }
            if (z7) {
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallSpeakingUsersUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        private boolean isSameVideo(TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo, TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo2) {
            if ((tLRPC$TL_groupCallParticipantVideo == null && tLRPC$TL_groupCallParticipantVideo2 != null) || (tLRPC$TL_groupCallParticipantVideo != null && tLRPC$TL_groupCallParticipantVideo2 == null)) {
                return false;
            }
            if (!(tLRPC$TL_groupCallParticipantVideo == null || tLRPC$TL_groupCallParticipantVideo2 == null)) {
                if (!TextUtils.equals(tLRPC$TL_groupCallParticipantVideo.endpoint, tLRPC$TL_groupCallParticipantVideo2.endpoint) || tLRPC$TL_groupCallParticipantVideo.source_groups.size() != tLRPC$TL_groupCallParticipantVideo2.source_groups.size()) {
                    return false;
                }
                int size = tLRPC$TL_groupCallParticipantVideo.source_groups.size();
                for (int i = 0; i < size; i++) {
                    TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup = tLRPC$TL_groupCallParticipantVideo.source_groups.get(i);
                    TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup2 = tLRPC$TL_groupCallParticipantVideo2.source_groups.get(i);
                    if (!TextUtils.equals(tLRPC$TL_groupCallParticipantVideoSourceGroup.semantics, tLRPC$TL_groupCallParticipantVideoSourceGroup2.semantics) || tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.size() != tLRPC$TL_groupCallParticipantVideoSourceGroup2.sources.size()) {
                        return false;
                    }
                    int size2 = tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        if (!tLRPC$TL_groupCallParticipantVideoSourceGroup2.sources.contains(tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.get(i2))) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        public void processGroupCallUpdate(TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall) {
            if (this.call.version < tLRPC$TL_updateGroupCall.call.version) {
                this.nextLoadOffset = null;
                loadMembers(true);
            }
            this.call = tLRPC$TL_updateGroupCall.call;
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.participants.get(getSelfId());
            this.recording = this.call.record_start_date != 0;
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
        }

        public TLRPC$TL_inputGroupCall getInputGroupCall() {
            TLRPC$TL_inputGroupCall tLRPC$TL_inputGroupCall = new TLRPC$TL_inputGroupCall();
            TLRPC$GroupCall tLRPC$GroupCall = this.call;
            tLRPC$TL_inputGroupCall.id = tLRPC$GroupCall.id;
            tLRPC$TL_inputGroupCall.access_hash = tLRPC$GroupCall.access_hash;
            return tLRPC$TL_inputGroupCall;
        }

        public static boolean videoIsActive(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z, Call call2) {
            VoIPService sharedInstance;
            if (tLRPC$TL_groupCallParticipant == null || (sharedInstance = VoIPService.getSharedInstance()) == null) {
                return false;
            }
            if (!tLRPC$TL_groupCallParticipant.self) {
                VideoParticipant videoParticipant = call2.videoNotAvailableParticipant;
                if ((videoParticipant == null || videoParticipant.participant != tLRPC$TL_groupCallParticipant) && call2.participants.get(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) == null) {
                    return false;
                }
                if (z) {
                    if (tLRPC$TL_groupCallParticipant.presentation != null) {
                        return true;
                    }
                    return false;
                } else if (tLRPC$TL_groupCallParticipant.video != null) {
                    return true;
                } else {
                    return false;
                }
            } else if (sharedInstance.getVideoState(z) == 2) {
                return true;
            } else {
                return false;
            }
        }

        public void sortParticipants() {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
            int size;
            VideoParticipant videoParticipant;
            int i;
            this.visibleVideoParticipants.clear();
            this.visibleParticipants.clear();
            TLRPC$Chat chat = this.currentAccount.getMessagesController().getChat(Integer.valueOf(this.chatId));
            boolean canManageCalls = ChatObject.canManageCalls(chat);
            int selfId = getSelfId();
            VoIPService.getSharedInstance();
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.participants.get(selfId);
            this.canStreamVideo = tLRPC$TL_groupCallParticipant2 != null && tLRPC$TL_groupCallParticipant2.video_joined;
            this.activeVideos = 0;
            int size2 = this.sortedParticipants.size();
            boolean z = false;
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant3 = this.sortedParticipants.get(i2);
                boolean videoIsActive = videoIsActive(tLRPC$TL_groupCallParticipant3, false, this);
                boolean videoIsActive2 = videoIsActive(tLRPC$TL_groupCallParticipant3, true, this);
                boolean z2 = tLRPC$TL_groupCallParticipant3.self;
                if (!z2) {
                    if (videoIsActive) {
                        this.activeVideos++;
                    }
                    if (videoIsActive2) {
                        this.activeVideos++;
                    }
                }
                if (videoIsActive || videoIsActive2) {
                    if (!this.canStreamVideo) {
                        tLRPC$TL_groupCallParticipant3.videoIndex = 0;
                    } else if (tLRPC$TL_groupCallParticipant3.videoIndex == 0) {
                        if (z2) {
                            tLRPC$TL_groupCallParticipant3.videoIndex = Integer.MAX_VALUE;
                        } else {
                            int i3 = videoPointer + 1;
                            videoPointer = i3;
                            tLRPC$TL_groupCallParticipant3.videoIndex = i3;
                        }
                    }
                    z = true;
                } else if (z2 || !this.canStreamVideo || (tLRPC$TL_groupCallParticipant3.video == null && tLRPC$TL_groupCallParticipant3.presentation == null)) {
                    tLRPC$TL_groupCallParticipant3.videoIndex = 0;
                }
            }
            Collections.sort(this.sortedParticipants, new Object(selfId, canManageCalls) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final int compare(Object obj, Object obj2) {
                    return ChatObject.Call.this.lambda$sortParticipants$11$ChatObject$Call(this.f$1, this.f$2, (TLRPC$TL_groupCallParticipant) obj, (TLRPC$TL_groupCallParticipant) obj2);
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
            if (this.sortedParticipants.isEmpty()) {
                tLRPC$TL_groupCallParticipant = null;
            } else {
                ArrayList<TLRPC$TL_groupCallParticipant> arrayList = this.sortedParticipants;
                tLRPC$TL_groupCallParticipant = arrayList.get(arrayList.size() - 1);
            }
            if ((videoIsActive(tLRPC$TL_groupCallParticipant, false, this) || videoIsActive(tLRPC$TL_groupCallParticipant, true, this)) && (i = this.call.unmuted_video_count) > this.activeVideos) {
                this.activeVideos = i;
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                if (sharedInstance != null && sharedInstance.groupCall == this) {
                    if (sharedInstance.getVideoState(false) == 2) {
                        this.activeVideos--;
                    }
                    if (sharedInstance.getVideoState(true) == 2) {
                        this.activeVideos--;
                    }
                }
            }
            if (this.sortedParticipants.size() > 5000 && (!ChatObject.canManageCalls(chat) || tLRPC$TL_groupCallParticipant.raise_hand_rating == 0)) {
                int size3 = this.sortedParticipants.size();
                for (int i4 = 5000; i4 < size3; i4++) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant4 = this.sortedParticipants.get(5000);
                    if (tLRPC$TL_groupCallParticipant4.raise_hand_rating == 0) {
                        processAllSources(tLRPC$TL_groupCallParticipant4, false);
                        this.participants.remove(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant4.peer));
                        this.sortedParticipants.remove(5000);
                    }
                }
            }
            checkOnlineParticipants();
            if (!this.canStreamVideo && z && (videoParticipant = this.videoNotAvailableParticipant) != null) {
                this.visibleVideoParticipants.add(videoParticipant);
            }
            int i5 = 0;
            for (int i6 = 0; i6 < this.sortedParticipants.size(); i6++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant5 = this.sortedParticipants.get(i6);
                if (!this.canStreamVideo || tLRPC$TL_groupCallParticipant5.videoIndex == 0) {
                    this.visibleParticipants.add(tLRPC$TL_groupCallParticipant5);
                } else {
                    if (!tLRPC$TL_groupCallParticipant5.self && videoIsActive(tLRPC$TL_groupCallParticipant5, true, this) && videoIsActive(tLRPC$TL_groupCallParticipant5, false, this)) {
                        VideoParticipant videoParticipant2 = this.videoParticipantsCache.get(tLRPC$TL_groupCallParticipant5.videoEndpoint);
                        if (videoParticipant2 == null) {
                            videoParticipant2 = new VideoParticipant(tLRPC$TL_groupCallParticipant5, false, true);
                            this.videoParticipantsCache.put(tLRPC$TL_groupCallParticipant5.videoEndpoint, videoParticipant2);
                        } else {
                            videoParticipant2.participant = tLRPC$TL_groupCallParticipant5;
                            videoParticipant2.presentation = false;
                            videoParticipant2.hasSame = true;
                        }
                        VideoParticipant videoParticipant3 = this.videoParticipantsCache.get(tLRPC$TL_groupCallParticipant5.presentationEndpoint);
                        if (videoParticipant3 == null) {
                            videoParticipant3 = new VideoParticipant(tLRPC$TL_groupCallParticipant5, true, true);
                        } else {
                            videoParticipant3.participant = tLRPC$TL_groupCallParticipant5;
                            videoParticipant3.presentation = true;
                            videoParticipant3.hasSame = true;
                        }
                        this.visibleVideoParticipants.add(videoParticipant2);
                        if (videoParticipant2.aspectRatio > 1.0f) {
                            i5 = this.visibleVideoParticipants.size() - 1;
                        }
                        this.visibleVideoParticipants.add(videoParticipant3);
                        if (videoParticipant3.aspectRatio > 1.0f) {
                            size = this.visibleVideoParticipants.size();
                        }
                    } else if (tLRPC$TL_groupCallParticipant5.self) {
                        if (videoIsActive(tLRPC$TL_groupCallParticipant5, true, this)) {
                            this.visibleVideoParticipants.add(new VideoParticipant(tLRPC$TL_groupCallParticipant5, true, false));
                        }
                        if (videoIsActive(tLRPC$TL_groupCallParticipant5, false, this)) {
                            this.visibleVideoParticipants.add(new VideoParticipant(tLRPC$TL_groupCallParticipant5, false, false));
                        }
                    } else {
                        boolean videoIsActive3 = videoIsActive(tLRPC$TL_groupCallParticipant5, true, this);
                        VideoParticipant videoParticipant4 = this.videoParticipantsCache.get(videoIsActive3 ? tLRPC$TL_groupCallParticipant5.presentationEndpoint : tLRPC$TL_groupCallParticipant5.videoEndpoint);
                        if (videoParticipant4 == null) {
                            videoParticipant4 = new VideoParticipant(tLRPC$TL_groupCallParticipant5, videoIsActive3, false);
                            this.videoParticipantsCache.put(videoIsActive3 ? tLRPC$TL_groupCallParticipant5.presentationEndpoint : tLRPC$TL_groupCallParticipant5.videoEndpoint, videoParticipant4);
                        } else {
                            videoParticipant4.participant = tLRPC$TL_groupCallParticipant5;
                            videoParticipant4.presentation = videoIsActive3;
                            videoParticipant4.hasSame = false;
                        }
                        this.visibleVideoParticipants.add(videoParticipant4);
                        if (videoParticipant4.aspectRatio > 1.0f) {
                            size = this.visibleVideoParticipants.size();
                        }
                    }
                    i5 = size - 1;
                }
            }
            if (!GroupCallActivity.isLandscapeMode && this.visibleVideoParticipants.size() % 2 == 1) {
                this.visibleVideoParticipants.add(this.visibleVideoParticipants.remove(i5));
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$sortParticipants$11 */
        public /* synthetic */ int lambda$sortParticipants$11$ChatObject$Call(int i, boolean z, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2) {
            int i2;
            int i3 = tLRPC$TL_groupCallParticipant.videoIndex;
            boolean z2 = false;
            boolean z3 = i3 > 0;
            int i4 = tLRPC$TL_groupCallParticipant2.videoIndex;
            if (i4 > 0) {
                z2 = true;
            }
            if (z3 && z2) {
                return i4 - i3;
            }
            if (z3) {
                return -1;
            }
            if (z2) {
                return 1;
            }
            int i5 = tLRPC$TL_groupCallParticipant.active_date;
            if (i5 != 0 && (i2 = tLRPC$TL_groupCallParticipant2.active_date) != 0) {
                return ChatObject$Call$$ExternalSynthetic0.m0(i2, i5);
            }
            if (i5 != 0) {
                return -1;
            }
            if (tLRPC$TL_groupCallParticipant2.active_date != 0) {
                return 1;
            }
            if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == i) {
                return -1;
            }
            if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant2.peer) == i) {
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
            if (this.call.join_date_asc) {
                return ChatObject$Call$$ExternalSynthetic0.m0(tLRPC$TL_groupCallParticipant.date, tLRPC$TL_groupCallParticipant2.date);
            }
            return ChatObject$Call$$ExternalSynthetic0.m0(tLRPC$TL_groupCallParticipant2.date, tLRPC$TL_groupCallParticipant.date);
        }

        public boolean canRecordVideo() {
            if (!this.canStreamVideo) {
                return false;
            }
            int i = this.activeVideos;
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance != null && sharedInstance.groupCall == this) {
                if (sharedInstance.getVideoState(false) == 2) {
                    i++;
                }
                if (sharedInstance.getVideoState(true) == 2) {
                    i++;
                }
            }
            if (i < this.call.unmuted_video_limit) {
                return true;
            }
            return false;
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
                    ChatObject.Call.this.lambda$toggleRecord$12$ChatObject$Call(tLObject, tLRPC$TL_error);
                }
            });
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$toggleRecord$12 */
        public /* synthetic */ void lambda$toggleRecord$12$ChatObject$Call(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = r0.photo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean hasPhoto(org.telegram.tgnet.TLRPC$Chat r0) {
        /*
            if (r0 == 0) goto L_0x000c
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r0.photo
            if (r0 == 0) goto L_0x000c
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatPhotoEmpty
            if (r0 != 0) goto L_0x000c
            r0 = 1
            goto L_0x000d
        L_0x000c:
            r0 = 0
        L_0x000d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.hasPhoto(org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static TLRPC$ChatPhoto getPhoto(TLRPC$Chat tLRPC$Chat) {
        if (hasPhoto(tLRPC$Chat)) {
            return tLRPC$Chat.photo;
        }
        return null;
    }

    public static class VideoParticipant {
        public float aspectRatio;
        public boolean hasSame;
        public TLRPC$TL_groupCallParticipant participant;
        public boolean presentation;

        public VideoParticipant(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z, boolean z2) {
            this.participant = tLRPC$TL_groupCallParticipant;
            this.presentation = z;
            this.hasSame = z2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            VideoParticipant videoParticipant = (VideoParticipant) obj;
            if (this.presentation == videoParticipant.presentation && MessageObject.getPeerId(this.participant.peer) == MessageObject.getPeerId(videoParticipant.participant.peer)) {
                return true;
            }
            return false;
        }

        public void setAspectRatio(float f, Call call) {
            if (this.aspectRatio != f) {
                this.aspectRatio = f;
                if (!GroupCallActivity.isLandscapeMode && call.visibleVideoParticipants.size() % 2 == 1) {
                    call.updateVisibleParticipants();
                }
            }
        }
    }
}
