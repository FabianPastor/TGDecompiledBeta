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
        boolean z;
        SparseArray sparseArray2 = sparseArray;
        ArrayList arrayList2 = arrayList;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        Integer valueOf = Integer.valueOf(0);
        int i2 = 0;
        while (true) {
            z = true;
            if (i2 >= sparseArray.size()) {
                break;
            }
            ArrayList arrayList3 = (ArrayList) sparseArray2.get(sparseArray2.keyAt(i2));
            for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                long intValue = (long) ((Integer) arrayList3.get(i3)).intValue();
                MessageObject messageObject = (MessageObject) this.pushMessagesDict.get(intValue);
                if (messageObject != null) {
                    Integer num;
                    long dialogId = messageObject.getDialogId();
                    Integer num2 = (Integer) this.pushDialogs.get(dialogId);
                    if (num2 == null) {
                        num2 = valueOf;
                    }
                    Integer valueOf2 = Integer.valueOf(num2.intValue() - 1);
                    if (valueOf2.intValue() <= 0) {
                        this.smartNotificationsDialogs.remove(dialogId);
                        num = valueOf;
                    } else {
                        num = valueOf2;
                    }
                    if (!num.equals(num2)) {
                        this.total_unread_count -= num2.intValue();
                        this.total_unread_count += num.intValue();
                        this.pushDialogs.put(dialogId, num);
                    }
                    if (num.intValue() == 0) {
                        this.pushDialogs.remove(dialogId);
                        this.pushDialogsOverrideMention.remove(dialogId);
                    }
                    this.pushMessagesDict.remove(intValue);
                    this.delayedPushMessages.remove(messageObject);
                    this.pushMessages.remove(messageObject);
                    if (isPersonalMessage(messageObject)) {
                        this.personal_count--;
                    }
                    arrayList2.add(messageObject);
                }
            }
            i2++;
        }
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

    /* JADX WARNING: Missing block: B:43:0x00bb, code skipped:
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
        if (r1 == 0) goto L_0x007a;
    L_0x000d:
        r8 = 0;
    L_0x000e:
        r9 = r19.size();
        if (r8 >= r9) goto L_0x007a;
    L_0x0014:
        r9 = r1.keyAt(r8);
        r10 = r1.get(r9);
        r12 = 0;
    L_0x001d:
        r13 = r0.pushMessages;
        r13 = r13.size();
        if (r12 >= r13) goto L_0x0077;
    L_0x0025:
        r13 = r0.pushMessages;
        r13 = r13.get(r12);
        r13 = (org.telegram.messenger.MessageObject) r13;
        r14 = r13.messageOwner;
        r14 = r14.from_scheduled;
        if (r14 != 0) goto L_0x0075;
    L_0x0033:
        r14 = r13.getDialogId();
        r5 = (long) r9;
        r17 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1));
        if (r17 != 0) goto L_0x0075;
    L_0x003c:
        r5 = r13.getId();
        r6 = (int) r10;
        if (r5 > r6) goto L_0x0075;
    L_0x0043:
        r5 = r0.isPersonalMessage(r13);
        if (r5 == 0) goto L_0x004e;
    L_0x0049:
        r5 = r0.personal_count;
        r5 = r5 - r7;
        r0.personal_count = r5;
    L_0x004e:
        r2.add(r13);
        r5 = r13.getId();
        r5 = (long) r5;
        r14 = r13.messageOwner;
        r14 = r14.to_id;
        r14 = r14.channel_id;
        if (r14 == 0) goto L_0x0064;
    L_0x005e:
        r14 = (long) r14;
        r16 = 32;
        r14 = r14 << r16;
        r5 = r5 | r14;
    L_0x0064:
        r14 = r0.pushMessagesDict;
        r14.remove(r5);
        r5 = r0.delayedPushMessages;
        r5.remove(r13);
        r5 = r0.pushMessages;
        r5.remove(r12);
        r12 = r12 + -1;
    L_0x0075:
        r12 = r12 + r7;
        goto L_0x001d;
    L_0x0077:
        r8 = r8 + 1;
        goto L_0x000e;
    L_0x007a:
        r5 = 0;
        r1 = (r21 > r5 ? 1 : (r21 == r5 ? 0 : -1));
        if (r1 == 0) goto L_0x00f7;
    L_0x0080:
        if (r3 != 0) goto L_0x0084;
    L_0x0082:
        if (r4 == 0) goto L_0x00f7;
    L_0x0084:
        r1 = 0;
    L_0x0085:
        r5 = r0.pushMessages;
        r5 = r5.size();
        if (r1 >= r5) goto L_0x00f7;
    L_0x008d:
        r5 = r0.pushMessages;
        r5 = r5.get(r1);
        r5 = (org.telegram.messenger.MessageObject) r5;
        r8 = r5.getDialogId();
        r6 = (r8 > r21 ? 1 : (r8 == r21 ? 0 : -1));
        if (r6 != 0) goto L_0x00f3;
    L_0x009d:
        if (r4 == 0) goto L_0x00a7;
    L_0x009f:
        r6 = r5.messageOwner;
        r6 = r6.date;
        if (r6 > r4) goto L_0x00bb;
    L_0x00a5:
        r6 = 1;
        goto L_0x00bc;
    L_0x00a7:
        if (r25 != 0) goto L_0x00b2;
    L_0x00a9:
        r6 = r5.getId();
        if (r6 <= r3) goto L_0x00a5;
    L_0x00af:
        if (r3 >= 0) goto L_0x00bb;
    L_0x00b1:
        goto L_0x00a5;
    L_0x00b2:
        r6 = r5.getId();
        if (r6 == r3) goto L_0x00a5;
    L_0x00b8:
        if (r3 >= 0) goto L_0x00bb;
    L_0x00ba:
        goto L_0x00a5;
    L_0x00bb:
        r6 = 0;
    L_0x00bc:
        if (r6 == 0) goto L_0x00f3;
    L_0x00be:
        r6 = r0.isPersonalMessage(r5);
        if (r6 == 0) goto L_0x00c9;
    L_0x00c4:
        r6 = r0.personal_count;
        r6 = r6 - r7;
        r0.personal_count = r6;
    L_0x00c9:
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
        if (r5 == 0) goto L_0x00e9;
    L_0x00e3:
        r5 = (long) r5;
        r10 = 32;
        r5 = r5 << r10;
        r8 = r8 | r5;
        goto L_0x00eb;
    L_0x00e9:
        r10 = 32;
    L_0x00eb:
        r5 = r0.pushMessagesDict;
        r5.remove(r8);
        r1 = r1 + -1;
        goto L_0x00f5;
    L_0x00f3:
        r10 = 32;
    L_0x00f5:
        r1 = r1 + r7;
        goto L_0x0085;
    L_0x00f7:
        r1 = r20.isEmpty();
        if (r1 != 0) goto L_0x0105;
    L_0x00fd:
        r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$uwXUA8kYkjmDHBUM6M6MDaJprzI;
        r1.<init>(r0, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x0105:
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

    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b0  */
    public /* synthetic */ void lambda$processNewMessages$16$NotificationsController(java.util.ArrayList r31, java.util.ArrayList r32, boolean r33, boolean r34, java.util.concurrent.CountDownLatch r35) {
        /*
        r30 = this;
        r8 = r30;
        r9 = r31;
        r10 = new android.util.LongSparseArray;
        r10.<init>();
        r0 = r30.getAccountInstance();
        r11 = r0.getNotificationsSettings();
        r12 = 1;
        r0 = "PinnedMessages";
        r13 = r11.getBoolean(r0, r12);
        r0 = 0;
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r18 = 0;
    L_0x0020:
        r1 = r31.size();
        if (r15 >= r1) goto L_0x01df;
    L_0x0026:
        r1 = r9.get(r15);
        r7 = r1;
        r7 = (org.telegram.messenger.MessageObject) r7;
        r1 = r7.messageOwner;
        if (r1 == 0) goto L_0x0047;
    L_0x0031:
        r4 = r1.silent;
        if (r4 == 0) goto L_0x0047;
    L_0x0035:
        r1 = r1.action;
        r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r4 != 0) goto L_0x003f;
    L_0x003b:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r1 == 0) goto L_0x0047;
    L_0x003f:
        r26 = r0;
        r22 = r13;
        r21 = r15;
        goto L_0x0118;
    L_0x0047:
        r1 = r7.getId();
        r4 = (long) r1;
        r1 = r7.isFcmMessage();
        r19 = 0;
        if (r1 == 0) goto L_0x005d;
    L_0x0054:
        r1 = r7.messageOwner;
        r21 = r15;
        r14 = r1.random_id;
        r22 = r13;
        goto L_0x0063;
    L_0x005d:
        r21 = r15;
        r22 = r13;
        r14 = r19;
    L_0x0063:
        r12 = r7.getDialogId();
        r6 = (int) r12;
        r1 = r7.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x0079;
    L_0x0070:
        r2 = (long) r1;
        r1 = 32;
        r1 = r2 << r1;
        r4 = r4 | r1;
        r25 = 1;
        goto L_0x007b;
    L_0x0079:
        r25 = 0;
    L_0x007b:
        r1 = r8.pushMessagesDict;
        r1 = r1.get(r4);
        r1 = (org.telegram.messenger.MessageObject) r1;
        if (r1 != 0) goto L_0x00ac;
    L_0x0085:
        r2 = r7.messageOwner;
        r2 = r2.random_id;
        r26 = (r2 > r19 ? 1 : (r2 == r19 ? 0 : -1));
        if (r26 == 0) goto L_0x00ac;
    L_0x008d:
        r1 = r8.fcmRandomMessagesDict;
        r1 = r1.get(r2);
        r1 = (org.telegram.messenger.MessageObject) r1;
        if (r1 == 0) goto L_0x00a5;
    L_0x0097:
        r2 = r8.fcmRandomMessagesDict;
        r3 = r7.messageOwner;
        r26 = r0;
        r27 = r1;
        r0 = r3.random_id;
        r2.remove(r0);
        goto L_0x00a9;
    L_0x00a5:
        r26 = r0;
        r27 = r1;
    L_0x00a9:
        r1 = r27;
        goto L_0x00ae;
    L_0x00ac:
        r26 = r0;
    L_0x00ae:
        if (r1 == 0) goto L_0x00ee;
    L_0x00b0:
        r0 = r1.isFcmMessage();
        if (r0 == 0) goto L_0x0118;
    L_0x00b6:
        r0 = r8.pushMessagesDict;
        r0.put(r4, r7);
        r0 = r8.pushMessages;
        r0 = r0.indexOf(r1);
        if (r0 < 0) goto L_0x00da;
    L_0x00c3:
        r1 = r8.pushMessages;
        r1.set(r0, r7);
        r0 = r30;
        r1 = r32;
        r2 = r7;
        r3 = r6;
        r4 = r12;
        r6 = r25;
        r12 = r7;
        r7 = r11;
        r0 = r0.addToPopupMessages(r1, r2, r3, r4, r6, r7);
        r26 = r0;
        goto L_0x00db;
    L_0x00da:
        r12 = r7;
    L_0x00db:
        if (r33 == 0) goto L_0x00e9;
    L_0x00dd:
        r0 = r12.localEdit;
        if (r0 == 0) goto L_0x00eb;
    L_0x00e1:
        r1 = r30.getMessagesStorage();
        r1.putPushMessage(r12);
        goto L_0x00eb;
    L_0x00e9:
        r0 = r18;
    L_0x00eb:
        r18 = r0;
        goto L_0x0118;
    L_0x00ee:
        if (r18 == 0) goto L_0x00f1;
    L_0x00f0:
        goto L_0x0118;
    L_0x00f1:
        if (r33 == 0) goto L_0x00fa;
    L_0x00f3:
        r0 = r30.getMessagesStorage();
        r0.putPushMessage(r7);
    L_0x00fa:
        r0 = r8.opened_dialog_id;
        r2 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1));
        if (r2 != 0) goto L_0x010a;
    L_0x0100:
        r0 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r0 == 0) goto L_0x010a;
    L_0x0104:
        if (r33 != 0) goto L_0x0118;
    L_0x0106:
        r30.playInChatSound();
        goto L_0x0118;
    L_0x010a:
        r0 = r7.messageOwner;
        r1 = r0.mentioned;
        if (r1 == 0) goto L_0x0125;
    L_0x0110:
        if (r22 != 0) goto L_0x011e;
    L_0x0112:
        r0 = r0.action;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r0 == 0) goto L_0x011e;
    L_0x0118:
        r27 = r10;
        r0 = r26;
        goto L_0x01d4;
    L_0x011e:
        r0 = r7.messageOwner;
        r0 = r0.from_id;
        r0 = (long) r0;
        r2 = r0;
        goto L_0x0126;
    L_0x0125:
        r2 = r12;
    L_0x0126:
        r0 = r8.isPersonalMessage(r7);
        if (r0 == 0) goto L_0x0132;
    L_0x012c:
        r0 = r8.personal_count;
        r1 = 1;
        r0 = r0 + r1;
        r8.personal_count = r0;
    L_0x0132:
        r0 = r10.indexOfKey(r2);
        if (r0 < 0) goto L_0x0143;
    L_0x0138:
        r0 = r10.valueAt(r0);
        r0 = (java.lang.Boolean) r0;
        r0 = r0.booleanValue();
        goto L_0x015c;
    L_0x0143:
        r0 = r8.getNotifyOverride(r11, r2);
        r1 = -1;
        if (r0 != r1) goto L_0x014f;
    L_0x014a:
        r0 = r8.isGlobalNotificationsEnabled(r2);
        goto L_0x0155;
    L_0x014f:
        r1 = 2;
        if (r0 == r1) goto L_0x0154;
    L_0x0152:
        r0 = 1;
        goto L_0x0155;
    L_0x0154:
        r0 = 0;
    L_0x0155:
        r1 = java.lang.Boolean.valueOf(r0);
        r10.put(r2, r1);
    L_0x015c:
        if (r0 == 0) goto L_0x01ce;
    L_0x015e:
        if (r33 != 0) goto L_0x017a;
    L_0x0160:
        r0 = r30;
        r1 = r32;
        r23 = r2;
        r2 = r7;
        r3 = r6;
        r27 = r10;
        r9 = r4;
        r4 = r23;
        r6 = r25;
        r28 = r12;
        r12 = r7;
        r7 = r11;
        r0 = r0.addToPopupMessages(r1, r2, r3, r4, r6, r7);
        r26 = r0;
        goto L_0x0182;
    L_0x017a:
        r23 = r2;
        r27 = r10;
        r28 = r12;
        r9 = r4;
        r12 = r7;
    L_0x0182:
        if (r17 != 0) goto L_0x0189;
    L_0x0184:
        r0 = r12.messageOwner;
        r0 = r0.from_scheduled;
        goto L_0x018b;
    L_0x0189:
        r0 = r17;
    L_0x018b:
        r1 = r8.delayedPushMessages;
        r1.add(r12);
        r1 = r8.pushMessages;
        r2 = 0;
        r1.add(r2, r12);
        r1 = (r9 > r19 ? 1 : (r9 == r19 ? 0 : -1));
        if (r1 == 0) goto L_0x01a0;
    L_0x019a:
        r1 = r8.pushMessagesDict;
        r1.put(r9, r12);
        goto L_0x01a9;
    L_0x01a0:
        r1 = (r14 > r19 ? 1 : (r14 == r19 ? 0 : -1));
        if (r1 == 0) goto L_0x01a9;
    L_0x01a4:
        r1 = r8.fcmRandomMessagesDict;
        r1.put(r14, r12);
    L_0x01a9:
        r1 = (r28 > r23 ? 1 : (r28 == r23 ? 0 : -1));
        if (r1 == 0) goto L_0x01cb;
    L_0x01ad:
        r1 = r8.pushDialogsOverrideMention;
        r2 = r28;
        r1 = r1.get(r2);
        r1 = (java.lang.Integer) r1;
        r4 = r8.pushDialogsOverrideMention;
        if (r1 != 0) goto L_0x01bd;
    L_0x01bb:
        r12 = 1;
        goto L_0x01c4;
    L_0x01bd:
        r1 = r1.intValue();
        r5 = 1;
        r12 = r1 + 1;
    L_0x01c4:
        r1 = java.lang.Integer.valueOf(r12);
        r4.put(r2, r1);
    L_0x01cb:
        r17 = r0;
        goto L_0x01d0;
    L_0x01ce:
        r27 = r10;
    L_0x01d0:
        r0 = r26;
        r16 = 1;
    L_0x01d4:
        r15 = r21 + 1;
        r9 = r31;
        r13 = r22;
        r10 = r27;
        r12 = 1;
        goto L_0x0020;
    L_0x01df:
        r26 = r0;
        if (r16 == 0) goto L_0x01e7;
    L_0x01e3:
        r0 = r34;
        r8.notifyCheck = r0;
    L_0x01e7:
        r0 = r32.isEmpty();
        if (r0 != 0) goto L_0x0200;
    L_0x01ed:
        r0 = 0;
        r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r0);
        if (r1 != 0) goto L_0x0200;
    L_0x01f4:
        r0 = new org.telegram.messenger.-$$Lambda$NotificationsController$vBhFCZdXUS15Ipx-fzqzTMIuA3o;
        r1 = r32;
        r14 = r26;
        r0.<init>(r8, r1, r14);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x0200:
        if (r33 != 0) goto L_0x0204;
    L_0x0202:
        if (r17 == 0) goto L_0x02ab;
    L_0x0204:
        if (r18 == 0) goto L_0x0212;
    L_0x0206:
        r0 = r8.delayedPushMessages;
        r0.clear();
        r0 = r8.notifyCheck;
        r8.showOrUpdateNotification(r0);
        goto L_0x02ab;
    L_0x0212:
        if (r16 == 0) goto L_0x02ab;
    L_0x0214:
        r0 = r31;
        r1 = 0;
        r0 = r0.get(r1);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.getDialogId();
        r2 = r8.total_unread_count;
        r3 = r8.getNotifyOverride(r11, r0);
        r4 = -1;
        if (r3 != r4) goto L_0x0230;
    L_0x022a:
        r3 = r8.isGlobalNotificationsEnabled(r0);
    L_0x022e:
        r12 = r3;
        goto L_0x0237;
    L_0x0230:
        r4 = 2;
        if (r3 == r4) goto L_0x0235;
    L_0x0233:
        r3 = 1;
        goto L_0x022e;
    L_0x0235:
        r3 = 0;
        goto L_0x022e;
    L_0x0237:
        r3 = r8.pushDialogs;
        r3 = r3.get(r0);
        r3 = (java.lang.Integer) r3;
        if (r3 == 0) goto L_0x0248;
    L_0x0241:
        r4 = r3.intValue();
        r5 = 1;
        r4 = r4 + r5;
        goto L_0x024a;
    L_0x0248:
        r5 = 1;
        r4 = 1;
    L_0x024a:
        r4 = java.lang.Integer.valueOf(r4);
        r6 = r8.notifyCheck;
        if (r6 == 0) goto L_0x0266;
    L_0x0252:
        if (r12 != 0) goto L_0x0266;
    L_0x0254:
        r6 = r8.pushDialogsOverrideMention;
        r6 = r6.get(r0);
        r6 = (java.lang.Integer) r6;
        if (r6 == 0) goto L_0x0266;
    L_0x025e:
        r7 = r6.intValue();
        if (r7 == 0) goto L_0x0266;
    L_0x0264:
        r4 = r6;
        r12 = 1;
    L_0x0266:
        if (r12 == 0) goto L_0x0281;
    L_0x0268:
        if (r3 == 0) goto L_0x0273;
    L_0x026a:
        r5 = r8.total_unread_count;
        r3 = r3.intValue();
        r5 = r5 - r3;
        r8.total_unread_count = r5;
    L_0x0273:
        r3 = r8.total_unread_count;
        r5 = r4.intValue();
        r3 = r3 + r5;
        r8.total_unread_count = r3;
        r3 = r8.pushDialogs;
        r3.put(r0, r4);
    L_0x0281:
        r0 = r8.total_unread_count;
        if (r2 == r0) goto L_0x029d;
    L_0x0285:
        r0 = r8.delayedPushMessages;
        r0.clear();
        r0 = r8.notifyCheck;
        r8.showOrUpdateNotification(r0);
        r0 = r8.pushDialogs;
        r0 = r0.size();
        r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$R3R5Z37efc0XPsswynnBTmucwac;
        r1.<init>(r8, r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x029d:
        r0 = 0;
        r8.notifyCheck = r0;
        r0 = r8.showBadgeNumber;
        if (r0 == 0) goto L_0x02ab;
    L_0x02a4:
        r0 = r30.getTotalAllUnreadCount();
        r8.setBadge(r0);
    L_0x02ab:
        if (r35 == 0) goto L_0x02b0;
    L_0x02ad:
        r35.countDown();
    L_0x02b0:
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
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00ee  */
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
        if (r6 >= r7) goto L_0x0102;
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
        goto L_0x00fe;
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
        if (r11 != 0) goto L_0x00ee;
    L_0x0091:
        r7 = r0.pushDialogs;
        r7.remove(r9);
        r7 = r0.pushDialogsOverrideMention;
        r7.remove(r9);
        r7 = 0;
    L_0x009c:
        r11 = r0.pushMessages;
        r11 = r11.size();
        if (r7 >= r11) goto L_0x00fe;
    L_0x00a4:
        r11 = r0.pushMessages;
        r11 = r11.get(r7);
        r11 = (org.telegram.messenger.MessageObject) r11;
        r12 = r11.messageOwner;
        r12 = r12.from_scheduled;
        if (r12 != 0) goto L_0x00ec;
    L_0x00b2:
        r12 = r11.getDialogId();
        r14 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1));
        if (r14 != 0) goto L_0x00ec;
    L_0x00ba:
        r12 = r0.isPersonalMessage(r11);
        if (r12 == 0) goto L_0x00c5;
    L_0x00c0:
        r12 = r0.personal_count;
        r12 = r12 - r8;
        r0.personal_count = r12;
    L_0x00c5:
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
        if (r14 == 0) goto L_0x00e4;
    L_0x00de:
        r14 = (long) r14;
        r16 = 32;
        r14 = r14 << r16;
        r12 = r12 | r14;
    L_0x00e4:
        r14 = r0.pushMessagesDict;
        r14.remove(r12);
        r2.add(r11);
    L_0x00ec:
        r7 = r7 + r8;
        goto L_0x009c;
    L_0x00ee:
        if (r7 == 0) goto L_0x00fe;
    L_0x00f0:
        r7 = r0.total_unread_count;
        r8 = r13.intValue();
        r7 = r7 + r8;
        r0.total_unread_count = r7;
        r7 = r0.pushDialogs;
        r7.put(r9, r13);
    L_0x00fe:
        r6 = r6 + 1;
        goto L_0x0012;
    L_0x0102:
        r1 = r19.isEmpty();
        if (r1 != 0) goto L_0x0110;
    L_0x0108:
        r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$ONJqyaSxnewsyizGxRK-V30P95A;
        r1.<init>(r0, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x0110:
        r1 = r0.total_unread_count;
        if (r3 == r1) goto L_0x0142;
    L_0x0114:
        r1 = r0.notifyCheck;
        if (r1 != 0) goto L_0x0123;
    L_0x0118:
        r1 = r0.delayedPushMessages;
        r1.clear();
        r1 = r0.notifyCheck;
        r0.showOrUpdateNotification(r1);
        goto L_0x0134;
    L_0x0123:
        r1 = r0.lastOnlineFromOtherDevice;
        r2 = r17.getConnectionsManager();
        r2 = r2.getCurrentTime();
        if (r1 <= r2) goto L_0x0130;
    L_0x012f:
        goto L_0x0131;
    L_0x0130:
        r8 = 0;
    L_0x0131:
        r0.scheduleNotificationDelay(r8);
    L_0x0134:
        r1 = r0.pushDialogs;
        r1 = r1.size();
        r2 = new org.telegram.messenger.-$$Lambda$NotificationsController$GAjtCMO1qmPedRnHLLIKT37DETU;
        r2.<init>(r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x0142:
        r0.notifyCheck = r5;
        r1 = r0.showBadgeNumber;
        if (r1 == 0) goto L_0x014f;
    L_0x0148:
        r1 = r17.getTotalAllUnreadCount();
        r0.setBadge(r1);
    L_0x014f:
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
        if (r1 == 0) goto L_0x00fd;
    L_0x002e:
        r11 = 0;
    L_0x002f:
        r12 = r21.size();
        if (r11 >= r12) goto L_0x00fd;
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
        goto L_0x00f3;
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
        r18 = r5;
        if (r9 == 0) goto L_0x00f3;
    L_0x00b9:
        r4 = r0.opened_dialog_id;
        r9 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1));
        if (r9 != 0) goto L_0x00c4;
    L_0x00bf:
        r4 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r4 == 0) goto L_0x00c4;
    L_0x00c3:
        goto L_0x00f3;
    L_0x00c4:
        r4 = r0.pushMessagesDict;
        r5 = r17;
        r4.put(r13, r5);
        r4 = r0.pushMessages;
        r9 = 0;
        r4.add(r9, r5);
        r4 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1));
        if (r4 == 0) goto L_0x00f3;
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
    L_0x00f3:
        r11 = r12 + 1;
        r5 = r18;
        r4 = 0;
        r7 = 32;
        r10 = 1;
        goto L_0x002f;
    L_0x00fd:
        r18 = r5;
        r1 = 0;
    L_0x0100:
        r4 = r22.size();
        if (r1 >= r4) goto L_0x0159;
    L_0x0106:
        r4 = r2.keyAt(r1);
        r7 = r6.indexOfKey(r4);
        if (r7 < 0) goto L_0x011e;
    L_0x0110:
        r7 = r6.valueAt(r7);
        r7 = (java.lang.Boolean) r7;
        r7 = r7.booleanValue();
        r8 = r7;
        r7 = r18;
        goto L_0x0139;
    L_0x011e:
        r7 = r18;
        r8 = r0.getNotifyOverride(r7, r4);
        r10 = -1;
        if (r8 != r10) goto L_0x012c;
    L_0x0127:
        r8 = r0.isGlobalNotificationsEnabled(r4);
        goto L_0x0132;
    L_0x012c:
        r10 = 2;
        if (r8 == r10) goto L_0x0131;
    L_0x012f:
        r8 = 1;
        goto L_0x0132;
    L_0x0131:
        r8 = 0;
    L_0x0132:
        r10 = java.lang.Boolean.valueOf(r8);
        r6.put(r4, r10);
    L_0x0139:
        if (r8 != 0) goto L_0x013c;
    L_0x013b:
        goto L_0x0154;
    L_0x013c:
        r8 = r2.valueAt(r1);
        r8 = (java.lang.Integer) r8;
        r8 = r8.intValue();
        r10 = r0.pushDialogs;
        r11 = java.lang.Integer.valueOf(r8);
        r10.put(r4, r11);
        r4 = r0.total_unread_count;
        r4 = r4 + r8;
        r0.total_unread_count = r4;
    L_0x0154:
        r1 = r1 + 1;
        r18 = r7;
        goto L_0x0100;
    L_0x0159:
        r7 = r18;
        if (r3 == 0) goto L_0x0261;
    L_0x015d:
        r1 = 0;
    L_0x015e:
        r2 = r23.size();
        if (r1 >= r2) goto L_0x0261;
    L_0x0164:
        r2 = r3.get(r1);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r4 = r2.getId();
        r4 = (long) r4;
        r8 = r2.messageOwner;
        r8 = r8.to_id;
        r8 = r8.channel_id;
        if (r8 == 0) goto L_0x017d;
    L_0x0177:
        r10 = (long) r8;
        r8 = 32;
        r10 = r10 << r8;
        r4 = r4 | r10;
        goto L_0x017f;
    L_0x017d:
        r8 = 32;
    L_0x017f:
        r10 = r0.pushMessagesDict;
        r10 = r10.indexOfKey(r4);
        if (r10 < 0) goto L_0x018c;
    L_0x0187:
        r5 = 0;
        r16 = 1;
        goto L_0x025d;
    L_0x018c:
        r10 = r0.isPersonalMessage(r2);
        if (r10 == 0) goto L_0x0198;
    L_0x0192:
        r10 = r0.personal_count;
        r9 = 1;
        r10 = r10 + r9;
        r0.personal_count = r10;
    L_0x0198:
        r10 = r2.getDialogId();
        r12 = r2.messageOwner;
        r13 = r12.random_id;
        r8 = r12.mentioned;
        if (r8 == 0) goto L_0x01aa;
    L_0x01a4:
        r8 = r12.from_id;
        r21 = r10;
        r9 = (long) r8;
        goto L_0x01ae;
    L_0x01aa:
        r21 = r10;
        r9 = r21;
    L_0x01ae:
        r8 = r6.indexOfKey(r9);
        if (r8 < 0) goto L_0x01c0;
    L_0x01b4:
        r8 = r6.valueAt(r8);
        r8 = (java.lang.Boolean) r8;
        r8 = r8.booleanValue();
        r12 = 2;
        goto L_0x01da;
    L_0x01c0:
        r8 = r0.getNotifyOverride(r7, r9);
        r11 = -1;
        if (r8 != r11) goto L_0x01cd;
    L_0x01c7:
        r8 = r0.isGlobalNotificationsEnabled(r9);
        r12 = 2;
        goto L_0x01d3;
    L_0x01cd:
        r12 = 2;
        if (r8 == r12) goto L_0x01d2;
    L_0x01d0:
        r8 = 1;
        goto L_0x01d3;
    L_0x01d2:
        r8 = 0;
    L_0x01d3:
        r11 = java.lang.Boolean.valueOf(r8);
        r6.put(r9, r11);
    L_0x01da:
        if (r8 == 0) goto L_0x0187;
    L_0x01dc:
        r18 = r13;
        r12 = r0.opened_dialog_id;
        r8 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1));
        if (r8 != 0) goto L_0x01e9;
    L_0x01e4:
        r8 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r8 == 0) goto L_0x01e9;
    L_0x01e8:
        goto L_0x0187;
    L_0x01e9:
        r11 = 0;
        r8 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1));
        if (r8 == 0) goto L_0x01f5;
    L_0x01ef:
        r8 = r0.pushMessagesDict;
        r8.put(r4, r2);
        goto L_0x0200;
    L_0x01f5:
        r4 = (r18 > r11 ? 1 : (r18 == r11 ? 0 : -1));
        if (r4 == 0) goto L_0x0200;
    L_0x01f9:
        r4 = r0.fcmRandomMessagesDict;
        r11 = r18;
        r4.put(r11, r2);
    L_0x0200:
        r4 = r0.pushMessages;
        r5 = 0;
        r4.add(r5, r2);
        r2 = (r21 > r9 ? 1 : (r21 == r9 ? 0 : -1));
        if (r2 == 0) goto L_0x022c;
    L_0x020a:
        r2 = r0.pushDialogsOverrideMention;
        r11 = r21;
        r2 = r2.get(r11);
        r2 = (java.lang.Integer) r2;
        r4 = r0.pushDialogsOverrideMention;
        if (r2 != 0) goto L_0x021c;
    L_0x0218:
        r2 = 1;
        r16 = 1;
        goto L_0x0224;
    L_0x021c:
        r2 = r2.intValue();
        r16 = 1;
        r2 = r2 + 1;
    L_0x0224:
        r2 = java.lang.Integer.valueOf(r2);
        r4.put(r11, r2);
        goto L_0x022e;
    L_0x022c:
        r16 = 1;
    L_0x022e:
        r2 = r0.pushDialogs;
        r2 = r2.get(r9);
        r2 = (java.lang.Integer) r2;
        if (r2 == 0) goto L_0x023f;
    L_0x0238:
        r4 = r2.intValue();
        r4 = r4 + 1;
        goto L_0x0240;
    L_0x023f:
        r4 = 1;
    L_0x0240:
        r4 = java.lang.Integer.valueOf(r4);
        if (r2 == 0) goto L_0x024f;
    L_0x0246:
        r8 = r0.total_unread_count;
        r2 = r2.intValue();
        r8 = r8 - r2;
        r0.total_unread_count = r8;
    L_0x024f:
        r2 = r0.total_unread_count;
        r8 = r4.intValue();
        r2 = r2 + r8;
        r0.total_unread_count = r2;
        r2 = r0.pushDialogs;
        r2.put(r9, r4);
    L_0x025d:
        r1 = r1 + 1;
        goto L_0x015e;
    L_0x0261:
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
        if (r6 >= 0) goto L_0x0280;
    L_0x027f:
        r5 = 1;
    L_0x0280:
        r0.showOrUpdateNotification(r5);
        r1 = r0.showBadgeNumber;
        if (r1 == 0) goto L_0x028e;
    L_0x0287:
        r1 = r20.getTotalAllUnreadCount();
        r0.setBadge(r1);
    L_0x028e:
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
        if (r2 != 0) goto L_0x0b78;
    L_0x0009:
        r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r2 == 0) goto L_0x000f;
    L_0x000d:
        goto L_0x0b78;
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
        r10 = NUM; // 0x7f0e05f4 float:1.8878129E38 double:1.0531629096E-314;
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
        r2 = NUM; // 0x7f0e024e float:1.8876234E38 double:1.053162448E-314;
        r3 = new java.lang.Object[r6];
        r0 = r0.localName;
        r3[r1] = r0;
        r0 = "ChannelMessageNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x00cb:
        r2 = NUM; // 0x7f0e06c6 float:1.8878555E38 double:1.0531630133E-314;
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
        r0 = NUM; // 0x7f0e0CLASSNAME float:1.8881317E38 double:1.0531636863E-314;
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
        if (r8 == 0) goto L_0x0b6c;
    L_0x0193:
        if (r5 != 0) goto L_0x019f;
    L_0x0195:
        if (r2 == 0) goto L_0x019f;
    L_0x0197:
        r4 = "EnablePreviewAll";
        r4 = r7.getBoolean(r4, r6);
        if (r4 != 0) goto L_0x01b5;
    L_0x019f:
        if (r5 == 0) goto L_0x0b6c;
    L_0x01a1:
        if (r3 != 0) goto L_0x01ab;
    L_0x01a3:
        r4 = "EnablePreviewGroup";
        r4 = r7.getBoolean(r4, r6);
        if (r4 != 0) goto L_0x01b5;
    L_0x01ab:
        if (r3 == 0) goto L_0x0b6c;
    L_0x01ad:
        r3 = "EnablePreviewChannel";
        r3 = r7.getBoolean(r3, r6);
        if (r3 == 0) goto L_0x0b6c;
    L_0x01b5:
        r3 = r0.messageOwner;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        r5 = "🖼 ";
        r7 = 19;
        if (r4 == 0) goto L_0x099e;
    L_0x01c0:
        r18[r1] = r9;
        r3 = r3.action;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r4 != 0) goto L_0x0990;
    L_0x01c8:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r4 == 0) goto L_0x01ce;
    L_0x01cc:
        goto L_0x0990;
    L_0x01ce:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
        if (r4 == 0) goto L_0x01e0;
    L_0x01d2:
        r0 = NUM; // 0x7f0e06a5 float:1.8878488E38 double:1.053162997E-314;
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
        r2 = NUM; // 0x7f0e0CLASSNAME float:1.8881406E38 double:1.053163708E-314;
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
        r3 = NUM; // 0x7f0e06df float:1.8878605E38 double:1.0531630257E-314;
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
        if (r4 != 0) goto L_0x0989;
    L_0x0243:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
        if (r4 == 0) goto L_0x0249;
    L_0x0247:
        goto L_0x0989;
    L_0x0249:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r4 == 0) goto L_0x0263;
    L_0x024d:
        r1 = r3.reason;
        r0 = r17.isOut();
        if (r0 != 0) goto L_0x0b61;
    L_0x0255:
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        if (r0 == 0) goto L_0x0b61;
    L_0x0259:
        r0 = NUM; // 0x7f0e01e9 float:1.887603E38 double:1.053162398E-314;
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
        r0 = NUM; // 0x7f0e021d float:1.8876135E38 double:1.053162424E-314;
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
        r0 = NUM; // 0x7f0e06b1 float:1.8878512E38 double:1.053163003E-314;
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
        r0 = NUM; // 0x7f0e06aa float:1.8878498E38 double:1.0531629995E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationGroupAddSelfMega";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x02e8:
        r0 = NUM; // 0x7f0e06a9 float:1.8878496E38 double:1.053162999E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationGroupAddSelf";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x02fa:
        r2 = NUM; // 0x7f0e06a8 float:1.8878494E38 double:1.0531629985E-314;
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
        r0 = NUM; // 0x7f0e06a8 float:1.8878494E38 double:1.0531629985E-314;
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
        r0 = NUM; // 0x7f0e06b2 float:1.8878514E38 double:1.0531630035E-314;
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
        r0 = NUM; // 0x7f0e06a6 float:1.887849E38 double:1.0531629975E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r3.title;
        r2[r6] = r1;
        r1 = "NotificationEditedGroupName";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0393:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
        if (r4 != 0) goto L_0x095b;
    L_0x0397:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
        if (r4 == 0) goto L_0x039d;
    L_0x039b:
        goto L_0x095b;
    L_0x039d:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r4 == 0) goto L_0x0406;
    L_0x03a1:
        r3 = r3.user_id;
        r4 = r16.getUserConfig();
        r4 = r4.getClientUserId();
        if (r3 != r4) goto L_0x03bf;
    L_0x03ad:
        r0 = NUM; // 0x7f0e06af float:1.8878508E38 double:1.053163002E-314;
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
        r0 = NUM; // 0x7f0e06b0 float:1.887851E38 double:1.0531630025E-314;
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
        r2 = NUM; // 0x7f0e06ae float:1.8878506E38 double:1.0531630015E-314;
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
        r0 = NUM; // 0x7f0e007f float:1.8875295E38 double:1.0531622194E-314;
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
        r0 = NUM; // 0x7f0e007f float:1.8875295E38 double:1.0531622194E-314;
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
        if (r2 == 0) goto L_0x0b61;
    L_0x0453:
        r2 = 20;
        if (r10 == 0) goto L_0x06f3;
    L_0x0457:
        r3 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r3 == 0) goto L_0x0461;
    L_0x045d:
        r3 = r10.megagroup;
        if (r3 == 0) goto L_0x06f3;
    L_0x0461:
        r0 = r0.replyMessageObject;
        if (r0 != 0) goto L_0x0477;
    L_0x0465:
        r0 = NUM; // 0x7f0e0692 float:1.887845E38 double:1.0531629876E-314;
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
        r0 = NUM; // 0x7f0e0690 float:1.8878445E38 double:1.0531629867E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedMusic";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x048f:
        r3 = r0.isVideo();
        r4 = NUM; // 0x7f0e069e float:1.8878473E38 double:1.0531629936E-314;
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
        r0 = NUM; // 0x7f0e06a0 float:1.8878477E38 double:1.0531629946E-314;
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
        r0 = NUM; // 0x7f0e068c float:1.8878437E38 double:1.0531629847E-314;
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
        r0 = NUM; // 0x7f0e06a2 float:1.8878481E38 double:1.0531629956E-314;
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
        r0 = NUM; // 0x7f0e0698 float:1.8878461E38 double:1.0531629906E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedRound";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x055a:
        r3 = r0.isSticker();
        if (r3 != 0) goto L_0x06c7;
    L_0x0560:
        r3 = r0.isAnimatedSticker();
        if (r3 == 0) goto L_0x0568;
    L_0x0566:
        goto L_0x06c7;
    L_0x0568:
        r3 = r0.messageOwner;
        r11 = r3.media;
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r12 == 0) goto L_0x05b3;
    L_0x0570:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x05a1;
    L_0x0574:
        r2 = r3.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x05a1;
    L_0x057c:
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
    L_0x05a1:
        r0 = NUM; // 0x7f0e0682 float:1.8878417E38 double:1.0531629797E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedFile";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x05b3:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r12 != 0) goto L_0x06b5;
    L_0x05b7:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r12 == 0) goto L_0x05bd;
    L_0x05bb:
        goto L_0x06b5;
    L_0x05bd:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r12 == 0) goto L_0x05d3;
    L_0x05c1:
        r0 = NUM; // 0x7f0e068a float:1.8878433E38 double:1.0531629837E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGeoLive";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x05d3:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r12 == 0) goto L_0x05f5;
    L_0x05d7:
        r11 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r11;
        r0 = NUM; // 0x7f0e0680 float:1.8878413E38 double:1.053162979E-314;
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
    L_0x05f5:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r12 == 0) goto L_0x0613;
    L_0x05f9:
        r11 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r11;
        r0 = NUM; // 0x7f0e0696 float:1.8878457E38 double:1.0531629896E-314;
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
    L_0x0613:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r12 == 0) goto L_0x0657;
    L_0x0617:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x0645;
    L_0x061b:
        r2 = r3.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0645;
    L_0x0623:
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
    L_0x0645:
        r0 = NUM; // 0x7f0e0694 float:1.8878453E38 double:1.0531629886E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0657:
        r3 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r3 == 0) goto L_0x066d;
    L_0x065b:
        r0 = NUM; // 0x7f0e0684 float:1.887842E38 double:1.0531629807E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGame";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x066d:
        r3 = r0.messageText;
        if (r3 == 0) goto L_0x06a3;
    L_0x0671:
        r3 = r3.length();
        if (r3 <= 0) goto L_0x06a3;
    L_0x0677:
        r0 = r0.messageText;
        r3 = r0.length();
        if (r3 <= r2) goto L_0x0694;
    L_0x067f:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r0 = r0.subSequence(r1, r2);
        r3.append(r0);
        r0 = "...";
        r3.append(r0);
        r0 = r3.toString();
    L_0x0694:
        r2 = new java.lang.Object[r8];
        r2[r1] = r14;
        r2[r6] = r0;
        r0 = r10.title;
        r2[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r4, r2);
        return r0;
    L_0x06a3:
        r0 = NUM; // 0x7f0e0692 float:1.887845E38 double:1.0531629876E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x06b5:
        r0 = NUM; // 0x7f0e0688 float:1.8878429E38 double:1.0531629827E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGeo";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x06c7:
        r0 = r0.getStickerEmoji();
        if (r0 == 0) goto L_0x06e1;
    L_0x06cd:
        r2 = NUM; // 0x7f0e069c float:1.887847E38 double:1.0531629926E-314;
        r3 = new java.lang.Object[r8];
        r3[r1] = r14;
        r1 = r10.title;
        r3[r6] = r1;
        r3[r13] = r0;
        r0 = "NotificationActionPinnedStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x06e1:
        r0 = NUM; // 0x7f0e069a float:1.8878465E38 double:1.0531629916E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x06f3:
        r0 = r0.replyMessageObject;
        if (r0 != 0) goto L_0x0707;
    L_0x06f7:
        r0 = NUM; // 0x7f0e0693 float:1.8878451E38 double:1.053162988E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedNoTextChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0707:
        r3 = r0.isMusic();
        if (r3 == 0) goto L_0x071d;
    L_0x070d:
        r0 = NUM; // 0x7f0e0691 float:1.8878447E38 double:1.053162987E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedMusicChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x071d:
        r3 = r0.isVideo();
        r4 = NUM; // 0x7f0e069f float:1.8878475E38 double:1.053162994E-314;
        r8 = "NotificationActionPinnedTextChannel";
        if (r3 == 0) goto L_0x0769;
    L_0x0728:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x0759;
    L_0x072c:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0759;
    L_0x0736:
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
    L_0x0759:
        r0 = NUM; // 0x7f0e06a1 float:1.887848E38 double:1.053162995E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedVideoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0769:
        r3 = r0.isGif();
        if (r3 == 0) goto L_0x07b0;
    L_0x076f:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x07a0;
    L_0x0773:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x07a0;
    L_0x077d:
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
    L_0x07a0:
        r0 = NUM; // 0x7f0e068d float:1.8878439E38 double:1.053162985E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGifChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x07b0:
        r3 = r0.isVoice();
        if (r3 == 0) goto L_0x07c6;
    L_0x07b6:
        r0 = NUM; // 0x7f0e06a3 float:1.8878484E38 double:1.053162996E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedVoiceChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x07c6:
        r3 = r0.isRoundVideo();
        if (r3 == 0) goto L_0x07dc;
    L_0x07cc:
        r0 = NUM; // 0x7f0e0699 float:1.8878463E38 double:1.053162991E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedRoundChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x07dc:
        r3 = r0.isSticker();
        if (r3 != 0) goto L_0x0933;
    L_0x07e2:
        r3 = r0.isAnimatedSticker();
        if (r3 == 0) goto L_0x07ea;
    L_0x07e8:
        goto L_0x0933;
    L_0x07ea:
        r3 = r0.messageOwner;
        r9 = r3.media;
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r11 == 0) goto L_0x0831;
    L_0x07f2:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x0821;
    L_0x07f6:
        r2 = r3.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0821;
    L_0x07fe:
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
    L_0x0821:
        r0 = NUM; // 0x7f0e0683 float:1.8878419E38 double:1.05316298E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedFileChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0831:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r11 != 0) goto L_0x0923;
    L_0x0835:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r11 == 0) goto L_0x083b;
    L_0x0839:
        goto L_0x0923;
    L_0x083b:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r11 == 0) goto L_0x084f;
    L_0x083f:
        r0 = NUM; // 0x7f0e068b float:1.8878435E38 double:1.053162984E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGeoLiveChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x084f:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r11 == 0) goto L_0x086f;
    L_0x0853:
        r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r9;
        r0 = NUM; // 0x7f0e0681 float:1.8878415E38 double:1.0531629792E-314;
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
    L_0x086f:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r11 == 0) goto L_0x088b;
    L_0x0873:
        r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r9;
        r0 = NUM; // 0x7f0e0697 float:1.887846E38 double:1.05316299E-314;
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = r9.poll;
        r1 = r1.question;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedPollChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x088b:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r11 == 0) goto L_0x08cb;
    L_0x088f:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x08bb;
    L_0x0893:
        r2 = r3.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x08bb;
    L_0x089b:
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
    L_0x08bb:
        r0 = NUM; // 0x7f0e0695 float:1.8878455E38 double:1.053162989E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedPhotoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x08cb:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r3 == 0) goto L_0x08df;
    L_0x08cf:
        r0 = NUM; // 0x7f0e0685 float:1.8878423E38 double:1.053162981E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGameChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x08df:
        r3 = r0.messageText;
        if (r3 == 0) goto L_0x0913;
    L_0x08e3:
        r3 = r3.length();
        if (r3 <= 0) goto L_0x0913;
    L_0x08e9:
        r0 = r0.messageText;
        r3 = r0.length();
        if (r3 <= r2) goto L_0x0906;
    L_0x08f1:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r0 = r0.subSequence(r1, r2);
        r3.append(r0);
        r0 = "...";
        r3.append(r0);
        r0 = r3.toString();
    L_0x0906:
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r1] = r3;
        r2[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r4, r2);
        return r0;
    L_0x0913:
        r0 = NUM; // 0x7f0e0693 float:1.8878451E38 double:1.053162988E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedNoTextChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0923:
        r0 = NUM; // 0x7f0e0689 float:1.887843E38 double:1.053162983E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGeoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0933:
        r0 = r0.getStickerEmoji();
        if (r0 == 0) goto L_0x094b;
    L_0x0939:
        r2 = NUM; // 0x7f0e069d float:1.8878471E38 double:1.053162993E-314;
        r3 = new java.lang.Object[r13];
        r4 = r10.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = "NotificationActionPinnedStickerEmojiChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x094b:
        r0 = NUM; // 0x7f0e069b float:1.8878467E38 double:1.053162992E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedStickerChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x095b:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x0977;
    L_0x0963:
        r0 = r10.megagroup;
        if (r0 != 0) goto L_0x0977;
    L_0x0967:
        r0 = NUM; // 0x7f0e025c float:1.8876263E38 double:1.053162455E-314;
        r2 = new java.lang.Object[r6];
        r3 = r10.title;
        r2[r1] = r3;
        r1 = "ChannelPhotoEditNotification";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0977:
        r0 = NUM; // 0x7f0e06a7 float:1.8878492E38 double:1.053162998E-314;
        r2 = new java.lang.Object[r13];
        r2[r1] = r14;
        r1 = r10.title;
        r2[r6] = r1;
        r1 = "NotificationEditedGroupPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0989:
        r0 = r0.messageText;
        r0 = r0.toString();
        return r0;
    L_0x0990:
        r0 = NUM; // 0x7f0e06a4 float:1.8878486E38 double:1.0531629965E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r14;
        r1 = "NotificationContactJoined";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x099e:
        r1 = r17.isMediaEmpty();
        if (r1 == 0) goto L_0x09bb;
    L_0x09a4:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x09b3;
    L_0x09ae:
        r0 = r0.messageOwner;
        r0 = r0.message;
        return r0;
    L_0x09b3:
        r0 = NUM; // 0x7f0e05f4 float:1.8878129E38 double:1.0531629096E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r11, r0);
        return r0;
    L_0x09bb:
        r1 = r0.messageOwner;
        r2 = r1.media;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r2 == 0) goto L_0x09ff;
    L_0x09c3:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x09e3;
    L_0x09c7:
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x09e3;
    L_0x09cf:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r5);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x09e3:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x09f5;
    L_0x09eb:
        r0 = NUM; // 0x7f0e013e float:1.8875683E38 double:1.0531623137E-314;
        r1 = "AttachDestructingPhoto";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x09f5:
        r0 = NUM; // 0x7f0e014d float:1.8875713E38 double:1.053162321E-314;
        r1 = "AttachPhoto";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x09ff:
        r1 = r17.isVideo();
        if (r1 == 0) goto L_0x0a46;
    L_0x0a05:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0a2a;
    L_0x0a09:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0a2a;
    L_0x0a13:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "📹 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0a2a:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0a3c;
    L_0x0a32:
        r0 = NUM; // 0x7f0e013f float:1.8875685E38 double:1.053162314E-314;
        r1 = "AttachDestructingVideo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a3c:
        r0 = NUM; // 0x7f0e0153 float:1.8875725E38 double:1.053162324E-314;
        r1 = "AttachVideo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a46:
        r1 = r17.isGame();
        if (r1 == 0) goto L_0x0a56;
    L_0x0a4c:
        r0 = NUM; // 0x7f0e0141 float:1.8875689E38 double:1.053162315E-314;
        r1 = "AttachGame";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a56:
        r1 = r17.isVoice();
        if (r1 == 0) goto L_0x0a66;
    L_0x0a5c:
        r0 = NUM; // 0x7f0e013b float:1.8875676E38 double:1.0531623123E-314;
        r1 = "AttachAudio";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a66:
        r1 = r17.isRoundVideo();
        if (r1 == 0) goto L_0x0a76;
    L_0x0a6c:
        r0 = NUM; // 0x7f0e014f float:1.8875717E38 double:1.053162322E-314;
        r1 = "AttachRound";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a76:
        r1 = r17.isMusic();
        if (r1 == 0) goto L_0x0a86;
    L_0x0a7c:
        r0 = NUM; // 0x7f0e014c float:1.887571E38 double:1.0531623207E-314;
        r1 = "AttachMusic";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a86:
        r1 = r0.messageOwner;
        r1 = r1.media;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r2 == 0) goto L_0x0a98;
    L_0x0a8e:
        r0 = NUM; // 0x7f0e013d float:1.887568E38 double:1.0531623132E-314;
        r1 = "AttachContact";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a98:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r2 == 0) goto L_0x0aa6;
    L_0x0a9c:
        r0 = NUM; // 0x7f0e0892 float:1.8879488E38 double:1.0531632406E-314;
        r1 = "Poll";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0aa6:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r2 != 0) goto L_0x0b62;
    L_0x0aaa:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r2 == 0) goto L_0x0ab0;
    L_0x0aae:
        goto L_0x0b62;
    L_0x0ab0:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r2 == 0) goto L_0x0abe;
    L_0x0ab4:
        r0 = NUM; // 0x7f0e0147 float:1.88757E38 double:1.053162318E-314;
        r1 = "AttachLiveLocation";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0abe:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r1 == 0) goto L_0x0b61;
    L_0x0ac2:
        r1 = r17.isSticker();
        if (r1 != 0) goto L_0x0b33;
    L_0x0ac8:
        r1 = r17.isAnimatedSticker();
        if (r1 == 0) goto L_0x0acf;
    L_0x0ace:
        goto L_0x0b33;
    L_0x0acf:
        r1 = r17.isGif();
        if (r1 == 0) goto L_0x0b04;
    L_0x0ad5:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0afa;
    L_0x0ad9:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0afa;
    L_0x0ae3:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "🎬 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0afa:
        r0 = NUM; // 0x7f0e0142 float:1.887569E38 double:1.0531623157E-314;
        r1 = "AttachGif";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0b04:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0b29;
    L_0x0b08:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0b29;
    L_0x0b12:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "📎 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0b29:
        r0 = NUM; // 0x7f0e0140 float:1.8875687E38 double:1.0531623147E-314;
        r1 = "AttachDocument";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0b33:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x0b57;
    L_0x0b39:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r0 = " ";
        r1.append(r0);
        r0 = NUM; // 0x7f0e0150 float:1.887572E38 double:1.0531623226E-314;
        r2 = "AttachSticker";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0b57:
        r0 = NUM; // 0x7f0e0150 float:1.887572E38 double:1.0531623226E-314;
        r1 = "AttachSticker";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0b61:
        return r9;
    L_0x0b62:
        r0 = NUM; // 0x7f0e0149 float:1.8875705E38 double:1.053162319E-314;
        r1 = "AttachLocation";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0b6c:
        if (r19 == 0) goto L_0x0b70;
    L_0x0b6e:
        r19[r1] = r1;
    L_0x0b70:
        r0 = NUM; // 0x7f0e05f4 float:1.8878129E38 double:1.0531629096E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r11, r0);
        return r0;
    L_0x0b78:
        r0 = NUM; // 0x7f0e0CLASSNAME float:1.8881317E38 double:1.0531636863E-314;
        r1 = "YouHaveNewMessage";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:76:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0142 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0142 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0143  */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r17, boolean r18, boolean[] r19, boolean[] r20) {
        /*
        r16 = this;
        r0 = r17;
        r1 = 0;
        r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1);
        if (r2 != 0) goto L_0x11fd;
    L_0x0009:
        r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r2 == 0) goto L_0x000f;
    L_0x000d:
        goto L_0x11fd;
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
        r2 = NUM; // 0x7f0e06d2 float:1.8878579E38 double:1.0531630193E-314;
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
        r2 = NUM; // 0x7f0e024e float:1.8876234E38 double:1.053162448E-314;
        r3 = new java.lang.Object[r6];
        r0 = r0.localName;
        r3[r1] = r0;
        r0 = "ChannelMessageNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x00ab:
        r2 = NUM; // 0x7f0e06c6 float:1.8878555E38 double:1.0531630133E-314;
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
        r9 = r16.getUserConfig();
        r9 = r9.getClientUserId();
        if (r2 != 0) goto L_0x00e4;
    L_0x00d0:
        r2 = r17.isFromUser();
        if (r2 != 0) goto L_0x00df;
    L_0x00d6:
        r2 = r17.getId();
        if (r2 >= 0) goto L_0x00dd;
    L_0x00dc:
        goto L_0x00df;
    L_0x00dd:
        r2 = -r5;
        goto L_0x00ea;
    L_0x00df:
        r2 = r0.messageOwner;
        r2 = r2.from_id;
        goto L_0x00ea;
    L_0x00e4:
        if (r2 != r9) goto L_0x00ea;
    L_0x00e6:
        r2 = r0.messageOwner;
        r2 = r2.from_id;
    L_0x00ea:
        r11 = 0;
        r13 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1));
        if (r13 != 0) goto L_0x00f8;
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
        r11 = 0;
        if (r2 <= 0) goto L_0x012d;
    L_0x00fb:
        r12 = r0.messageOwner;
        r12 = r12.from_scheduled;
        if (r12 == 0) goto L_0x011a;
    L_0x0101:
        r12 = (long) r9;
        r14 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1));
        if (r14 != 0) goto L_0x0110;
    L_0x0106:
        r12 = NUM; // 0x7f0e0605 float:1.8878163E38 double:1.053162918E-314;
        r13 = "MessageScheduledReminderNotification";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        goto L_0x0140;
    L_0x0110:
        r12 = NUM; // 0x7f0e06d9 float:1.8878593E38 double:1.0531630227E-314;
        r13 = "NotificationMessageScheduledName";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        goto L_0x0140;
    L_0x011a:
        r12 = r16.getMessagesController();
        r13 = java.lang.Integer.valueOf(r2);
        r12 = r12.getUser(r13);
        if (r12 == 0) goto L_0x013f;
    L_0x0128:
        r12 = org.telegram.messenger.UserObject.getUserName(r12);
        goto L_0x0140;
    L_0x012d:
        r12 = r16.getMessagesController();
        r13 = -r2;
        r13 = java.lang.Integer.valueOf(r13);
        r12 = r12.getChat(r13);
        if (r12 == 0) goto L_0x013f;
    L_0x013c:
        r12 = r12.title;
        goto L_0x0140;
    L_0x013f:
        r12 = r11;
    L_0x0140:
        if (r12 != 0) goto L_0x0143;
    L_0x0142:
        return r11;
    L_0x0143:
        if (r5 == 0) goto L_0x0154;
    L_0x0145:
        r13 = r16.getMessagesController();
        r14 = java.lang.Integer.valueOf(r5);
        r13 = r13.getChat(r14);
        if (r13 != 0) goto L_0x0155;
    L_0x0153:
        return r11;
    L_0x0154:
        r13 = r11;
    L_0x0155:
        r4 = (int) r3;
        if (r4 != 0) goto L_0x0163;
    L_0x0158:
        r0 = NUM; // 0x7f0e0CLASSNAME float:1.8881317E38 double:1.0531636863E-314;
        r1 = "YouHaveNewMessage";
        r11 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x11fc;
    L_0x0163:
        r3 = "🎬 ";
        r4 = "📎 ";
        r14 = "📹 ";
        r15 = "🖼 ";
        r11 = "NotificationMessageText";
        r10 = 3;
        if (r5 != 0) goto L_0x04f9;
    L_0x0174:
        if (r2 == 0) goto L_0x04f9;
    L_0x0176:
        if (r8 == 0) goto L_0x04e6;
    L_0x0178:
        r2 = "EnablePreviewAll";
        r2 = r7.getBoolean(r2, r6);
        if (r2 == 0) goto L_0x04e6;
    L_0x0180:
        r2 = r0.messageOwner;
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r5 == 0) goto L_0x0244;
    L_0x0186:
        r2 = r2.action;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r3 != 0) goto L_0x0235;
    L_0x018c:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r3 == 0) goto L_0x0192;
    L_0x0190:
        goto L_0x0235;
    L_0x0192:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
        if (r3 == 0) goto L_0x01a5;
    L_0x0196:
        r0 = NUM; // 0x7f0e06a5 float:1.8878488E38 double:1.053162997E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationContactNewPhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x01a5:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
        if (r3 == 0) goto L_0x0206;
    L_0x01a9:
        r2 = NUM; // 0x7f0e0CLASSNAME float:1.8881406E38 double:1.053163708E-314;
        r3 = 2;
        r4 = new java.lang.Object[r3];
        r3 = org.telegram.messenger.LocaleController.getInstance();
        r3 = r3.formatterYear;
        r5 = r0.messageOwner;
        r5 = r5.date;
        r7 = (long) r5;
        r11 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7 = r7 * r11;
        r3 = r3.format(r7);
        r4[r1] = r3;
        r3 = org.telegram.messenger.LocaleController.getInstance();
        r3 = r3.formatterDay;
        r5 = r0.messageOwner;
        r5 = r5.date;
        r7 = (long) r5;
        r7 = r7 * r11;
        r3 = r3.format(r7);
        r4[r6] = r3;
        r3 = "formatDateAtTime";
        r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r4);
        r3 = NUM; // 0x7f0e06df float:1.8878605E38 double:1.0531630257E-314;
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
        r2 = 2;
        r4[r2] = r1;
        r0 = r0.address;
        r4[r10] = r0;
        r0 = "NotificationUnrecognizedDevice";
        r11 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4);
        goto L_0x11fc;
    L_0x0206:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r1 != 0) goto L_0x022d;
    L_0x020a:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
        if (r1 == 0) goto L_0x020f;
    L_0x020e:
        goto L_0x022d;
    L_0x020f:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r1 == 0) goto L_0x11fa;
    L_0x0213:
        r1 = r2.reason;
        r0 = r17.isOut();
        if (r0 != 0) goto L_0x022a;
    L_0x021b:
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        if (r0 == 0) goto L_0x022a;
    L_0x021f:
        r0 = NUM; // 0x7f0e01e9 float:1.887603E38 double:1.053162398E-314;
        r1 = "CallMessageIncomingMissed";
        r11 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x11fc;
    L_0x022a:
        r11 = 0;
        goto L_0x11fc;
    L_0x022d:
        r0 = r0.messageText;
        r11 = r0.toString();
        goto L_0x11fc;
    L_0x0235:
        r0 = NUM; // 0x7f0e06a4 float:1.8878486E38 double:1.0531629965E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationContactJoined";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0244:
        r2 = r17.isMediaEmpty();
        if (r2 == 0) goto L_0x028a;
    L_0x024a:
        if (r18 != 0) goto L_0x027b;
    L_0x024c:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x026c;
    L_0x0256:
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2[r6] = r0;
        r0 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2);
        r19[r1] = r6;
        goto L_0x11fc;
    L_0x026c:
        r0 = NUM; // 0x7f0e06d2 float:1.8878579E38 double:1.0531630193E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x027b:
        r0 = NUM; // 0x7f0e06d2 float:1.8878579E38 double:1.0531630193E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x028a:
        r2 = r0.messageOwner;
        r5 = r2.media;
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r5 == 0) goto L_0x02ed;
    L_0x0292:
        if (r18 != 0) goto L_0x02c7;
    L_0x0294:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r3 < r4) goto L_0x02c7;
    L_0x029a:
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x02c7;
    L_0x02a2:
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r15);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2);
        r19[r1] = r6;
        goto L_0x11fc;
    L_0x02c7:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x02de;
    L_0x02cf:
        r0 = NUM; // 0x7f0e06d6 float:1.8878587E38 double:1.053163021E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageSDPhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x02de:
        r0 = NUM; // 0x7f0e06d3 float:1.887858E38 double:1.05316302E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessagePhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x02ed:
        r2 = r17.isVideo();
        if (r2 == 0) goto L_0x0350;
    L_0x02f3:
        if (r18 != 0) goto L_0x032a;
    L_0x02f5:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x032a;
    L_0x02fb:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x032a;
    L_0x0305:
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r14);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2);
        r19[r1] = r6;
        goto L_0x11fc;
    L_0x032a:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0341;
    L_0x0332:
        r0 = NUM; // 0x7f0e06d7 float:1.887859E38 double:1.0531630217E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageSDVideo";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0341:
        r0 = NUM; // 0x7f0e06dd float:1.8878601E38 double:1.0531630247E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageVideo";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0350:
        r2 = r17.isGame();
        if (r2 == 0) goto L_0x0370;
    L_0x0356:
        r2 = NUM; // 0x7f0e06b9 float:1.8878528E38 double:1.053163007E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r3[r1] = r12;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.game;
        r0 = r0.title;
        r3[r6] = r0;
        r0 = "NotificationMessageGame";
        r11 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x11fc;
    L_0x0370:
        r2 = r17.isVoice();
        if (r2 == 0) goto L_0x0385;
    L_0x0376:
        r0 = NUM; // 0x7f0e06b4 float:1.8878518E38 double:1.0531630044E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageAudio";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0385:
        r2 = r17.isRoundVideo();
        if (r2 == 0) goto L_0x039a;
    L_0x038b:
        r0 = NUM; // 0x7f0e06d5 float:1.8878585E38 double:1.0531630208E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageRound";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x039a:
        r2 = r17.isMusic();
        if (r2 == 0) goto L_0x03af;
    L_0x03a0:
        r0 = NUM; // 0x7f0e06d1 float:1.8878577E38 double:1.053163019E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageMusic";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x03af:
        r2 = r0.messageOwner;
        r2 = r2.media;
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r5 == 0) goto L_0x03d3;
    L_0x03b7:
        r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r2;
        r0 = NUM; // 0x7f0e06b5 float:1.887852E38 double:1.053163005E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r3[r1] = r12;
        r1 = r2.first_name;
        r2 = r2.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r1, r2);
        r3[r6] = r1;
        r1 = "NotificationMessageContact2";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3);
        goto L_0x11fc;
    L_0x03d3:
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r5 == 0) goto L_0x03ef;
    L_0x03d7:
        r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r2;
        r0 = NUM; // 0x7f0e06d4 float:1.8878583E38 double:1.0531630203E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r3[r1] = r12;
        r1 = r2.poll;
        r1 = r1.question;
        r3[r6] = r1;
        r1 = "NotificationMessagePoll2";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3);
        goto L_0x11fc;
    L_0x03ef:
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r5 != 0) goto L_0x04d7;
    L_0x03f3:
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r5 == 0) goto L_0x03f9;
    L_0x03f7:
        goto L_0x04d7;
    L_0x03f9:
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r5 == 0) goto L_0x040c;
    L_0x03fd:
        r0 = NUM; // 0x7f0e06cf float:1.8878573E38 double:1.053163018E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageLiveLocation";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x040c:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r2 == 0) goto L_0x11fa;
    L_0x0410:
        r2 = r17.isSticker();
        if (r2 != 0) goto L_0x04b0;
    L_0x0416:
        r2 = r17.isAnimatedSticker();
        if (r2 == 0) goto L_0x041e;
    L_0x041c:
        goto L_0x04b0;
    L_0x041e:
        r2 = r17.isGif();
        if (r2 == 0) goto L_0x046a;
    L_0x0424:
        if (r18 != 0) goto L_0x045b;
    L_0x0426:
        r2 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r2 < r4) goto L_0x045b;
    L_0x042c:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x045b;
    L_0x0436:
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r4.append(r0);
        r0 = r4.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2);
        r19[r1] = r6;
        goto L_0x11fc;
    L_0x045b:
        r0 = NUM; // 0x7f0e06bb float:1.8878532E38 double:1.053163008E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageGif";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x046a:
        if (r18 != 0) goto L_0x04a1;
    L_0x046c:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x04a1;
    L_0x0472:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x04a1;
    L_0x047c:
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r4);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2);
        r19[r1] = r6;
        goto L_0x11fc;
    L_0x04a1:
        r0 = NUM; // 0x7f0e06b6 float:1.8878522E38 double:1.0531630054E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageDocument";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x04b0:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x04c7;
    L_0x04b6:
        r2 = NUM; // 0x7f0e06db float:1.8878597E38 double:1.0531630237E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r3[r1] = r12;
        r3[r6] = r0;
        r0 = "NotificationMessageStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x04c7:
        r0 = NUM; // 0x7f0e06da float:1.8878595E38 double:1.053163023E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
    L_0x04d4:
        r11 = r0;
        goto L_0x11fc;
    L_0x04d7:
        r0 = NUM; // 0x7f0e06d0 float:1.8878575E38 double:1.0531630183E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageMap";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x04e6:
        if (r20 == 0) goto L_0x04ea;
    L_0x04e8:
        r20[r1] = r1;
    L_0x04ea:
        r0 = NUM; // 0x7f0e06d2 float:1.8878579E38 double:1.0531630193E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "NotificationMessageNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x04f9:
        if (r5 == 0) goto L_0x11fa;
    L_0x04fb:
        r5 = org.telegram.messenger.ChatObject.isChannel(r13);
        if (r5 == 0) goto L_0x0507;
    L_0x0501:
        r5 = r13.megagroup;
        if (r5 != 0) goto L_0x0507;
    L_0x0505:
        r5 = 1;
        goto L_0x0508;
    L_0x0507:
        r5 = 0;
    L_0x0508:
        if (r8 == 0) goto L_0x11cb;
    L_0x050a:
        if (r5 != 0) goto L_0x0514;
    L_0x050c:
        r8 = "EnablePreviewGroup";
        r8 = r7.getBoolean(r8, r6);
        if (r8 != 0) goto L_0x051e;
    L_0x0514:
        if (r5 == 0) goto L_0x11cb;
    L_0x0516:
        r5 = "EnablePreviewChannel";
        r5 = r7.getBoolean(r5, r6);
        if (r5 == 0) goto L_0x11cb;
    L_0x051e:
        r5 = r0.messageOwner;
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r7 == 0) goto L_0x0cd4;
    L_0x0524:
        r5 = r5.action;
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r7 == 0) goto L_0x062f;
    L_0x052a:
        r3 = r5.user_id;
        if (r3 != 0) goto L_0x0546;
    L_0x052e:
        r4 = r5.users;
        r4 = r4.size();
        if (r4 != r6) goto L_0x0546;
    L_0x0536:
        r3 = r0.messageOwner;
        r3 = r3.action;
        r3 = r3.users;
        r3 = r3.get(r1);
        r3 = (java.lang.Integer) r3;
        r3 = r3.intValue();
    L_0x0546:
        if (r3 == 0) goto L_0x05d8;
    L_0x0548:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x0568;
    L_0x0550:
        r0 = r13.megagroup;
        if (r0 != 0) goto L_0x0568;
    L_0x0554:
        r0 = NUM; // 0x7f0e021d float:1.8876135E38 double:1.053162424E-314;
        r4 = 2;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "ChannelAddedByNotification";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0568:
        r4 = 2;
        if (r3 != r9) goto L_0x057e;
    L_0x056b:
        r0 = NUM; // 0x7f0e06b1 float:1.8878512E38 double:1.053163003E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationInvitedToGroup";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x057e:
        r0 = r16.getMessagesController();
        r3 = java.lang.Integer.valueOf(r3);
        r0 = r0.getUser(r3);
        if (r0 != 0) goto L_0x058e;
    L_0x058c:
        r3 = 0;
        return r3;
    L_0x058e:
        r3 = r0.id;
        if (r2 != r3) goto L_0x05be;
    L_0x0592:
        r0 = r13.megagroup;
        if (r0 == 0) goto L_0x05aa;
    L_0x0596:
        r0 = NUM; // 0x7f0e06aa float:1.8878498E38 double:1.0531629995E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationGroupAddSelfMega";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x05aa:
        r2 = 2;
        r0 = NUM; // 0x7f0e06a9 float:1.8878496E38 double:1.053162999E-314;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationGroupAddSelf";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x05be:
        r2 = NUM; // 0x7f0e06a8 float:1.8878494E38 double:1.0531629985E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r1 = r13.title;
        r3[r6] = r1;
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r1 = 2;
        r3[r1] = r0;
        r0 = "NotificationGroupAddMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x05d8:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = 0;
    L_0x05de:
        r4 = r0.messageOwner;
        r4 = r4.action;
        r4 = r4.users;
        r4 = r4.size();
        if (r3 >= r4) goto L_0x0615;
    L_0x05ea:
        r4 = r16.getMessagesController();
        r5 = r0.messageOwner;
        r5 = r5.action;
        r5 = r5.users;
        r5 = r5.get(r3);
        r5 = (java.lang.Integer) r5;
        r4 = r4.getUser(r5);
        if (r4 == 0) goto L_0x0612;
    L_0x0600:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
        r5 = r2.length();
        if (r5 == 0) goto L_0x060f;
    L_0x060a:
        r5 = ", ";
        r2.append(r5);
    L_0x060f:
        r2.append(r4);
    L_0x0612:
        r3 = r3 + 1;
        goto L_0x05de;
    L_0x0615:
        r0 = NUM; // 0x7f0e06a8 float:1.8878494E38 double:1.0531629985E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r1 = r13.title;
        r3[r6] = r1;
        r1 = r2.toString();
        r7 = 2;
        r3[r7] = r1;
        r1 = "NotificationGroupAddMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3);
        goto L_0x04d4;
    L_0x062f:
        r7 = 2;
        r8 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
        if (r8 == 0) goto L_0x0647;
    L_0x0634:
        r0 = NUM; // 0x7f0e06b2 float:1.8878514E38 double:1.0531630035E-314;
        r2 = new java.lang.Object[r7];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationInvitedToGroupByLink";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0647:
        r8 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
        if (r8 == 0) goto L_0x065e;
    L_0x064b:
        r0 = NUM; // 0x7f0e06a6 float:1.887849E38 double:1.0531629975E-314;
        r2 = new java.lang.Object[r7];
        r2[r1] = r12;
        r1 = r5.title;
        r2[r6] = r1;
        r1 = "NotificationEditedGroupName";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x065e:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
        if (r7 != 0) goto L_0x0ca3;
    L_0x0662:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
        if (r7 == 0) goto L_0x0668;
    L_0x0666:
        goto L_0x0ca3;
    L_0x0668:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r7 == 0) goto L_0x06ca;
    L_0x066c:
        r3 = r5.user_id;
        if (r3 != r9) goto L_0x0684;
    L_0x0670:
        r0 = NUM; // 0x7f0e06af float:1.8878508E38 double:1.053163002E-314;
        r4 = 2;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationGroupKickYou";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0684:
        r4 = 2;
        if (r3 != r2) goto L_0x069a;
    L_0x0687:
        r0 = NUM; // 0x7f0e06b0 float:1.887851E38 double:1.0531630025E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationGroupLeftMember";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x069a:
        r2 = r16.getMessagesController();
        r0 = r0.messageOwner;
        r0 = r0.action;
        r0 = r0.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r2.getUser(r0);
        if (r0 != 0) goto L_0x06b0;
    L_0x06ae:
        r2 = 0;
        return r2;
    L_0x06b0:
        r2 = NUM; // 0x7f0e06ae float:1.8878506E38 double:1.0531630015E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r1 = r13.title;
        r3[r6] = r1;
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r1 = 2;
        r3[r1] = r0;
        r0 = "NotificationGroupKickMember";
        r11 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x11fc;
    L_0x06ca:
        r2 = 0;
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
        if (r7 == 0) goto L_0x06d7;
    L_0x06cf:
        r0 = r0.messageText;
        r11 = r0.toString();
        goto L_0x11fc;
    L_0x06d7:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r7 == 0) goto L_0x06e3;
    L_0x06db:
        r0 = r0.messageText;
        r11 = r0.toString();
        goto L_0x11fc;
    L_0x06e3:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r7 == 0) goto L_0x06f8;
    L_0x06e7:
        r0 = NUM; // 0x7f0e007f float:1.8875295E38 double:1.0531622194E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "ActionMigrateFromGroupNotify";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x06f8:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r7 == 0) goto L_0x070d;
    L_0x06fc:
        r0 = NUM; // 0x7f0e007f float:1.8875295E38 double:1.0531622194E-314;
        r2 = new java.lang.Object[r6];
        r3 = r5.title;
        r2[r1] = r3;
        r1 = "ActionMigrateFromGroupNotify";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x070d:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
        if (r7 == 0) goto L_0x0719;
    L_0x0711:
        r0 = r0.messageText;
        r11 = r0.toString();
        goto L_0x11fc;
    L_0x0719:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r7 == 0) goto L_0x0CLASSNAME;
    L_0x071d:
        if (r13 == 0) goto L_0x09ff;
    L_0x071f:
        r2 = org.telegram.messenger.ChatObject.isChannel(r13);
        if (r2 == 0) goto L_0x0729;
    L_0x0725:
        r2 = r13.megagroup;
        if (r2 == 0) goto L_0x09ff;
    L_0x0729:
        r2 = r0.replyMessageObject;
        if (r2 != 0) goto L_0x0741;
    L_0x072d:
        r0 = NUM; // 0x7f0e0692 float:1.887845E38 double:1.0531629876E-314;
        r5 = 2;
        r2 = new java.lang.Object[r5];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0741:
        r5 = 2;
        r7 = r2.isMusic();
        if (r7 == 0) goto L_0x075b;
    L_0x0748:
        r0 = NUM; // 0x7f0e0690 float:1.8878445E38 double:1.0531629867E-314;
        r2 = new java.lang.Object[r5];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedMusic";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x075b:
        r5 = r2.isVideo();
        if (r5 == 0) goto L_0x07ae;
    L_0x0761:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r0 < r3) goto L_0x079a;
    L_0x0767:
        r0 = r2.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x079a;
    L_0x0771:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r14);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0e069e float:1.8878473E38 double:1.0531629936E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r3[r6] = r0;
        r0 = r13.title;
        r4 = 2;
        r3[r4] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x079a:
        r4 = 2;
        r0 = NUM; // 0x7f0e06a0 float:1.8878477E38 double:1.0531629946E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedVideo";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x07ae:
        r5 = r2.isGif();
        if (r5 == 0) goto L_0x0801;
    L_0x07b4:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r0 < r4) goto L_0x07ed;
    L_0x07ba:
        r0 = r2.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x07ed;
    L_0x07c4:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0e069e float:1.8878473E38 double:1.0531629936E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r3[r6] = r0;
        r0 = r13.title;
        r5 = 2;
        r3[r5] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x07ed:
        r5 = 2;
        r0 = NUM; // 0x7f0e068c float:1.8878437E38 double:1.0531629847E-314;
        r2 = new java.lang.Object[r5];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGif";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0801:
        r5 = 2;
        r3 = r2.isVoice();
        if (r3 == 0) goto L_0x081b;
    L_0x0808:
        r0 = NUM; // 0x7f0e06a2 float:1.8878481E38 double:1.0531629956E-314;
        r2 = new java.lang.Object[r5];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedVoice";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x081b:
        r3 = r2.isRoundVideo();
        if (r3 == 0) goto L_0x0834;
    L_0x0821:
        r0 = NUM; // 0x7f0e0698 float:1.8878461E38 double:1.0531629906E-314;
        r2 = new java.lang.Object[r5];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedRound";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0834:
        r3 = r2.isSticker();
        if (r3 != 0) goto L_0x09cf;
    L_0x083a:
        r3 = r2.isAnimatedSticker();
        if (r3 == 0) goto L_0x0842;
    L_0x0840:
        goto L_0x09cf;
    L_0x0842:
        r3 = r2.messageOwner;
        r5 = r3.media;
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r7 == 0) goto L_0x0895;
    L_0x084a:
        r0 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r0 < r5) goto L_0x0881;
    L_0x0850:
        r0 = r3.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0881;
    L_0x0858:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r4);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0e069e float:1.8878473E38 double:1.0531629936E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r3[r6] = r0;
        r0 = r13.title;
        r4 = 2;
        r3[r4] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x0881:
        r4 = 2;
        r0 = NUM; // 0x7f0e0682 float:1.8878417E38 double:1.0531629797E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedFile";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0895:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r4 != 0) goto L_0x09bb;
    L_0x0899:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r4 == 0) goto L_0x089f;
    L_0x089d:
        goto L_0x09bb;
    L_0x089f:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r4 == 0) goto L_0x08b7;
    L_0x08a3:
        r0 = NUM; // 0x7f0e068a float:1.8878433E38 double:1.0531629837E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGeoLive";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x08b7:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r4 == 0) goto L_0x08df;
    L_0x08bb:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r0;
        r2 = NUM; // 0x7f0e0680 float:1.8878413E38 double:1.053162979E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r1 = r13.title;
        r3[r6] = r1;
        r1 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r1, r0);
        r1 = 2;
        r3[r1] = r0;
        r0 = "NotificationActionPinnedContact2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x08df:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r0 == 0) goto L_0x08ff;
    L_0x08e3:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5;
        r0 = NUM; // 0x7f0e0696 float:1.8878457E38 double:1.0531629896E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = r5.poll;
        r1 = r1.question;
        r3 = 2;
        r2[r3] = r1;
        r1 = "NotificationActionPinnedPoll2";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x08ff:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 == 0) goto L_0x094e;
    L_0x0903:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r0 < r4) goto L_0x093a;
    L_0x0909:
        r0 = r3.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x093a;
    L_0x0911:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r15);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0e069e float:1.8878473E38 double:1.0531629936E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r3[r6] = r0;
        r0 = r13.title;
        r4 = 2;
        r3[r4] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x093a:
        r4 = 2;
        r0 = NUM; // 0x7f0e0694 float:1.8878453E38 double:1.0531629886E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x094e:
        r4 = 2;
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x0966;
    L_0x0953:
        r0 = NUM; // 0x7f0e0684 float:1.887842E38 double:1.0531629807E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGame";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0966:
        r0 = r2.messageText;
        if (r0 == 0) goto L_0x09a7;
    L_0x096a:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x09a7;
    L_0x0970:
        r0 = r2.messageText;
        r2 = r0.length();
        r3 = 20;
        if (r2 <= r3) goto L_0x0991;
    L_0x097a:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = 20;
        r0 = r0.subSequence(r1, r3);
        r2.append(r0);
        r0 = "...";
        r2.append(r0);
        r0 = r2.toString();
    L_0x0991:
        r2 = NUM; // 0x7f0e069e float:1.8878473E38 double:1.0531629936E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r3[r6] = r0;
        r0 = r13.title;
        r4 = 2;
        r3[r4] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x09a7:
        r4 = 2;
        r0 = NUM; // 0x7f0e0692 float:1.887845E38 double:1.0531629876E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x09bb:
        r4 = 2;
        r0 = NUM; // 0x7f0e0688 float:1.8878429E38 double:1.0531629827E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedGeo";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x09cf:
        r0 = r2.getStickerEmoji();
        if (r0 == 0) goto L_0x09eb;
    L_0x09d5:
        r2 = NUM; // 0x7f0e069c float:1.887847E38 double:1.0531629926E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r1 = r13.title;
        r3[r6] = r1;
        r4 = 2;
        r3[r4] = r0;
        r0 = "NotificationActionPinnedStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x09eb:
        r4 = 2;
        r0 = NUM; // 0x7f0e069a float:1.8878465E38 double:1.0531629916E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x09ff:
        r2 = r0.replyMessageObject;
        if (r2 != 0) goto L_0x0a14;
    L_0x0a03:
        r0 = NUM; // 0x7f0e0693 float:1.8878451E38 double:1.053162988E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedNoTextChannel";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0a14:
        r5 = r2.isMusic();
        if (r5 == 0) goto L_0x0a2b;
    L_0x0a1a:
        r0 = NUM; // 0x7f0e0691 float:1.8878447E38 double:1.053162987E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedMusicChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0a2b:
        r5 = r2.isVideo();
        r7 = "NotificationActionPinnedTextChannel";
        if (r5 == 0) goto L_0x0a79;
    L_0x0a33:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r0 < r3) goto L_0x0a68;
    L_0x0a39:
        r0 = r2.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0a68;
    L_0x0a43:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r14);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0e069f float:1.8878475E38 double:1.053162994E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = r13.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r2, r3);
        goto L_0x04d4;
    L_0x0a68:
        r0 = NUM; // 0x7f0e06a1 float:1.887848E38 double:1.053162995E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedVideoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0a79:
        r5 = r2.isGif();
        if (r5 == 0) goto L_0x0ac5;
    L_0x0a7f:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r0 < r4) goto L_0x0ab4;
    L_0x0a85:
        r0 = r2.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0ab4;
    L_0x0a8f:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0e069f float:1.8878475E38 double:1.053162994E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = r13.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r2, r3);
        goto L_0x04d4;
    L_0x0ab4:
        r0 = NUM; // 0x7f0e068d float:1.8878439E38 double:1.053162985E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGifChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0ac5:
        r3 = r2.isVoice();
        if (r3 == 0) goto L_0x0adc;
    L_0x0acb:
        r0 = NUM; // 0x7f0e06a3 float:1.8878484E38 double:1.053162996E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedVoiceChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0adc:
        r3 = r2.isRoundVideo();
        if (r3 == 0) goto L_0x0af3;
    L_0x0ae2:
        r0 = NUM; // 0x7f0e0699 float:1.8878463E38 double:1.053162991E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedRoundChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0af3:
        r3 = r2.isSticker();
        if (r3 != 0) goto L_0x0c6c;
    L_0x0af9:
        r3 = r2.isAnimatedSticker();
        if (r3 == 0) goto L_0x0b01;
    L_0x0aff:
        goto L_0x0c6c;
    L_0x0b01:
        r3 = r2.messageOwner;
        r5 = r3.media;
        r8 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r8 == 0) goto L_0x0b4d;
    L_0x0b09:
        r0 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r0 < r5) goto L_0x0b3c;
    L_0x0b0f:
        r0 = r3.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0b3c;
    L_0x0b17:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r4);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0e069f float:1.8878475E38 double:1.053162994E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = r13.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r2, r3);
        goto L_0x04d4;
    L_0x0b3c:
        r0 = NUM; // 0x7f0e0683 float:1.8878419E38 double:1.05316298E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedFileChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0b4d:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r4 != 0) goto L_0x0c5b;
    L_0x0b51:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r4 == 0) goto L_0x0b57;
    L_0x0b55:
        goto L_0x0c5b;
    L_0x0b57:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r4 == 0) goto L_0x0b6c;
    L_0x0b5b:
        r0 = NUM; // 0x7f0e068b float:1.8878435E38 double:1.053162984E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGeoLiveChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0b6c:
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r4 == 0) goto L_0x0b92;
    L_0x0b70:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r0;
        r2 = NUM; // 0x7f0e0681 float:1.8878415E38 double:1.0531629792E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = r13.title;
        r3[r1] = r4;
        r1 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r1, r0);
        r3[r6] = r0;
        r0 = "NotificationActionPinnedContactChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x0b92:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r0 == 0) goto L_0x0bb0;
    L_0x0b96:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5;
        r0 = NUM; // 0x7f0e0697 float:1.887846E38 double:1.05316299E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = r5.poll;
        r1 = r1.question;
        r2[r6] = r1;
        r1 = "NotificationActionPinnedPollChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0bb0:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 == 0) goto L_0x0bf8;
    L_0x0bb4:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r0 < r4) goto L_0x0be7;
    L_0x0bba:
        r0 = r3.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0be7;
    L_0x0bc2:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r15);
        r2 = r2.messageOwner;
        r2 = r2.message;
        r0.append(r2);
        r0 = r0.toString();
        r2 = NUM; // 0x7f0e069f float:1.8878475E38 double:1.053162994E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = r13.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r2, r3);
        goto L_0x04d4;
    L_0x0be7:
        r0 = NUM; // 0x7f0e0695 float:1.8878455E38 double:1.053162989E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedPhotoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0bf8:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x0c0d;
    L_0x0bfc:
        r0 = NUM; // 0x7f0e0685 float:1.8878423E38 double:1.053162981E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGameChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0c0d:
        r0 = r2.messageText;
        if (r0 == 0) goto L_0x0c4a;
    L_0x0CLASSNAME:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x0c4a;
    L_0x0CLASSNAME:
        r0 = r2.messageText;
        r2 = r0.length();
        r3 = 20;
        if (r2 <= r3) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = 20;
        r0 = r0.subSequence(r1, r3);
        r2.append(r0);
        r0 = "...";
        r2.append(r0);
        r0 = r2.toString();
    L_0x0CLASSNAME:
        r2 = NUM; // 0x7f0e069f float:1.8878475E38 double:1.053162994E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = r13.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r2, r3);
        goto L_0x04d4;
    L_0x0c4a:
        r0 = NUM; // 0x7f0e0693 float:1.8878451E38 double:1.053162988E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedNoTextChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0c5b:
        r0 = NUM; // 0x7f0e0689 float:1.887843E38 double:1.053162983E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedGeoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0c6c:
        r0 = r2.getStickerEmoji();
        if (r0 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = NUM; // 0x7f0e069d float:1.8878471E38 double:1.053162993E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = r13.title;
        r3[r1] = r4;
        r3[r6] = r0;
        r0 = "NotificationActionPinnedStickerEmojiChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x0CLASSNAME:
        r0 = NUM; // 0x7f0e069b float:1.8878467E38 double:1.053162992E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "NotificationActionPinnedStickerChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0CLASSNAME:
        r1 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r1 == 0) goto L_0x11fb;
    L_0x0c9b:
        r0 = r0.messageText;
        r11 = r0.toString();
        goto L_0x11fc;
    L_0x0ca3:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x0cc0;
    L_0x0cab:
        r0 = r13.megagroup;
        if (r0 != 0) goto L_0x0cc0;
    L_0x0caf:
        r0 = NUM; // 0x7f0e025c float:1.8876263E38 double:1.053162455E-314;
        r2 = new java.lang.Object[r6];
        r3 = r13.title;
        r2[r1] = r3;
        r1 = "ChannelPhotoEditNotification";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0cc0:
        r0 = NUM; // 0x7f0e06a7 float:1.8878492E38 double:1.053162998E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationEditedGroupPhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0cd4:
        r2 = 0;
        r5 = org.telegram.messenger.ChatObject.isChannel(r13);
        if (r5 == 0) goto L_0x0var_;
    L_0x0cdb:
        r5 = r13.megagroup;
        if (r5 != 0) goto L_0x0var_;
    L_0x0cdf:
        r5 = r17.isMediaEmpty();
        if (r5 == 0) goto L_0x0d18;
    L_0x0ce5:
        if (r18 != 0) goto L_0x0d09;
    L_0x0ce7:
        r2 = r0.messageOwner;
        r2 = r2.message;
        if (r2 == 0) goto L_0x0d09;
    L_0x0ced:
        r2 = r2.length();
        if (r2 == 0) goto L_0x0d09;
    L_0x0cf3:
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2[r6] = r0;
        r0 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2);
        r19[r1] = r6;
        goto L_0x11fc;
    L_0x0d09:
        r0 = NUM; // 0x7f0e024e float:1.8876234E38 double:1.053162448E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0d18:
        r5 = r0.messageOwner;
        r7 = r5.media;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r7 == 0) goto L_0x0d64;
    L_0x0d20:
        if (r18 != 0) goto L_0x0d55;
    L_0x0d22:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0d55;
    L_0x0d28:
        r2 = r5.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0d55;
    L_0x0d30:
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r15);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2);
        r19[r1] = r6;
        goto L_0x11fc;
    L_0x0d55:
        r0 = NUM; // 0x7f0e024f float:1.8876236E38 double:1.0531624486E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessagePhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0d64:
        r5 = r17.isVideo();
        if (r5 == 0) goto L_0x0db0;
    L_0x0d6a:
        if (r18 != 0) goto L_0x0da1;
    L_0x0d6c:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0da1;
    L_0x0d72:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0da1;
    L_0x0d7c:
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r14);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2);
        r19[r1] = r6;
        goto L_0x11fc;
    L_0x0da1:
        r0 = NUM; // 0x7f0e0254 float:1.8876246E38 double:1.053162451E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageVideo";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0db0:
        r5 = r17.isVoice();
        if (r5 == 0) goto L_0x0dc5;
    L_0x0db6:
        r0 = NUM; // 0x7f0e0246 float:1.8876218E38 double:1.053162444E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageAudio";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0dc5:
        r5 = r17.isRoundVideo();
        if (r5 == 0) goto L_0x0dda;
    L_0x0dcb:
        r0 = NUM; // 0x7f0e0251 float:1.887624E38 double:1.0531624496E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageRound";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0dda:
        r5 = r17.isMusic();
        if (r5 == 0) goto L_0x0def;
    L_0x0de0:
        r0 = NUM; // 0x7f0e024d float:1.8876232E38 double:1.0531624476E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageMusic";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0def:
        r5 = r0.messageOwner;
        r5 = r5.media;
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r7 == 0) goto L_0x0e13;
    L_0x0df7:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r5;
        r0 = NUM; // 0x7f0e0247 float:1.887622E38 double:1.0531624447E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r1 = r5.first_name;
        r3 = r5.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r1, r3);
        r2[r6] = r1;
        r1 = "ChannelMessageContact2";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0e13:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r7 == 0) goto L_0x0e2f;
    L_0x0e17:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5;
        r0 = NUM; // 0x7f0e0250 float:1.8876238E38 double:1.053162449E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r1 = r5.poll;
        r1 = r1.question;
        r2[r6] = r1;
        r1 = "ChannelMessagePoll2";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0e2f:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r7 != 0) goto L_0x0var_;
    L_0x0e33:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r7 == 0) goto L_0x0e39;
    L_0x0e37:
        goto L_0x0var_;
    L_0x0e39:
        r7 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r7 == 0) goto L_0x0e4c;
    L_0x0e3d:
        r0 = NUM; // 0x7f0e024b float:1.8876228E38 double:1.0531624466E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageLiveLocation";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0e4c:
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r5 == 0) goto L_0x11fb;
    L_0x0e50:
        r2 = r17.isSticker();
        if (r2 != 0) goto L_0x0ef0;
    L_0x0e56:
        r2 = r17.isAnimatedSticker();
        if (r2 == 0) goto L_0x0e5e;
    L_0x0e5c:
        goto L_0x0ef0;
    L_0x0e5e:
        r2 = r17.isGif();
        if (r2 == 0) goto L_0x0eaa;
    L_0x0e64:
        if (r18 != 0) goto L_0x0e9b;
    L_0x0e66:
        r2 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r2 < r4) goto L_0x0e9b;
    L_0x0e6c:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0e9b;
    L_0x0e76:
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r4.append(r0);
        r0 = r4.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2);
        r19[r1] = r6;
        goto L_0x11fc;
    L_0x0e9b:
        r0 = NUM; // 0x7f0e024a float:1.8876226E38 double:1.053162446E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageGIF";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0eaa:
        if (r18 != 0) goto L_0x0ee1;
    L_0x0eac:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0ee1;
    L_0x0eb2:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0ee1;
    L_0x0ebc:
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r4);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2[r6] = r0;
        r0 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2);
        r19[r1] = r6;
        goto L_0x11fc;
    L_0x0ee1:
        r0 = NUM; // 0x7f0e0248 float:1.8876222E38 double:1.053162445E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageDocument";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0ef0:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x0var_;
    L_0x0ef6:
        r2 = NUM; // 0x7f0e0253 float:1.8876244E38 double:1.0531624506E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r3[r1] = r12;
        r3[r6] = r0;
        r0 = "ChannelMessageStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x0var_:
        r0 = NUM; // 0x7f0e0252 float:1.8876242E38 double:1.05316245E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x0var_:
        r0 = NUM; // 0x7f0e024c float:1.887623E38 double:1.053162447E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageMap";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0var_:
        r5 = r17.isMediaEmpty();
        r7 = NUM; // 0x7f0e06cc float:1.8878567E38 double:1.0531630163E-314;
        r8 = "NotificationMessageGroupText";
        if (r5 == 0) goto L_0x0var_;
    L_0x0var_:
        if (r18 != 0) goto L_0x0var_;
    L_0x0var_:
        r2 = r0.messageOwner;
        r2 = r2.message;
        if (r2 == 0) goto L_0x0var_;
    L_0x0var_:
        r2 = r2.length();
        if (r2 == 0) goto L_0x0var_;
    L_0x0f3f:
        r2 = new java.lang.Object[r10];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3 = 2;
        r2[r3] = r0;
        r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r2);
        goto L_0x11fc;
    L_0x0var_:
        r3 = 2;
        r0 = NUM; // 0x7f0e06c6 float:1.8878555E38 double:1.0531630133E-314;
        r2 = new java.lang.Object[r3];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0var_:
        r5 = r0.messageOwner;
        r9 = r5.media;
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r9 == 0) goto L_0x0fb8;
    L_0x0var_:
        if (r18 != 0) goto L_0x0fa4;
    L_0x0var_:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0fa4;
    L_0x0var_:
        r2 = r5.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0fa4;
    L_0x0var_:
        r2 = new java.lang.Object[r10];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r15);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r3 = 2;
        r2[r3] = r0;
        r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r2);
        goto L_0x11fc;
    L_0x0fa4:
        r3 = 2;
        r0 = NUM; // 0x7f0e06c7 float:1.8878557E38 double:1.053163014E-314;
        r2 = new java.lang.Object[r3];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupPhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x0fb8:
        r5 = r17.isVideo();
        if (r5 == 0) goto L_0x1008;
    L_0x0fbe:
        if (r18 != 0) goto L_0x0ff4;
    L_0x0fc0:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x0ff4;
    L_0x0fc6:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0ff4;
    L_0x0fd0:
        r2 = new java.lang.Object[r10];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r14);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r5 = 2;
        r2[r5] = r0;
        r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r2);
        goto L_0x11fc;
    L_0x0ff4:
        r5 = 2;
        r0 = NUM; // 0x7f0e06cd float:1.8878569E38 double:1.053163017E-314;
        r2 = new java.lang.Object[r5];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = " ";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x1008:
        r5 = 2;
        r9 = r17.isVoice();
        if (r9 == 0) goto L_0x1022;
    L_0x100f:
        r0 = NUM; // 0x7f0e06bc float:1.8878534E38 double:1.0531630084E-314;
        r2 = new java.lang.Object[r5];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupAudio";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x1022:
        r9 = r17.isRoundVideo();
        if (r9 == 0) goto L_0x103b;
    L_0x1028:
        r0 = NUM; // 0x7f0e06c9 float:1.887856E38 double:1.053163015E-314;
        r2 = new java.lang.Object[r5];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupRound";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x103b:
        r9 = r17.isMusic();
        if (r9 == 0) goto L_0x1054;
    L_0x1041:
        r0 = NUM; // 0x7f0e06c5 float:1.8878552E38 double:1.053163013E-314;
        r2 = new java.lang.Object[r5];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupMusic";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x1054:
        r5 = r0.messageOwner;
        r5 = r5.media;
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r9 == 0) goto L_0x107c;
    L_0x105c:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r5;
        r0 = NUM; // 0x7f0e06bd float:1.8878536E38 double:1.053163009E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = r5.first_name;
        r3 = r5.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r1, r3);
        r3 = 2;
        r2[r3] = r1;
        r1 = "NotificationMessageGroupContact2";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x107c:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r9 == 0) goto L_0x109c;
    L_0x1080:
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5;
        r0 = NUM; // 0x7f0e06c8 float:1.8878559E38 double:1.0531630143E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = r5.poll;
        r1 = r1.question;
        r3 = 2;
        r2[r3] = r1;
        r1 = "NotificationMessageGroupPoll2";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x109c:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r9 == 0) goto L_0x10ba;
    L_0x10a0:
        r0 = NUM; // 0x7f0e06bf float:1.887854E38 double:1.05316301E-314;
        r2 = new java.lang.Object[r10];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = r5.game;
        r1 = r1.title;
        r3 = 2;
        r2[r3] = r1;
        r1 = "NotificationMessageGroupGame";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x10ba:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r9 != 0) goto L_0x11b8;
    L_0x10be:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r9 == 0) goto L_0x10c4;
    L_0x10c2:
        goto L_0x11b8;
    L_0x10c4:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r9 == 0) goto L_0x10dc;
    L_0x10c8:
        r0 = NUM; // 0x7f0e06c3 float:1.8878548E38 double:1.053163012E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupLiveLocation";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x10dc:
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r5 == 0) goto L_0x11fb;
    L_0x10e0:
        r2 = r17.isSticker();
        if (r2 != 0) goto L_0x1188;
    L_0x10e6:
        r2 = r17.isAnimatedSticker();
        if (r2 == 0) goto L_0x10ee;
    L_0x10ec:
        goto L_0x1188;
    L_0x10ee:
        r2 = r17.isGif();
        if (r2 == 0) goto L_0x113e;
    L_0x10f4:
        if (r18 != 0) goto L_0x112a;
    L_0x10f6:
        r2 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r2 < r4) goto L_0x112a;
    L_0x10fc:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x112a;
    L_0x1106:
        r2 = new java.lang.Object[r10];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r3 = 2;
        r2[r3] = r0;
        r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r2);
        goto L_0x11fc;
    L_0x112a:
        r3 = 2;
        r0 = NUM; // 0x7f0e06c1 float:1.8878544E38 double:1.053163011E-314;
        r2 = new java.lang.Object[r3];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupGif";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x113e:
        if (r18 != 0) goto L_0x1174;
    L_0x1140:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x1174;
    L_0x1146:
        r2 = r0.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x1174;
    L_0x1150:
        r2 = new java.lang.Object[r10];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r4);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r3 = 2;
        r2[r3] = r0;
        r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r2);
        goto L_0x11fc;
    L_0x1174:
        r3 = 2;
        r0 = NUM; // 0x7f0e06be float:1.8878538E38 double:1.0531630094E-314;
        r2 = new java.lang.Object[r3];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupDocument";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x1188:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x11a4;
    L_0x118e:
        r2 = NUM; // 0x7f0e06cb float:1.8878565E38 double:1.053163016E-314;
        r3 = new java.lang.Object[r10];
        r3[r1] = r12;
        r1 = r13.title;
        r3[r6] = r1;
        r4 = 2;
        r3[r4] = r0;
        r0 = "NotificationMessageGroupStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x04d4;
    L_0x11a4:
        r4 = 2;
        r0 = NUM; // 0x7f0e06ca float:1.8878563E38 double:1.0531630153E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x04d4;
    L_0x11b8:
        r4 = 2;
        r0 = NUM; // 0x7f0e06c4 float:1.887855E38 double:1.0531630124E-314;
        r2 = new java.lang.Object[r4];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupMap";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x11cb:
        if (r20 == 0) goto L_0x11cf;
    L_0x11cd:
        r20[r1] = r1;
    L_0x11cf:
        r0 = org.telegram.messenger.ChatObject.isChannel(r13);
        if (r0 == 0) goto L_0x11e7;
    L_0x11d5:
        r0 = r13.megagroup;
        if (r0 != 0) goto L_0x11e7;
    L_0x11d9:
        r0 = NUM; // 0x7f0e024e float:1.8876234E38 double:1.053162448E-314;
        r2 = new java.lang.Object[r6];
        r2[r1] = r12;
        r1 = "ChannelMessageNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x11e7:
        r0 = NUM; // 0x7f0e06c6 float:1.8878555E38 double:1.0531630133E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r1] = r12;
        r1 = r13.title;
        r2[r6] = r1;
        r1 = "NotificationMessageGroupNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x11fc;
    L_0x11fa:
        r2 = 0;
    L_0x11fb:
        r11 = r2;
    L_0x11fc:
        return r11;
    L_0x11fd:
        r0 = NUM; // 0x7f0e0CLASSNAME float:1.8881317E38 double:1.0531636863E-314;
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

    /* JADX WARNING: Removed duplicated region for block: B:427:0x0910 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0906 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0917 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0aac A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a93 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0ae3 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0aca A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0848 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0988 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a93 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0aac A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0aca A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0ae3 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0848 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0988 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0aac A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a93 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0ae3 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0aca A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0848 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0988 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a93 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0aac A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0aca A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0ae3 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0848 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0988 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0aac A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a93 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0ae3 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0aca A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0848 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0988 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0a93 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0aac A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0aca A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0ae3 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x06b8 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x063f A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x07eb  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x07a1 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x063f A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x06b8 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x07a1 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x07eb  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x07f0  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x055f A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x052b A:{SYNTHETIC, Splitter:B:278:0x052b} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x056b A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0599 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0581 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04f8 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f5 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0513 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0512 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e2 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f5 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04f8 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0512 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0513 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c2 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03bd A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a4 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f4  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e2 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04f8 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f5 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0513 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0512 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03b6 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x033d A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03bd A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c2 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f4  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a4 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e2 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f5 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04f8 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0512 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0513 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02e3 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02ac A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02e9 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02e8 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f2 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02ee A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x031b  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030b  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x033d A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03b6 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c2 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03bd A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a4 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f4  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e2 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04f8 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f5 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0513 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0512 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x020b  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01bb A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0272 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02ac A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02e3 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02e8 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02e9 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02ee A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f2 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0302 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030b  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x031b  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03b6 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x033d A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03bd A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c2 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f4  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a4 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e2 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f5 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04f8 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0512 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0513 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01bb A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x020b  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0272 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02e3 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02ac A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02e9 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02e8 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f2 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02ee A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0302 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x031b  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030b  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x033d A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03b6 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c2 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03bd A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a4 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f4  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e2 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04f8 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f5 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0513 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0512 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x012b A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00fe A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0130 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x020b  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01bb A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0272 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02ac A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02e3 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02e8 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02e9 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02ee A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f2 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0302 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030b  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x031b  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03b6 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x033d A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03bd A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c2 A:{Catch:{ Exception -> 0x02de }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f4  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a4 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e2 A:{SKIP, Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f5 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04f8 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0512 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0513 A:{Catch:{ Exception -> 0x0aef }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:421:0x08f2 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing block: B:387:0x083f, code skipped:
            if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x0841;
     */
    private void showOrUpdateNotification(boolean r46) {
        /*
        r45 = this;
        r12 = r45;
        r13 = r46;
        r1 = "currentAccount";
        r2 = r45.getUserConfig();
        r2 = r2.isClientActivated();
        if (r2 == 0) goto L_0x0af5;
    L_0x0010:
        r2 = r12.pushMessages;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x0af5;
    L_0x0018:
        r2 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts;
        if (r2 != 0) goto L_0x0024;
    L_0x001c:
        r2 = r12.currentAccount;
        r3 = org.telegram.messenger.UserConfig.selectedAccount;
        if (r2 == r3) goto L_0x0024;
    L_0x0022:
        goto L_0x0af5;
    L_0x0024:
        r2 = r45.getConnectionsManager();	 Catch:{ Exception -> 0x0aef }
        r2.resumeNetworkMaybe();	 Catch:{ Exception -> 0x0aef }
        r2 = r12.pushMessages;	 Catch:{ Exception -> 0x0aef }
        r3 = 0;
        r2 = r2.get(r3);	 Catch:{ Exception -> 0x0aef }
        r2 = (org.telegram.messenger.MessageObject) r2;	 Catch:{ Exception -> 0x0aef }
        r4 = r45.getAccountInstance();	 Catch:{ Exception -> 0x0aef }
        r4 = r4.getNotificationsSettings();	 Catch:{ Exception -> 0x0aef }
        r5 = "dismissDate";
        r5 = r4.getInt(r5, r3);	 Catch:{ Exception -> 0x0aef }
        r6 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r6 = r6.date;	 Catch:{ Exception -> 0x0aef }
        if (r6 > r5) goto L_0x004c;
    L_0x0048:
        r45.dismissNotification();	 Catch:{ Exception -> 0x0aef }
        return;
    L_0x004c:
        r6 = r2.getDialogId();	 Catch:{ Exception -> 0x0aef }
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r8 = r8.mentioned;	 Catch:{ Exception -> 0x0aef }
        if (r8 == 0) goto L_0x005c;
    L_0x0056:
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r8 = r8.from_id;	 Catch:{ Exception -> 0x0aef }
        r8 = (long) r8;	 Catch:{ Exception -> 0x0aef }
        goto L_0x005d;
    L_0x005c:
        r8 = r6;
    L_0x005d:
        r2.getId();	 Catch:{ Exception -> 0x0aef }
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0aef }
        r10 = r10.chat_id;	 Catch:{ Exception -> 0x0aef }
        if (r10 == 0) goto L_0x006f;
    L_0x0068:
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0aef }
        r10 = r10.chat_id;	 Catch:{ Exception -> 0x0aef }
        goto L_0x0075;
    L_0x006f:
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0aef }
        r10 = r10.channel_id;	 Catch:{ Exception -> 0x0aef }
    L_0x0075:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r11 = r11.to_id;	 Catch:{ Exception -> 0x0aef }
        r11 = r11.user_id;	 Catch:{ Exception -> 0x0aef }
        if (r11 != 0) goto L_0x0082;
    L_0x007d:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r11 = r11.from_id;	 Catch:{ Exception -> 0x0aef }
        goto L_0x0090;
    L_0x0082:
        r14 = r45.getUserConfig();	 Catch:{ Exception -> 0x0aef }
        r14 = r14.getClientUserId();	 Catch:{ Exception -> 0x0aef }
        if (r11 != r14) goto L_0x0090;
    L_0x008c:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r11 = r11.from_id;	 Catch:{ Exception -> 0x0aef }
    L_0x0090:
        r14 = r45.getMessagesController();	 Catch:{ Exception -> 0x0aef }
        r15 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0aef }
        r14 = r14.getUser(r15);	 Catch:{ Exception -> 0x0aef }
        if (r10 == 0) goto L_0x00ba;
    L_0x009e:
        r15 = r45.getMessagesController();	 Catch:{ Exception -> 0x0aef }
        r3 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0aef }
        r15 = r15.getChat(r3);	 Catch:{ Exception -> 0x0aef }
        r3 = org.telegram.messenger.ChatObject.isChannel(r15);	 Catch:{ Exception -> 0x0aef }
        if (r3 == 0) goto L_0x00b6;
    L_0x00b0:
        r3 = r15.megagroup;	 Catch:{ Exception -> 0x0aef }
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
        r5 = r12.getNotifyOverride(r4, r8);	 Catch:{ Exception -> 0x0aef }
        r20 = r2;
        r2 = -1;
        r21 = r1;
        r1 = 2;
        if (r5 != r2) goto L_0x00cf;
    L_0x00ca:
        r2 = r12.isGlobalNotificationsEnabled(r6);	 Catch:{ Exception -> 0x0aef }
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
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r5.<init>();	 Catch:{ Exception -> 0x0aef }
        r8 = "custom_";
        r5.append(r8);	 Catch:{ Exception -> 0x0aef }
        r5.append(r6);	 Catch:{ Exception -> 0x0aef }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0aef }
        r8 = 0;
        r5 = r4.getBoolean(r5, r8);	 Catch:{ Exception -> 0x0aef }
        if (r5 == 0) goto L_0x012b;
    L_0x00fe:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r5.<init>();	 Catch:{ Exception -> 0x0aef }
        r8 = "smart_max_count_";
        r5.append(r8);	 Catch:{ Exception -> 0x0aef }
        r5.append(r6);	 Catch:{ Exception -> 0x0aef }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0aef }
        r5 = r4.getInt(r5, r1);	 Catch:{ Exception -> 0x0aef }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r8.<init>();	 Catch:{ Exception -> 0x0aef }
        r9 = "smart_delay_";
        r8.append(r9);	 Catch:{ Exception -> 0x0aef }
        r8.append(r6);	 Catch:{ Exception -> 0x0aef }
        r8 = r8.toString();	 Catch:{ Exception -> 0x0aef }
        r9 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r8 = r4.getInt(r8, r9);	 Catch:{ Exception -> 0x0aef }
        goto L_0x012e;
    L_0x012b:
        r8 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r5 = 2;
    L_0x012e:
        if (r5 == 0) goto L_0x017d;
    L_0x0130:
        r9 = r12.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0aef }
        r9 = r9.get(r6);	 Catch:{ Exception -> 0x0aef }
        r9 = (android.graphics.Point) r9;	 Catch:{ Exception -> 0x0aef }
        if (r9 != 0) goto L_0x014d;
    L_0x013a:
        r5 = new android.graphics.Point;	 Catch:{ Exception -> 0x0aef }
        r8 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0aef }
        r8 = r8 / r22;
        r9 = (int) r8;	 Catch:{ Exception -> 0x0aef }
        r8 = 1;
        r5.<init>(r8, r9);	 Catch:{ Exception -> 0x0aef }
        r8 = r12.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0aef }
        r8.put(r6, r5);	 Catch:{ Exception -> 0x0aef }
        goto L_0x017d;
    L_0x014d:
        r1 = r9.y;	 Catch:{ Exception -> 0x0aef }
        r1 = r1 + r8;
        r8 = r2;
        r1 = (long) r1;	 Catch:{ Exception -> 0x0aef }
        r24 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0aef }
        r24 = r24 / r22;
        r26 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1));
        if (r26 >= 0) goto L_0x0168;
    L_0x015c:
        r1 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0aef }
        r1 = r1 / r22;
        r2 = (int) r1;	 Catch:{ Exception -> 0x0aef }
        r1 = 1;
        r9.set(r1, r2);	 Catch:{ Exception -> 0x0aef }
        goto L_0x017e;
    L_0x0168:
        r1 = r9.x;	 Catch:{ Exception -> 0x0aef }
        if (r1 >= r5) goto L_0x017a;
    L_0x016c:
        r2 = 1;
        r1 = r1 + r2;
        r24 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0aef }
        r2 = r14;
        r13 = r24 / r22;
        r5 = (int) r13;	 Catch:{ Exception -> 0x0aef }
        r9.set(r1, r5);	 Catch:{ Exception -> 0x0aef }
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
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0aef }
        r1 = r1.getPath();	 Catch:{ Exception -> 0x0aef }
        r5 = "EnableInAppSounds";
        r9 = 1;
        r5 = r4.getBoolean(r5, r9);	 Catch:{ Exception -> 0x0aef }
        r13 = "EnableInAppVibrate";
        r13 = r4.getBoolean(r13, r9);	 Catch:{ Exception -> 0x0aef }
        r14 = "EnableInAppPreview";
        r14 = r4.getBoolean(r14, r9);	 Catch:{ Exception -> 0x0aef }
        r9 = "EnableInAppPriority";
        r24 = r14;
        r14 = 0;
        r9 = r4.getBoolean(r9, r14);	 Catch:{ Exception -> 0x0aef }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r14.<init>();	 Catch:{ Exception -> 0x0aef }
        r25 = r2;
        r2 = "custom_";
        r14.append(r2);	 Catch:{ Exception -> 0x0aef }
        r14.append(r6);	 Catch:{ Exception -> 0x0aef }
        r2 = r14.toString();	 Catch:{ Exception -> 0x0aef }
        r14 = 0;
        r2 = r4.getBoolean(r2, r14);	 Catch:{ Exception -> 0x0aef }
        if (r2 == 0) goto L_0x020b;
    L_0x01bb:
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r14.<init>();	 Catch:{ Exception -> 0x0aef }
        r27 = r15;
        r15 = "vibrate_";
        r14.append(r15);	 Catch:{ Exception -> 0x0aef }
        r14.append(r6);	 Catch:{ Exception -> 0x0aef }
        r14 = r14.toString();	 Catch:{ Exception -> 0x0aef }
        r15 = 0;
        r14 = r4.getInt(r14, r15);	 Catch:{ Exception -> 0x0aef }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r15.<init>();	 Catch:{ Exception -> 0x0aef }
        r28 = r14;
        r14 = "priority_";
        r15.append(r14);	 Catch:{ Exception -> 0x0aef }
        r15.append(r6);	 Catch:{ Exception -> 0x0aef }
        r14 = r15.toString();	 Catch:{ Exception -> 0x0aef }
        r15 = 3;
        r14 = r4.getInt(r14, r15);	 Catch:{ Exception -> 0x0aef }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r15.<init>();	 Catch:{ Exception -> 0x0aef }
        r29 = r14;
        r14 = "sound_path_";
        r15.append(r14);	 Catch:{ Exception -> 0x0aef }
        r15.append(r6);	 Catch:{ Exception -> 0x0aef }
        r14 = r15.toString();	 Catch:{ Exception -> 0x0aef }
        r15 = 0;
        r14 = r4.getString(r14, r15);	 Catch:{ Exception -> 0x0aef }
        r15 = r14;
        r14 = r28;
        r12 = r29;
        r28 = r8;
        goto L_0x0212;
    L_0x020b:
        r27 = r15;
        r28 = r8;
        r12 = 3;
        r14 = 0;
        r15 = 0;
    L_0x0212:
        if (r10 == 0) goto L_0x0272;
    L_0x0214:
        if (r3 == 0) goto L_0x0244;
    L_0x0216:
        if (r15 == 0) goto L_0x0220;
    L_0x0218:
        r3 = r15.equals(r1);	 Catch:{ Exception -> 0x02de }
        if (r3 == 0) goto L_0x0220;
    L_0x021e:
        r15 = 0;
        goto L_0x0228;
    L_0x0220:
        if (r15 != 0) goto L_0x0228;
    L_0x0222:
        r3 = "ChannelSoundPath";
        r15 = r4.getString(r3, r1);	 Catch:{ Exception -> 0x02de }
    L_0x0228:
        r3 = "vibrate_channel";
        r8 = 0;
        r3 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02de }
        r8 = "priority_channel";
        r30 = r3;
        r3 = 1;
        r8 = r4.getInt(r8, r3);	 Catch:{ Exception -> 0x02de }
        r3 = "ChannelLed";
        r31 = r8;
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r8 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02de }
        goto L_0x02a1;
    L_0x0244:
        if (r15 == 0) goto L_0x024e;
    L_0x0246:
        r3 = r15.equals(r1);	 Catch:{ Exception -> 0x02de }
        if (r3 == 0) goto L_0x024e;
    L_0x024c:
        r15 = 0;
        goto L_0x0256;
    L_0x024e:
        if (r15 != 0) goto L_0x0256;
    L_0x0250:
        r3 = "GroupSoundPath";
        r15 = r4.getString(r3, r1);	 Catch:{ Exception -> 0x02de }
    L_0x0256:
        r3 = "vibrate_group";
        r8 = 0;
        r3 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02de }
        r8 = "priority_group";
        r30 = r3;
        r3 = 1;
        r8 = r4.getInt(r8, r3);	 Catch:{ Exception -> 0x02de }
        r3 = "GroupLed";
        r31 = r8;
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r8 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02de }
        goto L_0x02a1;
    L_0x0272:
        if (r11 == 0) goto L_0x02a4;
    L_0x0274:
        if (r15 == 0) goto L_0x027e;
    L_0x0276:
        r3 = r15.equals(r1);	 Catch:{ Exception -> 0x02de }
        if (r3 == 0) goto L_0x027e;
    L_0x027c:
        r15 = 0;
        goto L_0x0286;
    L_0x027e:
        if (r15 != 0) goto L_0x0286;
    L_0x0280:
        r3 = "GlobalSoundPath";
        r15 = r4.getString(r3, r1);	 Catch:{ Exception -> 0x02de }
    L_0x0286:
        r3 = "vibrate_messages";
        r8 = 0;
        r3 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02de }
        r8 = "priority_messages";
        r30 = r3;
        r3 = 1;
        r8 = r4.getInt(r8, r3);	 Catch:{ Exception -> 0x02de }
        r3 = "MessagesLed";
        r31 = r8;
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r8 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02de }
    L_0x02a1:
        r3 = r30;
        goto L_0x02aa;
    L_0x02a4:
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r3 = 0;
        r31 = 0;
    L_0x02aa:
        if (r2 == 0) goto L_0x02e3;
    L_0x02ac:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02de }
        r2.<init>();	 Catch:{ Exception -> 0x02de }
        r29 = r8;
        r8 = "color_";
        r2.append(r8);	 Catch:{ Exception -> 0x02de }
        r2.append(r6);	 Catch:{ Exception -> 0x02de }
        r2 = r2.toString();	 Catch:{ Exception -> 0x02de }
        r2 = r4.contains(r2);	 Catch:{ Exception -> 0x02de }
        if (r2 == 0) goto L_0x02e5;
    L_0x02c5:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02de }
        r2.<init>();	 Catch:{ Exception -> 0x02de }
        r8 = "color_";
        r2.append(r8);	 Catch:{ Exception -> 0x02de }
        r2.append(r6);	 Catch:{ Exception -> 0x02de }
        r2 = r2.toString();	 Catch:{ Exception -> 0x02de }
        r8 = 0;
        r2 = r4.getInt(r2, r8);	 Catch:{ Exception -> 0x02de }
        r29 = r2;
        goto L_0x02e5;
    L_0x02de:
        r0 = move-exception;
        r12 = r45;
        goto L_0x0af0;
    L_0x02e3:
        r29 = r8;
    L_0x02e5:
        r2 = 3;
        if (r12 == r2) goto L_0x02e9;
    L_0x02e8:
        goto L_0x02eb;
    L_0x02e9:
        r12 = r31;
    L_0x02eb:
        r4 = 4;
        if (r3 != r4) goto L_0x02f2;
    L_0x02ee:
        r3 = 0;
        r4 = 2;
        r8 = 1;
        goto L_0x02f4;
    L_0x02f2:
        r4 = 2;
        r8 = 0;
    L_0x02f4:
        if (r3 != r4) goto L_0x02fb;
    L_0x02f6:
        r4 = 1;
        if (r14 == r4) goto L_0x0307;
    L_0x02f9:
        if (r14 == r2) goto L_0x0307;
    L_0x02fb:
        r2 = 2;
        if (r3 == r2) goto L_0x0300;
    L_0x02fe:
        if (r14 == r2) goto L_0x0307;
    L_0x0300:
        if (r14 == 0) goto L_0x0306;
    L_0x0302:
        r2 = 4;
        if (r14 == r2) goto L_0x0306;
    L_0x0305:
        goto L_0x0307;
    L_0x0306:
        r14 = r3;
    L_0x0307:
        r2 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x02de }
        if (r2 != 0) goto L_0x031b;
    L_0x030b:
        if (r5 != 0) goto L_0x030e;
    L_0x030d:
        r15 = 0;
    L_0x030e:
        if (r13 != 0) goto L_0x0311;
    L_0x0310:
        r14 = 2;
    L_0x0311:
        if (r9 != 0) goto L_0x0316;
    L_0x0313:
        r2 = 2;
        r3 = 0;
        goto L_0x031d;
    L_0x0316:
        r2 = 2;
        if (r12 != r2) goto L_0x031c;
    L_0x0319:
        r3 = 1;
        goto L_0x031d;
    L_0x031b:
        r2 = 2;
    L_0x031c:
        r3 = r12;
    L_0x031d:
        if (r8 == 0) goto L_0x0333;
    L_0x031f:
        if (r14 == r2) goto L_0x0333;
    L_0x0321:
        r2 = audioManager;	 Catch:{ Exception -> 0x032e }
        r2 = r2.getRingerMode();	 Catch:{ Exception -> 0x032e }
        if (r2 == 0) goto L_0x0333;
    L_0x0329:
        r4 = 1;
        if (r2 == r4) goto L_0x0333;
    L_0x032c:
        r14 = 2;
        goto L_0x0333;
    L_0x032e:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x02de }
    L_0x0333:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x02de }
        r4 = 100;
        r9 = 26;
        r12 = 0;
        if (r2 < r9) goto L_0x03b6;
    L_0x033d:
        r2 = 2;
        if (r14 != r2) goto L_0x034a;
    L_0x0340:
        r9 = new long[r2];	 Catch:{ Exception -> 0x02de }
        r2 = 0;
        r9[r2] = r12;	 Catch:{ Exception -> 0x02de }
        r2 = 1;
        r9[r2] = r12;	 Catch:{ Exception -> 0x02de }
        r8 = r9;
        goto L_0x0374;
    L_0x034a:
        r2 = 1;
        if (r14 != r2) goto L_0x035c;
    L_0x034d:
        r9 = 4;
        r8 = new long[r9];	 Catch:{ Exception -> 0x02de }
        r9 = 0;
        r8[r9] = r12;	 Catch:{ Exception -> 0x02de }
        r8[r2] = r4;	 Catch:{ Exception -> 0x02de }
        r2 = 2;
        r8[r2] = r12;	 Catch:{ Exception -> 0x02de }
        r2 = 3;
        r8[r2] = r4;	 Catch:{ Exception -> 0x02de }
        goto L_0x0374;
    L_0x035c:
        if (r14 == 0) goto L_0x0371;
    L_0x035e:
        r2 = 4;
        if (r14 != r2) goto L_0x0362;
    L_0x0361:
        goto L_0x0371;
    L_0x0362:
        r2 = 3;
        if (r14 != r2) goto L_0x036f;
    L_0x0365:
        r2 = 2;
        r8 = new long[r2];	 Catch:{ Exception -> 0x02de }
        r2 = 0;
        r8[r2] = r12;	 Catch:{ Exception -> 0x02de }
        r2 = 1;
        r8[r2] = r22;	 Catch:{ Exception -> 0x02de }
        goto L_0x0374;
    L_0x036f:
        r8 = 0;
        goto L_0x0374;
    L_0x0371:
        r2 = 0;
        r8 = new long[r2];	 Catch:{ Exception -> 0x02de }
    L_0x0374:
        if (r15 == 0) goto L_0x038c;
    L_0x0376:
        r2 = "NoSound";
        r2 = r15.equals(r2);	 Catch:{ Exception -> 0x02de }
        if (r2 != 0) goto L_0x038c;
    L_0x037e:
        r2 = r15.equals(r1);	 Catch:{ Exception -> 0x02de }
        if (r2 == 0) goto L_0x0387;
    L_0x0384:
        r2 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x02de }
        goto L_0x038d;
    L_0x0387:
        r2 = android.net.Uri.parse(r15);	 Catch:{ Exception -> 0x02de }
        goto L_0x038d;
    L_0x038c:
        r2 = 0;
    L_0x038d:
        if (r3 != 0) goto L_0x0395;
    L_0x038f:
        r32 = r2;
        r9 = r8;
        r33 = 3;
        goto L_0x03bb;
    L_0x0395:
        r9 = 1;
        if (r3 == r9) goto L_0x03b0;
    L_0x0398:
        r9 = 2;
        if (r3 != r9) goto L_0x039c;
    L_0x039b:
        goto L_0x03b0;
    L_0x039c:
        r9 = 4;
        if (r3 != r9) goto L_0x03a5;
    L_0x039f:
        r32 = r2;
        r9 = r8;
        r33 = 1;
        goto L_0x03bb;
    L_0x03a5:
        r9 = 5;
        r32 = r2;
        if (r3 != r9) goto L_0x03ae;
    L_0x03aa:
        r9 = r8;
        r33 = 2;
        goto L_0x03bb;
    L_0x03ae:
        r9 = r8;
        goto L_0x03b9;
    L_0x03b0:
        r32 = r2;
        r9 = r8;
        r33 = 4;
        goto L_0x03bb;
    L_0x03b6:
        r9 = 0;
        r32 = 0;
    L_0x03b9:
        r33 = 0;
    L_0x03bb:
        if (r28 == 0) goto L_0x03c2;
    L_0x03bd:
        r3 = 0;
        r8 = 0;
        r14 = 0;
        r15 = 0;
        goto L_0x03c4;
    L_0x03c2:
        r8 = r29;
    L_0x03c4:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x02de }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x02de }
        r5 = org.telegram.ui.LaunchActivity.class;
        r2.<init>(r4, r5);	 Catch:{ Exception -> 0x02de }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02de }
        r4.<init>();	 Catch:{ Exception -> 0x02de }
        r5 = "com.tmessages.openchat";
        r4.append(r5);	 Catch:{ Exception -> 0x02de }
        r12 = java.lang.Math.random();	 Catch:{ Exception -> 0x02de }
        r4.append(r12);	 Catch:{ Exception -> 0x02de }
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4.append(r5);	 Catch:{ Exception -> 0x02de }
        r4 = r4.toString();	 Catch:{ Exception -> 0x02de }
        r2.setAction(r4);	 Catch:{ Exception -> 0x02de }
        r4 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r2.setFlags(r4);	 Catch:{ Exception -> 0x02de }
        r4 = (int) r6;
        if (r4 == 0) goto L_0x04a4;
    L_0x03f4:
        r12 = r45;
        r5 = r12.pushDialogs;	 Catch:{ Exception -> 0x0aef }
        r5 = r5.size();	 Catch:{ Exception -> 0x0aef }
        r13 = 1;
        if (r5 != r13) goto L_0x040e;
    L_0x03ff:
        if (r10 == 0) goto L_0x0407;
    L_0x0401:
        r5 = "chatId";
        r2.putExtra(r5, r10);	 Catch:{ Exception -> 0x0aef }
        goto L_0x040e;
    L_0x0407:
        if (r11 == 0) goto L_0x040e;
    L_0x0409:
        r5 = "userId";
        r2.putExtra(r5, r11);	 Catch:{ Exception -> 0x0aef }
    L_0x040e:
        r5 = 0;
        r11 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r5);	 Catch:{ Exception -> 0x0aef }
        if (r11 != 0) goto L_0x0499;
    L_0x0415:
        r5 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0aef }
        if (r5 == 0) goto L_0x041b;
    L_0x0419:
        goto L_0x0499;
    L_0x041b:
        r5 = r12.pushDialogs;	 Catch:{ Exception -> 0x0aef }
        r5 = r5.size();	 Catch:{ Exception -> 0x0aef }
        r11 = 1;
        if (r5 != r11) goto L_0x0492;
    L_0x0424:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r11 = 28;
        if (r5 >= r11) goto L_0x0492;
    L_0x042a:
        if (r27 == 0) goto L_0x0462;
    L_0x042c:
        r5 = r27;
        r11 = r5.photo;	 Catch:{ Exception -> 0x0aef }
        if (r11 == 0) goto L_0x045b;
    L_0x0432:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0aef }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0aef }
        if (r11 == 0) goto L_0x045b;
    L_0x0438:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0aef }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0aef }
        r27 = r14;
        r13 = r11.volume_id;	 Catch:{ Exception -> 0x0aef }
        r34 = 0;
        r11 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1));
        if (r11 == 0) goto L_0x045d;
    L_0x0446:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0aef }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0aef }
        r11 = r11.local_id;	 Catch:{ Exception -> 0x0aef }
        if (r11 == 0) goto L_0x045d;
    L_0x044e:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0aef }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0aef }
        r29 = r8;
        r13 = r11;
        r11 = r25;
    L_0x0457:
        r25 = r9;
        goto L_0x04ce;
    L_0x045b:
        r27 = r14;
    L_0x045d:
        r29 = r8;
        r11 = r25;
        goto L_0x04a1;
    L_0x0462:
        r5 = r27;
        r27 = r14;
        if (r25 == 0) goto L_0x048f;
    L_0x0468:
        r11 = r25;
        r13 = r11.photo;	 Catch:{ Exception -> 0x0aef }
        if (r13 == 0) goto L_0x04ca;
    L_0x046e:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0aef }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0aef }
        if (r13 == 0) goto L_0x04ca;
    L_0x0474:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0aef }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0aef }
        r13 = r13.volume_id;	 Catch:{ Exception -> 0x0aef }
        r34 = 0;
        r25 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1));
        if (r25 == 0) goto L_0x04ca;
    L_0x0480:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0aef }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0aef }
        r13 = r13.local_id;	 Catch:{ Exception -> 0x0aef }
        if (r13 == 0) goto L_0x04ca;
    L_0x0488:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0aef }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0aef }
        r29 = r8;
        goto L_0x0457;
    L_0x048f:
        r11 = r25;
        goto L_0x04ca;
    L_0x0492:
        r11 = r25;
        r5 = r27;
        r27 = r14;
        goto L_0x04ca;
    L_0x0499:
        r11 = r25;
        r5 = r27;
        r27 = r14;
        r29 = r8;
    L_0x04a1:
        r25 = r9;
        goto L_0x04cd;
    L_0x04a4:
        r12 = r45;
        r11 = r25;
        r5 = r27;
        r27 = r14;
        r13 = r12.pushDialogs;	 Catch:{ Exception -> 0x0aef }
        r13 = r13.size();	 Catch:{ Exception -> 0x0aef }
        r14 = 1;
        if (r13 != r14) goto L_0x04ca;
    L_0x04b5:
        r13 = globalSecretChatId;	 Catch:{ Exception -> 0x0aef }
        r25 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1));
        if (r25 == 0) goto L_0x04ca;
    L_0x04bb:
        r13 = "encId";
        r14 = 32;
        r29 = r8;
        r25 = r9;
        r8 = r6 >> r14;
        r9 = (int) r8;	 Catch:{ Exception -> 0x0aef }
        r2.putExtra(r13, r9);	 Catch:{ Exception -> 0x0aef }
        goto L_0x04cd;
    L_0x04ca:
        r29 = r8;
        goto L_0x04a1;
    L_0x04cd:
        r13 = 0;
    L_0x04ce:
        r8 = r12.currentAccount;	 Catch:{ Exception -> 0x0aef }
        r9 = r21;
        r2.putExtra(r9, r8);	 Catch:{ Exception -> 0x0aef }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0aef }
        r14 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r36 = r6;
        r6 = 0;
        r2 = android.app.PendingIntent.getActivity(r8, r6, r2, r14);	 Catch:{ Exception -> 0x0aef }
        if (r10 == 0) goto L_0x04e4;
    L_0x04e2:
        if (r5 == 0) goto L_0x04e6;
    L_0x04e4:
        if (r11 != 0) goto L_0x04f1;
    L_0x04e6:
        r6 = r20.isFcmMessage();	 Catch:{ Exception -> 0x0aef }
        if (r6 == 0) goto L_0x04f1;
    L_0x04ec:
        r6 = r20;
        r7 = r6.localName;	 Catch:{ Exception -> 0x0aef }
        goto L_0x04fc;
    L_0x04f1:
        r6 = r20;
        if (r5 == 0) goto L_0x04f8;
    L_0x04f5:
        r7 = r5.title;	 Catch:{ Exception -> 0x0aef }
        goto L_0x04fc;
    L_0x04f8:
        r7 = org.telegram.messenger.UserObject.getUserName(r11);	 Catch:{ Exception -> 0x0aef }
    L_0x04fc:
        if (r4 == 0) goto L_0x0516;
    L_0x04fe:
        r4 = r12.pushDialogs;	 Catch:{ Exception -> 0x0aef }
        r4 = r4.size();	 Catch:{ Exception -> 0x0aef }
        r8 = 1;
        if (r4 > r8) goto L_0x0516;
    L_0x0507:
        r4 = 0;
        r8 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r4);	 Catch:{ Exception -> 0x0aef }
        if (r8 != 0) goto L_0x0516;
    L_0x050e:
        r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0aef }
        if (r4 == 0) goto L_0x0513;
    L_0x0512:
        goto L_0x0516;
    L_0x0513:
        r4 = r7;
        r8 = 1;
        goto L_0x0520;
    L_0x0516:
        r4 = "AppName";
        r8 = NUM; // 0x7f0e00f1 float:1.8875526E38 double:1.0531622757E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r8);	 Catch:{ Exception -> 0x0aef }
        r8 = 0;
    L_0x0520:
        r10 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();	 Catch:{ Exception -> 0x0aef }
        r14 = "";
        r20 = r7;
        r7 = 1;
        if (r10 <= r7) goto L_0x055f;
    L_0x052b:
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0aef }
        r10 = r10.size();	 Catch:{ Exception -> 0x0aef }
        if (r10 != r7) goto L_0x0540;
    L_0x0533:
        r7 = r45.getUserConfig();	 Catch:{ Exception -> 0x0aef }
        r7 = r7.getCurrentUser();	 Catch:{ Exception -> 0x0aef }
        r7 = org.telegram.messenger.UserObject.getFirstName(r7);	 Catch:{ Exception -> 0x0aef }
        goto L_0x0560;
    L_0x0540:
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r7.<init>();	 Catch:{ Exception -> 0x0aef }
        r10 = r45.getUserConfig();	 Catch:{ Exception -> 0x0aef }
        r10 = r10.getCurrentUser();	 Catch:{ Exception -> 0x0aef }
        r10 = org.telegram.messenger.UserObject.getFirstName(r10);	 Catch:{ Exception -> 0x0aef }
        r7.append(r10);	 Catch:{ Exception -> 0x0aef }
        r10 = "・";
        r7.append(r10);	 Catch:{ Exception -> 0x0aef }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0aef }
        goto L_0x0560;
    L_0x055f:
        r7 = r14;
    L_0x0560:
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0aef }
        r10 = r10.size();	 Catch:{ Exception -> 0x0aef }
        r21 = r1;
        r1 = 1;
        if (r10 != r1) goto L_0x0578;
    L_0x056b:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r10 = 23;
        if (r1 >= r10) goto L_0x0572;
    L_0x0571:
        goto L_0x0578;
    L_0x0572:
        r40 = r3;
        r39 = r15;
    L_0x0576:
        r15 = r7;
        goto L_0x05d3;
    L_0x0578:
        r1 = r12.pushDialogs;	 Catch:{ Exception -> 0x0aef }
        r1 = r1.size();	 Catch:{ Exception -> 0x0aef }
        r10 = 1;
        if (r1 != r10) goto L_0x0599;
    L_0x0581:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r1.<init>();	 Catch:{ Exception -> 0x0aef }
        r1.append(r7);	 Catch:{ Exception -> 0x0aef }
        r7 = "NewMessages";
        r10 = r12.total_unread_count;	 Catch:{ Exception -> 0x0aef }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r10);	 Catch:{ Exception -> 0x0aef }
        r1.append(r7);	 Catch:{ Exception -> 0x0aef }
        r7 = r1.toString();	 Catch:{ Exception -> 0x0aef }
        goto L_0x0572;
    L_0x0599:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r1.<init>();	 Catch:{ Exception -> 0x0aef }
        r1.append(r7);	 Catch:{ Exception -> 0x0aef }
        r7 = "NotificationMessagesPeopleDisplayOrder";
        r39 = r15;
        r10 = 2;
        r15 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0aef }
        r10 = "NewMessages";
        r40 = r3;
        r3 = r12.total_unread_count;	 Catch:{ Exception -> 0x0aef }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r10, r3);	 Catch:{ Exception -> 0x0aef }
        r10 = 0;
        r15[r10] = r3;	 Catch:{ Exception -> 0x0aef }
        r3 = "FromChats";
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0aef }
        r10 = r10.size();	 Catch:{ Exception -> 0x0aef }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r10);	 Catch:{ Exception -> 0x0aef }
        r10 = 1;
        r15[r10] = r3;	 Catch:{ Exception -> 0x0aef }
        r3 = NUM; // 0x7f0e06de float:1.8878603E38 double:1.053163025E-314;
        r3 = org.telegram.messenger.LocaleController.formatString(r7, r3, r15);	 Catch:{ Exception -> 0x0aef }
        r1.append(r3);	 Catch:{ Exception -> 0x0aef }
        r7 = r1.toString();	 Catch:{ Exception -> 0x0aef }
        goto L_0x0576;
    L_0x05d3:
        r10 = new androidx.core.app.NotificationCompat$Builder;	 Catch:{ Exception -> 0x0aef }
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0aef }
        r10.<init>(r1);	 Catch:{ Exception -> 0x0aef }
        r10.setContentTitle(r4);	 Catch:{ Exception -> 0x0aef }
        r1 = NUM; // 0x7var_b float:1.7945639E38 double:1.0529357614E-314;
        r10.setSmallIcon(r1);	 Catch:{ Exception -> 0x0aef }
        r1 = 1;
        r10.setAutoCancel(r1);	 Catch:{ Exception -> 0x0aef }
        r1 = r12.total_unread_count;	 Catch:{ Exception -> 0x0aef }
        r10.setNumber(r1);	 Catch:{ Exception -> 0x0aef }
        r10.setContentIntent(r2);	 Catch:{ Exception -> 0x0aef }
        r1 = r12.notificationGroup;	 Catch:{ Exception -> 0x0aef }
        r10.setGroup(r1);	 Catch:{ Exception -> 0x0aef }
        r1 = 1;
        r10.setGroupSummary(r1);	 Catch:{ Exception -> 0x0aef }
        r10.setShowWhen(r1);	 Catch:{ Exception -> 0x0aef }
        r1 = r6.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r1 = r1.date;	 Catch:{ Exception -> 0x0aef }
        r1 = (long) r1;	 Catch:{ Exception -> 0x0aef }
        r1 = r1 * r22;
        r10.setWhen(r1);	 Catch:{ Exception -> 0x0aef }
        r1 = -15618822; // 0xfffffffffvar_acfa float:-1.936362E38 double:NaN;
        r10.setColor(r1);	 Catch:{ Exception -> 0x0aef }
        r1 = "msg";
        r10.setCategory(r1);	 Catch:{ Exception -> 0x0aef }
        if (r5 != 0) goto L_0x0636;
    L_0x0612:
        if (r11 == 0) goto L_0x0636;
    L_0x0614:
        r1 = r11.phone;	 Catch:{ Exception -> 0x0aef }
        if (r1 == 0) goto L_0x0636;
    L_0x0618:
        r1 = r11.phone;	 Catch:{ Exception -> 0x0aef }
        r1 = r1.length();	 Catch:{ Exception -> 0x0aef }
        if (r1 <= 0) goto L_0x0636;
    L_0x0620:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r1.<init>();	 Catch:{ Exception -> 0x0aef }
        r2 = "tel:+";
        r1.append(r2);	 Catch:{ Exception -> 0x0aef }
        r2 = r11.phone;	 Catch:{ Exception -> 0x0aef }
        r1.append(r2);	 Catch:{ Exception -> 0x0aef }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0aef }
        r10.addPerson(r1);	 Catch:{ Exception -> 0x0aef }
    L_0x0636:
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0aef }
        r1 = r1.size();	 Catch:{ Exception -> 0x0aef }
        r2 = 1;
        if (r1 != r2) goto L_0x06b8;
    L_0x063f:
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0aef }
        r3 = 0;
        r1 = r1.get(r3);	 Catch:{ Exception -> 0x0aef }
        r1 = (org.telegram.messenger.MessageObject) r1;	 Catch:{ Exception -> 0x0aef }
        r7 = new boolean[r2];	 Catch:{ Exception -> 0x0aef }
        r2 = 0;
        r11 = r12.getStringForMessage(r1, r3, r7, r2);	 Catch:{ Exception -> 0x0aef }
        r1 = r1.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r1 = r1.silent;	 Catch:{ Exception -> 0x0aef }
        if (r11 != 0) goto L_0x0656;
    L_0x0655:
        return;
    L_0x0656:
        if (r8 == 0) goto L_0x06a1;
    L_0x0658:
        if (r5 == 0) goto L_0x0670;
    L_0x065a:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r2.<init>();	 Catch:{ Exception -> 0x0aef }
        r3 = " @ ";
        r2.append(r3);	 Catch:{ Exception -> 0x0aef }
        r2.append(r4);	 Catch:{ Exception -> 0x0aef }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0aef }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0aef }
        goto L_0x06a2;
    L_0x0670:
        r2 = 0;
        r3 = r7[r2];	 Catch:{ Exception -> 0x0aef }
        if (r3 == 0) goto L_0x068b;
    L_0x0675:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r2.<init>();	 Catch:{ Exception -> 0x0aef }
        r2.append(r4);	 Catch:{ Exception -> 0x0aef }
        r3 = ": ";
        r2.append(r3);	 Catch:{ Exception -> 0x0aef }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0aef }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0aef }
        goto L_0x06a2;
    L_0x068b:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r2.<init>();	 Catch:{ Exception -> 0x0aef }
        r2.append(r4);	 Catch:{ Exception -> 0x0aef }
        r3 = " ";
        r2.append(r3);	 Catch:{ Exception -> 0x0aef }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0aef }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0aef }
        goto L_0x06a2;
    L_0x06a1:
        r2 = r11;
    L_0x06a2:
        r10.setContentText(r2);	 Catch:{ Exception -> 0x0aef }
        r3 = new androidx.core.app.NotificationCompat$BigTextStyle;	 Catch:{ Exception -> 0x0aef }
        r3.<init>();	 Catch:{ Exception -> 0x0aef }
        r3.bigText(r2);	 Catch:{ Exception -> 0x0aef }
        r10.setStyle(r3);	 Catch:{ Exception -> 0x0aef }
        r44 = r6;
        r43 = r9;
        r42 = r13;
        goto L_0x0778;
    L_0x06b8:
        r10.setContentText(r15);	 Catch:{ Exception -> 0x0aef }
        r1 = new androidx.core.app.NotificationCompat$InboxStyle;	 Catch:{ Exception -> 0x0aef }
        r1.<init>();	 Catch:{ Exception -> 0x0aef }
        r1.setBigContentTitle(r4);	 Catch:{ Exception -> 0x0aef }
        r2 = 10;
        r3 = r12.pushMessages;	 Catch:{ Exception -> 0x0aef }
        r3 = r3.size();	 Catch:{ Exception -> 0x0aef }
        r2 = java.lang.Math.min(r2, r3);	 Catch:{ Exception -> 0x0aef }
        r3 = 1;
        r7 = new boolean[r3];	 Catch:{ Exception -> 0x0aef }
        r3 = 0;
        r11 = 2;
        r38 = 0;
    L_0x06d6:
        if (r3 >= r2) goto L_0x0769;
    L_0x06d8:
        r41 = r2;
        r2 = r12.pushMessages;	 Catch:{ Exception -> 0x0aef }
        r2 = r2.get(r3);	 Catch:{ Exception -> 0x0aef }
        r2 = (org.telegram.messenger.MessageObject) r2;	 Catch:{ Exception -> 0x0aef }
        r44 = r6;
        r43 = r9;
        r42 = r13;
        r9 = 0;
        r13 = 0;
        r6 = r12.getStringForMessage(r2, r9, r7, r13);	 Catch:{ Exception -> 0x0aef }
        if (r6 == 0) goto L_0x0759;
    L_0x06f0:
        r9 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r9 = r9.date;	 Catch:{ Exception -> 0x0aef }
        r13 = r19;
        if (r9 > r13) goto L_0x06f9;
    L_0x06f8:
        goto L_0x075b;
    L_0x06f9:
        r9 = 2;
        if (r11 != r9) goto L_0x0702;
    L_0x06fc:
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r11 = r2.silent;	 Catch:{ Exception -> 0x0aef }
        r38 = r6;
    L_0x0702:
        r2 = r12.pushDialogs;	 Catch:{ Exception -> 0x0aef }
        r2 = r2.size();	 Catch:{ Exception -> 0x0aef }
        r9 = 1;
        if (r2 != r9) goto L_0x0755;
    L_0x070b:
        if (r8 == 0) goto L_0x0755;
    L_0x070d:
        if (r5 == 0) goto L_0x0725;
    L_0x070f:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r2.<init>();	 Catch:{ Exception -> 0x0aef }
        r9 = " @ ";
        r2.append(r9);	 Catch:{ Exception -> 0x0aef }
        r2.append(r4);	 Catch:{ Exception -> 0x0aef }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0aef }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0aef }
        goto L_0x0755;
    L_0x0725:
        r2 = 0;
        r9 = r7[r2];	 Catch:{ Exception -> 0x0aef }
        if (r9 == 0) goto L_0x0740;
    L_0x072a:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r2.<init>();	 Catch:{ Exception -> 0x0aef }
        r2.append(r4);	 Catch:{ Exception -> 0x0aef }
        r9 = ": ";
        r2.append(r9);	 Catch:{ Exception -> 0x0aef }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0aef }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0aef }
        goto L_0x0755;
    L_0x0740:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r2.<init>();	 Catch:{ Exception -> 0x0aef }
        r2.append(r4);	 Catch:{ Exception -> 0x0aef }
        r9 = " ";
        r2.append(r9);	 Catch:{ Exception -> 0x0aef }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0aef }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0aef }
    L_0x0755:
        r1.addLine(r6);	 Catch:{ Exception -> 0x0aef }
        goto L_0x075b;
    L_0x0759:
        r13 = r19;
    L_0x075b:
        r3 = r3 + 1;
        r19 = r13;
        r2 = r41;
        r13 = r42;
        r9 = r43;
        r6 = r44;
        goto L_0x06d6;
    L_0x0769:
        r44 = r6;
        r43 = r9;
        r42 = r13;
        r1.setSummaryText(r15);	 Catch:{ Exception -> 0x0aef }
        r10.setStyle(r1);	 Catch:{ Exception -> 0x0aef }
        r1 = r11;
        r11 = r38;
    L_0x0778:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x0aef }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0aef }
        r4 = org.telegram.messenger.NotificationDismissReceiver.class;
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x0aef }
        r3 = "messageDate";
        r4 = r44;
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r5 = r5.date;	 Catch:{ Exception -> 0x0aef }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0aef }
        r3 = r12.currentAccount;	 Catch:{ Exception -> 0x0aef }
        r5 = r43;
        r2.putExtra(r5, r3);	 Catch:{ Exception -> 0x0aef }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0aef }
        r6 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r7 = 1;
        r2 = android.app.PendingIntent.getBroadcast(r3, r7, r2, r6);	 Catch:{ Exception -> 0x0aef }
        r10.setDeleteIntent(r2);	 Catch:{ Exception -> 0x0aef }
        if (r42 == 0) goto L_0x07eb;
    L_0x07a1:
        r2 = org.telegram.messenger.ImageLoader.getInstance();	 Catch:{ Exception -> 0x0aef }
        r3 = "50_50";
        r13 = r42;
        r7 = 0;
        r2 = r2.getImageFromMemory(r13, r7, r3);	 Catch:{ Exception -> 0x0aef }
        if (r2 == 0) goto L_0x07b8;
    L_0x07b0:
        r2 = r2.getBitmap();	 Catch:{ Exception -> 0x0aef }
        r10.setLargeIcon(r2);	 Catch:{ Exception -> 0x0aef }
        goto L_0x07ec;
    L_0x07b8:
        r2 = 1;
        r3 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r2);	 Catch:{ all -> 0x07ec }
        r2 = r3.exists();	 Catch:{ all -> 0x07ec }
        if (r2 == 0) goto L_0x07ec;
    L_0x07c3:
        r2 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r8 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ all -> 0x07ec }
        r8 = (float) r8;	 Catch:{ all -> 0x07ec }
        r2 = r2 / r8;
        r8 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x07ec }
        r8.<init>();	 Catch:{ all -> 0x07ec }
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r9 >= 0) goto L_0x07da;
    L_0x07d8:
        r2 = 1;
        goto L_0x07db;
    L_0x07da:
        r2 = (int) r2;	 Catch:{ all -> 0x07ec }
    L_0x07db:
        r8.inSampleSize = r2;	 Catch:{ all -> 0x07ec }
        r2 = r3.getAbsolutePath();	 Catch:{ all -> 0x07ec }
        r2 = android.graphics.BitmapFactory.decodeFile(r2, r8);	 Catch:{ all -> 0x07ec }
        if (r2 == 0) goto L_0x07ec;
    L_0x07e7:
        r10.setLargeIcon(r2);	 Catch:{ all -> 0x07ec }
        goto L_0x07ec;
    L_0x07eb:
        r7 = 0;
    L_0x07ec:
        r13 = r46;
        if (r13 == 0) goto L_0x0837;
    L_0x07f0:
        r2 = 1;
        if (r1 != r2) goto L_0x07f4;
    L_0x07f3:
        goto L_0x0837;
    L_0x07f4:
        if (r40 != 0) goto L_0x0803;
    L_0x07f6:
        r2 = 0;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0aef }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r3 = 26;
        if (r2 < r3) goto L_0x0844;
    L_0x0800:
        r2 = 1;
        r8 = 3;
        goto L_0x0846;
    L_0x0803:
        r3 = r40;
        r2 = 1;
        if (r3 == r2) goto L_0x082b;
    L_0x0808:
        r2 = 2;
        if (r3 != r2) goto L_0x080d;
    L_0x080b:
        r2 = 1;
        goto L_0x082b;
    L_0x080d:
        r2 = 4;
        if (r3 != r2) goto L_0x081d;
    L_0x0810:
        r2 = -2;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0aef }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r3 = 26;
        if (r2 < r3) goto L_0x0844;
    L_0x081a:
        r2 = 1;
        r8 = 1;
        goto L_0x0846;
    L_0x081d:
        r2 = 5;
        if (r3 != r2) goto L_0x0844;
    L_0x0820:
        r2 = -1;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0aef }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r3 = 26;
        if (r2 < r3) goto L_0x0844;
    L_0x082a:
        goto L_0x0841;
    L_0x082b:
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0aef }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r3 = 26;
        if (r2 < r3) goto L_0x0844;
    L_0x0834:
        r2 = 1;
        r8 = 4;
        goto L_0x0846;
    L_0x0837:
        r2 = -1;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0aef }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r3 = 26;
        if (r2 < r3) goto L_0x0844;
    L_0x0841:
        r2 = 1;
        r8 = 2;
        goto L_0x0846;
    L_0x0844:
        r2 = 1;
        r8 = 0;
    L_0x0846:
        if (r1 == r2) goto L_0x0970;
    L_0x0848:
        if (r28 != 0) goto L_0x0970;
    L_0x084a:
        r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0aef }
        if (r1 != 0) goto L_0x0850;
    L_0x084e:
        if (r24 == 0) goto L_0x087f;
    L_0x0850:
        r1 = r11.length();	 Catch:{ Exception -> 0x0aef }
        r2 = 100;
        if (r1 <= r2) goto L_0x087c;
    L_0x0858:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0aef }
        r1.<init>();	 Catch:{ Exception -> 0x0aef }
        r2 = 100;
        r3 = 0;
        r2 = r11.substring(r3, r2);	 Catch:{ Exception -> 0x0aef }
        r3 = 10;
        r9 = 32;
        r2 = r2.replace(r3, r9);	 Catch:{ Exception -> 0x0aef }
        r2 = r2.trim();	 Catch:{ Exception -> 0x0aef }
        r1.append(r2);	 Catch:{ Exception -> 0x0aef }
        r2 = "...";
        r1.append(r2);	 Catch:{ Exception -> 0x0aef }
        r11 = r1.toString();	 Catch:{ Exception -> 0x0aef }
    L_0x087c:
        r10.setTicker(r11);	 Catch:{ Exception -> 0x0aef }
    L_0x087f:
        r1 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0aef }
        r1 = r1.isRecordingAudio();	 Catch:{ Exception -> 0x0aef }
        if (r1 != 0) goto L_0x0903;
    L_0x0889:
        if (r39 == 0) goto L_0x0903;
    L_0x088b:
        r1 = "NoSound";
        r2 = r39;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0aef }
        if (r1 != 0) goto L_0x0903;
    L_0x0895:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r3 = 26;
        if (r1 < r3) goto L_0x08ab;
    L_0x089b:
        r1 = r21;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0aef }
        if (r1 == 0) goto L_0x08a6;
    L_0x08a3:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0aef }
        goto L_0x0904;
    L_0x08a6:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0aef }
        goto L_0x0904;
    L_0x08ab:
        r1 = r21;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0aef }
        if (r1 == 0) goto L_0x08ba;
    L_0x08b3:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0aef }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0aef }
        goto L_0x0903;
    L_0x08ba:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r3 = 24;
        if (r1 < r3) goto L_0x08fb;
    L_0x08c0:
        r1 = "file://";
        r1 = r2.startsWith(r1);	 Catch:{ Exception -> 0x0aef }
        if (r1 == 0) goto L_0x08fb;
    L_0x08c8:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0aef }
        r1 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1);	 Catch:{ Exception -> 0x0aef }
        if (r1 != 0) goto L_0x08fb;
    L_0x08d2:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x08f2 }
        r3 = "org.telegram.messenger.beta.provider";
        r9 = new java.io.File;	 Catch:{ Exception -> 0x08f2 }
        r11 = "file://";
        r11 = r2.replace(r11, r14);	 Catch:{ Exception -> 0x08f2 }
        r9.<init>(r11);	 Catch:{ Exception -> 0x08f2 }
        r1 = androidx.core.content.FileProvider.getUriForFile(r1, r3, r9);	 Catch:{ Exception -> 0x08f2 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x08f2 }
        r9 = "com.android.systemui";
        r11 = 1;
        r3.grantUriPermission(r9, r1, r11);	 Catch:{ Exception -> 0x08f2 }
        r3 = 5;
        r10.setSound(r1, r3);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0903;
    L_0x08f2:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0aef }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0aef }
        goto L_0x0903;
    L_0x08fb:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0aef }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0aef }
    L_0x0903:
        r1 = r7;
    L_0x0904:
        if (r29 == 0) goto L_0x0910;
    L_0x0906:
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r9 = r29;
        r10.setLights(r9, r2, r3);	 Catch:{ Exception -> 0x0aef }
        goto L_0x0912;
    L_0x0910:
        r9 = r29;
    L_0x0912:
        r14 = r27;
        r2 = 2;
        if (r14 == r2) goto L_0x095e;
    L_0x0917:
        r2 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0aef }
        r2 = r2.isRecordingAudio();	 Catch:{ Exception -> 0x0aef }
        if (r2 == 0) goto L_0x0923;
    L_0x0921:
        r2 = 2;
        goto L_0x095e;
    L_0x0923:
        r2 = 1;
        if (r14 != r2) goto L_0x093c;
    L_0x0926:
        r3 = 4;
        r3 = new long[r3];	 Catch:{ Exception -> 0x0aef }
        r7 = 0;
        r21 = 0;
        r3[r7] = r21;	 Catch:{ Exception -> 0x0aef }
        r27 = 100;
        r3[r2] = r27;	 Catch:{ Exception -> 0x0aef }
        r2 = 2;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0aef }
        r2 = 3;
        r3[r2] = r27;	 Catch:{ Exception -> 0x0aef }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0aef }
        goto L_0x096b;
    L_0x093c:
        if (r14 == 0) goto L_0x0956;
    L_0x093e:
        r2 = 4;
        if (r14 != r2) goto L_0x0942;
    L_0x0941:
        goto L_0x0956;
    L_0x0942:
        r2 = 3;
        if (r14 != r2) goto L_0x0954;
    L_0x0945:
        r2 = 2;
        r3 = new long[r2];	 Catch:{ Exception -> 0x0aef }
        r2 = 0;
        r18 = 0;
        r3[r2] = r18;	 Catch:{ Exception -> 0x0aef }
        r2 = 1;
        r3[r2] = r22;	 Catch:{ Exception -> 0x0aef }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0aef }
        goto L_0x096b;
    L_0x0954:
        r11 = r1;
        goto L_0x096d;
    L_0x0956:
        r2 = 2;
        r10.setDefaults(r2);	 Catch:{ Exception -> 0x0aef }
        r2 = 0;
        r3 = new long[r2];	 Catch:{ Exception -> 0x0aef }
        goto L_0x096b;
    L_0x095e:
        r3 = new long[r2];	 Catch:{ Exception -> 0x0aef }
        r2 = 0;
        r21 = 0;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0aef }
        r2 = 1;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0aef }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0aef }
    L_0x096b:
        r11 = r1;
        r7 = r3;
    L_0x096d:
        r1 = 0;
        r3 = 1;
        goto L_0x0982;
    L_0x0970:
        r9 = r29;
        r1 = 2;
        r2 = new long[r1];	 Catch:{ Exception -> 0x0aef }
        r1 = 0;
        r18 = 0;
        r2[r1] = r18;	 Catch:{ Exception -> 0x0aef }
        r3 = 1;
        r2[r3] = r18;	 Catch:{ Exception -> 0x0aef }
        r10.setVibrate(r2);	 Catch:{ Exception -> 0x0aef }
        r11 = r7;
        r7 = r2;
    L_0x0982:
        r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1);	 Catch:{ Exception -> 0x0aef }
        if (r2 != 0) goto L_0x0a5f;
    L_0x0988:
        r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0aef }
        if (r1 != 0) goto L_0x0a5f;
    L_0x098c:
        r1 = r4.getDialogId();	 Catch:{ Exception -> 0x0aef }
        r16 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r14 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1));
        if (r14 != 0) goto L_0x0a5f;
    L_0x0997:
        r1 = r4.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r1 = r1.reply_markup;	 Catch:{ Exception -> 0x0aef }
        if (r1 == 0) goto L_0x0a5f;
    L_0x099d:
        r1 = r4.messageOwner;	 Catch:{ Exception -> 0x0aef }
        r1 = r1.reply_markup;	 Catch:{ Exception -> 0x0aef }
        r1 = r1.rows;	 Catch:{ Exception -> 0x0aef }
        r2 = r1.size();	 Catch:{ Exception -> 0x0aef }
        r14 = 0;
        r16 = 0;
    L_0x09aa:
        if (r14 >= r2) goto L_0x0a57;
    L_0x09ac:
        r17 = r1.get(r14);	 Catch:{ Exception -> 0x0aef }
        r3 = r17;
        r3 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r3;	 Catch:{ Exception -> 0x0aef }
        r6 = r3.buttons;	 Catch:{ Exception -> 0x0aef }
        r6 = r6.size();	 Catch:{ Exception -> 0x0aef }
        r21 = r1;
        r1 = 0;
    L_0x09bd:
        if (r1 >= r6) goto L_0x0a3b;
    L_0x09bf:
        r22 = r2;
        r2 = r3.buttons;	 Catch:{ Exception -> 0x0aef }
        r2 = r2.get(r1);	 Catch:{ Exception -> 0x0aef }
        r2 = (org.telegram.tgnet.TLRPC.KeyboardButton) r2;	 Catch:{ Exception -> 0x0aef }
        r23 = r3;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;	 Catch:{ Exception -> 0x0aef }
        if (r3 == 0) goto L_0x0a1b;
    L_0x09cf:
        r3 = new android.content.Intent;	 Catch:{ Exception -> 0x0aef }
        r24 = r6;
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0aef }
        r13 = org.telegram.messenger.NotificationCallbackReceiver.class;
        r3.<init>(r6, r13);	 Catch:{ Exception -> 0x0aef }
        r6 = r12.currentAccount;	 Catch:{ Exception -> 0x0aef }
        r3.putExtra(r5, r6);	 Catch:{ Exception -> 0x0aef }
        r6 = "did";
        r13 = r8;
        r29 = r9;
        r8 = r36;
        r3.putExtra(r6, r8);	 Catch:{ Exception -> 0x0aef }
        r6 = r2.data;	 Catch:{ Exception -> 0x0aef }
        if (r6 == 0) goto L_0x09f7;
    L_0x09ed:
        r6 = "data";
        r26 = r15;
        r15 = r2.data;	 Catch:{ Exception -> 0x0aef }
        r3.putExtra(r6, r15);	 Catch:{ Exception -> 0x0aef }
        goto L_0x09f9;
    L_0x09f7:
        r26 = r15;
    L_0x09f9:
        r6 = "mid";
        r15 = r4.getId();	 Catch:{ Exception -> 0x0aef }
        r3.putExtra(r6, r15);	 Catch:{ Exception -> 0x0aef }
        r2 = r2.text;	 Catch:{ Exception -> 0x0aef }
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0aef }
        r15 = r12.lastButtonId;	 Catch:{ Exception -> 0x0aef }
        r44 = r4;
        r4 = r15 + 1;
        r12.lastButtonId = r4;	 Catch:{ Exception -> 0x0aef }
        r4 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r3 = android.app.PendingIntent.getBroadcast(r6, r15, r3, r4);	 Catch:{ Exception -> 0x0aef }
        r4 = 0;
        r10.addAction(r4, r2, r3);	 Catch:{ Exception -> 0x0aef }
        r16 = 1;
        goto L_0x0a27;
    L_0x0a1b:
        r44 = r4;
        r24 = r6;
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        r4 = 0;
    L_0x0a27:
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
        goto L_0x09bd;
    L_0x0a3b:
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
        goto L_0x09aa;
    L_0x0a57:
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        goto L_0x0a69;
    L_0x0a5f:
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        r4 = 0;
        r16 = 0;
    L_0x0a69:
        if (r16 != 0) goto L_0x0ac4;
    L_0x0a6b:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r2 = 24;
        if (r1 >= r2) goto L_0x0ac4;
    L_0x0a71:
        r1 = org.telegram.messenger.SharedConfig.passcodeHash;	 Catch:{ Exception -> 0x0aef }
        r1 = r1.length();	 Catch:{ Exception -> 0x0aef }
        if (r1 != 0) goto L_0x0ac4;
    L_0x0a79:
        r1 = r45.hasMessagesToReply();	 Catch:{ Exception -> 0x0aef }
        if (r1 == 0) goto L_0x0ac4;
    L_0x0a7f:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0aef }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0aef }
        r3 = org.telegram.messenger.PopupReplyReceiver.class;
        r1.<init>(r2, r3);	 Catch:{ Exception -> 0x0aef }
        r2 = r12.currentAccount;	 Catch:{ Exception -> 0x0aef }
        r1.putExtra(r5, r2);	 Catch:{ Exception -> 0x0aef }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r3 = 19;
        if (r2 > r3) goto L_0x0aac;
    L_0x0a93:
        r2 = NUM; // 0x7var_f2 float:1.7945069E38 double:1.0529356226E-314;
        r3 = "Reply";
        r4 = NUM; // 0x7f0e08fb float:1.88797E38 double:1.0531632925E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0aef }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0aef }
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r6 = 2;
        r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5);	 Catch:{ Exception -> 0x0aef }
        r10.addAction(r2, r3, r1);	 Catch:{ Exception -> 0x0aef }
        goto L_0x0ac4;
    L_0x0aac:
        r2 = NUM; // 0x7var_f1 float:1.7945067E38 double:1.052935622E-314;
        r3 = "Reply";
        r4 = NUM; // 0x7f0e08fb float:1.88797E38 double:1.0531632925E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0aef }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0aef }
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r6 = 2;
        r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5);	 Catch:{ Exception -> 0x0aef }
        r10.addAction(r2, r3, r1);	 Catch:{ Exception -> 0x0aef }
    L_0x0ac4:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0aef }
        r2 = 26;
        if (r1 < r2) goto L_0x0ae3;
    L_0x0aca:
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
        r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0aef }
        r13.setChannelId(r1);	 Catch:{ Exception -> 0x0aef }
        goto L_0x0ae4;
    L_0x0ae3:
        r13 = r10;
    L_0x0ae4:
        r1 = r46;
        r7 = r26;
        r12.showExtraNotifications(r13, r1, r7);	 Catch:{ Exception -> 0x0aef }
        r45.scheduleNotificationRepeat();	 Catch:{ Exception -> 0x0aef }
        goto L_0x0af4;
    L_0x0aef:
        r0 = move-exception;
    L_0x0af0:
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0af4:
        return;
    L_0x0af5:
        r45.dismissNotification();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:193:0x04c4  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x048f  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x018f  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0323  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0390  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x037e  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0434  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0451  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x07d9  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x07c8  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x07f9  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x0886  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x0853  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x08c9  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x08a7  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x098f  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0999  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x09b9  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x09be  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x09d2  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0a90  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0ae1 A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0b0a A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0b1b A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b15 A:{SYNTHETIC, Splitter:B:392:0x0b15} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02b1  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0323  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0331 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x037e  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0390  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x03e6  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x041b A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0434  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0451  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x07c8  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x07d9  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x07f9  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x0853  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x0886  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x08a7  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x08c9  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x098f  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0999  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x09a9  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x09b9  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x09be  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x09d2  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0a90  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0ae1 A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0b0a A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b15 A:{SYNTHETIC, Splitter:B:392:0x0b15} */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0b1b A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02b1  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0323  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0331 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0390  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x037e  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x03e6  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x041b A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0434  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0451  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x07d9  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x07c8  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x07f9  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x0886  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x0853  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x08c9  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x08a7  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x098f  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0999  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x09a9  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x09b9  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x09be  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x09d2  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0a90  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0ae1 A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0b0a A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0b1b A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b15 A:{SYNTHETIC, Splitter:B:392:0x0b15} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02b1  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0323  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0331 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x037e  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0390  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x03e6  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x041b A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0434  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0451  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x07c8  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x07d9  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x07f9  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x0853  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x0886  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x08a7  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x08c9  */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x098f  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0999  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x09a9  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x09b9  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x09be  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x09d2  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0a90  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0ae1 A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0b0a A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0b15 A:{SYNTHETIC, Splitter:B:392:0x0b15} */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0b1b A:{Catch:{ JSONException -> 0x0b33 }} */
    /* JADX WARNING: Missing block: B:66:0x0189, code skipped:
            if (r14.local_id != 0) goto L_0x018d;
     */
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
        r2 = r60.getAccountInstance();
        r2 = r2.getNotificationsSettings();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = new android.util.LongSparseArray;
        r4.<init>();
        r5 = 0;
        r6 = 0;
    L_0x0031:
        r7 = r0.pushMessages;
        r7 = r7.size();
        if (r6 >= r7) goto L_0x007e;
    L_0x0039:
        r7 = r0.pushMessages;
        r7 = r7.get(r6);
        r7 = (org.telegram.messenger.MessageObject) r7;
        r8 = r7.getDialogId();
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "dismissDate";
        r10.append(r11);
        r10.append(r8);
        r10 = r10.toString();
        r10 = r2.getInt(r10, r5);
        r11 = r7.messageOwner;
        r11 = r11.date;
        if (r11 > r10) goto L_0x0061;
    L_0x0060:
        goto L_0x007b;
    L_0x0061:
        r10 = r4.get(r8);
        r10 = (java.util.ArrayList) r10;
        if (r10 != 0) goto L_0x0078;
    L_0x0069:
        r10 = new java.util.ArrayList;
        r10.<init>();
        r4.put(r8, r10);
        r8 = java.lang.Long.valueOf(r8);
        r3.add(r5, r8);
    L_0x0078:
        r10.add(r7);
    L_0x007b:
        r6 = r6 + 1;
        goto L_0x0031;
    L_0x007e:
        r2 = r0.wearNotificationsIds;
        r2 = r2.clone();
        r6 = r0.wearNotificationsIds;
        r6.clear();
        r6 = new java.util.ArrayList;
        r6.<init>();
        r7 = org.telegram.messenger.WearDataLayerListenerService.isWatchConnected();
        if (r7 == 0) goto L_0x009a;
    L_0x0094:
        r7 = new org.json.JSONArray;
        r7.<init>();
        goto L_0x009b;
    L_0x009a:
        r7 = 0;
    L_0x009b:
        r9 = android.os.Build.VERSION.SDK_INT;
        r10 = 27;
        r11 = 1;
        if (r9 <= r10) goto L_0x00ad;
    L_0x00a2:
        if (r9 <= r10) goto L_0x00ab;
    L_0x00a4:
        r9 = r3.size();
        if (r9 <= r11) goto L_0x00ab;
    L_0x00aa:
        goto L_0x00ad;
    L_0x00ab:
        r9 = 0;
        goto L_0x00ae;
    L_0x00ad:
        r9 = 1;
    L_0x00ae:
        r10 = 26;
        if (r9 == 0) goto L_0x00b9;
    L_0x00b2:
        r12 = android.os.Build.VERSION.SDK_INT;
        if (r12 < r10) goto L_0x00b9;
    L_0x00b6:
        checkOtherNotificationsChannel();
    L_0x00b9:
        r12 = r60.getUserConfig();
        r12 = r12.getClientUserId();
        r13 = r3.size();
        r14 = 0;
    L_0x00c6:
        if (r14 >= r13) goto L_0x0b4d;
    L_0x00c8:
        r15 = r3.get(r14);
        r15 = (java.lang.Long) r15;
        r16 = r9;
        r8 = r15.longValue();
        r15 = r4.get(r8);
        r15 = (java.util.ArrayList) r15;
        r17 = r15.get(r5);
        r17 = (org.telegram.messenger.MessageObject) r17;
        r10 = r17.getId();
        r11 = (int) r8;
        r18 = 32;
        r20 = r6;
        r5 = r8 >> r18;
        r6 = (int) r5;
        r5 = r2.get(r8);
        r5 = (java.lang.Integer) r5;
        if (r5 != 0) goto L_0x0100;
    L_0x00f4:
        if (r11 == 0) goto L_0x00fb;
    L_0x00f6:
        r5 = java.lang.Integer.valueOf(r11);
        goto L_0x0103;
    L_0x00fb:
        r5 = java.lang.Integer.valueOf(r6);
        goto L_0x0103;
    L_0x0100:
        r2.remove(r8);
    L_0x0103:
        if (r7 == 0) goto L_0x010e;
    L_0x0105:
        r18 = new org.json.JSONObject;
        r18.<init>();
        r21 = r3;
        r3 = 0;
        goto L_0x0113;
    L_0x010e:
        r21 = r3;
        r3 = 0;
        r18 = 0;
    L_0x0113:
        r22 = r15.get(r3);
        r3 = r22;
        r3 = (org.telegram.messenger.MessageObject) r3;
        r22 = r4;
        r4 = r3.messageOwner;
        r4 = r4.date;
        r23 = r13;
        r13 = new android.util.LongSparseArray;
        r13.<init>();
        r24 = 0;
        if (r11 == 0) goto L_0x022f;
    L_0x012c:
        r26 = r2;
        r2 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r11 == r2) goto L_0x0135;
    L_0x0133:
        r2 = 1;
        goto L_0x0136;
    L_0x0135:
        r2 = 0;
    L_0x0136:
        if (r11 <= 0) goto L_0x01a3;
    L_0x0138:
        r27 = r2;
        r2 = r60.getMessagesController();
        r28 = r14;
        r14 = java.lang.Integer.valueOf(r11);
        r2 = r2.getUser(r14);
        if (r2 != 0) goto L_0x0171;
    L_0x014a:
        r14 = r3.isFcmMessage();
        if (r14 == 0) goto L_0x0157;
    L_0x0150:
        r3 = r3.localName;
    L_0x0152:
        r29 = r2;
        r30 = r3;
        goto L_0x018c;
    L_0x0157:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x01ec;
    L_0x015b:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not found user to show dialog notification ";
        r2.append(r3);
        r2.append(r11);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
        goto L_0x01ec;
    L_0x0171:
        r3 = org.telegram.messenger.UserObject.getUserName(r2);
        r14 = r2.photo;
        if (r14 == 0) goto L_0x0152;
    L_0x0179:
        r14 = r14.photo_small;
        if (r14 == 0) goto L_0x0152;
    L_0x017d:
        r29 = r2;
        r30 = r3;
        r2 = r14.volume_id;
        r31 = (r2 > r24 ? 1 : (r2 == r24 ? 0 : -1));
        if (r31 == 0) goto L_0x018c;
    L_0x0187:
        r2 = r14.local_id;
        if (r2 == 0) goto L_0x018c;
    L_0x018b:
        goto L_0x018d;
    L_0x018c:
        r14 = 0;
    L_0x018d:
        if (r11 != r12) goto L_0x0198;
    L_0x018f:
        r2 = NUM; // 0x7f0e0605 float:1.8878163E38 double:1.053162918E-314;
        r3 = "MessageScheduledReminderNotification";
        r30 = org.telegram.messenger.LocaleController.getString(r3, r2);
    L_0x0198:
        r31 = r1;
        r1 = r18;
        r2 = r29;
        r3 = 0;
        r18 = 0;
        goto L_0x02a7;
    L_0x01a3:
        r27 = r2;
        r28 = r14;
        r2 = r60.getMessagesController();
        r14 = -r11;
        r14 = java.lang.Integer.valueOf(r14);
        r2 = r2.getChat(r14);
        if (r2 != 0) goto L_0x01f0;
    L_0x01b6:
        r14 = r3.isFcmMessage();
        if (r14 == 0) goto L_0x01d4;
    L_0x01bc:
        r14 = r3.isMegagroup();
        r29 = r14;
        r14 = r3.localName;
        r3 = r3.localChannel;
        r31 = r1;
        r32 = r2;
        r30 = r14;
    L_0x01cc:
        r1 = r18;
        r2 = 0;
        r14 = 0;
        r18 = 0;
        goto L_0x02ab;
    L_0x01d4:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x01ec;
    L_0x01d8:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not found chat to show dialog notification ";
        r2.append(r3);
        r2.append(r11);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
    L_0x01ec:
        r31 = r1;
        goto L_0x0261;
    L_0x01f0:
        r3 = r2.megagroup;
        r14 = org.telegram.messenger.ChatObject.isChannel(r2);
        if (r14 == 0) goto L_0x0200;
    L_0x01f8:
        r14 = r2.megagroup;
        if (r14 != 0) goto L_0x0200;
    L_0x01fc:
        r29 = r3;
        r14 = 1;
        goto L_0x0203;
    L_0x0200:
        r29 = r3;
        r14 = 0;
    L_0x0203:
        r3 = r2.title;
        r30 = r3;
        r3 = r2.photo;
        if (r3 == 0) goto L_0x0229;
    L_0x020b:
        r3 = r3.photo_small;
        if (r3 == 0) goto L_0x0229;
    L_0x020f:
        r31 = r1;
        r32 = r2;
        r1 = r3.volume_id;
        r33 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1));
        if (r33 == 0) goto L_0x022d;
    L_0x0219:
        r1 = r3.local_id;
        if (r1 == 0) goto L_0x022d;
    L_0x021d:
        r1 = r18;
        r2 = 0;
        r18 = 0;
        r59 = r14;
        r14 = r3;
        r3 = r59;
        goto L_0x02ab;
    L_0x0229:
        r31 = r1;
        r32 = r2;
    L_0x022d:
        r3 = r14;
        goto L_0x01cc;
    L_0x022f:
        r31 = r1;
        r26 = r2;
        r28 = r14;
        r1 = globalSecretChatId;
        r3 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1));
        if (r3 == 0) goto L_0x0296;
    L_0x023b:
        r1 = r60.getMessagesController();
        r2 = java.lang.Integer.valueOf(r6);
        r1 = r1.getEncryptedChat(r2);
        if (r1 != 0) goto L_0x026b;
    L_0x0249:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0261;
    L_0x024d:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "not found secret chat to show dialog notification ";
        r1.append(r2);
        r1.append(r6);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.w(r1);
    L_0x0261:
        r8 = r7;
        r55 = r12;
        r5 = r20;
        r2 = 26;
        r3 = 0;
        goto L_0x0b35;
    L_0x026b:
        r2 = r60.getMessagesController();
        r3 = r1.user_id;
        r3 = java.lang.Integer.valueOf(r3);
        r2 = r2.getUser(r3);
        if (r2 != 0) goto L_0x0297;
    L_0x027b:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0261;
    L_0x027f:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not found secret chat user to show dialog notification ";
        r2.append(r3);
        r1 = r1.user_id;
        r2.append(r1);
        r1 = r2.toString();
        org.telegram.messenger.FileLog.w(r1);
        goto L_0x0261;
    L_0x0296:
        r2 = 0;
    L_0x0297:
        r1 = NUM; // 0x7f0e096e float:1.8879934E38 double:1.0531633493E-314;
        r3 = "SecretChatName";
        r30 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r1 = 0;
        r3 = 0;
        r14 = 0;
        r18 = 0;
        r27 = 0;
    L_0x02a7:
        r29 = 0;
        r32 = 0;
    L_0x02ab:
        r33 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r18);
        if (r33 != 0) goto L_0x02bf;
    L_0x02b1:
        r18 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r18 == 0) goto L_0x02b6;
    L_0x02b5:
        goto L_0x02bf;
    L_0x02b6:
        r18 = r7;
        r7 = r27;
        r27 = r2;
        r2 = r30;
        goto L_0x02d0;
    L_0x02bf:
        r14 = NUM; // 0x7f0e00f1 float:1.8875526E38 double:1.0531622757E-314;
        r18 = r7;
        r7 = "AppName";
        r30 = org.telegram.messenger.LocaleController.getString(r7, r14);
        r27 = r2;
        r2 = r30;
        r7 = 0;
        r14 = 0;
    L_0x02d0:
        r30 = r4;
        if (r14 == 0) goto L_0x0323;
    L_0x02d4:
        r4 = 1;
        r34 = org.telegram.messenger.FileLoader.getPathToAttach(r14, r4);
        r4 = android.os.Build.VERSION.SDK_INT;
        r35 = r6;
        r6 = 28;
        if (r4 >= r6) goto L_0x031e;
    L_0x02e1:
        r4 = org.telegram.messenger.ImageLoader.getInstance();
        r6 = "50_50";
        r36 = r13;
        r13 = 0;
        r4 = r4.getImageFromMemory(r14, r13, r6);
        if (r4 == 0) goto L_0x02f5;
    L_0x02f0:
        r4 = r4.getBitmap();
        goto L_0x032b;
    L_0x02f5:
        r4 = r34.exists();	 Catch:{ all -> 0x0321 }
        if (r4 == 0) goto L_0x0321;
    L_0x02fb:
        r4 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ all -> 0x0321 }
        r6 = (float) r6;	 Catch:{ all -> 0x0321 }
        r4 = r4 / r6;
        r6 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x0321 }
        r6.<init>();	 Catch:{ all -> 0x0321 }
        r37 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r37 = (r4 > r37 ? 1 : (r4 == r37 ? 0 : -1));
        if (r37 >= 0) goto L_0x0312;
    L_0x0310:
        r4 = 1;
        goto L_0x0313;
    L_0x0312:
        r4 = (int) r4;	 Catch:{ all -> 0x0321 }
    L_0x0313:
        r6.inSampleSize = r4;	 Catch:{ all -> 0x0321 }
        r4 = r34.getAbsolutePath();	 Catch:{ all -> 0x0321 }
        r4 = android.graphics.BitmapFactory.decodeFile(r4, r6);	 Catch:{ all -> 0x0321 }
        goto L_0x032b;
    L_0x031e:
        r36 = r13;
        r13 = 0;
    L_0x0321:
        r4 = r13;
        goto L_0x032b;
    L_0x0323:
        r35 = r6;
        r36 = r13;
        r13 = 0;
        r4 = r13;
        r34 = r4;
    L_0x032b:
        r6 = "max_id";
        r13 = "currentAccount";
        if (r3 == 0) goto L_0x0333;
    L_0x0331:
        if (r29 == 0) goto L_0x03bc;
    L_0x0333:
        if (r7 == 0) goto L_0x03bc;
    L_0x0335:
        r38 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r38 != 0) goto L_0x03bc;
    L_0x0339:
        if (r12 == r11) goto L_0x03bc;
    L_0x033b:
        r38 = r14;
        r14 = new android.content.Intent;
        r39 = r7;
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r40 = r4;
        r4 = org.telegram.messenger.WearReplyReceiver.class;
        r14.<init>(r7, r4);
        r4 = "dialog_id";
        r14.putExtra(r4, r8);
        r14.putExtra(r6, r10);
        r4 = r0.currentAccount;
        r14.putExtra(r13, r4);
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r7 = r5.intValue();
        r41 = r5;
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r4 = android.app.PendingIntent.getBroadcast(r4, r7, r14, r5);
        r5 = new androidx.core.app.RemoteInput$Builder;
        r7 = "extra_voice_reply";
        r5.<init>(r7);
        r7 = NUM; // 0x7f0e08fb float:1.88797E38 double:1.0531632925E-314;
        r14 = "Reply";
        r7 = org.telegram.messenger.LocaleController.getString(r14, r7);
        r5.setLabel(r7);
        r5 = r5.build();
        if (r11 >= 0) goto L_0x0390;
    L_0x037e:
        r14 = 1;
        r7 = new java.lang.Object[r14];
        r14 = 0;
        r7[r14] = r2;
        r14 = "ReplyToGroup";
        r42 = r6;
        r6 = NUM; // 0x7f0e08fc float:1.8879702E38 double:1.053163293E-314;
        r6 = org.telegram.messenger.LocaleController.formatString(r14, r6, r7);
        goto L_0x03a1;
    L_0x0390:
        r42 = r6;
        r6 = NUM; // 0x7f0e08fd float:1.8879705E38 double:1.0531632935E-314;
        r7 = 1;
        r14 = new java.lang.Object[r7];
        r7 = 0;
        r14[r7] = r2;
        r7 = "ReplyToUser";
        r6 = org.telegram.messenger.LocaleController.formatString(r7, r6, r14);
    L_0x03a1:
        r7 = new androidx.core.app.NotificationCompat$Action$Builder;
        r14 = NUM; // 0x7var_f float:1.794516E38 double:1.052935645E-314;
        r7.<init>(r14, r6, r4);
        r4 = 1;
        r7.setAllowGeneratedReplies(r4);
        r7.setSemanticAction(r4);
        r7.addRemoteInput(r5);
        r4 = 0;
        r7.setShowsUserInterface(r4);
        r5 = r7.build();
        goto L_0x03c8;
    L_0x03bc:
        r40 = r4;
        r41 = r5;
        r42 = r6;
        r39 = r7;
        r38 = r14;
        r4 = 0;
        r5 = 0;
    L_0x03c8:
        r6 = r0.pushDialogs;
        r6 = r6.get(r8);
        r6 = (java.lang.Integer) r6;
        if (r6 != 0) goto L_0x03d6;
    L_0x03d2:
        r6 = java.lang.Integer.valueOf(r4);
    L_0x03d6:
        r4 = r6.intValue();
        r6 = r15.size();
        r4 = java.lang.Math.max(r4, r6);
        r6 = 2;
        r7 = 1;
        if (r4 <= r7) goto L_0x0400;
    L_0x03e6:
        r14 = android.os.Build.VERSION.SDK_INT;
        r7 = 28;
        if (r14 < r7) goto L_0x03ed;
    L_0x03ec:
        goto L_0x0400;
    L_0x03ed:
        r7 = new java.lang.Object[r6];
        r14 = 0;
        r7[r14] = r2;
        r4 = java.lang.Integer.valueOf(r4);
        r14 = 1;
        r7[r14] = r4;
        r4 = "%1$s (%2$d)";
        r4 = java.lang.String.format(r4, r7);
        goto L_0x0401;
    L_0x0400:
        r4 = r2;
    L_0x0401:
        r7 = new androidx.core.app.NotificationCompat$MessagingStyle;
        r14 = "";
        r7.<init>(r14);
        r6 = android.os.Build.VERSION.SDK_INT;
        r43 = r10;
        r10 = 28;
        if (r6 < r10) goto L_0x0414;
    L_0x0410:
        if (r11 >= 0) goto L_0x0417;
    L_0x0412:
        if (r3 != 0) goto L_0x0417;
    L_0x0414:
        r7.setConversationTitle(r4);
    L_0x0417:
        r4 = android.os.Build.VERSION.SDK_INT;
        if (r4 < r10) goto L_0x0422;
    L_0x041b:
        if (r3 != 0) goto L_0x0420;
    L_0x041d:
        if (r11 >= 0) goto L_0x0420;
    L_0x041f:
        goto L_0x0422;
    L_0x0420:
        r4 = 0;
        goto L_0x0423;
    L_0x0422:
        r4 = 1;
    L_0x0423:
        r7.setGroupConversation(r4);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = 1;
        r10 = new java.lang.String[r6];
        r44 = r5;
        r5 = new boolean[r6];
        if (r1 == 0) goto L_0x043e;
    L_0x0434:
        r17 = new org.json.JSONArray;
        r17.<init>();
        r45 = r1;
        r1 = r17;
        goto L_0x0441;
    L_0x043e:
        r45 = r1;
        r1 = 0;
    L_0x0441:
        r17 = r15.size();
        r46 = r17 + -1;
        r6 = r46;
        r47 = 0;
        r48 = 0;
    L_0x044d:
        r49 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r6 < 0) goto L_0x0788;
    L_0x0451:
        r46 = r15.get(r6);
        r51 = r15;
        r15 = r46;
        r15 = (org.telegram.messenger.MessageObject) r15;
        r46 = r13;
        r13 = r0.getShortStringForMessage(r15, r10, r5);
        r53 = r6;
        r52 = r7;
        r6 = (long) r12;
        r54 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r54 != 0) goto L_0x0471;
    L_0x046a:
        r19 = 0;
        r10[r19] = r2;
        r54 = r2;
        goto L_0x048b;
    L_0x0471:
        r19 = 0;
        r54 = r2;
        if (r11 >= 0) goto L_0x048b;
    L_0x0477:
        r2 = r15.messageOwner;
        r2 = r2.from_scheduled;
        if (r2 == 0) goto L_0x048b;
    L_0x047d:
        r2 = NUM; // 0x7f0e06d9 float:1.8878593E38 double:1.0531630227E-314;
        r55 = r12;
        r12 = "NotificationMessageScheduledName";
        r2 = org.telegram.messenger.LocaleController.getString(r12, r2);
        r10[r19] = r2;
        goto L_0x048d;
    L_0x048b:
        r55 = r12;
    L_0x048d:
        if (r13 != 0) goto L_0x04c4;
    L_0x048f:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x04b7;
    L_0x0493:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r6 = "message text is null for ";
        r2.append(r6);
        r6 = r15.getId();
        r2.append(r6);
        r6 = " did = ";
        r2.append(r6);
        r6 = r15.getDialogId();
        r2.append(r6);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
    L_0x04b7:
        r57 = r3;
        r56 = r10;
        r58 = r36;
        r3 = r52;
        r10 = r4;
        r36 = r5;
        goto L_0x0772;
    L_0x04c4:
        r2 = r4.length();
        if (r2 <= 0) goto L_0x04cf;
    L_0x04ca:
        r2 = "\n\n";
        r4.append(r2);
    L_0x04cf:
        r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r2 == 0) goto L_0x04f7;
    L_0x04d3:
        r2 = r15.messageOwner;
        r2 = r2.from_scheduled;
        if (r2 == 0) goto L_0x04f7;
    L_0x04d9:
        if (r11 <= 0) goto L_0x04f7;
    L_0x04db:
        r2 = 2;
        r6 = new java.lang.Object[r2];
        r2 = NUM; // 0x7f0e06d9 float:1.8878593E38 double:1.0531630227E-314;
        r7 = "NotificationMessageScheduledName";
        r2 = org.telegram.messenger.LocaleController.getString(r7, r2);
        r7 = 0;
        r6[r7] = r2;
        r2 = 1;
        r6[r2] = r13;
        r2 = "%1$s: %2$s";
        r13 = java.lang.String.format(r2, r6);
        r4.append(r13);
        goto L_0x0513;
    L_0x04f7:
        r7 = 0;
        r2 = r10[r7];
        if (r2 == 0) goto L_0x0510;
    L_0x04fc:
        r2 = 2;
        r6 = new java.lang.Object[r2];
        r2 = r10[r7];
        r6[r7] = r2;
        r2 = 1;
        r6[r2] = r13;
        r2 = "%1$s: %2$s";
        r2 = java.lang.String.format(r2, r6);
        r4.append(r2);
        goto L_0x0513;
    L_0x0510:
        r4.append(r13);
    L_0x0513:
        if (r11 <= 0) goto L_0x0519;
    L_0x0515:
        r6 = (long) r11;
    L_0x0516:
        r2 = r36;
        goto L_0x0527;
    L_0x0519:
        if (r3 == 0) goto L_0x051e;
    L_0x051b:
        r2 = -r11;
    L_0x051c:
        r6 = (long) r2;
        goto L_0x0516;
    L_0x051e:
        if (r11 >= 0) goto L_0x0525;
    L_0x0520:
        r2 = r15.getFromId();
        goto L_0x051c;
    L_0x0525:
        r6 = r8;
        goto L_0x0516;
    L_0x0527:
        r12 = r2.get(r6);
        r12 = (androidx.core.app.Person) r12;
        if (r12 != 0) goto L_0x05bc;
    L_0x052f:
        r12 = new androidx.core.app.Person$Builder;
        r12.<init>();
        r19 = 0;
        r36 = r10[r19];
        if (r36 != 0) goto L_0x053e;
    L_0x053a:
        r56 = r10;
        r10 = r14;
        goto L_0x0544;
    L_0x053e:
        r36 = r10[r19];
        r56 = r10;
        r10 = r36;
    L_0x0544:
        r12.setName(r10);
        r10 = r5[r19];
        if (r10 == 0) goto L_0x05af;
    L_0x054b:
        if (r11 == 0) goto L_0x05af;
    L_0x054d:
        r10 = android.os.Build.VERSION.SDK_INT;
        r36 = r5;
        r5 = 28;
        if (r10 < r5) goto L_0x05ab;
    L_0x0555:
        if (r11 > 0) goto L_0x05a2;
    L_0x0557:
        if (r3 == 0) goto L_0x055a;
    L_0x0559:
        goto L_0x05a2;
    L_0x055a:
        if (r11 >= 0) goto L_0x059d;
    L_0x055c:
        r5 = r15.getFromId();
        r10 = r60.getMessagesController();
        r57 = r3;
        r3 = java.lang.Integer.valueOf(r5);
        r3 = r10.getUser(r3);
        if (r3 != 0) goto L_0x0582;
    L_0x0570:
        r3 = r60.getMessagesStorage();
        r3 = r3.getUserSync(r5);
        if (r3 == 0) goto L_0x0582;
    L_0x057a:
        r5 = r60.getMessagesController();
        r10 = 1;
        r5.putUser(r3, r10);
    L_0x0582:
        if (r3 == 0) goto L_0x059f;
    L_0x0584:
        r3 = r3.photo;
        if (r3 == 0) goto L_0x059f;
    L_0x0588:
        r3 = r3.photo_small;
        if (r3 == 0) goto L_0x059f;
    L_0x058c:
        r10 = r4;
        r4 = r3.volume_id;
        r58 = (r4 > r24 ? 1 : (r4 == r24 ? 0 : -1));
        if (r58 == 0) goto L_0x05a0;
    L_0x0593:
        r4 = r3.local_id;
        if (r4 == 0) goto L_0x05a0;
    L_0x0597:
        r4 = 1;
        r3 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r4);
        goto L_0x05a7;
    L_0x059d:
        r57 = r3;
    L_0x059f:
        r10 = r4;
    L_0x05a0:
        r3 = 0;
        goto L_0x05a7;
    L_0x05a2:
        r57 = r3;
        r10 = r4;
        r3 = r34;
    L_0x05a7:
        r0.loadRoundAvatar(r3, r12);
        goto L_0x05b4;
    L_0x05ab:
        r57 = r3;
        r10 = r4;
        goto L_0x05b4;
    L_0x05af:
        r57 = r3;
        r10 = r4;
        r36 = r5;
    L_0x05b4:
        r12 = r12.build();
        r2.put(r6, r12);
        goto L_0x05c3;
    L_0x05bc:
        r57 = r3;
        r36 = r5;
        r56 = r10;
        r10 = r4;
    L_0x05c3:
        if (r11 == 0) goto L_0x070b;
    L_0x05c5:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 28;
        if (r3 < r4) goto L_0x06be;
    L_0x05cb:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = "activity";
        r3 = r3.getSystemService(r5);
        r3 = (android.app.ActivityManager) r3;
        r3 = r3.isLowRamDevice();
        if (r3 != 0) goto L_0x06be;
    L_0x05db:
        r3 = r15.isSecretMedia();
        if (r3 != 0) goto L_0x06af;
    L_0x05e1:
        r3 = r15.type;
        r5 = 1;
        if (r3 == r5) goto L_0x05ec;
    L_0x05e6:
        r3 = r15.isSticker();
        if (r3 == 0) goto L_0x06af;
    L_0x05ec:
        r3 = r15.messageOwner;
        r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3);
        r5 = new androidx.core.app.NotificationCompat$MessagingStyle$Message;
        r6 = r15.messageOwner;
        r6 = r6.date;
        r6 = (long) r6;
        r6 = r6 * r49;
        r5.<init>(r13, r6, r12);
        r6 = r15.isSticker();
        if (r6 == 0) goto L_0x0607;
    L_0x0604:
        r6 = "image/webp";
        goto L_0x0609;
    L_0x0607:
        r6 = "image/jpeg";
    L_0x0609:
        r7 = r3.exists();
        if (r7 == 0) goto L_0x061b;
    L_0x060f:
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = "org.telegram.messenger.beta.provider";
        r3 = androidx.core.content.FileProvider.getUriForFile(r7, r4, r3);
        r58 = r2;
        r2 = r3;
        goto L_0x0671;
    L_0x061b:
        r4 = r60.getFileLoader();
        r7 = r3.getName();
        r4 = r4.isLoadingFile(r7);
        if (r4 == 0) goto L_0x066e;
    L_0x0629:
        r4 = new android.net.Uri$Builder;
        r4.<init>();
        r7 = "content";
        r4 = r4.scheme(r7);
        r7 = "org.telegram.messenger.beta.notification_image_provider";
        r4 = r4.authority(r7);
        r7 = "msg_media_raw";
        r4 = r4.appendPath(r7);
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r58 = r2;
        r2 = r0.currentAccount;
        r7.append(r2);
        r7.append(r14);
        r2 = r7.toString();
        r2 = r4.appendPath(r2);
        r4 = r3.getName();
        r2 = r2.appendPath(r4);
        r3 = r3.getAbsolutePath();
        r4 = "final_path";
        r2 = r2.appendQueryParameter(r4, r3);
        r2 = r2.build();
        goto L_0x0671;
    L_0x066e:
        r58 = r2;
        r2 = 0;
    L_0x0671:
        if (r2 == 0) goto L_0x06a2;
    L_0x0673:
        r5.setData(r6, r2);
        r3 = r52;
        r3.addMessage(r5);
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = "com.android.systemui";
        r6 = 1;
        r4.grantUriPermission(r5, r2, r6);
        r4 = new org.telegram.messenger.-$$Lambda$NotificationsController$hROO1aIM4eduzMv5uJ3U4yL97Bo;
        r4.<init>(r2);
        r5 = 20000; // 0x4e20 float:2.8026E-41 double:9.8813E-320;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4, r5);
        r2 = r15.caption;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x06cc;
    L_0x0695:
        r2 = r15.caption;
        r4 = r15.messageOwner;
        r4 = r4.date;
        r4 = (long) r4;
        r4 = r4 * r49;
        r3.addMessage(r2, r4, r12);
        goto L_0x06cc;
    L_0x06a2:
        r3 = r52;
        r2 = r15.messageOwner;
        r2 = r2.date;
        r4 = (long) r2;
        r4 = r4 * r49;
        r3.addMessage(r13, r4, r12);
        goto L_0x06cc;
    L_0x06af:
        r58 = r2;
        r3 = r52;
        r2 = r15.messageOwner;
        r2 = r2.date;
        r4 = (long) r2;
        r4 = r4 * r49;
        r3.addMessage(r13, r4, r12);
        goto L_0x06cc;
    L_0x06be:
        r58 = r2;
        r3 = r52;
        r2 = r15.messageOwner;
        r2 = r2.date;
        r4 = (long) r2;
        r4 = r4 * r49;
        r3.addMessage(r13, r4, r12);
    L_0x06cc:
        r2 = r15.isVoice();
        if (r2 == 0) goto L_0x0719;
    L_0x06d2:
        r2 = r3.getMessages();
        r4 = r2.isEmpty();
        if (r4 != 0) goto L_0x0719;
    L_0x06dc:
        r4 = r15.messageOwner;
        r4 = org.telegram.messenger.FileLoader.getPathToMessage(r4);
        r5 = android.os.Build.VERSION.SDK_INT;
        r6 = 24;
        if (r5 < r6) goto L_0x06f3;
    L_0x06e8:
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x06f1 }
        r6 = "org.telegram.messenger.beta.provider";
        r4 = androidx.core.content.FileProvider.getUriForFile(r5, r6, r4);	 Catch:{ Exception -> 0x06f1 }
        goto L_0x06f7;
    L_0x06f1:
        r4 = 0;
        goto L_0x06f7;
    L_0x06f3:
        r4 = android.net.Uri.fromFile(r4);
    L_0x06f7:
        if (r4 == 0) goto L_0x0719;
    L_0x06f9:
        r5 = r2.size();
        r6 = 1;
        r5 = r5 - r6;
        r2 = r2.get(r5);
        r2 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r2;
        r5 = "audio/ogg";
        r2.setData(r5, r4);
        goto L_0x0719;
    L_0x070b:
        r58 = r2;
        r3 = r52;
        r2 = r15.messageOwner;
        r2 = r2.date;
        r4 = (long) r2;
        r4 = r4 * r49;
        r3.addMessage(r13, r4, r12);
    L_0x0719:
        if (r1 == 0) goto L_0x075b;
    L_0x071b:
        r2 = new org.json.JSONObject;	 Catch:{ JSONException -> 0x075a }
        r2.<init>();	 Catch:{ JSONException -> 0x075a }
        r4 = "text";
        r2.put(r4, r13);	 Catch:{ JSONException -> 0x075a }
        r4 = "date";
        r5 = r15.messageOwner;	 Catch:{ JSONException -> 0x075a }
        r5 = r5.date;	 Catch:{ JSONException -> 0x075a }
        r2.put(r4, r5);	 Catch:{ JSONException -> 0x075a }
        r4 = r15.isFromUser();	 Catch:{ JSONException -> 0x075a }
        if (r4 == 0) goto L_0x0756;
    L_0x0734:
        if (r11 >= 0) goto L_0x0756;
    L_0x0736:
        r4 = r60.getMessagesController();	 Catch:{ JSONException -> 0x075a }
        r5 = r15.getFromId();	 Catch:{ JSONException -> 0x075a }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ JSONException -> 0x075a }
        r4 = r4.getUser(r5);	 Catch:{ JSONException -> 0x075a }
        if (r4 == 0) goto L_0x0756;
    L_0x0748:
        r5 = "fname";
        r6 = r4.first_name;	 Catch:{ JSONException -> 0x075a }
        r2.put(r5, r6);	 Catch:{ JSONException -> 0x075a }
        r5 = "lname";
        r4 = r4.last_name;	 Catch:{ JSONException -> 0x075a }
        r2.put(r5, r4);	 Catch:{ JSONException -> 0x075a }
    L_0x0756:
        r1.put(r2);	 Catch:{ JSONException -> 0x075a }
        goto L_0x075b;
    L_0x075b:
        r4 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r2 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x0772;
    L_0x0762:
        r2 = r15.messageOwner;
        r2 = r2.reply_markup;
        if (r2 == 0) goto L_0x0772;
    L_0x0768:
        r2 = r2.rows;
        r4 = r15.getId();
        r47 = r2;
        r48 = r4;
    L_0x0772:
        r6 = r53 + -1;
        r7 = r3;
        r4 = r10;
        r5 = r36;
        r13 = r46;
        r15 = r51;
        r2 = r54;
        r12 = r55;
        r10 = r56;
        r3 = r57;
        r36 = r58;
        goto L_0x044d;
    L_0x0788:
        r54 = r2;
        r57 = r3;
        r10 = r4;
        r3 = r7;
        r55 = r12;
        r46 = r13;
        r51 = r15;
        r2 = new android.content.Intent;
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = org.telegram.ui.LaunchActivity.class;
        r2.<init>(r4, r5);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "com.tmessages.openchat";
        r4.append(r5);
        r5 = java.lang.Math.random();
        r4.append(r5);
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4.append(r5);
        r4 = r4.toString();
        r2.setAction(r4);
        r4 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r2.setFlags(r4);
        r4 = "android.intent.category.LAUNCHER";
        r2.addCategory(r4);
        if (r11 == 0) goto L_0x07d9;
    L_0x07c8:
        if (r11 <= 0) goto L_0x07d0;
    L_0x07ca:
        r4 = "userId";
        r2.putExtra(r4, r11);
        goto L_0x07d6;
    L_0x07d0:
        r4 = -r11;
        r5 = "chatId";
        r2.putExtra(r5, r4);
    L_0x07d6:
        r5 = r35;
        goto L_0x07e0;
    L_0x07d9:
        r4 = "encId";
        r5 = r35;
        r2.putExtra(r4, r5);
    L_0x07e0:
        r4 = r0.currentAccount;
        r6 = r46;
        r2.putExtra(r6, r4);
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r7 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r12 = 0;
        r2 = android.app.PendingIntent.getActivity(r4, r12, r2, r7);
        r4 = new androidx.core.app.NotificationCompat$WearableExtender;
        r4.<init>();
        r7 = r44;
        if (r44 == 0) goto L_0x07fc;
    L_0x07f9:
        r4.addAction(r7);
    L_0x07fc:
        r12 = new android.content.Intent;
        r13 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r15 = org.telegram.messenger.AutoMessageHeardReceiver.class;
        r12.<init>(r13, r15);
        r13 = 32;
        r12.addFlags(r13);
        r13 = "org.telegram.messenger.ACTION_MESSAGE_HEARD";
        r12.setAction(r13);
        r13 = "dialog_id";
        r12.putExtra(r13, r8);
        r15 = r42;
        r13 = r43;
        r12.putExtra(r15, r13);
        r24 = r1;
        r1 = r0.currentAccount;
        r12.putExtra(r6, r1);
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r15 = r41.intValue();
        r44 = r7;
        r7 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r1 = android.app.PendingIntent.getBroadcast(r1, r15, r12, r7);
        r7 = new androidx.core.app.NotificationCompat$Action$Builder;
        r12 = NUM; // 0x7var_e float:1.7945418E38 double:1.0529357076E-314;
        r15 = NUM; // 0x7f0e05c7 float:1.8878037E38 double:1.0531628874E-314;
        r46 = r6;
        r6 = "MarkAsRead";
        r6 = org.telegram.messenger.LocaleController.getString(r6, r15);
        r7.<init>(r12, r6, r1);
        r1 = 2;
        r7.setSemanticAction(r1);
        r1 = 0;
        r7.setShowsUserInterface(r1);
        r1 = r7.build();
        r6 = "_";
        if (r11 == 0) goto L_0x0886;
    L_0x0853:
        if (r11 <= 0) goto L_0x086d;
    L_0x0855:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r7 = "tguser";
        r5.append(r7);
        r5.append(r11);
        r5.append(r6);
        r5.append(r13);
        r5 = r5.toString();
        goto L_0x08a5;
    L_0x086d:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r7 = "tgchat";
        r5.append(r7);
        r7 = -r11;
        r5.append(r7);
        r5.append(r6);
        r5.append(r13);
        r5 = r5.toString();
        goto L_0x08a5;
    L_0x0886:
        r33 = globalSecretChatId;
        r7 = (r8 > r33 ? 1 : (r8 == r33 ? 0 : -1));
        if (r7 == 0) goto L_0x08a4;
    L_0x088c:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r12 = "tgenc";
        r7.append(r12);
        r7.append(r5);
        r7.append(r6);
        r7.append(r13);
        r5 = r7.toString();
        goto L_0x08a5;
    L_0x08a4:
        r5 = 0;
    L_0x08a5:
        if (r5 == 0) goto L_0x08c9;
    L_0x08a7:
        r4.setDismissalId(r5);
        r7 = new androidx.core.app.NotificationCompat$WearableExtender;
        r7.<init>();
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r15 = "summary_";
        r12.append(r15);
        r12.append(r5);
        r5 = r12.toString();
        r7.setDismissalId(r5);
        r5 = r61;
        r5.extend(r7);
        goto L_0x08cb;
    L_0x08c9:
        r5 = r61;
    L_0x08cb:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r12 = "tgaccount";
        r7.append(r12);
        r12 = r55;
        r7.append(r12);
        r7 = r7.toString();
        r4.setBridgeTag(r7);
        r15 = r51;
        r7 = 0;
        r25 = r15.get(r7);
        r7 = r25;
        r7 = (org.telegram.messenger.MessageObject) r7;
        r7 = r7.messageOwner;
        r7 = r7.date;
        r25 = r6;
        r5 = (long) r7;
        r5 = r5 * r49;
        r7 = new androidx.core.app.NotificationCompat$Builder;
        r12 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r7.<init>(r12);
        r12 = r54;
        r7.setContentTitle(r12);
        r43 = r13;
        r13 = NUM; // 0x7var_b float:1.7945639E38 double:1.0529357614E-314;
        r7.setSmallIcon(r13);
        r10 = r10.toString();
        r7.setContentText(r10);
        r10 = 1;
        r7.setAutoCancel(r10);
        r13 = r15.size();
        r7.setNumber(r13);
        r13 = -15618822; // 0xfffffffffvar_acfa float:-1.936362E38 double:NaN;
        r7.setColor(r13);
        r13 = 0;
        r7.setGroupSummary(r13);
        r7.setWhen(r5);
        r7.setShowWhen(r10);
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r13 = "sdid_";
        r10.append(r13);
        r10.append(r8);
        r10 = r10.toString();
        r7.setShortcutId(r10);
        r7.setStyle(r3);
        r7.setContentIntent(r2);
        r7.extend(r4);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r14);
        r3 = NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        r3 = r3 - r5;
        r2.append(r3);
        r2 = r2.toString();
        r7.setSortKey(r2);
        r2 = "msg";
        r7.setCategory(r2);
        r2 = new android.content.Intent;
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = org.telegram.messenger.NotificationDismissReceiver.class;
        r2.<init>(r3, r4);
        r3 = "messageDate";
        r4 = r30;
        r2.putExtra(r3, r4);
        r3 = "dialogId";
        r2.putExtra(r3, r8);
        r3 = r0.currentAccount;
        r5 = r46;
        r2.putExtra(r5, r3);
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r6 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r10 = 1;
        r2 = android.app.PendingIntent.getBroadcast(r3, r10, r2, r6);
        r7.setDeleteIntent(r2);
        if (r16 == 0) goto L_0x0997;
    L_0x098f:
        r2 = r0.notificationGroup;
        r7.setGroup(r2);
        r7.setGroupAlertBehavior(r10);
    L_0x0997:
        if (r44 == 0) goto L_0x099e;
    L_0x0999:
        r2 = r44;
        r7.addAction(r2);
    L_0x099e:
        r7.addAction(r1);
        r1 = r0.pushDialogs;
        r1 = r1.size();
        if (r1 != r10) goto L_0x09b5;
    L_0x09a9:
        r1 = android.text.TextUtils.isEmpty(r63);
        if (r1 != 0) goto L_0x09b5;
    L_0x09af:
        r1 = r63;
        r7.setSubText(r1);
        goto L_0x09b7;
    L_0x09b5:
        r1 = r63;
    L_0x09b7:
        if (r11 != 0) goto L_0x09bc;
    L_0x09b9:
        r7.setLocalOnly(r10);
    L_0x09bc:
        if (r40 == 0) goto L_0x09c3;
    L_0x09be:
        r13 = r40;
        r7.setLargeIcon(r13);
    L_0x09c3:
        r2 = 0;
        r3 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r2);
        if (r3 != 0) goto L_0x0a63;
    L_0x09ca:
        r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r2 != 0) goto L_0x0a63;
    L_0x09ce:
        r2 = r47;
        if (r2 == 0) goto L_0x0a63;
    L_0x09d2:
        r3 = r2.size();
        r6 = 0;
    L_0x09d7:
        if (r6 >= r3) goto L_0x0a63;
    L_0x09d9:
        r13 = r2.get(r6);
        r13 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r13;
        r14 = r13.buttons;
        r14 = r14.size();
        r15 = 0;
    L_0x09e6:
        if (r15 >= r14) goto L_0x0a4d;
    L_0x09e8:
        r10 = r13.buttons;
        r10 = r10.get(r15);
        r10 = (org.telegram.tgnet.TLRPC.KeyboardButton) r10;
        r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
        if (r1 == 0) goto L_0x0a34;
    L_0x09f4:
        r1 = new android.content.Intent;
        r30 = r2;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r33 = r3;
        r3 = org.telegram.messenger.NotificationCallbackReceiver.class;
        r1.<init>(r2, r3);
        r2 = r0.currentAccount;
        r1.putExtra(r5, r2);
        r2 = "did";
        r1.putExtra(r2, r8);
        r2 = r10.data;
        if (r2 == 0) goto L_0x0a14;
    L_0x0a0f:
        r3 = "data";
        r1.putExtra(r3, r2);
    L_0x0a14:
        r2 = "mid";
        r3 = r48;
        r1.putExtra(r2, r3);
        r2 = r10.text;
        r10 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r34 = r3;
        r3 = r0.lastButtonId;
        r46 = r5;
        r5 = r3 + 1;
        r0.lastButtonId = r5;
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r1 = android.app.PendingIntent.getBroadcast(r10, r3, r1, r5);
        r3 = 0;
        r7.addAction(r3, r2, r1);
        goto L_0x0a3f;
    L_0x0a34:
        r30 = r2;
        r33 = r3;
        r46 = r5;
        r34 = r48;
        r3 = 0;
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
    L_0x0a3f:
        r15 = r15 + 1;
        r1 = r63;
        r2 = r30;
        r3 = r33;
        r48 = r34;
        r5 = r46;
        r10 = 1;
        goto L_0x09e6;
    L_0x0a4d:
        r30 = r2;
        r33 = r3;
        r46 = r5;
        r34 = r48;
        r3 = 0;
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r6 = r6 + 1;
        r1 = r63;
        r3 = r33;
        r5 = r46;
        r10 = 1;
        goto L_0x09d7;
    L_0x0a63:
        r3 = 0;
        if (r32 != 0) goto L_0x0a8a;
    L_0x0a66:
        if (r27 == 0) goto L_0x0a8a;
    L_0x0a68:
        r2 = r27;
        r1 = r2.phone;
        if (r1 == 0) goto L_0x0a8a;
    L_0x0a6e:
        r1 = r1.length();
        if (r1 <= 0) goto L_0x0a8a;
    L_0x0a74:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r5 = "tel:+";
        r1.append(r5);
        r2 = r2.phone;
        r1.append(r2);
        r1 = r1.toString();
        r7.addPerson(r1);
    L_0x0a8a:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 26;
        if (r1 < r2) goto L_0x0a9f;
    L_0x0a90:
        if (r16 == 0) goto L_0x0a98;
    L_0x0a92:
        r1 = OTHER_NOTIFICATIONS_CHANNEL;
        r7.setChannelId(r1);
        goto L_0x0a9f;
    L_0x0a98:
        r1 = r31.getChannelId();
        r7.setChannelId(r1);
    L_0x0a9f:
        r1 = new org.telegram.messenger.NotificationsController$1NotificationHolder;
        r5 = r41.intValue();
        r6 = r7.build();
        r1.<init>(r5, r6);
        r5 = r20;
        r5.add(r1);
        r1 = r0.wearNotificationsIds;
        r6 = r41;
        r1.put(r8, r6);
        if (r11 == 0) goto L_0x0b33;
    L_0x0aba:
        if (r45 == 0) goto L_0x0b33;
    L_0x0abc:
        r1 = "reply";
        r6 = r39;
        r7 = r45;
        r7.put(r1, r6);	 Catch:{ JSONException -> 0x0b33 }
        r1 = "name";
        r7.put(r1, r12);	 Catch:{ JSONException -> 0x0b33 }
        r6 = r42;
        r1 = r43;
        r7.put(r6, r1);	 Catch:{ JSONException -> 0x0b33 }
        r1 = "max_date";
        r7.put(r1, r4);	 Catch:{ JSONException -> 0x0b33 }
        r1 = "id";
        r4 = java.lang.Math.abs(r11);	 Catch:{ JSONException -> 0x0b33 }
        r7.put(r1, r4);	 Catch:{ JSONException -> 0x0b33 }
        if (r38 == 0) goto L_0x0b08;
    L_0x0ae1:
        r1 = "photo";
        r4 = new java.lang.StringBuilder;	 Catch:{ JSONException -> 0x0b33 }
        r4.<init>();	 Catch:{ JSONException -> 0x0b33 }
        r14 = r38;
        r6 = r14.dc_id;	 Catch:{ JSONException -> 0x0b33 }
        r4.append(r6);	 Catch:{ JSONException -> 0x0b33 }
        r6 = r25;
        r4.append(r6);	 Catch:{ JSONException -> 0x0b33 }
        r8 = r14.volume_id;	 Catch:{ JSONException -> 0x0b33 }
        r4.append(r8);	 Catch:{ JSONException -> 0x0b33 }
        r4.append(r6);	 Catch:{ JSONException -> 0x0b33 }
        r8 = r14.secret;	 Catch:{ JSONException -> 0x0b33 }
        r4.append(r8);	 Catch:{ JSONException -> 0x0b33 }
        r4 = r4.toString();	 Catch:{ JSONException -> 0x0b33 }
        r7.put(r1, r4);	 Catch:{ JSONException -> 0x0b33 }
    L_0x0b08:
        if (r24 == 0) goto L_0x0b11;
    L_0x0b0a:
        r1 = "msgs";
        r4 = r24;
        r7.put(r1, r4);	 Catch:{ JSONException -> 0x0b33 }
    L_0x0b11:
        r1 = "type";
        if (r11 <= 0) goto L_0x0b1b;
    L_0x0b15:
        r4 = "user";
        r7.put(r1, r4);	 Catch:{ JSONException -> 0x0b33 }
        goto L_0x0b2d;
    L_0x0b1b:
        if (r11 >= 0) goto L_0x0b2d;
    L_0x0b1d:
        if (r57 != 0) goto L_0x0b28;
    L_0x0b1f:
        if (r29 == 0) goto L_0x0b22;
    L_0x0b21:
        goto L_0x0b28;
    L_0x0b22:
        r4 = "group";
        r7.put(r1, r4);	 Catch:{ JSONException -> 0x0b33 }
        goto L_0x0b2d;
    L_0x0b28:
        r4 = "channel";
        r7.put(r1, r4);	 Catch:{ JSONException -> 0x0b33 }
    L_0x0b2d:
        r8 = r18;
        r8.put(r7);	 Catch:{ JSONException -> 0x0b35 }
        goto L_0x0b35;
    L_0x0b33:
        r8 = r18;
    L_0x0b35:
        r14 = r28 + 1;
        r6 = r5;
        r7 = r8;
        r9 = r16;
        r3 = r21;
        r4 = r22;
        r13 = r23;
        r2 = r26;
        r1 = r31;
        r12 = r55;
        r5 = 0;
        r10 = 26;
        r11 = 1;
        goto L_0x00c6;
    L_0x0b4d:
        r31 = r1;
        r26 = r2;
        r5 = r6;
        r8 = r7;
        r16 = r9;
        r55 = r12;
        r3 = 0;
        if (r16 == 0) goto L_0x0b7e;
    L_0x0b5a:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0b74;
    L_0x0b5e:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "show summary with id ";
        r1.append(r2);
        r2 = r0.notificationId;
        r1.append(r2);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0b74:
        r1 = notificationManager;
        r2 = r0.notificationId;
        r4 = r31;
        r1.notify(r2, r4);
        goto L_0x0b85;
    L_0x0b7e:
        r1 = notificationManager;
        r2 = r0.notificationId;
        r1.cancel(r2);
    L_0x0b85:
        r1 = r5.size();
        r2 = 0;
    L_0x0b8a:
        if (r2 >= r1) goto L_0x0b98;
    L_0x0b8c:
        r4 = r5.get(r2);
        r4 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r4;
        r4.call();
        r2 = r2 + 1;
        goto L_0x0b8a;
    L_0x0b98:
        r1 = r26.size();
        if (r3 >= r1) goto L_0x0bcc;
    L_0x0b9e:
        r1 = r26;
        r2 = r1.valueAt(r3);
        r2 = (java.lang.Integer) r2;
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0bbe;
    L_0x0baa:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "cancel notification id ";
        r4.append(r5);
        r4.append(r2);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.w(r4);
    L_0x0bbe:
        r4 = notificationManager;
        r2 = r2.intValue();
        r4.cancel(r2);
        r3 = r3 + 1;
        r26 = r1;
        goto L_0x0b98;
    L_0x0bcc:
        if (r8 == 0) goto L_0x0bee;
    L_0x0bce:
        r1 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0bee }
        r1.<init>();	 Catch:{ Exception -> 0x0bee }
        r2 = "id";
        r3 = r55;
        r1.put(r2, r3);	 Catch:{ Exception -> 0x0bee }
        r2 = "n";
        r1.put(r2, r8);	 Catch:{ Exception -> 0x0bee }
        r2 = "/notify";
        r1 = r1.toString();	 Catch:{ Exception -> 0x0bee }
        r1 = r1.getBytes();	 Catch:{ Exception -> 0x0bee }
        r3 = "remote_notifications";
        org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r2, r1, r3);	 Catch:{ Exception -> 0x0bee }
    L_0x0bee:
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
