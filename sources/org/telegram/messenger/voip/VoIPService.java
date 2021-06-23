package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRouter;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.KeyEvent;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.messenger.voip.Instance;
import org.telegram.messenger.voip.NativeInstance;
import org.telegram.messenger.voip.VoIPController;
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
import org.telegram.tgnet.TLRPC$TL_chatFull;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCall;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideoSourceGroup;
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
import org.telegram.tgnet.TLRPC$TL_phone_editGroupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_phone_getCallConfig;
import org.telegram.tgnet.TLRPC$TL_phone_joinGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_joinGroupCallPresentation;
import org.telegram.tgnet.TLRPC$TL_phone_leaveGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_leaveGroupCallPresentation;
import org.telegram.tgnet.TLRPC$TL_phone_phoneCall;
import org.telegram.tgnet.TLRPC$TL_phone_receivedCall;
import org.telegram.tgnet.TLRPC$TL_phone_requestCall;
import org.telegram.tgnet.TLRPC$TL_phone_saveCallDebug;
import org.telegram.tgnet.TLRPC$TL_phone_sendSignalingData;
import org.telegram.tgnet.TLRPC$TL_speakingInGroupCallAction;
import org.telegram.tgnet.TLRPC$TL_updateGroupCall;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallConnection;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants;
import org.telegram.tgnet.TLRPC$TL_updatePhoneCallSignalingData;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$TL_upload_file;
import org.telegram.tgnet.TLRPC$TL_upload_getFile;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$messages_DhConfig;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPFeedbackActivity;
import org.telegram.ui.VoIPPermissionActivity;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;
import org.webrtc.voiceengine.WebRtcAudioTrack;

@SuppressLint({"NewApi"})
public class VoIPService extends Service implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, VoIPController.ConnectionStateListener, NotificationCenter.NotificationCenterDelegate {
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final int AUDIO_ROUTE_BLUETOOTH = 2;
    public static final int AUDIO_ROUTE_EARPIECE = 0;
    public static final int AUDIO_ROUTE_SPEAKER = 1;
    public static final int CALL_MIN_LAYER = 65;
    public static final int CAPTURE_DEVICE_CAMERA = 0;
    public static final int CAPTURE_DEVICE_SCREEN = 1;
    public static final int DISCARD_REASON_DISCONNECT = 2;
    public static final int DISCARD_REASON_HANGUP = 1;
    public static final int DISCARD_REASON_LINE_BUSY = 4;
    public static final int DISCARD_REASON_MISSED = 3;
    private static final int ID_INCOMING_CALL_NOTIFICATION = 202;
    private static final int ID_ONGOING_CALL_NOTIFICATION = 201;
    private static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    public static final int QUALITY_FULL = 2;
    public static final int QUALITY_MEDIUM = 1;
    public static final int QUALITY_SMALL = 0;
    public static final int STATE_BUSY = 17;
    public static final int STATE_CREATING = 6;
    public static final int STATE_ENDED = 11;
    public static final int STATE_ESTABLISHED = 3;
    public static final int STATE_EXCHANGING_KEYS = 12;
    public static final int STATE_FAILED = 4;
    public static final int STATE_HANGING_UP = 10;
    public static final int STATE_RECONNECTING = 5;
    public static final int STATE_REQUESTING = 14;
    public static final int STATE_RINGING = 16;
    public static final int STATE_WAITING = 13;
    public static final int STATE_WAITING_INCOMING = 15;
    public static final int STATE_WAIT_INIT = 1;
    public static final int STATE_WAIT_INIT_ACK = 2;
    /* access modifiers changed from: private */
    public static final boolean USE_CONNECTION_SERVICE = isDeviceCompatibleWithConnectionServiceAPI();
    public static NativeInstance.AudioLevelsCallback audioLevelsCallback;
    public static TLRPC$PhoneCall callIShouldHavePutIntoIntent;
    /* access modifiers changed from: private */
    public static Runnable setModeRunnable;
    /* access modifiers changed from: private */
    public static VoIPService sharedInstance;
    /* access modifiers changed from: private */
    public static final Object sync = new Object();
    private byte[] a_or_b;
    private Runnable afterSoundRunnable = new Runnable() {
        public void run() {
            AudioManager audioManager = (AudioManager) VoIPService.this.getSystemService("audio");
            audioManager.abandonAudioFocus(VoIPService.this);
            audioManager.unregisterMediaButtonEventReceiver(new ComponentName(VoIPService.this, VoIPMediaButtonReceiver.class));
            if (!VoIPService.USE_CONNECTION_SERVICE && VoIPService.sharedInstance == null) {
                if (VoIPService.this.isBtHeadsetConnected) {
                    audioManager.stopBluetoothSco();
                    audioManager.setBluetoothScoOn(false);
                    boolean unused = VoIPService.this.bluetoothScoActive = false;
                    boolean unused2 = VoIPService.this.bluetoothScoConnecting = false;
                }
                audioManager.setSpeakerphoneOn(false);
            }
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    VoIPService.AnonymousClass1.this.lambda$run$0$VoIPService$1();
                }
            });
            Utilities.globalQueue.postRunnable(VoIPService.setModeRunnable = new Runnable(audioManager) {
                public final /* synthetic */ AudioManager f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    VoIPService.AnonymousClass1.lambda$run$1(this.f$0);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$run$0 */
        public /* synthetic */ void lambda$run$0$VoIPService$1() {
            VoIPService.this.soundPool.release();
        }

        static /* synthetic */ void lambda$run$1(AudioManager audioManager) {
            synchronized (VoIPService.sync) {
                if (VoIPService.setModeRunnable != null) {
                    Runnable unused = VoIPService.setModeRunnable = null;
                    try {
                        audioManager.setMode(0);
                    } catch (SecurityException e) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("Error setting audio more to normal", e);
                        }
                    }
                }
            }
        }
    };
    private boolean audioConfigured;
    private int audioRouteToSet = 2;
    private byte[] authKey;
    /* access modifiers changed from: private */
    public boolean bluetoothScoActive;
    /* access modifiers changed from: private */
    public boolean bluetoothScoConnecting;
    /* access modifiers changed from: private */
    public BluetoothAdapter btAdapter;
    private int callDiscardReason;
    private int callReqId;
    private long callStartTime;
    private long[] captureDevice = new long[2];
    private TLRPC$Chat chat;
    private int checkRequestId;
    private int classGuid;
    /* access modifiers changed from: private */
    public Runnable connectingSoundRunnable;
    private PowerManager.WakeLock cpuWakelock;
    private boolean createGroupCall;
    /* access modifiers changed from: private */
    public int currentAccount = -1;
    private String[] currentBackgroundEndpointId = new String[2];
    private ProxyVideoSink[] currentBackgroundSink = new ProxyVideoSink[2];
    public String currentBluetoothDeviceName;
    public boolean currentGroupModeStreaming;
    private int currentState = 0;
    private int currentStreamRequestId;
    private long currentStreamRequestTimestamp;
    private Runnable delayedStartOutgoingCall;
    private boolean[] destroyCaptureDevice = {true, true};
    /* access modifiers changed from: private */
    public boolean didDeleteConnectionServiceContact;
    private boolean endCallAfterRequest;
    boolean fetchingBluetoothDeviceName;
    private boolean forceRating;
    private byte[] g_a;
    private byte[] g_a_hash;
    public ChatObject.Call groupCall;
    private TLRPC$InputPeer groupCallPeer;
    private boolean hasAudioFocus;
    public boolean hasFewPeers;
    /* access modifiers changed from: private */
    public boolean isBtHeadsetConnected;
    private boolean isFrontFaceCamera = true;
    /* access modifiers changed from: private */
    public boolean isHeadsetPlugged;
    private boolean isOutgoing;
    /* access modifiers changed from: private */
    public boolean isProximityNear;
    private boolean isVideoAvailable;
    private String joinHash;
    private long keyFingerprint;
    private String lastError;
    private NetworkInfo lastNetInfo;
    private long lastTypingTimeSend;
    private ProxyVideoSink[] localSink = new ProxyVideoSink[2];
    private Boolean mHasEarpiece;
    private boolean micMute;
    public boolean micSwitching;
    private TLRPC$TL_dataJSON myParams;
    private int[] mySource = new int[2];
    /* access modifiers changed from: private */
    public boolean needPlayEndSound;
    private boolean needRateCall;
    private boolean needSendDebugLog;
    /* access modifiers changed from: private */
    public boolean needSwitchToBluetoothAfterScoActivates;
    private boolean notificationsDisabled;
    private Runnable onDestroyRunnable;
    private ArrayList<TLRPC$PhoneCall> pendingUpdates = new ArrayList<>();
    private boolean playedConnectedSound;
    private boolean playingSound;
    private Instance.TrafficStats prevTrafficStats;
    /* access modifiers changed from: private */
    public int previousAudioOutput = -1;
    public TLRPC$PhoneCall privateCall;
    /* access modifiers changed from: private */
    public PowerManager.WakeLock proximityWakelock;
    /* access modifiers changed from: private */
    public final LruCache<String, ProxyVideoSink> proxyVideoSinkLruCache = new LruCache<String, ProxyVideoSink>(6) {
        /* access modifiers changed from: protected */
        public void entryRemoved(boolean z, String str, ProxyVideoSink proxyVideoSink, ProxyVideoSink proxyVideoSink2) {
            super.entryRemoved(z, str, proxyVideoSink, proxyVideoSink2);
            VoIPService.this.tgVoip[0].removeIncomingVideoOutput(proxyVideoSink.nativeInstance);
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean z = true;
            if ("android.intent.action.HEADSET_PLUG".equals(intent.getAction())) {
                boolean unused = VoIPService.this.isHeadsetPlugged = intent.getIntExtra("state", 0) == 1;
                if (VoIPService.this.isHeadsetPlugged && VoIPService.this.proximityWakelock != null && VoIPService.this.proximityWakelock.isHeld()) {
                    VoIPService.this.proximityWakelock.release();
                }
                if (VoIPService.this.isHeadsetPlugged) {
                    AudioManager audioManager = (AudioManager) VoIPService.this.getSystemService("audio");
                    if (audioManager.isSpeakerphoneOn()) {
                        int unused2 = VoIPService.this.previousAudioOutput = 0;
                    } else if (audioManager.isBluetoothScoOn()) {
                        int unused3 = VoIPService.this.previousAudioOutput = 2;
                    } else {
                        int unused4 = VoIPService.this.previousAudioOutput = 1;
                    }
                    VoIPService.this.setAudioOutput(1);
                } else if (VoIPService.this.previousAudioOutput >= 0) {
                    VoIPService voIPService = VoIPService.this;
                    voIPService.setAudioOutput(voIPService.previousAudioOutput);
                    int unused5 = VoIPService.this.previousAudioOutput = -1;
                }
                boolean unused6 = VoIPService.this.isProximityNear = false;
                VoIPService.this.updateOutputGainControlState();
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                VoIPService.this.updateNetworkType();
            } else if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("bt headset state = " + intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0));
                }
                VoIPService voIPService2 = VoIPService.this;
                if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) != 2) {
                    z = false;
                }
                voIPService2.updateBluetoothHeadsetState(z);
            } else if ("android.media.ACTION_SCO_AUDIO_STATE_UPDATED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Bluetooth SCO state updated: " + intExtra);
                }
                if (intExtra != 0 || !VoIPService.this.isBtHeadsetConnected || (VoIPService.this.btAdapter.isEnabled() && VoIPService.this.btAdapter.getProfileConnectionState(1) == 2)) {
                    boolean unused7 = VoIPService.this.bluetoothScoConnecting = intExtra == 2;
                    boolean unused8 = VoIPService.this.bluetoothScoActive = intExtra == 1;
                    if (VoIPService.this.bluetoothScoActive) {
                        VoIPService.this.fetchBluetoothDeviceName();
                        if (VoIPService.this.needSwitchToBluetoothAfterScoActivates) {
                            boolean unused9 = VoIPService.this.needSwitchToBluetoothAfterScoActivates = false;
                            AudioManager audioManager2 = (AudioManager) VoIPService.this.getSystemService("audio");
                            audioManager2.setSpeakerphoneOn(false);
                            audioManager2.setBluetoothScoOn(true);
                        }
                    }
                    Iterator it = VoIPService.this.stateListeners.iterator();
                    while (it.hasNext()) {
                        ((StateListener) it.next()).onAudioSettingsChanged();
                    }
                    return;
                }
                VoIPService.this.updateBluetoothHeadsetState(false);
            } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(intent.getStringExtra("state"))) {
                    VoIPService.this.hangUp();
                }
            } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                for (int i = 0; i < VoIPService.this.stateListeners.size(); i++) {
                    ((StateListener) VoIPService.this.stateListeners.get(i)).onScreenOnChange(true);
                }
            } else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                for (int i2 = 0; i2 < VoIPService.this.stateListeners.size(); i2++) {
                    ((StateListener) VoIPService.this.stateListeners.get(i2)).onScreenOnChange(false);
                }
            }
        }
    };
    private boolean reconnectScreenCapture;
    private int remoteAudioState = 1;
    private ProxyVideoSink[] remoteSink = new ProxyVideoSink[2];
    /* access modifiers changed from: private */
    public HashMap<String, ProxyVideoSink> remoteSinks = new HashMap<>();
    private int remoteVideoState = 0;
    private MediaPlayer ringtonePlayer;
    private int scheduleDate;
    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        public void onServiceDisconnected(int i) {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            Iterator<BluetoothDevice> it = bluetoothProfile.getConnectedDevices().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                BluetoothDevice next = it.next();
                if (bluetoothProfile.getConnectionState(next) == 2) {
                    VoIPService.this.currentBluetoothDeviceName = next.getName();
                    break;
                }
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(i, bluetoothProfile);
            VoIPService.this.fetchingBluetoothDeviceName = false;
        }
    };
    public final SharedUIParams sharedUIParams = new SharedUIParams();
    private Runnable shortPollRunnable;
    private int signalBarCount;
    /* access modifiers changed from: private */
    public SoundPool soundPool;
    private int spAllowTalkId;
    private int spBusyId;
    /* access modifiers changed from: private */
    public int spConnectingId;
    private int spEndId;
    private int spFailedID;
    /* access modifiers changed from: private */
    public int spPlayId;
    private int spRingbackID;
    private int spStartRecordId;
    private int spVoiceChatConnecting;
    private int spVoiceChatEndId;
    private int spVoiceChatStartId;
    private boolean speakerphoneStateToSet;
    private boolean startedRinging;
    /* access modifiers changed from: private */
    public ArrayList<StateListener> stateListeners = new ArrayList<>();
    private boolean switchingAccount;
    private boolean switchingCamera;
    private boolean switchingStream;
    private Runnable switchingStreamTimeoutRunnable;
    /* access modifiers changed from: private */
    public CallConnection systemCallConnection;
    /* access modifiers changed from: private */
    public NativeInstance[] tgVoip = new NativeInstance[2];
    private Runnable timeoutRunnable;
    private boolean unmutedByHold;
    private Runnable updateNotificationRunnable;
    private TLRPC$User user;
    private Vibrator vibrator;
    public boolean videoCall;
    private int[] videoState = {0, 0};
    /* access modifiers changed from: private */
    public final HashMap<String, TLRPC$TL_groupCallParticipant> waitingFrameParticipant = new HashMap<>();
    private boolean wasConnected;
    private boolean wasEstablished;

    public static class SharedUIParams {
        public boolean cameraAlertWasShowed;
        public boolean tapToVideoTooltipWasShowed;
        public boolean wasVideoCall;
    }

    public interface StateListener {

        /* renamed from: org.telegram.messenger.voip.VoIPService$StateListener$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onAudioSettingsChanged(StateListener stateListener) {
            }

            public static void $default$onCameraSwitch(StateListener stateListener, boolean z) {
            }

            public static void $default$onMediaStateUpdated(StateListener stateListener, int i, int i2) {
            }

            public static void $default$onScreenOnChange(StateListener stateListener, boolean z) {
            }

            public static void $default$onSignalBarsCountChanged(StateListener stateListener, int i) {
            }

            public static void $default$onStateChanged(StateListener stateListener, int i) {
            }

            public static void $default$onVideoAvailableChange(StateListener stateListener, boolean z) {
            }
        }

        void onAudioSettingsChanged();

        void onCameraSwitch(boolean z);

        void onMediaStateUpdated(int i, int i2);

        void onScreenOnChange(boolean z);

        void onSignalBarsCountChanged(int i);

        void onStateChanged(int i);

        void onVideoAvailableChange(boolean z);
    }

    static /* synthetic */ void lambda$createGroupInstance$36(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$onSignalingData$54(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    private void onTgVoipPreStop() {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isFrontFaceCamera() {
        return this.isFrontFaceCamera;
    }

    public void setMicMute(boolean z, boolean z2, boolean z3) {
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        if (this.micMute != z && !this.micSwitching) {
            this.micMute = z;
            ChatObject.Call call = this.groupCall;
            boolean z4 = true;
            if (call != null) {
                if (!z3 && (tLRPC$TL_groupCallParticipant = call.participants.get(getSelfId())) != null && tLRPC$TL_groupCallParticipant.muted && !tLRPC$TL_groupCallParticipant.can_self_unmute) {
                    z3 = true;
                }
                if (z3) {
                    editCallMember(UserConfig.getInstance(this.currentAccount).getCurrentUser(), Boolean.valueOf(z), (Boolean) null, (Integer) null, (Boolean) null, (Runnable) null);
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
            int i = 0;
            if (this.micMute || !z2) {
                z4 = false;
            }
            this.unmutedByHold = z4;
            while (true) {
                NativeInstance[] nativeInstanceArr = this.tgVoip;
                if (i >= nativeInstanceArr.length) {
                    break;
                }
                if (nativeInstanceArr[i] != null) {
                    nativeInstanceArr[i].setMuteMicrophone(z);
                }
                i++;
            }
            Iterator<StateListener> it = this.stateListeners.iterator();
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

    public boolean hasVideoCapturer() {
        return this.captureDevice[0] != 0;
    }

    public void checkVideoFrame(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, final boolean z) {
        final String str = z ? tLRPC$TL_groupCallParticipant.presentationEndpoint : tLRPC$TL_groupCallParticipant.videoEndpoint;
        if (str != null) {
            if (z && tLRPC$TL_groupCallParticipant.hasPresentationFrame != 0) {
                return;
            }
            if (!z && tLRPC$TL_groupCallParticipant.hasCameraFrame != 0) {
                return;
            }
            if (this.proxyVideoSinkLruCache.get(str) != null || (this.remoteSinks.get(str) != null && this.waitingFrameParticipant.get(str) == null)) {
                if (z) {
                    tLRPC$TL_groupCallParticipant.hasPresentationFrame = 2;
                } else {
                    tLRPC$TL_groupCallParticipant.hasCameraFrame = 2;
                }
            } else if (this.waitingFrameParticipant.containsKey(str)) {
                this.waitingFrameParticipant.put(str, tLRPC$TL_groupCallParticipant);
                if (z) {
                    tLRPC$TL_groupCallParticipant.hasPresentationFrame = 1;
                } else {
                    tLRPC$TL_groupCallParticipant.hasCameraFrame = 1;
                }
            } else {
                if (z) {
                    tLRPC$TL_groupCallParticipant.hasPresentationFrame = 1;
                } else {
                    tLRPC$TL_groupCallParticipant.hasCameraFrame = 1;
                }
                this.waitingFrameParticipant.put(str, tLRPC$TL_groupCallParticipant);
                addRemoteSink(tLRPC$TL_groupCallParticipant, z, new VideoSink() {
                    public /* synthetic */ void setParentSink(VideoSink videoSink) {
                        VideoSink.CC.$default$setParentSink(this, videoSink);
                    }

                    public void onFrame(VideoFrame videoFrame) {
                        if (videoFrame != null && videoFrame.getBuffer().getHeight() != 0 && videoFrame.getBuffer().getWidth() != 0) {
                            AndroidUtilities.runOnUIThread(new Runnable(str, this, z) {
                                public final /* synthetic */ String f$1;
                                public final /* synthetic */ VideoSink f$2;
                                public final /* synthetic */ boolean f$3;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                }

                                public final void run() {
                                    VoIPService.AnonymousClass5.this.lambda$onFrame$0$VoIPService$5(this.f$1, this.f$2, this.f$3);
                                }
                            });
                        }
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$onFrame$0 */
                    public /* synthetic */ void lambda$onFrame$0$VoIPService$5(String str, VideoSink videoSink, boolean z) {
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) VoIPService.this.waitingFrameParticipant.remove(str);
                        ProxyVideoSink proxyVideoSink = (ProxyVideoSink) VoIPService.this.remoteSinks.get(str);
                        if (proxyVideoSink != null && proxyVideoSink.target == videoSink) {
                            VoIPService.this.proxyVideoSinkLruCache.put(str, proxyVideoSink);
                            VoIPService.this.remoteSinks.remove(str);
                            proxyVideoSink.setTarget((VideoSink) null);
                        }
                        if (tLRPC$TL_groupCallParticipant != null) {
                            if (z) {
                                tLRPC$TL_groupCallParticipant.hasPresentationFrame = 2;
                            } else {
                                tLRPC$TL_groupCallParticipant.hasCameraFrame = 2;
                            }
                        }
                        ChatObject.Call call = VoIPService.this.groupCall;
                        if (call != null) {
                            call.updateVisibleParticipants();
                        }
                    }
                }, (VideoSink) null);
            }
        }
    }

    public void clearRemoteSinks() {
        this.proxyVideoSinkLruCache.evictAll();
    }

    public void setAudioRoute(int i) {
        if (i == 1) {
            setAudioOutput(0);
        } else if (i == 0) {
            setAudioOutput(1);
        } else if (i == 2) {
            setAudioOutput(2);
        }
    }

    public static class ProxyVideoSink implements VideoSink {
        private VideoSink background;
        /* access modifiers changed from: private */
        public long nativeInstance;
        /* access modifiers changed from: private */
        public VideoSink target;

        public /* synthetic */ void setParentSink(VideoSink videoSink) {
            VideoSink.CC.$default$setParentSink(this, videoSink);
        }

        public synchronized void onFrame(VideoFrame videoFrame) {
            VideoSink videoSink = this.target;
            if (videoSink != null) {
                videoSink.onFrame(videoFrame);
            }
            VideoSink videoSink2 = this.background;
            if (videoSink2 != null) {
                videoSink2.onFrame(videoFrame);
            }
        }

        public synchronized void setTarget(VideoSink videoSink) {
            VideoSink videoSink2 = this.target;
            if (videoSink2 != videoSink) {
                if (videoSink2 != null) {
                    videoSink2.setParentSink((VideoSink) null);
                }
                this.target = videoSink;
                if (videoSink != null) {
                    videoSink.setParentSink(this);
                }
            }
        }

        public synchronized void setBackground(VideoSink videoSink) {
            VideoSink videoSink2 = this.background;
            if (videoSink2 != null) {
                videoSink2.setParentSink((VideoSink) null);
            }
            this.background = videoSink;
            if (videoSink != null) {
                videoSink.setParentSink(this);
            }
        }

        public synchronized void removeTarget(VideoSink videoSink) {
            if (this.target == videoSink) {
                this.target = null;
            }
        }

        public synchronized void removeBackground(VideoSink videoSink) {
            if (this.background == videoSink) {
                this.background = null;
            }
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
        if (sharedInstance != null) {
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
                TLRPC$Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(intExtra3));
                this.chat = chat2;
                if (ChatObject.isChannel(chat2)) {
                    MessagesController.getInstance(this.currentAccount).startShortPoll(this.chat, this.classGuid, false);
                }
            }
            loadResources();
            int i3 = 0;
            while (true) {
                ProxyVideoSink[] proxyVideoSinkArr = this.localSink;
                if (i3 >= proxyVideoSinkArr.length) {
                    break;
                }
                proxyVideoSinkArr[i3] = new ProxyVideoSink();
                this.remoteSink[i3] = new ProxyVideoSink();
                i3++;
            }
            try {
                this.isHeadsetPlugged = ((AudioManager) getSystemService("audio")).isWiredHeadsetOn();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (this.chat == null || this.createGroupCall || MessagesController.getInstance(this.currentAccount).getGroupCall(this.chat.id, false) != null) {
                if (this.videoCall) {
                    if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.CAMERA") == 0) {
                        this.captureDevice[0] = NativeInstance.createVideoCapturer(this.localSink[0], this.isFrontFaceCamera ? 1 : 0);
                        if (intExtra3 != 0) {
                            this.videoState[0] = 1;
                        } else {
                            this.videoState[0] = 2;
                        }
                    } else {
                        this.videoState[0] = 1;
                    }
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
                sharedInstance = this;
                synchronized (sync) {
                    if (setModeRunnable != null) {
                        Utilities.globalQueue.cancelRunnable(setModeRunnable);
                        setModeRunnable = null;
                    }
                }
                if (this.isOutgoing) {
                    if (this.user != null) {
                        dispatchStateChanged(14);
                        if (USE_CONNECTION_SERVICE) {
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
                    if (z && !this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                        setAudioOutput(0);
                    }
                    callIShouldHavePutIntoIntent = null;
                    if (USE_CONNECTION_SERVICE) {
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

    public static VoIPService getSharedInstance() {
        return sharedInstance;
    }

    public TLRPC$User getUser() {
        return this.user;
    }

    public TLRPC$Chat getChat() {
        return this.chat;
    }

    public void setNoiseSupressionEnabled(boolean z) {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] != null) {
            nativeInstanceArr[0].setNoiseSuppressionEnabled(z);
        }
    }

    public void setGroupCallHash(String str) {
        if (this.currentGroupModeStreaming && !TextUtils.isEmpty(str) && !str.equals(this.joinHash)) {
            this.joinHash = str;
            createGroupInstance(0, false);
        }
    }

    public int getCallerId() {
        TLRPC$User tLRPC$User = this.user;
        if (tLRPC$User != null) {
            return tLRPC$User.id;
        }
        return -this.chat.id;
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
                        VoIPService.this.lambda$hangUp$3$VoIPService(tLObject, tLRPC$TL_error);
                    }
                });
                return;
            }
            TLRPC$TL_phone_leaveGroupCall tLRPC$TL_phone_leaveGroupCall = new TLRPC$TL_phone_leaveGroupCall();
            tLRPC$TL_phone_leaveGroupCall.call = this.groupCall.getInputGroupCall();
            tLRPC$TL_phone_leaveGroupCall.source = this.mySource[0];
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_leaveGroupCall, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$hangUp$4$VoIPService(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hangUp$3 */
    public /* synthetic */ void lambda$hangUp$3$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hangUp$4 */
    public /* synthetic */ void lambda$hangUp$4$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
        }
    }

    private void startOutgoingCall() {
        CallConnection callConnection;
        if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
            callConnection.setDialing();
        }
        configureDeviceForCall();
        showNotification();
        startConnectingSound();
        dispatchStateChanged(14);
        AndroidUtilities.runOnUIThread($$Lambda$VoIPService$IKnzf8sxArrwrmzKnrr4TwkiCqw.INSTANCE);
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
                VoIPService.this.lambda$startOutgoingCall$10$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOutgoingCall$10 */
    public /* synthetic */ void lambda$startOutgoingCall$10$VoIPService(MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    VoIPService.this.lambda$startOutgoingCall$9$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
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
    /* renamed from: lambda$startOutgoingCall$9 */
    public /* synthetic */ void lambda$startOutgoingCall$9$VoIPService(byte[] bArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                VoIPService.this.lambda$startOutgoingCall$8$VoIPService(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOutgoingCall$8 */
    public /* synthetic */ void lambda$startOutgoingCall$8$VoIPService(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, byte[] bArr) {
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
            $$Lambda$VoIPService$DOFjKb4Brg36cgmYyqUNMz4Ppiw r1 = new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$startOutgoingCall$7$VoIPService();
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
    /* renamed from: lambda$startOutgoingCall$7 */
    public /* synthetic */ void lambda$startOutgoingCall$7$VoIPService() {
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
                VoIPService.this.lambda$startOutgoingCall$6$VoIPService(tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startOutgoingCall$6 */
    public /* synthetic */ void lambda$startOutgoingCall$6$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    VoIPService.this.lambda$acknowledgeCall$12$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
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
    /* renamed from: lambda$acknowledgeCall$12 */
    public /* synthetic */ void lambda$acknowledgeCall$12$VoIPService(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                VoIPService.this.lambda$acknowledgeCall$11$VoIPService(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$acknowledgeCall$11 */
    public /* synthetic */ void lambda$acknowledgeCall$11$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, boolean z) {
        if (sharedInstance != null) {
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
            if (USE_CONNECTION_SERVICE) {
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

    private boolean isRinging() {
        return this.currentState == 15;
    }

    public boolean isJoined() {
        int i = this.currentState;
        return (i == 1 || i == 6) ? false : true;
    }

    public void requestVideoCall() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] != null) {
            nativeInstanceArr[0].setupOutgoingVideo(this.localSink[0], this.isFrontFaceCamera);
        }
    }

    public void switchCamera() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] == null || !nativeInstanceArr[0].hasVideoCapturer() || this.switchingCamera) {
            long[] jArr = this.captureDevice;
            if (jArr[0] != 0 && !this.switchingCamera) {
                NativeInstance.switchCameraCapturer(jArr[0], !this.isFrontFaceCamera);
                return;
            }
            return;
        }
        this.switchingCamera = true;
        this.tgVoip[0].switchCamera(!this.isFrontFaceCamera);
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createCaptureDevice(boolean r10) {
        /*
            r9 = this;
            r0 = 2
            if (r10 == 0) goto L_0x0005
            r1 = 2
            goto L_0x0007
        L_0x0005:
            boolean r1 = r9.isFrontFaceCamera
        L_0x0007:
            r2 = 0
            r4 = 1
            if (r10 != r4) goto L_0x0038
            long[] r5 = r9.captureDevice
            r6 = r5[r10]
            int r8 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r8 == 0) goto L_0x0015
            return
        L_0x0015:
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r2 = r9.localSink
            r2 = r2[r10]
            long r1 = org.telegram.messenger.voip.NativeInstance.createVideoCapturer(r2, r1)
            r5[r10] = r1
            r10 = 0
            r9.createGroupInstance(r4, r10)
            r9.setVideoState(r4, r0)
            int r0 = r9.currentAccount
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.groupCallScreencastStateChanged
            java.lang.Object[] r10 = new java.lang.Object[r10]
            r0.postNotificationName(r1, r10)
            goto L_0x0051
        L_0x0038:
            long[] r0 = r9.captureDevice
            r4 = r0[r10]
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 != 0) goto L_0x0052
            org.telegram.messenger.voip.NativeInstance[] r4 = r9.tgVoip
            r4 = r4[r10]
            if (r4 != 0) goto L_0x0047
            goto L_0x0052
        L_0x0047:
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r2 = r9.localSink
            r2 = r2[r10]
            long r1 = org.telegram.messenger.voip.NativeInstance.createVideoCapturer(r2, r1)
            r0[r10] = r1
        L_0x0051:
            return
        L_0x0052:
            org.telegram.messenger.voip.NativeInstance[] r1 = r9.tgVoip
            r4 = r1[r10]
            if (r4 == 0) goto L_0x0065
            r4 = r0[r10]
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 == 0) goto L_0x0065
            r1 = r1[r10]
            r2 = r0[r10]
            r1.activateVideoCapturer(r2)
        L_0x0065:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.createCaptureDevice(boolean):void");
    }

    /* JADX WARNING: type inference failed for: r12v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setupCaptureDevice(boolean r12, boolean r13) {
        /*
            r11 = this;
            long[] r0 = r11.captureDevice
            r1 = r0[r12]
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0059
            org.telegram.messenger.voip.NativeInstance[] r1 = r11.tgVoip
            r2 = r1[r12]
            if (r2 != 0) goto L_0x0011
            goto L_0x0059
        L_0x0011:
            r1 = r1[r12]
            r2 = r0[r12]
            r1.setupOutgoingVideoCreated(r2)
            boolean[] r0 = r11.destroyCaptureDevice
            r1 = 0
            r0[r12] = r1
            int[] r0 = r11.videoState
            r2 = 2
            r0[r12] = r2
            boolean r0 = r11.micMute
            r3 = 1
            if (r0 != r13) goto L_0x002e
            r0 = r13 ^ 1
            r11.setMicMute(r0, r1, r1)
            r11.micSwitching = r3
        L_0x002e:
            if (r12 != 0) goto L_0x0059
            org.telegram.messenger.ChatObject$Call r0 = r11.groupCall
            if (r0 == 0) goto L_0x0059
            int r0 = r11.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            org.telegram.tgnet.TLRPC$User r5 = r0.getCurrentUser()
            r13 = r13 ^ r3
            java.lang.Boolean r6 = java.lang.Boolean.valueOf(r13)
            int[] r13 = r11.videoState
            r12 = r13[r12]
            if (r12 == r2) goto L_0x004a
            r1 = 1
        L_0x004a:
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r1)
            r8 = 0
            r9 = 0
            org.telegram.messenger.voip.-$$Lambda$VoIPService$2tTekcYS4k24m1N7v0o3Fp2hEys r10 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$2tTekcYS4k24m1N7v0o3Fp2hEys
            r10.<init>()
            r4 = r11
            r4.editCallMember(r5, r6, r7, r8, r9, r10)
        L_0x0059:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.setupCaptureDevice(boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setupCaptureDevice$13 */
    public /* synthetic */ void lambda$setupCaptureDevice$13$VoIPService() {
        this.micSwitching = false;
    }

    /* JADX WARNING: type inference failed for: r13v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setVideoState(boolean r13, int r14) {
        /*
            r12 = this;
            org.telegram.messenger.voip.NativeInstance[] r0 = r12.tgVoip
            r1 = r0[r13]
            r2 = 0
            r4 = 2
            if (r1 != 0) goto L_0x003a
            long[] r0 = r12.captureDevice
            r5 = r0[r13]
            int r1 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x001d
            int[] r1 = r12.videoState
            r1[r13] = r14
            r2 = r0[r13]
            r13 = r1[r13]
            org.telegram.messenger.voip.NativeInstance.setVideoStateCapturer(r2, r13)
            goto L_0x0039
        L_0x001d:
            if (r14 != r4) goto L_0x0039
            int r14 = r12.currentState
            r1 = 17
            if (r14 == r1) goto L_0x0039
            r1 = 11
            if (r14 == r1) goto L_0x0039
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r14 = r12.localSink
            r14 = r14[r13]
            boolean r1 = r12.isFrontFaceCamera
            long r1 = org.telegram.messenger.voip.NativeInstance.createVideoCapturer(r14, r1)
            r0[r13] = r1
            int[] r14 = r12.videoState
            r14[r13] = r4
        L_0x0039:
            return
        L_0x003a:
            int[] r1 = r12.videoState
            r1[r13] = r14
            r14 = r0[r13]
            r0 = r1[r13]
            r14.setVideoState(r0)
            long[] r14 = r12.captureDevice
            r0 = r14[r13]
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x0056
            r0 = r14[r13]
            int[] r14 = r12.videoState
            r14 = r14[r13]
            org.telegram.messenger.voip.NativeInstance.setVideoStateCapturer(r0, r14)
        L_0x0056:
            if (r13 != 0) goto L_0x007a
            org.telegram.messenger.ChatObject$Call r14 = r12.groupCall
            if (r14 == 0) goto L_0x007a
            int r14 = r12.currentAccount
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)
            org.telegram.tgnet.TLRPC$User r6 = r14.getCurrentUser()
            r7 = 0
            int[] r14 = r12.videoState
            r0 = 0
            r14 = r14[r0]
            if (r14 == r4) goto L_0x006f
            r0 = 1
        L_0x006f:
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r0)
            r9 = 0
            r10 = 0
            r11 = 0
            r5 = r12
            r5.editCallMember(r6, r7, r8, r9, r10, r11)
        L_0x007a:
            if (r13 != 0) goto L_0x007f
            r12.checkIsNear()
        L_0x007f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.setVideoState(boolean, int):void");
    }

    public void stopScreenCapture() {
        if (this.groupCall != null && this.videoState[1] == 2) {
            TLRPC$TL_phone_leaveGroupCallPresentation tLRPC$TL_phone_leaveGroupCallPresentation = new TLRPC$TL_phone_leaveGroupCallPresentation();
            tLRPC$TL_phone_leaveGroupCallPresentation.call = this.groupCall.getInputGroupCall();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_leaveGroupCallPresentation, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$stopScreenCapture$14$VoIPService(tLObject, tLRPC$TL_error);
                }
            });
            NativeInstance nativeInstance = this.tgVoip[1];
            if (nativeInstance != null) {
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        NativeInstance.this.stopGroup();
                    }
                });
            }
            this.mySource[1] = 0;
            this.tgVoip[1] = null;
            this.destroyCaptureDevice[1] = true;
            this.captureDevice[1] = 0;
            this.videoState[1] = 0;
            AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.groupCallScreencastStateChanged, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$stopScreenCapture$14 */
    public /* synthetic */ void lambda$stopScreenCapture$14$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getVideoState(boolean r2) {
        /*
            r1 = this;
            int[] r0 = r1.videoState
            r2 = r0[r2]
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.getVideoState(boolean):int");
    }

    public void setSinks(VideoSink videoSink, VideoSink videoSink2) {
        setSinks(videoSink, false, videoSink2);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSinks(org.webrtc.VideoSink r2, boolean r3, org.webrtc.VideoSink r4) {
        /*
            r1 = this;
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r0 = r1.localSink
            r0 = r0[r3]
            r0.setTarget(r2)
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r2 = r1.remoteSink
            r2 = r2[r3]
            r2.setTarget(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.setSinks(org.webrtc.VideoSink, boolean, org.webrtc.VideoSink):void");
    }

    public void setLocalSink(VideoSink videoSink, boolean z) {
        if (!z) {
            this.localSink[0].setTarget(videoSink);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setRemoteSink(org.webrtc.VideoSink r2, boolean r3) {
        /*
            r1 = this;
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r0 = r1.remoteSink
            r3 = r0[r3]
            r3.setTarget(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.setRemoteSink(org.webrtc.VideoSink, boolean):void");
    }

    public ProxyVideoSink addRemoteSink(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z, VideoSink videoSink, VideoSink videoSink2) {
        if (this.tgVoip[0] == null) {
            return null;
        }
        String str = z ? tLRPC$TL_groupCallParticipant.presentationEndpoint : tLRPC$TL_groupCallParticipant.videoEndpoint;
        if (str == null) {
            return null;
        }
        ProxyVideoSink proxyVideoSink = this.remoteSinks.get(str);
        if (proxyVideoSink != null && proxyVideoSink.target == videoSink) {
            return proxyVideoSink;
        }
        if (proxyVideoSink == null) {
            proxyVideoSink = this.proxyVideoSinkLruCache.remove(str);
        }
        if (proxyVideoSink == null) {
            proxyVideoSink = new ProxyVideoSink();
        }
        if (videoSink != null) {
            proxyVideoSink.setTarget(videoSink);
        }
        if (videoSink2 != null) {
            proxyVideoSink.setBackground(videoSink2);
        }
        this.remoteSinks.put(str, proxyVideoSink);
        long unused = proxyVideoSink.nativeInstance = this.tgVoip[0].addIncomingVideoOutput(1, str, createSsrcGroups(z ? tLRPC$TL_groupCallParticipant.presentation : tLRPC$TL_groupCallParticipant.video), proxyVideoSink);
        return proxyVideoSink;
    }

    private NativeInstance.SsrcGroup[] createSsrcGroups(TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo) {
        if (tLRPC$TL_groupCallParticipantVideo.source_groups.isEmpty()) {
            return null;
        }
        int size = tLRPC$TL_groupCallParticipantVideo.source_groups.size();
        NativeInstance.SsrcGroup[] ssrcGroupArr = new NativeInstance.SsrcGroup[size];
        for (int i = 0; i < size; i++) {
            ssrcGroupArr[i] = new NativeInstance.SsrcGroup();
            TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup = tLRPC$TL_groupCallParticipantVideo.source_groups.get(i);
            ssrcGroupArr[i].semantics = tLRPC$TL_groupCallParticipantVideoSourceGroup.semantics;
            ssrcGroupArr[i].ssrcs = new int[tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.size()];
            for (int i2 = 0; i2 < ssrcGroupArr[i].ssrcs.length; i2++) {
                ssrcGroupArr[i].ssrcs[i2] = tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.get(i2).intValue();
            }
        }
        return ssrcGroupArr;
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestFullScreen(org.telegram.tgnet.TLRPC$TL_groupCallParticipant r4, boolean r5) {
        /*
            r3 = this;
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r0 = r3.currentBackgroundSink
            r1 = r0[r5]
            r2 = 0
            if (r1 == 0) goto L_0x000c
            r0 = r0[r5]
            r0.setBackground(r2)
        L_0x000c:
            if (r4 != 0) goto L_0x0017
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r4 = r3.currentBackgroundSink
            r4[r5] = r2
            java.lang.String[] r4 = r3.currentBackgroundEndpointId
            r4[r5] = r2
            return
        L_0x0017:
            if (r5 == 0) goto L_0x001c
            java.lang.String r0 = r4.presentationEndpoint
            goto L_0x001e
        L_0x001c:
            java.lang.String r0 = r4.videoEndpoint
        L_0x001e:
            if (r0 != 0) goto L_0x0021
            return
        L_0x0021:
            java.util.HashMap<java.lang.String, org.telegram.messenger.voip.VoIPService$ProxyVideoSink> r1 = r3.remoteSinks
            java.lang.Object r1 = r1.get(r0)
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink r1 = (org.telegram.messenger.voip.VoIPService.ProxyVideoSink) r1
            if (r1 != 0) goto L_0x002f
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink r1 = r3.addRemoteSink(r4, r5, r2, r2)
        L_0x002f:
            if (r1 == 0) goto L_0x0041
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r4 = r3.remoteSink
            r4 = r4[r5]
            r1.setBackground(r4)
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r4 = r3.currentBackgroundSink
            r4[r5] = r1
            java.lang.String[] r4 = r3.currentBackgroundEndpointId
            r4[r5] = r0
            goto L_0x0049
        L_0x0041:
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r4 = r3.currentBackgroundSink
            r4[r5] = r2
            java.lang.String[] r4 = r3.currentBackgroundEndpointId
            r4[r5] = r2
        L_0x0049:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.requestFullScreen(org.telegram.tgnet.TLRPC$TL_groupCallParticipant, boolean):void");
    }

    public void removeRemoteSink(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z) {
        if (z) {
            ProxyVideoSink remove = this.remoteSinks.remove(tLRPC$TL_groupCallParticipant.presentationEndpoint);
            if (remove != null) {
                this.tgVoip[0].removeIncomingVideoOutput(remove.nativeInstance);
                return;
            }
            return;
        }
        ProxyVideoSink remove2 = this.remoteSinks.remove(tLRPC$TL_groupCallParticipant.videoEndpoint);
        if (remove2 != null) {
            this.tgVoip[0].removeIncomingVideoOutput(remove2.nativeInstance);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isFullscreen(org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2, boolean r3) {
        /*
            r1 = this;
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r0 = r1.currentBackgroundSink
            r0 = r0[r3]
            if (r0 == 0) goto L_0x0019
            java.lang.String[] r0 = r1.currentBackgroundEndpointId
            r0 = r0[r3]
            if (r3 == 0) goto L_0x000f
            java.lang.String r2 = r2.presentationEndpoint
            goto L_0x0011
        L_0x000f:
            java.lang.String r2 = r2.videoEndpoint
        L_0x0011:
            boolean r2 = android.text.TextUtils.equals(r0, r2)
            if (r2 == 0) goto L_0x0019
            r2 = 1
            goto L_0x001a
        L_0x0019:
            r2 = 0
        L_0x001a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.isFullscreen(org.telegram.tgnet.TLRPC$TL_groupCallParticipant, boolean):boolean");
    }

    public void setBackgroundSinks(VideoSink videoSink, VideoSink videoSink2) {
        this.localSink[0].setBackground(videoSink);
        this.remoteSink[0].setBackground(videoSink2);
    }

    public void swapSinks() {
        this.localSink[0].swap();
        this.remoteSink[0].swap();
    }

    public boolean isHangingUp() {
        return this.currentState == 10;
    }

    public void onSignalingData(TLRPC$TL_updatePhoneCallSignalingData tLRPC$TL_updatePhoneCallSignalingData) {
        if (this.user != null) {
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            if (nativeInstanceArr[0] != null && !nativeInstanceArr[0].isGroup() && getCallID() == tLRPC$TL_updatePhoneCallSignalingData.phone_call_id) {
                this.tgVoip[0].onSignalingDataReceive(tLRPC$TL_updatePhoneCallSignalingData.data);
            }
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
                    if (i2 != 0 && i2 == this.mySource[0]) {
                        int i3 = 0;
                        for (int i4 = 0; i4 < size; i4++) {
                            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = tLRPC$TL_updateGroupCallParticipants.participants.get(i4);
                            if (tLRPC$TL_groupCallParticipant2.self || tLRPC$TL_groupCallParticipant2.source == this.mySource[0]) {
                                i3++;
                            }
                        }
                        if (i3 > 1) {
                            hangUp(2);
                            return;
                        }
                    }
                } else if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == selfId) {
                    int i5 = tLRPC$TL_groupCallParticipant.source;
                    int[] iArr = this.mySource;
                    if (i5 == iArr[0] || iArr[0] == 0 || i5 == 0) {
                        if (ChatObject.isChannel(this.chat) && this.currentGroupModeStreaming && tLRPC$TL_groupCallParticipant.can_self_unmute) {
                            this.switchingStream = true;
                            createGroupInstance(0, false);
                        }
                        if (tLRPC$TL_groupCallParticipant.muted) {
                            setMicMute(true, false, false);
                        }
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("source mismatch my = " + this.mySource[0] + " psrc = " + tLRPC$TL_groupCallParticipant.source);
                        }
                        hangUp(2);
                        return;
                    }
                } else {
                    continue;
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onGroupCallUpdated(org.telegram.tgnet.TLRPC$GroupCall r6) {
        /*
            r5 = this;
            org.telegram.tgnet.TLRPC$Chat r0 = r5.chat
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.messenger.ChatObject$Call r0 = r5.groupCall
            if (r0 == 0) goto L_0x006b
            org.telegram.tgnet.TLRPC$GroupCall r0 = r0.call
            long r1 = r0.id
            long r3 = r6.id
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x0014
            goto L_0x006b
        L_0x0014:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_groupCallDiscarded
            r0 = 2
            if (r6 == 0) goto L_0x001d
            r5.hangUp((int) r0)
            return
        L_0x001d:
            org.telegram.tgnet.TLRPC$TL_dataJSON r6 = r5.myParams
            r1 = 0
            if (r6 == 0) goto L_0x0036
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ Exception -> 0x0032 }
            org.telegram.tgnet.TLRPC$TL_dataJSON r2 = r5.myParams     // Catch:{ Exception -> 0x0032 }
            java.lang.String r2 = r2.data     // Catch:{ Exception -> 0x0032 }
            r6.<init>(r2)     // Catch:{ Exception -> 0x0032 }
            java.lang.String r2 = "stream"
            boolean r6 = r6.optBoolean(r2)     // Catch:{ Exception -> 0x0032 }
            goto L_0x0037
        L_0x0032:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x0036:
            r6 = 0
        L_0x0037:
            int r2 = r5.currentState
            r3 = 1
            if (r2 == r3) goto L_0x0040
            boolean r2 = r5.currentGroupModeStreaming
            if (r6 == r2) goto L_0x006b
        L_0x0040:
            org.telegram.tgnet.TLRPC$TL_dataJSON r2 = r5.myParams
            if (r2 == 0) goto L_0x006b
            boolean r4 = r5.playedConnectedSound
            if (r4 == 0) goto L_0x004e
            boolean r4 = r5.currentGroupModeStreaming
            if (r6 == r4) goto L_0x004e
            r5.switchingStream = r3
        L_0x004e:
            r5.currentGroupModeStreaming = r6
            if (r6 == 0) goto L_0x005a
            org.telegram.messenger.voip.NativeInstance[] r6 = r5.tgVoip     // Catch:{ Exception -> 0x0067 }
            r6 = r6[r1]     // Catch:{ Exception -> 0x0067 }
            r6.prepareForStream()     // Catch:{ Exception -> 0x0067 }
            goto L_0x0063
        L_0x005a:
            org.telegram.messenger.voip.NativeInstance[] r6 = r5.tgVoip     // Catch:{ Exception -> 0x0067 }
            r6 = r6[r1]     // Catch:{ Exception -> 0x0067 }
            java.lang.String r1 = r2.data     // Catch:{ Exception -> 0x0067 }
            r6.setJoinResponsePayload(r1)     // Catch:{ Exception -> 0x0067 }
        L_0x0063:
            r5.dispatchStateChanged(r0)     // Catch:{ Exception -> 0x0067 }
            goto L_0x006b
        L_0x0067:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x006b:
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
            org.telegram.messenger.voip.-$$Lambda$VoIPService$3RuNawtNj9PrhzClZqa2OgSz8KM r0 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$3RuNawtNj9PrhzClZqa2OgSz8KM
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
            org.telegram.messenger.voip.-$$Lambda$VoIPService$HH-CiFA5TnOTya-PTE3AbAPlpP8 r1 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$HH-CiFA5TnOTya-PTE3AbAPlpP8
            r1.<init>()
            r7.postRunnable(r1)
            java.lang.Runnable r7 = r6.timeoutRunnable
            if (r7 == 0) goto L_0x01af
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r7)
            r6.timeoutRunnable = r0
        L_0x01af:
            org.telegram.messenger.voip.-$$Lambda$VoIPService$y2hkS0FnRKij3_B1BEOyfgZvzgs r7 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$y2hkS0FnRKij3_B1BEOyfgZvzgs
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
    /* renamed from: lambda$onCallUpdated$15 */
    public /* synthetic */ void lambda$onCallUpdated$15$VoIPService() {
        this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCallUpdated$16 */
    public /* synthetic */ void lambda$onCallUpdated$16$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.spRingbackID, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCallUpdated$17 */
    public /* synthetic */ void lambda$onCallUpdated$17$VoIPService() {
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
                    VoIPService.this.lambda$processAcceptedCall$19$VoIPService(tLObject, tLRPC$TL_error);
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
                VoIPService.this.lambda$processAcceptedCall$19$VoIPService(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processAcceptedCall$19 */
    public /* synthetic */ void lambda$processAcceptedCall$19$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPService.this.lambda$processAcceptedCall$18$VoIPService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processAcceptedCall$18 */
    public /* synthetic */ void lambda$processAcceptedCall$18$VoIPService(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
            createGroupInstance(0, true);
            if (this.videoState[1] == 2) {
                createGroupInstance(1, true);
            }
        }
    }

    private void startGroupCall(int i, String str, boolean z) {
        if (sharedInstance == this) {
            boolean z2 = true;
            if (this.createGroupCall) {
                ChatObject.Call call = new ChatObject.Call();
                this.groupCall = call;
                call.call = new TLRPC$TL_groupCall();
                ChatObject.Call call2 = this.groupCall;
                TLRPC$GroupCall tLRPC$GroupCall = call2.call;
                tLRPC$GroupCall.participants_count = 0;
                tLRPC$GroupCall.version = 1;
                tLRPC$GroupCall.can_start_video = true;
                tLRPC$GroupCall.can_change_join_muted = true;
                call2.chatId = this.chat.id;
                call2.currentAccount = AccountInstance.getInstance(this.currentAccount);
                this.groupCall.setSelfPeer(this.groupCallPeer);
                this.groupCall.createNoVideoParticipant();
                dispatchStateChanged(6);
                TLRPC$TL_phone_createGroupCall tLRPC$TL_phone_createGroupCall = new TLRPC$TL_phone_createGroupCall();
                tLRPC$TL_phone_createGroupCall.peer = MessagesController.getInputPeer(this.chat);
                tLRPC$TL_phone_createGroupCall.random_id = Utilities.random.nextInt();
                int i2 = this.scheduleDate;
                if (i2 != 0) {
                    tLRPC$TL_phone_createGroupCall.schedule_date = i2;
                    tLRPC$TL_phone_createGroupCall.flags |= 2;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_createGroupCall, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        VoIPService.this.lambda$startGroupCall$22$VoIPService(tLObject, tLRPC$TL_error);
                    }
                }, 2);
                this.createGroupCall = false;
            } else if (str == null) {
                if (this.groupCall == null) {
                    ChatObject.Call groupCall2 = MessagesController.getInstance(this.currentAccount).getGroupCall(this.chat.id, false);
                    this.groupCall = groupCall2;
                    if (groupCall2 != null) {
                        groupCall2.setSelfPeer(this.groupCallPeer);
                    }
                }
                configureDeviceForCall();
                showNotification();
                AndroidUtilities.runOnUIThread($$Lambda$VoIPService$vKtXr5ZQokjqtyylu7tpLMVXdI.INSTANCE);
                createGroupInstance(0, false);
            } else if (getSharedInstance() != null && this.groupCall != null) {
                dispatchStateChanged(1);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("initital source = " + i);
                }
                TLRPC$TL_phone_joinGroupCall tLRPC$TL_phone_joinGroupCall = new TLRPC$TL_phone_joinGroupCall();
                tLRPC$TL_phone_joinGroupCall.muted = true;
                if (this.videoState[0] == 2) {
                    z2 = false;
                }
                tLRPC$TL_phone_joinGroupCall.video_stopped = z2;
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_joinGroupCall, new RequestDelegate(i, z) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        VoIPService.this.lambda$startGroupCall$28$VoIPService(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCall$22 */
    public /* synthetic */ void lambda$startGroupCall$22$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                            VoIPService.this.lambda$startGroupCall$20$VoIPService(this.f$1);
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
                VoIPService.this.lambda$startGroupCall$21$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCall$20 */
    public /* synthetic */ void lambda$startGroupCall$20$VoIPService(TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall) {
        if (sharedInstance != null) {
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
    /* renamed from: lambda$startGroupCall$21 */
    public /* synthetic */ void lambda$startGroupCall$21$VoIPService(TLRPC$TL_error tLRPC$TL_error) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, tLRPC$TL_error.text);
        hangUp(0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCall$24 */
    public /* synthetic */ void lambda$startGroupCall$24$VoIPService(int i) {
        this.mySource[0] = i;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCall$28 */
    public /* synthetic */ void lambda$startGroupCall$28$VoIPService(int i, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VoIPService.this.lambda$startGroupCall$24$VoIPService(this.f$1);
                }
            });
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            int selfId = getSelfId();
            int size = tLRPC$Updates.updates.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i2);
                if (tLRPC$Update instanceof TLRPC$TL_updateGroupCallParticipants) {
                    TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants = (TLRPC$TL_updateGroupCallParticipants) tLRPC$Update;
                    int size2 = tLRPC$TL_updateGroupCallParticipants.participants.size();
                    int i3 = 0;
                    while (true) {
                        if (i3 >= size2) {
                            break;
                        }
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_updateGroupCallParticipants.participants.get(i3);
                        if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == selfId) {
                            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_groupCallParticipant) {
                                public final /* synthetic */ TLRPC$TL_groupCallParticipant f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    VoIPService.this.lambda$startGroupCall$25$VoIPService(this.f$1);
                                }
                            });
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("join source = " + tLRPC$TL_groupCallParticipant.source);
                            }
                        } else {
                            i3++;
                        }
                    }
                } else if (tLRPC$Update instanceof TLRPC$TL_updateGroupCallConnection) {
                    TLRPC$TL_updateGroupCallConnection tLRPC$TL_updateGroupCallConnection = (TLRPC$TL_updateGroupCallConnection) tLRPC$Update;
                    if (!tLRPC$TL_updateGroupCallConnection.presentation) {
                        this.myParams = tLRPC$TL_updateGroupCallConnection.params;
                    }
                }
            }
            MessagesController.getInstance(this.currentAccount).processUpdates(tLRPC$Updates, false);
            AndroidUtilities.runOnUIThread(new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VoIPService.this.lambda$startGroupCall$26$VoIPService(this.f$1);
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
                VoIPService.this.lambda$startGroupCall$27$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCall$25 */
    public /* synthetic */ void lambda$startGroupCall$25$VoIPService(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant) {
        this.mySource[0] = tLRPC$TL_groupCallParticipant.source;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCall$26 */
    public /* synthetic */ void lambda$startGroupCall$26$VoIPService(boolean z) {
        this.groupCall.loadMembers(z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCall$27 */
    public /* synthetic */ void lambda$startGroupCall$27$VoIPService(TLRPC$TL_error tLRPC$TL_error) {
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
            createGroupInstance(0, false);
        } else {
            if ("GROUPCALL_INVALID".equals(tLRPC$TL_error.text)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat.id, 0, true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, tLRPC$TL_error.text);
            hangUp(0);
        }
    }

    private void startScreenCapture(String str) {
        if (getSharedInstance() != null && this.groupCall != null) {
            this.mySource[1] = 0;
            TLRPC$TL_phone_joinGroupCallPresentation tLRPC$TL_phone_joinGroupCallPresentation = new TLRPC$TL_phone_joinGroupCallPresentation();
            tLRPC$TL_phone_joinGroupCallPresentation.call = this.groupCall.getInputGroupCall();
            TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
            tLRPC$TL_phone_joinGroupCallPresentation.params = tLRPC$TL_dataJSON;
            tLRPC$TL_dataJSON.data = str;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_joinGroupCallPresentation, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$startScreenCapture$31$VoIPService(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startScreenCapture$31 */
    public /* synthetic */ void lambda$startScreenCapture$31$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Updates) {
                public final /* synthetic */ TLRPC$Updates f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VoIPService.this.lambda$startScreenCapture$29$VoIPService(this.f$1);
                }
            });
            MessagesController.getInstance(this.currentAccount).processUpdates(tLRPC$Updates, false);
            startGroupCheckShortpoll();
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPService.this.lambda$startScreenCapture$30$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startScreenCapture$29 */
    public /* synthetic */ void lambda$startScreenCapture$29$VoIPService(TLRPC$Updates tLRPC$Updates) {
        if (this.tgVoip[1] != null) {
            int selfId = getSelfId();
            int size = tLRPC$Updates.updates.size();
            for (int i = 0; i < size; i++) {
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateGroupCallConnection) {
                    TLRPC$TL_updateGroupCallConnection tLRPC$TL_updateGroupCallConnection = (TLRPC$TL_updateGroupCallConnection) tLRPC$Update;
                    if (tLRPC$TL_updateGroupCallConnection.presentation) {
                        this.tgVoip[1].setJoinResponsePayload(tLRPC$TL_updateGroupCallConnection.params.data);
                    }
                } else if (tLRPC$Update instanceof TLRPC$TL_updateGroupCallParticipants) {
                    TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants = (TLRPC$TL_updateGroupCallParticipants) tLRPC$Update;
                    int size2 = tLRPC$TL_updateGroupCallParticipants.participants.size();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= size2) {
                            break;
                        }
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_updateGroupCallParticipants.participants.get(i2);
                        if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == selfId) {
                            TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo = tLRPC$TL_groupCallParticipant.presentation;
                            if (tLRPC$TL_groupCallParticipantVideo != null) {
                                int size3 = tLRPC$TL_groupCallParticipantVideo.source_groups.size();
                                for (int i3 = 0; i3 < size3; i3++) {
                                    TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup = tLRPC$TL_groupCallParticipant.presentation.source_groups.get(i3);
                                    if (tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.size() > 0) {
                                        this.mySource[1] = tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.get(0).intValue();
                                    }
                                }
                            }
                        } else {
                            i2++;
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startScreenCapture$30 */
    public /* synthetic */ void lambda$startScreenCapture$30$VoIPService(TLRPC$TL_error tLRPC$TL_error) {
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
            createGroupInstance(1, false);
        } else if ("GROUPCALL_INVALID".equals(tLRPC$TL_error.text)) {
            MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat.id, 0, true);
        }
    }

    private void startGroupCheckShortpoll() {
        if (this.shortPollRunnable == null && sharedInstance != null && this.groupCall != null) {
            int[] iArr = this.mySource;
            if (iArr[0] != 0 || iArr[1] != 0) {
                $$Lambda$VoIPService$9AX04tF8kozf8H9Fv78ngM96Y0 r0 = new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$startGroupCheckShortpoll$34$VoIPService();
                    }
                };
                this.shortPollRunnable = r0;
                AndroidUtilities.runOnUIThread(r0, 4000);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCheckShortpoll$34 */
    public /* synthetic */ void lambda$startGroupCheckShortpoll$34$VoIPService() {
        if (this.shortPollRunnable != null && sharedInstance != null && this.groupCall != null) {
            int[] iArr = this.mySource;
            int i = 0;
            if (iArr[0] != 0 || iArr[1] != 0) {
                TLRPC$TL_phone_checkGroupCall tLRPC$TL_phone_checkGroupCall = new TLRPC$TL_phone_checkGroupCall();
                tLRPC$TL_phone_checkGroupCall.call = this.groupCall.getInputGroupCall();
                while (true) {
                    int[] iArr2 = this.mySource;
                    if (i < iArr2.length) {
                        if (iArr2[i] != 0) {
                            tLRPC$TL_phone_checkGroupCall.sources.add(Integer.valueOf(iArr2[i]));
                        }
                        i++;
                    } else {
                        this.checkRequestId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_checkGroupCall, new RequestDelegate(tLRPC$TL_phone_checkGroupCall) {
                            public final /* synthetic */ TLRPC$TL_phone_checkGroupCall f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                VoIPService.this.lambda$startGroupCheckShortpoll$33$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
                            }
                        });
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCheckShortpoll$33 */
    public /* synthetic */ void lambda$startGroupCheckShortpoll$33$VoIPService(TLRPC$TL_phone_checkGroupCall tLRPC$TL_phone_checkGroupCall, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$TL_phone_checkGroupCall, tLRPC$TL_error) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ TLRPC$TL_phone_checkGroupCall f$2;
            public final /* synthetic */ TLRPC$TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                VoIPService.this.lambda$startGroupCheckShortpoll$32$VoIPService(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startGroupCheckShortpoll$32 */
    public /* synthetic */ void lambda$startGroupCheckShortpoll$32$VoIPService(TLObject tLObject, TLRPC$TL_phone_checkGroupCall tLRPC$TL_phone_checkGroupCall, TLRPC$TL_error tLRPC$TL_error) {
        boolean z;
        boolean z2;
        if (this.shortPollRunnable != null && sharedInstance != null && this.groupCall != null) {
            this.shortPollRunnable = null;
            this.checkRequestId = 0;
            if (tLObject instanceof TLRPC$Vector) {
                TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
                int[] iArr = this.mySource;
                z = iArr[0] != 0 && tLRPC$TL_phone_checkGroupCall.sources.contains(Integer.valueOf(iArr[0])) && !tLRPC$Vector.objects.contains(Integer.valueOf(this.mySource[0]));
                int[] iArr2 = this.mySource;
                z2 = iArr2[1] != 0 && tLRPC$TL_phone_checkGroupCall.sources.contains(Integer.valueOf(iArr2[1])) && !tLRPC$Vector.objects.contains(Integer.valueOf(this.mySource[1]));
            } else if (tLRPC$TL_error == null || tLRPC$TL_error.code != 400) {
                z2 = false;
                z = false;
            } else {
                int[] iArr3 = this.mySource;
                z2 = iArr3[1] != 0 && tLRPC$TL_phone_checkGroupCall.sources.contains(Integer.valueOf(iArr3[1]));
                z = true;
            }
            if (z) {
                createGroupInstance(0, false);
            }
            if (z2) {
                createGroupInstance(1, false);
            }
            int[] iArr4 = this.mySource;
            if (iArr4[1] != 0 || iArr4[0] != 0) {
                startGroupCheckShortpoll();
            }
        }
    }

    private void cancelGroupCheckShortPoll() {
        int[] iArr = this.mySource;
        if (iArr[1] == 0 && iArr[0] == 0) {
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
    }

    private static class RequestedParticipant {
        public int audioSsrc;
        public TLRPC$TL_groupCallParticipant participant;

        public RequestedParticipant(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant) {
            this.participant = tLRPC$TL_groupCallParticipant;
            this.audioSsrc = tLRPC$TL_groupCallParticipant.source;
        }
    }

    private void broadcastUnknownParticipants(long j, int[] iArr) {
        if (this.groupCall != null && this.tgVoip[0] != null) {
            int selfId = getSelfId();
            ArrayList arrayList = null;
            int length = iArr.length;
            for (int i = 0; i < length; i++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.groupCall.participantsBySources.get(iArr[i]);
                if (tLRPC$TL_groupCallParticipant == null && (tLRPC$TL_groupCallParticipant = this.groupCall.participantsByVideoSources.get(iArr[i])) == null) {
                    tLRPC$TL_groupCallParticipant = this.groupCall.participantsByPresentationSources.get(iArr[i]);
                }
                if (!(tLRPC$TL_groupCallParticipant == null || MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == selfId || tLRPC$TL_groupCallParticipant.source == 0)) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(new RequestedParticipant(tLRPC$TL_groupCallParticipant));
                }
            }
            if (arrayList != null) {
                int[] iArr2 = new int[arrayList.size()];
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    iArr2[i2] = ((RequestedParticipant) arrayList.get(i2)).audioSsrc;
                }
                this.tgVoip[0].onMediaDescriptionAvailable(j, iArr2);
                int size2 = arrayList.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    RequestedParticipant requestedParticipant = (RequestedParticipant) arrayList.get(i3);
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = requestedParticipant.participant;
                    if (tLRPC$TL_groupCallParticipant2.muted_by_you) {
                        this.tgVoip[0].setVolume(requestedParticipant.audioSsrc, 0.0d);
                    } else {
                        NativeInstance nativeInstance = this.tgVoip[0];
                        int i4 = requestedParticipant.audioSsrc;
                        double participantVolume = (double) ChatObject.getParticipantVolume(tLRPC$TL_groupCallParticipant2);
                        Double.isNaN(participantVolume);
                        nativeInstance.setVolume(i4, participantVolume / 10000.0d);
                    }
                }
            }
        }
    }

    private void createGroupInstance(int i, boolean z) {
        boolean z2;
        String str;
        if (z) {
            this.mySource[i] = 0;
            if (i == 0) {
                this.switchingAccount = z;
            }
        }
        cancelGroupCheckShortPoll();
        if (i == 0) {
            this.wasConnected = false;
        } else if (!this.wasConnected) {
            this.reconnectScreenCapture = true;
            return;
        }
        if (this.tgVoip[i] == null) {
            if (BuildVars.DEBUG_VERSION) {
                str = VoIPHelper.getLogFilePath("voip_" + i + "_" + this.groupCall.call.id);
            } else {
                str = VoIPHelper.getLogFilePath(this.groupCall.call.id, false);
            }
            this.tgVoip[i] = NativeInstance.makeGroup(str, this.captureDevice[i], i == 1, i == 0 && SharedConfig.noiseSupression, new NativeInstance.PayloadCallback(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(int i, String str) {
                    VoIPService.this.lambda$createGroupInstance$35$VoIPService(this.f$1, i, str);
                }
            }, new NativeInstance.AudioLevelsCallback(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(int[] iArr, float[] fArr, boolean[] zArr) {
                    VoIPService.this.lambda$createGroupInstance$37$VoIPService(this.f$1, iArr, fArr, zArr);
                }
            }, new NativeInstance.VideoSourcesCallback(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(long j, int[] iArr) {
                    VoIPService.this.lambda$createGroupInstance$39$VoIPService(this.f$1, j, iArr);
                }
            }, new NativeInstance.RequestBroadcastPartCallback(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(long j, long j2) {
                    VoIPService.this.lambda$createGroupInstance$42$VoIPService(this.f$1, j, j2);
                }
            }, new NativeInstance.RequestBroadcastPartCallback(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(long j, long j2) {
                    VoIPService.this.lambda$createGroupInstance$43$VoIPService(this.f$1, j, j2);
                }
            });
            this.tgVoip[i].setOnStateUpdatedListener(new Instance.OnStateUpdatedListener(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onStateUpdated(int i, boolean z) {
                    VoIPService.this.lambda$createGroupInstance$44$VoIPService(this.f$1, i, z);
                }
            });
            z2 = true;
        } else {
            z2 = false;
        }
        this.tgVoip[i].resetGroupInstance(!z2, false);
        if (this.captureDevice[i] != 0) {
            this.destroyCaptureDevice[i] = false;
        }
        if (i == 0) {
            dispatchStateChanged(1);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$35 */
    public /* synthetic */ void lambda$createGroupInstance$35$VoIPService(int i, int i2, String str) {
        if (i == 0) {
            startGroupCall(i2, str, true);
        } else {
            startScreenCapture(str);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$37 */
    public /* synthetic */ void lambda$createGroupInstance$37$VoIPService(int i, int[] iArr, float[] fArr, boolean[] zArr) {
        ChatObject.Call call;
        if (sharedInstance != null && (call = this.groupCall) != null && i == 0) {
            call.processVoiceLevelsUpdate(iArr, fArr, zArr);
            float f = 0.0f;
            boolean z = false;
            for (int i2 = 0; i2 < iArr.length; i2++) {
                if (iArr[i2] == 0) {
                    if (this.lastTypingTimeSend < SystemClock.uptimeMillis() - 5000 && fArr[i2] > 0.1f && zArr[i2]) {
                        this.lastTypingTimeSend = SystemClock.uptimeMillis();
                        TLRPC$TL_messages_setTyping tLRPC$TL_messages_setTyping = new TLRPC$TL_messages_setTyping();
                        tLRPC$TL_messages_setTyping.action = new TLRPC$TL_speakingInGroupCallAction();
                        tLRPC$TL_messages_setTyping.peer = MessagesController.getInputPeer(this.chat);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_setTyping, $$Lambda$VoIPService$STfovf_GVR4VQ3xYu2rbmHPoIAI.INSTANCE);
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(fArr[i2]));
                } else {
                    f = Math.max(f, fArr[i2]);
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
    /* renamed from: lambda$createGroupInstance$39 */
    public /* synthetic */ void lambda$createGroupInstance$39$VoIPService(int i, long j, int[] iArr) {
        ChatObject.Call call;
        if (sharedInstance != null && (call = this.groupCall) != null && i == 0) {
            call.processUnknownVideoParticipants(iArr, new ChatObject.Call.OnParticipantsLoad(j, iArr) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ int[] f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void onLoad(ArrayList arrayList) {
                    VoIPService.this.lambda$createGroupInstance$38$VoIPService(this.f$1, this.f$2, arrayList);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$38 */
    public /* synthetic */ void lambda$createGroupInstance$38$VoIPService(long j, int[] iArr, ArrayList arrayList) {
        if (sharedInstance != null && this.groupCall != null) {
            broadcastUnknownParticipants(j, iArr);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$42 */
    public /* synthetic */ void lambda$createGroupInstance$42$VoIPService(int i, long j, long j2) {
        if (i == 0) {
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
            this.currentStreamRequestId = AccountInstance.getInstance(this.currentAccount).getConnectionsManager().sendRequest(tLRPC$TL_upload_getFile, new RequestDelegateTimestamp(i, j) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, long j) {
                    VoIPService.this.lambda$createGroupInstance$41$VoIPService(this.f$1, this.f$2, tLObject, tLRPC$TL_error, j);
                }
            }, 2, 2, this.groupCall.call.stream_dc_id);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$41 */
    public /* synthetic */ void lambda$createGroupInstance$41$VoIPService(int i, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, long j2) {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[i] != null) {
            if (tLObject != null) {
                NativeInstance nativeInstance = nativeInstanceArr[i];
                NativeByteBuffer nativeByteBuffer = ((TLRPC$TL_upload_file) tLObject).bytes;
                nativeInstance.onStreamPartAvailable(j, nativeByteBuffer.buffer, nativeByteBuffer.limit(), j2);
            } else if ("GROUPCALL_JOIN_MISSING".equals(tLRPC$TL_error.text)) {
                AndroidUtilities.runOnUIThread(new Runnable(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        VoIPService.this.lambda$createGroupInstance$40$VoIPService(this.f$1);
                    }
                });
            } else {
                this.tgVoip[i].onStreamPartAvailable(j, (ByteBuffer) null, ("TIME_TOO_BIG".equals(tLRPC$TL_error.text) || tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) ? 0 : -1, j2);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$40 */
    public /* synthetic */ void lambda$createGroupInstance$40$VoIPService(int i) {
        createGroupInstance(i, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createGroupInstance$43 */
    public /* synthetic */ void lambda$createGroupInstance$43$VoIPService(int i, long j, long j2) {
        if (i == 0 && this.currentStreamRequestTimestamp == j) {
            AccountInstance.getInstance(this.currentAccount).getConnectionsManager().cancelRequest(this.currentStreamRequestId, true);
            this.currentStreamRequestId = 0;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: updateConnectionState */
    public void lambda$createGroupInstance$44(int i, int i2, boolean z) {
        if (i == 0) {
            dispatchStateChanged((i2 == 1 || this.switchingStream) ? 3 : 5);
            if (this.switchingStream && (i2 == 0 || (i2 == 1 && z))) {
                $$Lambda$VoIPService$GL5oykjOaTJ21Y5bPxJYpkFcrIY r1 = new Runnable(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        VoIPService.this.lambda$updateConnectionState$45$VoIPService(this.f$1);
                    }
                };
                this.switchingStreamTimeoutRunnable = r1;
                AndroidUtilities.runOnUIThread(r1, 3000);
            }
            if (i2 == 0) {
                startGroupCheckShortpoll();
                if (this.playedConnectedSound && this.spPlayId == 0 && !this.switchingStream && !this.switchingAccount) {
                    Utilities.globalQueue.postRunnable(new Runnable() {
                        public final void run() {
                            VoIPService.this.lambda$updateConnectionState$46$VoIPService();
                        }
                    });
                    return;
                }
                return;
            }
            cancelGroupCheckShortPoll();
            if (!z) {
                this.switchingStream = false;
                this.switchingAccount = false;
            }
            Runnable runnable = this.switchingStreamTimeoutRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.switchingStreamTimeoutRunnable = null;
            }
            if (this.playedConnectedSound) {
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$updateConnectionState$47$VoIPService();
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
                if (this.reconnectScreenCapture) {
                    createGroupInstance(1, false);
                    this.reconnectScreenCapture = false;
                }
                NativeInstance nativeInstance = this.tgVoip[0];
                if (nativeInstance != null && !this.micMute) {
                    nativeInstance.setMuteMicrophone(false);
                }
                setParticipantsVolume();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateConnectionState$45 */
    public /* synthetic */ void lambda$updateConnectionState$45$VoIPService(int i) {
        if (this.switchingStreamTimeoutRunnable != null) {
            this.switchingStream = false;
            lambda$createGroupInstance$44(i, 0, true);
            this.switchingStreamTimeoutRunnable = null;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateConnectionState$46 */
    public /* synthetic */ void lambda$updateConnectionState$46$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.spVoiceChatConnecting, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateConnectionState$47 */
    public /* synthetic */ void lambda$updateConnectionState$47$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    public void setParticipantsVolume() {
        int i;
        NativeInstance nativeInstance = this.tgVoip[0];
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
        NativeInstance nativeInstance = this.tgVoip[0];
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
    /* JADX WARNING: Removed duplicated region for block: B:103:0x026d A[Catch:{ Exception -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x030a A[Catch:{ Exception -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x033a  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0107 A[Catch:{ Exception -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0115 A[Catch:{ Exception -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x012f A[Catch:{ Exception -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x016e A[Catch:{ Exception -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0170 A[Catch:{ Exception -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x017e A[SYNTHETIC, Splitter:B:72:0x017e] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01d6 A[Catch:{ Exception -> 0x01ca }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01e1 A[Catch:{ Exception -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x024c A[Catch:{ Exception -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x024e A[Catch:{ Exception -> 0x0335 }] */
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
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0335 }
            if (r3 == 0) goto L_0x002a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0335 }
            r3.<init>()     // Catch:{ Exception -> 0x0335 }
            java.lang.String r5 = "InitCall: keyID="
            r3.append(r5)     // Catch:{ Exception -> 0x0335 }
            long r5 = r1.keyFingerprint     // Catch:{ Exception -> 0x0335 }
            r3.append(r5)     // Catch:{ Exception -> 0x0335 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x0335 }
        L_0x002a:
            int r3 = r1.currentAccount     // Catch:{ Exception -> 0x0335 }
            android.content.SharedPreferences r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3)     // Catch:{ Exception -> 0x0335 }
            java.util.Set r5 = r3.getStringSet(r0, r4)     // Catch:{ Exception -> 0x0335 }
            if (r5 == 0) goto L_0x003c
            java.util.HashSet r6 = new java.util.HashSet     // Catch:{ Exception -> 0x0335 }
            r6.<init>(r5)     // Catch:{ Exception -> 0x0335 }
            goto L_0x0041
        L_0x003c:
            java.util.HashSet r6 = new java.util.HashSet     // Catch:{ Exception -> 0x0335 }
            r6.<init>()     // Catch:{ Exception -> 0x0335 }
        L_0x0041:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0335 }
            r5.<init>()     // Catch:{ Exception -> 0x0335 }
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r1.privateCall     // Catch:{ Exception -> 0x0335 }
            long r7 = r7.id     // Catch:{ Exception -> 0x0335 }
            r5.append(r7)     // Catch:{ Exception -> 0x0335 }
            r5.append(r2)     // Catch:{ Exception -> 0x0335 }
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r1.privateCall     // Catch:{ Exception -> 0x0335 }
            long r7 = r7.access_hash     // Catch:{ Exception -> 0x0335 }
            r5.append(r7)     // Catch:{ Exception -> 0x0335 }
            r5.append(r2)     // Catch:{ Exception -> 0x0335 }
            long r7 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0335 }
            r5.append(r7)     // Catch:{ Exception -> 0x0335 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0335 }
            r6.add(r5)     // Catch:{ Exception -> 0x0335 }
        L_0x0068:
            int r5 = r6.size()     // Catch:{ Exception -> 0x0335 }
            r7 = 20
            r8 = 2
            if (r5 <= r7) goto L_0x00a9
            r9 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            java.util.Iterator r5 = r6.iterator()     // Catch:{ Exception -> 0x0335 }
            r7 = r4
        L_0x007b:
            boolean r11 = r5.hasNext()     // Catch:{ Exception -> 0x0335 }
            if (r11 == 0) goto L_0x00a3
            java.lang.Object r11 = r5.next()     // Catch:{ Exception -> 0x0335 }
            java.lang.String r11 = (java.lang.String) r11     // Catch:{ Exception -> 0x0335 }
            java.lang.String[] r12 = r11.split(r2)     // Catch:{ Exception -> 0x0335 }
            int r13 = r12.length     // Catch:{ Exception -> 0x0335 }
            if (r13 >= r8) goto L_0x0092
            r5.remove()     // Catch:{ Exception -> 0x0335 }
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
            r5.remove()     // Catch:{ Exception -> 0x0335 }
            goto L_0x007b
        L_0x00a3:
            if (r7 == 0) goto L_0x0068
            r6.remove(r7)     // Catch:{ Exception -> 0x0335 }
            goto L_0x0068
        L_0x00a9:
            android.content.SharedPreferences$Editor r2 = r3.edit()     // Catch:{ Exception -> 0x0335 }
            android.content.SharedPreferences$Editor r0 = r2.putStringSet(r0, r6)     // Catch:{ Exception -> 0x0335 }
            r0.commit()     // Catch:{ Exception -> 0x0335 }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0335 }
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
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0335 }
            int r6 = r1.currentAccount     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)     // Catch:{ Exception -> 0x0335 }
            int r7 = r6.callConnectTimeout     // Catch:{ Exception -> 0x0335 }
            double r9 = (double) r7
            r11 = 4652007308841189376(0x408fNUM, double:1000.0)
            java.lang.Double.isNaN(r9)
            double r14 = r9 / r11
            int r6 = r6.callPacketTimeout     // Catch:{ Exception -> 0x0335 }
            double r6 = (double) r6
            java.lang.Double.isNaN(r6)
            double r16 = r6 / r11
            java.lang.String r6 = "VoipDataSaving"
            int r7 = org.telegram.ui.Components.voip.VoIPHelper.getDataSavingDefault()     // Catch:{ Exception -> 0x0335 }
            int r6 = r5.getInt(r6, r7)     // Catch:{ Exception -> 0x0335 }
            int r18 = r1.convertDataSavingMode(r6)     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.Instance$ServerConfig r6 = org.telegram.messenger.voip.Instance.getGlobalServerConfig()     // Catch:{ Exception -> 0x0335 }
            if (r0 == 0) goto L_0x0103
            boolean r0 = r6.useSystemAec     // Catch:{ Exception -> 0x0335 }
            if (r0 != 0) goto L_0x0100
            goto L_0x0103
        L_0x0100:
            r20 = 0
            goto L_0x0105
        L_0x0103:
            r20 = 1
        L_0x0105:
            if (r2 == 0) goto L_0x010f
            boolean r0 = r6.useSystemNs     // Catch:{ Exception -> 0x0335 }
            if (r0 != 0) goto L_0x010c
            goto L_0x010f
        L_0x010c:
            r21 = 0
            goto L_0x0111
        L_0x010f:
            r21 = 1
        L_0x0111:
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0335 }
            if (r0 == 0) goto L_0x012f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0335 }
            r0.<init>()     // Catch:{ Exception -> 0x0335 }
            java.lang.String r2 = "voip"
            r0.append(r2)     // Catch:{ Exception -> 0x0335 }
            org.telegram.tgnet.TLRPC$PhoneCall r2 = r1.privateCall     // Catch:{ Exception -> 0x0335 }
            long r9 = r2.id     // Catch:{ Exception -> 0x0335 }
            r0.append(r9)     // Catch:{ Exception -> 0x0335 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0335 }
            java.lang.String r0 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath(r0)     // Catch:{ Exception -> 0x0335 }
            goto L_0x0137
        L_0x012f:
            org.telegram.tgnet.TLRPC$PhoneCall r0 = r1.privateCall     // Catch:{ Exception -> 0x0335 }
            long r9 = r0.id     // Catch:{ Exception -> 0x0335 }
            java.lang.String r0 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath(r9, r3)     // Catch:{ Exception -> 0x0335 }
        L_0x0137:
            r25 = r0
            org.telegram.messenger.voip.Instance$Config r0 = new org.telegram.messenger.voip.Instance$Config     // Catch:{ Exception -> 0x0335 }
            org.telegram.tgnet.TLRPC$PhoneCall r2 = r1.privateCall     // Catch:{ Exception -> 0x0335 }
            boolean r9 = r2.p2p_allowed     // Catch:{ Exception -> 0x0335 }
            r22 = 1
            r23 = 0
            boolean r6 = r6.enableStunMarking     // Catch:{ Exception -> 0x0335 }
            java.lang.String r26 = ""
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r2 = r2.protocol     // Catch:{ Exception -> 0x0335 }
            int r2 = r2.max_layer     // Catch:{ Exception -> 0x0335 }
            r13 = r0
            r19 = r9
            r24 = r6
            r27 = r2
            r13.<init>(r14, r16, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ Exception -> 0x0335 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x0335 }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0335 }
            java.io.File r6 = r6.getFilesDir()     // Catch:{ Exception -> 0x0335 }
            java.lang.String r9 = "voip_persistent_state.json"
            r2.<init>(r6, r9)     // Catch:{ Exception -> 0x0335 }
            java.lang.String r28 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x0335 }
            java.lang.String r2 = "dbg_force_tcp_in_calls"
            boolean r2 = r5.getBoolean(r2, r3)     // Catch:{ Exception -> 0x0335 }
            if (r2 == 0) goto L_0x0170
            r6 = 3
            goto L_0x0171
        L_0x0170:
            r6 = 2
        L_0x0171:
            org.telegram.tgnet.TLRPC$PhoneCall r9 = r1.privateCall     // Catch:{ Exception -> 0x0335 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneConnection> r9 = r9.connections     // Catch:{ Exception -> 0x0335 }
            int r15 = r9.size()     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.Instance$Endpoint[] r14 = new org.telegram.messenger.voip.Instance.Endpoint[r15]     // Catch:{ Exception -> 0x0335 }
            r13 = 0
        L_0x017c:
            if (r13 >= r15) goto L_0x01cf
            org.telegram.tgnet.TLRPC$PhoneCall r9 = r1.privateCall     // Catch:{ Exception -> 0x01ca }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneConnection> r9 = r9.connections     // Catch:{ Exception -> 0x01ca }
            java.lang.Object r9 = r9.get(r13)     // Catch:{ Exception -> 0x01ca }
            org.telegram.tgnet.TLRPC$PhoneConnection r9 = (org.telegram.tgnet.TLRPC$PhoneConnection) r9     // Catch:{ Exception -> 0x01ca }
            org.telegram.messenger.voip.Instance$Endpoint r22 = new org.telegram.messenger.voip.Instance$Endpoint     // Catch:{ Exception -> 0x01ca }
            boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$TL_phoneConnectionWebrtc     // Catch:{ Exception -> 0x01ca }
            long r11 = r9.id     // Catch:{ Exception -> 0x01ca }
            java.lang.String r7 = r9.ip     // Catch:{ Exception -> 0x01ca }
            java.lang.String r8 = r9.ipv6     // Catch:{ Exception -> 0x01ca }
            int r4 = r9.port     // Catch:{ Exception -> 0x01ca }
            byte[] r3 = r9.peer_tag     // Catch:{ Exception -> 0x01ca }
            r27 = r0
            boolean r0 = r9.turn     // Catch:{ Exception -> 0x01ca }
            r26 = r5
            boolean r5 = r9.stun     // Catch:{ Exception -> 0x01ca }
            java.lang.String r1 = r9.username     // Catch:{ Exception -> 0x01ca }
            java.lang.String r9 = r9.password     // Catch:{ Exception -> 0x01ca }
            r21 = r9
            r9 = r22
            r29 = r13
            r13 = r7
            r7 = r14
            r14 = r8
            r8 = r15
            r15 = r4
            r16 = r6
            r17 = r3
            r18 = r0
            r19 = r5
            r20 = r1
            r9.<init>(r10, r11, r13, r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ Exception -> 0x01ca }
            r7[r29] = r22     // Catch:{ Exception -> 0x01ca }
            int r13 = r29 + 1
            r3 = 0
            r4 = 0
            r1 = r37
            r14 = r7
            r15 = r8
            r5 = r26
            r0 = r27
            r8 = 2
            goto L_0x017c
        L_0x01ca:
            r0 = move-exception
            r1 = r37
            goto L_0x0336
        L_0x01cf:
            r27 = r0
            r26 = r5
            r7 = r14
            if (r2 == 0) goto L_0x01e1
            org.telegram.messenger.voip.-$$Lambda$VoIPService$xJH1HkhwVYMQNm1I3wM8UJW_FSM r0 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$xJH1HkhwVYMQNm1I3wM8UJW_FSM     // Catch:{ Exception -> 0x01ca }
            r1 = r37
            r0.<init>()     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x0335 }
            goto L_0x01e3
        L_0x01e1:
            r1 = r37
        L_0x01e3:
            java.lang.String r0 = "proxy_enabled"
            r2 = r26
            r3 = 0
            boolean r0 = r2.getBoolean(r0, r3)     // Catch:{ Exception -> 0x0335 }
            if (r0 == 0) goto L_0x022b
            java.lang.String r0 = "proxy_enabled_calls"
            boolean r0 = r2.getBoolean(r0, r3)     // Catch:{ Exception -> 0x0335 }
            if (r0 == 0) goto L_0x022b
            java.lang.String r0 = "proxy_ip"
            r3 = 0
            java.lang.String r0 = r2.getString(r0, r3)     // Catch:{ Exception -> 0x0335 }
            java.lang.String r4 = "proxy_secret"
            java.lang.String r4 = r2.getString(r4, r3)     // Catch:{ Exception -> 0x0335 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0335 }
            if (r3 != 0) goto L_0x022b
            boolean r3 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0335 }
            if (r3 == 0) goto L_0x022b
            org.telegram.messenger.voip.Instance$Proxy r3 = new org.telegram.messenger.voip.Instance$Proxy     // Catch:{ Exception -> 0x0335 }
            java.lang.String r4 = "proxy_port"
            r5 = 0
            int r4 = r2.getInt(r4, r5)     // Catch:{ Exception -> 0x0335 }
            java.lang.String r5 = "proxy_user"
            r6 = 0
            java.lang.String r5 = r2.getString(r5, r6)     // Catch:{ Exception -> 0x0335 }
            java.lang.String r8 = "proxy_pass"
            java.lang.String r2 = r2.getString(r8, r6)     // Catch:{ Exception -> 0x0335 }
            r3.<init>(r0, r4, r5, r2)     // Catch:{ Exception -> 0x0335 }
            r30 = r3
            goto L_0x022e
        L_0x022b:
            r6 = 0
            r30 = r6
        L_0x022e:
            org.telegram.messenger.voip.Instance$EncryptionKey r0 = new org.telegram.messenger.voip.Instance$EncryptionKey     // Catch:{ Exception -> 0x0335 }
            byte[] r2 = r1.authKey     // Catch:{ Exception -> 0x0335 }
            boolean r3 = r1.isOutgoing     // Catch:{ Exception -> 0x0335 }
            r0.<init>(r2, r3)     // Catch:{ Exception -> 0x0335 }
            java.lang.String r2 = "2.7.7"
            org.telegram.tgnet.TLRPC$PhoneCall r3 = r1.privateCall     // Catch:{ Exception -> 0x0335 }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r3 = r3.protocol     // Catch:{ Exception -> 0x0335 }
            java.util.ArrayList<java.lang.String> r3 = r3.library_versions     // Catch:{ Exception -> 0x0335 }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0335 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0335 }
            int r2 = r2.compareTo(r3)     // Catch:{ Exception -> 0x0335 }
            if (r2 > 0) goto L_0x024e
            r3 = 1
            goto L_0x024f
        L_0x024e:
            r3 = 0
        L_0x024f:
            long[] r2 = r1.captureDevice     // Catch:{ Exception -> 0x0335 }
            r4 = 0
            r5 = r2[r4]     // Catch:{ Exception -> 0x0335 }
            r8 = 0
            int r10 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x0269
            if (r3 != 0) goto L_0x0269
            r5 = r2[r4]     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.NativeInstance.destroyVideoCapturer(r5)     // Catch:{ Exception -> 0x0335 }
            long[] r2 = r1.captureDevice     // Catch:{ Exception -> 0x0335 }
            r2[r4] = r8     // Catch:{ Exception -> 0x0335 }
            int[] r2 = r1.videoState     // Catch:{ Exception -> 0x0335 }
            r2[r4] = r4     // Catch:{ Exception -> 0x0335 }
        L_0x0269:
            boolean r2 = r1.isOutgoing     // Catch:{ Exception -> 0x0335 }
            if (r2 != 0) goto L_0x029e
            boolean r2 = r1.videoCall     // Catch:{ Exception -> 0x0335 }
            if (r2 == 0) goto L_0x0299
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0335 }
            r4 = 23
            if (r2 < r4) goto L_0x027f
            java.lang.String r2 = "android.permission.CAMERA"
            int r2 = r1.checkSelfPermission(r2)     // Catch:{ Exception -> 0x0335 }
            if (r2 != 0) goto L_0x0299
        L_0x027f:
            long[] r2 = r1.captureDevice     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r4 = r1.localSink     // Catch:{ Exception -> 0x0335 }
            r5 = 0
            r4 = r4[r5]     // Catch:{ Exception -> 0x0335 }
            boolean r6 = r1.isFrontFaceCamera     // Catch:{ Exception -> 0x0335 }
            if (r6 == 0) goto L_0x028c
            r6 = 1
            goto L_0x028d
        L_0x028c:
            r6 = 0
        L_0x028d:
            long r10 = org.telegram.messenger.voip.NativeInstance.createVideoCapturer(r4, r6)     // Catch:{ Exception -> 0x0335 }
            r2[r5] = r10     // Catch:{ Exception -> 0x0335 }
            int[] r2 = r1.videoState     // Catch:{ Exception -> 0x0335 }
            r4 = 2
            r2[r5] = r4     // Catch:{ Exception -> 0x0335 }
            goto L_0x029e
        L_0x0299:
            int[] r2 = r1.videoState     // Catch:{ Exception -> 0x0335 }
            r4 = 0
            r2[r4] = r4     // Catch:{ Exception -> 0x0335 }
        L_0x029e:
            org.telegram.messenger.voip.NativeInstance[] r2 = r1.tgVoip     // Catch:{ Exception -> 0x0335 }
            org.telegram.tgnet.TLRPC$PhoneCall r4 = r1.privateCall     // Catch:{ Exception -> 0x0335 }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r4 = r4.protocol     // Catch:{ Exception -> 0x0335 }
            java.util.ArrayList<java.lang.String> r4 = r4.library_versions     // Catch:{ Exception -> 0x0335 }
            r5 = 0
            java.lang.Object r4 = r4.get(r5)     // Catch:{ Exception -> 0x0335 }
            r26 = r4
            java.lang.String r26 = (java.lang.String) r26     // Catch:{ Exception -> 0x0335 }
            int r31 = r37.getNetworkType()     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r4 = r1.remoteSink     // Catch:{ Exception -> 0x0335 }
            r33 = r4[r5]     // Catch:{ Exception -> 0x0335 }
            long[] r4 = r1.captureDevice     // Catch:{ Exception -> 0x0335 }
            r34 = r4[r5]     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.-$$Lambda$VoIPService$GTgTQkJz57EfTDQdGAz4SAASmPo r4 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$GTgTQkJz57EfTDQdGAz4SAASmPo     // Catch:{ Exception -> 0x0335 }
            r4.<init>()     // Catch:{ Exception -> 0x0335 }
            r29 = r7
            r32 = r0
            r36 = r4
            org.telegram.messenger.voip.NativeInstance r0 = org.telegram.messenger.voip.Instance.makeInstance(r26, r27, r28, r29, r30, r31, r32, r33, r34, r36)     // Catch:{ Exception -> 0x0335 }
            r4 = 0
            r2[r4] = r0     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.NativeInstance[] r0 = r1.tgVoip     // Catch:{ Exception -> 0x0335 }
            r0 = r0[r4]     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.-$$Lambda$sjy6PiJMA8_4lfe9Cli5v92zzh8 r2 = new org.telegram.messenger.voip.-$$Lambda$sjy6PiJMA8_4lfe9Cli5v92zzh8     // Catch:{ Exception -> 0x0335 }
            r2.<init>()     // Catch:{ Exception -> 0x0335 }
            r0.setOnStateUpdatedListener(r2)     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.NativeInstance[] r0 = r1.tgVoip     // Catch:{ Exception -> 0x0335 }
            r0 = r0[r4]     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.-$$Lambda$iUB9xedaG0IVoVQDFXyc4ati9RE r2 = new org.telegram.messenger.voip.-$$Lambda$iUB9xedaG0IVoVQDFXyc4ati9RE     // Catch:{ Exception -> 0x0335 }
            r2.<init>()     // Catch:{ Exception -> 0x0335 }
            r0.setOnSignalBarsUpdatedListener(r2)     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.NativeInstance[] r0 = r1.tgVoip     // Catch:{ Exception -> 0x0335 }
            r0 = r0[r4]     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.-$$Lambda$HNfvWF9Xn3-zdFJ1-nzWv-_4Tj8 r2 = new org.telegram.messenger.voip.-$$Lambda$HNfvWF9Xn3-zdFJ1-nzWv-_4Tj8     // Catch:{ Exception -> 0x0335 }
            r2.<init>()     // Catch:{ Exception -> 0x0335 }
            r0.setOnSignalDataListener(r2)     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.NativeInstance[] r0 = r1.tgVoip     // Catch:{ Exception -> 0x0335 }
            r0 = r0[r4]     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.-$$Lambda$VoIPService$kP9mOiwtzpINiYEqGnj-P2D5tm4 r2 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$kP9mOiwtzpINiYEqGnj-P2D5tm4     // Catch:{ Exception -> 0x0335 }
            r2.<init>()     // Catch:{ Exception -> 0x0335 }
            r0.setOnRemoteMediaStateUpdatedListener(r2)     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.NativeInstance[] r0 = r1.tgVoip     // Catch:{ Exception -> 0x0335 }
            r0 = r0[r4]     // Catch:{ Exception -> 0x0335 }
            boolean r2 = r1.micMute     // Catch:{ Exception -> 0x0335 }
            r0.setMuteMicrophone(r2)     // Catch:{ Exception -> 0x0335 }
            boolean r0 = r1.isVideoAvailable     // Catch:{ Exception -> 0x0335 }
            if (r3 == r0) goto L_0x0325
            r1.isVideoAvailable = r3     // Catch:{ Exception -> 0x0335 }
            r3 = 0
        L_0x030d:
            java.util.ArrayList<org.telegram.messenger.voip.VoIPService$StateListener> r0 = r1.stateListeners     // Catch:{ Exception -> 0x0335 }
            int r0 = r0.size()     // Catch:{ Exception -> 0x0335 }
            if (r3 >= r0) goto L_0x0325
            java.util.ArrayList<org.telegram.messenger.voip.VoIPService$StateListener> r0 = r1.stateListeners     // Catch:{ Exception -> 0x0335 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.VoIPService$StateListener r0 = (org.telegram.messenger.voip.VoIPService.StateListener) r0     // Catch:{ Exception -> 0x0335 }
            boolean r2 = r1.isVideoAvailable     // Catch:{ Exception -> 0x0335 }
            r0.onVideoAvailableChange(r2)     // Catch:{ Exception -> 0x0335 }
            int r3 = r3 + 1
            goto L_0x030d
        L_0x0325:
            long[] r0 = r1.captureDevice     // Catch:{ Exception -> 0x0335 }
            r2 = 0
            r0[r2] = r8     // Catch:{ Exception -> 0x0335 }
            org.telegram.messenger.voip.VoIPService$6 r0 = new org.telegram.messenger.voip.VoIPService$6     // Catch:{ Exception -> 0x0335 }
            r0.<init>()     // Catch:{ Exception -> 0x0335 }
            r2 = 5000(0x1388, double:2.4703E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r2)     // Catch:{ Exception -> 0x0335 }
            goto L_0x0342
        L_0x0335:
            r0 = move-exception
        L_0x0336:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x033f
            java.lang.String r2 = "error starting call"
            org.telegram.messenger.FileLog.e(r2, r0)
        L_0x033f:
            r37.callFailed()
        L_0x0342:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.initiateActualEncryptedCall():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initiateActualEncryptedCall$48 */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$48$VoIPService() {
        Toast.makeText(this, "This call uses TCP which will degrade its quality.", 0).show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initiateActualEncryptedCall$49 */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$49$VoIPService(int[] iArr, float[] fArr, boolean[] zArr) {
        if (sharedInstance != null && this.privateCall != null) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(fArr[0]));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initiateActualEncryptedCall$51 */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$51$VoIPService(int i, int i2) {
        AndroidUtilities.runOnUIThread(new Runnable(i, i2) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPService.this.lambda$initiateActualEncryptedCall$50$VoIPService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initiateActualEncryptedCall$50 */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$50$VoIPService(int i, int i2) {
        this.remoteAudioState = i;
        this.remoteVideoState = i2;
        checkIsNear();
        for (int i3 = 0; i3 < this.stateListeners.size(); i3++) {
            this.stateListeners.get(i3).onMediaStateUpdated(i, i2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$playConnectedSound$52 */
    public /* synthetic */ void lambda$playConnectedSound$52$VoIPService() {
        this.soundPool.play(this.spVoiceChatStartId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void playConnectedSound() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VoIPService.this.lambda$playConnectedSound$52$VoIPService();
            }
        });
        this.playedConnectedSound = true;
    }

    private void startConnectingSound() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VoIPService.this.lambda$startConnectingSound$53$VoIPService();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startConnectingSound$53 */
    public /* synthetic */ void lambda$startConnectingSound$53$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        int play = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        this.spPlayId = play;
        if (play == 0) {
            AnonymousClass7 r0 = new Runnable() {
                public void run() {
                    if (VoIPService.sharedInstance != null) {
                        Utilities.globalQueue.postRunnable(new Runnable() {
                            public final void run() {
                                VoIPService.AnonymousClass7.this.lambda$run$0$VoIPService$7();
                            }
                        });
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$run$0 */
                public /* synthetic */ void lambda$run$0$VoIPService$7() {
                    if (VoIPService.this.spPlayId == 0) {
                        VoIPService voIPService = VoIPService.this;
                        int unused = voIPService.spPlayId = voIPService.soundPool.play(VoIPService.this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
                    }
                    if (VoIPService.this.spPlayId == 0) {
                        AndroidUtilities.runOnUIThread(this, 100);
                    } else {
                        Runnable unused2 = VoIPService.this.connectingSoundRunnable = null;
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_sendSignalingData, $$Lambda$VoIPService$JjydGDxYJj8_cDw7PW6ILhk5fxM.INSTANCE);
        }
    }

    public boolean isVideoAvailable() {
        return this.isVideoAvailable;
    }

    /* access modifiers changed from: package-private */
    public void onMediaButtonEvent(KeyEvent keyEvent) {
        if (keyEvent != null) {
            if ((keyEvent.getKeyCode() != 79 && keyEvent.getKeyCode() != 127 && keyEvent.getKeyCode() != 85) || keyEvent.getAction() != 1) {
                return;
            }
            if (this.currentState == 15) {
                acceptIncomingCall();
            } else {
                setMicMute(!isMicMute(), false, true);
            }
        }
    }

    public byte[] getGA() {
        return this.g_a;
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

    public boolean hasEarpiece() {
        CallConnection callConnection;
        if (!USE_CONNECTION_SERVICE || (callConnection = this.systemCallConnection) == null || callConnection.getCallAudioState() == null) {
            if (((TelephonyManager) getSystemService("phone")).getPhoneType() != 0) {
                return true;
            }
            Boolean bool = this.mHasEarpiece;
            if (bool != null) {
                return bool.booleanValue();
            }
            try {
                Method method = AudioManager.class.getMethod("getDevicesForStream", new Class[]{Integer.TYPE});
                int i = AudioManager.class.getField("DEVICE_OUT_EARPIECE").getInt((Object) null);
                if ((((Integer) method.invoke((AudioManager) getSystemService("audio"), new Object[]{0})).intValue() & i) == i) {
                    this.mHasEarpiece = Boolean.TRUE;
                } else {
                    this.mHasEarpiece = Boolean.FALSE;
                }
            } catch (Throwable th) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error while checking earpiece! ", th);
                }
                this.mHasEarpiece = Boolean.TRUE;
            }
            return this.mHasEarpiece.booleanValue();
        } else if ((this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 5) != 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public int getStatsNetworkType() {
        NetworkInfo networkInfo = this.lastNetInfo;
        if (networkInfo == null || networkInfo.getType() != 0) {
            return 1;
        }
        return this.lastNetInfo.isRoaming() ? 2 : 0;
    }

    /* access modifiers changed from: protected */
    public void setSwitchingCamera(boolean z, boolean z2) {
        this.switchingCamera = z;
        if (!z) {
            this.isFrontFaceCamera = z2;
            for (int i = 0; i < this.stateListeners.size(); i++) {
                this.stateListeners.get(i).onCameraSwitch(this.isFrontFaceCamera);
            }
        }
    }

    public void registerStateListener(StateListener stateListener) {
        if (!this.stateListeners.contains(stateListener)) {
            this.stateListeners.add(stateListener);
            int i = this.currentState;
            if (i != 0) {
                stateListener.onStateChanged(i);
            }
            int i2 = this.signalBarCount;
            if (i2 != 0) {
                stateListener.onSignalBarsCountChanged(i2);
            }
        }
    }

    public void unregisterStateListener(StateListener stateListener) {
        this.stateListeners.remove(stateListener);
    }

    public void editCallMember(TLObject tLObject, Boolean bool, Boolean bool2, Integer num, Boolean bool3, Runnable runnable) {
        TLRPC$InputPeer tLRPC$InputPeer;
        if (tLObject != null && this.groupCall != null) {
            TLRPC$TL_phone_editGroupCallParticipant tLRPC$TL_phone_editGroupCallParticipant = new TLRPC$TL_phone_editGroupCallParticipant();
            tLRPC$TL_phone_editGroupCallParticipant.call = this.groupCall.getInputGroupCall();
            if (tLObject instanceof TLRPC$User) {
                TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                if (!UserObject.isUserSelf(tLRPC$User) || (tLRPC$InputPeer = this.groupCallPeer) == null) {
                    tLRPC$TL_phone_editGroupCallParticipant.participant = MessagesController.getInputPeer(tLRPC$User);
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("edit group call part id = " + tLRPC$TL_phone_editGroupCallParticipant.participant.user_id + " access_hash = " + tLRPC$TL_phone_editGroupCallParticipant.participant.user_id);
                    }
                } else {
                    tLRPC$TL_phone_editGroupCallParticipant.participant = tLRPC$InputPeer;
                }
            } else if (tLObject instanceof TLRPC$Chat) {
                tLRPC$TL_phone_editGroupCallParticipant.participant = MessagesController.getInputPeer((TLRPC$Chat) tLObject);
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("edit group call part id = ");
                    TLRPC$InputPeer tLRPC$InputPeer2 = tLRPC$TL_phone_editGroupCallParticipant.participant;
                    int i = tLRPC$InputPeer2.chat_id;
                    if (i == 0) {
                        i = tLRPC$InputPeer2.channel_id;
                    }
                    sb.append(i);
                    sb.append(" access_hash = ");
                    sb.append(tLRPC$TL_phone_editGroupCallParticipant.participant.access_hash);
                    FileLog.d(sb.toString());
                }
            }
            if (bool != null) {
                tLRPC$TL_phone_editGroupCallParticipant.muted = bool.booleanValue();
                tLRPC$TL_phone_editGroupCallParticipant.flags |= 1;
            }
            if (num != null) {
                tLRPC$TL_phone_editGroupCallParticipant.volume = num.intValue();
                tLRPC$TL_phone_editGroupCallParticipant.flags |= 2;
            }
            if (bool3 != null) {
                tLRPC$TL_phone_editGroupCallParticipant.raise_hand = bool3.booleanValue();
                tLRPC$TL_phone_editGroupCallParticipant.flags |= 4;
            }
            if (bool2 != null) {
                tLRPC$TL_phone_editGroupCallParticipant.video_stopped = bool2.booleanValue();
                tLRPC$TL_phone_editGroupCallParticipant.flags |= 8;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("edit group call flags = " + tLRPC$TL_phone_editGroupCallParticipant.flags);
            }
            int i2 = this.currentAccount;
            AccountInstance.getInstance(i2).getConnectionsManager().sendRequest(tLRPC$TL_phone_editGroupCallParticipant, new RequestDelegate(i2, runnable) {
                public final /* synthetic */ int f$0;
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.lambda$editCallMember$55(this.f$0, this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    static /* synthetic */ void lambda$editCallMember$55(int i, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AccountInstance.getInstance(i).getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public boolean isMicMute() {
        return this.micMute;
    }

    public void toggleSpeakerphoneOrShowRouteSheet(Context context, boolean z) {
        CallConnection callConnection;
        String str;
        int i;
        int i2 = 2;
        if (!isBluetoothHeadsetConnected() || !hasEarpiece()) {
            boolean z2 = USE_CONNECTION_SERVICE;
            if (z2 && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
                int i3 = 5;
                if (hasEarpiece()) {
                    CallConnection callConnection2 = this.systemCallConnection;
                    if (callConnection2.getCallAudioState().getRoute() != 8) {
                        i3 = 8;
                    }
                    callConnection2.setAudioRoute(i3);
                } else {
                    CallConnection callConnection3 = this.systemCallConnection;
                    if (callConnection3.getCallAudioState().getRoute() == 2) {
                        i2 = 5;
                    }
                    callConnection3.setAudioRoute(i2);
                }
            } else if (!this.audioConfigured || z2) {
                this.speakerphoneStateToSet = !this.speakerphoneStateToSet;
            } else {
                AudioManager audioManager = (AudioManager) getSystemService("audio");
                if (hasEarpiece()) {
                    audioManager.setSpeakerphoneOn(!audioManager.isSpeakerphoneOn());
                } else {
                    audioManager.setBluetoothScoOn(!audioManager.isBluetoothScoOn());
                }
                updateOutputGainControlState();
            }
            Iterator<StateListener> it = this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
            return;
        }
        BottomSheet.Builder title = new BottomSheet.Builder(context).setTitle(LocaleController.getString("VoipOutputDevices", NUM), true);
        CharSequence[] charSequenceArr = new CharSequence[3];
        charSequenceArr[0] = LocaleController.getString("VoipAudioRoutingSpeaker", NUM);
        if (this.isHeadsetPlugged) {
            i = NUM;
            str = "VoipAudioRoutingHeadset";
        } else {
            i = NUM;
            str = "VoipAudioRoutingEarpiece";
        }
        charSequenceArr[1] = LocaleController.getString(str, i);
        String str2 = this.currentBluetoothDeviceName;
        if (str2 == null) {
            str2 = LocaleController.getString("VoipAudioRoutingBluetooth", NUM);
        }
        charSequenceArr[2] = str2;
        int[] iArr = new int[3];
        iArr[0] = NUM;
        iArr[1] = this.isHeadsetPlugged ? NUM : NUM;
        iArr[2] = NUM;
        BottomSheet.Builder items = title.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                VoIPService.this.lambda$toggleSpeakerphoneOrShowRouteSheet$56$VoIPService(dialogInterface, i);
            }
        });
        BottomSheet create = items.create();
        if (z) {
            if (Build.VERSION.SDK_INT >= 26) {
                create.getWindow().setType(2038);
            } else {
                create.getWindow().setType(2003);
            }
        }
        items.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$toggleSpeakerphoneOrShowRouteSheet$56 */
    public /* synthetic */ void lambda$toggleSpeakerphoneOrShowRouteSheet$56$VoIPService(DialogInterface dialogInterface, int i) {
        if (getSharedInstance() != null) {
            setAudioOutput(i);
        }
    }

    public void setAudioOutput(int i) {
        CallConnection callConnection;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("setAudioOutput " + i);
        }
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        boolean z = USE_CONNECTION_SERVICE;
        if (!z || (callConnection = this.systemCallConnection) == null) {
            if (this.audioConfigured && !z) {
                if (i == 0) {
                    this.needSwitchToBluetoothAfterScoActivates = false;
                    if (this.bluetoothScoActive || this.bluetoothScoConnecting) {
                        audioManager.stopBluetoothSco();
                        this.bluetoothScoActive = false;
                        this.bluetoothScoConnecting = false;
                    }
                    audioManager.setBluetoothScoOn(false);
                    audioManager.setSpeakerphoneOn(true);
                } else if (i == 1) {
                    this.needSwitchToBluetoothAfterScoActivates = false;
                    if (this.bluetoothScoActive || this.bluetoothScoConnecting) {
                        audioManager.stopBluetoothSco();
                        this.bluetoothScoActive = false;
                        this.bluetoothScoConnecting = false;
                    }
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setBluetoothScoOn(false);
                } else if (i == 2) {
                    if (!this.bluetoothScoActive) {
                        this.needSwitchToBluetoothAfterScoActivates = true;
                        try {
                            audioManager.startBluetoothSco();
                        } catch (Throwable th) {
                            FileLog.e(th);
                        }
                    } else {
                        audioManager.setBluetoothScoOn(true);
                        audioManager.setSpeakerphoneOn(false);
                    }
                }
                updateOutputGainControlState();
            } else if (i == 0) {
                this.audioRouteToSet = 1;
                this.speakerphoneStateToSet = true;
            } else if (i == 1) {
                this.audioRouteToSet = 0;
                this.speakerphoneStateToSet = false;
            } else if (i == 2) {
                this.audioRouteToSet = 2;
                this.speakerphoneStateToSet = false;
            }
        } else if (i == 0) {
            callConnection.setAudioRoute(8);
        } else if (i == 1) {
            callConnection.setAudioRoute(5);
        } else if (i == 2) {
            callConnection.setAudioRoute(2);
        }
        Iterator<StateListener> it = this.stateListeners.iterator();
        while (it.hasNext()) {
            it.next().onAudioSettingsChanged();
        }
    }

    public boolean isSpeakerphoneOn() {
        CallConnection callConnection;
        boolean z = USE_CONNECTION_SERVICE;
        if (z && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
            int route = this.systemCallConnection.getCallAudioState().getRoute();
            if (hasEarpiece()) {
                if (route == 8) {
                    return true;
                }
            } else if (route == 2) {
                return true;
            }
            return false;
        } else if (!this.audioConfigured || z) {
            return this.speakerphoneStateToSet;
        } else {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            return hasEarpiece() ? audioManager.isSpeakerphoneOn() : audioManager.isBluetoothScoOn();
        }
    }

    public int getCurrentAudioRoute() {
        if (USE_CONNECTION_SERVICE) {
            CallConnection callConnection = this.systemCallConnection;
            if (!(callConnection == null || callConnection.getCallAudioState() == null)) {
                int route = this.systemCallConnection.getCallAudioState().getRoute();
                if (route != 1) {
                    if (route == 2) {
                        return 2;
                    }
                    if (route != 4) {
                        if (route == 8) {
                            return 1;
                        }
                    }
                }
                return 0;
            }
            return this.audioRouteToSet;
        } else if (!this.audioConfigured) {
            return this.audioRouteToSet;
        } else {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (audioManager.isBluetoothScoOn()) {
                return 2;
            }
            return audioManager.isSpeakerphoneOn() ? 1 : 0;
        }
    }

    public String getDebugString() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        return nativeInstanceArr[0] != null ? nativeInstanceArr[0].getDebugInfo() : "";
    }

    public long getCallDuration() {
        if (this.callStartTime == 0) {
            return 0;
        }
        return SystemClock.elapsedRealtime() - this.callStartTime;
    }

    public void stopRinging() {
        MediaPlayer mediaPlayer = this.ringtonePlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.ringtonePlayer.release();
            this.ringtonePlayer = null;
        }
        Vibrator vibrator2 = this.vibrator;
        if (vibrator2 != null) {
            vibrator2.cancel();
            this.vibrator = null;
        }
    }

    private void showNotification(String str, Bitmap bitmap) {
        String str2;
        int i;
        String str3;
        int i2;
        Intent action = new Intent(this, LaunchActivity.class).setAction(this.groupCall != null ? "voip_chat" : "voip");
        if (this.groupCall != null) {
            action.putExtra("currentAccount", this.currentAccount);
        }
        Notification.Builder builder = new Notification.Builder(this);
        if (this.groupCall != null) {
            i = NUM;
            str2 = "VoipVoiceChat";
        } else {
            i = NUM;
            str2 = "VoipOutgoingCall";
        }
        Notification.Builder contentIntent = builder.setContentTitle(LocaleController.getString(str2, i)).setContentText(str).setContentIntent(PendingIntent.getActivity(this, 50, action, 0));
        if (this.groupCall != null) {
            contentIntent.setSmallIcon(isMicMute() ? NUM : NUM);
        } else {
            contentIntent.setSmallIcon(NUM);
        }
        int i3 = Build.VERSION.SDK_INT;
        if (i3 >= 16) {
            Intent intent = new Intent(this, VoIPActionsReceiver.class);
            intent.setAction(getPackageName() + ".END_CALL");
            if (this.groupCall != null) {
                i2 = NUM;
                str3 = "VoipGroupLeaveAlertTitle";
            } else {
                i2 = NUM;
                str3 = "VoipEndCall";
            }
            contentIntent.addAction(NUM, LocaleController.getString(str3, i2), PendingIntent.getBroadcast(this, 0, intent, NUM));
            contentIntent.setPriority(2);
        }
        if (i3 >= 17) {
            contentIntent.setShowWhen(false);
        }
        if (i3 >= 26) {
            contentIntent.setColor(-14143951);
            contentIntent.setColorized(true);
        } else if (i3 >= 21) {
            contentIntent.setColor(-13851168);
        }
        if (i3 >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            contentIntent.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
        }
        if (bitmap != null) {
            contentIntent.setLargeIcon(bitmap);
        }
        startForeground(201, contentIntent.getNotification());
    }

    private void startRingtoneAndVibration(int i) {
        int i2;
        String str;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (audioManager.getRingerMode() != 0) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.ringtonePlayer = mediaPlayer;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public final void onPrepared(MediaPlayer mediaPlayer) {
                    VoIPService.this.lambda$startRingtoneAndVibration$57$VoIPService(mediaPlayer);
                }
            });
            this.ringtonePlayer.setLooping(true);
            if (this.isHeadsetPlugged) {
                this.ringtonePlayer.setAudioStreamType(0);
            } else {
                this.ringtonePlayer.setAudioStreamType(2);
                if (!USE_CONNECTION_SERVICE) {
                    audioManager.requestAudioFocus(this, 2, 1);
                }
            }
            try {
                if (notificationsSettings.getBoolean("custom_" + i, false)) {
                    str = notificationsSettings.getString("ringtone_path_" + i, RingtoneManager.getDefaultUri(1).toString());
                } else {
                    str = notificationsSettings.getString("CallsRingtonePath", RingtoneManager.getDefaultUri(1).toString());
                }
                this.ringtonePlayer.setDataSource(this, Uri.parse(str));
                this.ringtonePlayer.prepareAsync();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                MediaPlayer mediaPlayer2 = this.ringtonePlayer;
                if (mediaPlayer2 != null) {
                    mediaPlayer2.release();
                    this.ringtonePlayer = null;
                }
            }
            if (notificationsSettings.getBoolean("custom_" + i, false)) {
                i2 = notificationsSettings.getInt("calls_vibrate_" + i, 0);
            } else {
                i2 = notificationsSettings.getInt("vibrate_calls", 0);
            }
            if ((i2 != 2 && i2 != 4 && (audioManager.getRingerMode() == 1 || audioManager.getRingerMode() == 2)) || (i2 == 4 && audioManager.getRingerMode() == 1)) {
                Vibrator vibrator2 = (Vibrator) getSystemService("vibrator");
                this.vibrator = vibrator2;
                long j = 700;
                if (i2 == 1) {
                    j = 350;
                } else if (i2 == 3) {
                    j = 1400;
                }
                vibrator2.vibrate(new long[]{0, j, 500}, 0);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startRingtoneAndVibration$57 */
    public /* synthetic */ void lambda$startRingtoneAndVibration$57$VoIPService(MediaPlayer mediaPlayer) {
        try {
            this.ringtonePlayer.start();
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void onDestroy() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STOPPING ===============");
        }
        stopForeground(true);
        stopRinging();
        if (ApplicationLoader.mainInterfacePaused || !ApplicationLoader.isScreenOn) {
            MessagesController.getInstance(this.currentAccount).ignoreSetOnline = false;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
        SensorManager sensorManager = (SensorManager) getSystemService("sensor");
        if (sensorManager.getDefaultSensor(8) != null) {
            sensorManager.unregisterListener(this);
        }
        PowerManager.WakeLock wakeLock = this.proximityWakelock;
        if (wakeLock != null && wakeLock.isHeld()) {
            this.proximityWakelock.release();
        }
        if (this.updateNotificationRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.updateNotificationRunnable);
            this.updateNotificationRunnable = null;
        }
        Runnable runnable = this.switchingStreamTimeoutRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.switchingStreamTimeoutRunnable = null;
        }
        unregisterReceiver(this.receiver);
        Runnable runnable2 = this.timeoutRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.timeoutRunnable = null;
        }
        super.onDestroy();
        sharedInstance = null;
        Arrays.fill(this.mySource, 0);
        cancelGroupCheckShortPoll();
        AndroidUtilities.runOnUIThread($$Lambda$VoIPService$Re89En1tN80sCvsXyVB2vhoEpYU.INSTANCE);
        if (this.tgVoip[0] != null) {
            StatsController.getInstance(this.currentAccount).incrementTotalCallsTime(getStatsNetworkType(), ((int) (getCallDuration() / 1000)) % 5);
            onTgVoipPreStop();
            if (this.tgVoip[0].isGroup()) {
                NativeInstance nativeInstance = this.tgVoip[0];
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                nativeInstance.getClass();
                dispatchQueue.postRunnable(new Runnable() {
                    public final void run() {
                        NativeInstance.this.stopGroup();
                    }
                });
                AccountInstance.getInstance(this.currentAccount).getConnectionsManager().cancelRequest(this.currentStreamRequestId, true);
                this.currentStreamRequestId = 0;
            } else {
                Instance.FinalState stop = this.tgVoip[0].stop();
                updateTrafficStats(this.tgVoip[0], stop.trafficStats);
                onTgVoipStop(stop);
            }
            this.prevTrafficStats = null;
            this.callStartTime = 0;
            this.tgVoip[0] = null;
            Instance.destroyInstance();
        }
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[1] != null) {
            NativeInstance nativeInstance2 = nativeInstanceArr[1];
            DispatchQueue dispatchQueue2 = Utilities.globalQueue;
            nativeInstance2.getClass();
            dispatchQueue2.postRunnable(new Runnable() {
                public final void run() {
                    NativeInstance.this.stopGroup();
                }
            });
            this.tgVoip[1] = null;
        }
        int i = 0;
        while (true) {
            long[] jArr = this.captureDevice;
            if (i >= jArr.length) {
                break;
            }
            if (jArr[i] != 0) {
                if (this.destroyCaptureDevice[i]) {
                    NativeInstance.destroyVideoCapturer(jArr[i]);
                }
                this.captureDevice[i] = 0;
            }
            i++;
        }
        this.cpuWakelock.release();
        if (!this.playingSound) {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (!USE_CONNECTION_SERVICE) {
                if (this.isBtHeadsetConnected || this.bluetoothScoActive || this.bluetoothScoConnecting) {
                    audioManager.stopBluetoothSco();
                    audioManager.setBluetoothScoOn(false);
                    audioManager.setSpeakerphoneOn(false);
                    this.bluetoothScoActive = false;
                    this.bluetoothScoConnecting = false;
                }
                if (this.onDestroyRunnable == null) {
                    DispatchQueue dispatchQueue3 = Utilities.globalQueue;
                    $$Lambda$VoIPService$YosEiFipiUFnzwclaPpDmTI5XhQ r5 = new Runnable(audioManager) {
                        public final /* synthetic */ AudioManager f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void run() {
                            VoIPService.lambda$onDestroy$59(this.f$0);
                        }
                    };
                    setModeRunnable = r5;
                    dispatchQueue3.postRunnable(r5);
                }
                audioManager.abandonAudioFocus(this);
            }
            audioManager.unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (this.hasAudioFocus) {
                audioManager.abandonAudioFocus(this);
            }
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$onDestroy$60$VoIPService();
                }
            });
        }
        if (USE_CONNECTION_SERVICE) {
            if (!this.didDeleteConnectionServiceContact) {
                ContactsController.getInstance(this.currentAccount).deleteConnectionServiceContact();
            }
            CallConnection callConnection = this.systemCallConnection;
            if (callConnection != null && !this.playingSound) {
                callConnection.destroy();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        VoIPHelper.lastCallTime = SystemClock.elapsedRealtime();
        setSinks((VideoSink) null, (VideoSink) null);
        Runnable runnable3 = this.onDestroyRunnable;
        if (runnable3 != null) {
            runnable3.run();
        }
        if (ChatObject.isChannel(this.chat)) {
            MessagesController.getInstance(this.currentAccount).startShortPoll(this.chat, this.classGuid, true);
        }
    }

    static /* synthetic */ void lambda$onDestroy$59(AudioManager audioManager) {
        synchronized (sync) {
            if (setModeRunnable != null) {
                setModeRunnable = null;
                try {
                    audioManager.setMode(0);
                } catch (SecurityException e) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Error setting audio more to normal", e);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onDestroy$60 */
    public /* synthetic */ void lambda$onDestroy$60$VoIPService() {
        this.soundPool.release();
    }

    public long getCallID() {
        TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
        if (tLRPC$PhoneCall != null) {
            return tLRPC$PhoneCall.id;
        }
        return 0;
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

    public void acceptIncomingCall() {
        MessagesController.getInstance(this.currentAccount).ignoreSetOnline = false;
        stopRinging();
        showNotification();
        configureDeviceForCall();
        startConnectingSound();
        dispatchStateChanged(12);
        AndroidUtilities.runOnUIThread($$Lambda$VoIPService$h4QVsuxxWhIlvz6R7XbTl0w_tw.INSTANCE);
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
                VoIPService.this.lambda$acceptIncomingCall$64$VoIPService(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$acceptIncomingCall$64 */
    public /* synthetic */ void lambda$acceptIncomingCall$64$VoIPService(MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    VoIPService.this.lambda$acceptIncomingCall$63$VoIPService(tLObject, tLRPC$TL_error);
                }
            }, 2);
            return;
        }
        callFailed();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$acceptIncomingCall$63 */
    public /* synthetic */ void lambda$acceptIncomingCall$63$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPService.this.lambda$acceptIncomingCall$62$VoIPService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$acceptIncomingCall$62 */
    public /* synthetic */ void lambda$acceptIncomingCall$62$VoIPService(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
                    VoIPService.this.lambda$declineIncomingCall$65$VoIPService();
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
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            tLRPC$TL_phone_discardCall.connection_id = nativeInstanceArr[0] != null ? nativeInstanceArr[0].getPreferredRelayId() : 0;
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
                    VoIPService.this.lambda$declineIncomingCall$66$VoIPService(tLObject, tLRPC$TL_error);
                }
            }, 2);
            this.onDestroyRunnable = runnable;
            callEnded();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$declineIncomingCall$65 */
    public /* synthetic */ void lambda$declineIncomingCall$65$VoIPService() {
        if (this.currentState == 10) {
            callEnded();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$declineIncomingCall$66 */
    public /* synthetic */ void lambda$declineIncomingCall$66$VoIPService(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

    public void declineIncomingCall() {
        declineIncomingCall(1, (Runnable) null);
    }

    private Class<? extends Activity> getUIActivityClass() {
        return LaunchActivity.class;
    }

    @TargetApi(26)
    public CallConnection getConnectionAndStartCall() {
        if (this.systemCallConnection == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("creating call connection");
            }
            CallConnection callConnection = new CallConnection();
            this.systemCallConnection = callConnection;
            callConnection.setInitializing();
            if (this.isOutgoing) {
                $$Lambda$VoIPService$MSEpZjxLrDPWz5IoJEd2aBD1SQ r0 = new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$getConnectionAndStartCall$67$VoIPService();
                    }
                };
                this.delayedStartOutgoingCall = r0;
                AndroidUtilities.runOnUIThread(r0, 2000);
            }
            CallConnection callConnection2 = this.systemCallConnection;
            callConnection2.setAddress(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), 1);
            CallConnection callConnection3 = this.systemCallConnection;
            TLRPC$User tLRPC$User = this.user;
            callConnection3.setCallerDisplayName(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), 1);
        }
        return this.systemCallConnection;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getConnectionAndStartCall$67 */
    public /* synthetic */ void lambda$getConnectionAndStartCall$67$VoIPService() {
        this.delayedStartOutgoingCall = null;
        startOutgoingCall();
    }

    /* access modifiers changed from: private */
    public void startRinging() {
        CallConnection callConnection;
        if (this.currentState != 15) {
            if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
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

    private void updateServerConfig() {
        SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        Instance.setGlobalServerConfig(mainSettings.getString("voip_server_config", "{}"));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_phone_getCallConfig(), new RequestDelegate(mainSettings) {
            public final /* synthetic */ SharedPreferences f$0;

            {
                this.f$0 = r1;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.lambda$updateServerConfig$68(this.f$0, tLObject, tLRPC$TL_error);
            }
        });
    }

    static /* synthetic */ void lambda$updateServerConfig$68(SharedPreferences sharedPreferences, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            String str = ((TLRPC$TL_dataJSON) tLObject).data;
            Instance.setGlobalServerConfig(str);
            sharedPreferences.edit().putString("voip_server_config", str).commit();
        }
    }

    private void showNotification() {
        TLRPC$User tLRPC$User = this.user;
        if (tLRPC$User != null) {
            showNotification(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), getRoundAvatarBitmap(this.user));
            return;
        }
        TLRPC$Chat tLRPC$Chat = this.chat;
        showNotification(tLRPC$Chat.title, getRoundAvatarBitmap(tLRPC$Chat));
    }

    private void onTgVoipStop(Instance.FinalState finalState) {
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_saveCallDebug, $$Lambda$VoIPService$LLvwxjtYKlL0Si3GcnBBqaO7cyE.INSTANCE);
                this.needSendDebugLog = false;
            }
        }
    }

    static /* synthetic */ void lambda$onTgVoipStop$69(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Sent debug logs, response = " + tLObject);
        }
    }

    private void initializeAccountRelatedThings() {
        updateServerConfig();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
    }

    @SuppressLint({"InvalidWakeLockTag"})
    public void onCreate() {
        BluetoothAdapter bluetoothAdapter;
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STARTING ===============");
        }
        try {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            int i = Build.VERSION.SDK_INT;
            if (i < 17 || audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") == null) {
                Instance.setBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
            } else {
                Instance.setBufferSize(Integer.parseInt(audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")));
            }
            boolean z = true;
            PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "telegram-voip");
            this.cpuWakelock = newWakeLock;
            newWakeLock.acquire();
            this.btAdapter = audioManager.isBluetoothScoAvailableOffCall() ? BluetoothAdapter.getDefaultAdapter() : null;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            boolean z2 = USE_CONNECTION_SERVICE;
            if (!z2) {
                intentFilter.addAction("android.intent.action.HEADSET_PLUG");
                if (this.btAdapter != null) {
                    intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                    intentFilter.addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
                }
                intentFilter.addAction("android.intent.action.PHONE_STATE");
                intentFilter.addAction("android.intent.action.SCREEN_ON");
                intentFilter.addAction("android.intent.action.SCREEN_OFF");
            }
            registerReceiver(this.receiver, intentFilter);
            fetchBluetoothDeviceName();
            audioManager.registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (!z2 && (bluetoothAdapter = this.btAdapter) != null && bluetoothAdapter.isEnabled()) {
                try {
                    MediaRouter mediaRouter = (MediaRouter) getSystemService("media_router");
                    if (i < 24) {
                        if (this.btAdapter.getProfileConnectionState(1) != 2) {
                            z = false;
                        }
                        updateBluetoothHeadsetState(z);
                        Iterator<StateListener> it = this.stateListeners.iterator();
                        while (it.hasNext()) {
                            it.next().onAudioSettingsChanged();
                        }
                    } else if (mediaRouter.getSelectedRoute(1).getDeviceType() == 3) {
                        if (this.btAdapter.getProfileConnectionState(1) != 2) {
                            z = false;
                        }
                        updateBluetoothHeadsetState(z);
                        Iterator<StateListener> it2 = this.stateListeners.iterator();
                        while (it2.hasNext()) {
                            it2.next().onAudioSettingsChanged();
                        }
                    } else {
                        updateBluetoothHeadsetState(false);
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error initializing voip controller", e);
            }
            callFailed();
        }
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

    private void loadResources() {
        if (Build.VERSION.SDK_INT >= 21) {
            WebRtcAudioTrack.setAudioTrackUsageAttribute(2);
        }
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VoIPService.this.lambda$loadResources$70$VoIPService();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadResources$70 */
    public /* synthetic */ void lambda$loadResources$70$VoIPService() {
        SoundPool soundPool2 = new SoundPool(1, 0, 0);
        this.soundPool = soundPool2;
        this.spConnectingId = soundPool2.load(this, NUM, 1);
        this.spRingbackID = this.soundPool.load(this, NUM, 1);
        this.spFailedID = this.soundPool.load(this, NUM, 1);
        this.spEndId = this.soundPool.load(this, NUM, 1);
        this.spBusyId = this.soundPool.load(this, NUM, 1);
        this.spVoiceChatEndId = this.soundPool.load(this, NUM, 1);
        this.spVoiceChatStartId = this.soundPool.load(this, NUM, 1);
        this.spVoiceChatConnecting = this.soundPool.load(this, NUM, 1);
        this.spAllowTalkId = this.soundPool.load(this, NUM, 1);
        this.spStartRecordId = this.soundPool.load(this, NUM, 1);
    }

    private void dispatchStateChanged(int i) {
        CallConnection callConnection;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("== Call " + getCallID() + " state changed to " + i + " ==");
        }
        this.currentState = i;
        if (USE_CONNECTION_SERVICE && i == 3 && (callConnection = this.systemCallConnection) != null) {
            callConnection.setActive();
        }
        for (int i2 = 0; i2 < this.stateListeners.size(); i2++) {
            this.stateListeners.get(i2).onStateChanged(i);
        }
    }

    /* access modifiers changed from: private */
    public void updateTrafficStats(NativeInstance nativeInstance, Instance.TrafficStats trafficStats) {
        if (trafficStats == null) {
            trafficStats = nativeInstance.getTrafficStats();
        }
        long j = trafficStats.bytesSentWifi;
        Instance.TrafficStats trafficStats2 = this.prevTrafficStats;
        long j2 = j - (trafficStats2 != null ? trafficStats2.bytesSentWifi : 0);
        long j3 = trafficStats.bytesReceivedWifi - (trafficStats2 != null ? trafficStats2.bytesReceivedWifi : 0);
        long j4 = trafficStats.bytesSentMobile - (trafficStats2 != null ? trafficStats2.bytesSentMobile : 0);
        long j5 = trafficStats.bytesReceivedMobile - (trafficStats2 != null ? trafficStats2.bytesReceivedMobile : 0);
        this.prevTrafficStats = trafficStats;
        if (j2 > 0) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(1, 0, j2);
        }
        if (j3 > 0) {
            StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(1, 0, j3);
        }
        int i = 2;
        if (j4 > 0) {
            StatsController instance = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo = this.lastNetInfo;
            instance.incrementSentBytesCount((networkInfo == null || !networkInfo.isRoaming()) ? 0 : 2, 0, j4);
        }
        if (j5 > 0) {
            StatsController instance2 = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo2 = this.lastNetInfo;
            if (networkInfo2 == null || !networkInfo2.isRoaming()) {
                i = 0;
            }
            instance2.incrementReceivedBytesCount(i, 0, j5);
        }
    }

    @SuppressLint({"InvalidWakeLockTag"})
    private void configureDeviceForCall() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("configureDeviceForCall, route to set = " + this.audioRouteToSet);
        }
        this.needPlayEndSound = true;
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (!USE_CONNECTION_SERVICE) {
            Utilities.globalQueue.postRunnable(new Runnable(audioManager) {
                public final /* synthetic */ AudioManager f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VoIPService.this.lambda$configureDeviceForCall$72$VoIPService(this.f$1);
                }
            });
        }
        SensorManager sensorManager = (SensorManager) getSystemService("sensor");
        Sensor defaultSensor = sensorManager.getDefaultSensor(8);
        if (defaultSensor != null) {
            try {
                this.proximityWakelock = ((PowerManager) getSystemService("power")).newWakeLock(32, "telegram-voip-prx");
                sensorManager.registerListener(this, defaultSensor, 3);
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error initializing proximity sensor", e);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$configureDeviceForCall$72 */
    public /* synthetic */ void lambda$configureDeviceForCall$72$VoIPService(AudioManager audioManager) {
        try {
            audioManager.setMode(3);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AndroidUtilities.runOnUIThread(new Runnable(audioManager) {
            public final /* synthetic */ AudioManager f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPService.this.lambda$configureDeviceForCall$71$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$configureDeviceForCall$71 */
    public /* synthetic */ void lambda$configureDeviceForCall$71$VoIPService(AudioManager audioManager) {
        audioManager.requestAudioFocus(this, 0, 1);
        if (isBluetoothHeadsetConnected() && hasEarpiece()) {
            int i = this.audioRouteToSet;
            if (i == 0) {
                audioManager.setBluetoothScoOn(false);
                audioManager.setSpeakerphoneOn(false);
            } else if (i == 1) {
                audioManager.setBluetoothScoOn(false);
                audioManager.setSpeakerphoneOn(true);
            } else if (i == 2) {
                if (!this.bluetoothScoActive) {
                    this.needSwitchToBluetoothAfterScoActivates = true;
                    try {
                        audioManager.startBluetoothSco();
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                } else {
                    audioManager.setBluetoothScoOn(true);
                    audioManager.setSpeakerphoneOn(false);
                }
            }
        } else if (isBluetoothHeadsetConnected()) {
            audioManager.setBluetoothScoOn(this.speakerphoneStateToSet);
        } else {
            audioManager.setSpeakerphoneOn(this.speakerphoneStateToSet);
        }
        updateOutputGainControlState();
        this.audioConfigured = true;
    }

    /* access modifiers changed from: private */
    public void fetchBluetoothDeviceName() {
        if (!this.fetchingBluetoothDeviceName) {
            try {
                this.currentBluetoothDeviceName = null;
                this.fetchingBluetoothDeviceName = true;
                BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, this.serviceListener, 1);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    @SuppressLint({"NewApi"})
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!this.unmutedByHold && this.remoteVideoState != 2) {
            boolean z = false;
            if (this.videoState[0] != 2 && sensorEvent.sensor.getType() == 8) {
                AudioManager audioManager = (AudioManager) getSystemService("audio");
                if (!this.isHeadsetPlugged && !audioManager.isSpeakerphoneOn()) {
                    if (!isBluetoothHeadsetConnected() || !audioManager.isBluetoothScoOn()) {
                        if (sensorEvent.values[0] < Math.min(sensorEvent.sensor.getMaximumRange(), 3.0f)) {
                            z = true;
                        }
                        checkIsNear(z);
                    }
                }
            }
        }
    }

    private void checkIsNear() {
        if (this.remoteVideoState == 2 || this.videoState[0] == 2) {
            checkIsNear(false);
        }
    }

    private void checkIsNear(boolean z) {
        if (z != this.isProximityNear) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("proximity " + z);
            }
            this.isProximityNear = z;
            if (z) {
                try {
                    this.proximityWakelock.acquire();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                this.proximityWakelock.release(1);
            }
        }
    }

    public boolean isBluetoothHeadsetConnected() {
        CallConnection callConnection;
        if (!USE_CONNECTION_SERVICE || (callConnection = this.systemCallConnection) == null || callConnection.getCallAudioState() == null) {
            return this.isBtHeadsetConnected;
        }
        return (this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 2) != 0;
    }

    public void onAudioFocusChange(int i) {
        if (i == 1) {
            this.hasAudioFocus = true;
        } else {
            this.hasAudioFocus = false;
        }
    }

    /* access modifiers changed from: private */
    public void updateBluetoothHeadsetState(boolean z) {
        if (z != this.isBtHeadsetConnected) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("updateBluetoothHeadsetState: " + z);
            }
            this.isBtHeadsetConnected = z;
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (!z || isRinging() || this.currentState == 0) {
                this.bluetoothScoActive = false;
                this.bluetoothScoConnecting = false;
            } else if (this.bluetoothScoActive) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("SCO already active, setting audio routing");
                }
                audioManager.setSpeakerphoneOn(false);
                audioManager.setBluetoothScoOn(true);
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("startBluetoothSco");
                }
                this.needSwitchToBluetoothAfterScoActivates = true;
                AndroidUtilities.runOnUIThread(new Runnable(audioManager) {
                    public final /* synthetic */ AudioManager f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        VoIPService.lambda$updateBluetoothHeadsetState$73(this.f$0);
                    }
                }, 500);
            }
            Iterator<StateListener> it = this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
        }
    }

    static /* synthetic */ void lambda$updateBluetoothHeadsetState$73(AudioManager audioManager) {
        try {
            audioManager.startBluetoothSco();
        } catch (Throwable unused) {
        }
    }

    public String getLastError() {
        return this.lastError;
    }

    public int getCallState() {
        return this.currentState;
    }

    public TLRPC$InputPeer getGroupCallPeer() {
        return this.groupCallPeer;
    }

    /* access modifiers changed from: private */
    public void updateNetworkType() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] == null) {
            this.lastNetInfo = getActiveNetworkInfo();
        } else if (!nativeInstanceArr[0].isGroup()) {
            this.tgVoip[0].setNetworkType(getNetworkType());
        }
    }

    private int getNetworkType() {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo();
        this.lastNetInfo = activeNetworkInfo;
        if (activeNetworkInfo != null) {
            int type = activeNetworkInfo.getType();
            if (type == 0) {
                switch (activeNetworkInfo.getSubtype()) {
                    case 1:
                        return 1;
                    case 2:
                    case 7:
                        return 2;
                    case 3:
                    case 5:
                        return 3;
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 15:
                        return 4;
                    case 13:
                        return 5;
                    default:
                        return 11;
                }
            } else if (type == 1) {
                return 6;
            } else {
                if (type != 9) {
                    return 0;
                }
                return 7;
            }
        }
        return 0;
    }

    private NetworkInfo getActiveNetworkInfo() {
        return ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
    }

    /* access modifiers changed from: private */
    public void callFailed() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        callFailed(nativeInstanceArr[0] != null ? nativeInstanceArr[0].getLastError() : "ERROR_UNKNOWN");
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x0090  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.Bitmap getRoundAvatarBitmap(org.telegram.tgnet.TLObject r8) {
        /*
            r7 = this;
            r0 = 0
            r1 = 1
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$User     // Catch:{ all -> 0x008a }
            java.lang.String r3 = "50_50"
            if (r2 == 0) goto L_0x0049
            r2 = r8
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2     // Catch:{ all -> 0x008a }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r4 = r2.photo     // Catch:{ all -> 0x008a }
            if (r4 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$FileLocation r4 = r4.photo_small     // Catch:{ all -> 0x008a }
            if (r4 == 0) goto L_0x008e
            org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ all -> 0x008a }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r2.photo     // Catch:{ all -> 0x008a }
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ all -> 0x008a }
            android.graphics.drawable.BitmapDrawable r3 = r4.getImageFromMemory(r5, r0, r3)     // Catch:{ all -> 0x008a }
            if (r3 == 0) goto L_0x002c
            android.graphics.Bitmap r2 = r3.getBitmap()     // Catch:{ all -> 0x008a }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x008a }
            android.graphics.Bitmap r0 = r2.copy(r3, r1)     // Catch:{ all -> 0x008a }
            goto L_0x008e
        L_0x002c:
            android.graphics.BitmapFactory$Options r3 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0044 }
            r3.<init>()     // Catch:{ all -> 0x0044 }
            r3.inMutable = r1     // Catch:{ all -> 0x0044 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r2.photo     // Catch:{ all -> 0x0044 }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ all -> 0x0044 }
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r1)     // Catch:{ all -> 0x0044 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0044 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r2, r3)     // Catch:{ all -> 0x0044 }
            goto L_0x008e
        L_0x0044:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x008a }
            goto L_0x008e
        L_0x0049:
            r2 = r8
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2     // Catch:{ all -> 0x008a }
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r2.photo     // Catch:{ all -> 0x008a }
            if (r4 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$FileLocation r4 = r4.photo_small     // Catch:{ all -> 0x008a }
            if (r4 == 0) goto L_0x008e
            org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ all -> 0x008a }
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r2.photo     // Catch:{ all -> 0x008a }
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ all -> 0x008a }
            android.graphics.drawable.BitmapDrawable r3 = r4.getImageFromMemory(r5, r0, r3)     // Catch:{ all -> 0x008a }
            if (r3 == 0) goto L_0x006d
            android.graphics.Bitmap r2 = r3.getBitmap()     // Catch:{ all -> 0x008a }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x008a }
            android.graphics.Bitmap r0 = r2.copy(r3, r1)     // Catch:{ all -> 0x008a }
            goto L_0x008e
        L_0x006d:
            android.graphics.BitmapFactory$Options r3 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0085 }
            r3.<init>()     // Catch:{ all -> 0x0085 }
            r3.inMutable = r1     // Catch:{ all -> 0x0085 }
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r2.photo     // Catch:{ all -> 0x0085 }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ all -> 0x0085 }
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r1)     // Catch:{ all -> 0x0085 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0085 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r2, r3)     // Catch:{ all -> 0x0085 }
            goto L_0x008e
        L_0x0085:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x008a }
            goto L_0x008e
        L_0x008a:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x008e:
            if (r0 != 0) goto L_0x00cb
            org.telegram.ui.ActionBar.Theme.createDialogsResources(r7)
            boolean r0 = r8 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x009f
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC$User) r8
            r0.<init>((org.telegram.tgnet.TLRPC$User) r8)
            goto L_0x00a6
        L_0x009f:
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC$Chat) r8
            r0.<init>((org.telegram.tgnet.TLRPC$Chat) r8)
        L_0x00a6:
            r8 = 1109917696(0x42280000, float:42.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r2, r8, r3)
            int r2 = r8.getWidth()
            int r3 = r8.getHeight()
            r4 = 0
            r0.setBounds(r4, r4, r2, r3)
            android.graphics.Canvas r2 = new android.graphics.Canvas
            r2.<init>(r8)
            r0.draw(r2)
            r0 = r8
        L_0x00cb:
            android.graphics.Canvas r8 = new android.graphics.Canvas
            r8.<init>(r0)
            android.graphics.Path r2 = new android.graphics.Path
            r2.<init>()
            int r3 = r0.getWidth()
            int r3 = r3 / 2
            float r3 = (float) r3
            int r4 = r0.getHeight()
            int r4 = r4 / 2
            float r4 = (float) r4
            int r5 = r0.getWidth()
            int r5 = r5 / 2
            float r5 = (float) r5
            android.graphics.Path$Direction r6 = android.graphics.Path.Direction.CW
            r2.addCircle(r3, r4, r5, r6)
            r2.toggleInverseFillType()
            android.graphics.Paint r3 = new android.graphics.Paint
            r3.<init>(r1)
            android.graphics.PorterDuffXfermode r1 = new android.graphics.PorterDuffXfermode
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.CLEAR
            r1.<init>(r4)
            r3.setXfermode(r1)
            r8.drawPath(r2, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.getRoundAvatarBitmap(org.telegram.tgnet.TLObject):android.graphics.Bitmap");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r7v11 */
    /* JADX WARNING: type inference failed for: r7v12 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r7v4, types: [boolean, int] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x013a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showIncomingNotification(java.lang.String r19, java.lang.CharSequence r20, org.telegram.tgnet.TLObject r21, boolean r22, int r23) {
        /*
            r18 = this;
            r1 = r18
            r0 = r19
            r2 = r20
            r3 = r21
            android.content.Intent r4 = new android.content.Intent
            java.lang.Class<org.telegram.ui.LaunchActivity> r5 = org.telegram.ui.LaunchActivity.class
            r4.<init>(r1, r5)
            java.lang.String r5 = "voip"
            r4.setAction(r5)
            android.app.Notification$Builder r5 = new android.app.Notification$Builder
            r5.<init>(r1)
            r6 = 2131628200(0x7f0e10a8, float:1.8883686E38)
            java.lang.String r7 = "VoipInVideoCallBranding"
            r8 = 2131628198(0x7f0e10a6, float:1.8883682E38)
            java.lang.String r9 = "VoipInCallBranding"
            if (r22 == 0) goto L_0x002a
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x002e
        L_0x002a:
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r9, r8)
        L_0x002e:
            android.app.Notification$Builder r5 = r5.setContentTitle(r10)
            android.app.Notification$Builder r5 = r5.setContentText(r0)
            r10 = 2131165867(0x7var_ab, float:1.7945963E38)
            android.app.Notification$Builder r5 = r5.setSmallIcon(r10)
            android.app.Notification$Builder r5 = r5.setSubText(r2)
            r10 = 0
            android.app.PendingIntent r11 = android.app.PendingIntent.getActivity(r1, r10, r4, r10)
            android.app.Notification$Builder r5 = r5.setContentIntent(r11)
            java.lang.String r11 = "content://org.telegram.messenger.beta.call_sound_provider/start_ringing"
            android.net.Uri r11 = android.net.Uri.parse(r11)
            int r12 = android.os.Build.VERSION.SDK_INT
            r13 = 26
            if (r12 < r13) goto L_0x014f
            android.content.SharedPreferences r13 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings()
            java.lang.String r6 = "calls_notification_channel"
            int r14 = r13.getInt(r6, r10)
            java.lang.String r8 = "notification"
            java.lang.Object r8 = r1.getSystemService(r8)
            android.app.NotificationManager r8 = (android.app.NotificationManager) r8
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r15 = "incoming_calls2"
            r10.append(r15)
            r10.append(r14)
            java.lang.String r10 = r10.toString()
            android.app.NotificationChannel r10 = r8.getNotificationChannel(r10)
            if (r10 == 0) goto L_0x0086
            java.lang.String r10 = r10.getId()
            r8.deleteNotificationChannel(r10)
        L_0x0086:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r15 = "incoming_calls3"
            r10.append(r15)
            r10.append(r14)
            java.lang.String r10 = r10.toString()
            android.app.NotificationChannel r10 = r8.getNotificationChannel(r10)
            r2 = 4
            r16 = r9
            if (r10 == 0) goto L_0x00e7
            int r9 = r10.getImportance()
            if (r9 < r2) goto L_0x00bf
            android.net.Uri r9 = r10.getSound()
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x00bf
            long[] r9 = r10.getVibrationPattern()
            if (r9 != 0) goto L_0x00bf
            boolean r9 = r10.shouldVibrate()
            if (r9 == 0) goto L_0x00bd
            goto L_0x00bf
        L_0x00bd:
            r6 = 0
            goto L_0x00e8
        L_0x00bf:
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r9 == 0) goto L_0x00c8
            java.lang.String r9 = "User messed up the notification channel; deleting it and creating a proper one"
            org.telegram.messenger.FileLog.d(r9)
        L_0x00c8:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r15)
            r9.append(r14)
            java.lang.String r9 = r9.toString()
            r8.deleteNotificationChannel(r9)
            int r14 = r14 + 1
            android.content.SharedPreferences$Editor r9 = r13.edit()
            android.content.SharedPreferences$Editor r6 = r9.putInt(r6, r14)
            r6.commit()
        L_0x00e7:
            r6 = 1
        L_0x00e8:
            if (r6 == 0) goto L_0x013a
            android.media.AudioAttributes$Builder r6 = new android.media.AudioAttributes$Builder
            r6.<init>()
            android.media.AudioAttributes$Builder r6 = r6.setContentType(r2)
            r9 = 2
            android.media.AudioAttributes$Builder r6 = r6.setLegacyStreamType(r9)
            android.media.AudioAttributes$Builder r6 = r6.setUsage(r9)
            android.media.AudioAttributes r6 = r6.build()
            android.app.NotificationChannel r9 = new android.app.NotificationChannel
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r15)
            r10.append(r14)
            java.lang.String r10 = r10.toString()
            r13 = 2131625851(0x7f0e077b, float:1.8878922E38)
            r17 = r7
            java.lang.String r7 = "IncomingCalls"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r13)
            r9.<init>(r10, r7, r2)
            r9.setSound(r11, r6)
            r2 = 0
            r9.enableVibration(r2)
            r9.enableLights(r2)
            r2 = 1
            r9.setBypassDnd(r2)
            r8.createNotificationChannel(r9)     // Catch:{ Exception -> 0x0131 }
            goto L_0x013c
        L_0x0131:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
            r18.stopSelf()
            return
        L_0x013a:
            r17 = r7
        L_0x013c:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            r5.setChannelId(r2)
            goto L_0x015b
        L_0x014f:
            r17 = r7
            r16 = r9
            r2 = 21
            if (r12 < r2) goto L_0x015b
            r2 = 2
            r5.setSound(r11, r2)
        L_0x015b:
            android.content.Intent r2 = new android.content.Intent
            java.lang.Class<org.telegram.messenger.voip.VoIPActionsReceiver> r6 = org.telegram.messenger.voip.VoIPActionsReceiver.class
            r2.<init>(r1, r6)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = r18.getPackageName()
            r6.append(r7)
            java.lang.String r7 = ".DECLINE_CALL"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r2.setAction(r6)
            long r6 = r18.getCallID()
            java.lang.String r8 = "call_id"
            r2.putExtra(r8, r6)
            java.lang.String r6 = "VoipDeclineCall"
            r7 = 2131628083(0x7f0e1033, float:1.8883449E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r6, r7)
            r10 = 24
            if (r12 < r10) goto L_0x01a7
            android.text.SpannableString r11 = new android.text.SpannableString
            r11.<init>(r9)
            android.text.style.ForegroundColorSpan r9 = new android.text.style.ForegroundColorSpan
            r13 = -769226(0xffffffffffvar_, float:NaN)
            r9.<init>(r13)
            int r13 = r11.length()
            r14 = 0
            r11.setSpan(r9, r14, r13, r14)
            r9 = r11
            goto L_0x01a8
        L_0x01a7:
            r14 = 0
        L_0x01a8:
            r11 = 268435456(0x10000000, float:2.5243549E-29)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r1, r14, r2, r11)
            r13 = 2131165482(0x7var_a, float:1.7945182E38)
            r5.addAction(r13, r9, r2)
            android.content.Intent r9 = new android.content.Intent
            java.lang.Class<org.telegram.messenger.voip.VoIPActionsReceiver> r13 = org.telegram.messenger.voip.VoIPActionsReceiver.class
            r9.<init>(r1, r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = r18.getPackageName()
            r13.append(r14)
            java.lang.String r14 = ".ANSWER_CALL"
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r9.setAction(r13)
            long r13 = r18.getCallID()
            r9.putExtra(r8, r13)
            java.lang.String r8 = "VoipAnswerCall"
            r13 = 2131628058(0x7f0e101a, float:1.8883398E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r8, r13)
            if (r12 < r10) goto L_0x01fc
            android.text.SpannableString r10 = new android.text.SpannableString
            r10.<init>(r14)
            android.text.style.ForegroundColorSpan r14 = new android.text.style.ForegroundColorSpan
            r15 = -16733696(0xfffffffffvar_aa00, float:-1.7102387E38)
            r14.<init>(r15)
            int r15 = r10.length()
            r7 = 0
            r10.setSpan(r14, r7, r15, r7)
            r14 = r10
            goto L_0x01fd
        L_0x01fc:
            r7 = 0
        L_0x01fd:
            android.app.PendingIntent r9 = android.app.PendingIntent.getBroadcast(r1, r7, r9, r11)
            r10 = 2131165481(0x7var_, float:1.794518E38)
            r5.addAction(r10, r14, r9)
            r10 = 2
            r5.setPriority(r10)
            r10 = 17
            if (r12 < r10) goto L_0x0212
            r5.setShowWhen(r7)
        L_0x0212:
            r10 = 21
            if (r12 < r10) goto L_0x0253
            r10 = -13851168(0xffffffffff2ca5e0, float:-2.2948849E38)
            r5.setColor(r10)
            long[] r10 = new long[r7]
            r5.setVibrate(r10)
            java.lang.String r10 = "call"
            r5.setCategory(r10)
            android.app.PendingIntent r4 = android.app.PendingIntent.getActivity(r1, r7, r4, r7)
            r7 = 1
            r5.setFullScreenIntent(r4, r7)
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r4 == 0) goto L_0x0253
            r4 = r3
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            java.lang.String r7 = r4.phone
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0253
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r10 = "tel:"
            r7.append(r10)
            java.lang.String r4 = r4.phone
            r7.append(r4)
            java.lang.String r4 = r7.toString()
            r5.addPerson(r4)
        L_0x0253:
            android.app.Notification r4 = r5.getNotification()
            r7 = 21
            if (r12 < r7) goto L_0x0345
            android.widget.RemoteViews r7 = new android.widget.RemoteViews
            java.lang.String r10 = r18.getPackageName()
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x0269
            r11 = 2131427329(0x7f0b0001, float:1.8476271E38)
            goto L_0x026b
        L_0x0269:
            r11 = 2131427328(0x7f0b0000, float:1.847627E38)
        L_0x026b:
            r7.<init>(r10, r11)
            r10 = 2131230860(0x7var_c, float:1.8077785E38)
            r7.setTextViewText(r10, r0)
            boolean r0 = android.text.TextUtils.isEmpty(r20)
            r10 = 8
            r11 = 2131230936(0x7var_d8, float:1.8077939E38)
            r12 = 2131230913(0x7var_c1, float:1.8077892E38)
            if (r0 == 0) goto L_0x02de
            r7.setViewVisibility(r12, r10)
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r10 = 1
            if (r0 <= r10) goto L_0x02c9
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
            if (r22 == 0) goto L_0x02af
            r12 = 2131628201(0x7f0e10a9, float:1.8883688E38)
            java.lang.Object[] r10 = new java.lang.Object[r10]
            java.lang.String r14 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r14, r0)
            r14 = 0
            r10[r14] = r0
            java.lang.String r0 = "VoipInVideoCallBrandingWithName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r12, r10)
            goto L_0x02c5
        L_0x02af:
            r14 = 0
            r12 = 2131628199(0x7f0e10a7, float:1.8883684E38)
            java.lang.Object[] r10 = new java.lang.Object[r10]
            java.lang.String r15 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r15, r0)
            r10[r14] = r0
            java.lang.String r0 = "VoipInCallBrandingWithName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r12, r10)
        L_0x02c5:
            r7.setTextViewText(r11, r0)
            goto L_0x0311
        L_0x02c9:
            if (r22 == 0) goto L_0x02d1
            r10 = r17
            r0 = 2131628200(0x7f0e10a8, float:1.8883686E38)
            goto L_0x02d6
        L_0x02d1:
            r10 = r16
            r0 = 2131628198(0x7f0e10a6, float:1.8883682E38)
        L_0x02d6:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
            r7.setTextViewText(r11, r0)
            goto L_0x0311
        L_0x02de:
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r14 = 1
            if (r0 <= r14) goto L_0x0309
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
            r10 = 2131628059(0x7f0e101b, float:1.88834E38)
            java.lang.Object[] r14 = new java.lang.Object[r14]
            java.lang.String r15 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r15, r0)
            r15 = 0
            r14[r15] = r0
            java.lang.String r0 = "VoipAnsweringAsAccount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r10, r14)
            r7.setTextViewText(r12, r0)
            goto L_0x030c
        L_0x0309:
            r7.setViewVisibility(r12, r10)
        L_0x030c:
            r0 = r20
            r7.setTextViewText(r11, r0)
        L_0x0311:
            android.graphics.Bitmap r0 = r1.getRoundAvatarBitmap(r3)
            r3 = 2131230769(0x7var_, float:1.80776E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r13)
            r7.setTextViewText(r3, r8)
            r3 = 2131230803(0x7var_, float:1.807767E38)
            r8 = 2131628083(0x7f0e1033, float:1.8883449E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r8)
            r7.setTextViewText(r3, r6)
            r3 = 2131230882(0x7var_a2, float:1.807783E38)
            r7.setImageViewBitmap(r3, r0)
            r3 = 2131230768(0x7var_, float:1.8077598E38)
            r7.setOnClickPendingIntent(r3, r9)
            r3 = 2131230802(0x7var_, float:1.8077667E38)
            r7.setOnClickPendingIntent(r3, r2)
            r5.setLargeIcon(r0)
            r4.bigContentView = r7
            r4.headsUpContentView = r7
        L_0x0345:
            r0 = 202(0xca, float:2.83E-43)
            r1.startForeground(r0, r4)
            r18.startRingtoneAndVibration()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.showIncomingNotification(java.lang.String, java.lang.CharSequence, org.telegram.tgnet.TLObject, boolean, int):void");
    }

    private void callFailed(String str) {
        CallConnection callConnection;
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
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            tLRPC$TL_phone_discardCall.connection_id = nativeInstanceArr[0] != null ? nativeInstanceArr[0].getPreferredRelayId() : 0;
            tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonDisconnect();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, $$Lambda$VoIPService$NYHw5DGxIxUkvZHftLkDNUN9KpY.INSTANCE);
        }
        try {
            throw new Exception("Call " + getCallID() + " failed with error: " + str);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.lastError = str;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$callFailed$75$VoIPService();
                }
            });
            if (TextUtils.equals(str, "ERROR_LOCALIZED") && this.soundPool != null) {
                this.playingSound = true;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$callFailed$76$VoIPService();
                    }
                });
                AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1000);
            }
            if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
                callConnection.setDisconnected(new DisconnectCause(1));
                this.systemCallConnection.destroy();
                this.systemCallConnection = null;
            }
            stopSelf();
        }
    }

    static /* synthetic */ void lambda$callFailed$74(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error on phone.discardCall: " + tLRPC$TL_error);
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("phone.discardCall " + tLObject);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$callFailed$75 */
    public /* synthetic */ void lambda$callFailed$75$VoIPService() {
        dispatchStateChanged(4);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$callFailed$76 */
    public /* synthetic */ void lambda$callFailed$76$VoIPService() {
        this.soundPool.play(this.spFailedID, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    /* access modifiers changed from: package-private */
    public void callFailedFromConnectionService() {
        if (this.isOutgoing) {
            callFailed("ERROR_CONNECTION_SERVICE");
        } else {
            hangUp();
        }
    }

    public void onConnectionStateChanged(int i, boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPService.this.lambda$onConnectionStateChanged$79$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onConnectionStateChanged$79 */
    public /* synthetic */ void lambda$onConnectionStateChanged$79$VoIPService(int i) {
        if (i == 3 && this.callStartTime == 0) {
            this.callStartTime = SystemClock.elapsedRealtime();
        }
        if (i == 4) {
            callFailed();
            return;
        }
        if (i == 3) {
            Runnable runnable = this.connectingSoundRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.connectingSoundRunnable = null;
            }
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$onConnectionStateChanged$77$VoIPService();
                }
            });
            if (this.groupCall == null && !this.wasEstablished) {
                this.wasEstablished = true;
                if (!this.isProximityNear && !this.privateCall.video) {
                    Vibrator vibrator2 = (Vibrator) getSystemService("vibrator");
                    if (vibrator2.hasVibrator()) {
                        vibrator2.vibrate(100);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (VoIPService.this.tgVoip[0] != null) {
                            StatsController.getInstance(VoIPService.this.currentAccount).incrementTotalCallsTime(VoIPService.this.getStatsNetworkType(), 5);
                            AndroidUtilities.runOnUIThread(this, 5000);
                        }
                    }
                }, 5000);
                if (this.isOutgoing) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(getStatsNetworkType(), 0, 1);
                } else {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(getStatsNetworkType(), 0, 1);
                }
            }
        }
        if (i == 5) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$onConnectionStateChanged$78$VoIPService();
                }
            });
        }
        dispatchStateChanged(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onConnectionStateChanged$77 */
    public /* synthetic */ void lambda$onConnectionStateChanged$77$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onConnectionStateChanged$78 */
    public /* synthetic */ void lambda$onConnectionStateChanged$78$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.groupCall != null ? this.spVoiceChatConnecting : this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$playStartRecordSound$80 */
    public /* synthetic */ void lambda$playStartRecordSound$80$VoIPService() {
        this.soundPool.play(this.spStartRecordId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public void playStartRecordSound() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VoIPService.this.lambda$playStartRecordSound$80$VoIPService();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$playAllowTalkSound$81 */
    public /* synthetic */ void lambda$playAllowTalkSound$81$VoIPService() {
        this.soundPool.play(this.spAllowTalkId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public void playAllowTalkSound() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VoIPService.this.lambda$playAllowTalkSound$81$VoIPService();
            }
        });
    }

    public void onSignalBarCountChanged(int i) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPService.this.lambda$onSignalBarCountChanged$82$VoIPService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onSignalBarCountChanged$82 */
    public /* synthetic */ void lambda$onSignalBarCountChanged$82$VoIPService(int i) {
        this.signalBarCount = i;
        for (int i2 = 0; i2 < this.stateListeners.size(); i2++) {
            this.stateListeners.get(i2).onSignalBarsCountChanged(i);
        }
    }

    public boolean isBluetoothOn() {
        return ((AudioManager) getSystemService("audio")).isBluetoothScoOn();
    }

    public boolean isBluetoothWillOn() {
        return this.needSwitchToBluetoothAfterScoActivates;
    }

    public boolean isHeadsetPlugged() {
        return this.isHeadsetPlugged;
    }

    private void callEnded() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Call " + getCallID() + " ended");
        }
        if (this.groupCall != null && (!this.playedConnectedSound || this.onDestroyRunnable != null)) {
            this.needPlayEndSound = false;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                VoIPService.this.lambda$callEnded$83$VoIPService();
            }
        });
        int i = 700;
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VoIPService.this.lambda$callEnded$84$VoIPService();
            }
        });
        Runnable runnable = this.connectingSoundRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.connectingSoundRunnable = null;
        }
        if (this.needPlayEndSound) {
            this.playingSound = true;
            if (this.groupCall == null) {
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$callEnded$85$VoIPService();
                    }
                });
            } else {
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        VoIPService.this.lambda$callEnded$86$VoIPService();
                    }
                }, 100);
                i = 500;
            }
            AndroidUtilities.runOnUIThread(this.afterSoundRunnable, (long) i);
        }
        Runnable runnable2 = this.timeoutRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.timeoutRunnable = null;
        }
        endConnectionServiceCall(this.needPlayEndSound ? (long) i : 0);
        stopSelf();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$callEnded$83 */
    public /* synthetic */ void lambda$callEnded$83$VoIPService() {
        dispatchStateChanged(11);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$callEnded$84 */
    public /* synthetic */ void lambda$callEnded$84$VoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$callEnded$85 */
    public /* synthetic */ void lambda$callEnded$85$VoIPService() {
        this.soundPool.play(this.spEndId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$callEnded$86 */
    public /* synthetic */ void lambda$callEnded$86$VoIPService() {
        this.soundPool.play(this.spVoiceChatEndId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    private void endConnectionServiceCall(long j) {
        if (USE_CONNECTION_SERVICE) {
            $$Lambda$VoIPService$Tg0QuR5cc7TgPaKsiwU7cFWEOiU r0 = new Runnable() {
                public final void run() {
                    VoIPService.this.lambda$endConnectionServiceCall$87$VoIPService();
                }
            };
            if (j > 0) {
                AndroidUtilities.runOnUIThread(r0, j);
            } else {
                r0.run();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$endConnectionServiceCall$87 */
    public /* synthetic */ void lambda$endConnectionServiceCall$87$VoIPService() {
        CallConnection callConnection = this.systemCallConnection;
        if (callConnection != null) {
            int i = this.callDiscardReason;
            int i2 = 2;
            if (i == 1) {
                if (!this.isOutgoing) {
                    i2 = 6;
                }
                callConnection.setDisconnected(new DisconnectCause(i2));
            } else if (i != 2) {
                int i3 = 4;
                if (i == 3) {
                    if (!this.isOutgoing) {
                        i3 = 5;
                    }
                    callConnection.setDisconnected(new DisconnectCause(i3));
                } else if (i != 4) {
                    callConnection.setDisconnected(new DisconnectCause(3));
                } else {
                    callConnection.setDisconnected(new DisconnectCause(7));
                }
            } else {
                callConnection.setDisconnected(new DisconnectCause(1));
            }
            this.systemCallConnection.destroy();
            this.systemCallConnection = null;
        }
    }

    public boolean isOutgoing() {
        return this.isOutgoing;
    }

    public void handleNotificationAction(Intent intent) {
        if ((getPackageName() + ".END_CALL").equals(intent.getAction())) {
            stopForeground(true);
            hangUp();
            return;
        }
        if ((getPackageName() + ".DECLINE_CALL").equals(intent.getAction())) {
            stopForeground(true);
            declineIncomingCall(4, (Runnable) null);
            return;
        }
        if ((getPackageName() + ".ANSWER_CALL").equals(intent.getAction())) {
            acceptIncomingCallFromNotification();
        }
    }

    /* access modifiers changed from: private */
    public void acceptIncomingCallFromNotification() {
        showNotification();
        int i = Build.VERSION.SDK_INT;
        if (i < 23 || i >= 30 || (checkSelfPermission("android.permission.RECORD_AUDIO") == 0 && (!this.privateCall.video || checkSelfPermission("android.permission.CAMERA") == 0))) {
            acceptIncomingCall();
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, getUIActivityClass()).setAction("voip"), 0).send();
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting incall activity", e);
                }
            }
        } else {
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, VoIPPermissionActivity.class).addFlags(NUM), NUM).send();
            } catch (Exception e2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting permission activity", e2);
                }
            }
        }
    }

    public void updateOutputGainControlState() {
        int i = 0;
        if (this.tgVoip[0] == null) {
            return;
        }
        if (!USE_CONNECTION_SERVICE) {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            this.tgVoip[0].setAudioOutputGainControlEnabled(hasEarpiece() && !audioManager.isSpeakerphoneOn() && !audioManager.isBluetoothScoOn() && !this.isHeadsetPlugged);
            NativeInstance nativeInstance = this.tgVoip[0];
            if (!this.isHeadsetPlugged && (!hasEarpiece() || audioManager.isSpeakerphoneOn() || audioManager.isBluetoothScoOn() || this.isHeadsetPlugged)) {
                i = 1;
            }
            nativeInstance.setEchoCancellationStrength(i);
            return;
        }
        boolean z = this.systemCallConnection.getCallAudioState().getRoute() == 1;
        this.tgVoip[0].setAudioOutputGainControlEnabled(z);
        this.tgVoip[0].setEchoCancellationStrength(z ^ true ? 1 : 0);
    }

    public int getAccount() {
        return this.currentAccount;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.appDidLogout) {
            callEnded();
        }
    }

    public static boolean isAnyKindOfCallActive() {
        if (getSharedInstance() == null || getSharedInstance().getCallState() == 15) {
            return false;
        }
        return true;
    }

    private boolean isFinished() {
        int i = this.currentState;
        return i == 11 || i == 4;
    }

    public int getRemoteAudioState() {
        return this.remoteAudioState;
    }

    public int getRemoteVideoState() {
        return this.remoteVideoState;
    }

    @TargetApi(26)
    private PhoneAccountHandle addAccountToTelecomManager() {
        TLRPC$User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        ComponentName componentName = new ComponentName(this, TelegramConnectionService.class);
        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(componentName, "" + currentUser.id);
        ((TelecomManager) getSystemService("telecom")).registerPhoneAccount(new PhoneAccount.Builder(phoneAccountHandle, ContactsController.formatName(currentUser.first_name, currentUser.last_name)).setCapabilities(2048).setIcon(Icon.createWithResource(this, NUM)).setHighlightColor(-13851168).addSupportedUriScheme("sip").build());
        return phoneAccountHandle;
    }

    private static boolean isDeviceCompatibleWithConnectionServiceAPI() {
        if (Build.VERSION.SDK_INT < 26) {
        }
        return false;
    }

    public class CallConnection extends Connection {
        public CallConnection() {
            setConnectionProperties(128);
            setAudioModeIsVoip(true);
        }

        public void onCallAudioStateChanged(CallAudioState callAudioState) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService call audio state changed: " + callAudioState);
            }
            Iterator it = VoIPService.this.stateListeners.iterator();
            while (it.hasNext()) {
                ((StateListener) it.next()).onAudioSettingsChanged();
            }
        }

        public void onDisconnect() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onDisconnect");
            }
            setDisconnected(new DisconnectCause(2));
            destroy();
            CallConnection unused = VoIPService.this.systemCallConnection = null;
            VoIPService.this.hangUp();
        }

        public void onAnswer() {
            VoIPService.this.acceptIncomingCallFromNotification();
        }

        public void onReject() {
            boolean unused = VoIPService.this.needPlayEndSound = false;
            VoIPService.this.declineIncomingCall(1, (Runnable) null);
        }

        public void onShowIncomingCallUi() {
            VoIPService.this.startRinging();
        }

        public void onStateChanged(int i) {
            super.onStateChanged(i);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onStateChanged " + Connection.stateToString(i));
            }
            if (i == 4) {
                ContactsController.getInstance(VoIPService.this.currentAccount).deleteConnectionServiceContact();
                boolean unused = VoIPService.this.didDeleteConnectionServiceContact = true;
            }
        }

        public void onCallEvent(String str, Bundle bundle) {
            super.onCallEvent(str, bundle);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onCallEvent " + str);
            }
        }

        public void onSilence() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("onSlience");
            }
            VoIPService.this.stopRinging();
        }
    }
}
