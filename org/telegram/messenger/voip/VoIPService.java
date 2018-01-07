package org.telegram.messenger.voip;

import android.app.KeyguardManager;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.voip.VoIPBaseService.StateListener;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
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
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.VoIPActivity;
import org.telegram.ui.VoIPFeedbackActivity;

public class VoIPService extends VoIPBaseService implements NotificationCenterDelegate {
    private static final int CALL_MAX_LAYER = 65;
    private static final int CALL_MIN_LAYER = 65;
    public static final int STATE_BUSY = 17;
    public static final int STATE_EXCHANGING_KEYS = 12;
    public static final int STATE_HANGING_UP = 10;
    public static final int STATE_REQUESTING = 14;
    public static final int STATE_RINGING = 16;
    public static final int STATE_WAITING = 13;
    public static final int STATE_WAITING_INCOMING = 15;
    private static final String TAG = "tg-voip-service";
    public static PhoneCall callIShouldHavePutIntoIntent;
    private byte[] a_or_b;
    private byte[] authKey;
    private PhoneCall call;
    private int callReqId;
    private Runnable delayedStartOutgoingCall;
    private boolean endCallAfterRequest = false;
    private boolean forceRating;
    private byte[] g_a;
    private byte[] g_a_hash;
    private long keyFingerprint;
    private boolean needSendDebugLog = false;
    private ArrayList<PhoneCall> pendingUpdates = new ArrayList();
    private User user;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (sharedInstance == null) {
            this.currentAccount = intent.getIntExtra("account", -1);
            if (this.currentAccount == -1) {
                throw new IllegalStateException("No account specified when starting VoIP service");
            }
            int userID = intent.getIntExtra("user_id", 0);
            this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
            this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userID));
            if (this.user == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("VoIPService: user==null");
                }
                stopSelf();
            } else {
                if (this.isOutgoing) {
                    dispatchStateChanged(14);
                    this.delayedStartOutgoingCall = new Runnable() {
                        public void run() {
                            VoIPService.this.delayedStartOutgoingCall = null;
                            VoIPService.this.startOutgoingCall();
                        }
                    };
                    AndroidUtilities.runOnUIThread(this.delayedStartOutgoingCall, 2000);
                    if (intent.getBooleanExtra("start_incall_activity", false)) {
                        startActivity(new Intent(this, VoIPActivity.class).addFlags(268435456));
                    }
                } else {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeInCallActivity, new Object[0]);
                    this.call = callIShouldHavePutIntoIntent;
                    callIShouldHavePutIntoIntent = null;
                    acknowledgeCallAndStartRinging();
                    if (VERSION.SDK_INT >= 26) {
                        showNotification();
                    }
                }
                sharedInstance = this;
                initializeAccountRelatedThings();
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.e("Tried to start the VoIP service when it's already started");
        }
        return 2;
    }

    protected void updateServerConfig() {
        final SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        VoIPServerConfig.setConfig(preferences.getString("voip_server_config", "{}"));
        if (System.currentTimeMillis() - preferences.getLong("voip_server_config_updated", 0) > 86400000) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_phone_getCallConfig(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        String data = ((TL_dataJSON) response).data;
                        VoIPServerConfig.setConfig(data);
                        preferences.edit().putString("voip_server_config", data).putLong("voip_server_config_updated", BuildConfig.DEBUG ? 0 : System.currentTimeMillis()).commit();
                    }
                }
            });
        }
    }

    protected void onControllerPreRelease() {
        if (this.needSendDebugLog) {
            String debugLog = this.controller.getDebugLog();
            TL_phone_saveCallDebug req = new TL_phone_saveCallDebug();
            req.debug = new TL_dataJSON();
            req.debug.data = debugLog;
            req.peer = new TL_inputPhoneCall();
            req.peer.access_hash = this.call.access_hash;
            req.peer.id = this.call.id;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("Sent debug logs, response=" + response);
                    }
                }
            });
        }
    }

    public static VoIPService getSharedInstance() {
        return sharedInstance instanceof VoIPService ? (VoIPService) sharedInstance : null;
    }

    public User getUser() {
        return this.user;
    }

    public void hangUp() {
        int i = (this.currentState == 16 || (this.currentState == 13 && this.isOutgoing)) ? 3 : 1;
        declineIncomingCall(i, null);
    }

    public void hangUp(Runnable onDone) {
        int i = (this.currentState == 16 || (this.currentState == 13 && this.isOutgoing)) ? 3 : 1;
        declineIncomingCall(i, onDone);
    }

    private void startOutgoingCall() {
        configureDeviceForCall();
        showNotification();
        startConnectingSound();
        dispatchStateChanged(14);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
            }
        });
        Utilities.random.nextBytes(new byte[256]);
        TL_messages_getDhConfig req = new TL_messages_getDhConfig();
        req.random_length = 256;
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        req.version = messagesStorage.getLastSecretVersion();
        this.callReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                VoIPService.this.callReqId = 0;
                if (error == null) {
                    messages_DhConfig res = (messages_DhConfig) response;
                    if (response instanceof TL_messages_dhConfig) {
                        if (Utilities.isGoodPrime(res.p, res.g)) {
                            messagesStorage.setSecretPBytes(res.p);
                            messagesStorage.setSecretG(res.g);
                            messagesStorage.setLastSecretVersion(res.version);
                            messagesStorage.saveSecretParams(messagesStorage.getLastSecretVersion(), messagesStorage.getSecretG(), messagesStorage.getSecretPBytes());
                        } else {
                            VoIPService.this.callFailed();
                            return;
                        }
                    }
                    final byte[] salt = new byte[256];
                    for (int a = 0; a < 256; a++) {
                        salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
                    }
                    byte[] g_a = BigInteger.valueOf((long) messagesStorage.getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, messagesStorage.getSecretPBytes())).toByteArray();
                    if (g_a.length > 256) {
                        byte[] correctedAuth = new byte[256];
                        System.arraycopy(g_a, 1, correctedAuth, 0, 256);
                        g_a = correctedAuth;
                    }
                    TL_phone_requestCall reqCall = new TL_phone_requestCall();
                    reqCall.user_id = MessagesController.getInstance(VoIPService.this.currentAccount).getInputUser(VoIPService.this.user);
                    reqCall.protocol = new TL_phoneCallProtocol();
                    reqCall.protocol.udp_p2p = true;
                    reqCall.protocol.udp_reflector = true;
                    reqCall.protocol.min_layer = 65;
                    reqCall.protocol.max_layer = 65;
                    VoIPService.this.g_a = g_a;
                    reqCall.g_a_hash = Utilities.computeSHA256(g_a, 0, g_a.length);
                    reqCall.random_id = Utilities.random.nextInt();
                    ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(reqCall, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (error == null) {
                                        VoIPService.this.call = ((TL_phone_phoneCall) response).phone_call;
                                        VoIPService.this.a_or_b = salt;
                                        VoIPService.this.dispatchStateChanged(13);
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
                                                ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                                    public void run(TLObject response, TL_error error) {
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            if (error != null) {
                                                                FileLog.e("error on phone.discardCall: " + error);
                                                            } else {
                                                                FileLog.d("phone.discardCall " + response);
                                                            }
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
                                        AndroidUtilities.runOnUIThread(VoIPService.this.timeoutRunnable, (long) MessagesController.getInstance(VoIPService.this.currentAccount).callReceiveTimeout);
                                    } else if (error.code == 400 && "PARTICIPANT_VERSION_OUTDATED".equals(error.text)) {
                                        VoIPService.this.callFailed(-1);
                                    } else if (error.code == 403 && "USER_PRIVACY_RESTRICTED".equals(error.text)) {
                                        VoIPService.this.callFailed(-2);
                                    } else if (error.code == 406) {
                                        VoIPService.this.callFailed(-3);
                                    } else {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.e("Error on phone.requestCall: " + error);
                                        }
                                        VoIPService.this.callFailed();
                                    }
                                }
                            });
                        }
                    }, 2);
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error on getDhConfig " + error);
                }
                VoIPService.this.callFailed();
            }
        }, 2);
    }

    private void acknowledgeCallAndStartRinging() {
        if (this.call instanceof TL_phoneCallDiscarded) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("Call " + this.call.id + " was discarded before the service started, stopping");
            }
            stopSelf();
            return;
        }
        TL_phone_receivedCall req = new TL_phone_receivedCall();
        req.peer = new TL_inputPhoneCall();
        req.peer.id = this.call.id;
        req.peer.access_hash = this.call.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (VoIPBaseService.sharedInstance != null) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.w("receivedCall response = " + response);
                            }
                            if (error != null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.e("error on receivedCall: " + error);
                                }
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
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("starting ringing for call " + this.call.id);
        }
        dispatchStateChanged(15);
        SharedPreferences prefs = MessagesController.getNotificationsSettings(this.currentAccount);
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
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Starting incall activity for incoming call");
            }
            try {
                PendingIntent.getActivity(this, 12345, new Intent(this, VoIPActivity.class).addFlags(268435456), 0).send();
            } catch (Exception x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting incall activity", x);
                }
            }
            if (VERSION.SDK_INT >= 26) {
                showNotification();
                return;
            }
            return;
        }
        showIncomingNotification();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Showing incoming call notification");
        }
    }

    public void acceptIncomingCall() {
        stopRinging();
        showNotification();
        configureDeviceForCall();
        startConnectingSound();
        dispatchStateChanged(12);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
            }
        });
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        TL_messages_getDhConfig req = new TL_messages_getDhConfig();
        req.random_length = 256;
        req.version = messagesStorage.getLastSecretVersion();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    messages_DhConfig res = (messages_DhConfig) response;
                    if (response instanceof TL_messages_dhConfig) {
                        if (Utilities.isGoodPrime(res.p, res.g)) {
                            messagesStorage.setSecretPBytes(res.p);
                            messagesStorage.setSecretG(res.g);
                            messagesStorage.setLastSecretVersion(res.version);
                            MessagesStorage.getInstance(VoIPService.this.currentAccount).saveSecretParams(messagesStorage.getLastSecretVersion(), messagesStorage.getSecretG(), messagesStorage.getSecretPBytes());
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("stopping VoIP service, bad prime");
                            }
                            VoIPService.this.callFailed();
                            return;
                        }
                    }
                    byte[] salt = new byte[256];
                    for (int a = 0; a < 256; a++) {
                        salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
                    }
                    if (VoIPService.this.call == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("call is null");
                        }
                        VoIPService.this.callFailed();
                        return;
                    }
                    VoIPService.this.a_or_b = salt;
                    BigInteger g_b = BigInteger.valueOf((long) messagesStorage.getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, messagesStorage.getSecretPBytes()));
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
                    ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (error == null) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.w("accept call ok! " + response);
                                        }
                                        VoIPService.this.call = ((TL_phone_phoneCall) response).phone_call;
                                        if (VoIPService.this.call instanceof TL_phoneCallDiscarded) {
                                            VoIPService.this.onCallUpdated(VoIPService.this.call);
                                            return;
                                        }
                                        return;
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("Error on phone.acceptCall: " + error);
                                    }
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
        if (this.currentState == 14) {
            if (this.delayedStartOutgoingCall != null) {
                AndroidUtilities.cancelRunOnUIThread(this.delayedStartOutgoingCall);
                callEnded();
                return;
            }
            dispatchStateChanged(10);
            this.endCallAfterRequest = true;
        } else if (this.currentState != 10 && this.currentState != 11) {
            dispatchStateChanged(10);
            if (this.call == null) {
                if (onDone != null) {
                    onDone.run();
                }
                callEnded();
                if (this.callReqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.callReqId, false);
                    this.callReqId = 0;
                    return;
                }
                return;
            }
            int i;
            Runnable stopper;
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
            if (ConnectionsManager.getInstance(this.currentAccount).getConnectionState() == 3) {
                wasNotConnected = false;
            }
            if (wasNotConnected) {
                if (onDone != null) {
                    onDone.run();
                }
                callEnded();
                stopper = null;
            } else {
                stopper = new Runnable() {
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
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        if (response instanceof TL_updates) {
                            MessagesController.getInstance(VoIPService.this.currentAccount).processUpdates((TL_updates) response, false);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("phone.discardCall " + response);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("error on phone.discardCall: " + error);
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
            if (BuildVars.LOGS_ENABLED) {
                for (Field f : PhoneCall.class.getFields()) {
                    FileLog.d(f.getName() + " = " + f.get(this.call));
                }
            }
        } catch (Throwable x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(x);
            }
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
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("Call updated: " + call);
                    dumpCallObject();
                }
                this.call = call;
                if (call instanceof TL_phoneCallDiscarded) {
                    this.needSendDebugLog = call.need_debug;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("call discarded, stopping service");
                    }
                    if (call.reason instanceof TL_phoneCallDiscardReasonBusy) {
                        dispatchStateChanged(17);
                        this.playingSound = true;
                        this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
                        AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1500);
                        stopSelf();
                    } else {
                        callEnded();
                    }
                    if (call.need_rating || this.forceRating) {
                        startRatingActivity();
                    }
                } else if ((call instanceof TL_phoneCall) && this.authKey == null) {
                    if (call.g_a_or_b == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("stopping VoIP service, Ga == null");
                        }
                        callFailed();
                    } else if (Arrays.equals(this.g_a_hash, Utilities.computeSHA256(call.g_a_or_b, 0, call.g_a_or_b.length))) {
                        this.g_a = call.g_a_or_b;
                        BigInteger g_a = new BigInteger(1, call.g_a_or_b);
                        BigInteger p = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                        if (Utilities.isGoodGaAndGb(g_a, p)) {
                            byte[] authKey = g_a.modPow(new BigInteger(1, this.a_or_b), p).toByteArray();
                            byte[] correctedAuth;
                            if (authKey.length > 256) {
                                correctedAuth = new byte[256];
                                System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
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
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.w("key fingerprints don't match");
                                }
                                callFailed();
                                return;
                            }
                            initiateActualEncryptedCall();
                            return;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("stopping VoIP service, bad Ga and Gb (accepting)");
                        }
                        callFailed();
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("stopping VoIP service, Ga hash doesn't match");
                        }
                        callFailed();
                    }
                } else if ((call instanceof TL_phoneCallAccepted) && this.authKey == null) {
                    processAcceptedCall();
                } else if (this.currentState == 13 && call.receive_date != 0) {
                    dispatchStateChanged(16);
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("!!!!!! CALL RECEIVED");
                    }
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
                    AndroidUtilities.runOnUIThread(this.timeoutRunnable, (long) MessagesController.getInstance(this.currentAccount).callRingTimeout);
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.w("onCallUpdated called with wrong call id (got " + call.id + ", expected " + this.call.id + ")");
            }
        }
    }

    private void startRatingActivity() {
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).putExtra("call_id", this.call.id).putExtra("call_access_hash", this.call.access_hash).putExtra("account", this.currentAccount).addFlags(805306368), 0).send();
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error starting incall activity", x);
            }
        }
    }

    public byte[] getEncryptionKey() {
        return this.authKey;
    }

    private void processAcceptedCall() {
        dispatchStateChanged(12);
        BigInteger p = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
        BigInteger i_authKey = new BigInteger(1, this.call.g_b);
        if (Utilities.isGoodGaAndGb(i_authKey, p)) {
            byte[] authKey = i_authKey.modPow(new BigInteger(1, this.a_or_b), p).toByteArray();
            byte[] correctedAuth;
            if (authKey.length > 256) {
                correctedAuth = new byte[256];
                System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
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
        if (BuildVars.LOGS_ENABLED) {
            FileLog.w("stopping VoIP service, bad Ga and Gb");
        }
        callFailed();
    }

    private void initiateActualEncryptedCall() {
        if (this.timeoutRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
            this.timeoutRunnable = null;
        }
        try {
            int i;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("InitCall: keyID=" + this.keyFingerprint);
            }
            SharedPreferences nprefs = MessagesController.getNotificationsSettings(this.currentAccount);
            HashSet<String> hashes = new HashSet(nprefs.getStringSet("calls_access_hashes", Collections.EMPTY_SET));
            hashes.add(this.call.id + " " + this.call.access_hash + " " + System.currentTimeMillis());
            while (hashes.size() > 20) {
                String oldest = null;
                long oldestTime = Long.MAX_VALUE;
                Iterator<String> itr = hashes.iterator();
                while (itr.hasNext()) {
                    String item = (String) itr.next();
                    String[] s = item.split(" ");
                    if (s.length < 2) {
                        itr.remove();
                    } else {
                        try {
                            long t = Long.parseLong(s[2]);
                            if (t < oldestTime) {
                                oldestTime = t;
                                oldest = item;
                            }
                        } catch (Exception e) {
                            itr.remove();
                        }
                    }
                }
                if (oldest != null) {
                    hashes.remove(oldest);
                }
            }
            nprefs.edit().putStringSet("calls_access_hashes", hashes).commit();
            this.controller.setConfig(((double) MessagesController.getInstance(this.currentAccount).callPacketTimeout) / 1000.0d, ((double) MessagesController.getInstance(this.currentAccount).callConnectTimeout) / 1000.0d, MessagesController.getGlobalMainSettings().getInt("VoipDataSaving", 0), this.call.id);
            this.controller.setEncryptionKey(this.authKey, this.isOutgoing);
            TL_phoneConnection[] endpoints = new TL_phoneConnection[(this.call.alternative_connections.size() + 1)];
            endpoints[0] = this.call.connection;
            for (int i2 = 0; i2 < this.call.alternative_connections.size(); i2++) {
                endpoints[i2 + 1] = (TL_phoneConnection) this.call.alternative_connections.get(i2);
            }
            VoIPHelper.upgradeP2pSetting(this.currentAccount);
            boolean allowP2p = true;
            SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
            String str = "calls_p2p_new";
            if (MessagesController.getInstance(this.currentAccount).defaultP2pContacts) {
                i = 1;
            } else {
                i = 0;
            }
            switch (mainSettings.getInt(str, i)) {
                case 0:
                    allowP2p = true;
                    break;
                case 1:
                    allowP2p = ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.user.id)) != null;
                    break;
                case 2:
                    allowP2p = false;
                    break;
            }
            VoIPController voIPController = this.controller;
            boolean z = this.call.protocol.udp_p2p && allowP2p;
            voIPController.setRemoteEndpoints(endpoints, z);
            SharedPreferences prefs = MessagesController.getGlobalMainSettings();
            if (prefs.getBoolean("proxy_enabled", false)) {
                if (prefs.getBoolean("proxy_enabled_calls", false)) {
                    String server = prefs.getString("proxy_ip", null);
                    if (server != null) {
                        this.controller.setProxy(server, prefs.getInt("proxy_port", 0), prefs.getString("proxy_user", null), prefs.getString("proxy_pass", null));
                    }
                }
            }
            this.controller.start();
            updateNetworkType();
            this.controller.connect();
            this.controllerStarted = true;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (VoIPService.this.controller != null) {
                        VoIPService.this.updateStats();
                        AndroidUtilities.runOnUIThread(this, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                    }
                }
            }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } catch (Throwable x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error starting call", x);
            }
            callFailed();
        }
    }

    protected void showNotification() {
        showNotification(ContactsController.formatName(this.user.first_name, this.user.last_name), this.user.photo != null ? this.user.photo.photo_small : null, VoIPActivity.class);
    }

    private void showIncomingNotification() {
        Intent intent = new Intent(this, VoIPActivity.class);
        intent.addFlags(805306368);
        Builder builder = new Builder(this).setContentText(LocaleController.getString("VoipInCallBranding", R.string.VoipInCallBranding)).setContentTitle(ContactsController.formatName(this.user.first_name, this.user.last_name)).setSmallIcon(R.drawable.notification).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        if (VERSION.SDK_INT >= 26) {
            SharedPreferences nprefs = MessagesController.getGlobalNotificationsSettings();
            int chanIndex = nprefs.getInt("calls_notification_channel", 0);
            NotificationManager nm = (NotificationManager) getSystemService("notification");
            NotificationChannel existingChannel = nm.getNotificationChannel("incoming_calls" + chanIndex);
            boolean needCreate = true;
            if (existingChannel != null) {
                if (existingChannel.getImportance() >= 4 && existingChannel.getSound() == null && existingChannel.getVibrationPattern() == null) {
                    needCreate = false;
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("User messed up the notification channel; deleting it and creating a proper one");
                    }
                    nm.deleteNotificationChannel("incoming_calls" + chanIndex);
                    chanIndex++;
                    nprefs.edit().putInt("calls_notification_channel", chanIndex).commit();
                }
            }
            if (needCreate) {
                NotificationChannel chan = new NotificationChannel("incoming_calls" + chanIndex, LocaleController.getString("IncomingCalls", R.string.IncomingCalls), 4);
                chan.setSound(null, null);
                chan.enableVibration(false);
                chan.enableLights(false);
                nm.createNotificationChannel(chan);
            }
            builder.setChannelId("incoming_calls" + chanIndex);
        }
        Intent endIntent = new Intent(this, VoIPActionsReceiver.class);
        endIntent.setAction(getPackageName() + ".DECLINE_CALL");
        endIntent.putExtra("call_id", getCallID());
        endIntent.setFlags(268435456);
        CharSequence endTitle = LocaleController.getString("VoipDeclineCall", R.string.VoipDeclineCall);
        if (VERSION.SDK_INT >= 24) {
            CharSequence endTitle2 = new SpannableString(endTitle);
            ((SpannableString) endTitle2).setSpan(new ForegroundColorSpan(-769226), 0, endTitle2.length(), 0);
            endTitle = endTitle2;
        }
        builder.addAction(R.drawable.ic_call_end_white_24dp, endTitle, PendingIntent.getBroadcast(this, 0, endIntent, 134217728));
        Intent answerIntent = new Intent(this, VoIPActionsReceiver.class);
        answerIntent.setAction(getPackageName() + ".ANSWER_CALL");
        answerIntent.putExtra("call_id", getCallID());
        answerIntent.setFlags(268435456);
        CharSequence answerTitle = LocaleController.getString("VoipAnswerCall", R.string.VoipAnswerCall);
        if (VERSION.SDK_INT >= 24) {
            CharSequence answerTitle2 = new SpannableString(answerTitle);
            ((SpannableString) answerTitle2).setSpan(new ForegroundColorSpan(-16733696), 0, answerTitle2.length(), 0);
            answerTitle = answerTitle2;
        }
        builder.addAction(R.drawable.ic_call_white_24dp, answerTitle, PendingIntent.getBroadcast(this, 0, answerIntent, 134217728));
        builder.setPriority(2);
        if (UserConfig.getActivatedAccountsCount() > 1) {
            User self = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            builder.setSubText(LocaleController.formatString("VoipAnsweringAsAccount", R.string.VoipAnsweringAsAccount, ContactsController.formatName(self.first_name, self.last_name)));
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
        startForeground(202, builder.getNotification());
    }

    private void startConnectingSound() {
        if (this.spPlayID != 0) {
            this.soundPool.stop(this.spPlayID);
        }
        this.spPlayID = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        if (this.spPlayID == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (VoIPBaseService.sharedInstance != null) {
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

    protected void callFailed(int errorCode) {
        if (this.call != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Discarding failed call");
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("error on phone.discardCall: " + error);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("phone.discardCall " + response);
                    }
                }
            });
        }
        super.callFailed(errorCode);
    }

    protected long getCallID() {
        return this.call != null ? this.call.id : 0;
    }

    public void onUIForegroundStateChanged(boolean isForeground) {
        if (this.currentState != 15) {
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
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("error restarting activity", e);
                        }
                    }
                    if (VERSION.SDK_INT >= 26) {
                        VoIPService.this.showNotification();
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
        if (this.currentState == 15) {
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

    public byte[] getGA() {
        return this.g_a;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.appDidLogout) {
            callEnded();
        }
    }

    public void forceRating() {
        this.forceRating = true;
    }
}
