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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
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
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
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
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$GroupCall;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Peer;
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
import org.telegram.tgnet.TLRPC$TL_phone_getGroupCallStreamChannels;
import org.telegram.tgnet.TLRPC$TL_phone_groupCallStreamChannels;
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
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$messages_DhConfig;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPFeedbackActivity;
import org.telegram.ui.VoIPFragment;
import org.telegram.ui.VoIPPermissionActivity;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;
import org.webrtc.voiceengine.WebRtcAudioTrack;
@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
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
    public static NativeInstance.AudioLevelsCallback audioLevelsCallback;
    public static TLRPC$PhoneCall callIShouldHavePutIntoIntent;
    private static Runnable setModeRunnable;
    private static VoIPService sharedInstance;
    private byte[] a_or_b;
    private boolean audioConfigured;
    private byte[] authKey;
    private boolean bluetoothScoActive;
    private boolean bluetoothScoConnecting;
    private BluetoothAdapter btAdapter;
    private int callDiscardReason;
    private int callReqId;
    private long callStartTime;
    private TLRPC$Chat chat;
    private int checkRequestId;
    private int classGuid;
    private Runnable connectingSoundRunnable;
    private PowerManager.WakeLock cpuWakelock;
    private boolean createGroupCall;
    public String currentBluetoothDeviceName;
    public boolean currentGroupModeStreaming;
    private Runnable delayedStartOutgoingCall;
    private boolean didDeleteConnectionServiceContact;
    private boolean endCallAfterRequest;
    boolean fetchingBluetoothDeviceName;
    private boolean forceRating;
    private byte[] g_a;
    private byte[] g_a_hash;
    public ChatObject.Call groupCall;
    private TLRPC$InputPeer groupCallPeer;
    private boolean hasAudioFocus;
    public boolean hasFewPeers;
    private boolean isBtHeadsetConnected;
    private boolean isHeadsetPlugged;
    private boolean isOutgoing;
    private boolean isPrivateScreencast;
    private boolean isProximityNear;
    private boolean isVideoAvailable;
    private String joinHash;
    private long keyFingerprint;
    private String lastError;
    private NetworkInfo lastNetInfo;
    private long lastTypingTimeSend;
    private Boolean mHasEarpiece;
    private boolean micMute;
    public boolean micSwitching;
    private TLRPC$TL_dataJSON myParams;
    private boolean needPlayEndSound;
    private boolean needRateCall;
    private boolean needSendDebugLog;
    private boolean needSwitchToBluetoothAfterScoActivates;
    private boolean notificationsDisabled;
    private Runnable onDestroyRunnable;
    private boolean playedConnectedSound;
    private boolean playingSound;
    private Instance.TrafficStats prevTrafficStats;
    public TLRPC$PhoneCall privateCall;
    private PowerManager.WakeLock proximityWakelock;
    private boolean reconnectScreenCapture;
    private MediaPlayer ringtonePlayer;
    private int scheduleDate;
    private Runnable shortPollRunnable;
    private int signalBarCount;
    private SoundPool soundPool;
    private int spAllowTalkId;
    private int spBusyId;
    private int spConnectingId;
    private int spEndId;
    private int spFailedID;
    private int spPlayId;
    private int spRingbackID;
    private int spStartRecordId;
    private int spVoiceChatConnecting;
    private int spVoiceChatEndId;
    private int spVoiceChatStartId;
    private boolean speakerphoneStateToSet;
    private boolean startedRinging;
    private boolean switchingAccount;
    private boolean switchingCamera;
    private boolean switchingStream;
    private Runnable switchingStreamTimeoutRunnable;
    private CallConnection systemCallConnection;
    private Runnable timeoutRunnable;
    private boolean unmutedByHold;
    private Runnable updateNotificationRunnable;
    private TLRPC$User user;
    private Vibrator vibrator;
    public boolean videoCall;
    private boolean wasConnected;
    private boolean wasEstablished;
    private static final boolean USE_CONNECTION_SERVICE = isDeviceCompatibleWithConnectionServiceAPI();
    private static final Object sync = new Object();
    private int currentAccount = -1;
    private int currentState = 0;
    private boolean isFrontFaceCamera = true;
    private int previousAudioOutput = -1;
    private ArrayList<StateListener> stateListeners = new ArrayList<>();
    private int remoteVideoState = 0;
    private int[] mySource = new int[2];
    private NativeInstance[] tgVoip = new NativeInstance[2];
    private long[] captureDevice = new long[2];
    private boolean[] destroyCaptureDevice = {true, true};
    private int[] videoState = {0, 0};
    private int remoteAudioState = 1;
    private int audioRouteToSet = 2;
    public final SharedUIParams sharedUIParams = new SharedUIParams();
    private ArrayList<TLRPC$PhoneCall> pendingUpdates = new ArrayList<>();
    private HashMap<String, Integer> currentStreamRequestTimestamp = new HashMap<>();
    private Runnable afterSoundRunnable = new AnonymousClass1();
    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() { // from class: org.telegram.messenger.voip.VoIPService.2
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            try {
                if (Build.VERSION.SDK_INT < 31) {
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
                }
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(i, bluetoothProfile);
                VoIPService.this.fetchingBluetoothDeviceName = false;
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() { // from class: org.telegram.messenger.voip.VoIPService.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean z = true;
            if ("android.intent.action.HEADSET_PLUG".equals(intent.getAction())) {
                VoIPService.this.isHeadsetPlugged = intent.getIntExtra("state", 0) == 1;
                if (VoIPService.this.isHeadsetPlugged && VoIPService.this.proximityWakelock != null && VoIPService.this.proximityWakelock.isHeld()) {
                    VoIPService.this.proximityWakelock.release();
                }
                if (!VoIPService.this.isHeadsetPlugged) {
                    if (VoIPService.this.previousAudioOutput >= 0) {
                        VoIPService voIPService = VoIPService.this;
                        voIPService.setAudioOutput(voIPService.previousAudioOutput);
                        VoIPService.this.previousAudioOutput = -1;
                    }
                } else {
                    AudioManager audioManager = (AudioManager) VoIPService.this.getSystemService("audio");
                    if (audioManager.isSpeakerphoneOn()) {
                        VoIPService.this.previousAudioOutput = 0;
                    } else if (audioManager.isBluetoothScoOn()) {
                        VoIPService.this.previousAudioOutput = 2;
                    } else {
                        VoIPService.this.previousAudioOutput = 1;
                    }
                    VoIPService.this.setAudioOutput(1);
                }
                VoIPService.this.isProximityNear = false;
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
                if (intExtra == 0 && VoIPService.this.isBtHeadsetConnected && (!VoIPService.this.btAdapter.isEnabled() || VoIPService.this.btAdapter.getProfileConnectionState(1) != 2)) {
                    VoIPService.this.updateBluetoothHeadsetState(false);
                    return;
                }
                VoIPService.this.bluetoothScoConnecting = intExtra == 2;
                VoIPService.this.bluetoothScoActive = intExtra == 1;
                if (VoIPService.this.bluetoothScoActive) {
                    VoIPService.this.fetchBluetoothDeviceName();
                    if (VoIPService.this.needSwitchToBluetoothAfterScoActivates) {
                        VoIPService.this.needSwitchToBluetoothAfterScoActivates = false;
                        AudioManager audioManager2 = (AudioManager) VoIPService.this.getSystemService("audio");
                        audioManager2.setSpeakerphoneOn(false);
                        audioManager2.setBluetoothScoOn(true);
                    }
                }
                Iterator it = VoIPService.this.stateListeners.iterator();
                while (it.hasNext()) {
                    ((StateListener) it.next()).onAudioSettingsChanged();
                }
            } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
                if (!TelephonyManager.EXTRA_STATE_OFFHOOK.equals(intent.getStringExtra("state"))) {
                    return;
                }
                VoIPService.this.hangUp();
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
    private final HashMap<String, TLRPC$TL_groupCallParticipant> waitingFrameParticipant = new HashMap<>();
    private final LruCache<String, ProxyVideoSink> proxyVideoSinkLruCache = new LruCache<String, ProxyVideoSink>(6) { // from class: org.telegram.messenger.voip.VoIPService.4
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.util.LruCache
        public void entryRemoved(boolean z, String str, ProxyVideoSink proxyVideoSink, ProxyVideoSink proxyVideoSink2) {
            super.entryRemoved(z, (boolean) str, proxyVideoSink, proxyVideoSink2);
            VoIPService.this.tgVoip[0].removeIncomingVideoOutput(proxyVideoSink.nativeInstance);
        }
    };
    private ProxyVideoSink[] localSink = new ProxyVideoSink[2];
    private ProxyVideoSink[] remoteSink = new ProxyVideoSink[2];
    private ProxyVideoSink[] currentBackgroundSink = new ProxyVideoSink[2];
    private String[] currentBackgroundEndpointId = new String[2];
    private HashMap<String, ProxyVideoSink> remoteSinks = new HashMap<>();

    /* loaded from: classes.dex */
    public static class SharedUIParams {
        public boolean cameraAlertWasShowed;
        public boolean tapToVideoTooltipWasShowed;
        public boolean wasVideoCall;
    }

    /* loaded from: classes.dex */
    public interface StateListener {

        /* renamed from: org.telegram.messenger.voip.VoIPService$StateListener$-CC  reason: invalid class name */
        /* loaded from: classes.dex */
        public final /* synthetic */ class CC {
            public static void $default$onAudioSettingsChanged(StateListener stateListener) {
            }

            public static void $default$onCameraFirstFrameAvailable(StateListener stateListener) {
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

        void onCameraFirstFrameAvailable();

        void onCameraSwitch(boolean z);

        void onMediaStateUpdated(int i, int i2);

        void onScreenOnChange(boolean z);

        void onSignalBarsCountChanged(int i);

        void onStateChanged(int i);

        void onVideoAvailableChange(boolean z);
    }

    private static boolean isDeviceCompatibleWithConnectionServiceAPI() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$createGroupInstance$37(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onSignalingData$60(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    private void onTgVoipPreStop() {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.messenger.voip.VoIPService$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            final AudioManager audioManager = (AudioManager) VoIPService.this.getSystemService("audio");
            audioManager.abandonAudioFocus(VoIPService.this);
            audioManager.unregisterMediaButtonEventReceiver(new ComponentName(VoIPService.this, VoIPMediaButtonReceiver.class));
            if (!VoIPService.USE_CONNECTION_SERVICE && VoIPService.sharedInstance == null) {
                if (VoIPService.this.isBtHeadsetConnected) {
                    audioManager.stopBluetoothSco();
                    audioManager.setBluetoothScoOn(false);
                    VoIPService.this.bluetoothScoActive = false;
                    VoIPService.this.bluetoothScoConnecting = false;
                }
                audioManager.setSpeakerphoneOn(false);
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.AnonymousClass1.this.lambda$run$0();
                }
            });
            Utilities.globalQueue.postRunnable(VoIPService.setModeRunnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.AnonymousClass1.lambda$run$1(audioManager);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0() {
            VoIPService.this.soundPool.release();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$run$1(AudioManager audioManager) {
            synchronized (VoIPService.sync) {
                if (VoIPService.setModeRunnable == null) {
                    return;
                }
                Runnable unused = VoIPService.setModeRunnable = null;
                try {
                    audioManager.setMode(0);
                } catch (SecurityException e) {
                    if (!BuildVars.LOGS_ENABLED) {
                        return;
                    }
                    FileLog.e("Error setting audio more to normal", e);
                }
            }
        }
    }

    public boolean isFrontFaceCamera() {
        return this.isFrontFaceCamera;
    }

    public boolean isScreencast() {
        return this.isPrivateScreencast;
    }

    public void setMicMute(boolean z, boolean z2, boolean z3) {
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        if (this.micMute == z || this.micSwitching) {
            return;
        }
        this.micMute = z;
        ChatObject.Call call = this.groupCall;
        boolean z4 = true;
        if (call != null) {
            if (!z3 && (tLRPC$TL_groupCallParticipant = call.participants.get(getSelfId())) != null && tLRPC$TL_groupCallParticipant.muted && !tLRPC$TL_groupCallParticipant.can_self_unmute) {
                z3 = true;
            }
            if (z3) {
                editCallMember(UserConfig.getInstance(this.currentAccount).getCurrentUser(), Boolean.valueOf(z), null, null, null, null);
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda11
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.lambda$setMicMute$0();
                    }
                };
                this.updateNotificationRunnable = runnable;
                dispatchQueue.postRunnable(runnable);
            }
        }
        if (this.micMute || !z2) {
            z4 = false;
        }
        this.unmutedByHold = z4;
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] != null) {
            nativeInstanceArr[0].setMuteMicrophone(z);
        }
        Iterator<StateListener> it = this.stateListeners.iterator();
        while (it.hasNext()) {
            it.next().onAudioSettingsChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setMicMute$0() {
        if (this.updateNotificationRunnable == null) {
            return;
        }
        this.updateNotificationRunnable = null;
        TLRPC$Chat tLRPC$Chat = this.chat;
        showNotification(tLRPC$Chat.title, getRoundAvatarBitmap(tLRPC$Chat));
    }

    public boolean mutedByAdmin() {
        ChatObject.Call call = this.groupCall;
        if (call != null) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call.participants.get(getSelfId());
            return tLRPC$TL_groupCallParticipant != null && !tLRPC$TL_groupCallParticipant.can_self_unmute && tLRPC$TL_groupCallParticipant.muted && !ChatObject.canManageCalls(this.chat);
        }
        return false;
    }

    public boolean hasVideoCapturer() {
        return this.captureDevice[0] != 0;
    }

    public void checkVideoFrame(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z) {
        String str = z ? tLRPC$TL_groupCallParticipant.presentationEndpoint : tLRPC$TL_groupCallParticipant.videoEndpoint;
        if (str == null) {
            return;
        }
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
            addRemoteSink(tLRPC$TL_groupCallParticipant, z, new AnonymousClass5(str, z), null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.messenger.voip.VoIPService$5  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass5 implements VideoSink {
        final /* synthetic */ String val$endpointId;
        final /* synthetic */ boolean val$screencast;

        @Override // org.webrtc.VideoSink
        public /* synthetic */ void setParentSink(VideoSink videoSink) {
            VideoSink.CC.$default$setParentSink(this, videoSink);
        }

        AnonymousClass5(String str, boolean z) {
            this.val$endpointId = str;
            this.val$screencast = z;
        }

        @Override // org.webrtc.VideoSink
        public void onFrame(VideoFrame videoFrame) {
            if (videoFrame == null || videoFrame.getBuffer().getHeight() == 0 || videoFrame.getBuffer().getWidth() == 0) {
                return;
            }
            final String str = this.val$endpointId;
            final boolean z = this.val$screencast;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$5$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.AnonymousClass5.this.lambda$onFrame$0(str, this, z);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFrame$0(String str, VideoSink videoSink, boolean z) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) VoIPService.this.waitingFrameParticipant.remove(str);
            ProxyVideoSink proxyVideoSink = (ProxyVideoSink) VoIPService.this.remoteSinks.get(str);
            if (proxyVideoSink != null && proxyVideoSink.target == videoSink) {
                VoIPService.this.proxyVideoSinkLruCache.put(str, proxyVideoSink);
                VoIPService.this.remoteSinks.remove(str);
                proxyVideoSink.setTarget(null);
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
    }

    public void clearRemoteSinks() {
        this.proxyVideoSinkLruCache.evictAll();
    }

    public void setAudioRoute(int i) {
        if (i == 1) {
            setAudioOutput(0);
        } else if (i == 0) {
            setAudioOutput(1);
        } else if (i != 2) {
        } else {
            setAudioOutput(2);
        }
    }

    /* loaded from: classes.dex */
    public static class ProxyVideoSink implements VideoSink {
        private VideoSink background;
        private long nativeInstance;
        private VideoSink target;

        @Override // org.webrtc.VideoSink
        public /* synthetic */ void setParentSink(VideoSink videoSink) {
            VideoSink.CC.$default$setParentSink(this, videoSink);
        }

        @Override // org.webrtc.VideoSink
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
                    videoSink2.setParentSink(null);
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
                videoSink2.setParentSink(null);
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
            if (this.target != null && (videoSink = this.background) != null) {
                this.target = videoSink;
                this.background = null;
            }
        }
    }

    @Override // android.app.Service
    @SuppressLint({"MissingPermission", "InlinedApi"})
    public int onStartCommand(Intent intent, int i, int i2) {
        boolean z;
        boolean z2;
        int i3;
        if (sharedInstance != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Tried to start the VoIP service when it's already started");
            }
            return 2;
        }
        int intExtra = intent.getIntExtra("account", -1);
        this.currentAccount = intExtra;
        if (intExtra == -1) {
            throw new IllegalStateException("No account specified when starting VoIP service");
        }
        this.classGuid = ConnectionsManager.generateClassGuid();
        long longExtra = intent.getLongExtra("user_id", 0L);
        long longExtra2 = intent.getLongExtra("chat_id", 0L);
        this.createGroupCall = intent.getBooleanExtra("createGroupCall", false);
        this.hasFewPeers = intent.getBooleanExtra("hasFewPeers", false);
        this.joinHash = intent.getStringExtra("hash");
        long longExtra3 = intent.getLongExtra("peerChannelId", 0L);
        long longExtra4 = intent.getLongExtra("peerChatId", 0L);
        long longExtra5 = intent.getLongExtra("peerUserId", 0L);
        if (longExtra4 != 0) {
            TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
            this.groupCallPeer = tLRPC$TL_inputPeerChat;
            tLRPC$TL_inputPeerChat.chat_id = longExtra4;
            tLRPC$TL_inputPeerChat.access_hash = intent.getLongExtra("peerAccessHash", 0L);
        } else if (longExtra3 != 0) {
            TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
            this.groupCallPeer = tLRPC$TL_inputPeerChannel;
            tLRPC$TL_inputPeerChannel.channel_id = longExtra3;
            tLRPC$TL_inputPeerChannel.access_hash = intent.getLongExtra("peerAccessHash", 0L);
        } else if (longExtra5 != 0) {
            TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
            this.groupCallPeer = tLRPC$TL_inputPeerUser;
            tLRPC$TL_inputPeerUser.user_id = longExtra5;
            tLRPC$TL_inputPeerUser.access_hash = intent.getLongExtra("peerAccessHash", 0L);
        }
        this.scheduleDate = intent.getIntExtra("scheduleDate", 0);
        this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
        this.videoCall = intent.getBooleanExtra("video_call", false);
        this.isVideoAvailable = intent.getBooleanExtra("can_video_call", false);
        this.notificationsDisabled = intent.getBooleanExtra("notifications_disabled", false);
        if (longExtra != 0) {
            this.user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(longExtra));
        }
        if (longExtra2 != 0) {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(longExtra2));
            this.chat = chat;
            if (ChatObject.isChannel(chat)) {
                MessagesController.getInstance(this.currentAccount).startShortPoll(this.chat, this.classGuid, false);
            }
        }
        loadResources();
        int i4 = 0;
        while (true) {
            ProxyVideoSink[] proxyVideoSinkArr = this.localSink;
            if (i4 < proxyVideoSinkArr.length) {
                proxyVideoSinkArr[i4] = new ProxyVideoSink();
                this.remoteSink[i4] = new ProxyVideoSink();
                i4++;
            } else {
                try {
                    break;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
        this.isHeadsetPlugged = ((AudioManager) getSystemService("audio")).isWiredHeadsetOn();
        if (this.chat != null && !this.createGroupCall && MessagesController.getInstance(this.currentAccount).getGroupCall(this.chat.id, false) == null) {
            FileLog.w("VoIPService: trying to open group call without call " + this.chat.id);
            stopSelf();
            return 2;
        }
        if (this.videoCall) {
            if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.CAMERA") == 0) {
                i3 = 0;
                this.captureDevice[0] = NativeInstance.createVideoCapturer(this.localSink[0], this.isFrontFaceCamera ? 1 : 0);
                if (longExtra2 != 0) {
                    this.videoState[0] = 1;
                } else {
                    this.videoState[0] = 2;
                }
            } else {
                i3 = 0;
                this.videoState[0] = 1;
            }
            if (!this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                setAudioOutput(i3);
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
                    ContactsController contactsController = ContactsController.getInstance(this.currentAccount);
                    TLRPC$User tLRPC$User = this.user;
                    contactsController.createOrUpdateConnectionServiceContact(tLRPC$User.id, tLRPC$User.first_name, tLRPC$User.last_name);
                    ((TelecomManager) getSystemService("telecom")).placeCall(Uri.fromParts("tel", "+99084" + this.user.id, null), bundle);
                } else {
                    Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda34
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPService.this.lambda$onStartCommand$1();
                        }
                    };
                    this.delayedStartOutgoingCall = runnable;
                    AndroidUtilities.runOnUIThread(runnable, 2000L);
                }
                z2 = false;
            } else {
                this.micMute = true;
                z2 = false;
                startGroupCall(0, null, false);
                if (!this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                    setAudioOutput(0);
                }
            }
            if (intent.getBooleanExtra("start_incall_activity", z2)) {
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
            boolean z3 = tLRPC$PhoneCall != null && tLRPC$PhoneCall.video;
            this.videoCall = z3;
            if (z3) {
                this.isVideoAvailable = true;
            }
            if (!z3 || this.isBtHeadsetConnected || this.isHeadsetPlugged) {
                z = false;
            } else {
                z = false;
                setAudioOutput(0);
            }
            callIShouldHavePutIntoIntent = null;
            if (USE_CONNECTION_SERVICE) {
                acknowledgeCall(z);
                showNotification();
            } else {
                acknowledgeCall(true);
            }
        }
        initializeAccountRelatedThings();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$onStartCommand$2();
            }
        });
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartCommand$1() {
        this.delayedStartOutgoingCall = null;
        startOutgoingCall();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartCommand$2() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.voipServiceCreated, new Object[0]);
    }

    public static boolean hasRtmpStream() {
        return (getSharedInstance() == null || getSharedInstance().groupCall == null || !getSharedInstance().groupCall.call.rtmp_stream) ? false : true;
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
        if (nativeInstanceArr[0] == null) {
            return;
        }
        nativeInstanceArr[0].setNoiseSuppressionEnabled(z);
    }

    public void setGroupCallHash(String str) {
        if (!this.currentGroupModeStreaming || TextUtils.isEmpty(str) || str.equals(this.joinHash)) {
            return;
        }
        this.joinHash = str;
        createGroupInstance(0, false);
    }

    public long getCallerId() {
        TLRPC$User tLRPC$User = this.user;
        if (tLRPC$User != null) {
            return tLRPC$User.id;
        }
        return -this.chat.id;
    }

    public void hangUp(int i, Runnable runnable) {
        int i2 = this.currentState;
        declineIncomingCall((i2 == 16 || (i2 == 13 && this.isOutgoing)) ? 3 : 1, runnable);
        if (this.groupCall == null || i == 2) {
            return;
        }
        if (i == 1) {
            TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chat.id);
            if (chatFull != null) {
                chatFull.flags &= -2097153;
                chatFull.call = null;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chat.id), Long.valueOf(this.groupCall.call.id), Boolean.FALSE);
            }
            TLRPC$TL_phone_discardGroupCall tLRPC$TL_phone_discardGroupCall = new TLRPC$TL_phone_discardGroupCall();
            tLRPC$TL_phone_discardGroupCall.call = this.groupCall.getInputGroupCall();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardGroupCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda84
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$hangUp$3(tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        TLRPC$TL_phone_leaveGroupCall tLRPC$TL_phone_leaveGroupCall = new TLRPC$TL_phone_leaveGroupCall();
        tLRPC$TL_phone_leaveGroupCall.call = this.groupCall.getInputGroupCall();
        tLRPC$TL_phone_leaveGroupCall.source = this.mySource[0];
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_leaveGroupCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda83
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$hangUp$4(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hangUp$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hangUp$4(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda61.INSTANCE);
        Utilities.random.nextBytes(new byte[256]);
        TLRPC$TL_messages_getDhConfig tLRPC$TL_messages_getDhConfig = new TLRPC$TL_messages_getDhConfig();
        tLRPC$TL_messages_getDhConfig.random_length = 256;
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        tLRPC$TL_messages_getDhConfig.version = messagesStorage.getLastSecretVersion();
        this.callReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getDhConfig, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda89
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$startOutgoingCall$10(messagesStorage, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$startOutgoingCall$5() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startOutgoingCall$10(MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
            final byte[] bArr = new byte[256];
            for (int i = 0; i < 256; i++) {
                bArr[i] = (byte) (((byte) (Utilities.random.nextDouble() * 256.0d)) ^ tLRPC$messages_DhConfig.random[i]);
            }
            byte[] byteArray = BigInteger.valueOf(messagesStorage.getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, messagesStorage.getSecretPBytes())).toByteArray();
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_requestCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda93
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error2) {
                    VoIPService.this.lambda$startOutgoingCall$9(bArr, tLObject2, tLRPC$TL_error2);
                }
            }, 2);
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error on getDhConfig " + tLRPC$TL_error);
            }
            callFailed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startOutgoingCall$9(final byte[] bArr, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda54
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$startOutgoingCall$8(tLRPC$TL_error, tLObject, bArr);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startOutgoingCall$8(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, byte[] bArr) {
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
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$startOutgoingCall$7();
                }
            };
            this.timeoutRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, MessagesController.getInstance(this.currentAccount).callReceiveTimeout);
        } else if (tLRPC$TL_error.code == 400 && "PARTICIPANT_VERSION_OUTDATED".equals(tLRPC$TL_error.text)) {
            callFailed("ERROR_PEER_OUTDATED");
        } else {
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
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startOutgoingCall$7() {
        this.timeoutRunnable = null;
        TLRPC$TL_phone_discardCall tLRPC$TL_phone_discardCall = new TLRPC$TL_phone_discardCall();
        TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
        tLRPC$TL_phone_discardCall.peer = tLRPC$TL_inputPhoneCall;
        TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
        tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
        tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
        tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonMissed();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda79
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$startOutgoingCall$6(tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startOutgoingCall$6(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (BuildVars.LOGS_ENABLED) {
            if (tLRPC$TL_error != null) {
                FileLog.e("error on phone.discardCall: " + tLRPC$TL_error);
            } else {
                FileLog.d("phone.discardCall " + tLObject);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.callFailed();
            }
        });
    }

    private void acknowledgeCall(final boolean z) {
        if (this.privateCall instanceof TLRPC$TL_phoneCallDiscarded) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("Call " + this.privateCall.id + " was discarded before the service started, stopping");
            }
            stopSelf();
        } else if (Build.VERSION.SDK_INT >= 19 && XiaomiUtilities.isMIUI() && !XiaomiUtilities.isCustomPermissionGranted(10020) && ((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("MIUI: no permission to show when locked but the screen is locked. ¯\\_(ツ)_/¯");
            }
            stopSelf();
        } else {
            TLRPC$TL_phone_receivedCall tLRPC$TL_phone_receivedCall = new TLRPC$TL_phone_receivedCall();
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_receivedCall.peer = tLRPC$TL_inputPhoneCall;
            TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_receivedCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda92
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$acknowledgeCall$12(z, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acknowledgeCall$12(final boolean z, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$acknowledgeCall$11(tLObject, tLRPC$TL_error, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acknowledgeCall$11(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, boolean z) {
        if (sharedInstance == null) {
            return;
        }
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
            ContactsController contactsController = ContactsController.getInstance(this.currentAccount);
            TLRPC$User tLRPC$User = this.user;
            contactsController.createOrUpdateConnectionServiceContact(tLRPC$User.id, tLRPC$User.first_name, tLRPC$User.last_name);
            Bundle bundle = new Bundle();
            bundle.putInt("call_type", 1);
            ((TelecomManager) getSystemService("telecom")).addNewIncomingCall(addAccountToTelecomManager(), bundle);
        }
        if (!z) {
            return;
        }
        startRinging();
    }

    private boolean isRinging() {
        return this.currentState == 15;
    }

    public boolean isJoined() {
        int i = this.currentState;
        return (i == 1 || i == 6) ? false : true;
    }

    public void requestVideoCall(boolean z) {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        int i = 0;
        if (nativeInstanceArr[0] == null) {
            return;
        }
        if (!z) {
            long[] jArr = this.captureDevice;
            if (jArr[0] != 0) {
                nativeInstanceArr[0].setupOutgoingVideoCreated(jArr[0]);
                this.destroyCaptureDevice[0] = false;
                this.isPrivateScreencast = z;
            }
        }
        NativeInstance nativeInstance = nativeInstanceArr[0];
        ProxyVideoSink proxyVideoSink = this.localSink[0];
        if (z) {
            i = 2;
        } else if (this.isFrontFaceCamera) {
            i = 1;
        }
        nativeInstance.setupOutgoingVideo(proxyVideoSink, i);
        this.isPrivateScreencast = z;
    }

    public void switchCamera() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] == null || !nativeInstanceArr[0].hasVideoCapturer() || this.switchingCamera) {
            long[] jArr = this.captureDevice;
            if (jArr[0] == 0 || this.switchingCamera) {
                return;
            }
            NativeInstance.switchCameraCapturer(jArr[0], !this.isFrontFaceCamera);
            return;
        }
        this.switchingCamera = true;
        this.tgVoip[0].switchCamera(!this.isFrontFaceCamera);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1, types: [int] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    public void createCaptureDevice(boolean z) {
        ?? r1 = z != 0 ? 2 : this.isFrontFaceCamera;
        if (this.groupCall == null) {
            if (!this.isPrivateScreencast && z != 0) {
                setVideoState(false, 0);
            }
            this.isPrivateScreencast = z;
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            if (nativeInstanceArr[0] != null) {
                nativeInstanceArr[0].clearVideoCapturer();
            }
        }
        if (z == 1) {
            if (this.groupCall != null) {
                long[] jArr = this.captureDevice;
                if (jArr[z] != 0) {
                    return;
                }
                jArr[z] = NativeInstance.createVideoCapturer(this.localSink[z], r1);
                createGroupInstance(1, false);
                setVideoState(true, 2);
                AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.groupCallScreencastStateChanged, new Object[0]);
                return;
            }
            requestVideoCall(true);
            setVideoState(true, 2);
            if (VoIPFragment.getInstance() == null) {
                return;
            }
            VoIPFragment.getInstance().onScreenCastStart();
            return;
        }
        long[] jArr2 = this.captureDevice;
        if (jArr2[z ? 1 : 0] != 0 || this.tgVoip[z] == null) {
            NativeInstance[] nativeInstanceArr2 = this.tgVoip;
            if (nativeInstanceArr2[z] != null && jArr2[z] != 0) {
                nativeInstanceArr2[z].activateVideoCapturer(jArr2[z]);
            }
            if (this.captureDevice[z] != 0) {
                return;
            }
        }
        this.captureDevice[z] = NativeInstance.createVideoCapturer(this.localSink[z], r1);
    }

    public void setupCaptureDevice(boolean z, boolean z2) {
        boolean z3 = false;
        if (z == 0) {
            long[] jArr = this.captureDevice;
            if (jArr[z ? 1 : 0] == 0) {
                return;
            }
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            if (nativeInstanceArr[z] == null) {
                return;
            }
            nativeInstanceArr[z].setupOutgoingVideoCreated(jArr[z]);
            this.destroyCaptureDevice[z] = false;
            this.videoState[z] = 2;
        }
        if (this.micMute == z2) {
            setMicMute(!z2, false, false);
            this.micSwitching = true;
        }
        if (this.groupCall != null) {
            TLRPC$User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            Boolean valueOf = Boolean.valueOf(!z2);
            if (this.videoState[0] != 2) {
                z3 = true;
            }
            editCallMember(currentUser, valueOf, Boolean.valueOf(z3), null, null, new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$setupCaptureDevice$13();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setupCaptureDevice$13() {
        this.micSwitching = false;
    }

    public void clearCamera() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] != null) {
            nativeInstanceArr[0].clearVideoCapturer();
        }
    }

    public void setVideoState(boolean z, int i) {
        int i2;
        boolean z2 = false;
        char c = this.groupCall != null ? z ? 1 : 0 : (char) 0;
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[c] == null) {
            long[] jArr = this.captureDevice;
            if (jArr[z] != 0) {
                int[] iArr = this.videoState;
                iArr[c] = i;
                NativeInstance.setVideoStateCapturer(jArr[z], iArr[c]);
                return;
            } else if (i != 2 || (i2 = this.currentState) == 17 || i2 == 11) {
                return;
            } else {
                jArr[z] = NativeInstance.createVideoCapturer(this.localSink[c], this.isFrontFaceCamera ? 1 : 0);
                this.videoState[c] = 2;
                return;
            }
        }
        int[] iArr2 = this.videoState;
        iArr2[c] = i;
        nativeInstanceArr[c].setVideoState(iArr2[c]);
        long[] jArr2 = this.captureDevice;
        if (jArr2[z ? 1 : 0] != 0) {
            NativeInstance.setVideoStateCapturer(jArr2[z], this.videoState[c]);
        }
        if (z != 0) {
            return;
        }
        if (this.groupCall != null) {
            TLRPC$User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            if (this.videoState[0] != 2) {
                z2 = true;
            }
            editCallMember(currentUser, null, Boolean.valueOf(z2), null, null, null);
        }
        checkIsNear();
    }

    public void stopScreenCapture() {
        if (this.groupCall == null || this.videoState[1] != 2) {
            return;
        }
        TLRPC$TL_phone_leaveGroupCallPresentation tLRPC$TL_phone_leaveGroupCallPresentation = new TLRPC$TL_phone_leaveGroupCallPresentation();
        tLRPC$TL_phone_leaveGroupCallPresentation.call = this.groupCall.getInputGroupCall();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_leaveGroupCallPresentation, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda80
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$stopScreenCapture$14(tLObject, tLRPC$TL_error);
            }
        });
        NativeInstance nativeInstance = this.tgVoip[1];
        if (nativeInstance != null) {
            Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda4(nativeInstance));
        }
        this.mySource[1] = 0;
        this.tgVoip[1] = null;
        this.destroyCaptureDevice[1] = true;
        this.captureDevice[1] = 0;
        this.videoState[1] = 0;
        AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.groupCallScreencastStateChanged, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$stopScreenCapture$14(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public int getVideoState(boolean z) {
        return this.videoState[z ? 1 : 0];
    }

    public void setSinks(VideoSink videoSink, VideoSink videoSink2) {
        setSinks(videoSink, false, videoSink2);
    }

    public void setSinks(VideoSink videoSink, boolean z, VideoSink videoSink2) {
        this.localSink[z ? 1 : 0].setTarget(videoSink);
        this.remoteSink[z].setTarget(videoSink2);
    }

    public void setLocalSink(VideoSink videoSink, boolean z) {
        if (z) {
            return;
        }
        this.localSink[0].setTarget(videoSink);
    }

    public void setRemoteSink(VideoSink videoSink, boolean z) {
        this.remoteSink[z ? 1 : 0].setTarget(videoSink);
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
        proxyVideoSink.nativeInstance = this.tgVoip[0].addIncomingVideoOutput(1, str, createSsrcGroups(z ? tLRPC$TL_groupCallParticipant.presentation : tLRPC$TL_groupCallParticipant.video), proxyVideoSink);
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

    public void requestFullScreen(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z, boolean z2) {
        String str = z2 ? tLRPC$TL_groupCallParticipant.presentationEndpoint : tLRPC$TL_groupCallParticipant.videoEndpoint;
        if (str == null) {
            return;
        }
        if (z) {
            this.tgVoip[0].setVideoEndpointQuality(str, 2);
        } else {
            this.tgVoip[0].setVideoEndpointQuality(str, 1);
        }
    }

    public void removeRemoteSink(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z) {
        if (z) {
            ProxyVideoSink remove = this.remoteSinks.remove(tLRPC$TL_groupCallParticipant.presentationEndpoint);
            if (remove == null) {
                return;
            }
            this.tgVoip[0].removeIncomingVideoOutput(remove.nativeInstance);
            return;
        }
        ProxyVideoSink remove2 = this.remoteSinks.remove(tLRPC$TL_groupCallParticipant.videoEndpoint);
        if (remove2 == null) {
            return;
        }
        this.tgVoip[0].removeIncomingVideoOutput(remove2.nativeInstance);
    }

    public boolean isFullscreen(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z) {
        if (this.currentBackgroundSink[z ? 1 : 0] != null) {
            if (TextUtils.equals(this.currentBackgroundEndpointId[z], z != 0 ? tLRPC$TL_groupCallParticipant.presentationEndpoint : tLRPC$TL_groupCallParticipant.videoEndpoint)) {
                return true;
            }
        }
        return false;
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
            if (nativeInstanceArr[0] == null || nativeInstanceArr[0].isGroup() || getCallID() != tLRPC$TL_updatePhoneCallSignalingData.phone_call_id) {
                return;
            }
            this.tgVoip[0].onSignalingDataReceive(tLRPC$TL_updatePhoneCallSignalingData.data);
        }
    }

    public long getSelfId() {
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
        if (this.chat == null || (call = this.groupCall) == null || call.call.id != tLRPC$TL_updateGroupCallParticipants.call.id) {
            return;
        }
        long selfId = getSelfId();
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
                if (i5 != iArr[0] && iArr[0] != 0 && i5 != 0) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("source mismatch my = " + this.mySource[0] + " psrc = " + tLRPC$TL_groupCallParticipant.source);
                    }
                    hangUp(2);
                    return;
                }
                if (ChatObject.isChannel(this.chat) && this.currentGroupModeStreaming && tLRPC$TL_groupCallParticipant.can_self_unmute) {
                    this.switchingStream = true;
                    createGroupInstance(0, false);
                }
                if (tLRPC$TL_groupCallParticipant.muted) {
                    setMicMute(true, false, false);
                }
            } else {
                continue;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0052 A[Catch: Exception -> 0x0072, TRY_ENTER, TryCatch #0 {Exception -> 0x0072, blocks: (B:34:0x0052, B:36:0x005c, B:39:0x0061, B:41:0x006e, B:40:0x0065), top: B:46:0x0050 }] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0065 A[Catch: Exception -> 0x0072, TryCatch #0 {Exception -> 0x0072, blocks: (B:34:0x0052, B:36:0x005c, B:39:0x0061, B:41:0x006e, B:40:0x0065), top: B:46:0x0050 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onGroupCallUpdated(org.telegram.tgnet.TLRPC$GroupCall r6) {
        /*
            r5 = this;
            org.telegram.tgnet.TLRPC$Chat r0 = r5.chat
            if (r0 != 0) goto L5
            return
        L5:
            org.telegram.messenger.ChatObject$Call r0 = r5.groupCall
            if (r0 == 0) goto L76
            org.telegram.tgnet.TLRPC$GroupCall r0 = r0.call
            long r1 = r0.id
            long r3 = r6.id
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 == 0) goto L14
            goto L76
        L14:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_groupCallDiscarded
            r0 = 2
            if (r6 == 0) goto L1d
            r5.hangUp(r0)
            return
        L1d:
            org.telegram.tgnet.TLRPC$TL_dataJSON r6 = r5.myParams
            r1 = 0
            if (r6 == 0) goto L36
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch: java.lang.Exception -> L32
            org.telegram.tgnet.TLRPC$TL_dataJSON r2 = r5.myParams     // Catch: java.lang.Exception -> L32
            java.lang.String r2 = r2.data     // Catch: java.lang.Exception -> L32
            r6.<init>(r2)     // Catch: java.lang.Exception -> L32
            java.lang.String r2 = "stream"
            boolean r6 = r6.optBoolean(r2)     // Catch: java.lang.Exception -> L32
            goto L37
        L32:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6)
        L36:
            r6 = 0
        L37:
            int r2 = r5.currentState
            r3 = 1
            if (r2 == r3) goto L40
            boolean r2 = r5.currentGroupModeStreaming
            if (r6 == r2) goto L76
        L40:
            org.telegram.tgnet.TLRPC$TL_dataJSON r2 = r5.myParams
            if (r2 == 0) goto L76
            boolean r4 = r5.playedConnectedSound
            if (r4 == 0) goto L4e
            boolean r4 = r5.currentGroupModeStreaming
            if (r6 == r4) goto L4e
            r5.switchingStream = r3
        L4e:
            r5.currentGroupModeStreaming = r6
            if (r6 == 0) goto L65
            org.telegram.messenger.voip.NativeInstance[] r6 = r5.tgVoip     // Catch: java.lang.Exception -> L72
            r6 = r6[r1]     // Catch: java.lang.Exception -> L72
            org.telegram.messenger.ChatObject$Call r2 = r5.groupCall     // Catch: java.lang.Exception -> L72
            org.telegram.tgnet.TLRPC$GroupCall r2 = r2.call     // Catch: java.lang.Exception -> L72
            if (r2 == 0) goto L61
            boolean r2 = r2.rtmp_stream     // Catch: java.lang.Exception -> L72
            if (r2 == 0) goto L61
            r1 = 1
        L61:
            r6.prepareForStream(r1)     // Catch: java.lang.Exception -> L72
            goto L6e
        L65:
            org.telegram.messenger.voip.NativeInstance[] r6 = r5.tgVoip     // Catch: java.lang.Exception -> L72
            r6 = r6[r1]     // Catch: java.lang.Exception -> L72
            java.lang.String r1 = r2.data     // Catch: java.lang.Exception -> L72
            r6.setJoinResponsePayload(r1)     // Catch: java.lang.Exception -> L72
        L6e:
            r5.dispatchStateChanged(r0)     // Catch: java.lang.Exception -> L72
            goto L76
        L72:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6)
        L76:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.onGroupCallUpdated(org.telegram.tgnet.TLRPC$GroupCall):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:69:0x015e  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x016b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onCallUpdated(org.telegram.tgnet.TLRPC$PhoneCall r7) {
        /*
            Method dump skipped, instructions count: 452
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.onCallUpdated(org.telegram.tgnet.TLRPC$PhoneCall):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCallUpdated$15() {
        this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCallUpdated$16() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.spRingbackID, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCallUpdated$17() {
        this.timeoutRunnable = null;
        declineIncomingCall(3, null);
    }

    private void startRatingActivity() {
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).putExtra("call_id", this.privateCall.id).putExtra("call_access_hash", this.privateCall.access_hash).putExtra("call_video", this.privateCall.video).putExtra("account", this.currentAccount).addFlags(NUM), 0).send();
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("Error starting incall activity", e);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_confirmCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda85
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$processAcceptedCall$19(tLObject, tLRPC$TL_error);
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_confirmCall2, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda85
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$processAcceptedCall$19(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processAcceptedCall$19(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda53
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$processAcceptedCall$18(tLRPC$TL_error, tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processAcceptedCall$18(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error != null) {
            callFailed();
            return;
        }
        this.privateCall = ((TLRPC$TL_phone_phoneCall) tLObject).phone_call;
        initiateActualEncryptedCall();
    }

    private int convertDataSavingMode(int i) {
        return i != 3 ? i : ApplicationLoader.isRoaming() ? 1 : 0;
    }

    public void migrateToChat(TLRPC$Chat tLRPC$Chat) {
        this.chat = tLRPC$Chat;
    }

    public void setGroupCallPeer(TLRPC$InputPeer tLRPC$InputPeer) {
        ChatObject.Call call = this.groupCall;
        if (call == null) {
            return;
        }
        this.groupCallPeer = tLRPC$InputPeer;
        call.setSelfPeer(tLRPC$InputPeer);
        TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.groupCall.chatId);
        if (chatFull != null) {
            TLRPC$Peer tLRPC$Peer = this.groupCall.selfPeer;
            chatFull.groupcall_default_join_as = tLRPC$Peer;
            if (tLRPC$Peer != null) {
                if (chatFull instanceof TLRPC$TL_chatFull) {
                    chatFull.flags |= 32768;
                } else {
                    chatFull.flags |= 67108864;
                }
            } else if (chatFull instanceof TLRPC$TL_chatFull) {
                chatFull.flags &= -32769;
            } else {
                chatFull.flags &= -67108865;
            }
        }
        createGroupInstance(0, true);
        if (this.videoState[1] != 2) {
            return;
        }
        createGroupInstance(1, true);
    }

    private void startGroupCall(final int i, String str, final boolean z) {
        if (sharedInstance != this) {
            return;
        }
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_createGroupCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda81
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$startGroupCall$22(tLObject, tLRPC$TL_error);
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
            AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda63.INSTANCE);
            createGroupInstance(0, false);
        } else if (getSharedInstance() == null || this.groupCall == null) {
        } else {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_joinGroupCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda88
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$startGroupCall$28(i, z, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCall$22(TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            int i = 0;
            while (true) {
                if (i >= tLRPC$Updates.updates.size()) {
                    break;
                }
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateGroupCall) {
                    final TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall = (TLRPC$TL_updateGroupCall) tLRPC$Update;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda56
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPService.this.lambda$startGroupCall$20(tLRPC$TL_updateGroupCall);
                        }
                    });
                    break;
                }
                i++;
            }
            MessagesController.getInstance(this.currentAccount).processUpdates(tLRPC$Updates, false);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda50
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$startGroupCall$21(tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCall$20(TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall) {
        if (sharedInstance == null) {
            return;
        }
        TLRPC$GroupCall tLRPC$GroupCall = this.groupCall.call;
        TLRPC$GroupCall tLRPC$GroupCall2 = tLRPC$TL_updateGroupCall.call;
        tLRPC$GroupCall.access_hash = tLRPC$GroupCall2.access_hash;
        tLRPC$GroupCall.id = tLRPC$GroupCall2.id;
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        ChatObject.Call call = this.groupCall;
        messagesController.putGroupCall(call.chatId, call);
        startGroupCall(0, null, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCall$21(TLRPC$TL_error tLRPC$TL_error) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, tLRPC$TL_error.text);
        hangUp(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$startGroupCall$23() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCall$24(int i) {
        this.mySource[0] = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCall$28(final int i, final boolean z, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda38
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$startGroupCall$24(i);
                }
            });
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            long selfId = getSelfId();
            int size = tLRPC$Updates.updates.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i2);
                if (tLRPC$Update instanceof TLRPC$TL_updateGroupCallParticipants) {
                    TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants = (TLRPC$TL_updateGroupCallParticipants) tLRPC$Update;
                    int size2 = tLRPC$TL_updateGroupCallParticipants.participants.size();
                    int i3 = 0;
                    while (true) {
                        if (i3 < size2) {
                            final TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_updateGroupCallParticipants.participants.get(i3);
                            if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == selfId) {
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda55
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        VoIPService.this.lambda$startGroupCall$25(tLRPC$TL_groupCallParticipant);
                                    }
                                });
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("join source = " + tLRPC$TL_groupCallParticipant.source);
                                }
                            } else {
                                i3++;
                            }
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
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda58
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$startGroupCall$26(z);
                }
            });
            startGroupCheckShortpoll();
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda49
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$startGroupCall$27(tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCall$25(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant) {
        this.mySource[0] = tLRPC$TL_groupCallParticipant.source;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCall$26(boolean z) {
        this.groupCall.loadMembers(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCall$27(TLRPC$TL_error tLRPC$TL_error) {
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

    private void startScreenCapture(final int i, String str) {
        if (getSharedInstance() == null || this.groupCall == null) {
            return;
        }
        this.mySource[1] = 0;
        TLRPC$TL_phone_joinGroupCallPresentation tLRPC$TL_phone_joinGroupCallPresentation = new TLRPC$TL_phone_joinGroupCallPresentation();
        tLRPC$TL_phone_joinGroupCallPresentation.call = this.groupCall.getInputGroupCall();
        TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
        tLRPC$TL_phone_joinGroupCallPresentation.params = tLRPC$TL_dataJSON;
        tLRPC$TL_dataJSON.data = str;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_joinGroupCallPresentation, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda86
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$startScreenCapture$32(i, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startScreenCapture$29(int i) {
        this.mySource[1] = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startScreenCapture$32(final int i, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda40
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$startScreenCapture$29(i);
                }
            });
            final TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda57
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$startScreenCapture$30(tLRPC$Updates);
                }
            });
            MessagesController.getInstance(this.currentAccount).processUpdates(tLRPC$Updates, false);
            startGroupCheckShortpoll();
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda51
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$startScreenCapture$31(tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startScreenCapture$30(TLRPC$Updates tLRPC$Updates) {
        if (this.tgVoip[1] != null) {
            long selfId = getSelfId();
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
                        if (i2 < size2) {
                            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = tLRPC$TL_updateGroupCallParticipants.participants.get(i2);
                            if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == selfId) {
                                TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo = tLRPC$TL_groupCallParticipant.presentation;
                                if (tLRPC$TL_groupCallParticipantVideo != null) {
                                    if ((tLRPC$TL_groupCallParticipantVideo.flags & 2) != 0) {
                                        this.mySource[1] = tLRPC$TL_groupCallParticipantVideo.audio_source;
                                    } else {
                                        int size3 = tLRPC$TL_groupCallParticipantVideo.source_groups.size();
                                        for (int i3 = 0; i3 < size3; i3++) {
                                            TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup = tLRPC$TL_groupCallParticipant.presentation.source_groups.get(i3);
                                            if (tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.size() > 0) {
                                                this.mySource[1] = tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.get(0).intValue();
                                            }
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
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startScreenCapture$31(TLRPC$TL_error tLRPC$TL_error) {
        if ("GROUPCALL_VIDEO_TOO_MUCH".equals(tLRPC$TL_error.text)) {
            this.groupCall.reloadGroupCall();
        } else if ("JOIN_AS_PEER_INVALID".equals(tLRPC$TL_error.text)) {
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
        } else if (!"GROUPCALL_INVALID".equals(tLRPC$TL_error.text)) {
        } else {
            MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat.id, 0, true);
        }
    }

    private void startGroupCheckShortpoll() {
        ChatObject.Call call;
        TLRPC$GroupCall tLRPC$GroupCall;
        if (this.shortPollRunnable != null || sharedInstance == null || (call = this.groupCall) == null) {
            return;
        }
        int[] iArr = this.mySource;
        if (iArr[0] == 0 && iArr[1] == 0 && ((tLRPC$GroupCall = call.call) == null || !tLRPC$GroupCall.rtmp_stream)) {
            return;
        }
        Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$startGroupCheckShortpoll$35();
            }
        };
        this.shortPollRunnable = runnable;
        AndroidUtilities.runOnUIThread(runnable, 4000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCheckShortpoll$35() {
        ChatObject.Call call;
        TLRPC$GroupCall tLRPC$GroupCall;
        if (this.shortPollRunnable == null || sharedInstance == null || (call = this.groupCall) == null) {
            return;
        }
        int[] iArr = this.mySource;
        int i = 0;
        if (iArr[0] == 0 && iArr[1] == 0 && ((tLRPC$GroupCall = call.call) == null || !tLRPC$GroupCall.rtmp_stream)) {
            return;
        }
        final TLRPC$TL_phone_checkGroupCall tLRPC$TL_phone_checkGroupCall = new TLRPC$TL_phone_checkGroupCall();
        tLRPC$TL_phone_checkGroupCall.call = this.groupCall.getInputGroupCall();
        while (true) {
            int[] iArr2 = this.mySource;
            if (i < iArr2.length) {
                if (iArr2[i] != 0) {
                    tLRPC$TL_phone_checkGroupCall.sources.add(Integer.valueOf(iArr2[i]));
                }
                i++;
            } else {
                this.checkRequestId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_checkGroupCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda91
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        VoIPService.this.lambda$startGroupCheckShortpoll$34(tLRPC$TL_phone_checkGroupCall, tLObject, tLRPC$TL_error);
                    }
                });
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCheckShortpoll$34(final TLRPC$TL_phone_checkGroupCall tLRPC$TL_phone_checkGroupCall, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$startGroupCheckShortpoll$33(tLObject, tLRPC$TL_phone_checkGroupCall, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGroupCheckShortpoll$33(TLObject tLObject, TLRPC$TL_phone_checkGroupCall tLRPC$TL_phone_checkGroupCall, TLRPC$TL_error tLRPC$TL_error) {
        boolean z;
        boolean z2;
        TLRPC$GroupCall tLRPC$GroupCall;
        if (this.shortPollRunnable == null || sharedInstance == null || this.groupCall == null) {
            return;
        }
        this.shortPollRunnable = null;
        this.checkRequestId = 0;
        if (tLObject instanceof TLRPC$Vector) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            int[] iArr = this.mySource;
            z2 = iArr[0] != 0 && tLRPC$TL_phone_checkGroupCall.sources.contains(Integer.valueOf(iArr[0])) && !tLRPC$Vector.objects.contains(Integer.valueOf(this.mySource[0]));
            int[] iArr2 = this.mySource;
            z = iArr2[1] != 0 && tLRPC$TL_phone_checkGroupCall.sources.contains(Integer.valueOf(iArr2[1])) && !tLRPC$Vector.objects.contains(Integer.valueOf(this.mySource[1]));
        } else if (tLRPC$TL_error == null || tLRPC$TL_error.code != 400) {
            z = false;
            z2 = false;
        } else {
            int[] iArr3 = this.mySource;
            z = iArr3[1] != 0 && tLRPC$TL_phone_checkGroupCall.sources.contains(Integer.valueOf(iArr3[1]));
            z2 = true;
        }
        if (z2) {
            createGroupInstance(0, false);
        }
        if (z) {
            createGroupInstance(1, false);
        }
        int[] iArr4 = this.mySource;
        if (iArr4[1] == 0 && iArr4[0] == 0 && ((tLRPC$GroupCall = this.groupCall.call) == null || !tLRPC$GroupCall.rtmp_stream)) {
            return;
        }
        startGroupCheckShortpoll();
    }

    private void cancelGroupCheckShortPoll() {
        int[] iArr = this.mySource;
        if (iArr[1] == 0 && iArr[0] == 0) {
            if (this.checkRequestId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkRequestId, false);
                this.checkRequestId = 0;
            }
            Runnable runnable = this.shortPollRunnable;
            if (runnable == null) {
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.shortPollRunnable = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class RequestedParticipant {
        public int audioSsrc;
        public TLRPC$TL_groupCallParticipant participant;

        public RequestedParticipant(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, int i) {
            this.participant = tLRPC$TL_groupCallParticipant;
            this.audioSsrc = i;
        }
    }

    private void broadcastUnknownParticipants(long j, int[] iArr) {
        if (this.groupCall == null || this.tgVoip[0] == null) {
            return;
        }
        long selfId = getSelfId();
        ArrayList arrayList = null;
        int length = iArr.length;
        for (int i = 0; i < length; i++) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.groupCall.participantsBySources.get(iArr[i]);
            if (tLRPC$TL_groupCallParticipant == null && (tLRPC$TL_groupCallParticipant = this.groupCall.participantsByVideoSources.get(iArr[i])) == null) {
                tLRPC$TL_groupCallParticipant = this.groupCall.participantsByPresentationSources.get(iArr[i]);
            }
            if (tLRPC$TL_groupCallParticipant != null && MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) != selfId && tLRPC$TL_groupCallParticipant.source != 0) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(new RequestedParticipant(tLRPC$TL_groupCallParticipant, iArr[i]));
            }
        }
        if (arrayList == null) {
            return;
        }
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
                double participantVolume = ChatObject.getParticipantVolume(tLRPC$TL_groupCallParticipant2);
                Double.isNaN(participantVolume);
                nativeInstance.setVolume(i4, participantVolume / 10000.0d);
            }
        }
    }

    private void createGroupInstance(final int i, boolean z) {
        boolean z2;
        String logFilePath;
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
                logFilePath = VoIPHelper.getLogFilePath("voip_" + i + "_" + this.groupCall.call.id);
            } else {
                logFilePath = VoIPHelper.getLogFilePath(this.groupCall.call.id, false);
            }
            String str = logFilePath;
            this.tgVoip[i] = NativeInstance.makeGroup(str, this.captureDevice[i], i == 1, i == 0 && SharedConfig.noiseSupression, new NativeInstance.PayloadCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda72
                @Override // org.telegram.messenger.voip.NativeInstance.PayloadCallback
                public final void run(int i2, String str2) {
                    VoIPService.this.lambda$createGroupInstance$36(i, i2, str2);
                }
            }, new NativeInstance.AudioLevelsCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda71
                @Override // org.telegram.messenger.voip.NativeInstance.AudioLevelsCallback
                public final void run(int[] iArr, float[] fArr, boolean[] zArr) {
                    VoIPService.this.lambda$createGroupInstance$38(i, iArr, fArr, zArr);
                }
            }, new NativeInstance.VideoSourcesCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda76
                @Override // org.telegram.messenger.voip.NativeInstance.VideoSourcesCallback
                public final void run(long j, int[] iArr) {
                    VoIPService.this.lambda$createGroupInstance$40(i, j, iArr);
                }
            }, new NativeInstance.RequestBroadcastPartCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda74
                @Override // org.telegram.messenger.voip.NativeInstance.RequestBroadcastPartCallback
                public final void run(long j, long j2, int i2, int i3) {
                    VoIPService.this.lambda$createGroupInstance$45(i, j, j2, i2, i3);
                }
            }, new NativeInstance.RequestBroadcastPartCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda73
                @Override // org.telegram.messenger.voip.NativeInstance.RequestBroadcastPartCallback
                public final void run(long j, long j2, int i2, int i3) {
                    VoIPService.this.lambda$createGroupInstance$47(i, j, j2, i2, i3);
                }
            }, new NativeInstance.RequestCurrentTimeCallback() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda75
                @Override // org.telegram.messenger.voip.NativeInstance.RequestCurrentTimeCallback
                public final void run(long j) {
                    VoIPService.this.lambda$createGroupInstance$49(i, j);
                }
            });
            this.tgVoip[i].setOnStateUpdatedListener(new Instance.OnStateUpdatedListener() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda69
                @Override // org.telegram.messenger.voip.Instance.OnStateUpdatedListener
                public final void onStateUpdated(int i2, boolean z3) {
                    VoIPService.this.lambda$createGroupInstance$50(i, i2, z3);
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$36(int i, int i2, String str) {
        if (i == 0) {
            startGroupCall(i2, str, true);
        } else {
            startScreenCapture(i2, str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$38(int i, int[] iArr, float[] fArr, boolean[] zArr) {
        ChatObject.Call call;
        if (sharedInstance == null || (call = this.groupCall) == null || i != 0) {
            return;
        }
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
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_setTyping, VoIPService$$ExternalSyntheticLambda97.INSTANCE);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(fArr[i2]));
            } else {
                f = Math.max(f, fArr[i2]);
                z = true;
            }
        }
        if (!z) {
            return;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcSpeakerAmplitudeEvent, Float.valueOf(f));
        NativeInstance.AudioLevelsCallback audioLevelsCallback2 = audioLevelsCallback;
        if (audioLevelsCallback2 == null) {
            return;
        }
        audioLevelsCallback2.run(iArr, fArr, zArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$40(int i, final long j, final int[] iArr) {
        ChatObject.Call call;
        if (sharedInstance == null || (call = this.groupCall) == null || i != 0) {
            return;
        }
        call.processUnknownVideoParticipants(iArr, new ChatObject.Call.OnParticipantsLoad() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda64
            @Override // org.telegram.messenger.ChatObject.Call.OnParticipantsLoad
            public final void onLoad(ArrayList arrayList) {
                VoIPService.this.lambda$createGroupInstance$39(j, iArr, arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$39(long j, int[] iArr, ArrayList arrayList) {
        if (sharedInstance == null || this.groupCall == null) {
            return;
        }
        broadcastUnknownParticipants(j, iArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.telegram.messenger.AccountInstance, java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v9, types: [java.lang.String, int] */
    public /* synthetic */ void lambda$createGroupInstance$45(final int i, final long j, long j2, final int i2, final int i3) {
        StringBuilder sb;
        if (i != 0) {
            return;
        }
        TLRPC$TL_upload_getFile tLRPC$TL_upload_getFile = new TLRPC$TL_upload_getFile();
        tLRPC$TL_upload_getFile.limit = 131072;
        TLRPC$TL_inputGroupCallStream tLRPC$TL_inputGroupCallStream = new TLRPC$TL_inputGroupCallStream();
        tLRPC$TL_inputGroupCallStream.call = this.groupCall.getInputGroupCall();
        tLRPC$TL_inputGroupCallStream.time_ms = j;
        if (j2 == 500) {
            tLRPC$TL_inputGroupCallStream.scale = 1;
        }
        if (i2 != 0) {
            tLRPC$TL_inputGroupCallStream.flags |= 1;
            tLRPC$TL_inputGroupCallStream.video_channel = i2;
            tLRPC$TL_inputGroupCallStream.video_quality = i3;
        }
        tLRPC$TL_upload_getFile.location = tLRPC$TL_inputGroupCallStream;
        if (i2 == 0) {
            sb = new StringBuilder();
            sb.append("");
            sb.append(j);
        } else {
            sb = new StringBuilder();
            sb.append(i2);
            sb.append("_");
            sb.append(j);
            sb.append("_");
            sb.append(i3);
        }
        sb.toString();
        final ?? accountInstance = AccountInstance.getInstance(this.currentAccount);
        final ?? sendRequest = accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_upload_getFile, new RequestDelegateTimestamp() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda99
            @Override // org.telegram.tgnet.RequestDelegateTimestamp
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, long j3) {
                VoIPService.this.lambda$createGroupInstance$43(accountInstance, i, j, i2, i3, tLObject, tLRPC$TL_error, j3);
            }
        }, 2, 2, this.groupCall.call.stream_dc_id);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$createGroupInstance$44(sendRequest, sendRequest);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$41(String str) {
        this.currentStreamRequestTimestamp.remove(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$43(final String str, final int i, long j, int i2, int i3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, long j2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda45
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$createGroupInstance$41(str);
            }
        });
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[i] == null) {
            return;
        }
        if (tLObject != null) {
            NativeInstance nativeInstance = nativeInstanceArr[i];
            NativeByteBuffer nativeByteBuffer = ((TLRPC$TL_upload_file) tLObject).bytes;
            nativeInstance.onStreamPartAvailable(j, nativeByteBuffer.buffer, nativeByteBuffer.limit(), j2, i2, i3);
        } else if ("GROUPCALL_JOIN_MISSING".equals(tLRPC$TL_error.text)) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda39
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$createGroupInstance$42(i);
                }
            });
        } else {
            this.tgVoip[i].onStreamPartAvailable(j, null, ("TIME_TOO_BIG".equals(tLRPC$TL_error.text) || tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) ? 0 : -1, j2, i2, i3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$42(int i) {
        createGroupInstance(i, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$44(String str, int i) {
        this.currentStreamRequestTimestamp.put(str, Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$47(int i, final long j, long j2, final int i2, final int i3) {
        if (i != 0) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$createGroupInstance$46(i2, j, i3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$46(int i, long j, int i2) {
        String str;
        if (i == 0) {
            str = "" + j;
        } else {
            str = i + "_" + j + "_" + i2;
        }
        Integer num = this.currentStreamRequestTimestamp.get(str);
        if (num != null) {
            AccountInstance.getInstance(this.currentAccount).getConnectionsManager().cancelRequest(num.intValue(), true);
            this.currentStreamRequestTimestamp.remove(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$49(final int i, final long j) {
        TLRPC$GroupCall tLRPC$GroupCall;
        ChatObject.Call call = this.groupCall;
        if (call != null && (tLRPC$GroupCall = call.call) != null && tLRPC$GroupCall.rtmp_stream) {
            TLRPC$TL_phone_getGroupCallStreamChannels tLRPC$TL_phone_getGroupCallStreamChannels = new TLRPC$TL_phone_getGroupCallStreamChannels();
            tLRPC$TL_phone_getGroupCallStreamChannels.call = this.groupCall.getInputGroupCall();
            ChatObject.Call call2 = this.groupCall;
            if (call2 == null || call2.call == null || this.tgVoip[i] == null) {
                NativeInstance[] nativeInstanceArr = this.tgVoip;
                if (nativeInstanceArr[i] == null) {
                    return;
                }
                nativeInstanceArr[i].onRequestTimeComplete(j, 0L);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_getGroupCallStreamChannels, new RequestDelegateTimestamp() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda98
                @Override // org.telegram.tgnet.RequestDelegateTimestamp
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, long j2) {
                    VoIPService.this.lambda$createGroupInstance$48(i, j, tLObject, tLRPC$TL_error, j2);
                }
            }, 2, 2, this.groupCall.call.stream_dc_id);
            return;
        }
        NativeInstance[] nativeInstanceArr2 = this.tgVoip;
        if (nativeInstanceArr2[i] == null) {
            return;
        }
        nativeInstanceArr2[i].onRequestTimeComplete(j, ConnectionsManager.getInstance(this.currentAccount).getCurrentTimeMillis());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createGroupInstance$48(int i, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, long j2) {
        long j3 = 0;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_phone_groupCallStreamChannels tLRPC$TL_phone_groupCallStreamChannels = (TLRPC$TL_phone_groupCallStreamChannels) tLObject;
            if (!tLRPC$TL_phone_groupCallStreamChannels.channels.isEmpty()) {
                j3 = tLRPC$TL_phone_groupCallStreamChannels.channels.get(0).last_timestamp_ms;
            }
            ChatObject.Call call = this.groupCall;
            if (!call.loadedRtmpStreamParticipant) {
                call.createRtmpStreamParticipant(tLRPC$TL_phone_groupCallStreamChannels.channels);
                this.groupCall.loadedRtmpStreamParticipant = true;
            }
        }
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[i] != null) {
            nativeInstanceArr[i].onRequestTimeComplete(j, j3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateConnectionState */
    public void lambda$createGroupInstance$50(final int i, int i2, boolean z) {
        if (i != 0) {
            return;
        }
        dispatchStateChanged((i2 == 1 || this.switchingStream) ? 3 : 5);
        if (this.switchingStream && (i2 == 0 || (i2 == 1 && z))) {
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda36
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$updateConnectionState$51(i);
                }
            };
            this.switchingStreamTimeoutRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 3000L);
        }
        if (i2 == 0) {
            startGroupCheckShortpoll();
            if (!this.playedConnectedSound || this.spPlayId != 0 || this.switchingStream || this.switchingAccount) {
                return;
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$updateConnectionState$52();
                }
            });
            return;
        }
        cancelGroupCheckShortPoll();
        if (!z) {
            this.switchingStream = false;
            this.switchingAccount = false;
        }
        Runnable runnable2 = this.switchingStreamTimeoutRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.switchingStreamTimeoutRunnable = null;
        }
        if (this.playedConnectedSound) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$updateConnectionState$53();
                }
            });
            Runnable runnable3 = this.connectingSoundRunnable;
            if (runnable3 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable3);
                this.connectingSoundRunnable = null;
            }
        } else {
            playConnectedSound();
        }
        if (this.wasConnected) {
            return;
        }
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateConnectionState$51(int i) {
        if (this.switchingStreamTimeoutRunnable == null) {
            return;
        }
        this.switchingStream = false;
        lambda$createGroupInstance$50(i, 0, true);
        this.switchingStreamTimeoutRunnable = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateConnectionState$52() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.spVoiceChatConnecting, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateConnectionState$53() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    public void setParticipantsVolume() {
        if (this.tgVoip[0] != null) {
            int size = this.groupCall.participants.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_groupCallParticipant valueAt = this.groupCall.participants.valueAt(i);
                if (!valueAt.self && valueAt.source != 0 && (valueAt.can_self_unmute || !valueAt.muted)) {
                    if (valueAt.muted_by_you) {
                        setParticipantVolume(valueAt, 0);
                    } else {
                        setParticipantVolume(valueAt, ChatObject.getParticipantVolume(valueAt));
                    }
                }
            }
        }
    }

    public void setParticipantVolume(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, int i) {
        int i2;
        NativeInstance nativeInstance = this.tgVoip[0];
        int i3 = tLRPC$TL_groupCallParticipant.source;
        double d = i;
        Double.isNaN(d);
        double d2 = d / 10000.0d;
        nativeInstance.setVolume(i3, d2);
        TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo = tLRPC$TL_groupCallParticipant.presentation;
        if (tLRPC$TL_groupCallParticipantVideo == null || (i2 = tLRPC$TL_groupCallParticipantVideo.audio_source) == 0) {
            return;
        }
        this.tgVoip[0].setVolume(i2, d2);
    }

    public boolean isSwitchingStream() {
        return this.switchingStream;
    }

    /* JADX WARN: Removed duplicated region for block: B:110:0x0312 A[Catch: Exception -> 0x033d, TryCatch #2 {Exception -> 0x033d, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0036, B:12:0x0041, B:13:0x0068, B:15:0x0071, B:16:0x007b, B:18:0x0081, B:20:0x008e, B:26:0x009f, B:28:0x00a5, B:29:0x00a9, B:39:0x00c8, B:41:0x00df, B:43:0x00e7, B:45:0x00fc, B:51:0x0108, B:56:0x0112, B:58:0x0116, B:60:0x0138, B:64:0x0179, B:74:0x01e2, B:76:0x01eb, B:78:0x01f6, B:80:0x01fe, B:82:0x0211, B:84:0x0217, B:86:0x0236, B:90:0x0257, B:93:0x0264, B:94:0x0271, B:96:0x0275, B:98:0x0279, B:100:0x027f, B:102:0x0287, B:106:0x0295, B:107:0x02a1, B:108:0x02a6, B:110:0x0312, B:111:0x0315, B:113:0x031d, B:114:0x032d, B:59:0x0130, B:11:0x003c, B:22:0x0092), top: B:127:0x0010, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:119:0x0342  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x0186 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0108 A[Catch: Exception -> 0x033d, TryCatch #2 {Exception -> 0x033d, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0036, B:12:0x0041, B:13:0x0068, B:15:0x0071, B:16:0x007b, B:18:0x0081, B:20:0x008e, B:26:0x009f, B:28:0x00a5, B:29:0x00a9, B:39:0x00c8, B:41:0x00df, B:43:0x00e7, B:45:0x00fc, B:51:0x0108, B:56:0x0112, B:58:0x0116, B:60:0x0138, B:64:0x0179, B:74:0x01e2, B:76:0x01eb, B:78:0x01f6, B:80:0x01fe, B:82:0x0211, B:84:0x0217, B:86:0x0236, B:90:0x0257, B:93:0x0264, B:94:0x0271, B:96:0x0275, B:98:0x0279, B:100:0x027f, B:102:0x0287, B:106:0x0295, B:107:0x02a1, B:108:0x02a6, B:110:0x0312, B:111:0x0315, B:113:0x031d, B:114:0x032d, B:59:0x0130, B:11:0x003c, B:22:0x0092), top: B:127:0x0010, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0116 A[Catch: Exception -> 0x033d, TryCatch #2 {Exception -> 0x033d, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0036, B:12:0x0041, B:13:0x0068, B:15:0x0071, B:16:0x007b, B:18:0x0081, B:20:0x008e, B:26:0x009f, B:28:0x00a5, B:29:0x00a9, B:39:0x00c8, B:41:0x00df, B:43:0x00e7, B:45:0x00fc, B:51:0x0108, B:56:0x0112, B:58:0x0116, B:60:0x0138, B:64:0x0179, B:74:0x01e2, B:76:0x01eb, B:78:0x01f6, B:80:0x01fe, B:82:0x0211, B:84:0x0217, B:86:0x0236, B:90:0x0257, B:93:0x0264, B:94:0x0271, B:96:0x0275, B:98:0x0279, B:100:0x027f, B:102:0x0287, B:106:0x0295, B:107:0x02a1, B:108:0x02a6, B:110:0x0312, B:111:0x0315, B:113:0x031d, B:114:0x032d, B:59:0x0130, B:11:0x003c, B:22:0x0092), top: B:127:0x0010, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0130 A[Catch: Exception -> 0x033d, TryCatch #2 {Exception -> 0x033d, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0036, B:12:0x0041, B:13:0x0068, B:15:0x0071, B:16:0x007b, B:18:0x0081, B:20:0x008e, B:26:0x009f, B:28:0x00a5, B:29:0x00a9, B:39:0x00c8, B:41:0x00df, B:43:0x00e7, B:45:0x00fc, B:51:0x0108, B:56:0x0112, B:58:0x0116, B:60:0x0138, B:64:0x0179, B:74:0x01e2, B:76:0x01eb, B:78:0x01f6, B:80:0x01fe, B:82:0x0211, B:84:0x0217, B:86:0x0236, B:90:0x0257, B:93:0x0264, B:94:0x0271, B:96:0x0275, B:98:0x0279, B:100:0x027f, B:102:0x0287, B:106:0x0295, B:107:0x02a1, B:108:0x02a6, B:110:0x0312, B:111:0x0315, B:113:0x031d, B:114:0x032d, B:59:0x0130, B:11:0x003c, B:22:0x0092), top: B:127:0x0010, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0176  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0178  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x01de A[Catch: Exception -> 0x01d4, TRY_LEAVE, TryCatch #4 {Exception -> 0x01d4, blocks: (B:67:0x0186, B:72:0x01de), top: B:131:0x0186 }] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x01e9  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x01f6 A[Catch: Exception -> 0x033d, TryCatch #2 {Exception -> 0x033d, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0036, B:12:0x0041, B:13:0x0068, B:15:0x0071, B:16:0x007b, B:18:0x0081, B:20:0x008e, B:26:0x009f, B:28:0x00a5, B:29:0x00a9, B:39:0x00c8, B:41:0x00df, B:43:0x00e7, B:45:0x00fc, B:51:0x0108, B:56:0x0112, B:58:0x0116, B:60:0x0138, B:64:0x0179, B:74:0x01e2, B:76:0x01eb, B:78:0x01f6, B:80:0x01fe, B:82:0x0211, B:84:0x0217, B:86:0x0236, B:90:0x0257, B:93:0x0264, B:94:0x0271, B:96:0x0275, B:98:0x0279, B:100:0x027f, B:102:0x0287, B:106:0x0295, B:107:0x02a1, B:108:0x02a6, B:110:0x0312, B:111:0x0315, B:113:0x031d, B:114:0x032d, B:59:0x0130, B:11:0x003c, B:22:0x0092), top: B:127:0x0010, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0254  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0256  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0262 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0275 A[Catch: Exception -> 0x033d, TryCatch #2 {Exception -> 0x033d, blocks: (B:5:0x0010, B:7:0x0014, B:8:0x002a, B:10:0x0036, B:12:0x0041, B:13:0x0068, B:15:0x0071, B:16:0x007b, B:18:0x0081, B:20:0x008e, B:26:0x009f, B:28:0x00a5, B:29:0x00a9, B:39:0x00c8, B:41:0x00df, B:43:0x00e7, B:45:0x00fc, B:51:0x0108, B:56:0x0112, B:58:0x0116, B:60:0x0138, B:64:0x0179, B:74:0x01e2, B:76:0x01eb, B:78:0x01f6, B:80:0x01fe, B:82:0x0211, B:84:0x0217, B:86:0x0236, B:90:0x0257, B:93:0x0264, B:94:0x0271, B:96:0x0275, B:98:0x0279, B:100:0x027f, B:102:0x0287, B:106:0x0295, B:107:0x02a1, B:108:0x02a6, B:110:0x0312, B:111:0x0315, B:113:0x031d, B:114:0x032d, B:59:0x0130, B:11:0x003c, B:22:0x0092), top: B:127:0x0010, inners: #1 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void initiateActualEncryptedCall() {
        /*
            Method dump skipped, instructions count: 843
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.initiateActualEncryptedCall():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$54() {
        Toast.makeText(this, "This call uses TCP which will degrade its quality.", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$55(int[] iArr, float[] fArr, boolean[] zArr) {
        if (sharedInstance == null || this.privateCall == null) {
            return;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(fArr[0]));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$57(final int i, final int i2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$initiateActualEncryptedCall$56(i, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initiateActualEncryptedCall$56(int i, int i2) {
        this.remoteAudioState = i;
        this.remoteVideoState = i2;
        checkIsNear();
        for (int i3 = 0; i3 < this.stateListeners.size(); i3++) {
            this.stateListeners.get(i3).onMediaStateUpdated(i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$playConnectedSound$58() {
        this.soundPool.play(this.spVoiceChatStartId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void playConnectedSound() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$playConnectedSound$58();
            }
        });
        this.playedConnectedSound = true;
    }

    private void startConnectingSound() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$startConnectingSound$59();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startConnectingSound$59() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        int play = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        this.spPlayId = play;
        if (play == 0) {
            AnonymousClass7 anonymousClass7 = new AnonymousClass7();
            this.connectingSoundRunnable = anonymousClass7;
            AndroidUtilities.runOnUIThread(anonymousClass7, 100L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.messenger.voip.VoIPService$7  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass7 implements Runnable {
        AnonymousClass7() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (VoIPService.sharedInstance == null) {
                return;
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$7$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.AnonymousClass7.this.lambda$run$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0() {
            if (VoIPService.this.spPlayId == 0) {
                VoIPService voIPService = VoIPService.this;
                voIPService.spPlayId = voIPService.soundPool.play(VoIPService.this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
            }
            if (VoIPService.this.spPlayId != 0) {
                VoIPService.this.connectingSoundRunnable = null;
            } else {
                AndroidUtilities.runOnUIThread(this, 100L);
            }
        }
    }

    public void onSignalingData(byte[] bArr) {
        if (this.privateCall == null) {
            return;
        }
        TLRPC$TL_phone_sendSignalingData tLRPC$TL_phone_sendSignalingData = new TLRPC$TL_phone_sendSignalingData();
        TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
        tLRPC$TL_phone_sendSignalingData.peer = tLRPC$TL_inputPhoneCall;
        TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
        tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
        tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
        tLRPC$TL_phone_sendSignalingData.data = bArr;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_sendSignalingData, VoIPService$$ExternalSyntheticLambda95.INSTANCE);
    }

    public boolean isVideoAvailable() {
        return this.isVideoAvailable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onMediaButtonEvent(KeyEvent keyEvent) {
        if (keyEvent == null) {
            return;
        }
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
        if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
            return (this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 5) != 0;
        } else if (((TelephonyManager) getSystemService("phone")).getPhoneType() != 0) {
            return true;
        } else {
            Boolean bool = this.mHasEarpiece;
            if (bool != null) {
                return bool.booleanValue();
            }
            try {
                Method method = AudioManager.class.getMethod("getDevicesForStream", Integer.TYPE);
                int i = AudioManager.class.getField("DEVICE_OUT_EARPIECE").getInt(null);
                if ((((Integer) method.invoke((AudioManager) getSystemService("audio"), 0)).intValue() & i) == i) {
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
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getStatsNetworkType() {
        NetworkInfo networkInfo = this.lastNetInfo;
        if (networkInfo == null || networkInfo.getType() != 0) {
            return 1;
        }
        return this.lastNetInfo.isRoaming() ? 2 : 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setSwitchingCamera(boolean z, boolean z2) {
        this.switchingCamera = z;
        if (!z) {
            this.isFrontFaceCamera = z2;
            for (int i = 0; i < this.stateListeners.size(); i++) {
                this.stateListeners.get(i).onCameraSwitch(this.isFrontFaceCamera);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onCameraFirstFrameAvailable() {
        for (int i = 0; i < this.stateListeners.size(); i++) {
            this.stateListeners.get(i).onCameraFirstFrameAvailable();
        }
    }

    public void registerStateListener(StateListener stateListener) {
        if (this.stateListeners.contains(stateListener)) {
            return;
        }
        this.stateListeners.add(stateListener);
        int i = this.currentState;
        if (i != 0) {
            stateListener.onStateChanged(i);
        }
        int i2 = this.signalBarCount;
        if (i2 == 0) {
            return;
        }
        stateListener.onSignalBarsCountChanged(i2);
    }

    public void unregisterStateListener(StateListener stateListener) {
        this.stateListeners.remove(stateListener);
    }

    public void editCallMember(TLObject tLObject, Boolean bool, Boolean bool2, Integer num, Boolean bool3, final Runnable runnable) {
        TLRPC$InputPeer tLRPC$InputPeer;
        if (tLObject == null || this.groupCall == null) {
            return;
        }
        TLRPC$TL_phone_editGroupCallParticipant tLRPC$TL_phone_editGroupCallParticipant = new TLRPC$TL_phone_editGroupCallParticipant();
        tLRPC$TL_phone_editGroupCallParticipant.call = this.groupCall.getInputGroupCall();
        if (tLObject instanceof TLRPC$User) {
            TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
            if (UserObject.isUserSelf(tLRPC$User) && (tLRPC$InputPeer = this.groupCallPeer) != null) {
                tLRPC$TL_phone_editGroupCallParticipant.participant = tLRPC$InputPeer;
            } else {
                tLRPC$TL_phone_editGroupCallParticipant.participant = MessagesController.getInputPeer(tLRPC$User);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("edit group call part id = " + tLRPC$TL_phone_editGroupCallParticipant.participant.user_id + " access_hash = " + tLRPC$TL_phone_editGroupCallParticipant.participant.user_id);
                }
            }
        } else if (tLObject instanceof TLRPC$Chat) {
            tLRPC$TL_phone_editGroupCallParticipant.participant = MessagesController.getInputPeer((TLRPC$Chat) tLObject);
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder sb = new StringBuilder();
                sb.append("edit group call part id = ");
                TLRPC$InputPeer tLRPC$InputPeer2 = tLRPC$TL_phone_editGroupCallParticipant.participant;
                long j = tLRPC$InputPeer2.chat_id;
                if (j == 0) {
                    j = tLRPC$InputPeer2.channel_id;
                }
                sb.append(j);
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
        final int i = this.currentAccount;
        AccountInstance.getInstance(i).getConnectionsManager().sendRequest(tLRPC$TL_phone_editGroupCallParticipant, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda87
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$editCallMember$61(i, runnable, tLObject2, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$editCallMember$61(int i, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AccountInstance.getInstance(i).getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        } else if (tLRPC$TL_error != null && "GROUPCALL_VIDEO_TOO_MUCH".equals(tLRPC$TL_error.text)) {
            this.groupCall.reloadGroupCall();
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
        int i;
        String str;
        int i2 = 2;
        if (isBluetoothHeadsetConnected() && hasEarpiece()) {
            BottomSheet.Builder title = new BottomSheet.Builder(context).setTitle(LocaleController.getString("VoipOutputDevices", R.string.VoipOutputDevices), true);
            CharSequence[] charSequenceArr = new CharSequence[3];
            charSequenceArr[0] = LocaleController.getString("VoipAudioRoutingSpeaker", R.string.VoipAudioRoutingSpeaker);
            if (this.isHeadsetPlugged) {
                i = R.string.VoipAudioRoutingHeadset;
                str = "VoipAudioRoutingHeadset";
            } else {
                i = R.string.VoipAudioRoutingEarpiece;
                str = "VoipAudioRoutingEarpiece";
            }
            charSequenceArr[1] = LocaleController.getString(str, i);
            String str2 = this.currentBluetoothDeviceName;
            if (str2 == null) {
                str2 = LocaleController.getString("VoipAudioRoutingBluetooth", R.string.VoipAudioRoutingBluetooth);
            }
            charSequenceArr[2] = str2;
            int[] iArr = new int[3];
            iArr[0] = R.drawable.calls_menu_speaker;
            iArr[1] = this.isHeadsetPlugged ? R.drawable.calls_menu_headset : R.drawable.calls_menu_phone;
            iArr[2] = R.drawable.calls_menu_bluetooth;
            BottomSheet.Builder items = title.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    VoIPService.this.lambda$toggleSpeakerphoneOrShowRouteSheet$62(dialogInterface, i3);
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
            return;
        }
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
        } else if (this.audioConfigured && !z2) {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (hasEarpiece()) {
                audioManager.setSpeakerphoneOn(!audioManager.isSpeakerphoneOn());
            } else {
                audioManager.setBluetoothScoOn(!audioManager.isBluetoothScoOn());
            }
            updateOutputGainControlState();
        } else {
            this.speakerphoneStateToSet = !this.speakerphoneStateToSet;
        }
        Iterator<StateListener> it = this.stateListeners.iterator();
        while (it.hasNext()) {
            it.next().onAudioSettingsChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleSpeakerphoneOrShowRouteSheet$62(DialogInterface dialogInterface, int i) {
        if (getSharedInstance() == null) {
            return;
        }
        setAudioOutput(i);
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
                    this.audioRouteToSet = 1;
                } else if (i == 1) {
                    this.needSwitchToBluetoothAfterScoActivates = false;
                    if (this.bluetoothScoActive || this.bluetoothScoConnecting) {
                        audioManager.stopBluetoothSco();
                        this.bluetoothScoActive = false;
                        this.bluetoothScoConnecting = false;
                    }
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setBluetoothScoOn(false);
                    this.audioRouteToSet = 0;
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
                    this.audioRouteToSet = 2;
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
        } else if (this.audioConfigured && !z) {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            return hasEarpiece() ? audioManager.isSpeakerphoneOn() : audioManager.isBluetoothScoOn();
        } else {
            return this.speakerphoneStateToSet;
        }
    }

    public int getCurrentAudioRoute() {
        if (USE_CONNECTION_SERVICE) {
            CallConnection callConnection = this.systemCallConnection;
            if (callConnection != null && callConnection.getCallAudioState() != null) {
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
        } else if (this.audioConfigured) {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (audioManager.isBluetoothScoOn()) {
                return 2;
            }
            return audioManager.isSpeakerphoneOn() ? 1 : 0;
        } else {
            return this.audioRouteToSet;
        }
    }

    public String getDebugString() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        return nativeInstanceArr[0] != null ? nativeInstanceArr[0].getDebugInfo() : "";
    }

    public long getCallDuration() {
        if (this.callStartTime == 0) {
            return 0L;
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
        Vibrator vibrator = this.vibrator;
        if (vibrator != null) {
            vibrator.cancel();
            this.vibrator = null;
        }
    }

    private void showNotification(String str, Bitmap bitmap) {
        int i;
        String str2;
        int i2;
        String str3;
        Intent action = new Intent(this, LaunchActivity.class).setAction(this.groupCall != null ? "voip_chat" : "voip");
        if (this.groupCall != null) {
            action.putExtra("currentAccount", this.currentAccount);
        }
        Notification.Builder contentIntent = new Notification.Builder(this).setContentText(str).setContentIntent(PendingIntent.getActivity(this, 50, action, 0));
        if (this.groupCall != null) {
            if (ChatObject.isChannelOrGiga(this.chat)) {
                i2 = R.string.VoipLiveStream;
                str3 = "VoipLiveStream";
            } else {
                i2 = R.string.VoipVoiceChat;
                str3 = "VoipVoiceChat";
            }
            contentIntent.setContentTitle(LocaleController.getString(str3, i2));
            contentIntent.setSmallIcon(isMicMute() ? R.drawable.voicechat_muted : R.drawable.voicechat_active);
        } else {
            contentIntent.setContentTitle(LocaleController.getString("VoipOutgoingCall", R.string.VoipOutgoingCall));
            contentIntent.setSmallIcon(R.drawable.notification);
        }
        int i3 = Build.VERSION.SDK_INT;
        if (i3 >= 16) {
            Intent intent = new Intent(this, VoIPActionsReceiver.class);
            intent.setAction(getPackageName() + ".END_CALL");
            if (this.groupCall != null) {
                int i4 = R.drawable.ic_call_end_white_24dp;
                if (ChatObject.isChannelOrGiga(this.chat)) {
                    i = R.string.VoipChannelLeaveAlertTitle;
                    str2 = "VoipChannelLeaveAlertTitle";
                } else {
                    i = R.string.VoipGroupLeaveAlertTitle;
                    str2 = "VoipGroupLeaveAlertTitle";
                }
                contentIntent.addAction(i4, LocaleController.getString(str2, i), PendingIntent.getBroadcast(this, 0, intent, NUM));
            } else {
                contentIntent.addAction(R.drawable.ic_call_end_white_24dp, LocaleController.getString("VoipEndCall", R.string.VoipEndCall), PendingIntent.getBroadcast(this, 0, intent, NUM));
            }
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
        try {
            startForeground(201, contentIntent.getNotification());
        } catch (Exception e) {
            if (bitmap == null || !(e instanceof IllegalArgumentException)) {
                return;
            }
            showNotification(str, null);
        }
    }

    private void startRingtoneAndVibration(long j) {
        int i;
        String string;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (audioManager.getRingerMode() != 0) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.ringtonePlayer = mediaPlayer;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda1
                @Override // android.media.MediaPlayer.OnPreparedListener
                public final void onPrepared(MediaPlayer mediaPlayer2) {
                    VoIPService.this.lambda$startRingtoneAndVibration$63(mediaPlayer2);
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
                if (notificationsSettings.getBoolean("custom_" + j, false)) {
                    string = notificationsSettings.getString("ringtone_path_" + j, RingtoneManager.getDefaultUri(1).toString());
                } else {
                    string = notificationsSettings.getString("CallsRingtonePath", RingtoneManager.getDefaultUri(1).toString());
                }
                this.ringtonePlayer.setDataSource(this, Uri.parse(string));
                this.ringtonePlayer.prepareAsync();
            } catch (Exception e) {
                FileLog.e(e);
                MediaPlayer mediaPlayer2 = this.ringtonePlayer;
                if (mediaPlayer2 != null) {
                    mediaPlayer2.release();
                    this.ringtonePlayer = null;
                }
            }
            if (notificationsSettings.getBoolean("custom_" + j, false)) {
                i = notificationsSettings.getInt("calls_vibrate_" + j, 0);
            } else {
                i = notificationsSettings.getInt("vibrate_calls", 0);
            }
            if ((i == 2 || i == 4 || !(audioManager.getRingerMode() == 1 || audioManager.getRingerMode() == 2)) && !(i == 4 && audioManager.getRingerMode() == 1)) {
                return;
            }
            Vibrator vibrator = (Vibrator) getSystemService("vibrator");
            this.vibrator = vibrator;
            long j2 = 700;
            if (i == 1) {
                j2 = 350;
            } else if (i == 3) {
                j2 = 1400;
            }
            vibrator.vibrate(new long[]{0, j2, 500}, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startRingtoneAndVibration$63(MediaPlayer mediaPlayer) {
        try {
            this.ringtonePlayer.start();
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STOPPING ===============");
        }
        stopForeground(true);
        stopRinging();
        if (this.currentAccount >= 0) {
            if (ApplicationLoader.mainInterfacePaused || !ApplicationLoader.isScreenOn) {
                MessagesController.getInstance(this.currentAccount).ignoreSetOnline = false;
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
        }
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
        AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda59.INSTANCE);
        if (this.tgVoip[0] != null) {
            StatsController.getInstance(this.currentAccount).incrementTotalCallsTime(getStatsNetworkType(), ((int) (getCallDuration() / 1000)) % 5);
            onTgVoipPreStop();
            if (this.tgVoip[0].isGroup()) {
                NativeInstance nativeInstance = this.tgVoip[0];
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                nativeInstance.getClass();
                dispatchQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda4(nativeInstance));
                for (Map.Entry<String, Integer> entry : this.currentStreamRequestTimestamp.entrySet()) {
                    AccountInstance.getInstance(this.currentAccount).getConnectionsManager().cancelRequest(entry.getValue().intValue(), true);
                }
                this.currentStreamRequestTimestamp.clear();
            } else {
                Instance.FinalState stop = this.tgVoip[0].stop();
                updateTrafficStats(this.tgVoip[0], stop.trafficStats);
                onTgVoipStop(stop);
            }
            this.prevTrafficStats = null;
            this.callStartTime = 0L;
            this.tgVoip[0] = null;
            Instance.destroyInstance();
        }
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[1] != null) {
            NativeInstance nativeInstance2 = nativeInstanceArr[1];
            DispatchQueue dispatchQueue2 = Utilities.globalQueue;
            nativeInstance2.getClass();
            dispatchQueue2.postRunnable(new VoIPService$$ExternalSyntheticLambda4(nativeInstance2));
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
            final AudioManager audioManager = (AudioManager) getSystemService("audio");
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
                    Runnable runnable3 = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPService.lambda$onDestroy$65(audioManager);
                        }
                    };
                    setModeRunnable = runnable3;
                    dispatchQueue3.postRunnable(runnable3);
                }
                audioManager.abandonAudioFocus(this);
            }
            audioManager.unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (this.hasAudioFocus) {
                audioManager.abandonAudioFocus(this);
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$onDestroy$66();
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
        VoIPHelper.lastCallTime = SystemClock.elapsedRealtime();
        setSinks(null, null);
        Runnable runnable4 = this.onDestroyRunnable;
        if (runnable4 != null) {
            runnable4.run();
        }
        int i2 = this.currentAccount;
        if (i2 >= 0) {
            ConnectionsManager.getInstance(i2).setAppPaused(true, false);
            if (!ChatObject.isChannel(this.chat)) {
                return;
            }
            MessagesController.getInstance(this.currentAccount).startShortPoll(this.chat, this.classGuid, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onDestroy$64() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onDestroy$65(AudioManager audioManager) {
        synchronized (sync) {
            if (setModeRunnable == null) {
                return;
            }
            setModeRunnable = null;
            try {
                audioManager.setMode(0);
            } catch (SecurityException e) {
                if (!BuildVars.LOGS_ENABLED) {
                    return;
                }
                FileLog.e("Error setting audio more to normal", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDestroy$66() {
        this.soundPool.release();
    }

    public long getCallID() {
        TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
        if (tLRPC$PhoneCall != null) {
            return tLRPC$PhoneCall.id;
        }
        return 0L;
    }

    public void hangUp() {
        hangUp(0, null);
    }

    public void hangUp(int i) {
        hangUp(i, null);
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
        AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda60.INSTANCE);
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        TLRPC$TL_messages_getDhConfig tLRPC$TL_messages_getDhConfig = new TLRPC$TL_messages_getDhConfig();
        tLRPC$TL_messages_getDhConfig.random_length = 256;
        tLRPC$TL_messages_getDhConfig.version = messagesStorage.getLastSecretVersion();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getDhConfig, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda90
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.this.lambda$acceptIncomingCall$70(messagesStorage, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$acceptIncomingCall$67() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acceptIncomingCall$70(MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                bArr[i] = (byte) (((byte) (Utilities.random.nextDouble() * 256.0d)) ^ tLRPC$messages_DhConfig.random[i]);
            }
            if (this.privateCall == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("call is null");
                }
                callFailed();
                return;
            }
            this.a_or_b = bArr;
            BigInteger modPow = BigInteger.valueOf(messagesStorage.getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, messagesStorage.getSecretPBytes()));
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_acceptCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda82
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error2) {
                    VoIPService.this.lambda$acceptIncomingCall$69(tLObject2, tLRPC$TL_error2);
                }
            }, 2);
            return;
        }
        callFailed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acceptIncomingCall$69(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda52
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$acceptIncomingCall$68(tLRPC$TL_error, tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acceptIncomingCall$68(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("accept call ok! " + tLObject);
            }
            TLRPC$PhoneCall tLRPC$PhoneCall = ((TLRPC$TL_phone_phoneCall) tLObject).phone_call;
            this.privateCall = tLRPC$PhoneCall;
            if (!(tLRPC$PhoneCall instanceof TLRPC$TL_phoneCallDiscarded)) {
                return;
            }
            onCallUpdated(tLRPC$PhoneCall);
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
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$declineIncomingCall$71();
                }
            }, 5000L);
        } else if (i2 == 10 || i2 == 11) {
        } else {
            dispatchStateChanged(10);
            if (this.privateCall == null) {
                this.onDestroyRunnable = runnable;
                callEnded();
                if (this.callReqId == 0) {
                    return;
                }
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.callReqId, false);
                this.callReqId = 0;
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
            tLRPC$TL_phone_discardCall.connection_id = nativeInstanceArr[0] != null ? nativeInstanceArr[0].getPreferredRelayId() : 0L;
            if (i == 2) {
                tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonDisconnect();
            } else if (i == 3) {
                tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonMissed();
            } else if (i == 4) {
                tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonBusy();
            } else {
                tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonHangup();
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda78
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPService.this.lambda$declineIncomingCall$72(tLObject, tLRPC$TL_error);
                }
            }, 2);
            this.onDestroyRunnable = runnable;
            callEnded();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$declineIncomingCall$71() {
        if (this.currentState == 10) {
            callEnded();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$declineIncomingCall$72(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("error on phone.discardCall: " + tLRPC$TL_error);
            return;
        }
        if (tLObject instanceof TLRPC$TL_updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
        }
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        FileLog.d("phone.discardCall " + tLObject);
    }

    public void declineIncomingCall() {
        declineIncomingCall(1, null);
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
                Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.lambda$getConnectionAndStartCall$73();
                    }
                };
                this.delayedStartOutgoingCall = runnable;
                AndroidUtilities.runOnUIThread(runnable, 2000L);
            }
            CallConnection callConnection2 = this.systemCallConnection;
            callConnection2.setAddress(Uri.fromParts("tel", "+99084" + this.user.id, null), 1);
            CallConnection callConnection3 = this.systemCallConnection;
            TLRPC$User tLRPC$User = this.user;
            callConnection3.setCallerDisplayName(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), 1);
        }
        return this.systemCallConnection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getConnectionAndStartCall$73() {
        this.delayedStartOutgoingCall = null;
        startOutgoingCall();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startRinging() {
        CallConnection callConnection;
        if (this.currentState == 15) {
            return;
        }
        if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
            callConnection.setRinging();
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("starting ringing for call " + this.privateCall.id);
        }
        dispatchStateChanged(15);
        if (!this.notificationsDisabled && Build.VERSION.SDK_INT >= 21) {
            TLRPC$User tLRPC$User = this.user;
            showIncomingNotification(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), null, this.user, this.privateCall.video, 0);
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.d("Showing incoming call notification");
            return;
        }
        startRingtoneAndVibration(this.user.id);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Starting incall activity for incoming call");
        }
        try {
            PendingIntent.getActivity(this, 12345, new Intent(this, LaunchActivity.class).setAction("voip"), 0).send();
        } catch (Exception e) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("Error starting incall activity", e);
        }
    }

    public void startRingtoneAndVibration() {
        if (!this.startedRinging) {
            startRingtoneAndVibration(this.user.id);
            this.startedRinging = true;
        }
    }

    private void updateServerConfig() {
        final SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        Instance.setGlobalServerConfig(mainSettings.getString("voip_server_config", "{}"));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_phone_getCallConfig
            public static int constructor = NUM;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
                return TLRPC$TL_dataJSON.TLdeserialize(abstractSerializedData, i, z);
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda77
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.lambda$updateServerConfig$74(mainSettings, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateServerConfig$74(SharedPreferences sharedPreferences, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

    public static String convertStreamToString(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                sb.append(readLine);
                sb.append("\n");
            } else {
                bufferedReader.close();
                return sb.toString();
            }
        }
    }

    public static String getStringFromFile(String str) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(new File(str));
        String convertStreamToString = convertStreamToString(fileInputStream);
        fileInputStream.close();
        return convertStreamToString;
    }

    private void onTgVoipStop(Instance.FinalState finalState) {
        if (this.user == null) {
            return;
        }
        if (TextUtils.isEmpty(finalState.debugLog)) {
            try {
                finalState.debugLog = getStringFromFile(VoIPHelper.getLogFilePath(this.privateCall.id, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.needRateCall || this.forceRating || finalState.isRatingSuggested) {
            startRatingActivity();
            this.needRateCall = false;
        }
        if (!this.needSendDebugLog || finalState.debugLog == null) {
            return;
        }
        TLRPC$TL_phone_saveCallDebug tLRPC$TL_phone_saveCallDebug = new TLRPC$TL_phone_saveCallDebug();
        TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
        tLRPC$TL_phone_saveCallDebug.debug = tLRPC$TL_dataJSON;
        tLRPC$TL_dataJSON.data = finalState.debugLog;
        TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
        tLRPC$TL_phone_saveCallDebug.peer = tLRPC$TL_inputPhoneCall;
        TLRPC$PhoneCall tLRPC$PhoneCall = this.privateCall;
        tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
        tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_saveCallDebug, VoIPService$$ExternalSyntheticLambda96.INSTANCE);
        this.needSendDebugLog = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onTgVoipStop$75(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Sent debug logs, response = " + tLObject);
        }
    }

    private void initializeAccountRelatedThings() {
        updateServerConfig();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
    }

    @Override // android.app.Service
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
            if (i >= 17 && audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") != null) {
                Instance.setBufferSize(Integer.parseInt(audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")));
            } else {
                Instance.setBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
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
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error initializing voip controller", e);
            }
            callFailed();
        }
        if (callIShouldHavePutIntoIntent == null || Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationsController.checkOtherNotificationsChannel();
        Notification.Builder showWhen = new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(LocaleController.getString("VoipOutgoingCall", R.string.VoipOutgoingCall)).setShowWhen(false);
        if (this.groupCall != null) {
            showWhen.setSmallIcon(isMicMute() ? R.drawable.voicechat_muted : R.drawable.voicechat_active);
        } else {
            showWhen.setSmallIcon(R.drawable.notification);
        }
        startForeground(201, showWhen.build());
    }

    private void loadResources() {
        if (Build.VERSION.SDK_INT >= 21) {
            WebRtcAudioTrack.setAudioTrackUsageAttribute(2);
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$loadResources$76();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadResources$76() {
        SoundPool soundPool = new SoundPool(1, 0, 0);
        this.soundPool = soundPool;
        this.spConnectingId = soundPool.load(this, R.raw.voip_connecting, 1);
        this.spRingbackID = this.soundPool.load(this, R.raw.voip_ringback, 1);
        this.spFailedID = this.soundPool.load(this, R.raw.voip_failed, 1);
        this.spEndId = this.soundPool.load(this, R.raw.voip_end, 1);
        this.spBusyId = this.soundPool.load(this, R.raw.voip_busy, 1);
        this.spVoiceChatEndId = this.soundPool.load(this, R.raw.voicechat_leave, 1);
        this.spVoiceChatStartId = this.soundPool.load(this, R.raw.voicechat_join, 1);
        this.spVoiceChatConnecting = this.soundPool.load(this, R.raw.voicechat_connecting, 1);
        this.spAllowTalkId = this.soundPool.load(this, R.raw.voip_onallowtalk, 1);
        this.spStartRecordId = this.soundPool.load(this, R.raw.voip_recordstart, 1);
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

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTrafficStats(NativeInstance nativeInstance, Instance.TrafficStats trafficStats) {
        if (trafficStats == null) {
            trafficStats = nativeInstance.getTrafficStats();
        }
        long j = trafficStats.bytesSentWifi;
        Instance.TrafficStats trafficStats2 = this.prevTrafficStats;
        long j2 = j - (trafficStats2 != null ? trafficStats2.bytesSentWifi : 0L);
        long j3 = trafficStats.bytesReceivedWifi - (trafficStats2 != null ? trafficStats2.bytesReceivedWifi : 0L);
        long j4 = trafficStats.bytesSentMobile - (trafficStats2 != null ? trafficStats2.bytesSentMobile : 0L);
        long j5 = trafficStats.bytesReceivedMobile - (trafficStats2 != null ? trafficStats2.bytesReceivedMobile : 0L);
        this.prevTrafficStats = trafficStats;
        if (j2 > 0) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(1, 0, j2);
        }
        if (j3 > 0) {
            StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(1, 0, j3);
        }
        int i = 2;
        if (j4 > 0) {
            StatsController statsController = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo = this.lastNetInfo;
            statsController.incrementSentBytesCount((networkInfo == null || !networkInfo.isRoaming()) ? 0 : 2, 0, j4);
        }
        if (j5 > 0) {
            StatsController statsController2 = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo2 = this.lastNetInfo;
            if (networkInfo2 == null || !networkInfo2.isRoaming()) {
                i = 0;
            }
            statsController2.incrementReceivedBytesCount(i, 0, j5);
        }
    }

    @SuppressLint({"InvalidWakeLockTag"})
    private void configureDeviceForCall() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("configureDeviceForCall, route to set = " + this.audioRouteToSet);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            WebRtcAudioTrack.setAudioTrackUsageAttribute(hasRtmpStream() ? 1 : 2);
            WebRtcAudioTrack.setAudioStreamType(hasRtmpStream() ? Integer.MIN_VALUE : 0);
        }
        this.needPlayEndSound = true;
        final AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (!USE_CONNECTION_SERVICE) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda44
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$configureDeviceForCall$79(audioManager);
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
                if (!BuildVars.LOGS_ENABLED) {
                    return;
                }
                FileLog.e("Error initializing proximity sensor", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$configureDeviceForCall$79(final AudioManager audioManager) {
        try {
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (hasRtmpStream()) {
            audioManager.setMode(0);
            audioManager.setBluetoothScoOn(false);
            AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda62.INSTANCE);
            return;
        }
        audioManager.setMode(3);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$configureDeviceForCall$78(audioManager);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$configureDeviceForCall$77() {
        if (!MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().lambda$startAudioAgain$7(MediaController.getInstance().getPlayingMessageObject());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$configureDeviceForCall$78(AudioManager audioManager) {
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
            if (this.speakerphoneStateToSet) {
                this.audioRouteToSet = 1;
            } else {
                this.audioRouteToSet = 0;
            }
        }
        updateOutputGainControlState();
        this.audioConfigured = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchBluetoothDeviceName() {
        if (this.fetchingBluetoothDeviceName) {
            return;
        }
        try {
            this.currentBluetoothDeviceName = null;
            this.fetchingBluetoothDeviceName = true;
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, this.serviceListener, 1);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    @Override // android.hardware.SensorEventListener
    @SuppressLint({"NewApi"})
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (this.unmutedByHold || this.remoteVideoState == 2) {
            return;
        }
        boolean z = false;
        if (this.videoState[0] == 2 || sensorEvent.sensor.getType() != 8) {
            return;
        }
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (this.audioRouteToSet != 0 || this.isHeadsetPlugged || audioManager.isSpeakerphoneOn()) {
            return;
        }
        if (isBluetoothHeadsetConnected() && audioManager.isBluetoothScoOn()) {
            return;
        }
        if (sensorEvent.values[0] < Math.min(sensorEvent.sensor.getMaximumRange(), 3.0f)) {
            z = true;
        }
        checkIsNear(z);
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
            try {
                if (z) {
                    this.proximityWakelock.acquire();
                } else {
                    this.proximityWakelock.release(1);
                }
            } catch (Exception e) {
                FileLog.e(e);
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

    @Override // android.media.AudioManager.OnAudioFocusChangeListener
    public void onAudioFocusChange(int i) {
        if (i == 1) {
            this.hasAudioFocus = true;
        } else {
            this.hasAudioFocus = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBluetoothHeadsetState(boolean z) {
        if (z == this.isBtHeadsetConnected) {
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("updateBluetoothHeadsetState: " + z);
        }
        this.isBtHeadsetConnected = z;
        final AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (z && !isRinging() && this.currentState != 0) {
            if (this.bluetoothScoActive) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("SCO already active, setting audio routing");
                }
                if (!hasRtmpStream()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setBluetoothScoOn(true);
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("startBluetoothSco");
                }
                if (!hasRtmpStream()) {
                    this.needSwitchToBluetoothAfterScoActivates = true;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPService.lambda$updateBluetoothHeadsetState$80(audioManager);
                        }
                    }, 500L);
                }
            }
        } else {
            this.bluetoothScoActive = false;
            this.bluetoothScoConnecting = false;
        }
        Iterator<StateListener> it = this.stateListeners.iterator();
        while (it.hasNext()) {
            it.next().onAudioSettingsChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateBluetoothHeadsetState$80(AudioManager audioManager) {
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

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkType() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] != null) {
            if (nativeInstanceArr[0].isGroup()) {
                return;
            }
            this.tgVoip[0].setNetworkType(getNetworkType());
            return;
        }
        this.lastNetInfo = getActiveNetworkInfo();
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
                    case 4:
                    case 11:
                    case 14:
                    default:
                        return 11;
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 15:
                        return 4;
                    case 13:
                        return 5;
                }
            } else if (type == 1) {
                return 6;
            } else {
                if (type == 9) {
                    return 7;
                }
            }
        }
        return 0;
    }

    private NetworkInfo getActiveNetworkInfo() {
        return ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callFailed() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        callFailed(nativeInstanceArr[0] != null ? nativeInstanceArr[0].getLastError() : "ERROR_UNKNOWN");
    }

    private Bitmap getRoundAvatarBitmap(TLObject tLObject) {
        AvatarDrawable avatarDrawable;
        Bitmap bitmap = null;
        try {
            if (tLObject instanceof TLRPC$User) {
                TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$User.photo;
                if (tLRPC$UserProfilePhoto != null && tLRPC$UserProfilePhoto.photo_small != null) {
                    BitmapDrawable imageFromMemory = ImageLoader.getInstance().getImageFromMemory(tLRPC$User.photo.photo_small, null, "50_50");
                    if (imageFromMemory != null) {
                        bitmap = imageFromMemory.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                    } else {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        bitmap = BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$User.photo.photo_small, true).toString(), options);
                    }
                }
            } else {
                TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
                TLRPC$ChatPhoto tLRPC$ChatPhoto = tLRPC$Chat.photo;
                if (tLRPC$ChatPhoto != null && tLRPC$ChatPhoto.photo_small != null) {
                    BitmapDrawable imageFromMemory2 = ImageLoader.getInstance().getImageFromMemory(tLRPC$Chat.photo.photo_small, null, "50_50");
                    if (imageFromMemory2 != null) {
                        bitmap = imageFromMemory2.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                    } else {
                        BitmapFactory.Options options2 = new BitmapFactory.Options();
                        options2.inMutable = true;
                        bitmap = BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$Chat.photo.photo_small, true).toString(), options2);
                    }
                }
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
        if (bitmap == null) {
            Theme.createDialogsResources(this);
            if (tLObject instanceof TLRPC$User) {
                avatarDrawable = new AvatarDrawable((TLRPC$User) tLObject);
            } else {
                avatarDrawable = new AvatarDrawable((TLRPC$Chat) tLObject);
            }
            Bitmap createBitmap = Bitmap.createBitmap(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), Bitmap.Config.ARGB_8888);
            avatarDrawable.setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
            avatarDrawable.draw(new Canvas(createBitmap));
            bitmap = createBitmap;
        }
        Canvas canvas = new Canvas(bitmap);
        Path path = new Path();
        path.addCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, Path.Direction.CW);
        path.toggleInverseFillType();
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPath(path, paint);
        return bitmap;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00ff  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x014e  */
    /* JADX WARN: Type inference failed for: r12v1 */
    /* JADX WARN: Type inference failed for: r12v14 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void showIncomingNotification(java.lang.String r19, java.lang.CharSequence r20, org.telegram.tgnet.TLObject r21, boolean r22, int r23) {
        /*
            Method dump skipped, instructions count: 856
            To view this dump add '--comments-level debug' option
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
            tLRPC$TL_phone_discardCall.connection_id = nativeInstanceArr[0] != null ? nativeInstanceArr[0].getPreferredRelayId() : 0L;
            tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonDisconnect();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, VoIPService$$ExternalSyntheticLambda94.INSTANCE);
        }
        try {
            throw new Exception("Call " + getCallID() + " failed with error: " + str);
        } catch (Exception e) {
            FileLog.e(e);
            this.lastError = str;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$callFailed$82();
                }
            });
            if (TextUtils.equals(str, "ERROR_LOCALIZED") && this.soundPool != null) {
                this.playingSound = true;
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda32
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.lambda$callFailed$83();
                    }
                });
                AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1000L);
            }
            if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
                callConnection.setDisconnected(new DisconnectCause(1));
                this.systemCallConnection.destroy();
                this.systemCallConnection = null;
            }
            stopSelf();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$callFailed$81(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("error on phone.discardCall: " + tLRPC$TL_error);
        } else if (!BuildVars.LOGS_ENABLED) {
        } else {
            FileLog.d("phone.discardCall " + tLObject);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$callFailed$82() {
        dispatchStateChanged(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$callFailed$83() {
        this.soundPool.play(this.spFailedID, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void callFailedFromConnectionService() {
        if (this.isOutgoing) {
            callFailed("ERROR_CONNECTION_SERVICE");
        } else {
            hangUp();
        }
    }

    @Override // org.telegram.messenger.voip.VoIPController.ConnectionStateListener
    public void onConnectionStateChanged(final int i, boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$onConnectionStateChanged$86(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConnectionStateChanged$86(int i) {
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
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$onConnectionStateChanged$84();
                }
            });
            if (this.groupCall == null && !this.wasEstablished) {
                this.wasEstablished = true;
                if (!this.isProximityNear && !this.privateCall.video) {
                    Vibrator vibrator = (Vibrator) getSystemService("vibrator");
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(100L);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService.8
                    @Override // java.lang.Runnable
                    public void run() {
                        if (VoIPService.this.tgVoip[0] != null) {
                            StatsController.getInstance(VoIPService.this.currentAccount).incrementTotalCallsTime(VoIPService.this.getStatsNetworkType(), 5);
                            AndroidUtilities.runOnUIThread(this, 5000L);
                        }
                    }
                }, 5000L);
                if (this.isOutgoing) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(getStatsNetworkType(), 0, 1);
                } else {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(getStatsNetworkType(), 0, 1);
                }
            }
        }
        if (i == 5) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$onConnectionStateChanged$85();
                }
            });
        }
        dispatchStateChanged(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConnectionStateChanged$84() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConnectionStateChanged$85() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.groupCall != null ? this.spVoiceChatConnecting : this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$playStartRecordSound$87() {
        this.soundPool.play(this.spStartRecordId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public void playStartRecordSound() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$playStartRecordSound$87();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$playAllowTalkSound$88() {
        this.soundPool.play(this.spAllowTalkId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public void playAllowTalkSound() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$playAllowTalkSound$88();
            }
        });
    }

    @Override // org.telegram.messenger.voip.VoIPController.ConnectionStateListener
    public void onSignalBarCountChanged(final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$onSignalBarCountChanged$89(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSignalBarCountChanged$89(int i) {
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
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$callEnded$90();
            }
        });
        int i = 700;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                VoIPService.this.lambda$callEnded$91();
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
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda9
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.lambda$callEnded$92();
                    }
                });
            } else {
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPService.this.lambda$callEnded$93();
                    }
                }, 100L);
                i = 500;
            }
            AndroidUtilities.runOnUIThread(this.afterSoundRunnable, i);
        }
        Runnable runnable2 = this.timeoutRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.timeoutRunnable = null;
        }
        endConnectionServiceCall(this.needPlayEndSound ? i : 0L);
        stopSelf();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$callEnded$90() {
        dispatchStateChanged(11);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$callEnded$91() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$callEnded$92() {
        this.soundPool.play(this.spEndId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$callEnded$93() {
        this.soundPool.play(this.spVoiceChatEndId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    private void endConnectionServiceCall(long j) {
        if (USE_CONNECTION_SERVICE) {
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPService.this.lambda$endConnectionServiceCall$94();
                }
            };
            if (j > 0) {
                AndroidUtilities.runOnUIThread(runnable, j);
            } else {
                runnable.run();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$endConnectionServiceCall$94() {
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
                } else if (i == 4) {
                    callConnection.setDisconnected(new DisconnectCause(7));
                } else {
                    callConnection.setDisconnected(new DisconnectCause(3));
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
            declineIncomingCall(4, null);
            return;
        }
        if (!(getPackageName() + ".ANSWER_CALL").equals(intent.getAction())) {
            return;
        }
        acceptIncomingCallFromNotification();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acceptIncomingCallFromNotification() {
        showNotification();
        int i = Build.VERSION.SDK_INT;
        if (i >= 23 && i < 30 && (checkSelfPermission("android.permission.RECORD_AUDIO") != 0 || (this.privateCall.video && checkSelfPermission("android.permission.CAMERA") != 0))) {
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, VoIPPermissionActivity.class).addFlags(NUM), NUM).send();
                return;
            } catch (Exception e) {
                if (!BuildVars.LOGS_ENABLED) {
                    return;
                }
                FileLog.e("Error starting permission activity", e);
                return;
            }
        }
        acceptIncomingCall();
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, getUIActivityClass()).setAction("voip"), 0).send();
        } catch (Exception e2) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("Error starting incall activity", e2);
        }
    }

    public void updateOutputGainControlState() {
        if (hasRtmpStream()) {
            return;
        }
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
        this.tgVoip[0].setEchoCancellationStrength(!z ? 1 : 0);
    }

    public int getAccount() {
        return this.currentAccount;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.appDidLogout) {
            callEnded();
        }
    }

    public static boolean isAnyKindOfCallActive() {
        return (getSharedInstance() == null || getSharedInstance().getCallState() == 15) ? false : true;
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
        ((TelecomManager) getSystemService("telecom")).registerPhoneAccount(new PhoneAccount.Builder(phoneAccountHandle, ContactsController.formatName(currentUser.first_name, currentUser.last_name)).setCapabilities(2048).setIcon(Icon.createWithResource(this, R.drawable.ic_launcher_dr)).setHighlightColor(-13851168).addSupportedUriScheme("sip").build());
        return phoneAccountHandle;
    }

    /* loaded from: classes.dex */
    public class CallConnection extends Connection {
        public CallConnection() {
            setConnectionProperties(128);
            setAudioModeIsVoip(true);
        }

        @Override // android.telecom.Connection
        public void onCallAudioStateChanged(CallAudioState callAudioState) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService call audio state changed: " + callAudioState);
            }
            Iterator it = VoIPService.this.stateListeners.iterator();
            while (it.hasNext()) {
                ((StateListener) it.next()).onAudioSettingsChanged();
            }
        }

        @Override // android.telecom.Connection
        public void onDisconnect() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onDisconnect");
            }
            setDisconnected(new DisconnectCause(2));
            destroy();
            VoIPService.this.systemCallConnection = null;
            VoIPService.this.hangUp();
        }

        @Override // android.telecom.Connection
        public void onAnswer() {
            VoIPService.this.acceptIncomingCallFromNotification();
        }

        @Override // android.telecom.Connection
        public void onReject() {
            VoIPService.this.needPlayEndSound = false;
            VoIPService.this.declineIncomingCall(1, null);
        }

        @Override // android.telecom.Connection
        public void onShowIncomingCallUi() {
            VoIPService.this.startRinging();
        }

        @Override // android.telecom.Connection
        public void onStateChanged(int i) {
            super.onStateChanged(i);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onStateChanged " + Connection.stateToString(i));
            }
            if (i == 4) {
                ContactsController.getInstance(VoIPService.this.currentAccount).deleteConnectionServiceContact();
                VoIPService.this.didDeleteConnectionServiceContact = true;
            }
        }

        @Override // android.telecom.Connection
        public void onCallEvent(String str, Bundle bundle) {
            super.onCallEvent(str, bundle);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onCallEvent " + str);
            }
        }

        @Override // android.telecom.Connection
        public void onSilence() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("onSlience");
            }
            VoIPService.this.stopRinging();
        }
    }
}
