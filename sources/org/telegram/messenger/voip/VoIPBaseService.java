package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.RemoteViews;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
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
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.voip.VoIPController.ConnectionStateListener;
import org.telegram.messenger.voip.VoIPController.Stats;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.VoIPPermissionActivity;

public abstract class VoIPBaseService extends Service implements SensorEventListener, OnAudioFocusChangeListener, NotificationCenterDelegate, ConnectionStateListener {
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
    protected static final boolean USE_CONNECTION_SERVICE = false;
    protected static VoIPBaseService sharedInstance;
    protected Runnable afterSoundRunnable = new C06751();
    protected boolean audioConfigured;
    protected int audioRouteToSet = 2;
    protected BluetoothAdapter btAdapter;
    protected int callDiscardReason;
    protected VoIPController controller;
    protected boolean controllerStarted;
    protected WakeLock cpuWakelock;
    protected int currentAccount = -1;
    protected int currentState = 0;
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
    protected Notification ongoingCallNotification;
    protected boolean playingSound;
    protected Stats prevStats = new Stats();
    protected WakeLock proximityWakelock;
    protected BroadcastReceiver receiver = new C06762();
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

    /* renamed from: org.telegram.messenger.voip.VoIPBaseService$1 */
    class C06751 implements Runnable {
        C06751() {
        }

        public void run() {
            VoIPBaseService.this.soundPool.release();
            if (VoIPBaseService.this.isBtHeadsetConnected) {
                ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).stopBluetoothSco();
            }
            ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).setSpeakerphoneOn(false);
        }
    }

    /* renamed from: org.telegram.messenger.voip.VoIPBaseService$2 */
    class C06762 extends BroadcastReceiver {
        C06762() {
        }

        public void onReceive(Context context, Intent intent) {
            boolean z = true;
            if (VoIPBaseService.ACTION_HEADSET_PLUG.equals(intent.getAction()) != null) {
                context = VoIPBaseService.this;
                if (intent.getIntExtra("state", 0) != 1) {
                    z = false;
                }
                context.isHeadsetPlugged = z;
                if (!(VoIPBaseService.this.isHeadsetPlugged == null || VoIPBaseService.this.proximityWakelock == null || VoIPBaseService.this.proximityWakelock.isHeld() == null)) {
                    VoIPBaseService.this.proximityWakelock.release();
                }
                VoIPBaseService.this.isProximityNear = false;
                VoIPBaseService.this.updateOutputGainControlState();
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction()) != null) {
                VoIPBaseService.this.updateNetworkType();
            } else if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction()) != null) {
                context = VoIPBaseService.this;
                if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) != 2) {
                    z = false;
                }
                context.updateBluetoothHeadsetState(z);
            } else if ("android.media.ACTION_SCO_AUDIO_STATE_UPDATED".equals(intent.getAction()) != null) {
                context = VoIPBaseService.this.stateListeners.iterator();
                while (context.hasNext() != null) {
                    ((StateListener) context.next()).onAudioSettingsChanged();
                }
            } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction()) != null) {
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(intent.getStringExtra("state")) != null) {
                    VoIPBaseService.this.hangUp();
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.voip.VoIPBaseService$3 */
    class C06773 implements OnClickListener {
        C06773() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            AudioManager audioManager = (AudioManager) VoIPBaseService.this.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (VoIPBaseService.getSharedInstance() != null) {
                if (!VoIPBaseService.this.audioConfigured) {
                    switch (i) {
                        case 0:
                            VoIPBaseService.this.audioRouteToSet = 2;
                            break;
                        case 1:
                            VoIPBaseService.this.audioRouteToSet = 0;
                            break;
                        case 2:
                            VoIPBaseService.this.audioRouteToSet = 1;
                            break;
                        default:
                            break;
                    }
                }
                switch (i) {
                    case 0:
                        audioManager.setBluetoothScoOn(true);
                        audioManager.setSpeakerphoneOn(false);
                        break;
                    case 1:
                        audioManager.setBluetoothScoOn(false);
                        audioManager.setSpeakerphoneOn(false);
                        break;
                    case 2:
                        audioManager.setBluetoothScoOn(false);
                        audioManager.setSpeakerphoneOn(true);
                        break;
                    default:
                        break;
                }
                VoIPBaseService.this.updateOutputGainControlState();
                dialogInterface = VoIPBaseService.this.stateListeners.iterator();
                while (dialogInterface.hasNext() != 0) {
                    ((StateListener) dialogInterface.next()).onAudioSettingsChanged();
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.voip.VoIPBaseService$4 */
    class C06784 implements OnPreparedListener {
        C06784() {
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            VoIPBaseService.this.ringtonePlayer.start();
        }
    }

    /* renamed from: org.telegram.messenger.voip.VoIPBaseService$5 */
    class C06795 implements Runnable {
        C06795() {
        }

        public void run() {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndedCall, new Object[0]);
        }
    }

    /* renamed from: org.telegram.messenger.voip.VoIPBaseService$6 */
    class C06806 implements Runnable {
        C06806() {
        }

        public void run() {
            if (VoIPBaseService.this.controller != null) {
                StatsController.getInstance(VoIPBaseService.this.currentAccount).incrementTotalCallsTime(VoIPBaseService.this.getStatsNetworkType(), 5);
                AndroidUtilities.runOnUIThread(this, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            }
        }
    }

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
                FileLog.m0d(stringBuilder.toString());
            }
            callAudioState = VoIPBaseService.this.stateListeners.iterator();
            while (callAudioState.hasNext()) {
                ((StateListener) callAudioState.next()).onAudioSettingsChanged();
            }
        }

        public void onDisconnect() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("ConnectionService onDisconnect");
            }
            setDisconnected(new DisconnectCause(2));
            destroy();
            VoIPBaseService.this.systemCallConnection = null;
            VoIPBaseService.this.hangUp();
        }

        public void onAnswer() {
            VoIPBaseService.this.acceptIncomingCallFromNotification();
        }

        public void onReject() {
            VoIPBaseService.this.declineIncomingCall(1, null);
        }

        public void onShowIncomingCallUi() {
            VoIPBaseService.this.startRinging();
        }

        public void onStateChanged(int i) {
            super.onStateChanged(i);
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ConnectionService onStateChanged ");
                stringBuilder.append(i);
                FileLog.m0d(stringBuilder.toString());
            }
        }

        public void onCallEvent(String str, Bundle bundle) {
            super.onCallEvent(str, bundle);
            if (BuildVars.LOGS_ENABLED != null) {
                bundle = new StringBuilder();
                bundle.append("ConnectionService onCallEvent ");
                bundle.append(str);
                FileLog.m0d(bundle.toString());
            }
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

    protected abstract Class<? extends Activity> getUIActivityClass();

    public abstract void hangUp();

    public abstract void hangUp(Runnable runnable);

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    protected void onControllerPreRelease() {
    }

    protected abstract void showNotification();

    protected abstract void startRinging();

    protected abstract void updateServerConfig();

    public boolean hasEarpiece() {
        if (((TelephonyManager) getSystemService("phone")).getPhoneType() != 0) {
            return true;
        }
        if (this.mHasEarpiece != null) {
            return this.mHasEarpiece.booleanValue();
        }
        try {
            AudioManager audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            Method method = AudioManager.class.getMethod("getDevicesForStream", new Class[]{Integer.TYPE});
            int i = AudioManager.class.getField("DEVICE_OUT_EARPIECE").getInt(null);
            if ((((Integer) method.invoke(audioManager, new Object[]{Integer.valueOf(0)})).intValue() & i) == i) {
                this.mHasEarpiece = Boolean.TRUE;
            } else {
                this.mHasEarpiece = Boolean.FALSE;
            }
        } catch (Throwable th) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m2e("Error while checking earpiece! ", th);
            }
            this.mHasEarpiece = Boolean.TRUE;
        }
        return this.mHasEarpiece.booleanValue();
    }

    protected int getStatsNetworkType() {
        if (this.lastNetInfo == null || this.lastNetInfo.getType() != 0) {
            return 1;
        }
        return this.lastNetInfo.isRoaming() ? 2 : 0;
    }

    public void registerStateListener(StateListener stateListener) {
        this.stateListeners.add(stateListener);
        if (this.currentState != 0) {
            stateListener.onStateChanged(this.currentState);
        }
        if (this.signalBarCount != 0) {
            stateListener.onSignalBarsCountChanged(this.signalBarCount);
        }
    }

    public void unregisterStateListener(StateListener stateListener) {
        this.stateListeners.remove(stateListener);
    }

    public void setMicMute(boolean z) {
        this.micMute = z;
        if (this.controller != null) {
            this.controller.setMicMute(z);
        }
    }

    public boolean isMicMute() {
        return this.micMute;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void toggleSpeakerphoneOrShowRouteSheet(Activity activity) {
        if (isBluetoothHeadsetConnected() && hasEarpiece()) {
            Builder builder = new Builder(activity);
            r2 = new CharSequence[3];
            int i = 0;
            r2[0] = LocaleController.getString("VoipAudioRoutingBluetooth", C0446R.string.VoipAudioRoutingBluetooth);
            r2[1] = LocaleController.getString("VoipAudioRoutingEarpiece", C0446R.string.VoipAudioRoutingEarpiece);
            r2[2] = LocaleController.getString("VoipAudioRoutingSpeaker", C0446R.string.VoipAudioRoutingSpeaker);
            activity = builder.setItems(r2, new int[]{C0446R.drawable.ic_bluetooth_white_24dp, C0446R.drawable.ic_phone_in_talk_white_24dp, C0446R.drawable.ic_volume_up_white_24dp}, new C06773()).create();
            activity.setBackgroundColor(-13948117);
            activity.show();
            activity = activity.getSheetContainer();
            while (i < activity.getChildCount()) {
                ((BottomSheetCell) activity.getChildAt(i)).setTextColor(-1);
                i++;
            }
            return;
        }
        if (this.audioConfigured != null) {
            AudioManager audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (hasEarpiece()) {
                audioManager.setSpeakerphoneOn(audioManager.isSpeakerphoneOn() ^ true);
            } else {
                audioManager.setBluetoothScoOn(audioManager.isBluetoothScoOn() ^ true);
            }
            updateOutputGainControlState();
        } else {
            this.speakerphoneStateToSet ^= 1;
        }
        activity = this.stateListeners.iterator();
        while (activity.hasNext()) {
            ((StateListener) activity.next()).onAudioSettingsChanged();
        }
    }

    public boolean isSpeakerphoneOn() {
        if (!this.audioConfigured) {
            return this.speakerphoneStateToSet;
        }
        AudioManager audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        return hasEarpiece() ? audioManager.isSpeakerphoneOn() : audioManager.isBluetoothScoOn();
    }

    public int getCurrentAudioRoute() {
        if (!this.audioConfigured) {
            return this.audioRouteToSet;
        }
        AudioManager audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (audioManager.isBluetoothScoOn()) {
            return 2;
        }
        return audioManager.isSpeakerphoneOn() ? 1 : 0;
    }

    public String getDebugString() {
        return this.controller.getDebugString();
    }

    public long getCallDuration() {
        if (this.controllerStarted) {
            if (this.controller != null) {
                long callDuration = this.controller.getCallDuration();
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

    protected void showNotification(String str, FileLocation fileLocation, Class<? extends Activity> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(805306368);
        str = new Notification.Builder(this).setContentTitle(LocaleController.getString("VoipOutgoingCall", C0446R.string.VoipOutgoingCall)).setContentText(str).setSmallIcon(C0446R.drawable.notification).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        if (VERSION.SDK_INT >= 16) {
            intent = new Intent(this, VoIPActionsReceiver.class);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getPackageName());
            stringBuilder.append(".END_CALL");
            intent.setAction(stringBuilder.toString());
            str.addAction(C0446R.drawable.ic_call_end_white_24dp, LocaleController.getString("VoipEndCall", C0446R.string.VoipEndCall), PendingIntent.getBroadcast(this, 0, intent, 134217728));
            str.setPriority(2);
        }
        if (VERSION.SDK_INT >= 17) {
            str.setShowWhen(false);
        }
        if (VERSION.SDK_INT >= 21) {
            str.setColor(-13851168);
        }
        if (VERSION.SDK_INT >= 26) {
            str.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
        }
        if (fileLocation != null) {
            cls = ImageLoader.getInstance().getImageFromMemory(fileLocation, null, "50_50");
            if (cls != null) {
                str.setLargeIcon(cls.getBitmap());
            } else {
                try {
                    cls = NUM / ((float) AndroidUtilities.dp(50.0f));
                    Options options = new Options();
                    options.inSampleSize = cls < 1.0f ? 1 : (int) cls;
                    fileLocation = BitmapFactory.decodeFile(FileLoader.getPathToAttach(fileLocation, true).toString(), options);
                    if (fileLocation != null) {
                        str.setLargeIcon(fileLocation);
                    }
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
        }
        this.ongoingCallNotification = str.getNotification();
        startForeground(ID_ONGOING_CALL_NOTIFICATION, this.ongoingCallNotification);
    }

    protected void startRingtoneAndVibration(int r9) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r8 = this;
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0);
        r1 = "audio";
        r1 = r8.getSystemService(r1);
        r1 = (android.media.AudioManager) r1;
        r2 = r1.getRingerMode();
        r3 = 0;
        r4 = 1;
        if (r2 == 0) goto L_0x0018;
    L_0x0016:
        r2 = r4;
        goto L_0x0019;
    L_0x0018:
        r2 = r3;
    L_0x0019:
        r5 = android.os.Build.VERSION.SDK_INT;
        r6 = 21;
        if (r5 < r6) goto L_0x0030;
    L_0x001f:
        r5 = r8.getContentResolver();	 Catch:{ Exception -> 0x0030 }
        r6 = "zen_mode";	 Catch:{ Exception -> 0x0030 }
        r5 = android.provider.Settings.Global.getInt(r5, r6);	 Catch:{ Exception -> 0x0030 }
        if (r2 == 0) goto L_0x0030;
    L_0x002b:
        if (r5 != 0) goto L_0x002f;
    L_0x002d:
        r2 = r4;
        goto L_0x0030;
    L_0x002f:
        r2 = r3;
    L_0x0030:
        if (r2 == 0) goto L_0x0123;
    L_0x0032:
        r2 = new android.media.MediaPlayer;
        r2.<init>();
        r8.ringtonePlayer = r2;
        r2 = r8.ringtonePlayer;
        r5 = new org.telegram.messenger.voip.VoIPBaseService$4;
        r5.<init>();
        r2.setOnPreparedListener(r5);
        r2 = r8.ringtonePlayer;
        r2.setLooping(r4);
        r2 = r8.ringtonePlayer;
        r5 = 2;
        r2.setAudioStreamType(r5);
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00a0 }
        r2.<init>();	 Catch:{ Exception -> 0x00a0 }
        r6 = "custom_";	 Catch:{ Exception -> 0x00a0 }
        r2.append(r6);	 Catch:{ Exception -> 0x00a0 }
        r2.append(r9);	 Catch:{ Exception -> 0x00a0 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x00a0 }
        r2 = r0.getBoolean(r2, r3);	 Catch:{ Exception -> 0x00a0 }
        if (r2 == 0) goto L_0x0083;	 Catch:{ Exception -> 0x00a0 }
    L_0x0065:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00a0 }
        r2.<init>();	 Catch:{ Exception -> 0x00a0 }
        r6 = "ringtone_path_";	 Catch:{ Exception -> 0x00a0 }
        r2.append(r6);	 Catch:{ Exception -> 0x00a0 }
        r2.append(r9);	 Catch:{ Exception -> 0x00a0 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x00a0 }
        r6 = android.media.RingtoneManager.getDefaultUri(r4);	 Catch:{ Exception -> 0x00a0 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x00a0 }
        r2 = r0.getString(r2, r6);	 Catch:{ Exception -> 0x00a0 }
        goto L_0x0091;	 Catch:{ Exception -> 0x00a0 }
    L_0x0083:
        r2 = "CallsRingtonePath";	 Catch:{ Exception -> 0x00a0 }
        r6 = android.media.RingtoneManager.getDefaultUri(r4);	 Catch:{ Exception -> 0x00a0 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x00a0 }
        r2 = r0.getString(r2, r6);	 Catch:{ Exception -> 0x00a0 }
    L_0x0091:
        r6 = r8.ringtonePlayer;	 Catch:{ Exception -> 0x00a0 }
        r2 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x00a0 }
        r6.setDataSource(r8, r2);	 Catch:{ Exception -> 0x00a0 }
        r2 = r8.ringtonePlayer;	 Catch:{ Exception -> 0x00a0 }
        r2.prepareAsync();	 Catch:{ Exception -> 0x00a0 }
        goto L_0x00b0;
    L_0x00a0:
        r2 = move-exception;
        org.telegram.messenger.FileLog.m3e(r2);
        r2 = r8.ringtonePlayer;
        if (r2 == 0) goto L_0x00b0;
    L_0x00a8:
        r2 = r8.ringtonePlayer;
        r2.release();
        r2 = 0;
        r8.ringtonePlayer = r2;
    L_0x00b0:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r6 = "custom_";
        r2.append(r6);
        r2.append(r9);
        r2 = r2.toString();
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x00dd;
    L_0x00c7:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r6 = "calls_vibrate_";
        r2.append(r6);
        r2.append(r9);
        r9 = r2.toString();
        r9 = r0.getInt(r9, r3);
        goto L_0x00e3;
    L_0x00dd:
        r9 = "vibrate_calls";
        r9 = r0.getInt(r9, r3);
    L_0x00e3:
        r0 = 4;
        if (r9 == r5) goto L_0x00f4;
    L_0x00e6:
        if (r9 == r0) goto L_0x00f4;
    L_0x00e8:
        r2 = r1.getRingerMode();
        if (r2 == r4) goto L_0x00fc;
    L_0x00ee:
        r2 = r1.getRingerMode();
        if (r2 == r5) goto L_0x00fc;
    L_0x00f4:
        if (r9 != r0) goto L_0x0123;
    L_0x00f6:
        r0 = r1.getRingerMode();
        if (r0 != r4) goto L_0x0123;
    L_0x00fc:
        r0 = "vibrator";
        r0 = r8.getSystemService(r0);
        r0 = (android.os.Vibrator) r0;
        r8.vibrator = r0;
        r0 = 700; // 0x2bc float:9.81E-43 double:3.46E-321;
        r2 = 3;
        if (r9 != r4) goto L_0x010e;
    L_0x010b:
        r0 = 350; // 0x15e float:4.9E-43 double:1.73E-321;
        goto L_0x0112;
    L_0x010e:
        if (r9 != r2) goto L_0x0112;
    L_0x0110:
        r0 = 1400; // 0x578 float:1.962E-42 double:6.917E-321;
    L_0x0112:
        r9 = r8.vibrator;
        r2 = new long[r2];
        r6 = 0;
        r2[r3] = r6;
        r2[r4] = r0;
        r0 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r2[r5] = r0;
        r9.vibrate(r2, r3);
    L_0x0123:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VoIPBaseService.startRingtoneAndVibration(int):void");
    }

    public void onDestroy() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("=============== VoIPService STOPPING ===============");
        }
        stopForeground(true);
        stopRinging();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
        SensorManager sensorManager = (SensorManager) getSystemService("sensor");
        if (sensorManager.getDefaultSensor(8) != null) {
            sensorManager.unregisterListener(this);
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
        AndroidUtilities.runOnUIThread(new C06795());
        if (this.controller != null && this.controllerStarted) {
            this.lastKnownDuration = this.controller.getCallDuration();
            updateStats();
            StatsController.getInstance(this.currentAccount).incrementTotalCallsTime(getStatsNetworkType(), ((int) (this.lastKnownDuration / 1000)) % 5);
            onControllerPreRelease();
            this.controller.release();
            this.controller = null;
        }
        this.cpuWakelock.release();
        AudioManager audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (this.isBtHeadsetConnected && !this.playingSound) {
            audioManager.stopBluetoothSco();
            audioManager.setSpeakerphoneOn(false);
        }
        try {
            audioManager.setMode(0);
        } catch (Throwable e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m2e("Error setting audio more to normal", e);
            }
        }
        audioManager.unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
        if (this.haveAudioFocus) {
            audioManager.abandonAudioFocus(this);
        }
        if (!this.playingSound) {
            this.soundPool.release();
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        VoIPHelper.lastCallTime = System.currentTimeMillis();
    }

    protected VoIPController createController() {
        return new VoIPController();
    }

    protected void initializeAccountRelatedThings() {
        updateServerConfig();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        this.controller = createController();
        this.controller.setConnectionStateListener(this);
    }

    public void onCreate() {
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("=============== VoIPService STARTING ===============");
        }
        AudioManager audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (VERSION.SDK_INT < 17 || audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") == null) {
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
            intentFilter.addAction(ACTION_HEADSET_PLUG);
            if (this.btAdapter != null) {
                intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                intentFilter.addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
            }
            intentFilter.addAction("android.intent.action.PHONE_STATE");
            registerReceiver(this.receiver, intentFilter);
            boolean z = false;
            this.soundPool = new SoundPool(1, 0, 0);
            this.spConnectingId = this.soundPool.load(this, C0446R.raw.voip_connecting, 1);
            this.spRingbackID = this.soundPool.load(this, C0446R.raw.voip_ringback, 1);
            this.spFailedID = this.soundPool.load(this, C0446R.raw.voip_failed, 1);
            this.spEndId = this.soundPool.load(this, C0446R.raw.voip_end, 1);
            this.spBusyId = this.soundPool.load(this, C0446R.raw.voip_busy, 1);
            audioManager.registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (this.btAdapter != null && this.btAdapter.isEnabled()) {
                int profileConnectionState = this.btAdapter.getProfileConnectionState(1);
                if (profileConnectionState == 2) {
                    z = true;
                }
                updateBluetoothHeadsetState(z);
                if (profileConnectionState == 2) {
                    audioManager.setBluetoothScoOn(true);
                }
                Iterator it = this.stateListeners.iterator();
                while (it.hasNext()) {
                    ((StateListener) it.next()).onAudioSettingsChanged();
                }
            }
        } catch (Throwable e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m2e("error initializing voip controller", e);
            }
            callFailed();
        }
    }

    protected void dispatchStateChanged(int i) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("== Call ");
            stringBuilder.append(getCallID());
            stringBuilder.append(" state changed to ");
            stringBuilder.append(i);
            stringBuilder.append(" ==");
            FileLog.m0d(stringBuilder.toString());
        }
        this.currentState = i;
        for (int i2 = 0; i2 < this.stateListeners.size(); i2++) {
            ((StateListener) this.stateListeners.get(i2)).onStateChanged(i);
        }
    }

    protected void updateStats() {
        this.controller.getStats(this.stats);
        long j = this.stats.bytesSentWifi - this.prevStats.bytesSentWifi;
        long j2 = this.stats.bytesRecvdWifi - this.prevStats.bytesRecvdWifi;
        long j3 = this.stats.bytesSentMobile - this.prevStats.bytesSentMobile;
        long j4 = this.stats.bytesRecvdMobile - this.prevStats.bytesRecvdMobile;
        Stats stats = this.stats;
        this.stats = this.prevStats;
        this.prevStats = stats;
        if (j > 0) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(1, 0, j);
        }
        if (j2 > 0) {
            StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(1, 0, j2);
        }
        int i = 2;
        if (j3 > 0) {
            StatsController instance = StatsController.getInstance(this.currentAccount);
            int i2 = (this.lastNetInfo == null || !this.lastNetInfo.isRoaming()) ? 0 : 2;
            instance.incrementSentBytesCount(i2, 0, j3);
        }
        if (j4 > 0) {
            StatsController instance2 = StatsController.getInstance(this.currentAccount);
            if (this.lastNetInfo == null || !this.lastNetInfo.isRoaming()) {
                i = 0;
            }
            instance2.incrementReceivedBytesCount(i, 0, j4);
        }
    }

    protected void configureDeviceForCall() {
        this.needPlayEndSound = true;
        AudioManager audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        audioManager.setMode(3);
        audioManager.requestAudioFocus(this, 0, 1);
        if (isBluetoothHeadsetConnected() && hasEarpiece()) {
            switch (this.audioRouteToSet) {
                case 0:
                    audioManager.setBluetoothScoOn(false);
                    audioManager.setSpeakerphoneOn(false);
                    break;
                case 1:
                    audioManager.setBluetoothScoOn(false);
                    audioManager.setSpeakerphoneOn(true);
                    break;
                case 2:
                    audioManager.setBluetoothScoOn(true);
                    audioManager.setSpeakerphoneOn(false);
                    break;
                default:
                    break;
            }
        } else if (isBluetoothHeadsetConnected()) {
            audioManager.setBluetoothScoOn(this.speakerphoneStateToSet);
        } else {
            audioManager.setSpeakerphoneOn(this.speakerphoneStateToSet);
        }
        updateOutputGainControlState();
        this.audioConfigured = true;
        SensorManager sensorManager = (SensorManager) getSystemService("sensor");
        Sensor defaultSensor = sensorManager.getDefaultSensor(8);
        if (defaultSensor != null) {
            try {
                this.proximityWakelock = ((PowerManager) getSystemService("power")).newWakeLock(32, "telegram-voip-prx");
                sensorManager.registerListener(this, defaultSensor, 3);
            } catch (Throwable e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m2e("Error initializing proximity sensor", e);
                }
            }
        }
    }

    @SuppressLint({"NewApi"})
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8) {
            AudioManager audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (!(this.isHeadsetPlugged || audioManager.isSpeakerphoneOn())) {
                if (!isBluetoothHeadsetConnected() || !audioManager.isBluetoothScoOn()) {
                    boolean z = false;
                    if (sensorEvent.values[0] < Math.min(sensorEvent.sensor.getMaximumRange(), 3.0f)) {
                        z = true;
                    }
                    if (z != this.isProximityNear) {
                        if (BuildVars.LOGS_ENABLED != null) {
                            sensorEvent = new StringBuilder();
                            sensorEvent.append("proximity ");
                            sensorEvent.append(z);
                            FileLog.m0d(sensorEvent.toString());
                        }
                        this.isProximityNear = z;
                        try {
                            if (this.isProximityNear != null) {
                                this.proximityWakelock.acquire();
                            } else {
                                this.proximityWakelock.release(1);
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            }
        }
    }

    public boolean isBluetoothHeadsetConnected() {
        return this.isBtHeadsetConnected;
    }

    public void onAudioFocusChange(int i) {
        if (i == 1) {
            this.haveAudioFocus = true;
        } else {
            this.haveAudioFocus = false;
        }
    }

    protected void updateBluetoothHeadsetState(boolean z) {
        if (z != this.isBtHeadsetConnected) {
            this.isBtHeadsetConnected = z;
            AudioManager audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (z) {
                audioManager.startBluetoothSco();
                audioManager.setSpeakerphoneOn(false);
                audioManager.setBluetoothScoOn(true);
            } else {
                audioManager.stopBluetoothSco();
            }
            z = this.stateListeners.iterator();
            while (z.hasNext()) {
                ((StateListener) z.next()).onAudioSettingsChanged();
            }
        }
    }

    public int getLastError() {
        return this.lastError;
    }

    public int getCallState() {
        return this.currentState;
    }

    protected void updateNetworkType() {
        int i;
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        this.lastNetInfo = activeNetworkInfo;
        if (activeNetworkInfo != null) {
            int type = activeNetworkInfo.getType();
            if (type != 9) {
                switch (type) {
                    case 0:
                        switch (activeNetworkInfo.getSubtype()) {
                            case 1:
                                i = 1;
                                break;
                            case 2:
                            case 7:
                                i = 2;
                                break;
                            case 3:
                            case 5:
                                i = 3;
                                break;
                            case 6:
                            case 8:
                            case 9:
                            case 10:
                            case 12:
                            case 15:
                                i = 4;
                                break;
                            case 13:
                                i = 5;
                                break;
                            default:
                                i = 11;
                                break;
                        }
                    case 1:
                        i = 6;
                        break;
                    default:
                        break;
                }
            }
            i = 7;
            if (this.controller != null) {
                this.controller.setNetworkType(i);
            }
        }
        i = 0;
        if (this.controller != null) {
            this.controller.setNetworkType(i);
        }
    }

    protected void callFailed() {
        int lastError = (this.controller == null || !this.controllerStarted) ? 0 : this.controller.getLastError();
        callFailed(lastError);
    }

    protected Bitmap getRoundAvatarBitmap(TLObject tLObject) {
        Bitmap decodeFile;
        Path path;
        Paint paint;
        boolean z = tLObject instanceof User;
        Bitmap bitmap = null;
        BitmapDrawable imageFromMemory;
        AvatarDrawable avatarDrawable;
        if (z) {
            User user = (User) tLObject;
            if (!(user.photo == null || user.photo.photo_small == null)) {
                imageFromMemory = ImageLoader.getInstance().getImageFromMemory(user.photo.photo_small, null, "50_50");
                if (imageFromMemory != null) {
                    bitmap = imageFromMemory.getBitmap().copy(Config.ARGB_8888, true);
                } else {
                    try {
                        Options options = new Options();
                        options.inMutable = true;
                        decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(user.photo.photo_small, true).toString(), options);
                    } catch (Throwable th) {
                        FileLog.m3e(th);
                    }
                }
            }
            if (bitmap == null) {
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
            tLObject = new Canvas(bitmap);
            path = new Path();
            path.addCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2), (float) (bitmap.getWidth() / 2), Direction.CW);
            path.toggleInverseFillType();
            paint = new Paint(1);
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            tLObject.drawPath(path, paint);
            return bitmap;
        }
        Chat chat = (Chat) tLObject;
        if (!(chat.photo == null || chat.photo.photo_small == null)) {
            imageFromMemory = ImageLoader.getInstance().getImageFromMemory(chat.photo.photo_small, null, "50_50");
            if (imageFromMemory != null) {
                bitmap = imageFromMemory.getBitmap().copy(Config.ARGB_8888, true);
            } else {
                try {
                    options = new Options();
                    options.inMutable = true;
                    decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(chat.photo.photo_small, true).toString(), options);
                } catch (Throwable th2) {
                    FileLog.m3e(th2);
                }
            }
        }
        if (bitmap == null) {
            Theme.createDialogsResources(this);
            if (z) {
                avatarDrawable = new AvatarDrawable((Chat) tLObject);
            } else {
                avatarDrawable = new AvatarDrawable((User) tLObject);
            }
            bitmap = Bitmap.createBitmap(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), Config.ARGB_8888);
            avatarDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            avatarDrawable.draw(new Canvas(bitmap));
        }
        tLObject = new Canvas(bitmap);
        path = new Path();
        path.addCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2), (float) (bitmap.getWidth() / 2), Direction.CW);
        path.toggleInverseFillType();
        paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        tLObject.drawPath(path, paint);
        return bitmap;
        bitmap = decodeFile;
        if (bitmap == null) {
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
        tLObject = new Canvas(bitmap);
        path = new Path();
        path.addCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2), (float) (bitmap.getWidth() / 2), Direction.CW);
        path.toggleInverseFillType();
        paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        tLObject.drawPath(path, paint);
        return bitmap;
    }

    protected void showIncomingNotification(String str, CharSequence charSequence, TLObject tLObject, List<User> list, int i, Class<? extends Activity> cls) {
        CharSequence spannableString;
        String str2 = str;
        CharSequence charSequence2 = charSequence;
        Intent intent = new Intent(this, cls);
        intent.addFlags(805306368);
        Notification.Builder contentIntent = new Notification.Builder(this).setContentTitle(LocaleController.getString("VoipInCallBranding", C0446R.string.VoipInCallBranding)).setContentText(str2).setSmallIcon(C0446R.drawable.notification).setSubText(charSequence2).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        if (VERSION.SDK_INT >= 26) {
            boolean z;
            NotificationChannel notificationChannel;
            StringBuilder stringBuilder;
            SharedPreferences globalNotificationsSettings = MessagesController.getGlobalNotificationsSettings();
            int i2 = globalNotificationsSettings.getInt("calls_notification_channel", 0);
            NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("incoming_calls");
            stringBuilder2.append(i2);
            NotificationChannel notificationChannel2 = notificationManager.getNotificationChannel(stringBuilder2.toString());
            if (notificationChannel2 != null) {
                if (notificationChannel2.getImportance() >= 4 && notificationChannel2.getSound() == null) {
                    if (notificationChannel2.getVibrationPattern() == null) {
                        z = false;
                        if (z) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("incoming_calls");
                            stringBuilder2.append(i2);
                            notificationChannel = new NotificationChannel(stringBuilder2.toString(), LocaleController.getString("IncomingCalls", C0446R.string.IncomingCalls), 4);
                            notificationChannel.setSound(null, null);
                            notificationChannel.enableVibration(false);
                            notificationChannel.enableLights(false);
                            notificationManager.createNotificationChannel(notificationChannel);
                        }
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("incoming_calls");
                        stringBuilder.append(i2);
                        contentIntent.setChannelId(stringBuilder.toString());
                    }
                }
                FileLog.m0d("User messed up the notification channel; deleting it and creating a proper one");
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("incoming_calls");
                stringBuilder2.append(i2);
                notificationManager.deleteNotificationChannel(stringBuilder2.toString());
                i2++;
                globalNotificationsSettings.edit().putInt("calls_notification_channel", i2).commit();
            }
            z = true;
            if (z) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("incoming_calls");
                stringBuilder2.append(i2);
                notificationChannel = new NotificationChannel(stringBuilder2.toString(), LocaleController.getString("IncomingCalls", C0446R.string.IncomingCalls), 4);
                notificationChannel.setSound(null, null);
                notificationChannel.enableVibration(false);
                notificationChannel.enableLights(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("incoming_calls");
            stringBuilder.append(i2);
            contentIntent.setChannelId(stringBuilder.toString());
        }
        Intent intent2 = new Intent(r0, VoIPActionsReceiver.class);
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(getPackageName());
        stringBuilder3.append(".DECLINE_CALL");
        intent2.setAction(stringBuilder3.toString());
        intent2.putExtra("call_id", getCallID());
        CharSequence string = LocaleController.getString("VoipDeclineCall", C0446R.string.VoipDeclineCall);
        if (VERSION.SDK_INT >= 24) {
            CharSequence spannableString2 = new SpannableString(string);
            ((SpannableString) spannableString2).setSpan(new ForegroundColorSpan(-769226), 0, spannableString2.length(), 0);
            string = spannableString2;
        }
        PendingIntent broadcast = PendingIntent.getBroadcast(r0, 0, intent2, 268435456);
        contentIntent.addAction(C0446R.drawable.ic_call_end_white_24dp, string, broadcast);
        Intent intent3 = new Intent(r0, VoIPActionsReceiver.class);
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append(getPackageName());
        stringBuilder4.append(".ANSWER_CALL");
        intent3.setAction(stringBuilder4.toString());
        intent3.putExtra("call_id", getCallID());
        CharSequence string2 = LocaleController.getString("VoipAnswerCall", C0446R.string.VoipAnswerCall);
        if (VERSION.SDK_INT >= 24) {
            spannableString = new SpannableString(string2);
            ((SpannableString) spannableString).setSpan(new ForegroundColorSpan(-16733696), 0, spannableString.length(), 0);
        } else {
            spannableString = string2;
        }
        PendingIntent broadcast2 = PendingIntent.getBroadcast(r0, 0, intent3, 268435456);
        contentIntent.addAction(C0446R.drawable.ic_call_white_24dp, spannableString, broadcast2);
        contentIntent.setPriority(2);
        if (VERSION.SDK_INT >= 17) {
            contentIntent.setShowWhen(false);
        }
        if (VERSION.SDK_INT >= 21) {
            contentIntent.setColor(-13851168);
            contentIntent.setVibrate(new long[0]);
            contentIntent.setCategory("call");
            contentIntent.setFullScreenIntent(PendingIntent.getActivity(r0, 0, intent, 0), true);
        }
        Notification notification = contentIntent.getNotification();
        if (VERSION.SDK_INT >= 21) {
            RemoteViews remoteViews = new RemoteViews(getPackageName(), LocaleController.isRTL ? C0446R.layout.call_notification_rtl : C0446R.layout.call_notification);
            remoteViews.setTextViewText(C0446R.id.name, str2);
            User currentUser;
            if (TextUtils.isEmpty(charSequence)) {
                remoteViews.setViewVisibility(C0446R.id.subtitle, 8);
                if (UserConfig.getActivatedAccountsCount() > 1) {
                    currentUser = UserConfig.getInstance(r0.currentAccount).getCurrentUser();
                    remoteViews.setTextViewText(C0446R.id.title, LocaleController.formatString("VoipInCallBrandingWithName", C0446R.string.VoipInCallBrandingWithName, ContactsController.formatName(currentUser.first_name, currentUser.last_name)));
                } else {
                    remoteViews.setTextViewText(C0446R.id.title, LocaleController.getString("VoipInCallBranding", C0446R.string.VoipInCallBranding));
                }
            } else {
                if (UserConfig.getActivatedAccountsCount() > 1) {
                    currentUser = UserConfig.getInstance(r0.currentAccount).getCurrentUser();
                    remoteViews.setTextViewText(C0446R.id.subtitle, LocaleController.formatString("VoipAnsweringAsAccount", C0446R.string.VoipAnsweringAsAccount, ContactsController.formatName(currentUser.first_name, currentUser.last_name)));
                } else {
                    remoteViews.setViewVisibility(C0446R.id.subtitle, 8);
                }
                remoteViews.setTextViewText(C0446R.id.title, charSequence2);
            }
            remoteViews.setTextViewText(C0446R.id.answer_text, LocaleController.getString("VoipAnswerCall", C0446R.string.VoipAnswerCall));
            remoteViews.setTextViewText(C0446R.id.decline_text, LocaleController.getString("VoipDeclineCall", C0446R.string.VoipDeclineCall));
            remoteViews.setImageViewBitmap(C0446R.id.photo, getRoundAvatarBitmap(tLObject));
            remoteViews.setOnClickPendingIntent(C0446R.id.answer_btn, broadcast2);
            remoteViews.setOnClickPendingIntent(C0446R.id.decline_btn, broadcast);
            notification.bigContentView = remoteViews;
            notification.headsUpContentView = remoteViews;
        }
        startForeground(ID_INCOMING_CALL_NOTIFICATION, notification);
    }

    protected void callFailed(int i) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Call ");
            stringBuilder.append(getCallID());
            stringBuilder.append(" failed with error code ");
            stringBuilder.append(i);
            throw new Exception(stringBuilder.toString());
        } catch (Throwable e) {
            FileLog.m3e(e);
            this.lastError = i;
            dispatchStateChanged(4);
            if (!(i == -3 || this.soundPool == 0)) {
                this.playingSound = true;
                this.soundPool.play(this.spFailedID, 1.0f, 1.0f, 0, 0, 1.0f);
                AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1000);
            }
            stopSelf();
        }
    }

    void callFailedFromConnectionService() {
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
            if (this.spPlayID != 0) {
                this.soundPool.stop(this.spPlayID);
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
                AndroidUtilities.runOnUIThread(new C06806(), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                if (this.isOutgoing) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(getStatsNetworkType(), 0, 1);
                } else {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(getStatsNetworkType(), 0, 1);
                }
            }
        }
        if (i == 5) {
            if (this.spPlayID != 0) {
                this.soundPool.stop(this.spPlayID);
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

    protected void callEnded() {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Call ");
            stringBuilder.append(getCallID());
            stringBuilder.append(" ended");
            FileLog.m0d(stringBuilder.toString());
        }
        dispatchStateChanged(11);
        if (this.needPlayEndSound) {
            this.playingSound = true;
            this.soundPool.play(this.spEndId, 1.0f, 1.0f, 0, 0, 1.0f);
            AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 700);
        }
        if (this.timeoutRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
            this.timeoutRunnable = null;
        }
        stopSelf();
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
        if (stringBuilder.toString().equals(intent.getAction()) != null) {
            acceptIncomingCallFromNotification();
        }
    }

    private void acceptIncomingCallFromNotification() {
        showNotification();
        if (VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            acceptIncomingCall();
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, getUIActivityClass()).addFlags(805306368), 0).send();
            } catch (Throwable e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m2e("Error starting incall activity", e);
                }
            }
            return;
        }
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPPermissionActivity.class).addFlags(268435456), 0).send();
        } catch (Throwable e2) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m2e("Error starting permission activity", e2);
            }
        }
    }

    public void updateOutputGainControlState() {
        if (this.controller != null) {
            if (this.controllerStarted) {
                AudioManager audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                VoIPController voIPController = this.controller;
                int i = 0;
                boolean z = (!hasEarpiece() || audioManager.isSpeakerphoneOn() || audioManager.isBluetoothScoOn() || this.isHeadsetPlugged) ? false : true;
                voIPController.setAudioOutputGainControlEnabled(z);
                voIPController = this.controller;
                if (!this.isHeadsetPlugged) {
                    if (!hasEarpiece() || audioManager.isSpeakerphoneOn() || audioManager.isBluetoothScoOn() || this.isHeadsetPlugged) {
                        i = 1;
                    }
                }
                voIPController.setEchoCancellationStrength(i);
            }
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
        boolean z = false;
        if (VoIPService.getSharedInstance() == null) {
            return false;
        }
        if (VoIPService.getSharedInstance().getCallState() != 15) {
            z = true;
        }
        return z;
    }

    protected boolean isFinished() {
        if (this.currentState != 11) {
            if (this.currentState != 4) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(26)
    protected PhoneAccountHandle addAccountToTelecomManager() {
        TelecomManager telecomManager = (TelecomManager) getSystemService("telecom");
        User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        ComponentName componentName = new ComponentName(this, TelegramConnectionService.class);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
        stringBuilder.append(currentUser.id);
        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(componentName, stringBuilder.toString());
        telecomManager.registerPhoneAccount(new PhoneAccount.Builder(phoneAccountHandle, ContactsController.formatName(currentUser.first_name, currentUser.last_name)).setCapabilities(2048).setIcon(Icon.createWithResource(this, C0446R.drawable.ic_launcher)).setHighlightColor(-13851168).addSupportedUriScheme("sip").build());
        return phoneAccountHandle;
    }
}
