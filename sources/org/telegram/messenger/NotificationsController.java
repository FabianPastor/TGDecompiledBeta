package org.telegram.messenger;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
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
import java.util.concurrent.CountDownLatch;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
    /* access modifiers changed from: private */
    public static NotificationManagerCompat notificationManager;
    private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    private static NotificationManager systemNotificationManager;
    private AlarmManager alarmManager;
    private ArrayList<MessageObject> delayedPushMessages = new ArrayList<>();
    private LongSparseArray<MessageObject> fcmRandomMessagesDict = new LongSparseArray<>();
    private boolean inChatSoundEnabled;
    private int lastBadgeCount = -1;
    private int lastButtonId = 5000;
    private int lastOnlineFromOtherDevice = 0;
    private long lastSoundOutPlay;
    private long lastSoundPlay;
    private LongSparseArray<Integer> lastWearNotifiedMessageId = new LongSparseArray<>();
    private String launcherClassName;
    private Runnable notificationDelayRunnable;
    private PowerManager.WakeLock notificationDelayWakelock;
    private String notificationGroup;
    private int notificationId = (this.currentAccount + 1);
    private boolean notifyCheck = false;
    private long opened_dialog_id = 0;
    private int personal_count = 0;
    public ArrayList<MessageObject> popupMessages = new ArrayList<>();
    public ArrayList<MessageObject> popupReplyMessages = new ArrayList<>();
    private LongSparseArray<Integer> pushDialogs = new LongSparseArray<>();
    private LongSparseArray<Integer> pushDialogsOverrideMention = new LongSparseArray<>();
    private ArrayList<MessageObject> pushMessages = new ArrayList<>();
    private LongSparseArray<MessageObject> pushMessagesDict = new LongSparseArray<>();
    public boolean showBadgeMessages;
    public boolean showBadgeMuted;
    public boolean showBadgeNumber;
    private LongSparseArray<Point> smartNotificationsDialogs = new LongSparseArray<>();
    private int soundIn;
    private boolean soundInLoaded;
    private int soundOut;
    private boolean soundOutLoaded;
    private SoundPool soundPool;
    private int soundRecord;
    private boolean soundRecordLoaded;
    private int total_unread_count = 0;
    private LongSparseArray<Integer> wearNotificationsIds = new LongSparseArray<>();

    static /* synthetic */ void lambda$updateServerNotificationsSettings$36(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$37(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public String getGlobalNotificationsKey(int i) {
        return i == 0 ? "EnableGroup2" : i == 1 ? "EnableAll2" : "EnableChannel2";
    }

    static {
        notificationManager = null;
        systemNotificationManager = null;
        if (Build.VERSION.SDK_INT >= 26 && ApplicationLoader.applicationContext != null) {
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
        StringBuilder sb = new StringBuilder();
        sb.append("messages");
        int i2 = this.currentAccount;
        sb.append(i2 == 0 ? "" : Integer.valueOf(i2));
        this.notificationGroup = sb.toString();
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
            FileLog.e((Throwable) e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService("alarm");
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        try {
            this.notificationDelayWakelock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "telegram:notification_delay_lock");
            this.notificationDelayWakelock.setReferenceCounted(false);
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        this.notificationDelayRunnable = new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$new$0$NotificationsController();
            }
        };
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
            FileLog.e((Throwable) e);
        }
    }

    public static void checkOtherNotificationsChannel() {
        SharedPreferences sharedPreferences;
        if (Build.VERSION.SDK_INT >= 26) {
            if (OTHER_NOTIFICATIONS_CHANNEL == null) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                OTHER_NOTIFICATIONS_CHANNEL = sharedPreferences.getString("OtherKey", "Other3");
            } else {
                sharedPreferences = null;
            }
            NotificationChannel notificationChannel = systemNotificationManager.getNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
            if (notificationChannel != null && notificationChannel.getImportance() == 0) {
                systemNotificationManager.deleteNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
                OTHER_NOTIFICATIONS_CHANNEL = null;
                notificationChannel = null;
            }
            if (OTHER_NOTIFICATIONS_CHANNEL == null) {
                if (sharedPreferences == null) {
                    sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                }
                OTHER_NOTIFICATIONS_CHANNEL = "Other" + Utilities.random.nextLong();
                sharedPreferences.edit().putString("OtherKey", OTHER_NOTIFICATIONS_CHANNEL).commit();
            }
            if (notificationChannel == null) {
                NotificationChannel notificationChannel2 = new NotificationChannel(OTHER_NOTIFICATIONS_CHANNEL, "Other", 3);
                notificationChannel2.enableLights(false);
                notificationChannel2.enableVibration(false);
                notificationChannel2.setSound((Uri) null, (AudioAttributes) null);
                systemNotificationManager.createNotificationChannel(notificationChannel2);
            }
        }
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$cleanup$1$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$cleanup$1$NotificationsController() {
        this.opened_dialog_id = 0;
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
            FileLog.e((Throwable) e);
        }
        dismissNotification();
        setBadge(getTotalAllUnreadCount());
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        edit.clear();
        edit.commit();
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                String str = this.currentAccount + "channel";
                List<NotificationChannel> notificationChannels = systemNotificationManager.getNotificationChannels();
                int size = notificationChannels.size();
                for (int i = 0; i < size; i++) {
                    String id = notificationChannels.get(i).getId();
                    if (id.startsWith(str)) {
                        systemNotificationManager.deleteNotificationChannel(id);
                    }
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
        notificationsQueue.postRunnable(new Runnable(j) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationsController.this.lambda$setOpenedDialogId$2$NotificationsController(this.f$1);
            }
        });
    }

    public void setLastOnlineFromOtherDevice(int i) {
        notificationsQueue.postRunnable(new Runnable(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationsController.this.lambda$setLastOnlineFromOtherDevice$3$NotificationsController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setLastOnlineFromOtherDevice$3$NotificationsController(int i) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set last online from other device = " + i);
        }
        this.lastOnlineFromOtherDevice = i;
    }

    public void removeNotificationsForDialog(long j) {
        processReadMessages((SparseLongArray) null, j, 0, Integer.MAX_VALUE, false);
        LongSparseArray longSparseArray = new LongSparseArray();
        longSparseArray.put(j, 0);
        processDialogsUpdateRead(longSparseArray);
    }

    public boolean hasMessagesToReply() {
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            TLRPC.Message message = messageObject.messageOwner;
            if ((!message.mentioned || !(message.action instanceof TLRPC.TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$forceShowPopupForReply$5$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$forceShowPopupForReply$5$NotificationsController() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            TLRPC.Message message = messageObject.messageOwner;
            if ((!message.mentioned || !(message.action instanceof TLRPC.TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
                arrayList.add(0, messageObject);
            }
        }
        if (!arrayList.isEmpty() && !AndroidUtilities.needShowPasscode() && !SharedConfig.isWaitingForPasscodeEnter) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$4$NotificationsController(this.f$1);
                }
            });
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
        notificationsQueue.postRunnable(new Runnable(sparseArray, new ArrayList(0)) {
            private final /* synthetic */ SparseArray f$1;
            private final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$8$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$8$NotificationsController(SparseArray sparseArray, ArrayList arrayList) {
        Integer num;
        SparseArray sparseArray2 = sparseArray;
        ArrayList arrayList2 = arrayList;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
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
                MessageObject messageObject = this.pushMessagesDict.get(intValue);
                if (messageObject != null) {
                    long dialogId = messageObject.getDialogId();
                    Integer num2 = this.pushDialogs.get(dialogId);
                    if (num2 == null) {
                        num2 = 0;
                    }
                    Integer valueOf = Integer.valueOf(num2.intValue() - 1);
                    if (valueOf.intValue() <= 0) {
                        this.smartNotificationsDialogs.remove(dialogId);
                        num = 0;
                    } else {
                        num = valueOf;
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
                SparseArray sparseArray3 = sparseArray;
            }
            i2++;
            sparseArray2 = sparseArray;
        }
        boolean z = true;
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList2) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$6$NotificationsController(this.f$1);
                }
            });
        }
        if (i != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            }
            AndroidUtilities.runOnUIThread(new Runnable(this.pushDialogs.size()) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$7$NotificationsController(this.f$1);
                }
            });
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
        notificationsQueue.postRunnable(new Runnable(sparseIntArray, new ArrayList(0)) {
            private final /* synthetic */ SparseIntArray f$1;
            private final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$11$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$11$NotificationsController(SparseIntArray sparseIntArray, ArrayList arrayList) {
        boolean z;
        Integer num;
        SparseIntArray sparseIntArray2 = sparseIntArray;
        ArrayList arrayList2 = arrayList;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        int i2 = 0;
        int i3 = 0;
        while (true) {
            z = true;
            if (i3 >= sparseIntArray.size()) {
                break;
            }
            int keyAt = sparseIntArray2.keyAt(i3);
            long j = (long) (-keyAt);
            int i4 = sparseIntArray2.get(keyAt);
            Integer num2 = this.pushDialogs.get(j);
            if (num2 == null) {
                num2 = i2;
            }
            Integer num3 = num2;
            int i5 = 0;
            while (i5 < this.pushMessages.size()) {
                MessageObject messageObject = this.pushMessages.get(i5);
                if (messageObject.getDialogId() != j || messageObject.getId() > i4) {
                    num = i2;
                } else {
                    num = i2;
                    this.pushMessagesDict.remove(messageObject.getIdWithChannel());
                    this.delayedPushMessages.remove(messageObject);
                    this.pushMessages.remove(messageObject);
                    i5--;
                    if (isPersonalMessage(messageObject)) {
                        this.personal_count--;
                    }
                    arrayList2.add(messageObject);
                    num3 = Integer.valueOf(num3.intValue() - 1);
                }
                i5++;
                i2 = num;
            }
            Integer num4 = i2;
            if (num3.intValue() <= 0) {
                this.smartNotificationsDialogs.remove(j);
                num3 = num4;
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
            i3++;
            i2 = num4;
        }
        if (arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList2) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$9$NotificationsController(this.f$1);
                }
            });
        }
        if (i != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            }
            AndroidUtilities.runOnUIThread(new Runnable(this.pushDialogs.size()) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$10$NotificationsController(this.f$1);
                }
            });
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
        notificationsQueue.postRunnable(new Runnable(sparseLongArray, new ArrayList(0), j, i2, i, z) {
            private final /* synthetic */ SparseLongArray f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ long f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ boolean f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                NotificationsController.this.lambda$processReadMessages$13$NotificationsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00bb, code lost:
        r6 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processReadMessages$13$NotificationsController(org.telegram.messenger.support.SparseLongArray r19, java.util.ArrayList r20, long r21, int r23, int r24, boolean r25) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r23
            r4 = r24
            r7 = 1
            if (r1 == 0) goto L_0x007a
            r8 = 0
        L_0x000e:
            int r9 = r19.size()
            if (r8 >= r9) goto L_0x007a
            int r9 = r1.keyAt(r8)
            long r10 = r1.get(r9)
            r12 = 0
        L_0x001d:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.pushMessages
            int r13 = r13.size()
            if (r12 >= r13) goto L_0x0077
            java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.pushMessages
            java.lang.Object r13 = r13.get(r12)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            org.telegram.tgnet.TLRPC$Message r14 = r13.messageOwner
            boolean r14 = r14.from_scheduled
            if (r14 != 0) goto L_0x0075
            long r14 = r13.getDialogId()
            long r5 = (long) r9
            int r17 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
            if (r17 != 0) goto L_0x0075
            int r5 = r13.getId()
            int r6 = (int) r10
            if (r5 > r6) goto L_0x0075
            boolean r5 = r0.isPersonalMessage(r13)
            if (r5 == 0) goto L_0x004e
            int r5 = r0.personal_count
            int r5 = r5 - r7
            r0.personal_count = r5
        L_0x004e:
            r2.add(r13)
            int r5 = r13.getId()
            long r5 = (long) r5
            org.telegram.tgnet.TLRPC$Message r14 = r13.messageOwner
            org.telegram.tgnet.TLRPC$Peer r14 = r14.to_id
            int r14 = r14.channel_id
            if (r14 == 0) goto L_0x0064
            long r14 = (long) r14
            r16 = 32
            long r14 = r14 << r16
            long r5 = r5 | r14
        L_0x0064:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r14 = r0.pushMessagesDict
            r14.remove(r5)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.delayedPushMessages
            r5.remove(r13)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.pushMessages
            r5.remove(r12)
            int r12 = r12 + -1
        L_0x0075:
            int r12 = r12 + r7
            goto L_0x001d
        L_0x0077:
            int r8 = r8 + 1
            goto L_0x000e
        L_0x007a:
            r5 = 0
            int r1 = (r21 > r5 ? 1 : (r21 == r5 ? 0 : -1))
            if (r1 == 0) goto L_0x00f7
            if (r3 != 0) goto L_0x0084
            if (r4 == 0) goto L_0x00f7
        L_0x0084:
            r1 = 0
        L_0x0085:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.pushMessages
            int r5 = r5.size()
            if (r1 >= r5) goto L_0x00f7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.pushMessages
            java.lang.Object r5 = r5.get(r1)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            long r8 = r5.getDialogId()
            int r6 = (r8 > r21 ? 1 : (r8 == r21 ? 0 : -1))
            if (r6 != 0) goto L_0x00f3
            if (r4 == 0) goto L_0x00a7
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            int r6 = r6.date
            if (r6 > r4) goto L_0x00bb
        L_0x00a5:
            r6 = 1
            goto L_0x00bc
        L_0x00a7:
            if (r25 != 0) goto L_0x00b2
            int r6 = r5.getId()
            if (r6 <= r3) goto L_0x00a5
            if (r3 >= 0) goto L_0x00bb
            goto L_0x00a5
        L_0x00b2:
            int r6 = r5.getId()
            if (r6 == r3) goto L_0x00a5
            if (r3 >= 0) goto L_0x00bb
            goto L_0x00a5
        L_0x00bb:
            r6 = 0
        L_0x00bc:
            if (r6 == 0) goto L_0x00f3
            boolean r6 = r0.isPersonalMessage(r5)
            if (r6 == 0) goto L_0x00c9
            int r6 = r0.personal_count
            int r6 = r6 - r7
            r0.personal_count = r6
        L_0x00c9:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.pushMessages
            r6.remove(r1)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.delayedPushMessages
            r6.remove(r5)
            r2.add(r5)
            int r6 = r5.getId()
            long r8 = (long) r6
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.to_id
            int r5 = r5.channel_id
            if (r5 == 0) goto L_0x00e9
            long r5 = (long) r5
            r10 = 32
            long r5 = r5 << r10
            long r8 = r8 | r5
            goto L_0x00eb
        L_0x00e9:
            r10 = 32
        L_0x00eb:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r5 = r0.pushMessagesDict
            r5.remove(r8)
            int r1 = r1 + -1
            goto L_0x00f5
        L_0x00f3:
            r10 = 32
        L_0x00f5:
            int r1 = r1 + r7
            goto L_0x0085
        L_0x00f7:
            boolean r1 = r20.isEmpty()
            if (r1 != 0) goto L_0x0105
            org.telegram.messenger.-$$Lambda$NotificationsController$uwXUA8kYkjmDHBUM6M6MDaJprzI r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$uwXUA8kYkjmDHBUM6M6MDaJprzI
            r1.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x0105:
            return
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

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004f, code lost:
        if (r5 == 2) goto L_0x0051;
     */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0065  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int addToPopupMessages(java.util.ArrayList<org.telegram.messenger.MessageObject> r3, org.telegram.messenger.MessageObject r4, int r5, long r6, boolean r8, android.content.SharedPreferences r9) {
        /*
            r2 = this;
            r0 = 0
            if (r5 == 0) goto L_0x0051
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r1 = "custom_"
            r5.append(r1)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            boolean r5 = r9.getBoolean(r5, r0)
            if (r5 == 0) goto L_0x0030
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r1 = "popup_"
            r5.append(r1)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            int r5 = r9.getInt(r5, r0)
            goto L_0x0031
        L_0x0030:
            r5 = 0
        L_0x0031:
            if (r5 != 0) goto L_0x0049
            if (r8 == 0) goto L_0x003c
            java.lang.String r5 = "popupChannel"
            int r5 = r9.getInt(r5, r0)
            goto L_0x0052
        L_0x003c:
            int r5 = (int) r6
            if (r5 >= 0) goto L_0x0042
            java.lang.String r5 = "popupGroup"
            goto L_0x0044
        L_0x0042:
            java.lang.String r5 = "popupAll"
        L_0x0044:
            int r5 = r9.getInt(r5, r0)
            goto L_0x0052
        L_0x0049:
            r6 = 1
            if (r5 != r6) goto L_0x004e
            r5 = 3
            goto L_0x0052
        L_0x004e:
            r6 = 2
            if (r5 != r6) goto L_0x0052
        L_0x0051:
            r5 = 0
        L_0x0052:
            if (r5 == 0) goto L_0x0063
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.to_id
            int r6 = r6.channel_id
            if (r6 == 0) goto L_0x0063
            boolean r6 = r4.isMegagroup()
            if (r6 != 0) goto L_0x0063
            r5 = 0
        L_0x0063:
            if (r5 == 0) goto L_0x0068
            r3.add(r0, r4)
        L_0x0068:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.addToPopupMessages(java.util.ArrayList, org.telegram.messenger.MessageObject, int, long, boolean, android.content.SharedPreferences):int");
    }

    public void processNewMessages(ArrayList<MessageObject> arrayList, boolean z, boolean z2, CountDownLatch countDownLatch) {
        if (!arrayList.isEmpty()) {
            notificationsQueue.postRunnable(new Runnable(arrayList, new ArrayList(0), z2, z, countDownLatch) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ boolean f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ CountDownLatch f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    NotificationsController.this.lambda$processNewMessages$16$NotificationsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        } else if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ee  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processNewMessages$16$NotificationsController(java.util.ArrayList r31, java.util.ArrayList r32, boolean r33, boolean r34, java.util.concurrent.CountDownLatch r35) {
        /*
            r30 = this;
            r8 = r30
            r9 = r31
            android.util.LongSparseArray r10 = new android.util.LongSparseArray
            r10.<init>()
            org.telegram.messenger.AccountInstance r0 = r30.getAccountInstance()
            android.content.SharedPreferences r11 = r0.getNotificationsSettings()
            r12 = 1
            java.lang.String r0 = "PinnedMessages"
            boolean r13 = r11.getBoolean(r0, r12)
            r0 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
        L_0x0020:
            int r1 = r31.size()
            if (r15 >= r1) goto L_0x01df
            java.lang.Object r1 = r9.get(r15)
            r7 = r1
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            if (r1 == 0) goto L_0x0047
            boolean r4 = r1.silent
            if (r4 == 0) goto L_0x0047
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp
            if (r4 != 0) goto L_0x003f
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined
            if (r1 == 0) goto L_0x0047
        L_0x003f:
            r26 = r0
            r22 = r13
            r21 = r15
            goto L_0x0118
        L_0x0047:
            int r1 = r7.getId()
            long r4 = (long) r1
            boolean r1 = r7.isFcmMessage()
            r19 = 0
            if (r1 == 0) goto L_0x005d
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            r21 = r15
            long r14 = r1.random_id
            r22 = r13
            goto L_0x0063
        L_0x005d:
            r21 = r15
            r22 = r13
            r14 = r19
        L_0x0063:
            long r12 = r7.getDialogId()
            int r6 = (int) r12
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0079
            long r2 = (long) r1
            r1 = 32
            long r1 = r2 << r1
            long r4 = r4 | r1
            r25 = 1
            goto L_0x007b
        L_0x0079:
            r25 = 0
        L_0x007b:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.pushMessagesDict
            java.lang.Object r1 = r1.get(r4)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 != 0) goto L_0x00ac
            org.telegram.tgnet.TLRPC$Message r2 = r7.messageOwner
            long r2 = r2.random_id
            int r26 = (r2 > r19 ? 1 : (r2 == r19 ? 0 : -1))
            if (r26 == 0) goto L_0x00ac
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.fcmRandomMessagesDict
            java.lang.Object r1 = r1.get(r2)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 == 0) goto L_0x00a5
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r8.fcmRandomMessagesDict
            org.telegram.tgnet.TLRPC$Message r3 = r7.messageOwner
            r26 = r0
            r27 = r1
            long r0 = r3.random_id
            r2.remove(r0)
            goto L_0x00a9
        L_0x00a5:
            r26 = r0
            r27 = r1
        L_0x00a9:
            r1 = r27
            goto L_0x00ae
        L_0x00ac:
            r26 = r0
        L_0x00ae:
            if (r1 == 0) goto L_0x00ee
            boolean r0 = r1.isFcmMessage()
            if (r0 == 0) goto L_0x0118
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r8.pushMessagesDict
            r0.put(r4, r7)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.pushMessages
            int r0 = r0.indexOf(r1)
            if (r0 < 0) goto L_0x00da
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r8.pushMessages
            r1.set(r0, r7)
            r0 = r30
            r1 = r32
            r2 = r7
            r3 = r6
            r4 = r12
            r6 = r25
            r12 = r7
            r7 = r11
            int r0 = r0.addToPopupMessages(r1, r2, r3, r4, r6, r7)
            r26 = r0
            goto L_0x00db
        L_0x00da:
            r12 = r7
        L_0x00db:
            if (r33 == 0) goto L_0x00e9
            boolean r0 = r12.localEdit
            if (r0 == 0) goto L_0x00eb
            org.telegram.messenger.MessagesStorage r1 = r30.getMessagesStorage()
            r1.putPushMessage(r12)
            goto L_0x00eb
        L_0x00e9:
            r0 = r17
        L_0x00eb:
            r17 = r0
            goto L_0x0118
        L_0x00ee:
            if (r17 == 0) goto L_0x00f1
            goto L_0x0118
        L_0x00f1:
            if (r33 == 0) goto L_0x00fa
            org.telegram.messenger.MessagesStorage r0 = r30.getMessagesStorage()
            r0.putPushMessage(r7)
        L_0x00fa:
            long r0 = r8.opened_dialog_id
            int r2 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x010a
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x010a
            if (r33 != 0) goto L_0x0118
            r30.playInChatSound()
            goto L_0x0118
        L_0x010a:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            boolean r1 = r0.mentioned
            if (r1 == 0) goto L_0x0125
            if (r22 != 0) goto L_0x011e
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage
            if (r0 == 0) goto L_0x011e
        L_0x0118:
            r27 = r10
            r0 = r26
            goto L_0x01d4
        L_0x011e:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            int r0 = r0.from_id
            long r0 = (long) r0
            r2 = r0
            goto L_0x0126
        L_0x0125:
            r2 = r12
        L_0x0126:
            boolean r0 = r8.isPersonalMessage(r7)
            if (r0 == 0) goto L_0x0132
            int r0 = r8.personal_count
            r1 = 1
            int r0 = r0 + r1
            r8.personal_count = r0
        L_0x0132:
            int r0 = r10.indexOfKey(r2)
            if (r0 < 0) goto L_0x0143
            java.lang.Object r0 = r10.valueAt(r0)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            goto L_0x015c
        L_0x0143:
            int r0 = r8.getNotifyOverride(r11, r2)
            r1 = -1
            if (r0 != r1) goto L_0x014f
            boolean r0 = r8.isGlobalNotificationsEnabled((long) r2)
            goto L_0x0155
        L_0x014f:
            r1 = 2
            if (r0 == r1) goto L_0x0154
            r0 = 1
            goto L_0x0155
        L_0x0154:
            r0 = 0
        L_0x0155:
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r0)
            r10.put(r2, r1)
        L_0x015c:
            if (r0 == 0) goto L_0x01ce
            if (r33 != 0) goto L_0x017a
            r0 = r30
            r1 = r32
            r23 = r2
            r2 = r7
            r3 = r6
            r27 = r10
            r9 = r4
            r4 = r23
            r6 = r25
            r28 = r12
            r12 = r7
            r7 = r11
            int r0 = r0.addToPopupMessages(r1, r2, r3, r4, r6, r7)
            r26 = r0
            goto L_0x0182
        L_0x017a:
            r23 = r2
            r27 = r10
            r28 = r12
            r9 = r4
            r12 = r7
        L_0x0182:
            if (r18 != 0) goto L_0x0189
            org.telegram.tgnet.TLRPC$Message r0 = r12.messageOwner
            boolean r0 = r0.from_scheduled
            goto L_0x018b
        L_0x0189:
            r0 = r18
        L_0x018b:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r8.delayedPushMessages
            r1.add(r12)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r8.pushMessages
            r2 = 0
            r1.add(r2, r12)
            int r1 = (r9 > r19 ? 1 : (r9 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x01a0
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.pushMessagesDict
            r1.put(r9, r12)
            goto L_0x01a9
        L_0x01a0:
            int r1 = (r14 > r19 ? 1 : (r14 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x01a9
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.fcmRandomMessagesDict
            r1.put(r14, r12)
        L_0x01a9:
            int r1 = (r28 > r23 ? 1 : (r28 == r23 ? 0 : -1))
            if (r1 == 0) goto L_0x01cb
            android.util.LongSparseArray<java.lang.Integer> r1 = r8.pushDialogsOverrideMention
            r2 = r28
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            android.util.LongSparseArray<java.lang.Integer> r4 = r8.pushDialogsOverrideMention
            if (r1 != 0) goto L_0x01bd
            r12 = 1
            goto L_0x01c4
        L_0x01bd:
            int r1 = r1.intValue()
            r5 = 1
            int r12 = r1 + 1
        L_0x01c4:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
            r4.put(r2, r1)
        L_0x01cb:
            r18 = r0
            goto L_0x01d0
        L_0x01ce:
            r27 = r10
        L_0x01d0:
            r0 = r26
            r16 = 1
        L_0x01d4:
            int r15 = r21 + 1
            r9 = r31
            r13 = r22
            r10 = r27
            r12 = 1
            goto L_0x0020
        L_0x01df:
            r26 = r0
            if (r16 == 0) goto L_0x01e7
            r0 = r34
            r8.notifyCheck = r0
        L_0x01e7:
            boolean r0 = r32.isEmpty()
            if (r0 != 0) goto L_0x0203
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x0203
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0203
            org.telegram.messenger.-$$Lambda$NotificationsController$vBhFCZdXUS15Ipx-fzqzTMIuA3o r0 = new org.telegram.messenger.-$$Lambda$NotificationsController$vBhFCZdXUS15Ipx-fzqzTMIuA3o
            r1 = r32
            r14 = r26
            r0.<init>(r1, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x0203:
            if (r33 != 0) goto L_0x0207
            if (r18 == 0) goto L_0x02ae
        L_0x0207:
            if (r17 == 0) goto L_0x0215
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.delayedPushMessages
            r0.clear()
            boolean r0 = r8.notifyCheck
            r8.showOrUpdateNotification(r0)
            goto L_0x02ae
        L_0x0215:
            if (r16 == 0) goto L_0x02ae
            r0 = r31
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r0 = r0.getDialogId()
            int r2 = r8.total_unread_count
            int r3 = r8.getNotifyOverride(r11, r0)
            r4 = -1
            if (r3 != r4) goto L_0x0233
            boolean r3 = r8.isGlobalNotificationsEnabled((long) r0)
        L_0x0231:
            r12 = r3
            goto L_0x023a
        L_0x0233:
            r4 = 2
            if (r3 == r4) goto L_0x0238
            r3 = 1
            goto L_0x0231
        L_0x0238:
            r3 = 0
            goto L_0x0231
        L_0x023a:
            android.util.LongSparseArray<java.lang.Integer> r3 = r8.pushDialogs
            java.lang.Object r3 = r3.get(r0)
            java.lang.Integer r3 = (java.lang.Integer) r3
            if (r3 == 0) goto L_0x024b
            int r4 = r3.intValue()
            r5 = 1
            int r4 = r4 + r5
            goto L_0x024d
        L_0x024b:
            r5 = 1
            r4 = 1
        L_0x024d:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            boolean r6 = r8.notifyCheck
            if (r6 == 0) goto L_0x0269
            if (r12 != 0) goto L_0x0269
            android.util.LongSparseArray<java.lang.Integer> r6 = r8.pushDialogsOverrideMention
            java.lang.Object r6 = r6.get(r0)
            java.lang.Integer r6 = (java.lang.Integer) r6
            if (r6 == 0) goto L_0x0269
            int r7 = r6.intValue()
            if (r7 == 0) goto L_0x0269
            r4 = r6
            r12 = 1
        L_0x0269:
            if (r12 == 0) goto L_0x0284
            if (r3 == 0) goto L_0x0276
            int r5 = r8.total_unread_count
            int r3 = r3.intValue()
            int r5 = r5 - r3
            r8.total_unread_count = r5
        L_0x0276:
            int r3 = r8.total_unread_count
            int r5 = r4.intValue()
            int r3 = r3 + r5
            r8.total_unread_count = r3
            android.util.LongSparseArray<java.lang.Integer> r3 = r8.pushDialogs
            r3.put(r0, r4)
        L_0x0284:
            int r0 = r8.total_unread_count
            if (r2 == r0) goto L_0x02a0
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.delayedPushMessages
            r0.clear()
            boolean r0 = r8.notifyCheck
            r8.showOrUpdateNotification(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            int r0 = r0.size()
            org.telegram.messenger.-$$Lambda$NotificationsController$R3R5Z37efc0XPsswynnBTmucwac r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$R3R5Z37efc0XPsswynnBTmucwac
            r1.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x02a0:
            r0 = 0
            r8.notifyCheck = r0
            boolean r0 = r8.showBadgeNumber
            if (r0 == 0) goto L_0x02ae
            int r0 = r30.getTotalAllUnreadCount()
            r8.setBadge(r0)
        L_0x02ae:
            if (r35 == 0) goto L_0x02b3
            r35.countDown()
        L_0x02b3:
            return
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
        notificationsQueue.postRunnable(new Runnable(longSparseArray, new ArrayList()) {
            private final /* synthetic */ LongSparseArray f$1;
            private final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationsController.this.lambda$processDialogsUpdateRead$19$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$processDialogsUpdateRead$19$NotificationsController(LongSparseArray longSparseArray, ArrayList arrayList) {
        boolean z;
        Integer num;
        LongSparseArray longSparseArray2 = longSparseArray;
        ArrayList arrayList2 = arrayList;
        int i = this.total_unread_count;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        int i2 = 0;
        while (true) {
            z = true;
            if (i2 >= longSparseArray.size()) {
                break;
            }
            long keyAt = longSparseArray2.keyAt(i2);
            int notifyOverride = getNotifyOverride(notificationsSettings, keyAt);
            boolean isGlobalNotificationsEnabled = notifyOverride == -1 ? isGlobalNotificationsEnabled(keyAt) : notifyOverride != 2;
            Integer num2 = this.pushDialogs.get(keyAt);
            Integer num3 = (Integer) longSparseArray2.get(keyAt);
            if (!this.notifyCheck || isGlobalNotificationsEnabled || (num = this.pushDialogsOverrideMention.get(keyAt)) == null || num.intValue() == 0) {
                num = num3;
            } else {
                isGlobalNotificationsEnabled = true;
            }
            if (num.intValue() == 0) {
                this.smartNotificationsDialogs.remove(keyAt);
            }
            if (num.intValue() < 0) {
                if (num2 == null) {
                    i2++;
                } else {
                    num = Integer.valueOf(num2.intValue() + num.intValue());
                }
            }
            if ((isGlobalNotificationsEnabled || num.intValue() == 0) && num2 != null) {
                this.total_unread_count -= num2.intValue();
            }
            if (num.intValue() == 0) {
                this.pushDialogs.remove(keyAt);
                this.pushDialogsOverrideMention.remove(keyAt);
                int i3 = 0;
                while (i3 < this.pushMessages.size()) {
                    MessageObject messageObject = this.pushMessages.get(i3);
                    if (!messageObject.messageOwner.from_scheduled && messageObject.getDialogId() == keyAt) {
                        if (isPersonalMessage(messageObject)) {
                            this.personal_count--;
                        }
                        this.pushMessages.remove(i3);
                        i3--;
                        this.delayedPushMessages.remove(messageObject);
                        long id = (long) messageObject.getId();
                        int i4 = messageObject.messageOwner.to_id.channel_id;
                        if (i4 != 0) {
                            id |= ((long) i4) << 32;
                        }
                        this.pushMessagesDict.remove(id);
                        arrayList2.add(messageObject);
                    }
                    i3++;
                }
            } else if (isGlobalNotificationsEnabled) {
                this.total_unread_count += num.intValue();
                this.pushDialogs.put(keyAt, num);
            }
            i2++;
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList2) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$17$NotificationsController(this.f$1);
                }
            });
        }
        if (i != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            }
            AndroidUtilities.runOnUIThread(new Runnable(this.pushDialogs.size()) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$18$NotificationsController(this.f$1);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
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

    public void processLoadedUnreadMessages(LongSparseArray<Integer> longSparseArray, ArrayList<TLRPC.Message> arrayList, ArrayList<MessageObject> arrayList2, ArrayList<TLRPC.User> arrayList3, ArrayList<TLRPC.Chat> arrayList4, ArrayList<TLRPC.EncryptedChat> arrayList5) {
        getMessagesController().putUsers(arrayList3, true);
        getMessagesController().putChats(arrayList4, true);
        getMessagesController().putEncryptedChats(arrayList5, true);
        notificationsQueue.postRunnable(new Runnable(arrayList, longSparseArray, arrayList2) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ LongSparseArray f$2;
            private final /* synthetic */ ArrayList f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                NotificationsController.this.lambda$processLoadedUnreadMessages$21$NotificationsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0049, code lost:
        if ((r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined) == false) goto L_0x0050;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedUnreadMessages$21$NotificationsController(java.util.ArrayList r21, android.util.LongSparseArray r22, java.util.ArrayList r23) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = r22
            r3 = r23
            android.util.LongSparseArray<java.lang.Integer> r4 = r0.pushDialogs
            r4.clear()
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.pushMessages
            r4.clear()
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r4 = r0.pushMessagesDict
            r4.clear()
            r4 = 0
            r0.total_unread_count = r4
            r0.personal_count = r4
            org.telegram.messenger.AccountInstance r5 = r20.getAccountInstance()
            android.content.SharedPreferences r5 = r5.getNotificationsSettings()
            android.util.LongSparseArray r6 = new android.util.LongSparseArray
            r6.<init>()
            r7 = 32
            r10 = 1
            if (r1 == 0) goto L_0x00fd
            r11 = 0
        L_0x002f:
            int r12 = r21.size()
            if (r11 >= r12) goto L_0x00fd
            java.lang.Object r12 = r1.get(r11)
            org.telegram.tgnet.TLRPC$Message r12 = (org.telegram.tgnet.TLRPC.Message) r12
            if (r12 == 0) goto L_0x0050
            boolean r13 = r12.silent
            if (r13 == 0) goto L_0x0050
            org.telegram.tgnet.TLRPC$MessageAction r13 = r12.action
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp
            if (r14 != 0) goto L_0x004b
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined
            if (r13 == 0) goto L_0x0050
        L_0x004b:
            r18 = r5
            r12 = r11
            goto L_0x00f3
        L_0x0050:
            int r13 = r12.id
            long r13 = (long) r13
            org.telegram.tgnet.TLRPC$Peer r15 = r12.to_id
            int r15 = r15.channel_id
            if (r15 == 0) goto L_0x005c
            long r8 = (long) r15
            long r8 = r8 << r7
            long r13 = r13 | r8
        L_0x005c:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r8 = r0.pushMessagesDict
            int r8 = r8.indexOfKey(r13)
            if (r8 < 0) goto L_0x0065
            goto L_0x004b
        L_0x0065:
            org.telegram.messenger.MessageObject r8 = new org.telegram.messenger.MessageObject
            int r9 = r0.currentAccount
            r8.<init>(r9, r12, r4)
            boolean r9 = r0.isPersonalMessage(r8)
            if (r9 == 0) goto L_0x0077
            int r9 = r0.personal_count
            int r9 = r9 + r10
            r0.personal_count = r9
        L_0x0077:
            r12 = r11
            long r10 = r8.getDialogId()
            org.telegram.tgnet.TLRPC$Message r15 = r8.messageOwner
            boolean r9 = r15.mentioned
            if (r9 == 0) goto L_0x0088
            int r9 = r15.from_id
            r17 = r8
            long r7 = (long) r9
            goto L_0x008b
        L_0x0088:
            r17 = r8
            r7 = r10
        L_0x008b:
            int r9 = r6.indexOfKey(r7)
            if (r9 < 0) goto L_0x009c
            java.lang.Object r9 = r6.valueAt(r9)
            java.lang.Boolean r9 = (java.lang.Boolean) r9
            boolean r9 = r9.booleanValue()
            goto L_0x00b5
        L_0x009c:
            int r9 = r0.getNotifyOverride(r5, r7)
            r15 = -1
            if (r9 != r15) goto L_0x00a8
            boolean r9 = r0.isGlobalNotificationsEnabled((long) r7)
            goto L_0x00ae
        L_0x00a8:
            r15 = 2
            if (r9 == r15) goto L_0x00ad
            r9 = 1
            goto L_0x00ae
        L_0x00ad:
            r9 = 0
        L_0x00ae:
            java.lang.Boolean r15 = java.lang.Boolean.valueOf(r9)
            r6.put(r7, r15)
        L_0x00b5:
            r18 = r5
            if (r9 == 0) goto L_0x00f3
            long r4 = r0.opened_dialog_id
            int r9 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r9 != 0) goto L_0x00c4
            boolean r4 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r4 == 0) goto L_0x00c4
            goto L_0x00f3
        L_0x00c4:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r4 = r0.pushMessagesDict
            r5 = r17
            r4.put(r13, r5)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.pushMessages
            r9 = 0
            r4.add(r9, r5)
            int r4 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r4 == 0) goto L_0x00f3
            android.util.LongSparseArray<java.lang.Integer> r4 = r0.pushDialogsOverrideMention
            java.lang.Object r4 = r4.get(r10)
            java.lang.Integer r4 = (java.lang.Integer) r4
            android.util.LongSparseArray<java.lang.Integer> r5 = r0.pushDialogsOverrideMention
            if (r4 != 0) goto L_0x00e4
            r16 = 1
            goto L_0x00ec
        L_0x00e4:
            int r4 = r4.intValue()
            r7 = 1
            int r4 = r4 + r7
            r16 = r4
        L_0x00ec:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r16)
            r5.put(r10, r4)
        L_0x00f3:
            int r11 = r12 + 1
            r5 = r18
            r4 = 0
            r7 = 32
            r10 = 1
            goto L_0x002f
        L_0x00fd:
            r18 = r5
            r1 = 0
        L_0x0100:
            int r4 = r22.size()
            if (r1 >= r4) goto L_0x0159
            long r4 = r2.keyAt(r1)
            int r7 = r6.indexOfKey(r4)
            if (r7 < 0) goto L_0x011e
            java.lang.Object r7 = r6.valueAt(r7)
            java.lang.Boolean r7 = (java.lang.Boolean) r7
            boolean r7 = r7.booleanValue()
            r8 = r7
            r7 = r18
            goto L_0x0139
        L_0x011e:
            r7 = r18
            int r8 = r0.getNotifyOverride(r7, r4)
            r10 = -1
            if (r8 != r10) goto L_0x012c
            boolean r8 = r0.isGlobalNotificationsEnabled((long) r4)
            goto L_0x0132
        L_0x012c:
            r10 = 2
            if (r8 == r10) goto L_0x0131
            r8 = 1
            goto L_0x0132
        L_0x0131:
            r8 = 0
        L_0x0132:
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r8)
            r6.put(r4, r10)
        L_0x0139:
            if (r8 != 0) goto L_0x013c
            goto L_0x0154
        L_0x013c:
            java.lang.Object r8 = r2.valueAt(r1)
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r8 = r8.intValue()
            android.util.LongSparseArray<java.lang.Integer> r10 = r0.pushDialogs
            java.lang.Integer r11 = java.lang.Integer.valueOf(r8)
            r10.put(r4, r11)
            int r4 = r0.total_unread_count
            int r4 = r4 + r8
            r0.total_unread_count = r4
        L_0x0154:
            int r1 = r1 + 1
            r18 = r7
            goto L_0x0100
        L_0x0159:
            r7 = r18
            if (r3 == 0) goto L_0x0261
            r1 = 0
        L_0x015e:
            int r2 = r23.size()
            if (r1 >= r2) goto L_0x0261
            java.lang.Object r2 = r3.get(r1)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r4 = r2.getId()
            long r4 = (long) r4
            org.telegram.tgnet.TLRPC$Message r8 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r8 = r8.to_id
            int r8 = r8.channel_id
            if (r8 == 0) goto L_0x017d
            long r10 = (long) r8
            r8 = 32
            long r10 = r10 << r8
            long r4 = r4 | r10
            goto L_0x017f
        L_0x017d:
            r8 = 32
        L_0x017f:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r10 = r0.pushMessagesDict
            int r10 = r10.indexOfKey(r4)
            if (r10 < 0) goto L_0x018c
        L_0x0187:
            r5 = 0
            r16 = 1
            goto L_0x025d
        L_0x018c:
            boolean r10 = r0.isPersonalMessage(r2)
            if (r10 == 0) goto L_0x0198
            int r10 = r0.personal_count
            r9 = 1
            int r10 = r10 + r9
            r0.personal_count = r10
        L_0x0198:
            long r10 = r2.getDialogId()
            org.telegram.tgnet.TLRPC$Message r12 = r2.messageOwner
            long r13 = r12.random_id
            boolean r8 = r12.mentioned
            if (r8 == 0) goto L_0x01aa
            int r8 = r12.from_id
            r21 = r10
            long r9 = (long) r8
            goto L_0x01ae
        L_0x01aa:
            r21 = r10
            r9 = r21
        L_0x01ae:
            int r8 = r6.indexOfKey(r9)
            if (r8 < 0) goto L_0x01c0
            java.lang.Object r8 = r6.valueAt(r8)
            java.lang.Boolean r8 = (java.lang.Boolean) r8
            boolean r8 = r8.booleanValue()
            r12 = 2
            goto L_0x01da
        L_0x01c0:
            int r8 = r0.getNotifyOverride(r7, r9)
            r11 = -1
            if (r8 != r11) goto L_0x01cd
            boolean r8 = r0.isGlobalNotificationsEnabled((long) r9)
            r12 = 2
            goto L_0x01d3
        L_0x01cd:
            r12 = 2
            if (r8 == r12) goto L_0x01d2
            r8 = 1
            goto L_0x01d3
        L_0x01d2:
            r8 = 0
        L_0x01d3:
            java.lang.Boolean r11 = java.lang.Boolean.valueOf(r8)
            r6.put(r9, r11)
        L_0x01da:
            if (r8 == 0) goto L_0x0187
            r18 = r13
            long r12 = r0.opened_dialog_id
            int r8 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r8 != 0) goto L_0x01e9
            boolean r8 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r8 == 0) goto L_0x01e9
            goto L_0x0187
        L_0x01e9:
            r11 = 0
            int r8 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r8 == 0) goto L_0x01f5
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r8 = r0.pushMessagesDict
            r8.put(r4, r2)
            goto L_0x0200
        L_0x01f5:
            int r4 = (r18 > r11 ? 1 : (r18 == r11 ? 0 : -1))
            if (r4 == 0) goto L_0x0200
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r4 = r0.fcmRandomMessagesDict
            r11 = r18
            r4.put(r11, r2)
        L_0x0200:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.pushMessages
            r5 = 0
            r4.add(r5, r2)
            int r2 = (r21 > r9 ? 1 : (r21 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x022c
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogsOverrideMention
            r11 = r21
            java.lang.Object r2 = r2.get(r11)
            java.lang.Integer r2 = (java.lang.Integer) r2
            android.util.LongSparseArray<java.lang.Integer> r4 = r0.pushDialogsOverrideMention
            if (r2 != 0) goto L_0x021c
            r2 = 1
            r16 = 1
            goto L_0x0224
        L_0x021c:
            int r2 = r2.intValue()
            r16 = 1
            int r2 = r2 + 1
        L_0x0224:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r4.put(r11, r2)
            goto L_0x022e
        L_0x022c:
            r16 = 1
        L_0x022e:
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogs
            java.lang.Object r2 = r2.get(r9)
            java.lang.Integer r2 = (java.lang.Integer) r2
            if (r2 == 0) goto L_0x023f
            int r4 = r2.intValue()
            int r4 = r4 + 1
            goto L_0x0240
        L_0x023f:
            r4 = 1
        L_0x0240:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            if (r2 == 0) goto L_0x024f
            int r8 = r0.total_unread_count
            int r2 = r2.intValue()
            int r8 = r8 - r2
            r0.total_unread_count = r8
        L_0x024f:
            int r2 = r0.total_unread_count
            int r8 = r4.intValue()
            int r2 = r2 + r8
            r0.total_unread_count = r2
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogs
            r2.put(r9, r4)
        L_0x025d:
            int r1 = r1 + 1
            goto L_0x015e
        L_0x0261:
            r5 = 0
            r16 = 1
            android.util.LongSparseArray<java.lang.Integer> r1 = r0.pushDialogs
            int r1 = r1.size()
            org.telegram.messenger.-$$Lambda$NotificationsController$CkSMdSXLZtMteSgS81186zoUJaI r2 = new org.telegram.messenger.-$$Lambda$NotificationsController$CkSMdSXLZtMteSgS81186zoUJaI
            r2.<init>(r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            long r1 = android.os.SystemClock.elapsedRealtime()
            r3 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 / r3
            r3 = 60
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 >= 0) goto L_0x0280
            r5 = 1
        L_0x0280:
            r0.showOrUpdateNotification(r5)
            boolean r1 = r0.showBadgeNumber
            if (r1 == 0) goto L_0x028e
            int r1 = r20.getTotalAllUnreadCount()
            r0.setBadge(r1)
        L_0x028e:
            return
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
        int i;
        int i2;
        Exception e;
        Exception e2;
        int i3 = 0;
        for (int i4 = 0; i4 < 3; i4++) {
            if (UserConfig.getInstance(i4).isClientActivated()) {
                NotificationsController instance = getInstance(i4);
                if (instance.showBadgeNumber) {
                    if (instance.showBadgeMessages) {
                        if (instance.showBadgeMuted) {
                            try {
                                int size = MessagesController.getInstance(i4).allDialogs.size();
                                i2 = i3;
                                int i5 = 0;
                                while (i5 < size) {
                                    try {
                                        TLRPC.Dialog dialog = MessagesController.getInstance(i4).allDialogs.get(i5);
                                        if (dialog.unread_count != 0) {
                                            i2 += dialog.unread_count;
                                        }
                                        i5++;
                                    } catch (Exception e3) {
                                        e2 = e3;
                                        FileLog.e((Throwable) e2);
                                        i3 = i2;
                                    }
                                }
                            } catch (Exception e4) {
                                i2 = i3;
                                e2 = e4;
                                FileLog.e((Throwable) e2);
                                i3 = i2;
                            }
                        } else {
                            i = instance.total_unread_count;
                            i3 += i;
                        }
                    } else if (instance.showBadgeMuted) {
                        try {
                            int size2 = MessagesController.getInstance(i4).allDialogs.size();
                            i2 = i3;
                            int i6 = 0;
                            while (i6 < size2) {
                                try {
                                    if (MessagesController.getInstance(i4).allDialogs.get(i6).unread_count != 0) {
                                        i2++;
                                    }
                                    i6++;
                                } catch (Exception e5) {
                                    e = e5;
                                    FileLog.e((Throwable) e);
                                    i3 = i2;
                                }
                            }
                        } catch (Exception e6) {
                            i2 = i3;
                            e = e6;
                            FileLog.e((Throwable) e);
                            i3 = i2;
                        }
                    } else {
                        i = instance.pushDialogs.size();
                        i3 += i;
                    }
                    i3 = i2;
                }
            }
        }
        return i3;
    }

    public /* synthetic */ void lambda$updateBadge$22$NotificationsController() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$updateBadge$22$NotificationsController();
            }
        });
    }

    private void setBadge(int i) {
        if (this.lastBadgeCount != i) {
            this.lastBadgeCount = i;
            NotificationBadge.applyCount(i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:86:0x0151 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0152  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r17, java.lang.String[] r18, boolean[] r19) {
        /*
            r16 = this;
            r0 = r17
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x0bbc
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x0bbc
        L_0x000e:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r2 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r4 = r1.chat_id
            if (r4 == 0) goto L_0x0019
            goto L_0x001b
        L_0x0019:
            int r4 = r1.channel_id
        L_0x001b:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.user_id
            r5 = 1
            r6 = 0
            if (r19 == 0) goto L_0x0027
            r19[r6] = r5
        L_0x0027:
            org.telegram.messenger.AccountInstance r7 = r16.getAccountInstance()
            android.content.SharedPreferences r7 = r7.getNotificationsSettings()
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "content_preview_"
            r8.append(r9)
            r8.append(r2)
            java.lang.String r8 = r8.toString()
            boolean r8 = r7.getBoolean(r8, r5)
            boolean r9 = r17.isFcmMessage()
            r10 = 2131625543(0x7f0e0647, float:1.8878297E38)
            java.lang.String r11 = "Message"
            r12 = 27
            r13 = 2
            if (r9 == 0) goto L_0x00e4
            if (r4 != 0) goto L_0x0071
            if (r1 == 0) goto L_0x0071
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 <= r12) goto L_0x005e
            java.lang.String r1 = r0.localName
            r18[r6] = r1
        L_0x005e:
            if (r8 == 0) goto L_0x0068
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 != 0) goto L_0x00df
        L_0x0068:
            if (r19 == 0) goto L_0x006c
            r19[r6] = r6
        L_0x006c:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r11, r10)
            return r0
        L_0x0071:
            if (r4 == 0) goto L_0x00df
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x008b
            boolean r1 = r17.isMegagroup()
            if (r1 == 0) goto L_0x0082
            goto L_0x008b
        L_0x0082:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 <= r12) goto L_0x008f
            java.lang.String r1 = r0.localName
            r18[r6] = r1
            goto L_0x008f
        L_0x008b:
            java.lang.String r1 = r0.localUserName
            r18[r6] = r1
        L_0x008f:
            if (r8 == 0) goto L_0x00a9
            boolean r1 = r0.localChannel
            if (r1 != 0) goto L_0x009d
            java.lang.String r1 = "EnablePreviewGroup"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 == 0) goto L_0x00a9
        L_0x009d:
            boolean r1 = r0.localChannel
            if (r1 == 0) goto L_0x00df
            java.lang.String r1 = "EnablePreviewChannel"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 != 0) goto L_0x00df
        L_0x00a9:
            if (r19 == 0) goto L_0x00ad
            r19[r6] = r6
        L_0x00ad:
            boolean r1 = r17.isMegagroup()
            if (r1 != 0) goto L_0x00cb
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00cb
            r1 = 2131624562(0x7f0e0272, float:1.8876307E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            java.lang.String r0 = r0.localName
            r2[r6] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00cb:
            r1 = 2131625772(0x7f0e072c, float:1.8878761E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            java.lang.String r3 = r0.localUserName
            r2[r6] = r3
            java.lang.String r0 = r0.localName
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageGroupNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00df:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x00e4:
            if (r1 != 0) goto L_0x00fa
            boolean r1 = r17.isFromUser()
            if (r1 != 0) goto L_0x00f5
            int r1 = r17.getId()
            if (r1 >= 0) goto L_0x00f3
            goto L_0x00f5
        L_0x00f3:
            int r1 = -r4
            goto L_0x0108
        L_0x00f5:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            int r1 = r1.from_id
            goto L_0x0108
        L_0x00fa:
            org.telegram.messenger.UserConfig r9 = r16.getUserConfig()
            int r9 = r9.getClientUserId()
            if (r1 != r9) goto L_0x0108
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            int r1 = r1.from_id
        L_0x0108:
            r14 = 0
            int r9 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r9 != 0) goto L_0x0116
            if (r4 == 0) goto L_0x0113
            int r2 = -r4
            long r2 = (long) r2
            goto L_0x0116
        L_0x0113:
            if (r1 == 0) goto L_0x0116
            long r2 = (long) r1
        L_0x0116:
            r9 = 0
            if (r1 <= 0) goto L_0x013a
            org.telegram.messenger.MessagesController r14 = r16.getMessagesController()
            java.lang.Integer r15 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r14 = r14.getUser(r15)
            if (r14 == 0) goto L_0x014e
            java.lang.String r14 = org.telegram.messenger.UserObject.getUserName(r14)
            if (r4 == 0) goto L_0x0130
            r18[r6] = r14
            goto L_0x014f
        L_0x0130:
            int r15 = android.os.Build.VERSION.SDK_INT
            if (r15 <= r12) goto L_0x0137
            r18[r6] = r14
            goto L_0x014f
        L_0x0137:
            r18[r6] = r9
            goto L_0x014f
        L_0x013a:
            org.telegram.messenger.MessagesController r14 = r16.getMessagesController()
            int r15 = -r1
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            org.telegram.tgnet.TLRPC$Chat r14 = r14.getChat(r15)
            if (r14 == 0) goto L_0x014e
            java.lang.String r14 = r14.title
            r18[r6] = r14
            goto L_0x014f
        L_0x014e:
            r14 = r9
        L_0x014f:
            if (r14 != 0) goto L_0x0152
            return r9
        L_0x0152:
            if (r4 == 0) goto L_0x0174
            org.telegram.messenger.MessagesController r15 = r16.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r10 = r15.getChat(r10)
            if (r10 != 0) goto L_0x0163
            return r9
        L_0x0163:
            boolean r15 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r15 == 0) goto L_0x0175
            boolean r15 = r10.megagroup
            if (r15 != 0) goto L_0x0175
            int r15 = android.os.Build.VERSION.SDK_INT
            if (r15 > r12) goto L_0x0175
            r18[r6] = r9
            goto L_0x0175
        L_0x0174:
            r10 = r9
        L_0x0175:
            int r3 = (int) r2
            if (r3 != 0) goto L_0x0184
            r18[r6] = r9
            r0 = 2131625749(0x7f0e0715, float:1.8878715E38)
            java.lang.String r1 = "NotificationHiddenMessage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0184:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r2 == 0) goto L_0x0190
            boolean r2 = r10.megagroup
            if (r2 != 0) goto L_0x0190
            r2 = 1
            goto L_0x0191
        L_0x0190:
            r2 = 0
        L_0x0191:
            if (r8 == 0) goto L_0x0bb0
            if (r4 != 0) goto L_0x019f
            if (r1 == 0) goto L_0x019f
            java.lang.String r3 = "EnablePreviewAll"
            boolean r3 = r7.getBoolean(r3, r5)
            if (r3 != 0) goto L_0x01b5
        L_0x019f:
            if (r4 == 0) goto L_0x0bb0
            if (r2 != 0) goto L_0x01ab
            java.lang.String r3 = "EnablePreviewGroup"
            boolean r3 = r7.getBoolean(r3, r5)
            if (r3 != 0) goto L_0x01b5
        L_0x01ab:
            if (r2 == 0) goto L_0x0bb0
            java.lang.String r2 = "EnablePreviewChannel"
            boolean r2 = r7.getBoolean(r2, r5)
            if (r2 == 0) goto L_0x0bb0
        L_0x01b5:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            java.lang.String r4 = "🖼 "
            r7 = 19
            if (r3 == 0) goto L_0x09d0
            r18[r6] = r9
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined
            if (r3 != 0) goto L_0x09c2
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp
            if (r3 == 0) goto L_0x01ce
            goto L_0x09c2
        L_0x01ce:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto
            if (r3 == 0) goto L_0x01e0
            r0 = 2131625735(0x7f0e0707, float:1.8878686E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r14
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x01e0:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation
            r8 = 3
            if (r3 == 0) goto L_0x023f
            r1 = 2131627272(0x7f0e0d08, float:1.8881804E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r3 = r3.formatterYear
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r9 = (long) r4
            r11 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 * r11
            java.lang.String r3 = r3.format((long) r9)
            r2[r6] = r3
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r3 = r3.formatterDay
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r9 = (long) r4
            long r9 = r9 * r11
            java.lang.String r3 = r3.format((long) r9)
            r2[r5] = r3
            java.lang.String r3 = "formatDateAtTime"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            r2 = 2131625799(0x7f0e0747, float:1.8878816E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r4 = r16.getUserConfig()
            org.telegram.tgnet.TLRPC$User r4 = r4.getCurrentUser()
            java.lang.String r4 = r4.first_name
            r3[r6] = r4
            r3[r5] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r1 = r0.title
            r3[r13] = r1
            java.lang.String r0 = r0.address
            r3[r8] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x023f:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore
            if (r3 != 0) goto L_0x09bb
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent
            if (r3 == 0) goto L_0x0249
            goto L_0x09bb
        L_0x0249:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r3 == 0) goto L_0x0263
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r1 = r2.reason
            boolean r0 = r17.isOut()
            if (r0 != 0) goto L_0x0ba5
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed
            if (r0 == 0) goto L_0x0ba5
            r0 = 2131624459(0x7f0e020b, float:1.8876098E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0263:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r3 == 0) goto L_0x0367
            int r3 = r2.user_id
            if (r3 != 0) goto L_0x0283
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            int r2 = r2.size()
            if (r2 != r5) goto L_0x0283
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            java.lang.Object r2 = r2.get(r6)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r3 = r2.intValue()
        L_0x0283:
            if (r3 == 0) goto L_0x0312
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x02a3
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x02a3
            r0 = 2131624513(0x7f0e0241, float:1.8876208E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x02a3:
            org.telegram.messenger.UserConfig r0 = r16.getUserConfig()
            int r0 = r0.getClientUserId()
            if (r3 != r0) goto L_0x02bf
            r0 = 2131625751(0x7f0e0717, float:1.8878719E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x02bf:
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x02ce
            return r9
        L_0x02ce:
            int r2 = r0.id
            if (r1 != r2) goto L_0x02fa
            boolean r0 = r10.megagroup
            if (r0 == 0) goto L_0x02e8
            r0 = 2131625740(0x7f0e070c, float:1.8878696E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x02e8:
            r0 = 2131625739(0x7f0e070b, float:1.8878694E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x02fa:
            r1 = 2131625738(0x7f0e070a, float:1.8878692E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r6] = r14
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2[r13] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0312:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0318:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x034f
            org.telegram.messenger.MessagesController r3 = r16.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x034c
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0349
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0349:
            r1.append(r3)
        L_0x034c:
            int r2 = r2 + 1
            goto L_0x0318
        L_0x034f:
            r0 = 2131625738(0x7f0e070a, float:1.8878692E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r6] = r14
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r1 = r1.toString()
            r2[r13] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0367:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r3 == 0) goto L_0x037d
            r0 = 2131625752(0x7f0e0718, float:1.887872E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x037d:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle
            if (r3 == 0) goto L_0x0393
            r0 = 2131625736(0x7f0e0708, float:1.8878688E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r2.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0393:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r3 != 0) goto L_0x098d
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto
            if (r3 == 0) goto L_0x039d
            goto L_0x098d
        L_0x039d:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r3 == 0) goto L_0x0406
            int r2 = r2.user_id
            org.telegram.messenger.UserConfig r3 = r16.getUserConfig()
            int r3 = r3.getClientUserId()
            if (r2 != r3) goto L_0x03bf
            r0 = 2131625745(0x7f0e0711, float:1.8878707E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03bf:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.user_id
            if (r2 != r1) goto L_0x03d9
            r0 = 2131625746(0x7f0e0712, float:1.8878709E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03d9:
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x03ee
            return r9
        L_0x03ee:
            r1 = 2131625744(0x7f0e0710, float:1.8878705E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r6] = r14
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2[r13] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0406:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate
            if (r1 == 0) goto L_0x0411
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0411:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r1 == 0) goto L_0x041c
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x041c:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r1 == 0) goto L_0x0430
            r0 = 2131624066(0x7f0e0082, float:1.8875301E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0430:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r1 == 0) goto L_0x0444
            r0 = 2131624066(0x7f0e0082, float:1.8875301E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r2.title
            r1[r6] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0444:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken
            if (r1 == 0) goto L_0x044f
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x044f:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage
            if (r1 == 0) goto L_0x0ba5
            r1 = 20
            if (r10 == 0) goto L_0x070d
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r2 == 0) goto L_0x0461
            boolean r2 = r10.megagroup
            if (r2 == 0) goto L_0x070d
        L_0x0461:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0477
            r0 = 2131625714(0x7f0e06f2, float:1.8878644E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0477:
            boolean r2 = r0.isMusic()
            if (r2 == 0) goto L_0x048f
            r0 = 2131625712(0x7f0e06f0, float:1.887864E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x048f:
            boolean r2 = r0.isVideo()
            r3 = 2131625728(0x7f0e0700, float:1.8878672E38)
            java.lang.String r9 = "NotificationActionPinnedText"
            if (r2 == 0) goto L_0x04df
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x04cd
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x04cd
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "📹 "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r6] = r14
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r1[r13] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r3, r1)
            return r0
        L_0x04cd:
            r0 = 2131625730(0x7f0e0702, float:1.8878676E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04df:
            boolean r2 = r0.isGif()
            if (r2 == 0) goto L_0x052a
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x0518
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0518
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "🎬 "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r6] = r14
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r1[r13] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r3, r1)
            return r0
        L_0x0518:
            r0 = 2131625708(0x7f0e06ec, float:1.8878632E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x052a:
            boolean r2 = r0.isVoice()
            if (r2 == 0) goto L_0x0542
            r0 = 2131625732(0x7f0e0704, float:1.887868E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0542:
            boolean r2 = r0.isRoundVideo()
            if (r2 == 0) goto L_0x055a
            r0 = 2131625722(0x7f0e06fa, float:1.887866E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x055a:
            boolean r2 = r0.isSticker()
            if (r2 != 0) goto L_0x06e1
            boolean r2 = r0.isAnimatedSticker()
            if (r2 == 0) goto L_0x0568
            goto L_0x06e1
        L_0x0568:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r2.media
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r12 == 0) goto L_0x05b3
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x05a1
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x05a1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "📎 "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r6] = r14
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r1[r13] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r3, r1)
            return r0
        L_0x05a1:
            r0 = 2131625698(0x7f0e06e2, float:1.8878611E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05b3:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r12 != 0) goto L_0x06cf
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r12 == 0) goto L_0x05bd
            goto L_0x06cf
        L_0x05bd:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r12 == 0) goto L_0x05d3
            r0 = 2131625706(0x7f0e06ea, float:1.8878628E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05d3:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r12 == 0) goto L_0x05f5
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r11 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r11
            r0 = 2131625696(0x7f0e06e0, float:1.8878607E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = r11.first_name
            java.lang.String r3 = r11.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r1[r13] = r2
            java.lang.String r2 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05f5:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r12 == 0) goto L_0x062d
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r11 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r11
            org.telegram.tgnet.TLRPC$TL_poll r0 = r11.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0617
            r1 = 2131625720(0x7f0e06f8, float:1.8878656E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r6] = r14
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r13] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0617:
            r1 = 2131625718(0x7f0e06f6, float:1.8878652E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r6] = r14
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r13] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x062d:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r12 == 0) goto L_0x0671
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x065f
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x065f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r6] = r14
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r1[r13] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r3, r1)
            return r0
        L_0x065f:
            r0 = 2131625716(0x7f0e06f4, float:1.8878648E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0671:
            boolean r2 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r2 == 0) goto L_0x0687
            r0 = 2131625700(0x7f0e06e4, float:1.8878615E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0687:
            java.lang.CharSequence r2 = r0.messageText
            if (r2 == 0) goto L_0x06bd
            int r2 = r2.length()
            if (r2 <= 0) goto L_0x06bd
            java.lang.CharSequence r0 = r0.messageText
            int r2 = r0.length()
            if (r2 <= r1) goto L_0x06ae
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r6, r1)
            r2.append(r0)
            java.lang.String r0 = "..."
            r2.append(r0)
            java.lang.String r0 = r2.toString()
        L_0x06ae:
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r6] = r14
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r1[r13] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r3, r1)
            return r0
        L_0x06bd:
            r0 = 2131625714(0x7f0e06f2, float:1.8878644E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06cf:
            r0 = 2131625704(0x7f0e06e8, float:1.8878623E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06e1:
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x06fb
            r1 = 2131625726(0x7f0e06fe, float:1.8878668E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r6] = r14
            java.lang.String r3 = r10.title
            r2[r5] = r3
            r2[r13] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x06fb:
            r0 = 2131625724(0x7f0e06fc, float:1.8878664E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x070d:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0721
            r0 = 2131625715(0x7f0e06f3, float:1.8878646E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0721:
            boolean r2 = r0.isMusic()
            if (r2 == 0) goto L_0x0737
            r0 = 2131625713(0x7f0e06f1, float:1.8878642E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0737:
            boolean r2 = r0.isVideo()
            r3 = 2131625729(0x7f0e0701, float:1.8878674E38)
            java.lang.String r8 = "NotificationActionPinnedTextChannel"
            if (r2 == 0) goto L_0x0783
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x0773
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0773
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "📹 "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r13]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1)
            return r0
        L_0x0773:
            r0 = 2131625731(0x7f0e0703, float:1.8878678E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0783:
            boolean r2 = r0.isGif()
            if (r2 == 0) goto L_0x07ca
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x07ba
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x07ba
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "🎬 "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r13]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1)
            return r0
        L_0x07ba:
            r0 = 2131625709(0x7f0e06ed, float:1.8878634E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07ca:
            boolean r2 = r0.isVoice()
            if (r2 == 0) goto L_0x07e0
            r0 = 2131625733(0x7f0e0705, float:1.8878682E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07e0:
            boolean r2 = r0.isRoundVideo()
            if (r2 == 0) goto L_0x07f6
            r0 = 2131625723(0x7f0e06fb, float:1.8878662E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07f6:
            boolean r2 = r0.isSticker()
            if (r2 != 0) goto L_0x0965
            boolean r2 = r0.isAnimatedSticker()
            if (r2 == 0) goto L_0x0804
            goto L_0x0965
        L_0x0804:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r2.media
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r11 == 0) goto L_0x084b
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x083b
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x083b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "📎 "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r13]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1)
            return r0
        L_0x083b:
            r0 = 2131625699(0x7f0e06e3, float:1.8878613E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x084b:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r11 != 0) goto L_0x0955
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r11 == 0) goto L_0x0855
            goto L_0x0955
        L_0x0855:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r11 == 0) goto L_0x0869
            r0 = 2131625707(0x7f0e06eb, float:1.887863E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0869:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r11 == 0) goto L_0x0889
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r9
            r0 = 2131625697(0x7f0e06e1, float:1.887861E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = r9.first_name
            java.lang.String r3 = r9.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0889:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r11 == 0) goto L_0x08bd
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r9 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r9
            org.telegram.tgnet.TLRPC$TL_poll r0 = r9.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x08a9
            r1 = 2131625721(0x7f0e06f9, float:1.8878658E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            java.lang.String r3 = r10.title
            r2[r6] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x08a9:
            r1 = 2131625719(0x7f0e06f7, float:1.8878654E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            java.lang.String r3 = r10.title
            r2[r6] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x08bd:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r11 == 0) goto L_0x08fd
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x08ed
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x08ed
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r13]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1)
            return r0
        L_0x08ed:
            r0 = 2131625717(0x7f0e06f5, float:1.887865E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08fd:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r2 == 0) goto L_0x0911
            r0 = 2131625701(0x7f0e06e5, float:1.8878617E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0911:
            java.lang.CharSequence r2 = r0.messageText
            if (r2 == 0) goto L_0x0945
            int r2 = r2.length()
            if (r2 <= 0) goto L_0x0945
            java.lang.CharSequence r0 = r0.messageText
            int r2 = r0.length()
            if (r2 <= r1) goto L_0x0938
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r6, r1)
            r2.append(r0)
            java.lang.String r0 = "..."
            r2.append(r0)
            java.lang.String r0 = r2.toString()
        L_0x0938:
            java.lang.Object[] r1 = new java.lang.Object[r13]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1)
            return r0
        L_0x0945:
            r0 = 2131625715(0x7f0e06f3, float:1.8878646E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0955:
            r0 = 2131625705(0x7f0e06e9, float:1.8878625E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0965:
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x097d
            r1 = 2131625727(0x7f0e06ff, float:1.887867E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            java.lang.String r3 = r10.title
            r2[r6] = r3
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x097d:
            r0 = 2131625725(0x7f0e06fd, float:1.8878666E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x098d:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x09a9
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x09a9
            r0 = 2131624577(0x7f0e0281, float:1.8876338E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r6] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09a9:
            r0 = 2131625737(0x7f0e0709, float:1.887869E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r6] = r14
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09bb:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x09c2:
            r0 = 2131625734(0x7f0e0706, float:1.8878684E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r14
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09d0:
            boolean r1 = r17.isMediaEmpty()
            if (r1 == 0) goto L_0x09ed
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x09e5
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x09e5:
            r0 = 2131625543(0x7f0e0647, float:1.8878297E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r11, r0)
            return r0
        L_0x09ed:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0a31
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r7) goto L_0x0a15
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0a15
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0a15:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0a27
            r0 = 2131624275(0x7f0e0153, float:1.8875725E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0a27:
            r0 = 2131624290(0x7f0e0162, float:1.8875756E38)
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0a31:
            boolean r1 = r17.isVideo()
            if (r1 == 0) goto L_0x0a78
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x0a5c
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0a5c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "📹 "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0a5c:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0a6e
            r0 = 2131624276(0x7f0e0154, float:1.8875727E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0a6e:
            r0 = 2131624296(0x7f0e0168, float:1.8875768E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0a78:
            boolean r1 = r17.isGame()
            if (r1 == 0) goto L_0x0a88
            r0 = 2131624278(0x7f0e0156, float:1.8875731E38)
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0a88:
            boolean r1 = r17.isVoice()
            if (r1 == 0) goto L_0x0a98
            r0 = 2131624272(0x7f0e0150, float:1.887572E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0a98:
            boolean r1 = r17.isRoundVideo()
            if (r1 == 0) goto L_0x0aa8
            r0 = 2131624292(0x7f0e0164, float:1.887576E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0aa8:
            boolean r1 = r17.isMusic()
            if (r1 == 0) goto L_0x0ab8
            r0 = 2131624289(0x7f0e0161, float:1.8875754E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ab8:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r2 == 0) goto L_0x0aca
            r0 = 2131624274(0x7f0e0152, float:1.8875723E38)
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0aca:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r2 == 0) goto L_0x0aea
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$TL_poll r0 = r1.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0ae0
            r0 = 2131626320(0x7f0e0950, float:1.8879873E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ae0:
            r0 = 2131626239(0x7f0e08ff, float:1.8879709E38)
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0aea:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r2 != 0) goto L_0x0ba6
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r2 == 0) goto L_0x0af4
            goto L_0x0ba6
        L_0x0af4:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x0b02
            r0 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b02:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r1 == 0) goto L_0x0ba5
            boolean r1 = r17.isSticker()
            if (r1 != 0) goto L_0x0b77
            boolean r1 = r17.isAnimatedSticker()
            if (r1 == 0) goto L_0x0b13
            goto L_0x0b77
        L_0x0b13:
            boolean r1 = r17.isGif()
            if (r1 == 0) goto L_0x0b48
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x0b3e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b3e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "🎬 "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0b3e:
            r0 = 2131624279(0x7f0e0157, float:1.8875733E38)
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b48:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x0b6d
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b6d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "📎 "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0b6d:
            r0 = 2131624277(0x7f0e0155, float:1.887573E38)
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b77:
            java.lang.String r0 = r17.getStickerEmoji()
            if (r0 == 0) goto L_0x0b9b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " "
            r1.append(r0)
            r0 = 2131624293(0x7f0e0165, float:1.8875762E38)
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0b9b:
            r0 = 2131624293(0x7f0e0165, float:1.8875762E38)
            java.lang.String r1 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ba5:
            return r9
        L_0x0ba6:
            r0 = 2131624286(0x7f0e015e, float:1.8875747E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0bb0:
            if (r19 == 0) goto L_0x0bb4
            r19[r6] = r6
        L_0x0bb4:
            r0 = 2131625543(0x7f0e0647, float:1.8878297E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r11, r0)
            return r0
        L_0x0bbc:
            r0 = 2131625749(0x7f0e0715, float:1.8878715E38)
            java.lang.String r1 = "NotificationHiddenMessage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:75:0x0142 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0143  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r17, boolean r18, boolean[] r19, boolean[] r20) {
        /*
            r16 = this;
            r0 = r17
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x127f
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x127f
        L_0x000e:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r2 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r4 = r1.chat_id
            if (r4 == 0) goto L_0x0019
            goto L_0x001b
        L_0x0019:
            int r4 = r1.channel_id
        L_0x001b:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.user_id
            r5 = 1
            r6 = 0
            if (r20 == 0) goto L_0x0027
            r20[r6] = r5
        L_0x0027:
            org.telegram.messenger.AccountInstance r7 = r16.getAccountInstance()
            android.content.SharedPreferences r7 = r7.getNotificationsSettings()
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "content_preview_"
            r8.append(r9)
            r8.append(r2)
            java.lang.String r8 = r8.toString()
            boolean r8 = r7.getBoolean(r8, r5)
            boolean r9 = r17.isFcmMessage()
            r10 = 2
            if (r9 == 0) goto L_0x00c6
            if (r4 != 0) goto L_0x006d
            if (r1 == 0) goto L_0x006d
            if (r8 == 0) goto L_0x0059
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 != 0) goto L_0x00bf
        L_0x0059:
            if (r20 == 0) goto L_0x005d
            r20[r6] = r6
        L_0x005d:
            r1 = 2131625785(0x7f0e0739, float:1.8878788E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            java.lang.String r0 = r0.localName
            r2[r6] = r0
            java.lang.String r0 = "NotificationMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x006d:
            if (r4 == 0) goto L_0x00bf
            if (r8 == 0) goto L_0x0089
            boolean r1 = r0.localChannel
            if (r1 != 0) goto L_0x007d
            java.lang.String r1 = "EnablePreviewGroup"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 == 0) goto L_0x0089
        L_0x007d:
            boolean r1 = r0.localChannel
            if (r1 == 0) goto L_0x00bf
            java.lang.String r1 = "EnablePreviewChannel"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 != 0) goto L_0x00bf
        L_0x0089:
            if (r20 == 0) goto L_0x008d
            r20[r6] = r6
        L_0x008d:
            boolean r1 = r17.isMegagroup()
            if (r1 != 0) goto L_0x00ab
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00ab
            r1 = 2131624562(0x7f0e0272, float:1.8876307E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            java.lang.String r0 = r0.localName
            r2[r6] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00ab:
            r1 = 2131625772(0x7f0e072c, float:1.8878761E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            java.lang.String r3 = r0.localUserName
            r2[r6] = r3
            java.lang.String r0 = r0.localName
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageGroupNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00bf:
            r19[r6] = r5
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = (java.lang.String) r0
            return r0
        L_0x00c6:
            org.telegram.messenger.UserConfig r9 = r16.getUserConfig()
            int r9 = r9.getClientUserId()
            if (r1 != 0) goto L_0x00e4
            boolean r1 = r17.isFromUser()
            if (r1 != 0) goto L_0x00df
            int r1 = r17.getId()
            if (r1 >= 0) goto L_0x00dd
            goto L_0x00df
        L_0x00dd:
            int r1 = -r4
            goto L_0x00ea
        L_0x00df:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            int r1 = r1.from_id
            goto L_0x00ea
        L_0x00e4:
            if (r1 != r9) goto L_0x00ea
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            int r1 = r1.from_id
        L_0x00ea:
            r11 = 0
            int r13 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r13 != 0) goto L_0x00f8
            if (r4 == 0) goto L_0x00f5
            int r2 = -r4
            long r2 = (long) r2
            goto L_0x00f8
        L_0x00f5:
            if (r1 == 0) goto L_0x00f8
            long r2 = (long) r1
        L_0x00f8:
            r11 = 0
            if (r1 <= 0) goto L_0x012d
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            boolean r12 = r12.from_scheduled
            if (r12 == 0) goto L_0x011a
            long r12 = (long) r9
            int r14 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x0110
            r12 = 2131625560(0x7f0e0658, float:1.8878331E38)
            java.lang.String r13 = "MessageScheduledReminderNotification"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            goto L_0x0140
        L_0x0110:
            r12 = 2131625793(0x7f0e0741, float:1.8878804E38)
            java.lang.String r13 = "NotificationMessageScheduledName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            goto L_0x0140
        L_0x011a:
            org.telegram.messenger.MessagesController r12 = r16.getMessagesController()
            java.lang.Integer r13 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r12 = r12.getUser(r13)
            if (r12 == 0) goto L_0x013f
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r12)
            goto L_0x0140
        L_0x012d:
            org.telegram.messenger.MessagesController r12 = r16.getMessagesController()
            int r13 = -r1
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r12 = r12.getChat(r13)
            if (r12 == 0) goto L_0x013f
            java.lang.String r12 = r12.title
            goto L_0x0140
        L_0x013f:
            r12 = r11
        L_0x0140:
            if (r12 != 0) goto L_0x0143
            return r11
        L_0x0143:
            if (r4 == 0) goto L_0x0154
            org.telegram.messenger.MessagesController r13 = r16.getMessagesController()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r13 = r13.getChat(r14)
            if (r13 != 0) goto L_0x0155
            return r11
        L_0x0154:
            r13 = r11
        L_0x0155:
            int r3 = (int) r2
            if (r3 != 0) goto L_0x0163
            r0 = 2131627228(0x7f0e0cdc, float:1.8881715E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x127e
        L_0x0163:
            java.lang.String r2 = "🎬 "
            java.lang.String r3 = "📎 "
            java.lang.String r14 = "📹 "
            java.lang.String r15 = "🖼 "
            java.lang.String r11 = "NotificationMessageText"
            r10 = 3
            if (r4 != 0) goto L_0x0511
            if (r1 == 0) goto L_0x0511
            if (r8 == 0) goto L_0x04fe
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 == 0) goto L_0x04fe
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            if (r4 == 0) goto L_0x0244
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined
            if (r2 != 0) goto L_0x0235
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp
            if (r2 == 0) goto L_0x0192
            goto L_0x0235
        L_0x0192:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto
            if (r2 == 0) goto L_0x01a5
            r0 = 2131625735(0x7f0e0707, float:1.8878686E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x01a5:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation
            if (r2 == 0) goto L_0x0206
            r1 = 2131627272(0x7f0e0d08, float:1.8881804E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterYear
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r7 = (long) r4
            r11 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 * r11
            java.lang.String r2 = r2.format((long) r7)
            r3[r6] = r2
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterDay
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r7 = (long) r4
            long r7 = r7 * r11
            java.lang.String r2 = r2.format((long) r7)
            r3[r5] = r2
            java.lang.String r2 = "formatDateAtTime"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            r2 = 2131625799(0x7f0e0747, float:1.8878816E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r4 = r16.getUserConfig()
            org.telegram.tgnet.TLRPC$User r4 = r4.getCurrentUser()
            java.lang.String r4 = r4.first_name
            r3[r6] = r4
            r3[r5] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r1 = r0.title
            r4 = 2
            r3[r4] = r1
            java.lang.String r0 = r0.address
            r3[r10] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x127e
        L_0x0206:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore
            if (r2 != 0) goto L_0x022d
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent
            if (r2 == 0) goto L_0x020f
            goto L_0x022d
        L_0x020f:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r2 == 0) goto L_0x127c
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r1 = r1.reason
            boolean r0 = r17.isOut()
            if (r0 != 0) goto L_0x022a
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed
            if (r0 == 0) goto L_0x022a
            r0 = 2131624459(0x7f0e020b, float:1.8876098E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x127e
        L_0x022a:
            r11 = 0
            goto L_0x127e
        L_0x022d:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r11 = r0.toString()
            goto L_0x127e
        L_0x0235:
            r0 = 2131625734(0x7f0e0706, float:1.8878684E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0244:
            boolean r1 = r17.isMediaEmpty()
            if (r1 == 0) goto L_0x028a
            if (r18 != 0) goto L_0x027b
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x026c
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            r19[r6] = r5
            goto L_0x127e
        L_0x026c:
            r0 = 2131625785(0x7f0e0739, float:1.8878788E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageNoText"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x027b:
            r0 = 2131625785(0x7f0e0739, float:1.8878788E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageNoText"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x028a:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r4 == 0) goto L_0x02ed
            if (r18 != 0) goto L_0x02c7
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x02c7
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x02c7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1[r5] = r0
            r0 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            r19[r6] = r5
            goto L_0x127e
        L_0x02c7:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x02de
            r0 = 2131625790(0x7f0e073e, float:1.8878798E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageSDPhoto"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x02de:
            r0 = 2131625786(0x7f0e073a, float:1.887879E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessagePhoto"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x02ed:
            boolean r1 = r17.isVideo()
            if (r1 == 0) goto L_0x0350
            if (r18 != 0) goto L_0x032a
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x032a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x032a
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1[r5] = r0
            r0 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            r19[r6] = r5
            goto L_0x127e
        L_0x032a:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0341
            r0 = 2131625791(0x7f0e073f, float:1.88788E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageSDVideo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0341:
            r0 = 2131625797(0x7f0e0745, float:1.8878812E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageVideo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0350:
            boolean r1 = r17.isGame()
            if (r1 == 0) goto L_0x0370
            r1 = 2131625759(0x7f0e071f, float:1.8878735E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            java.lang.String r0 = r0.title
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageGame"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x127e
        L_0x0370:
            boolean r1 = r17.isVoice()
            if (r1 == 0) goto L_0x0385
            r0 = 2131625754(0x7f0e071a, float:1.8878725E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageAudio"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0385:
            boolean r1 = r17.isRoundVideo()
            if (r1 == 0) goto L_0x039a
            r0 = 2131625789(0x7f0e073d, float:1.8878796E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageRound"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x039a:
            boolean r1 = r17.isMusic()
            if (r1 == 0) goto L_0x03af
            r0 = 2131625784(0x7f0e0738, float:1.8878786E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageMusic"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x03af:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r4 == 0) goto L_0x03d3
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r1
            r0 = 2131625755(0x7f0e071b, float:1.8878727E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageContact2"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x127e
        L_0x03d3:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r4 == 0) goto L_0x0407
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$TL_poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x03f2
            r1 = 2131625788(0x7f0e073c, float:1.8878794E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x03f2:
            r2 = 2
            r1 = 2131625787(0x7f0e073b, float:1.8878792E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
        L_0x0404:
            r11 = r0
            goto L_0x127e
        L_0x0407:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r4 != 0) goto L_0x04ef
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r4 == 0) goto L_0x0411
            goto L_0x04ef
        L_0x0411:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x0424
            r0 = 2131625782(0x7f0e0736, float:1.8878782E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageLiveLocation"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0424:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r1 == 0) goto L_0x127c
            boolean r1 = r17.isSticker()
            if (r1 != 0) goto L_0x04c8
            boolean r1 = r17.isAnimatedSticker()
            if (r1 == 0) goto L_0x0436
            goto L_0x04c8
        L_0x0436:
            boolean r1 = r17.isGif()
            if (r1 == 0) goto L_0x0482
            if (r18 != 0) goto L_0x0473
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x0473
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0473
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r5] = r0
            r0 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            r19[r6] = r5
            goto L_0x127e
        L_0x0473:
            r0 = 2131625761(0x7f0e0721, float:1.887874E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageGif"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0482:
            if (r18 != 0) goto L_0x04b9
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x04b9
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x04b9
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1[r5] = r0
            r0 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            r19[r6] = r5
            goto L_0x127e
        L_0x04b9:
            r0 = 2131625756(0x7f0e071c, float:1.8878729E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageDocument"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x04c8:
            java.lang.String r0 = r17.getStickerEmoji()
            if (r0 == 0) goto L_0x04e0
            r1 = 2131625795(0x7f0e0743, float:1.8878808E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x04e0:
            r0 = 2131625794(0x7f0e0742, float:1.8878806E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x04ef:
            r0 = 2131625783(0x7f0e0737, float:1.8878784E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageMap"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x04fe:
            if (r20 == 0) goto L_0x0502
            r20[r6] = r6
        L_0x0502:
            r0 = 2131625785(0x7f0e0739, float:1.8878788E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageNoText"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0511:
            if (r4 == 0) goto L_0x127c
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r13)
            if (r4 == 0) goto L_0x051f
            boolean r4 = r13.megagroup
            if (r4 != 0) goto L_0x051f
            r4 = 1
            goto L_0x0520
        L_0x051f:
            r4 = 0
        L_0x0520:
            if (r8 == 0) goto L_0x124d
            if (r4 != 0) goto L_0x052c
            java.lang.String r8 = "EnablePreviewGroup"
            boolean r8 = r7.getBoolean(r8, r5)
            if (r8 != 0) goto L_0x0536
        L_0x052c:
            if (r4 == 0) goto L_0x124d
            java.lang.String r4 = "EnablePreviewChannel"
            boolean r4 = r7.getBoolean(r4, r5)
            if (r4 == 0) goto L_0x124d
        L_0x0536:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            if (r7 == 0) goto L_0x0d22
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r7 == 0) goto L_0x0647
            int r2 = r4.user_id
            if (r2 != 0) goto L_0x055e
            java.util.ArrayList<java.lang.Integer> r3 = r4.users
            int r3 = r3.size()
            if (r3 != r5) goto L_0x055e
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            java.lang.Object r2 = r2.get(r6)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
        L_0x055e:
            if (r2 == 0) goto L_0x05f0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x0580
            boolean r0 = r13.megagroup
            if (r0 != 0) goto L_0x0580
            r0 = 2131624513(0x7f0e0241, float:1.8876208E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0580:
            r3 = 2
            if (r2 != r9) goto L_0x0596
            r0 = 2131625751(0x7f0e0717, float:1.8878719E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0596:
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x05a6
            r2 = 0
            return r2
        L_0x05a6:
            int r2 = r0.id
            if (r1 != r2) goto L_0x05d6
            boolean r0 = r13.megagroup
            if (r0 == 0) goto L_0x05c2
            r0 = 2131625740(0x7f0e070c, float:1.8878696E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x05c2:
            r1 = 2
            r0 = 2131625739(0x7f0e070b, float:1.8878694E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x05d6:
            r1 = 2131625738(0x7f0e070a, float:1.8878692E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            java.lang.String r3 = r13.title
            r2[r5] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x05f0:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x05f6:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x062d
            org.telegram.messenger.MessagesController r3 = r16.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x062a
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0627
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0627:
            r1.append(r3)
        L_0x062a:
            int r2 = r2 + 1
            goto L_0x05f6
        L_0x062d:
            r0 = 2131625738(0x7f0e070a, float:1.8878692E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            java.lang.String r3 = r13.title
            r2[r5] = r3
            java.lang.String r1 = r1.toString()
            r7 = 2
            r2[r7] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0404
        L_0x0647:
            r7 = 2
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r8 == 0) goto L_0x065f
            r0 = 2131625752(0x7f0e0718, float:1.887872E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x065f:
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle
            if (r8 == 0) goto L_0x0676
            r0 = 2131625736(0x7f0e0708, float:1.8878688E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r6] = r12
            java.lang.String r2 = r4.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0676:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r7 != 0) goto L_0x0cf1
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto
            if (r7 == 0) goto L_0x0680
            goto L_0x0cf1
        L_0x0680:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r7 == 0) goto L_0x06e2
            int r2 = r4.user_id
            if (r2 != r9) goto L_0x069c
            r0 = 2131625745(0x7f0e0711, float:1.8878707E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x069c:
            r3 = 2
            if (r2 != r1) goto L_0x06b2
            r0 = 2131625746(0x7f0e0712, float:1.8878709E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x06b2:
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x06c8
            r1 = 0
            return r1
        L_0x06c8:
            r1 = 2131625744(0x7f0e0710, float:1.8878705E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            java.lang.String r3 = r13.title
            r2[r5] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x127e
        L_0x06e2:
            r1 = 0
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate
            if (r7 == 0) goto L_0x06ef
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r11 = r0.toString()
            goto L_0x127e
        L_0x06ef:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r7 == 0) goto L_0x06fb
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r11 = r0.toString()
            goto L_0x127e
        L_0x06fb:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r7 == 0) goto L_0x0710
            r0 = 2131624066(0x7f0e0082, float:1.8875301E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0710:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r7 == 0) goto L_0x0725
            r0 = 2131624066(0x7f0e0082, float:1.8875301E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r4.title
            r1[r6] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0725:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken
            if (r7 == 0) goto L_0x0731
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r11 = r0.toString()
            goto L_0x127e
        L_0x0731:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage
            if (r7 == 0) goto L_0x0ce5
            if (r13 == 0) goto L_0x0a33
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r13)
            if (r1 == 0) goto L_0x0741
            boolean r1 = r13.megagroup
            if (r1 == 0) goto L_0x0a33
        L_0x0741:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x0759
            r0 = 2131625714(0x7f0e06f2, float:1.8878644E38)
            r4 = 2
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0759:
            r4 = 2
            boolean r7 = r1.isMusic()
            if (r7 == 0) goto L_0x0773
            r0 = 2131625712(0x7f0e06f0, float:1.887864E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0773:
            boolean r4 = r1.isVideo()
            if (r4 == 0) goto L_0x07c6
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x07b2
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x07b2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131625728(0x7f0e0700, float:1.8878672E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            r2[r5] = r0
            java.lang.String r0 = r13.title
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x07b2:
            r3 = 2
            r0 = 2131625730(0x7f0e0702, float:1.8878676E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x07c6:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0819
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0805
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0805
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r2)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131625728(0x7f0e0700, float:1.8878672E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            r2[r5] = r0
            java.lang.String r0 = r13.title
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0805:
            r4 = 2
            r0 = 2131625708(0x7f0e06ec, float:1.8878632E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0819:
            r4 = 2
            boolean r2 = r1.isVoice()
            if (r2 == 0) goto L_0x0833
            r0 = 2131625732(0x7f0e0704, float:1.887868E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0833:
            boolean r2 = r1.isRoundVideo()
            if (r2 == 0) goto L_0x084c
            r0 = 2131625722(0x7f0e06fa, float:1.887866E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x084c:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0a03
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x085a
            goto L_0x0a03
        L_0x085a:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r7 == 0) goto L_0x08ad
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x0899
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0899
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131625728(0x7f0e0700, float:1.8878672E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            r2[r5] = r0
            java.lang.String r0 = r13.title
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0899:
            r3 = 2
            r0 = 2131625698(0x7f0e06e2, float:1.8878611E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x08ad:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r3 != 0) goto L_0x09ef
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r3 == 0) goto L_0x08b7
            goto L_0x09ef
        L_0x08b7:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x08cf
            r0 = 2131625706(0x7f0e06ea, float:1.8878628E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x08cf:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r3 == 0) goto L_0x08f7
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r0
            r1 = 2131625696(0x7f0e06e0, float:1.8878607E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            java.lang.String r3 = r13.title
            r2[r5] = r3
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x08f7:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r0 == 0) goto L_0x0933
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$TL_poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x091b
            r1 = 2131625720(0x7f0e06f8, float:1.8878656E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            java.lang.String r3 = r13.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x091b:
            r3 = 2
            r1 = 2131625718(0x7f0e06f6, float:1.8878652E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            java.lang.String r4 = r13.title
            r2[r5] = r4
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0933:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0982
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x096e
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x096e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131625728(0x7f0e0700, float:1.8878672E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            r2[r5] = r0
            java.lang.String r0 = r13.title
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x096e:
            r3 = 2
            r0 = 2131625716(0x7f0e06f4, float:1.8878648E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0982:
            r3 = 2
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x099a
            r0 = 2131625700(0x7f0e06e4, float:1.8878615E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x099a:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x09db
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x09db
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x09c5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            java.lang.CharSequence r0 = r0.subSequence(r6, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x09c5:
            r1 = 2131625728(0x7f0e0700, float:1.8878672E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            r2[r5] = r0
            java.lang.String r0 = r13.title
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x09db:
            r3 = 2
            r0 = 2131625714(0x7f0e06f2, float:1.8878644E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x09ef:
            r3 = 2
            r0 = 2131625704(0x7f0e06e8, float:1.8878623E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0a03:
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0a1f
            r1 = 2131625726(0x7f0e06fe, float:1.8878668E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            java.lang.String r3 = r13.title
            r2[r5] = r3
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0a1f:
            r3 = 2
            r0 = 2131625724(0x7f0e06fc, float:1.8878664E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0a33:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x0a48
            r0 = 2131625715(0x7f0e06f3, float:1.8878646E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0a48:
            boolean r4 = r1.isMusic()
            if (r4 == 0) goto L_0x0a5f
            r0 = 2131625713(0x7f0e06f1, float:1.8878642E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0a5f:
            boolean r4 = r1.isVideo()
            java.lang.String r7 = "NotificationActionPinnedTextChannel"
            if (r4 == 0) goto L_0x0aad
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x0a9c
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0a9c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131625729(0x7f0e0701, float:1.8878674E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r13.title
            r2[r6] = r3
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2)
            goto L_0x0404
        L_0x0a9c:
            r0 = 2131625731(0x7f0e0703, float:1.8878678E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0aad:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0af9
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0ae8
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0ae8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r2)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131625729(0x7f0e0701, float:1.8878674E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r13.title
            r2[r6] = r3
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2)
            goto L_0x0404
        L_0x0ae8:
            r0 = 2131625709(0x7f0e06ed, float:1.8878634E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0af9:
            boolean r2 = r1.isVoice()
            if (r2 == 0) goto L_0x0b10
            r0 = 2131625733(0x7f0e0705, float:1.8878682E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0b10:
            boolean r2 = r1.isRoundVideo()
            if (r2 == 0) goto L_0x0b27
            r0 = 2131625723(0x7f0e06fb, float:1.8878662E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0b27:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0cba
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x0b35
            goto L_0x0cba
        L_0x0b35:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r8 == 0) goto L_0x0b81
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x0b70
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0b70
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131625729(0x7f0e0701, float:1.8878674E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r13.title
            r2[r6] = r3
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2)
            goto L_0x0404
        L_0x0b70:
            r0 = 2131625699(0x7f0e06e3, float:1.8878613E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0b81:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r3 != 0) goto L_0x0ca9
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r3 == 0) goto L_0x0b8b
            goto L_0x0ca9
        L_0x0b8b:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0ba0
            r0 = 2131625707(0x7f0e06eb, float:1.887863E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0ba0:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r3 == 0) goto L_0x0bc6
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r0
            r1 = 2131625697(0x7f0e06e1, float:1.887861E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r13.title
            r2[r6] = r3
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0bc6:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r0 == 0) goto L_0x0bfe
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$TL_poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0be8
            r1 = 2131625721(0x7f0e06f9, float:1.8878658E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r13.title
            r2[r6] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0be8:
            r2 = 2
            r1 = 2131625719(0x7f0e06f7, float:1.8878654E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r13.title
            r2[r6] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0bfe:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0CLASSNAME
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0CLASSNAME
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131625729(0x7f0e0701, float:1.8878674E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r13.title
            r2[r6] = r3
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2)
            goto L_0x0404
        L_0x0CLASSNAME:
            r0 = 2131625717(0x7f0e06f5, float:1.887865E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0CLASSNAME:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x0c5b
            r0 = 2131625701(0x7f0e06e5, float:1.8878617E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0c5b:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0CLASSNAME
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0CLASSNAME
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0CLASSNAME
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            java.lang.CharSequence r0 = r0.subSequence(r6, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x0CLASSNAME:
            r1 = 2131625729(0x7f0e0701, float:1.8878674E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r13.title
            r2[r6] = r3
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2)
            goto L_0x0404
        L_0x0CLASSNAME:
            r0 = 2131625715(0x7f0e06f3, float:1.8878646E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0ca9:
            r0 = 2131625705(0x7f0e06e9, float:1.8878625E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0cba:
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0cd4
            r1 = 2131625727(0x7f0e06ff, float:1.887867E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r13.title
            r2[r6] = r3
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0cd4:
            r0 = 2131625725(0x7f0e06fd, float:1.8878666E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0ce5:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore
            if (r2 == 0) goto L_0x127d
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r11 = r0.toString()
            goto L_0x127e
        L_0x0cf1:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x0d0e
            boolean r0 = r13.megagroup
            if (r0 != 0) goto L_0x0d0e
            r0 = 2131624577(0x7f0e0281, float:1.8876338E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r13.title
            r1[r6] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0d0e:
            r0 = 2131625737(0x7f0e0709, float:1.887869E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0d22:
            r1 = 0
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r13)
            if (r4 == 0) goto L_0x0f8c
            boolean r4 = r13.megagroup
            if (r4 != 0) goto L_0x0f8c
            boolean r4 = r17.isMediaEmpty()
            if (r4 == 0) goto L_0x0d66
            if (r18 != 0) goto L_0x0d57
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            if (r1 == 0) goto L_0x0d57
            int r1 = r1.length()
            if (r1 == 0) goto L_0x0d57
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            r19[r6] = r5
            goto L_0x127e
        L_0x0d57:
            r0 = 2131624562(0x7f0e0272, float:1.8876307E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0d66:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r4.media
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r7 == 0) goto L_0x0db2
            if (r18 != 0) goto L_0x0da3
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0da3
            java.lang.String r1 = r4.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0da3
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1[r5] = r0
            r0 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            r19[r6] = r5
            goto L_0x127e
        L_0x0da3:
            r0 = 2131624563(0x7f0e0273, float:1.887631E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessagePhoto"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0db2:
            boolean r4 = r17.isVideo()
            if (r4 == 0) goto L_0x0dfe
            if (r18 != 0) goto L_0x0def
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0def
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0def
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1[r5] = r0
            r0 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            r19[r6] = r5
            goto L_0x127e
        L_0x0def:
            r0 = 2131624569(0x7f0e0279, float:1.8876321E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageVideo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0dfe:
            boolean r4 = r17.isVoice()
            if (r4 == 0) goto L_0x0e13
            r0 = 2131624554(0x7f0e026a, float:1.887629E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageAudio"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0e13:
            boolean r4 = r17.isRoundVideo()
            if (r4 == 0) goto L_0x0e28
            r0 = 2131624566(0x7f0e0276, float:1.8876315E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageRound"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0e28:
            boolean r4 = r17.isMusic()
            if (r4 == 0) goto L_0x0e3d
            r0 = 2131624561(0x7f0e0271, float:1.8876305E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageMusic"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0e3d:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r7 == 0) goto L_0x0e61
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r4
            r0 = 2131624555(0x7f0e026b, float:1.8876293E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.String r2 = r4.first_name
            java.lang.String r3 = r4.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r1[r5] = r2
            java.lang.String r2 = "ChannelMessageContact2"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0e61:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r7 == 0) goto L_0x0e95
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$TL_poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0e81
            r1 = 2131624565(0x7f0e0275, float:1.8876313E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0e81:
            r2 = 2
            r1 = 2131624564(0x7f0e0274, float:1.8876311E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0e95:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r7 != 0) goto L_0x0f7d
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r7 == 0) goto L_0x0e9f
            goto L_0x0f7d
        L_0x0e9f:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r7 == 0) goto L_0x0eb2
            r0 = 2131624559(0x7f0e026f, float:1.8876301E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageLiveLocation"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0eb2:
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x127d
            boolean r1 = r17.isSticker()
            if (r1 != 0) goto L_0x0var_
            boolean r1 = r17.isAnimatedSticker()
            if (r1 == 0) goto L_0x0ec4
            goto L_0x0var_
        L_0x0ec4:
            boolean r1 = r17.isGif()
            if (r1 == 0) goto L_0x0var_
            if (r18 != 0) goto L_0x0var_
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0var_
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r5] = r0
            r0 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            r19[r6] = r5
            goto L_0x127e
        L_0x0var_:
            r0 = 2131624558(0x7f0e026e, float:1.88763E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageGIF"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0var_:
            if (r18 != 0) goto L_0x0var_
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0var_
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1[r5] = r0
            r0 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            r19[r6] = r5
            goto L_0x127e
        L_0x0var_:
            r0 = 2131624556(0x7f0e026c, float:1.8876295E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageDocument"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0var_:
            java.lang.String r0 = r17.getStickerEmoji()
            if (r0 == 0) goto L_0x0f6e
            r1 = 2131624568(0x7f0e0278, float:1.887632E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x0f6e:
            r0 = 2131624567(0x7f0e0277, float:1.8876317E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x0f7d:
            r0 = 2131624560(0x7f0e0270, float:1.8876303E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageMap"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0f8c:
            boolean r4 = r17.isMediaEmpty()
            r7 = 2131625779(0x7f0e0733, float:1.8878776E38)
            java.lang.String r8 = "NotificationMessageGroupText"
            if (r4 == 0) goto L_0x0fce
            if (r18 != 0) goto L_0x0fba
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            if (r1 == 0) goto L_0x0fba
            int r1 = r1.length()
            if (r1 == 0) goto L_0x0fba
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2 = 2
            r1[r2] = r0
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x127e
        L_0x0fba:
            r2 = 2
            r0 = 2131625772(0x7f0e072c, float:1.8878761E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupNoText"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x0fce:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r4.media
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r9 == 0) goto L_0x101e
            if (r18 != 0) goto L_0x100a
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x100a
            java.lang.String r1 = r4.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x100a
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x127e
        L_0x100a:
            r2 = 2
            r0 = 2131625773(0x7f0e072d, float:1.8878763E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x101e:
            boolean r4 = r17.isVideo()
            if (r4 == 0) goto L_0x106e
            if (r18 != 0) goto L_0x105a
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x105a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x105a
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r4 = 2
            r1[r4] = r0
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x127e
        L_0x105a:
            r4 = 2
            r0 = 2131625780(0x7f0e0734, float:1.8878778E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = " "
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x106e:
            r4 = 2
            boolean r9 = r17.isVoice()
            if (r9 == 0) goto L_0x1088
            r0 = 2131625762(0x7f0e0722, float:1.8878741E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupAudio"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x1088:
            boolean r9 = r17.isRoundVideo()
            if (r9 == 0) goto L_0x10a1
            r0 = 2131625776(0x7f0e0730, float:1.887877E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupRound"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x10a1:
            boolean r9 = r17.isMusic()
            if (r9 == 0) goto L_0x10ba
            r0 = 2131625771(0x7f0e072b, float:1.887876E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMusic"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x10ba:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r9 == 0) goto L_0x10e2
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r4
            r0 = 2131625763(0x7f0e0723, float:1.8878743E38)
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = r4.first_name
            java.lang.String r3 = r4.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupContact2"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x10e2:
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r9 == 0) goto L_0x111e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$TL_poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x1106
            r1 = 2131625775(0x7f0e072f, float:1.8878767E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            java.lang.String r3 = r13.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationMessageGroupQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x1106:
            r3 = 2
            r1 = 2131625774(0x7f0e072e, float:1.8878765E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            java.lang.String r4 = r13.title
            r2[r5] = r4
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x111e:
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r9 == 0) goto L_0x113c
            r0 = 2131625765(0x7f0e0725, float:1.8878747E38)
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            org.telegram.tgnet.TLRPC$TL_game r2 = r4.game
            java.lang.String r2 = r2.title
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupGame"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x113c:
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r9 != 0) goto L_0x123a
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r9 == 0) goto L_0x1146
            goto L_0x123a
        L_0x1146:
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r9 == 0) goto L_0x115e
            r0 = 2131625769(0x7f0e0729, float:1.8878755E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x115e:
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x127d
            boolean r1 = r17.isSticker()
            if (r1 != 0) goto L_0x120a
            boolean r1 = r17.isAnimatedSticker()
            if (r1 == 0) goto L_0x1170
            goto L_0x120a
        L_0x1170:
            boolean r1 = r17.isGif()
            if (r1 == 0) goto L_0x11c0
            if (r18 != 0) goto L_0x11ac
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x11ac
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x11ac
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r6] = r12
            java.lang.String r3 = r13.title
            r1[r5] = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x127e
        L_0x11ac:
            r2 = 2
            r0 = 2131625767(0x7f0e0727, float:1.8878751E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupGif"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x11c0:
            if (r18 != 0) goto L_0x11f6
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x11f6
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x11f6
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x127e
        L_0x11f6:
            r2 = 2
            r0 = 2131625764(0x7f0e0724, float:1.8878745E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupDocument"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x120a:
            java.lang.String r0 = r17.getStickerEmoji()
            if (r0 == 0) goto L_0x1226
            r1 = 2131625778(0x7f0e0732, float:1.8878774E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r6] = r12
            java.lang.String r3 = r13.title
            r2[r5] = r3
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0404
        L_0x1226:
            r3 = 2
            r0 = 2131625777(0x7f0e0731, float:1.8878772E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0404
        L_0x123a:
            r3 = 2
            r0 = 2131625770(0x7f0e072a, float:1.8878757E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMap"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x124d:
            if (r20 == 0) goto L_0x1251
            r20[r6] = r6
        L_0x1251:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r13)
            if (r0 == 0) goto L_0x1269
            boolean r0 = r13.megagroup
            if (r0 != 0) goto L_0x1269
            r0 = 2131624562(0x7f0e0272, float:1.8876307E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x1269:
            r0 = 2131625772(0x7f0e072c, float:1.8878761E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r12
            java.lang.String r2 = r13.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupNoText"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x127e
        L_0x127c:
            r1 = 0
        L_0x127d:
            r11 = r1
        L_0x127e:
            return r11
        L_0x127f:
            r0 = 2131627228(0x7f0e0cdc, float:1.8881715E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
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
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) (i * 60 * 1000)), service);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000e, code lost:
        r3 = r3.action;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isPersonalMessage(org.telegram.messenger.MessageObject r3) {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r3.to_id
            if (r0 == 0) goto L_0x0018
            int r1 = r0.chat_id
            if (r1 != 0) goto L_0x0018
            int r0 = r0.channel_id
            if (r0 != 0) goto L_0x0018
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            if (r3 == 0) goto L_0x0016
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty
            if (r3 == 0) goto L_0x0018
        L_0x0016:
            r3 = 1
            goto L_0x0019
        L_0x0018:
            r3 = 0
        L_0x0019:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.isPersonalMessage(org.telegram.messenger.MessageObject):boolean");
    }

    private int getNotifyOverride(SharedPreferences sharedPreferences, long j) {
        int i = sharedPreferences.getInt("notify2_" + j, -1);
        if (i != 3) {
            return i;
        }
        if (sharedPreferences.getInt("notifyuntil_" + j, 0) >= getConnectionsManager().getCurrentTime()) {
            return 2;
        }
        return i;
    }

    public /* synthetic */ void lambda$showNotifications$23$NotificationsController() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$showNotifications$23$NotificationsController();
            }
        });
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$hideNotifications$24$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$hideNotifications$24$NotificationsController() {
        notificationManager.cancel(this.notificationId);
        this.lastWearNotifiedMessageId.clear();
        for (int i = 0; i < this.wearNotificationsIds.size(); i++) {
            notificationManager.cancel(this.wearNotificationsIds.valueAt(i).intValue());
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
                notificationManager.cancel(this.wearNotificationsIds.valueAt(i).intValue());
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread($$Lambda$NotificationsController$2v2nyML5dTCxIdrQrE6xmPJzze8.INSTANCE);
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
            FileLog.e((Throwable) e);
        }
    }

    private void playInChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                if (getNotifyOverride(getAccountInstance().getNotificationsSettings(), this.opened_dialog_id) != 2) {
                    notificationsQueue.postRunnable(new Runnable() {
                        public final void run() {
                            NotificationsController.this.lambda$playInChatSound$27$NotificationsController();
                        }
                    });
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public /* synthetic */ void lambda$playInChatSound$27$NotificationsController() {
        if (Math.abs(System.currentTimeMillis() - this.lastSoundPlay) > 500) {
            try {
                if (this.soundPool == null) {
                    this.soundPool = new SoundPool(3, 1, 0);
                    this.soundPool.setOnLoadCompleteListener($$Lambda$NotificationsController$NULIntVdHQSUoPd6L0mVTH6J8n0.INSTANCE);
                }
                if (this.soundIn == 0 && !this.soundInLoaded) {
                    this.soundInLoaded = true;
                    this.soundIn = this.soundPool.load(ApplicationLoader.applicationContext, NUM, 1);
                }
                if (this.soundIn != 0) {
                    try {
                        this.soundPool.play(this.soundIn, 1.0f, 1.0f, 1, 0, 1.0f);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    static /* synthetic */ void lambda$null$26(SoundPool soundPool2, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool2.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private void scheduleNotificationDelay(boolean z) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay notification start, onlineReason = " + z);
            }
            this.notificationDelayWakelock.acquire(10000);
            notificationsQueue.cancelRunnable(this.notificationDelayRunnable);
            notificationsQueue.postRunnable(this.notificationDelayRunnable, (long) (z ? 3000 : 1000));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    /* access modifiers changed from: protected */
    public void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$repeatNotificationMaybe$28$NotificationsController();
            }
        });
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
        notificationsQueue.postRunnable(new Runnable(j) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationsController.this.lambda$deleteNotificationChannel$29$NotificationsController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$deleteNotificationChannel$29$NotificationsController(long j) {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
                String str = "org.telegram.key" + j;
                String string = notificationsSettings.getString(str, (String) null);
                if (string != null) {
                    notificationsSettings.edit().remove(str).remove(str + "_s").commit();
                    systemNotificationManager.deleteNotificationChannel(string);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    @TargetApi(26)
    public void deleteAllNotificationChannels() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$deleteAllNotificationChannels$30$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$deleteAllNotificationChannels$30$NotificationsController() {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
                Map<String, ?> all = notificationsSettings.getAll();
                SharedPreferences.Editor edit = notificationsSettings.edit();
                for (Map.Entry next : all.entrySet()) {
                    String str = (String) next.getKey();
                    if (str.startsWith("org.telegram.key")) {
                        if (!str.endsWith("_s")) {
                            systemNotificationManager.deleteNotificationChannel((String) next.getValue());
                        }
                        edit.remove(str);
                    }
                }
                edit.commit();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    @TargetApi(26)
    private String validateChannelId(long j, String str, long[] jArr, int i, Uri uri, int i2, long[] jArr2, Uri uri2, int i3) {
        long j2 = j;
        long[] jArr3 = jArr;
        int i4 = i;
        Uri uri3 = uri;
        int i5 = i2;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        String str2 = "org.telegram.key" + j2;
        String string = notificationsSettings.getString(str2, (String) null);
        String string2 = notificationsSettings.getString(str2 + "_s", (String) null);
        StringBuilder sb = new StringBuilder();
        boolean z = ((int) j2) == 0;
        int i6 = 0;
        while (i6 < jArr3.length) {
            sb.append(jArr3[i6]);
            i6++;
            str2 = str2;
        }
        String str3 = str2;
        sb.append(i4);
        if (uri3 != null) {
            sb.append(uri.toString());
        }
        sb.append(i5);
        if (z) {
            sb.append("secret");
        }
        String MD5 = Utilities.MD5(sb.toString());
        if (string != null && !string2.equals(MD5)) {
            systemNotificationManager.deleteNotificationChannel(string);
            string = null;
        }
        if (string == null) {
            string = this.currentAccount + "channel" + j2 + "_" + Utilities.random.nextLong();
            NotificationChannel notificationChannel = new NotificationChannel(string, z ? LocaleController.getString("SecretChatName", NUM) : str, i5);
            if (i4 != 0) {
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(i4);
            }
            if (!isEmptyVibration(jArr3)) {
                notificationChannel.enableVibration(true);
                if (jArr3 != null && jArr3.length > 0) {
                    notificationChannel.setVibrationPattern(jArr3);
                }
            } else {
                notificationChannel.enableVibration(false);
            }
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setContentType(4);
            builder.setUsage(5);
            if (uri3 != null) {
                notificationChannel.setSound(uri3, builder.build());
            } else {
                notificationChannel.setSound((Uri) null, builder.build());
            }
            systemNotificationManager.createNotificationChannel(notificationChannel);
            String str4 = str3;
            notificationsSettings.edit().putString(str4, string).putString(str4 + "_s", MD5).commit();
        }
        return string;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v13, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: java.lang.Object} */
    /* JADX WARNING: type inference failed for: r11v17 */
    /* JADX WARNING: type inference failed for: r11v18 */
    /* JADX WARNING: type inference failed for: r11v55 */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x0860, code lost:
        if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x0862;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:430:0x0913 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x02b0 A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02e7 A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02ec A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02ed A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02f2 A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02f6 A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0309 A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x030a A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0341 A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03ba A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c1 A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x03c6 A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03f8  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a8 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04e6 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x04f9 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x04fc A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0506 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x051b A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x051c A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x054c A[SYNTHETIC, Splitter:B:287:0x054c] */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x0580 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x058c A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x05a2 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x05ba A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x0660 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x06d9 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x07c2 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x080c  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0815  */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x0858 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0869 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0927 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x0931 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x0938 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x09bd A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0a7f A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0ab3 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0acc A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0aea A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0b03 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00fe A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x012b A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0130 A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01bb A[Catch:{ Exception -> 0x0b0f }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x020c  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0215  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0275 A[Catch:{ Exception -> 0x02e2 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r46) {
        /*
            r45 = this;
            r12 = r45
            r13 = r46
            java.lang.String r1 = "currentAccount"
            org.telegram.messenger.UserConfig r2 = r45.getUserConfig()
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x0b15
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r12.pushMessages
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0b15
            boolean r2 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r2 != 0) goto L_0x0024
            int r2 = r12.currentAccount
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            if (r2 == r3) goto L_0x0024
            goto L_0x0b15
        L_0x0024:
            org.telegram.tgnet.ConnectionsManager r2 = r45.getConnectionsManager()     // Catch:{ Exception -> 0x0b0f }
            r2.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0b0f }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r12.pushMessages     // Catch:{ Exception -> 0x0b0f }
            r3 = 0
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x0b0f }
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2     // Catch:{ Exception -> 0x0b0f }
            org.telegram.messenger.AccountInstance r4 = r45.getAccountInstance()     // Catch:{ Exception -> 0x0b0f }
            android.content.SharedPreferences r4 = r4.getNotificationsSettings()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r5 = "dismissDate"
            int r5 = r4.getInt(r5, r3)     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            int r6 = r6.date     // Catch:{ Exception -> 0x0b0f }
            if (r6 > r5) goto L_0x004c
            r45.dismissNotification()     // Catch:{ Exception -> 0x0b0f }
            return
        L_0x004c:
            long r6 = r2.getDialogId()     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$Message r8 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            boolean r8 = r8.mentioned     // Catch:{ Exception -> 0x0b0f }
            if (r8 == 0) goto L_0x005c
            org.telegram.tgnet.TLRPC$Message r8 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            int r8 = r8.from_id     // Catch:{ Exception -> 0x0b0f }
            long r8 = (long) r8     // Catch:{ Exception -> 0x0b0f }
            goto L_0x005d
        L_0x005c:
            r8 = r6
        L_0x005d:
            r2.getId()     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.to_id     // Catch:{ Exception -> 0x0b0f }
            int r10 = r10.chat_id     // Catch:{ Exception -> 0x0b0f }
            if (r10 == 0) goto L_0x006f
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.to_id     // Catch:{ Exception -> 0x0b0f }
            int r10 = r10.chat_id     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0075
        L_0x006f:
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.to_id     // Catch:{ Exception -> 0x0b0f }
            int r10 = r10.channel_id     // Catch:{ Exception -> 0x0b0f }
        L_0x0075:
            org.telegram.tgnet.TLRPC$Message r11 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$Peer r11 = r11.to_id     // Catch:{ Exception -> 0x0b0f }
            int r11 = r11.user_id     // Catch:{ Exception -> 0x0b0f }
            if (r11 != 0) goto L_0x0082
            org.telegram.tgnet.TLRPC$Message r11 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            int r11 = r11.from_id     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0090
        L_0x0082:
            org.telegram.messenger.UserConfig r14 = r45.getUserConfig()     // Catch:{ Exception -> 0x0b0f }
            int r14 = r14.getClientUserId()     // Catch:{ Exception -> 0x0b0f }
            if (r11 != r14) goto L_0x0090
            org.telegram.tgnet.TLRPC$Message r11 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            int r11 = r11.from_id     // Catch:{ Exception -> 0x0b0f }
        L_0x0090:
            org.telegram.messenger.MessagesController r14 = r45.getMessagesController()     // Catch:{ Exception -> 0x0b0f }
            java.lang.Integer r15 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$User r14 = r14.getUser(r15)     // Catch:{ Exception -> 0x0b0f }
            if (r10 == 0) goto L_0x00ba
            org.telegram.messenger.MessagesController r15 = r45.getMessagesController()     // Catch:{ Exception -> 0x0b0f }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$Chat r15 = r15.getChat(r3)     // Catch:{ Exception -> 0x0b0f }
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r15)     // Catch:{ Exception -> 0x0b0f }
            if (r3 == 0) goto L_0x00b6
            boolean r3 = r15.megagroup     // Catch:{ Exception -> 0x0b0f }
            if (r3 != 0) goto L_0x00b6
            r3 = 1
            goto L_0x00b7
        L_0x00b6:
            r3 = 0
        L_0x00b7:
            r19 = r5
            goto L_0x00be
        L_0x00ba:
            r19 = r5
            r3 = 0
            r15 = 0
        L_0x00be:
            int r5 = r12.getNotifyOverride(r4, r8)     // Catch:{ Exception -> 0x0b0f }
            r20 = r2
            r2 = -1
            r21 = r1
            r1 = 2
            if (r5 != r2) goto L_0x00cf
            boolean r2 = r12.isGlobalNotificationsEnabled((long) r6)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x00d4
        L_0x00cf:
            if (r5 == r1) goto L_0x00d3
            r2 = 1
            goto L_0x00d4
        L_0x00d3:
            r2 = 0
        L_0x00d4:
            if (r13 == 0) goto L_0x00db
            if (r2 != 0) goto L_0x00d9
            goto L_0x00db
        L_0x00d9:
            r2 = 0
            goto L_0x00dc
        L_0x00db:
            r2 = 1
        L_0x00dc:
            r22 = 1000(0x3e8, double:4.94E-321)
            if (r2 != 0) goto L_0x017d
            int r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x017d
            if (r15 == 0) goto L_0x017d
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r5.<init>()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r8 = "custom_"
            r5.append(r8)     // Catch:{ Exception -> 0x0b0f }
            r5.append(r6)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0b0f }
            r8 = 0
            boolean r5 = r4.getBoolean(r5, r8)     // Catch:{ Exception -> 0x0b0f }
            if (r5 == 0) goto L_0x012b
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r5.<init>()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r8 = "smart_max_count_"
            r5.append(r8)     // Catch:{ Exception -> 0x0b0f }
            r5.append(r6)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0b0f }
            int r5 = r4.getInt(r5, r1)     // Catch:{ Exception -> 0x0b0f }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r8.<init>()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r9 = "smart_delay_"
            r8.append(r9)     // Catch:{ Exception -> 0x0b0f }
            r8.append(r6)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0b0f }
            r9 = 180(0xb4, float:2.52E-43)
            int r8 = r4.getInt(r8, r9)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x012e
        L_0x012b:
            r8 = 180(0xb4, float:2.52E-43)
            r5 = 2
        L_0x012e:
            if (r5 == 0) goto L_0x017d
            android.util.LongSparseArray<android.graphics.Point> r9 = r12.smartNotificationsDialogs     // Catch:{ Exception -> 0x0b0f }
            java.lang.Object r9 = r9.get(r6)     // Catch:{ Exception -> 0x0b0f }
            android.graphics.Point r9 = (android.graphics.Point) r9     // Catch:{ Exception -> 0x0b0f }
            if (r9 != 0) goto L_0x014d
            android.graphics.Point r5 = new android.graphics.Point     // Catch:{ Exception -> 0x0b0f }
            long r8 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0b0f }
            long r8 = r8 / r22
            int r9 = (int) r8     // Catch:{ Exception -> 0x0b0f }
            r8 = 1
            r5.<init>(r8, r9)     // Catch:{ Exception -> 0x0b0f }
            android.util.LongSparseArray<android.graphics.Point> r8 = r12.smartNotificationsDialogs     // Catch:{ Exception -> 0x0b0f }
            r8.put(r6, r5)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x017d
        L_0x014d:
            int r1 = r9.y     // Catch:{ Exception -> 0x0b0f }
            int r1 = r1 + r8
            r8 = r2
            long r1 = (long) r1     // Catch:{ Exception -> 0x0b0f }
            long r24 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0b0f }
            long r24 = r24 / r22
            int r26 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1))
            if (r26 >= 0) goto L_0x0168
            long r1 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0b0f }
            long r1 = r1 / r22
            int r2 = (int) r1     // Catch:{ Exception -> 0x0b0f }
            r1 = 1
            r9.set(r1, r2)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x017e
        L_0x0168:
            int r1 = r9.x     // Catch:{ Exception -> 0x0b0f }
            if (r1 >= r5) goto L_0x017a
            r2 = 1
            int r1 = r1 + r2
            long r24 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0b0f }
            r2 = r14
            long r13 = r24 / r22
            int r5 = (int) r13     // Catch:{ Exception -> 0x0b0f }
            r9.set(r1, r5)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x017f
        L_0x017a:
            r2 = r14
            r8 = 1
            goto L_0x017f
        L_0x017d:
            r8 = r2
        L_0x017e:
            r2 = r14
        L_0x017f:
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r1 = r1.getPath()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r5 = "EnableInAppSounds"
            r9 = 1
            boolean r5 = r4.getBoolean(r5, r9)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r13 = "EnableInAppVibrate"
            boolean r13 = r4.getBoolean(r13, r9)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r14 = "EnableInAppPreview"
            boolean r14 = r4.getBoolean(r14, r9)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r9 = "EnableInAppPriority"
            r24 = r14
            r14 = 0
            boolean r9 = r4.getBoolean(r9, r14)     // Catch:{ Exception -> 0x0b0f }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r14.<init>()     // Catch:{ Exception -> 0x0b0f }
            r25 = r2
            java.lang.String r2 = "custom_"
            r14.append(r2)     // Catch:{ Exception -> 0x0b0f }
            r14.append(r6)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r14.toString()     // Catch:{ Exception -> 0x0b0f }
            r14 = 0
            boolean r2 = r4.getBoolean(r2, r14)     // Catch:{ Exception -> 0x0b0f }
            if (r2 == 0) goto L_0x020c
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r14.<init>()     // Catch:{ Exception -> 0x0b0f }
            r27 = r15
            java.lang.String r15 = "vibrate_"
            r14.append(r15)     // Catch:{ Exception -> 0x0b0f }
            r14.append(r6)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x0b0f }
            r15 = 0
            int r14 = r4.getInt(r14, r15)     // Catch:{ Exception -> 0x0b0f }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r15.<init>()     // Catch:{ Exception -> 0x0b0f }
            r28 = r14
            java.lang.String r14 = "priority_"
            r15.append(r14)     // Catch:{ Exception -> 0x0b0f }
            r15.append(r6)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r14 = r15.toString()     // Catch:{ Exception -> 0x0b0f }
            r15 = 3
            int r14 = r4.getInt(r14, r15)     // Catch:{ Exception -> 0x0b0f }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r15.<init>()     // Catch:{ Exception -> 0x0b0f }
            r29 = r14
            java.lang.String r14 = "sound_path_"
            r15.append(r14)     // Catch:{ Exception -> 0x0b0f }
            r15.append(r6)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r14 = r15.toString()     // Catch:{ Exception -> 0x0b0f }
            r15 = 0
            java.lang.String r14 = r4.getString(r14, r15)     // Catch:{ Exception -> 0x0b0f }
            r15 = r14
            r14 = r28
            r12 = r29
            r28 = r8
            goto L_0x0213
        L_0x020c:
            r27 = r15
            r28 = r8
            r12 = 3
            r14 = 0
            r15 = 0
        L_0x0213:
            if (r10 == 0) goto L_0x0275
            if (r3 == 0) goto L_0x0246
            if (r15 == 0) goto L_0x0221
            boolean r3 = r15.equals(r1)     // Catch:{ Exception -> 0x02e2 }
            if (r3 == 0) goto L_0x0221
            r15 = 0
            goto L_0x0229
        L_0x0221:
            if (r15 != 0) goto L_0x0229
            java.lang.String r3 = "ChannelSoundPath"
            java.lang.String r15 = r4.getString(r3, r1)     // Catch:{ Exception -> 0x02e2 }
        L_0x0229:
            java.lang.String r3 = "vibrate_channel"
            r8 = 0
            int r3 = r4.getInt(r3, r8)     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r8 = "priority_channel"
            r30 = r3
            r3 = 1
            int r8 = r4.getInt(r8, r3)     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r3 = "ChannelLed"
            r31 = r8
            r8 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r8 = r4.getInt(r3, r8)     // Catch:{ Exception -> 0x02e2 }
            goto L_0x02a5
        L_0x0246:
            if (r15 == 0) goto L_0x0250
            boolean r3 = r15.equals(r1)     // Catch:{ Exception -> 0x02e2 }
            if (r3 == 0) goto L_0x0250
            r15 = 0
            goto L_0x0258
        L_0x0250:
            if (r15 != 0) goto L_0x0258
            java.lang.String r3 = "GroupSoundPath"
            java.lang.String r15 = r4.getString(r3, r1)     // Catch:{ Exception -> 0x02e2 }
        L_0x0258:
            java.lang.String r3 = "vibrate_group"
            r8 = 0
            int r3 = r4.getInt(r3, r8)     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r8 = "priority_group"
            r30 = r3
            r3 = 1
            int r8 = r4.getInt(r8, r3)     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r3 = "GroupLed"
            r31 = r8
            r8 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r8 = r4.getInt(r3, r8)     // Catch:{ Exception -> 0x02e2 }
            goto L_0x02a5
        L_0x0275:
            if (r11 == 0) goto L_0x02a8
            if (r15 == 0) goto L_0x0281
            boolean r3 = r15.equals(r1)     // Catch:{ Exception -> 0x02e2 }
            if (r3 == 0) goto L_0x0281
            r15 = 0
            goto L_0x0289
        L_0x0281:
            if (r15 != 0) goto L_0x0289
            java.lang.String r3 = "GlobalSoundPath"
            java.lang.String r15 = r4.getString(r3, r1)     // Catch:{ Exception -> 0x02e2 }
        L_0x0289:
            java.lang.String r3 = "vibrate_messages"
            r8 = 0
            int r3 = r4.getInt(r3, r8)     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r8 = "priority_messages"
            r30 = r3
            r3 = 1
            int r8 = r4.getInt(r8, r3)     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r3 = "MessagesLed"
            r31 = r8
            r8 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r8 = r4.getInt(r3, r8)     // Catch:{ Exception -> 0x02e2 }
        L_0x02a5:
            r3 = r30
            goto L_0x02ae
        L_0x02a8:
            r8 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r3 = 0
            r31 = 0
        L_0x02ae:
            if (r2 == 0) goto L_0x02e7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02e2 }
            r2.<init>()     // Catch:{ Exception -> 0x02e2 }
            r29 = r8
            java.lang.String r8 = "color_"
            r2.append(r8)     // Catch:{ Exception -> 0x02e2 }
            r2.append(r6)     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x02e2 }
            boolean r2 = r4.contains(r2)     // Catch:{ Exception -> 0x02e2 }
            if (r2 == 0) goto L_0x02e9
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02e2 }
            r2.<init>()     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r8 = "color_"
            r2.append(r8)     // Catch:{ Exception -> 0x02e2 }
            r2.append(r6)     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x02e2 }
            r8 = 0
            int r2 = r4.getInt(r2, r8)     // Catch:{ Exception -> 0x02e2 }
            r29 = r2
            goto L_0x02e9
        L_0x02e2:
            r0 = move-exception
            r12 = r45
            goto L_0x0b10
        L_0x02e7:
            r29 = r8
        L_0x02e9:
            r2 = 3
            if (r12 == r2) goto L_0x02ed
            goto L_0x02ef
        L_0x02ed:
            r12 = r31
        L_0x02ef:
            r4 = 4
            if (r3 != r4) goto L_0x02f6
            r3 = 0
            r4 = 2
            r8 = 1
            goto L_0x02f8
        L_0x02f6:
            r4 = 2
            r8 = 0
        L_0x02f8:
            if (r3 != r4) goto L_0x02ff
            r4 = 1
            if (r14 == r4) goto L_0x030b
            if (r14 == r2) goto L_0x030b
        L_0x02ff:
            r2 = 2
            if (r3 == r2) goto L_0x0304
            if (r14 == r2) goto L_0x030b
        L_0x0304:
            if (r14 == 0) goto L_0x030a
            r2 = 4
            if (r14 == r2) goto L_0x030a
            goto L_0x030b
        L_0x030a:
            r14 = r3
        L_0x030b:
            boolean r2 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x02e2 }
            if (r2 != 0) goto L_0x031f
            if (r5 != 0) goto L_0x0312
            r15 = 0
        L_0x0312:
            if (r13 != 0) goto L_0x0315
            r14 = 2
        L_0x0315:
            if (r9 != 0) goto L_0x031a
            r2 = 2
            r3 = 0
            goto L_0x0321
        L_0x031a:
            r2 = 2
            if (r12 != r2) goto L_0x0320
            r3 = 1
            goto L_0x0321
        L_0x031f:
            r2 = 2
        L_0x0320:
            r3 = r12
        L_0x0321:
            if (r8 == 0) goto L_0x0337
            if (r14 == r2) goto L_0x0337
            android.media.AudioManager r2 = audioManager     // Catch:{ Exception -> 0x0332 }
            int r2 = r2.getRingerMode()     // Catch:{ Exception -> 0x0332 }
            if (r2 == 0) goto L_0x0337
            r4 = 1
            if (r2 == r4) goto L_0x0337
            r14 = 2
            goto L_0x0337
        L_0x0332:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x02e2 }
        L_0x0337:
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02e2 }
            r4 = 100
            r9 = 26
            r12 = 0
            if (r2 < r9) goto L_0x03ba
            r2 = 2
            if (r14 != r2) goto L_0x034e
            long[] r9 = new long[r2]     // Catch:{ Exception -> 0x02e2 }
            r2 = 0
            r9[r2] = r12     // Catch:{ Exception -> 0x02e2 }
            r2 = 1
            r9[r2] = r12     // Catch:{ Exception -> 0x02e2 }
            r8 = r9
            goto L_0x0378
        L_0x034e:
            r2 = 1
            if (r14 != r2) goto L_0x0360
            r9 = 4
            long[] r8 = new long[r9]     // Catch:{ Exception -> 0x02e2 }
            r9 = 0
            r8[r9] = r12     // Catch:{ Exception -> 0x02e2 }
            r8[r2] = r4     // Catch:{ Exception -> 0x02e2 }
            r2 = 2
            r8[r2] = r12     // Catch:{ Exception -> 0x02e2 }
            r2 = 3
            r8[r2] = r4     // Catch:{ Exception -> 0x02e2 }
            goto L_0x0378
        L_0x0360:
            if (r14 == 0) goto L_0x0375
            r2 = 4
            if (r14 != r2) goto L_0x0366
            goto L_0x0375
        L_0x0366:
            r2 = 3
            if (r14 != r2) goto L_0x0373
            r2 = 2
            long[] r8 = new long[r2]     // Catch:{ Exception -> 0x02e2 }
            r2 = 0
            r8[r2] = r12     // Catch:{ Exception -> 0x02e2 }
            r2 = 1
            r8[r2] = r22     // Catch:{ Exception -> 0x02e2 }
            goto L_0x0378
        L_0x0373:
            r8 = 0
            goto L_0x0378
        L_0x0375:
            r2 = 0
            long[] r8 = new long[r2]     // Catch:{ Exception -> 0x02e2 }
        L_0x0378:
            if (r15 == 0) goto L_0x0390
            java.lang.String r2 = "NoSound"
            boolean r2 = r15.equals(r2)     // Catch:{ Exception -> 0x02e2 }
            if (r2 != 0) goto L_0x0390
            boolean r2 = r15.equals(r1)     // Catch:{ Exception -> 0x02e2 }
            if (r2 == 0) goto L_0x038b
            android.net.Uri r2 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x02e2 }
            goto L_0x0391
        L_0x038b:
            android.net.Uri r2 = android.net.Uri.parse(r15)     // Catch:{ Exception -> 0x02e2 }
            goto L_0x0391
        L_0x0390:
            r2 = 0
        L_0x0391:
            if (r3 != 0) goto L_0x0399
            r32 = r2
            r9 = r8
            r33 = 3
            goto L_0x03bf
        L_0x0399:
            r9 = 1
            if (r3 == r9) goto L_0x03b4
            r9 = 2
            if (r3 != r9) goto L_0x03a0
            goto L_0x03b4
        L_0x03a0:
            r9 = 4
            if (r3 != r9) goto L_0x03a9
            r32 = r2
            r9 = r8
            r33 = 1
            goto L_0x03bf
        L_0x03a9:
            r9 = 5
            r32 = r2
            if (r3 != r9) goto L_0x03b2
            r9 = r8
            r33 = 2
            goto L_0x03bf
        L_0x03b2:
            r9 = r8
            goto L_0x03bd
        L_0x03b4:
            r32 = r2
            r9 = r8
            r33 = 4
            goto L_0x03bf
        L_0x03ba:
            r9 = 0
            r32 = 0
        L_0x03bd:
            r33 = 0
        L_0x03bf:
            if (r28 == 0) goto L_0x03c6
            r3 = 0
            r8 = 0
            r14 = 0
            r15 = 0
            goto L_0x03c8
        L_0x03c6:
            r8 = r29
        L_0x03c8:
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x02e2 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02e2 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r5 = org.telegram.ui.LaunchActivity.class
            r2.<init>(r4, r5)     // Catch:{ Exception -> 0x02e2 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02e2 }
            r4.<init>()     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r5 = "com.tmessages.openchat"
            r4.append(r5)     // Catch:{ Exception -> 0x02e2 }
            double r12 = java.lang.Math.random()     // Catch:{ Exception -> 0x02e2 }
            r4.append(r12)     // Catch:{ Exception -> 0x02e2 }
            r5 = 2147483647(0x7fffffff, float:NaN)
            r4.append(r5)     // Catch:{ Exception -> 0x02e2 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x02e2 }
            r2.setAction(r4)     // Catch:{ Exception -> 0x02e2 }
            r4 = 32768(0x8000, float:4.5918E-41)
            r2.setFlags(r4)     // Catch:{ Exception -> 0x02e2 }
            int r4 = (int) r6
            if (r4 == 0) goto L_0x04a8
            r12 = r45
            android.util.LongSparseArray<java.lang.Integer> r5 = r12.pushDialogs     // Catch:{ Exception -> 0x0b0f }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0b0f }
            r13 = 1
            if (r5 != r13) goto L_0x0413
            if (r10 == 0) goto L_0x040b
            java.lang.String r5 = "chatId"
            r2.putExtra(r5, r10)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0413
        L_0x040b:
            if (r11 == 0) goto L_0x0413
            java.lang.String r5 = "userId"
            r2.putExtra(r5, r11)     // Catch:{ Exception -> 0x0b0f }
        L_0x0413:
            boolean r5 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b0f }
            if (r5 != 0) goto L_0x049d
            boolean r5 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b0f }
            if (r5 == 0) goto L_0x041f
            goto L_0x049d
        L_0x041f:
            android.util.LongSparseArray<java.lang.Integer> r5 = r12.pushDialogs     // Catch:{ Exception -> 0x0b0f }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0b0f }
            r11 = 1
            if (r5 != r11) goto L_0x0496
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r11 = 28
            if (r5 >= r11) goto L_0x0496
            if (r27 == 0) goto L_0x0466
            r5 = r27
            org.telegram.tgnet.TLRPC$ChatPhoto r11 = r5.photo     // Catch:{ Exception -> 0x0b0f }
            if (r11 == 0) goto L_0x045f
            org.telegram.tgnet.TLRPC$ChatPhoto r11 = r5.photo     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small     // Catch:{ Exception -> 0x0b0f }
            if (r11 == 0) goto L_0x045f
            org.telegram.tgnet.TLRPC$ChatPhoto r11 = r5.photo     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small     // Catch:{ Exception -> 0x0b0f }
            r27 = r14
            long r13 = r11.volume_id     // Catch:{ Exception -> 0x0b0f }
            r34 = 0
            int r11 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1))
            if (r11 == 0) goto L_0x0461
            org.telegram.tgnet.TLRPC$ChatPhoto r11 = r5.photo     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small     // Catch:{ Exception -> 0x0b0f }
            int r11 = r11.local_id     // Catch:{ Exception -> 0x0b0f }
            if (r11 == 0) goto L_0x0461
            org.telegram.tgnet.TLRPC$ChatPhoto r11 = r5.photo     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small     // Catch:{ Exception -> 0x0b0f }
            r29 = r8
            r13 = r11
            r11 = r25
        L_0x045b:
            r25 = r9
            goto L_0x04d2
        L_0x045f:
            r27 = r14
        L_0x0461:
            r29 = r8
            r11 = r25
            goto L_0x04a5
        L_0x0466:
            r5 = r27
            r27 = r14
            if (r25 == 0) goto L_0x0493
            r11 = r25
            org.telegram.tgnet.TLRPC$UserProfilePhoto r13 = r11.photo     // Catch:{ Exception -> 0x0b0f }
            if (r13 == 0) goto L_0x04ce
            org.telegram.tgnet.TLRPC$UserProfilePhoto r13 = r11.photo     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$FileLocation r13 = r13.photo_small     // Catch:{ Exception -> 0x0b0f }
            if (r13 == 0) goto L_0x04ce
            org.telegram.tgnet.TLRPC$UserProfilePhoto r13 = r11.photo     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$FileLocation r13 = r13.photo_small     // Catch:{ Exception -> 0x0b0f }
            long r13 = r13.volume_id     // Catch:{ Exception -> 0x0b0f }
            r34 = 0
            int r25 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1))
            if (r25 == 0) goto L_0x04ce
            org.telegram.tgnet.TLRPC$UserProfilePhoto r13 = r11.photo     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$FileLocation r13 = r13.photo_small     // Catch:{ Exception -> 0x0b0f }
            int r13 = r13.local_id     // Catch:{ Exception -> 0x0b0f }
            if (r13 == 0) goto L_0x04ce
            org.telegram.tgnet.TLRPC$UserProfilePhoto r13 = r11.photo     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$FileLocation r13 = r13.photo_small     // Catch:{ Exception -> 0x0b0f }
            r29 = r8
            goto L_0x045b
        L_0x0493:
            r11 = r25
            goto L_0x04ce
        L_0x0496:
            r11 = r25
            r5 = r27
            r27 = r14
            goto L_0x04ce
        L_0x049d:
            r11 = r25
            r5 = r27
            r27 = r14
            r29 = r8
        L_0x04a5:
            r25 = r9
            goto L_0x04d1
        L_0x04a8:
            r12 = r45
            r11 = r25
            r5 = r27
            r27 = r14
            android.util.LongSparseArray<java.lang.Integer> r13 = r12.pushDialogs     // Catch:{ Exception -> 0x0b0f }
            int r13 = r13.size()     // Catch:{ Exception -> 0x0b0f }
            r14 = 1
            if (r13 != r14) goto L_0x04ce
            long r13 = globalSecretChatId     // Catch:{ Exception -> 0x0b0f }
            int r25 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r25 == 0) goto L_0x04ce
            java.lang.String r13 = "encId"
            r14 = 32
            r29 = r8
            r25 = r9
            long r8 = r6 >> r14
            int r9 = (int) r8     // Catch:{ Exception -> 0x0b0f }
            r2.putExtra(r13, r9)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x04d1
        L_0x04ce:
            r29 = r8
            goto L_0x04a5
        L_0x04d1:
            r13 = 0
        L_0x04d2:
            int r8 = r12.currentAccount     // Catch:{ Exception -> 0x0b0f }
            r9 = r21
            r2.putExtra(r9, r8)     // Catch:{ Exception -> 0x0b0f }
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b0f }
            r14 = 1073741824(0x40000000, float:2.0)
            r36 = r6
            r6 = 0
            android.app.PendingIntent r2 = android.app.PendingIntent.getActivity(r8, r6, r2, r14)     // Catch:{ Exception -> 0x0b0f }
            if (r10 == 0) goto L_0x04e8
            if (r5 == 0) goto L_0x04ea
        L_0x04e8:
            if (r11 != 0) goto L_0x04f5
        L_0x04ea:
            boolean r6 = r20.isFcmMessage()     // Catch:{ Exception -> 0x0b0f }
            if (r6 == 0) goto L_0x04f5
            r6 = r20
            java.lang.String r7 = r6.localName     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0500
        L_0x04f5:
            r6 = r20
            if (r5 == 0) goto L_0x04fc
            java.lang.String r7 = r5.title     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0500
        L_0x04fc:
            java.lang.String r7 = org.telegram.messenger.UserObject.getUserName(r11)     // Catch:{ Exception -> 0x0b0f }
        L_0x0500:
            boolean r8 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b0f }
            if (r8 != 0) goto L_0x050d
            boolean r8 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b0f }
            if (r8 == 0) goto L_0x050b
            goto L_0x050d
        L_0x050b:
            r8 = 0
            goto L_0x050e
        L_0x050d:
            r8 = 1
        L_0x050e:
            if (r4 == 0) goto L_0x051f
            android.util.LongSparseArray<java.lang.Integer> r4 = r12.pushDialogs     // Catch:{ Exception -> 0x0b0f }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0b0f }
            r14 = 1
            if (r4 > r14) goto L_0x051f
            if (r8 == 0) goto L_0x051c
            goto L_0x051f
        L_0x051c:
            r4 = r7
            r8 = 1
            goto L_0x0541
        L_0x051f:
            if (r8 == 0) goto L_0x0537
            if (r10 == 0) goto L_0x052d
            java.lang.String r4 = "NotificationHiddenChatName"
            r8 = 2131625747(0x7f0e0713, float:1.887871E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0540
        L_0x052d:
            java.lang.String r4 = "NotificationHiddenName"
            r8 = 2131625750(0x7f0e0716, float:1.8878717E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0540
        L_0x0537:
            java.lang.String r4 = "AppName"
            r8 = 2131624192(0x7f0e0100, float:1.8875557E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ Exception -> 0x0b0f }
        L_0x0540:
            r8 = 0
        L_0x0541:
            int r10 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r14 = ""
            r20 = r7
            r7 = 1
            if (r10 <= r7) goto L_0x0580
            android.util.LongSparseArray<java.lang.Integer> r10 = r12.pushDialogs     // Catch:{ Exception -> 0x0b0f }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0b0f }
            if (r10 != r7) goto L_0x0561
            org.telegram.messenger.UserConfig r7 = r45.getUserConfig()     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$User r7 = r7.getCurrentUser()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r7 = org.telegram.messenger.UserObject.getFirstName(r7)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0581
        L_0x0561:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r7.<init>()     // Catch:{ Exception -> 0x0b0f }
            org.telegram.messenger.UserConfig r10 = r45.getUserConfig()     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$User r10 = r10.getCurrentUser()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r10)     // Catch:{ Exception -> 0x0b0f }
            r7.append(r10)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r10 = "・"
            r7.append(r10)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0581
        L_0x0580:
            r7 = r14
        L_0x0581:
            android.util.LongSparseArray<java.lang.Integer> r10 = r12.pushDialogs     // Catch:{ Exception -> 0x0b0f }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0b0f }
            r21 = r1
            r1 = 1
            if (r10 != r1) goto L_0x0599
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r10 = 23
            if (r1 >= r10) goto L_0x0593
            goto L_0x0599
        L_0x0593:
            r40 = r3
            r39 = r15
        L_0x0597:
            r15 = r7
            goto L_0x05f4
        L_0x0599:
            android.util.LongSparseArray<java.lang.Integer> r1 = r12.pushDialogs     // Catch:{ Exception -> 0x0b0f }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0b0f }
            r10 = 1
            if (r1 != r10) goto L_0x05ba
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r1.<init>()     // Catch:{ Exception -> 0x0b0f }
            r1.append(r7)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r7 = "NewMessages"
            int r10 = r12.total_unread_count     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r10)     // Catch:{ Exception -> 0x0b0f }
            r1.append(r7)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r7 = r1.toString()     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0593
        L_0x05ba:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r1.<init>()     // Catch:{ Exception -> 0x0b0f }
            r1.append(r7)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r7 = "NotificationMessagesPeopleDisplayOrder"
            r39 = r15
            r10 = 2
            java.lang.Object[] r15 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r10 = "NewMessages"
            r40 = r3
            int r3 = r12.total_unread_count     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r10, r3)     // Catch:{ Exception -> 0x0b0f }
            r10 = 0
            r15[r10] = r3     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r3 = "FromChats"
            android.util.LongSparseArray<java.lang.Integer> r10 = r12.pushDialogs     // Catch:{ Exception -> 0x0b0f }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r10)     // Catch:{ Exception -> 0x0b0f }
            r10 = 1
            r15[r10] = r3     // Catch:{ Exception -> 0x0b0f }
            r3 = 2131625798(0x7f0e0746, float:1.8878814E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r7, r3, r15)     // Catch:{ Exception -> 0x0b0f }
            r1.append(r3)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r7 = r1.toString()     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0597
        L_0x05f4:
            androidx.core.app.NotificationCompat$Builder r10 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0b0f }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b0f }
            r10.<init>(r1)     // Catch:{ Exception -> 0x0b0f }
            r10.setContentTitle(r4)     // Catch:{ Exception -> 0x0b0f }
            r1 = 2131165724(0x7var_c, float:1.7945673E38)
            r10.setSmallIcon(r1)     // Catch:{ Exception -> 0x0b0f }
            r1 = 1
            r10.setAutoCancel(r1)     // Catch:{ Exception -> 0x0b0f }
            int r1 = r12.total_unread_count     // Catch:{ Exception -> 0x0b0f }
            r10.setNumber(r1)     // Catch:{ Exception -> 0x0b0f }
            r10.setContentIntent(r2)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r1 = r12.notificationGroup     // Catch:{ Exception -> 0x0b0f }
            r10.setGroup(r1)     // Catch:{ Exception -> 0x0b0f }
            r1 = 1
            r10.setGroupSummary(r1)     // Catch:{ Exception -> 0x0b0f }
            r10.setShowWhen(r1)     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner     // Catch:{ Exception -> 0x0b0f }
            int r1 = r1.date     // Catch:{ Exception -> 0x0b0f }
            long r1 = (long) r1     // Catch:{ Exception -> 0x0b0f }
            long r1 = r1 * r22
            r10.setWhen(r1)     // Catch:{ Exception -> 0x0b0f }
            r1 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r10.setColor(r1)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r1 = "msg"
            r10.setCategory(r1)     // Catch:{ Exception -> 0x0b0f }
            if (r5 != 0) goto L_0x0657
            if (r11 == 0) goto L_0x0657
            java.lang.String r1 = r11.phone     // Catch:{ Exception -> 0x0b0f }
            if (r1 == 0) goto L_0x0657
            java.lang.String r1 = r11.phone     // Catch:{ Exception -> 0x0b0f }
            int r1 = r1.length()     // Catch:{ Exception -> 0x0b0f }
            if (r1 <= 0) goto L_0x0657
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r1.<init>()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = "tel:+"
            r1.append(r2)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r11.phone     // Catch:{ Exception -> 0x0b0f }
            r1.append(r2)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0b0f }
            r10.addPerson(r1)     // Catch:{ Exception -> 0x0b0f }
        L_0x0657:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r12.pushMessages     // Catch:{ Exception -> 0x0b0f }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0b0f }
            r2 = 1
            if (r1 != r2) goto L_0x06d9
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r12.pushMessages     // Catch:{ Exception -> 0x0b0f }
            r3 = 0
            java.lang.Object r1 = r1.get(r3)     // Catch:{ Exception -> 0x0b0f }
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1     // Catch:{ Exception -> 0x0b0f }
            boolean[] r7 = new boolean[r2]     // Catch:{ Exception -> 0x0b0f }
            r2 = 0
            java.lang.String r11 = r12.getStringForMessage(r1, r3, r7, r2)     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner     // Catch:{ Exception -> 0x0b0f }
            boolean r1 = r1.silent     // Catch:{ Exception -> 0x0b0f }
            if (r11 != 0) goto L_0x0677
            return
        L_0x0677:
            if (r8 == 0) goto L_0x06c2
            if (r5 == 0) goto L_0x0691
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r2.<init>()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r3 = " @ "
            r2.append(r3)     // Catch:{ Exception -> 0x0b0f }
            r2.append(r4)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r11.replace(r2, r14)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x06c3
        L_0x0691:
            r2 = 0
            boolean r3 = r7[r2]     // Catch:{ Exception -> 0x0b0f }
            if (r3 == 0) goto L_0x06ac
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r2.<init>()     // Catch:{ Exception -> 0x0b0f }
            r2.append(r4)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r3 = ": "
            r2.append(r3)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r11.replace(r2, r14)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x06c3
        L_0x06ac:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r2.<init>()     // Catch:{ Exception -> 0x0b0f }
            r2.append(r4)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r3 = " "
            r2.append(r3)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r11.replace(r2, r14)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x06c3
        L_0x06c2:
            r2 = r11
        L_0x06c3:
            r10.setContentText(r2)     // Catch:{ Exception -> 0x0b0f }
            androidx.core.app.NotificationCompat$BigTextStyle r3 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0b0f }
            r3.<init>()     // Catch:{ Exception -> 0x0b0f }
            r3.bigText(r2)     // Catch:{ Exception -> 0x0b0f }
            r10.setStyle(r3)     // Catch:{ Exception -> 0x0b0f }
            r44 = r6
            r43 = r9
            r42 = r13
            goto L_0x0799
        L_0x06d9:
            r10.setContentText(r15)     // Catch:{ Exception -> 0x0b0f }
            androidx.core.app.NotificationCompat$InboxStyle r1 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0b0f }
            r1.<init>()     // Catch:{ Exception -> 0x0b0f }
            r1.setBigContentTitle(r4)     // Catch:{ Exception -> 0x0b0f }
            r2 = 10
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r12.pushMessages     // Catch:{ Exception -> 0x0b0f }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0b0f }
            int r2 = java.lang.Math.min(r2, r3)     // Catch:{ Exception -> 0x0b0f }
            r3 = 1
            boolean[] r7 = new boolean[r3]     // Catch:{ Exception -> 0x0b0f }
            r3 = 0
            r11 = 2
            r38 = 0
        L_0x06f7:
            if (r3 >= r2) goto L_0x078a
            r41 = r2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r12.pushMessages     // Catch:{ Exception -> 0x0b0f }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x0b0f }
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2     // Catch:{ Exception -> 0x0b0f }
            r44 = r6
            r43 = r9
            r42 = r13
            r9 = 0
            r13 = 0
            java.lang.String r6 = r12.getStringForMessage(r2, r9, r7, r13)     // Catch:{ Exception -> 0x0b0f }
            if (r6 == 0) goto L_0x077a
            org.telegram.tgnet.TLRPC$Message r9 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            int r9 = r9.date     // Catch:{ Exception -> 0x0b0f }
            r13 = r19
            if (r9 > r13) goto L_0x071a
            goto L_0x077c
        L_0x071a:
            r9 = 2
            if (r11 != r9) goto L_0x0723
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner     // Catch:{ Exception -> 0x0b0f }
            boolean r11 = r2.silent     // Catch:{ Exception -> 0x0b0f }
            r38 = r6
        L_0x0723:
            android.util.LongSparseArray<java.lang.Integer> r2 = r12.pushDialogs     // Catch:{ Exception -> 0x0b0f }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0b0f }
            r9 = 1
            if (r2 != r9) goto L_0x0776
            if (r8 == 0) goto L_0x0776
            if (r5 == 0) goto L_0x0746
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r2.<init>()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r9 = " @ "
            r2.append(r9)     // Catch:{ Exception -> 0x0b0f }
            r2.append(r4)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r6 = r6.replace(r2, r14)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0776
        L_0x0746:
            r2 = 0
            boolean r9 = r7[r2]     // Catch:{ Exception -> 0x0b0f }
            if (r9 == 0) goto L_0x0761
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r2.<init>()     // Catch:{ Exception -> 0x0b0f }
            r2.append(r4)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r9 = ": "
            r2.append(r9)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r6 = r6.replace(r2, r14)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0776
        L_0x0761:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r2.<init>()     // Catch:{ Exception -> 0x0b0f }
            r2.append(r4)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r9 = " "
            r2.append(r9)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r6 = r6.replace(r2, r14)     // Catch:{ Exception -> 0x0b0f }
        L_0x0776:
            r1.addLine(r6)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x077c
        L_0x077a:
            r13 = r19
        L_0x077c:
            int r3 = r3 + 1
            r19 = r13
            r2 = r41
            r13 = r42
            r9 = r43
            r6 = r44
            goto L_0x06f7
        L_0x078a:
            r44 = r6
            r43 = r9
            r42 = r13
            r1.setSummaryText(r15)     // Catch:{ Exception -> 0x0b0f }
            r10.setStyle(r1)     // Catch:{ Exception -> 0x0b0f }
            r1 = r11
            r11 = r38
        L_0x0799:
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0b0f }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b0f }
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r4 = org.telegram.messenger.NotificationDismissReceiver.class
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r3 = "messageDate"
            r4 = r44
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner     // Catch:{ Exception -> 0x0b0f }
            int r5 = r5.date     // Catch:{ Exception -> 0x0b0f }
            r2.putExtra(r3, r5)     // Catch:{ Exception -> 0x0b0f }
            int r3 = r12.currentAccount     // Catch:{ Exception -> 0x0b0f }
            r5 = r43
            r2.putExtra(r5, r3)     // Catch:{ Exception -> 0x0b0f }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b0f }
            r6 = 134217728(0x8000000, float:3.85186E-34)
            r7 = 1
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r3, r7, r2, r6)     // Catch:{ Exception -> 0x0b0f }
            r10.setDeleteIntent(r2)     // Catch:{ Exception -> 0x0b0f }
            if (r42 == 0) goto L_0x080c
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r3 = "50_50"
            r13 = r42
            r7 = 0
            android.graphics.drawable.BitmapDrawable r2 = r2.getImageFromMemory(r13, r7, r3)     // Catch:{ Exception -> 0x0b0f }
            if (r2 == 0) goto L_0x07d9
            android.graphics.Bitmap r2 = r2.getBitmap()     // Catch:{ Exception -> 0x0b0f }
            r10.setLargeIcon(r2)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x080d
        L_0x07d9:
            r2 = 1
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r2)     // Catch:{ all -> 0x080d }
            boolean r2 = r3.exists()     // Catch:{ all -> 0x080d }
            if (r2 == 0) goto L_0x080d
            r2 = 1126170624(0x43200000, float:160.0)
            r8 = 1112014848(0x42480000, float:50.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ all -> 0x080d }
            float r8 = (float) r8     // Catch:{ all -> 0x080d }
            float r2 = r2 / r8
            android.graphics.BitmapFactory$Options r8 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x080d }
            r8.<init>()     // Catch:{ all -> 0x080d }
            r9 = 1065353216(0x3var_, float:1.0)
            int r9 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r9 >= 0) goto L_0x07fb
            r2 = 1
            goto L_0x07fc
        L_0x07fb:
            int r2 = (int) r2     // Catch:{ all -> 0x080d }
        L_0x07fc:
            r8.inSampleSize = r2     // Catch:{ all -> 0x080d }
            java.lang.String r2 = r3.getAbsolutePath()     // Catch:{ all -> 0x080d }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2, r8)     // Catch:{ all -> 0x080d }
            if (r2 == 0) goto L_0x080d
            r10.setLargeIcon(r2)     // Catch:{ all -> 0x080d }
            goto L_0x080d
        L_0x080c:
            r7 = 0
        L_0x080d:
            r13 = r46
            if (r13 == 0) goto L_0x0858
            r2 = 1
            if (r1 != r2) goto L_0x0815
            goto L_0x0858
        L_0x0815:
            if (r40 != 0) goto L_0x0824
            r2 = 0
            r10.setPriority(r2)     // Catch:{ Exception -> 0x0b0f }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r3 = 26
            if (r2 < r3) goto L_0x0865
            r2 = 1
            r8 = 3
            goto L_0x0867
        L_0x0824:
            r3 = r40
            r2 = 1
            if (r3 == r2) goto L_0x084c
            r2 = 2
            if (r3 != r2) goto L_0x082e
            r2 = 1
            goto L_0x084c
        L_0x082e:
            r2 = 4
            if (r3 != r2) goto L_0x083e
            r2 = -2
            r10.setPriority(r2)     // Catch:{ Exception -> 0x0b0f }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r3 = 26
            if (r2 < r3) goto L_0x0865
            r2 = 1
            r8 = 1
            goto L_0x0867
        L_0x083e:
            r2 = 5
            if (r3 != r2) goto L_0x0865
            r2 = -1
            r10.setPriority(r2)     // Catch:{ Exception -> 0x0b0f }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r3 = 26
            if (r2 < r3) goto L_0x0865
            goto L_0x0862
        L_0x084c:
            r10.setPriority(r2)     // Catch:{ Exception -> 0x0b0f }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r3 = 26
            if (r2 < r3) goto L_0x0865
            r2 = 1
            r8 = 4
            goto L_0x0867
        L_0x0858:
            r2 = -1
            r10.setPriority(r2)     // Catch:{ Exception -> 0x0b0f }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r3 = 26
            if (r2 < r3) goto L_0x0865
        L_0x0862:
            r2 = 1
            r8 = 2
            goto L_0x0867
        L_0x0865:
            r2 = 1
            r8 = 0
        L_0x0867:
            if (r1 == r2) goto L_0x0990
            if (r28 != 0) goto L_0x0990
            boolean r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0b0f }
            if (r1 != 0) goto L_0x0871
            if (r24 == 0) goto L_0x08a0
        L_0x0871:
            int r1 = r11.length()     // Catch:{ Exception -> 0x0b0f }
            r2 = 100
            if (r1 <= r2) goto L_0x089d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b0f }
            r1.<init>()     // Catch:{ Exception -> 0x0b0f }
            r2 = 100
            r3 = 0
            java.lang.String r2 = r11.substring(r3, r2)     // Catch:{ Exception -> 0x0b0f }
            r3 = 10
            r9 = 32
            java.lang.String r2 = r2.replace(r3, r9)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = r2.trim()     // Catch:{ Exception -> 0x0b0f }
            r1.append(r2)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r2 = "..."
            r1.append(r2)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r11 = r1.toString()     // Catch:{ Exception -> 0x0b0f }
        L_0x089d:
            r10.setTicker(r11)     // Catch:{ Exception -> 0x0b0f }
        L_0x08a0:
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0b0f }
            boolean r1 = r1.isRecordingAudio()     // Catch:{ Exception -> 0x0b0f }
            if (r1 != 0) goto L_0x0924
            if (r39 == 0) goto L_0x0924
            java.lang.String r1 = "NoSound"
            r2 = r39
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0b0f }
            if (r1 != 0) goto L_0x0924
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r3 = 26
            if (r1 < r3) goto L_0x08cc
            r1 = r21
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0b0f }
            if (r1 == 0) goto L_0x08c7
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0925
        L_0x08c7:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0925
        L_0x08cc:
            r1 = r21
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0b0f }
            if (r1 == 0) goto L_0x08db
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b0f }
            r2 = 5
            r10.setSound(r1, r2)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0924
        L_0x08db:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r3 = 24
            if (r1 < r3) goto L_0x091c
            java.lang.String r1 = "file://"
            boolean r1 = r2.startsWith(r1)     // Catch:{ Exception -> 0x0b0f }
            if (r1 == 0) goto L_0x091c
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b0f }
            boolean r1 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1)     // Catch:{ Exception -> 0x0b0f }
            if (r1 != 0) goto L_0x091c
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0913 }
            java.lang.String r3 = "org.telegram.messenger.beta.provider"
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x0913 }
            java.lang.String r11 = "file://"
            java.lang.String r11 = r2.replace(r11, r14)     // Catch:{ Exception -> 0x0913 }
            r9.<init>(r11)     // Catch:{ Exception -> 0x0913 }
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r1, r3, r9)     // Catch:{ Exception -> 0x0913 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0913 }
            java.lang.String r9 = "com.android.systemui"
            r11 = 1
            r3.grantUriPermission(r9, r1, r11)     // Catch:{ Exception -> 0x0913 }
            r3 = 5
            r10.setSound(r1, r3)     // Catch:{ Exception -> 0x0913 }
            goto L_0x0924
        L_0x0913:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b0f }
            r2 = 5
            r10.setSound(r1, r2)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0924
        L_0x091c:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b0f }
            r2 = 5
            r10.setSound(r1, r2)     // Catch:{ Exception -> 0x0b0f }
        L_0x0924:
            r1 = r7
        L_0x0925:
            if (r29 == 0) goto L_0x0931
            r2 = 1000(0x3e8, float:1.401E-42)
            r3 = 1000(0x3e8, float:1.401E-42)
            r9 = r29
            r10.setLights(r9, r2, r3)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0933
        L_0x0931:
            r9 = r29
        L_0x0933:
            r14 = r27
            r2 = 2
            if (r14 == r2) goto L_0x097f
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0b0f }
            boolean r2 = r2.isRecordingAudio()     // Catch:{ Exception -> 0x0b0f }
            if (r2 == 0) goto L_0x0944
            r2 = 2
            goto L_0x097f
        L_0x0944:
            r2 = 1
            if (r14 != r2) goto L_0x095d
            r3 = 4
            long[] r3 = new long[r3]     // Catch:{ Exception -> 0x0b0f }
            r7 = 0
            r21 = 0
            r3[r7] = r21     // Catch:{ Exception -> 0x0b0f }
            r27 = 100
            r3[r2] = r27     // Catch:{ Exception -> 0x0b0f }
            r2 = 2
            r3[r2] = r21     // Catch:{ Exception -> 0x0b0f }
            r2 = 3
            r3[r2] = r27     // Catch:{ Exception -> 0x0b0f }
            r10.setVibrate(r3)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x098c
        L_0x095d:
            if (r14 == 0) goto L_0x0977
            r2 = 4
            if (r14 != r2) goto L_0x0963
            goto L_0x0977
        L_0x0963:
            r2 = 3
            if (r14 != r2) goto L_0x0975
            r2 = 2
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b0f }
            r2 = 0
            r18 = 0
            r3[r2] = r18     // Catch:{ Exception -> 0x0b0f }
            r2 = 1
            r3[r2] = r22     // Catch:{ Exception -> 0x0b0f }
            r10.setVibrate(r3)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x098c
        L_0x0975:
            r11 = r1
            goto L_0x098e
        L_0x0977:
            r2 = 2
            r10.setDefaults(r2)     // Catch:{ Exception -> 0x0b0f }
            r2 = 0
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b0f }
            goto L_0x098c
        L_0x097f:
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b0f }
            r2 = 0
            r21 = 0
            r3[r2] = r21     // Catch:{ Exception -> 0x0b0f }
            r2 = 1
            r3[r2] = r21     // Catch:{ Exception -> 0x0b0f }
            r10.setVibrate(r3)     // Catch:{ Exception -> 0x0b0f }
        L_0x098c:
            r11 = r1
            r7 = r3
        L_0x098e:
            r1 = 1
            goto L_0x09a2
        L_0x0990:
            r9 = r29
            r1 = 2
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0b0f }
            r1 = 0
            r21 = 0
            r2[r1] = r21     // Catch:{ Exception -> 0x0b0f }
            r1 = 1
            r2[r1] = r21     // Catch:{ Exception -> 0x0b0f }
            r10.setVibrate(r2)     // Catch:{ Exception -> 0x0b0f }
            r11 = r7
            r7 = r2
        L_0x09a2:
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b0f }
            if (r2 != 0) goto L_0x0a7f
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b0f }
            if (r2 != 0) goto L_0x0a7f
            long r2 = r4.getDialogId()     // Catch:{ Exception -> 0x0b0f }
            r16 = 777000(0xbdb28, double:3.83889E-318)
            int r14 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x0a7f
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup     // Catch:{ Exception -> 0x0b0f }
            if (r2 == 0) goto L_0x0a7f
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup     // Catch:{ Exception -> 0x0b0f }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r2 = r2.rows     // Catch:{ Exception -> 0x0b0f }
            int r3 = r2.size()     // Catch:{ Exception -> 0x0b0f }
            r14 = 0
            r16 = 0
        L_0x09ca:
            if (r14 >= r3) goto L_0x0a77
            java.lang.Object r17 = r2.get(r14)     // Catch:{ Exception -> 0x0b0f }
            r1 = r17
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r1 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r1     // Catch:{ Exception -> 0x0b0f }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r6 = r1.buttons     // Catch:{ Exception -> 0x0b0f }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0b0f }
            r21 = r2
            r2 = 0
        L_0x09dd:
            if (r2 >= r6) goto L_0x0a5b
            r22 = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r3 = r1.buttons     // Catch:{ Exception -> 0x0b0f }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0b0f }
            org.telegram.tgnet.TLRPC$KeyboardButton r3 = (org.telegram.tgnet.TLRPC.KeyboardButton) r3     // Catch:{ Exception -> 0x0b0f }
            r23 = r1
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0b0f }
            if (r1 == 0) goto L_0x0a3b
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0b0f }
            r24 = r6
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b0f }
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r13 = org.telegram.messenger.NotificationCallbackReceiver.class
            r1.<init>(r6, r13)     // Catch:{ Exception -> 0x0b0f }
            int r6 = r12.currentAccount     // Catch:{ Exception -> 0x0b0f }
            r1.putExtra(r5, r6)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r6 = "did"
            r13 = r8
            r29 = r9
            r8 = r36
            r1.putExtra(r6, r8)     // Catch:{ Exception -> 0x0b0f }
            byte[] r6 = r3.data     // Catch:{ Exception -> 0x0b0f }
            if (r6 == 0) goto L_0x0a17
            java.lang.String r6 = "data"
            r26 = r15
            byte[] r15 = r3.data     // Catch:{ Exception -> 0x0b0f }
            r1.putExtra(r6, r15)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0a19
        L_0x0a17:
            r26 = r15
        L_0x0a19:
            java.lang.String r6 = "mid"
            int r15 = r4.getId()     // Catch:{ Exception -> 0x0b0f }
            r1.putExtra(r6, r15)     // Catch:{ Exception -> 0x0b0f }
            java.lang.String r3 = r3.text     // Catch:{ Exception -> 0x0b0f }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b0f }
            int r15 = r12.lastButtonId     // Catch:{ Exception -> 0x0b0f }
            r44 = r4
            int r4 = r15 + 1
            r12.lastButtonId = r4     // Catch:{ Exception -> 0x0b0f }
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r6, r15, r1, r4)     // Catch:{ Exception -> 0x0b0f }
            r4 = 0
            r10.addAction(r4, r3, r1)     // Catch:{ Exception -> 0x0b0f }
            r16 = 1
            goto L_0x0a47
        L_0x0a3b:
            r44 = r4
            r24 = r6
            r13 = r8
            r29 = r9
            r26 = r15
            r8 = r36
            r4 = 0
        L_0x0a47:
            int r2 = r2 + 1
            r36 = r8
            r8 = r13
            r3 = r22
            r1 = r23
            r6 = r24
            r15 = r26
            r9 = r29
            r4 = r44
            r13 = r46
            goto L_0x09dd
        L_0x0a5b:
            r22 = r3
            r44 = r4
            r13 = r8
            r29 = r9
            r26 = r15
            r8 = r36
            r4 = 0
            int r14 = r14 + 1
            r8 = r13
            r2 = r21
            r9 = r29
            r4 = r44
            r1 = 1
            r6 = 134217728(0x8000000, float:3.85186E-34)
            r13 = r46
            goto L_0x09ca
        L_0x0a77:
            r13 = r8
            r29 = r9
            r26 = r15
            r8 = r36
            goto L_0x0a89
        L_0x0a7f:
            r13 = r8
            r29 = r9
            r26 = r15
            r8 = r36
            r4 = 0
            r16 = 0
        L_0x0a89:
            if (r16 != 0) goto L_0x0ae4
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r2 = 24
            if (r1 >= r2) goto L_0x0ae4
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0b0f }
            int r1 = r1.length()     // Catch:{ Exception -> 0x0b0f }
            if (r1 != 0) goto L_0x0ae4
            boolean r1 = r45.hasMessagesToReply()     // Catch:{ Exception -> 0x0b0f }
            if (r1 == 0) goto L_0x0ae4
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0b0f }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b0f }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r3 = org.telegram.messenger.PopupReplyReceiver.class
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x0b0f }
            int r2 = r12.currentAccount     // Catch:{ Exception -> 0x0b0f }
            r1.putExtra(r5, r2)     // Catch:{ Exception -> 0x0b0f }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r3 = 19
            if (r2 > r3) goto L_0x0acc
            r2 = 2131165436(0x7var_fc, float:1.794509E38)
            java.lang.String r3 = "Reply"
            r4 = 2131626367(0x7f0e097f, float:1.8879968E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0b0f }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b0f }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5)     // Catch:{ Exception -> 0x0b0f }
            r10.addAction(r2, r3, r1)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0ae4
        L_0x0acc:
            r2 = 2131165435(0x7var_fb, float:1.7945087E38)
            java.lang.String r3 = "Reply"
            r4 = 2131626367(0x7f0e097f, float:1.8879968E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0b0f }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b0f }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5)     // Catch:{ Exception -> 0x0b0f }
            r10.addAction(r2, r3, r1)     // Catch:{ Exception -> 0x0b0f }
        L_0x0ae4:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b0f }
            r2 = 26
            if (r1 < r2) goto L_0x0b03
            r1 = r45
            r2 = r8
            r4 = r20
            r5 = r7
            r6 = r29
            r7 = r11
            r8 = r13
            r9 = r25
            r13 = r10
            r10 = r32
            r11 = r33
            java.lang.String r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0b0f }
            r13.setChannelId(r1)     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0b04
        L_0x0b03:
            r13 = r10
        L_0x0b04:
            r1 = r46
            r7 = r26
            r12.showExtraNotifications(r13, r1, r7)     // Catch:{ Exception -> 0x0b0f }
            r45.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0b0f }
            goto L_0x0b14
        L_0x0b0f:
            r0 = move-exception
        L_0x0b10:
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0b14:
            return
        L_0x0b15:
            r45.dismissNotification()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0a92: MOVE  (r0v77 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>) = 
          (r49v1 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>)
        
        	at jadx.core.dex.instructions.args.InsnArg.wrapArg(InsnArg.java:164)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.assignInline(CodeShrinkVisitor.java:133)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.checkInline(CodeShrinkVisitor.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:65)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.regions.TernaryMod.makeTernaryInsn(TernaryMod.java:122)
        	at jadx.core.dex.visitors.regions.TernaryMod.visitRegion(TernaryMod.java:34)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:73)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterative(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.IfRegionVisitor.visit(IfRegionVisitor.java:31)
        */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x02c4  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x02e2  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02ed  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0340  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x039a  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x03ac  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03ed  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x04b0  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04b6  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x04cc A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x04e5  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x053b  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x056e  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0784  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x07a0  */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0896  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x08b6  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0912  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0945  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0966  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0988  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0a63  */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0a73  */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x0a79  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0a7d  */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x0a82  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0a9d  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x0b50  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0ba1 A[Catch:{ JSONException -> 0x0bf4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0bca A[Catch:{ JSONException -> 0x0bf4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0bd5  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0bdc A[Catch:{ JSONException -> 0x0bf4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01a9  */
    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r63, boolean r64, java.lang.String r65) {
        /*
            r62 = this;
            r1 = r62
            android.app.Notification r2 = r63.build()
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 18
            if (r0 >= r3) goto L_0x001d
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r3 = r1.notificationId
            r0.notify(r3, r2)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x001c
            java.lang.String r0 = "show summary notification by SDK check"
            org.telegram.messenger.FileLog.d(r0)
        L_0x001c:
            return
        L_0x001d:
            org.telegram.messenger.AccountInstance r0 = r62.getAccountInstance()
            android.content.SharedPreferences r0 = r0.getNotificationsSettings()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            android.util.LongSparseArray r4 = new android.util.LongSparseArray
            r4.<init>()
            r5 = 0
            r6 = 0
        L_0x0031:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r1.pushMessages
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x007e
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r1.pushMessages
            java.lang.Object r7 = r7.get(r6)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            long r8 = r7.getDialogId()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "dismissDate"
            r10.append(r11)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            int r10 = r0.getInt(r10, r5)
            org.telegram.tgnet.TLRPC$Message r11 = r7.messageOwner
            int r11 = r11.date
            if (r11 > r10) goto L_0x0061
            goto L_0x007b
        L_0x0061:
            java.lang.Object r10 = r4.get(r8)
            java.util.ArrayList r10 = (java.util.ArrayList) r10
            if (r10 != 0) goto L_0x0078
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            r4.put(r8, r10)
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            r3.add(r5, r8)
        L_0x0078:
            r10.add(r7)
        L_0x007b:
            int r6 = r6 + 1
            goto L_0x0031
        L_0x007e:
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.wearNotificationsIds
            android.util.LongSparseArray r6 = r0.clone()
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.wearNotificationsIds
            r0.clear()
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            boolean r0 = org.telegram.messenger.WearDataLayerListenerService.isWatchConnected()
            if (r0 == 0) goto L_0x009b
            org.json.JSONArray r0 = new org.json.JSONArray
            r0.<init>()
            r9 = r0
            goto L_0x009c
        L_0x009b:
            r9 = 0
        L_0x009c:
            int r0 = android.os.Build.VERSION.SDK_INT
            r10 = 27
            r11 = 1
            if (r0 <= r10) goto L_0x00ae
            if (r0 <= r10) goto L_0x00ac
            int r0 = r3.size()
            if (r0 <= r11) goto L_0x00ac
            goto L_0x00ae
        L_0x00ac:
            r12 = 0
            goto L_0x00af
        L_0x00ae:
            r12 = 1
        L_0x00af:
            r13 = 26
            if (r12 == 0) goto L_0x00ba
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r13) goto L_0x00ba
            checkOtherNotificationsChannel()
        L_0x00ba:
            org.telegram.messenger.UserConfig r0 = r62.getUserConfig()
            int r14 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x00cf
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x00cd
            goto L_0x00cf
        L_0x00cd:
            r15 = 0
            goto L_0x00d0
        L_0x00cf:
            r15 = 1
        L_0x00d0:
            int r13 = r3.size()
            r10 = 0
        L_0x00d5:
            if (r10 >= r13) goto L_0x0c0e
            java.lang.Object r0 = r3.get(r10)
            java.lang.Long r0 = (java.lang.Long) r0
            r17 = r12
            long r11 = r0.longValue()
            java.lang.Object r0 = r4.get(r11)
            r8 = r0
            java.util.ArrayList r8 = (java.util.ArrayList) r8
            java.lang.Object r0 = r8.get(r5)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r5 = r0.getId()
            r20 = r4
            int r4 = (int) r11
            r0 = 32
            r21 = r2
            r22 = r3
            long r2 = r11 >> r0
            int r3 = (int) r2
            java.lang.Object r0 = r6.get(r11)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0114
            if (r4 == 0) goto L_0x010f
            java.lang.Integer r0 = java.lang.Integer.valueOf(r4)
            goto L_0x0117
        L_0x010f:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r3)
            goto L_0x0117
        L_0x0114:
            r6.remove(r11)
        L_0x0117:
            r2 = r0
            if (r9 == 0) goto L_0x0122
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            r23 = r13
            goto L_0x0125
        L_0x0122:
            r23 = r13
            r0 = 0
        L_0x0125:
            r13 = 0
            java.lang.Object r24 = r8.get(r13)
            r13 = r24
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            r24 = r0
            org.telegram.tgnet.TLRPC$Message r0 = r13.messageOwner
            r25 = r6
            int r6 = r0.date
            r26 = r10
            android.util.LongSparseArray r10 = new android.util.LongSparseArray
            r10.<init>()
            r27 = 0
            if (r4 == 0) goto L_0x0243
            r0 = 777000(0xbdb28, float:1.088809E-39)
            if (r4 == r0) goto L_0x0148
            r0 = 1
            goto L_0x0149
        L_0x0148:
            r0 = 0
        L_0x0149:
            if (r4 <= 0) goto L_0x01c0
            r29 = r0
            org.telegram.messenger.MessagesController r0 = r62.getMessagesController()
            r30 = r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r9)
            if (r0 != 0) goto L_0x018b
            boolean r9 = r13.isFcmMessage()
            if (r9 == 0) goto L_0x016a
            java.lang.String r9 = r13.localName
        L_0x0165:
            r32 = r6
            r31 = r7
            goto L_0x01a6
        L_0x016a:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0182
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found user to show dialog notification "
            r0.append(r2)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x0182:
            r6 = r7
            r46 = r14
            r36 = r15
            r4 = r30
            goto L_0x027d
        L_0x018b:
            java.lang.String r9 = org.telegram.messenger.UserObject.getUserName(r0)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r13 = r0.photo
            if (r13 == 0) goto L_0x0165
            org.telegram.tgnet.TLRPC$FileLocation r13 = r13.photo_small
            if (r13 == 0) goto L_0x0165
            r32 = r6
            r31 = r7
            long r6 = r13.volume_id
            int r33 = (r6 > r27 ? 1 : (r6 == r27 ? 0 : -1))
            if (r33 == 0) goto L_0x01a6
            int r6 = r13.local_id
            if (r6 == 0) goto L_0x01a6
            goto L_0x01a7
        L_0x01a6:
            r13 = 0
        L_0x01a7:
            if (r4 != r14) goto L_0x01b2
            r6 = 2131625560(0x7f0e0658, float:1.8878331E38)
            java.lang.String r7 = "MessageScheduledReminderNotification"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r7, r6)
        L_0x01b2:
            r6 = r24
            r7 = 0
            r33 = 0
            r34 = 0
            r61 = r9
            r9 = r0
            r0 = r61
            goto L_0x02c2
        L_0x01c0:
            r29 = r0
            r32 = r6
            r31 = r7
            r30 = r9
            org.telegram.messenger.MessagesController r0 = r62.getMessagesController()
            int r6 = -r4
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r6)
            if (r0 != 0) goto L_0x020b
            boolean r6 = r13.isFcmMessage()
            if (r6 == 0) goto L_0x01f1
            boolean r6 = r13.isMegagroup()
            java.lang.String r9 = r13.localName
            boolean r7 = r13.localChannel
            r33 = r6
            r34 = r7
            r6 = r24
            r13 = 0
            r7 = r0
            r0 = r9
        L_0x01ee:
            r9 = 0
            goto L_0x02c2
        L_0x01f1:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0275
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found chat to show dialog notification "
            r0.append(r2)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x0275
        L_0x020b:
            boolean r6 = r0.megagroup
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r7 == 0) goto L_0x0219
            boolean r7 = r0.megagroup
            if (r7 != 0) goto L_0x0219
            r7 = 1
            goto L_0x021a
        L_0x0219:
            r7 = 0
        L_0x021a:
            java.lang.String r9 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r13 = r0.photo
            if (r13 == 0) goto L_0x0237
            org.telegram.tgnet.TLRPC$FileLocation r13 = r13.photo_small
            if (r13 == 0) goto L_0x0237
            r33 = r6
            r34 = r7
            long r6 = r13.volume_id
            int r35 = (r6 > r27 ? 1 : (r6 == r27 ? 0 : -1))
            if (r35 == 0) goto L_0x023b
            int r6 = r13.local_id
            if (r6 == 0) goto L_0x023b
            r7 = r0
            r0 = r9
            r6 = r24
            goto L_0x01ee
        L_0x0237:
            r33 = r6
            r34 = r7
        L_0x023b:
            r7 = r0
            r0 = r9
            r6 = r24
            r9 = 0
            r13 = 0
            goto L_0x02c2
        L_0x0243:
            r32 = r6
            r31 = r7
            r30 = r9
            long r6 = globalSecretChatId
            int r0 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x02ad
            org.telegram.messenger.MessagesController r0 = r62.getMessagesController()
            java.lang.Integer r6 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r6)
            if (r0 != 0) goto L_0x0282
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0275
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found secret chat to show dialog notification "
            r0.append(r2)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x0275:
            r46 = r14
            r36 = r15
            r4 = r30
            r6 = r31
        L_0x027d:
            r2 = 26
            r3 = 0
            goto L_0x0bf6
        L_0x0282:
            org.telegram.messenger.MessagesController r6 = r62.getMessagesController()
            int r7 = r0.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r6 != 0) goto L_0x02ae
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0275
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "not found secret chat user to show dialog notification "
            r2.append(r3)
            int r0 = r0.user_id
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x0275
        L_0x02ad:
            r6 = 0
        L_0x02ae:
            r0 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            java.lang.String r7 = "SecretChatName"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r7, r0)
            r0 = r9
            r7 = 0
            r13 = 0
            r29 = 0
            r33 = 0
            r34 = 0
            r9 = r6
            r6 = 0
        L_0x02c2:
            if (r15 == 0) goto L_0x02e2
            if (r4 >= 0) goto L_0x02d0
            r0 = 2131625747(0x7f0e0713, float:1.887871E38)
            java.lang.String r13 = "NotificationHiddenChatName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            goto L_0x02d9
        L_0x02d0:
            r0 = 2131625750(0x7f0e0716, float:1.8878717E38)
            java.lang.String r13 = "NotificationHiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
        L_0x02d9:
            r35 = r3
            r29 = r7
            r24 = r9
            r9 = 0
            r13 = 0
            goto L_0x02ea
        L_0x02e2:
            r35 = r3
            r24 = r9
            r9 = r29
            r29 = r7
        L_0x02ea:
            r7 = r0
            if (r13 == 0) goto L_0x0340
            r3 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r3)
            int r3 = android.os.Build.VERSION.SDK_INT
            r36 = r15
            r15 = 28
            if (r3 >= r15) goto L_0x033a
            org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r15 = "50_50"
            r37 = r6
            r6 = 0
            android.graphics.drawable.BitmapDrawable r3 = r3.getImageFromMemory(r13, r6, r15)
            if (r3 == 0) goto L_0x030f
            android.graphics.Bitmap r3 = r3.getBitmap()
        L_0x030d:
            r15 = r0
            goto L_0x0347
        L_0x030f:
            boolean r3 = r0.exists()     // Catch:{ all -> 0x033d }
            if (r3 == 0) goto L_0x0338
            r3 = 1126170624(0x43200000, float:160.0)
            r15 = 1112014848(0x42480000, float:50.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)     // Catch:{ all -> 0x033d }
            float r15 = (float) r15     // Catch:{ all -> 0x033d }
            float r3 = r3 / r15
            android.graphics.BitmapFactory$Options r15 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x033d }
            r15.<init>()     // Catch:{ all -> 0x033d }
            r18 = 1065353216(0x3var_, float:1.0)
            int r18 = (r3 > r18 ? 1 : (r3 == r18 ? 0 : -1))
            if (r18 >= 0) goto L_0x032c
            r3 = 1
            goto L_0x032d
        L_0x032c:
            int r3 = (int) r3     // Catch:{ all -> 0x033d }
        L_0x032d:
            r15.inSampleSize = r3     // Catch:{ all -> 0x033d }
            java.lang.String r3 = r0.getAbsolutePath()     // Catch:{ all -> 0x033d }
            android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeFile(r3, r15)     // Catch:{ all -> 0x033d }
            goto L_0x030d
        L_0x0338:
            r3 = r6
            goto L_0x030d
        L_0x033a:
            r37 = r6
            r6 = 0
        L_0x033d:
            r15 = r0
            r3 = r6
            goto L_0x0347
        L_0x0340:
            r37 = r6
            r36 = r15
            r6 = 0
            r3 = r6
            r15 = r3
        L_0x0347:
            java.lang.String r6 = "max_id"
            r38 = r15
            java.lang.String r15 = "currentAccount"
            if (r34 == 0) goto L_0x0351
            if (r33 == 0) goto L_0x03d9
        L_0x0351:
            if (r9 == 0) goto L_0x03d9
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x03d9
            if (r14 == r4) goto L_0x03d9
            android.content.Intent r0 = new android.content.Intent
            r39 = r13
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            r40 = r9
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r9 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r13, r9)
            java.lang.String r9 = "dialog_id"
            r0.putExtra(r9, r11)
            r0.putExtra(r6, r5)
            int r9 = r1.currentAccount
            r0.putExtra(r15, r9)
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r13 = r2.intValue()
            r41 = r3
            r3 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r9, r13, r0, r3)
            androidx.core.app.RemoteInput$Builder r3 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r9 = "extra_voice_reply"
            r3.<init>(r9)
            r9 = 2131626367(0x7f0e097f, float:1.8879968E38)
            java.lang.String r13 = "Reply"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r13, r9)
            r3.setLabel(r9)
            androidx.core.app.RemoteInput r3 = r3.build()
            if (r4 >= 0) goto L_0x03ac
            r13 = 1
            java.lang.Object[] r9 = new java.lang.Object[r13]
            r13 = 0
            r9[r13] = r7
            java.lang.String r13 = "ReplyToGroup"
            r42 = r2
            r2 = 2131626368(0x7f0e0980, float:1.887997E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r13, r2, r9)
            goto L_0x03bd
        L_0x03ac:
            r42 = r2
            r2 = 2131626369(0x7f0e0981, float:1.8879972E38)
            r9 = 1
            java.lang.Object[] r13 = new java.lang.Object[r9]
            r9 = 0
            r13[r9] = r7
            java.lang.String r9 = "ReplyToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r9, r2, r13)
        L_0x03bd:
            androidx.core.app.NotificationCompat$Action$Builder r9 = new androidx.core.app.NotificationCompat$Action$Builder
            r13 = 2131165479(0x7var_, float:1.7945176E38)
            r9.<init>(r13, r2, r0)
            r2 = 1
            r9.setAllowGeneratedReplies(r2)
            r9.setSemanticAction(r2)
            r9.addRemoteInput(r3)
            r2 = 0
            r9.setShowsUserInterface(r2)
            androidx.core.app.NotificationCompat$Action r0 = r9.build()
            r3 = r0
            goto L_0x03e3
        L_0x03d9:
            r42 = r2
            r41 = r3
            r40 = r9
            r39 = r13
            r2 = 0
            r3 = 0
        L_0x03e3:
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.pushDialogs
            java.lang.Object r0 = r0.get(r11)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x03f1
            java.lang.Integer r0 = java.lang.Integer.valueOf(r2)
        L_0x03f1:
            int r0 = r0.intValue()
            int r2 = r8.size()
            int r0 = java.lang.Math.max(r0, r2)
            r2 = 2
            r9 = 1
            if (r0 <= r9) goto L_0x041d
            int r13 = android.os.Build.VERSION.SDK_INT
            r9 = 28
            if (r13 < r9) goto L_0x0408
            goto L_0x041d
        L_0x0408:
            java.lang.Object[] r9 = new java.lang.Object[r2]
            r13 = 0
            r9[r13] = r7
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r13 = 1
            r9[r13] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r9)
            r9 = r0
            r13 = r3
            goto L_0x041f
        L_0x041d:
            r13 = r3
            r9 = r7
        L_0x041f:
            long r2 = (long) r14
            java.lang.Object r0 = r10.get(r2)
            r43 = r0
            androidx.core.app.Person r43 = (androidx.core.app.Person) r43
            int r0 = android.os.Build.VERSION.SDK_INT
            r44 = r5
            r5 = 28
            if (r0 < r5) goto L_0x04a7
            if (r43 != 0) goto L_0x04a7
            org.telegram.messenger.MessagesController r0 = r62.getMessagesController()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r5)
            if (r0 != 0) goto L_0x0448
            org.telegram.messenger.UserConfig r0 = r62.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x0448:
            if (r0 == 0) goto L_0x04a2
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r0.photo     // Catch:{ all -> 0x0499 }
            if (r5 == 0) goto L_0x04a2
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r0.photo     // Catch:{ all -> 0x0499 }
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ all -> 0x0499 }
            if (r5 == 0) goto L_0x04a2
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r0.photo     // Catch:{ all -> 0x0499 }
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ all -> 0x0499 }
            r45 = r6
            long r5 = r5.volume_id     // Catch:{ all -> 0x0497 }
            int r46 = (r5 > r27 ? 1 : (r5 == r27 ? 0 : -1))
            if (r46 == 0) goto L_0x04a4
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r0.photo     // Catch:{ all -> 0x0497 }
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ all -> 0x0497 }
            int r5 = r5.local_id     // Catch:{ all -> 0x0497 }
            if (r5 == 0) goto L_0x04a4
            androidx.core.app.Person$Builder r5 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x0497 }
            r5.<init>()     // Catch:{ all -> 0x0497 }
            java.lang.String r6 = "FromYou"
            r46 = r14
            r14 = 2131625253(0x7f0e0525, float:1.8877709E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r14)     // Catch:{ all -> 0x0495 }
            r5.setName(r6)     // Catch:{ all -> 0x0495 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x0495 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x0495 }
            r6 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r6)     // Catch:{ all -> 0x0495 }
            r1.loadRoundAvatar(r0, r5)     // Catch:{ all -> 0x0495 }
            androidx.core.app.Person r5 = r5.build()     // Catch:{ all -> 0x0495 }
            r10.put(r2, r5)     // Catch:{ all -> 0x0491 }
            r43 = r5
            goto L_0x04aa
        L_0x0491:
            r0 = move-exception
            r43 = r5
            goto L_0x049e
        L_0x0495:
            r0 = move-exception
            goto L_0x049e
        L_0x0497:
            r0 = move-exception
            goto L_0x049c
        L_0x0499:
            r0 = move-exception
            r45 = r6
        L_0x049c:
            r46 = r14
        L_0x049e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x04aa
        L_0x04a2:
            r45 = r6
        L_0x04a4:
            r46 = r14
            goto L_0x04aa
        L_0x04a7:
            r45 = r6
            goto L_0x04a4
        L_0x04aa:
            r0 = r43
            java.lang.String r5 = ""
            if (r0 == 0) goto L_0x04b6
            androidx.core.app.NotificationCompat$MessagingStyle r6 = new androidx.core.app.NotificationCompat$MessagingStyle
            r6.<init>((androidx.core.app.Person) r0)
            goto L_0x04bb
        L_0x04b6:
            androidx.core.app.NotificationCompat$MessagingStyle r6 = new androidx.core.app.NotificationCompat$MessagingStyle
            r6.<init>((java.lang.CharSequence) r5)
        L_0x04bb:
            int r0 = android.os.Build.VERSION.SDK_INT
            r14 = 28
            if (r0 < r14) goto L_0x04c5
            if (r4 >= 0) goto L_0x04c8
            if (r34 != 0) goto L_0x04c8
        L_0x04c5:
            r6.setConversationTitle(r9)
        L_0x04c8:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r14) goto L_0x04d3
            if (r34 != 0) goto L_0x04d1
            if (r4 >= 0) goto L_0x04d1
            goto L_0x04d3
        L_0x04d1:
            r0 = 0
            goto L_0x04d4
        L_0x04d3:
            r0 = 1
        L_0x04d4:
            r6.setGroupConversation(r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r9 = 1
            java.lang.String[] r14 = new java.lang.String[r9]
            r43 = r13
            boolean[] r13 = new boolean[r9]
            if (r37 == 0) goto L_0x04ef
            org.json.JSONArray r16 = new org.json.JSONArray
            r16.<init>()
            r47 = r15
            r15 = r16
            goto L_0x04f2
        L_0x04ef:
            r47 = r15
            r15 = 0
        L_0x04f2:
            int r16 = r8.size()
            int r48 = r16 + -1
            r9 = r48
            r49 = 0
            r50 = 0
        L_0x04fe:
            r51 = 1000(0x3e8, double:4.94E-321)
            if (r9 < 0) goto L_0x0849
            java.lang.Object r48 = r8.get(r9)
            r53 = r8
            r8 = r48
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            r48 = r9
            java.lang.String r9 = r1.getShortStringForMessage(r8, r14, r13)
            int r54 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
            if (r54 != 0) goto L_0x051d
            r19 = 0
            r14[r19] = r7
            r54 = r7
            goto L_0x0537
        L_0x051d:
            r19 = 0
            r54 = r7
            if (r4 >= 0) goto L_0x0537
            org.telegram.tgnet.TLRPC$Message r7 = r8.messageOwner
            boolean r7 = r7.from_scheduled
            if (r7 == 0) goto L_0x0537
            r7 = 2131625793(0x7f0e0741, float:1.8878804E38)
            r55 = r15
            java.lang.String r15 = "NotificationMessageScheduledName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r15, r7)
            r14[r19] = r7
            goto L_0x0539
        L_0x0537:
            r55 = r15
        L_0x0539:
            if (r9 != 0) goto L_0x056e
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r7 == 0) goto L_0x0563
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r9 = "message text is null for "
            r7.append(r9)
            int r9 = r8.getId()
            r7.append(r9)
            java.lang.String r9 = " did = "
            r7.append(r9)
            long r8 = r8.getDialogId()
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            org.telegram.messenger.FileLog.w(r7)
        L_0x0563:
            r56 = r2
            r60 = r10
            r58 = r11
            r15 = r13
            r3 = r55
            goto L_0x0839
        L_0x056e:
            int r7 = r0.length()
            if (r7 <= 0) goto L_0x0579
            java.lang.String r7 = "\n\n"
            r0.append(r7)
        L_0x0579:
            int r7 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x05a3
            org.telegram.tgnet.TLRPC$Message r7 = r8.messageOwner
            boolean r7 = r7.from_scheduled
            if (r7 == 0) goto L_0x05a3
            if (r4 <= 0) goto L_0x05a3
            r7 = 2
            java.lang.Object[] r15 = new java.lang.Object[r7]
            r7 = 2131625793(0x7f0e0741, float:1.8878804E38)
            r56 = r2
            java.lang.String r2 = "NotificationMessageScheduledName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r7)
            r3 = 0
            r15[r3] = r2
            r2 = 1
            r15[r2] = r9
            java.lang.String r2 = "%1$s: %2$s"
            java.lang.String r9 = java.lang.String.format(r2, r15)
            r0.append(r9)
            goto L_0x05c1
        L_0x05a3:
            r56 = r2
            r3 = 0
            r2 = r14[r3]
            if (r2 == 0) goto L_0x05be
            r2 = 2
            java.lang.Object[] r7 = new java.lang.Object[r2]
            r2 = r14[r3]
            r7[r3] = r2
            r2 = 1
            r7[r2] = r9
            java.lang.String r2 = "%1$s: %2$s"
            java.lang.String r2 = java.lang.String.format(r2, r7)
            r0.append(r2)
            goto L_0x05c1
        L_0x05be:
            r0.append(r9)
        L_0x05c1:
            if (r4 <= 0) goto L_0x05c5
            long r2 = (long) r4
            goto L_0x05d2
        L_0x05c5:
            if (r34 == 0) goto L_0x05ca
            int r2 = -r4
        L_0x05c8:
            long r2 = (long) r2
            goto L_0x05d2
        L_0x05ca:
            if (r4 >= 0) goto L_0x05d1
            int r2 = r8.getFromId()
            goto L_0x05c8
        L_0x05d1:
            r2 = r11
        L_0x05d2:
            java.lang.Object r7 = r10.get(r2)
            androidx.core.app.Person r7 = (androidx.core.app.Person) r7
            r15 = 0
            r58 = r14[r15]
            if (r58 != 0) goto L_0x061b
            if (r36 == 0) goto L_0x0615
            if (r4 >= 0) goto L_0x0603
            if (r34 == 0) goto L_0x05f5
            int r15 = android.os.Build.VERSION.SDK_INT
            r58 = r11
            r11 = 27
            if (r15 <= r11) goto L_0x0619
            r12 = 2131625747(0x7f0e0713, float:1.887871E38)
            java.lang.String r15 = "NotificationHiddenChatName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            goto L_0x0623
        L_0x05f5:
            r58 = r11
            r11 = 27
            r12 = 2131625748(0x7f0e0714, float:1.8878713E38)
            java.lang.String r15 = "NotificationHiddenChatUserName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            goto L_0x0623
        L_0x0603:
            r58 = r11
            r11 = 27
            int r12 = android.os.Build.VERSION.SDK_INT
            if (r12 <= r11) goto L_0x0619
            r12 = 2131625750(0x7f0e0716, float:1.8878717E38)
            java.lang.String r15 = "NotificationHiddenName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            goto L_0x0623
        L_0x0615:
            r58 = r11
            r11 = 27
        L_0x0619:
            r12 = r5
            goto L_0x0623
        L_0x061b:
            r58 = r11
            r11 = 27
            r12 = 0
            r15 = r14[r12]
            r12 = r15
        L_0x0623:
            if (r7 == 0) goto L_0x0633
            java.lang.CharSequence r15 = r7.getName()
            boolean r15 = android.text.TextUtils.equals(r15, r12)
            if (r15 != 0) goto L_0x0630
            goto L_0x0633
        L_0x0630:
            r15 = r13
            goto L_0x06a0
        L_0x0633:
            androidx.core.app.Person$Builder r7 = new androidx.core.app.Person$Builder
            r7.<init>()
            r7.setName(r12)
            r12 = 0
            boolean r15 = r13[r12]
            if (r15 == 0) goto L_0x0698
            if (r4 == 0) goto L_0x0698
            int r12 = android.os.Build.VERSION.SDK_INT
            r15 = 28
            if (r12 < r15) goto L_0x0698
            if (r4 > 0) goto L_0x0691
            if (r34 == 0) goto L_0x064d
            goto L_0x0691
        L_0x064d:
            if (r4 >= 0) goto L_0x068e
            int r12 = r8.getFromId()
            org.telegram.messenger.MessagesController r15 = r62.getMessagesController()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r11 = r15.getUser(r11)
            if (r11 != 0) goto L_0x0673
            org.telegram.messenger.MessagesStorage r11 = r62.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r11 = r11.getUserSync(r12)
            if (r11 == 0) goto L_0x0673
            org.telegram.messenger.MessagesController r12 = r62.getMessagesController()
            r15 = 1
            r12.putUser(r11, r15)
        L_0x0673:
            if (r11 == 0) goto L_0x068e
            org.telegram.tgnet.TLRPC$UserProfilePhoto r11 = r11.photo
            if (r11 == 0) goto L_0x068e
            org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small
            if (r11 == 0) goto L_0x068e
            r15 = r13
            long r12 = r11.volume_id
            int r60 = (r12 > r27 ? 1 : (r12 == r27 ? 0 : -1))
            if (r60 == 0) goto L_0x068f
            int r12 = r11.local_id
            if (r12 == 0) goto L_0x068f
            r12 = 1
            java.io.File r11 = org.telegram.messenger.FileLoader.getPathToAttach(r11, r12)
            goto L_0x0694
        L_0x068e:
            r15 = r13
        L_0x068f:
            r11 = 0
            goto L_0x0694
        L_0x0691:
            r15 = r13
            r11 = r38
        L_0x0694:
            r1.loadRoundAvatar(r11, r7)
            goto L_0x0699
        L_0x0698:
            r15 = r13
        L_0x0699:
            androidx.core.app.Person r7 = r7.build()
            r10.put(r2, r7)
        L_0x06a0:
            if (r4 == 0) goto L_0x07cf
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r2 < r3) goto L_0x077f
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r11 = "activity"
            java.lang.Object r2 = r2.getSystemService(r11)
            android.app.ActivityManager r2 = (android.app.ActivityManager) r2
            boolean r2 = r2.isLowRamDevice()
            if (r2 != 0) goto L_0x077f
            if (r36 != 0) goto L_0x077f
            boolean r2 = r8.isSecretMedia()
            if (r2 != 0) goto L_0x077f
            int r2 = r8.type
            r11 = 1
            if (r2 == r11) goto L_0x06cb
            boolean r2 = r8.isSticker()
            if (r2 == 0) goto L_0x077f
        L_0x06cb:
            org.telegram.tgnet.TLRPC$Message r2 = r8.messageOwner
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToMessage(r2)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r11 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r12 = r8.messageOwner
            int r12 = r12.date
            long r12 = (long) r12
            long r12 = r12 * r51
            r11.<init>(r9, r12, r7)
            boolean r12 = r8.isSticker()
            if (r12 == 0) goto L_0x06e6
            java.lang.String r12 = "image/webp"
            goto L_0x06e8
        L_0x06e6:
            java.lang.String r12 = "image/jpeg"
        L_0x06e8:
            boolean r13 = r2.exists()
            if (r13 == 0) goto L_0x06f9
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r3 = "org.telegram.messenger.beta.provider"
            android.net.Uri r2 = androidx.core.content.FileProvider.getUriForFile(r13, r3, r2)
            r60 = r10
            goto L_0x074f
        L_0x06f9:
            org.telegram.messenger.FileLoader r3 = r62.getFileLoader()
            java.lang.String r13 = r2.getName()
            boolean r3 = r3.isLoadingFile(r13)
            if (r3 == 0) goto L_0x074c
            android.net.Uri$Builder r3 = new android.net.Uri$Builder
            r3.<init>()
            java.lang.String r13 = "content"
            android.net.Uri$Builder r3 = r3.scheme(r13)
            java.lang.String r13 = "org.telegram.messenger.beta.notification_image_provider"
            android.net.Uri$Builder r3 = r3.authority(r13)
            java.lang.String r13 = "msg_media_raw"
            android.net.Uri$Builder r3 = r3.appendPath(r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r60 = r10
            int r10 = r1.currentAccount
            r13.append(r10)
            r13.append(r5)
            java.lang.String r10 = r13.toString()
            android.net.Uri$Builder r3 = r3.appendPath(r10)
            java.lang.String r10 = r2.getName()
            android.net.Uri$Builder r3 = r3.appendPath(r10)
            java.lang.String r2 = r2.getAbsolutePath()
            java.lang.String r10 = "final_path"
            android.net.Uri$Builder r2 = r3.appendQueryParameter(r10, r2)
            android.net.Uri r2 = r2.build()
            goto L_0x074f
        L_0x074c:
            r60 = r10
            r2 = 0
        L_0x074f:
            if (r2 == 0) goto L_0x0781
            r11.setData(r12, r2)
            r6.addMessage(r11)
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r10 = "com.android.systemui"
            r11 = 1
            r3.grantUriPermission(r10, r2, r11)
            org.telegram.messenger.-$$Lambda$NotificationsController$hROO1aIM4eduzMv5uJ3U4yL97Bo r3 = new org.telegram.messenger.-$$Lambda$NotificationsController$hROO1aIM4eduzMv5uJ3U4yL97Bo
            r3.<init>(r2)
            r10 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r10)
            java.lang.CharSequence r2 = r8.caption
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x077d
            java.lang.CharSequence r2 = r8.caption
            org.telegram.tgnet.TLRPC$Message r3 = r8.messageOwner
            int r3 = r3.date
            long r10 = (long) r3
            long r10 = r10 * r51
            r6.addMessage(r2, r10, r7)
        L_0x077d:
            r2 = 1
            goto L_0x0782
        L_0x077f:
            r60 = r10
        L_0x0781:
            r2 = 0
        L_0x0782:
            if (r2 != 0) goto L_0x078e
            org.telegram.tgnet.TLRPC$Message r2 = r8.messageOwner
            int r2 = r2.date
            long r2 = (long) r2
            long r2 = r2 * r51
            r6.addMessage(r9, r2, r7)
        L_0x078e:
            if (r36 != 0) goto L_0x07db
            boolean r2 = r8.isVoice()
            if (r2 == 0) goto L_0x07db
            java.util.List r2 = r6.getMessages()
            boolean r3 = r2.isEmpty()
            if (r3 != 0) goto L_0x07db
            org.telegram.tgnet.TLRPC$Message r3 = r8.messageOwner
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3)
            int r7 = android.os.Build.VERSION.SDK_INT
            r10 = 24
            if (r7 < r10) goto L_0x07b7
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x07b5 }
            java.lang.String r10 = "org.telegram.messenger.beta.provider"
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r7, r10, r3)     // Catch:{ Exception -> 0x07b5 }
            goto L_0x07bb
        L_0x07b5:
            r3 = 0
            goto L_0x07bb
        L_0x07b7:
            android.net.Uri r3 = android.net.Uri.fromFile(r3)
        L_0x07bb:
            if (r3 == 0) goto L_0x07db
            int r7 = r2.size()
            r10 = 1
            int r7 = r7 - r10
            java.lang.Object r2 = r2.get(r7)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r2 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r2
            java.lang.String r7 = "audio/ogg"
            r2.setData(r7, r3)
            goto L_0x07db
        L_0x07cf:
            r60 = r10
            org.telegram.tgnet.TLRPC$Message r2 = r8.messageOwner
            int r2 = r2.date
            long r2 = (long) r2
            long r2 = r2 * r51
            r6.addMessage(r9, r2, r7)
        L_0x07db:
            if (r55 == 0) goto L_0x0820
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0820 }
            r2.<init>()     // Catch:{ JSONException -> 0x0820 }
            java.lang.String r3 = "text"
            r2.put(r3, r9)     // Catch:{ JSONException -> 0x0820 }
            java.lang.String r3 = "date"
            org.telegram.tgnet.TLRPC$Message r7 = r8.messageOwner     // Catch:{ JSONException -> 0x0820 }
            int r7 = r7.date     // Catch:{ JSONException -> 0x0820 }
            r2.put(r3, r7)     // Catch:{ JSONException -> 0x0820 }
            boolean r3 = r8.isFromUser()     // Catch:{ JSONException -> 0x0820 }
            if (r3 == 0) goto L_0x0818
            if (r4 >= 0) goto L_0x0818
            org.telegram.messenger.MessagesController r3 = r62.getMessagesController()     // Catch:{ JSONException -> 0x0820 }
            int r7 = r8.getFromId()     // Catch:{ JSONException -> 0x0820 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ JSONException -> 0x0820 }
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r7)     // Catch:{ JSONException -> 0x0820 }
            if (r3 == 0) goto L_0x0818
            java.lang.String r7 = "fname"
            java.lang.String r9 = r3.first_name     // Catch:{ JSONException -> 0x0820 }
            r2.put(r7, r9)     // Catch:{ JSONException -> 0x0820 }
            java.lang.String r7 = "lname"
            java.lang.String r3 = r3.last_name     // Catch:{ JSONException -> 0x0820 }
            r2.put(r7, r3)     // Catch:{ JSONException -> 0x0820 }
        L_0x0818:
            r3 = r55
            r3.put(r2)     // Catch:{ JSONException -> 0x081e }
            goto L_0x0822
        L_0x081e:
            goto L_0x0822
        L_0x0820:
            r3 = r55
        L_0x0822:
            r9 = 777000(0xbdb28, double:3.83889E-318)
            int r2 = (r58 > r9 ? 1 : (r58 == r9 ? 0 : -1))
            if (r2 != 0) goto L_0x0839
            org.telegram.tgnet.TLRPC$Message r2 = r8.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup
            if (r2 == 0) goto L_0x0839
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r2 = r2.rows
            int r7 = r8.getId()
            r49 = r2
            r50 = r7
        L_0x0839:
            int r9 = r48 + -1
            r13 = r15
            r8 = r53
            r7 = r54
            r11 = r58
            r10 = r60
            r15 = r3
            r2 = r56
            goto L_0x04fe
        L_0x0849:
            r54 = r7
            r53 = r8
            r58 = r11
            r3 = r15
            android.content.Intent r2 = new android.content.Intent
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.ui.LaunchActivity> r8 = org.telegram.ui.LaunchActivity.class
            r2.<init>(r7, r8)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "com.tmessages.openchat"
            r7.append(r8)
            double r8 = java.lang.Math.random()
            r7.append(r8)
            r8 = 2147483647(0x7fffffff, float:NaN)
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r2.setAction(r7)
            r7 = 32768(0x8000, float:4.5918E-41)
            r2.setFlags(r7)
            java.lang.String r7 = "android.intent.category.LAUNCHER"
            r2.addCategory(r7)
            if (r4 == 0) goto L_0x0896
            if (r4 <= 0) goto L_0x088d
            java.lang.String r7 = "userId"
            r2.putExtra(r7, r4)
            goto L_0x0893
        L_0x088d:
            int r7 = -r4
            java.lang.String r8 = "chatId"
            r2.putExtra(r8, r7)
        L_0x0893:
            r8 = r35
            goto L_0x089d
        L_0x0896:
            java.lang.String r7 = "encId"
            r8 = r35
            r2.putExtra(r7, r8)
        L_0x089d:
            int r7 = r1.currentAccount
            r9 = r47
            r2.putExtra(r9, r7)
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            r10 = 1073741824(0x40000000, float:2.0)
            r11 = 0
            android.app.PendingIntent r2 = android.app.PendingIntent.getActivity(r7, r11, r2, r10)
            androidx.core.app.NotificationCompat$WearableExtender r7 = new androidx.core.app.NotificationCompat$WearableExtender
            r7.<init>()
            r10 = r43
            if (r43 == 0) goto L_0x08b9
            r7.addAction(r10)
        L_0x08b9:
            android.content.Intent r11 = new android.content.Intent
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r13 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r11.<init>(r12, r13)
            r12 = 32
            r11.addFlags(r12)
            java.lang.String r12 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r11.setAction(r12)
            java.lang.String r12 = "dialog_id"
            r13 = r58
            r11.putExtra(r12, r13)
            r12 = r44
            r15 = r45
            r11.putExtra(r15, r12)
            r55 = r3
            int r3 = r1.currentAccount
            r11.putExtra(r9, r3)
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r15 = r42.intValue()
            r43 = r10
            r10 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r3 = android.app.PendingIntent.getBroadcast(r3, r15, r11, r10)
            androidx.core.app.NotificationCompat$Action$Builder r10 = new androidx.core.app.NotificationCompat$Action$Builder
            r11 = 2131165612(0x7var_ac, float:1.7945446E38)
            r15 = 2131625498(0x7f0e061a, float:1.8878206E38)
            r47 = r9
            java.lang.String r9 = "MarkAsRead"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r15)
            r10.<init>(r11, r9, r3)
            r3 = 2
            r10.setSemanticAction(r3)
            r3 = 0
            r10.setShowsUserInterface(r3)
            androidx.core.app.NotificationCompat$Action r3 = r10.build()
            java.lang.String r9 = "_"
            if (r4 == 0) goto L_0x0945
            if (r4 <= 0) goto L_0x092c
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r10 = "tguser"
            r8.append(r10)
            r8.append(r4)
            r8.append(r9)
            r8.append(r12)
            java.lang.String r8 = r8.toString()
            goto L_0x0964
        L_0x092c:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r10 = "tgchat"
            r8.append(r10)
            int r10 = -r4
            r8.append(r10)
            r8.append(r9)
            r8.append(r12)
            java.lang.String r8 = r8.toString()
            goto L_0x0964
        L_0x0945:
            long r10 = globalSecretChatId
            int r15 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r15 == 0) goto L_0x0963
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "tgenc"
            r10.append(r11)
            r10.append(r8)
            r10.append(r9)
            r10.append(r12)
            java.lang.String r8 = r10.toString()
            goto L_0x0964
        L_0x0963:
            r8 = 0
        L_0x0964:
            if (r8 == 0) goto L_0x0988
            r7.setDismissalId(r8)
            androidx.core.app.NotificationCompat$WearableExtender r10 = new androidx.core.app.NotificationCompat$WearableExtender
            r10.<init>()
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r15 = "summary_"
            r11.append(r15)
            r11.append(r8)
            java.lang.String r8 = r11.toString()
            r10.setDismissalId(r8)
            r8 = r63
            r8.extend(r10)
            goto L_0x098a
        L_0x0988:
            r8 = r63
        L_0x098a:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "tgaccount"
            r10.append(r11)
            r11 = r46
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r7.setBridgeTag(r10)
            r10 = r53
            r15 = 0
            java.lang.Object r27 = r10.get(r15)
            r15 = r27
            org.telegram.messenger.MessageObject r15 = (org.telegram.messenger.MessageObject) r15
            org.telegram.tgnet.TLRPC$Message r15 = r15.messageOwner
            int r15 = r15.date
            r27 = r9
            long r8 = (long) r15
            long r8 = r8 * r51
            androidx.core.app.NotificationCompat$Builder r15 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
            r15.<init>(r11)
            r11 = r54
            r15.setContentTitle(r11)
            r44 = r12
            r12 = 2131165724(0x7var_c, float:1.7945673E38)
            r15.setSmallIcon(r12)
            java.lang.String r0 = r0.toString()
            r15.setContentText(r0)
            r12 = 1
            r15.setAutoCancel(r12)
            int r0 = r10.size()
            r15.setNumber(r0)
            r0 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r15.setColor(r0)
            r10 = 0
            r15.setGroupSummary(r10)
            r15.setWhen(r8)
            r15.setShowWhen(r12)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r10 = "sdid_"
            r0.append(r10)
            r0.append(r13)
            java.lang.String r0 = r0.toString()
            r15.setShortcutId(r0)
            r15.setStyle(r6)
            r15.setContentIntent(r2)
            r15.extend(r7)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r5)
            r5 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r5 = r5 - r8
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            r15.setSortKey(r0)
            java.lang.String r0 = "msg"
            r15.setCategory(r0)
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r5 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r2, r5)
            java.lang.String r2 = "messageDate"
            r5 = r32
            r0.putExtra(r2, r5)
            java.lang.String r2 = "dialogId"
            r0.putExtra(r2, r13)
            int r2 = r1.currentAccount
            r6 = r47
            r0.putExtra(r6, r2)
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r7 = r42.intValue()
            r8 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r2, r7, r0, r8)
            r15.setDeleteIntent(r0)
            if (r17 == 0) goto L_0x0a5a
            java.lang.String r0 = r1.notificationGroup
            r15.setGroup(r0)
            r2 = 1
            r15.setGroupAlertBehavior(r2)
        L_0x0a5a:
            if (r43 == 0) goto L_0x0a61
            r2 = r43
            r15.addAction(r2)
        L_0x0a61:
            if (r36 != 0) goto L_0x0a66
            r15.addAction(r3)
        L_0x0a66:
            int r0 = r22.size()
            r2 = 1
            if (r0 != r2) goto L_0x0a79
            boolean r0 = android.text.TextUtils.isEmpty(r65)
            if (r0 != 0) goto L_0x0a79
            r3 = r65
            r15.setSubText(r3)
            goto L_0x0a7b
        L_0x0a79:
            r3 = r65
        L_0x0a7b:
            if (r4 != 0) goto L_0x0a80
            r15.setLocalOnly(r2)
        L_0x0a80:
            if (r41 == 0) goto L_0x0a87
            r7 = r41
            r15.setLargeIcon(r7)
        L_0x0a87:
            r7 = 0
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r7)
            if (r0 != 0) goto L_0x0b23
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0b23
            r0 = r49
            if (r0 == 0) goto L_0x0b23
            int r7 = r0.size()
            r8 = 0
        L_0x0a9b:
            if (r8 >= r7) goto L_0x0b23
            java.lang.Object r9 = r0.get(r8)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r9 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r10 = r9.buttons
            int r10 = r10.size()
            r12 = 0
        L_0x0aaa:
            if (r12 >= r10) goto L_0x0b0f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r2 = r9.buttons
            java.lang.Object r2 = r2.get(r12)
            org.telegram.tgnet.TLRPC$KeyboardButton r2 = (org.telegram.tgnet.TLRPC.KeyboardButton) r2
            r28 = r0
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback
            if (r0 == 0) goto L_0x0af8
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            r32 = r7
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r7 = org.telegram.messenger.NotificationCallbackReceiver.class
            r0.<init>(r3, r7)
            int r3 = r1.currentAccount
            r0.putExtra(r6, r3)
            java.lang.String r3 = "did"
            r0.putExtra(r3, r13)
            byte[] r3 = r2.data
            if (r3 == 0) goto L_0x0ad8
            java.lang.String r7 = "data"
            r0.putExtra(r7, r3)
        L_0x0ad8:
            java.lang.String r3 = "mid"
            r7 = r50
            r0.putExtra(r3, r7)
            java.lang.String r2 = r2.text
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            r47 = r6
            int r6 = r1.lastButtonId
            r35 = r7
            int r7 = r6 + 1
            r1.lastButtonId = r7
            r7 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r3, r6, r0, r7)
            r3 = 0
            r15.addAction(r3, r2, r0)
            goto L_0x0b01
        L_0x0af8:
            r47 = r6
            r32 = r7
            r35 = r50
            r3 = 0
            r7 = 134217728(0x8000000, float:3.85186E-34)
        L_0x0b01:
            int r12 = r12 + 1
            r3 = r65
            r0 = r28
            r7 = r32
            r50 = r35
            r6 = r47
            r2 = 1
            goto L_0x0aaa
        L_0x0b0f:
            r28 = r0
            r47 = r6
            r32 = r7
            r35 = r50
            r3 = 0
            r7 = 134217728(0x8000000, float:3.85186E-34)
            int r8 = r8 + 1
            r3 = r65
            r7 = r32
            r2 = 1
            goto L_0x0a9b
        L_0x0b23:
            r3 = 0
            if (r29 != 0) goto L_0x0b4a
            if (r24 == 0) goto L_0x0b4a
            r6 = r24
            java.lang.String r0 = r6.phone
            if (r0 == 0) goto L_0x0b4a
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0b4a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "tel:+"
            r0.append(r2)
            java.lang.String r2 = r6.phone
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            r15.addPerson(r0)
        L_0x0b4a:
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 26
            if (r0 < r2) goto L_0x0b5f
            if (r17 == 0) goto L_0x0b58
            java.lang.String r0 = OTHER_NOTIFICATIONS_CHANNEL
            r15.setChannelId(r0)
            goto L_0x0b5f
        L_0x0b58:
            java.lang.String r0 = r21.getChannelId()
            r15.setChannelId(r0)
        L_0x0b5f:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            int r6 = r42.intValue()
            android.app.Notification r7 = r15.build()
            r0.<init>(r6, r7)
            r6 = r31
            r6.add(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.wearNotificationsIds
            r7 = r42
            r0.put(r13, r7)
            if (r4 == 0) goto L_0x0bf4
            if (r37 == 0) goto L_0x0bf4
            java.lang.String r0 = "reply"
            r8 = r37
            r7 = r40
            r8.put(r0, r7)     // Catch:{ JSONException -> 0x0bf4 }
            java.lang.String r0 = "name"
            r8.put(r0, r11)     // Catch:{ JSONException -> 0x0bf4 }
            r7 = r44
            r9 = r45
            r8.put(r9, r7)     // Catch:{ JSONException -> 0x0bf4 }
            java.lang.String r0 = "max_date"
            r8.put(r0, r5)     // Catch:{ JSONException -> 0x0bf4 }
            java.lang.String r0 = "id"
            int r5 = java.lang.Math.abs(r4)     // Catch:{ JSONException -> 0x0bf4 }
            r8.put(r0, r5)     // Catch:{ JSONException -> 0x0bf4 }
            if (r39 == 0) goto L_0x0bc8
            java.lang.String r0 = "photo"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0bf4 }
            r5.<init>()     // Catch:{ JSONException -> 0x0bf4 }
            r13 = r39
            int r7 = r13.dc_id     // Catch:{ JSONException -> 0x0bf4 }
            r5.append(r7)     // Catch:{ JSONException -> 0x0bf4 }
            r7 = r27
            r5.append(r7)     // Catch:{ JSONException -> 0x0bf4 }
            long r9 = r13.volume_id     // Catch:{ JSONException -> 0x0bf4 }
            r5.append(r9)     // Catch:{ JSONException -> 0x0bf4 }
            r5.append(r7)     // Catch:{ JSONException -> 0x0bf4 }
            long r9 = r13.secret     // Catch:{ JSONException -> 0x0bf4 }
            r5.append(r9)     // Catch:{ JSONException -> 0x0bf4 }
            java.lang.String r5 = r5.toString()     // Catch:{ JSONException -> 0x0bf4 }
            r8.put(r0, r5)     // Catch:{ JSONException -> 0x0bf4 }
        L_0x0bc8:
            if (r55 == 0) goto L_0x0bd1
            java.lang.String r0 = "msgs"
            r5 = r55
            r8.put(r0, r5)     // Catch:{ JSONException -> 0x0bf4 }
        L_0x0bd1:
            java.lang.String r0 = "type"
            if (r4 <= 0) goto L_0x0bdc
            java.lang.String r4 = "user"
            r8.put(r0, r4)     // Catch:{ JSONException -> 0x0bf4 }
            goto L_0x0bee
        L_0x0bdc:
            if (r4 >= 0) goto L_0x0bee
            if (r34 != 0) goto L_0x0be9
            if (r33 == 0) goto L_0x0be3
            goto L_0x0be9
        L_0x0be3:
            java.lang.String r4 = "group"
            r8.put(r0, r4)     // Catch:{ JSONException -> 0x0bf4 }
            goto L_0x0bee
        L_0x0be9:
            java.lang.String r4 = "channel"
            r8.put(r0, r4)     // Catch:{ JSONException -> 0x0bf4 }
        L_0x0bee:
            r4 = r30
            r4.put(r8)     // Catch:{ JSONException -> 0x0bf6 }
            goto L_0x0bf6
        L_0x0bf4:
            r4 = r30
        L_0x0bf6:
            int r10 = r26 + 1
            r9 = r4
            r7 = r6
            r12 = r17
            r4 = r20
            r2 = r21
            r3 = r22
            r13 = r23
            r6 = r25
            r15 = r36
            r14 = r46
            r5 = 0
            r11 = 1
            goto L_0x00d5
        L_0x0c0e:
            r21 = r2
            r25 = r6
            r6 = r7
            r4 = r9
            r17 = r12
            r46 = r14
            r3 = 0
            if (r17 == 0) goto L_0x0c3f
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "show summary with id "
            r0.append(r2)
            int r2 = r1.notificationId
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0CLASSNAME:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r2 = r1.notificationId
            r5 = r21
            r0.notify(r2, r5)
            goto L_0x0CLASSNAME
        L_0x0c3f:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r2 = r1.notificationId
            r0.cancel(r2)
        L_0x0CLASSNAME:
            int r0 = r6.size()
            r2 = 0
        L_0x0c4b:
            if (r2 >= r0) goto L_0x0CLASSNAME
            java.lang.Object r5 = r6.get(r2)
            org.telegram.messenger.NotificationsController$1NotificationHolder r5 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r5
            r5.call()
            int r2 = r2 + 1
            goto L_0x0c4b
        L_0x0CLASSNAME:
            int r0 = r25.size()
            if (r3 >= r0) goto L_0x0c8d
            r2 = r25
            java.lang.Object r0 = r2.valueAt(r3)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r5 == 0) goto L_0x0c7f
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "cancel notification id "
            r5.append(r6)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            org.telegram.messenger.FileLog.w(r5)
        L_0x0c7f:
            androidx.core.app.NotificationManagerCompat r5 = notificationManager
            int r0 = r0.intValue()
            r5.cancel(r0)
            int r3 = r3 + 1
            r25 = r2
            goto L_0x0CLASSNAME
        L_0x0c8d:
            if (r4 == 0) goto L_0x0caf
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0caf }
            r0.<init>()     // Catch:{ Exception -> 0x0caf }
            java.lang.String r2 = "id"
            r3 = r46
            r0.put(r2, r3)     // Catch:{ Exception -> 0x0caf }
            java.lang.String r2 = "n"
            r0.put(r2, r4)     // Catch:{ Exception -> 0x0caf }
            java.lang.String r2 = "/notify"
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0caf }
            byte[] r0 = r0.getBytes()     // Catch:{ Exception -> 0x0caf }
            java.lang.String r3 = "remote_notifications"
            org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r2, r0, r3)     // Catch:{ Exception -> 0x0caf }
        L_0x0caf:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, boolean, java.lang.String):void");
    }

    @TargetApi(28)
    private void loadRoundAvatar(File file, Person.Builder builder) {
        if (file != null) {
            try {
                builder.setIcon(IconCompat.createWithBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(file), $$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8.INSTANCE)));
            } catch (Throwable unused) {
            }
        }
    }

    static /* synthetic */ int lambda$null$32(Canvas canvas) {
        Path path = new Path();
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        int width = canvas.getWidth();
        float f = (float) (width / 2);
        path.addRoundRect(0.0f, 0.0f, (float) width, (float) canvas.getHeight(), f, f, Path.Direction.CW);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
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
                FileLog.e((Throwable) e);
            }
            notificationsQueue.postRunnable(new Runnable() {
                public final void run() {
                    NotificationsController.this.lambda$playOutChatSound$35$NotificationsController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$playOutChatSound$35$NotificationsController() {
        try {
            if (Math.abs(System.currentTimeMillis() - this.lastSoundOutPlay) > 100) {
                this.lastSoundOutPlay = System.currentTimeMillis();
                if (this.soundPool == null) {
                    this.soundPool = new SoundPool(3, 1, 0);
                    this.soundPool.setOnLoadCompleteListener($$Lambda$NotificationsController$wVHQwnWTTlh7lF1NZGGoEEMMuyY.INSTANCE);
                }
                if (this.soundOut == 0 && !this.soundOutLoaded) {
                    this.soundOutLoaded = true;
                    this.soundOut = this.soundPool.load(ApplicationLoader.applicationContext, NUM, 1);
                }
                if (this.soundOut != 0) {
                    try {
                        this.soundPool.play(this.soundOut, 1.0f, 1.0f, 1, 0, 1.0f);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    static /* synthetic */ void lambda$null$34(SoundPool soundPool2, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool2.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void setDialogNotificationsSettings(long j, int i) {
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        TLRPC.Dialog dialog = MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(j);
        if (i == 4) {
            if (isGlobalNotificationsEnabled(j)) {
                edit.remove("notify2_" + j);
            } else {
                edit.putInt("notify2_" + j, 0);
            }
            getMessagesStorage().setDialogFlags(j, 0);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
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
                edit.putInt("notify2_" + j, 2);
            } else {
                edit.putInt("notify2_" + j, 3);
                edit.putInt("notifyuntil_" + j, currentTime);
                j2 = 1 | (((long) currentTime) << 32);
            }
            getInstance(UserConfig.selectedAccount).removeNotificationsForDialog(j);
            MessagesStorage.getInstance(UserConfig.selectedAccount).setDialogFlags(j, j2);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
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
            TLRPC.TL_account_updateNotifySettings tL_account_updateNotifySettings = new TLRPC.TL_account_updateNotifySettings();
            tL_account_updateNotifySettings.settings = new TLRPC.TL_inputPeerNotifySettings();
            TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings = tL_account_updateNotifySettings.settings;
            tL_inputPeerNotifySettings.flags |= 1;
            tL_inputPeerNotifySettings.show_previews = notificationsSettings.getBoolean("content_preview_" + j, true);
            TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings2 = tL_account_updateNotifySettings.settings;
            tL_inputPeerNotifySettings2.flags = tL_inputPeerNotifySettings2.flags | 2;
            tL_inputPeerNotifySettings2.silent = notificationsSettings.getBoolean("silent_" + j, false);
            int i3 = notificationsSettings.getInt("notify2_" + j, -1);
            if (i3 != -1) {
                TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings3 = tL_account_updateNotifySettings.settings;
                tL_inputPeerNotifySettings3.flags |= 4;
                if (i3 == 3) {
                    tL_inputPeerNotifySettings3.mute_until = notificationsSettings.getInt("notifyuntil_" + j, 0);
                } else {
                    if (i3 == 2) {
                        i = Integer.MAX_VALUE;
                    }
                    tL_inputPeerNotifySettings3.mute_until = i;
                }
            }
            tL_account_updateNotifySettings.peer = new TLRPC.TL_inputNotifyPeer();
            ((TLRPC.TL_inputNotifyPeer) tL_account_updateNotifySettings.peer).peer = getMessagesController().getInputPeer(i2);
            getConnectionsManager().sendRequest(tL_account_updateNotifySettings, $$Lambda$NotificationsController$KyQqllEdy_fdmMCr6frsin2S3Cs.INSTANCE);
        }
    }

    public void updateServerNotificationsSettings(int i) {
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        TLRPC.TL_account_updateNotifySettings tL_account_updateNotifySettings = new TLRPC.TL_account_updateNotifySettings();
        tL_account_updateNotifySettings.settings = new TLRPC.TL_inputPeerNotifySettings();
        tL_account_updateNotifySettings.settings.flags = 5;
        if (i == 0) {
            tL_account_updateNotifySettings.peer = new TLRPC.TL_inputNotifyChats();
            tL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableGroup2", 0);
            tL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewGroup", true);
        } else if (i == 1) {
            tL_account_updateNotifySettings.peer = new TLRPC.TL_inputNotifyUsers();
            tL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableAll2", 0);
            tL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewAll", true);
        } else {
            tL_account_updateNotifySettings.peer = new TLRPC.TL_inputNotifyBroadcasts();
            tL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableChannel2", 0);
            tL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewChannel", true);
        }
        getConnectionsManager().sendRequest(tL_account_updateNotifySettings, $$Lambda$NotificationsController$WV8JpQrNXdfWVJfPV9wKTUTuLBk.INSTANCE);
    }

    public boolean isGlobalNotificationsEnabled(long j) {
        int i;
        int i2 = (int) j;
        if (i2 < 0) {
            TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(-i2));
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
