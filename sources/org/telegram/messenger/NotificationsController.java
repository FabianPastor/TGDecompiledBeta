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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputNotifyBroadcasts;
import org.telegram.tgnet.TLRPC$TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC$TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC$TL_inputNotifyUsers;
import org.telegram.tgnet.TLRPC$TL_inputPeerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$User;
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

    static /* synthetic */ void lambda$updateServerNotificationsSettings$36(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$37(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
            PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "telegram:notification_delay_lock");
            this.notificationDelayWakelock = newWakeLock;
            newWakeLock.setReferenceCounted(false);
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
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
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
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
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
                        int intValue2 = this.total_unread_count - num2.intValue();
                        this.total_unread_count = intValue2;
                        this.total_unread_count = intValue2 + num.intValue();
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
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > getConnectionsManager().getCurrentTime());
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
                int intValue = this.total_unread_count - num2.intValue();
                this.total_unread_count = intValue;
                this.total_unread_count = intValue + num3.intValue();
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

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0105  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processNewMessages$16$NotificationsController(java.util.ArrayList r32, java.util.ArrayList r33, boolean r34, boolean r35, java.util.concurrent.CountDownLatch r36) {
        /*
            r31 = this;
            r8 = r31
            r9 = r32
            android.util.LongSparseArray r10 = new android.util.LongSparseArray
            r10.<init>()
            org.telegram.messenger.AccountInstance r0 = r31.getAccountInstance()
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
            int r1 = r32.size()
            if (r15 >= r1) goto L_0x01fc
            java.lang.Object r1 = r9.get(r15)
            r7 = r1
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            if (r1 == 0) goto L_0x0045
            boolean r4 = r1.silent
            if (r4 == 0) goto L_0x0045
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r4 != 0) goto L_0x003f
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r1 == 0) goto L_0x0045
        L_0x003f:
            r22 = r13
            r21 = r15
            goto L_0x0131
        L_0x0045:
            int r1 = r7.getId()
            long r4 = (long) r1
            boolean r1 = r7.isFcmMessage()
            r19 = 0
            if (r1 == 0) goto L_0x005b
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            r21 = r15
            long r14 = r1.random_id
            r22 = r13
            goto L_0x0061
        L_0x005b:
            r21 = r15
            r22 = r13
            r14 = r19
        L_0x0061:
            long r12 = r7.getDialogId()
            int r6 = (int) r12
            boolean r1 = r7.isFcmMessage()
            if (r1 == 0) goto L_0x0071
            boolean r1 = r7.localChannel
        L_0x006e:
            r24 = r1
            goto L_0x0090
        L_0x0071:
            if (r6 >= 0) goto L_0x008e
            org.telegram.messenger.MessagesController r1 = r31.getMessagesController()
            int r2 = -r6
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x008c
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x008c
            r1 = 1
            goto L_0x006e
        L_0x008c:
            r1 = 0
            goto L_0x006e
        L_0x008e:
            r24 = 0
        L_0x0090:
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x009e
            long r1 = (long) r1
            r25 = 32
            long r1 = r1 << r25
            long r4 = r4 | r1
        L_0x009e:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.pushMessagesDict
            java.lang.Object r1 = r1.get(r4)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 != 0) goto L_0x00c6
            org.telegram.tgnet.TLRPC$Message r2 = r7.messageOwner
            r26 = r4
            long r3 = r2.random_id
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 == 0) goto L_0x00c8
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.fcmRandomMessagesDict
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 == 0) goto L_0x00c8
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r8.fcmRandomMessagesDict
            org.telegram.tgnet.TLRPC$Message r3 = r7.messageOwner
            long r3 = r3.random_id
            r2.remove(r3)
            goto L_0x00c8
        L_0x00c6:
            r26 = r4
        L_0x00c8:
            if (r1 == 0) goto L_0x0105
            boolean r2 = r1.isFcmMessage()
            if (r2 == 0) goto L_0x0131
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r8.pushMessagesDict
            r4 = r26
            r2.put(r4, r7)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r8.pushMessages
            int r1 = r2.indexOf(r1)
            if (r1 < 0) goto L_0x00f4
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.pushMessages
            r0.set(r1, r7)
            r0 = r31
            r1 = r33
            r2 = r7
            r3 = r6
            r4 = r12
            r6 = r24
            r12 = r7
            r7 = r11
            int r0 = r0.addToPopupMessages(r1, r2, r3, r4, r6, r7)
            goto L_0x00f5
        L_0x00f4:
            r12 = r7
        L_0x00f5:
            if (r34 == 0) goto L_0x0131
            boolean r1 = r12.localEdit
            if (r1 == 0) goto L_0x0102
            org.telegram.messenger.MessagesStorage r2 = r31.getMessagesStorage()
            r2.putPushMessage(r12)
        L_0x0102:
            r17 = r1
            goto L_0x0131
        L_0x0105:
            r4 = r26
            if (r17 == 0) goto L_0x010a
            goto L_0x0131
        L_0x010a:
            if (r34 == 0) goto L_0x0113
            org.telegram.messenger.MessagesStorage r1 = r31.getMessagesStorage()
            r1.putPushMessage(r7)
        L_0x0113:
            long r1 = r8.opened_dialog_id
            int r3 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x0123
            boolean r1 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r1 == 0) goto L_0x0123
            if (r34 != 0) goto L_0x0131
            r31.playInChatSound()
            goto L_0x0131
        L_0x0123:
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            boolean r2 = r1.mentioned
            if (r2 == 0) goto L_0x013c
            if (r22 != 0) goto L_0x0135
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r1 == 0) goto L_0x0135
        L_0x0131:
            r30 = r10
            goto L_0x01f1
        L_0x0135:
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            int r1 = r1.from_id
            long r1 = (long) r1
            r2 = r1
            goto L_0x013d
        L_0x013c:
            r2 = r12
        L_0x013d:
            boolean r1 = r8.isPersonalMessage(r7)
            if (r1 == 0) goto L_0x014b
            int r1 = r8.personal_count
            r16 = 1
            int r1 = r1 + 1
            r8.personal_count = r1
        L_0x014b:
            int r1 = r10.indexOfKey(r2)
            if (r1 < 0) goto L_0x015e
            java.lang.Object r1 = r10.valueAt(r1)
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r26 = r4
            goto L_0x017d
        L_0x015e:
            int r1 = r8.getNotifyOverride(r11, r2)
            r26 = r4
            r4 = -1
            if (r1 != r4) goto L_0x0170
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r24)
            boolean r1 = r8.isGlobalNotificationsEnabled(r2, r1)
            goto L_0x0176
        L_0x0170:
            r4 = 2
            if (r1 == r4) goto L_0x0175
            r1 = 1
            goto L_0x0176
        L_0x0175:
            r1 = 0
        L_0x0176:
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r1)
            r10.put(r2, r4)
        L_0x017d:
            if (r1 == 0) goto L_0x01ed
            if (r34 != 0) goto L_0x019a
            r0 = r31
            r1 = r33
            r28 = r2
            r2 = r7
            r3 = r6
            r30 = r10
            r9 = r26
            r4 = r28
            r6 = r24
            r23 = r12
            r12 = r7
            r7 = r11
            int r0 = r0.addToPopupMessages(r1, r2, r3, r4, r6, r7)
            goto L_0x01a3
        L_0x019a:
            r28 = r2
            r30 = r10
            r23 = r12
            r9 = r26
            r12 = r7
        L_0x01a3:
            if (r18 != 0) goto L_0x01ab
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner
            boolean r1 = r1.from_scheduled
            r18 = r1
        L_0x01ab:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r8.delayedPushMessages
            r1.add(r12)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r8.pushMessages
            r2 = 0
            r1.add(r2, r12)
            int r1 = (r9 > r19 ? 1 : (r9 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x01c0
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.pushMessagesDict
            r1.put(r9, r12)
            goto L_0x01c9
        L_0x01c0:
            int r1 = (r14 > r19 ? 1 : (r14 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x01c9
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.fcmRandomMessagesDict
            r1.put(r14, r12)
        L_0x01c9:
            int r1 = (r23 > r28 ? 1 : (r23 == r28 ? 0 : -1))
            if (r1 == 0) goto L_0x01ef
            android.util.LongSparseArray<java.lang.Integer> r1 = r8.pushDialogsOverrideMention
            r2 = r23
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            android.util.LongSparseArray<java.lang.Integer> r4 = r8.pushDialogsOverrideMention
            if (r1 != 0) goto L_0x01de
            r16 = 1
            goto L_0x01e5
        L_0x01de:
            int r1 = r1.intValue()
            r5 = 1
            int r16 = r1 + 1
        L_0x01e5:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r16)
            r4.put(r2, r1)
            goto L_0x01ef
        L_0x01ed:
            r30 = r10
        L_0x01ef:
            r16 = 1
        L_0x01f1:
            int r15 = r21 + 1
            r9 = r32
            r13 = r22
            r10 = r30
            r12 = 1
            goto L_0x0020
        L_0x01fc:
            if (r16 == 0) goto L_0x0202
            r1 = r35
            r8.notifyCheck = r1
        L_0x0202:
            boolean r1 = r33.isEmpty()
            if (r1 != 0) goto L_0x021c
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x021c
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 != 0) goto L_0x021c
            org.telegram.messenger.-$$Lambda$NotificationsController$vBhFCZdXUS15Ipx-fzqzTMIuA3o r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$vBhFCZdXUS15Ipx-fzqzTMIuA3o
            r2 = r33
            r1.<init>(r2, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x021c:
            if (r34 != 0) goto L_0x0220
            if (r18 == 0) goto L_0x02dc
        L_0x0220:
            if (r17 == 0) goto L_0x022e
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.delayedPushMessages
            r0.clear()
            boolean r0 = r8.notifyCheck
            r8.showOrUpdateNotification(r0)
            goto L_0x02dc
        L_0x022e:
            if (r16 == 0) goto L_0x02dc
            r0 = r32
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r1 = r0.getDialogId()
            boolean r3 = r0.isFcmMessage()
            if (r3 == 0) goto L_0x024a
            boolean r0 = r0.localChannel
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            goto L_0x024b
        L_0x024a:
            r0 = 0
        L_0x024b:
            int r3 = r8.total_unread_count
            int r4 = r8.getNotifyOverride(r11, r1)
            r5 = -1
            if (r4 != r5) goto L_0x0259
            boolean r0 = r8.isGlobalNotificationsEnabled(r1, r0)
            goto L_0x0263
        L_0x0259:
            r0 = 2
            if (r4 == r0) goto L_0x025f
            r16 = 1
            goto L_0x0261
        L_0x025f:
            r16 = 0
        L_0x0261:
            r0 = r16
        L_0x0263:
            android.util.LongSparseArray<java.lang.Integer> r4 = r8.pushDialogs
            java.lang.Object r4 = r4.get(r1)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 == 0) goto L_0x0276
            int r5 = r4.intValue()
            r16 = 1
            int r5 = r5 + 1
            goto L_0x0279
        L_0x0276:
            r16 = 1
            r5 = 1
        L_0x0279:
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            boolean r6 = r8.notifyCheck
            if (r6 == 0) goto L_0x0296
            if (r0 != 0) goto L_0x0296
            android.util.LongSparseArray<java.lang.Integer> r6 = r8.pushDialogsOverrideMention
            java.lang.Object r6 = r6.get(r1)
            java.lang.Integer r6 = (java.lang.Integer) r6
            if (r6 == 0) goto L_0x0296
            int r7 = r6.intValue()
            if (r7 == 0) goto L_0x0296
            r5 = r6
            r12 = 1
            goto L_0x0297
        L_0x0296:
            r12 = r0
        L_0x0297:
            if (r12 == 0) goto L_0x02b2
            if (r4 == 0) goto L_0x02a4
            int r0 = r8.total_unread_count
            int r4 = r4.intValue()
            int r0 = r0 - r4
            r8.total_unread_count = r0
        L_0x02a4:
            int r0 = r8.total_unread_count
            int r4 = r5.intValue()
            int r0 = r0 + r4
            r8.total_unread_count = r0
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            r0.put(r1, r5)
        L_0x02b2:
            int r0 = r8.total_unread_count
            if (r3 == r0) goto L_0x02ce
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.delayedPushMessages
            r0.clear()
            boolean r0 = r8.notifyCheck
            r8.showOrUpdateNotification(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            int r0 = r0.size()
            org.telegram.messenger.-$$Lambda$NotificationsController$R3R5Z37efc0XPsswynnBTmucwac r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$R3R5Z37efc0XPsswynnBTmucwac
            r1.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x02ce:
            r0 = 0
            r8.notifyCheck = r0
            boolean r0 = r8.showBadgeNumber
            if (r0 == 0) goto L_0x02dc
            int r0 = r31.getTotalAllUnreadCount()
            r8.setBadge(r0)
        L_0x02dc:
            if (r36 == 0) goto L_0x02e1
            r36.countDown()
        L_0x02e1:
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
            if (this.notifyCheck && !isGlobalNotificationsEnabled && (num = this.pushDialogsOverrideMention.get(keyAt)) != null && num.intValue() != 0) {
                num3 = num;
                isGlobalNotificationsEnabled = true;
            }
            if (num3.intValue() == 0) {
                this.smartNotificationsDialogs.remove(keyAt);
            }
            if (num3.intValue() < 0) {
                if (num2 == null) {
                    i2++;
                } else {
                    num3 = Integer.valueOf(num2.intValue() + num3.intValue());
                }
            }
            if ((isGlobalNotificationsEnabled || num3.intValue() == 0) && num2 != null) {
                this.total_unread_count -= num2.intValue();
            }
            if (num3.intValue() == 0) {
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
                this.total_unread_count += num3.intValue();
                this.pushDialogs.put(keyAt, num3);
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

    public void processLoadedUnreadMessages(LongSparseArray<Integer> longSparseArray, ArrayList<TLRPC$Message> arrayList, ArrayList<MessageObject> arrayList2, ArrayList<TLRPC$User> arrayList3, ArrayList<TLRPC$Chat> arrayList4, ArrayList<TLRPC$EncryptedChat> arrayList5) {
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
        if ((r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) == false) goto L_0x0050;
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
            org.telegram.tgnet.TLRPC$Message r12 = (org.telegram.tgnet.TLRPC$Message) r12
            if (r12 == 0) goto L_0x0050
            boolean r13 = r12.silent
            if (r13 == 0) goto L_0x0050
            org.telegram.tgnet.TLRPC$MessageAction r13 = r12.action
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r14 != 0) goto L_0x004b
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
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
            if (r6 >= 0) goto L_0x0281
            r4 = 1
            goto L_0x0282
        L_0x0281:
            r4 = 0
        L_0x0282:
            r0.showOrUpdateNotification(r4)
            boolean r1 = r0.showBadgeNumber
            if (r1 == 0) goto L_0x0290
            int r1 = r20.getTotalAllUnreadCount()
            r0.setBadge(r1)
        L_0x0290:
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
        int i2 = 0;
        for (int i3 = 0; i3 < 3; i3++) {
            if (UserConfig.getInstance(i3).isClientActivated()) {
                NotificationsController instance = getInstance(i3);
                if (instance.showBadgeNumber) {
                    if (instance.showBadgeMessages) {
                        if (instance.showBadgeMuted) {
                            try {
                                int size = MessagesController.getInstance(i3).allDialogs.size();
                                for (int i4 = 0; i4 < size; i4++) {
                                    TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(i3).allDialogs.get(i4);
                                    if (tLRPC$Dialog.unread_count != 0) {
                                        i2 += tLRPC$Dialog.unread_count;
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        } else {
                            i = instance.total_unread_count;
                        }
                    } else if (instance.showBadgeMuted) {
                        try {
                            int size2 = MessagesController.getInstance(i3).allDialogs.size();
                            for (int i5 = 0; i5 < size2; i5++) {
                                if (MessagesController.getInstance(i3).allDialogs.get(i5).unread_count != 0) {
                                    i2++;
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    } else {
                        i = instance.pushDialogs.size();
                    }
                    i2 += i;
                }
            }
        }
        return i2;
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

    /* JADX WARNING: Code restructure failed: missing block: B:114:0x01a0, code lost:
        if (r9.getBoolean("EnablePreviewAll", true) == false) goto L_0x01a4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x01ac, code lost:
        if (r9.getBoolean("EnablePreviewGroup", r3) != false) goto L_0x01b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x01b6, code lost:
        if (r9.getBoolean("EnablePreviewChannel", r3) != false) goto L_0x01b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x01b8, code lost:
        r2 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x01c6, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0a61;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x01c8, code lost:
        r20[0] = null;
        r2 = r2.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x01cf, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L_0x0a51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x01d3, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L_0x01d7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x01d9, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L_0x01ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01e9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", NUM, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x01ed, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L_0x024e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x01ef, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", NUM, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(((long) r0.messageOwner.date) * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(((long) r0.messageOwner.date) * 1000));
        r0 = r0.messageOwner.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x024d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", NUM, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0250, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L_0x0a4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x0254, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L_0x0258;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x025a, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L_0x0266;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0265, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0268, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x037e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x026a, code lost:
        r3 = r2.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x026c, code lost:
        if (r3 != 0) goto L_0x0288;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0275, code lost:
        if (r2.users.size() != 1) goto L_0x0288;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0277, code lost:
        r3 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x0288, code lost:
        if (r3 == 0) goto L_0x0326;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x0290, code lost:
        if (r0.messageOwner.to_id.channel_id == 0) goto L_0x02ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0294, code lost:
        if (r8.megagroup != false) goto L_0x02ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x02aa, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x02b3, code lost:
        if (r3 != getUserConfig().getClientUserId()) goto L_0x02ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x02c9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x02ca, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x02d6, code lost:
        if (r0 != null) goto L_0x02d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x02d8, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x02db, code lost:
        if (r1 != r0.id) goto L_0x030b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x02df, code lost:
        if (r8.megagroup == false) goto L_0x02f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x02f5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x030a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x0325, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r7, r8.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x0326, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x0336, code lost:
        if (r2 >= r0.messageOwner.action.users.size()) goto L_0x0363;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x0338, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x034c, code lost:
        if (r3 == null) goto L_0x0360;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:181:0x034e, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x0356, code lost:
        if (r1.length() == 0) goto L_0x035d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x0358, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x035d, code lost:
        r1.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x0360, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x037d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r7, r8.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0381, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x0397;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x0396, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x039b, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x03af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x03ae, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x03b1, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0a17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x03b5, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x03b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x03bb, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x042b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x03c7, code lost:
        if (r2.user_id != getUserConfig().getClientUserId()) goto L_0x03de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x03dd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x03e4, code lost:
        if (r0.messageOwner.action.user_id != r1) goto L_0x03fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x03fa, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x03fb, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x040d, code lost:
        if (r0 != null) goto L_0x0410;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x040f, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x042a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r7, r8.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x042d, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x0436;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x0435, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x0438, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x0441;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x0440, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x0443, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x0457;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x0456, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x045b, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x046d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x046c, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x046f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x0478;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x0477, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x047a, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0a16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x047e, code lost:
        if (r8 == null) goto L_0x076c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0484, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r8) == false) goto L_0x048a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x0488, code lost:
        if (r8.megagroup == false) goto L_0x076c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x048a, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x048c, code lost:
        if (r0 != null) goto L_0x04a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x04a2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x04aa, code lost:
        if (r0.isMusic() == false) goto L_0x04be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x04bd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x04c7, code lost:
        if (r0.isVideo() == false) goto L_0x0511;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04cb, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x04fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x04d5, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x04fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x04fb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "📹 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0510, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0515, code lost:
        if (r0.isGif() == false) goto L_0x055f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0519, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x054a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x0523, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x054a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x0549, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "🎬 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x055e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0566, code lost:
        if (r0.isVoice() == false) goto L_0x057a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0579, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x057e, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0592;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0591, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0596, code lost:
        if (r0.isSticker() != false) goto L_0x073c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x059c, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x05a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x05a0, code lost:
        r2 = r0.messageOwner;
        r4 = r2.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x05a6, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x05ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x05aa, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x05d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x05b2, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x05d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x05d8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "📎 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x05ed, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x05f0, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0727;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x05f4, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x05f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x05fa, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0611;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x0610, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x0615, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0636;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x0617, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x0635, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r7, r8.title, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x0638, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0674;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x063a, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x0640, code lost:
        if (r0.quiz == false) goto L_0x065b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x065a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r7, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x0673, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r7, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x0676, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x06be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x067a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x06a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x0682, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x06a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x06a8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "🖼 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x06bd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x06c3, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x06d7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x06d6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x06d7, code lost:
        r2 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x06d9, code lost:
        if (r2 == null) goto L_0x0712;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x06df, code lost:
        if (r2.length() <= 0) goto L_0x0712;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x06e1, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x06e7, code lost:
        if (r0.length() <= 20) goto L_0x0700;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x06e9, code lost:
        r2 = new java.lang.StringBuilder();
        r4 = 0;
        r2.append(r0.subSequence(0, 20));
        r2.append("...");
        r0 = r2.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0700, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0701, code lost:
        r1 = new java.lang.Object[3];
        r1[r4] = r7;
        r1[1] = r0;
        r1[2] = r8.title;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x0711, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x0726, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x073b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x073c, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x0742, code lost:
        if (r0 == null) goto L_0x0759;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0758, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r7, r8.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x076b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x076c, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x076f, code lost:
        if (r0 != null) goto L_0x0782;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0781, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x0787, code lost:
        if (r0.isMusic() == false) goto L_0x0799;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0798, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x07a2, code lost:
        if (r0.isVideo() == false) goto L_0x07e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x07a6, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x07d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x07b0, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x07d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x07d4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r8.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x07e6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x07eb, code lost:
        if (r0.isGif() == false) goto L_0x0830;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x07ef, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x081e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x07f9, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x081e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x081d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r8.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x082f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x0836, code lost:
        if (r0.isVoice() == false) goto L_0x0848;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x0847, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x084c, code lost:
        if (r0.isRoundVideo() == false) goto L_0x085e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x085d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x0862, code lost:
        if (r0.isSticker() != false) goto L_0x09ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x0868, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x086c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x086c, code lost:
        r2 = r0.messageOwner;
        r4 = r2.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x0872, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x08b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x0876, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x08a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x087e, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x08a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x08a2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r8.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x08b4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x08b7, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x09d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x08bb, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x08bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x08c1, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x08d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x08d4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x08d8, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x08f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x08da, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x08f7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r8.title, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x08fa, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0932;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x08fc, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0902, code lost:
        if (r0.quiz == false) goto L_0x091b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x091a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0931, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0934, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0977;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x0938, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0965;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0940, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x0965;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x0964, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r8.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x0976, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x097b, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x098d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x098c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x098d, code lost:
        r2 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x098f, code lost:
        if (r2 == null) goto L_0x09c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x0995, code lost:
        if (r2.length() <= 0) goto L_0x09c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0997, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x099d, code lost:
        if (r0.length() <= 20) goto L_0x09b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x099f, code lost:
        r2 = new java.lang.StringBuilder();
        r4 = 0;
        r2.append(r0.subSequence(0, 20));
        r2.append("...");
        r0 = r2.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x09b6, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x09b7, code lost:
        r1 = new java.lang.Object[2];
        r1[r4] = r8.title;
        r1[1] = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x09c5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x09d7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x09e9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x09ea, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x09ef, code lost:
        if (r0 == null) goto L_0x0a05;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0a04, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r8.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x0a15, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0a16, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x0a1d, code lost:
        if (r0.messageOwner.to_id.channel_id == 0) goto L_0x0a35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0a21, code lost:
        if (r8.megagroup != false) goto L_0x0a35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x0a34, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x0a49, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0a50, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0a60, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", NUM, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0a65, code lost:
        if (r19.isMediaEmpty() == false) goto L_0x0a7e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0a6f, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0a76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0a75, code lost:
        return r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0a7d, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0a7e, code lost:
        r1 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0a84, code lost:
        if ((r1.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0a88, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0aa6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0a90, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L_0x0aa6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0aa5, code lost:
        return "🖼 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0aac, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0ab8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0ab7, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0ac1, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0ac6, code lost:
        if (r19.isVideo() == false) goto L_0x0b06;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0aca, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0aea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0ad4, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0aea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0ae9, code lost:
        return "📹 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x0af0, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0afc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0afb, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x0b05, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x0b0a, code lost:
        if (r19.isGame() == false) goto L_0x0b16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0b15, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0b1a, code lost:
        if (r19.isVoice() == false) goto L_0x0b26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0b25, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0b2a, code lost:
        if (r19.isRoundVideo() == false) goto L_0x0b36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x0b35, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0b3a, code lost:
        if (r19.isMusic() == false) goto L_0x0b46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0b45, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0b46, code lost:
        r1 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x0b4c, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0b58;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0b57, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0b5a, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0b78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0b62, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1).poll.quiz == false) goto L_0x0b6e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0b6d, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0b77, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0b7a, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0b7e, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0b82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0b84, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0b90;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0b8f, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0b92, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0c2d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0b98, code lost:
        if (r19.isSticker() != false) goto L_0x0bff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0b9e, code lost:
        if (r19.isAnimatedSticker() == false) goto L_0x0ba1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0ba5, code lost:
        if (r19.isGif() == false) goto L_0x0bd3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0ba9, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0bc9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0bb3, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0bc9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0bc8, code lost:
        return "🎬 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0bd2, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0bd5, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0bf5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x0bdf, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0bf5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0bf4, code lost:
        return "📎 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0bfe, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x0bff, code lost:
        r0 = r19.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0CLASSNAME, code lost:
        if (r0 == null) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0CLASSNAME, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0c2c, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x0c3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0c3b, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0c4d, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0153 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0154  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r19, java.lang.String[] r20, boolean[] r21) {
        /*
            r18 = this;
            r0 = r19
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            java.lang.String r3 = "NotificationHiddenMessage"
            if (r1 != 0) goto L_0x0c5b
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x0010
            goto L_0x0c5b
        L_0x0010:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r4 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r6 = r1.chat_id
            if (r6 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            int r6 = r1.channel_id
        L_0x001d:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.user_id
            r7 = 1
            r8 = 0
            if (r21 == 0) goto L_0x0029
            r21[r8] = r7
        L_0x0029:
            org.telegram.messenger.AccountInstance r9 = r18.getAccountInstance()
            android.content.SharedPreferences r9 = r9.getNotificationsSettings()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "content_preview_"
            r10.append(r11)
            r10.append(r4)
            java.lang.String r10 = r10.toString()
            boolean r10 = r9.getBoolean(r10, r7)
            boolean r11 = r19.isFcmMessage()
            java.lang.String r12 = "EnablePreviewGroup"
            java.lang.String r13 = "EnablePreviewAll"
            java.lang.String r15 = "Message"
            r2 = 27
            r14 = 2
            if (r11 == 0) goto L_0x00e6
            if (r6 != 0) goto L_0x0075
            if (r1 == 0) goto L_0x0075
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 <= r2) goto L_0x0061
            java.lang.String r1 = r0.localName
            r20[r8] = r1
        L_0x0061:
            if (r10 == 0) goto L_0x0069
            boolean r1 = r9.getBoolean(r13, r7)
            if (r1 != 0) goto L_0x00e1
        L_0x0069:
            if (r21 == 0) goto L_0x006d
            r21[r8] = r8
        L_0x006d:
            r0 = 2131625690(0x7f0e06da, float:1.8878595E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0075:
            if (r6 == 0) goto L_0x00e1
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x008f
            boolean r1 = r19.isMegagroup()
            if (r1 == 0) goto L_0x0086
            goto L_0x008f
        L_0x0086:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 <= r2) goto L_0x0093
            java.lang.String r1 = r0.localName
            r20[r8] = r1
            goto L_0x0093
        L_0x008f:
            java.lang.String r1 = r0.localUserName
            r20[r8] = r1
        L_0x0093:
            if (r10 == 0) goto L_0x00ab
            boolean r1 = r0.localChannel
            if (r1 != 0) goto L_0x009f
            boolean r1 = r9.getBoolean(r12, r7)
            if (r1 == 0) goto L_0x00ab
        L_0x009f:
            boolean r1 = r0.localChannel
            if (r1 == 0) goto L_0x00e1
            java.lang.String r1 = "EnablePreviewChannel"
            boolean r1 = r9.getBoolean(r1, r7)
            if (r1 != 0) goto L_0x00e1
        L_0x00ab:
            if (r21 == 0) goto L_0x00af
            r21[r8] = r8
        L_0x00af:
            boolean r1 = r19.isMegagroup()
            if (r1 != 0) goto L_0x00cd
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00cd
            r1 = 2131624576(0x7f0e0280, float:1.8876336E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            java.lang.String r0 = r0.localName
            r2[r8] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00cd:
            r1 = 2131625928(0x7f0e07c8, float:1.8879078E38)
            java.lang.Object[] r2 = new java.lang.Object[r14]
            java.lang.String r3 = r0.localUserName
            r2[r8] = r3
            java.lang.String r0 = r0.localName
            r2[r7] = r0
            java.lang.String r0 = "NotificationMessageGroupNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00e1:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x00e6:
            if (r1 != 0) goto L_0x00fc
            boolean r1 = r19.isFromUser()
            if (r1 != 0) goto L_0x00f7
            int r1 = r19.getId()
            if (r1 >= 0) goto L_0x00f5
            goto L_0x00f7
        L_0x00f5:
            int r1 = -r6
            goto L_0x010a
        L_0x00f7:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            int r1 = r1.from_id
            goto L_0x010a
        L_0x00fc:
            org.telegram.messenger.UserConfig r11 = r18.getUserConfig()
            int r11 = r11.getClientUserId()
            if (r1 != r11) goto L_0x010a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            int r1 = r1.from_id
        L_0x010a:
            r16 = 0
            int r11 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r11 != 0) goto L_0x0118
            if (r6 == 0) goto L_0x0115
            int r4 = -r6
            long r4 = (long) r4
            goto L_0x0118
        L_0x0115:
            if (r1 == 0) goto L_0x0118
            long r4 = (long) r1
        L_0x0118:
            r11 = 0
            if (r1 <= 0) goto L_0x013c
            org.telegram.messenger.MessagesController r14 = r18.getMessagesController()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r7 = r14.getUser(r7)
            if (r7 == 0) goto L_0x0150
            java.lang.String r7 = org.telegram.messenger.UserObject.getUserName(r7)
            if (r6 == 0) goto L_0x0132
            r20[r8] = r7
            goto L_0x0151
        L_0x0132:
            int r14 = android.os.Build.VERSION.SDK_INT
            if (r14 <= r2) goto L_0x0139
            r20[r8] = r7
            goto L_0x0151
        L_0x0139:
            r20[r8] = r11
            goto L_0x0151
        L_0x013c:
            org.telegram.messenger.MessagesController r7 = r18.getMessagesController()
            int r14 = -r1
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r14)
            if (r7 == 0) goto L_0x0150
            java.lang.String r7 = r7.title
            r20[r8] = r7
            goto L_0x0151
        L_0x0150:
            r7 = r11
        L_0x0151:
            if (r7 != 0) goto L_0x0154
            return r11
        L_0x0154:
            if (r6 == 0) goto L_0x0179
            org.telegram.messenger.MessagesController r14 = r18.getMessagesController()
            java.lang.Integer r8 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r8 = r14.getChat(r8)
            if (r8 != 0) goto L_0x0165
            return r11
        L_0x0165:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r14 == 0) goto L_0x0177
            boolean r14 = r8.megagroup
            if (r14 != 0) goto L_0x0177
            int r14 = android.os.Build.VERSION.SDK_INT
            if (r14 > r2) goto L_0x0177
            r2 = 0
            r20[r2] = r11
            goto L_0x017b
        L_0x0177:
            r2 = 0
            goto L_0x017b
        L_0x0179:
            r2 = 0
            r8 = r11
        L_0x017b:
            int r5 = (int) r4
            if (r5 != 0) goto L_0x0188
            r20[r2] = r11
            r0 = 2131625905(0x7f0e07b1, float:1.8879031E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            return r0
        L_0x0188:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r2 == 0) goto L_0x0194
            boolean r2 = r8.megagroup
            if (r2 != 0) goto L_0x0194
            r2 = 1
            goto L_0x0195
        L_0x0194:
            r2 = 0
        L_0x0195:
            if (r10 == 0) goto L_0x0c4e
            if (r6 != 0) goto L_0x01a3
            if (r1 == 0) goto L_0x01a3
            r3 = 1
            boolean r4 = r9.getBoolean(r13, r3)
            if (r4 != 0) goto L_0x01b8
            goto L_0x01a4
        L_0x01a3:
            r3 = 1
        L_0x01a4:
            if (r6 == 0) goto L_0x0c4e
            if (r2 != 0) goto L_0x01ae
            boolean r4 = r9.getBoolean(r12, r3)
            if (r4 != 0) goto L_0x01b8
        L_0x01ae:
            if (r2 == 0) goto L_0x0c4e
            java.lang.String r2 = "EnablePreviewChannel"
            boolean r2 = r9.getBoolean(r2, r3)
            if (r2 == 0) goto L_0x0c4e
        L_0x01b8:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r4 = "🎬 "
            java.lang.String r5 = "📎 "
            java.lang.String r6 = "📹 "
            java.lang.String r9 = "🖼 "
            r10 = 19
            if (r3 == 0) goto L_0x0a61
            r3 = 0
            r20[r3] = r11
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r12 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r12 != 0) goto L_0x0a51
            boolean r12 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r12 == 0) goto L_0x01d7
            goto L_0x0a51
        L_0x01d7:
            boolean r12 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r12 == 0) goto L_0x01ea
            r0 = 2131625891(0x7f0e07a3, float:1.8879003E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r7
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x01ea:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            r12 = 3
            if (r3 == 0) goto L_0x024e
            r1 = 2131627529(0x7f0e0e09, float:1.8882325E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterYear
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r4 = (long) r4
            r6 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 * r6
            java.lang.String r2 = r2.format((long) r4)
            r4 = 0
            r3[r4] = r2
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterDay
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r4 = (long) r4
            long r4 = r4 * r6
            java.lang.String r2 = r2.format((long) r4)
            r4 = 1
            r3[r4] = r2
            java.lang.String r2 = "formatDateAtTime"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            r2 = 2131625955(0x7f0e07e3, float:1.8879133E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r5 = r18.getUserConfig()
            org.telegram.tgnet.TLRPC$User r5 = r5.getCurrentUser()
            java.lang.String r5 = r5.first_name
            r6 = 0
            r3[r6] = r5
            r3[r4] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r1 = r0.title
            r4 = 2
            r3[r4] = r1
            java.lang.String r0 = r0.address
            r3[r12] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x024e:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r3 != 0) goto L_0x0a4a
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r3 == 0) goto L_0x0258
            goto L_0x0a4a
        L_0x0258:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r3 == 0) goto L_0x0266
            r0 = 2131624472(0x7f0e0218, float:1.8876125E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0266:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r3 == 0) goto L_0x037e
            int r3 = r2.user_id
            if (r3 != 0) goto L_0x0288
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            int r2 = r2.size()
            r4 = 1
            if (r2 != r4) goto L_0x0288
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r3 = r2.intValue()
        L_0x0288:
            if (r3 == 0) goto L_0x0326
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x02ab
            boolean r0 = r8.megagroup
            if (r0 != 0) goto L_0x02ab
            r0 = 2131624527(0x7f0e024f, float:1.8876236E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x02ab:
            org.telegram.messenger.UserConfig r0 = r18.getUserConfig()
            int r0 = r0.getClientUserId()
            if (r3 != r0) goto L_0x02ca
            r0 = 2131625907(0x7f0e07b3, float:1.8879035E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x02ca:
            org.telegram.messenger.MessagesController r0 = r18.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x02d9
            return r11
        L_0x02d9:
            int r2 = r0.id
            if (r1 != r2) goto L_0x030b
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x02f6
            r0 = 2131625896(0x7f0e07a8, float:1.8879013E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x02f6:
            r1 = 2
            r2 = 0
            r3 = 1
            r0 = 2131625895(0x7f0e07a7, float:1.887901E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x030b:
            r2 = 0
            r3 = 1
            r1 = 2131625894(0x7f0e07a6, float:1.8879009E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            r4[r2] = r7
            java.lang.String r2 = r8.title
            r4[r3] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r4[r2] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r4)
            return r0
        L_0x0326:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x032c:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0363
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0360
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x035d
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x035d:
            r1.append(r3)
        L_0x0360:
            int r2 = r2 + 1
            goto L_0x032c
        L_0x0363:
            r0 = 2131625894(0x7f0e07a6, float:1.8879009E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r3 = 0
            r2[r3] = r7
            java.lang.String r3 = r8.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r1 = r1.toString()
            r3 = 2
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x037e:
            r3 = 2
            boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r13 == 0) goto L_0x0397
            r0 = 2131625908(0x7f0e07b4, float:1.8879037E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r13 = 0
            r1[r13] = r7
            java.lang.String r2 = r8.title
            r14 = 1
            r1[r14] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0397:
            r13 = 0
            r14 = 1
            boolean r15 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r15 == 0) goto L_0x03af
            r0 = 2131625892(0x7f0e07a4, float:1.8879005E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r13] = r7
            java.lang.String r2 = r2.title
            r1[r14] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03af:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r3 != 0) goto L_0x0a17
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r3 == 0) goto L_0x03b9
            goto L_0x0a17
        L_0x03b9:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r3 == 0) goto L_0x042b
            int r2 = r2.user_id
            org.telegram.messenger.UserConfig r3 = r18.getUserConfig()
            int r3 = r3.getClientUserId()
            if (r2 != r3) goto L_0x03de
            r0 = 2131625901(0x7f0e07ad, float:1.8879023E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03de:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.user_id
            if (r2 != r1) goto L_0x03fb
            r0 = 2131625902(0x7f0e07ae, float:1.8879025E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03fb:
            org.telegram.messenger.MessagesController r1 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x0410
            return r11
        L_0x0410:
            r1 = 2131625900(0x7f0e07ac, float:1.887902E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r3 = 0
            r2[r3] = r7
            java.lang.String r3 = r8.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x042b:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r1 == 0) goto L_0x0436
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0436:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r1 == 0) goto L_0x0441
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0441:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r1 == 0) goto L_0x0457
            r0 = 2131624066(0x7f0e0082, float:1.8875301E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0457:
            r1 = 1
            r3 = 0
            boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r13 == 0) goto L_0x046d
            r0 = 2131624066(0x7f0e0082, float:1.8875301E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x046d:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r1 == 0) goto L_0x0478
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0478:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r1 == 0) goto L_0x0a16
            r1 = 20
            if (r8 == 0) goto L_0x076c
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r2 == 0) goto L_0x048a
            boolean r2 = r8.megagroup
            if (r2 == 0) goto L_0x076c
        L_0x048a:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x04a3
            r0 = 2131625870(0x7f0e078e, float:1.887896E38)
            r2 = 2
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r3 = 0
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r11 = 1
            r1[r11] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04a3:
            r2 = 2
            r3 = 0
            r11 = 1
            boolean r13 = r0.isMusic()
            if (r13 == 0) goto L_0x04be
            r0 = 2131625868(0x7f0e078c, float:1.8878956E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r11] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04be:
            boolean r2 = r0.isVideo()
            r3 = 2131625884(0x7f0e079c, float:1.8878989E38)
            java.lang.String r11 = "NotificationActionPinnedText"
            if (r2 == 0) goto L_0x0511
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x04fc
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x04fc
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r2 = 0
            r1[r2] = r7
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = r8.title
            r5 = 2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r3, r1)
            return r0
        L_0x04fc:
            r2 = 0
            r4 = 1
            r5 = 2
            r0 = 2131625886(0x7f0e079e, float:1.8878993E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0511:
            boolean r2 = r0.isGif()
            if (r2 == 0) goto L_0x055f
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x054a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x054a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r2 = 0
            r1[r2] = r7
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = r8.title
            r6 = 2
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r3, r1)
            return r0
        L_0x054a:
            r2 = 0
            r4 = 1
            r6 = 2
            r0 = 2131625864(0x7f0e0788, float:1.8878948E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x055f:
            r2 = 0
            r4 = 1
            r6 = 2
            boolean r13 = r0.isVoice()
            if (r13 == 0) goto L_0x057a
            r0 = 2131625888(0x7f0e07a0, float:1.8878997E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x057a:
            boolean r13 = r0.isRoundVideo()
            if (r13 == 0) goto L_0x0592
            r0 = 2131625878(0x7f0e0796, float:1.8878976E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0592:
            boolean r2 = r0.isSticker()
            if (r2 != 0) goto L_0x073c
            boolean r2 = r0.isAnimatedSticker()
            if (r2 == 0) goto L_0x05a0
            goto L_0x073c
        L_0x05a0:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x05ee
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x05d9
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x05d9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r2 = 0
            r1[r2] = r7
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = r8.title
            r5 = 2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r3, r1)
            return r0
        L_0x05d9:
            r2 = 0
            r4 = 1
            r5 = 2
            r0 = 2131625854(0x7f0e077e, float:1.8878928E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05ee:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x0727
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x05f8
            goto L_0x0727
        L_0x05f8:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0611
            r0 = 2131625862(0x7f0e0786, float:1.8878944E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r5 = 0
            r1[r5] = r7
            java.lang.String r2 = r8.title
            r6 = 1
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0611:
            r5 = 0
            r6 = 1
            boolean r13 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r13 == 0) goto L_0x0636
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4
            r0 = 2131625852(0x7f0e077c, float:1.8878924E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r5] = r7
            java.lang.String r2 = r8.title
            r1[r6] = r2
            java.lang.String r2 = r4.first_name
            java.lang.String r3 = r4.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0636:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0674
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x065b
            r1 = 2131625876(0x7f0e0794, float:1.8878972E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r3 = 0
            r2[r3] = r7
            java.lang.String r3 = r8.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r5 = 2
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x065b:
            r3 = 0
            r4 = 1
            r5 = 2
            r1 = 2131625874(0x7f0e0792, float:1.8878968E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r2[r3] = r7
            java.lang.String r3 = r8.title
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0674:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x06be
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x06a9
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x06a9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r2 = 0
            r1[r2] = r7
            r5 = 1
            r1[r5] = r0
            java.lang.String r0 = r8.title
            r6 = 2
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r3, r1)
            return r0
        L_0x06a9:
            r2 = 0
            r5 = 1
            r6 = 2
            r0 = 2131625872(0x7f0e0790, float:1.8878964E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06be:
            r2 = 0
            r5 = 1
            r6 = 2
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x06d7
            r0 = 2131625856(0x7f0e0780, float:1.8878932E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06d7:
            java.lang.CharSequence r2 = r0.messageText
            if (r2 == 0) goto L_0x0712
            int r2 = r2.length()
            if (r2 <= 0) goto L_0x0712
            java.lang.CharSequence r0 = r0.messageText
            int r2 = r0.length()
            if (r2 <= r1) goto L_0x0700
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r4 = 0
            java.lang.CharSequence r0 = r0.subSequence(r4, r1)
            r2.append(r0)
            java.lang.String r0 = "..."
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0701
        L_0x0700:
            r4 = 0
        L_0x0701:
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r4] = r7
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = r8.title
            r5 = 2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r3, r1)
            return r0
        L_0x0712:
            r2 = 1
            r4 = 0
            r5 = 2
            r0 = 2131625870(0x7f0e078e, float:1.887896E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r7
            java.lang.String r3 = r8.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0727:
            r2 = 1
            r4 = 0
            r5 = 2
            r0 = 2131625860(0x7f0e0784, float:1.887894E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r7
            java.lang.String r3 = r8.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x073c:
            r2 = 1
            r4 = 0
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0759
            r1 = 2131625882(0x7f0e079a, float:1.8878984E38)
            java.lang.Object[] r3 = new java.lang.Object[r12]
            r3[r4] = r7
            java.lang.String r4 = r8.title
            r3[r2] = r4
            r5 = 2
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x0759:
            r5 = 2
            r0 = 2131625880(0x7f0e0798, float:1.887898E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r7
            java.lang.String r3 = r8.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x076c:
            r2 = 1
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0782
            r0 = 2131625871(0x7f0e078f, float:1.8878962E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0782:
            r3 = 0
            boolean r7 = r0.isMusic()
            if (r7 == 0) goto L_0x0799
            r0 = 2131625869(0x7f0e078d, float:1.8878958E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0799:
            boolean r2 = r0.isVideo()
            r3 = 2131625885(0x7f0e079d, float:1.887899E38)
            java.lang.String r7 = "NotificationActionPinnedTextChannel"
            if (r2 == 0) goto L_0x07e7
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x07d5
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x07d5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r4 = 0
            r1[r4] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r3, r1)
            return r0
        L_0x07d5:
            r2 = 1
            r4 = 0
            r0 = 2131625887(0x7f0e079f, float:1.8878995E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07e7:
            boolean r2 = r0.isGif()
            if (r2 == 0) goto L_0x0830
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x081e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x081e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r4 = 0
            r1[r4] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r3, r1)
            return r0
        L_0x081e:
            r2 = 1
            r4 = 0
            r0 = 2131625865(0x7f0e0789, float:1.887895E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0830:
            r2 = 1
            r4 = 0
            boolean r6 = r0.isVoice()
            if (r6 == 0) goto L_0x0848
            r0 = 2131625889(0x7f0e07a1, float:1.8878999E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0848:
            boolean r6 = r0.isRoundVideo()
            if (r6 == 0) goto L_0x085e
            r0 = 2131625879(0x7f0e0797, float:1.8878978E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x085e:
            boolean r2 = r0.isSticker()
            if (r2 != 0) goto L_0x09ea
            boolean r2 = r0.isAnimatedSticker()
            if (r2 == 0) goto L_0x086c
            goto L_0x09ea
        L_0x086c:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x08b5
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x08a3
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x08a3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r4 = 0
            r1[r4] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r3, r1)
            return r0
        L_0x08a3:
            r2 = 1
            r4 = 0
            r0 = 2131625855(0x7f0e077f, float:1.887893E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08b5:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x09d8
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x08bf
            goto L_0x09d8
        L_0x08bf:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x08d5
            r0 = 2131625863(0x7f0e0787, float:1.8878946E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r5 = 0
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08d5:
            r5 = 0
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r6 == 0) goto L_0x08f8
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4
            r0 = 2131625853(0x7f0e077d, float:1.8878926E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = r4.first_name
            java.lang.String r3 = r4.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08f8:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0932
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x091b
            r1 = 2131625877(0x7f0e0795, float:1.8878974E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r8.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x091b:
            r2 = 2
            r3 = 1
            r4 = 0
            r1 = 2131625875(0x7f0e0793, float:1.887897E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r5 = r8.title
            r2[r4] = r5
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0932:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0977
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x0965
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0965
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r5 = 0
            r1[r5] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r3, r1)
            return r0
        L_0x0965:
            r2 = 1
            r5 = 0
            r0 = 2131625873(0x7f0e0791, float:1.8878966E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0977:
            r2 = 1
            r5 = 0
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x098d
            r0 = 2131625857(0x7f0e0781, float:1.8878934E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x098d:
            java.lang.CharSequence r2 = r0.messageText
            if (r2 == 0) goto L_0x09c6
            int r2 = r2.length()
            if (r2 <= 0) goto L_0x09c6
            java.lang.CharSequence r0 = r0.messageText
            int r2 = r0.length()
            if (r2 <= r1) goto L_0x09b6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r4 = 0
            java.lang.CharSequence r0 = r0.subSequence(r4, r1)
            r2.append(r0)
            java.lang.String r0 = "..."
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x09b7
        L_0x09b6:
            r4 = 0
        L_0x09b7:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r3, r1)
            return r0
        L_0x09c6:
            r2 = 1
            r4 = 0
            r0 = 2131625871(0x7f0e078f, float:1.8878962E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09d8:
            r2 = 1
            r4 = 0
            r0 = 2131625861(0x7f0e0785, float:1.8878942E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09ea:
            r4 = 0
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0a05
            r1 = 2131625883(0x7f0e079b, float:1.8878987E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r8.title
            r2[r4] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0a05:
            r3 = 1
            r0 = 2131625881(0x7f0e0799, float:1.8878982E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a16:
            return r11
        L_0x0a17:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x0a35
            boolean r0 = r8.megagroup
            if (r0 != 0) goto L_0x0a35
            r0 = 2131624591(0x7f0e028f, float:1.8876366E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a35:
            r3 = 0
            r0 = 2131625893(0x7f0e07a5, float:1.8879007E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a4a:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0a51:
            r3 = 1
            r0 = 2131625890(0x7f0e07a2, float:1.8879E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a61:
            boolean r1 = r19.isMediaEmpty()
            if (r1 == 0) goto L_0x0a7e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0a76
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x0a76:
            r0 = 2131625690(0x7f0e06da, float:1.8878595E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0a7e:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0ac2
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r10) goto L_0x0aa6
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0aa6
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0aa6:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0ab8
            r0 = 2131624284(0x7f0e015c, float:1.8875743E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ab8:
            r0 = 2131624299(0x7f0e016b, float:1.8875774E38)
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ac2:
            boolean r1 = r19.isVideo()
            if (r1 == 0) goto L_0x0b06
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x0aea
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0aea
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0aea:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0afc
            r0 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0afc:
            r0 = 2131624305(0x7f0e0171, float:1.8875786E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b06:
            boolean r1 = r19.isGame()
            if (r1 == 0) goto L_0x0b16
            r0 = 2131624287(0x7f0e015f, float:1.887575E38)
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b16:
            boolean r1 = r19.isVoice()
            if (r1 == 0) goto L_0x0b26
            r0 = 2131624281(0x7f0e0159, float:1.8875737E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b26:
            boolean r1 = r19.isRoundVideo()
            if (r1 == 0) goto L_0x0b36
            r0 = 2131624301(0x7f0e016d, float:1.8875778E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b36:
            boolean r1 = r19.isMusic()
            if (r1 == 0) goto L_0x0b46
            r0 = 2131624298(0x7f0e016a, float:1.8875772E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b46:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r2 == 0) goto L_0x0b58
            r0 = 2131624283(0x7f0e015b, float:1.8875741E38)
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b58:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0b78
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0b6e
            r0 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b6e:
            r0 = 2131626398(0x7f0e099e, float:1.8880031E38)
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b78:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r2 != 0) goto L_0x0CLASSNAME
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r2 == 0) goto L_0x0b82
            goto L_0x0CLASSNAME
        L_0x0b82:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x0b90
            r0 = 2131624293(0x7f0e0165, float:1.8875762E38)
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b90:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x0c2d
            boolean r1 = r19.isSticker()
            if (r1 != 0) goto L_0x0bff
            boolean r1 = r19.isAnimatedSticker()
            if (r1 == 0) goto L_0x0ba1
            goto L_0x0bff
        L_0x0ba1:
            boolean r1 = r19.isGif()
            if (r1 == 0) goto L_0x0bd3
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x0bc9
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0bc9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0bc9:
            r0 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0bd3:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x0bf5
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0bf5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0bf5:
            r0 = 2131624286(0x7f0e015e, float:1.8875747E38)
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0bff:
            java.lang.String r0 = r19.getStickerEmoji()
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " "
            r1.append(r0)
            r0 = 2131624302(0x7f0e016e, float:1.887578E38)
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0CLASSNAME:
            r0 = 2131624302(0x7f0e016e, float:1.887578E38)
            java.lang.String r1 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0c2d:
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0c3c
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0c3c:
            r0 = 2131625690(0x7f0e06da, float:1.8878595E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0CLASSNAME:
            r0 = 2131624295(0x7f0e0167, float:1.8875766E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0c4e:
            if (r21 == 0) goto L_0x0CLASSNAME
            r0 = 0
            r21[r0] = r0
        L_0x0CLASSNAME:
            r0 = 2131625690(0x7f0e06da, float:1.8878595E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0c5b:
            r0 = 2131625905(0x7f0e07b1, float:1.8879031E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:75:0x0148 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0149  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r20, boolean r21, boolean[] r22, boolean[] r23) {
        /*
            r19 = this;
            r0 = r20
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x134b
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x134b
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
            if (r23 == 0) goto L_0x0027
            r23[r6] = r5
        L_0x0027:
            org.telegram.messenger.AccountInstance r7 = r19.getAccountInstance()
            android.content.SharedPreferences r7 = r7.getNotificationsSettings()
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "content_preview_"
            r8.append(r9)
            r8.append(r2)
            java.lang.String r8 = r8.toString()
            boolean r8 = r7.getBoolean(r8, r5)
            boolean r9 = r20.isFcmMessage()
            r10 = 2131625928(0x7f0e07c8, float:1.8879078E38)
            java.lang.String r11 = "NotificationMessageGroupNoText"
            r12 = 2131625941(0x7f0e07d5, float:1.8879104E38)
            java.lang.String r13 = "NotificationMessageNoText"
            r14 = 2
            if (r9 == 0) goto L_0x00c6
            if (r4 != 0) goto L_0x0072
            if (r1 == 0) goto L_0x0072
            if (r8 == 0) goto L_0x0063
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 != 0) goto L_0x00bf
        L_0x0063:
            if (r23 == 0) goto L_0x0067
            r23[r6] = r6
        L_0x0067:
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r0 = r0.localName
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r13, r12, r1)
            return r0
        L_0x0072:
            if (r4 == 0) goto L_0x00bf
            if (r8 == 0) goto L_0x008e
            boolean r1 = r0.localChannel
            if (r1 != 0) goto L_0x0082
            java.lang.String r1 = "EnablePreviewGroup"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 == 0) goto L_0x008e
        L_0x0082:
            boolean r1 = r0.localChannel
            if (r1 == 0) goto L_0x00bf
            java.lang.String r1 = "EnablePreviewChannel"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 != 0) goto L_0x00bf
        L_0x008e:
            if (r23 == 0) goto L_0x0092
            r23[r6] = r6
        L_0x0092:
            boolean r1 = r20.isMegagroup()
            if (r1 != 0) goto L_0x00b0
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00b0
            r1 = 2131624576(0x7f0e0280, float:1.8876336E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            java.lang.String r0 = r0.localName
            r2[r6] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00b0:
            java.lang.Object[] r1 = new java.lang.Object[r14]
            java.lang.String r2 = r0.localUserName
            r1[r6] = r2
            java.lang.String r0 = r0.localName
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x00bf:
            r22[r6] = r5
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = (java.lang.String) r0
            return r0
        L_0x00c6:
            org.telegram.messenger.UserConfig r9 = r19.getUserConfig()
            int r9 = r9.getClientUserId()
            if (r1 != 0) goto L_0x00e4
            boolean r1 = r20.isFromUser()
            if (r1 != 0) goto L_0x00df
            int r1 = r20.getId()
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
            r15 = 0
            int r17 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1))
            if (r17 != 0) goto L_0x00f8
            if (r4 == 0) goto L_0x00f5
            int r2 = -r4
            long r2 = (long) r2
            goto L_0x00f8
        L_0x00f5:
            if (r1 == 0) goto L_0x00f8
            long r2 = (long) r1
        L_0x00f8:
            r15 = 0
            if (r1 <= 0) goto L_0x0131
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            boolean r10 = r10.from_scheduled
            if (r10 == 0) goto L_0x011c
            r17 = r13
            long r12 = (long) r9
            int r18 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r18 != 0) goto L_0x0112
            r12 = 2131625707(0x7f0e06eb, float:1.887863E38)
            java.lang.String r13 = "MessageScheduledReminderNotification"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            goto L_0x0146
        L_0x0112:
            r12 = 2131625949(0x7f0e07dd, float:1.887912E38)
            java.lang.String r13 = "NotificationMessageScheduledName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            goto L_0x0146
        L_0x011c:
            r17 = r13
            org.telegram.messenger.MessagesController r12 = r19.getMessagesController()
            java.lang.Integer r13 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r12 = r12.getUser(r13)
            if (r12 == 0) goto L_0x0145
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r12)
            goto L_0x0146
        L_0x0131:
            r17 = r13
            org.telegram.messenger.MessagesController r12 = r19.getMessagesController()
            int r13 = -r1
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r12 = r12.getChat(r13)
            if (r12 == 0) goto L_0x0145
            java.lang.String r12 = r12.title
            goto L_0x0146
        L_0x0145:
            r12 = r15
        L_0x0146:
            if (r12 != 0) goto L_0x0149
            return r15
        L_0x0149:
            if (r4 == 0) goto L_0x015a
            org.telegram.messenger.MessagesController r13 = r19.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r10 = r13.getChat(r10)
            if (r10 != 0) goto L_0x015b
            return r15
        L_0x015a:
            r10 = r15
        L_0x015b:
            int r3 = (int) r2
            if (r3 != 0) goto L_0x0169
            r0 = 2131627463(0x7f0e0dc7, float:1.8882191E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x134a
        L_0x0169:
            java.lang.String r2 = "🎬 "
            java.lang.String r3 = "📎 "
            java.lang.String r13 = "📹 "
            java.lang.String r15 = "🖼 "
            java.lang.String r14 = "NotificationMessageText"
            r6 = 3
            if (r4 != 0) goto L_0x054a
            if (r1 == 0) goto L_0x054a
            if (r8 == 0) goto L_0x0536
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 == 0) goto L_0x0536
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r4 == 0) goto L_0x023b
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r2 != 0) goto L_0x022b
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r2 == 0) goto L_0x0194
            goto L_0x022b
        L_0x0194:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r2 == 0) goto L_0x01a8
            r0 = 2131625891(0x7f0e07a3, float:1.8879003E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x01a8:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r2 == 0) goto L_0x020b
            r1 = 2131627529(0x7f0e0e09, float:1.8882325E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterYear
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r7 = (long) r4
            r9 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 * r9
            java.lang.String r2 = r2.format((long) r7)
            r4 = 0
            r3[r4] = r2
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterDay
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r7 = (long) r4
            long r7 = r7 * r9
            java.lang.String r2 = r2.format((long) r7)
            r3[r5] = r2
            java.lang.String r2 = "formatDateAtTime"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            r2 = 2131625955(0x7f0e07e3, float:1.8879133E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r4 = r19.getUserConfig()
            org.telegram.tgnet.TLRPC$User r4 = r4.getCurrentUser()
            java.lang.String r4 = r4.first_name
            r7 = 0
            r3[r7] = r4
            r3[r5] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r1 = r0.title
            r4 = 2
            r3[r4] = r1
            java.lang.String r0 = r0.address
            r3[r6] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x134a
        L_0x020b:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 != 0) goto L_0x0223
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r2 == 0) goto L_0x0214
            goto L_0x0223
        L_0x0214:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x1348
            r0 = 2131624472(0x7f0e0218, float:1.8876125E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x134a
        L_0x0223:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x134a
        L_0x022b:
            r0 = 2131625890(0x7f0e07a2, float:1.8879E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x023b:
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x0284
            if (r21 != 0) goto L_0x0274
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0264
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x134a
        L_0x0264:
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r4 = r17
            r1 = 2131625941(0x7f0e07d5, float:1.8879104E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x134a
        L_0x0274:
            r4 = r17
            r1 = 2131625941(0x7f0e07d5, float:1.8879104E38)
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x134a
        L_0x0284:
            r4 = r17
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r1.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x02eb
            if (r21 != 0) goto L_0x02c4
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x02c4
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x02c4
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x134a
        L_0x02c4:
            r2 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x02dc
            r0 = 2131625946(0x7f0e07da, float:1.8879114E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageSDPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x02dc:
            r0 = 2131625942(0x7f0e07d6, float:1.8879106E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x02eb:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x0350
            if (r21 != 0) goto L_0x0329
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0329
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0329
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r6 = 0
            r1[r6] = r12
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r6] = r5
            goto L_0x134a
        L_0x0329:
            r6 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0341
            r0 = 2131625947(0x7f0e07db, float:1.8879116E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageSDVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0341:
            r0 = 2131625953(0x7f0e07e1, float:1.8879129E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0350:
            r6 = 0
            boolean r1 = r20.isGame()
            if (r1 == 0) goto L_0x0371
            r1 = 2131625915(0x7f0e07bb, float:1.8879051E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            java.lang.String r0 = r0.title
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageGame"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x134a
        L_0x0371:
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x0387
            r0 = 2131625910(0x7f0e07b6, float:1.8879041E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r6 = 0
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0387:
            r6 = 0
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x039d
            r0 = 2131625945(0x7f0e07d9, float:1.8879112E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x039d:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x03b2
            r0 = 2131625940(0x7f0e07d4, float:1.8879102E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x03b2:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x03d6
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131625911(0x7f0e07b7, float:1.8879043E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x134a
        L_0x03d6:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x040c
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x03f6
            r1 = 2131625944(0x7f0e07d8, float:1.887911E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x03f6:
            r2 = 2
            r3 = 0
            r1 = 2131625943(0x7f0e07d7, float:1.8879108E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
        L_0x0409:
            r15 = r0
            goto L_0x134a
        L_0x040c:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r6 != 0) goto L_0x0526
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r6 == 0) goto L_0x0416
            goto L_0x0526
        L_0x0416:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x042a
            r0 = 2131625938(0x7f0e07d2, float:1.8879098E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x042a:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x04fa
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x04d2
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x043c
            goto L_0x04d2
        L_0x043c:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x048a
            if (r21 != 0) goto L_0x047a
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x047a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x047a
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r12
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r3] = r5
            goto L_0x134a
        L_0x047a:
            r3 = 0
            r0 = 2131625917(0x7f0e07bd, float:1.8879055E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r12
            java.lang.String r2 = "NotificationMessageGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x048a:
            if (r21 != 0) goto L_0x04c2
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x04c2
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x04c2
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x134a
        L_0x04c2:
            r2 = 0
            r0 = 2131625912(0x7f0e07b8, float:1.8879045E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x04d2:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x04eb
            r1 = 2131625951(0x7f0e07df, float:1.8879124E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r12
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0409
        L_0x04eb:
            r0 = 2131625950(0x7f0e07de, float:1.8879122E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x04fa:
            r2 = 0
            if (r21 != 0) goto L_0x0519
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0519
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.CharSequence r0 = r0.messageText
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x134a
        L_0x0519:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r1 = 2131625941(0x7f0e07d5, float:1.8879104E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x134a
        L_0x0526:
            r2 = 0
            r0 = 2131625939(0x7f0e07d3, float:1.88791E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0536:
            r4 = r17
            r2 = 0
            if (r23 == 0) goto L_0x053d
            r23[r2] = r2
        L_0x053d:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r1 = 2131625941(0x7f0e07d5, float:1.8879104E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x134a
        L_0x054a:
            if (r4 == 0) goto L_0x1348
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r4 == 0) goto L_0x0558
            boolean r4 = r10.megagroup
            if (r4 != 0) goto L_0x0558
            r4 = 1
            goto L_0x0559
        L_0x0558:
            r4 = 0
        L_0x0559:
            if (r8 == 0) goto L_0x131a
            if (r4 != 0) goto L_0x0565
            java.lang.String r8 = "EnablePreviewGroup"
            boolean r8 = r7.getBoolean(r8, r5)
            if (r8 != 0) goto L_0x056f
        L_0x0565:
            if (r4 == 0) goto L_0x131a
            java.lang.String r4 = "EnablePreviewChannel"
            boolean r4 = r7.getBoolean(r4, r5)
            if (r4 == 0) goto L_0x131a
        L_0x056f:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r7 == 0) goto L_0x0d77
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r7 == 0) goto L_0x0687
            int r2 = r4.user_id
            if (r2 != 0) goto L_0x0598
            java.util.ArrayList<java.lang.Integer> r3 = r4.users
            int r3 = r3.size()
            if (r3 != r5) goto L_0x0598
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
        L_0x0598:
            if (r2 == 0) goto L_0x062f
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x05bb
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x05bb
            r0 = 2131624527(0x7f0e024f, float:1.8876236E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x05bb:
            r3 = 2
            r4 = 0
            if (r2 != r9) goto L_0x05d2
            r0 = 2131625907(0x7f0e07b3, float:1.8879035E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x05d2:
            org.telegram.messenger.MessagesController r0 = r19.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x05e2
            r2 = 0
            return r2
        L_0x05e2:
            int r2 = r0.id
            if (r1 != r2) goto L_0x0614
            boolean r0 = r10.megagroup
            if (r0 == 0) goto L_0x05ff
            r0 = 2131625896(0x7f0e07a8, float:1.8879013E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x05ff:
            r1 = 2
            r2 = 0
            r0 = 2131625895(0x7f0e07a7, float:1.887901E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0614:
            r2 = 0
            r1 = 2131625894(0x7f0e07a6, float:1.8879009E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r2] = r12
            java.lang.String r2 = r10.title
            r3[r5] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r3[r2] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0409
        L_0x062f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0635:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x066c
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0669
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0666
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0666:
            r1.append(r3)
        L_0x0669:
            int r2 = r2 + 1
            goto L_0x0635
        L_0x066c:
            r0 = 2131625894(0x7f0e07a6, float:1.8879009E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r1 = r1.toString()
            r7 = 2
            r2[r7] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0409
        L_0x0687:
            r7 = 2
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r8 == 0) goto L_0x06a0
            r0 = 2131625908(0x7f0e07b4, float:1.8879037E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r8 = 0
            r1[r8] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x06a0:
            r8 = 0
            boolean r11 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r11 == 0) goto L_0x06b8
            r0 = 2131625892(0x7f0e07a4, float:1.8879005E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r8] = r12
            java.lang.String r2 = r4.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x06b8:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r7 != 0) goto L_0x0d44
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r7 == 0) goto L_0x06c2
            goto L_0x0d44
        L_0x06c2:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r7 == 0) goto L_0x0727
            int r2 = r4.user_id
            if (r2 != r9) goto L_0x06df
            r0 = 2131625901(0x7f0e07ad, float:1.8879023E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x06df:
            r3 = 2
            r4 = 0
            if (r2 != r1) goto L_0x06f6
            r0 = 2131625902(0x7f0e07ae, float:1.8879025E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x06f6:
            org.telegram.messenger.MessagesController r1 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x070c
            r1 = 0
            return r1
        L_0x070c:
            r1 = 2131625900(0x7f0e07ac, float:1.887902E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x134a
        L_0x0727:
            r1 = 0
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r7 == 0) goto L_0x0734
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x134a
        L_0x0734:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r7 == 0) goto L_0x0740
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x134a
        L_0x0740:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r7 == 0) goto L_0x0756
            r0 = 2131624066(0x7f0e0082, float:1.8875301E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r7 = 0
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0756:
            r7 = 0
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r8 == 0) goto L_0x076c
            r0 = 2131624066(0x7f0e0082, float:1.8875301E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r4.title
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x076c:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r7 == 0) goto L_0x0778
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x134a
        L_0x0778:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r7 == 0) goto L_0x0d38
            if (r10 == 0) goto L_0x0a7c
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r1 == 0) goto L_0x0788
            boolean r1 = r10.megagroup
            if (r1 == 0) goto L_0x0a7c
        L_0x0788:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x07a1
            r0 = 2131625870(0x7f0e078e, float:1.887896E38)
            r4 = 2
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r7 = 0
            r1[r7] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x07a1:
            r4 = 2
            r7 = 0
            boolean r8 = r1.isMusic()
            if (r8 == 0) goto L_0x07bc
            r0 = 2131625868(0x7f0e078c, float:1.8878956E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x07bc:
            boolean r4 = r1.isVideo()
            r7 = 2131625884(0x7f0e079c, float:1.8878989E38)
            java.lang.String r8 = "NotificationActionPinnedText"
            if (r4 == 0) goto L_0x0811
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x07fc
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x07fc
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r13)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r3 = 2
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x0409
        L_0x07fc:
            r2 = 0
            r3 = 2
            r0 = 2131625886(0x7f0e079e, float:1.8878993E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0811:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0861
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x084c
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x084c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r2)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r4 = 2
            r1[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x0409
        L_0x084c:
            r2 = 0
            r4 = 2
            r0 = 2131625864(0x7f0e0788, float:1.8878948E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0861:
            r2 = 0
            r4 = 2
            boolean r9 = r1.isVoice()
            if (r9 == 0) goto L_0x087c
            r0 = 2131625888(0x7f0e07a0, float:1.8878997E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x087c:
            boolean r9 = r1.isRoundVideo()
            if (r9 == 0) goto L_0x0895
            r0 = 2131625878(0x7f0e0796, float:1.8878976E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0895:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0a4b
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x08a3
            goto L_0x0a4b
        L_0x08a3:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x08f3
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x08de
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08de
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r3 = 2
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x0409
        L_0x08de:
            r2 = 0
            r3 = 2
            r0 = 2131625854(0x7f0e077e, float:1.8878928E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x08f3:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0a36
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x08fd
            goto L_0x0a36
        L_0x08fd:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0916
            r0 = 2131625862(0x7f0e0786, float:1.8878944E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0916:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x093f
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131625852(0x7f0e077c, float:1.8878924E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x093f:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x097d
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0964
            r1 = 2131625876(0x7f0e0794, float:1.8878972E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x0964:
            r3 = 0
            r4 = 2
            r1 = 2131625874(0x7f0e0792, float:1.8878968E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x097d:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x09c9
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x09b4
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09b4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r3 = 2
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x0409
        L_0x09b4:
            r2 = 0
            r3 = 2
            r0 = 2131625872(0x7f0e0790, float:1.8878964E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x09c9:
            r2 = 0
            r3 = 2
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x09e2
            r0 = 2131625856(0x7f0e0780, float:1.8878932E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x09e2:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0a21
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0a21
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0a0f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0a10
        L_0x0a0f:
            r3 = 0
        L_0x0a10:
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r12
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r2 = 2
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x0409
        L_0x0a21:
            r2 = 2
            r3 = 0
            r0 = 2131625870(0x7f0e078e, float:1.887896E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0a36:
            r2 = 2
            r3 = 0
            r0 = 2131625860(0x7f0e0784, float:1.887894E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0a4b:
            r3 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0a68
            r1 = 2131625882(0x7f0e079a, float:1.8878984E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x0a68:
            r4 = 2
            r0 = 2131625880(0x7f0e0798, float:1.887898E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0a7c:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x0a92
            r0 = 2131625871(0x7f0e078f, float:1.8878962E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r4 = 0
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0a92:
            r4 = 0
            boolean r6 = r1.isMusic()
            if (r6 == 0) goto L_0x0aaa
            r0 = 2131625869(0x7f0e078d, float:1.8878958E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0aaa:
            boolean r4 = r1.isVideo()
            r6 = 2131625885(0x7f0e079d, float:1.887899E38)
            java.lang.String r7 = "NotificationActionPinnedTextChannel"
            if (r4 == 0) goto L_0x0afa
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x0ae8
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0ae8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r13)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            goto L_0x0409
        L_0x0ae8:
            r3 = 0
            r0 = 2131625887(0x7f0e079f, float:1.8878995E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0afa:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0b45
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0b33
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0b33
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r2)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r10.title
            r4 = 0
            r1[r4] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            goto L_0x0409
        L_0x0b33:
            r4 = 0
            r0 = 2131625865(0x7f0e0789, float:1.887895E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0b45:
            r4 = 0
            boolean r2 = r1.isVoice()
            if (r2 == 0) goto L_0x0b5d
            r0 = 2131625889(0x7f0e07a1, float:1.8878999E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0b5d:
            boolean r2 = r1.isRoundVideo()
            if (r2 == 0) goto L_0x0b74
            r0 = 2131625879(0x7f0e0797, float:1.8878978E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0b74:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0d0c
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x0b82
            goto L_0x0d0c
        L_0x0b82:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0bcd
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x0bbb
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0bbb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            goto L_0x0409
        L_0x0bbb:
            r3 = 0
            r0 = 2131625855(0x7f0e077f, float:1.887893E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0bcd:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0cfa
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x0bd7
            goto L_0x0cfa
        L_0x0bd7:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0bed
            r0 = 2131625863(0x7f0e0787, float:1.8878946E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0bed:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131625853(0x7f0e077d, float:1.8878926E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x0CLASSNAME:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0c4e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0CLASSNAME
            r1 = 2131625877(0x7f0e0795, float:1.8878974E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x0CLASSNAME:
            r2 = 2
            r4 = 0
            r1 = 2131625875(0x7f0e0793, float:1.887897E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x0c4e:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
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
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            goto L_0x0409
        L_0x0CLASSNAME:
            r3 = 0
            r0 = 2131625873(0x7f0e0791, float:1.8878966E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0CLASSNAME:
            r3 = 0
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0cab
            r0 = 2131625857(0x7f0e0781, float:1.8878934E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0cab:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0ce8
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0ce8
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0cd8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0cd9
        L_0x0cd8:
            r3 = 0
        L_0x0cd9:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            goto L_0x0409
        L_0x0ce8:
            r3 = 0
            r0 = 2131625871(0x7f0e078f, float:1.8878962E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0cfa:
            r3 = 0
            r0 = 2131625861(0x7f0e0785, float:1.8878942E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0d0c:
            r3 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0d27
            r1 = 2131625883(0x7f0e079b, float:1.8878987E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r10.title
            r2[r3] = r4
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x0d27:
            r0 = 2131625881(0x7f0e0799, float:1.8878982E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0d38:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 == 0) goto L_0x1349
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x134a
        L_0x0d44:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x0d62
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x0d62
            r0 = 2131624591(0x7f0e028f, float:1.8876366E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0d62:
            r3 = 0
            r0 = 2131625893(0x7f0e07a5, float:1.8879007E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0d77:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r1 == 0) goto L_0x101c
            boolean r1 = r10.megagroup
            if (r1 != 0) goto L_0x101c
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x0dba
            if (r21 != 0) goto L_0x0daa
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0daa
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x134a
        L_0x0daa:
            r2 = 0
            r0 = 2131624576(0x7f0e0280, float:1.8876336E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0dba:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0e08
            if (r21 != 0) goto L_0x0df8
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0df8
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0df8
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x134a
        L_0x0df8:
            r2 = 0
            r0 = 2131624577(0x7f0e0281, float:1.8876338E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0e08:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x0e56
            if (r21 != 0) goto L_0x0e46
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0e46
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e46
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r4 = 0
            r1[r4] = r12
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r4] = r5
            goto L_0x134a
        L_0x0e46:
            r4 = 0
            r0 = 2131624583(0x7f0e0287, float:1.887635E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0e56:
            r4 = 0
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x0e6c
            r0 = 2131624568(0x7f0e0278, float:1.887632E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0e6c:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x0e81
            r0 = 2131624580(0x7f0e0284, float:1.8876344E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0e81:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x0e96
            r0 = 2131624575(0x7f0e027f, float:1.8876334E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0e96:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r6 == 0) goto L_0x0eba
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131624569(0x7f0e0279, float:1.8876321E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "ChannelMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x134a
        L_0x0eba:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0ef0
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0edb
            r1 = 2131624579(0x7f0e0283, float:1.8876342E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x0edb:
            r2 = 2
            r3 = 0
            r1 = 2131624578(0x7f0e0282, float:1.887634E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x0ef0:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x100c
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x0efa
            goto L_0x100c
        L_0x0efa:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x0f0e
            r0 = 2131624573(0x7f0e027d, float:1.887633E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0f0e:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x0fde
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x0fb6
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x0var_
            goto L_0x0fb6
        L_0x0var_:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x0f6e
            if (r21 != 0) goto L_0x0f5e
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x0f5e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0f5e
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r12
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r3] = r5
            goto L_0x134a
        L_0x0f5e:
            r3 = 0
            r0 = 2131624572(0x7f0e027c, float:1.8876328E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r12
            java.lang.String r2 = "ChannelMessageGIF"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0f6e:
            if (r21 != 0) goto L_0x0fa6
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0fa6
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0fa6
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x134a
        L_0x0fa6:
            r2 = 0
            r0 = 2131624570(0x7f0e027a, float:1.8876323E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x0fb6:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x0fcf
            r1 = 2131624582(0x7f0e0286, float:1.8876348E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r12
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0409
        L_0x0fcf:
            r0 = 2131624581(0x7f0e0285, float:1.8876346E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x0fde:
            r2 = 0
            if (r21 != 0) goto L_0x0ffd
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ffd
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.CharSequence r0 = r0.messageText
            r1[r5] = r0
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x134a
        L_0x0ffd:
            r0 = 2131624576(0x7f0e0280, float:1.8876336E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x100c:
            r2 = 0
            r0 = 2131624574(0x7f0e027e, float:1.8876332E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x101c:
            boolean r1 = r20.isMediaEmpty()
            r4 = 2131625935(0x7f0e07cf, float:1.8879092E38)
            java.lang.String r7 = "NotificationMessageGroupText"
            if (r1 == 0) goto L_0x105c
            if (r21 != 0) goto L_0x1049
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1049
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3 = 2
            r1[r3] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            goto L_0x134a
        L_0x1049:
            r2 = 0
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131625928(0x7f0e07c8, float:1.8879078E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x134a
        L_0x105c:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x10ae
            if (r21 != 0) goto L_0x1099
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x1099
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1099
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
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
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            goto L_0x134a
        L_0x1099:
            r2 = 2
            r0 = 2131625929(0x7f0e07c9, float:1.887908E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x10ae:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x1100
            if (r21 != 0) goto L_0x10eb
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x10eb
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x10eb
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r8 = 2
            r1[r8] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            goto L_0x134a
        L_0x10eb:
            r8 = 2
            r0 = 2131625936(0x7f0e07d0, float:1.8879094E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r9 = 0
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = " "
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x1100:
            r8 = 2
            r9 = 0
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x111b
            r0 = 2131625918(0x7f0e07be, float:1.8879058E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x111b:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x1134
            r0 = 2131625932(0x7f0e07cc, float:1.8879086E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x1134:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x114d
            r0 = 2131625927(0x7f0e07c7, float:1.8879076E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x114d:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x1176
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131625919(0x7f0e07bf, float:1.887906E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r3 = 2
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x134a
        L_0x1176:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x11b4
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x119b
            r1 = 2131625931(0x7f0e07cb, float:1.8879084E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x119b:
            r3 = 0
            r4 = 2
            r1 = 2131625930(0x7f0e07ca, float:1.8879082E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0409
        L_0x11b4:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x11d3
            r0 = 2131625921(0x7f0e07c1, float:1.8879064E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            org.telegram.tgnet.TLRPC$TL_game r1 = r1.game
            java.lang.String r1 = r1.title
            r3 = 2
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupGame"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x134a
        L_0x11d3:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x1306
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x11dd
            goto L_0x1306
        L_0x11dd:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x11f6
            r0 = 2131625925(0x7f0e07c5, float:1.8879072E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x11f6:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x12d7
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x12a6
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x1208
            goto L_0x12a6
        L_0x1208:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x125a
            if (r21 != 0) goto L_0x1245
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x1245
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1245
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r3 = 0
            r1[r3] = r12
            java.lang.String r3 = r10.title
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
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            goto L_0x134a
        L_0x1245:
            r2 = 2
            r0 = 2131625923(0x7f0e07c3, float:1.8879068E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x125a:
            if (r21 != 0) goto L_0x1291
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x1291
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1291
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
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
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            goto L_0x134a
        L_0x1291:
            r2 = 2
            r0 = 2131625920(0x7f0e07c0, float:1.8879062E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x12a6:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x12c3
            r1 = 2131625934(0x7f0e07ce, float:1.887909E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r2] = r12
            java.lang.String r2 = r10.title
            r3[r5] = r2
            r4 = 2
            r3[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0409
        L_0x12c3:
            r4 = 2
            r0 = 2131625933(0x7f0e07cd, float:1.8879088E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0409
        L_0x12d7:
            if (r21 != 0) goto L_0x12f4
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x12f4
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.CharSequence r0 = r0.messageText
            r3 = 2
            r1[r3] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            goto L_0x134a
        L_0x12f4:
            r2 = 0
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131625928(0x7f0e07c8, float:1.8879078E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x134a
        L_0x1306:
            r2 = 0
            r3 = 2
            r0 = 2131625926(0x7f0e07c6, float:1.8879074E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x131a:
            r2 = 0
            if (r23 == 0) goto L_0x131f
            r23[r2] = r2
        L_0x131f:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r0 == 0) goto L_0x1337
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x1337
            r0 = 2131624576(0x7f0e0280, float:1.8876336E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x134a
        L_0x1337:
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131625928(0x7f0e07c8, float:1.8879078E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x134a
        L_0x1348:
            r1 = 0
        L_0x1349:
            r15 = r1
        L_0x134a:
            return r15
        L_0x134b:
            r0 = 2131627463(0x7f0e0dc7, float:1.8882191E38)
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
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
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
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener($$Lambda$NotificationsController$NULIntVdHQSUoPd6L0mVTH6J8n0.INSTANCE);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v17, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: java.lang.Object} */
    /* JADX WARNING: type inference failed for: r3v63 */
    /* JADX WARNING: type inference failed for: r3v64 */
    /* JADX WARNING: type inference failed for: r3v91 */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:450|451|452|453) */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0876, code lost:
        if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x0878;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:452:0x0929 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x022c A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x028d A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02d3 A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x02ff A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0300 A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0305 A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0309 A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0320  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x0335  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x035a  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x03d1 A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x03d8 A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x03dd A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x040f  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x04c1 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x04fd A[ADDED_TO_REGION, Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0510 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x0513 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x051d A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x0532 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0533 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0563 A[SYNTHETIC, Splitter:B:307:0x0563] */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x0596 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x05a2 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x05bc A[SYNTHETIC, Splitter:B:323:0x05bc] */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x05d4 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0676 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x06ef A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x07d8 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0822  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x082b  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x086e A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x087f A[ADDED_TO_REGION, Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x093d A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0947 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x094e A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:488:0x09cb A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x0a88 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x0ab9 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x0ad2 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0af0 A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0b0b A[Catch:{ Exception -> 0x0b17 }] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01cf A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0221 A[Catch:{ Exception -> 0x0b19 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r47) {
        /*
            r46 = this;
            r12 = r46
            r13 = r47
            java.lang.String r1 = "color_"
            java.lang.String r2 = "currentAccount"
            org.telegram.messenger.UserConfig r3 = r46.getUserConfig()
            boolean r3 = r3.isClientActivated()
            if (r3 == 0) goto L_0x0b24
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r12.pushMessages
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0b24
            boolean r3 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r3 != 0) goto L_0x0026
            int r3 = r12.currentAccount
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            if (r3 == r4) goto L_0x0026
            goto L_0x0b24
        L_0x0026:
            org.telegram.tgnet.ConnectionsManager r3 = r46.getConnectionsManager()     // Catch:{ Exception -> 0x0b1d }
            r3.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0b1d }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r12.pushMessages     // Catch:{ Exception -> 0x0b1d }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0b1d }
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3     // Catch:{ Exception -> 0x0b1d }
            org.telegram.messenger.AccountInstance r5 = r46.getAccountInstance()     // Catch:{ Exception -> 0x0b1d }
            android.content.SharedPreferences r5 = r5.getNotificationsSettings()     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r6 = "dismissDate"
            int r6 = r5.getInt(r6, r4)     // Catch:{ Exception -> 0x0b1d }
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner     // Catch:{ Exception -> 0x0b1d }
            int r7 = r7.date     // Catch:{ Exception -> 0x0b1d }
            if (r7 > r6) goto L_0x0053
            r46.dismissNotification()     // Catch:{ Exception -> 0x004e }
            return
        L_0x004e:
            r0 = move-exception
            r1 = r0
            r13 = r12
            goto L_0x0b20
        L_0x0053:
            long r7 = r3.getDialogId()     // Catch:{ Exception -> 0x0b1d }
            org.telegram.tgnet.TLRPC$Message r9 = r3.messageOwner     // Catch:{ Exception -> 0x0b1d }
            boolean r9 = r9.mentioned     // Catch:{ Exception -> 0x0b1d }
            if (r9 == 0) goto L_0x0063
            org.telegram.tgnet.TLRPC$Message r9 = r3.messageOwner     // Catch:{ Exception -> 0x004e }
            int r9 = r9.from_id     // Catch:{ Exception -> 0x004e }
            long r9 = (long) r9
            goto L_0x0064
        L_0x0063:
            r9 = r7
        L_0x0064:
            r3.getId()     // Catch:{ Exception -> 0x0b1d }
            org.telegram.tgnet.TLRPC$Message r11 = r3.messageOwner     // Catch:{ Exception -> 0x0b1d }
            org.telegram.tgnet.TLRPC$Peer r11 = r11.to_id     // Catch:{ Exception -> 0x0b1d }
            int r11 = r11.chat_id     // Catch:{ Exception -> 0x0b1d }
            if (r11 == 0) goto L_0x0076
            org.telegram.tgnet.TLRPC$Message r11 = r3.messageOwner     // Catch:{ Exception -> 0x004e }
            org.telegram.tgnet.TLRPC$Peer r11 = r11.to_id     // Catch:{ Exception -> 0x004e }
            int r11 = r11.chat_id     // Catch:{ Exception -> 0x004e }
            goto L_0x007c
        L_0x0076:
            org.telegram.tgnet.TLRPC$Message r11 = r3.messageOwner     // Catch:{ Exception -> 0x0b1d }
            org.telegram.tgnet.TLRPC$Peer r11 = r11.to_id     // Catch:{ Exception -> 0x0b1d }
            int r11 = r11.channel_id     // Catch:{ Exception -> 0x0b1d }
        L_0x007c:
            org.telegram.tgnet.TLRPC$Message r14 = r3.messageOwner     // Catch:{ Exception -> 0x0b1d }
            org.telegram.tgnet.TLRPC$Peer r14 = r14.to_id     // Catch:{ Exception -> 0x0b1d }
            int r14 = r14.user_id     // Catch:{ Exception -> 0x0b1d }
            if (r14 != 0) goto L_0x0089
            org.telegram.tgnet.TLRPC$Message r14 = r3.messageOwner     // Catch:{ Exception -> 0x004e }
            int r14 = r14.from_id     // Catch:{ Exception -> 0x004e }
            goto L_0x0097
        L_0x0089:
            org.telegram.messenger.UserConfig r15 = r46.getUserConfig()     // Catch:{ Exception -> 0x0b1d }
            int r15 = r15.getClientUserId()     // Catch:{ Exception -> 0x0b1d }
            if (r14 != r15) goto L_0x0097
            org.telegram.tgnet.TLRPC$Message r14 = r3.messageOwner     // Catch:{ Exception -> 0x004e }
            int r14 = r14.from_id     // Catch:{ Exception -> 0x004e }
        L_0x0097:
            org.telegram.messenger.MessagesController r15 = r46.getMessagesController()     // Catch:{ Exception -> 0x0b1d }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x0b1d }
            org.telegram.tgnet.TLRPC$User r4 = r15.getUser(r4)     // Catch:{ Exception -> 0x0b1d }
            if (r11 == 0) goto L_0x00ce
            org.telegram.messenger.MessagesController r15 = r46.getMessagesController()     // Catch:{ Exception -> 0x004e }
            r18 = r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x004e }
            org.telegram.tgnet.TLRPC$Chat r6 = r15.getChat(r6)     // Catch:{ Exception -> 0x004e }
            if (r6 != 0) goto L_0x00be
            boolean r15 = r3.isFcmMessage()     // Catch:{ Exception -> 0x004e }
            if (r15 == 0) goto L_0x00be
            boolean r15 = r3.localChannel     // Catch:{ Exception -> 0x004e }
            goto L_0x00cb
        L_0x00be:
            boolean r15 = org.telegram.messenger.ChatObject.isChannel(r6)     // Catch:{ Exception -> 0x004e }
            if (r15 == 0) goto L_0x00ca
            boolean r15 = r6.megagroup     // Catch:{ Exception -> 0x004e }
            if (r15 != 0) goto L_0x00ca
            r15 = 1
            goto L_0x00cb
        L_0x00ca:
            r15 = 0
        L_0x00cb:
            r19 = r3
            goto L_0x00d4
        L_0x00ce:
            r18 = r6
            r19 = r3
            r6 = 0
            r15 = 0
        L_0x00d4:
            int r3 = r12.getNotifyOverride(r5, r9)     // Catch:{ Exception -> 0x0b1d }
            r20 = r2
            r2 = -1
            r21 = r4
            r4 = 2
            if (r3 != r2) goto L_0x00e9
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r15)     // Catch:{ Exception -> 0x004e }
            boolean r3 = r12.isGlobalNotificationsEnabled(r7, r3)     // Catch:{ Exception -> 0x004e }
            goto L_0x00ee
        L_0x00e9:
            if (r3 == r4) goto L_0x00ed
            r3 = 1
            goto L_0x00ee
        L_0x00ed:
            r3 = 0
        L_0x00ee:
            if (r13 == 0) goto L_0x00f5
            if (r3 != 0) goto L_0x00f3
            goto L_0x00f5
        L_0x00f3:
            r3 = 0
            goto L_0x00f6
        L_0x00f5:
            r3 = 1
        L_0x00f6:
            java.lang.String r2 = "custom_"
            r23 = 1000(0x3e8, double:4.94E-321)
            if (r3 != 0) goto L_0x0197
            int r25 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r25 != 0) goto L_0x0197
            if (r6 == 0) goto L_0x0197
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b19 }
            r9.<init>()     // Catch:{ Exception -> 0x0b19 }
            r9.append(r2)     // Catch:{ Exception -> 0x0b19 }
            r9.append(r7)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0b19 }
            r10 = 0
            boolean r9 = r5.getBoolean(r9, r10)     // Catch:{ Exception -> 0x0b19 }
            if (r9 == 0) goto L_0x0145
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x004e }
            r9.<init>()     // Catch:{ Exception -> 0x004e }
            java.lang.String r10 = "smart_max_count_"
            r9.append(r10)     // Catch:{ Exception -> 0x004e }
            r9.append(r7)     // Catch:{ Exception -> 0x004e }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x004e }
            int r9 = r5.getInt(r9, r4)     // Catch:{ Exception -> 0x004e }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x004e }
            r10.<init>()     // Catch:{ Exception -> 0x004e }
            java.lang.String r4 = "smart_delay_"
            r10.append(r4)     // Catch:{ Exception -> 0x004e }
            r10.append(r7)     // Catch:{ Exception -> 0x004e }
            java.lang.String r4 = r10.toString()     // Catch:{ Exception -> 0x004e }
            r10 = 180(0xb4, float:2.52E-43)
            int r10 = r5.getInt(r4, r10)     // Catch:{ Exception -> 0x004e }
            goto L_0x0148
        L_0x0145:
            r10 = 180(0xb4, float:2.52E-43)
            r9 = 2
        L_0x0148:
            if (r9 == 0) goto L_0x0197
            android.util.LongSparseArray<android.graphics.Point> r4 = r12.smartNotificationsDialogs     // Catch:{ Exception -> 0x0b19 }
            java.lang.Object r4 = r4.get(r7)     // Catch:{ Exception -> 0x0b19 }
            android.graphics.Point r4 = (android.graphics.Point) r4     // Catch:{ Exception -> 0x0b19 }
            if (r4 != 0) goto L_0x0167
            android.graphics.Point r4 = new android.graphics.Point     // Catch:{ Exception -> 0x004e }
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x004e }
            long r9 = r9 / r23
            int r10 = (int) r9     // Catch:{ Exception -> 0x004e }
            r9 = 1
            r4.<init>(r9, r10)     // Catch:{ Exception -> 0x004e }
            android.util.LongSparseArray<android.graphics.Point> r9 = r12.smartNotificationsDialogs     // Catch:{ Exception -> 0x004e }
            r9.put(r7, r4)     // Catch:{ Exception -> 0x004e }
            goto L_0x0197
        L_0x0167:
            r25 = r3
            int r3 = r4.y     // Catch:{ Exception -> 0x0b19 }
            int r3 = r3 + r10
            long r12 = (long) r3     // Catch:{ Exception -> 0x0b19 }
            long r26 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0b19 }
            long r26 = r26 / r23
            int r3 = (r12 > r26 ? 1 : (r12 == r26 ? 0 : -1))
            if (r3 >= 0) goto L_0x0183
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0b19 }
            long r9 = r9 / r23
            int r3 = (int) r9     // Catch:{ Exception -> 0x0b19 }
            r9 = 1
            r4.set(r9, r3)     // Catch:{ Exception -> 0x0b19 }
            goto L_0x0199
        L_0x0183:
            int r3 = r4.x     // Catch:{ Exception -> 0x0b19 }
            if (r3 >= r9) goto L_0x0194
            r9 = 1
            int r3 = r3 + r9
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0b19 }
            long r9 = r9 / r23
            int r10 = (int) r9     // Catch:{ Exception -> 0x0b19 }
            r4.set(r3, r10)     // Catch:{ Exception -> 0x0b19 }
            goto L_0x0199
        L_0x0194:
            r25 = 1
            goto L_0x0199
        L_0x0197:
            r25 = r3
        L_0x0199:
            android.net.Uri r3 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r3 = r3.getPath()     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r4 = "EnableInAppSounds"
            r9 = 1
            boolean r4 = r5.getBoolean(r4, r9)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r10 = "EnableInAppVibrate"
            boolean r10 = r5.getBoolean(r10, r9)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r12 = "EnableInAppPreview"
            boolean r12 = r5.getBoolean(r12, r9)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r9 = "EnableInAppPriority"
            r13 = 0
            boolean r9 = r5.getBoolean(r9, r13)     // Catch:{ Exception -> 0x0b19 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b19 }
            r13.<init>()     // Catch:{ Exception -> 0x0b19 }
            r13.append(r2)     // Catch:{ Exception -> 0x0b19 }
            r13.append(r7)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r2 = r13.toString()     // Catch:{ Exception -> 0x0b19 }
            r13 = 0
            boolean r2 = r5.getBoolean(r2, r13)     // Catch:{ Exception -> 0x0b19 }
            if (r2 == 0) goto L_0x0221
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b19 }
            r13.<init>()     // Catch:{ Exception -> 0x0b19 }
            r27 = r12
            java.lang.String r12 = "vibrate_"
            r13.append(r12)     // Catch:{ Exception -> 0x0b19 }
            r13.append(r7)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r12 = r13.toString()     // Catch:{ Exception -> 0x0b19 }
            r13 = 0
            int r12 = r5.getInt(r12, r13)     // Catch:{ Exception -> 0x0b19 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b19 }
            r13.<init>()     // Catch:{ Exception -> 0x0b19 }
            r28 = r12
            java.lang.String r12 = "priority_"
            r13.append(r12)     // Catch:{ Exception -> 0x0b19 }
            r13.append(r7)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r12 = r13.toString()     // Catch:{ Exception -> 0x0b19 }
            r13 = 3
            int r12 = r5.getInt(r12, r13)     // Catch:{ Exception -> 0x0b19 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b19 }
            r13.<init>()     // Catch:{ Exception -> 0x0b19 }
            r29 = r12
            java.lang.String r12 = "sound_path_"
            r13.append(r12)     // Catch:{ Exception -> 0x0b19 }
            r13.append(r7)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r12 = r13.toString()     // Catch:{ Exception -> 0x0b19 }
            r13 = 0
            java.lang.String r12 = r5.getString(r12, r13)     // Catch:{ Exception -> 0x0b19 }
            r13 = r12
            r12 = r28
            r28 = r6
            r6 = r29
            r29 = r9
            goto L_0x022a
        L_0x0221:
            r27 = r12
            r28 = r6
            r29 = r9
            r6 = 3
            r12 = 0
            r13 = 0
        L_0x022a:
            if (r11 == 0) goto L_0x028d
            if (r15 == 0) goto L_0x025d
            if (r13 == 0) goto L_0x0238
            boolean r15 = r13.equals(r3)     // Catch:{ Exception -> 0x0b19 }
            if (r15 == 0) goto L_0x0238
            r13 = 0
            goto L_0x0240
        L_0x0238:
            if (r13 != 0) goto L_0x0240
            java.lang.String r13 = "ChannelSoundPath"
            java.lang.String r13 = r5.getString(r13, r3)     // Catch:{ Exception -> 0x0b19 }
        L_0x0240:
            java.lang.String r15 = "vibrate_channel"
            r9 = 0
            int r15 = r5.getInt(r15, r9)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r9 = "priority_channel"
            r30 = r13
            r13 = 1
            int r9 = r5.getInt(r9, r13)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r13 = "ChannelLed"
            r31 = r9
            r9 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r9 = r5.getInt(r13, r9)     // Catch:{ Exception -> 0x0b19 }
            goto L_0x02d1
        L_0x025d:
            if (r13 == 0) goto L_0x0267
            boolean r9 = r13.equals(r3)     // Catch:{ Exception -> 0x0b19 }
            if (r9 == 0) goto L_0x0267
            r9 = 0
            goto L_0x0271
        L_0x0267:
            if (r13 != 0) goto L_0x0270
            java.lang.String r9 = "GroupSoundPath"
            java.lang.String r9 = r5.getString(r9, r3)     // Catch:{ Exception -> 0x0b19 }
            goto L_0x0271
        L_0x0270:
            r9 = r13
        L_0x0271:
            java.lang.String r13 = "vibrate_group"
            r15 = 0
            int r13 = r5.getInt(r13, r15)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r15 = "priority_group"
            r30 = r9
            r9 = 1
            int r15 = r5.getInt(r15, r9)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r9 = "GroupLed"
            r31 = r13
            r13 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r9 = r5.getInt(r9, r13)     // Catch:{ Exception -> 0x0b19 }
            goto L_0x02be
        L_0x028d:
            if (r14 == 0) goto L_0x02c5
            if (r13 == 0) goto L_0x0299
            boolean r9 = r13.equals(r3)     // Catch:{ Exception -> 0x0b19 }
            if (r9 == 0) goto L_0x0299
            r9 = 0
            goto L_0x02a3
        L_0x0299:
            if (r13 != 0) goto L_0x02a2
            java.lang.String r9 = "GlobalSoundPath"
            java.lang.String r9 = r5.getString(r9, r3)     // Catch:{ Exception -> 0x0b19 }
            goto L_0x02a3
        L_0x02a2:
            r9 = r13
        L_0x02a3:
            java.lang.String r13 = "vibrate_messages"
            r15 = 0
            int r13 = r5.getInt(r13, r15)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r15 = "priority_messages"
            r30 = r9
            r9 = 1
            int r15 = r5.getInt(r15, r9)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r9 = "MessagesLed"
            r31 = r13
            r13 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r9 = r5.getInt(r9, r13)     // Catch:{ Exception -> 0x0b19 }
        L_0x02be:
            r45 = r31
            r31 = r15
            r15 = r45
            goto L_0x02d1
        L_0x02c5:
            r9 = r13
            r13 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r30 = r9
            r9 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r15 = 0
            r31 = 0
        L_0x02d1:
            if (r2 == 0) goto L_0x02fc
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b19 }
            r2.<init>()     // Catch:{ Exception -> 0x0b19 }
            r2.append(r1)     // Catch:{ Exception -> 0x0b19 }
            r2.append(r7)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b19 }
            boolean r2 = r5.contains(r2)     // Catch:{ Exception -> 0x0b19 }
            if (r2 == 0) goto L_0x02fc
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b19 }
            r2.<init>()     // Catch:{ Exception -> 0x0b19 }
            r2.append(r1)     // Catch:{ Exception -> 0x0b19 }
            r2.append(r7)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x0b19 }
            r2 = 0
            int r9 = r5.getInt(r1, r2)     // Catch:{ Exception -> 0x0b19 }
        L_0x02fc:
            r1 = 3
            if (r6 == r1) goto L_0x0300
            goto L_0x0302
        L_0x0300:
            r6 = r31
        L_0x0302:
            r2 = 4
            if (r15 != r2) goto L_0x0309
            r5 = 1
            r13 = 2
            r15 = 0
            goto L_0x030b
        L_0x0309:
            r5 = 0
            r13 = 2
        L_0x030b:
            if (r15 != r13) goto L_0x0312
            r2 = 1
            if (r12 == r2) goto L_0x031b
            if (r12 == r1) goto L_0x031b
        L_0x0312:
            if (r15 == r13) goto L_0x0316
            if (r12 == r13) goto L_0x031b
        L_0x0316:
            if (r12 == 0) goto L_0x031c
            r1 = 4
            if (r12 == r1) goto L_0x031c
        L_0x031b:
            r15 = r12
        L_0x031c:
            boolean r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0b19 }
            if (r1 != 0) goto L_0x0335
            if (r4 != 0) goto L_0x0324
            r30 = 0
        L_0x0324:
            if (r10 != 0) goto L_0x0327
            r15 = 2
        L_0x0327:
            if (r29 != 0) goto L_0x032e
            r2 = r30
            r1 = 2
            r6 = 0
            goto L_0x0338
        L_0x032e:
            r1 = 2
            r2 = r30
            if (r6 != r1) goto L_0x0338
            r6 = 1
            goto L_0x0338
        L_0x0335:
            r1 = 2
            r2 = r30
        L_0x0338:
            if (r5 == 0) goto L_0x034e
            if (r15 == r1) goto L_0x034e
            android.media.AudioManager r1 = audioManager     // Catch:{ Exception -> 0x0349 }
            int r1 = r1.getRingerMode()     // Catch:{ Exception -> 0x0349 }
            if (r1 == 0) goto L_0x034e
            r4 = 1
            if (r1 == r4) goto L_0x034e
            r15 = 2
            goto L_0x034e
        L_0x0349:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0b19 }
        L_0x034e:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r4 = "NoSound"
            r12 = 100
            r10 = 26
            r30 = 0
            if (r1 < r10) goto L_0x03d1
            r1 = 2
            if (r15 != r1) goto L_0x0367
            long[] r10 = new long[r1]     // Catch:{ Exception -> 0x0b19 }
            r1 = 0
            r10[r1] = r30     // Catch:{ Exception -> 0x0b19 }
            r1 = 1
            r10[r1] = r30     // Catch:{ Exception -> 0x0b19 }
            r5 = r10
            goto L_0x0391
        L_0x0367:
            r1 = 1
            if (r15 != r1) goto L_0x0379
            r10 = 4
            long[] r5 = new long[r10]     // Catch:{ Exception -> 0x0b19 }
            r10 = 0
            r5[r10] = r30     // Catch:{ Exception -> 0x0b19 }
            r5[r1] = r12     // Catch:{ Exception -> 0x0b19 }
            r1 = 2
            r5[r1] = r30     // Catch:{ Exception -> 0x0b19 }
            r1 = 3
            r5[r1] = r12     // Catch:{ Exception -> 0x0b19 }
            goto L_0x0391
        L_0x0379:
            if (r15 == 0) goto L_0x038e
            r1 = 4
            if (r15 != r1) goto L_0x037f
            goto L_0x038e
        L_0x037f:
            r1 = 3
            if (r15 != r1) goto L_0x038c
            r1 = 2
            long[] r5 = new long[r1]     // Catch:{ Exception -> 0x0b19 }
            r1 = 0
            r5[r1] = r30     // Catch:{ Exception -> 0x0b19 }
            r1 = 1
            r5[r1] = r23     // Catch:{ Exception -> 0x0b19 }
            goto L_0x0391
        L_0x038c:
            r5 = 0
            goto L_0x0391
        L_0x038e:
            r1 = 0
            long[] r5 = new long[r1]     // Catch:{ Exception -> 0x0b19 }
        L_0x0391:
            if (r2 == 0) goto L_0x03a7
            boolean r1 = r2.equals(r4)     // Catch:{ Exception -> 0x0b19 }
            if (r1 != 0) goto L_0x03a7
            boolean r1 = r2.equals(r3)     // Catch:{ Exception -> 0x0b19 }
            if (r1 == 0) goto L_0x03a2
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b19 }
            goto L_0x03a8
        L_0x03a2:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b19 }
            goto L_0x03a8
        L_0x03a7:
            r1 = 0
        L_0x03a8:
            if (r6 != 0) goto L_0x03b0
            r32 = r1
            r10 = r5
            r33 = 3
            goto L_0x03d6
        L_0x03b0:
            r10 = 1
            if (r6 == r10) goto L_0x03cb
            r10 = 2
            if (r6 != r10) goto L_0x03b7
            goto L_0x03cb
        L_0x03b7:
            r10 = 4
            if (r6 != r10) goto L_0x03c0
            r32 = r1
            r10 = r5
            r33 = 1
            goto L_0x03d6
        L_0x03c0:
            r10 = 5
            r32 = r1
            if (r6 != r10) goto L_0x03c9
            r10 = r5
            r33 = 2
            goto L_0x03d6
        L_0x03c9:
            r10 = r5
            goto L_0x03d4
        L_0x03cb:
            r32 = r1
            r10 = r5
            r33 = 4
            goto L_0x03d6
        L_0x03d1:
            r10 = 0
            r32 = 0
        L_0x03d4:
            r33 = 0
        L_0x03d6:
            if (r25 == 0) goto L_0x03dd
            r1 = 0
            r2 = 0
            r6 = 0
            r15 = 0
            goto L_0x03df
        L_0x03dd:
            r1 = r6
            r6 = r9
        L_0x03df:
            android.content.Intent r5 = new android.content.Intent     // Catch:{ Exception -> 0x0b19 }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b19 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r12 = org.telegram.ui.LaunchActivity.class
            r5.<init>(r9, r12)     // Catch:{ Exception -> 0x0b19 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b19 }
            r9.<init>()     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r12 = "com.tmessages.openchat"
            r9.append(r12)     // Catch:{ Exception -> 0x0b19 }
            double r12 = java.lang.Math.random()     // Catch:{ Exception -> 0x0b19 }
            r9.append(r12)     // Catch:{ Exception -> 0x0b19 }
            r12 = 2147483647(0x7fffffff, float:NaN)
            r9.append(r12)     // Catch:{ Exception -> 0x0b19 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0b19 }
            r5.setAction(r9)     // Catch:{ Exception -> 0x0b19 }
            r9 = 32768(0x8000, float:4.5918E-41)
            r5.setFlags(r9)     // Catch:{ Exception -> 0x0b19 }
            int r9 = (int) r7
            if (r9 == 0) goto L_0x04c1
            r13 = r46
            android.util.LongSparseArray<java.lang.Integer> r12 = r13.pushDialogs     // Catch:{ Exception -> 0x0b17 }
            int r12 = r12.size()     // Catch:{ Exception -> 0x0b17 }
            r34 = r10
            r10 = 1
            if (r12 != r10) goto L_0x042b
            if (r11 == 0) goto L_0x0424
            java.lang.String r10 = "chatId"
            r5.putExtra(r10, r11)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x042b
        L_0x0424:
            if (r14 == 0) goto L_0x042b
            java.lang.String r10 = "userId"
            r5.putExtra(r10, r14)     // Catch:{ Exception -> 0x0b17 }
        L_0x042b:
            boolean r10 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b17 }
            if (r10 != 0) goto L_0x04b6
            boolean r10 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b17 }
            if (r10 == 0) goto L_0x0437
            goto L_0x04b6
        L_0x0437:
            android.util.LongSparseArray<java.lang.Integer> r10 = r13.pushDialogs     // Catch:{ Exception -> 0x0b17 }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0b17 }
            r12 = 1
            if (r10 != r12) goto L_0x04af
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r12 = 28
            if (r10 >= r12) goto L_0x04af
            if (r28 == 0) goto L_0x047f
            r10 = r28
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r10.photo     // Catch:{ Exception -> 0x0b17 }
            if (r12 == 0) goto L_0x0475
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r10.photo     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ Exception -> 0x0b17 }
            if (r12 == 0) goto L_0x0475
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r10.photo     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ Exception -> 0x0b17 }
            r28 = r15
            long r14 = r12.volume_id     // Catch:{ Exception -> 0x0b17 }
            int r12 = (r14 > r30 ? 1 : (r14 == r30 ? 0 : -1))
            if (r12 == 0) goto L_0x0477
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r10.photo     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ Exception -> 0x0b17 }
            int r12 = r12.local_id     // Catch:{ Exception -> 0x0b17 }
            if (r12 == 0) goto L_0x0477
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r10.photo     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ Exception -> 0x0b17 }
            r35 = r4
            r14 = r12
            r12 = r21
            r21 = r3
            goto L_0x04e9
        L_0x0475:
            r28 = r15
        L_0x0477:
            r35 = r4
            r12 = r21
            r21 = r3
            goto L_0x04e8
        L_0x047f:
            r10 = r28
            r28 = r15
            if (r21 == 0) goto L_0x04ac
            r12 = r21
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo     // Catch:{ Exception -> 0x0b17 }
            if (r14 == 0) goto L_0x04bc
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ Exception -> 0x0b17 }
            if (r14 == 0) goto L_0x04bc
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ Exception -> 0x0b17 }
            long r14 = r14.volume_id     // Catch:{ Exception -> 0x0b17 }
            int r21 = (r14 > r30 ? 1 : (r14 == r30 ? 0 : -1))
            if (r21 == 0) goto L_0x04bc
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ Exception -> 0x0b17 }
            int r14 = r14.local_id     // Catch:{ Exception -> 0x0b17 }
            if (r14 == 0) goto L_0x04bc
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ Exception -> 0x0b17 }
            r21 = r3
            r35 = r4
            goto L_0x04e9
        L_0x04ac:
            r12 = r21
            goto L_0x04bc
        L_0x04af:
            r12 = r21
            r10 = r28
            r28 = r15
            goto L_0x04bc
        L_0x04b6:
            r12 = r21
            r10 = r28
            r28 = r15
        L_0x04bc:
            r21 = r3
            r35 = r4
            goto L_0x04e8
        L_0x04c1:
            r13 = r46
            r34 = r10
            r12 = r21
            r10 = r28
            r28 = r15
            android.util.LongSparseArray<java.lang.Integer> r14 = r13.pushDialogs     // Catch:{ Exception -> 0x0b17 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0b17 }
            r15 = 1
            if (r14 != r15) goto L_0x04bc
            long r14 = globalSecretChatId     // Catch:{ Exception -> 0x0b17 }
            int r21 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
            if (r21 == 0) goto L_0x04bc
            java.lang.String r14 = "encId"
            r21 = r3
            r35 = r4
            r15 = 32
            long r3 = r7 >> r15
            int r4 = (int) r3     // Catch:{ Exception -> 0x0b17 }
            r5.putExtra(r14, r4)     // Catch:{ Exception -> 0x0b17 }
        L_0x04e8:
            r14 = 0
        L_0x04e9:
            int r3 = r13.currentAccount     // Catch:{ Exception -> 0x0b17 }
            r4 = r20
            r5.putExtra(r4, r3)     // Catch:{ Exception -> 0x0b17 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b17 }
            r15 = 1073741824(0x40000000, float:2.0)
            r36 = r7
            r7 = 0
            android.app.PendingIntent r3 = android.app.PendingIntent.getActivity(r3, r7, r5, r15)     // Catch:{ Exception -> 0x0b17 }
            if (r11 == 0) goto L_0x04ff
            if (r10 == 0) goto L_0x0501
        L_0x04ff:
            if (r12 != 0) goto L_0x050c
        L_0x0501:
            boolean r5 = r19.isFcmMessage()     // Catch:{ Exception -> 0x0b17 }
            if (r5 == 0) goto L_0x050c
            r5 = r19
            java.lang.String r7 = r5.localName     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0517
        L_0x050c:
            r5 = r19
            if (r10 == 0) goto L_0x0513
            java.lang.String r7 = r10.title     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0517
        L_0x0513:
            java.lang.String r7 = org.telegram.messenger.UserObject.getUserName(r12)     // Catch:{ Exception -> 0x0b17 }
        L_0x0517:
            boolean r8 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b17 }
            if (r8 != 0) goto L_0x0524
            boolean r8 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b17 }
            if (r8 == 0) goto L_0x0522
            goto L_0x0524
        L_0x0522:
            r8 = 0
            goto L_0x0525
        L_0x0524:
            r8 = 1
        L_0x0525:
            if (r9 == 0) goto L_0x0536
            android.util.LongSparseArray<java.lang.Integer> r9 = r13.pushDialogs     // Catch:{ Exception -> 0x0b17 }
            int r9 = r9.size()     // Catch:{ Exception -> 0x0b17 }
            r15 = 1
            if (r9 > r15) goto L_0x0536
            if (r8 == 0) goto L_0x0533
            goto L_0x0536
        L_0x0533:
            r8 = r7
            r9 = 1
            goto L_0x0558
        L_0x0536:
            if (r8 == 0) goto L_0x054e
            if (r11 == 0) goto L_0x0544
            java.lang.String r8 = "NotificationHiddenChatName"
            r9 = 2131625903(0x7f0e07af, float:1.8879027E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0557
        L_0x0544:
            java.lang.String r8 = "NotificationHiddenName"
            r9 = 2131625906(0x7f0e07b2, float:1.8879033E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0557
        L_0x054e:
            java.lang.String r8 = "AppName"
            r9 = 2131624198(0x7f0e0106, float:1.8875569E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0b17 }
        L_0x0557:
            r9 = 0
        L_0x0558:
            int r11 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r15 = ""
            r19 = r7
            r7 = 1
            if (r11 <= r7) goto L_0x0596
            android.util.LongSparseArray<java.lang.Integer> r11 = r13.pushDialogs     // Catch:{ Exception -> 0x0b17 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0b17 }
            if (r11 != r7) goto L_0x0578
            org.telegram.messenger.UserConfig r7 = r46.getUserConfig()     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$User r7 = r7.getCurrentUser()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r7 = org.telegram.messenger.UserObject.getFirstName(r7)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0597
        L_0x0578:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r7.<init>()     // Catch:{ Exception -> 0x0b17 }
            org.telegram.messenger.UserConfig r11 = r46.getUserConfig()     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$User r11 = r11.getCurrentUser()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r11)     // Catch:{ Exception -> 0x0b17 }
            r7.append(r11)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r11 = "・"
            r7.append(r11)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0597
        L_0x0596:
            r7 = r15
        L_0x0597:
            android.util.LongSparseArray<java.lang.Integer> r11 = r13.pushDialogs     // Catch:{ Exception -> 0x0b17 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0b17 }
            r20 = r6
            r6 = 1
            if (r11 != r6) goto L_0x05af
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r11 = 23
            if (r6 >= r11) goto L_0x05a9
            goto L_0x05af
        L_0x05a9:
            r40 = r1
            r38 = r2
        L_0x05ad:
            r11 = r7
            goto L_0x060a
        L_0x05af:
            android.util.LongSparseArray<java.lang.Integer> r6 = r13.pushDialogs     // Catch:{ Exception -> 0x0b17 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r11 = "NewMessages"
            r38 = r2
            r2 = 1
            if (r6 != r2) goto L_0x05d4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r2.<init>()     // Catch:{ Exception -> 0x0b17 }
            r2.append(r7)     // Catch:{ Exception -> 0x0b17 }
            int r6 = r13.total_unread_count     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r11, r6)     // Catch:{ Exception -> 0x0b17 }
            r2.append(r6)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r7 = r2.toString()     // Catch:{ Exception -> 0x0b17 }
            r40 = r1
            goto L_0x05ad
        L_0x05d4:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r2.<init>()     // Catch:{ Exception -> 0x0b17 }
            r2.append(r7)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r6 = "NotificationMessagesPeopleDisplayOrder"
            r40 = r1
            r7 = 2
            java.lang.Object[] r1 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0b17 }
            int r7 = r13.total_unread_count     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r11, r7)     // Catch:{ Exception -> 0x0b17 }
            r11 = 0
            r1[r11] = r7     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r7 = "FromChats"
            android.util.LongSparseArray<java.lang.Integer> r11 = r13.pushDialogs     // Catch:{ Exception -> 0x0b17 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11)     // Catch:{ Exception -> 0x0b17 }
            r11 = 1
            r1[r11] = r7     // Catch:{ Exception -> 0x0b17 }
            r7 = 2131625954(0x7f0e07e2, float:1.887913E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r7, r1)     // Catch:{ Exception -> 0x0b17 }
            r2.append(r1)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r7 = r2.toString()     // Catch:{ Exception -> 0x0b17 }
            goto L_0x05ad
        L_0x060a:
            androidx.core.app.NotificationCompat$Builder r7 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0b17 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b17 }
            r7.<init>(r1)     // Catch:{ Exception -> 0x0b17 }
            r7.setContentTitle(r8)     // Catch:{ Exception -> 0x0b17 }
            r1 = 2131165744(0x7var_, float:1.7945714E38)
            r7.setSmallIcon(r1)     // Catch:{ Exception -> 0x0b17 }
            r1 = 1
            r7.setAutoCancel(r1)     // Catch:{ Exception -> 0x0b17 }
            int r1 = r13.total_unread_count     // Catch:{ Exception -> 0x0b17 }
            r7.setNumber(r1)     // Catch:{ Exception -> 0x0b17 }
            r7.setContentIntent(r3)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r1 = r13.notificationGroup     // Catch:{ Exception -> 0x0b17 }
            r7.setGroup(r1)     // Catch:{ Exception -> 0x0b17 }
            r1 = 1
            r7.setGroupSummary(r1)     // Catch:{ Exception -> 0x0b17 }
            r7.setShowWhen(r1)     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner     // Catch:{ Exception -> 0x0b17 }
            int r1 = r1.date     // Catch:{ Exception -> 0x0b17 }
            long r1 = (long) r1     // Catch:{ Exception -> 0x0b17 }
            long r1 = r1 * r23
            r7.setWhen(r1)     // Catch:{ Exception -> 0x0b17 }
            r1 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r7.setColor(r1)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r1 = "msg"
            r7.setCategory(r1)     // Catch:{ Exception -> 0x0b17 }
            if (r10 != 0) goto L_0x066d
            if (r12 == 0) goto L_0x066d
            java.lang.String r1 = r12.phone     // Catch:{ Exception -> 0x0b17 }
            if (r1 == 0) goto L_0x066d
            java.lang.String r1 = r12.phone     // Catch:{ Exception -> 0x0b17 }
            int r1 = r1.length()     // Catch:{ Exception -> 0x0b17 }
            if (r1 <= 0) goto L_0x066d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r1.<init>()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = "tel:+"
            r1.append(r2)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r12.phone     // Catch:{ Exception -> 0x0b17 }
            r1.append(r2)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0b17 }
            r7.addPerson(r1)     // Catch:{ Exception -> 0x0b17 }
        L_0x066d:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r13.pushMessages     // Catch:{ Exception -> 0x0b17 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0b17 }
            r2 = 1
            if (r1 != r2) goto L_0x06ef
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r13.pushMessages     // Catch:{ Exception -> 0x0b17 }
            r3 = 0
            java.lang.Object r1 = r1.get(r3)     // Catch:{ Exception -> 0x0b17 }
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1     // Catch:{ Exception -> 0x0b17 }
            boolean[] r6 = new boolean[r2]     // Catch:{ Exception -> 0x0b17 }
            r2 = 0
            java.lang.String r12 = r13.getStringForMessage(r1, r3, r6, r2)     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner     // Catch:{ Exception -> 0x0b17 }
            boolean r1 = r1.silent     // Catch:{ Exception -> 0x0b17 }
            if (r12 != 0) goto L_0x068d
            return
        L_0x068d:
            if (r9 == 0) goto L_0x06d8
            if (r10 == 0) goto L_0x06a7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r2.<init>()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r3 = " @ "
            r2.append(r3)     // Catch:{ Exception -> 0x0b17 }
            r2.append(r8)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r12.replace(r2, r15)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x06d9
        L_0x06a7:
            r2 = 0
            boolean r3 = r6[r2]     // Catch:{ Exception -> 0x0b17 }
            if (r3 == 0) goto L_0x06c2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r2.<init>()     // Catch:{ Exception -> 0x0b17 }
            r2.append(r8)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r3 = ": "
            r2.append(r3)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r12.replace(r2, r15)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x06d9
        L_0x06c2:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r2.<init>()     // Catch:{ Exception -> 0x0b17 }
            r2.append(r8)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r3 = " "
            r2.append(r3)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r12.replace(r2, r15)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x06d9
        L_0x06d8:
            r2 = r12
        L_0x06d9:
            r7.setContentText(r2)     // Catch:{ Exception -> 0x0b17 }
            androidx.core.app.NotificationCompat$BigTextStyle r3 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0b17 }
            r3.<init>()     // Catch:{ Exception -> 0x0b17 }
            r3.bigText(r2)     // Catch:{ Exception -> 0x0b17 }
            r7.setStyle(r3)     // Catch:{ Exception -> 0x0b17 }
            r43 = r4
            r44 = r5
            r42 = r14
            goto L_0x07af
        L_0x06ef:
            r7.setContentText(r11)     // Catch:{ Exception -> 0x0b17 }
            androidx.core.app.NotificationCompat$InboxStyle r1 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0b17 }
            r1.<init>()     // Catch:{ Exception -> 0x0b17 }
            r1.setBigContentTitle(r8)     // Catch:{ Exception -> 0x0b17 }
            r2 = 10
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r13.pushMessages     // Catch:{ Exception -> 0x0b17 }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0b17 }
            int r2 = java.lang.Math.min(r2, r3)     // Catch:{ Exception -> 0x0b17 }
            r3 = 1
            boolean[] r6 = new boolean[r3]     // Catch:{ Exception -> 0x0b17 }
            r3 = 2
            r12 = 0
            r39 = 0
        L_0x070d:
            if (r12 >= r2) goto L_0x07a0
            r41 = r2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r13.pushMessages     // Catch:{ Exception -> 0x0b17 }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Exception -> 0x0b17 }
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2     // Catch:{ Exception -> 0x0b17 }
            r43 = r4
            r44 = r5
            r42 = r14
            r4 = 0
            r14 = 0
            java.lang.String r5 = r13.getStringForMessage(r2, r4, r6, r14)     // Catch:{ Exception -> 0x0b17 }
            if (r5 == 0) goto L_0x0790
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner     // Catch:{ Exception -> 0x0b17 }
            int r4 = r4.date     // Catch:{ Exception -> 0x0b17 }
            r14 = r18
            if (r4 > r14) goto L_0x0730
            goto L_0x0792
        L_0x0730:
            r4 = 2
            if (r3 != r4) goto L_0x0739
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner     // Catch:{ Exception -> 0x0b17 }
            boolean r3 = r2.silent     // Catch:{ Exception -> 0x0b17 }
            r39 = r5
        L_0x0739:
            android.util.LongSparseArray<java.lang.Integer> r2 = r13.pushDialogs     // Catch:{ Exception -> 0x0b17 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0b17 }
            r4 = 1
            if (r2 != r4) goto L_0x078c
            if (r9 == 0) goto L_0x078c
            if (r10 == 0) goto L_0x075c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r2.<init>()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r4 = " @ "
            r2.append(r4)     // Catch:{ Exception -> 0x0b17 }
            r2.append(r8)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r5 = r5.replace(r2, r15)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x078c
        L_0x075c:
            r2 = 0
            boolean r4 = r6[r2]     // Catch:{ Exception -> 0x0b17 }
            if (r4 == 0) goto L_0x0777
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r2.<init>()     // Catch:{ Exception -> 0x0b17 }
            r2.append(r8)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r4 = ": "
            r2.append(r4)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r5 = r5.replace(r2, r15)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x078c
        L_0x0777:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r2.<init>()     // Catch:{ Exception -> 0x0b17 }
            r2.append(r8)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r4 = " "
            r2.append(r4)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r5 = r5.replace(r2, r15)     // Catch:{ Exception -> 0x0b17 }
        L_0x078c:
            r1.addLine(r5)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0792
        L_0x0790:
            r14 = r18
        L_0x0792:
            int r12 = r12 + 1
            r18 = r14
            r2 = r41
            r14 = r42
            r4 = r43
            r5 = r44
            goto L_0x070d
        L_0x07a0:
            r43 = r4
            r44 = r5
            r42 = r14
            r1.setSummaryText(r11)     // Catch:{ Exception -> 0x0b17 }
            r7.setStyle(r1)     // Catch:{ Exception -> 0x0b17 }
            r1 = r3
            r12 = r39
        L_0x07af:
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0b17 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b17 }
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r4 = org.telegram.messenger.NotificationDismissReceiver.class
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r3 = "messageDate"
            r4 = r44
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner     // Catch:{ Exception -> 0x0b17 }
            int r5 = r5.date     // Catch:{ Exception -> 0x0b17 }
            r2.putExtra(r3, r5)     // Catch:{ Exception -> 0x0b17 }
            int r3 = r13.currentAccount     // Catch:{ Exception -> 0x0b17 }
            r5 = r43
            r2.putExtra(r5, r3)     // Catch:{ Exception -> 0x0b17 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b17 }
            r6 = 134217728(0x8000000, float:3.85186E-34)
            r8 = 1
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r3, r8, r2, r6)     // Catch:{ Exception -> 0x0b17 }
            r7.setDeleteIntent(r2)     // Catch:{ Exception -> 0x0b17 }
            if (r42 == 0) goto L_0x0822
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r3 = "50_50"
            r14 = r42
            r8 = 0
            android.graphics.drawable.BitmapDrawable r2 = r2.getImageFromMemory(r14, r8, r3)     // Catch:{ Exception -> 0x0b17 }
            if (r2 == 0) goto L_0x07ef
            android.graphics.Bitmap r2 = r2.getBitmap()     // Catch:{ Exception -> 0x0b17 }
            r7.setLargeIcon(r2)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0823
        L_0x07ef:
            r2 = 1
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r14, r2)     // Catch:{ all -> 0x0823 }
            boolean r2 = r3.exists()     // Catch:{ all -> 0x0823 }
            if (r2 == 0) goto L_0x0823
            r2 = 1126170624(0x43200000, float:160.0)
            r9 = 1112014848(0x42480000, float:50.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x0823 }
            float r9 = (float) r9     // Catch:{ all -> 0x0823 }
            float r2 = r2 / r9
            android.graphics.BitmapFactory$Options r9 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0823 }
            r9.<init>()     // Catch:{ all -> 0x0823 }
            r10 = 1065353216(0x3var_, float:1.0)
            int r10 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r10 >= 0) goto L_0x0811
            r2 = 1
            goto L_0x0812
        L_0x0811:
            int r2 = (int) r2     // Catch:{ all -> 0x0823 }
        L_0x0812:
            r9.inSampleSize = r2     // Catch:{ all -> 0x0823 }
            java.lang.String r2 = r3.getAbsolutePath()     // Catch:{ all -> 0x0823 }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2, r9)     // Catch:{ all -> 0x0823 }
            if (r2 == 0) goto L_0x0823
            r7.setLargeIcon(r2)     // Catch:{ all -> 0x0823 }
            goto L_0x0823
        L_0x0822:
            r8 = 0
        L_0x0823:
            r14 = r47
            if (r14 == 0) goto L_0x086e
            r2 = 1
            if (r1 != r2) goto L_0x082b
            goto L_0x086e
        L_0x082b:
            if (r40 != 0) goto L_0x083a
            r2 = 0
            r7.setPriority(r2)     // Catch:{ Exception -> 0x0b17 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r3 = 26
            if (r2 < r3) goto L_0x087b
            r2 = 1
            r9 = 3
            goto L_0x087d
        L_0x083a:
            r2 = r40
            r3 = 1
            if (r2 == r3) goto L_0x0861
            r3 = 2
            if (r2 != r3) goto L_0x0843
            goto L_0x0861
        L_0x0843:
            r3 = 4
            if (r2 != r3) goto L_0x0853
            r2 = -2
            r7.setPriority(r2)     // Catch:{ Exception -> 0x0b17 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r3 = 26
            if (r2 < r3) goto L_0x087b
            r2 = 1
            r9 = 1
            goto L_0x087d
        L_0x0853:
            r3 = 5
            if (r2 != r3) goto L_0x087b
            r2 = -1
            r7.setPriority(r2)     // Catch:{ Exception -> 0x0b17 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r3 = 26
            if (r2 < r3) goto L_0x087b
            goto L_0x0878
        L_0x0861:
            r2 = 1
            r7.setPriority(r2)     // Catch:{ Exception -> 0x0b17 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r3 = 26
            if (r2 < r3) goto L_0x087b
            r2 = 1
            r9 = 4
            goto L_0x087d
        L_0x086e:
            r2 = -1
            r7.setPriority(r2)     // Catch:{ Exception -> 0x0b17 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r3 = 26
            if (r2 < r3) goto L_0x087b
        L_0x0878:
            r2 = 1
            r9 = 2
            goto L_0x087d
        L_0x087b:
            r2 = 1
            r9 = 0
        L_0x087d:
            if (r1 == r2) goto L_0x09a0
            if (r25 != 0) goto L_0x09a0
            boolean r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0b17 }
            if (r1 != 0) goto L_0x0887
            if (r27 == 0) goto L_0x08b6
        L_0x0887:
            int r1 = r12.length()     // Catch:{ Exception -> 0x0b17 }
            r2 = 100
            if (r1 <= r2) goto L_0x08b3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b17 }
            r1.<init>()     // Catch:{ Exception -> 0x0b17 }
            r2 = 100
            r3 = 0
            java.lang.String r2 = r12.substring(r3, r2)     // Catch:{ Exception -> 0x0b17 }
            r3 = 10
            r10 = 32
            java.lang.String r2 = r2.replace(r3, r10)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = r2.trim()     // Catch:{ Exception -> 0x0b17 }
            r1.append(r2)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r2 = "..."
            r1.append(r2)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r12 = r1.toString()     // Catch:{ Exception -> 0x0b17 }
        L_0x08b3:
            r7.setTicker(r12)     // Catch:{ Exception -> 0x0b17 }
        L_0x08b6:
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0b17 }
            boolean r1 = r1.isRecordingAudio()     // Catch:{ Exception -> 0x0b17 }
            if (r1 != 0) goto L_0x093a
            if (r38 == 0) goto L_0x093a
            r1 = r35
            r2 = r38
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0b17 }
            if (r1 != 0) goto L_0x093a
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r3 = 26
            if (r1 < r3) goto L_0x08e2
            r1 = r21
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0b17 }
            if (r1 == 0) goto L_0x08dd
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b17 }
            goto L_0x093b
        L_0x08dd:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x093b
        L_0x08e2:
            r1 = r21
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0b17 }
            if (r1 == 0) goto L_0x08f1
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b17 }
            r2 = 5
            r7.setSound(r1, r2)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x093a
        L_0x08f1:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r3 = 24
            if (r1 < r3) goto L_0x0932
            java.lang.String r1 = "file://"
            boolean r1 = r2.startsWith(r1)     // Catch:{ Exception -> 0x0b17 }
            if (r1 == 0) goto L_0x0932
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b17 }
            boolean r1 = org.telegram.messenger.AndroidUtilities.isInternalUri(r1)     // Catch:{ Exception -> 0x0b17 }
            if (r1 != 0) goto L_0x0932
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0929 }
            java.lang.String r3 = "org.telegram.messenger.beta.provider"
            java.io.File r10 = new java.io.File     // Catch:{ Exception -> 0x0929 }
            java.lang.String r12 = "file://"
            java.lang.String r12 = r2.replace(r12, r15)     // Catch:{ Exception -> 0x0929 }
            r10.<init>(r12)     // Catch:{ Exception -> 0x0929 }
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r1, r3, r10)     // Catch:{ Exception -> 0x0929 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0929 }
            java.lang.String r10 = "com.android.systemui"
            r12 = 1
            r3.grantUriPermission(r10, r1, r12)     // Catch:{ Exception -> 0x0929 }
            r3 = 5
            r7.setSound(r1, r3)     // Catch:{ Exception -> 0x0929 }
            goto L_0x093a
        L_0x0929:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b17 }
            r2 = 5
            r7.setSound(r1, r2)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x093a
        L_0x0932:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b17 }
            r2 = 5
            r7.setSound(r1, r2)     // Catch:{ Exception -> 0x0b17 }
        L_0x093a:
            r1 = r8
        L_0x093b:
            if (r20 == 0) goto L_0x0947
            r2 = 1000(0x3e8, float:1.401E-42)
            r3 = 1000(0x3e8, float:1.401E-42)
            r10 = r20
            r7.setLights(r10, r2, r3)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0949
        L_0x0947:
            r10 = r20
        L_0x0949:
            r15 = r28
            r2 = 2
            if (r15 == r2) goto L_0x0991
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0b17 }
            boolean r2 = r2.isRecordingAudio()     // Catch:{ Exception -> 0x0b17 }
            if (r2 == 0) goto L_0x095a
            r2 = 2
            goto L_0x0991
        L_0x095a:
            r2 = 1
            if (r15 != r2) goto L_0x0971
            r3 = 4
            long[] r3 = new long[r3]     // Catch:{ Exception -> 0x0b17 }
            r8 = 0
            r3[r8] = r30     // Catch:{ Exception -> 0x0b17 }
            r20 = 100
            r3[r2] = r20     // Catch:{ Exception -> 0x0b17 }
            r2 = 2
            r3[r2] = r30     // Catch:{ Exception -> 0x0b17 }
            r2 = 3
            r3[r2] = r20     // Catch:{ Exception -> 0x0b17 }
            r7.setVibrate(r3)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x099c
        L_0x0971:
            if (r15 == 0) goto L_0x0989
            r2 = 4
            if (r15 != r2) goto L_0x0977
            goto L_0x0989
        L_0x0977:
            r2 = 3
            if (r15 != r2) goto L_0x0987
            r2 = 2
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b17 }
            r2 = 0
            r3[r2] = r30     // Catch:{ Exception -> 0x0b17 }
            r2 = 1
            r3[r2] = r23     // Catch:{ Exception -> 0x0b17 }
            r7.setVibrate(r3)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x099c
        L_0x0987:
            r12 = r1
            goto L_0x099e
        L_0x0989:
            r2 = 2
            r7.setDefaults(r2)     // Catch:{ Exception -> 0x0b17 }
            r2 = 0
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b17 }
            goto L_0x099c
        L_0x0991:
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b17 }
            r2 = 0
            r3[r2] = r30     // Catch:{ Exception -> 0x0b17 }
            r2 = 1
            r3[r2] = r30     // Catch:{ Exception -> 0x0b17 }
            r7.setVibrate(r3)     // Catch:{ Exception -> 0x0b17 }
        L_0x099c:
            r12 = r1
            r8 = r3
        L_0x099e:
            r1 = 1
            goto L_0x09b0
        L_0x09a0:
            r10 = r20
            r1 = 2
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0b17 }
            r1 = 0
            r2[r1] = r30     // Catch:{ Exception -> 0x0b17 }
            r1 = 1
            r2[r1] = r30     // Catch:{ Exception -> 0x0b17 }
            r7.setVibrate(r2)     // Catch:{ Exception -> 0x0b17 }
            r12 = r8
            r8 = r2
        L_0x09b0:
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b17 }
            if (r2 != 0) goto L_0x0a88
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b17 }
            if (r2 != 0) goto L_0x0a88
            long r2 = r4.getDialogId()     // Catch:{ Exception -> 0x0b17 }
            r16 = 777000(0xbdb28, double:3.83889E-318)
            int r15 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r15 != 0) goto L_0x0a88
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup     // Catch:{ Exception -> 0x0b17 }
            if (r2 == 0) goto L_0x0a88
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup     // Catch:{ Exception -> 0x0b17 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r2 = r2.rows     // Catch:{ Exception -> 0x0b17 }
            int r3 = r2.size()     // Catch:{ Exception -> 0x0b17 }
            r15 = 0
            r16 = 0
        L_0x09d8:
            if (r15 >= r3) goto L_0x0a7f
            java.lang.Object r17 = r2.get(r15)     // Catch:{ Exception -> 0x0b17 }
            r1 = r17
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r1 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r1     // Catch:{ Exception -> 0x0b17 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r6 = r1.buttons     // Catch:{ Exception -> 0x0b17 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0b17 }
            r20 = r2
            r2 = 0
        L_0x09eb:
            if (r2 >= r6) goto L_0x0a63
            r21 = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r3 = r1.buttons     // Catch:{ Exception -> 0x0b17 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0b17 }
            org.telegram.tgnet.TLRPC$KeyboardButton r3 = (org.telegram.tgnet.TLRPC$KeyboardButton) r3     // Catch:{ Exception -> 0x0b17 }
            r22 = r1
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0b17 }
            if (r1 == 0) goto L_0x0a45
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0b17 }
            r23 = r6
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b17 }
            r24 = r11
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r11 = org.telegram.messenger.NotificationCallbackReceiver.class
            r1.<init>(r6, r11)     // Catch:{ Exception -> 0x0b17 }
            int r6 = r13.currentAccount     // Catch:{ Exception -> 0x0b17 }
            r1.putExtra(r5, r6)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r6 = "did"
            r25 = r12
            r11 = r36
            r1.putExtra(r6, r11)     // Catch:{ Exception -> 0x0b17 }
            byte[] r6 = r3.data     // Catch:{ Exception -> 0x0b17 }
            if (r6 == 0) goto L_0x0a23
            java.lang.String r6 = "data"
            byte[] r14 = r3.data     // Catch:{ Exception -> 0x0b17 }
            r1.putExtra(r6, r14)     // Catch:{ Exception -> 0x0b17 }
        L_0x0a23:
            java.lang.String r6 = "mid"
            int r14 = r4.getId()     // Catch:{ Exception -> 0x0b17 }
            r1.putExtra(r6, r14)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r3 = r3.text     // Catch:{ Exception -> 0x0b17 }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b17 }
            int r14 = r13.lastButtonId     // Catch:{ Exception -> 0x0b17 }
            r44 = r4
            int r4 = r14 + 1
            r13.lastButtonId = r4     // Catch:{ Exception -> 0x0b17 }
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r6, r14, r1, r4)     // Catch:{ Exception -> 0x0b17 }
            r4 = 0
            r7.addAction(r4, r3, r1)     // Catch:{ Exception -> 0x0b17 }
            r16 = 1
            goto L_0x0a50
        L_0x0a45:
            r44 = r4
            r23 = r6
            r24 = r11
            r25 = r12
            r11 = r36
            r4 = 0
        L_0x0a50:
            int r2 = r2 + 1
            r14 = r47
            r36 = r11
            r3 = r21
            r1 = r22
            r6 = r23
            r11 = r24
            r12 = r25
            r4 = r44
            goto L_0x09eb
        L_0x0a63:
            r21 = r3
            r44 = r4
            r24 = r11
            r25 = r12
            r11 = r36
            r4 = 0
            int r15 = r15 + 1
            r14 = r47
            r2 = r20
            r11 = r24
            r12 = r25
            r4 = r44
            r1 = 1
            r6 = 134217728(0x8000000, float:3.85186E-34)
            goto L_0x09d8
        L_0x0a7f:
            r24 = r11
            r25 = r12
            r11 = r36
            r4 = r16
            goto L_0x0a8f
        L_0x0a88:
            r24 = r11
            r25 = r12
            r11 = r36
            r4 = 0
        L_0x0a8f:
            if (r4 != 0) goto L_0x0aea
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r2 = 24
            if (r1 >= r2) goto L_0x0aea
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0b17 }
            int r1 = r1.length()     // Catch:{ Exception -> 0x0b17 }
            if (r1 != 0) goto L_0x0aea
            boolean r1 = r46.hasMessagesToReply()     // Catch:{ Exception -> 0x0b17 }
            if (r1 == 0) goto L_0x0aea
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0b17 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b17 }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r3 = org.telegram.messenger.PopupReplyReceiver.class
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x0b17 }
            int r2 = r13.currentAccount     // Catch:{ Exception -> 0x0b17 }
            r1.putExtra(r5, r2)     // Catch:{ Exception -> 0x0b17 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r3 = 19
            if (r2 > r3) goto L_0x0ad2
            r2 = 2131165441(0x7var_, float:1.79451E38)
            java.lang.String r3 = "Reply"
            r4 = 2131626539(0x7f0e0a2b, float:1.8880317E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0b17 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b17 }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5)     // Catch:{ Exception -> 0x0b17 }
            r7.addAction(r2, r3, r1)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0aea
        L_0x0ad2:
            r2 = 2131165440(0x7var_, float:1.7945097E38)
            java.lang.String r3 = "Reply"
            r4 = 2131626539(0x7f0e0a2b, float:1.8880317E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0b17 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b17 }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5)     // Catch:{ Exception -> 0x0b17 }
            r7.addAction(r2, r3, r1)     // Catch:{ Exception -> 0x0b17 }
        L_0x0aea:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b17 }
            r2 = 26
            if (r1 < r2) goto L_0x0b0b
            r1 = r46
            r2 = r11
            r4 = r19
            r5 = r8
            r6 = r10
            r12 = r7
            r7 = r25
            r8 = r9
            r9 = r34
            r10 = r32
            r14 = r24
            r11 = r33
            java.lang.String r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0b17 }
            r12.setChannelId(r1)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0b0e
        L_0x0b0b:
            r12 = r7
            r14 = r24
        L_0x0b0e:
            r1 = r47
            r13.showExtraNotifications(r12, r1, r14)     // Catch:{ Exception -> 0x0b17 }
            r46.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0b23
        L_0x0b17:
            r0 = move-exception
            goto L_0x0b1f
        L_0x0b19:
            r0 = move-exception
            r13 = r46
            goto L_0x0b1f
        L_0x0b1d:
            r0 = move-exception
            r13 = r12
        L_0x0b1f:
            r1 = r0
        L_0x0b20:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0b23:
            return
        L_0x0b24:
            r13 = r12
            r46.dismissNotification()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0ae8: MOVE  (r0v75 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>) = 
          (r57v1 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>)
        
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
    /* JADX WARNING: Removed duplicated region for block: B:125:0x02d9  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02fd  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0310  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0377 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x03c0  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0413  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x042e  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0443  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x046a  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0474 A[SYNTHETIC, Splitter:B:179:0x0474] */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04db  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x04e1  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x04f7 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0510  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x051a  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x052d  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0569  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x059f  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x067a  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x067f  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x06f6  */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x07d8  */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x07f4  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0823  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x0831 A[SYNTHETIC, Splitter:B:367:0x0831] */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x08db  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x08ec  */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x090c  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0968  */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x099b  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x09de  */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x0aa7  */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x0ab2  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0ab9  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0ac9  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0acf  */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x0ad3  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0ad8  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0af3  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0ba6  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0bf7 A[Catch:{ JSONException -> 0x0CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x0CLASSNAME A[Catch:{ JSONException -> 0x0CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x0c2b  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0CLASSNAME A[Catch:{ JSONException -> 0x0CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01ac  */
    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r72, boolean r73, java.lang.String r74) {
        /*
            r71 = this;
            r1 = r71
            android.app.Notification r2 = r72.build()
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
            org.telegram.messenger.AccountInstance r0 = r71.getAccountInstance()
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
            org.telegram.messenger.UserConfig r0 = r71.getUserConfig()
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
            java.lang.String r8 = "id"
            if (r10 >= r13) goto L_0x0CLASSNAME
            java.lang.Object r0 = r3.get(r10)
            java.lang.Long r0 = (java.lang.Long) r0
            r19 = r12
            long r11 = r0.longValue()
            java.lang.Object r0 = r4.get(r11)
            r20 = r4
            r4 = r0
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            java.lang.Object r0 = r4.get(r5)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r5 = r0.getId()
            r22 = r13
            int r13 = (int) r11
            r23 = r10
            r10 = 32
            r24 = r7
            r25 = r8
            long r7 = r11 >> r10
            int r8 = (int) r7
            java.lang.Object r0 = r6.get(r11)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x011a
            if (r13 == 0) goto L_0x0115
            java.lang.Integer r0 = java.lang.Integer.valueOf(r13)
            goto L_0x011d
        L_0x0115:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r8)
            goto L_0x011d
        L_0x011a:
            r6.remove(r11)
        L_0x011d:
            r7 = r0
            if (r9 == 0) goto L_0x0126
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            goto L_0x0127
        L_0x0126:
            r0 = 0
        L_0x0127:
            r10 = 0
            java.lang.Object r27 = r4.get(r10)
            r10 = r27
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            r27 = r0
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            r28 = r6
            int r6 = r0.date
            r29 = r9
            android.util.LongSparseArray r9 = new android.util.LongSparseArray
            r9.<init>()
            r30 = 0
            if (r13 == 0) goto L_0x024f
            r0 = 777000(0xbdb28, float:1.088809E-39)
            if (r13 == r0) goto L_0x014a
            r0 = 1
            goto L_0x014b
        L_0x014a:
            r0 = 0
        L_0x014b:
            if (r13 <= 0) goto L_0x01c4
            r32 = r0
            org.telegram.messenger.MessagesController r0 = r71.getMessagesController()
            r33 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x0186
            boolean r2 = r10.isFcmMessage()
            if (r2 == 0) goto L_0x016a
            java.lang.String r2 = r10.localName
            r34 = r3
            goto L_0x01a9
        L_0x016a:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0182
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found user to show dialog notification "
            r0.append(r2)
            r0.append(r13)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x0182:
            r34 = r3
            goto L_0x0281
        L_0x0186:
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r0)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r10 = r0.photo
            if (r10 == 0) goto L_0x01a3
            org.telegram.tgnet.TLRPC$FileLocation r10 = r10.photo_small
            if (r10 == 0) goto L_0x01a3
            r35 = r2
            r34 = r3
            long r2 = r10.volume_id
            int r36 = (r2 > r30 ? 1 : (r2 == r30 ? 0 : -1))
            if (r36 == 0) goto L_0x01a7
            int r2 = r10.local_id
            if (r2 == 0) goto L_0x01a7
            r2 = r35
            goto L_0x01aa
        L_0x01a3:
            r35 = r2
            r34 = r3
        L_0x01a7:
            r2 = r35
        L_0x01a9:
            r10 = 0
        L_0x01aa:
            if (r13 != r14) goto L_0x01b5
            r2 = 2131625707(0x7f0e06eb, float:1.887863E38)
            java.lang.String r3 = "MessageScheduledReminderNotification"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x01b5:
            r37 = r2
            r36 = r9
            r2 = r27
            r3 = 0
            r27 = 0
            r35 = 0
            r9 = r0
            r0 = r10
            goto L_0x02d1
        L_0x01c4:
            r32 = r0
            r33 = r2
            r34 = r3
            org.telegram.messenger.MessagesController r0 = r71.getMessagesController()
            int r2 = -r13
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            if (r0 != 0) goto L_0x0210
            boolean r2 = r10.isFcmMessage()
            if (r2 == 0) goto L_0x01f6
            boolean r2 = r10.isMegagroup()
            java.lang.String r3 = r10.localName
            boolean r10 = r10.localChannel
            r35 = r2
            r37 = r3
            r36 = r9
            r2 = r27
            r9 = 0
            r3 = r0
            r27 = r10
        L_0x01f3:
            r0 = 0
            goto L_0x02d1
        L_0x01f6:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0281
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found chat to show dialog notification "
            r0.append(r2)
            r0.append(r13)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x0281
        L_0x0210:
            boolean r2 = r0.megagroup
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r3 == 0) goto L_0x021e
            boolean r3 = r0.megagroup
            if (r3 != 0) goto L_0x021e
            r3 = 1
            goto L_0x021f
        L_0x021e:
            r3 = 0
        L_0x021f:
            java.lang.String r10 = r0.title
            r35 = r2
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r0.photo
            if (r2 == 0) goto L_0x0244
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
            if (r2 == 0) goto L_0x0244
            r36 = r9
            r37 = r10
            long r9 = r2.volume_id
            int r38 = (r9 > r30 ? 1 : (r9 == r30 ? 0 : -1))
            if (r38 == 0) goto L_0x0248
            int r9 = r2.local_id
            if (r9 == 0) goto L_0x0248
            r9 = 0
            r69 = r3
            r3 = r0
            r0 = r2
            r2 = r27
            r27 = r69
            goto L_0x02d1
        L_0x0244:
            r36 = r9
            r37 = r10
        L_0x0248:
            r2 = r27
            r9 = 0
            r27 = r3
            r3 = r0
            goto L_0x01f3
        L_0x024f:
            r33 = r2
            r34 = r3
            r36 = r9
            long r2 = globalSecretChatId
            int r0 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x02bb
            org.telegram.messenger.MessagesController r0 = r71.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r2)
            if (r0 != 0) goto L_0x028e
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0281
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found secret chat to show dialog notification "
            r0.append(r2)
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x0281:
            r53 = r14
            r44 = r15
            r5 = r24
            r8 = r29
            r2 = 26
            r3 = 0
            goto L_0x0c4b
        L_0x028e:
            org.telegram.messenger.MessagesController r2 = r71.getMessagesController()
            int r3 = r0.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            if (r2 != 0) goto L_0x02b9
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0281
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "not found secret chat user to show dialog notification "
            r2.append(r3)
            int r0 = r0.user_id
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x0281
        L_0x02b9:
            r0 = r2
            goto L_0x02bc
        L_0x02bb:
            r0 = 0
        L_0x02bc:
            r2 = 2131626664(0x7f0e0aa8, float:1.888057E38)
            java.lang.String r3 = "SecretChatName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r9 = r0
            r37 = r2
            r0 = 0
            r2 = 0
            r3 = 0
            r27 = 0
            r32 = 0
            r35 = 0
        L_0x02d1:
            java.lang.String r10 = "NotificationHiddenChatName"
            r39 = r9
            java.lang.String r9 = "NotificationHiddenName"
            if (r15 == 0) goto L_0x02fd
            if (r13 >= 0) goto L_0x02e5
            r41 = r3
            r3 = 2131625903(0x7f0e07af, float:1.8879027E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r3)
            goto L_0x02ee
        L_0x02e5:
            r41 = r3
            r3 = 2131625906(0x7f0e07b2, float:1.8879033E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r3)
        L_0x02ee:
            r37 = r0
            r32 = r6
            r42 = r9
            r3 = 0
            r6 = 0
            r69 = r37
            r37 = r8
            r8 = r69
            goto L_0x030e
        L_0x02fd:
            r41 = r3
            r3 = r0
            r42 = r9
            r69 = r32
            r32 = r6
            r6 = r69
            r70 = r37
            r37 = r8
            r8 = r70
        L_0x030e:
            if (r3 == 0) goto L_0x0364
            r9 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r9)
            int r9 = android.os.Build.VERSION.SDK_INT
            r43 = r10
            r10 = 28
            if (r9 >= r10) goto L_0x035e
            org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r10 = "50_50"
            r44 = r15
            r15 = 0
            android.graphics.drawable.BitmapDrawable r9 = r9.getImageFromMemory(r3, r15, r10)
            if (r9 == 0) goto L_0x0333
            android.graphics.Bitmap r9 = r9.getBitmap()
        L_0x0330:
            r10 = r9
            r9 = r0
            goto L_0x036b
        L_0x0333:
            boolean r9 = r0.exists()     // Catch:{ all -> 0x0361 }
            if (r9 == 0) goto L_0x035c
            r9 = 1126170624(0x43200000, float:160.0)
            r10 = 1112014848(0x42480000, float:50.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ all -> 0x0361 }
            float r10 = (float) r10     // Catch:{ all -> 0x0361 }
            float r9 = r9 / r10
            android.graphics.BitmapFactory$Options r10 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0361 }
            r10.<init>()     // Catch:{ all -> 0x0361 }
            r17 = 1065353216(0x3var_, float:1.0)
            int r17 = (r9 > r17 ? 1 : (r9 == r17 ? 0 : -1))
            if (r17 >= 0) goto L_0x0350
            r9 = 1
            goto L_0x0351
        L_0x0350:
            int r9 = (int) r9     // Catch:{ all -> 0x0361 }
        L_0x0351:
            r10.inSampleSize = r9     // Catch:{ all -> 0x0361 }
            java.lang.String r9 = r0.getAbsolutePath()     // Catch:{ all -> 0x0361 }
            android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeFile(r9, r10)     // Catch:{ all -> 0x0361 }
            goto L_0x0330
        L_0x035c:
            r9 = r15
            goto L_0x0330
        L_0x035e:
            r44 = r15
            r15 = 0
        L_0x0361:
            r9 = r0
            r10 = r15
            goto L_0x036b
        L_0x0364:
            r43 = r10
            r44 = r15
            r15 = 0
            r9 = r15
            r10 = r9
        L_0x036b:
            java.lang.String r15 = "dialog_id"
            r45 = r9
            java.lang.String r9 = "max_id"
            r46 = r3
            java.lang.String r3 = "currentAccount"
            if (r27 == 0) goto L_0x0379
            if (r35 == 0) goto L_0x03ff
        L_0x0379:
            if (r6 == 0) goto L_0x03ff
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x03ff
            if (r14 == r13) goto L_0x03ff
            android.content.Intent r0 = new android.content.Intent
            r47 = r6
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            r48 = r10
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r10 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r6, r10)
            r0.putExtra(r15, r11)
            r0.putExtra(r9, r5)
            int r6 = r1.currentAccount
            r0.putExtra(r3, r6)
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r10 = r7.intValue()
            r49 = r7
            r7 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r6, r10, r0, r7)
            androidx.core.app.RemoteInput$Builder r6 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r7 = "extra_voice_reply"
            r6.<init>(r7)
            r7 = 2131626539(0x7f0e0a2b, float:1.8880317E38)
            java.lang.String r10 = "Reply"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6.setLabel(r7)
            androidx.core.app.RemoteInput r6 = r6.build()
            if (r13 >= 0) goto L_0x03d2
            r10 = 1
            java.lang.Object[] r7 = new java.lang.Object[r10]
            r10 = 0
            r7[r10] = r8
            java.lang.String r10 = "ReplyToGroup"
            r50 = r5
            r5 = 2131626540(0x7f0e0a2c, float:1.888032E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r10, r5, r7)
            goto L_0x03e3
        L_0x03d2:
            r50 = r5
            r5 = 2131626541(0x7f0e0a2d, float:1.8880321E38)
            r7 = 1
            java.lang.Object[] r10 = new java.lang.Object[r7]
            r7 = 0
            r10[r7] = r8
            java.lang.String r7 = "ReplyToUser"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r10)
        L_0x03e3:
            androidx.core.app.NotificationCompat$Action$Builder r7 = new androidx.core.app.NotificationCompat$Action$Builder
            r10 = 2131165485(0x7var_d, float:1.7945188E38)
            r7.<init>(r10, r5, r0)
            r5 = 1
            r7.setAllowGeneratedReplies(r5)
            r7.setSemanticAction(r5)
            r7.addRemoteInput(r6)
            r5 = 0
            r7.setShowsUserInterface(r5)
            androidx.core.app.NotificationCompat$Action r0 = r7.build()
            r6 = r0
            goto L_0x0409
        L_0x03ff:
            r50 = r5
            r47 = r6
            r49 = r7
            r48 = r10
            r5 = 0
            r6 = 0
        L_0x0409:
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.pushDialogs
            java.lang.Object r0 = r0.get(r11)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0417
            java.lang.Integer r0 = java.lang.Integer.valueOf(r5)
        L_0x0417:
            int r0 = r0.intValue()
            int r5 = r4.size()
            int r0 = java.lang.Math.max(r0, r5)
            r5 = 2
            r7 = 1
            if (r0 <= r7) goto L_0x0443
            int r10 = android.os.Build.VERSION.SDK_INT
            r7 = 28
            if (r10 < r7) goto L_0x042e
            goto L_0x0443
        L_0x042e:
            java.lang.Object[] r7 = new java.lang.Object[r5]
            r10 = 0
            r7[r10] = r8
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r10 = 1
            r7[r10] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r7)
            r7 = r0
            r10 = r6
            goto L_0x0445
        L_0x0443:
            r10 = r6
            r7 = r8
        L_0x0445:
            long r5 = (long) r14
            r51 = r9
            r9 = r36
            java.lang.Object r0 = r9.get(r5)
            r36 = r0
            androidx.core.app.Person r36 = (androidx.core.app.Person) r36
            int r0 = android.os.Build.VERSION.SDK_INT
            r52 = r15
            r15 = 28
            if (r0 < r15) goto L_0x04d1
            if (r36 != 0) goto L_0x04d1
            org.telegram.messenger.MessagesController r0 = r71.getMessagesController()
            java.lang.Integer r15 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r15)
            if (r0 != 0) goto L_0x0472
            org.telegram.messenger.UserConfig r0 = r71.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x0472:
            if (r0 == 0) goto L_0x04d1
            org.telegram.tgnet.TLRPC$UserProfilePhoto r15 = r0.photo     // Catch:{ all -> 0x04c8 }
            if (r15 == 0) goto L_0x04d1
            org.telegram.tgnet.TLRPC$UserProfilePhoto r15 = r0.photo     // Catch:{ all -> 0x04c8 }
            org.telegram.tgnet.TLRPC$FileLocation r15 = r15.photo_small     // Catch:{ all -> 0x04c8 }
            if (r15 == 0) goto L_0x04d1
            org.telegram.tgnet.TLRPC$UserProfilePhoto r15 = r0.photo     // Catch:{ all -> 0x04c8 }
            org.telegram.tgnet.TLRPC$FileLocation r15 = r15.photo_small     // Catch:{ all -> 0x04c8 }
            r53 = r14
            long r14 = r15.volume_id     // Catch:{ all -> 0x04c4 }
            int r54 = (r14 > r30 ? 1 : (r14 == r30 ? 0 : -1))
            if (r54 == 0) goto L_0x04c1
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r0.photo     // Catch:{ all -> 0x04c4 }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ all -> 0x04c4 }
            int r14 = r14.local_id     // Catch:{ all -> 0x04c4 }
            if (r14 == 0) goto L_0x04c1
            androidx.core.app.Person$Builder r14 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x04c4 }
            r14.<init>()     // Catch:{ all -> 0x04c4 }
            java.lang.String r15 = "FromYou"
            r54 = r10
            r10 = 2131625369(0x7f0e0599, float:1.8877944E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r15, r10)     // Catch:{ all -> 0x04bf }
            r14.setName(r10)     // Catch:{ all -> 0x04bf }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x04bf }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x04bf }
            r10 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r10)     // Catch:{ all -> 0x04bf }
            r1.loadRoundAvatar(r0, r14)     // Catch:{ all -> 0x04bf }
            androidx.core.app.Person r10 = r14.build()     // Catch:{ all -> 0x04bf }
            r9.put(r5, r10)     // Catch:{ all -> 0x04bb }
            r36 = r10
            goto L_0x04d5
        L_0x04bb:
            r0 = move-exception
            r36 = r10
            goto L_0x04cd
        L_0x04bf:
            r0 = move-exception
            goto L_0x04cd
        L_0x04c1:
            r54 = r10
            goto L_0x04d5
        L_0x04c4:
            r0 = move-exception
            r54 = r10
            goto L_0x04cd
        L_0x04c8:
            r0 = move-exception
            r54 = r10
            r53 = r14
        L_0x04cd:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x04d5
        L_0x04d1:
            r54 = r10
            r53 = r14
        L_0x04d5:
            r0 = r36
            java.lang.String r10 = ""
            if (r0 == 0) goto L_0x04e1
            androidx.core.app.NotificationCompat$MessagingStyle r14 = new androidx.core.app.NotificationCompat$MessagingStyle
            r14.<init>((androidx.core.app.Person) r0)
            goto L_0x04e6
        L_0x04e1:
            androidx.core.app.NotificationCompat$MessagingStyle r14 = new androidx.core.app.NotificationCompat$MessagingStyle
            r14.<init>((java.lang.CharSequence) r10)
        L_0x04e6:
            int r0 = android.os.Build.VERSION.SDK_INT
            r15 = 28
            if (r0 < r15) goto L_0x04f0
            if (r13 >= 0) goto L_0x04f3
            if (r27 != 0) goto L_0x04f3
        L_0x04f0:
            r14.setConversationTitle(r7)
        L_0x04f3:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r15) goto L_0x04fe
            if (r27 != 0) goto L_0x04fc
            if (r13 >= 0) goto L_0x04fc
            goto L_0x04fe
        L_0x04fc:
            r0 = 0
            goto L_0x04ff
        L_0x04fe:
            r0 = 1
        L_0x04ff:
            r14.setGroupConversation(r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r7 = 1
            java.lang.String[] r15 = new java.lang.String[r7]
            r36 = r3
            boolean[] r3 = new boolean[r7]
            if (r2 == 0) goto L_0x051a
            org.json.JSONArray r18 = new org.json.JSONArray
            r18.<init>()
            r55 = r2
            r2 = r18
            goto L_0x051d
        L_0x051a:
            r55 = r2
            r2 = 0
        L_0x051d:
            int r18 = r4.size()
            int r56 = r18 + -1
            r7 = r56
            r56 = 0
            r57 = 0
        L_0x0529:
            r58 = 1000(0x3e8, double:4.94E-321)
            if (r7 < 0) goto L_0x08a0
            java.lang.Object r60 = r4.get(r7)
            r61 = r4
            r4 = r60
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            r60 = r7
            java.lang.String r7 = r1.getShortStringForMessage(r4, r15, r3)
            int r62 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r62 != 0) goto L_0x0546
            r21 = 0
            r15[r21] = r8
            goto L_0x0563
        L_0x0546:
            r21 = 0
            if (r13 >= 0) goto L_0x0563
            r62 = r8
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner
            boolean r8 = r8.from_scheduled
            if (r8 == 0) goto L_0x0560
            r8 = 2131625949(0x7f0e07dd, float:1.887912E38)
            r63 = r2
            java.lang.String r2 = "NotificationMessageScheduledName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r8)
            r15[r21] = r2
            goto L_0x0567
        L_0x0560:
            r63 = r2
            goto L_0x0567
        L_0x0563:
            r63 = r2
            r62 = r8
        L_0x0567:
            if (r7 != 0) goto L_0x059f
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0591
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r7 = "message text is null for "
            r2.append(r7)
            int r7 = r4.getId()
            r2.append(r7)
            java.lang.String r7 = " did = "
            r2.append(r7)
            long r7 = r4.getDialogId()
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.w(r2)
        L_0x0591:
            r64 = r5
            r66 = r11
            r40 = r42
            r68 = r43
            r43 = r3
            r3 = r63
            goto L_0x088d
        L_0x059f:
            int r2 = r0.length()
            if (r2 <= 0) goto L_0x05aa
            java.lang.String r2 = "\n\n"
            r0.append(r2)
        L_0x05aa:
            int r2 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r2 == 0) goto L_0x05d4
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
            boolean r2 = r2.from_scheduled
            if (r2 == 0) goto L_0x05d4
            if (r13 <= 0) goto L_0x05d4
            r2 = 2
            java.lang.Object[] r8 = new java.lang.Object[r2]
            r2 = 2131625949(0x7f0e07dd, float:1.887912E38)
            r64 = r5
            java.lang.String r5 = "NotificationMessageScheduledName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r5 = 0
            r8[r5] = r2
            r2 = 1
            r8[r2] = r7
            java.lang.String r2 = "%1$s: %2$s"
            java.lang.String r7 = java.lang.String.format(r2, r8)
            r0.append(r7)
            goto L_0x05f2
        L_0x05d4:
            r64 = r5
            r5 = 0
            r2 = r15[r5]
            if (r2 == 0) goto L_0x05ef
            r2 = 2
            java.lang.Object[] r6 = new java.lang.Object[r2]
            r2 = r15[r5]
            r6[r5] = r2
            r2 = 1
            r6[r2] = r7
            java.lang.String r2 = "%1$s: %2$s"
            java.lang.String r2 = java.lang.String.format(r2, r6)
            r0.append(r2)
            goto L_0x05f2
        L_0x05ef:
            r0.append(r7)
        L_0x05f2:
            if (r13 <= 0) goto L_0x05f6
            long r5 = (long) r13
            goto L_0x0603
        L_0x05f6:
            if (r27 == 0) goto L_0x05fb
            int r2 = -r13
        L_0x05f9:
            long r5 = (long) r2
            goto L_0x0603
        L_0x05fb:
            if (r13 >= 0) goto L_0x0602
            int r2 = r4.getFromId()
            goto L_0x05f9
        L_0x0602:
            r5 = r11
        L_0x0603:
            java.lang.Object r2 = r9.get(r5)
            androidx.core.app.Person r2 = (androidx.core.app.Person) r2
            r8 = 0
            r66 = r15[r8]
            if (r66 != 0) goto L_0x065c
            if (r44 == 0) goto L_0x0651
            if (r13 >= 0) goto L_0x063a
            if (r27 == 0) goto L_0x0628
            int r8 = android.os.Build.VERSION.SDK_INT
            r66 = r11
            r11 = 27
            r12 = r43
            if (r8 <= r11) goto L_0x064e
            r8 = 2131625903(0x7f0e07af, float:1.8879027E38)
            java.lang.String r16 = org.telegram.messenger.LocaleController.getString(r12, r8)
            r8 = r16
            goto L_0x0637
        L_0x0628:
            r66 = r11
            r12 = r43
            r11 = 27
            r8 = 2131625904(0x7f0e07b0, float:1.887903E38)
            java.lang.String r11 = "NotificationHiddenChatUserName"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
        L_0x0637:
            r11 = r42
            goto L_0x066b
        L_0x063a:
            r66 = r11
            r12 = r43
            int r8 = android.os.Build.VERSION.SDK_INT
            r11 = 27
            if (r8 <= r11) goto L_0x064e
            r11 = r42
            r8 = 2131625906(0x7f0e07b2, float:1.8879033E38)
            java.lang.String r40 = org.telegram.messenger.LocaleController.getString(r11, r8)
            goto L_0x0669
        L_0x064e:
            r11 = r42
            goto L_0x0657
        L_0x0651:
            r66 = r11
            r11 = r42
            r12 = r43
        L_0x0657:
            r8 = 2131625906(0x7f0e07b2, float:1.8879033E38)
            r8 = r10
            goto L_0x066b
        L_0x065c:
            r66 = r11
            r11 = r42
            r12 = r43
            r8 = 2131625906(0x7f0e07b2, float:1.8879033E38)
            r21 = 0
            r40 = r15[r21]
        L_0x0669:
            r8 = r40
        L_0x066b:
            r40 = r11
            if (r2 == 0) goto L_0x067f
            java.lang.CharSequence r11 = r2.getName()
            boolean r11 = android.text.TextUtils.equals(r11, r8)
            if (r11 != 0) goto L_0x067a
            goto L_0x067f
        L_0x067a:
            r43 = r3
            r8 = r12
            goto L_0x06f4
        L_0x067f:
            androidx.core.app.Person$Builder r2 = new androidx.core.app.Person$Builder
            r2.<init>()
            r2.setName(r8)
            r8 = 0
            boolean r11 = r3[r8]
            if (r11 == 0) goto L_0x06ea
            if (r13 == 0) goto L_0x06ea
            int r8 = android.os.Build.VERSION.SDK_INT
            r11 = 28
            if (r8 < r11) goto L_0x06ea
            if (r13 > 0) goto L_0x06e1
            if (r27 == 0) goto L_0x0699
            goto L_0x06e1
        L_0x0699:
            if (r13 >= 0) goto L_0x06dc
            int r8 = r4.getFromId()
            org.telegram.messenger.MessagesController r11 = r71.getMessagesController()
            r43 = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r3 = r11.getUser(r3)
            if (r3 != 0) goto L_0x06c1
            org.telegram.messenger.MessagesStorage r3 = r71.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r3 = r3.getUserSync(r8)
            if (r3 == 0) goto L_0x06c1
            org.telegram.messenger.MessagesController r8 = r71.getMessagesController()
            r11 = 1
            r8.putUser(r3, r11)
        L_0x06c1:
            if (r3 == 0) goto L_0x06de
            org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r3.photo
            if (r3 == 0) goto L_0x06de
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_small
            if (r3 == 0) goto L_0x06de
            r8 = r12
            long r11 = r3.volume_id
            int r68 = (r11 > r30 ? 1 : (r11 == r30 ? 0 : -1))
            if (r68 == 0) goto L_0x06df
            int r11 = r3.local_id
            if (r11 == 0) goto L_0x06df
            r11 = 1
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r11)
            goto L_0x06e6
        L_0x06dc:
            r43 = r3
        L_0x06de:
            r8 = r12
        L_0x06df:
            r3 = 0
            goto L_0x06e6
        L_0x06e1:
            r43 = r3
            r8 = r12
            r3 = r45
        L_0x06e6:
            r1.loadRoundAvatar(r3, r2)
            goto L_0x06ed
        L_0x06ea:
            r43 = r3
            r8 = r12
        L_0x06ed:
            androidx.core.app.Person r2 = r2.build()
            r9.put(r5, r2)
        L_0x06f4:
            if (r13 == 0) goto L_0x0823
            int r3 = android.os.Build.VERSION.SDK_INT
            r5 = 28
            if (r3 < r5) goto L_0x07d3
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r6 = "activity"
            java.lang.Object r3 = r3.getSystemService(r6)
            android.app.ActivityManager r3 = (android.app.ActivityManager) r3
            boolean r3 = r3.isLowRamDevice()
            if (r3 != 0) goto L_0x07d3
            if (r44 != 0) goto L_0x07d3
            boolean r3 = r4.isSecretMedia()
            if (r3 != 0) goto L_0x07d3
            int r3 = r4.type
            r6 = 1
            if (r3 == r6) goto L_0x071f
            boolean r3 = r4.isSticker()
            if (r3 == 0) goto L_0x07d3
        L_0x071f:
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r6 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r11 = r4.messageOwner
            int r11 = r11.date
            long r11 = (long) r11
            long r11 = r11 * r58
            r6.<init>(r7, r11, r2)
            boolean r11 = r4.isSticker()
            if (r11 == 0) goto L_0x073a
            java.lang.String r11 = "image/webp"
            goto L_0x073c
        L_0x073a:
            java.lang.String r11 = "image/jpeg"
        L_0x073c:
            boolean r12 = r3.exists()
            if (r12 == 0) goto L_0x074d
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r5 = "org.telegram.messenger.beta.provider"
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r12, r5, r3)
            r68 = r8
            goto L_0x07a3
        L_0x074d:
            org.telegram.messenger.FileLoader r5 = r71.getFileLoader()
            java.lang.String r12 = r3.getName()
            boolean r5 = r5.isLoadingFile(r12)
            if (r5 == 0) goto L_0x07a0
            android.net.Uri$Builder r5 = new android.net.Uri$Builder
            r5.<init>()
            java.lang.String r12 = "content"
            android.net.Uri$Builder r5 = r5.scheme(r12)
            java.lang.String r12 = "org.telegram.messenger.beta.notification_image_provider"
            android.net.Uri$Builder r5 = r5.authority(r12)
            java.lang.String r12 = "msg_media_raw"
            android.net.Uri$Builder r5 = r5.appendPath(r12)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r68 = r8
            int r8 = r1.currentAccount
            r12.append(r8)
            r12.append(r10)
            java.lang.String r8 = r12.toString()
            android.net.Uri$Builder r5 = r5.appendPath(r8)
            java.lang.String r8 = r3.getName()
            android.net.Uri$Builder r5 = r5.appendPath(r8)
            java.lang.String r3 = r3.getAbsolutePath()
            java.lang.String r8 = "final_path"
            android.net.Uri$Builder r3 = r5.appendQueryParameter(r8, r3)
            android.net.Uri r3 = r3.build()
            goto L_0x07a3
        L_0x07a0:
            r68 = r8
            r3 = 0
        L_0x07a3:
            if (r3 == 0) goto L_0x07d5
            r6.setData(r11, r3)
            r14.addMessage(r6)
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r6 = "com.android.systemui"
            r8 = 1
            r5.grantUriPermission(r6, r3, r8)
            org.telegram.messenger.-$$Lambda$NotificationsController$hROO1aIM4eduzMv5uJ3U4yL97Bo r5 = new org.telegram.messenger.-$$Lambda$NotificationsController$hROO1aIM4eduzMv5uJ3U4yL97Bo
            r5.<init>(r3)
            r11 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r5, r11)
            java.lang.CharSequence r3 = r4.caption
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x07d1
            java.lang.CharSequence r3 = r4.caption
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            int r5 = r5.date
            long r5 = (long) r5
            long r5 = r5 * r58
            r14.addMessage(r3, r5, r2)
        L_0x07d1:
            r3 = 1
            goto L_0x07d6
        L_0x07d3:
            r68 = r8
        L_0x07d5:
            r3 = 0
        L_0x07d6:
            if (r3 != 0) goto L_0x07e2
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            int r3 = r3.date
            long r5 = (long) r3
            long r5 = r5 * r58
            r14.addMessage(r7, r5, r2)
        L_0x07e2:
            if (r44 != 0) goto L_0x082f
            boolean r2 = r4.isVoice()
            if (r2 == 0) goto L_0x082f
            java.util.List r2 = r14.getMessages()
            boolean r3 = r2.isEmpty()
            if (r3 != 0) goto L_0x082f
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3)
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 24
            if (r5 < r6) goto L_0x080b
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0809 }
            java.lang.String r6 = "org.telegram.messenger.beta.provider"
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r5, r6, r3)     // Catch:{ Exception -> 0x0809 }
            goto L_0x080f
        L_0x0809:
            r3 = 0
            goto L_0x080f
        L_0x080b:
            android.net.Uri r3 = android.net.Uri.fromFile(r3)
        L_0x080f:
            if (r3 == 0) goto L_0x082f
            int r5 = r2.size()
            r6 = 1
            int r5 = r5 - r6
            java.lang.Object r2 = r2.get(r5)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r2 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r2
            java.lang.String r5 = "audio/ogg"
            r2.setData(r5, r3)
            goto L_0x082f
        L_0x0823:
            r68 = r8
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            int r3 = r3.date
            long r5 = (long) r3
            long r5 = r5 * r58
            r14.addMessage(r7, r5, r2)
        L_0x082f:
            if (r63 == 0) goto L_0x0874
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0874 }
            r2.<init>()     // Catch:{ JSONException -> 0x0874 }
            java.lang.String r3 = "text"
            r2.put(r3, r7)     // Catch:{ JSONException -> 0x0874 }
            java.lang.String r3 = "date"
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner     // Catch:{ JSONException -> 0x0874 }
            int r5 = r5.date     // Catch:{ JSONException -> 0x0874 }
            r2.put(r3, r5)     // Catch:{ JSONException -> 0x0874 }
            boolean r3 = r4.isFromUser()     // Catch:{ JSONException -> 0x0874 }
            if (r3 == 0) goto L_0x086c
            if (r13 >= 0) goto L_0x086c
            org.telegram.messenger.MessagesController r3 = r71.getMessagesController()     // Catch:{ JSONException -> 0x0874 }
            int r5 = r4.getFromId()     // Catch:{ JSONException -> 0x0874 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ JSONException -> 0x0874 }
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)     // Catch:{ JSONException -> 0x0874 }
            if (r3 == 0) goto L_0x086c
            java.lang.String r5 = "fname"
            java.lang.String r6 = r3.first_name     // Catch:{ JSONException -> 0x0874 }
            r2.put(r5, r6)     // Catch:{ JSONException -> 0x0874 }
            java.lang.String r5 = "lname"
            java.lang.String r3 = r3.last_name     // Catch:{ JSONException -> 0x0874 }
            r2.put(r5, r3)     // Catch:{ JSONException -> 0x0874 }
        L_0x086c:
            r3 = r63
            r3.put(r2)     // Catch:{ JSONException -> 0x0872 }
            goto L_0x0876
        L_0x0872:
            goto L_0x0876
        L_0x0874:
            r3 = r63
        L_0x0876:
            r5 = 777000(0xbdb28, double:3.83889E-318)
            int r2 = (r66 > r5 ? 1 : (r66 == r5 ? 0 : -1))
            if (r2 != 0) goto L_0x088d
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup
            if (r2 == 0) goto L_0x088d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r2 = r2.rows
            int r4 = r4.getId()
            r57 = r2
            r56 = r4
        L_0x088d:
            int r7 = r60 + -1
            r2 = r3
            r42 = r40
            r3 = r43
            r4 = r61
            r8 = r62
            r5 = r64
            r11 = r66
            r43 = r68
            goto L_0x0529
        L_0x08a0:
            r3 = r2
            r61 = r4
            r62 = r8
            r66 = r11
            android.content.Intent r2 = new android.content.Intent
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.ui.LaunchActivity> r5 = org.telegram.ui.LaunchActivity.class
            r2.<init>(r4, r5)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "com.tmessages.openchat"
            r4.append(r5)
            double r5 = java.lang.Math.random()
            r4.append(r5)
            r5 = 2147483647(0x7fffffff, float:NaN)
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r2.setAction(r4)
            r4 = 32768(0x8000, float:4.5918E-41)
            r2.setFlags(r4)
            java.lang.String r4 = "android.intent.category.LAUNCHER"
            r2.addCategory(r4)
            if (r13 == 0) goto L_0x08ec
            if (r13 <= 0) goto L_0x08e3
            java.lang.String r4 = "userId"
            r2.putExtra(r4, r13)
            goto L_0x08e9
        L_0x08e3:
            int r4 = -r13
            java.lang.String r5 = "chatId"
            r2.putExtra(r5, r4)
        L_0x08e9:
            r5 = r37
            goto L_0x08f3
        L_0x08ec:
            java.lang.String r4 = "encId"
            r5 = r37
            r2.putExtra(r4, r5)
        L_0x08f3:
            int r4 = r1.currentAccount
            r6 = r36
            r2.putExtra(r6, r4)
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            r7 = 1073741824(0x40000000, float:2.0)
            r8 = 0
            android.app.PendingIntent r2 = android.app.PendingIntent.getActivity(r4, r8, r2, r7)
            androidx.core.app.NotificationCompat$WearableExtender r4 = new androidx.core.app.NotificationCompat$WearableExtender
            r4.<init>()
            r7 = r54
            if (r54 == 0) goto L_0x090f
            r4.addAction(r7)
        L_0x090f:
            android.content.Intent r8 = new android.content.Intent
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r11 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r8.<init>(r9, r11)
            r9 = 32
            r8.addFlags(r9)
            java.lang.String r9 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r8.setAction(r9)
            r9 = r52
            r11 = r66
            r8.putExtra(r9, r11)
            r9 = r50
            r15 = r51
            r8.putExtra(r15, r9)
            r63 = r3
            int r3 = r1.currentAccount
            r8.putExtra(r6, r3)
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r15 = r49.intValue()
            r54 = r7
            r7 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r3 = android.app.PendingIntent.getBroadcast(r3, r15, r8, r7)
            androidx.core.app.NotificationCompat$Action$Builder r7 = new androidx.core.app.NotificationCompat$Action$Builder
            r8 = 2131165625(0x7var_b9, float:1.7945472E38)
            r15 = 2131625637(0x7f0e06a5, float:1.8878488E38)
            r36 = r6
            java.lang.String r6 = "MarkAsRead"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r15)
            r7.<init>(r8, r6, r3)
            r3 = 2
            r7.setSemanticAction(r3)
            r3 = 0
            r7.setShowsUserInterface(r3)
            androidx.core.app.NotificationCompat$Action r3 = r7.build()
            java.lang.String r6 = "_"
            if (r13 == 0) goto L_0x099b
            if (r13 <= 0) goto L_0x0982
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "tguser"
            r5.append(r7)
            r5.append(r13)
            r5.append(r6)
            r5.append(r9)
            java.lang.String r5 = r5.toString()
            goto L_0x09ba
        L_0x0982:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "tgchat"
            r5.append(r7)
            int r7 = -r13
            r5.append(r7)
            r5.append(r6)
            r5.append(r9)
            java.lang.String r5 = r5.toString()
            goto L_0x09ba
        L_0x099b:
            long r7 = globalSecretChatId
            int r15 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r15 == 0) goto L_0x09b9
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "tgenc"
            r7.append(r8)
            r7.append(r5)
            r7.append(r6)
            r7.append(r9)
            java.lang.String r5 = r7.toString()
            goto L_0x09ba
        L_0x09b9:
            r5 = 0
        L_0x09ba:
            if (r5 == 0) goto L_0x09de
            r4.setDismissalId(r5)
            androidx.core.app.NotificationCompat$WearableExtender r7 = new androidx.core.app.NotificationCompat$WearableExtender
            r7.<init>()
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r15 = "summary_"
            r8.append(r15)
            r8.append(r5)
            java.lang.String r5 = r8.toString()
            r7.setDismissalId(r5)
            r5 = r72
            r5.extend(r7)
            goto L_0x09e0
        L_0x09de:
            r5 = r72
        L_0x09e0:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "tgaccount"
            r7.append(r8)
            r8 = r53
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r4.setBridgeTag(r7)
            r7 = r61
            r15 = 0
            java.lang.Object r26 = r7.get(r15)
            r15 = r26
            org.telegram.messenger.MessageObject r15 = (org.telegram.messenger.MessageObject) r15
            org.telegram.tgnet.TLRPC$Message r15 = r15.messageOwner
            int r15 = r15.date
            r26 = r6
            long r5 = (long) r15
            long r5 = r5 * r58
            androidx.core.app.NotificationCompat$Builder r15 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            r15.<init>(r8)
            r8 = r62
            r15.setContentTitle(r8)
            r50 = r9
            r9 = 2131165744(0x7var_, float:1.7945714E38)
            r15.setSmallIcon(r9)
            java.lang.String r0 = r0.toString()
            r15.setContentText(r0)
            r9 = 1
            r15.setAutoCancel(r9)
            int r0 = r7.size()
            r15.setNumber(r0)
            r0 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r15.setColor(r0)
            r7 = 0
            r15.setGroupSummary(r7)
            r15.setWhen(r5)
            r15.setShowWhen(r9)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r7 = "sdid_"
            r0.append(r7)
            r0.append(r11)
            java.lang.String r0 = r0.toString()
            r15.setShortcutId(r0)
            r15.setStyle(r14)
            r15.setContentIntent(r2)
            r15.extend(r4)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r10)
            r9 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r9 = r9 - r5
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            r15.setSortKey(r0)
            java.lang.String r0 = "msg"
            r15.setCategory(r0)
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r4 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r2, r4)
            java.lang.String r2 = "messageDate"
            r4 = r32
            r0.putExtra(r2, r4)
            java.lang.String r2 = "dialogId"
            r0.putExtra(r2, r11)
            int r2 = r1.currentAccount
            r5 = r36
            r0.putExtra(r5, r2)
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r6 = r49.intValue()
            r7 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r2, r6, r0, r7)
            r15.setDeleteIntent(r0)
            if (r19 == 0) goto L_0x0ab0
            java.lang.String r0 = r1.notificationGroup
            r15.setGroup(r0)
            r2 = 1
            r15.setGroupAlertBehavior(r2)
        L_0x0ab0:
            if (r54 == 0) goto L_0x0ab7
            r2 = r54
            r15.addAction(r2)
        L_0x0ab7:
            if (r44 != 0) goto L_0x0abc
            r15.addAction(r3)
        L_0x0abc:
            int r0 = r34.size()
            r2 = 1
            if (r0 != r2) goto L_0x0acf
            boolean r0 = android.text.TextUtils.isEmpty(r74)
            if (r0 != 0) goto L_0x0acf
            r3 = r74
            r15.setSubText(r3)
            goto L_0x0ad1
        L_0x0acf:
            r3 = r74
        L_0x0ad1:
            if (r13 != 0) goto L_0x0ad6
            r15.setLocalOnly(r2)
        L_0x0ad6:
            if (r48 == 0) goto L_0x0add
            r9 = r48
            r15.setLargeIcon(r9)
        L_0x0add:
            r6 = 0
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r6)
            if (r0 != 0) goto L_0x0b79
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0b79
            r0 = r57
            if (r0 == 0) goto L_0x0b79
            int r6 = r0.size()
            r10 = 0
        L_0x0af1:
            if (r10 >= r6) goto L_0x0b79
            java.lang.Object r7 = r0.get(r10)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r7 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r9 = r7.buttons
            int r9 = r9.size()
            r14 = 0
        L_0x0b00:
            if (r14 >= r9) goto L_0x0b65
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r2 = r7.buttons
            java.lang.Object r2 = r2.get(r14)
            org.telegram.tgnet.TLRPC$KeyboardButton r2 = (org.telegram.tgnet.TLRPC$KeyboardButton) r2
            r30 = r0
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback
            if (r0 == 0) goto L_0x0b4e
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            r31 = r6
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r6 = org.telegram.messenger.NotificationCallbackReceiver.class
            r0.<init>(r3, r6)
            int r3 = r1.currentAccount
            r0.putExtra(r5, r3)
            java.lang.String r3 = "did"
            r0.putExtra(r3, r11)
            byte[] r3 = r2.data
            if (r3 == 0) goto L_0x0b2e
            java.lang.String r6 = "data"
            r0.putExtra(r6, r3)
        L_0x0b2e:
            java.lang.String r3 = "mid"
            r6 = r56
            r0.putExtra(r3, r6)
            java.lang.String r2 = r2.text
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            r36 = r5
            int r5 = r1.lastButtonId
            r32 = r6
            int r6 = r5 + 1
            r1.lastButtonId = r6
            r6 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r3, r5, r0, r6)
            r3 = 0
            r15.addAction(r3, r2, r0)
            goto L_0x0b57
        L_0x0b4e:
            r36 = r5
            r31 = r6
            r32 = r56
            r3 = 0
            r6 = 134217728(0x8000000, float:3.85186E-34)
        L_0x0b57:
            int r14 = r14 + 1
            r3 = r74
            r0 = r30
            r6 = r31
            r56 = r32
            r5 = r36
            r2 = 1
            goto L_0x0b00
        L_0x0b65:
            r30 = r0
            r36 = r5
            r31 = r6
            r32 = r56
            r3 = 0
            r6 = 134217728(0x8000000, float:3.85186E-34)
            int r10 = r10 + 1
            r3 = r74
            r6 = r31
            r2 = 1
            goto L_0x0af1
        L_0x0b79:
            r3 = 0
            if (r41 != 0) goto L_0x0ba0
            if (r39 == 0) goto L_0x0ba0
            r2 = r39
            java.lang.String r0 = r2.phone
            if (r0 == 0) goto L_0x0ba0
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0ba0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "tel:+"
            r0.append(r5)
            java.lang.String r2 = r2.phone
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            r15.addPerson(r0)
        L_0x0ba0:
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 26
            if (r0 < r2) goto L_0x0bb5
            if (r19 == 0) goto L_0x0bae
            java.lang.String r0 = OTHER_NOTIFICATIONS_CHANNEL
            r15.setChannelId(r0)
            goto L_0x0bb5
        L_0x0bae:
            java.lang.String r0 = r33.getChannelId()
            r15.setChannelId(r0)
        L_0x0bb5:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            int r5 = r49.intValue()
            android.app.Notification r6 = r15.build()
            r0.<init>(r5, r6)
            r5 = r24
            r5.add(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.wearNotificationsIds
            r6 = r49
            r0.put(r11, r6)
            if (r13 == 0) goto L_0x0CLASSNAME
            if (r55 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "reply"
            r6 = r47
            r7 = r55
            r7.put(r0, r6)     // Catch:{ JSONException -> 0x0CLASSNAME }
            java.lang.String r0 = "name"
            r7.put(r0, r8)     // Catch:{ JSONException -> 0x0CLASSNAME }
            r6 = r50
            r8 = r51
            r7.put(r8, r6)     // Catch:{ JSONException -> 0x0CLASSNAME }
            java.lang.String r0 = "max_date"
            r7.put(r0, r4)     // Catch:{ JSONException -> 0x0CLASSNAME }
            int r0 = java.lang.Math.abs(r13)     // Catch:{ JSONException -> 0x0CLASSNAME }
            r4 = r25
            r7.put(r4, r0)     // Catch:{ JSONException -> 0x0CLASSNAME }
            if (r46 == 0) goto L_0x0c1e
            java.lang.String r0 = "photo"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0CLASSNAME }
            r4.<init>()     // Catch:{ JSONException -> 0x0CLASSNAME }
            r6 = r46
            int r8 = r6.dc_id     // Catch:{ JSONException -> 0x0CLASSNAME }
            r4.append(r8)     // Catch:{ JSONException -> 0x0CLASSNAME }
            r8 = r26
            r4.append(r8)     // Catch:{ JSONException -> 0x0CLASSNAME }
            long r9 = r6.volume_id     // Catch:{ JSONException -> 0x0CLASSNAME }
            r4.append(r9)     // Catch:{ JSONException -> 0x0CLASSNAME }
            r4.append(r8)     // Catch:{ JSONException -> 0x0CLASSNAME }
            long r8 = r6.secret     // Catch:{ JSONException -> 0x0CLASSNAME }
            r4.append(r8)     // Catch:{ JSONException -> 0x0CLASSNAME }
            java.lang.String r4 = r4.toString()     // Catch:{ JSONException -> 0x0CLASSNAME }
            r7.put(r0, r4)     // Catch:{ JSONException -> 0x0CLASSNAME }
        L_0x0c1e:
            if (r63 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "msgs"
            r4 = r63
            r7.put(r0, r4)     // Catch:{ JSONException -> 0x0CLASSNAME }
        L_0x0CLASSNAME:
            java.lang.String r0 = "type"
            if (r13 <= 0) goto L_0x0CLASSNAME
            java.lang.String r4 = "user"
            r7.put(r0, r4)     // Catch:{ JSONException -> 0x0CLASSNAME }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            if (r13 >= 0) goto L_0x0CLASSNAME
            if (r27 != 0) goto L_0x0c3e
            if (r35 == 0) goto L_0x0CLASSNAME
            goto L_0x0c3e
        L_0x0CLASSNAME:
            java.lang.String r4 = "group"
            r7.put(r0, r4)     // Catch:{ JSONException -> 0x0CLASSNAME }
            goto L_0x0CLASSNAME
        L_0x0c3e:
            java.lang.String r4 = "channel"
            r7.put(r0, r4)     // Catch:{ JSONException -> 0x0CLASSNAME }
        L_0x0CLASSNAME:
            r8 = r29
            r8.put(r7)     // Catch:{ JSONException -> 0x0c4b }
            goto L_0x0c4b
        L_0x0CLASSNAME:
            r8 = r29
        L_0x0c4b:
            int r10 = r23 + 1
            r7 = r5
            r9 = r8
            r12 = r19
            r4 = r20
            r13 = r22
            r6 = r28
            r2 = r33
            r3 = r34
            r15 = r44
            r14 = r53
            r5 = 0
            r11 = 1
            goto L_0x00d5
        L_0x0CLASSNAME:
            r33 = r2
            r28 = r6
            r5 = r7
            r4 = r8
            r8 = r9
            r19 = r12
            r53 = r14
            r3 = 0
            if (r19 == 0) goto L_0x0CLASSNAME
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0c8b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "show summary with id "
            r0.append(r2)
            int r2 = r1.notificationId
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0c8b:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r2 = r1.notificationId
            r6 = r33
            r0.notify(r2, r6)
            goto L_0x0c9c
        L_0x0CLASSNAME:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r2 = r1.notificationId
            r0.cancel(r2)
        L_0x0c9c:
            int r0 = r5.size()
            r10 = 0
        L_0x0ca1:
            if (r10 >= r0) goto L_0x0caf
            java.lang.Object r2 = r5.get(r10)
            org.telegram.messenger.NotificationsController$1NotificationHolder r2 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r2
            r2.call()
            int r10 = r10 + 1
            goto L_0x0ca1
        L_0x0caf:
            r5 = 0
        L_0x0cb0:
            int r0 = r28.size()
            if (r5 >= r0) goto L_0x0ce4
            r2 = r28
            java.lang.Object r0 = r2.valueAt(r5)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x0cd6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = "cancel notification id "
            r3.append(r6)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.w(r3)
        L_0x0cd6:
            androidx.core.app.NotificationManagerCompat r3 = notificationManager
            int r0 = r0.intValue()
            r3.cancel(r0)
            int r5 = r5 + 1
            r28 = r2
            goto L_0x0cb0
        L_0x0ce4:
            if (r8 == 0) goto L_0x0d04
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0d04 }
            r0.<init>()     // Catch:{ Exception -> 0x0d04 }
            r2 = r53
            r0.put(r4, r2)     // Catch:{ Exception -> 0x0d04 }
            java.lang.String r2 = "n"
            r0.put(r2, r8)     // Catch:{ Exception -> 0x0d04 }
            java.lang.String r2 = "/notify"
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0d04 }
            byte[] r0 = r0.getBytes()     // Catch:{ Exception -> 0x0d04 }
            java.lang.String r3 = "remote_notifications"
            org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r2, r0, r3)     // Catch:{ Exception -> 0x0d04 }
        L_0x0d04:
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
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener($$Lambda$NotificationsController$wVHQwnWTTlh7lF1NZGGoEEMMuyY.INSTANCE);
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
        TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(j);
        if (i == 4) {
            if (isGlobalNotificationsEnabled(j)) {
                edit.remove("notify2_" + j);
            } else {
                edit.putInt("notify2_" + j, 0);
            }
            getMessagesStorage().setDialogFlags(j, 0);
            if (tLRPC$Dialog != null) {
                tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
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
            if (tLRPC$Dialog != null) {
                TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
                tLRPC$Dialog.notify_settings = tLRPC$TL_peerNotifySettings;
                tLRPC$TL_peerNotifySettings.mute_until = currentTime;
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
            TLRPC$TL_account_updateNotifySettings tLRPC$TL_account_updateNotifySettings = new TLRPC$TL_account_updateNotifySettings();
            TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings = new TLRPC$TL_inputPeerNotifySettings();
            tLRPC$TL_account_updateNotifySettings.settings = tLRPC$TL_inputPeerNotifySettings;
            tLRPC$TL_inputPeerNotifySettings.flags |= 1;
            tLRPC$TL_inputPeerNotifySettings.show_previews = notificationsSettings.getBoolean("content_preview_" + j, true);
            TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings2 = tLRPC$TL_account_updateNotifySettings.settings;
            tLRPC$TL_inputPeerNotifySettings2.flags = tLRPC$TL_inputPeerNotifySettings2.flags | 2;
            tLRPC$TL_inputPeerNotifySettings2.silent = notificationsSettings.getBoolean("silent_" + j, false);
            int i3 = notificationsSettings.getInt("notify2_" + j, -1);
            if (i3 != -1) {
                TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings3 = tLRPC$TL_account_updateNotifySettings.settings;
                tLRPC$TL_inputPeerNotifySettings3.flags |= 4;
                if (i3 == 3) {
                    tLRPC$TL_inputPeerNotifySettings3.mute_until = notificationsSettings.getInt("notifyuntil_" + j, 0);
                } else {
                    if (i3 == 2) {
                        i = Integer.MAX_VALUE;
                    }
                    tLRPC$TL_inputPeerNotifySettings3.mute_until = i;
                }
            }
            TLRPC$TL_inputNotifyPeer tLRPC$TL_inputNotifyPeer = new TLRPC$TL_inputNotifyPeer();
            tLRPC$TL_account_updateNotifySettings.peer = tLRPC$TL_inputNotifyPeer;
            tLRPC$TL_inputNotifyPeer.peer = getMessagesController().getInputPeer(i2);
            getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, $$Lambda$NotificationsController$KyQqllEdy_fdmMCr6frsin2S3Cs.INSTANCE);
        }
    }

    public void updateServerNotificationsSettings(int i) {
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        TLRPC$TL_account_updateNotifySettings tLRPC$TL_account_updateNotifySettings = new TLRPC$TL_account_updateNotifySettings();
        TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings = new TLRPC$TL_inputPeerNotifySettings();
        tLRPC$TL_account_updateNotifySettings.settings = tLRPC$TL_inputPeerNotifySettings;
        tLRPC$TL_inputPeerNotifySettings.flags = 5;
        if (i == 0) {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyChats();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableGroup2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewGroup", true);
        } else if (i == 1) {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyUsers();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableAll2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewAll", true);
        } else {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyBroadcasts();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableChannel2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewChannel", true);
        }
        getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, $$Lambda$NotificationsController$WV8JpQrNXdfWVJfPV9wKTUTuLBk.INSTANCE);
    }

    public boolean isGlobalNotificationsEnabled(long j) {
        return isGlobalNotificationsEnabled(j, (Boolean) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000b, code lost:
        if (r4.booleanValue() != false) goto L_0x0029;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0025, code lost:
        if (r3.megagroup == false) goto L_0x0029;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isGlobalNotificationsEnabled(long r2, java.lang.Boolean r4) {
        /*
            r1 = this;
            int r3 = (int) r2
            r2 = 2
            r0 = 0
            if (r3 >= 0) goto L_0x0028
            if (r4 == 0) goto L_0x0010
            boolean r3 = r4.booleanValue()
            if (r3 == 0) goto L_0x000e
            goto L_0x0029
        L_0x000e:
            r2 = 0
            goto L_0x0029
        L_0x0010:
            org.telegram.messenger.MessagesController r4 = r1.getMessagesController()
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r4.getChat(r3)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x000e
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x000e
            goto L_0x0029
        L_0x0028:
            r2 = 1
        L_0x0029:
            boolean r2 = r1.isGlobalNotificationsEnabled((int) r2)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.isGlobalNotificationsEnabled(long, java.lang.Boolean):boolean");
    }

    public boolean isGlobalNotificationsEnabled(int i) {
        return getAccountInstance().getNotificationsSettings().getInt(getGlobalNotificationsKey(i), 0) < getConnectionsManager().getCurrentTime();
    }

    public void setGlobalNotificationsEnabled(int i, int i2) {
        getAccountInstance().getNotificationsSettings().edit().putInt(getGlobalNotificationsKey(i), i2).commit();
        updateServerNotificationsSettings(i);
        getMessagesStorage().updateMutedDialogsFiltersCounters();
    }
}
