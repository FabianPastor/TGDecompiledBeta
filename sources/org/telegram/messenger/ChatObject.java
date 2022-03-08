package org.telegram.messenger;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.internal.mlkit_language_id.zzdp$$ExternalSyntheticBackport0;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.RequestDelegateTimestamp;
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
import org.telegram.tgnet.TLRPC$TL_groupCallStreamChannel;
import org.telegram.tgnet.TLRPC$TL_inputGroupCall;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_phone_editGroupCallTitle;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupCallStreamChannels;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupParticipants;
import org.telegram.tgnet.TLRPC$TL_phone_groupCall;
import org.telegram.tgnet.TLRPC$TL_phone_groupCallStreamChannels;
import org.telegram.tgnet.TLRPC$TL_phone_groupParticipants;
import org.telegram.tgnet.TLRPC$TL_phone_toggleGroupCallRecord;
import org.telegram.tgnet.TLRPC$TL_updateGroupCall;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.WriteToSocketDelegate;
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
        public long chatId;
        private Runnable checkQueueRunnable;
        public AccountInstance currentAccount;
        public final LongSparseArray<TLRPC$TL_groupCallParticipant> currentSpeakingPeers = new LongSparseArray<>();
        public ArrayList<Long> invitedUsers = new ArrayList<>();
        public HashSet<Long> invitedUsersMap = new HashSet<>();
        private long lastGroupCallReloadTime;
        private int lastLoadGuid;
        private boolean loadedRtmpStreamParticipant;
        private boolean loadingGroupCall;
        private HashSet<Integer> loadingGuids = new HashSet<>();
        public boolean loadingMembers;
        private HashSet<Long> loadingSsrcs = new HashSet<>();
        private HashSet<Long> loadingUids = new HashSet<>();
        public boolean membersLoadEndReached;
        private String nextLoadOffset;
        public LongSparseArray<TLRPC$TL_groupCallParticipant> participants = new LongSparseArray<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsByPresentationSources = new SparseArray<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsBySources = new SparseArray<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsByVideoSources = new SparseArray<>();
        public boolean recording;
        public boolean reloadingMembers;
        public VideoParticipant rtmpStreamParticipant;
        public TLRPC$Peer selfPeer;
        public final ArrayList<TLRPC$TL_groupCallParticipant> sortedParticipants = new ArrayList<>();
        public int speakingMembersCount;
        public final HashMap<String, Bitmap> thumbs = new HashMap<>();
        private Runnable typingUpdateRunnable = new ChatObject$Call$$ExternalSyntheticLambda1(this);
        private boolean typingUpdateRunnableScheduled;
        /* access modifiers changed from: private */
        public final Runnable updateCurrentSpeakingRunnable = new Runnable() {
            public void run() {
                long uptimeMillis = SystemClock.uptimeMillis();
                int i = 0;
                boolean z = false;
                while (i < Call.this.currentSpeakingPeers.size()) {
                    long keyAt = Call.this.currentSpeakingPeers.keyAt(i);
                    if (uptimeMillis - Call.this.currentSpeakingPeers.get(keyAt).lastSpeakTime >= 500) {
                        Call.this.currentSpeakingPeers.remove(keyAt);
                        String str = null;
                        if (keyAt > 0) {
                            TLRPC$User user = MessagesController.getInstance(Call.this.currentAccount.getCurrentAccount()).getUser(Long.valueOf(keyAt));
                            StringBuilder sb = new StringBuilder();
                            sb.append("remove from speaking ");
                            sb.append(keyAt);
                            sb.append(" ");
                            if (user != null) {
                                str = user.first_name;
                            }
                            sb.append(str);
                            Log.d("GroupCall", sb.toString());
                        } else {
                            TLRPC$Chat chat = MessagesController.getInstance(Call.this.currentAccount.getCurrentAccount()).getChat(Long.valueOf(-keyAt));
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("remove from speaking ");
                            sb2.append(keyAt);
                            sb2.append(" ");
                            if (chat != null) {
                                str = chat.title;
                            }
                            sb2.append(str);
                            Log.d("GroupCall", sb2.toString());
                        }
                        i--;
                        z = true;
                    }
                    i++;
                }
                if (Call.this.currentSpeakingPeers.size() > 0) {
                    AndroidUtilities.runOnUIThread(Call.this.updateCurrentSpeakingRunnable, 550);
                }
                if (z) {
                    Call.this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallSpeakingUsersUpdated, Long.valueOf(Call.this.chatId), Long.valueOf(Call.this.call.id), Boolean.FALSE);
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
            void onLoad(ArrayList<Long> arrayList);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            this.typingUpdateRunnableScheduled = false;
            checkOnlineParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallTypingsUpdated, new Object[0]);
        }

        public void setCall(AccountInstance accountInstance, long j, TLRPC$TL_phone_groupCall tLRPC$TL_phone_groupCall) {
            this.chatId = j;
            this.currentAccount = accountInstance;
            TLRPC$GroupCall tLRPC$GroupCall = tLRPC$TL_phone_groupCall.call;
            this.call = tLRPC$GroupCall;
            this.recording = tLRPC$GroupCall.record_start_date != 0;
            int i = Integer.MAX_VALUE;
            int size = tLRPC$TL_phone_groupCall.participants.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_phone_groupCall.participants.get(i2);
                this.participants.put(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer), tLRPC$TL_groupCallParticipant);
                this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                processAllSources(tLRPC$TL_groupCallParticipant, true);
                i = Math.min(i, tLRPC$TL_groupCallParticipant.date);
            }
            sortParticipants();
            this.nextLoadOffset = tLRPC$TL_phone_groupCall.participants_next_offset;
            loadMembers(true);
            createNoVideoParticipant();
            if (this.call.rtmp_stream) {
                createRtmpStreamParticipant(Collections.emptyList());
            }
        }

        public void loadRtmpStreamChannels() {
            if (this.call != null && !this.loadedRtmpStreamParticipant) {
                TLRPC$TL_phone_getGroupCallStreamChannels tLRPC$TL_phone_getGroupCallStreamChannels = new TLRPC$TL_phone_getGroupCallStreamChannels();
                tLRPC$TL_phone_getGroupCallStreamChannels.call = getInputGroupCall();
                this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCallStreamChannels, new ChatObject$Call$$ExternalSyntheticLambda12(this), (RequestDelegateTimestamp) null, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.call.stream_dc_id, 1, true);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadRtmpStreamChannels$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject instanceof TLRPC$TL_phone_groupCallStreamChannels) {
                createRtmpStreamParticipant(((TLRPC$TL_phone_groupCallStreamChannels) tLObject).channels);
                this.loadedRtmpStreamParticipant = true;
            }
        }

        public void createRtmpStreamParticipant(List<TLRPC$TL_groupCallStreamChannel> list) {
            if (!this.loadedRtmpStreamParticipant || this.rtmpStreamParticipant == null) {
                VideoParticipant videoParticipant = this.rtmpStreamParticipant;
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = videoParticipant != null ? videoParticipant.participant : new TLRPC$TL_groupCallParticipant();
                TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                tLRPC$TL_groupCallParticipant.peer = tLRPC$TL_peerChat;
                tLRPC$TL_peerChat.channel_id = this.chatId;
                tLRPC$TL_groupCallParticipant.video = new TLRPC$TL_groupCallParticipantVideo();
                TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup = new TLRPC$TL_groupCallParticipantVideoSourceGroup();
                tLRPC$TL_groupCallParticipantVideoSourceGroup.semantics = "SIM";
                for (TLRPC$TL_groupCallStreamChannel tLRPC$TL_groupCallStreamChannel : list) {
                    tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.add(Integer.valueOf(tLRPC$TL_groupCallStreamChannel.channel));
                }
                tLRPC$TL_groupCallParticipant.video.source_groups.add(tLRPC$TL_groupCallParticipantVideoSourceGroup);
                tLRPC$TL_groupCallParticipant.video.endpoint = "unified";
                tLRPC$TL_groupCallParticipant.videoEndpoint = "unified";
                this.rtmpStreamParticipant = new VideoParticipant(tLRPC$TL_groupCallParticipant, false, false);
                sortParticipants();
                AndroidUtilities.runOnUIThread(new ChatObject$Call$$ExternalSyntheticLambda2(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$createRtmpStreamParticipant$2() {
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
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
            long selfId = getSelfId();
            if (this.participants.indexOfKey(selfId) < 0) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = new TLRPC$TL_groupCallParticipant();
                tLRPC$TL_groupCallParticipant.peer = this.selfPeer;
                tLRPC$TL_groupCallParticipant.muted = true;
                tLRPC$TL_groupCallParticipant.self = true;
                tLRPC$TL_groupCallParticipant.video_joined = this.call.can_start_video;
                TLRPC$Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(this.chatId));
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
                    this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
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
            TLRPC$GroupCall tLRPC$GroupCall = this.call;
            return tLRPC$GroupCall.participants_count > 0 || tLRPC$GroupCall.rtmp_stream || isScheduled();
        }

        public boolean isScheduled() {
            return (this.call.flags & 128) != 0;
        }

        private long getSelfId() {
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
            LongSparseArray<TLRPC$TL_groupCallParticipant> longSparseArray = null;
            if (TextUtils.isEmpty(str)) {
                if (this.participants.size() != 0) {
                    longSparseArray = this.participants;
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
                } else if (!(longSparseArray == null || (tLRPC$TL_groupCallParticipant2 = longSparseArray.get(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer))) == null)) {
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
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
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
                this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new ChatObject$Call$$ExternalSyntheticLambda15(this, z, tLRPC$TL_phone_getGroupParticipants));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadMembers$4(boolean z, TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new ChatObject$Call$$ExternalSyntheticLambda6(this, z, tLObject, tLRPC$TL_phone_getGroupParticipants));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadMembers$3(boolean z, TLObject tLObject, TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants) {
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
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_editGroupCallTitle, new ChatObject$Call$$ExternalSyntheticLambda10(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setTitle$5(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                this.currentAccount.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            }
        }

        public void addInvitedUser(long j) {
            if (this.participants.get(j) == null && !this.invitedUsersMap.contains(Long.valueOf(j))) {
                this.invitedUsersMap.add(Long.valueOf(j));
                this.invitedUsers.add(Long.valueOf(j));
            }
        }

        public void processTypingsUpdate(AccountInstance accountInstance, ArrayList<Long> arrayList, int i) {
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(SystemClock.elapsedRealtime()));
            int size = arrayList.size();
            ArrayList arrayList2 = null;
            boolean z = false;
            for (int i2 = 0; i2 < size; i2++) {
                Long l = arrayList.get(i2);
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.participants.get(l.longValue());
                if (tLRPC$TL_groupCallParticipant == null) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(l);
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
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        private void loadUnknownParticipants(ArrayList<Long> arrayList, boolean z, OnParticipantsLoad onParticipantsLoad) {
            TLRPC$InputPeer tLRPC$InputPeer;
            HashSet<Long> hashSet = z ? this.loadingUids : this.loadingSsrcs;
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
                int size2 = arrayList.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    long longValue = arrayList.get(i3).longValue();
                    if (!z) {
                        tLRPC$TL_phone_getGroupParticipants.sources.add(Integer.valueOf((int) longValue));
                    } else if (longValue > 0) {
                        TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
                        tLRPC$TL_inputPeerUser.user_id = longValue;
                        tLRPC$TL_phone_getGroupParticipants.ids.add(tLRPC$TL_inputPeerUser);
                    } else {
                        long j = -longValue;
                        TLRPC$Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(j));
                        if (chat == null || ChatObject.isChannel(chat)) {
                            tLRPC$InputPeer = new TLRPC$TL_inputPeerChannel();
                            tLRPC$InputPeer.channel_id = j;
                        } else {
                            tLRPC$InputPeer = new TLRPC$TL_inputPeerChat();
                            tLRPC$InputPeer.chat_id = j;
                        }
                        tLRPC$TL_phone_getGroupParticipants.ids.add(tLRPC$InputPeer);
                    }
                }
                tLRPC$TL_phone_getGroupParticipants.offset = "";
                tLRPC$TL_phone_getGroupParticipants.limit = 100;
                this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new ChatObject$Call$$ExternalSyntheticLambda14(this, i2, onParticipantsLoad, arrayList, hashSet));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadUnknownParticipants$7(int i, OnParticipantsLoad onParticipantsLoad, ArrayList arrayList, HashSet hashSet, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new ChatObject$Call$$ExternalSyntheticLambda3(this, i, tLObject, onParticipantsLoad, arrayList, hashSet));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadUnknownParticipants$6(int i, TLObject tLObject, OnParticipantsLoad onParticipantsLoad, ArrayList arrayList, HashSet hashSet) {
            if (this.loadingGuids.remove(Integer.valueOf(i))) {
                if (tLObject != null) {
                    TLRPC$TL_phone_groupParticipants tLRPC$TL_phone_groupParticipants = (TLRPC$TL_phone_groupParticipants) tLObject;
                    this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupParticipants.users, false);
                    this.currentAccount.getMessagesController().putChats(tLRPC$TL_phone_groupParticipants.chats, false);
                    int size = tLRPC$TL_phone_groupParticipants.participants.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_phone_groupParticipants.participants.get(i2);
                        long peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.participants.get(peerId);
                        if (tLRPC$TL_groupCallParticipant2 != null) {
                            this.sortedParticipants.remove(tLRPC$TL_groupCallParticipant2);
                            processAllSources(tLRPC$TL_groupCallParticipant2, false);
                        }
                        this.participants.put(peerId, tLRPC$TL_groupCallParticipant);
                        this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                        processAllSources(tLRPC$TL_groupCallParticipant, true);
                        if (this.invitedUsersMap.contains(Long.valueOf(peerId))) {
                            Long valueOf = Long.valueOf(peerId);
                            this.invitedUsersMap.remove(valueOf);
                            this.invitedUsers.remove(valueOf);
                        }
                    }
                    if (this.call.participants_count < this.participants.size()) {
                        this.call.participants_count = this.participants.size();
                    }
                    sortParticipants();
                    this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
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
            boolean z;
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
            long j;
            boolean z2;
            int i;
            ArrayList arrayList;
            int[] iArr2 = iArr;
            int currentTime = this.currentAccount.getConnectionsManager().getCurrentTime();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptimeMillis = SystemClock.uptimeMillis();
            int i2 = 1;
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(elapsedRealtime));
            int i3 = 0;
            ArrayList arrayList2 = null;
            boolean z3 = false;
            boolean z4 = false;
            while (i3 < iArr2.length) {
                if (iArr2[i3] == 0) {
                    z = z4;
                    tLRPC$TL_groupCallParticipant = this.participants.get(getSelfId());
                } else {
                    z = z4;
                    tLRPC$TL_groupCallParticipant = this.participantsBySources.get(iArr2[i3]);
                }
                if (tLRPC$TL_groupCallParticipant != null) {
                    tLRPC$TL_groupCallParticipant.hasVoice = zArr[i3];
                    if (zArr[i3] || elapsedRealtime - tLRPC$TL_groupCallParticipant.lastVoiceUpdateTime > 500) {
                        tLRPC$TL_groupCallParticipant.hasVoiceDelayed = zArr[i3];
                        tLRPC$TL_groupCallParticipant.lastVoiceUpdateTime = elapsedRealtime;
                    }
                    long peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                    if (fArr[i3] > 0.1f) {
                        if (zArr[i3]) {
                            z2 = z3;
                            arrayList = arrayList2;
                            if (tLRPC$TL_groupCallParticipant.lastTypingDate + i2 < currentTime) {
                                if (elapsedRealtime != tLRPC$TL_groupCallParticipant.lastVisibleDate) {
                                    tLRPC$TL_groupCallParticipant.active_date = currentTime;
                                }
                                tLRPC$TL_groupCallParticipant.lastTypingDate = currentTime;
                                z2 = true;
                            }
                        } else {
                            arrayList = arrayList2;
                            z2 = z3;
                        }
                        tLRPC$TL_groupCallParticipant.lastSpeakTime = uptimeMillis;
                        tLRPC$TL_groupCallParticipant.amplitude = fArr[i3];
                        if (this.currentSpeakingPeers.get(peerId, null) == null) {
                            if (peerId > 0) {
                                TLRPC$User user = MessagesController.getInstance(this.currentAccount.getCurrentAccount()).getUser(Long.valueOf(peerId));
                                StringBuilder sb = new StringBuilder();
                                sb.append("add to current speaking ");
                                sb.append(peerId);
                                sb.append(" ");
                                sb.append(user == null ? null : user.first_name);
                                Log.d("GroupCall", sb.toString());
                                i = currentTime;
                                j = elapsedRealtime;
                            } else {
                                i = currentTime;
                                j = elapsedRealtime;
                                TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount.getCurrentAccount()).getChat(Long.valueOf(-peerId));
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("add to current speaking ");
                                sb2.append(peerId);
                                sb2.append(" ");
                                sb2.append(chat == null ? null : chat.title);
                                Log.d("GroupCall", sb2.toString());
                            }
                            this.currentSpeakingPeers.put(peerId, tLRPC$TL_groupCallParticipant);
                            z4 = true;
                        } else {
                            i = currentTime;
                            j = elapsedRealtime;
                        }
                    } else {
                        j = elapsedRealtime;
                        arrayList = arrayList2;
                        z2 = z3;
                        i = currentTime;
                        if (uptimeMillis - tLRPC$TL_groupCallParticipant.lastSpeakTime < 500 || this.currentSpeakingPeers.get(peerId, null) == null) {
                            z4 = z;
                        } else {
                            this.currentSpeakingPeers.remove(peerId);
                            if (peerId > 0) {
                                TLRPC$User user2 = MessagesController.getInstance(this.currentAccount.getCurrentAccount()).getUser(Long.valueOf(peerId));
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("remove from speaking ");
                                sb3.append(peerId);
                                sb3.append(" ");
                                sb3.append(user2 == null ? null : user2.first_name);
                                Log.d("GroupCall", sb3.toString());
                            } else {
                                TLRPC$Chat chat2 = MessagesController.getInstance(this.currentAccount.getCurrentAccount()).getChat(Long.valueOf(-peerId));
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("remove from speaking ");
                                sb4.append(peerId);
                                sb4.append(" ");
                                sb4.append(chat2 == null ? null : chat2.title);
                                Log.d("GroupCall", sb4.toString());
                            }
                            z4 = true;
                        }
                        tLRPC$TL_groupCallParticipant.amplitude = 0.0f;
                    }
                    arrayList2 = arrayList;
                    i3++;
                    currentTime = i;
                    z3 = z2;
                    elapsedRealtime = j;
                    i2 = 1;
                } else {
                    j = elapsedRealtime;
                    arrayList = arrayList2;
                    z2 = z3;
                    i = currentTime;
                    if (iArr2[i3] != 0) {
                        arrayList2 = arrayList == null ? new ArrayList() : arrayList;
                        arrayList2.add(Long.valueOf((long) iArr2[i3]));
                        z4 = z;
                        i3++;
                        currentTime = i;
                        z3 = z2;
                        elapsedRealtime = j;
                        i2 = 1;
                    }
                }
                z4 = z;
                arrayList2 = arrayList;
                i3++;
                currentTime = i;
                z3 = z2;
                elapsedRealtime = j;
                i2 = 1;
            }
            ArrayList arrayList3 = arrayList2;
            boolean z5 = z3;
            boolean z6 = z4;
            if (arrayList3 != null) {
                loadUnknownParticipants(arrayList3, false, (OnParticipantsLoad) null);
            }
            if (z5) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
            if (z6) {
                if (this.currentSpeakingPeers.size() > 0) {
                    AndroidUtilities.cancelRunOnUIThread(this.updateCurrentSpeakingRunnable);
                    AndroidUtilities.runOnUIThread(this.updateCurrentSpeakingRunnable, 550);
                }
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallSpeakingUsersUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        public void updateVisibleParticipants() {
            sortParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE, 0L);
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
                    arrayList.add(Long.valueOf((long) iArr[i]));
                }
            }
            if (arrayList != null) {
                loadUnknownParticipants(arrayList, false, onParticipantsLoad);
            } else {
                onParticipantsLoad.onLoad((ArrayList<Long>) null);
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
            Collections.sort(this.updatesQueue, ChatObject$Call$$ExternalSyntheticLambda8.INSTANCE);
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
                ChatObject$Call$$ExternalSyntheticLambda0 chatObject$Call$$ExternalSyntheticLambda0 = new ChatObject$Call$$ExternalSyntheticLambda0(this);
                this.checkQueueRunnable = chatObject$Call$$ExternalSyntheticLambda0;
                AndroidUtilities.runOnUIThread(chatObject$Call$$ExternalSyntheticLambda0, 1000);
            }
        }

        public void reloadGroupCall() {
            TLRPC$TL_phone_getGroupCall tLRPC$TL_phone_getGroupCall = new TLRPC$TL_phone_getGroupCall();
            tLRPC$TL_phone_getGroupCall.call = getInputGroupCall();
            tLRPC$TL_phone_getGroupCall.limit = 100;
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCall, new ChatObject$Call$$ExternalSyntheticLambda9(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reloadGroupCall$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new ChatObject$Call$$ExternalSyntheticLambda5(this, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reloadGroupCall$9(TLObject tLObject) {
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
                this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new ChatObject$Call$$ExternalSyntheticLambda13(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadGroupCall$12(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new ChatObject$Call$$ExternalSyntheticLambda4(this, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadGroupCall$11(TLObject tLObject) {
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
                    this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:157:0x03e2  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void processParticipantsUpdate(org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants r28, boolean r29) {
            /*
                r27 = this;
                r0 = r27
                r1 = r28
                r2 = 0
                r3 = 0
                r5 = 0
                r6 = 1
                if (r29 != 0) goto L_0x00a1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r7 = r1.participants
                int r7 = r7.size()
                r8 = 0
            L_0x0012:
                if (r8 >= r7) goto L_0x0025
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r9 = r1.participants
                java.lang.Object r9 = r9.get(r8)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r9
                boolean r9 = r9.versioned
                if (r9 == 0) goto L_0x0022
                r7 = 1
                goto L_0x0026
            L_0x0022:
                int r8 = r8 + 1
                goto L_0x0012
            L_0x0025:
                r7 = 0
            L_0x0026:
                if (r7 == 0) goto L_0x008d
                org.telegram.tgnet.TLRPC$GroupCall r8 = r0.call
                int r8 = r8.version
                int r8 = r8 + r6
                int r9 = r1.version
                if (r8 >= r9) goto L_0x008d
                boolean r5 = r0.reloadingMembers
                r7 = 1500(0x5dc, double:7.41E-321)
                if (r5 != 0) goto L_0x0053
                long r9 = r0.updatesStartWaitTime
                int r5 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
                if (r5 == 0) goto L_0x0053
                long r9 = java.lang.System.currentTimeMillis()
                long r11 = r0.updatesStartWaitTime
                long r9 = r9 - r11
                long r9 = java.lang.Math.abs(r9)
                int r5 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
                if (r5 > 0) goto L_0x004d
                goto L_0x0053
            L_0x004d:
                r0.nextLoadOffset = r2
                r0.loadMembers(r6)
                goto L_0x008c
            L_0x0053:
                long r5 = r0.updatesStartWaitTime
                int r2 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
                if (r2 != 0) goto L_0x005f
                long r2 = java.lang.System.currentTimeMillis()
                r0.updatesStartWaitTime = r2
            L_0x005f:
                boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x0079
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "add TL_updateGroupCallParticipants to queue "
                r2.append(r3)
                int r3 = r1.version
                r2.append(r3)
                java.lang.String r2 = r2.toString()
                org.telegram.messenger.FileLog.d(r2)
            L_0x0079:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants> r2 = r0.updatesQueue
                r2.add(r1)
                java.lang.Runnable r1 = r0.checkQueueRunnable
                if (r1 != 0) goto L_0x008c
                org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda0 r1 = new org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda0
                r1.<init>(r0)
                r0.checkQueueRunnable = r1
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r7)
            L_0x008c:
                return
            L_0x008d:
                if (r7 == 0) goto L_0x00a1
                int r7 = r1.version
                org.telegram.tgnet.TLRPC$GroupCall r8 = r0.call
                int r8 = r8.version
                if (r7 >= r8) goto L_0x00a1
                boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r1 == 0) goto L_0x00a0
                java.lang.String r1 = "ignore processParticipantsUpdate because of version"
                org.telegram.messenger.FileLog.d(r1)
            L_0x00a0:
                return
            L_0x00a1:
                long r7 = r27.getSelfId()
                long r9 = android.os.SystemClock.elapsedRealtime()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r11 = r0.sortedParticipants
                boolean r11 = r11.isEmpty()
                if (r11 != 0) goto L_0x00c1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r11 = r0.sortedParticipants
                int r12 = r11.size()
                int r12 = r12 - r6
                java.lang.Object r11 = r11.get(r12)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r11
                int r11 = r11.date
                goto L_0x00c2
            L_0x00c1:
                r11 = 0
            L_0x00c2:
                org.telegram.messenger.AccountInstance r12 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r12 = r12.getNotificationCenter()
                int r13 = org.telegram.messenger.NotificationCenter.applyGroupCallVisibleParticipants
                java.lang.Object[] r14 = new java.lang.Object[r6]
                java.lang.Long r15 = java.lang.Long.valueOf(r9)
                r14[r5] = r15
                r12.postNotificationName(r13, r14)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r12 = r1.participants
                int r12 = r12.size()
                r19 = r3
                r13 = 0
                r14 = 0
                r15 = 0
                r16 = 0
                r17 = 0
                r18 = 0
            L_0x00e6:
                if (r13 >= r12) goto L_0x044e
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r15 = r1.participants
                java.lang.Object r15 = r15.get(r13)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r15 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r15
                org.telegram.tgnet.TLRPC$Peer r6 = r15.peer
                long r3 = org.telegram.messenger.MessageObject.getPeerId(r6)
                boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r6 == 0) goto L_0x013e
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r2 = "process participant "
                r6.append(r2)
                r6.append(r3)
                java.lang.String r2 = " left = "
                r6.append(r2)
                boolean r2 = r15.left
                r6.append(r2)
                java.lang.String r2 = " versioned "
                r6.append(r2)
                boolean r2 = r15.versioned
                r6.append(r2)
                java.lang.String r2 = " flags = "
                r6.append(r2)
                int r2 = r15.flags
                r6.append(r2)
                java.lang.String r2 = " self = "
                r6.append(r2)
                r6.append(r7)
                java.lang.String r2 = " volume = "
                r6.append(r2)
                int r2 = r15.volume
                r6.append(r2)
                java.lang.String r2 = r6.toString()
                org.telegram.messenger.FileLog.d(r2)
            L_0x013e:
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.participants
                java.lang.Object r2 = r2.get(r3)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r2
                boolean r6 = r15.left
                java.lang.String r5 = " "
                r23 = r12
                java.lang.String r12 = "GroupCall"
                if (r6 == 0) goto L_0x0245
                if (r2 != 0) goto L_0x0164
                int r6 = r1.version
                org.telegram.tgnet.TLRPC$GroupCall r15 = r0.call
                int r15 = r15.version
                if (r6 != r15) goto L_0x0164
                boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r6 == 0) goto L_0x0163
                java.lang.String r6 = "unknowd participant left, reload call"
                org.telegram.messenger.FileLog.d(r6)
            L_0x0163:
                r14 = 1
            L_0x0164:
                if (r2 == 0) goto L_0x022d
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r6 = r0.participants
                r6.remove(r3)
                r6 = 0
                r0.processAllSources(r2, r6)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r6 = r0.sortedParticipants
                r6.remove(r2)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r6 = r0.visibleParticipants
                r6.remove(r2)
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r6 = r0.currentSpeakingPeers
                r15 = 0
                java.lang.Object r6 = r6.get(r3, r15)
                if (r6 == 0) goto L_0x01fc
                java.lang.String r6 = "left remove from speaking "
                r21 = 0
                int r15 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
                if (r15 <= 0) goto L_0x01bf
                org.telegram.messenger.AccountInstance r15 = r0.currentAccount
                int r15 = r15.getCurrentAccount()
                org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
                r24 = r14
                java.lang.Long r14 = java.lang.Long.valueOf(r3)
                org.telegram.tgnet.TLRPC$User r14 = r15.getUser(r14)
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                r15.append(r6)
                r15.append(r3)
                r15.append(r5)
                if (r14 != 0) goto L_0x01b0
                r5 = 0
                goto L_0x01b2
            L_0x01b0:
                java.lang.String r5 = r14.first_name
            L_0x01b2:
                r15.append(r5)
                java.lang.String r5 = r15.toString()
                com.google.android.exoplayer2.util.Log.d(r12, r5)
                r25 = r7
                goto L_0x01f4
            L_0x01bf:
                r24 = r14
                org.telegram.messenger.AccountInstance r14 = r0.currentAccount
                int r14 = r14.getCurrentAccount()
                org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r14)
                r25 = r7
                long r7 = -r3
                java.lang.Long r7 = java.lang.Long.valueOf(r7)
                org.telegram.tgnet.TLRPC$Chat r7 = r14.getChat(r7)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r8.append(r6)
                r8.append(r3)
                r8.append(r5)
                if (r7 != 0) goto L_0x01e8
                r15 = 0
                goto L_0x01ea
            L_0x01e8:
                java.lang.String r15 = r7.title
            L_0x01ea:
                r8.append(r15)
                java.lang.String r5 = r8.toString()
                com.google.android.exoplayer2.util.Log.d(r12, r5)
            L_0x01f4:
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r5 = r0.currentSpeakingPeers
                r5.remove(r3)
                r17 = 1
                goto L_0x0200
            L_0x01fc:
                r25 = r7
                r24 = r14
            L_0x0200:
                r5 = 0
            L_0x0201:
                java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r6 = r0.visibleVideoParticipants
                int r6 = r6.size()
                if (r5 >= r6) goto L_0x0231
                java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r6 = r0.visibleVideoParticipants
                java.lang.Object r6 = r6.get(r5)
                org.telegram.messenger.ChatObject$VideoParticipant r6 = (org.telegram.messenger.ChatObject.VideoParticipant) r6
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = r6.participant
                org.telegram.tgnet.TLRPC$Peer r6 = r6.peer
                long r6 = org.telegram.messenger.MessageObject.getPeerId(r6)
                org.telegram.tgnet.TLRPC$Peer r8 = r2.peer
                long r14 = org.telegram.messenger.MessageObject.getPeerId(r8)
                int r8 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
                if (r8 != 0) goto L_0x022a
                java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r6 = r0.visibleVideoParticipants
                r6.remove(r5)
                int r5 = r5 + -1
            L_0x022a:
                r6 = 1
                int r5 = r5 + r6
                goto L_0x0201
            L_0x022d:
                r25 = r7
                r24 = r14
            L_0x0231:
                r6 = 1
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                int r5 = r2.participants_count
                int r5 = r5 - r6
                r2.participants_count = r5
                if (r5 >= 0) goto L_0x023e
                r5 = 0
                r2.participants_count = r5
            L_0x023e:
                r8 = r13
                r14 = r24
                r12 = 0
                goto L_0x043a
            L_0x0245:
                r25 = r7
                java.util.HashSet<java.lang.Long> r6 = r0.invitedUsersMap
                java.lang.Long r7 = java.lang.Long.valueOf(r3)
                boolean r6 = r6.contains(r7)
                if (r6 == 0) goto L_0x0261
                java.lang.Long r6 = java.lang.Long.valueOf(r3)
                java.util.HashSet<java.lang.Long> r7 = r0.invitedUsersMap
                r7.remove(r6)
                java.util.ArrayList<java.lang.Long> r7 = r0.invitedUsers
                r7.remove(r6)
            L_0x0261:
                if (r2 == 0) goto L_0x03a9
                boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r6 == 0) goto L_0x026c
                java.lang.String r6 = "new participant, update old"
                org.telegram.messenger.FileLog.d(r6)
            L_0x026c:
                boolean r6 = r15.muted
                r2.muted = r6
                boolean r6 = r15.muted
                if (r6 == 0) goto L_0x02f5
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r6 = r0.currentSpeakingPeers
                r7 = 0
                java.lang.Object r6 = r6.get(r3, r7)
                if (r6 == 0) goto L_0x02f5
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r6 = r0.currentSpeakingPeers
                r6.remove(r3)
                java.lang.String r6 = "muted remove from speaking "
                r16 = 0
                int r8 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
                if (r8 <= 0) goto L_0x02be
                org.telegram.messenger.AccountInstance r8 = r0.currentAccount
                int r8 = r8.getCurrentAccount()
                org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
                java.lang.Long r7 = java.lang.Long.valueOf(r3)
                org.telegram.tgnet.TLRPC$User r7 = r8.getUser(r7)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r8.append(r6)
                r8.append(r3)
                r8.append(r5)
                if (r7 != 0) goto L_0x02ae
                r5 = 0
                goto L_0x02b0
            L_0x02ae:
                java.lang.String r5 = r7.first_name
            L_0x02b0:
                r8.append(r5)
                java.lang.String r5 = r8.toString()
                com.google.android.exoplayer2.util.Log.d(r12, r5)
                r8 = r13
                r24 = r14
                goto L_0x02f2
            L_0x02be:
                org.telegram.messenger.AccountInstance r7 = r0.currentAccount
                int r7 = r7.getCurrentAccount()
                org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
                r8 = r13
                r24 = r14
                long r13 = -r3
                java.lang.Long r13 = java.lang.Long.valueOf(r13)
                org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r13)
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                r13.append(r6)
                r13.append(r3)
                r13.append(r5)
                if (r7 != 0) goto L_0x02e6
                r5 = 0
                goto L_0x02e8
            L_0x02e6:
                java.lang.String r5 = r7.title
            L_0x02e8:
                r13.append(r5)
                java.lang.String r5 = r13.toString()
                com.google.android.exoplayer2.util.Log.d(r12, r5)
            L_0x02f2:
                r17 = 1
                goto L_0x02f8
            L_0x02f5:
                r8 = r13
                r24 = r14
            L_0x02f8:
                boolean r5 = r15.min
                if (r5 != 0) goto L_0x0305
                int r5 = r15.volume
                r2.volume = r5
                boolean r5 = r15.muted_by_you
                r2.muted_by_you = r5
                goto L_0x0321
            L_0x0305:
                int r5 = r15.flags
                r6 = r5 & 128(0x80, float:1.794E-43)
                if (r6 == 0) goto L_0x0315
                int r6 = r2.flags
                r6 = r6 & 128(0x80, float:1.794E-43)
                if (r6 != 0) goto L_0x0315
                r5 = r5 & -129(0xffffffffffffff7f, float:NaN)
                r15.flags = r5
            L_0x0315:
                boolean r5 = r15.volume_by_admin
                if (r5 == 0) goto L_0x0321
                boolean r5 = r2.volume_by_admin
                if (r5 == 0) goto L_0x0321
                int r5 = r15.volume
                r2.volume = r5
            L_0x0321:
                int r5 = r15.flags
                r2.flags = r5
                boolean r5 = r15.can_self_unmute
                r2.can_self_unmute = r5
                boolean r5 = r15.video_joined
                r2.video_joined = r5
                long r5 = r2.raise_hand_rating
                r12 = 0
                int r7 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r7 != 0) goto L_0x0341
                long r5 = r15.raise_hand_rating
                int r7 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r7 == 0) goto L_0x0341
                long r5 = android.os.SystemClock.elapsedRealtime()
                r2.lastRaiseHandDate = r5
            L_0x0341:
                long r5 = r15.raise_hand_rating
                r2.raise_hand_rating = r5
                int r5 = r15.date
                r2.date = r5
                int r5 = r2.active_date
                int r6 = r15.active_date
                int r5 = java.lang.Math.max(r5, r6)
                r2.lastTypingDate = r5
                long r6 = r2.lastVisibleDate
                int r12 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
                if (r12 == 0) goto L_0x035b
                r2.active_date = r5
            L_0x035b:
                int r5 = r2.source
                int r6 = r15.source
                if (r5 != r6) goto L_0x0383
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r5 = r2.video
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r6 = r15.video
                boolean r5 = r0.isSameVideo(r5, r6)
                if (r5 == 0) goto L_0x0383
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r5 = r2.presentation
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r6 = r15.presentation
                boolean r5 = r0.isSameVideo(r5, r6)
                if (r5 != 0) goto L_0x0376
                goto L_0x0383
            L_0x0376:
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r2 = r2.video
                if (r2 == 0) goto L_0x03a3
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r5 = r15.video
                if (r5 == 0) goto L_0x03a3
                boolean r5 = r5.paused
                r2.paused = r5
                goto L_0x03a3
            L_0x0383:
                r5 = 0
                r0.processAllSources(r2, r5)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r5 = r15.video
                r2.video = r5
                org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r5 = r15.presentation
                r2.presentation = r5
                int r5 = r15.source
                r2.source = r5
                r5 = 1
                r0.processAllSources(r2, r5)
                java.lang.String r5 = r2.presentationEndpoint
                r15.presentationEndpoint = r5
                java.lang.String r5 = r2.videoEndpoint
                r15.videoEndpoint = r5
                int r2 = r2.videoIndex
                r15.videoIndex = r2
            L_0x03a3:
                r14 = r24
                r12 = 0
                goto L_0x041c
            L_0x03a9:
                r8 = r13
                r24 = r14
                boolean r2 = r15.just_joined
                if (r2 == 0) goto L_0x03d8
                int r2 = (r3 > r25 ? 1 : (r3 == r25 ? 0 : -1))
                if (r2 == 0) goto L_0x03b6
                r19 = r3
            L_0x03b6:
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                int r5 = r2.participants_count
                r6 = 1
                int r5 = r5 + r6
                r2.participants_count = r5
                int r5 = r1.version
                int r2 = r2.version
                if (r5 != r2) goto L_0x03cf
                boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x03cd
                java.lang.String r2 = "new participant, just joined, reload call"
                org.telegram.messenger.FileLog.d(r2)
            L_0x03cd:
                r14 = 1
                goto L_0x03da
            L_0x03cf:
                boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x03d8
                java.lang.String r2 = "new participant, just joined"
                org.telegram.messenger.FileLog.d(r2)
            L_0x03d8:
                r14 = r24
            L_0x03da:
                long r5 = r15.raise_hand_rating
                r12 = 0
                int r2 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r2 == 0) goto L_0x03e8
                long r5 = android.os.SystemClock.elapsedRealtime()
                r15.lastRaiseHandDate = r5
            L_0x03e8:
                int r2 = (r3 > r25 ? 1 : (r3 == r25 ? 0 : -1))
                if (r2 == 0) goto L_0x040e
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.sortedParticipants
                int r2 = r2.size()
                r5 = 20
                if (r2 < r5) goto L_0x040e
                int r2 = r15.date
                if (r2 <= r11) goto L_0x040e
                int r2 = r15.active_date
                if (r2 != 0) goto L_0x040e
                boolean r2 = r15.can_self_unmute
                if (r2 != 0) goto L_0x040e
                boolean r2 = r15.muted
                if (r2 == 0) goto L_0x040e
                boolean r2 = r15.min
                if (r2 == 0) goto L_0x040e
                boolean r2 = r0.membersLoadEndReached
                if (r2 == 0) goto L_0x0413
            L_0x040e:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.sortedParticipants
                r2.add(r15)
            L_0x0413:
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.participants
                r2.put(r3, r15)
                r2 = 1
                r0.processAllSources(r15, r2)
            L_0x041c:
                int r2 = (r3 > r25 ? 1 : (r3 == r25 ? 0 : -1))
                if (r2 != 0) goto L_0x0438
                int r2 = r15.active_date
                if (r2 != 0) goto L_0x0438
                boolean r2 = r15.can_self_unmute
                if (r2 != 0) goto L_0x042c
                boolean r2 = r15.muted
                if (r2 != 0) goto L_0x0438
            L_0x042c:
                org.telegram.messenger.AccountInstance r2 = r0.currentAccount
                org.telegram.tgnet.ConnectionsManager r2 = r2.getConnectionsManager()
                int r2 = r2.getCurrentTime()
                r15.active_date = r2
            L_0x0438:
                r16 = 1
            L_0x043a:
                int r2 = (r3 > r25 ? 1 : (r3 == r25 ? 0 : -1))
                if (r2 != 0) goto L_0x0440
                r18 = 1
            L_0x0440:
                int r2 = r8 + 1
                r3 = r12
                r12 = r23
                r7 = r25
                r5 = 0
                r6 = 1
                r15 = 1
                r13 = r2
                r2 = 0
                goto L_0x00e6
            L_0x044e:
                r24 = r14
                int r1 = r1.version
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                int r3 = r2.version
                if (r1 <= r3) goto L_0x045f
                r2.version = r1
                if (r29 != 0) goto L_0x045f
                r27.processUpdatesQueue()
            L_0x045f:
                org.telegram.tgnet.TLRPC$GroupCall r1 = r0.call
                int r1 = r1.participants_count
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.participants
                int r2 = r2.size()
                if (r1 >= r2) goto L_0x0475
                org.telegram.tgnet.TLRPC$GroupCall r1 = r0.call
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r0.participants
                int r2 = r2.size()
                r1.participants_count = r2
            L_0x0475:
                boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r1 == 0) goto L_0x0491
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "new participants count after update "
                r1.append(r2)
                org.telegram.tgnet.TLRPC$GroupCall r2 = r0.call
                int r2 = r2.participants_count
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                org.telegram.messenger.FileLog.d(r1)
            L_0x0491:
                if (r24 == 0) goto L_0x0496
                r27.loadGroupCall()
            L_0x0496:
                r1 = 2
                r2 = 3
                if (r15 == 0) goto L_0x04cd
                if (r16 == 0) goto L_0x049f
                r27.sortParticipants()
            L_0x049f:
                org.telegram.messenger.AccountInstance r3 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r3 = r3.getNotificationCenter()
                int r4 = org.telegram.messenger.NotificationCenter.groupCallUpdated
                r5 = 4
                java.lang.Object[] r5 = new java.lang.Object[r5]
                long r6 = r0.chatId
                java.lang.Long r6 = java.lang.Long.valueOf(r6)
                r7 = 0
                r5[r7] = r6
                org.telegram.tgnet.TLRPC$GroupCall r6 = r0.call
                long r6 = r6.id
                java.lang.Long r6 = java.lang.Long.valueOf(r6)
                r7 = 1
                r5[r7] = r6
                java.lang.Boolean r6 = java.lang.Boolean.valueOf(r18)
                r5[r1] = r6
                java.lang.Long r6 = java.lang.Long.valueOf(r19)
                r5[r2] = r6
                r3.postNotificationName(r4, r5)
            L_0x04cd:
                if (r17 == 0) goto L_0x04f4
                org.telegram.messenger.AccountInstance r3 = r0.currentAccount
                org.telegram.messenger.NotificationCenter r3 = r3.getNotificationCenter()
                int r4 = org.telegram.messenger.NotificationCenter.groupCallSpeakingUsersUpdated
                java.lang.Object[] r2 = new java.lang.Object[r2]
                long r5 = r0.chatId
                java.lang.Long r5 = java.lang.Long.valueOf(r5)
                r6 = 0
                r2[r6] = r5
                org.telegram.tgnet.TLRPC$GroupCall r5 = r0.call
                long r5 = r5.id
                java.lang.Long r5 = java.lang.Long.valueOf(r5)
                r6 = 1
                r2[r6] = r5
                java.lang.Boolean r5 = java.lang.Boolean.FALSE
                r2[r1] = r5
                r3.postNotificationName(r4, r2)
            L_0x04f4:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.processParticipantsUpdate(org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants, boolean):void");
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
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
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
            VideoParticipant videoParticipant;
            if (tLRPC$TL_groupCallParticipant == null || (sharedInstance = VoIPService.getSharedInstance()) == null) {
                return false;
            }
            if (!tLRPC$TL_groupCallParticipant.self) {
                VideoParticipant videoParticipant2 = call2.rtmpStreamParticipant;
                if ((videoParticipant2 == null || videoParticipant2.participant != tLRPC$TL_groupCallParticipant) && (((videoParticipant = call2.videoNotAvailableParticipant) == null || videoParticipant.participant != tLRPC$TL_groupCallParticipant) && call2.participants.get(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) == null)) {
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
            TLRPC$Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(this.chatId));
            boolean canManageCalls = ChatObject.canManageCalls(chat);
            VideoParticipant videoParticipant2 = this.rtmpStreamParticipant;
            if (videoParticipant2 != null) {
                this.visibleVideoParticipants.add(videoParticipant2);
            }
            long selfId = getSelfId();
            VoIPService.getSharedInstance();
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.participants.get(selfId);
            this.canStreamVideo = true;
            this.activeVideos = 0;
            int size2 = this.sortedParticipants.size();
            boolean z = false;
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant3 = this.sortedParticipants.get(i2);
                boolean videoIsActive = videoIsActive(tLRPC$TL_groupCallParticipant3, false, this);
                boolean videoIsActive2 = videoIsActive(tLRPC$TL_groupCallParticipant3, true, this);
                boolean z2 = tLRPC$TL_groupCallParticipant3.self;
                if (!z2 && (videoIsActive || videoIsActive2)) {
                    this.activeVideos++;
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
            Collections.sort(this.sortedParticipants, new ChatObject$Call$$ExternalSyntheticLambda7(this, selfId, canManageCalls));
            if (this.sortedParticipants.isEmpty()) {
                tLRPC$TL_groupCallParticipant = null;
            } else {
                ArrayList<TLRPC$TL_groupCallParticipant> arrayList = this.sortedParticipants;
                tLRPC$TL_groupCallParticipant = arrayList.get(arrayList.size() - 1);
            }
            if ((videoIsActive(tLRPC$TL_groupCallParticipant, false, this) || videoIsActive(tLRPC$TL_groupCallParticipant, true, this)) && (i = this.call.unmuted_video_count) > this.activeVideos) {
                this.activeVideos = i;
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                if (sharedInstance != null && sharedInstance.groupCall == this && (sharedInstance.getVideoState(false) == 2 || sharedInstance.getVideoState(true) == 2)) {
                    this.activeVideos--;
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
                        VideoParticipant videoParticipant3 = this.videoParticipantsCache.get(tLRPC$TL_groupCallParticipant5.videoEndpoint);
                        if (videoParticipant3 == null) {
                            videoParticipant3 = new VideoParticipant(tLRPC$TL_groupCallParticipant5, false, true);
                            this.videoParticipantsCache.put(tLRPC$TL_groupCallParticipant5.videoEndpoint, videoParticipant3);
                        } else {
                            videoParticipant3.participant = tLRPC$TL_groupCallParticipant5;
                            videoParticipant3.presentation = false;
                            videoParticipant3.hasSame = true;
                        }
                        VideoParticipant videoParticipant4 = this.videoParticipantsCache.get(tLRPC$TL_groupCallParticipant5.presentationEndpoint);
                        if (videoParticipant4 == null) {
                            videoParticipant4 = new VideoParticipant(tLRPC$TL_groupCallParticipant5, true, true);
                        } else {
                            videoParticipant4.participant = tLRPC$TL_groupCallParticipant5;
                            videoParticipant4.presentation = true;
                            videoParticipant4.hasSame = true;
                        }
                        this.visibleVideoParticipants.add(videoParticipant3);
                        if (videoParticipant3.aspectRatio > 1.0f) {
                            i5 = this.visibleVideoParticipants.size() - 1;
                        }
                        this.visibleVideoParticipants.add(videoParticipant4);
                        if (videoParticipant4.aspectRatio > 1.0f) {
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
                        VideoParticipant videoParticipant5 = this.videoParticipantsCache.get(videoIsActive3 ? tLRPC$TL_groupCallParticipant5.presentationEndpoint : tLRPC$TL_groupCallParticipant5.videoEndpoint);
                        if (videoParticipant5 == null) {
                            videoParticipant5 = new VideoParticipant(tLRPC$TL_groupCallParticipant5, videoIsActive3, false);
                            this.videoParticipantsCache.put(videoIsActive3 ? tLRPC$TL_groupCallParticipant5.presentationEndpoint : tLRPC$TL_groupCallParticipant5.videoEndpoint, videoParticipant5);
                        } else {
                            videoParticipant5.participant = tLRPC$TL_groupCallParticipant5;
                            videoParticipant5.presentation = videoIsActive3;
                            videoParticipant5.hasSame = false;
                        }
                        this.visibleVideoParticipants.add(videoParticipant5);
                        if (videoParticipant5.aspectRatio > 1.0f) {
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
        public /* synthetic */ int lambda$sortParticipants$13(long j, boolean z, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2) {
            int i;
            int i2 = tLRPC$TL_groupCallParticipant.videoIndex;
            boolean z2 = false;
            boolean z3 = i2 > 0;
            int i3 = tLRPC$TL_groupCallParticipant2.videoIndex;
            if (i3 > 0) {
                z2 = true;
            }
            if (z3 && z2) {
                return i3 - i2;
            }
            if (z3) {
                return -1;
            }
            if (z2) {
                return 1;
            }
            int i4 = tLRPC$TL_groupCallParticipant.active_date;
            if (i4 != 0 && (i = tLRPC$TL_groupCallParticipant2.active_date) != 0) {
                return zzdp$$ExternalSyntheticBackport0.m(i, i4);
            }
            if (i4 != 0) {
                return -1;
            }
            if (tLRPC$TL_groupCallParticipant2.active_date != 0) {
                return 1;
            }
            if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == j) {
                return -1;
            }
            if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant2.peer) == j) {
                return 1;
            }
            if (z) {
                long j2 = tLRPC$TL_groupCallParticipant.raise_hand_rating;
                if (j2 != 0) {
                    long j3 = tLRPC$TL_groupCallParticipant2.raise_hand_rating;
                    if (j3 != 0) {
                        return (j3 > j2 ? 1 : (j3 == j2 ? 0 : -1));
                    }
                }
                if (j2 != 0) {
                    return -1;
                }
                if (tLRPC$TL_groupCallParticipant2.raise_hand_rating != 0) {
                    return 1;
                }
            }
            if (this.call.join_date_asc) {
                return zzdp$$ExternalSyntheticBackport0.m(tLRPC$TL_groupCallParticipant.date, tLRPC$TL_groupCallParticipant2.date);
            }
            return zzdp$$ExternalSyntheticBackport0.m(tLRPC$TL_groupCallParticipant2.date, tLRPC$TL_groupCallParticipant.date);
        }

        public boolean canRecordVideo() {
            if (!this.canStreamVideo) {
                return false;
            }
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if ((sharedInstance == null || sharedInstance.groupCall != this || (sharedInstance.getVideoState(false) != 2 && sharedInstance.getVideoState(true) != 2)) && this.activeVideos >= this.call.unmuted_video_limit) {
                return false;
            }
            return true;
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

        public void toggleRecord(String str, int i) {
            this.recording = !this.recording;
            TLRPC$TL_phone_toggleGroupCallRecord tLRPC$TL_phone_toggleGroupCallRecord = new TLRPC$TL_phone_toggleGroupCallRecord();
            tLRPC$TL_phone_toggleGroupCallRecord.call = getInputGroupCall();
            tLRPC$TL_phone_toggleGroupCallRecord.start = this.recording;
            if (str != null) {
                tLRPC$TL_phone_toggleGroupCallRecord.title = str;
                tLRPC$TL_phone_toggleGroupCallRecord.flags |= 2;
            }
            if (i == 1 || i == 2) {
                tLRPC$TL_phone_toggleGroupCallRecord.flags |= 4;
                tLRPC$TL_phone_toggleGroupCallRecord.video = true;
                tLRPC$TL_phone_toggleGroupCallRecord.video_portrait = i == 1;
            }
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_toggleGroupCallRecord, new ChatObject$Call$$ExternalSyntheticLambda11(this));
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$toggleRecord$14(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

    public static boolean canSendAsPeers(TLRPC$Chat tLRPC$Chat) {
        return isChannel(tLRPC$Chat) && tLRPC$Chat.megagroup && (!TextUtils.isEmpty(tLRPC$Chat.username) || tLRPC$Chat.has_geo || tLRPC$Chat.has_link);
    }

    public static boolean isChannel(TLRPC$Chat tLRPC$Chat) {
        return (tLRPC$Chat instanceof TLRPC$TL_channel) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden);
    }

    public static boolean isChannelOrGiga(TLRPC$Chat tLRPC$Chat) {
        return ((tLRPC$Chat instanceof TLRPC$TL_channel) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden)) && (!tLRPC$Chat.megagroup || tLRPC$Chat.gigagroup);
    }

    public static boolean isMegagroup(TLRPC$Chat tLRPC$Chat) {
        return ((tLRPC$Chat instanceof TLRPC$TL_channel) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden)) && tLRPC$Chat.megagroup;
    }

    public static boolean isChannelAndNotMegaGroup(TLRPC$Chat tLRPC$Chat) {
        return isChannel(tLRPC$Chat) && !isMegagroup(tLRPC$Chat);
    }

    public static boolean isMegagroup(int i, long j) {
        TLRPC$Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(j));
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

    public static long getSendAsPeerId(TLRPC$Chat tLRPC$Chat, TLRPC$ChatFull tLRPC$ChatFull) {
        return getSendAsPeerId(tLRPC$Chat, tLRPC$ChatFull, false);
    }

    public static long getSendAsPeerId(TLRPC$Chat tLRPC$Chat, TLRPC$ChatFull tLRPC$ChatFull, boolean z) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        TLRPC$Peer tLRPC$Peer;
        if (tLRPC$Chat != null && tLRPC$ChatFull != null && (tLRPC$Peer = tLRPC$ChatFull.default_send_as) != null) {
            long j = tLRPC$Peer.user_id;
            if (j != 0) {
                return j;
            }
            return z ? -tLRPC$Peer.channel_id : tLRPC$Peer.channel_id;
        } else if (tLRPC$Chat == null || (tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) == null || !tLRPC$TL_chatAdminRights.anonymous) {
            return UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        } else {
            long j2 = tLRPC$Chat.id;
            return z ? -j2 : j2;
        }
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

    public static boolean isChannel(long j, int i) {
        TLRPC$Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(j));
        return (chat instanceof TLRPC$TL_channel) || (chat instanceof TLRPC$TL_channelForbidden);
    }

    public static boolean isChannelAndNotMegaGroup(long j, int i) {
        return isChannelAndNotMegaGroup(MessagesController.getInstance(i).getChat(Long.valueOf(j)));
    }

    public static boolean isCanWriteToChannel(long j, int i) {
        TLRPC$Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(j));
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
        public int aspectRatioFromHeight;
        public int aspectRatioFromWidth;
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

        public void setAspectRatio(int i, int i2, Call call) {
            this.aspectRatioFromWidth = i;
            this.aspectRatioFromHeight = i2;
            setAspectRatio(((float) i) / ((float) i2), call);
        }

        private void setAspectRatio(float f, Call call) {
            if (this.aspectRatio != f) {
                this.aspectRatio = f;
                if (!GroupCallActivity.isLandscapeMode && call.visibleVideoParticipants.size() % 2 == 1) {
                    call.updateVisibleParticipants();
                }
            }
        }
    }
}
