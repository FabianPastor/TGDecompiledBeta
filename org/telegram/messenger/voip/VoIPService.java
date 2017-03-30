package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
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
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.InputDeviceCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
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
import org.telegram.tgnet.TLRPC.TL_dataJSON;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messages_dhConfig;
import org.telegram.tgnet.TLRPC.TL_messages_getDhConfig;
import org.telegram.tgnet.TLRPC.TL_phoneCall;
import org.telegram.tgnet.TLRPC.TL_phoneCallAccepted;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonDisconnect;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonHangup;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscarded;
import org.telegram.tgnet.TLRPC.TL_phoneCallProtocol;
import org.telegram.tgnet.TLRPC.TL_phoneConnection;
import org.telegram.tgnet.TLRPC.TL_phone_acceptCall;
import org.telegram.tgnet.TLRPC.TL_phone_confirmCall;
import org.telegram.tgnet.TLRPC.TL_phone_discardCall;
import org.telegram.tgnet.TLRPC.TL_phone_getCallConfig;
import org.telegram.tgnet.TLRPC.TL_phone_phoneCall;
import org.telegram.tgnet.TLRPC.TL_phone_receivedCall;
import org.telegram.tgnet.TLRPC.TL_phone_requestCall;
import org.telegram.tgnet.TLRPC.TL_phone_saveCallDebug;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_DhConfig;
import org.telegram.ui.VoIPActivity;
import org.telegram.ui.VoIPFeedbackActivity;
import org.telegram.ui.VoIPPermissionActivity;

public class VoIPService extends Service implements ConnectionStateListener, SensorEventListener, OnAudioFocusChangeListener, NotificationCenterDelegate {
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    private static final int CALL_MAX_LAYER = 65;
    private static final int CALL_MIN_LAYER = 65;
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
    private BluetoothAdapter btAdapter;
    private PhoneCall call;
    private int callReqId;
    private VoIPController controller;
    private boolean controllerStarted;
    private WakeLock cpuWakelock;
    private int currentState = 0;
    private boolean endCallAfterRequest = false;
    private int endHash;
    private byte[] g_a;
    private byte[] g_a_hash;
    private boolean haveAudioFocus;
    private boolean isBtHeadsetConnected;
    private boolean isHeadsetPlugged;
    private boolean isOutgoing;
    private boolean isProximityNear;
    private long keyFingerprint;
    private int lastError;
    private long lastKnownDuration = 0;
    private NetworkInfo lastNetInfo;
    private Boolean mHasEarpiece = null;
    private boolean micMute;
    private boolean needPlayEndSound;
    private boolean needSendDebugLog = false;
    private Notification ongoingCallNotification;
    private ArrayList<PhoneCall> pendingUpdates = new ArrayList();
    private boolean playingSound;
    private Stats prevStats = new Stats();
    private WakeLock proximityWakelock;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean z = true;
            VoIPService voIPService;
            if (VoIPService.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
                voIPService = VoIPService.this;
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
                    VoIPService.this.stopForeground(true);
                    VoIPService.this.hangUp();
                }
            } else if ((VoIPService.this.getPackageName() + ".DECLINE_CALL").equals(intent.getAction())) {
                if (intent.getIntExtra("end_hash", 0) == VoIPService.this.endHash) {
                    VoIPService.this.stopForeground(true);
                    VoIPService.this.declineIncomingCall(4, null);
                }
            } else if ((VoIPService.this.getPackageName() + ".ANSWER_CALL").equals(intent.getAction())) {
                if (intent.getIntExtra("end_hash", 0) == VoIPService.this.endHash) {
                    VoIPService.this.showNotification();
                    if (VERSION.SDK_INT < 23 || VoIPService.this.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                        VoIPService.this.acceptIncomingCall();
                        try {
                            PendingIntent.getActivity(VoIPService.this, 0, new Intent(VoIPService.this, VoIPActivity.class).addFlags(805306368), 0).send();
                            return;
                        } catch (Exception x) {
                            FileLog.e("Error starting incall activity", x);
                            return;
                        }
                    }
                    try {
                        PendingIntent.getActivity(VoIPService.this, 0, new Intent(VoIPService.this, VoIPPermissionActivity.class).addFlags(268435456), 0).send();
                    } catch (Exception x2) {
                        FileLog.e("Error starting permission activity", x2);
                    }
                }
            } else if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                voIPService = VoIPService.this;
                if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) != 2) {
                    z = false;
                }
                voIPService.updateBluetoothHeadsetState(z);
            } else if ("android.media.ACTION_SCO_AUDIO_STATE_UPDATED".equals(intent.getAction())) {
                Iterator it = VoIPService.this.stateListeners.iterator();
                while (it.hasNext()) {
                    ((StateListener) it.next()).onAudioSettingsChanged();
                }
            } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(intent.getStringExtra("state"))) {
                    VoIPService.this.hangUp();
                }
            }
        }
    };
    private MediaPlayer ringtonePlayer;
    private SoundPool soundPool;
    private int spBusyId;
    private int spConnectingId;
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
        if (sharedInstance != null) {
            FileLog.e("Tried to start the VoIP service when it's already started");
        } else {
            this.userID = intent.getIntExtra("user_id", 0);
            this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
            this.user = MessagesController.getInstance().getUser(Integer.valueOf(this.userID));
            if (this.user == null) {
                FileLog.w("VoIPService: user==null");
                stopSelf();
            } else {
                if (this.isOutgoing) {
                    startOutgoingCall();
                    if (intent.getBooleanExtra("start_incall_activity", false)) {
                        startActivity(new Intent(this, VoIPActivity.class).addFlags(268435456));
                    }
                } else {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeInCallActivity, new Object[0]);
                    this.call = callIShouldHavePutIntoIntent;
                    callIShouldHavePutIntoIntent = null;
                    acknowledgeCallAndStartRinging();
                }
                sharedInstance = this;
            }
        }
        return 2;
    }

    public void onCreate() {
        super.onCreate();
        FileLog.d("=============== VoIPService STARTING ===============");
        AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (VERSION.SDK_INT < 17 || am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") == null) {
            VoIPController.setNativeBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
        } else {
            VoIPController.setNativeBufferSize(Integer.parseInt(am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")));
        }
        final SharedPreferences preferences = getSharedPreferences("mainconfig", 0);
        VoIPServerConfig.setConfig(preferences.getString("voip_server_config", "{}"));
        if (System.currentTimeMillis() - preferences.getLong("voip_server_config_updated", 0) > 86400000) {
            ConnectionsManager.getInstance().sendRequest(new TL_phone_getCallConfig(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        String data = ((TL_dataJSON) response).data;
                        VoIPServerConfig.setConfig(data);
                        preferences.edit().putString("voip_server_config", data).putLong("voip_server_config_updated", BuildConfig.DEBUG ? 0 : System.currentTimeMillis()).apply();
                    }
                }
            });
        }
        try {
            this.controller = new VoIPController();
            this.controller.setConnectionStateListener(this);
            this.controller.setConfig(((double) MessagesController.getInstance().callPacketTimeout) / 1000.0d, ((double) MessagesController.getInstance().callConnectTimeout) / 1000.0d, preferences.getInt("VoipDataSaving", 0));
            this.cpuWakelock = ((PowerManager) getSystemService("power")).newWakeLock(1, "telegram-voip");
            this.cpuWakelock.acquire();
            this.btAdapter = am.isBluetoothScoAvailableOffCall() ? BluetoothAdapter.getDefaultAdapter() : null;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            filter.addAction(ACTION_HEADSET_PLUG);
            if (this.btAdapter != null) {
                filter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                filter.addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
            }
            filter.addAction("android.intent.action.PHONE_STATE");
            filter.addAction(getPackageName() + ".END_CALL");
            filter.addAction(getPackageName() + ".DECLINE_CALL");
            filter.addAction(getPackageName() + ".ANSWER_CALL");
            registerReceiver(this.receiver, filter);
            ConnectionsManager.getInstance().setAppPaused(false, false);
            this.soundPool = new SoundPool(1, 0, 0);
            this.spConnectingId = this.soundPool.load(this, R.raw.voip_connecting, 1);
            this.spRingbackID = this.soundPool.load(this, R.raw.voip_ringback, 1);
            this.spFailedID = this.soundPool.load(this, R.raw.voip_failed, 1);
            this.spEndId = this.soundPool.load(this, R.raw.voip_end, 1);
            this.spBusyId = this.soundPool.load(this, R.raw.voip_busy, 1);
            am.registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (this.btAdapter != null && this.btAdapter.isEnabled()) {
                int headsetState = this.btAdapter.getProfileConnectionState(1);
                updateBluetoothHeadsetState(headsetState == 2);
                if (headsetState == 2) {
                    am.setBluetoothScoOn(true);
                }
                Iterator it = this.stateListeners.iterator();
                while (it.hasNext()) {
                    ((StateListener) it.next()).onAudioSettingsChanged();
                }
            }
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
        } catch (Exception x) {
            FileLog.e("error initializing voip controller", x);
            callFailed();
        }
    }

    public void onDestroy() {
        FileLog.d("=============== VoIPService STOPPING ===============");
        stopForeground(true);
        stopRinging();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
        SensorManager sm = (SensorManager) getSystemService("sensor");
        if (sm.getDefaultSensor(8) != null) {
            sm.unregisterListener(this);
        }
        if (this.proximityWakelock != null && this.proximityWakelock.isHeld()) {
            this.proximityWakelock.release();
        }
        unregisterReceiver(this.receiver);
        if (this.timeoutRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
            this.timeoutRunnable = null;
        }
        super.onDestroy();
        sharedInstance = null;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didEndedCall, new Object[0]);
            }
        });
        if (this.controller != null && this.controllerStarted) {
            this.lastKnownDuration = this.controller.getCallDuration();
            updateStats();
            StatsController.getInstance().incrementTotalCallsTime(getStatsNetworkType(), ((int) (this.lastKnownDuration / 1000)) % 5);
            if (this.needSendDebugLog) {
                String debugLog = this.controller.getDebugLog();
                TL_phone_saveCallDebug req = new TL_phone_saveCallDebug();
                req.debug = new TL_dataJSON();
                req.debug.data = debugLog;
                req.peer = new TL_inputPhoneCall();
                req.peer.access_hash = this.call.access_hash;
                req.peer.id = this.call.id;
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        FileLog.d("Sent debug logs, response=" + response);
                    }
                });
            }
            this.controller.release();
            this.controller = null;
        }
        this.cpuWakelock.release();
        AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (this.isBtHeadsetConnected && !this.playingSound) {
            am.stopBluetoothSco();
        }
        am.setMode(0);
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
        if (this.haveAudioFocus) {
            am.abandonAudioFocus(this);
        }
        if (!this.playingSound) {
            this.soundPool.release();
        }
        ConnectionsManager.getInstance().setAppPaused(true, false);
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
        int i = (this.currentState == 11 || (this.currentState == 8 && this.isOutgoing)) ? 3 : 1;
        declineIncomingCall(i, null);
    }

    public void hangUp(Runnable onDone) {
        int i = (this.currentState == 11 || (this.currentState == 8 && this.isOutgoing)) ? 3 : 1;
        declineIncomingCall(i, onDone);
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
        startConnectingSound();
        dispatchStateChanged(9);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
            }
        });
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
                    reqCall.protocol.min_layer = 65;
                    reqCall.protocol.max_layer = 65;
                    VoIPService.this.g_a = g_a;
                    reqCall.g_a_hash = Utilities.computeSHA256(g_a, 0, g_a.length);
                    reqCall.random_id = Utilities.random.nextInt();
                    ConnectionsManager.getInstance().sendRequest(reqCall, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (error == null) {
                                        VoIPService.this.call = ((TL_phone_phoneCall) response).phone_call;
                                        VoIPService.this.a_or_b = salt;
                                        VoIPService.this.dispatchStateChanged(8);
                                        if (VoIPService.this.endCallAfterRequest) {
                                            VoIPService.this.hangUp();
                                            return;
                                        }
                                        if (VoIPService.this.pendingUpdates.size() > 0 && VoIPService.this.call != null) {
                                            Iterator it = VoIPService.this.pendingUpdates.iterator();
                                            while (it.hasNext()) {
                                                VoIPService.this.onCallUpdated((PhoneCall) it.next());
                                            }
                                            VoIPService.this.pendingUpdates.clear();
                                        }
                                        VoIPService.this.timeoutRunnable = new Runnable() {
                                            public void run() {
                                                VoIPService.this.timeoutRunnable = null;
                                                TL_phone_discardCall req = new TL_phone_discardCall();
                                                req.peer = new TL_inputPhoneCall();
                                                req.peer.access_hash = VoIPService.this.call.access_hash;
                                                req.peer.id = VoIPService.this.call.id;
                                                req.reason = new TL_phoneCallDiscardReasonMissed();
                                                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                                    public void run(TLObject response, TL_error error) {
                                                        if (error != null) {
                                                            FileLog.e("error on phone.discardCall: " + error);
                                                        } else {
                                                            FileLog.d("phone.discardCall " + response);
                                                        }
                                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                                            public void run() {
                                                                VoIPService.this.callFailed();
                                                            }
                                                        });
                                                    }
                                                }, 2);
                                            }
                                        };
                                        AndroidUtilities.runOnUIThread(VoIPService.this.timeoutRunnable, (long) MessagesController.getInstance().callReceiveTimeout);
                                    } else if (error.code == 400 && "PARTICIPANT_VERSION_OUTDATED".equals(error.text)) {
                                        VoIPService.this.callFailed(-1);
                                    } else if (error.code == 403 && "USER_PRIVACY_RESTRICTED".equals(error.text)) {
                                        VoIPService.this.callFailed(-2);
                                    } else if (error.code == 406) {
                                        VoIPService.this.callFailed(-3);
                                    } else {
                                        FileLog.e("Error on phone.requestCall: " + error);
                                        VoIPService.this.callFailed();
                                    }
                                }
                            });
                        }
                    }, 2);
                    return;
                }
                FileLog.e("Error on getDhConfig " + error);
                VoIPService.this.callFailed();
            }
        }, 2);
    }

    private void acknowledgeCallAndStartRinging() {
        if (this.call instanceof TL_phoneCallDiscarded) {
            FileLog.w("Call " + this.call.id + " was discarded before the service started, stopping");
            stopSelf();
            return;
        }
        TL_phone_receivedCall req = new TL_phone_receivedCall();
        req.peer = new TL_inputPhoneCall();
        req.peer.id = this.call.id;
        req.peer.access_hash = this.call.access_hash;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (VoIPService.sharedInstance != null) {
                            FileLog.w("receivedCall response = " + response);
                            if (error != null) {
                                FileLog.e("error on receivedCall: " + error);
                                VoIPService.this.stopSelf();
                                return;
                            }
                            VoIPService.this.startRinging();
                        }
                    }
                });
            }
        }, 2);
    }

    private void startRinging() {
        int vibrate;
        FileLog.d("starting ringing for call " + this.call.id);
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
            FileLog.e(e);
            if (this.ringtonePlayer != null) {
                this.ringtonePlayer.release();
                this.ringtonePlayer = null;
            }
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
        if (VERSION.SDK_INT < 21 || ((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode() || !NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            FileLog.d("Starting incall activity for incoming call");
            try {
                PendingIntent.getActivity(this, 12345, new Intent(this, VoIPActivity.class).addFlags(268435456), 0).send();
                return;
            } catch (Exception x) {
                FileLog.e("Error starting incall activity", x);
                return;
            }
        }
        showIncomingNotification();
        FileLog.d("Showing incoming call notification");
    }

    public void acceptIncomingCall() {
        stopRinging();
        showNotification();
        configureDeviceForCall();
        startConnectingSound();
        dispatchStateChanged(7);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
            }
        });
        TL_messages_getDhConfig req = new TL_messages_getDhConfig();
        req.random_length = 256;
        req.version = MessagesStorage.lastSecretVersion;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    messages_DhConfig res = (messages_DhConfig) response;
                    if (response instanceof TL_messages_dhConfig) {
                        if (Utilities.isGoodPrime(res.p, res.g)) {
                            MessagesStorage.secretPBytes = res.p;
                            MessagesStorage.secretG = res.g;
                            MessagesStorage.lastSecretVersion = res.version;
                            MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
                        } else {
                            FileLog.e("stopping VoIP service, bad prime");
                            VoIPService.this.callFailed();
                            return;
                        }
                    }
                    byte[] salt = new byte[256];
                    for (int a = 0; a < 256; a++) {
                        salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
                    }
                    VoIPService.this.a_or_b = salt;
                    BigInteger g_b = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, salt), new BigInteger(1, MessagesStorage.secretPBytes));
                    VoIPService.this.g_a_hash = VoIPService.this.call.g_a_hash;
                    byte[] g_b_bytes = g_b.toByteArray();
                    if (g_b_bytes.length > 256) {
                        byte[] correctedAuth = new byte[256];
                        System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                        g_b_bytes = correctedAuth;
                    }
                    TL_phone_acceptCall req = new TL_phone_acceptCall();
                    req.g_b = g_b_bytes;
                    req.peer = new TL_inputPhoneCall();
                    req.peer.id = VoIPService.this.call.id;
                    req.peer.access_hash = VoIPService.this.call.access_hash;
                    req.protocol = new TL_phoneCallProtocol();
                    TL_phoneCallProtocol tL_phoneCallProtocol = req.protocol;
                    req.protocol.udp_reflector = true;
                    tL_phoneCallProtocol.udp_p2p = true;
                    req.protocol.min_layer = 65;
                    req.protocol.max_layer = 65;
                    ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (error == null) {
                                        FileLog.w("accept call ok! " + response);
                                        VoIPService.this.call = ((TL_phone_phoneCall) response).phone_call;
                                        if (VoIPService.this.call instanceof TL_phoneCallDiscarded) {
                                            VoIPService.this.onCallUpdated(VoIPService.this.call);
                                            return;
                                        }
                                        return;
                                    }
                                    FileLog.e("Error on phone.acceptCall: " + error);
                                    VoIPService.this.callFailed();
                                }
                            });
                        }
                    }, 2);
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
        boolean wasNotConnected = true;
        if (this.currentState == 9) {
            this.endCallAfterRequest = true;
        } else if (this.currentState != 5 && this.currentState != 6) {
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
            int i;
            TL_phone_discardCall req = new TL_phone_discardCall();
            req.peer = new TL_inputPhoneCall();
            req.peer.access_hash = this.call.access_hash;
            req.peer.id = this.call.id;
            if (this.controller == null || !this.controllerStarted) {
                i = 0;
            } else {
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
            if (ConnectionsManager.getInstance().getConnectionState() == 3) {
                wasNotConnected = false;
            }
            if (wasNotConnected) {
                if (onDone != null) {
                    onDone.run();
                }
                callEnded();
            }
            final Runnable stopper = new Runnable() {
                private boolean done = false;

                public void run() {
                    if (!this.done) {
                        this.done = true;
                        if (onDone != null) {
                            onDone.run();
                        }
                        VoIPService.this.callEnded();
                    }
                }
            };
            AndroidUtilities.runOnUIThread(stopper, (long) ((int) (VoIPServerConfig.getDouble("hangup_ui_timeout", 5.0d) * 1000.0d)));
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error != null) {
                        FileLog.e("error on phone.discardCall: " + error);
                    } else {
                        if (response instanceof TL_updates) {
                            MessagesController.getInstance().processUpdates((TL_updates) response, false);
                        }
                        FileLog.d("phone.discardCall " + response);
                    }
                    if (!wasNotConnected) {
                        AndroidUtilities.cancelRunOnUIThread(stopper);
                        if (onDone != null) {
                            onDone.run();
                        }
                    }
                }
            }, 2);
        }
    }

    private void dumpCallObject() {
        try {
            for (Field f : PhoneCall.class.getFields()) {
                FileLog.d(f.getName() + " = " + f.get(this.call));
            }
        } catch (Throwable x) {
            FileLog.e(x);
        }
    }

    public void onCallUpdated(PhoneCall call) {
        if (this.call == null) {
            this.pendingUpdates.add(call);
        } else if (call == null) {
        } else {
            if (call.id == this.call.id) {
                if (call.access_hash == 0) {
                    call.access_hash = this.call.access_hash;
                }
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("Call updated: " + call);
                    dumpCallObject();
                }
                this.call = call;
                if (call instanceof TL_phoneCallDiscarded) {
                    this.needSendDebugLog = call.need_debug;
                    FileLog.d("call discarded, stopping service");
                    if (call.reason instanceof TL_phoneCallDiscardReasonBusy) {
                        dispatchStateChanged(12);
                        this.playingSound = true;
                        this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                VoIPService.this.soundPool.release();
                                if (VoIPService.this.isBtHeadsetConnected) {
                                    ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).stopBluetoothSco();
                                }
                            }
                        }, 2500);
                        stopSelf();
                    } else {
                        callEnded();
                    }
                    if (call.need_rating) {
                        startRatingActivity();
                    }
                } else if ((call instanceof TL_phoneCall) && this.authKey == null) {
                    if (call.g_a_or_b == null) {
                        FileLog.w("stopping VoIP service, Ga == null");
                        callFailed();
                    } else if (Arrays.equals(this.g_a_hash, Utilities.computeSHA256(call.g_a_or_b, 0, call.g_a_or_b.length))) {
                        this.g_a = call.g_a_or_b;
                        BigInteger g_a = new BigInteger(1, call.g_a_or_b);
                        BigInteger p = new BigInteger(1, MessagesStorage.secretPBytes);
                        if (Utilities.isGoodGaAndGb(g_a, p)) {
                            byte[] authKey = g_a.modPow(new BigInteger(1, this.a_or_b), p).toByteArray();
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
                            this.authKey = authKey;
                            this.keyFingerprint = Utilities.bytesToLong(authKeyId);
                            if (this.keyFingerprint != call.key_fingerprint) {
                                FileLog.w("key fingerprints don't match");
                                callFailed();
                                return;
                            }
                            initiateActualEncryptedCall();
                            return;
                        }
                        FileLog.w("stopping VoIP service, bad Ga and Gb (accepting)");
                        callFailed();
                    } else {
                        FileLog.w("stopping VoIP service, Ga hash doesn't match");
                        callFailed();
                    }
                } else if ((call instanceof TL_phoneCallAccepted) && this.authKey == null) {
                    processAcceptedCall();
                } else if (this.currentState == 8 && call.receive_date != 0) {
                    dispatchStateChanged(11);
                    FileLog.d("!!!!!! CALL RECEIVED");
                    if (this.spPlayID != 0) {
                        this.soundPool.stop(this.spPlayID);
                    }
                    this.spPlayID = this.soundPool.play(this.spRingbackID, 1.0f, 1.0f, 0, -1, 1.0f);
                    if (this.timeoutRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
                        this.timeoutRunnable = null;
                    }
                    this.timeoutRunnable = new Runnable() {
                        public void run() {
                            VoIPService.this.timeoutRunnable = null;
                            VoIPService.this.declineIncomingCall(3, null);
                        }
                    };
                    AndroidUtilities.runOnUIThread(this.timeoutRunnable, (long) MessagesController.getInstance().callRingTimeout);
                }
            } else if (BuildVars.DEBUG_VERSION) {
                FileLog.w("onCallUpdated called with wrong call id (got " + call.id + ", expected " + this.call.id + ")");
            }
        }
    }

    private void startRatingActivity() {
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).putExtra("call_id", this.call.id).putExtra("call_access_hash", this.call.access_hash).addFlags(805306368), 0).send();
        } catch (Exception x) {
            FileLog.e("Error starting incall activity", x);
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
        dispatchStateChanged(7);
        BigInteger p = new BigInteger(1, MessagesStorage.secretPBytes);
        BigInteger i_authKey = new BigInteger(1, this.call.g_b);
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
            this.authKey = authKey;
            this.keyFingerprint = fingerprint;
            TL_phone_confirmCall req = new TL_phone_confirmCall();
            req.g_a = this.g_a;
            req.key_fingerprint = fingerprint;
            req.peer = new TL_inputPhoneCall();
            req.peer.id = this.call.id;
            req.peer.access_hash = this.call.access_hash;
            req.protocol = new TL_phoneCallProtocol();
            req.protocol.max_layer = 65;
            req.protocol.min_layer = 65;
            TL_phoneCallProtocol tL_phoneCallProtocol = req.protocol;
            req.protocol.udp_reflector = true;
            tL_phoneCallProtocol.udp_p2p = true;
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error != null) {
                                VoIPService.this.callFailed();
                                return;
                            }
                            VoIPService.this.call = ((TL_phone_phoneCall) response).phone_call;
                            VoIPService.this.initiateActualEncryptedCall();
                        }
                    });
                }
            });
            return;
        }
        FileLog.w("stopping VoIP service, bad Ga and Gb");
        callFailed();
    }

    private void initiateActualEncryptedCall() {
        if (this.timeoutRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
            this.timeoutRunnable = null;
        }
        try {
            FileLog.d("InitCall: keyID=" + this.keyFingerprint);
            this.controller.setEncryptionKey(this.authKey, this.isOutgoing);
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
            FileLog.e("error starting call", x);
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
                        FileLog.e(e);
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
            Intent endIntent = new Intent();
            endIntent.setAction(getPackageName() + ".DECLINE_CALL");
            endIntent.putExtra("end_hash", this.endHash);
            CharSequence endTitle = LocaleController.getString("VoipDeclineCall", R.string.VoipDeclineCall);
            if (VERSION.SDK_INT >= 24) {
                CharSequence endTitle2 = new SpannableString(endTitle);
                ((SpannableString) endTitle2).setSpan(new ForegroundColorSpan(-769226), 0, endTitle2.length(), 0);
                endTitle = endTitle2;
            }
            builder.addAction(R.drawable.ic_call_end_white_24dp, endTitle, PendingIntent.getBroadcast(this, 0, endIntent, 134217728));
            Intent answerIntent = new Intent();
            answerIntent.setAction(getPackageName() + ".ANSWER_CALL");
            answerIntent.putExtra("end_hash", this.endHash);
            CharSequence answerTitle = LocaleController.getString("VoipAnswerCall", R.string.VoipAnswerCall);
            if (VERSION.SDK_INT >= 24) {
                CharSequence answerTitle2 = new SpannableString(answerTitle);
                ((SpannableString) answerTitle2).setSpan(new ForegroundColorSpan(-16733696), 0, answerTitle2.length(), 0);
                answerTitle = answerTitle2;
            }
            builder.addAction(R.drawable.ic_call_white_24dp, answerTitle, PendingIntent.getBroadcast(this, 0, answerIntent, 134217728));
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
            TLObject photoPath = this.user.photo.photo_small;
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
                        FileLog.e(e);
                    }
                }
            }
        }
        startForeground(ID_INCOMING_CALL_NOTIFICATION, builder.getNotification());
    }

    private void startConnectingSound() {
        if (this.spPlayID != 0) {
            this.soundPool.stop(this.spPlayID);
        }
        this.spPlayID = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        if (this.spPlayID == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (VoIPService.sharedInstance != null) {
                        if (VoIPService.this.spPlayID == 0) {
                            VoIPService.this.spPlayID = VoIPService.this.soundPool.play(VoIPService.this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
                        }
                        if (VoIPService.this.spPlayID == 0) {
                            AndroidUtilities.runOnUIThread(this, 100);
                        }
                    }
                }
            }, 100);
        }
    }

    private void callFailed() {
        int lastError = (this.controller == null || !this.controllerStarted) ? 0 : this.controller.getLastError();
        callFailed(lastError);
    }

    private void callFailed(int errorCode) {
        try {
            throw new Exception("Call " + (this.call != null ? this.call.id : 0) + " failed with error code " + errorCode);
        } catch (Throwable x) {
            FileLog.e(x);
            this.lastError = errorCode;
            if (this.call != null) {
                FileLog.d("Discarding failed call");
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
                            FileLog.e("error on phone.discardCall: " + error);
                        } else {
                            FileLog.d("phone.discardCall " + response);
                        }
                    }
                });
            }
            dispatchStateChanged(4);
            if (!(errorCode == -3 || this.soundPool == null)) {
                this.playingSound = true;
                this.soundPool.play(this.spFailedID, 1.0f, 1.0f, 0, 0, 1.0f);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        VoIPService.this.soundPool.release();
                        if (VoIPService.this.isBtHeadsetConnected) {
                            ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).stopBluetoothSco();
                        }
                    }
                }, 1000);
            }
            stopSelf();
        }
    }

    private void callEnded() {
        FileLog.d("Call " + (this.call != null ? this.call.id : 0) + " ended");
        dispatchStateChanged(6);
        if (this.needPlayEndSound) {
            this.playingSound = true;
            this.soundPool.play(this.spEndId, 1.0f, 1.0f, 0, 0, 1.0f);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    VoIPService.this.soundPool.release();
                    if (VoIPService.this.isBtHeadsetConnected) {
                        ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).stopBluetoothSco();
                    }
                }
            }, 1000);
        }
        if (this.timeoutRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
            this.timeoutRunnable = null;
        }
        stopSelf();
    }

    public void onConnectionStateChanged(int newState) {
        if (newState == 4) {
            callFailed();
            return;
        }
        if (newState == 3) {
            if (this.spPlayID != 0) {
                this.soundPool.stop(this.spPlayID);
                this.spPlayID = 0;
            }
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

    public boolean isOutgoing() {
        return this.isOutgoing;
    }

    @SuppressLint({"NewApi"})
    public void onSensorChanged(SensorEvent event) {
        boolean newIsNear = true;
        if (event.sensor.getType() == 8) {
            AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (!this.isHeadsetPlugged && !am.isSpeakerphoneOn()) {
                if (!isBluetoothHeadsetConnected() || !am.isBluetoothScoOn()) {
                    if (event.values[0] >= Math.min(event.sensor.getMaximumRange(), 3.0f)) {
                        newIsNear = false;
                    }
                    if (newIsNear != this.isProximityNear) {
                        FileLog.d("proximity " + newIsNear);
                        this.isProximityNear = newIsNear;
                        try {
                            if (this.isProximityNear) {
                                this.proximityWakelock.acquire();
                            } else {
                                this.proximityWakelock.release(1);
                            }
                        } catch (Throwable x) {
                            FileLog.e(x);
                        }
                    }
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
                        case 7:
                            type = 2;
                            break;
                        case 3:
                        case 5:
                            type = 3;
                            break;
                        case 6:
                        case 8:
                        case 9:
                        case 10:
                        case 12:
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

    private void updateBluetoothHeadsetState(boolean connected) {
        if (connected != this.isBtHeadsetConnected) {
            this.isBtHeadsetConnected = connected;
            AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (connected) {
                am.startBluetoothSco();
            } else {
                am.stopBluetoothSco();
            }
            Iterator it = this.stateListeners.iterator();
            while (it.hasNext()) {
                ((StateListener) it.next()).onAudioSettingsChanged();
            }
        }
    }

    public boolean isBluetoothHeadsetConnected() {
        return this.isBtHeadsetConnected;
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
            try {
                this.proximityWakelock = ((PowerManager) getSystemService("power")).newWakeLock(32, "telegram-voip-prx");
                sm.registerListener(this, proximity, 3);
            } catch (Exception x) {
                FileLog.e("Error initializing proximity sensor", x);
            }
        }
    }

    private void dispatchStateChanged(int state) {
        FileLog.d("== Call " + (this.call != null ? this.call.id : 0) + " state changed to " + state + " ==");
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
                        FileLog.e("error restarting activity", e);
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

    public boolean hasEarpiece() {
        if (((TelephonyManager) getSystemService("phone")).getPhoneType() != 0) {
            return true;
        }
        if (this.mHasEarpiece != null) {
            return this.mHasEarpiece.booleanValue();
        }
        try {
            AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            Method method = AudioManager.class.getMethod("getDevicesForStream", new Class[]{Integer.TYPE});
            int earpieceFlag = AudioManager.class.getField("DEVICE_OUT_EARPIECE").getInt(null);
            if ((((Integer) method.invoke(am, new Object[]{Integer.valueOf(0)})).intValue() & earpieceFlag) == earpieceFlag) {
                this.mHasEarpiece = Boolean.TRUE;
            } else {
                this.mHasEarpiece = Boolean.FALSE;
            }
        } catch (Throwable error) {
            FileLog.e("Error while checking earpiece! ", error);
            this.mHasEarpiece = Boolean.TRUE;
        }
        return this.mHasEarpiece.booleanValue();
    }

    public int getCallState() {
        return this.currentState;
    }

    public byte[] getGA() {
        return this.g_a;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.appDidLogout) {
            callEnded();
        }
    }
}
