package org.telegram.messenger;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.internal.icing.zzby$$ExternalSyntheticBackport0;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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

    public static class Call {
        public static final int RECORD_TYPE_AUDIO = 0;
        public static final int RECORD_TYPE_VIDEO_LANDSCAPE = 2;
        public static final int RECORD_TYPE_VIDEO_PORTAIT = 1;
        private static int videoPointer;
        public int activeVideos;
        public TLRPC.GroupCall call;
        public boolean canStreamVideo;
        public long chatId;
        private Runnable checkQueueRunnable;
        public AccountInstance currentAccount;
        public final LongSparseArray<TLRPC.TL_groupCallParticipant> currentSpeakingPeers = new LongSparseArray<>();
        public ArrayList<Long> invitedUsers = new ArrayList<>();
        public HashSet<Long> invitedUsersMap = new HashSet<>();
        private long lastGroupCallReloadTime;
        private int lastLoadGuid;
        public boolean loadedRtmpStreamParticipant;
        private boolean loadingGroupCall;
        private HashSet<Integer> loadingGuids = new HashSet<>();
        public boolean loadingMembers;
        private HashSet<Long> loadingSsrcs = new HashSet<>();
        private HashSet<Long> loadingUids = new HashSet<>();
        public boolean membersLoadEndReached;
        private String nextLoadOffset;
        public LongSparseArray<TLRPC.TL_groupCallParticipant> participants = new LongSparseArray<>();
        public SparseArray<TLRPC.TL_groupCallParticipant> participantsByPresentationSources = new SparseArray<>();
        public SparseArray<TLRPC.TL_groupCallParticipant> participantsBySources = new SparseArray<>();
        public SparseArray<TLRPC.TL_groupCallParticipant> participantsByVideoSources = new SparseArray<>();
        public boolean recording;
        public boolean reloadingMembers;
        public VideoParticipant rtmpStreamParticipant;
        public TLRPC.Peer selfPeer;
        public final ArrayList<TLRPC.TL_groupCallParticipant> sortedParticipants = new ArrayList<>();
        public int speakingMembersCount;
        public final HashMap<String, Bitmap> thumbs = new HashMap<>();
        private Runnable typingUpdateRunnable = new ChatObject$Call$$ExternalSyntheticLambda7(this);
        private boolean typingUpdateRunnableScheduled;
        /* access modifiers changed from: private */
        public final Runnable updateCurrentSpeakingRunnable = new Runnable() {
            public void run() {
                long uptime = SystemClock.uptimeMillis();
                boolean update = false;
                int i = 0;
                while (i < Call.this.currentSpeakingPeers.size()) {
                    long key = Call.this.currentSpeakingPeers.keyAt(i);
                    if (uptime - Call.this.currentSpeakingPeers.get(key).lastSpeakTime >= 500) {
                        update = true;
                        Call.this.currentSpeakingPeers.remove(key);
                        if (key > 0) {
                            TLRPC.User user = MessagesController.getInstance(Call.this.currentAccount.getCurrentAccount()).getUser(Long.valueOf(key));
                            StringBuilder sb = new StringBuilder();
                            sb.append("remove from speaking ");
                            sb.append(key);
                            sb.append(" ");
                            sb.append(user == null ? null : user.first_name);
                            Log.d("GroupCall", sb.toString());
                        } else {
                            TLRPC.Chat user2 = MessagesController.getInstance(Call.this.currentAccount.getCurrentAccount()).getChat(Long.valueOf(-key));
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("remove from speaking ");
                            sb2.append(key);
                            sb2.append(" ");
                            sb2.append(user2 == null ? null : user2.title);
                            Log.d("GroupCall", sb2.toString());
                        }
                        i--;
                    }
                    i++;
                }
                if (Call.this.currentSpeakingPeers.size() > 0) {
                    AndroidUtilities.runOnUIThread(Call.this.updateCurrentSpeakingRunnable, 550);
                }
                if (update) {
                    Call.this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallSpeakingUsersUpdated, Long.valueOf(Call.this.chatId), Long.valueOf(Call.this.call.id), false);
                }
            }
        };
        private ArrayList<TLRPC.TL_updateGroupCallParticipants> updatesQueue = new ArrayList<>();
        private long updatesStartWaitTime;
        public VideoParticipant videoNotAvailableParticipant;
        private final HashMap<String, VideoParticipant> videoParticipantsCache = new HashMap<>();
        public final ArrayList<TLRPC.TL_groupCallParticipant> visibleParticipants = new ArrayList<>();
        public final ArrayList<VideoParticipant> visibleVideoParticipants = new ArrayList<>();

        @Retention(RetentionPolicy.SOURCE)
        public @interface RecordType {
        }

        public interface OnParticipantsLoad {
            void onLoad(ArrayList<Long> arrayList);
        }

        /* renamed from: lambda$new$0$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1788lambda$new$0$orgtelegrammessengerChatObject$Call() {
            this.typingUpdateRunnableScheduled = false;
            checkOnlineParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallTypingsUpdated, new Object[0]);
        }

        public void setCall(AccountInstance account, long chatId2, TLRPC.TL_phone_groupCall groupCall) {
            this.chatId = chatId2;
            this.currentAccount = account;
            TLRPC.GroupCall groupCall2 = groupCall.call;
            this.call = groupCall2;
            this.recording = groupCall2.record_start_date != 0;
            int date = Integer.MAX_VALUE;
            int N = groupCall.participants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant participant = groupCall.participants.get(a);
                this.participants.put(MessageObject.getPeerId(participant.peer), participant);
                this.sortedParticipants.add(participant);
                processAllSources(participant, true);
                date = Math.min(date, participant.date);
            }
            sortParticipants();
            this.nextLoadOffset = groupCall.participants_next_offset;
            loadMembers(true);
            createNoVideoParticipant();
            if (this.call.rtmp_stream) {
                createRtmpStreamParticipant(Collections.emptyList());
            }
        }

        public void createRtmpStreamParticipant(List<TLRPC.TL_groupCallStreamChannel> channels) {
            if (!this.loadedRtmpStreamParticipant || this.rtmpStreamParticipant == null) {
                VideoParticipant videoParticipant = this.rtmpStreamParticipant;
                TLRPC.TL_groupCallParticipant participant = videoParticipant != null ? videoParticipant.participant : new TLRPC.TL_groupCallParticipant();
                participant.peer = new TLRPC.TL_peerChat();
                participant.peer.channel_id = this.chatId;
                participant.video = new TLRPC.TL_groupCallParticipantVideo();
                TLRPC.TL_groupCallParticipantVideoSourceGroup sourceGroup = new TLRPC.TL_groupCallParticipantVideoSourceGroup();
                sourceGroup.semantics = "SIM";
                for (TLRPC.TL_groupCallStreamChannel channel : channels) {
                    sourceGroup.sources.add(Integer.valueOf(channel.channel));
                }
                participant.video.source_groups.add(sourceGroup);
                participant.video.endpoint = "unified";
                participant.videoEndpoint = "unified";
                this.rtmpStreamParticipant = new VideoParticipant(participant, false, false);
                sortParticipants();
                AndroidUtilities.runOnUIThread(new ChatObject$Call$$ExternalSyntheticLambda6(this));
            }
        }

        /* renamed from: lambda$createRtmpStreamParticipant$1$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1781x5582b510() {
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
        }

        public void createNoVideoParticipant() {
            if (this.videoNotAvailableParticipant == null) {
                TLRPC.TL_groupCallParticipant noVideoParticipant = new TLRPC.TL_groupCallParticipant();
                noVideoParticipant.peer = new TLRPC.TL_peerChannel();
                noVideoParticipant.peer.channel_id = this.chatId;
                noVideoParticipant.muted = true;
                noVideoParticipant.video = new TLRPC.TL_groupCallParticipantVideo();
                noVideoParticipant.video.paused = true;
                noVideoParticipant.video.endpoint = "";
                this.videoNotAvailableParticipant = new VideoParticipant(noVideoParticipant, false, false);
            }
        }

        public void addSelfDummyParticipant(boolean notify) {
            long selfId = getSelfId();
            if (this.participants.indexOfKey(selfId) < 0) {
                TLRPC.TL_groupCallParticipant selfDummyParticipant = new TLRPC.TL_groupCallParticipant();
                selfDummyParticipant.peer = this.selfPeer;
                selfDummyParticipant.muted = true;
                selfDummyParticipant.self = true;
                selfDummyParticipant.video_joined = this.call.can_start_video;
                TLRPC.Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(this.chatId));
                selfDummyParticipant.can_self_unmute = !this.call.join_muted || ChatObject.canManageCalls(chat);
                selfDummyParticipant.date = this.currentAccount.getConnectionsManager().getCurrentTime();
                if (ChatObject.canManageCalls(chat) || !ChatObject.isChannel(chat) || chat.megagroup || selfDummyParticipant.can_self_unmute) {
                    selfDummyParticipant.active_date = this.currentAccount.getConnectionsManager().getCurrentTime();
                }
                if (selfId > 0) {
                    TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount.getCurrentAccount()).getUserFull(selfId);
                    if (userFull != null) {
                        selfDummyParticipant.about = userFull.about;
                    }
                } else {
                    TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount.getCurrentAccount()).getChatFull(-selfId);
                    if (chatFull != null) {
                        selfDummyParticipant.about = chatFull.about;
                    }
                }
                this.participants.put(selfId, selfDummyParticipant);
                this.sortedParticipants.add(selfDummyParticipant);
                sortParticipants();
                if (notify) {
                    this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
                }
            }
        }

        public void migrateToChat(TLRPC.Chat chat) {
            this.chatId = chat.id;
            VoIPService voIPService = VoIPService.getSharedInstance();
            if (voIPService != null && voIPService.getAccount() == this.currentAccount.getCurrentAccount() && voIPService.getChat() != null && voIPService.getChat().id == (-this.chatId)) {
                voIPService.migrateToChat(chat);
            }
        }

        public boolean shouldShowPanel() {
            return this.call.participants_count > 0 || this.call.rtmp_stream || isScheduled();
        }

        public boolean isScheduled() {
            return (this.call.flags & 128) != 0;
        }

        private long getSelfId() {
            TLRPC.Peer peer = this.selfPeer;
            if (peer != null) {
                return MessageObject.getPeerId(peer);
            }
            return this.currentAccount.getUserConfig().getClientUserId();
        }

        private void onParticipantsLoad(ArrayList<TLRPC.TL_groupCallParticipant> loadedParticipants, boolean fromBegin, String reqOffset, String nextOffset, int version, int participantCount) {
            TLRPC.TL_groupCallParticipant oldSelf;
            long selfId;
            LongSparseArray<TLRPC.TL_groupCallParticipant> old;
            TLRPC.TL_groupCallParticipant participant;
            TLRPC.TL_groupCallParticipant oldParticipant;
            TLRPC.TL_groupCallParticipant oldParticipant2;
            LongSparseArray<TLRPC.TL_groupCallParticipant> old2 = null;
            long selfId2 = getSelfId();
            TLRPC.TL_groupCallParticipant oldSelf2 = this.participants.get(selfId2);
            if (TextUtils.isEmpty(reqOffset)) {
                if (this.participants.size() != 0) {
                    old2 = this.participants;
                    this.participants = new LongSparseArray<>();
                } else {
                    this.participants.clear();
                }
                this.sortedParticipants.clear();
                this.participantsBySources.clear();
                this.participantsByVideoSources.clear();
                this.participantsByPresentationSources.clear();
                this.loadingGuids.clear();
            }
            this.nextLoadOffset = nextOffset;
            if (loadedParticipants.isEmpty() || TextUtils.isEmpty(this.nextLoadOffset)) {
                this.membersLoadEndReached = true;
            }
            if (TextUtils.isEmpty(reqOffset)) {
                this.call.version = version;
                this.call.participants_count = participantCount;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("new participants count " + this.call.participants_count);
                }
            } else {
                int i = version;
                int i2 = participantCount;
            }
            long time = SystemClock.elapsedRealtime();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(time));
            boolean hasSelf = false;
            int a = 0;
            int N = loadedParticipants.size();
            while (a <= N) {
                if (a != N) {
                    participant = loadedParticipants.get(a);
                    if (participant.self) {
                        hasSelf = true;
                    }
                } else if (!fromBegin || oldSelf2 == null || hasSelf) {
                    ArrayList<TLRPC.TL_groupCallParticipant> arrayList = loadedParticipants;
                    old = old2;
                    selfId = selfId2;
                    oldSelf = oldSelf2;
                    a++;
                    old2 = old;
                    selfId2 = selfId;
                    oldSelf2 = oldSelf;
                } else {
                    participant = oldSelf2;
                    ArrayList<TLRPC.TL_groupCallParticipant> arrayList2 = loadedParticipants;
                }
                selfId = selfId2;
                TLRPC.TL_groupCallParticipant oldParticipant3 = this.participants.get(MessageObject.getPeerId(participant.peer));
                if (oldParticipant3 != null) {
                    this.sortedParticipants.remove(oldParticipant3);
                    processAllSources(oldParticipant3, false);
                    if (oldParticipant3.self) {
                        participant.lastTypingDate = oldParticipant3.active_date;
                    } else {
                        participant.lastTypingDate = Math.max(participant.active_date, oldParticipant3.active_date);
                    }
                    oldParticipant2 = oldParticipant3;
                    if (time != participant.lastVisibleDate) {
                        participant.active_date = participant.lastTypingDate;
                        oldSelf = oldSelf2;
                    } else {
                        oldSelf = oldSelf2;
                    }
                } else {
                    oldParticipant2 = oldParticipant3;
                    if (old2 != null) {
                        oldParticipant = old2.get(MessageObject.getPeerId(participant.peer));
                        if (oldParticipant != null) {
                            if (oldParticipant.self) {
                                participant.lastTypingDate = oldParticipant.active_date;
                            } else {
                                participant.lastTypingDate = Math.max(participant.active_date, oldParticipant.active_date);
                            }
                            oldSelf = oldSelf2;
                            if (time != participant.lastVisibleDate) {
                                participant.active_date = participant.lastTypingDate;
                            } else {
                                participant.active_date = oldParticipant.active_date;
                            }
                        } else {
                            oldSelf = oldSelf2;
                        }
                        old = old2;
                        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = oldParticipant;
                        this.participants.put(MessageObject.getPeerId(participant.peer), participant);
                        this.sortedParticipants.add(participant);
                        processAllSources(participant, true);
                        a++;
                        old2 = old;
                        selfId2 = selfId;
                        oldSelf2 = oldSelf;
                    } else {
                        oldSelf = oldSelf2;
                    }
                }
                oldParticipant = oldParticipant2;
                old = old2;
                TLRPC.TL_groupCallParticipant tL_groupCallParticipant2 = oldParticipant;
                this.participants.put(MessageObject.getPeerId(participant.peer), participant);
                this.sortedParticipants.add(participant);
                processAllSources(participant, true);
                a++;
                old2 = old;
                selfId2 = selfId;
                oldSelf2 = oldSelf;
            }
            ArrayList<TLRPC.TL_groupCallParticipant> arrayList3 = loadedParticipants;
            LongSparseArray<TLRPC.TL_groupCallParticipant> longSparseArray = old2;
            long j = selfId2;
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant3 = oldSelf2;
            if (this.call.participants_count < this.participants.size()) {
                this.call.participants_count = this.participants.size();
            }
            sortParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
            setParticiapantsVolume();
        }

        public void loadMembers(boolean fromBegin) {
            if (fromBegin) {
                if (!this.reloadingMembers) {
                    this.membersLoadEndReached = false;
                    this.nextLoadOffset = null;
                } else {
                    return;
                }
            }
            if (!this.membersLoadEndReached && this.sortedParticipants.size() <= 5000) {
                if (fromBegin) {
                    this.reloadingMembers = true;
                }
                this.loadingMembers = true;
                TLRPC.TL_phone_getGroupParticipants req = new TLRPC.TL_phone_getGroupParticipants();
                req.call = getInputGroupCall();
                String str = this.nextLoadOffset;
                if (str == null) {
                    str = "";
                }
                req.offset = str;
                req.limit = 20;
                this.currentAccount.getConnectionsManager().sendRequest(req, new ChatObject$Call$$ExternalSyntheticLambda5(this, fromBegin, req));
            }
        }

        /* renamed from: lambda$loadMembers$3$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1785lambda$loadMembers$3$orgtelegrammessengerChatObject$Call(boolean fromBegin, TLRPC.TL_phone_getGroupParticipants req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new ChatObject$Call$$ExternalSyntheticLambda12(this, fromBegin, response, req));
        }

        /* renamed from: lambda$loadMembers$2$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1784lambda$loadMembers$2$orgtelegrammessengerChatObject$Call(boolean fromBegin, TLObject response, TLRPC.TL_phone_getGroupParticipants req) {
            this.loadingMembers = false;
            if (fromBegin) {
                this.reloadingMembers = false;
            }
            if (response != null) {
                TLRPC.TL_phone_groupParticipants groupParticipants = (TLRPC.TL_phone_groupParticipants) response;
                this.currentAccount.getMessagesController().putUsers(groupParticipants.users, false);
                this.currentAccount.getMessagesController().putChats(groupParticipants.chats, false);
                onParticipantsLoad(groupParticipants.participants, fromBegin, req.offset, groupParticipants.next_offset, groupParticipants.version, groupParticipants.count);
            }
        }

        private void setParticiapantsVolume() {
            VoIPService voIPService = VoIPService.getSharedInstance();
            if (voIPService != null && voIPService.getAccount() == this.currentAccount.getCurrentAccount() && voIPService.getChat() != null && voIPService.getChat().id == (-this.chatId)) {
                voIPService.setParticipantsVolume();
            }
        }

        public void setTitle(String title) {
            TLRPC.TL_phone_editGroupCallTitle req = new TLRPC.TL_phone_editGroupCallTitle();
            req.call = getInputGroupCall();
            req.title = title;
            this.currentAccount.getConnectionsManager().sendRequest(req, new ChatObject$Call$$ExternalSyntheticLambda2(this));
        }

        /* renamed from: lambda$setTitle$4$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1791lambda$setTitle$4$orgtelegrammessengerChatObject$Call(TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                this.currentAccount.getMessagesController().processUpdates((TLRPC.Updates) response, false);
            }
        }

        public void addInvitedUser(long uid) {
            if (this.participants.get(uid) == null && !this.invitedUsersMap.contains(Long.valueOf(uid))) {
                this.invitedUsersMap.add(Long.valueOf(uid));
                this.invitedUsers.add(Long.valueOf(uid));
            }
        }

        public void processTypingsUpdate(AccountInstance accountInstance, ArrayList<Long> uids, int date) {
            int i = date;
            boolean updated = false;
            ArrayList<Long> participantsToLoad = null;
            long time = SystemClock.elapsedRealtime();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(time));
            int N = uids.size();
            for (int a = 0; a < N; a++) {
                Long id = uids.get(a);
                TLRPC.TL_groupCallParticipant participant = this.participants.get(id.longValue());
                if (participant == null) {
                    if (participantsToLoad == null) {
                        participantsToLoad = new ArrayList<>();
                    }
                    participantsToLoad.add(id);
                } else if (i - participant.lastTypingDate > 10) {
                    if (participant.lastVisibleDate != ((long) i)) {
                        participant.active_date = i;
                    }
                    participant.lastTypingDate = i;
                    updated = true;
                }
            }
            if (participantsToLoad != null) {
                loadUnknownParticipants(participantsToLoad, true, (OnParticipantsLoad) null);
            }
            if (updated) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
            }
        }

        private void loadUnknownParticipants(ArrayList<Long> participantsToLoad, boolean isIds, OnParticipantsLoad onLoad) {
            TLRPC.InputPeer inputPeer;
            HashSet<Long> set = isIds ? this.loadingUids : this.loadingSsrcs;
            int a = 0;
            int N = participantsToLoad.size();
            while (a < N) {
                if (set.contains(participantsToLoad.get(a))) {
                    participantsToLoad.remove(a);
                    a--;
                    N--;
                }
                a++;
            }
            if (participantsToLoad.isEmpty() == 0) {
                int guid = this.lastLoadGuid + 1;
                this.lastLoadGuid = guid;
                this.loadingGuids.add(Integer.valueOf(guid));
                set.addAll(participantsToLoad);
                TLRPC.TL_phone_getGroupParticipants req = new TLRPC.TL_phone_getGroupParticipants();
                req.call = getInputGroupCall();
                int N2 = participantsToLoad.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    long uid = participantsToLoad.get(a2).longValue();
                    if (!isIds) {
                        req.sources.add(Integer.valueOf((int) uid));
                    } else if (uid > 0) {
                        TLRPC.TL_inputPeerUser peerUser = new TLRPC.TL_inputPeerUser();
                        peerUser.user_id = uid;
                        req.ids.add(peerUser);
                    } else {
                        TLRPC.Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(-uid));
                        if (chat == null || ChatObject.isChannel(chat)) {
                            inputPeer = new TLRPC.TL_inputPeerChannel();
                            inputPeer.channel_id = -uid;
                        } else {
                            inputPeer = new TLRPC.TL_inputPeerChat();
                            inputPeer.chat_id = -uid;
                        }
                        req.ids.add(inputPeer);
                    }
                }
                req.offset = "";
                req.limit = 100;
                this.currentAccount.getConnectionsManager().sendRequest(req, new ChatObject$Call$$ExternalSyntheticLambda4(this, guid, onLoad, participantsToLoad, set));
            }
        }

        /* renamed from: lambda$loadUnknownParticipants$6$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1787x3a6fd8f9(int guid, OnParticipantsLoad onLoad, ArrayList participantsToLoad, HashSet set, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new ChatObject$Call$$ExternalSyntheticLambda9(this, guid, response, onLoad, participantsToLoad, set));
        }

        /* renamed from: lambda$loadUnknownParticipants$5$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1786xe351e81a(int guid, TLObject response, OnParticipantsLoad onLoad, ArrayList participantsToLoad, HashSet set) {
            if (this.loadingGuids.remove(Integer.valueOf(guid))) {
                if (response != null) {
                    TLRPC.TL_phone_groupParticipants groupParticipants = (TLRPC.TL_phone_groupParticipants) response;
                    this.currentAccount.getMessagesController().putUsers(groupParticipants.users, false);
                    this.currentAccount.getMessagesController().putChats(groupParticipants.chats, false);
                    int N = groupParticipants.participants.size();
                    for (int a = 0; a < N; a++) {
                        TLRPC.TL_groupCallParticipant participant = groupParticipants.participants.get(a);
                        long pid = MessageObject.getPeerId(participant.peer);
                        TLRPC.TL_groupCallParticipant oldParticipant = this.participants.get(pid);
                        if (oldParticipant != null) {
                            this.sortedParticipants.remove(oldParticipant);
                            processAllSources(oldParticipant, false);
                        }
                        this.participants.put(pid, participant);
                        this.sortedParticipants.add(participant);
                        processAllSources(participant, true);
                        if (this.invitedUsersMap.contains(Long.valueOf(pid))) {
                            Long id = Long.valueOf(pid);
                            this.invitedUsersMap.remove(id);
                            this.invitedUsers.remove(id);
                        }
                    }
                    if (this.call.participants_count < this.participants.size()) {
                        this.call.participants_count = this.participants.size();
                    }
                    sortParticipants();
                    this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
                    if (onLoad != null) {
                        onLoad.onLoad(participantsToLoad);
                    } else {
                        setParticiapantsVolume();
                    }
                }
                set.removeAll(participantsToLoad);
            }
        }

        private void processAllSources(TLRPC.TL_groupCallParticipant participant, boolean add) {
            if (participant.source != 0) {
                if (add) {
                    this.participantsBySources.put(participant.source, participant);
                } else {
                    this.participantsBySources.remove(participant.source);
                }
            }
            int c = 0;
            while (c < 2) {
                TLRPC.TL_groupCallParticipantVideo data = c == 0 ? participant.video : participant.presentation;
                if (data != null) {
                    if (!((2 & data.flags) == 0 || data.audio_source == 0)) {
                        if (add) {
                            this.participantsBySources.put(data.audio_source, participant);
                        } else {
                            this.participantsBySources.remove(data.audio_source);
                        }
                    }
                    SparseArray<TLRPC.TL_groupCallParticipant> sourcesArray = c == 0 ? this.participantsByVideoSources : this.participantsByPresentationSources;
                    int N = data.source_groups.size();
                    for (int a = 0; a < N; a++) {
                        TLRPC.TL_groupCallParticipantVideoSourceGroup sourceGroup = data.source_groups.get(a);
                        int N2 = sourceGroup.sources.size();
                        for (int b = 0; b < N2; b++) {
                            int source = sourceGroup.sources.get(b).intValue();
                            if (add) {
                                sourcesArray.put(source, participant);
                            } else {
                                sourcesArray.remove(source);
                            }
                        }
                    }
                    if (add) {
                        if (c == 0) {
                            participant.videoEndpoint = data.endpoint;
                        } else {
                            participant.presentationEndpoint = data.endpoint;
                        }
                    } else if (c == 0) {
                        participant.videoEndpoint = null;
                    } else {
                        participant.presentationEndpoint = null;
                    }
                }
                c++;
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:28:0x00b6  */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x0130  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void processVoiceLevelsUpdate(int[] r27, float[] r28, boolean[] r29) {
            /*
                r26 = this;
                r0 = r26
                r1 = r27
                r2 = 0
                r3 = 0
                org.telegram.messenger.AccountInstance r4 = r0.currentAccount
                org.telegram.tgnet.ConnectionsManager r4 = r4.getConnectionsManager()
                int r4 = r4.getCurrentTime()
                r5 = 0
                long r6 = android.os.SystemClock.elapsedRealtime()
                long r8 = android.os.SystemClock.uptimeMillis()
                org.telegram.messenger.AccountInstance r10 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r10 = r10.getNotificationCenter()
                int r11 = org.telegram.messenger.NotificationCenter.applyGroupCallVisibleParticipants
                r12 = 1
                java.lang.Object[] r13 = new java.lang.Object[r12]
                java.lang.Long r14 = java.lang.Long.valueOf(r6)
                r15 = 0
                java.lang.Boolean r16 = java.lang.Boolean.valueOf(r15)
                r13[r15] = r14
                r10.postNotificationName(r11, r13)
                r10 = 0
            L_0x0033:
                int r11 = r1.length
                if (r10 >= r11) goto L_0x01fb
                r11 = r1[r10]
                if (r11 != 0) goto L_0x0047
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r11 = r0.participants
                long r13 = r26.getSelfId()
                java.lang.Object r11 = r11.get(r13)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = (org.telegram.tgnet.TLRPC.TL_groupCallParticipant) r11
                goto L_0x0051
            L_0x0047:
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r11 = r0.participantsBySources
                r13 = r1[r10]
                java.lang.Object r11 = r11.get(r13)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = (org.telegram.tgnet.TLRPC.TL_groupCallParticipant) r11
            L_0x0051:
                if (r11 == 0) goto L_0x01ca
                boolean r13 = r29[r10]
                r11.hasVoice = r13
                boolean r13 = r29[r10]
                r17 = 500(0x1f4, double:2.47E-321)
                if (r13 != 0) goto L_0x0065
                long r13 = r11.lastVoiceUpdateTime
                long r13 = r6 - r13
                int r19 = (r13 > r17 ? 1 : (r13 == r17 ? 0 : -1))
                if (r19 <= 0) goto L_0x006b
            L_0x0065:
                boolean r13 = r29[r10]
                r11.hasVoiceDelayed = r13
                r11.lastVoiceUpdateTime = r6
            L_0x006b:
                org.telegram.tgnet.TLRPC$Peer r13 = r11.peer
                long r13 = org.telegram.messenger.MessageObject.getPeerId(r13)
                r19 = r28[r10]
                r20 = 1036831949(0x3dcccccd, float:0.1)
                r21 = 0
                java.lang.String r15 = " "
                java.lang.String r12 = "GroupCall"
                int r19 = (r19 > r20 ? 1 : (r19 == r20 ? 0 : -1))
                if (r19 <= 0) goto L_0x013a
                boolean r17 = r29[r10]
                if (r17 == 0) goto L_0x009f
                r19 = r3
                int r3 = r11.lastTypingDate
                r17 = 1
                int r3 = r3 + 1
                if (r3 >= r4) goto L_0x009c
                r20 = r2
                long r2 = r11.lastVisibleDate
                int r17 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
                if (r17 == 0) goto L_0x0098
                r11.active_date = r4
            L_0x0098:
                r11.lastTypingDate = r4
                r2 = 1
                goto L_0x00a5
            L_0x009c:
                r20 = r2
                goto L_0x00a3
            L_0x009f:
                r20 = r2
                r19 = r3
            L_0x00a3:
                r2 = r20
            L_0x00a5:
                r11.lastSpeakTime = r8
                r3 = r28[r10]
                r11.amplitude = r3
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r3 = r0.currentSpeakingPeers
                r17 = r2
                r2 = 0
                java.lang.Object r3 = r3.get(r13, r2)
                if (r3 != 0) goto L_0x0130
                java.lang.String r2 = "add to current speaking "
                int r3 = (r13 > r21 ? 1 : (r13 == r21 ? 0 : -1))
                if (r3 <= 0) goto L_0x00f1
                org.telegram.messenger.AccountInstance r3 = r0.currentAccount
                int r3 = r3.getCurrentAccount()
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                r23 = r4
                java.lang.Long r4 = java.lang.Long.valueOf(r13)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r2)
                r4.append(r13)
                r4.append(r15)
                if (r3 != 0) goto L_0x00e2
                r2 = 0
                goto L_0x00e4
            L_0x00e2:
                java.lang.String r2 = r3.first_name
            L_0x00e4:
                r4.append(r2)
                java.lang.String r2 = r4.toString()
                com.google.android.exoplayer2.util.Log.d(r12, r2)
                r24 = r6
                goto L_0x0126
            L_0x00f1:
                r23 = r4
                org.telegram.messenger.AccountInstance r3 = r0.currentAccount
                int r3 = r3.getCurrentAccount()
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                r24 = r6
                long r6 = -r13
                java.lang.Long r4 = java.lang.Long.valueOf(r6)
                org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r2)
                r4.append(r13)
                r4.append(r15)
                if (r3 != 0) goto L_0x011a
                r2 = 0
                goto L_0x011c
            L_0x011a:
                java.lang.String r2 = r3.title
            L_0x011c:
                r4.append(r2)
                java.lang.String r2 = r4.toString()
                com.google.android.exoplayer2.util.Log.d(r12, r2)
            L_0x0126:
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.currentSpeakingPeers
                r2.put(r13, r11)
                r3 = 1
                r2 = r17
                goto L_0x01c9
            L_0x0130:
                r23 = r4
                r24 = r6
                r2 = r17
                r3 = r19
                goto L_0x01c9
            L_0x013a:
                r20 = r2
                r19 = r3
                r23 = r4
                r24 = r6
                long r2 = r11.lastSpeakTime
                long r2 = r8 - r2
                int r4 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
                if (r4 < 0) goto L_0x01c2
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.currentSpeakingPeers
                r3 = 0
                java.lang.Object r2 = r2.get(r13, r3)
                if (r2 == 0) goto L_0x01c2
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.currentSpeakingPeers
                r2.remove(r13)
                java.lang.String r2 = "remove from speaking "
                int r3 = (r13 > r21 ? 1 : (r13 == r21 ? 0 : -1))
                if (r3 <= 0) goto L_0x018f
                org.telegram.messenger.AccountInstance r3 = r0.currentAccount
                int r3 = r3.getCurrentAccount()
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                java.lang.Long r4 = java.lang.Long.valueOf(r13)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r2)
                r4.append(r13)
                r4.append(r15)
                if (r3 != 0) goto L_0x0182
                r2 = 0
                goto L_0x0184
            L_0x0182:
                java.lang.String r2 = r3.first_name
            L_0x0184:
                r4.append(r2)
                java.lang.String r2 = r4.toString()
                com.google.android.exoplayer2.util.Log.d(r12, r2)
                goto L_0x01c0
            L_0x018f:
                org.telegram.messenger.AccountInstance r3 = r0.currentAccount
                int r3 = r3.getCurrentAccount()
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                long r6 = -r13
                java.lang.Long r4 = java.lang.Long.valueOf(r6)
                org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r2)
                r4.append(r13)
                r4.append(r15)
                if (r3 != 0) goto L_0x01b4
                r2 = 0
                goto L_0x01b6
            L_0x01b4:
                java.lang.String r2 = r3.title
            L_0x01b6:
                r4.append(r2)
                java.lang.String r2 = r4.toString()
                com.google.android.exoplayer2.util.Log.d(r12, r2)
            L_0x01c0:
                r3 = 1
                goto L_0x01c4
            L_0x01c2:
                r3 = r19
            L_0x01c4:
                r2 = 0
                r11.amplitude = r2
                r2 = r20
            L_0x01c9:
                goto L_0x01f1
            L_0x01ca:
                r20 = r2
                r19 = r3
                r23 = r4
                r24 = r6
                r2 = r1[r10]
                if (r2 == 0) goto L_0x01ed
                if (r5 != 0) goto L_0x01de
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r5 = r2
            L_0x01de:
                r2 = r1[r10]
                long r2 = (long) r2
                java.lang.Long r2 = java.lang.Long.valueOf(r2)
                r5.add(r2)
                r3 = r19
                r2 = r20
                goto L_0x01f1
            L_0x01ed:
                r3 = r19
                r2 = r20
            L_0x01f1:
                int r10 = r10 + 1
                r4 = r23
                r6 = r24
                r12 = 1
                r15 = 0
                goto L_0x0033
            L_0x01fb:
                r20 = r2
                r19 = r3
                r23 = r4
                r24 = r6
                if (r5 == 0) goto L_0x020a
                r2 = 0
                r3 = 0
                r0.loadUnknownParticipants(r5, r2, r3)
            L_0x020a:
                r2 = 2
                r3 = 3
                if (r20 == 0) goto L_0x0234
                r26.sortParticipants()
                org.telegram.messenger.AccountInstance r4 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r4 = r4.getNotificationCenter()
                int r6 = org.telegram.messenger.NotificationCenter.groupCallUpdated
                java.lang.Object[] r7 = new java.lang.Object[r3]
                long r10 = r0.chatId
                java.lang.Long r10 = java.lang.Long.valueOf(r10)
                r11 = 0
                r7[r11] = r10
                org.telegram.tgnet.TLRPC$GroupCall r10 = r0.call
                long r10 = r10.id
                java.lang.Long r10 = java.lang.Long.valueOf(r10)
                r11 = 1
                r7[r11] = r10
                r7[r2] = r16
                r4.postNotificationName(r6, r7)
            L_0x0234:
                if (r19 == 0) goto L_0x026d
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r4 = r0.currentSpeakingPeers
                int r4 = r4.size()
                if (r4 <= 0) goto L_0x024a
                java.lang.Runnable r4 = r0.updateCurrentSpeakingRunnable
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4)
                java.lang.Runnable r4 = r0.updateCurrentSpeakingRunnable
                r6 = 550(0x226, double:2.717E-321)
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r4, r6)
            L_0x024a:
                org.telegram.messenger.AccountInstance r4 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r4 = r4.getNotificationCenter()
                int r6 = org.telegram.messenger.NotificationCenter.groupCallSpeakingUsersUpdated
                java.lang.Object[] r3 = new java.lang.Object[r3]
                long r10 = r0.chatId
                java.lang.Long r7 = java.lang.Long.valueOf(r10)
                r10 = 0
                r3[r10] = r7
                org.telegram.tgnet.TLRPC$GroupCall r7 = r0.call
                long r10 = r7.id
                java.lang.Long r7 = java.lang.Long.valueOf(r10)
                r10 = 1
                r3[r10] = r7
                r3[r2] = r16
                r4.postNotificationName(r6, r3)
            L_0x026d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.processVoiceLevelsUpdate(int[], float[], boolean[]):void");
        }

        public void updateVisibleParticipants() {
            sortParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false, 0L);
        }

        public void clearVideFramesInfo() {
            for (int i = 0; i < this.sortedParticipants.size(); i++) {
                this.sortedParticipants.get(i).hasCameraFrame = 0;
                this.sortedParticipants.get(i).hasPresentationFrame = 0;
                this.sortedParticipants.get(i).videoIndex = 0;
            }
            sortParticipants();
        }

        public void processUnknownVideoParticipants(int[] ssrc, OnParticipantsLoad onLoad) {
            ArrayList<Long> participantsToLoad = null;
            for (int a = 0; a < ssrc.length; a++) {
                if (this.participantsBySources.get(ssrc[a]) == null && this.participantsByVideoSources.get(ssrc[a]) == null && this.participantsByPresentationSources.get(ssrc[a]) == null) {
                    if (participantsToLoad == null) {
                        participantsToLoad = new ArrayList<>();
                    }
                    participantsToLoad.add(Long.valueOf((long) ssrc[a]));
                }
            }
            if (participantsToLoad != null) {
                loadUnknownParticipants(participantsToLoad, false, onLoad);
            } else {
                onLoad.onLoad((ArrayList<Long>) null);
            }
        }

        private int isValidUpdate(TLRPC.TL_updateGroupCallParticipants update) {
            if (this.call.version + 1 == update.version || this.call.version == update.version) {
                return 0;
            }
            if (this.call.version < update.version) {
                return 1;
            }
            return 2;
        }

        public void setSelfPeer(TLRPC.InputPeer peer) {
            if (peer == null) {
                this.selfPeer = null;
            } else if (peer instanceof TLRPC.TL_inputPeerUser) {
                TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                this.selfPeer = tL_peerUser;
                tL_peerUser.user_id = peer.user_id;
            } else if (peer instanceof TLRPC.TL_inputPeerChat) {
                TLRPC.TL_peerChat tL_peerChat = new TLRPC.TL_peerChat();
                this.selfPeer = tL_peerChat;
                tL_peerChat.chat_id = peer.chat_id;
            } else {
                TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
                this.selfPeer = tL_peerChannel;
                tL_peerChannel.channel_id = peer.channel_id;
            }
        }

        private void processUpdatesQueue() {
            Collections.sort(this.updatesQueue, ChatObject$Call$$ExternalSyntheticLambda14.INSTANCE);
            ArrayList<TLRPC.TL_updateGroupCallParticipants> arrayList = this.updatesQueue;
            if (arrayList != null && !arrayList.isEmpty()) {
                boolean anyProceed = false;
                for (int a = 0; a < this.updatesQueue.size(); a = (a - 1) + 1) {
                    TLRPC.TL_updateGroupCallParticipants update = this.updatesQueue.get(a);
                    int updateState = isValidUpdate(update);
                    if (updateState == 0) {
                        processParticipantsUpdate(update, true);
                        anyProceed = true;
                        this.updatesQueue.remove(a);
                    } else if (updateState != 1) {
                        this.updatesQueue.remove(a);
                    } else if (this.updatesStartWaitTime == 0 || (!anyProceed && Math.abs(System.currentTimeMillis() - this.updatesStartWaitTime) > 1500)) {
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
                        if (anyProceed) {
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
                ChatObject$Call$$ExternalSyntheticLambda8 chatObject$Call$$ExternalSyntheticLambda8 = new ChatObject$Call$$ExternalSyntheticLambda8(this);
                this.checkQueueRunnable = chatObject$Call$$ExternalSyntheticLambda8;
                AndroidUtilities.runOnUIThread(chatObject$Call$$ExternalSyntheticLambda8, 1000);
            }
        }

        public void reloadGroupCall() {
            TLRPC.TL_phone_getGroupCall req = new TLRPC.TL_phone_getGroupCall();
            req.call = getInputGroupCall();
            req.limit = 100;
            this.currentAccount.getConnectionsManager().sendRequest(req, new ChatObject$Call$$ExternalSyntheticLambda1(this));
        }

        /* renamed from: lambda$reloadGroupCall$9$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1790lambda$reloadGroupCall$9$orgtelegrammessengerChatObject$Call(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new ChatObject$Call$$ExternalSyntheticLambda11(this, response));
        }

        /* renamed from: lambda$reloadGroupCall$8$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1789lambda$reloadGroupCall$8$orgtelegrammessengerChatObject$Call(TLObject response) {
            if (response instanceof TLRPC.TL_phone_groupCall) {
                TLRPC.TL_phone_groupCall phoneGroupCall = (TLRPC.TL_phone_groupCall) response;
                this.call = phoneGroupCall.call;
                this.currentAccount.getMessagesController().putUsers(phoneGroupCall.users, false);
                this.currentAccount.getMessagesController().putChats(phoneGroupCall.chats, false);
                onParticipantsLoad(phoneGroupCall.participants, true, "", phoneGroupCall.participants_next_offset, phoneGroupCall.call.version, phoneGroupCall.call.participants_count);
            }
        }

        private void loadGroupCall() {
            if (!this.loadingGroupCall && SystemClock.elapsedRealtime() - this.lastGroupCallReloadTime >= 30000) {
                this.loadingGroupCall = true;
                TLRPC.TL_phone_getGroupParticipants req = new TLRPC.TL_phone_getGroupParticipants();
                req.call = getInputGroupCall();
                req.offset = "";
                req.limit = 1;
                this.currentAccount.getConnectionsManager().sendRequest(req, new ChatObject$Call$$ExternalSyntheticLambda0(this));
            }
        }

        /* renamed from: lambda$loadGroupCall$11$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1783lambda$loadGroupCall$11$orgtelegrammessengerChatObject$Call(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new ChatObject$Call$$ExternalSyntheticLambda10(this, response));
        }

        /* renamed from: lambda$loadGroupCall$10$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1782lambda$loadGroupCall$10$orgtelegrammessengerChatObject$Call(TLObject response) {
            this.lastGroupCallReloadTime = SystemClock.elapsedRealtime();
            this.loadingGroupCall = false;
            if (response != null) {
                TLRPC.TL_phone_groupParticipants res = (TLRPC.TL_phone_groupParticipants) response;
                this.currentAccount.getMessagesController().putUsers(res.users, false);
                this.currentAccount.getMessagesController().putChats(res.chats, false);
                if (this.call.participants_count != res.count) {
                    this.call.participants_count = res.count;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("new participants reload count " + this.call.participants_count);
                    }
                    this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:112:0x0321  */
        /* JADX WARNING: Removed duplicated region for block: B:113:0x032a  */
        /* JADX WARNING: Removed duplicated region for block: B:130:0x0380  */
        /* JADX WARNING: Removed duplicated region for block: B:137:0x039f  */
        /* JADX WARNING: Removed duplicated region for block: B:142:0x03b0  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x017b  */
        /* JADX WARNING: Removed duplicated region for block: B:79:0x0247  */
        /* JADX WARNING: Removed duplicated region for block: B:82:0x025d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void processParticipantsUpdate(org.telegram.tgnet.TLRPC.TL_updateGroupCallParticipants r33, boolean r34) {
            /*
                r32 = this;
                r0 = r32
                r1 = r33
                r2 = 0
                r3 = 0
                r5 = 1
                if (r34 != 0) goto L_0x00a0
                r6 = 0
                r7 = 0
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r8 = r1.participants
                int r8 = r8.size()
            L_0x0012:
                if (r7 >= r8) goto L_0x0025
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r9 = r1.participants
                java.lang.Object r9 = r9.get(r7)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = (org.telegram.tgnet.TLRPC.TL_groupCallParticipant) r9
                boolean r10 = r9.versioned
                if (r10 == 0) goto L_0x0022
                r6 = 1
                goto L_0x0025
            L_0x0022:
                int r7 = r7 + 1
                goto L_0x0012
            L_0x0025:
                if (r6 == 0) goto L_0x008c
                org.telegram.tgnet.TLRPC$GroupCall r7 = r0.call
                int r7 = r7.version
                int r7 = r7 + r5
                int r8 = r1.version
                if (r7 >= r8) goto L_0x008c
                boolean r7 = r0.reloadingMembers
                r8 = 1500(0x5dc, double:7.41E-321)
                if (r7 != 0) goto L_0x0052
                long r10 = r0.updatesStartWaitTime
                int r7 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
                if (r7 == 0) goto L_0x0052
                long r10 = java.lang.System.currentTimeMillis()
                long r12 = r0.updatesStartWaitTime
                long r10 = r10 - r12
                long r10 = java.lang.Math.abs(r10)
                int r7 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
                if (r7 > 0) goto L_0x004c
                goto L_0x0052
            L_0x004c:
                r0.nextLoadOffset = r2
                r0.loadMembers(r5)
                goto L_0x008b
            L_0x0052:
                long r10 = r0.updatesStartWaitTime
                int r2 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
                if (r2 != 0) goto L_0x005e
                long r2 = java.lang.System.currentTimeMillis()
                r0.updatesStartWaitTime = r2
            L_0x005e:
                boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x0078
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "add TL_updateGroupCallParticipants to queue "
                r2.append(r3)
                int r3 = r1.version
                r2.append(r3)
                java.lang.String r2 = r2.toString()
                org.telegram.messenger.FileLog.d(r2)
            L_0x0078:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants> r2 = r0.updatesQueue
                r2.add(r1)
                java.lang.Runnable r2 = r0.checkQueueRunnable
                if (r2 != 0) goto L_0x008b
                org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda8 r2 = new org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda8
                r2.<init>(r0)
                r0.checkQueueRunnable = r2
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r8)
            L_0x008b:
                return
            L_0x008c:
                if (r6 == 0) goto L_0x00a0
                int r7 = r1.version
                org.telegram.tgnet.TLRPC$GroupCall r8 = r0.call
                int r8 = r8.version
                if (r7 >= r8) goto L_0x00a0
                boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x009f
                java.lang.String r2 = "ignore processParticipantsUpdate because of version"
                org.telegram.messenger.FileLog.d(r2)
            L_0x009f:
                return
            L_0x00a0:
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                r10 = 0
                long r11 = r32.getSelfId()
                long r13 = android.os.SystemClock.elapsedRealtime()
                r15 = 0
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r3 = r0.sortedParticipants
                boolean r3 = r3.isEmpty()
                if (r3 != 0) goto L_0x00c7
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r3 = r0.sortedParticipants
                int r4 = r3.size()
                int r4 = r4 - r5
                java.lang.Object r3 = r3.get(r4)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = (org.telegram.tgnet.TLRPC.TL_groupCallParticipant) r3
                int r3 = r3.date
                goto L_0x00c8
            L_0x00c7:
                r3 = 0
            L_0x00c8:
                org.telegram.messenger.AccountInstance r4 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r4 = r4.getNotificationCenter()
                int r2 = org.telegram.messenger.NotificationCenter.applyGroupCallVisibleParticipants
                r19 = r6
                java.lang.Object[] r6 = new java.lang.Object[r5]
                java.lang.Long r20 = java.lang.Long.valueOf(r13)
                r5 = 0
                r6[r5] = r20
                r4.postNotificationName(r2, r6)
                r2 = 0
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r4 = r1.participants
                int r4 = r4.size()
                r6 = r19
            L_0x00e7:
                if (r2 >= r4) goto L_0x0480
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r5 = r1.participants
                java.lang.Object r5 = r5.get(r2)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r5 = (org.telegram.tgnet.TLRPC.TL_groupCallParticipant) r5
                r20 = r4
                org.telegram.tgnet.TLRPC$Peer r4 = r5.peer
                r21 = r8
                r22 = r9
                long r8 = org.telegram.messenger.MessageObject.getPeerId(r4)
                boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r4 == 0) goto L_0x0148
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r23 = r10
                java.lang.String r10 = "process participant "
                r4.append(r10)
                r4.append(r8)
                java.lang.String r10 = " left = "
                r4.append(r10)
                boolean r10 = r5.left
                r4.append(r10)
                java.lang.String r10 = " versioned "
                r4.append(r10)
                boolean r10 = r5.versioned
                r4.append(r10)
                java.lang.String r10 = " flags = "
                r4.append(r10)
                int r10 = r5.flags
                r4.append(r10)
                java.lang.String r10 = " self = "
                r4.append(r10)
                r4.append(r11)
                java.lang.String r10 = " volume = "
                r4.append(r10)
                int r10 = r5.volume
                r4.append(r10)
                java.lang.String r4 = r4.toString()
                org.telegram.messenger.FileLog.d(r4)
                goto L_0x014a
            L_0x0148:
                r23 = r10
            L_0x014a:
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r4 = r0.participants
                java.lang.Object r4 = r4.get(r8)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r4 = (org.telegram.tgnet.TLRPC.TL_groupCallParticipant) r4
                boolean r10 = r5.left
                r24 = r15
                java.lang.String r15 = " "
                r16 = r7
                java.lang.String r7 = "GroupCall"
                if (r10 == 0) goto L_0x026d
                if (r4 != 0) goto L_0x0175
                int r10 = r1.version
                r26 = r6
                org.telegram.tgnet.TLRPC$GroupCall r6 = r0.call
                int r6 = r6.version
                if (r10 != r6) goto L_0x0177
                boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r6 == 0) goto L_0x0173
                java.lang.String r6 = "unknowd participant left, reload call"
                org.telegram.messenger.FileLog.d(r6)
            L_0x0173:
                r6 = 1
                goto L_0x0179
            L_0x0175:
                r26 = r6
            L_0x0177:
                r6 = r26
            L_0x0179:
                if (r4 == 0) goto L_0x0247
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r10 = r0.participants
                r10.remove(r8)
                r10 = 0
                r0.processAllSources(r4, r10)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r10 = r0.sortedParticipants
                r10.remove(r4)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r10 = r0.visibleParticipants
                r10.remove(r4)
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r10 = r0.currentSpeakingPeers
                r26 = r6
                r6 = 0
                java.lang.Object r10 = r10.get(r8, r6)
                if (r10 == 0) goto L_0x0212
                java.lang.String r6 = "left remove from speaking "
                r17 = 0
                int r10 = (r8 > r17 ? 1 : (r8 == r17 ? 0 : -1))
                if (r10 <= 0) goto L_0x01d6
                org.telegram.messenger.AccountInstance r10 = r0.currentAccount
                int r10 = r10.getCurrentAccount()
                org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
                r27 = r2
                java.lang.Long r2 = java.lang.Long.valueOf(r8)
                org.telegram.tgnet.TLRPC$User r2 = r10.getUser(r2)
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                r10.append(r6)
                r10.append(r8)
                r10.append(r15)
                if (r2 != 0) goto L_0x01c7
                r6 = 0
                goto L_0x01c9
            L_0x01c7:
                java.lang.String r6 = r2.first_name
            L_0x01c9:
                r10.append(r6)
                java.lang.String r6 = r10.toString()
                com.google.android.exoplayer2.util.Log.d(r7, r6)
                r28 = r11
                goto L_0x020b
            L_0x01d6:
                r27 = r2
                org.telegram.messenger.AccountInstance r2 = r0.currentAccount
                int r2 = r2.getCurrentAccount()
                org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                r28 = r11
                long r10 = -r8
                java.lang.Long r10 = java.lang.Long.valueOf(r10)
                org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r10)
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                r10.append(r6)
                r10.append(r8)
                r10.append(r15)
                if (r2 != 0) goto L_0x01ff
                r6 = 0
                goto L_0x0201
            L_0x01ff:
                java.lang.String r6 = r2.title
            L_0x0201:
                r10.append(r6)
                java.lang.String r6 = r10.toString()
                com.google.android.exoplayer2.util.Log.d(r7, r6)
            L_0x020b:
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.currentSpeakingPeers
                r2.remove(r8)
                r10 = 1
                goto L_0x0218
            L_0x0212:
                r27 = r2
                r28 = r11
                r10 = r23
            L_0x0218:
                r2 = 0
            L_0x0219:
                java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r6 = r0.visibleVideoParticipants
                int r6 = r6.size()
                if (r2 >= r6) goto L_0x0245
                java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r6 = r0.visibleVideoParticipants
                java.lang.Object r6 = r6.get(r2)
                org.telegram.messenger.ChatObject$VideoParticipant r6 = (org.telegram.messenger.ChatObject.VideoParticipant) r6
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r7 = r6.participant
                org.telegram.tgnet.TLRPC$Peer r7 = r7.peer
                long r11 = org.telegram.messenger.MessageObject.getPeerId(r7)
                org.telegram.tgnet.TLRPC$Peer r7 = r4.peer
                long r30 = org.telegram.messenger.MessageObject.getPeerId(r7)
                int r7 = (r11 > r30 ? 1 : (r11 == r30 ? 0 : -1))
                if (r7 != 0) goto L_0x0242
                java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r7 = r0.visibleVideoParticipants
                r7.remove(r2)
                int r2 = r2 + -1
            L_0x0242:
                r6 = 1
                int r2 = r2 + r6
                goto L_0x0219
            L_0x0245:
                r6 = 1
                goto L_0x0250
            L_0x0247:
                r27 = r2
                r26 = r6
                r28 = r11
                r6 = 1
                r10 = r23
            L_0x0250:
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                int r7 = r2.participants_count
                int r7 = r7 - r6
                r2.participants_count = r7
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                int r2 = r2.participants_count
                if (r2 >= 0) goto L_0x0262
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                r6 = 0
                r2.participants_count = r6
            L_0x0262:
                r2 = 1
                r7 = r2
                r2 = r22
                r15 = r24
                r6 = 0
                r17 = 0
                goto L_0x046a
            L_0x026d:
                r27 = r2
                r26 = r6
                r28 = r11
                java.util.HashSet<java.lang.Long> r2 = r0.invitedUsersMap
                java.lang.Long r6 = java.lang.Long.valueOf(r8)
                boolean r2 = r2.contains(r6)
                if (r2 == 0) goto L_0x028d
                java.lang.Long r2 = java.lang.Long.valueOf(r8)
                java.util.HashSet<java.lang.Long> r6 = r0.invitedUsersMap
                r6.remove(r2)
                java.util.ArrayList<java.lang.Long> r6 = r0.invitedUsers
                r6.remove(r2)
            L_0x028d:
                if (r4 == 0) goto L_0x03d4
                boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x0298
                java.lang.String r2 = "new participant, update old"
                org.telegram.messenger.FileLog.d(r2)
            L_0x0298:
                boolean r2 = r5.muted
                r4.muted = r2
                boolean r2 = r5.muted
                if (r2 == 0) goto L_0x031a
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.currentSpeakingPeers
                r6 = 0
                java.lang.Object r2 = r2.get(r8, r6)
                if (r2 == 0) goto L_0x031b
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.currentSpeakingPeers
                r2.remove(r8)
                java.lang.String r2 = "muted remove from speaking "
                r10 = 0
                int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                if (r12 <= 0) goto L_0x02e7
                org.telegram.messenger.AccountInstance r10 = r0.currentAccount
                int r10 = r10.getCurrentAccount()
                org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
                java.lang.Long r11 = java.lang.Long.valueOf(r8)
                org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r11)
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r2)
                r11.append(r8)
                r11.append(r15)
                if (r10 != 0) goto L_0x02da
                r2 = r6
                goto L_0x02dc
            L_0x02da:
                java.lang.String r2 = r10.first_name
            L_0x02dc:
                r11.append(r2)
                java.lang.String r2 = r11.toString()
                com.google.android.exoplayer2.util.Log.d(r7, r2)
                goto L_0x0318
            L_0x02e7:
                org.telegram.messenger.AccountInstance r10 = r0.currentAccount
                int r10 = r10.getCurrentAccount()
                org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
                long r11 = -r8
                java.lang.Long r11 = java.lang.Long.valueOf(r11)
                org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r11)
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r2)
                r11.append(r8)
                r11.append(r15)
                if (r10 != 0) goto L_0x030c
                r2 = r6
                goto L_0x030e
            L_0x030c:
                java.lang.String r2 = r10.title
            L_0x030e:
                r11.append(r2)
                java.lang.String r2 = r11.toString()
                com.google.android.exoplayer2.util.Log.d(r7, r2)
            L_0x0318:
                r10 = 1
                goto L_0x031d
            L_0x031a:
                r6 = 0
            L_0x031b:
                r10 = r23
            L_0x031d:
                boolean r2 = r5.min
                if (r2 != 0) goto L_0x032a
                int r2 = r5.volume
                r4.volume = r2
                boolean r2 = r5.muted_by_you
                r4.muted_by_you = r2
                goto L_0x0348
            L_0x032a:
                int r2 = r5.flags
                r2 = r2 & 128(0x80, float:1.794E-43)
                if (r2 == 0) goto L_0x033c
                int r2 = r4.flags
                r2 = r2 & 128(0x80, float:1.794E-43)
                if (r2 != 0) goto L_0x033c
                int r2 = r5.flags
                r2 = r2 & -129(0xffffffffffffff7f, float:NaN)
                r5.flags = r2
            L_0x033c:
                boolean r2 = r5.volume_by_admin
                if (r2 == 0) goto L_0x0348
                boolean r2 = r4.volume_by_admin
                if (r2 == 0) goto L_0x0348
                int r2 = r5.volume
                r4.volume = r2
            L_0x0348:
                int r2 = r5.flags
                r4.flags = r2
                boolean r2 = r5.can_self_unmute
                r4.can_self_unmute = r2
                boolean r2 = r5.video_joined
                r4.video_joined = r2
                long r11 = r4.raise_hand_rating
                r17 = 0
                int r2 = (r11 > r17 ? 1 : (r11 == r17 ? 0 : -1))
                if (r2 != 0) goto L_0x0368
                long r11 = r5.raise_hand_rating
                int r2 = (r11 > r17 ? 1 : (r11 == r17 ? 0 : -1))
                if (r2 == 0) goto L_0x0368
                long r11 = android.os.SystemClock.elapsedRealtime()
                r4.lastRaiseHandDate = r11
            L_0x0368:
                long r11 = r5.raise_hand_rating
                r4.raise_hand_rating = r11
                int r2 = r5.date
                r4.date = r2
                int r2 = r4.active_date
                int r7 = r5.active_date
                int r2 = java.lang.Math.max(r2, r7)
                r4.lastTypingDate = r2
                long r11 = r4.lastVisibleDate
                int r2 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
                if (r2 == 0) goto L_0x0384
                int r2 = r4.lastTypingDate
                r4.active_date = r2
            L_0x0384:
                int r2 = r4.source
                int r7 = r5.source
                if (r2 != r7) goto L_0x03b0
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r2 = r4.video
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r7 = r5.video
                boolean r2 = r0.isSameVideo(r2, r7)
                if (r2 == 0) goto L_0x03b0
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r2 = r4.presentation
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r7 = r5.presentation
                boolean r2 = r0.isSameVideo(r2, r7)
                if (r2 != 0) goto L_0x039f
                goto L_0x03b0
            L_0x039f:
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r2 = r4.video
                if (r2 == 0) goto L_0x03d0
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r2 = r5.video
                if (r2 == 0) goto L_0x03d0
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r2 = r4.video
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r7 = r5.video
                boolean r7 = r7.paused
                r2.paused = r7
                goto L_0x03d0
            L_0x03b0:
                r2 = 0
                r0.processAllSources(r4, r2)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r2 = r5.video
                r4.video = r2
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r2 = r5.presentation
                r4.presentation = r2
                int r2 = r5.source
                r4.source = r2
                r2 = 1
                r0.processAllSources(r4, r2)
                java.lang.String r2 = r4.presentationEndpoint
                r5.presentationEndpoint = r2
                java.lang.String r2 = r4.videoEndpoint
                r5.videoEndpoint = r2
                int r2 = r4.videoIndex
                r5.videoIndex = r2
            L_0x03d0:
                r17 = 0
                goto L_0x044a
            L_0x03d4:
                r6 = 0
                boolean r2 = r5.just_joined
                if (r2 == 0) goto L_0x0406
                int r2 = (r8 > r28 ? 1 : (r8 == r28 ? 0 : -1))
                if (r2 == 0) goto L_0x03e0
                r10 = r8
                r24 = r10
            L_0x03e0:
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                int r7 = r2.participants_count
                r10 = 1
                int r7 = r7 + r10
                r2.participants_count = r7
                int r2 = r1.version
                org.telegram.tgnet.TLRPC$GroupCall r7 = r0.call
                int r7 = r7.version
                if (r2 != r7) goto L_0x03fd
                r2 = 1
                boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r7 == 0) goto L_0x03fa
                java.lang.String r7 = "new participant, just joined, reload call"
                org.telegram.messenger.FileLog.d(r7)
            L_0x03fa:
                r26 = r2
                goto L_0x0406
            L_0x03fd:
                boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x0406
                java.lang.String r2 = "new participant, just joined"
                org.telegram.messenger.FileLog.d(r2)
            L_0x0406:
                long r10 = r5.raise_hand_rating
                r17 = 0
                int r2 = (r10 > r17 ? 1 : (r10 == r17 ? 0 : -1))
                if (r2 == 0) goto L_0x0414
                long r10 = android.os.SystemClock.elapsedRealtime()
                r5.lastRaiseHandDate = r10
            L_0x0414:
                int r2 = (r8 > r28 ? 1 : (r8 == r28 ? 0 : -1))
                if (r2 == 0) goto L_0x043a
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.sortedParticipants
                int r2 = r2.size()
                r7 = 20
                if (r2 < r7) goto L_0x043a
                int r2 = r5.date
                if (r2 <= r3) goto L_0x043a
                int r2 = r5.active_date
                if (r2 != 0) goto L_0x043a
                boolean r2 = r5.can_self_unmute
                if (r2 != 0) goto L_0x043a
                boolean r2 = r5.muted
                if (r2 == 0) goto L_0x043a
                boolean r2 = r5.min
                if (r2 == 0) goto L_0x043a
                boolean r2 = r0.membersLoadEndReached
                if (r2 == 0) goto L_0x043f
            L_0x043a:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.sortedParticipants
                r2.add(r5)
            L_0x043f:
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.participants
                r2.put(r8, r5)
                r2 = 1
                r0.processAllSources(r5, r2)
                r10 = r23
            L_0x044a:
                int r2 = (r8 > r28 ? 1 : (r8 == r28 ? 0 : -1))
                if (r2 != 0) goto L_0x0466
                int r2 = r5.active_date
                if (r2 != 0) goto L_0x0466
                boolean r2 = r5.can_self_unmute
                if (r2 != 0) goto L_0x045a
                boolean r2 = r5.muted
                if (r2 != 0) goto L_0x0466
            L_0x045a:
                org.telegram.messenger.AccountInstance r2 = r0.currentAccount
                org.telegram.tgnet.ConnectionsManager r2 = r2.getConnectionsManager()
                int r2 = r2.getCurrentTime()
                r5.active_date = r2
            L_0x0466:
                r2 = 1
                r7 = 1
                r15 = r24
            L_0x046a:
                int r11 = (r8 > r28 ? 1 : (r8 == r28 ? 0 : -1))
                if (r11 != 0) goto L_0x0471
                r11 = 1
                r8 = r11
                goto L_0x0473
            L_0x0471:
                r8 = r21
            L_0x0473:
                int r4 = r27 + 1
                r9 = r2
                r2 = r4
                r4 = r20
                r6 = r26
                r11 = r28
                r5 = 0
                goto L_0x00e7
            L_0x0480:
                r27 = r2
                r20 = r4
                r26 = r6
                r21 = r8
                r22 = r9
                r23 = r10
                r28 = r11
                r24 = r15
                r16 = r7
                int r2 = r1.version
                org.telegram.tgnet.TLRPC$GroupCall r4 = r0.call
                int r4 = r4.version
                if (r2 <= r4) goto L_0x04a5
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                int r4 = r1.version
                r2.version = r4
                if (r34 != 0) goto L_0x04a5
                r32.processUpdatesQueue()
            L_0x04a5:
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                int r2 = r2.participants_count
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r4 = r0.participants
                int r4 = r4.size()
                if (r2 >= r4) goto L_0x04bb
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r4 = r0.participants
                int r4 = r4.size()
                r2.participants_count = r4
            L_0x04bb:
                boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x04d7
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r4 = "new participants count after update "
                r2.append(r4)
                org.telegram.tgnet.TLRPC$GroupCall r4 = r0.call
                int r4 = r4.participants_count
                r2.append(r4)
                java.lang.String r2 = r2.toString()
                org.telegram.messenger.FileLog.d(r2)
            L_0x04d7:
                if (r26 == 0) goto L_0x04dc
                r32.loadGroupCall()
            L_0x04dc:
                r2 = 2
                r4 = 3
                if (r16 == 0) goto L_0x0513
                if (r22 == 0) goto L_0x04e5
                r32.sortParticipants()
            L_0x04e5:
                org.telegram.messenger.AccountInstance r5 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r5 = r5.getNotificationCenter()
                int r6 = org.telegram.messenger.NotificationCenter.groupCallUpdated
                r7 = 4
                java.lang.Object[] r7 = new java.lang.Object[r7]
                long r8 = r0.chatId
                java.lang.Long r8 = java.lang.Long.valueOf(r8)
                r9 = 0
                r7[r9] = r8
                org.telegram.tgnet.TLRPC$GroupCall r8 = r0.call
                long r8 = r8.id
                java.lang.Long r8 = java.lang.Long.valueOf(r8)
                r9 = 1
                r7[r9] = r8
                java.lang.Boolean r8 = java.lang.Boolean.valueOf(r21)
                r7[r2] = r8
                java.lang.Long r8 = java.lang.Long.valueOf(r24)
                r7[r4] = r8
                r5.postNotificationName(r6, r7)
            L_0x0513:
                if (r23 == 0) goto L_0x053c
                org.telegram.messenger.AccountInstance r5 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r5 = r5.getNotificationCenter()
                int r6 = org.telegram.messenger.NotificationCenter.groupCallSpeakingUsersUpdated
                java.lang.Object[] r4 = new java.lang.Object[r4]
                long r7 = r0.chatId
                java.lang.Long r7 = java.lang.Long.valueOf(r7)
                r8 = 0
                r4[r8] = r7
                org.telegram.tgnet.TLRPC$GroupCall r7 = r0.call
                long r9 = r7.id
                java.lang.Long r7 = java.lang.Long.valueOf(r9)
                r9 = 1
                r4[r9] = r7
                java.lang.Boolean r7 = java.lang.Boolean.valueOf(r8)
                r4[r2] = r7
                r5.postNotificationName(r6, r4)
            L_0x053c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.processParticipantsUpdate(org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants, boolean):void");
        }

        private boolean isSameVideo(TLRPC.TL_groupCallParticipantVideo oldVideo, TLRPC.TL_groupCallParticipantVideo newVideo) {
            if ((oldVideo == null && newVideo != null) || (oldVideo != null && newVideo == null)) {
                return false;
            }
            if (oldVideo == null || newVideo == null) {
                return true;
            }
            if (!TextUtils.equals(oldVideo.endpoint, newVideo.endpoint) || oldVideo.source_groups.size() != newVideo.source_groups.size()) {
                return false;
            }
            int N = oldVideo.source_groups.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipantVideoSourceGroup oldGroup = oldVideo.source_groups.get(a);
                TLRPC.TL_groupCallParticipantVideoSourceGroup newGroup = newVideo.source_groups.get(a);
                if (!TextUtils.equals(oldGroup.semantics, newGroup.semantics) || oldGroup.sources.size() != newGroup.sources.size()) {
                    return false;
                }
                int N2 = oldGroup.sources.size();
                for (int b = 0; b < N2; b++) {
                    if (!newGroup.sources.contains(oldGroup.sources.get(b))) {
                        return false;
                    }
                }
            }
            return true;
        }

        public void processGroupCallUpdate(TLRPC.TL_updateGroupCall update) {
            if (this.call.version < update.call.version) {
                this.nextLoadOffset = null;
                loadMembers(true);
            }
            this.call = update.call;
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.participants.get(getSelfId());
            this.recording = this.call.record_start_date != 0;
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
        }

        public TLRPC.TL_inputGroupCall getInputGroupCall() {
            TLRPC.TL_inputGroupCall inputGroupCall = new TLRPC.TL_inputGroupCall();
            inputGroupCall.id = this.call.id;
            inputGroupCall.access_hash = this.call.access_hash;
            return inputGroupCall;
        }

        public static boolean videoIsActive(TLRPC.TL_groupCallParticipant participant, boolean presentation, Call call2) {
            VoIPService service;
            VideoParticipant videoParticipant;
            if (participant == null || (service = VoIPService.getSharedInstance()) == null) {
                return false;
            }
            if (!participant.self) {
                VideoParticipant videoParticipant2 = call2.rtmpStreamParticipant;
                if ((videoParticipant2 == null || videoParticipant2.participant != participant) && (((videoParticipant = call2.videoNotAvailableParticipant) == null || videoParticipant.participant != participant) && call2.participants.get(MessageObject.getPeerId(participant.peer)) == null)) {
                    return false;
                }
                if (presentation) {
                    if (participant.presentation != null) {
                        return true;
                    }
                    return false;
                } else if (participant.video != null) {
                    return true;
                } else {
                    return false;
                }
            } else if (service.getVideoState(presentation) == 2) {
                return true;
            } else {
                return false;
            }
        }

        public void sortParticipants() {
            TLRPC.TL_groupCallParticipant lastParticipant;
            boolean hasAnyVideo;
            boolean z;
            boolean z2;
            VideoParticipant videoParticipant;
            Comparator<TLRPC.TL_groupCallParticipant> comparator;
            this.visibleVideoParticipants.clear();
            this.visibleParticipants.clear();
            TLRPC.Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(this.chatId));
            boolean isAdmin = ChatObject.canManageCalls(chat);
            VideoParticipant videoParticipant2 = this.rtmpStreamParticipant;
            if (videoParticipant2 != null) {
                this.visibleVideoParticipants.add(videoParticipant2);
            }
            long selfId = getSelfId();
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.participants.get(selfId);
            this.canStreamVideo = true;
            boolean hasAnyVideo2 = false;
            boolean z3 = false;
            this.activeVideos = 0;
            int N = this.sortedParticipants.size();
            for (int i = 0; i < N; i++) {
                TLRPC.TL_groupCallParticipant participant = this.sortedParticipants.get(i);
                boolean cameraActive = videoIsActive(participant, false, this);
                boolean screenActive = videoIsActive(participant, true, this);
                if (!participant.self && (cameraActive || screenActive)) {
                    this.activeVideos++;
                }
                if (cameraActive || screenActive) {
                    hasAnyVideo2 = true;
                    if (!this.canStreamVideo) {
                        participant.videoIndex = 0;
                    } else if (participant.videoIndex == 0) {
                        if (participant.self) {
                            participant.videoIndex = Integer.MAX_VALUE;
                        } else {
                            int i2 = videoPointer + 1;
                            videoPointer = i2;
                            participant.videoIndex = i2;
                        }
                    }
                } else if (participant.self || !this.canStreamVideo || (participant.video == null && participant.presentation == null)) {
                    participant.videoIndex = 0;
                }
            }
            Comparator<TLRPC.TL_groupCallParticipant> comparator2 = new ChatObject$Call$$ExternalSyntheticLambda13(this, selfId, isAdmin);
            Collections.sort(this.sortedParticipants, comparator2);
            if (this.sortedParticipants.isEmpty()) {
                lastParticipant = null;
            } else {
                ArrayList<TLRPC.TL_groupCallParticipant> arrayList = this.sortedParticipants;
                lastParticipant = arrayList.get(arrayList.size() - 1);
            }
            if ((videoIsActive(lastParticipant, false, this) || videoIsActive(lastParticipant, true, this)) && this.call.unmuted_video_count > this.activeVideos) {
                this.activeVideos = this.call.unmuted_video_count;
                VoIPService voIPService = VoIPService.getSharedInstance();
                if (voIPService != null && voIPService.groupCall == this && (voIPService.getVideoState(false) == 2 || voIPService.getVideoState(true) == 2)) {
                    this.activeVideos--;
                }
            }
            int i3 = 5000;
            if (this.sortedParticipants.size() > 5000) {
                if (ChatObject.canManageCalls(chat)) {
                    hasAnyVideo = hasAnyVideo2;
                    if (lastParticipant.raise_hand_rating != 0) {
                        ChatObject$Call$$ExternalSyntheticLambda13 chatObject$Call$$ExternalSyntheticLambda13 = comparator2;
                    }
                } else {
                    hasAnyVideo = hasAnyVideo2;
                }
                int a = 5000;
                int N2 = this.sortedParticipants.size();
                while (a < N2) {
                    TLRPC.TL_groupCallParticipant p = this.sortedParticipants.get(i3);
                    if (p.raise_hand_rating != 0) {
                        comparator = comparator2;
                    } else {
                        processAllSources(p, z3);
                        comparator = comparator2;
                        this.participants.remove(MessageObject.getPeerId(p.peer));
                        this.sortedParticipants.remove(5000);
                    }
                    a++;
                    comparator2 = comparator;
                    z3 = false;
                    i3 = 5000;
                }
            } else {
                hasAnyVideo = hasAnyVideo2;
                Comparator<TLRPC.TL_groupCallParticipant> comparator3 = comparator2;
            }
            checkOnlineParticipants();
            if (!this.canStreamVideo && hasAnyVideo && (videoParticipant = this.videoNotAvailableParticipant) != null) {
                this.visibleVideoParticipants.add(videoParticipant);
            }
            int wideVideoIndex = 0;
            for (int i4 = 0; i4 < this.sortedParticipants.size(); i4++) {
                TLRPC.TL_groupCallParticipant participant2 = this.sortedParticipants.get(i4);
                if (!this.canStreamVideo || participant2.videoIndex == 0) {
                    this.visibleParticipants.add(participant2);
                } else if (!participant2.self && videoIsActive(participant2, true, this) && videoIsActive(participant2, false, this)) {
                    VideoParticipant videoParticipant3 = this.videoParticipantsCache.get(participant2.videoEndpoint);
                    if (videoParticipant3 == null) {
                        videoParticipant3 = new VideoParticipant(participant2, false, true);
                        this.videoParticipantsCache.put(participant2.videoEndpoint, videoParticipant3);
                        z2 = true;
                    } else {
                        videoParticipant3.participant = participant2;
                        videoParticipant3.presentation = false;
                        z2 = true;
                        videoParticipant3.hasSame = true;
                    }
                    VideoParticipant presentationParticipant = this.videoParticipantsCache.get(participant2.presentationEndpoint);
                    if (presentationParticipant == null) {
                        presentationParticipant = new VideoParticipant(participant2, z2, z2);
                    } else {
                        presentationParticipant.participant = participant2;
                        presentationParticipant.presentation = z2;
                        presentationParticipant.hasSame = z2;
                    }
                    this.visibleVideoParticipants.add(videoParticipant3);
                    if (videoParticipant3.aspectRatio > 1.0f) {
                        wideVideoIndex = this.visibleVideoParticipants.size() - 1;
                    }
                    this.visibleVideoParticipants.add(presentationParticipant);
                    if (presentationParticipant.aspectRatio > 1.0f) {
                        wideVideoIndex = this.visibleVideoParticipants.size() - 1;
                    }
                } else if (participant2.self) {
                    if (videoIsActive(participant2, true, this)) {
                        z = false;
                        this.visibleVideoParticipants.add(new VideoParticipant(participant2, true, false));
                    } else {
                        z = false;
                    }
                    if (videoIsActive(participant2, z, this)) {
                        this.visibleVideoParticipants.add(new VideoParticipant(participant2, z, z));
                    }
                } else {
                    boolean presentation = videoIsActive(participant2, true, this);
                    VideoParticipant videoParticipant4 = this.videoParticipantsCache.get(presentation ? participant2.presentationEndpoint : participant2.videoEndpoint);
                    if (videoParticipant4 == null) {
                        videoParticipant4 = new VideoParticipant(participant2, presentation, false);
                        this.videoParticipantsCache.put(presentation ? participant2.presentationEndpoint : participant2.videoEndpoint, videoParticipant4);
                    } else {
                        videoParticipant4.participant = participant2;
                        videoParticipant4.presentation = presentation;
                        videoParticipant4.hasSame = false;
                    }
                    this.visibleVideoParticipants.add(videoParticipant4);
                    if (videoParticipant4.aspectRatio > 1.0f) {
                        wideVideoIndex = this.visibleVideoParticipants.size() - 1;
                    }
                }
            }
            if (GroupCallActivity.isLandscapeMode == 0 && this.visibleVideoParticipants.size() % 2 == 1) {
                this.visibleVideoParticipants.add(this.visibleVideoParticipants.remove(wideVideoIndex));
            }
        }

        /* renamed from: lambda$sortParticipants$12$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ int m1792x43a3748(long selfId, boolean isAdmin, TLRPC.TL_groupCallParticipant o1, TLRPC.TL_groupCallParticipant o2) {
            boolean videoActive2 = false;
            boolean videoActive1 = o1.videoIndex > 0;
            if (o2.videoIndex > 0) {
                videoActive2 = true;
            }
            if (videoActive1 && videoActive2) {
                return o2.videoIndex - o1.videoIndex;
            }
            if (videoActive1) {
                return -1;
            }
            if (videoActive2) {
                return 1;
            }
            if (o1.active_date != 0 && o2.active_date != 0) {
                return zzby$$ExternalSyntheticBackport0.m(o2.active_date, o1.active_date);
            }
            if (o1.active_date != 0) {
                return -1;
            }
            if (o2.active_date != 0) {
                return 1;
            }
            if (MessageObject.getPeerId(o1.peer) == selfId) {
                return -1;
            }
            if (MessageObject.getPeerId(o2.peer) == selfId) {
                return 1;
            }
            if (isAdmin) {
                if (o1.raise_hand_rating != 0 && o2.raise_hand_rating != 0) {
                    return (o2.raise_hand_rating > o1.raise_hand_rating ? 1 : (o2.raise_hand_rating == o1.raise_hand_rating ? 0 : -1));
                }
                if (o1.raise_hand_rating != 0) {
                    return -1;
                }
                if (o2.raise_hand_rating != 0) {
                    return 1;
                }
            }
            if (this.call.join_date_asc) {
                return zzby$$ExternalSyntheticBackport0.m(o1.date, o2.date);
            }
            return zzby$$ExternalSyntheticBackport0.m(o2.date, o1.date);
        }

        public boolean canRecordVideo() {
            if (!this.canStreamVideo) {
                return false;
            }
            VoIPService voIPService = VoIPService.getSharedInstance();
            if ((voIPService == null || voIPService.groupCall != this || (voIPService.getVideoState(false) != 2 && voIPService.getVideoState(true) != 2)) && this.activeVideos >= this.call.unmuted_video_limit) {
                return false;
            }
            return true;
        }

        public void saveActiveDates() {
            int N = this.sortedParticipants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant p = this.sortedParticipants.get(a);
                p.lastActiveDate = (long) p.active_date;
            }
        }

        private void checkOnlineParticipants() {
            if (this.typingUpdateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.typingUpdateRunnable);
                this.typingUpdateRunnableScheduled = false;
            }
            this.speakingMembersCount = 0;
            int currentTime = this.currentAccount.getConnectionsManager().getCurrentTime();
            int minDiff = Integer.MAX_VALUE;
            int N = this.sortedParticipants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant participant = this.sortedParticipants.get(a);
                int diff = currentTime - participant.active_date;
                if (diff < 5) {
                    this.speakingMembersCount++;
                    minDiff = Math.min(diff, minDiff);
                }
                if (Math.max(participant.date, participant.active_date) <= currentTime - 5) {
                    break;
                }
            }
            if (minDiff != Integer.MAX_VALUE) {
                AndroidUtilities.runOnUIThread(this.typingUpdateRunnable, (long) (minDiff * 1000));
                this.typingUpdateRunnableScheduled = true;
            }
        }

        public void toggleRecord(String title, int type) {
            this.recording = !this.recording;
            TLRPC.TL_phone_toggleGroupCallRecord req = new TLRPC.TL_phone_toggleGroupCallRecord();
            req.call = getInputGroupCall();
            req.start = this.recording;
            if (title != null) {
                req.title = title;
                req.flags |= 2;
            }
            if (type == 1 || type == 2) {
                req.flags |= 4;
                req.video = true;
                req.video_portrait = type == 1;
            }
            this.currentAccount.getConnectionsManager().sendRequest(req, new ChatObject$Call$$ExternalSyntheticLambda3(this));
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
        }

        /* renamed from: lambda$toggleRecord$13$org-telegram-messenger-ChatObject$Call  reason: not valid java name */
        public /* synthetic */ void m1793lambda$toggleRecord$13$orgtelegrammessengerChatObject$Call(TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                this.currentAccount.getMessagesController().processUpdates((TLRPC.Updates) response, false);
            }
        }
    }

    public static int getParticipantVolume(TLRPC.TL_groupCallParticipant participant) {
        if ((participant.flags & 128) != 0) {
            return participant.volume;
        }
        return 10000;
    }

    private static boolean isBannableAction(int action) {
        switch (action) {
            case 0:
            case 1:
            case 3:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return true;
            default:
                return false;
        }
    }

    private static boolean isAdminAction(int action) {
        switch (action) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 12:
            case 13:
                return true;
            default:
                return false;
        }
    }

    private static boolean getBannedRight(TLRPC.TL_chatBannedRights rights, int action) {
        if (rights == null) {
            return false;
        }
        switch (action) {
            case 0:
                return rights.pin_messages;
            case 1:
                return rights.change_info;
            case 3:
                return rights.invite_users;
            case 6:
                return rights.send_messages;
            case 7:
                return rights.send_media;
            case 8:
                return rights.send_stickers;
            case 9:
                return rights.embed_links;
            case 10:
                return rights.send_polls;
            case 11:
                return rights.view_messages;
            default:
                return false;
        }
    }

    public static boolean isActionBannedByDefault(TLRPC.Chat chat, int action) {
        if (getBannedRight(chat.banned_rights, action)) {
            return false;
        }
        return getBannedRight(chat.default_banned_rights, action);
    }

    public static boolean isActionBanned(TLRPC.Chat chat, int action) {
        return chat != null && (getBannedRight(chat.banned_rights, action) || getBannedRight(chat.default_banned_rights, action));
    }

    public static boolean canUserDoAdminAction(TLRPC.Chat chat, int action) {
        boolean value;
        if (chat == null) {
            return false;
        }
        if (chat.creator) {
            return true;
        }
        if (chat.admin_rights != null) {
            switch (action) {
                case 0:
                    value = chat.admin_rights.pin_messages;
                    break;
                case 1:
                    value = chat.admin_rights.change_info;
                    break;
                case 2:
                    value = chat.admin_rights.ban_users;
                    break;
                case 3:
                    value = chat.admin_rights.invite_users;
                    break;
                case 4:
                    value = chat.admin_rights.add_admins;
                    break;
                case 5:
                    value = chat.admin_rights.post_messages;
                    break;
                case 12:
                    value = chat.admin_rights.edit_messages;
                    break;
                case 13:
                    value = chat.admin_rights.delete_messages;
                    break;
                case 14:
                    value = chat.admin_rights.manage_call;
                    break;
                default:
                    value = false;
                    break;
            }
            if (value) {
                return true;
            }
        }
        return false;
    }

    public static boolean canUserDoAction(TLRPC.Chat chat, int action) {
        if (chat == null || canUserDoAdminAction(chat, action)) {
            return true;
        }
        if (getBannedRight(chat.banned_rights, action) || !isBannableAction(action)) {
            return false;
        }
        if (chat.admin_rights != null && !isAdminAction(action)) {
            return true;
        }
        if (chat.default_banned_rights == null && ((chat instanceof TLRPC.TL_chat_layer92) || (chat instanceof TLRPC.TL_chat_old) || (chat instanceof TLRPC.TL_chat_old2) || (chat instanceof TLRPC.TL_channel_layer92) || (chat instanceof TLRPC.TL_channel_layer77) || (chat instanceof TLRPC.TL_channel_layer72) || (chat instanceof TLRPC.TL_channel_layer67) || (chat instanceof TLRPC.TL_channel_layer48) || (chat instanceof TLRPC.TL_channel_old))) {
            return true;
        }
        if (chat.default_banned_rights == null || getBannedRight(chat.default_banned_rights, action)) {
            return false;
        }
        return true;
    }

    public static boolean isLeftFromChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.left || chat.deactivated;
    }

    public static boolean isKickedFromChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.kicked || chat.deactivated || (chat.banned_rights != null && chat.banned_rights.view_messages);
    }

    public static boolean isNotInChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.left || chat.kicked || chat.deactivated;
    }

    public static boolean canSendAsPeers(TLRPC.Chat chat) {
        return isChannel(chat) && chat.megagroup && (!TextUtils.isEmpty(chat.username) || chat.has_geo || chat.has_link);
    }

    public static boolean isChannel(TLRPC.Chat chat) {
        return (chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden);
    }

    public static boolean isChannelOrGiga(TLRPC.Chat chat) {
        return ((chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden)) && (!chat.megagroup || chat.gigagroup);
    }

    public static boolean isMegagroup(TLRPC.Chat chat) {
        return ((chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden)) && chat.megagroup;
    }

    public static boolean isChannelAndNotMegaGroup(TLRPC.Chat chat) {
        return isChannel(chat) && !isMegagroup(chat);
    }

    public static boolean isMegagroup(int currentAccount, long chatId) {
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(chatId));
        return isChannel(chat) && chat.megagroup;
    }

    public static boolean hasAdminRights(TLRPC.Chat chat) {
        return chat != null && (chat.creator || !(chat.admin_rights == null || chat.admin_rights.flags == 0));
    }

    public static boolean canChangeChatInfo(TLRPC.Chat chat) {
        return canUserDoAction(chat, 1);
    }

    public static boolean canAddAdmins(TLRPC.Chat chat) {
        return canUserDoAction(chat, 4);
    }

    public static boolean canBlockUsers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 2);
    }

    public static boolean canManageCalls(TLRPC.Chat chat) {
        return canUserDoAction(chat, 14);
    }

    public static boolean canSendStickers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 8);
    }

    public static boolean canSendEmbed(TLRPC.Chat chat) {
        return canUserDoAction(chat, 9);
    }

    public static boolean canSendMedia(TLRPC.Chat chat) {
        return canUserDoAction(chat, 7);
    }

    public static boolean canSendPolls(TLRPC.Chat chat) {
        return canUserDoAction(chat, 10);
    }

    public static boolean canSendMessages(TLRPC.Chat chat) {
        return canUserDoAction(chat, 6);
    }

    public static boolean canPost(TLRPC.Chat chat) {
        return canUserDoAction(chat, 5);
    }

    public static boolean canAddUsers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 3);
    }

    public static boolean shouldSendAnonymously(TLRPC.Chat chat) {
        return (chat == null || chat.admin_rights == null || !chat.admin_rights.anonymous) ? false : true;
    }

    public static long getSendAsPeerId(TLRPC.Chat chat, TLRPC.ChatFull chatFull) {
        return getSendAsPeerId(chat, chatFull, false);
    }

    public static long getSendAsPeerId(TLRPC.Chat chat, TLRPC.ChatFull chatFull, boolean invertChannel) {
        if (chat != null && chatFull != null && chatFull.default_send_as != null) {
            TLRPC.Peer p = chatFull.default_send_as;
            if (p.user_id != 0) {
                return p.user_id;
            }
            long j = p.channel_id;
            return invertChannel ? -j : j;
        } else if (chat == null || chat.admin_rights == null || !chat.admin_rights.anonymous) {
            return UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        } else {
            long j2 = chat.id;
            return invertChannel ? -j2 : j2;
        }
    }

    public static boolean canAddBotsToChat(TLRPC.Chat chat) {
        if (isChannel(chat)) {
            if (!chat.megagroup) {
                return false;
            }
            if ((chat.admin_rights == null || (!chat.admin_rights.post_messages && !chat.admin_rights.add_admins)) && !chat.creator) {
                return false;
            }
            return true;
        } else if (chat.migrated_to == null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean canPinMessages(TLRPC.Chat chat) {
        return canUserDoAction(chat, 0) || (isChannel(chat) && !chat.megagroup && chat.admin_rights != null && chat.admin_rights.edit_messages);
    }

    public static boolean isChannel(long chatId, int currentAccount) {
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(chatId));
        return (chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden);
    }

    public static boolean isChannelAndNotMegaGroup(long chatId, int currentAccount) {
        return isChannelAndNotMegaGroup(MessagesController.getInstance(currentAccount).getChat(Long.valueOf(chatId)));
    }

    public static boolean isCanWriteToChannel(long chatId, int currentAccount) {
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(chatId));
        return canSendMessages(chat) || chat.megagroup;
    }

    public static boolean canWriteToChat(TLRPC.Chat chat) {
        return !isChannel(chat) || chat.creator || (chat.admin_rights != null && chat.admin_rights.post_messages) || ((!chat.broadcast && !chat.gigagroup) || (chat.gigagroup && hasAdminRights(chat)));
    }

    public static String getBannedRightsString(TLRPC.TL_chatBannedRights bannedRights) {
        return (((((((((((("" + (bannedRights.view_messages ? 1 : 0)) + (bannedRights.send_messages ? 1 : 0)) + (bannedRights.send_media ? 1 : 0)) + (bannedRights.send_stickers ? 1 : 0)) + (bannedRights.send_gifs ? 1 : 0)) + (bannedRights.send_games ? 1 : 0)) + (bannedRights.send_inline ? 1 : 0)) + (bannedRights.embed_links ? 1 : 0)) + (bannedRights.send_polls ? 1 : 0)) + (bannedRights.invite_users ? 1 : 0)) + (bannedRights.change_info ? 1 : 0)) + (bannedRights.pin_messages ? 1 : 0)) + bannedRights.until_date;
    }

    public static boolean hasPhoto(TLRPC.Chat chat) {
        return (chat == null || chat.photo == null || (chat.photo instanceof TLRPC.TL_chatPhotoEmpty)) ? false : true;
    }

    public static TLRPC.ChatPhoto getPhoto(TLRPC.Chat chat) {
        if (hasPhoto(chat)) {
            return chat.photo;
        }
        return null;
    }

    public static class VideoParticipant {
        public float aspectRatio;
        public int aspectRatioFromHeight;
        public int aspectRatioFromWidth;
        public boolean hasSame;
        public TLRPC.TL_groupCallParticipant participant;
        public boolean presentation;

        public VideoParticipant(TLRPC.TL_groupCallParticipant participant2, boolean presentation2, boolean hasSame2) {
            this.participant = participant2;
            this.presentation = presentation2;
            this.hasSame = hasSame2;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            VideoParticipant that = (VideoParticipant) o;
            if (this.presentation == that.presentation && MessageObject.getPeerId(this.participant.peer) == MessageObject.getPeerId(that.participant.peer)) {
                return true;
            }
            return false;
        }

        public void setAspectRatio(int width, int height, Call call) {
            this.aspectRatioFromWidth = width;
            this.aspectRatioFromHeight = height;
            setAspectRatio(((float) width) / ((float) height), call);
        }

        private void setAspectRatio(float aspectRatio2, Call call) {
            if (this.aspectRatio != aspectRatio2) {
                this.aspectRatio = aspectRatio2;
                if (!GroupCallActivity.isLandscapeMode && call.visibleVideoParticipants.size() % 2 == 1) {
                    call.updateVisibleParticipants();
                }
            }
        }
    }
}
