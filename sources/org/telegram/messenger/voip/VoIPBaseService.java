package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
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
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.ViewGroup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.VoIPPermissionActivity;

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
    public static final int STATE_ENDED = 11;
    public static final int STATE_ESTABLISHED = 3;
    public static final int STATE_FAILED = 4;
    public static final int STATE_RECONNECTING = 5;
    public static final int STATE_WAIT_INIT = 1;
    public static final int STATE_WAIT_INIT_ACK = 2;
    protected static final boolean USE_CONNECTION_SERVICE = isDeviceCompatibleWithConnectionServiceAPI();
    protected static VoIPBaseService sharedInstance;
    protected Runnable afterSoundRunnable = new Runnable() {
        public void run() {
            VoIPBaseService.this.soundPool.release();
            if (!VoIPBaseService.USE_CONNECTION_SERVICE) {
                if (VoIPBaseService.this.isBtHeadsetConnected) {
                    ((AudioManager) ApplicationLoader.applicationContext.getSystemService("audio")).stopBluetoothSco();
                }
                ((AudioManager) ApplicationLoader.applicationContext.getSystemService("audio")).setSpeakerphoneOn(false);
            }
        }
    };
    protected boolean audioConfigured;
    protected int audioRouteToSet = 2;
    protected boolean bluetoothScoActive = false;
    protected BluetoothAdapter btAdapter;
    protected int callDiscardReason;
    protected Runnable connectingSoundRunnable;
    protected VoIPController controller;
    protected boolean controllerStarted;
    protected PowerManager.WakeLock cpuWakelock;
    protected int currentAccount = -1;
    protected int currentState = 0;
    protected boolean didDeleteConnectionServiceContact = false;
    protected boolean haveAudioFocus;
    protected boolean isBtHeadsetConnected;
    protected boolean isHeadsetPlugged;
    protected boolean isOutgoing;
    protected boolean isProximityNear;
    protected int lastError;
    protected long lastKnownDuration = 0;
    protected NetworkInfo lastNetInfo;
    private Boolean mHasEarpiece = null;
    protected boolean micMute;
    protected boolean needPlayEndSound;
    protected boolean needSwitchToBluetoothAfterScoActivates = false;
    protected Notification ongoingCallNotification;
    protected boolean playingSound;
    protected VoIPController.Stats prevStats = new VoIPController.Stats();
    protected PowerManager.WakeLock proximityWakelock;
    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            PowerManager.WakeLock wakeLock;
            boolean z = true;
            if ("android.intent.action.HEADSET_PLUG".equals(intent.getAction())) {
                VoIPBaseService voIPBaseService = VoIPBaseService.this;
                if (intent.getIntExtra("state", 0) != 1) {
                    z = false;
                }
                voIPBaseService.isHeadsetPlugged = z;
                VoIPBaseService voIPBaseService2 = VoIPBaseService.this;
                if (voIPBaseService2.isHeadsetPlugged && (wakeLock = voIPBaseService2.proximityWakelock) != null && wakeLock.isHeld()) {
                    VoIPBaseService.this.proximityWakelock.release();
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
                VoIPBaseService.this.bluetoothScoActive = intExtra == 1;
                VoIPBaseService voIPBaseService6 = VoIPBaseService.this;
                if (voIPBaseService6.bluetoothScoActive && voIPBaseService6.needSwitchToBluetoothAfterScoActivates) {
                    voIPBaseService6.needSwitchToBluetoothAfterScoActivates = false;
                    AudioManager audioManager = (AudioManager) voIPBaseService6.getSystemService("audio");
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setBluetoothScoOn(true);
                }
                Iterator<StateListener> it = VoIPBaseService.this.stateListeners.iterator();
                while (it.hasNext()) {
                    it.next().onAudioSettingsChanged();
                }
            } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(intent.getStringExtra("state"))) {
                    VoIPBaseService.this.hangUp();
                }
            }
        }
    };
    protected MediaPlayer ringtonePlayer;
    protected int signalBarCount;
    protected SoundPool soundPool;
    protected int spBusyId;
    protected int spConnectingId;
    protected int spEndId;
    protected int spFailedID;
    protected int spPlayID;
    protected int spRingbackID;
    protected boolean speakerphoneStateToSet;
    protected ArrayList<StateListener> stateListeners = new ArrayList<>();
    protected VoIPController.Stats stats = new VoIPController.Stats();
    protected CallConnection systemCallConnection;
    protected Runnable timeoutRunnable;
    protected Vibrator vibrator;
    private boolean wasEstablished;

    public interface StateListener {
        void onAudioSettingsChanged();

        void onSignalBarsCountChanged(int i);

        void onStateChanged(int i);
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
    public void onControllerPreRelease() {
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

    public void registerStateListener(StateListener stateListener) {
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

    public void unregisterStateListener(StateListener stateListener) {
        this.stateListeners.remove(stateListener);
    }

    public void setMicMute(boolean z) {
        this.micMute = z;
        VoIPController voIPController = this.controller;
        if (voIPController != null) {
            voIPController.setMicMute(z);
        }
    }

    public boolean isMicMute() {
        return this.micMute;
    }

    public void toggleSpeakerphoneOrShowRouteSheet(Activity activity) {
        CallConnection callConnection;
        if (!isBluetoothHeadsetConnected() || !hasEarpiece()) {
            if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
                int i = 5;
                if (hasEarpiece()) {
                    CallConnection callConnection2 = this.systemCallConnection;
                    if (callConnection2.getCallAudioState().getRoute() != 8) {
                        i = 8;
                    }
                    callConnection2.setAudioRoute(i);
                } else {
                    CallConnection callConnection3 = this.systemCallConnection;
                    if (callConnection3.getCallAudioState().getRoute() != 2) {
                        i = 2;
                    }
                    callConnection3.setAudioRoute(i);
                }
            } else if (!this.audioConfigured || USE_CONNECTION_SERVICE) {
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
        BottomSheet create = new BottomSheet.Builder(activity).setItems(new CharSequence[]{LocaleController.getString("VoipAudioRoutingBluetooth", NUM), LocaleController.getString("VoipAudioRoutingEarpiece", NUM), LocaleController.getString("VoipAudioRoutingSpeaker", NUM)}, new int[]{NUM, NUM, NUM}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                CallConnection callConnection;
                AudioManager audioManager = (AudioManager) VoIPBaseService.this.getSystemService("audio");
                if (VoIPBaseService.getSharedInstance() != null) {
                    if (!VoIPBaseService.USE_CONNECTION_SERVICE || (callConnection = VoIPBaseService.this.systemCallConnection) == null) {
                        VoIPBaseService voIPBaseService = VoIPBaseService.this;
                        if (voIPBaseService.audioConfigured && !VoIPBaseService.USE_CONNECTION_SERVICE) {
                            if (i != 0) {
                                if (i == 1) {
                                    if (voIPBaseService.bluetoothScoActive) {
                                        audioManager.stopBluetoothSco();
                                    }
                                    audioManager.setSpeakerphoneOn(false);
                                    audioManager.setBluetoothScoOn(false);
                                } else if (i == 2) {
                                    if (voIPBaseService.bluetoothScoActive) {
                                        audioManager.stopBluetoothSco();
                                    }
                                    audioManager.setBluetoothScoOn(false);
                                    audioManager.setSpeakerphoneOn(true);
                                }
                            } else if (!voIPBaseService.bluetoothScoActive) {
                                voIPBaseService.needSwitchToBluetoothAfterScoActivates = true;
                                try {
                                    audioManager.startBluetoothSco();
                                } catch (Throwable unused) {
                                }
                            } else {
                                audioManager.setBluetoothScoOn(true);
                                audioManager.setSpeakerphoneOn(false);
                            }
                            VoIPBaseService.this.updateOutputGainControlState();
                        } else if (i == 0) {
                            VoIPBaseService.this.audioRouteToSet = 2;
                        } else if (i == 1) {
                            VoIPBaseService.this.audioRouteToSet = 0;
                        } else if (i == 2) {
                            VoIPBaseService.this.audioRouteToSet = 1;
                        }
                    } else if (i == 0) {
                        callConnection.setAudioRoute(2);
                    } else if (i == 1) {
                        callConnection.setAudioRoute(5);
                    } else if (i == 2) {
                        callConnection.setAudioRoute(8);
                    }
                    Iterator<StateListener> it = VoIPBaseService.this.stateListeners.iterator();
                    while (it.hasNext()) {
                        it.next().onAudioSettingsChanged();
                    }
                }
            }
        }).create();
        create.setBackgroundColor(-13948117);
        create.show();
        ViewGroup sheetContainer = create.getSheetContainer();
        for (int i2 = 0; i2 < sheetContainer.getChildCount(); i2++) {
            ((BottomSheet.BottomSheetCell) sheetContainer.getChildAt(i2)).setTextColor(-1);
        }
    }

    public boolean isSpeakerphoneOn() {
        CallConnection callConnection;
        if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
            int route = this.systemCallConnection.getCallAudioState().getRoute();
            if (hasEarpiece()) {
                if (route == 8) {
                    return true;
                }
            } else if (route == 2) {
                return true;
            }
            return false;
        } else if (!this.audioConfigured || USE_CONNECTION_SERVICE) {
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
        return this.controller.getDebugString();
    }

    public long getCallDuration() {
        VoIPController voIPController;
        if (!this.controllerStarted || (voIPController = this.controller) == null) {
            return this.lastKnownDuration;
        }
        long callDuration = voIPController.getCallDuration();
        this.lastKnownDuration = callDuration;
        return callDuration;
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
    public void showNotification(String str, TLRPC.FileLocation fileLocation, Class<? extends Activity> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(NUM);
        Notification.Builder contentIntent = new Notification.Builder(this).setContentTitle(LocaleController.getString("VoipOutgoingCall", NUM)).setContentText(str).setSmallIcon(NUM).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        if (Build.VERSION.SDK_INT >= 16) {
            Intent intent2 = new Intent(this, VoIPActionsReceiver.class);
            intent2.setAction(getPackageName() + ".END_CALL");
            contentIntent.addAction(NUM, LocaleController.getString("VoipEndCall", NUM), PendingIntent.getBroadcast(this, 0, intent2, NUM));
            contentIntent.setPriority(2);
        }
        if (Build.VERSION.SDK_INT >= 17) {
            contentIntent.setShowWhen(false);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            contentIntent.setColor(-13851168);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            contentIntent.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
        }
        if (fileLocation != null) {
            BitmapDrawable imageFromMemory = ImageLoader.getInstance().getImageFromMemory(fileLocation, (String) null, "50_50");
            if (imageFromMemory != null) {
                contentIntent.setLargeIcon(imageFromMemory.getBitmap());
            } else {
                try {
                    float dp = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = dp < 1.0f ? 1 : (int) dp;
                    Bitmap decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(fileLocation, true).toString(), options);
                    if (decodeFile != null) {
                        contentIntent.setLargeIcon(decodeFile);
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
        this.ongoingCallNotification = contentIntent.getNotification();
        startForeground(201, this.ongoingCallNotification);
    }

    /* access modifiers changed from: protected */
    public void startRingtoneAndVibration(int i) {
        int i2;
        String str;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (audioManager.getRingerMode() != 0) {
            if (!USE_CONNECTION_SERVICE) {
                audioManager.requestAudioFocus(this, 2, 1);
            }
            this.ringtonePlayer = new MediaPlayer();
            this.ringtonePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    VoIPBaseService.this.ringtonePlayer.start();
                }
            });
            this.ringtonePlayer.setLooping(true);
            this.ringtonePlayer.setAudioStreamType(2);
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
                MediaPlayer mediaPlayer = this.ringtonePlayer;
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    this.ringtonePlayer = null;
                }
            }
            if (notificationsSettings.getBoolean("custom_" + i, false)) {
                i2 = notificationsSettings.getInt("calls_vibrate_" + i, 0);
            } else {
                i2 = notificationsSettings.getInt("vibrate_calls", 0);
            }
            if ((i2 != 2 && i2 != 4 && (audioManager.getRingerMode() == 1 || audioManager.getRingerMode() == 2)) || (i2 == 4 && audioManager.getRingerMode() == 1)) {
                this.vibrator = (Vibrator) getSystemService("vibrator");
                long j = 700;
                if (i2 == 1) {
                    j = 350;
                } else if (i2 == 3) {
                    j = 1400;
                }
                this.vibrator.vibrate(new long[]{0, j, 500}, 0);
            }
        }
    }

    public void onDestroy() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STOPPING ===============");
        }
        stopForeground(true);
        stopRinging();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
        SensorManager sensorManager = (SensorManager) getSystemService("sensor");
        if (sensorManager.getDefaultSensor(8) != null) {
            sensorManager.unregisterListener(this);
        }
        PowerManager.WakeLock wakeLock = this.proximityWakelock;
        if (wakeLock != null && wakeLock.isHeld()) {
            this.proximityWakelock.release();
        }
        unregisterReceiver(this.receiver);
        Runnable runnable = this.timeoutRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.timeoutRunnable = null;
        }
        super.onDestroy();
        sharedInstance = null;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
            }
        });
        VoIPController voIPController = this.controller;
        if (voIPController != null && this.controllerStarted) {
            this.lastKnownDuration = voIPController.getCallDuration();
            updateStats();
            StatsController.getInstance(this.currentAccount).incrementTotalCallsTime(getStatsNetworkType(), ((int) (this.lastKnownDuration / 1000)) % 5);
            onControllerPreRelease();
            this.controller.release();
            this.controller = null;
        }
        this.cpuWakelock.release();
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (!USE_CONNECTION_SERVICE) {
            if (this.isBtHeadsetConnected && !this.playingSound) {
                audioManager.stopBluetoothSco();
                audioManager.setSpeakerphoneOn(false);
            }
            try {
                audioManager.setMode(0);
            } catch (SecurityException e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error setting audio more to normal", e);
                }
            }
            audioManager.abandonAudioFocus(this);
        }
        audioManager.unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
        if (this.haveAudioFocus) {
            audioManager.abandonAudioFocus(this);
        }
        if (!this.playingSound) {
            this.soundPool.release();
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
        VoIPHelper.lastCallTime = System.currentTimeMillis();
    }

    /* access modifiers changed from: protected */
    public VoIPController createController() {
        return new VoIPController();
    }

    /* access modifiers changed from: protected */
    public void initializeAccountRelatedThings() {
        updateServerConfig();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        this.controller = createController();
        this.controller.setConnectionStateListener(this);
    }

    public void onCreate() {
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STARTING ===============");
        }
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (Build.VERSION.SDK_INT < 17 || audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") == null) {
            VoIPController.setNativeBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
        } else {
            VoIPController.setNativeBufferSize(Integer.parseInt(audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")));
        }
        try {
            this.cpuWakelock = ((PowerManager) getSystemService("power")).newWakeLock(1, "telegram-voip");
            this.cpuWakelock.acquire();
            this.btAdapter = audioManager.isBluetoothScoAvailableOffCall() ? BluetoothAdapter.getDefaultAdapter() : null;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            if (!USE_CONNECTION_SERVICE) {
                intentFilter.addAction("android.intent.action.HEADSET_PLUG");
                if (this.btAdapter != null) {
                    intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                    intentFilter.addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
                }
                intentFilter.addAction("android.intent.action.PHONE_STATE");
            }
            registerReceiver(this.receiver, intentFilter);
            boolean z = false;
            this.soundPool = new SoundPool(1, 0, 0);
            this.spConnectingId = this.soundPool.load(this, NUM, 1);
            this.spRingbackID = this.soundPool.load(this, NUM, 1);
            this.spFailedID = this.soundPool.load(this, NUM, 1);
            this.spEndId = this.soundPool.load(this, NUM, 1);
            this.spBusyId = this.soundPool.load(this, NUM, 1);
            audioManager.registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (!USE_CONNECTION_SERVICE && this.btAdapter != null && this.btAdapter.isEnabled()) {
                if (this.btAdapter.getProfileConnectionState(1) == 2) {
                    z = true;
                }
                updateBluetoothHeadsetState(z);
                Iterator<StateListener> it = this.stateListeners.iterator();
                while (it.hasNext()) {
                    it.next().onAudioSettingsChanged();
                }
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error initializing voip controller", e);
            }
            callFailed();
        }
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
    public void updateStats() {
        this.controller.getStats(this.stats);
        VoIPController.Stats stats2 = this.stats;
        long j = stats2.bytesSentWifi;
        VoIPController.Stats stats3 = this.prevStats;
        long j2 = j - stats3.bytesSentWifi;
        long j3 = stats2.bytesRecvdWifi - stats3.bytesRecvdWifi;
        long j4 = stats2.bytesSentMobile - stats3.bytesSentMobile;
        long j5 = stats2.bytesRecvdMobile - stats3.bytesRecvdMobile;
        this.stats = stats3;
        this.prevStats = stats2;
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
    public void configureDeviceForCall() {
        this.needPlayEndSound = true;
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (!USE_CONNECTION_SERVICE) {
            audioManager.setMode(3);
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

    @SuppressLint({"NewApi"})
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8) {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (!this.isHeadsetPlugged && !audioManager.isSpeakerphoneOn()) {
                if (!isBluetoothHeadsetConnected() || !audioManager.isBluetoothScoOn()) {
                    boolean z = false;
                    if (sensorEvent.values[0] < Math.min(sensorEvent.sensor.getMaximumRange(), 3.0f)) {
                        z = true;
                    }
                    if (z != this.isProximityNear) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("proximity " + z);
                        }
                        this.isProximityNear = z;
                        try {
                            if (this.isProximityNear) {
                                this.proximityWakelock.acquire();
                            } else {
                                this.proximityWakelock.release(1);
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                }
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
            this.haveAudioFocus = true;
        } else {
            this.haveAudioFocus = false;
        }
    }

    /* access modifiers changed from: protected */
    public void updateBluetoothHeadsetState(boolean z) {
        if (z != this.isBtHeadsetConnected) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("updateBluetoothHeadsetState: " + z);
            }
            this.isBtHeadsetConnected = z;
            final AudioManager audioManager = (AudioManager) getSystemService("audio");
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
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        try {
                            audioManager.startBluetoothSco();
                        } catch (Throwable unused) {
                        }
                    }
                }, 500);
            }
            Iterator<StateListener> it = this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
        }
    }

    public int getLastError() {
        return this.lastError;
    }

    public int getCallState() {
        return this.currentState;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0039  */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateNetworkType() {
        /*
            r3 = this;
            java.lang.String r0 = "connectivity"
            java.lang.Object r0 = r3.getSystemService(r0)
            android.net.ConnectivityManager r0 = (android.net.ConnectivityManager) r0
            android.net.NetworkInfo r0 = r0.getActiveNetworkInfo()
            r3.lastNetInfo = r0
            r1 = 1
            if (r0 == 0) goto L_0x0034
            int r2 = r0.getType()
            if (r2 == 0) goto L_0x0022
            if (r2 == r1) goto L_0x0020
            r0 = 9
            if (r2 == r0) goto L_0x001e
            goto L_0x0034
        L_0x001e:
            r1 = 7
            goto L_0x0035
        L_0x0020:
            r1 = 6
            goto L_0x0035
        L_0x0022:
            int r0 = r0.getSubtype()
            switch(r0) {
                case 1: goto L_0x0035;
                case 2: goto L_0x0032;
                case 3: goto L_0x0030;
                case 4: goto L_0x0029;
                case 5: goto L_0x0030;
                case 6: goto L_0x002e;
                case 7: goto L_0x0032;
                case 8: goto L_0x002e;
                case 9: goto L_0x002e;
                case 10: goto L_0x002e;
                case 11: goto L_0x0029;
                case 12: goto L_0x002e;
                case 13: goto L_0x002c;
                case 14: goto L_0x0029;
                case 15: goto L_0x002e;
                default: goto L_0x0029;
            }
        L_0x0029:
            r1 = 11
            goto L_0x0035
        L_0x002c:
            r1 = 5
            goto L_0x0035
        L_0x002e:
            r1 = 4
            goto L_0x0035
        L_0x0030:
            r1 = 3
            goto L_0x0035
        L_0x0032:
            r1 = 2
            goto L_0x0035
        L_0x0034:
            r1 = 0
        L_0x0035:
            org.telegram.messenger.voip.VoIPController r0 = r3.controller
            if (r0 == 0) goto L_0x003c
            r0.setNetworkType(r1)
        L_0x003c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPBaseService.updateNetworkType():void");
    }

    /* access modifiers changed from: protected */
    public void callFailed() {
        VoIPController voIPController = this.controller;
        callFailed((voIPController == null || !this.controllerStarted) ? 0 : voIPController.getLastError());
    }

    /* access modifiers changed from: protected */
    public Bitmap getRoundAvatarBitmap(TLObject tLObject) {
        AvatarDrawable avatarDrawable;
        Bitmap decodeFile;
        boolean z = tLObject instanceof TLRPC.User;
        Bitmap bitmap = null;
        if (z) {
            TLRPC.User user = (TLRPC.User) tLObject;
            TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
            if (!(userProfilePhoto == null || userProfilePhoto.photo_small == null)) {
                BitmapDrawable imageFromMemory = ImageLoader.getInstance().getImageFromMemory(user.photo.photo_small, (String) null, "50_50");
                if (imageFromMemory != null) {
                    decodeFile = imageFromMemory.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                } else {
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(user.photo.photo_small, true).toString(), options);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                bitmap = decodeFile;
            }
        } else {
            TLRPC.Chat chat = (TLRPC.Chat) tLObject;
            TLRPC.ChatPhoto chatPhoto = chat.photo;
            if (!(chatPhoto == null || chatPhoto.photo_small == null)) {
                BitmapDrawable imageFromMemory2 = ImageLoader.getInstance().getImageFromMemory(chat.photo.photo_small, (String) null, "50_50");
                if (imageFromMemory2 != null) {
                    bitmap = imageFromMemory2.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                } else {
                    try {
                        BitmapFactory.Options options2 = new BitmapFactory.Options();
                        options2.inMutable = true;
                        bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(chat.photo.photo_small, true).toString(), options2);
                    } catch (Throwable th2) {
                        FileLog.e(th2);
                    }
                }
            }
        }
        if (bitmap == null) {
            Theme.createDialogsResources(this);
            if (z) {
                avatarDrawable = new AvatarDrawable((TLRPC.User) tLObject);
            } else {
                avatarDrawable = new AvatarDrawable((TLRPC.Chat) tLObject);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v8, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v10, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r15v6 */
    /* JADX WARNING: type inference failed for: r15v7 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r15v3, types: [boolean, int] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00dc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showIncomingNotification(java.lang.String r17, java.lang.CharSequence r18, org.telegram.tgnet.TLObject r19, java.util.List<org.telegram.tgnet.TLRPC.User> r20, int r21, java.lang.Class<? extends android.app.Activity> r22) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = r18
            r3 = r19
            android.content.Intent r4 = new android.content.Intent
            r5 = r22
            r4.<init>(r0, r5)
            r5 = 805306368(0x30000000, float:4.656613E-10)
            r4.addFlags(r5)
            android.app.Notification$Builder r5 = new android.app.Notification$Builder
            r5.<init>(r0)
            r6 = 2131627079(0x7f0e0CLASSNAME, float:1.8881412E38)
            java.lang.String r7 = "VoipInCallBranding"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r7, r6)
            android.app.Notification$Builder r5 = r5.setContentTitle(r8)
            android.app.Notification$Builder r5 = r5.setContentText(r1)
            r8 = 2131165736(0x7var_, float:1.7945698E38)
            android.app.Notification$Builder r5 = r5.setSmallIcon(r8)
            android.app.Notification$Builder r5 = r5.setSubText(r2)
            r8 = 0
            android.app.PendingIntent r9 = android.app.PendingIntent.getActivity(r0, r8, r4, r8)
            android.app.Notification$Builder r5 = r5.setContentIntent(r9)
            java.lang.String r9 = "content://org.telegram.messenger.beta.call_sound_provider/start_ringing"
            android.net.Uri r9 = android.net.Uri.parse(r9)
            int r10 = android.os.Build.VERSION.SDK_INT
            r14 = 26
            if (r10 < r14) goto L_0x0128
            android.content.SharedPreferences r10 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings()
            java.lang.String r14 = "calls_notification_channel"
            int r15 = r10.getInt(r14, r8)
            java.lang.String r6 = "notification"
            java.lang.Object r6 = r0.getSystemService(r6)
            android.app.NotificationManager r6 = (android.app.NotificationManager) r6
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r11 = "incoming_calls"
            r13.append(r11)
            r13.append(r15)
            java.lang.String r11 = r13.toString()
            android.app.NotificationChannel r11 = r6.getNotificationChannel(r11)
            if (r11 == 0) goto L_0x007a
            java.lang.String r11 = r11.getId()
            r6.deleteNotificationChannel(r11)
        L_0x007a:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r13 = "incoming_calls2"
            r11.append(r13)
            r11.append(r15)
            java.lang.String r11 = r11.toString()
            android.app.NotificationChannel r11 = r6.getNotificationChannel(r11)
            if (r11 == 0) goto L_0x00d9
            int r12 = r11.getImportance()
            r8 = 4
            if (r12 < r8) goto L_0x00b1
            android.net.Uri r8 = r11.getSound()
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x00b1
            long[] r8 = r11.getVibrationPattern()
            if (r8 != 0) goto L_0x00b1
            boolean r8 = r11.shouldVibrate()
            if (r8 == 0) goto L_0x00af
            goto L_0x00b1
        L_0x00af:
            r8 = 0
            goto L_0x00da
        L_0x00b1:
            boolean r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r8 == 0) goto L_0x00ba
            java.lang.String r8 = "User messed up the notification channel; deleting it and creating a proper one"
            org.telegram.messenger.FileLog.d(r8)
        L_0x00ba:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r13)
            r8.append(r15)
            java.lang.String r8 = r8.toString()
            r6.deleteNotificationChannel(r8)
            int r15 = r15 + 1
            android.content.SharedPreferences$Editor r8 = r10.edit()
            android.content.SharedPreferences$Editor r8 = r8.putInt(r14, r15)
            r8.commit()
        L_0x00d9:
            r8 = 1
        L_0x00da:
            if (r8 == 0) goto L_0x0115
            android.media.AudioAttributes$Builder r8 = new android.media.AudioAttributes$Builder
            r8.<init>()
            r10 = 6
            android.media.AudioAttributes$Builder r8 = r8.setUsage(r10)
            android.media.AudioAttributes r8 = r8.build()
            android.app.NotificationChannel r10 = new android.app.NotificationChannel
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r13)
            r11.append(r15)
            java.lang.String r11 = r11.toString()
            r12 = 2131625361(0x7f0e0591, float:1.8877928E38)
            java.lang.String r14 = "IncomingCalls"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r14 = 4
            r10.<init>(r11, r12, r14)
            r10.setSound(r9, r8)
            r8 = 0
            r10.enableVibration(r8)
            r10.enableLights(r8)
            r6.createNotificationChannel(r10)
        L_0x0115:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r13)
            r6.append(r15)
            java.lang.String r6 = r6.toString()
            r5.setChannelId(r6)
            goto L_0x0130
        L_0x0128:
            r6 = 21
            if (r10 < r6) goto L_0x0130
            r6 = 2
            r5.setSound(r9, r6)
        L_0x0130:
            android.content.Intent r6 = new android.content.Intent
            java.lang.Class<org.telegram.messenger.voip.VoIPActionsReceiver> r8 = org.telegram.messenger.voip.VoIPActionsReceiver.class
            r6.<init>(r0, r8)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = r16.getPackageName()
            r8.append(r9)
            java.lang.String r9 = ".DECLINE_CALL"
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r6.setAction(r8)
            long r8 = r16.getCallID()
            java.lang.String r10 = "call_id"
            r6.putExtra(r10, r8)
            r8 = 2131627072(0x7f0e0CLASSNAME, float:1.8881398E38)
            java.lang.String r9 = "VoipDeclineCall"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r9, r8)
            int r12 = android.os.Build.VERSION.SDK_INT
            r13 = 24
            if (r12 < r13) goto L_0x017e
            android.text.SpannableString r12 = new android.text.SpannableString
            r12.<init>(r11)
            android.text.style.ForegroundColorSpan r11 = new android.text.style.ForegroundColorSpan
            r14 = -769226(0xffffffffffvar_, float:NaN)
            r11.<init>(r14)
            int r14 = r12.length()
            r15 = 0
            r12.setSpan(r11, r15, r14, r15)
            r11 = r12
            goto L_0x017f
        L_0x017e:
            r15 = 0
        L_0x017f:
            r12 = 268435456(0x10000000, float:2.5243549E-29)
            android.app.PendingIntent r6 = android.app.PendingIntent.getBroadcast(r0, r15, r6, r12)
            r14 = 2131165451(0x7var_b, float:1.794512E38)
            r5.addAction(r14, r11, r6)
            android.content.Intent r11 = new android.content.Intent
            java.lang.Class<org.telegram.messenger.voip.VoIPActionsReceiver> r14 = org.telegram.messenger.voip.VoIPActionsReceiver.class
            r11.<init>(r0, r14)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = r16.getPackageName()
            r14.append(r15)
            java.lang.String r15 = ".ANSWER_CALL"
            r14.append(r15)
            java.lang.String r14 = r14.toString()
            r11.setAction(r14)
            long r14 = r16.getCallID()
            r11.putExtra(r10, r14)
            r10 = 2131627064(0x7f0e0CLASSNAME, float:1.8881382E38)
            java.lang.String r14 = "VoipAnswerCall"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            int r14 = android.os.Build.VERSION.SDK_INT
            if (r14 < r13) goto L_0x01d5
            android.text.SpannableString r13 = new android.text.SpannableString
            r13.<init>(r10)
            android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan
            r14 = -16733696(0xfffffffffvar_aa00, float:-1.7102387E38)
            r10.<init>(r14)
            int r14 = r13.length()
            r15 = 0
            r13.setSpan(r10, r15, r14, r15)
            r10 = r13
            goto L_0x01d6
        L_0x01d5:
            r15 = 0
        L_0x01d6:
            android.app.PendingIntent r11 = android.app.PendingIntent.getBroadcast(r0, r15, r11, r12)
            r12 = 2131165450(0x7var_a, float:1.7945117E38)
            r5.addAction(r12, r10, r11)
            r10 = 2
            r5.setPriority(r10)
            int r10 = android.os.Build.VERSION.SDK_INT
            r12 = 17
            if (r10 < r12) goto L_0x01ed
            r5.setShowWhen(r15)
        L_0x01ed:
            int r10 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r10 < r12) goto L_0x0230
            r10 = -13851168(0xffffffffff2ca5e0, float:-2.2948849E38)
            r5.setColor(r10)
            long[] r10 = new long[r15]
            r5.setVibrate(r10)
            java.lang.String r10 = "call"
            r5.setCategory(r10)
            android.app.PendingIntent r4 = android.app.PendingIntent.getActivity(r0, r15, r4, r15)
            r10 = 1
            r5.setFullScreenIntent(r4, r10)
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.User
            if (r4 == 0) goto L_0x0230
            r4 = r3
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC.User) r4
            java.lang.String r10 = r4.phone
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 != 0) goto L_0x0230
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r12 = "tel:"
            r10.append(r12)
            java.lang.String r4 = r4.phone
            r10.append(r4)
            java.lang.String r4 = r10.toString()
            r5.addPerson(r4)
        L_0x0230:
            android.app.Notification r4 = r5.getNotification()
            int r10 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r10 < r12) goto L_0x0303
            android.widget.RemoteViews r10 = new android.widget.RemoteViews
            java.lang.String r12 = r16.getPackageName()
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x0248
            r13 = 2131427329(0x7f0b0001, float:1.8476271E38)
            goto L_0x024a
        L_0x0248:
            r13 = 2131427328(0x7f0b0000, float:1.847627E38)
        L_0x024a:
            r10.<init>(r12, r13)
            r12 = 2131230849(0x7var_, float:1.8077762E38)
            r10.setTextViewText(r12, r1)
            boolean r1 = android.text.TextUtils.isEmpty(r18)
            r12 = 2131230907(0x7var_bb, float:1.807788E38)
            r13 = 2131230891(0x7var_ab, float:1.8077848E38)
            if (r1 == 0) goto L_0x029a
            r1 = 8
            r10.setViewVisibility(r13, r1)
            int r1 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r2 = 1
            if (r1 <= r2) goto L_0x028f
            int r1 = r0.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            r7 = 2131627080(0x7f0e0CLASSNAME, float:1.8881414E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r13 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r13, r1)
            r13 = 0
            r2[r13] = r1
            java.lang.String r1 = "VoipInCallBrandingWithName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r2)
            r10.setTextViewText(r12, r1)
            goto L_0x02cd
        L_0x028f:
            r1 = 2131627079(0x7f0e0CLASSNAME, float:1.8881412E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r1)
            r10.setTextViewText(r12, r1)
            goto L_0x02cd
        L_0x029a:
            int r1 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r7 = 1
            if (r1 <= r7) goto L_0x02c5
            int r1 = r0.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            r14 = 2131627065(0x7f0e0CLASSNAME, float:1.8881384E38)
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r15 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r15, r1)
            r15 = 0
            r7[r15] = r1
            java.lang.String r1 = "VoipAnsweringAsAccount"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r14, r7)
            r10.setTextViewText(r13, r1)
            goto L_0x02ca
        L_0x02c5:
            r1 = 8
            r10.setViewVisibility(r13, r1)
        L_0x02ca:
            r10.setTextViewText(r12, r2)
        L_0x02cd:
            android.graphics.Bitmap r1 = r0.getRoundAvatarBitmap(r3)
            r2 = 2131230769(0x7var_, float:1.80776E38)
            r3 = 2131627064(0x7f0e0CLASSNAME, float:1.8881382E38)
            java.lang.String r7 = "VoipAnswerCall"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r10.setTextViewText(r2, r3)
            r2 = 2131230796(0x7var_c, float:1.8077655E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r10.setTextViewText(r2, r3)
            r2 = 2131230869(0x7var_, float:1.8077803E38)
            r10.setImageViewBitmap(r2, r1)
            r2 = 2131230768(0x7var_, float:1.8077598E38)
            r10.setOnClickPendingIntent(r2, r11)
            r2 = 2131230795(0x7var_b, float:1.8077653E38)
            r10.setOnClickPendingIntent(r2, r6)
            r5.setLargeIcon(r1)
            r4.bigContentView = r10
            r4.headsUpContentView = r10
        L_0x0303:
            r1 = 202(0xca, float:2.83E-43)
            r0.startForeground(r1, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPBaseService.showIncomingNotification(java.lang.String, java.lang.CharSequence, org.telegram.tgnet.TLObject, java.util.List, int, java.lang.Class):void");
    }

    /* access modifiers changed from: protected */
    public void callFailed(int i) {
        CallConnection callConnection;
        SoundPool soundPool2;
        try {
            throw new Exception("Call " + getCallID() + " failed with error code " + i);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.lastError = i;
            dispatchStateChanged(4);
            if (!(i == -3 || (soundPool2 = this.soundPool) == null)) {
                this.playingSound = true;
                soundPool2.play(this.spFailedID, 1.0f, 1.0f, 0, 0, 1.0f);
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

    /* access modifiers changed from: package-private */
    public void callFailedFromConnectionService() {
        if (this.isOutgoing) {
            callFailed(-5);
        } else {
            hangUp();
        }
    }

    public void onConnectionStateChanged(int i) {
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
            int i2 = this.spPlayID;
            if (i2 != 0) {
                this.soundPool.stop(i2);
                this.spPlayID = 0;
            }
            if (!this.wasEstablished) {
                this.wasEstablished = true;
                if (!this.isProximityNear) {
                    Vibrator vibrator2 = (Vibrator) getSystemService("vibrator");
                    if (vibrator2.hasVibrator()) {
                        vibrator2.vibrate(100);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        VoIPBaseService voIPBaseService = VoIPBaseService.this;
                        if (voIPBaseService.controller != null) {
                            StatsController.getInstance(VoIPBaseService.this.currentAccount).incrementTotalCallsTime(voIPBaseService.getStatsNetworkType(), 5);
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
            int i3 = this.spPlayID;
            if (i3 != 0) {
                this.soundPool.stop(i3);
            }
            this.spPlayID = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        }
        dispatchStateChanged(i);
    }

    public void onSignalBarCountChanged(int i) {
        this.signalBarCount = i;
        for (int i2 = 0; i2 < this.stateListeners.size(); i2++) {
            this.stateListeners.get(i2).onSignalBarsCountChanged(i);
        }
    }

    /* access modifiers changed from: protected */
    public void callEnded() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Call " + getCallID() + " ended");
        }
        dispatchStateChanged(11);
        long j = 700;
        if (this.needPlayEndSound) {
            this.playingSound = true;
            this.soundPool.play(this.spEndId, 1.0f, 1.0f, 0, 0, 1.0f);
            AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 700);
        }
        Runnable runnable = this.timeoutRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.timeoutRunnable = null;
        }
        if (!this.needPlayEndSound) {
            j = 0;
        }
        endConnectionServiceCall(j);
        stopSelf();
    }

    /* access modifiers changed from: protected */
    public void endConnectionServiceCall(long j) {
        if (USE_CONNECTION_SERVICE) {
            AnonymousClass8 r0 = new Runnable() {
                public void run() {
                    VoIPBaseService voIPBaseService = VoIPBaseService.this;
                    CallConnection callConnection = voIPBaseService.systemCallConnection;
                    if (callConnection != null) {
                        int i = voIPBaseService.callDiscardReason;
                        int i2 = 2;
                        if (i == 1) {
                            if (!voIPBaseService.isOutgoing) {
                                i2 = 6;
                            }
                            callConnection.setDisconnected(new DisconnectCause(i2));
                        } else if (i != 2) {
                            int i3 = 4;
                            if (i == 3) {
                                if (!voIPBaseService.isOutgoing) {
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
                        VoIPBaseService.this.systemCallConnection.destroy();
                        VoIPBaseService.this.systemCallConnection = null;
                    }
                }
            };
            if (j > 0) {
                AndroidUtilities.runOnUIThread(r0, j);
            } else {
                r0.run();
            }
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
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            acceptIncomingCall();
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, getUIActivityClass()).addFlags(NUM), 0).send();
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
        if (this.controller != null && this.controllerStarted) {
            boolean z = false;
            if (!USE_CONNECTION_SERVICE) {
                AudioManager audioManager = (AudioManager) getSystemService("audio");
                this.controller.setAudioOutputGainControlEnabled(hasEarpiece() && !audioManager.isSpeakerphoneOn() && !audioManager.isBluetoothScoOn() && !this.isHeadsetPlugged);
                VoIPController voIPController = this.controller;
                if (!this.isHeadsetPlugged && (!hasEarpiece() || audioManager.isSpeakerphoneOn() || audioManager.isBluetoothScoOn() || this.isHeadsetPlugged)) {
                    z = true;
                }
                voIPController.setEchoCancellationStrength(z ? 1 : 0);
                return;
            }
            if (this.systemCallConnection.getCallAudioState().getRoute() == 1) {
                z = true;
            }
            this.controller.setAudioOutputGainControlEnabled(z);
            this.controller.setEchoCancellationStrength(z ^ true ? 1 : 0);
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

    /* access modifiers changed from: protected */
    @TargetApi(26)
    public PhoneAccountHandle addAccountToTelecomManager() {
        TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        ComponentName componentName = new ComponentName(this, TelegramConnectionService.class);
        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(componentName, "" + currentUser.id);
        ((TelecomManager) getSystemService("telecom")).registerPhoneAccount(new PhoneAccount.Builder(phoneAccountHandle, ContactsController.formatName(currentUser.first_name, currentUser.last_name)).setCapabilities(2048).setIcon(Icon.createWithResource(this, NUM)).setHighlightColor(-13851168).addSupportedUriScheme("sip").build());
        return phoneAccountHandle;
    }

    private static boolean isDeviceCompatibleWithConnectionServiceAPI() {
        if (Build.VERSION.SDK_INT < 26) {
            return false;
        }
        if ("angler".equals(Build.PRODUCT) || "bullhead".equals(Build.PRODUCT) || "sailfish".equals(Build.PRODUCT) || "marlin".equals(Build.PRODUCT) || "walleye".equals(Build.PRODUCT) || "taimen".equals(Build.PRODUCT) || "blueline".equals(Build.PRODUCT) || "crosshatch".equals(Build.PRODUCT) || MessagesController.getGlobalMainSettings().getBoolean("dbg_force_connection_service", false)) {
            return true;
        }
        return false;
    }

    @TargetApi(26)
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
