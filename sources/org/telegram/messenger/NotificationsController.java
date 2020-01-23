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
        if (!arrayList.isEmpty() && !AndroidUtilities.needShowPasscode() && !SharedConfig.isWaitingForPasscodeEnter) {
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
            int keyAt = sparseArray2.keyAt(i2);
            ArrayList arrayList3 = (ArrayList) sparseArray2.get(keyAt);
            int i3 = 0;
            while (i3 < arrayList3.size()) {
                long intValue = (long) ((Integer) arrayList3.get(i3)).intValue();
                if (keyAt != 0) {
                    intValue |= ((long) keyAt) << 32;
                }
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
                i3++;
                sparseArray2 = sparseArray;
            }
            i2++;
            sparseArray2 = sparseArray;
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
        r0 = r17;
    L_0x00eb:
        r17 = r0;
        goto L_0x0118;
    L_0x00ee:
        if (r17 == 0) goto L_0x00f1;
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
        if (r18 != 0) goto L_0x0189;
    L_0x0184:
        r0 = r12.messageOwner;
        r0 = r0.from_scheduled;
        goto L_0x018b;
    L_0x0189:
        r0 = r18;
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
        r18 = r0;
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
        if (r0 != 0) goto L_0x0203;
    L_0x01ed:
        r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode();
        if (r0 != 0) goto L_0x0203;
    L_0x01f3:
        r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r0 != 0) goto L_0x0203;
    L_0x01f7:
        r0 = new org.telegram.messenger.-$$Lambda$NotificationsController$vBhFCZdXUS15Ipx-fzqzTMIuA3o;
        r1 = r32;
        r14 = r26;
        r0.<init>(r8, r1, r14);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x0203:
        if (r33 != 0) goto L_0x0207;
    L_0x0205:
        if (r18 == 0) goto L_0x02ae;
    L_0x0207:
        if (r17 == 0) goto L_0x0215;
    L_0x0209:
        r0 = r8.delayedPushMessages;
        r0.clear();
        r0 = r8.notifyCheck;
        r8.showOrUpdateNotification(r0);
        goto L_0x02ae;
    L_0x0215:
        if (r16 == 0) goto L_0x02ae;
    L_0x0217:
        r0 = r31;
        r1 = 0;
        r0 = r0.get(r1);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.getDialogId();
        r2 = r8.total_unread_count;
        r3 = r8.getNotifyOverride(r11, r0);
        r4 = -1;
        if (r3 != r4) goto L_0x0233;
    L_0x022d:
        r3 = r8.isGlobalNotificationsEnabled(r0);
    L_0x0231:
        r12 = r3;
        goto L_0x023a;
    L_0x0233:
        r4 = 2;
        if (r3 == r4) goto L_0x0238;
    L_0x0236:
        r3 = 1;
        goto L_0x0231;
    L_0x0238:
        r3 = 0;
        goto L_0x0231;
    L_0x023a:
        r3 = r8.pushDialogs;
        r3 = r3.get(r0);
        r3 = (java.lang.Integer) r3;
        if (r3 == 0) goto L_0x024b;
    L_0x0244:
        r4 = r3.intValue();
        r5 = 1;
        r4 = r4 + r5;
        goto L_0x024d;
    L_0x024b:
        r5 = 1;
        r4 = 1;
    L_0x024d:
        r4 = java.lang.Integer.valueOf(r4);
        r6 = r8.notifyCheck;
        if (r6 == 0) goto L_0x0269;
    L_0x0255:
        if (r12 != 0) goto L_0x0269;
    L_0x0257:
        r6 = r8.pushDialogsOverrideMention;
        r6 = r6.get(r0);
        r6 = (java.lang.Integer) r6;
        if (r6 == 0) goto L_0x0269;
    L_0x0261:
        r7 = r6.intValue();
        if (r7 == 0) goto L_0x0269;
    L_0x0267:
        r4 = r6;
        r12 = 1;
    L_0x0269:
        if (r12 == 0) goto L_0x0284;
    L_0x026b:
        if (r3 == 0) goto L_0x0276;
    L_0x026d:
        r5 = r8.total_unread_count;
        r3 = r3.intValue();
        r5 = r5 - r3;
        r8.total_unread_count = r5;
    L_0x0276:
        r3 = r8.total_unread_count;
        r5 = r4.intValue();
        r3 = r3 + r5;
        r8.total_unread_count = r3;
        r3 = r8.pushDialogs;
        r3.put(r0, r4);
    L_0x0284:
        r0 = r8.total_unread_count;
        if (r2 == r0) goto L_0x02a0;
    L_0x0288:
        r0 = r8.delayedPushMessages;
        r0.clear();
        r0 = r8.notifyCheck;
        r8.showOrUpdateNotification(r0);
        r0 = r8.pushDialogs;
        r0 = r0.size();
        r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$R3R5Z37efc0XPsswynnBTmucwac;
        r1.<init>(r8, r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x02a0:
        r0 = 0;
        r8.notifyCheck = r0;
        r0 = r8.showBadgeNumber;
        if (r0 == 0) goto L_0x02ae;
    L_0x02a7:
        r0 = r30.getTotalAllUnreadCount();
        r8.setBadge(r0);
    L_0x02ae:
        if (r35 == 0) goto L_0x02b3;
    L_0x02b0:
        r35.countDown();
    L_0x02b3:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processNewMessages$16$NotificationsController(java.util.ArrayList, java.util.ArrayList, boolean, boolean, java.util.concurrent.CountDownLatch):void");
    }

    public /* synthetic */ void lambda$null$14$NotificationsController(ArrayList arrayList, int i) {
        this.popupMessages.addAll(0, arrayList);
        if (!ApplicationLoader.mainInterfacePaused && ApplicationLoader.isScreenOn) {
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
        r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode();
        if (r1 != 0) goto L_0x0bbc;
    L_0x0008:
        r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r1 == 0) goto L_0x000e;
    L_0x000c:
        goto L_0x0bbc;
    L_0x000e:
        r1 = r0.messageOwner;
        r2 = r1.dialog_id;
        r1 = r1.to_id;
        r4 = r1.chat_id;
        if (r4 == 0) goto L_0x0019;
    L_0x0018:
        goto L_0x001b;
    L_0x0019:
        r4 = r1.channel_id;
    L_0x001b:
        r1 = r0.messageOwner;
        r1 = r1.to_id;
        r1 = r1.user_id;
        r5 = 1;
        r6 = 0;
        if (r19 == 0) goto L_0x0027;
    L_0x0025:
        r19[r6] = r5;
    L_0x0027:
        r7 = r16.getAccountInstance();
        r7 = r7.getNotificationsSettings();
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "content_preview_";
        r8.append(r9);
        r8.append(r2);
        r8 = r8.toString();
        r8 = r7.getBoolean(r8, r5);
        r9 = r17.isFcmMessage();
        r10 = NUM; // 0x7f0e0647 float:1.8878297E38 double:1.0531629506E-314;
        r11 = "Message";
        r12 = 27;
        r13 = 2;
        if (r9 == 0) goto L_0x00e4;
    L_0x0052:
        if (r4 != 0) goto L_0x0071;
    L_0x0054:
        if (r1 == 0) goto L_0x0071;
    L_0x0056:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 <= r12) goto L_0x005e;
    L_0x005a:
        r1 = r0.localName;
        r18[r6] = r1;
    L_0x005e:
        if (r8 == 0) goto L_0x0068;
    L_0x0060:
        r1 = "EnablePreviewAll";
        r1 = r7.getBoolean(r1, r5);
        if (r1 != 0) goto L_0x00df;
    L_0x0068:
        if (r19 == 0) goto L_0x006c;
    L_0x006a:
        r19[r6] = r6;
    L_0x006c:
        r0 = org.telegram.messenger.LocaleController.getString(r11, r10);
        return r0;
    L_0x0071:
        if (r4 == 0) goto L_0x00df;
    L_0x0073:
        r1 = r0.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x008b;
    L_0x007b:
        r1 = r17.isMegagroup();
        if (r1 == 0) goto L_0x0082;
    L_0x0081:
        goto L_0x008b;
    L_0x0082:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 <= r12) goto L_0x008f;
    L_0x0086:
        r1 = r0.localName;
        r18[r6] = r1;
        goto L_0x008f;
    L_0x008b:
        r1 = r0.localUserName;
        r18[r6] = r1;
    L_0x008f:
        if (r8 == 0) goto L_0x00a9;
    L_0x0091:
        r1 = r0.localChannel;
        if (r1 != 0) goto L_0x009d;
    L_0x0095:
        r1 = "EnablePreviewGroup";
        r1 = r7.getBoolean(r1, r5);
        if (r1 == 0) goto L_0x00a9;
    L_0x009d:
        r1 = r0.localChannel;
        if (r1 == 0) goto L_0x00df;
    L_0x00a1:
        r1 = "EnablePreviewChannel";
        r1 = r7.getBoolean(r1, r5);
        if (r1 != 0) goto L_0x00df;
    L_0x00a9:
        if (r19 == 0) goto L_0x00ad;
    L_0x00ab:
        r19[r6] = r6;
    L_0x00ad:
        r1 = r17.isMegagroup();
        if (r1 != 0) goto L_0x00cb;
    L_0x00b3:
        r1 = r0.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x00cb;
    L_0x00bb:
        r1 = NUM; // 0x7f0e0272 float:1.8876307E38 double:1.053162466E-314;
        r2 = new java.lang.Object[r5];
        r0 = r0.localName;
        r2[r6] = r0;
        r0 = "ChannelMessageNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x00cb:
        r1 = NUM; // 0x7f0e072b float:1.887876E38 double:1.053163063E-314;
        r2 = new java.lang.Object[r13];
        r3 = r0.localUserName;
        r2[r6] = r3;
        r0 = r0.localName;
        r2[r5] = r0;
        r0 = "NotificationMessageGroupNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x00df:
        r0 = r0.messageOwner;
        r0 = r0.message;
        return r0;
    L_0x00e4:
        if (r1 != 0) goto L_0x00fa;
    L_0x00e6:
        r1 = r17.isFromUser();
        if (r1 != 0) goto L_0x00f5;
    L_0x00ec:
        r1 = r17.getId();
        if (r1 >= 0) goto L_0x00f3;
    L_0x00f2:
        goto L_0x00f5;
    L_0x00f3:
        r1 = -r4;
        goto L_0x0108;
    L_0x00f5:
        r1 = r0.messageOwner;
        r1 = r1.from_id;
        goto L_0x0108;
    L_0x00fa:
        r9 = r16.getUserConfig();
        r9 = r9.getClientUserId();
        if (r1 != r9) goto L_0x0108;
    L_0x0104:
        r1 = r0.messageOwner;
        r1 = r1.from_id;
    L_0x0108:
        r14 = 0;
        r9 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1));
        if (r9 != 0) goto L_0x0116;
    L_0x010e:
        if (r4 == 0) goto L_0x0113;
    L_0x0110:
        r2 = -r4;
        r2 = (long) r2;
        goto L_0x0116;
    L_0x0113:
        if (r1 == 0) goto L_0x0116;
    L_0x0115:
        r2 = (long) r1;
    L_0x0116:
        r9 = 0;
        if (r1 <= 0) goto L_0x013a;
    L_0x0119:
        r14 = r16.getMessagesController();
        r15 = java.lang.Integer.valueOf(r1);
        r14 = r14.getUser(r15);
        if (r14 == 0) goto L_0x014e;
    L_0x0127:
        r14 = org.telegram.messenger.UserObject.getUserName(r14);
        if (r4 == 0) goto L_0x0130;
    L_0x012d:
        r18[r6] = r14;
        goto L_0x014f;
    L_0x0130:
        r15 = android.os.Build.VERSION.SDK_INT;
        if (r15 <= r12) goto L_0x0137;
    L_0x0134:
        r18[r6] = r14;
        goto L_0x014f;
    L_0x0137:
        r18[r6] = r9;
        goto L_0x014f;
    L_0x013a:
        r14 = r16.getMessagesController();
        r15 = -r1;
        r15 = java.lang.Integer.valueOf(r15);
        r14 = r14.getChat(r15);
        if (r14 == 0) goto L_0x014e;
    L_0x0149:
        r14 = r14.title;
        r18[r6] = r14;
        goto L_0x014f;
    L_0x014e:
        r14 = r9;
    L_0x014f:
        if (r14 != 0) goto L_0x0152;
    L_0x0151:
        return r9;
    L_0x0152:
        if (r4 == 0) goto L_0x0174;
    L_0x0154:
        r15 = r16.getMessagesController();
        r10 = java.lang.Integer.valueOf(r4);
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
        r18[r6] = r9;
        goto L_0x0175;
    L_0x0174:
        r10 = r9;
    L_0x0175:
        r3 = (int) r2;
        if (r3 != 0) goto L_0x0184;
    L_0x0178:
        r18[r6] = r9;
        r0 = NUM; // 0x7f0e0714 float:1.8878713E38 double:1.053163052E-314;
        r1 = "NotificationHiddenMessage";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0184:
        r2 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r2 == 0) goto L_0x0190;
    L_0x018a:
        r2 = r10.megagroup;
        if (r2 != 0) goto L_0x0190;
    L_0x018e:
        r2 = 1;
        goto L_0x0191;
    L_0x0190:
        r2 = 0;
    L_0x0191:
        if (r8 == 0) goto L_0x0bb0;
    L_0x0193:
        if (r4 != 0) goto L_0x019f;
    L_0x0195:
        if (r1 == 0) goto L_0x019f;
    L_0x0197:
        r3 = "EnablePreviewAll";
        r3 = r7.getBoolean(r3, r5);
        if (r3 != 0) goto L_0x01b5;
    L_0x019f:
        if (r4 == 0) goto L_0x0bb0;
    L_0x01a1:
        if (r2 != 0) goto L_0x01ab;
    L_0x01a3:
        r3 = "EnablePreviewGroup";
        r3 = r7.getBoolean(r3, r5);
        if (r3 != 0) goto L_0x01b5;
    L_0x01ab:
        if (r2 == 0) goto L_0x0bb0;
    L_0x01ad:
        r2 = "EnablePreviewChannel";
        r2 = r7.getBoolean(r2, r5);
        if (r2 == 0) goto L_0x0bb0;
    L_0x01b5:
        r2 = r0.messageOwner;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        r4 = "🖼 ";
        r7 = 19;
        if (r3 == 0) goto L_0x09d0;
    L_0x01c0:
        r18[r6] = r9;
        r2 = r2.action;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r3 != 0) goto L_0x09c2;
    L_0x01c8:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r3 == 0) goto L_0x01ce;
    L_0x01cc:
        goto L_0x09c2;
    L_0x01ce:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
        if (r3 == 0) goto L_0x01e0;
    L_0x01d2:
        r0 = NUM; // 0x7f0e0706 float:1.8878684E38 double:1.053163045E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r14;
        r2 = "NotificationContactNewPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x01e0:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
        r8 = 3;
        if (r3 == 0) goto L_0x023f;
    L_0x01e5:
        r1 = NUM; // 0x7f0e0d06 float:1.88818E38 double:1.053163804E-314;
        r2 = new java.lang.Object[r13];
        r3 = org.telegram.messenger.LocaleController.getInstance();
        r3 = r3.formatterYear;
        r4 = r0.messageOwner;
        r4 = r4.date;
        r9 = (long) r4;
        r11 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r9 = r9 * r11;
        r3 = r3.format(r9);
        r2[r6] = r3;
        r3 = org.telegram.messenger.LocaleController.getInstance();
        r3 = r3.formatterDay;
        r4 = r0.messageOwner;
        r4 = r4.date;
        r9 = (long) r4;
        r9 = r9 * r11;
        r3 = r3.format(r9);
        r2[r5] = r3;
        r3 = "formatDateAtTime";
        r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2);
        r2 = NUM; // 0x7f0e0746 float:1.8878814E38 double:1.0531630766E-314;
        r3 = 4;
        r3 = new java.lang.Object[r3];
        r4 = r16.getUserConfig();
        r4 = r4.getCurrentUser();
        r4 = r4.first_name;
        r3[r6] = r4;
        r3[r5] = r1;
        r0 = r0.messageOwner;
        r0 = r0.action;
        r1 = r0.title;
        r3[r13] = r1;
        r0 = r0.address;
        r3[r8] = r0;
        r0 = "NotificationUnrecognizedDevice";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        return r0;
    L_0x023f:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r3 != 0) goto L_0x09bb;
    L_0x0243:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
        if (r3 == 0) goto L_0x0249;
    L_0x0247:
        goto L_0x09bb;
    L_0x0249:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r3 == 0) goto L_0x0263;
    L_0x024d:
        r1 = r2.reason;
        r0 = r17.isOut();
        if (r0 != 0) goto L_0x0ba5;
    L_0x0255:
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        if (r0 == 0) goto L_0x0ba5;
    L_0x0259:
        r0 = NUM; // 0x7f0e020b float:1.8876098E38 double:1.053162415E-314;
        r1 = "CallMessageIncomingMissed";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0263:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r3 == 0) goto L_0x0367;
    L_0x0267:
        r3 = r2.user_id;
        if (r3 != 0) goto L_0x0283;
    L_0x026b:
        r2 = r2.users;
        r2 = r2.size();
        if (r2 != r5) goto L_0x0283;
    L_0x0273:
        r2 = r0.messageOwner;
        r2 = r2.action;
        r2 = r2.users;
        r2 = r2.get(r6);
        r2 = (java.lang.Integer) r2;
        r3 = r2.intValue();
    L_0x0283:
        if (r3 == 0) goto L_0x0312;
    L_0x0285:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x02a3;
    L_0x028d:
        r0 = r10.megagroup;
        if (r0 != 0) goto L_0x02a3;
    L_0x0291:
        r0 = NUM; // 0x7f0e0241 float:1.8876208E38 double:1.0531624417E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "ChannelAddedByNotification";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x02a3:
        r0 = r16.getUserConfig();
        r0 = r0.getClientUserId();
        if (r3 != r0) goto L_0x02bf;
    L_0x02ad:
        r0 = NUM; // 0x7f0e0716 float:1.8878717E38 double:1.053163053E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationInvitedToGroup";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x02bf:
        r0 = r16.getMessagesController();
        r2 = java.lang.Integer.valueOf(r3);
        r0 = r0.getUser(r2);
        if (r0 != 0) goto L_0x02ce;
    L_0x02cd:
        return r9;
    L_0x02ce:
        r2 = r0.id;
        if (r1 != r2) goto L_0x02fa;
    L_0x02d2:
        r0 = r10.megagroup;
        if (r0 == 0) goto L_0x02e8;
    L_0x02d6:
        r0 = NUM; // 0x7f0e070b float:1.8878694E38 double:1.0531630474E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationGroupAddSelfMega";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x02e8:
        r0 = NUM; // 0x7f0e070a float:1.8878692E38 double:1.053163047E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationGroupAddSelf";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x02fa:
        r1 = NUM; // 0x7f0e0709 float:1.887869E38 double:1.0531630464E-314;
        r2 = new java.lang.Object[r8];
        r2[r6] = r14;
        r3 = r10.title;
        r2[r5] = r3;
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r2[r13] = r0;
        r0 = "NotificationGroupAddMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x0312:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = 0;
    L_0x0318:
        r3 = r0.messageOwner;
        r3 = r3.action;
        r3 = r3.users;
        r3 = r3.size();
        if (r2 >= r3) goto L_0x034f;
    L_0x0324:
        r3 = r16.getMessagesController();
        r4 = r0.messageOwner;
        r4 = r4.action;
        r4 = r4.users;
        r4 = r4.get(r2);
        r4 = (java.lang.Integer) r4;
        r3 = r3.getUser(r4);
        if (r3 == 0) goto L_0x034c;
    L_0x033a:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
        r4 = r1.length();
        if (r4 == 0) goto L_0x0349;
    L_0x0344:
        r4 = ", ";
        r1.append(r4);
    L_0x0349:
        r1.append(r3);
    L_0x034c:
        r2 = r2 + 1;
        goto L_0x0318;
    L_0x034f:
        r0 = NUM; // 0x7f0e0709 float:1.887869E38 double:1.0531630464E-314;
        r2 = new java.lang.Object[r8];
        r2[r6] = r14;
        r3 = r10.title;
        r2[r5] = r3;
        r1 = r1.toString();
        r2[r13] = r1;
        r1 = "NotificationGroupAddMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        return r0;
    L_0x0367:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
        if (r3 == 0) goto L_0x037d;
    L_0x036b:
        r0 = NUM; // 0x7f0e0717 float:1.8878719E38 double:1.0531630534E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationInvitedToGroupByLink";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x037d:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
        if (r3 == 0) goto L_0x0393;
    L_0x0381:
        r0 = NUM; // 0x7f0e0707 float:1.8878686E38 double:1.0531630455E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r2.title;
        r1[r5] = r2;
        r2 = "NotificationEditedGroupName";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0393:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
        if (r3 != 0) goto L_0x098d;
    L_0x0397:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
        if (r3 == 0) goto L_0x039d;
    L_0x039b:
        goto L_0x098d;
    L_0x039d:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r3 == 0) goto L_0x0406;
    L_0x03a1:
        r2 = r2.user_id;
        r3 = r16.getUserConfig();
        r3 = r3.getClientUserId();
        if (r2 != r3) goto L_0x03bf;
    L_0x03ad:
        r0 = NUM; // 0x7f0e0710 float:1.8878705E38 double:1.05316305E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationGroupKickYou";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x03bf:
        r2 = r0.messageOwner;
        r2 = r2.action;
        r2 = r2.user_id;
        if (r2 != r1) goto L_0x03d9;
    L_0x03c7:
        r0 = NUM; // 0x7f0e0711 float:1.8878707E38 double:1.0531630504E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationGroupLeftMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x03d9:
        r1 = r16.getMessagesController();
        r0 = r0.messageOwner;
        r0 = r0.action;
        r0 = r0.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r1.getUser(r0);
        if (r0 != 0) goto L_0x03ee;
    L_0x03ed:
        return r9;
    L_0x03ee:
        r1 = NUM; // 0x7f0e070f float:1.8878703E38 double:1.0531630494E-314;
        r2 = new java.lang.Object[r8];
        r2[r6] = r14;
        r3 = r10.title;
        r2[r5] = r3;
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r2[r13] = r0;
        r0 = "NotificationGroupKickMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x0406:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
        if (r1 == 0) goto L_0x0411;
    L_0x040a:
        r0 = r0.messageText;
        r0 = r0.toString();
        return r0;
    L_0x0411:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r1 == 0) goto L_0x041c;
    L_0x0415:
        r0 = r0.messageText;
        r0 = r0.toString();
        return r0;
    L_0x041c:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r1 == 0) goto L_0x0430;
    L_0x0420:
        r0 = NUM; // 0x7f0e0082 float:1.8875301E38 double:1.053162221E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "ActionMigrateFromGroupNotify";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0430:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r1 == 0) goto L_0x0444;
    L_0x0434:
        r0 = NUM; // 0x7f0e0082 float:1.8875301E38 double:1.053162221E-314;
        r1 = new java.lang.Object[r5];
        r2 = r2.title;
        r1[r6] = r2;
        r2 = "ActionMigrateFromGroupNotify";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0444:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
        if (r1 == 0) goto L_0x044f;
    L_0x0448:
        r0 = r0.messageText;
        r0 = r0.toString();
        return r0;
    L_0x044f:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r1 == 0) goto L_0x0ba5;
    L_0x0453:
        r1 = 20;
        if (r10 == 0) goto L_0x070d;
    L_0x0457:
        r2 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r2 == 0) goto L_0x0461;
    L_0x045d:
        r2 = r10.megagroup;
        if (r2 == 0) goto L_0x070d;
    L_0x0461:
        r0 = r0.replyMessageObject;
        if (r0 != 0) goto L_0x0477;
    L_0x0465:
        r0 = NUM; // 0x7f0e06f1 float:1.8878642E38 double:1.0531630346E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0477:
        r2 = r0.isMusic();
        if (r2 == 0) goto L_0x048f;
    L_0x047d:
        r0 = NUM; // 0x7f0e06ef float:1.8878638E38 double:1.0531630336E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedMusic";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x048f:
        r2 = r0.isVideo();
        r3 = NUM; // 0x7f0e06ff float:1.887867E38 double:1.0531630415E-314;
        r9 = "NotificationActionPinnedText";
        if (r2 == 0) goto L_0x04df;
    L_0x049a:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x04cd;
    L_0x049e:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x04cd;
    L_0x04a8:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "📹 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r1 = new java.lang.Object[r8];
        r1[r6] = r14;
        r1[r5] = r0;
        r0 = r10.title;
        r1[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r3, r1);
        return r0;
    L_0x04cd:
        r0 = NUM; // 0x7f0e0701 float:1.8878674E38 double:1.0531630425E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedVideo";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x04df:
        r2 = r0.isGif();
        if (r2 == 0) goto L_0x052a;
    L_0x04e5:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0518;
    L_0x04e9:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0518;
    L_0x04f3:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "🎬 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r1 = new java.lang.Object[r8];
        r1[r6] = r14;
        r1[r5] = r0;
        r0 = r10.title;
        r1[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r3, r1);
        return r0;
    L_0x0518:
        r0 = NUM; // 0x7f0e06eb float:1.887863E38 double:1.0531630316E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedGif";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x052a:
        r2 = r0.isVoice();
        if (r2 == 0) goto L_0x0542;
    L_0x0530:
        r0 = NUM; // 0x7f0e0703 float:1.8878678E38 double:1.0531630435E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedVoice";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0542:
        r2 = r0.isRoundVideo();
        if (r2 == 0) goto L_0x055a;
    L_0x0548:
        r0 = NUM; // 0x7f0e06f9 float:1.8878658E38 double:1.0531630385E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedRound";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x055a:
        r2 = r0.isSticker();
        if (r2 != 0) goto L_0x06e1;
    L_0x0560:
        r2 = r0.isAnimatedSticker();
        if (r2 == 0) goto L_0x0568;
    L_0x0566:
        goto L_0x06e1;
    L_0x0568:
        r2 = r0.messageOwner;
        r11 = r2.media;
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r12 == 0) goto L_0x05b3;
    L_0x0570:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x05a1;
    L_0x0574:
        r1 = r2.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x05a1;
    L_0x057c:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "📎 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r1 = new java.lang.Object[r8];
        r1[r6] = r14;
        r1[r5] = r0;
        r0 = r10.title;
        r1[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r3, r1);
        return r0;
    L_0x05a1:
        r0 = NUM; // 0x7f0e06e1 float:1.887861E38 double:1.0531630267E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedFile";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x05b3:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r12 != 0) goto L_0x06cf;
    L_0x05b7:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r12 == 0) goto L_0x05bd;
    L_0x05bb:
        goto L_0x06cf;
    L_0x05bd:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r12 == 0) goto L_0x05d3;
    L_0x05c1:
        r0 = NUM; // 0x7f0e06e9 float:1.8878625E38 double:1.0531630306E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedGeoLive";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x05d3:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r12 == 0) goto L_0x05f5;
    L_0x05d7:
        r11 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r11;
        r0 = NUM; // 0x7f0e06df float:1.8878605E38 double:1.0531630257E-314;
        r1 = new java.lang.Object[r8];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = r11.first_name;
        r3 = r11.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r3);
        r1[r13] = r2;
        r2 = "NotificationActionPinnedContact2";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x05f5:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r12 == 0) goto L_0x062d;
    L_0x05f9:
        r11 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r11;
        r0 = r11.poll;
        r1 = r0.quiz;
        if (r1 == 0) goto L_0x0617;
    L_0x0601:
        r1 = NUM; // 0x7f0e06f7 float:1.8878654E38 double:1.0531630375E-314;
        r2 = new java.lang.Object[r8];
        r2[r6] = r14;
        r3 = r10.title;
        r2[r5] = r3;
        r0 = r0.question;
        r2[r13] = r0;
        r0 = "NotificationActionPinnedQuiz2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x0617:
        r1 = NUM; // 0x7f0e06f5 float:1.887865E38 double:1.0531630366E-314;
        r2 = new java.lang.Object[r8];
        r2[r6] = r14;
        r3 = r10.title;
        r2[r5] = r3;
        r0 = r0.question;
        r2[r13] = r0;
        r0 = "NotificationActionPinnedPoll2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x062d:
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r12 == 0) goto L_0x0671;
    L_0x0631:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x065f;
    L_0x0635:
        r1 = r2.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x065f;
    L_0x063d:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r4);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r1 = new java.lang.Object[r8];
        r1[r6] = r14;
        r1[r5] = r0;
        r0 = r10.title;
        r1[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r3, r1);
        return r0;
    L_0x065f:
        r0 = NUM; // 0x7f0e06f3 float:1.8878646E38 double:1.0531630356E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0671:
        r2 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r2 == 0) goto L_0x0687;
    L_0x0675:
        r0 = NUM; // 0x7f0e06e3 float:1.8878613E38 double:1.0531630277E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedGame";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0687:
        r2 = r0.messageText;
        if (r2 == 0) goto L_0x06bd;
    L_0x068b:
        r2 = r2.length();
        if (r2 <= 0) goto L_0x06bd;
    L_0x0691:
        r0 = r0.messageText;
        r2 = r0.length();
        if (r2 <= r1) goto L_0x06ae;
    L_0x0699:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r0.subSequence(r6, r1);
        r2.append(r0);
        r0 = "...";
        r2.append(r0);
        r0 = r2.toString();
    L_0x06ae:
        r1 = new java.lang.Object[r8];
        r1[r6] = r14;
        r1[r5] = r0;
        r0 = r10.title;
        r1[r13] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r9, r3, r1);
        return r0;
    L_0x06bd:
        r0 = NUM; // 0x7f0e06f1 float:1.8878642E38 double:1.0531630346E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x06cf:
        r0 = NUM; // 0x7f0e06e7 float:1.8878621E38 double:1.0531630296E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedGeo";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x06e1:
        r0 = r0.getStickerEmoji();
        if (r0 == 0) goto L_0x06fb;
    L_0x06e7:
        r1 = NUM; // 0x7f0e06fd float:1.8878666E38 double:1.0531630405E-314;
        r2 = new java.lang.Object[r8];
        r2[r6] = r14;
        r3 = r10.title;
        r2[r5] = r3;
        r2[r13] = r0;
        r0 = "NotificationActionPinnedStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x06fb:
        r0 = NUM; // 0x7f0e06fb float:1.8878662E38 double:1.0531630395E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x070d:
        r0 = r0.replyMessageObject;
        if (r0 != 0) goto L_0x0721;
    L_0x0711:
        r0 = NUM; // 0x7f0e06f2 float:1.8878644E38 double:1.053163035E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedNoTextChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0721:
        r2 = r0.isMusic();
        if (r2 == 0) goto L_0x0737;
    L_0x0727:
        r0 = NUM; // 0x7f0e06f0 float:1.887864E38 double:1.053163034E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedMusicChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0737:
        r2 = r0.isVideo();
        r3 = NUM; // 0x7f0e0700 float:1.8878672E38 double:1.053163042E-314;
        r8 = "NotificationActionPinnedTextChannel";
        if (r2 == 0) goto L_0x0783;
    L_0x0742:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0773;
    L_0x0746:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0773;
    L_0x0750:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "📹 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r1 = new java.lang.Object[r13];
        r2 = r10.title;
        r1[r6] = r2;
        r1[r5] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1);
        return r0;
    L_0x0773:
        r0 = NUM; // 0x7f0e0702 float:1.8878676E38 double:1.053163043E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedVideoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0783:
        r2 = r0.isGif();
        if (r2 == 0) goto L_0x07ca;
    L_0x0789:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x07ba;
    L_0x078d:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x07ba;
    L_0x0797:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "🎬 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r1 = new java.lang.Object[r13];
        r2 = r10.title;
        r1[r6] = r2;
        r1[r5] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1);
        return r0;
    L_0x07ba:
        r0 = NUM; // 0x7f0e06ec float:1.8878632E38 double:1.053163032E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedGifChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x07ca:
        r2 = r0.isVoice();
        if (r2 == 0) goto L_0x07e0;
    L_0x07d0:
        r0 = NUM; // 0x7f0e0704 float:1.887868E38 double:1.053163044E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedVoiceChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x07e0:
        r2 = r0.isRoundVideo();
        if (r2 == 0) goto L_0x07f6;
    L_0x07e6:
        r0 = NUM; // 0x7f0e06fa float:1.887866E38 double:1.053163039E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedRoundChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x07f6:
        r2 = r0.isSticker();
        if (r2 != 0) goto L_0x0965;
    L_0x07fc:
        r2 = r0.isAnimatedSticker();
        if (r2 == 0) goto L_0x0804;
    L_0x0802:
        goto L_0x0965;
    L_0x0804:
        r2 = r0.messageOwner;
        r9 = r2.media;
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r11 == 0) goto L_0x084b;
    L_0x080c:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x083b;
    L_0x0810:
        r1 = r2.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x083b;
    L_0x0818:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "📎 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r1 = new java.lang.Object[r13];
        r2 = r10.title;
        r1[r6] = r2;
        r1[r5] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1);
        return r0;
    L_0x083b:
        r0 = NUM; // 0x7f0e06e2 float:1.8878611E38 double:1.053163027E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedFileChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x084b:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r11 != 0) goto L_0x0955;
    L_0x084f:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r11 == 0) goto L_0x0855;
    L_0x0853:
        goto L_0x0955;
    L_0x0855:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r11 == 0) goto L_0x0869;
    L_0x0859:
        r0 = NUM; // 0x7f0e06ea float:1.8878628E38 double:1.053163031E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedGeoLiveChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0869:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r11 == 0) goto L_0x0889;
    L_0x086d:
        r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r9;
        r0 = NUM; // 0x7f0e06e0 float:1.8878607E38 double:1.053163026E-314;
        r1 = new java.lang.Object[r13];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = r9.first_name;
        r3 = r9.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r3);
        r1[r5] = r2;
        r2 = "NotificationActionPinnedContactChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0889:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r11 == 0) goto L_0x08bd;
    L_0x088d:
        r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r9;
        r0 = r9.poll;
        r1 = r0.quiz;
        if (r1 == 0) goto L_0x08a9;
    L_0x0895:
        r1 = NUM; // 0x7f0e06f8 float:1.8878656E38 double:1.053163038E-314;
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r6] = r3;
        r0 = r0.question;
        r2[r5] = r0;
        r0 = "NotificationActionPinnedQuizChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x08a9:
        r1 = NUM; // 0x7f0e06f6 float:1.8878652E38 double:1.053163037E-314;
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r6] = r3;
        r0 = r0.question;
        r2[r5] = r0;
        r0 = "NotificationActionPinnedPollChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x08bd:
        r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r11 == 0) goto L_0x08fd;
    L_0x08c1:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x08ed;
    L_0x08c5:
        r1 = r2.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x08ed;
    L_0x08cd:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r4);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        r1 = new java.lang.Object[r13];
        r2 = r10.title;
        r1[r6] = r2;
        r1[r5] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1);
        return r0;
    L_0x08ed:
        r0 = NUM; // 0x7f0e06f4 float:1.8878648E38 double:1.053163036E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedPhotoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x08fd:
        r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r2 == 0) goto L_0x0911;
    L_0x0901:
        r0 = NUM; // 0x7f0e06e4 float:1.8878615E38 double:1.053163028E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedGameChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0911:
        r2 = r0.messageText;
        if (r2 == 0) goto L_0x0945;
    L_0x0915:
        r2 = r2.length();
        if (r2 <= 0) goto L_0x0945;
    L_0x091b:
        r0 = r0.messageText;
        r2 = r0.length();
        if (r2 <= r1) goto L_0x0938;
    L_0x0923:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r0.subSequence(r6, r1);
        r2.append(r0);
        r0 = "...";
        r2.append(r0);
        r0 = r2.toString();
    L_0x0938:
        r1 = new java.lang.Object[r13];
        r2 = r10.title;
        r1[r6] = r2;
        r1[r5] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1);
        return r0;
    L_0x0945:
        r0 = NUM; // 0x7f0e06f2 float:1.8878644E38 double:1.053163035E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedNoTextChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0955:
        r0 = NUM; // 0x7f0e06e8 float:1.8878623E38 double:1.05316303E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedGeoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x0965:
        r0 = r0.getStickerEmoji();
        if (r0 == 0) goto L_0x097d;
    L_0x096b:
        r1 = NUM; // 0x7f0e06fe float:1.8878668E38 double:1.053163041E-314;
        r2 = new java.lang.Object[r13];
        r3 = r10.title;
        r2[r6] = r3;
        r2[r5] = r0;
        r0 = "NotificationActionPinnedStickerEmojiChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x097d:
        r0 = NUM; // 0x7f0e06fc float:1.8878664E38 double:1.05316304E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedStickerChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x098d:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x09a9;
    L_0x0995:
        r0 = r10.megagroup;
        if (r0 != 0) goto L_0x09a9;
    L_0x0999:
        r0 = NUM; // 0x7f0e0281 float:1.8876338E38 double:1.0531624733E-314;
        r1 = new java.lang.Object[r5];
        r2 = r10.title;
        r1[r6] = r2;
        r2 = "ChannelPhotoEditNotification";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x09a9:
        r0 = NUM; // 0x7f0e0708 float:1.8878688E38 double:1.053163046E-314;
        r1 = new java.lang.Object[r13];
        r1[r6] = r14;
        r2 = r10.title;
        r1[r5] = r2;
        r2 = "NotificationEditedGroupPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x09bb:
        r0 = r0.messageText;
        r0 = r0.toString();
        return r0;
    L_0x09c2:
        r0 = NUM; // 0x7f0e0705 float:1.8878682E38 double:1.0531630445E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r14;
        r2 = "NotificationContactJoined";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        return r0;
    L_0x09d0:
        r1 = r17.isMediaEmpty();
        if (r1 == 0) goto L_0x09ed;
    L_0x09d6:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x09e5;
    L_0x09e0:
        r0 = r0.messageOwner;
        r0 = r0.message;
        return r0;
    L_0x09e5:
        r0 = NUM; // 0x7f0e0647 float:1.8878297E38 double:1.0531629506E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r11, r0);
        return r0;
    L_0x09ed:
        r1 = r0.messageOwner;
        r2 = r1.media;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r2 == 0) goto L_0x0a31;
    L_0x09f5:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r7) goto L_0x0a15;
    L_0x09f9:
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0a15;
    L_0x0a01:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r4);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0a15:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0a27;
    L_0x0a1d:
        r0 = NUM; // 0x7f0e0153 float:1.8875725E38 double:1.053162324E-314;
        r1 = "AttachDestructingPhoto";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a27:
        r0 = NUM; // 0x7f0e0162 float:1.8875756E38 double:1.0531623315E-314;
        r1 = "AttachPhoto";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a31:
        r1 = r17.isVideo();
        if (r1 == 0) goto L_0x0a78;
    L_0x0a37:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0a5c;
    L_0x0a3b:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0a5c;
    L_0x0a45:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "📹 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0a5c:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0a6e;
    L_0x0a64:
        r0 = NUM; // 0x7f0e0154 float:1.8875727E38 double:1.0531623246E-314;
        r1 = "AttachDestructingVideo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a6e:
        r0 = NUM; // 0x7f0e0168 float:1.8875768E38 double:1.0531623345E-314;
        r1 = "AttachVideo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a78:
        r1 = r17.isGame();
        if (r1 == 0) goto L_0x0a88;
    L_0x0a7e:
        r0 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r1 = "AttachGame";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a88:
        r1 = r17.isVoice();
        if (r1 == 0) goto L_0x0a98;
    L_0x0a8e:
        r0 = NUM; // 0x7f0e0150 float:1.887572E38 double:1.0531623226E-314;
        r1 = "AttachAudio";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0a98:
        r1 = r17.isRoundVideo();
        if (r1 == 0) goto L_0x0aa8;
    L_0x0a9e:
        r0 = NUM; // 0x7f0e0164 float:1.887576E38 double:1.0531623325E-314;
        r1 = "AttachRound";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0aa8:
        r1 = r17.isMusic();
        if (r1 == 0) goto L_0x0ab8;
    L_0x0aae:
        r0 = NUM; // 0x7f0e0161 float:1.8875754E38 double:1.053162331E-314;
        r1 = "AttachMusic";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0ab8:
        r1 = r0.messageOwner;
        r1 = r1.media;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r2 == 0) goto L_0x0aca;
    L_0x0ac0:
        r0 = NUM; // 0x7f0e0152 float:1.8875723E38 double:1.0531623236E-314;
        r1 = "AttachContact";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0aca:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r2 == 0) goto L_0x0aea;
    L_0x0ace:
        r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r1;
        r0 = r1.poll;
        r0 = r0.quiz;
        if (r0 == 0) goto L_0x0ae0;
    L_0x0ad6:
        r0 = NUM; // 0x7f0e094e float:1.8879869E38 double:1.0531633335E-314;
        r1 = "QuizPoll";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0ae0:
        r0 = NUM; // 0x7f0e08fe float:1.8879707E38 double:1.053163294E-314;
        r1 = "Poll";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0aea:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r2 != 0) goto L_0x0ba6;
    L_0x0aee:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r2 == 0) goto L_0x0af4;
    L_0x0af2:
        goto L_0x0ba6;
    L_0x0af4:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r2 == 0) goto L_0x0b02;
    L_0x0af8:
        r0 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r1 = "AttachLiveLocation";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0b02:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r1 == 0) goto L_0x0ba5;
    L_0x0b06:
        r1 = r17.isSticker();
        if (r1 != 0) goto L_0x0b77;
    L_0x0b0c:
        r1 = r17.isAnimatedSticker();
        if (r1 == 0) goto L_0x0b13;
    L_0x0b12:
        goto L_0x0b77;
    L_0x0b13:
        r1 = r17.isGif();
        if (r1 == 0) goto L_0x0b48;
    L_0x0b19:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0b3e;
    L_0x0b1d:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0b3e;
    L_0x0b27:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "🎬 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0b3e:
        r0 = NUM; // 0x7f0e0157 float:1.8875733E38 double:1.053162326E-314;
        r1 = "AttachGif";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0b48:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r7) goto L_0x0b6d;
    L_0x0b4c:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0b6d;
    L_0x0b56:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "📎 ";
        r1.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0b6d:
        r0 = NUM; // 0x7f0e0155 float:1.887573E38 double:1.053162325E-314;
        r1 = "AttachDocument";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0b77:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x0b9b;
    L_0x0b7d:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r0 = " ";
        r1.append(r0);
        r0 = NUM; // 0x7f0e0165 float:1.8875762E38 double:1.053162333E-314;
        r2 = "AttachSticker";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r1.append(r0);
        r0 = r1.toString();
        return r0;
    L_0x0b9b:
        r0 = NUM; // 0x7f0e0165 float:1.8875762E38 double:1.053162333E-314;
        r1 = "AttachSticker";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0ba5:
        return r9;
    L_0x0ba6:
        r0 = NUM; // 0x7f0e015e float:1.8875747E38 double:1.0531623296E-314;
        r1 = "AttachLocation";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        return r0;
    L_0x0bb0:
        if (r19 == 0) goto L_0x0bb4;
    L_0x0bb2:
        r19[r6] = r6;
    L_0x0bb4:
        r0 = NUM; // 0x7f0e0647 float:1.8878297E38 double:1.0531629506E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r11, r0);
        return r0;
    L_0x0bbc:
        r0 = NUM; // 0x7f0e0714 float:1.8878713E38 double:1.053163052E-314;
        r1 = "NotificationHiddenMessage";
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
        r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode();
        if (r1 != 0) goto L_0x127f;
    L_0x0008:
        r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r1 == 0) goto L_0x000e;
    L_0x000c:
        goto L_0x127f;
    L_0x000e:
        r1 = r0.messageOwner;
        r2 = r1.dialog_id;
        r1 = r1.to_id;
        r4 = r1.chat_id;
        if (r4 == 0) goto L_0x0019;
    L_0x0018:
        goto L_0x001b;
    L_0x0019:
        r4 = r1.channel_id;
    L_0x001b:
        r1 = r0.messageOwner;
        r1 = r1.to_id;
        r1 = r1.user_id;
        r5 = 1;
        r6 = 0;
        if (r20 == 0) goto L_0x0027;
    L_0x0025:
        r20[r6] = r5;
    L_0x0027:
        r7 = r16.getAccountInstance();
        r7 = r7.getNotificationsSettings();
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "content_preview_";
        r8.append(r9);
        r8.append(r2);
        r8 = r8.toString();
        r8 = r7.getBoolean(r8, r5);
        r9 = r17.isFcmMessage();
        r10 = 2;
        if (r9 == 0) goto L_0x00c6;
    L_0x004b:
        if (r4 != 0) goto L_0x006d;
    L_0x004d:
        if (r1 == 0) goto L_0x006d;
    L_0x004f:
        if (r8 == 0) goto L_0x0059;
    L_0x0051:
        r1 = "EnablePreviewAll";
        r1 = r7.getBoolean(r1, r5);
        if (r1 != 0) goto L_0x00bf;
    L_0x0059:
        if (r20 == 0) goto L_0x005d;
    L_0x005b:
        r20[r6] = r6;
    L_0x005d:
        r1 = NUM; // 0x7f0e0738 float:1.8878786E38 double:1.0531630697E-314;
        r2 = new java.lang.Object[r5];
        r0 = r0.localName;
        r2[r6] = r0;
        r0 = "NotificationMessageNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x006d:
        if (r4 == 0) goto L_0x00bf;
    L_0x006f:
        if (r8 == 0) goto L_0x0089;
    L_0x0071:
        r1 = r0.localChannel;
        if (r1 != 0) goto L_0x007d;
    L_0x0075:
        r1 = "EnablePreviewGroup";
        r1 = r7.getBoolean(r1, r5);
        if (r1 == 0) goto L_0x0089;
    L_0x007d:
        r1 = r0.localChannel;
        if (r1 == 0) goto L_0x00bf;
    L_0x0081:
        r1 = "EnablePreviewChannel";
        r1 = r7.getBoolean(r1, r5);
        if (r1 != 0) goto L_0x00bf;
    L_0x0089:
        if (r20 == 0) goto L_0x008d;
    L_0x008b:
        r20[r6] = r6;
    L_0x008d:
        r1 = r17.isMegagroup();
        if (r1 != 0) goto L_0x00ab;
    L_0x0093:
        r1 = r0.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x00ab;
    L_0x009b:
        r1 = NUM; // 0x7f0e0272 float:1.8876307E38 double:1.053162466E-314;
        r2 = new java.lang.Object[r5];
        r0 = r0.localName;
        r2[r6] = r0;
        r0 = "ChannelMessageNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x00ab:
        r1 = NUM; // 0x7f0e072b float:1.887876E38 double:1.053163063E-314;
        r2 = new java.lang.Object[r10];
        r3 = r0.localUserName;
        r2[r6] = r3;
        r0 = r0.localName;
        r2[r5] = r0;
        r0 = "NotificationMessageGroupNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        return r0;
    L_0x00bf:
        r19[r6] = r5;
        r0 = r0.messageText;
        r0 = (java.lang.String) r0;
        return r0;
    L_0x00c6:
        r9 = r16.getUserConfig();
        r9 = r9.getClientUserId();
        if (r1 != 0) goto L_0x00e4;
    L_0x00d0:
        r1 = r17.isFromUser();
        if (r1 != 0) goto L_0x00df;
    L_0x00d6:
        r1 = r17.getId();
        if (r1 >= 0) goto L_0x00dd;
    L_0x00dc:
        goto L_0x00df;
    L_0x00dd:
        r1 = -r4;
        goto L_0x00ea;
    L_0x00df:
        r1 = r0.messageOwner;
        r1 = r1.from_id;
        goto L_0x00ea;
    L_0x00e4:
        if (r1 != r9) goto L_0x00ea;
    L_0x00e6:
        r1 = r0.messageOwner;
        r1 = r1.from_id;
    L_0x00ea:
        r11 = 0;
        r13 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1));
        if (r13 != 0) goto L_0x00f8;
    L_0x00f0:
        if (r4 == 0) goto L_0x00f5;
    L_0x00f2:
        r2 = -r4;
        r2 = (long) r2;
        goto L_0x00f8;
    L_0x00f5:
        if (r1 == 0) goto L_0x00f8;
    L_0x00f7:
        r2 = (long) r1;
    L_0x00f8:
        r11 = 0;
        if (r1 <= 0) goto L_0x012d;
    L_0x00fb:
        r12 = r0.messageOwner;
        r12 = r12.from_scheduled;
        if (r12 == 0) goto L_0x011a;
    L_0x0101:
        r12 = (long) r9;
        r14 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
        if (r14 != 0) goto L_0x0110;
    L_0x0106:
        r12 = NUM; // 0x7f0e0658 float:1.8878331E38 double:1.053162959E-314;
        r13 = "MessageScheduledReminderNotification";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        goto L_0x0140;
    L_0x0110:
        r12 = NUM; // 0x7f0e0740 float:1.8878802E38 double:1.0531630736E-314;
        r13 = "NotificationMessageScheduledName";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        goto L_0x0140;
    L_0x011a:
        r12 = r16.getMessagesController();
        r13 = java.lang.Integer.valueOf(r1);
        r12 = r12.getUser(r13);
        if (r12 == 0) goto L_0x013f;
    L_0x0128:
        r12 = org.telegram.messenger.UserObject.getUserName(r12);
        goto L_0x0140;
    L_0x012d:
        r12 = r16.getMessagesController();
        r13 = -r1;
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
        if (r4 == 0) goto L_0x0154;
    L_0x0145:
        r13 = r16.getMessagesController();
        r14 = java.lang.Integer.valueOf(r4);
        r13 = r13.getChat(r14);
        if (r13 != 0) goto L_0x0155;
    L_0x0153:
        return r11;
    L_0x0154:
        r13 = r11;
    L_0x0155:
        r3 = (int) r2;
        if (r3 != 0) goto L_0x0163;
    L_0x0158:
        r0 = NUM; // 0x7f0e0cda float:1.888171E38 double:1.053163782E-314;
        r1 = "YouHaveNewMessage";
        r11 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x127e;
    L_0x0163:
        r2 = "🎬 ";
        r3 = "📎 ";
        r14 = "📹 ";
        r15 = "🖼 ";
        r11 = "NotificationMessageText";
        r10 = 3;
        if (r4 != 0) goto L_0x0511;
    L_0x0174:
        if (r1 == 0) goto L_0x0511;
    L_0x0176:
        if (r8 == 0) goto L_0x04fe;
    L_0x0178:
        r1 = "EnablePreviewAll";
        r1 = r7.getBoolean(r1, r5);
        if (r1 == 0) goto L_0x04fe;
    L_0x0180:
        r1 = r0.messageOwner;
        r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r4 == 0) goto L_0x0244;
    L_0x0186:
        r1 = r1.action;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r2 != 0) goto L_0x0235;
    L_0x018c:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r2 == 0) goto L_0x0192;
    L_0x0190:
        goto L_0x0235;
    L_0x0192:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
        if (r2 == 0) goto L_0x01a5;
    L_0x0196:
        r0 = NUM; // 0x7f0e0706 float:1.8878684E38 double:1.053163045E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationContactNewPhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x01a5:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
        if (r2 == 0) goto L_0x0206;
    L_0x01a9:
        r1 = NUM; // 0x7f0e0d06 float:1.88818E38 double:1.053163804E-314;
        r2 = 2;
        r3 = new java.lang.Object[r2];
        r2 = org.telegram.messenger.LocaleController.getInstance();
        r2 = r2.formatterYear;
        r4 = r0.messageOwner;
        r4 = r4.date;
        r7 = (long) r4;
        r11 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7 = r7 * r11;
        r2 = r2.format(r7);
        r3[r6] = r2;
        r2 = org.telegram.messenger.LocaleController.getInstance();
        r2 = r2.formatterDay;
        r4 = r0.messageOwner;
        r4 = r4.date;
        r7 = (long) r4;
        r7 = r7 * r11;
        r2 = r2.format(r7);
        r3[r5] = r2;
        r2 = "formatDateAtTime";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        r2 = NUM; // 0x7f0e0746 float:1.8878814E38 double:1.0531630766E-314;
        r3 = 4;
        r3 = new java.lang.Object[r3];
        r4 = r16.getUserConfig();
        r4 = r4.getCurrentUser();
        r4 = r4.first_name;
        r3[r6] = r4;
        r3[r5] = r1;
        r0 = r0.messageOwner;
        r0 = r0.action;
        r1 = r0.title;
        r4 = 2;
        r3[r4] = r1;
        r0 = r0.address;
        r3[r10] = r0;
        r0 = "NotificationUnrecognizedDevice";
        r11 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3);
        goto L_0x127e;
    L_0x0206:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r2 != 0) goto L_0x022d;
    L_0x020a:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
        if (r2 == 0) goto L_0x020f;
    L_0x020e:
        goto L_0x022d;
    L_0x020f:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r2 == 0) goto L_0x127c;
    L_0x0213:
        r1 = r1.reason;
        r0 = r17.isOut();
        if (r0 != 0) goto L_0x022a;
    L_0x021b:
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        if (r0 == 0) goto L_0x022a;
    L_0x021f:
        r0 = NUM; // 0x7f0e020b float:1.8876098E38 double:1.053162415E-314;
        r1 = "CallMessageIncomingMissed";
        r11 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x127e;
    L_0x022a:
        r11 = 0;
        goto L_0x127e;
    L_0x022d:
        r0 = r0.messageText;
        r11 = r0.toString();
        goto L_0x127e;
    L_0x0235:
        r0 = NUM; // 0x7f0e0705 float:1.8878682E38 double:1.0531630445E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationContactJoined";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0244:
        r1 = r17.isMediaEmpty();
        if (r1 == 0) goto L_0x028a;
    L_0x024a:
        if (r18 != 0) goto L_0x027b;
    L_0x024c:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x026c;
    L_0x0256:
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1[r5] = r0;
        r0 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1);
        r19[r6] = r5;
        goto L_0x127e;
    L_0x026c:
        r0 = NUM; // 0x7f0e0738 float:1.8878786E38 double:1.0531630697E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x027b:
        r0 = NUM; // 0x7f0e0738 float:1.8878786E38 double:1.0531630697E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x028a:
        r1 = r0.messageOwner;
        r4 = r1.media;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r4 == 0) goto L_0x02ed;
    L_0x0292:
        if (r18 != 0) goto L_0x02c7;
    L_0x0294:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r2 < r3) goto L_0x02c7;
    L_0x029a:
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x02c7;
    L_0x02a2:
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r15);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r1[r5] = r0;
        r0 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1);
        r19[r6] = r5;
        goto L_0x127e;
    L_0x02c7:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x02de;
    L_0x02cf:
        r0 = NUM; // 0x7f0e073d float:1.8878796E38 double:1.053163072E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageSDPhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x02de:
        r0 = NUM; // 0x7f0e0739 float:1.8878788E38 double:1.05316307E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessagePhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x02ed:
        r1 = r17.isVideo();
        if (r1 == 0) goto L_0x0350;
    L_0x02f3:
        if (r18 != 0) goto L_0x032a;
    L_0x02f5:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 19;
        if (r1 < r2) goto L_0x032a;
    L_0x02fb:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x032a;
    L_0x0305:
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r14);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r1[r5] = r0;
        r0 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1);
        r19[r6] = r5;
        goto L_0x127e;
    L_0x032a:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0341;
    L_0x0332:
        r0 = NUM; // 0x7f0e073e float:1.8878798E38 double:1.0531630726E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageSDVideo";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0341:
        r0 = NUM; // 0x7f0e0744 float:1.887881E38 double:1.0531630756E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageVideo";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0350:
        r1 = r17.isGame();
        if (r1 == 0) goto L_0x0370;
    L_0x0356:
        r1 = NUM; // 0x7f0e071e float:1.8878733E38 double:1.053163057E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r6] = r12;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.game;
        r0 = r0.title;
        r2[r5] = r0;
        r0 = "NotificationMessageGame";
        r11 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x127e;
    L_0x0370:
        r1 = r17.isVoice();
        if (r1 == 0) goto L_0x0385;
    L_0x0376:
        r0 = NUM; // 0x7f0e0719 float:1.8878723E38 double:1.0531630543E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageAudio";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0385:
        r1 = r17.isRoundVideo();
        if (r1 == 0) goto L_0x039a;
    L_0x038b:
        r0 = NUM; // 0x7f0e073c float:1.8878794E38 double:1.0531630716E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageRound";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x039a:
        r1 = r17.isMusic();
        if (r1 == 0) goto L_0x03af;
    L_0x03a0:
        r0 = NUM; // 0x7f0e0737 float:1.8878784E38 double:1.053163069E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageMusic";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x03af:
        r1 = r0.messageOwner;
        r1 = r1.media;
        r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r4 == 0) goto L_0x03d3;
    L_0x03b7:
        r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r1;
        r0 = NUM; // 0x7f0e071a float:1.8878725E38 double:1.053163055E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r6] = r12;
        r3 = r1.first_name;
        r1 = r1.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r3, r1);
        r2[r5] = r1;
        r1 = "NotificationMessageContact2";
        r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x127e;
    L_0x03d3:
        r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r4 == 0) goto L_0x0407;
    L_0x03d7:
        r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r1;
        r0 = r1.poll;
        r1 = r0.quiz;
        if (r1 == 0) goto L_0x03f2;
    L_0x03df:
        r1 = NUM; // 0x7f0e073b float:1.8878792E38 double:1.053163071E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r6] = r12;
        r0 = r0.question;
        r2[r5] = r0;
        r0 = "NotificationMessageQuiz2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x03f2:
        r2 = 2;
        r1 = NUM; // 0x7f0e073a float:1.887879E38 double:1.0531630707E-314;
        r2 = new java.lang.Object[r2];
        r2[r6] = r12;
        r0 = r0.question;
        r2[r5] = r0;
        r0 = "NotificationMessagePoll2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
    L_0x0404:
        r11 = r0;
        goto L_0x127e;
    L_0x0407:
        r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r4 != 0) goto L_0x04ef;
    L_0x040b:
        r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r4 == 0) goto L_0x0411;
    L_0x040f:
        goto L_0x04ef;
    L_0x0411:
        r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r4 == 0) goto L_0x0424;
    L_0x0415:
        r0 = NUM; // 0x7f0e0735 float:1.887878E38 double:1.053163068E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageLiveLocation";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0424:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r1 == 0) goto L_0x127c;
    L_0x0428:
        r1 = r17.isSticker();
        if (r1 != 0) goto L_0x04c8;
    L_0x042e:
        r1 = r17.isAnimatedSticker();
        if (r1 == 0) goto L_0x0436;
    L_0x0434:
        goto L_0x04c8;
    L_0x0436:
        r1 = r17.isGif();
        if (r1 == 0) goto L_0x0482;
    L_0x043c:
        if (r18 != 0) goto L_0x0473;
    L_0x043e:
        r1 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r1 < r3) goto L_0x0473;
    L_0x0444:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0473;
    L_0x044e:
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r1[r5] = r0;
        r0 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1);
        r19[r6] = r5;
        goto L_0x127e;
    L_0x0473:
        r0 = NUM; // 0x7f0e0720 float:1.8878737E38 double:1.053163058E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageGif";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0482:
        if (r18 != 0) goto L_0x04b9;
    L_0x0484:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 19;
        if (r1 < r2) goto L_0x04b9;
    L_0x048a:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x04b9;
    L_0x0494:
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r1[r5] = r0;
        r0 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1);
        r19[r6] = r5;
        goto L_0x127e;
    L_0x04b9:
        r0 = NUM; // 0x7f0e071b float:1.8878727E38 double:1.0531630553E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageDocument";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x04c8:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x04e0;
    L_0x04ce:
        r1 = NUM; // 0x7f0e0742 float:1.8878806E38 double:1.0531630746E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r6] = r12;
        r2[r5] = r0;
        r0 = "NotificationMessageStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x04e0:
        r0 = NUM; // 0x7f0e0741 float:1.8878804E38 double:1.053163074E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x04ef:
        r0 = NUM; // 0x7f0e0736 float:1.8878782E38 double:1.0531630687E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageMap";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x04fe:
        if (r20 == 0) goto L_0x0502;
    L_0x0500:
        r20[r6] = r6;
    L_0x0502:
        r0 = NUM; // 0x7f0e0738 float:1.8878786E38 double:1.0531630697E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "NotificationMessageNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0511:
        if (r4 == 0) goto L_0x127c;
    L_0x0513:
        r4 = org.telegram.messenger.ChatObject.isChannel(r13);
        if (r4 == 0) goto L_0x051f;
    L_0x0519:
        r4 = r13.megagroup;
        if (r4 != 0) goto L_0x051f;
    L_0x051d:
        r4 = 1;
        goto L_0x0520;
    L_0x051f:
        r4 = 0;
    L_0x0520:
        if (r8 == 0) goto L_0x124d;
    L_0x0522:
        if (r4 != 0) goto L_0x052c;
    L_0x0524:
        r8 = "EnablePreviewGroup";
        r8 = r7.getBoolean(r8, r5);
        if (r8 != 0) goto L_0x0536;
    L_0x052c:
        if (r4 == 0) goto L_0x124d;
    L_0x052e:
        r4 = "EnablePreviewChannel";
        r4 = r7.getBoolean(r4, r5);
        if (r4 == 0) goto L_0x124d;
    L_0x0536:
        r4 = r0.messageOwner;
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r7 == 0) goto L_0x0d22;
    L_0x053c:
        r4 = r4.action;
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r7 == 0) goto L_0x0647;
    L_0x0542:
        r2 = r4.user_id;
        if (r2 != 0) goto L_0x055e;
    L_0x0546:
        r3 = r4.users;
        r3 = r3.size();
        if (r3 != r5) goto L_0x055e;
    L_0x054e:
        r2 = r0.messageOwner;
        r2 = r2.action;
        r2 = r2.users;
        r2 = r2.get(r6);
        r2 = (java.lang.Integer) r2;
        r2 = r2.intValue();
    L_0x055e:
        if (r2 == 0) goto L_0x05f0;
    L_0x0560:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x0580;
    L_0x0568:
        r0 = r13.megagroup;
        if (r0 != 0) goto L_0x0580;
    L_0x056c:
        r0 = NUM; // 0x7f0e0241 float:1.8876208E38 double:1.0531624417E-314;
        r3 = 2;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "ChannelAddedByNotification";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0580:
        r3 = 2;
        if (r2 != r9) goto L_0x0596;
    L_0x0583:
        r0 = NUM; // 0x7f0e0716 float:1.8878717E38 double:1.053163053E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationInvitedToGroup";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0596:
        r0 = r16.getMessagesController();
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r0.getUser(r2);
        if (r0 != 0) goto L_0x05a6;
    L_0x05a4:
        r2 = 0;
        return r2;
    L_0x05a6:
        r2 = r0.id;
        if (r1 != r2) goto L_0x05d6;
    L_0x05aa:
        r0 = r13.megagroup;
        if (r0 == 0) goto L_0x05c2;
    L_0x05ae:
        r0 = NUM; // 0x7f0e070b float:1.8878694E38 double:1.0531630474E-314;
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationGroupAddSelfMega";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x05c2:
        r1 = 2;
        r0 = NUM; // 0x7f0e070a float:1.8878692E38 double:1.053163047E-314;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationGroupAddSelf";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x05d6:
        r1 = NUM; // 0x7f0e0709 float:1.887869E38 double:1.0531630464E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r3 = r13.title;
        r2[r5] = r3;
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationGroupAddMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x05f0:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = 0;
    L_0x05f6:
        r3 = r0.messageOwner;
        r3 = r3.action;
        r3 = r3.users;
        r3 = r3.size();
        if (r2 >= r3) goto L_0x062d;
    L_0x0602:
        r3 = r16.getMessagesController();
        r4 = r0.messageOwner;
        r4 = r4.action;
        r4 = r4.users;
        r4 = r4.get(r2);
        r4 = (java.lang.Integer) r4;
        r3 = r3.getUser(r4);
        if (r3 == 0) goto L_0x062a;
    L_0x0618:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
        r4 = r1.length();
        if (r4 == 0) goto L_0x0627;
    L_0x0622:
        r4 = ", ";
        r1.append(r4);
    L_0x0627:
        r1.append(r3);
    L_0x062a:
        r2 = r2 + 1;
        goto L_0x05f6;
    L_0x062d:
        r0 = NUM; // 0x7f0e0709 float:1.887869E38 double:1.0531630464E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r3 = r13.title;
        r2[r5] = r3;
        r1 = r1.toString();
        r7 = 2;
        r2[r7] = r1;
        r1 = "NotificationGroupAddMember";
        r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2);
        goto L_0x0404;
    L_0x0647:
        r7 = 2;
        r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
        if (r8 == 0) goto L_0x065f;
    L_0x064c:
        r0 = NUM; // 0x7f0e0717 float:1.8878719E38 double:1.0531630534E-314;
        r1 = new java.lang.Object[r7];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationInvitedToGroupByLink";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x065f:
        r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
        if (r8 == 0) goto L_0x0676;
    L_0x0663:
        r0 = NUM; // 0x7f0e0707 float:1.8878686E38 double:1.0531630455E-314;
        r1 = new java.lang.Object[r7];
        r1[r6] = r12;
        r2 = r4.title;
        r1[r5] = r2;
        r2 = "NotificationEditedGroupName";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0676:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
        if (r7 != 0) goto L_0x0cf1;
    L_0x067a:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
        if (r7 == 0) goto L_0x0680;
    L_0x067e:
        goto L_0x0cf1;
    L_0x0680:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r7 == 0) goto L_0x06e2;
    L_0x0684:
        r2 = r4.user_id;
        if (r2 != r9) goto L_0x069c;
    L_0x0688:
        r0 = NUM; // 0x7f0e0710 float:1.8878705E38 double:1.05316305E-314;
        r3 = 2;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationGroupKickYou";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x069c:
        r3 = 2;
        if (r2 != r1) goto L_0x06b2;
    L_0x069f:
        r0 = NUM; // 0x7f0e0711 float:1.8878707E38 double:1.0531630504E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationGroupLeftMember";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x06b2:
        r1 = r16.getMessagesController();
        r0 = r0.messageOwner;
        r0 = r0.action;
        r0 = r0.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r1.getUser(r0);
        if (r0 != 0) goto L_0x06c8;
    L_0x06c6:
        r1 = 0;
        return r1;
    L_0x06c8:
        r1 = NUM; // 0x7f0e070f float:1.8878703E38 double:1.0531630494E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r3 = r13.title;
        r2[r5] = r3;
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationGroupKickMember";
        r11 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x127e;
    L_0x06e2:
        r1 = 0;
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
        if (r7 == 0) goto L_0x06ef;
    L_0x06e7:
        r0 = r0.messageText;
        r11 = r0.toString();
        goto L_0x127e;
    L_0x06ef:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r7 == 0) goto L_0x06fb;
    L_0x06f3:
        r0 = r0.messageText;
        r11 = r0.toString();
        goto L_0x127e;
    L_0x06fb:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r7 == 0) goto L_0x0710;
    L_0x06ff:
        r0 = NUM; // 0x7f0e0082 float:1.8875301E38 double:1.053162221E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "ActionMigrateFromGroupNotify";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0710:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r7 == 0) goto L_0x0725;
    L_0x0714:
        r0 = NUM; // 0x7f0e0082 float:1.8875301E38 double:1.053162221E-314;
        r1 = new java.lang.Object[r5];
        r2 = r4.title;
        r1[r6] = r2;
        r2 = "ActionMigrateFromGroupNotify";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0725:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
        if (r7 == 0) goto L_0x0731;
    L_0x0729:
        r0 = r0.messageText;
        r11 = r0.toString();
        goto L_0x127e;
    L_0x0731:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r7 == 0) goto L_0x0ce5;
    L_0x0735:
        if (r13 == 0) goto L_0x0a33;
    L_0x0737:
        r1 = org.telegram.messenger.ChatObject.isChannel(r13);
        if (r1 == 0) goto L_0x0741;
    L_0x073d:
        r1 = r13.megagroup;
        if (r1 == 0) goto L_0x0a33;
    L_0x0741:
        r1 = r0.replyMessageObject;
        if (r1 != 0) goto L_0x0759;
    L_0x0745:
        r0 = NUM; // 0x7f0e06f1 float:1.8878642E38 double:1.0531630346E-314;
        r4 = 2;
        r1 = new java.lang.Object[r4];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0759:
        r4 = 2;
        r7 = r1.isMusic();
        if (r7 == 0) goto L_0x0773;
    L_0x0760:
        r0 = NUM; // 0x7f0e06ef float:1.8878638E38 double:1.0531630336E-314;
        r1 = new java.lang.Object[r4];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedMusic";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0773:
        r4 = r1.isVideo();
        if (r4 == 0) goto L_0x07c6;
    L_0x0779:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 19;
        if (r0 < r2) goto L_0x07b2;
    L_0x077f:
        r0 = r1.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x07b2;
    L_0x0789:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r14);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r0.append(r1);
        r0 = r0.toString();
        r1 = NUM; // 0x7f0e06ff float:1.887867E38 double:1.0531630415E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r2[r5] = r0;
        r0 = r13.title;
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x07b2:
        r3 = 2;
        r0 = NUM; // 0x7f0e0701 float:1.8878674E38 double:1.0531630425E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedVideo";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x07c6:
        r4 = r1.isGif();
        if (r4 == 0) goto L_0x0819;
    L_0x07cc:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r0 < r3) goto L_0x0805;
    L_0x07d2:
        r0 = r1.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0805;
    L_0x07dc:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r2);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r0.append(r1);
        r0 = r0.toString();
        r1 = NUM; // 0x7f0e06ff float:1.887867E38 double:1.0531630415E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r2[r5] = r0;
        r0 = r13.title;
        r4 = 2;
        r2[r4] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0805:
        r4 = 2;
        r0 = NUM; // 0x7f0e06eb float:1.887863E38 double:1.0531630316E-314;
        r1 = new java.lang.Object[r4];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedGif";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0819:
        r4 = 2;
        r2 = r1.isVoice();
        if (r2 == 0) goto L_0x0833;
    L_0x0820:
        r0 = NUM; // 0x7f0e0703 float:1.8878678E38 double:1.0531630435E-314;
        r1 = new java.lang.Object[r4];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedVoice";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0833:
        r2 = r1.isRoundVideo();
        if (r2 == 0) goto L_0x084c;
    L_0x0839:
        r0 = NUM; // 0x7f0e06f9 float:1.8878658E38 double:1.0531630385E-314;
        r1 = new java.lang.Object[r4];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedRound";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x084c:
        r2 = r1.isSticker();
        if (r2 != 0) goto L_0x0a03;
    L_0x0852:
        r2 = r1.isAnimatedSticker();
        if (r2 == 0) goto L_0x085a;
    L_0x0858:
        goto L_0x0a03;
    L_0x085a:
        r2 = r1.messageOwner;
        r4 = r2.media;
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r7 == 0) goto L_0x08ad;
    L_0x0862:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r0 < r4) goto L_0x0899;
    L_0x0868:
        r0 = r2.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0899;
    L_0x0870:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r0.append(r1);
        r0 = r0.toString();
        r1 = NUM; // 0x7f0e06ff float:1.887867E38 double:1.0531630415E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r2[r5] = r0;
        r0 = r13.title;
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0899:
        r3 = 2;
        r0 = NUM; // 0x7f0e06e1 float:1.887861E38 double:1.0531630267E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedFile";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x08ad:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r3 != 0) goto L_0x09ef;
    L_0x08b1:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r3 == 0) goto L_0x08b7;
    L_0x08b5:
        goto L_0x09ef;
    L_0x08b7:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r3 == 0) goto L_0x08cf;
    L_0x08bb:
        r0 = NUM; // 0x7f0e06e9 float:1.8878625E38 double:1.0531630306E-314;
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedGeoLive";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x08cf:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r3 == 0) goto L_0x08f7;
    L_0x08d3:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r0;
        r1 = NUM; // 0x7f0e06df float:1.8878605E38 double:1.0531630257E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r3 = r13.title;
        r2[r5] = r3;
        r3 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r3, r0);
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationActionPinnedContact2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x08f7:
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r0 == 0) goto L_0x0933;
    L_0x08fb:
        r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4;
        r0 = r4.poll;
        r1 = r0.quiz;
        if (r1 == 0) goto L_0x091b;
    L_0x0903:
        r1 = NUM; // 0x7f0e06f7 float:1.8878654E38 double:1.0531630375E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r3 = r13.title;
        r2[r5] = r3;
        r0 = r0.question;
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationActionPinnedQuiz2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x091b:
        r3 = 2;
        r1 = NUM; // 0x7f0e06f5 float:1.887865E38 double:1.0531630366E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r4 = r13.title;
        r2[r5] = r4;
        r0 = r0.question;
        r2[r3] = r0;
        r0 = "NotificationActionPinnedPoll2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0933:
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 == 0) goto L_0x0982;
    L_0x0937:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r0 < r3) goto L_0x096e;
    L_0x093d:
        r0 = r2.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x096e;
    L_0x0945:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r15);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r0.append(r1);
        r0 = r0.toString();
        r1 = NUM; // 0x7f0e06ff float:1.887867E38 double:1.0531630415E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r2[r5] = r0;
        r0 = r13.title;
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x096e:
        r3 = 2;
        r0 = NUM; // 0x7f0e06f3 float:1.8878646E38 double:1.0531630356E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedPhoto";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0982:
        r3 = 2;
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x099a;
    L_0x0987:
        r0 = NUM; // 0x7f0e06e3 float:1.8878613E38 double:1.0531630277E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedGame";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x099a:
        r0 = r1.messageText;
        if (r0 == 0) goto L_0x09db;
    L_0x099e:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x09db;
    L_0x09a4:
        r0 = r1.messageText;
        r1 = r0.length();
        r2 = 20;
        if (r1 <= r2) goto L_0x09c5;
    L_0x09ae:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = 20;
        r0 = r0.subSequence(r6, r2);
        r1.append(r0);
        r0 = "...";
        r1.append(r0);
        r0 = r1.toString();
    L_0x09c5:
        r1 = NUM; // 0x7f0e06ff float:1.887867E38 double:1.0531630415E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r2[r5] = r0;
        r0 = r13.title;
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationActionPinnedText";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x09db:
        r3 = 2;
        r0 = NUM; // 0x7f0e06f1 float:1.8878642E38 double:1.0531630346E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedNoText";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x09ef:
        r3 = 2;
        r0 = NUM; // 0x7f0e06e7 float:1.8878621E38 double:1.0531630296E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedGeo";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0a03:
        r0 = r1.getStickerEmoji();
        if (r0 == 0) goto L_0x0a1f;
    L_0x0a09:
        r1 = NUM; // 0x7f0e06fd float:1.8878666E38 double:1.0531630405E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r3 = r13.title;
        r2[r5] = r3;
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationActionPinnedStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0a1f:
        r3 = 2;
        r0 = NUM; // 0x7f0e06fb float:1.8878662E38 double:1.0531630395E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationActionPinnedSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0a33:
        r1 = r0.replyMessageObject;
        if (r1 != 0) goto L_0x0a48;
    L_0x0a37:
        r0 = NUM; // 0x7f0e06f2 float:1.8878644E38 double:1.053163035E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedNoTextChannel";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0a48:
        r4 = r1.isMusic();
        if (r4 == 0) goto L_0x0a5f;
    L_0x0a4e:
        r0 = NUM; // 0x7f0e06f0 float:1.887864E38 double:1.053163034E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedMusicChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0a5f:
        r4 = r1.isVideo();
        r7 = "NotificationActionPinnedTextChannel";
        if (r4 == 0) goto L_0x0aad;
    L_0x0a67:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 19;
        if (r0 < r2) goto L_0x0a9c;
    L_0x0a6d:
        r0 = r1.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0a9c;
    L_0x0a77:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r14);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r0.append(r1);
        r0 = r0.toString();
        r1 = NUM; // 0x7f0e0700 float:1.8878672E38 double:1.053163042E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = r13.title;
        r2[r6] = r3;
        r2[r5] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2);
        goto L_0x0404;
    L_0x0a9c:
        r0 = NUM; // 0x7f0e0702 float:1.8878676E38 double:1.053163043E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedVideoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0aad:
        r4 = r1.isGif();
        if (r4 == 0) goto L_0x0af9;
    L_0x0ab3:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r0 < r3) goto L_0x0ae8;
    L_0x0ab9:
        r0 = r1.messageOwner;
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0ae8;
    L_0x0ac3:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r2);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r0.append(r1);
        r0 = r0.toString();
        r1 = NUM; // 0x7f0e0700 float:1.8878672E38 double:1.053163042E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = r13.title;
        r2[r6] = r3;
        r2[r5] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2);
        goto L_0x0404;
    L_0x0ae8:
        r0 = NUM; // 0x7f0e06ec float:1.8878632E38 double:1.053163032E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedGifChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0af9:
        r2 = r1.isVoice();
        if (r2 == 0) goto L_0x0b10;
    L_0x0aff:
        r0 = NUM; // 0x7f0e0704 float:1.887868E38 double:1.053163044E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedVoiceChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0b10:
        r2 = r1.isRoundVideo();
        if (r2 == 0) goto L_0x0b27;
    L_0x0b16:
        r0 = NUM; // 0x7f0e06fa float:1.887866E38 double:1.053163039E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedRoundChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0b27:
        r2 = r1.isSticker();
        if (r2 != 0) goto L_0x0cba;
    L_0x0b2d:
        r2 = r1.isAnimatedSticker();
        if (r2 == 0) goto L_0x0b35;
    L_0x0b33:
        goto L_0x0cba;
    L_0x0b35:
        r2 = r1.messageOwner;
        r4 = r2.media;
        r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r8 == 0) goto L_0x0b81;
    L_0x0b3d:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r0 < r4) goto L_0x0b70;
    L_0x0b43:
        r0 = r2.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0b70;
    L_0x0b4b:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r0.append(r1);
        r0 = r0.toString();
        r1 = NUM; // 0x7f0e0700 float:1.8878672E38 double:1.053163042E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = r13.title;
        r2[r6] = r3;
        r2[r5] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2);
        goto L_0x0404;
    L_0x0b70:
        r0 = NUM; // 0x7f0e06e2 float:1.8878611E38 double:1.053163027E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedFileChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0b81:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r3 != 0) goto L_0x0ca9;
    L_0x0b85:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r3 == 0) goto L_0x0b8b;
    L_0x0b89:
        goto L_0x0ca9;
    L_0x0b8b:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r3 == 0) goto L_0x0ba0;
    L_0x0b8f:
        r0 = NUM; // 0x7f0e06ea float:1.8878628E38 double:1.053163031E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedGeoLiveChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0ba0:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r3 == 0) goto L_0x0bc6;
    L_0x0ba4:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r0;
        r1 = NUM; // 0x7f0e06e0 float:1.8878607E38 double:1.053163026E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = r13.title;
        r2[r6] = r3;
        r3 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r3, r0);
        r2[r5] = r0;
        r0 = "NotificationActionPinnedContactChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0bc6:
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r0 == 0) goto L_0x0bfe;
    L_0x0bca:
        r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4;
        r0 = r4.poll;
        r1 = r0.quiz;
        if (r1 == 0) goto L_0x0be8;
    L_0x0bd2:
        r1 = NUM; // 0x7f0e06f8 float:1.8878656E38 double:1.053163038E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = r13.title;
        r2[r6] = r3;
        r0 = r0.question;
        r2[r5] = r0;
        r0 = "NotificationActionPinnedQuizChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0be8:
        r2 = 2;
        r1 = NUM; // 0x7f0e06f6 float:1.8878652E38 double:1.053163037E-314;
        r2 = new java.lang.Object[r2];
        r3 = r13.title;
        r2[r6] = r3;
        r0 = r0.question;
        r2[r5] = r0;
        r0 = "NotificationActionPinnedPollChannel2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0bfe:
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r0 < r3) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = r2.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r15);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r0.append(r1);
        r0 = r0.toString();
        r1 = NUM; // 0x7f0e0700 float:1.8878672E38 double:1.053163042E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = r13.title;
        r2[r6] = r3;
        r2[r5] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2);
        goto L_0x0404;
    L_0x0CLASSNAME:
        r0 = NUM; // 0x7f0e06f4 float:1.8878648E38 double:1.053163036E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedPhotoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0CLASSNAME:
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x0c5b;
    L_0x0c4a:
        r0 = NUM; // 0x7f0e06e4 float:1.8878615E38 double:1.053163028E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedGameChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0c5b:
        r0 = r1.messageText;
        if (r0 == 0) goto L_0x0CLASSNAME;
    L_0x0c5f:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = r1.messageText;
        r1 = r0.length();
        r2 = 20;
        if (r1 <= r2) goto L_0x0CLASSNAME;
    L_0x0c6f:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = 20;
        r0 = r0.subSequence(r6, r2);
        r1.append(r0);
        r0 = "...";
        r1.append(r0);
        r0 = r1.toString();
    L_0x0CLASSNAME:
        r1 = NUM; // 0x7f0e0700 float:1.8878672E38 double:1.053163042E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = r13.title;
        r2[r6] = r3;
        r2[r5] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2);
        goto L_0x0404;
    L_0x0CLASSNAME:
        r0 = NUM; // 0x7f0e06f2 float:1.8878644E38 double:1.053163035E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedNoTextChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0ca9:
        r0 = NUM; // 0x7f0e06e8 float:1.8878623E38 double:1.05316303E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedGeoChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0cba:
        r0 = r1.getStickerEmoji();
        if (r0 == 0) goto L_0x0cd4;
    L_0x0cc0:
        r1 = NUM; // 0x7f0e06fe float:1.8878668E38 double:1.053163041E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = r13.title;
        r2[r6] = r3;
        r2[r5] = r0;
        r0 = "NotificationActionPinnedStickerEmojiChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0cd4:
        r0 = NUM; // 0x7f0e06fc float:1.8878664E38 double:1.05316304E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "NotificationActionPinnedStickerChannel";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0ce5:
        r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r2 == 0) goto L_0x127d;
    L_0x0ce9:
        r0 = r0.messageText;
        r11 = r0.toString();
        goto L_0x127e;
    L_0x0cf1:
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x0d0e;
    L_0x0cf9:
        r0 = r13.megagroup;
        if (r0 != 0) goto L_0x0d0e;
    L_0x0cfd:
        r0 = NUM; // 0x7f0e0281 float:1.8876338E38 double:1.0531624733E-314;
        r1 = new java.lang.Object[r5];
        r2 = r13.title;
        r1[r6] = r2;
        r2 = "ChannelPhotoEditNotification";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0d0e:
        r0 = NUM; // 0x7f0e0708 float:1.8878688E38 double:1.053163046E-314;
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationEditedGroupPhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0d22:
        r1 = 0;
        r4 = org.telegram.messenger.ChatObject.isChannel(r13);
        if (r4 == 0) goto L_0x0f8c;
    L_0x0d29:
        r4 = r13.megagroup;
        if (r4 != 0) goto L_0x0f8c;
    L_0x0d2d:
        r4 = r17.isMediaEmpty();
        if (r4 == 0) goto L_0x0d66;
    L_0x0d33:
        if (r18 != 0) goto L_0x0d57;
    L_0x0d35:
        r1 = r0.messageOwner;
        r1 = r1.message;
        if (r1 == 0) goto L_0x0d57;
    L_0x0d3b:
        r1 = r1.length();
        if (r1 == 0) goto L_0x0d57;
    L_0x0d41:
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r0 = r0.messageOwner;
        r0 = r0.message;
        r1[r5] = r0;
        r0 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1);
        r19[r6] = r5;
        goto L_0x127e;
    L_0x0d57:
        r0 = NUM; // 0x7f0e0272 float:1.8876307E38 double:1.053162466E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0d66:
        r4 = r0.messageOwner;
        r7 = r4.media;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r7 == 0) goto L_0x0db2;
    L_0x0d6e:
        if (r18 != 0) goto L_0x0da3;
    L_0x0d70:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 19;
        if (r1 < r2) goto L_0x0da3;
    L_0x0d76:
        r1 = r4.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0da3;
    L_0x0d7e:
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r15);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r1[r5] = r0;
        r0 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1);
        r19[r6] = r5;
        goto L_0x127e;
    L_0x0da3:
        r0 = NUM; // 0x7f0e0273 float:1.887631E38 double:1.0531624664E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessagePhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0db2:
        r4 = r17.isVideo();
        if (r4 == 0) goto L_0x0dfe;
    L_0x0db8:
        if (r18 != 0) goto L_0x0def;
    L_0x0dba:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 19;
        if (r1 < r2) goto L_0x0def;
    L_0x0dc0:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0def;
    L_0x0dca:
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r14);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r1[r5] = r0;
        r0 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1);
        r19[r6] = r5;
        goto L_0x127e;
    L_0x0def:
        r0 = NUM; // 0x7f0e0279 float:1.8876321E38 double:1.0531624694E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageVideo";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0dfe:
        r4 = r17.isVoice();
        if (r4 == 0) goto L_0x0e13;
    L_0x0e04:
        r0 = NUM; // 0x7f0e026a float:1.887629E38 double:1.053162462E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageAudio";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0e13:
        r4 = r17.isRoundVideo();
        if (r4 == 0) goto L_0x0e28;
    L_0x0e19:
        r0 = NUM; // 0x7f0e0276 float:1.8876315E38 double:1.053162468E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageRound";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0e28:
        r4 = r17.isMusic();
        if (r4 == 0) goto L_0x0e3d;
    L_0x0e2e:
        r0 = NUM; // 0x7f0e0271 float:1.8876305E38 double:1.0531624654E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageMusic";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0e3d:
        r4 = r0.messageOwner;
        r4 = r4.media;
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r7 == 0) goto L_0x0e61;
    L_0x0e45:
        r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r4;
        r0 = NUM; // 0x7f0e026b float:1.8876293E38 double:1.0531624625E-314;
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = r4.first_name;
        r3 = r4.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r3);
        r1[r5] = r2;
        r2 = "ChannelMessageContact2";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0e61:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r7 == 0) goto L_0x0e95;
    L_0x0e65:
        r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4;
        r0 = r4.poll;
        r1 = r0.quiz;
        if (r1 == 0) goto L_0x0e81;
    L_0x0e6d:
        r1 = NUM; // 0x7f0e0275 float:1.8876313E38 double:1.0531624674E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r6] = r12;
        r0 = r0.question;
        r2[r5] = r0;
        r0 = "ChannelMessageQuiz2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0e81:
        r2 = 2;
        r1 = NUM; // 0x7f0e0274 float:1.8876311E38 double:1.053162467E-314;
        r2 = new java.lang.Object[r2];
        r2[r6] = r12;
        r0 = r0.question;
        r2[r5] = r0;
        r0 = "ChannelMessagePoll2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0e95:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r7 != 0) goto L_0x0f7d;
    L_0x0e99:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r7 == 0) goto L_0x0e9f;
    L_0x0e9d:
        goto L_0x0f7d;
    L_0x0e9f:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r7 == 0) goto L_0x0eb2;
    L_0x0ea3:
        r0 = NUM; // 0x7f0e026f float:1.8876301E38 double:1.0531624644E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageLiveLocation";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0eb2:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r4 == 0) goto L_0x127d;
    L_0x0eb6:
        r1 = r17.isSticker();
        if (r1 != 0) goto L_0x0var_;
    L_0x0ebc:
        r1 = r17.isAnimatedSticker();
        if (r1 == 0) goto L_0x0ec4;
    L_0x0ec2:
        goto L_0x0var_;
    L_0x0ec4:
        r1 = r17.isGif();
        if (r1 == 0) goto L_0x0var_;
    L_0x0eca:
        if (r18 != 0) goto L_0x0var_;
    L_0x0ecc:
        r1 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r1 < r3) goto L_0x0var_;
    L_0x0ed2:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0var_;
    L_0x0edc:
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r1[r5] = r0;
        r0 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1);
        r19[r6] = r5;
        goto L_0x127e;
    L_0x0var_:
        r0 = NUM; // 0x7f0e026e float:1.88763E38 double:1.053162464E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageGIF";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0var_:
        if (r18 != 0) goto L_0x0var_;
    L_0x0var_:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 19;
        if (r1 < r2) goto L_0x0var_;
    L_0x0var_:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0var_;
    L_0x0var_:
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r1[r5] = r0;
        r0 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1);
        r19[r6] = r5;
        goto L_0x127e;
    L_0x0var_:
        r0 = NUM; // 0x7f0e026c float:1.8876295E38 double:1.053162463E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageDocument";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0var_:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x0f6e;
    L_0x0f5c:
        r1 = NUM; // 0x7f0e0278 float:1.887632E38 double:1.053162469E-314;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r2[r6] = r12;
        r2[r5] = r0;
        r0 = "ChannelMessageStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x0f6e:
        r0 = NUM; // 0x7f0e0277 float:1.8876317E38 double:1.0531624684E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x0f7d:
        r0 = NUM; // 0x7f0e0270 float:1.8876303E38 double:1.053162465E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageMap";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0f8c:
        r4 = r17.isMediaEmpty();
        r7 = NUM; // 0x7f0e0732 float:1.8878774E38 double:1.0531630667E-314;
        r8 = "NotificationMessageGroupText";
        if (r4 == 0) goto L_0x0fce;
    L_0x0var_:
        if (r18 != 0) goto L_0x0fba;
    L_0x0var_:
        r1 = r0.messageOwner;
        r1 = r1.message;
        if (r1 == 0) goto L_0x0fba;
    L_0x0f9f:
        r1 = r1.length();
        if (r1 == 0) goto L_0x0fba;
    L_0x0fa5:
        r1 = new java.lang.Object[r10];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2 = 2;
        r1[r2] = r0;
        r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1);
        goto L_0x127e;
    L_0x0fba:
        r2 = 2;
        r0 = NUM; // 0x7f0e072b float:1.887876E38 double:1.053163063E-314;
        r1 = new java.lang.Object[r2];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x0fce:
        r4 = r0.messageOwner;
        r9 = r4.media;
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r9 == 0) goto L_0x101e;
    L_0x0fd6:
        if (r18 != 0) goto L_0x100a;
    L_0x0fd8:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 19;
        if (r1 < r2) goto L_0x100a;
    L_0x0fde:
        r1 = r4.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x100a;
    L_0x0fe6:
        r1 = new java.lang.Object[r10];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r15);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r2 = 2;
        r1[r2] = r0;
        r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1);
        goto L_0x127e;
    L_0x100a:
        r2 = 2;
        r0 = NUM; // 0x7f0e072c float:1.8878761E38 double:1.0531630637E-314;
        r1 = new java.lang.Object[r2];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupPhoto";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x101e:
        r4 = r17.isVideo();
        if (r4 == 0) goto L_0x106e;
    L_0x1024:
        if (r18 != 0) goto L_0x105a;
    L_0x1026:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 19;
        if (r1 < r2) goto L_0x105a;
    L_0x102c:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x105a;
    L_0x1036:
        r1 = new java.lang.Object[r10];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r14);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r4 = 2;
        r1[r4] = r0;
        r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1);
        goto L_0x127e;
    L_0x105a:
        r4 = 2;
        r0 = NUM; // 0x7f0e0733 float:1.8878776E38 double:1.053163067E-314;
        r1 = new java.lang.Object[r4];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = " ";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x106e:
        r4 = 2;
        r9 = r17.isVoice();
        if (r9 == 0) goto L_0x1088;
    L_0x1075:
        r0 = NUM; // 0x7f0e0721 float:1.887874E38 double:1.0531630583E-314;
        r1 = new java.lang.Object[r4];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupAudio";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x1088:
        r9 = r17.isRoundVideo();
        if (r9 == 0) goto L_0x10a1;
    L_0x108e:
        r0 = NUM; // 0x7f0e072f float:1.8878767E38 double:1.053163065E-314;
        r1 = new java.lang.Object[r4];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupRound";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x10a1:
        r9 = r17.isMusic();
        if (r9 == 0) goto L_0x10ba;
    L_0x10a7:
        r0 = NUM; // 0x7f0e072a float:1.8878757E38 double:1.0531630627E-314;
        r1 = new java.lang.Object[r4];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupMusic";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x10ba:
        r4 = r0.messageOwner;
        r4 = r4.media;
        r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r9 == 0) goto L_0x10e2;
    L_0x10c2:
        r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r4;
        r0 = NUM; // 0x7f0e0722 float:1.8878741E38 double:1.053163059E-314;
        r1 = new java.lang.Object[r10];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = r4.first_name;
        r3 = r4.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r3);
        r3 = 2;
        r1[r3] = r2;
        r2 = "NotificationMessageGroupContact2";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x10e2:
        r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r9 == 0) goto L_0x111e;
    L_0x10e6:
        r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4;
        r0 = r4.poll;
        r1 = r0.quiz;
        if (r1 == 0) goto L_0x1106;
    L_0x10ee:
        r1 = NUM; // 0x7f0e072e float:1.8878765E38 double:1.0531630647E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r3 = r13.title;
        r2[r5] = r3;
        r0 = r0.question;
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationMessageGroupQuiz2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x1106:
        r3 = 2;
        r1 = NUM; // 0x7f0e072d float:1.8878763E38 double:1.053163064E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r4 = r13.title;
        r2[r5] = r4;
        r0 = r0.question;
        r2[r3] = r0;
        r0 = "NotificationMessageGroupPoll2";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x111e:
        r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r9 == 0) goto L_0x113c;
    L_0x1122:
        r0 = NUM; // 0x7f0e0724 float:1.8878745E38 double:1.05316306E-314;
        r1 = new java.lang.Object[r10];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = r4.game;
        r2 = r2.title;
        r3 = 2;
        r1[r3] = r2;
        r2 = "NotificationMessageGroupGame";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x113c:
        r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r9 != 0) goto L_0x123a;
    L_0x1140:
        r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r9 == 0) goto L_0x1146;
    L_0x1144:
        goto L_0x123a;
    L_0x1146:
        r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r9 == 0) goto L_0x115e;
    L_0x114a:
        r0 = NUM; // 0x7f0e0728 float:1.8878753E38 double:1.053163062E-314;
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupLiveLocation";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x115e:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r4 == 0) goto L_0x127d;
    L_0x1162:
        r1 = r17.isSticker();
        if (r1 != 0) goto L_0x120a;
    L_0x1168:
        r1 = r17.isAnimatedSticker();
        if (r1 == 0) goto L_0x1170;
    L_0x116e:
        goto L_0x120a;
    L_0x1170:
        r1 = r17.isGif();
        if (r1 == 0) goto L_0x11c0;
    L_0x1176:
        if (r18 != 0) goto L_0x11ac;
    L_0x1178:
        r1 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r1 < r3) goto L_0x11ac;
    L_0x117e:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x11ac;
    L_0x1188:
        r1 = new java.lang.Object[r10];
        r1[r6] = r12;
        r3 = r13.title;
        r1[r5] = r3;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r3.append(r0);
        r0 = r3.toString();
        r2 = 2;
        r1[r2] = r0;
        r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1);
        goto L_0x127e;
    L_0x11ac:
        r2 = 2;
        r0 = NUM; // 0x7f0e0726 float:1.887875E38 double:1.053163061E-314;
        r1 = new java.lang.Object[r2];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupGif";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x11c0:
        if (r18 != 0) goto L_0x11f6;
    L_0x11c2:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 19;
        if (r1 < r2) goto L_0x11f6;
    L_0x11c8:
        r1 = r0.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x11f6;
    L_0x11d2:
        r1 = new java.lang.Object[r10];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r3);
        r0 = r0.messageOwner;
        r0 = r0.message;
        r2.append(r0);
        r0 = r2.toString();
        r2 = 2;
        r1[r2] = r0;
        r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1);
        goto L_0x127e;
    L_0x11f6:
        r2 = 2;
        r0 = NUM; // 0x7f0e0723 float:1.8878743E38 double:1.0531630593E-314;
        r1 = new java.lang.Object[r2];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupDocument";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x120a:
        r0 = r17.getStickerEmoji();
        if (r0 == 0) goto L_0x1226;
    L_0x1210:
        r1 = NUM; // 0x7f0e0731 float:1.8878772E38 double:1.053163066E-314;
        r2 = new java.lang.Object[r10];
        r2[r6] = r12;
        r3 = r13.title;
        r2[r5] = r3;
        r3 = 2;
        r2[r3] = r0;
        r0 = "NotificationMessageGroupStickerEmoji";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2);
        goto L_0x0404;
    L_0x1226:
        r3 = 2;
        r0 = NUM; // 0x7f0e0730 float:1.887877E38 double:1.0531630657E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupSticker";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x0404;
    L_0x123a:
        r3 = 2;
        r0 = NUM; // 0x7f0e0729 float:1.8878755E38 double:1.0531630623E-314;
        r1 = new java.lang.Object[r3];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupMap";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x124d:
        if (r20 == 0) goto L_0x1251;
    L_0x124f:
        r20[r6] = r6;
    L_0x1251:
        r0 = org.telegram.messenger.ChatObject.isChannel(r13);
        if (r0 == 0) goto L_0x1269;
    L_0x1257:
        r0 = r13.megagroup;
        if (r0 != 0) goto L_0x1269;
    L_0x125b:
        r0 = NUM; // 0x7f0e0272 float:1.8876307E38 double:1.053162466E-314;
        r1 = new java.lang.Object[r5];
        r1[r6] = r12;
        r2 = "ChannelMessageNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x1269:
        r0 = NUM; // 0x7f0e072b float:1.887876E38 double:1.053163063E-314;
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r1[r6] = r12;
        r2 = r13.title;
        r1[r5] = r2;
        r2 = "NotificationMessageGroupNoText";
        r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        goto L_0x127e;
    L_0x127c:
        r1 = 0;
    L_0x127d:
        r11 = r1;
    L_0x127e:
        return r11;
    L_0x127f:
        r0 = NUM; // 0x7f0e0cda float:1.888171E38 double:1.053163782E-314;
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
        Object obj = ((int) j2) == 0 ? 1 : null;
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
        if (obj != null) {
            stringBuilder4.append("secret");
        }
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
            NotificationChannel notificationChannel = new NotificationChannel(string, obj != null ? LocaleController.getString("SecretChatName", NUM) : str, i5);
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

    /* JADX WARNING: Removed duplicated region for block: B:436:0x0931 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0927 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x0938 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0acc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0ab3 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b03 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0aea A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0869 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x09a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0ab3 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0acc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0aea A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b03 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0869 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x09a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0acc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0ab3 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b03 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0aea A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0869 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x09a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0ab3 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0acc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0aea A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b03 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0869 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x09a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0acc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0ab3 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b03 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0aea A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0869 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x09a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0ab3 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0acc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0aea A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b03 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x06d9 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x0660 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x080c  */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x07c2 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x0660 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x06d9 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x07c2 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x080c  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0811  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x0580 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x054c A:{SYNTHETIC, Splitter:B:287:0x054c} */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x058c A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x05ba A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x05a2 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x051c A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x051b A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04fc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f9 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0506 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x051b A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x051c A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e6 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f9 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04fc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0506 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x051c A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x051b A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c1 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f8  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e6 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04fc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f9 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0506 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x051b A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x051c A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03ba A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0341 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c1 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f8  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e6 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f9 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04fc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0506 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x051c A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x051b A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02e7 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02b0 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02ed A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02ec A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02f2 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0341 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03ba A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c1 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f8  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e6 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04fc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f9 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0506 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x051b A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x051c A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x020c  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01bb A:{Catch:{ Exception -> 0x0b0f }} */
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
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03ba A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0341 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c1 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f8  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e6 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f9 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04fc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0506 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x051c A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x051b A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01bb A:{Catch:{ Exception -> 0x0b0f }} */
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
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03ba A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c1 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f8  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e6 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04fc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f9 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0506 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x051b A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x051c A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x012b A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00fe A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0130 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x020c  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01bb A:{Catch:{ Exception -> 0x0b0f }} */
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
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03ba A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0341 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c1 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c6 A:{Catch:{ Exception -> 0x02e2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f8  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a8 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e6 A:{SKIP, Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f9 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04fc A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0506 A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x051c A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x051b A:{Catch:{ Exception -> 0x0b0f }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:430:0x0913 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing block: B:396:0x0860, code skipped:
            if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x0862;
     */
    private void showOrUpdateNotification(boolean r46) {
        /*
        r45 = this;
        r12 = r45;
        r13 = r46;
        r1 = "currentAccount";
        r2 = r45.getUserConfig();
        r2 = r2.isClientActivated();
        if (r2 == 0) goto L_0x0b15;
    L_0x0010:
        r2 = r12.pushMessages;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x0b15;
    L_0x0018:
        r2 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts;
        if (r2 != 0) goto L_0x0024;
    L_0x001c:
        r2 = r12.currentAccount;
        r3 = org.telegram.messenger.UserConfig.selectedAccount;
        if (r2 == r3) goto L_0x0024;
    L_0x0022:
        goto L_0x0b15;
    L_0x0024:
        r2 = r45.getConnectionsManager();	 Catch:{ Exception -> 0x0b0f }
        r2.resumeNetworkMaybe();	 Catch:{ Exception -> 0x0b0f }
        r2 = r12.pushMessages;	 Catch:{ Exception -> 0x0b0f }
        r3 = 0;
        r2 = r2.get(r3);	 Catch:{ Exception -> 0x0b0f }
        r2 = (org.telegram.messenger.MessageObject) r2;	 Catch:{ Exception -> 0x0b0f }
        r4 = r45.getAccountInstance();	 Catch:{ Exception -> 0x0b0f }
        r4 = r4.getNotificationsSettings();	 Catch:{ Exception -> 0x0b0f }
        r5 = "dismissDate";
        r5 = r4.getInt(r5, r3);	 Catch:{ Exception -> 0x0b0f }
        r6 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r6 = r6.date;	 Catch:{ Exception -> 0x0b0f }
        if (r6 > r5) goto L_0x004c;
    L_0x0048:
        r45.dismissNotification();	 Catch:{ Exception -> 0x0b0f }
        return;
    L_0x004c:
        r6 = r2.getDialogId();	 Catch:{ Exception -> 0x0b0f }
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r8 = r8.mentioned;	 Catch:{ Exception -> 0x0b0f }
        if (r8 == 0) goto L_0x005c;
    L_0x0056:
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r8 = r8.from_id;	 Catch:{ Exception -> 0x0b0f }
        r8 = (long) r8;	 Catch:{ Exception -> 0x0b0f }
        goto L_0x005d;
    L_0x005c:
        r8 = r6;
    L_0x005d:
        r2.getId();	 Catch:{ Exception -> 0x0b0f }
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0b0f }
        r10 = r10.chat_id;	 Catch:{ Exception -> 0x0b0f }
        if (r10 == 0) goto L_0x006f;
    L_0x0068:
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0b0f }
        r10 = r10.chat_id;	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0075;
    L_0x006f:
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0b0f }
        r10 = r10.channel_id;	 Catch:{ Exception -> 0x0b0f }
    L_0x0075:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r11 = r11.to_id;	 Catch:{ Exception -> 0x0b0f }
        r11 = r11.user_id;	 Catch:{ Exception -> 0x0b0f }
        if (r11 != 0) goto L_0x0082;
    L_0x007d:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r11 = r11.from_id;	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0090;
    L_0x0082:
        r14 = r45.getUserConfig();	 Catch:{ Exception -> 0x0b0f }
        r14 = r14.getClientUserId();	 Catch:{ Exception -> 0x0b0f }
        if (r11 != r14) goto L_0x0090;
    L_0x008c:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r11 = r11.from_id;	 Catch:{ Exception -> 0x0b0f }
    L_0x0090:
        r14 = r45.getMessagesController();	 Catch:{ Exception -> 0x0b0f }
        r15 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0b0f }
        r14 = r14.getUser(r15);	 Catch:{ Exception -> 0x0b0f }
        if (r10 == 0) goto L_0x00ba;
    L_0x009e:
        r15 = r45.getMessagesController();	 Catch:{ Exception -> 0x0b0f }
        r3 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0b0f }
        r15 = r15.getChat(r3);	 Catch:{ Exception -> 0x0b0f }
        r3 = org.telegram.messenger.ChatObject.isChannel(r15);	 Catch:{ Exception -> 0x0b0f }
        if (r3 == 0) goto L_0x00b6;
    L_0x00b0:
        r3 = r15.megagroup;	 Catch:{ Exception -> 0x0b0f }
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
        r5 = r12.getNotifyOverride(r4, r8);	 Catch:{ Exception -> 0x0b0f }
        r20 = r2;
        r2 = -1;
        r21 = r1;
        r1 = 2;
        if (r5 != r2) goto L_0x00cf;
    L_0x00ca:
        r2 = r12.isGlobalNotificationsEnabled(r6);	 Catch:{ Exception -> 0x0b0f }
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
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r5.<init>();	 Catch:{ Exception -> 0x0b0f }
        r8 = "custom_";
        r5.append(r8);	 Catch:{ Exception -> 0x0b0f }
        r5.append(r6);	 Catch:{ Exception -> 0x0b0f }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0b0f }
        r8 = 0;
        r5 = r4.getBoolean(r5, r8);	 Catch:{ Exception -> 0x0b0f }
        if (r5 == 0) goto L_0x012b;
    L_0x00fe:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r5.<init>();	 Catch:{ Exception -> 0x0b0f }
        r8 = "smart_max_count_";
        r5.append(r8);	 Catch:{ Exception -> 0x0b0f }
        r5.append(r6);	 Catch:{ Exception -> 0x0b0f }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0b0f }
        r5 = r4.getInt(r5, r1);	 Catch:{ Exception -> 0x0b0f }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r8.<init>();	 Catch:{ Exception -> 0x0b0f }
        r9 = "smart_delay_";
        r8.append(r9);	 Catch:{ Exception -> 0x0b0f }
        r8.append(r6);	 Catch:{ Exception -> 0x0b0f }
        r8 = r8.toString();	 Catch:{ Exception -> 0x0b0f }
        r9 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r8 = r4.getInt(r8, r9);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x012e;
    L_0x012b:
        r8 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r5 = 2;
    L_0x012e:
        if (r5 == 0) goto L_0x017d;
    L_0x0130:
        r9 = r12.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0b0f }
        r9 = r9.get(r6);	 Catch:{ Exception -> 0x0b0f }
        r9 = (android.graphics.Point) r9;	 Catch:{ Exception -> 0x0b0f }
        if (r9 != 0) goto L_0x014d;
    L_0x013a:
        r5 = new android.graphics.Point;	 Catch:{ Exception -> 0x0b0f }
        r8 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0b0f }
        r8 = r8 / r22;
        r9 = (int) r8;	 Catch:{ Exception -> 0x0b0f }
        r8 = 1;
        r5.<init>(r8, r9);	 Catch:{ Exception -> 0x0b0f }
        r8 = r12.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0b0f }
        r8.put(r6, r5);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x017d;
    L_0x014d:
        r1 = r9.y;	 Catch:{ Exception -> 0x0b0f }
        r1 = r1 + r8;
        r8 = r2;
        r1 = (long) r1;	 Catch:{ Exception -> 0x0b0f }
        r24 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0b0f }
        r24 = r24 / r22;
        r26 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1));
        if (r26 >= 0) goto L_0x0168;
    L_0x015c:
        r1 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0b0f }
        r1 = r1 / r22;
        r2 = (int) r1;	 Catch:{ Exception -> 0x0b0f }
        r1 = 1;
        r9.set(r1, r2);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x017e;
    L_0x0168:
        r1 = r9.x;	 Catch:{ Exception -> 0x0b0f }
        if (r1 >= r5) goto L_0x017a;
    L_0x016c:
        r2 = 1;
        r1 = r1 + r2;
        r24 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0b0f }
        r2 = r14;
        r13 = r24 / r22;
        r5 = (int) r13;	 Catch:{ Exception -> 0x0b0f }
        r9.set(r1, r5);	 Catch:{ Exception -> 0x0b0f }
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
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0b0f }
        r1 = r1.getPath();	 Catch:{ Exception -> 0x0b0f }
        r5 = "EnableInAppSounds";
        r9 = 1;
        r5 = r4.getBoolean(r5, r9);	 Catch:{ Exception -> 0x0b0f }
        r13 = "EnableInAppVibrate";
        r13 = r4.getBoolean(r13, r9);	 Catch:{ Exception -> 0x0b0f }
        r14 = "EnableInAppPreview";
        r14 = r4.getBoolean(r14, r9);	 Catch:{ Exception -> 0x0b0f }
        r9 = "EnableInAppPriority";
        r24 = r14;
        r14 = 0;
        r9 = r4.getBoolean(r9, r14);	 Catch:{ Exception -> 0x0b0f }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r14.<init>();	 Catch:{ Exception -> 0x0b0f }
        r25 = r2;
        r2 = "custom_";
        r14.append(r2);	 Catch:{ Exception -> 0x0b0f }
        r14.append(r6);	 Catch:{ Exception -> 0x0b0f }
        r2 = r14.toString();	 Catch:{ Exception -> 0x0b0f }
        r14 = 0;
        r2 = r4.getBoolean(r2, r14);	 Catch:{ Exception -> 0x0b0f }
        if (r2 == 0) goto L_0x020c;
    L_0x01bb:
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r14.<init>();	 Catch:{ Exception -> 0x0b0f }
        r27 = r15;
        r15 = "vibrate_";
        r14.append(r15);	 Catch:{ Exception -> 0x0b0f }
        r14.append(r6);	 Catch:{ Exception -> 0x0b0f }
        r14 = r14.toString();	 Catch:{ Exception -> 0x0b0f }
        r15 = 0;
        r14 = r4.getInt(r14, r15);	 Catch:{ Exception -> 0x0b0f }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r15.<init>();	 Catch:{ Exception -> 0x0b0f }
        r28 = r14;
        r14 = "priority_";
        r15.append(r14);	 Catch:{ Exception -> 0x0b0f }
        r15.append(r6);	 Catch:{ Exception -> 0x0b0f }
        r14 = r15.toString();	 Catch:{ Exception -> 0x0b0f }
        r15 = 3;
        r14 = r4.getInt(r14, r15);	 Catch:{ Exception -> 0x0b0f }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r15.<init>();	 Catch:{ Exception -> 0x0b0f }
        r29 = r14;
        r14 = "sound_path_";
        r15.append(r14);	 Catch:{ Exception -> 0x0b0f }
        r15.append(r6);	 Catch:{ Exception -> 0x0b0f }
        r14 = r15.toString();	 Catch:{ Exception -> 0x0b0f }
        r15 = 0;
        r14 = r4.getString(r14, r15);	 Catch:{ Exception -> 0x0b0f }
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
        goto L_0x0b10;
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
        if (r2 < r9) goto L_0x03ba;
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
        goto L_0x03bf;
    L_0x0399:
        r9 = 1;
        if (r3 == r9) goto L_0x03b4;
    L_0x039c:
        r9 = 2;
        if (r3 != r9) goto L_0x03a0;
    L_0x039f:
        goto L_0x03b4;
    L_0x03a0:
        r9 = 4;
        if (r3 != r9) goto L_0x03a9;
    L_0x03a3:
        r32 = r2;
        r9 = r8;
        r33 = 1;
        goto L_0x03bf;
    L_0x03a9:
        r9 = 5;
        r32 = r2;
        if (r3 != r9) goto L_0x03b2;
    L_0x03ae:
        r9 = r8;
        r33 = 2;
        goto L_0x03bf;
    L_0x03b2:
        r9 = r8;
        goto L_0x03bd;
    L_0x03b4:
        r32 = r2;
        r9 = r8;
        r33 = 4;
        goto L_0x03bf;
    L_0x03ba:
        r9 = 0;
        r32 = 0;
    L_0x03bd:
        r33 = 0;
    L_0x03bf:
        if (r28 == 0) goto L_0x03c6;
    L_0x03c1:
        r3 = 0;
        r8 = 0;
        r14 = 0;
        r15 = 0;
        goto L_0x03c8;
    L_0x03c6:
        r8 = r29;
    L_0x03c8:
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
        if (r4 == 0) goto L_0x04a8;
    L_0x03f8:
        r12 = r45;
        r5 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b0f }
        r5 = r5.size();	 Catch:{ Exception -> 0x0b0f }
        r13 = 1;
        if (r5 != r13) goto L_0x0413;
    L_0x0403:
        if (r10 == 0) goto L_0x040b;
    L_0x0405:
        r5 = "chatId";
        r2.putExtra(r5, r10);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0413;
    L_0x040b:
        if (r11 == 0) goto L_0x0413;
    L_0x040d:
        r5 = "userId";
        r2.putExtra(r5, r11);	 Catch:{ Exception -> 0x0b0f }
    L_0x0413:
        r5 = org.telegram.messenger.AndroidUtilities.needShowPasscode();	 Catch:{ Exception -> 0x0b0f }
        if (r5 != 0) goto L_0x049d;
    L_0x0419:
        r5 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0b0f }
        if (r5 == 0) goto L_0x041f;
    L_0x041d:
        goto L_0x049d;
    L_0x041f:
        r5 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b0f }
        r5 = r5.size();	 Catch:{ Exception -> 0x0b0f }
        r11 = 1;
        if (r5 != r11) goto L_0x0496;
    L_0x0428:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r11 = 28;
        if (r5 >= r11) goto L_0x0496;
    L_0x042e:
        if (r27 == 0) goto L_0x0466;
    L_0x0430:
        r5 = r27;
        r11 = r5.photo;	 Catch:{ Exception -> 0x0b0f }
        if (r11 == 0) goto L_0x045f;
    L_0x0436:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0b0f }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0b0f }
        if (r11 == 0) goto L_0x045f;
    L_0x043c:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0b0f }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0b0f }
        r27 = r14;
        r13 = r11.volume_id;	 Catch:{ Exception -> 0x0b0f }
        r34 = 0;
        r11 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1));
        if (r11 == 0) goto L_0x0461;
    L_0x044a:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0b0f }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0b0f }
        r11 = r11.local_id;	 Catch:{ Exception -> 0x0b0f }
        if (r11 == 0) goto L_0x0461;
    L_0x0452:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0b0f }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0b0f }
        r29 = r8;
        r13 = r11;
        r11 = r25;
    L_0x045b:
        r25 = r9;
        goto L_0x04d2;
    L_0x045f:
        r27 = r14;
    L_0x0461:
        r29 = r8;
        r11 = r25;
        goto L_0x04a5;
    L_0x0466:
        r5 = r27;
        r27 = r14;
        if (r25 == 0) goto L_0x0493;
    L_0x046c:
        r11 = r25;
        r13 = r11.photo;	 Catch:{ Exception -> 0x0b0f }
        if (r13 == 0) goto L_0x04ce;
    L_0x0472:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0b0f }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0b0f }
        if (r13 == 0) goto L_0x04ce;
    L_0x0478:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0b0f }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0b0f }
        r13 = r13.volume_id;	 Catch:{ Exception -> 0x0b0f }
        r34 = 0;
        r25 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1));
        if (r25 == 0) goto L_0x04ce;
    L_0x0484:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0b0f }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0b0f }
        r13 = r13.local_id;	 Catch:{ Exception -> 0x0b0f }
        if (r13 == 0) goto L_0x04ce;
    L_0x048c:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0b0f }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0b0f }
        r29 = r8;
        goto L_0x045b;
    L_0x0493:
        r11 = r25;
        goto L_0x04ce;
    L_0x0496:
        r11 = r25;
        r5 = r27;
        r27 = r14;
        goto L_0x04ce;
    L_0x049d:
        r11 = r25;
        r5 = r27;
        r27 = r14;
        r29 = r8;
    L_0x04a5:
        r25 = r9;
        goto L_0x04d1;
    L_0x04a8:
        r12 = r45;
        r11 = r25;
        r5 = r27;
        r27 = r14;
        r13 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b0f }
        r13 = r13.size();	 Catch:{ Exception -> 0x0b0f }
        r14 = 1;
        if (r13 != r14) goto L_0x04ce;
    L_0x04b9:
        r13 = globalSecretChatId;	 Catch:{ Exception -> 0x0b0f }
        r25 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1));
        if (r25 == 0) goto L_0x04ce;
    L_0x04bf:
        r13 = "encId";
        r14 = 32;
        r29 = r8;
        r25 = r9;
        r8 = r6 >> r14;
        r9 = (int) r8;	 Catch:{ Exception -> 0x0b0f }
        r2.putExtra(r13, r9);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x04d1;
    L_0x04ce:
        r29 = r8;
        goto L_0x04a5;
    L_0x04d1:
        r13 = 0;
    L_0x04d2:
        r8 = r12.currentAccount;	 Catch:{ Exception -> 0x0b0f }
        r9 = r21;
        r2.putExtra(r9, r8);	 Catch:{ Exception -> 0x0b0f }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b0f }
        r14 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r36 = r6;
        r6 = 0;
        r2 = android.app.PendingIntent.getActivity(r8, r6, r2, r14);	 Catch:{ Exception -> 0x0b0f }
        if (r10 == 0) goto L_0x04e8;
    L_0x04e6:
        if (r5 == 0) goto L_0x04ea;
    L_0x04e8:
        if (r11 != 0) goto L_0x04f5;
    L_0x04ea:
        r6 = r20.isFcmMessage();	 Catch:{ Exception -> 0x0b0f }
        if (r6 == 0) goto L_0x04f5;
    L_0x04f0:
        r6 = r20;
        r7 = r6.localName;	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0500;
    L_0x04f5:
        r6 = r20;
        if (r5 == 0) goto L_0x04fc;
    L_0x04f9:
        r7 = r5.title;	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0500;
    L_0x04fc:
        r7 = org.telegram.messenger.UserObject.getUserName(r11);	 Catch:{ Exception -> 0x0b0f }
    L_0x0500:
        r8 = org.telegram.messenger.AndroidUtilities.needShowPasscode();	 Catch:{ Exception -> 0x0b0f }
        if (r8 != 0) goto L_0x050d;
    L_0x0506:
        r8 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0b0f }
        if (r8 == 0) goto L_0x050b;
    L_0x050a:
        goto L_0x050d;
    L_0x050b:
        r8 = 0;
        goto L_0x050e;
    L_0x050d:
        r8 = 1;
    L_0x050e:
        if (r4 == 0) goto L_0x051f;
    L_0x0510:
        r4 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b0f }
        r4 = r4.size();	 Catch:{ Exception -> 0x0b0f }
        r14 = 1;
        if (r4 > r14) goto L_0x051f;
    L_0x0519:
        if (r8 == 0) goto L_0x051c;
    L_0x051b:
        goto L_0x051f;
    L_0x051c:
        r4 = r7;
        r8 = 1;
        goto L_0x0541;
    L_0x051f:
        if (r8 == 0) goto L_0x0537;
    L_0x0521:
        if (r10 == 0) goto L_0x052d;
    L_0x0523:
        r4 = "NotificationHiddenChatName";
        r8 = NUM; // 0x7f0e0712 float:1.8878709E38 double:1.053163051E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r8);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0540;
    L_0x052d:
        r4 = "NotificationHiddenName";
        r8 = NUM; // 0x7f0e0715 float:1.8878715E38 double:1.0531630524E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r8);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0540;
    L_0x0537:
        r4 = "AppName";
        r8 = NUM; // 0x7f0e0100 float:1.8875557E38 double:1.053162283E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r8);	 Catch:{ Exception -> 0x0b0f }
    L_0x0540:
        r8 = 0;
    L_0x0541:
        r10 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();	 Catch:{ Exception -> 0x0b0f }
        r14 = "";
        r20 = r7;
        r7 = 1;
        if (r10 <= r7) goto L_0x0580;
    L_0x054c:
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b0f }
        r10 = r10.size();	 Catch:{ Exception -> 0x0b0f }
        if (r10 != r7) goto L_0x0561;
    L_0x0554:
        r7 = r45.getUserConfig();	 Catch:{ Exception -> 0x0b0f }
        r7 = r7.getCurrentUser();	 Catch:{ Exception -> 0x0b0f }
        r7 = org.telegram.messenger.UserObject.getFirstName(r7);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0581;
    L_0x0561:
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r7.<init>();	 Catch:{ Exception -> 0x0b0f }
        r10 = r45.getUserConfig();	 Catch:{ Exception -> 0x0b0f }
        r10 = r10.getCurrentUser();	 Catch:{ Exception -> 0x0b0f }
        r10 = org.telegram.messenger.UserObject.getFirstName(r10);	 Catch:{ Exception -> 0x0b0f }
        r7.append(r10);	 Catch:{ Exception -> 0x0b0f }
        r10 = "・";
        r7.append(r10);	 Catch:{ Exception -> 0x0b0f }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0581;
    L_0x0580:
        r7 = r14;
    L_0x0581:
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b0f }
        r10 = r10.size();	 Catch:{ Exception -> 0x0b0f }
        r21 = r1;
        r1 = 1;
        if (r10 != r1) goto L_0x0599;
    L_0x058c:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r10 = 23;
        if (r1 >= r10) goto L_0x0593;
    L_0x0592:
        goto L_0x0599;
    L_0x0593:
        r40 = r3;
        r39 = r15;
    L_0x0597:
        r15 = r7;
        goto L_0x05f4;
    L_0x0599:
        r1 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b0f }
        r1 = r1.size();	 Catch:{ Exception -> 0x0b0f }
        r10 = 1;
        if (r1 != r10) goto L_0x05ba;
    L_0x05a2:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r1.<init>();	 Catch:{ Exception -> 0x0b0f }
        r1.append(r7);	 Catch:{ Exception -> 0x0b0f }
        r7 = "NewMessages";
        r10 = r12.total_unread_count;	 Catch:{ Exception -> 0x0b0f }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r10);	 Catch:{ Exception -> 0x0b0f }
        r1.append(r7);	 Catch:{ Exception -> 0x0b0f }
        r7 = r1.toString();	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0593;
    L_0x05ba:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r1.<init>();	 Catch:{ Exception -> 0x0b0f }
        r1.append(r7);	 Catch:{ Exception -> 0x0b0f }
        r7 = "NotificationMessagesPeopleDisplayOrder";
        r39 = r15;
        r10 = 2;
        r15 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0b0f }
        r10 = "NewMessages";
        r40 = r3;
        r3 = r12.total_unread_count;	 Catch:{ Exception -> 0x0b0f }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r10, r3);	 Catch:{ Exception -> 0x0b0f }
        r10 = 0;
        r15[r10] = r3;	 Catch:{ Exception -> 0x0b0f }
        r3 = "FromChats";
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b0f }
        r10 = r10.size();	 Catch:{ Exception -> 0x0b0f }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r10);	 Catch:{ Exception -> 0x0b0f }
        r10 = 1;
        r15[r10] = r3;	 Catch:{ Exception -> 0x0b0f }
        r3 = NUM; // 0x7f0e0745 float:1.8878812E38 double:1.053163076E-314;
        r3 = org.telegram.messenger.LocaleController.formatString(r7, r3, r15);	 Catch:{ Exception -> 0x0b0f }
        r1.append(r3);	 Catch:{ Exception -> 0x0b0f }
        r7 = r1.toString();	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0597;
    L_0x05f4:
        r10 = new androidx.core.app.NotificationCompat$Builder;	 Catch:{ Exception -> 0x0b0f }
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b0f }
        r10.<init>(r1);	 Catch:{ Exception -> 0x0b0f }
        r10.setContentTitle(r4);	 Catch:{ Exception -> 0x0b0f }
        r1 = NUM; // 0x7var_c float:1.7945673E38 double:1.05293577E-314;
        r10.setSmallIcon(r1);	 Catch:{ Exception -> 0x0b0f }
        r1 = 1;
        r10.setAutoCancel(r1);	 Catch:{ Exception -> 0x0b0f }
        r1 = r12.total_unread_count;	 Catch:{ Exception -> 0x0b0f }
        r10.setNumber(r1);	 Catch:{ Exception -> 0x0b0f }
        r10.setContentIntent(r2);	 Catch:{ Exception -> 0x0b0f }
        r1 = r12.notificationGroup;	 Catch:{ Exception -> 0x0b0f }
        r10.setGroup(r1);	 Catch:{ Exception -> 0x0b0f }
        r1 = 1;
        r10.setGroupSummary(r1);	 Catch:{ Exception -> 0x0b0f }
        r10.setShowWhen(r1);	 Catch:{ Exception -> 0x0b0f }
        r1 = r6.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r1 = r1.date;	 Catch:{ Exception -> 0x0b0f }
        r1 = (long) r1;	 Catch:{ Exception -> 0x0b0f }
        r1 = r1 * r22;
        r10.setWhen(r1);	 Catch:{ Exception -> 0x0b0f }
        r1 = -15618822; // 0xfffffffffvar_acfa float:-1.936362E38 double:NaN;
        r10.setColor(r1);	 Catch:{ Exception -> 0x0b0f }
        r1 = "msg";
        r10.setCategory(r1);	 Catch:{ Exception -> 0x0b0f }
        if (r5 != 0) goto L_0x0657;
    L_0x0633:
        if (r11 == 0) goto L_0x0657;
    L_0x0635:
        r1 = r11.phone;	 Catch:{ Exception -> 0x0b0f }
        if (r1 == 0) goto L_0x0657;
    L_0x0639:
        r1 = r11.phone;	 Catch:{ Exception -> 0x0b0f }
        r1 = r1.length();	 Catch:{ Exception -> 0x0b0f }
        if (r1 <= 0) goto L_0x0657;
    L_0x0641:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r1.<init>();	 Catch:{ Exception -> 0x0b0f }
        r2 = "tel:+";
        r1.append(r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = r11.phone;	 Catch:{ Exception -> 0x0b0f }
        r1.append(r2);	 Catch:{ Exception -> 0x0b0f }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0b0f }
        r10.addPerson(r1);	 Catch:{ Exception -> 0x0b0f }
    L_0x0657:
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0b0f }
        r1 = r1.size();	 Catch:{ Exception -> 0x0b0f }
        r2 = 1;
        if (r1 != r2) goto L_0x06d9;
    L_0x0660:
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0b0f }
        r3 = 0;
        r1 = r1.get(r3);	 Catch:{ Exception -> 0x0b0f }
        r1 = (org.telegram.messenger.MessageObject) r1;	 Catch:{ Exception -> 0x0b0f }
        r7 = new boolean[r2];	 Catch:{ Exception -> 0x0b0f }
        r2 = 0;
        r11 = r12.getStringForMessage(r1, r3, r7, r2);	 Catch:{ Exception -> 0x0b0f }
        r1 = r1.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r1 = r1.silent;	 Catch:{ Exception -> 0x0b0f }
        if (r11 != 0) goto L_0x0677;
    L_0x0676:
        return;
    L_0x0677:
        if (r8 == 0) goto L_0x06c2;
    L_0x0679:
        if (r5 == 0) goto L_0x0691;
    L_0x067b:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r2.<init>();	 Catch:{ Exception -> 0x0b0f }
        r3 = " @ ";
        r2.append(r3);	 Catch:{ Exception -> 0x0b0f }
        r2.append(r4);	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b0f }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x06c3;
    L_0x0691:
        r2 = 0;
        r3 = r7[r2];	 Catch:{ Exception -> 0x0b0f }
        if (r3 == 0) goto L_0x06ac;
    L_0x0696:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r2.<init>();	 Catch:{ Exception -> 0x0b0f }
        r2.append(r4);	 Catch:{ Exception -> 0x0b0f }
        r3 = ": ";
        r2.append(r3);	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b0f }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x06c3;
    L_0x06ac:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r2.<init>();	 Catch:{ Exception -> 0x0b0f }
        r2.append(r4);	 Catch:{ Exception -> 0x0b0f }
        r3 = " ";
        r2.append(r3);	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b0f }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x06c3;
    L_0x06c2:
        r2 = r11;
    L_0x06c3:
        r10.setContentText(r2);	 Catch:{ Exception -> 0x0b0f }
        r3 = new androidx.core.app.NotificationCompat$BigTextStyle;	 Catch:{ Exception -> 0x0b0f }
        r3.<init>();	 Catch:{ Exception -> 0x0b0f }
        r3.bigText(r2);	 Catch:{ Exception -> 0x0b0f }
        r10.setStyle(r3);	 Catch:{ Exception -> 0x0b0f }
        r44 = r6;
        r43 = r9;
        r42 = r13;
        goto L_0x0799;
    L_0x06d9:
        r10.setContentText(r15);	 Catch:{ Exception -> 0x0b0f }
        r1 = new androidx.core.app.NotificationCompat$InboxStyle;	 Catch:{ Exception -> 0x0b0f }
        r1.<init>();	 Catch:{ Exception -> 0x0b0f }
        r1.setBigContentTitle(r4);	 Catch:{ Exception -> 0x0b0f }
        r2 = 10;
        r3 = r12.pushMessages;	 Catch:{ Exception -> 0x0b0f }
        r3 = r3.size();	 Catch:{ Exception -> 0x0b0f }
        r2 = java.lang.Math.min(r2, r3);	 Catch:{ Exception -> 0x0b0f }
        r3 = 1;
        r7 = new boolean[r3];	 Catch:{ Exception -> 0x0b0f }
        r3 = 0;
        r11 = 2;
        r38 = 0;
    L_0x06f7:
        if (r3 >= r2) goto L_0x078a;
    L_0x06f9:
        r41 = r2;
        r2 = r12.pushMessages;	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.get(r3);	 Catch:{ Exception -> 0x0b0f }
        r2 = (org.telegram.messenger.MessageObject) r2;	 Catch:{ Exception -> 0x0b0f }
        r44 = r6;
        r43 = r9;
        r42 = r13;
        r9 = 0;
        r13 = 0;
        r6 = r12.getStringForMessage(r2, r9, r7, r13);	 Catch:{ Exception -> 0x0b0f }
        if (r6 == 0) goto L_0x077a;
    L_0x0711:
        r9 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r9 = r9.date;	 Catch:{ Exception -> 0x0b0f }
        r13 = r19;
        if (r9 > r13) goto L_0x071a;
    L_0x0719:
        goto L_0x077c;
    L_0x071a:
        r9 = 2;
        if (r11 != r9) goto L_0x0723;
    L_0x071d:
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r11 = r2.silent;	 Catch:{ Exception -> 0x0b0f }
        r38 = r6;
    L_0x0723:
        r2 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.size();	 Catch:{ Exception -> 0x0b0f }
        r9 = 1;
        if (r2 != r9) goto L_0x0776;
    L_0x072c:
        if (r8 == 0) goto L_0x0776;
    L_0x072e:
        if (r5 == 0) goto L_0x0746;
    L_0x0730:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r2.<init>();	 Catch:{ Exception -> 0x0b0f }
        r9 = " @ ";
        r2.append(r9);	 Catch:{ Exception -> 0x0b0f }
        r2.append(r4);	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b0f }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0776;
    L_0x0746:
        r2 = 0;
        r9 = r7[r2];	 Catch:{ Exception -> 0x0b0f }
        if (r9 == 0) goto L_0x0761;
    L_0x074b:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r2.<init>();	 Catch:{ Exception -> 0x0b0f }
        r2.append(r4);	 Catch:{ Exception -> 0x0b0f }
        r9 = ": ";
        r2.append(r9);	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b0f }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0776;
    L_0x0761:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r2.<init>();	 Catch:{ Exception -> 0x0b0f }
        r2.append(r4);	 Catch:{ Exception -> 0x0b0f }
        r9 = " ";
        r2.append(r9);	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b0f }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0b0f }
    L_0x0776:
        r1.addLine(r6);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x077c;
    L_0x077a:
        r13 = r19;
    L_0x077c:
        r3 = r3 + 1;
        r19 = r13;
        r2 = r41;
        r13 = r42;
        r9 = r43;
        r6 = r44;
        goto L_0x06f7;
    L_0x078a:
        r44 = r6;
        r43 = r9;
        r42 = r13;
        r1.setSummaryText(r15);	 Catch:{ Exception -> 0x0b0f }
        r10.setStyle(r1);	 Catch:{ Exception -> 0x0b0f }
        r1 = r11;
        r11 = r38;
    L_0x0799:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x0b0f }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b0f }
        r4 = org.telegram.messenger.NotificationDismissReceiver.class;
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x0b0f }
        r3 = "messageDate";
        r4 = r44;
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r5 = r5.date;	 Catch:{ Exception -> 0x0b0f }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0b0f }
        r3 = r12.currentAccount;	 Catch:{ Exception -> 0x0b0f }
        r5 = r43;
        r2.putExtra(r5, r3);	 Catch:{ Exception -> 0x0b0f }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b0f }
        r6 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r7 = 1;
        r2 = android.app.PendingIntent.getBroadcast(r3, r7, r2, r6);	 Catch:{ Exception -> 0x0b0f }
        r10.setDeleteIntent(r2);	 Catch:{ Exception -> 0x0b0f }
        if (r42 == 0) goto L_0x080c;
    L_0x07c2:
        r2 = org.telegram.messenger.ImageLoader.getInstance();	 Catch:{ Exception -> 0x0b0f }
        r3 = "50_50";
        r13 = r42;
        r7 = 0;
        r2 = r2.getImageFromMemory(r13, r7, r3);	 Catch:{ Exception -> 0x0b0f }
        if (r2 == 0) goto L_0x07d9;
    L_0x07d1:
        r2 = r2.getBitmap();	 Catch:{ Exception -> 0x0b0f }
        r10.setLargeIcon(r2);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x080d;
    L_0x07d9:
        r2 = 1;
        r3 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r2);	 Catch:{ all -> 0x080d }
        r2 = r3.exists();	 Catch:{ all -> 0x080d }
        if (r2 == 0) goto L_0x080d;
    L_0x07e4:
        r2 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r8 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ all -> 0x080d }
        r8 = (float) r8;	 Catch:{ all -> 0x080d }
        r2 = r2 / r8;
        r8 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x080d }
        r8.<init>();	 Catch:{ all -> 0x080d }
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r9 >= 0) goto L_0x07fb;
    L_0x07f9:
        r2 = 1;
        goto L_0x07fc;
    L_0x07fb:
        r2 = (int) r2;	 Catch:{ all -> 0x080d }
    L_0x07fc:
        r8.inSampleSize = r2;	 Catch:{ all -> 0x080d }
        r2 = r3.getAbsolutePath();	 Catch:{ all -> 0x080d }
        r2 = android.graphics.BitmapFactory.decodeFile(r2, r8);	 Catch:{ all -> 0x080d }
        if (r2 == 0) goto L_0x080d;
    L_0x0808:
        r10.setLargeIcon(r2);	 Catch:{ all -> 0x080d }
        goto L_0x080d;
    L_0x080c:
        r7 = 0;
    L_0x080d:
        r13 = r46;
        if (r13 == 0) goto L_0x0858;
    L_0x0811:
        r2 = 1;
        if (r1 != r2) goto L_0x0815;
    L_0x0814:
        goto L_0x0858;
    L_0x0815:
        if (r40 != 0) goto L_0x0824;
    L_0x0817:
        r2 = 0;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r3 = 26;
        if (r2 < r3) goto L_0x0865;
    L_0x0821:
        r2 = 1;
        r8 = 3;
        goto L_0x0867;
    L_0x0824:
        r3 = r40;
        r2 = 1;
        if (r3 == r2) goto L_0x084c;
    L_0x0829:
        r2 = 2;
        if (r3 != r2) goto L_0x082e;
    L_0x082c:
        r2 = 1;
        goto L_0x084c;
    L_0x082e:
        r2 = 4;
        if (r3 != r2) goto L_0x083e;
    L_0x0831:
        r2 = -2;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r3 = 26;
        if (r2 < r3) goto L_0x0865;
    L_0x083b:
        r2 = 1;
        r8 = 1;
        goto L_0x0867;
    L_0x083e:
        r2 = 5;
        if (r3 != r2) goto L_0x0865;
    L_0x0841:
        r2 = -1;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r3 = 26;
        if (r2 < r3) goto L_0x0865;
    L_0x084b:
        goto L_0x0862;
    L_0x084c:
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r3 = 26;
        if (r2 < r3) goto L_0x0865;
    L_0x0855:
        r2 = 1;
        r8 = 4;
        goto L_0x0867;
    L_0x0858:
        r2 = -1;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r3 = 26;
        if (r2 < r3) goto L_0x0865;
    L_0x0862:
        r2 = 1;
        r8 = 2;
        goto L_0x0867;
    L_0x0865:
        r2 = 1;
        r8 = 0;
    L_0x0867:
        if (r1 == r2) goto L_0x0990;
    L_0x0869:
        if (r28 != 0) goto L_0x0990;
    L_0x086b:
        r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0b0f }
        if (r1 != 0) goto L_0x0871;
    L_0x086f:
        if (r24 == 0) goto L_0x08a0;
    L_0x0871:
        r1 = r11.length();	 Catch:{ Exception -> 0x0b0f }
        r2 = 100;
        if (r1 <= r2) goto L_0x089d;
    L_0x0879:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0f }
        r1.<init>();	 Catch:{ Exception -> 0x0b0f }
        r2 = 100;
        r3 = 0;
        r2 = r11.substring(r3, r2);	 Catch:{ Exception -> 0x0b0f }
        r3 = 10;
        r9 = 32;
        r2 = r2.replace(r3, r9);	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.trim();	 Catch:{ Exception -> 0x0b0f }
        r1.append(r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = "...";
        r1.append(r2);	 Catch:{ Exception -> 0x0b0f }
        r11 = r1.toString();	 Catch:{ Exception -> 0x0b0f }
    L_0x089d:
        r10.setTicker(r11);	 Catch:{ Exception -> 0x0b0f }
    L_0x08a0:
        r1 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0b0f }
        r1 = r1.isRecordingAudio();	 Catch:{ Exception -> 0x0b0f }
        if (r1 != 0) goto L_0x0924;
    L_0x08aa:
        if (r39 == 0) goto L_0x0924;
    L_0x08ac:
        r1 = "NoSound";
        r2 = r39;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0b0f }
        if (r1 != 0) goto L_0x0924;
    L_0x08b6:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r3 = 26;
        if (r1 < r3) goto L_0x08cc;
    L_0x08bc:
        r1 = r21;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0b0f }
        if (r1 == 0) goto L_0x08c7;
    L_0x08c4:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0925;
    L_0x08c7:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0925;
    L_0x08cc:
        r1 = r21;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0b0f }
        if (r1 == 0) goto L_0x08db;
    L_0x08d4:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0b0f }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0924;
    L_0x08db:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r3 = 24;
        if (r1 < r3) goto L_0x091c;
    L_0x08e1:
        r1 = "file://";
        r1 = r2.startsWith(r1);	 Catch:{ Exception -> 0x0b0f }
        if (r1 == 0) goto L_0x091c;
    L_0x08e9:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0b0f }
        r1 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1);	 Catch:{ Exception -> 0x0b0f }
        if (r1 != 0) goto L_0x091c;
    L_0x08f3:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0913 }
        r3 = "org.telegram.messenger.beta.provider";
        r9 = new java.io.File;	 Catch:{ Exception -> 0x0913 }
        r11 = "file://";
        r11 = r2.replace(r11, r14);	 Catch:{ Exception -> 0x0913 }
        r9.<init>(r11);	 Catch:{ Exception -> 0x0913 }
        r1 = androidx.core.content.FileProvider.getUriForFile(r1, r3, r9);	 Catch:{ Exception -> 0x0913 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0913 }
        r9 = "com.android.systemui";
        r11 = 1;
        r3.grantUriPermission(r9, r1, r11);	 Catch:{ Exception -> 0x0913 }
        r3 = 5;
        r10.setSound(r1, r3);	 Catch:{ Exception -> 0x0913 }
        goto L_0x0924;
    L_0x0913:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0924;
    L_0x091c:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0b0f }
    L_0x0924:
        r1 = r7;
    L_0x0925:
        if (r29 == 0) goto L_0x0931;
    L_0x0927:
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r9 = r29;
        r10.setLights(r9, r2, r3);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0933;
    L_0x0931:
        r9 = r29;
    L_0x0933:
        r14 = r27;
        r2 = 2;
        if (r14 == r2) goto L_0x097f;
    L_0x0938:
        r2 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.isRecordingAudio();	 Catch:{ Exception -> 0x0b0f }
        if (r2 == 0) goto L_0x0944;
    L_0x0942:
        r2 = 2;
        goto L_0x097f;
    L_0x0944:
        r2 = 1;
        if (r14 != r2) goto L_0x095d;
    L_0x0947:
        r3 = 4;
        r3 = new long[r3];	 Catch:{ Exception -> 0x0b0f }
        r7 = 0;
        r21 = 0;
        r3[r7] = r21;	 Catch:{ Exception -> 0x0b0f }
        r27 = 100;
        r3[r2] = r27;	 Catch:{ Exception -> 0x0b0f }
        r2 = 2;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0b0f }
        r2 = 3;
        r3[r2] = r27;	 Catch:{ Exception -> 0x0b0f }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x098c;
    L_0x095d:
        if (r14 == 0) goto L_0x0977;
    L_0x095f:
        r2 = 4;
        if (r14 != r2) goto L_0x0963;
    L_0x0962:
        goto L_0x0977;
    L_0x0963:
        r2 = 3;
        if (r14 != r2) goto L_0x0975;
    L_0x0966:
        r2 = 2;
        r3 = new long[r2];	 Catch:{ Exception -> 0x0b0f }
        r2 = 0;
        r18 = 0;
        r3[r2] = r18;	 Catch:{ Exception -> 0x0b0f }
        r2 = 1;
        r3[r2] = r22;	 Catch:{ Exception -> 0x0b0f }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x098c;
    L_0x0975:
        r11 = r1;
        goto L_0x098e;
    L_0x0977:
        r2 = 2;
        r10.setDefaults(r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = 0;
        r3 = new long[r2];	 Catch:{ Exception -> 0x0b0f }
        goto L_0x098c;
    L_0x097f:
        r3 = new long[r2];	 Catch:{ Exception -> 0x0b0f }
        r2 = 0;
        r21 = 0;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0b0f }
        r2 = 1;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0b0f }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0b0f }
    L_0x098c:
        r11 = r1;
        r7 = r3;
    L_0x098e:
        r1 = 1;
        goto L_0x09a2;
    L_0x0990:
        r9 = r29;
        r1 = 2;
        r2 = new long[r1];	 Catch:{ Exception -> 0x0b0f }
        r1 = 0;
        r21 = 0;
        r2[r1] = r21;	 Catch:{ Exception -> 0x0b0f }
        r1 = 1;
        r2[r1] = r21;	 Catch:{ Exception -> 0x0b0f }
        r10.setVibrate(r2);	 Catch:{ Exception -> 0x0b0f }
        r11 = r7;
        r7 = r2;
    L_0x09a2:
        r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode();	 Catch:{ Exception -> 0x0b0f }
        if (r2 != 0) goto L_0x0a7f;
    L_0x09a8:
        r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0b0f }
        if (r2 != 0) goto L_0x0a7f;
    L_0x09ac:
        r2 = r4.getDialogId();	 Catch:{ Exception -> 0x0b0f }
        r16 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r14 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1));
        if (r14 != 0) goto L_0x0a7f;
    L_0x09b7:
        r2 = r4.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.reply_markup;	 Catch:{ Exception -> 0x0b0f }
        if (r2 == 0) goto L_0x0a7f;
    L_0x09bd:
        r2 = r4.messageOwner;	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.reply_markup;	 Catch:{ Exception -> 0x0b0f }
        r2 = r2.rows;	 Catch:{ Exception -> 0x0b0f }
        r3 = r2.size();	 Catch:{ Exception -> 0x0b0f }
        r14 = 0;
        r16 = 0;
    L_0x09ca:
        if (r14 >= r3) goto L_0x0a77;
    L_0x09cc:
        r17 = r2.get(r14);	 Catch:{ Exception -> 0x0b0f }
        r1 = r17;
        r1 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r1;	 Catch:{ Exception -> 0x0b0f }
        r6 = r1.buttons;	 Catch:{ Exception -> 0x0b0f }
        r6 = r6.size();	 Catch:{ Exception -> 0x0b0f }
        r21 = r2;
        r2 = 0;
    L_0x09dd:
        if (r2 >= r6) goto L_0x0a5b;
    L_0x09df:
        r22 = r3;
        r3 = r1.buttons;	 Catch:{ Exception -> 0x0b0f }
        r3 = r3.get(r2);	 Catch:{ Exception -> 0x0b0f }
        r3 = (org.telegram.tgnet.TLRPC.KeyboardButton) r3;	 Catch:{ Exception -> 0x0b0f }
        r23 = r1;
        r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;	 Catch:{ Exception -> 0x0b0f }
        if (r1 == 0) goto L_0x0a3b;
    L_0x09ef:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0b0f }
        r24 = r6;
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b0f }
        r13 = org.telegram.messenger.NotificationCallbackReceiver.class;
        r1.<init>(r6, r13);	 Catch:{ Exception -> 0x0b0f }
        r6 = r12.currentAccount;	 Catch:{ Exception -> 0x0b0f }
        r1.putExtra(r5, r6);	 Catch:{ Exception -> 0x0b0f }
        r6 = "did";
        r13 = r8;
        r29 = r9;
        r8 = r36;
        r1.putExtra(r6, r8);	 Catch:{ Exception -> 0x0b0f }
        r6 = r3.data;	 Catch:{ Exception -> 0x0b0f }
        if (r6 == 0) goto L_0x0a17;
    L_0x0a0d:
        r6 = "data";
        r26 = r15;
        r15 = r3.data;	 Catch:{ Exception -> 0x0b0f }
        r1.putExtra(r6, r15);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0a19;
    L_0x0a17:
        r26 = r15;
    L_0x0a19:
        r6 = "mid";
        r15 = r4.getId();	 Catch:{ Exception -> 0x0b0f }
        r1.putExtra(r6, r15);	 Catch:{ Exception -> 0x0b0f }
        r3 = r3.text;	 Catch:{ Exception -> 0x0b0f }
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b0f }
        r15 = r12.lastButtonId;	 Catch:{ Exception -> 0x0b0f }
        r44 = r4;
        r4 = r15 + 1;
        r12.lastButtonId = r4;	 Catch:{ Exception -> 0x0b0f }
        r4 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r1 = android.app.PendingIntent.getBroadcast(r6, r15, r1, r4);	 Catch:{ Exception -> 0x0b0f }
        r4 = 0;
        r10.addAction(r4, r3, r1);	 Catch:{ Exception -> 0x0b0f }
        r16 = 1;
        goto L_0x0a47;
    L_0x0a3b:
        r44 = r4;
        r24 = r6;
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        r4 = 0;
    L_0x0a47:
        r2 = r2 + 1;
        r36 = r8;
        r8 = r13;
        r3 = r22;
        r1 = r23;
        r6 = r24;
        r15 = r26;
        r9 = r29;
        r4 = r44;
        r13 = r46;
        goto L_0x09dd;
    L_0x0a5b:
        r22 = r3;
        r44 = r4;
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        r4 = 0;
        r14 = r14 + 1;
        r8 = r13;
        r2 = r21;
        r9 = r29;
        r4 = r44;
        r1 = 1;
        r6 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r13 = r46;
        goto L_0x09ca;
    L_0x0a77:
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        goto L_0x0a89;
    L_0x0a7f:
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        r4 = 0;
        r16 = 0;
    L_0x0a89:
        if (r16 != 0) goto L_0x0ae4;
    L_0x0a8b:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r2 = 24;
        if (r1 >= r2) goto L_0x0ae4;
    L_0x0a91:
        r1 = org.telegram.messenger.SharedConfig.passcodeHash;	 Catch:{ Exception -> 0x0b0f }
        r1 = r1.length();	 Catch:{ Exception -> 0x0b0f }
        if (r1 != 0) goto L_0x0ae4;
    L_0x0a99:
        r1 = r45.hasMessagesToReply();	 Catch:{ Exception -> 0x0b0f }
        if (r1 == 0) goto L_0x0ae4;
    L_0x0a9f:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0b0f }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b0f }
        r3 = org.telegram.messenger.PopupReplyReceiver.class;
        r1.<init>(r2, r3);	 Catch:{ Exception -> 0x0b0f }
        r2 = r12.currentAccount;	 Catch:{ Exception -> 0x0b0f }
        r1.putExtra(r5, r2);	 Catch:{ Exception -> 0x0b0f }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r3 = 19;
        if (r2 > r3) goto L_0x0acc;
    L_0x0ab3:
        r2 = NUM; // 0x7var_fc float:1.794509E38 double:1.0529356275E-314;
        r3 = "Reply";
        r4 = NUM; // 0x7f0e097d float:1.8879964E38 double:1.0531633567E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0b0f }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b0f }
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r6 = 2;
        r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5);	 Catch:{ Exception -> 0x0b0f }
        r10.addAction(r2, r3, r1);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0ae4;
    L_0x0acc:
        r2 = NUM; // 0x7var_fb float:1.7945087E38 double:1.052935627E-314;
        r3 = "Reply";
        r4 = NUM; // 0x7f0e097d float:1.8879964E38 double:1.0531633567E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0b0f }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b0f }
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r6 = 2;
        r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5);	 Catch:{ Exception -> 0x0b0f }
        r10.addAction(r2, r3, r1);	 Catch:{ Exception -> 0x0b0f }
    L_0x0ae4:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b0f }
        r2 = 26;
        if (r1 < r2) goto L_0x0b03;
    L_0x0aea:
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
        r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0b0f }
        r13.setChannelId(r1);	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0b04;
    L_0x0b03:
        r13 = r10;
    L_0x0b04:
        r1 = r46;
        r7 = r26;
        r12.showExtraNotifications(r13, r1, r7);	 Catch:{ Exception -> 0x0b0f }
        r45.scheduleNotificationRepeat();	 Catch:{ Exception -> 0x0b0f }
        goto L_0x0b14;
    L_0x0b0f:
        r0 = move-exception;
    L_0x0b10:
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0b14:
        return;
    L_0x0b15:
        r45.dismissNotification();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:77:0x01a9  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x0784  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x07a0  */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x0839 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x0829  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x056e  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x053b  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04b6  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04b0  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x04e5  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x0896  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x08b6  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0945  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0912  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0988  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0966  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0a63  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a7d  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0a82  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a96  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0b50  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0ba1 A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0bca A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0bdc A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0bd5 A:{SYNTHETIC, Splitter:B:461:0x0bd5} */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02e2  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02c4  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0340  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02ed  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x03ac  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x039a  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x03ed  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04b0  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04b6  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x04cc A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x04e5  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x0896  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x08b6  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0912  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0945  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0966  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0988  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0a63  */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x0a6d  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a7d  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0a82  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a96  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0b50  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0ba1 A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0bca A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0bd5 A:{SYNTHETIC, Splitter:B:461:0x0bd5} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0bdc A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04b6  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04b0  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x04cc A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x04e5  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x0896  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x08b6  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0945  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0912  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0988  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0966  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0a63  */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x0a6d  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a7d  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0a82  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a96  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0b50  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0ba1 A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0bca A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0bdc A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0bd5 A:{SYNTHETIC, Splitter:B:461:0x0bd5} */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04b0  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04b6  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x04cc A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x04e5  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x0896  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x08b6  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0912  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0945  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0966  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0988  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0a63  */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x0a6d  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a7d  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0a82  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a96  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0b50  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0ba1 A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0bca A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0bd5 A:{SYNTHETIC, Splitter:B:461:0x0bd5} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0bdc A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04b6  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04b0  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x04cc A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x04e5  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x0896  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x08b6  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0945  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0912  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0988  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0966  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0a63  */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x0a6d  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a7d  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0a82  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a96  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0b50  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0ba1 A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0bca A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0bdc A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0bd5 A:{SYNTHETIC, Splitter:B:461:0x0bd5} */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04b0  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04b6  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x04cc A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x04e5  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x0896  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x08b6  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0912  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0945  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0966  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0988  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0a63  */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x0a6d  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a7d  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0a82  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a96  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0b50  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0ba1 A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0bca A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0bd5 A:{SYNTHETIC, Splitter:B:461:0x0bd5} */
    /* JADX WARNING: Removed duplicated region for block: B:463:0x0bdc A:{Catch:{ JSONException -> 0x0bf4 }} */
    /* JADX WARNING: Missing block: B:74:0x01a3, code skipped:
            if (r13.local_id != 0) goto L_0x01a7;
     */
    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r63, boolean r64, java.lang.String r65) {
        /*
        r62 = this;
        r1 = r62;
        r2 = r63.build();
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 18;
        if (r0 >= r3) goto L_0x001d;
    L_0x000c:
        r0 = notificationManager;
        r3 = r1.notificationId;
        r0.notify(r3, r2);
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x001c;
    L_0x0017:
        r0 = "show summary notification by SDK check";
        org.telegram.messenger.FileLog.d(r0);
    L_0x001c:
        return;
    L_0x001d:
        r0 = r62.getAccountInstance();
        r0 = r0.getNotificationsSettings();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = new android.util.LongSparseArray;
        r4.<init>();
        r5 = 0;
        r6 = 0;
    L_0x0031:
        r7 = r1.pushMessages;
        r7 = r7.size();
        if (r6 >= r7) goto L_0x007e;
    L_0x0039:
        r7 = r1.pushMessages;
        r7 = r7.get(r6);
        r7 = (org.telegram.messenger.MessageObject) r7;
        r8 = r7.getDialogId();
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "dismissDate";
        r10.append(r11);
        r10.append(r8);
        r10 = r10.toString();
        r10 = r0.getInt(r10, r5);
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
        r0 = r1.wearNotificationsIds;
        r6 = r0.clone();
        r0 = r1.wearNotificationsIds;
        r0.clear();
        r7 = new java.util.ArrayList;
        r7.<init>();
        r0 = org.telegram.messenger.WearDataLayerListenerService.isWatchConnected();
        if (r0 == 0) goto L_0x009b;
    L_0x0094:
        r0 = new org.json.JSONArray;
        r0.<init>();
        r9 = r0;
        goto L_0x009c;
    L_0x009b:
        r9 = 0;
    L_0x009c:
        r0 = android.os.Build.VERSION.SDK_INT;
        r10 = 27;
        r11 = 1;
        if (r0 <= r10) goto L_0x00ae;
    L_0x00a3:
        if (r0 <= r10) goto L_0x00ac;
    L_0x00a5:
        r0 = r3.size();
        if (r0 <= r11) goto L_0x00ac;
    L_0x00ab:
        goto L_0x00ae;
    L_0x00ac:
        r12 = 0;
        goto L_0x00af;
    L_0x00ae:
        r12 = 1;
    L_0x00af:
        r13 = 26;
        if (r12 == 0) goto L_0x00ba;
    L_0x00b3:
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r13) goto L_0x00ba;
    L_0x00b7:
        checkOtherNotificationsChannel();
    L_0x00ba:
        r0 = r62.getUserConfig();
        r14 = r0.getClientUserId();
        r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode();
        if (r0 != 0) goto L_0x00cf;
    L_0x00c8:
        r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r0 == 0) goto L_0x00cd;
    L_0x00cc:
        goto L_0x00cf;
    L_0x00cd:
        r15 = 0;
        goto L_0x00d0;
    L_0x00cf:
        r15 = 1;
    L_0x00d0:
        r13 = r3.size();
        r10 = 0;
    L_0x00d5:
        if (r10 >= r13) goto L_0x0c0e;
    L_0x00d7:
        r0 = r3.get(r10);
        r0 = (java.lang.Long) r0;
        r17 = r12;
        r11 = r0.longValue();
        r0 = r4.get(r11);
        r8 = r0;
        r8 = (java.util.ArrayList) r8;
        r0 = r8.get(r5);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r5 = r0.getId();
        r20 = r4;
        r4 = (int) r11;
        r0 = 32;
        r21 = r2;
        r22 = r3;
        r2 = r11 >> r0;
        r3 = (int) r2;
        r0 = r6.get(r11);
        r0 = (java.lang.Integer) r0;
        if (r0 != 0) goto L_0x0114;
    L_0x0108:
        if (r4 == 0) goto L_0x010f;
    L_0x010a:
        r0 = java.lang.Integer.valueOf(r4);
        goto L_0x0117;
    L_0x010f:
        r0 = java.lang.Integer.valueOf(r3);
        goto L_0x0117;
    L_0x0114:
        r6.remove(r11);
    L_0x0117:
        r2 = r0;
        if (r9 == 0) goto L_0x0122;
    L_0x011a:
        r0 = new org.json.JSONObject;
        r0.<init>();
        r23 = r13;
        goto L_0x0125;
    L_0x0122:
        r23 = r13;
        r0 = 0;
    L_0x0125:
        r13 = 0;
        r24 = r8.get(r13);
        r13 = r24;
        r13 = (org.telegram.messenger.MessageObject) r13;
        r24 = r0;
        r0 = r13.messageOwner;
        r25 = r6;
        r6 = r0.date;
        r26 = r10;
        r10 = new android.util.LongSparseArray;
        r10.<init>();
        r27 = 0;
        if (r4 == 0) goto L_0x0243;
    L_0x0141:
        r0 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r4 == r0) goto L_0x0148;
    L_0x0146:
        r0 = 1;
        goto L_0x0149;
    L_0x0148:
        r0 = 0;
    L_0x0149:
        if (r4 <= 0) goto L_0x01c0;
    L_0x014b:
        r29 = r0;
        r0 = r62.getMessagesController();
        r30 = r9;
        r9 = java.lang.Integer.valueOf(r4);
        r0 = r0.getUser(r9);
        if (r0 != 0) goto L_0x018b;
    L_0x015d:
        r9 = r13.isFcmMessage();
        if (r9 == 0) goto L_0x016a;
    L_0x0163:
        r9 = r13.localName;
    L_0x0165:
        r32 = r6;
        r31 = r7;
        goto L_0x01a6;
    L_0x016a:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0182;
    L_0x016e:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "not found user to show dialog notification ";
        r0.append(r2);
        r0.append(r4);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.w(r0);
    L_0x0182:
        r6 = r7;
        r46 = r14;
        r36 = r15;
        r4 = r30;
        goto L_0x027d;
    L_0x018b:
        r9 = org.telegram.messenger.UserObject.getUserName(r0);
        r13 = r0.photo;
        if (r13 == 0) goto L_0x0165;
    L_0x0193:
        r13 = r13.photo_small;
        if (r13 == 0) goto L_0x0165;
    L_0x0197:
        r32 = r6;
        r31 = r7;
        r6 = r13.volume_id;
        r33 = (r6 > r27 ? 1 : (r6 == r27 ? 0 : -1));
        if (r33 == 0) goto L_0x01a6;
    L_0x01a1:
        r6 = r13.local_id;
        if (r6 == 0) goto L_0x01a6;
    L_0x01a5:
        goto L_0x01a7;
    L_0x01a6:
        r13 = 0;
    L_0x01a7:
        if (r4 != r14) goto L_0x01b2;
    L_0x01a9:
        r6 = NUM; // 0x7f0e0658 float:1.8878331E38 double:1.053162959E-314;
        r7 = "MessageScheduledReminderNotification";
        r9 = org.telegram.messenger.LocaleController.getString(r7, r6);
    L_0x01b2:
        r6 = r24;
        r7 = 0;
        r33 = 0;
        r34 = 0;
        r61 = r9;
        r9 = r0;
        r0 = r61;
        goto L_0x02c2;
    L_0x01c0:
        r29 = r0;
        r32 = r6;
        r31 = r7;
        r30 = r9;
        r0 = r62.getMessagesController();
        r6 = -r4;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r0.getChat(r6);
        if (r0 != 0) goto L_0x020b;
    L_0x01d7:
        r6 = r13.isFcmMessage();
        if (r6 == 0) goto L_0x01f1;
    L_0x01dd:
        r6 = r13.isMegagroup();
        r9 = r13.localName;
        r7 = r13.localChannel;
        r33 = r6;
        r34 = r7;
        r6 = r24;
        r13 = 0;
        r7 = r0;
        r0 = r9;
    L_0x01ee:
        r9 = 0;
        goto L_0x02c2;
    L_0x01f1:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0275;
    L_0x01f5:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "not found chat to show dialog notification ";
        r0.append(r2);
        r0.append(r4);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.w(r0);
        goto L_0x0275;
    L_0x020b:
        r6 = r0.megagroup;
        r7 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r7 == 0) goto L_0x0219;
    L_0x0213:
        r7 = r0.megagroup;
        if (r7 != 0) goto L_0x0219;
    L_0x0217:
        r7 = 1;
        goto L_0x021a;
    L_0x0219:
        r7 = 0;
    L_0x021a:
        r9 = r0.title;
        r13 = r0.photo;
        if (r13 == 0) goto L_0x0237;
    L_0x0220:
        r13 = r13.photo_small;
        if (r13 == 0) goto L_0x0237;
    L_0x0224:
        r33 = r6;
        r34 = r7;
        r6 = r13.volume_id;
        r35 = (r6 > r27 ? 1 : (r6 == r27 ? 0 : -1));
        if (r35 == 0) goto L_0x023b;
    L_0x022e:
        r6 = r13.local_id;
        if (r6 == 0) goto L_0x023b;
    L_0x0232:
        r7 = r0;
        r0 = r9;
        r6 = r24;
        goto L_0x01ee;
    L_0x0237:
        r33 = r6;
        r34 = r7;
    L_0x023b:
        r7 = r0;
        r0 = r9;
        r6 = r24;
        r9 = 0;
        r13 = 0;
        goto L_0x02c2;
    L_0x0243:
        r32 = r6;
        r31 = r7;
        r30 = r9;
        r6 = globalSecretChatId;
        r0 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1));
        if (r0 == 0) goto L_0x02ad;
    L_0x024f:
        r0 = r62.getMessagesController();
        r6 = java.lang.Integer.valueOf(r3);
        r0 = r0.getEncryptedChat(r6);
        if (r0 != 0) goto L_0x0282;
    L_0x025d:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0275;
    L_0x0261:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "not found secret chat to show dialog notification ";
        r0.append(r2);
        r0.append(r3);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.w(r0);
    L_0x0275:
        r46 = r14;
        r36 = r15;
        r4 = r30;
        r6 = r31;
    L_0x027d:
        r2 = 26;
        r3 = 0;
        goto L_0x0bf6;
    L_0x0282:
        r6 = r62.getMessagesController();
        r7 = r0.user_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getUser(r7);
        if (r6 != 0) goto L_0x02ae;
    L_0x0292:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0275;
    L_0x0296:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not found secret chat user to show dialog notification ";
        r2.append(r3);
        r0 = r0.user_id;
        r2.append(r0);
        r0 = r2.toString();
        org.telegram.messenger.FileLog.w(r0);
        goto L_0x0275;
    L_0x02ad:
        r6 = 0;
    L_0x02ae:
        r0 = NUM; // 0x7f0e09f7 float:1.8880212E38 double:1.053163417E-314;
        r7 = "SecretChatName";
        r9 = org.telegram.messenger.LocaleController.getString(r7, r0);
        r0 = r9;
        r7 = 0;
        r13 = 0;
        r29 = 0;
        r33 = 0;
        r34 = 0;
        r9 = r6;
        r6 = 0;
    L_0x02c2:
        if (r15 == 0) goto L_0x02e2;
    L_0x02c4:
        if (r4 >= 0) goto L_0x02d0;
    L_0x02c6:
        r0 = NUM; // 0x7f0e0712 float:1.8878709E38 double:1.053163051E-314;
        r13 = "NotificationHiddenChatName";
        r0 = org.telegram.messenger.LocaleController.getString(r13, r0);
        goto L_0x02d9;
    L_0x02d0:
        r0 = NUM; // 0x7f0e0715 float:1.8878715E38 double:1.0531630524E-314;
        r13 = "NotificationHiddenName";
        r0 = org.telegram.messenger.LocaleController.getString(r13, r0);
    L_0x02d9:
        r35 = r3;
        r29 = r7;
        r24 = r9;
        r9 = 0;
        r13 = 0;
        goto L_0x02ea;
    L_0x02e2:
        r35 = r3;
        r24 = r9;
        r9 = r29;
        r29 = r7;
    L_0x02ea:
        r7 = r0;
        if (r13 == 0) goto L_0x0340;
    L_0x02ed:
        r3 = 1;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r3);
        r3 = android.os.Build.VERSION.SDK_INT;
        r36 = r15;
        r15 = 28;
        if (r3 >= r15) goto L_0x033a;
    L_0x02fa:
        r3 = org.telegram.messenger.ImageLoader.getInstance();
        r15 = "50_50";
        r37 = r6;
        r6 = 0;
        r3 = r3.getImageFromMemory(r13, r6, r15);
        if (r3 == 0) goto L_0x030f;
    L_0x0309:
        r3 = r3.getBitmap();
    L_0x030d:
        r15 = r0;
        goto L_0x0347;
    L_0x030f:
        r3 = r0.exists();	 Catch:{ all -> 0x033d }
        if (r3 == 0) goto L_0x0338;
    L_0x0315:
        r3 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r15 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);	 Catch:{ all -> 0x033d }
        r15 = (float) r15;	 Catch:{ all -> 0x033d }
        r3 = r3 / r15;
        r15 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x033d }
        r15.<init>();	 Catch:{ all -> 0x033d }
        r18 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r18 = (r3 > r18 ? 1 : (r3 == r18 ? 0 : -1));
        if (r18 >= 0) goto L_0x032c;
    L_0x032a:
        r3 = 1;
        goto L_0x032d;
    L_0x032c:
        r3 = (int) r3;	 Catch:{ all -> 0x033d }
    L_0x032d:
        r15.inSampleSize = r3;	 Catch:{ all -> 0x033d }
        r3 = r0.getAbsolutePath();	 Catch:{ all -> 0x033d }
        r3 = android.graphics.BitmapFactory.decodeFile(r3, r15);	 Catch:{ all -> 0x033d }
        goto L_0x030d;
    L_0x0338:
        r3 = r6;
        goto L_0x030d;
    L_0x033a:
        r37 = r6;
        r6 = 0;
    L_0x033d:
        r15 = r0;
        r3 = r6;
        goto L_0x0347;
    L_0x0340:
        r37 = r6;
        r36 = r15;
        r6 = 0;
        r3 = r6;
        r15 = r3;
    L_0x0347:
        r6 = "max_id";
        r38 = r15;
        r15 = "currentAccount";
        if (r34 == 0) goto L_0x0351;
    L_0x034f:
        if (r33 == 0) goto L_0x03d9;
    L_0x0351:
        if (r9 == 0) goto L_0x03d9;
    L_0x0353:
        r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r0 != 0) goto L_0x03d9;
    L_0x0357:
        if (r14 == r4) goto L_0x03d9;
    L_0x0359:
        r0 = new android.content.Intent;
        r39 = r13;
        r13 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r40 = r9;
        r9 = org.telegram.messenger.WearReplyReceiver.class;
        r0.<init>(r13, r9);
        r9 = "dialog_id";
        r0.putExtra(r9, r11);
        r0.putExtra(r6, r5);
        r9 = r1.currentAccount;
        r0.putExtra(r15, r9);
        r9 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r13 = r2.intValue();
        r41 = r3;
        r3 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r0 = android.app.PendingIntent.getBroadcast(r9, r13, r0, r3);
        r3 = new androidx.core.app.RemoteInput$Builder;
        r9 = "extra_voice_reply";
        r3.<init>(r9);
        r9 = NUM; // 0x7f0e097d float:1.8879964E38 double:1.0531633567E-314;
        r13 = "Reply";
        r9 = org.telegram.messenger.LocaleController.getString(r13, r9);
        r3.setLabel(r9);
        r3 = r3.build();
        if (r4 >= 0) goto L_0x03ac;
    L_0x039a:
        r13 = 1;
        r9 = new java.lang.Object[r13];
        r13 = 0;
        r9[r13] = r7;
        r13 = "ReplyToGroup";
        r42 = r2;
        r2 = NUM; // 0x7f0e097e float:1.8879966E38 double:1.053163357E-314;
        r2 = org.telegram.messenger.LocaleController.formatString(r13, r2, r9);
        goto L_0x03bd;
    L_0x03ac:
        r42 = r2;
        r2 = NUM; // 0x7f0e097f float:1.8879968E38 double:1.0531633577E-314;
        r9 = 1;
        r13 = new java.lang.Object[r9];
        r9 = 0;
        r13[r9] = r7;
        r9 = "ReplyToUser";
        r2 = org.telegram.messenger.LocaleController.formatString(r9, r2, r13);
    L_0x03bd:
        r9 = new androidx.core.app.NotificationCompat$Action$Builder;
        r13 = NUM; // 0x7var_ float:1.7945176E38 double:1.052935649E-314;
        r9.<init>(r13, r2, r0);
        r2 = 1;
        r9.setAllowGeneratedReplies(r2);
        r9.setSemanticAction(r2);
        r9.addRemoteInput(r3);
        r2 = 0;
        r9.setShowsUserInterface(r2);
        r0 = r9.build();
        r3 = r0;
        goto L_0x03e3;
    L_0x03d9:
        r42 = r2;
        r41 = r3;
        r40 = r9;
        r39 = r13;
        r2 = 0;
        r3 = 0;
    L_0x03e3:
        r0 = r1.pushDialogs;
        r0 = r0.get(r11);
        r0 = (java.lang.Integer) r0;
        if (r0 != 0) goto L_0x03f1;
    L_0x03ed:
        r0 = java.lang.Integer.valueOf(r2);
    L_0x03f1:
        r0 = r0.intValue();
        r2 = r8.size();
        r0 = java.lang.Math.max(r0, r2);
        r2 = 2;
        r9 = 1;
        if (r0 <= r9) goto L_0x041d;
    L_0x0401:
        r13 = android.os.Build.VERSION.SDK_INT;
        r9 = 28;
        if (r13 < r9) goto L_0x0408;
    L_0x0407:
        goto L_0x041d;
    L_0x0408:
        r9 = new java.lang.Object[r2];
        r13 = 0;
        r9[r13] = r7;
        r0 = java.lang.Integer.valueOf(r0);
        r13 = 1;
        r9[r13] = r0;
        r0 = "%1$s (%2$d)";
        r0 = java.lang.String.format(r0, r9);
        r9 = r0;
        r13 = r3;
        goto L_0x041f;
    L_0x041d:
        r13 = r3;
        r9 = r7;
    L_0x041f:
        r2 = (long) r14;
        r0 = r10.get(r2);
        r43 = r0;
        r43 = (androidx.core.app.Person) r43;
        r0 = android.os.Build.VERSION.SDK_INT;
        r44 = r5;
        r5 = 28;
        if (r0 < r5) goto L_0x04a7;
    L_0x0430:
        if (r43 != 0) goto L_0x04a7;
    L_0x0432:
        r0 = r62.getMessagesController();
        r5 = java.lang.Integer.valueOf(r14);
        r0 = r0.getUser(r5);
        if (r0 != 0) goto L_0x0448;
    L_0x0440:
        r0 = r62.getUserConfig();
        r0 = r0.getCurrentUser();
    L_0x0448:
        if (r0 == 0) goto L_0x04a2;
    L_0x044a:
        r5 = r0.photo;	 Catch:{ all -> 0x0499 }
        if (r5 == 0) goto L_0x04a2;
    L_0x044e:
        r5 = r0.photo;	 Catch:{ all -> 0x0499 }
        r5 = r5.photo_small;	 Catch:{ all -> 0x0499 }
        if (r5 == 0) goto L_0x04a2;
    L_0x0454:
        r5 = r0.photo;	 Catch:{ all -> 0x0499 }
        r5 = r5.photo_small;	 Catch:{ all -> 0x0499 }
        r45 = r6;
        r5 = r5.volume_id;	 Catch:{ all -> 0x0497 }
        r46 = (r5 > r27 ? 1 : (r5 == r27 ? 0 : -1));
        if (r46 == 0) goto L_0x04a4;
    L_0x0460:
        r5 = r0.photo;	 Catch:{ all -> 0x0497 }
        r5 = r5.photo_small;	 Catch:{ all -> 0x0497 }
        r5 = r5.local_id;	 Catch:{ all -> 0x0497 }
        if (r5 == 0) goto L_0x04a4;
    L_0x0468:
        r5 = new androidx.core.app.Person$Builder;	 Catch:{ all -> 0x0497 }
        r5.<init>();	 Catch:{ all -> 0x0497 }
        r6 = "FromYou";
        r46 = r14;
        r14 = NUM; // 0x7f0e0525 float:1.8877709E38 double:1.0531628073E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r14);	 Catch:{ all -> 0x0495 }
        r5.setName(r6);	 Catch:{ all -> 0x0495 }
        r0 = r0.photo;	 Catch:{ all -> 0x0495 }
        r0 = r0.photo_small;	 Catch:{ all -> 0x0495 }
        r6 = 1;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r6);	 Catch:{ all -> 0x0495 }
        r1.loadRoundAvatar(r0, r5);	 Catch:{ all -> 0x0495 }
        r5 = r5.build();	 Catch:{ all -> 0x0495 }
        r10.put(r2, r5);	 Catch:{ all -> 0x0491 }
        r43 = r5;
        goto L_0x04aa;
    L_0x0491:
        r0 = move-exception;
        r43 = r5;
        goto L_0x049e;
    L_0x0495:
        r0 = move-exception;
        goto L_0x049e;
    L_0x0497:
        r0 = move-exception;
        goto L_0x049c;
    L_0x0499:
        r0 = move-exception;
        r45 = r6;
    L_0x049c:
        r46 = r14;
    L_0x049e:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x04aa;
    L_0x04a2:
        r45 = r6;
    L_0x04a4:
        r46 = r14;
        goto L_0x04aa;
    L_0x04a7:
        r45 = r6;
        goto L_0x04a4;
    L_0x04aa:
        r0 = r43;
        r5 = "";
        if (r0 == 0) goto L_0x04b6;
    L_0x04b0:
        r6 = new androidx.core.app.NotificationCompat$MessagingStyle;
        r6.<init>(r0);
        goto L_0x04bb;
    L_0x04b6:
        r6 = new androidx.core.app.NotificationCompat$MessagingStyle;
        r6.<init>(r5);
    L_0x04bb:
        r0 = android.os.Build.VERSION.SDK_INT;
        r14 = 28;
        if (r0 < r14) goto L_0x04c5;
    L_0x04c1:
        if (r4 >= 0) goto L_0x04c8;
    L_0x04c3:
        if (r34 != 0) goto L_0x04c8;
    L_0x04c5:
        r6.setConversationTitle(r9);
    L_0x04c8:
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r14) goto L_0x04d3;
    L_0x04cc:
        if (r34 != 0) goto L_0x04d1;
    L_0x04ce:
        if (r4 >= 0) goto L_0x04d1;
    L_0x04d0:
        goto L_0x04d3;
    L_0x04d1:
        r0 = 0;
        goto L_0x04d4;
    L_0x04d3:
        r0 = 1;
    L_0x04d4:
        r6.setGroupConversation(r0);
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r9 = 1;
        r14 = new java.lang.String[r9];
        r43 = r13;
        r13 = new boolean[r9];
        if (r37 == 0) goto L_0x04ef;
    L_0x04e5:
        r16 = new org.json.JSONArray;
        r16.<init>();
        r47 = r15;
        r15 = r16;
        goto L_0x04f2;
    L_0x04ef:
        r47 = r15;
        r15 = 0;
    L_0x04f2:
        r16 = r8.size();
        r48 = r16 + -1;
        r9 = r48;
        r49 = 0;
        r50 = 0;
    L_0x04fe:
        r51 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r9 < 0) goto L_0x0849;
    L_0x0502:
        r48 = r8.get(r9);
        r53 = r8;
        r8 = r48;
        r8 = (org.telegram.messenger.MessageObject) r8;
        r48 = r9;
        r9 = r1.getShortStringForMessage(r8, r14, r13);
        r54 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1));
        if (r54 != 0) goto L_0x051d;
    L_0x0516:
        r19 = 0;
        r14[r19] = r7;
        r54 = r7;
        goto L_0x0537;
    L_0x051d:
        r19 = 0;
        r54 = r7;
        if (r4 >= 0) goto L_0x0537;
    L_0x0523:
        r7 = r8.messageOwner;
        r7 = r7.from_scheduled;
        if (r7 == 0) goto L_0x0537;
    L_0x0529:
        r7 = NUM; // 0x7f0e0740 float:1.8878802E38 double:1.0531630736E-314;
        r55 = r15;
        r15 = "NotificationMessageScheduledName";
        r7 = org.telegram.messenger.LocaleController.getString(r15, r7);
        r14[r19] = r7;
        goto L_0x0539;
    L_0x0537:
        r55 = r15;
    L_0x0539:
        if (r9 != 0) goto L_0x056e;
    L_0x053b:
        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r7 == 0) goto L_0x0563;
    L_0x053f:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r9 = "message text is null for ";
        r7.append(r9);
        r9 = r8.getId();
        r7.append(r9);
        r9 = " did = ";
        r7.append(r9);
        r8 = r8.getDialogId();
        r7.append(r8);
        r7 = r7.toString();
        org.telegram.messenger.FileLog.w(r7);
    L_0x0563:
        r56 = r2;
        r60 = r10;
        r58 = r11;
        r15 = r13;
        r3 = r55;
        goto L_0x0839;
    L_0x056e:
        r7 = r0.length();
        if (r7 <= 0) goto L_0x0579;
    L_0x0574:
        r7 = "\n\n";
        r0.append(r7);
    L_0x0579:
        r7 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1));
        if (r7 == 0) goto L_0x05a3;
    L_0x057d:
        r7 = r8.messageOwner;
        r7 = r7.from_scheduled;
        if (r7 == 0) goto L_0x05a3;
    L_0x0583:
        if (r4 <= 0) goto L_0x05a3;
    L_0x0585:
        r7 = 2;
        r15 = new java.lang.Object[r7];
        r7 = NUM; // 0x7f0e0740 float:1.8878802E38 double:1.0531630736E-314;
        r56 = r2;
        r2 = "NotificationMessageScheduledName";
        r2 = org.telegram.messenger.LocaleController.getString(r2, r7);
        r3 = 0;
        r15[r3] = r2;
        r2 = 1;
        r15[r2] = r9;
        r2 = "%1$s: %2$s";
        r9 = java.lang.String.format(r2, r15);
        r0.append(r9);
        goto L_0x05c1;
    L_0x05a3:
        r56 = r2;
        r3 = 0;
        r2 = r14[r3];
        if (r2 == 0) goto L_0x05be;
    L_0x05aa:
        r2 = 2;
        r7 = new java.lang.Object[r2];
        r2 = r14[r3];
        r7[r3] = r2;
        r2 = 1;
        r7[r2] = r9;
        r2 = "%1$s: %2$s";
        r2 = java.lang.String.format(r2, r7);
        r0.append(r2);
        goto L_0x05c1;
    L_0x05be:
        r0.append(r9);
    L_0x05c1:
        if (r4 <= 0) goto L_0x05c5;
    L_0x05c3:
        r2 = (long) r4;
        goto L_0x05d2;
    L_0x05c5:
        if (r34 == 0) goto L_0x05ca;
    L_0x05c7:
        r2 = -r4;
    L_0x05c8:
        r2 = (long) r2;
        goto L_0x05d2;
    L_0x05ca:
        if (r4 >= 0) goto L_0x05d1;
    L_0x05cc:
        r2 = r8.getFromId();
        goto L_0x05c8;
    L_0x05d1:
        r2 = r11;
    L_0x05d2:
        r7 = r10.get(r2);
        r7 = (androidx.core.app.Person) r7;
        r15 = 0;
        r58 = r14[r15];
        if (r58 != 0) goto L_0x061b;
    L_0x05dd:
        if (r36 == 0) goto L_0x0615;
    L_0x05df:
        if (r4 >= 0) goto L_0x0603;
    L_0x05e1:
        if (r34 == 0) goto L_0x05f5;
    L_0x05e3:
        r15 = android.os.Build.VERSION.SDK_INT;
        r58 = r11;
        r11 = 27;
        if (r15 <= r11) goto L_0x0619;
    L_0x05eb:
        r12 = NUM; // 0x7f0e0712 float:1.8878709E38 double:1.053163051E-314;
        r15 = "NotificationHiddenChatName";
        r12 = org.telegram.messenger.LocaleController.getString(r15, r12);
        goto L_0x0623;
    L_0x05f5:
        r58 = r11;
        r11 = 27;
        r12 = NUM; // 0x7f0e0713 float:1.887871E38 double:1.0531630514E-314;
        r15 = "NotificationHiddenChatUserName";
        r12 = org.telegram.messenger.LocaleController.getString(r15, r12);
        goto L_0x0623;
    L_0x0603:
        r58 = r11;
        r11 = 27;
        r12 = android.os.Build.VERSION.SDK_INT;
        if (r12 <= r11) goto L_0x0619;
    L_0x060b:
        r12 = NUM; // 0x7f0e0715 float:1.8878715E38 double:1.0531630524E-314;
        r15 = "NotificationHiddenName";
        r12 = org.telegram.messenger.LocaleController.getString(r15, r12);
        goto L_0x0623;
    L_0x0615:
        r58 = r11;
        r11 = 27;
    L_0x0619:
        r12 = r5;
        goto L_0x0623;
    L_0x061b:
        r58 = r11;
        r11 = 27;
        r12 = 0;
        r15 = r14[r12];
        r12 = r15;
    L_0x0623:
        if (r7 == 0) goto L_0x0633;
    L_0x0625:
        r15 = r7.getName();
        r15 = android.text.TextUtils.equals(r15, r12);
        if (r15 != 0) goto L_0x0630;
    L_0x062f:
        goto L_0x0633;
    L_0x0630:
        r15 = r13;
        goto L_0x06a0;
    L_0x0633:
        r7 = new androidx.core.app.Person$Builder;
        r7.<init>();
        r7.setName(r12);
        r12 = 0;
        r15 = r13[r12];
        if (r15 == 0) goto L_0x0698;
    L_0x0640:
        if (r4 == 0) goto L_0x0698;
    L_0x0642:
        r12 = android.os.Build.VERSION.SDK_INT;
        r15 = 28;
        if (r12 < r15) goto L_0x0698;
    L_0x0648:
        if (r4 > 0) goto L_0x0691;
    L_0x064a:
        if (r34 == 0) goto L_0x064d;
    L_0x064c:
        goto L_0x0691;
    L_0x064d:
        if (r4 >= 0) goto L_0x068e;
    L_0x064f:
        r12 = r8.getFromId();
        r15 = r62.getMessagesController();
        r11 = java.lang.Integer.valueOf(r12);
        r11 = r15.getUser(r11);
        if (r11 != 0) goto L_0x0673;
    L_0x0661:
        r11 = r62.getMessagesStorage();
        r11 = r11.getUserSync(r12);
        if (r11 == 0) goto L_0x0673;
    L_0x066b:
        r12 = r62.getMessagesController();
        r15 = 1;
        r12.putUser(r11, r15);
    L_0x0673:
        if (r11 == 0) goto L_0x068e;
    L_0x0675:
        r11 = r11.photo;
        if (r11 == 0) goto L_0x068e;
    L_0x0679:
        r11 = r11.photo_small;
        if (r11 == 0) goto L_0x068e;
    L_0x067d:
        r15 = r13;
        r12 = r11.volume_id;
        r60 = (r12 > r27 ? 1 : (r12 == r27 ? 0 : -1));
        if (r60 == 0) goto L_0x068f;
    L_0x0684:
        r12 = r11.local_id;
        if (r12 == 0) goto L_0x068f;
    L_0x0688:
        r12 = 1;
        r11 = org.telegram.messenger.FileLoader.getPathToAttach(r11, r12);
        goto L_0x0694;
    L_0x068e:
        r15 = r13;
    L_0x068f:
        r11 = 0;
        goto L_0x0694;
    L_0x0691:
        r15 = r13;
        r11 = r38;
    L_0x0694:
        r1.loadRoundAvatar(r11, r7);
        goto L_0x0699;
    L_0x0698:
        r15 = r13;
    L_0x0699:
        r7 = r7.build();
        r10.put(r2, r7);
    L_0x06a0:
        if (r4 == 0) goto L_0x07cf;
    L_0x06a2:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 28;
        if (r2 < r3) goto L_0x077f;
    L_0x06a8:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r11 = "activity";
        r2 = r2.getSystemService(r11);
        r2 = (android.app.ActivityManager) r2;
        r2 = r2.isLowRamDevice();
        if (r2 != 0) goto L_0x077f;
    L_0x06b8:
        if (r36 != 0) goto L_0x077f;
    L_0x06ba:
        r2 = r8.isSecretMedia();
        if (r2 != 0) goto L_0x077f;
    L_0x06c0:
        r2 = r8.type;
        r11 = 1;
        if (r2 == r11) goto L_0x06cb;
    L_0x06c5:
        r2 = r8.isSticker();
        if (r2 == 0) goto L_0x077f;
    L_0x06cb:
        r2 = r8.messageOwner;
        r2 = org.telegram.messenger.FileLoader.getPathToMessage(r2);
        r11 = new androidx.core.app.NotificationCompat$MessagingStyle$Message;
        r12 = r8.messageOwner;
        r12 = r12.date;
        r12 = (long) r12;
        r12 = r12 * r51;
        r11.<init>(r9, r12, r7);
        r12 = r8.isSticker();
        if (r12 == 0) goto L_0x06e6;
    L_0x06e3:
        r12 = "image/webp";
        goto L_0x06e8;
    L_0x06e6:
        r12 = "image/jpeg";
    L_0x06e8:
        r13 = r2.exists();
        if (r13 == 0) goto L_0x06f9;
    L_0x06ee:
        r13 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = "org.telegram.messenger.beta.provider";
        r2 = androidx.core.content.FileProvider.getUriForFile(r13, r3, r2);
        r60 = r10;
        goto L_0x074f;
    L_0x06f9:
        r3 = r62.getFileLoader();
        r13 = r2.getName();
        r3 = r3.isLoadingFile(r13);
        if (r3 == 0) goto L_0x074c;
    L_0x0707:
        r3 = new android.net.Uri$Builder;
        r3.<init>();
        r13 = "content";
        r3 = r3.scheme(r13);
        r13 = "org.telegram.messenger.beta.notification_image_provider";
        r3 = r3.authority(r13);
        r13 = "msg_media_raw";
        r3 = r3.appendPath(r13);
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r60 = r10;
        r10 = r1.currentAccount;
        r13.append(r10);
        r13.append(r5);
        r10 = r13.toString();
        r3 = r3.appendPath(r10);
        r10 = r2.getName();
        r3 = r3.appendPath(r10);
        r2 = r2.getAbsolutePath();
        r10 = "final_path";
        r2 = r3.appendQueryParameter(r10, r2);
        r2 = r2.build();
        goto L_0x074f;
    L_0x074c:
        r60 = r10;
        r2 = 0;
    L_0x074f:
        if (r2 == 0) goto L_0x0781;
    L_0x0751:
        r11.setData(r12, r2);
        r6.addMessage(r11);
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r10 = "com.android.systemui";
        r11 = 1;
        r3.grantUriPermission(r10, r2, r11);
        r3 = new org.telegram.messenger.-$$Lambda$NotificationsController$hROO1aIM4eduzMv5uJ3U4yL97Bo;
        r3.<init>(r2);
        r10 = 20000; // 0x4e20 float:2.8026E-41 double:9.8813E-320;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r10);
        r2 = r8.caption;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x077d;
    L_0x0771:
        r2 = r8.caption;
        r3 = r8.messageOwner;
        r3 = r3.date;
        r10 = (long) r3;
        r10 = r10 * r51;
        r6.addMessage(r2, r10, r7);
    L_0x077d:
        r2 = 1;
        goto L_0x0782;
    L_0x077f:
        r60 = r10;
    L_0x0781:
        r2 = 0;
    L_0x0782:
        if (r2 != 0) goto L_0x078e;
    L_0x0784:
        r2 = r8.messageOwner;
        r2 = r2.date;
        r2 = (long) r2;
        r2 = r2 * r51;
        r6.addMessage(r9, r2, r7);
    L_0x078e:
        if (r36 != 0) goto L_0x07db;
    L_0x0790:
        r2 = r8.isVoice();
        if (r2 == 0) goto L_0x07db;
    L_0x0796:
        r2 = r6.getMessages();
        r3 = r2.isEmpty();
        if (r3 != 0) goto L_0x07db;
    L_0x07a0:
        r3 = r8.messageOwner;
        r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3);
        r7 = android.os.Build.VERSION.SDK_INT;
        r10 = 24;
        if (r7 < r10) goto L_0x07b7;
    L_0x07ac:
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x07b5 }
        r10 = "org.telegram.messenger.beta.provider";
        r3 = androidx.core.content.FileProvider.getUriForFile(r7, r10, r3);	 Catch:{ Exception -> 0x07b5 }
        goto L_0x07bb;
    L_0x07b5:
        r3 = 0;
        goto L_0x07bb;
    L_0x07b7:
        r3 = android.net.Uri.fromFile(r3);
    L_0x07bb:
        if (r3 == 0) goto L_0x07db;
    L_0x07bd:
        r7 = r2.size();
        r10 = 1;
        r7 = r7 - r10;
        r2 = r2.get(r7);
        r2 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r2;
        r7 = "audio/ogg";
        r2.setData(r7, r3);
        goto L_0x07db;
    L_0x07cf:
        r60 = r10;
        r2 = r8.messageOwner;
        r2 = r2.date;
        r2 = (long) r2;
        r2 = r2 * r51;
        r6.addMessage(r9, r2, r7);
    L_0x07db:
        if (r55 == 0) goto L_0x0820;
    L_0x07dd:
        r2 = new org.json.JSONObject;	 Catch:{ JSONException -> 0x0820 }
        r2.<init>();	 Catch:{ JSONException -> 0x0820 }
        r3 = "text";
        r2.put(r3, r9);	 Catch:{ JSONException -> 0x0820 }
        r3 = "date";
        r7 = r8.messageOwner;	 Catch:{ JSONException -> 0x0820 }
        r7 = r7.date;	 Catch:{ JSONException -> 0x0820 }
        r2.put(r3, r7);	 Catch:{ JSONException -> 0x0820 }
        r3 = r8.isFromUser();	 Catch:{ JSONException -> 0x0820 }
        if (r3 == 0) goto L_0x0818;
    L_0x07f6:
        if (r4 >= 0) goto L_0x0818;
    L_0x07f8:
        r3 = r62.getMessagesController();	 Catch:{ JSONException -> 0x0820 }
        r7 = r8.getFromId();	 Catch:{ JSONException -> 0x0820 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ JSONException -> 0x0820 }
        r3 = r3.getUser(r7);	 Catch:{ JSONException -> 0x0820 }
        if (r3 == 0) goto L_0x0818;
    L_0x080a:
        r7 = "fname";
        r9 = r3.first_name;	 Catch:{ JSONException -> 0x0820 }
        r2.put(r7, r9);	 Catch:{ JSONException -> 0x0820 }
        r7 = "lname";
        r3 = r3.last_name;	 Catch:{ JSONException -> 0x0820 }
        r2.put(r7, r3);	 Catch:{ JSONException -> 0x0820 }
    L_0x0818:
        r3 = r55;
        r3.put(r2);	 Catch:{ JSONException -> 0x081e }
        goto L_0x0822;
        goto L_0x0822;
    L_0x0820:
        r3 = r55;
    L_0x0822:
        r9 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r2 = (r58 > r9 ? 1 : (r58 == r9 ? 0 : -1));
        if (r2 != 0) goto L_0x0839;
    L_0x0829:
        r2 = r8.messageOwner;
        r2 = r2.reply_markup;
        if (r2 == 0) goto L_0x0839;
    L_0x082f:
        r2 = r2.rows;
        r7 = r8.getId();
        r49 = r2;
        r50 = r7;
    L_0x0839:
        r9 = r48 + -1;
        r13 = r15;
        r8 = r53;
        r7 = r54;
        r11 = r58;
        r10 = r60;
        r15 = r3;
        r2 = r56;
        goto L_0x04fe;
    L_0x0849:
        r54 = r7;
        r53 = r8;
        r58 = r11;
        r3 = r15;
        r2 = new android.content.Intent;
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r8 = org.telegram.ui.LaunchActivity.class;
        r2.<init>(r7, r8);
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "com.tmessages.openchat";
        r7.append(r8);
        r8 = java.lang.Math.random();
        r7.append(r8);
        r8 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r7.append(r8);
        r7 = r7.toString();
        r2.setAction(r7);
        r7 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r2.setFlags(r7);
        r7 = "android.intent.category.LAUNCHER";
        r2.addCategory(r7);
        if (r4 == 0) goto L_0x0896;
    L_0x0884:
        if (r4 <= 0) goto L_0x088d;
    L_0x0886:
        r7 = "userId";
        r2.putExtra(r7, r4);
        goto L_0x0893;
    L_0x088d:
        r7 = -r4;
        r8 = "chatId";
        r2.putExtra(r8, r7);
    L_0x0893:
        r8 = r35;
        goto L_0x089d;
    L_0x0896:
        r7 = "encId";
        r8 = r35;
        r2.putExtra(r7, r8);
    L_0x089d:
        r7 = r1.currentAccount;
        r9 = r47;
        r2.putExtra(r9, r7);
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r10 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r11 = 0;
        r2 = android.app.PendingIntent.getActivity(r7, r11, r2, r10);
        r7 = new androidx.core.app.NotificationCompat$WearableExtender;
        r7.<init>();
        r10 = r43;
        if (r43 == 0) goto L_0x08b9;
    L_0x08b6:
        r7.addAction(r10);
    L_0x08b9:
        r11 = new android.content.Intent;
        r12 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r13 = org.telegram.messenger.AutoMessageHeardReceiver.class;
        r11.<init>(r12, r13);
        r12 = 32;
        r11.addFlags(r12);
        r12 = "org.telegram.messenger.ACTION_MESSAGE_HEARD";
        r11.setAction(r12);
        r12 = "dialog_id";
        r13 = r58;
        r11.putExtra(r12, r13);
        r12 = r44;
        r15 = r45;
        r11.putExtra(r15, r12);
        r55 = r3;
        r3 = r1.currentAccount;
        r11.putExtra(r9, r3);
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r15 = r42.intValue();
        r43 = r10;
        r10 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r3 = android.app.PendingIntent.getBroadcast(r3, r15, r11, r10);
        r10 = new androidx.core.app.NotificationCompat$Action$Builder;
        r11 = NUM; // 0x7var_ac float:1.7945446E38 double:1.0529357145E-314;
        r15 = NUM; // 0x7f0e061a float:1.8878206E38 double:1.0531629284E-314;
        r47 = r9;
        r9 = "MarkAsRead";
        r9 = org.telegram.messenger.LocaleController.getString(r9, r15);
        r10.<init>(r11, r9, r3);
        r3 = 2;
        r10.setSemanticAction(r3);
        r3 = 0;
        r10.setShowsUserInterface(r3);
        r3 = r10.build();
        r9 = "_";
        if (r4 == 0) goto L_0x0945;
    L_0x0912:
        if (r4 <= 0) goto L_0x092c;
    L_0x0914:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r10 = "tguser";
        r8.append(r10);
        r8.append(r4);
        r8.append(r9);
        r8.append(r12);
        r8 = r8.toString();
        goto L_0x0964;
    L_0x092c:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r10 = "tgchat";
        r8.append(r10);
        r10 = -r4;
        r8.append(r10);
        r8.append(r9);
        r8.append(r12);
        r8 = r8.toString();
        goto L_0x0964;
    L_0x0945:
        r10 = globalSecretChatId;
        r15 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1));
        if (r15 == 0) goto L_0x0963;
    L_0x094b:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "tgenc";
        r10.append(r11);
        r10.append(r8);
        r10.append(r9);
        r10.append(r12);
        r8 = r10.toString();
        goto L_0x0964;
    L_0x0963:
        r8 = 0;
    L_0x0964:
        if (r8 == 0) goto L_0x0988;
    L_0x0966:
        r7.setDismissalId(r8);
        r10 = new androidx.core.app.NotificationCompat$WearableExtender;
        r10.<init>();
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r15 = "summary_";
        r11.append(r15);
        r11.append(r8);
        r8 = r11.toString();
        r10.setDismissalId(r8);
        r8 = r63;
        r8.extend(r10);
        goto L_0x098a;
    L_0x0988:
        r8 = r63;
    L_0x098a:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "tgaccount";
        r10.append(r11);
        r11 = r46;
        r10.append(r11);
        r10 = r10.toString();
        r7.setBridgeTag(r10);
        r10 = r53;
        r15 = 0;
        r27 = r10.get(r15);
        r15 = r27;
        r15 = (org.telegram.messenger.MessageObject) r15;
        r15 = r15.messageOwner;
        r15 = r15.date;
        r27 = r9;
        r8 = (long) r15;
        r8 = r8 * r51;
        r15 = new androidx.core.app.NotificationCompat$Builder;
        r11 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r15.<init>(r11);
        r11 = r54;
        r15.setContentTitle(r11);
        r44 = r12;
        r12 = NUM; // 0x7var_c float:1.7945673E38 double:1.05293577E-314;
        r15.setSmallIcon(r12);
        r0 = r0.toString();
        r15.setContentText(r0);
        r12 = 1;
        r15.setAutoCancel(r12);
        r0 = r10.size();
        r15.setNumber(r0);
        r0 = -15618822; // 0xfffffffffvar_acfa float:-1.936362E38 double:NaN;
        r15.setColor(r0);
        r10 = 0;
        r15.setGroupSummary(r10);
        r15.setWhen(r8);
        r15.setShowWhen(r12);
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r10 = "sdid_";
        r0.append(r10);
        r0.append(r13);
        r0 = r0.toString();
        r15.setShortcutId(r0);
        r15.setStyle(r6);
        r15.setContentIntent(r2);
        r15.extend(r7);
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r5);
        r5 = NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        r5 = r5 - r8;
        r0.append(r5);
        r0 = r0.toString();
        r15.setSortKey(r0);
        r0 = "msg";
        r15.setCategory(r0);
        r0 = new android.content.Intent;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = org.telegram.messenger.NotificationDismissReceiver.class;
        r0.<init>(r2, r5);
        r2 = "messageDate";
        r5 = r32;
        r0.putExtra(r2, r5);
        r2 = "dialogId";
        r0.putExtra(r2, r13);
        r2 = r1.currentAccount;
        r6 = r47;
        r0.putExtra(r6, r2);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r7 = r42.intValue();
        r8 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r0 = android.app.PendingIntent.getBroadcast(r2, r7, r0, r8);
        r15.setDeleteIntent(r0);
        if (r17 == 0) goto L_0x0a5a;
    L_0x0a51:
        r0 = r1.notificationGroup;
        r15.setGroup(r0);
        r2 = 1;
        r15.setGroupAlertBehavior(r2);
    L_0x0a5a:
        if (r43 == 0) goto L_0x0a61;
    L_0x0a5c:
        r2 = r43;
        r15.addAction(r2);
    L_0x0a61:
        if (r36 != 0) goto L_0x0a66;
    L_0x0a63:
        r15.addAction(r3);
    L_0x0a66:
        r0 = r22.size();
        r2 = 1;
        if (r0 != r2) goto L_0x0a79;
    L_0x0a6d:
        r0 = android.text.TextUtils.isEmpty(r65);
        if (r0 != 0) goto L_0x0a79;
    L_0x0a73:
        r3 = r65;
        r15.setSubText(r3);
        goto L_0x0a7b;
    L_0x0a79:
        r3 = r65;
    L_0x0a7b:
        if (r4 != 0) goto L_0x0a80;
    L_0x0a7d:
        r15.setLocalOnly(r2);
    L_0x0a80:
        if (r41 == 0) goto L_0x0a87;
    L_0x0a82:
        r7 = r41;
        r15.setLargeIcon(r7);
    L_0x0a87:
        r7 = 0;
        r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r7);
        if (r0 != 0) goto L_0x0b23;
    L_0x0a8e:
        r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r0 != 0) goto L_0x0b23;
    L_0x0a92:
        r0 = r49;
        if (r0 == 0) goto L_0x0b23;
    L_0x0a96:
        r7 = r0.size();
        r8 = 0;
    L_0x0a9b:
        if (r8 >= r7) goto L_0x0b23;
    L_0x0a9d:
        r9 = r0.get(r8);
        r9 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r9;
        r10 = r9.buttons;
        r10 = r10.size();
        r12 = 0;
    L_0x0aaa:
        if (r12 >= r10) goto L_0x0b0f;
    L_0x0aac:
        r2 = r9.buttons;
        r2 = r2.get(r12);
        r2 = (org.telegram.tgnet.TLRPC.KeyboardButton) r2;
        r28 = r0;
        r0 = r2 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
        if (r0 == 0) goto L_0x0af8;
    L_0x0aba:
        r0 = new android.content.Intent;
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r32 = r7;
        r7 = org.telegram.messenger.NotificationCallbackReceiver.class;
        r0.<init>(r3, r7);
        r3 = r1.currentAccount;
        r0.putExtra(r6, r3);
        r3 = "did";
        r0.putExtra(r3, r13);
        r3 = r2.data;
        if (r3 == 0) goto L_0x0ad8;
    L_0x0ad3:
        r7 = "data";
        r0.putExtra(r7, r3);
    L_0x0ad8:
        r3 = "mid";
        r7 = r50;
        r0.putExtra(r3, r7);
        r2 = r2.text;
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r47 = r6;
        r6 = r1.lastButtonId;
        r35 = r7;
        r7 = r6 + 1;
        r1.lastButtonId = r7;
        r7 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r0 = android.app.PendingIntent.getBroadcast(r3, r6, r0, r7);
        r3 = 0;
        r15.addAction(r3, r2, r0);
        goto L_0x0b01;
    L_0x0af8:
        r47 = r6;
        r32 = r7;
        r35 = r50;
        r3 = 0;
        r7 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
    L_0x0b01:
        r12 = r12 + 1;
        r3 = r65;
        r0 = r28;
        r7 = r32;
        r50 = r35;
        r6 = r47;
        r2 = 1;
        goto L_0x0aaa;
    L_0x0b0f:
        r28 = r0;
        r47 = r6;
        r32 = r7;
        r35 = r50;
        r3 = 0;
        r7 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r8 = r8 + 1;
        r3 = r65;
        r7 = r32;
        r2 = 1;
        goto L_0x0a9b;
    L_0x0b23:
        r3 = 0;
        if (r29 != 0) goto L_0x0b4a;
    L_0x0b26:
        if (r24 == 0) goto L_0x0b4a;
    L_0x0b28:
        r6 = r24;
        r0 = r6.phone;
        if (r0 == 0) goto L_0x0b4a;
    L_0x0b2e:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x0b4a;
    L_0x0b34:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "tel:+";
        r0.append(r2);
        r2 = r6.phone;
        r0.append(r2);
        r0 = r0.toString();
        r15.addPerson(r0);
    L_0x0b4a:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 26;
        if (r0 < r2) goto L_0x0b5f;
    L_0x0b50:
        if (r17 == 0) goto L_0x0b58;
    L_0x0b52:
        r0 = OTHER_NOTIFICATIONS_CHANNEL;
        r15.setChannelId(r0);
        goto L_0x0b5f;
    L_0x0b58:
        r0 = r21.getChannelId();
        r15.setChannelId(r0);
    L_0x0b5f:
        r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder;
        r6 = r42.intValue();
        r7 = r15.build();
        r0.<init>(r6, r7);
        r6 = r31;
        r6.add(r0);
        r0 = r1.wearNotificationsIds;
        r7 = r42;
        r0.put(r13, r7);
        if (r4 == 0) goto L_0x0bf4;
    L_0x0b7a:
        if (r37 == 0) goto L_0x0bf4;
    L_0x0b7c:
        r0 = "reply";
        r8 = r37;
        r7 = r40;
        r8.put(r0, r7);	 Catch:{ JSONException -> 0x0bf4 }
        r0 = "name";
        r8.put(r0, r11);	 Catch:{ JSONException -> 0x0bf4 }
        r7 = r44;
        r9 = r45;
        r8.put(r9, r7);	 Catch:{ JSONException -> 0x0bf4 }
        r0 = "max_date";
        r8.put(r0, r5);	 Catch:{ JSONException -> 0x0bf4 }
        r0 = "id";
        r5 = java.lang.Math.abs(r4);	 Catch:{ JSONException -> 0x0bf4 }
        r8.put(r0, r5);	 Catch:{ JSONException -> 0x0bf4 }
        if (r39 == 0) goto L_0x0bc8;
    L_0x0ba1:
        r0 = "photo";
        r5 = new java.lang.StringBuilder;	 Catch:{ JSONException -> 0x0bf4 }
        r5.<init>();	 Catch:{ JSONException -> 0x0bf4 }
        r13 = r39;
        r7 = r13.dc_id;	 Catch:{ JSONException -> 0x0bf4 }
        r5.append(r7);	 Catch:{ JSONException -> 0x0bf4 }
        r7 = r27;
        r5.append(r7);	 Catch:{ JSONException -> 0x0bf4 }
        r9 = r13.volume_id;	 Catch:{ JSONException -> 0x0bf4 }
        r5.append(r9);	 Catch:{ JSONException -> 0x0bf4 }
        r5.append(r7);	 Catch:{ JSONException -> 0x0bf4 }
        r9 = r13.secret;	 Catch:{ JSONException -> 0x0bf4 }
        r5.append(r9);	 Catch:{ JSONException -> 0x0bf4 }
        r5 = r5.toString();	 Catch:{ JSONException -> 0x0bf4 }
        r8.put(r0, r5);	 Catch:{ JSONException -> 0x0bf4 }
    L_0x0bc8:
        if (r55 == 0) goto L_0x0bd1;
    L_0x0bca:
        r0 = "msgs";
        r5 = r55;
        r8.put(r0, r5);	 Catch:{ JSONException -> 0x0bf4 }
    L_0x0bd1:
        r0 = "type";
        if (r4 <= 0) goto L_0x0bdc;
    L_0x0bd5:
        r4 = "user";
        r8.put(r0, r4);	 Catch:{ JSONException -> 0x0bf4 }
        goto L_0x0bee;
    L_0x0bdc:
        if (r4 >= 0) goto L_0x0bee;
    L_0x0bde:
        if (r34 != 0) goto L_0x0be9;
    L_0x0be0:
        if (r33 == 0) goto L_0x0be3;
    L_0x0be2:
        goto L_0x0be9;
    L_0x0be3:
        r4 = "group";
        r8.put(r0, r4);	 Catch:{ JSONException -> 0x0bf4 }
        goto L_0x0bee;
    L_0x0be9:
        r4 = "channel";
        r8.put(r0, r4);	 Catch:{ JSONException -> 0x0bf4 }
    L_0x0bee:
        r4 = r30;
        r4.put(r8);	 Catch:{ JSONException -> 0x0bf6 }
        goto L_0x0bf6;
    L_0x0bf4:
        r4 = r30;
    L_0x0bf6:
        r10 = r26 + 1;
        r9 = r4;
        r7 = r6;
        r12 = r17;
        r4 = r20;
        r2 = r21;
        r3 = r22;
        r13 = r23;
        r6 = r25;
        r15 = r36;
        r14 = r46;
        r5 = 0;
        r11 = 1;
        goto L_0x00d5;
    L_0x0c0e:
        r21 = r2;
        r25 = r6;
        r6 = r7;
        r4 = r9;
        r17 = r12;
        r46 = r14;
        r3 = 0;
        if (r17 == 0) goto L_0x0c3f;
    L_0x0c1b:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0CLASSNAME;
    L_0x0c1f:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "show summary with id ";
        r0.append(r2);
        r2 = r1.notificationId;
        r0.append(r2);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0CLASSNAME:
        r0 = notificationManager;
        r2 = r1.notificationId;
        r5 = r21;
        r0.notify(r2, r5);
        goto L_0x0CLASSNAME;
    L_0x0c3f:
        r0 = notificationManager;
        r2 = r1.notificationId;
        r0.cancel(r2);
    L_0x0CLASSNAME:
        r0 = r6.size();
        r2 = 0;
    L_0x0c4b:
        if (r2 >= r0) goto L_0x0CLASSNAME;
    L_0x0c4d:
        r5 = r6.get(r2);
        r5 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r5;
        r5.call();
        r2 = r2 + 1;
        goto L_0x0c4b;
    L_0x0CLASSNAME:
        r0 = r25.size();
        if (r3 >= r0) goto L_0x0c8d;
    L_0x0c5f:
        r2 = r25;
        r0 = r2.valueAt(r3);
        r0 = (java.lang.Integer) r0;
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0c7f;
    L_0x0c6b:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "cancel notification id ";
        r5.append(r6);
        r5.append(r0);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.w(r5);
    L_0x0c7f:
        r5 = notificationManager;
        r0 = r0.intValue();
        r5.cancel(r0);
        r3 = r3 + 1;
        r25 = r2;
        goto L_0x0CLASSNAME;
    L_0x0c8d:
        if (r4 == 0) goto L_0x0caf;
    L_0x0c8f:
        r0 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0caf }
        r0.<init>();	 Catch:{ Exception -> 0x0caf }
        r2 = "id";
        r3 = r46;
        r0.put(r2, r3);	 Catch:{ Exception -> 0x0caf }
        r2 = "n";
        r0.put(r2, r4);	 Catch:{ Exception -> 0x0caf }
        r2 = "/notify";
        r0 = r0.toString();	 Catch:{ Exception -> 0x0caf }
        r0 = r0.getBytes();	 Catch:{ Exception -> 0x0caf }
        r3 = "remote_notifications";
        org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r2, r0, r3);	 Catch:{ Exception -> 0x0caf }
    L_0x0caf:
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
