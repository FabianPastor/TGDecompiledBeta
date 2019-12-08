package org.telegram.messenger;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputNotifyBroadcasts;
import org.telegram.tgnet.TLRPC.TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC.TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC.TL_inputNotifyUsers;
import org.telegram.tgnet.TLRPC.TL_inputPeerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PopupNotificationActivity;

public class NotificationsController extends BaseController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = new NotificationsController[3];
    public static String OTHER_NOTIFICATIONS_CHANNEL = null;
    public static final int SETTING_MUTE_2_DAYS = 2;
    public static final int SETTING_MUTE_8_HOURS = 1;
    public static final int SETTING_MUTE_FOREVER = 3;
    public static final int SETTING_MUTE_HOUR = 0;
    public static final int SETTING_MUTE_UNMUTE = 4;
    public static final int TYPE_CHANNEL = 2;
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_PRIVATE = 1;
    protected static AudioManager audioManager = ((AudioManager) ApplicationLoader.applicationContext.getSystemService("audio"));
    public static long globalSecretChatId = -4294967296L;
    private static NotificationManagerCompat notificationManager;
    private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    private static NotificationManager systemNotificationManager;
    private AlarmManager alarmManager;
    private ArrayList<MessageObject> delayedPushMessages = new ArrayList();
    private LongSparseArray<MessageObject> fcmRandomMessagesDict = new LongSparseArray();
    private boolean inChatSoundEnabled;
    private int lastBadgeCount = -1;
    private int lastButtonId = 5000;
    private int lastOnlineFromOtherDevice = 0;
    private long lastSoundOutPlay;
    private long lastSoundPlay;
    private LongSparseArray<Integer> lastWearNotifiedMessageId = new LongSparseArray();
    private String launcherClassName;
    private Runnable notificationDelayRunnable;
    private WakeLock notificationDelayWakelock;
    private String notificationGroup;
    private int notificationId = (this.currentAccount + 1);
    private boolean notifyCheck = false;
    private long opened_dialog_id = 0;
    private int personal_count = 0;
    public ArrayList<MessageObject> popupMessages = new ArrayList();
    public ArrayList<MessageObject> popupReplyMessages = new ArrayList();
    private LongSparseArray<Integer> pushDialogs = new LongSparseArray();
    private LongSparseArray<Integer> pushDialogsOverrideMention = new LongSparseArray();
    private ArrayList<MessageObject> pushMessages = new ArrayList();
    private LongSparseArray<MessageObject> pushMessagesDict = new LongSparseArray();
    public boolean showBadgeMessages;
    public boolean showBadgeMuted;
    public boolean showBadgeNumber;
    private LongSparseArray<Point> smartNotificationsDialogs = new LongSparseArray();
    private int soundIn;
    private boolean soundInLoaded;
    private int soundOut;
    private boolean soundOutLoaded;
    private SoundPool soundPool;
    private int soundRecord;
    private boolean soundRecordLoaded;
    private int total_unread_count = 0;
    private LongSparseArray<Integer> wearNotificationsIds = new LongSparseArray();

    /* renamed from: org.telegram.messenger.NotificationsController$1NotificationHolder */
    class AnonymousClass1NotificationHolder {
        int id;
        Notification notification;

        AnonymousClass1NotificationHolder(int i, Notification notification) {
            this.id = i;
            this.notification = notification;
        }

        /* Access modifiers changed, original: 0000 */
        public void call() {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("show dialog notification with id ");
                stringBuilder.append(this.id);
                FileLog.w(stringBuilder.toString());
            }
            NotificationsController.notificationManager.notify(this.id, this.notification);
        }
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$36(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$37(TLObject tLObject, TL_error tL_error) {
    }

    public String getGlobalNotificationsKey(int i) {
        return i == 0 ? "EnableGroup2" : i == 1 ? "EnableAll2" : "EnableChannel2";
    }

    static {
        notificationManager = null;
        systemNotificationManager = null;
        if (VERSION.SDK_INT >= 26 && ApplicationLoader.applicationContext != null) {
            notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
            systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
            checkOtherNotificationsChannel();
        }
    }

    public static NotificationsController getInstance(int i) {
        NotificationsController notificationsController = Instance[i];
        if (notificationsController == null) {
            synchronized (NotificationsController.class) {
                notificationsController = Instance[i];
                if (notificationsController == null) {
                    NotificationsController[] notificationsControllerArr = Instance;
                    NotificationsController notificationsController2 = new NotificationsController(i);
                    notificationsControllerArr[i] = notificationsController2;
                    notificationsController = notificationsController2;
                }
            }
        }
        return notificationsController;
    }

    public NotificationsController(int i) {
        super(i);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("messages");
        int i2 = this.currentAccount;
        stringBuilder.append(i2 == 0 ? "" : Integer.valueOf(i2));
        this.notificationGroup = stringBuilder.toString();
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        this.inChatSoundEnabled = notificationsSettings.getBoolean("EnableInChatSound", true);
        this.showBadgeNumber = notificationsSettings.getBoolean("badgeNumber", true);
        this.showBadgeMuted = notificationsSettings.getBoolean("badgeNumberMuted", false);
        this.showBadgeMessages = notificationsSettings.getBoolean("badgeNumberMessages", true);
        notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
        systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
        try {
            audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService("audio");
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService("alarm");
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            this.notificationDelayWakelock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "telegram:notification_delay_lock");
            this.notificationDelayWakelock.setReferenceCounted(false);
        } catch (Exception e3) {
            FileLog.e(e3);
        }
        this.notificationDelayRunnable = new -$$Lambda$NotificationsController$u_XWL43v4eUkt0lAcsDPJJv0mZM(this);
    }

    public /* synthetic */ void lambda$new$0$NotificationsController() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("delay reached");
        }
        if (!this.delayedPushMessages.isEmpty()) {
            showOrUpdateNotification(true);
            this.delayedPushMessages.clear();
        }
        try {
            if (this.notificationDelayWakelock.isHeld()) {
                this.notificationDelayWakelock.release();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void checkOtherNotificationsChannel() {
        if (VERSION.SDK_INT >= 26) {
            SharedPreferences sharedPreferences;
            String str = "OtherKey";
            String str2 = "Notifications";
            if (OTHER_NOTIFICATIONS_CHANNEL == null) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences(str2, 0);
                OTHER_NOTIFICATIONS_CHANNEL = sharedPreferences.getString(str, "Other3");
            } else {
                sharedPreferences = null;
            }
            NotificationChannel notificationChannel = systemNotificationManager.getNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
            if (notificationChannel != null && notificationChannel.getImportance() == 0) {
                systemNotificationManager.deleteNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
                OTHER_NOTIFICATIONS_CHANNEL = null;
                notificationChannel = null;
            }
            String str3 = "Other";
            if (OTHER_NOTIFICATIONS_CHANNEL == null) {
                if (sharedPreferences == null) {
                    sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences(str2, 0);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str3);
                stringBuilder.append(Utilities.random.nextLong());
                OTHER_NOTIFICATIONS_CHANNEL = stringBuilder.toString();
                sharedPreferences.edit().putString(str, OTHER_NOTIFICATIONS_CHANNEL).commit();
            }
            if (notificationChannel == null) {
                NotificationChannel notificationChannel2 = new NotificationChannel(OTHER_NOTIFICATIONS_CHANNEL, str3, 3);
                notificationChannel2.enableLights(false);
                notificationChannel2.enableVibration(false);
                notificationChannel2.setSound(null, null);
                systemNotificationManager.createNotificationChannel(notificationChannel2);
            }
        }
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$A9SCTrujp78_YxIRivW7UAoIEBo(this));
    }

    public /* synthetic */ void lambda$cleanup$1$NotificationsController() {
        this.opened_dialog_id = 0;
        int i = 0;
        this.total_unread_count = 0;
        this.personal_count = 0;
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        this.fcmRandomMessagesDict.clear();
        this.pushDialogs.clear();
        this.wearNotificationsIds.clear();
        this.lastWearNotifiedMessageId.clear();
        this.delayedPushMessages.clear();
        this.notifyCheck = false;
        this.lastBadgeCount = 0;
        try {
            if (this.notificationDelayWakelock.isHeld()) {
                this.notificationDelayWakelock.release();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        dismissNotification();
        setBadge(getTotalAllUnreadCount());
        Editor edit = getAccountInstance().getNotificationsSettings().edit();
        edit.clear();
        edit.commit();
        if (VERSION.SDK_INT >= 26) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.currentAccount);
                stringBuilder.append("channel");
                String stringBuilder2 = stringBuilder.toString();
                List notificationChannels = systemNotificationManager.getNotificationChannels();
                int size = notificationChannels.size();
                while (i < size) {
                    String id = ((NotificationChannel) notificationChannels.get(i)).getId();
                    if (id.startsWith(stringBuilder2)) {
                        systemNotificationManager.deleteNotificationChannel(id);
                    }
                    i++;
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    public void setInChatSoundEnabled(boolean z) {
        this.inChatSoundEnabled = z;
    }

    public /* synthetic */ void lambda$setOpenedDialogId$2$NotificationsController(long j) {
        this.opened_dialog_id = j;
    }

    public void setOpenedDialogId(long j) {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$XWu9HxcgJh0WGxxES9w4G4Lj_cA(this, j));
    }

    public void setLastOnlineFromOtherDevice(int i) {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$aMKmdt9uT4z6-2MONOs1umiLD6k(this, i));
    }

    public /* synthetic */ void lambda$setLastOnlineFromOtherDevice$3$NotificationsController(int i) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("set last online from other device = ");
            stringBuilder.append(i);
            FileLog.d(stringBuilder.toString());
        }
        this.lastOnlineFromOtherDevice = i;
    }

    public void removeNotificationsForDialog(long j) {
        processReadMessages(null, j, 0, Integer.MAX_VALUE, false);
        LongSparseArray longSparseArray = new LongSparseArray();
        longSparseArray.put(j, Integer.valueOf(0));
        processDialogsUpdateRead(longSparseArray);
    }

    public boolean hasMessagesToReply() {
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = (MessageObject) this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            Message message = messageObject.messageOwner;
            if ((!message.mentioned || !(message.action instanceof TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
                return true;
            }
        }
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$eQV-fs8YB0lhGMYS2TKm4CX_EZk(this));
    }

    public /* synthetic */ void lambda$forceShowPopupForReply$5$NotificationsController() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = (MessageObject) this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            Message message = messageObject.messageOwner;
            if (!((message.mentioned && (message.action instanceof TL_messageActionPinMessage)) || ((int) dialogId) == 0 || (messageObject.messageOwner.to_id.channel_id != 0 && !messageObject.isMegagroup()))) {
                arrayList.add(0, messageObject);
            }
        }
        if (!arrayList.isEmpty() && !AndroidUtilities.needShowPasscode(false)) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$NotificationsController$SfVCz2vPoedKrTlwsJaPH9ngam4(this, arrayList));
        }
    }

    public /* synthetic */ void lambda$null$4$NotificationsController(ArrayList arrayList) {
        this.popupReplyMessages = arrayList;
        Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
        intent.putExtra("force", true);
        intent.putExtra("currentAccount", this.currentAccount);
        intent.setFlags(NUM);
        ApplicationLoader.applicationContext.startActivity(intent);
        ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    public void removeDeletedMessagesFromNotifications(SparseArray<ArrayList<Integer>> sparseArray) {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$8lQbr5XMNBt__wC6arYqfGdfeMk(this, sparseArray, new ArrayList(0)));
    }

    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$8$NotificationsController(SparseArray sparseArray, ArrayList arrayList) {
        SparseArray sparseArray2 = sparseArray;
        ArrayList arrayList2 = arrayList;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        Integer valueOf = Integer.valueOf(0);
        int i2 = 0;
        while (i2 < sparseArray.size()) {
            Integer num;
            long j;
            long j2;
            int keyAt = sparseArray2.keyAt(i2);
            long j3 = (long) (-keyAt);
            ArrayList arrayList3 = (ArrayList) sparseArray2.get(keyAt);
            Integer num2 = (Integer) this.pushDialogs.get(j3);
            if (num2 == null) {
                num2 = valueOf;
            }
            Integer num3 = num2;
            int i3 = 0;
            while (i3 < arrayList3.size()) {
                num = valueOf;
                j = j3;
                long intValue = ((long) ((Integer) arrayList3.get(i3)).intValue()) | (((long) keyAt) << 32);
                MessageObject messageObject = (MessageObject) this.pushMessagesDict.get(intValue);
                if (messageObject != null) {
                    int i4;
                    this.pushMessagesDict.remove(intValue);
                    this.delayedPushMessages.remove(messageObject);
                    this.pushMessages.remove(messageObject);
                    if (isPersonalMessage(messageObject)) {
                        i4 = 1;
                        this.personal_count--;
                    } else {
                        i4 = 1;
                    }
                    arrayList2.add(messageObject);
                    num3 = Integer.valueOf(num3.intValue() - i4);
                }
                i3++;
                valueOf = num;
                j3 = j;
            }
            num = valueOf;
            j = j3;
            if (num3.intValue() <= 0) {
                j2 = j;
                this.smartNotificationsDialogs.remove(j2);
                num3 = num;
            } else {
                j2 = j;
            }
            if (!num3.equals(num2)) {
                this.total_unread_count -= num2.intValue();
                this.total_unread_count += num3.intValue();
                this.pushDialogs.put(j2, num3);
            }
            if (num3.intValue() == 0) {
                this.pushDialogs.remove(j2);
                this.pushDialogsOverrideMention.remove(j2);
            }
            i2++;
            valueOf = num;
        }
        boolean z = true;
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$NotificationsController$uUrKIQpuu_OHFjMyR7HGe660wQk(this, arrayList2));
        }
        if (i != this.total_unread_count) {
            if (this.notifyCheck) {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            } else {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$NotificationsController$VcdDGTs8T17vFBc_zmAJ5lCdPBU(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$null$6$NotificationsController(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
    }

    public /* synthetic */ void lambda$null$7$NotificationsController(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void removeDeletedHisoryFromNotifications(SparseIntArray sparseIntArray) {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$4ZPSiSXCXkKfxVPcPpmsFy8foEU(this, sparseIntArray, new ArrayList(0)));
    }

    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$11$NotificationsController(SparseIntArray sparseIntArray, ArrayList arrayList) {
        boolean z;
        SparseIntArray sparseIntArray2 = sparseIntArray;
        ArrayList arrayList2 = arrayList;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        Integer valueOf = Integer.valueOf(0);
        int i2 = 0;
        while (true) {
            z = true;
            if (i2 >= sparseIntArray.size()) {
                break;
            }
            Integer num;
            int keyAt = sparseIntArray2.keyAt(i2);
            long j = (long) (-keyAt);
            keyAt = sparseIntArray2.get(keyAt);
            Integer num2 = (Integer) this.pushDialogs.get(j);
            if (num2 == null) {
                num2 = valueOf;
            }
            Integer num3 = num2;
            int i3 = 0;
            while (i3 < this.pushMessages.size()) {
                MessageObject messageObject = (MessageObject) this.pushMessages.get(i3);
                if (messageObject.getDialogId() != j || messageObject.getId() > keyAt) {
                    num = valueOf;
                } else {
                    num = valueOf;
                    this.pushMessagesDict.remove(messageObject.getIdWithChannel());
                    this.delayedPushMessages.remove(messageObject);
                    this.pushMessages.remove(messageObject);
                    i3--;
                    if (isPersonalMessage(messageObject)) {
                        this.personal_count--;
                    }
                    arrayList2.add(messageObject);
                    num3 = Integer.valueOf(num3.intValue() - 1);
                }
                i3++;
                valueOf = num;
            }
            num = valueOf;
            if (num3.intValue() <= 0) {
                this.smartNotificationsDialogs.remove(j);
                num3 = num;
            }
            if (!num3.equals(num2)) {
                this.total_unread_count -= num2.intValue();
                this.total_unread_count += num3.intValue();
                this.pushDialogs.put(j, num3);
            }
            if (num3.intValue() == 0) {
                this.pushDialogs.remove(j);
                this.pushDialogsOverrideMention.remove(j);
            }
            i2++;
            valueOf = num;
        }
        if (arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$NotificationsController$sZTwdrj4Q3g5O_k6lbH6PmmVEkI(this, arrayList2));
        }
        if (i != this.total_unread_count) {
            if (this.notifyCheck) {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            } else {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$NotificationsController$hEqV8j2COvHkVH0SA_DnqOAATPc(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$null$9$NotificationsController(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
    }

    public /* synthetic */ void lambda$null$10$NotificationsController(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processReadMessages(SparseLongArray sparseLongArray, long j, int i, int i2, boolean z) {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$bn_qy54k0GHNymLhNYsBBa6g2mw(this, sparseLongArray, new ArrayList(0), j, i2, i, z));
    }

    /* JADX WARNING: Missing block: B:41:0x00b5, code skipped:
            r6 = null;
     */
    public /* synthetic */ void lambda$processReadMessages$13$NotificationsController(org.telegram.messenger.support.SparseLongArray r19, java.util.ArrayList r20, long r21, int r23, int r24, boolean r25) {
        /*
        r18 = this;
        r0 = r18;
        r1 = r19;
        r2 = r20;
        r3 = r23;
        r4 = r24;
        r7 = 1;
        if (r1 == 0) goto L_0x0074;
    L_0x000d:
        r8 = 0;
    L_0x000e:
        r9 = r19.size();
        if (r8 >= r9) goto L_0x0074;
    L_0x0014:
        r9 = r1.keyAt(r8);
        r10 = r1.get(r9);
        r12 = 0;
    L_0x001d:
        r13 = r0.pushMessages;
        r13 = r13.size();
        if (r12 >= r13) goto L_0x0071;
    L_0x0025:
        r13 = r0.pushMessages;
        r13 = r13.get(r12);
        r13 = (org.telegram.messenger.MessageObject) r13;
        r14 = r13.getDialogId();
        r5 = (long) r9;
        r17 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1));
        if (r17 != 0) goto L_0x006f;
    L_0x0036:
        r5 = r13.getId();
        r6 = (int) r10;
        if (r5 > r6) goto L_0x006f;
    L_0x003d:
        r5 = r0.isPersonalMessage(r13);
        if (r5 == 0) goto L_0x0048;
    L_0x0043:
        r5 = r0.personal_count;
        r5 = r5 - r7;
        r0.personal_count = r5;
    L_0x0048:
        r2.add(r13);
        r5 = r13.getId();
        r5 = (long) r5;
        r14 = r13.messageOwner;
        r14 = r14.to_id;
        r14 = r14.channel_id;
        if (r14 == 0) goto L_0x005e;
    L_0x0058:
        r14 = (long) r14;
        r16 = 32;
        r14 = r14 << r16;
        r5 = r5 | r14;
    L_0x005e:
        r14 = r0.pushMessagesDict;
        r14.remove(r5);
        r5 = r0.delayedPushMessages;
        r5.remove(r13);
        r5 = r0.pushMessages;
        r5.remove(r12);
        r12 = r12 + -1;
    L_0x006f:
        r12 = r12 + r7;
        goto L_0x001d;
    L_0x0071:
        r8 = r8 + 1;
        goto L_0x000e;
    L_0x0074:
        r5 = 0;
        r1 = (r21 > r5 ? 1 : (r21 == r5 ? 0 : -1));
        if (r1 == 0) goto L_0x00f1;
    L_0x007a:
        if (r3 != 0) goto L_0x007e;
    L_0x007c:
        if (r4 == 0) goto L_0x00f1;
    L_0x007e:
        r1 = 0;
    L_0x007f:
        r5 = r0.pushMessages;
        r5 = r5.size();
        if (r1 >= r5) goto L_0x00f1;
    L_0x0087:
        r5 = r0.pushMessages;
        r5 = r5.get(r1);
        r5 = (org.telegram.messenger.MessageObject) r5;
        r8 = r5.getDialogId();
        r6 = (r8 > r21 ? 1 : (r8 == r21 ? 0 : -1));
        if (r6 != 0) goto L_0x00ed;
    L_0x0097:
        if (r4 == 0) goto L_0x00a1;
    L_0x0099:
        r6 = r5.messageOwner;
        r6 = r6.date;
        if (r6 > r4) goto L_0x00b5;
    L_0x009f:
        r6 = 1;
        goto L_0x00b6;
    L_0x00a1:
        if (r25 != 0) goto L_0x00ac;
    L_0x00a3:
        r6 = r5.getId();
        if (r6 <= r3) goto L_0x009f;
    L_0x00a9:
        if (r3 >= 0) goto L_0x00b5;
    L_0x00ab:
        goto L_0x009f;
    L_0x00ac:
        r6 = r5.getId();
        if (r6 == r3) goto L_0x009f;
    L_0x00b2:
        if (r3 >= 0) goto L_0x00b5;
    L_0x00b4:
        goto L_0x009f;
    L_0x00b5:
        r6 = 0;
    L_0x00b6:
        if (r6 == 0) goto L_0x00ed;
    L_0x00b8:
        r6 = r0.isPersonalMessage(r5);
        if (r6 == 0) goto L_0x00c3;
    L_0x00be:
        r6 = r0.personal_count;
        r6 = r6 - r7;
        r0.personal_count = r6;
    L_0x00c3:
        r6 = r0.pushMessages;
        r6.remove(r1);
        r6 = r0.delayedPushMessages;
        r6.remove(r5);
        r2.add(r5);
        r6 = r5.getId();
        r8 = (long) r6;
        r5 = r5.messageOwner;
        r5 = r5.to_id;
        r5 = r5.channel_id;
        if (r5 == 0) goto L_0x00e3;
    L_0x00dd:
        r5 = (long) r5;
        r10 = 32;
        r5 = r5 << r10;
        r8 = r8 | r5;
        goto L_0x00e5;
    L_0x00e3:
        r10 = 32;
    L_0x00e5:
        r5 = r0.pushMessagesDict;
        r5.remove(r8);
        r1 = r1 + -1;
        goto L_0x00ef;
    L_0x00ed:
        r10 = 32;
    L_0x00ef:
        r1 = r1 + r7;
        goto L_0x007f;
    L_0x00f1:
        r1 = r20.isEmpty();
        if (r1 != 0) goto L_0x00ff;
    L_0x00f7:
        r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$uwXUA8kYkjmDHBUM6M6MDaJprzI;
        r1.<init>(r0, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x00ff:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processReadMessages$13$NotificationsController(org.telegram.messenger.support.SparseLongArray, java.util.ArrayList, long, int, int, boolean):void");
    }

    public /* synthetic */ void lambda$null$12$NotificationsController(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0065  */
    /* JADX WARNING: Missing block: B:18:0x004f, code skipped:
            if (r5 == 2) goto L_0x0051;
     */
    private int addToPopupMessages(java.util.ArrayList<org.telegram.messenger.MessageObject> r3, org.telegram.messenger.MessageObject r4, int r5, long r6, boolean r8, android.content.SharedPreferences r9) {
        /*
        r2 = this;
        r0 = 0;
        if (r5 == 0) goto L_0x0051;
    L_0x0003:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r1 = "custom_";
        r5.append(r1);
        r5.append(r6);
        r5 = r5.toString();
        r5 = r9.getBoolean(r5, r0);
        if (r5 == 0) goto L_0x0030;
    L_0x001a:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r1 = "popup_";
        r5.append(r1);
        r5.append(r6);
        r5 = r5.toString();
        r5 = r9.getInt(r5, r0);
        goto L_0x0031;
    L_0x0030:
        r5 = 0;
    L_0x0031:
        if (r5 != 0) goto L_0x0049;
    L_0x0033:
        if (r8 == 0) goto L_0x003c;
    L_0x0035:
        r5 = "popupChannel";
        r5 = r9.getInt(r5, r0);
        goto L_0x0052;
    L_0x003c:
        r5 = (int) r6;
        if (r5 >= 0) goto L_0x0042;
    L_0x003f:
        r5 = "popupGroup";
        goto L_0x0044;
    L_0x0042:
        r5 = "popupAll";
    L_0x0044:
        r5 = r9.getInt(r5, r0);
        goto L_0x0052;
    L_0x0049:
        r6 = 1;
        if (r5 != r6) goto L_0x004e;
    L_0x004c:
        r5 = 3;
        goto L_0x0052;
    L_0x004e:
        r6 = 2;
        if (r5 != r6) goto L_0x0052;
    L_0x0051:
        r5 = 0;
    L_0x0052:
        if (r5 == 0) goto L_0x0063;
    L_0x0054:
        r6 = r4.messageOwner;
        r6 = r6.to_id;
        r6 = r6.channel_id;
        if (r6 == 0) goto L_0x0063;
    L_0x005c:
        r6 = r4.isMegagroup();
        if (r6 != 0) goto L_0x0063;
    L_0x0062:
        r5 = 0;
    L_0x0063:
        if (r5 == 0) goto L_0x0068;
    L_0x0065:
        r3.add(r0, r4);
    L_0x0068:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.addToPopupMessages(java.util.ArrayList, org.telegram.messenger.MessageObject, int, long, boolean, android.content.SharedPreferences):int");
    }

    public void processNewMessages(ArrayList<MessageObject> arrayList, boolean z, boolean z2, CountDownLatch countDownLatch) {
        if (arrayList.isEmpty()) {
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            return;
        }
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$blpPMIxTaKEgWkp2zDr1_y8eGUY(this, arrayList, new ArrayList(0), z2, z, countDownLatch));
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ec  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00ae  */
    public /* synthetic */ void lambda$processNewMessages$16$NotificationsController(java.util.ArrayList r30, java.util.ArrayList r31, boolean r32, boolean r33, java.util.concurrent.CountDownLatch r34) {
        /*
        r29 = this;
        r8 = r29;
        r9 = r30;
        r10 = new android.util.LongSparseArray;
        r10.<init>();
        r0 = r29.getAccountInstance();
        r11 = r0.getNotificationsSettings();
        r12 = 1;
        r0 = "PinnedMessages";
        r13 = r11.getBoolean(r0, r12);
        r0 = 0;
        r15 = 0;
        r16 = 0;
        r17 = 0;
    L_0x001e:
        r1 = r30.size();
        if (r15 >= r1) goto L_0x01d2;
    L_0x0024:
        r1 = r9.get(r15);
        r7 = r1;
        r7 = (org.telegram.messenger.MessageObject) r7;
        r1 = r7.messageOwner;
        if (r1 == 0) goto L_0x0045;
    L_0x002f:
        r4 = r1.silent;
        if (r4 == 0) goto L_0x0045;
    L_0x0033:
        r1 = r1.action;
        r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r4 != 0) goto L_0x003d;
    L_0x0039:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r1 == 0) goto L_0x0045;
    L_0x003d:
        r25 = r0;
        r21 = r13;
        r20 = r15;
        goto L_0x0116;
    L_0x0045:
        r1 = r7.getId();
        r4 = (long) r1;
        r1 = r7.isFcmMessage();
        r18 = 0;
        if (r1 == 0) goto L_0x005b;
    L_0x0052:
        r1 = r7.messageOwner;
        r20 = r15;
        r14 = r1.random_id;
        r21 = r13;
        goto L_0x0061;
    L_0x005b:
        r20 = r15;
        r21 = r13;
        r14 = r18;
    L_0x0061:
        r12 = r7.getDialogId();
        r6 = (int) r12;
        r1 = r7.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x0077;
    L_0x006e:
        r2 = (long) r1;
        r1 = 32;
        r1 = r2 << r1;
        r4 = r4 | r1;
        r24 = 1;
        goto L_0x0079;
    L_0x0077:
        r24 = 0;
    L_0x0079:
        r1 = r8.pushMessagesDict;
        r1 = r1.get(r4);
        r1 = (org.telegram.messenger.MessageObject) r1;
        if (r1 != 0) goto L_0x00aa;
    L_0x0083:
        r2 = r7.messageOwner;
        r2 = r2.random_id;
        r25 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1));
        if (r25 == 0) goto L_0x00aa;
    L_0x008b:
        r1 = r8.fcmRandomMessagesDict;
        r1 = r1.get(r2);
        r1 = (org.telegram.messenger.MessageObject) r1;
        if (r1 == 0) goto L_0x00a3;
    L_0x0095:
        r2 = r8.fcmRandomMessagesDict;
        r3 = r7.messageOwner;
        r25 = r0;
        r26 = r1;
        r0 = r3.random_id;
        r2.remove(r0);
        goto L_0x00a7;
    L_0x00a3:
        r25 = r0;
        r26 = r1;
    L_0x00a7:
        r1 = r26;
        goto L_0x00ac;
    L_0x00aa:
        r25 = r0;
    L_0x00ac:
        if (r1 == 0) goto L_0x00ec;
    L_0x00ae:
        r0 = r1.isFcmMessage();
        if (r0 == 0) goto L_0x0116;
    L_0x00b4:
        r0 = r8.pushMessagesDict;
        r0.put(r4, r7);
        r0 = r8.pushMessages;
        r0 = r0.indexOf(r1);
        if (r0 < 0) goto L_0x00d8;
    L_0x00c1:
        r1 = r8.pushMessages;
        r1.set(r0, r7);
        r0 = r29;
        r1 = r31;
        r2 = r7;
        r3 = r6;
        r4 = r12;
        r6 = r24;
        r12 = r7;
        r7 = r11;
        r0 = r0.addToPopupMessages(r1, r2, r3, r4, r6, r7);
        r25 = r0;
        goto L_0x00d9;
    L_0x00d8:
        r12 = r7;
    L_0x00d9:
        if (r32 == 0) goto L_0x00e7;
    L_0x00db:
        r0 = r12.localEdit;
        if (r0 == 0) goto L_0x00e9;
    L_0x00df:
        r1 = r29.getMessagesStorage();
        r1.putPushMessage(r12);
        goto L_0x00e9;
    L_0x00e7:
        r0 = r17;
    L_0x00e9:
        r17 = r0;
        goto L_0x0116;
    L_0x00ec:
        if (r17 == 0) goto L_0x00ef;
    L_0x00ee:
        goto L_0x0116;
    L_0x00ef:
        if (r32 == 0) goto L_0x00f8;
    L_0x00f1:
        r0 = r29.getMessagesStorage();
        r0.putPushMessage(r7);
    L_0x00f8:
        r0 = r8.opened_dialog_id;
        r2 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1));
        if (r2 != 0) goto L_0x0108;
    L_0x00fe:
        r0 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r0 == 0) goto L_0x0108;
    L_0x0102:
        if (r32 != 0) goto L_0x0116;
    L_0x0104:
        r29.playInChatSound();
        goto L_0x0116;
    L_0x0108:
        r0 = r7.messageOwner;
        r1 = r0.mentioned;
        if (r1 == 0) goto L_0x0123;
    L_0x010e:
        if (r21 != 0) goto L_0x011c;
    L_0x0110:
        r0 = r0.action;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r0 == 0) goto L_0x011c;
    L_0x0116:
        r26 = r10;
        r0 = r25;
        goto L_0x01c7;
    L_0x011c:
        r0 = r7.messageOwner;
        r0 = r0.from_id;
        r0 = (long) r0;
        r2 = r0;
        goto L_0x0124;
    L_0x0123:
        r2 = r12;
    L_0x0124:
        r0 = r8.isPersonalMessage(r7);
        if (r0 == 0) goto L_0x0130;
    L_0x012a:
        r0 = r8.personal_count;
        r1 = 1;
        r0 = r0 + r1;
        r8.personal_count = r0;
    L_0x0130:
        r0 = r10.indexOfKey(r2);
        if (r0 < 0) goto L_0x0141;
    L_0x0136:
        r0 = r10.valueAt(r0);
        r0 = (java.lang.Boolean) r0;
        r0 = r0.booleanValue();
        goto L_0x015a;
    L_0x0141:
        r0 = r8.getNotifyOverride(r11, r2);
        r1 = -1;
        if (r0 != r1) goto L_0x014d;
    L_0x0148:
        r0 = r8.isGlobalNotificationsEnabled(r2);
        goto L_0x0153;
    L_0x014d:
        r1 = 2;
        if (r0 == r1) goto L_0x0152;
    L_0x0150:
        r0 = 1;
        goto L_0x0153;
    L_0x0152:
        r0 = 0;
    L_0x0153:
        r1 = java.lang.Boolean.valueOf(r0);
        r10.put(r2, r1);
    L_0x015a:
        if (r0 == 0) goto L_0x01c1;
    L_0x015c:
        if (r32 != 0) goto L_0x0178;
    L_0x015e:
        r0 = r29;
        r1 = r31;
        r22 = r2;
        r2 = r7;
        r3 = r6;
        r26 = r10;
        r9 = r4;
        r4 = r22;
        r6 = r24;
        r27 = r12;
        r12 = r7;
        r7 = r11;
        r0 = r0.addToPopupMessages(r1, r2, r3, r4, r6, r7);
        r25 = r0;
        goto L_0x0180;
    L_0x0178:
        r22 = r2;
        r26 = r10;
        r27 = r12;
        r9 = r4;
        r12 = r7;
    L_0x0180:
        r0 = r8.delayedPushMessages;
        r0.add(r12);
        r0 = r8.pushMessages;
        r1 = 0;
        r0.add(r1, r12);
        r0 = (r9 > r18 ? 1 : (r9 == r18 ? 0 : -1));
        if (r0 == 0) goto L_0x0195;
    L_0x018f:
        r0 = r8.pushMessagesDict;
        r0.put(r9, r12);
        goto L_0x019e;
    L_0x0195:
        r0 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1));
        if (r0 == 0) goto L_0x019e;
    L_0x0199:
        r0 = r8.fcmRandomMessagesDict;
        r0.put(r14, r12);
    L_0x019e:
        r0 = (r27 > r22 ? 1 : (r27 == r22 ? 0 : -1));
        if (r0 == 0) goto L_0x01c3;
    L_0x01a2:
        r0 = r8.pushDialogsOverrideMention;
        r1 = r27;
        r0 = r0.get(r1);
        r0 = (java.lang.Integer) r0;
        r3 = r8.pushDialogsOverrideMention;
        if (r0 != 0) goto L_0x01b2;
    L_0x01b0:
        r12 = 1;
        goto L_0x01b9;
    L_0x01b2:
        r0 = r0.intValue();
        r4 = 1;
        r12 = r0 + 1;
    L_0x01b9:
        r0 = java.lang.Integer.valueOf(r12);
        r3.put(r1, r0);
        goto L_0x01c3;
    L_0x01c1:
        r26 = r10;
    L_0x01c3:
        r0 = r25;
        r16 = 1;
    L_0x01c7:
        r15 = r20 + 1;
        r9 = r30;
        r13 = r21;
        r10 = r26;
        r12 = 1;
        goto L_0x001e;
    L_0x01d2:
        r25 = r0;
        if (r16 == 0) goto L_0x01da;
    L_0x01d6:
        r0 = r33;
        r8.notifyCheck = r0;
    L_0x01da:
        r0 = r31.isEmpty();
        if (r0 != 0) goto L_0x01f3;
    L_0x01e0:
        r0 = 0;
        r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r0);
        if (r1 != 0) goto L_0x01f3;
    L_0x01e7:
        r0 = new org.telegram.messenger.-$$Lambda$NotificationsController$vBhFCZdXUS15Ipx-fzqzTMIuA3o;
        r1 = r31;
        r14 = r25;
        r0.<init>(r8, r1, r14);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x01f3:
        if (r32 == 0) goto L_0x029c;
    L_0x01f5:
        if (r17 == 0) goto L_0x0203;
    L_0x01f7:
        r0 = r8.delayedPushMessages;
        r0.clear();
        r0 = r8.notifyCheck;
        r8.showOrUpdateNotification(r0);
        goto L_0x029c;
    L_0x0203:
        if (r16 == 0) goto L_0x029c;
    L_0x0205:
        r0 = r30;
        r1 = 0;
        r0 = r0.get(r1);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.getDialogId();
        r2 = r8.total_unread_count;
        r3 = r8.getNotifyOverride(r11, r0);
        r4 = -1;
        if (r3 != r4) goto L_0x0221;
    L_0x021b:
        r3 = r8.isGlobalNotificationsEnabled(r0);
    L_0x021f:
        r12 = r3;
        goto L_0x0228;
    L_0x0221:
        r4 = 2;
        if (r3 == r4) goto L_0x0226;
    L_0x0224:
        r3 = 1;
        goto L_0x021f;
    L_0x0226:
        r3 = 0;
        goto L_0x021f;
    L_0x0228:
        r3 = r8.pushDialogs;
        r3 = r3.get(r0);
        r3 = (java.lang.Integer) r3;
        if (r3 == 0) goto L_0x0239;
    L_0x0232:
        r4 = r3.intValue();
        r5 = 1;
        r4 = r4 + r5;
        goto L_0x023b;
    L_0x0239:
        r5 = 1;
        r4 = 1;
    L_0x023b:
        r4 = java.lang.Integer.valueOf(r4);
        r6 = r8.notifyCheck;
        if (r6 == 0) goto L_0x0257;
    L_0x0243:
        if (r12 != 0) goto L_0x0257;
    L_0x0245:
        r6 = r8.pushDialogsOverrideMention;
        r6 = r6.get(r0);
        r6 = (java.lang.Integer) r6;
        if (r6 == 0) goto L_0x0257;
    L_0x024f:
        r7 = r6.intValue();
        if (r7 == 0) goto L_0x0257;
    L_0x0255:
        r4 = r6;
        r12 = 1;
    L_0x0257:
        if (r12 == 0) goto L_0x0272;
    L_0x0259:
        if (r3 == 0) goto L_0x0264;
    L_0x025b:
        r5 = r8.total_unread_count;
        r3 = r3.intValue();
        r5 = r5 - r3;
        r8.total_unread_count = r5;
    L_0x0264:
        r3 = r8.total_unread_count;
        r5 = r4.intValue();
        r3 = r3 + r5;
        r8.total_unread_count = r3;
        r3 = r8.pushDialogs;
        r3.put(r0, r4);
    L_0x0272:
        r0 = r8.total_unread_count;
        if (r2 == r0) goto L_0x028e;
    L_0x0276:
        r0 = r8.delayedPushMessages;
        r0.clear();
        r0 = r8.notifyCheck;
        r8.showOrUpdateNotification(r0);
        r0 = r8.pushDialogs;
        r0 = r0.size();
        r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$R3R5Z37efc0XPsswynnBTmucwac;
        r1.<init>(r8, r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x028e:
        r0 = 0;
        r8.notifyCheck = r0;
        r0 = r8.showBadgeNumber;
        if (r0 == 0) goto L_0x029c;
    L_0x0295:
        r0 = r29.getTotalAllUnreadCount();
        r8.setBadge(r0);
    L_0x029c:
        if (r34 == 0) goto L_0x02a1;
    L_0x029e:
        r34.countDown();
    L_0x02a1:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processNewMessages$16$NotificationsController(java.util.ArrayList, java.util.ArrayList, boolean, boolean, java.util.concurrent.CountDownLatch):void");
    }

    public /* synthetic */ void lambda$null$14$NotificationsController(ArrayList arrayList, int i) {
        this.popupMessages.addAll(0, arrayList);
        if (!ApplicationLoader.mainInterfacePaused && (ApplicationLoader.isScreenOn || SharedConfig.isWaitingForPasscodeEnter)) {
            return;
        }
        if (i == 3 || ((i == 1 && ApplicationLoader.isScreenOn) || (i == 2 && !ApplicationLoader.isScreenOn))) {
            Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
            intent.setFlags(NUM);
            try {
                ApplicationLoader.applicationContext.startActivity(intent);
            } catch (Throwable unused) {
            }
        }
    }

    public /* synthetic */ void lambda$null$15$NotificationsController(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(LongSparseArray<Integer> longSparseArray) {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$bRv8AkmkiAwGyZ1dPg2TuCyHYS0(this, longSparseArray, new ArrayList()));
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0091  */
    public /* synthetic */ void lambda$processDialogsUpdateRead$19$NotificationsController(android.util.LongSparseArray r18, java.util.ArrayList r19) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r18;
        r2 = r19;
        r3 = r0.total_unread_count;
        r4 = r17.getAccountInstance();
        r4 = r4.getNotificationsSettings();
        r5 = 0;
        r6 = 0;
    L_0x0012:
        r7 = r18.size();
        r8 = 1;
        if (r6 >= r7) goto L_0x00fc;
    L_0x0019:
        r9 = r1.keyAt(r6);
        r7 = r0.getNotifyOverride(r4, r9);
        r11 = -1;
        if (r7 != r11) goto L_0x0029;
    L_0x0024:
        r7 = r0.isGlobalNotificationsEnabled(r9);
        goto L_0x002f;
    L_0x0029:
        r11 = 2;
        if (r7 == r11) goto L_0x002e;
    L_0x002c:
        r7 = 1;
        goto L_0x002f;
    L_0x002e:
        r7 = 0;
    L_0x002f:
        r11 = r0.pushDialogs;
        r11 = r11.get(r9);
        r11 = (java.lang.Integer) r11;
        r12 = r1.get(r9);
        r12 = (java.lang.Integer) r12;
        r13 = r0.notifyCheck;
        if (r13 == 0) goto L_0x0055;
    L_0x0041:
        if (r7 != 0) goto L_0x0055;
    L_0x0043:
        r13 = r0.pushDialogsOverrideMention;
        r13 = r13.get(r9);
        r13 = (java.lang.Integer) r13;
        if (r13 == 0) goto L_0x0055;
    L_0x004d:
        r14 = r13.intValue();
        if (r14 == 0) goto L_0x0055;
    L_0x0053:
        r7 = 1;
        goto L_0x0056;
    L_0x0055:
        r13 = r12;
    L_0x0056:
        r12 = r13.intValue();
        if (r12 != 0) goto L_0x0061;
    L_0x005c:
        r12 = r0.smartNotificationsDialogs;
        r12.remove(r9);
    L_0x0061:
        r12 = r13.intValue();
        if (r12 >= 0) goto L_0x0078;
    L_0x0067:
        if (r11 != 0) goto L_0x006b;
    L_0x0069:
        goto L_0x00f8;
    L_0x006b:
        r12 = r11.intValue();
        r13 = r13.intValue();
        r12 = r12 + r13;
        r13 = java.lang.Integer.valueOf(r12);
    L_0x0078:
        if (r7 != 0) goto L_0x0080;
    L_0x007a:
        r12 = r13.intValue();
        if (r12 != 0) goto L_0x008b;
    L_0x0080:
        if (r11 == 0) goto L_0x008b;
    L_0x0082:
        r12 = r0.total_unread_count;
        r11 = r11.intValue();
        r12 = r12 - r11;
        r0.total_unread_count = r12;
    L_0x008b:
        r11 = r13.intValue();
        if (r11 != 0) goto L_0x00e8;
    L_0x0091:
        r7 = r0.pushDialogs;
        r7.remove(r9);
        r7 = r0.pushDialogsOverrideMention;
        r7.remove(r9);
        r7 = 0;
    L_0x009c:
        r11 = r0.pushMessages;
        r11 = r11.size();
        if (r7 >= r11) goto L_0x00f8;
    L_0x00a4:
        r11 = r0.pushMessages;
        r11 = r11.get(r7);
        r11 = (org.telegram.messenger.MessageObject) r11;
        r12 = r11.getDialogId();
        r14 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1));
        if (r14 != 0) goto L_0x00e6;
    L_0x00b4:
        r12 = r0.isPersonalMessage(r11);
        if (r12 == 0) goto L_0x00bf;
    L_0x00ba:
        r12 = r0.personal_count;
        r12 = r12 - r8;
        r0.personal_count = r12;
    L_0x00bf:
        r12 = r0.pushMessages;
        r12.remove(r7);
        r7 = r7 + -1;
        r12 = r0.delayedPushMessages;
        r12.remove(r11);
        r12 = r11.getId();
        r12 = (long) r12;
        r14 = r11.messageOwner;
        r14 = r14.to_id;
        r14 = r14.channel_id;
        if (r14 == 0) goto L_0x00de;
    L_0x00d8:
        r14 = (long) r14;
        r16 = 32;
        r14 = r14 << r16;
        r12 = r12 | r14;
    L_0x00de:
        r14 = r0.pushMessagesDict;
        r14.remove(r12);
        r2.add(r11);
    L_0x00e6:
        r7 = r7 + r8;
        goto L_0x009c;
    L_0x00e8:
        if (r7 == 0) goto L_0x00f8;
    L_0x00ea:
        r7 = r0.total_unread_count;
        r8 = r13.intValue();
        r7 = r7 + r8;
        r0.total_unread_count = r7;
        r7 = r0.pushDialogs;
        r7.put(r9, r13);
    L_0x00f8:
        r6 = r6 + 1;
        goto L_0x0012;
    L_0x00fc:
        r1 = r19.isEmpty();
        if (r1 != 0) goto L_0x010a;
    L_0x0102:
        r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$ONJqyaSxnewsyizGxRK-V30P95A;
        r1.<init>(r0, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x010a:
        r1 = r0.total_unread_count;
        if (r3 == r1) goto L_0x013c;
    L_0x010e:
        r1 = r0.notifyCheck;
        if (r1 != 0) goto L_0x011d;
    L_0x0112:
        r1 = r0.delayedPushMessages;
        r1.clear();
        r1 = r0.notifyCheck;
        r0.showOrUpdateNotification(r1);
        goto L_0x012e;
    L_0x011d:
        r1 = r0.lastOnlineFromOtherDevice;
        r2 = r17.getConnectionsManager();
        r2 = r2.getCurrentTime();
        if (r1 <= r2) goto L_0x012a;
    L_0x0129:
        goto L_0x012b;
    L_0x012a:
        r8 = 0;
    L_0x012b:
        r0.scheduleNotificationDelay(r8);
    L_0x012e:
        r1 = r0.pushDialogs;
        r1 = r1.size();
        r2 = new org.telegram.messenger.-$$Lambda$NotificationsController$GAjtCMO1qmPedRnHLLIKT37DETU;
        r2.<init>(r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x013c:
        r0.notifyCheck = r5;
        r1 = r0.showBadgeNumber;
        if (r1 == 0) goto L_0x0149;
    L_0x0142:
        r1 = r17.getTotalAllUnreadCount();
        r0.setBadge(r1);
    L_0x0149:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processDialogsUpdateRead$19$NotificationsController(android.util.LongSparseArray, java.util.ArrayList):void");
    }

    public /* synthetic */ void lambda$null$17$NotificationsController(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
    }

    public /* synthetic */ void lambda$null$18$NotificationsController(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processLoadedUnreadMessages(LongSparseArray<Integer> longSparseArray, ArrayList<Message> arrayList, ArrayList<MessageObject> arrayList2, ArrayList<User> arrayList3, ArrayList<Chat> arrayList4, ArrayList<EncryptedChat> arrayList5) {
        getMessagesController().putUsers(arrayList3, true);
        getMessagesController().putChats(arrayList4, true);
        getMessagesController().putEncryptedChats(arrayList5, true);
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$XEAogHRWLk5KuijEFvgR3DVl_Oc(this, arrayList, longSparseArray, arrayList2));
    }

    /* JADX WARNING: Missing block: B:12:0x0049, code skipped:
            if ((r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined) == false) goto L_0x0050;
     */
    public /* synthetic */ void lambda$processLoadedUnreadMessages$21$NotificationsController(java.util.ArrayList r21, android.util.LongSparseArray r22, java.util.ArrayList r23) {
        /*
        r20 = this;
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r3 = r23;
        r4 = r0.pushDialogs;
        r4.clear();
        r4 = r0.pushMessages;
        r4.clear();
        r4 = r0.pushMessagesDict;
        r4.clear();
        r4 = 0;
        r0.total_unread_count = r4;
        r0.personal_count = r4;
        r5 = r20.getAccountInstance();
        r5 = r5.getNotificationsSettings();
        r6 = new android.util.LongSparseArray;
        r6.<init>();
        r7 = 32;
        r10 = 1;
        if (r1 == 0) goto L_0x0100;
    L_0x002e:
        r11 = 0;
    L_0x002f:
        r12 = r21.size();
        if (r11 >= r12) goto L_0x0100;
    L_0x0035:
        r12 = r1.get(r11);
        r12 = (org.telegram.tgnet.TLRPC.Message) r12;
        if (r12 == 0) goto L_0x0050;
    L_0x003d:
        r13 = r12.silent;
        if (r13 == 0) goto L_0x0050;
    L_0x0041:
        r13 = r12.action;
        r14 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r14 != 0) goto L_0x004b;
    L_0x0047:
        r13 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r13 == 0) goto L_0x0050;
    L_0x004b:
        r18 = r5;
        r12 = r11;
        goto L_0x00f6;
    L_0x0050:
        r13 = r12.id;
        r13 = (long) r13;
        r15 = r12.to_id;
        r15 = r15.channel_id;
        if (r15 == 0) goto L_0x005c;
    L_0x0059:
        r8 = (long) r15;
        r8 = r8 << r7;
        r13 = r13 | r8;
    L_0x005c:
        r8 = r0.pushMessagesDict;
        r8 = r8.indexOfKey(r13);
        if (r8 < 0) goto L_0x0065;
    L_0x0064:
        goto L_0x004b;
    L_0x0065:
        r8 = new org.telegram.messenger.MessageObject;
        r9 = r0.currentAccount;
        r8.<init>(r9, r12, r4);
        r9 = r0.isPersonalMessage(r8);
        if (r9 == 0) goto L_0x0077;
    L_0x0072:
        r9 = r0.personal_count;
        r9 = r9 + r10;
        r0.personal_count = r9;
    L_0x0077:
        r12 = r11;
        r10 = r8.getDialogId();
        r15 = r8.messageOwner;
        r9 = r15.mentioned;
        if (r9 == 0) goto L_0x0088;
    L_0x0082:
        r9 = r15.from_id;
        r17 = r8;
        r7 = (long) r9;
        goto L_0x008b;
    L_0x0088:
        r17 = r8;
        r7 = r10;
    L_0x008b:
        r9 = r6.indexOfKey(r7);
        if (r9 < 0) goto L_0x009c;
    L_0x0091:
        r9 = r6.valueAt(r9);
        r9 = (java.lang.Boolean) r9;
        r9 = r9.booleanValue();
        goto L_0x00b5;
    L_0x009c:
        r9 = r0.getNotifyOverride(r5, r7);
        r15 = -1;
        if (r9 != r15) goto L_0x00a8;
    L_0x00a3:
        r9 = r0.isGlobalNotificationsEnabled(r7);
        goto L_0x00ae;
    L_0x00a8:
        r15 = 2;
        if (r9 == r15) goto L_0x00ad;
    L_0x00ab:
        r9 = 1;
        goto L_0x00ae;
    L_0x00ad:
        r9 = 0;
    L_0x00ae:
        r15 = java.lang.Boolean.valueOf(r9);
        r6.put(r7, r15);
    L_0x00b5:
        if (r9 == 0) goto L_0x00f4;
    L_0x00b7:
        r18 = r5;
        r4 = r0.opened_dialog_id;
        r9 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1));
        if (r9 != 0) goto L_0x00c4;
    L_0x00bf:
        r4 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r4 == 0) goto L_0x00c4;
    L_0x00c3:
        goto L_0x00f6;
    L_0x00c4:
        r4 = r0.pushMessagesDict;
        r5 = r17;
        r4.put(r13, r5);
        r4 = r0.pushMessages;
        r9 = 0;
        r4.add(r9, r5);
        r4 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1));
        if (r4 == 0) goto L_0x00f6;
    L_0x00d5:
        r4 = r0.pushDialogsOverrideMention;
        r4 = r4.get(r10);
        r4 = (java.lang.Integer) r4;
        r5 = r0.pushDialogsOverrideMention;
        if (r4 != 0) goto L_0x00e4;
    L_0x00e1:
        r16 = 1;
        goto L_0x00ec;
    L_0x00e4:
        r4 = r4.intValue();
        r7 = 1;
        r4 = r4 + r7;
        r16 = r4;
    L_0x00ec:
        r4 = java.lang.Integer.valueOf(r16);
        r5.put(r10, r4);
        goto L_0x00f6;
    L_0x00f4:
        r18 = r5;
    L_0x00f6:
        r11 = r12 + 1;
        r5 = r18;
        r4 = 0;
        r7 = 32;
        r10 = 1;
        goto L_0x002f;
    L_0x0100:
        r18 = r5;
        r1 = 0;
    L_0x0103:
        r4 = r22.size();
        if (r1 >= r4) goto L_0x015c;
    L_0x0109:
        r4 = r2.keyAt(r1);
        r7 = r6.indexOfKey(r4);
        if (r7 < 0) goto L_0x0121;
    L_0x0113:
        r7 = r6.valueAt(r7);
        r7 = (java.lang.Boolean) r7;
        r7 = r7.booleanValue();
        r8 = r7;
        r7 = r18;
        goto L_0x013c;
    L_0x0121:
        r7 = r18;
        r8 = r0.getNotifyOverride(r7, r4);
        r10 = -1;
        if (r8 != r10) goto L_0x012f;
    L_0x012a:
        r8 = r0.isGlobalNotificationsEnabled(r4);
        goto L_0x0135;
    L_0x012f:
        r10 = 2;
        if (r8 == r10) goto L_0x0134;
    L_0x0132:
        r8 = 1;
        goto L_0x0135;
    L_0x0134:
        r8 = 0;
    L_0x0135:
        r10 = java.lang.Boolean.valueOf(r8);
        r6.put(r4, r10);
    L_0x013c:
        if (r8 != 0) goto L_0x013f;
    L_0x013e:
        goto L_0x0157;
    L_0x013f:
        r8 = r2.valueAt(r1);
        r8 = (java.lang.Integer) r8;
        r8 = r8.intValue();
        r10 = r0.pushDialogs;
        r11 = java.lang.Integer.valueOf(r8);
        r10.put(r4, r11);
        r4 = r0.total_unread_count;
        r4 = r4 + r8;
        r0.total_unread_count = r4;
    L_0x0157:
        r1 = r1 + 1;
        r18 = r7;
        goto L_0x0103;
    L_0x015c:
        r7 = r18;
        if (r3 == 0) goto L_0x0264;
    L_0x0160:
        r1 = 0;
    L_0x0161:
        r2 = r23.size();
        if (r1 >= r2) goto L_0x0264;
    L_0x0167:
        r2 = r3.get(r1);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r4 = r2.getId();
        r4 = (long) r4;
        r8 = r2.messageOwner;
        r8 = r8.to_id;
        r8 = r8.channel_id;
        if (r8 == 0) goto L_0x0180;
    L_0x017a:
        r10 = (long) r8;
        r8 = 32;
        r10 = r10 << r8;
        r4 = r4 | r10;
        goto L_0x0182;
    L_0x0180:
        r8 = 32;
    L_0x0182:
        r10 = r0.pushMessagesDict;
        r10 = r10.indexOfKey(r4);
        if (r10 < 0) goto L_0x018f;
    L_0x018a:
        r5 = 0;
        r16 = 1;
        goto L_0x0260;
    L_0x018f:
        r10 = r0.isPersonalMessage(r2);
        if (r10 == 0) goto L_0x019b;
    L_0x0195:
        r10 = r0.personal_count;
        r9 = 1;
        r10 = r10 + r9;
        r0.personal_count = r10;
    L_0x019b:
        r10 = r2.getDialogId();
        r12 = r2.messageOwner;
        r13 = r12.random_id;
        r8 = r12.mentioned;
        if (r8 == 0) goto L_0x01ad;
    L_0x01a7:
        r8 = r12.from_id;
        r21 = r10;
        r9 = (long) r8;
        goto L_0x01b1;
    L_0x01ad:
        r21 = r10;
        r9 = r21;
    L_0x01b1:
        r8 = r6.indexOfKey(r9);
        if (r8 < 0) goto L_0x01c3;
    L_0x01b7:
        r8 = r6.valueAt(r8);
        r8 = (java.lang.Boolean) r8;
        r8 = r8.booleanValue();
        r12 = 2;
        goto L_0x01dd;
    L_0x01c3:
        r8 = r0.getNotifyOverride(r7, r9);
        r11 = -1;
        if (r8 != r11) goto L_0x01d0;
    L_0x01ca:
        r8 = r0.isGlobalNotificationsEnabled(r9);
        r12 = 2;
        goto L_0x01d6;
    L_0x01d0:
        r12 = 2;
        if (r8 == r12) goto L_0x01d5;
    L_0x01d3:
        r8 = 1;
        goto L_0x01d6;
    L_0x01d5:
        r8 = 0;
    L_0x01d6:
        r11 = java.lang.Boolean.valueOf(r8);
        r6.put(r9, r11);
    L_0x01dd:
        if (r8 == 0) goto L_0x018a;
    L_0x01df:
        r18 = r13;
        r12 = r0.opened_dialog_id;
        r8 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1));
        if (r8 != 0) goto L_0x01ec;
    L_0x01e7:
        r8 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r8 == 0) goto L_0x01ec;
    L_0x01eb:
        goto L_0x018a;
    L_0x01ec:
        r11 = 0;
        r8 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1));
        if (r8 == 0) goto L_0x01f8;
    L_0x01f2:
        r8 = r0.pushMessagesDict;
        r8.put(r4, r2);
        goto L_0x0203;
    L_0x01f8:
        r4 = (r18 > r11 ? 1 : (r18 == r11 ? 0 : -1));
        if (r4 == 0) goto L_0x0203;
    L_0x01fc:
        r4 = r0.fcmRandomMessagesDict;
        r11 = r18;
        r4.put(r11, r2);
    L_0x0203:
        r4 = r0.pushMessages;
        r5 = 0;
        r4.add(r5, r2);
        r2 = (r21 > r9 ? 1 : (r21 == r9 ? 0 : -1));
        if (r2 == 0) goto L_0x022f;
    L_0x020d:
        r2 = r0.pushDialogsOverrideMention;
        r11 = r21;
        r2 = r2.get(r11);
        r2 = (java.lang.Integer) r2;
        r4 = r0.pushDialogsOverrideMention;
        if (r2 != 0) goto L_0x021f;
    L_0x021b:
        r2 = 1;
        r16 = 1;
        goto L_0x0227;
    L_0x021f:
        r2 = r2.intValue();
        r16 = 1;
        r2 = r2 + 1;
    L_0x0227:
        r2 = java.lang.Integer.valueOf(r2);
        r4.put(r11, r2);
        goto L_0x0231;
    L_0x022f:
        r16 = 1;
    L_0x0231:
        r2 = r0.pushDialogs;
        r2 = r2.get(r9);
        r2 = (java.lang.Integer) r2;
        if (r2 == 0) goto L_0x0242;
    L_0x023b:
        r4 = r2.intValue();
        r4 = r4 + 1;
        goto L_0x0243;
    L_0x0242:
        r4 = 1;
    L_0x0243:
        r4 = java.lang.Integer.valueOf(r4);
        if (r2 == 0) goto L_0x0252;
    L_0x0249:
        r8 = r0.total_unread_count;
        r2 = r2.intValue();
        r8 = r8 - r2;
        r0.total_unread_count = r8;
    L_0x0252:
        r2 = r0.total_unread_count;
        r8 = r4.intValue();
        r2 = r2 + r8;
        r0.total_unread_count = r2;
        r2 = r0.pushDialogs;
        r2.put(r9, r4);
    L_0x0260:
        r1 = r1 + 1;
        goto L_0x0161;
    L_0x0264:
        r5 = 0;
        r16 = 1;
        r1 = r0.pushDialogs;
        r1 = r1.size();
        r2 = new org.telegram.messenger.-$$Lambda$NotificationsController$CkSMdSXLZtMteSgS81186zoUJaI;
        r2.<init>(r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
        r1 = android.os.SystemClock.elapsedRealtime();
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r1 = r1 / r3;
        r3 = 60;
        r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r6 >= 0) goto L_0x0283;
    L_0x0282:
        r5 = 1;
    L_0x0283:
        r0.showOrUpdateNotification(r5);
        r1 = r0.showBadgeNumber;
        if (r1 == 0) goto L_0x0291;
    L_0x028a:
        r1 = r20.getTotalAllUnreadCount();
        r0.setBadge(r1);
    L_0x0291:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processLoadedUnreadMessages$21$NotificationsController(java.util.ArrayList, android.util.LongSparseArray, java.util.ArrayList):void");
    }

    public /* synthetic */ void lambda$null$20$NotificationsController(int i) {
        if (this.total_unread_count == 0) {
            this.popupMessages.clear();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    private int getTotalAllUnreadCount() {
        Throwable e;
        int i = 0;
        for (int i2 = 0; i2 < 3; i2++) {
            if (UserConfig.getInstance(i2).isClientActivated()) {
                NotificationsController instance = getInstance(i2);
                if (instance.showBadgeNumber) {
                    int i3;
                    int size;
                    if (instance.showBadgeMessages) {
                        if (instance.showBadgeMuted) {
                            try {
                                size = MessagesController.getInstance(i2).allDialogs.size();
                                i3 = i;
                                i = 0;
                                while (i < size) {
                                    try {
                                        Dialog dialog = (Dialog) MessagesController.getInstance(i2).allDialogs.get(i);
                                        if (dialog.unread_count != 0) {
                                            i3 += dialog.unread_count;
                                        }
                                        i++;
                                    } catch (Exception e2) {
                                        e = e2;
                                        FileLog.e(e);
                                        i = i3;
                                    }
                                }
                            } catch (Exception e3) {
                                i3 = i;
                                e = e3;
                                FileLog.e(e);
                                i = i3;
                            }
                        } else {
                            size = instance.total_unread_count;
                            i += size;
                        }
                    } else if (instance.showBadgeMuted) {
                        try {
                            size = MessagesController.getInstance(i2).allDialogs.size();
                            i3 = i;
                            i = 0;
                            while (i < size) {
                                try {
                                    if (((Dialog) MessagesController.getInstance(i2).allDialogs.get(i)).unread_count != 0) {
                                        i3++;
                                    }
                                    i++;
                                } catch (Exception e4) {
                                    e = e4;
                                    FileLog.e(e);
                                    i = i3;
                                }
                            }
                        } catch (Exception e32) {
                            i3 = i;
                            e = e32;
                            FileLog.e(e);
                            i = i3;
                        }
                    } else {
                        size = instance.pushDialogs.size();
                        i += size;
                    }
                    i = i3;
                }
            }
        }
        return i;
    }

    public /* synthetic */ void lambda$updateBadge$22$NotificationsController() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$z9M3KFS8OpgW1aPw2rnfQYb2xt0(this));
    }

    private void setBadge(int i) {
        if (this.lastBadgeCount != i) {
            this.lastBadgeCount = i;
            NotificationBadge.applyCount(i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:87:0x0152  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0151 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0151 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0152  */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r17, java.lang.String[] r18, boolean[] r19) {
        /*
        r16 = this;
        r0 = r17;
        r1 = 0;
        r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1);
        if (r2 != 0) goto L_0x0b61;
    L_0x0009:
        r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r2 == 0) goto L_0x000f;
    L_0x000d:
        goto L_0x0b61;
    L_0x000f:
        r2 = r0.messageOwner;
        r3 = r2.dialog_id;
        r2 = r2.to_id;
        r5 = r2.chat_id;
        if (r5 == 0) goto L_0x001a;
    L_0x0019:
        goto L_0x001c;
    L_0x001a:
        r5 = r2.channel_id;
    L_0x001c:
        r2 = r0.messageOwner;
        r2 = r2.to_id;
        r2 = r2.user_id;
        r6 = 1;
        if (r19 == 0) goto L_0x0027;
    L_0x0025:
        r19[r1] = r6;
    L_0x0027:
        r7 = r16.getAccountInstance();
        r7 = r7.getNotificationsSettings();
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "content_preview_";
        r8.append(r9);
        r8.append(r3);
        r8 = r8.toString();
        r8 = r7.getBoolean(r8, r6);
        r9 = r17.isFcmMessage();
        r10 = NUM; // 0x7f0d05ca float:1.874512E38 double:1.0531305097E-314;
        r11 = "Message";
        r12 = 27;
        r13 = 2;
        if (r9 == 0) goto L_0x00e4;
    L_0x0052:
        if (r5 != 0) goto L_0x0071;
    L_0x0054:
        if (r2 == 0) goto L_0x0071;
    L_0x0056:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 <= r12) goto L_0x005e;
    L_0x005a:
        r2 = r0.localName;
        r18[r1] = r2;
    L_0x005e:
        if (r8 == 0) goto L_0x0068;
    L_0x0060:
        r2 = "EnablePreviewAll";
        r2 = r7.getBoolean(r2, r6);
        if (r2 != 0) goto L_0x00df;
    L_0x0068:
        if (r19 == 0) goto L_0x006c;
    L_0x006a:
        r19[r1] = r1;
    L_0x006c:
        r0 = org.telegram.messenger.LocaleController.getString(r11, r10);
        return r0;
    L_0x0071:
        if (r5 == 0) goto L_0x00df;
    L_0x0073:
        r2 = r0.messageOwner;
        r2 = r2.to_id;
        r2 = r2.channel_id;
        if (r2 == 0) goto L_0x008b;
    L_0x007b:
        r2 = r17.isMegagroup();
        if (r2 == 0) goto L_0x0082;
    L_0x0081:
        goto L_0x008b;
    L_0x0082:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 <= r12) goto L_0x008f;
    L_0x0086:
        r2 = r0.localName;
        r18[r1] = r2;
        goto L_0x008f;
    L_0x008b:
        r2 = r0.localUserName;
        r18[r1] = r2;
    L_0x008f:
        if (r8 == 0) goto L_0x00a9;
    L_0x0091:
        r2 = r0.localChannel;
        if (r2 != 0) goto L_0x009d;
    L_0x0095:
        r2 = "EnablePreviewGroup";
        r2 = r7.getBoolean(r2, r6);
        if (r2 == 0) goto L_0x00a9;
    L_0x009d:
        r2 = r0.localChannel;
        if (r2 == 0) goto L_0x00df;
    L_0x00a1:
        r2 = "EnablePreviewChannel";
        r2 = r7.getBoolean(r2, r6);
        if (r2 != 0) goto L_0x00df;
    L_0x00a9:
        if (r19 == 0) goto L_0x00ad;
    L_0x00ab:
        r19[r1] = r1;
    L_0x00ad:
        r2 = r17.isMegagroup();
        if (r2 != 0) goto L_0x00cb;
    L_0x00b3:
        r2 = r0.messageOwner;
        r2 = r2.to_id;
        r2 = r2.channel_id;
        if (r2 == 0) goto L_0x00cb;
    L_0x00bb:
        r2 = NUM; // 0x7f0d024a float:1.8743303E38 double:1.053130067E-314;
        r3 = new java.lang.Object[r6];
        r0 = r0.localName;
        r3[r1] = r0;
        r0 = "ChannelMessageNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x00cb:
        r2 = NUM; // 0x7f0d068c float:1.8745514E38 double:1.0531306056E-314;
        r3 = new java.lang.Object[r13];
        r4 = r0.localUserName;
        r3[r1] = r4;
        r0 = r0.localName;
        r3[r6] = r0;
        r0 = "NotificationMessageGroupNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x00df:
        r0 = r0.messageOwner;
        r0 = r0.message;
        return r0;
    L_0x00e4:
        if (r2 != 0) goto L_0x00fa;
    L_0x00e6:
        r2 = r17.isFromUser();
        if (r2 != 0) goto L_0x00f5;
    L_0x00ec:
        r2 = r17.getId();
        if (r2 >= 0) goto L_0x00f3;
    L_0x00f2:
        goto L_0x00f5;
    L_0x00f3:
        r2 = -r5;
        goto L_0x0108;
    L_0x00f5:
        r2 = r0.messageOwner;
        r2 = r2.from_id;
        goto L_0x0108;
    L_0x00fa:
        r9 = r16.getUserConfig();
        r9 = r9.getClientUserId();
        if (r2 != r9) goto L_0x0108;
    L_0x0104:
        r2 = r0.messageOwner;
        r2 = r2.from_id;
    L_0x0108:
        r14 = 0;
        r9 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1));
        if (r9 != 0) goto L_0x0116;
    L_0x010e:
        if (r5 == 0) goto L_0x0113;
    L_0x0110:
        r3 = -r5;
        r3 = (long) r3;
        goto L_0x0116;
    L_0x0113:
        if (r2 == 0) goto L_0x0116;
    L_0x0115:
        r3 = (long) r2;
    L_0x0116:
        r9 = 0;
        if (r2 <= 0) goto L_0x013a;
    L_0x0119:
        r14 = r16.getMessagesController();
        r15 = java.lang.Integer.valueOf(r2);
        r14 = r14.getUser(r15);
        if (r14 == 0) goto L_0x014e;
    L_0x0127:
        r14 = org.telegram.messenger.UserObject.getUserName(r14);
        if (r5 == 0) goto L_0x0130;
    L_0x012d:
        r18[r1] = r14;
        goto L_0x014f;
    L_0x0130:
        r15 = android.os.Build.VERSION.SDK_INT;
        if (r15 <= r12) goto L_0x0137;
    L_0x0134:
        r18[r1] = r14;
        goto L_0x014f;
    L_0x0137:
        r18[r1] = r9;
        goto L_0x014f;
    L_0x013a:
        r14 = r16.getMessagesController();
        r15 = -r2;
        r15 = java.lang.Integer.valueOf(r15);
        r14 = r14.getChat(r15);
        if (r14 == 0) goto L_0x014e;
    L_0x0149:
        r14 = r14.title;
        r18[r1] = r14;
        goto L_0x014f;
    L_0x014e:
        r14 = r9;
    L_0x014f:
        if (r14 != 0) goto L_0x0152;
    L_0x0151:
        return r9;
    L_0x0152:
        if (r5 == 0) goto L_0x0174;
    L_0x0154:
        r15 = r16.getMessagesController();
        r10 = java.lang.Integer.valueOf(r5);
        r10 = r15.getChat(r10);
        if (r10 != 0) goto L_0x0163;
    L_0x0162:
        return r9;
    L_0x0163:
        r15 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r15 == 0) goto L_0x0175;
    L_0x0169:
        r15 = r10.megagroup;
        if (r15 != 0) goto L_0x0175;
    L_0x016d:
        r15 = android.os.Build.VERSION.SDK_INT;
        if (r15 > r12) goto L_0x0175;
    L_0x0171:
        r18[r1] = r9;
        goto L_0x0175;
    L_0x0174:
        r10 = r9;
    L_0x0175:
        r4 = (int) r3;
        if (r4 != 0) goto L_0x0184;
    L_0x0178:
        r18[r1] = r9;
        r0 = NUM; // 0x7f0d0afc float:1.8747818E38 double:1.053131167E-314;
        r1 = "YouHaveNewMessage";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0184:
        r3 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r3 == 0) goto L_0x0190;
    L_0x018a:
        r3 = r10.megagroup;
        if (r3 != 0) goto L_0x0190;
    L_0x018e:
        r3 = 1;
        goto L_0x0191;
    L_0x0190:
        r3 = 0;
    L_0x0191:
        if (r8 == 0) goto L_0x0b55;
    L_0x0193:
        if (r5 != 0) goto L_0x019f;
    L_0x0195:
        if (r2 == 0) goto L_0x019f;
    L_0x0197:
        r4 = "EnablePreviewAll";
        r4 = r7.getBoolean(r4, r6);
        if (r4 != 0) goto L_0x01b5;
    L_0x019f:
        if (r5 == 0) goto L_0x0b55;
    L_0x01a1:
        if (r3 != 0) goto L_0x01ab;
    L_0x01a3:
        r4 = "EnablePreviewGroup";
        r4 = r7.getBoolean(r4, r6);
        if (r4 != 0) goto L_0x01b5;
    L_0x01ab:
        if (r3 == 0) goto L_0x0b55;
    L_0x01ad:
        r3 = "EnablePreviewChannel";
        r3 = r7.getBoolean(r3, r6);
        if (r3 == 0) goto L_0x0b55;
    L_0x01b5:
        r3 = r0.messageOwner;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        r5 = "🖼 ";
        r7 = 19;
        if (r4 == 0) goto L_0x098e;
    L_0x01c0:
        r18[r1] = r9;
        r3 = r3.action;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r4 != 0) goto L_0x0980;
    L_0x01c8:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r4 == 0) goto L_0x01ce;
    L_0x01cc:
        goto L_0x0980;
    L_0x01ce:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
        if (r4 == 0) goto L_0x01e0;
    L_0x01d2:
        r0 = NUM; // 0x7f0d066b float:1.8745447E38 double:1.0531305893E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r14;
        r1 = "NotificationContactNewPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x01e0:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
        r8 = 3;
        if (r4 == 0) goto L_0x023f;
    L_0x01e5:
        r2 = NUM; // 0x7f0d0b4f float:1.8747986E38 double:1.053131208E-314;
        r3 = new java.lang.Object[r13];
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r4 = r4.formatterYear;
        r5 = r0.messageOwner;
        r5 = r5.date;
        r9 = (long) r5;
        r11 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r9 = r9 * r11;
        r4 = r4.format(r9);
        r3[r1] = r4;
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r4 = r4.formatterDay;
        r5 = r0.messageOwner;
        r5 = r5.date;
        r9 = (long) r5;
        r9 = r9 * r11;
        r4 = r4.format(r9);
        r3[r6] = r4;
        r4 = "formatDateAtTime";
        r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3);
        r3 = NUM; // 0x7f0d06a3 float:1.874556E38 double:1.053130617E-314;
        r4 = 4;
        r4 = new java.lang.Object[r4];
        r5 = r16.getUserConfig();
        r5 = r5.getCurrentUser();
        r5 = r5.first_name;
        r4[r1] = r5;
        r4[r6] = r2;
        r0 = r0.messageOwner;
        r0 = r0.action;
        r1 = r0.title;
        r4[r13] = r1;
        r0 = r0.address;
        r4[r8] = r0;
        r0 = "NotificationUnrecognizedDevice";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4);
        return r0;
    L_0x023f:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r4 != 0) goto L_0x0979;
    L_0x0243:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
        if (r4 == 0) goto L_0x0249;
    L_0x0247:
        goto L_0x0979;
    L_0x0249:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r4 == 0) goto L_0x0263;
    L_0x024d:
        r1 = r3.reason;
        r0 = r17.isOut();
        if (r0 != 0) goto L_0x0b4a;
    L_0x0255:
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        if (r0 == 0) goto L_0x0b4a;
    L_0x0259:
        r0 = NUM; // 0x7f0d01e6 float:1.87431E38 double:1.0531300177E-314;
        r1 = "CallMessageIncomingMissed";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0263:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r4 == 0) goto L_0x0367;
    L_0x0267:
        r4 = r3.user_id;
        if (r4 != 0) goto L_0x0283;
    L_0x026b:
        r3 = r3.users;
        r3 = r3.size();
        if (r3 != r6) goto L_0x0283;
    L_0x0273:
        r3 = r0.messageOwner;
        r3 = r3.action;
        r3 = r3.users;
        r3 = r3.get(r1);
        r3 = (java.lang.Integer) r3;
        r4 = r3.intValue();
    L_0x0283:
        if (r4 == 0) goto L_0x0312;
    L_0x0285:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x02a3;
    L_0x028d:
        r0 = r10.megagroup;
        if (r0 != 0) goto L_0x02a3;
    L_0x0291:
        r0 = NUM; // 0x7f0d0219 float:1.8743204E38 double:1.053130043E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "ChannelAddedByNotification";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x02a3:
        r0 = r16.getUserConfig();
        r0 = r0.getClientUserId();
        if (r4 != r0) goto L_0x02bf;
    L_0x02ad:
        r0 = NUM; // 0x7f0d0677 float:1.8745471E38 double:1.053130595E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationInvitedToGroup";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x02bf:
        r0 = r16.getMessagesController();
        r3 = java.lang.Integer.valueOf(r4);
        r0 = r0.getUser(r3);
        if (r0 != 0) goto L_0x02ce;
    L_0x02cd:
        return r9;
    L_0x02ce:
        r3 = r0.id;
        if (r2 != r3) goto L_0x02fa;
    L_0x02d2:
        r0 = r10.megagroup;
        if (r0 == 0) goto L_0x02e8;
    L_0x02d6:
        r0 = NUM; // 0x7f0d0670 float:1.8745457E38 double:1.053130592E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationGroupAddSelfMega";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x02e8:
        r0 = NUM; // 0x7f0d066f float:1.8745455E38 double:1.0531305913E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationGroupAddSelf";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x02fa:
        r2 = NUM; // 0x7f0d066e float:1.8745453E38 double:1.053130591E-314;
        r3 = new java.lang.Object[r8];
        r3[r1] = r14;
        r1 = r10.title;
        r3[r6] = r1;
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r3[r13] = r0;
        r0 = "NotificationGroupAddMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x0312:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = 0;
    L_0x0318:
        r4 = r0.messageOwner;
        r4 = r4.action;
        r4 = r4.users;
        r4 = r4.size();
        if (r3 >= r4) goto L_0x034f;
    L_0x0324:
        r4 = r16.getMessagesController();
        r5 = r0.messageOwner;
        r5 = r5.action;
        r5 = r5.users;
        r5 = r5.get(r3);
        r5 = (java.lang.Integer) r5;
        r4 = r4.getUser(r5);
        if (r4 == 0) goto L_0x034c;
    L_0x033a:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
        r5 = r2.length();
        if (r5 == 0) goto L_0x0349;
    L_0x0344:
        r5 = ", ";
        r2.append(r5);
    L_0x0349:
        r2.append(r4);
    L_0x034c:
        r3 = r3 + 1;
        goto L_0x0318;
    L_0x034f:
        r0 = NUM; // 0x7f0d066e float:1.8745453E38 double:1.053130591E-314;
        r3 = new java.lang.Object[r8];
        r3[r1] = r14;
        r1 = r10.title;
        r3[r6] = r1;
        r1 = r2.toString();
        r3[r13] = r1;
        r1 = "NotificationGroupAddMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3);
        return r0;
    L_0x0367:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
        if (r4 == 0) goto L_0x037d;
    L_0x036b:
        r0 = NUM; // 0x7f0d0678 float:1.8745474E38 double:1.0531305957E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationInvitedToGroupByLink";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x037d:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
        if (r4 == 0) goto L_0x0393;
    L_0x0381:
        r0 = NUM; // 0x7f0d066c float:1.874545E38 double:1.05313059E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r3.title;
        r2[r6] = r1;
        r1 = "NotificationEditedGroupName";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0393:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
        if (r4 != 0) goto L_0x094b;
    L_0x0397:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
        if (r4 == 0) goto L_0x039d;
    L_0x039b:
        goto L_0x094b;
    L_0x039d:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r4 == 0) goto L_0x0406;
    L_0x03a1:
        r3 = r3.user_id;
        r4 = r16.getUserConfig();
        r4 = r4.getClientUserId();
        if (r3 != r4) goto L_0x03bf;
    L_0x03ad:
        r0 = NUM; // 0x7f0d0675 float:1.8745467E38 double:1.053130594E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationGroupKickYou";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x03bf:
        r3 = r0.messageOwner;
        r3 = r3.action;
        r3 = r3.user_id;
        if (r3 != r2) goto L_0x03d9;
    L_0x03c7:
        r0 = NUM; // 0x7f0d0676 float:1.874547E38 double:1.0531305947E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationGroupLeftMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x03d9:
        r2 = r16.getMessagesController();
        r0 = r0.messageOwner;
        r0 = r0.action;
        r0 = r0.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r2.getUser(r0);
        if (r0 != 0) goto L_0x03ee;
    L_0x03ed:
        return r9;
    L_0x03ee:
        r2 = NUM; // 0x7f0d0674 float:1.8745465E38 double:1.0531305937E-314;
        r3 = new java.lang.Object[r8];
        r3[r1] = r14;
        r1 = r10.title;
        r3[r6] = r1;
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r3[r13] = r0;
        r0 = "NotificationGroupKickMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x0406:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
        if (r2 == 0) goto L_0x0411;
    L_0x040a:
        r0 = r0.messageText;
        r0 = r0.toString();
        return r0;
    L_0x0411:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r2 == 0) goto L_0x041c;
    L_0x0415:
        r0 = r0.messageText;
        r0 = r0.toString();
        return r0;
    L_0x041c:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r2 == 0) goto L_0x0430;
    L_0x0420:
        r0 = NUM; // 0x7f0d007d float:1.8742368E38 double:1.0531298393E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "ActionMigrateFromGroupNotify";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0430:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r2 == 0) goto L_0x0444;
    L_0x0434:
        r0 = NUM; // 0x7f0d007d float:1.8742368E38 double:1.0531298393E-314;
        r2 = new java.lang.Object[r6];
        r3 = r3.title;
        r2[r1] = r3;
        r1 = "ActionMigrateFromGroupNotify";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0444:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
        if (r2 == 0) goto L_0x044f;
    L_0x0448:
        r0 = r0.messageText;
        r0 = r0.toString();
        return r0;
    L_0x044f:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r2 == 0) goto L_0x0b4a;
    L_0x0453:
        r2 = 20;
        if (r10 == 0) goto L_0x06eb;
    L_0x0457:
        r3 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r3 == 0) goto L_0x0461;
    L_0x045d:
        r3 = r10.megagroup;
        if (r3 == 0) goto L_0x06eb;
    L_0x0461:
        r0 = r0.replyMessageObject;
        if (r0 != 0) goto L_0x0477;
    L_0x0465:
        r0 = NUM; // 0x7f0d0658 float:1.8745409E38 double:1.05313058E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0477:
        r3 = r0.isMusic();
        if (r3 == 0) goto L_0x048f;
    L_0x047d:
        r0 = NUM; // 0x7f0d0656 float:1.8745405E38 double:1.053130579E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedMusic";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x048f:
        r3 = r0.isVideo();
        r4 = NUM; // 0x7f0d0664 float:1.8745433E38 double:1.053130586E-314;
        r9 = "NotificationActionPinnedText";
        if (r3 == 0) goto L_0x04df;
    L_0x049a:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x04cd;
    L_0x049e:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x04cd;
    L_0x04a8:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "📹 ";
        r2.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r2 = new java.lang.Object[r8];
        r2[r1] = r14;
        r2[r6] = r0;
        r0 = r10.title;
        r2[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r4, r2);
        return r0;
    L_0x04cd:
        r0 = NUM; // 0x7f0d0666 float:1.8745437E38 double:1.053130587E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedVideo";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x04df:
        r3 = r0.isGif();
        if (r3 == 0) goto L_0x052a;
    L_0x04e5:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x0518;
    L_0x04e9:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0518;
    L_0x04f3:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "🎬 ";
        r2.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r2 = new java.lang.Object[r8];
        r2[r1] = r14;
        r2[r6] = r0;
        r0 = r10.title;
        r2[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r4, r2);
        return r0;
    L_0x0518:
        r0 = NUM; // 0x7f0d0652 float:1.8745396E38 double:1.053130577E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGif";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x052a:
        r3 = r0.isVoice();
        if (r3 == 0) goto L_0x0542;
    L_0x0530:
        r0 = NUM; // 0x7f0d0668 float:1.8745441E38 double:1.053130588E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedVoice";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0542:
        r3 = r0.isRoundVideo();
        if (r3 == 0) goto L_0x055a;
    L_0x0548:
        r0 = NUM; // 0x7f0d065e float:1.874542E38 double:1.053130583E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedRound";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x055a:
        r3 = r0.isSticker();
        if (r3 == 0) goto L_0x058c;
    L_0x0560:
        r0 = r0.getStickerEmoji();
        if (r0 == 0) goto L_0x057a;
    L_0x0566:
        r2 = NUM; // 0x7f0d0662 float:1.8745429E38 double:1.053130585E-314;
        r3 = new java.lang.Object[r8];
        r3[r1] = r14;
        r1 = r10.title;
        r3[r6] = r1;
        r3[r13] = r0;
        r0 = "NotificationActionPinnedStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x057a:
        r0 = NUM; // 0x7f0d0660 float:1.8745425E38 double:1.053130584E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x058c:
        r3 = r0.messageOwner;
        r11 = r3.media;
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r12 == 0) goto L_0x05d7;
    L_0x0594:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x05c5;
    L_0x0598:
        r2 = r3.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x05c5;
    L_0x05a0:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "📎 ";
        r2.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r2 = new java.lang.Object[r8];
        r2[r1] = r14;
        r2[r6] = r0;
        r0 = r10.title;
        r2[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r4, r2);
        return r0;
    L_0x05c5:
        r0 = NUM; // 0x7f0d0648 float:1.8745376E38 double:1.053130572E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedFile";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x05d7:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r12 != 0) goto L_0x06d9;
    L_0x05db:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r12 == 0) goto L_0x05e1;
    L_0x05df:
        goto L_0x06d9;
    L_0x05e1:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r12 == 0) goto L_0x05f7;
    L_0x05e5:
        r0 = NUM; // 0x7f0d0650 float:1.8745392E38 double:1.053130576E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGeoLive";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x05f7:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r12 == 0) goto L_0x0619;
    L_0x05fb:
        r11 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r11;
        r0 = NUM; // 0x7f0d0646 float:1.8745372E38 double:1.053130571E-314;
        r2 = new java.lang.Object[r8];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = r11.first_name;
        r3 = r11.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r1, r3);
        r2[r13] = r1;
        r1 = "NotificationActionPinnedContact2";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0619:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r12 == 0) goto L_0x0637;
    L_0x061d:
        r11 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r11;
        r0 = NUM; // 0x7f0d065c float:1.8745417E38 double:1.053130582E-314;
        r2 = new java.lang.Object[r8];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = r11.poll;
        r1 = r1.question;
        r2[r13] = r1;
        r1 = "NotificationActionPinnedPoll2";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0637:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r12 == 0) goto L_0x067b;
    L_0x063b:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x0669;
    L_0x063f:
        r2 = r3.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0669;
    L_0x0647:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r5);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r2 = new java.lang.Object[r8];
        r2[r1] = r14;
        r2[r6] = r0;
        r0 = r10.title;
        r2[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r4, r2);
        return r0;
    L_0x0669:
        r0 = NUM; // 0x7f0d065a float:1.8745413E38 double:1.053130581E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x067b:
        r3 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r3 == 0) goto L_0x0691;
    L_0x067f:
        r0 = NUM; // 0x7f0d064a float:1.874538E38 double:1.053130573E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGame";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0691:
        r3 = r0.messageText;
        if (r3 == 0) goto L_0x06c7;
    L_0x0695:
        r3 = r3.length();
        if (r3 <= 0) goto L_0x06c7;
    L_0x069b:
        r0 = r0.messageText;
        r3 = r0.length();
        if (r3 <= r2) goto L_0x06b8;
    L_0x06a3:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r0 = r0.subSequence(r1, r2);
        r3.append(r0);
        r0 = "...";
        r3.append(r0);
        r0 = r3.toString();
    L_0x06b8:
        r2 = new java.lang.Object[r8];
        r2[r1] = r14;
        r2[r6] = r0;
        r0 = r10.title;
        r2[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r4, r2);
        return r0;
    L_0x06c7:
        r0 = NUM; // 0x7f0d0658 float:1.8745409E38 double:1.05313058E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x06d9:
        r0 = NUM; // 0x7f0d064e float:1.8745388E38 double:1.053130575E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGeo";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x06eb:
        r0 = r0.replyMessageObject;
        if (r0 != 0) goto L_0x06ff;
    L_0x06ef:
        r0 = NUM; // 0x7f0d0659 float:1.874541E38 double:1.0531305804E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedNoTextChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x06ff:
        r3 = r0.isMusic();
        if (r3 == 0) goto L_0x0715;
    L_0x0705:
        r0 = NUM; // 0x7f0d0657 float:1.8745407E38 double:1.0531305794E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedMusicChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0715:
        r3 = r0.isVideo();
        r4 = NUM; // 0x7f0d0665 float:1.8745435E38 double:1.0531305863E-314;
        r8 = "NotificationActionPinnedTextChannel";
        if (r3 == 0) goto L_0x0761;
    L_0x0720:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x0751;
    L_0x0724:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0751;
    L_0x072e:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "📹 ";
        r2.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r1] = r3;
        r2[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r4, r2);
        return r0;
    L_0x0751:
        r0 = NUM; // 0x7f0d0667 float:1.874544E38 double:1.0531305873E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedVideoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0761:
        r3 = r0.isGif();
        if (r3 == 0) goto L_0x07a8;
    L_0x0767:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x0798;
    L_0x076b:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0798;
    L_0x0775:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "🎬 ";
        r2.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r1] = r3;
        r2[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r4, r2);
        return r0;
    L_0x0798:
        r0 = NUM; // 0x7f0d0653 float:1.8745398E38 double:1.0531305774E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGifChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x07a8:
        r3 = r0.isVoice();
        if (r3 == 0) goto L_0x07be;
    L_0x07ae:
        r0 = NUM; // 0x7f0d0669 float:1.8745443E38 double:1.0531305883E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedVoiceChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x07be:
        r3 = r0.isRoundVideo();
        if (r3 == 0) goto L_0x07d4;
    L_0x07c4:
        r0 = NUM; // 0x7f0d065f float:1.8745423E38 double:1.0531305834E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedRoundChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x07d4:
        r3 = r0.isSticker();
        if (r3 == 0) goto L_0x0802;
    L_0x07da:
        r0 = r0.getStickerEmoji();
        if (r0 == 0) goto L_0x07f2;
    L_0x07e0:
        r2 = NUM; // 0x7f0d0663 float:1.874543E38 double:1.0531305853E-314;
        r3 = new java.lang.Object[r13];
        r4 = r10.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = "NotificationActionPinnedStickerEmojiChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x07f2:
        r0 = NUM; // 0x7f0d0661 float:1.8745427E38 double:1.0531305844E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedStickerChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0802:
        r3 = r0.messageOwner;
        r9 = r3.media;
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r11 == 0) goto L_0x0849;
    L_0x080a:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x0839;
    L_0x080e:
        r2 = r3.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0839;
    L_0x0816:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "📎 ";
        r2.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r1] = r3;
        r2[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r4, r2);
        return r0;
    L_0x0839:
        r0 = NUM; // 0x7f0d0649 float:1.8745378E38 double:1.0531305725E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedFileChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0849:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r11 != 0) goto L_0x093b;
    L_0x084d:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r11 == 0) goto L_0x0853;
    L_0x0851:
        goto L_0x093b;
    L_0x0853:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r11 == 0) goto L_0x0867;
    L_0x0857:
        r0 = NUM; // 0x7f0d0651 float:1.8745394E38 double:1.0531305764E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGeoLiveChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0867:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r11 == 0) goto L_0x0887;
    L_0x086b:
        r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r9;
        r0 = NUM; // 0x7f0d0647 float:1.8745374E38 double:1.0531305715E-314;
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = r9.first_name;
        r3 = r9.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r1, r3);
        r2[r6] = r1;
        r1 = "NotificationActionPinnedContactChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0887:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r11 == 0) goto L_0x08a3;
    L_0x088b:
        r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r9;
        r0 = NUM; // 0x7f0d065d float:1.8745419E38 double:1.0531305824E-314;
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = r9.poll;
        r1 = r1.question;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedPollChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x08a3:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r11 == 0) goto L_0x08e3;
    L_0x08a7:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x08d3;
    L_0x08ab:
        r2 = r3.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x08d3;
    L_0x08b3:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r5);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r1] = r3;
        r2[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r4, r2);
        return r0;
    L_0x08d3:
        r0 = NUM; // 0x7f0d065b float:1.8745415E38 double:1.0531305814E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedPhotoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x08e3:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r3 == 0) goto L_0x08f7;
    L_0x08e7:
        r0 = NUM; // 0x7f0d064b float:1.8745382E38 double:1.0531305735E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGameChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x08f7:
        r3 = r0.messageText;
        if (r3 == 0) goto L_0x092b;
    L_0x08fb:
        r3 = r3.length();
        if (r3 <= 0) goto L_0x092b;
    L_0x0901:
        r0 = r0.messageText;
        r3 = r0.length();
        if (r3 <= r2) goto L_0x091e;
    L_0x0909:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r0 = r0.subSequence(r1, r2);
        r3.append(r0);
        r0 = "...";
        r3.append(r0);
        r0 = r3.toString();
    L_0x091e:
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r1] = r3;
        r2[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r4, r2);
        return r0;
    L_0x092b:
        r0 = NUM; // 0x7f0d0659 float:1.874541E38 double:1.0531305804E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedNoTextChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x093b:
        r0 = NUM; // 0x7f0d064f float:1.874539E38 double:1.0531305755E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGeoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x094b:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x0967;
    L_0x0953:
        r0 = r10.megagroup;
        if (r0 != 0) goto L_0x0967;
    L_0x0957:
        r0 = NUM; // 0x7f0d0258 float:1.8743332E38 double:1.053130074E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "ChannelPhotoEditNotification";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0967:
        r0 = NUM; // 0x7f0d066d float:1.8745451E38 double:1.0531305903E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationEditedGroupPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0979:
        r0 = r0.messageText;
        r0 = r0.toString();
        return r0;
    L_0x0980:
        r0 = NUM; // 0x7f0d066a float:1.8745445E38 double:1.053130589E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r14;
        r1 = "NotificationContactJoined";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x098e:
        r1 = r17.isMediaEmpty();
        if (r1 == 0) goto L_0x09ab;
    L_0x0994:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x09a3;
    L_0x099e:
        r0 = r0.messageOwner;
        r0 = r0.message;
        return r0;
    L_0x09a3:
        r0 = NUM; // 0x7f0d05ca float:1.874512E38 double:1.0531305097E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r11, r0);
        return r0;
    L_0x09ab:
        r1 = r0.messageOwner;
        r2 = r1.media;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r2 == 0) goto L_0x09ef;
    L_0x09b3:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x09d3;
    L_0x09b7:
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x09d3;
    L_0x09bf:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r5);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x09d3:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x09e5;
    L_0x09db:
        r0 = NUM; // 0x7f0d013c float:1.8742756E38 double:1.0531299337E-314;
        r1 = "AttachDestructingPhoto";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x09e5:
        r0 = NUM; // 0x7f0d014b float:1.8742786E38 double:1.053129941E-314;
        r1 = "AttachPhoto";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x09ef:
        r1 = r17.isVideo();
        if (r1 == 0) goto L_0x0a36;
    L_0x09f5:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0a1a;
    L_0x09f9:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0a1a;
    L_0x0a03:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "📹 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0a1a:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0a2c;
    L_0x0a22:
        r0 = NUM; // 0x7f0d013d float:1.8742758E38 double:1.053129934E-314;
        r1 = "AttachDestructingVideo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a2c:
        r0 = NUM; // 0x7f0d0151 float:1.8742798E38 double:1.053129944E-314;
        r1 = "AttachVideo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a36:
        r1 = r17.isGame();
        if (r1 == 0) goto L_0x0a46;
    L_0x0a3c:
        r0 = NUM; // 0x7f0d013f float:1.8742762E38 double:1.053129935E-314;
        r1 = "AttachGame";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a46:
        r1 = r17.isVoice();
        if (r1 == 0) goto L_0x0a56;
    L_0x0a4c:
        r0 = NUM; // 0x7f0d0139 float:1.874275E38 double:1.053129932E-314;
        r1 = "AttachAudio";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a56:
        r1 = r17.isRoundVideo();
        if (r1 == 0) goto L_0x0a66;
    L_0x0a5c:
        r0 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r1 = "AttachRound";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a66:
        r1 = r17.isMusic();
        if (r1 == 0) goto L_0x0a76;
    L_0x0a6c:
        r0 = NUM; // 0x7f0d014a float:1.8742784E38 double:1.0531299406E-314;
        r1 = "AttachMusic";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a76:
        r1 = r0.messageOwner;
        r1 = r1.media;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r2 == 0) goto L_0x0a88;
    L_0x0a7e:
        r0 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r1 = "AttachContact";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a88:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r2 == 0) goto L_0x0a96;
    L_0x0a8c:
        r0 = NUM; // 0x7f0d084f float:1.8746429E38 double:1.0531308284E-314;
        r1 = "Poll";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a96:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r2 != 0) goto L_0x0b4b;
    L_0x0a9a:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r2 == 0) goto L_0x0aa0;
    L_0x0a9e:
        goto L_0x0b4b;
    L_0x0aa0:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r2 == 0) goto L_0x0aae;
    L_0x0aa4:
        r0 = NUM; // 0x7f0d0145 float:1.8742774E38 double:1.053129938E-314;
        r1 = "AttachLiveLocation";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0aae:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r1 == 0) goto L_0x0b4a;
    L_0x0ab2:
        r1 = r17.isSticker();
        if (r1 == 0) goto L_0x0ae6;
    L_0x0ab8:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x0adc;
    L_0x0abe:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r0 = " ";
        r1.append(r0);
        r0 = NUM; // 0x7f0d014e float:1.8742792E38 double:1.0531299426E-314;
        r2 = "AttachSticker";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0adc:
        r0 = NUM; // 0x7f0d014e float:1.8742792E38 double:1.0531299426E-314;
        r1 = "AttachSticker";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0ae6:
        r1 = r17.isGif();
        if (r1 == 0) goto L_0x0b1b;
    L_0x0aec:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0b11;
    L_0x0af0:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0b11;
    L_0x0afa:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "🎬 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0b11:
        r0 = NUM; // 0x7f0d0140 float:1.8742764E38 double:1.0531299356E-314;
        r1 = "AttachGif";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0b1b:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0b40;
    L_0x0b1f:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0b40;
    L_0x0b29:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "📎 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0b40:
        r0 = NUM; // 0x7f0d013e float:1.874276E38 double:1.0531299347E-314;
        r1 = "AttachDocument";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0b4a:
        return r9;
    L_0x0b4b:
        r0 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r1 = "AttachLocation";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0b55:
        if (r19 == 0) goto L_0x0b59;
    L_0x0b57:
        r19[r1] = r1;
    L_0x0b59:
        r0 = NUM; // 0x7f0d05ca float:1.874512E38 double:1.0531305097E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r11, r0);
        return r0;
    L_0x0b61:
        r0 = NUM; // 0x7f0d0afc float:1.8747818E38 double:1.053131167E-314;
        r1 = "YouHaveNewMessage";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:70:0x0124  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0123 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0123 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0124  */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r17, boolean r18, boolean[] r19, boolean[] r20) {
        /*
        r16 = this;
        r0 = r17;
        r1 = 0;
        r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1);
        if (r2 != 0) goto L_0x117d;
    L_0x0009:
        r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r2 == 0) goto L_0x000f;
    L_0x000d:
        goto L_0x117d;
    L_0x000f:
        r2 = r0.messageOwner;
        r3 = r2.dialog_id;
        r2 = r2.to_id;
        r5 = r2.chat_id;
        if (r5 == 0) goto L_0x001a;
    L_0x0019:
        goto L_0x001c;
    L_0x001a:
        r5 = r2.channel_id;
    L_0x001c:
        r2 = r0.messageOwner;
        r2 = r2.to_id;
        r2 = r2.user_id;
        r6 = 1;
        if (r20 == 0) goto L_0x0027;
    L_0x0025:
        r20[r1] = r6;
    L_0x0027:
        r7 = r16.getAccountInstance();
        r7 = r7.getNotificationsSettings();
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "content_preview_";
        r8.append(r9);
        r8.append(r3);
        r8 = r8.toString();
        r8 = r7.getBoolean(r8, r6);
        r9 = r17.isFcmMessage();
        r10 = 2;
        if (r9 == 0) goto L_0x00c6;
    L_0x004b:
        if (r5 != 0) goto L_0x006d;
    L_0x004d:
        if (r2 == 0) goto L_0x006d;
    L_0x004f:
        if (r8 == 0) goto L_0x0059;
    L_0x0051:
        r2 = "EnablePreviewAll";
        r2 = r7.getBoolean(r2, r6);
        if (r2 != 0) goto L_0x00bf;
    L_0x0059:
        if (r20 == 0) goto L_0x005d;
    L_0x005b:
        r20[r1] = r1;
    L_0x005d:
        r2 = NUM; // 0x7f0d0698 float:1.8745538E38 double:1.0531306115E-314;
        r3 = new java.lang.Object[r6];
        r0 = r0.localName;
        r3[r1] = r0;
        r0 = "NotificationMessageNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x006d:
        if (r5 == 0) goto L_0x00bf;
    L_0x006f:
        if (r8 == 0) goto L_0x0089;
    L_0x0071:
        r2 = r0.localChannel;
        if (r2 != 0) goto L_0x007d;
    L_0x0075:
        r2 = "EnablePreviewGroup";
        r2 = r7.getBoolean(r2, r6);
        if (r2 == 0) goto L_0x0089;
    L_0x007d:
        r2 = r0.localChannel;
        if (r2 == 0) goto L_0x00bf;
    L_0x0081:
        r2 = "EnablePreviewChannel";
        r2 = r7.getBoolean(r2, r6);
        if (r2 != 0) goto L_0x00bf;
    L_0x0089:
        if (r20 == 0) goto L_0x008d;
    L_0x008b:
        r20[r1] = r1;
    L_0x008d:
        r2 = r17.isMegagroup();
        if (r2 != 0) goto L_0x00ab;
    L_0x0093:
        r2 = r0.messageOwner;
        r2 = r2.to_id;
        r2 = r2.channel_id;
        if (r2 == 0) goto L_0x00ab;
    L_0x009b:
        r2 = NUM; // 0x7f0d024a float:1.8743303E38 double:1.053130067E-314;
        r3 = new java.lang.Object[r6];
        r0 = r0.localName;
        r3[r1] = r0;
        r0 = "ChannelMessageNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x00ab:
        r2 = NUM; // 0x7f0d068c float:1.8745514E38 double:1.0531306056E-314;
        r3 = new java.lang.Object[r10];
        r4 = r0.localUserName;
        r3[r1] = r4;
        r0 = r0.localName;
        r3[r6] = r0;
        r0 = "NotificationMessageGroupNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x00bf:
        r19[r1] = r6;
        r0 = r0.messageText;
        r0 = (java.lang.String) r0;
        return r0;
    L_0x00c6:
        if (r2 != 0) goto L_0x00dc;
    L_0x00c8:
        r2 = r17.isFromUser();
        if (r2 != 0) goto L_0x00d7;
    L_0x00ce:
        r2 = r17.getId();
        if (r2 >= 0) goto L_0x00d5;
    L_0x00d4:
        goto L_0x00d7;
    L_0x00d5:
        r2 = -r5;
        goto L_0x00ea;
    L_0x00d7:
        r2 = r0.messageOwner;
        r2 = r2.from_id;
        goto L_0x00ea;
    L_0x00dc:
        r9 = r16.getUserConfig();
        r9 = r9.getClientUserId();
        if (r2 != r9) goto L_0x00ea;
    L_0x00e6:
        r2 = r0.messageOwner;
        r2 = r2.from_id;
    L_0x00ea:
        r11 = 0;
        r9 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1));
        if (r9 != 0) goto L_0x00f8;
    L_0x00f0:
        if (r5 == 0) goto L_0x00f5;
    L_0x00f2:
        r3 = -r5;
        r3 = (long) r3;
        goto L_0x00f8;
    L_0x00f5:
        if (r2 == 0) goto L_0x00f8;
    L_0x00f7:
        r3 = (long) r2;
    L_0x00f8:
        r9 = 0;
        if (r2 <= 0) goto L_0x010e;
    L_0x00fb:
        r11 = r16.getMessagesController();
        r12 = java.lang.Integer.valueOf(r2);
        r11 = r11.getUser(r12);
        if (r11 == 0) goto L_0x0120;
    L_0x0109:
        r11 = org.telegram.messenger.UserObject.getUserName(r11);
        goto L_0x0121;
    L_0x010e:
        r11 = r16.getMessagesController();
        r12 = -r2;
        r12 = java.lang.Integer.valueOf(r12);
        r11 = r11.getChat(r12);
        if (r11 == 0) goto L_0x0120;
    L_0x011d:
        r11 = r11.title;
        goto L_0x0121;
    L_0x0120:
        r11 = r9;
    L_0x0121:
        if (r11 != 0) goto L_0x0124;
    L_0x0123:
        return r9;
    L_0x0124:
        if (r5 == 0) goto L_0x0135;
    L_0x0126:
        r12 = r16.getMessagesController();
        r13 = java.lang.Integer.valueOf(r5);
        r12 = r12.getChat(r13);
        if (r12 != 0) goto L_0x0136;
    L_0x0134:
        return r9;
    L_0x0135:
        r12 = r9;
    L_0x0136:
        r4 = (int) r3;
        if (r4 != 0) goto L_0x0144;
    L_0x0139:
        r0 = NUM; // 0x7f0d0afc float:1.8747818E38 double:1.053131167E-314;
        r1 = "YouHaveNewMessage";
        r9 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x117c;
    L_0x0144:
        r3 = "🎬 ";
        r4 = "📎 ";
        r13 = "📹 ";
        r14 = "🖼 ";
        r9 = "NotificationMessageText";
        r15 = 3;
        if (r5 != 0) goto L_0x04c7;
    L_0x0155:
        if (r2 == 0) goto L_0x04c7;
    L_0x0157:
        if (r8 == 0) goto L_0x04b4;
    L_0x0159:
        r2 = "EnablePreviewAll";
        r2 = r7.getBoolean(r2, r6);
        if (r2 == 0) goto L_0x04b4;
    L_0x0161:
        r2 = r0.messageOwner;
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r5 == 0) goto L_0x0223;
    L_0x0167:
        r2 = r2.action;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r3 != 0) goto L_0x0214;
    L_0x016d:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r3 == 0) goto L_0x0173;
    L_0x0171:
        goto L_0x0214;
    L_0x0173:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
        if (r3 == 0) goto L_0x0186;
    L_0x0177:
        r0 = NUM; // 0x7f0d066b float:1.8745447E38 double:1.0531305893E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationContactNewPhoto";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0186:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
        if (r3 == 0) goto L_0x01e5;
    L_0x018a:
        r2 = NUM; // 0x7f0d0b4f float:1.8747986E38 double:1.053131208E-314;
        r3 = new java.lang.Object[r10];
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r4 = r4.formatterYear;
        r5 = r0.messageOwner;
        r5 = r5.date;
        r7 = (long) r5;
        r11 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7 = r7 * r11;
        r4 = r4.format(r7);
        r3[r1] = r4;
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r4 = r4.formatterDay;
        r5 = r0.messageOwner;
        r5 = r5.date;
        r7 = (long) r5;
        r7 = r7 * r11;
        r4 = r4.format(r7);
        r3[r6] = r4;
        r4 = "formatDateAtTime";
        r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3);
        r3 = NUM; // 0x7f0d06a3 float:1.874556E38 double:1.053130617E-314;
        r4 = 4;
        r4 = new java.lang.Object[r4];
        r5 = r16.getUserConfig();
        r5 = r5.getCurrentUser();
        r5 = r5.first_name;
        r4[r1] = r5;
        r4[r6] = r2;
        r0 = r0.messageOwner;
        r0 = r0.action;
        r1 = r0.title;
        r4[r10] = r1;
        r0 = r0.address;
        r4[r15] = r0;
        r0 = "NotificationUnrecognizedDevice";
        r9 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4);
        goto L_0x117c;
    L_0x01e5:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r1 != 0) goto L_0x020c;
    L_0x01e9:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
        if (r1 == 0) goto L_0x01ee;
    L_0x01ed:
        goto L_0x020c;
    L_0x01ee:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r1 == 0) goto L_0x117a;
    L_0x01f2:
        r1 = r2.reason;
        r0 = r17.isOut();
        if (r0 != 0) goto L_0x0209;
    L_0x01fa:
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        if (r0 == 0) goto L_0x0209;
    L_0x01fe:
        r0 = NUM; // 0x7f0d01e6 float:1.87431E38 double:1.0531300177E-314;
        r1 = "CallMessageIncomingMissed";
        r9 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x117c;
    L_0x0209:
        r9 = 0;
        goto L_0x117c;
    L_0x020c:
        r0 = r0.messageText;
        r9 = r0.toString();
        goto L_0x117c;
    L_0x0214:
        r0 = NUM; // 0x7f0d066a float:1.8745445E38 double:1.053130589E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationContactJoined";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0223:
        r2 = r17.isMediaEmpty();
        if (r2 == 0) goto L_0x0268;
    L_0x0229:
        if (r18 != 0) goto L_0x0259;
    L_0x022b:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x024a;
    L_0x0235:
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2[r6] = r0;
        r0 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r0, r2);
        r19[r1] = r6;
        goto L_0x117c;
    L_0x024a:
        r0 = NUM; // 0x7f0d0698 float:1.8745538E38 double:1.0531306115E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageNoText";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0259:
        r0 = NUM; // 0x7f0d0698 float:1.8745538E38 double:1.0531306115E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageNoText";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0268:
        r2 = r0.messageOwner;
        r5 = r2.media;
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r5 == 0) goto L_0x02ca;
    L_0x0270:
        if (r18 != 0) goto L_0x02a4;
    L_0x0272:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r3 < r4) goto L_0x02a4;
    L_0x0278:
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x02a4;
    L_0x0280:
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r14);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r0, r2);
        r19[r1] = r6;
        goto L_0x117c;
    L_0x02a4:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x02bb;
    L_0x02ac:
        r0 = NUM; // 0x7f0d069c float:1.8745547E38 double:1.0531306135E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageSDPhoto";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x02bb:
        r0 = NUM; // 0x7f0d0699 float:1.874554E38 double:1.053130612E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessagePhoto";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x02ca:
        r2 = r17.isVideo();
        if (r2 == 0) goto L_0x032c;
    L_0x02d0:
        if (r18 != 0) goto L_0x0306;
    L_0x02d2:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0306;
    L_0x02d8:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0306;
    L_0x02e2:
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r13);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r0, r2);
        r19[r1] = r6;
        goto L_0x117c;
    L_0x0306:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x031d;
    L_0x030e:
        r0 = NUM; // 0x7f0d069d float:1.8745549E38 double:1.053130614E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageSDVideo";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x031d:
        r0 = NUM; // 0x7f0d06a1 float:1.8745557E38 double:1.053130616E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageVideo";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x032c:
        r2 = r17.isGame();
        if (r2 == 0) goto L_0x034b;
    L_0x0332:
        r2 = NUM; // 0x7f0d067f float:1.8745488E38 double:1.053130599E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r11;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.game;
        r0 = r0.title;
        r3[r6] = r0;
        r0 = "NotificationMessageGame";
        r9 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x117c;
    L_0x034b:
        r2 = r17.isVoice();
        if (r2 == 0) goto L_0x0360;
    L_0x0351:
        r0 = NUM; // 0x7f0d067a float:1.8745478E38 double:1.0531305967E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageAudio";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0360:
        r2 = r17.isRoundVideo();
        if (r2 == 0) goto L_0x0375;
    L_0x0366:
        r0 = NUM; // 0x7f0d069b float:1.8745544E38 double:1.053130613E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageRound";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0375:
        r2 = r17.isMusic();
        if (r2 == 0) goto L_0x038a;
    L_0x037b:
        r0 = NUM; // 0x7f0d0697 float:1.8745536E38 double:1.053130611E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageMusic";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x038a:
        r2 = r0.messageOwner;
        r2 = r2.media;
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r5 == 0) goto L_0x03ad;
    L_0x0392:
        r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r2;
        r0 = NUM; // 0x7f0d067b float:1.874548E38 double:1.053130597E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r11;
        r1 = r2.first_name;
        r2 = r2.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r1, r2);
        r3[r6] = r1;
        r1 = "NotificationMessageContact2";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3);
        goto L_0x117c;
    L_0x03ad:
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r5 == 0) goto L_0x03c8;
    L_0x03b1:
        r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r2;
        r0 = NUM; // 0x7f0d069a float:1.8745542E38 double:1.0531306125E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r11;
        r1 = r2.poll;
        r1 = r1.question;
        r3[r6] = r1;
        r1 = "NotificationMessagePoll2";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3);
        goto L_0x117c;
    L_0x03c8:
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r5 != 0) goto L_0x04a5;
    L_0x03cc:
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r5 == 0) goto L_0x03d2;
    L_0x03d0:
        goto L_0x04a5;
    L_0x03d2:
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r5 == 0) goto L_0x03e5;
    L_0x03d6:
        r0 = NUM; // 0x7f0d0695 float:1.8745532E38 double:1.05313061E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageLiveLocation";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x03e5:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r2 == 0) goto L_0x117a;
    L_0x03e9:
        r2 = r17.isSticker();
        if (r2 == 0) goto L_0x0415;
    L_0x03ef:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x0405;
    L_0x03f5:
        r2 = NUM; // 0x7f0d069f float:1.8745553E38 double:1.053130615E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r11;
        r3[r6] = r0;
        r0 = "NotificationMessageStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x0405:
        r0 = NUM; // 0x7f0d069e float:1.874555E38 double:1.0531306145E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
    L_0x0412:
        r9 = r0;
        goto L_0x117c;
    L_0x0415:
        r2 = r17.isGif();
        if (r2 == 0) goto L_0x0460;
    L_0x041b:
        if (r18 != 0) goto L_0x0451;
    L_0x041d:
        r2 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r2 < r4) goto L_0x0451;
    L_0x0423:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0451;
    L_0x042d:
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r4.append(r0);
        r0 = r4.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r0, r2);
        r19[r1] = r6;
        goto L_0x117c;
    L_0x0451:
        r0 = NUM; // 0x7f0d0681 float:1.8745492E38 double:1.0531306E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageGif";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0460:
        if (r18 != 0) goto L_0x0496;
    L_0x0462:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0496;
    L_0x0468:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0496;
    L_0x0472:
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r4);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r0, r2);
        r19[r1] = r6;
        goto L_0x117c;
    L_0x0496:
        r0 = NUM; // 0x7f0d067c float:1.8745482E38 double:1.0531305977E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageDocument";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x04a5:
        r0 = NUM; // 0x7f0d0696 float:1.8745534E38 double:1.0531306105E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageMap";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x04b4:
        if (r20 == 0) goto L_0x04b8;
    L_0x04b6:
        r20[r1] = r1;
    L_0x04b8:
        r0 = NUM; // 0x7f0d0698 float:1.8745538E38 double:1.0531306115E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "NotificationMessageNoText";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x04c7:
        if (r5 == 0) goto L_0x117a;
    L_0x04c9:
        r5 = org.telegram.messenger.ChatObject.isChannel(r12);
        if (r5 == 0) goto L_0x04d5;
    L_0x04cf:
        r5 = r12.megagroup;
        if (r5 != 0) goto L_0x04d5;
    L_0x04d3:
        r5 = 1;
        goto L_0x04d6;
    L_0x04d5:
        r5 = 0;
    L_0x04d6:
        if (r8 == 0) goto L_0x114c;
    L_0x04d8:
        if (r5 != 0) goto L_0x04e2;
    L_0x04da:
        r8 = "EnablePreviewGroup";
        r8 = r7.getBoolean(r8, r6);
        if (r8 != 0) goto L_0x04ec;
    L_0x04e2:
        if (r5 == 0) goto L_0x114c;
    L_0x04e4:
        r5 = "EnablePreviewChannel";
        r5 = r7.getBoolean(r5, r6);
        if (r5 == 0) goto L_0x114c;
    L_0x04ec:
        r5 = r0.messageOwner;
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r7 == 0) goto L_0x0CLASSNAME;
    L_0x04f2:
        r5 = r5.action;
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r7 == 0) goto L_0x05ff;
    L_0x04f8:
        r3 = r5.user_id;
        if (r3 != 0) goto L_0x0514;
    L_0x04fc:
        r4 = r5.users;
        r4 = r4.size();
        if (r4 != r6) goto L_0x0514;
    L_0x0504:
        r3 = r0.messageOwner;
        r3 = r3.action;
        r3 = r3.users;
        r3 = r3.get(r1);
        r3 = (java.lang.Integer) r3;
        r3 = r3.intValue();
    L_0x0514:
        if (r3 == 0) goto L_0x05a9;
    L_0x0516:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x0535;
    L_0x051e:
        r0 = r12.megagroup;
        if (r0 != 0) goto L_0x0535;
    L_0x0522:
        r0 = NUM; // 0x7f0d0219 float:1.8743204E38 double:1.053130043E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "ChannelAddedByNotification";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0535:
        r0 = r16.getUserConfig();
        r0 = r0.getClientUserId();
        if (r3 != r0) goto L_0x0552;
    L_0x053f:
        r0 = NUM; // 0x7f0d0677 float:1.8745471E38 double:1.053130595E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationInvitedToGroup";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0552:
        r0 = r16.getMessagesController();
        r3 = java.lang.Integer.valueOf(r3);
        r0 = r0.getUser(r3);
        if (r0 != 0) goto L_0x0562;
    L_0x0560:
        r3 = 0;
        return r3;
    L_0x0562:
        r3 = r0.id;
        if (r2 != r3) goto L_0x0590;
    L_0x0566:
        r0 = r12.megagroup;
        if (r0 == 0) goto L_0x057d;
    L_0x056a:
        r0 = NUM; // 0x7f0d0670 float:1.8745457E38 double:1.053130592E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationGroupAddSelfMega";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x057d:
        r0 = NUM; // 0x7f0d066f float:1.8745455E38 double:1.0531305913E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationGroupAddSelf";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0590:
        r2 = NUM; // 0x7f0d066e float:1.8745453E38 double:1.053130591E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r1 = r12.title;
        r3[r6] = r1;
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r3[r10] = r0;
        r0 = "NotificationGroupAddMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x05a9:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = 0;
    L_0x05af:
        r4 = r0.messageOwner;
        r4 = r4.action;
        r4 = r4.users;
        r4 = r4.size();
        if (r3 >= r4) goto L_0x05e6;
    L_0x05bb:
        r4 = r16.getMessagesController();
        r5 = r0.messageOwner;
        r5 = r5.action;
        r5 = r5.users;
        r5 = r5.get(r3);
        r5 = (java.lang.Integer) r5;
        r4 = r4.getUser(r5);
        if (r4 == 0) goto L_0x05e3;
    L_0x05d1:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
        r5 = r2.length();
        if (r5 == 0) goto L_0x05e0;
    L_0x05db:
        r5 = ", ";
        r2.append(r5);
    L_0x05e0:
        r2.append(r4);
    L_0x05e3:
        r3 = r3 + 1;
        goto L_0x05af;
    L_0x05e6:
        r0 = NUM; // 0x7f0d066e float:1.8745453E38 double:1.053130591E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r1 = r12.title;
        r3[r6] = r1;
        r1 = r2.toString();
        r3[r10] = r1;
        r1 = "NotificationGroupAddMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3);
        goto L_0x0412;
    L_0x05ff:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
        if (r7 == 0) goto L_0x0616;
    L_0x0603:
        r0 = NUM; // 0x7f0d0678 float:1.8745474E38 double:1.0531305957E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationInvitedToGroupByLink";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0616:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
        if (r7 == 0) goto L_0x062d;
    L_0x061a:
        r0 = NUM; // 0x7f0d066c float:1.874545E38 double:1.05313059E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r5.title;
        r2[r6] = r1;
        r1 = "NotificationEditedGroupName";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x062d:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
        if (r7 != 0) goto L_0x0CLASSNAME;
    L_0x0631:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
        if (r7 == 0) goto L_0x0637;
    L_0x0635:
        goto L_0x0CLASSNAME;
    L_0x0637:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r7 == 0) goto L_0x06a4;
    L_0x063b:
        r3 = r5.user_id;
        r4 = r16.getUserConfig();
        r4 = r4.getClientUserId();
        if (r3 != r4) goto L_0x065a;
    L_0x0647:
        r0 = NUM; // 0x7f0d0675 float:1.8745467E38 double:1.053130594E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationGroupKickYou";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x065a:
        r3 = r0.messageOwner;
        r3 = r3.action;
        r3 = r3.user_id;
        if (r3 != r2) goto L_0x0675;
    L_0x0662:
        r0 = NUM; // 0x7f0d0676 float:1.874547E38 double:1.0531305947E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationGroupLeftMember";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0675:
        r2 = r16.getMessagesController();
        r0 = r0.messageOwner;
        r0 = r0.action;
        r0 = r0.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r2.getUser(r0);
        if (r0 != 0) goto L_0x068b;
    L_0x0689:
        r2 = 0;
        return r2;
    L_0x068b:
        r2 = NUM; // 0x7f0d0674 float:1.8745465E38 double:1.0531305937E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r1 = r12.title;
        r3[r6] = r1;
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r3[r10] = r0;
        r0 = "NotificationGroupKickMember";
        r9 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x117c;
    L_0x06a4:
        r2 = 0;
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
        if (r7 == 0) goto L_0x06b1;
    L_0x06a9:
        r0 = r0.messageText;
        r9 = r0.toString();
        goto L_0x117c;
    L_0x06b1:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r7 == 0) goto L_0x06bd;
    L_0x06b5:
        r0 = r0.messageText;
        r9 = r0.toString();
        goto L_0x117c;
    L_0x06bd:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r7 == 0) goto L_0x06d2;
    L_0x06c1:
        r0 = NUM; // 0x7f0d007d float:1.8742368E38 double:1.0531298393E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "ActionMigrateFromGroupNotify";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x06d2:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r7 == 0) goto L_0x06e7;
    L_0x06d6:
        r0 = NUM; // 0x7f0d007d float:1.8742368E38 double:1.0531298393E-314;
        r2 = new java.lang.Object[r6];
        r3 = r5.title;
        r2[r1] = r3;
        r1 = "ActionMigrateFromGroupNotify";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x06e7:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
        if (r7 == 0) goto L_0x06f3;
    L_0x06eb:
        r0 = r0.messageText;
        r9 = r0.toString();
        goto L_0x117c;
    L_0x06f3:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r7 == 0) goto L_0x0CLASSNAME;
    L_0x06f7:
        if (r12 == 0) goto L_0x09bd;
    L_0x06f9:
        r2 = org.telegram.messenger.ChatObject.isChannel(r12);
        if (r2 == 0) goto L_0x0703;
    L_0x06ff:
        r2 = r12.megagroup;
        if (r2 == 0) goto L_0x09bd;
    L_0x0703:
        r2 = r0.replyMessageObject;
        if (r2 != 0) goto L_0x071a;
    L_0x0707:
        r0 = NUM; // 0x7f0d0658 float:1.8745409E38 double:1.05313058E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedNoText";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x071a:
        r5 = r2.isMusic();
        if (r5 == 0) goto L_0x0733;
    L_0x0720:
        r0 = NUM; // 0x7f0d0656 float:1.8745405E38 double:1.053130579E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedMusic";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0733:
        r5 = r2.isVideo();
        if (r5 == 0) goto L_0x0784;
    L_0x0739:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r0 < r3) goto L_0x0771;
    L_0x073f:
        r0 = r2.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0771;
    L_0x0749:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r13);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0d0664 float:1.8745433E38 double:1.053130586E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r3[r6] = r0;
        r0 = r12.title;
        r3[r10] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x0771:
        r0 = NUM; // 0x7f0d0666 float:1.8745437E38 double:1.053130587E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedVideo";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0784:
        r5 = r2.isGif();
        if (r5 == 0) goto L_0x07d5;
    L_0x078a:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r0 < r4) goto L_0x07c2;
    L_0x0790:
        r0 = r2.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x07c2;
    L_0x079a:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0d0664 float:1.8745433E38 double:1.053130586E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r3[r6] = r0;
        r0 = r12.title;
        r3[r10] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x07c2:
        r0 = NUM; // 0x7f0d0652 float:1.8745396E38 double:1.053130577E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGif";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x07d5:
        r3 = r2.isVoice();
        if (r3 == 0) goto L_0x07ee;
    L_0x07db:
        r0 = NUM; // 0x7f0d0668 float:1.8745441E38 double:1.053130588E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedVoice";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x07ee:
        r3 = r2.isRoundVideo();
        if (r3 == 0) goto L_0x0807;
    L_0x07f4:
        r0 = NUM; // 0x7f0d065e float:1.874542E38 double:1.053130583E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedRound";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0807:
        r3 = r2.isSticker();
        if (r3 == 0) goto L_0x083b;
    L_0x080d:
        r0 = r2.getStickerEmoji();
        if (r0 == 0) goto L_0x0828;
    L_0x0813:
        r2 = NUM; // 0x7f0d0662 float:1.8745429E38 double:1.053130585E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r1 = r12.title;
        r3[r6] = r1;
        r3[r10] = r0;
        r0 = "NotificationActionPinnedStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x0828:
        r0 = NUM; // 0x7f0d0660 float:1.8745425E38 double:1.053130584E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x083b:
        r3 = r2.messageOwner;
        r5 = r3.media;
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r7 == 0) goto L_0x088c;
    L_0x0843:
        r0 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r0 < r5) goto L_0x0879;
    L_0x0849:
        r0 = r3.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0879;
    L_0x0851:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r4);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0d0664 float:1.8745433E38 double:1.053130586E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r3[r6] = r0;
        r0 = r12.title;
        r3[r10] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x0879:
        r0 = NUM; // 0x7f0d0648 float:1.8745376E38 double:1.053130572E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedFile";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x088c:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r4 != 0) goto L_0x09aa;
    L_0x0890:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r4 == 0) goto L_0x0896;
    L_0x0894:
        goto L_0x09aa;
    L_0x0896:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r4 == 0) goto L_0x08ad;
    L_0x089a:
        r0 = NUM; // 0x7f0d0650 float:1.8745392E38 double:1.053130576E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGeoLive";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x08ad:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r4 == 0) goto L_0x08d4;
    L_0x08b1:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r0;
        r2 = NUM; // 0x7f0d0646 float:1.8745372E38 double:1.053130571E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r1 = r12.title;
        r3[r6] = r1;
        r1 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r1, r0);
        r3[r10] = r0;
        r0 = "NotificationActionPinnedContact2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x08d4:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r0 == 0) goto L_0x08f3;
    L_0x08d8:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5;
        r0 = NUM; // 0x7f0d065c float:1.8745417E38 double:1.053130582E-314;
        r2 = new java.lang.Object[r15];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = r5.poll;
        r1 = r1.question;
        r2[r10] = r1;
        r1 = "NotificationActionPinnedPoll2";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x08f3:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 == 0) goto L_0x0940;
    L_0x08f7:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r0 < r4) goto L_0x092d;
    L_0x08fd:
        r0 = r3.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x092d;
    L_0x0905:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r14);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0d0664 float:1.8745433E38 double:1.053130586E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r3[r6] = r0;
        r0 = r12.title;
        r3[r10] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x092d:
        r0 = NUM; // 0x7f0d065a float:1.8745413E38 double:1.053130581E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0940:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x0957;
    L_0x0944:
        r0 = NUM; // 0x7f0d064a float:1.874538E38 double:1.053130573E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGame";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0957:
        r0 = r2.messageText;
        if (r0 == 0) goto L_0x0997;
    L_0x095b:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x0997;
    L_0x0961:
        r0 = r2.messageText;
        r2 = r0.length();
        r3 = 20;
        if (r2 <= r3) goto L_0x0982;
    L_0x096b:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = 20;
        r0 = r0.subSequence(r1, r3);
        r2.append(r0);
        r0 = "...";
        r2.append(r0);
        r0 = r2.toString();
    L_0x0982:
        r2 = NUM; // 0x7f0d0664 float:1.8745433E38 double:1.053130586E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r3[r6] = r0;
        r0 = r12.title;
        r3[r10] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x0997:
        r0 = NUM; // 0x7f0d0658 float:1.8745409E38 double:1.05313058E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x09aa:
        r0 = NUM; // 0x7f0d064e float:1.8745388E38 double:1.053130575E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGeo";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x09bd:
        r2 = r0.replyMessageObject;
        if (r2 != 0) goto L_0x09d2;
    L_0x09c1:
        r0 = NUM; // 0x7f0d0659 float:1.874541E38 double:1.0531305804E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedNoTextChannel";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x09d2:
        r5 = r2.isMusic();
        if (r5 == 0) goto L_0x09e9;
    L_0x09d8:
        r0 = NUM; // 0x7f0d0657 float:1.8745407E38 double:1.0531305794E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedMusicChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x09e9:
        r5 = r2.isVideo();
        r7 = "NotificationActionPinnedTextChannel";
        if (r5 == 0) goto L_0x0a36;
    L_0x09f1:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r0 < r3) goto L_0x0a25;
    L_0x09f7:
        r0 = r2.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0a25;
    L_0x0a01:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r13);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0d0665 float:1.8745435E38 double:1.0531305863E-314;
        r3 = new java.lang.Object[r10];
        r4 = r12.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r2, r3);
        goto L_0x0412;
    L_0x0a25:
        r0 = NUM; // 0x7f0d0667 float:1.874544E38 double:1.0531305873E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedVideoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0a36:
        r5 = r2.isGif();
        if (r5 == 0) goto L_0x0a81;
    L_0x0a3c:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r0 < r4) goto L_0x0a70;
    L_0x0a42:
        r0 = r2.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0a70;
    L_0x0a4c:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0d0665 float:1.8745435E38 double:1.0531305863E-314;
        r3 = new java.lang.Object[r10];
        r4 = r12.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r2, r3);
        goto L_0x0412;
    L_0x0a70:
        r0 = NUM; // 0x7f0d0653 float:1.8745398E38 double:1.0531305774E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGifChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0a81:
        r3 = r2.isVoice();
        if (r3 == 0) goto L_0x0a98;
    L_0x0a87:
        r0 = NUM; // 0x7f0d0669 float:1.8745443E38 double:1.0531305883E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedVoiceChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0a98:
        r3 = r2.isRoundVideo();
        if (r3 == 0) goto L_0x0aaf;
    L_0x0a9e:
        r0 = NUM; // 0x7f0d065f float:1.8745423E38 double:1.0531305834E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedRoundChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0aaf:
        r3 = r2.isSticker();
        if (r3 == 0) goto L_0x0adf;
    L_0x0ab5:
        r0 = r2.getStickerEmoji();
        if (r0 == 0) goto L_0x0ace;
    L_0x0abb:
        r2 = NUM; // 0x7f0d0663 float:1.874543E38 double:1.0531305853E-314;
        r3 = new java.lang.Object[r10];
        r4 = r12.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = "NotificationActionPinnedStickerEmojiChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x0ace:
        r0 = NUM; // 0x7f0d0661 float:1.8745427E38 double:1.0531305844E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedStickerChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0adf:
        r3 = r2.messageOwner;
        r5 = r3.media;
        r8 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r8 == 0) goto L_0x0b2a;
    L_0x0ae7:
        r0 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r0 < r5) goto L_0x0b19;
    L_0x0aed:
        r0 = r3.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0b19;
    L_0x0af5:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r4);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0d0665 float:1.8745435E38 double:1.0531305863E-314;
        r3 = new java.lang.Object[r10];
        r4 = r12.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r2, r3);
        goto L_0x0412;
    L_0x0b19:
        r0 = NUM; // 0x7f0d0649 float:1.8745378E38 double:1.0531305725E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedFileChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0b2a:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r4 != 0) goto L_0x0CLASSNAME;
    L_0x0b2e:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r4 == 0) goto L_0x0b34;
    L_0x0b32:
        goto L_0x0CLASSNAME;
    L_0x0b34:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r4 == 0) goto L_0x0b49;
    L_0x0b38:
        r0 = NUM; // 0x7f0d0651 float:1.8745394E38 double:1.0531305764E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGeoLiveChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0b49:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r4 == 0) goto L_0x0b6e;
    L_0x0b4d:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r0;
        r2 = NUM; // 0x7f0d0647 float:1.8745374E38 double:1.0531305715E-314;
        r3 = new java.lang.Object[r10];
        r4 = r12.title;
        r3[r1] = r4;
        r1 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r1, r0);
        r3[r6] = r0;
        r0 = "NotificationActionPinnedContactChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x0b6e:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r0 == 0) goto L_0x0b8b;
    L_0x0b72:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5;
        r0 = NUM; // 0x7f0d065d float:1.8745419E38 double:1.0531305824E-314;
        r2 = new java.lang.Object[r10];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = r5.poll;
        r1 = r1.question;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedPollChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0b8b:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 == 0) goto L_0x0bd2;
    L_0x0b8f:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r0 < r4) goto L_0x0bc1;
    L_0x0b95:
        r0 = r3.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0bc1;
    L_0x0b9d:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r14);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0d0665 float:1.8745435E38 double:1.0531305863E-314;
        r3 = new java.lang.Object[r10];
        r4 = r12.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r2, r3);
        goto L_0x0412;
    L_0x0bc1:
        r0 = NUM; // 0x7f0d065b float:1.8745415E38 double:1.0531305814E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedPhotoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0bd2:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x0be7;
    L_0x0bd6:
        r0 = NUM; // 0x7f0d064b float:1.8745382E38 double:1.0531305735E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGameChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0be7:
        r0 = r2.messageText;
        if (r0 == 0) goto L_0x0CLASSNAME;
    L_0x0beb:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x0CLASSNAME;
    L_0x0bf1:
        r0 = r2.messageText;
        r2 = r0.length();
        r3 = 20;
        if (r2 <= r3) goto L_0x0CLASSNAME;
    L_0x0bfb:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = 20;
        r0 = r0.subSequence(r1, r3);
        r2.append(r0);
        r0 = "...";
        r2.append(r0);
        r0 = r2.toString();
    L_0x0CLASSNAME:
        r2 = NUM; // 0x7f0d0665 float:1.8745435E38 double:1.0531305863E-314;
        r3 = new java.lang.Object[r10];
        r4 = r12.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r2, r3);
        goto L_0x0412;
    L_0x0CLASSNAME:
        r0 = NUM; // 0x7f0d0659 float:1.874541E38 double:1.0531305804E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedNoTextChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0CLASSNAME:
        r0 = NUM; // 0x7f0d064f float:1.874539E38 double:1.0531305755E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGeoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0CLASSNAME:
        r1 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r1 == 0) goto L_0x117b;
    L_0x0CLASSNAME:
        r0 = r0.messageText;
        r9 = r0.toString();
        goto L_0x117c;
    L_0x0CLASSNAME:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x0c6e;
    L_0x0CLASSNAME:
        r0 = r12.megagroup;
        if (r0 != 0) goto L_0x0c6e;
    L_0x0c5d:
        r0 = NUM; // 0x7f0d0258 float:1.8743332E38 double:1.053130074E-314;
        r2 = new java.lang.Object[r6];
        r3 = r12.title;
        r2[r1] = r3;
        r1 = "ChannelPhotoEditNotification";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0c6e:
        r0 = NUM; // 0x7f0d066d float:1.8745451E38 double:1.0531305903E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationEditedGroupPhoto";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0CLASSNAME:
        r2 = 0;
        r5 = org.telegram.messenger.ChatObject.isChannel(r12);
        if (r5 == 0) goto L_0x0ec3;
    L_0x0CLASSNAME:
        r5 = r12.megagroup;
        if (r5 != 0) goto L_0x0ec3;
    L_0x0c8c:
        r5 = r17.isMediaEmpty();
        if (r5 == 0) goto L_0x0cc4;
    L_0x0CLASSNAME:
        if (r18 != 0) goto L_0x0cb5;
    L_0x0CLASSNAME:
        r2 = r0.messageOwner;
        r2 = r2.message;
        if (r2 == 0) goto L_0x0cb5;
    L_0x0c9a:
        r2 = r2.length();
        if (r2 == 0) goto L_0x0cb5;
    L_0x0ca0:
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2[r6] = r0;
        r0 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r0, r2);
        r19[r1] = r6;
        goto L_0x117c;
    L_0x0cb5:
        r0 = NUM; // 0x7f0d024a float:1.8743303E38 double:1.053130067E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageNoText";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0cc4:
        r5 = r0.messageOwner;
        r7 = r5.media;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r7 == 0) goto L_0x0d0f;
    L_0x0ccc:
        if (r18 != 0) goto L_0x0d00;
    L_0x0cce:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0d00;
    L_0x0cd4:
        r2 = r5.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0d00;
    L_0x0cdc:
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r14);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r0, r2);
        r19[r1] = r6;
        goto L_0x117c;
    L_0x0d00:
        r0 = NUM; // 0x7f0d024b float:1.8743305E38 double:1.0531300676E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessagePhoto";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0d0f:
        r5 = r17.isVideo();
        if (r5 == 0) goto L_0x0d5a;
    L_0x0d15:
        if (r18 != 0) goto L_0x0d4b;
    L_0x0d17:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0d4b;
    L_0x0d1d:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0d4b;
    L_0x0d27:
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r13);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r0, r2);
        r19[r1] = r6;
        goto L_0x117c;
    L_0x0d4b:
        r0 = NUM; // 0x7f0d0250 float:1.8743315E38 double:1.05313007E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageVideo";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0d5a:
        r5 = r17.isVoice();
        if (r5 == 0) goto L_0x0d6f;
    L_0x0d60:
        r0 = NUM; // 0x7f0d0242 float:1.8743287E38 double:1.053130063E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageAudio";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0d6f:
        r5 = r17.isRoundVideo();
        if (r5 == 0) goto L_0x0d84;
    L_0x0d75:
        r0 = NUM; // 0x7f0d024d float:1.874331E38 double:1.0531300685E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageRound";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0d84:
        r5 = r17.isMusic();
        if (r5 == 0) goto L_0x0d99;
    L_0x0d8a:
        r0 = NUM; // 0x7f0d0249 float:1.8743301E38 double:1.0531300666E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageMusic";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0d99:
        r5 = r0.messageOwner;
        r5 = r5.media;
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r7 == 0) goto L_0x0dbc;
    L_0x0da1:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r5;
        r0 = NUM; // 0x7f0d0243 float:1.874329E38 double:1.0531300636E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r5.first_name;
        r3 = r5.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r1, r3);
        r2[r6] = r1;
        r1 = "ChannelMessageContact2";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0dbc:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r7 == 0) goto L_0x0dd7;
    L_0x0dc0:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5;
        r0 = NUM; // 0x7f0d024c float:1.8743307E38 double:1.053130068E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r5.poll;
        r1 = r1.question;
        r2[r6] = r1;
        r1 = "ChannelMessagePoll2";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0dd7:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r7 != 0) goto L_0x0eb4;
    L_0x0ddb:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r7 == 0) goto L_0x0de1;
    L_0x0ddf:
        goto L_0x0eb4;
    L_0x0de1:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r7 == 0) goto L_0x0df4;
    L_0x0de5:
        r0 = NUM; // 0x7f0d0247 float:1.8743297E38 double:1.0531300656E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageLiveLocation";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0df4:
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r5 == 0) goto L_0x117b;
    L_0x0df8:
        r2 = r17.isSticker();
        if (r2 == 0) goto L_0x0e24;
    L_0x0dfe:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x0e15;
    L_0x0e04:
        r2 = NUM; // 0x7f0d024f float:1.8743313E38 double:1.0531300695E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r11;
        r3[r6] = r0;
        r0 = "ChannelMessageStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x0e15:
        r0 = NUM; // 0x7f0d024e float:1.8743311E38 double:1.053130069E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x0e24:
        r2 = r17.isGif();
        if (r2 == 0) goto L_0x0e6f;
    L_0x0e2a:
        if (r18 != 0) goto L_0x0e60;
    L_0x0e2c:
        r2 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r2 < r4) goto L_0x0e60;
    L_0x0e32:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0e60;
    L_0x0e3c:
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r4.append(r0);
        r0 = r4.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r0, r2);
        r19[r1] = r6;
        goto L_0x117c;
    L_0x0e60:
        r0 = NUM; // 0x7f0d0246 float:1.8743295E38 double:1.053130065E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageGIF";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0e6f:
        if (r18 != 0) goto L_0x0ea5;
    L_0x0e71:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0ea5;
    L_0x0e77:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0ea5;
    L_0x0e81:
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r4);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0d06a0 float:1.8745555E38 double:1.0531306155E-314;
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r0, r2);
        r19[r1] = r6;
        goto L_0x117c;
    L_0x0ea5:
        r0 = NUM; // 0x7f0d0244 float:1.8743291E38 double:1.053130064E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageDocument";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0eb4:
        r0 = NUM; // 0x7f0d0248 float:1.87433E38 double:1.053130066E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageMap";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0ec3:
        r5 = r17.isMediaEmpty();
        r7 = NUM; // 0x7f0d0692 float:1.8745526E38 double:1.0531306086E-314;
        r8 = "NotificationMessageGroupText";
        if (r5 == 0) goto L_0x0var_;
    L_0x0ece:
        if (r18 != 0) goto L_0x0ef0;
    L_0x0ed0:
        r2 = r0.messageOwner;
        r2 = r2.message;
        if (r2 == 0) goto L_0x0ef0;
    L_0x0ed6:
        r2 = r2.length();
        if (r2 == 0) goto L_0x0ef0;
    L_0x0edc:
        r2 = new java.lang.Object[r15];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2[r10] = r0;
        r9 = org.telegram.messenger.LocaleController.formatString(r8, r7, r2);
        goto L_0x117c;
    L_0x0ef0:
        r0 = NUM; // 0x7f0d068c float:1.8745514E38 double:1.0531306056E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupNoText";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0var_:
        r5 = r0.messageOwner;
        r9 = r5.media;
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r9 == 0) goto L_0x0var_;
    L_0x0f0b:
        if (r18 != 0) goto L_0x0f3e;
    L_0x0f0d:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0f3e;
    L_0x0var_:
        r2 = r5.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0f3e;
    L_0x0f1b:
        r2 = new java.lang.Object[r15];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r14);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r2[r10] = r0;
        r9 = org.telegram.messenger.LocaleController.formatString(r8, r7, r2);
        goto L_0x117c;
    L_0x0f3e:
        r0 = NUM; // 0x7f0d068d float:1.8745516E38 double:1.053130606E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupPhoto";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0var_:
        r5 = r17.isVideo();
        if (r5 == 0) goto L_0x0f9f;
    L_0x0var_:
        if (r18 != 0) goto L_0x0f8c;
    L_0x0var_:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0f8c;
    L_0x0f5f:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0f8c;
    L_0x0var_:
        r2 = new java.lang.Object[r15];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r13);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r2[r10] = r0;
        r9 = org.telegram.messenger.LocaleController.formatString(r8, r7, r2);
        goto L_0x117c;
    L_0x0f8c:
        r0 = NUM; // 0x7f0d0693 float:1.8745528E38 double:1.053130609E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = " ";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0f9f:
        r5 = r17.isVoice();
        if (r5 == 0) goto L_0x0fb8;
    L_0x0fa5:
        r0 = NUM; // 0x7f0d0682 float:1.8745494E38 double:1.0531306007E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupAudio";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0fb8:
        r5 = r17.isRoundVideo();
        if (r5 == 0) goto L_0x0fd1;
    L_0x0fbe:
        r0 = NUM; // 0x7f0d068f float:1.874552E38 double:1.053130607E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupRound";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0fd1:
        r5 = r17.isMusic();
        if (r5 == 0) goto L_0x0fea;
    L_0x0fd7:
        r0 = NUM; // 0x7f0d068b float:1.8745512E38 double:1.053130605E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupMusic";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x0fea:
        r5 = r0.messageOwner;
        r5 = r5.media;
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r9 == 0) goto L_0x1011;
    L_0x0ff2:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r5;
        r0 = NUM; // 0x7f0d0683 float:1.8745496E38 double:1.053130601E-314;
        r2 = new java.lang.Object[r15];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = r5.first_name;
        r3 = r5.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r1, r3);
        r2[r10] = r1;
        r1 = "NotificationMessageGroupContact2";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x1011:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r9 == 0) goto L_0x1030;
    L_0x1015:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5;
        r0 = NUM; // 0x7f0d068e float:1.8745518E38 double:1.0531306066E-314;
        r2 = new java.lang.Object[r15];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = r5.poll;
        r1 = r1.question;
        r2[r10] = r1;
        r1 = "NotificationMessageGroupPoll2";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x1030:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r9 == 0) goto L_0x104d;
    L_0x1034:
        r0 = NUM; // 0x7f0d0685 float:1.87455E38 double:1.053130602E-314;
        r2 = new java.lang.Object[r15];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = r5.game;
        r1 = r1.title;
        r2[r10] = r1;
        r1 = "NotificationMessageGroupGame";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x104d:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r9 != 0) goto L_0x113a;
    L_0x1051:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r9 == 0) goto L_0x1057;
    L_0x1055:
        goto L_0x113a;
    L_0x1057:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r9 == 0) goto L_0x106e;
    L_0x105b:
        r0 = NUM; // 0x7f0d0689 float:1.8745508E38 double:1.053130604E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupLiveLocation";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x106e:
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r5 == 0) goto L_0x117b;
    L_0x1072:
        r2 = r17.isSticker();
        if (r2 == 0) goto L_0x10a6;
    L_0x1078:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x1093;
    L_0x107e:
        r2 = NUM; // 0x7f0d0691 float:1.8745524E38 double:1.053130608E-314;
        r3 = new java.lang.Object[r15];
        r3[r1] = r11;
        r1 = r12.title;
        r3[r6] = r1;
        r3[r10] = r0;
        r0 = "NotificationMessageGroupStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x0412;
    L_0x1093:
        r0 = NUM; // 0x7f0d0690 float:1.8745522E38 double:1.0531306076E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0412;
    L_0x10a6:
        r2 = r17.isGif();
        if (r2 == 0) goto L_0x10f4;
    L_0x10ac:
        if (r18 != 0) goto L_0x10e1;
    L_0x10ae:
        r2 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r2 < r4) goto L_0x10e1;
    L_0x10b4:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x10e1;
    L_0x10be:
        r2 = new java.lang.Object[r15];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r2[r10] = r0;
        r9 = org.telegram.messenger.LocaleController.formatString(r8, r7, r2);
        goto L_0x117c;
    L_0x10e1:
        r0 = NUM; // 0x7f0d0687 float:1.8745504E38 double:1.053130603E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupGif";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x10f4:
        if (r18 != 0) goto L_0x1128;
    L_0x10f6:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x1128;
    L_0x10fc:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x1128;
    L_0x1106:
        r2 = new java.lang.Object[r15];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r4);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r2[r10] = r0;
        r9 = org.telegram.messenger.LocaleController.formatString(r8, r7, r2);
        goto L_0x117c;
    L_0x1128:
        r0 = NUM; // 0x7f0d0684 float:1.8745498E38 double:1.0531306016E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupDocument";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x113a:
        r0 = NUM; // 0x7f0d068a float:1.874551E38 double:1.0531306046E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupMap";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x114c:
        if (r20 == 0) goto L_0x1150;
    L_0x114e:
        r20[r1] = r1;
    L_0x1150:
        r0 = org.telegram.messenger.ChatObject.isChannel(r12);
        if (r0 == 0) goto L_0x1168;
    L_0x1156:
        r0 = r12.megagroup;
        if (r0 != 0) goto L_0x1168;
    L_0x115a:
        r0 = NUM; // 0x7f0d024a float:1.8743303E38 double:1.053130067E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r11;
        r1 = "ChannelMessageNoText";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x1168:
        r0 = NUM; // 0x7f0d068c float:1.8745514E38 double:1.0531306056E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r11;
        r1 = r12.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupNoText";
        r9 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x117c;
    L_0x117a:
        r2 = 0;
    L_0x117b:
        r9 = r2;
    L_0x117c:
        return r9;
    L_0x117d:
        r0 = NUM; // 0x7f0d0afc float:1.8747818E38 double:1.053131167E-314;
        r1 = "YouHaveNewMessage";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getStringForMessage(org.telegram.messenger.MessageObject, boolean, boolean[], boolean[]):java.lang.String");
    }

    private void scheduleNotificationRepeat() {
        try {
            Intent intent = new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class);
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent service = PendingIntent.getService(ApplicationLoader.applicationContext, 0, intent, 0);
            int i = getAccountInstance().getNotificationsSettings().getInt("repeat_messages", 60);
            if (i <= 0 || this.personal_count <= 0) {
                this.alarmManager.cancel(service);
            } else {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) ((i * 60) * 1000)), service);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        Message message = messageObject.messageOwner;
        Peer peer = message.to_id;
        if (peer != null && peer.chat_id == 0 && peer.channel_id == 0) {
            MessageAction messageAction = message.action;
            if (messageAction == null || (messageAction instanceof TL_messageActionEmpty)) {
                return true;
            }
        }
        return false;
    }

    private int getNotifyOverride(SharedPreferences sharedPreferences, long j) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(j);
        int i = sharedPreferences.getInt(stringBuilder.toString(), -1);
        if (i != 3) {
            return i;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("notifyuntil_");
        stringBuilder2.append(j);
        return sharedPreferences.getInt(stringBuilder2.toString(), 0) >= getConnectionsManager().getCurrentTime() ? 2 : i;
    }

    public /* synthetic */ void lambda$showNotifications$23$NotificationsController() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$tVtEcXBSUtzhqixsWunEmHPHAAI(this));
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$-1VL5AJa2XU8eBaEZNLOYhMw8bE(this));
    }

    public /* synthetic */ void lambda$hideNotifications$24$NotificationsController() {
        notificationManager.cancel(this.notificationId);
        this.lastWearNotifiedMessageId.clear();
        for (int i = 0; i < this.wearNotificationsIds.size(); i++) {
            notificationManager.cancel(((Integer) this.wearNotificationsIds.valueAt(i)).intValue());
        }
        this.wearNotificationsIds.clear();
    }

    private void dismissNotification() {
        try {
            notificationManager.cancel(this.notificationId);
            this.pushMessages.clear();
            this.pushMessagesDict.clear();
            this.lastWearNotifiedMessageId.clear();
            for (int i = 0; i < this.wearNotificationsIds.size(); i++) {
                notificationManager.cancel(((Integer) this.wearNotificationsIds.valueAt(i)).intValue());
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread(-$$Lambda$NotificationsController$2v2nyML5dTCxIdrQrE6xmPJzze8.INSTANCE);
            if (WearDataLayerListenerService.isWatchConnected()) {
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("id", getUserConfig().getClientUserId());
                    jSONObject.put("cancel_all", true);
                    WearDataLayerListenerService.sendMessageToWatch("/notify", jSONObject.toString().getBytes(), "remote_notifications");
                } catch (JSONException unused) {
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void playInChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                if (getNotifyOverride(getAccountInstance().getNotificationsSettings(), this.opened_dialog_id) != 2) {
                    notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$51wmHPlGOlC0_zQ9GY7w7j4BjsE(this));
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    public /* synthetic */ void lambda$playInChatSound$27$NotificationsController() {
        if (Math.abs(System.currentTimeMillis() - this.lastSoundPlay) > 500) {
            try {
                if (this.soundPool == null) {
                    this.soundPool = new SoundPool(3, 1, 0);
                    this.soundPool.setOnLoadCompleteListener(-$$Lambda$NotificationsController$NULIntVdHQSUoPd6L0mVTH6J8n0.INSTANCE);
                }
                if (this.soundIn == 0 && !this.soundInLoaded) {
                    this.soundInLoaded = true;
                    this.soundIn = this.soundPool.load(ApplicationLoader.applicationContext, NUM, 1);
                }
                if (this.soundIn != 0) {
                    try {
                        this.soundPool.play(this.soundIn, 1.0f, 1.0f, 1, 0, 1.0f);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    static /* synthetic */ void lambda$null$26(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void scheduleNotificationDelay(boolean z) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("delay notification start, onlineReason = ");
                stringBuilder.append(z);
                FileLog.d(stringBuilder.toString());
            }
            this.notificationDelayWakelock.acquire(10000);
            notificationsQueue.cancelRunnable(this.notificationDelayRunnable);
            notificationsQueue.postRunnable(this.notificationDelayRunnable, (long) (z ? 3000 : 1000));
        } catch (Exception e) {
            FileLog.e(e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    /* Access modifiers changed, original: protected */
    public void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$kDrFFl__TRrIJW3mtxiKJeeK1vw(this));
    }

    public /* synthetic */ void lambda$repeatNotificationMaybe$28$NotificationsController() {
        int i = Calendar.getInstance().get(11);
        if (i < 11 || i > 22) {
            scheduleNotificationRepeat();
            return;
        }
        notificationManager.cancel(this.notificationId);
        showOrUpdateNotification(true);
    }

    private boolean isEmptyVibration(long[] jArr) {
        if (jArr == null || jArr.length == 0) {
            return false;
        }
        for (long j : jArr) {
            if (j != 0) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(26)
    public void deleteNotificationChannel(long j) {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$eYqBa_GxEYzKlHSSB2VWl64XX2Q(this, j));
    }

    public /* synthetic */ void lambda$deleteNotificationChannel$29$NotificationsController(long j) {
        if (VERSION.SDK_INT >= 26) {
            try {
                SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("org.telegram.key");
                stringBuilder.append(j);
                String stringBuilder2 = stringBuilder.toString();
                String string = notificationsSettings.getString(stringBuilder2, null);
                if (string != null) {
                    Editor remove = notificationsSettings.edit().remove(stringBuilder2);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(stringBuilder2);
                    stringBuilder.append("_s");
                    remove.remove(stringBuilder.toString()).commit();
                    systemNotificationManager.deleteNotificationChannel(string);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    @TargetApi(26)
    public void deleteAllNotificationChannels() {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$iv6fUe9w-2CLASSNAMEmbdiQOLFrNptrg(this));
    }

    public /* synthetic */ void lambda$deleteAllNotificationChannels$30$NotificationsController() {
        if (VERSION.SDK_INT >= 26) {
            try {
                SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
                Map all = notificationsSettings.getAll();
                Editor edit = notificationsSettings.edit();
                for (Entry entry : all.entrySet()) {
                    String str = (String) entry.getKey();
                    if (str.startsWith("org.telegram.key")) {
                        if (!str.endsWith("_s")) {
                            systemNotificationManager.deleteNotificationChannel((String) entry.getValue());
                        }
                        edit.remove(str);
                    }
                }
                edit.commit();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    @TargetApi(26)
    private String validateChannelId(long j, String str, long[] jArr, int i, Uri uri, int i2, long[] jArr2, Uri uri2, int i3) {
        String str2;
        long j2 = j;
        long[] jArr3 = jArr;
        int i4 = i;
        Uri uri3 = uri;
        int i5 = i2;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("org.telegram.key");
        stringBuilder.append(j2);
        String stringBuilder2 = stringBuilder.toString();
        String string = notificationsSettings.getString(stringBuilder2, null);
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        String str3 = "_s";
        stringBuilder3.append(str3);
        String string2 = notificationsSettings.getString(stringBuilder3.toString(), null);
        StringBuilder stringBuilder4 = new StringBuilder();
        int i6 = 0;
        while (i6 < jArr3.length) {
            str2 = stringBuilder2;
            stringBuilder4.append(jArr3[i6]);
            i6++;
            stringBuilder2 = str2;
        }
        str2 = stringBuilder2;
        stringBuilder4.append(i4);
        if (uri3 != null) {
            stringBuilder4.append(uri.toString());
        }
        stringBuilder4.append(i5);
        stringBuilder2 = Utilities.MD5(stringBuilder4.toString());
        if (!(string == null || string2.equals(stringBuilder2))) {
            systemNotificationManager.deleteNotificationChannel(string);
            string = null;
        }
        if (string == null) {
            StringBuilder stringBuilder5 = new StringBuilder();
            stringBuilder5.append(this.currentAccount);
            stringBuilder5.append("channel");
            stringBuilder5.append(j2);
            stringBuilder5.append("_");
            stringBuilder5.append(Utilities.random.nextLong());
            string = stringBuilder5.toString();
            NotificationChannel notificationChannel = new NotificationChannel(string, str, i5);
            if (i4 != 0) {
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(i4);
            }
            if (isEmptyVibration(jArr3)) {
                notificationChannel.enableVibration(false);
            } else {
                notificationChannel.enableVibration(true);
                if (jArr3 != null && jArr3.length > 0) {
                    notificationChannel.setVibrationPattern(jArr3);
                }
            }
            Builder builder = new Builder();
            builder.setContentType(4);
            builder.setUsage(5);
            if (uri3 != null) {
                notificationChannel.setSound(uri3, builder.build());
            } else {
                notificationChannel.setSound(null, builder.build());
            }
            systemNotificationManager.createNotificationChannel(notificationChannel);
            String str4 = str2;
            Editor putString = notificationsSettings.edit().putString(str4, string);
            StringBuilder stringBuilder6 = new StringBuilder();
            stringBuilder6.append(str4);
            stringBuilder6.append(str3);
            putString.putString(stringBuilder6.toString(), stringBuilder2).commit();
        }
        return string;
    }

    /* JADX WARNING: Removed duplicated region for block: B:427:0x0917 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x090d A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x091e A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0ab3 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a9a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0aea A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0ad1 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x084f A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x098f A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a9a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0ab3 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0ad1 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0aea A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x084f A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x098f A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0ab3 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a9a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0aea A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0ad1 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x084f A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x098f A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a9a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0ab3 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0ad1 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0aea A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x084f A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x098f A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0ab3 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a9a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0aea A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0ad1 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x084f A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x098f A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a9a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0ab3 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0ad1 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0aea A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x06bf A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0646 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x07f2  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x07a8 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0646 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x06bf A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x07a8 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x07f2  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x07f7  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0566 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0532 A:{SYNTHETIC, Splitter:B:278:0x0532} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x0572 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x05a0 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0588 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04ff A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04fc A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x051a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0519 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e9 A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04fc A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04ff A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0519 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x051a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c8 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c3 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04ab A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03fa  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e9 A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04ff A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04fc A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x051a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0519 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03bc A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0341 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c3 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c8 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03fa  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04ab A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e9 A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04fc A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04ff A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0519 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x051a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02e7 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02b0 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02ed A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02ec A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02f2 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0341 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03bc A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c8 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c3 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04ab A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03fa  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e9 A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04ff A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04fc A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x051a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0519 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x020c  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01bb A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0275 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0215  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02b0 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02e7 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02ec A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02ed A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02f2 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0306 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03bc A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0341 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c3 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c8 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03fa  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04ab A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e9 A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04fc A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04ff A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0519 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x051a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01bb A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x020c  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0215  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0275 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02e7 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02b0 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02ed A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02ec A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02f2 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0306 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0341 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03bc A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c8 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c3 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04ab A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03fa  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e9 A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04ff A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04fc A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x051a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0519 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x012b A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00fe A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0130 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x020c  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01bb A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0275 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0215  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02b0 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02e7 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02ec A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02ed A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02f2 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0306 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03bc A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0341 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c3 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c8 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03fa  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04ab A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e9 A:{SKIP, Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04fc A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04ff A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0519 A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x051a A:{Catch:{ Exception -> 0x0af6 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:421:0x08f9 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing block: B:387:0x0846, code skipped:
            if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x0848;
     */
    private void showOrUpdateNotification(boolean r46) {
        /*
        r45 = this;
        r12 = r45;
        r13 = r46;
        r1 = "currentAccount";
        r2 = r45.getUserConfig();
        r2 = r2.isClientActivated();
        if (r2 == 0) goto L_0x0afc;
    L_0x0010:
        r2 = r12.pushMessages;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x0afc;
    L_0x0018:
        r2 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts;
        if (r2 != 0) goto L_0x0024;
    L_0x001c:
        r2 = r12.currentAccount;
        r3 = org.telegram.messenger.UserConfig.selectedAccount;
        if (r2 == r3) goto L_0x0024;
    L_0x0022:
        goto L_0x0afc;
    L_0x0024:
        r2 = r45.getConnectionsManager();	 Catch:{ Exception -> 0x0af6 }
        r2.resumeNetworkMaybe();	 Catch:{ Exception -> 0x0af6 }
        r2 = r12.pushMessages;	 Catch:{ Exception -> 0x0af6 }
        r3 = 0;
        r2 = r2.get(r3);	 Catch:{ Exception -> 0x0af6 }
        r2 = (org.telegram.messenger.MessageObject) r2;	 Catch:{ Exception -> 0x0af6 }
        r4 = r45.getAccountInstance();	 Catch:{ Exception -> 0x0af6 }
        r4 = r4.getNotificationsSettings();	 Catch:{ Exception -> 0x0af6 }
        r5 = "dismissDate";
        r5 = r4.getInt(r5, r3);	 Catch:{ Exception -> 0x0af6 }
        r6 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r6 = r6.date;	 Catch:{ Exception -> 0x0af6 }
        if (r6 > r5) goto L_0x004c;
    L_0x0048:
        r45.dismissNotification();	 Catch:{ Exception -> 0x0af6 }
        return;
    L_0x004c:
        r6 = r2.getDialogId();	 Catch:{ Exception -> 0x0af6 }
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r8 = r8.mentioned;	 Catch:{ Exception -> 0x0af6 }
        if (r8 == 0) goto L_0x005c;
    L_0x0056:
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r8 = r8.from_id;	 Catch:{ Exception -> 0x0af6 }
        r8 = (long) r8;	 Catch:{ Exception -> 0x0af6 }
        goto L_0x005d;
    L_0x005c:
        r8 = r6;
    L_0x005d:
        r2.getId();	 Catch:{ Exception -> 0x0af6 }
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0af6 }
        r10 = r10.chat_id;	 Catch:{ Exception -> 0x0af6 }
        if (r10 == 0) goto L_0x006f;
    L_0x0068:
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0af6 }
        r10 = r10.chat_id;	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0075;
    L_0x006f:
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0af6 }
        r10 = r10.channel_id;	 Catch:{ Exception -> 0x0af6 }
    L_0x0075:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r11 = r11.to_id;	 Catch:{ Exception -> 0x0af6 }
        r11 = r11.user_id;	 Catch:{ Exception -> 0x0af6 }
        if (r11 != 0) goto L_0x0082;
    L_0x007d:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r11 = r11.from_id;	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0090;
    L_0x0082:
        r14 = r45.getUserConfig();	 Catch:{ Exception -> 0x0af6 }
        r14 = r14.getClientUserId();	 Catch:{ Exception -> 0x0af6 }
        if (r11 != r14) goto L_0x0090;
    L_0x008c:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r11 = r11.from_id;	 Catch:{ Exception -> 0x0af6 }
    L_0x0090:
        r14 = r45.getMessagesController();	 Catch:{ Exception -> 0x0af6 }
        r15 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0af6 }
        r14 = r14.getUser(r15);	 Catch:{ Exception -> 0x0af6 }
        if (r10 == 0) goto L_0x00ba;
    L_0x009e:
        r15 = r45.getMessagesController();	 Catch:{ Exception -> 0x0af6 }
        r3 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0af6 }
        r15 = r15.getChat(r3);	 Catch:{ Exception -> 0x0af6 }
        r3 = org.telegram.messenger.ChatObject.isChannel(r15);	 Catch:{ Exception -> 0x0af6 }
        if (r3 == 0) goto L_0x00b6;
    L_0x00b0:
        r3 = r15.megagroup;	 Catch:{ Exception -> 0x0af6 }
        if (r3 != 0) goto L_0x00b6;
    L_0x00b4:
        r3 = 1;
        goto L_0x00b7;
    L_0x00b6:
        r3 = 0;
    L_0x00b7:
        r19 = r5;
        goto L_0x00be;
    L_0x00ba:
        r19 = r5;
        r3 = 0;
        r15 = 0;
    L_0x00be:
        r5 = r12.getNotifyOverride(r4, r8);	 Catch:{ Exception -> 0x0af6 }
        r20 = r2;
        r2 = -1;
        r21 = r1;
        r1 = 2;
        if (r5 != r2) goto L_0x00cf;
    L_0x00ca:
        r2 = r12.isGlobalNotificationsEnabled(r6);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x00d4;
    L_0x00cf:
        if (r5 == r1) goto L_0x00d3;
    L_0x00d1:
        r2 = 1;
        goto L_0x00d4;
    L_0x00d3:
        r2 = 0;
    L_0x00d4:
        if (r13 == 0) goto L_0x00db;
    L_0x00d6:
        if (r2 != 0) goto L_0x00d9;
    L_0x00d8:
        goto L_0x00db;
    L_0x00d9:
        r2 = 0;
        goto L_0x00dc;
    L_0x00db:
        r2 = 1;
    L_0x00dc:
        r22 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r2 != 0) goto L_0x017d;
    L_0x00e0:
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 != 0) goto L_0x017d;
    L_0x00e4:
        if (r15 == 0) goto L_0x017d;
    L_0x00e6:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r5.<init>();	 Catch:{ Exception -> 0x0af6 }
        r8 = "custom_";
        r5.append(r8);	 Catch:{ Exception -> 0x0af6 }
        r5.append(r6);	 Catch:{ Exception -> 0x0af6 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0af6 }
        r8 = 0;
        r5 = r4.getBoolean(r5, r8);	 Catch:{ Exception -> 0x0af6 }
        if (r5 == 0) goto L_0x012b;
    L_0x00fe:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r5.<init>();	 Catch:{ Exception -> 0x0af6 }
        r8 = "smart_max_count_";
        r5.append(r8);	 Catch:{ Exception -> 0x0af6 }
        r5.append(r6);	 Catch:{ Exception -> 0x0af6 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0af6 }
        r5 = r4.getInt(r5, r1);	 Catch:{ Exception -> 0x0af6 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r8.<init>();	 Catch:{ Exception -> 0x0af6 }
        r9 = "smart_delay_";
        r8.append(r9);	 Catch:{ Exception -> 0x0af6 }
        r8.append(r6);	 Catch:{ Exception -> 0x0af6 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x0af6 }
        r9 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r8 = r4.getInt(r8, r9);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x012e;
    L_0x012b:
        r8 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r5 = 2;
    L_0x012e:
        if (r5 == 0) goto L_0x017d;
    L_0x0130:
        r9 = r12.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0af6 }
        r9 = r9.get(r6);	 Catch:{ Exception -> 0x0af6 }
        r9 = (android.graphics.Point) r9;	 Catch:{ Exception -> 0x0af6 }
        if (r9 != 0) goto L_0x014d;
    L_0x013a:
        r5 = new android.graphics.Point;	 Catch:{ Exception -> 0x0af6 }
        r8 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0af6 }
        r8 = r8 / r22;
        r9 = (int) r8;	 Catch:{ Exception -> 0x0af6 }
        r8 = 1;
        r5.<init>(r8, r9);	 Catch:{ Exception -> 0x0af6 }
        r8 = r12.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0af6 }
        r8.put(r6, r5);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x017d;
    L_0x014d:
        r1 = r9.y;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1 + r8;
        r8 = r2;
        r1 = (long) r1;	 Catch:{ Exception -> 0x0af6 }
        r24 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0af6 }
        r24 = r24 / r22;
        r26 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1));
        if (r26 >= 0) goto L_0x0168;
    L_0x015c:
        r1 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0af6 }
        r1 = r1 / r22;
        r2 = (int) r1;	 Catch:{ Exception -> 0x0af6 }
        r1 = 1;
        r9.set(r1, r2);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x017e;
    L_0x0168:
        r1 = r9.x;	 Catch:{ Exception -> 0x0af6 }
        if (r1 >= r5) goto L_0x017a;
    L_0x016c:
        r2 = 1;
        r1 = r1 + r2;
        r24 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0af6 }
        r2 = r14;
        r13 = r24 / r22;
        r5 = (int) r13;	 Catch:{ Exception -> 0x0af6 }
        r9.set(r1, r5);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x017f;
    L_0x017a:
        r2 = r14;
        r8 = 1;
        goto L_0x017f;
    L_0x017d:
        r8 = r2;
    L_0x017e:
        r2 = r14;
    L_0x017f:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.getPath();	 Catch:{ Exception -> 0x0af6 }
        r5 = "EnableInAppSounds";
        r9 = 1;
        r5 = r4.getBoolean(r5, r9);	 Catch:{ Exception -> 0x0af6 }
        r13 = "EnableInAppVibrate";
        r13 = r4.getBoolean(r13, r9);	 Catch:{ Exception -> 0x0af6 }
        r14 = "EnableInAppPreview";
        r14 = r4.getBoolean(r14, r9);	 Catch:{ Exception -> 0x0af6 }
        r9 = "EnableInAppPriority";
        r24 = r14;
        r14 = 0;
        r9 = r4.getBoolean(r9, r14);	 Catch:{ Exception -> 0x0af6 }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r14.<init>();	 Catch:{ Exception -> 0x0af6 }
        r25 = r2;
        r2 = "custom_";
        r14.append(r2);	 Catch:{ Exception -> 0x0af6 }
        r14.append(r6);	 Catch:{ Exception -> 0x0af6 }
        r2 = r14.toString();	 Catch:{ Exception -> 0x0af6 }
        r14 = 0;
        r2 = r4.getBoolean(r2, r14);	 Catch:{ Exception -> 0x0af6 }
        if (r2 == 0) goto L_0x020c;
    L_0x01bb:
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r14.<init>();	 Catch:{ Exception -> 0x0af6 }
        r27 = r15;
        r15 = "vibrate_";
        r14.append(r15);	 Catch:{ Exception -> 0x0af6 }
        r14.append(r6);	 Catch:{ Exception -> 0x0af6 }
        r14 = r14.toString();	 Catch:{ Exception -> 0x0af6 }
        r15 = 0;
        r14 = r4.getInt(r14, r15);	 Catch:{ Exception -> 0x0af6 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r15.<init>();	 Catch:{ Exception -> 0x0af6 }
        r28 = r14;
        r14 = "priority_";
        r15.append(r14);	 Catch:{ Exception -> 0x0af6 }
        r15.append(r6);	 Catch:{ Exception -> 0x0af6 }
        r14 = r15.toString();	 Catch:{ Exception -> 0x0af6 }
        r15 = 3;
        r14 = r4.getInt(r14, r15);	 Catch:{ Exception -> 0x0af6 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r15.<init>();	 Catch:{ Exception -> 0x0af6 }
        r29 = r14;
        r14 = "sound_path_";
        r15.append(r14);	 Catch:{ Exception -> 0x0af6 }
        r15.append(r6);	 Catch:{ Exception -> 0x0af6 }
        r14 = r15.toString();	 Catch:{ Exception -> 0x0af6 }
        r15 = 0;
        r14 = r4.getString(r14, r15);	 Catch:{ Exception -> 0x0af6 }
        r15 = r14;
        r14 = r28;
        r12 = r29;
        r28 = r8;
        goto L_0x0213;
    L_0x020c:
        r27 = r15;
        r28 = r8;
        r12 = 3;
        r14 = 0;
        r15 = 0;
    L_0x0213:
        if (r10 == 0) goto L_0x0275;
    L_0x0215:
        if (r3 == 0) goto L_0x0246;
    L_0x0217:
        if (r15 == 0) goto L_0x0221;
    L_0x0219:
        r3 = r15.equals(r1);	 Catch:{ Exception -> 0x02e2 }
        if (r3 == 0) goto L_0x0221;
    L_0x021f:
        r15 = 0;
        goto L_0x0229;
    L_0x0221:
        if (r15 != 0) goto L_0x0229;
    L_0x0223:
        r3 = "ChannelSoundPath";
        r15 = r4.getString(r3, r1);	 Catch:{ Exception -> 0x02e2 }
    L_0x0229:
        r3 = "vibrate_channel";
        r8 = 0;
        r3 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02e2 }
        r8 = "priority_channel";
        r30 = r3;
        r3 = 1;
        r8 = r4.getInt(r8, r3);	 Catch:{ Exception -> 0x02e2 }
        r3 = "ChannelLed";
        r31 = r8;
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r8 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02e2 }
        goto L_0x02a5;
    L_0x0246:
        if (r15 == 0) goto L_0x0250;
    L_0x0248:
        r3 = r15.equals(r1);	 Catch:{ Exception -> 0x02e2 }
        if (r3 == 0) goto L_0x0250;
    L_0x024e:
        r15 = 0;
        goto L_0x0258;
    L_0x0250:
        if (r15 != 0) goto L_0x0258;
    L_0x0252:
        r3 = "GroupSoundPath";
        r15 = r4.getString(r3, r1);	 Catch:{ Exception -> 0x02e2 }
    L_0x0258:
        r3 = "vibrate_group";
        r8 = 0;
        r3 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02e2 }
        r8 = "priority_group";
        r30 = r3;
        r3 = 1;
        r8 = r4.getInt(r8, r3);	 Catch:{ Exception -> 0x02e2 }
        r3 = "GroupLed";
        r31 = r8;
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r8 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02e2 }
        goto L_0x02a5;
    L_0x0275:
        if (r11 == 0) goto L_0x02a8;
    L_0x0277:
        if (r15 == 0) goto L_0x0281;
    L_0x0279:
        r3 = r15.equals(r1);	 Catch:{ Exception -> 0x02e2 }
        if (r3 == 0) goto L_0x0281;
    L_0x027f:
        r15 = 0;
        goto L_0x0289;
    L_0x0281:
        if (r15 != 0) goto L_0x0289;
    L_0x0283:
        r3 = "GlobalSoundPath";
        r15 = r4.getString(r3, r1);	 Catch:{ Exception -> 0x02e2 }
    L_0x0289:
        r3 = "vibrate_messages";
        r8 = 0;
        r3 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02e2 }
        r8 = "priority_messages";
        r30 = r3;
        r3 = 1;
        r8 = r4.getInt(r8, r3);	 Catch:{ Exception -> 0x02e2 }
        r3 = "MessagesLed";
        r31 = r8;
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r8 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02e2 }
    L_0x02a5:
        r3 = r30;
        goto L_0x02ae;
    L_0x02a8:
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r3 = 0;
        r31 = 0;
    L_0x02ae:
        if (r2 == 0) goto L_0x02e7;
    L_0x02b0:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02e2 }
        r2.<init>();	 Catch:{ Exception -> 0x02e2 }
        r29 = r8;
        r8 = "color_";
        r2.append(r8);	 Catch:{ Exception -> 0x02e2 }
        r2.append(r6);	 Catch:{ Exception -> 0x02e2 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x02e2 }
        r2 = r4.contains(r2);	 Catch:{ Exception -> 0x02e2 }
        if (r2 == 0) goto L_0x02e9;
    L_0x02c9:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02e2 }
        r2.<init>();	 Catch:{ Exception -> 0x02e2 }
        r8 = "color_";
        r2.append(r8);	 Catch:{ Exception -> 0x02e2 }
        r2.append(r6);	 Catch:{ Exception -> 0x02e2 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x02e2 }
        r8 = 0;
        r2 = r4.getInt(r2, r8);	 Catch:{ Exception -> 0x02e2 }
        r29 = r2;
        goto L_0x02e9;
    L_0x02e2:
        r0 = move-exception;
        r12 = r45;
        goto L_0x0af7;
    L_0x02e7:
        r29 = r8;
    L_0x02e9:
        r2 = 3;
        if (r12 == r2) goto L_0x02ed;
    L_0x02ec:
        goto L_0x02ef;
    L_0x02ed:
        r12 = r31;
    L_0x02ef:
        r4 = 4;
        if (r3 != r4) goto L_0x02f6;
    L_0x02f2:
        r3 = 0;
        r4 = 2;
        r8 = 1;
        goto L_0x02f8;
    L_0x02f6:
        r4 = 2;
        r8 = 0;
    L_0x02f8:
        if (r3 != r4) goto L_0x02ff;
    L_0x02fa:
        r4 = 1;
        if (r14 == r4) goto L_0x030b;
    L_0x02fd:
        if (r14 == r2) goto L_0x030b;
    L_0x02ff:
        r2 = 2;
        if (r3 == r2) goto L_0x0304;
    L_0x0302:
        if (r14 == r2) goto L_0x030b;
    L_0x0304:
        if (r14 == 0) goto L_0x030a;
    L_0x0306:
        r2 = 4;
        if (r14 == r2) goto L_0x030a;
    L_0x0309:
        goto L_0x030b;
    L_0x030a:
        r14 = r3;
    L_0x030b:
        r2 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x02e2 }
        if (r2 != 0) goto L_0x031f;
    L_0x030f:
        if (r5 != 0) goto L_0x0312;
    L_0x0311:
        r15 = 0;
    L_0x0312:
        if (r13 != 0) goto L_0x0315;
    L_0x0314:
        r14 = 2;
    L_0x0315:
        if (r9 != 0) goto L_0x031a;
    L_0x0317:
        r2 = 2;
        r3 = 0;
        goto L_0x0321;
    L_0x031a:
        r2 = 2;
        if (r12 != r2) goto L_0x0320;
    L_0x031d:
        r3 = 1;
        goto L_0x0321;
    L_0x031f:
        r2 = 2;
    L_0x0320:
        r3 = r12;
    L_0x0321:
        if (r8 == 0) goto L_0x0337;
    L_0x0323:
        if (r14 == r2) goto L_0x0337;
    L_0x0325:
        r2 = audioManager;	 Catch:{ Exception -> 0x0332 }
        r2 = r2.getRingerMode();	 Catch:{ Exception -> 0x0332 }
        if (r2 == 0) goto L_0x0337;
    L_0x032d:
        r4 = 1;
        if (r2 == r4) goto L_0x0337;
    L_0x0330:
        r14 = 2;
        goto L_0x0337;
    L_0x0332:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x02e2 }
    L_0x0337:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x02e2 }
        r4 = 100;
        r9 = 26;
        r12 = 0;
        if (r2 < r9) goto L_0x03bc;
    L_0x0341:
        r2 = 2;
        if (r14 != r2) goto L_0x034e;
    L_0x0344:
        r9 = new long[r2];	 Catch:{ Exception -> 0x02e2 }
        r2 = 0;
        r9[r2] = r12;	 Catch:{ Exception -> 0x02e2 }
        r2 = 1;
        r9[r2] = r12;	 Catch:{ Exception -> 0x02e2 }
        r8 = r9;
        goto L_0x0378;
    L_0x034e:
        r2 = 1;
        if (r14 != r2) goto L_0x0360;
    L_0x0351:
        r9 = 4;
        r8 = new long[r9];	 Catch:{ Exception -> 0x02e2 }
        r9 = 0;
        r8[r9] = r12;	 Catch:{ Exception -> 0x02e2 }
        r8[r2] = r4;	 Catch:{ Exception -> 0x02e2 }
        r2 = 2;
        r8[r2] = r12;	 Catch:{ Exception -> 0x02e2 }
        r2 = 3;
        r8[r2] = r4;	 Catch:{ Exception -> 0x02e2 }
        goto L_0x0378;
    L_0x0360:
        if (r14 == 0) goto L_0x0375;
    L_0x0362:
        r2 = 4;
        if (r14 != r2) goto L_0x0366;
    L_0x0365:
        goto L_0x0375;
    L_0x0366:
        r2 = 3;
        if (r14 != r2) goto L_0x0373;
    L_0x0369:
        r2 = 2;
        r8 = new long[r2];	 Catch:{ Exception -> 0x02e2 }
        r2 = 0;
        r8[r2] = r12;	 Catch:{ Exception -> 0x02e2 }
        r2 = 1;
        r8[r2] = r22;	 Catch:{ Exception -> 0x02e2 }
        goto L_0x0378;
    L_0x0373:
        r8 = 0;
        goto L_0x0378;
    L_0x0375:
        r2 = 0;
        r8 = new long[r2];	 Catch:{ Exception -> 0x02e2 }
    L_0x0378:
        if (r15 == 0) goto L_0x0390;
    L_0x037a:
        r2 = "NoSound";
        r2 = r15.equals(r2);	 Catch:{ Exception -> 0x02e2 }
        if (r2 != 0) goto L_0x0390;
    L_0x0382:
        r2 = r15.equals(r1);	 Catch:{ Exception -> 0x02e2 }
        if (r2 == 0) goto L_0x038b;
    L_0x0388:
        r2 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x02e2 }
        goto L_0x0391;
    L_0x038b:
        r2 = android.net.Uri.parse(r15);	 Catch:{ Exception -> 0x02e2 }
        goto L_0x0391;
    L_0x0390:
        r2 = 0;
    L_0x0391:
        if (r3 != 0) goto L_0x0399;
    L_0x0393:
        r32 = r2;
        r9 = r8;
        r33 = 3;
        goto L_0x03c1;
    L_0x0399:
        r9 = 1;
        if (r3 == r9) goto L_0x03b6;
    L_0x039c:
        r9 = 2;
        if (r3 != r9) goto L_0x03a0;
    L_0x039f:
        goto L_0x03b6;
    L_0x03a0:
        r9 = 4;
        if (r3 != r9) goto L_0x03a9;
    L_0x03a3:
        r32 = r2;
        r9 = r8;
        r33 = 1;
        goto L_0x03c1;
    L_0x03a9:
        r9 = 5;
        if (r3 != r9) goto L_0x03b2;
    L_0x03ac:
        r32 = r2;
        r9 = r8;
        r33 = 2;
        goto L_0x03c1;
    L_0x03b2:
        r32 = r2;
        r9 = r8;
        goto L_0x03bf;
    L_0x03b6:
        r32 = r2;
        r9 = r8;
        r33 = 4;
        goto L_0x03c1;
    L_0x03bc:
        r9 = 0;
        r32 = 0;
    L_0x03bf:
        r33 = 0;
    L_0x03c1:
        if (r28 == 0) goto L_0x03c8;
    L_0x03c3:
        r3 = 0;
        r8 = 0;
        r14 = 0;
        r15 = 0;
        goto L_0x03ca;
    L_0x03c8:
        r8 = r29;
    L_0x03ca:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x02e2 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x02e2 }
        r5 = org.telegram.ui.LaunchActivity.class;
        r2.<init>(r4, r5);	 Catch:{ Exception -> 0x02e2 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02e2 }
        r4.<init>();	 Catch:{ Exception -> 0x02e2 }
        r5 = "com.tmessages.openchat";
        r4.append(r5);	 Catch:{ Exception -> 0x02e2 }
        r12 = java.lang.Math.random();	 Catch:{ Exception -> 0x02e2 }
        r4.append(r12);	 Catch:{ Exception -> 0x02e2 }
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4.append(r5);	 Catch:{ Exception -> 0x02e2 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x02e2 }
        r2.setAction(r4);	 Catch:{ Exception -> 0x02e2 }
        r4 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r2.setFlags(r4);	 Catch:{ Exception -> 0x02e2 }
        r4 = (int) r6;
        if (r4 == 0) goto L_0x04ab;
    L_0x03fa:
        r12 = r45;
        r5 = r12.pushDialogs;	 Catch:{ Exception -> 0x0af6 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0af6 }
        r13 = 1;
        if (r5 != r13) goto L_0x0415;
    L_0x0405:
        if (r10 == 0) goto L_0x040d;
    L_0x0407:
        r5 = "chatId";
        r2.putExtra(r5, r10);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0415;
    L_0x040d:
        if (r11 == 0) goto L_0x0415;
    L_0x040f:
        r5 = "userId";
        r2.putExtra(r5, r11);	 Catch:{ Exception -> 0x0af6 }
    L_0x0415:
        r5 = 0;
        r11 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r5);	 Catch:{ Exception -> 0x0af6 }
        if (r11 != 0) goto L_0x04a0;
    L_0x041c:
        r5 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0af6 }
        if (r5 == 0) goto L_0x0422;
    L_0x0420:
        goto L_0x04a0;
    L_0x0422:
        r5 = r12.pushDialogs;	 Catch:{ Exception -> 0x0af6 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0af6 }
        r11 = 1;
        if (r5 != r11) goto L_0x0499;
    L_0x042b:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r11 = 28;
        if (r5 >= r11) goto L_0x0499;
    L_0x0431:
        if (r27 == 0) goto L_0x0469;
    L_0x0433:
        r5 = r27;
        r11 = r5.photo;	 Catch:{ Exception -> 0x0af6 }
        if (r11 == 0) goto L_0x0462;
    L_0x0439:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0af6 }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0af6 }
        if (r11 == 0) goto L_0x0462;
    L_0x043f:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0af6 }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0af6 }
        r27 = r14;
        r13 = r11.volume_id;	 Catch:{ Exception -> 0x0af6 }
        r34 = 0;
        r11 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1));
        if (r11 == 0) goto L_0x0464;
    L_0x044d:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0af6 }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0af6 }
        r11 = r11.local_id;	 Catch:{ Exception -> 0x0af6 }
        if (r11 == 0) goto L_0x0464;
    L_0x0455:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0af6 }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0af6 }
        r29 = r8;
        r13 = r11;
        r11 = r25;
    L_0x045e:
        r25 = r9;
        goto L_0x04d5;
    L_0x0462:
        r27 = r14;
    L_0x0464:
        r29 = r8;
        r11 = r25;
        goto L_0x04a8;
    L_0x0469:
        r5 = r27;
        r27 = r14;
        if (r25 == 0) goto L_0x0496;
    L_0x046f:
        r11 = r25;
        r13 = r11.photo;	 Catch:{ Exception -> 0x0af6 }
        if (r13 == 0) goto L_0x04d1;
    L_0x0475:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0af6 }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0af6 }
        if (r13 == 0) goto L_0x04d1;
    L_0x047b:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0af6 }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0af6 }
        r13 = r13.volume_id;	 Catch:{ Exception -> 0x0af6 }
        r34 = 0;
        r25 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1));
        if (r25 == 0) goto L_0x04d1;
    L_0x0487:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0af6 }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0af6 }
        r13 = r13.local_id;	 Catch:{ Exception -> 0x0af6 }
        if (r13 == 0) goto L_0x04d1;
    L_0x048f:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0af6 }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0af6 }
        r29 = r8;
        goto L_0x045e;
    L_0x0496:
        r11 = r25;
        goto L_0x04d1;
    L_0x0499:
        r11 = r25;
        r5 = r27;
        r27 = r14;
        goto L_0x04d1;
    L_0x04a0:
        r11 = r25;
        r5 = r27;
        r27 = r14;
        r29 = r8;
    L_0x04a8:
        r25 = r9;
        goto L_0x04d4;
    L_0x04ab:
        r12 = r45;
        r11 = r25;
        r5 = r27;
        r27 = r14;
        r13 = r12.pushDialogs;	 Catch:{ Exception -> 0x0af6 }
        r13 = r13.size();	 Catch:{ Exception -> 0x0af6 }
        r14 = 1;
        if (r13 != r14) goto L_0x04d1;
    L_0x04bc:
        r13 = globalSecretChatId;	 Catch:{ Exception -> 0x0af6 }
        r25 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1));
        if (r25 == 0) goto L_0x04d1;
    L_0x04c2:
        r13 = "encId";
        r14 = 32;
        r29 = r8;
        r25 = r9;
        r8 = r6 >> r14;
        r9 = (int) r8;	 Catch:{ Exception -> 0x0af6 }
        r2.putExtra(r13, r9);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x04d4;
    L_0x04d1:
        r29 = r8;
        goto L_0x04a8;
    L_0x04d4:
        r13 = 0;
    L_0x04d5:
        r8 = r12.currentAccount;	 Catch:{ Exception -> 0x0af6 }
        r9 = r21;
        r2.putExtra(r9, r8);	 Catch:{ Exception -> 0x0af6 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0af6 }
        r14 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r36 = r6;
        r6 = 0;
        r2 = android.app.PendingIntent.getActivity(r8, r6, r2, r14);	 Catch:{ Exception -> 0x0af6 }
        if (r10 == 0) goto L_0x04eb;
    L_0x04e9:
        if (r5 == 0) goto L_0x04ed;
    L_0x04eb:
        if (r11 != 0) goto L_0x04f8;
    L_0x04ed:
        r6 = r20.isFcmMessage();	 Catch:{ Exception -> 0x0af6 }
        if (r6 == 0) goto L_0x04f8;
    L_0x04f3:
        r6 = r20;
        r7 = r6.localName;	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0503;
    L_0x04f8:
        r6 = r20;
        if (r5 == 0) goto L_0x04ff;
    L_0x04fc:
        r7 = r5.title;	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0503;
    L_0x04ff:
        r7 = org.telegram.messenger.UserObject.getUserName(r11);	 Catch:{ Exception -> 0x0af6 }
    L_0x0503:
        if (r4 == 0) goto L_0x051d;
    L_0x0505:
        r4 = r12.pushDialogs;	 Catch:{ Exception -> 0x0af6 }
        r4 = r4.size();	 Catch:{ Exception -> 0x0af6 }
        r8 = 1;
        if (r4 > r8) goto L_0x051d;
    L_0x050e:
        r4 = 0;
        r8 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r4);	 Catch:{ Exception -> 0x0af6 }
        if (r8 != 0) goto L_0x051d;
    L_0x0515:
        r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0af6 }
        if (r4 == 0) goto L_0x051a;
    L_0x0519:
        goto L_0x051d;
    L_0x051a:
        r4 = r7;
        r8 = 1;
        goto L_0x0527;
    L_0x051d:
        r4 = "AppName";
        r8 = NUM; // 0x7f0d00ef float:1.87426E38 double:1.0531298956E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r8);	 Catch:{ Exception -> 0x0af6 }
        r8 = 0;
    L_0x0527:
        r10 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();	 Catch:{ Exception -> 0x0af6 }
        r14 = "";
        r20 = r7;
        r7 = 1;
        if (r10 <= r7) goto L_0x0566;
    L_0x0532:
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0af6 }
        r10 = r10.size();	 Catch:{ Exception -> 0x0af6 }
        if (r10 != r7) goto L_0x0547;
    L_0x053a:
        r7 = r45.getUserConfig();	 Catch:{ Exception -> 0x0af6 }
        r7 = r7.getCurrentUser();	 Catch:{ Exception -> 0x0af6 }
        r7 = org.telegram.messenger.UserObject.getFirstName(r7);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0567;
    L_0x0547:
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r7.<init>();	 Catch:{ Exception -> 0x0af6 }
        r10 = r45.getUserConfig();	 Catch:{ Exception -> 0x0af6 }
        r10 = r10.getCurrentUser();	 Catch:{ Exception -> 0x0af6 }
        r10 = org.telegram.messenger.UserObject.getFirstName(r10);	 Catch:{ Exception -> 0x0af6 }
        r7.append(r10);	 Catch:{ Exception -> 0x0af6 }
        r10 = "・";
        r7.append(r10);	 Catch:{ Exception -> 0x0af6 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0567;
    L_0x0566:
        r7 = r14;
    L_0x0567:
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0af6 }
        r10 = r10.size();	 Catch:{ Exception -> 0x0af6 }
        r21 = r1;
        r1 = 1;
        if (r10 != r1) goto L_0x057f;
    L_0x0572:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r10 = 23;
        if (r1 >= r10) goto L_0x0579;
    L_0x0578:
        goto L_0x057f;
    L_0x0579:
        r40 = r3;
        r39 = r15;
    L_0x057d:
        r15 = r7;
        goto L_0x05da;
    L_0x057f:
        r1 = r12.pushDialogs;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.size();	 Catch:{ Exception -> 0x0af6 }
        r10 = 1;
        if (r1 != r10) goto L_0x05a0;
    L_0x0588:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r1.<init>();	 Catch:{ Exception -> 0x0af6 }
        r1.append(r7);	 Catch:{ Exception -> 0x0af6 }
        r7 = "NewMessages";
        r10 = r12.total_unread_count;	 Catch:{ Exception -> 0x0af6 }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r10);	 Catch:{ Exception -> 0x0af6 }
        r1.append(r7);	 Catch:{ Exception -> 0x0af6 }
        r7 = r1.toString();	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0579;
    L_0x05a0:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r1.<init>();	 Catch:{ Exception -> 0x0af6 }
        r1.append(r7);	 Catch:{ Exception -> 0x0af6 }
        r7 = "NotificationMessagesPeopleDisplayOrder";
        r39 = r15;
        r10 = 2;
        r15 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0af6 }
        r10 = "NewMessages";
        r40 = r3;
        r3 = r12.total_unread_count;	 Catch:{ Exception -> 0x0af6 }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r10, r3);	 Catch:{ Exception -> 0x0af6 }
        r10 = 0;
        r15[r10] = r3;	 Catch:{ Exception -> 0x0af6 }
        r3 = "FromChats";
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0af6 }
        r10 = r10.size();	 Catch:{ Exception -> 0x0af6 }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r10);	 Catch:{ Exception -> 0x0af6 }
        r10 = 1;
        r15[r10] = r3;	 Catch:{ Exception -> 0x0af6 }
        r3 = NUM; // 0x7f0d06a2 float:1.8745559E38 double:1.0531306165E-314;
        r3 = org.telegram.messenger.LocaleController.formatString(r7, r3, r15);	 Catch:{ Exception -> 0x0af6 }
        r1.append(r3);	 Catch:{ Exception -> 0x0af6 }
        r7 = r1.toString();	 Catch:{ Exception -> 0x0af6 }
        goto L_0x057d;
    L_0x05da:
        r10 = new androidx.core.app.NotificationCompat$Builder;	 Catch:{ Exception -> 0x0af6 }
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0af6 }
        r10.<init>(r1);	 Catch:{ Exception -> 0x0af6 }
        r10.setContentTitle(r4);	 Catch:{ Exception -> 0x0af6 }
        r1 = NUM; // 0x7var_ float:1.7945655E38 double:1.0529357654E-314;
        r10.setSmallIcon(r1);	 Catch:{ Exception -> 0x0af6 }
        r1 = 1;
        r10.setAutoCancel(r1);	 Catch:{ Exception -> 0x0af6 }
        r1 = r12.total_unread_count;	 Catch:{ Exception -> 0x0af6 }
        r10.setNumber(r1);	 Catch:{ Exception -> 0x0af6 }
        r10.setContentIntent(r2);	 Catch:{ Exception -> 0x0af6 }
        r1 = r12.notificationGroup;	 Catch:{ Exception -> 0x0af6 }
        r10.setGroup(r1);	 Catch:{ Exception -> 0x0af6 }
        r1 = 1;
        r10.setGroupSummary(r1);	 Catch:{ Exception -> 0x0af6 }
        r10.setShowWhen(r1);	 Catch:{ Exception -> 0x0af6 }
        r1 = r6.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.date;	 Catch:{ Exception -> 0x0af6 }
        r1 = (long) r1;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1 * r22;
        r10.setWhen(r1);	 Catch:{ Exception -> 0x0af6 }
        r1 = -15618822; // 0xfffffffffvar_acfa float:-1.936362E38 double:NaN;
        r10.setColor(r1);	 Catch:{ Exception -> 0x0af6 }
        r1 = "msg";
        r10.setCategory(r1);	 Catch:{ Exception -> 0x0af6 }
        if (r5 != 0) goto L_0x063d;
    L_0x0619:
        if (r11 == 0) goto L_0x063d;
    L_0x061b:
        r1 = r11.phone;	 Catch:{ Exception -> 0x0af6 }
        if (r1 == 0) goto L_0x063d;
    L_0x061f:
        r1 = r11.phone;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.length();	 Catch:{ Exception -> 0x0af6 }
        if (r1 <= 0) goto L_0x063d;
    L_0x0627:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r1.<init>();	 Catch:{ Exception -> 0x0af6 }
        r2 = "tel:+";
        r1.append(r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = r11.phone;	 Catch:{ Exception -> 0x0af6 }
        r1.append(r2);	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0af6 }
        r10.addPerson(r1);	 Catch:{ Exception -> 0x0af6 }
    L_0x063d:
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.size();	 Catch:{ Exception -> 0x0af6 }
        r2 = 1;
        if (r1 != r2) goto L_0x06bf;
    L_0x0646:
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0af6 }
        r3 = 0;
        r1 = r1.get(r3);	 Catch:{ Exception -> 0x0af6 }
        r1 = (org.telegram.messenger.MessageObject) r1;	 Catch:{ Exception -> 0x0af6 }
        r7 = new boolean[r2];	 Catch:{ Exception -> 0x0af6 }
        r2 = 0;
        r11 = r12.getStringForMessage(r1, r3, r7, r2);	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.silent;	 Catch:{ Exception -> 0x0af6 }
        if (r11 != 0) goto L_0x065d;
    L_0x065c:
        return;
    L_0x065d:
        if (r8 == 0) goto L_0x06a8;
    L_0x065f:
        if (r5 == 0) goto L_0x0677;
    L_0x0661:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r2.<init>();	 Catch:{ Exception -> 0x0af6 }
        r3 = " @ ";
        r2.append(r3);	 Catch:{ Exception -> 0x0af6 }
        r2.append(r4);	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0af6 }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x06a9;
    L_0x0677:
        r2 = 0;
        r3 = r7[r2];	 Catch:{ Exception -> 0x0af6 }
        if (r3 == 0) goto L_0x0692;
    L_0x067c:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r2.<init>();	 Catch:{ Exception -> 0x0af6 }
        r2.append(r4);	 Catch:{ Exception -> 0x0af6 }
        r3 = ": ";
        r2.append(r3);	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0af6 }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x06a9;
    L_0x0692:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r2.<init>();	 Catch:{ Exception -> 0x0af6 }
        r2.append(r4);	 Catch:{ Exception -> 0x0af6 }
        r3 = " ";
        r2.append(r3);	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0af6 }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x06a9;
    L_0x06a8:
        r2 = r11;
    L_0x06a9:
        r10.setContentText(r2);	 Catch:{ Exception -> 0x0af6 }
        r3 = new androidx.core.app.NotificationCompat$BigTextStyle;	 Catch:{ Exception -> 0x0af6 }
        r3.<init>();	 Catch:{ Exception -> 0x0af6 }
        r3.bigText(r2);	 Catch:{ Exception -> 0x0af6 }
        r10.setStyle(r3);	 Catch:{ Exception -> 0x0af6 }
        r44 = r6;
        r43 = r9;
        r42 = r13;
        goto L_0x077f;
    L_0x06bf:
        r10.setContentText(r15);	 Catch:{ Exception -> 0x0af6 }
        r1 = new androidx.core.app.NotificationCompat$InboxStyle;	 Catch:{ Exception -> 0x0af6 }
        r1.<init>();	 Catch:{ Exception -> 0x0af6 }
        r1.setBigContentTitle(r4);	 Catch:{ Exception -> 0x0af6 }
        r2 = 10;
        r3 = r12.pushMessages;	 Catch:{ Exception -> 0x0af6 }
        r3 = r3.size();	 Catch:{ Exception -> 0x0af6 }
        r2 = java.lang.Math.min(r2, r3);	 Catch:{ Exception -> 0x0af6 }
        r3 = 1;
        r7 = new boolean[r3];	 Catch:{ Exception -> 0x0af6 }
        r3 = 0;
        r11 = 2;
        r38 = 0;
    L_0x06dd:
        if (r3 >= r2) goto L_0x0770;
    L_0x06df:
        r41 = r2;
        r2 = r12.pushMessages;	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.get(r3);	 Catch:{ Exception -> 0x0af6 }
        r2 = (org.telegram.messenger.MessageObject) r2;	 Catch:{ Exception -> 0x0af6 }
        r44 = r6;
        r43 = r9;
        r42 = r13;
        r9 = 0;
        r13 = 0;
        r6 = r12.getStringForMessage(r2, r9, r7, r13);	 Catch:{ Exception -> 0x0af6 }
        if (r6 == 0) goto L_0x0760;
    L_0x06f7:
        r9 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r9 = r9.date;	 Catch:{ Exception -> 0x0af6 }
        r13 = r19;
        if (r9 > r13) goto L_0x0700;
    L_0x06ff:
        goto L_0x0762;
    L_0x0700:
        r9 = 2;
        if (r11 != r9) goto L_0x0709;
    L_0x0703:
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r11 = r2.silent;	 Catch:{ Exception -> 0x0af6 }
        r38 = r6;
    L_0x0709:
        r2 = r12.pushDialogs;	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0af6 }
        r9 = 1;
        if (r2 != r9) goto L_0x075c;
    L_0x0712:
        if (r8 == 0) goto L_0x075c;
    L_0x0714:
        if (r5 == 0) goto L_0x072c;
    L_0x0716:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r2.<init>();	 Catch:{ Exception -> 0x0af6 }
        r9 = " @ ";
        r2.append(r9);	 Catch:{ Exception -> 0x0af6 }
        r2.append(r4);	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0af6 }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x075c;
    L_0x072c:
        r2 = 0;
        r9 = r7[r2];	 Catch:{ Exception -> 0x0af6 }
        if (r9 == 0) goto L_0x0747;
    L_0x0731:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r2.<init>();	 Catch:{ Exception -> 0x0af6 }
        r2.append(r4);	 Catch:{ Exception -> 0x0af6 }
        r9 = ": ";
        r2.append(r9);	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0af6 }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x075c;
    L_0x0747:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r2.<init>();	 Catch:{ Exception -> 0x0af6 }
        r2.append(r4);	 Catch:{ Exception -> 0x0af6 }
        r9 = " ";
        r2.append(r9);	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0af6 }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0af6 }
    L_0x075c:
        r1.addLine(r6);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0762;
    L_0x0760:
        r13 = r19;
    L_0x0762:
        r3 = r3 + 1;
        r19 = r13;
        r2 = r41;
        r13 = r42;
        r9 = r43;
        r6 = r44;
        goto L_0x06dd;
    L_0x0770:
        r44 = r6;
        r43 = r9;
        r42 = r13;
        r1.setSummaryText(r15);	 Catch:{ Exception -> 0x0af6 }
        r10.setStyle(r1);	 Catch:{ Exception -> 0x0af6 }
        r1 = r11;
        r11 = r38;
    L_0x077f:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x0af6 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0af6 }
        r4 = org.telegram.messenger.NotificationDismissReceiver.class;
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x0af6 }
        r3 = "messageDate";
        r4 = r44;
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r5 = r5.date;	 Catch:{ Exception -> 0x0af6 }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0af6 }
        r3 = r12.currentAccount;	 Catch:{ Exception -> 0x0af6 }
        r5 = r43;
        r2.putExtra(r5, r3);	 Catch:{ Exception -> 0x0af6 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0af6 }
        r6 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r7 = 1;
        r2 = android.app.PendingIntent.getBroadcast(r3, r7, r2, r6);	 Catch:{ Exception -> 0x0af6 }
        r10.setDeleteIntent(r2);	 Catch:{ Exception -> 0x0af6 }
        if (r42 == 0) goto L_0x07f2;
    L_0x07a8:
        r2 = org.telegram.messenger.ImageLoader.getInstance();	 Catch:{ Exception -> 0x0af6 }
        r3 = "50_50";
        r13 = r42;
        r7 = 0;
        r2 = r2.getImageFromMemory(r13, r7, r3);	 Catch:{ Exception -> 0x0af6 }
        if (r2 == 0) goto L_0x07bf;
    L_0x07b7:
        r2 = r2.getBitmap();	 Catch:{ Exception -> 0x0af6 }
        r10.setLargeIcon(r2);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x07f3;
    L_0x07bf:
        r2 = 1;
        r3 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r2);	 Catch:{ Throwable -> 0x07f3 }
        r2 = r3.exists();	 Catch:{ Throwable -> 0x07f3 }
        if (r2 == 0) goto L_0x07f3;
    L_0x07ca:
        r2 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r8 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x07f3 }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x07f3 }
        r2 = r2 / r8;
        r8 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x07f3 }
        r8.<init>();	 Catch:{ Throwable -> 0x07f3 }
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r9 >= 0) goto L_0x07e1;
    L_0x07df:
        r2 = 1;
        goto L_0x07e2;
    L_0x07e1:
        r2 = (int) r2;	 Catch:{ Throwable -> 0x07f3 }
    L_0x07e2:
        r8.inSampleSize = r2;	 Catch:{ Throwable -> 0x07f3 }
        r2 = r3.getAbsolutePath();	 Catch:{ Throwable -> 0x07f3 }
        r2 = android.graphics.BitmapFactory.decodeFile(r2, r8);	 Catch:{ Throwable -> 0x07f3 }
        if (r2 == 0) goto L_0x07f3;
    L_0x07ee:
        r10.setLargeIcon(r2);	 Catch:{ Throwable -> 0x07f3 }
        goto L_0x07f3;
    L_0x07f2:
        r7 = 0;
    L_0x07f3:
        r13 = r46;
        if (r13 == 0) goto L_0x083e;
    L_0x07f7:
        r2 = 1;
        if (r1 != r2) goto L_0x07fb;
    L_0x07fa:
        goto L_0x083e;
    L_0x07fb:
        if (r40 != 0) goto L_0x080a;
    L_0x07fd:
        r2 = 0;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r3 = 26;
        if (r2 < r3) goto L_0x084b;
    L_0x0807:
        r2 = 1;
        r8 = 3;
        goto L_0x084d;
    L_0x080a:
        r3 = r40;
        r2 = 1;
        if (r3 == r2) goto L_0x0832;
    L_0x080f:
        r2 = 2;
        if (r3 != r2) goto L_0x0814;
    L_0x0812:
        r2 = 1;
        goto L_0x0832;
    L_0x0814:
        r2 = 4;
        if (r3 != r2) goto L_0x0824;
    L_0x0817:
        r2 = -2;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r3 = 26;
        if (r2 < r3) goto L_0x084b;
    L_0x0821:
        r2 = 1;
        r8 = 1;
        goto L_0x084d;
    L_0x0824:
        r2 = 5;
        if (r3 != r2) goto L_0x084b;
    L_0x0827:
        r2 = -1;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r3 = 26;
        if (r2 < r3) goto L_0x084b;
    L_0x0831:
        goto L_0x0848;
    L_0x0832:
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r3 = 26;
        if (r2 < r3) goto L_0x084b;
    L_0x083b:
        r2 = 1;
        r8 = 4;
        goto L_0x084d;
    L_0x083e:
        r2 = -1;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r3 = 26;
        if (r2 < r3) goto L_0x084b;
    L_0x0848:
        r2 = 1;
        r8 = 2;
        goto L_0x084d;
    L_0x084b:
        r2 = 1;
        r8 = 0;
    L_0x084d:
        if (r1 == r2) goto L_0x0977;
    L_0x084f:
        if (r28 != 0) goto L_0x0977;
    L_0x0851:
        r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0af6 }
        if (r1 != 0) goto L_0x0857;
    L_0x0855:
        if (r24 == 0) goto L_0x0886;
    L_0x0857:
        r1 = r11.length();	 Catch:{ Exception -> 0x0af6 }
        r2 = 100;
        if (r1 <= r2) goto L_0x0883;
    L_0x085f:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af6 }
        r1.<init>();	 Catch:{ Exception -> 0x0af6 }
        r2 = 100;
        r3 = 0;
        r2 = r11.substring(r3, r2);	 Catch:{ Exception -> 0x0af6 }
        r3 = 10;
        r9 = 32;
        r2 = r2.replace(r3, r9);	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.trim();	 Catch:{ Exception -> 0x0af6 }
        r1.append(r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = "...";
        r1.append(r2);	 Catch:{ Exception -> 0x0af6 }
        r11 = r1.toString();	 Catch:{ Exception -> 0x0af6 }
    L_0x0883:
        r10.setTicker(r11);	 Catch:{ Exception -> 0x0af6 }
    L_0x0886:
        r1 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.isRecordingAudio();	 Catch:{ Exception -> 0x0af6 }
        if (r1 != 0) goto L_0x090a;
    L_0x0890:
        if (r39 == 0) goto L_0x090a;
    L_0x0892:
        r1 = "NoSound";
        r2 = r39;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0af6 }
        if (r1 != 0) goto L_0x090a;
    L_0x089c:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r3 = 26;
        if (r1 < r3) goto L_0x08b2;
    L_0x08a2:
        r1 = r21;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0af6 }
        if (r1 == 0) goto L_0x08ad;
    L_0x08aa:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0af6 }
        goto L_0x090b;
    L_0x08ad:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x090b;
    L_0x08b2:
        r1 = r21;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0af6 }
        if (r1 == 0) goto L_0x08c1;
    L_0x08ba:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0af6 }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x090a;
    L_0x08c1:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r3 = 24;
        if (r1 < r3) goto L_0x0902;
    L_0x08c7:
        r1 = "file://";
        r1 = r2.startsWith(r1);	 Catch:{ Exception -> 0x0af6 }
        if (r1 == 0) goto L_0x0902;
    L_0x08cf:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0af6 }
        r1 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1);	 Catch:{ Exception -> 0x0af6 }
        if (r1 != 0) goto L_0x0902;
    L_0x08d9:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x08f9 }
        r3 = "org.telegram.messenger.provider";
        r9 = new java.io.File;	 Catch:{ Exception -> 0x08f9 }
        r11 = "file://";
        r11 = r2.replace(r11, r14);	 Catch:{ Exception -> 0x08f9 }
        r9.<init>(r11);	 Catch:{ Exception -> 0x08f9 }
        r1 = androidx.core.content.FileProvider.getUriForFile(r1, r3, r9);	 Catch:{ Exception -> 0x08f9 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x08f9 }
        r9 = "com.android.systemui";
        r11 = 1;
        r3.grantUriPermission(r9, r1, r11);	 Catch:{ Exception -> 0x08f9 }
        r3 = 5;
        r10.setSound(r1, r3);	 Catch:{ Exception -> 0x08f9 }
        goto L_0x090a;
    L_0x08f9:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x090a;
    L_0x0902:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0af6 }
    L_0x090a:
        r1 = r7;
    L_0x090b:
        if (r29 == 0) goto L_0x0917;
    L_0x090d:
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r9 = r29;
        r10.setLights(r9, r2, r3);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0919;
    L_0x0917:
        r9 = r29;
    L_0x0919:
        r14 = r27;
        r2 = 2;
        if (r14 == r2) goto L_0x0965;
    L_0x091e:
        r2 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.isRecordingAudio();	 Catch:{ Exception -> 0x0af6 }
        if (r2 == 0) goto L_0x092a;
    L_0x0928:
        r2 = 2;
        goto L_0x0965;
    L_0x092a:
        r2 = 1;
        if (r14 != r2) goto L_0x0943;
    L_0x092d:
        r3 = 4;
        r3 = new long[r3];	 Catch:{ Exception -> 0x0af6 }
        r7 = 0;
        r21 = 0;
        r3[r7] = r21;	 Catch:{ Exception -> 0x0af6 }
        r27 = 100;
        r3[r2] = r27;	 Catch:{ Exception -> 0x0af6 }
        r2 = 2;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0af6 }
        r2 = 3;
        r3[r2] = r27;	 Catch:{ Exception -> 0x0af6 }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0972;
    L_0x0943:
        if (r14 == 0) goto L_0x095d;
    L_0x0945:
        r2 = 4;
        if (r14 != r2) goto L_0x0949;
    L_0x0948:
        goto L_0x095d;
    L_0x0949:
        r2 = 3;
        if (r14 != r2) goto L_0x095b;
    L_0x094c:
        r2 = 2;
        r3 = new long[r2];	 Catch:{ Exception -> 0x0af6 }
        r2 = 0;
        r18 = 0;
        r3[r2] = r18;	 Catch:{ Exception -> 0x0af6 }
        r2 = 1;
        r3[r2] = r22;	 Catch:{ Exception -> 0x0af6 }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0972;
    L_0x095b:
        r11 = r1;
        goto L_0x0974;
    L_0x095d:
        r2 = 2;
        r10.setDefaults(r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = 0;
        r3 = new long[r2];	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0972;
    L_0x0965:
        r3 = new long[r2];	 Catch:{ Exception -> 0x0af6 }
        r2 = 0;
        r21 = 0;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0af6 }
        r2 = 1;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0af6 }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0af6 }
    L_0x0972:
        r11 = r1;
        r7 = r3;
    L_0x0974:
        r1 = 0;
        r3 = 1;
        goto L_0x0989;
    L_0x0977:
        r9 = r29;
        r1 = 2;
        r2 = new long[r1];	 Catch:{ Exception -> 0x0af6 }
        r1 = 0;
        r18 = 0;
        r2[r1] = r18;	 Catch:{ Exception -> 0x0af6 }
        r3 = 1;
        r2[r3] = r18;	 Catch:{ Exception -> 0x0af6 }
        r10.setVibrate(r2);	 Catch:{ Exception -> 0x0af6 }
        r11 = r7;
        r7 = r2;
    L_0x0989:
        r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1);	 Catch:{ Exception -> 0x0af6 }
        if (r2 != 0) goto L_0x0a66;
    L_0x098f:
        r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0af6 }
        if (r1 != 0) goto L_0x0a66;
    L_0x0993:
        r1 = r4.getDialogId();	 Catch:{ Exception -> 0x0af6 }
        r16 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r14 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1));
        if (r14 != 0) goto L_0x0a66;
    L_0x099e:
        r1 = r4.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.reply_markup;	 Catch:{ Exception -> 0x0af6 }
        if (r1 == 0) goto L_0x0a66;
    L_0x09a4:
        r1 = r4.messageOwner;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.reply_markup;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.rows;	 Catch:{ Exception -> 0x0af6 }
        r2 = r1.size();	 Catch:{ Exception -> 0x0af6 }
        r14 = 0;
        r16 = 0;
    L_0x09b1:
        if (r14 >= r2) goto L_0x0a5e;
    L_0x09b3:
        r17 = r1.get(r14);	 Catch:{ Exception -> 0x0af6 }
        r3 = r17;
        r3 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r3;	 Catch:{ Exception -> 0x0af6 }
        r6 = r3.buttons;	 Catch:{ Exception -> 0x0af6 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0af6 }
        r21 = r1;
        r1 = 0;
    L_0x09c4:
        if (r1 >= r6) goto L_0x0a42;
    L_0x09c6:
        r22 = r2;
        r2 = r3.buttons;	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.get(r1);	 Catch:{ Exception -> 0x0af6 }
        r2 = (org.telegram.tgnet.TLRPC.KeyboardButton) r2;	 Catch:{ Exception -> 0x0af6 }
        r23 = r3;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;	 Catch:{ Exception -> 0x0af6 }
        if (r3 == 0) goto L_0x0a22;
    L_0x09d6:
        r3 = new android.content.Intent;	 Catch:{ Exception -> 0x0af6 }
        r24 = r6;
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0af6 }
        r13 = org.telegram.messenger.NotificationCallbackReceiver.class;
        r3.<init>(r6, r13);	 Catch:{ Exception -> 0x0af6 }
        r6 = r12.currentAccount;	 Catch:{ Exception -> 0x0af6 }
        r3.putExtra(r5, r6);	 Catch:{ Exception -> 0x0af6 }
        r6 = "did";
        r13 = r8;
        r29 = r9;
        r8 = r36;
        r3.putExtra(r6, r8);	 Catch:{ Exception -> 0x0af6 }
        r6 = r2.data;	 Catch:{ Exception -> 0x0af6 }
        if (r6 == 0) goto L_0x09fe;
    L_0x09f4:
        r6 = "data";
        r26 = r15;
        r15 = r2.data;	 Catch:{ Exception -> 0x0af6 }
        r3.putExtra(r6, r15);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0a00;
    L_0x09fe:
        r26 = r15;
    L_0x0a00:
        r6 = "mid";
        r15 = r4.getId();	 Catch:{ Exception -> 0x0af6 }
        r3.putExtra(r6, r15);	 Catch:{ Exception -> 0x0af6 }
        r2 = r2.text;	 Catch:{ Exception -> 0x0af6 }
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0af6 }
        r15 = r12.lastButtonId;	 Catch:{ Exception -> 0x0af6 }
        r44 = r4;
        r4 = r15 + 1;
        r12.lastButtonId = r4;	 Catch:{ Exception -> 0x0af6 }
        r4 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r3 = android.app.PendingIntent.getBroadcast(r6, r15, r3, r4);	 Catch:{ Exception -> 0x0af6 }
        r4 = 0;
        r10.addAction(r4, r2, r3);	 Catch:{ Exception -> 0x0af6 }
        r16 = 1;
        goto L_0x0a2e;
    L_0x0a22:
        r44 = r4;
        r24 = r6;
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        r4 = 0;
    L_0x0a2e:
        r1 = r1 + 1;
        r36 = r8;
        r8 = r13;
        r2 = r22;
        r3 = r23;
        r6 = r24;
        r15 = r26;
        r9 = r29;
        r4 = r44;
        r13 = r46;
        goto L_0x09c4;
    L_0x0a42:
        r22 = r2;
        r44 = r4;
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        r4 = 0;
        r14 = r14 + 1;
        r8 = r13;
        r1 = r21;
        r9 = r29;
        r4 = r44;
        r3 = 1;
        r6 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r13 = r46;
        goto L_0x09b1;
    L_0x0a5e:
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        goto L_0x0a70;
    L_0x0a66:
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        r4 = 0;
        r16 = 0;
    L_0x0a70:
        if (r16 != 0) goto L_0x0acb;
    L_0x0a72:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r2 = 24;
        if (r1 >= r2) goto L_0x0acb;
    L_0x0a78:
        r1 = org.telegram.messenger.SharedConfig.passcodeHash;	 Catch:{ Exception -> 0x0af6 }
        r1 = r1.length();	 Catch:{ Exception -> 0x0af6 }
        if (r1 != 0) goto L_0x0acb;
    L_0x0a80:
        r1 = r45.hasMessagesToReply();	 Catch:{ Exception -> 0x0af6 }
        if (r1 == 0) goto L_0x0acb;
    L_0x0a86:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0af6 }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0af6 }
        r3 = org.telegram.messenger.PopupReplyReceiver.class;
        r1.<init>(r2, r3);	 Catch:{ Exception -> 0x0af6 }
        r2 = r12.currentAccount;	 Catch:{ Exception -> 0x0af6 }
        r1.putExtra(r5, r2);	 Catch:{ Exception -> 0x0af6 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r3 = 19;
        if (r2 > r3) goto L_0x0ab3;
    L_0x0a9a:
        r2 = NUM; // 0x7var_ float:1.79451E38 double:1.05293563E-314;
        r3 = "Reply";
        r4 = NUM; // 0x7f0d08b2 float:1.874663E38 double:1.0531308773E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0af6 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0af6 }
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r6 = 2;
        r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5);	 Catch:{ Exception -> 0x0af6 }
        r10.addAction(r2, r3, r1);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0acb;
    L_0x0ab3:
        r2 = NUM; // 0x7var_ float:1.7945097E38 double:1.0529356295E-314;
        r3 = "Reply";
        r4 = NUM; // 0x7f0d08b2 float:1.874663E38 double:1.0531308773E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0af6 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0af6 }
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r6 = 2;
        r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5);	 Catch:{ Exception -> 0x0af6 }
        r10.addAction(r2, r3, r1);	 Catch:{ Exception -> 0x0af6 }
    L_0x0acb:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af6 }
        r2 = 26;
        if (r1 < r2) goto L_0x0aea;
    L_0x0ad1:
        r1 = r45;
        r2 = r8;
        r4 = r20;
        r5 = r7;
        r6 = r29;
        r7 = r11;
        r8 = r13;
        r9 = r25;
        r13 = r10;
        r10 = r32;
        r11 = r33;
        r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0af6 }
        r13.setChannelId(r1);	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0aeb;
    L_0x0aea:
        r13 = r10;
    L_0x0aeb:
        r1 = r46;
        r7 = r26;
        r12.showExtraNotifications(r13, r1, r7);	 Catch:{ Exception -> 0x0af6 }
        r45.scheduleNotificationRepeat();	 Catch:{ Exception -> 0x0af6 }
        goto L_0x0afb;
    L_0x0af6:
        r0 = move-exception;
    L_0x0af7:
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0afb:
        return;
    L_0x0afc:
        r45.dismissNotification();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:134:0x02ed  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x029e  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0358  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0346  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x039a  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x03fc  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0419  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0753  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x0741  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0777  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x0771  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x07d0  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x0846  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0824  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x08f2  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x08e8  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x08f5  */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0913  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0918  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x092c  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x09de  */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x0a31 A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x0a5a A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x0a6c A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x0a65 A:{SYNTHETIC, Splitter:B:377:0x0a65} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x029e  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x02ed  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02fb A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0346  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0358  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x039a  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x03ae  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x03e3 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x03fc  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0419  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x0741  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0753  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x0771  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0777  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x07d0  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0824  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x0846  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x08e8  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x08f2  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x08f5  */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0903  */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0913  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0918  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x092c  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x09de  */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x0a31 A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x0a5a A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x0a65 A:{SYNTHETIC, Splitter:B:377:0x0a65} */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x0a6c A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x02ed  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x029e  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02fb A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0358  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0346  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x039a  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x03ae  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x03e3 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x03fc  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0419  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0753  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x0741  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0777  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x0771  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x07d0  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x0846  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0824  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x08f2  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x08e8  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x08f5  */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0903  */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0913  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0918  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x092c  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x09de  */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x0a31 A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x0a5a A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x0a6c A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x0a65 A:{SYNTHETIC, Splitter:B:377:0x0a65} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x029e  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x02ed  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02fb A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0346  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0358  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x039a  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x03ae  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x03e3 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x03fc  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0419  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x0741  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0753  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x0771  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0777  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x07d0  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0824  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x0846  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x08e8  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x08f2  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x08f5  */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0903  */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0913  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0918  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x092c  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x09de  */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x0a31 A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x0a5a A:{Catch:{ JSONException -> 0x0a84 }} */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x0a65 A:{SYNTHETIC, Splitter:B:377:0x0a65} */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x0a6c A:{Catch:{ JSONException -> 0x0a84 }} */
    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r61, boolean r62, java.lang.String r63) {
        /*
        r60 = this;
        r0 = r60;
        r1 = r61.build();
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 18;
        if (r2 >= r3) goto L_0x001d;
    L_0x000c:
        r2 = notificationManager;
        r3 = r0.notificationId;
        r2.notify(r3, r1);
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x001c;
    L_0x0017:
        r1 = "show summary notification by SDK check";
        org.telegram.messenger.FileLog.d(r1);
    L_0x001c:
        return;
    L_0x001d:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new android.util.LongSparseArray;
        r3.<init>();
        r4 = 0;
        r5 = 0;
    L_0x0029:
        r6 = r0.pushMessages;
        r6 = r6.size();
        if (r5 >= r6) goto L_0x005a;
    L_0x0031:
        r6 = r0.pushMessages;
        r6 = r6.get(r5);
        r6 = (org.telegram.messenger.MessageObject) r6;
        r7 = r6.getDialogId();
        r9 = r3.get(r7);
        r9 = (java.util.ArrayList) r9;
        if (r9 != 0) goto L_0x0054;
    L_0x0045:
        r9 = new java.util.ArrayList;
        r9.<init>();
        r3.put(r7, r9);
        r7 = java.lang.Long.valueOf(r7);
        r2.add(r4, r7);
    L_0x0054:
        r9.add(r6);
        r5 = r5 + 1;
        goto L_0x0029;
    L_0x005a:
        r5 = r0.wearNotificationsIds;
        r5 = r5.clone();
        r6 = r0.wearNotificationsIds;
        r6.clear();
        r6 = new java.util.ArrayList;
        r6.<init>();
        r7 = org.telegram.messenger.WearDataLayerListenerService.isWatchConnected();
        if (r7 == 0) goto L_0x0076;
    L_0x0070:
        r7 = new org.json.JSONArray;
        r7.<init>();
        goto L_0x0077;
    L_0x0076:
        r7 = 0;
    L_0x0077:
        r9 = android.os.Build.VERSION.SDK_INT;
        r10 = 27;
        r11 = 1;
        if (r9 <= r10) goto L_0x0089;
    L_0x007e:
        if (r9 <= r10) goto L_0x0087;
    L_0x0080:
        r9 = r2.size();
        if (r9 <= r11) goto L_0x0087;
    L_0x0086:
        goto L_0x0089;
    L_0x0087:
        r9 = 0;
        goto L_0x008a;
    L_0x0089:
        r9 = 1;
    L_0x008a:
        r10 = 26;
        if (r9 == 0) goto L_0x0095;
    L_0x008e:
        r12 = android.os.Build.VERSION.SDK_INT;
        if (r12 < r10) goto L_0x0095;
    L_0x0092:
        checkOtherNotificationsChannel();
    L_0x0095:
        r12 = r2.size();
        r13 = 0;
    L_0x009a:
        if (r13 >= r12) goto L_0x0a9b;
    L_0x009c:
        r14 = r2.get(r13);
        r14 = (java.lang.Long) r14;
        r14 = r14.longValue();
        r16 = r3.get(r14);
        r10 = r16;
        r10 = (java.util.ArrayList) r10;
        r16 = r10.get(r4);
        r16 = (org.telegram.messenger.MessageObject) r16;
        r8 = r16.getId();
        r11 = (int) r14;
        r18 = 32;
        r19 = r2;
        r20 = r3;
        r2 = r14 >> r18;
        r3 = (int) r2;
        r2 = r5.get(r14);
        r2 = (java.lang.Integer) r2;
        if (r2 != 0) goto L_0x00d6;
    L_0x00ca:
        if (r11 == 0) goto L_0x00d1;
    L_0x00cc:
        r2 = java.lang.Integer.valueOf(r11);
        goto L_0x00d9;
    L_0x00d1:
        r2 = java.lang.Integer.valueOf(r3);
        goto L_0x00d9;
    L_0x00d6:
        r5.remove(r14);
    L_0x00d9:
        if (r7 == 0) goto L_0x00e1;
    L_0x00db:
        r18 = new org.json.JSONObject;
        r18.<init>();
        goto L_0x00e3;
    L_0x00e1:
        r18 = 0;
    L_0x00e3:
        r21 = r10.get(r4);
        r4 = r21;
        r4 = (org.telegram.messenger.MessageObject) r4;
        r21 = r12;
        r12 = r4.messageOwner;
        r12 = r12.date;
        r23 = r5;
        r5 = new android.util.LongSparseArray;
        r5.<init>();
        r24 = 0;
        if (r11 == 0) goto L_0x01fa;
    L_0x00fc:
        r26 = r13;
        r13 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r11 == r13) goto L_0x0105;
    L_0x0103:
        r13 = 1;
        goto L_0x0106;
    L_0x0105:
        r13 = 0;
    L_0x0106:
        if (r11 <= 0) goto L_0x0174;
    L_0x0108:
        r27 = r13;
        r13 = r60.getMessagesController();
        r28 = r7;
        r7 = java.lang.Integer.valueOf(r11);
        r7 = r13.getUser(r7);
        if (r7 != 0) goto L_0x0148;
    L_0x011a:
        r13 = r4.isFcmMessage();
        if (r13 == 0) goto L_0x0127;
    L_0x0120:
        r4 = r4.localName;
        r29 = r6;
        r6 = r18;
        goto L_0x016f;
    L_0x0127:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x013f;
    L_0x012b:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not found user to show dialog notification ";
        r2.append(r3);
        r2.append(r11);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
    L_0x013f:
        r27 = r1;
        r3 = r6;
        r36 = r9;
        r7 = r28;
        goto L_0x0234;
    L_0x0148:
        r4 = org.telegram.messenger.UserObject.getUserName(r7);
        r13 = r7.photo;
        if (r13 == 0) goto L_0x0167;
    L_0x0150:
        r13 = r13.photo_small;
        if (r13 == 0) goto L_0x0167;
    L_0x0154:
        r29 = r6;
        r30 = r7;
        r6 = r13.volume_id;
        r31 = (r6 > r24 ? 1 : (r6 == r24 ? 0 : -1));
        if (r31 == 0) goto L_0x016b;
    L_0x015e:
        r6 = r13.local_id;
        if (r6 == 0) goto L_0x016b;
    L_0x0162:
        r6 = r18;
        r7 = r30;
        goto L_0x0170;
    L_0x0167:
        r29 = r6;
        r30 = r7;
    L_0x016b:
        r6 = r18;
        r7 = r30;
    L_0x016f:
        r13 = 0;
    L_0x0170:
        r18 = 0;
        goto L_0x0275;
    L_0x0174:
        r29 = r6;
        r28 = r7;
        r27 = r13;
        r6 = r60.getMessagesController();
        r7 = -r11;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getChat(r7);
        if (r6 != 0) goto L_0x01c0;
    L_0x0189:
        r7 = r4.isFcmMessage();
        if (r7 == 0) goto L_0x01a6;
    L_0x018f:
        r7 = r4.isMegagroup();
        r13 = r4.localName;
        r4 = r4.localChannel;
        r32 = r4;
        r31 = r6;
        r30 = r7;
    L_0x019d:
        r4 = r13;
        r6 = r18;
        r7 = 0;
        r13 = 0;
        r18 = 0;
        goto L_0x027b;
    L_0x01a6:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x022c;
    L_0x01aa:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not found chat to show dialog notification ";
        r2.append(r3);
        r2.append(r11);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
        goto L_0x022c;
    L_0x01c0:
        r4 = r6.megagroup;
        r7 = org.telegram.messenger.ChatObject.isChannel(r6);
        if (r7 == 0) goto L_0x01ce;
    L_0x01c8:
        r7 = r6.megagroup;
        if (r7 != 0) goto L_0x01ce;
    L_0x01cc:
        r7 = 1;
        goto L_0x01cf;
    L_0x01ce:
        r7 = 0;
    L_0x01cf:
        r13 = r6.title;
        r30 = r4;
        r4 = r6.photo;
        if (r4 == 0) goto L_0x01f5;
    L_0x01d7:
        r4 = r4.photo_small;
        if (r4 == 0) goto L_0x01f5;
    L_0x01db:
        r31 = r6;
        r32 = r7;
        r6 = r4.volume_id;
        r33 = (r6 > r24 ? 1 : (r6 == r24 ? 0 : -1));
        if (r33 == 0) goto L_0x019d;
    L_0x01e5:
        r6 = r4.local_id;
        if (r6 == 0) goto L_0x019d;
    L_0x01e9:
        r6 = r18;
        r7 = 0;
        r18 = 0;
        r59 = r13;
        r13 = r4;
        r4 = r59;
        goto L_0x027b;
    L_0x01f5:
        r31 = r6;
        r32 = r7;
        goto L_0x019d;
    L_0x01fa:
        r29 = r6;
        r28 = r7;
        r26 = r13;
        r6 = globalSecretChatId;
        r4 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x0264;
    L_0x0206:
        r4 = r60.getMessagesController();
        r6 = java.lang.Integer.valueOf(r3);
        r4 = r4.getEncryptedChat(r6);
        if (r4 != 0) goto L_0x0239;
    L_0x0214:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x022c;
    L_0x0218:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "not found secret chat to show dialog notification ";
        r2.append(r4);
        r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
    L_0x022c:
        r27 = r1;
        r36 = r9;
        r7 = r28;
        r3 = r29;
    L_0x0234:
        r2 = 26;
        r14 = 0;
        goto L_0x0a86;
    L_0x0239:
        r6 = r60.getMessagesController();
        r7 = r4.user_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getUser(r7);
        if (r6 != 0) goto L_0x0265;
    L_0x0249:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x022c;
    L_0x024d:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not found secret chat user to show dialog notification ";
        r2.append(r3);
        r3 = r4.user_id;
        r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
        goto L_0x022c;
    L_0x0264:
        r6 = 0;
    L_0x0265:
        r4 = NUM; // 0x7f0d0922 float:1.8746857E38 double:1.0531309327E-314;
        r7 = "SecretChatName";
        r4 = org.telegram.messenger.LocaleController.getString(r7, r4);
        r7 = r6;
        r6 = 0;
        r13 = 0;
        r18 = 0;
        r27 = 0;
    L_0x0275:
        r30 = 0;
        r31 = 0;
        r32 = 0;
    L_0x027b:
        r33 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r18);
        if (r33 != 0) goto L_0x028d;
    L_0x0281:
        r18 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r18 == 0) goto L_0x0286;
    L_0x0285:
        goto L_0x028d;
    L_0x0286:
        r18 = r12;
        r12 = r27;
        r27 = r1;
        goto L_0x029c;
    L_0x028d:
        r4 = NUM; // 0x7f0d00ef float:1.87426E38 double:1.0531298956E-314;
        r13 = "AppName";
        r4 = org.telegram.messenger.LocaleController.getString(r13, r4);
        r27 = r1;
        r18 = r12;
        r12 = 0;
        r13 = 0;
    L_0x029c:
        if (r13 == 0) goto L_0x02ed;
    L_0x029e:
        r1 = 1;
        r34 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r1);
        r1 = android.os.Build.VERSION.SDK_INT;
        r35 = r7;
        r7 = 28;
        if (r1 >= r7) goto L_0x02e8;
    L_0x02ab:
        r1 = org.telegram.messenger.ImageLoader.getInstance();
        r7 = "50_50";
        r36 = r9;
        r9 = 0;
        r1 = r1.getImageFromMemory(r13, r9, r7);
        if (r1 == 0) goto L_0x02bf;
    L_0x02ba:
        r1 = r1.getBitmap();
        goto L_0x02f5;
    L_0x02bf:
        r1 = r34.exists();	 Catch:{ Throwable -> 0x02eb }
        if (r1 == 0) goto L_0x02eb;
    L_0x02c5:
        r1 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r7 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);	 Catch:{ Throwable -> 0x02eb }
        r7 = (float) r7;	 Catch:{ Throwable -> 0x02eb }
        r1 = r1 / r7;
        r7 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x02eb }
        r7.<init>();	 Catch:{ Throwable -> 0x02eb }
        r17 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r17 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1));
        if (r17 >= 0) goto L_0x02dc;
    L_0x02da:
        r1 = 1;
        goto L_0x02dd;
    L_0x02dc:
        r1 = (int) r1;	 Catch:{ Throwable -> 0x02eb }
    L_0x02dd:
        r7.inSampleSize = r1;	 Catch:{ Throwable -> 0x02eb }
        r1 = r34.getAbsolutePath();	 Catch:{ Throwable -> 0x02eb }
        r1 = android.graphics.BitmapFactory.decodeFile(r1, r7);	 Catch:{ Throwable -> 0x02eb }
        goto L_0x02f5;
    L_0x02e8:
        r36 = r9;
        r9 = 0;
    L_0x02eb:
        r1 = r9;
        goto L_0x02f5;
    L_0x02ed:
        r35 = r7;
        r36 = r9;
        r9 = 0;
        r1 = r9;
        r34 = r1;
    L_0x02f5:
        r9 = "max_id";
        r7 = "currentAccount";
        if (r32 == 0) goto L_0x02fd;
    L_0x02fb:
        if (r30 == 0) goto L_0x0384;
    L_0x02fd:
        if (r12 == 0) goto L_0x0384;
    L_0x02ff:
        r37 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r37 != 0) goto L_0x0384;
    L_0x0303:
        r37 = r13;
        r13 = new android.content.Intent;
        r38 = r12;
        r12 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r39 = r1;
        r1 = org.telegram.messenger.WearReplyReceiver.class;
        r13.<init>(r12, r1);
        r1 = "dialog_id";
        r13.putExtra(r1, r14);
        r13.putExtra(r9, r8);
        r1 = r0.currentAccount;
        r13.putExtra(r7, r1);
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r12 = r2.intValue();
        r40 = r2;
        r2 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r1 = android.app.PendingIntent.getBroadcast(r1, r12, r13, r2);
        r2 = new androidx.core.app.RemoteInput$Builder;
        r12 = "extra_voice_reply";
        r2.<init>(r12);
        r12 = NUM; // 0x7f0d08b2 float:1.874663E38 double:1.0531308773E-314;
        r13 = "Reply";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        r2.setLabel(r12);
        r2 = r2.build();
        if (r11 >= 0) goto L_0x0358;
    L_0x0346:
        r13 = 1;
        r12 = new java.lang.Object[r13];
        r13 = 0;
        r12[r13] = r4;
        r13 = "ReplyToGroup";
        r41 = r8;
        r8 = NUM; // 0x7f0d08b3 float:1.8746632E38 double:1.053130878E-314;
        r8 = org.telegram.messenger.LocaleController.formatString(r13, r8, r12);
        goto L_0x0369;
    L_0x0358:
        r41 = r8;
        r8 = NUM; // 0x7f0d08b4 float:1.8746634E38 double:1.0531308783E-314;
        r12 = 1;
        r13 = new java.lang.Object[r12];
        r12 = 0;
        r13[r12] = r4;
        r12 = "ReplyToUser";
        r8 = org.telegram.messenger.LocaleController.formatString(r12, r8, r13);
    L_0x0369:
        r12 = new androidx.core.app.NotificationCompat$Action$Builder;
        r13 = NUM; // 0x7var_e float:1.794519E38 double:1.052935652E-314;
        r12.<init>(r13, r8, r1);
        r1 = 1;
        r12.setAllowGeneratedReplies(r1);
        r12.setSemanticAction(r1);
        r12.addRemoteInput(r2);
        r1 = 0;
        r12.setShowsUserInterface(r1);
        r8 = r12.build();
        goto L_0x0390;
    L_0x0384:
        r39 = r1;
        r40 = r2;
        r41 = r8;
        r38 = r12;
        r37 = r13;
        r1 = 0;
        r8 = 0;
    L_0x0390:
        r2 = r0.pushDialogs;
        r2 = r2.get(r14);
        r2 = (java.lang.Integer) r2;
        if (r2 != 0) goto L_0x039e;
    L_0x039a:
        r2 = java.lang.Integer.valueOf(r1);
    L_0x039e:
        r1 = r2.intValue();
        r2 = r10.size();
        r1 = java.lang.Math.max(r1, r2);
        r2 = 2;
        r12 = 1;
        if (r1 <= r12) goto L_0x03c8;
    L_0x03ae:
        r13 = android.os.Build.VERSION.SDK_INT;
        r12 = 28;
        if (r13 < r12) goto L_0x03b5;
    L_0x03b4:
        goto L_0x03c8;
    L_0x03b5:
        r12 = new java.lang.Object[r2];
        r13 = 0;
        r12[r13] = r4;
        r1 = java.lang.Integer.valueOf(r1);
        r13 = 1;
        r12[r13] = r1;
        r1 = "%1$s (%2$d)";
        r1 = java.lang.String.format(r1, r12);
        goto L_0x03c9;
    L_0x03c8:
        r1 = r4;
    L_0x03c9:
        r12 = new androidx.core.app.NotificationCompat$MessagingStyle;
        r13 = "";
        r12.<init>(r13);
        r2 = android.os.Build.VERSION.SDK_INT;
        r42 = r4;
        r4 = 28;
        if (r2 < r4) goto L_0x03dc;
    L_0x03d8:
        if (r11 >= 0) goto L_0x03df;
    L_0x03da:
        if (r32 != 0) goto L_0x03df;
    L_0x03dc:
        r12.setConversationTitle(r1);
    L_0x03df:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r4) goto L_0x03ea;
    L_0x03e3:
        if (r32 != 0) goto L_0x03e8;
    L_0x03e5:
        if (r11 >= 0) goto L_0x03e8;
    L_0x03e7:
        goto L_0x03ea;
    L_0x03e8:
        r1 = 0;
        goto L_0x03eb;
    L_0x03ea:
        r1 = 1;
    L_0x03eb:
        r12.setGroupConversation(r1);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = 1;
        r4 = new java.lang.String[r2];
        r43 = r9;
        r9 = new boolean[r2];
        if (r6 == 0) goto L_0x0406;
    L_0x03fc:
        r16 = new org.json.JSONArray;
        r16.<init>();
        r44 = r6;
        r6 = r16;
        goto L_0x0409;
    L_0x0406:
        r44 = r6;
        r6 = 0;
    L_0x0409:
        r16 = r10.size();
        r45 = r16 + -1;
        r2 = r45;
        r46 = 0;
        r47 = 0;
    L_0x0415:
        r48 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r2 < 0) goto L_0x0701;
    L_0x0419:
        r45 = r10.get(r2);
        r50 = r10;
        r10 = r45;
        r10 = (org.telegram.messenger.MessageObject) r10;
        r45 = r8;
        r8 = r0.getShortStringForMessage(r10, r4, r9);
        if (r8 != 0) goto L_0x0471;
    L_0x042b:
        r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r8 == 0) goto L_0x045f;
    L_0x042f:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r51 = r7;
        r7 = "message text is null for ";
        r8.append(r7);
        r7 = r10.getId();
        r8.append(r7);
        r7 = " did = ";
        r8.append(r7);
        r52 = r2;
        r7 = r3;
        r2 = r10.getDialogId();
        r8.append(r2);
        r2 = r8.toString();
        org.telegram.messenger.FileLog.w(r2);
        r56 = r1;
        r55 = r4;
        r53 = r7;
        goto L_0x0469;
    L_0x045f:
        r52 = r2;
        r51 = r7;
        r56 = r1;
        r53 = r3;
        r55 = r4;
    L_0x0469:
        r54 = r9;
        r57 = r14;
        r2 = 28;
        goto L_0x06ec;
    L_0x0471:
        r52 = r2;
        r51 = r7;
        r7 = r3;
        r2 = r1.length();
        if (r2 <= 0) goto L_0x0481;
    L_0x047c:
        r2 = "\n\n";
        r1.append(r2);
    L_0x0481:
        r2 = 0;
        r3 = r4[r2];
        if (r3 == 0) goto L_0x049c;
    L_0x0486:
        r53 = r7;
        r3 = 2;
        r7 = new java.lang.Object[r3];
        r3 = r4[r2];
        r7[r2] = r3;
        r2 = 1;
        r7[r2] = r8;
        r2 = "%1$s: %2$s";
        r2 = java.lang.String.format(r2, r7);
        r1.append(r2);
        goto L_0x04a1;
    L_0x049c:
        r53 = r7;
        r1.append(r8);
    L_0x04a1:
        if (r11 <= 0) goto L_0x04a5;
    L_0x04a3:
        r2 = (long) r11;
        goto L_0x04b2;
    L_0x04a5:
        if (r32 == 0) goto L_0x04aa;
    L_0x04a7:
        r2 = -r11;
    L_0x04a8:
        r2 = (long) r2;
        goto L_0x04b2;
    L_0x04aa:
        if (r11 >= 0) goto L_0x04b1;
    L_0x04ac:
        r2 = r10.getFromId();
        goto L_0x04a8;
    L_0x04b1:
        r2 = r14;
    L_0x04b2:
        r7 = r5.get(r2);
        r7 = (androidx.core.app.Person) r7;
        if (r7 != 0) goto L_0x054a;
    L_0x04ba:
        r7 = new androidx.core.app.Person$Builder;
        r7.<init>();
        r22 = 0;
        r54 = r4[r22];
        if (r54 != 0) goto L_0x04c9;
    L_0x04c5:
        r55 = r4;
        r4 = r13;
        goto L_0x04cf;
    L_0x04c9:
        r54 = r4[r22];
        r55 = r4;
        r4 = r54;
    L_0x04cf:
        r7.setName(r4);
        r4 = r9[r22];
        if (r4 == 0) goto L_0x053c;
    L_0x04d6:
        if (r11 == 0) goto L_0x053c;
    L_0x04d8:
        r4 = android.os.Build.VERSION.SDK_INT;
        r54 = r9;
        r9 = 28;
        if (r4 < r9) goto L_0x0539;
    L_0x04e0:
        if (r11 > 0) goto L_0x052f;
    L_0x04e2:
        if (r32 == 0) goto L_0x04e5;
    L_0x04e4:
        goto L_0x052f;
    L_0x04e5:
        if (r11 >= 0) goto L_0x0529;
    L_0x04e7:
        r4 = r10.getFromId();
        r9 = r60.getMessagesController();
        r56 = r1;
        r1 = java.lang.Integer.valueOf(r4);
        r1 = r9.getUser(r1);
        if (r1 != 0) goto L_0x050d;
    L_0x04fb:
        r1 = r60.getMessagesStorage();
        r1 = r1.getUserSync(r4);
        if (r1 == 0) goto L_0x050d;
    L_0x0505:
        r4 = r60.getMessagesController();
        r9 = 1;
        r4.putUser(r1, r9);
    L_0x050d:
        if (r1 == 0) goto L_0x052b;
    L_0x050f:
        r1 = r1.photo;
        if (r1 == 0) goto L_0x052b;
    L_0x0513:
        r1 = r1.photo_small;
        if (r1 == 0) goto L_0x052b;
    L_0x0517:
        r57 = r14;
        r14 = r1.volume_id;
        r4 = (r14 > r24 ? 1 : (r14 == r24 ? 0 : -1));
        if (r4 == 0) goto L_0x052d;
    L_0x051f:
        r4 = r1.local_id;
        if (r4 == 0) goto L_0x052d;
    L_0x0523:
        r4 = 1;
        r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r4);
        goto L_0x0535;
    L_0x0529:
        r56 = r1;
    L_0x052b:
        r57 = r14;
    L_0x052d:
        r1 = 0;
        goto L_0x0535;
    L_0x052f:
        r56 = r1;
        r57 = r14;
        r1 = r34;
    L_0x0535:
        r0.loadRoundAvatar(r1, r7);
        goto L_0x0542;
    L_0x0539:
        r56 = r1;
        goto L_0x0540;
    L_0x053c:
        r56 = r1;
        r54 = r9;
    L_0x0540:
        r57 = r14;
    L_0x0542:
        r7 = r7.build();
        r5.put(r2, r7);
        goto L_0x0552;
    L_0x054a:
        r56 = r1;
        r55 = r4;
        r54 = r9;
        r57 = r14;
    L_0x0552:
        if (r11 == 0) goto L_0x0687;
    L_0x0554:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 28;
        if (r1 < r2) goto L_0x063e;
    L_0x055a:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = "activity";
        r1 = r1.getSystemService(r3);
        r1 = (android.app.ActivityManager) r1;
        r1 = r1.isLowRamDevice();
        if (r1 != 0) goto L_0x063e;
    L_0x056a:
        r1 = r10.isSecretMedia();
        if (r1 != 0) goto L_0x0633;
    L_0x0570:
        r1 = r10.type;
        r3 = 1;
        if (r1 == r3) goto L_0x057b;
    L_0x0575:
        r1 = r10.isSticker();
        if (r1 == 0) goto L_0x0633;
    L_0x057b:
        r1 = r10.messageOwner;
        r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1);
        r3 = new androidx.core.app.NotificationCompat$MessagingStyle$Message;
        r4 = r10.messageOwner;
        r4 = r4.date;
        r14 = (long) r4;
        r14 = r14 * r48;
        r3.<init>(r8, r14, r7);
        r4 = r10.isSticker();
        if (r4 == 0) goto L_0x0596;
    L_0x0593:
        r4 = "image/webp";
        goto L_0x0598;
    L_0x0596:
        r4 = "image/jpeg";
    L_0x0598:
        r9 = r1.exists();
        if (r9 == 0) goto L_0x05a7;
    L_0x059e:
        r9 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r14 = "org.telegram.messenger.provider";
        r1 = androidx.core.content.FileProvider.getUriForFile(r9, r14, r1);
        goto L_0x05f9;
    L_0x05a7:
        r9 = r60.getFileLoader();
        r14 = r1.getName();
        r9 = r9.isLoadingFile(r14);
        if (r9 == 0) goto L_0x05f8;
    L_0x05b5:
        r9 = new android.net.Uri$Builder;
        r9.<init>();
        r14 = "content";
        r9 = r9.scheme(r14);
        r14 = "org.telegram.messenger.notification_image_provider";
        r9 = r9.authority(r14);
        r14 = "msg_media_raw";
        r9 = r9.appendPath(r14);
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = r0.currentAccount;
        r14.append(r15);
        r14.append(r13);
        r14 = r14.toString();
        r9 = r9.appendPath(r14);
        r14 = r1.getName();
        r9 = r9.appendPath(r14);
        r1 = r1.getAbsolutePath();
        r14 = "final_path";
        r1 = r9.appendQueryParameter(r14, r1);
        r1 = r1.build();
        goto L_0x05f9;
    L_0x05f8:
        r1 = 0;
    L_0x05f9:
        if (r1 == 0) goto L_0x0628;
    L_0x05fb:
        r3.setData(r4, r1);
        r12.addMessage(r3);
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = "com.android.systemui";
        r9 = 1;
        r3.grantUriPermission(r4, r1, r9);
        r3 = new org.telegram.messenger.-$$Lambda$NotificationsController$hROO1aIM4eduzMv5uJ3U4yL97Bo;
        r3.<init>(r1);
        r14 = 20000; // 0x4e20 float:2.8026E-41 double:9.8813E-320;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r14);
        r1 = r10.caption;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0648;
    L_0x061b:
        r1 = r10.caption;
        r3 = r10.messageOwner;
        r3 = r3.date;
        r3 = (long) r3;
        r3 = r3 * r48;
        r12.addMessage(r1, r3, r7);
        goto L_0x0648;
    L_0x0628:
        r1 = r10.messageOwner;
        r1 = r1.date;
        r3 = (long) r1;
        r3 = r3 * r48;
        r12.addMessage(r8, r3, r7);
        goto L_0x0648;
    L_0x0633:
        r1 = r10.messageOwner;
        r1 = r1.date;
        r3 = (long) r1;
        r3 = r3 * r48;
        r12.addMessage(r8, r3, r7);
        goto L_0x0648;
    L_0x063e:
        r1 = r10.messageOwner;
        r1 = r1.date;
        r3 = (long) r1;
        r3 = r3 * r48;
        r12.addMessage(r8, r3, r7);
    L_0x0648:
        r1 = r10.isVoice();
        if (r1 == 0) goto L_0x0693;
    L_0x064e:
        r1 = r12.getMessages();
        r3 = r1.isEmpty();
        if (r3 != 0) goto L_0x0693;
    L_0x0658:
        r3 = r10.messageOwner;
        r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3);
        r4 = android.os.Build.VERSION.SDK_INT;
        r7 = 24;
        if (r4 < r7) goto L_0x066f;
    L_0x0664:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x066d }
        r7 = "org.telegram.messenger.provider";
        r3 = androidx.core.content.FileProvider.getUriForFile(r4, r7, r3);	 Catch:{ Exception -> 0x066d }
        goto L_0x0673;
    L_0x066d:
        r3 = 0;
        goto L_0x0673;
    L_0x066f:
        r3 = android.net.Uri.fromFile(r3);
    L_0x0673:
        if (r3 == 0) goto L_0x0693;
    L_0x0675:
        r4 = r1.size();
        r7 = 1;
        r4 = r4 - r7;
        r1 = r1.get(r4);
        r1 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r1;
        r4 = "audio/ogg";
        r1.setData(r4, r3);
        goto L_0x0693;
    L_0x0687:
        r2 = 28;
        r1 = r10.messageOwner;
        r1 = r1.date;
        r3 = (long) r1;
        r3 = r3 * r48;
        r12.addMessage(r8, r3, r7);
    L_0x0693:
        if (r6 == 0) goto L_0x06d5;
    L_0x0695:
        r1 = new org.json.JSONObject;	 Catch:{ JSONException -> 0x06d4 }
        r1.<init>();	 Catch:{ JSONException -> 0x06d4 }
        r3 = "text";
        r1.put(r3, r8);	 Catch:{ JSONException -> 0x06d4 }
        r3 = "date";
        r4 = r10.messageOwner;	 Catch:{ JSONException -> 0x06d4 }
        r4 = r4.date;	 Catch:{ JSONException -> 0x06d4 }
        r1.put(r3, r4);	 Catch:{ JSONException -> 0x06d4 }
        r3 = r10.isFromUser();	 Catch:{ JSONException -> 0x06d4 }
        if (r3 == 0) goto L_0x06d0;
    L_0x06ae:
        if (r11 >= 0) goto L_0x06d0;
    L_0x06b0:
        r3 = r60.getMessagesController();	 Catch:{ JSONException -> 0x06d4 }
        r4 = r10.getFromId();	 Catch:{ JSONException -> 0x06d4 }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ JSONException -> 0x06d4 }
        r3 = r3.getUser(r4);	 Catch:{ JSONException -> 0x06d4 }
        if (r3 == 0) goto L_0x06d0;
    L_0x06c2:
        r4 = "fname";
        r7 = r3.first_name;	 Catch:{ JSONException -> 0x06d4 }
        r1.put(r4, r7);	 Catch:{ JSONException -> 0x06d4 }
        r4 = "lname";
        r3 = r3.last_name;	 Catch:{ JSONException -> 0x06d4 }
        r1.put(r4, r3);	 Catch:{ JSONException -> 0x06d4 }
    L_0x06d0:
        r6.put(r1);	 Catch:{ JSONException -> 0x06d4 }
        goto L_0x06d5;
    L_0x06d5:
        r3 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r1 = (r57 > r3 ? 1 : (r57 == r3 ? 0 : -1));
        if (r1 != 0) goto L_0x06ec;
    L_0x06dc:
        r1 = r10.messageOwner;
        r1 = r1.reply_markup;
        if (r1 == 0) goto L_0x06ec;
    L_0x06e2:
        r1 = r1.rows;
        r3 = r10.getId();
        r46 = r1;
        r47 = r3;
    L_0x06ec:
        r1 = r52 + -1;
        r2 = r1;
        r8 = r45;
        r10 = r50;
        r7 = r51;
        r3 = r53;
        r9 = r54;
        r4 = r55;
        r1 = r56;
        r14 = r57;
        goto L_0x0415;
    L_0x0701:
        r56 = r1;
        r53 = r3;
        r51 = r7;
        r45 = r8;
        r50 = r10;
        r57 = r14;
        r1 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = org.telegram.ui.LaunchActivity.class;
        r1.<init>(r2, r3);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "com.tmessages.openchat";
        r2.append(r3);
        r3 = java.lang.Math.random();
        r2.append(r3);
        r3 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r2.append(r3);
        r2 = r2.toString();
        r1.setAction(r2);
        r2 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r1.setFlags(r2);
        r2 = "android.intent.category.LAUNCHER";
        r1.addCategory(r2);
        if (r11 == 0) goto L_0x0753;
    L_0x0741:
        if (r11 <= 0) goto L_0x074a;
    L_0x0743:
        r2 = "userId";
        r1.putExtra(r2, r11);
        goto L_0x0750;
    L_0x074a:
        r2 = -r11;
        r3 = "chatId";
        r1.putExtra(r3, r2);
    L_0x0750:
        r3 = r53;
        goto L_0x075a;
    L_0x0753:
        r2 = "encId";
        r3 = r53;
        r1.putExtra(r2, r3);
    L_0x075a:
        r2 = r0.currentAccount;
        r4 = r51;
        r1.putExtra(r4, r2);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r7 = 0;
        r1 = android.app.PendingIntent.getActivity(r2, r7, r1, r5);
        r2 = new androidx.core.app.NotificationCompat$WearableExtender;
        r2.<init>();
        if (r45 == 0) goto L_0x0777;
    L_0x0771:
        r8 = r45;
        r2.addAction(r8);
        goto L_0x0779;
    L_0x0777:
        r8 = r45;
    L_0x0779:
        r5 = new android.content.Intent;
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r9 = org.telegram.messenger.AutoMessageHeardReceiver.class;
        r5.<init>(r7, r9);
        r7 = 32;
        r5.addFlags(r7);
        r7 = "org.telegram.messenger.ACTION_MESSAGE_HEARD";
        r5.setAction(r7);
        r7 = "dialog_id";
        r9 = r57;
        r5.putExtra(r7, r9);
        r7 = r41;
        r14 = r43;
        r5.putExtra(r14, r7);
        r15 = r0.currentAccount;
        r5.putExtra(r4, r15);
        r15 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r24 = r6;
        r6 = r40.intValue();
        r14 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r5 = android.app.PendingIntent.getBroadcast(r15, r6, r5, r14);
        r6 = new androidx.core.app.NotificationCompat$Action$Builder;
        r14 = NUM; // 0x7var_aa float:1.7945442E38 double:1.0529357135E-314;
        r15 = NUM; // 0x7f0d05a3 float:1.8745041E38 double:1.0531304905E-314;
        r51 = r4;
        r4 = "MarkAsRead";
        r4 = org.telegram.messenger.LocaleController.getString(r4, r15);
        r6.<init>(r14, r4, r5);
        r4 = 2;
        r6.setSemanticAction(r4);
        r4 = 0;
        r6.setShowsUserInterface(r4);
        r4 = r6.build();
        r5 = "_";
        if (r11 == 0) goto L_0x0803;
    L_0x07d0:
        if (r11 <= 0) goto L_0x07ea;
    L_0x07d2:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r6 = "tguser";
        r3.append(r6);
        r3.append(r11);
        r3.append(r5);
        r3.append(r7);
        r3 = r3.toString();
        goto L_0x0822;
    L_0x07ea:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r6 = "tgchat";
        r3.append(r6);
        r6 = -r11;
        r3.append(r6);
        r3.append(r5);
        r3.append(r7);
        r3 = r3.toString();
        goto L_0x0822;
    L_0x0803:
        r14 = globalSecretChatId;
        r6 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1));
        if (r6 == 0) goto L_0x0821;
    L_0x0809:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r14 = "tgenc";
        r6.append(r14);
        r6.append(r3);
        r6.append(r5);
        r6.append(r7);
        r3 = r6.toString();
        goto L_0x0822;
    L_0x0821:
        r3 = 0;
    L_0x0822:
        if (r3 == 0) goto L_0x0846;
    L_0x0824:
        r2.setDismissalId(r3);
        r6 = new androidx.core.app.NotificationCompat$WearableExtender;
        r6.<init>();
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = "summary_";
        r14.append(r15);
        r14.append(r3);
        r3 = r14.toString();
        r6.setDismissalId(r3);
        r3 = r61;
        r3.extend(r6);
        goto L_0x0848;
    L_0x0846:
        r3 = r61;
    L_0x0848:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r14 = "tgaccount";
        r6.append(r14);
        r14 = r60.getUserConfig();
        r14 = r14.getClientUserId();
        r6.append(r14);
        r6 = r6.toString();
        r2.setBridgeTag(r6);
        r6 = r50;
        r14 = 0;
        r15 = r6.get(r14);
        r15 = (org.telegram.messenger.MessageObject) r15;
        r14 = r15.messageOwner;
        r14 = r14.date;
        r14 = (long) r14;
        r14 = r14 * r48;
        r3 = new androidx.core.app.NotificationCompat$Builder;
        r25 = r5;
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3.<init>(r5);
        r5 = r42;
        r3.setContentTitle(r5);
        r41 = r7;
        r7 = NUM; // 0x7var_ float:1.7945655E38 double:1.0529357654E-314;
        r3.setSmallIcon(r7);
        r7 = r56.toString();
        r3.setContentText(r7);
        r7 = 1;
        r3.setAutoCancel(r7);
        r6 = r6.size();
        r3.setNumber(r6);
        r6 = -15618822; // 0xfffffffffvar_acfa float:-1.936362E38 double:NaN;
        r3.setColor(r6);
        r6 = 0;
        r3.setGroupSummary(r6);
        r3.setWhen(r14);
        r3.setShowWhen(r7);
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "sdid_";
        r6.append(r7);
        r6.append(r9);
        r6 = r6.toString();
        r3.setShortcutId(r6);
        r3.setStyle(r12);
        r3.setContentIntent(r1);
        r3.extend(r2);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r13);
        r6 = NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        r6 = r6 - r14;
        r1.append(r6);
        r1 = r1.toString();
        r3.setSortKey(r1);
        r1 = "msg";
        r3.setCategory(r1);
        if (r36 == 0) goto L_0x08f2;
    L_0x08e8:
        r1 = r0.notificationGroup;
        r3.setGroup(r1);
        r1 = 1;
        r3.setGroupAlertBehavior(r1);
        goto L_0x08f3;
    L_0x08f2:
        r1 = 1;
    L_0x08f3:
        if (r8 == 0) goto L_0x08f8;
    L_0x08f5:
        r3.addAction(r8);
    L_0x08f8:
        r3.addAction(r4);
        r2 = r0.pushDialogs;
        r2 = r2.size();
        if (r2 != r1) goto L_0x090f;
    L_0x0903:
        r2 = android.text.TextUtils.isEmpty(r63);
        if (r2 != 0) goto L_0x090f;
    L_0x0909:
        r2 = r63;
        r3.setSubText(r2);
        goto L_0x0911;
    L_0x090f:
        r2 = r63;
    L_0x0911:
        if (r11 != 0) goto L_0x0916;
    L_0x0913:
        r3.setLocalOnly(r1);
    L_0x0916:
        if (r39 == 0) goto L_0x091d;
    L_0x0918:
        r4 = r39;
        r3.setLargeIcon(r4);
    L_0x091d:
        r4 = 0;
        r6 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r4);
        if (r6 != 0) goto L_0x09b1;
    L_0x0924:
        r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r4 != 0) goto L_0x09b1;
    L_0x0928:
        r4 = r46;
        if (r4 == 0) goto L_0x09b1;
    L_0x092c:
        r6 = r4.size();
        r7 = 0;
    L_0x0931:
        if (r7 >= r6) goto L_0x09b1;
    L_0x0933:
        r8 = r4.get(r7);
        r8 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r8;
        r12 = r8.buttons;
        r12 = r12.size();
        r13 = 0;
    L_0x0940:
        if (r13 >= r12) goto L_0x09a2;
    L_0x0942:
        r14 = r8.buttons;
        r14 = r14.get(r13);
        r14 = (org.telegram.tgnet.TLRPC.KeyboardButton) r14;
        r15 = r14 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
        if (r15 == 0) goto L_0x0991;
    L_0x094e:
        r15 = new android.content.Intent;
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r2 = org.telegram.messenger.NotificationCallbackReceiver.class;
        r15.<init>(r1, r2);
        r1 = r0.currentAccount;
        r2 = r51;
        r15.putExtra(r2, r1);
        r1 = "did";
        r15.putExtra(r1, r9);
        r1 = r14.data;
        if (r1 == 0) goto L_0x096f;
    L_0x0967:
        r51 = r2;
        r2 = "data";
        r15.putExtra(r2, r1);
        goto L_0x0971;
    L_0x096f:
        r51 = r2;
    L_0x0971:
        r1 = "mid";
        r2 = r47;
        r15.putExtra(r1, r2);
        r1 = r14.text;
        r14 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r33 = r2;
        r2 = r0.lastButtonId;
        r34 = r4;
        r4 = r2 + 1;
        r0.lastButtonId = r4;
        r4 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r2 = android.app.PendingIntent.getBroadcast(r14, r2, r15, r4);
        r14 = 0;
        r3.addAction(r14, r1, r2);
        goto L_0x0998;
    L_0x0991:
        r34 = r4;
        r33 = r47;
        r4 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r14 = 0;
    L_0x0998:
        r13 = r13 + 1;
        r2 = r63;
        r47 = r33;
        r4 = r34;
        r1 = 1;
        goto L_0x0940;
    L_0x09a2:
        r34 = r4;
        r33 = r47;
        r4 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r14 = 0;
        r7 = r7 + 1;
        r2 = r63;
        r4 = r34;
        r1 = 1;
        goto L_0x0931;
    L_0x09b1:
        r14 = 0;
        if (r31 != 0) goto L_0x09d8;
    L_0x09b4:
        if (r35 == 0) goto L_0x09d8;
    L_0x09b6:
        r6 = r35;
        r1 = r6.phone;
        if (r1 == 0) goto L_0x09d8;
    L_0x09bc:
        r1 = r1.length();
        if (r1 <= 0) goto L_0x09d8;
    L_0x09c2:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "tel:+";
        r1.append(r2);
        r2 = r6.phone;
        r1.append(r2);
        r1 = r1.toString();
        r3.addPerson(r1);
    L_0x09d8:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 26;
        if (r1 < r2) goto L_0x09ed;
    L_0x09de:
        if (r36 == 0) goto L_0x09e6;
    L_0x09e0:
        r1 = OTHER_NOTIFICATIONS_CHANNEL;
        r3.setChannelId(r1);
        goto L_0x09ed;
    L_0x09e6:
        r1 = r27.getChannelId();
        r3.setChannelId(r1);
    L_0x09ed:
        r1 = new org.telegram.messenger.NotificationsController$1NotificationHolder;
        r4 = r40.intValue();
        r3 = r3.build();
        r1.<init>(r4, r3);
        r3 = r29;
        r3.add(r1);
        r1 = r0.wearNotificationsIds;
        r4 = r40;
        r1.put(r9, r4);
        if (r11 == 0) goto L_0x0a84;
    L_0x0a08:
        if (r44 == 0) goto L_0x0a84;
    L_0x0a0a:
        r1 = "reply";
        r4 = r38;
        r6 = r44;
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a84 }
        r1 = "name";
        r6.put(r1, r5);	 Catch:{ JSONException -> 0x0a84 }
        r1 = r41;
        r4 = r43;
        r6.put(r4, r1);	 Catch:{ JSONException -> 0x0a84 }
        r1 = "max_date";
        r4 = r18;
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a84 }
        r1 = "id";
        r4 = java.lang.Math.abs(r11);	 Catch:{ JSONException -> 0x0a84 }
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a84 }
        if (r37 == 0) goto L_0x0a58;
    L_0x0a31:
        r1 = "photo";
        r4 = new java.lang.StringBuilder;	 Catch:{ JSONException -> 0x0a84 }
        r4.<init>();	 Catch:{ JSONException -> 0x0a84 }
        r13 = r37;
        r5 = r13.dc_id;	 Catch:{ JSONException -> 0x0a84 }
        r4.append(r5);	 Catch:{ JSONException -> 0x0a84 }
        r5 = r25;
        r4.append(r5);	 Catch:{ JSONException -> 0x0a84 }
        r7 = r13.volume_id;	 Catch:{ JSONException -> 0x0a84 }
        r4.append(r7);	 Catch:{ JSONException -> 0x0a84 }
        r4.append(r5);	 Catch:{ JSONException -> 0x0a84 }
        r7 = r13.secret;	 Catch:{ JSONException -> 0x0a84 }
        r4.append(r7);	 Catch:{ JSONException -> 0x0a84 }
        r4 = r4.toString();	 Catch:{ JSONException -> 0x0a84 }
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a84 }
    L_0x0a58:
        if (r24 == 0) goto L_0x0a61;
    L_0x0a5a:
        r1 = "msgs";
        r4 = r24;
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a84 }
    L_0x0a61:
        r1 = "type";
        if (r11 <= 0) goto L_0x0a6c;
    L_0x0a65:
        r4 = "user";
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a84 }
        goto L_0x0a7e;
    L_0x0a6c:
        if (r11 >= 0) goto L_0x0a7e;
    L_0x0a6e:
        if (r32 != 0) goto L_0x0a79;
    L_0x0a70:
        if (r30 == 0) goto L_0x0a73;
    L_0x0a72:
        goto L_0x0a79;
    L_0x0a73:
        r4 = "group";
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a84 }
        goto L_0x0a7e;
    L_0x0a79:
        r4 = "channel";
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a84 }
    L_0x0a7e:
        r7 = r28;
        r7.put(r6);	 Catch:{ JSONException -> 0x0a86 }
        goto L_0x0a86;
    L_0x0a84:
        r7 = r28;
    L_0x0a86:
        r13 = r26 + 1;
        r6 = r3;
        r2 = r19;
        r3 = r20;
        r12 = r21;
        r5 = r23;
        r1 = r27;
        r9 = r36;
        r4 = 0;
        r10 = 26;
        r11 = 1;
        goto L_0x009a;
    L_0x0a9b:
        r27 = r1;
        r23 = r5;
        r3 = r6;
        r36 = r9;
        r14 = 0;
        if (r36 == 0) goto L_0x0ac9;
    L_0x0aa5:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0abf;
    L_0x0aa9:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "show summary with id ";
        r1.append(r2);
        r2 = r0.notificationId;
        r1.append(r2);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0abf:
        r1 = notificationManager;
        r2 = r0.notificationId;
        r4 = r27;
        r1.notify(r2, r4);
        goto L_0x0ad0;
    L_0x0ac9:
        r1 = notificationManager;
        r2 = r0.notificationId;
        r1.cancel(r2);
    L_0x0ad0:
        r1 = r3.size();
        r2 = 0;
    L_0x0ad5:
        if (r2 >= r1) goto L_0x0ae3;
    L_0x0ad7:
        r4 = r3.get(r2);
        r4 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r4;
        r4.call();
        r2 = r2 + 1;
        goto L_0x0ad5;
    L_0x0ae3:
        r1 = r23.size();
        if (r14 >= r1) goto L_0x0b17;
    L_0x0ae9:
        r1 = r23;
        r2 = r1.valueAt(r14);
        r2 = (java.lang.Integer) r2;
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x0b09;
    L_0x0af5:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "cancel notification id ";
        r3.append(r4);
        r3.append(r2);
        r3 = r3.toString();
        org.telegram.messenger.FileLog.w(r3);
    L_0x0b09:
        r3 = notificationManager;
        r2 = r2.intValue();
        r3.cancel(r2);
        r14 = r14 + 1;
        r23 = r1;
        goto L_0x0ae3;
    L_0x0b17:
        if (r7 == 0) goto L_0x0b3f;
    L_0x0b19:
        r1 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0b3f }
        r1.<init>();	 Catch:{ Exception -> 0x0b3f }
        r2 = "id";
        r3 = r60.getUserConfig();	 Catch:{ Exception -> 0x0b3f }
        r3 = r3.getClientUserId();	 Catch:{ Exception -> 0x0b3f }
        r1.put(r2, r3);	 Catch:{ Exception -> 0x0b3f }
        r2 = "n";
        r1.put(r2, r7);	 Catch:{ Exception -> 0x0b3f }
        r2 = "/notify";
        r1 = r1.toString();	 Catch:{ Exception -> 0x0b3f }
        r1 = r1.getBytes();	 Catch:{ Exception -> 0x0b3f }
        r3 = "remote_notifications";
        org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r2, r1, r3);	 Catch:{ Exception -> 0x0b3f }
    L_0x0b3f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, boolean, java.lang.String):void");
    }

    @TargetApi(28)
    private void loadRoundAvatar(File file, Person.Builder builder) {
        if (file != null) {
            try {
                builder.setIcon(IconCompat.createWithBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(file), -$$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8.INSTANCE)));
            } catch (Throwable unused) {
            }
        }
    }

    static /* synthetic */ int lambda$null$32(Canvas canvas) {
        Path path = new Path();
        path.setFillType(FillType.INVERSE_EVEN_ODD);
        int width = canvas.getWidth();
        float f = (float) (width / 2);
        path.addRoundRect(0.0f, 0.0f, (float) width, (float) canvas.getHeight(), f, f, Direction.CW);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
        canvas.drawPath(path, paint);
        return -3;
    }

    public void playOutChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$9BWFjQml5zrAo3EV8FWEAyCpJLQ(this));
        }
    }

    public /* synthetic */ void lambda$playOutChatSound$35$NotificationsController() {
        try {
            if (Math.abs(System.currentTimeMillis() - this.lastSoundOutPlay) > 100) {
                this.lastSoundOutPlay = System.currentTimeMillis();
                if (this.soundPool == null) {
                    this.soundPool = new SoundPool(3, 1, 0);
                    this.soundPool.setOnLoadCompleteListener(-$$Lambda$NotificationsController$wVHQwnWTTlh7lF1NZGGoEEMMuyY.INSTANCE);
                }
                if (this.soundOut == 0 && !this.soundOutLoaded) {
                    this.soundOutLoaded = true;
                    this.soundOut = this.soundPool.load(ApplicationLoader.applicationContext, NUM, 1);
                }
                if (this.soundOut != 0) {
                    try {
                        this.soundPool.play(this.soundOut, 1.0f, 1.0f, 1, 0, 1.0f);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    static /* synthetic */ void lambda$null$34(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void setDialogNotificationsSettings(long j, int i) {
        Editor edit = getAccountInstance().getNotificationsSettings().edit();
        Dialog dialog = (Dialog) MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(j);
        String str = "notify2_";
        StringBuilder stringBuilder;
        if (i == 4) {
            if (isGlobalNotificationsEnabled(j)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(j);
                edit.remove(stringBuilder.toString());
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(j);
                edit.putInt(stringBuilder.toString(), 0);
            }
            getMessagesStorage().setDialogFlags(j, 0);
            if (dialog != null) {
                dialog.notify_settings = new TL_peerNotifySettings();
            }
        } else {
            int currentTime = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
            if (i == 0) {
                currentTime += 3600;
            } else if (i == 1) {
                currentTime += 28800;
            } else if (i == 2) {
                currentTime += 172800;
            } else if (i == 3) {
                currentTime = Integer.MAX_VALUE;
            }
            long j2 = 1;
            if (i == 3) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(j);
                edit.putInt(stringBuilder.toString(), 2);
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(j);
                edit.putInt(stringBuilder.toString(), 3);
                stringBuilder = new StringBuilder();
                stringBuilder.append("notifyuntil_");
                stringBuilder.append(j);
                edit.putInt(stringBuilder.toString(), currentTime);
                j2 = 1 | (((long) currentTime) << 32);
            }
            getInstance(UserConfig.selectedAccount).removeNotificationsForDialog(j);
            MessagesStorage.getInstance(UserConfig.selectedAccount).setDialogFlags(j, j2);
            if (dialog != null) {
                dialog.notify_settings = new TL_peerNotifySettings();
                dialog.notify_settings.mute_until = currentTime;
            }
        }
        edit.commit();
        updateServerNotificationsSettings(j);
    }

    public void updateServerNotificationsSettings(long j) {
        updateServerNotificationsSettings(j, true);
    }

    public void updateServerNotificationsSettings(long j, boolean z) {
        int i = 0;
        if (z) {
            getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
        int i2 = (int) j;
        if (i2 != 0) {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            TL_account_updateNotifySettings tL_account_updateNotifySettings = new TL_account_updateNotifySettings();
            tL_account_updateNotifySettings.settings = new TL_inputPeerNotifySettings();
            TL_inputPeerNotifySettings tL_inputPeerNotifySettings = tL_account_updateNotifySettings.settings;
            tL_inputPeerNotifySettings.flags |= 1;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("content_preview_");
            stringBuilder.append(j);
            tL_inputPeerNotifySettings.show_previews = notificationsSettings.getBoolean(stringBuilder.toString(), true);
            tL_inputPeerNotifySettings = tL_account_updateNotifySettings.settings;
            tL_inputPeerNotifySettings.flags |= 2;
            stringBuilder = new StringBuilder();
            stringBuilder.append("silent_");
            stringBuilder.append(j);
            tL_inputPeerNotifySettings.silent = notificationsSettings.getBoolean(stringBuilder.toString(), false);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("notify2_");
            stringBuilder2.append(j);
            int i3 = notificationsSettings.getInt(stringBuilder2.toString(), -1);
            if (i3 != -1) {
                TL_inputPeerNotifySettings tL_inputPeerNotifySettings2 = tL_account_updateNotifySettings.settings;
                tL_inputPeerNotifySettings2.flags |= 4;
                if (i3 == 3) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("notifyuntil_");
                    stringBuilder2.append(j);
                    tL_inputPeerNotifySettings2.mute_until = notificationsSettings.getInt(stringBuilder2.toString(), 0);
                } else {
                    if (i3 == 2) {
                        i = Integer.MAX_VALUE;
                    }
                    tL_inputPeerNotifySettings2.mute_until = i;
                }
            }
            tL_account_updateNotifySettings.peer = new TL_inputNotifyPeer();
            ((TL_inputNotifyPeer) tL_account_updateNotifySettings.peer).peer = getMessagesController().getInputPeer(i2);
            getConnectionsManager().sendRequest(tL_account_updateNotifySettings, -$$Lambda$NotificationsController$KyQqllEdy_fdmMCr6frsin2S3Cs.INSTANCE);
        }
    }

    public void updateServerNotificationsSettings(int i) {
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        TL_account_updateNotifySettings tL_account_updateNotifySettings = new TL_account_updateNotifySettings();
        tL_account_updateNotifySettings.settings = new TL_inputPeerNotifySettings();
        tL_account_updateNotifySettings.settings.flags = 5;
        if (i == 0) {
            tL_account_updateNotifySettings.peer = new TL_inputNotifyChats();
            tL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableGroup2", 0);
            tL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewGroup", true);
        } else if (i == 1) {
            tL_account_updateNotifySettings.peer = new TL_inputNotifyUsers();
            tL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableAll2", 0);
            tL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewAll", true);
        } else {
            tL_account_updateNotifySettings.peer = new TL_inputNotifyBroadcasts();
            tL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableChannel2", 0);
            tL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewChannel", true);
        }
        getConnectionsManager().sendRequest(tL_account_updateNotifySettings, -$$Lambda$NotificationsController$WV8JpQrNXdfWVJfPV9wKTUTuLBk.INSTANCE);
    }

    public boolean isGlobalNotificationsEnabled(long j) {
        int i;
        int i2 = (int) j;
        if (i2 < 0) {
            Chat chat = getMessagesController().getChat(Integer.valueOf(-i2));
            i = (!ChatObject.isChannel(chat) || chat.megagroup) ? 0 : 2;
        } else {
            i = 1;
        }
        return isGlobalNotificationsEnabled(i);
    }

    public boolean isGlobalNotificationsEnabled(int i) {
        return getAccountInstance().getNotificationsSettings().getInt(getGlobalNotificationsKey(i), 0) < getConnectionsManager().getCurrentTime();
    }

    public void setGlobalNotificationsEnabled(int i, int i2) {
        getAccountInstance().getNotificationsSettings().edit().putInt(getGlobalNotificationsKey(i), i2).commit();
        updateServerNotificationsSettings(i);
    }
}
