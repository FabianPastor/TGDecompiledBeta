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

public class NotificationsController {
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
    private int currentAccount;
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
    private int notificationId;
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
        this.currentAccount = i;
        this.notificationId = this.currentAccount + 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("messages");
        int i2 = this.currentAccount;
        stringBuilder.append(i2 == 0 ? "" : Integer.valueOf(i2));
        this.notificationGroup = stringBuilder.toString();
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
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
            this.notificationDelayWakelock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "lock");
            this.notificationDelayWakelock.setReferenceCounted(false);
        } catch (Exception e22) {
            FileLog.e(e22);
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
        Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
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
        getInstance(this.currentAccount).processReadMessages(null, j, 0, Integer.MAX_VALUE, false);
        LongSparseArray longSparseArray = new LongSparseArray();
        longSparseArray.put(j, Integer.valueOf(0));
        getInstance(this.currentAccount).processDialogsUpdateRead(longSparseArray);
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
        MessagesController.getNotificationsSettings(this.currentAccount);
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
                if (this.lastOnlineFromOtherDevice <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void removeDeletedHisoryFromNotifications(SparseIntArray sparseIntArray) {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$4ZPSiSXCXkKfxVPcPpmsFy8foEU(this, sparseIntArray, new ArrayList(0)));
    }

    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$11$NotificationsController(SparseIntArray sparseIntArray, ArrayList arrayList) {
        boolean z;
        SparseIntArray sparseIntArray2 = sparseIntArray;
        ArrayList arrayList2 = arrayList;
        int i = this.total_unread_count;
        MessagesController.getNotificationsSettings(this.currentAccount);
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
                if (this.lastOnlineFromOtherDevice <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
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
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00ac  */
    public /* synthetic */ void lambda$processNewMessages$16$NotificationsController(java.util.ArrayList r30, java.util.ArrayList r31, boolean r32, boolean r33, java.util.concurrent.CountDownLatch r34) {
        /*
        r29 = this;
        r8 = r29;
        r9 = r30;
        r10 = new android.util.LongSparseArray;
        r10.<init>();
        r0 = r8.currentAccount;
        r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0);
        r12 = 1;
        r0 = "PinnedMessages";
        r13 = r11.getBoolean(r0, r12);
        r0 = 0;
        r15 = 0;
        r16 = 0;
        r17 = 0;
    L_0x001c:
        r1 = r30.size();
        if (r15 >= r1) goto L_0x01d4;
    L_0x0022:
        r1 = r9.get(r15);
        r7 = r1;
        r7 = (org.telegram.messenger.MessageObject) r7;
        r1 = r7.messageOwner;
        if (r1 == 0) goto L_0x0043;
    L_0x002d:
        r4 = r1.silent;
        if (r4 == 0) goto L_0x0043;
    L_0x0031:
        r1 = r1.action;
        r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r4 != 0) goto L_0x003b;
    L_0x0037:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r1 == 0) goto L_0x0043;
    L_0x003b:
        r25 = r0;
        r21 = r13;
        r20 = r15;
        goto L_0x0118;
    L_0x0043:
        r1 = r7.getId();
        r4 = (long) r1;
        r1 = r7.isFcmMessage();
        r18 = 0;
        if (r1 == 0) goto L_0x0059;
    L_0x0050:
        r1 = r7.messageOwner;
        r20 = r15;
        r14 = r1.random_id;
        r21 = r13;
        goto L_0x005f;
    L_0x0059:
        r20 = r15;
        r21 = r13;
        r14 = r18;
    L_0x005f:
        r12 = r7.getDialogId();
        r6 = (int) r12;
        r1 = r7.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x0075;
    L_0x006c:
        r2 = (long) r1;
        r1 = 32;
        r1 = r2 << r1;
        r4 = r4 | r1;
        r24 = 1;
        goto L_0x0077;
    L_0x0075:
        r24 = 0;
    L_0x0077:
        r1 = r8.pushMessagesDict;
        r1 = r1.get(r4);
        r1 = (org.telegram.messenger.MessageObject) r1;
        if (r1 != 0) goto L_0x00a8;
    L_0x0081:
        r2 = r7.messageOwner;
        r2 = r2.random_id;
        r25 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1));
        if (r25 == 0) goto L_0x00a8;
    L_0x0089:
        r1 = r8.fcmRandomMessagesDict;
        r1 = r1.get(r2);
        r1 = (org.telegram.messenger.MessageObject) r1;
        if (r1 == 0) goto L_0x00a1;
    L_0x0093:
        r2 = r8.fcmRandomMessagesDict;
        r3 = r7.messageOwner;
        r25 = r0;
        r26 = r1;
        r0 = r3.random_id;
        r2.remove(r0);
        goto L_0x00a5;
    L_0x00a1:
        r25 = r0;
        r26 = r1;
    L_0x00a5:
        r1 = r26;
        goto L_0x00aa;
    L_0x00a8:
        r25 = r0;
    L_0x00aa:
        if (r1 == 0) goto L_0x00ec;
    L_0x00ac:
        r0 = r1.isFcmMessage();
        if (r0 == 0) goto L_0x0118;
    L_0x00b2:
        r0 = r8.pushMessagesDict;
        r0.put(r4, r7);
        r0 = r8.pushMessages;
        r0 = r0.indexOf(r1);
        if (r0 < 0) goto L_0x00d6;
    L_0x00bf:
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
        goto L_0x00d7;
    L_0x00d6:
        r12 = r7;
    L_0x00d7:
        if (r32 == 0) goto L_0x00e7;
    L_0x00d9:
        r0 = r12.localEdit;
        if (r0 == 0) goto L_0x00e9;
    L_0x00dd:
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r1.putPushMessage(r12);
        goto L_0x00e9;
    L_0x00e7:
        r0 = r17;
    L_0x00e9:
        r17 = r0;
        goto L_0x0118;
    L_0x00ec:
        if (r17 == 0) goto L_0x00ef;
    L_0x00ee:
        goto L_0x0118;
    L_0x00ef:
        if (r32 == 0) goto L_0x00fa;
    L_0x00f1:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0.putPushMessage(r7);
    L_0x00fa:
        r0 = r8.opened_dialog_id;
        r2 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1));
        if (r2 != 0) goto L_0x010a;
    L_0x0100:
        r0 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r0 == 0) goto L_0x010a;
    L_0x0104:
        if (r32 != 0) goto L_0x0118;
    L_0x0106:
        r29.playInChatSound();
        goto L_0x0118;
    L_0x010a:
        r0 = r7.messageOwner;
        r1 = r0.mentioned;
        if (r1 == 0) goto L_0x0125;
    L_0x0110:
        if (r21 != 0) goto L_0x011e;
    L_0x0112:
        r0 = r0.action;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r0 == 0) goto L_0x011e;
    L_0x0118:
        r26 = r10;
        r0 = r25;
        goto L_0x01c9;
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
        if (r0 == 0) goto L_0x01c3;
    L_0x015e:
        if (r32 != 0) goto L_0x017a;
    L_0x0160:
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
        goto L_0x0182;
    L_0x017a:
        r22 = r2;
        r26 = r10;
        r27 = r12;
        r9 = r4;
        r12 = r7;
    L_0x0182:
        r0 = r8.delayedPushMessages;
        r0.add(r12);
        r0 = r8.pushMessages;
        r1 = 0;
        r0.add(r1, r12);
        r0 = (r9 > r18 ? 1 : (r9 == r18 ? 0 : -1));
        if (r0 == 0) goto L_0x0197;
    L_0x0191:
        r0 = r8.pushMessagesDict;
        r0.put(r9, r12);
        goto L_0x01a0;
    L_0x0197:
        r0 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1));
        if (r0 == 0) goto L_0x01a0;
    L_0x019b:
        r0 = r8.fcmRandomMessagesDict;
        r0.put(r14, r12);
    L_0x01a0:
        r0 = (r27 > r22 ? 1 : (r27 == r22 ? 0 : -1));
        if (r0 == 0) goto L_0x01c5;
    L_0x01a4:
        r0 = r8.pushDialogsOverrideMention;
        r1 = r27;
        r0 = r0.get(r1);
        r0 = (java.lang.Integer) r0;
        r3 = r8.pushDialogsOverrideMention;
        if (r0 != 0) goto L_0x01b4;
    L_0x01b2:
        r12 = 1;
        goto L_0x01bb;
    L_0x01b4:
        r0 = r0.intValue();
        r4 = 1;
        r12 = r0 + 1;
    L_0x01bb:
        r0 = java.lang.Integer.valueOf(r12);
        r3.put(r1, r0);
        goto L_0x01c5;
    L_0x01c3:
        r26 = r10;
    L_0x01c5:
        r0 = r25;
        r16 = 1;
    L_0x01c9:
        r15 = r20 + 1;
        r9 = r30;
        r13 = r21;
        r10 = r26;
        r12 = 1;
        goto L_0x001c;
    L_0x01d4:
        r25 = r0;
        if (r16 == 0) goto L_0x01dc;
    L_0x01d8:
        r0 = r33;
        r8.notifyCheck = r0;
    L_0x01dc:
        r0 = r31.isEmpty();
        if (r0 != 0) goto L_0x01f5;
    L_0x01e2:
        r0 = 0;
        r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r0);
        if (r1 != 0) goto L_0x01f5;
    L_0x01e9:
        r0 = new org.telegram.messenger.-$$Lambda$NotificationsController$vBhFCZdXUS15Ipx-fzqzTMIuA3o;
        r1 = r31;
        r14 = r25;
        r0.<init>(r8, r1, r14);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x01f5:
        if (r32 == 0) goto L_0x029e;
    L_0x01f7:
        if (r17 == 0) goto L_0x0205;
    L_0x01f9:
        r0 = r8.delayedPushMessages;
        r0.clear();
        r0 = r8.notifyCheck;
        r8.showOrUpdateNotification(r0);
        goto L_0x029e;
    L_0x0205:
        if (r16 == 0) goto L_0x029e;
    L_0x0207:
        r0 = r30;
        r1 = 0;
        r0 = r0.get(r1);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.getDialogId();
        r2 = r8.total_unread_count;
        r3 = r8.getNotifyOverride(r11, r0);
        r4 = -1;
        if (r3 != r4) goto L_0x0223;
    L_0x021d:
        r3 = r8.isGlobalNotificationsEnabled(r0);
    L_0x0221:
        r12 = r3;
        goto L_0x022a;
    L_0x0223:
        r4 = 2;
        if (r3 == r4) goto L_0x0228;
    L_0x0226:
        r3 = 1;
        goto L_0x0221;
    L_0x0228:
        r3 = 0;
        goto L_0x0221;
    L_0x022a:
        r3 = r8.pushDialogs;
        r3 = r3.get(r0);
        r3 = (java.lang.Integer) r3;
        if (r3 == 0) goto L_0x023b;
    L_0x0234:
        r4 = r3.intValue();
        r5 = 1;
        r4 = r4 + r5;
        goto L_0x023d;
    L_0x023b:
        r5 = 1;
        r4 = 1;
    L_0x023d:
        r4 = java.lang.Integer.valueOf(r4);
        r6 = r8.notifyCheck;
        if (r6 == 0) goto L_0x0259;
    L_0x0245:
        if (r12 != 0) goto L_0x0259;
    L_0x0247:
        r6 = r8.pushDialogsOverrideMention;
        r6 = r6.get(r0);
        r6 = (java.lang.Integer) r6;
        if (r6 == 0) goto L_0x0259;
    L_0x0251:
        r7 = r6.intValue();
        if (r7 == 0) goto L_0x0259;
    L_0x0257:
        r4 = r6;
        r12 = 1;
    L_0x0259:
        if (r12 == 0) goto L_0x0274;
    L_0x025b:
        if (r3 == 0) goto L_0x0266;
    L_0x025d:
        r5 = r8.total_unread_count;
        r3 = r3.intValue();
        r5 = r5 - r3;
        r8.total_unread_count = r5;
    L_0x0266:
        r3 = r8.total_unread_count;
        r5 = r4.intValue();
        r3 = r3 + r5;
        r8.total_unread_count = r3;
        r3 = r8.pushDialogs;
        r3.put(r0, r4);
    L_0x0274:
        r0 = r8.total_unread_count;
        if (r2 == r0) goto L_0x0290;
    L_0x0278:
        r0 = r8.delayedPushMessages;
        r0.clear();
        r0 = r8.notifyCheck;
        r8.showOrUpdateNotification(r0);
        r0 = r8.pushDialogs;
        r0 = r0.size();
        r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$R3R5Z37efc0XPsswynnBTmucwac;
        r1.<init>(r8, r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x0290:
        r0 = 0;
        r8.notifyCheck = r0;
        r0 = r8.showBadgeNumber;
        if (r0 == 0) goto L_0x029e;
    L_0x0297:
        r0 = r29.getTotalAllUnreadCount();
        r8.setBadge(r0);
    L_0x029e:
        if (r34 == 0) goto L_0x02a3;
    L_0x02a0:
        r34.countDown();
    L_0x02a3:
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(LongSparseArray<Integer> longSparseArray) {
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$bRv8AkmkiAwGyZ1dPg2TuCyHYS0(this, longSparseArray, new ArrayList()));
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x005a  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x008f  */
    public /* synthetic */ void lambda$processDialogsUpdateRead$19$NotificationsController(android.util.LongSparseArray r18, java.util.ArrayList r19) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r18;
        r2 = r19;
        r3 = r0.total_unread_count;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);
        r5 = 0;
        r6 = 0;
    L_0x0010:
        r7 = r18.size();
        r8 = 1;
        if (r6 >= r7) goto L_0x00fa;
    L_0x0017:
        r9 = r1.keyAt(r6);
        r7 = r0.getNotifyOverride(r4, r9);
        r11 = -1;
        if (r7 != r11) goto L_0x0027;
    L_0x0022:
        r7 = r0.isGlobalNotificationsEnabled(r9);
        goto L_0x002d;
    L_0x0027:
        r11 = 2;
        if (r7 == r11) goto L_0x002c;
    L_0x002a:
        r7 = 1;
        goto L_0x002d;
    L_0x002c:
        r7 = 0;
    L_0x002d:
        r11 = r0.pushDialogs;
        r11 = r11.get(r9);
        r11 = (java.lang.Integer) r11;
        r12 = r1.get(r9);
        r12 = (java.lang.Integer) r12;
        r13 = r0.notifyCheck;
        if (r13 == 0) goto L_0x0053;
    L_0x003f:
        if (r7 != 0) goto L_0x0053;
    L_0x0041:
        r13 = r0.pushDialogsOverrideMention;
        r13 = r13.get(r9);
        r13 = (java.lang.Integer) r13;
        if (r13 == 0) goto L_0x0053;
    L_0x004b:
        r14 = r13.intValue();
        if (r14 == 0) goto L_0x0053;
    L_0x0051:
        r7 = 1;
        goto L_0x0054;
    L_0x0053:
        r13 = r12;
    L_0x0054:
        r12 = r13.intValue();
        if (r12 != 0) goto L_0x005f;
    L_0x005a:
        r12 = r0.smartNotificationsDialogs;
        r12.remove(r9);
    L_0x005f:
        r12 = r13.intValue();
        if (r12 >= 0) goto L_0x0076;
    L_0x0065:
        if (r11 != 0) goto L_0x0069;
    L_0x0067:
        goto L_0x00f6;
    L_0x0069:
        r12 = r11.intValue();
        r13 = r13.intValue();
        r12 = r12 + r13;
        r13 = java.lang.Integer.valueOf(r12);
    L_0x0076:
        if (r7 != 0) goto L_0x007e;
    L_0x0078:
        r12 = r13.intValue();
        if (r12 != 0) goto L_0x0089;
    L_0x007e:
        if (r11 == 0) goto L_0x0089;
    L_0x0080:
        r12 = r0.total_unread_count;
        r11 = r11.intValue();
        r12 = r12 - r11;
        r0.total_unread_count = r12;
    L_0x0089:
        r11 = r13.intValue();
        if (r11 != 0) goto L_0x00e6;
    L_0x008f:
        r7 = r0.pushDialogs;
        r7.remove(r9);
        r7 = r0.pushDialogsOverrideMention;
        r7.remove(r9);
        r7 = 0;
    L_0x009a:
        r11 = r0.pushMessages;
        r11 = r11.size();
        if (r7 >= r11) goto L_0x00f6;
    L_0x00a2:
        r11 = r0.pushMessages;
        r11 = r11.get(r7);
        r11 = (org.telegram.messenger.MessageObject) r11;
        r12 = r11.getDialogId();
        r14 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1));
        if (r14 != 0) goto L_0x00e4;
    L_0x00b2:
        r12 = r0.isPersonalMessage(r11);
        if (r12 == 0) goto L_0x00bd;
    L_0x00b8:
        r12 = r0.personal_count;
        r12 = r12 - r8;
        r0.personal_count = r12;
    L_0x00bd:
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
        if (r14 == 0) goto L_0x00dc;
    L_0x00d6:
        r14 = (long) r14;
        r16 = 32;
        r14 = r14 << r16;
        r12 = r12 | r14;
    L_0x00dc:
        r14 = r0.pushMessagesDict;
        r14.remove(r12);
        r2.add(r11);
    L_0x00e4:
        r7 = r7 + r8;
        goto L_0x009a;
    L_0x00e6:
        if (r7 == 0) goto L_0x00f6;
    L_0x00e8:
        r7 = r0.total_unread_count;
        r8 = r13.intValue();
        r7 = r7 + r8;
        r0.total_unread_count = r7;
        r7 = r0.pushDialogs;
        r7.put(r9, r13);
    L_0x00f6:
        r6 = r6 + 1;
        goto L_0x0010;
    L_0x00fa:
        r1 = r19.isEmpty();
        if (r1 != 0) goto L_0x0108;
    L_0x0100:
        r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$ONJqyaSxnewsyizGxRK-V30P95A;
        r1.<init>(r0, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
    L_0x0108:
        r1 = r0.total_unread_count;
        if (r3 == r1) goto L_0x013c;
    L_0x010c:
        r1 = r0.notifyCheck;
        if (r1 != 0) goto L_0x011b;
    L_0x0110:
        r1 = r0.delayedPushMessages;
        r1.clear();
        r1 = r0.notifyCheck;
        r0.showOrUpdateNotification(r1);
        goto L_0x012e;
    L_0x011b:
        r1 = r0.lastOnlineFromOtherDevice;
        r2 = r0.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processLoadedUnreadMessages(LongSparseArray<Integer> longSparseArray, ArrayList<Message> arrayList, ArrayList<MessageObject> arrayList2, ArrayList<User> arrayList3, ArrayList<Chat> arrayList4, ArrayList<EncryptedChat> arrayList5) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList3, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList4, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(arrayList5, true);
        notificationsQueue.postRunnable(new -$$Lambda$NotificationsController$XEAogHRWLk5KuijEFvgR3DVl_Oc(this, arrayList, longSparseArray, arrayList2));
    }

    /* JADX WARNING: Missing block: B:12:0x0047, code skipped:
            if ((r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined) == false) goto L_0x004e;
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
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getNotificationsSettings(r5);
        r6 = new android.util.LongSparseArray;
        r6.<init>();
        r7 = 32;
        r10 = 1;
        if (r1 == 0) goto L_0x00fe;
    L_0x002c:
        r11 = 0;
    L_0x002d:
        r12 = r21.size();
        if (r11 >= r12) goto L_0x00fe;
    L_0x0033:
        r12 = r1.get(r11);
        r12 = (org.telegram.tgnet.TLRPC.Message) r12;
        if (r12 == 0) goto L_0x004e;
    L_0x003b:
        r13 = r12.silent;
        if (r13 == 0) goto L_0x004e;
    L_0x003f:
        r13 = r12.action;
        r14 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r14 != 0) goto L_0x0049;
    L_0x0045:
        r13 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r13 == 0) goto L_0x004e;
    L_0x0049:
        r18 = r5;
        r12 = r11;
        goto L_0x00f4;
    L_0x004e:
        r13 = r12.id;
        r13 = (long) r13;
        r15 = r12.to_id;
        r15 = r15.channel_id;
        if (r15 == 0) goto L_0x005a;
    L_0x0057:
        r8 = (long) r15;
        r8 = r8 << r7;
        r13 = r13 | r8;
    L_0x005a:
        r8 = r0.pushMessagesDict;
        r8 = r8.indexOfKey(r13);
        if (r8 < 0) goto L_0x0063;
    L_0x0062:
        goto L_0x0049;
    L_0x0063:
        r8 = new org.telegram.messenger.MessageObject;
        r9 = r0.currentAccount;
        r8.<init>(r9, r12, r4);
        r9 = r0.isPersonalMessage(r8);
        if (r9 == 0) goto L_0x0075;
    L_0x0070:
        r9 = r0.personal_count;
        r9 = r9 + r10;
        r0.personal_count = r9;
    L_0x0075:
        r12 = r11;
        r10 = r8.getDialogId();
        r15 = r8.messageOwner;
        r9 = r15.mentioned;
        if (r9 == 0) goto L_0x0086;
    L_0x0080:
        r9 = r15.from_id;
        r17 = r8;
        r7 = (long) r9;
        goto L_0x0089;
    L_0x0086:
        r17 = r8;
        r7 = r10;
    L_0x0089:
        r9 = r6.indexOfKey(r7);
        if (r9 < 0) goto L_0x009a;
    L_0x008f:
        r9 = r6.valueAt(r9);
        r9 = (java.lang.Boolean) r9;
        r9 = r9.booleanValue();
        goto L_0x00b3;
    L_0x009a:
        r9 = r0.getNotifyOverride(r5, r7);
        r15 = -1;
        if (r9 != r15) goto L_0x00a6;
    L_0x00a1:
        r9 = r0.isGlobalNotificationsEnabled(r7);
        goto L_0x00ac;
    L_0x00a6:
        r15 = 2;
        if (r9 == r15) goto L_0x00ab;
    L_0x00a9:
        r9 = 1;
        goto L_0x00ac;
    L_0x00ab:
        r9 = 0;
    L_0x00ac:
        r15 = java.lang.Boolean.valueOf(r9);
        r6.put(r7, r15);
    L_0x00b3:
        if (r9 == 0) goto L_0x00f2;
    L_0x00b5:
        r18 = r5;
        r4 = r0.opened_dialog_id;
        r9 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1));
        if (r9 != 0) goto L_0x00c2;
    L_0x00bd:
        r4 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r4 == 0) goto L_0x00c2;
    L_0x00c1:
        goto L_0x00f4;
    L_0x00c2:
        r4 = r0.pushMessagesDict;
        r5 = r17;
        r4.put(r13, r5);
        r4 = r0.pushMessages;
        r9 = 0;
        r4.add(r9, r5);
        r4 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1));
        if (r4 == 0) goto L_0x00f4;
    L_0x00d3:
        r4 = r0.pushDialogsOverrideMention;
        r4 = r4.get(r10);
        r4 = (java.lang.Integer) r4;
        r5 = r0.pushDialogsOverrideMention;
        if (r4 != 0) goto L_0x00e2;
    L_0x00df:
        r16 = 1;
        goto L_0x00ea;
    L_0x00e2:
        r4 = r4.intValue();
        r7 = 1;
        r4 = r4 + r7;
        r16 = r4;
    L_0x00ea:
        r4 = java.lang.Integer.valueOf(r16);
        r5.put(r10, r4);
        goto L_0x00f4;
    L_0x00f2:
        r18 = r5;
    L_0x00f4:
        r11 = r12 + 1;
        r5 = r18;
        r4 = 0;
        r7 = 32;
        r10 = 1;
        goto L_0x002d;
    L_0x00fe:
        r18 = r5;
        r1 = 0;
    L_0x0101:
        r4 = r22.size();
        if (r1 >= r4) goto L_0x015a;
    L_0x0107:
        r4 = r2.keyAt(r1);
        r7 = r6.indexOfKey(r4);
        if (r7 < 0) goto L_0x011f;
    L_0x0111:
        r7 = r6.valueAt(r7);
        r7 = (java.lang.Boolean) r7;
        r7 = r7.booleanValue();
        r8 = r7;
        r7 = r18;
        goto L_0x013a;
    L_0x011f:
        r7 = r18;
        r8 = r0.getNotifyOverride(r7, r4);
        r10 = -1;
        if (r8 != r10) goto L_0x012d;
    L_0x0128:
        r8 = r0.isGlobalNotificationsEnabled(r4);
        goto L_0x0133;
    L_0x012d:
        r10 = 2;
        if (r8 == r10) goto L_0x0132;
    L_0x0130:
        r8 = 1;
        goto L_0x0133;
    L_0x0132:
        r8 = 0;
    L_0x0133:
        r10 = java.lang.Boolean.valueOf(r8);
        r6.put(r4, r10);
    L_0x013a:
        if (r8 != 0) goto L_0x013d;
    L_0x013c:
        goto L_0x0155;
    L_0x013d:
        r8 = r2.valueAt(r1);
        r8 = (java.lang.Integer) r8;
        r8 = r8.intValue();
        r10 = r0.pushDialogs;
        r11 = java.lang.Integer.valueOf(r8);
        r10.put(r4, r11);
        r4 = r0.total_unread_count;
        r4 = r4 + r8;
        r0.total_unread_count = r4;
    L_0x0155:
        r1 = r1 + 1;
        r18 = r7;
        goto L_0x0101;
    L_0x015a:
        r7 = r18;
        if (r3 == 0) goto L_0x0262;
    L_0x015e:
        r1 = 0;
    L_0x015f:
        r2 = r23.size();
        if (r1 >= r2) goto L_0x0262;
    L_0x0165:
        r2 = r3.get(r1);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r4 = r2.getId();
        r4 = (long) r4;
        r8 = r2.messageOwner;
        r8 = r8.to_id;
        r8 = r8.channel_id;
        if (r8 == 0) goto L_0x017e;
    L_0x0178:
        r10 = (long) r8;
        r8 = 32;
        r10 = r10 << r8;
        r4 = r4 | r10;
        goto L_0x0180;
    L_0x017e:
        r8 = 32;
    L_0x0180:
        r10 = r0.pushMessagesDict;
        r10 = r10.indexOfKey(r4);
        if (r10 < 0) goto L_0x018d;
    L_0x0188:
        r5 = 0;
        r16 = 1;
        goto L_0x025e;
    L_0x018d:
        r10 = r0.isPersonalMessage(r2);
        if (r10 == 0) goto L_0x0199;
    L_0x0193:
        r10 = r0.personal_count;
        r9 = 1;
        r10 = r10 + r9;
        r0.personal_count = r10;
    L_0x0199:
        r10 = r2.getDialogId();
        r12 = r2.messageOwner;
        r13 = r12.random_id;
        r8 = r12.mentioned;
        if (r8 == 0) goto L_0x01ab;
    L_0x01a5:
        r8 = r12.from_id;
        r21 = r10;
        r9 = (long) r8;
        goto L_0x01af;
    L_0x01ab:
        r21 = r10;
        r9 = r21;
    L_0x01af:
        r8 = r6.indexOfKey(r9);
        if (r8 < 0) goto L_0x01c1;
    L_0x01b5:
        r8 = r6.valueAt(r8);
        r8 = (java.lang.Boolean) r8;
        r8 = r8.booleanValue();
        r12 = 2;
        goto L_0x01db;
    L_0x01c1:
        r8 = r0.getNotifyOverride(r7, r9);
        r11 = -1;
        if (r8 != r11) goto L_0x01ce;
    L_0x01c8:
        r8 = r0.isGlobalNotificationsEnabled(r9);
        r12 = 2;
        goto L_0x01d4;
    L_0x01ce:
        r12 = 2;
        if (r8 == r12) goto L_0x01d3;
    L_0x01d1:
        r8 = 1;
        goto L_0x01d4;
    L_0x01d3:
        r8 = 0;
    L_0x01d4:
        r11 = java.lang.Boolean.valueOf(r8);
        r6.put(r9, r11);
    L_0x01db:
        if (r8 == 0) goto L_0x0188;
    L_0x01dd:
        r18 = r13;
        r12 = r0.opened_dialog_id;
        r8 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1));
        if (r8 != 0) goto L_0x01ea;
    L_0x01e5:
        r8 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r8 == 0) goto L_0x01ea;
    L_0x01e9:
        goto L_0x0188;
    L_0x01ea:
        r11 = 0;
        r8 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1));
        if (r8 == 0) goto L_0x01f6;
    L_0x01f0:
        r8 = r0.pushMessagesDict;
        r8.put(r4, r2);
        goto L_0x0201;
    L_0x01f6:
        r4 = (r18 > r11 ? 1 : (r18 == r11 ? 0 : -1));
        if (r4 == 0) goto L_0x0201;
    L_0x01fa:
        r4 = r0.fcmRandomMessagesDict;
        r11 = r18;
        r4.put(r11, r2);
    L_0x0201:
        r4 = r0.pushMessages;
        r5 = 0;
        r4.add(r5, r2);
        r2 = (r21 > r9 ? 1 : (r21 == r9 ? 0 : -1));
        if (r2 == 0) goto L_0x022d;
    L_0x020b:
        r2 = r0.pushDialogsOverrideMention;
        r11 = r21;
        r2 = r2.get(r11);
        r2 = (java.lang.Integer) r2;
        r4 = r0.pushDialogsOverrideMention;
        if (r2 != 0) goto L_0x021d;
    L_0x0219:
        r2 = 1;
        r16 = 1;
        goto L_0x0225;
    L_0x021d:
        r2 = r2.intValue();
        r16 = 1;
        r2 = r2 + 1;
    L_0x0225:
        r2 = java.lang.Integer.valueOf(r2);
        r4.put(r11, r2);
        goto L_0x022f;
    L_0x022d:
        r16 = 1;
    L_0x022f:
        r2 = r0.pushDialogs;
        r2 = r2.get(r9);
        r2 = (java.lang.Integer) r2;
        if (r2 == 0) goto L_0x0240;
    L_0x0239:
        r4 = r2.intValue();
        r4 = r4 + 1;
        goto L_0x0241;
    L_0x0240:
        r4 = 1;
    L_0x0241:
        r4 = java.lang.Integer.valueOf(r4);
        if (r2 == 0) goto L_0x0250;
    L_0x0247:
        r8 = r0.total_unread_count;
        r2 = r2.intValue();
        r8 = r8 - r2;
        r0.total_unread_count = r8;
    L_0x0250:
        r2 = r0.total_unread_count;
        r8 = r4.intValue();
        r2 = r2 + r8;
        r0.total_unread_count = r2;
        r2 = r0.pushDialogs;
        r2.put(r9, r4);
    L_0x025e:
        r1 = r1 + 1;
        goto L_0x015f;
    L_0x0262:
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
        if (r6 >= 0) goto L_0x0281;
    L_0x0280:
        r5 = 1;
    L_0x0281:
        r0.showOrUpdateNotification(r5);
        r1 = r0.showBadgeNumber;
        if (r1 == 0) goto L_0x028f;
    L_0x0288:
        r1 = r20.getTotalAllUnreadCount();
        r0.setBadge(r1);
    L_0x028f:
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
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

    /* JADX WARNING: Removed duplicated region for block: B:85:0x014d  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x014c A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x014c A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x014d  */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r17, java.lang.String[] r18, boolean[] r19) {
        /*
        r16 = this;
        r0 = r16;
        r1 = r17;
        r2 = 0;
        r3 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r2);
        if (r3 != 0) goto L_0x0b68;
    L_0x000b:
        r3 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r3 == 0) goto L_0x0011;
    L_0x000f:
        goto L_0x0b68;
    L_0x0011:
        r3 = r1.messageOwner;
        r4 = r3.dialog_id;
        r3 = r3.to_id;
        r6 = r3.chat_id;
        if (r6 == 0) goto L_0x001c;
    L_0x001b:
        goto L_0x001e;
    L_0x001c:
        r6 = r3.channel_id;
    L_0x001e:
        r3 = r1.messageOwner;
        r3 = r3.to_id;
        r3 = r3.user_id;
        r7 = 1;
        if (r19 == 0) goto L_0x0029;
    L_0x0027:
        r19[r2] = r7;
    L_0x0029:
        r8 = r17.isFcmMessage();
        r9 = 27;
        r10 = 2;
        if (r8 == 0) goto L_0x00d9;
    L_0x0032:
        if (r6 != 0) goto L_0x0062;
    L_0x0034:
        if (r3 == 0) goto L_0x0062;
    L_0x0036:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3);
        r4 = "EnablePreviewAll";
        r3 = r3.getBoolean(r4, r7);
        if (r3 != 0) goto L_0x0058;
    L_0x0044:
        if (r19 == 0) goto L_0x0048;
    L_0x0046:
        r19[r2] = r2;
    L_0x0048:
        r3 = NUM; // 0x7f0d066b float:1.8745447E38 double:1.0531305893E-314;
        r4 = new java.lang.Object[r7];
        r1 = r1.localName;
        r4[r2] = r1;
        r1 = "NotificationMessageNoText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        return r1;
    L_0x0058:
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 <= r9) goto L_0x00d4;
    L_0x005c:
        r3 = r1.localName;
        r18[r2] = r3;
        goto L_0x00d4;
    L_0x0062:
        if (r6 == 0) goto L_0x00d4;
    L_0x0064:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3);
        r4 = r1.localChannel;
        if (r4 != 0) goto L_0x0076;
    L_0x006e:
        r4 = "EnablePreviewGroup";
        r4 = r3.getBoolean(r4, r7);
        if (r4 == 0) goto L_0x0082;
    L_0x0076:
        r4 = r1.localChannel;
        if (r4 == 0) goto L_0x00b8;
    L_0x007a:
        r4 = "EnablePreviewChannel";
        r3 = r3.getBoolean(r4, r7);
        if (r3 != 0) goto L_0x00b8;
    L_0x0082:
        if (r19 == 0) goto L_0x0086;
    L_0x0084:
        r19[r2] = r2;
    L_0x0086:
        r3 = r17.isMegagroup();
        if (r3 != 0) goto L_0x00a4;
    L_0x008c:
        r3 = r1.messageOwner;
        r3 = r3.to_id;
        r3 = r3.channel_id;
        if (r3 == 0) goto L_0x00a4;
    L_0x0094:
        r3 = NUM; // 0x7f0d023d float:1.8743277E38 double:1.0531300606E-314;
        r4 = new java.lang.Object[r7];
        r1 = r1.localName;
        r4[r2] = r1;
        r1 = "ChannelMessageNoText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        return r1;
    L_0x00a4:
        r3 = NUM; // 0x7f0d065f float:1.8745423E38 double:1.0531305834E-314;
        r4 = new java.lang.Object[r10];
        r5 = r1.localUserName;
        r4[r2] = r5;
        r1 = r1.localName;
        r4[r7] = r1;
        r1 = "NotificationMessageGroupNoText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        return r1;
    L_0x00b8:
        r3 = r1.messageOwner;
        r3 = r3.to_id;
        r3 = r3.channel_id;
        if (r3 == 0) goto L_0x00d0;
    L_0x00c0:
        r3 = r17.isMegagroup();
        if (r3 == 0) goto L_0x00c7;
    L_0x00c6:
        goto L_0x00d0;
    L_0x00c7:
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 <= r9) goto L_0x00d4;
    L_0x00cb:
        r3 = r1.localName;
        r18[r2] = r3;
        goto L_0x00d4;
    L_0x00d0:
        r3 = r1.localUserName;
        r18[r2] = r3;
    L_0x00d4:
        r1 = r1.messageOwner;
        r1 = r1.message;
        return r1;
    L_0x00d9:
        if (r3 != 0) goto L_0x00ef;
    L_0x00db:
        r3 = r17.isFromUser();
        if (r3 != 0) goto L_0x00ea;
    L_0x00e1:
        r3 = r17.getId();
        if (r3 >= 0) goto L_0x00e8;
    L_0x00e7:
        goto L_0x00ea;
    L_0x00e8:
        r3 = -r6;
        goto L_0x00ff;
    L_0x00ea:
        r3 = r1.messageOwner;
        r3 = r3.from_id;
        goto L_0x00ff;
    L_0x00ef:
        r8 = r0.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.getClientUserId();
        if (r3 != r8) goto L_0x00ff;
    L_0x00fb:
        r3 = r1.messageOwner;
        r3 = r3.from_id;
    L_0x00ff:
        r11 = 0;
        r8 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1));
        if (r8 != 0) goto L_0x010d;
    L_0x0105:
        if (r6 == 0) goto L_0x010a;
    L_0x0107:
        r4 = -r6;
        r4 = (long) r4;
        goto L_0x010d;
    L_0x010a:
        if (r3 == 0) goto L_0x010d;
    L_0x010c:
        r4 = (long) r3;
    L_0x010d:
        r8 = 0;
        if (r3 <= 0) goto L_0x0133;
    L_0x0110:
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);
        r12 = java.lang.Integer.valueOf(r3);
        r11 = r11.getUser(r12);
        if (r11 == 0) goto L_0x0149;
    L_0x0120:
        r11 = org.telegram.messenger.UserObject.getUserName(r11);
        if (r6 == 0) goto L_0x0129;
    L_0x0126:
        r18[r2] = r11;
        goto L_0x014a;
    L_0x0129:
        r12 = android.os.Build.VERSION.SDK_INT;
        if (r12 <= r9) goto L_0x0130;
    L_0x012d:
        r18[r2] = r11;
        goto L_0x014a;
    L_0x0130:
        r18[r2] = r8;
        goto L_0x014a;
    L_0x0133:
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);
        r12 = -r3;
        r12 = java.lang.Integer.valueOf(r12);
        r11 = r11.getChat(r12);
        if (r11 == 0) goto L_0x0149;
    L_0x0144:
        r11 = r11.title;
        r18[r2] = r11;
        goto L_0x014a;
    L_0x0149:
        r11 = r8;
    L_0x014a:
        if (r11 != 0) goto L_0x014d;
    L_0x014c:
        return r8;
    L_0x014d:
        if (r6 == 0) goto L_0x0171;
    L_0x014f:
        r12 = r0.currentAccount;
        r12 = org.telegram.messenger.MessagesController.getInstance(r12);
        r13 = java.lang.Integer.valueOf(r6);
        r12 = r12.getChat(r13);
        if (r12 != 0) goto L_0x0160;
    L_0x015f:
        return r8;
    L_0x0160:
        r13 = org.telegram.messenger.ChatObject.isChannel(r12);
        if (r13 == 0) goto L_0x0172;
    L_0x0166:
        r13 = r12.megagroup;
        if (r13 != 0) goto L_0x0172;
    L_0x016a:
        r13 = android.os.Build.VERSION.SDK_INT;
        if (r13 > r9) goto L_0x0172;
    L_0x016e:
        r18[r2] = r8;
        goto L_0x0172;
    L_0x0171:
        r12 = r8;
    L_0x0172:
        r5 = (int) r4;
        if (r5 != 0) goto L_0x0181;
    L_0x0175:
        r18[r2] = r8;
        r1 = NUM; // 0x7f0d0ab3 float:1.874767E38 double:1.053131131E-314;
        r2 = "YouHaveNewMessage";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0181:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);
        r5 = org.telegram.messenger.ChatObject.isChannel(r12);
        if (r5 == 0) goto L_0x0193;
    L_0x018d:
        r5 = r12.megagroup;
        if (r5 != 0) goto L_0x0193;
    L_0x0191:
        r5 = 1;
        goto L_0x0194;
    L_0x0193:
        r5 = 0;
    L_0x0194:
        if (r6 != 0) goto L_0x01a0;
    L_0x0196:
        if (r3 == 0) goto L_0x01a0;
    L_0x0198:
        r9 = "EnablePreviewAll";
        r9 = r4.getBoolean(r9, r7);
        if (r9 != 0) goto L_0x01b6;
    L_0x01a0:
        if (r6 == 0) goto L_0x0b5a;
    L_0x01a2:
        if (r5 != 0) goto L_0x01ac;
    L_0x01a4:
        r6 = "EnablePreviewGroup";
        r6 = r4.getBoolean(r6, r7);
        if (r6 != 0) goto L_0x01b6;
    L_0x01ac:
        if (r5 == 0) goto L_0x0b5a;
    L_0x01ae:
        r5 = "EnablePreviewChannel";
        r4 = r4.getBoolean(r5, r7);
        if (r4 == 0) goto L_0x0b5a;
    L_0x01b6:
        r4 = r1.messageOwner;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        r6 = "📎 ";
        r9 = "📹 ";
        r13 = "🖼 ";
        r14 = 19;
        if (r5 == 0) goto L_0x0997;
    L_0x01c7:
        r18[r2] = r8;
        r4 = r4.action;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r5 != 0) goto L_0x0989;
    L_0x01cf:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r5 == 0) goto L_0x01d5;
    L_0x01d3:
        goto L_0x0989;
    L_0x01d5:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
        if (r5 == 0) goto L_0x01e7;
    L_0x01d9:
        r1 = NUM; // 0x7f0d063e float:1.8745356E38 double:1.053130567E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r11;
        r2 = "NotificationContactNewPhoto";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x01e7:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
        r15 = 3;
        if (r5 == 0) goto L_0x0248;
    L_0x01ec:
        r3 = NUM; // 0x7f0d0b05 float:1.8747836E38 double:1.0531311713E-314;
        r4 = new java.lang.Object[r10];
        r5 = org.telegram.messenger.LocaleController.getInstance();
        r5 = r5.formatterYear;
        r6 = r1.messageOwner;
        r6 = r6.date;
        r8 = (long) r6;
        r11 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r8 = r8 * r11;
        r5 = r5.format(r8);
        r4[r2] = r5;
        r5 = org.telegram.messenger.LocaleController.getInstance();
        r5 = r5.formatterDay;
        r6 = r1.messageOwner;
        r6 = r6.date;
        r8 = (long) r6;
        r8 = r8 * r11;
        r5 = r5.format(r8);
        r4[r7] = r5;
        r5 = "formatDateAtTime";
        r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4);
        r4 = NUM; // 0x7f0d0676 float:1.874547E38 double:1.0531305947E-314;
        r5 = 4;
        r5 = new java.lang.Object[r5];
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.getCurrentUser();
        r6 = r6.first_name;
        r5[r2] = r6;
        r5[r7] = r3;
        r1 = r1.messageOwner;
        r1 = r1.action;
        r2 = r1.title;
        r5[r10] = r2;
        r1 = r1.address;
        r5[r15] = r1;
        r1 = "NotificationUnrecognizedDevice";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r4, r5);
        return r1;
    L_0x0248:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r5 != 0) goto L_0x0982;
    L_0x024c:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
        if (r5 == 0) goto L_0x0252;
    L_0x0250:
        goto L_0x0982;
    L_0x0252:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r5 == 0) goto L_0x026c;
    L_0x0256:
        r2 = r4.reason;
        r1 = r17.isOut();
        if (r1 != 0) goto L_0x0b4f;
    L_0x025e:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        if (r1 == 0) goto L_0x0b4f;
    L_0x0262:
        r1 = NUM; // 0x7f0d01db float:1.8743078E38 double:1.053130012E-314;
        r2 = "CallMessageIncomingMissed";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x026c:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r5 == 0) goto L_0x0376;
    L_0x0270:
        r5 = r4.user_id;
        if (r5 != 0) goto L_0x028c;
    L_0x0274:
        r4 = r4.users;
        r4 = r4.size();
        if (r4 != r7) goto L_0x028c;
    L_0x027c:
        r4 = r1.messageOwner;
        r4 = r4.action;
        r4 = r4.users;
        r4 = r4.get(r2);
        r4 = (java.lang.Integer) r4;
        r5 = r4.intValue();
    L_0x028c:
        if (r5 == 0) goto L_0x031f;
    L_0x028e:
        r1 = r1.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x02ac;
    L_0x0296:
        r1 = r12.megagroup;
        if (r1 != 0) goto L_0x02ac;
    L_0x029a:
        r1 = NUM; // 0x7f0d020d float:1.874318E38 double:1.053130037E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "ChannelAddedByNotification";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x02ac:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getClientUserId();
        if (r5 != r1) goto L_0x02ca;
    L_0x02b8:
        r1 = NUM; // 0x7f0d064a float:1.874538E38 double:1.053130573E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationInvitedToGroup";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x02ca:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r4 = java.lang.Integer.valueOf(r5);
        r1 = r1.getUser(r4);
        if (r1 != 0) goto L_0x02db;
    L_0x02da:
        return r8;
    L_0x02db:
        r4 = r1.id;
        if (r3 != r4) goto L_0x0307;
    L_0x02df:
        r1 = r12.megagroup;
        if (r1 == 0) goto L_0x02f5;
    L_0x02e3:
        r1 = NUM; // 0x7f0d0643 float:1.8745366E38 double:1.0531305695E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationGroupAddSelfMega";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x02f5:
        r1 = NUM; // 0x7f0d0642 float:1.8745364E38 double:1.053130569E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationGroupAddSelf";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0307:
        r3 = NUM; // 0x7f0d0641 float:1.8745362E38 double:1.0531305685E-314;
        r4 = new java.lang.Object[r15];
        r4[r2] = r11;
        r2 = r12.title;
        r4[r7] = r2;
        r1 = org.telegram.messenger.UserObject.getUserName(r1);
        r4[r10] = r1;
        r1 = "NotificationGroupAddMember";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        return r1;
    L_0x031f:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = 0;
    L_0x0325:
        r5 = r1.messageOwner;
        r5 = r5.action;
        r5 = r5.users;
        r5 = r5.size();
        if (r4 >= r5) goto L_0x035e;
    L_0x0331:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r6 = r1.messageOwner;
        r6 = r6.action;
        r6 = r6.users;
        r6 = r6.get(r4);
        r6 = (java.lang.Integer) r6;
        r5 = r5.getUser(r6);
        if (r5 == 0) goto L_0x035b;
    L_0x0349:
        r5 = org.telegram.messenger.UserObject.getUserName(r5);
        r6 = r3.length();
        if (r6 == 0) goto L_0x0358;
    L_0x0353:
        r6 = ", ";
        r3.append(r6);
    L_0x0358:
        r3.append(r5);
    L_0x035b:
        r4 = r4 + 1;
        goto L_0x0325;
    L_0x035e:
        r1 = NUM; // 0x7f0d0641 float:1.8745362E38 double:1.0531305685E-314;
        r4 = new java.lang.Object[r15];
        r4[r2] = r11;
        r2 = r12.title;
        r4[r7] = r2;
        r2 = r3.toString();
        r4[r10] = r2;
        r2 = "NotificationGroupAddMember";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r4);
        return r1;
    L_0x0376:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
        if (r5 == 0) goto L_0x038c;
    L_0x037a:
        r1 = NUM; // 0x7f0d064b float:1.8745382E38 double:1.0531305735E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationInvitedToGroupByLink";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x038c:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
        if (r5 == 0) goto L_0x03a2;
    L_0x0390:
        r1 = NUM; // 0x7f0d063f float:1.8745358E38 double:1.0531305676E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r4.title;
        r3[r7] = r2;
        r2 = "NotificationEditedGroupName";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x03a2:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
        if (r5 != 0) goto L_0x0954;
    L_0x03a6:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
        if (r5 == 0) goto L_0x03ac;
    L_0x03aa:
        goto L_0x0954;
    L_0x03ac:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r5 == 0) goto L_0x0419;
    L_0x03b0:
        r4 = r4.user_id;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r5 = r5.getClientUserId();
        if (r4 != r5) goto L_0x03d0;
    L_0x03be:
        r1 = NUM; // 0x7f0d0648 float:1.8745376E38 double:1.053130572E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationGroupKickYou";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x03d0:
        r4 = r1.messageOwner;
        r4 = r4.action;
        r4 = r4.user_id;
        if (r4 != r3) goto L_0x03ea;
    L_0x03d8:
        r1 = NUM; // 0x7f0d0649 float:1.8745378E38 double:1.0531305725E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationGroupLeftMember";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x03ea:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r1 = r1.messageOwner;
        r1 = r1.action;
        r1 = r1.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r3.getUser(r1);
        if (r1 != 0) goto L_0x0401;
    L_0x0400:
        return r8;
    L_0x0401:
        r3 = NUM; // 0x7f0d0647 float:1.8745374E38 double:1.0531305715E-314;
        r4 = new java.lang.Object[r15];
        r4[r2] = r11;
        r2 = r12.title;
        r4[r7] = r2;
        r1 = org.telegram.messenger.UserObject.getUserName(r1);
        r4[r10] = r1;
        r1 = "NotificationGroupKickMember";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        return r1;
    L_0x0419:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
        if (r3 == 0) goto L_0x0424;
    L_0x041d:
        r1 = r1.messageText;
        r1 = r1.toString();
        return r1;
    L_0x0424:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r3 == 0) goto L_0x042f;
    L_0x0428:
        r1 = r1.messageText;
        r1 = r1.toString();
        return r1;
    L_0x042f:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r3 == 0) goto L_0x0443;
    L_0x0433:
        r1 = NUM; // 0x7f0d007d float:1.8742368E38 double:1.0531298393E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "ActionMigrateFromGroupNotify";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0443:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r3 == 0) goto L_0x0457;
    L_0x0447:
        r1 = NUM; // 0x7f0d007d float:1.8742368E38 double:1.0531298393E-314;
        r3 = new java.lang.Object[r7];
        r4 = r4.title;
        r3[r2] = r4;
        r2 = "ActionMigrateFromGroupNotify";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0457:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
        if (r3 == 0) goto L_0x0462;
    L_0x045b:
        r1 = r1.messageText;
        r1 = r1.toString();
        return r1;
    L_0x0462:
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r3 == 0) goto L_0x0b4f;
    L_0x0466:
        if (r12 == 0) goto L_0x06f8;
    L_0x0468:
        r4 = org.telegram.messenger.ChatObject.isChannel(r12);
        if (r4 == 0) goto L_0x0472;
    L_0x046e:
        r4 = r12.megagroup;
        if (r4 == 0) goto L_0x06f8;
    L_0x0472:
        r1 = r1.replyMessageObject;
        if (r1 != 0) goto L_0x0488;
    L_0x0476:
        r1 = NUM; // 0x7f0d062b float:1.8745317E38 double:1.0531305577E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedNoText";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0488:
        r4 = r1.isMusic();
        if (r4 == 0) goto L_0x04a0;
    L_0x048e:
        r1 = NUM; // 0x7f0d0629 float:1.8745313E38 double:1.0531305567E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedMusic";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x04a0:
        r4 = r1.isVideo();
        r5 = NUM; // 0x7f0d0637 float:1.8745342E38 double:1.0531305636E-314;
        r8 = "NotificationActionPinnedText";
        if (r4 == 0) goto L_0x04ed;
    L_0x04ab:
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 < r14) goto L_0x04db;
    L_0x04af:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x04db;
    L_0x04b9:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r9);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3.append(r1);
        r1 = r3.toString();
        r3 = new java.lang.Object[r15];
        r3[r2] = r11;
        r3[r7] = r1;
        r1 = r12.title;
        r3[r10] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r5, r3);
        return r1;
    L_0x04db:
        r1 = NUM; // 0x7f0d0639 float:1.8745346E38 double:1.0531305646E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedVideo";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x04ed:
        r4 = r1.isGif();
        if (r4 == 0) goto L_0x0538;
    L_0x04f3:
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 < r14) goto L_0x0526;
    L_0x04f7:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0526;
    L_0x0501:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "🎬 ";
        r3.append(r4);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3.append(r1);
        r1 = r3.toString();
        r3 = new java.lang.Object[r15];
        r3[r2] = r11;
        r3[r7] = r1;
        r1 = r12.title;
        r3[r10] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r5, r3);
        return r1;
    L_0x0526:
        r1 = NUM; // 0x7f0d0625 float:1.8745305E38 double:1.0531305547E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedGif";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0538:
        r4 = r1.isVoice();
        if (r4 == 0) goto L_0x0550;
    L_0x053e:
        r1 = NUM; // 0x7f0d063b float:1.874535E38 double:1.0531305656E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedVoice";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0550:
        r4 = r1.isRoundVideo();
        if (r4 == 0) goto L_0x0568;
    L_0x0556:
        r1 = NUM; // 0x7f0d0631 float:1.874533E38 double:1.0531305606E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedRound";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0568:
        r4 = r1.isSticker();
        if (r4 == 0) goto L_0x059a;
    L_0x056e:
        r1 = r1.getStickerEmoji();
        if (r1 == 0) goto L_0x0588;
    L_0x0574:
        r3 = NUM; // 0x7f0d0635 float:1.8745338E38 double:1.0531305626E-314;
        r4 = new java.lang.Object[r15];
        r4[r2] = r11;
        r2 = r12.title;
        r4[r7] = r2;
        r4[r10] = r1;
        r1 = "NotificationActionPinnedStickerEmoji";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        return r1;
    L_0x0588:
        r1 = NUM; // 0x7f0d0633 float:1.8745334E38 double:1.0531305616E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedSticker";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x059a:
        r4 = r1.messageOwner;
        r9 = r4.media;
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r3 == 0) goto L_0x05e2;
    L_0x05a2:
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 < r14) goto L_0x05d0;
    L_0x05a6:
        r3 = r4.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x05d0;
    L_0x05ae:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r6);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3.append(r1);
        r1 = r3.toString();
        r3 = new java.lang.Object[r15];
        r3[r2] = r11;
        r3[r7] = r1;
        r1 = r12.title;
        r3[r10] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r5, r3);
        return r1;
    L_0x05d0:
        r1 = NUM; // 0x7f0d061b float:1.8745285E38 double:1.05313055E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedFile";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x05e2:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r3 != 0) goto L_0x06e6;
    L_0x05e6:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r3 == 0) goto L_0x05ec;
    L_0x05ea:
        goto L_0x06e6;
    L_0x05ec:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r3 == 0) goto L_0x0602;
    L_0x05f0:
        r1 = NUM; // 0x7f0d0623 float:1.8745301E38 double:1.0531305537E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedGeoLive";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0602:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r3 == 0) goto L_0x0624;
    L_0x0606:
        r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r9;
        r1 = NUM; // 0x7f0d0619 float:1.874528E38 double:1.053130549E-314;
        r3 = new java.lang.Object[r15];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = r9.first_name;
        r4 = r9.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r4);
        r3[r10] = r2;
        r2 = "NotificationActionPinnedContact2";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0624:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r3 == 0) goto L_0x0642;
    L_0x0628:
        r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r9;
        r1 = NUM; // 0x7f0d062f float:1.8745325E38 double:1.0531305597E-314;
        r3 = new java.lang.Object[r15];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = r9.poll;
        r2 = r2.question;
        r3[r10] = r2;
        r2 = "NotificationActionPinnedPoll2";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0642:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r3 == 0) goto L_0x0686;
    L_0x0646:
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 < r14) goto L_0x0674;
    L_0x064a:
        r3 = r4.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0674;
    L_0x0652:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r13);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3.append(r1);
        r1 = r3.toString();
        r3 = new java.lang.Object[r15];
        r3[r2] = r11;
        r3[r7] = r1;
        r1 = r12.title;
        r3[r10] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r5, r3);
        return r1;
    L_0x0674:
        r1 = NUM; // 0x7f0d062d float:1.8745321E38 double:1.0531305587E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedPhoto";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0686:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r3 == 0) goto L_0x069c;
    L_0x068a:
        r1 = NUM; // 0x7f0d061d float:1.8745289E38 double:1.053130551E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedGame";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x069c:
        r3 = r1.messageText;
        if (r3 == 0) goto L_0x06d4;
    L_0x06a0:
        r3 = r3.length();
        if (r3 <= 0) goto L_0x06d4;
    L_0x06a6:
        r1 = r1.messageText;
        r3 = r1.length();
        r4 = 20;
        if (r3 <= r4) goto L_0x06c5;
    L_0x06b0:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r1 = r1.subSequence(r2, r4);
        r3.append(r1);
        r1 = "...";
        r3.append(r1);
        r1 = r3.toString();
    L_0x06c5:
        r3 = new java.lang.Object[r15];
        r3[r2] = r11;
        r3[r7] = r1;
        r1 = r12.title;
        r3[r10] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r5, r3);
        return r1;
    L_0x06d4:
        r1 = NUM; // 0x7f0d062b float:1.8745317E38 double:1.0531305577E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedNoText";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x06e6:
        r1 = NUM; // 0x7f0d0621 float:1.8745297E38 double:1.0531305527E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedGeo";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x06f8:
        r1 = r1.replyMessageObject;
        if (r1 != 0) goto L_0x070c;
    L_0x06fc:
        r1 = NUM; // 0x7f0d062c float:1.874532E38 double:1.053130558E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedNoTextChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x070c:
        r3 = r1.isMusic();
        if (r3 == 0) goto L_0x0722;
    L_0x0712:
        r1 = NUM; // 0x7f0d062a float:1.8745315E38 double:1.053130557E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedMusicChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0722:
        r3 = r1.isVideo();
        r4 = NUM; // 0x7f0d0638 float:1.8745344E38 double:1.053130564E-314;
        r5 = "NotificationActionPinnedTextChannel";
        if (r3 == 0) goto L_0x076b;
    L_0x072d:
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 < r14) goto L_0x075b;
    L_0x0731:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x075b;
    L_0x073b:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r9);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3.append(r1);
        r1 = r3.toString();
        r3 = new java.lang.Object[r10];
        r6 = r12.title;
        r3[r2] = r6;
        r3[r7] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
        return r1;
    L_0x075b:
        r1 = NUM; // 0x7f0d063a float:1.8745348E38 double:1.053130565E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedVideoChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x076b:
        r3 = r1.isGif();
        if (r3 == 0) goto L_0x07b2;
    L_0x0771:
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 < r14) goto L_0x07a2;
    L_0x0775:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x07a2;
    L_0x077f:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r6 = "🎬 ";
        r3.append(r6);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3.append(r1);
        r1 = r3.toString();
        r3 = new java.lang.Object[r10];
        r6 = r12.title;
        r3[r2] = r6;
        r3[r7] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
        return r1;
    L_0x07a2:
        r1 = NUM; // 0x7f0d0626 float:1.8745307E38 double:1.053130555E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedGifChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x07b2:
        r3 = r1.isVoice();
        if (r3 == 0) goto L_0x07c8;
    L_0x07b8:
        r1 = NUM; // 0x7f0d063c float:1.8745352E38 double:1.053130566E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedVoiceChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x07c8:
        r3 = r1.isRoundVideo();
        if (r3 == 0) goto L_0x07de;
    L_0x07ce:
        r1 = NUM; // 0x7f0d0632 float:1.8745332E38 double:1.053130561E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedRoundChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x07de:
        r3 = r1.isSticker();
        if (r3 == 0) goto L_0x080c;
    L_0x07e4:
        r1 = r1.getStickerEmoji();
        if (r1 == 0) goto L_0x07fc;
    L_0x07ea:
        r3 = NUM; // 0x7f0d0636 float:1.874534E38 double:1.053130563E-314;
        r4 = new java.lang.Object[r10];
        r5 = r12.title;
        r4[r2] = r5;
        r4[r7] = r1;
        r1 = "NotificationActionPinnedStickerEmojiChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        return r1;
    L_0x07fc:
        r1 = NUM; // 0x7f0d0634 float:1.8745336E38 double:1.053130562E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedStickerChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x080c:
        r3 = r1.messageOwner;
        r8 = r3.media;
        r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r9 == 0) goto L_0x0850;
    L_0x0814:
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r14) goto L_0x0840;
    L_0x0818:
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0840;
    L_0x0820:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r6);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3.append(r1);
        r1 = r3.toString();
        r3 = new java.lang.Object[r10];
        r6 = r12.title;
        r3[r2] = r6;
        r3[r7] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
        return r1;
    L_0x0840:
        r1 = NUM; // 0x7f0d061c float:1.8745287E38 double:1.0531305503E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedFileChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0850:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r6 != 0) goto L_0x0944;
    L_0x0854:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r6 == 0) goto L_0x085a;
    L_0x0858:
        goto L_0x0944;
    L_0x085a:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r6 == 0) goto L_0x086e;
    L_0x085e:
        r1 = NUM; // 0x7f0d0624 float:1.8745303E38 double:1.053130554E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedGeoLiveChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x086e:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r6 == 0) goto L_0x088e;
    L_0x0872:
        r8 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r8;
        r1 = NUM; // 0x7f0d061a float:1.8745283E38 double:1.0531305493E-314;
        r3 = new java.lang.Object[r10];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = r8.first_name;
        r4 = r8.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r4);
        r3[r7] = r2;
        r2 = "NotificationActionPinnedContactChannel2";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x088e:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r6 == 0) goto L_0x08aa;
    L_0x0892:
        r8 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r8;
        r1 = NUM; // 0x7f0d0630 float:1.8745327E38 double:1.05313056E-314;
        r3 = new java.lang.Object[r10];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = r8.poll;
        r2 = r2.question;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedPollChannel2";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x08aa:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r6 == 0) goto L_0x08ea;
    L_0x08ae:
        r6 = android.os.Build.VERSION.SDK_INT;
        if (r6 < r14) goto L_0x08da;
    L_0x08b2:
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x08da;
    L_0x08ba:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r13);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3.append(r1);
        r1 = r3.toString();
        r3 = new java.lang.Object[r10];
        r6 = r12.title;
        r3[r2] = r6;
        r3[r7] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
        return r1;
    L_0x08da:
        r1 = NUM; // 0x7f0d062e float:1.8745323E38 double:1.053130559E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedPhotoChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x08ea:
        r3 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r3 == 0) goto L_0x08fe;
    L_0x08ee:
        r1 = NUM; // 0x7f0d061e float:1.874529E38 double:1.0531305513E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedGameChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x08fe:
        r3 = r1.messageText;
        if (r3 == 0) goto L_0x0934;
    L_0x0902:
        r3 = r3.length();
        if (r3 <= 0) goto L_0x0934;
    L_0x0908:
        r1 = r1.messageText;
        r3 = r1.length();
        r6 = 20;
        if (r3 <= r6) goto L_0x0927;
    L_0x0912:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r1 = r1.subSequence(r2, r6);
        r3.append(r1);
        r1 = "...";
        r3.append(r1);
        r1 = r3.toString();
    L_0x0927:
        r3 = new java.lang.Object[r10];
        r6 = r12.title;
        r3[r2] = r6;
        r3[r7] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
        return r1;
    L_0x0934:
        r1 = NUM; // 0x7f0d062c float:1.874532E38 double:1.053130558E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedNoTextChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0944:
        r1 = NUM; // 0x7f0d0622 float:1.87453E38 double:1.053130553E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedGeoChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0954:
        r1 = r1.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x0970;
    L_0x095c:
        r1 = r12.megagroup;
        if (r1 != 0) goto L_0x0970;
    L_0x0960:
        r1 = NUM; // 0x7f0d024b float:1.8743305E38 double:1.0531300676E-314;
        r3 = new java.lang.Object[r7];
        r4 = r12.title;
        r3[r2] = r4;
        r2 = "ChannelPhotoEditNotification";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0970:
        r1 = NUM; // 0x7f0d0640 float:1.874536E38 double:1.053130568E-314;
        r3 = new java.lang.Object[r10];
        r3[r2] = r11;
        r2 = r12.title;
        r3[r7] = r2;
        r2 = "NotificationEditedGroupPhoto";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0982:
        r1 = r1.messageText;
        r1 = r1.toString();
        return r1;
    L_0x0989:
        r1 = NUM; // 0x7f0d063d float:1.8745354E38 double:1.0531305666E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r11;
        r2 = "NotificationContactJoined";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        return r1;
    L_0x0997:
        r2 = r17.isMediaEmpty();
        if (r2 == 0) goto L_0x09b6;
    L_0x099d:
        r2 = r1.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x09ac;
    L_0x09a7:
        r1 = r1.messageOwner;
        r1 = r1.message;
        return r1;
    L_0x09ac:
        r1 = NUM; // 0x7f0d05a5 float:1.8745046E38 double:1.0531304915E-314;
        r2 = "Message";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x09b6:
        r2 = r1.messageOwner;
        r3 = r2.media;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r3 == 0) goto L_0x09fa;
    L_0x09be:
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 < r14) goto L_0x09de;
    L_0x09c2:
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x09de;
    L_0x09ca:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r13);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r2.append(r1);
        r1 = r2.toString();
        return r1;
    L_0x09de:
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.ttl_seconds;
        if (r1 == 0) goto L_0x09f0;
    L_0x09e6:
        r1 = NUM; // 0x7f0d0138 float:1.8742748E38 double:1.0531299317E-314;
        r2 = "AttachDestructingPhoto";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x09f0:
        r1 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r2 = "AttachPhoto";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x09fa:
        r2 = r17.isVideo();
        if (r2 == 0) goto L_0x0a3e;
    L_0x0a00:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r14) goto L_0x0a22;
    L_0x0a04:
        r2 = r1.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0a22;
    L_0x0a0e:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r9);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r2.append(r1);
        r1 = r2.toString();
        return r1;
    L_0x0a22:
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.ttl_seconds;
        if (r1 == 0) goto L_0x0a34;
    L_0x0a2a:
        r1 = NUM; // 0x7f0d0139 float:1.874275E38 double:1.053129932E-314;
        r2 = "AttachDestructingVideo";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0a34:
        r1 = NUM; // 0x7f0d014d float:1.874279E38 double:1.053129942E-314;
        r2 = "AttachVideo";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0a3e:
        r2 = r17.isGame();
        if (r2 == 0) goto L_0x0a4e;
    L_0x0a44:
        r1 = NUM; // 0x7f0d013b float:1.8742754E38 double:1.053129933E-314;
        r2 = "AttachGame";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0a4e:
        r2 = r17.isVoice();
        if (r2 == 0) goto L_0x0a5e;
    L_0x0a54:
        r1 = NUM; // 0x7f0d0135 float:1.8742741E38 double:1.05312993E-314;
        r2 = "AttachAudio";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0a5e:
        r2 = r17.isRoundVideo();
        if (r2 == 0) goto L_0x0a6e;
    L_0x0a64:
        r1 = NUM; // 0x7f0d0149 float:1.8742782E38 double:1.05312994E-314;
        r2 = "AttachRound";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0a6e:
        r2 = r17.isMusic();
        if (r2 == 0) goto L_0x0a7e;
    L_0x0a74:
        r1 = NUM; // 0x7f0d0146 float:1.8742776E38 double:1.0531299386E-314;
        r2 = "AttachMusic";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0a7e:
        r2 = r1.messageOwner;
        r2 = r2.media;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r3 == 0) goto L_0x0a90;
    L_0x0a86:
        r1 = NUM; // 0x7f0d0137 float:1.8742746E38 double:1.053129931E-314;
        r2 = "AttachContact";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0a90:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r3 == 0) goto L_0x0a9e;
    L_0x0a94:
        r1 = NUM; // 0x7f0d0813 float:1.8746307E38 double:1.053130799E-314;
        r2 = "Poll";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0a9e:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r3 != 0) goto L_0x0b50;
    L_0x0aa2:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r3 == 0) goto L_0x0aa8;
    L_0x0aa6:
        goto L_0x0b50;
    L_0x0aa8:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r3 == 0) goto L_0x0ab6;
    L_0x0aac:
        r1 = NUM; // 0x7f0d0141 float:1.8742766E38 double:1.053129936E-314;
        r2 = "AttachLiveLocation";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0ab6:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r2 == 0) goto L_0x0b4f;
    L_0x0aba:
        r2 = r17.isSticker();
        if (r2 == 0) goto L_0x0aee;
    L_0x0ac0:
        r1 = r17.getStickerEmoji();
        if (r1 == 0) goto L_0x0ae4;
    L_0x0ac6:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r1);
        r1 = " ";
        r2.append(r1);
        r1 = NUM; // 0x7f0d014a float:1.8742784E38 double:1.0531299406E-314;
        r3 = "AttachSticker";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r2.append(r1);
        r1 = r2.toString();
        return r1;
    L_0x0ae4:
        r1 = NUM; // 0x7f0d014a float:1.8742784E38 double:1.0531299406E-314;
        r2 = "AttachSticker";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0aee:
        r2 = r17.isGif();
        if (r2 == 0) goto L_0x0b23;
    L_0x0af4:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r14) goto L_0x0b19;
    L_0x0af8:
        r2 = r1.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0b19;
    L_0x0b02:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "🎬 ";
        r2.append(r3);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r2.append(r1);
        r1 = r2.toString();
        return r1;
    L_0x0b19:
        r1 = NUM; // 0x7f0d013c float:1.8742756E38 double:1.0531299337E-314;
        r2 = "AttachGif";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0b23:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r14) goto L_0x0b45;
    L_0x0b27:
        r2 = r1.messageOwner;
        r2 = r2.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0b45;
    L_0x0b31:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r6);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r2.append(r1);
        r1 = r2.toString();
        return r1;
    L_0x0b45:
        r1 = NUM; // 0x7f0d013a float:1.8742752E38 double:1.0531299327E-314;
        r2 = "AttachDocument";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0b4f:
        return r8;
    L_0x0b50:
        r1 = NUM; // 0x7f0d0143 float:1.874277E38 double:1.053129937E-314;
        r2 = "AttachLocation";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0b5a:
        if (r19 == 0) goto L_0x0b5e;
    L_0x0b5c:
        r19[r2] = r2;
    L_0x0b5e:
        r1 = NUM; // 0x7f0d05a5 float:1.8745046E38 double:1.0531304915E-314;
        r2 = "Message";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
    L_0x0b68:
        r1 = NUM; // 0x7f0d0ab3 float:1.874767E38 double:1.053131131E-314;
        r2 = "YouHaveNewMessage";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:68:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0116 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0116 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0117  */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r18, boolean r19, boolean[] r20, boolean[] r21) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r18;
        r2 = 0;
        r3 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r2);
        if (r3 != 0) goto L_0x118b;
    L_0x000b:
        r3 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r3 == 0) goto L_0x0011;
    L_0x000f:
        goto L_0x118b;
    L_0x0011:
        r3 = r1.messageOwner;
        r4 = r3.dialog_id;
        r3 = r3.to_id;
        r6 = r3.chat_id;
        if (r6 == 0) goto L_0x001c;
    L_0x001b:
        goto L_0x001e;
    L_0x001c:
        r6 = r3.channel_id;
    L_0x001e:
        r3 = r1.messageOwner;
        r3 = r3.to_id;
        r3 = r3.user_id;
        r7 = 1;
        if (r21 == 0) goto L_0x0029;
    L_0x0027:
        r21[r2] = r7;
    L_0x0029:
        r8 = r18.isFcmMessage();
        r9 = 2;
        if (r8 == 0) goto L_0x00b3;
    L_0x0030:
        if (r6 != 0) goto L_0x0056;
    L_0x0032:
        if (r3 == 0) goto L_0x0056;
    L_0x0034:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3);
        r4 = "EnablePreviewAll";
        r3 = r3.getBoolean(r4, r7);
        if (r3 != 0) goto L_0x00ac;
    L_0x0042:
        if (r21 == 0) goto L_0x0046;
    L_0x0044:
        r21[r2] = r2;
    L_0x0046:
        r3 = NUM; // 0x7f0d066b float:1.8745447E38 double:1.0531305893E-314;
        r4 = new java.lang.Object[r7];
        r1 = r1.localName;
        r4[r2] = r1;
        r1 = "NotificationMessageNoText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        return r1;
    L_0x0056:
        if (r6 == 0) goto L_0x00ac;
    L_0x0058:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3);
        r4 = r1.localChannel;
        if (r4 != 0) goto L_0x006a;
    L_0x0062:
        r4 = "EnablePreviewGroup";
        r4 = r3.getBoolean(r4, r7);
        if (r4 == 0) goto L_0x0076;
    L_0x006a:
        r4 = r1.localChannel;
        if (r4 == 0) goto L_0x00ac;
    L_0x006e:
        r4 = "EnablePreviewChannel";
        r3 = r3.getBoolean(r4, r7);
        if (r3 != 0) goto L_0x00ac;
    L_0x0076:
        if (r21 == 0) goto L_0x007a;
    L_0x0078:
        r21[r2] = r2;
    L_0x007a:
        r3 = r18.isMegagroup();
        if (r3 != 0) goto L_0x0098;
    L_0x0080:
        r3 = r1.messageOwner;
        r3 = r3.to_id;
        r3 = r3.channel_id;
        if (r3 == 0) goto L_0x0098;
    L_0x0088:
        r3 = NUM; // 0x7f0d023d float:1.8743277E38 double:1.0531300606E-314;
        r4 = new java.lang.Object[r7];
        r1 = r1.localName;
        r4[r2] = r1;
        r1 = "ChannelMessageNoText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        return r1;
    L_0x0098:
        r3 = NUM; // 0x7f0d065f float:1.8745423E38 double:1.0531305834E-314;
        r4 = new java.lang.Object[r9];
        r5 = r1.localUserName;
        r4[r2] = r5;
        r1 = r1.localName;
        r4[r7] = r1;
        r1 = "NotificationMessageGroupNoText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        return r1;
    L_0x00ac:
        r20[r2] = r7;
        r1 = r1.messageText;
        r1 = (java.lang.String) r1;
        return r1;
    L_0x00b3:
        if (r3 != 0) goto L_0x00c9;
    L_0x00b5:
        r3 = r18.isFromUser();
        if (r3 != 0) goto L_0x00c4;
    L_0x00bb:
        r3 = r18.getId();
        if (r3 >= 0) goto L_0x00c2;
    L_0x00c1:
        goto L_0x00c4;
    L_0x00c2:
        r3 = -r6;
        goto L_0x00d9;
    L_0x00c4:
        r3 = r1.messageOwner;
        r3 = r3.from_id;
        goto L_0x00d9;
    L_0x00c9:
        r8 = r0.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.getClientUserId();
        if (r3 != r8) goto L_0x00d9;
    L_0x00d5:
        r3 = r1.messageOwner;
        r3 = r3.from_id;
    L_0x00d9:
        r10 = 0;
        r8 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r8 != 0) goto L_0x00e7;
    L_0x00df:
        if (r6 == 0) goto L_0x00e4;
    L_0x00e1:
        r4 = -r6;
        r4 = (long) r4;
        goto L_0x00e7;
    L_0x00e4:
        if (r3 == 0) goto L_0x00e7;
    L_0x00e6:
        r4 = (long) r3;
    L_0x00e7:
        r8 = 0;
        if (r3 <= 0) goto L_0x00ff;
    L_0x00ea:
        r10 = r0.currentAccount;
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);
        r11 = java.lang.Integer.valueOf(r3);
        r10 = r10.getUser(r11);
        if (r10 == 0) goto L_0x0113;
    L_0x00fa:
        r10 = org.telegram.messenger.UserObject.getUserName(r10);
        goto L_0x0114;
    L_0x00ff:
        r10 = r0.currentAccount;
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);
        r11 = -r3;
        r11 = java.lang.Integer.valueOf(r11);
        r10 = r10.getChat(r11);
        if (r10 == 0) goto L_0x0113;
    L_0x0110:
        r10 = r10.title;
        goto L_0x0114;
    L_0x0113:
        r10 = r8;
    L_0x0114:
        if (r10 != 0) goto L_0x0117;
    L_0x0116:
        return r8;
    L_0x0117:
        if (r6 == 0) goto L_0x012a;
    L_0x0119:
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);
        r12 = java.lang.Integer.valueOf(r6);
        r11 = r11.getChat(r12);
        if (r11 != 0) goto L_0x012b;
    L_0x0129:
        return r8;
    L_0x012a:
        r11 = r8;
    L_0x012b:
        r5 = (int) r4;
        if (r5 != 0) goto L_0x0139;
    L_0x012e:
        r1 = NUM; // 0x7f0d0ab3 float:1.874767E38 double:1.053131131E-314;
        r2 = "YouHaveNewMessage";
        r8 = org.telegram.messenger.LocaleController.getString(r2, r1);
        goto L_0x118a;
    L_0x0139:
        r4 = "🎬 ";
        r5 = "📎 ";
        r12 = "📹 ";
        r13 = "🖼 ";
        r14 = NUM; // 0x7f0d0673 float:1.8745463E38 double:1.053130593E-314;
        r15 = "NotificationMessageText";
        r8 = 3;
        if (r6 != 0) goto L_0x04b6;
    L_0x014d:
        if (r3 == 0) goto L_0x04b6;
    L_0x014f:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3);
        r6 = "EnablePreviewAll";
        r3 = r3.getBoolean(r6, r7);
        if (r3 == 0) goto L_0x04a3;
    L_0x015d:
        r3 = r1.messageOwner;
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r6 == 0) goto L_0x0221;
    L_0x0163:
        r3 = r3.action;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
        if (r4 != 0) goto L_0x0212;
    L_0x0169:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
        if (r4 == 0) goto L_0x016f;
    L_0x016d:
        goto L_0x0212;
    L_0x016f:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
        if (r4 == 0) goto L_0x0182;
    L_0x0173:
        r1 = NUM; // 0x7f0d063e float:1.8745356E38 double:1.053130567E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationContactNewPhoto";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0182:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
        if (r4 == 0) goto L_0x01e3;
    L_0x0186:
        r3 = NUM; // 0x7f0d0b05 float:1.8747836E38 double:1.0531311713E-314;
        r4 = new java.lang.Object[r9];
        r5 = org.telegram.messenger.LocaleController.getInstance();
        r5 = r5.formatterYear;
        r6 = r1.messageOwner;
        r6 = r6.date;
        r10 = (long) r6;
        r12 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r10 = r10 * r12;
        r5 = r5.format(r10);
        r4[r2] = r5;
        r5 = org.telegram.messenger.LocaleController.getInstance();
        r5 = r5.formatterDay;
        r6 = r1.messageOwner;
        r6 = r6.date;
        r10 = (long) r6;
        r10 = r10 * r12;
        r5 = r5.format(r10);
        r4[r7] = r5;
        r5 = "formatDateAtTime";
        r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4);
        r4 = NUM; // 0x7f0d0676 float:1.874547E38 double:1.0531305947E-314;
        r5 = 4;
        r5 = new java.lang.Object[r5];
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.getCurrentUser();
        r6 = r6.first_name;
        r5[r2] = r6;
        r5[r7] = r3;
        r1 = r1.messageOwner;
        r1 = r1.action;
        r2 = r1.title;
        r5[r9] = r2;
        r1 = r1.address;
        r5[r8] = r1;
        r1 = "NotificationUnrecognizedDevice";
        r8 = org.telegram.messenger.LocaleController.formatString(r1, r4, r5);
        goto L_0x118a;
    L_0x01e3:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r2 != 0) goto L_0x020a;
    L_0x01e7:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
        if (r2 == 0) goto L_0x01ec;
    L_0x01eb:
        goto L_0x020a;
    L_0x01ec:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r2 == 0) goto L_0x0207;
    L_0x01f0:
        r2 = r3.reason;
        r1 = r18.isOut();
        if (r1 != 0) goto L_0x0207;
    L_0x01f8:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        if (r1 == 0) goto L_0x0207;
    L_0x01fc:
        r1 = NUM; // 0x7f0d01db float:1.8743078E38 double:1.053130012E-314;
        r2 = "CallMessageIncomingMissed";
        r8 = org.telegram.messenger.LocaleController.getString(r2, r1);
        goto L_0x118a;
    L_0x0207:
        r8 = 0;
        goto L_0x118a;
    L_0x020a:
        r1 = r1.messageText;
        r8 = r1.toString();
        goto L_0x118a;
    L_0x0212:
        r1 = NUM; // 0x7f0d063d float:1.8745354E38 double:1.0531305666E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationContactJoined";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0221:
        r3 = r18.isMediaEmpty();
        if (r3 == 0) goto L_0x0263;
    L_0x0227:
        if (r19 != 0) goto L_0x0254;
    L_0x0229:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0245;
    L_0x0233:
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3[r7] = r1;
        r8 = org.telegram.messenger.LocaleController.formatString(r15, r14, r3);
        r20[r2] = r7;
        goto L_0x118a;
    L_0x0245:
        r1 = NUM; // 0x7f0d066b float:1.8745447E38 double:1.0531305893E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageNoText";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0254:
        r1 = NUM; // 0x7f0d066b float:1.8745447E38 double:1.0531305893E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageNoText";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0263:
        r3 = r1.messageOwner;
        r6 = r3.media;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r6 == 0) goto L_0x02c2;
    L_0x026b:
        if (r19 != 0) goto L_0x029c;
    L_0x026d:
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r4 < r5) goto L_0x029c;
    L_0x0273:
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x029c;
    L_0x027b:
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r13);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r4.append(r1);
        r1 = r4.toString();
        r3[r7] = r1;
        r8 = org.telegram.messenger.LocaleController.formatString(r15, r14, r3);
        r20[r2] = r7;
        goto L_0x118a;
    L_0x029c:
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.ttl_seconds;
        if (r1 == 0) goto L_0x02b3;
    L_0x02a4:
        r1 = NUM; // 0x7f0d066f float:1.8745455E38 double:1.0531305913E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageSDPhoto";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x02b3:
        r1 = NUM; // 0x7f0d066c float:1.874545E38 double:1.05313059E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessagePhoto";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x02c2:
        r3 = r18.isVideo();
        if (r3 == 0) goto L_0x0321;
    L_0x02c8:
        if (r19 != 0) goto L_0x02fb;
    L_0x02ca:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r3 < r4) goto L_0x02fb;
    L_0x02d0:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x02fb;
    L_0x02da:
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r12);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r4.append(r1);
        r1 = r4.toString();
        r3[r7] = r1;
        r8 = org.telegram.messenger.LocaleController.formatString(r15, r14, r3);
        r20[r2] = r7;
        goto L_0x118a;
    L_0x02fb:
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.ttl_seconds;
        if (r1 == 0) goto L_0x0312;
    L_0x0303:
        r1 = NUM; // 0x7f0d0670 float:1.8745457E38 double:1.053130592E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageSDVideo";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0312:
        r1 = NUM; // 0x7f0d0674 float:1.8745465E38 double:1.0531305937E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageVideo";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0321:
        r3 = r18.isGame();
        if (r3 == 0) goto L_0x0340;
    L_0x0327:
        r3 = NUM; // 0x7f0d0652 float:1.8745396E38 double:1.053130577E-314;
        r4 = new java.lang.Object[r9];
        r4[r2] = r10;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.game;
        r1 = r1.title;
        r4[r7] = r1;
        r1 = "NotificationMessageGame";
        r8 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x118a;
    L_0x0340:
        r3 = r18.isVoice();
        if (r3 == 0) goto L_0x0355;
    L_0x0346:
        r1 = NUM; // 0x7f0d064d float:1.8745386E38 double:1.0531305745E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageAudio";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0355:
        r3 = r18.isRoundVideo();
        if (r3 == 0) goto L_0x036a;
    L_0x035b:
        r1 = NUM; // 0x7f0d066e float:1.8745453E38 double:1.053130591E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageRound";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x036a:
        r3 = r18.isMusic();
        if (r3 == 0) goto L_0x037f;
    L_0x0370:
        r1 = NUM; // 0x7f0d066a float:1.8745445E38 double:1.053130589E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageMusic";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x037f:
        r3 = r1.messageOwner;
        r3 = r3.media;
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r6 == 0) goto L_0x03a2;
    L_0x0387:
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r3;
        r1 = NUM; // 0x7f0d064e float:1.8745388E38 double:1.053130575E-314;
        r4 = new java.lang.Object[r9];
        r4[r2] = r10;
        r2 = r3.first_name;
        r3 = r3.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r3);
        r4[r7] = r2;
        r2 = "NotificationMessageContact2";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r4);
        goto L_0x118a;
    L_0x03a2:
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r6 == 0) goto L_0x03bd;
    L_0x03a6:
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3;
        r1 = NUM; // 0x7f0d066d float:1.8745451E38 double:1.0531305903E-314;
        r4 = new java.lang.Object[r9];
        r4[r2] = r10;
        r2 = r3.poll;
        r2 = r2.question;
        r4[r7] = r2;
        r2 = "NotificationMessagePoll2";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r4);
        goto L_0x118a;
    L_0x03bd:
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r6 != 0) goto L_0x0494;
    L_0x03c1:
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r6 == 0) goto L_0x03c7;
    L_0x03c5:
        goto L_0x0494;
    L_0x03c7:
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r6 == 0) goto L_0x03da;
    L_0x03cb:
        r1 = NUM; // 0x7f0d0668 float:1.8745441E38 double:1.053130588E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageLiveLocation";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x03da:
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r3 == 0) goto L_0x0207;
    L_0x03de:
        r3 = r18.isSticker();
        if (r3 == 0) goto L_0x040a;
    L_0x03e4:
        r1 = r18.getStickerEmoji();
        if (r1 == 0) goto L_0x03fa;
    L_0x03ea:
        r3 = NUM; // 0x7f0d0672 float:1.8745461E38 double:1.0531305928E-314;
        r4 = new java.lang.Object[r9];
        r4[r2] = r10;
        r4[r7] = r1;
        r1 = "NotificationMessageStickerEmoji";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x03fa:
        r1 = NUM; // 0x7f0d0671 float:1.874546E38 double:1.0531305923E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageSticker";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
    L_0x0407:
        r8 = r1;
        goto L_0x118a;
    L_0x040a:
        r3 = r18.isGif();
        if (r3 == 0) goto L_0x0452;
    L_0x0410:
        if (r19 != 0) goto L_0x0443;
    L_0x0412:
        r3 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r3 < r5) goto L_0x0443;
    L_0x0418:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0443;
    L_0x0422:
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r4);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r5.append(r1);
        r1 = r5.toString();
        r3[r7] = r1;
        r8 = org.telegram.messenger.LocaleController.formatString(r15, r14, r3);
        r20[r2] = r7;
        goto L_0x118a;
    L_0x0443:
        r1 = NUM; // 0x7f0d0654 float:1.87454E38 double:1.053130578E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageGif";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0452:
        if (r19 != 0) goto L_0x0485;
    L_0x0454:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r3 < r4) goto L_0x0485;
    L_0x045a:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0485;
    L_0x0464:
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r5);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r4.append(r1);
        r1 = r4.toString();
        r3[r7] = r1;
        r8 = org.telegram.messenger.LocaleController.formatString(r15, r14, r3);
        r20[r2] = r7;
        goto L_0x118a;
    L_0x0485:
        r1 = NUM; // 0x7f0d064f float:1.874539E38 double:1.0531305755E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageDocument";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0494:
        r1 = NUM; // 0x7f0d0669 float:1.8745443E38 double:1.0531305883E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageMap";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x04a3:
        if (r21 == 0) goto L_0x04a7;
    L_0x04a5:
        r21[r2] = r2;
    L_0x04a7:
        r1 = NUM; // 0x7f0d066b float:1.8745447E38 double:1.0531305893E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "NotificationMessageNoText";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x04b6:
        if (r6 == 0) goto L_0x0207;
    L_0x04b8:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getNotificationsSettings(r6);
        r16 = org.telegram.messenger.ChatObject.isChannel(r11);
        if (r16 == 0) goto L_0x04ca;
    L_0x04c4:
        r14 = r11.megagroup;
        if (r14 != 0) goto L_0x04ca;
    L_0x04c8:
        r14 = 1;
        goto L_0x04cb;
    L_0x04ca:
        r14 = 0;
    L_0x04cb:
        if (r14 != 0) goto L_0x04d5;
    L_0x04cd:
        r8 = "EnablePreviewGroup";
        r8 = r6.getBoolean(r8, r7);
        if (r8 != 0) goto L_0x04df;
    L_0x04d5:
        if (r14 == 0) goto L_0x115d;
    L_0x04d7:
        r8 = "EnablePreviewChannel";
        r6 = r6.getBoolean(r8, r7);
        if (r6 == 0) goto L_0x115d;
    L_0x04df:
        r6 = r1.messageOwner;
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageService;
        if (r8 == 0) goto L_0x0CLASSNAME;
    L_0x04e5:
        r6 = r6.action;
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r8 == 0) goto L_0x05fa;
    L_0x04eb:
        r4 = r6.user_id;
        if (r4 != 0) goto L_0x0507;
    L_0x04ef:
        r5 = r6.users;
        r5 = r5.size();
        if (r5 != r7) goto L_0x0507;
    L_0x04f7:
        r4 = r1.messageOwner;
        r4 = r4.action;
        r4 = r4.users;
        r4 = r4.get(r2);
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
    L_0x0507:
        if (r4 == 0) goto L_0x05a1;
    L_0x0509:
        r1 = r1.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x0528;
    L_0x0511:
        r1 = r11.megagroup;
        if (r1 != 0) goto L_0x0528;
    L_0x0515:
        r1 = NUM; // 0x7f0d020d float:1.874318E38 double:1.053130037E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "ChannelAddedByNotification";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0528:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getClientUserId();
        if (r4 != r1) goto L_0x0547;
    L_0x0534:
        r1 = NUM; // 0x7f0d064a float:1.874538E38 double:1.053130573E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationInvitedToGroup";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0547:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r4 = java.lang.Integer.valueOf(r4);
        r1 = r1.getUser(r4);
        if (r1 != 0) goto L_0x0559;
    L_0x0557:
        r4 = 0;
        return r4;
    L_0x0559:
        r4 = r1.id;
        if (r3 != r4) goto L_0x0587;
    L_0x055d:
        r1 = r11.megagroup;
        if (r1 == 0) goto L_0x0574;
    L_0x0561:
        r1 = NUM; // 0x7f0d0643 float:1.8745366E38 double:1.0531305695E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationGroupAddSelfMega";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0574:
        r1 = NUM; // 0x7f0d0642 float:1.8745364E38 double:1.053130569E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationGroupAddSelf";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0587:
        r3 = NUM; // 0x7f0d0641 float:1.8745362E38 double:1.0531305685E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r2 = r11.title;
        r4[r7] = r2;
        r1 = org.telegram.messenger.UserObject.getUserName(r1);
        r4[r9] = r1;
        r1 = "NotificationGroupAddMember";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x05a1:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = 0;
    L_0x05a7:
        r5 = r1.messageOwner;
        r5 = r5.action;
        r5 = r5.users;
        r5 = r5.size();
        if (r4 >= r5) goto L_0x05e0;
    L_0x05b3:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r6 = r1.messageOwner;
        r6 = r6.action;
        r6 = r6.users;
        r6 = r6.get(r4);
        r6 = (java.lang.Integer) r6;
        r5 = r5.getUser(r6);
        if (r5 == 0) goto L_0x05dd;
    L_0x05cb:
        r5 = org.telegram.messenger.UserObject.getUserName(r5);
        r6 = r3.length();
        if (r6 == 0) goto L_0x05da;
    L_0x05d5:
        r6 = ", ";
        r3.append(r6);
    L_0x05da:
        r3.append(r5);
    L_0x05dd:
        r4 = r4 + 1;
        goto L_0x05a7;
    L_0x05e0:
        r1 = NUM; // 0x7f0d0641 float:1.8745362E38 double:1.0531305685E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r2 = r11.title;
        r4[r7] = r2;
        r2 = r3.toString();
        r4[r9] = r2;
        r2 = "NotificationGroupAddMember";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r4);
        goto L_0x0407;
    L_0x05fa:
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
        if (r8 == 0) goto L_0x0611;
    L_0x05fe:
        r1 = NUM; // 0x7f0d064b float:1.8745382E38 double:1.0531305735E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationInvitedToGroupByLink";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0611:
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
        if (r8 == 0) goto L_0x0628;
    L_0x0615:
        r1 = NUM; // 0x7f0d063f float:1.8745358E38 double:1.0531305676E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r6.title;
        r3[r7] = r2;
        r2 = "NotificationEditedGroupName";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0628:
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
        if (r8 != 0) goto L_0x0CLASSNAME;
    L_0x062c:
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
        if (r8 == 0) goto L_0x0632;
    L_0x0630:
        goto L_0x0CLASSNAME;
    L_0x0632:
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r8 == 0) goto L_0x06a4;
    L_0x0636:
        r4 = r6.user_id;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r5 = r5.getClientUserId();
        if (r4 != r5) goto L_0x0657;
    L_0x0644:
        r1 = NUM; // 0x7f0d0648 float:1.8745376E38 double:1.053130572E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationGroupKickYou";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0657:
        r4 = r1.messageOwner;
        r4 = r4.action;
        r4 = r4.user_id;
        if (r4 != r3) goto L_0x0672;
    L_0x065f:
        r1 = NUM; // 0x7f0d0649 float:1.8745378E38 double:1.0531305725E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationGroupLeftMember";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0672:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r1 = r1.messageOwner;
        r1 = r1.action;
        r1 = r1.user_id;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r3.getUser(r1);
        if (r1 != 0) goto L_0x068a;
    L_0x0688:
        r8 = 0;
        return r8;
    L_0x068a:
        r3 = NUM; // 0x7f0d0647 float:1.8745374E38 double:1.0531305715E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r2 = r11.title;
        r4[r7] = r2;
        r1 = org.telegram.messenger.UserObject.getUserName(r1);
        r4[r9] = r1;
        r1 = "NotificationGroupKickMember";
        r8 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x118a;
    L_0x06a4:
        r8 = 0;
        r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
        if (r3 == 0) goto L_0x06b1;
    L_0x06a9:
        r1 = r1.messageText;
        r8 = r1.toString();
        goto L_0x118a;
    L_0x06b1:
        r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r3 == 0) goto L_0x06bd;
    L_0x06b5:
        r1 = r1.messageText;
        r8 = r1.toString();
        goto L_0x118a;
    L_0x06bd:
        r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r3 == 0) goto L_0x06d2;
    L_0x06c1:
        r1 = NUM; // 0x7f0d007d float:1.8742368E38 double:1.0531298393E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "ActionMigrateFromGroupNotify";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x06d2:
        r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
        if (r3 == 0) goto L_0x06e7;
    L_0x06d6:
        r1 = NUM; // 0x7f0d007d float:1.8742368E38 double:1.0531298393E-314;
        r3 = new java.lang.Object[r7];
        r4 = r6.title;
        r3[r2] = r4;
        r2 = "ActionMigrateFromGroupNotify";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x06e7:
        r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
        if (r3 == 0) goto L_0x06f3;
    L_0x06eb:
        r1 = r1.messageText;
        r8 = r1.toString();
        goto L_0x118a;
    L_0x06f3:
        r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r3 == 0) goto L_0x0c4d;
    L_0x06f7:
        if (r11 == 0) goto L_0x09c5;
    L_0x06f9:
        r3 = org.telegram.messenger.ChatObject.isChannel(r11);
        if (r3 == 0) goto L_0x0703;
    L_0x06ff:
        r3 = r11.megagroup;
        if (r3 == 0) goto L_0x09c5;
    L_0x0703:
        r3 = r1.replyMessageObject;
        if (r3 != 0) goto L_0x071a;
    L_0x0707:
        r1 = NUM; // 0x7f0d062b float:1.8745317E38 double:1.0531305577E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedNoText";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x071a:
        r6 = r3.isMusic();
        if (r6 == 0) goto L_0x0733;
    L_0x0720:
        r1 = NUM; // 0x7f0d0629 float:1.8745313E38 double:1.0531305567E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedMusic";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0733:
        r6 = r3.isVideo();
        if (r6 == 0) goto L_0x0785;
    L_0x0739:
        r1 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r1 < r4) goto L_0x0772;
    L_0x073f:
        r1 = r3.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0772;
    L_0x0749:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r12);
        r3 = r3.messageOwner;
        r3 = r3.message;
        r1.append(r3);
        r1 = r1.toString();
        r3 = NUM; // 0x7f0d0637 float:1.8745342E38 double:1.0531305636E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r4[r7] = r1;
        r1 = r11.title;
        r4[r9] = r1;
        r1 = "NotificationActionPinnedText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x0772:
        r1 = NUM; // 0x7f0d0639 float:1.8745346E38 double:1.0531305646E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedVideo";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0785:
        r6 = r3.isGif();
        if (r6 == 0) goto L_0x07d7;
    L_0x078b:
        r1 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r1 < r5) goto L_0x07c4;
    L_0x0791:
        r1 = r3.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x07c4;
    L_0x079b:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r4);
        r3 = r3.messageOwner;
        r3 = r3.message;
        r1.append(r3);
        r1 = r1.toString();
        r3 = NUM; // 0x7f0d0637 float:1.8745342E38 double:1.0531305636E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r4[r7] = r1;
        r1 = r11.title;
        r4[r9] = r1;
        r1 = "NotificationActionPinnedText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x07c4:
        r1 = NUM; // 0x7f0d0625 float:1.8745305E38 double:1.0531305547E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedGif";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x07d7:
        r4 = r3.isVoice();
        if (r4 == 0) goto L_0x07f0;
    L_0x07dd:
        r1 = NUM; // 0x7f0d063b float:1.874535E38 double:1.0531305656E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedVoice";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x07f0:
        r4 = r3.isRoundVideo();
        if (r4 == 0) goto L_0x0809;
    L_0x07f6:
        r1 = NUM; // 0x7f0d0631 float:1.874533E38 double:1.0531305606E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedRound";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0809:
        r4 = r3.isSticker();
        if (r4 == 0) goto L_0x083e;
    L_0x080f:
        r1 = r3.getStickerEmoji();
        if (r1 == 0) goto L_0x082b;
    L_0x0815:
        r3 = NUM; // 0x7f0d0635 float:1.8745338E38 double:1.0531305626E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r2 = r11.title;
        r4[r7] = r2;
        r4[r9] = r1;
        r1 = "NotificationActionPinnedStickerEmoji";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x082b:
        r1 = NUM; // 0x7f0d0633 float:1.8745334E38 double:1.0531305616E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedSticker";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x083e:
        r4 = r3.messageOwner;
        r6 = r4.media;
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r8 == 0) goto L_0x0890;
    L_0x0846:
        r1 = android.os.Build.VERSION.SDK_INT;
        r6 = 19;
        if (r1 < r6) goto L_0x087d;
    L_0x084c:
        r1 = r4.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x087d;
    L_0x0854:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r5);
        r3 = r3.messageOwner;
        r3 = r3.message;
        r1.append(r3);
        r1 = r1.toString();
        r3 = NUM; // 0x7f0d0637 float:1.8745342E38 double:1.0531305636E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r4[r7] = r1;
        r1 = r11.title;
        r4[r9] = r1;
        r1 = "NotificationActionPinnedText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x087d:
        r1 = NUM; // 0x7f0d061b float:1.8745285E38 double:1.05313055E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedFile";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0890:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r5 != 0) goto L_0x09b2;
    L_0x0894:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r5 == 0) goto L_0x089a;
    L_0x0898:
        goto L_0x09b2;
    L_0x089a:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r5 == 0) goto L_0x08b1;
    L_0x089e:
        r1 = NUM; // 0x7f0d0623 float:1.8745301E38 double:1.0531305537E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedGeoLive";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x08b1:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r5 == 0) goto L_0x08d9;
    L_0x08b5:
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r1;
        r3 = NUM; // 0x7f0d0619 float:1.874528E38 double:1.053130549E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r2 = r11.title;
        r4[r7] = r2;
        r2 = r1.first_name;
        r1 = r1.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r2, r1);
        r4[r9] = r1;
        r1 = "NotificationActionPinnedContact2";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x08d9:
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r1 == 0) goto L_0x08f9;
    L_0x08dd:
        r6 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r6;
        r1 = NUM; // 0x7f0d062f float:1.8745325E38 double:1.0531305597E-314;
        r3 = 3;
        r3 = new java.lang.Object[r3];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = r6.poll;
        r2 = r2.question;
        r3[r9] = r2;
        r2 = "NotificationActionPinnedPoll2";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x08f9:
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r1 == 0) goto L_0x0947;
    L_0x08fd:
        r1 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r1 < r5) goto L_0x0934;
    L_0x0903:
        r1 = r4.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0934;
    L_0x090b:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r13);
        r3 = r3.messageOwner;
        r3 = r3.message;
        r1.append(r3);
        r1 = r1.toString();
        r3 = NUM; // 0x7f0d0637 float:1.8745342E38 double:1.0531305636E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r4[r7] = r1;
        r1 = r11.title;
        r4[r9] = r1;
        r1 = "NotificationActionPinnedText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x0934:
        r1 = NUM; // 0x7f0d062d float:1.8745321E38 double:1.0531305587E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedPhoto";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0947:
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 == 0) goto L_0x095e;
    L_0x094b:
        r1 = NUM; // 0x7f0d061d float:1.8745289E38 double:1.053130551E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedGame";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x095e:
        r1 = r3.messageText;
        if (r1 == 0) goto L_0x099f;
    L_0x0962:
        r1 = r1.length();
        if (r1 <= 0) goto L_0x099f;
    L_0x0968:
        r1 = r3.messageText;
        r3 = r1.length();
        r4 = 20;
        if (r3 <= r4) goto L_0x0989;
    L_0x0972:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = 20;
        r1 = r1.subSequence(r2, r4);
        r3.append(r1);
        r1 = "...";
        r3.append(r1);
        r1 = r3.toString();
    L_0x0989:
        r3 = NUM; // 0x7f0d0637 float:1.8745342E38 double:1.0531305636E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r4[r7] = r1;
        r1 = r11.title;
        r4[r9] = r1;
        r1 = "NotificationActionPinnedText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x099f:
        r1 = NUM; // 0x7f0d062b float:1.8745317E38 double:1.0531305577E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedNoText";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x09b2:
        r1 = NUM; // 0x7f0d0621 float:1.8745297E38 double:1.0531305527E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedGeo";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x09c5:
        r3 = r1.replyMessageObject;
        if (r3 != 0) goto L_0x09da;
    L_0x09c9:
        r1 = NUM; // 0x7f0d062c float:1.874532E38 double:1.053130558E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedNoTextChannel";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x09da:
        r6 = r3.isMusic();
        if (r6 == 0) goto L_0x09f1;
    L_0x09e0:
        r1 = NUM; // 0x7f0d062a float:1.8745315E38 double:1.053130557E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedMusicChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x09f1:
        r6 = r3.isVideo();
        r8 = "NotificationActionPinnedTextChannel";
        if (r6 == 0) goto L_0x0a3e;
    L_0x09f9:
        r1 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r1 < r4) goto L_0x0a2d;
    L_0x09ff:
        r1 = r3.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0a2d;
    L_0x0a09:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r12);
        r3 = r3.messageOwner;
        r3 = r3.message;
        r1.append(r3);
        r1 = r1.toString();
        r3 = NUM; // 0x7f0d0638 float:1.8745344E38 double:1.053130564E-314;
        r4 = new java.lang.Object[r9];
        r5 = r11.title;
        r4[r2] = r5;
        r4[r7] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4);
        goto L_0x0407;
    L_0x0a2d:
        r1 = NUM; // 0x7f0d063a float:1.8745348E38 double:1.053130565E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedVideoChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0a3e:
        r6 = r3.isGif();
        if (r6 == 0) goto L_0x0a89;
    L_0x0a44:
        r1 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r1 < r5) goto L_0x0a78;
    L_0x0a4a:
        r1 = r3.messageOwner;
        r1 = r1.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0a78;
    L_0x0a54:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r4);
        r3 = r3.messageOwner;
        r3 = r3.message;
        r1.append(r3);
        r1 = r1.toString();
        r3 = NUM; // 0x7f0d0638 float:1.8745344E38 double:1.053130564E-314;
        r4 = new java.lang.Object[r9];
        r5 = r11.title;
        r4[r2] = r5;
        r4[r7] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4);
        goto L_0x0407;
    L_0x0a78:
        r1 = NUM; // 0x7f0d0626 float:1.8745307E38 double:1.053130555E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedGifChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0a89:
        r4 = r3.isVoice();
        if (r4 == 0) goto L_0x0aa0;
    L_0x0a8f:
        r1 = NUM; // 0x7f0d063c float:1.8745352E38 double:1.053130566E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedVoiceChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0aa0:
        r4 = r3.isRoundVideo();
        if (r4 == 0) goto L_0x0ab7;
    L_0x0aa6:
        r1 = NUM; // 0x7f0d0632 float:1.8745332E38 double:1.053130561E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedRoundChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0ab7:
        r4 = r3.isSticker();
        if (r4 == 0) goto L_0x0ae7;
    L_0x0abd:
        r1 = r3.getStickerEmoji();
        if (r1 == 0) goto L_0x0ad6;
    L_0x0ac3:
        r3 = NUM; // 0x7f0d0636 float:1.874534E38 double:1.053130563E-314;
        r4 = new java.lang.Object[r9];
        r5 = r11.title;
        r4[r2] = r5;
        r4[r7] = r1;
        r1 = "NotificationActionPinnedStickerEmojiChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x0ad6:
        r1 = NUM; // 0x7f0d0634 float:1.8745336E38 double:1.053130562E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedStickerChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0ae7:
        r4 = r3.messageOwner;
        r6 = r4.media;
        r10 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r10 == 0) goto L_0x0b32;
    L_0x0aef:
        r1 = android.os.Build.VERSION.SDK_INT;
        r6 = 19;
        if (r1 < r6) goto L_0x0b21;
    L_0x0af5:
        r1 = r4.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0b21;
    L_0x0afd:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r5);
        r3 = r3.messageOwner;
        r3 = r3.message;
        r1.append(r3);
        r1 = r1.toString();
        r3 = NUM; // 0x7f0d0638 float:1.8745344E38 double:1.053130564E-314;
        r4 = new java.lang.Object[r9];
        r5 = r11.title;
        r4[r2] = r5;
        r4[r7] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4);
        goto L_0x0407;
    L_0x0b21:
        r1 = NUM; // 0x7f0d061c float:1.8745287E38 double:1.0531305503E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedFileChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0b32:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r5 != 0) goto L_0x0c3c;
    L_0x0b36:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r5 == 0) goto L_0x0b3c;
    L_0x0b3a:
        goto L_0x0c3c;
    L_0x0b3c:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r5 == 0) goto L_0x0b51;
    L_0x0b40:
        r1 = NUM; // 0x7f0d0624 float:1.8745303E38 double:1.053130554E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedGeoLiveChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0b51:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r5 == 0) goto L_0x0b76;
    L_0x0b55:
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r1;
        r3 = NUM; // 0x7f0d061a float:1.8745283E38 double:1.0531305493E-314;
        r4 = new java.lang.Object[r9];
        r5 = r11.title;
        r4[r2] = r5;
        r2 = r1.first_name;
        r1 = r1.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r2, r1);
        r4[r7] = r1;
        r1 = "NotificationActionPinnedContactChannel2";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x0b76:
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r1 == 0) goto L_0x0b93;
    L_0x0b7a:
        r6 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r6;
        r1 = NUM; // 0x7f0d0630 float:1.8745327E38 double:1.05313056E-314;
        r3 = new java.lang.Object[r9];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = r6.poll;
        r2 = r2.question;
        r3[r7] = r2;
        r2 = "NotificationActionPinnedPollChannel2";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0b93:
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r1 == 0) goto L_0x0bda;
    L_0x0b97:
        r1 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r1 < r5) goto L_0x0bc9;
    L_0x0b9d:
        r1 = r4.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0bc9;
    L_0x0ba5:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r13);
        r3 = r3.messageOwner;
        r3 = r3.message;
        r1.append(r3);
        r1 = r1.toString();
        r3 = NUM; // 0x7f0d0638 float:1.8745344E38 double:1.053130564E-314;
        r4 = new java.lang.Object[r9];
        r5 = r11.title;
        r4[r2] = r5;
        r4[r7] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4);
        goto L_0x0407;
    L_0x0bc9:
        r1 = NUM; // 0x7f0d062e float:1.8745323E38 double:1.053130559E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedPhotoChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0bda:
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 == 0) goto L_0x0bef;
    L_0x0bde:
        r1 = NUM; // 0x7f0d061e float:1.874529E38 double:1.0531305513E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedGameChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0bef:
        r1 = r3.messageText;
        if (r1 == 0) goto L_0x0c2b;
    L_0x0bf3:
        r1 = r1.length();
        if (r1 <= 0) goto L_0x0c2b;
    L_0x0bf9:
        r1 = r3.messageText;
        r3 = r1.length();
        r4 = 20;
        if (r3 <= r4) goto L_0x0c1a;
    L_0x0CLASSNAME:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = 20;
        r1 = r1.subSequence(r2, r4);
        r3.append(r1);
        r1 = "...";
        r3.append(r1);
        r1 = r3.toString();
    L_0x0c1a:
        r3 = NUM; // 0x7f0d0638 float:1.8745344E38 double:1.053130564E-314;
        r4 = new java.lang.Object[r9];
        r5 = r11.title;
        r4[r2] = r5;
        r4[r7] = r1;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r3, r4);
        goto L_0x0407;
    L_0x0c2b:
        r1 = NUM; // 0x7f0d062c float:1.874532E38 double:1.053130558E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedNoTextChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0c3c:
        r1 = NUM; // 0x7f0d0622 float:1.87453E38 double:1.053130553E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "NotificationActionPinnedGeoChannel";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0c4d:
        r2 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r2 == 0) goto L_0x118a;
    L_0x0CLASSNAME:
        r1 = r1.messageText;
        r8 = r1.toString();
        goto L_0x118a;
    L_0x0CLASSNAME:
        r1 = r1.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r11.megagroup;
        if (r1 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = NUM; // 0x7f0d024b float:1.8743305E38 double:1.0531300676E-314;
        r3 = new java.lang.Object[r7];
        r4 = r11.title;
        r3[r2] = r4;
        r2 = "ChannelPhotoEditNotification";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0CLASSNAME:
        r1 = NUM; // 0x7f0d0640 float:1.874536E38 double:1.053130568E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationEditedGroupPhoto";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0CLASSNAME:
        r8 = 0;
        r3 = org.telegram.messenger.ChatObject.isChannel(r11);
        if (r3 == 0) goto L_0x0ecb;
    L_0x0CLASSNAME:
        r3 = r11.megagroup;
        if (r3 != 0) goto L_0x0ecb;
    L_0x0CLASSNAME:
        r3 = r18.isMediaEmpty();
        if (r3 == 0) goto L_0x0ccc;
    L_0x0c9a:
        if (r19 != 0) goto L_0x0cbd;
    L_0x0c9c:
        r3 = r1.messageOwner;
        r3 = r3.message;
        if (r3 == 0) goto L_0x0cbd;
    L_0x0ca2:
        r3 = r3.length();
        if (r3 == 0) goto L_0x0cbd;
    L_0x0ca8:
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3[r7] = r1;
        r1 = NUM; // 0x7f0d0673 float:1.8745463E38 double:1.053130593E-314;
        r8 = org.telegram.messenger.LocaleController.formatString(r15, r1, r3);
        r20[r2] = r7;
        goto L_0x118a;
    L_0x0cbd:
        r1 = NUM; // 0x7f0d023d float:1.8743277E38 double:1.0531300606E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageNoText";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0ccc:
        r3 = r1.messageOwner;
        r6 = r3.media;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r6 == 0) goto L_0x0d17;
    L_0x0cd4:
        if (r19 != 0) goto L_0x0d08;
    L_0x0cd6:
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r4 < r5) goto L_0x0d08;
    L_0x0cdc:
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0d08;
    L_0x0ce4:
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r13);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r4.append(r1);
        r1 = r4.toString();
        r3[r7] = r1;
        r1 = NUM; // 0x7f0d0673 float:1.8745463E38 double:1.053130593E-314;
        r8 = org.telegram.messenger.LocaleController.formatString(r15, r1, r3);
        r20[r2] = r7;
        goto L_0x118a;
    L_0x0d08:
        r1 = NUM; // 0x7f0d023e float:1.8743279E38 double:1.053130061E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessagePhoto";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0d17:
        r3 = r18.isVideo();
        if (r3 == 0) goto L_0x0d62;
    L_0x0d1d:
        if (r19 != 0) goto L_0x0d53;
    L_0x0d1f:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r3 < r4) goto L_0x0d53;
    L_0x0d25:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0d53;
    L_0x0d2f:
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r12);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r4.append(r1);
        r1 = r4.toString();
        r3[r7] = r1;
        r1 = NUM; // 0x7f0d0673 float:1.8745463E38 double:1.053130593E-314;
        r8 = org.telegram.messenger.LocaleController.formatString(r15, r1, r3);
        r20[r2] = r7;
        goto L_0x118a;
    L_0x0d53:
        r1 = NUM; // 0x7f0d0243 float:1.874329E38 double:1.0531300636E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageVideo";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0d62:
        r3 = r18.isVoice();
        if (r3 == 0) goto L_0x0d77;
    L_0x0d68:
        r1 = NUM; // 0x7f0d0235 float:1.874326E38 double:1.0531300567E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageAudio";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0d77:
        r3 = r18.isRoundVideo();
        if (r3 == 0) goto L_0x0d8c;
    L_0x0d7d:
        r1 = NUM; // 0x7f0d0240 float:1.8743283E38 double:1.053130062E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageRound";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0d8c:
        r3 = r18.isMusic();
        if (r3 == 0) goto L_0x0da1;
    L_0x0d92:
        r1 = NUM; // 0x7f0d023c float:1.8743275E38 double:1.05313006E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageMusic";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0da1:
        r3 = r1.messageOwner;
        r3 = r3.media;
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r6 == 0) goto L_0x0dc4;
    L_0x0da9:
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r3;
        r1 = NUM; // 0x7f0d0236 float:1.8743263E38 double:1.053130057E-314;
        r4 = new java.lang.Object[r9];
        r4[r2] = r10;
        r2 = r3.first_name;
        r3 = r3.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r3);
        r4[r7] = r2;
        r2 = "ChannelMessageContact2";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r4);
        goto L_0x118a;
    L_0x0dc4:
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r6 == 0) goto L_0x0ddf;
    L_0x0dc8:
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3;
        r1 = NUM; // 0x7f0d023f float:1.874328E38 double:1.0531300616E-314;
        r4 = new java.lang.Object[r9];
        r4[r2] = r10;
        r2 = r3.poll;
        r2 = r2.question;
        r4[r7] = r2;
        r2 = "ChannelMessagePoll2";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r4);
        goto L_0x118a;
    L_0x0ddf:
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r6 != 0) goto L_0x0ebc;
    L_0x0de3:
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r6 == 0) goto L_0x0de9;
    L_0x0de7:
        goto L_0x0ebc;
    L_0x0de9:
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r6 == 0) goto L_0x0dfc;
    L_0x0ded:
        r1 = NUM; // 0x7f0d023a float:1.874327E38 double:1.053130059E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageLiveLocation";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0dfc:
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r3 == 0) goto L_0x118a;
    L_0x0e00:
        r3 = r18.isSticker();
        if (r3 == 0) goto L_0x0e2c;
    L_0x0e06:
        r1 = r18.getStickerEmoji();
        if (r1 == 0) goto L_0x0e1d;
    L_0x0e0c:
        r3 = NUM; // 0x7f0d0242 float:1.8743287E38 double:1.053130063E-314;
        r4 = new java.lang.Object[r9];
        r4[r2] = r10;
        r4[r7] = r1;
        r1 = "ChannelMessageStickerEmoji";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x0e1d:
        r1 = NUM; // 0x7f0d0241 float:1.8743285E38 double:1.0531300626E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageSticker";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x0e2c:
        r3 = r18.isGif();
        if (r3 == 0) goto L_0x0e77;
    L_0x0e32:
        if (r19 != 0) goto L_0x0e68;
    L_0x0e34:
        r3 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r3 < r5) goto L_0x0e68;
    L_0x0e3a:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0e68;
    L_0x0e44:
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r4);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r5.append(r1);
        r1 = r5.toString();
        r3[r7] = r1;
        r1 = NUM; // 0x7f0d0673 float:1.8745463E38 double:1.053130593E-314;
        r8 = org.telegram.messenger.LocaleController.formatString(r15, r1, r3);
        r20[r2] = r7;
        goto L_0x118a;
    L_0x0e68:
        r1 = NUM; // 0x7f0d0239 float:1.8743269E38 double:1.0531300587E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageGIF";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0e77:
        if (r19 != 0) goto L_0x0ead;
    L_0x0e79:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r3 < r4) goto L_0x0ead;
    L_0x0e7f:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0ead;
    L_0x0e89:
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r5);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r4.append(r1);
        r1 = r4.toString();
        r3[r7] = r1;
        r1 = NUM; // 0x7f0d0673 float:1.8745463E38 double:1.053130593E-314;
        r8 = org.telegram.messenger.LocaleController.formatString(r15, r1, r3);
        r20[r2] = r7;
        goto L_0x118a;
    L_0x0ead:
        r1 = NUM; // 0x7f0d0237 float:1.8743265E38 double:1.0531300577E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageDocument";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0ebc:
        r1 = NUM; // 0x7f0d023b float:1.8743273E38 double:1.0531300597E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageMap";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0ecb:
        r3 = r18.isMediaEmpty();
        r6 = NUM; // 0x7f0d0665 float:1.8745435E38 double:1.0531305863E-314;
        r14 = "NotificationMessageGroupText";
        if (r3 == 0) goto L_0x0f0c;
    L_0x0ed6:
        if (r19 != 0) goto L_0x0ef9;
    L_0x0ed8:
        r3 = r1.messageOwner;
        r3 = r3.message;
        if (r3 == 0) goto L_0x0ef9;
    L_0x0ede:
        r3 = r3.length();
        if (r3 == 0) goto L_0x0ef9;
    L_0x0ee4:
        r3 = 3;
        r3 = new java.lang.Object[r3];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r1 = r1.messageOwner;
        r1 = r1.message;
        r3[r9] = r1;
        r8 = org.telegram.messenger.LocaleController.formatString(r14, r6, r3);
        goto L_0x118a;
    L_0x0ef9:
        r1 = NUM; // 0x7f0d065f float:1.8745423E38 double:1.0531305834E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupNoText";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0f0c:
        r3 = r1.messageOwner;
        r15 = r3.media;
        r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r15 == 0) goto L_0x0f5b;
    L_0x0var_:
        if (r19 != 0) goto L_0x0var_;
    L_0x0var_:
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r4 < r5) goto L_0x0var_;
    L_0x0f1c:
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0var_;
    L_0x0var_:
        r3 = 3;
        r3 = new java.lang.Object[r3];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r13);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r2.append(r1);
        r1 = r2.toString();
        r3[r9] = r1;
        r8 = org.telegram.messenger.LocaleController.formatString(r14, r6, r3);
        goto L_0x118a;
    L_0x0var_:
        r1 = NUM; // 0x7f0d0660 float:1.8745425E38 double:1.053130584E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupPhoto";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0f5b:
        r3 = r18.isVideo();
        if (r3 == 0) goto L_0x0faa;
    L_0x0var_:
        if (r19 != 0) goto L_0x0var_;
    L_0x0var_:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r3 < r4) goto L_0x0var_;
    L_0x0var_:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0var_;
    L_0x0var_:
        r3 = 3;
        r3 = new java.lang.Object[r3];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r12);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r2.append(r1);
        r1 = r2.toString();
        r3[r9] = r1;
        r8 = org.telegram.messenger.LocaleController.formatString(r14, r6, r3);
        goto L_0x118a;
    L_0x0var_:
        r1 = NUM; // 0x7f0d0666 float:1.8745437E38 double:1.053130587E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = " ";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0faa:
        r3 = r18.isVoice();
        if (r3 == 0) goto L_0x0fc3;
    L_0x0fb0:
        r1 = NUM; // 0x7f0d0655 float:1.8745403E38 double:1.0531305784E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupAudio";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0fc3:
        r3 = r18.isRoundVideo();
        if (r3 == 0) goto L_0x0fdc;
    L_0x0fc9:
        r1 = NUM; // 0x7f0d0662 float:1.8745429E38 double:1.053130585E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupRound";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0fdc:
        r3 = r18.isMusic();
        if (r3 == 0) goto L_0x0ff5;
    L_0x0fe2:
        r1 = NUM; // 0x7f0d065e float:1.874542E38 double:1.053130583E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupMusic";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x0ff5:
        r3 = r1.messageOwner;
        r3 = r3.media;
        r12 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r12 == 0) goto L_0x101d;
    L_0x0ffd:
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r3;
        r1 = NUM; // 0x7f0d0656 float:1.8745405E38 double:1.053130579E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r2 = r11.title;
        r4[r7] = r2;
        r2 = r3.first_name;
        r3 = r3.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r3);
        r4[r9] = r2;
        r2 = "NotificationMessageGroupContact2";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r4);
        goto L_0x118a;
    L_0x101d:
        r12 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r12 == 0) goto L_0x103d;
    L_0x1021:
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3;
        r1 = NUM; // 0x7f0d0661 float:1.8745427E38 double:1.0531305844E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r2 = r11.title;
        r4[r7] = r2;
        r2 = r3.poll;
        r2 = r2.question;
        r4[r9] = r2;
        r2 = "NotificationMessageGroupPoll2";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r4);
        goto L_0x118a;
    L_0x103d:
        r12 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r12 == 0) goto L_0x105b;
    L_0x1041:
        r1 = NUM; // 0x7f0d0658 float:1.8745409E38 double:1.05313058E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r2 = r11.title;
        r4[r7] = r2;
        r2 = r3.game;
        r2 = r2.title;
        r4[r9] = r2;
        r2 = "NotificationMessageGroupGame";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r4);
        goto L_0x118a;
    L_0x105b:
        r12 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r12 != 0) goto L_0x114b;
    L_0x105f:
        r12 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
        if (r12 == 0) goto L_0x1065;
    L_0x1063:
        goto L_0x114b;
    L_0x1065:
        r12 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r12 == 0) goto L_0x107c;
    L_0x1069:
        r1 = NUM; // 0x7f0d065c float:1.8745417E38 double:1.053130582E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupLiveLocation";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x107c:
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r3 == 0) goto L_0x118a;
    L_0x1080:
        r3 = r18.isSticker();
        if (r3 == 0) goto L_0x10b5;
    L_0x1086:
        r1 = r18.getStickerEmoji();
        if (r1 == 0) goto L_0x10a2;
    L_0x108c:
        r3 = NUM; // 0x7f0d0664 float:1.8745433E38 double:1.053130586E-314;
        r4 = 3;
        r4 = new java.lang.Object[r4];
        r4[r2] = r10;
        r2 = r11.title;
        r4[r7] = r2;
        r4[r9] = r1;
        r1 = "NotificationMessageGroupStickerEmoji";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        goto L_0x0407;
    L_0x10a2:
        r1 = NUM; // 0x7f0d0663 float:1.874543E38 double:1.0531305853E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupSticker";
        r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x0407;
    L_0x10b5:
        r3 = r18.isGif();
        if (r3 == 0) goto L_0x1104;
    L_0x10bb:
        if (r19 != 0) goto L_0x10f1;
    L_0x10bd:
        r3 = android.os.Build.VERSION.SDK_INT;
        r5 = 19;
        if (r3 < r5) goto L_0x10f1;
    L_0x10c3:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x10f1;
    L_0x10cd:
        r3 = 3;
        r3 = new java.lang.Object[r3];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r4);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r2.append(r1);
        r1 = r2.toString();
        r3[r9] = r1;
        r8 = org.telegram.messenger.LocaleController.formatString(r14, r6, r3);
        goto L_0x118a;
    L_0x10f1:
        r1 = NUM; // 0x7f0d065a float:1.8745413E38 double:1.053130581E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupGif";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x1104:
        if (r19 != 0) goto L_0x1139;
    L_0x1106:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 19;
        if (r3 < r4) goto L_0x1139;
    L_0x110c:
        r3 = r1.messageOwner;
        r3 = r3.message;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x1139;
    L_0x1116:
        r3 = 3;
        r3 = new java.lang.Object[r3];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r5);
        r1 = r1.messageOwner;
        r1 = r1.message;
        r2.append(r1);
        r1 = r2.toString();
        r3[r9] = r1;
        r8 = org.telegram.messenger.LocaleController.formatString(r14, r6, r3);
        goto L_0x118a;
    L_0x1139:
        r1 = NUM; // 0x7f0d0657 float:1.8745407E38 double:1.0531305794E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupDocument";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x114b:
        r1 = NUM; // 0x7f0d065d float:1.8745419E38 double:1.0531305824E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupMap";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x115d:
        if (r21 == 0) goto L_0x1161;
    L_0x115f:
        r21[r2] = r2;
    L_0x1161:
        r1 = org.telegram.messenger.ChatObject.isChannel(r11);
        if (r1 == 0) goto L_0x1179;
    L_0x1167:
        r1 = r11.megagroup;
        if (r1 != 0) goto L_0x1179;
    L_0x116b:
        r1 = NUM; // 0x7f0d023d float:1.8743277E38 double:1.0531300606E-314;
        r3 = new java.lang.Object[r7];
        r3[r2] = r10;
        r2 = "ChannelMessageNoText";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
        goto L_0x118a;
    L_0x1179:
        r1 = NUM; // 0x7f0d065f float:1.8745423E38 double:1.0531305834E-314;
        r3 = new java.lang.Object[r9];
        r3[r2] = r10;
        r2 = r11.title;
        r3[r7] = r2;
        r2 = "NotificationMessageGroupNoText";
        r8 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3);
    L_0x118a:
        return r8;
    L_0x118b:
        r1 = NUM; // 0x7f0d0ab3 float:1.874767E38 double:1.053131131E-314;
        r2 = "YouHaveNewMessage";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getStringForMessage(org.telegram.messenger.MessageObject, boolean, boolean[], boolean[]):java.lang.String");
    }

    private void scheduleNotificationRepeat() {
        try {
            Intent intent = new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class);
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent service = PendingIntent.getService(ApplicationLoader.applicationContext, 0, intent, 0);
            int i = MessagesController.getNotificationsSettings(this.currentAccount).getInt("repeat_messages", 60);
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
        return sharedPreferences.getInt(stringBuilder2.toString(), 0) >= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() ? 2 : i;
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
                    jSONObject.put("id", UserConfig.getInstance(this.currentAccount).getClientUserId());
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
                if (getNotifyOverride(MessagesController.getNotificationsSettings(this.currentAccount), this.opened_dialog_id) != 2) {
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
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
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
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
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
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
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

    /* JADX WARNING: Removed duplicated region for block: B:427:0x0922 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0918 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0929 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0abe A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0aa5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0af5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0adc A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x085a A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x099a A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0aa5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0abe A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0adc A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0af5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x085a A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x099a A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0abe A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0aa5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0af5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0adc A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x085a A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x099a A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0aa5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0abe A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0adc A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0af5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x085a A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x099a A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0abe A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0aa5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0af5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0adc A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x085a A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x099a A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0aa5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0abe A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0adc A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0af5 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x06ca A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0651 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x07fd  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x07b3 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0651 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x06ca A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x07b3 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x07fd  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0802  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0571 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0539 A:{SYNTHETIC, Splitter:B:278:0x0539} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x057d A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x05ab A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0593 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0506 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0503 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0521 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0520 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04f0 A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0503 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0506 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0520 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0521 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03d0 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03cb A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04b2 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04f0 A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0506 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0503 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0521 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0520 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03c4 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0349 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03cb A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03d0 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04b2 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04f0 A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0503 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0506 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0520 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0521 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02ef A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02b8 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02f5 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02f4 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02fe A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02fa A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0327  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0317  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0349 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03c4 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03d0 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03cb A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04b2 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04f0 A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0506 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0503 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0521 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0520 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01c3 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x027d A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02b8 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02ef A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02f4 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02f5 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02fa A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02fe A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x030e A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0317  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0327  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03c4 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0349 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03cb A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03d0 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04b2 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04f0 A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0503 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0506 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0520 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0521 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01c3 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x027d A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02ef A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02b8 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02f5 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02f4 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02fe A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02fa A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x030e A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0327  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0317  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0349 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03c4 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03d0 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03cb A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04b2 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04f0 A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0506 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0503 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0521 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0520 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0133 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0106 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0138 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01c3 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x027d A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02b8 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02ef A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02f4 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02f5 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02fa A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02fe A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x030e A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0317  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0327  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03c4 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0349 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03cb A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03d0 A:{Catch:{ Exception -> 0x02ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04b2 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04f0 A:{SKIP, Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0503 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0506 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0520 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0521 A:{Catch:{ Exception -> 0x0b01 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:421:0x0904 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing block: B:387:0x0851, code skipped:
            if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x0853;
     */
    private void showOrUpdateNotification(boolean r46) {
        /*
        r45 = this;
        r12 = r45;
        r13 = r46;
        r1 = "currentAccount";
        r2 = r12.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.isClientActivated();
        if (r2 == 0) goto L_0x0b07;
    L_0x0012:
        r2 = r12.pushMessages;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x0b07;
    L_0x001a:
        r2 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts;
        if (r2 != 0) goto L_0x0026;
    L_0x001e:
        r2 = r12.currentAccount;
        r3 = org.telegram.messenger.UserConfig.selectedAccount;
        if (r2 == r3) goto L_0x0026;
    L_0x0024:
        goto L_0x0b07;
    L_0x0026:
        r2 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);	 Catch:{ Exception -> 0x0b01 }
        r2.resumeNetworkMaybe();	 Catch:{ Exception -> 0x0b01 }
        r2 = r12.pushMessages;	 Catch:{ Exception -> 0x0b01 }
        r3 = 0;
        r2 = r2.get(r3);	 Catch:{ Exception -> 0x0b01 }
        r2 = (org.telegram.messenger.MessageObject) r2;	 Catch:{ Exception -> 0x0b01 }
        r4 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0b01 }
        r5 = "dismissDate";
        r5 = r4.getInt(r5, r3);	 Catch:{ Exception -> 0x0b01 }
        r6 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r6 = r6.date;	 Catch:{ Exception -> 0x0b01 }
        if (r6 > r5) goto L_0x004e;
    L_0x004a:
        r45.dismissNotification();	 Catch:{ Exception -> 0x0b01 }
        return;
    L_0x004e:
        r6 = r2.getDialogId();	 Catch:{ Exception -> 0x0b01 }
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r8 = r8.mentioned;	 Catch:{ Exception -> 0x0b01 }
        if (r8 == 0) goto L_0x005e;
    L_0x0058:
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r8 = r8.from_id;	 Catch:{ Exception -> 0x0b01 }
        r8 = (long) r8;	 Catch:{ Exception -> 0x0b01 }
        goto L_0x005f;
    L_0x005e:
        r8 = r6;
    L_0x005f:
        r2.getId();	 Catch:{ Exception -> 0x0b01 }
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0b01 }
        r10 = r10.chat_id;	 Catch:{ Exception -> 0x0b01 }
        if (r10 == 0) goto L_0x0071;
    L_0x006a:
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0b01 }
        r10 = r10.chat_id;	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0077;
    L_0x0071:
        r10 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r10 = r10.to_id;	 Catch:{ Exception -> 0x0b01 }
        r10 = r10.channel_id;	 Catch:{ Exception -> 0x0b01 }
    L_0x0077:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r11 = r11.to_id;	 Catch:{ Exception -> 0x0b01 }
        r11 = r11.user_id;	 Catch:{ Exception -> 0x0b01 }
        if (r11 != 0) goto L_0x0084;
    L_0x007f:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r11 = r11.from_id;	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0094;
    L_0x0084:
        r14 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ Exception -> 0x0b01 }
        r14 = r14.getClientUserId();	 Catch:{ Exception -> 0x0b01 }
        if (r11 != r14) goto L_0x0094;
    L_0x0090:
        r11 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r11 = r11.from_id;	 Catch:{ Exception -> 0x0b01 }
    L_0x0094:
        r14 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r14 = org.telegram.messenger.MessagesController.getInstance(r14);	 Catch:{ Exception -> 0x0b01 }
        r15 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0b01 }
        r14 = r14.getUser(r15);	 Catch:{ Exception -> 0x0b01 }
        if (r10 == 0) goto L_0x00c2;
    L_0x00a4:
        r15 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r15 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Exception -> 0x0b01 }
        r3 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0b01 }
        r15 = r15.getChat(r3);	 Catch:{ Exception -> 0x0b01 }
        r3 = org.telegram.messenger.ChatObject.isChannel(r15);	 Catch:{ Exception -> 0x0b01 }
        if (r3 == 0) goto L_0x00be;
    L_0x00b8:
        r3 = r15.megagroup;	 Catch:{ Exception -> 0x0b01 }
        if (r3 != 0) goto L_0x00be;
    L_0x00bc:
        r3 = 1;
        goto L_0x00bf;
    L_0x00be:
        r3 = 0;
    L_0x00bf:
        r19 = r5;
        goto L_0x00c6;
    L_0x00c2:
        r19 = r5;
        r3 = 0;
        r15 = 0;
    L_0x00c6:
        r5 = r12.getNotifyOverride(r4, r8);	 Catch:{ Exception -> 0x0b01 }
        r20 = r2;
        r2 = -1;
        r21 = r1;
        r1 = 2;
        if (r5 != r2) goto L_0x00d7;
    L_0x00d2:
        r2 = r12.isGlobalNotificationsEnabled(r6);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x00dc;
    L_0x00d7:
        if (r5 == r1) goto L_0x00db;
    L_0x00d9:
        r2 = 1;
        goto L_0x00dc;
    L_0x00db:
        r2 = 0;
    L_0x00dc:
        if (r13 == 0) goto L_0x00e3;
    L_0x00de:
        if (r2 != 0) goto L_0x00e1;
    L_0x00e0:
        goto L_0x00e3;
    L_0x00e1:
        r2 = 0;
        goto L_0x00e4;
    L_0x00e3:
        r2 = 1;
    L_0x00e4:
        r22 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r2 != 0) goto L_0x0185;
    L_0x00e8:
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 != 0) goto L_0x0185;
    L_0x00ec:
        if (r15 == 0) goto L_0x0185;
    L_0x00ee:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r5.<init>();	 Catch:{ Exception -> 0x0b01 }
        r8 = "custom_";
        r5.append(r8);	 Catch:{ Exception -> 0x0b01 }
        r5.append(r6);	 Catch:{ Exception -> 0x0b01 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0b01 }
        r8 = 0;
        r5 = r4.getBoolean(r5, r8);	 Catch:{ Exception -> 0x0b01 }
        if (r5 == 0) goto L_0x0133;
    L_0x0106:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r5.<init>();	 Catch:{ Exception -> 0x0b01 }
        r8 = "smart_max_count_";
        r5.append(r8);	 Catch:{ Exception -> 0x0b01 }
        r5.append(r6);	 Catch:{ Exception -> 0x0b01 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0b01 }
        r5 = r4.getInt(r5, r1);	 Catch:{ Exception -> 0x0b01 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r8.<init>();	 Catch:{ Exception -> 0x0b01 }
        r9 = "smart_delay_";
        r8.append(r9);	 Catch:{ Exception -> 0x0b01 }
        r8.append(r6);	 Catch:{ Exception -> 0x0b01 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x0b01 }
        r9 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r8 = r4.getInt(r8, r9);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0136;
    L_0x0133:
        r8 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r5 = 2;
    L_0x0136:
        if (r5 == 0) goto L_0x0185;
    L_0x0138:
        r9 = r12.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0b01 }
        r9 = r9.get(r6);	 Catch:{ Exception -> 0x0b01 }
        r9 = (android.graphics.Point) r9;	 Catch:{ Exception -> 0x0b01 }
        if (r9 != 0) goto L_0x0155;
    L_0x0142:
        r5 = new android.graphics.Point;	 Catch:{ Exception -> 0x0b01 }
        r8 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0b01 }
        r8 = r8 / r22;
        r9 = (int) r8;	 Catch:{ Exception -> 0x0b01 }
        r8 = 1;
        r5.<init>(r8, r9);	 Catch:{ Exception -> 0x0b01 }
        r8 = r12.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0b01 }
        r8.put(r6, r5);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0185;
    L_0x0155:
        r1 = r9.y;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1 + r8;
        r8 = r2;
        r1 = (long) r1;	 Catch:{ Exception -> 0x0b01 }
        r24 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0b01 }
        r24 = r24 / r22;
        r26 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1));
        if (r26 >= 0) goto L_0x0170;
    L_0x0164:
        r1 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0b01 }
        r1 = r1 / r22;
        r2 = (int) r1;	 Catch:{ Exception -> 0x0b01 }
        r1 = 1;
        r9.set(r1, r2);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0186;
    L_0x0170:
        r1 = r9.x;	 Catch:{ Exception -> 0x0b01 }
        if (r1 >= r5) goto L_0x0182;
    L_0x0174:
        r2 = 1;
        r1 = r1 + r2;
        r24 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0b01 }
        r2 = r14;
        r13 = r24 / r22;
        r5 = (int) r13;	 Catch:{ Exception -> 0x0b01 }
        r9.set(r1, r5);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0187;
    L_0x0182:
        r2 = r14;
        r8 = 1;
        goto L_0x0187;
    L_0x0185:
        r8 = r2;
    L_0x0186:
        r2 = r14;
    L_0x0187:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.getPath();	 Catch:{ Exception -> 0x0b01 }
        r5 = "EnableInAppSounds";
        r9 = 1;
        r5 = r4.getBoolean(r5, r9);	 Catch:{ Exception -> 0x0b01 }
        r13 = "EnableInAppVibrate";
        r13 = r4.getBoolean(r13, r9);	 Catch:{ Exception -> 0x0b01 }
        r14 = "EnableInAppPreview";
        r14 = r4.getBoolean(r14, r9);	 Catch:{ Exception -> 0x0b01 }
        r9 = "EnableInAppPriority";
        r24 = r14;
        r14 = 0;
        r9 = r4.getBoolean(r9, r14);	 Catch:{ Exception -> 0x0b01 }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r14.<init>();	 Catch:{ Exception -> 0x0b01 }
        r25 = r2;
        r2 = "custom_";
        r14.append(r2);	 Catch:{ Exception -> 0x0b01 }
        r14.append(r6);	 Catch:{ Exception -> 0x0b01 }
        r2 = r14.toString();	 Catch:{ Exception -> 0x0b01 }
        r14 = 0;
        r2 = r4.getBoolean(r2, r14);	 Catch:{ Exception -> 0x0b01 }
        if (r2 == 0) goto L_0x0214;
    L_0x01c3:
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r14.<init>();	 Catch:{ Exception -> 0x0b01 }
        r27 = r15;
        r15 = "vibrate_";
        r14.append(r15);	 Catch:{ Exception -> 0x0b01 }
        r14.append(r6);	 Catch:{ Exception -> 0x0b01 }
        r14 = r14.toString();	 Catch:{ Exception -> 0x0b01 }
        r15 = 0;
        r14 = r4.getInt(r14, r15);	 Catch:{ Exception -> 0x0b01 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r15.<init>();	 Catch:{ Exception -> 0x0b01 }
        r28 = r14;
        r14 = "priority_";
        r15.append(r14);	 Catch:{ Exception -> 0x0b01 }
        r15.append(r6);	 Catch:{ Exception -> 0x0b01 }
        r14 = r15.toString();	 Catch:{ Exception -> 0x0b01 }
        r15 = 3;
        r14 = r4.getInt(r14, r15);	 Catch:{ Exception -> 0x0b01 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r15.<init>();	 Catch:{ Exception -> 0x0b01 }
        r29 = r14;
        r14 = "sound_path_";
        r15.append(r14);	 Catch:{ Exception -> 0x0b01 }
        r15.append(r6);	 Catch:{ Exception -> 0x0b01 }
        r14 = r15.toString();	 Catch:{ Exception -> 0x0b01 }
        r15 = 0;
        r14 = r4.getString(r14, r15);	 Catch:{ Exception -> 0x0b01 }
        r15 = r14;
        r14 = r28;
        r12 = r29;
        r28 = r8;
        goto L_0x021b;
    L_0x0214:
        r27 = r15;
        r28 = r8;
        r12 = 3;
        r14 = 0;
        r15 = 0;
    L_0x021b:
        if (r10 == 0) goto L_0x027d;
    L_0x021d:
        if (r3 == 0) goto L_0x024e;
    L_0x021f:
        if (r15 == 0) goto L_0x0229;
    L_0x0221:
        r3 = r15.equals(r1);	 Catch:{ Exception -> 0x02ea }
        if (r3 == 0) goto L_0x0229;
    L_0x0227:
        r15 = 0;
        goto L_0x0231;
    L_0x0229:
        if (r15 != 0) goto L_0x0231;
    L_0x022b:
        r3 = "ChannelSoundPath";
        r15 = r4.getString(r3, r1);	 Catch:{ Exception -> 0x02ea }
    L_0x0231:
        r3 = "vibrate_channel";
        r8 = 0;
        r3 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02ea }
        r8 = "priority_channel";
        r30 = r3;
        r3 = 1;
        r8 = r4.getInt(r8, r3);	 Catch:{ Exception -> 0x02ea }
        r3 = "ChannelLed";
        r31 = r8;
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r8 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02ea }
        goto L_0x02ad;
    L_0x024e:
        if (r15 == 0) goto L_0x0258;
    L_0x0250:
        r3 = r15.equals(r1);	 Catch:{ Exception -> 0x02ea }
        if (r3 == 0) goto L_0x0258;
    L_0x0256:
        r15 = 0;
        goto L_0x0260;
    L_0x0258:
        if (r15 != 0) goto L_0x0260;
    L_0x025a:
        r3 = "GroupSoundPath";
        r15 = r4.getString(r3, r1);	 Catch:{ Exception -> 0x02ea }
    L_0x0260:
        r3 = "vibrate_group";
        r8 = 0;
        r3 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02ea }
        r8 = "priority_group";
        r30 = r3;
        r3 = 1;
        r8 = r4.getInt(r8, r3);	 Catch:{ Exception -> 0x02ea }
        r3 = "GroupLed";
        r31 = r8;
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r8 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02ea }
        goto L_0x02ad;
    L_0x027d:
        if (r11 == 0) goto L_0x02b0;
    L_0x027f:
        if (r15 == 0) goto L_0x0289;
    L_0x0281:
        r3 = r15.equals(r1);	 Catch:{ Exception -> 0x02ea }
        if (r3 == 0) goto L_0x0289;
    L_0x0287:
        r15 = 0;
        goto L_0x0291;
    L_0x0289:
        if (r15 != 0) goto L_0x0291;
    L_0x028b:
        r3 = "GlobalSoundPath";
        r15 = r4.getString(r3, r1);	 Catch:{ Exception -> 0x02ea }
    L_0x0291:
        r3 = "vibrate_messages";
        r8 = 0;
        r3 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02ea }
        r8 = "priority_messages";
        r30 = r3;
        r3 = 1;
        r8 = r4.getInt(r8, r3);	 Catch:{ Exception -> 0x02ea }
        r3 = "MessagesLed";
        r31 = r8;
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r8 = r4.getInt(r3, r8);	 Catch:{ Exception -> 0x02ea }
    L_0x02ad:
        r3 = r30;
        goto L_0x02b6;
    L_0x02b0:
        r8 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r3 = 0;
        r31 = 0;
    L_0x02b6:
        if (r2 == 0) goto L_0x02ef;
    L_0x02b8:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02ea }
        r2.<init>();	 Catch:{ Exception -> 0x02ea }
        r29 = r8;
        r8 = "color_";
        r2.append(r8);	 Catch:{ Exception -> 0x02ea }
        r2.append(r6);	 Catch:{ Exception -> 0x02ea }
        r2 = r2.toString();	 Catch:{ Exception -> 0x02ea }
        r2 = r4.contains(r2);	 Catch:{ Exception -> 0x02ea }
        if (r2 == 0) goto L_0x02f1;
    L_0x02d1:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02ea }
        r2.<init>();	 Catch:{ Exception -> 0x02ea }
        r8 = "color_";
        r2.append(r8);	 Catch:{ Exception -> 0x02ea }
        r2.append(r6);	 Catch:{ Exception -> 0x02ea }
        r2 = r2.toString();	 Catch:{ Exception -> 0x02ea }
        r8 = 0;
        r2 = r4.getInt(r2, r8);	 Catch:{ Exception -> 0x02ea }
        r29 = r2;
        goto L_0x02f1;
    L_0x02ea:
        r0 = move-exception;
        r12 = r45;
        goto L_0x0b02;
    L_0x02ef:
        r29 = r8;
    L_0x02f1:
        r2 = 3;
        if (r12 == r2) goto L_0x02f5;
    L_0x02f4:
        goto L_0x02f7;
    L_0x02f5:
        r12 = r31;
    L_0x02f7:
        r4 = 4;
        if (r3 != r4) goto L_0x02fe;
    L_0x02fa:
        r3 = 0;
        r4 = 2;
        r8 = 1;
        goto L_0x0300;
    L_0x02fe:
        r4 = 2;
        r8 = 0;
    L_0x0300:
        if (r3 != r4) goto L_0x0307;
    L_0x0302:
        r4 = 1;
        if (r14 == r4) goto L_0x0313;
    L_0x0305:
        if (r14 == r2) goto L_0x0313;
    L_0x0307:
        r2 = 2;
        if (r3 == r2) goto L_0x030c;
    L_0x030a:
        if (r14 == r2) goto L_0x0313;
    L_0x030c:
        if (r14 == 0) goto L_0x0312;
    L_0x030e:
        r2 = 4;
        if (r14 == r2) goto L_0x0312;
    L_0x0311:
        goto L_0x0313;
    L_0x0312:
        r14 = r3;
    L_0x0313:
        r2 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x02ea }
        if (r2 != 0) goto L_0x0327;
    L_0x0317:
        if (r5 != 0) goto L_0x031a;
    L_0x0319:
        r15 = 0;
    L_0x031a:
        if (r13 != 0) goto L_0x031d;
    L_0x031c:
        r14 = 2;
    L_0x031d:
        if (r9 != 0) goto L_0x0322;
    L_0x031f:
        r2 = 2;
        r3 = 0;
        goto L_0x0329;
    L_0x0322:
        r2 = 2;
        if (r12 != r2) goto L_0x0328;
    L_0x0325:
        r3 = 1;
        goto L_0x0329;
    L_0x0327:
        r2 = 2;
    L_0x0328:
        r3 = r12;
    L_0x0329:
        if (r8 == 0) goto L_0x033f;
    L_0x032b:
        if (r14 == r2) goto L_0x033f;
    L_0x032d:
        r2 = audioManager;	 Catch:{ Exception -> 0x033a }
        r2 = r2.getRingerMode();	 Catch:{ Exception -> 0x033a }
        if (r2 == 0) goto L_0x033f;
    L_0x0335:
        r4 = 1;
        if (r2 == r4) goto L_0x033f;
    L_0x0338:
        r14 = 2;
        goto L_0x033f;
    L_0x033a:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x02ea }
    L_0x033f:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x02ea }
        r4 = 100;
        r9 = 26;
        r12 = 0;
        if (r2 < r9) goto L_0x03c4;
    L_0x0349:
        r2 = 2;
        if (r14 != r2) goto L_0x0356;
    L_0x034c:
        r9 = new long[r2];	 Catch:{ Exception -> 0x02ea }
        r2 = 0;
        r9[r2] = r12;	 Catch:{ Exception -> 0x02ea }
        r2 = 1;
        r9[r2] = r12;	 Catch:{ Exception -> 0x02ea }
        r8 = r9;
        goto L_0x0380;
    L_0x0356:
        r2 = 1;
        if (r14 != r2) goto L_0x0368;
    L_0x0359:
        r9 = 4;
        r8 = new long[r9];	 Catch:{ Exception -> 0x02ea }
        r9 = 0;
        r8[r9] = r12;	 Catch:{ Exception -> 0x02ea }
        r8[r2] = r4;	 Catch:{ Exception -> 0x02ea }
        r2 = 2;
        r8[r2] = r12;	 Catch:{ Exception -> 0x02ea }
        r2 = 3;
        r8[r2] = r4;	 Catch:{ Exception -> 0x02ea }
        goto L_0x0380;
    L_0x0368:
        if (r14 == 0) goto L_0x037d;
    L_0x036a:
        r2 = 4;
        if (r14 != r2) goto L_0x036e;
    L_0x036d:
        goto L_0x037d;
    L_0x036e:
        r2 = 3;
        if (r14 != r2) goto L_0x037b;
    L_0x0371:
        r2 = 2;
        r8 = new long[r2];	 Catch:{ Exception -> 0x02ea }
        r2 = 0;
        r8[r2] = r12;	 Catch:{ Exception -> 0x02ea }
        r2 = 1;
        r8[r2] = r22;	 Catch:{ Exception -> 0x02ea }
        goto L_0x0380;
    L_0x037b:
        r8 = 0;
        goto L_0x0380;
    L_0x037d:
        r2 = 0;
        r8 = new long[r2];	 Catch:{ Exception -> 0x02ea }
    L_0x0380:
        if (r15 == 0) goto L_0x0398;
    L_0x0382:
        r2 = "NoSound";
        r2 = r15.equals(r2);	 Catch:{ Exception -> 0x02ea }
        if (r2 != 0) goto L_0x0398;
    L_0x038a:
        r2 = r15.equals(r1);	 Catch:{ Exception -> 0x02ea }
        if (r2 == 0) goto L_0x0393;
    L_0x0390:
        r2 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x02ea }
        goto L_0x0399;
    L_0x0393:
        r2 = android.net.Uri.parse(r15);	 Catch:{ Exception -> 0x02ea }
        goto L_0x0399;
    L_0x0398:
        r2 = 0;
    L_0x0399:
        if (r3 != 0) goto L_0x03a1;
    L_0x039b:
        r32 = r2;
        r9 = r8;
        r33 = 3;
        goto L_0x03c9;
    L_0x03a1:
        r9 = 1;
        if (r3 == r9) goto L_0x03be;
    L_0x03a4:
        r9 = 2;
        if (r3 != r9) goto L_0x03a8;
    L_0x03a7:
        goto L_0x03be;
    L_0x03a8:
        r9 = 4;
        if (r3 != r9) goto L_0x03b1;
    L_0x03ab:
        r32 = r2;
        r9 = r8;
        r33 = 1;
        goto L_0x03c9;
    L_0x03b1:
        r9 = 5;
        if (r3 != r9) goto L_0x03ba;
    L_0x03b4:
        r32 = r2;
        r9 = r8;
        r33 = 2;
        goto L_0x03c9;
    L_0x03ba:
        r32 = r2;
        r9 = r8;
        goto L_0x03c7;
    L_0x03be:
        r32 = r2;
        r9 = r8;
        r33 = 4;
        goto L_0x03c9;
    L_0x03c4:
        r9 = 0;
        r32 = 0;
    L_0x03c7:
        r33 = 0;
    L_0x03c9:
        if (r28 == 0) goto L_0x03d0;
    L_0x03cb:
        r3 = 0;
        r8 = 0;
        r14 = 0;
        r15 = 0;
        goto L_0x03d2;
    L_0x03d0:
        r8 = r29;
    L_0x03d2:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x02ea }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x02ea }
        r5 = org.telegram.ui.LaunchActivity.class;
        r2.<init>(r4, r5);	 Catch:{ Exception -> 0x02ea }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02ea }
        r4.<init>();	 Catch:{ Exception -> 0x02ea }
        r5 = "com.tmessages.openchat";
        r4.append(r5);	 Catch:{ Exception -> 0x02ea }
        r12 = java.lang.Math.random();	 Catch:{ Exception -> 0x02ea }
        r4.append(r12);	 Catch:{ Exception -> 0x02ea }
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4.append(r5);	 Catch:{ Exception -> 0x02ea }
        r4 = r4.toString();	 Catch:{ Exception -> 0x02ea }
        r2.setAction(r4);	 Catch:{ Exception -> 0x02ea }
        r4 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r2.setFlags(r4);	 Catch:{ Exception -> 0x02ea }
        r4 = (int) r6;
        if (r4 == 0) goto L_0x04b2;
    L_0x0402:
        r12 = r45;
        r5 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b01 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0b01 }
        r13 = 1;
        if (r5 != r13) goto L_0x041c;
    L_0x040d:
        if (r10 == 0) goto L_0x0415;
    L_0x040f:
        r5 = "chatId";
        r2.putExtra(r5, r10);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x041c;
    L_0x0415:
        if (r11 == 0) goto L_0x041c;
    L_0x0417:
        r5 = "userId";
        r2.putExtra(r5, r11);	 Catch:{ Exception -> 0x0b01 }
    L_0x041c:
        r5 = 0;
        r11 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r5);	 Catch:{ Exception -> 0x0b01 }
        if (r11 != 0) goto L_0x04a7;
    L_0x0423:
        r5 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0b01 }
        if (r5 == 0) goto L_0x0429;
    L_0x0427:
        goto L_0x04a7;
    L_0x0429:
        r5 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b01 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0b01 }
        r11 = 1;
        if (r5 != r11) goto L_0x04a0;
    L_0x0432:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r11 = 28;
        if (r5 >= r11) goto L_0x04a0;
    L_0x0438:
        if (r27 == 0) goto L_0x0470;
    L_0x043a:
        r5 = r27;
        r11 = r5.photo;	 Catch:{ Exception -> 0x0b01 }
        if (r11 == 0) goto L_0x0469;
    L_0x0440:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0b01 }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0b01 }
        if (r11 == 0) goto L_0x0469;
    L_0x0446:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0b01 }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0b01 }
        r27 = r14;
        r13 = r11.volume_id;	 Catch:{ Exception -> 0x0b01 }
        r34 = 0;
        r11 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1));
        if (r11 == 0) goto L_0x046b;
    L_0x0454:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0b01 }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0b01 }
        r11 = r11.local_id;	 Catch:{ Exception -> 0x0b01 }
        if (r11 == 0) goto L_0x046b;
    L_0x045c:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0b01 }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0b01 }
        r29 = r8;
        r13 = r11;
        r11 = r25;
    L_0x0465:
        r25 = r9;
        goto L_0x04dc;
    L_0x0469:
        r27 = r14;
    L_0x046b:
        r29 = r8;
        r11 = r25;
        goto L_0x04af;
    L_0x0470:
        r5 = r27;
        r27 = r14;
        if (r25 == 0) goto L_0x049d;
    L_0x0476:
        r11 = r25;
        r13 = r11.photo;	 Catch:{ Exception -> 0x0b01 }
        if (r13 == 0) goto L_0x04d8;
    L_0x047c:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0b01 }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0b01 }
        if (r13 == 0) goto L_0x04d8;
    L_0x0482:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0b01 }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0b01 }
        r13 = r13.volume_id;	 Catch:{ Exception -> 0x0b01 }
        r34 = 0;
        r25 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1));
        if (r25 == 0) goto L_0x04d8;
    L_0x048e:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0b01 }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0b01 }
        r13 = r13.local_id;	 Catch:{ Exception -> 0x0b01 }
        if (r13 == 0) goto L_0x04d8;
    L_0x0496:
        r13 = r11.photo;	 Catch:{ Exception -> 0x0b01 }
        r13 = r13.photo_small;	 Catch:{ Exception -> 0x0b01 }
        r29 = r8;
        goto L_0x0465;
    L_0x049d:
        r11 = r25;
        goto L_0x04d8;
    L_0x04a0:
        r11 = r25;
        r5 = r27;
        r27 = r14;
        goto L_0x04d8;
    L_0x04a7:
        r11 = r25;
        r5 = r27;
        r27 = r14;
        r29 = r8;
    L_0x04af:
        r25 = r9;
        goto L_0x04db;
    L_0x04b2:
        r12 = r45;
        r11 = r25;
        r5 = r27;
        r27 = r14;
        r13 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b01 }
        r13 = r13.size();	 Catch:{ Exception -> 0x0b01 }
        r14 = 1;
        if (r13 != r14) goto L_0x04d8;
    L_0x04c3:
        r13 = globalSecretChatId;	 Catch:{ Exception -> 0x0b01 }
        r25 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1));
        if (r25 == 0) goto L_0x04d8;
    L_0x04c9:
        r13 = "encId";
        r14 = 32;
        r29 = r8;
        r25 = r9;
        r8 = r6 >> r14;
        r9 = (int) r8;	 Catch:{ Exception -> 0x0b01 }
        r2.putExtra(r13, r9);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x04db;
    L_0x04d8:
        r29 = r8;
        goto L_0x04af;
    L_0x04db:
        r13 = 0;
    L_0x04dc:
        r8 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r9 = r21;
        r2.putExtra(r9, r8);	 Catch:{ Exception -> 0x0b01 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b01 }
        r14 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r36 = r6;
        r6 = 0;
        r2 = android.app.PendingIntent.getActivity(r8, r6, r2, r14);	 Catch:{ Exception -> 0x0b01 }
        if (r10 == 0) goto L_0x04f2;
    L_0x04f0:
        if (r5 == 0) goto L_0x04f4;
    L_0x04f2:
        if (r11 != 0) goto L_0x04ff;
    L_0x04f4:
        r6 = r20.isFcmMessage();	 Catch:{ Exception -> 0x0b01 }
        if (r6 == 0) goto L_0x04ff;
    L_0x04fa:
        r6 = r20;
        r7 = r6.localName;	 Catch:{ Exception -> 0x0b01 }
        goto L_0x050a;
    L_0x04ff:
        r6 = r20;
        if (r5 == 0) goto L_0x0506;
    L_0x0503:
        r7 = r5.title;	 Catch:{ Exception -> 0x0b01 }
        goto L_0x050a;
    L_0x0506:
        r7 = org.telegram.messenger.UserObject.getUserName(r11);	 Catch:{ Exception -> 0x0b01 }
    L_0x050a:
        if (r4 == 0) goto L_0x0524;
    L_0x050c:
        r4 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b01 }
        r4 = r4.size();	 Catch:{ Exception -> 0x0b01 }
        r8 = 1;
        if (r4 > r8) goto L_0x0524;
    L_0x0515:
        r4 = 0;
        r8 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r4);	 Catch:{ Exception -> 0x0b01 }
        if (r8 != 0) goto L_0x0524;
    L_0x051c:
        r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0b01 }
        if (r4 == 0) goto L_0x0521;
    L_0x0520:
        goto L_0x0524;
    L_0x0521:
        r4 = r7;
        r8 = 1;
        goto L_0x052e;
    L_0x0524:
        r4 = "AppName";
        r8 = NUM; // 0x7f0d00eb float:1.8742591E38 double:1.0531298936E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r8);	 Catch:{ Exception -> 0x0b01 }
        r8 = 0;
    L_0x052e:
        r10 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();	 Catch:{ Exception -> 0x0b01 }
        r14 = "";
        r20 = r7;
        r7 = 1;
        if (r10 <= r7) goto L_0x0571;
    L_0x0539:
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b01 }
        r10 = r10.size();	 Catch:{ Exception -> 0x0b01 }
        if (r10 != r7) goto L_0x0550;
    L_0x0541:
        r7 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r7 = org.telegram.messenger.UserConfig.getInstance(r7);	 Catch:{ Exception -> 0x0b01 }
        r7 = r7.getCurrentUser();	 Catch:{ Exception -> 0x0b01 }
        r7 = org.telegram.messenger.UserObject.getFirstName(r7);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0572;
    L_0x0550:
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r7.<init>();	 Catch:{ Exception -> 0x0b01 }
        r10 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r10 = org.telegram.messenger.UserConfig.getInstance(r10);	 Catch:{ Exception -> 0x0b01 }
        r10 = r10.getCurrentUser();	 Catch:{ Exception -> 0x0b01 }
        r10 = org.telegram.messenger.UserObject.getFirstName(r10);	 Catch:{ Exception -> 0x0b01 }
        r7.append(r10);	 Catch:{ Exception -> 0x0b01 }
        r10 = "・";
        r7.append(r10);	 Catch:{ Exception -> 0x0b01 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0572;
    L_0x0571:
        r7 = r14;
    L_0x0572:
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b01 }
        r10 = r10.size();	 Catch:{ Exception -> 0x0b01 }
        r21 = r1;
        r1 = 1;
        if (r10 != r1) goto L_0x058a;
    L_0x057d:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r10 = 23;
        if (r1 >= r10) goto L_0x0584;
    L_0x0583:
        goto L_0x058a;
    L_0x0584:
        r40 = r3;
        r39 = r15;
    L_0x0588:
        r15 = r7;
        goto L_0x05e5;
    L_0x058a:
        r1 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.size();	 Catch:{ Exception -> 0x0b01 }
        r10 = 1;
        if (r1 != r10) goto L_0x05ab;
    L_0x0593:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r1.<init>();	 Catch:{ Exception -> 0x0b01 }
        r1.append(r7);	 Catch:{ Exception -> 0x0b01 }
        r7 = "NewMessages";
        r10 = r12.total_unread_count;	 Catch:{ Exception -> 0x0b01 }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r10);	 Catch:{ Exception -> 0x0b01 }
        r1.append(r7);	 Catch:{ Exception -> 0x0b01 }
        r7 = r1.toString();	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0584;
    L_0x05ab:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r1.<init>();	 Catch:{ Exception -> 0x0b01 }
        r1.append(r7);	 Catch:{ Exception -> 0x0b01 }
        r7 = "NotificationMessagesPeopleDisplayOrder";
        r39 = r15;
        r10 = 2;
        r15 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0b01 }
        r10 = "NewMessages";
        r40 = r3;
        r3 = r12.total_unread_count;	 Catch:{ Exception -> 0x0b01 }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r10, r3);	 Catch:{ Exception -> 0x0b01 }
        r10 = 0;
        r15[r10] = r3;	 Catch:{ Exception -> 0x0b01 }
        r3 = "FromChats";
        r10 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b01 }
        r10 = r10.size();	 Catch:{ Exception -> 0x0b01 }
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r10);	 Catch:{ Exception -> 0x0b01 }
        r10 = 1;
        r15[r10] = r3;	 Catch:{ Exception -> 0x0b01 }
        r3 = NUM; // 0x7f0d0675 float:1.8745467E38 double:1.053130594E-314;
        r3 = org.telegram.messenger.LocaleController.formatString(r7, r3, r15);	 Catch:{ Exception -> 0x0b01 }
        r1.append(r3);	 Catch:{ Exception -> 0x0b01 }
        r7 = r1.toString();	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0588;
    L_0x05e5:
        r10 = new androidx.core.app.NotificationCompat$Builder;	 Catch:{ Exception -> 0x0b01 }
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b01 }
        r10.<init>(r1);	 Catch:{ Exception -> 0x0b01 }
        r10.setContentTitle(r4);	 Catch:{ Exception -> 0x0b01 }
        r1 = NUM; // 0x7var_b float:1.7945639E38 double:1.0529357614E-314;
        r10.setSmallIcon(r1);	 Catch:{ Exception -> 0x0b01 }
        r1 = 1;
        r10.setAutoCancel(r1);	 Catch:{ Exception -> 0x0b01 }
        r1 = r12.total_unread_count;	 Catch:{ Exception -> 0x0b01 }
        r10.setNumber(r1);	 Catch:{ Exception -> 0x0b01 }
        r10.setContentIntent(r2);	 Catch:{ Exception -> 0x0b01 }
        r1 = r12.notificationGroup;	 Catch:{ Exception -> 0x0b01 }
        r10.setGroup(r1);	 Catch:{ Exception -> 0x0b01 }
        r1 = 1;
        r10.setGroupSummary(r1);	 Catch:{ Exception -> 0x0b01 }
        r10.setShowWhen(r1);	 Catch:{ Exception -> 0x0b01 }
        r1 = r6.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.date;	 Catch:{ Exception -> 0x0b01 }
        r1 = (long) r1;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1 * r22;
        r10.setWhen(r1);	 Catch:{ Exception -> 0x0b01 }
        r1 = -15618822; // 0xfffffffffvar_acfa float:-1.936362E38 double:NaN;
        r10.setColor(r1);	 Catch:{ Exception -> 0x0b01 }
        r1 = "msg";
        r10.setCategory(r1);	 Catch:{ Exception -> 0x0b01 }
        if (r5 != 0) goto L_0x0648;
    L_0x0624:
        if (r11 == 0) goto L_0x0648;
    L_0x0626:
        r1 = r11.phone;	 Catch:{ Exception -> 0x0b01 }
        if (r1 == 0) goto L_0x0648;
    L_0x062a:
        r1 = r11.phone;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.length();	 Catch:{ Exception -> 0x0b01 }
        if (r1 <= 0) goto L_0x0648;
    L_0x0632:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r1.<init>();	 Catch:{ Exception -> 0x0b01 }
        r2 = "tel:+";
        r1.append(r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = r11.phone;	 Catch:{ Exception -> 0x0b01 }
        r1.append(r2);	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0b01 }
        r10.addPerson(r1);	 Catch:{ Exception -> 0x0b01 }
    L_0x0648:
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.size();	 Catch:{ Exception -> 0x0b01 }
        r2 = 1;
        if (r1 != r2) goto L_0x06ca;
    L_0x0651:
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0b01 }
        r3 = 0;
        r1 = r1.get(r3);	 Catch:{ Exception -> 0x0b01 }
        r1 = (org.telegram.messenger.MessageObject) r1;	 Catch:{ Exception -> 0x0b01 }
        r7 = new boolean[r2];	 Catch:{ Exception -> 0x0b01 }
        r2 = 0;
        r11 = r12.getStringForMessage(r1, r3, r7, r2);	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.silent;	 Catch:{ Exception -> 0x0b01 }
        if (r11 != 0) goto L_0x0668;
    L_0x0667:
        return;
    L_0x0668:
        if (r8 == 0) goto L_0x06b3;
    L_0x066a:
        if (r5 == 0) goto L_0x0682;
    L_0x066c:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r2.<init>();	 Catch:{ Exception -> 0x0b01 }
        r3 = " @ ";
        r2.append(r3);	 Catch:{ Exception -> 0x0b01 }
        r2.append(r4);	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b01 }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x06b4;
    L_0x0682:
        r2 = 0;
        r3 = r7[r2];	 Catch:{ Exception -> 0x0b01 }
        if (r3 == 0) goto L_0x069d;
    L_0x0687:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r2.<init>();	 Catch:{ Exception -> 0x0b01 }
        r2.append(r4);	 Catch:{ Exception -> 0x0b01 }
        r3 = ": ";
        r2.append(r3);	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b01 }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x06b4;
    L_0x069d:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r2.<init>();	 Catch:{ Exception -> 0x0b01 }
        r2.append(r4);	 Catch:{ Exception -> 0x0b01 }
        r3 = " ";
        r2.append(r3);	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b01 }
        r2 = r11.replace(r2, r14);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x06b4;
    L_0x06b3:
        r2 = r11;
    L_0x06b4:
        r10.setContentText(r2);	 Catch:{ Exception -> 0x0b01 }
        r3 = new androidx.core.app.NotificationCompat$BigTextStyle;	 Catch:{ Exception -> 0x0b01 }
        r3.<init>();	 Catch:{ Exception -> 0x0b01 }
        r3.bigText(r2);	 Catch:{ Exception -> 0x0b01 }
        r10.setStyle(r3);	 Catch:{ Exception -> 0x0b01 }
        r44 = r6;
        r43 = r9;
        r42 = r13;
        goto L_0x078a;
    L_0x06ca:
        r10.setContentText(r15);	 Catch:{ Exception -> 0x0b01 }
        r1 = new androidx.core.app.NotificationCompat$InboxStyle;	 Catch:{ Exception -> 0x0b01 }
        r1.<init>();	 Catch:{ Exception -> 0x0b01 }
        r1.setBigContentTitle(r4);	 Catch:{ Exception -> 0x0b01 }
        r2 = 10;
        r3 = r12.pushMessages;	 Catch:{ Exception -> 0x0b01 }
        r3 = r3.size();	 Catch:{ Exception -> 0x0b01 }
        r2 = java.lang.Math.min(r2, r3);	 Catch:{ Exception -> 0x0b01 }
        r3 = 1;
        r7 = new boolean[r3];	 Catch:{ Exception -> 0x0b01 }
        r3 = 0;
        r11 = 2;
        r38 = 0;
    L_0x06e8:
        if (r3 >= r2) goto L_0x077b;
    L_0x06ea:
        r41 = r2;
        r2 = r12.pushMessages;	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.get(r3);	 Catch:{ Exception -> 0x0b01 }
        r2 = (org.telegram.messenger.MessageObject) r2;	 Catch:{ Exception -> 0x0b01 }
        r44 = r6;
        r43 = r9;
        r42 = r13;
        r9 = 0;
        r13 = 0;
        r6 = r12.getStringForMessage(r2, r9, r7, r13);	 Catch:{ Exception -> 0x0b01 }
        if (r6 == 0) goto L_0x076b;
    L_0x0702:
        r9 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r9 = r9.date;	 Catch:{ Exception -> 0x0b01 }
        r13 = r19;
        if (r9 > r13) goto L_0x070b;
    L_0x070a:
        goto L_0x076d;
    L_0x070b:
        r9 = 2;
        if (r11 != r9) goto L_0x0714;
    L_0x070e:
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r11 = r2.silent;	 Catch:{ Exception -> 0x0b01 }
        r38 = r6;
    L_0x0714:
        r2 = r12.pushDialogs;	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0b01 }
        r9 = 1;
        if (r2 != r9) goto L_0x0767;
    L_0x071d:
        if (r8 == 0) goto L_0x0767;
    L_0x071f:
        if (r5 == 0) goto L_0x0737;
    L_0x0721:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r2.<init>();	 Catch:{ Exception -> 0x0b01 }
        r9 = " @ ";
        r2.append(r9);	 Catch:{ Exception -> 0x0b01 }
        r2.append(r4);	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b01 }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0767;
    L_0x0737:
        r2 = 0;
        r9 = r7[r2];	 Catch:{ Exception -> 0x0b01 }
        if (r9 == 0) goto L_0x0752;
    L_0x073c:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r2.<init>();	 Catch:{ Exception -> 0x0b01 }
        r2.append(r4);	 Catch:{ Exception -> 0x0b01 }
        r9 = ": ";
        r2.append(r9);	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b01 }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0767;
    L_0x0752:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r2.<init>();	 Catch:{ Exception -> 0x0b01 }
        r2.append(r4);	 Catch:{ Exception -> 0x0b01 }
        r9 = " ";
        r2.append(r9);	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b01 }
        r6 = r6.replace(r2, r14);	 Catch:{ Exception -> 0x0b01 }
    L_0x0767:
        r1.addLine(r6);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x076d;
    L_0x076b:
        r13 = r19;
    L_0x076d:
        r3 = r3 + 1;
        r19 = r13;
        r2 = r41;
        r13 = r42;
        r9 = r43;
        r6 = r44;
        goto L_0x06e8;
    L_0x077b:
        r44 = r6;
        r43 = r9;
        r42 = r13;
        r1.setSummaryText(r15);	 Catch:{ Exception -> 0x0b01 }
        r10.setStyle(r1);	 Catch:{ Exception -> 0x0b01 }
        r1 = r11;
        r11 = r38;
    L_0x078a:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x0b01 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b01 }
        r4 = org.telegram.messenger.NotificationDismissReceiver.class;
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x0b01 }
        r3 = "messageDate";
        r4 = r44;
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r5 = r5.date;	 Catch:{ Exception -> 0x0b01 }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0b01 }
        r3 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r5 = r43;
        r2.putExtra(r5, r3);	 Catch:{ Exception -> 0x0b01 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b01 }
        r6 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r7 = 1;
        r2 = android.app.PendingIntent.getBroadcast(r3, r7, r2, r6);	 Catch:{ Exception -> 0x0b01 }
        r10.setDeleteIntent(r2);	 Catch:{ Exception -> 0x0b01 }
        if (r42 == 0) goto L_0x07fd;
    L_0x07b3:
        r2 = org.telegram.messenger.ImageLoader.getInstance();	 Catch:{ Exception -> 0x0b01 }
        r3 = "50_50";
        r13 = r42;
        r7 = 0;
        r2 = r2.getImageFromMemory(r13, r7, r3);	 Catch:{ Exception -> 0x0b01 }
        if (r2 == 0) goto L_0x07ca;
    L_0x07c2:
        r2 = r2.getBitmap();	 Catch:{ Exception -> 0x0b01 }
        r10.setLargeIcon(r2);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x07fe;
    L_0x07ca:
        r2 = 1;
        r3 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r2);	 Catch:{ Throwable -> 0x07fe }
        r2 = r3.exists();	 Catch:{ Throwable -> 0x07fe }
        if (r2 == 0) goto L_0x07fe;
    L_0x07d5:
        r2 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r8 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x07fe }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x07fe }
        r2 = r2 / r8;
        r8 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x07fe }
        r8.<init>();	 Catch:{ Throwable -> 0x07fe }
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r9 >= 0) goto L_0x07ec;
    L_0x07ea:
        r2 = 1;
        goto L_0x07ed;
    L_0x07ec:
        r2 = (int) r2;	 Catch:{ Throwable -> 0x07fe }
    L_0x07ed:
        r8.inSampleSize = r2;	 Catch:{ Throwable -> 0x07fe }
        r2 = r3.getAbsolutePath();	 Catch:{ Throwable -> 0x07fe }
        r2 = android.graphics.BitmapFactory.decodeFile(r2, r8);	 Catch:{ Throwable -> 0x07fe }
        if (r2 == 0) goto L_0x07fe;
    L_0x07f9:
        r10.setLargeIcon(r2);	 Catch:{ Throwable -> 0x07fe }
        goto L_0x07fe;
    L_0x07fd:
        r7 = 0;
    L_0x07fe:
        r13 = r46;
        if (r13 == 0) goto L_0x0849;
    L_0x0802:
        r2 = 1;
        if (r1 != r2) goto L_0x0806;
    L_0x0805:
        goto L_0x0849;
    L_0x0806:
        if (r40 != 0) goto L_0x0815;
    L_0x0808:
        r2 = 0;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r3 = 26;
        if (r2 < r3) goto L_0x0856;
    L_0x0812:
        r2 = 1;
        r8 = 3;
        goto L_0x0858;
    L_0x0815:
        r3 = r40;
        r2 = 1;
        if (r3 == r2) goto L_0x083d;
    L_0x081a:
        r2 = 2;
        if (r3 != r2) goto L_0x081f;
    L_0x081d:
        r2 = 1;
        goto L_0x083d;
    L_0x081f:
        r2 = 4;
        if (r3 != r2) goto L_0x082f;
    L_0x0822:
        r2 = -2;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r3 = 26;
        if (r2 < r3) goto L_0x0856;
    L_0x082c:
        r2 = 1;
        r8 = 1;
        goto L_0x0858;
    L_0x082f:
        r2 = 5;
        if (r3 != r2) goto L_0x0856;
    L_0x0832:
        r2 = -1;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r3 = 26;
        if (r2 < r3) goto L_0x0856;
    L_0x083c:
        goto L_0x0853;
    L_0x083d:
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r3 = 26;
        if (r2 < r3) goto L_0x0856;
    L_0x0846:
        r2 = 1;
        r8 = 4;
        goto L_0x0858;
    L_0x0849:
        r2 = -1;
        r10.setPriority(r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r3 = 26;
        if (r2 < r3) goto L_0x0856;
    L_0x0853:
        r2 = 1;
        r8 = 2;
        goto L_0x0858;
    L_0x0856:
        r2 = 1;
        r8 = 0;
    L_0x0858:
        if (r1 == r2) goto L_0x0982;
    L_0x085a:
        if (r28 != 0) goto L_0x0982;
    L_0x085c:
        r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0b01 }
        if (r1 != 0) goto L_0x0862;
    L_0x0860:
        if (r24 == 0) goto L_0x0891;
    L_0x0862:
        r1 = r11.length();	 Catch:{ Exception -> 0x0b01 }
        r2 = 100;
        if (r1 <= r2) goto L_0x088e;
    L_0x086a:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b01 }
        r1.<init>();	 Catch:{ Exception -> 0x0b01 }
        r2 = 100;
        r3 = 0;
        r2 = r11.substring(r3, r2);	 Catch:{ Exception -> 0x0b01 }
        r3 = 10;
        r9 = 32;
        r2 = r2.replace(r3, r9);	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.trim();	 Catch:{ Exception -> 0x0b01 }
        r1.append(r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = "...";
        r1.append(r2);	 Catch:{ Exception -> 0x0b01 }
        r11 = r1.toString();	 Catch:{ Exception -> 0x0b01 }
    L_0x088e:
        r10.setTicker(r11);	 Catch:{ Exception -> 0x0b01 }
    L_0x0891:
        r1 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.isRecordingAudio();	 Catch:{ Exception -> 0x0b01 }
        if (r1 != 0) goto L_0x0915;
    L_0x089b:
        if (r39 == 0) goto L_0x0915;
    L_0x089d:
        r1 = "NoSound";
        r2 = r39;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0b01 }
        if (r1 != 0) goto L_0x0915;
    L_0x08a7:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r3 = 26;
        if (r1 < r3) goto L_0x08bd;
    L_0x08ad:
        r1 = r21;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0b01 }
        if (r1 == 0) goto L_0x08b8;
    L_0x08b5:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0916;
    L_0x08b8:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0916;
    L_0x08bd:
        r1 = r21;
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0b01 }
        if (r1 == 0) goto L_0x08cc;
    L_0x08c5:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0b01 }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0915;
    L_0x08cc:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r3 = 24;
        if (r1 < r3) goto L_0x090d;
    L_0x08d2:
        r1 = "file://";
        r1 = r2.startsWith(r1);	 Catch:{ Exception -> 0x0b01 }
        if (r1 == 0) goto L_0x090d;
    L_0x08da:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0b01 }
        r1 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1);	 Catch:{ Exception -> 0x0b01 }
        if (r1 != 0) goto L_0x090d;
    L_0x08e4:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0904 }
        r3 = "org.telegram.messenger.beta.provider";
        r9 = new java.io.File;	 Catch:{ Exception -> 0x0904 }
        r11 = "file://";
        r11 = r2.replace(r11, r14);	 Catch:{ Exception -> 0x0904 }
        r9.<init>(r11);	 Catch:{ Exception -> 0x0904 }
        r1 = androidx.core.content.FileProvider.getUriForFile(r1, r3, r9);	 Catch:{ Exception -> 0x0904 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0904 }
        r9 = "com.android.systemui";
        r11 = 1;
        r3.grantUriPermission(r9, r1, r11);	 Catch:{ Exception -> 0x0904 }
        r3 = 5;
        r10.setSound(r1, r3);	 Catch:{ Exception -> 0x0904 }
        goto L_0x0915;
    L_0x0904:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0915;
    L_0x090d:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = 5;
        r10.setSound(r1, r2);	 Catch:{ Exception -> 0x0b01 }
    L_0x0915:
        r1 = r7;
    L_0x0916:
        if (r29 == 0) goto L_0x0922;
    L_0x0918:
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r9 = r29;
        r10.setLights(r9, r2, r3);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0924;
    L_0x0922:
        r9 = r29;
    L_0x0924:
        r14 = r27;
        r2 = 2;
        if (r14 == r2) goto L_0x0970;
    L_0x0929:
        r2 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.isRecordingAudio();	 Catch:{ Exception -> 0x0b01 }
        if (r2 == 0) goto L_0x0935;
    L_0x0933:
        r2 = 2;
        goto L_0x0970;
    L_0x0935:
        r2 = 1;
        if (r14 != r2) goto L_0x094e;
    L_0x0938:
        r3 = 4;
        r3 = new long[r3];	 Catch:{ Exception -> 0x0b01 }
        r7 = 0;
        r21 = 0;
        r3[r7] = r21;	 Catch:{ Exception -> 0x0b01 }
        r27 = 100;
        r3[r2] = r27;	 Catch:{ Exception -> 0x0b01 }
        r2 = 2;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0b01 }
        r2 = 3;
        r3[r2] = r27;	 Catch:{ Exception -> 0x0b01 }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x097d;
    L_0x094e:
        if (r14 == 0) goto L_0x0968;
    L_0x0950:
        r2 = 4;
        if (r14 != r2) goto L_0x0954;
    L_0x0953:
        goto L_0x0968;
    L_0x0954:
        r2 = 3;
        if (r14 != r2) goto L_0x0966;
    L_0x0957:
        r2 = 2;
        r3 = new long[r2];	 Catch:{ Exception -> 0x0b01 }
        r2 = 0;
        r18 = 0;
        r3[r2] = r18;	 Catch:{ Exception -> 0x0b01 }
        r2 = 1;
        r3[r2] = r22;	 Catch:{ Exception -> 0x0b01 }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x097d;
    L_0x0966:
        r11 = r1;
        goto L_0x097f;
    L_0x0968:
        r2 = 2;
        r10.setDefaults(r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = 0;
        r3 = new long[r2];	 Catch:{ Exception -> 0x0b01 }
        goto L_0x097d;
    L_0x0970:
        r3 = new long[r2];	 Catch:{ Exception -> 0x0b01 }
        r2 = 0;
        r21 = 0;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0b01 }
        r2 = 1;
        r3[r2] = r21;	 Catch:{ Exception -> 0x0b01 }
        r10.setVibrate(r3);	 Catch:{ Exception -> 0x0b01 }
    L_0x097d:
        r11 = r1;
        r7 = r3;
    L_0x097f:
        r1 = 0;
        r3 = 1;
        goto L_0x0994;
    L_0x0982:
        r9 = r29;
        r1 = 2;
        r2 = new long[r1];	 Catch:{ Exception -> 0x0b01 }
        r1 = 0;
        r18 = 0;
        r2[r1] = r18;	 Catch:{ Exception -> 0x0b01 }
        r3 = 1;
        r2[r3] = r18;	 Catch:{ Exception -> 0x0b01 }
        r10.setVibrate(r2);	 Catch:{ Exception -> 0x0b01 }
        r11 = r7;
        r7 = r2;
    L_0x0994:
        r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1);	 Catch:{ Exception -> 0x0b01 }
        if (r2 != 0) goto L_0x0a71;
    L_0x099a:
        r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0b01 }
        if (r1 != 0) goto L_0x0a71;
    L_0x099e:
        r1 = r4.getDialogId();	 Catch:{ Exception -> 0x0b01 }
        r16 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r14 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1));
        if (r14 != 0) goto L_0x0a71;
    L_0x09a9:
        r1 = r4.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.reply_markup;	 Catch:{ Exception -> 0x0b01 }
        if (r1 == 0) goto L_0x0a71;
    L_0x09af:
        r1 = r4.messageOwner;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.reply_markup;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.rows;	 Catch:{ Exception -> 0x0b01 }
        r2 = r1.size();	 Catch:{ Exception -> 0x0b01 }
        r14 = 0;
        r16 = 0;
    L_0x09bc:
        if (r14 >= r2) goto L_0x0a69;
    L_0x09be:
        r17 = r1.get(r14);	 Catch:{ Exception -> 0x0b01 }
        r3 = r17;
        r3 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r3;	 Catch:{ Exception -> 0x0b01 }
        r6 = r3.buttons;	 Catch:{ Exception -> 0x0b01 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0b01 }
        r21 = r1;
        r1 = 0;
    L_0x09cf:
        if (r1 >= r6) goto L_0x0a4d;
    L_0x09d1:
        r22 = r2;
        r2 = r3.buttons;	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.get(r1);	 Catch:{ Exception -> 0x0b01 }
        r2 = (org.telegram.tgnet.TLRPC.KeyboardButton) r2;	 Catch:{ Exception -> 0x0b01 }
        r23 = r3;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;	 Catch:{ Exception -> 0x0b01 }
        if (r3 == 0) goto L_0x0a2d;
    L_0x09e1:
        r3 = new android.content.Intent;	 Catch:{ Exception -> 0x0b01 }
        r24 = r6;
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b01 }
        r13 = org.telegram.messenger.NotificationCallbackReceiver.class;
        r3.<init>(r6, r13);	 Catch:{ Exception -> 0x0b01 }
        r6 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r3.putExtra(r5, r6);	 Catch:{ Exception -> 0x0b01 }
        r6 = "did";
        r13 = r8;
        r29 = r9;
        r8 = r36;
        r3.putExtra(r6, r8);	 Catch:{ Exception -> 0x0b01 }
        r6 = r2.data;	 Catch:{ Exception -> 0x0b01 }
        if (r6 == 0) goto L_0x0a09;
    L_0x09ff:
        r6 = "data";
        r26 = r15;
        r15 = r2.data;	 Catch:{ Exception -> 0x0b01 }
        r3.putExtra(r6, r15);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0a0b;
    L_0x0a09:
        r26 = r15;
    L_0x0a0b:
        r6 = "mid";
        r15 = r4.getId();	 Catch:{ Exception -> 0x0b01 }
        r3.putExtra(r6, r15);	 Catch:{ Exception -> 0x0b01 }
        r2 = r2.text;	 Catch:{ Exception -> 0x0b01 }
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b01 }
        r15 = r12.lastButtonId;	 Catch:{ Exception -> 0x0b01 }
        r44 = r4;
        r4 = r15 + 1;
        r12.lastButtonId = r4;	 Catch:{ Exception -> 0x0b01 }
        r4 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r3 = android.app.PendingIntent.getBroadcast(r6, r15, r3, r4);	 Catch:{ Exception -> 0x0b01 }
        r4 = 0;
        r10.addAction(r4, r2, r3);	 Catch:{ Exception -> 0x0b01 }
        r16 = 1;
        goto L_0x0a39;
    L_0x0a2d:
        r44 = r4;
        r24 = r6;
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        r4 = 0;
    L_0x0a39:
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
        goto L_0x09cf;
    L_0x0a4d:
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
        goto L_0x09bc;
    L_0x0a69:
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        goto L_0x0a7b;
    L_0x0a71:
        r13 = r8;
        r29 = r9;
        r26 = r15;
        r8 = r36;
        r4 = 0;
        r16 = 0;
    L_0x0a7b:
        if (r16 != 0) goto L_0x0ad6;
    L_0x0a7d:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r2 = 24;
        if (r1 >= r2) goto L_0x0ad6;
    L_0x0a83:
        r1 = org.telegram.messenger.SharedConfig.passcodeHash;	 Catch:{ Exception -> 0x0b01 }
        r1 = r1.length();	 Catch:{ Exception -> 0x0b01 }
        if (r1 != 0) goto L_0x0ad6;
    L_0x0a8b:
        r1 = r45.hasMessagesToReply();	 Catch:{ Exception -> 0x0b01 }
        if (r1 == 0) goto L_0x0ad6;
    L_0x0a91:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0b01 }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b01 }
        r3 = org.telegram.messenger.PopupReplyReceiver.class;
        r1.<init>(r2, r3);	 Catch:{ Exception -> 0x0b01 }
        r2 = r12.currentAccount;	 Catch:{ Exception -> 0x0b01 }
        r1.putExtra(r5, r2);	 Catch:{ Exception -> 0x0b01 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r3 = 19;
        if (r2 > r3) goto L_0x0abe;
    L_0x0aa5:
        r2 = NUM; // 0x7var_fd float:1.7945091E38 double:1.052935628E-314;
        r3 = "Reply";
        r4 = NUM; // 0x7f0d0875 float:1.8746506E38 double:1.053130847E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0b01 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b01 }
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r6 = 2;
        r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5);	 Catch:{ Exception -> 0x0b01 }
        r10.addAction(r2, r3, r1);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0ad6;
    L_0x0abe:
        r2 = NUM; // 0x7var_fc float:1.794509E38 double:1.0529356275E-314;
        r3 = "Reply";
        r4 = NUM; // 0x7f0d0875 float:1.8746506E38 double:1.053130847E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0b01 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0b01 }
        r5 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r6 = 2;
        r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5);	 Catch:{ Exception -> 0x0b01 }
        r10.addAction(r2, r3, r1);	 Catch:{ Exception -> 0x0b01 }
    L_0x0ad6:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01 }
        r2 = 26;
        if (r1 < r2) goto L_0x0af5;
    L_0x0adc:
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
        r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0b01 }
        r13.setChannelId(r1);	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0af6;
    L_0x0af5:
        r13 = r10;
    L_0x0af6:
        r1 = r46;
        r7 = r26;
        r12.showExtraNotifications(r13, r1, r7);	 Catch:{ Exception -> 0x0b01 }
        r45.scheduleNotificationRepeat();	 Catch:{ Exception -> 0x0b01 }
        goto L_0x0b06;
    L_0x0b01:
        r0 = move-exception;
    L_0x0b02:
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0b06:
        return;
    L_0x0b07:
        r45.dismissNotification();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:133:0x02f2  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02a6  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x039f  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0401  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0758  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0747  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x077c  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0776  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0808  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x07d5  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x084b  */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x0829  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x08f9  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x08ef  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x08fc  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x091a  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0939  */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x09eb  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a3e A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0a67 A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0a78 A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0a72 A:{SYNTHETIC, Splitter:B:376:0x0a72} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0289  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02a6  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02f2  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0300 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x039f  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03b3  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03e8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0401  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0747  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0758  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0776  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x077c  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x07d5  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0808  */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x0829  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x084b  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x08ef  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x08f9  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x08fc  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x090a  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x091a  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0939  */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x09eb  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a3e A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0a67 A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0a72 A:{SYNTHETIC, Splitter:B:376:0x0a72} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0a78 A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0289  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02f2  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02a6  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0300 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x039f  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03b3  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03e8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0401  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0758  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0747  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x077c  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0776  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0808  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x07d5  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x084b  */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x0829  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x08f9  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x08ef  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x08fc  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x090a  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x091a  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0939  */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x09eb  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a3e A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0a67 A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0a78 A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0a72 A:{SYNTHETIC, Splitter:B:376:0x0a72} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0289  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02a6  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02f2  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0300 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x039f  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03b3  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03e8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0401  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0747  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0758  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0776  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x077c  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x07d5  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0808  */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x0829  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x084b  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x08ef  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x08f9  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x08fc  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x090a  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x091a  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0939  */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x09eb  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0a3e A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0a67 A:{Catch:{ JSONException -> 0x0a90 }} */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0a72 A:{SYNTHETIC, Splitter:B:376:0x0a72} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0a78 A:{Catch:{ JSONException -> 0x0a90 }} */
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
        if (r13 >= r12) goto L_0x0aa7;
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
        if (r11 == 0) goto L_0x01fe;
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
        if (r11 <= 0) goto L_0x0176;
    L_0x0108:
        r27 = r13;
        r13 = r0.currentAccount;
        r13 = org.telegram.messenger.MessagesController.getInstance(r13);
        r28 = r7;
        r7 = java.lang.Integer.valueOf(r11);
        r7 = r13.getUser(r7);
        if (r7 != 0) goto L_0x014a;
    L_0x011c:
        r13 = r4.isFcmMessage();
        if (r13 == 0) goto L_0x0129;
    L_0x0122:
        r4 = r4.localName;
        r29 = r6;
        r6 = r18;
        goto L_0x0171;
    L_0x0129:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0141;
    L_0x012d:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not found user to show dialog notification ";
        r2.append(r3);
        r2.append(r11);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
    L_0x0141:
        r27 = r1;
        r3 = r6;
        r36 = r9;
        r7 = r28;
        goto L_0x023a;
    L_0x014a:
        r4 = org.telegram.messenger.UserObject.getUserName(r7);
        r13 = r7.photo;
        if (r13 == 0) goto L_0x0169;
    L_0x0152:
        r13 = r13.photo_small;
        if (r13 == 0) goto L_0x0169;
    L_0x0156:
        r29 = r6;
        r30 = r7;
        r6 = r13.volume_id;
        r31 = (r6 > r24 ? 1 : (r6 == r24 ? 0 : -1));
        if (r31 == 0) goto L_0x016d;
    L_0x0160:
        r6 = r13.local_id;
        if (r6 == 0) goto L_0x016d;
    L_0x0164:
        r6 = r18;
        r7 = r30;
        goto L_0x0172;
    L_0x0169:
        r29 = r6;
        r30 = r7;
    L_0x016d:
        r6 = r18;
        r7 = r30;
    L_0x0171:
        r13 = 0;
    L_0x0172:
        r18 = 0;
        goto L_0x027d;
    L_0x0176:
        r29 = r6;
        r28 = r7;
        r27 = r13;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r7 = -r11;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getChat(r7);
        if (r6 != 0) goto L_0x01c4;
    L_0x018d:
        r7 = r4.isFcmMessage();
        if (r7 == 0) goto L_0x01aa;
    L_0x0193:
        r7 = r4.isMegagroup();
        r13 = r4.localName;
        r4 = r4.localChannel;
        r32 = r4;
        r31 = r6;
        r30 = r7;
    L_0x01a1:
        r4 = r13;
        r6 = r18;
        r7 = 0;
        r13 = 0;
        r18 = 0;
        goto L_0x0283;
    L_0x01aa:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0232;
    L_0x01ae:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not found chat to show dialog notification ";
        r2.append(r3);
        r2.append(r11);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
        goto L_0x0232;
    L_0x01c4:
        r4 = r6.megagroup;
        r7 = org.telegram.messenger.ChatObject.isChannel(r6);
        if (r7 == 0) goto L_0x01d2;
    L_0x01cc:
        r7 = r6.megagroup;
        if (r7 != 0) goto L_0x01d2;
    L_0x01d0:
        r7 = 1;
        goto L_0x01d3;
    L_0x01d2:
        r7 = 0;
    L_0x01d3:
        r13 = r6.title;
        r30 = r4;
        r4 = r6.photo;
        if (r4 == 0) goto L_0x01f9;
    L_0x01db:
        r4 = r4.photo_small;
        if (r4 == 0) goto L_0x01f9;
    L_0x01df:
        r31 = r6;
        r32 = r7;
        r6 = r4.volume_id;
        r33 = (r6 > r24 ? 1 : (r6 == r24 ? 0 : -1));
        if (r33 == 0) goto L_0x01a1;
    L_0x01e9:
        r6 = r4.local_id;
        if (r6 == 0) goto L_0x01a1;
    L_0x01ed:
        r6 = r18;
        r7 = 0;
        r18 = 0;
        r59 = r13;
        r13 = r4;
        r4 = r59;
        goto L_0x0283;
    L_0x01f9:
        r31 = r6;
        r32 = r7;
        goto L_0x01a1;
    L_0x01fe:
        r29 = r6;
        r28 = r7;
        r26 = r13;
        r6 = globalSecretChatId;
        r4 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x026c;
    L_0x020a:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r6 = java.lang.Integer.valueOf(r3);
        r4 = r4.getEncryptedChat(r6);
        if (r4 != 0) goto L_0x023f;
    L_0x021a:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0232;
    L_0x021e:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "not found secret chat to show dialog notification ";
        r2.append(r4);
        r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
    L_0x0232:
        r27 = r1;
        r36 = r9;
        r7 = r28;
        r3 = r29;
    L_0x023a:
        r2 = 26;
        r14 = 0;
        goto L_0x0a92;
    L_0x023f:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r7 = r4.user_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getUser(r7);
        if (r6 != 0) goto L_0x026d;
    L_0x0251:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x0232;
    L_0x0255:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not found secret chat user to show dialog notification ";
        r2.append(r3);
        r3 = r4.user_id;
        r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.w(r2);
        goto L_0x0232;
    L_0x026c:
        r6 = 0;
    L_0x026d:
        r4 = NUM; // 0x7f0d08df float:1.874672E38 double:1.0531308996E-314;
        r7 = "SecretChatName";
        r4 = org.telegram.messenger.LocaleController.getString(r7, r4);
        r7 = r6;
        r6 = 0;
        r13 = 0;
        r18 = 0;
        r27 = 0;
    L_0x027d:
        r30 = 0;
        r31 = 0;
        r32 = 0;
    L_0x0283:
        r33 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r18);
        if (r33 != 0) goto L_0x0295;
    L_0x0289:
        r18 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r18 == 0) goto L_0x028e;
    L_0x028d:
        goto L_0x0295;
    L_0x028e:
        r18 = r12;
        r12 = r27;
        r27 = r1;
        goto L_0x02a4;
    L_0x0295:
        r4 = NUM; // 0x7f0d00eb float:1.8742591E38 double:1.0531298936E-314;
        r13 = "AppName";
        r4 = org.telegram.messenger.LocaleController.getString(r13, r4);
        r27 = r1;
        r18 = r12;
        r12 = 0;
        r13 = 0;
    L_0x02a4:
        if (r13 == 0) goto L_0x02f2;
    L_0x02a6:
        r1 = 1;
        r34 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r1);
        r1 = org.telegram.messenger.ImageLoader.getInstance();
        r35 = r7;
        r7 = "50_50";
        r36 = r9;
        r9 = 0;
        r1 = r1.getImageFromMemory(r13, r9, r7);
        if (r1 == 0) goto L_0x02c1;
    L_0x02bc:
        r1 = r1.getBitmap();
        goto L_0x02fa;
    L_0x02c1:
        r1 = android.os.Build.VERSION.SDK_INT;
        r7 = 28;
        if (r1 >= r7) goto L_0x02f0;
    L_0x02c7:
        r1 = r34.exists();	 Catch:{ Throwable -> 0x02f0 }
        if (r1 == 0) goto L_0x02f0;
    L_0x02cd:
        r1 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r7 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);	 Catch:{ Throwable -> 0x02f0 }
        r7 = (float) r7;	 Catch:{ Throwable -> 0x02f0 }
        r1 = r1 / r7;
        r7 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x02f0 }
        r7.<init>();	 Catch:{ Throwable -> 0x02f0 }
        r17 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r17 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1));
        if (r17 >= 0) goto L_0x02e4;
    L_0x02e2:
        r1 = 1;
        goto L_0x02e5;
    L_0x02e4:
        r1 = (int) r1;	 Catch:{ Throwable -> 0x02f0 }
    L_0x02e5:
        r7.inSampleSize = r1;	 Catch:{ Throwable -> 0x02f0 }
        r1 = r34.getAbsolutePath();	 Catch:{ Throwable -> 0x02f0 }
        r1 = android.graphics.BitmapFactory.decodeFile(r1, r7);	 Catch:{ Throwable -> 0x02f0 }
        goto L_0x02fa;
    L_0x02f0:
        r1 = r9;
        goto L_0x02fa;
    L_0x02f2:
        r35 = r7;
        r36 = r9;
        r9 = 0;
        r1 = r9;
        r34 = r1;
    L_0x02fa:
        r9 = "max_id";
        r7 = "currentAccount";
        if (r32 == 0) goto L_0x0302;
    L_0x0300:
        if (r30 == 0) goto L_0x0389;
    L_0x0302:
        if (r12 == 0) goto L_0x0389;
    L_0x0304:
        r37 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r37 != 0) goto L_0x0389;
    L_0x0308:
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
        r12 = NUM; // 0x7f0d0875 float:1.8746506E38 double:1.053130847E-314;
        r13 = "Reply";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        r2.setLabel(r12);
        r2 = r2.build();
        if (r11 >= 0) goto L_0x035d;
    L_0x034b:
        r13 = 1;
        r12 = new java.lang.Object[r13];
        r13 = 0;
        r12[r13] = r4;
        r13 = "ReplyToGroup";
        r41 = r8;
        r8 = NUM; // 0x7f0d0876 float:1.8746508E38 double:1.0531308477E-314;
        r8 = org.telegram.messenger.LocaleController.formatString(r13, r8, r12);
        goto L_0x036e;
    L_0x035d:
        r41 = r8;
        r8 = NUM; // 0x7f0d0877 float:1.874651E38 double:1.053130848E-314;
        r12 = 1;
        r13 = new java.lang.Object[r12];
        r12 = 0;
        r13[r12] = r4;
        r12 = "ReplyToUser";
        r8 = org.telegram.messenger.LocaleController.formatString(r12, r8, r13);
    L_0x036e:
        r12 = new androidx.core.app.NotificationCompat$Action$Builder;
        r13 = NUM; // 0x7var_a float:1.7945182E38 double:1.0529356503E-314;
        r12.<init>(r13, r8, r1);
        r1 = 1;
        r12.setAllowGeneratedReplies(r1);
        r12.setSemanticAction(r1);
        r12.addRemoteInput(r2);
        r1 = 0;
        r12.setShowsUserInterface(r1);
        r8 = r12.build();
        goto L_0x0395;
    L_0x0389:
        r39 = r1;
        r40 = r2;
        r41 = r8;
        r38 = r12;
        r37 = r13;
        r1 = 0;
        r8 = 0;
    L_0x0395:
        r2 = r0.pushDialogs;
        r2 = r2.get(r14);
        r2 = (java.lang.Integer) r2;
        if (r2 != 0) goto L_0x03a3;
    L_0x039f:
        r2 = java.lang.Integer.valueOf(r1);
    L_0x03a3:
        r1 = r2.intValue();
        r2 = r10.size();
        r1 = java.lang.Math.max(r1, r2);
        r2 = 2;
        r12 = 1;
        if (r1 <= r12) goto L_0x03cd;
    L_0x03b3:
        r13 = android.os.Build.VERSION.SDK_INT;
        r12 = 28;
        if (r13 < r12) goto L_0x03ba;
    L_0x03b9:
        goto L_0x03cd;
    L_0x03ba:
        r12 = new java.lang.Object[r2];
        r13 = 0;
        r12[r13] = r4;
        r1 = java.lang.Integer.valueOf(r1);
        r13 = 1;
        r12[r13] = r1;
        r1 = "%1$s (%2$d)";
        r1 = java.lang.String.format(r1, r12);
        goto L_0x03ce;
    L_0x03cd:
        r1 = r4;
    L_0x03ce:
        r12 = new androidx.core.app.NotificationCompat$MessagingStyle;
        r13 = "";
        r12.<init>(r13);
        r2 = android.os.Build.VERSION.SDK_INT;
        r42 = r4;
        r4 = 28;
        if (r2 < r4) goto L_0x03e1;
    L_0x03dd:
        if (r11 >= 0) goto L_0x03e4;
    L_0x03df:
        if (r32 != 0) goto L_0x03e4;
    L_0x03e1:
        r12.setConversationTitle(r1);
    L_0x03e4:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r4) goto L_0x03ef;
    L_0x03e8:
        if (r32 != 0) goto L_0x03ed;
    L_0x03ea:
        if (r11 >= 0) goto L_0x03ed;
    L_0x03ec:
        goto L_0x03ef;
    L_0x03ed:
        r1 = 0;
        goto L_0x03f0;
    L_0x03ef:
        r1 = 1;
    L_0x03f0:
        r12.setGroupConversation(r1);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = 1;
        r4 = new java.lang.String[r2];
        r43 = r9;
        r9 = new boolean[r2];
        if (r6 == 0) goto L_0x040b;
    L_0x0401:
        r16 = new org.json.JSONArray;
        r16.<init>();
        r44 = r6;
        r6 = r16;
        goto L_0x040e;
    L_0x040b:
        r44 = r6;
        r6 = 0;
    L_0x040e:
        r16 = r10.size();
        r45 = r16 + -1;
        r2 = r45;
        r46 = 0;
        r47 = 0;
    L_0x041a:
        r48 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r2 < 0) goto L_0x0707;
    L_0x041e:
        r45 = r10.get(r2);
        r50 = r10;
        r10 = r45;
        r10 = (org.telegram.messenger.MessageObject) r10;
        r45 = r8;
        r8 = r0.getShortStringForMessage(r10, r4, r9);
        if (r8 != 0) goto L_0x0474;
    L_0x0430:
        r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r8 == 0) goto L_0x0464;
    L_0x0434:
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
        goto L_0x046e;
    L_0x0464:
        r52 = r2;
        r51 = r7;
        r56 = r1;
        r53 = r3;
        r55 = r4;
    L_0x046e:
        r54 = r9;
        r57 = r14;
        goto L_0x06f3;
    L_0x0474:
        r52 = r2;
        r51 = r7;
        r7 = r3;
        r2 = r1.length();
        if (r2 <= 0) goto L_0x0484;
    L_0x047f:
        r2 = "\n\n";
        r1.append(r2);
    L_0x0484:
        r2 = 0;
        r3 = r4[r2];
        if (r3 == 0) goto L_0x049f;
    L_0x0489:
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
        goto L_0x04a4;
    L_0x049f:
        r53 = r7;
        r1.append(r8);
    L_0x04a4:
        if (r11 <= 0) goto L_0x04a8;
    L_0x04a6:
        r2 = (long) r11;
        goto L_0x04b5;
    L_0x04a8:
        if (r32 == 0) goto L_0x04ad;
    L_0x04aa:
        r2 = -r11;
    L_0x04ab:
        r2 = (long) r2;
        goto L_0x04b5;
    L_0x04ad:
        if (r11 >= 0) goto L_0x04b4;
    L_0x04af:
        r2 = r10.getFromId();
        goto L_0x04ab;
    L_0x04b4:
        r2 = r14;
    L_0x04b5:
        r7 = r5.get(r2);
        r7 = (androidx.core.app.Person) r7;
        if (r7 != 0) goto L_0x0553;
    L_0x04bd:
        r7 = new androidx.core.app.Person$Builder;
        r7.<init>();
        r22 = 0;
        r54 = r4[r22];
        if (r54 != 0) goto L_0x04cc;
    L_0x04c8:
        r55 = r4;
        r4 = r13;
        goto L_0x04d2;
    L_0x04cc:
        r54 = r4[r22];
        r55 = r4;
        r4 = r54;
    L_0x04d2:
        r7.setName(r4);
        r4 = r9[r22];
        if (r4 == 0) goto L_0x0545;
    L_0x04d9:
        if (r11 == 0) goto L_0x0545;
    L_0x04db:
        r4 = android.os.Build.VERSION.SDK_INT;
        r54 = r9;
        r9 = 28;
        if (r4 < r9) goto L_0x0542;
    L_0x04e3:
        if (r11 > 0) goto L_0x0538;
    L_0x04e5:
        if (r32 == 0) goto L_0x04e8;
    L_0x04e7:
        goto L_0x0538;
    L_0x04e8:
        if (r11 >= 0) goto L_0x0532;
    L_0x04ea:
        r4 = r10.getFromId();
        r9 = r0.currentAccount;
        r9 = org.telegram.messenger.MessagesController.getInstance(r9);
        r56 = r1;
        r1 = java.lang.Integer.valueOf(r4);
        r1 = r9.getUser(r1);
        if (r1 != 0) goto L_0x0516;
    L_0x0500:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r1 = r1.getUserSync(r4);
        if (r1 == 0) goto L_0x0516;
    L_0x050c:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r9 = 1;
        r4.putUser(r1, r9);
    L_0x0516:
        if (r1 == 0) goto L_0x0534;
    L_0x0518:
        r1 = r1.photo;
        if (r1 == 0) goto L_0x0534;
    L_0x051c:
        r1 = r1.photo_small;
        if (r1 == 0) goto L_0x0534;
    L_0x0520:
        r57 = r14;
        r14 = r1.volume_id;
        r4 = (r14 > r24 ? 1 : (r14 == r24 ? 0 : -1));
        if (r4 == 0) goto L_0x0536;
    L_0x0528:
        r4 = r1.local_id;
        if (r4 == 0) goto L_0x0536;
    L_0x052c:
        r4 = 1;
        r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r4);
        goto L_0x053e;
    L_0x0532:
        r56 = r1;
    L_0x0534:
        r57 = r14;
    L_0x0536:
        r1 = 0;
        goto L_0x053e;
    L_0x0538:
        r56 = r1;
        r57 = r14;
        r1 = r34;
    L_0x053e:
        r0.loadRoundAvatar(r1, r7);
        goto L_0x054b;
    L_0x0542:
        r56 = r1;
        goto L_0x0549;
    L_0x0545:
        r56 = r1;
        r54 = r9;
    L_0x0549:
        r57 = r14;
    L_0x054b:
        r7 = r7.build();
        r5.put(r2, r7);
        goto L_0x055b;
    L_0x0553:
        r56 = r1;
        r55 = r4;
        r54 = r9;
        r57 = r14;
    L_0x055b:
        if (r11 == 0) goto L_0x068e;
    L_0x055d:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 28;
        if (r1 < r2) goto L_0x0645;
    L_0x0563:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r2 = "activity";
        r1 = r1.getSystemService(r2);
        r1 = (android.app.ActivityManager) r1;
        r1 = r1.isLowRamDevice();
        if (r1 != 0) goto L_0x0645;
    L_0x0573:
        r1 = r10.type;
        r2 = 1;
        if (r1 == r2) goto L_0x058b;
    L_0x0578:
        r1 = r10.isSticker();
        if (r1 == 0) goto L_0x057f;
    L_0x057e:
        goto L_0x058b;
    L_0x057f:
        r1 = r10.messageOwner;
        r1 = r1.date;
        r1 = (long) r1;
        r1 = r1 * r48;
        r12.addMessage(r8, r1, r7);
        goto L_0x064f;
    L_0x058b:
        r1 = r10.messageOwner;
        r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1);
        r2 = new androidx.core.app.NotificationCompat$MessagingStyle$Message;
        r3 = r10.messageOwner;
        r3 = r3.date;
        r3 = (long) r3;
        r3 = r3 * r48;
        r2.<init>(r8, r3, r7);
        r3 = r10.isSticker();
        if (r3 == 0) goto L_0x05a6;
    L_0x05a3:
        r3 = "image/webp";
        goto L_0x05a8;
    L_0x05a6:
        r3 = "image/jpeg";
    L_0x05a8:
        r4 = r1.exists();
        if (r4 == 0) goto L_0x05b7;
    L_0x05ae:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r9 = "org.telegram.messenger.beta.provider";
        r1 = androidx.core.content.FileProvider.getUriForFile(r4, r9, r1);
        goto L_0x060b;
    L_0x05b7:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r9 = r1.getName();
        r4 = r4.isLoadingFile(r9);
        if (r4 == 0) goto L_0x060a;
    L_0x05c7:
        r4 = new android.net.Uri$Builder;
        r4.<init>();
        r9 = "content";
        r4 = r4.scheme(r9);
        r9 = "org.telegram.messenger.beta.notification_image_provider";
        r4 = r4.authority(r9);
        r9 = "msg_media_raw";
        r4 = r4.appendPath(r9);
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r14 = r0.currentAccount;
        r9.append(r14);
        r9.append(r13);
        r9 = r9.toString();
        r4 = r4.appendPath(r9);
        r9 = r1.getName();
        r4 = r4.appendPath(r9);
        r1 = r1.getAbsolutePath();
        r9 = "final_path";
        r1 = r4.appendQueryParameter(r9, r1);
        r1 = r1.build();
        goto L_0x060b;
    L_0x060a:
        r1 = 0;
    L_0x060b:
        if (r1 == 0) goto L_0x063a;
    L_0x060d:
        r2.setData(r3, r1);
        r12.addMessage(r2);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = "com.android.systemui";
        r4 = 1;
        r2.grantUriPermission(r3, r1, r4);
        r2 = new org.telegram.messenger.-$$Lambda$NotificationsController$hROO1aIM4eduzMv5uJ3U4yL97Bo;
        r2.<init>(r1);
        r3 = 20000; // 0x4e20 float:2.8026E-41 double:9.8813E-320;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r3);
        r1 = r10.caption;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x064f;
    L_0x062d:
        r1 = r10.caption;
        r2 = r10.messageOwner;
        r2 = r2.date;
        r2 = (long) r2;
        r2 = r2 * r48;
        r12.addMessage(r1, r2, r7);
        goto L_0x064f;
    L_0x063a:
        r1 = r10.messageOwner;
        r1 = r1.date;
        r1 = (long) r1;
        r1 = r1 * r48;
        r12.addMessage(r8, r1, r7);
        goto L_0x064f;
    L_0x0645:
        r1 = r10.messageOwner;
        r1 = r1.date;
        r1 = (long) r1;
        r1 = r1 * r48;
        r12.addMessage(r8, r1, r7);
    L_0x064f:
        r1 = r10.isVoice();
        if (r1 == 0) goto L_0x0698;
    L_0x0655:
        r1 = r12.getMessages();
        r2 = r1.isEmpty();
        if (r2 != 0) goto L_0x0698;
    L_0x065f:
        r2 = r10.messageOwner;
        r2 = org.telegram.messenger.FileLoader.getPathToMessage(r2);
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 24;
        if (r3 < r4) goto L_0x0676;
    L_0x066b:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0674 }
        r4 = "org.telegram.messenger.beta.provider";
        r2 = androidx.core.content.FileProvider.getUriForFile(r3, r4, r2);	 Catch:{ Exception -> 0x0674 }
        goto L_0x067a;
    L_0x0674:
        r2 = 0;
        goto L_0x067a;
    L_0x0676:
        r2 = android.net.Uri.fromFile(r2);
    L_0x067a:
        if (r2 == 0) goto L_0x0698;
    L_0x067c:
        r3 = r1.size();
        r4 = 1;
        r3 = r3 - r4;
        r1 = r1.get(r3);
        r1 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r1;
        r3 = "audio/ogg";
        r1.setData(r3, r2);
        goto L_0x0698;
    L_0x068e:
        r1 = r10.messageOwner;
        r1 = r1.date;
        r1 = (long) r1;
        r1 = r1 * r48;
        r12.addMessage(r8, r1, r7);
    L_0x0698:
        if (r6 == 0) goto L_0x06dc;
    L_0x069a:
        r1 = new org.json.JSONObject;	 Catch:{ JSONException -> 0x06db }
        r1.<init>();	 Catch:{ JSONException -> 0x06db }
        r2 = "text";
        r1.put(r2, r8);	 Catch:{ JSONException -> 0x06db }
        r2 = "date";
        r3 = r10.messageOwner;	 Catch:{ JSONException -> 0x06db }
        r3 = r3.date;	 Catch:{ JSONException -> 0x06db }
        r1.put(r2, r3);	 Catch:{ JSONException -> 0x06db }
        r2 = r10.isFromUser();	 Catch:{ JSONException -> 0x06db }
        if (r2 == 0) goto L_0x06d7;
    L_0x06b3:
        if (r11 >= 0) goto L_0x06d7;
    L_0x06b5:
        r2 = r0.currentAccount;	 Catch:{ JSONException -> 0x06db }
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);	 Catch:{ JSONException -> 0x06db }
        r3 = r10.getFromId();	 Catch:{ JSONException -> 0x06db }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ JSONException -> 0x06db }
        r2 = r2.getUser(r3);	 Catch:{ JSONException -> 0x06db }
        if (r2 == 0) goto L_0x06d7;
    L_0x06c9:
        r3 = "fname";
        r4 = r2.first_name;	 Catch:{ JSONException -> 0x06db }
        r1.put(r3, r4);	 Catch:{ JSONException -> 0x06db }
        r3 = "lname";
        r2 = r2.last_name;	 Catch:{ JSONException -> 0x06db }
        r1.put(r3, r2);	 Catch:{ JSONException -> 0x06db }
    L_0x06d7:
        r6.put(r1);	 Catch:{ JSONException -> 0x06db }
        goto L_0x06dc;
    L_0x06dc:
        r1 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r3 = (r57 > r1 ? 1 : (r57 == r1 ? 0 : -1));
        if (r3 != 0) goto L_0x06f3;
    L_0x06e3:
        r1 = r10.messageOwner;
        r1 = r1.reply_markup;
        if (r1 == 0) goto L_0x06f3;
    L_0x06e9:
        r1 = r1.rows;
        r2 = r10.getId();
        r46 = r1;
        r47 = r2;
    L_0x06f3:
        r2 = r52 + -1;
        r8 = r45;
        r10 = r50;
        r7 = r51;
        r3 = r53;
        r9 = r54;
        r4 = r55;
        r1 = r56;
        r14 = r57;
        goto L_0x041a;
    L_0x0707:
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
        if (r11 == 0) goto L_0x0758;
    L_0x0747:
        if (r11 <= 0) goto L_0x074f;
    L_0x0749:
        r2 = "userId";
        r1.putExtra(r2, r11);
        goto L_0x0755;
    L_0x074f:
        r2 = -r11;
        r3 = "chatId";
        r1.putExtra(r3, r2);
    L_0x0755:
        r3 = r53;
        goto L_0x075f;
    L_0x0758:
        r2 = "encId";
        r3 = r53;
        r1.putExtra(r2, r3);
    L_0x075f:
        r2 = r0.currentAccount;
        r4 = r51;
        r1.putExtra(r4, r2);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r7 = 0;
        r1 = android.app.PendingIntent.getActivity(r2, r7, r1, r5);
        r2 = new androidx.core.app.NotificationCompat$WearableExtender;
        r2.<init>();
        if (r45 == 0) goto L_0x077c;
    L_0x0776:
        r8 = r45;
        r2.addAction(r8);
        goto L_0x077e;
    L_0x077c:
        r8 = r45;
    L_0x077e:
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
        r14 = NUM; // 0x7var_a4 float:1.794543E38 double:1.0529357105E-314;
        r15 = NUM; // 0x7f0d057f float:1.8744968E38 double:1.0531304727E-314;
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
        if (r11 == 0) goto L_0x0808;
    L_0x07d5:
        if (r11 <= 0) goto L_0x07ef;
    L_0x07d7:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r6 = "tguser";
        r3.append(r6);
        r3.append(r11);
        r3.append(r5);
        r3.append(r7);
        r3 = r3.toString();
        goto L_0x0827;
    L_0x07ef:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r6 = "tgchat";
        r3.append(r6);
        r6 = -r11;
        r3.append(r6);
        r3.append(r5);
        r3.append(r7);
        r3 = r3.toString();
        goto L_0x0827;
    L_0x0808:
        r14 = globalSecretChatId;
        r6 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1));
        if (r6 == 0) goto L_0x0826;
    L_0x080e:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r14 = "tgenc";
        r6.append(r14);
        r6.append(r3);
        r6.append(r5);
        r6.append(r7);
        r3 = r6.toString();
        goto L_0x0827;
    L_0x0826:
        r3 = 0;
    L_0x0827:
        if (r3 == 0) goto L_0x084b;
    L_0x0829:
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
        goto L_0x084d;
    L_0x084b:
        r3 = r61;
    L_0x084d:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r14 = "tgaccount";
        r6.append(r14);
        r14 = r0.currentAccount;
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);
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
        r7 = NUM; // 0x7var_b float:1.7945639E38 double:1.0529357614E-314;
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
        if (r36 == 0) goto L_0x08f9;
    L_0x08ef:
        r1 = r0.notificationGroup;
        r3.setGroup(r1);
        r1 = 1;
        r3.setGroupAlertBehavior(r1);
        goto L_0x08fa;
    L_0x08f9:
        r1 = 1;
    L_0x08fa:
        if (r8 == 0) goto L_0x08ff;
    L_0x08fc:
        r3.addAction(r8);
    L_0x08ff:
        r3.addAction(r4);
        r2 = r0.pushDialogs;
        r2 = r2.size();
        if (r2 != r1) goto L_0x0916;
    L_0x090a:
        r2 = android.text.TextUtils.isEmpty(r63);
        if (r2 != 0) goto L_0x0916;
    L_0x0910:
        r2 = r63;
        r3.setSubText(r2);
        goto L_0x0918;
    L_0x0916:
        r2 = r63;
    L_0x0918:
        if (r11 != 0) goto L_0x091d;
    L_0x091a:
        r3.setLocalOnly(r1);
    L_0x091d:
        if (r39 == 0) goto L_0x092a;
    L_0x091f:
        r4 = android.os.Build.VERSION.SDK_INT;
        r6 = 28;
        if (r4 >= r6) goto L_0x092a;
    L_0x0925:
        r4 = r39;
        r3.setLargeIcon(r4);
    L_0x092a:
        r4 = 0;
        r6 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r4);
        if (r6 != 0) goto L_0x09be;
    L_0x0931:
        r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r4 != 0) goto L_0x09be;
    L_0x0935:
        r4 = r46;
        if (r4 == 0) goto L_0x09be;
    L_0x0939:
        r6 = r4.size();
        r7 = 0;
    L_0x093e:
        if (r7 >= r6) goto L_0x09be;
    L_0x0940:
        r8 = r4.get(r7);
        r8 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r8;
        r12 = r8.buttons;
        r12 = r12.size();
        r13 = 0;
    L_0x094d:
        if (r13 >= r12) goto L_0x09af;
    L_0x094f:
        r14 = r8.buttons;
        r14 = r14.get(r13);
        r14 = (org.telegram.tgnet.TLRPC.KeyboardButton) r14;
        r15 = r14 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
        if (r15 == 0) goto L_0x099e;
    L_0x095b:
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
        if (r1 == 0) goto L_0x097c;
    L_0x0974:
        r51 = r2;
        r2 = "data";
        r15.putExtra(r2, r1);
        goto L_0x097e;
    L_0x097c:
        r51 = r2;
    L_0x097e:
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
        goto L_0x09a5;
    L_0x099e:
        r34 = r4;
        r33 = r47;
        r4 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r14 = 0;
    L_0x09a5:
        r13 = r13 + 1;
        r2 = r63;
        r47 = r33;
        r4 = r34;
        r1 = 1;
        goto L_0x094d;
    L_0x09af:
        r34 = r4;
        r33 = r47;
        r4 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r14 = 0;
        r7 = r7 + 1;
        r2 = r63;
        r4 = r34;
        r1 = 1;
        goto L_0x093e;
    L_0x09be:
        r14 = 0;
        if (r31 != 0) goto L_0x09e5;
    L_0x09c1:
        if (r35 == 0) goto L_0x09e5;
    L_0x09c3:
        r6 = r35;
        r1 = r6.phone;
        if (r1 == 0) goto L_0x09e5;
    L_0x09c9:
        r1 = r1.length();
        if (r1 <= 0) goto L_0x09e5;
    L_0x09cf:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "tel:+";
        r1.append(r2);
        r2 = r6.phone;
        r1.append(r2);
        r1 = r1.toString();
        r3.addPerson(r1);
    L_0x09e5:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 26;
        if (r1 < r2) goto L_0x09fa;
    L_0x09eb:
        if (r36 == 0) goto L_0x09f3;
    L_0x09ed:
        r1 = OTHER_NOTIFICATIONS_CHANNEL;
        r3.setChannelId(r1);
        goto L_0x09fa;
    L_0x09f3:
        r1 = r27.getChannelId();
        r3.setChannelId(r1);
    L_0x09fa:
        r1 = new org.telegram.messenger.NotificationsController$1NotificationHolder;
        r4 = r40.intValue();
        r3 = r3.build();
        r1.<init>(r4, r3);
        r3 = r29;
        r3.add(r1);
        r1 = r0.wearNotificationsIds;
        r4 = r40;
        r1.put(r9, r4);
        if (r11 == 0) goto L_0x0a90;
    L_0x0a15:
        if (r44 == 0) goto L_0x0a90;
    L_0x0a17:
        r1 = "reply";
        r4 = r38;
        r6 = r44;
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a90 }
        r1 = "name";
        r6.put(r1, r5);	 Catch:{ JSONException -> 0x0a90 }
        r1 = r41;
        r4 = r43;
        r6.put(r4, r1);	 Catch:{ JSONException -> 0x0a90 }
        r1 = "max_date";
        r4 = r18;
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a90 }
        r1 = "id";
        r4 = java.lang.Math.abs(r11);	 Catch:{ JSONException -> 0x0a90 }
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a90 }
        if (r37 == 0) goto L_0x0a65;
    L_0x0a3e:
        r1 = "photo";
        r4 = new java.lang.StringBuilder;	 Catch:{ JSONException -> 0x0a90 }
        r4.<init>();	 Catch:{ JSONException -> 0x0a90 }
        r13 = r37;
        r5 = r13.dc_id;	 Catch:{ JSONException -> 0x0a90 }
        r4.append(r5);	 Catch:{ JSONException -> 0x0a90 }
        r5 = r25;
        r4.append(r5);	 Catch:{ JSONException -> 0x0a90 }
        r7 = r13.volume_id;	 Catch:{ JSONException -> 0x0a90 }
        r4.append(r7);	 Catch:{ JSONException -> 0x0a90 }
        r4.append(r5);	 Catch:{ JSONException -> 0x0a90 }
        r7 = r13.secret;	 Catch:{ JSONException -> 0x0a90 }
        r4.append(r7);	 Catch:{ JSONException -> 0x0a90 }
        r4 = r4.toString();	 Catch:{ JSONException -> 0x0a90 }
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a90 }
    L_0x0a65:
        if (r24 == 0) goto L_0x0a6e;
    L_0x0a67:
        r1 = "msgs";
        r4 = r24;
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a90 }
    L_0x0a6e:
        r1 = "type";
        if (r11 <= 0) goto L_0x0a78;
    L_0x0a72:
        r4 = "user";
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a90 }
        goto L_0x0a8a;
    L_0x0a78:
        if (r11 >= 0) goto L_0x0a8a;
    L_0x0a7a:
        if (r32 != 0) goto L_0x0a85;
    L_0x0a7c:
        if (r30 == 0) goto L_0x0a7f;
    L_0x0a7e:
        goto L_0x0a85;
    L_0x0a7f:
        r4 = "group";
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a90 }
        goto L_0x0a8a;
    L_0x0a85:
        r4 = "channel";
        r6.put(r1, r4);	 Catch:{ JSONException -> 0x0a90 }
    L_0x0a8a:
        r7 = r28;
        r7.put(r6);	 Catch:{ JSONException -> 0x0a92 }
        goto L_0x0a92;
    L_0x0a90:
        r7 = r28;
    L_0x0a92:
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
    L_0x0aa7:
        r27 = r1;
        r23 = r5;
        r3 = r6;
        r36 = r9;
        r14 = 0;
        if (r36 == 0) goto L_0x0ad5;
    L_0x0ab1:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0acb;
    L_0x0ab5:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "show summary with id ";
        r1.append(r2);
        r2 = r0.notificationId;
        r1.append(r2);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0acb:
        r1 = notificationManager;
        r2 = r0.notificationId;
        r4 = r27;
        r1.notify(r2, r4);
        goto L_0x0adc;
    L_0x0ad5:
        r1 = notificationManager;
        r2 = r0.notificationId;
        r1.cancel(r2);
    L_0x0adc:
        r1 = r3.size();
        r2 = 0;
    L_0x0ae1:
        if (r2 >= r1) goto L_0x0aef;
    L_0x0ae3:
        r4 = r3.get(r2);
        r4 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r4;
        r4.call();
        r2 = r2 + 1;
        goto L_0x0ae1;
    L_0x0aef:
        r1 = r23.size();
        if (r14 >= r1) goto L_0x0b23;
    L_0x0af5:
        r1 = r23;
        r2 = r1.valueAt(r14);
        r2 = (java.lang.Integer) r2;
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x0b15;
    L_0x0b01:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "cancel notification id ";
        r3.append(r4);
        r3.append(r2);
        r3 = r3.toString();
        org.telegram.messenger.FileLog.w(r3);
    L_0x0b15:
        r3 = notificationManager;
        r2 = r2.intValue();
        r3.cancel(r2);
        r14 = r14 + 1;
        r23 = r1;
        goto L_0x0aef;
    L_0x0b23:
        if (r7 == 0) goto L_0x0b4d;
    L_0x0b25:
        r1 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0b4d }
        r1.<init>();	 Catch:{ Exception -> 0x0b4d }
        r2 = "id";
        r3 = r0.currentAccount;	 Catch:{ Exception -> 0x0b4d }
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);	 Catch:{ Exception -> 0x0b4d }
        r3 = r3.getClientUserId();	 Catch:{ Exception -> 0x0b4d }
        r1.put(r2, r3);	 Catch:{ Exception -> 0x0b4d }
        r2 = "n";
        r1.put(r2, r7);	 Catch:{ Exception -> 0x0b4d }
        r2 = "/notify";
        r1 = r1.toString();	 Catch:{ Exception -> 0x0b4d }
        r1 = r1.getBytes();	 Catch:{ Exception -> 0x0b4d }
        r3 = "remote_notifications";
        org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r2, r1, r3);	 Catch:{ Exception -> 0x0b4d }
    L_0x0b4d:
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
        Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        Dialog dialog = (Dialog) MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(j);
        String str = "notify2_";
        StringBuilder stringBuilder;
        if (i == 4) {
            if (getInstance(this.currentAccount).isGlobalNotificationsEnabled(j)) {
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
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j, 0);
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
        int i = 0;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        int i2 = (int) j;
        if (i2 != 0) {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            TL_account_updateNotifySettings tL_account_updateNotifySettings = new TL_account_updateNotifySettings();
            tL_account_updateNotifySettings.settings = new TL_inputPeerNotifySettings();
            TL_inputPeerNotifySettings tL_inputPeerNotifySettings = tL_account_updateNotifySettings.settings;
            tL_inputPeerNotifySettings.flags |= 1;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("preview_");
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
            ((TL_inputNotifyPeer) tL_account_updateNotifySettings.peer).peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i2);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateNotifySettings, -$$Lambda$NotificationsController$KyQqllEdy_fdmMCr6frsin2S3Cs.INSTANCE);
        }
    }

    public void updateServerNotificationsSettings(int i) {
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateNotifySettings, -$$Lambda$NotificationsController$WV8JpQrNXdfWVJfPV9wKTUTuLBk.INSTANCE);
    }

    public boolean isGlobalNotificationsEnabled(long j) {
        int i;
        int i2 = (int) j;
        if (i2 < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i2));
            i = (!ChatObject.isChannel(chat) || chat.megagroup) ? 0 : 2;
        } else {
            i = 1;
        }
        return isGlobalNotificationsEnabled(i);
    }

    public boolean isGlobalNotificationsEnabled(int i) {
        return MessagesController.getNotificationsSettings(this.currentAccount).getInt(getGlobalNotificationsKey(i), 0) < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
    }

    public void setGlobalNotificationsEnabled(int i, int i2) {
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt(getGlobalNotificationsKey(i), i2).commit();
        getInstance(this.currentAccount).updateServerNotificationsSettings(i);
    }
}
