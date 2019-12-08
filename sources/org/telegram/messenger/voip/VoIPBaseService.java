package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
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
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VoIPController.ConnectionStateListener;
import org.telegram.messenger.voip.VoIPController.Stats;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.VoIPPermissionActivity;

public abstract class VoIPBaseService extends Service implements SensorEventListener, OnAudioFocusChangeListener, ConnectionStateListener, NotificationCenterDelegate {
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
                String str = "audio";
                if (VoIPBaseService.this.isBtHeadsetConnected) {
                    ((AudioManager) ApplicationLoader.applicationContext.getSystemService(str)).stopBluetoothSco();
                }
                ((AudioManager) ApplicationLoader.applicationContext.getSystemService(str)).setSpeakerphoneOn(false);
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
    protected WakeLock cpuWakelock;
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
    protected Stats prevStats = new Stats();
    protected WakeLock proximityWakelock;
    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String str = "state";
            boolean z = true;
            VoIPBaseService voIPBaseService;
            if ("android.intent.action.HEADSET_PLUG".equals(intent.getAction())) {
                voIPBaseService = VoIPBaseService.this;
                if (intent.getIntExtra(str, 0) != 1) {
                    z = false;
                }
                voIPBaseService.isHeadsetPlugged = z;
                voIPBaseService = VoIPBaseService.this;
                if (voIPBaseService.isHeadsetPlugged) {
                    WakeLock wakeLock = voIPBaseService.proximityWakelock;
                    if (wakeLock != null && wakeLock.isHeld()) {
                        VoIPBaseService.this.proximityWakelock.release();
                    }
                }
                voIPBaseService = VoIPBaseService.this;
                voIPBaseService.isProximityNear = false;
                voIPBaseService.updateOutputGainControlState();
            } else {
                if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                    VoIPBaseService.this.updateNetworkType();
                } else {
                    if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                        str = "android.bluetooth.profile.extra.STATE";
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("bt headset state = ");
                            stringBuilder.append(intent.getIntExtra(str, 0));
                            FileLog.e(stringBuilder.toString());
                        }
                        voIPBaseService = VoIPBaseService.this;
                        if (intent.getIntExtra(str, 0) != 2) {
                            z = false;
                        }
                        voIPBaseService.updateBluetoothHeadsetState(z);
                    } else {
                        if ("android.media.ACTION_SCO_AUDIO_STATE_UPDATED".equals(intent.getAction())) {
                            int intExtra = intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", 0);
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Bluetooth SCO state updated: ");
                                stringBuilder2.append(intExtra);
                                FileLog.e(stringBuilder2.toString());
                            }
                            if (intExtra == 0) {
                                VoIPBaseService voIPBaseService2 = VoIPBaseService.this;
                                if (voIPBaseService2.isBtHeadsetConnected && !(voIPBaseService2.btAdapter.isEnabled() && VoIPBaseService.this.btAdapter.getProfileConnectionState(1) == 2)) {
                                    VoIPBaseService.this.updateBluetoothHeadsetState(false);
                                    return;
                                }
                            }
                            VoIPBaseService.this.bluetoothScoActive = intExtra == 1;
                            voIPBaseService = VoIPBaseService.this;
                            if (voIPBaseService.bluetoothScoActive && voIPBaseService.needSwitchToBluetoothAfterScoActivates) {
                                voIPBaseService.needSwitchToBluetoothAfterScoActivates = false;
                                AudioManager audioManager = (AudioManager) voIPBaseService.getSystemService("audio");
                                audioManager.setSpeakerphoneOn(false);
                                audioManager.setBluetoothScoOn(true);
                            }
                            Iterator it = VoIPBaseService.this.stateListeners.iterator();
                            while (it.hasNext()) {
                                ((StateListener) it.next()).onAudioSettingsChanged();
                            }
                        } else {
                            if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
                                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(intent.getStringExtra(str))) {
                                    VoIPBaseService.this.hangUp();
                                }
                            }
                        }
                    }
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
    protected ArrayList<StateListener> stateListeners = new ArrayList();
    protected Stats stats = new Stats();
    protected CallConnection systemCallConnection;
    protected Runnable timeoutRunnable;
    protected Vibrator vibrator;
    private boolean wasEstablished;

    @TargetApi(26)
    public class CallConnection extends Connection {
        public CallConnection() {
            setConnectionProperties(128);
            setAudioModeIsVoip(true);
        }

        public void onCallAudioStateChanged(CallAudioState callAudioState) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ConnectionService call audio state changed: ");
                stringBuilder.append(callAudioState);
                FileLog.d(stringBuilder.toString());
            }
            Iterator it = VoIPBaseService.this.stateListeners.iterator();
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
            voIPBaseService.declineIncomingCall(1, null);
        }

        public void onShowIncomingCallUi() {
            VoIPBaseService.this.startRinging();
        }

        public void onStateChanged(int i) {
            super.onStateChanged(i);
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ConnectionService onStateChanged ");
                stringBuilder.append(Connection.stateToString(i));
                FileLog.d(stringBuilder.toString());
            }
            if (i == 4) {
                ContactsController.getInstance(VoIPBaseService.this.currentAccount).deleteConnectionServiceContact();
                VoIPBaseService.this.didDeleteConnectionServiceContact = true;
            }
        }

        public void onCallEvent(String str, Bundle bundle) {
            super.onCallEvent(str, bundle);
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ConnectionService onCallEvent ");
                stringBuilder.append(str);
                FileLog.d(stringBuilder.toString());
            }
        }

        public void onSilence() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("onSlience");
            }
            VoIPBaseService.this.stopRinging();
        }
    }

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

    public abstract Class<? extends Activity> getUIActivityClass();

    public abstract void hangUp();

    public abstract void hangUp(Runnable runnable);

    /* Access modifiers changed, original: protected */
    public boolean isRinging() {
        return false;
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* Access modifiers changed, original: protected */
    public void onControllerPreRelease() {
    }

    public abstract void showNotification();

    public abstract void startRinging();

    public abstract void startRingtoneAndVibration();

    public abstract void updateServerConfig();

    public boolean hasEarpiece() {
        boolean z = false;
        if (USE_CONNECTION_SERVICE) {
            CallConnection callConnection = this.systemCallConnection;
            if (!(callConnection == null || callConnection.getCallAudioState() == null)) {
                if ((this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 5) != 0) {
                    z = true;
                }
                return z;
            }
        }
        if (((TelephonyManager) getSystemService("phone")).getPhoneType() != 0) {
            return true;
        }
        Boolean bool = this.mHasEarpiece;
        if (bool != null) {
            return bool.booleanValue();
        }
        try {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            Method method = AudioManager.class.getMethod("getDevicesForStream", new Class[]{Integer.TYPE});
            int i = AudioManager.class.getField("DEVICE_OUT_EARPIECE").getInt(null);
            if ((((Integer) method.invoke(audioManager, new Object[]{Integer.valueOf(0)})).intValue() & i) == i) {
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
    }

    /* Access modifiers changed, original: protected */
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
        i = this.signalBarCount;
        if (i != 0) {
            stateListener.onSignalBarsCountChanged(i);
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

    /* JADX WARNING: Removed duplicated region for block: B:38:0x00e3 A:{LOOP_END, LOOP:1: B:36:0x00dd->B:38:0x00e3} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00e3 A:{LOOP_END, LOOP:1: B:36:0x00dd->B:38:0x00e3} */
    public void toggleSpeakerphoneOrShowRouteSheet(android.app.Activity r8) {
        /*
        r7 = this;
        r0 = r7.isBluetoothHeadsetConnected();
        r1 = 2;
        r2 = 1;
        if (r0 == 0) goto L_0x006b;
    L_0x0008:
        r0 = r7.hasEarpiece();
        if (r0 == 0) goto L_0x006b;
    L_0x000e:
        r0 = new org.telegram.ui.ActionBar.BottomSheet$Builder;
        r0.<init>(r8);
        r8 = 3;
        r3 = new java.lang.CharSequence[r8];
        r4 = NUM; // 0x7f0d0b36 float:1.8747936E38 double:1.0531311955E-314;
        r5 = "VoipAudioRoutingBluetooth";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = 0;
        r3[r5] = r4;
        r4 = NUM; // 0x7f0d0b37 float:1.8747938E38 double:1.053131196E-314;
        r6 = "VoipAudioRoutingEarpiece";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3[r2] = r4;
        r2 = NUM; // 0x7f0d0b38 float:1.874794E38 double:1.0531311965E-314;
        r4 = "VoipAudioRoutingSpeaker";
        r2 = org.telegram.messenger.LocaleController.getString(r4, r2);
        r3[r1] = r2;
        r8 = new int[r8];
        r8 = {NUM, NUM, NUM};
        r1 = new org.telegram.messenger.voip.VoIPBaseService$3;
        r1.<init>();
        r8 = r0.setItems(r3, r8, r1);
        r8 = r8.create();
        r0 = -13948117; // 0xffffffffff2b2b2b float:-2.2752213E38 double:NaN;
        r8.setBackgroundColor(r0);
        r8.show();
        r8 = r8.getSheetContainer();
    L_0x0057:
        r0 = r8.getChildCount();
        if (r5 >= r0) goto L_0x006a;
    L_0x005d:
        r0 = r8.getChildAt(r5);
        r0 = (org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell) r0;
        r1 = -1;
        r0.setTextColor(r1);
        r5 = r5 + 1;
        goto L_0x0057;
    L_0x006a:
        return;
    L_0x006b:
        r8 = USE_CONNECTION_SERVICE;
        if (r8 == 0) goto L_0x00a7;
    L_0x006f:
        r8 = r7.systemCallConnection;
        if (r8 == 0) goto L_0x00a7;
    L_0x0073:
        r8 = r8.getCallAudioState();
        if (r8 == 0) goto L_0x00a7;
    L_0x0079:
        r8 = r7.hasEarpiece();
        r0 = 5;
        if (r8 == 0) goto L_0x0095;
    L_0x0080:
        r8 = r7.systemCallConnection;
        r1 = r8.getCallAudioState();
        r1 = r1.getRoute();
        r2 = 8;
        if (r1 != r2) goto L_0x008f;
    L_0x008e:
        goto L_0x0091;
    L_0x008f:
        r0 = 8;
    L_0x0091:
        r8.setAudioRoute(r0);
        goto L_0x00d7;
    L_0x0095:
        r8 = r7.systemCallConnection;
        r2 = r8.getCallAudioState();
        r2 = r2.getRoute();
        if (r2 != r1) goto L_0x00a2;
    L_0x00a1:
        goto L_0x00a3;
    L_0x00a2:
        r0 = 2;
    L_0x00a3:
        r8.setAudioRoute(r0);
        goto L_0x00d7;
    L_0x00a7:
        r8 = r7.audioConfigured;
        if (r8 == 0) goto L_0x00d2;
    L_0x00ab:
        r8 = USE_CONNECTION_SERVICE;
        if (r8 != 0) goto L_0x00d2;
    L_0x00af:
        r8 = "audio";
        r8 = r7.getSystemService(r8);
        r8 = (android.media.AudioManager) r8;
        r0 = r7.hasEarpiece();
        if (r0 == 0) goto L_0x00c6;
    L_0x00bd:
        r0 = r8.isSpeakerphoneOn();
        r0 = r0 ^ r2;
        r8.setSpeakerphoneOn(r0);
        goto L_0x00ce;
    L_0x00c6:
        r0 = r8.isBluetoothScoOn();
        r0 = r0 ^ r2;
        r8.setBluetoothScoOn(r0);
    L_0x00ce:
        r7.updateOutputGainControlState();
        goto L_0x00d7;
    L_0x00d2:
        r8 = r7.speakerphoneStateToSet;
        r8 = r8 ^ r2;
        r7.speakerphoneStateToSet = r8;
    L_0x00d7:
        r8 = r7.stateListeners;
        r8 = r8.iterator();
    L_0x00dd:
        r0 = r8.hasNext();
        if (r0 == 0) goto L_0x00ed;
    L_0x00e3:
        r0 = r8.next();
        r0 = (org.telegram.messenger.voip.VoIPBaseService.StateListener) r0;
        r0.onAudioSettingsChanged();
        goto L_0x00dd;
    L_0x00ed:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPBaseService.toggleSpeakerphoneOrShowRouteSheet(android.app.Activity):void");
    }

    public boolean isSpeakerphoneOn() {
        if (USE_CONNECTION_SERVICE) {
            CallConnection callConnection = this.systemCallConnection;
            if (!(callConnection == null || callConnection.getCallAudioState() == null)) {
                int route = this.systemCallConnection.getCallAudioState().getRoute();
                boolean z = true;
                if (hasEarpiece() ? route != 8 : route != 2) {
                    z = false;
                }
                return z;
            }
        }
        if (!this.audioConfigured || USE_CONNECTION_SERVICE) {
            return this.speakerphoneStateToSet;
        }
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        return hasEarpiece() ? audioManager.isSpeakerphoneOn() : audioManager.isBluetoothScoOn();
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
        if (this.controllerStarted) {
            VoIPController voIPController = this.controller;
            if (voIPController != null) {
                long callDuration = voIPController.getCallDuration();
                this.lastKnownDuration = callDuration;
                return callDuration;
            }
        }
        return this.lastKnownDuration;
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
        Vibrator vibrator = this.vibrator;
        if (vibrator != null) {
            vibrator.cancel();
            this.vibrator = null;
        }
    }

    /* Access modifiers changed, original: protected */
    public void showNotification(String str, FileLocation fileLocation, Class<? extends Activity> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(NUM);
        Builder contentIntent = new Builder(this).setContentTitle(LocaleController.getString("VoipOutgoingCall", NUM)).setContentText(str).setSmallIcon(NUM).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        if (VERSION.SDK_INT >= 16) {
            intent = new Intent(this, VoIPActionsReceiver.class);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getPackageName());
            stringBuilder.append(".END_CALL");
            intent.setAction(stringBuilder.toString());
            contentIntent.addAction(NUM, LocaleController.getString("VoipEndCall", NUM), PendingIntent.getBroadcast(this, 0, intent, NUM));
            contentIntent.setPriority(2);
        }
        if (VERSION.SDK_INT >= 17) {
            contentIntent.setShowWhen(false);
        }
        if (VERSION.SDK_INT >= 21) {
            contentIntent.setColor(-13851168);
        }
        if (VERSION.SDK_INT >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            contentIntent.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
        }
        if (fileLocation != null) {
            BitmapDrawable imageFromMemory = ImageLoader.getInstance().getImageFromMemory(fileLocation, null, "50_50");
            if (imageFromMemory != null) {
                contentIntent.setLargeIcon(imageFromMemory.getBitmap());
            } else {
                try {
                    float dp = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                    Options options = new Options();
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

    /* Access modifiers changed, original: protected */
    public void startRingtoneAndVibration(int i) {
        String str = "custom_";
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if ((audioManager.getRingerMode() != 0 ? 1 : null) != null) {
            StringBuilder stringBuilder;
            if (!USE_CONNECTION_SERVICE) {
                audioManager.requestAudioFocus(this, 2, 1);
            }
            this.ringtonePlayer = new MediaPlayer();
            this.ringtonePlayer.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    VoIPBaseService.this.ringtonePlayer.start();
                }
            });
            this.ringtonePlayer.setLooping(true);
            this.ringtonePlayer.setAudioStreamType(2);
            try {
                String string;
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(i);
                if (notificationsSettings.getBoolean(stringBuilder.toString(), false)) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ringtone_path_");
                    stringBuilder.append(i);
                    string = notificationsSettings.getString(stringBuilder.toString(), RingtoneManager.getDefaultUri(1).toString());
                } else {
                    string = notificationsSettings.getString("CallsRingtonePath", RingtoneManager.getDefaultUri(1).toString());
                }
                this.ringtonePlayer.setDataSource(this, Uri.parse(string));
                this.ringtonePlayer.prepareAsync();
            } catch (Exception e) {
                FileLog.e(e);
                MediaPlayer mediaPlayer = this.ringtonePlayer;
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    this.ringtonePlayer = null;
                }
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(i);
            if (notificationsSettings.getBoolean(stringBuilder.toString(), false)) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("calls_vibrate_");
                stringBuilder2.append(i);
                i = notificationsSettings.getInt(stringBuilder2.toString(), 0);
            } else {
                i = notificationsSettings.getInt("vibrate_calls", 0);
            }
            if ((i != 2 && i != 4 && (audioManager.getRingerMode() == 1 || audioManager.getRingerMode() == 2)) || (i == 4 && audioManager.getRingerMode() == 1)) {
                this.vibrator = (Vibrator) getSystemService("vibrator");
                long j = 700;
                if (i == 1) {
                    j = 350;
                } else if (i == 3) {
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
        WakeLock wakeLock = this.proximityWakelock;
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
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndedCall, new Object[0]);
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
            if (!(callConnection == null || this.playingSound)) {
                callConnection.destroy();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        VoIPHelper.lastCallTime = System.currentTimeMillis();
    }

    /* Access modifiers changed, original: protected */
    public VoIPController createController() {
        return new VoIPController();
    }

    /* Access modifiers changed, original: protected */
    public void initializeAccountRelatedThings() {
        updateServerConfig();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        this.controller = createController();
        this.controller.setConnectionStateListener(this);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x005c A:{Catch:{ Exception -> 0x0105 }} */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0057 A:{Catch:{ Exception -> 0x0105 }} */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x006d A:{Catch:{ Exception -> 0x0105 }} */
    public void onCreate() {
        /*
        r6 = this;
        super.onCreate();
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x000c;
    L_0x0007:
        r0 = "=============== VoIPService STARTING ===============";
        org.telegram.messenger.FileLog.d(r0);
    L_0x000c:
        r0 = "audio";
        r0 = r6.getSystemService(r0);
        r0 = (android.media.AudioManager) r0;
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 17;
        r3 = 2;
        if (r1 < r2) goto L_0x002f;
    L_0x001b:
        r1 = "android.media.property.OUTPUT_FRAMES_PER_BUFFER";
        r2 = r0.getProperty(r1);
        if (r2 == 0) goto L_0x002f;
    L_0x0023:
        r1 = r0.getProperty(r1);
        r1 = java.lang.Integer.parseInt(r1);
        org.telegram.messenger.voip.VoIPController.setNativeBufferSize(r1);
        goto L_0x003b;
    L_0x002f:
        r1 = 48000; // 0xbb80 float:6.7262E-41 double:2.3715E-319;
        r2 = 4;
        r1 = android.media.AudioTrack.getMinBufferSize(r1, r2, r3);
        r1 = r1 / r3;
        org.telegram.messenger.voip.VoIPController.setNativeBufferSize(r1);
    L_0x003b:
        r1 = "power";
        r1 = r6.getSystemService(r1);	 Catch:{ Exception -> 0x0105 }
        r1 = (android.os.PowerManager) r1;	 Catch:{ Exception -> 0x0105 }
        r2 = "telegram-voip";
        r4 = 1;
        r1 = r1.newWakeLock(r4, r2);	 Catch:{ Exception -> 0x0105 }
        r6.cpuWakelock = r1;	 Catch:{ Exception -> 0x0105 }
        r1 = r6.cpuWakelock;	 Catch:{ Exception -> 0x0105 }
        r1.acquire();	 Catch:{ Exception -> 0x0105 }
        r1 = r0.isBluetoothScoAvailableOffCall();	 Catch:{ Exception -> 0x0105 }
        if (r1 == 0) goto L_0x005c;
    L_0x0057:
        r1 = android.bluetooth.BluetoothAdapter.getDefaultAdapter();	 Catch:{ Exception -> 0x0105 }
        goto L_0x005d;
    L_0x005c:
        r1 = 0;
    L_0x005d:
        r6.btAdapter = r1;	 Catch:{ Exception -> 0x0105 }
        r1 = new android.content.IntentFilter;	 Catch:{ Exception -> 0x0105 }
        r1.<init>();	 Catch:{ Exception -> 0x0105 }
        r2 = "android.net.conn.CONNECTIVITY_CHANGE";
        r1.addAction(r2);	 Catch:{ Exception -> 0x0105 }
        r2 = USE_CONNECTION_SERVICE;	 Catch:{ Exception -> 0x0105 }
        if (r2 != 0) goto L_0x0085;
    L_0x006d:
        r2 = "android.intent.action.HEADSET_PLUG";
        r1.addAction(r2);	 Catch:{ Exception -> 0x0105 }
        r2 = r6.btAdapter;	 Catch:{ Exception -> 0x0105 }
        if (r2 == 0) goto L_0x0080;
    L_0x0076:
        r2 = "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED";
        r1.addAction(r2);	 Catch:{ Exception -> 0x0105 }
        r2 = "android.media.ACTION_SCO_AUDIO_STATE_UPDATED";
        r1.addAction(r2);	 Catch:{ Exception -> 0x0105 }
    L_0x0080:
        r2 = "android.intent.action.PHONE_STATE";
        r1.addAction(r2);	 Catch:{ Exception -> 0x0105 }
    L_0x0085:
        r2 = r6.receiver;	 Catch:{ Exception -> 0x0105 }
        r6.registerReceiver(r2, r1);	 Catch:{ Exception -> 0x0105 }
        r1 = new android.media.SoundPool;	 Catch:{ Exception -> 0x0105 }
        r2 = 0;
        r1.<init>(r4, r2, r2);	 Catch:{ Exception -> 0x0105 }
        r6.soundPool = r1;	 Catch:{ Exception -> 0x0105 }
        r1 = r6.soundPool;	 Catch:{ Exception -> 0x0105 }
        r5 = NUM; // 0x7f0CLASSNAMEc float:1.8609216E38 double:1.0530974044E-314;
        r1 = r1.load(r6, r5, r4);	 Catch:{ Exception -> 0x0105 }
        r6.spConnectingId = r1;	 Catch:{ Exception -> 0x0105 }
        r1 = r6.soundPool;	 Catch:{ Exception -> 0x0105 }
        r5 = NUM; // 0x7f0CLASSNAMEf float:1.8609222E38 double:1.053097406E-314;
        r1 = r1.load(r6, r5, r4);	 Catch:{ Exception -> 0x0105 }
        r6.spRingbackID = r1;	 Catch:{ Exception -> 0x0105 }
        r1 = r6.soundPool;	 Catch:{ Exception -> 0x0105 }
        r5 = NUM; // 0x7f0CLASSNAMEe float:1.860922E38 double:1.0530974054E-314;
        r1 = r1.load(r6, r5, r4);	 Catch:{ Exception -> 0x0105 }
        r6.spFailedID = r1;	 Catch:{ Exception -> 0x0105 }
        r1 = r6.soundPool;	 Catch:{ Exception -> 0x0105 }
        r5 = NUM; // 0x7f0CLASSNAMEd float:1.8609218E38 double:1.053097405E-314;
        r1 = r1.load(r6, r5, r4);	 Catch:{ Exception -> 0x0105 }
        r6.spEndId = r1;	 Catch:{ Exception -> 0x0105 }
        r1 = r6.soundPool;	 Catch:{ Exception -> 0x0105 }
        r5 = NUM; // 0x7f0CLASSNAMEb float:1.8609214E38 double:1.053097404E-314;
        r1 = r1.load(r6, r5, r4);	 Catch:{ Exception -> 0x0105 }
        r6.spBusyId = r1;	 Catch:{ Exception -> 0x0105 }
        r1 = new android.content.ComponentName;	 Catch:{ Exception -> 0x0105 }
        r5 = org.telegram.messenger.voip.VoIPMediaButtonReceiver.class;
        r1.<init>(r6, r5);	 Catch:{ Exception -> 0x0105 }
        r0.registerMediaButtonEventReceiver(r1);	 Catch:{ Exception -> 0x0105 }
        r0 = USE_CONNECTION_SERVICE;	 Catch:{ Exception -> 0x0105 }
        if (r0 != 0) goto L_0x0112;
    L_0x00d7:
        r0 = r6.btAdapter;	 Catch:{ Exception -> 0x0105 }
        if (r0 == 0) goto L_0x0112;
    L_0x00db:
        r0 = r6.btAdapter;	 Catch:{ Exception -> 0x0105 }
        r0 = r0.isEnabled();	 Catch:{ Exception -> 0x0105 }
        if (r0 == 0) goto L_0x0112;
    L_0x00e3:
        r0 = r6.btAdapter;	 Catch:{ Exception -> 0x0105 }
        r0 = r0.getProfileConnectionState(r4);	 Catch:{ Exception -> 0x0105 }
        if (r0 != r3) goto L_0x00ec;
    L_0x00eb:
        r2 = 1;
    L_0x00ec:
        r6.updateBluetoothHeadsetState(r2);	 Catch:{ Exception -> 0x0105 }
        r0 = r6.stateListeners;	 Catch:{ Exception -> 0x0105 }
        r0 = r0.iterator();	 Catch:{ Exception -> 0x0105 }
    L_0x00f5:
        r1 = r0.hasNext();	 Catch:{ Exception -> 0x0105 }
        if (r1 == 0) goto L_0x0112;
    L_0x00fb:
        r1 = r0.next();	 Catch:{ Exception -> 0x0105 }
        r1 = (org.telegram.messenger.voip.VoIPBaseService.StateListener) r1;	 Catch:{ Exception -> 0x0105 }
        r1.onAudioSettingsChanged();	 Catch:{ Exception -> 0x0105 }
        goto L_0x00f5;
    L_0x0105:
        r0 = move-exception;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x010f;
    L_0x010a:
        r1 = "error initializing voip controller";
        org.telegram.messenger.FileLog.e(r1, r0);
    L_0x010f:
        r6.callFailed();
    L_0x0112:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPBaseService.onCreate():void");
    }

    /* Access modifiers changed, original: protected */
    public void dispatchStateChanged(int i) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("== Call ");
            stringBuilder.append(getCallID());
            stringBuilder.append(" state changed to ");
            stringBuilder.append(i);
            stringBuilder.append(" ==");
            FileLog.d(stringBuilder.toString());
        }
        this.currentState = i;
        if (USE_CONNECTION_SERVICE && i == 3) {
            CallConnection callConnection = this.systemCallConnection;
            if (callConnection != null) {
                callConnection.setActive();
            }
        }
        for (int i2 = 0; i2 < this.stateListeners.size(); i2++) {
            ((StateListener) this.stateListeners.get(i2)).onStateChanged(i);
        }
    }

    /* Access modifiers changed, original: protected */
    public void updateStats() {
        StatsController instance;
        NetworkInfo networkInfo;
        this.controller.getStats(this.stats);
        Stats stats = this.stats;
        long j = stats.bytesSentWifi;
        Stats stats2 = this.prevStats;
        j -= stats2.bytesSentWifi;
        long j2 = stats.bytesRecvdWifi - stats2.bytesRecvdWifi;
        long j3 = stats.bytesSentMobile - stats2.bytesSentMobile;
        long j4 = stats.bytesRecvdMobile - stats2.bytesRecvdMobile;
        this.stats = stats2;
        this.prevStats = stats;
        if (j > 0) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(1, 0, j);
        }
        if (j2 > 0) {
            StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(1, 0, j2);
        }
        int i = 2;
        if (j3 > 0) {
            instance = StatsController.getInstance(this.currentAccount);
            networkInfo = this.lastNetInfo;
            int i2 = (networkInfo == null || !networkInfo.isRoaming()) ? 0 : 2;
            instance.incrementSentBytesCount(i2, 0, j3);
        }
        if (j4 > 0) {
            instance = StatsController.getInstance(this.currentAccount);
            networkInfo = this.lastNetInfo;
            if (networkInfo == null || !networkInfo.isRoaming()) {
                i = 0;
            }
            instance.incrementReceivedBytesCount(i, 0, j4);
        }
    }

    /* Access modifiers changed, original: protected */
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
                    if (this.bluetoothScoActive) {
                        audioManager.setBluetoothScoOn(true);
                        audioManager.setSpeakerphoneOn(false);
                    } else {
                        this.needSwitchToBluetoothAfterScoActivates = true;
                        try {
                            audioManager.startBluetoothSco();
                        } catch (Throwable unused) {
                        }
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
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("proximity ");
                            stringBuilder.append(z);
                            FileLog.d(stringBuilder.toString());
                        }
                        this.isProximityNear = z;
                        try {
                            if (this.isProximityNear) {
                                this.proximityWakelock.acquire();
                            } else {
                                this.proximityWakelock.release(1);
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                }
            }
        }
    }

    public boolean isBluetoothHeadsetConnected() {
        if (USE_CONNECTION_SERVICE) {
            CallConnection callConnection = this.systemCallConnection;
            if (!(callConnection == null || callConnection.getCallAudioState() == null)) {
                return (this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 2) != 0;
            }
        }
        return this.isBtHeadsetConnected;
    }

    public void onAudioFocusChange(int i) {
        if (i == 1) {
            this.haveAudioFocus = true;
        } else {
            this.haveAudioFocus = false;
        }
    }

    /* Access modifiers changed, original: protected */
    public void updateBluetoothHeadsetState(boolean z) {
        if (z != this.isBtHeadsetConnected) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("updateBluetoothHeadsetState: ");
                stringBuilder.append(z);
                FileLog.d(stringBuilder.toString());
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
            Iterator it = this.stateListeners.iterator();
            while (it.hasNext()) {
                ((StateListener) it.next()).onAudioSettingsChanged();
            }
        }
    }

    public int getLastError() {
        return this.lastError;
    }

    public int getCallState() {
        return this.currentState;
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0039  */
    public void updateNetworkType() {
        /*
        r3 = this;
        r0 = "connectivity";
        r0 = r3.getSystemService(r0);
        r0 = (android.net.ConnectivityManager) r0;
        r0 = r0.getActiveNetworkInfo();
        r3.lastNetInfo = r0;
        r1 = 1;
        if (r0 == 0) goto L_0x0034;
    L_0x0011:
        r2 = r0.getType();
        if (r2 == 0) goto L_0x0022;
    L_0x0017:
        if (r2 == r1) goto L_0x0020;
    L_0x0019:
        r0 = 9;
        if (r2 == r0) goto L_0x001e;
    L_0x001d:
        goto L_0x0034;
    L_0x001e:
        r1 = 7;
        goto L_0x0035;
    L_0x0020:
        r1 = 6;
        goto L_0x0035;
    L_0x0022:
        r0 = r0.getSubtype();
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
        };
    L_0x0029:
        r1 = 11;
        goto L_0x0035;
    L_0x002c:
        r1 = 5;
        goto L_0x0035;
    L_0x002e:
        r1 = 4;
        goto L_0x0035;
    L_0x0030:
        r1 = 3;
        goto L_0x0035;
    L_0x0032:
        r1 = 2;
        goto L_0x0035;
    L_0x0034:
        r1 = 0;
    L_0x0035:
        r0 = r3.controller;
        if (r0 == 0) goto L_0x003c;
    L_0x0039:
        r0.setNetworkType(r1);
    L_0x003c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPBaseService.updateNetworkType():void");
    }

    /* Access modifiers changed, original: protected */
    public void callFailed() {
        VoIPController voIPController = this.controller;
        int lastError = (voIPController == null || !this.controllerStarted) ? 0 : voIPController.getLastError();
        callFailed(lastError);
    }

    /* Access modifiers changed, original: protected */
    public Bitmap getRoundAvatarBitmap(TLObject tLObject) {
        boolean z = tLObject instanceof User;
        String str = "50_50";
        Bitmap bitmap = null;
        BitmapDrawable imageFromMemory;
        Options options;
        if (z) {
            User user = (User) tLObject;
            UserProfilePhoto userProfilePhoto = user.photo;
            if (!(userProfilePhoto == null || userProfilePhoto.photo_small == null)) {
                Bitmap copy;
                imageFromMemory = ImageLoader.getInstance().getImageFromMemory(user.photo.photo_small, null, str);
                if (imageFromMemory != null) {
                    copy = imageFromMemory.getBitmap().copy(Config.ARGB_8888, true);
                } else {
                    try {
                        options = new Options();
                        options.inMutable = true;
                        copy = BitmapFactory.decodeFile(FileLoader.getPathToAttach(user.photo.photo_small, true).toString(), options);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                bitmap = copy;
            }
        } else {
            Chat chat = (Chat) tLObject;
            ChatPhoto chatPhoto = chat.photo;
            if (!(chatPhoto == null || chatPhoto.photo_small == null)) {
                imageFromMemory = ImageLoader.getInstance().getImageFromMemory(chat.photo.photo_small, null, str);
                if (imageFromMemory != null) {
                    bitmap = imageFromMemory.getBitmap().copy(Config.ARGB_8888, true);
                } else {
                    try {
                        options = new Options();
                        options.inMutable = true;
                        bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(chat.photo.photo_small, true).toString(), options);
                    } catch (Throwable th2) {
                        FileLog.e(th2);
                    }
                }
            }
        }
        if (bitmap == null) {
            Drawable avatarDrawable;
            Theme.createDialogsResources(this);
            if (z) {
                avatarDrawable = new AvatarDrawable((User) tLObject);
            } else {
                avatarDrawable = new AvatarDrawable((Chat) tLObject);
            }
            bitmap = Bitmap.createBitmap(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), Config.ARGB_8888);
            avatarDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            avatarDrawable.draw(new Canvas(bitmap));
        }
        Canvas canvas = new Canvas(bitmap);
        Path path = new Path();
        path.addCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2), (float) (bitmap.getWidth() / 2), Direction.CW);
        path.toggleInverseFillType();
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        canvas.drawPath(path, paint);
        return bitmap;
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00dc  */
    public void showIncomingNotification(java.lang.String r17, java.lang.CharSequence r18, org.telegram.tgnet.TLObject r19, java.util.List<org.telegram.tgnet.TLRPC.User> r20, int r21, java.lang.Class<? extends android.app.Activity> r22) {
        /*
        r16 = this;
        r0 = r16;
        r1 = r17;
        r2 = r18;
        r3 = r19;
        r4 = new android.content.Intent;
        r5 = r22;
        r4.<init>(r0, r5);
        r5 = NUM; // 0x30000000 float:4.656613E-10 double:3.97874211E-315;
        r4.addFlags(r5);
        r5 = new android.app.Notification$Builder;
        r5.<init>(r0);
        r6 = NUM; // 0x7f0d0b43 float:1.8747962E38 double:1.053131202E-314;
        r7 = "VoipInCallBranding";
        r8 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r5 = r5.setContentTitle(r8);
        r5 = r5.setContentText(r1);
        r8 = NUM; // 0x7var_c5 float:1.7945497E38 double:1.052935727E-314;
        r5 = r5.setSmallIcon(r8);
        r5 = r5.setSubText(r2);
        r8 = 0;
        r9 = android.app.PendingIntent.getActivity(r0, r8, r4, r8);
        r5 = r5.setContentIntent(r9);
        r9 = "content://org.telegram.messenger.call_sound_provider/start_ringing";
        r9 = android.net.Uri.parse(r9);
        r10 = android.os.Build.VERSION.SDK_INT;
        r14 = 26;
        if (r10 < r14) goto L_0x0128;
    L_0x004a:
        r10 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings();
        r14 = "calls_notification_channel";
        r15 = r10.getInt(r14, r8);
        r6 = "notification";
        r6 = r0.getSystemService(r6);
        r6 = (android.app.NotificationManager) r6;
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r11 = "incoming_calls";
        r13.append(r11);
        r13.append(r15);
        r11 = r13.toString();
        r11 = r6.getNotificationChannel(r11);
        if (r11 == 0) goto L_0x007a;
    L_0x0073:
        r11 = r11.getId();
        r6.deleteNotificationChannel(r11);
    L_0x007a:
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r13 = "incoming_calls2";
        r11.append(r13);
        r11.append(r15);
        r11 = r11.toString();
        r11 = r6.getNotificationChannel(r11);
        if (r11 == 0) goto L_0x00d9;
    L_0x0091:
        r12 = r11.getImportance();
        r8 = 4;
        if (r12 < r8) goto L_0x00b1;
    L_0x0098:
        r8 = r11.getSound();
        r8 = r9.equals(r8);
        if (r8 == 0) goto L_0x00b1;
    L_0x00a2:
        r8 = r11.getVibrationPattern();
        if (r8 != 0) goto L_0x00b1;
    L_0x00a8:
        r8 = r11.shouldVibrate();
        if (r8 == 0) goto L_0x00af;
    L_0x00ae:
        goto L_0x00b1;
    L_0x00af:
        r8 = 0;
        goto L_0x00da;
    L_0x00b1:
        r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r8 == 0) goto L_0x00ba;
    L_0x00b5:
        r8 = "User messed up the notification channel; deleting it and creating a proper one";
        org.telegram.messenger.FileLog.d(r8);
    L_0x00ba:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r8.append(r13);
        r8.append(r15);
        r8 = r8.toString();
        r6.deleteNotificationChannel(r8);
        r15 = r15 + 1;
        r8 = r10.edit();
        r8 = r8.putInt(r14, r15);
        r8.commit();
    L_0x00d9:
        r8 = 1;
    L_0x00da:
        if (r8 == 0) goto L_0x0115;
    L_0x00dc:
        r8 = new android.media.AudioAttributes$Builder;
        r8.<init>();
        r10 = 6;
        r8 = r8.setUsage(r10);
        r8 = r8.build();
        r10 = new android.app.NotificationChannel;
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r11.append(r13);
        r11.append(r15);
        r11 = r11.toString();
        r12 = NUM; // 0x7f0d0525 float:1.8744786E38 double:1.053130428E-314;
        r14 = "IncomingCalls";
        r12 = org.telegram.messenger.LocaleController.getString(r14, r12);
        r14 = 4;
        r10.<init>(r11, r12, r14);
        r10.setSound(r9, r8);
        r8 = 0;
        r10.enableVibration(r8);
        r10.enableLights(r8);
        r6.createNotificationChannel(r10);
    L_0x0115:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r13);
        r6.append(r15);
        r6 = r6.toString();
        r5.setChannelId(r6);
        goto L_0x0130;
    L_0x0128:
        r6 = 21;
        if (r10 < r6) goto L_0x0130;
    L_0x012c:
        r6 = 2;
        r5.setSound(r9, r6);
    L_0x0130:
        r6 = new android.content.Intent;
        r8 = org.telegram.messenger.voip.VoIPActionsReceiver.class;
        r6.<init>(r0, r8);
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = r16.getPackageName();
        r8.append(r9);
        r9 = ".DECLINE_CALL";
        r8.append(r9);
        r8 = r8.toString();
        r6.setAction(r8);
        r8 = r16.getCallID();
        r10 = "call_id";
        r6.putExtra(r10, r8);
        r8 = NUM; // 0x7f0d0b3c float:1.8747948E38 double:1.0531311985E-314;
        r9 = "VoipDeclineCall";
        r11 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r12 = android.os.Build.VERSION.SDK_INT;
        r13 = 24;
        if (r12 < r13) goto L_0x017e;
    L_0x0167:
        r12 = new android.text.SpannableString;
        r12.<init>(r11);
        r11 = new android.text.style.ForegroundColorSpan;
        r14 = -769226; // 0xffffffffffvar_ float:NaN double:NaN;
        r11.<init>(r14);
        r14 = r12.length();
        r15 = 0;
        r12.setSpan(r11, r15, r14, r15);
        r11 = r12;
        goto L_0x017f;
    L_0x017e:
        r15 = 0;
    L_0x017f:
        r12 = NUM; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r6 = android.app.PendingIntent.getBroadcast(r0, r15, r6, r12);
        r14 = NUM; // 0x7var_ba float:1.7944955E38 double:1.052935595E-314;
        r5.addAction(r14, r11, r6);
        r11 = new android.content.Intent;
        r14 = org.telegram.messenger.voip.VoIPActionsReceiver.class;
        r11.<init>(r0, r14);
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = r16.getPackageName();
        r14.append(r15);
        r15 = ".ANSWER_CALL";
        r14.append(r15);
        r14 = r14.toString();
        r11.setAction(r14);
        r14 = r16.getCallID();
        r11.putExtra(r10, r14);
        r10 = NUM; // 0x7f0d0b34 float:1.8747932E38 double:1.0531311945E-314;
        r14 = "VoipAnswerCall";
        r10 = org.telegram.messenger.LocaleController.getString(r14, r10);
        r14 = android.os.Build.VERSION.SDK_INT;
        if (r14 < r13) goto L_0x01d5;
    L_0x01be:
        r13 = new android.text.SpannableString;
        r13.<init>(r10);
        r10 = new android.text.style.ForegroundColorSpan;
        r14 = -16733696; // 0xfffffffffvar_aa00 float:-1.7102387E38 double:NaN;
        r10.<init>(r14);
        r14 = r13.length();
        r15 = 0;
        r13.setSpan(r10, r15, r14, r15);
        r10 = r13;
        goto L_0x01d6;
    L_0x01d5:
        r15 = 0;
    L_0x01d6:
        r11 = android.app.PendingIntent.getBroadcast(r0, r15, r11, r12);
        r12 = NUM; // 0x7var_b9 float:1.7944953E38 double:1.0529355944E-314;
        r5.addAction(r12, r10, r11);
        r10 = 2;
        r5.setPriority(r10);
        r10 = android.os.Build.VERSION.SDK_INT;
        r12 = 17;
        if (r10 < r12) goto L_0x01ed;
    L_0x01ea:
        r5.setShowWhen(r15);
    L_0x01ed:
        r10 = android.os.Build.VERSION.SDK_INT;
        r12 = 21;
        if (r10 < r12) goto L_0x0230;
    L_0x01f3:
        r10 = -13851168; // 0xffffffffff2ca5e0 float:-2.2948849E38 double:NaN;
        r5.setColor(r10);
        r10 = new long[r15];
        r5.setVibrate(r10);
        r10 = "call";
        r5.setCategory(r10);
        r4 = android.app.PendingIntent.getActivity(r0, r15, r4, r15);
        r10 = 1;
        r5.setFullScreenIntent(r4, r10);
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.User;
        if (r4 == 0) goto L_0x0230;
    L_0x020f:
        r4 = r3;
        r4 = (org.telegram.tgnet.TLRPC.User) r4;
        r10 = r4.phone;
        r10 = android.text.TextUtils.isEmpty(r10);
        if (r10 != 0) goto L_0x0230;
    L_0x021a:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r12 = "tel:";
        r10.append(r12);
        r4 = r4.phone;
        r10.append(r4);
        r4 = r10.toString();
        r5.addPerson(r4);
    L_0x0230:
        r4 = r5.getNotification();
        r10 = android.os.Build.VERSION.SDK_INT;
        r12 = 21;
        if (r10 < r12) goto L_0x0303;
    L_0x023a:
        r10 = new android.widget.RemoteViews;
        r12 = r16.getPackageName();
        r13 = org.telegram.messenger.LocaleController.isRTL;
        if (r13 == 0) goto L_0x0248;
    L_0x0244:
        r13 = NUM; // 0x7f0a0001 float:1.8343348E38 double:1.053032641E-314;
        goto L_0x024a;
    L_0x0248:
        r13 = NUM; // 0x7f0a0000 float:1.8343346E38 double:1.0530326403E-314;
    L_0x024a:
        r10.<init>(r12, r13);
        r12 = NUM; // 0x7var_ float:1.807776E38 double:1.0529679454E-314;
        r10.setTextViewText(r12, r1);
        r1 = android.text.TextUtils.isEmpty(r18);
        r12 = NUM; // 0x7var_b9 float:1.8077876E38 double:1.0529679735E-314;
        r13 = NUM; // 0x7var_a9 float:1.8077844E38 double:1.0529679656E-314;
        if (r1 == 0) goto L_0x029a;
    L_0x025f:
        r1 = 8;
        r10.setViewVisibility(r13, r1);
        r1 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();
        r2 = 1;
        if (r1 <= r2) goto L_0x028f;
    L_0x026b:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getCurrentUser();
        r7 = NUM; // 0x7f0d0b44 float:1.8747964E38 double:1.0531312024E-314;
        r2 = new java.lang.Object[r2];
        r13 = r1.first_name;
        r1 = r1.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r13, r1);
        r13 = 0;
        r2[r13] = r1;
        r1 = "VoipInCallBrandingWithName";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r2);
        r10.setTextViewText(r12, r1);
        goto L_0x02cd;
    L_0x028f:
        r1 = NUM; // 0x7f0d0b43 float:1.8747962E38 double:1.053131202E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r7, r1);
        r10.setTextViewText(r12, r1);
        goto L_0x02cd;
    L_0x029a:
        r1 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();
        r7 = 1;
        if (r1 <= r7) goto L_0x02c5;
    L_0x02a1:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getCurrentUser();
        r14 = NUM; // 0x7f0d0b35 float:1.8747934E38 double:1.053131195E-314;
        r7 = new java.lang.Object[r7];
        r15 = r1.first_name;
        r1 = r1.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r15, r1);
        r15 = 0;
        r7[r15] = r1;
        r1 = "VoipAnsweringAsAccount";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r14, r7);
        r10.setTextViewText(r13, r1);
        goto L_0x02ca;
    L_0x02c5:
        r1 = 8;
        r10.setViewVisibility(r13, r1);
    L_0x02ca:
        r10.setTextViewText(r12, r2);
    L_0x02cd:
        r1 = r0.getRoundAvatarBitmap(r3);
        r2 = NUM; // 0x7var_ float:1.80776E38 double:1.0529679063E-314;
        r3 = NUM; // 0x7f0d0b34 float:1.8747932E38 double:1.0531311945E-314;
        r7 = "VoipAnswerCall";
        r3 = org.telegram.messenger.LocaleController.getString(r7, r3);
        r10.setTextViewText(r2, r3);
        r2 = NUM; // 0x7var_c float:1.8077655E38 double:1.0529679197E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r10.setTextViewText(r2, r3);
        r2 = NUM; // 0x7var_ float:1.8077799E38 double:1.0529679547E-314;
        r10.setImageViewBitmap(r2, r1);
        r2 = NUM; // 0x7var_ float:1.8077598E38 double:1.052967906E-314;
        r10.setOnClickPendingIntent(r2, r11);
        r2 = NUM; // 0x7var_b float:1.8077653E38 double:1.052967919E-314;
        r10.setOnClickPendingIntent(r2, r6);
        r5.setLargeIcon(r1);
        r4.bigContentView = r10;
        r4.headsUpContentView = r10;
    L_0x0303:
        r1 = 202; // 0xca float:2.83E-43 double:1.0E-321;
        r0.startForeground(r1, r4);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPBaseService.showIncomingNotification(java.lang.String, java.lang.CharSequence, org.telegram.tgnet.TLObject, java.util.List, int, java.lang.Class):void");
    }

    /* Access modifiers changed, original: protected */
    public void callFailed(int i) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Call ");
            stringBuilder.append(getCallID());
            stringBuilder.append(" failed with error code ");
            stringBuilder.append(i);
            throw new Exception(stringBuilder.toString());
        } catch (Exception e) {
            FileLog.e(e);
            this.lastError = i;
            dispatchStateChanged(4);
            if (i != -3) {
                SoundPool soundPool = this.soundPool;
                if (soundPool != null) {
                    this.playingSound = true;
                    soundPool.play(this.spFailedID, 1.0f, 1.0f, 0, 0, 1.0f);
                    AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1000);
                }
            }
            if (USE_CONNECTION_SERVICE) {
                CallConnection callConnection = this.systemCallConnection;
                if (callConnection != null) {
                    callConnection.setDisconnected(new DisconnectCause(1));
                    this.systemCallConnection.destroy();
                    this.systemCallConnection = null;
                }
            }
            stopSelf();
        }
    }

    /* Access modifiers changed, original: 0000 */
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
        int i2;
        if (i == 3) {
            Runnable runnable = this.connectingSoundRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.connectingSoundRunnable = null;
            }
            i2 = this.spPlayID;
            if (i2 != 0) {
                this.soundPool.stop(i2);
                this.spPlayID = 0;
            }
            if (!this.wasEstablished) {
                this.wasEstablished = true;
                if (!this.isProximityNear) {
                    Vibrator vibrator = (Vibrator) getSystemService("vibrator");
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(100);
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
            i2 = this.spPlayID;
            if (i2 != 0) {
                this.soundPool.stop(i2);
            }
            this.spPlayID = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        }
        dispatchStateChanged(i);
    }

    public void onSignalBarCountChanged(int i) {
        this.signalBarCount = i;
        for (int i2 = 0; i2 < this.stateListeners.size(); i2++) {
            ((StateListener) this.stateListeners.get(i2)).onSignalBarsCountChanged(i);
        }
    }

    /* Access modifiers changed, original: protected */
    public void callEnded() {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Call ");
            stringBuilder.append(getCallID());
            stringBuilder.append(" ended");
            FileLog.d(stringBuilder.toString());
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

    /* Access modifiers changed, original: protected */
    public void endConnectionServiceCall(long j) {
        if (USE_CONNECTION_SERVICE) {
            AnonymousClass8 anonymousClass8 = new Runnable() {
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
                            i2 = 4;
                            if (i == 3) {
                                if (!voIPBaseService.isOutgoing) {
                                    i2 = 5;
                                }
                                callConnection.setDisconnected(new DisconnectCause(i2));
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
                AndroidUtilities.runOnUIThread(anonymousClass8, j);
            } else {
                anonymousClass8.run();
            }
        }
    }

    public boolean isOutgoing() {
        return this.isOutgoing;
    }

    public void handleNotificationAction(Intent intent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPackageName());
        stringBuilder.append(".END_CALL");
        if (stringBuilder.toString().equals(intent.getAction())) {
            stopForeground(true);
            hangUp();
            return;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(getPackageName());
        stringBuilder.append(".DECLINE_CALL");
        if (stringBuilder.toString().equals(intent.getAction())) {
            stopForeground(true);
            declineIncomingCall(4, null);
            return;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(getPackageName());
        stringBuilder.append(".ANSWER_CALL");
        if (stringBuilder.toString().equals(intent.getAction())) {
            acceptIncomingCallFromNotification();
        }
    }

    private void acceptIncomingCallFromNotification() {
        showNotification();
        if (VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            acceptIncomingCall();
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, getUIActivityClass()).addFlags(NUM), 0).send();
            } catch (Exception e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting incall activity", e);
                }
            }
            return;
        }
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPPermissionActivity.class).addFlags(NUM), 0).send();
        } catch (Exception e2) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error starting permission activity", e2);
            }
        }
    }

    public void updateOutputGainControlState() {
        if (this.controller != null && this.controllerStarted) {
            boolean z = false;
            if (USE_CONNECTION_SERVICE) {
                if (this.systemCallConnection.getCallAudioState().getRoute() == 1) {
                    z = true;
                }
                this.controller.setAudioOutputGainControlEnabled(z);
                this.controller.setEchoCancellationStrength(z ^ 1);
                return;
            }
            int i;
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            VoIPController voIPController = this.controller;
            boolean z2 = (!hasEarpiece() || audioManager.isSpeakerphoneOn() || audioManager.isBluetoothScoOn() || this.isHeadsetPlugged) ? false : true;
            voIPController.setAudioOutputGainControlEnabled(z2);
            voIPController = this.controller;
            if (!this.isHeadsetPlugged && (!hasEarpiece() || audioManager.isSpeakerphoneOn() || audioManager.isBluetoothScoOn() || this.isHeadsetPlugged)) {
                i = 1;
            }
            voIPController.setEchoCancellationStrength(i);
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

    /* Access modifiers changed, original: protected */
    public boolean isFinished() {
        int i = this.currentState;
        return i == 11 || i == 4;
    }

    /* Access modifiers changed, original: protected */
    @TargetApi(26)
    public PhoneAccountHandle addAccountToTelecomManager() {
        TelecomManager telecomManager = (TelecomManager) getSystemService("telecom");
        User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        ComponentName componentName = new ComponentName(this, TelegramConnectionService.class);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(currentUser.id);
        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(componentName, stringBuilder.toString());
        telecomManager.registerPhoneAccount(new PhoneAccount.Builder(phoneAccountHandle, ContactsController.formatName(currentUser.first_name, currentUser.last_name)).setCapabilities(2048).setIcon(Icon.createWithResource(this, NUM)).setHighlightColor(-13851168).addSupportedUriScheme("sip").build());
        return phoneAccountHandle;
    }

    /* JADX WARNING: Missing block: B:20:0x0062, code skipped:
            if (org.telegram.messenger.MessagesController.getGlobalMainSettings().getBoolean("dbg_force_connection_service", false) == false) goto L_0x0065;
     */
    private static boolean isDeviceCompatibleWithConnectionServiceAPI() {
        /*
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 0;
        r2 = 26;
        if (r0 >= r2) goto L_0x0008;
    L_0x0007:
        return r1;
    L_0x0008:
        r0 = android.os.Build.PRODUCT;
        r2 = "angler";
        r0 = r2.equals(r0);
        if (r0 != 0) goto L_0x0064;
    L_0x0012:
        r0 = android.os.Build.PRODUCT;
        r2 = "bullhead";
        r0 = r2.equals(r0);
        if (r0 != 0) goto L_0x0064;
    L_0x001c:
        r0 = android.os.Build.PRODUCT;
        r2 = "sailfish";
        r0 = r2.equals(r0);
        if (r0 != 0) goto L_0x0064;
    L_0x0026:
        r0 = android.os.Build.PRODUCT;
        r2 = "marlin";
        r0 = r2.equals(r0);
        if (r0 != 0) goto L_0x0064;
    L_0x0030:
        r0 = android.os.Build.PRODUCT;
        r2 = "walleye";
        r0 = r2.equals(r0);
        if (r0 != 0) goto L_0x0064;
    L_0x003a:
        r0 = android.os.Build.PRODUCT;
        r2 = "taimen";
        r0 = r2.equals(r0);
        if (r0 != 0) goto L_0x0064;
    L_0x0044:
        r0 = android.os.Build.PRODUCT;
        r2 = "blueline";
        r0 = r2.equals(r0);
        if (r0 != 0) goto L_0x0064;
    L_0x004e:
        r0 = android.os.Build.PRODUCT;
        r2 = "crosshatch";
        r0 = r2.equals(r0);
        if (r0 != 0) goto L_0x0064;
    L_0x0058:
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r2 = "dbg_force_connection_service";
        r0 = r0.getBoolean(r2, r1);
        if (r0 == 0) goto L_0x0065;
    L_0x0064:
        r1 = 1;
    L_0x0065:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPBaseService.isDeviceCompatibleWithConnectionServiceAPI():boolean");
    }
}
