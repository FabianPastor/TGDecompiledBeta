package org.telegram.messenger.voip;

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
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPFeedbackActivity;
import org.telegram.ui.VoIPFragment;
import org.telegram.ui.VoIPPermissionActivity;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;
import org.webrtc.voiceengine.WebRtcAudioTrack;

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
    public static TLRPC.PhoneCall callIShouldHavePutIntoIntent;
    /* access modifiers changed from: private */
    public static Runnable setModeRunnable;
    /* access modifiers changed from: private */
    public static VoIPService sharedInstance;
    /* access modifiers changed from: private */
    public static final Object sync = new Object();
    private byte[] a_or_b;
    private Runnable afterSoundRunnable = new Runnable() {
        public void run() {
            AudioManager am = (AudioManager) VoIPService.this.getSystemService("audio");
            am.abandonAudioFocus(VoIPService.this);
            am.unregisterMediaButtonEventReceiver(new ComponentName(VoIPService.this, VoIPMediaButtonReceiver.class));
            if (!VoIPService.USE_CONNECTION_SERVICE && VoIPService.sharedInstance == null) {
                if (VoIPService.this.isBtHeadsetConnected) {
                    am.stopBluetoothSco();
                    am.setBluetoothScoOn(false);
                    boolean unused = VoIPService.this.bluetoothScoActive = false;
                    boolean unused2 = VoIPService.this.bluetoothScoConnecting = false;
                }
                am.setSpeakerphoneOn(false);
            }
            Utilities.globalQueue.postRunnable(new VoIPService$1$$ExternalSyntheticLambda1(this));
            Utilities.globalQueue.postRunnable(VoIPService.setModeRunnable = new VoIPService$1$$ExternalSyntheticLambda0(am));
        }

        /* renamed from: lambda$run$0$org-telegram-messenger-voip-VoIPService$1  reason: not valid java name */
        public /* synthetic */ void m2517lambda$run$0$orgtelegrammessengervoipVoIPService$1() {
            VoIPService.this.soundPool.release();
        }

        static /* synthetic */ void lambda$run$1(AudioManager am) {
            synchronized (VoIPService.sync) {
                if (VoIPService.setModeRunnable != null) {
                    Runnable unused = VoIPService.setModeRunnable = null;
                    try {
                        am.setMode(0);
                    } catch (SecurityException x) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("Error setting audio more to normal", (Throwable) x);
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
    private TLRPC.Chat chat;
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
    private HashMap<String, Integer> currentStreamRequestTimestamp = new HashMap<>();
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
    private TLRPC.InputPeer groupCallPeer;
    private boolean hasAudioFocus;
    public boolean hasFewPeers;
    /* access modifiers changed from: private */
    public boolean isBtHeadsetConnected;
    private boolean isFrontFaceCamera = true;
    /* access modifiers changed from: private */
    public boolean isHeadsetPlugged;
    private boolean isOutgoing;
    private boolean isPrivateScreencast;
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
    private TLRPC.TL_dataJSON myParams;
    private int[] mySource = new int[2];
    /* access modifiers changed from: private */
    public boolean needPlayEndSound;
    private boolean needRateCall;
    private boolean needSendDebugLog;
    /* access modifiers changed from: private */
    public boolean needSwitchToBluetoothAfterScoActivates;
    private boolean notificationsDisabled;
    private Runnable onDestroyRunnable;
    private ArrayList<TLRPC.PhoneCall> pendingUpdates = new ArrayList<>();
    private boolean playedConnectedSound;
    private boolean playingSound;
    private Instance.TrafficStats prevTrafficStats;
    /* access modifiers changed from: private */
    public int previousAudioOutput = -1;
    public TLRPC.PhoneCall privateCall;
    /* access modifiers changed from: private */
    public PowerManager.WakeLock proximityWakelock;
    /* access modifiers changed from: private */
    public final LruCache<String, ProxyVideoSink> proxyVideoSinkLruCache = new LruCache<String, ProxyVideoSink>(6) {
        /* access modifiers changed from: protected */
        public void entryRemoved(boolean evicted, String key, ProxyVideoSink oldValue, ProxyVideoSink newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
            VoIPService.this.tgVoip[0].removeIncomingVideoOutput(oldValue.nativeInstance);
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
                    AudioManager am = (AudioManager) VoIPService.this.getSystemService("audio");
                    if (am.isSpeakerphoneOn()) {
                        int unused2 = VoIPService.this.previousAudioOutput = 0;
                    } else if (am.isBluetoothScoOn()) {
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
                int state = intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Bluetooth SCO state updated: " + state);
                }
                if (state != 0 || !VoIPService.this.isBtHeadsetConnected || (VoIPService.this.btAdapter.isEnabled() && VoIPService.this.btAdapter.getProfileConnectionState(1) == 2)) {
                    boolean unused7 = VoIPService.this.bluetoothScoConnecting = state == 2;
                    boolean unused8 = VoIPService.this.bluetoothScoActive = state == 1;
                    if (VoIPService.this.bluetoothScoActive) {
                        VoIPService.this.fetchBluetoothDeviceName();
                        if (VoIPService.this.needSwitchToBluetoothAfterScoActivates) {
                            boolean unused9 = VoIPService.this.needSwitchToBluetoothAfterScoActivates = false;
                            AudioManager am2 = (AudioManager) VoIPService.this.getSystemService("audio");
                            am2.setSpeakerphoneOn(false);
                            am2.setBluetoothScoOn(true);
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
        public void onServiceDisconnected(int profile) {
        }

        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Iterator<BluetoothDevice> it = proxy.getConnectedDevices().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                BluetoothDevice device = it.next();
                if (proxy.getConnectionState(device) == 2) {
                    VoIPService.this.currentBluetoothDeviceName = device.getName();
                    break;
                }
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
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
    private TLRPC.User user;
    private Vibrator vibrator;
    public boolean videoCall;
    private int[] videoState = {0, 0};
    /* access modifiers changed from: private */
    public final HashMap<String, TLRPC.TL_groupCallParticipant> waitingFrameParticipant = new HashMap<>();
    private boolean wasConnected;
    private boolean wasEstablished;

    public static class SharedUIParams {
        public boolean cameraAlertWasShowed;
        public boolean tapToVideoTooltipWasShowed;
        public boolean wasVideoCall;
    }

    public boolean isFrontFaceCamera() {
        return this.isFrontFaceCamera;
    }

    public boolean isScreencast() {
        return this.isPrivateScreencast;
    }

    public void setMicMute(boolean mute, boolean hold, boolean send) {
        TLRPC.TL_groupCallParticipant self;
        if (this.micMute != mute && !this.micSwitching) {
            this.micMute = mute;
            ChatObject.Call call = this.groupCall;
            if (call != null) {
                if (!send && (self = call.participants.get(getSelfId())) != null && self.muted && !self.can_self_unmute) {
                    send = true;
                }
                if (send) {
                    editCallMember(UserConfig.getInstance(this.currentAccount).getCurrentUser(), Boolean.valueOf(mute), (Boolean) null, (Integer) null, (Boolean) null, (Runnable) null);
                    DispatchQueue dispatchQueue = Utilities.globalQueue;
                    VoIPService$$ExternalSyntheticLambda19 voIPService$$ExternalSyntheticLambda19 = new VoIPService$$ExternalSyntheticLambda19(this);
                    this.updateNotificationRunnable = voIPService$$ExternalSyntheticLambda19;
                    dispatchQueue.postRunnable(voIPService$$ExternalSyntheticLambda19);
                }
            }
            this.unmutedByHold = !this.micMute && hold;
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            if (nativeInstanceArr[0] != null) {
                nativeInstanceArr[0].setMuteMicrophone(mute);
            }
            Iterator<StateListener> it = this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
        }
    }

    /* renamed from: lambda$setMicMute$0$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2488lambda$setMicMute$0$orgtelegrammessengervoipVoIPService() {
        if (this.updateNotificationRunnable != null) {
            this.updateNotificationRunnable = null;
            showNotification(this.chat.title, getRoundAvatarBitmap(this.chat));
        }
    }

    public boolean mutedByAdmin() {
        TLRPC.TL_groupCallParticipant participant;
        ChatObject.Call call = this.groupCall;
        if (call == null || (participant = call.participants.get(getSelfId())) == null || participant.can_self_unmute || !participant.muted || ChatObject.canManageCalls(this.chat)) {
            return false;
        }
        return true;
    }

    public boolean hasVideoCapturer() {
        return this.captureDevice[0] != 0;
    }

    public void checkVideoFrame(TLRPC.TL_groupCallParticipant participant, final boolean screencast) {
        final String endpointId = screencast ? participant.presentationEndpoint : participant.videoEndpoint;
        if (endpointId != null) {
            if (screencast && participant.hasPresentationFrame != 0) {
                return;
            }
            if (!screencast && participant.hasCameraFrame != 0) {
                return;
            }
            if (this.proxyVideoSinkLruCache.get(endpointId) != null || (this.remoteSinks.get(endpointId) != null && this.waitingFrameParticipant.get(endpointId) == null)) {
                if (screencast) {
                    participant.hasPresentationFrame = 2;
                } else {
                    participant.hasCameraFrame = 2;
                }
            } else if (this.waitingFrameParticipant.containsKey(endpointId)) {
                this.waitingFrameParticipant.put(endpointId, participant);
                if (screencast) {
                    participant.hasPresentationFrame = 1;
                } else {
                    participant.hasCameraFrame = 1;
                }
            } else {
                if (screencast) {
                    participant.hasPresentationFrame = 1;
                } else {
                    participant.hasCameraFrame = 1;
                }
                this.waitingFrameParticipant.put(endpointId, participant);
                addRemoteSink(participant, screencast, new VideoSink() {
                    public /* synthetic */ void setParentSink(VideoSink videoSink) {
                        VideoSink.CC.$default$setParentSink(this, videoSink);
                    }

                    public void onFrame(VideoFrame frame) {
                        if (frame != null && frame.getBuffer().getHeight() != 0 && frame.getBuffer().getWidth() != 0) {
                            AndroidUtilities.runOnUIThread(new VoIPService$5$$ExternalSyntheticLambda0(this, endpointId, this, screencast));
                        }
                    }

                    /* renamed from: lambda$onFrame$0$org-telegram-messenger-voip-VoIPService$5  reason: not valid java name */
                    public /* synthetic */ void m2518lambda$onFrame$0$orgtelegrammessengervoipVoIPService$5(String endpointId, VideoSink thisSink, boolean screencast) {
                        TLRPC.TL_groupCallParticipant currentParticipant = (TLRPC.TL_groupCallParticipant) VoIPService.this.waitingFrameParticipant.remove(endpointId);
                        ProxyVideoSink proxyVideoSink = (ProxyVideoSink) VoIPService.this.remoteSinks.get(endpointId);
                        if (proxyVideoSink != null && proxyVideoSink.target == thisSink) {
                            VoIPService.this.proxyVideoSinkLruCache.put(endpointId, proxyVideoSink);
                            VoIPService.this.remoteSinks.remove(endpointId);
                            proxyVideoSink.setTarget((VideoSink) null);
                        }
                        if (currentParticipant != null) {
                            if (screencast) {
                                currentParticipant.hasPresentationFrame = 2;
                            } else {
                                currentParticipant.hasCameraFrame = 2;
                            }
                        }
                        if (VoIPService.this.groupCall != null) {
                            VoIPService.this.groupCall.updateVisibleParticipants();
                        }
                    }
                }, (VideoSink) null);
            }
        }
    }

    public void clearRemoteSinks() {
        this.proxyVideoSinkLruCache.evictAll();
    }

    public void setAudioRoute(int route) {
        if (route == 1) {
            setAudioOutput(0);
        } else if (route == 0) {
            setAudioOutput(1);
        } else if (route == 2) {
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

        public synchronized void onFrame(VideoFrame frame) {
            VideoSink videoSink = this.target;
            if (videoSink != null) {
                videoSink.onFrame(frame);
            }
            VideoSink videoSink2 = this.background;
            if (videoSink2 != null) {
                videoSink2.onFrame(frame);
            }
        }

        public synchronized void setTarget(VideoSink newTarget) {
            VideoSink videoSink = this.target;
            if (videoSink != newTarget) {
                if (videoSink != null) {
                    videoSink.setParentSink((VideoSink) null);
                }
                this.target = newTarget;
                if (newTarget != null) {
                    newTarget.setParentSink(this);
                }
            }
        }

        public synchronized void setBackground(VideoSink newBackground) {
            VideoSink videoSink = this.background;
            if (videoSink != null) {
                videoSink.setParentSink((VideoSink) null);
            }
            this.background = newBackground;
            if (newBackground != null) {
                newBackground.setParentSink(this);
            }
        }

        public synchronized void removeTarget(VideoSink target2) {
            if (this.target == target2) {
                this.target = null;
            }
        }

        public synchronized void removeBackground(VideoSink background2) {
            if (this.background == background2) {
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

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean z;
        int i;
        Intent intent2 = intent;
        if (sharedInstance != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Tried to start the VoIP service when it's already started");
            }
            return 2;
        }
        int intExtra = intent2.getIntExtra("account", -1);
        this.currentAccount = intExtra;
        if (intExtra != -1) {
            this.classGuid = ConnectionsManager.generateClassGuid();
            long userID = intent2.getLongExtra("user_id", 0);
            long chatID = intent2.getLongExtra("chat_id", 0);
            this.createGroupCall = intent2.getBooleanExtra("createGroupCall", false);
            this.hasFewPeers = intent2.getBooleanExtra("hasFewPeers", false);
            this.joinHash = intent2.getStringExtra("hash");
            long peerChannelId = intent2.getLongExtra("peerChannelId", 0);
            long peerChatId = intent2.getLongExtra("peerChatId", 0);
            long peerChannelId2 = peerChannelId;
            long peerUserId = intent2.getLongExtra("peerUserId", 0);
            if (peerChatId != 0) {
                TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
                this.groupCallPeer = tL_inputPeerChat;
                tL_inputPeerChat.chat_id = peerChatId;
                long j = peerChatId;
                this.groupCallPeer.access_hash = intent2.getLongExtra("peerAccessHash", 0);
            } else {
                if (peerChannelId2 != 0) {
                    TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
                    this.groupCallPeer = tL_inputPeerChannel;
                    tL_inputPeerChannel.channel_id = peerChannelId2;
                    this.groupCallPeer.access_hash = intent2.getLongExtra("peerAccessHash", 0);
                } else if (peerUserId != 0) {
                    TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
                    this.groupCallPeer = tL_inputPeerUser;
                    tL_inputPeerUser.user_id = peerUserId;
                    this.groupCallPeer.access_hash = intent2.getLongExtra("peerAccessHash", 0);
                }
            }
            this.scheduleDate = intent2.getIntExtra("scheduleDate", 0);
            this.isOutgoing = intent2.getBooleanExtra("is_outgoing", false);
            this.videoCall = intent2.getBooleanExtra("video_call", false);
            this.isVideoAvailable = intent2.getBooleanExtra("can_video_call", false);
            this.notificationsDisabled = intent2.getBooleanExtra("notifications_disabled", false);
            if (userID != 0) {
                this.user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(userID));
            }
            if (chatID != 0) {
                TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chatID));
                this.chat = chat2;
                if (ChatObject.isChannel(chat2)) {
                    MessagesController.getInstance(this.currentAccount).startShortPoll(this.chat, this.classGuid, false);
                }
            }
            loadResources();
            int a = 0;
            while (true) {
                ProxyVideoSink[] proxyVideoSinkArr = this.localSink;
                if (a >= proxyVideoSinkArr.length) {
                    break;
                }
                proxyVideoSinkArr[a] = new ProxyVideoSink();
                this.remoteSink[a] = new ProxyVideoSink();
                a++;
            }
            try {
                this.isHeadsetPlugged = ((AudioManager) getSystemService("audio")).isWiredHeadsetOn();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (this.chat == null || this.createGroupCall || MessagesController.getInstance(this.currentAccount).getGroupCall(this.chat.id, false) != null) {
                if (this.videoCall) {
                    if (Build.VERSION.SDK_INT < 23) {
                        i = 0;
                    } else if (checkSelfPermission("android.permission.CAMERA") == 0) {
                        i = 0;
                    } else {
                        i = 0;
                        this.videoState[0] = 1;
                        if (!this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                            setAudioOutput(i);
                        }
                    }
                    this.captureDevice[i] = NativeInstance.createVideoCapturer(this.localSink[i], this.isFrontFaceCamera ? 1 : 0);
                    if (chatID != 0) {
                        this.videoState[i] = 1;
                    } else {
                        this.videoState[i] = 2;
                    }
                    setAudioOutput(i);
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
                            Bundle extras = new Bundle();
                            Bundle myExtras = new Bundle();
                            extras.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", addAccountToTelecomManager());
                            myExtras.putInt("call_type", 1);
                            extras.putBundle("android.telecom.extra.OUTGOING_CALL_EXTRAS", myExtras);
                            ContactsController.getInstance(this.currentAccount).createOrUpdateConnectionServiceContact(this.user.id, this.user.first_name, this.user.last_name);
                            ((TelecomManager) getSystemService("telecom")).placeCall(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), extras);
                            z = false;
                        } else {
                            VoIPService$$ExternalSyntheticLambda14 voIPService$$ExternalSyntheticLambda14 = new VoIPService$$ExternalSyntheticLambda14(this);
                            this.delayedStartOutgoingCall = voIPService$$ExternalSyntheticLambda14;
                            AndroidUtilities.runOnUIThread(voIPService$$ExternalSyntheticLambda14, 2000);
                            z = false;
                        }
                    } else {
                        this.micMute = true;
                        z = false;
                        startGroupCall(0, (String) null, false);
                        if (!this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
                            setAudioOutput(0);
                        }
                    }
                    if (intent2.getBooleanExtra("start_incall_activity", z)) {
                        Intent intent1 = new Intent(this, LaunchActivity.class).setAction(this.user != null ? "voip" : "voip_chat").addFlags(NUM);
                        if (this.chat != null) {
                            intent1.putExtra("currentAccount", this.currentAccount);
                        }
                        startActivity(intent1);
                    }
                } else {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeInCallActivity, new Object[0]);
                    TLRPC.PhoneCall phoneCall = callIShouldHavePutIntoIntent;
                    this.privateCall = phoneCall;
                    boolean z2 = phoneCall != null && phoneCall.video;
                    this.videoCall = z2;
                    if (z2) {
                        this.isVideoAvailable = true;
                    }
                    if (z2 && !this.isBtHeadsetConnected && !this.isHeadsetPlugged) {
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
                AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda15(this));
                return 2;
            }
            FileLog.w("VoIPService: trying to open group call without call " + this.chat.id);
            stopSelf();
            return 2;
        }
        throw new IllegalStateException("No account specified when starting VoIP service");
    }

    /* renamed from: lambda$onStartCommand$1$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2481lambda$onStartCommand$1$orgtelegrammessengervoipVoIPService() {
        this.delayedStartOutgoingCall = null;
        startOutgoingCall();
    }

    /* renamed from: lambda$onStartCommand$2$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2482lambda$onStartCommand$2$orgtelegrammessengervoipVoIPService() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.voipServiceCreated, new Object[0]);
    }

    public static boolean hasRtmpStream() {
        return (getSharedInstance() == null || getSharedInstance().groupCall == null || !getSharedInstance().groupCall.call.rtmp_stream) ? false : true;
    }

    public static VoIPService getSharedInstance() {
        return sharedInstance;
    }

    public TLRPC.User getUser() {
        return this.user;
    }

    public TLRPC.Chat getChat() {
        return this.chat;
    }

    public void setNoiseSupressionEnabled(boolean enabled) {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] != null) {
            nativeInstanceArr[0].setNoiseSuppressionEnabled(enabled);
        }
    }

    public void setGroupCallHash(String hash) {
        if (this.currentGroupModeStreaming && !TextUtils.isEmpty(hash) && !hash.equals(this.joinHash)) {
            this.joinHash = hash;
            createGroupInstance(0, false);
        }
    }

    public long getCallerId() {
        TLRPC.User user2 = this.user;
        if (user2 != null) {
            return user2.id;
        }
        return -this.chat.id;
    }

    public void hangUp(int discard, Runnable onDone) {
        int i = this.currentState;
        declineIncomingCall((i == 16 || (i == 13 && this.isOutgoing)) ? 3 : 1, onDone);
        if (this.groupCall != null && discard != 2) {
            if (discard == 1) {
                TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chat.id);
                if (chatFull != null) {
                    chatFull.flags &= -2097153;
                    chatFull.call = null;
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chat.id), Long.valueOf(this.groupCall.call.id), false);
                }
                TLRPC.TL_phone_discardGroupCall req = new TLRPC.TL_phone_discardGroupCall();
                req.call = this.groupCall.getInputGroupCall();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda78(this));
                return;
            }
            TLRPC.TL_phone_leaveGroupCall req2 = new TLRPC.TL_phone_leaveGroupCall();
            req2.call = this.groupCall.getInputGroupCall();
            req2.source = this.mySource[0];
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new VoIPService$$ExternalSyntheticLambda79(this));
        }
    }

    /* renamed from: lambda$hangUp$3$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2466lambda$hangUp$3$orgtelegrammessengervoipVoIPService(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.TL_updates) response, false);
        }
    }

    /* renamed from: lambda$hangUp$4$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2467lambda$hangUp$4$orgtelegrammessengervoipVoIPService(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.TL_updates) response, false);
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
        AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda59.INSTANCE);
        Utilities.random.nextBytes(new byte[256]);
        TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
        req.random_length = 256;
        MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        req.version = messagesStorage.getLastSecretVersion();
        this.callReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda89(this, messagesStorage), 2);
    }

    /* renamed from: lambda$startOutgoingCall$10$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2502x6e6635f5(MessagesStorage messagesStorage, TLObject response, TLRPC.TL_error error) {
        this.callReqId = 0;
        if (this.endCallAfterRequest) {
            callEnded();
        } else if (error == null) {
            TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) response;
            if (response instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(res.p, res.g)) {
                    callFailed();
                    return;
                }
                messagesStorage.setSecretPBytes(res.p);
                messagesStorage.setSecretG(res.g);
                messagesStorage.setLastSecretVersion(res.version);
                messagesStorage.saveSecretParams(messagesStorage.getLastSecretVersion(), messagesStorage.getSecretG(), messagesStorage.getSecretPBytes());
            }
            byte[] salt1 = new byte[256];
            for (int a = 0; a < 256; a++) {
                salt1[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
            }
            byte[] g_a2 = BigInteger.valueOf((long) messagesStorage.getSecretG()).modPow(new BigInteger(1, salt1), new BigInteger(1, messagesStorage.getSecretPBytes())).toByteArray();
            if (g_a2.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_a2, 1, correctedAuth, 0, 256);
                g_a2 = correctedAuth;
            }
            TLRPC.TL_phone_requestCall reqCall = new TLRPC.TL_phone_requestCall();
            reqCall.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user);
            reqCall.protocol = new TLRPC.TL_phoneCallProtocol();
            reqCall.video = this.videoCall;
            reqCall.protocol.udp_p2p = true;
            reqCall.protocol.udp_reflector = true;
            reqCall.protocol.min_layer = 65;
            reqCall.protocol.max_layer = Instance.getConnectionMaxLayer();
            reqCall.protocol.library_versions.addAll(Instance.AVAILABLE_VERSIONS);
            this.g_a = g_a2;
            reqCall.g_a_hash = Utilities.computeSHA256(g_a2, 0, (long) g_a2.length);
            reqCall.random_id = Utilities.random.nextInt();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(reqCall, new VoIPService$$ExternalSyntheticLambda92(this, salt1), 2);
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error on getDhConfig " + error);
            }
            callFailed();
        }
    }

    /* renamed from: lambda$startOutgoingCall$9$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2506xad14a785(byte[] salt1, TLObject response12, TLRPC.TL_error error12) {
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda49(this, error12, response12, salt1));
    }

    /* renamed from: lambda$startOutgoingCall$8$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2505x20747CLASSNAME(TLRPC.TL_error error12, TLObject response12, byte[] salt1) {
        if (error12 == null) {
            this.privateCall = ((TLRPC.TL_phone_phoneCall) response12).phone_call;
            this.a_or_b = salt1;
            dispatchStateChanged(13);
            if (this.endCallAfterRequest) {
                hangUp();
                return;
            }
            if (this.pendingUpdates.size() > 0 && this.privateCall != null) {
                Iterator<TLRPC.PhoneCall> it = this.pendingUpdates.iterator();
                while (it.hasNext()) {
                    onCallUpdated(it.next());
                }
                this.pendingUpdates.clear();
            }
            VoIPService$$ExternalSyntheticLambda24 voIPService$$ExternalSyntheticLambda24 = new VoIPService$$ExternalSyntheticLambda24(this);
            this.timeoutRunnable = voIPService$$ExternalSyntheticLambda24;
            AndroidUtilities.runOnUIThread(voIPService$$ExternalSyntheticLambda24, (long) MessagesController.getInstance(this.currentAccount).callReceiveTimeout);
        } else if (error12.code == 400 && "PARTICIPANT_VERSION_OUTDATED".equals(error12.text)) {
            callFailed("ERROR_PEER_OUTDATED");
        } else if (error12.code == 403) {
            callFailed("ERROR_PRIVACY");
        } else if (error12.code == 406) {
            callFailed("ERROR_LOCALIZED");
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error on phone.requestCall: " + error12);
            }
            callFailed();
        }
    }

    /* renamed from: lambda$startOutgoingCall$7$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2504x93d45183() {
        this.timeoutRunnable = null;
        TLRPC.TL_phone_discardCall req1 = new TLRPC.TL_phone_discardCall();
        req1.peer = new TLRPC.TL_inputPhoneCall();
        req1.peer.access_hash = this.privateCall.access_hash;
        req1.peer.id = this.privateCall.id;
        req1.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new VoIPService$$ExternalSyntheticLambda82(this), 2);
    }

    /* renamed from: lambda$startOutgoingCall$6$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2503x7342682(TLObject response1, TLRPC.TL_error error1) {
        if (BuildVars.LOGS_ENABLED) {
            if (error1 != null) {
                FileLog.e("error on phone.discardCall: " + error1);
            } else {
                FileLog.d("phone.discardCall " + response1);
            }
        }
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda27(this));
    }

    private void acknowledgeCall(boolean startRinging) {
        if (this.privateCall instanceof TLRPC.TL_phoneCallDiscarded) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("Call " + this.privateCall.id + " was discarded before the service started, stopping");
            }
            stopSelf();
        } else if (Build.VERSION.SDK_INT < 19 || !XiaomiUtilities.isMIUI() || XiaomiUtilities.isCustomPermissionGranted(10020) || !((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            TLRPC.TL_phone_receivedCall req = new TLRPC.TL_phone_receivedCall();
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.id = this.privateCall.id;
            req.peer.access_hash = this.privateCall.access_hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda91(this, startRinging), 2);
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("MIUI: no permission to show when locked but the screen is locked. ¯\\_(ツ)_/¯");
            }
            stopSelf();
        }
    }

    /* renamed from: lambda$acknowledgeCall$12$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2438xcd5054c9(boolean startRinging, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda41(this, response, error, startRinging));
    }

    /* renamed from: lambda$acknowledgeCall$11$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2437x40b029c8(TLObject response, TLRPC.TL_error error, boolean startRinging) {
        if (sharedInstance != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("receivedCall response = " + response);
            }
            if (error != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("error on receivedCall: " + error);
                }
                stopSelf();
                return;
            }
            if (USE_CONNECTION_SERVICE) {
                ContactsController.getInstance(this.currentAccount).createOrUpdateConnectionServiceContact(this.user.id, this.user.first_name, this.user.last_name);
                Bundle extras = new Bundle();
                extras.putInt("call_type", 1);
                ((TelecomManager) getSystemService("telecom")).addNewIncomingCall(addAccountToTelecomManager(), extras);
            }
            if (startRinging) {
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

    public void requestVideoCall(boolean screencast) {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        int i = 0;
        if (nativeInstanceArr[0] != null) {
            if (!screencast) {
                long[] jArr = this.captureDevice;
                if (jArr[0] != 0) {
                    nativeInstanceArr[0].setupOutgoingVideoCreated(jArr[0]);
                    this.destroyCaptureDevice[0] = false;
                    this.isPrivateScreencast = screencast;
                }
            }
            NativeInstance nativeInstance = nativeInstanceArr[0];
            ProxyVideoSink proxyVideoSink = this.localSink[0];
            if (screencast) {
                i = 2;
            } else if (this.isFrontFaceCamera) {
                i = 1;
            }
            nativeInstance.setupOutgoingVideo(proxyVideoSink, i);
            this.isPrivateScreencast = screencast;
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

    public void createCaptureDevice(boolean screencast) {
        int deviceType;
        int index = screencast;
        if (screencast) {
            deviceType = 2;
        } else {
            deviceType = this.isFrontFaceCamera;
        }
        if (this.groupCall == null) {
            if (!this.isPrivateScreencast && screencast) {
                setVideoState(false, 0);
            }
            this.isPrivateScreencast = screencast;
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            if (nativeInstanceArr[0] != null) {
                nativeInstanceArr[0].clearVideoCapturer();
            }
        }
        if (index != 1) {
            long[] jArr = this.captureDevice;
            if (jArr[index] != 0 || this.tgVoip[index] == null) {
                NativeInstance[] nativeInstanceArr2 = this.tgVoip;
                if (!(nativeInstanceArr2[index] == null || jArr[index] == 0)) {
                    nativeInstanceArr2[index].activateVideoCapturer(jArr[index]);
                }
                if (this.captureDevice[index] != 0) {
                    return;
                }
            }
            this.captureDevice[index] = NativeInstance.createVideoCapturer(this.localSink[index], deviceType);
        } else if (this.groupCall != null) {
            long[] jArr2 = this.captureDevice;
            if (jArr2[index] == 0) {
                jArr2[index] = NativeInstance.createVideoCapturer(this.localSink[index], deviceType);
                createGroupInstance(1, false);
                setVideoState(true, 2);
                AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.groupCallScreencastStateChanged, new Object[0]);
            }
        } else {
            requestVideoCall(true);
            setVideoState(true, 2);
            if (VoIPFragment.getInstance() != null) {
                VoIPFragment.getInstance().onScreenCastStart();
            }
        }
    }

    public void setupCaptureDevice(boolean screencast, boolean micEnabled) {
        boolean z = false;
        if (!screencast) {
            int index = screencast;
            long[] jArr = this.captureDevice;
            if (jArr[index] != 0) {
                NativeInstance[] nativeInstanceArr = this.tgVoip;
                if (nativeInstanceArr[index] != null) {
                    nativeInstanceArr[index].setupOutgoingVideoCreated(jArr[index]);
                    this.destroyCaptureDevice[index] = false;
                    this.videoState[index] = 2;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (this.micMute == micEnabled) {
            setMicMute(!micEnabled, false, false);
            this.micSwitching = true;
        }
        if (this.groupCall != null) {
            TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            Boolean valueOf = Boolean.valueOf(!micEnabled);
            if (this.videoState[0] != 2) {
                z = true;
            }
            editCallMember(currentUser, valueOf, Boolean.valueOf(z), (Integer) null, (Boolean) null, new VoIPService$$ExternalSyntheticLambda20(this));
        }
    }

    /* renamed from: lambda$setupCaptureDevice$13$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2489xbfa2c4eb() {
        this.micSwitching = false;
    }

    public void clearCamera() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[0] != null) {
            nativeInstanceArr[0].clearVideoCapturer();
        }
    }

    public void setVideoState(boolean screencast, int state) {
        int i;
        char c = screencast;
        boolean z = false;
        int trueIndex = this.groupCall != null ? c : 0;
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[trueIndex] == null) {
            long[] jArr = this.captureDevice;
            if (jArr[c] != 0) {
                int[] iArr = this.videoState;
                iArr[trueIndex] = state;
                NativeInstance.setVideoStateCapturer(jArr[c], iArr[trueIndex]);
            } else if (state == 2 && (i = this.currentState) != 17 && i != 11) {
                jArr[c] = NativeInstance.createVideoCapturer(this.localSink[trueIndex], this.isFrontFaceCamera ? 1 : 0);
                this.videoState[trueIndex] = 2;
            }
        } else {
            int[] iArr2 = this.videoState;
            iArr2[trueIndex] = state;
            nativeInstanceArr[trueIndex].setVideoState(iArr2[trueIndex]);
            long[] jArr2 = this.captureDevice;
            if (jArr2[c] != 0) {
                NativeInstance.setVideoStateCapturer(jArr2[c], this.videoState[trueIndex]);
            }
            if (!screencast) {
                if (this.groupCall != null) {
                    TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                    if (this.videoState[0] != 2) {
                        z = true;
                    }
                    editCallMember(currentUser, (Boolean) null, Boolean.valueOf(z), (Integer) null, (Boolean) null, (Runnable) null);
                }
                checkIsNear();
            }
        }
    }

    public void stopScreenCapture() {
        if (this.groupCall != null && this.videoState[1] == 2) {
            TLRPC.TL_phone_leaveGroupCallPresentation req = new TLRPC.TL_phone_leaveGroupCallPresentation();
            req.call = this.groupCall.getInputGroupCall();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda83(this));
            NativeInstance instance = this.tgVoip[1];
            if (instance != null) {
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                instance.getClass();
                dispatchQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda44(instance));
            }
            this.mySource[1] = 0;
            this.tgVoip[1] = null;
            this.destroyCaptureDevice[1] = true;
            this.captureDevice[1] = 0;
            this.videoState[1] = 0;
            AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.groupCallScreencastStateChanged, new Object[0]);
        }
    }

    /* renamed from: lambda$stopScreenCapture$14$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2512x5d471f8d(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) response, false);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getVideoState(boolean r2) {
        /*
            r1 = this;
            int[] r0 = r1.videoState
            r0 = r0[r2]
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.getVideoState(boolean):int");
    }

    public void setSinks(VideoSink local, VideoSink remote) {
        setSinks(local, false, remote);
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
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r0 = r1.remoteSink
            r0 = r0[r3]
            r0.setTarget(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.setSinks(org.webrtc.VideoSink, boolean, org.webrtc.VideoSink):void");
    }

    public void setLocalSink(VideoSink local, boolean screencast) {
        if (!screencast) {
            this.localSink[0].setTarget(local);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setRemoteSink(org.webrtc.VideoSink r2, boolean r3) {
        /*
            r1 = this;
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r0 = r1.remoteSink
            r0 = r0[r3]
            r0.setTarget(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.setRemoteSink(org.webrtc.VideoSink, boolean):void");
    }

    public ProxyVideoSink addRemoteSink(TLRPC.TL_groupCallParticipant participant, boolean screencast, VideoSink remote, VideoSink background) {
        if (this.tgVoip[0] == null) {
            return null;
        }
        String endpointId = screencast ? participant.presentationEndpoint : participant.videoEndpoint;
        if (endpointId == null) {
            return null;
        }
        ProxyVideoSink sink = this.remoteSinks.get(endpointId);
        if (sink != null && sink.target == remote) {
            return sink;
        }
        if (sink == null) {
            sink = this.proxyVideoSinkLruCache.remove(endpointId);
        }
        if (sink == null) {
            sink = new ProxyVideoSink();
        }
        if (remote != null) {
            sink.setTarget(remote);
        }
        if (background != null) {
            sink.setBackground(background);
        }
        this.remoteSinks.put(endpointId, sink);
        long unused = sink.nativeInstance = this.tgVoip[0].addIncomingVideoOutput(1, endpointId, createSsrcGroups(screencast ? participant.presentation : participant.video), sink);
        return sink;
    }

    private NativeInstance.SsrcGroup[] createSsrcGroups(TLRPC.TL_groupCallParticipantVideo video) {
        if (video.source_groups.isEmpty()) {
            return null;
        }
        NativeInstance.SsrcGroup[] result = new NativeInstance.SsrcGroup[video.source_groups.size()];
        for (int a = 0; a < result.length; a++) {
            result[a] = new NativeInstance.SsrcGroup();
            TLRPC.TL_groupCallParticipantVideoSourceGroup group = video.source_groups.get(a);
            result[a].semantics = group.semantics;
            result[a].ssrcs = new int[group.sources.size()];
            for (int b = 0; b < result[a].ssrcs.length; b++) {
                result[a].ssrcs[b] = group.sources.get(b).intValue();
            }
        }
        return result;
    }

    public void requestFullScreen(TLRPC.TL_groupCallParticipant participant, boolean full, boolean screencast) {
        String endpointId = screencast ? participant.presentationEndpoint : participant.videoEndpoint;
        if (endpointId != null) {
            if (full) {
                this.tgVoip[0].setVideoEndpointQuality(endpointId, 2);
            } else {
                this.tgVoip[0].setVideoEndpointQuality(endpointId, 1);
            }
        }
    }

    public void removeRemoteSink(TLRPC.TL_groupCallParticipant participant, boolean presentation) {
        if (presentation) {
            ProxyVideoSink sink = this.remoteSinks.remove(participant.presentationEndpoint);
            if (sink != null) {
                this.tgVoip[0].removeIncomingVideoOutput(sink.nativeInstance);
                return;
            }
            return;
        }
        ProxyVideoSink sink2 = this.remoteSinks.remove(participant.videoEndpoint);
        if (sink2 != null) {
            this.tgVoip[0].removeIncomingVideoOutput(sink2.nativeInstance);
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isFullscreen(org.telegram.tgnet.TLRPC.TL_groupCallParticipant r3, boolean r4) {
        /*
            r2 = this;
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r0 = r2.currentBackgroundSink
            r0 = r0[r4]
            if (r0 == 0) goto L_0x0019
            java.lang.String[] r0 = r2.currentBackgroundEndpointId
            r0 = r0[r4]
            if (r4 == 0) goto L_0x000f
            java.lang.String r1 = r3.presentationEndpoint
            goto L_0x0011
        L_0x000f:
            java.lang.String r1 = r3.videoEndpoint
        L_0x0011:
            boolean r0 = android.text.TextUtils.equals(r0, r1)
            if (r0 == 0) goto L_0x0019
            r0 = 1
            goto L_0x001a
        L_0x0019:
            r0 = 0
        L_0x001a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.isFullscreen(org.telegram.tgnet.TLRPC$TL_groupCallParticipant, boolean):boolean");
    }

    public void setBackgroundSinks(VideoSink local, VideoSink remote) {
        this.localSink[0].setBackground(local);
        this.remoteSink[0].setBackground(remote);
    }

    public void swapSinks() {
        this.localSink[0].swap();
        this.remoteSink[0].swap();
    }

    public boolean isHangingUp() {
        return this.currentState == 10;
    }

    public void onSignalingData(TLRPC.TL_updatePhoneCallSignalingData data) {
        if (this.user != null) {
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            if (nativeInstanceArr[0] != null && !nativeInstanceArr[0].isGroup() && getCallID() == data.phone_call_id) {
                this.tgVoip[0].onSignalingDataReceive(data.data);
            }
        }
    }

    public long getSelfId() {
        TLRPC.InputPeer inputPeer = this.groupCallPeer;
        if (inputPeer == null) {
            return UserConfig.getInstance(this.currentAccount).clientUserId;
        }
        if (inputPeer instanceof TLRPC.TL_inputPeerUser) {
            return inputPeer.user_id;
        }
        if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
            return -inputPeer.channel_id;
        }
        return -inputPeer.chat_id;
    }

    public void onGroupCallParticipantsUpdate(TLRPC.TL_updateGroupCallParticipants update) {
        ChatObject.Call call;
        if (this.chat != null && (call = this.groupCall) != null && call.call.id == update.call.id) {
            long selfId = getSelfId();
            int N = update.participants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant participant = update.participants.get(a);
                if (participant.left) {
                    if (participant.source != 0 && participant.source == this.mySource[0]) {
                        int selfCount = 0;
                        for (int b = 0; b < N; b++) {
                            TLRPC.TL_groupCallParticipant p = update.participants.get(b);
                            if (p.self || p.source == this.mySource[0]) {
                                selfCount++;
                            }
                        }
                        if (selfCount > 1) {
                            hangUp(2);
                            return;
                        }
                    }
                } else if (MessageObject.getPeerId(participant.peer) == selfId) {
                    int i = participant.source;
                    int[] iArr = this.mySource;
                    if (i == iArr[0] || iArr[0] == 0 || participant.source == 0) {
                        if (ChatObject.isChannel(this.chat) && this.currentGroupModeStreaming && participant.can_self_unmute) {
                            this.switchingStream = true;
                            createGroupInstance(0, false);
                        }
                        if (participant.muted) {
                            setMicMute(true, false, false);
                        }
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("source mismatch my = " + this.mySource[0] + " psrc = " + participant.source);
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

    public void onGroupCallUpdated(TLRPC.GroupCall call) {
        ChatObject.Call call2;
        TLRPC.TL_dataJSON tL_dataJSON;
        if (this.chat != null && (call2 = this.groupCall) != null && call2.call.id == call.id) {
            if (this.groupCall.call instanceof TLRPC.TL_groupCallDiscarded) {
                hangUp(2);
                return;
            }
            boolean newModeStreaming = false;
            if (this.myParams != null) {
                try {
                    newModeStreaming = new JSONObject(this.myParams.data).optBoolean("stream");
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            boolean z = true;
            if ((this.currentState == 1 || newModeStreaming != this.currentGroupModeStreaming) && (tL_dataJSON = this.myParams) != null) {
                if (this.playedConnectedSound && newModeStreaming != this.currentGroupModeStreaming) {
                    this.switchingStream = true;
                }
                this.currentGroupModeStreaming = newModeStreaming;
                if (newModeStreaming) {
                    try {
                        NativeInstance nativeInstance = this.tgVoip[0];
                        if (this.groupCall.call == null || !this.groupCall.call.rtmp_stream) {
                            z = false;
                        }
                        nativeInstance.prepareForStream(z);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                        return;
                    }
                } else {
                    this.tgVoip[0].setJoinResponsePayload(tL_dataJSON.data);
                }
                dispatchStateChanged(2);
            }
        }
    }

    public void onCallUpdated(TLRPC.PhoneCall phoneCall) {
        if (this.user != null) {
            if (this.privateCall == null) {
                this.pendingUpdates.add(phoneCall);
            } else if (phoneCall != null) {
                if (phoneCall.id == this.privateCall.id) {
                    if (phoneCall.access_hash == 0) {
                        phoneCall.access_hash = this.privateCall.access_hash;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("Call updated: " + phoneCall);
                    }
                    this.privateCall = phoneCall;
                    if (phoneCall instanceof TLRPC.TL_phoneCallDiscarded) {
                        this.needSendDebugLog = phoneCall.need_debug;
                        this.needRateCall = phoneCall.need_rating;
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("call discarded, stopping service");
                        }
                        if (phoneCall.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) {
                            dispatchStateChanged(17);
                            this.playingSound = true;
                            Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda7(this));
                            AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1500);
                            endConnectionServiceCall(1500);
                            stopSelf();
                            return;
                        }
                        callEnded();
                    } else if (!(phoneCall instanceof TLRPC.TL_phoneCall) || this.authKey != null) {
                        if ((phoneCall instanceof TLRPC.TL_phoneCallAccepted) && this.authKey == null) {
                            processAcceptedCall();
                        } else if (this.currentState == 13 && phoneCall.receive_date != 0) {
                            dispatchStateChanged(16);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("!!!!!! CALL RECEIVED");
                            }
                            Runnable runnable = this.connectingSoundRunnable;
                            if (runnable != null) {
                                AndroidUtilities.cancelRunOnUIThread(runnable);
                                this.connectingSoundRunnable = null;
                            }
                            Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda8(this));
                            Runnable runnable2 = this.timeoutRunnable;
                            if (runnable2 != null) {
                                AndroidUtilities.cancelRunOnUIThread(runnable2);
                                this.timeoutRunnable = null;
                            }
                            VoIPService$$ExternalSyntheticLambda9 voIPService$$ExternalSyntheticLambda9 = new VoIPService$$ExternalSyntheticLambda9(this);
                            this.timeoutRunnable = voIPService$$ExternalSyntheticLambda9;
                            AndroidUtilities.runOnUIThread(voIPService$$ExternalSyntheticLambda9, (long) MessagesController.getInstance(this.currentAccount).callRingTimeout);
                        }
                    } else if (phoneCall.g_a_or_b == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("stopping VoIP service, Ga == null");
                        }
                        callFailed();
                    } else if (!Arrays.equals(this.g_a_hash, Utilities.computeSHA256(phoneCall.g_a_or_b, 0, (long) phoneCall.g_a_or_b.length))) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("stopping VoIP service, Ga hash doesn't match");
                        }
                        callFailed();
                    } else {
                        this.g_a = phoneCall.g_a_or_b;
                        BigInteger g_a2 = new BigInteger(1, phoneCall.g_a_or_b);
                        BigInteger p = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                        if (!Utilities.isGoodGaAndGb(g_a2, p)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.w("stopping VoIP service, bad Ga and Gb (accepting)");
                            }
                            callFailed();
                            return;
                        }
                        byte[] authKey2 = g_a2.modPow(new BigInteger(1, this.a_or_b), p).toByteArray();
                        if (authKey2.length > 256) {
                            byte[] correctedAuth = new byte[256];
                            System.arraycopy(authKey2, authKey2.length - 256, correctedAuth, 0, 256);
                            authKey2 = correctedAuth;
                        } else if (authKey2.length < 256) {
                            byte[] correctedAuth2 = new byte[256];
                            System.arraycopy(authKey2, 0, correctedAuth2, 256 - authKey2.length, authKey2.length);
                            for (int a = 0; a < 256 - authKey2.length; a++) {
                                correctedAuth2[a] = 0;
                            }
                            authKey2 = correctedAuth2;
                        }
                        byte[] authKeyHash = Utilities.computeSHA1(authKey2);
                        byte[] authKeyId = new byte[8];
                        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                        this.authKey = authKey2;
                        long bytesToLong = Utilities.bytesToLong(authKeyId);
                        this.keyFingerprint = bytesToLong;
                        if (bytesToLong != phoneCall.key_fingerprint) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.w("key fingerprints don't match");
                            }
                            callFailed();
                            return;
                        }
                        initiateActualEncryptedCall();
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("onCallUpdated called with wrong call id (got " + phoneCall.id + ", expected " + this.privateCall.id + ")");
                }
            }
        }
    }

    /* renamed from: lambda$onCallUpdated$15$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2473lambda$onCallUpdated$15$orgtelegrammessengervoipVoIPService() {
        this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* renamed from: lambda$onCallUpdated$16$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2474lambda$onCallUpdated$16$orgtelegrammessengervoipVoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.spRingbackID, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* renamed from: lambda$onCallUpdated$17$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2475lambda$onCallUpdated$17$orgtelegrammessengervoipVoIPService() {
        this.timeoutRunnable = null;
        declineIncomingCall(3, (Runnable) null);
    }

    private void startRatingActivity() {
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).putExtra("call_id", this.privateCall.id).putExtra("call_access_hash", this.privateCall.access_hash).putExtra("call_video", this.privateCall.video).putExtra("account", this.currentAccount).addFlags(NUM), 0).send();
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error starting incall activity", (Throwable) x);
            }
        }
    }

    public byte[] getEncryptionKey() {
        return this.authKey;
    }

    private void processAcceptedCall() {
        dispatchStateChanged(12);
        BigInteger p = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
        BigInteger i_authKey = new BigInteger(1, this.privateCall.g_b);
        if (!Utilities.isGoodGaAndGb(i_authKey, p)) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("stopping VoIP service, bad Ga and Gb");
            }
            callFailed();
            return;
        }
        byte[] authKey2 = i_authKey.modPow(new BigInteger(1, this.a_or_b), p).toByteArray();
        if (authKey2.length > 256) {
            byte[] correctedAuth = new byte[256];
            System.arraycopy(authKey2, authKey2.length - 256, correctedAuth, 0, 256);
            authKey2 = correctedAuth;
        } else if (authKey2.length < 256) {
            byte[] correctedAuth2 = new byte[256];
            System.arraycopy(authKey2, 0, correctedAuth2, 256 - authKey2.length, authKey2.length);
            for (int a = 0; a < 256 - authKey2.length; a++) {
                correctedAuth2[a] = 0;
            }
            authKey2 = correctedAuth2;
        }
        byte[] authKeyHash = Utilities.computeSHA1(authKey2);
        byte[] authKeyId = new byte[8];
        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
        long fingerprint = Utilities.bytesToLong(authKeyId);
        this.authKey = authKey2;
        this.keyFingerprint = fingerprint;
        TLRPC.TL_phone_confirmCall req = new TLRPC.TL_phone_confirmCall();
        req.g_a = this.g_a;
        req.key_fingerprint = fingerprint;
        req.peer = new TLRPC.TL_inputPhoneCall();
        req.peer.id = this.privateCall.id;
        req.peer.access_hash = this.privateCall.access_hash;
        req.protocol = new TLRPC.TL_phoneCallProtocol();
        req.protocol.max_layer = Instance.getConnectionMaxLayer();
        req.protocol.min_layer = 65;
        TLRPC.TL_phoneCallProtocol tL_phoneCallProtocol = req.protocol;
        req.protocol.udp_reflector = true;
        tL_phoneCallProtocol.udp_p2p = true;
        req.protocol.library_versions.addAll(Instance.AVAILABLE_VERSIONS);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda80(this));
    }

    /* renamed from: lambda$processAcceptedCall$19$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2487x66fcbb76(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda48(this, error, response));
    }

    /* renamed from: lambda$processAcceptedCall$18$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2486xda5CLASSNAME(TLRPC.TL_error error, TLObject response) {
        if (error != null) {
            callFailed();
            return;
        }
        this.privateCall = ((TLRPC.TL_phone_phoneCall) response).phone_call;
        initiateActualEncryptedCall();
    }

    private int convertDataSavingMode(int mode) {
        if (mode != 3) {
            return mode;
        }
        return ApplicationLoader.isRoaming() ? 1 : 0;
    }

    public void migrateToChat(TLRPC.Chat newChat) {
        this.chat = newChat;
    }

    public void setGroupCallPeer(TLRPC.InputPeer peer) {
        ChatObject.Call call = this.groupCall;
        if (call != null) {
            this.groupCallPeer = peer;
            call.setSelfPeer(peer);
            TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.groupCall.chatId);
            if (chatFull != null) {
                chatFull.groupcall_default_join_as = this.groupCall.selfPeer;
                if (chatFull.groupcall_default_join_as != null) {
                    if (chatFull instanceof TLRPC.TL_chatFull) {
                        chatFull.flags |= 32768;
                    } else {
                        chatFull.flags |= 67108864;
                    }
                } else if (chatFull instanceof TLRPC.TL_chatFull) {
                    chatFull.flags &= -32769;
                } else {
                    chatFull.flags &= -67108865;
                }
            }
            createGroupInstance(0, true);
            if (this.videoState[1] == 2) {
                createGroupInstance(1, true);
            }
        }
    }

    private void startGroupCall(int ssrc, String json, boolean create) {
        if (sharedInstance == this) {
            boolean z = true;
            if (this.createGroupCall) {
                ChatObject.Call call = new ChatObject.Call();
                this.groupCall = call;
                call.call = new TLRPC.TL_groupCall();
                this.groupCall.call.participants_count = 0;
                this.groupCall.call.version = 1;
                this.groupCall.call.can_start_video = true;
                this.groupCall.call.can_change_join_muted = true;
                this.groupCall.chatId = this.chat.id;
                this.groupCall.currentAccount = AccountInstance.getInstance(this.currentAccount);
                this.groupCall.setSelfPeer(this.groupCallPeer);
                this.groupCall.createNoVideoParticipant();
                dispatchStateChanged(6);
                TLRPC.TL_phone_createGroupCall req = new TLRPC.TL_phone_createGroupCall();
                req.peer = MessagesController.getInputPeer(this.chat);
                req.random_id = Utilities.random.nextInt();
                int i = this.scheduleDate;
                if (i != 0) {
                    req.schedule_date = i;
                    req.flags |= 2;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda81(this), 2);
                this.createGroupCall = false;
            } else if (json == null) {
                if (this.groupCall == null) {
                    ChatObject.Call groupCall2 = MessagesController.getInstance(this.currentAccount).getGroupCall(this.chat.id, false);
                    this.groupCall = groupCall2;
                    if (groupCall2 != null) {
                        groupCall2.setSelfPeer(this.groupCallPeer);
                    }
                }
                configureDeviceForCall();
                showNotification();
                AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda58.INSTANCE);
                createGroupInstance(0, false);
            } else if (getSharedInstance() != null && this.groupCall != null) {
                dispatchStateChanged(1);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("initital source = " + ssrc);
                }
                TLRPC.TL_phone_joinGroupCall req2 = new TLRPC.TL_phone_joinGroupCall();
                req2.muted = true;
                if (this.videoState[0] == 2) {
                    z = false;
                }
                req2.video_stopped = z;
                req2.call = this.groupCall.getInputGroupCall();
                req2.params = new TLRPC.TL_dataJSON();
                req2.params.data = json;
                if (!TextUtils.isEmpty(this.joinHash)) {
                    req2.invite_hash = this.joinHash;
                    req2.flags = 2 | req2.flags;
                }
                TLRPC.InputPeer inputPeer = this.groupCallPeer;
                if (inputPeer != null) {
                    req2.join_as = inputPeer;
                } else {
                    req2.join_as = new TLRPC.TL_inputPeerUser();
                    req2.join_as.user_id = AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientUserId();
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new VoIPService$$ExternalSyntheticLambda86(this, ssrc, create));
            }
        }
    }

    /* renamed from: lambda$startGroupCall$22$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2493lambda$startGroupCall$22$orgtelegrammessengervoipVoIPService(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            int a = 0;
            while (true) {
                if (a >= updates.updates.size()) {
                    break;
                }
                TLRPC.Update update = updates.updates.get(a);
                if (update instanceof TLRPC.TL_updateGroupCall) {
                    AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda51(this, (TLRPC.TL_updateGroupCall) update));
                    break;
                }
                a++;
            }
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            return;
        }
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda43(this, error));
    }

    /* renamed from: lambda$startGroupCall$20$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2491lambda$startGroupCall$20$orgtelegrammessengervoipVoIPService(TLRPC.TL_updateGroupCall updateGroupCall) {
        if (sharedInstance != null) {
            this.groupCall.call.access_hash = updateGroupCall.call.access_hash;
            this.groupCall.call.id = updateGroupCall.call.id;
            MessagesController.getInstance(this.currentAccount).putGroupCall(this.groupCall.chatId, this.groupCall);
            startGroupCall(0, (String) null, false);
        }
    }

    /* renamed from: lambda$startGroupCall$21$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2492lambda$startGroupCall$21$orgtelegrammessengervoipVoIPService(TLRPC.TL_error error) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, error.text);
        hangUp(0);
    }

    /* renamed from: lambda$startGroupCall$28$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2498lambda$startGroupCall$28$orgtelegrammessengervoipVoIPService(int ssrc, boolean create, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda31(this, ssrc));
            TLRPC.Updates updates = (TLRPC.Updates) response;
            long selfId = getSelfId();
            int N = updates.updates.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Update update = updates.updates.get(a);
                if (update instanceof TLRPC.TL_updateGroupCallParticipants) {
                    TLRPC.TL_updateGroupCallParticipants updateGroupCallParticipants = (TLRPC.TL_updateGroupCallParticipants) update;
                    int b = 0;
                    int N2 = updateGroupCallParticipants.participants.size();
                    while (true) {
                        if (b >= N2) {
                            break;
                        }
                        TLRPC.TL_groupCallParticipant participant = updateGroupCallParticipants.participants.get(b);
                        if (MessageObject.getPeerId(participant.peer) == selfId) {
                            AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda50(this, participant));
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("join source = " + participant.source);
                            }
                        } else {
                            b++;
                        }
                    }
                } else if (update instanceof TLRPC.TL_updateGroupCallConnection) {
                    TLRPC.TL_updateGroupCallConnection updateGroupCallConnection = (TLRPC.TL_updateGroupCallConnection) update;
                    if (!updateGroupCallConnection.presentation) {
                        this.myParams = updateGroupCallConnection.params;
                    }
                }
            }
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda53(this, create));
            startGroupCheckShortpoll();
            TLRPC.TL_error tL_error = error;
            return;
        }
        int i = ssrc;
        boolean z = create;
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda45(this, error));
    }

    /* renamed from: lambda$startGroupCall$24$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2494lambda$startGroupCall$24$orgtelegrammessengervoipVoIPService(int ssrc) {
        this.mySource[0] = ssrc;
    }

    /* renamed from: lambda$startGroupCall$25$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2495lambda$startGroupCall$25$orgtelegrammessengervoipVoIPService(TLRPC.TL_groupCallParticipant participant) {
        this.mySource[0] = participant.source;
    }

    /* renamed from: lambda$startGroupCall$26$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2496lambda$startGroupCall$26$orgtelegrammessengervoipVoIPService(boolean create) {
        this.groupCall.loadMembers(create);
    }

    /* renamed from: lambda$startGroupCall$27$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2497lambda$startGroupCall$27$orgtelegrammessengervoipVoIPService(TLRPC.TL_error error) {
        if ("JOIN_AS_PEER_INVALID".equals(error.text)) {
            TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chat.id);
            if (chatFull != null) {
                if (chatFull instanceof TLRPC.TL_chatFull) {
                    chatFull.flags &= -32769;
                } else {
                    chatFull.flags &= -67108865;
                }
                chatFull.groupcall_default_join_as = null;
                JoinCallAlert.resetCache();
            }
            hangUp(2);
        } else if ("GROUPCALL_SSRC_DUPLICATE_MUCH".equals(error.text)) {
            createGroupInstance(0, false);
        } else {
            if ("GROUPCALL_INVALID".equals(error.text)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat.id, 0, true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, 6, error.text);
            hangUp(0);
        }
    }

    private void startScreenCapture(int ssrc, String json) {
        if (getSharedInstance() != null && this.groupCall != null) {
            this.mySource[1] = 0;
            TLRPC.TL_phone_joinGroupCallPresentation req = new TLRPC.TL_phone_joinGroupCallPresentation();
            req.call = this.groupCall.getInputGroupCall();
            req.params = new TLRPC.TL_dataJSON();
            req.params.data = json;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda84(this, ssrc));
        }
    }

    /* renamed from: lambda$startScreenCapture$32$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2511xe204004f(int ssrc, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda32(this, ssrc));
            TLRPC.Updates updates = (TLRPC.Updates) response;
            AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda52(this, updates));
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            startGroupCheckShortpoll();
            return;
        }
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda46(this, error));
    }

    /* renamed from: lambda$startScreenCapture$29$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2508xb2ffvar_(int ssrc) {
        this.mySource[1] = ssrc;
    }

    /* renamed from: lambda$startScreenCapture$30$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2509xc8c3aa4d(TLRPC.Updates updates) {
        VoIPService voIPService = this;
        TLRPC.Updates updates2 = updates;
        char c = 1;
        if (voIPService.tgVoip[1] != null) {
            long selfId = getSelfId();
            int a = 0;
            int N = updates2.updates.size();
            while (a < N) {
                TLRPC.Update update = updates2.updates.get(a);
                if (update instanceof TLRPC.TL_updateGroupCallConnection) {
                    TLRPC.TL_updateGroupCallConnection updateGroupCallConnection = (TLRPC.TL_updateGroupCallConnection) update;
                    if (updateGroupCallConnection.presentation) {
                        voIPService.tgVoip[c].setJoinResponsePayload(updateGroupCallConnection.params.data);
                    }
                } else if (update instanceof TLRPC.TL_updateGroupCallParticipants) {
                    TLRPC.TL_updateGroupCallParticipants updateGroupCallParticipants = (TLRPC.TL_updateGroupCallParticipants) update;
                    int b = 0;
                    int N2 = updateGroupCallParticipants.participants.size();
                    while (true) {
                        if (b >= N2) {
                            break;
                        }
                        TLRPC.TL_groupCallParticipant participant = updateGroupCallParticipants.participants.get(b);
                        if (MessageObject.getPeerId(participant.peer) != selfId) {
                            b++;
                            voIPService = this;
                        } else if (participant.presentation != null) {
                            if ((participant.presentation.flags & 2) != 0) {
                                voIPService.mySource[c] = participant.presentation.audio_source;
                            } else {
                                int c2 = 0;
                                int N3 = participant.presentation.source_groups.size();
                                while (c2 < N3) {
                                    TLRPC.TL_groupCallParticipantVideoSourceGroup sourceGroup = participant.presentation.source_groups.get(c2);
                                    if (sourceGroup.sources.size() > 0) {
                                        c = 1;
                                        voIPService.mySource[1] = sourceGroup.sources.get(0).intValue();
                                    }
                                    c2++;
                                    voIPService = this;
                                }
                            }
                        }
                    }
                }
                a++;
                voIPService = this;
            }
        }
    }

    /* renamed from: lambda$startScreenCapture$31$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2510x5563d54e(TLRPC.TL_error error) {
        if ("GROUPCALL_VIDEO_TOO_MUCH".equals(error.text)) {
            this.groupCall.reloadGroupCall();
        } else if ("JOIN_AS_PEER_INVALID".equals(error.text)) {
            TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chat.id);
            if (chatFull != null) {
                if (chatFull instanceof TLRPC.TL_chatFull) {
                    chatFull.flags &= -32769;
                } else {
                    chatFull.flags &= -67108865;
                }
                chatFull.groupcall_default_join_as = null;
                JoinCallAlert.resetCache();
            }
            hangUp(2);
        } else if ("GROUPCALL_SSRC_DUPLICATE_MUCH".equals(error.text)) {
            createGroupInstance(1, false);
        } else if ("GROUPCALL_INVALID".equals(error.text)) {
            MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat.id, 0, true);
        }
    }

    private void startGroupCheckShortpoll() {
        ChatObject.Call call;
        if (this.shortPollRunnable == null && sharedInstance != null && (call = this.groupCall) != null) {
            int[] iArr = this.mySource;
            if (iArr[0] != 0 || iArr[1] != 0 || (call.call != null && this.groupCall.call.rtmp_stream)) {
                VoIPService$$ExternalSyntheticLambda23 voIPService$$ExternalSyntheticLambda23 = new VoIPService$$ExternalSyntheticLambda23(this);
                this.shortPollRunnable = voIPService$$ExternalSyntheticLambda23;
                AndroidUtilities.runOnUIThread(voIPService$$ExternalSyntheticLambda23, 4000);
            }
        }
    }

    /* renamed from: lambda$startGroupCheckShortpoll$35$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2501x130015a() {
        ChatObject.Call call;
        if (this.shortPollRunnable != null && sharedInstance != null && (call = this.groupCall) != null) {
            int[] iArr = this.mySource;
            if (iArr[0] != 0 || iArr[1] != 0 || (call.call != null && this.groupCall.call.rtmp_stream)) {
                TLRPC.TL_phone_checkGroupCall req = new TLRPC.TL_phone_checkGroupCall();
                req.call = this.groupCall.getInputGroupCall();
                int a = 0;
                while (true) {
                    int[] iArr2 = this.mySource;
                    if (a < iArr2.length) {
                        if (iArr2[a] != 0) {
                            req.sources.add(Integer.valueOf(this.mySource[a]));
                        }
                        a++;
                    } else {
                        this.checkRequestId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda90(this, req));
                        return;
                    }
                }
            }
        }
    }

    /* renamed from: lambda$startGroupCheckShortpoll$34$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2500x748fd659(TLRPC.TL_phone_checkGroupCall req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda42(this, response, req, error));
    }

    /* renamed from: lambda$startGroupCheckShortpoll$33$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2499xe7efab58(TLObject response, TLRPC.TL_phone_checkGroupCall req, TLRPC.TL_error error) {
        if (this.shortPollRunnable != null && sharedInstance != null && this.groupCall != null) {
            this.shortPollRunnable = null;
            this.checkRequestId = 0;
            boolean recreateCamera = false;
            boolean recreateScreenCapture = false;
            if (response instanceof TLRPC.Vector) {
                TLRPC.Vector vector = (TLRPC.Vector) response;
                if (this.mySource[0] != 0 && req.sources.contains(Integer.valueOf(this.mySource[0])) && !vector.objects.contains(Integer.valueOf(this.mySource[0]))) {
                    recreateCamera = true;
                }
                if (this.mySource[1] != 0 && req.sources.contains(Integer.valueOf(this.mySource[1])) && !vector.objects.contains(Integer.valueOf(this.mySource[1]))) {
                    recreateScreenCapture = true;
                }
            } else if (error != null && error.code == 400) {
                recreateCamera = true;
                if (this.mySource[1] != 0 && req.sources.contains(Integer.valueOf(this.mySource[1]))) {
                    recreateScreenCapture = true;
                }
            }
            if (recreateCamera) {
                createGroupInstance(0, false);
            }
            if (recreateScreenCapture) {
                createGroupInstance(1, false);
            }
            int[] iArr = this.mySource;
            if (iArr[1] != 0 || iArr[0] != 0 || (this.groupCall.call != null && this.groupCall.call.rtmp_stream)) {
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
        public TLRPC.TL_groupCallParticipant participant;

        public RequestedParticipant(TLRPC.TL_groupCallParticipant p, int ssrc) {
            this.participant = p;
            this.audioSsrc = ssrc;
        }
    }

    private void broadcastUnknownParticipants(long taskPtr, int[] unknown) {
        int[] iArr = unknown;
        if (this.groupCall == null) {
            long j = taskPtr;
        } else if (this.tgVoip[0] == null) {
            long j2 = taskPtr;
        } else {
            long selfId = getSelfId();
            ArrayList<RequestedParticipant> participants = null;
            int N = iArr.length;
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant p = this.groupCall.participantsBySources.get(iArr[a]);
                if (p == null && (p = this.groupCall.participantsByVideoSources.get(iArr[a])) == null) {
                    p = this.groupCall.participantsByPresentationSources.get(iArr[a]);
                }
                if (!(p == null || MessageObject.getPeerId(p.peer) == selfId || p.source == 0)) {
                    if (participants == null) {
                        participants = new ArrayList<>();
                    }
                    participants.add(new RequestedParticipant(p, iArr[a]));
                }
            }
            if (participants != null) {
                int[] ssrcs = new int[participants.size()];
                int N2 = participants.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    ssrcs[a2] = participants.get(a2).audioSsrc;
                }
                this.tgVoip[0].onMediaDescriptionAvailable(taskPtr, ssrcs);
                int N3 = participants.size();
                for (int a3 = 0; a3 < N3; a3++) {
                    RequestedParticipant p2 = participants.get(a3);
                    if (p2.participant.muted_by_you) {
                        this.tgVoip[0].setVolume(p2.audioSsrc, 0.0d);
                    } else {
                        NativeInstance nativeInstance = this.tgVoip[0];
                        int i = p2.audioSsrc;
                        double participantVolume = (double) ChatObject.getParticipantVolume(p2.participant);
                        Double.isNaN(participantVolume);
                        nativeInstance.setVolume(i, participantVolume / 10000.0d);
                    }
                }
                return;
            }
            long j3 = taskPtr;
        }
    }

    private void createGroupInstance(int type, boolean switchAccount) {
        String str;
        int i = type;
        boolean z = switchAccount;
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
        boolean created = false;
        if (this.tgVoip[i] == null) {
            created = true;
            if (BuildVars.DEBUG_VERSION) {
                str = VoIPHelper.getLogFilePath("voip_" + i + "_" + this.groupCall.call.id);
            } else {
                str = VoIPHelper.getLogFilePath(this.groupCall.call.id, false);
            }
            String logFilePath = str;
            this.tgVoip[i] = NativeInstance.makeGroup(logFilePath, this.captureDevice[i], i == 1, i == 0 && SharedConfig.noiseSupression, new VoIPService$$ExternalSyntheticLambda69(this, i), new VoIPService$$ExternalSyntheticLambda68(this, i), new VoIPService$$ExternalSyntheticLambda73(this, i), new VoIPService$$ExternalSyntheticLambda70(this, i), new VoIPService$$ExternalSyntheticLambda71(this, i), new VoIPService$$ExternalSyntheticLambda72(this, i));
            this.tgVoip[i].setOnStateUpdatedListener(new VoIPService$$ExternalSyntheticLambda65(this, i));
        }
        this.tgVoip[i].resetGroupInstance(!created, false);
        if (this.captureDevice[i] != 0) {
            this.destroyCaptureDevice[i] = false;
        }
        if (i == 0) {
            dispatchStateChanged(1);
        }
    }

    /* renamed from: lambda$createGroupInstance$36$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2447xffCLASSNAMEc4d(int type, int ssrc, String json) {
        if (type == 0) {
            startGroupCall(ssrc, json, true);
        } else {
            startScreenCapture(ssrc, json);
        }
    }

    /* renamed from: lambda$createGroupInstance$38$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2448x1906d24f(int type, int[] uids, float[] levels, boolean[] voice) {
        ChatObject.Call call;
        int[] iArr = uids;
        float[] fArr = levels;
        boolean[] zArr = voice;
        if (sharedInstance != null && (call = this.groupCall) != null && type == 0) {
            call.processVoiceLevelsUpdate(iArr, fArr, zArr);
            float maxAmplitude = 0.0f;
            boolean hasOther = false;
            for (int a = 0; a < iArr.length; a++) {
                if (iArr[a] == 0) {
                    if (this.lastTypingTimeSend < SystemClock.uptimeMillis() - 5000 && fArr[a] > 0.1f && zArr[a]) {
                        this.lastTypingTimeSend = SystemClock.uptimeMillis();
                        TLRPC.TL_messages_setTyping req = new TLRPC.TL_messages_setTyping();
                        req.action = new TLRPC.TL_speakingInGroupCallAction();
                        req.peer = MessagesController.getInputPeer(this.chat);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, VoIPService$$ExternalSyntheticLambda94.INSTANCE);
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(fArr[a]));
                } else {
                    hasOther = true;
                    maxAmplitude = Math.max(maxAmplitude, fArr[a]);
                }
            }
            if (hasOther) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcSpeakerAmplitudeEvent, Float.valueOf(maxAmplitude));
                NativeInstance.AudioLevelsCallback audioLevelsCallback2 = audioLevelsCallback;
                if (audioLevelsCallback2 != null) {
                    audioLevelsCallback2.run(iArr, fArr, zArr);
                }
            }
        }
    }

    static /* synthetic */ void lambda$createGroupInstance$37(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$createGroupInstance$40$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2450xbb6aavar_(int type, long taskPtr, int[] unknown) {
        ChatObject.Call call;
        if (sharedInstance != null && (call = this.groupCall) != null && type == 0) {
            call.processUnknownVideoParticipants(unknown, new VoIPService$$ExternalSyntheticLambda60(this, taskPtr, unknown));
        }
    }

    /* renamed from: lambda$createGroupInstance$39$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2449xa5a6fd50(long taskPtr, int[] unknown, ArrayList ssrcs) {
        if (sharedInstance != null && this.groupCall != null) {
            broadcastUnknownParticipants(taskPtr, unknown);
        }
    }

    /* renamed from: lambda$createGroupInstance$45$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2455x7a8b866b(int type, long timestamp, long duration, int videoChannel, int quality) {
        StringBuilder sb;
        long j = timestamp;
        int i = videoChannel;
        int i2 = quality;
        if (type == 0) {
            TLRPC.TL_upload_getFile req = new TLRPC.TL_upload_getFile();
            req.limit = 131072;
            TLRPC.TL_inputGroupCallStream inputGroupCallStream = new TLRPC.TL_inputGroupCallStream();
            inputGroupCallStream.call = this.groupCall.getInputGroupCall();
            inputGroupCallStream.time_ms = j;
            if (duration == 500) {
                inputGroupCallStream.scale = 1;
            }
            if (i != 0) {
                inputGroupCallStream.flags |= 1;
                inputGroupCallStream.video_channel = i;
                inputGroupCallStream.video_quality = i2;
            }
            req.location = inputGroupCallStream;
            if (i == 0) {
                sb.append("");
                sb.append(j);
            } else {
                sb = new StringBuilder();
                sb.append(i);
                sb.append("_");
                sb.append(j);
                sb.append("_");
                sb.append(i2);
            }
            String key = sb.toString();
            AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda40(this, key, AccountInstance.getInstance(this.currentAccount).getConnectionsManager().sendRequest(req, new VoIPService$$ExternalSyntheticLambda98(this, key, type, timestamp, videoChannel, quality), 2, 2, this.groupCall.call.stream_dc_id)));
        }
    }

    /* renamed from: lambda$createGroupInstance$41$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2451x480ada67(String key) {
        this.currentStreamRequestTimestamp.remove(key);
    }

    /* renamed from: lambda$createGroupInstance$43$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2453x614b3069(String key, int type, long timestamp, int videoChannel, int quality, TLObject response, TLRPC.TL_error error, long responseTime) {
        int status;
        int i = type;
        TLRPC.TL_error tL_error = error;
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda39(this, key));
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[i] != null) {
            if (response != null) {
                TLRPC.TL_upload_file res = (TLRPC.TL_upload_file) response;
                nativeInstanceArr[i].onStreamPartAvailable(timestamp, res.bytes.buffer, res.bytes.limit(), responseTime, videoChannel, quality);
            } else if ("GROUPCALL_JOIN_MISSING".equals(tL_error.text)) {
                AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda28(this, i));
            } else {
                if ("TIME_TOO_BIG".equals(tL_error.text) || tL_error.text.startsWith("FLOOD_WAIT")) {
                    status = 0;
                } else {
                    status = -1;
                }
                this.tgVoip[i].onStreamPartAvailable(timestamp, (ByteBuffer) null, status, responseTime, videoChannel, quality);
            }
        }
    }

    /* renamed from: lambda$createGroupInstance$42$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2452xd4ab0568(int type) {
        createGroupInstance(type, false);
    }

    /* renamed from: lambda$createGroupInstance$44$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2454xedeb5b6a(String key, int reqId) {
        this.currentStreamRequestTimestamp.put(key, Integer.valueOf(reqId));
    }

    /* renamed from: lambda$createGroupInstance$47$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2457x93cbdc6d(int type, long timestamp, long duration, int videoChannel, int quality) {
        if (type == 0) {
            AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda36(this, videoChannel, timestamp, quality));
        }
    }

    /* renamed from: lambda$createGroupInstance$46$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2456x72bb16c(int videoChannel, long timestamp, int quality) {
        StringBuilder sb;
        if (videoChannel == 0) {
            sb.append("");
            sb.append(timestamp);
        } else {
            sb = new StringBuilder();
            sb.append(videoChannel);
            sb.append("_");
            sb.append(timestamp);
            sb.append("_");
            sb.append(quality);
        }
        String key = sb.toString();
        Integer reqId = this.currentStreamRequestTimestamp.get(key);
        if (reqId != null) {
            AccountInstance.getInstance(this.currentAccount).getConnectionsManager().cancelRequest(reqId.intValue(), true);
            this.currentStreamRequestTimestamp.remove(key);
        }
    }

    /* renamed from: lambda$createGroupInstance$49$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2459xad0CLASSNAMEf(int type, long taskPtr) {
        ChatObject.Call call = this.groupCall;
        if (call == null || call.call == null || !this.groupCall.call.rtmp_stream) {
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            if (nativeInstanceArr[type] != null) {
                nativeInstanceArr[type].onRequestTimeComplete(taskPtr, ConnectionsManager.getInstance(this.currentAccount).getCurrentTimeMillis());
                return;
            }
            return;
        }
        TLRPC.TL_phone_getGroupCallStreamChannels req = new TLRPC.TL_phone_getGroupCallStreamChannels();
        req.call = this.groupCall.getInputGroupCall();
        ChatObject.Call call2 = this.groupCall;
        if (call2 == null || call2.call == null || this.tgVoip[type] == null) {
            NativeInstance[] nativeInstanceArr2 = this.tgVoip;
            if (nativeInstanceArr2[type] != null) {
                nativeInstanceArr2[type].onRequestTimeComplete(taskPtr, 0);
                return;
            }
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda97(this, type, taskPtr), 2, 2, this.groupCall.call.stream_dc_id);
    }

    /* renamed from: lambda$createGroupInstance$48$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2458x206CLASSNAMEe(int type, long taskPtr, TLObject response, TLRPC.TL_error error, long responseTime) {
        long currentTime = 0;
        if (error == null) {
            TLRPC.TL_phone_groupCallStreamChannels res = (TLRPC.TL_phone_groupCallStreamChannels) response;
            if (!res.channels.isEmpty()) {
                currentTime = res.channels.get(0).last_timestamp_ms;
            }
            if (!this.groupCall.loadedRtmpStreamParticipant) {
                this.groupCall.createRtmpStreamParticipant(res.channels);
                this.groupCall.loadedRtmpStreamParticipant = true;
            }
        }
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[type] != null) {
            nativeInstanceArr[type].onRequestTimeComplete(taskPtr, currentTime);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: updateConnectionState */
    public void m2460xc2cfe485(int type, int state, boolean inTransition) {
        if (type == 0) {
            dispatchStateChanged((state == 1 || this.switchingStream) ? 3 : 5);
            if (this.switchingStream && (state == 0 || (state == 1 && inTransition))) {
                VoIPService$$ExternalSyntheticLambda34 voIPService$$ExternalSyntheticLambda34 = new VoIPService$$ExternalSyntheticLambda34(this, type);
                this.switchingStreamTimeoutRunnable = voIPService$$ExternalSyntheticLambda34;
                AndroidUtilities.runOnUIThread(voIPService$$ExternalSyntheticLambda34, 3000);
            }
            if (state == 0) {
                startGroupCheckShortpoll();
                if (this.playedConnectedSound && this.spPlayId == 0 && !this.switchingStream && !this.switchingAccount) {
                    Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda25(this));
                    return;
                }
                return;
            }
            cancelGroupCheckShortPoll();
            if (!inTransition) {
                this.switchingStream = false;
                this.switchingAccount = false;
            }
            Runnable runnable = this.switchingStreamTimeoutRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.switchingStreamTimeoutRunnable = null;
            }
            if (this.playedConnectedSound) {
                Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda26(this));
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
                NativeInstance instance = this.tgVoip[0];
                if (instance != null && !this.micMute) {
                    instance.setMuteMicrophone(false);
                }
                setParticipantsVolume();
            }
        }
    }

    /* renamed from: lambda$updateConnectionState$51$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2514xfaecad54(int type) {
        if (this.switchingStreamTimeoutRunnable != null) {
            this.switchingStream = false;
            m2460xc2cfe485(type, 0, true);
            this.switchingStreamTimeoutRunnable = null;
        }
    }

    /* renamed from: lambda$updateConnectionState$52$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2515x878cd855() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.spVoiceChatConnecting, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* renamed from: lambda$updateConnectionState$53$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2516x142d0356() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    public void setParticipantsVolume() {
        if (this.tgVoip[0] != null) {
            int N = this.groupCall.participants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant participant = this.groupCall.participants.valueAt(a);
                if (!participant.self && participant.source != 0 && (participant.can_self_unmute || !participant.muted)) {
                    if (participant.muted_by_you) {
                        setParticipantVolume(participant, 0);
                    } else {
                        setParticipantVolume(participant, ChatObject.getParticipantVolume(participant));
                    }
                }
            }
        }
    }

    public void setParticipantVolume(TLRPC.TL_groupCallParticipant participant, int volume) {
        NativeInstance nativeInstance = this.tgVoip[0];
        int i = participant.source;
        double d = (double) volume;
        Double.isNaN(d);
        nativeInstance.setVolume(i, d / 10000.0d);
        if (participant.presentation != null && participant.presentation.audio_source != 0) {
            NativeInstance nativeInstance2 = this.tgVoip[0];
            int i2 = participant.presentation.audio_source;
            double d2 = (double) volume;
            Double.isNaN(d2);
            nativeInstance2.setVolume(i2, d2 / 10000.0d);
        }
    }

    public boolean isSwitchingStream() {
        return this.switchingStream;
    }

    /* JADX WARNING: Removed duplicated region for block: B:104:0x02b8 A[Catch:{ Exception -> 0x0386 }] */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x035b A[Catch:{ Exception -> 0x0386 }] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x038b  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0112 A[Catch:{ Exception -> 0x0386 }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0120 A[Catch:{ Exception -> 0x0386 }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x013e A[Catch:{ Exception -> 0x0386 }] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0191 A[Catch:{ Exception -> 0x0386 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0195 A[Catch:{ Exception -> 0x0386 }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01a5 A[SYNTHETIC, Splitter:B:74:0x01a5] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0221 A[Catch:{ Exception -> 0x020a }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x022c A[Catch:{ Exception -> 0x0386 }] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0296 A[Catch:{ Exception -> 0x0386 }] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0298 A[Catch:{ Exception -> 0x0386 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initiateActualEncryptedCall() {
        /*
            r61 = this;
            r1 = r61
            java.lang.String r2 = "calls_access_hashes"
            java.lang.String r3 = " "
            java.lang.Runnable r0 = r1.timeoutRunnable
            r4 = 0
            if (r0 == 0) goto L_0x0010
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r1.timeoutRunnable = r4
        L_0x0010:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0386 }
            if (r0 == 0) goto L_0x002a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0386 }
            r0.<init>()     // Catch:{ Exception -> 0x0386 }
            java.lang.String r5 = "InitCall: keyID="
            r0.append(r5)     // Catch:{ Exception -> 0x0386 }
            long r5 = r1.keyFingerprint     // Catch:{ Exception -> 0x0386 }
            r0.append(r5)     // Catch:{ Exception -> 0x0386 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x0386 }
        L_0x002a:
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x0386 }
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0)     // Catch:{ Exception -> 0x0386 }
            r5 = r0
            java.util.Set r0 = r5.getStringSet(r2, r4)     // Catch:{ Exception -> 0x0386 }
            r6 = r0
            if (r6 == 0) goto L_0x003f
            java.util.HashSet r0 = new java.util.HashSet     // Catch:{ Exception -> 0x0386 }
            r0.<init>(r6)     // Catch:{ Exception -> 0x0386 }
            r7 = r0
            goto L_0x0045
        L_0x003f:
            java.util.HashSet r0 = new java.util.HashSet     // Catch:{ Exception -> 0x0386 }
            r0.<init>()     // Catch:{ Exception -> 0x0386 }
            r7 = r0
        L_0x0045:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0386 }
            r0.<init>()     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$PhoneCall r8 = r1.privateCall     // Catch:{ Exception -> 0x0386 }
            long r8 = r8.id     // Catch:{ Exception -> 0x0386 }
            r0.append(r8)     // Catch:{ Exception -> 0x0386 }
            r0.append(r3)     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$PhoneCall r8 = r1.privateCall     // Catch:{ Exception -> 0x0386 }
            long r8 = r8.access_hash     // Catch:{ Exception -> 0x0386 }
            r0.append(r8)     // Catch:{ Exception -> 0x0386 }
            r0.append(r3)     // Catch:{ Exception -> 0x0386 }
            long r8 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0386 }
            r0.append(r8)     // Catch:{ Exception -> 0x0386 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0386 }
            r7.add(r0)     // Catch:{ Exception -> 0x0386 }
        L_0x006c:
            int r0 = r7.size()     // Catch:{ Exception -> 0x0386 }
            r8 = 20
            r9 = 2
            if (r0 <= r8) goto L_0x00b2
            r0 = 0
            r10 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            java.util.Iterator r8 = r7.iterator()     // Catch:{ Exception -> 0x0386 }
            r11 = r10
            r10 = r0
        L_0x0081:
            boolean r0 = r8.hasNext()     // Catch:{ Exception -> 0x0386 }
            if (r0 == 0) goto L_0x00ac
            java.lang.Object r0 = r8.next()     // Catch:{ Exception -> 0x0386 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x0386 }
            r13 = r0
            java.lang.String[] r0 = r13.split(r3)     // Catch:{ Exception -> 0x0386 }
            r14 = r0
            int r0 = r14.length     // Catch:{ Exception -> 0x0386 }
            if (r0 >= r9) goto L_0x009a
            r8.remove()     // Catch:{ Exception -> 0x0386 }
            goto L_0x00ab
        L_0x009a:
            r0 = r14[r9]     // Catch:{ Exception -> 0x00a7 }
            long r15 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x00a7 }
            int r0 = (r15 > r11 ? 1 : (r15 == r11 ? 0 : -1))
            if (r0 >= 0) goto L_0x00a6
            r11 = r15
            r10 = r13
        L_0x00a6:
            goto L_0x00ab
        L_0x00a7:
            r0 = move-exception
            r8.remove()     // Catch:{ Exception -> 0x0386 }
        L_0x00ab:
            goto L_0x0081
        L_0x00ac:
            if (r10 == 0) goto L_0x00b1
            r7.remove(r10)     // Catch:{ Exception -> 0x0386 }
        L_0x00b1:
            goto L_0x006c
        L_0x00b2:
            android.content.SharedPreferences$Editor r0 = r5.edit()     // Catch:{ Exception -> 0x0386 }
            android.content.SharedPreferences$Editor r0 = r0.putStringSet(r2, r7)     // Catch:{ Exception -> 0x0386 }
            r0.commit()     // Catch:{ Exception -> 0x0386 }
            r2 = 0
            r3 = 0
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0386 }
            r8 = 16
            if (r0 < r8) goto L_0x00d3
            boolean r0 = android.media.audiofx.AcousticEchoCanceler.isAvailable()     // Catch:{ Exception -> 0x00cb }
            r2 = r0
            goto L_0x00cc
        L_0x00cb:
            r0 = move-exception
        L_0x00cc:
            boolean r0 = android.media.audiofx.NoiseSuppressor.isAvailable()     // Catch:{ Exception -> 0x00d2 }
            r3 = r0
            goto L_0x00d3
        L_0x00d2:
            r0 = move-exception
        L_0x00d3:
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0386 }
            int r8 = r1.currentAccount     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)     // Catch:{ Exception -> 0x0386 }
            int r10 = r8.callConnectTimeout     // Catch:{ Exception -> 0x0386 }
            double r10 = (double) r10
            r12 = 4652007308841189376(0x408fNUM, double:1000.0)
            java.lang.Double.isNaN(r10)
            double r10 = r10 / r12
            int r14 = r8.callPacketTimeout     // Catch:{ Exception -> 0x0386 }
            double r14 = (double) r14
            java.lang.Double.isNaN(r14)
            double r12 = r14 / r12
            java.lang.String r14 = "VoipDataSaving"
            int r15 = org.telegram.ui.Components.voip.VoIPHelper.getDataSavingDefault()     // Catch:{ Exception -> 0x0386 }
            int r14 = r0.getInt(r14, r15)     // Catch:{ Exception -> 0x0386 }
            int r19 = r1.convertDataSavingMode(r14)     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.Instance$ServerConfig r14 = org.telegram.messenger.voip.Instance.getGlobalServerConfig()     // Catch:{ Exception -> 0x0386 }
            r15 = r14
            if (r2 == 0) goto L_0x010e
            boolean r4 = r15.useSystemAec     // Catch:{ Exception -> 0x0386 }
            if (r4 != 0) goto L_0x010b
            goto L_0x010e
        L_0x010b:
            r21 = 0
            goto L_0x0110
        L_0x010e:
            r21 = 1
        L_0x0110:
            if (r3 == 0) goto L_0x011a
            boolean r4 = r15.useSystemNs     // Catch:{ Exception -> 0x0386 }
            if (r4 != 0) goto L_0x0117
            goto L_0x011a
        L_0x0117:
            r22 = 0
            goto L_0x011c
        L_0x011a:
            r22 = 1
        L_0x011c:
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0386 }
            if (r4 == 0) goto L_0x013e
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0386 }
            r4.<init>()     // Catch:{ Exception -> 0x0386 }
            java.lang.String r14 = "voip"
            r4.append(r14)     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$PhoneCall r14 = r1.privateCall     // Catch:{ Exception -> 0x0386 }
            r29 = r10
            long r9 = r14.id     // Catch:{ Exception -> 0x0386 }
            r4.append(r9)     // Catch:{ Exception -> 0x0386 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0386 }
            java.lang.String r4 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath(r4)     // Catch:{ Exception -> 0x0386 }
            r26 = r4
            goto L_0x014b
        L_0x013e:
            r29 = r10
            org.telegram.tgnet.TLRPC$PhoneCall r4 = r1.privateCall     // Catch:{ Exception -> 0x0386 }
            long r9 = r4.id     // Catch:{ Exception -> 0x0386 }
            r4 = 0
            java.lang.String r9 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath(r9, r4)     // Catch:{ Exception -> 0x0386 }
            r26 = r9
        L_0x014b:
            org.telegram.tgnet.TLRPC$PhoneCall r4 = r1.privateCall     // Catch:{ Exception -> 0x0386 }
            long r9 = r4.id     // Catch:{ Exception -> 0x0386 }
            r4 = 1
            java.lang.String r27 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath(r9, r4)     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.Instance$Config r32 = new org.telegram.messenger.voip.Instance$Config     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$PhoneCall r9 = r1.privateCall     // Catch:{ Exception -> 0x0386 }
            boolean r9 = r9.p2p_allowed     // Catch:{ Exception -> 0x0386 }
            r23 = 1
            r24 = 0
            boolean r10 = r15.enableStunMarking     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$PhoneCall r14 = r1.privateCall     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r14 = r14.protocol     // Catch:{ Exception -> 0x0386 }
            int r14 = r14.max_layer     // Catch:{ Exception -> 0x0386 }
            r28 = r14
            r14 = r32
            r42 = r15
            r15 = r29
            r17 = r12
            r20 = r9
            r25 = r10
            r14.<init>(r15, r17, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ Exception -> 0x0386 }
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x0386 }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0386 }
            java.io.File r10 = r10.getCacheDir()     // Catch:{ Exception -> 0x0386 }
            java.lang.String r14 = "voip_persistent_state.json"
            r9.<init>(r10, r14)     // Catch:{ Exception -> 0x0386 }
            java.lang.String r33 = r9.getAbsolutePath()     // Catch:{ Exception -> 0x0386 }
            java.lang.String r9 = "dbg_force_tcp_in_calls"
            r10 = 0
            boolean r9 = r0.getBoolean(r9, r10)     // Catch:{ Exception -> 0x0386 }
            if (r9 == 0) goto L_0x0195
            r10 = 3
            r50 = 3
            goto L_0x0197
        L_0x0195:
            r50 = 2
        L_0x0197:
            org.telegram.tgnet.TLRPC$PhoneCall r10 = r1.privateCall     // Catch:{ Exception -> 0x0386 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneConnection> r10 = r10.connections     // Catch:{ Exception -> 0x0386 }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.Instance$Endpoint[] r10 = new org.telegram.messenger.voip.Instance.Endpoint[r10]     // Catch:{ Exception -> 0x0386 }
            r14 = 0
        L_0x01a2:
            int r15 = r10.length     // Catch:{ Exception -> 0x0386 }
            if (r14 >= r15) goto L_0x020f
            org.telegram.tgnet.TLRPC$PhoneCall r15 = r1.privateCall     // Catch:{ Exception -> 0x020a }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneConnection> r15 = r15.connections     // Catch:{ Exception -> 0x020a }
            java.lang.Object r15 = r15.get(r14)     // Catch:{ Exception -> 0x020a }
            org.telegram.tgnet.TLRPC$PhoneConnection r15 = (org.telegram.tgnet.TLRPC.PhoneConnection) r15     // Catch:{ Exception -> 0x020a }
            org.telegram.messenger.voip.Instance$Endpoint r16 = new org.telegram.messenger.voip.Instance$Endpoint     // Catch:{ Exception -> 0x020a }
            boolean r4 = r15 instanceof org.telegram.tgnet.TLRPC.TL_phoneConnectionWebrtc     // Catch:{ Exception -> 0x020a }
            r23 = r12
            long r11 = r15.id     // Catch:{ Exception -> 0x020a }
            java.lang.String r13 = r15.ip     // Catch:{ Exception -> 0x020a }
            r20 = r2
            java.lang.String r2 = r15.ipv6     // Catch:{ Exception -> 0x020a }
            r25 = r3
            int r3 = r15.port     // Catch:{ Exception -> 0x020a }
            r28 = r5
            byte[] r5 = r15.peer_tag     // Catch:{ Exception -> 0x020a }
            r57 = r6
            boolean r6 = r15.turn     // Catch:{ Exception -> 0x020a }
            r58 = r7
            boolean r7 = r15.stun     // Catch:{ Exception -> 0x020a }
            r59 = r8
            java.lang.String r8 = r15.username     // Catch:{ Exception -> 0x020a }
            r60 = r0
            java.lang.String r0 = r15.password     // Catch:{ Exception -> 0x020a }
            boolean r1 = r15.tcp     // Catch:{ Exception -> 0x020a }
            r43 = r16
            r44 = r4
            r45 = r11
            r47 = r13
            r48 = r2
            r49 = r3
            r51 = r5
            r52 = r6
            r53 = r7
            r54 = r8
            r55 = r0
            r56 = r1
            r43.<init>(r44, r45, r47, r48, r49, r50, r51, r52, r53, r54, r55, r56)     // Catch:{ Exception -> 0x020a }
            r10[r14] = r16     // Catch:{ Exception -> 0x020a }
            int r14 = r14 + 1
            r1 = r61
            r2 = r20
            r12 = r23
            r3 = r25
            r5 = r28
            r6 = r57
            r7 = r58
            r8 = r59
            r0 = r60
            r4 = 1
            goto L_0x01a2
        L_0x020a:
            r0 = move-exception
            r1 = r61
            goto L_0x0387
        L_0x020f:
            r60 = r0
            r20 = r2
            r25 = r3
            r28 = r5
            r57 = r6
            r58 = r7
            r59 = r8
            r23 = r12
            if (r9 == 0) goto L_0x022c
            org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda5 r0 = new org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda5     // Catch:{ Exception -> 0x020a }
            r1 = r61
            r0.<init>(r1)     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x0386 }
            goto L_0x022e
        L_0x022c:
            r1 = r61
        L_0x022e:
            r0 = 0
            java.lang.String r2 = "proxy_enabled"
            r3 = r60
            r4 = 0
            boolean r2 = r3.getBoolean(r2, r4)     // Catch:{ Exception -> 0x0386 }
            if (r2 == 0) goto L_0x0276
            java.lang.String r2 = "proxy_enabled_calls"
            boolean r2 = r3.getBoolean(r2, r4)     // Catch:{ Exception -> 0x0386 }
            if (r2 == 0) goto L_0x0276
            java.lang.String r2 = "proxy_ip"
            r4 = 0
            java.lang.String r2 = r3.getString(r2, r4)     // Catch:{ Exception -> 0x0386 }
            java.lang.String r5 = "proxy_secret"
            java.lang.String r5 = r3.getString(r5, r4)     // Catch:{ Exception -> 0x0386 }
            r4 = r5
            boolean r5 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0386 }
            if (r5 != 0) goto L_0x0276
            boolean r5 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0386 }
            if (r5 == 0) goto L_0x0276
            org.telegram.messenger.voip.Instance$Proxy r5 = new org.telegram.messenger.voip.Instance$Proxy     // Catch:{ Exception -> 0x0386 }
            java.lang.String r6 = "proxy_port"
            r7 = 0
            int r6 = r3.getInt(r6, r7)     // Catch:{ Exception -> 0x0386 }
            java.lang.String r7 = "proxy_user"
            r8 = 0
            java.lang.String r7 = r3.getString(r7, r8)     // Catch:{ Exception -> 0x0386 }
            java.lang.String r12 = "proxy_pass"
            java.lang.String r8 = r3.getString(r12, r8)     // Catch:{ Exception -> 0x0386 }
            r5.<init>(r2, r6, r7, r8)     // Catch:{ Exception -> 0x0386 }
            r0 = r5
        L_0x0276:
            org.telegram.messenger.voip.Instance$EncryptionKey r2 = new org.telegram.messenger.voip.Instance$EncryptionKey     // Catch:{ Exception -> 0x0386 }
            byte[] r4 = r1.authKey     // Catch:{ Exception -> 0x0386 }
            boolean r5 = r1.isOutgoing     // Catch:{ Exception -> 0x0386 }
            r2.<init>(r4, r5)     // Catch:{ Exception -> 0x0386 }
            r37 = r2
            java.lang.String r2 = "2.7.7"
            org.telegram.tgnet.TLRPC$PhoneCall r4 = r1.privateCall     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r4 = r4.protocol     // Catch:{ Exception -> 0x0386 }
            java.util.ArrayList<java.lang.String> r4 = r4.library_versions     // Catch:{ Exception -> 0x0386 }
            r5 = 0
            java.lang.Object r4 = r4.get(r5)     // Catch:{ Exception -> 0x0386 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x0386 }
            int r2 = r2.compareTo(r4)     // Catch:{ Exception -> 0x0386 }
            if (r2 > 0) goto L_0x0298
            r14 = 1
            goto L_0x0299
        L_0x0298:
            r14 = 0
        L_0x0299:
            r2 = r14
            long[] r4 = r1.captureDevice     // Catch:{ Exception -> 0x0386 }
            r5 = 0
            r6 = r4[r5]     // Catch:{ Exception -> 0x0386 }
            r11 = 0
            int r8 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r8 == 0) goto L_0x02b4
            if (r2 != 0) goto L_0x02b4
            r6 = r4[r5]     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.NativeInstance.destroyVideoCapturer(r6)     // Catch:{ Exception -> 0x0386 }
            long[] r4 = r1.captureDevice     // Catch:{ Exception -> 0x0386 }
            r4[r5] = r11     // Catch:{ Exception -> 0x0386 }
            int[] r4 = r1.videoState     // Catch:{ Exception -> 0x0386 }
            r4[r5] = r5     // Catch:{ Exception -> 0x0386 }
        L_0x02b4:
            boolean r4 = r1.isOutgoing     // Catch:{ Exception -> 0x0386 }
            if (r4 != 0) goto L_0x02ea
            boolean r4 = r1.videoCall     // Catch:{ Exception -> 0x0386 }
            if (r4 == 0) goto L_0x02e5
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0386 }
            r5 = 23
            if (r4 < r5) goto L_0x02ca
            java.lang.String r4 = "android.permission.CAMERA"
            int r4 = r1.checkSelfPermission(r4)     // Catch:{ Exception -> 0x0386 }
            if (r4 != 0) goto L_0x02e5
        L_0x02ca:
            long[] r4 = r1.captureDevice     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r5 = r1.localSink     // Catch:{ Exception -> 0x0386 }
            r6 = 0
            r5 = r5[r6]     // Catch:{ Exception -> 0x0386 }
            boolean r6 = r1.isFrontFaceCamera     // Catch:{ Exception -> 0x0386 }
            if (r6 == 0) goto L_0x02d7
            r14 = 1
            goto L_0x02d8
        L_0x02d7:
            r14 = 0
        L_0x02d8:
            long r5 = org.telegram.messenger.voip.NativeInstance.createVideoCapturer(r5, r14)     // Catch:{ Exception -> 0x0386 }
            r7 = 0
            r4[r7] = r5     // Catch:{ Exception -> 0x0386 }
            int[] r4 = r1.videoState     // Catch:{ Exception -> 0x0386 }
            r5 = 2
            r4[r7] = r5     // Catch:{ Exception -> 0x0386 }
            goto L_0x02ea
        L_0x02e5:
            int[] r4 = r1.videoState     // Catch:{ Exception -> 0x0386 }
            r5 = 0
            r4[r5] = r5     // Catch:{ Exception -> 0x0386 }
        L_0x02ea:
            org.telegram.messenger.voip.NativeInstance[] r4 = r1.tgVoip     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$PhoneCall r5 = r1.privateCall     // Catch:{ Exception -> 0x0386 }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r5 = r5.protocol     // Catch:{ Exception -> 0x0386 }
            java.util.ArrayList<java.lang.String> r5 = r5.library_versions     // Catch:{ Exception -> 0x0386 }
            r6 = 0
            java.lang.Object r5 = r5.get(r6)     // Catch:{ Exception -> 0x0386 }
            r31 = r5
            java.lang.String r31 = (java.lang.String) r31     // Catch:{ Exception -> 0x0386 }
            int r36 = r61.getNetworkType()     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.VoIPService$ProxyVideoSink[] r5 = r1.remoteSink     // Catch:{ Exception -> 0x0386 }
            r6 = 0
            r38 = r5[r6]     // Catch:{ Exception -> 0x0386 }
            long[] r5 = r1.captureDevice     // Catch:{ Exception -> 0x0386 }
            r39 = r5[r6]     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda67 r5 = new org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda67     // Catch:{ Exception -> 0x0386 }
            r5.<init>(r1)     // Catch:{ Exception -> 0x0386 }
            r34 = r10
            r35 = r0
            r41 = r5
            org.telegram.messenger.voip.NativeInstance r5 = org.telegram.messenger.voip.Instance.makeInstance(r31, r32, r33, r34, r35, r36, r37, r38, r39, r41)     // Catch:{ Exception -> 0x0386 }
            r6 = 0
            r4[r6] = r5     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.NativeInstance[] r4 = r1.tgVoip     // Catch:{ Exception -> 0x0386 }
            r4 = r4[r6]     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda64 r5 = new org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda64     // Catch:{ Exception -> 0x0386 }
            r5.<init>(r1)     // Catch:{ Exception -> 0x0386 }
            r4.setOnStateUpdatedListener(r5)     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.NativeInstance[] r4 = r1.tgVoip     // Catch:{ Exception -> 0x0386 }
            r5 = 0
            r4 = r4[r5]     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda62 r5 = new org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda62     // Catch:{ Exception -> 0x0386 }
            r5.<init>(r1)     // Catch:{ Exception -> 0x0386 }
            r4.setOnSignalBarsUpdatedListener(r5)     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.NativeInstance[] r4 = r1.tgVoip     // Catch:{ Exception -> 0x0386 }
            r5 = 0
            r4 = r4[r5]     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda63 r5 = new org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda63     // Catch:{ Exception -> 0x0386 }
            r5.<init>(r1)     // Catch:{ Exception -> 0x0386 }
            r4.setOnSignalDataListener(r5)     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.NativeInstance[] r4 = r1.tgVoip     // Catch:{ Exception -> 0x0386 }
            r5 = 0
            r4 = r4[r5]     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda61 r5 = new org.telegram.messenger.voip.VoIPService$$ExternalSyntheticLambda61     // Catch:{ Exception -> 0x0386 }
            r5.<init>(r1)     // Catch:{ Exception -> 0x0386 }
            r4.setOnRemoteMediaStateUpdatedListener(r5)     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.NativeInstance[] r4 = r1.tgVoip     // Catch:{ Exception -> 0x0386 }
            r5 = 0
            r4 = r4[r5]     // Catch:{ Exception -> 0x0386 }
            boolean r5 = r1.micMute     // Catch:{ Exception -> 0x0386 }
            r4.setMuteMicrophone(r5)     // Catch:{ Exception -> 0x0386 }
            boolean r4 = r1.isVideoAvailable     // Catch:{ Exception -> 0x0386 }
            if (r2 == r4) goto L_0x0376
            r1.isVideoAvailable = r2     // Catch:{ Exception -> 0x0386 }
            r4 = 0
        L_0x035e:
            java.util.ArrayList<org.telegram.messenger.voip.VoIPService$StateListener> r5 = r1.stateListeners     // Catch:{ Exception -> 0x0386 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0386 }
            if (r4 >= r5) goto L_0x0376
            java.util.ArrayList<org.telegram.messenger.voip.VoIPService$StateListener> r5 = r1.stateListeners     // Catch:{ Exception -> 0x0386 }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.VoIPService$StateListener r5 = (org.telegram.messenger.voip.VoIPService.StateListener) r5     // Catch:{ Exception -> 0x0386 }
            boolean r6 = r1.isVideoAvailable     // Catch:{ Exception -> 0x0386 }
            r5.onVideoAvailableChange(r6)     // Catch:{ Exception -> 0x0386 }
            int r4 = r4 + 1
            goto L_0x035e
        L_0x0376:
            boolean[] r4 = r1.destroyCaptureDevice     // Catch:{ Exception -> 0x0386 }
            r5 = 0
            r4[r5] = r5     // Catch:{ Exception -> 0x0386 }
            org.telegram.messenger.voip.VoIPService$6 r4 = new org.telegram.messenger.voip.VoIPService$6     // Catch:{ Exception -> 0x0386 }
            r4.<init>()     // Catch:{ Exception -> 0x0386 }
            r5 = 5000(0x1388, double:2.4703E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4, r5)     // Catch:{ Exception -> 0x0386 }
            goto L_0x0393
        L_0x0386:
            r0 = move-exception
        L_0x0387:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0390
            java.lang.String r2 = "error starting call"
            org.telegram.messenger.FileLog.e((java.lang.String) r2, (java.lang.Throwable) r0)
        L_0x0390:
            r61.callFailed()
        L_0x0393:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.initiateActualEncryptedCall():void");
    }

    /* renamed from: lambda$initiateActualEncryptedCall$54$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2468x7f1b7446() {
        Toast.makeText(this, "This call uses TCP which will degrade its quality.", 0).show();
    }

    /* renamed from: lambda$initiateActualEncryptedCall$55$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2469xbbb9var_(int[] uids, float[] levels, boolean[] voice) {
        if (sharedInstance != null && this.privateCall != null) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.webRtcMicAmplitudeEvent, Float.valueOf(levels[0]));
        }
    }

    /* renamed from: lambda$initiateActualEncryptedCall$57$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2471x24fbvar_(int audioState, int videoState2) {
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda35(this, audioState, videoState2));
    }

    /* renamed from: lambda$initiateActualEncryptedCall$56$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2470x985bca48(int audioState, int videoState2) {
        this.remoteAudioState = audioState;
        this.remoteVideoState = videoState2;
        checkIsNear();
        for (int a = 0; a < this.stateListeners.size(); a++) {
            this.stateListeners.get(a).onMediaStateUpdated(audioState, videoState2);
        }
    }

    /* renamed from: lambda$playConnectedSound$58$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2484x5248ab71() {
        this.soundPool.play(this.spVoiceChatStartId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void playConnectedSound() {
        Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda17(this));
        this.playedConnectedSound = true;
    }

    private void startConnectingSound() {
        Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda21(this));
    }

    /* renamed from: lambda$startConnectingSound$59$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2490x630e1637() {
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
                        Utilities.globalQueue.postRunnable(new VoIPService$7$$ExternalSyntheticLambda0(this));
                    }
                }

                /* renamed from: lambda$run$0$org-telegram-messenger-voip-VoIPService$7  reason: not valid java name */
                public /* synthetic */ void m2519lambda$run$0$orgtelegrammessengervoipVoIPService$7() {
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

    public void onSignalingData(byte[] data) {
        if (this.privateCall != null) {
            TLRPC.TL_phone_sendSignalingData req = new TLRPC.TL_phone_sendSignalingData();
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.access_hash = this.privateCall.access_hash;
            req.peer.id = this.privateCall.id;
            req.data = data;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, VoIPService$$ExternalSyntheticLambda95.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$onSignalingData$60(TLObject response, TLRPC.TL_error error) {
    }

    public boolean isVideoAvailable() {
        return this.isVideoAvailable;
    }

    /* access modifiers changed from: package-private */
    public void onMediaButtonEvent(KeyEvent ev) {
        if (ev != null) {
            if ((ev.getKeyCode() != 79 && ev.getKeyCode() != 127 && ev.getKeyCode() != 85) || ev.getAction() != 1) {
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
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            os.write(this.authKey);
            os.write(this.g_a);
        } catch (IOException e) {
        }
        return EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(os.toByteArray(), 0, (long) os.size()));
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
                int earpieceFlag = AudioManager.class.getField("DEVICE_OUT_EARPIECE").getInt((Object) null);
                if ((((Integer) method.invoke((AudioManager) getSystemService("audio"), new Object[]{0})).intValue() & earpieceFlag) == earpieceFlag) {
                    this.mHasEarpiece = Boolean.TRUE;
                } else {
                    this.mHasEarpiece = Boolean.FALSE;
                }
            } catch (Throwable error) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error while checking earpiece! ", error);
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
    public void setSwitchingCamera(boolean switching, boolean isFrontFace) {
        this.switchingCamera = switching;
        if (!switching) {
            this.isFrontFaceCamera = isFrontFace;
            for (int a = 0; a < this.stateListeners.size(); a++) {
                this.stateListeners.get(a).onCameraSwitch(this.isFrontFaceCamera);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCameraFirstFrameAvailable() {
        for (int a = 0; a < this.stateListeners.size(); a++) {
            this.stateListeners.get(a).onCameraFirstFrameAvailable();
        }
    }

    public void registerStateListener(StateListener l) {
        if (!this.stateListeners.contains(l)) {
            this.stateListeners.add(l);
            int i = this.currentState;
            if (i != 0) {
                l.onStateChanged(i);
            }
            int i2 = this.signalBarCount;
            if (i2 != 0) {
                l.onSignalBarsCountChanged(i2);
            }
        }
    }

    public void unregisterStateListener(StateListener l) {
        this.stateListeners.remove(l);
    }

    public void editCallMember(TLObject object, Boolean mute, Boolean muteVideo, Integer volume, Boolean raiseHand, Runnable onComplete) {
        TLRPC.InputPeer inputPeer;
        if (object != null && this.groupCall != null) {
            TLRPC.TL_phone_editGroupCallParticipant req = new TLRPC.TL_phone_editGroupCallParticipant();
            req.call = this.groupCall.getInputGroupCall();
            if (object instanceof TLRPC.User) {
                TLRPC.User user2 = (TLRPC.User) object;
                if (!UserObject.isUserSelf(user2) || (inputPeer = this.groupCallPeer) == null) {
                    req.participant = MessagesController.getInputPeer(user2);
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("edit group call part id = " + req.participant.user_id + " access_hash = " + req.participant.user_id);
                    }
                } else {
                    req.participant = inputPeer;
                }
            } else if (object instanceof TLRPC.Chat) {
                req.participant = MessagesController.getInputPeer((TLRPC.Chat) object);
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("edit group call part id = ");
                    sb.append(req.participant.chat_id != 0 ? req.participant.chat_id : req.participant.channel_id);
                    sb.append(" access_hash = ");
                    sb.append(req.participant.access_hash);
                    FileLog.d(sb.toString());
                }
            }
            if (mute != null) {
                req.muted = mute.booleanValue();
                req.flags |= 1;
            }
            if (volume != null) {
                req.volume = volume.intValue();
                req.flags |= 2;
            }
            if (raiseHand != null) {
                req.raise_hand = raiseHand.booleanValue();
                req.flags |= 4;
            }
            if (muteVideo != null) {
                req.video_stopped = muteVideo.booleanValue();
                req.flags |= 8;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("edit group call flags = " + req.flags);
            }
            int account = this.currentAccount;
            AccountInstance.getInstance(account).getConnectionsManager().sendRequest(req, new VoIPService$$ExternalSyntheticLambda85(this, account, onComplete));
        }
    }

    /* renamed from: lambda$editCallMember$61$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2463lambda$editCallMember$61$orgtelegrammessengervoipVoIPService(int account, Runnable onComplete, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AccountInstance.getInstance(account).getMessagesController().processUpdates((TLRPC.Updates) response, false);
        } else if (error != null && "GROUPCALL_VIDEO_TOO_MUCH".equals(error.text)) {
            this.groupCall.reloadGroupCall();
        }
        if (onComplete != null) {
            AndroidUtilities.runOnUIThread(onComplete);
        }
    }

    public boolean isMicMute() {
        return this.micMute;
    }

    public void toggleSpeakerphoneOrShowRouteSheet(Context context, boolean fromOverlayWindow) {
        CallConnection callConnection;
        String str;
        int i;
        int i2 = 2;
        if (!isBluetoothHeadsetConnected() || !hasEarpiece()) {
            boolean z = USE_CONNECTION_SERVICE;
            if (z && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
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
            } else if (!this.audioConfigured || z) {
                this.speakerphoneStateToSet = !this.speakerphoneStateToSet;
            } else {
                AudioManager am = (AudioManager) getSystemService("audio");
                if (hasEarpiece()) {
                    am.setSpeakerphoneOn(!am.isSpeakerphoneOn());
                } else {
                    am.setBluetoothScoOn(!am.isBluetoothScoOn());
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
        BottomSheet.Builder builder = title.setItems(charSequenceArr, iArr, new VoIPService$$ExternalSyntheticLambda0(this));
        BottomSheet bottomSheet = builder.create();
        if (fromOverlayWindow) {
            if (Build.VERSION.SDK_INT >= 26) {
                bottomSheet.getWindow().setType(2038);
            } else {
                bottomSheet.getWindow().setType(2003);
            }
        }
        builder.show();
    }

    /* renamed from: lambda$toggleSpeakerphoneOrShowRouteSheet$62$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2513x6676e26b(DialogInterface dialog, int which) {
        if (getSharedInstance() != null) {
            setAudioOutput(which);
        }
    }

    public void setAudioOutput(int which) {
        CallConnection callConnection;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("setAudioOutput " + which);
        }
        AudioManager am = (AudioManager) getSystemService("audio");
        boolean z = USE_CONNECTION_SERVICE;
        if (z && (callConnection = this.systemCallConnection) != null) {
            switch (which) {
                case 0:
                    callConnection.setAudioRoute(8);
                    break;
                case 1:
                    callConnection.setAudioRoute(5);
                    break;
                case 2:
                    callConnection.setAudioRoute(2);
                    break;
            }
        } else if (!this.audioConfigured || z) {
            switch (which) {
                case 0:
                    this.audioRouteToSet = 1;
                    this.speakerphoneStateToSet = true;
                    break;
                case 1:
                    this.audioRouteToSet = 0;
                    this.speakerphoneStateToSet = false;
                    break;
                case 2:
                    this.audioRouteToSet = 2;
                    this.speakerphoneStateToSet = false;
                    break;
            }
        } else {
            switch (which) {
                case 0:
                    this.needSwitchToBluetoothAfterScoActivates = false;
                    if (this.bluetoothScoActive || this.bluetoothScoConnecting) {
                        am.stopBluetoothSco();
                        this.bluetoothScoActive = false;
                        this.bluetoothScoConnecting = false;
                    }
                    am.setBluetoothScoOn(false);
                    am.setSpeakerphoneOn(true);
                    this.audioRouteToSet = 1;
                    break;
                case 1:
                    this.needSwitchToBluetoothAfterScoActivates = false;
                    if (this.bluetoothScoActive || this.bluetoothScoConnecting) {
                        am.stopBluetoothSco();
                        this.bluetoothScoActive = false;
                        this.bluetoothScoConnecting = false;
                    }
                    am.setSpeakerphoneOn(false);
                    am.setBluetoothScoOn(false);
                    this.audioRouteToSet = 0;
                    break;
                case 2:
                    if (!this.bluetoothScoActive) {
                        this.needSwitchToBluetoothAfterScoActivates = true;
                        try {
                            am.startBluetoothSco();
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    } else {
                        am.setBluetoothScoOn(true);
                        am.setSpeakerphoneOn(false);
                    }
                    this.audioRouteToSet = 2;
                    break;
            }
            updateOutputGainControlState();
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
            AudioManager am = (AudioManager) getSystemService("audio");
            return hasEarpiece() ? am.isSpeakerphoneOn() : am.isBluetoothScoOn();
        }
    }

    public int getCurrentAudioRoute() {
        if (USE_CONNECTION_SERVICE) {
            CallConnection callConnection = this.systemCallConnection;
            if (!(callConnection == null || callConnection.getCallAudioState() == null)) {
                switch (this.systemCallConnection.getCallAudioState().getRoute()) {
                    case 1:
                    case 4:
                        return 0;
                    case 2:
                        return 2;
                    case 8:
                        return 1;
                }
            }
            return this.audioRouteToSet;
        } else if (!this.audioConfigured) {
            return this.audioRouteToSet;
        } else {
            AudioManager am = (AudioManager) getSystemService("audio");
            if (am.isBluetoothScoOn()) {
                return 2;
            }
            return am.isSpeakerphoneOn() ? 1 : 0;
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

    private void showNotification(String name, Bitmap photo) {
        String str;
        int i;
        String str2;
        int i2;
        Intent intent = new Intent(this, LaunchActivity.class).setAction(this.groupCall != null ? "voip_chat" : "voip");
        if (this.groupCall != null) {
            intent.putExtra("currentAccount", this.currentAccount);
        }
        Notification.Builder builder = new Notification.Builder(this).setContentText(name).setContentIntent(PendingIntent.getActivity(this, 50, intent, 0));
        if (this.groupCall != null) {
            if (ChatObject.isChannelOrGiga(this.chat)) {
                i2 = NUM;
                str2 = "VoipLiveStream";
            } else {
                i2 = NUM;
                str2 = "VoipVoiceChat";
            }
            builder.setContentTitle(LocaleController.getString(str2, i2));
            builder.setSmallIcon(isMicMute() ? NUM : NUM);
        } else {
            builder.setContentTitle(LocaleController.getString("VoipOutgoingCall", NUM));
            builder.setSmallIcon(NUM);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            Intent endIntent = new Intent(this, VoIPActionsReceiver.class);
            endIntent.setAction(getPackageName() + ".END_CALL");
            if (this.groupCall != null) {
                if (ChatObject.isChannelOrGiga(this.chat)) {
                    i = NUM;
                    str = "VoipChannelLeaveAlertTitle";
                } else {
                    i = NUM;
                    str = "VoipGroupLeaveAlertTitle";
                }
                builder.addAction(NUM, LocaleController.getString(str, i), PendingIntent.getBroadcast(this, 0, endIntent, NUM));
            } else {
                builder.addAction(NUM, LocaleController.getString("VoipEndCall", NUM), PendingIntent.getBroadcast(this, 0, endIntent, NUM));
            }
            builder.setPriority(2);
        }
        if (Build.VERSION.SDK_INT >= 17) {
            builder.setShowWhen(false);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            builder.setColor(-14143951);
            builder.setColorized(true);
        } else if (Build.VERSION.SDK_INT >= 21) {
            builder.setColor(-13851168);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
        }
        if (photo != null) {
            builder.setLargeIcon(photo);
        }
        try {
            startForeground(201, builder.getNotification());
        } catch (Exception e) {
            if (photo != null && (e instanceof IllegalArgumentException)) {
                showNotification(name, (Bitmap) null);
            }
        }
    }

    private void startRingtoneAndVibration(long chatID) {
        int vibrate;
        String notificationUri;
        SharedPreferences prefs = MessagesController.getNotificationsSettings(this.currentAccount);
        AudioManager am = (AudioManager) getSystemService("audio");
        if (am.getRingerMode() != 0) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.ringtonePlayer = mediaPlayer;
            mediaPlayer.setOnPreparedListener(new VoIPService$$ExternalSyntheticLambda11(this));
            this.ringtonePlayer.setLooping(true);
            if (this.isHeadsetPlugged) {
                this.ringtonePlayer.setAudioStreamType(0);
            } else {
                this.ringtonePlayer.setAudioStreamType(2);
                if (!USE_CONNECTION_SERVICE) {
                    am.requestAudioFocus(this, 2, 1);
                }
            }
            try {
                if (prefs.getBoolean("custom_" + chatID, false)) {
                    notificationUri = prefs.getString("ringtone_path_" + chatID, RingtoneManager.getDefaultUri(1).toString());
                } else {
                    notificationUri = prefs.getString("CallsRingtonePath", RingtoneManager.getDefaultUri(1).toString());
                }
                this.ringtonePlayer.setDataSource(this, Uri.parse(notificationUri));
                this.ringtonePlayer.prepareAsync();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                MediaPlayer mediaPlayer2 = this.ringtonePlayer;
                if (mediaPlayer2 != null) {
                    mediaPlayer2.release();
                    this.ringtonePlayer = null;
                }
            }
            if (prefs.getBoolean("custom_" + chatID, false)) {
                vibrate = prefs.getInt("calls_vibrate_" + chatID, 0);
            } else {
                vibrate = prefs.getInt("vibrate_calls", 0);
            }
            if ((vibrate != 2 && vibrate != 4 && (am.getRingerMode() == 1 || am.getRingerMode() == 2)) || (vibrate == 4 && am.getRingerMode() == 1)) {
                Vibrator vibrator2 = (Vibrator) getSystemService("vibrator");
                this.vibrator = vibrator2;
                long duration = 700;
                if (vibrate == 1) {
                    duration = 700 / 2;
                } else if (vibrate == 3) {
                    duration = 700 * 2;
                }
                vibrator2.vibrate(new long[]{0, duration, 500}, 0);
            }
        }
    }

    /* renamed from: lambda$startRingtoneAndVibration$63$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2507xd4dCLASSNAME(MediaPlayer mediaPlayer) {
        try {
            this.ringtonePlayer.start();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

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
        SensorManager sm = (SensorManager) getSystemService("sensor");
        if (sm.getDefaultSensor(8) != null) {
            sm.unregisterListener(this);
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
        AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda57.INSTANCE);
        if (this.tgVoip[0] != null) {
            StatsController.getInstance(this.currentAccount).incrementTotalCallsTime(getStatsNetworkType(), ((int) (getCallDuration() / 1000)) % 5);
            onTgVoipPreStop();
            if (this.tgVoip[0].isGroup()) {
                NativeInstance instance = this.tgVoip[0];
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                instance.getClass();
                dispatchQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda44(instance));
                for (Map.Entry<String, Integer> entry : this.currentStreamRequestTimestamp.entrySet()) {
                    AccountInstance.getInstance(this.currentAccount).getConnectionsManager().cancelRequest(entry.getValue().intValue(), true);
                }
                this.currentStreamRequestTimestamp.clear();
            } else {
                Instance.FinalState state = this.tgVoip[0].stop();
                updateTrafficStats(this.tgVoip[0], state.trafficStats);
                onTgVoipStop(state);
            }
            this.prevTrafficStats = null;
            this.callStartTime = 0;
            this.tgVoip[0] = null;
            Instance.destroyInstance();
        }
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        if (nativeInstanceArr[1] != null) {
            NativeInstance instance2 = nativeInstanceArr[1];
            DispatchQueue dispatchQueue2 = Utilities.globalQueue;
            instance2.getClass();
            dispatchQueue2.postRunnable(new VoIPService$$ExternalSyntheticLambda44(instance2));
            this.tgVoip[1] = null;
        }
        int a = 0;
        while (true) {
            long[] jArr = this.captureDevice;
            if (a >= jArr.length) {
                break;
            }
            if (jArr[a] != 0) {
                if (this.destroyCaptureDevice[a]) {
                    NativeInstance.destroyVideoCapturer(jArr[a]);
                }
                this.captureDevice[a] = 0;
            }
            a++;
        }
        this.cpuWakelock.release();
        if (!this.playingSound) {
            AudioManager am = (AudioManager) getSystemService("audio");
            if (!USE_CONNECTION_SERVICE) {
                if (this.isBtHeadsetConnected || this.bluetoothScoActive || this.bluetoothScoConnecting) {
                    am.stopBluetoothSco();
                    am.setBluetoothScoOn(false);
                    am.setSpeakerphoneOn(false);
                    this.bluetoothScoActive = false;
                    this.bluetoothScoConnecting = false;
                }
                if (this.onDestroyRunnable == null) {
                    DispatchQueue dispatchQueue3 = Utilities.globalQueue;
                    VoIPService$$ExternalSyntheticLambda22 voIPService$$ExternalSyntheticLambda22 = new VoIPService$$ExternalSyntheticLambda22(am);
                    setModeRunnable = voIPService$$ExternalSyntheticLambda22;
                    dispatchQueue3.postRunnable(voIPService$$ExternalSyntheticLambda22);
                }
                am.abandonAudioFocus(this);
            }
            am.unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (this.hasAudioFocus) {
                am.abandonAudioFocus(this);
            }
            Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda13(this));
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
        setSinks((VideoSink) null, (VideoSink) null);
        Runnable runnable3 = this.onDestroyRunnable;
        if (runnable3 != null) {
            runnable3.run();
        }
        int i = this.currentAccount;
        if (i >= 0) {
            ConnectionsManager.getInstance(i).setAppPaused(true, false);
            if (ChatObject.isChannel(this.chat)) {
                MessagesController.getInstance(this.currentAccount).startShortPoll(this.chat, this.classGuid, true);
            }
        }
    }

    static /* synthetic */ void lambda$onDestroy$65(AudioManager am) {
        synchronized (sync) {
            if (setModeRunnable != null) {
                setModeRunnable = null;
                try {
                    am.setMode(0);
                } catch (SecurityException x) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Error setting audio more to normal", (Throwable) x);
                    }
                }
            }
        }
    }

    /* renamed from: lambda$onDestroy$66$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2479lambda$onDestroy$66$orgtelegrammessengervoipVoIPService() {
        this.soundPool.release();
    }

    public long getCallID() {
        TLRPC.PhoneCall phoneCall = this.privateCall;
        if (phoneCall != null) {
            return phoneCall.id;
        }
        return 0;
    }

    public void hangUp() {
        hangUp(0, (Runnable) null);
    }

    public void hangUp(int discard) {
        hangUp(discard, (Runnable) null);
    }

    public void hangUp(Runnable onDone) {
        hangUp(0, onDone);
    }

    public void acceptIncomingCall() {
        MessagesController.getInstance(this.currentAccount).ignoreSetOnline = false;
        stopRinging();
        showNotification();
        configureDeviceForCall();
        startConnectingSound();
        dispatchStateChanged(12);
        AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda54.INSTANCE);
        MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
        req.random_length = 256;
        req.version = messagesStorage.getLastSecretVersion();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda87(this, messagesStorage));
    }

    /* renamed from: lambda$acceptIncomingCall$70$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2436x4CLASSNAME(MessagesStorage messagesStorage, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) response;
            if (response instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(res.p, res.g)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("stopping VoIP service, bad prime");
                    }
                    callFailed();
                    return;
                }
                messagesStorage.setSecretPBytes(res.p);
                messagesStorage.setSecretG(res.g);
                messagesStorage.setLastSecretVersion(res.version);
                MessagesStorage.getInstance(this.currentAccount).saveSecretParams(messagesStorage.getLastSecretVersion(), messagesStorage.getSecretG(), messagesStorage.getSecretPBytes());
            }
            byte[] salt = new byte[256];
            for (int a = 0; a < 256; a++) {
                salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
            }
            if (this.privateCall == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("call is null");
                }
                callFailed();
                return;
            }
            this.a_or_b = salt;
            BigInteger g_b = BigInteger.valueOf((long) messagesStorage.getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, messagesStorage.getSecretPBytes()));
            this.g_a_hash = this.privateCall.g_a_hash;
            byte[] g_b_bytes = g_b.toByteArray();
            if (g_b_bytes.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                g_b_bytes = correctedAuth;
            }
            TLRPC.TL_phone_acceptCall req1 = new TLRPC.TL_phone_acceptCall();
            req1.g_b = g_b_bytes;
            req1.peer = new TLRPC.TL_inputPhoneCall();
            req1.peer.id = this.privateCall.id;
            req1.peer.access_hash = this.privateCall.access_hash;
            req1.protocol = new TLRPC.TL_phoneCallProtocol();
            TLRPC.TL_phoneCallProtocol tL_phoneCallProtocol = req1.protocol;
            req1.protocol.udp_reflector = true;
            tL_phoneCallProtocol.udp_p2p = true;
            req1.protocol.min_layer = 65;
            req1.protocol.max_layer = Instance.getConnectionMaxLayer();
            req1.protocol.library_versions.addAll(Instance.AVAILABLE_VERSIONS);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new VoIPService$$ExternalSyntheticLambda75(this), 2);
            return;
        }
        callFailed();
    }

    /* renamed from: lambda$acceptIncomingCall$69$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2435x369d975f(TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda47(this, error1, response1));
    }

    /* renamed from: lambda$acceptIncomingCall$68$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2434xa9fd6c5e(TLRPC.TL_error error1, TLObject response1) {
        if (error1 == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("accept call ok! " + response1);
            }
            TLRPC.PhoneCall phoneCall = ((TLRPC.TL_phone_phoneCall) response1).phone_call;
            this.privateCall = phoneCall;
            if (phoneCall instanceof TLRPC.TL_phoneCallDiscarded) {
                onCallUpdated(phoneCall);
                return;
            }
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("Error on phone.acceptCall: " + error1);
        }
        callFailed();
    }

    public void declineIncomingCall(int reason, Runnable onDone) {
        stopRinging();
        this.callDiscardReason = reason;
        int i = this.currentState;
        if (i == 14) {
            Runnable runnable = this.delayedStartOutgoingCall;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                callEnded();
                return;
            }
            dispatchStateChanged(10);
            this.endCallAfterRequest = true;
            AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda2(this), 5000);
        } else if (i != 10 && i != 11) {
            dispatchStateChanged(10);
            if (this.privateCall == null) {
                this.onDestroyRunnable = onDone;
                callEnded();
                if (this.callReqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.callReqId, false);
                    this.callReqId = 0;
                    return;
                }
                return;
            }
            TLRPC.TL_phone_discardCall req = new TLRPC.TL_phone_discardCall();
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.access_hash = this.privateCall.access_hash;
            req.peer.id = this.privateCall.id;
            req.duration = (int) (getCallDuration() / 1000);
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            req.connection_id = nativeInstanceArr[0] != null ? nativeInstanceArr[0].getPreferredRelayId() : 0;
            switch (reason) {
                case 2:
                    req.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
                    break;
                case 3:
                    req.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
                    break;
                case 4:
                    req.reason = new TLRPC.TL_phoneCallDiscardReasonBusy();
                    break;
                default:
                    req.reason = new TLRPC.TL_phoneCallDiscardReasonHangup();
                    break;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new VoIPService$$ExternalSyntheticLambda76(this), 2);
            this.onDestroyRunnable = onDone;
            callEnded();
        }
    }

    /* renamed from: lambda$declineIncomingCall$71$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2461x29148762() {
        if (this.currentState == 10) {
            callEnded();
        }
    }

    /* renamed from: lambda$declineIncomingCall$72$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2462xb5b4b263(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            if (response instanceof TLRPC.TL_updates) {
                MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.TL_updates) response, false);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("phone.discardCall " + response);
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.e("error on phone.discardCall: " + error);
        }
    }

    public void declineIncomingCall() {
        declineIncomingCall(1, (Runnable) null);
    }

    private Class<? extends Activity> getUIActivityClass() {
        return LaunchActivity.class;
    }

    public CallConnection getConnectionAndStartCall() {
        if (this.systemCallConnection == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("creating call connection");
            }
            CallConnection callConnection = new CallConnection();
            this.systemCallConnection = callConnection;
            callConnection.setInitializing();
            if (this.isOutgoing) {
                VoIPService$$ExternalSyntheticLambda4 voIPService$$ExternalSyntheticLambda4 = new VoIPService$$ExternalSyntheticLambda4(this);
                this.delayedStartOutgoingCall = voIPService$$ExternalSyntheticLambda4;
                AndroidUtilities.runOnUIThread(voIPService$$ExternalSyntheticLambda4, 2000);
            }
            CallConnection callConnection2 = this.systemCallConnection;
            callConnection2.setAddress(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), 1);
            this.systemCallConnection.setCallerDisplayName(ContactsController.formatName(this.user.first_name, this.user.last_name), 1);
        }
        return this.systemCallConnection;
    }

    /* renamed from: lambda$getConnectionAndStartCall$73$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2465xa7dddac1() {
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
                } catch (Exception x) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Error starting incall activity", (Throwable) x);
                    }
                }
            } else {
                showIncomingNotification(ContactsController.formatName(this.user.first_name, this.user.last_name), (CharSequence) null, this.user, this.privateCall.video, 0);
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
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        Instance.setGlobalServerConfig(preferences.getString("voip_server_config", "{}"));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_phone_getCallConfig(), new VoIPService$$ExternalSyntheticLambda74(preferences));
    }

    static /* synthetic */ void lambda$updateServerConfig$74(SharedPreferences preferences, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            String data = ((TLRPC.TL_dataJSON) response).data;
            Instance.setGlobalServerConfig(data);
            preferences.edit().putString("voip_server_config", data).commit();
        }
    }

    private void showNotification() {
        TLRPC.User user2 = this.user;
        if (user2 != null) {
            showNotification(ContactsController.formatName(user2.first_name, this.user.last_name), getRoundAvatarBitmap(this.user));
        } else {
            showNotification(this.chat.title, getRoundAvatarBitmap(this.chat));
        }
    }

    private void onTgVoipPreStop() {
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String readLine = reader.readLine();
            String line = readLine;
            if (readLine != null) {
                sb.append(line);
                sb.append("\n");
            } else {
                reader.close();
                return sb.toString();
            }
        }
    }

    public static String getStringFromFile(String filePath) throws Exception {
        FileInputStream fin = new FileInputStream(new File(filePath));
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    private void onTgVoipStop(Instance.FinalState finalState) {
        if (this.user != null) {
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
            if (this.needSendDebugLog && finalState.debugLog != null) {
                TLRPC.TL_phone_saveCallDebug req = new TLRPC.TL_phone_saveCallDebug();
                req.debug = new TLRPC.TL_dataJSON();
                req.debug.data = finalState.debugLog;
                req.peer = new TLRPC.TL_inputPhoneCall();
                req.peer.access_hash = this.privateCall.access_hash;
                req.peer.id = this.privateCall.id;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, VoIPService$$ExternalSyntheticLambda96.INSTANCE);
                this.needSendDebugLog = false;
            }
        }
    }

    static /* synthetic */ void lambda$onTgVoipStop$75(TLObject response, TLRPC.TL_error error) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Sent debug logs, response = " + response);
        }
    }

    private void initializeAccountRelatedThings() {
        updateServerConfig();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
    }

    public void onCreate() {
        BluetoothAdapter bluetoothAdapter;
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STARTING ===============");
        }
        try {
            AudioManager am = (AudioManager) getSystemService("audio");
            if (Build.VERSION.SDK_INT < 17 || am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") == null) {
                Instance.setBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
            } else {
                Instance.setBufferSize(Integer.parseInt(am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")));
            }
            boolean z = true;
            PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "telegram-voip");
            this.cpuWakelock = newWakeLock;
            newWakeLock.acquire();
            this.btAdapter = am.isBluetoothScoAvailableOffCall() ? BluetoothAdapter.getDefaultAdapter() : null;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            boolean z2 = USE_CONNECTION_SERVICE;
            if (!z2) {
                filter.addAction("android.intent.action.HEADSET_PLUG");
                if (this.btAdapter != null) {
                    filter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                    filter.addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
                }
                filter.addAction("android.intent.action.PHONE_STATE");
                filter.addAction("android.intent.action.SCREEN_ON");
                filter.addAction("android.intent.action.SCREEN_OFF");
            }
            registerReceiver(this.receiver, filter);
            fetchBluetoothDeviceName();
            am.registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (!z2 && (bluetoothAdapter = this.btAdapter) != null && bluetoothAdapter.isEnabled()) {
                try {
                    MediaRouter mr = (MediaRouter) getSystemService("media_router");
                    if (Build.VERSION.SDK_INT < 24) {
                        if (this.btAdapter.getProfileConnectionState(1) != 2) {
                            z = false;
                        }
                        updateBluetoothHeadsetState(z);
                        Iterator<StateListener> it = this.stateListeners.iterator();
                        while (it.hasNext()) {
                            it.next().onAudioSettingsChanged();
                        }
                    } else if (mr.getSelectedRoute(1).getDeviceType() == 3) {
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
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error initializing voip controller", (Throwable) x);
            }
            callFailed();
        }
        if (callIShouldHavePutIntoIntent != null && Build.VERSION.SDK_INT >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            Notification.Builder bldr = new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(LocaleController.getString("VoipOutgoingCall", NUM)).setShowWhen(false);
            if (this.groupCall != null) {
                bldr.setSmallIcon(isMicMute() ? NUM : NUM);
            } else {
                bldr.setSmallIcon(NUM);
            }
            startForeground(201, bldr.build());
        }
    }

    private void loadResources() {
        if (Build.VERSION.SDK_INT >= 21) {
            WebRtcAudioTrack.setAudioTrackUsageAttribute(2);
        }
        Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda6(this));
    }

    /* renamed from: lambda$loadResources$76$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2472lambda$loadResources$76$orgtelegrammessengervoipVoIPService() {
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

    private void dispatchStateChanged(int state) {
        CallConnection callConnection;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("== Call " + getCallID() + " state changed to " + state + " ==");
        }
        this.currentState = state;
        if (USE_CONNECTION_SERVICE && state == 3 && (callConnection = this.systemCallConnection) != null) {
            callConnection.setActive();
        }
        for (int a = 0; a < this.stateListeners.size(); a++) {
            this.stateListeners.get(a).onStateChanged(state);
        }
    }

    /* access modifiers changed from: private */
    public void updateTrafficStats(NativeInstance instance, Instance.TrafficStats trafficStats) {
        Instance.TrafficStats trafficStats2;
        if (trafficStats == null) {
            trafficStats2 = instance.getTrafficStats();
        } else {
            trafficStats2 = trafficStats;
        }
        long j = trafficStats2.bytesSentWifi;
        Instance.TrafficStats trafficStats3 = this.prevTrafficStats;
        long wifiSentDiff = j - (trafficStats3 != null ? trafficStats3.bytesSentWifi : 0);
        long j2 = trafficStats2.bytesReceivedWifi;
        Instance.TrafficStats trafficStats4 = this.prevTrafficStats;
        long wifiRecvdDiff = j2 - (trafficStats4 != null ? trafficStats4.bytesReceivedWifi : 0);
        long j3 = trafficStats2.bytesSentMobile;
        Instance.TrafficStats trafficStats5 = this.prevTrafficStats;
        long mobileSentDiff = j3 - (trafficStats5 != null ? trafficStats5.bytesSentMobile : 0);
        long j4 = trafficStats2.bytesReceivedMobile;
        Instance.TrafficStats trafficStats6 = this.prevTrafficStats;
        long mobileRecvdDiff = j4 - (trafficStats6 != null ? trafficStats6.bytesReceivedMobile : 0);
        this.prevTrafficStats = trafficStats2;
        if (wifiSentDiff > 0) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(1, 0, wifiSentDiff);
        }
        if (wifiRecvdDiff > 0) {
            StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(1, 0, wifiRecvdDiff);
        }
        int i = 2;
        if (mobileSentDiff > 0) {
            StatsController instance2 = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo = this.lastNetInfo;
            instance2.incrementSentBytesCount((networkInfo == null || !networkInfo.isRoaming()) ? 0 : 2, 0, mobileSentDiff);
        }
        if (mobileRecvdDiff > 0) {
            StatsController instance3 = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo2 = this.lastNetInfo;
            if (networkInfo2 == null || !networkInfo2.isRoaming()) {
                i = 0;
            }
            instance3.incrementReceivedBytesCount(i, 0, mobileRecvdDiff);
        }
    }

    private void configureDeviceForCall() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("configureDeviceForCall, route to set = " + this.audioRouteToSet);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            WebRtcAudioTrack.setAudioTrackUsageAttribute(hasRtmpStream() ? 1 : 2);
            WebRtcAudioTrack.setAudioStreamType(hasRtmpStream() ? Integer.MIN_VALUE : 0);
        }
        this.needPlayEndSound = true;
        AudioManager am = (AudioManager) getSystemService("audio");
        if (!USE_CONNECTION_SERVICE) {
            Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda38(this, am));
        }
        SensorManager sm = (SensorManager) getSystemService("sensor");
        Sensor proximity = sm.getDefaultSensor(8);
        if (proximity != null) {
            try {
                this.proximityWakelock = ((PowerManager) getSystemService("power")).newWakeLock(32, "telegram-voip-prx");
                sm.registerListener(this, proximity, 3);
            } catch (Exception x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error initializing proximity sensor", (Throwable) x);
                }
            }
        }
    }

    /* renamed from: lambda$configureDeviceForCall$79$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2446x462c7c1f(AudioManager am) {
        try {
            if (hasRtmpStream()) {
                am.setMode(0);
                am.setBluetoothScoOn(false);
                AndroidUtilities.runOnUIThread(VoIPService$$ExternalSyntheticLambda56.INSTANCE);
                return;
            }
            am.setMode(3);
            AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda37(this, am));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ void lambda$configureDeviceForCall$77() {
        if (!MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().m104lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
        }
    }

    /* renamed from: lambda$configureDeviceForCall$78$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2445xb98CLASSNAMEe(AudioManager am) {
        am.requestAudioFocus(this, 0, 1);
        if (isBluetoothHeadsetConnected() && hasEarpiece()) {
            switch (this.audioRouteToSet) {
                case 0:
                    am.setBluetoothScoOn(false);
                    am.setSpeakerphoneOn(false);
                    break;
                case 1:
                    am.setBluetoothScoOn(false);
                    am.setSpeakerphoneOn(true);
                    break;
                case 2:
                    if (this.bluetoothScoActive) {
                        am.setBluetoothScoOn(true);
                        am.setSpeakerphoneOn(false);
                        break;
                    } else {
                        this.needSwitchToBluetoothAfterScoActivates = true;
                        try {
                            am.startBluetoothSco();
                            break;
                        } catch (Throwable e) {
                            FileLog.e(e);
                            break;
                        }
                    }
            }
        } else if (isBluetoothHeadsetConnected()) {
            am.setBluetoothScoOn(this.speakerphoneStateToSet);
        } else {
            am.setSpeakerphoneOn(this.speakerphoneStateToSet);
            if (this.speakerphoneStateToSet) {
                this.audioRouteToSet = 1;
            } else {
                this.audioRouteToSet = 0;
            }
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
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (!this.unmutedByHold && this.remoteVideoState != 2) {
            boolean newIsNear = false;
            if (this.videoState[0] != 2 && event.sensor.getType() == 8) {
                AudioManager am = (AudioManager) getSystemService("audio");
                if (this.audioRouteToSet == 0 && !this.isHeadsetPlugged && !am.isSpeakerphoneOn()) {
                    if (!isBluetoothHeadsetConnected() || !am.isBluetoothScoOn()) {
                        if (event.values[0] < Math.min(event.sensor.getMaximumRange(), 3.0f)) {
                            newIsNear = true;
                        }
                        checkIsNear(newIsNear);
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

    private void checkIsNear(boolean newIsNear) {
        if (newIsNear != this.isProximityNear) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("proximity " + newIsNear);
            }
            this.isProximityNear = newIsNear;
            if (newIsNear) {
                try {
                    this.proximityWakelock.acquire();
                } catch (Exception x) {
                    FileLog.e((Throwable) x);
                }
            } else {
                this.proximityWakelock.release(1);
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public boolean isBluetoothHeadsetConnected() {
        CallConnection callConnection;
        if (!USE_CONNECTION_SERVICE || (callConnection = this.systemCallConnection) == null || callConnection.getCallAudioState() == null) {
            return this.isBtHeadsetConnected;
        }
        return (this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 2) != 0;
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == 1) {
            this.hasAudioFocus = true;
        } else {
            this.hasAudioFocus = false;
        }
    }

    /* access modifiers changed from: private */
    public void updateBluetoothHeadsetState(boolean connected) {
        if (connected != this.isBtHeadsetConnected) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("updateBluetoothHeadsetState: " + connected);
            }
            this.isBtHeadsetConnected = connected;
            AudioManager am = (AudioManager) getSystemService("audio");
            if (!connected || isRinging() || this.currentState == 0) {
                this.bluetoothScoActive = false;
                this.bluetoothScoConnecting = false;
            } else if (this.bluetoothScoActive) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("SCO already active, setting audio routing");
                }
                if (!hasRtmpStream()) {
                    am.setSpeakerphoneOn(false);
                    am.setBluetoothScoOn(true);
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("startBluetoothSco");
                }
                if (!hasRtmpStream()) {
                    this.needSwitchToBluetoothAfterScoActivates = true;
                    AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda33(am), 500);
                }
            }
            Iterator<StateListener> it = this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
        }
    }

    static /* synthetic */ void lambda$updateBluetoothHeadsetState$80(AudioManager am) {
        try {
            am.startBluetoothSco();
        } catch (Throwable th) {
        }
    }

    public String getLastError() {
        return this.lastError;
    }

    public int getCallState() {
        return this.currentState;
    }

    public TLRPC.InputPeer getGroupCallPeer() {
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
        NetworkInfo info = getActiveNetworkInfo();
        this.lastNetInfo = info;
        if (info == null) {
            return 0;
        }
        switch (info.getType()) {
            case 0:
                switch (info.getSubtype()) {
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
            case 1:
                return 6;
            case 9:
                return 7;
            default:
                return 0;
        }
    }

    private NetworkInfo getActiveNetworkInfo() {
        return ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
    }

    /* access modifiers changed from: private */
    public void callFailed() {
        NativeInstance[] nativeInstanceArr = this.tgVoip;
        callFailed(nativeInstanceArr[0] != null ? nativeInstanceArr[0].getLastError() : "ERROR_UNKNOWN");
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x00a5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.Bitmap getRoundAvatarBitmap(org.telegram.tgnet.TLObject r9) {
        /*
            r8 = this;
            r0 = 0
            r1 = 1
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.User     // Catch:{ all -> 0x009f }
            java.lang.String r3 = "50_50"
            r4 = 0
            if (r2 == 0) goto L_0x0054
            r2 = r9
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2     // Catch:{ all -> 0x009f }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r2.photo     // Catch:{ all -> 0x009f }
            if (r5 == 0) goto L_0x0053
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r2.photo     // Catch:{ all -> 0x009f }
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ all -> 0x009f }
            if (r5 == 0) goto L_0x0053
            org.telegram.messenger.ImageLoader r5 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ all -> 0x009f }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r2.photo     // Catch:{ all -> 0x009f }
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small     // Catch:{ all -> 0x009f }
            android.graphics.drawable.BitmapDrawable r3 = r5.getImageFromMemory(r6, r4, r3)     // Catch:{ all -> 0x009f }
            if (r3 == 0) goto L_0x0030
            android.graphics.Bitmap r4 = r3.getBitmap()     // Catch:{ all -> 0x009f }
            android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x009f }
            android.graphics.Bitmap r4 = r4.copy(r5, r1)     // Catch:{ all -> 0x009f }
            r0 = r4
            goto L_0x0053
        L_0x0030:
            android.graphics.BitmapFactory$Options r4 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x004f }
            r4.<init>()     // Catch:{ all -> 0x004f }
            r4.inMutable = r1     // Catch:{ all -> 0x004f }
            int r5 = r8.currentAccount     // Catch:{ all -> 0x004f }
            org.telegram.messenger.FileLoader r5 = org.telegram.messenger.FileLoader.getInstance(r5)     // Catch:{ all -> 0x004f }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r2.photo     // Catch:{ all -> 0x004f }
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small     // Catch:{ all -> 0x004f }
            java.io.File r5 = r5.getPathToAttach(r6, r1)     // Catch:{ all -> 0x004f }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x004f }
            android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeFile(r5, r4)     // Catch:{ all -> 0x004f }
            r0 = r5
            goto L_0x0053
        L_0x004f:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x009f }
        L_0x0053:
            goto L_0x009e
        L_0x0054:
            r2 = r9
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2     // Catch:{ all -> 0x009f }
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r2.photo     // Catch:{ all -> 0x009f }
            if (r5 == 0) goto L_0x009e
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r2.photo     // Catch:{ all -> 0x009f }
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ all -> 0x009f }
            if (r5 == 0) goto L_0x009e
            org.telegram.messenger.ImageLoader r5 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ all -> 0x009f }
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r2.photo     // Catch:{ all -> 0x009f }
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small     // Catch:{ all -> 0x009f }
            android.graphics.drawable.BitmapDrawable r3 = r5.getImageFromMemory(r6, r4, r3)     // Catch:{ all -> 0x009f }
            if (r3 == 0) goto L_0x007b
            android.graphics.Bitmap r4 = r3.getBitmap()     // Catch:{ all -> 0x009f }
            android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x009f }
            android.graphics.Bitmap r4 = r4.copy(r5, r1)     // Catch:{ all -> 0x009f }
            r0 = r4
            goto L_0x009e
        L_0x007b:
            android.graphics.BitmapFactory$Options r4 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x009a }
            r4.<init>()     // Catch:{ all -> 0x009a }
            r4.inMutable = r1     // Catch:{ all -> 0x009a }
            int r5 = r8.currentAccount     // Catch:{ all -> 0x009a }
            org.telegram.messenger.FileLoader r5 = org.telegram.messenger.FileLoader.getInstance(r5)     // Catch:{ all -> 0x009a }
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r2.photo     // Catch:{ all -> 0x009a }
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small     // Catch:{ all -> 0x009a }
            java.io.File r5 = r5.getPathToAttach(r6, r1)     // Catch:{ all -> 0x009a }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x009a }
            android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeFile(r5, r4)     // Catch:{ all -> 0x009a }
            r0 = r5
            goto L_0x009e
        L_0x009a:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x009f }
        L_0x009e:
            goto L_0x00a3
        L_0x009f:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00a3:
            if (r0 != 0) goto L_0x00e1
            org.telegram.ui.ActionBar.Theme.createDialogsResources(r8)
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.User
            if (r2 == 0) goto L_0x00b5
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable
            r3 = r9
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC.User) r3
            r2.<init>((org.telegram.tgnet.TLRPC.User) r3)
            goto L_0x00bd
        L_0x00b5:
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable
            r3 = r9
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC.Chat) r3
            r2.<init>((org.telegram.tgnet.TLRPC.Chat) r3)
        L_0x00bd:
            r3 = 1109917696(0x42280000, float:42.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r4, r3, r5)
            int r3 = r0.getWidth()
            int r4 = r0.getHeight()
            r5 = 0
            r2.setBounds(r5, r5, r3, r4)
            android.graphics.Canvas r3 = new android.graphics.Canvas
            r3.<init>(r0)
            r2.draw(r3)
        L_0x00e1:
            android.graphics.Canvas r2 = new android.graphics.Canvas
            r2.<init>(r0)
            android.graphics.Path r3 = new android.graphics.Path
            r3.<init>()
            int r4 = r0.getWidth()
            int r4 = r4 / 2
            float r4 = (float) r4
            int r5 = r0.getHeight()
            int r5 = r5 / 2
            float r5 = (float) r5
            int r6 = r0.getWidth()
            int r6 = r6 / 2
            float r6 = (float) r6
            android.graphics.Path$Direction r7 = android.graphics.Path.Direction.CW
            r3.addCircle(r4, r5, r6, r7)
            r3.toggleInverseFillType()
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r1)
            r1 = r4
            android.graphics.PorterDuffXfermode r4 = new android.graphics.PorterDuffXfermode
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.CLEAR
            r4.<init>(r5)
            r1.setXfermode(r4)
            r2.drawPath(r3, r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.getRoundAvatarBitmap(org.telegram.tgnet.TLObject):android.graphics.Bitmap");
    }

    /* JADX WARNING: type inference failed for: r10v15 */
    /* JADX WARNING: type inference failed for: r10v16 */
    /* JADX WARNING: type inference failed for: r13v7, types: [android.text.SpannableString] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r10v5, types: [int, boolean] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showIncomingNotification(java.lang.String r25, java.lang.CharSequence r26, org.telegram.tgnet.TLObject r27, boolean r28, int r29) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            r3 = r26
            r4 = r27
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<org.telegram.ui.LaunchActivity> r5 = org.telegram.ui.LaunchActivity.class
            r0.<init>(r1, r5)
            r5 = r0
            java.lang.String r0 = "voip"
            r5.setAction(r0)
            android.app.Notification$Builder r0 = new android.app.Notification$Builder
            r0.<init>(r1)
            r6 = 2131629138(0x7f0e1452, float:1.8885588E38)
            java.lang.String r7 = "VoipInVideoCallBranding"
            r8 = 2131629136(0x7f0e1450, float:1.8885584E38)
            java.lang.String r9 = "VoipInCallBranding"
            if (r28 == 0) goto L_0x002b
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x002f
        L_0x002b:
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r9, r8)
        L_0x002f:
            android.app.Notification$Builder r0 = r0.setContentTitle(r10)
            android.app.Notification$Builder r0 = r0.setContentText(r2)
            r10 = 2131166005(0x7var_, float:1.7946243E38)
            android.app.Notification$Builder r0 = r0.setSmallIcon(r10)
            android.app.Notification$Builder r0 = r0.setSubText(r3)
            r10 = 0
            android.app.PendingIntent r11 = android.app.PendingIntent.getActivity(r1, r10, r5, r10)
            android.app.Notification$Builder r11 = r0.setContentIntent(r11)
            java.lang.String r0 = "content://org.telegram.messenger.beta.call_sound_provider/start_ringing"
            android.net.Uri r12 = android.net.Uri.parse(r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r13 = 26
            if (r0 < r13) goto L_0x0159
            android.content.SharedPreferences r13 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings()
            java.lang.String r0 = "calls_notification_channel"
            int r6 = r13.getInt(r0, r10)
            java.lang.String r14 = "notification"
            java.lang.Object r14 = r1.getSystemService(r14)
            android.app.NotificationManager r14 = (android.app.NotificationManager) r14
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r10 = "incoming_calls2"
            r8.append(r10)
            r8.append(r6)
            java.lang.String r8 = r8.toString()
            android.app.NotificationChannel r8 = r14.getNotificationChannel(r8)
            if (r8 == 0) goto L_0x0087
            java.lang.String r10 = r8.getId()
            r14.deleteNotificationChannel(r10)
        L_0x0087:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r15 = "incoming_calls3"
            r10.append(r15)
            r10.append(r6)
            java.lang.String r10 = r10.toString()
            android.app.NotificationChannel r10 = r14.getNotificationChannel(r10)
            r19 = 1
            r20 = r8
            r8 = 4
            if (r10 == 0) goto L_0x00eb
            int r3 = r10.getImportance()
            if (r3 < r8) goto L_0x00c3
            android.net.Uri r3 = r10.getSound()
            boolean r3 = r12.equals(r3)
            if (r3 == 0) goto L_0x00c3
            long[] r3 = r10.getVibrationPattern()
            if (r3 != 0) goto L_0x00c3
            boolean r3 = r10.shouldVibrate()
            if (r3 == 0) goto L_0x00c0
            goto L_0x00c3
        L_0x00c0:
            r19 = 0
            goto L_0x00eb
        L_0x00c3:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x00cc
            java.lang.String r3 = "User messed up the notification channel; deleting it and creating a proper one"
            org.telegram.messenger.FileLog.d(r3)
        L_0x00cc:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            r14.deleteNotificationChannel(r3)
            int r6 = r6 + 1
            android.content.SharedPreferences$Editor r3 = r13.edit()
            android.content.SharedPreferences$Editor r0 = r3.putInt(r0, r6)
            r0.commit()
        L_0x00eb:
            if (r19 == 0) goto L_0x0142
            android.media.AudioAttributes$Builder r0 = new android.media.AudioAttributes$Builder
            r0.<init>()
            android.media.AudioAttributes$Builder r0 = r0.setContentType(r8)
            r3 = 2
            android.media.AudioAttributes$Builder r0 = r0.setLegacyStreamType(r3)
            android.media.AudioAttributes$Builder r0 = r0.setUsage(r3)
            android.media.AudioAttributes r3 = r0.build()
            android.app.NotificationChannel r0 = new android.app.NotificationChannel
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r15)
            r8.append(r6)
            java.lang.String r8 = r8.toString()
            r21 = r10
            r10 = 2131626229(0x7f0e08f5, float:1.8879688E38)
            r22 = r13
            java.lang.String r13 = "IncomingCalls"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r13 = 4
            r0.<init>(r8, r10, r13)
            r8 = r0
            r8.setSound(r12, r3)
            r0 = 0
            r8.enableVibration(r0)
            r8.enableLights(r0)
            r0 = 1
            r8.setBypassDnd(r0)
            r14.createNotificationChannel(r8)     // Catch:{ Exception -> 0x0138 }
            goto L_0x0146
        L_0x0138:
            r0 = move-exception
            r7 = r0
            r0 = r7
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r24.stopSelf()
            return
        L_0x0142:
            r21 = r10
            r22 = r13
        L_0x0146:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            r11.setChannelId(r0)
            goto L_0x0164
        L_0x0159:
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r0 < r3) goto L_0x0164
            r0 = 2
            r11.setSound(r12, r0)
            goto L_0x0165
        L_0x0164:
        L_0x0165:
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<org.telegram.messenger.voip.VoIPActionsReceiver> r3 = org.telegram.messenger.voip.VoIPActionsReceiver.class
            r0.<init>(r1, r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = r24.getPackageName()
            r3.append(r6)
            java.lang.String r6 = ".DECLINE_CALL"
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            r0.setAction(r3)
            long r13 = r24.getCallID()
            java.lang.String r3 = "call_id"
            r0.putExtra(r3, r13)
            java.lang.String r6 = "VoipDeclineCall"
            r8 = 2131629014(0x7f0e13d6, float:1.8885337E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r6, r8)
            int r13 = android.os.Build.VERSION.SDK_INT
            r14 = 24
            if (r13 < r14) goto L_0x01b6
            android.text.SpannableString r13 = new android.text.SpannableString
            r13.<init>(r10)
            r10 = r13
            r13 = r10
            android.text.SpannableString r13 = (android.text.SpannableString) r13
            android.text.style.ForegroundColorSpan r15 = new android.text.style.ForegroundColorSpan
            r8 = -769226(0xffffffffffvar_, float:NaN)
            r15.<init>(r8)
            int r8 = r10.length()
            r14 = 0
            r13.setSpan(r15, r14, r8, r14)
            goto L_0x01b7
        L_0x01b6:
            r14 = 0
        L_0x01b7:
            r8 = 268435456(0x10000000, float:2.5243549E-29)
            android.app.PendingIntent r13 = android.app.PendingIntent.getBroadcast(r1, r14, r0, r8)
            r14 = 2131165465(0x7var_, float:1.7945148E38)
            r11.addAction(r14, r10, r13)
            android.content.Intent r14 = new android.content.Intent
            java.lang.Class<org.telegram.messenger.voip.VoIPActionsReceiver> r15 = org.telegram.messenger.voip.VoIPActionsReceiver.class
            r14.<init>(r1, r15)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r8 = r24.getPackageName()
            r15.append(r8)
            java.lang.String r8 = ".ANSWER_CALL"
            r15.append(r8)
            java.lang.String r8 = r15.toString()
            r14.setAction(r8)
            r8 = r12
            r15 = r13
            long r12 = r24.getCallID()
            r14.putExtra(r3, r12)
            java.lang.String r3 = "VoipAnswerCall"
            r12 = 2131628954(0x7f0e139a, float:1.8885215E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r3, r12)
            int r12 = android.os.Build.VERSION.SDK_INT
            r22 = r0
            r0 = 24
            if (r12 < r0) goto L_0x021a
            android.text.SpannableString r0 = new android.text.SpannableString
            r0.<init>(r13)
            r13 = r0
            r0 = r13
            android.text.SpannableString r0 = (android.text.SpannableString) r0
            android.text.style.ForegroundColorSpan r12 = new android.text.style.ForegroundColorSpan
            r20 = r8
            r8 = -16733696(0xfffffffffvar_aa00, float:-1.7102387E38)
            r12.<init>(r8)
            int r8 = r13.length()
            r23 = r10
            r10 = 0
            r0.setSpan(r12, r10, r8, r10)
            goto L_0x021f
        L_0x021a:
            r20 = r8
            r23 = r10
            r10 = 0
        L_0x021f:
            r0 = 268435456(0x10000000, float:2.5243549E-29)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r1, r10, r14, r0)
            r8 = 2131165464(0x7var_, float:1.7945146E38)
            r11.addAction(r8, r13, r0)
            r8 = 2
            r11.setPriority(r8)
            int r8 = android.os.Build.VERSION.SDK_INT
            r12 = 17
            if (r8 < r12) goto L_0x0238
            r11.setShowWhen(r10)
        L_0x0238:
            int r8 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r8 < r12) goto L_0x027b
            r8 = -13851168(0xffffffffff2ca5e0, float:-2.2948849E38)
            r11.setColor(r8)
            long[] r8 = new long[r10]
            r11.setVibrate(r8)
            java.lang.String r8 = "call"
            r11.setCategory(r8)
            android.app.PendingIntent r8 = android.app.PendingIntent.getActivity(r1, r10, r5, r10)
            r10 = 1
            r11.setFullScreenIntent(r8, r10)
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC.User
            if (r8 == 0) goto L_0x027b
            r8 = r4
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC.User) r8
            java.lang.String r10 = r8.phone
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 != 0) goto L_0x027b
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r12 = "tel:"
            r10.append(r12)
            java.lang.String r12 = r8.phone
            r10.append(r12)
            java.lang.String r10 = r10.toString()
            r11.addPerson(r10)
        L_0x027b:
            android.app.Notification r8 = r11.getNotification()
            int r10 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r10 < r12) goto L_0x0393
            android.widget.RemoteViews r10 = new android.widget.RemoteViews
            java.lang.String r12 = r24.getPackageName()
            boolean r17 = org.telegram.messenger.LocaleController.isRTL
            if (r17 == 0) goto L_0x0298
            r17 = 2131427329(0x7f0b0001, float:1.8476271E38)
            r18 = r5
            r5 = 2131427329(0x7f0b0001, float:1.8476271E38)
            goto L_0x029e
        L_0x0298:
            r17 = 2131427328(0x7f0b0000, float:1.847627E38)
            r18 = r5
            r5 = 2131427328(0x7f0b0000, float:1.847627E38)
        L_0x029e:
            r10.<init>(r12, r5)
            r5 = r10
            r10 = 2131230869(0x7var_, float:1.8077803E38)
            r5.setTextViewText(r10, r2)
            r10 = 1
            boolean r12 = android.text.TextUtils.isEmpty(r26)
            r2 = 2131230923(0x7var_cb, float:1.8077912E38)
            if (r12 == 0) goto L_0x031c
            r12 = 8
            r5.setViewVisibility(r2, r12)
            int r2 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r12 = 1
            if (r2 <= r12) goto L_0x0302
            int r2 = r1.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            if (r28 == 0) goto L_0x02e2
            java.lang.Object[] r9 = new java.lang.Object[r12]
            java.lang.String r12 = r2.first_name
            java.lang.String r7 = r2.last_name
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r12, r7)
            r16 = 0
            r9[r16] = r7
            java.lang.String r7 = "VoipInVideoCallBrandingWithName"
            r12 = 2131629139(0x7f0e1453, float:1.888559E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r12, r9)
            goto L_0x02f9
        L_0x02e2:
            r16 = 0
            java.lang.Object[] r9 = new java.lang.Object[r12]
            java.lang.String r12 = r2.first_name
            java.lang.String r7 = r2.last_name
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r12, r7)
            r9[r16] = r7
            java.lang.String r7 = "VoipInCallBrandingWithName"
            r12 = 2131629137(0x7f0e1451, float:1.8885586E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r12, r9)
        L_0x02f9:
            r9 = 2131230947(0x7var_e3, float:1.8077961E38)
            r5.setTextViewText(r9, r7)
            r2 = r26
            goto L_0x035a
        L_0x0302:
            if (r28 == 0) goto L_0x030c
            r2 = 2131629138(0x7f0e1452, float:1.8885588E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)
            goto L_0x0313
        L_0x030c:
            r2 = 2131629136(0x7f0e1450, float:1.8885584E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
        L_0x0313:
            r7 = 2131230947(0x7var_e3, float:1.8077961E38)
            r5.setTextViewText(r7, r2)
            r2 = r26
            goto L_0x035a
        L_0x031c:
            int r7 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r9 = 1
            if (r7 <= r9) goto L_0x034a
            int r7 = r1.currentAccount
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
            org.telegram.tgnet.TLRPC$User r7 = r7.getCurrentUser()
            java.lang.Object[] r9 = new java.lang.Object[r9]
            java.lang.String r2 = r7.first_name
            java.lang.String r12 = r7.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r12)
            r12 = 0
            r9[r12] = r2
            java.lang.String r2 = "VoipAnsweringAsAccount"
            r12 = 2131628955(0x7f0e139b, float:1.8885217E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r12, r9)
            r9 = 2131230923(0x7var_cb, float:1.8077912E38)
            r5.setTextViewText(r9, r2)
            goto L_0x0352
        L_0x034a:
            r9 = 2131230923(0x7var_cb, float:1.8077912E38)
            r2 = 8
            r5.setViewVisibility(r9, r2)
        L_0x0352:
            r2 = r26
            r7 = 2131230947(0x7var_e3, float:1.8077961E38)
            r5.setTextViewText(r7, r2)
        L_0x035a:
            android.graphics.Bitmap r7 = r1.getRoundAvatarBitmap(r4)
            r9 = 2131230771(0x7var_, float:1.8077604E38)
            r12 = 2131628954(0x7f0e139a, float:1.8885215E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r12)
            r5.setTextViewText(r9, r3)
            r3 = 2131230806(0x7var_, float:1.8077675E38)
            r9 = 2131629014(0x7f0e13d6, float:1.8885337E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r9)
            r5.setTextViewText(r3, r6)
            r3 = 2131230892(0x7var_ac, float:1.807785E38)
            r5.setImageViewBitmap(r3, r7)
            r3 = 2131230770(0x7var_, float:1.8077602E38)
            r5.setOnClickPendingIntent(r3, r0)
            r3 = 2131230805(0x7var_, float:1.8077673E38)
            r6 = r15
            r5.setOnClickPendingIntent(r3, r6)
            r11.setLargeIcon(r7)
            r8.bigContentView = r5
            r8.headsUpContentView = r5
            goto L_0x0398
        L_0x0393:
            r2 = r26
            r18 = r5
            r6 = r15
        L_0x0398:
            r3 = 202(0xca, float:2.83E-43)
            r1.startForeground(r3, r8)
            r24.startRingtoneAndVibration()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.showIncomingNotification(java.lang.String, java.lang.CharSequence, org.telegram.tgnet.TLObject, boolean, int):void");
    }

    private void callFailed(String error) {
        CallConnection callConnection;
        if (this.privateCall != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Discarding failed call");
            }
            TLRPC.TL_phone_discardCall req = new TLRPC.TL_phone_discardCall();
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.access_hash = this.privateCall.access_hash;
            req.peer.id = this.privateCall.id;
            req.duration = (int) (getCallDuration() / 1000);
            NativeInstance[] nativeInstanceArr = this.tgVoip;
            req.connection_id = nativeInstanceArr[0] != null ? nativeInstanceArr[0].getPreferredRelayId() : 0;
            req.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, VoIPService$$ExternalSyntheticLambda93.INSTANCE);
        }
        try {
            throw new Exception("Call " + getCallID() + " failed with error: " + error);
        } catch (Exception x) {
            FileLog.e((Throwable) x);
            this.lastError = error;
            AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda99(this));
            if (TextUtils.equals(error, "ERROR_LOCALIZED") && this.soundPool != null) {
                this.playingSound = true;
                Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda1(this));
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

    static /* synthetic */ void lambda$callFailed$81(TLObject response, TLRPC.TL_error error1) {
        if (error1 != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error on phone.discardCall: " + error1);
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("phone.discardCall " + response);
        }
    }

    /* renamed from: lambda$callFailed$82$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2443lambda$callFailed$82$orgtelegrammessengervoipVoIPService() {
        dispatchStateChanged(4);
    }

    /* renamed from: lambda$callFailed$83$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2444lambda$callFailed$83$orgtelegrammessengervoipVoIPService() {
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

    public void onConnectionStateChanged(int newState, boolean inTransition) {
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda29(this, newState));
    }

    /* renamed from: lambda$onConnectionStateChanged$86$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2478x77725var_(int newState) {
        if (newState == 3 && this.callStartTime == 0) {
            this.callStartTime = SystemClock.elapsedRealtime();
        }
        if (newState == 4) {
            callFailed();
            return;
        }
        if (newState == 3) {
            Runnable runnable = this.connectingSoundRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.connectingSoundRunnable = null;
            }
            Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda10(this));
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
        if (newState == 5) {
            Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda12(this));
        }
        dispatchStateChanged(newState);
    }

    /* renamed from: lambda$onConnectionStateChanged$84$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2476x5e320904() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    /* renamed from: lambda$onConnectionStateChanged$85$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2477xead23405() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayId = this.soundPool.play(this.groupCall != null ? this.spVoiceChatConnecting : this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    /* renamed from: lambda$playStartRecordSound$87$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2485x4a77var_() {
        this.soundPool.play(this.spStartRecordId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public void playStartRecordSound() {
        Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda18(this));
    }

    /* renamed from: lambda$playAllowTalkSound$88$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2483xb4a30f5a() {
        this.soundPool.play(this.spAllowTalkId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public void playAllowTalkSound() {
        Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda16(this));
    }

    public void onSignalBarCountChanged(int newCount) {
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda30(this, newCount));
    }

    /* renamed from: lambda$onSignalBarCountChanged$89$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2480x96697d72(int newCount) {
        this.signalBarCount = newCount;
        for (int a = 0; a < this.stateListeners.size(); a++) {
            this.stateListeners.get(a).onSignalBarsCountChanged(newCount);
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
        AndroidUtilities.runOnUIThread(new VoIPService$$ExternalSyntheticLambda55(this));
        int delay = 700;
        Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda66(this));
        Runnable runnable = this.connectingSoundRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.connectingSoundRunnable = null;
        }
        if (this.needPlayEndSound) {
            this.playingSound = true;
            if (this.groupCall == null) {
                Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda77(this));
            } else {
                Utilities.globalQueue.postRunnable(new VoIPService$$ExternalSyntheticLambda88(this), 100);
                delay = 500;
            }
            AndroidUtilities.runOnUIThread(this.afterSoundRunnable, (long) delay);
        }
        Runnable runnable2 = this.timeoutRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.timeoutRunnable = null;
        }
        endConnectionServiceCall(this.needPlayEndSound ? (long) delay : 0);
        stopSelf();
    }

    /* renamed from: lambda$callEnded$90$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2439lambda$callEnded$90$orgtelegrammessengervoipVoIPService() {
        dispatchStateChanged(11);
    }

    /* renamed from: lambda$callEnded$91$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2440lambda$callEnded$91$orgtelegrammessengervoipVoIPService() {
        int i = this.spPlayId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayId = 0;
        }
    }

    /* renamed from: lambda$callEnded$92$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2441lambda$callEnded$92$orgtelegrammessengervoipVoIPService() {
        this.soundPool.play(this.spEndId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    /* renamed from: lambda$callEnded$93$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2442lambda$callEnded$93$orgtelegrammessengervoipVoIPService() {
        this.soundPool.play(this.spVoiceChatEndId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    private void endConnectionServiceCall(long delay) {
        if (USE_CONNECTION_SERVICE) {
            Runnable r = new VoIPService$$ExternalSyntheticLambda3(this);
            if (delay > 0) {
                AndroidUtilities.runOnUIThread(r, delay);
            } else {
                r.run();
            }
        }
    }

    /* renamed from: lambda$endConnectionServiceCall$94$org-telegram-messenger-voip-VoIPService  reason: not valid java name */
    public /* synthetic */ void m2464x168fbaa9() {
        CallConnection callConnection = this.systemCallConnection;
        if (callConnection != null) {
            switch (this.callDiscardReason) {
                case 1:
                    callConnection.setDisconnected(new DisconnectCause(this.isOutgoing ? 2 : 6));
                    break;
                case 2:
                    callConnection.setDisconnected(new DisconnectCause(1));
                    break;
                case 3:
                    callConnection.setDisconnected(new DisconnectCause(this.isOutgoing ? 4 : 5));
                    break;
                case 4:
                    callConnection.setDisconnected(new DisconnectCause(7));
                    break;
                default:
                    callConnection.setDisconnected(new DisconnectCause(3));
                    break;
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
        if (Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT >= 30 || (checkSelfPermission("android.permission.RECORD_AUDIO") == 0 && (!this.privateCall.video || checkSelfPermission("android.permission.CAMERA") == 0))) {
            acceptIncomingCall();
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, getUIActivityClass()).setAction("voip"), 0).send();
            } catch (Exception x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting incall activity", (Throwable) x);
                }
            }
        } else {
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, VoIPPermissionActivity.class).addFlags(NUM), NUM).send();
            } catch (Exception x2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting permission activity", (Throwable) x2);
                }
            }
        }
    }

    public void updateOutputGainControlState() {
        if (!hasRtmpStream()) {
            int i = 0;
            if (this.tgVoip[0] == null) {
                return;
            }
            if (!USE_CONNECTION_SERVICE) {
                AudioManager am = (AudioManager) getSystemService("audio");
                this.tgVoip[0].setAudioOutputGainControlEnabled(hasEarpiece() && !am.isSpeakerphoneOn() && !am.isBluetoothScoOn() && !this.isHeadsetPlugged);
                NativeInstance nativeInstance = this.tgVoip[0];
                if (!this.isHeadsetPlugged && (!hasEarpiece() || am.isSpeakerphoneOn() || am.isBluetoothScoOn() || this.isHeadsetPlugged)) {
                    i = 1;
                }
                nativeInstance.setEchoCancellationStrength(i);
                return;
            }
            boolean isEarpiece = this.systemCallConnection.getCallAudioState().getRoute() == 1;
            this.tgVoip[0].setAudioOutputGainControlEnabled(isEarpiece);
            NativeInstance nativeInstance2 = this.tgVoip[0];
            if (!isEarpiece) {
                i = 1;
            }
            nativeInstance2.setEchoCancellationStrength(i);
        }
    }

    public int getAccount() {
        return this.currentAccount;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.appDidLogout) {
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

    private PhoneAccountHandle addAccountToTelecomManager() {
        TLRPC.User self = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        ComponentName componentName = new ComponentName(this, TelegramConnectionService.class);
        PhoneAccountHandle handle = new PhoneAccountHandle(componentName, "" + self.id);
        ((TelecomManager) getSystemService("telecom")).registerPhoneAccount(new PhoneAccount.Builder(handle, ContactsController.formatName(self.first_name, self.last_name)).setCapabilities(2048).setIcon(Icon.createWithResource(this, NUM)).setHighlightColor(-13851168).addSupportedUriScheme("sip").build());
        return handle;
    }

    private static boolean isDeviceCompatibleWithConnectionServiceAPI() {
        int i = Build.VERSION.SDK_INT;
        return false;
    }

    public interface StateListener {
        void onAudioSettingsChanged();

        void onCameraFirstFrameAvailable();

        void onCameraSwitch(boolean z);

        void onMediaStateUpdated(int i, int i2);

        void onScreenOnChange(boolean z);

        void onSignalBarsCountChanged(int i);

        void onStateChanged(int i);

        void onVideoAvailableChange(boolean z);

        /* renamed from: org.telegram.messenger.voip.VoIPService$StateListener$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onStateChanged(StateListener _this, int state) {
            }

            public static void $default$onSignalBarsCountChanged(StateListener _this, int count) {
            }

            public static void $default$onAudioSettingsChanged(StateListener _this) {
            }

            public static void $default$onMediaStateUpdated(StateListener _this, int audioState, int videoState) {
            }

            public static void $default$onCameraSwitch(StateListener _this, boolean isFrontFace) {
            }

            public static void $default$onCameraFirstFrameAvailable(StateListener _this) {
            }

            public static void $default$onVideoAvailableChange(StateListener _this, boolean isAvailable) {
            }

            public static void $default$onScreenOnChange(StateListener _this, boolean screenOn) {
            }
        }
    }

    public class CallConnection extends Connection {
        public CallConnection() {
            setConnectionProperties(128);
            setAudioModeIsVoip(true);
        }

        public void onCallAudioStateChanged(CallAudioState state) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService call audio state changed: " + state);
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

        public void onStateChanged(int state) {
            super.onStateChanged(state);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onStateChanged " + stateToString(state));
            }
            if (state == 4) {
                ContactsController.getInstance(VoIPService.this.currentAccount).deleteConnectionServiceContact();
                boolean unused = VoIPService.this.didDeleteConnectionServiceContact = true;
            }
        }

        public void onCallEvent(String event, Bundle extras) {
            super.onCallEvent(event, extras);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onCallEvent " + event);
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
