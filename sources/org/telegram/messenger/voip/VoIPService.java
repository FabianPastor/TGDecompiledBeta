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
import android.os.SystemClock;
import android.telecom.TelecomManager;
import android.view.KeyEvent;
import android.widget.Toast;
import androidx.core.app.NotificationManagerCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
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
import org.telegram.messenger.voip.TgVoip;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$PhoneCall;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC$TL_messages_dhConfig;
import org.telegram.tgnet.TLRPC$TL_messages_getDhConfig;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonDisconnect;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonHangup;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscarded;
import org.telegram.tgnet.TLRPC$TL_phoneCallProtocol;
import org.telegram.tgnet.TLRPC$TL_phone_acceptCall;
import org.telegram.tgnet.TLRPC$TL_phone_confirmCall;
import org.telegram.tgnet.TLRPC$TL_phone_discardCall;
import org.telegram.tgnet.TLRPC$TL_phone_getCallConfig;
import org.telegram.tgnet.TLRPC$TL_phone_phoneCall;
import org.telegram.tgnet.TLRPC$TL_phone_receivedCall;
import org.telegram.tgnet.TLRPC$TL_phone_requestCall;
import org.telegram.tgnet.TLRPC$TL_phone_saveCallDebug;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$messages_DhConfig;
import org.telegram.ui.VoIPActivity;
import org.telegram.ui.VoIPFeedbackActivity;

public class VoIPService extends VoIPBaseService {
    public static final int CALL_MIN_LAYER = 65;
    public static final int STATE_BUSY = 17;
    public static final int STATE_EXCHANGING_KEYS = 12;
    public static final int STATE_HANGING_UP = 10;
    public static final int STATE_REQUESTING = 14;
    public static final int STATE_RINGING = 16;
    public static final int STATE_WAITING = 13;
    public static final int STATE_WAITING_INCOMING = 15;
    public static TLRPC$PhoneCall callIShouldHavePutIntoIntent;
    /* access modifiers changed from: private */
    public byte[] a_or_b;
    private byte[] authKey;
    /* access modifiers changed from: private */
    public TLRPC$PhoneCall call;
    /* access modifiers changed from: private */
    public int callReqId;
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
    private boolean needRateCall = false;
    private boolean needSendDebugLog = false;
    private int peerCapabilities;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$PhoneCall> pendingUpdates = new ArrayList<>();
    private boolean startedRinging = false;
    private boolean upgrading;
    /* access modifiers changed from: private */
    public TLRPC$User user;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onGroupCallKeySent() {
    }

    /* access modifiers changed from: protected */
    public void onTgVoipPreStop() {
    }

    @SuppressLint({"MissingPermission"})
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
            this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
            TLRPC$User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(intExtra2));
            this.user = user2;
            if (user2 == null) {
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
                    TLRPC$User tLRPC$User = this.user;
                    instance.createOrUpdateConnectionServiceContact(tLRPC$User.id, tLRPC$User.first_name, tLRPC$User.last_name);
                    ((TelecomManager) getSystemService("telecom")).placeCall(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), bundle);
                } else {
                    AnonymousClass1 r0 = new Runnable() {
                        public void run() {
                            Runnable unused = VoIPService.this.delayedStartOutgoingCall = null;
                            VoIPService.this.startOutgoingCall();
                        }
                    };
                    this.delayedStartOutgoingCall = r0;
                    AndroidUtilities.runOnUIThread(r0, 2000);
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
        SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        TgVoip.setGlobalServerConfig(mainSettings.getString("voip_server_config", "{}"));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_phone_getCallConfig(), new RequestDelegate(mainSettings) {
            private final /* synthetic */ SharedPreferences f$0;

            {
                this.f$0 = r1;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                VoIPService.lambda$updateServerConfig$0(this.f$0, tLObject, tLRPC$TL_error);
            }
        });
    }

    static /* synthetic */ void lambda$updateServerConfig$0(SharedPreferences sharedPreferences, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            String str = ((TLRPC$TL_dataJSON) tLObject).data;
            TgVoip.setGlobalServerConfig(str);
            sharedPreferences.edit().putString("voip_server_config", str).commit();
        }
    }

    /* access modifiers changed from: protected */
    public void onTgVoipStop(TgVoip.FinalState finalState) {
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
            TLRPC$PhoneCall tLRPC$PhoneCall = this.call;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_saveCallDebug, $$Lambda$VoIPService$1BA73Tqpn3TDFxZL0d2b3Q7IVjI.INSTANCE);
            this.needSendDebugLog = false;
        }
    }

    static /* synthetic */ void lambda$onTgVoipStop$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        TLRPC$TL_messages_getDhConfig tLRPC$TL_messages_getDhConfig = new TLRPC$TL_messages_getDhConfig();
        tLRPC$TL_messages_getDhConfig.random_length = 256;
        final MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
        tLRPC$TL_messages_getDhConfig.version = instance.getLastSecretVersion();
        this.callReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getDhConfig, new RequestDelegate() {
            public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                int unused = VoIPService.this.callReqId = 0;
                if (VoIPService.this.endCallAfterRequest) {
                    VoIPService.this.callEnded();
                } else if (tLRPC$TL_error == null) {
                    TLRPC$messages_DhConfig tLRPC$messages_DhConfig = (TLRPC$messages_DhConfig) tLObject;
                    if (tLObject instanceof TLRPC$TL_messages_dhConfig) {
                        if (!Utilities.isGoodPrime(tLRPC$messages_DhConfig.p, tLRPC$messages_DhConfig.g)) {
                            VoIPService.this.callFailed();
                            return;
                        }
                        instance.setSecretPBytes(tLRPC$messages_DhConfig.p);
                        instance.setSecretG(tLRPC$messages_DhConfig.g);
                        instance.setLastSecretVersion(tLRPC$messages_DhConfig.version);
                        MessagesStorage messagesStorage = instance;
                        messagesStorage.saveSecretParams(messagesStorage.getLastSecretVersion(), instance.getSecretG(), instance.getSecretPBytes());
                    }
                    final byte[] bArr = new byte[256];
                    for (int i = 0; i < 256; i++) {
                        bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ tLRPC$messages_DhConfig.random[i]);
                    }
                    byte[] byteArray = BigInteger.valueOf((long) instance.getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, instance.getSecretPBytes())).toByteArray();
                    if (byteArray.length > 256) {
                        byte[] bArr2 = new byte[256];
                        System.arraycopy(byteArray, 1, bArr2, 0, 256);
                        byteArray = bArr2;
                    }
                    TLRPC$TL_phone_requestCall tLRPC$TL_phone_requestCall = new TLRPC$TL_phone_requestCall();
                    tLRPC$TL_phone_requestCall.user_id = MessagesController.getInstance(VoIPService.this.currentAccount).getInputUser(VoIPService.this.user);
                    TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol = new TLRPC$TL_phoneCallProtocol();
                    tLRPC$TL_phone_requestCall.protocol = tLRPC$TL_phoneCallProtocol;
                    tLRPC$TL_phoneCallProtocol.udp_p2p = true;
                    tLRPC$TL_phoneCallProtocol.udp_reflector = true;
                    tLRPC$TL_phoneCallProtocol.min_layer = 65;
                    tLRPC$TL_phoneCallProtocol.max_layer = TgVoip.getConnectionMaxLayer();
                    tLRPC$TL_phone_requestCall.protocol.library_versions.addAll(TgVoip.getAvailableVersions());
                    byte[] unused2 = VoIPService.this.g_a = byteArray;
                    tLRPC$TL_phone_requestCall.g_a_hash = Utilities.computeSHA256(byteArray, 0, byteArray.length);
                    tLRPC$TL_phone_requestCall.random_id = Utilities.random.nextInt();
                    ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(tLRPC$TL_phone_requestCall, new RequestDelegate() {
                        public void run(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    TLRPC$TL_error tLRPC$TL_error = tLRPC$TL_error;
                                    if (tLRPC$TL_error == null) {
                                        TLRPC$PhoneCall unused = VoIPService.this.call = ((TLRPC$TL_phone_phoneCall) tLObject).phone_call;
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
                                                VoIPService.this.onCallUpdated((TLRPC$PhoneCall) it.next());
                                            }
                                            VoIPService.this.pendingUpdates.clear();
                                        }
                                        VoIPService.this.timeoutRunnable = new Runnable() {
                                            public void run() {
                                                VoIPService.this.timeoutRunnable = null;
                                                TLRPC$TL_phone_discardCall tLRPC$TL_phone_discardCall = new TLRPC$TL_phone_discardCall();
                                                TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
                                                tLRPC$TL_phone_discardCall.peer = tLRPC$TL_inputPhoneCall;
                                                tLRPC$TL_inputPhoneCall.access_hash = VoIPService.this.call.access_hash;
                                                tLRPC$TL_phone_discardCall.peer.id = VoIPService.this.call.id;
                                                tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonMissed();
                                                ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, new RequestDelegate() {
                                                    public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            if (tLRPC$TL_error != null) {
                                                                FileLog.e("error on phone.discardCall: " + tLRPC$TL_error);
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
                                    } else if (tLRPC$TL_error.code != 400 || !"PARTICIPANT_VERSION_OUTDATED".equals(tLRPC$TL_error.text)) {
                                        int i = tLRPC$TL_error.code;
                                        if (i == 403) {
                                            VoIPService.this.callFailed("ERROR_PRIVACY");
                                        } else if (i == 406) {
                                            VoIPService.this.callFailed("ERROR_LOCALIZED");
                                        } else {
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.e("Error on phone.requestCall: " + tLRPC$TL_error);
                                            }
                                            VoIPService.this.callFailed();
                                        }
                                    } else {
                                        VoIPService.this.callFailed("ERROR_PEER_OUTDATED");
                                    }
                                }
                            });
                        }
                    }, 2);
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Error on getDhConfig " + tLRPC$TL_error);
                    }
                    VoIPService.this.callFailed();
                }
            }
        }, 2);
    }

    private void acknowledgeCall(final boolean z) {
        if (this.call instanceof TLRPC$TL_phoneCallDiscarded) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("Call " + this.call.id + " was discarded before the service started, stopping");
            }
            stopSelf();
        } else if (Build.VERSION.SDK_INT < 19 || !XiaomiUtilities.isMIUI() || XiaomiUtilities.isCustomPermissionGranted(10020) || !((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            TLRPC$TL_phone_receivedCall tLRPC$TL_phone_receivedCall = new TLRPC$TL_phone_receivedCall();
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_receivedCall.peer = tLRPC$TL_inputPhoneCall;
            TLRPC$PhoneCall tLRPC$PhoneCall = this.call;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_receivedCall, new RequestDelegate() {
                public void run(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (VoIPBaseService.sharedInstance != null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.w("receivedCall response = " + tLObject);
                                }
                                if (tLRPC$TL_error != null) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("error on receivedCall: " + tLRPC$TL_error);
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
                                AnonymousClass4 r0 = AnonymousClass4.this;
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
                TLRPC$User tLRPC$User = this.user;
                showIncomingNotification(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), (CharSequence) null, this.user, (List<TLRPC$User>) null, 0, VoIPActivity.class);
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
        TLRPC$TL_messages_getDhConfig tLRPC$TL_messages_getDhConfig = new TLRPC$TL_messages_getDhConfig();
        tLRPC$TL_messages_getDhConfig.random_length = 256;
        tLRPC$TL_messages_getDhConfig.version = instance.getLastSecretVersion();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getDhConfig, new RequestDelegate() {
            public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                if (tLRPC$TL_error == null) {
                    TLRPC$messages_DhConfig tLRPC$messages_DhConfig = (TLRPC$messages_DhConfig) tLObject;
                    if (tLObject instanceof TLRPC$TL_messages_dhConfig) {
                        if (!Utilities.isGoodPrime(tLRPC$messages_DhConfig.p, tLRPC$messages_DhConfig.g)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("stopping VoIP service, bad prime");
                            }
                            VoIPService.this.callFailed();
                            return;
                        }
                        instance.setSecretPBytes(tLRPC$messages_DhConfig.p);
                        instance.setSecretG(tLRPC$messages_DhConfig.g);
                        instance.setLastSecretVersion(tLRPC$messages_DhConfig.version);
                        MessagesStorage.getInstance(VoIPService.this.currentAccount).saveSecretParams(instance.getLastSecretVersion(), instance.getSecretG(), instance.getSecretPBytes());
                    }
                    byte[] bArr = new byte[256];
                    for (int i = 0; i < 256; i++) {
                        bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ tLRPC$messages_DhConfig.random[i]);
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
                    TLRPC$TL_phone_acceptCall tLRPC$TL_phone_acceptCall = new TLRPC$TL_phone_acceptCall();
                    tLRPC$TL_phone_acceptCall.g_b = byteArray;
                    TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
                    tLRPC$TL_phone_acceptCall.peer = tLRPC$TL_inputPhoneCall;
                    tLRPC$TL_inputPhoneCall.id = VoIPService.this.call.id;
                    tLRPC$TL_phone_acceptCall.peer.access_hash = VoIPService.this.call.access_hash;
                    TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol = new TLRPC$TL_phoneCallProtocol();
                    tLRPC$TL_phone_acceptCall.protocol = tLRPC$TL_phoneCallProtocol;
                    tLRPC$TL_phoneCallProtocol.udp_reflector = true;
                    tLRPC$TL_phoneCallProtocol.udp_p2p = true;
                    tLRPC$TL_phoneCallProtocol.min_layer = 65;
                    tLRPC$TL_phoneCallProtocol.max_layer = TgVoip.getConnectionMaxLayer();
                    tLRPC$TL_phone_acceptCall.protocol.library_versions.addAll(TgVoip.getAvailableVersions());
                    ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(tLRPC$TL_phone_acceptCall, new RequestDelegate() {
                        public void run(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (tLRPC$TL_error == null) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.w("accept call ok! " + tLObject);
                                        }
                                        TLRPC$PhoneCall unused = VoIPService.this.call = ((TLRPC$TL_phone_phoneCall) tLObject).phone_call;
                                        if (VoIPService.this.call instanceof TLRPC$TL_phoneCallDiscarded) {
                                            VoIPService voIPService = VoIPService.this;
                                            voIPService.onCallUpdated(voIPService.call);
                                            return;
                                        }
                                        return;
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("Error on phone.acceptCall: " + tLRPC$TL_error);
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
        final AnonymousClass8 r8;
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
            TLRPC$TL_phone_discardCall tLRPC$TL_phone_discardCall = new TLRPC$TL_phone_discardCall();
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_discardCall.peer = tLRPC$TL_inputPhoneCall;
            TLRPC$PhoneCall tLRPC$PhoneCall = this.call;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_phone_discardCall.duration = (int) (getCallDuration() / 1000);
            TgVoip.Instance instance = this.tgVoip;
            tLRPC$TL_phone_discardCall.connection_id = instance != null ? instance.getPreferredRelayId() : 0;
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
                AndroidUtilities.runOnUIThread(r8, (long) ((int) (TgVoip.getGlobalServerConfig().hangupUiTimeout * 1000.0d)));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, new RequestDelegate() {
                public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    if (tLRPC$TL_error == null) {
                        if (tLObject instanceof TLRPC$TL_updates) {
                            MessagesController.getInstance(VoIPService.this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("phone.discardCall " + tLObject);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("error on phone.discardCall: " + tLRPC$TL_error);
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
                for (Field field : TLRPC$PhoneCall.class.getFields()) {
                    FileLog.d(field.getName() + " = " + field.get(this.call));
                }
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x0160  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x016d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCallUpdated(org.telegram.tgnet.TLRPC$PhoneCall r10) {
        /*
            r9 = this;
            org.telegram.tgnet.TLRPC$PhoneCall r0 = r9.call
            if (r0 != 0) goto L_0x000a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhoneCall> r0 = r9.pendingUpdates
            r0.add(r10)
            return
        L_0x000a:
            if (r10 != 0) goto L_0x000d
            return
        L_0x000d:
            long r1 = r10.id
            long r3 = r0.id
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0041
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0040
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onCallUpdated called with wrong call id (got "
            r0.append(r1)
            long r1 = r10.id
            r0.append(r1)
            java.lang.String r10 = ", expected "
            r0.append(r10)
            org.telegram.tgnet.TLRPC$PhoneCall r10 = r9.call
            long r1 = r10.id
            r0.append(r1)
            java.lang.String r10 = ")"
            r0.append(r10)
            java.lang.String r10 = r0.toString()
            org.telegram.messenger.FileLog.w(r10)
        L_0x0040:
            return
        L_0x0041:
            long r1 = r10.access_hash
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x004d
            long r0 = r0.access_hash
            r10.access_hash = r0
        L_0x004d:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0068
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Call updated: "
            r0.append(r1)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            r9.dumpCallObject()
        L_0x0068:
            r9.call = r10
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallDiscarded
            r1 = 1
            if (r0 == 0) goto L_0x00b0
            boolean r0 = r10.need_debug
            r9.needSendDebugLog = r0
            boolean r0 = r10.need_rating
            r9.needRateCall = r0
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0080
            java.lang.String r0 = "call discarded, stopping service"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0080:
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r10 = r10.reason
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy
            if (r10 == 0) goto L_0x00ab
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
            goto L_0x01d6
        L_0x00ab:
            r9.callEnded()
            goto L_0x01d6
        L_0x00b0:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_phoneCall
            if (r0 == 0) goto L_0x0171
            byte[] r0 = r9.authKey
            if (r0 != 0) goto L_0x0171
            byte[] r0 = r10.g_a_or_b
            if (r0 != 0) goto L_0x00c9
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r10 == 0) goto L_0x00c5
            java.lang.String r10 = "stopping VoIP service, Ga == null"
            org.telegram.messenger.FileLog.w(r10)
        L_0x00c5:
            r9.callFailed()
            return
        L_0x00c9:
            byte[] r2 = r9.g_a_hash
            int r3 = r0.length
            r4 = 0
            byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r0, r4, r3)
            boolean r0 = java.util.Arrays.equals(r2, r0)
            if (r0 != 0) goto L_0x00e4
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r10 == 0) goto L_0x00e0
            java.lang.String r10 = "stopping VoIP service, Ga hash doesn't match"
            org.telegram.messenger.FileLog.w(r10)
        L_0x00e0:
            r9.callFailed()
            return
        L_0x00e4:
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
            if (r3 != 0) goto L_0x0111
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r10 == 0) goto L_0x010d
            java.lang.String r10 = "stopping VoIP service, bad Ga and Gb (accepting)"
            org.telegram.messenger.FileLog.w(r10)
        L_0x010d:
            r9.callFailed()
            return
        L_0x0111:
            java.math.BigInteger r3 = new java.math.BigInteger
            byte[] r5 = r9.a_or_b
            r3.<init>(r1, r5)
            java.math.BigInteger r0 = r0.modPow(r3, r2)
            byte[] r0 = r0.toByteArray()
            int r1 = r0.length
            r2 = 256(0x100, float:3.59E-43)
            if (r1 <= r2) goto L_0x012e
            byte[] r1 = new byte[r2]
            int r3 = r0.length
            int r3 = r3 - r2
            java.lang.System.arraycopy(r0, r3, r1, r4, r2)
        L_0x012c:
            r0 = r1
            goto L_0x0145
        L_0x012e:
            int r1 = r0.length
            if (r1 >= r2) goto L_0x0145
            byte[] r1 = new byte[r2]
            int r3 = r0.length
            int r3 = 256 - r3
            int r5 = r0.length
            java.lang.System.arraycopy(r0, r4, r1, r3, r5)
            r3 = 0
        L_0x013b:
            int r5 = r0.length
            int r5 = 256 - r5
            if (r3 >= r5) goto L_0x012c
            r1[r3] = r4
            int r3 = r3 + 1
            goto L_0x013b
        L_0x0145:
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
            if (r10 == 0) goto L_0x016d
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r10 == 0) goto L_0x0169
            java.lang.String r10 = "key fingerprints don't match"
            org.telegram.messenger.FileLog.w(r10)
        L_0x0169:
            r9.callFailed()
            return
        L_0x016d:
            r9.initiateActualEncryptedCall()
            goto L_0x01d6
        L_0x0171:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallAccepted
            if (r0 == 0) goto L_0x017d
            byte[] r0 = r9.authKey
            if (r0 != 0) goto L_0x017d
            r9.processAcceptedCall()
            goto L_0x01d6
        L_0x017d:
            int r0 = r9.currentState
            r1 = 13
            if (r0 != r1) goto L_0x01d6
            int r10 = r10.receive_date
            if (r10 == 0) goto L_0x01d6
            r10 = 16
            r9.dispatchStateChanged(r10)
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r10 == 0) goto L_0x0195
            java.lang.String r10 = "!!!!!! CALL RECEIVED"
            org.telegram.messenger.FileLog.d(r10)
        L_0x0195:
            java.lang.Runnable r10 = r9.connectingSoundRunnable
            r0 = 0
            if (r10 == 0) goto L_0x019f
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r10)
            r9.connectingSoundRunnable = r0
        L_0x019f:
            int r10 = r9.spPlayID
            if (r10 == 0) goto L_0x01a8
            android.media.SoundPool r1 = r9.soundPool
            r1.stop(r10)
        L_0x01a8:
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
            if (r10 == 0) goto L_0x01c3
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r10)
            r9.timeoutRunnable = r0
        L_0x01c3:
            org.telegram.messenger.voip.VoIPService$10 r10 = new org.telegram.messenger.voip.VoIPService$10
            r10.<init>()
            r9.timeoutRunnable = r10
            int r0 = r9.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r0 = r0.callRingTimeout
            long r0 = (long) r0
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r10, r0)
        L_0x01d6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.onCallUpdated(org.telegram.tgnet.TLRPC$PhoneCall):void");
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
            TLRPC$PhoneCall tLRPC$PhoneCall = this.call;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol = new TLRPC$TL_phoneCallProtocol();
            tLRPC$TL_phone_confirmCall.protocol = tLRPC$TL_phoneCallProtocol;
            tLRPC$TL_phoneCallProtocol.max_layer = TgVoip.getConnectionMaxLayer();
            TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol2 = tLRPC$TL_phone_confirmCall.protocol;
            tLRPC$TL_phoneCallProtocol2.min_layer = 65;
            tLRPC$TL_phoneCallProtocol2.udp_reflector = true;
            tLRPC$TL_phoneCallProtocol2.udp_p2p = true;
            tLRPC$TL_phoneCallProtocol2.library_versions.addAll(TgVoip.getAvailableVersions());
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_confirmCall, new RequestDelegate() {
                public void run(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (tLRPC$TL_error != null) {
                                VoIPService.this.callFailed();
                                return;
                            }
                            TLRPC$PhoneCall unused = VoIPService.this.call = ((TLRPC$TL_phone_phoneCall) tLObject).phone_call;
                            VoIPService.this.initiateActualEncryptedCall();
                        }
                    });
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
        TLRPC$PhoneCall tLRPC$PhoneCall2 = this.call;
        tLRPC$TL_inputPhoneCall2.id = tLRPC$PhoneCall2.id;
        tLRPC$TL_inputPhoneCall2.access_hash = tLRPC$PhoneCall2.access_hash;
        TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol3 = new TLRPC$TL_phoneCallProtocol();
        tLRPC$TL_phone_confirmCall2.protocol = tLRPC$TL_phoneCallProtocol3;
        tLRPC$TL_phoneCallProtocol3.max_layer = TgVoip.getConnectionMaxLayer();
        TLRPC$TL_phoneCallProtocol tLRPC$TL_phoneCallProtocol22 = tLRPC$TL_phone_confirmCall2.protocol;
        tLRPC$TL_phoneCallProtocol22.min_layer = 65;
        tLRPC$TL_phoneCallProtocol22.udp_reflector = true;
        tLRPC$TL_phoneCallProtocol22.udp_p2p = true;
        tLRPC$TL_phoneCallProtocol22.library_versions.addAll(TgVoip.getAvailableVersions());
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_confirmCall2, new RequestDelegate() {
            public void run(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (tLRPC$TL_error != null) {
                            VoIPService.this.callFailed();
                            return;
                        }
                        TLRPC$PhoneCall unused = VoIPService.this.call = ((TLRPC$TL_phone_phoneCall) tLObject).phone_call;
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
        r9.remove();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0099 */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0111 A[Catch:{ Exception -> 0x022d }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x011f A[Catch:{ Exception -> 0x022d }] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x013a A[Catch:{ Exception -> 0x022d }] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0176 A[Catch:{ Exception -> 0x022d }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0184 A[Catch:{ Exception -> 0x022d }, LOOP:2: B:65:0x0182->B:66:0x0184, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01ab A[Catch:{ Exception -> 0x022d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initiateActualEncryptedCall() {
        /*
            r31 = this;
            r1 = r31
            java.lang.String r0 = "calls_access_hashes"
            java.lang.String r2 = " "
            java.lang.Runnable r3 = r1.timeoutRunnable
            r4 = 0
            if (r3 == 0) goto L_0x0010
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r3)
            r1.timeoutRunnable = r4
        L_0x0010:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x022d }
            if (r3 == 0) goto L_0x002a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x022d }
            r3.<init>()     // Catch:{ Exception -> 0x022d }
            java.lang.String r5 = "InitCall: keyID="
            r3.append(r5)     // Catch:{ Exception -> 0x022d }
            long r5 = r1.keyFingerprint     // Catch:{ Exception -> 0x022d }
            r3.append(r5)     // Catch:{ Exception -> 0x022d }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x022d }
        L_0x002a:
            int r3 = r1.currentAccount     // Catch:{ Exception -> 0x022d }
            android.content.SharedPreferences r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3)     // Catch:{ Exception -> 0x022d }
            java.util.HashSet r5 = new java.util.HashSet     // Catch:{ Exception -> 0x022d }
            java.util.Set r6 = java.util.Collections.EMPTY_SET     // Catch:{ Exception -> 0x022d }
            java.util.Set r6 = r3.getStringSet(r0, r6)     // Catch:{ Exception -> 0x022d }
            r5.<init>(r6)     // Catch:{ Exception -> 0x022d }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x022d }
            r6.<init>()     // Catch:{ Exception -> 0x022d }
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r1.call     // Catch:{ Exception -> 0x022d }
            long r7 = r7.id     // Catch:{ Exception -> 0x022d }
            r6.append(r7)     // Catch:{ Exception -> 0x022d }
            r6.append(r2)     // Catch:{ Exception -> 0x022d }
            org.telegram.tgnet.TLRPC$PhoneCall r7 = r1.call     // Catch:{ Exception -> 0x022d }
            long r7 = r7.access_hash     // Catch:{ Exception -> 0x022d }
            r6.append(r7)     // Catch:{ Exception -> 0x022d }
            r6.append(r2)     // Catch:{ Exception -> 0x022d }
            long r7 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x022d }
            r6.append(r7)     // Catch:{ Exception -> 0x022d }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x022d }
            r5.add(r6)     // Catch:{ Exception -> 0x022d }
        L_0x0062:
            int r6 = r5.size()     // Catch:{ Exception -> 0x022d }
            r7 = 20
            r8 = 2
            if (r6 <= r7) goto L_0x00a3
            r6 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            java.util.Iterator r9 = r5.iterator()     // Catch:{ Exception -> 0x022d }
            r10 = r4
        L_0x0075:
            boolean r11 = r9.hasNext()     // Catch:{ Exception -> 0x022d }
            if (r11 == 0) goto L_0x009d
            java.lang.Object r11 = r9.next()     // Catch:{ Exception -> 0x022d }
            java.lang.String r11 = (java.lang.String) r11     // Catch:{ Exception -> 0x022d }
            java.lang.String[] r12 = r11.split(r2)     // Catch:{ Exception -> 0x022d }
            int r13 = r12.length     // Catch:{ Exception -> 0x022d }
            if (r13 >= r8) goto L_0x008c
            r9.remove()     // Catch:{ Exception -> 0x022d }
            goto L_0x0075
        L_0x008c:
            r12 = r12[r8]     // Catch:{ Exception -> 0x0099 }
            long r12 = java.lang.Long.parseLong(r12)     // Catch:{ Exception -> 0x0099 }
            int r14 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r14 >= 0) goto L_0x0075
            r10 = r11
            r6 = r12
            goto L_0x0075
        L_0x0099:
            r9.remove()     // Catch:{ Exception -> 0x022d }
            goto L_0x0075
        L_0x009d:
            if (r10 == 0) goto L_0x0062
            r5.remove(r10)     // Catch:{ Exception -> 0x022d }
            goto L_0x0062
        L_0x00a3:
            android.content.SharedPreferences$Editor r2 = r3.edit()     // Catch:{ Exception -> 0x022d }
            android.content.SharedPreferences$Editor r0 = r2.putStringSet(r0, r5)     // Catch:{ Exception -> 0x022d }
            r0.commit()     // Catch:{ Exception -> 0x022d }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x022d }
            r2 = 16
            r3 = 0
            if (r0 < r2) goto L_0x00c0
            boolean r0 = android.media.audiofx.AcousticEchoCanceler.isAvailable()     // Catch:{ Exception -> 0x00ba }
            goto L_0x00bb
        L_0x00ba:
            r0 = 0
        L_0x00bb:
            boolean r2 = android.media.audiofx.NoiseSuppressor.isAvailable()     // Catch:{ Exception -> 0x00c1 }
            goto L_0x00c2
        L_0x00c0:
            r0 = 0
        L_0x00c1:
            r2 = 0
        L_0x00c2:
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x022d }
            org.telegram.tgnet.TLRPC$PhoneCall r6 = r1.call     // Catch:{ Exception -> 0x022d }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r6 = r6.protocol     // Catch:{ Exception -> 0x022d }
            java.util.ArrayList<java.lang.String> r6 = r6.library_versions     // Catch:{ Exception -> 0x022d }
            java.lang.Object r6 = r6.get(r3)     // Catch:{ Exception -> 0x022d }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.voip.TgVoip.setNativeVersion(r1, r6)     // Catch:{ Exception -> 0x022d }
            int r6 = r1.currentAccount     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)     // Catch:{ Exception -> 0x022d }
            int r7 = r6.callConnectTimeout     // Catch:{ Exception -> 0x022d }
            double r9 = (double) r7
            r11 = 4652007308841189376(0x408fNUM, double:1000.0)
            java.lang.Double.isNaN(r9)
            double r14 = r9 / r11
            int r6 = r6.callPacketTimeout     // Catch:{ Exception -> 0x022d }
            double r6 = (double) r6
            java.lang.Double.isNaN(r6)
            double r16 = r6 / r11
            java.lang.String r6 = "VoipDataSaving"
            int r7 = org.telegram.ui.Components.voip.VoIPHelper.getDataSavingDefault()     // Catch:{ Exception -> 0x022d }
            int r6 = r5.getInt(r6, r7)     // Catch:{ Exception -> 0x022d }
            int r18 = r1.convertDataSavingMode(r6)     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.voip.TgVoip$ServerConfig r6 = org.telegram.messenger.voip.TgVoip.getGlobalServerConfig()     // Catch:{ Exception -> 0x022d }
            r7 = 1
            if (r0 == 0) goto L_0x010d
            boolean r0 = r6.useSystemAec     // Catch:{ Exception -> 0x022d }
            if (r0 != 0) goto L_0x010a
            goto L_0x010d
        L_0x010a:
            r20 = 0
            goto L_0x010f
        L_0x010d:
            r20 = 1
        L_0x010f:
            if (r2 == 0) goto L_0x0119
            boolean r0 = r6.useSystemNs     // Catch:{ Exception -> 0x022d }
            if (r0 != 0) goto L_0x0116
            goto L_0x0119
        L_0x0116:
            r21 = 0
            goto L_0x011b
        L_0x0119:
            r21 = 1
        L_0x011b:
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x022d }
            if (r0 == 0) goto L_0x013a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x022d }
            r0.<init>()     // Catch:{ Exception -> 0x022d }
            java.lang.String r2 = "voip"
            r0.append(r2)     // Catch:{ Exception -> 0x022d }
            org.telegram.tgnet.TLRPC$PhoneCall r2 = r1.call     // Catch:{ Exception -> 0x022d }
            long r6 = r2.id     // Catch:{ Exception -> 0x022d }
            r0.append(r6)     // Catch:{ Exception -> 0x022d }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x022d }
            java.lang.String r0 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath((java.lang.String) r0)     // Catch:{ Exception -> 0x022d }
            goto L_0x0142
        L_0x013a:
            org.telegram.tgnet.TLRPC$PhoneCall r0 = r1.call     // Catch:{ Exception -> 0x022d }
            long r6 = r0.id     // Catch:{ Exception -> 0x022d }
            java.lang.String r0 = org.telegram.ui.Components.voip.VoIPHelper.getLogFilePath((long) r6)     // Catch:{ Exception -> 0x022d }
        L_0x0142:
            r24 = r0
            org.telegram.messenger.voip.TgVoip$Config r0 = new org.telegram.messenger.voip.TgVoip$Config     // Catch:{ Exception -> 0x022d }
            org.telegram.tgnet.TLRPC$PhoneCall r2 = r1.call     // Catch:{ Exception -> 0x022d }
            boolean r2 = r2.p2p_allowed     // Catch:{ Exception -> 0x022d }
            r22 = 1
            r23 = 0
            org.telegram.tgnet.TLRPC$PhoneCall r6 = r1.call     // Catch:{ Exception -> 0x022d }
            org.telegram.tgnet.TLRPC$PhoneCallProtocol r6 = r6.protocol     // Catch:{ Exception -> 0x022d }
            int r6 = r6.max_layer     // Catch:{ Exception -> 0x022d }
            r13 = r0
            r19 = r2
            r25 = r6
            r13.<init>(r14, r16, r18, r19, r20, r21, r22, r23, r24, r25)     // Catch:{ Exception -> 0x022d }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x022d }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x022d }
            java.io.File r6 = r6.getFilesDir()     // Catch:{ Exception -> 0x022d }
            java.lang.String r7 = "voip_persistent_state.json"
            r2.<init>(r6, r7)     // Catch:{ Exception -> 0x022d }
            java.lang.String r26 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x022d }
            java.lang.String r2 = "dbg_force_tcp_in_calls"
            boolean r2 = r5.getBoolean(r2, r3)     // Catch:{ Exception -> 0x022d }
            if (r2 == 0) goto L_0x0177
            r8 = 3
        L_0x0177:
            org.telegram.tgnet.TLRPC$PhoneCall r6 = r1.call     // Catch:{ Exception -> 0x022d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_phoneConnection> r6 = r6.connections     // Catch:{ Exception -> 0x022d }
            int r6 = r6.size()     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.voip.TgVoip$Endpoint[] r7 = new org.telegram.messenger.voip.TgVoip.Endpoint[r6]     // Catch:{ Exception -> 0x022d }
            r15 = 0
        L_0x0182:
            if (r15 >= r6) goto L_0x01a9
            org.telegram.tgnet.TLRPC$PhoneCall r9 = r1.call     // Catch:{ Exception -> 0x022d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_phoneConnection> r9 = r9.connections     // Catch:{ Exception -> 0x022d }
            java.lang.Object r9 = r9.get(r15)     // Catch:{ Exception -> 0x022d }
            org.telegram.tgnet.TLRPC$TL_phoneConnection r9 = (org.telegram.tgnet.TLRPC$TL_phoneConnection) r9     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.voip.TgVoip$Endpoint r17 = new org.telegram.messenger.voip.TgVoip$Endpoint     // Catch:{ Exception -> 0x022d }
            long r10 = r9.id     // Catch:{ Exception -> 0x022d }
            java.lang.String r12 = r9.ip     // Catch:{ Exception -> 0x022d }
            java.lang.String r13 = r9.ipv6     // Catch:{ Exception -> 0x022d }
            int r14 = r9.port     // Catch:{ Exception -> 0x022d }
            byte[] r9 = r9.peer_tag     // Catch:{ Exception -> 0x022d }
            r16 = r9
            r9 = r17
            r18 = r15
            r15 = r8
            r9.<init>(r10, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x022d }
            r7[r18] = r17     // Catch:{ Exception -> 0x022d }
            int r15 = r18 + 1
            goto L_0x0182
        L_0x01a9:
            if (r2 == 0) goto L_0x01b3
            org.telegram.messenger.voip.-$$Lambda$VoIPService$lOsJLL8d8By2ZlFuKsdzloRN2Cw r2 = new org.telegram.messenger.voip.-$$Lambda$VoIPService$lOsJLL8d8By2ZlFuKsdzloRN2Cw     // Catch:{ Exception -> 0x022d }
            r2.<init>()     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ Exception -> 0x022d }
        L_0x01b3:
            java.lang.String r2 = "proxy_enabled"
            boolean r2 = r5.getBoolean(r2, r3)     // Catch:{ Exception -> 0x022d }
            if (r2 == 0) goto L_0x01f5
            java.lang.String r2 = "proxy_enabled_calls"
            boolean r2 = r5.getBoolean(r2, r3)     // Catch:{ Exception -> 0x022d }
            if (r2 == 0) goto L_0x01f5
            java.lang.String r2 = "proxy_ip"
            java.lang.String r2 = r5.getString(r2, r4)     // Catch:{ Exception -> 0x022d }
            java.lang.String r6 = "proxy_secret"
            java.lang.String r6 = r5.getString(r6, r4)     // Catch:{ Exception -> 0x022d }
            boolean r8 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x022d }
            if (r8 != 0) goto L_0x01f5
            boolean r6 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x022d }
            if (r6 == 0) goto L_0x01f5
            org.telegram.messenger.voip.TgVoip$Proxy r6 = new org.telegram.messenger.voip.TgVoip$Proxy     // Catch:{ Exception -> 0x022d }
            java.lang.String r8 = "proxy_port"
            int r3 = r5.getInt(r8, r3)     // Catch:{ Exception -> 0x022d }
            java.lang.String r8 = "proxy_user"
            java.lang.String r8 = r5.getString(r8, r4)     // Catch:{ Exception -> 0x022d }
            java.lang.String r9 = "proxy_pass"
            java.lang.String r4 = r5.getString(r9, r4)     // Catch:{ Exception -> 0x022d }
            r6.<init>(r2, r3, r8, r4)     // Catch:{ Exception -> 0x022d }
            r28 = r6
            goto L_0x01f7
        L_0x01f5:
            r28 = r4
        L_0x01f7:
            org.telegram.messenger.voip.TgVoip$EncryptionKey r2 = new org.telegram.messenger.voip.TgVoip$EncryptionKey     // Catch:{ Exception -> 0x022d }
            byte[] r3 = r1.authKey     // Catch:{ Exception -> 0x022d }
            boolean r4 = r1.isOutgoing     // Catch:{ Exception -> 0x022d }
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x022d }
            int r29 = r31.getNetworkType()     // Catch:{ Exception -> 0x022d }
            r25 = r0
            r27 = r7
            r30 = r2
            org.telegram.messenger.voip.TgVoip$Instance r0 = org.telegram.messenger.voip.TgVoip.makeInstance(r25, r26, r27, r28, r29, r30)     // Catch:{ Exception -> 0x022d }
            r1.tgVoip = r0     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.voip.-$$Lambda$b8A5yasDBlAjdpFqahxprborVAY r2 = new org.telegram.messenger.voip.-$$Lambda$b8A5yasDBlAjdpFqahxprborVAY     // Catch:{ Exception -> 0x022d }
            r2.<init>()     // Catch:{ Exception -> 0x022d }
            r0.setOnStateUpdatedListener(r2)     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.voip.TgVoip$Instance r0 = r1.tgVoip     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.voip.-$$Lambda$pnbijAD66hCvE4d8f2qqwg0X4UY r2 = new org.telegram.messenger.voip.-$$Lambda$pnbijAD66hCvE4d8f2qqwg0X4UY     // Catch:{ Exception -> 0x022d }
            r2.<init>()     // Catch:{ Exception -> 0x022d }
            r0.setOnSignalBarsUpdatedListener(r2)     // Catch:{ Exception -> 0x022d }
            org.telegram.messenger.voip.VoIPService$12 r0 = new org.telegram.messenger.voip.VoIPService$12     // Catch:{ Exception -> 0x022d }
            r0.<init>()     // Catch:{ Exception -> 0x022d }
            r2 = 5000(0x1388, double:2.4703E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r2)     // Catch:{ Exception -> 0x022d }
            goto L_0x023a
        L_0x022d:
            r0 = move-exception
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0237
            java.lang.String r2 = "error starting call"
            org.telegram.messenger.FileLog.e(r2, r0)
        L_0x0237:
            r31.callFailed()
        L_0x023a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPService.initiateActualEncryptedCall():void");
    }

    public /* synthetic */ void lambda$initiateActualEncryptedCall$2$VoIPService() {
        Toast.makeText(this, "This call uses TCP which will degrade its quality.", 0).show();
    }

    /* access modifiers changed from: protected */
    public void showNotification() {
        TLRPC$User tLRPC$User = this.user;
        String formatName = ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name);
        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = this.user.photo;
        showNotification(formatName, tLRPC$UserProfilePhoto != null ? tLRPC$UserProfilePhoto.photo_small : null, VoIPActivity.class);
    }

    private void startConnectingSound() {
        int i = this.spPlayID;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        int play = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        this.spPlayID = play;
        if (play == 0) {
            AnonymousClass13 r0 = new Runnable() {
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
    public void callFailed(String str) {
        if (this.call != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Discarding failed call");
            }
            TLRPC$TL_phone_discardCall tLRPC$TL_phone_discardCall = new TLRPC$TL_phone_discardCall();
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_discardCall.peer = tLRPC$TL_inputPhoneCall;
            TLRPC$PhoneCall tLRPC$PhoneCall = this.call;
            tLRPC$TL_inputPhoneCall.access_hash = tLRPC$PhoneCall.access_hash;
            tLRPC$TL_inputPhoneCall.id = tLRPC$PhoneCall.id;
            tLRPC$TL_phone_discardCall.duration = (int) (getCallDuration() / 1000);
            TgVoip.Instance instance = this.tgVoip;
            tLRPC$TL_phone_discardCall.connection_id = instance != null ? instance.getPreferredRelayId() : 0;
            tLRPC$TL_phone_discardCall.reason = new TLRPC$TL_phoneCallDiscardReasonDisconnect();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_phone_discardCall, new RequestDelegate() {
                public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    if (tLRPC$TL_error != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("error on phone.discardCall: " + tLRPC$TL_error);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("phone.discardCall " + tLObject);
                    }
                }
            });
        }
        super.callFailed(str);
    }

    public long getCallID() {
        TLRPC$PhoneCall tLRPC$PhoneCall = this.call;
        if (tLRPC$PhoneCall != null) {
            return tLRPC$PhoneCall.id;
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
            TLRPC$User tLRPC$User = this.user;
            showIncomingNotification(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), (CharSequence) null, this.user, (List<TLRPC$User>) null, 0, VoIPActivity.class);
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
                this.tgVoip.requestCallUpgrade();
                return;
            }
            this.upgrading = true;
            byte[] bArr = new byte[256];
            this.groupCallEncryptionKey = bArr;
            Utilities.random.nextBytes(bArr);
            byte[] bArr2 = this.groupCallEncryptionKey;
            bArr2[0] = (byte) (bArr2[0] & Byte.MAX_VALUE);
            byte[] computeSHA1 = Utilities.computeSHA1(bArr2);
            byte[] bArr3 = new byte[8];
            System.arraycopy(computeSHA1, computeSHA1.length - 8, bArr3, 0, 8);
            this.groupCallKeyFingerprint = Utilities.bytesToLong(bArr3);
            this.tgVoip.sendGroupCallKey(this.groupCallEncryptionKey);
        }
    }

    public void onConnectionStateChanged(int i) {
        if (i == 3) {
            if (this.callStartTime == 0) {
                this.callStartTime = SystemClock.elapsedRealtime();
            }
            this.peerCapabilities = this.tgVoip.getPeerCapabilities();
        }
        super.onConnectionStateChanged(i);
    }

    public void onGroupCallKeyReceived(byte[] bArr) {
        this.joiningGroupCall = true;
        this.groupCallEncryptionKey = bArr;
        byte[] computeSHA1 = Utilities.computeSHA1(bArr);
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
            VoIPBaseService.CallConnection callConnection = new VoIPBaseService.CallConnection();
            this.systemCallConnection = callConnection;
            callConnection.setInitializing();
            if (this.isOutgoing) {
                AnonymousClass16 r0 = new Runnable() {
                    public void run() {
                        Runnable unused = VoIPService.this.delayedStartOutgoingCall = null;
                        VoIPService.this.startOutgoingCall();
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
}
