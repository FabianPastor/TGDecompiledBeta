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
import android.view.KeyEvent;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$GroupCall;
import org.telegram.tgnet.TLRPC$PhoneCall;
import org.telegram.tgnet.TLRPC$TL_boolFalse;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCall;
import org.telegram.tgnet.TLRPC$TL_groupCallDiscarded;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
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
import org.telegram.tgnet.TLRPC$TL_phone_toggleGroupCallSettings;
import org.telegram.tgnet.TLRPC$TL_speakingInGroupCallAction;
import org.telegram.tgnet.TLRPC$TL_updateGroupCall;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants;
import org.telegram.tgnet.TLRPC$TL_updatePhoneCallSignalingData;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_DhConfig;
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
    private TLRPC$Chat chat;
    private int checkRequestId;
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
    private Runnable onDestroyRunnable;
    private ArrayList<TLRPC$PhoneCall> pendingUpdates = new ArrayList<>();
    private ProxyVideoSink remoteSink;
    private Runnable shortPollRunnable;
    private boolean startedRinging;
    private TLRPC$User user;

    static /* synthetic */ void lambda$null$34(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$onSignalingData$38(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
            int intExtra2 = intent.getIntExtra("user_id", 0);
            int intExtra3 = intent.getIntExtra("chat_id", 0);
            this.createGroupCall = intent.getIntExtra("createGroupCall", 0);
            this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
            this.videoCall = intent.getBooleanExtra("video_call", false);
            this.isVideoAvailable = intent.getBooleanExtra("can_video_call", false);
            this.notificationsDisabled = intent.getBooleanExtra("notifications_disabled", false);
            if (intExtra2 != 0) {
                this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(intExtra2));
            }
            if (intExtra3 != 0) {
                this.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(intExtra3));
            }
            this.localSink = new ProxyVideoSink();
            this.remoteSink = new ProxyVideoSink();
            try {
                this.isHeadsetPlugged = ((AudioManager) getSystemService("audio")).isWiredHeadsetOn();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (this.videoCall) {
                this.videoCapturer = NativeInstance.createVideoCapturer(this.localSink, this.isFrontFaceCamera);
                this.videoState = 2;
                if (!this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                    setAudioOutput(0);
                }
            } else if (this.groupCall != null && !this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                setAudioOutput(0);
            }
            TLRPC$User tLRPC$User = this.user;
            if (tLRPC$User == null && this.chat == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("VoIPService: user == null AND chat == null");
                }
                stopSelf();
                return 2;
            }
            VoIPBaseService.sharedInstance = this;
            if (this.isOutgoing) {
                if (tLRPC$User != null) {
                    dispatchStateChanged(14);
                    if (VoIPBaseService.USE_CONNECTION_SERVICE) {
                        Bundle bundle = new Bundle();
                        Bundle bundle2 = new Bundle();
                        bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", addAccountToTelecomManager());
                        bundle2.putInt("call_type", 1);
                        bundle.putBundle("android.telecom.extra.OUTGOING_CALL_EXTRAS", bundle2);
                        ContactsController instance = ContactsController.getInstance(this.currentAccount);
                        TLRPC$User tLRPC$User2 = this.user;
                        instance.createOrUpdateConnectionServiceContact(tLRPC$User2.id, tLRPC$User2.first_name, tLRPC$User2.last_name);
                        ((TelecomManager) getSystemService("telecom")).placeCall(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), bundle);
                    } else {
                        $$Lambda$VoIPService$cnIW_wfBChuvGeQZ5_THjfGbn8I r9 = new Runnable() {
                            public final void run() {
                                VoIPService.this.lambda$onStartCommand$0$VoIPService();
                            }
                        };
                        this.delayedStartOutgoingCall = r9;
                        AndroidUtilities.runOnUIThread(r9, 2000);
                    }
                } else {
                    this.micMute = true;
                    startGroupCall(0, (String) null);
                    if (!this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                        setAudioOutput(0);
                    }
                }
                if (intent.getBooleanExtra("start_incall_activity", false)) {
                    startActivity(new Intent(this, LaunchActivity.class).setAction(this.user != null ? "voip" : "voip_chat").addFlags(NUM));
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
                    VoIPService.this.lambda$onStartCommand$1$VoIPService();
                }
            });
            return 2;
        }
        throw new IllegalStateException("No account specified when starting VoIP service");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStartCommand$0 */
    public /* synthetic */ void lambda$onStartCommand$0$VoIPService() {
        this.delayedStartOutgoingCall = null;
        startOutgoingCall();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStartCommand$1 */
    public /* synthetic */ void lambda$onStartCommand$1$VoIPService() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.voipServiceCreated, new Object[0]);
    }

    public void onCreate() {
        super.onCreate();
        if (callIShouldHavePutIntoIntent != null && Build.VERSION.SDK_INT >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            startForeground(201, new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setSmallIcon(NUM).setContentTitle(LocaleController.getString("VoipOutgoingCall", NUM)).setShowWhen(false).build());
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
                VoIPService.lambda$updateServerConfig$2(this.f$0, tLObject, tLRPC$TL_error);
            }
        });
    }

    static /* synthetic */ void lambda$updateServerConfig$2(SharedPreferences sharedPreferences, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_saveCallDebug, $$Lambda$VoIPService$SqDUwGjWhC8YCDytO8sjWUzaE10.INSTANCE);
                this.needSendDebugLog = false;
            }
        }
    }

    static /* synthetic */ void lambda$onTgVoipStop$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.groupCallUpdated, Integer.valueOf(this.chat.id), Long.valueOf(this.groupCall.call.id));
                }
                TLRPC$TL_phone_discardGroupCall tLRPC$TL_phone_discardGroupCall = new TLRPC$TL_phone_discardGroupCall();
                tLRPC$TL_phone_discardGroupCall.call = this.groupCall.getInputGroupCall();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardGroupCall, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        VoIPService.this.lambda$hangUp$4$VoIPService(tLObject, tLRPC$TL_error);
                    }
                });
                return;
            }
            TLRPC$TL_phone_leaveGroupCall tLRPC$TL_phone_leaveGroupCall = new TLRPC$TL_phone_leaveGroupCall();
            tLRPC$TL_phone_leaveGroupCall.call = this.groupCall.getInputGroupCall();
            tLRPC$TL_phone_leaveGroupCall.source = this.mySource;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_leaveGroupCall, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$hangUp$5$VoIPService(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hangUp$4 */
    public /* synthetic */ void lambda$hangUp$4$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hangUp$5 */
    public /* synthetic */ void lambda$hangUp$5$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        AndroidUtilities.runOnUIThread($$Lambda$VoIPService$vnWO6KEtK3PoR1snVX3t9sZtgk.INSTANCE);
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
                VoIPService.this.lambda$startOutgoingCall$11$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOutgoingCall$11 */
    public /* synthetic */ void lambda$startOutgoingCall$11$VoIPService(MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    VoIPService.this.lambda$null$10$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
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
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$VoIPService(byte[] bArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                VoIPService.this.lambda$null$9$VoIPService(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$9 */
    public /* synthetic */ void lambda$null$9$VoIPService(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, byte[] bArr) {
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
            $$Lambda$VoIPService$7jyH06jjjp1CrJc_to9X8oyks r1 = new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$null$8$VoIPService();
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
    /* renamed from: lambda$null$8 */
    public /* synthetic */ void lambda$null$8$VoIPService() {
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
                VoIPService.this.lambda$null$7$VoIPService(tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    VoIPService.this.lambda$acknowledgeCall$13$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
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
    /* renamed from: lambda$acknowledgeCall$13 */
    public /* synthetic */ void lambda$acknowledgeCall$13$VoIPService(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                VoIPService.this.lambda$null$12$VoIPService(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$12 */
    public /* synthetic */ void lambda$null$12$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, boolean z) {
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
        AndroidUtilities.runOnUIThread($$Lambda$VoIPService$wLD2th9ywSnQAfQ8zK_j9bMmGI.INSTANCE);
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
                VoIPService.this.lambda$acceptIncomingCall$17$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$acceptIncomingCall$17 */
    public /* synthetic */ void lambda$acceptIncomingCall$17$VoIPService(MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    VoIPService.this.lambda$null$16$VoIPService(tLObject, tLRPC$TL_error);
                }
            }, 2);
            return;
        }
        callFailed();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$16 */
    public /* synthetic */ void lambda$null$16$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPService.this.lambda$null$15$VoIPService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$15 */
    public /* synthetic */ void lambda$null$15$VoIPService(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
    }

    /* access modifiers changed from: protected */
    public Class<? extends Activity> getUIActivityClass() {
        return LaunchActivity.class;
    }

    public boolean isHangingUp() {
        return this.currentState == 10;
    }

    public void declineIncomingCall(int i, final Runnable runnable) {
        AnonymousClass1 r8;
        stopRinging();
        this.callDiscardReason = i;
        int i2 = this.currentState;
        boolean z = true;
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
                    VoIPService.this.lambda$declineIncomingCall$18$VoIPService();
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
            if (ConnectionsManager.getInstance(this.currentAccount).getConnectionState() == 3) {
                z = false;
            }
            if (z) {
                if (runnable != null) {
                    runnable.run();
                }
                callEnded();
                r8 = null;
            } else {
                r8 = new Runnable() {
                    private boolean done = false;

                    public void run() {
                        if (!this.done) {
                            this.done = true;
                            Runnable runnable = runnable;
                            if (runnable != null) {
                                runnable.run();
                            }
                            VoIPService.this.callEnded();
                        }
                    }
                };
                AndroidUtilities.runOnUIThread(r8, (long) ((int) (Instance.getGlobalServerConfig().hangupUiTimeout * 1000.0d)));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, new RequestDelegate(z, r8, runnable) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ Runnable f$2;
                public final /* synthetic */ Runnable f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$declineIncomingCall$19$VoIPService(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$declineIncomingCall$18 */
    public /* synthetic */ void lambda$declineIncomingCall$18$VoIPService() {
        if (this.currentState == 10) {
            callEnded();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$declineIncomingCall$19 */
    public /* synthetic */ void lambda$declineIncomingCall$19$VoIPService(boolean z, Runnable runnable, Runnable runnable2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        if (!z) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            if (runnable2 != null) {
                runnable2.run();
            }
        }
    }

    public void onSignalingData(TLRPC$TL_updatePhoneCallSignalingData tLRPC$TL_updatePhoneCallSignalingData) {
        NativeInstance nativeInstance;
        if (this.user != null && (nativeInstance = this.tgVoip) != null && !nativeInstance.isGroup() && getCallID() == tLRPC$TL_updatePhoneCallSignalingData.phone_call_id) {
            this.tgVoip.onSignalingDataReceive(tLRPC$TL_updatePhoneCallSignalingData.data);
        }
    }

    public void onGroupCallParticipantsUpdate(TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants) {
        ChatObject.Call call;
        if (this.chat != null && (call = this.groupCall) != null && call.call.id == tLRPC$TL_updateGroupCallParticipants.call.id) {
            ArrayList arrayList = null;
            int size = tLRPC$TL_updateGroupCallParticipants.participants.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_updateGroupCallParticipants.participants.get(i);
                if (tLRPC$TL_groupCallParticipant.left) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(tLRPC$TL_groupCallParticipant.source));
                }
            }
            if (arrayList != null) {
                int[] iArr = new int[arrayList.size()];
                int size2 = arrayList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    iArr[i2] = ((Integer) arrayList.get(i2)).intValue();
                }
                this.tgVoip.removeSsrcs(iArr);
            }
        }
    }

    public void onGroupCallUpdated(TLRPC$GroupCall tLRPC$GroupCall) {
        ChatObject.Call call;
        TLRPC$TL_dataJSON tLRPC$TL_dataJSON;
        TLRPC$GroupCall tLRPC$GroupCall2 = tLRPC$GroupCall;
        if (this.chat != null && (call = this.groupCall) != null) {
            TLRPC$GroupCall tLRPC$GroupCall3 = call.call;
            if (tLRPC$GroupCall3.id == tLRPC$GroupCall2.id) {
                if (tLRPC$GroupCall3 instanceof TLRPC$TL_groupCallDiscarded) {
                    hangUp(2);
                } else if (this.currentState == 1 && (tLRPC$TL_dataJSON = tLRPC$GroupCall2.params) != null) {
                    try {
                        JSONObject jSONObject = new JSONObject(tLRPC$TL_dataJSON.data).getJSONObject("transport");
                        String string = jSONObject.getString("ufrag");
                        String string2 = jSONObject.getString("pwd");
                        JSONArray jSONArray = jSONObject.getJSONArray("fingerprints");
                        int length = jSONArray.length();
                        Instance.Fingerprint[] fingerprintArr = new Instance.Fingerprint[length];
                        for (int i = 0; i < length; i++) {
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                            fingerprintArr[i] = new Instance.Fingerprint(jSONObject2.getString("hash"), jSONObject2.getString("setup"), jSONObject2.getString("fingerprint"));
                        }
                        JSONArray jSONArray2 = jSONObject.getJSONArray("candidates");
                        int length2 = jSONArray2.length();
                        Instance.Candidate[] candidateArr = new Instance.Candidate[length2];
                        for (int i2 = 0; i2 < length2; i2++) {
                            JSONObject jSONObject3 = jSONArray2.getJSONObject(i2);
                            candidateArr[i2] = new Instance.Candidate(jSONObject3.optString("port", ""), jSONObject3.optString("protocol", ""), jSONObject3.optString("network", ""), jSONObject3.optString("generation", ""), jSONObject3.optString("id", ""), jSONObject3.optString("component", ""), jSONObject3.optString("foundation", ""), jSONObject3.optString("priority", ""), jSONObject3.optString("ip", ""), jSONObject3.optString("type", ""), jSONObject3.optString("tcpType", ""), jSONObject3.optString("relAddr", ""), jSONObject3.optString("relPort", ""));
                        }
                        this.tgVoip.setJoinResponsePayload(string, string2, fingerprintArr, candidateArr);
                        dispatchStateChanged(2);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x0162  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x016f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCallUpdated(org.telegram.tgnet.TLRPC$PhoneCall r10) {
        /*
            r9 = this;
            org.telegram.tgnet.TLRPC$User r0 = r9.user
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.tgnet.TLRPC$PhoneCall r0 = r9.privateCall
            if (r0 != 0) goto L_0x000f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneCall> r0 = r9.pendingUpdates
            r0.add(r10)
            return
        L_0x000f:
            if (r10 != 0) goto L_0x0012
            return
        L_0x0012:
            long r1 = r10.id
            long r3 = r0.id
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0046
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0045
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onCallUpdated called with wrong call id (got "
            r0.append(r1)
            long r1 = r10.id
            r0.append(r1)
            java.lang.String r10 = ", expected "
            r0.append(r10)
            org.telegram.tgnet.TLRPC$PhoneCall r10 = r9.privateCall
            long r1 = r10.id
            r0.append(r1)
            java.lang.String r10 = ")"
            r0.append(r10)
            java.lang.String r10 = r0.toString()
            org.telegram.messenger.FileLog.w(r10)
        L_0x0045:
            return
        L_0x0046:
            long r1 = r10.access_hash
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x0052
            long r0 = r0.access_hash
            r10.access_hash = r0
        L_0x0052:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x006a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Call updated: "
            r0.append(r1)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x006a:
            r9.privateCall = r10
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallDiscarded
            r1 = 1
            if (r0 == 0) goto L_0x00b2
            boolean r0 = r10.need_debug
            r9.needSendDebugLog = r0
            boolean r0 = r10.need_rating
            r9.needRateCall = r0
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0082
            java.lang.String r0 = "call discarded, stopping service"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0082:
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r10 = r10.reason
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy
            if (r10 == 0) goto L_0x00ad
            r10 = 17
            r9.dispatchStateChanged(r10)
            r9.playingSound = r1
            android.media.SoundPool r2 = r9.soundPool
            int r3 = r9.spBusyId
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            r7 = -1
            r8 = 1065353216(0x3var_, float:1.0)
            r2.play(r3, r4, r5, r6, r7, r8)
            java.lang.Runnable r10 = r9.afterSoundRunnable
            r0 = 1500(0x5dc, double:7.41E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r10, r0)
            r9.endConnectionServiceCall(r0)
            r9.stopSelf()
            goto L_0x01d8
        L_0x00ad:
            r9.callEnded()
            goto L_0x01d8
        L_0x00b2:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_phoneCall
            if (r0 == 0) goto L_0x0173
            byte[] r0 = r9.authKey
            if (r0 != 0) goto L_0x0173
            byte[] r0 = r10.g_a_or_b
            if (r0 != 0) goto L_0x00cb
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r10 == 0) goto L_0x00c7
            java.lang.String r10 = "stopping VoIP service, Ga == null"
            org.telegram.messenger.FileLog.w(r10)
        L_0x00c7:
            r9.callFailed()
            return
        L_0x00cb:
            byte[] r2 = r9.g_a_hash
            int r3 = r0.length
            r4 = 0
            byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r0, r4, r3)
            boolean r0 = java.util.Arrays.equals(r2, r0)
            if (r0 != 0) goto L_0x00e6
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r10 == 0) goto L_0x00e2
            java.lang.String r10 = "stopping VoIP service, Ga hash doesn't match"
            org.telegram.messenger.FileLog.w(r10)
        L_0x00e2:
            r9.callFailed()
            return
        L_0x00e6:
            byte[] r0 = r10.g_a_or_b
            r9.g_a = r0
            java.math.BigInteger r0 = new java.math.BigInteger
            byte[] r2 = r10.g_a_or_b
            r0.<init>(r1, r2)
            java.math.BigInteger r2 = new java.math.BigInteger
            int r3 = r9.currentAccount
            org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)
            byte[] r3 = r3.getSecretPBytes()
            r2.<init>(r1, r3)
            boolean r3 = org.telegram.messenger.Utilities.isGoodGaAndGb(r0, r2)
            if (r3 != 0) goto L_0x0113
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r10 == 0) goto L_0x010f
            java.lang.String r10 = "stopping VoIP service, bad Ga and Gb (accepting)"
            org.telegram.messenger.FileLog.w(r10)
        L_0x010f:
            r9.callFailed()
            return
        L_0x0113:
            java.math.BigInteger r3 = new java.math.BigInteger
            byte[] r5 = r9.a_or_b
            r3.<init>(r1, r5)
            java.math.BigInteger r0 = r0.modPow(r3, r2)
            byte[] r0 = r0.toByteArray()
            int r1 = r0.length
            r2 = 256(0x100, float:3.59E-43)
            if (r1 <= r2) goto L_0x0130
            byte[] r1 = new byte[r2]
            int r3 = r0.length
            int r3 = r3 - r2
            java.lang.System.arraycopy(r0, r3, r1, r4, r2)
        L_0x012e:
            r0 = r1
            goto L_0x0147
        L_0x0130:
            int r1 = r0.length
            if (r1 >= r2) goto L_0x0147
            byte[] r1 = new byte[r2]
            int r3 = r0.length
            int r3 = 256 - r3
            int r5 = r0.length
            java.lang.System.arraycopy(r0, r4, r1, r3, r5)
            r3 = 0
        L_0x013d:
            int r5 = r0.length
            int r5 = 256 - r5
            if (r3 >= r5) goto L_0x012e
            r1[r3] = r4
            int r3 = r3 + 1
            goto L_0x013d
        L_0x0147:
            byte[] r1 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r0)
            r2 = 8
            byte[] r3 = new byte[r2]
            int r5 = r1.length
            int r5 = r5 - r2
            java.lang.System.arraycopy(r1, r5, r3, r4, r2)
            r9.authKey = r0
            long r0 = org.telegram.messenger.Utilities.bytesToLong(r3)
            r9.keyFingerprint = r0
            long r2 = r10.key_fingerprint
            int r10 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r10 == 0) goto L_0x016f
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r10 == 0) goto L_0x016b
            java.lang.String r10 = "key fingerprints don't match"
            org.telegram.messenger.FileLog.w(r10)
        L_0x016b:
            r9.callFailed()
            return
        L_0x016f:
            r9.initiateActualEncryptedCall()
            goto L_0x01d8
        L_0x0173:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallAccepted
            if (r0 == 0) goto L_0x017f
            byte[] r0 = r9.authKey
            if (r0 != 0) goto L_0x017f
            r9.processAcceptedCall()
            goto L_0x01d8
        L_0x017f:
            int r0 = r9.currentState
            r1 = 13
            if (r0 != r1) goto L_0x01d8
            int r10 = r10.receive_date
            if (r10 == 0) goto L_0x01d8
            r10 = 16
            r9.dispatchStateChanged(r10)
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r10 == 0) goto L_0x0197
            java.lang.String r10 = "!!!!!! CALL RECEIVED"
            org.telegram.messenger.FileLog.d(r10)
        L_0x0197:
            java.lang.Runnable r10 = r9.connectingSoundRunnable
            r0 = 0
            if (r10 == 0) goto L_0x01a1
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r10)
            r9.connectingSoundRunnable = r0
        L_0x01a1:
            int r10 = r9.spPlayID
            if (r10 == 0) goto L_0x01aa
            android.media.SoundPool r1 = r9.soundPool
            r1.stop(r10)
        L_0x01aa:
            android.media.SoundPool r2 = r9.soundPool
            int r3 = r9.spRingbackID
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            r7 = -1
            r8 = 1065353216(0x3var_, float:1.0)
            int r10 = r2.play(r3, r4, r5, r6, r7, r8)
            r9.spPlayID = r10
            java.lang.Runnable r10 = r9.timeoutRunnable
            if (r10 == 0) goto L_0x01c5
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r10)
            r9.timeoutRunnable = r0
        L_0x01c5:
            org.telegram.messenger.voip.-$$Lambda$VoIPService$RNbVktdEwQBTJmDL9WrFrWzcXck r10 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$RNbVktdEwQBTJmDL9WrFrWzcXck
            r10.<init>()
            r9.timeoutRunnable = r10
            int r0 = r9.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r0 = r0.callRingTimeout
            long r0 = (long) r0
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r10, r0)
        L_0x01d8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.onCallUpdated(org.telegram.tgnet.TLRPC$PhoneCall):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCallUpdated$20 */
    public /* synthetic */ void lambda$onCallUpdated$20$VoIPService() {
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
                    VoIPService.this.lambda$processAcceptedCall$22$VoIPService(tLObject, tLRPC$TL_error);
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
                VoIPService.this.lambda$processAcceptedCall$22$VoIPService(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processAcceptedCall$22 */
    public /* synthetic */ void lambda$processAcceptedCall$22$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPService.this.lambda$null$21$VoIPService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$21 */
    public /* synthetic */ void lambda$null$21$VoIPService(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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

    /* access modifiers changed from: private */
    public void startGroupCall(int i, String str) {
        if (VoIPBaseService.sharedInstance == this) {
            int i2 = 2;
            boolean z = true;
            if (this.createGroupCall != 0) {
                ChatObject.Call call = new ChatObject.Call();
                this.groupCall = call;
                call.call = new TLRPC$TL_groupCall();
                ChatObject.Call call2 = this.groupCall;
                TLRPC$GroupCall tLRPC$GroupCall = call2.call;
                tLRPC$GroupCall.participants_count = 0;
                tLRPC$GroupCall.version = 1;
                tLRPC$GroupCall.can_change_join_muted = true;
                int i3 = this.createGroupCall;
                tLRPC$GroupCall.join_muted = i3 == 2;
                call2.chatId = this.chat.id;
                call2.currentAccount = this.currentAccount;
                if (i3 != 2) {
                    z = false;
                }
                dispatchStateChanged(6);
                TLRPC$TL_phone_createGroupCall tLRPC$TL_phone_createGroupCall = new TLRPC$TL_phone_createGroupCall();
                tLRPC$TL_phone_createGroupCall.channel = MessagesController.getInputChannel(this.chat);
                tLRPC$TL_phone_createGroupCall.random_id = Utilities.random.nextInt();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_createGroupCall, new RequestDelegate(z) {
                    public final /* synthetic */ boolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        VoIPService.this.lambda$startGroupCall$26$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
                    }
                }, 2);
                this.createGroupCall = 0;
            } else if (str == null) {
                if (this.groupCall == null) {
                    this.groupCall = MessagesController.getInstance(this.currentAccount).getGroupCall(this.chat.id, false);
                }
                configureDeviceForCall();
                showNotification();
                AndroidUtilities.runOnUIThread($$Lambda$VoIPService$tGLyMzKBQ5hlAuZqJ0vWtFAsmcE.INSTANCE);
                createGroupInstance();
            } else if (getSharedInstance() != null) {
                dispatchStateChanged(1);
                this.mySource = i;
                TLRPC$TL_phone_joinGroupCall tLRPC$TL_phone_joinGroupCall = new TLRPC$TL_phone_joinGroupCall();
                tLRPC$TL_phone_joinGroupCall.muted = true;
                tLRPC$TL_phone_joinGroupCall.call = this.groupCall.getInputGroupCall();
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
                tLRPC$TL_phone_joinGroupCall.params = tLRPC$TL_dataJSON;
                tLRPC$TL_dataJSON.data = str;
                ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
                $$Lambda$VoIPService$BtHVA7AO1irDjn7CxzA6aZSsGZM r0 = new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        VoIPService.this.lambda$startGroupCall$30$VoIPService(tLObject, tLRPC$TL_error);
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
    /* renamed from: lambda$startGroupCall$26 */
    public /* synthetic */ void lambda$startGroupCall$26$VoIPService(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            int i = 0;
            while (true) {
                if (i >= tLRPC$Updates.updates.size()) {
                    break;
                }
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateGroupCall) {
                    AndroidUtilities.runOnUIThread(new Runnable((TLRPC$TL_updateGroupCall) tLRPC$Update, z) {
                        public final /* synthetic */ TLRPC$TL_updateGroupCall f$1;
                        public final /* synthetic */ boolean f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            VoIPService.this.lambda$null$24$VoIPService(this.f$1, this.f$2);
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
                VoIPService.this.lambda$null$25$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$24 */
    public /* synthetic */ void lambda$null$24$VoIPService(TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall, boolean z) {
        if (VoIPBaseService.sharedInstance != null) {
            TLRPC$GroupCall tLRPC$GroupCall = this.groupCall.call;
            TLRPC$GroupCall tLRPC$GroupCall2 = tLRPC$TL_updateGroupCall.call;
            tLRPC$GroupCall.access_hash = tLRPC$GroupCall2.access_hash;
            tLRPC$GroupCall.id = tLRPC$GroupCall2.id;
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            ChatObject.Call call = this.groupCall;
            instance.putGroupCall(call.chatId, call);
            if (z) {
                TLRPC$TL_phone_toggleGroupCallSettings tLRPC$TL_phone_toggleGroupCallSettings = new TLRPC$TL_phone_toggleGroupCallSettings();
                tLRPC$TL_phone_toggleGroupCallSettings.call = this.groupCall.getInputGroupCall();
                tLRPC$TL_phone_toggleGroupCallSettings.flags |= 1;
                tLRPC$TL_phone_toggleGroupCallSettings.join_muted = true;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_toggleGroupCallSettings, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        VoIPService.this.lambda$null$23$VoIPService(tLObject, tLRPC$TL_error);
                    }
                });
            }
            startGroupCall(0, (String) null);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$23 */
    public /* synthetic */ void lambda$null$23$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$25 */
    public /* synthetic */ void lambda$null$25$VoIPService(TLRPC$TL_error tLRPC$TL_error) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, tLRPC$TL_error.text);
        hangUp(0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCall$30 */
    public /* synthetic */ void lambda$startGroupCall$30$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$null$28$VoIPService();
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPService.this.lambda$null$29$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$28 */
    public /* synthetic */ void lambda$null$28$VoIPService() {
        this.groupCall.loadMembers(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$29 */
    public /* synthetic */ void lambda$null$29$VoIPService(TLRPC$TL_error tLRPC$TL_error) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, tLRPC$TL_error.text);
        hangUp(0);
    }

    private void startGroupCheckShortpoll() {
        if (this.shortPollRunnable == null && VoIPBaseService.sharedInstance != null && this.groupCall != null) {
            $$Lambda$VoIPService$X0c_kB7d9gnY7H0LaF2BDaHdU r0 = new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$startGroupCheckShortpoll$33$VoIPService();
                }
            };
            this.shortPollRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 4000);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCheckShortpoll$33 */
    public /* synthetic */ void lambda$startGroupCheckShortpoll$33$VoIPService() {
        if (this.shortPollRunnable != null && VoIPBaseService.sharedInstance != null && this.groupCall != null) {
            TLRPC$TL_phone_checkGroupCall tLRPC$TL_phone_checkGroupCall = new TLRPC$TL_phone_checkGroupCall();
            tLRPC$TL_phone_checkGroupCall.call = this.groupCall.getInputGroupCall();
            tLRPC$TL_phone_checkGroupCall.source = this.mySource;
            this.checkRequestId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_checkGroupCall, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$null$32$VoIPService(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$32 */
    public /* synthetic */ void lambda$null$32$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$TL_error) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPService.this.lambda$null$31$VoIPService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$31 */
    public /* synthetic */ void lambda$null$31$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (this.shortPollRunnable != null && VoIPBaseService.sharedInstance != null && this.groupCall != null) {
            this.shortPollRunnable = null;
            this.checkRequestId = 0;
            if ((tLObject instanceof TLRPC$TL_boolFalse) || (tLRPC$TL_error != null && tLRPC$TL_error.code == 400)) {
                createGroupInstance();
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

    private void createGroupInstance() {
        NativeInstance nativeInstance = this.tgVoip;
        if (nativeInstance != null) {
            nativeInstance.stopGroup();
        }
        NativeInstance makeGroup = NativeInstance.makeGroup(new NativeInstance.PayloadCallback() {
            public final void run(int i, String str) {
                VoIPService.this.startGroupCall(i, str);
            }
        }, new NativeInstance.AudioLevelsCallback() {
            public final void run(int[] iArr, float[] fArr) {
                VoIPService.this.lambda$createGroupInstance$35$VoIPService(iArr, fArr);
            }
        });
        this.tgVoip = makeGroup;
        makeGroup.setOnStateUpdatedListener(new Instance.OnStateUpdatedListener() {
            public final void onStateUpdated(int i) {
                VoIPService.this.lambda$createGroupInstance$36$VoIPService(i);
            }
        });
        dispatchStateChanged(1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$35 */
    public /* synthetic */ void lambda$createGroupInstance$35$VoIPService(int[] iArr, float[] fArr) {
        ChatObject.Call call;
        if (VoIPBaseService.sharedInstance != null && (call = this.groupCall) != null) {
            call.processVoiceLevelsUpdate(iArr, fArr);
            if (iArr == null) {
                if (this.lastTypingTimeSend < SystemClock.uptimeMillis() - 5000 && fArr[0] > 0.1f) {
                    this.lastTypingTimeSend = SystemClock.uptimeMillis();
                    TLRPC$TL_messages_setTyping tLRPC$TL_messages_setTyping = new TLRPC$TL_messages_setTyping();
                    tLRPC$TL_messages_setTyping.action = new TLRPC$TL_speakingInGroupCallAction();
                    tLRPC$TL_messages_setTyping.peer = MessagesController.getInputPeer(this.chat);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_setTyping, $$Lambda$VoIPService$2ibAjneDk9vGW6Nw2TIThdGMncw.INSTANCE);
                }
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.groupCall.participants.get(UserConfig.getInstance(this.currentAccount).getClientUserId());
                if (tLRPC$TL_groupCallParticipant != null && fArr[0] > 0.1f) {
                    tLRPC$TL_groupCallParticipant.lastSpeakTime = SystemClock.uptimeMillis();
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(fArr[0]));
                return;
            }
            float f = 0.0f;
            for (int i = 0; i < iArr.length; i++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.groupCall.participantsBySources.get(iArr[i]);
                if (tLRPC$TL_groupCallParticipant2 != null && fArr[i] > 0.1f) {
                    tLRPC$TL_groupCallParticipant2.lastSpeakTime = SystemClock.uptimeMillis();
                    tLRPC$TL_groupCallParticipant2.amplitude = fArr[i];
                } else if (tLRPC$TL_groupCallParticipant2 != null) {
                    tLRPC$TL_groupCallParticipant2.amplitude = 0.0f;
                }
                f = Math.max(f, fArr[i]);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcSpeakerAmplitudeEvent, Float.valueOf(f));
            NativeInstance.AudioLevelsCallback audioLevelsCallback2 = audioLevelsCallback;
            if (audioLevelsCallback2 != null) {
                audioLevelsCallback2.run(iArr, fArr);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$36 */
    public /* synthetic */ void lambda$createGroupInstance$36$VoIPService(int i) {
        dispatchStateChanged(i == 1 ? 3 : 5);
        if (i == 0) {
            startGroupCheckShortpoll();
        } else {
            cancelGroupCheckShortPoll();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r5.remove();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x009f */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0107 A[Catch:{ Exception -> 0x02d3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0115 A[Catch:{ Exception -> 0x02d3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0130 A[Catch:{ Exception -> 0x02d3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0170 A[Catch:{ Exception -> 0x02d3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x017e A[Catch:{ Exception -> 0x02d3 }, LOOP:2: B:69:0x017c->B:70:0x017e, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01cf A[Catch:{ Exception -> 0x02d3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0240 A[Catch:{ Exception -> 0x02d3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0242 A[Catch:{ Exception -> 0x02d3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x02ab A[Catch:{ Exception -> 0x02d3 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initiateActualEncryptedCall() {
        /*
            r36 = this;
            r1 = r36
            java.lang.String r0 = "calls_access_hashes"
            java.lang.String r2 = " "
            java.lang.Runnable r3 = r1.timeoutRunnable
            r4 = 0
            if (r3 == 0) goto L_0x0010
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r3)
            r1.timeoutRunnable = r4
        L_0x0010:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02d3 }
            if (r3 == 0) goto L_0x002a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02d3 }
            r3.<init>()     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r5 = "InitCall: keyID="
            r3.append(r5)     // Catch:{ Exception -> 0x02d3 }
            long r5 = r1.keyFingerprint     // Catch:{ Exception -> 0x02d3 }
            r3.append(r5)     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x02d3 }
        L_0x002a:
            int r3 = r1.currentAccount     // Catch:{ Exception -> 0x02d3 }
            android.content.SharedPreferences r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3)     // Catch:{ Exception -> 0x02d3 }
            java.util.Set r5 = r3.getStringSet(r0, r4)     // Catch:{ Exception -> 0x02d3 }
            if (r5 == 0) goto L_0x003c
            java.util.HashSet r6 = new java.util.HashSet     // Catch:{ Exception -> 0x02d3 }
            r6.<init>(r5)     // Catch:{ Exception -> 0x02d3 }
            goto L_0x0041
        L_0x003c:
            java.util.HashSet r6 = new java.util.HashSet     // Catch:{ Exception -> 0x02d3 }
            r6.<init>()     // Catch:{ Exception -> 0x02d3 }
        L_0x0041:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02d3 }
            r5.<init>()     // Catch:{ Exception -> 0x02d3 }
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r1.privateCall     // Catch:{ Exception -> 0x02d3 }
            long r7 = r7.id     // Catch:{ Exception -> 0x02d3 }
            r5.append(r7)     // Catch:{ Exception -> 0x02d3 }
            r5.append(r2)     // Catch:{ Exception -> 0x02d3 }
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r1.privateCall     // Catch:{ Exception -> 0x02d3 }
            long r7 = r7.access_hash     // Catch:{ Exception -> 0x02d3 }
            r5.append(r7)     // Catch:{ Exception -> 0x02d3 }
            r5.append(r2)     // Catch:{ Exception -> 0x02d3 }
            long r7 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x02d3 }
            r5.append(r7)     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x02d3 }
            r6.add(r5)     // Catch:{ Exception -> 0x02d3 }
        L_0x0068:
            int r5 = r6.size()     // Catch:{ Exception -> 0x02d3 }
            r7 = 20
            r8 = 2
            if (r5 <= r7) goto L_0x00a9
            r9 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            java.util.Iterator r5 = r6.iterator()     // Catch:{ Exception -> 0x02d3 }
            r7 = r4
        L_0x007b:
            boolean r11 = r5.hasNext()     // Catch:{ Exception -> 0x02d3 }
            if (r11 == 0) goto L_0x00a3
            java.lang.Object r11 = r5.next()     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r11 = (java.lang.String) r11     // Catch:{ Exception -> 0x02d3 }
            java.lang.String[] r12 = r11.split(r2)     // Catch:{ Exception -> 0x02d3 }
            int r13 = r12.length     // Catch:{ Exception -> 0x02d3 }
            if (r13 >= r8) goto L_0x0092
            r5.remove()     // Catch:{ Exception -> 0x02d3 }
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
            r5.remove()     // Catch:{ Exception -> 0x02d3 }
            goto L_0x007b
        L_0x00a3:
            if (r7 == 0) goto L_0x0068
            r6.remove(r7)     // Catch:{ Exception -> 0x02d3 }
            goto L_0x0068
        L_0x00a9:
            android.content.SharedPreferences$Editor r2 = r3.edit()     // Catch:{ Exception -> 0x02d3 }
            android.content.SharedPreferences$Editor r0 = r2.putStringSet(r0, r6)     // Catch:{ Exception -> 0x02d3 }
            r0.commit()     // Catch:{ Exception -> 0x02d3 }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02d3 }
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
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x02d3 }
            int r6 = r1.currentAccount     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)     // Catch:{ Exception -> 0x02d3 }
            int r7 = r6.callConnectTimeout     // Catch:{ Exception -> 0x02d3 }
            double r9 = (double) r7
            r11 = 4652007308841189376(0x408fNUM, double:1000.0)
            java.lang.Double.isNaN(r9)
            double r14 = r9 / r11
            int r6 = r6.callPacketTimeout     // Catch:{ Exception -> 0x02d3 }
            double r6 = (double) r6
            java.lang.Double.isNaN(r6)
            double r16 = r6 / r11
            java.lang.String r6 = "VoipDataSaving"
            int r7 = org.telegram.ui.Components.voip.VoIPHelper.getDataSavingDefault()     // Catch:{ Exception -> 0x02d3 }
            int r6 = r5.getInt(r6, r7)     // Catch:{ Exception -> 0x02d3 }
            int r18 = r1.convertDataSavingMode(r6)     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.Instance$ServerConfig r6 = org.telegram.messenger.voip.Instance.getGlobalServerConfig()     // Catch:{ Exception -> 0x02d3 }
            if (r0 == 0) goto L_0x0103
            boolean r0 = r6.useSystemAec     // Catch:{ Exception -> 0x02d3 }
            if (r0 != 0) goto L_0x0100
            goto L_0x0103
        L_0x0100:
            r20 = 0
            goto L_0x0105
        L_0x0103:
            r20 = 1
        L_0x0105:
            if (r2 == 0) goto L_0x010f
            boolean r0 = r6.useSystemNs     // Catch:{ Exception -> 0x02d3 }
            if (r0 != 0) goto L_0x010c
            goto L_0x010f
        L_0x010c:
            r21 = 0
            goto L_0x0111
        L_0x010f:
            r21 = 1
        L_0x0111:
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x02d3 }
            if (r0 == 0) goto L_0x0130
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02d3 }
            r0.<init>()     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r2 = "voip"
            r0.append(r2)     // Catch:{ Exception -> 0x02d3 }
            org.telegram.tgnet.TLRPC$PhoneCall r2 = r1.privateCall     // Catch:{ Exception -> 0x02d3 }
            long r9 = r2.id     // Catch:{ Exception -> 0x02d3 }
            r0.append(r9)     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r0 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath(r0)     // Catch:{ Exception -> 0x02d3 }
            goto L_0x0138
        L_0x0130:
            org.telegram.tgnet.TLRPC$PhoneCall r0 = r1.privateCall     // Catch:{ Exception -> 0x02d3 }
            long r9 = r0.id     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r0 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath(r9, r3)     // Catch:{ Exception -> 0x02d3 }
        L_0x0138:
            r25 = r0
            org.telegram.messenger.voip.Instance$Config r0 = new org.telegram.messenger.voip.Instance$Config     // Catch:{ Exception -> 0x02d3 }
            org.telegram.tgnet.TLRPC$PhoneCall r2 = r1.privateCall     // Catch:{ Exception -> 0x02d3 }
            boolean r9 = r2.p2p_allowed     // Catch:{ Exception -> 0x02d3 }
            r22 = 1
            r23 = 0
            boolean r6 = r6.enableStunMarking     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r26 = ""
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r2 = r2.protocol     // Catch:{ Exception -> 0x02d3 }
            int r2 = r2.max_layer     // Catch:{ Exception -> 0x02d3 }
            r13 = r0
            r19 = r9
            r24 = r6
            r27 = r2
            r13.<init>(r14, r16, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ Exception -> 0x02d3 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x02d3 }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02d3 }
            java.io.File r6 = r6.getFilesDir()     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r9 = "voip_persistent_state.json"
            r2.<init>(r6, r9)     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r28 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r2 = "dbg_force_tcp_in_calls"
            boolean r2 = r5.getBoolean(r2, r3)     // Catch:{ Exception -> 0x02d3 }
            if (r2 == 0) goto L_0x0171
            r8 = 3
        L_0x0171:
            org.telegram.tgnet.TLRPC$PhoneCall r6 = r1.privateCall     // Catch:{ Exception -> 0x02d3 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneConnection> r6 = r6.connections     // Catch:{ Exception -> 0x02d3 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.Instance$Endpoint[] r15 = new org.telegram.messenger.voip.Instance.Endpoint[r6]     // Catch:{ Exception -> 0x02d3 }
            r14 = 0
        L_0x017c:
            if (r14 >= r6) goto L_0x01c8
            org.telegram.tgnet.TLRPC$PhoneCall r9 = r1.privateCall     // Catch:{ Exception -> 0x02d3 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneConnection> r9 = r9.connections     // Catch:{ Exception -> 0x02d3 }
            java.lang.Object r9 = r9.get(r14)     // Catch:{ Exception -> 0x02d3 }
            org.telegram.tgnet.TLRPC$PhoneConnection r9 = (org.telegram.tgnet.TLRPC$PhoneConnection) r9     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.Instance$Endpoint r22 = new org.telegram.messenger.voip.Instance$Endpoint     // Catch:{ Exception -> 0x02d3 }
            boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$TL_phoneConnectionWebrtc     // Catch:{ Exception -> 0x02d3 }
            long r11 = r9.id     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r13 = r9.ip     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r7 = r9.ipv6     // Catch:{ Exception -> 0x02d3 }
            int r4 = r9.port     // Catch:{ Exception -> 0x02d3 }
            byte[] r3 = r9.peer_tag     // Catch:{ Exception -> 0x02d3 }
            r26 = r6
            boolean r6 = r9.turn     // Catch:{ Exception -> 0x02d3 }
            r27 = r0
            boolean r0 = r9.stun     // Catch:{ Exception -> 0x02d3 }
            r29 = r5
            java.lang.String r5 = r9.username     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r9 = r9.password     // Catch:{ Exception -> 0x02d3 }
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
            r9.<init>(r10, r11, r13, r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ Exception -> 0x02d3 }
            r7[r30] = r22     // Catch:{ Exception -> 0x02d3 }
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
            org.telegram.messenger.voip.-$$Lambda$VoIPService$2n9wh10VvWKivmqLujW9wiucLXk r0 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$2n9wh10VvWKivmqLujW9wiucLXk     // Catch:{ Exception -> 0x02d3 }
            r0.<init>()     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x02d3 }
        L_0x01d7:
            java.lang.String r0 = "proxy_enabled"
            r2 = r29
            r3 = 0
            boolean r0 = r2.getBoolean(r0, r3)     // Catch:{ Exception -> 0x02d3 }
            if (r0 == 0) goto L_0x021f
            java.lang.String r0 = "proxy_enabled_calls"
            boolean r0 = r2.getBoolean(r0, r3)     // Catch:{ Exception -> 0x02d3 }
            if (r0 == 0) goto L_0x021f
            java.lang.String r0 = "proxy_ip"
            r3 = 0
            java.lang.String r0 = r2.getString(r0, r3)     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r4 = "proxy_secret"
            java.lang.String r4 = r2.getString(r4, r3)     // Catch:{ Exception -> 0x02d3 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x02d3 }
            if (r3 != 0) goto L_0x021f
            boolean r3 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x02d3 }
            if (r3 == 0) goto L_0x021f
            org.telegram.messenger.voip.Instance$Proxy r3 = new org.telegram.messenger.voip.Instance$Proxy     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r4 = "proxy_port"
            r5 = 0
            int r4 = r2.getInt(r4, r5)     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r5 = "proxy_user"
            r6 = 0
            java.lang.String r5 = r2.getString(r5, r6)     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r8 = "proxy_pass"
            java.lang.String r2 = r2.getString(r8, r6)     // Catch:{ Exception -> 0x02d3 }
            r3.<init>(r0, r4, r5, r2)     // Catch:{ Exception -> 0x02d3 }
            r30 = r3
            goto L_0x0222
        L_0x021f:
            r6 = 0
            r30 = r6
        L_0x0222:
            org.telegram.messenger.voip.Instance$EncryptionKey r0 = new org.telegram.messenger.voip.Instance$EncryptionKey     // Catch:{ Exception -> 0x02d3 }
            byte[] r2 = r1.authKey     // Catch:{ Exception -> 0x02d3 }
            boolean r3 = r1.isOutgoing     // Catch:{ Exception -> 0x02d3 }
            r0.<init>(r2, r3)     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r2 = "2.7.7"
            org.telegram.tgnet.TLRPC$PhoneCall r3 = r1.privateCall     // Catch:{ Exception -> 0x02d3 }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r3 = r3.protocol     // Catch:{ Exception -> 0x02d3 }
            java.util.ArrayList<java.lang.String> r3 = r3.library_versions     // Catch:{ Exception -> 0x02d3 }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x02d3 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x02d3 }
            int r2 = r2.compareTo(r3)     // Catch:{ Exception -> 0x02d3 }
            if (r2 > 0) goto L_0x0242
            r2 = 1
            goto L_0x0243
        L_0x0242:
            r2 = 0
        L_0x0243:
            long r3 = r1.videoCapturer     // Catch:{ Exception -> 0x02d3 }
            r5 = 0
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 == 0) goto L_0x0255
            if (r2 != 0) goto L_0x0255
            org.telegram.messenger.voip.NativeInstance.destroyVideoCapturer(r3)     // Catch:{ Exception -> 0x02d3 }
            r1.videoCapturer = r5     // Catch:{ Exception -> 0x02d3 }
            r3 = 0
            r1.videoState = r3     // Catch:{ Exception -> 0x02d3 }
        L_0x0255:
            org.telegram.tgnet.TLRPC$PhoneCall r3 = r1.privateCall     // Catch:{ Exception -> 0x02d3 }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r3 = r3.protocol     // Catch:{ Exception -> 0x02d3 }
            java.util.ArrayList<java.lang.String> r3 = r3.library_versions     // Catch:{ Exception -> 0x02d3 }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x02d3 }
            r26 = r3
            java.lang.String r26 = (java.lang.String) r26     // Catch:{ Exception -> 0x02d3 }
            int r31 = r36.getNetworkType()     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink r3 = r1.remoteSink     // Catch:{ Exception -> 0x02d3 }
            long r8 = r1.videoCapturer     // Catch:{ Exception -> 0x02d3 }
            r29 = r7
            r32 = r0
            r33 = r3
            r34 = r8
            org.telegram.messenger.voip.NativeInstance r0 = org.telegram.messenger.voip.Instance.makeInstance(r26, r27, r28, r29, r30, r31, r32, r33, r34)     // Catch:{ Exception -> 0x02d3 }
            r1.tgVoip = r0     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.-$$Lambda$Qeh5YABUJkCA-VgvENqapas9LlU r3 = new org.telegram.messenger.voip.-$$Lambda$Qeh5YABUJkCA-VgvENqapas9LlU     // Catch:{ Exception -> 0x02d3 }
            r3.<init>()     // Catch:{ Exception -> 0x02d3 }
            r0.setOnStateUpdatedListener(r3)     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.NativeInstance r0 = r1.tgVoip     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.-$$Lambda$nq07hMB6FzcXz1SuNu8qNNOnIFw r3 = new org.telegram.messenger.voip.-$$Lambda$nq07hMB6FzcXz1SuNu8qNNOnIFw     // Catch:{ Exception -> 0x02d3 }
            r3.<init>()     // Catch:{ Exception -> 0x02d3 }
            r0.setOnSignalBarsUpdatedListener(r3)     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.NativeInstance r0 = r1.tgVoip     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.-$$Lambda$HNfvWF9Xn3-zdFJ1-nzWv-_4Tj8 r3 = new org.telegram.messenger.voip.-$$Lambda$HNfvWF9Xn3-zdFJ1-nzWv-_4Tj8     // Catch:{ Exception -> 0x02d3 }
            r3.<init>()     // Catch:{ Exception -> 0x02d3 }
            r0.setOnSignalDataListener(r3)     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.NativeInstance r0 = r1.tgVoip     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.-$$Lambda$HTKGoi8h-oyhhoKW5NwxR_c0PRM r3 = new org.telegram.messenger.voip.-$$Lambda$HTKGoi8h-oyhhoKW5NwxR_c0PRM     // Catch:{ Exception -> 0x02d3 }
            r3.<init>()     // Catch:{ Exception -> 0x02d3 }
            r0.setOnRemoteMediaStateUpdatedListener(r3)     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.NativeInstance r0 = r1.tgVoip     // Catch:{ Exception -> 0x02d3 }
            boolean r3 = r1.micMute     // Catch:{ Exception -> 0x02d3 }
            r0.setMuteMicrophone(r3)     // Catch:{ Exception -> 0x02d3 }
            boolean r0 = r1.isVideoAvailable     // Catch:{ Exception -> 0x02d3 }
            if (r2 == r0) goto L_0x02c6
            r1.isVideoAvailable = r2     // Catch:{ Exception -> 0x02d3 }
            r3 = 0
        L_0x02ae:
            java.util.ArrayList<org.telegram.messenger.voip.VoIPBaseService$StateListener> r0 = r1.stateListeners     // Catch:{ Exception -> 0x02d3 }
            int r0 = r0.size()     // Catch:{ Exception -> 0x02d3 }
            if (r3 >= r0) goto L_0x02c6
            java.util.ArrayList<org.telegram.messenger.voip.VoIPBaseService$StateListener> r0 = r1.stateListeners     // Catch:{ Exception -> 0x02d3 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.VoIPBaseService$StateListener r0 = (org.telegram.messenger.voip.VoIPBaseService.StateListener) r0     // Catch:{ Exception -> 0x02d3 }
            boolean r2 = r1.isVideoAvailable     // Catch:{ Exception -> 0x02d3 }
            r0.onVideoAvailableChange(r2)     // Catch:{ Exception -> 0x02d3 }
            int r3 = r3 + 1
            goto L_0x02ae
        L_0x02c6:
            r1.videoCapturer = r5     // Catch:{ Exception -> 0x02d3 }
            org.telegram.messenger.voip.VoIPService$2 r0 = new org.telegram.messenger.voip.VoIPService$2     // Catch:{ Exception -> 0x02d3 }
            r0.<init>()     // Catch:{ Exception -> 0x02d3 }
            r2 = 5000(0x1388, double:2.4703E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r2)     // Catch:{ Exception -> 0x02d3 }
            goto L_0x02e0
        L_0x02d3:
            r0 = move-exception
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x02dd
            java.lang.String r2 = "error starting call"
            org.telegram.messenger.FileLog.e(r2, r0)
        L_0x02dd:
            r36.callFailed()
        L_0x02e0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.initiateActualEncryptedCall():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initiateActualEncryptedCall$37 */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$37$VoIPService() {
        Toast.makeText(this, "This call uses TCP which will degrade its quality.", 0).show();
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

    private void startConnectingSound() {
        int i = this.spPlayID;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        int play = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        this.spPlayID = play;
        if (play == 0) {
            AnonymousClass3 r0 = new Runnable() {
                public void run() {
                    if (VoIPBaseService.sharedInstance != null) {
                        VoIPService voIPService = VoIPService.this;
                        if (voIPService.spPlayID == 0) {
                            voIPService.spPlayID = voIPService.soundPool.play(voIPService.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
                        }
                        VoIPService voIPService2 = VoIPService.this;
                        if (voIPService2.spPlayID == 0) {
                            AndroidUtilities.runOnUIThread(this, 100);
                        } else {
                            voIPService2.connectingSoundRunnable = null;
                        }
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_sendSignalingData, $$Lambda$VoIPService$Cfd5Q5CD23jkengMqfF4cBoBKtc.INSTANCE);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, $$Lambda$VoIPService$0yqB0NU11RJQEfbpKRXpaloAMc.INSTANCE);
        }
        super.callFailed(str);
    }

    static /* synthetic */ void lambda$callFailed$39(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
            setMicMute(!isMicMute(), false);
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

    public void onConnectionStateChanged(int i) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPService.this.lambda$onConnectionStateChanged$40$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onConnectionStateChanged$40 */
    public /* synthetic */ void lambda$onConnectionStateChanged$40$VoIPService(int i) {
        if (i == 3 && this.callStartTime == 0) {
            this.callStartTime = SystemClock.elapsedRealtime();
        }
        super.onConnectionStateChanged(i);
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
                $$Lambda$VoIPService$DYO4rintqxCoEnv4pdXCvVeT5I r0 = new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$getConnectionAndStartCall$41$VoIPService();
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
    /* renamed from: lambda$getConnectionAndStartCall$41 */
    public /* synthetic */ void lambda$getConnectionAndStartCall$41$VoIPService() {
        this.delayedStartOutgoingCall = null;
        startOutgoingCall();
    }
}
