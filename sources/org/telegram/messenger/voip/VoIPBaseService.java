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
import org.telegram.messenger.voip.TgVoip;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
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
    protected long callStartTime;
    protected Runnable connectingSoundRunnable;
    protected PowerManager.WakeLock cpuWakelock;
    protected int currentAccount = -1;
    protected int currentState = 0;
    protected boolean didDeleteConnectionServiceContact = false;
    protected boolean haveAudioFocus;
    protected boolean isBtHeadsetConnected;
    protected boolean isHeadsetPlugged;
    protected boolean isOutgoing;
    protected boolean isProximityNear;
    protected String lastError;
    protected NetworkInfo lastNetInfo;
    private Boolean mHasEarpiece = null;
    protected boolean micMute;
    protected boolean needPlayEndSound;
    protected boolean needSwitchToBluetoothAfterScoActivates = false;
    protected Notification ongoingCallNotification;
    protected boolean playingSound;
    protected TgVoip.TrafficStats prevTrafficStats;
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
    protected CallConnection systemCallConnection;
    protected TgVoip.Instance tgVoip;
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
    public void onTgVoipPreStop() {
    }

    /* access modifiers changed from: protected */
    public void onTgVoipStop(TgVoip.FinalState finalState) {
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
        TgVoip.Instance instance = this.tgVoip;
        if (instance != null) {
            instance.setMuteMicrophone(z);
        }
    }

    public boolean isMicMute() {
        return this.micMute;
    }

    public void toggleSpeakerphoneOrShowRouteSheet(Activity activity) {
        CallConnection callConnection;
        int i = 2;
        if (!isBluetoothHeadsetConnected() || !hasEarpiece()) {
            if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
                int i2 = 5;
                if (hasEarpiece()) {
                    CallConnection callConnection2 = this.systemCallConnection;
                    if (callConnection2.getCallAudioState().getRoute() != 8) {
                        i2 = 8;
                    }
                    callConnection2.setAudioRoute(i2);
                } else {
                    CallConnection callConnection3 = this.systemCallConnection;
                    if (callConnection3.getCallAudioState().getRoute() == 2) {
                        i = 5;
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
        BottomSheet.Builder builder = new BottomSheet.Builder(activity);
        builder.setItems(new CharSequence[]{LocaleController.getString("VoipAudioRoutingBluetooth", NUM), LocaleController.getString("VoipAudioRoutingEarpiece", NUM), LocaleController.getString("VoipAudioRoutingSpeaker", NUM)}, new int[]{NUM, NUM, NUM}, new DialogInterface.OnClickListener() {
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
        });
        BottomSheet create = builder.create();
        create.setBackgroundColor(-13948117);
        create.show();
        ViewGroup sheetContainer = create.getSheetContainer();
        for (int i3 = 0; i3 < sheetContainer.getChildCount(); i3++) {
            ((BottomSheet.BottomSheetCell) sheetContainer.getChildAt(i3)).setTextColor(-1);
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
        TgVoip.Instance instance = this.tgVoip;
        return instance != null ? instance.getDebugInfo() : "";
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
    public void showNotification(String str, TLRPC$FileLocation tLRPC$FileLocation, Class<? extends Activity> cls) {
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
        if (tLRPC$FileLocation != null) {
            BitmapDrawable imageFromMemory = ImageLoader.getInstance().getImageFromMemory(tLRPC$FileLocation, (String) null, "50_50");
            if (imageFromMemory != null) {
                contentIntent.setLargeIcon(imageFromMemory.getBitmap());
            } else {
                try {
                    float dp = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = dp < 1.0f ? 1 : (int) dp;
                    Bitmap decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(tLRPC$FileLocation, true).toString(), options);
                    if (decodeFile != null) {
                        contentIntent.setLargeIcon(decodeFile);
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
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
            if (!USE_CONNECTION_SERVICE) {
                audioManager.requestAudioFocus(this, 2, 1);
            }
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.ringtonePlayer = mediaPlayer;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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
        if (this.tgVoip != null) {
            updateTrafficStats();
            StatsController.getInstance(this.currentAccount).incrementTotalCallsTime(getStatsNetworkType(), ((int) (getCallDuration() / 1000)) % 5);
            onTgVoipPreStop();
            onTgVoipStop(this.tgVoip.stop());
            this.prevTrafficStats = null;
            this.callStartTime = 0;
            this.tgVoip = null;
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
    public void initializeAccountRelatedThings() {
        updateServerConfig();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
    }

    public void onCreate() {
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STARTING ===============");
        }
        try {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (Build.VERSION.SDK_INT < 17 || audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") == null) {
                TgVoip.setBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
            } else {
                TgVoip.setBufferSize(Integer.parseInt(audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")));
            }
            boolean z = true;
            PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "telegram-voip");
            this.cpuWakelock = newWakeLock;
            newWakeLock.acquire();
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
            SoundPool soundPool2 = new SoundPool(1, 0, 0);
            this.soundPool = soundPool2;
            this.spConnectingId = soundPool2.load(this, NUM, 1);
            this.spRingbackID = this.soundPool.load(this, NUM, 1);
            this.spFailedID = this.soundPool.load(this, NUM, 1);
            this.spEndId = this.soundPool.load(this, NUM, 1);
            this.spBusyId = this.soundPool.load(this, NUM, 1);
            audioManager.registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (!USE_CONNECTION_SERVICE && this.btAdapter != null && this.btAdapter.isEnabled()) {
                if (this.btAdapter.getProfileConnectionState(1) != 2) {
                    z = false;
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
    public void updateTrafficStats() {
        TgVoip.TrafficStats trafficStats = this.tgVoip.getTrafficStats();
        long j = trafficStats.bytesSentWifi;
        TgVoip.TrafficStats trafficStats2 = this.prevTrafficStats;
        long j2 = j - (trafficStats2 != null ? trafficStats2.bytesSentWifi : 0);
        long j3 = trafficStats.bytesReceivedWifi;
        TgVoip.TrafficStats trafficStats3 = this.prevTrafficStats;
        long j4 = j3 - (trafficStats3 != null ? trafficStats3.bytesReceivedWifi : 0);
        long j5 = trafficStats.bytesSentMobile;
        TgVoip.TrafficStats trafficStats4 = this.prevTrafficStats;
        long j6 = j5 - (trafficStats4 != null ? trafficStats4.bytesSentMobile : 0);
        long j7 = trafficStats.bytesReceivedMobile;
        TgVoip.TrafficStats trafficStats5 = this.prevTrafficStats;
        long j8 = j7 - (trafficStats5 != null ? trafficStats5.bytesReceivedMobile : 0);
        this.prevTrafficStats = trafficStats;
        if (j2 > 0) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(1, 0, j2);
        }
        if (j4 > 0) {
            StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(1, 0, j4);
        }
        int i = 2;
        if (j6 > 0) {
            StatsController instance = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo = this.lastNetInfo;
            instance.incrementSentBytesCount((networkInfo == null || !networkInfo.isRoaming()) ? 0 : 2, 0, j6);
        }
        if (j8 > 0) {
            StatsController instance2 = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo2 = this.lastNetInfo;
            if (networkInfo2 == null || !networkInfo2.isRoaming()) {
                i = 0;
            }
            instance2.incrementReceivedBytesCount(i, 0, j8);
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

    public String getLastError() {
        return this.lastError;
    }

    public int getCallState() {
        return this.currentState;
    }

    /* access modifiers changed from: protected */
    public void updateNetworkType() {
        TgVoip.Instance instance = this.tgVoip;
        if (instance != null) {
            instance.setNetworkType(getNetworkType());
        } else {
            this.lastNetInfo = getActiveNetworkInfo();
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
        TgVoip.Instance instance = this.tgVoip;
        callFailed(instance != null ? instance.getLastError() : "ERROR_UNKNOWN");
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v8, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: android.text.SpannableString} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r8v14 */
    /* JADX WARNING: type inference failed for: r8v15 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r8v7, types: [int, boolean] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00dc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showIncomingNotification(java.lang.String r17, java.lang.CharSequence r18, org.telegram.tgnet.TLObject r19, java.util.List<org.telegram.tgnet.TLRPC$User> r20, int r21, java.lang.Class<? extends android.app.Activity> r22) {
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
            r6 = 2131627243(0x7f0e0ceb, float:1.8881745E38)
            java.lang.String r7 = "VoipInCallBranding"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r7, r6)
            android.app.Notification$Builder r5 = r5.setContentTitle(r8)
            android.app.Notification$Builder r5 = r5.setContentText(r1)
            r8 = 2131165744(0x7var_, float:1.7945714E38)
            android.app.Notification$Builder r5 = r5.setSmallIcon(r8)
            android.app.Notification$Builder r5 = r5.setSubText(r2)
            r8 = 0
            android.app.PendingIntent r9 = android.app.PendingIntent.getActivity(r0, r8, r4, r8)
            android.app.Notification$Builder r5 = r5.setContentIntent(r9)
            java.lang.String r9 = "content://org.telegram.messenger.beta.call_sound_provider/start_ringing"
            android.net.Uri r9 = android.net.Uri.parse(r9)
            int r10 = android.os.Build.VERSION.SDK_INT
            r14 = 26
            if (r10 < r14) goto L_0x0127
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
            r12 = 4
            if (r11 == 0) goto L_0x00d9
            int r8 = r11.getImportance()
            if (r8 < r12) goto L_0x00b1
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
            if (r8 == 0) goto L_0x0114
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
            r14 = 2131625467(0x7f0e05fb, float:1.8878143E38)
            java.lang.String r2 = "IncomingCalls"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r14)
            r10.<init>(r11, r2, r12)
            r10.setSound(r9, r8)
            r2 = 0
            r10.enableVibration(r2)
            r10.enableLights(r2)
            r6.createNotificationChannel(r10)
        L_0x0114:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r13)
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            r5.setChannelId(r2)
            goto L_0x012f
        L_0x0127:
            r2 = 21
            if (r10 < r2) goto L_0x012f
            r2 = 2
            r5.setSound(r9, r2)
        L_0x012f:
            android.content.Intent r2 = new android.content.Intent
            java.lang.Class<org.telegram.messenger.voip.VoIPActionsReceiver> r6 = org.telegram.messenger.voip.VoIPActionsReceiver.class
            r2.<init>(r0, r6)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = r16.getPackageName()
            r6.append(r8)
            java.lang.String r8 = ".DECLINE_CALL"
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            r2.setAction(r6)
            long r8 = r16.getCallID()
            java.lang.String r6 = "call_id"
            r2.putExtra(r6, r8)
            r8 = 2131627236(0x7f0e0ce4, float:1.888173E38)
            java.lang.String r9 = "VoipDeclineCall"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r9, r8)
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 24
            if (r11 < r12) goto L_0x017d
            android.text.SpannableString r11 = new android.text.SpannableString
            r11.<init>(r10)
            android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan
            r13 = -769226(0xffffffffffvar_, float:NaN)
            r10.<init>(r13)
            int r13 = r11.length()
            r14 = 0
            r11.setSpan(r10, r14, r13, r14)
            r10 = r11
            goto L_0x017e
        L_0x017d:
            r14 = 0
        L_0x017e:
            r11 = 268435456(0x10000000, float:2.5243549E-29)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r0, r14, r2, r11)
            r13 = 2131165453(0x7var_d, float:1.7945124E38)
            r5.addAction(r13, r10, r2)
            android.content.Intent r10 = new android.content.Intent
            java.lang.Class<org.telegram.messenger.voip.VoIPActionsReceiver> r13 = org.telegram.messenger.voip.VoIPActionsReceiver.class
            r10.<init>(r0, r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = r16.getPackageName()
            r13.append(r14)
            java.lang.String r14 = ".ANSWER_CALL"
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r10.setAction(r13)
            long r13 = r16.getCallID()
            r10.putExtra(r6, r13)
            r6 = 2131627228(0x7f0e0cdc, float:1.8881715E38)
            java.lang.String r13 = "VoipAnswerCall"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r13, r6)
            int r15 = android.os.Build.VERSION.SDK_INT
            if (r15 < r12) goto L_0x01d4
            android.text.SpannableString r12 = new android.text.SpannableString
            r12.<init>(r14)
            android.text.style.ForegroundColorSpan r14 = new android.text.style.ForegroundColorSpan
            r15 = -16733696(0xfffffffffvar_aa00, float:-1.7102387E38)
            r14.<init>(r15)
            int r15 = r12.length()
            r8 = 0
            r12.setSpan(r14, r8, r15, r8)
            r14 = r12
            goto L_0x01d5
        L_0x01d4:
            r8 = 0
        L_0x01d5:
            android.app.PendingIntent r10 = android.app.PendingIntent.getBroadcast(r0, r8, r10, r11)
            r11 = 2131165452(0x7var_c, float:1.7945122E38)
            r5.addAction(r11, r14, r10)
            r11 = 2
            r5.setPriority(r11)
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 17
            if (r11 < r12) goto L_0x01ec
            r5.setShowWhen(r8)
        L_0x01ec:
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r11 < r12) goto L_0x022f
            r11 = -13851168(0xffffffffff2ca5e0, float:-2.2948849E38)
            r5.setColor(r11)
            long[] r11 = new long[r8]
            r5.setVibrate(r11)
            java.lang.String r11 = "call"
            r5.setCategory(r11)
            android.app.PendingIntent r4 = android.app.PendingIntent.getActivity(r0, r8, r4, r8)
            r8 = 1
            r5.setFullScreenIntent(r4, r8)
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$User
            if (r4 == 0) goto L_0x022f
            r4 = r3
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            java.lang.String r8 = r4.phone
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 != 0) goto L_0x022f
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r11 = "tel:"
            r8.append(r11)
            java.lang.String r4 = r4.phone
            r8.append(r4)
            java.lang.String r4 = r8.toString()
            r5.addPerson(r4)
        L_0x022f:
            android.app.Notification r4 = r5.getNotification()
            int r8 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r8 < r11) goto L_0x0300
            android.widget.RemoteViews r8 = new android.widget.RemoteViews
            java.lang.String r11 = r16.getPackageName()
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x0247
            r12 = 2131427329(0x7f0b0001, float:1.8476271E38)
            goto L_0x0249
        L_0x0247:
            r12 = 2131427328(0x7f0b0000, float:1.847627E38)
        L_0x0249:
            r8.<init>(r11, r12)
            r11 = 2131230806(0x7var_, float:1.8077675E38)
            r8.setTextViewText(r11, r1)
            boolean r1 = android.text.TextUtils.isEmpty(r18)
            r11 = 8
            r12 = 2131230857(0x7var_, float:1.8077779E38)
            r14 = 2131230843(0x7var_b, float:1.807775E38)
            if (r1 == 0) goto L_0x0299
            r8.setViewVisibility(r14, r11)
            int r1 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r11 = 1
            if (r1 <= r11) goto L_0x028e
            int r1 = r0.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            r7 = 2131627244(0x7f0e0cec, float:1.8881747E38)
            java.lang.Object[] r11 = new java.lang.Object[r11]
            java.lang.String r14 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r14, r1)
            r14 = 0
            r11[r14] = r1
            java.lang.String r1 = "VoipInCallBrandingWithName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)
            r8.setTextViewText(r12, r1)
            goto L_0x02cc
        L_0x028e:
            r1 = 2131627243(0x7f0e0ceb, float:1.8881745E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r1)
            r8.setTextViewText(r12, r1)
            goto L_0x02cc
        L_0x0299:
            int r1 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r7 = 1
            if (r1 <= r7) goto L_0x02c4
            int r1 = r0.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            r11 = 2131627229(0x7f0e0cdd, float:1.8881717E38)
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r15 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r15, r1)
            r15 = 0
            r7[r15] = r1
            java.lang.String r1 = "VoipAnsweringAsAccount"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r11, r7)
            r8.setTextViewText(r14, r1)
            goto L_0x02c7
        L_0x02c4:
            r8.setViewVisibility(r14, r11)
        L_0x02c7:
            r1 = r18
            r8.setTextViewText(r12, r1)
        L_0x02cc:
            android.graphics.Bitmap r1 = r0.getRoundAvatarBitmap(r3)
            r3 = 2131230765(0x7var_d, float:1.8077592E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r6)
            r8.setTextViewText(r3, r6)
            r3 = 2131230780(0x7var_c, float:1.8077622E38)
            r6 = 2131627236(0x7f0e0ce4, float:1.888173E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r8.setTextViewText(r3, r6)
            r3 = 2131230826(0x7var_a, float:1.8077716E38)
            r8.setImageViewBitmap(r3, r1)
            r3 = 2131230764(0x7var_c, float:1.807759E38)
            r8.setOnClickPendingIntent(r3, r10)
            r3 = 2131230779(0x7var_b, float:1.807762E38)
            r8.setOnClickPendingIntent(r3, r2)
            r5.setLargeIcon(r1)
            r4.bigContentView = r8
            r4.headsUpContentView = r8
        L_0x0300:
            r1 = 202(0xca, float:2.83E-43)
            r0.startForeground(r1, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPBaseService.showIncomingNotification(java.lang.String, java.lang.CharSequence, org.telegram.tgnet.TLObject, java.util.List, int, java.lang.Class):void");
    }

    /* access modifiers changed from: protected */
    public void callFailed(String str) {
        CallConnection callConnection;
        SoundPool soundPool2;
        try {
            throw new Exception("Call " + getCallID() + " failed with error: " + str);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.lastError = str;
            dispatchStateChanged(4);
            if (TextUtils.equals(str, "ERROR_LOCALIZED") && (soundPool2 = this.soundPool) != null) {
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
            callFailed("ERROR_CONNECTION_SERVICE");
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
                            if (!VoIPBaseService.this.isOutgoing) {
                                i2 = 6;
                            }
                            callConnection.setDisconnected(new DisconnectCause(i2));
                        } else if (i != 2) {
                            int i3 = 4;
                            if (i == 3) {
                                if (!VoIPBaseService.this.isOutgoing) {
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
        if (this.tgVoip != null) {
            boolean z = false;
            if (!USE_CONNECTION_SERVICE) {
                AudioManager audioManager = (AudioManager) getSystemService("audio");
                this.tgVoip.setAudioOutputGainControlEnabled(hasEarpiece() && !audioManager.isSpeakerphoneOn() && !audioManager.isBluetoothScoOn() && !this.isHeadsetPlugged);
                TgVoip.Instance instance = this.tgVoip;
                if (!this.isHeadsetPlugged && (!hasEarpiece() || audioManager.isSpeakerphoneOn() || audioManager.isBluetoothScoOn() || this.isHeadsetPlugged)) {
                    z = true;
                }
                instance.setEchoCancellationStrength(z ? 1 : 0);
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
