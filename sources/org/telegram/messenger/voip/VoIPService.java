package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.view.KeyEvent;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.voip.VoIPBaseService.CallConnection;
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

public class VoIPService extends VoIPBaseService {
    public static final int CALL_MAX_LAYER = 74;
    public static final int CALL_MIN_LAYER = 65;
    public static final int STATE_BUSY = 17;
    public static final int STATE_EXCHANGING_KEYS = 12;
    public static final int STATE_HANGING_UP = 10;
    public static final int STATE_REQUESTING = 14;
    public static final int STATE_RINGING = 16;
    public static final int STATE_WAITING = 13;
    public static final int STATE_WAITING_INCOMING = 15;
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
    private byte[] groupCallEncryptionKey;
    private long groupCallKeyFingerprint;
    private List<Integer> groupUsersToAdd = new ArrayList();
    private boolean joiningGroupCall;
    private long keyFingerprint;
    private boolean needSendDebugLog = false;
    private int peerCapabilities;
    private ArrayList<PhoneCall> pendingUpdates = new ArrayList();
    private boolean upgrading;
    private User user;

    /* renamed from: org.telegram.messenger.voip.VoIPService$1 */
    class C06821 implements Runnable {
        C06821() {
        }

        public void run() {
            VoIPService.this.delayedStartOutgoingCall = null;
            VoIPService.this.startOutgoingCall();
        }
    }

    /* renamed from: org.telegram.messenger.voip.VoIPService$4 */
    class C06834 implements Runnable {
        C06834() {
        }

        public void run() {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
        }
    }

    /* renamed from: org.telegram.messenger.voip.VoIPService$7 */
    class C06887 implements Runnable {
        C06887() {
        }

        public void run() {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
        }
    }

    /* renamed from: org.telegram.messenger.voip.VoIPService$3 */
    class C18803 implements RequestDelegate {
        C18803() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (BuildVars.LOGS_ENABLED != null) {
                tL_error = new StringBuilder();
                tL_error.append("Sent debug logs, response=");
                tL_error.append(tLObject);
                FileLog.m0d(tL_error.toString());
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint({"MissingPermission"})
    public int onStartCommand(Intent intent, int i, int i2) {
        if (sharedInstance != 0) {
            if (BuildVars.LOGS_ENABLED != null) {
                FileLog.m1e("Tried to start the VoIP service when it's already started");
            }
            return 2;
        }
        this.currentAccount = intent.getIntExtra("account", -1);
        if (this.currentAccount == -1) {
            throw new IllegalStateException("No account specified when starting VoIP service");
        }
        i = intent.getIntExtra("user_id", 0);
        this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
        this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
        if (this.user == 0) {
            if (BuildVars.LOGS_ENABLED != null) {
                FileLog.m4w("VoIPService: user==null");
            }
            stopSelf();
            return 2;
        }
        sharedInstance = this;
        if (this.isOutgoing != 0) {
            dispatchStateChanged(14);
            this.delayedStartOutgoingCall = new C06821();
            AndroidUtilities.runOnUIThread(this.delayedStartOutgoingCall, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            if (intent.getBooleanExtra("start_incall_activity", false) != null) {
                startActivity(new Intent(this, VoIPActivity.class).addFlags(268435456));
            }
        } else {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeInCallActivity, new Object[0]);
            this.call = callIShouldHavePutIntoIntent;
            callIShouldHavePutIntoIntent = null;
            acknowledgeCall(true);
        }
        initializeAccountRelatedThings();
        return 2;
    }

    public void onCreate() {
        super.onCreate();
        if (callIShouldHavePutIntoIntent != null && VERSION.SDK_INT >= 26) {
            startForeground(201, new Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setSmallIcon(C0446R.drawable.notification).setContentTitle(LocaleController.getString("VoipOutgoingCall", C0446R.string.VoipOutgoingCall)).setShowWhen(false).build());
        }
    }

    protected void updateServerConfig() {
        final SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        VoIPServerConfig.setConfig(mainSettings.getString("voip_server_config", "{}"));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_phone_getCallConfig(), new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    tLObject = ((TL_dataJSON) tLObject).data;
                    VoIPServerConfig.setConfig(tLObject);
                    mainSettings.edit().putString("voip_server_config", tLObject).commit();
                }
            }
        });
    }

    protected void onControllerPreRelease() {
        if (this.needSendDebugLog) {
            String debugLog = this.controller.getDebugLog();
            TLObject tL_phone_saveCallDebug = new TL_phone_saveCallDebug();
            tL_phone_saveCallDebug.debug = new TL_dataJSON();
            tL_phone_saveCallDebug.debug.data = debugLog;
            tL_phone_saveCallDebug.peer = new TL_inputPhoneCall();
            tL_phone_saveCallDebug.peer.access_hash = this.call.access_hash;
            tL_phone_saveCallDebug.peer.id = this.call.id;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_phone_saveCallDebug, new C18803());
        }
    }

    public static VoIPService getSharedInstance() {
        return sharedInstance instanceof VoIPService ? (VoIPService) sharedInstance : null;
    }

    public User getUser() {
        return this.user;
    }

    public void hangUp() {
        int i;
        if (this.currentState != 16) {
            if (this.currentState != 13 || !this.isOutgoing) {
                i = 1;
                declineIncomingCall(i, null);
            }
        }
        i = 3;
        declineIncomingCall(i, null);
    }

    public void hangUp(Runnable runnable) {
        int i;
        if (this.currentState != 16) {
            if (this.currentState != 13 || !this.isOutgoing) {
                i = 1;
                declineIncomingCall(i, runnable);
            }
        }
        i = 3;
        declineIncomingCall(i, runnable);
    }

    private void startOutgoingCall() {
        configureDeviceForCall();
        showNotification();
        startConnectingSound();
        dispatchStateChanged(14);
        AndroidUtilities.runOnUIThread(new C06834());
        Utilities.random.nextBytes(new byte[256]);
        TLObject tL_messages_getDhConfig = new TL_messages_getDhConfig();
        tL_messages_getDhConfig.random_length = 256;
        final MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
        tL_messages_getDhConfig.version = instance.getLastSecretVersion();
        this.callReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getDhConfig, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                VoIPService.this.callReqId = 0;
                if (tL_error == null) {
                    messages_DhConfig messages_dhconfig = (messages_DhConfig) tLObject;
                    if ((tLObject instanceof TL_messages_dhConfig) != null) {
                        if (Utilities.isGoodPrime(messages_dhconfig.f56p, messages_dhconfig.f55g) == null) {
                            VoIPService.this.callFailed();
                            return;
                        }
                        instance.setSecretPBytes(messages_dhconfig.f56p);
                        instance.setSecretG(messages_dhconfig.f55g);
                        instance.setLastSecretVersion(messages_dhconfig.version);
                        instance.saveSecretParams(instance.getLastSecretVersion(), instance.getSecretG(), instance.getSecretPBytes());
                    }
                    final byte[] bArr = new byte[256];
                    for (int i = 0; i < 256; i++) {
                        bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ messages_dhconfig.random[i]);
                    }
                    tL_error = BigInteger.valueOf((long) instance.getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, instance.getSecretPBytes())).toByteArray();
                    if (tL_error.length > 256) {
                        TL_error tL_error2 = new byte[256];
                        System.arraycopy(tL_error, 1, tL_error2, 0, 256);
                        tL_error = tL_error2;
                    }
                    tLObject = new TL_phone_requestCall();
                    tLObject.user_id = MessagesController.getInstance(VoIPService.this.currentAccount).getInputUser(VoIPService.this.user);
                    tLObject.protocol = new TL_phoneCallProtocol();
                    tLObject.protocol.udp_p2p = true;
                    tLObject.protocol.udp_reflector = true;
                    tLObject.protocol.min_layer = 65;
                    tLObject.protocol.max_layer = 74;
                    VoIPService.this.g_a = tL_error;
                    tLObject.g_a_hash = Utilities.computeSHA256(tL_error, 0, tL_error.length);
                    tLObject.random_id = Utilities.random.nextInt();
                    ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(tLObject, new RequestDelegate() {
                        public void run(final TLObject tLObject, final TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.messenger.voip.VoIPService$5$1$1$1 */
                                class C06851 implements Runnable {

                                    /* renamed from: org.telegram.messenger.voip.VoIPService$5$1$1$1$1 */
                                    class C18811 implements RequestDelegate {

                                        /* renamed from: org.telegram.messenger.voip.VoIPService$5$1$1$1$1$1 */
                                        class C06841 implements Runnable {
                                            C06841() {
                                            }

                                            public void run() {
                                                VoIPService.this.callFailed();
                                            }
                                        }

                                        C18811() {
                                        }

                                        public void run(TLObject tLObject, TL_error tL_error) {
                                            if (BuildVars.LOGS_ENABLED) {
                                                if (tL_error != null) {
                                                    tLObject = new StringBuilder();
                                                    tLObject.append("error on phone.discardCall: ");
                                                    tLObject.append(tL_error);
                                                    FileLog.m1e(tLObject.toString());
                                                } else {
                                                    tL_error = new StringBuilder();
                                                    tL_error.append("phone.discardCall ");
                                                    tL_error.append(tLObject);
                                                    FileLog.m0d(tL_error.toString());
                                                }
                                            }
                                            AndroidUtilities.runOnUIThread(new C06841());
                                        }
                                    }

                                    C06851() {
                                    }

                                    public void run() {
                                        VoIPService.this.timeoutRunnable = null;
                                        TLObject tL_phone_discardCall = new TL_phone_discardCall();
                                        tL_phone_discardCall.peer = new TL_inputPhoneCall();
                                        tL_phone_discardCall.peer.access_hash = VoIPService.this.call.access_hash;
                                        tL_phone_discardCall.peer.id = VoIPService.this.call.id;
                                        tL_phone_discardCall.reason = new TL_phoneCallDiscardReasonMissed();
                                        ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(tL_phone_discardCall, new C18811(), 2);
                                    }
                                }

                                public void run() {
                                    if (tL_error == null) {
                                        VoIPService.this.call = ((TL_phone_phoneCall) tLObject).phone_call;
                                        VoIPService.this.a_or_b = bArr;
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
                                        VoIPService.this.timeoutRunnable = new C06851();
                                        AndroidUtilities.runOnUIThread(VoIPService.this.timeoutRunnable, (long) MessagesController.getInstance(VoIPService.this.currentAccount).callReceiveTimeout);
                                    } else if (tL_error.code == 400 && "PARTICIPANT_VERSION_OUTDATED".equals(tL_error.text)) {
                                        VoIPService.this.callFailed(-1);
                                    } else if (tL_error.code == 403 && "USER_PRIVACY_RESTRICTED".equals(tL_error.text)) {
                                        VoIPService.this.callFailed(-2);
                                    } else if (tL_error.code == 406) {
                                        VoIPService.this.callFailed(-3);
                                    } else {
                                        if (BuildVars.LOGS_ENABLED) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append("Error on phone.requestCall: ");
                                            stringBuilder.append(tL_error);
                                            FileLog.m1e(stringBuilder.toString());
                                        }
                                        VoIPService.this.callFailed();
                                    }
                                }
                            });
                        }
                    }, 2);
                } else {
                    if (BuildVars.LOGS_ENABLED != null) {
                        tLObject = new StringBuilder();
                        tLObject.append("Error on getDhConfig ");
                        tLObject.append(tL_error);
                        FileLog.m1e(tLObject.toString());
                    }
                    VoIPService.this.callFailed();
                }
            }
        }, 2);
    }

    private void acknowledgeCall(final boolean z) {
        if (this.call instanceof TL_phoneCallDiscarded) {
            if (BuildVars.LOGS_ENABLED) {
                z = new StringBuilder();
                z.append("Call ");
                z.append(this.call.id);
                z.append(" was discarded before the service started, stopping");
                FileLog.m4w(z.toString());
            }
            stopSelf();
            return;
        }
        TLObject tL_phone_receivedCall = new TL_phone_receivedCall();
        tL_phone_receivedCall.peer = new TL_inputPhoneCall();
        tL_phone_receivedCall.peer.id = this.call.id;
        tL_phone_receivedCall.peer.access_hash = this.call.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_phone_receivedCall, new RequestDelegate() {
            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (VoIPBaseService.sharedInstance != null) {
                            StringBuilder stringBuilder;
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("receivedCall response = ");
                                stringBuilder.append(tLObject);
                                FileLog.m4w(stringBuilder.toString());
                            }
                            if (tL_error != null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("error on receivedCall: ");
                                    stringBuilder.append(tL_error);
                                    FileLog.m1e(stringBuilder.toString());
                                }
                                VoIPService.this.stopSelf();
                            } else if (z) {
                                VoIPService.this.startRinging();
                            }
                        }
                    }
                });
            }
        }, true);
    }

    protected void startRinging() {
        if (this.currentState != 15) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("starting ringing for call ");
                stringBuilder.append(this.call.id);
                FileLog.m0d(stringBuilder.toString());
            }
            dispatchStateChanged(15);
            startRingtoneAndVibration(this.user.id);
            if (VERSION.SDK_INT < 21 || ((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode() || !NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("Starting incall activity for incoming call");
                }
                try {
                    PendingIntent.getActivity(this, 12345, new Intent(this, VoIPActivity.class).addFlags(268435456), 0).send();
                } catch (Throwable e) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m2e("Error starting incall activity", e);
                    }
                }
                if (VERSION.SDK_INT >= 26) {
                    showNotification();
                }
            } else {
                showIncomingNotification(ContactsController.formatName(this.user.first_name, this.user.last_name), null, this.user, null, 0, VoIPActivity.class);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("Showing incoming call notification");
                }
            }
        }
    }

    public void acceptIncomingCall() {
        stopRinging();
        showNotification();
        configureDeviceForCall();
        startConnectingSound();
        dispatchStateChanged(12);
        AndroidUtilities.runOnUIThread(new C06887());
        final MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
        TLObject tL_messages_getDhConfig = new TL_messages_getDhConfig();
        tL_messages_getDhConfig.random_length = 256;
        tL_messages_getDhConfig.version = instance.getLastSecretVersion();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getDhConfig, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.voip.VoIPService$8$1 */
            class C18851 implements RequestDelegate {
                C18851() {
                }

                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (tL_error == null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("accept call ok! ");
                                    stringBuilder.append(tLObject);
                                    FileLog.m4w(stringBuilder.toString());
                                }
                                VoIPService.this.call = ((TL_phone_phoneCall) tLObject).phone_call;
                                if (VoIPService.this.call instanceof TL_phoneCallDiscarded) {
                                    VoIPService.this.onCallUpdated(VoIPService.this.call);
                                    return;
                                }
                                return;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Error on phone.acceptCall: ");
                                stringBuilder.append(tL_error);
                                FileLog.m1e(stringBuilder.toString());
                            }
                            VoIPService.this.callFailed();
                        }
                    });
                }
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    messages_DhConfig messages_dhconfig = (messages_DhConfig) tLObject;
                    if ((tLObject instanceof TL_messages_dhConfig) != null) {
                        if (Utilities.isGoodPrime(messages_dhconfig.f56p, messages_dhconfig.f55g) == null) {
                            if (BuildVars.LOGS_ENABLED != null) {
                                FileLog.m1e("stopping VoIP service, bad prime");
                            }
                            VoIPService.this.callFailed();
                            return;
                        }
                        instance.setSecretPBytes(messages_dhconfig.f56p);
                        instance.setSecretG(messages_dhconfig.f55g);
                        instance.setLastSecretVersion(messages_dhconfig.version);
                        MessagesStorage.getInstance(VoIPService.this.currentAccount).saveSecretParams(instance.getLastSecretVersion(), instance.getSecretG(), instance.getSecretPBytes());
                    }
                    byte[] bArr = new byte[256];
                    for (int i = 0; i < 256; i++) {
                        bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ messages_dhconfig.random[i]);
                    }
                    if (VoIPService.this.call == null) {
                        if (BuildVars.LOGS_ENABLED != null) {
                            FileLog.m1e("call is null");
                        }
                        VoIPService.this.callFailed();
                        return;
                    }
                    VoIPService.this.a_or_b = bArr;
                    tL_error = BigInteger.valueOf((long) instance.getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, instance.getSecretPBytes()));
                    VoIPService.this.g_a_hash = VoIPService.this.call.g_a_hash;
                    tL_error = tL_error.toByteArray();
                    if (tL_error.length > 256) {
                        TL_error tL_error2 = new byte[256];
                        System.arraycopy(tL_error, 1, tL_error2, 0, 256);
                        tL_error = tL_error2;
                    }
                    tLObject = new TL_phone_acceptCall();
                    tLObject.g_b = tL_error;
                    tLObject.peer = new TL_inputPhoneCall();
                    tLObject.peer.id = VoIPService.this.call.id;
                    tLObject.peer.access_hash = VoIPService.this.call.access_hash;
                    tLObject.protocol = new TL_phoneCallProtocol();
                    tL_error = tLObject.protocol;
                    tLObject.protocol.udp_reflector = true;
                    tL_error.udp_p2p = true;
                    tLObject.protocol.min_layer = 65;
                    tLObject.protocol.max_layer = 74;
                    ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(tLObject, new C18851(), 2);
                } else {
                    VoIPService.this.callFailed();
                }
            }
        });
    }

    public void declineIncomingCall() {
        declineIncomingCall(1, null);
    }

    protected Class<? extends Activity> getUIActivityClass() {
        return VoIPActivity.class;
    }

    public void declineIncomingCall(int i, final Runnable runnable) {
        stopRinging();
        this.callDiscardReason = i;
        boolean z = true;
        if (this.currentState == 14) {
            if (this.delayedStartOutgoingCall != 0) {
                AndroidUtilities.cancelRunOnUIThread(this.delayedStartOutgoingCall);
                callEnded();
            } else {
                dispatchStateChanged(10);
                this.endCallAfterRequest = true;
            }
            return;
        }
        if (this.currentState != 10) {
            if (this.currentState != 11) {
                dispatchStateChanged(10);
                if (this.call == null) {
                    if (runnable != null) {
                        runnable.run();
                    }
                    callEnded();
                    if (this.callReqId != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.callReqId, false);
                        this.callReqId = 0;
                    }
                    return;
                }
                TLObject tL_phone_discardCall = new TL_phone_discardCall();
                tL_phone_discardCall.peer = new TL_inputPhoneCall();
                tL_phone_discardCall.peer.access_hash = this.call.access_hash;
                tL_phone_discardCall.peer.id = this.call.id;
                int callDuration = (this.controller == null || !this.controllerStarted) ? 0 : (int) (this.controller.getCallDuration() / 1000);
                tL_phone_discardCall.duration = callDuration;
                long preferredRelayID = (this.controller == null || !this.controllerStarted) ? 0 : this.controller.getPreferredRelayID();
                tL_phone_discardCall.connection_id = preferredRelayID;
                switch (i) {
                    case 2:
                        tL_phone_discardCall.reason = new TL_phoneCallDiscardReasonDisconnect();
                        break;
                    case 3:
                        tL_phone_discardCall.reason = new TL_phoneCallDiscardReasonMissed();
                        break;
                    case 4:
                        tL_phone_discardCall.reason = new TL_phoneCallDiscardReasonBusy();
                        break;
                    default:
                        tL_phone_discardCall.reason = new TL_phoneCallDiscardReasonHangup();
                        break;
                }
                if (ConnectionsManager.getInstance(this.currentAccount).getConnectionState() == 3) {
                    z = false;
                }
                if (z) {
                    if (runnable != null) {
                        runnable.run();
                    }
                    callEnded();
                    i = 0;
                } else {
                    i = new Runnable() {
                        private boolean done = null;

                        public void run() {
                            if (!this.done) {
                                this.done = true;
                                if (runnable != null) {
                                    runnable.run();
                                }
                                VoIPService.this.callEnded();
                            }
                        }
                    };
                    AndroidUtilities.runOnUIThread(i, (long) ((int) (VoIPServerConfig.getDouble("hangup_ui_timeout", 5.0d) * 1000.0d)));
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_phone_discardCall, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            if ((tLObject instanceof TL_updates) != null) {
                                MessagesController.getInstance(VoIPService.this.currentAccount).processUpdates((TL_updates) tLObject, false);
                            }
                            if (BuildVars.LOGS_ENABLED != null) {
                                tL_error = new StringBuilder();
                                tL_error.append("phone.discardCall ");
                                tL_error.append(tLObject);
                                FileLog.m0d(tL_error.toString());
                            }
                        } else if (BuildVars.LOGS_ENABLED != null) {
                            tLObject = new StringBuilder();
                            tLObject.append("error on phone.discardCall: ");
                            tLObject.append(tL_error);
                            FileLog.m1e(tLObject.toString());
                        }
                        if (z == null) {
                            AndroidUtilities.cancelRunOnUIThread(i);
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    }
                }, 2);
            }
        }
    }

    private void dumpCallObject() {
        try {
            if (BuildVars.LOGS_ENABLED) {
                for (Field field : PhoneCall.class.getFields()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(field.getName());
                    stringBuilder.append(" = ");
                    stringBuilder.append(field.get(this.call));
                    FileLog.m0d(stringBuilder.toString());
                }
            }
        } catch (Throwable e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m3e(e);
            }
        }
    }

    public void onCallUpdated(PhoneCall phoneCall) {
        if (this.call == null) {
            this.pendingUpdates.add(phoneCall);
        } else if (phoneCall != null) {
            StringBuilder stringBuilder;
            if (phoneCall.id != this.call.id) {
                if (BuildVars.LOGS_ENABLED) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("onCallUpdated called with wrong call id (got ");
                    stringBuilder.append(phoneCall.id);
                    stringBuilder.append(", expected ");
                    stringBuilder.append(this.call.id);
                    stringBuilder.append(")");
                    FileLog.m4w(stringBuilder.toString());
                }
                return;
            }
            if (phoneCall.access_hash == 0) {
                phoneCall.access_hash = this.call.access_hash;
            }
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Call updated: ");
                stringBuilder.append(phoneCall);
                FileLog.m0d(stringBuilder.toString());
                dumpCallObject();
            }
            this.call = phoneCall;
            if (phoneCall instanceof TL_phoneCallDiscarded) {
                this.needSendDebugLog = phoneCall.need_debug;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("call discarded, stopping service");
                }
                if (phoneCall.reason instanceof TL_phoneCallDiscardReasonBusy) {
                    dispatchStateChanged(17);
                    this.playingSound = true;
                    this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
                    AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1500);
                    stopSelf();
                } else {
                    callEnded();
                }
                if (!(phoneCall.need_rating == null && this.forceRating == null)) {
                    startRatingActivity();
                }
            } else if ((phoneCall instanceof TL_phoneCall) && this.authKey == null) {
                if (phoneCall.g_a_or_b == null) {
                    if (BuildVars.LOGS_ENABLED != null) {
                        FileLog.m4w("stopping VoIP service, Ga == null");
                    }
                    callFailed();
                } else if (Arrays.equals(this.g_a_hash, Utilities.computeSHA256(phoneCall.g_a_or_b, 0, phoneCall.g_a_or_b.length))) {
                    this.g_a = phoneCall.g_a_or_b;
                    BigInteger bigInteger = new BigInteger(1, phoneCall.g_a_or_b);
                    BigInteger bigInteger2 = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                    if (Utilities.isGoodGaAndGb(bigInteger, bigInteger2)) {
                        byte[] bArr;
                        Object toByteArray = bigInteger.modPow(new BigInteger(1, this.a_or_b), bigInteger2).toByteArray();
                        if (toByteArray.length > 256) {
                            bArr = new byte[256];
                            System.arraycopy(toByteArray, toByteArray.length - 256, bArr, 0, 256);
                        } else if (toByteArray.length < 256) {
                            bArr = new byte[256];
                            System.arraycopy(toByteArray, 0, bArr, 256 - toByteArray.length, toByteArray.length);
                            for (int i = 0; i < 256 - toByteArray.length; i++) {
                                toByteArray[i] = null;
                            }
                        } else {
                            bArr = toByteArray;
                        }
                        toByteArray = Utilities.computeSHA1(bArr);
                        Object obj = new byte[8];
                        System.arraycopy(toByteArray, toByteArray.length - 8, obj, 0, 8);
                        this.authKey = bArr;
                        this.keyFingerprint = Utilities.bytesToLong(obj);
                        if (this.keyFingerprint != phoneCall.key_fingerprint) {
                            if (BuildVars.LOGS_ENABLED != null) {
                                FileLog.m4w("key fingerprints don't match");
                            }
                            callFailed();
                            return;
                        }
                        initiateActualEncryptedCall();
                    } else {
                        if (BuildVars.LOGS_ENABLED != null) {
                            FileLog.m4w("stopping VoIP service, bad Ga and Gb (accepting)");
                        }
                        callFailed();
                    }
                } else {
                    if (BuildVars.LOGS_ENABLED != null) {
                        FileLog.m4w("stopping VoIP service, Ga hash doesn't match");
                    }
                    callFailed();
                }
            } else if ((phoneCall instanceof TL_phoneCallAccepted) && this.authKey == null) {
                processAcceptedCall();
            } else if (this.currentState == 13 && phoneCall.receive_date != null) {
                dispatchStateChanged(16);
                if (BuildVars.LOGS_ENABLED != null) {
                    FileLog.m0d("!!!!!! CALL RECEIVED");
                }
                if (this.spPlayID != null) {
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
        }
    }

    private void startRatingActivity() {
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).putExtra("call_id", this.call.id).putExtra("call_access_hash", this.call.access_hash).putExtra("account", this.currentAccount).addFlags(805306368), 0).send();
        } catch (Throwable e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m2e("Error starting incall activity", e);
            }
        }
    }

    public byte[] getEncryptionKey() {
        return this.authKey;
    }

    private void processAcceptedCall() {
        dispatchStateChanged(12);
        BigInteger bigInteger = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
        BigInteger bigInteger2 = new BigInteger(1, this.call.g_b);
        if (Utilities.isGoodGaAndGb(bigInteger2, bigInteger)) {
            byte[] bArr;
            Object toByteArray = bigInteger2.modPow(new BigInteger(1, this.a_or_b), bigInteger).toByteArray();
            if (toByteArray.length > 256) {
                bArr = new byte[256];
                System.arraycopy(toByteArray, toByteArray.length - 256, bArr, 0, 256);
            } else if (toByteArray.length < 256) {
                bArr = new byte[256];
                System.arraycopy(toByteArray, 0, bArr, 256 - toByteArray.length, toByteArray.length);
                for (int i = 0; i < 256 - toByteArray.length; i++) {
                    toByteArray[i] = null;
                }
            } else {
                bArr = toByteArray;
            }
            toByteArray = Utilities.computeSHA1(bArr);
            Object obj = new byte[8];
            System.arraycopy(toByteArray, toByteArray.length - 8, obj, 0, 8);
            long bytesToLong = Utilities.bytesToLong(obj);
            this.authKey = bArr;
            this.keyFingerprint = bytesToLong;
            TLObject tL_phone_confirmCall = new TL_phone_confirmCall();
            tL_phone_confirmCall.g_a = this.g_a;
            tL_phone_confirmCall.key_fingerprint = bytesToLong;
            tL_phone_confirmCall.peer = new TL_inputPhoneCall();
            tL_phone_confirmCall.peer.id = this.call.id;
            tL_phone_confirmCall.peer.access_hash = this.call.access_hash;
            tL_phone_confirmCall.protocol = new TL_phoneCallProtocol();
            tL_phone_confirmCall.protocol.max_layer = 74;
            tL_phone_confirmCall.protocol.min_layer = 65;
            TL_phoneCallProtocol tL_phoneCallProtocol = tL_phone_confirmCall.protocol;
            tL_phone_confirmCall.protocol.udp_reflector = true;
            tL_phoneCallProtocol.udp_p2p = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_phone_confirmCall, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (tL_error != null) {
                                VoIPService.this.callFailed();
                                return;
                            }
                            VoIPService.this.call = ((TL_phone_phoneCall) tLObject).phone_call;
                            VoIPService.this.initiateActualEncryptedCall();
                        }
                    });
                }
            });
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m4w("stopping VoIP service, bad Ga and Gb");
        }
        callFailed();
    }

    private void initiateActualEncryptedCall() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r11 = this;
        r0 = r11.timeoutRunnable;
        r1 = 0;
        if (r0 == 0) goto L_0x000c;
    L_0x0005:
        r0 = r11.timeoutRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r11.timeoutRunnable = r1;
    L_0x000c:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01d8 }
        if (r0 == 0) goto L_0x0026;	 Catch:{ Exception -> 0x01d8 }
    L_0x0010:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01d8 }
        r0.<init>();	 Catch:{ Exception -> 0x01d8 }
        r2 = "InitCall: keyID=";	 Catch:{ Exception -> 0x01d8 }
        r0.append(r2);	 Catch:{ Exception -> 0x01d8 }
        r2 = r11.keyFingerprint;	 Catch:{ Exception -> 0x01d8 }
        r0.append(r2);	 Catch:{ Exception -> 0x01d8 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x01d8 }
        org.telegram.messenger.FileLog.m0d(r0);	 Catch:{ Exception -> 0x01d8 }
    L_0x0026:
        r0 = r11.currentAccount;	 Catch:{ Exception -> 0x01d8 }
        r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0);	 Catch:{ Exception -> 0x01d8 }
        r2 = new java.util.HashSet;	 Catch:{ Exception -> 0x01d8 }
        r3 = "calls_access_hashes";	 Catch:{ Exception -> 0x01d8 }
        r4 = java.util.Collections.EMPTY_SET;	 Catch:{ Exception -> 0x01d8 }
        r3 = r0.getStringSet(r3, r4);	 Catch:{ Exception -> 0x01d8 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x01d8 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01d8 }
        r3.<init>();	 Catch:{ Exception -> 0x01d8 }
        r4 = r11.call;	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.id;	 Catch:{ Exception -> 0x01d8 }
        r3.append(r4);	 Catch:{ Exception -> 0x01d8 }
        r4 = " ";	 Catch:{ Exception -> 0x01d8 }
        r3.append(r4);	 Catch:{ Exception -> 0x01d8 }
        r4 = r11.call;	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.access_hash;	 Catch:{ Exception -> 0x01d8 }
        r3.append(r4);	 Catch:{ Exception -> 0x01d8 }
        r4 = " ";	 Catch:{ Exception -> 0x01d8 }
        r3.append(r4);	 Catch:{ Exception -> 0x01d8 }
        r4 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x01d8 }
        r3.append(r4);	 Catch:{ Exception -> 0x01d8 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x01d8 }
        r2.add(r3);	 Catch:{ Exception -> 0x01d8 }
    L_0x0064:
        r3 = r2.size();	 Catch:{ Exception -> 0x01d8 }
        r4 = 20;	 Catch:{ Exception -> 0x01d8 }
        if (r3 <= r4) goto L_0x00a8;	 Catch:{ Exception -> 0x01d8 }
    L_0x006c:
        r3 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;	 Catch:{ Exception -> 0x01d8 }
        r5 = r2.iterator();	 Catch:{ Exception -> 0x01d8 }
        r6 = r3;	 Catch:{ Exception -> 0x01d8 }
        r3 = r1;	 Catch:{ Exception -> 0x01d8 }
    L_0x0077:
        r4 = r5.hasNext();	 Catch:{ Exception -> 0x01d8 }
        if (r4 == 0) goto L_0x00a2;	 Catch:{ Exception -> 0x01d8 }
    L_0x007d:
        r4 = r5.next();	 Catch:{ Exception -> 0x01d8 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x01d8 }
        r8 = " ";	 Catch:{ Exception -> 0x01d8 }
        r8 = r4.split(r8);	 Catch:{ Exception -> 0x01d8 }
        r9 = r8.length;	 Catch:{ Exception -> 0x01d8 }
        r10 = 2;	 Catch:{ Exception -> 0x01d8 }
        if (r9 >= r10) goto L_0x0091;	 Catch:{ Exception -> 0x01d8 }
    L_0x008d:
        r5.remove();	 Catch:{ Exception -> 0x01d8 }
        goto L_0x0077;
    L_0x0091:
        r8 = r8[r10];	 Catch:{ Exception -> 0x009e }
        r8 = java.lang.Long.parseLong(r8);	 Catch:{ Exception -> 0x009e }
        r10 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r10 >= 0) goto L_0x0077;
    L_0x009b:
        r3 = r4;
        r6 = r8;
        goto L_0x0077;
    L_0x009e:
        r5.remove();	 Catch:{ Exception -> 0x01d8 }
        goto L_0x0077;	 Catch:{ Exception -> 0x01d8 }
    L_0x00a2:
        if (r3 == 0) goto L_0x0064;	 Catch:{ Exception -> 0x01d8 }
    L_0x00a4:
        r2.remove(r3);	 Catch:{ Exception -> 0x01d8 }
        goto L_0x0064;	 Catch:{ Exception -> 0x01d8 }
    L_0x00a8:
        r0 = r0.edit();	 Catch:{ Exception -> 0x01d8 }
        r3 = "calls_access_hashes";	 Catch:{ Exception -> 0x01d8 }
        r0 = r0.putStringSet(r3, r2);	 Catch:{ Exception -> 0x01d8 }
        r0.commit();	 Catch:{ Exception -> 0x01d8 }
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x01d8 }
        r2 = r11.controller;	 Catch:{ Exception -> 0x01d8 }
        r3 = r11.currentAccount;	 Catch:{ Exception -> 0x01d8 }
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);	 Catch:{ Exception -> 0x01d8 }
        r3 = r3.callPacketTimeout;	 Catch:{ Exception -> 0x01d8 }
        r3 = (double) r3;	 Catch:{ Exception -> 0x01d8 }
        r5 = 465200730NUM; // 0x408f40NUM float:0.0 double:1000.0;	 Catch:{ Exception -> 0x01d8 }
        r3 = r3 / r5;	 Catch:{ Exception -> 0x01d8 }
        r7 = r11.currentAccount;	 Catch:{ Exception -> 0x01d8 }
        r7 = org.telegram.messenger.MessagesController.getInstance(r7);	 Catch:{ Exception -> 0x01d8 }
        r7 = r7.callConnectTimeout;	 Catch:{ Exception -> 0x01d8 }
        r7 = (double) r7;	 Catch:{ Exception -> 0x01d8 }
        r5 = r7 / r5;	 Catch:{ Exception -> 0x01d8 }
        r7 = "VoipDataSaving";	 Catch:{ Exception -> 0x01d8 }
        r10 = 0;	 Catch:{ Exception -> 0x01d8 }
        r7 = r0.getInt(r7, r10);	 Catch:{ Exception -> 0x01d8 }
        r0 = r11.call;	 Catch:{ Exception -> 0x01d8 }
        r8 = r0.id;	 Catch:{ Exception -> 0x01d8 }
        r2.setConfig(r3, r5, r7, r8);	 Catch:{ Exception -> 0x01d8 }
        r0 = r11.controller;	 Catch:{ Exception -> 0x01d8 }
        r2 = r11.authKey;	 Catch:{ Exception -> 0x01d8 }
        r3 = r11.isOutgoing;	 Catch:{ Exception -> 0x01d8 }
        r0.setEncryptionKey(r2, r3);	 Catch:{ Exception -> 0x01d8 }
        r0 = r11.call;	 Catch:{ Exception -> 0x01d8 }
        r0 = r0.alternative_connections;	 Catch:{ Exception -> 0x01d8 }
        r0 = r0.size();	 Catch:{ Exception -> 0x01d8 }
        r2 = 1;	 Catch:{ Exception -> 0x01d8 }
        r0 = r0 + r2;	 Catch:{ Exception -> 0x01d8 }
        r0 = new org.telegram.tgnet.TLRPC.TL_phoneConnection[r0];	 Catch:{ Exception -> 0x01d8 }
        r3 = r11.call;	 Catch:{ Exception -> 0x01d8 }
        r3 = r3.connection;	 Catch:{ Exception -> 0x01d8 }
        r0[r10] = r3;	 Catch:{ Exception -> 0x01d8 }
        r3 = r10;	 Catch:{ Exception -> 0x01d8 }
    L_0x00ff:
        r4 = r11.call;	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.alternative_connections;	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.size();	 Catch:{ Exception -> 0x01d8 }
        if (r3 >= r4) goto L_0x0119;	 Catch:{ Exception -> 0x01d8 }
    L_0x0109:
        r4 = r3 + 1;	 Catch:{ Exception -> 0x01d8 }
        r5 = r11.call;	 Catch:{ Exception -> 0x01d8 }
        r5 = r5.alternative_connections;	 Catch:{ Exception -> 0x01d8 }
        r3 = r5.get(r3);	 Catch:{ Exception -> 0x01d8 }
        r3 = (org.telegram.tgnet.TLRPC.TL_phoneConnection) r3;	 Catch:{ Exception -> 0x01d8 }
        r0[r4] = r3;	 Catch:{ Exception -> 0x01d8 }
        r3 = r4;	 Catch:{ Exception -> 0x01d8 }
        goto L_0x00ff;	 Catch:{ Exception -> 0x01d8 }
    L_0x0119:
        r3 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x01d8 }
        r4 = r11.currentAccount;	 Catch:{ Exception -> 0x01d8 }
        org.telegram.ui.Components.voip.VoIPHelper.upgradeP2pSetting(r4);	 Catch:{ Exception -> 0x01d8 }
        r4 = r11.currentAccount;	 Catch:{ Exception -> 0x01d8 }
        r4 = org.telegram.messenger.MessagesController.getMainSettings(r4);	 Catch:{ Exception -> 0x01d8 }
        r5 = "calls_p2p_new";	 Catch:{ Exception -> 0x01d8 }
        r6 = r11.currentAccount;	 Catch:{ Exception -> 0x01d8 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x01d8 }
        r6 = r6.defaultP2pContacts;	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.getInt(r5, r6);	 Catch:{ Exception -> 0x01d8 }
        switch(r4) {
            case 0: goto L_0x0139;
            case 1: goto L_0x013d;
            case 2: goto L_0x013b;
            default: goto L_0x0139;
        };	 Catch:{ Exception -> 0x01d8 }
    L_0x0139:
        r4 = r2;	 Catch:{ Exception -> 0x01d8 }
        goto L_0x0154;	 Catch:{ Exception -> 0x01d8 }
    L_0x013b:
        r4 = r10;	 Catch:{ Exception -> 0x01d8 }
        goto L_0x0154;	 Catch:{ Exception -> 0x01d8 }
    L_0x013d:
        r4 = r11.currentAccount;	 Catch:{ Exception -> 0x01d8 }
        r4 = org.telegram.messenger.ContactsController.getInstance(r4);	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.contactsDict;	 Catch:{ Exception -> 0x01d8 }
        r5 = r11.user;	 Catch:{ Exception -> 0x01d8 }
        r5 = r5.id;	 Catch:{ Exception -> 0x01d8 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.get(r5);	 Catch:{ Exception -> 0x01d8 }
        if (r4 == 0) goto L_0x013b;	 Catch:{ Exception -> 0x01d8 }
    L_0x0153:
        goto L_0x0139;	 Catch:{ Exception -> 0x01d8 }
    L_0x0154:
        r5 = r11.controller;	 Catch:{ Exception -> 0x01d8 }
        r6 = r11.call;	 Catch:{ Exception -> 0x01d8 }
        r6 = r6.protocol;	 Catch:{ Exception -> 0x01d8 }
        r6 = r6.udp_p2p;	 Catch:{ Exception -> 0x01d8 }
        if (r6 == 0) goto L_0x0162;	 Catch:{ Exception -> 0x01d8 }
    L_0x015e:
        if (r4 == 0) goto L_0x0162;	 Catch:{ Exception -> 0x01d8 }
    L_0x0160:
        r4 = r2;	 Catch:{ Exception -> 0x01d8 }
        goto L_0x0163;	 Catch:{ Exception -> 0x01d8 }
    L_0x0162:
        r4 = r10;	 Catch:{ Exception -> 0x01d8 }
    L_0x0163:
        r6 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x01d8 }
        if (r6 == 0) goto L_0x0171;	 Catch:{ Exception -> 0x01d8 }
    L_0x0167:
        r6 = "dbg_force_tcp_in_calls";	 Catch:{ Exception -> 0x01d8 }
        r6 = r3.getBoolean(r6, r10);	 Catch:{ Exception -> 0x01d8 }
        if (r6 == 0) goto L_0x0171;	 Catch:{ Exception -> 0x01d8 }
    L_0x016f:
        r6 = r2;	 Catch:{ Exception -> 0x01d8 }
        goto L_0x0172;	 Catch:{ Exception -> 0x01d8 }
    L_0x0171:
        r6 = r10;	 Catch:{ Exception -> 0x01d8 }
    L_0x0172:
        r7 = r11.call;	 Catch:{ Exception -> 0x01d8 }
        r7 = r7.protocol;	 Catch:{ Exception -> 0x01d8 }
        r7 = r7.max_layer;	 Catch:{ Exception -> 0x01d8 }
        r5.setRemoteEndpoints(r0, r4, r6, r7);	 Catch:{ Exception -> 0x01d8 }
        r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x01d8 }
        if (r0 == 0) goto L_0x018f;	 Catch:{ Exception -> 0x01d8 }
    L_0x017f:
        r0 = "dbg_force_tcp_in_calls";	 Catch:{ Exception -> 0x01d8 }
        r0 = r3.getBoolean(r0, r10);	 Catch:{ Exception -> 0x01d8 }
        if (r0 == 0) goto L_0x018f;	 Catch:{ Exception -> 0x01d8 }
    L_0x0187:
        r0 = new org.telegram.messenger.voip.VoIPService$13;	 Catch:{ Exception -> 0x01d8 }
        r0.<init>();	 Catch:{ Exception -> 0x01d8 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x01d8 }
    L_0x018f:
        r0 = "proxy_enabled";	 Catch:{ Exception -> 0x01d8 }
        r0 = r3.getBoolean(r0, r10);	 Catch:{ Exception -> 0x01d8 }
        if (r0 == 0) goto L_0x01be;	 Catch:{ Exception -> 0x01d8 }
    L_0x0197:
        r0 = "proxy_enabled_calls";	 Catch:{ Exception -> 0x01d8 }
        r0 = r3.getBoolean(r0, r10);	 Catch:{ Exception -> 0x01d8 }
        if (r0 == 0) goto L_0x01be;	 Catch:{ Exception -> 0x01d8 }
    L_0x019f:
        r0 = "proxy_ip";	 Catch:{ Exception -> 0x01d8 }
        r0 = r3.getString(r0, r1);	 Catch:{ Exception -> 0x01d8 }
        if (r0 == 0) goto L_0x01be;	 Catch:{ Exception -> 0x01d8 }
    L_0x01a7:
        r4 = r11.controller;	 Catch:{ Exception -> 0x01d8 }
        r5 = "proxy_port";	 Catch:{ Exception -> 0x01d8 }
        r5 = r3.getInt(r5, r10);	 Catch:{ Exception -> 0x01d8 }
        r6 = "proxy_user";	 Catch:{ Exception -> 0x01d8 }
        r6 = r3.getString(r6, r1);	 Catch:{ Exception -> 0x01d8 }
        r7 = "proxy_pass";	 Catch:{ Exception -> 0x01d8 }
        r1 = r3.getString(r7, r1);	 Catch:{ Exception -> 0x01d8 }
        r4.setProxy(r0, r5, r6, r1);	 Catch:{ Exception -> 0x01d8 }
    L_0x01be:
        r0 = r11.controller;	 Catch:{ Exception -> 0x01d8 }
        r0.start();	 Catch:{ Exception -> 0x01d8 }
        r11.updateNetworkType();	 Catch:{ Exception -> 0x01d8 }
        r0 = r11.controller;	 Catch:{ Exception -> 0x01d8 }
        r0.connect();	 Catch:{ Exception -> 0x01d8 }
        r11.controllerStarted = r2;	 Catch:{ Exception -> 0x01d8 }
        r0 = new org.telegram.messenger.voip.VoIPService$14;	 Catch:{ Exception -> 0x01d8 }
        r0.<init>();	 Catch:{ Exception -> 0x01d8 }
        r1 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;	 Catch:{ Exception -> 0x01d8 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1);	 Catch:{ Exception -> 0x01d8 }
        goto L_0x01e5;
    L_0x01d8:
        r0 = move-exception;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x01e2;
    L_0x01dd:
        r1 = "error starting call";
        org.telegram.messenger.FileLog.m2e(r1, r0);
    L_0x01e2:
        r11.callFailed();
    L_0x01e5:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.initiateActualEncryptedCall():void");
    }

    protected void showNotification() {
        showNotification(ContactsController.formatName(this.user.first_name, this.user.last_name), this.user.photo != null ? this.user.photo.photo_small : null, VoIPActivity.class);
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

    protected void callFailed(int i) {
        if (this.call != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("Discarding failed call");
            }
            TLObject tL_phone_discardCall = new TL_phone_discardCall();
            tL_phone_discardCall.peer = new TL_inputPhoneCall();
            tL_phone_discardCall.peer.access_hash = this.call.access_hash;
            tL_phone_discardCall.peer.id = this.call.id;
            int callDuration = (this.controller == null || !this.controllerStarted) ? 0 : (int) (this.controller.getCallDuration() / 1000);
            tL_phone_discardCall.duration = callDuration;
            long preferredRelayID = (this.controller == null || !this.controllerStarted) ? 0 : this.controller.getPreferredRelayID();
            tL_phone_discardCall.connection_id = preferredRelayID;
            tL_phone_discardCall.reason = new TL_phoneCallDiscardReasonDisconnect();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_phone_discardCall, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error != null) {
                        if (BuildVars.LOGS_ENABLED != null) {
                            tLObject = new StringBuilder();
                            tLObject.append("error on phone.discardCall: ");
                            tLObject.append(tL_error);
                            FileLog.m1e(tLObject.toString());
                        }
                    } else if (BuildVars.LOGS_ENABLED != null) {
                        tL_error = new StringBuilder();
                        tL_error.append("phone.discardCall ");
                        tL_error.append(tLObject);
                        FileLog.m0d(tL_error.toString());
                    }
                }
            });
        }
        super.callFailed(i);
    }

    public long getCallID() {
        return this.call != null ? this.call.id : 0;
    }

    public void onUIForegroundStateChanged(boolean z) {
        if (this.currentState != 15) {
            return;
        }
        if (z) {
            stopForeground(true);
        } else if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(VoIPService.this, VoIPActivity.class);
                    intent.addFlags(805306368);
                    try {
                        PendingIntent.getActivity(VoIPService.this, 0, intent, 0).send();
                    } catch (Throwable e) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m2e("error restarting activity", e);
                        }
                        VoIPService.this.declineIncomingCall(4, null);
                    }
                    if (VERSION.SDK_INT >= 26) {
                        VoIPService.this.showNotification();
                    }
                }
            }, 500);
        } else if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            showIncomingNotification(ContactsController.formatName(this.user.first_name, this.user.last_name), null, this.user, null, 0, VoIPActivity.class);
        } else {
            declineIncomingCall(true, null);
        }
    }

    void onMediaButtonEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != 79 || keyEvent.getAction() != 1) {
            return;
        }
        if (this.currentState == 15) {
            acceptIncomingCall();
            return;
        }
        setMicMute(isMicMute() ^ 1);
        keyEvent = this.stateListeners.iterator();
        while (keyEvent.hasNext()) {
            ((StateListener) keyEvent.next()).onAudioSettingsChanged();
        }
    }

    public void debugCtl(int i, int i2) {
        if (this.controller != null) {
            this.controller.debugCtl(i, i2);
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

    private java.lang.String[] getEmoji() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r3 = this;
        r0 = new java.io.ByteArrayOutputStream;
        r0.<init>();
        r1 = r3.authKey;	 Catch:{ IOException -> 0x000f }
        r0.write(r1);	 Catch:{ IOException -> 0x000f }
        r1 = r3.g_a;	 Catch:{ IOException -> 0x000f }
        r0.write(r1);	 Catch:{ IOException -> 0x000f }
    L_0x000f:
        r1 = r0.toByteArray();
        r2 = 0;
        r0 = r0.size();
        r0 = org.telegram.messenger.Utilities.computeSHA256(r1, r2, r0);
        r0 = org.telegram.messenger.voip.EncryptionKeyEmojifier.emojifyForCall(r0);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.getEmoji():java.lang.String[]");
    }

    public boolean canUpgrate() {
        return (this.peerCapabilities & 1) == 1;
    }

    public void upgradeToGroupCall(List<Integer> list) {
        if (!this.upgrading) {
            this.groupUsersToAdd = list;
            if (this.isOutgoing == null) {
                this.controller.requestCallUpgrade();
                return;
            }
            this.upgrading = true;
            this.groupCallEncryptionKey = new byte[256];
            Utilities.random.nextBytes(this.groupCallEncryptionKey);
            list = this.groupCallEncryptionKey;
            list[0] = (byte) (list[0] & 127);
            list = Utilities.computeSHA1(this.groupCallEncryptionKey);
            Object obj = new byte[8];
            System.arraycopy(list, list.length - 8, obj, 0, 8);
            this.groupCallKeyFingerprint = Utilities.bytesToLong(obj);
            this.controller.sendGroupCallKey(this.groupCallEncryptionKey);
        }
    }

    public void onConnectionStateChanged(int i) {
        if (i == 3) {
            this.peerCapabilities = this.controller.getPeerCapabilities();
        }
        super.onConnectionStateChanged(i);
    }

    public void onGroupCallKeyReceived(byte[] bArr) {
        this.joiningGroupCall = true;
        this.groupCallEncryptionKey = bArr;
        bArr = Utilities.computeSHA1(this.groupCallEncryptionKey);
        Object obj = new byte[8];
        System.arraycopy(bArr, bArr.length - 8, obj, 0, 8);
        this.groupCallKeyFingerprint = Utilities.bytesToLong(obj);
    }

    public void onGroupCallKeySent() {
        boolean z = this.isOutgoing;
    }

    public void onCallUpgradeRequestReceived() {
        upgradeToGroupCall(new ArrayList());
    }

    @TargetApi(26)
    public CallConnection getConnectionAndStartCall() {
        if (this.systemCallConnection == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("creating call connection");
            }
            this.systemCallConnection = new CallConnection();
            if (this.isOutgoing) {
                this.delayedStartOutgoingCall = new Runnable() {
                    public void run() {
                        VoIPService.this.delayedStartOutgoingCall = null;
                        VoIPService.this.startOutgoingCall();
                    }
                };
                AndroidUtilities.runOnUIThread(this.delayedStartOutgoingCall, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
            this.systemCallConnection.setCallerDisplayName(ContactsController.formatName(this.user.first_name, this.user.last_name), 1);
        }
        return this.systemCallConnection;
    }
}
