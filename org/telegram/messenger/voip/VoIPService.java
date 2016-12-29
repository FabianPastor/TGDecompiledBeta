package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.view.InputDeviceCompat;
import android.view.KeyEvent;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.ExoPlayerFactory;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.voip.VoIPController.ConnectionStateListener;
import org.telegram.messenger.voip.VoIPController.Stats;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.PhoneCall;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messages_dhConfig;
import org.telegram.tgnet.TLRPC.TL_messages_getDhConfig;
import org.telegram.tgnet.TLRPC.TL_phoneCall;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonDisconnect;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonHangup;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscarded;
import org.telegram.tgnet.TLRPC.TL_phoneCallProtocol;
import org.telegram.tgnet.TLRPC.TL_phoneConnection;
import org.telegram.tgnet.TLRPC.TL_phone_acceptCall;
import org.telegram.tgnet.TLRPC.TL_phone_discardCall;
import org.telegram.tgnet.TLRPC.TL_phone_phoneCall;
import org.telegram.tgnet.TLRPC.TL_phone_receivedCall;
import org.telegram.tgnet.TLRPC.TL_phone_requestCall;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_DhConfig;
import org.telegram.ui.VoIPActivity;
import org.telegram.ui.VoIPFeedbackActivity;
import org.telegram.ui.VoIPPermissionActivity;

public class VoIPService extends Service implements ConnectionStateListener, SensorEventListener, OnAudioFocusChangeListener {
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    private static final int CALL_MAX_LAYER = 60;
    private static final int CALL_MIN_LAYER = 60;
    public static final int DISCARD_REASON_DISCONNECT = 2;
    public static final int DISCARD_REASON_HANGUP = 1;
    public static final int DISCARD_REASON_LINE_BUSY = 4;
    public static final int DISCARD_REASON_MISSED = 3;
    private static final int ID_INCOMING_CALL_NOTIFICATION = 202;
    private static final int ID_ONGOING_CALL_NOTIFICATION = 201;
    private static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    public static final int STATE_BUSY = 12;
    public static final int STATE_ENDED = 6;
    public static final int STATE_ESTABLISHED = 3;
    public static final int STATE_EXCHANGING_KEYS = 7;
    public static final int STATE_FAILED = 4;
    public static final int STATE_HANGING_UP = 5;
    public static final int STATE_REQUESTING = 9;
    public static final int STATE_RINGING = 11;
    public static final int STATE_WAITING = 8;
    public static final int STATE_WAITING_INCOMING = 10;
    public static final int STATE_WAIT_INIT = 1;
    public static final int STATE_WAIT_INIT_ACK = 2;
    private static final String TAG = "tg-voip-service";
    public static PhoneCall callIShouldHavePutIntoIntent;
    private static VoIPService sharedInstance;
    private byte[] a_or_b;
    private byte[] authKey;
    private PhoneCall call;
    private int callReqId;
    private VoIPController controller;
    private boolean controllerStarted;
    private WakeLock cpuWakelock;
    private int currentState = 0;
    private int endHash;
    private boolean haveAudioFocus;
    private boolean isHeadsetPlugged;
    private boolean isOutgoing;
    private boolean isProximityNear;
    private long keyFingerprint;
    private int lastError;
    private long lastKnownDuration = 0;
    private NetworkInfo lastNetInfo;
    private boolean micMute;
    private boolean needPlayEndSound;
    private Notification ongoingCallNotification;
    private boolean playingSound;
    private Stats prevStats = new Stats();
    private WakeLock proximityWakelock;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean z = true;
            if (VoIPService.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
                VoIPService voIPService = VoIPService.this;
                if (intent.getIntExtra("state", 0) != 1) {
                    z = false;
                }
                voIPService.isHeadsetPlugged = z;
                if (VoIPService.this.isHeadsetPlugged && VoIPService.this.proximityWakelock != null && VoIPService.this.proximityWakelock.isHeld()) {
                    VoIPService.this.proximityWakelock.release();
                }
                VoIPService.this.isProximityNear = false;
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                VoIPService.this.updateNetworkType();
            } else if ((VoIPService.this.getPackageName() + ".END_CALL").equals(intent.getAction())) {
                if (intent.getIntExtra("end_hash", 0) == VoIPService.this.endHash) {
                    VoIPService.this.hangUp();
                }
            } else if (!(VoIPService.this.getPackageName() + ".ANSWER_CALL").equals(intent.getAction()) || intent.getIntExtra("end_hash", 0) != VoIPService.this.endHash) {
            } else {
                if (VERSION.SDK_INT < 23 || VoIPService.this.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                    VoIPService.this.acceptIncomingCall();
                    try {
                        PendingIntent.getActivity(VoIPService.this, 0, new Intent(VoIPService.this, VoIPActivity.class).addFlags(805306368), 0).send();
                        return;
                    } catch (Exception x) {
                        FileLog.e(VoIPService.TAG, "Error starting incall activity", x);
                        return;
                    }
                }
                try {
                    PendingIntent.getActivity(VoIPService.this, 0, new Intent(VoIPService.this, VoIPPermissionActivity.class).addFlags(268435456), 0).send();
                } catch (Exception x2) {
                    FileLog.e(VoIPService.TAG, "Error starting permission activity", x2);
                }
            }
        }
    };
    private MediaPlayer ringtonePlayer;
    private SoundPool soundPool;
    private int spBusyId;
    private int spEndId;
    private int spFailedID;
    private int spPlayID;
    private int spRingbackID;
    private ArrayList<StateListener> stateListeners = new ArrayList();
    private Stats stats = new Stats();
    private Runnable timeoutRunnable;
    private User user;
    private int userID;
    private Vibrator vibrator;

    public interface StateListener {
        void onAudioSettingsChanged();

        void onStateChanged(int i);
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.userID = intent.getIntExtra("user_id", 0);
        this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
        this.user = MessagesController.getInstance().getUser(Integer.valueOf(this.userID));
        if (this.isOutgoing) {
            startOutgoingCall();
            if (intent.getBooleanExtra("start_incall_activity", false)) {
                startActivity(new Intent(this, VoIPActivity.class).addFlags(268435456));
            }
        } else {
            this.call = callIShouldHavePutIntoIntent;
            callIShouldHavePutIntoIntent = null;
            startRinging();
        }
        return 2;
    }

    public void onCreate() {
        super.onCreate();
        sharedInstance = this;
        if (VERSION.SDK_INT >= 17) {
            AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            int outFramesPerBuffer = Integer.parseInt(am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER"));
            int outSampleRate = Integer.parseInt(am.getProperty("android.media.property.OUTPUT_SAMPLE_RATE"));
            int inFramesPerBuffer = AudioRecord.getMinBufferSize(48000, 16, 2) / 2;
            VoIPController.setNativeBufferSize(outFramesPerBuffer);
        } else {
            VoIPController.setNativeBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
        }
        try {
            this.controller = new VoIPController();
            this.controller.setConnectionStateListener(this);
            this.controller.setConfig(((double) MessagesController.getInstance().callPacketTimeout) / 1000.0d, ((double) MessagesController.getInstance().callConnectTimeout) / 1000.0d, ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("VoipDataSaving", 0), 40);
            this.cpuWakelock = ((PowerManager) getSystemService("power")).newWakeLock(1, "telegram-voip");
            this.cpuWakelock.acquire();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            filter.addAction(ACTION_HEADSET_PLUG);
            filter.addAction(getPackageName() + ".END_CALL");
            filter.addAction(getPackageName() + ".ANSWER_CALL");
            registerReceiver(this.receiver, filter);
            ConnectionsManager.getInstance().setAppPaused(false, false);
            this.soundPool = new SoundPool(1, 0, 0);
            this.spRingbackID = this.soundPool.load(this, R.raw.voip_ringback, 1);
            this.spFailedID = this.soundPool.load(this, R.raw.voip_failed, 1);
            this.spEndId = this.soundPool.load(this, R.raw.voip_end, 1);
            this.spBusyId = this.soundPool.load(this, R.raw.voip_busy, 1);
            ((AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO)).registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
        } catch (Exception x) {
            FileLog.e(TAG, "error initializing voip controller", x);
            callFailed();
        }
    }

    public void onDestroy() {
        stopForeground(true);
        stopRinging();
        SensorManager sm = (SensorManager) getSystemService("sensor");
        if (sm.getDefaultSensor(8) != null) {
            sm.unregisterListener(this);
        }
        if (this.proximityWakelock != null && this.proximityWakelock.isHeld()) {
            this.proximityWakelock.release();
        }
        unregisterReceiver(this.receiver);
        super.onDestroy();
        sharedInstance = null;
        if (this.controller != null && this.controllerStarted) {
            this.lastKnownDuration = this.controller.getCallDuration();
            updateStats();
            StatsController.getInstance().incrementTotalCallsTime(getStatsNetworkType(), ((int) (this.lastKnownDuration / 1000)) % 5);
            this.controller.release();
            this.controller = null;
        }
        this.cpuWakelock.release();
        AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        am.setMode(0);
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
        if (this.haveAudioFocus) {
            am.abandonAudioFocus(this);
        }
        if (!this.playingSound) {
            this.soundPool.release();
        }
        ConnectionsManager.getInstance().setAppPaused(true, false);
        if (this.lastKnownDuration >= 10000) {
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).addFlags(805306368), 0).send();
            } catch (Exception x) {
                FileLog.e(TAG, "Error starting incall activity", x);
            }
        }
    }

    public static VoIPService getSharedInstance() {
        return sharedInstance;
    }

    public User getUser() {
        return this.user;
    }

    public void setMicMute(boolean mute) {
        VoIPController voIPController = this.controller;
        this.micMute = mute;
        voIPController.setMicMute(mute);
    }

    public boolean isMicMute() {
        return this.micMute;
    }

    public String getDebugString() {
        return this.controller.getDebugString();
    }

    public long getCallDuration() {
        if (!this.controllerStarted || this.controller == null) {
            return this.lastKnownDuration;
        }
        long callDuration = this.controller.getCallDuration();
        this.lastKnownDuration = callDuration;
        return callDuration;
    }

    public void hangUp() {
        declineIncomingCall(this.currentState == 11 ? 3 : 1, null);
    }

    public void hangUp(Runnable onDone) {
        declineIncomingCall(this.currentState == 11 ? 3 : 1, onDone);
    }

    public void registerStateListener(StateListener l) {
        this.stateListeners.add(l);
        if (this.currentState != 0) {
            l.onStateChanged(this.currentState);
        }
    }

    public void unregisterStateListener(StateListener l) {
        this.stateListeners.remove(l);
    }

    private void startOutgoingCall() {
        configureDeviceForCall();
        showNotification();
        dispatchStateChanged(9);
        Utilities.random.nextBytes(new byte[256]);
        TL_messages_getDhConfig req = new TL_messages_getDhConfig();
        req.random_length = 256;
        req.version = MessagesStorage.lastSecretVersion;
        this.callReqId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                VoIPService.this.callReqId = 0;
                if (error == null) {
                    messages_DhConfig res = (messages_DhConfig) response;
                    if (response instanceof TL_messages_dhConfig) {
                        if (Utilities.isGoodPrime(res.p, res.g)) {
                            MessagesStorage.secretPBytes = res.p;
                            MessagesStorage.secretG = res.g;
                            MessagesStorage.lastSecretVersion = res.version;
                            MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
                        } else {
                            VoIPService.this.callFailed();
                            return;
                        }
                    }
                    final byte[] salt = new byte[256];
                    for (int a = 0; a < 256; a++) {
                        salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
                    }
                    byte[] g_a = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, salt), new BigInteger(1, MessagesStorage.secretPBytes)).toByteArray();
                    if (g_a.length > 256) {
                        byte[] correctedAuth = new byte[256];
                        System.arraycopy(g_a, 1, correctedAuth, 0, 256);
                        g_a = correctedAuth;
                    }
                    TL_phone_requestCall reqCall = new TL_phone_requestCall();
                    reqCall.user_id = MessagesController.getInputUser(VoIPService.this.user);
                    reqCall.protocol = new TL_phoneCallProtocol();
                    TL_phoneCallProtocol tL_phoneCallProtocol = reqCall.protocol;
                    reqCall.protocol.udp_reflector = true;
                    tL_phoneCallProtocol.udp_p2p = true;
                    reqCall.protocol.min_layer = 60;
                    reqCall.protocol.max_layer = 60;
                    reqCall.g_a = g_a;
                    reqCall.random_id = Utilities.random.nextInt();
                    ConnectionsManager.getInstance().sendRequest(reqCall, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (error == null) {
                                        VoIPService.this.call = ((TL_phone_phoneCall) response).phone_call;
                                        VoIPService.this.a_or_b = salt;
                                        VoIPService.this.dispatchStateChanged(8);
                                        VoIPService.this.timeoutRunnable = new Runnable() {
                                            public void run() {
                                                TL_phone_discardCall req = new TL_phone_discardCall();
                                                req.peer = new TL_inputPhoneCall();
                                                req.peer.access_hash = VoIPService.this.call.access_hash;
                                                req.peer.id = VoIPService.this.call.id;
                                                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                                    public void run(TLObject response, TL_error error) {
                                                        if (error != null) {
                                                            FileLog.e(VoIPService.TAG, "error on phone.discardCall: " + error);
                                                        } else {
                                                            FileLog.d(VoIPService.TAG, "phone.discardCall " + response);
                                                        }
                                                        VoIPService.this.callFailed();
                                                    }
                                                }, 2);
                                            }
                                        };
                                        AndroidUtilities.runOnUIThread(VoIPService.this.timeoutRunnable, (long) MessagesController.getInstance().callReceiveTimeout);
                                        return;
                                    }
                                    FileLog.e(VoIPService.TAG, "Error on phone.requestCall: " + error);
                                    VoIPService.this.callFailed();
                                }
                            }, 2);
                        }
                    });
                    return;
                }
                FileLog.e(VoIPService.TAG, "Error on getDhConfig " + error);
                VoIPService.this.callFailed();
            }
        }, 2);
    }

    private void startRinging() {
        int vibrate;
        dispatchStateChanged(10);
        SharedPreferences prefs = getSharedPreferences("Notifications", 0);
        this.ringtonePlayer = new MediaPlayer();
        this.ringtonePlayer.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                VoIPService.this.ringtonePlayer.start();
            }
        });
        this.ringtonePlayer.setLooping(true);
        this.ringtonePlayer.setAudioStreamType(2);
        try {
            String notificationUri;
            if (prefs.getBoolean("custom_" + this.user.id, false)) {
                notificationUri = prefs.getString("ringtone_path_" + this.user.id, RingtoneManager.getDefaultUri(1).toString());
            } else {
                notificationUri = prefs.getString("CallsRingtonePath", RingtoneManager.getDefaultUri(1).toString());
            }
            this.ringtonePlayer.setDataSource(this, Uri.parse(notificationUri));
            this.ringtonePlayer.prepareAsync();
        } catch (Throwable e) {
            FileLog.e(TAG, e);
            this.ringtonePlayer.release();
            this.ringtonePlayer = null;
        }
        AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (prefs.getBoolean("custom_" + this.user.id, false)) {
            vibrate = prefs.getInt("calls_vibrate_" + this.user.id, 0);
        } else {
            vibrate = prefs.getInt("vibrate_calls", 0);
        }
        if (!(vibrate == 2 || vibrate == 4 || (am.getRingerMode() != 1 && am.getRingerMode() != 2)) || (vibrate == 4 && am.getRingerMode() == 1)) {
            this.vibrator = (Vibrator) getSystemService("vibrator");
            long duration = 700;
            if (vibrate == 1) {
                duration = 700 / 2;
            } else if (vibrate == 3) {
                duration = 700 * 2;
            }
            this.vibrator.vibrate(new long[]{0, duration, 500}, 0);
        }
        if (VERSION.SDK_INT < 21 || ((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, VoIPActivity.class).addFlags(805306368), 0).send();
            } catch (Exception x) {
                FileLog.e(TAG, "Error starting incall activity", x);
            }
        } else {
            showIncomingNotification();
        }
        TL_phone_receivedCall req = new TL_phone_receivedCall();
        req.peer = new TL_inputPhoneCall();
        req.peer.id = this.call.id;
        req.peer.access_hash = this.call.access_hash;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                FileLog.w(VoIPService.TAG, "receivedCall response = " + response);
                if (error != null) {
                    FileLog.e(VoIPService.TAG, "error on receivedCall: " + error);
                }
            }
        }, 2);
    }

    public void acceptIncomingCall() {
        stopRinging();
        showNotification();
        dispatchStateChanged(7);
        TL_messages_getDhConfig req = new TL_messages_getDhConfig();
        req.random_length = 256;
        req.version = MessagesStorage.lastSecretVersion;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    int a;
                    messages_DhConfig res = (messages_DhConfig) response;
                    if (response instanceof TL_messages_dhConfig) {
                        if (Utilities.isGoodPrime(res.p, res.g)) {
                            MessagesStorage.secretPBytes = res.p;
                            MessagesStorage.secretG = res.g;
                            MessagesStorage.lastSecretVersion = res.version;
                            MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
                        } else {
                            FileLog.e(VoIPService.TAG, "stopping VoIP service, bad prime");
                            VoIPService.this.callFailed();
                            return;
                        }
                    }
                    byte[] salt = new byte[256];
                    for (a = 0; a < 256; a++) {
                        salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
                    }
                    VoIPService.this.a_or_b = salt;
                    BigInteger p = new BigInteger(1, MessagesStorage.secretPBytes);
                    BigInteger g_b = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, salt), p);
                    BigInteger g_a = new BigInteger(1, VoIPService.this.call.g_a);
                    if (Utilities.isGoodGaAndGb(g_a, p)) {
                        byte[] correctedAuth;
                        byte[] g_b_bytes = g_b.toByteArray();
                        if (g_b_bytes.length > 256) {
                            correctedAuth = new byte[256];
                            System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                            g_b_bytes = correctedAuth;
                        }
                        byte[] authKey = g_a.modPow(new BigInteger(1, salt), p).toByteArray();
                        if (authKey.length > 256) {
                            correctedAuth = new byte[256];
                            System.arraycopy(authKey, authKey.length + InputDeviceCompat.SOURCE_ANY, correctedAuth, 0, 256);
                            authKey = correctedAuth;
                        } else if (authKey.length < 256) {
                            correctedAuth = new byte[256];
                            System.arraycopy(authKey, 0, correctedAuth, 256 - authKey.length, authKey.length);
                            for (a = 0; a < 256 - authKey.length; a++) {
                                authKey[a] = (byte) 0;
                            }
                            authKey = correctedAuth;
                        }
                        byte[] authKeyHash = Utilities.computeSHA1(authKey);
                        byte[] authKeyId = new byte[8];
                        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                        VoIPService.this.authKey = authKey;
                        VoIPService.this.keyFingerprint = Utilities.bytesToLong(authKeyId);
                        TL_phone_acceptCall req = new TL_phone_acceptCall();
                        req.g_b = g_b_bytes;
                        req.key_fingerprint = Utilities.bytesToLong(authKeyId);
                        req.peer = new TL_inputPhoneCall();
                        req.peer.id = VoIPService.this.call.id;
                        req.peer.access_hash = VoIPService.this.call.access_hash;
                        req.protocol = new TL_phoneCallProtocol();
                        TL_phoneCallProtocol tL_phoneCallProtocol = req.protocol;
                        req.protocol.udp_reflector = true;
                        tL_phoneCallProtocol.udp_p2p = true;
                        req.protocol.min_layer = 60;
                        req.protocol.max_layer = 60;
                        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                if (error == null) {
                                    FileLog.w(VoIPService.TAG, "accept call ok! " + response);
                                    VoIPService.this.call = ((TL_phone_phoneCall) response).phone_call;
                                    VoIPService.this.configureDeviceForCall();
                                    VoIPService.this.initiateActualEncryptedCall();
                                    return;
                                }
                                FileLog.e(VoIPService.TAG, "Error on phone.acceptCall: " + error);
                                VoIPService.this.callFailed();
                            }
                        }, 2);
                        return;
                    }
                    FileLog.w(VoIPService.TAG, "stopping VoIP service, bad Ga and Gb (accepting)");
                    VoIPService.this.callFailed();
                    return;
                }
                VoIPService.this.callFailed();
            }
        });
    }

    public void declineIncomingCall() {
        declineIncomingCall(1, null);
    }

    public void declineIncomingCall(int reason, final Runnable onDone) {
        int i = 0;
        if (this.currentState != 5 && this.currentState != 6) {
            dispatchStateChanged(5);
            if (this.call == null) {
                if (onDone != null) {
                    onDone.run();
                }
                callEnded();
                if (this.callReqId != 0) {
                    ConnectionsManager.getInstance().cancelRequest(this.callReqId, false);
                    this.callReqId = 0;
                    return;
                }
                return;
            }
            TL_phone_discardCall req = new TL_phone_discardCall();
            req.peer = new TL_inputPhoneCall();
            req.peer.access_hash = this.call.access_hash;
            req.peer.id = this.call.id;
            if (this.controller != null && this.controllerStarted) {
                i = (int) (this.controller.getCallDuration() / 1000);
            }
            req.duration = i;
            long preferredRelayID = (this.controller == null || !this.controllerStarted) ? 0 : this.controller.getPreferredRelayID();
            req.connection_id = preferredRelayID;
            switch (reason) {
                case 2:
                    req.reason = new TL_phoneCallDiscardReasonDisconnect();
                    break;
                case 3:
                    req.reason = new TL_phoneCallDiscardReasonMissed();
                    break;
                case 4:
                    req.reason = new TL_phoneCallDiscardReasonBusy();
                    break;
                default:
                    req.reason = new TL_phoneCallDiscardReasonHangup();
                    break;
            }
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error != null) {
                        FileLog.e(VoIPService.TAG, "error on phone.discardCall: " + error);
                    } else {
                        FileLog.d(VoIPService.TAG, "phone.discardCall " + response);
                    }
                    if (onDone != null) {
                        AndroidUtilities.runOnUIThread(onDone);
                    }
                    VoIPService.this.callEnded();
                }
            }, 2);
        }
    }

    public void onCallUpdated(PhoneCall call) {
        this.call = call;
        if (call instanceof TL_phoneCallDiscarded) {
            FileLog.d(TAG, "call discarded, stopping service");
            if (call.reason instanceof TL_phoneCallDiscardReasonBusy) {
                dispatchStateChanged(12);
                this.playingSound = true;
                this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        VoIPService.this.soundPool.release();
                    }
                }, 2500);
                stopSelf();
                return;
            }
            callEnded();
        } else if ((call instanceof TL_phoneCall) && this.authKey == null) {
            processAcceptedCall();
        } else if (this.currentState == 8 && call.receive_date != 0) {
            dispatchStateChanged(11);
            FileLog.d(TAG, "!!!!!! CALL RECEIVED");
            if (this.spPlayID == 0) {
                this.spPlayID = this.soundPool.play(this.spRingbackID, 1.0f, 1.0f, 0, -1, 1.0f);
            }
            if (this.timeoutRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
            }
            this.timeoutRunnable = new Runnable() {
                public void run() {
                    VoIPService.this.declineIncomingCall(3, null);
                }
            };
            AndroidUtilities.runOnUIThread(this.timeoutRunnable, (long) MessagesController.getInstance().callRingTimeout);
        }
    }

    public void stopRinging() {
        if (this.ringtonePlayer != null) {
            this.ringtonePlayer.stop();
            this.ringtonePlayer.release();
            this.ringtonePlayer = null;
        }
        if (this.vibrator != null) {
            this.vibrator.cancel();
            this.vibrator = null;
        }
    }

    public byte[] getEncryptionKey() {
        return this.authKey;
    }

    private void processAcceptedCall() {
        BigInteger p = new BigInteger(1, MessagesStorage.secretPBytes);
        BigInteger i_authKey = new BigInteger(1, this.call.g_a_or_b);
        if (Utilities.isGoodGaAndGb(i_authKey, p)) {
            byte[] authKey = i_authKey.modPow(new BigInteger(1, this.a_or_b), p).toByteArray();
            byte[] correctedAuth;
            if (authKey.length > 256) {
                correctedAuth = new byte[256];
                System.arraycopy(authKey, authKey.length + InputDeviceCompat.SOURCE_ANY, correctedAuth, 0, 256);
                authKey = correctedAuth;
            } else if (authKey.length < 256) {
                correctedAuth = new byte[256];
                System.arraycopy(authKey, 0, correctedAuth, 256 - authKey.length, authKey.length);
                for (int a = 0; a < 256 - authKey.length; a++) {
                    authKey[a] = (byte) 0;
                }
                authKey = correctedAuth;
            }
            byte[] authKeyHash = Utilities.computeSHA1(authKey);
            byte[] authKeyId = new byte[8];
            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
            long fingerprint = Utilities.bytesToLong(authKeyId);
            if (this.call.key_fingerprint == fingerprint) {
                this.authKey = authKey;
                this.keyFingerprint = fingerprint;
                initiateActualEncryptedCall();
                return;
            }
            declineIncomingCall();
            return;
        }
        FileLog.w(TAG, "stopping VoIP service, bad Ga and Gb");
        callFailed();
    }

    private void initiateActualEncryptedCall() {
        if (this.spPlayID != 0) {
            this.soundPool.stop(this.spPlayID);
            this.spPlayID = 0;
        }
        if (this.timeoutRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
            this.timeoutRunnable = null;
        }
        try {
            FileLog.d(TAG, "InitCall: keyID=" + this.keyFingerprint);
            this.controller.setEncryptionKey(this.authKey);
            TL_phoneConnection[] endpoints = new TL_phoneConnection[(this.call.alternative_connections.size() + 1)];
            endpoints[0] = this.call.connection;
            for (int i = 0; i < this.call.alternative_connections.size(); i++) {
                endpoints[i + 1] = (TL_phoneConnection) this.call.alternative_connections.get(i);
            }
            this.controller.setRemoteEndpoints(endpoints, this.call.protocol.udp_p2p);
            this.controller.start();
            updateNetworkType();
            this.controller.connect();
            this.controllerStarted = true;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (VoIPService.this.controller != null) {
                        VoIPService.this.updateStats();
                        AndroidUtilities.runOnUIThread(this, ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                    }
                }
            }, ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } catch (Exception x) {
            FileLog.e(TAG, "error starting call", x);
            callFailed();
        }
    }

    private void showNotification() {
        int i = 1;
        Intent intent = new Intent(this, VoIPActivity.class);
        intent.addFlags(805306368);
        Builder builder = new Builder(this).setContentTitle(LocaleController.getString("VoipOutgoingCall", R.string.VoipOutgoingCall)).setContentText(ContactsController.formatName(this.user.first_name, this.user.last_name)).setSmallIcon(R.drawable.notification).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        if (VERSION.SDK_INT >= 16) {
            Intent endIntent = new Intent();
            endIntent.setAction(getPackageName() + ".END_CALL");
            int nextInt = Utilities.random.nextInt();
            this.endHash = nextInt;
            endIntent.putExtra("end_hash", nextInt);
            builder.addAction(R.drawable.ic_call_end_white_24dp, LocaleController.getString("VoipEndCall", R.string.VoipEndCall), PendingIntent.getBroadcast(this, 0, endIntent, 134217728));
            builder.setPriority(2);
        }
        if (VERSION.SDK_INT >= 17) {
            builder.setShowWhen(false);
        }
        if (VERSION.SDK_INT >= 21) {
            builder.setColor(-13851168);
        }
        if (this.user.photo != null) {
            FileLocation photoPath = this.user.photo.photo_small;
            if (photoPath != null) {
                BitmapDrawable img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                if (img != null) {
                    builder.setLargeIcon(img.getBitmap());
                } else {
                    try {
                        float scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                        Options options = new Options();
                        if (scaleFactor >= 1.0f) {
                            i = (int) scaleFactor;
                        }
                        options.inSampleSize = i;
                        Bitmap bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photoPath, true).toString(), options);
                        if (bitmap != null) {
                            builder.setLargeIcon(bitmap);
                        }
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            }
        }
        this.ongoingCallNotification = builder.getNotification();
        startForeground(ID_ONGOING_CALL_NOTIFICATION, this.ongoingCallNotification);
    }

    private void showIncomingNotification() {
        Intent intent = new Intent(this, VoIPActivity.class);
        intent.addFlags(805306368);
        Builder builder = new Builder(this).setContentTitle(LocaleController.getString("VoipInCallBranding", R.string.VoipInCallBranding)).setContentText(ContactsController.formatName(this.user.first_name, this.user.last_name)).setSmallIcon(R.drawable.notification).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        if (VERSION.SDK_INT >= 16) {
            this.endHash = Utilities.random.nextInt();
            Intent answerIntent = new Intent();
            answerIntent.setAction(getPackageName() + ".ANSWER_CALL");
            answerIntent.putExtra("end_hash", this.endHash);
            builder.addAction(R.drawable.ic_call_white_24dp, LocaleController.getString("VoipAnswerCall", R.string.VoipAnswerCall), PendingIntent.getBroadcast(this, 0, answerIntent, 134217728));
            Intent endIntent = new Intent();
            endIntent.setAction(getPackageName() + ".END_CALL");
            endIntent.putExtra("end_hash", this.endHash);
            builder.addAction(R.drawable.ic_call_end_white_24dp, LocaleController.getString("VoipDeclineCall", R.string.VoipDeclineCall), PendingIntent.getBroadcast(this, 0, endIntent, 134217728));
            builder.setPriority(2);
        }
        if (VERSION.SDK_INT >= 17) {
            builder.setShowWhen(false);
        }
        if (VERSION.SDK_INT >= 21) {
            builder.setColor(-13851168);
            builder.setVibrate(new long[0]);
            builder.setCategory("call");
            builder.setFullScreenIntent(PendingIntent.getActivity(this, 0, intent, 0), true);
        }
        if (this.user.photo != null) {
            FileLocation photoPath = this.user.photo.photo_small;
            if (photoPath != null) {
                BitmapDrawable img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                if (img != null) {
                    builder.setLargeIcon(img.getBitmap());
                } else {
                    try {
                        float scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                        Options options = new Options();
                        options.inSampleSize = scaleFactor < 1.0f ? 1 : (int) scaleFactor;
                        Bitmap bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photoPath, true).toString(), options);
                        if (bitmap != null) {
                            builder.setLargeIcon(bitmap);
                        }
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            }
        }
        startForeground(ID_INCOMING_CALL_NOTIFICATION, builder.getNotification());
    }

    private void callFailed() {
        if (this.controller != null && this.controllerStarted) {
            this.lastError = this.controller.getLastError();
        }
        TL_phone_discardCall req = new TL_phone_discardCall();
        req.peer = new TL_inputPhoneCall();
        req.peer.access_hash = this.call.access_hash;
        req.peer.id = this.call.id;
        int callDuration = (this.controller == null || !this.controllerStarted) ? 0 : (int) (this.controller.getCallDuration() / 1000);
        req.duration = callDuration;
        long preferredRelayID = (this.controller == null || !this.controllerStarted) ? 0 : this.controller.getPreferredRelayID();
        req.connection_id = preferredRelayID;
        req.reason = new TL_phoneCallDiscardReasonDisconnect();
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error != null) {
                    FileLog.e(VoIPService.TAG, "error on phone.discardCall: " + error);
                } else {
                    FileLog.d(VoIPService.TAG, "phone.discardCall " + response);
                }
            }
        });
        dispatchStateChanged(4);
        this.playingSound = true;
        this.soundPool.play(this.spFailedID, 1.0f, 1.0f, 0, 0, 1.0f);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                VoIPService.this.soundPool.release();
            }
        }, 1000);
        stopSelf();
    }

    private void callEnded() {
        dispatchStateChanged(6);
        if (this.needPlayEndSound) {
            this.playingSound = true;
            this.soundPool.play(this.spEndId, 1.0f, 1.0f, 0, 0, 1.0f);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    VoIPService.this.soundPool.release();
                }
            }, 1000);
        }
        stopSelf();
    }

    public void onConnectionStateChanged(int newState) {
        if (newState == 4) {
            callFailed();
            return;
        }
        if (newState == 3) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (VoIPService.this.controller != null) {
                        int netType = 1;
                        if (VoIPService.this.lastNetInfo != null && VoIPService.this.lastNetInfo.getType() == 0) {
                            netType = VoIPService.this.lastNetInfo.isRoaming() ? 2 : 0;
                        }
                        StatsController.getInstance().incrementTotalCallsTime(netType, 5);
                        AndroidUtilities.runOnUIThread(this, ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                    }
                }
            }, ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            if (this.isOutgoing) {
                StatsController.getInstance().incrementSentItemsCount(getStatsNetworkType(), 0, 1);
            } else {
                StatsController.getInstance().incrementReceivedItemsCount(getStatsNetworkType(), 0, 1);
            }
        }
        dispatchStateChanged(newState);
    }

    @SuppressLint({"NewApi"})
    public void onSensorChanged(SensorEvent event) {
        boolean newIsNear = false;
        if (event.sensor.getType() == 8 && !this.isHeadsetPlugged && !((AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO)).isSpeakerphoneOn()) {
            if (event.values[0] < Math.min(event.sensor.getMaximumRange(), 3.0f)) {
                newIsNear = true;
            }
            if (newIsNear != this.isProximityNear) {
                FileLog.d(TAG, "proximity " + newIsNear);
                this.isProximityNear = newIsNear;
                if (this.isProximityNear) {
                    this.proximityWakelock.acquire();
                } else {
                    this.proximityWakelock.release(1);
                }
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void updateNetworkType() {
        NetworkInfo info = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        this.lastNetInfo = info;
        int type = 0;
        if (info != null) {
            switch (info.getType()) {
                case 0:
                    switch (info.getSubtype()) {
                        case 1:
                            type = 1;
                            break;
                        case 2:
                            type = 2;
                            break;
                        case 3:
                            type = 3;
                            break;
                        case 8:
                        case 9:
                        case 10:
                        case 15:
                            type = 4;
                            break;
                        case 13:
                            type = 5;
                            break;
                        default:
                            type = 11;
                            break;
                    }
                case 1:
                    type = 6;
                    break;
                case 9:
                    type = 7;
                    break;
            }
        }
        if (this.controller != null) {
            this.controller.setNetworkType(type);
        }
    }

    private String getLanIP() {
        try {
            Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
            while (ifs.hasMoreElements()) {
                NetworkInterface i = (NetworkInterface) ifs.nextElement();
                if (!(!i.isUp() || i.isLoopback() || i.isPointToPoint())) {
                    Enumeration<InetAddress> addrs = i.getInetAddresses();
                    while (addrs.hasMoreElements()) {
                        InetAddress addr = (InetAddress) addrs.nextElement();
                        if (!addr.isLoopbackAddress() && (addr instanceof Inet4Address)) {
                            return addr.getHostAddress();
                        }
                    }
                    continue;
                }
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        return "0.0.0.0";
    }

    private void configureDeviceForCall() {
        this.needPlayEndSound = true;
        AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        am.setMode(3);
        am.setSpeakerphoneOn(false);
        am.requestAudioFocus(this, 0, 1);
        SensorManager sm = (SensorManager) getSystemService("sensor");
        Sensor proximity = sm.getDefaultSensor(8);
        if (proximity != null) {
            sm.registerListener(this, proximity, 3);
            this.proximityWakelock = ((PowerManager) getSystemService("power")).newWakeLock(32, "telegram-voip-prx");
        }
    }

    private void dispatchStateChanged(int state) {
        this.currentState = state;
        for (int a = 0; a < this.stateListeners.size(); a++) {
            ((StateListener) this.stateListeners.get(a)).onStateChanged(state);
        }
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == 1) {
            this.haveAudioFocus = true;
        } else {
            this.haveAudioFocus = false;
        }
    }

    public void onUIForegroundStateChanged(boolean isForeground) {
        if (this.currentState != 10) {
            return;
        }
        if (isForeground) {
            stopForeground(true);
        } else if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(VoIPService.this, VoIPActivity.class);
                    intent.addFlags(805306368);
                    try {
                        PendingIntent.getActivity(VoIPService.this, 0, intent, 0).send();
                    } catch (CanceledException e) {
                        FileLog.e(VoIPService.TAG, "error restarting activity", e);
                    }
                }
            }, 500);
        } else {
            showIncomingNotification();
        }
    }

    void onMediaButtonEvent(KeyEvent ev) {
        boolean z = true;
        if (ev.getKeyCode() != 79 || ev.getAction() != 1) {
            return;
        }
        if (this.currentState == 10) {
            acceptIncomingCall();
            return;
        }
        if (isMicMute()) {
            z = false;
        }
        setMicMute(z);
        Iterator it = this.stateListeners.iterator();
        while (it.hasNext()) {
            ((StateListener) it.next()).onAudioSettingsChanged();
        }
    }

    public void debugCtl(int request, int param) {
        if (this.controller != null) {
            this.controller.debugCtl(request, param);
        }
    }

    public int getLastError() {
        return this.lastError;
    }

    private void updateStats() {
        this.controller.getStats(this.stats);
        long wifiSentDiff = this.stats.bytesSentWifi - this.prevStats.bytesSentWifi;
        long wifiRecvdDiff = this.stats.bytesRecvdWifi - this.prevStats.bytesRecvdWifi;
        long mobileSentDiff = this.stats.bytesSentMobile - this.prevStats.bytesSentMobile;
        long mobileRecvdDiff = this.stats.bytesRecvdMobile - this.prevStats.bytesRecvdMobile;
        Stats tmp = this.stats;
        this.stats = this.prevStats;
        this.prevStats = tmp;
        if (wifiSentDiff > 0) {
            StatsController.getInstance().incrementSentBytesCount(1, 0, wifiSentDiff);
        }
        if (wifiRecvdDiff > 0) {
            StatsController.getInstance().incrementReceivedBytesCount(1, 0, wifiRecvdDiff);
        }
        if (mobileSentDiff > 0) {
            StatsController instance = StatsController.getInstance();
            int i = (this.lastNetInfo == null || !this.lastNetInfo.isRoaming()) ? 0 : 2;
            instance.incrementSentBytesCount(i, 0, mobileSentDiff);
        }
        if (mobileRecvdDiff > 0) {
            instance = StatsController.getInstance();
            i = (this.lastNetInfo == null || !this.lastNetInfo.isRoaming()) ? 0 : 2;
            instance.incrementReceivedBytesCount(i, 0, mobileRecvdDiff);
        }
    }

    private int getStatsNetworkType() {
        if (this.lastNetInfo == null || this.lastNetInfo.getType() != 0) {
            return 1;
        }
        return this.lastNetInfo.isRoaming() ? 2 : 0;
    }
}
