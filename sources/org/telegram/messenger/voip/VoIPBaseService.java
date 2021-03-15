package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.Instance;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$PhoneCall;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_phone_editGroupCallParticipant;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPPermissionActivity;

@SuppressLint({"NewApi"})
public abstract class VoIPBaseService extends Service implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, VoIPController.ConnectionStateListener, NotificationCenter.NotificationCenterDelegate {
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final int AUDIO_ROUTE_BLUETOOTH = 2;
    public static final int AUDIO_ROUTE_EARPIECE = 0;
    public static final int AUDIO_ROUTE_SPEAKER = 1;
    public static final int DISCARD_REASON_DISCONNECT = 2;
    public static final int DISCARD_REASON_HANGUP = 1;
    public static final int DISCARD_REASON_LINE_BUSY = 4;
    public static final int DISCARD_REASON_MISSED = 3;
    protected static final int ID_INCOMING_CALL_NOTIFICATION = 202;
    protected static final int ID_ONGOING_CALL_NOTIFICATION = 201;
    protected static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    public static final int STATE_CREATING = 6;
    public static final int STATE_ENDED = 11;
    public static final int STATE_ESTABLISHED = 3;
    public static final int STATE_FAILED = 4;
    public static final int STATE_RECONNECTING = 5;
    public static final int STATE_WAIT_INIT = 1;
    public static final int STATE_WAIT_INIT_ACK = 2;
    protected static final boolean USE_CONNECTION_SERVICE = isDeviceCompatibleWithConnectionServiceAPI();
    protected static Runnable setModeRunnable;
    protected static VoIPBaseService sharedInstance;
    protected static final Object sync = new Object();
    protected Runnable afterSoundRunnable = new Runnable() {
        public void run() {
            AudioManager audioManager = (AudioManager) VoIPBaseService.this.getSystemService("audio");
            audioManager.abandonAudioFocus(VoIPBaseService.this);
            audioManager.unregisterMediaButtonEventReceiver(new ComponentName(VoIPBaseService.this, VoIPMediaButtonReceiver.class));
            if (!VoIPBaseService.USE_CONNECTION_SERVICE && VoIPBaseService.sharedInstance == null) {
                if (VoIPBaseService.this.isBtHeadsetConnected) {
                    audioManager.stopBluetoothSco();
                    audioManager.setBluetoothScoOn(false);
                    VoIPBaseService.this.bluetoothScoActive = false;
                }
                audioManager.setSpeakerphoneOn(false);
            }
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    VoIPBaseService.AnonymousClass1.this.lambda$run$0$VoIPBaseService$1();
                }
            });
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            $$Lambda$VoIPBaseService$1$XyEJU0yTVmLNUNhg1JpbS7IG8Do r2 = new Runnable(audioManager) {
                public final /* synthetic */ AudioManager f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    VoIPBaseService.AnonymousClass1.lambda$run$1(this.f$0);
                }
            };
            VoIPBaseService.setModeRunnable = r2;
            dispatchQueue.postRunnable(r2);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$run$0 */
        public /* synthetic */ void lambda$run$0$VoIPBaseService$1() {
            VoIPBaseService.this.soundPool.release();
        }

        static /* synthetic */ void lambda$run$1(AudioManager audioManager) {
            synchronized (VoIPBaseService.sync) {
                if (VoIPBaseService.setModeRunnable != null) {
                    VoIPBaseService.setModeRunnable = null;
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
    protected boolean audioConfigured;
    protected int audioRouteToSet = 2;
    protected boolean bluetoothScoActive;
    protected BluetoothAdapter btAdapter;
    protected int callDiscardReason;
    protected long callStartTime;
    protected TLRPC$Chat chat;
    protected Runnable connectingSoundRunnable;
    protected PowerManager.WakeLock cpuWakelock;
    protected boolean createGroupCall;
    protected int currentAccount = -1;
    protected int currentAudioState = 1;
    /* access modifiers changed from: private */
    public String currentBluetoothDeviceName;
    public boolean currentGroupModeStreaming = false;
    protected int currentState = 0;
    protected int currentStreamRequestId;
    protected int currentVideoState = 0;
    protected boolean didDeleteConnectionServiceContact;
    boolean fetchingBluetoothDeviceName;
    public ChatObject.Call groupCall;
    protected TLRPC$InputPeer groupCallPeer;
    protected boolean hasAudioFocus;
    public boolean hasFewPeers;
    protected boolean isBtHeadsetConnected;
    protected boolean isFrontFaceCamera = true;
    protected boolean isHeadsetPlugged;
    protected boolean isOutgoing;
    protected boolean isProximityNear;
    protected boolean isVideoAvailable;
    protected String joinHash;
    protected String lastError;
    protected NetworkInfo lastNetInfo;
    private Boolean mHasEarpiece;
    protected boolean micMute;
    protected String myJson;
    protected int mySource;
    protected boolean needPlayEndSound;
    protected boolean needSwitchToBluetoothAfterScoActivates;
    protected boolean notificationsDisabled;
    protected Runnable onDestroyRunnable;
    protected Notification ongoingCallNotification;
    protected boolean playedConnectedSound;
    protected boolean playingSound;
    protected Instance.TrafficStats prevTrafficStats;
    protected int previousAudioOutput = -1;
    public TLRPC$PhoneCall privateCall;
    protected PowerManager.WakeLock proximityWakelock;
    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            PowerManager.WakeLock wakeLock;
            boolean z = true;
            int i = 0;
            if ("android.intent.action.HEADSET_PLUG".equals(intent.getAction())) {
                VoIPBaseService.this.isHeadsetPlugged = intent.getIntExtra("state", 0) == 1;
                VoIPBaseService voIPBaseService = VoIPBaseService.this;
                if (voIPBaseService.isHeadsetPlugged && (wakeLock = voIPBaseService.proximityWakelock) != null && wakeLock.isHeld()) {
                    VoIPBaseService.this.proximityWakelock.release();
                }
                VoIPBaseService voIPBaseService2 = VoIPBaseService.this;
                if (voIPBaseService2.isHeadsetPlugged) {
                    AudioManager audioManager = (AudioManager) voIPBaseService2.getSystemService("audio");
                    if (audioManager.isSpeakerphoneOn()) {
                        VoIPBaseService.this.previousAudioOutput = 0;
                    } else if (audioManager.isBluetoothScoOn()) {
                        VoIPBaseService.this.previousAudioOutput = 2;
                    } else {
                        VoIPBaseService.this.previousAudioOutput = 1;
                    }
                    VoIPBaseService.this.setAudioOutput(1);
                } else {
                    int i2 = voIPBaseService2.previousAudioOutput;
                    if (i2 >= 0) {
                        voIPBaseService2.setAudioOutput(i2);
                        VoIPBaseService.this.previousAudioOutput = -1;
                    }
                }
                VoIPBaseService voIPBaseService3 = VoIPBaseService.this;
                voIPBaseService3.isProximityNear = false;
                voIPBaseService3.updateOutputGainControlState();
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                VoIPBaseService.this.updateNetworkType();
            } else if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("bt headset state = " + intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0));
                }
                VoIPBaseService voIPBaseService4 = VoIPBaseService.this;
                if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) != 2) {
                    z = false;
                }
                voIPBaseService4.updateBluetoothHeadsetState(z);
            } else if ("android.media.ACTION_SCO_AUDIO_STATE_UPDATED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Bluetooth SCO state updated: " + intExtra);
                }
                if (intExtra == 0) {
                    VoIPBaseService voIPBaseService5 = VoIPBaseService.this;
                    if (voIPBaseService5.isBtHeadsetConnected && (!voIPBaseService5.btAdapter.isEnabled() || VoIPBaseService.this.btAdapter.getProfileConnectionState(1) != 2)) {
                        VoIPBaseService.this.updateBluetoothHeadsetState(false);
                        return;
                    }
                }
                VoIPBaseService voIPBaseService6 = VoIPBaseService.this;
                boolean z2 = intExtra == 1;
                voIPBaseService6.bluetoothScoActive = z2;
                if (z2) {
                    voIPBaseService6.fetchBluetoothDeviceName();
                    VoIPBaseService voIPBaseService7 = VoIPBaseService.this;
                    if (voIPBaseService7.needSwitchToBluetoothAfterScoActivates) {
                        voIPBaseService7.needSwitchToBluetoothAfterScoActivates = false;
                        AudioManager audioManager2 = (AudioManager) voIPBaseService7.getSystemService("audio");
                        audioManager2.setSpeakerphoneOn(false);
                        audioManager2.setBluetoothScoOn(true);
                    }
                }
                Iterator<StateListener> it = VoIPBaseService.this.stateListeners.iterator();
                while (it.hasNext()) {
                    it.next().onAudioSettingsChanged();
                }
            } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(intent.getStringExtra("state"))) {
                    VoIPBaseService.this.hangUp();
                }
            } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                VoIPBaseService.this.screenOn = true;
                while (i < VoIPBaseService.this.stateListeners.size()) {
                    VoIPBaseService.this.stateListeners.get(i).onScreenOnChange(VoIPBaseService.this.screenOn);
                    i++;
                }
            } else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                VoIPBaseService.this.screenOn = false;
                while (i < VoIPBaseService.this.stateListeners.size()) {
                    VoIPBaseService.this.stateListeners.get(i).onScreenOnChange(VoIPBaseService.this.screenOn);
                    i++;
                }
            }
        }
    };
    protected MediaPlayer ringtonePlayer;
    protected boolean screenOn;
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
                    String unused = VoIPBaseService.this.currentBluetoothDeviceName = next.getName();
                    break;
                }
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(i, bluetoothProfile);
            VoIPBaseService.this.fetchingBluetoothDeviceName = false;
        }
    };
    public final SharedUIParams sharedUIParams = new SharedUIParams();
    protected int signalBarCount;
    protected SoundPool soundPool;
    protected int spBusyId;
    protected int spConnectingId;
    protected int spEndId;
    protected int spFailedID;
    protected int spPlayID;
    protected int spRingbackID;
    protected int spVoiceChatConnecting;
    protected int spVoiceChatEndId;
    protected int spVoiceChatStartId;
    protected boolean speakerphoneStateToSet;
    protected ArrayList<StateListener> stateListeners = new ArrayList<>();
    protected boolean switchingCamera;
    protected boolean switchingStream;
    protected CallConnection systemCallConnection;
    protected NativeInstance tgVoip;
    protected Runnable timeoutRunnable;
    protected boolean unmutedByHold;
    private Runnable updateNotificationRunnable;
    protected Vibrator vibrator;
    public boolean videoCall;
    protected long videoCapturer;
    protected int videoState = 0;
    protected boolean wasConnected;
    private boolean wasEstablished;

    public static class SharedUIParams {
        public boolean cameraAlertWasShowed;
        public boolean tapToVideoTooltipWasShowed;
        public boolean wasVideoCall;
    }

    public interface StateListener {

        /* renamed from: org.telegram.messenger.voip.VoIPBaseService$StateListener$-CC  reason: invalid class name */
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

    public abstract void acceptIncomingCall();

    public abstract void declineIncomingCall();

    public abstract void declineIncomingCall(int i, Runnable runnable);

    public abstract long getCallID();

    public abstract CallConnection getConnectionAndStartCall();

    /* access modifiers changed from: protected */
    public abstract Class<? extends Activity> getUIActivityClass();

    public abstract void hangUp();

    public abstract void hangUp(Runnable runnable);

    /* access modifiers changed from: protected */
    public boolean isRinging() {
        return false;
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* access modifiers changed from: protected */
    public void onTgVoipPreStop() {
    }

    /* access modifiers changed from: protected */
    public void onTgVoipStop(Instance.FinalState finalState) {
    }

    /* access modifiers changed from: protected */
    public abstract void showNotification();

    /* access modifiers changed from: protected */
    public abstract void startRinging();

    public abstract void startRingtoneAndVibration();

    /* access modifiers changed from: protected */
    public abstract void updateServerConfig();

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

    /* access modifiers changed from: protected */
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

    public void setMicMute(boolean z, boolean z2, boolean z3) {
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        if (this.micMute != z) {
            this.micMute = z;
            ChatObject.Call call = this.groupCall;
            boolean z4 = true;
            if (call != null) {
                if (!z3 && (tLRPC$TL_groupCallParticipant = call.participants.get(UserConfig.getInstance(this.currentAccount).getClientUserId())) != null && tLRPC$TL_groupCallParticipant.muted && !tLRPC$TL_groupCallParticipant.can_self_unmute) {
                    z3 = true;
                }
                if (z3) {
                    editCallMember(UserConfig.getInstance(this.currentAccount).getCurrentUser(), z, -1, (Boolean) null);
                    DispatchQueue dispatchQueue = Utilities.globalQueue;
                    $$Lambda$VoIPBaseService$2g4fCttj150miIvar_fb9pscys r0 = new Runnable() {
                        public final void run() {
                            VoIPBaseService.this.lambda$setMicMute$0$VoIPBaseService();
                        }
                    };
                    this.updateNotificationRunnable = r0;
                    dispatchQueue.postRunnable(r0);
                }
            }
            if (this.micMute || !z2) {
                z4 = false;
            }
            this.unmutedByHold = z4;
            NativeInstance nativeInstance = this.tgVoip;
            if (nativeInstance != null) {
                nativeInstance.setMuteMicrophone(z);
            }
            Iterator<StateListener> it = this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setMicMute$0 */
    public /* synthetic */ void lambda$setMicMute$0$VoIPBaseService() {
        if (this.updateNotificationRunnable != null) {
            this.updateNotificationRunnable = null;
            TLRPC$Chat tLRPC$Chat = this.chat;
            showNotification(tLRPC$Chat.title, getRoundAvatarBitmap(tLRPC$Chat));
        }
    }

    public void editCallMember(TLObject tLObject, boolean z, int i, Boolean bool) {
        if (this.groupCall != null) {
            TLRPC$TL_phone_editGroupCallParticipant tLRPC$TL_phone_editGroupCallParticipant = new TLRPC$TL_phone_editGroupCallParticipant();
            tLRPC$TL_phone_editGroupCallParticipant.call = this.groupCall.getInputGroupCall();
            if (tLObject instanceof TLRPC$User) {
                tLRPC$TL_phone_editGroupCallParticipant.participant = MessagesController.getInputPeer((TLRPC$User) tLObject);
            } else if (tLObject instanceof TLRPC$Chat) {
                tLRPC$TL_phone_editGroupCallParticipant.participant = MessagesController.getInputPeer((TLRPC$Chat) tLObject);
            }
            tLRPC$TL_phone_editGroupCallParticipant.muted = z;
            if (i >= 0) {
                tLRPC$TL_phone_editGroupCallParticipant.volume = i;
                tLRPC$TL_phone_editGroupCallParticipant.flags |= 2;
            }
            if (bool != null) {
                tLRPC$TL_phone_editGroupCallParticipant.raise_hand = bool.booleanValue();
                tLRPC$TL_phone_editGroupCallParticipant.flags |= 4;
            }
            int i2 = this.currentAccount;
            AccountInstance.getInstance(i2).getConnectionsManager().sendRequest(tLRPC$TL_phone_editGroupCallParticipant, new RequestDelegate(i2) {
                public final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPBaseService.lambda$editCallMember$1(this.f$0, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    static /* synthetic */ void lambda$editCallMember$1(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AccountInstance.getInstance(i).getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
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
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setTitle(LocaleController.getString("VoipOutputDevices", NUM), true);
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
        builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                VoIPBaseService.this.lambda$toggleSpeakerphoneOrShowRouteSheet$2$VoIPBaseService(dialogInterface, i);
            }
        });
        BottomSheet create = builder.create();
        if (z) {
            if (Build.VERSION.SDK_INT >= 26) {
                create.getWindow().setType(2038);
            } else {
                create.getWindow().setType(2003);
            }
        }
        builder.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$toggleSpeakerphoneOrShowRouteSheet$2 */
    public /* synthetic */ void lambda$toggleSpeakerphoneOrShowRouteSheet$2$VoIPBaseService(DialogInterface dialogInterface, int i) {
        if (getSharedInstance() != null) {
            setAudioOutput(i);
        }
    }

    /* access modifiers changed from: protected */
    public void setAudioOutput(int i) {
        CallConnection callConnection;
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        boolean z = USE_CONNECTION_SERVICE;
        if (!z || (callConnection = this.systemCallConnection) == null) {
            if (this.audioConfigured && !z) {
                if (i == 0) {
                    if (this.bluetoothScoActive) {
                        audioManager.stopBluetoothSco();
                        this.bluetoothScoActive = false;
                    }
                    audioManager.setBluetoothScoOn(false);
                    audioManager.setSpeakerphoneOn(true);
                } else if (i == 1) {
                    if (this.bluetoothScoActive) {
                        audioManager.stopBluetoothSco();
                        this.bluetoothScoActive = false;
                    }
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setBluetoothScoOn(false);
                } else if (i == 2) {
                    if (!this.bluetoothScoActive) {
                        this.needSwitchToBluetoothAfterScoActivates = true;
                        try {
                            audioManager.startBluetoothSco();
                        } catch (Throwable unused) {
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
        NativeInstance nativeInstance = this.tgVoip;
        return nativeInstance != null ? nativeInstance.getDebugInfo() : "";
    }

    public long getCallDuration() {
        if (this.callStartTime == 0) {
            return 0;
        }
        return SystemClock.elapsedRealtime() - this.callStartTime;
    }

    public static VoIPBaseService getSharedInstance() {
        return sharedInstance;
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

    /* access modifiers changed from: protected */
    public void showNotification(String str, Bitmap bitmap) {
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
        Notification notification = contentIntent.getNotification();
        this.ongoingCallNotification = notification;
        startForeground(201, notification);
    }

    /* access modifiers changed from: protected */
    public void startRingtoneAndVibration(int i) {
        int i2;
        String str;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (audioManager.getRingerMode() != 0) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.ringtonePlayer = mediaPlayer;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public final void onPrepared(MediaPlayer mediaPlayer) {
                    VoIPBaseService.this.lambda$startRingtoneAndVibration$3$VoIPBaseService(mediaPlayer);
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
    /* renamed from: lambda$startRingtoneAndVibration$3 */
    public /* synthetic */ void lambda$startRingtoneAndVibration$3$VoIPBaseService(MediaPlayer mediaPlayer) {
        this.ringtonePlayer.start();
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
        unregisterReceiver(this.receiver);
        Runnable runnable = this.timeoutRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.timeoutRunnable = null;
        }
        super.onDestroy();
        sharedInstance = null;
        AndroidUtilities.runOnUIThread($$Lambda$VoIPBaseService$pCP0tqa49bvI41OBc2M8fGTZm2E.INSTANCE);
        if (this.tgVoip != null) {
            StatsController.getInstance(this.currentAccount).incrementTotalCallsTime(getStatsNetworkType(), ((int) (getCallDuration() / 1000)) % 5);
            onTgVoipPreStop();
            if (this.tgVoip.isGroup()) {
                NativeInstance nativeInstance = this.tgVoip;
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
                Instance.FinalState stop = this.tgVoip.stop();
                updateTrafficStats(stop.trafficStats);
                onTgVoipStop(stop);
            }
            this.prevTrafficStats = null;
            this.callStartTime = 0;
            this.tgVoip = null;
            Instance.destroyInstance();
        }
        long j = this.videoCapturer;
        if (j != 0) {
            NativeInstance.destroyVideoCapturer(j);
            this.videoCapturer = 0;
        }
        this.cpuWakelock.release();
        if (!this.playingSound) {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (!USE_CONNECTION_SERVICE) {
                if (this.isBtHeadsetConnected) {
                    audioManager.stopBluetoothSco();
                    audioManager.setBluetoothScoOn(false);
                    audioManager.setSpeakerphoneOn(false);
                    this.bluetoothScoActive = false;
                }
                if (this.onDestroyRunnable == null) {
                    DispatchQueue dispatchQueue2 = Utilities.globalQueue;
                    $$Lambda$VoIPBaseService$80p9uXvPV7KwbilS7_Q7NhEQDI r4 = new Runnable(audioManager) {
                        public final /* synthetic */ AudioManager f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void run() {
                            VoIPBaseService.lambda$onDestroy$5(this.f$0);
                        }
                    };
                    setModeRunnable = r4;
                    dispatchQueue2.postRunnable(r4);
                }
                audioManager.abandonAudioFocus(this);
            }
            audioManager.unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (this.hasAudioFocus) {
                audioManager.abandonAudioFocus(this);
            }
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    VoIPBaseService.this.lambda$onDestroy$6$VoIPBaseService();
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
    }

    static /* synthetic */ void lambda$onDestroy$5(AudioManager audioManager) {
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
    /* renamed from: lambda$onDestroy$6 */
    public /* synthetic */ void lambda$onDestroy$6$VoIPBaseService() {
        this.soundPool.release();
    }

    /* access modifiers changed from: protected */
    public void initializeAccountRelatedThings() {
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
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    VoIPBaseService.this.lambda$onCreate$7$VoIPBaseService();
                }
            });
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
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$7 */
    public /* synthetic */ void lambda$onCreate$7$VoIPBaseService() {
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
    }

    /* access modifiers changed from: protected */
    public void dispatchStateChanged(int i) {
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

    /* access modifiers changed from: protected */
    public void updateTrafficStats(Instance.TrafficStats trafficStats) {
        if (trafficStats == null) {
            trafficStats = this.tgVoip.getTrafficStats();
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

    /* access modifiers changed from: protected */
    @SuppressLint({"InvalidWakeLockTag"})
    public void configureDeviceForCall() {
        this.needPlayEndSound = true;
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (!USE_CONNECTION_SERVICE) {
            Utilities.globalQueue.postRunnable(new Runnable(audioManager) {
                public final /* synthetic */ AudioManager f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    VoIPBaseService.lambda$configureDeviceForCall$8(this.f$0);
                }
            });
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
                        } catch (Throwable unused) {
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
        }
        updateOutputGainControlState();
        this.audioConfigured = true;
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

    static /* synthetic */ void lambda$configureDeviceForCall$8(AudioManager audioManager) {
        try {
            audioManager.setMode(3);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
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
        if (!this.unmutedByHold && this.currentVideoState != 2 && this.videoState != 2 && sensorEvent.sensor.getType() == 8) {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (!this.isHeadsetPlugged && !audioManager.isSpeakerphoneOn()) {
                if (!isBluetoothHeadsetConnected() || !audioManager.isBluetoothScoOn()) {
                    boolean z = false;
                    if (sensorEvent.values[0] < Math.min(sensorEvent.sensor.getMaximumRange(), 3.0f)) {
                        z = true;
                    }
                    checkIsNear(z);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkIsNear() {
        if (this.currentVideoState == 2 || this.videoState == 2) {
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

    /* access modifiers changed from: protected */
    public void updateBluetoothHeadsetState(boolean z) {
        if (z != this.isBtHeadsetConnected) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("updateBluetoothHeadsetState: " + z);
            }
            this.isBtHeadsetConnected = z;
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (!z || isRinging() || this.currentState == 0) {
                this.bluetoothScoActive = false;
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
                        VoIPBaseService.lambda$updateBluetoothHeadsetState$9(this.f$0);
                    }
                }, 500);
            }
            Iterator<StateListener> it = this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
        }
    }

    static /* synthetic */ void lambda$updateBluetoothHeadsetState$9(AudioManager audioManager) {
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

    /* access modifiers changed from: protected */
    public void updateNetworkType() {
        NativeInstance nativeInstance = this.tgVoip;
        if (nativeInstance == null) {
            this.lastNetInfo = getActiveNetworkInfo();
        } else if (!nativeInstance.isGroup()) {
            this.tgVoip.setNetworkType(getNetworkType());
        }
    }

    /* access modifiers changed from: protected */
    public int getNetworkType() {
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

    /* access modifiers changed from: protected */
    public NetworkInfo getActiveNetworkInfo() {
        return ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
    }

    /* access modifiers changed from: protected */
    public void callFailed() {
        NativeInstance nativeInstance = this.tgVoip;
        callFailed(nativeInstance != null ? nativeInstance.getLastError() : "ERROR_UNKNOWN");
    }

    /* access modifiers changed from: protected */
    public Bitmap getRoundAvatarBitmap(TLObject tLObject) {
        AvatarDrawable avatarDrawable;
        Bitmap decodeFile;
        boolean z = tLObject instanceof TLRPC$User;
        Bitmap bitmap = null;
        if (z) {
            TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
            TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$User.photo;
            if (!(tLRPC$UserProfilePhoto == null || tLRPC$UserProfilePhoto.photo_small == null)) {
                BitmapDrawable imageFromMemory = ImageLoader.getInstance().getImageFromMemory(tLRPC$User.photo.photo_small, (String) null, "50_50");
                if (imageFromMemory != null) {
                    decodeFile = imageFromMemory.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                } else {
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(tLRPC$User.photo.photo_small, true).toString(), options);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                bitmap = decodeFile;
            }
        } else {
            TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
            TLRPC$ChatPhoto tLRPC$ChatPhoto = tLRPC$Chat.photo;
            if (!(tLRPC$ChatPhoto == null || tLRPC$ChatPhoto.photo_small == null)) {
                BitmapDrawable imageFromMemory2 = ImageLoader.getInstance().getImageFromMemory(tLRPC$Chat.photo.photo_small, (String) null, "50_50");
                if (imageFromMemory2 != null) {
                    bitmap = imageFromMemory2.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                } else {
                    try {
                        BitmapFactory.Options options2 = new BitmapFactory.Options();
                        options2.inMutable = true;
                        bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(tLRPC$Chat.photo.photo_small, true).toString(), options2);
                    } catch (Throwable th2) {
                        FileLog.e(th2);
                    }
                }
            }
        }
        if (bitmap == null) {
            Theme.createDialogsResources(this);
            if (z) {
                avatarDrawable = new AvatarDrawable((TLRPC$User) tLObject);
            } else {
                avatarDrawable = new AvatarDrawable((TLRPC$Chat) tLObject);
            }
            bitmap = Bitmap.createBitmap(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), Bitmap.Config.ARGB_8888);
            avatarDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            avatarDrawable.draw(new Canvas(bitmap));
        }
        Canvas canvas = new Canvas(bitmap);
        Path path = new Path();
        path.addCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2), (float) (bitmap.getWidth() / 2), Path.Direction.CW);
        path.toggleInverseFillType();
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPath(path, paint);
        return bitmap;
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
    /* access modifiers changed from: protected */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r7v4, types: [int, boolean] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x013b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showIncomingNotification(java.lang.String r19, java.lang.CharSequence r20, org.telegram.tgnet.TLObject r21, boolean r22, int r23) {
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
            r6 = 2131628042(0x7f0e100a, float:1.8883365E38)
            java.lang.String r7 = "VoipInVideoCallBranding"
            r8 = 2131628040(0x7f0e1008, float:1.8883361E38)
            java.lang.String r9 = "VoipInCallBranding"
            if (r22 == 0) goto L_0x002b
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x002f
        L_0x002b:
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r9, r8)
        L_0x002f:
            android.app.Notification$Builder r5 = r5.setContentTitle(r10)
            android.app.Notification$Builder r5 = r5.setContentText(r0)
            r10 = 2131165856(0x7var_a0, float:1.794594E38)
            android.app.Notification$Builder r5 = r5.setSmallIcon(r10)
            android.app.Notification$Builder r5 = r5.setSubText(r2)
            r10 = 0
            android.app.PendingIntent r11 = android.app.PendingIntent.getActivity(r1, r10, r4, r10)
            android.app.Notification$Builder r5 = r5.setContentIntent(r11)
            java.lang.String r11 = "content://org.telegram.messenger.beta.call_sound_provider/start_ringing"
            android.net.Uri r11 = android.net.Uri.parse(r11)
            int r12 = android.os.Build.VERSION.SDK_INT
            r13 = 26
            if (r12 < r13) goto L_0x0150
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
            if (r10 == 0) goto L_0x0087
            java.lang.String r10 = r10.getId()
            r8.deleteNotificationChannel(r10)
        L_0x0087:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r15 = "incoming_calls3"
            r10.append(r15)
            r10.append(r14)
            java.lang.String r10 = r10.toString()
            android.app.NotificationChannel r10 = r8.getNotificationChannel(r10)
            r2 = 4
            r16 = r9
            if (r10 == 0) goto L_0x00e8
            int r9 = r10.getImportance()
            if (r9 < r2) goto L_0x00c0
            android.net.Uri r9 = r10.getSound()
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x00c0
            long[] r9 = r10.getVibrationPattern()
            if (r9 != 0) goto L_0x00c0
            boolean r9 = r10.shouldVibrate()
            if (r9 == 0) goto L_0x00be
            goto L_0x00c0
        L_0x00be:
            r6 = 0
            goto L_0x00e9
        L_0x00c0:
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r9 == 0) goto L_0x00c9
            java.lang.String r9 = "User messed up the notification channel; deleting it and creating a proper one"
            org.telegram.messenger.FileLog.d(r9)
        L_0x00c9:
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
        L_0x00e8:
            r6 = 1
        L_0x00e9:
            if (r6 == 0) goto L_0x013b
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
            r13 = 2131625800(0x7f0e0748, float:1.8878818E38)
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
            r8.createNotificationChannel(r9)     // Catch:{ Exception -> 0x0132 }
            goto L_0x013d
        L_0x0132:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
            r18.stopSelf()
            return
        L_0x013b:
            r17 = r7
        L_0x013d:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            r5.setChannelId(r2)
            goto L_0x015c
        L_0x0150:
            r17 = r7
            r16 = r9
            r2 = 21
            if (r12 < r2) goto L_0x015c
            r2 = 2
            r5.setSound(r11, r2)
        L_0x015c:
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
            r7 = 2131627950(0x7f0e0fae, float:1.8883179E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r6, r7)
            r10 = 24
            if (r12 < r10) goto L_0x01a8
            android.text.SpannableString r11 = new android.text.SpannableString
            r11.<init>(r9)
            android.text.style.ForegroundColorSpan r9 = new android.text.style.ForegroundColorSpan
            r13 = -769226(0xffffffffffvar_, float:NaN)
            r9.<init>(r13)
            int r13 = r11.length()
            r14 = 0
            r11.setSpan(r9, r14, r13, r14)
            r9 = r11
            goto L_0x01a9
        L_0x01a8:
            r14 = 0
        L_0x01a9:
            r11 = 268435456(0x10000000, float:2.5243549E-29)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r1, r14, r2, r11)
            r13 = 2131165489(0x7var_, float:1.7945197E38)
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
            r13 = 2131627940(0x7f0e0fa4, float:1.8883159E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r8, r13)
            if (r12 < r10) goto L_0x01fd
            android.text.SpannableString r10 = new android.text.SpannableString
            r10.<init>(r14)
            android.text.style.ForegroundColorSpan r14 = new android.text.style.ForegroundColorSpan
            r15 = -16733696(0xfffffffffvar_aa00, float:-1.7102387E38)
            r14.<init>(r15)
            int r15 = r10.length()
            r7 = 0
            r10.setSpan(r14, r7, r15, r7)
            r14 = r10
            goto L_0x01fe
        L_0x01fd:
            r7 = 0
        L_0x01fe:
            android.app.PendingIntent r9 = android.app.PendingIntent.getBroadcast(r1, r7, r9, r11)
            r10 = 2131165488(0x7var_, float:1.7945195E38)
            r5.addAction(r10, r14, r9)
            r10 = 2
            r5.setPriority(r10)
            r10 = 17
            if (r12 < r10) goto L_0x0213
            r5.setShowWhen(r7)
        L_0x0213:
            r10 = 21
            if (r12 < r10) goto L_0x0254
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
            if (r4 == 0) goto L_0x0254
            r4 = r3
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            java.lang.String r7 = r4.phone
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0254
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r10 = "tel:"
            r7.append(r10)
            java.lang.String r4 = r4.phone
            r7.append(r4)
            java.lang.String r4 = r7.toString()
            r5.addPerson(r4)
        L_0x0254:
            android.app.Notification r4 = r5.getNotification()
            r7 = 21
            if (r12 < r7) goto L_0x0346
            android.widget.RemoteViews r7 = new android.widget.RemoteViews
            java.lang.String r10 = r18.getPackageName()
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x026a
            r11 = 2131427329(0x7f0b0001, float:1.8476271E38)
            goto L_0x026c
        L_0x026a:
            r11 = 2131427328(0x7f0b0000, float:1.847627E38)
        L_0x026c:
            r7.<init>(r10, r11)
            r10 = 2131230860(0x7var_c, float:1.8077785E38)
            r7.setTextViewText(r10, r0)
            boolean r0 = android.text.TextUtils.isEmpty(r20)
            r10 = 8
            r11 = 2131230931(0x7var_d3, float:1.8077929E38)
            r12 = 2131230913(0x7var_c1, float:1.8077892E38)
            if (r0 == 0) goto L_0x02df
            r7.setViewVisibility(r12, r10)
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r10 = 1
            if (r0 <= r10) goto L_0x02ca
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
            if (r22 == 0) goto L_0x02b0
            r12 = 2131628043(0x7f0e100b, float:1.8883368E38)
            java.lang.Object[] r10 = new java.lang.Object[r10]
            java.lang.String r14 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r14, r0)
            r14 = 0
            r10[r14] = r0
            java.lang.String r0 = "VoipInVideoCallBrandingWithName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r12, r10)
            goto L_0x02c6
        L_0x02b0:
            r14 = 0
            r12 = 2131628041(0x7f0e1009, float:1.8883363E38)
            java.lang.Object[] r10 = new java.lang.Object[r10]
            java.lang.String r15 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r15, r0)
            r10[r14] = r0
            java.lang.String r0 = "VoipInCallBrandingWithName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r12, r10)
        L_0x02c6:
            r7.setTextViewText(r11, r0)
            goto L_0x0312
        L_0x02ca:
            if (r22 == 0) goto L_0x02d2
            r10 = r17
            r0 = 2131628042(0x7f0e100a, float:1.8883365E38)
            goto L_0x02d7
        L_0x02d2:
            r10 = r16
            r0 = 2131628040(0x7f0e1008, float:1.8883361E38)
        L_0x02d7:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
            r7.setTextViewText(r11, r0)
            goto L_0x0312
        L_0x02df:
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r14 = 1
            if (r0 <= r14) goto L_0x030a
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
            r10 = 2131627941(0x7f0e0fa5, float:1.888316E38)
            java.lang.Object[] r14 = new java.lang.Object[r14]
            java.lang.String r15 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r15, r0)
            r15 = 0
            r14[r15] = r0
            java.lang.String r0 = "VoipAnsweringAsAccount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r10, r14)
            r7.setTextViewText(r12, r0)
            goto L_0x030d
        L_0x030a:
            r7.setViewVisibility(r12, r10)
        L_0x030d:
            r0 = r20
            r7.setTextViewText(r11, r0)
        L_0x0312:
            android.graphics.Bitmap r0 = r1.getRoundAvatarBitmap(r3)
            r3 = 2131230769(0x7var_, float:1.80776E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r13)
            r7.setTextViewText(r3, r8)
            r3 = 2131230803(0x7var_, float:1.807767E38)
            r8 = 2131627950(0x7f0e0fae, float:1.8883179E38)
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
        L_0x0346:
            r0 = 202(0xca, float:2.83E-43)
            r1.startForeground(r0, r4)
            r18.startRingtoneAndVibration()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPBaseService.showIncomingNotification(java.lang.String, java.lang.CharSequence, org.telegram.tgnet.TLObject, boolean, int):void");
    }

    /* access modifiers changed from: protected */
    public void callFailed(String str) {
        CallConnection callConnection;
        try {
            throw new Exception("Call " + getCallID() + " failed with error: " + str);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.lastError = str;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    VoIPBaseService.this.lambda$callFailed$10$VoIPBaseService();
                }
            });
            if (TextUtils.equals(str, "ERROR_LOCALIZED") && this.soundPool != null) {
                this.playingSound = true;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        VoIPBaseService.this.lambda$callFailed$11$VoIPBaseService();
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$callFailed$10 */
    public /* synthetic */ void lambda$callFailed$10$VoIPBaseService() {
        dispatchStateChanged(4);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$callFailed$11 */
    public /* synthetic */ void lambda$callFailed$11$VoIPBaseService() {
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
                    VoIPBaseService.this.lambda$onConnectionStateChanged$12$VoIPBaseService();
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
                        VoIPBaseService voIPBaseService = VoIPBaseService.this;
                        if (voIPBaseService.tgVoip != null) {
                            StatsController.getInstance(voIPBaseService.currentAccount).incrementTotalCallsTime(VoIPBaseService.this.getStatsNetworkType(), 5);
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
                    VoIPBaseService.this.lambda$onConnectionStateChanged$13$VoIPBaseService();
                }
            });
        }
        dispatchStateChanged(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onConnectionStateChanged$12 */
    public /* synthetic */ void lambda$onConnectionStateChanged$12$VoIPBaseService() {
        int i = this.spPlayID;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayID = 0;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onConnectionStateChanged$13 */
    public /* synthetic */ void lambda$onConnectionStateChanged$13$VoIPBaseService() {
        int i = this.spPlayID;
        if (i != 0) {
            this.soundPool.stop(i);
        }
        this.spPlayID = this.soundPool.play(this.groupCall != null ? this.spVoiceChatConnecting : this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    public void onSignalBarCountChanged(int i) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPBaseService.this.lambda$onSignalBarCountChanged$14$VoIPBaseService(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onSignalBarCountChanged$14 */
    public /* synthetic */ void lambda$onSignalBarCountChanged$14$VoIPBaseService(int i) {
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

    public void onMediaStateUpdated(int i, int i2) {
        AndroidUtilities.runOnUIThread(new Runnable(i, i2) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VoIPBaseService.this.lambda$onMediaStateUpdated$15$VoIPBaseService(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onMediaStateUpdated$15 */
    public /* synthetic */ void lambda$onMediaStateUpdated$15$VoIPBaseService(int i, int i2) {
        this.currentAudioState = i;
        this.currentVideoState = i2;
        checkIsNear();
        for (int i3 = 0; i3 < this.stateListeners.size(); i3++) {
            this.stateListeners.get(i3).onMediaStateUpdated(i, i2);
        }
    }

    /* access modifiers changed from: protected */
    public void callEnded() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Call " + getCallID() + " ended");
        }
        if (this.groupCall != null && (!this.playedConnectedSound || this.onDestroyRunnable != null)) {
            this.needPlayEndSound = false;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                VoIPBaseService.this.lambda$callEnded$16$VoIPBaseService();
            }
        });
        int i = 700;
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VoIPBaseService.this.lambda$callEnded$17$VoIPBaseService();
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
                        VoIPBaseService.this.lambda$callEnded$18$VoIPBaseService();
                    }
                });
            } else {
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        VoIPBaseService.this.lambda$callEnded$19$VoIPBaseService();
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
    /* renamed from: lambda$callEnded$16 */
    public /* synthetic */ void lambda$callEnded$16$VoIPBaseService() {
        dispatchStateChanged(11);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$callEnded$17 */
    public /* synthetic */ void lambda$callEnded$17$VoIPBaseService() {
        int i = this.spPlayID;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spPlayID = 0;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$callEnded$18 */
    public /* synthetic */ void lambda$callEnded$18$VoIPBaseService() {
        this.soundPool.play(this.spEndId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$callEnded$19 */
    public /* synthetic */ void lambda$callEnded$19$VoIPBaseService() {
        this.soundPool.play(this.spVoiceChatEndId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    /* access modifiers changed from: protected */
    public void endConnectionServiceCall(long j) {
        if (USE_CONNECTION_SERVICE) {
            $$Lambda$VoIPBaseService$iGhJEDfsphWDLSs0A9aBx5peopM r0 = new Runnable() {
                public final void run() {
                    VoIPBaseService.this.lambda$endConnectionServiceCall$20$VoIPBaseService();
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
    /* renamed from: lambda$endConnectionServiceCall$20 */
    public /* synthetic */ void lambda$endConnectionServiceCall$20$VoIPBaseService() {
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
        if (Build.VERSION.SDK_INT < 23 || (checkSelfPermission("android.permission.RECORD_AUDIO") == 0 && (!this.privateCall.video || checkSelfPermission("android.permission.CAMERA") == 0))) {
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
                PendingIntent.getActivity(this, 0, new Intent(this, VoIPPermissionActivity.class).addFlags(NUM), 0).send();
            } catch (Exception e2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting permission activity", e2);
                }
            }
        }
    }

    public void updateOutputGainControlState() {
        if (this.tgVoip != null) {
            boolean z = false;
            if (!USE_CONNECTION_SERVICE) {
                AudioManager audioManager = (AudioManager) getSystemService("audio");
                this.tgVoip.setAudioOutputGainControlEnabled(hasEarpiece() && !audioManager.isSpeakerphoneOn() && !audioManager.isBluetoothScoOn() && !this.isHeadsetPlugged);
                NativeInstance nativeInstance = this.tgVoip;
                if (!this.isHeadsetPlugged && (!hasEarpiece() || audioManager.isSpeakerphoneOn() || audioManager.isBluetoothScoOn() || this.isHeadsetPlugged)) {
                    z = true;
                }
                nativeInstance.setEchoCancellationStrength(z ? 1 : 0);
                return;
            }
            if (this.systemCallConnection.getCallAudioState().getRoute() == 1) {
                z = true;
            }
            this.tgVoip.setAudioOutputGainControlEnabled(z);
            this.tgVoip.setEchoCancellationStrength(z ^ true ? 1 : 0);
        }
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
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isFinished() {
        int i = this.currentState;
        return i == 11 || i == 4;
    }

    public int getCurrentAudioState() {
        return this.currentAudioState;
    }

    public int getCurrentVideoState() {
        return this.currentVideoState;
    }

    /* access modifiers changed from: protected */
    @TargetApi(26)
    public PhoneAccountHandle addAccountToTelecomManager() {
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
            Iterator<StateListener> it = VoIPBaseService.this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
        }

        public void onDisconnect() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onDisconnect");
            }
            setDisconnected(new DisconnectCause(2));
            destroy();
            VoIPBaseService voIPBaseService = VoIPBaseService.this;
            voIPBaseService.systemCallConnection = null;
            voIPBaseService.hangUp();
        }

        public void onAnswer() {
            VoIPBaseService.this.acceptIncomingCallFromNotification();
        }

        public void onReject() {
            VoIPBaseService voIPBaseService = VoIPBaseService.this;
            voIPBaseService.needPlayEndSound = false;
            voIPBaseService.declineIncomingCall(1, (Runnable) null);
        }

        public void onShowIncomingCallUi() {
            VoIPBaseService.this.startRinging();
        }

        public void onStateChanged(int i) {
            super.onStateChanged(i);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onStateChanged " + Connection.stateToString(i));
            }
            if (i == 4) {
                ContactsController.getInstance(VoIPBaseService.this.currentAccount).deleteConnectionServiceContact();
                VoIPBaseService.this.didDeleteConnectionServiceContact = true;
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
            VoIPBaseService.this.stopRinging();
        }
    }
}
