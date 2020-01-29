package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.view.KeyEvent;
import androidx.core.app.NotificationManagerCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.VoIPActivity;
import org.telegram.ui.VoIPFeedbackActivity;

public class VoIPService extends VoIPBaseService {
    public static final int CALL_MAX_LAYER = VoIPController.getConnectionMaxLayer();
    public static final int CALL_MIN_LAYER = 65;
    public static final int STATE_BUSY = 17;
    public static final int STATE_EXCHANGING_KEYS = 12;
    public static final int STATE_HANGING_UP = 10;
    public static final int STATE_REQUESTING = 14;
    public static final int STATE_RINGING = 16;
    public static final int STATE_WAITING = 13;
    public static final int STATE_WAITING_INCOMING = 15;
    public static TLRPC.PhoneCall callIShouldHavePutIntoIntent;
    /* access modifiers changed from: private */
    public byte[] a_or_b;
    private byte[] authKey;
    /* access modifiers changed from: private */
    public TLRPC.PhoneCall call;
    /* access modifiers changed from: private */
    public int callReqId;
    private String debugLog;
    /* access modifiers changed from: private */
    public Runnable delayedStartOutgoingCall;
    /* access modifiers changed from: private */
    public boolean endCallAfterRequest = false;
    private boolean forceRating;
    /* access modifiers changed from: private */
    public byte[] g_a;
    /* access modifiers changed from: private */
    public byte[] g_a_hash;
    private byte[] groupCallEncryptionKey;
    private long groupCallKeyFingerprint;
    private List<Integer> groupUsersToAdd = new ArrayList();
    private boolean joiningGroupCall;
    private long keyFingerprint;
    private boolean needSendDebugLog = false;
    private int peerCapabilities;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.PhoneCall> pendingUpdates = new ArrayList<>();
    private boolean startedRinging = false;
    private boolean upgrading;
    /* access modifiers changed from: private */
    public TLRPC.User user;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onGroupCallKeySent() {
    }

    @SuppressLint({"MissingPermission"})
    public int onStartCommand(Intent intent, int i, int i2) {
        if (VoIPBaseService.sharedInstance != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Tried to start the VoIP service when it's already started");
            }
            return 2;
        }
        this.currentAccount = intent.getIntExtra("account", -1);
        if (this.currentAccount != -1) {
            int intExtra = intent.getIntExtra("user_id", 0);
            this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
            this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(intExtra));
            if (this.user == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("VoIPService: user==null");
                }
                stopSelf();
                return 2;
            }
            VoIPBaseService.sharedInstance = this;
            if (this.isOutgoing) {
                dispatchStateChanged(14);
                if (VoIPBaseService.USE_CONNECTION_SERVICE) {
                    Bundle bundle = new Bundle();
                    Bundle bundle2 = new Bundle();
                    bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", addAccountToTelecomManager());
                    bundle2.putInt("call_type", 1);
                    bundle.putBundle("android.telecom.extra.OUTGOING_CALL_EXTRAS", bundle2);
                    ContactsController instance = ContactsController.getInstance(this.currentAccount);
                    TLRPC.User user2 = this.user;
                    instance.createOrUpdateConnectionServiceContact(user2.id, user2.first_name, user2.last_name);
                    ((TelecomManager) getSystemService("telecom")).placeCall(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), bundle);
                } else {
                    this.delayedStartOutgoingCall = new Runnable() {
                        public void run() {
                            Runnable unused = VoIPService.this.delayedStartOutgoingCall = null;
                            VoIPService.this.startOutgoingCall();
                        }
                    };
                    AndroidUtilities.runOnUIThread(this.delayedStartOutgoingCall, 2000);
                }
                if (intent.getBooleanExtra("start_incall_activity", false)) {
                    startActivity(new Intent(this, VoIPActivity.class).addFlags(NUM));
                }
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeInCallActivity, new Object[0]);
                this.call = callIShouldHavePutIntoIntent;
                callIShouldHavePutIntoIntent = null;
                if (VoIPBaseService.USE_CONNECTION_SERVICE) {
                    acknowledgeCall(false);
                    showNotification();
                } else {
                    acknowledgeCall(true);
                }
            }
            initializeAccountRelatedThings();
            return 2;
        }
        throw new IllegalStateException("No account specified when starting VoIP service");
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
        final SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        VoIPServerConfig.setConfig(mainSettings.getString("voip_server_config", "{}"));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_phone_getCallConfig(), new RequestDelegate() {
            public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                if (tL_error == null) {
                    String str = ((TLRPC.TL_dataJSON) tLObject).data;
                    VoIPServerConfig.setConfig(str);
                    mainSettings.edit().putString("voip_server_config", str).commit();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onControllerPreRelease() {
        if (this.debugLog == null) {
            this.debugLog = this.controller.getDebugLog();
        }
    }

    public static VoIPService getSharedInstance() {
        VoIPBaseService voIPBaseService = VoIPBaseService.sharedInstance;
        if (voIPBaseService instanceof VoIPService) {
            return (VoIPService) voIPBaseService;
        }
        return null;
    }

    public TLRPC.User getUser() {
        return this.user;
    }

    public void hangUp() {
        int i = this.currentState;
        declineIncomingCall((i == 16 || (i == 13 && this.isOutgoing)) ? 3 : 1, (Runnable) null);
    }

    public void hangUp(Runnable runnable) {
        int i = this.currentState;
        declineIncomingCall((i == 16 || (i == 13 && this.isOutgoing)) ? 3 : 1, runnable);
    }

    /* access modifiers changed from: private */
    public void startOutgoingCall() {
        VoIPBaseService.CallConnection callConnection;
        if (VoIPBaseService.USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
            callConnection.setDialing();
        }
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
        TLRPC.TL_messages_getDhConfig tL_messages_getDhConfig = new TLRPC.TL_messages_getDhConfig();
        tL_messages_getDhConfig.random_length = 256;
        final MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
        tL_messages_getDhConfig.version = instance.getLastSecretVersion();
        this.callReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getDhConfig, new RequestDelegate() {
            public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                int unused = VoIPService.this.callReqId = 0;
                if (VoIPService.this.endCallAfterRequest) {
                    VoIPService.this.callEnded();
                } else if (tL_error == null) {
                    TLRPC.messages_DhConfig messages_dhconfig = (TLRPC.messages_DhConfig) tLObject;
                    if (tLObject instanceof TLRPC.TL_messages_dhConfig) {
                        if (!Utilities.isGoodPrime(messages_dhconfig.p, messages_dhconfig.g)) {
                            VoIPService.this.callFailed();
                            return;
                        }
                        instance.setSecretPBytes(messages_dhconfig.p);
                        instance.setSecretG(messages_dhconfig.g);
                        instance.setLastSecretVersion(messages_dhconfig.version);
                        MessagesStorage messagesStorage = instance;
                        messagesStorage.saveSecretParams(messagesStorage.getLastSecretVersion(), instance.getSecretG(), instance.getSecretPBytes());
                    }
                    final byte[] bArr = new byte[256];
                    for (int i = 0; i < 256; i++) {
                        bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ messages_dhconfig.random[i]);
                    }
                    byte[] byteArray = BigInteger.valueOf((long) instance.getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, instance.getSecretPBytes())).toByteArray();
                    if (byteArray.length > 256) {
                        byte[] bArr2 = new byte[256];
                        System.arraycopy(byteArray, 1, bArr2, 0, 256);
                        byteArray = bArr2;
                    }
                    TLRPC.TL_phone_requestCall tL_phone_requestCall = new TLRPC.TL_phone_requestCall();
                    tL_phone_requestCall.user_id = MessagesController.getInstance(VoIPService.this.currentAccount).getInputUser(VoIPService.this.user);
                    tL_phone_requestCall.protocol = new TLRPC.TL_phoneCallProtocol();
                    TLRPC.TL_phoneCallProtocol tL_phoneCallProtocol = tL_phone_requestCall.protocol;
                    tL_phoneCallProtocol.udp_p2p = true;
                    tL_phoneCallProtocol.udp_reflector = true;
                    tL_phoneCallProtocol.min_layer = 65;
                    tL_phoneCallProtocol.max_layer = VoIPService.CALL_MAX_LAYER;
                    byte[] unused2 = VoIPService.this.g_a = byteArray;
                    tL_phone_requestCall.g_a_hash = Utilities.computeSHA256(byteArray, 0, byteArray.length);
                    tL_phone_requestCall.random_id = Utilities.random.nextInt();
                    ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(tL_phone_requestCall, new RequestDelegate() {
                        public void run(final TLObject tLObject, final TLRPC.TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    TLRPC.TL_error tL_error = tL_error;
                                    if (tL_error == null) {
                                        TLRPC.PhoneCall unused = VoIPService.this.call = ((TLRPC.TL_phone_phoneCall) tLObject).phone_call;
                                        AnonymousClass1 r0 = AnonymousClass1.this;
                                        byte[] unused2 = VoIPService.this.a_or_b = bArr;
                                        VoIPService.this.dispatchStateChanged(13);
                                        if (VoIPService.this.endCallAfterRequest) {
                                            VoIPService.this.hangUp();
                                            return;
                                        }
                                        if (VoIPService.this.pendingUpdates.size() > 0 && VoIPService.this.call != null) {
                                            Iterator it = VoIPService.this.pendingUpdates.iterator();
                                            while (it.hasNext()) {
                                                VoIPService.this.onCallUpdated((TLRPC.PhoneCall) it.next());
                                            }
                                            VoIPService.this.pendingUpdates.clear();
                                        }
                                        VoIPService.this.timeoutRunnable = new Runnable() {
                                            public void run() {
                                                VoIPService.this.timeoutRunnable = null;
                                                TLRPC.TL_phone_discardCall tL_phone_discardCall = new TLRPC.TL_phone_discardCall();
                                                tL_phone_discardCall.peer = new TLRPC.TL_inputPhoneCall();
                                                tL_phone_discardCall.peer.access_hash = VoIPService.this.call.access_hash;
                                                tL_phone_discardCall.peer.id = VoIPService.this.call.id;
                                                tL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
                                                ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(tL_phone_discardCall, new RequestDelegate() {
                                                    public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            if (tL_error != null) {
                                                                FileLog.e("error on phone.discardCall: " + tL_error);
                                                            } else {
                                                                FileLog.d("phone.discardCall " + tLObject);
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
                                        VoIPService voIPService = VoIPService.this;
                                        AndroidUtilities.runOnUIThread(voIPService.timeoutRunnable, (long) MessagesController.getInstance(voIPService.currentAccount).callReceiveTimeout);
                                    } else if (tL_error.code != 400 || !"PARTICIPANT_VERSION_OUTDATED".equals(tL_error.text)) {
                                        int i = tL_error.code;
                                        if (i == 403) {
                                            VoIPService.this.callFailed(-2);
                                        } else if (i == 406) {
                                            VoIPService.this.callFailed(-3);
                                        } else {
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.e("Error on phone.requestCall: " + tL_error);
                                            }
                                            VoIPService.this.callFailed();
                                        }
                                    } else {
                                        VoIPService.this.callFailed(-1);
                                    }
                                }
                            });
                        }
                    }, 2);
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Error on getDhConfig " + tL_error);
                    }
                    VoIPService.this.callFailed();
                }
            }
        }, 2);
    }

    private void acknowledgeCall(final boolean z) {
        if (this.call instanceof TLRPC.TL_phoneCallDiscarded) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("Call " + this.call.id + " was discarded before the service started, stopping");
            }
            stopSelf();
        } else if (Build.VERSION.SDK_INT < 19 || !XiaomiUtilities.isMIUI() || XiaomiUtilities.isCustomPermissionGranted(10020) || !((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            TLRPC.TL_phone_receivedCall tL_phone_receivedCall = new TLRPC.TL_phone_receivedCall();
            tL_phone_receivedCall.peer = new TLRPC.TL_inputPhoneCall();
            TLRPC.TL_inputPhoneCall tL_inputPhoneCall = tL_phone_receivedCall.peer;
            TLRPC.PhoneCall phoneCall = this.call;
            tL_inputPhoneCall.id = phoneCall.id;
            tL_inputPhoneCall.access_hash = phoneCall.access_hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_phone_receivedCall, new RequestDelegate() {
                public void run(final TLObject tLObject, final TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (VoIPBaseService.sharedInstance != null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.w("receivedCall response = " + tLObject);
                                }
                                if (tL_error != null) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("error on receivedCall: " + tL_error);
                                    }
                                    VoIPService.this.stopSelf();
                                    return;
                                }
                                if (VoIPBaseService.USE_CONNECTION_SERVICE) {
                                    ContactsController.getInstance(VoIPService.this.currentAccount).createOrUpdateConnectionServiceContact(VoIPService.this.user.id, VoIPService.this.user.first_name, VoIPService.this.user.last_name);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("call_type", 1);
                                    ((TelecomManager) VoIPService.this.getSystemService("telecom")).addNewIncomingCall(VoIPService.this.addAccountToTelecomManager(), bundle);
                                }
                                AnonymousClass5 r0 = AnonymousClass5.this;
                                if (z) {
                                    VoIPService.this.startRinging();
                                }
                            }
                        }
                    });
                }
            }, 2);
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("MIUI: no permission to show when locked but the screen is locked. ¯\\_(ツ)_/¯");
            }
            stopSelf();
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
                FileLog.d("starting ringing for call " + this.call.id);
            }
            dispatchStateChanged(15);
            if (Build.VERSION.SDK_INT >= 21) {
                TLRPC.User user2 = this.user;
                showIncomingNotification(ContactsController.formatName(user2.first_name, user2.last_name), (CharSequence) null, this.user, (List<TLRPC.User>) null, 0, VoIPActivity.class);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("Showing incoming call notification");
                    return;
                }
                return;
            }
            startRingtoneAndVibration(this.user.id);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Starting incall activity for incoming call");
            }
            try {
                PendingIntent.getActivity(this, 12345, new Intent(this, VoIPActivity.class).addFlags(NUM), 0).send();
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting incall activity", e);
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
        final MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
        TLRPC.TL_messages_getDhConfig tL_messages_getDhConfig = new TLRPC.TL_messages_getDhConfig();
        tL_messages_getDhConfig.random_length = 256;
        tL_messages_getDhConfig.version = instance.getLastSecretVersion();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getDhConfig, new RequestDelegate() {
            public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                if (tL_error == null) {
                    TLRPC.messages_DhConfig messages_dhconfig = (TLRPC.messages_DhConfig) tLObject;
                    if (tLObject instanceof TLRPC.TL_messages_dhConfig) {
                        if (!Utilities.isGoodPrime(messages_dhconfig.p, messages_dhconfig.g)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("stopping VoIP service, bad prime");
                            }
                            VoIPService.this.callFailed();
                            return;
                        }
                        instance.setSecretPBytes(messages_dhconfig.p);
                        instance.setSecretG(messages_dhconfig.g);
                        instance.setLastSecretVersion(messages_dhconfig.version);
                        MessagesStorage.getInstance(VoIPService.this.currentAccount).saveSecretParams(instance.getLastSecretVersion(), instance.getSecretG(), instance.getSecretPBytes());
                    }
                    byte[] bArr = new byte[256];
                    for (int i = 0; i < 256; i++) {
                        bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ messages_dhconfig.random[i]);
                    }
                    if (VoIPService.this.call == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("call is null");
                        }
                        VoIPService.this.callFailed();
                        return;
                    }
                    byte[] unused = VoIPService.this.a_or_b = bArr;
                    BigInteger modPow = BigInteger.valueOf((long) instance.getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, instance.getSecretPBytes()));
                    VoIPService voIPService = VoIPService.this;
                    byte[] unused2 = voIPService.g_a_hash = voIPService.call.g_a_hash;
                    byte[] byteArray = modPow.toByteArray();
                    if (byteArray.length > 256) {
                        byte[] bArr2 = new byte[256];
                        System.arraycopy(byteArray, 1, bArr2, 0, 256);
                        byteArray = bArr2;
                    }
                    TLRPC.TL_phone_acceptCall tL_phone_acceptCall = new TLRPC.TL_phone_acceptCall();
                    tL_phone_acceptCall.g_b = byteArray;
                    tL_phone_acceptCall.peer = new TLRPC.TL_inputPhoneCall();
                    tL_phone_acceptCall.peer.id = VoIPService.this.call.id;
                    tL_phone_acceptCall.peer.access_hash = VoIPService.this.call.access_hash;
                    tL_phone_acceptCall.protocol = new TLRPC.TL_phoneCallProtocol();
                    TLRPC.TL_phoneCallProtocol tL_phoneCallProtocol = tL_phone_acceptCall.protocol;
                    tL_phoneCallProtocol.udp_reflector = true;
                    tL_phoneCallProtocol.udp_p2p = true;
                    tL_phoneCallProtocol.min_layer = 65;
                    tL_phoneCallProtocol.max_layer = VoIPService.CALL_MAX_LAYER;
                    ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(tL_phone_acceptCall, new RequestDelegate() {
                        public void run(final TLObject tLObject, final TLRPC.TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (tL_error == null) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.w("accept call ok! " + tLObject);
                                        }
                                        TLRPC.PhoneCall unused = VoIPService.this.call = ((TLRPC.TL_phone_phoneCall) tLObject).phone_call;
                                        if (VoIPService.this.call instanceof TLRPC.TL_phoneCallDiscarded) {
                                            VoIPService voIPService = VoIPService.this;
                                            voIPService.onCallUpdated(voIPService.call);
                                            return;
                                        }
                                        return;
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("Error on phone.acceptCall: " + tL_error);
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
        declineIncomingCall(1, (Runnable) null);
    }

    /* access modifiers changed from: protected */
    public Class<? extends Activity> getUIActivityClass() {
        return VoIPActivity.class;
    }

    public void declineIncomingCall(int i, final Runnable runnable) {
        final AnonymousClass9 r8;
        stopRinging();
        this.callDiscardReason = i;
        int i2 = this.currentState;
        final boolean z = true;
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
                public void run() {
                    VoIPService voIPService = VoIPService.this;
                    if (voIPService.currentState == 10) {
                        voIPService.callEnded();
                    }
                }
            }, 5000);
        } else if (i2 != 10 && i2 != 11) {
            dispatchStateChanged(10);
            if (this.call == null) {
                if (runnable != null) {
                    runnable.run();
                }
                callEnded();
                if (this.callReqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.callReqId, false);
                    this.callReqId = 0;
                    return;
                }
                return;
            }
            TLRPC.TL_phone_discardCall tL_phone_discardCall = new TLRPC.TL_phone_discardCall();
            tL_phone_discardCall.peer = new TLRPC.TL_inputPhoneCall();
            TLRPC.TL_inputPhoneCall tL_inputPhoneCall = tL_phone_discardCall.peer;
            TLRPC.PhoneCall phoneCall = this.call;
            tL_inputPhoneCall.access_hash = phoneCall.access_hash;
            tL_inputPhoneCall.id = phoneCall.id;
            VoIPController voIPController = this.controller;
            tL_phone_discardCall.duration = (voIPController == null || !this.controllerStarted) ? 0 : (int) (voIPController.getCallDuration() / 1000);
            VoIPController voIPController2 = this.controller;
            tL_phone_discardCall.connection_id = (voIPController2 == null || !this.controllerStarted) ? 0 : voIPController2.getPreferredRelayID();
            if (i == 2) {
                tL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
            } else if (i == 3) {
                tL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
            } else if (i != 4) {
                tL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonHangup();
            } else {
                tL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonBusy();
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
                AndroidUtilities.runOnUIThread(r8, (long) ((int) (VoIPServerConfig.getDouble("hangup_ui_timeout", 5.0d) * 1000.0d)));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_phone_discardCall, new RequestDelegate() {
                public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    if (tL_error == null) {
                        if (tLObject instanceof TLRPC.TL_updates) {
                            MessagesController.getInstance(VoIPService.this.currentAccount).processUpdates((TLRPC.TL_updates) tLObject, false);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("phone.discardCall " + tLObject);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("error on phone.discardCall: " + tL_error);
                    }
                    if (!z) {
                        AndroidUtilities.cancelRunOnUIThread(r8);
                        Runnable runnable = runnable;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                }
            }, 2);
        }
    }

    private void dumpCallObject() {
        try {
            if (BuildVars.LOGS_ENABLED) {
                for (Field field : TLRPC.PhoneCall.class.getFields()) {
                    FileLog.d(field.getName() + " = " + field.get(this.call));
                }
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void onCallUpdated(TLRPC.PhoneCall phoneCall) {
        byte[] bArr;
        VoIPController voIPController;
        TLRPC.PhoneCall phoneCall2 = this.call;
        if (phoneCall2 == null) {
            this.pendingUpdates.add(phoneCall);
        } else if (phoneCall != null) {
            if (phoneCall.id == phoneCall2.id) {
                if (phoneCall.access_hash == 0) {
                    phoneCall.access_hash = phoneCall2.access_hash;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("Call updated: " + phoneCall);
                    dumpCallObject();
                }
                this.call = phoneCall;
                if (phoneCall instanceof TLRPC.TL_phoneCallDiscarded) {
                    this.needSendDebugLog = phoneCall.need_debug;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("call discarded, stopping service");
                    }
                    if (phoneCall.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) {
                        dispatchStateChanged(17);
                        this.playingSound = true;
                        this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
                        AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1500);
                        endConnectionServiceCall(1500);
                        stopSelf();
                    } else {
                        callEnded();
                    }
                    if (phoneCall.need_rating || this.forceRating || (this.controller != null && VoIPServerConfig.getBoolean("bad_call_rating", true) && this.controller.needRate())) {
                        startRatingActivity();
                    }
                    if (this.debugLog == null && (voIPController = this.controller) != null) {
                        this.debugLog = voIPController.getDebugLog();
                    }
                    if (this.needSendDebugLog && this.debugLog != null) {
                        TLRPC.TL_phone_saveCallDebug tL_phone_saveCallDebug = new TLRPC.TL_phone_saveCallDebug();
                        tL_phone_saveCallDebug.debug = new TLRPC.TL_dataJSON();
                        tL_phone_saveCallDebug.debug.data = this.debugLog;
                        tL_phone_saveCallDebug.peer = new TLRPC.TL_inputPhoneCall();
                        TLRPC.TL_inputPhoneCall tL_inputPhoneCall = tL_phone_saveCallDebug.peer;
                        tL_inputPhoneCall.access_hash = phoneCall.access_hash;
                        tL_inputPhoneCall.id = phoneCall.id;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_phone_saveCallDebug, new RequestDelegate() {
                            public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("Sent debug logs, response=" + tLObject);
                                }
                            }
                        });
                    }
                } else if ((phoneCall instanceof TLRPC.TL_phoneCall) && this.authKey == null) {
                    byte[] bArr2 = phoneCall.g_a_or_b;
                    if (bArr2 == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("stopping VoIP service, Ga == null");
                        }
                        callFailed();
                    } else if (!Arrays.equals(this.g_a_hash, Utilities.computeSHA256(bArr2, 0, bArr2.length))) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("stopping VoIP service, Ga hash doesn't match");
                        }
                        callFailed();
                    } else {
                        byte[] bArr3 = phoneCall.g_a_or_b;
                        this.g_a = bArr3;
                        BigInteger bigInteger = new BigInteger(1, bArr3);
                        BigInteger bigInteger2 = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                        if (!Utilities.isGoodGaAndGb(bigInteger, bigInteger2)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.w("stopping VoIP service, bad Ga and Gb (accepting)");
                            }
                            callFailed();
                            return;
                        }
                        byte[] byteArray = bigInteger.modPow(new BigInteger(1, this.a_or_b), bigInteger2).toByteArray();
                        if (byteArray.length > 256) {
                            bArr = new byte[256];
                            System.arraycopy(byteArray, byteArray.length - 256, bArr, 0, 256);
                        } else if (byteArray.length < 256) {
                            bArr = new byte[256];
                            System.arraycopy(byteArray, 0, bArr, 256 - byteArray.length, byteArray.length);
                            for (int i = 0; i < 256 - byteArray.length; i++) {
                                bArr[i] = 0;
                            }
                        } else {
                            bArr = byteArray;
                        }
                        byte[] computeSHA1 = Utilities.computeSHA1(bArr);
                        byte[] bArr4 = new byte[8];
                        System.arraycopy(computeSHA1, computeSHA1.length - 8, bArr4, 0, 8);
                        this.authKey = bArr;
                        this.keyFingerprint = Utilities.bytesToLong(bArr4);
                        if (this.keyFingerprint != phoneCall.key_fingerprint) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.w("key fingerprints don't match");
                            }
                            callFailed();
                            return;
                        }
                        initiateActualEncryptedCall();
                    }
                } else if ((phoneCall instanceof TLRPC.TL_phoneCallAccepted) && this.authKey == null) {
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
                    int i2 = this.spPlayID;
                    if (i2 != 0) {
                        this.soundPool.stop(i2);
                    }
                    this.spPlayID = this.soundPool.play(this.spRingbackID, 1.0f, 1.0f, 0, -1, 1.0f);
                    Runnable runnable2 = this.timeoutRunnable;
                    if (runnable2 != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable2);
                        this.timeoutRunnable = null;
                    }
                    this.timeoutRunnable = new Runnable() {
                        public void run() {
                            VoIPService voIPService = VoIPService.this;
                            voIPService.timeoutRunnable = null;
                            voIPService.declineIncomingCall(3, (Runnable) null);
                        }
                    };
                    AndroidUtilities.runOnUIThread(this.timeoutRunnable, (long) MessagesController.getInstance(this.currentAccount).callRingTimeout);
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.w("onCallUpdated called with wrong call id (got " + phoneCall.id + ", expected " + this.call.id + ")");
            }
        }
    }

    private void startRatingActivity() {
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).putExtra("call_id", this.call.id).putExtra("call_access_hash", this.call.access_hash).putExtra("account", this.currentAccount).addFlags(NUM), 0).send();
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
        BigInteger bigInteger2 = new BigInteger(1, this.call.g_b);
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
        } else if (byteArray.length < 256) {
            bArr = new byte[256];
            System.arraycopy(byteArray, 0, bArr, 256 - byteArray.length, byteArray.length);
            for (int i = 0; i < 256 - byteArray.length; i++) {
                bArr[i] = 0;
            }
        } else {
            bArr = byteArray;
        }
        byte[] computeSHA1 = Utilities.computeSHA1(bArr);
        byte[] bArr2 = new byte[8];
        System.arraycopy(computeSHA1, computeSHA1.length - 8, bArr2, 0, 8);
        long bytesToLong = Utilities.bytesToLong(bArr2);
        this.authKey = bArr;
        this.keyFingerprint = bytesToLong;
        TLRPC.TL_phone_confirmCall tL_phone_confirmCall = new TLRPC.TL_phone_confirmCall();
        tL_phone_confirmCall.g_a = this.g_a;
        tL_phone_confirmCall.key_fingerprint = bytesToLong;
        tL_phone_confirmCall.peer = new TLRPC.TL_inputPhoneCall();
        TLRPC.TL_inputPhoneCall tL_inputPhoneCall = tL_phone_confirmCall.peer;
        TLRPC.PhoneCall phoneCall = this.call;
        tL_inputPhoneCall.id = phoneCall.id;
        tL_inputPhoneCall.access_hash = phoneCall.access_hash;
        tL_phone_confirmCall.protocol = new TLRPC.TL_phoneCallProtocol();
        TLRPC.TL_phoneCallProtocol tL_phoneCallProtocol = tL_phone_confirmCall.protocol;
        tL_phoneCallProtocol.max_layer = CALL_MAX_LAYER;
        tL_phoneCallProtocol.min_layer = 65;
        tL_phoneCallProtocol.udp_reflector = true;
        tL_phoneCallProtocol.udp_p2p = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_phone_confirmCall, new RequestDelegate() {
            public void run(final TLObject tLObject, final TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (tL_error != null) {
                            VoIPService.this.callFailed();
                            return;
                        }
                        TLRPC.PhoneCall unused = VoIPService.this.call = ((TLRPC.TL_phone_phoneCall) tLObject).phone_call;
                        VoIPService.this.initiateActualEncryptedCall();
                    }
                });
            }
        });
    }

    private int convertDataSavingMode(int i) {
        if (i != 3) {
            return i;
        }
        return ApplicationLoader.isRoaming() ? 1 : 0;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r8.remove();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x009a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initiateActualEncryptedCall() {
        /*
            r14 = this;
            java.lang.String r0 = "dbg_force_tcp_in_calls"
            java.lang.String r1 = "calls_access_hashes"
            java.lang.String r2 = " "
            java.lang.Runnable r3 = r14.timeoutRunnable
            r4 = 0
            if (r3 == 0) goto L_0x0010
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r3)
            r14.timeoutRunnable = r4
        L_0x0010:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0187 }
            if (r3 == 0) goto L_0x002a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0187 }
            r3.<init>()     // Catch:{ Exception -> 0x0187 }
            java.lang.String r5 = "InitCall: keyID="
            r3.append(r5)     // Catch:{ Exception -> 0x0187 }
            long r5 = r14.keyFingerprint     // Catch:{ Exception -> 0x0187 }
            r3.append(r5)     // Catch:{ Exception -> 0x0187 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0187 }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x0187 }
        L_0x002a:
            int r3 = r14.currentAccount     // Catch:{ Exception -> 0x0187 }
            android.content.SharedPreferences r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3)     // Catch:{ Exception -> 0x0187 }
            java.util.HashSet r5 = new java.util.HashSet     // Catch:{ Exception -> 0x0187 }
            java.util.Set r6 = java.util.Collections.EMPTY_SET     // Catch:{ Exception -> 0x0187 }
            java.util.Set r6 = r3.getStringSet(r1, r6)     // Catch:{ Exception -> 0x0187 }
            r5.<init>(r6)     // Catch:{ Exception -> 0x0187 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0187 }
            r6.<init>()     // Catch:{ Exception -> 0x0187 }
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r14.call     // Catch:{ Exception -> 0x0187 }
            long r7 = r7.id     // Catch:{ Exception -> 0x0187 }
            r6.append(r7)     // Catch:{ Exception -> 0x0187 }
            r6.append(r2)     // Catch:{ Exception -> 0x0187 }
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r14.call     // Catch:{ Exception -> 0x0187 }
            long r7 = r7.access_hash     // Catch:{ Exception -> 0x0187 }
            r6.append(r7)     // Catch:{ Exception -> 0x0187 }
            r6.append(r2)     // Catch:{ Exception -> 0x0187 }
            long r7 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0187 }
            r6.append(r7)     // Catch:{ Exception -> 0x0187 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0187 }
            r5.add(r6)     // Catch:{ Exception -> 0x0187 }
        L_0x0062:
            int r6 = r5.size()     // Catch:{ Exception -> 0x0187 }
            r7 = 20
            if (r6 <= r7) goto L_0x00a4
            r6 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            java.util.Iterator r8 = r5.iterator()     // Catch:{ Exception -> 0x0187 }
            r9 = r6
            r6 = r4
        L_0x0075:
            boolean r7 = r8.hasNext()     // Catch:{ Exception -> 0x0187 }
            if (r7 == 0) goto L_0x009e
            java.lang.Object r7 = r8.next()     // Catch:{ Exception -> 0x0187 }
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ Exception -> 0x0187 }
            java.lang.String[] r11 = r7.split(r2)     // Catch:{ Exception -> 0x0187 }
            int r12 = r11.length     // Catch:{ Exception -> 0x0187 }
            r13 = 2
            if (r12 >= r13) goto L_0x008d
            r8.remove()     // Catch:{ Exception -> 0x0187 }
            goto L_0x0075
        L_0x008d:
            r11 = r11[r13]     // Catch:{ Exception -> 0x009a }
            long r11 = java.lang.Long.parseLong(r11)     // Catch:{ Exception -> 0x009a }
            int r13 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r13 >= 0) goto L_0x0075
            r6 = r7
            r9 = r11
            goto L_0x0075
        L_0x009a:
            r8.remove()     // Catch:{ Exception -> 0x0187 }
            goto L_0x0075
        L_0x009e:
            if (r6 == 0) goto L_0x0062
            r5.remove(r6)     // Catch:{ Exception -> 0x0187 }
            goto L_0x0062
        L_0x00a4:
            android.content.SharedPreferences$Editor r2 = r3.edit()     // Catch:{ Exception -> 0x0187 }
            android.content.SharedPreferences$Editor r1 = r2.putStringSet(r1, r5)     // Catch:{ Exception -> 0x0187 }
            r1.commit()     // Catch:{ Exception -> 0x0187 }
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0187 }
            org.telegram.messenger.voip.VoIPController r5 = r14.controller     // Catch:{ Exception -> 0x0187 }
            int r2 = r14.currentAccount     // Catch:{ Exception -> 0x0187 }
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)     // Catch:{ Exception -> 0x0187 }
            int r2 = r2.callPacketTimeout     // Catch:{ Exception -> 0x0187 }
            double r2 = (double) r2
            r6 = 4652007308841189376(0x408fNUM, double:1000.0)
            java.lang.Double.isNaN(r2)
            double r2 = r2 / r6
            int r8 = r14.currentAccount     // Catch:{ Exception -> 0x0187 }
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)     // Catch:{ Exception -> 0x0187 }
            int r8 = r8.callConnectTimeout     // Catch:{ Exception -> 0x0187 }
            double r8 = (double) r8
            java.lang.Double.isNaN(r8)
            double r8 = r8 / r6
            java.lang.String r6 = "VoipDataSaving"
            int r7 = org.telegram.ui.Components.voip.VoIPHelper.getDataSavingDefault()     // Catch:{ Exception -> 0x0187 }
            int r1 = r1.getInt(r6, r7)     // Catch:{ Exception -> 0x0187 }
            int r10 = r14.convertDataSavingMode(r1)     // Catch:{ Exception -> 0x0187 }
            org.telegram.tgnet.TLRPC$PhoneCall r1 = r14.call     // Catch:{ Exception -> 0x0187 }
            long r11 = r1.id     // Catch:{ Exception -> 0x0187 }
            r6 = r2
            r5.setConfig(r6, r8, r10, r11)     // Catch:{ Exception -> 0x0187 }
            org.telegram.messenger.voip.VoIPController r1 = r14.controller     // Catch:{ Exception -> 0x0187 }
            byte[] r2 = r14.authKey     // Catch:{ Exception -> 0x0187 }
            boolean r3 = r14.isOutgoing     // Catch:{ Exception -> 0x0187 }
            r1.setEncryptionKey(r2, r3)     // Catch:{ Exception -> 0x0187 }
            org.telegram.tgnet.TLRPC$PhoneCall r1 = r14.call     // Catch:{ Exception -> 0x0187 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_phoneConnection> r1 = r1.connections     // Catch:{ Exception -> 0x0187 }
            org.telegram.tgnet.TLRPC$PhoneCall r2 = r14.call     // Catch:{ Exception -> 0x0187 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_phoneConnection> r2 = r2.connections     // Catch:{ Exception -> 0x0187 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0187 }
            org.telegram.tgnet.TLRPC$TL_phoneConnection[] r2 = new org.telegram.tgnet.TLRPC.TL_phoneConnection[r2]     // Catch:{ Exception -> 0x0187 }
            java.lang.Object[] r1 = r1.toArray(r2)     // Catch:{ Exception -> 0x0187 }
            org.telegram.tgnet.TLRPC$TL_phoneConnection[] r1 = (org.telegram.tgnet.TLRPC.TL_phoneConnection[]) r1     // Catch:{ Exception -> 0x0187 }
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0187 }
            org.telegram.messenger.voip.VoIPController r3 = r14.controller     // Catch:{ Exception -> 0x0187 }
            org.telegram.tgnet.TLRPC$PhoneCall r5 = r14.call     // Catch:{ Exception -> 0x0187 }
            boolean r5 = r5.p2p_allowed     // Catch:{ Exception -> 0x0187 }
            r6 = 0
            boolean r7 = r2.getBoolean(r0, r6)     // Catch:{ Exception -> 0x0187 }
            org.telegram.tgnet.TLRPC$PhoneCall r8 = r14.call     // Catch:{ Exception -> 0x0187 }
            org.telegram.tgnet.TLRPC$TL_phoneCallProtocol r8 = r8.protocol     // Catch:{ Exception -> 0x0187 }
            int r8 = r8.max_layer     // Catch:{ Exception -> 0x0187 }
            r3.setRemoteEndpoints(r1, r5, r7, r8)     // Catch:{ Exception -> 0x0187 }
            boolean r0 = r2.getBoolean(r0, r6)     // Catch:{ Exception -> 0x0187 }
            if (r0 == 0) goto L_0x012d
            org.telegram.messenger.voip.VoIPService$14 r0 = new org.telegram.messenger.voip.VoIPService$14     // Catch:{ Exception -> 0x0187 }
            r0.<init>()     // Catch:{ Exception -> 0x0187 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x0187 }
        L_0x012d:
            java.lang.String r0 = "proxy_enabled"
            boolean r0 = r2.getBoolean(r0, r6)     // Catch:{ Exception -> 0x0187 }
            if (r0 == 0) goto L_0x016c
            java.lang.String r0 = "proxy_enabled_calls"
            boolean r0 = r2.getBoolean(r0, r6)     // Catch:{ Exception -> 0x0187 }
            if (r0 == 0) goto L_0x016c
            java.lang.String r0 = "proxy_ip"
            java.lang.String r0 = r2.getString(r0, r4)     // Catch:{ Exception -> 0x0187 }
            java.lang.String r1 = "proxy_secret"
            java.lang.String r1 = r2.getString(r1, r4)     // Catch:{ Exception -> 0x0187 }
            boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0187 }
            if (r3 != 0) goto L_0x016c
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x016c
            org.telegram.messenger.voip.VoIPController r1 = r14.controller     // Catch:{ Exception -> 0x0187 }
            java.lang.String r3 = "proxy_port"
            int r3 = r2.getInt(r3, r6)     // Catch:{ Exception -> 0x0187 }
            java.lang.String r5 = "proxy_user"
            java.lang.String r5 = r2.getString(r5, r4)     // Catch:{ Exception -> 0x0187 }
            java.lang.String r6 = "proxy_pass"
            java.lang.String r2 = r2.getString(r6, r4)     // Catch:{ Exception -> 0x0187 }
            r1.setProxy(r0, r3, r5, r2)     // Catch:{ Exception -> 0x0187 }
        L_0x016c:
            org.telegram.messenger.voip.VoIPController r0 = r14.controller     // Catch:{ Exception -> 0x0187 }
            r0.start()     // Catch:{ Exception -> 0x0187 }
            r14.updateNetworkType()     // Catch:{ Exception -> 0x0187 }
            org.telegram.messenger.voip.VoIPController r0 = r14.controller     // Catch:{ Exception -> 0x0187 }
            r0.connect()     // Catch:{ Exception -> 0x0187 }
            r0 = 1
            r14.controllerStarted = r0     // Catch:{ Exception -> 0x0187 }
            org.telegram.messenger.voip.VoIPService$15 r0 = new org.telegram.messenger.voip.VoIPService$15     // Catch:{ Exception -> 0x0187 }
            r0.<init>()     // Catch:{ Exception -> 0x0187 }
            r1 = 5000(0x1388, double:2.4703E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)     // Catch:{ Exception -> 0x0187 }
            goto L_0x0194
        L_0x0187:
            r0 = move-exception
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x0191
            java.lang.String r1 = "error starting call"
            org.telegram.messenger.FileLog.e(r1, r0)
        L_0x0191:
            r14.callFailed()
        L_0x0194:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.initiateActualEncryptedCall():void");
    }

    /* access modifiers changed from: protected */
    public void showNotification() {
        TLRPC.User user2 = this.user;
        String formatName = ContactsController.formatName(user2.first_name, user2.last_name);
        TLRPC.UserProfilePhoto userProfilePhoto = this.user.photo;
        showNotification(formatName, userProfilePhoto != null ? userProfilePhoto.photo_small : null, VoIPActivity.class);
    }

    private void startConnectingSound() {
        int i = this.spPlayID;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayID = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        if (this.spPlayID == 0) {
            AnonymousClass16 r0 = new Runnable() {
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

    /* access modifiers changed from: protected */
    public void callFailed(int i) {
        if (this.call != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Discarding failed call");
            }
            TLRPC.TL_phone_discardCall tL_phone_discardCall = new TLRPC.TL_phone_discardCall();
            tL_phone_discardCall.peer = new TLRPC.TL_inputPhoneCall();
            TLRPC.TL_inputPhoneCall tL_inputPhoneCall = tL_phone_discardCall.peer;
            TLRPC.PhoneCall phoneCall = this.call;
            tL_inputPhoneCall.access_hash = phoneCall.access_hash;
            tL_inputPhoneCall.id = phoneCall.id;
            VoIPController voIPController = this.controller;
            tL_phone_discardCall.duration = (voIPController == null || !this.controllerStarted) ? 0 : (int) (voIPController.getCallDuration() / 1000);
            VoIPController voIPController2 = this.controller;
            tL_phone_discardCall.connection_id = (voIPController2 == null || !this.controllerStarted) ? 0 : voIPController2.getPreferredRelayID();
            tL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_phone_discardCall, new RequestDelegate() {
                public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    if (tL_error != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("error on phone.discardCall: " + tL_error);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("phone.discardCall " + tLObject);
                    }
                }
            });
        }
        super.callFailed(i);
    }

    public long getCallID() {
        TLRPC.PhoneCall phoneCall = this.call;
        if (phoneCall != null) {
            return phoneCall.id;
        }
        return 0;
    }

    public void onUIForegroundStateChanged(boolean z) {
        if (Build.VERSION.SDK_INT >= 21 || this.currentState != 15) {
            return;
        }
        if (z) {
            stopForeground(true);
        } else if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(VoIPService.this, VoIPActivity.class);
                    intent.addFlags(NUM);
                    try {
                        PendingIntent.getActivity(VoIPService.this, 0, intent, 0).send();
                    } catch (PendingIntent.CanceledException e) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("error restarting activity", e);
                        }
                        VoIPService.this.declineIncomingCall(4, (Runnable) null);
                    }
                    if (Build.VERSION.SDK_INT >= 26) {
                        VoIPService.this.showNotification();
                    }
                }
            }, 500);
        } else if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            TLRPC.User user2 = this.user;
            showIncomingNotification(ContactsController.formatName(user2.first_name, user2.last_name), (CharSequence) null, this.user, (List<TLRPC.User>) null, 0, VoIPActivity.class);
        } else {
            declineIncomingCall(4, (Runnable) null);
        }
    }

    /* access modifiers changed from: package-private */
    public void onMediaButtonEvent(KeyEvent keyEvent) {
        if ((keyEvent.getKeyCode() != 79 && keyEvent.getKeyCode() != 127 && keyEvent.getKeyCode() != 85) || keyEvent.getAction() != 1) {
            return;
        }
        if (this.currentState == 15) {
            acceptIncomingCall();
            return;
        }
        setMicMute(!isMicMute());
        Iterator<VoIPBaseService.StateListener> it = this.stateListeners.iterator();
        while (it.hasNext()) {
            it.next().onAudioSettingsChanged();
        }
    }

    public void debugCtl(int i, int i2) {
        VoIPController voIPController = this.controller;
        if (voIPController != null) {
            voIPController.debugCtl(i, i2);
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

    public boolean canUpgrate() {
        return (this.peerCapabilities & 1) == 1;
    }

    public void upgradeToGroupCall(List<Integer> list) {
        if (!this.upgrading) {
            this.groupUsersToAdd = list;
            if (!this.isOutgoing) {
                this.controller.requestCallUpgrade();
                return;
            }
            this.upgrading = true;
            this.groupCallEncryptionKey = new byte[256];
            Utilities.random.nextBytes(this.groupCallEncryptionKey);
            byte[] bArr = this.groupCallEncryptionKey;
            bArr[0] = (byte) (bArr[0] & Byte.MAX_VALUE);
            byte[] computeSHA1 = Utilities.computeSHA1(bArr);
            byte[] bArr2 = new byte[8];
            System.arraycopy(computeSHA1, computeSHA1.length - 8, bArr2, 0, 8);
            this.groupCallKeyFingerprint = Utilities.bytesToLong(bArr2);
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
        byte[] computeSHA1 = Utilities.computeSHA1(this.groupCallEncryptionKey);
        byte[] bArr2 = new byte[8];
        System.arraycopy(computeSHA1, computeSHA1.length - 8, bArr2, 0, 8);
        this.groupCallKeyFingerprint = Utilities.bytesToLong(bArr2);
    }

    public void onCallUpgradeRequestReceived() {
        upgradeToGroupCall(new ArrayList());
    }

    @TargetApi(26)
    public VoIPBaseService.CallConnection getConnectionAndStartCall() {
        if (this.systemCallConnection == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("creating call connection");
            }
            this.systemCallConnection = new VoIPBaseService.CallConnection();
            this.systemCallConnection.setInitializing();
            if (this.isOutgoing) {
                this.delayedStartOutgoingCall = new Runnable() {
                    public void run() {
                        Runnable unused = VoIPService.this.delayedStartOutgoingCall = null;
                        VoIPService.this.startOutgoingCall();
                    }
                };
                AndroidUtilities.runOnUIThread(this.delayedStartOutgoingCall, 2000);
            }
            VoIPBaseService.CallConnection callConnection = this.systemCallConnection;
            callConnection.setAddress(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), 1);
            VoIPBaseService.CallConnection callConnection2 = this.systemCallConnection;
            TLRPC.User user2 = this.user;
            callConnection2.setCallerDisplayName(ContactsController.formatName(user2.first_name, user2.last_name), 1);
        }
        return this.systemCallConnection;
    }
}
