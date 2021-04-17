package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.messenger.voip.Instance;
import org.telegram.messenger.voip.NativeInstance;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.RequestDelegateTimestamp;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$GroupCall;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$PhoneCall;
import org.telegram.tgnet.TLRPC$TL_boolFalse;
import org.telegram.tgnet.TLRPC$TL_chatFull;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCall;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_inputGroupCallStream;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC$TL_messages_dhConfig;
import org.telegram.tgnet.TLRPC$TL_messages_getDhConfig;
import org.telegram.tgnet.TLRPC$TL_messages_setTyping;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonDisconnect;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonHangup;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscarded;
import org.telegram.tgnet.TLRPC$TL_phoneCallProtocol;
import org.telegram.tgnet.TLRPC$TL_phone_acceptCall;
import org.telegram.tgnet.TLRPC$TL_phone_checkGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_confirmCall;
import org.telegram.tgnet.TLRPC$TL_phone_createGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_discardCall;
import org.telegram.tgnet.TLRPC$TL_phone_discardGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_getCallConfig;
import org.telegram.tgnet.TLRPC$TL_phone_joinGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_leaveGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_phoneCall;
import org.telegram.tgnet.TLRPC$TL_phone_receivedCall;
import org.telegram.tgnet.TLRPC$TL_phone_requestCall;
import org.telegram.tgnet.TLRPC$TL_phone_saveCallDebug;
import org.telegram.tgnet.TLRPC$TL_phone_sendSignalingData;
import org.telegram.tgnet.TLRPC$TL_speakingInGroupCallAction;
import org.telegram.tgnet.TLRPC$TL_updateGroupCall;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants;
import org.telegram.tgnet.TLRPC$TL_updatePhoneCallSignalingData;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$TL_upload_file;
import org.telegram.tgnet.TLRPC$TL_upload_getFile;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_DhConfig;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPFeedbackActivity;
import org.webrtc.VideoSink;

@SuppressLint({"NewApi"})
public class VoIPService extends VoIPBaseService {
    public static final int CALL_MIN_LAYER = 65;
    public static final int STATE_BUSY = 17;
    public static final int STATE_EXCHANGING_KEYS = 12;
    public static final int STATE_HANGING_UP = 10;
    public static final int STATE_REQUESTING = 14;
    public static final int STATE_RINGING = 16;
    public static final int STATE_WAITING = 13;
    public static final int STATE_WAITING_INCOMING = 15;
    public static NativeInstance.AudioLevelsCallback audioLevelsCallback;
    public static TLRPC$PhoneCall callIShouldHavePutIntoIntent;
    private byte[] a_or_b;
    private byte[] authKey;
    private int callReqId;
    private int checkRequestId;
    private int classGuid;
    private long currentStreamRequestTimestamp;
    private Runnable delayedStartOutgoingCall;
    private boolean endCallAfterRequest;
    private boolean forceRating;
    private byte[] g_a;
    private byte[] g_a_hash;
    private long keyFingerprint;
    private long lastTypingTimeSend;
    private ProxyVideoSink localSink;
    private boolean needRateCall;
    private boolean needSendDebugLog;
    private ArrayList<TLRPC$PhoneCall> pendingUpdates = new ArrayList<>();
    private ProxyVideoSink remoteSink;
    private Runnable shortPollRunnable;
    private boolean startedRinging;
    private TLRPC$User user;

    static /* synthetic */ void lambda$createGroupInstance$39(int[] iArr) {
    }

    static /* synthetic */ void lambda$null$37(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$onSignalingData$53(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    /* access modifiers changed from: protected */
    public void onTgVoipPreStop() {
    }

    public boolean isFrontFaceCamera() {
        return this.isFrontFaceCamera;
    }

    public void setMicMute(boolean z, boolean z2, boolean z3) {
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        if (this.micMute != z) {
            this.micMute = z;
            ChatObject.Call call = this.groupCall;
            boolean z4 = true;
            if (call != null) {
                if (!z3 && (tLRPC$TL_groupCallParticipant = call.participants.get(getSelfId())) != null && tLRPC$TL_groupCallParticipant.muted && !tLRPC$TL_groupCallParticipant.can_self_unmute) {
                    z3 = true;
                }
                if (z3) {
                    editCallMember(UserConfig.getInstance(this.currentAccount).getCurrentUser(), z, -1, (Boolean) null);
                    DispatchQueue dispatchQueue = Utilities.globalQueue;
                    $$Lambda$VoIPService$B0eAKygTSH_UOopoQhftbjx1Ag r0 = new Runnable() {
                        public final void run() {
                            VoIPService.this.lambda$setMicMute$0$VoIPService();
                        }
                    };
                    this.updateNotificationRunnable = r0;
                    dispatchQueue.postRunnable(r0);
                }
            }
            if (this.micMute || !z2) {
                z4 = false;
            }
            this.unmutedByHold = z4;
            NativeInstance nativeInstance = this.tgVoip;
            if (nativeInstance != null) {
                nativeInstance.setMuteMicrophone(z);
            }
            Iterator<VoIPBaseService.StateListener> it = this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setMicMute$0 */
    public /* synthetic */ void lambda$setMicMute$0$VoIPService() {
        if (this.updateNotificationRunnable != null) {
            this.updateNotificationRunnable = null;
            TLRPC$Chat tLRPC$Chat = this.chat;
            showNotification(tLRPC$Chat.title, getRoundAvatarBitmap(tLRPC$Chat));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r0.participants.get(getSelfId());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean mutedByAdmin() {
        /*
            r2 = this;
            org.telegram.messenger.ChatObject$Call r0 = r2.groupCall
            if (r0 == 0) goto L_0x0024
            int r1 = r2.getSelfId()
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.participants
            java.lang.Object r0 = r0.get(r1)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r0
            if (r0 == 0) goto L_0x0024
            boolean r1 = r0.can_self_unmute
            if (r1 != 0) goto L_0x0024
            boolean r0 = r0.muted
            if (r0 == 0) goto L_0x0024
            org.telegram.tgnet.TLRPC$Chat r0 = r2.chat
            boolean r0 = org.telegram.messenger.ChatObject.canManageCalls(r0)
            if (r0 != 0) goto L_0x0024
            r0 = 1
            return r0
        L_0x0024:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.mutedByAdmin():boolean");
    }

    private static class ProxyVideoSink implements VideoSink {
        private VideoSink background;
        private VideoSink target;

        private ProxyVideoSink() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0012, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized void onFrame(org.webrtc.VideoFrame r2) {
            /*
                r1 = this;
                monitor-enter(r1)
                org.webrtc.VideoSink r0 = r1.target     // Catch:{ all -> 0x0013 }
                if (r0 != 0) goto L_0x0007
                monitor-exit(r1)
                return
            L_0x0007:
                r0.onFrame(r2)     // Catch:{ all -> 0x0013 }
                org.webrtc.VideoSink r0 = r1.background     // Catch:{ all -> 0x0013 }
                if (r0 == 0) goto L_0x0011
                r0.onFrame(r2)     // Catch:{ all -> 0x0013 }
            L_0x0011:
                monitor-exit(r1)
                return
            L_0x0013:
                r2 = move-exception
                monitor-exit(r1)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.ProxyVideoSink.onFrame(org.webrtc.VideoFrame):void");
        }

        public synchronized void setTarget(VideoSink videoSink) {
            this.target = videoSink;
        }

        public synchronized void setBackground(VideoSink videoSink) {
            this.background = videoSink;
        }

        public synchronized void swap() {
            VideoSink videoSink;
            if (!(this.target == null || (videoSink = this.background) == null)) {
                this.target = videoSink;
                this.background = null;
            }
        }
    }

    @SuppressLint({"MissingPermission", "InlinedApi"})
    public int onStartCommand(Intent intent, int i, int i2) {
        if (VoIPBaseService.sharedInstance != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Tried to start the VoIP service when it's already started");
            }
            return 2;
        }
        int intExtra = intent.getIntExtra("account", -1);
        this.currentAccount = intExtra;
        if (intExtra != -1) {
            this.classGuid = ConnectionsManager.generateClassGuid();
            int intExtra2 = intent.getIntExtra("user_id", 0);
            int intExtra3 = intent.getIntExtra("chat_id", 0);
            this.createGroupCall = intent.getBooleanExtra("createGroupCall", false);
            this.hasFewPeers = intent.getBooleanExtra("hasFewPeers", false);
            this.joinHash = intent.getStringExtra("hash");
            int intExtra4 = intent.getIntExtra("peerChannelId", 0);
            int intExtra5 = intent.getIntExtra("peerChatId", 0);
            int intExtra6 = intent.getIntExtra("peerUserId", 0);
            if (intExtra5 != 0) {
                TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
                this.groupCallPeer = tLRPC$TL_inputPeerChat;
                tLRPC$TL_inputPeerChat.chat_id = intExtra5;
                tLRPC$TL_inputPeerChat.access_hash = intent.getLongExtra("peerAccessHash", 0);
            } else if (intExtra4 != 0) {
                TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
                this.groupCallPeer = tLRPC$TL_inputPeerChannel;
                tLRPC$TL_inputPeerChannel.channel_id = intExtra4;
                tLRPC$TL_inputPeerChannel.access_hash = intent.getLongExtra("peerAccessHash", 0);
            } else if (intExtra6 != 0) {
                TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
                this.groupCallPeer = tLRPC$TL_inputPeerUser;
                tLRPC$TL_inputPeerUser.user_id = intExtra6;
                tLRPC$TL_inputPeerUser.access_hash = intent.getLongExtra("peerAccessHash", 0);
            }
            this.scheduleDate = intent.getIntExtra("scheduleDate", 0);
            this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
            this.videoCall = intent.getBooleanExtra("video_call", false);
            this.isVideoAvailable = intent.getBooleanExtra("can_video_call", false);
            this.notificationsDisabled = intent.getBooleanExtra("notifications_disabled", false);
            if (intExtra2 != 0) {
                this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(intExtra2));
            }
            if (intExtra3 != 0) {
                TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(intExtra3));
                this.chat = chat;
                if (ChatObject.isChannel(chat)) {
                    MessagesController.getInstance(this.currentAccount).startShortPoll(this.chat, this.classGuid, false);
                }
            }
            loadResources();
            this.localSink = new ProxyVideoSink();
            this.remoteSink = new ProxyVideoSink();
            try {
                this.isHeadsetPlugged = ((AudioManager) getSystemService("audio")).isWiredHeadsetOn();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (this.chat == null || this.createGroupCall || MessagesController.getInstance(this.currentAccount).getGroupCall(this.chat.id, false) != null) {
                if (this.videoCall) {
                    this.videoCapturer = NativeInstance.createVideoCapturer(this.localSink, this.isFrontFaceCamera);
                    this.videoState = 2;
                    if (!this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                        setAudioOutput(0);
                    }
                }
                if (this.user == null && this.chat == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("VoIPService: user == null AND chat == null");
                    }
                    stopSelf();
                    return 2;
                }
                VoIPBaseService.sharedInstance = this;
                synchronized (VoIPBaseService.sync) {
                    if (VoIPBaseService.setModeRunnable != null) {
                        Utilities.globalQueue.cancelRunnable(VoIPBaseService.setModeRunnable);
                        VoIPBaseService.setModeRunnable = null;
                    }
                }
                if (this.isOutgoing) {
                    if (this.user != null) {
                        dispatchStateChanged(14);
                        if (VoIPBaseService.USE_CONNECTION_SERVICE) {
                            Bundle bundle = new Bundle();
                            Bundle bundle2 = new Bundle();
                            bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", addAccountToTelecomManager());
                            bundle2.putInt("call_type", 1);
                            bundle.putBundle("android.telecom.extra.OUTGOING_CALL_EXTRAS", bundle2);
                            ContactsController instance = ContactsController.getInstance(this.currentAccount);
                            TLRPC$User tLRPC$User = this.user;
                            instance.createOrUpdateConnectionServiceContact(tLRPC$User.id, tLRPC$User.first_name, tLRPC$User.last_name);
                            ((TelecomManager) getSystemService("telecom")).placeCall(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), bundle);
                        } else {
                            $$Lambda$VoIPService$y6ZCLrqfb90Pe7tZWgDlkiMdlLo r9 = new Runnable() {
                                public final void run() {
                                    VoIPService.this.lambda$onStartCommand$1$VoIPService();
                                }
                            };
                            this.delayedStartOutgoingCall = r9;
                            AndroidUtilities.runOnUIThread(r9, 2000);
                        }
                    } else {
                        this.micMute = true;
                        startGroupCall(0, (String) null, false);
                        if (!this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                            setAudioOutput(0);
                        }
                    }
                    if (intent.getBooleanExtra("start_incall_activity", false)) {
                        Intent addFlags = new Intent(this, LaunchActivity.class).setAction(this.user != null ? "voip" : "voip_chat").addFlags(NUM);
                        if (this.chat != null) {
                            addFlags.putExtra("currentAccount", this.currentAccount);
                        }
                        startActivity(addFlags);
                    }
                } else {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeInCallActivity, new Object[0]);
                    TLRPC$PhoneCall tLRPC$PhoneCall = callIShouldHavePutIntoIntent;
                    this.privateCall = tLRPC$PhoneCall;
                    boolean z = tLRPC$PhoneCall != null && tLRPC$PhoneCall.video;
                    this.videoCall = z;
                    if (z) {
                        this.isVideoAvailable = true;
                    }
                    if (!z || (Build.VERSION.SDK_INT >= 23 && checkSelfPermission("android.permission.CAMERA") != 0)) {
                        this.videoState = 0;
                    } else {
                        this.videoCapturer = NativeInstance.createVideoCapturer(this.localSink, this.isFrontFaceCamera);
                        this.videoState = 2;
                    }
                    if (this.videoCall && !this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                        setAudioOutput(0);
                    }
                    callIShouldHavePutIntoIntent = null;
                    if (VoIPBaseService.USE_CONNECTION_SERVICE) {
                        acknowledgeCall(false);
                        showNotification();
                    } else {
                        acknowledgeCall(true);
                    }
                }
                initializeAccountRelatedThings();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$onStartCommand$2$VoIPService();
                    }
                });
                return 2;
            }
            FileLog.w("VoIPService: trying to open group call without call " + this.chat.id);
            stopSelf();
            return 2;
        }
        throw new IllegalStateException("No account specified when starting VoIP service");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStartCommand$1 */
    public /* synthetic */ void lambda$onStartCommand$1$VoIPService() {
        this.delayedStartOutgoingCall = null;
        startOutgoingCall();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStartCommand$2 */
    public /* synthetic */ void lambda$onStartCommand$2$VoIPService() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.voipServiceCreated, new Object[0]);
    }

    public void onCreate() {
        super.onCreate();
        if (callIShouldHavePutIntoIntent != null && Build.VERSION.SDK_INT >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            Notification.Builder showWhen = new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(LocaleController.getString("VoipOutgoingCall", NUM)).setShowWhen(false);
            if (this.groupCall != null) {
                showWhen.setSmallIcon(isMicMute() ? NUM : NUM);
            } else {
                showWhen.setSmallIcon(NUM);
            }
            startForeground(201, showWhen.build());
        }
    }

    /* access modifiers changed from: protected */
    public void updateServerConfig() {
        SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        Instance.setGlobalServerConfig(mainSettings.getString("voip_server_config", "{}"));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_phone_getCallConfig(), new RequestDelegate(mainSettings) {
            public final /* synthetic */ SharedPreferences f$0;

            {
                this.f$0 = r1;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.lambda$updateServerConfig$3(this.f$0, tLObject, tLRPC$TL_error);
            }
        });
    }

    static /* synthetic */ void lambda$updateServerConfig$3(SharedPreferences sharedPreferences, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            String str = ((TLRPC$TL_dataJSON) tLObject).data;
            Instance.setGlobalServerConfig(str);
            sharedPreferences.edit().putString("voip_server_config", str).commit();
        }
    }

    /* access modifiers changed from: protected */
    public void onTgVoipStop(Instance.FinalState finalState) {
        if (this.user != null) {
            if (this.needRateCall || this.forceRating || finalState.isRatingSuggested) {
                startRatingActivity();
                this.needRateCall = false;
            }
            if (this.needSendDebugLog && finalState.debugLog != null) {
                TLRPC$TL_phone_saveCallDebug tLRPC$TL_phone_saveCallDebug = new TLRPC$TL_phone_saveCallDebug();
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
                tLRPC$TL_phone_saveCallDebug.debug = tLRPC$TL_dataJSON;
                tLRPC$TL_dataJSON.data = finalState.debugLog;
                TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
                tLRPC$TL_phone_saveCallDebug.peer = tLRPC$TL_inputPhoneCall;
                TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
                tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
                tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_saveCallDebug, $$Lambda$VoIPService$WMZK72erMIrcdd14_ySFO2ejqYY.INSTANCE);
                this.needSendDebugLog = false;
            }
        }
    }

    static /* synthetic */ void lambda$onTgVoipStop$4(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Sent debug logs, response = " + tLObject);
        }
    }

    public static VoIPService getSharedInstance() {
        VoIPBaseService voIPBaseService = VoIPBaseService.sharedInstance;
        if (voIPBaseService instanceof VoIPService) {
            return (VoIPService) voIPBaseService;
        }
        return null;
    }

    public TLRPC$User getUser() {
        return this.user;
    }

    public TLRPC$Chat getChat() {
        return this.chat;
    }

    public void setGroupCallHash(String str) {
        if (this.currentGroupModeStreaming && !TextUtils.isEmpty(str) && !str.equals(this.joinHash)) {
            this.joinHash = str;
            createGroupInstance(false);
        }
    }

    public int getCallerId() {
        TLRPC$User tLRPC$User = this.user;
        if (tLRPC$User != null) {
            return tLRPC$User.id;
        }
        return -this.chat.id;
    }

    public void hangUp() {
        hangUp(0, (Runnable) null);
    }

    public void hangUp(int i) {
        hangUp(i, (Runnable) null);
    }

    public void hangUp(Runnable runnable) {
        hangUp(0, runnable);
    }

    public void hangUp(int i, Runnable runnable) {
        int i2 = this.currentState;
        declineIncomingCall((i2 == 16 || (i2 == 13 && this.isOutgoing)) ? 3 : 1, runnable);
        if (this.groupCall != null && i != 2) {
            if (i == 1) {
                TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chat.id);
                if (chatFull != null) {
                    chatFull.flags &= -2097153;
                    chatFull.call = null;
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chat.id), Long.valueOf(this.groupCall.call.id), Boolean.FALSE);
                }
                TLRPC$TL_phone_discardGroupCall tLRPC$TL_phone_discardGroupCall = new TLRPC$TL_phone_discardGroupCall();
                tLRPC$TL_phone_discardGroupCall.call = this.groupCall.getInputGroupCall();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardGroupCall, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        VoIPService.this.lambda$hangUp$5$VoIPService(tLObject, tLRPC$TL_error);
                    }
                });
                return;
            }
            TLRPC$TL_phone_leaveGroupCall tLRPC$TL_phone_leaveGroupCall = new TLRPC$TL_phone_leaveGroupCall();
            tLRPC$TL_phone_leaveGroupCall.call = this.groupCall.getInputGroupCall();
            tLRPC$TL_phone_leaveGroupCall.source = this.mySource;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_leaveGroupCall, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$hangUp$6$VoIPService(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hangUp$5 */
    public /* synthetic */ void lambda$hangUp$5$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hangUp$6 */
    public /* synthetic */ void lambda$hangUp$6$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
        }
    }

    private void startOutgoingCall() {
        VoIPBaseService.CallConnection callConnection;
        if (VoIPBaseService.USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
            callConnection.setDialing();
        }
        configureDeviceForCall();
        showNotification();
        startConnectingSound();
        dispatchStateChanged(14);
        AndroidUtilities.runOnUIThread($$Lambda$VoIPService$zDdjldXbqTBUVL8d63c5hYpU0Ac.INSTANCE);
        Utilities.random.nextBytes(new byte[256]);
        TLRPC$TL_messages_getDhConfig tLRPC$TL_messages_getDhConfig = new TLRPC$TL_messages_getDhConfig();
        tLRPC$TL_messages_getDhConfig.random_length = 256;
        MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
        tLRPC$TL_messages_getDhConfig.version = instance.getLastSecretVersion();
        this.callReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getDhConfig, new RequestDelegate(instance) {
            public final /* synthetic */ MessagesStorage f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$startOutgoingCall$12$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOutgoingCall$12 */
    public /* synthetic */ void lambda$startOutgoingCall$12$VoIPService(MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.callReqId = 0;
        if (this.endCallAfterRequest) {
            callEnded();
        } else if (tLRPC$TL_error == null) {
            TLRPC$messages_DhConfig tLRPC$messages_DhConfig = (TLRPC$messages_DhConfig) tLObject;
            if (tLObject instanceof TLRPC$TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(tLRPC$messages_DhConfig.p, tLRPC$messages_DhConfig.g)) {
                    callFailed();
                    return;
                }
                messagesStorage.setSecretPBytes(tLRPC$messages_DhConfig.p);
                messagesStorage.setSecretG(tLRPC$messages_DhConfig.g);
                messagesStorage.setLastSecretVersion(tLRPC$messages_DhConfig.version);
                messagesStorage.saveSecretParams(messagesStorage.getLastSecretVersion(), messagesStorage.getSecretG(), messagesStorage.getSecretPBytes());
            }
            byte[] bArr = new byte[256];
            for (int i = 0; i < 256; i++) {
                bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ tLRPC$messages_DhConfig.random[i]);
            }
            byte[] byteArray = BigInteger.valueOf((long) messagesStorage.getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, messagesStorage.getSecretPBytes())).toByteArray();
            if (byteArray.length > 256) {
                byte[] bArr2 = new byte[256];
                System.arraycopy(byteArray, 1, bArr2, 0, 256);
                byteArray = bArr2;
            }
            TLRPC$TL_phone_requestCall tLRPC$TL_phone_requestCall = new TLRPC$TL_phone_requestCall();
            tLRPC$TL_phone_requestCall.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user);
            TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol = new TLRPC$TL_phoneCallProtocol();
            tLRPC$TL_phone_requestCall.protocol = tLRPC$TL_phoneCallProtocol;
            tLRPC$TL_phone_requestCall.video = this.videoCall;
            tLRPC$TL_phoneCallProtocol.udp_p2p = true;
            tLRPC$TL_phoneCallProtocol.udp_reflector = true;
            tLRPC$TL_phoneCallProtocol.min_layer = 65;
            tLRPC$TL_phoneCallProtocol.max_layer = Instance.getConnectionMaxLayer();
            tLRPC$TL_phone_requestCall.protocol.library_versions.addAll(Instance.AVAILABLE_VERSIONS);
            this.g_a = byteArray;
            tLRPC$TL_phone_requestCall.g_a_hash = Utilities.computeSHA256(byteArray, 0, byteArray.length);
            tLRPC$TL_phone_requestCall.random_id = Utilities.random.nextInt();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_requestCall, new RequestDelegate(bArr) {
                public final /* synthetic */ byte[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$null$11$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 2);
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error on getDhConfig " + tLRPC$TL_error);
            }
            callFailed();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$11 */
    public /* synthetic */ void lambda$null$11$VoIPService(byte[] bArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, bArr) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ byte[] f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                VoIPService.this.lambda$null$10$VoIPService(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$VoIPService(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, byte[] bArr) {
        if (tLRPC$TL_error == null) {
            this.privateCall = ((TLRPC$TL_phone_phoneCall) tLObject).phone_call;
            this.a_or_b = bArr;
            dispatchStateChanged(13);
            if (this.endCallAfterRequest) {
                hangUp();
                return;
            }
            if (this.pendingUpdates.size() > 0 && this.privateCall != null) {
                Iterator<TLRPC$PhoneCall> it = this.pendingUpdates.iterator();
                while (it.hasNext()) {
                    onCallUpdated(it.next());
                }
                this.pendingUpdates.clear();
            }
            $$Lambda$VoIPService$rg8IpyAwLYDhhFDq5sDDyoLx7Y r1 = new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$null$9$VoIPService();
                }
            };
            this.timeoutRunnable = r1;
            AndroidUtilities.runOnUIThread(r1, (long) MessagesController.getInstance(this.currentAccount).callReceiveTimeout);
        } else if (tLRPC$TL_error.code != 400 || !"PARTICIPANT_VERSION_OUTDATED".equals(tLRPC$TL_error.text)) {
            int i = tLRPC$TL_error.code;
            if (i == 403) {
                callFailed("ERROR_PRIVACY");
            } else if (i == 406) {
                callFailed("ERROR_LOCALIZED");
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error on phone.requestCall: " + tLRPC$TL_error);
                }
                callFailed();
            }
        } else {
            callFailed("ERROR_PEER_OUTDATED");
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$9 */
    public /* synthetic */ void lambda$null$9$VoIPService() {
        this.timeoutRunnable = null;
        TLRPC$TL_phone_discardCall tLRPC$TL_phone_discardCall = new TLRPC$TL_phone_discardCall();
        TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
        tLRPC$TL_phone_discardCall.peer = tLRPC$TL_inputPhoneCall;
        TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
        tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
        tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
        tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonMissed();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$null$8$VoIPService(tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
    public /* synthetic */ void lambda$null$8$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (BuildVars.LOGS_ENABLED) {
            if (tLRPC$TL_error != null) {
                FileLog.e("error on phone.discardCall: " + tLRPC$TL_error);
            } else {
                FileLog.d("phone.discardCall " + tLObject);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                VoIPService.this.callFailed();
            }
        });
    }

    private void acknowledgeCall(boolean z) {
        if (this.privateCall instanceof TLRPC$TL_phoneCallDiscarded) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("Call " + this.privateCall.id + " was discarded before the service started, stopping");
            }
            stopSelf();
        } else if (Build.VERSION.SDK_INT < 19 || !XiaomiUtilities.isMIUI() || XiaomiUtilities.isCustomPermissionGranted(10020) || !((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            TLRPC$TL_phone_receivedCall tLRPC$TL_phone_receivedCall = new TLRPC$TL_phone_receivedCall();
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_receivedCall.peer = tLRPC$TL_inputPhoneCall;
            TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_receivedCall, new RequestDelegate(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$acknowledgeCall$14$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 2);
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("MIUI: no permission to show when locked but the screen is locked. ¯\\_(ツ)_/¯");
            }
            stopSelf();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$acknowledgeCall$14 */
    public /* synthetic */ void lambda$acknowledgeCall$14$VoIPService(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$TL_error, z) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                VoIPService.this.lambda$null$13$VoIPService(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$13 */
    public /* synthetic */ void lambda$null$13$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, boolean z) {
        if (VoIPBaseService.sharedInstance != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("receivedCall response = " + tLObject);
            }
            if (tLRPC$TL_error != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("error on receivedCall: " + tLRPC$TL_error);
                }
                stopSelf();
                return;
            }
            if (VoIPBaseService.USE_CONNECTION_SERVICE) {
                ContactsController instance = ContactsController.getInstance(this.currentAccount);
                TLRPC$User tLRPC$User = this.user;
                instance.createOrUpdateConnectionServiceContact(tLRPC$User.id, tLRPC$User.first_name, tLRPC$User.last_name);
                Bundle bundle = new Bundle();
                bundle.putInt("call_type", 1);
                ((TelecomManager) getSystemService("telecom")).addNewIncomingCall(addAccountToTelecomManager(), bundle);
            }
            if (z) {
                startRinging();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void startRinging() {
        VoIPBaseService.CallConnection callConnection;
        if (this.currentState != 15) {
            if (VoIPBaseService.USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
                callConnection.setRinging();
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("starting ringing for call " + this.privateCall.id);
            }
            dispatchStateChanged(15);
            if (this.notificationsDisabled || Build.VERSION.SDK_INT < 21) {
                startRingtoneAndVibration(this.user.id);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("Starting incall activity for incoming call");
                }
                try {
                    PendingIntent.getActivity(this, 12345, new Intent(this, LaunchActivity.class).setAction("voip"), 0).send();
                } catch (Exception e) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Error starting incall activity", e);
                    }
                }
            } else {
                TLRPC$User tLRPC$User = this.user;
                showIncomingNotification(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), (CharSequence) null, this.user, this.privateCall.video, 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("Showing incoming call notification");
                }
            }
        }
    }

    public void startRingtoneAndVibration() {
        if (!this.startedRinging) {
            startRingtoneAndVibration(this.user.id);
            this.startedRinging = true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isRinging() {
        return this.currentState == 15;
    }

    public boolean isJoined() {
        int i = this.currentState;
        return (i == 1 || i == 6) ? false : true;
    }

    public void acceptIncomingCall() {
        MessagesController.getInstance(this.currentAccount).ignoreSetOnline = false;
        stopRinging();
        showNotification();
        configureDeviceForCall();
        startConnectingSound();
        dispatchStateChanged(12);
        AndroidUtilities.runOnUIThread($$Lambda$VoIPService$3okraCk310F_cIDUKkTRsh742FA.INSTANCE);
        MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
        TLRPC$TL_messages_getDhConfig tLRPC$TL_messages_getDhConfig = new TLRPC$TL_messages_getDhConfig();
        tLRPC$TL_messages_getDhConfig.random_length = 256;
        tLRPC$TL_messages_getDhConfig.version = instance.getLastSecretVersion();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getDhConfig, new RequestDelegate(instance) {
            public final /* synthetic */ MessagesStorage f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$acceptIncomingCall$18$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$acceptIncomingCall$18 */
    public /* synthetic */ void lambda$acceptIncomingCall$18$VoIPService(MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_DhConfig tLRPC$messages_DhConfig = (TLRPC$messages_DhConfig) tLObject;
            if (tLObject instanceof TLRPC$TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(tLRPC$messages_DhConfig.p, tLRPC$messages_DhConfig.g)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("stopping VoIP service, bad prime");
                    }
                    callFailed();
                    return;
                }
                messagesStorage.setSecretPBytes(tLRPC$messages_DhConfig.p);
                messagesStorage.setSecretG(tLRPC$messages_DhConfig.g);
                messagesStorage.setLastSecretVersion(tLRPC$messages_DhConfig.version);
                MessagesStorage.getInstance(this.currentAccount).saveSecretParams(messagesStorage.getLastSecretVersion(), messagesStorage.getSecretG(), messagesStorage.getSecretPBytes());
            }
            byte[] bArr = new byte[256];
            for (int i = 0; i < 256; i++) {
                bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ tLRPC$messages_DhConfig.random[i]);
            }
            if (this.privateCall == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("call is null");
                }
                callFailed();
                return;
            }
            this.a_or_b = bArr;
            BigInteger modPow = BigInteger.valueOf((long) messagesStorage.getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, messagesStorage.getSecretPBytes()));
            this.g_a_hash = this.privateCall.g_a_hash;
            byte[] byteArray = modPow.toByteArray();
            if (byteArray.length > 256) {
                byte[] bArr2 = new byte[256];
                System.arraycopy(byteArray, 1, bArr2, 0, 256);
                byteArray = bArr2;
            }
            TLRPC$TL_phone_acceptCall tLRPC$TL_phone_acceptCall = new TLRPC$TL_phone_acceptCall();
            tLRPC$TL_phone_acceptCall.g_b = byteArray;
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_acceptCall.peer = tLRPC$TL_inputPhoneCall;
            TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol = new TLRPC$TL_phoneCallProtocol();
            tLRPC$TL_phone_acceptCall.protocol = tLRPC$TL_phoneCallProtocol;
            tLRPC$TL_phoneCallProtocol.udp_reflector = true;
            tLRPC$TL_phoneCallProtocol.udp_p2p = true;
            tLRPC$TL_phoneCallProtocol.min_layer = 65;
            tLRPC$TL_phoneCallProtocol.max_layer = Instance.getConnectionMaxLayer();
            tLRPC$TL_phone_acceptCall.protocol.library_versions.addAll(Instance.AVAILABLE_VERSIONS);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_acceptCall, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$null$17$VoIPService(tLObject, tLRPC$TL_error);
                }
            }, 2);
            return;
        }
        callFailed();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$17 */
    public /* synthetic */ void lambda$null$17$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPService.this.lambda$null$16$VoIPService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$16 */
    public /* synthetic */ void lambda$null$16$VoIPService(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("accept call ok! " + tLObject);
            }
            TLRPC$PhoneCall tLRPC$PhoneCall = ((TLRPC$TL_phone_phoneCall) tLObject).phone_call;
            this.privateCall = tLRPC$PhoneCall;
            if (tLRPC$PhoneCall instanceof TLRPC$TL_phoneCallDiscarded) {
                onCallUpdated(tLRPC$PhoneCall);
                return;
            }
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("Error on phone.acceptCall: " + tLRPC$TL_error);
        }
        callFailed();
    }

    public void declineIncomingCall() {
        declineIncomingCall(1, (Runnable) null);
    }

    public void requestVideoCall() {
        NativeInstance nativeInstance = this.tgVoip;
        if (nativeInstance != null) {
            nativeInstance.setupOutgoingVideo(this.localSink, this.isFrontFaceCamera);
        }
    }

    public void switchCamera() {
        NativeInstance nativeInstance = this.tgVoip;
        if (nativeInstance == null || this.switchingCamera) {
            long j = this.videoCapturer;
            if (j != 0 && !this.switchingCamera) {
                NativeInstance.switchCameraCapturer(j, !this.isFrontFaceCamera);
                return;
            }
            return;
        }
        this.switchingCamera = true;
        nativeInstance.switchCamera(true ^ this.isFrontFaceCamera);
    }

    public void setVideoState(int i) {
        int i2;
        NativeInstance nativeInstance = this.tgVoip;
        if (nativeInstance == null) {
            long j = this.videoCapturer;
            if (j != 0) {
                this.videoState = i;
                NativeInstance.setVideoStateCapturer(j, i);
            } else if (i == 2 && (i2 = this.currentState) != 17 && i2 != 11) {
                this.videoCapturer = NativeInstance.createVideoCapturer(this.localSink, this.isFrontFaceCamera);
                this.videoState = 2;
            }
        } else {
            this.videoState = i;
            nativeInstance.setVideoState(i);
            checkIsNear();
        }
    }

    public int getVideoState() {
        return this.videoState;
    }

    public void setSinks(VideoSink videoSink, VideoSink videoSink2) {
        this.localSink.setTarget(videoSink);
        this.remoteSink.setTarget(videoSink2);
    }

    public void setBackgroundSinks(VideoSink videoSink, VideoSink videoSink2) {
        this.localSink.setBackground(videoSink);
        this.remoteSink.setBackground(videoSink2);
    }

    public void swapSinks() {
        this.localSink.swap();
        this.remoteSink.swap();
    }

    public void onDestroy() {
        super.onDestroy();
        setSinks((VideoSink) null, (VideoSink) null);
        Runnable runnable = this.onDestroyRunnable;
        if (runnable != null) {
            runnable.run();
        }
        if (ChatObject.isChannel(this.chat)) {
            MessagesController.getInstance(this.currentAccount).startShortPoll(this.chat, this.classGuid, true);
        }
    }

    /* access modifiers changed from: protected */
    public Class<? extends Activity> getUIActivityClass() {
        return LaunchActivity.class;
    }

    public boolean isHangingUp() {
        return this.currentState == 10;
    }

    public void declineIncomingCall(int i, Runnable runnable) {
        stopRinging();
        this.callDiscardReason = i;
        int i2 = this.currentState;
        if (i2 == 14) {
            Runnable runnable2 = this.delayedStartOutgoingCall;
            if (runnable2 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable2);
                callEnded();
                return;
            }
            dispatchStateChanged(10);
            this.endCallAfterRequest = true;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$declineIncomingCall$19$VoIPService();
                }
            }, 5000);
        } else if (i2 != 10 && i2 != 11) {
            dispatchStateChanged(10);
            if (this.privateCall == null) {
                this.onDestroyRunnable = runnable;
                callEnded();
                if (this.callReqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.callReqId, false);
                    this.callReqId = 0;
                    return;
                }
                return;
            }
            TLRPC$TL_phone_discardCall tLRPC$TL_phone_discardCall = new TLRPC$TL_phone_discardCall();
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_discardCall.peer = tLRPC$TL_inputPhoneCall;
            TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_phone_discardCall.duration = (int) (getCallDuration() / 1000);
            NativeInstance nativeInstance = this.tgVoip;
            tLRPC$TL_phone_discardCall.connection_id = nativeInstance != null ? nativeInstance.getPreferredRelayId() : 0;
            if (i == 2) {
                tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonDisconnect();
            } else if (i == 3) {
                tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonMissed();
            } else if (i != 4) {
                tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonHangup();
            } else {
                tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonBusy();
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$declineIncomingCall$20$VoIPService(tLObject, tLRPC$TL_error);
                }
            }, 2);
            this.onDestroyRunnable = runnable;
            callEnded();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$declineIncomingCall$19 */
    public /* synthetic */ void lambda$declineIncomingCall$19$VoIPService() {
        if (this.currentState == 10) {
            callEnded();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$declineIncomingCall$20 */
    public /* synthetic */ void lambda$declineIncomingCall$20$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            if (tLObject instanceof TLRPC$TL_updates) {
                MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("phone.discardCall " + tLObject);
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.e("error on phone.discardCall: " + tLRPC$TL_error);
        }
    }

    public void onSignalingData(TLRPC$TL_updatePhoneCallSignalingData tLRPC$TL_updatePhoneCallSignalingData) {
        NativeInstance nativeInstance;
        if (this.user != null && (nativeInstance = this.tgVoip) != null && !nativeInstance.isGroup() && getCallID() == tLRPC$TL_updatePhoneCallSignalingData.phone_call_id) {
            this.tgVoip.onSignalingDataReceive(tLRPC$TL_updatePhoneCallSignalingData.data);
        }
    }

    public int getSelfId() {
        TLRPC$InputPeer tLRPC$InputPeer = this.groupCallPeer;
        if (tLRPC$InputPeer == null) {
            return UserConfig.getInstance(this.currentAccount).clientUserId;
        }
        if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerUser) {
            return tLRPC$InputPeer.user_id;
        }
        if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChannel) {
            return -tLRPC$InputPeer.channel_id;
        }
        return -tLRPC$InputPeer.chat_id;
    }

    public void onGroupCallParticipantsUpdate(TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants) {
        ChatObject.Call call;
        if (this.chat != null && (call = this.groupCall) != null && call.call.id == tLRPC$TL_updateGroupCallParticipants.call.id) {
            int selfId = getSelfId();
            int size = tLRPC$TL_updateGroupCallParticipants.participants.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_updateGroupCallParticipants.participants.get(i);
                if (tLRPC$TL_groupCallParticipant.left) {
                    int i2 = tLRPC$TL_groupCallParticipant.source;
                    if (i2 != 0 && i2 == this.mySource) {
                        int i3 = 0;
                        for (int i4 = 0; i4 < size; i4++) {
                            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = tLRPC$TL_updateGroupCallParticipants.participants.get(i4);
                            if (tLRPC$TL_groupCallParticipant2.self || tLRPC$TL_groupCallParticipant2.source == this.mySource) {
                                i3++;
                            }
                        }
                        if (i3 > 1) {
                            hangUp(2);
                            return;
                        }
                    }
                } else if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) != selfId) {
                    continue;
                } else {
                    int i5 = tLRPC$TL_groupCallParticipant.source;
                    int i6 = this.mySource;
                    if (i5 == i6 || i6 == 0 || i5 == 0) {
                        if (ChatObject.isChannel(this.chat) && this.currentGroupModeStreaming && tLRPC$TL_groupCallParticipant.can_self_unmute) {
                            this.switchingStream = true;
                            createGroupInstance(false);
                        }
                        if (tLRPC$TL_groupCallParticipant.muted) {
                            setMicMute(true, false, false);
                        }
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("source mismatch my = " + this.mySource + " psrc = " + tLRPC$TL_groupCallParticipant.source);
                        }
                        hangUp(2);
                        return;
                    }
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onGroupCallUpdated(org.telegram.tgnet.TLRPC$GroupCall r27) {
        /*
            r26 = this;
            r1 = r26
            r2 = r27
            java.lang.String r3 = ""
            org.telegram.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            org.telegram.messenger.ChatObject$Call r0 = r1.groupCall
            if (r0 == 0) goto L_0x011f
            org.telegram.tgnet.TLRPC$GroupCall r0 = r0.call
            long r4 = r0.id
            long r6 = r2.id
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x001b
            goto L_0x011f
        L_0x001b:
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_groupCallDiscarded
            r4 = 2
            if (r0 == 0) goto L_0x0024
            r1.hangUp((int) r4)
            return
        L_0x0024:
            r5 = 0
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r2.params
            r6 = 0
            if (r0 == 0) goto L_0x0040
            org.json.JSONObject r7 = new org.json.JSONObject     // Catch:{ Exception -> 0x003c }
            java.lang.String r0 = r0.data     // Catch:{ Exception -> 0x003c }
            r7.<init>(r0)     // Catch:{ Exception -> 0x003c }
            java.lang.String r0 = "stream"
            boolean r0 = r7.optBoolean(r0)     // Catch:{ Exception -> 0x0039 }
            r5 = r7
            goto L_0x0041
        L_0x0039:
            r0 = move-exception
            r5 = r7
            goto L_0x003d
        L_0x003c:
            r0 = move-exception
        L_0x003d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0040:
            r0 = 0
        L_0x0041:
            int r7 = r1.currentState
            r8 = 1
            if (r7 == r8) goto L_0x004a
            boolean r7 = r1.currentGroupModeStreaming
            if (r0 == r7) goto L_0x011f
        L_0x004a:
            org.telegram.tgnet.TLRPC$TL_dataJSON r2 = r2.params
            if (r2 == 0) goto L_0x011f
            boolean r2 = r1.playedConnectedSound
            if (r2 == 0) goto L_0x0058
            boolean r2 = r1.currentGroupModeStreaming
            if (r0 == r2) goto L_0x0058
            r1.switchingStream = r8
        L_0x0058:
            r1.currentGroupModeStreaming = r0
            if (r0 == 0) goto L_0x0063
            org.telegram.messenger.voip.NativeInstance r0 = r1.tgVoip     // Catch:{ Exception -> 0x011b }
            r0.prepareForStream()     // Catch:{ Exception -> 0x011b }
            goto L_0x0117
        L_0x0063:
            java.lang.String r0 = "transport"
            org.json.JSONObject r0 = r5.getJSONObject(r0)     // Catch:{ Exception -> 0x011b }
            java.lang.String r2 = "ufrag"
            java.lang.String r2 = r0.getString(r2)     // Catch:{ Exception -> 0x011b }
            java.lang.String r5 = "pwd"
            java.lang.String r5 = r0.getString(r5)     // Catch:{ Exception -> 0x011b }
            java.lang.String r7 = "fingerprints"
            org.json.JSONArray r7 = r0.getJSONArray(r7)     // Catch:{ Exception -> 0x011b }
            int r8 = r7.length()     // Catch:{ Exception -> 0x011b }
            org.telegram.messenger.voip.Instance$Fingerprint[] r9 = new org.telegram.messenger.voip.Instance.Fingerprint[r8]     // Catch:{ Exception -> 0x011b }
            r10 = 0
        L_0x0083:
            if (r10 >= r8) goto L_0x00a5
            org.json.JSONObject r11 = r7.getJSONObject(r10)     // Catch:{ Exception -> 0x011b }
            org.telegram.messenger.voip.Instance$Fingerprint r12 = new org.telegram.messenger.voip.Instance$Fingerprint     // Catch:{ Exception -> 0x011b }
            java.lang.String r13 = "hash"
            java.lang.String r13 = r11.getString(r13)     // Catch:{ Exception -> 0x011b }
            java.lang.String r14 = "setup"
            java.lang.String r14 = r11.getString(r14)     // Catch:{ Exception -> 0x011b }
            java.lang.String r15 = "fingerprint"
            java.lang.String r11 = r11.getString(r15)     // Catch:{ Exception -> 0x011b }
            r12.<init>(r13, r14, r11)     // Catch:{ Exception -> 0x011b }
            r9[r10] = r12     // Catch:{ Exception -> 0x011b }
            int r10 = r10 + 1
            goto L_0x0083
        L_0x00a5:
            java.lang.String r7 = "candidates"
            org.json.JSONArray r0 = r0.getJSONArray(r7)     // Catch:{ Exception -> 0x011b }
            int r7 = r0.length()     // Catch:{ Exception -> 0x011b }
            org.telegram.messenger.voip.Instance$Candidate[] r8 = new org.telegram.messenger.voip.Instance.Candidate[r7]     // Catch:{ Exception -> 0x011b }
        L_0x00b1:
            if (r6 >= r7) goto L_0x0112
            org.json.JSONObject r10 = r0.getJSONObject(r6)     // Catch:{ Exception -> 0x011b }
            org.telegram.messenger.voip.Instance$Candidate r25 = new org.telegram.messenger.voip.Instance$Candidate     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "port"
            java.lang.String r12 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "protocol"
            java.lang.String r13 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "network"
            java.lang.String r14 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "generation"
            java.lang.String r15 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "id"
            java.lang.String r16 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "component"
            java.lang.String r17 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "foundation"
            java.lang.String r18 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "priority"
            java.lang.String r19 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "ip"
            java.lang.String r20 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "type"
            java.lang.String r21 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "tcpType"
            java.lang.String r22 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "relAddr"
            java.lang.String r23 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            java.lang.String r11 = "relPort"
            java.lang.String r24 = r10.optString(r11, r3)     // Catch:{ Exception -> 0x011b }
            r11 = r25
            r11.<init>(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24)     // Catch:{ Exception -> 0x011b }
            r8[r6] = r25     // Catch:{ Exception -> 0x011b }
            int r6 = r6 + 1
            goto L_0x00b1
        L_0x0112:
            org.telegram.messenger.voip.NativeInstance r0 = r1.tgVoip     // Catch:{ Exception -> 0x011b }
            r0.setJoinResponsePayload(r2, r5, r9, r8)     // Catch:{ Exception -> 0x011b }
        L_0x0117:
            r1.dispatchStateChanged(r4)     // Catch:{ Exception -> 0x011b }
            goto L_0x011f
        L_0x011b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x011f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.onGroupCallUpdated(org.telegram.tgnet.TLRPC$GroupCall):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x015d  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x016a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCallUpdated(org.telegram.tgnet.TLRPC$PhoneCall r7) {
        /*
            r6 = this;
            org.telegram.tgnet.TLRPC$User r0 = r6.user
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.tgnet.TLRPC$PhoneCall r0 = r6.privateCall
            if (r0 != 0) goto L_0x000f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneCall> r0 = r6.pendingUpdates
            r0.add(r7)
            return
        L_0x000f:
            if (r7 != 0) goto L_0x0012
            return
        L_0x0012:
            long r1 = r7.id
            long r3 = r0.id
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0046
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0045
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onCallUpdated called with wrong call id (got "
            r0.append(r1)
            long r1 = r7.id
            r0.append(r1)
            java.lang.String r7 = ", expected "
            r0.append(r7)
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r6.privateCall
            long r1 = r7.id
            r0.append(r1)
            java.lang.String r7 = ")"
            r0.append(r7)
            java.lang.String r7 = r0.toString()
            org.telegram.messenger.FileLog.w(r7)
        L_0x0045:
            return
        L_0x0046:
            long r1 = r7.access_hash
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x0052
            long r0 = r0.access_hash
            r7.access_hash = r0
        L_0x0052:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x006a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Call updated: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x006a:
            r6.privateCall = r7
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallDiscarded
            r1 = 1
            if (r0 == 0) goto L_0x00ad
            boolean r0 = r7.need_debug
            r6.needSendDebugLog = r0
            boolean r0 = r7.need_rating
            r6.needRateCall = r0
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0082
            java.lang.String r0 = "call discarded, stopping service"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0082:
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r7 = r7.reason
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy
            if (r7 == 0) goto L_0x00a8
            r7 = 17
            r6.dispatchStateChanged(r7)
            r6.playingSound = r1
            org.telegram.messenger.DispatchQueue r7 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.messenger.voip.-$$Lambda$VoIPService$KmYVqcAZ0uG31IQIdA1Z-MU_PmY r0 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$KmYVqcAZ0uG31IQIdA1Z-MU_PmY
            r0.<init>()
            r7.postRunnable(r0)
            java.lang.Runnable r7 = r6.afterSoundRunnable
            r0 = 1500(0x5dc, double:7.41E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7, r0)
            r6.endConnectionServiceCall(r0)
            r6.stopSelf()
            goto L_0x01c2
        L_0x00a8:
            r6.callEnded()
            goto L_0x01c2
        L_0x00ad:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_phoneCall
            if (r0 == 0) goto L_0x016e
            byte[] r0 = r6.authKey
            if (r0 != 0) goto L_0x016e
            byte[] r0 = r7.g_a_or_b
            if (r0 != 0) goto L_0x00c6
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r7 == 0) goto L_0x00c2
            java.lang.String r7 = "stopping VoIP service, Ga == null"
            org.telegram.messenger.FileLog.w(r7)
        L_0x00c2:
            r6.callFailed()
            return
        L_0x00c6:
            byte[] r2 = r6.g_a_hash
            int r3 = r0.length
            r4 = 0
            byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r0, r4, r3)
            boolean r0 = java.util.Arrays.equals(r2, r0)
            if (r0 != 0) goto L_0x00e1
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r7 == 0) goto L_0x00dd
            java.lang.String r7 = "stopping VoIP service, Ga hash doesn't match"
            org.telegram.messenger.FileLog.w(r7)
        L_0x00dd:
            r6.callFailed()
            return
        L_0x00e1:
            byte[] r0 = r7.g_a_or_b
            r6.g_a = r0
            java.math.BigInteger r0 = new java.math.BigInteger
            byte[] r2 = r7.g_a_or_b
            r0.<init>(r1, r2)
            java.math.BigInteger r2 = new java.math.BigInteger
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)
            byte[] r3 = r3.getSecretPBytes()
            r2.<init>(r1, r3)
            boolean r3 = org.telegram.messenger.Utilities.isGoodGaAndGb(r0, r2)
            if (r3 != 0) goto L_0x010e
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r7 == 0) goto L_0x010a
            java.lang.String r7 = "stopping VoIP service, bad Ga and Gb (accepting)"
            org.telegram.messenger.FileLog.w(r7)
        L_0x010a:
            r6.callFailed()
            return
        L_0x010e:
            java.math.BigInteger r3 = new java.math.BigInteger
            byte[] r5 = r6.a_or_b
            r3.<init>(r1, r5)
            java.math.BigInteger r0 = r0.modPow(r3, r2)
            byte[] r0 = r0.toByteArray()
            int r1 = r0.length
            r2 = 256(0x100, float:3.59E-43)
            if (r1 <= r2) goto L_0x012b
            byte[] r1 = new byte[r2]
            int r3 = r0.length
            int r3 = r3 - r2
            java.lang.System.arraycopy(r0, r3, r1, r4, r2)
        L_0x0129:
            r0 = r1
            goto L_0x0142
        L_0x012b:
            int r1 = r0.length
            if (r1 >= r2) goto L_0x0142
            byte[] r1 = new byte[r2]
            int r3 = r0.length
            int r3 = 256 - r3
            int r5 = r0.length
            java.lang.System.arraycopy(r0, r4, r1, r3, r5)
            r3 = 0
        L_0x0138:
            int r5 = r0.length
            int r5 = 256 - r5
            if (r3 >= r5) goto L_0x0129
            r1[r3] = r4
            int r3 = r3 + 1
            goto L_0x0138
        L_0x0142:
            byte[] r1 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r0)
            r2 = 8
            byte[] r3 = new byte[r2]
            int r5 = r1.length
            int r5 = r5 - r2
            java.lang.System.arraycopy(r1, r5, r3, r4, r2)
            r6.authKey = r0
            long r0 = org.telegram.messenger.Utilities.bytesToLong(r3)
            r6.keyFingerprint = r0
            long r2 = r7.key_fingerprint
            int r7 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x016a
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r7 == 0) goto L_0x0166
            java.lang.String r7 = "key fingerprints don't match"
            org.telegram.messenger.FileLog.w(r7)
        L_0x0166:
            r6.callFailed()
            return
        L_0x016a:
            r6.initiateActualEncryptedCall()
            goto L_0x01c2
        L_0x016e:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallAccepted
            if (r0 == 0) goto L_0x017a
            byte[] r0 = r6.authKey
            if (r0 != 0) goto L_0x017a
            r6.processAcceptedCall()
            goto L_0x01c2
        L_0x017a:
            int r0 = r6.currentState
            r1 = 13
            if (r0 != r1) goto L_0x01c2
            int r7 = r7.receive_date
            if (r7 == 0) goto L_0x01c2
            r7 = 16
            r6.dispatchStateChanged(r7)
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r7 == 0) goto L_0x0192
            java.lang.String r7 = "!!!!!! CALL RECEIVED"
            org.telegram.messenger.FileLog.d(r7)
        L_0x0192:
            java.lang.Runnable r7 = r6.connectingSoundRunnable
            r0 = 0
            if (r7 == 0) goto L_0x019c
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r7)
            r6.connectingSoundRunnable = r0
        L_0x019c:
            org.telegram.messenger.DispatchQueue r7 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.messenger.voip.-$$Lambda$VoIPService$cVj6RuJCv533q_4Bv0gyzG9XU9s r1 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$cVj6RuJCv533q_4Bv0gyzG9XU9s
            r1.<init>()
            r7.postRunnable(r1)
            java.lang.Runnable r7 = r6.timeoutRunnable
            if (r7 == 0) goto L_0x01af
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r7)
            r6.timeoutRunnable = r0
        L_0x01af:
            org.telegram.messenger.voip.-$$Lambda$VoIPService$PwT4Cw0uKxzJuluwzmnKImCJvF8 r7 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$PwT4Cw0uKxzJuluwzmnKImCJvF8
            r7.<init>()
            r6.timeoutRunnable = r7
            int r0 = r6.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r0 = r0.callRingTimeout
            long r0 = (long) r0
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7, r0)
        L_0x01c2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.onCallUpdated(org.telegram.tgnet.TLRPC$PhoneCall):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCallUpdated$21 */
    public /* synthetic */ void lambda$onCallUpdated$21$VoIPService() {
        this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCallUpdated$22 */
    public /* synthetic */ void lambda$onCallUpdated$22$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.spRingbackID, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCallUpdated$23 */
    public /* synthetic */ void lambda$onCallUpdated$23$VoIPService() {
        this.timeoutRunnable = null;
        declineIncomingCall(3, (Runnable) null);
    }

    private void startRatingActivity() {
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).putExtra("call_id", this.privateCall.id).putExtra("call_access_hash", this.privateCall.access_hash).putExtra("call_video", this.privateCall.video).putExtra("account", this.currentAccount).addFlags(NUM), 0).send();
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error starting incall activity", e);
            }
        }
    }

    public byte[] getEncryptionKey() {
        return this.authKey;
    }

    private void processAcceptedCall() {
        byte[] bArr;
        dispatchStateChanged(12);
        BigInteger bigInteger = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
        BigInteger bigInteger2 = new BigInteger(1, this.privateCall.g_b);
        if (!Utilities.isGoodGaAndGb(bigInteger2, bigInteger)) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("stopping VoIP service, bad Ga and Gb");
            }
            callFailed();
            return;
        }
        byte[] byteArray = bigInteger2.modPow(new BigInteger(1, this.a_or_b), bigInteger).toByteArray();
        if (byteArray.length > 256) {
            bArr = new byte[256];
            System.arraycopy(byteArray, byteArray.length - 256, bArr, 0, 256);
        } else {
            if (byteArray.length < 256) {
                bArr = new byte[256];
                System.arraycopy(byteArray, 0, bArr, 256 - byteArray.length, byteArray.length);
                for (int i = 0; i < 256 - byteArray.length; i++) {
                    bArr[i] = 0;
                }
            }
            byte[] computeSHA1 = Utilities.computeSHA1(byteArray);
            byte[] bArr2 = new byte[8];
            System.arraycopy(computeSHA1, computeSHA1.length - 8, bArr2, 0, 8);
            long bytesToLong = Utilities.bytesToLong(bArr2);
            this.authKey = byteArray;
            this.keyFingerprint = bytesToLong;
            TLRPC$TL_phone_confirmCall tLRPC$TL_phone_confirmCall = new TLRPC$TL_phone_confirmCall();
            tLRPC$TL_phone_confirmCall.g_a = this.g_a;
            tLRPC$TL_phone_confirmCall.key_fingerprint = bytesToLong;
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_confirmCall.peer = tLRPC$TL_inputPhoneCall;
            TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol = new TLRPC$TL_phoneCallProtocol();
            tLRPC$TL_phone_confirmCall.protocol = tLRPC$TL_phoneCallProtocol;
            tLRPC$TL_phoneCallProtocol.max_layer = Instance.getConnectionMaxLayer();
            TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol2 = tLRPC$TL_phone_confirmCall.protocol;
            tLRPC$TL_phoneCallProtocol2.min_layer = 65;
            tLRPC$TL_phoneCallProtocol2.udp_reflector = true;
            tLRPC$TL_phoneCallProtocol2.udp_p2p = true;
            tLRPC$TL_phoneCallProtocol2.library_versions.addAll(Instance.AVAILABLE_VERSIONS);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_confirmCall, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$processAcceptedCall$25$VoIPService(tLObject, tLRPC$TL_error);
                }
            });
        }
        byteArray = bArr;
        byte[] computeSHA12 = Utilities.computeSHA1(byteArray);
        byte[] bArr22 = new byte[8];
        System.arraycopy(computeSHA12, computeSHA12.length - 8, bArr22, 0, 8);
        long bytesToLong2 = Utilities.bytesToLong(bArr22);
        this.authKey = byteArray;
        this.keyFingerprint = bytesToLong2;
        TLRPC$TL_phone_confirmCall tLRPC$TL_phone_confirmCall2 = new TLRPC$TL_phone_confirmCall();
        tLRPC$TL_phone_confirmCall2.g_a = this.g_a;
        tLRPC$TL_phone_confirmCall2.key_fingerprint = bytesToLong2;
        TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall2 = new TLRPC$TL_inputPhoneCall();
        tLRPC$TL_phone_confirmCall2.peer = tLRPC$TL_inputPhoneCall2;
        TLRPC$PhoneCall tLRPC$PhoneCall2 = this.privateCall;
        tLRPC$TL_inputPhoneCall2.id = tLRPC$PhoneCall2.id;
        tLRPC$TL_inputPhoneCall2.access_hash = tLRPC$PhoneCall2.access_hash;
        TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol3 = new TLRPC$TL_phoneCallProtocol();
        tLRPC$TL_phone_confirmCall2.protocol = tLRPC$TL_phoneCallProtocol3;
        tLRPC$TL_phoneCallProtocol3.max_layer = Instance.getConnectionMaxLayer();
        TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol22 = tLRPC$TL_phone_confirmCall2.protocol;
        tLRPC$TL_phoneCallProtocol22.min_layer = 65;
        tLRPC$TL_phoneCallProtocol22.udp_reflector = true;
        tLRPC$TL_phoneCallProtocol22.udp_p2p = true;
        tLRPC$TL_phoneCallProtocol22.library_versions.addAll(Instance.AVAILABLE_VERSIONS);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_confirmCall2, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$processAcceptedCall$25$VoIPService(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processAcceptedCall$25 */
    public /* synthetic */ void lambda$processAcceptedCall$25$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPService.this.lambda$null$24$VoIPService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$24 */
    public /* synthetic */ void lambda$null$24$VoIPService(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error != null) {
            callFailed();
            return;
        }
        this.privateCall = ((TLRPC$TL_phone_phoneCall) tLObject).phone_call;
        initiateActualEncryptedCall();
    }

    private int convertDataSavingMode(int i) {
        if (i != 3) {
            return i;
        }
        return ApplicationLoader.isRoaming() ? 1 : 0;
    }

    public void migrateToChat(TLRPC$Chat tLRPC$Chat) {
        this.chat = tLRPC$Chat;
    }

    public void setGroupCallPeer(TLRPC$InputPeer tLRPC$InputPeer) {
        ChatObject.Call call = this.groupCall;
        if (call != null) {
            this.groupCallPeer = tLRPC$InputPeer;
            call.setSelfPeer(tLRPC$InputPeer);
            createGroupInstance(true);
        }
    }

    private void startGroupCall(int i, String str, boolean z) {
        if (VoIPBaseService.sharedInstance == this) {
            int i2 = 2;
            if (this.createGroupCall) {
                ChatObject.Call call = new ChatObject.Call();
                this.groupCall = call;
                call.call = new TLRPC$TL_groupCall();
                ChatObject.Call call2 = this.groupCall;
                TLRPC$GroupCall tLRPC$GroupCall = call2.call;
                tLRPC$GroupCall.participants_count = 0;
                tLRPC$GroupCall.version = 1;
                tLRPC$GroupCall.can_change_join_muted = true;
                call2.chatId = this.chat.id;
                call2.currentAccount = AccountInstance.getInstance(this.currentAccount);
                this.groupCall.setSelfPeer(this.groupCallPeer);
                dispatchStateChanged(6);
                TLRPC$TL_phone_createGroupCall tLRPC$TL_phone_createGroupCall = new TLRPC$TL_phone_createGroupCall();
                tLRPC$TL_phone_createGroupCall.peer = MessagesController.getInputPeer(this.chat);
                tLRPC$TL_phone_createGroupCall.random_id = Utilities.random.nextInt();
                int i3 = this.scheduleDate;
                if (i3 != 0) {
                    tLRPC$TL_phone_createGroupCall.schedule_date = i3;
                    tLRPC$TL_phone_createGroupCall.flags |= 2;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_createGroupCall, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        VoIPService.this.lambda$startGroupCall$28$VoIPService(tLObject, tLRPC$TL_error);
                    }
                }, 2);
                this.createGroupCall = false;
            } else if (str == null) {
                if (this.groupCall == null) {
                    ChatObject.Call groupCall = MessagesController.getInstance(this.currentAccount).getGroupCall(this.chat.id, false);
                    this.groupCall = groupCall;
                    if (groupCall != null) {
                        groupCall.setSelfPeer(this.groupCallPeer);
                    }
                }
                configureDeviceForCall();
                showNotification();
                AndroidUtilities.runOnUIThread($$Lambda$VoIPService$jeLp4qadaQlaTEPMEZddhttGjk.INSTANCE);
                createGroupInstance(false);
            } else if (getSharedInstance() != null && this.groupCall != null) {
                dispatchStateChanged(1);
                this.mySource = i;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("initital source = " + this.mySource);
                }
                this.myJson = str;
                TLRPC$TL_phone_joinGroupCall tLRPC$TL_phone_joinGroupCall = new TLRPC$TL_phone_joinGroupCall();
                tLRPC$TL_phone_joinGroupCall.muted = true;
                tLRPC$TL_phone_joinGroupCall.call = this.groupCall.getInputGroupCall();
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
                tLRPC$TL_phone_joinGroupCall.params = tLRPC$TL_dataJSON;
                tLRPC$TL_dataJSON.data = str;
                if (!TextUtils.isEmpty(this.joinHash)) {
                    tLRPC$TL_phone_joinGroupCall.invite_hash = this.joinHash;
                    tLRPC$TL_phone_joinGroupCall.flags |= 2;
                }
                TLRPC$InputPeer tLRPC$InputPeer = this.groupCallPeer;
                if (tLRPC$InputPeer != null) {
                    tLRPC$TL_phone_joinGroupCall.join_as = tLRPC$InputPeer;
                } else {
                    TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
                    tLRPC$TL_phone_joinGroupCall.join_as = tLRPC$TL_inputPeerUser;
                    tLRPC$TL_inputPeerUser.user_id = AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientUserId();
                }
                ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
                $$Lambda$VoIPService$81k8c0RsX2fcUh5Bq_AS2RLN5ts r0 = new RequestDelegate(z) {
                    public final /* synthetic */ boolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        VoIPService.this.lambda$startGroupCall$32$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
                    }
                };
                if (!BuildVars.DEBUG_PRIVATE_VERSION) {
                    i2 = 0;
                }
                instance.sendRequest(tLRPC$TL_phone_joinGroupCall, r0, i2);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCall$28 */
    public /* synthetic */ void lambda$startGroupCall$28$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            int i = 0;
            while (true) {
                if (i >= tLRPC$Updates.updates.size()) {
                    break;
                }
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateGroupCall) {
                    AndroidUtilities.runOnUIThread(new Runnable((TLRPC$TL_updateGroupCall) tLRPC$Update) {
                        public final /* synthetic */ TLRPC$TL_updateGroupCall f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            VoIPService.this.lambda$null$26$VoIPService(this.f$1);
                        }
                    });
                    break;
                }
                i++;
            }
            MessagesController.getInstance(this.currentAccount).processUpdates(tLRPC$Updates, false);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPService.this.lambda$null$27$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$26 */
    public /* synthetic */ void lambda$null$26$VoIPService(TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall) {
        if (VoIPBaseService.sharedInstance != null) {
            TLRPC$GroupCall tLRPC$GroupCall = this.groupCall.call;
            TLRPC$GroupCall tLRPC$GroupCall2 = tLRPC$TL_updateGroupCall.call;
            tLRPC$GroupCall.access_hash = tLRPC$GroupCall2.access_hash;
            tLRPC$GroupCall.id = tLRPC$GroupCall2.id;
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            ChatObject.Call call = this.groupCall;
            instance.putGroupCall(call.chatId, call);
            startGroupCall(0, (String) null, false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$27 */
    public /* synthetic */ void lambda$null$27$VoIPService(TLRPC$TL_error tLRPC$TL_error) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, tLRPC$TL_error.text);
        hangUp(0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCall$32 */
    public /* synthetic */ void lambda$startGroupCall$32$VoIPService(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            int selfId = getSelfId();
            int size = tLRPC$Updates.updates.size();
            int i = 0;
            while (i < size) {
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateGroupCallParticipants) {
                    TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants = (TLRPC$TL_updateGroupCallParticipants) tLRPC$Update;
                    int size2 = tLRPC$TL_updateGroupCallParticipants.participants.size();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= size2) {
                            break;
                        }
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_updateGroupCallParticipants.participants.get(i2);
                        if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == selfId) {
                            this.mySource = tLRPC$TL_groupCallParticipant.source;
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("join source = " + this.mySource);
                            }
                            i = size;
                        } else {
                            i2++;
                        }
                    }
                }
                i++;
            }
            MessagesController.getInstance(this.currentAccount).processUpdates(tLRPC$Updates, false);
            AndroidUtilities.runOnUIThread(new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VoIPService.this.lambda$null$30$VoIPService(this.f$1);
                }
            });
            startGroupCheckShortpoll();
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPService.this.lambda$null$31$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$30 */
    public /* synthetic */ void lambda$null$30$VoIPService(boolean z) {
        this.groupCall.loadMembers(z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$31 */
    public /* synthetic */ void lambda$null$31$VoIPService(TLRPC$TL_error tLRPC$TL_error) {
        if ("JOIN_AS_PEER_INVALID".equals(tLRPC$TL_error.text)) {
            TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chat.id);
            if (chatFull != null) {
                if (chatFull instanceof TLRPC$TL_chatFull) {
                    chatFull.flags &= -32769;
                } else {
                    chatFull.flags &= -67108865;
                }
                chatFull.groupcall_default_join_as = null;
                JoinCallAlert.resetCache();
            }
            hangUp(2);
        } else if ("GROUPCALL_SSRC_DUPLICATE_MUCH".equals(tLRPC$TL_error.text)) {
            createGroupInstance(false);
        } else {
            if ("GROUPCALL_INVALID".equals(tLRPC$TL_error.text)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat.id, 0, true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, tLRPC$TL_error.text);
            hangUp(0);
        }
    }

    private void startGroupCheckShortpoll() {
        if (this.shortPollRunnable == null && VoIPBaseService.sharedInstance != null && this.groupCall != null && this.mySource != 0) {
            $$Lambda$VoIPService$Whw_70T_B58Kxo9daP564M0r9s r0 = new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$startGroupCheckShortpoll$35$VoIPService();
                }
            };
            this.shortPollRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 4000);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCheckShortpoll$35 */
    public /* synthetic */ void lambda$startGroupCheckShortpoll$35$VoIPService() {
        if (this.shortPollRunnable != null && VoIPBaseService.sharedInstance != null && this.groupCall != null && this.mySource != 0) {
            TLRPC$TL_phone_checkGroupCall tLRPC$TL_phone_checkGroupCall = new TLRPC$TL_phone_checkGroupCall();
            tLRPC$TL_phone_checkGroupCall.call = this.groupCall.getInputGroupCall();
            tLRPC$TL_phone_checkGroupCall.source = this.mySource;
            this.checkRequestId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_checkGroupCall, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$null$34$VoIPService(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$34 */
    public /* synthetic */ void lambda$null$34$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$TL_error) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPService.this.lambda$null$33$VoIPService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$33 */
    public /* synthetic */ void lambda$null$33$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (this.shortPollRunnable != null && VoIPBaseService.sharedInstance != null && this.groupCall != null) {
            this.shortPollRunnable = null;
            this.checkRequestId = 0;
            if ((tLObject instanceof TLRPC$TL_boolFalse) || (tLRPC$TL_error != null && tLRPC$TL_error.code == 400)) {
                createGroupInstance(false);
            } else {
                startGroupCheckShortpoll();
            }
        }
    }

    private void cancelGroupCheckShortPoll() {
        if (this.checkRequestId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkRequestId, false);
            this.checkRequestId = 0;
        }
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.shortPollRunnable = null;
        }
    }

    private void broadcastUnknownParticipants(int[] iArr, ArrayList<Integer> arrayList) {
        int i;
        if (this.groupCall != null && this.tgVoip != null) {
            int selfId = getSelfId();
            int length = iArr != null ? iArr.length : arrayList.size();
            ArrayList arrayList2 = null;
            for (int i2 = 0; i2 < length; i2++) {
                if (iArr != null) {
                    i = iArr[i2];
                } else {
                    i = arrayList.get(i2).intValue();
                }
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.groupCall.participantsBySources.get(i);
                if (!(tLRPC$TL_groupCallParticipant == null || MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == selfId || tLRPC$TL_groupCallParticipant.source == 0)) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(tLRPC$TL_groupCallParticipant);
                }
            }
            if (arrayList2 != null) {
                String[] strArr = new String[arrayList2.size()];
                int[] iArr2 = new int[arrayList2.size()];
                int size = arrayList2.size();
                for (int i3 = 0; i3 < size; i3++) {
                    strArr[i3] = null;
                    iArr2[i3] = ((TLRPC$TL_groupCallParticipant) arrayList2.get(i3)).source;
                }
                this.tgVoip.addParticipants(iArr2, strArr);
                int size2 = arrayList2.size();
                for (int i4 = 0; i4 < size2; i4++) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = (TLRPC$TL_groupCallParticipant) arrayList2.get(i4);
                    if (tLRPC$TL_groupCallParticipant2.muted_by_you) {
                        this.tgVoip.setVolume(tLRPC$TL_groupCallParticipant2.source, 0.0d);
                    } else {
                        NativeInstance nativeInstance = this.tgVoip;
                        int i5 = tLRPC$TL_groupCallParticipant2.source;
                        double participantVolume = (double) ChatObject.getParticipantVolume(tLRPC$TL_groupCallParticipant2);
                        Double.isNaN(participantVolume);
                        nativeInstance.setVolume(i5, participantVolume / 10000.0d);
                    }
                }
            }
        }
    }

    private void createGroupInstance(boolean z) {
        String str;
        cancelGroupCheckShortPoll();
        this.wasConnected = false;
        if (z) {
            this.mySource = 0;
            this.tgVoip.stopGroup();
            this.tgVoip = null;
        }
        if (this.tgVoip == null) {
            if (BuildVars.DEBUG_VERSION) {
                str = VoIPHelper.getLogFilePath("voip" + this.groupCall.call.id);
            } else {
                str = VoIPHelper.getLogFilePath(this.groupCall.call.id, false);
            }
            NativeInstance makeGroup = NativeInstance.makeGroup(str, new NativeInstance.PayloadCallback() {
                public final void run(int i, String str) {
                    VoIPService.this.lambda$createGroupInstance$36$VoIPService(i, str);
                }
            }, new NativeInstance.AudioLevelsCallback() {
                public final void run(int[] iArr, float[] fArr, boolean[] zArr) {
                    VoIPService.this.lambda$createGroupInstance$38$VoIPService(iArr, fArr, zArr);
                }
            }, $$Lambda$VoIPService$hzph7rEeLW5Ts99cqyvrS7EUVI.INSTANCE, new NativeInstance.VideoSourcesCallback() {
                public final void run(int[] iArr) {
                    VoIPService.this.lambda$createGroupInstance$41$VoIPService(iArr);
                }
            }, new NativeInstance.RequestBroadcastPartCallback() {
                public final void run(long j, long j2) {
                    VoIPService.this.lambda$createGroupInstance$44$VoIPService(j, j2);
                }
            }, new NativeInstance.RequestBroadcastPartCallback() {
                public final void run(long j, long j2) {
                    VoIPService.this.lambda$createGroupInstance$45$VoIPService(j, j2);
                }
            });
            this.tgVoip = makeGroup;
            makeGroup.setOnStateUpdatedListener(new Instance.OnStateUpdatedListener() {
                public final void onStateUpdated(int i, boolean z) {
                    VoIPService.this.updateConnectionState(i, z);
                }
            });
        }
        this.tgVoip.resetGroupInstance(false);
        dispatchStateChanged(1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$36 */
    public /* synthetic */ void lambda$createGroupInstance$36$VoIPService(int i, String str) {
        startGroupCall(i, str, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$38 */
    public /* synthetic */ void lambda$createGroupInstance$38$VoIPService(int[] iArr, float[] fArr, boolean[] zArr) {
        ChatObject.Call call;
        if (VoIPBaseService.sharedInstance != null && (call = this.groupCall) != null) {
            call.processVoiceLevelsUpdate(iArr, fArr, zArr);
            float f = 0.0f;
            boolean z = false;
            for (int i = 0; i < iArr.length; i++) {
                if (iArr[i] == 0) {
                    if (this.lastTypingTimeSend < SystemClock.uptimeMillis() - 5000 && fArr[i] > 0.1f && zArr[i]) {
                        this.lastTypingTimeSend = SystemClock.uptimeMillis();
                        TLRPC$TL_messages_setTyping tLRPC$TL_messages_setTyping = new TLRPC$TL_messages_setTyping();
                        tLRPC$TL_messages_setTyping.action = new TLRPC$TL_speakingInGroupCallAction();
                        tLRPC$TL_messages_setTyping.peer = MessagesController.getInputPeer(this.chat);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_setTyping, $$Lambda$VoIPService$QjsP_3U3l3qkms3Y23vfbVmrY8.INSTANCE);
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(fArr[i]));
                } else {
                    f = Math.max(f, fArr[i]);
                    z = true;
                }
            }
            if (z) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcSpeakerAmplitudeEvent, Float.valueOf(f));
                NativeInstance.AudioLevelsCallback audioLevelsCallback2 = audioLevelsCallback;
                if (audioLevelsCallback2 != null) {
                    audioLevelsCallback2.run(iArr, fArr, zArr);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$41 */
    public /* synthetic */ void lambda$createGroupInstance$41$VoIPService(int[] iArr) {
        ChatObject.Call call;
        if (VoIPBaseService.sharedInstance != null && (call = this.groupCall) != null) {
            call.processUnknownVideoParticipants(iArr, new ChatObject.Call.OnParticipantsLoad() {
                public final void onLoad(ArrayList arrayList) {
                    VoIPService.this.lambda$null$40$VoIPService(arrayList);
                }
            });
            broadcastUnknownParticipants(iArr, (ArrayList<Integer>) null);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$40 */
    public /* synthetic */ void lambda$null$40$VoIPService(ArrayList arrayList) {
        if (VoIPBaseService.sharedInstance != null && this.groupCall != null) {
            broadcastUnknownParticipants((int[]) null, arrayList);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$44 */
    public /* synthetic */ void lambda$createGroupInstance$44$VoIPService(long j, long j2) {
        TLRPC$TL_upload_getFile tLRPC$TL_upload_getFile = new TLRPC$TL_upload_getFile();
        tLRPC$TL_upload_getFile.limit = 131072;
        TLRPC$TL_inputGroupCallStream tLRPC$TL_inputGroupCallStream = new TLRPC$TL_inputGroupCallStream();
        tLRPC$TL_inputGroupCallStream.call = this.groupCall.getInputGroupCall();
        tLRPC$TL_inputGroupCallStream.time_ms = j;
        if (j2 == 500) {
            tLRPC$TL_inputGroupCallStream.scale = 1;
        }
        tLRPC$TL_upload_getFile.location = tLRPC$TL_inputGroupCallStream;
        this.currentStreamRequestTimestamp = j;
        this.currentStreamRequestId = AccountInstance.getInstance(this.currentAccount).getConnectionsManager().sendRequest(tLRPC$TL_upload_getFile, new RequestDelegateTimestamp(j) {
            public final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, long j) {
                VoIPService.this.lambda$null$43$VoIPService(this.f$1, tLObject, tLRPC$TL_error, j);
            }
        }, 2, 2, this.groupCall.call.stream_dc_id);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$43 */
    public /* synthetic */ void lambda$null$43$VoIPService(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, long j2) {
        NativeInstance nativeInstance = this.tgVoip;
        if (nativeInstance != null) {
            if (tLObject != null) {
                NativeByteBuffer nativeByteBuffer = ((TLRPC$TL_upload_file) tLObject).bytes;
                nativeInstance.onStreamPartAvailable(j, nativeByteBuffer.buffer, nativeByteBuffer.limit(), j2);
            } else if ("GROUPCALL_JOIN_MISSING".equals(tLRPC$TL_error.text)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$null$42$VoIPService();
                    }
                });
            } else {
                this.tgVoip.onStreamPartAvailable(j, (ByteBuffer) null, ("TIME_TOO_BIG".equals(tLRPC$TL_error.text) || tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) ? 0 : -1, j2);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$42 */
    public /* synthetic */ void lambda$null$42$VoIPService() {
        createGroupInstance(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$45 */
    public /* synthetic */ void lambda$createGroupInstance$45$VoIPService(long j, long j2) {
        if (this.currentStreamRequestTimestamp == j) {
            AccountInstance.getInstance(this.currentAccount).getConnectionsManager().cancelRequest(this.currentStreamRequestId, true);
            this.currentStreamRequestId = 0;
        }
    }

    /* access modifiers changed from: private */
    public void updateConnectionState(int i, boolean z) {
        dispatchStateChanged((i == 1 || this.switchingStream) ? 3 : 5);
        if (this.switchingStream && (i == 0 || (i == 1 && z))) {
            $$Lambda$VoIPService$zJg35vAnE3jbe6FzRXwKpV4OhU r1 = new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$updateConnectionState$46$VoIPService();
                }
            };
            this.switchingStreamTimeoutRunnable = r1;
            AndroidUtilities.runOnUIThread(r1, 3000);
        }
        if (i == 0) {
            startGroupCheckShortpoll();
            if (this.playedConnectedSound && this.spPlayId == 0 && !this.switchingStream) {
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$updateConnectionState$47$VoIPService();
                    }
                });
                return;
            }
            return;
        }
        cancelGroupCheckShortPoll();
        if (!z) {
            this.switchingStream = false;
        }
        Runnable runnable = this.switchingStreamTimeoutRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.switchingStreamTimeoutRunnable = null;
        }
        if (this.playedConnectedSound) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$updateConnectionState$48$VoIPService();
                }
            });
            Runnable runnable2 = this.connectingSoundRunnable;
            if (runnable2 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable2);
                this.connectingSoundRunnable = null;
            }
        } else {
            playConnectedSound();
        }
        if (!this.wasConnected) {
            this.wasConnected = true;
            NativeInstance nativeInstance = this.tgVoip;
            if (nativeInstance != null && !this.micMute) {
                nativeInstance.setMuteMicrophone(false);
            }
            setParticipantsVolume();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateConnectionState$46 */
    public /* synthetic */ void lambda$updateConnectionState$46$VoIPService() {
        if (this.switchingStreamTimeoutRunnable != null) {
            this.switchingStream = false;
            updateConnectionState(0, true);
            this.switchingStreamTimeoutRunnable = null;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateConnectionState$47 */
    public /* synthetic */ void lambda$updateConnectionState$47$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.spVoiceChatConnecting, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateConnectionState$48 */
    public /* synthetic */ void lambda$updateConnectionState$48$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    public void setParticipantsVolume() {
        int i;
        NativeInstance nativeInstance = this.tgVoip;
        if (nativeInstance != null) {
            int size = this.groupCall.participants.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$TL_groupCallParticipant valueAt = this.groupCall.participants.valueAt(i2);
                if (!valueAt.self && (i = valueAt.source) != 0 && (valueAt.can_self_unmute || !valueAt.muted)) {
                    if (valueAt.muted_by_you) {
                        nativeInstance.setVolume(i, 0.0d);
                    } else {
                        double participantVolume = (double) ChatObject.getParticipantVolume(valueAt);
                        Double.isNaN(participantVolume);
                        nativeInstance.setVolume(i, participantVolume / 10000.0d);
                    }
                }
            }
        }
    }

    public void setParticipantVolume(int i, int i2) {
        NativeInstance nativeInstance = this.tgVoip;
        double d = (double) i2;
        Double.isNaN(d);
        nativeInstance.setVolume(i, d / 10000.0d);
    }

    public boolean isSwitchingStream() {
        return this.switchingStream;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r5.remove();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x009f */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0107 A[Catch:{ Exception -> 0x02da }] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0115 A[Catch:{ Exception -> 0x02da }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0130 A[Catch:{ Exception -> 0x02da }] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0170 A[Catch:{ Exception -> 0x02da }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x017e A[Catch:{ Exception -> 0x02da }, LOOP:2: B:69:0x017c->B:70:0x017e, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01cf A[Catch:{ Exception -> 0x02da }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0240 A[Catch:{ Exception -> 0x02da }] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0242 A[Catch:{ Exception -> 0x02da }] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x02b2 A[Catch:{ Exception -> 0x02da }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initiateActualEncryptedCall() {
        /*
            r37 = this;
            r1 = r37
            java.lang.String r0 = "calls_access_hashes"
            java.lang.String r2 = " "
            java.lang.Runnable r3 = r1.timeoutRunnable
            r4 = 0
            if (r3 == 0) goto L_0x0010
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r3)
            r1.timeoutRunnable = r4
        L_0x0010:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02da }
            if (r3 == 0) goto L_0x002a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02da }
            r3.<init>()     // Catch:{ Exception -> 0x02da }
            java.lang.String r5 = "InitCall: keyID="
            r3.append(r5)     // Catch:{ Exception -> 0x02da }
            long r5 = r1.keyFingerprint     // Catch:{ Exception -> 0x02da }
            r3.append(r5)     // Catch:{ Exception -> 0x02da }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x02da }
        L_0x002a:
            int r3 = r1.currentAccount     // Catch:{ Exception -> 0x02da }
            android.content.SharedPreferences r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3)     // Catch:{ Exception -> 0x02da }
            java.util.Set r5 = r3.getStringSet(r0, r4)     // Catch:{ Exception -> 0x02da }
            if (r5 == 0) goto L_0x003c
            java.util.HashSet r6 = new java.util.HashSet     // Catch:{ Exception -> 0x02da }
            r6.<init>(r5)     // Catch:{ Exception -> 0x02da }
            goto L_0x0041
        L_0x003c:
            java.util.HashSet r6 = new java.util.HashSet     // Catch:{ Exception -> 0x02da }
            r6.<init>()     // Catch:{ Exception -> 0x02da }
        L_0x0041:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02da }
            r5.<init>()     // Catch:{ Exception -> 0x02da }
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r1.privateCall     // Catch:{ Exception -> 0x02da }
            long r7 = r7.id     // Catch:{ Exception -> 0x02da }
            r5.append(r7)     // Catch:{ Exception -> 0x02da }
            r5.append(r2)     // Catch:{ Exception -> 0x02da }
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r1.privateCall     // Catch:{ Exception -> 0x02da }
            long r7 = r7.access_hash     // Catch:{ Exception -> 0x02da }
            r5.append(r7)     // Catch:{ Exception -> 0x02da }
            r5.append(r2)     // Catch:{ Exception -> 0x02da }
            long r7 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x02da }
            r5.append(r7)     // Catch:{ Exception -> 0x02da }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x02da }
            r6.add(r5)     // Catch:{ Exception -> 0x02da }
        L_0x0068:
            int r5 = r6.size()     // Catch:{ Exception -> 0x02da }
            r7 = 20
            r8 = 2
            if (r5 <= r7) goto L_0x00a9
            r9 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            java.util.Iterator r5 = r6.iterator()     // Catch:{ Exception -> 0x02da }
            r7 = r4
        L_0x007b:
            boolean r11 = r5.hasNext()     // Catch:{ Exception -> 0x02da }
            if (r11 == 0) goto L_0x00a3
            java.lang.Object r11 = r5.next()     // Catch:{ Exception -> 0x02da }
            java.lang.String r11 = (java.lang.String) r11     // Catch:{ Exception -> 0x02da }
            java.lang.String[] r12 = r11.split(r2)     // Catch:{ Exception -> 0x02da }
            int r13 = r12.length     // Catch:{ Exception -> 0x02da }
            if (r13 >= r8) goto L_0x0092
            r5.remove()     // Catch:{ Exception -> 0x02da }
            goto L_0x007b
        L_0x0092:
            r12 = r12[r8]     // Catch:{ Exception -> 0x009f }
            long r12 = java.lang.Long.parseLong(r12)     // Catch:{ Exception -> 0x009f }
            int r14 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
            if (r14 >= 0) goto L_0x007b
            r7 = r11
            r9 = r12
            goto L_0x007b
        L_0x009f:
            r5.remove()     // Catch:{ Exception -> 0x02da }
            goto L_0x007b
        L_0x00a3:
            if (r7 == 0) goto L_0x0068
            r6.remove(r7)     // Catch:{ Exception -> 0x02da }
            goto L_0x0068
        L_0x00a9:
            android.content.SharedPreferences$Editor r2 = r3.edit()     // Catch:{ Exception -> 0x02da }
            android.content.SharedPreferences$Editor r0 = r2.putStringSet(r0, r6)     // Catch:{ Exception -> 0x02da }
            r0.commit()     // Catch:{ Exception -> 0x02da }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02da }
            r2 = 16
            r3 = 0
            if (r0 < r2) goto L_0x00c6
            boolean r0 = android.media.audiofx.AcousticEchoCanceler.isAvailable()     // Catch:{ Exception -> 0x00c0 }
            goto L_0x00c1
        L_0x00c0:
            r0 = 0
        L_0x00c1:
            boolean r2 = android.media.audiofx.NoiseSuppressor.isAvailable()     // Catch:{ Exception -> 0x00c7 }
            goto L_0x00c8
        L_0x00c6:
            r0 = 0
        L_0x00c7:
            r2 = 0
        L_0x00c8:
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x02da }
            int r6 = r1.currentAccount     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)     // Catch:{ Exception -> 0x02da }
            int r7 = r6.callConnectTimeout     // Catch:{ Exception -> 0x02da }
            double r9 = (double) r7
            r11 = 4652007308841189376(0x408fNUM, double:1000.0)
            java.lang.Double.isNaN(r9)
            double r14 = r9 / r11
            int r6 = r6.callPacketTimeout     // Catch:{ Exception -> 0x02da }
            double r6 = (double) r6
            java.lang.Double.isNaN(r6)
            double r16 = r6 / r11
            java.lang.String r6 = "VoipDataSaving"
            int r7 = org.telegram.ui.Components.voip.VoIPHelper.getDataSavingDefault()     // Catch:{ Exception -> 0x02da }
            int r6 = r5.getInt(r6, r7)     // Catch:{ Exception -> 0x02da }
            int r18 = r1.convertDataSavingMode(r6)     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.Instance$ServerConfig r6 = org.telegram.messenger.voip.Instance.getGlobalServerConfig()     // Catch:{ Exception -> 0x02da }
            if (r0 == 0) goto L_0x0103
            boolean r0 = r6.useSystemAec     // Catch:{ Exception -> 0x02da }
            if (r0 != 0) goto L_0x0100
            goto L_0x0103
        L_0x0100:
            r20 = 0
            goto L_0x0105
        L_0x0103:
            r20 = 1
        L_0x0105:
            if (r2 == 0) goto L_0x010f
            boolean r0 = r6.useSystemNs     // Catch:{ Exception -> 0x02da }
            if (r0 != 0) goto L_0x010c
            goto L_0x010f
        L_0x010c:
            r21 = 0
            goto L_0x0111
        L_0x010f:
            r21 = 1
        L_0x0111:
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x02da }
            if (r0 == 0) goto L_0x0130
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02da }
            r0.<init>()     // Catch:{ Exception -> 0x02da }
            java.lang.String r2 = "voip"
            r0.append(r2)     // Catch:{ Exception -> 0x02da }
            org.telegram.tgnet.TLRPC$PhoneCall r2 = r1.privateCall     // Catch:{ Exception -> 0x02da }
            long r9 = r2.id     // Catch:{ Exception -> 0x02da }
            r0.append(r9)     // Catch:{ Exception -> 0x02da }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x02da }
            java.lang.String r0 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath(r0)     // Catch:{ Exception -> 0x02da }
            goto L_0x0138
        L_0x0130:
            org.telegram.tgnet.TLRPC$PhoneCall r0 = r1.privateCall     // Catch:{ Exception -> 0x02da }
            long r9 = r0.id     // Catch:{ Exception -> 0x02da }
            java.lang.String r0 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath(r9, r3)     // Catch:{ Exception -> 0x02da }
        L_0x0138:
            r25 = r0
            org.telegram.messenger.voip.Instance$Config r0 = new org.telegram.messenger.voip.Instance$Config     // Catch:{ Exception -> 0x02da }
            org.telegram.tgnet.TLRPC$PhoneCall r2 = r1.privateCall     // Catch:{ Exception -> 0x02da }
            boolean r9 = r2.p2p_allowed     // Catch:{ Exception -> 0x02da }
            r22 = 1
            r23 = 0
            boolean r6 = r6.enableStunMarking     // Catch:{ Exception -> 0x02da }
            java.lang.String r26 = ""
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r2 = r2.protocol     // Catch:{ Exception -> 0x02da }
            int r2 = r2.max_layer     // Catch:{ Exception -> 0x02da }
            r13 = r0
            r19 = r9
            r24 = r6
            r27 = r2
            r13.<init>(r14, r16, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ Exception -> 0x02da }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x02da }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02da }
            java.io.File r6 = r6.getFilesDir()     // Catch:{ Exception -> 0x02da }
            java.lang.String r9 = "voip_persistent_state.json"
            r2.<init>(r6, r9)     // Catch:{ Exception -> 0x02da }
            java.lang.String r28 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x02da }
            java.lang.String r2 = "dbg_force_tcp_in_calls"
            boolean r2 = r5.getBoolean(r2, r3)     // Catch:{ Exception -> 0x02da }
            if (r2 == 0) goto L_0x0171
            r8 = 3
        L_0x0171:
            org.telegram.tgnet.TLRPC$PhoneCall r6 = r1.privateCall     // Catch:{ Exception -> 0x02da }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneConnection> r6 = r6.connections     // Catch:{ Exception -> 0x02da }
            int r6 = r6.size()     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.Instance$Endpoint[] r15 = new org.telegram.messenger.voip.Instance.Endpoint[r6]     // Catch:{ Exception -> 0x02da }
            r14 = 0
        L_0x017c:
            if (r14 >= r6) goto L_0x01c8
            org.telegram.tgnet.TLRPC$PhoneCall r9 = r1.privateCall     // Catch:{ Exception -> 0x02da }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneConnection> r9 = r9.connections     // Catch:{ Exception -> 0x02da }
            java.lang.Object r9 = r9.get(r14)     // Catch:{ Exception -> 0x02da }
            org.telegram.tgnet.TLRPC$PhoneConnection r9 = (org.telegram.tgnet.TLRPC$PhoneConnection) r9     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.Instance$Endpoint r22 = new org.telegram.messenger.voip.Instance$Endpoint     // Catch:{ Exception -> 0x02da }
            boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$TL_phoneConnectionWebrtc     // Catch:{ Exception -> 0x02da }
            long r11 = r9.id     // Catch:{ Exception -> 0x02da }
            java.lang.String r13 = r9.ip     // Catch:{ Exception -> 0x02da }
            java.lang.String r7 = r9.ipv6     // Catch:{ Exception -> 0x02da }
            int r4 = r9.port     // Catch:{ Exception -> 0x02da }
            byte[] r3 = r9.peer_tag     // Catch:{ Exception -> 0x02da }
            r26 = r6
            boolean r6 = r9.turn     // Catch:{ Exception -> 0x02da }
            r27 = r0
            boolean r0 = r9.stun     // Catch:{ Exception -> 0x02da }
            r29 = r5
            java.lang.String r5 = r9.username     // Catch:{ Exception -> 0x02da }
            java.lang.String r9 = r9.password     // Catch:{ Exception -> 0x02da }
            r21 = r9
            r9 = r22
            r30 = r14
            r14 = r7
            r7 = r15
            r15 = r4
            r16 = r8
            r17 = r3
            r18 = r6
            r19 = r0
            r20 = r5
            r9.<init>(r10, r11, r13, r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ Exception -> 0x02da }
            r7[r30] = r22     // Catch:{ Exception -> 0x02da }
            int r14 = r30 + 1
            r15 = r7
            r6 = r26
            r0 = r27
            r5 = r29
            r3 = 0
            r4 = 0
            goto L_0x017c
        L_0x01c8:
            r27 = r0
            r29 = r5
            r7 = r15
            if (r2 == 0) goto L_0x01d7
            org.telegram.messenger.voip.-$$Lambda$VoIPService$1dgM1D1Nvar_uAr5TFLWXcmdU7Dk r0 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$1dgM1D1Nvar_uAr5TFLWXcmdU7Dk     // Catch:{ Exception -> 0x02da }
            r0.<init>()     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x02da }
        L_0x01d7:
            java.lang.String r0 = "proxy_enabled"
            r2 = r29
            r3 = 0
            boolean r0 = r2.getBoolean(r0, r3)     // Catch:{ Exception -> 0x02da }
            if (r0 == 0) goto L_0x021f
            java.lang.String r0 = "proxy_enabled_calls"
            boolean r0 = r2.getBoolean(r0, r3)     // Catch:{ Exception -> 0x02da }
            if (r0 == 0) goto L_0x021f
            java.lang.String r0 = "proxy_ip"
            r3 = 0
            java.lang.String r0 = r2.getString(r0, r3)     // Catch:{ Exception -> 0x02da }
            java.lang.String r4 = "proxy_secret"
            java.lang.String r4 = r2.getString(r4, r3)     // Catch:{ Exception -> 0x02da }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x02da }
            if (r3 != 0) goto L_0x021f
            boolean r3 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x02da }
            if (r3 == 0) goto L_0x021f
            org.telegram.messenger.voip.Instance$Proxy r3 = new org.telegram.messenger.voip.Instance$Proxy     // Catch:{ Exception -> 0x02da }
            java.lang.String r4 = "proxy_port"
            r5 = 0
            int r4 = r2.getInt(r4, r5)     // Catch:{ Exception -> 0x02da }
            java.lang.String r5 = "proxy_user"
            r6 = 0
            java.lang.String r5 = r2.getString(r5, r6)     // Catch:{ Exception -> 0x02da }
            java.lang.String r8 = "proxy_pass"
            java.lang.String r2 = r2.getString(r8, r6)     // Catch:{ Exception -> 0x02da }
            r3.<init>(r0, r4, r5, r2)     // Catch:{ Exception -> 0x02da }
            r30 = r3
            goto L_0x0222
        L_0x021f:
            r6 = 0
            r30 = r6
        L_0x0222:
            org.telegram.messenger.voip.Instance$EncryptionKey r0 = new org.telegram.messenger.voip.Instance$EncryptionKey     // Catch:{ Exception -> 0x02da }
            byte[] r2 = r1.authKey     // Catch:{ Exception -> 0x02da }
            boolean r3 = r1.isOutgoing     // Catch:{ Exception -> 0x02da }
            r0.<init>(r2, r3)     // Catch:{ Exception -> 0x02da }
            java.lang.String r2 = "2.7.7"
            org.telegram.tgnet.TLRPC$PhoneCall r3 = r1.privateCall     // Catch:{ Exception -> 0x02da }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r3 = r3.protocol     // Catch:{ Exception -> 0x02da }
            java.util.ArrayList<java.lang.String> r3 = r3.library_versions     // Catch:{ Exception -> 0x02da }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x02da }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x02da }
            int r2 = r2.compareTo(r3)     // Catch:{ Exception -> 0x02da }
            if (r2 > 0) goto L_0x0242
            r2 = 1
            goto L_0x0243
        L_0x0242:
            r2 = 0
        L_0x0243:
            long r3 = r1.videoCapturer     // Catch:{ Exception -> 0x02da }
            r5 = 0
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 == 0) goto L_0x0255
            if (r2 != 0) goto L_0x0255
            org.telegram.messenger.voip.NativeInstance.destroyVideoCapturer(r3)     // Catch:{ Exception -> 0x02da }
            r1.videoCapturer = r5     // Catch:{ Exception -> 0x02da }
            r3 = 0
            r1.videoState = r3     // Catch:{ Exception -> 0x02da }
        L_0x0255:
            org.telegram.tgnet.TLRPC$PhoneCall r3 = r1.privateCall     // Catch:{ Exception -> 0x02da }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r3 = r3.protocol     // Catch:{ Exception -> 0x02da }
            java.util.ArrayList<java.lang.String> r3 = r3.library_versions     // Catch:{ Exception -> 0x02da }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x02da }
            r26 = r3
            java.lang.String r26 = (java.lang.String) r26     // Catch:{ Exception -> 0x02da }
            int r31 = r37.getNetworkType()     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink r3 = r1.remoteSink     // Catch:{ Exception -> 0x02da }
            long r8 = r1.videoCapturer     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.-$$Lambda$VoIPService$6lUwD0L0NvpU2i9IXIFH9oCLASSNAMEws r10 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$6lUwD0L0NvpU2i9IXIFH9oCLASSNAMEws     // Catch:{ Exception -> 0x02da }
            r10.<init>()     // Catch:{ Exception -> 0x02da }
            r29 = r7
            r32 = r0
            r33 = r3
            r34 = r8
            r36 = r10
            org.telegram.messenger.voip.NativeInstance r0 = org.telegram.messenger.voip.Instance.makeInstance(r26, r27, r28, r29, r30, r31, r32, r33, r34, r36)     // Catch:{ Exception -> 0x02da }
            r1.tgVoip = r0     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.-$$Lambda$sjy6PiJMA8_4lfe9Cli5v92zzh8 r3 = new org.telegram.messenger.voip.-$$Lambda$sjy6PiJMA8_4lfe9Cli5v92zzh8     // Catch:{ Exception -> 0x02da }
            r3.<init>()     // Catch:{ Exception -> 0x02da }
            r0.setOnStateUpdatedListener(r3)     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.NativeInstance r0 = r1.tgVoip     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.-$$Lambda$nq07hMB6FzcXz1SuNu8qNNOnIFw r3 = new org.telegram.messenger.voip.-$$Lambda$nq07hMB6FzcXz1SuNu8qNNOnIFw     // Catch:{ Exception -> 0x02da }
            r3.<init>()     // Catch:{ Exception -> 0x02da }
            r0.setOnSignalBarsUpdatedListener(r3)     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.NativeInstance r0 = r1.tgVoip     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.-$$Lambda$HNfvWF9Xn3-zdFJ1-nzWv-_4Tj8 r3 = new org.telegram.messenger.voip.-$$Lambda$HNfvWF9Xn3-zdFJ1-nzWv-_4Tj8     // Catch:{ Exception -> 0x02da }
            r3.<init>()     // Catch:{ Exception -> 0x02da }
            r0.setOnSignalDataListener(r3)     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.NativeInstance r0 = r1.tgVoip     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.-$$Lambda$HTKGoi8h-oyhhoKW5NwxR_c0PRM r3 = new org.telegram.messenger.voip.-$$Lambda$HTKGoi8h-oyhhoKW5NwxR_c0PRM     // Catch:{ Exception -> 0x02da }
            r3.<init>()     // Catch:{ Exception -> 0x02da }
            r0.setOnRemoteMediaStateUpdatedListener(r3)     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.NativeInstance r0 = r1.tgVoip     // Catch:{ Exception -> 0x02da }
            boolean r3 = r1.micMute     // Catch:{ Exception -> 0x02da }
            r0.setMuteMicrophone(r3)     // Catch:{ Exception -> 0x02da }
            boolean r0 = r1.isVideoAvailable     // Catch:{ Exception -> 0x02da }
            if (r2 == r0) goto L_0x02cd
            r1.isVideoAvailable = r2     // Catch:{ Exception -> 0x02da }
            r3 = 0
        L_0x02b5:
            java.util.ArrayList<org.telegram.messenger.voip.VoIPBaseService$StateListener> r0 = r1.stateListeners     // Catch:{ Exception -> 0x02da }
            int r0 = r0.size()     // Catch:{ Exception -> 0x02da }
            if (r3 >= r0) goto L_0x02cd
            java.util.ArrayList<org.telegram.messenger.voip.VoIPBaseService$StateListener> r0 = r1.stateListeners     // Catch:{ Exception -> 0x02da }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.VoIPBaseService$StateListener r0 = (org.telegram.messenger.voip.VoIPBaseService.StateListener) r0     // Catch:{ Exception -> 0x02da }
            boolean r2 = r1.isVideoAvailable     // Catch:{ Exception -> 0x02da }
            r0.onVideoAvailableChange(r2)     // Catch:{ Exception -> 0x02da }
            int r3 = r3 + 1
            goto L_0x02b5
        L_0x02cd:
            r1.videoCapturer = r5     // Catch:{ Exception -> 0x02da }
            org.telegram.messenger.voip.VoIPService$1 r0 = new org.telegram.messenger.voip.VoIPService$1     // Catch:{ Exception -> 0x02da }
            r0.<init>()     // Catch:{ Exception -> 0x02da }
            r2 = 5000(0x1388, double:2.4703E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r2)     // Catch:{ Exception -> 0x02da }
            goto L_0x02e7
        L_0x02da:
            r0 = move-exception
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x02e4
            java.lang.String r2 = "error starting call"
            org.telegram.messenger.FileLog.e(r2, r0)
        L_0x02e4:
            r37.callFailed()
        L_0x02e7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.initiateActualEncryptedCall():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initiateActualEncryptedCall$49 */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$49$VoIPService() {
        Toast.makeText(this, "This call uses TCP which will degrade its quality.", 0).show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initiateActualEncryptedCall$50 */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$50$VoIPService(int[] iArr, float[] fArr, boolean[] zArr) {
        if (VoIPBaseService.sharedInstance != null && this.privateCall != null) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(fArr[0]));
        }
    }

    /* access modifiers changed from: protected */
    public void showNotification() {
        TLRPC$User tLRPC$User = this.user;
        if (tLRPC$User != null) {
            showNotification(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), getRoundAvatarBitmap(this.user));
            return;
        }
        TLRPC$Chat tLRPC$Chat = this.chat;
        showNotification(tLRPC$Chat.title, getRoundAvatarBitmap(tLRPC$Chat));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$playConnectedSound$51 */
    public /* synthetic */ void lambda$playConnectedSound$51$VoIPService() {
        this.soundPool.play(this.spVoiceChatStartId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void playConnectedSound() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VoIPService.this.lambda$playConnectedSound$51$VoIPService();
            }
        });
        this.playedConnectedSound = true;
    }

    private void startConnectingSound() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VoIPService.this.lambda$startConnectingSound$52$VoIPService();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startConnectingSound$52 */
    public /* synthetic */ void lambda$startConnectingSound$52$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        int play = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        this.spPlayId = play;
        if (play == 0) {
            AnonymousClass2 r0 = new Runnable() {
                public void run() {
                    if (VoIPBaseService.sharedInstance != null) {
                        Utilities.globalQueue.postRunnable(new Runnable() {
                            public final void run() {
                                VoIPService.AnonymousClass2.this.lambda$run$0$VoIPService$2();
                            }
                        });
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$run$0 */
                public /* synthetic */ void lambda$run$0$VoIPService$2() {
                    VoIPService voIPService = VoIPService.this;
                    if (voIPService.spPlayId == 0) {
                        voIPService.spPlayId = voIPService.soundPool.play(voIPService.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
                    }
                    VoIPService voIPService2 = VoIPService.this;
                    if (voIPService2.spPlayId == 0) {
                        AndroidUtilities.runOnUIThread(this, 100);
                    } else {
                        voIPService2.connectingSoundRunnable = null;
                    }
                }
            };
            this.connectingSoundRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 100);
        }
    }

    public void onSignalingData(byte[] bArr) {
        if (this.privateCall != null) {
            TLRPC$TL_phone_sendSignalingData tLRPC$TL_phone_sendSignalingData = new TLRPC$TL_phone_sendSignalingData();
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_sendSignalingData.peer = tLRPC$TL_inputPhoneCall;
            TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_phone_sendSignalingData.data = bArr;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_sendSignalingData, $$Lambda$VoIPService$TGBJKDJxmUYg_xRu7uwKiyWWOPY.INSTANCE);
        }
    }

    /* access modifiers changed from: protected */
    public void callFailed(String str) {
        if (this.privateCall != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Discarding failed call");
            }
            TLRPC$TL_phone_discardCall tLRPC$TL_phone_discardCall = new TLRPC$TL_phone_discardCall();
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_discardCall.peer = tLRPC$TL_inputPhoneCall;
            TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_phone_discardCall.duration = (int) (getCallDuration() / 1000);
            NativeInstance nativeInstance = this.tgVoip;
            tLRPC$TL_phone_discardCall.connection_id = nativeInstance != null ? nativeInstance.getPreferredRelayId() : 0;
            tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonDisconnect();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, $$Lambda$VoIPService$IFYXbC_mVTTaGZ0ShSA5ReYLhuY.INSTANCE);
        }
        super.callFailed(str);
    }

    static /* synthetic */ void lambda$callFailed$54(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error on phone.discardCall: " + tLRPC$TL_error);
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("phone.discardCall " + tLObject);
        }
    }

    public long getCallID() {
        TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
        if (tLRPC$PhoneCall != null) {
            return tLRPC$PhoneCall.id;
        }
        return 0;
    }

    public boolean isVideoAvailable() {
        return this.isVideoAvailable;
    }

    /* access modifiers changed from: package-private */
    public void onMediaButtonEvent(KeyEvent keyEvent) {
        if ((keyEvent.getKeyCode() != 79 && keyEvent.getKeyCode() != 127 && keyEvent.getKeyCode() != 85) || keyEvent.getAction() != 1) {
            return;
        }
        if (this.currentState == 15) {
            acceptIncomingCall();
        } else {
            setMicMute(!isMicMute(), false, true);
        }
    }

    public byte[] getGA() {
        return this.g_a;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.appDidLogout) {
            callEnded();
        }
    }

    public void forceRating() {
        this.forceRating = true;
    }

    private String[] getEmoji() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(this.authKey);
            byteArrayOutputStream.write(this.g_a);
        } catch (IOException unused) {
        }
        return EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size()));
    }

    public void onConnectionStateChanged(int i, boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable(i, z) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPService.this.lambda$onConnectionStateChanged$55$VoIPService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onConnectionStateChanged$55 */
    public /* synthetic */ void lambda$onConnectionStateChanged$55$VoIPService(int i, boolean z) {
        if (i == 3 && this.callStartTime == 0) {
            this.callStartTime = SystemClock.elapsedRealtime();
        }
        super.onConnectionStateChanged(i, z);
    }

    @TargetApi(26)
    public VoIPBaseService.CallConnection getConnectionAndStartCall() {
        if (this.systemCallConnection == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("creating call connection");
            }
            VoIPBaseService.CallConnection callConnection = new VoIPBaseService.CallConnection();
            this.systemCallConnection = callConnection;
            callConnection.setInitializing();
            if (this.isOutgoing) {
                $$Lambda$VoIPService$_Li76uL85dmJnDv6DGF_pACiipA r0 = new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$getConnectionAndStartCall$56$VoIPService();
                    }
                };
                this.delayedStartOutgoingCall = r0;
                AndroidUtilities.runOnUIThread(r0, 2000);
            }
            VoIPBaseService.CallConnection callConnection2 = this.systemCallConnection;
            callConnection2.setAddress(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), 1);
            VoIPBaseService.CallConnection callConnection3 = this.systemCallConnection;
            TLRPC$User tLRPC$User = this.user;
            callConnection3.setCallerDisplayName(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), 1);
        }
        return this.systemCallConnection;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getConnectionAndStartCall$56 */
    public /* synthetic */ void lambda$getConnectionAndStartCall$56$VoIPService() {
        this.delayedStartOutgoingCall = null;
        startOutgoingCall();
    }
}
