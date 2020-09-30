package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
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
import org.telegram.ui.BubbleActivity;
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
    private HashSet<Long> openedInBubbleDialogs = new HashSet<>();
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

    static /* synthetic */ void lambda$updateServerNotificationsSettings$37(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$38(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public String getGlobalNotificationsKey(int i) {
        return i == 0 ? "EnableGroup2" : i == 1 ? "EnableAll2" : "EnableChannel2";
    }

    public void processEditedMessages(LongSparseArray<ArrayList<MessageObject>> longSparseArray) {
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
                try {
                    systemNotificationManager.createNotificationChannel(notificationChannel2);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
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
        this.openedInBubbleDialogs.clear();
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
            public final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationsController.this.lambda$setOpenedDialogId$2$NotificationsController(this.f$1);
            }
        });
    }

    public void setOpenedInBubble(long j, boolean z) {
        notificationsQueue.postRunnable(new Runnable(z, j) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationsController.this.lambda$setOpenedInBubble$3$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$setOpenedInBubble$3$NotificationsController(boolean z, long j) {
        if (z) {
            this.openedInBubbleDialogs.add(Long.valueOf(j));
        } else {
            this.openedInBubbleDialogs.remove(Long.valueOf(j));
        }
    }

    public void setLastOnlineFromOtherDevice(int i) {
        notificationsQueue.postRunnable(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationsController.this.lambda$setLastOnlineFromOtherDevice$4$NotificationsController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setLastOnlineFromOtherDevice$4$NotificationsController(int i) {
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
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isMegagroup())) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$forceShowPopupForReply$6$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$forceShowPopupForReply$6$NotificationsController() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isMegagroup())) {
                arrayList.add(0, messageObject);
            }
        }
        if (!arrayList.isEmpty() && !AndroidUtilities.needShowPasscode() && !SharedConfig.isWaitingForPasscodeEnter) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                public final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$5$NotificationsController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$5$NotificationsController(ArrayList arrayList) {
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
            public final /* synthetic */ SparseArray f$1;
            public final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$9$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$9$NotificationsController(SparseArray sparseArray, ArrayList arrayList) {
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
                public final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$7$NotificationsController(this.f$1);
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
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$8$NotificationsController(this.f$1);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$null$7$NotificationsController(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public /* synthetic */ void lambda$null$8$NotificationsController(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void removeDeletedHisoryFromNotifications(SparseIntArray sparseIntArray) {
        notificationsQueue.postRunnable(new Runnable(sparseIntArray, new ArrayList(0)) {
            public final /* synthetic */ SparseIntArray f$1;
            public final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$12$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$12$NotificationsController(SparseIntArray sparseIntArray, ArrayList arrayList) {
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
                public final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$10$NotificationsController(this.f$1);
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
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$11$NotificationsController(this.f$1);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$null$10$NotificationsController(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public /* synthetic */ void lambda$null$11$NotificationsController(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processReadMessages(SparseLongArray sparseLongArray, long j, int i, int i2, boolean z) {
        notificationsQueue.postRunnable(new Runnable(sparseLongArray, new ArrayList(0), j, i2, i, z) {
            public final /* synthetic */ SparseLongArray f$1;
            public final /* synthetic */ ArrayList f$2;
            public final /* synthetic */ long f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ int f$5;
            public final /* synthetic */ boolean f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                NotificationsController.this.lambda$processReadMessages$14$NotificationsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00f6 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processReadMessages$14$NotificationsController(org.telegram.messenger.support.SparseLongArray r19, java.util.ArrayList r20, long r21, int r23, int r24, boolean r25) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r23
            r4 = r24
            r5 = 32
            r6 = 1
            if (r1 == 0) goto L_0x0082
            r8 = 0
        L_0x0010:
            int r9 = r19.size()
            if (r8 >= r9) goto L_0x0082
            int r9 = r1.keyAt(r8)
            long r10 = r1.get(r9)
            r12 = 0
        L_0x001f:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.pushMessages
            int r13 = r13.size()
            if (r12 >= r13) goto L_0x007d
            java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.pushMessages
            java.lang.Object r13 = r13.get(r12)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            org.telegram.tgnet.TLRPC$Message r14 = r13.messageOwner
            boolean r14 = r14.from_scheduled
            if (r14 != 0) goto L_0x0077
            long r14 = r13.getDialogId()
            r16 = r8
            long r7 = (long) r9
            int r17 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1))
            if (r17 != 0) goto L_0x0079
            int r7 = r13.getId()
            int r8 = (int) r10
            if (r7 > r8) goto L_0x0079
            boolean r7 = r0.isPersonalMessage(r13)
            if (r7 == 0) goto L_0x0052
            int r7 = r0.personal_count
            int r7 = r7 - r6
            r0.personal_count = r7
        L_0x0052:
            r2.add(r13)
            int r7 = r13.getId()
            long r7 = (long) r7
            org.telegram.tgnet.TLRPC$Message r14 = r13.messageOwner
            org.telegram.tgnet.TLRPC$Peer r14 = r14.peer_id
            int r14 = r14.channel_id
            if (r14 == 0) goto L_0x0065
            long r14 = (long) r14
            long r14 = r14 << r5
            long r7 = r7 | r14
        L_0x0065:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r14 = r0.pushMessagesDict
            r14.remove(r7)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r0.delayedPushMessages
            r7.remove(r13)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r0.pushMessages
            r7.remove(r12)
            int r12 = r12 + -1
            goto L_0x0079
        L_0x0077:
            r16 = r8
        L_0x0079:
            int r12 = r12 + r6
            r8 = r16
            goto L_0x001f
        L_0x007d:
            r16 = r8
            int r8 = r16 + 1
            goto L_0x0010
        L_0x0082:
            r7 = 0
            int r1 = (r21 > r7 ? 1 : (r21 == r7 ? 0 : -1))
            if (r1 == 0) goto L_0x00f8
            if (r3 != 0) goto L_0x008c
            if (r4 == 0) goto L_0x00f8
        L_0x008c:
            r1 = 0
        L_0x008d:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r0.pushMessages
            int r7 = r7.size()
            if (r1 >= r7) goto L_0x00f8
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r0.pushMessages
            java.lang.Object r7 = r7.get(r1)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            long r8 = r7.getDialogId()
            int r10 = (r8 > r21 ? 1 : (r8 == r21 ? 0 : -1))
            if (r10 != 0) goto L_0x00f6
            if (r4 == 0) goto L_0x00ae
            org.telegram.tgnet.TLRPC$Message r8 = r7.messageOwner
            int r8 = r8.date
            if (r8 > r4) goto L_0x00c2
            goto L_0x00c4
        L_0x00ae:
            if (r25 != 0) goto L_0x00b9
            int r8 = r7.getId()
            if (r8 <= r3) goto L_0x00c4
            if (r3 >= 0) goto L_0x00c2
            goto L_0x00c4
        L_0x00b9:
            int r8 = r7.getId()
            if (r8 == r3) goto L_0x00c4
            if (r3 >= 0) goto L_0x00c2
            goto L_0x00c4
        L_0x00c2:
            r8 = 0
            goto L_0x00c5
        L_0x00c4:
            r8 = 1
        L_0x00c5:
            if (r8 == 0) goto L_0x00f6
            boolean r8 = r0.isPersonalMessage(r7)
            if (r8 == 0) goto L_0x00d2
            int r8 = r0.personal_count
            int r8 = r8 - r6
            r0.personal_count = r8
        L_0x00d2:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r0.pushMessages
            r8.remove(r1)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r0.delayedPushMessages
            r8.remove(r7)
            r2.add(r7)
            int r8 = r7.getId()
            long r8 = (long) r8
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            int r7 = r7.channel_id
            if (r7 == 0) goto L_0x00ef
            long r10 = (long) r7
            long r10 = r10 << r5
            long r8 = r8 | r10
        L_0x00ef:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r7 = r0.pushMessagesDict
            r7.remove(r8)
            int r1 = r1 + -1
        L_0x00f6:
            int r1 = r1 + r6
            goto L_0x008d
        L_0x00f8:
            boolean r1 = r20.isEmpty()
            if (r1 != 0) goto L_0x0106
            org.telegram.messenger.-$$Lambda$NotificationsController$hYfnxb5aCShrnoDeAgemyzWDJyc r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$hYfnxb5aCShrnoDeAgemyzWDJyc
            r1.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x0106:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processReadMessages$14$NotificationsController(org.telegram.messenger.support.SparseLongArray, java.util.ArrayList, long, int, int, boolean):void");
    }

    public /* synthetic */ void lambda$null$13$NotificationsController(ArrayList arrayList) {
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
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
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
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ ArrayList f$2;
                public final /* synthetic */ boolean f$3;
                public final /* synthetic */ boolean f$4;
                public final /* synthetic */ CountDownLatch f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    NotificationsController.this.lambda$processNewMessages$17$NotificationsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
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
    public /* synthetic */ void lambda$processNewMessages$17$NotificationsController(java.util.ArrayList r32, java.util.ArrayList r33, boolean r34, boolean r35, java.util.concurrent.CountDownLatch r36) {
        /*
            r31 = this;
            r8 = r31
            r9 = r32
            android.util.LongSparseArray r10 = new android.util.LongSparseArray
            r10.<init>()
            org.telegram.messenger.AccountInstance r0 = r31.getAccountInstance()
            android.content.SharedPreferences r11 = r0.getNotificationsSettings()
            java.lang.String r0 = "PinnedMessages"
            r12 = 1
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
            if (r1 == 0) goto L_0x006f
            boolean r1 = r7.localChannel
            goto L_0x008b
        L_0x006f:
            if (r6 >= 0) goto L_0x008e
            org.telegram.messenger.MessagesController r1 = r31.getMessagesController()
            int r2 = -r6
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x008a
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x008a
            r1 = 1
            goto L_0x008b
        L_0x008a:
            r1 = 0
        L_0x008b:
            r24 = r1
            goto L_0x0090
        L_0x008e:
            r24 = 0
        L_0x0090:
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
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
            int r1 = r7.getFromChatId()
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
            org.telegram.messenger.-$$Lambda$NotificationsController$QDzfGX8st0KAXCbxJrmXBka4BoE r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$QDzfGX8st0KAXCbxJrmXBka4BoE
            r2 = r33
            r1.<init>(r2, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x021c:
            if (r34 != 0) goto L_0x0220
            if (r18 == 0) goto L_0x02db
        L_0x0220:
            if (r17 == 0) goto L_0x022e
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.delayedPushMessages
            r0.clear()
            boolean r0 = r8.notifyCheck
            r8.showOrUpdateNotification(r0)
            goto L_0x02db
        L_0x022e:
            if (r16 == 0) goto L_0x02db
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
            boolean r6 = r8.notifyCheck
            if (r6 == 0) goto L_0x0295
            if (r0 != 0) goto L_0x0295
            android.util.LongSparseArray<java.lang.Integer> r6 = r8.pushDialogsOverrideMention
            java.lang.Object r6 = r6.get(r1)
            java.lang.Integer r6 = (java.lang.Integer) r6
            if (r6 == 0) goto L_0x0295
            int r7 = r6.intValue()
            if (r7 == 0) goto L_0x0295
            int r5 = r6.intValue()
            r12 = 1
            goto L_0x0296
        L_0x0295:
            r12 = r0
        L_0x0296:
            if (r12 == 0) goto L_0x02b1
            if (r4 == 0) goto L_0x02a3
            int r0 = r8.total_unread_count
            int r4 = r4.intValue()
            int r0 = r0 - r4
            r8.total_unread_count = r0
        L_0x02a3:
            int r0 = r8.total_unread_count
            int r0 = r0 + r5
            r8.total_unread_count = r0
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r0.put(r1, r4)
        L_0x02b1:
            int r0 = r8.total_unread_count
            if (r3 == r0) goto L_0x02cd
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.delayedPushMessages
            r0.clear()
            boolean r0 = r8.notifyCheck
            r8.showOrUpdateNotification(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            int r0 = r0.size()
            org.telegram.messenger.-$$Lambda$NotificationsController$8dgn4YYZ8Yk1zWGWaoyaxcMFn7c r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$8dgn4YYZ8Yk1zWGWaoyaxcMFn7c
            r1.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x02cd:
            r0 = 0
            r8.notifyCheck = r0
            boolean r0 = r8.showBadgeNumber
            if (r0 == 0) goto L_0x02db
            int r0 = r31.getTotalAllUnreadCount()
            r8.setBadge(r0)
        L_0x02db:
            if (r36 == 0) goto L_0x02e0
            r36.countDown()
        L_0x02e0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processNewMessages$17$NotificationsController(java.util.ArrayList, java.util.ArrayList, boolean, boolean, java.util.concurrent.CountDownLatch):void");
    }

    public /* synthetic */ void lambda$null$15$NotificationsController(ArrayList arrayList, int i) {
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

    public /* synthetic */ void lambda$null$16$NotificationsController(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(LongSparseArray<Integer> longSparseArray) {
        notificationsQueue.postRunnable(new Runnable(longSparseArray, new ArrayList()) {
            public final /* synthetic */ LongSparseArray f$1;
            public final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationsController.this.lambda$processDialogsUpdateRead$20$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$processDialogsUpdateRead$20$NotificationsController(LongSparseArray longSparseArray, ArrayList arrayList) {
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
                        int i4 = messageObject.messageOwner.peer_id.channel_id;
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
                public final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$18$NotificationsController(this.f$1);
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
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$19$NotificationsController(this.f$1);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$null$18$NotificationsController(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public /* synthetic */ void lambda$null$19$NotificationsController(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processLoadedUnreadMessages(LongSparseArray<Integer> longSparseArray, ArrayList<TLRPC$Message> arrayList, ArrayList<MessageObject> arrayList2, ArrayList<TLRPC$User> arrayList3, ArrayList<TLRPC$Chat> arrayList4, ArrayList<TLRPC$EncryptedChat> arrayList5) {
        getMessagesController().putUsers(arrayList3, true);
        getMessagesController().putChats(arrayList4, true);
        getMessagesController().putEncryptedChats(arrayList5, true);
        notificationsQueue.postRunnable(new Runnable(arrayList, longSparseArray, arrayList2) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ LongSparseArray f$2;
            public final /* synthetic */ ArrayList f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                NotificationsController.this.lambda$processLoadedUnreadMessages$22$NotificationsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0049, code lost:
        if ((r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) == false) goto L_0x004c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedUnreadMessages$22$NotificationsController(java.util.ArrayList r21, android.util.LongSparseArray r22, java.util.ArrayList r23) {
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
            if (r1 == 0) goto L_0x00fc
            r11 = 0
        L_0x002f:
            int r12 = r21.size()
            if (r11 >= r12) goto L_0x00fc
            java.lang.Object r12 = r1.get(r11)
            org.telegram.tgnet.TLRPC$Message r12 = (org.telegram.tgnet.TLRPC$Message) r12
            if (r12 == 0) goto L_0x004c
            boolean r13 = r12.silent
            if (r13 == 0) goto L_0x004c
            org.telegram.tgnet.TLRPC$MessageAction r13 = r12.action
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r14 != 0) goto L_0x0060
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r13 == 0) goto L_0x004c
            goto L_0x0060
        L_0x004c:
            int r13 = r12.id
            long r13 = (long) r13
            org.telegram.tgnet.TLRPC$Peer r15 = r12.peer_id
            int r15 = r15.channel_id
            if (r15 == 0) goto L_0x0058
            long r8 = (long) r15
            long r8 = r8 << r7
            long r13 = r13 | r8
        L_0x0058:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r8 = r0.pushMessagesDict
            int r8 = r8.indexOfKey(r13)
            if (r8 < 0) goto L_0x0064
        L_0x0060:
            r15 = r5
            r12 = r11
            goto L_0x00f4
        L_0x0064:
            org.telegram.messenger.MessageObject r8 = new org.telegram.messenger.MessageObject
            int r9 = r0.currentAccount
            r8.<init>(r9, r12, r4, r4)
            boolean r9 = r0.isPersonalMessage(r8)
            if (r9 == 0) goto L_0x0076
            int r9 = r0.personal_count
            int r9 = r9 + r10
            r0.personal_count = r9
        L_0x0076:
            r12 = r11
            long r10 = r8.getDialogId()
            org.telegram.tgnet.TLRPC$Message r15 = r8.messageOwner
            boolean r15 = r15.mentioned
            if (r15 == 0) goto L_0x0089
            int r15 = r8.getFromChatId()
            r17 = r10
            long r9 = (long) r15
            goto L_0x008d
        L_0x0089:
            r17 = r10
            r9 = r17
        L_0x008d:
            int r11 = r6.indexOfKey(r9)
            if (r11 < 0) goto L_0x009e
            java.lang.Object r11 = r6.valueAt(r11)
            java.lang.Boolean r11 = (java.lang.Boolean) r11
            boolean r11 = r11.booleanValue()
            goto L_0x00b7
        L_0x009e:
            int r11 = r0.getNotifyOverride(r5, r9)
            r15 = -1
            if (r11 != r15) goto L_0x00aa
            boolean r11 = r0.isGlobalNotificationsEnabled((long) r9)
            goto L_0x00b0
        L_0x00aa:
            r15 = 2
            if (r11 == r15) goto L_0x00af
            r11 = 1
            goto L_0x00b0
        L_0x00af:
            r11 = 0
        L_0x00b0:
            java.lang.Boolean r15 = java.lang.Boolean.valueOf(r11)
            r6.put(r9, r15)
        L_0x00b7:
            r15 = r5
            if (r11 == 0) goto L_0x00f4
            long r4 = r0.opened_dialog_id
            int r19 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r19 != 0) goto L_0x00c5
            boolean r4 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r4 == 0) goto L_0x00c5
            goto L_0x00f4
        L_0x00c5:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r4 = r0.pushMessagesDict
            r4.put(r13, r8)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.pushMessages
            r5 = 0
            r4.add(r5, r8)
            int r4 = (r17 > r9 ? 1 : (r17 == r9 ? 0 : -1))
            if (r4 == 0) goto L_0x00f4
            android.util.LongSparseArray<java.lang.Integer> r4 = r0.pushDialogsOverrideMention
            r13 = r17
            java.lang.Object r4 = r4.get(r13)
            java.lang.Integer r4 = (java.lang.Integer) r4
            android.util.LongSparseArray<java.lang.Integer> r5 = r0.pushDialogsOverrideMention
            if (r4 != 0) goto L_0x00e5
            r16 = 1
            goto L_0x00ed
        L_0x00e5:
            int r4 = r4.intValue()
            r8 = 1
            int r4 = r4 + r8
            r16 = r4
        L_0x00ed:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r16)
            r5.put(r13, r4)
        L_0x00f4:
            int r4 = r12 + 1
            r11 = r4
            r5 = r15
            r4 = 0
            r10 = 1
            goto L_0x002f
        L_0x00fc:
            r15 = r5
            r5 = 0
        L_0x00fe:
            int r1 = r22.size()
            if (r5 >= r1) goto L_0x0154
            long r12 = r2.keyAt(r5)
            int r1 = r6.indexOfKey(r12)
            if (r1 < 0) goto L_0x011b
            java.lang.Object r1 = r6.valueAt(r1)
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r4 = r1
            r1 = r15
            goto L_0x0135
        L_0x011b:
            r1 = r15
            int r4 = r0.getNotifyOverride(r1, r12)
            r8 = -1
            if (r4 != r8) goto L_0x0128
            boolean r4 = r0.isGlobalNotificationsEnabled((long) r12)
            goto L_0x012e
        L_0x0128:
            r8 = 2
            if (r4 == r8) goto L_0x012d
            r4 = 1
            goto L_0x012e
        L_0x012d:
            r4 = 0
        L_0x012e:
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r4)
            r6.put(r12, r8)
        L_0x0135:
            if (r4 != 0) goto L_0x0138
            goto L_0x0150
        L_0x0138:
            java.lang.Object r4 = r2.valueAt(r5)
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            android.util.LongSparseArray<java.lang.Integer> r8 = r0.pushDialogs
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            r8.put(r12, r10)
            int r8 = r0.total_unread_count
            int r8 = r8 + r4
            r0.total_unread_count = r8
        L_0x0150:
            int r5 = r5 + 1
            r15 = r1
            goto L_0x00fe
        L_0x0154:
            r1 = r15
            if (r3 == 0) goto L_0x024b
            r5 = 0
        L_0x0158:
            int r2 = r23.size()
            if (r5 >= r2) goto L_0x024b
            java.lang.Object r2 = r3.get(r5)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r4 = r2.getId()
            long r12 = (long) r4
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id
            int r4 = r4.channel_id
            if (r4 == 0) goto L_0x0174
            long r14 = (long) r4
            long r14 = r14 << r7
            long r12 = r12 | r14
        L_0x0174:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r4 = r0.pushMessagesDict
            int r4 = r4.indexOfKey(r12)
            if (r4 < 0) goto L_0x0181
        L_0x017c:
            r4 = 0
            r16 = 1
            goto L_0x0243
        L_0x0181:
            boolean r4 = r0.isPersonalMessage(r2)
            if (r4 == 0) goto L_0x018d
            int r4 = r0.personal_count
            r8 = 1
            int r4 = r4 + r8
            r0.personal_count = r4
        L_0x018d:
            long r14 = r2.getDialogId()
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
            long r7 = r4.random_id
            boolean r4 = r4.mentioned
            if (r4 == 0) goto L_0x019f
            int r4 = r2.getFromChatId()
            long r9 = (long) r4
            goto L_0x01a0
        L_0x019f:
            r9 = r14
        L_0x01a0:
            int r4 = r6.indexOfKey(r9)
            if (r4 < 0) goto L_0x01b1
            java.lang.Object r4 = r6.valueAt(r4)
            java.lang.Boolean r4 = (java.lang.Boolean) r4
            boolean r4 = r4.booleanValue()
            goto L_0x01ca
        L_0x01b1:
            int r4 = r0.getNotifyOverride(r1, r9)
            r11 = -1
            if (r4 != r11) goto L_0x01bd
            boolean r4 = r0.isGlobalNotificationsEnabled((long) r9)
            goto L_0x01c3
        L_0x01bd:
            r11 = 2
            if (r4 == r11) goto L_0x01c2
            r4 = 1
            goto L_0x01c3
        L_0x01c2:
            r4 = 0
        L_0x01c3:
            java.lang.Boolean r11 = java.lang.Boolean.valueOf(r4)
            r6.put(r9, r11)
        L_0x01ca:
            if (r4 == 0) goto L_0x017c
            long r3 = r0.opened_dialog_id
            int r11 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r11 != 0) goto L_0x01d7
            boolean r3 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r3 == 0) goto L_0x01d7
            goto L_0x017c
        L_0x01d7:
            r3 = 0
            int r11 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r11 == 0) goto L_0x01e3
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r0.pushMessagesDict
            r3.put(r12, r2)
            goto L_0x01ec
        L_0x01e3:
            int r11 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r11 == 0) goto L_0x01ec
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r0.fcmRandomMessagesDict
            r3.put(r7, r2)
        L_0x01ec:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.pushMessages
            r4 = 0
            r3.add(r4, r2)
            int r2 = (r14 > r9 ? 1 : (r14 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0216
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogsOverrideMention
            java.lang.Object r2 = r2.get(r14)
            java.lang.Integer r2 = (java.lang.Integer) r2
            android.util.LongSparseArray<java.lang.Integer> r3 = r0.pushDialogsOverrideMention
            if (r2 != 0) goto L_0x0206
            r2 = 1
            r16 = 1
            goto L_0x020e
        L_0x0206:
            int r2 = r2.intValue()
            r16 = 1
            int r2 = r2 + 1
        L_0x020e:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r3.put(r14, r2)
            goto L_0x0218
        L_0x0216:
            r16 = 1
        L_0x0218:
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogs
            java.lang.Object r2 = r2.get(r9)
            java.lang.Integer r2 = (java.lang.Integer) r2
            if (r2 == 0) goto L_0x0229
            int r3 = r2.intValue()
            int r3 = r3 + 1
            goto L_0x022a
        L_0x0229:
            r3 = 1
        L_0x022a:
            if (r2 == 0) goto L_0x0235
            int r7 = r0.total_unread_count
            int r2 = r2.intValue()
            int r7 = r7 - r2
            r0.total_unread_count = r7
        L_0x0235:
            int r2 = r0.total_unread_count
            int r2 = r2 + r3
            r0.total_unread_count = r2
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogs
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2.put(r9, r3)
        L_0x0243:
            int r5 = r5 + 1
            r3 = r23
            r7 = 32
            goto L_0x0158
        L_0x024b:
            r4 = 0
            r16 = 1
            android.util.LongSparseArray<java.lang.Integer> r1 = r0.pushDialogs
            int r1 = r1.size()
            org.telegram.messenger.-$$Lambda$NotificationsController$5iZSA4iiOYvgCSPcQ2XgPFuD-jI r2 = new org.telegram.messenger.-$$Lambda$NotificationsController$5iZSA4iiOYvgCSPcQ2XgPFuD-jI
            r2.<init>(r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            long r1 = android.os.SystemClock.elapsedRealtime()
            r5 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 / r5
            r5 = 60
            int r3 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r3 >= 0) goto L_0x026a
            r4 = 1
        L_0x026a:
            r0.showOrUpdateNotification(r4)
            boolean r1 = r0.showBadgeNumber
            if (r1 == 0) goto L_0x0278
            int r1 = r20.getTotalAllUnreadCount()
            r0.setBadge(r1)
        L_0x0278:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processLoadedUnreadMessages$22$NotificationsController(java.util.ArrayList, android.util.LongSparseArray, java.util.ArrayList):void");
    }

    public /* synthetic */ void lambda$null$21$NotificationsController(int i) {
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

    public /* synthetic */ void lambda$updateBadge$23$NotificationsController() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$updateBadge$23$NotificationsController();
            }
        });
    }

    private void setBadge(int i) {
        if (this.lastBadgeCount != i) {
            this.lastBadgeCount = i;
            NotificationBadge.applyCount(i);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:134:0x01f2, code lost:
        if (r9.getBoolean("EnablePreviewAll", true) == false) goto L_0x01f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x01fe, code lost:
        if (r9.getBoolean("EnablePreviewGroup", r4) != false) goto L_0x020a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0208, code lost:
        if (r9.getBoolean("EnablePreviewChannel", r4) != false) goto L_0x020a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x020a, code lost:
        r3 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x021c, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0ab6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x021e, code lost:
        r21[0] = null;
        r3 = r3.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0224, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L_0x0aa7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0228, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L_0x022c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x022e, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L_0x023f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x023e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", NUM, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x0242, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L_0x02a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0244, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", NUM, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(((long) r0.messageOwner.date) * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(((long) r0.messageOwner.date) * 1000));
        r0 = r0.messageOwner.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x02a0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", NUM, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x02a3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L_0x0aa0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x02a7, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L_0x02ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x02ad, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L_0x02c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x02b1, code lost:
        if (r3.video == false) goto L_0x02bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x02bc, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageVideoIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x02c6, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x02c9, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x03d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x02cb, code lost:
        r4 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02cd, code lost:
        if (r4 != 0) goto L_0x02e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02d6, code lost:
        if (r3.users.size() != 1) goto L_0x02e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x02d8, code lost:
        r4 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02e8, code lost:
        if (r4 == 0) goto L_0x0381;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x02f0, code lost:
        if (r0.messageOwner.peer_id.channel_id == 0) goto L_0x030a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x02f4, code lost:
        if (r2.megagroup != false) goto L_0x030a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x0309, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0312, code lost:
        if (r4 != getUserConfig().getClientUserId()) goto L_0x0328;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x0327, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x0328, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x0334, code lost:
        if (r0 != null) goto L_0x0337;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0336, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x0339, code lost:
        if (r1 != r0.id) goto L_0x0367;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x033d, code lost:
        if (r2.megagroup == false) goto L_0x0353;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0352, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x0366, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0380, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r7, r2.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0381, code lost:
        r1 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0391, code lost:
        if (r3 >= r0.messageOwner.action.users.size()) goto L_0x03be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x0393, code lost:
        r4 = getMessagesController().getUser(r0.messageOwner.action.users.get(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x03a7, code lost:
        if (r4 == null) goto L_0x03bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x03a9, code lost:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x03b1, code lost:
        if (r1.length() == 0) goto L_0x03b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x03b3, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x03b8, code lost:
        r1.append(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x03bb, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x03d7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r7, r2.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x03db, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x03f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x03ef, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03f3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x0407;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x0406, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r7, r3.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0409, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x040d, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x0411;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x0413, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x0480;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x041f, code lost:
        if (r3.user_id != getUserConfig().getClientUserId()) goto L_0x0435;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0434, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x043b, code lost:
        if (r0.messageOwner.action.user_id != r1) goto L_0x0451;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x0450, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x0451, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x0463, code lost:
        if (r0 != null) goto L_0x0466;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x0465, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x047f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r7, r2.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0482, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x048b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x048a, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x048d, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x0496;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x0495, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x0498, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x04ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x04aa, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x04ae, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x04c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04bf, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r3.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x04c2, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x04cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x04ca, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04cd, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0a3d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04d1, code lost:
        if (r2 == null) goto L_0x07a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x04d7, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r2) == false) goto L_0x04dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x04db, code lost:
        if (r2.megagroup == false) goto L_0x07a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x04dd, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x04df, code lost:
        if (r0 != null) goto L_0x04f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x04f4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x04fb, code lost:
        if (r0.isMusic() == false) goto L_0x050f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x050e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0518, code lost:
        if (r0.isVideo() == false) goto L_0x0560;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x051c, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x054c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0526, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x054c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x054b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "📹 " + r0.messageOwner.message, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x055f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x0564, code lost:
        if (r0.isGif() == false) goto L_0x05ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0568, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0598;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0572, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0598;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0597, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "🎬 " + r0.messageOwner.message, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x05ab, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x05b2, code lost:
        if (r0.isVoice() == false) goto L_0x05c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x05c5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x05ca, code lost:
        if (r0.isRoundVideo() == false) goto L_0x05de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x05dd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x05e2, code lost:
        if (r0.isSticker() != false) goto L_0x077a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x05e8, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x05ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x05ec, code lost:
        r3 = r0.messageOwner;
        r5 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x05f2, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0638;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x05f6, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0624;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05fe, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0624;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x0623, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "📎 " + r0.messageOwner.message, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x0637, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x063a, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0766;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x063e, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0642;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x0644, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x065a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x0659, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x065d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x067e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x065f, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x067d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r7, r2.title, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x0680, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x06ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x0682, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0688, code lost:
        if (r0.quiz == false) goto L_0x06a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x06a1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r7, r2.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x06b9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r7, r2.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x06bc, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0702;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x06c0, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x06ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x06c8, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x06ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x06ed, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "🖼 " + r0.messageOwner.message, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x0701, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x0706, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x071a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0719, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x071a, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x071c, code lost:
        if (r3 == null) goto L_0x0752;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0722, code lost:
        if (r3.length() <= 0) goto L_0x0752;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0724, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x072a, code lost:
        if (r0.length() <= 20) goto L_0x0741;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x072c, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x0751, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, r0, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0765, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x0779, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x077a, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x077f, code lost:
        if (r0 == null) goto L_0x0796;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x0795, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r7, r2.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x07a8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x07a9, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x07ac, code lost:
        if (r0 != null) goto L_0x07be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x07bd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x07c2, code lost:
        if (r0.isMusic() == false) goto L_0x07d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x07d3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x07dd, code lost:
        if (r0.isVideo() == false) goto L_0x0820;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x07e1, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x080f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x07eb, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x080f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x080e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r2.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x081f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x0824, code lost:
        if (r0.isGif() == false) goto L_0x0867;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0828, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0856;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x0832, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0856;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0855, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r2.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0866, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x086c, code lost:
        if (r0.isVoice() == false) goto L_0x087e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x087d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0882, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0894;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0893, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0898, code lost:
        if (r0.isSticker() != false) goto L_0x0a12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x089e, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x08a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x08a2, code lost:
        r3 = r0.messageOwner;
        r5 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x08a8, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x08e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x08ac, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x08d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x08b4, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x08d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x08d7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r2.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x08e8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x08eb, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0a01;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x08ef, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x08f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x08f5, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0908;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x0907, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x090a, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x092a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x090c, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0929, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r2.title, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x092c, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0962;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x092e, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0934, code lost:
        if (r0.quiz == false) goto L_0x094c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x094b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r2.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x0961, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r2.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x0964, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x09a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x0968, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0994;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x0970, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0994;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x0993, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r2.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x09a4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x09a8, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x09ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x09b9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x09ba, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x09bc, code lost:
        if (r3 == null) goto L_0x09f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x09c2, code lost:
        if (r3.length() <= 0) goto L_0x09f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x09c4, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x09ca, code lost:
        if (r0.length() <= 20) goto L_0x09e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x09cc, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x09ef, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r2.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0a00, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0a11, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0a12, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x0a16, code lost:
        if (r0 == null) goto L_0x0a2c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0a2b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r2.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0a3c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0a3d, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0a44, code lost:
        if (r0.messageOwner.peer_id.channel_id == 0) goto L_0x0a72;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0a48, code lost:
        if (r2.megagroup != false) goto L_0x0a72;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0a4e, code lost:
        if (r20.isVideoAvatar() == false) goto L_0x0a61;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0a60, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0a71, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0a76, code lost:
        if (r20.isVideoAvatar() == false) goto L_0x0a8c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0a8b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0a9f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0aa6, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0ab5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", NUM, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x0aba, code lost:
        if (r20.isMediaEmpty() == false) goto L_0x0ad3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0ac4, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0acb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x0aca, code lost:
        return r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x0ad2, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x0ad3, code lost:
        r1 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0ad9, code lost:
        if ((r1.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0b17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0add, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0ae5, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L_0x0afb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0afa, code lost:
        return "🖼 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x0b01, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0b0d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0b0c, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0b16, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x0b1b, code lost:
        if (r20.isVideo() == false) goto L_0x0b5b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0b1f, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0b3f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0b29, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0b3f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0b3e, code lost:
        return "📹 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0b45, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0b51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0b50, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0b5a, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0b5f, code lost:
        if (r20.isGame() == false) goto L_0x0b6b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0b6a, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0b6f, code lost:
        if (r20.isVoice() == false) goto L_0x0b7b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0b7a, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0b7f, code lost:
        if (r20.isRoundVideo() == false) goto L_0x0b8b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0b8a, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0b8f, code lost:
        if (r20.isMusic() == false) goto L_0x0b9b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0b9a, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x0b9b, code lost:
        r1 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0ba1, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0bad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0bac, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0baf, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0bcd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0bb7, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1).poll.quiz == false) goto L_0x0bc3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x0bc2, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0bcc, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0bcf, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0bd3, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0bd7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0bd9, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0be5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0be4, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x0be7, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0bed, code lost:
        if (r20.isSticker() != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0bf3, code lost:
        if (r20.isAnimatedSticker() == false) goto L_0x0bf6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0bfa, code lost:
        if (r20.isGif() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0bfe, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0c1e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0c1e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0c1d, code lost:
        return "🎬 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0c2a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0c4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0c4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0CLASSNAME, code lost:
        return "📎 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0CLASSNAME, code lost:
        r0 = r20.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0CLASSNAME, code lost:
        if (r0 == null) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0CLASSNAME, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0CLASSNAME, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0ca2, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x019c  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01a0 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r20, java.lang.String[] r21, boolean[] r22) {
        /*
            r19 = this;
            r0 = r20
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            java.lang.String r3 = "NotificationHiddenMessage"
            if (r1 != 0) goto L_0x0caf
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x0010
            goto L_0x0caf
        L_0x0010:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r4 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r6 = r1.chat_id
            if (r6 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            int r6 = r1.channel_id
        L_0x001d:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.user_id
            r7 = 1
            r8 = 0
            if (r22 == 0) goto L_0x0029
            r22[r8] = r7
        L_0x0029:
            org.telegram.messenger.AccountInstance r9 = r19.getAccountInstance()
            android.content.SharedPreferences r9 = r9.getNotificationsSettings()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "content_preview_"
            r10.append(r11)
            r10.append(r4)
            java.lang.String r10 = r10.toString()
            boolean r10 = r9.getBoolean(r10, r7)
            boolean r11 = r20.isFcmMessage()
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
            r21[r8] = r1
        L_0x0061:
            if (r10 == 0) goto L_0x0069
            boolean r1 = r9.getBoolean(r13, r7)
            if (r1 != 0) goto L_0x00e1
        L_0x0069:
            if (r22 == 0) goto L_0x006d
            r22[r8] = r8
        L_0x006d:
            r0 = 2131625839(0x7f0e076f, float:1.8878897E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0075:
            if (r6 == 0) goto L_0x00e1
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x008f
            boolean r1 = r20.isMegagroup()
            if (r1 == 0) goto L_0x0086
            goto L_0x008f
        L_0x0086:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 <= r2) goto L_0x0093
            java.lang.String r1 = r0.localName
            r21[r8] = r1
            goto L_0x0093
        L_0x008f:
            java.lang.String r1 = r0.localUserName
            r21[r8] = r1
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
            if (r22 == 0) goto L_0x00af
            r22[r8] = r8
        L_0x00af:
            boolean r1 = r20.isMegagroup()
            if (r1 != 0) goto L_0x00cd
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00cd
            r1 = 2131624649(0x7f0e02c9, float:1.8876484E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            java.lang.String r0 = r0.localName
            r2[r8] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00cd:
            r1 = 2131626099(0x7f0e0873, float:1.8879425E38)
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
            if (r1 != 0) goto L_0x00f0
            int r1 = r20.getFromChatId()
            if (r1 != 0) goto L_0x00fe
            int r1 = -r6
            goto L_0x00fe
        L_0x00f0:
            org.telegram.messenger.UserConfig r11 = r19.getUserConfig()
            int r11 = r11.getClientUserId()
            if (r1 != r11) goto L_0x00fe
            int r1 = r20.getFromChatId()
        L_0x00fe:
            r16 = 0
            int r11 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r11 != 0) goto L_0x010c
            if (r6 == 0) goto L_0x0109
            int r4 = -r6
            long r4 = (long) r4
            goto L_0x010c
        L_0x0109:
            if (r1 == 0) goto L_0x010c
            long r4 = (long) r1
        L_0x010c:
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r11 == 0) goto L_0x0120
            org.telegram.tgnet.TLRPC$Message r11 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r11 = r11.fwd_from
            if (r11 == 0) goto L_0x0120
            org.telegram.tgnet.TLRPC$Peer r11 = r11.from_id
            if (r11 == 0) goto L_0x0120
            int r1 = org.telegram.messenger.MessageObject.getPeerId(r11)
        L_0x0120:
            r11 = 0
            if (r1 <= 0) goto L_0x0144
            org.telegram.messenger.MessagesController r14 = r19.getMessagesController()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r7 = r14.getUser(r7)
            if (r7 == 0) goto L_0x0158
            java.lang.String r7 = org.telegram.messenger.UserObject.getUserName(r7)
            if (r6 == 0) goto L_0x013a
            r21[r8] = r7
            goto L_0x0159
        L_0x013a:
            int r14 = android.os.Build.VERSION.SDK_INT
            if (r14 <= r2) goto L_0x0141
            r21[r8] = r7
            goto L_0x0159
        L_0x0141:
            r21[r8] = r11
            goto L_0x0159
        L_0x0144:
            org.telegram.messenger.MessagesController r7 = r19.getMessagesController()
            int r14 = -r1
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r14)
            if (r7 == 0) goto L_0x0158
            java.lang.String r7 = r7.title
            r21[r8] = r7
            goto L_0x0159
        L_0x0158:
            r7 = r11
        L_0x0159:
            if (r7 == 0) goto L_0x019e
            if (r1 <= 0) goto L_0x019e
            boolean r14 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r14 == 0) goto L_0x019e
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            if (r14 == 0) goto L_0x019e
            org.telegram.tgnet.TLRPC$Peer r14 = r14.saved_from_peer
            if (r14 == 0) goto L_0x019e
            int r14 = org.telegram.messenger.MessageObject.getPeerId(r14)
            if (r14 >= 0) goto L_0x019e
            org.telegram.messenger.MessagesController r2 = r19.getMessagesController()
            int r14 = -r14
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r14)
            if (r2 == 0) goto L_0x019e
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r7)
            java.lang.String r7 = " @ "
            r14.append(r7)
            java.lang.String r2 = r2.title
            r14.append(r2)
            java.lang.String r7 = r14.toString()
            r2 = r21[r8]
            if (r2 == 0) goto L_0x019e
            r21[r8] = r7
        L_0x019e:
            if (r7 != 0) goto L_0x01a1
            return r11
        L_0x01a1:
            if (r6 == 0) goto L_0x01cc
            org.telegram.messenger.MessagesController r2 = r19.getMessagesController()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r14)
            if (r2 != 0) goto L_0x01b2
            return r11
        L_0x01b2:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r14 == 0) goto L_0x01c7
            boolean r14 = r2.megagroup
            if (r14 != 0) goto L_0x01c7
            int r14 = android.os.Build.VERSION.SDK_INT
            r18 = r2
            r2 = 27
            if (r14 > r2) goto L_0x01c9
            r21[r8] = r11
            goto L_0x01c9
        L_0x01c7:
            r18 = r2
        L_0x01c9:
            r2 = r18
            goto L_0x01cd
        L_0x01cc:
            r2 = r11
        L_0x01cd:
            int r5 = (int) r4
            if (r5 != 0) goto L_0x01da
            r21[r8] = r11
            r0 = 2131626076(0x7f0e085c, float:1.8879378E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            return r0
        L_0x01da:
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r3 == 0) goto L_0x01e6
            boolean r3 = r2.megagroup
            if (r3 != 0) goto L_0x01e6
            r3 = 1
            goto L_0x01e7
        L_0x01e6:
            r3 = 0
        L_0x01e7:
            if (r10 == 0) goto L_0x0ca3
            if (r6 != 0) goto L_0x01f5
            if (r1 == 0) goto L_0x01f5
            r4 = 1
            boolean r5 = r9.getBoolean(r13, r4)
            if (r5 != 0) goto L_0x020a
            goto L_0x01f6
        L_0x01f5:
            r4 = 1
        L_0x01f6:
            if (r6 == 0) goto L_0x0ca3
            if (r3 != 0) goto L_0x0200
            boolean r5 = r9.getBoolean(r12, r4)
            if (r5 != 0) goto L_0x020a
        L_0x0200:
            if (r3 == 0) goto L_0x0ca3
            java.lang.String r3 = "EnablePreviewChannel"
            boolean r3 = r9.getBoolean(r3, r4)
            if (r3 == 0) goto L_0x0ca3
        L_0x020a:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r5 = "🎬 "
            java.lang.String r6 = "📎 "
            java.lang.String r9 = "📹 "
            java.lang.String r10 = "🖼 "
            r12 = 19
            if (r4 == 0) goto L_0x0ab6
            r21[r8] = r11
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r4 != 0) goto L_0x0aa7
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r4 == 0) goto L_0x022c
            goto L_0x0aa7
        L_0x022c:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r4 == 0) goto L_0x023f
            r0 = 2131626061(0x7f0e084d, float:1.8879348E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r8] = r7
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x023f:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            r13 = 3
            if (r4 == 0) goto L_0x02a1
            r1 = 2131627665(0x7f0e0e91, float:1.88826E38)
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
            r3[r8] = r2
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
            r2 = 2131626126(0x7f0e088e, float:1.887948E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r5 = r19.getUserConfig()
            org.telegram.tgnet.TLRPC$User r5 = r5.getCurrentUser()
            java.lang.String r5 = r5.first_name
            r3[r8] = r5
            r3[r4] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r1 = r0.title
            r4 = 2
            r3[r4] = r1
            java.lang.String r0 = r0.address
            r3[r13] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x02a1:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r4 != 0) goto L_0x0aa0
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r4 == 0) goto L_0x02ab
            goto L_0x0aa0
        L_0x02ab:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r4 == 0) goto L_0x02c7
            boolean r0 = r3.video
            if (r0 == 0) goto L_0x02bd
            r0 = 2131624545(0x7f0e0261, float:1.8876273E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02bd:
            r0 = 2131624539(0x7f0e025b, float:1.887626E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02c7:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r4 == 0) goto L_0x03d8
            int r4 = r3.user_id
            if (r4 != 0) goto L_0x02e8
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            r5 = 1
            if (r3 != r5) goto L_0x02e8
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            java.lang.Object r3 = r3.get(r8)
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r4 = r3.intValue()
        L_0x02e8:
            if (r4 == 0) goto L_0x0381
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x030a
            boolean r0 = r2.megagroup
            if (r0 != 0) goto L_0x030a
            r0 = 2131624599(0x7f0e0297, float:1.8876382E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x030a:
            org.telegram.messenger.UserConfig r0 = r19.getUserConfig()
            int r0 = r0.getClientUserId()
            if (r4 != r0) goto L_0x0328
            r0 = 2131626078(0x7f0e085e, float:1.8879382E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0328:
            org.telegram.messenger.MessagesController r0 = r19.getMessagesController()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            if (r0 != 0) goto L_0x0337
            return r11
        L_0x0337:
            int r3 = r0.id
            if (r1 != r3) goto L_0x0367
            boolean r0 = r2.megagroup
            if (r0 == 0) goto L_0x0353
            r0 = 2131626067(0x7f0e0853, float:1.887936E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0353:
            r1 = 2
            r3 = 1
            r0 = 2131626066(0x7f0e0852, float:1.8879358E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0367:
            r3 = 1
            r1 = 2131626065(0x7f0e0851, float:1.8879356E38)
            java.lang.Object[] r4 = new java.lang.Object[r13]
            r4[r8] = r7
            java.lang.String r2 = r2.title
            r4[r3] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r4[r2] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r4)
            return r0
        L_0x0381:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = 0
        L_0x0387:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x03be
            org.telegram.messenger.MessagesController r4 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            java.util.ArrayList<java.lang.Integer> r5 = r5.users
            java.lang.Object r5 = r5.get(r3)
            java.lang.Integer r5 = (java.lang.Integer) r5
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 == 0) goto L_0x03bb
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r4)
            int r5 = r1.length()
            if (r5 == 0) goto L_0x03b8
            java.lang.String r5 = ", "
            r1.append(r5)
        L_0x03b8:
            r1.append(r4)
        L_0x03bb:
            int r3 = r3 + 1
            goto L_0x0387
        L_0x03be:
            r0 = 2131626065(0x7f0e0851, float:1.8879356E38)
            java.lang.Object[] r3 = new java.lang.Object[r13]
            r3[r8] = r7
            java.lang.String r2 = r2.title
            r4 = 1
            r3[r4] = r2
            java.lang.String r1 = r1.toString()
            r4 = 2
            r3[r4] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            return r0
        L_0x03d8:
            r4 = 2
            boolean r14 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r14 == 0) goto L_0x03f0
            r0 = 2131626079(0x7f0e085f, float:1.8879384E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r14 = 1
            r1[r14] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03f0:
            r14 = 1
            boolean r15 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r15 == 0) goto L_0x0407
            r0 = 2131626062(0x7f0e084e, float:1.887935E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r8] = r7
            java.lang.String r2 = r3.title
            r1[r14] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0407:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r4 != 0) goto L_0x0a3e
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r4 == 0) goto L_0x0411
            goto L_0x0a3e
        L_0x0411:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r4 == 0) goto L_0x0480
            int r3 = r3.user_id
            org.telegram.messenger.UserConfig r4 = r19.getUserConfig()
            int r4 = r4.getClientUserId()
            if (r3 != r4) goto L_0x0435
            r0 = 2131626072(0x7f0e0858, float:1.887937E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0435:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            int r3 = r3.user_id
            if (r3 != r1) goto L_0x0451
            r0 = 2131626073(0x7f0e0859, float:1.8879372E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0451:
            org.telegram.messenger.MessagesController r1 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x0466
            return r11
        L_0x0466:
            r1 = 2131626071(0x7f0e0857, float:1.8879368E38)
            java.lang.Object[] r3 = new java.lang.Object[r13]
            r3[r8] = r7
            java.lang.String r2 = r2.title
            r4 = 1
            r3[r4] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r3[r2] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x0480:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r1 == 0) goto L_0x048b
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x048b:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r1 == 0) goto L_0x0496
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0496:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r1 == 0) goto L_0x04ab
            r0 = 2131624110(0x7f0e00ae, float:1.887539E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04ab:
            r1 = 1
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r4 == 0) goto L_0x04c0
            r0 = 2131624110(0x7f0e00ae, float:1.887539E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r3.title
            r1[r8] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04c0:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r1 == 0) goto L_0x04cb
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x04cb:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r1 == 0) goto L_0x0a3d
            r1 = 20
            if (r2 == 0) goto L_0x07a9
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r3 == 0) goto L_0x04dd
            boolean r3 = r2.megagroup
            if (r3 == 0) goto L_0x07a9
        L_0x04dd:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x04f5
            r0 = 2131626040(0x7f0e0838, float:1.8879305E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04f5:
            r3 = 2
            r4 = 1
            boolean r11 = r0.isMusic()
            if (r11 == 0) goto L_0x050f
            r0 = 2131626038(0x7f0e0836, float:1.88793E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x050f:
            boolean r3 = r0.isVideo()
            r4 = 2131626054(0x7f0e0846, float:1.8879333E38)
            java.lang.String r11 = "NotificationActionPinnedText"
            if (r3 == 0) goto L_0x0560
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x054c
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x054c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r8] = r7
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = r2.title
            r5 = 2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r4, r1)
            return r0
        L_0x054c:
            r3 = 1
            r5 = 2
            r0 = 2131626056(0x7f0e0848, float:1.8879337E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0560:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x05ac
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x0598
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0598
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r8] = r7
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = r2.title
            r5 = 2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r4, r1)
            return r0
        L_0x0598:
            r3 = 1
            r5 = 2
            r0 = 2131626034(0x7f0e0832, float:1.8879293E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05ac:
            r3 = 1
            r5 = 2
            boolean r9 = r0.isVoice()
            if (r9 == 0) goto L_0x05c6
            r0 = 2131626058(0x7f0e084a, float:1.8879341E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05c6:
            boolean r9 = r0.isRoundVideo()
            if (r9 == 0) goto L_0x05de
            r0 = 2131626048(0x7f0e0840, float:1.8879321E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05de:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x077a
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x05ec
            goto L_0x077a
        L_0x05ec:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x0638
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x0624
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0624
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r8] = r7
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = r2.title
            r5 = 2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r4, r1)
            return r0
        L_0x0624:
            r3 = 1
            r5 = 2
            r0 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0638:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r6 != 0) goto L_0x0766
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r6 == 0) goto L_0x0642
            goto L_0x0766
        L_0x0642:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x065a
            r0 = 2131626032(0x7f0e0830, float:1.8879289E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r6 = 1
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x065a:
            r6 = 1
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r9 == 0) goto L_0x067e
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            r0 = 2131626022(0x7f0e0826, float:1.8879268E38)
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r6] = r2
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x067e:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x06ba
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x06a2
            r1 = 2131626046(0x7f0e083e, float:1.8879317E38)
            java.lang.Object[] r3 = new java.lang.Object[r13]
            r3[r8] = r7
            java.lang.String r2 = r2.title
            r4 = 1
            r3[r4] = r2
            java.lang.String r0 = r0.question
            r5 = 2
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x06a2:
            r4 = 1
            r5 = 2
            r1 = 2131626044(0x7f0e083c, float:1.8879313E38)
            java.lang.Object[] r3 = new java.lang.Object[r13]
            r3[r8] = r7
            java.lang.String r2 = r2.title
            r3[r4] = r2
            java.lang.String r0 = r0.question
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x06ba:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x0702
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x06ee
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x06ee
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r8] = r7
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = r2.title
            r6 = 2
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r4, r1)
            return r0
        L_0x06ee:
            r3 = 1
            r6 = 2
            r0 = 2131626042(0x7f0e083a, float:1.887931E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0702:
            r3 = 1
            r6 = 2
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r5 == 0) goto L_0x071a
            r0 = 2131626026(0x7f0e082a, float:1.8879277E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x071a:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x0752
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0752
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r1) goto L_0x0741
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r8, r1)
            r3.append(r0)
            java.lang.String r0 = "..."
            r3.append(r0)
            java.lang.String r0 = r3.toString()
        L_0x0741:
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r8] = r7
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = r2.title
            r5 = 2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r4, r1)
            return r0
        L_0x0752:
            r3 = 1
            r5 = 2
            r0 = 2131626040(0x7f0e0838, float:1.8879305E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0766:
            r3 = 1
            r5 = 2
            r0 = 2131626030(0x7f0e082e, float:1.8879285E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x077a:
            r3 = 1
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0796
            r1 = 2131626052(0x7f0e0844, float:1.887933E38)
            java.lang.Object[] r4 = new java.lang.Object[r13]
            r4[r8] = r7
            java.lang.String r2 = r2.title
            r4[r3] = r2
            r5 = 2
            r4[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r4)
            return r0
        L_0x0796:
            r5 = 2
            r0 = 2131626050(0x7f0e0842, float:1.8879325E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07a9:
            r3 = 1
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x07be
            r0 = 2131626041(0x7f0e0839, float:1.8879307E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07be:
            boolean r4 = r0.isMusic()
            if (r4 == 0) goto L_0x07d4
            r0 = 2131626039(0x7f0e0837, float:1.8879303E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07d4:
            boolean r3 = r0.isVideo()
            r4 = 2131626055(0x7f0e0847, float:1.8879335E38)
            java.lang.String r7 = "NotificationActionPinnedTextChannel"
            if (r3 == 0) goto L_0x0820
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x080f
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x080f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            return r0
        L_0x080f:
            r3 = 1
            r0 = 2131626057(0x7f0e0849, float:1.887934E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0820:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x0867
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x0856
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0856
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            return r0
        L_0x0856:
            r3 = 1
            r0 = 2131626035(0x7f0e0833, float:1.8879295E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0867:
            r3 = 1
            boolean r5 = r0.isVoice()
            if (r5 == 0) goto L_0x087e
            r0 = 2131626059(0x7f0e084b, float:1.8879343E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x087e:
            boolean r5 = r0.isRoundVideo()
            if (r5 == 0) goto L_0x0894
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0894:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x0a12
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x08a2
            goto L_0x0a12
        L_0x08a2:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x08e9
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x08d8
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x08d8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            return r0
        L_0x08d8:
            r3 = 1
            r0 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08e9:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r6 != 0) goto L_0x0a01
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r6 == 0) goto L_0x08f3
            goto L_0x0a01
        L_0x08f3:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x0908
            r0 = 2131626033(0x7f0e0831, float:1.887929E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0908:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r6 == 0) goto L_0x092a
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            r0 = 2131626023(0x7f0e0827, float:1.887927E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x092a:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x0962
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x094c
            r1 = 2131626047(0x7f0e083f, float:1.887932E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r3[r8] = r2
            java.lang.String r0 = r0.question
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x094c:
            r3 = 2
            r4 = 1
            r1 = 2131626045(0x7f0e083d, float:1.8879315E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r3[r8] = r2
            java.lang.String r0 = r0.question
            r3[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x0962:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x09a5
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x0994
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0994
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            return r0
        L_0x0994:
            r3 = 1
            r0 = 2131626043(0x7f0e083b, float:1.8879311E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09a5:
            r3 = 1
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r5 == 0) goto L_0x09ba
            r0 = 2131626027(0x7f0e082b, float:1.8879279E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09ba:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x09f0
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x09f0
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r1) goto L_0x09e1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r8, r1)
            r3.append(r0)
            java.lang.String r0 = "..."
            r3.append(r0)
            java.lang.String r0 = r3.toString()
        L_0x09e1:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            return r0
        L_0x09f0:
            r3 = 1
            r0 = 2131626041(0x7f0e0839, float:1.8879307E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a01:
            r3 = 1
            r0 = 2131626031(0x7f0e082f, float:1.8879287E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a12:
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0a2c
            r1 = 2131626053(0x7f0e0845, float:1.8879331E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r2 = r2.title
            r3[r8] = r2
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x0a2c:
            r4 = 1
            r0 = 2131626051(0x7f0e0843, float:1.8879327E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a3d:
            return r11
        L_0x0a3e:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0a72
            boolean r1 = r2.megagroup
            if (r1 != 0) goto L_0x0a72
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0a61
            r0 = 2131624699(0x7f0e02fb, float:1.8876585E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a61:
            r1 = 1
            r0 = 2131624665(0x7f0e02d9, float:1.8876516E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r8] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a72:
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0a8c
            r0 = 2131626064(0x7f0e0850, float:1.8879354E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a8c:
            r1 = 2
            r3 = 1
            r0 = 2131626063(0x7f0e084f, float:1.8879352E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r8] = r7
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0aa0:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0aa7:
            r3 = 1
            r0 = 2131626060(0x7f0e084c, float:1.8879346E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r8] = r7
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ab6:
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x0ad3
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0acb
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x0acb:
            r0 = 2131625839(0x7f0e076f, float:1.8878897E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0ad3:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0b17
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r12) goto L_0x0afb
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0afb
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0afb:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0b0d
            r0 = 2131624336(0x7f0e0190, float:1.8875849E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b0d:
            r0 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b17:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x0b5b
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x0b3f
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b3f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0b3f:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0b51
            r0 = 2131624337(0x7f0e0191, float:1.887585E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b51:
            r0 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b5b:
            boolean r1 = r20.isGame()
            if (r1 == 0) goto L_0x0b6b
            r0 = 2131624339(0x7f0e0193, float:1.8875855E38)
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b6b:
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x0b7b
            r0 = 2131624333(0x7f0e018d, float:1.8875843E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b7b:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x0b8b
            r0 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b8b:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x0b9b
            r0 = 2131624350(0x7f0e019e, float:1.8875877E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b9b:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r2 == 0) goto L_0x0bad
            r0 = 2131624335(0x7f0e018f, float:1.8875847E38)
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0bad:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0bcd
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0bc3
            r0 = 2131626677(0x7f0e0ab5, float:1.8880597E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0bc3:
            r0 = 2131626585(0x7f0e0a59, float:1.888041E38)
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0bcd:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r2 != 0) goto L_0x0CLASSNAME
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r2 == 0) goto L_0x0bd7
            goto L_0x0CLASSNAME
        L_0x0bd7:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x0be5
            r0 = 2131624345(0x7f0e0199, float:1.8875867E38)
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0be5:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x0CLASSNAME
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x0CLASSNAME
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x0bf6
            goto L_0x0CLASSNAME
        L_0x0bf6:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x0CLASSNAME
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x0c1e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0c1e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0c1e:
            r0 = 2131624340(0x7f0e0194, float:1.8875857E38)
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0CLASSNAME:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r12) goto L_0x0c4a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0c4a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0c4a:
            r0 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0CLASSNAME:
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " "
            r1.append(r0)
            r0 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0CLASSNAME:
            r0 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r1 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0CLASSNAME:
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0CLASSNAME
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0CLASSNAME:
            r0 = 2131625839(0x7f0e076f, float:1.8878897E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0CLASSNAME:
            r0 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ca3:
            if (r22 == 0) goto L_0x0ca7
            r22[r8] = r8
        L_0x0ca7:
            r0 = 2131625839(0x7f0e076f, float:1.8878897E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0caf:
            r0 = 2131626076(0x7f0e085c, float:1.8879378E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:72:0x013c A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x013d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r20, boolean r21, boolean[] r22, boolean[] r23) {
        /*
            r19 = this;
            r0 = r20
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x1384
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x1384
        L_0x000e:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r2 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r4 = r1.chat_id
            if (r4 == 0) goto L_0x0019
            goto L_0x001b
        L_0x0019:
            int r4 = r1.channel_id
        L_0x001b:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
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
            r10 = 2131626099(0x7f0e0873, float:1.8879425E38)
            java.lang.String r11 = "NotificationMessageGroupNoText"
            r12 = 2131626112(0x7f0e0880, float:1.887945E38)
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
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00b0
            r1 = 2131624649(0x7f0e02c9, float:1.8876484E38)
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
            if (r1 != 0) goto L_0x00d8
            int r1 = r20.getFromChatId()
            if (r1 != 0) goto L_0x00de
            int r1 = -r4
            goto L_0x00de
        L_0x00d8:
            if (r1 != r9) goto L_0x00de
            int r1 = r20.getFromChatId()
        L_0x00de:
            r15 = 0
            int r17 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1))
            if (r17 != 0) goto L_0x00ec
            if (r4 == 0) goto L_0x00e9
            int r2 = -r4
            long r2 = (long) r2
            goto L_0x00ec
        L_0x00e9:
            if (r1 == 0) goto L_0x00ec
            long r2 = (long) r1
        L_0x00ec:
            r15 = 0
            if (r1 <= 0) goto L_0x0125
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            boolean r10 = r10.from_scheduled
            if (r10 == 0) goto L_0x0110
            r17 = r13
            long r12 = (long) r9
            int r18 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r18 != 0) goto L_0x0106
            r12 = 2131625856(0x7f0e0780, float:1.8878932E38)
            java.lang.String r13 = "MessageScheduledReminderNotification"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            goto L_0x013a
        L_0x0106:
            r12 = 2131626120(0x7f0e0888, float:1.8879467E38)
            java.lang.String r13 = "NotificationMessageScheduledName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            goto L_0x013a
        L_0x0110:
            r17 = r13
            org.telegram.messenger.MessagesController r12 = r19.getMessagesController()
            java.lang.Integer r13 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r12 = r12.getUser(r13)
            if (r12 == 0) goto L_0x0139
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r12)
            goto L_0x013a
        L_0x0125:
            r17 = r13
            org.telegram.messenger.MessagesController r12 = r19.getMessagesController()
            int r13 = -r1
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r12 = r12.getChat(r13)
            if (r12 == 0) goto L_0x0139
            java.lang.String r12 = r12.title
            goto L_0x013a
        L_0x0139:
            r12 = r15
        L_0x013a:
            if (r12 != 0) goto L_0x013d
            return r15
        L_0x013d:
            if (r4 == 0) goto L_0x014e
            org.telegram.messenger.MessagesController r13 = r19.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r10 = r13.getChat(r10)
            if (r10 != 0) goto L_0x014f
            return r15
        L_0x014e:
            r10 = r15
        L_0x014f:
            int r3 = (int) r2
            if (r3 != 0) goto L_0x015d
            r0 = 2131627597(0x7f0e0e4d, float:1.8882463E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1383
        L_0x015d:
            java.lang.String r2 = "🎬 "
            java.lang.String r3 = "📎 "
            java.lang.String r13 = "📹 "
            java.lang.String r15 = "🖼 "
            java.lang.String r14 = "NotificationMessageText"
            r6 = 3
            if (r4 != 0) goto L_0x0551
            if (r1 == 0) goto L_0x0551
            if (r8 == 0) goto L_0x053d
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 == 0) goto L_0x053d
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r4 == 0) goto L_0x0242
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r2 != 0) goto L_0x0232
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r2 == 0) goto L_0x018c
            goto L_0x0232
        L_0x018c:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r2 == 0) goto L_0x01a0
            r0 = 2131626061(0x7f0e084d, float:1.8879348E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x01a0:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r2 == 0) goto L_0x0203
            r1 = 2131627665(0x7f0e0e91, float:1.88826E38)
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
            r2 = 2131626126(0x7f0e088e, float:1.887948E38)
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
            goto L_0x1383
        L_0x0203:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 != 0) goto L_0x022a
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r2 == 0) goto L_0x020c
            goto L_0x022a
        L_0x020c:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x1381
            boolean r0 = r1.video
            if (r0 == 0) goto L_0x021f
            r0 = 2131624545(0x7f0e0261, float:1.8876273E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1383
        L_0x021f:
            r0 = 2131624539(0x7f0e025b, float:1.887626E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1383
        L_0x022a:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1383
        L_0x0232:
            r0 = 2131626060(0x7f0e084c, float:1.8879346E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0242:
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x028b
            if (r21 != 0) goto L_0x027b
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x026b
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1383
        L_0x026b:
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r4 = r17
            r1 = 2131626112(0x7f0e0880, float:1.887945E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1383
        L_0x027b:
            r4 = r17
            r1 = 2131626112(0x7f0e0880, float:1.887945E38)
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1383
        L_0x028b:
            r4 = r17
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r1.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x02f2
            if (r21 != 0) goto L_0x02cb
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x02cb
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x02cb
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
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1383
        L_0x02cb:
            r2 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x02e3
            r0 = 2131626117(0x7f0e0885, float:1.8879461E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageSDPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x02e3:
            r0 = 2131626113(0x7f0e0881, float:1.8879453E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x02f2:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x0357
            if (r21 != 0) goto L_0x0330
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0330
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0330
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
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r6] = r5
            goto L_0x1383
        L_0x0330:
            r6 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0348
            r0 = 2131626118(0x7f0e0886, float:1.8879463E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageSDVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0348:
            r0 = 2131626124(0x7f0e088c, float:1.8879475E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0357:
            r6 = 0
            boolean r1 = r20.isGame()
            if (r1 == 0) goto L_0x0378
            r1 = 2131626086(0x7f0e0866, float:1.8879398E38)
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
            goto L_0x1383
        L_0x0378:
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x038e
            r0 = 2131626081(0x7f0e0861, float:1.8879388E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r6 = 0
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x038e:
            r6 = 0
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x03a4
            r0 = 2131626116(0x7f0e0884, float:1.887946E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x03a4:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x03b9
            r0 = 2131626111(0x7f0e087f, float:1.8879449E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x03b9:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x03dd
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131626082(0x7f0e0862, float:1.887939E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1383
        L_0x03dd:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x0413
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x03fd
            r1 = 2131626115(0x7f0e0883, float:1.8879457E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0410
        L_0x03fd:
            r2 = 2
            r3 = 0
            r1 = 2131626114(0x7f0e0882, float:1.8879455E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
        L_0x0410:
            r15 = r0
            goto L_0x1383
        L_0x0413:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r6 != 0) goto L_0x052d
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r6 == 0) goto L_0x041d
            goto L_0x052d
        L_0x041d:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x0431
            r0 = 2131626109(0x7f0e087d, float:1.8879445E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0431:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x0501
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x04d9
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x0443
            goto L_0x04d9
        L_0x0443:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x0491
            if (r21 != 0) goto L_0x0481
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x0481
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0481
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
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r3] = r5
            goto L_0x1383
        L_0x0481:
            r3 = 0
            r0 = 2131626088(0x7f0e0868, float:1.8879402E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r12
            java.lang.String r2 = "NotificationMessageGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0491:
            if (r21 != 0) goto L_0x04c9
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x04c9
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x04c9
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
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1383
        L_0x04c9:
            r2 = 0
            r0 = 2131626083(0x7f0e0863, float:1.8879392E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x04d9:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x04f2
            r1 = 2131626122(0x7f0e088a, float:1.8879471E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r12
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0410
        L_0x04f2:
            r0 = 2131626121(0x7f0e0889, float:1.887947E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0501:
            r2 = 0
            if (r21 != 0) goto L_0x0520
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0520
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.CharSequence r0 = r0.messageText
            r1[r5] = r0
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1383
        L_0x0520:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r1 = 2131626112(0x7f0e0880, float:1.887945E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1383
        L_0x052d:
            r2 = 0
            r0 = 2131626110(0x7f0e087e, float:1.8879447E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x053d:
            r4 = r17
            r2 = 0
            if (r23 == 0) goto L_0x0544
            r23[r2] = r2
        L_0x0544:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r1 = 2131626112(0x7f0e0880, float:1.887945E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1383
        L_0x0551:
            if (r4 == 0) goto L_0x1381
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r4 == 0) goto L_0x055f
            boolean r4 = r10.megagroup
            if (r4 != 0) goto L_0x055f
            r4 = 1
            goto L_0x0560
        L_0x055f:
            r4 = 0
        L_0x0560:
            if (r8 == 0) goto L_0x1353
            if (r4 != 0) goto L_0x056c
            java.lang.String r8 = "EnablePreviewGroup"
            boolean r8 = r7.getBoolean(r8, r5)
            if (r8 != 0) goto L_0x0576
        L_0x056c:
            if (r4 == 0) goto L_0x1353
            java.lang.String r4 = "EnablePreviewChannel"
            boolean r4 = r7.getBoolean(r4, r5)
            if (r4 == 0) goto L_0x1353
        L_0x0576:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r7 == 0) goto L_0x0db0
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r7 == 0) goto L_0x068e
            int r2 = r4.user_id
            if (r2 != 0) goto L_0x059f
            java.util.ArrayList<java.lang.Integer> r3 = r4.users
            int r3 = r3.size()
            if (r3 != r5) goto L_0x059f
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
        L_0x059f:
            if (r2 == 0) goto L_0x0636
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x05c2
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x05c2
            r0 = 2131624599(0x7f0e0297, float:1.8876382E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x05c2:
            r3 = 2
            r4 = 0
            if (r2 != r9) goto L_0x05d9
            r0 = 2131626078(0x7f0e085e, float:1.8879382E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x05d9:
            org.telegram.messenger.MessagesController r0 = r19.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x05e9
            r2 = 0
            return r2
        L_0x05e9:
            int r2 = r0.id
            if (r1 != r2) goto L_0x061b
            boolean r0 = r10.megagroup
            if (r0 == 0) goto L_0x0606
            r0 = 2131626067(0x7f0e0853, float:1.887936E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0606:
            r1 = 2
            r2 = 0
            r0 = 2131626066(0x7f0e0852, float:1.8879358E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x061b:
            r2 = 0
            r1 = 2131626065(0x7f0e0851, float:1.8879356E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r2] = r12
            java.lang.String r2 = r10.title
            r3[r5] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r3[r2] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0410
        L_0x0636:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x063c:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0673
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0670
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x066d
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x066d:
            r1.append(r3)
        L_0x0670:
            int r2 = r2 + 1
            goto L_0x063c
        L_0x0673:
            r0 = 2131626065(0x7f0e0851, float:1.8879356E38)
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
            goto L_0x0410
        L_0x068e:
            r7 = 2
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r8 == 0) goto L_0x06a7
            r0 = 2131626079(0x7f0e085f, float:1.8879384E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r8 = 0
            r1[r8] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x06a7:
            r8 = 0
            boolean r11 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r11 == 0) goto L_0x06bf
            r0 = 2131626062(0x7f0e084e, float:1.887935E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r8] = r12
            java.lang.String r2 = r4.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x06bf:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r7 != 0) goto L_0x0d4b
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r7 == 0) goto L_0x06c9
            goto L_0x0d4b
        L_0x06c9:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r7 == 0) goto L_0x072e
            int r2 = r4.user_id
            if (r2 != r9) goto L_0x06e6
            r0 = 2131626072(0x7f0e0858, float:1.887937E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x06e6:
            r3 = 2
            r4 = 0
            if (r2 != r1) goto L_0x06fd
            r0 = 2131626073(0x7f0e0859, float:1.8879372E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x06fd:
            org.telegram.messenger.MessagesController r1 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x0713
            r1 = 0
            return r1
        L_0x0713:
            r1 = 2131626071(0x7f0e0857, float:1.8879368E38)
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
            goto L_0x1383
        L_0x072e:
            r1 = 0
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r7 == 0) goto L_0x073b
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1383
        L_0x073b:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r7 == 0) goto L_0x0747
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1383
        L_0x0747:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r7 == 0) goto L_0x075d
            r0 = 2131624110(0x7f0e00ae, float:1.887539E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r7 = 0
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x075d:
            r7 = 0
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r8 == 0) goto L_0x0773
            r0 = 2131624110(0x7f0e00ae, float:1.887539E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r4.title
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0773:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r7 == 0) goto L_0x077f
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1383
        L_0x077f:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r7 == 0) goto L_0x0d3f
            if (r10 == 0) goto L_0x0a83
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r1 == 0) goto L_0x078f
            boolean r1 = r10.megagroup
            if (r1 == 0) goto L_0x0a83
        L_0x078f:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x07a8
            r0 = 2131626040(0x7f0e0838, float:1.8879305E38)
            r4 = 2
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r7 = 0
            r1[r7] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x07a8:
            r4 = 2
            r7 = 0
            boolean r8 = r1.isMusic()
            if (r8 == 0) goto L_0x07c3
            r0 = 2131626038(0x7f0e0836, float:1.88793E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x07c3:
            boolean r4 = r1.isVideo()
            r7 = 2131626054(0x7f0e0846, float:1.8879333E38)
            java.lang.String r8 = "NotificationActionPinnedText"
            if (r4 == 0) goto L_0x0818
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x0803
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0803
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
            goto L_0x0410
        L_0x0803:
            r2 = 0
            r3 = 2
            r0 = 2131626056(0x7f0e0848, float:1.8879337E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0818:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0868
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0853
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0853
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
            goto L_0x0410
        L_0x0853:
            r2 = 0
            r4 = 2
            r0 = 2131626034(0x7f0e0832, float:1.8879293E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0868:
            r2 = 0
            r4 = 2
            boolean r9 = r1.isVoice()
            if (r9 == 0) goto L_0x0883
            r0 = 2131626058(0x7f0e084a, float:1.8879341E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0883:
            boolean r9 = r1.isRoundVideo()
            if (r9 == 0) goto L_0x089c
            r0 = 2131626048(0x7f0e0840, float:1.8879321E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x089c:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0a52
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x08aa
            goto L_0x0a52
        L_0x08aa:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x08fa
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x08e5
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08e5
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
            goto L_0x0410
        L_0x08e5:
            r2 = 0
            r3 = 2
            r0 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x08fa:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0a3d
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x0904
            goto L_0x0a3d
        L_0x0904:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x091d
            r0 = 2131626032(0x7f0e0830, float:1.8879289E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x091d:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0946
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626022(0x7f0e0826, float:1.8879268E38)
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
            goto L_0x0410
        L_0x0946:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0984
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x096b
            r1 = 2131626046(0x7f0e083e, float:1.8879317E38)
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
            goto L_0x0410
        L_0x096b:
            r3 = 0
            r4 = 2
            r1 = 2131626044(0x7f0e083c, float:1.8879313E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0410
        L_0x0984:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x09d0
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x09bb
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09bb
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
            goto L_0x0410
        L_0x09bb:
            r2 = 0
            r3 = 2
            r0 = 2131626042(0x7f0e083a, float:1.887931E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x09d0:
            r2 = 0
            r3 = 2
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x09e9
            r0 = 2131626026(0x7f0e082a, float:1.8879277E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x09e9:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0a28
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0a28
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0a16
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0a17
        L_0x0a16:
            r3 = 0
        L_0x0a17:
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r12
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r2 = 2
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x0410
        L_0x0a28:
            r2 = 2
            r3 = 0
            r0 = 2131626040(0x7f0e0838, float:1.8879305E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0a3d:
            r2 = 2
            r3 = 0
            r0 = 2131626030(0x7f0e082e, float:1.8879285E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0a52:
            r3 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0a6f
            r1 = 2131626052(0x7f0e0844, float:1.887933E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0410
        L_0x0a6f:
            r4 = 2
            r0 = 2131626050(0x7f0e0842, float:1.8879325E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0a83:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x0a99
            r0 = 2131626041(0x7f0e0839, float:1.8879307E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r4 = 0
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0a99:
            r4 = 0
            boolean r6 = r1.isMusic()
            if (r6 == 0) goto L_0x0ab1
            r0 = 2131626039(0x7f0e0837, float:1.8879303E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0ab1:
            boolean r4 = r1.isVideo()
            r6 = 2131626055(0x7f0e0847, float:1.8879335E38)
            java.lang.String r7 = "NotificationActionPinnedTextChannel"
            if (r4 == 0) goto L_0x0b01
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x0aef
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0aef
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
            goto L_0x0410
        L_0x0aef:
            r3 = 0
            r0 = 2131626057(0x7f0e0849, float:1.887934E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0b01:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0b4c
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0b3a
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0b3a
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
            goto L_0x0410
        L_0x0b3a:
            r4 = 0
            r0 = 2131626035(0x7f0e0833, float:1.8879295E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0b4c:
            r4 = 0
            boolean r2 = r1.isVoice()
            if (r2 == 0) goto L_0x0b64
            r0 = 2131626059(0x7f0e084b, float:1.8879343E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0b64:
            boolean r2 = r1.isRoundVideo()
            if (r2 == 0) goto L_0x0b7b
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0b7b:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0d13
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x0b89
            goto L_0x0d13
        L_0x0b89:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0bd4
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x0bc2
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0bc2
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
            goto L_0x0410
        L_0x0bc2:
            r3 = 0
            r0 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0bd4:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0d01
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x0bde
            goto L_0x0d01
        L_0x0bde:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0bf4
            r0 = 2131626033(0x7f0e0831, float:1.887929E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0bf4:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0c1b
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626023(0x7f0e0827, float:1.887927E38)
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
            goto L_0x0410
        L_0x0c1b:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0c3e
            r1 = 2131626047(0x7f0e083f, float:1.887932E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0410
        L_0x0c3e:
            r2 = 2
            r4 = 0
            r1 = 2131626045(0x7f0e083d, float:1.8879315E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0410
        L_0x0CLASSNAME:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0c9c
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0c8a
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0c8a
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
            goto L_0x0410
        L_0x0c8a:
            r3 = 0
            r0 = 2131626043(0x7f0e083b, float:1.8879311E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0c9c:
            r3 = 0
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0cb2
            r0 = 2131626027(0x7f0e082b, float:1.8879279E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0cb2:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0cef
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0cef
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0cdf
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0ce0
        L_0x0cdf:
            r3 = 0
        L_0x0ce0:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            goto L_0x0410
        L_0x0cef:
            r3 = 0
            r0 = 2131626041(0x7f0e0839, float:1.8879307E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0d01:
            r3 = 0
            r0 = 2131626031(0x7f0e082f, float:1.8879287E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0d13:
            r3 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0d2e
            r1 = 2131626053(0x7f0e0845, float:1.8879331E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r10.title
            r2[r3] = r4
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0410
        L_0x0d2e:
            r0 = 2131626051(0x7f0e0843, float:1.8879327E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x0d3f:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 == 0) goto L_0x1382
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1383
        L_0x0d4b:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0d81
            boolean r1 = r10.megagroup
            if (r1 != 0) goto L_0x0d81
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0d6f
            r0 = 2131624699(0x7f0e02fb, float:1.8876585E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0d6f:
            r3 = 0
            r0 = 2131624665(0x7f0e02d9, float:1.8876516E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0d81:
            r3 = 0
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0d9c
            r0 = 2131626064(0x7f0e0850, float:1.8879354E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0d9c:
            r1 = 2
            r0 = 2131626063(0x7f0e084f, float:1.8879352E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0db0:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r1 == 0) goto L_0x1055
            boolean r1 = r10.megagroup
            if (r1 != 0) goto L_0x1055
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x0df3
            if (r21 != 0) goto L_0x0de3
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0de3
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1383
        L_0x0de3:
            r2 = 0
            r0 = 2131624649(0x7f0e02c9, float:1.8876484E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0df3:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0e41
            if (r21 != 0) goto L_0x0e31
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0e31
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e31
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
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1383
        L_0x0e31:
            r2 = 0
            r0 = 2131624650(0x7f0e02ca, float:1.8876486E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0e41:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x0e8f
            if (r21 != 0) goto L_0x0e7f
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0e7f
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e7f
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
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r4] = r5
            goto L_0x1383
        L_0x0e7f:
            r4 = 0
            r0 = 2131624656(0x7f0e02d0, float:1.8876498E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0e8f:
            r4 = 0
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x0ea5
            r0 = 2131624641(0x7f0e02c1, float:1.8876467E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0ea5:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x0eba
            r0 = 2131624653(0x7f0e02cd, float:1.8876492E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0eba:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x0ecf
            r0 = 2131624648(0x7f0e02c8, float:1.8876482E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0ecf:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r6 == 0) goto L_0x0ef3
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131624642(0x7f0e02c2, float:1.887647E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "ChannelMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1383
        L_0x0ef3:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0var_
            r1 = 2131624652(0x7f0e02cc, float:1.887649E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0410
        L_0x0var_:
            r2 = 2
            r3 = 0
            r1 = 2131624651(0x7f0e02cb, float:1.8876488E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0410
        L_0x0var_:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x1045
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x0var_
            goto L_0x1045
        L_0x0var_:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x0var_
            r0 = 2131624646(0x7f0e02c6, float:1.8876478E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0var_:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x1017
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x0fef
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x0var_
            goto L_0x0fef
        L_0x0var_:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x0fa7
            if (r21 != 0) goto L_0x0var_
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0var_
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
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r3] = r5
            goto L_0x1383
        L_0x0var_:
            r3 = 0
            r0 = 2131624645(0x7f0e02c5, float:1.8876476E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r12
            java.lang.String r2 = "ChannelMessageGIF"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0fa7:
            if (r21 != 0) goto L_0x0fdf
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0fdf
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0fdf
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
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1383
        L_0x0fdf:
            r2 = 0
            r0 = 2131624643(0x7f0e02c3, float:1.8876472E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x0fef:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x1008
            r1 = 2131624655(0x7f0e02cf, float:1.8876496E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r12
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0410
        L_0x1008:
            r0 = 2131624654(0x7f0e02ce, float:1.8876494E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x1017:
            r2 = 0
            if (r21 != 0) goto L_0x1036
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1036
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.CharSequence r0 = r0.messageText
            r1[r5] = r0
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1383
        L_0x1036:
            r0 = 2131624649(0x7f0e02c9, float:1.8876484E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x1045:
            r2 = 0
            r0 = 2131624647(0x7f0e02c7, float:1.887648E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x1055:
            boolean r1 = r20.isMediaEmpty()
            r4 = 2131626106(0x7f0e087a, float:1.8879439E38)
            java.lang.String r7 = "NotificationMessageGroupText"
            if (r1 == 0) goto L_0x1095
            if (r21 != 0) goto L_0x1082
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1082
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
            goto L_0x1383
        L_0x1082:
            r2 = 0
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626099(0x7f0e0873, float:1.8879425E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x1383
        L_0x1095:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x10e7
            if (r21 != 0) goto L_0x10d2
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x10d2
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x10d2
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
            goto L_0x1383
        L_0x10d2:
            r2 = 2
            r0 = 2131626100(0x7f0e0874, float:1.8879427E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x10e7:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x1139
            if (r21 != 0) goto L_0x1124
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x1124
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1124
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
            goto L_0x1383
        L_0x1124:
            r8 = 2
            r0 = 2131626107(0x7f0e087b, float:1.887944E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r9 = 0
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = " "
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x1139:
            r8 = 2
            r9 = 0
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x1154
            r0 = 2131626089(0x7f0e0869, float:1.8879404E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x1154:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x116d
            r0 = 2131626103(0x7f0e0877, float:1.8879433E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x116d:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x1186
            r0 = 2131626098(0x7f0e0872, float:1.8879423E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x1186:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x11af
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131626090(0x7f0e086a, float:1.8879406E38)
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
            goto L_0x1383
        L_0x11af:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x11ed
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x11d4
            r1 = 2131626102(0x7f0e0876, float:1.887943E38)
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
            goto L_0x0410
        L_0x11d4:
            r3 = 0
            r4 = 2
            r1 = 2131626101(0x7f0e0875, float:1.8879429E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0410
        L_0x11ed:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x120c
            r0 = 2131626092(0x7f0e086c, float:1.887941E38)
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
            goto L_0x1383
        L_0x120c:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x133f
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x1216
            goto L_0x133f
        L_0x1216:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x122f
            r0 = 2131626096(0x7f0e0870, float:1.8879419E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x122f:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x1310
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x12df
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x1241
            goto L_0x12df
        L_0x1241:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x1293
            if (r21 != 0) goto L_0x127e
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x127e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x127e
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
            goto L_0x1383
        L_0x127e:
            r2 = 2
            r0 = 2131626094(0x7f0e086e, float:1.8879414E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x1293:
            if (r21 != 0) goto L_0x12ca
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x12ca
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x12ca
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
            goto L_0x1383
        L_0x12ca:
            r2 = 2
            r0 = 2131626091(0x7f0e086b, float:1.8879408E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x12df:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x12fc
            r1 = 2131626105(0x7f0e0879, float:1.8879437E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r2] = r12
            java.lang.String r2 = r10.title
            r3[r5] = r2
            r4 = 2
            r3[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0410
        L_0x12fc:
            r4 = 2
            r0 = 2131626104(0x7f0e0878, float:1.8879435E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0410
        L_0x1310:
            if (r21 != 0) goto L_0x132d
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x132d
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.CharSequence r0 = r0.messageText
            r3 = 2
            r1[r3] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            goto L_0x1383
        L_0x132d:
            r2 = 0
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626099(0x7f0e0873, float:1.8879425E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x1383
        L_0x133f:
            r2 = 0
            r3 = 2
            r0 = 2131626097(0x7f0e0871, float:1.887942E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x1353:
            r2 = 0
            if (r23 == 0) goto L_0x1358
            r23[r2] = r2
        L_0x1358:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r0 == 0) goto L_0x1370
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x1370
            r0 = 2131624649(0x7f0e02c9, float:1.8876484E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1383
        L_0x1370:
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626099(0x7f0e0873, float:1.8879425E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x1383
        L_0x1381:
            r1 = 0
        L_0x1382:
            r15 = r1
        L_0x1383:
            return r15
        L_0x1384:
            r0 = 2131627597(0x7f0e0e4d, float:1.8882463E38)
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
            org.telegram.tgnet.TLRPC$Peer r0 = r3.peer_id
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

    public /* synthetic */ void lambda$showNotifications$24$NotificationsController() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$showNotifications$24$NotificationsController();
            }
        });
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$hideNotifications$25$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$hideNotifications$25$NotificationsController() {
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
                if (!this.openedInBubbleDialogs.contains(Long.valueOf(this.wearNotificationsIds.keyAt(i)))) {
                    notificationManager.cancel(this.wearNotificationsIds.valueAt(i).intValue());
                }
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread($$Lambda$NotificationsController$Iii6Ysd4L9akcd1WhGl6DiaJBA.INSTANCE);
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
                            NotificationsController.this.lambda$playInChatSound$28$NotificationsController();
                        }
                    });
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public /* synthetic */ void lambda$playInChatSound$28$NotificationsController() {
        if (Math.abs(System.currentTimeMillis() - this.lastSoundPlay) > 500) {
            try {
                if (this.soundPool == null) {
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener($$Lambda$NotificationsController$KifNbzrscru9TRdUtK9fbMo4ilE.INSTANCE);
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

    static /* synthetic */ void lambda$null$27(SoundPool soundPool2, int i, int i2) {
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
                NotificationsController.this.lambda$repeatNotificationMaybe$29$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$repeatNotificationMaybe$29$NotificationsController() {
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
            public final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationsController.this.lambda$deleteNotificationChannel$30$NotificationsController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$deleteNotificationChannel$30$NotificationsController(long j) {
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
                NotificationsController.this.lambda$deleteAllNotificationChannels$31$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$deleteAllNotificationChannels$31$NotificationsController() {
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

    private boolean unsupportedNotificationShortcut() {
        return Build.VERSION.SDK_INT < 29 || !SharedConfig.chatBubbles;
    }

    @SuppressLint({"RestrictedApi"})
    private void createNotificationShortcut(NotificationCompat.Builder builder, int i, String str, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, Person person) {
        String str2;
        if (unsupportedNotificationShortcut()) {
            return;
        }
        if (!ChatObject.isChannel(tLRPC$Chat) || tLRPC$Chat.megagroup) {
            try {
                String str3 = "ndid_" + i;
                ShortcutInfoCompat.Builder builder2 = new ShortcutInfoCompat.Builder(ApplicationLoader.applicationContext, str3);
                if (tLRPC$Chat != null) {
                    str2 = str;
                } else {
                    str2 = UserObject.getFirstName(tLRPC$User);
                }
                builder2.setShortLabel(str2);
                builder2.setLongLabel(str);
                builder2.setIntent(new Intent("android.intent.action.VIEW"));
                builder2.setLongLived(true);
                Bitmap bitmap = null;
                if (person != null) {
                    builder2.setPerson(person);
                    builder2.setIcon(person.getIcon());
                    if (person.getIcon() != null) {
                        bitmap = person.getIcon().getBitmap();
                    }
                }
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(builder2.build());
                ShortcutManagerCompat.addDynamicShortcuts(ApplicationLoader.applicationContext, arrayList);
                builder.setShortcutId(str3);
                NotificationCompat.BubbleMetadata.Builder builder3 = new NotificationCompat.BubbleMetadata.Builder();
                Intent intent = new Intent(ApplicationLoader.applicationContext, BubbleActivity.class);
                intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
                if (i > 0) {
                    intent.putExtra("userId", i);
                } else {
                    intent.putExtra("chatId", -i);
                }
                intent.putExtra("currentAccount", this.currentAccount);
                builder3.setIntent(PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM));
                builder3.setSuppressNotification(true);
                builder3.setAutoExpandBubble(false);
                builder3.setDesiredHeight(AndroidUtilities.dp(640.0f));
                if (bitmap != null) {
                    builder3.setIcon(IconCompat.createWithAdaptiveBitmap(bitmap));
                } else if (tLRPC$User != null) {
                    builder3.setIcon(IconCompat.createWithResource(ApplicationLoader.applicationContext, tLRPC$User.bot ? NUM : NUM));
                } else {
                    builder3.setIcon(IconCompat.createWithResource(ApplicationLoader.applicationContext, NUM));
                }
                builder.setBubbleMetadata(builder3.build());
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
    /* JADX WARNING: Can't wrap try/catch for region: R(4:448|449|450|451) */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0092, code lost:
        if (r14 == getUserConfig().getClientUserId()) goto L_0x0094;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x087a, code lost:
        if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x087c;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:450:0x092d */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0290 A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02d6 A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0302 A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0303 A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0308 A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x030c A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0323  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0338  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x03d4 A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x03db A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x03e0 A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x04c4 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0500 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0513 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0516 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0520 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0535 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x0536 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0566 A[SYNTHETIC, Splitter:B:305:0x0566] */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x059a A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x05a6 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x05c0 A[SYNTHETIC, Splitter:B:321:0x05c0] */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x05d8 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x067a A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x06f3 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x07dc A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x0826  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x082f  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0872 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0883 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0941 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x094b A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0952 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x09cf A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x0a8c A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0abd A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x0ad6 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x0af4 A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:515:0x0b0f A[Catch:{ Exception -> 0x0b1b }] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01d2 A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0224 A[Catch:{ Exception -> 0x0b1d }] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x022f A[Catch:{ Exception -> 0x0b1d }] */
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
            if (r3 == 0) goto L_0x0b28
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r12.pushMessages
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0b28
            boolean r3 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r3 != 0) goto L_0x0026
            int r3 = r12.currentAccount
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            if (r3 == r4) goto L_0x0026
            goto L_0x0b28
        L_0x0026:
            org.telegram.tgnet.ConnectionsManager r3 = r46.getConnectionsManager()     // Catch:{ Exception -> 0x0b21 }
            r3.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0b21 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r12.pushMessages     // Catch:{ Exception -> 0x0b21 }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0b21 }
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3     // Catch:{ Exception -> 0x0b21 }
            org.telegram.messenger.AccountInstance r5 = r46.getAccountInstance()     // Catch:{ Exception -> 0x0b21 }
            android.content.SharedPreferences r5 = r5.getNotificationsSettings()     // Catch:{ Exception -> 0x0b21 }
            java.lang.String r6 = "dismissDate"
            int r6 = r5.getInt(r6, r4)     // Catch:{ Exception -> 0x0b21 }
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner     // Catch:{ Exception -> 0x0b21 }
            int r7 = r7.date     // Catch:{ Exception -> 0x0b21 }
            if (r7 > r6) goto L_0x0053
            r46.dismissNotification()     // Catch:{ Exception -> 0x004e }
            return
        L_0x004e:
            r0 = move-exception
            r1 = r0
            r13 = r12
            goto L_0x0b24
        L_0x0053:
            long r7 = r3.getDialogId()     // Catch:{ Exception -> 0x0b21 }
            org.telegram.tgnet.TLRPC$Message r9 = r3.messageOwner     // Catch:{ Exception -> 0x0b21 }
            boolean r9 = r9.mentioned     // Catch:{ Exception -> 0x0b21 }
            if (r9 == 0) goto L_0x0063
            int r9 = r3.getFromChatId()     // Catch:{ Exception -> 0x004e }
            long r9 = (long) r9
            goto L_0x0064
        L_0x0063:
            r9 = r7
        L_0x0064:
            r3.getId()     // Catch:{ Exception -> 0x0b21 }
            org.telegram.tgnet.TLRPC$Message r11 = r3.messageOwner     // Catch:{ Exception -> 0x0b21 }
            org.telegram.tgnet.TLRPC$Peer r11 = r11.peer_id     // Catch:{ Exception -> 0x0b21 }
            int r11 = r11.chat_id     // Catch:{ Exception -> 0x0b21 }
            if (r11 == 0) goto L_0x0076
            org.telegram.tgnet.TLRPC$Message r11 = r3.messageOwner     // Catch:{ Exception -> 0x004e }
            org.telegram.tgnet.TLRPC$Peer r11 = r11.peer_id     // Catch:{ Exception -> 0x004e }
            int r11 = r11.chat_id     // Catch:{ Exception -> 0x004e }
            goto L_0x007c
        L_0x0076:
            org.telegram.tgnet.TLRPC$Message r11 = r3.messageOwner     // Catch:{ Exception -> 0x0b21 }
            org.telegram.tgnet.TLRPC$Peer r11 = r11.peer_id     // Catch:{ Exception -> 0x0b21 }
            int r11 = r11.channel_id     // Catch:{ Exception -> 0x0b21 }
        L_0x007c:
            org.telegram.tgnet.TLRPC$Message r14 = r3.messageOwner     // Catch:{ Exception -> 0x0b21 }
            org.telegram.tgnet.TLRPC$Peer r14 = r14.peer_id     // Catch:{ Exception -> 0x0b21 }
            int r14 = r14.user_id     // Catch:{ Exception -> 0x0b21 }
            boolean r15 = r3.isFromUser()     // Catch:{ Exception -> 0x0b21 }
            if (r15 == 0) goto L_0x009a
            if (r14 == 0) goto L_0x0094
            org.telegram.messenger.UserConfig r15 = r46.getUserConfig()     // Catch:{ Exception -> 0x004e }
            int r15 = r15.getClientUserId()     // Catch:{ Exception -> 0x004e }
            if (r14 != r15) goto L_0x009a
        L_0x0094:
            org.telegram.tgnet.TLRPC$Message r14 = r3.messageOwner     // Catch:{ Exception -> 0x004e }
            org.telegram.tgnet.TLRPC$Peer r14 = r14.from_id     // Catch:{ Exception -> 0x004e }
            int r14 = r14.user_id     // Catch:{ Exception -> 0x004e }
        L_0x009a:
            org.telegram.messenger.MessagesController r15 = r46.getMessagesController()     // Catch:{ Exception -> 0x0b21 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x0b21 }
            org.telegram.tgnet.TLRPC$User r4 = r15.getUser(r4)     // Catch:{ Exception -> 0x0b21 }
            if (r11 == 0) goto L_0x00d1
            org.telegram.messenger.MessagesController r15 = r46.getMessagesController()     // Catch:{ Exception -> 0x004e }
            r18 = r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x004e }
            org.telegram.tgnet.TLRPC$Chat r6 = r15.getChat(r6)     // Catch:{ Exception -> 0x004e }
            if (r6 != 0) goto L_0x00c1
            boolean r15 = r3.isFcmMessage()     // Catch:{ Exception -> 0x004e }
            if (r15 == 0) goto L_0x00c1
            boolean r15 = r3.localChannel     // Catch:{ Exception -> 0x004e }
            goto L_0x00ce
        L_0x00c1:
            boolean r15 = org.telegram.messenger.ChatObject.isChannel(r6)     // Catch:{ Exception -> 0x004e }
            if (r15 == 0) goto L_0x00cd
            boolean r15 = r6.megagroup     // Catch:{ Exception -> 0x004e }
            if (r15 != 0) goto L_0x00cd
            r15 = 1
            goto L_0x00ce
        L_0x00cd:
            r15 = 0
        L_0x00ce:
            r19 = r3
            goto L_0x00d7
        L_0x00d1:
            r18 = r6
            r19 = r3
            r6 = 0
            r15 = 0
        L_0x00d7:
            int r3 = r12.getNotifyOverride(r5, r9)     // Catch:{ Exception -> 0x0b21 }
            r20 = r2
            r2 = -1
            r21 = r4
            r4 = 2
            if (r3 != r2) goto L_0x00ec
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r15)     // Catch:{ Exception -> 0x004e }
            boolean r3 = r12.isGlobalNotificationsEnabled(r7, r3)     // Catch:{ Exception -> 0x004e }
            goto L_0x00f1
        L_0x00ec:
            if (r3 == r4) goto L_0x00f0
            r3 = 1
            goto L_0x00f1
        L_0x00f0:
            r3 = 0
        L_0x00f1:
            if (r13 == 0) goto L_0x00f8
            if (r3 != 0) goto L_0x00f6
            goto L_0x00f8
        L_0x00f6:
            r3 = 0
            goto L_0x00f9
        L_0x00f8:
            r3 = 1
        L_0x00f9:
            java.lang.String r2 = "custom_"
            r23 = 1000(0x3e8, double:4.94E-321)
            if (r3 != 0) goto L_0x019a
            int r25 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r25 != 0) goto L_0x019a
            if (r6 == 0) goto L_0x019a
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1d }
            r9.<init>()     // Catch:{ Exception -> 0x0b1d }
            r9.append(r2)     // Catch:{ Exception -> 0x0b1d }
            r9.append(r7)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0b1d }
            r10 = 0
            boolean r9 = r5.getBoolean(r9, r10)     // Catch:{ Exception -> 0x0b1d }
            if (r9 == 0) goto L_0x0148
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
            goto L_0x014b
        L_0x0148:
            r10 = 180(0xb4, float:2.52E-43)
            r9 = 2
        L_0x014b:
            if (r9 == 0) goto L_0x019a
            android.util.LongSparseArray<android.graphics.Point> r4 = r12.smartNotificationsDialogs     // Catch:{ Exception -> 0x0b1d }
            java.lang.Object r4 = r4.get(r7)     // Catch:{ Exception -> 0x0b1d }
            android.graphics.Point r4 = (android.graphics.Point) r4     // Catch:{ Exception -> 0x0b1d }
            if (r4 != 0) goto L_0x016a
            android.graphics.Point r4 = new android.graphics.Point     // Catch:{ Exception -> 0x004e }
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x004e }
            long r9 = r9 / r23
            int r10 = (int) r9     // Catch:{ Exception -> 0x004e }
            r9 = 1
            r4.<init>(r9, r10)     // Catch:{ Exception -> 0x004e }
            android.util.LongSparseArray<android.graphics.Point> r9 = r12.smartNotificationsDialogs     // Catch:{ Exception -> 0x004e }
            r9.put(r7, r4)     // Catch:{ Exception -> 0x004e }
            goto L_0x019a
        L_0x016a:
            r25 = r3
            int r3 = r4.y     // Catch:{ Exception -> 0x0b1d }
            int r3 = r3 + r10
            long r12 = (long) r3     // Catch:{ Exception -> 0x0b1d }
            long r26 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0b1d }
            long r26 = r26 / r23
            int r3 = (r12 > r26 ? 1 : (r12 == r26 ? 0 : -1))
            if (r3 >= 0) goto L_0x0186
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0b1d }
            long r9 = r9 / r23
            int r3 = (int) r9     // Catch:{ Exception -> 0x0b1d }
            r9 = 1
            r4.set(r9, r3)     // Catch:{ Exception -> 0x0b1d }
            goto L_0x019c
        L_0x0186:
            int r3 = r4.x     // Catch:{ Exception -> 0x0b1d }
            if (r3 >= r9) goto L_0x0197
            r9 = 1
            int r3 = r3 + r9
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0b1d }
            long r9 = r9 / r23
            int r10 = (int) r9     // Catch:{ Exception -> 0x0b1d }
            r4.set(r3, r10)     // Catch:{ Exception -> 0x0b1d }
            goto L_0x019c
        L_0x0197:
            r25 = 1
            goto L_0x019c
        L_0x019a:
            r25 = r3
        L_0x019c:
            android.net.Uri r3 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r3 = r3.getPath()     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r4 = "EnableInAppSounds"
            r9 = 1
            boolean r4 = r5.getBoolean(r4, r9)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r10 = "EnableInAppVibrate"
            boolean r10 = r5.getBoolean(r10, r9)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r12 = "EnableInAppPreview"
            boolean r12 = r5.getBoolean(r12, r9)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r9 = "EnableInAppPriority"
            r13 = 0
            boolean r9 = r5.getBoolean(r9, r13)     // Catch:{ Exception -> 0x0b1d }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1d }
            r13.<init>()     // Catch:{ Exception -> 0x0b1d }
            r13.append(r2)     // Catch:{ Exception -> 0x0b1d }
            r13.append(r7)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r2 = r13.toString()     // Catch:{ Exception -> 0x0b1d }
            r13 = 0
            boolean r2 = r5.getBoolean(r2, r13)     // Catch:{ Exception -> 0x0b1d }
            if (r2 == 0) goto L_0x0224
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1d }
            r13.<init>()     // Catch:{ Exception -> 0x0b1d }
            r27 = r12
            java.lang.String r12 = "vibrate_"
            r13.append(r12)     // Catch:{ Exception -> 0x0b1d }
            r13.append(r7)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r12 = r13.toString()     // Catch:{ Exception -> 0x0b1d }
            r13 = 0
            int r12 = r5.getInt(r12, r13)     // Catch:{ Exception -> 0x0b1d }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1d }
            r13.<init>()     // Catch:{ Exception -> 0x0b1d }
            r28 = r12
            java.lang.String r12 = "priority_"
            r13.append(r12)     // Catch:{ Exception -> 0x0b1d }
            r13.append(r7)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r12 = r13.toString()     // Catch:{ Exception -> 0x0b1d }
            r13 = 3
            int r12 = r5.getInt(r12, r13)     // Catch:{ Exception -> 0x0b1d }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1d }
            r13.<init>()     // Catch:{ Exception -> 0x0b1d }
            r29 = r12
            java.lang.String r12 = "sound_path_"
            r13.append(r12)     // Catch:{ Exception -> 0x0b1d }
            r13.append(r7)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r12 = r13.toString()     // Catch:{ Exception -> 0x0b1d }
            r13 = 0
            java.lang.String r12 = r5.getString(r12, r13)     // Catch:{ Exception -> 0x0b1d }
            r13 = r12
            r12 = r28
            r28 = r6
            r6 = r29
            r29 = r9
            goto L_0x022d
        L_0x0224:
            r27 = r12
            r28 = r6
            r29 = r9
            r6 = 3
            r12 = 0
            r13 = 0
        L_0x022d:
            if (r11 == 0) goto L_0x0290
            if (r15 == 0) goto L_0x0260
            if (r13 == 0) goto L_0x023b
            boolean r15 = r13.equals(r3)     // Catch:{ Exception -> 0x0b1d }
            if (r15 == 0) goto L_0x023b
            r13 = 0
            goto L_0x0243
        L_0x023b:
            if (r13 != 0) goto L_0x0243
            java.lang.String r13 = "ChannelSoundPath"
            java.lang.String r13 = r5.getString(r13, r3)     // Catch:{ Exception -> 0x0b1d }
        L_0x0243:
            java.lang.String r15 = "vibrate_channel"
            r9 = 0
            int r15 = r5.getInt(r15, r9)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r9 = "priority_channel"
            r30 = r13
            r13 = 1
            int r9 = r5.getInt(r9, r13)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r13 = "ChannelLed"
            r31 = r9
            r9 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r9 = r5.getInt(r13, r9)     // Catch:{ Exception -> 0x0b1d }
            goto L_0x02d4
        L_0x0260:
            if (r13 == 0) goto L_0x026a
            boolean r9 = r13.equals(r3)     // Catch:{ Exception -> 0x0b1d }
            if (r9 == 0) goto L_0x026a
            r9 = 0
            goto L_0x0274
        L_0x026a:
            if (r13 != 0) goto L_0x0273
            java.lang.String r9 = "GroupSoundPath"
            java.lang.String r9 = r5.getString(r9, r3)     // Catch:{ Exception -> 0x0b1d }
            goto L_0x0274
        L_0x0273:
            r9 = r13
        L_0x0274:
            java.lang.String r13 = "vibrate_group"
            r15 = 0
            int r13 = r5.getInt(r13, r15)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r15 = "priority_group"
            r30 = r9
            r9 = 1
            int r15 = r5.getInt(r15, r9)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r9 = "GroupLed"
            r31 = r13
            r13 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r9 = r5.getInt(r9, r13)     // Catch:{ Exception -> 0x0b1d }
            goto L_0x02c1
        L_0x0290:
            if (r14 == 0) goto L_0x02c8
            if (r13 == 0) goto L_0x029c
            boolean r9 = r13.equals(r3)     // Catch:{ Exception -> 0x0b1d }
            if (r9 == 0) goto L_0x029c
            r9 = 0
            goto L_0x02a6
        L_0x029c:
            if (r13 != 0) goto L_0x02a5
            java.lang.String r9 = "GlobalSoundPath"
            java.lang.String r9 = r5.getString(r9, r3)     // Catch:{ Exception -> 0x0b1d }
            goto L_0x02a6
        L_0x02a5:
            r9 = r13
        L_0x02a6:
            java.lang.String r13 = "vibrate_messages"
            r15 = 0
            int r13 = r5.getInt(r13, r15)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r15 = "priority_messages"
            r30 = r9
            r9 = 1
            int r15 = r5.getInt(r15, r9)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r9 = "MessagesLed"
            r31 = r13
            r13 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r9 = r5.getInt(r9, r13)     // Catch:{ Exception -> 0x0b1d }
        L_0x02c1:
            r45 = r31
            r31 = r15
            r15 = r45
            goto L_0x02d4
        L_0x02c8:
            r9 = r13
            r13 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r30 = r9
            r9 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r15 = 0
            r31 = 0
        L_0x02d4:
            if (r2 == 0) goto L_0x02ff
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1d }
            r2.<init>()     // Catch:{ Exception -> 0x0b1d }
            r2.append(r1)     // Catch:{ Exception -> 0x0b1d }
            r2.append(r7)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b1d }
            boolean r2 = r5.contains(r2)     // Catch:{ Exception -> 0x0b1d }
            if (r2 == 0) goto L_0x02ff
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1d }
            r2.<init>()     // Catch:{ Exception -> 0x0b1d }
            r2.append(r1)     // Catch:{ Exception -> 0x0b1d }
            r2.append(r7)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x0b1d }
            r2 = 0
            int r9 = r5.getInt(r1, r2)     // Catch:{ Exception -> 0x0b1d }
        L_0x02ff:
            r1 = 3
            if (r6 == r1) goto L_0x0303
            goto L_0x0305
        L_0x0303:
            r6 = r31
        L_0x0305:
            r2 = 4
            if (r15 != r2) goto L_0x030c
            r5 = 1
            r13 = 2
            r15 = 0
            goto L_0x030e
        L_0x030c:
            r5 = 0
            r13 = 2
        L_0x030e:
            if (r15 != r13) goto L_0x0315
            r2 = 1
            if (r12 == r2) goto L_0x031e
            if (r12 == r1) goto L_0x031e
        L_0x0315:
            if (r15 == r13) goto L_0x0319
            if (r12 == r13) goto L_0x031e
        L_0x0319:
            if (r12 == 0) goto L_0x031f
            r1 = 4
            if (r12 == r1) goto L_0x031f
        L_0x031e:
            r15 = r12
        L_0x031f:
            boolean r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0b1d }
            if (r1 != 0) goto L_0x0338
            if (r4 != 0) goto L_0x0327
            r30 = 0
        L_0x0327:
            if (r10 != 0) goto L_0x032a
            r15 = 2
        L_0x032a:
            if (r29 != 0) goto L_0x0331
            r2 = r30
            r1 = 2
            r6 = 0
            goto L_0x033b
        L_0x0331:
            r1 = 2
            r2 = r30
            if (r6 != r1) goto L_0x033b
            r6 = 1
            goto L_0x033b
        L_0x0338:
            r1 = 2
            r2 = r30
        L_0x033b:
            if (r5 == 0) goto L_0x0351
            if (r15 == r1) goto L_0x0351
            android.media.AudioManager r1 = audioManager     // Catch:{ Exception -> 0x034c }
            int r1 = r1.getRingerMode()     // Catch:{ Exception -> 0x034c }
            if (r1 == 0) goto L_0x0351
            r4 = 1
            if (r1 == r4) goto L_0x0351
            r15 = 2
            goto L_0x0351
        L_0x034c:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0b1d }
        L_0x0351:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r4 = "NoSound"
            r12 = 100
            r10 = 26
            r30 = 0
            if (r1 < r10) goto L_0x03d4
            r1 = 2
            if (r15 != r1) goto L_0x036a
            long[] r10 = new long[r1]     // Catch:{ Exception -> 0x0b1d }
            r1 = 0
            r10[r1] = r30     // Catch:{ Exception -> 0x0b1d }
            r1 = 1
            r10[r1] = r30     // Catch:{ Exception -> 0x0b1d }
            r5 = r10
            goto L_0x0394
        L_0x036a:
            r1 = 1
            if (r15 != r1) goto L_0x037c
            r10 = 4
            long[] r5 = new long[r10]     // Catch:{ Exception -> 0x0b1d }
            r10 = 0
            r5[r10] = r30     // Catch:{ Exception -> 0x0b1d }
            r5[r1] = r12     // Catch:{ Exception -> 0x0b1d }
            r1 = 2
            r5[r1] = r30     // Catch:{ Exception -> 0x0b1d }
            r1 = 3
            r5[r1] = r12     // Catch:{ Exception -> 0x0b1d }
            goto L_0x0394
        L_0x037c:
            if (r15 == 0) goto L_0x0391
            r1 = 4
            if (r15 != r1) goto L_0x0382
            goto L_0x0391
        L_0x0382:
            r1 = 3
            if (r15 != r1) goto L_0x038f
            r1 = 2
            long[] r5 = new long[r1]     // Catch:{ Exception -> 0x0b1d }
            r1 = 0
            r5[r1] = r30     // Catch:{ Exception -> 0x0b1d }
            r1 = 1
            r5[r1] = r23     // Catch:{ Exception -> 0x0b1d }
            goto L_0x0394
        L_0x038f:
            r5 = 0
            goto L_0x0394
        L_0x0391:
            r1 = 0
            long[] r5 = new long[r1]     // Catch:{ Exception -> 0x0b1d }
        L_0x0394:
            if (r2 == 0) goto L_0x03aa
            boolean r1 = r2.equals(r4)     // Catch:{ Exception -> 0x0b1d }
            if (r1 != 0) goto L_0x03aa
            boolean r1 = r2.equals(r3)     // Catch:{ Exception -> 0x0b1d }
            if (r1 == 0) goto L_0x03a5
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b1d }
            goto L_0x03ab
        L_0x03a5:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b1d }
            goto L_0x03ab
        L_0x03aa:
            r1 = 0
        L_0x03ab:
            if (r6 != 0) goto L_0x03b3
            r32 = r1
            r10 = r5
            r33 = 3
            goto L_0x03d9
        L_0x03b3:
            r10 = 1
            if (r6 == r10) goto L_0x03ce
            r10 = 2
            if (r6 != r10) goto L_0x03ba
            goto L_0x03ce
        L_0x03ba:
            r10 = 4
            if (r6 != r10) goto L_0x03c3
            r32 = r1
            r10 = r5
            r33 = 1
            goto L_0x03d9
        L_0x03c3:
            r10 = 5
            r32 = r1
            if (r6 != r10) goto L_0x03cc
            r10 = r5
            r33 = 2
            goto L_0x03d9
        L_0x03cc:
            r10 = r5
            goto L_0x03d7
        L_0x03ce:
            r32 = r1
            r10 = r5
            r33 = 4
            goto L_0x03d9
        L_0x03d4:
            r10 = 0
            r32 = 0
        L_0x03d7:
            r33 = 0
        L_0x03d9:
            if (r25 == 0) goto L_0x03e0
            r1 = 0
            r2 = 0
            r6 = 0
            r15 = 0
            goto L_0x03e2
        L_0x03e0:
            r1 = r6
            r6 = r9
        L_0x03e2:
            android.content.Intent r5 = new android.content.Intent     // Catch:{ Exception -> 0x0b1d }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b1d }
            java.lang.Class<org.telegram.ui.LaunchActivity> r12 = org.telegram.ui.LaunchActivity.class
            r5.<init>(r9, r12)     // Catch:{ Exception -> 0x0b1d }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1d }
            r9.<init>()     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r12 = "com.tmessages.openchat"
            r9.append(r12)     // Catch:{ Exception -> 0x0b1d }
            double r12 = java.lang.Math.random()     // Catch:{ Exception -> 0x0b1d }
            r9.append(r12)     // Catch:{ Exception -> 0x0b1d }
            r12 = 2147483647(0x7fffffff, float:NaN)
            r9.append(r12)     // Catch:{ Exception -> 0x0b1d }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0b1d }
            r5.setAction(r9)     // Catch:{ Exception -> 0x0b1d }
            r9 = 32768(0x8000, float:4.5918E-41)
            r5.setFlags(r9)     // Catch:{ Exception -> 0x0b1d }
            int r9 = (int) r7
            if (r9 == 0) goto L_0x04c4
            r13 = r46
            android.util.LongSparseArray<java.lang.Integer> r12 = r13.pushDialogs     // Catch:{ Exception -> 0x0b1b }
            int r12 = r12.size()     // Catch:{ Exception -> 0x0b1b }
            r34 = r10
            r10 = 1
            if (r12 != r10) goto L_0x042e
            if (r11 == 0) goto L_0x0427
            java.lang.String r10 = "chatId"
            r5.putExtra(r10, r11)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x042e
        L_0x0427:
            if (r14 == 0) goto L_0x042e
            java.lang.String r10 = "userId"
            r5.putExtra(r10, r14)     // Catch:{ Exception -> 0x0b1b }
        L_0x042e:
            boolean r10 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b1b }
            if (r10 != 0) goto L_0x04b9
            boolean r10 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b1b }
            if (r10 == 0) goto L_0x043a
            goto L_0x04b9
        L_0x043a:
            android.util.LongSparseArray<java.lang.Integer> r10 = r13.pushDialogs     // Catch:{ Exception -> 0x0b1b }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0b1b }
            r12 = 1
            if (r10 != r12) goto L_0x04b2
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r12 = 28
            if (r10 >= r12) goto L_0x04b2
            if (r28 == 0) goto L_0x0482
            r10 = r28
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r10.photo     // Catch:{ Exception -> 0x0b1b }
            if (r12 == 0) goto L_0x0478
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r10.photo     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ Exception -> 0x0b1b }
            if (r12 == 0) goto L_0x0478
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r10.photo     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ Exception -> 0x0b1b }
            r28 = r15
            long r14 = r12.volume_id     // Catch:{ Exception -> 0x0b1b }
            int r12 = (r14 > r30 ? 1 : (r14 == r30 ? 0 : -1))
            if (r12 == 0) goto L_0x047a
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r10.photo     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ Exception -> 0x0b1b }
            int r12 = r12.local_id     // Catch:{ Exception -> 0x0b1b }
            if (r12 == 0) goto L_0x047a
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r10.photo     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ Exception -> 0x0b1b }
            r35 = r4
            r14 = r12
            r12 = r21
            r21 = r3
            goto L_0x04ec
        L_0x0478:
            r28 = r15
        L_0x047a:
            r35 = r4
            r12 = r21
            r21 = r3
            goto L_0x04eb
        L_0x0482:
            r10 = r28
            r28 = r15
            if (r21 == 0) goto L_0x04af
            r12 = r21
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo     // Catch:{ Exception -> 0x0b1b }
            if (r14 == 0) goto L_0x04bf
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ Exception -> 0x0b1b }
            if (r14 == 0) goto L_0x04bf
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ Exception -> 0x0b1b }
            long r14 = r14.volume_id     // Catch:{ Exception -> 0x0b1b }
            int r21 = (r14 > r30 ? 1 : (r14 == r30 ? 0 : -1))
            if (r21 == 0) goto L_0x04bf
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ Exception -> 0x0b1b }
            int r14 = r14.local_id     // Catch:{ Exception -> 0x0b1b }
            if (r14 == 0) goto L_0x04bf
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ Exception -> 0x0b1b }
            r21 = r3
            r35 = r4
            goto L_0x04ec
        L_0x04af:
            r12 = r21
            goto L_0x04bf
        L_0x04b2:
            r12 = r21
            r10 = r28
            r28 = r15
            goto L_0x04bf
        L_0x04b9:
            r12 = r21
            r10 = r28
            r28 = r15
        L_0x04bf:
            r21 = r3
            r35 = r4
            goto L_0x04eb
        L_0x04c4:
            r13 = r46
            r34 = r10
            r12 = r21
            r10 = r28
            r28 = r15
            android.util.LongSparseArray<java.lang.Integer> r14 = r13.pushDialogs     // Catch:{ Exception -> 0x0b1b }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0b1b }
            r15 = 1
            if (r14 != r15) goto L_0x04bf
            long r14 = globalSecretChatId     // Catch:{ Exception -> 0x0b1b }
            int r21 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
            if (r21 == 0) goto L_0x04bf
            java.lang.String r14 = "encId"
            r21 = r3
            r35 = r4
            r15 = 32
            long r3 = r7 >> r15
            int r4 = (int) r3     // Catch:{ Exception -> 0x0b1b }
            r5.putExtra(r14, r4)     // Catch:{ Exception -> 0x0b1b }
        L_0x04eb:
            r14 = 0
        L_0x04ec:
            int r3 = r13.currentAccount     // Catch:{ Exception -> 0x0b1b }
            r4 = r20
            r5.putExtra(r4, r3)     // Catch:{ Exception -> 0x0b1b }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b1b }
            r15 = 1073741824(0x40000000, float:2.0)
            r36 = r7
            r7 = 0
            android.app.PendingIntent r3 = android.app.PendingIntent.getActivity(r3, r7, r5, r15)     // Catch:{ Exception -> 0x0b1b }
            if (r11 == 0) goto L_0x0502
            if (r10 == 0) goto L_0x0504
        L_0x0502:
            if (r12 != 0) goto L_0x050f
        L_0x0504:
            boolean r5 = r19.isFcmMessage()     // Catch:{ Exception -> 0x0b1b }
            if (r5 == 0) goto L_0x050f
            r5 = r19
            java.lang.String r7 = r5.localName     // Catch:{ Exception -> 0x0b1b }
            goto L_0x051a
        L_0x050f:
            r5 = r19
            if (r10 == 0) goto L_0x0516
            java.lang.String r7 = r10.title     // Catch:{ Exception -> 0x0b1b }
            goto L_0x051a
        L_0x0516:
            java.lang.String r7 = org.telegram.messenger.UserObject.getUserName(r12)     // Catch:{ Exception -> 0x0b1b }
        L_0x051a:
            boolean r8 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b1b }
            if (r8 != 0) goto L_0x0527
            boolean r8 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b1b }
            if (r8 == 0) goto L_0x0525
            goto L_0x0527
        L_0x0525:
            r8 = 0
            goto L_0x0528
        L_0x0527:
            r8 = 1
        L_0x0528:
            if (r9 == 0) goto L_0x0539
            android.util.LongSparseArray<java.lang.Integer> r9 = r13.pushDialogs     // Catch:{ Exception -> 0x0b1b }
            int r9 = r9.size()     // Catch:{ Exception -> 0x0b1b }
            r15 = 1
            if (r9 > r15) goto L_0x0539
            if (r8 == 0) goto L_0x0536
            goto L_0x0539
        L_0x0536:
            r8 = r7
            r9 = 1
            goto L_0x055b
        L_0x0539:
            if (r8 == 0) goto L_0x0551
            if (r11 == 0) goto L_0x0547
            java.lang.String r8 = "NotificationHiddenChatName"
            r9 = 2131626074(0x7f0e085a, float:1.8879374E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x055a
        L_0x0547:
            java.lang.String r8 = "NotificationHiddenName"
            r9 = 2131626077(0x7f0e085d, float:1.887938E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x055a
        L_0x0551:
            java.lang.String r8 = "AppName"
            r9 = 2131624245(0x7f0e0135, float:1.8875664E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0b1b }
        L_0x055a:
            r9 = 0
        L_0x055b:
            int r11 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r15 = ""
            r19 = r7
            r7 = 1
            if (r11 <= r7) goto L_0x059a
            android.util.LongSparseArray<java.lang.Integer> r11 = r13.pushDialogs     // Catch:{ Exception -> 0x0b1b }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0b1b }
            if (r11 != r7) goto L_0x057b
            org.telegram.messenger.UserConfig r7 = r46.getUserConfig()     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$User r7 = r7.getCurrentUser()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r7 = org.telegram.messenger.UserObject.getFirstName(r7)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x059b
        L_0x057b:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r7.<init>()     // Catch:{ Exception -> 0x0b1b }
            org.telegram.messenger.UserConfig r11 = r46.getUserConfig()     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$User r11 = r11.getCurrentUser()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r11)     // Catch:{ Exception -> 0x0b1b }
            r7.append(r11)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r11 = "・"
            r7.append(r11)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0b1b }
            goto L_0x059b
        L_0x059a:
            r7 = r15
        L_0x059b:
            android.util.LongSparseArray<java.lang.Integer> r11 = r13.pushDialogs     // Catch:{ Exception -> 0x0b1b }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0b1b }
            r20 = r6
            r6 = 1
            if (r11 != r6) goto L_0x05b3
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r11 = 23
            if (r6 >= r11) goto L_0x05ad
            goto L_0x05b3
        L_0x05ad:
            r40 = r1
            r38 = r2
        L_0x05b1:
            r11 = r7
            goto L_0x060e
        L_0x05b3:
            android.util.LongSparseArray<java.lang.Integer> r6 = r13.pushDialogs     // Catch:{ Exception -> 0x0b1b }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r11 = "NewMessages"
            r38 = r2
            r2 = 1
            if (r6 != r2) goto L_0x05d8
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r2.<init>()     // Catch:{ Exception -> 0x0b1b }
            r2.append(r7)     // Catch:{ Exception -> 0x0b1b }
            int r6 = r13.total_unread_count     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r11, r6)     // Catch:{ Exception -> 0x0b1b }
            r2.append(r6)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r7 = r2.toString()     // Catch:{ Exception -> 0x0b1b }
            r40 = r1
            goto L_0x05b1
        L_0x05d8:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r2.<init>()     // Catch:{ Exception -> 0x0b1b }
            r2.append(r7)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r6 = "NotificationMessagesPeopleDisplayOrder"
            r40 = r1
            r7 = 2
            java.lang.Object[] r1 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0b1b }
            int r7 = r13.total_unread_count     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r11, r7)     // Catch:{ Exception -> 0x0b1b }
            r11 = 0
            r1[r11] = r7     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r7 = "FromChats"
            android.util.LongSparseArray<java.lang.Integer> r11 = r13.pushDialogs     // Catch:{ Exception -> 0x0b1b }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11)     // Catch:{ Exception -> 0x0b1b }
            r11 = 1
            r1[r11] = r7     // Catch:{ Exception -> 0x0b1b }
            r7 = 2131626125(0x7f0e088d, float:1.8879477E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r7, r1)     // Catch:{ Exception -> 0x0b1b }
            r2.append(r1)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r7 = r2.toString()     // Catch:{ Exception -> 0x0b1b }
            goto L_0x05b1
        L_0x060e:
            androidx.core.app.NotificationCompat$Builder r7 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0b1b }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b1b }
            r7.<init>(r1)     // Catch:{ Exception -> 0x0b1b }
            r7.setContentTitle(r8)     // Catch:{ Exception -> 0x0b1b }
            r1 = 2131165796(0x7var_, float:1.794582E38)
            r7.setSmallIcon(r1)     // Catch:{ Exception -> 0x0b1b }
            r1 = 1
            r7.setAutoCancel(r1)     // Catch:{ Exception -> 0x0b1b }
            int r1 = r13.total_unread_count     // Catch:{ Exception -> 0x0b1b }
            r7.setNumber(r1)     // Catch:{ Exception -> 0x0b1b }
            r7.setContentIntent(r3)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r1 = r13.notificationGroup     // Catch:{ Exception -> 0x0b1b }
            r7.setGroup(r1)     // Catch:{ Exception -> 0x0b1b }
            r1 = 1
            r7.setGroupSummary(r1)     // Catch:{ Exception -> 0x0b1b }
            r7.setShowWhen(r1)     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner     // Catch:{ Exception -> 0x0b1b }
            int r1 = r1.date     // Catch:{ Exception -> 0x0b1b }
            long r1 = (long) r1     // Catch:{ Exception -> 0x0b1b }
            long r1 = r1 * r23
            r7.setWhen(r1)     // Catch:{ Exception -> 0x0b1b }
            r1 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r7.setColor(r1)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r1 = "msg"
            r7.setCategory(r1)     // Catch:{ Exception -> 0x0b1b }
            if (r10 != 0) goto L_0x0671
            if (r12 == 0) goto L_0x0671
            java.lang.String r1 = r12.phone     // Catch:{ Exception -> 0x0b1b }
            if (r1 == 0) goto L_0x0671
            java.lang.String r1 = r12.phone     // Catch:{ Exception -> 0x0b1b }
            int r1 = r1.length()     // Catch:{ Exception -> 0x0b1b }
            if (r1 <= 0) goto L_0x0671
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r1.<init>()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = "tel:+"
            r1.append(r2)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r12.phone     // Catch:{ Exception -> 0x0b1b }
            r1.append(r2)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0b1b }
            r7.addPerson(r1)     // Catch:{ Exception -> 0x0b1b }
        L_0x0671:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r13.pushMessages     // Catch:{ Exception -> 0x0b1b }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0b1b }
            r2 = 1
            if (r1 != r2) goto L_0x06f3
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r13.pushMessages     // Catch:{ Exception -> 0x0b1b }
            r3 = 0
            java.lang.Object r1 = r1.get(r3)     // Catch:{ Exception -> 0x0b1b }
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1     // Catch:{ Exception -> 0x0b1b }
            boolean[] r6 = new boolean[r2]     // Catch:{ Exception -> 0x0b1b }
            r2 = 0
            java.lang.String r12 = r13.getStringForMessage(r1, r3, r6, r2)     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner     // Catch:{ Exception -> 0x0b1b }
            boolean r1 = r1.silent     // Catch:{ Exception -> 0x0b1b }
            if (r12 != 0) goto L_0x0691
            return
        L_0x0691:
            if (r9 == 0) goto L_0x06dc
            if (r10 == 0) goto L_0x06ab
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r2.<init>()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r3 = " @ "
            r2.append(r3)     // Catch:{ Exception -> 0x0b1b }
            r2.append(r8)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r12.replace(r2, r15)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x06dd
        L_0x06ab:
            r2 = 0
            boolean r3 = r6[r2]     // Catch:{ Exception -> 0x0b1b }
            if (r3 == 0) goto L_0x06c6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r2.<init>()     // Catch:{ Exception -> 0x0b1b }
            r2.append(r8)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r3 = ": "
            r2.append(r3)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r12.replace(r2, r15)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x06dd
        L_0x06c6:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r2.<init>()     // Catch:{ Exception -> 0x0b1b }
            r2.append(r8)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r3 = " "
            r2.append(r3)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r12.replace(r2, r15)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x06dd
        L_0x06dc:
            r2 = r12
        L_0x06dd:
            r7.setContentText(r2)     // Catch:{ Exception -> 0x0b1b }
            androidx.core.app.NotificationCompat$BigTextStyle r3 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0b1b }
            r3.<init>()     // Catch:{ Exception -> 0x0b1b }
            r3.bigText(r2)     // Catch:{ Exception -> 0x0b1b }
            r7.setStyle(r3)     // Catch:{ Exception -> 0x0b1b }
            r43 = r4
            r44 = r5
            r42 = r14
            goto L_0x07b3
        L_0x06f3:
            r7.setContentText(r11)     // Catch:{ Exception -> 0x0b1b }
            androidx.core.app.NotificationCompat$InboxStyle r1 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0b1b }
            r1.<init>()     // Catch:{ Exception -> 0x0b1b }
            r1.setBigContentTitle(r8)     // Catch:{ Exception -> 0x0b1b }
            r2 = 10
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r13.pushMessages     // Catch:{ Exception -> 0x0b1b }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0b1b }
            int r2 = java.lang.Math.min(r2, r3)     // Catch:{ Exception -> 0x0b1b }
            r3 = 1
            boolean[] r6 = new boolean[r3]     // Catch:{ Exception -> 0x0b1b }
            r3 = 2
            r12 = 0
            r39 = 0
        L_0x0711:
            if (r12 >= r2) goto L_0x07a4
            r41 = r2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r13.pushMessages     // Catch:{ Exception -> 0x0b1b }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Exception -> 0x0b1b }
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2     // Catch:{ Exception -> 0x0b1b }
            r43 = r4
            r44 = r5
            r42 = r14
            r4 = 0
            r14 = 0
            java.lang.String r5 = r13.getStringForMessage(r2, r4, r6, r14)     // Catch:{ Exception -> 0x0b1b }
            if (r5 == 0) goto L_0x0794
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner     // Catch:{ Exception -> 0x0b1b }
            int r4 = r4.date     // Catch:{ Exception -> 0x0b1b }
            r14 = r18
            if (r4 > r14) goto L_0x0734
            goto L_0x0796
        L_0x0734:
            r4 = 2
            if (r3 != r4) goto L_0x073d
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner     // Catch:{ Exception -> 0x0b1b }
            boolean r3 = r2.silent     // Catch:{ Exception -> 0x0b1b }
            r39 = r5
        L_0x073d:
            android.util.LongSparseArray<java.lang.Integer> r2 = r13.pushDialogs     // Catch:{ Exception -> 0x0b1b }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0b1b }
            r4 = 1
            if (r2 != r4) goto L_0x0790
            if (r9 == 0) goto L_0x0790
            if (r10 == 0) goto L_0x0760
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r2.<init>()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r4 = " @ "
            r2.append(r4)     // Catch:{ Exception -> 0x0b1b }
            r2.append(r8)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r5 = r5.replace(r2, r15)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x0790
        L_0x0760:
            r2 = 0
            boolean r4 = r6[r2]     // Catch:{ Exception -> 0x0b1b }
            if (r4 == 0) goto L_0x077b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r2.<init>()     // Catch:{ Exception -> 0x0b1b }
            r2.append(r8)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r4 = ": "
            r2.append(r4)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r5 = r5.replace(r2, r15)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x0790
        L_0x077b:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r2.<init>()     // Catch:{ Exception -> 0x0b1b }
            r2.append(r8)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r4 = " "
            r2.append(r4)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r5 = r5.replace(r2, r15)     // Catch:{ Exception -> 0x0b1b }
        L_0x0790:
            r1.addLine(r5)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x0796
        L_0x0794:
            r14 = r18
        L_0x0796:
            int r12 = r12 + 1
            r18 = r14
            r2 = r41
            r14 = r42
            r4 = r43
            r5 = r44
            goto L_0x0711
        L_0x07a4:
            r43 = r4
            r44 = r5
            r42 = r14
            r1.setSummaryText(r11)     // Catch:{ Exception -> 0x0b1b }
            r7.setStyle(r1)     // Catch:{ Exception -> 0x0b1b }
            r1 = r3
            r12 = r39
        L_0x07b3:
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0b1b }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b1b }
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r4 = org.telegram.messenger.NotificationDismissReceiver.class
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r3 = "messageDate"
            r4 = r44
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner     // Catch:{ Exception -> 0x0b1b }
            int r5 = r5.date     // Catch:{ Exception -> 0x0b1b }
            r2.putExtra(r3, r5)     // Catch:{ Exception -> 0x0b1b }
            int r3 = r13.currentAccount     // Catch:{ Exception -> 0x0b1b }
            r5 = r43
            r2.putExtra(r5, r3)     // Catch:{ Exception -> 0x0b1b }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b1b }
            r6 = 134217728(0x8000000, float:3.85186E-34)
            r8 = 1
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r3, r8, r2, r6)     // Catch:{ Exception -> 0x0b1b }
            r7.setDeleteIntent(r2)     // Catch:{ Exception -> 0x0b1b }
            if (r42 == 0) goto L_0x0826
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r3 = "50_50"
            r14 = r42
            r8 = 0
            android.graphics.drawable.BitmapDrawable r2 = r2.getImageFromMemory(r14, r8, r3)     // Catch:{ Exception -> 0x0b1b }
            if (r2 == 0) goto L_0x07f3
            android.graphics.Bitmap r2 = r2.getBitmap()     // Catch:{ Exception -> 0x0b1b }
            r7.setLargeIcon(r2)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x0827
        L_0x07f3:
            r2 = 1
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r14, r2)     // Catch:{ all -> 0x0827 }
            boolean r2 = r3.exists()     // Catch:{ all -> 0x0827 }
            if (r2 == 0) goto L_0x0827
            r2 = 1126170624(0x43200000, float:160.0)
            r9 = 1112014848(0x42480000, float:50.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x0827 }
            float r9 = (float) r9     // Catch:{ all -> 0x0827 }
            float r2 = r2 / r9
            android.graphics.BitmapFactory$Options r9 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0827 }
            r9.<init>()     // Catch:{ all -> 0x0827 }
            r10 = 1065353216(0x3var_, float:1.0)
            int r10 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r10 >= 0) goto L_0x0815
            r2 = 1
            goto L_0x0816
        L_0x0815:
            int r2 = (int) r2     // Catch:{ all -> 0x0827 }
        L_0x0816:
            r9.inSampleSize = r2     // Catch:{ all -> 0x0827 }
            java.lang.String r2 = r3.getAbsolutePath()     // Catch:{ all -> 0x0827 }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2, r9)     // Catch:{ all -> 0x0827 }
            if (r2 == 0) goto L_0x0827
            r7.setLargeIcon(r2)     // Catch:{ all -> 0x0827 }
            goto L_0x0827
        L_0x0826:
            r8 = 0
        L_0x0827:
            r14 = r47
            if (r14 == 0) goto L_0x0872
            r2 = 1
            if (r1 != r2) goto L_0x082f
            goto L_0x0872
        L_0x082f:
            if (r40 != 0) goto L_0x083e
            r2 = 0
            r7.setPriority(r2)     // Catch:{ Exception -> 0x0b1b }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r3 = 26
            if (r2 < r3) goto L_0x087f
            r2 = 1
            r9 = 3
            goto L_0x0881
        L_0x083e:
            r2 = r40
            r3 = 1
            if (r2 == r3) goto L_0x0865
            r3 = 2
            if (r2 != r3) goto L_0x0847
            goto L_0x0865
        L_0x0847:
            r3 = 4
            if (r2 != r3) goto L_0x0857
            r2 = -2
            r7.setPriority(r2)     // Catch:{ Exception -> 0x0b1b }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r3 = 26
            if (r2 < r3) goto L_0x087f
            r2 = 1
            r9 = 1
            goto L_0x0881
        L_0x0857:
            r3 = 5
            if (r2 != r3) goto L_0x087f
            r2 = -1
            r7.setPriority(r2)     // Catch:{ Exception -> 0x0b1b }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r3 = 26
            if (r2 < r3) goto L_0x087f
            goto L_0x087c
        L_0x0865:
            r2 = 1
            r7.setPriority(r2)     // Catch:{ Exception -> 0x0b1b }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r3 = 26
            if (r2 < r3) goto L_0x087f
            r2 = 1
            r9 = 4
            goto L_0x0881
        L_0x0872:
            r2 = -1
            r7.setPriority(r2)     // Catch:{ Exception -> 0x0b1b }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r3 = 26
            if (r2 < r3) goto L_0x087f
        L_0x087c:
            r2 = 1
            r9 = 2
            goto L_0x0881
        L_0x087f:
            r2 = 1
            r9 = 0
        L_0x0881:
            if (r1 == r2) goto L_0x09a4
            if (r25 != 0) goto L_0x09a4
            boolean r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0b1b }
            if (r1 != 0) goto L_0x088b
            if (r27 == 0) goto L_0x08ba
        L_0x088b:
            int r1 = r12.length()     // Catch:{ Exception -> 0x0b1b }
            r2 = 100
            if (r1 <= r2) goto L_0x08b7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b1b }
            r1.<init>()     // Catch:{ Exception -> 0x0b1b }
            r2 = 100
            r3 = 0
            java.lang.String r2 = r12.substring(r3, r2)     // Catch:{ Exception -> 0x0b1b }
            r3 = 10
            r10 = 32
            java.lang.String r2 = r2.replace(r3, r10)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = r2.trim()     // Catch:{ Exception -> 0x0b1b }
            r1.append(r2)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r2 = "..."
            r1.append(r2)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r12 = r1.toString()     // Catch:{ Exception -> 0x0b1b }
        L_0x08b7:
            r7.setTicker(r12)     // Catch:{ Exception -> 0x0b1b }
        L_0x08ba:
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0b1b }
            boolean r1 = r1.isRecordingAudio()     // Catch:{ Exception -> 0x0b1b }
            if (r1 != 0) goto L_0x093e
            if (r38 == 0) goto L_0x093e
            r1 = r35
            r2 = r38
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0b1b }
            if (r1 != 0) goto L_0x093e
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r3 = 26
            if (r1 < r3) goto L_0x08e6
            r1 = r21
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0b1b }
            if (r1 == 0) goto L_0x08e1
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b1b }
            goto L_0x093f
        L_0x08e1:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x093f
        L_0x08e6:
            r1 = r21
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0b1b }
            if (r1 == 0) goto L_0x08f5
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b1b }
            r2 = 5
            r7.setSound(r1, r2)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x093e
        L_0x08f5:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r3 = 24
            if (r1 < r3) goto L_0x0936
            java.lang.String r1 = "file://"
            boolean r1 = r2.startsWith(r1)     // Catch:{ Exception -> 0x0b1b }
            if (r1 == 0) goto L_0x0936
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b1b }
            boolean r1 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r1)     // Catch:{ Exception -> 0x0b1b }
            if (r1 != 0) goto L_0x0936
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x092d }
            java.lang.String r3 = "org.telegram.messenger.beta.provider"
            java.io.File r10 = new java.io.File     // Catch:{ Exception -> 0x092d }
            java.lang.String r12 = "file://"
            java.lang.String r12 = r2.replace(r12, r15)     // Catch:{ Exception -> 0x092d }
            r10.<init>(r12)     // Catch:{ Exception -> 0x092d }
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r1, r3, r10)     // Catch:{ Exception -> 0x092d }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x092d }
            java.lang.String r10 = "com.android.systemui"
            r12 = 1
            r3.grantUriPermission(r10, r1, r12)     // Catch:{ Exception -> 0x092d }
            r3 = 5
            r7.setSound(r1, r3)     // Catch:{ Exception -> 0x092d }
            goto L_0x093e
        L_0x092d:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b1b }
            r2 = 5
            r7.setSound(r1, r2)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x093e
        L_0x0936:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0b1b }
            r2 = 5
            r7.setSound(r1, r2)     // Catch:{ Exception -> 0x0b1b }
        L_0x093e:
            r1 = r8
        L_0x093f:
            if (r20 == 0) goto L_0x094b
            r2 = 1000(0x3e8, float:1.401E-42)
            r3 = 1000(0x3e8, float:1.401E-42)
            r10 = r20
            r7.setLights(r10, r2, r3)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x094d
        L_0x094b:
            r10 = r20
        L_0x094d:
            r15 = r28
            r2 = 2
            if (r15 == r2) goto L_0x0995
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0b1b }
            boolean r2 = r2.isRecordingAudio()     // Catch:{ Exception -> 0x0b1b }
            if (r2 == 0) goto L_0x095e
            r2 = 2
            goto L_0x0995
        L_0x095e:
            r2 = 1
            if (r15 != r2) goto L_0x0975
            r3 = 4
            long[] r3 = new long[r3]     // Catch:{ Exception -> 0x0b1b }
            r8 = 0
            r3[r8] = r30     // Catch:{ Exception -> 0x0b1b }
            r20 = 100
            r3[r2] = r20     // Catch:{ Exception -> 0x0b1b }
            r2 = 2
            r3[r2] = r30     // Catch:{ Exception -> 0x0b1b }
            r2 = 3
            r3[r2] = r20     // Catch:{ Exception -> 0x0b1b }
            r7.setVibrate(r3)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x09a0
        L_0x0975:
            if (r15 == 0) goto L_0x098d
            r2 = 4
            if (r15 != r2) goto L_0x097b
            goto L_0x098d
        L_0x097b:
            r2 = 3
            if (r15 != r2) goto L_0x098b
            r2 = 2
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b1b }
            r2 = 0
            r3[r2] = r30     // Catch:{ Exception -> 0x0b1b }
            r2 = 1
            r3[r2] = r23     // Catch:{ Exception -> 0x0b1b }
            r7.setVibrate(r3)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x09a0
        L_0x098b:
            r12 = r1
            goto L_0x09a2
        L_0x098d:
            r2 = 2
            r7.setDefaults(r2)     // Catch:{ Exception -> 0x0b1b }
            r2 = 0
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b1b }
            goto L_0x09a0
        L_0x0995:
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b1b }
            r2 = 0
            r3[r2] = r30     // Catch:{ Exception -> 0x0b1b }
            r2 = 1
            r3[r2] = r30     // Catch:{ Exception -> 0x0b1b }
            r7.setVibrate(r3)     // Catch:{ Exception -> 0x0b1b }
        L_0x09a0:
            r12 = r1
            r8 = r3
        L_0x09a2:
            r1 = 1
            goto L_0x09b4
        L_0x09a4:
            r10 = r20
            r1 = 2
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0b1b }
            r1 = 0
            r2[r1] = r30     // Catch:{ Exception -> 0x0b1b }
            r1 = 1
            r2[r1] = r30     // Catch:{ Exception -> 0x0b1b }
            r7.setVibrate(r2)     // Catch:{ Exception -> 0x0b1b }
            r12 = r8
            r8 = r2
        L_0x09b4:
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b1b }
            if (r2 != 0) goto L_0x0a8c
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b1b }
            if (r2 != 0) goto L_0x0a8c
            long r2 = r4.getDialogId()     // Catch:{ Exception -> 0x0b1b }
            r16 = 777000(0xbdb28, double:3.83889E-318)
            int r15 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r15 != 0) goto L_0x0a8c
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup     // Catch:{ Exception -> 0x0b1b }
            if (r2 == 0) goto L_0x0a8c
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup     // Catch:{ Exception -> 0x0b1b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r2 = r2.rows     // Catch:{ Exception -> 0x0b1b }
            int r3 = r2.size()     // Catch:{ Exception -> 0x0b1b }
            r15 = 0
            r16 = 0
        L_0x09dc:
            if (r15 >= r3) goto L_0x0a83
            java.lang.Object r17 = r2.get(r15)     // Catch:{ Exception -> 0x0b1b }
            r1 = r17
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r1 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r1     // Catch:{ Exception -> 0x0b1b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r6 = r1.buttons     // Catch:{ Exception -> 0x0b1b }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0b1b }
            r20 = r2
            r2 = 0
        L_0x09ef:
            if (r2 >= r6) goto L_0x0a67
            r21 = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r3 = r1.buttons     // Catch:{ Exception -> 0x0b1b }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0b1b }
            org.telegram.tgnet.TLRPC$KeyboardButton r3 = (org.telegram.tgnet.TLRPC$KeyboardButton) r3     // Catch:{ Exception -> 0x0b1b }
            r22 = r1
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0b1b }
            if (r1 == 0) goto L_0x0a49
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0b1b }
            r23 = r6
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b1b }
            r24 = r11
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r11 = org.telegram.messenger.NotificationCallbackReceiver.class
            r1.<init>(r6, r11)     // Catch:{ Exception -> 0x0b1b }
            int r6 = r13.currentAccount     // Catch:{ Exception -> 0x0b1b }
            r1.putExtra(r5, r6)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r6 = "did"
            r25 = r12
            r11 = r36
            r1.putExtra(r6, r11)     // Catch:{ Exception -> 0x0b1b }
            byte[] r6 = r3.data     // Catch:{ Exception -> 0x0b1b }
            if (r6 == 0) goto L_0x0a27
            java.lang.String r6 = "data"
            byte[] r14 = r3.data     // Catch:{ Exception -> 0x0b1b }
            r1.putExtra(r6, r14)     // Catch:{ Exception -> 0x0b1b }
        L_0x0a27:
            java.lang.String r6 = "mid"
            int r14 = r4.getId()     // Catch:{ Exception -> 0x0b1b }
            r1.putExtra(r6, r14)     // Catch:{ Exception -> 0x0b1b }
            java.lang.String r3 = r3.text     // Catch:{ Exception -> 0x0b1b }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b1b }
            int r14 = r13.lastButtonId     // Catch:{ Exception -> 0x0b1b }
            r44 = r4
            int r4 = r14 + 1
            r13.lastButtonId = r4     // Catch:{ Exception -> 0x0b1b }
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r6, r14, r1, r4)     // Catch:{ Exception -> 0x0b1b }
            r4 = 0
            r7.addAction(r4, r3, r1)     // Catch:{ Exception -> 0x0b1b }
            r16 = 1
            goto L_0x0a54
        L_0x0a49:
            r44 = r4
            r23 = r6
            r24 = r11
            r25 = r12
            r11 = r36
            r4 = 0
        L_0x0a54:
            int r2 = r2 + 1
            r14 = r47
            r36 = r11
            r3 = r21
            r1 = r22
            r6 = r23
            r11 = r24
            r12 = r25
            r4 = r44
            goto L_0x09ef
        L_0x0a67:
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
            goto L_0x09dc
        L_0x0a83:
            r24 = r11
            r25 = r12
            r11 = r36
            r4 = r16
            goto L_0x0a93
        L_0x0a8c:
            r24 = r11
            r25 = r12
            r11 = r36
            r4 = 0
        L_0x0a93:
            if (r4 != 0) goto L_0x0aee
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r2 = 24
            if (r1 >= r2) goto L_0x0aee
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0b1b }
            int r1 = r1.length()     // Catch:{ Exception -> 0x0b1b }
            if (r1 != 0) goto L_0x0aee
            boolean r1 = r46.hasMessagesToReply()     // Catch:{ Exception -> 0x0b1b }
            if (r1 == 0) goto L_0x0aee
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0b1b }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b1b }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r3 = org.telegram.messenger.PopupReplyReceiver.class
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x0b1b }
            int r2 = r13.currentAccount     // Catch:{ Exception -> 0x0b1b }
            r1.putExtra(r5, r2)     // Catch:{ Exception -> 0x0b1b }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r3 = 19
            if (r2 > r3) goto L_0x0ad6
            r2 = 2131165467(0x7var_b, float:1.7945152E38)
            java.lang.String r3 = "Reply"
            r4 = 2131626740(0x7f0e0af4, float:1.8880725E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0b1b }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b1b }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5)     // Catch:{ Exception -> 0x0b1b }
            r7.addAction(r2, r3, r1)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x0aee
        L_0x0ad6:
            r2 = 2131165466(0x7var_a, float:1.794515E38)
            java.lang.String r3 = "Reply"
            r4 = 2131626740(0x7f0e0af4, float:1.8880725E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0b1b }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b1b }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5)     // Catch:{ Exception -> 0x0b1b }
            r7.addAction(r2, r3, r1)     // Catch:{ Exception -> 0x0b1b }
        L_0x0aee:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b1b }
            r2 = 26
            if (r1 < r2) goto L_0x0b0f
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
            java.lang.String r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0b1b }
            r12.setChannelId(r1)     // Catch:{ Exception -> 0x0b1b }
            goto L_0x0b12
        L_0x0b0f:
            r12 = r7
            r14 = r24
        L_0x0b12:
            r1 = r47
            r13.showExtraNotifications(r12, r1, r14)     // Catch:{ Exception -> 0x0b1b }
            r46.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0b1b }
            goto L_0x0b27
        L_0x0b1b:
            r0 = move-exception
            goto L_0x0b23
        L_0x0b1d:
            r0 = move-exception
            r13 = r46
            goto L_0x0b23
        L_0x0b21:
            r0 = move-exception
            r13 = r12
        L_0x0b23:
            r1 = r0
        L_0x0b24:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0b27:
            return
        L_0x0b28:
            r13 = r12
            r46.dismissNotification()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    @SuppressLint({"NewApi"})
    private void setNotificationChannel(Notification notification, NotificationCompat.Builder builder, boolean z) {
        if (z) {
            builder.setChannelId(OTHER_NOTIFICATIONS_CHANNEL);
        } else {
            builder.setChannelId(notification.getChannelId());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:224:0x0553, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser((long) r11) != false) goto L_0x0559;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x01d3, code lost:
        if (r7.local_id != 0) goto L_0x01d7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0338  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0357  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x036a  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x03be  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x03d1 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x03e6  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0460  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0476  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0492  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x04a7  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x04cc  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x04d6 A[SYNTHETIC, Splitter:B:192:0x04d6] */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x0536  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x053c  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0547 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0564 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x0586  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x058f  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x05a0  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x05d9  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0620  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x06e8  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x06ef  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0769  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x0855  */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x0876  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x08a5  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x08b5 A[SYNTHETIC, Splitter:B:386:0x08b5] */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0967  */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0978  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0998  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x09f6  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0a29  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0a4a  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0a6c  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0b21  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0b2c  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0b33  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0b43  */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x0b49  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0b4d  */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x0b52  */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x0b66  */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:485:0x0cc8 A[Catch:{ JSONException -> 0x0d1a }] */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0cf1 A[Catch:{ JSONException -> 0x0d1a }] */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0cfc  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0d02 A[Catch:{ JSONException -> 0x0d1a }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01de  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01e8  */
    @android.annotation.SuppressLint({"InlinedApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r79, boolean r80, java.lang.String r81) {
        /*
            r78 = this;
            r8 = r78
            android.app.Notification r9 = r79.build()
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 18
            if (r0 >= r1) goto L_0x001d
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r8.notificationId
            r0.notify(r1, r9)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x001c
            java.lang.String r0 = "show summary notification by SDK check"
            org.telegram.messenger.FileLog.d(r0)
        L_0x001c:
            return
        L_0x001d:
            org.telegram.messenger.AccountInstance r0 = r78.getAccountInstance()
            android.content.SharedPreferences r0 = r0.getNotificationsSettings()
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            android.util.LongSparseArray r11 = new android.util.LongSparseArray
            r11.<init>()
            r12 = 0
            r1 = 0
        L_0x0031:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r8.pushMessages
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x007e
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r8.pushMessages
            java.lang.Object r2 = r2.get(r1)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            long r3 = r2.getDialogId()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "dismissDate"
            r5.append(r6)
            r5.append(r3)
            java.lang.String r5 = r5.toString()
            int r5 = r0.getInt(r5, r12)
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            int r6 = r6.date
            if (r6 > r5) goto L_0x0061
            goto L_0x007b
        L_0x0061:
            java.lang.Object r5 = r11.get(r3)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x0078
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r11.put(r3, r5)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r10.add(r12, r3)
        L_0x0078:
            r5.add(r2)
        L_0x007b:
            int r1 = r1 + 1
            goto L_0x0031
        L_0x007e:
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.wearNotificationsIds
            android.util.LongSparseArray r13 = r0.clone()
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.wearNotificationsIds
            r0.clear()
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            boolean r0 = org.telegram.messenger.WearDataLayerListenerService.isWatchConnected()
            if (r0 == 0) goto L_0x009b
            org.json.JSONArray r0 = new org.json.JSONArray
            r0.<init>()
            r7 = r0
            goto L_0x009c
        L_0x009b:
            r7 = 0
        L_0x009c:
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 27
            r5 = 1
            if (r0 <= r6) goto L_0x00ae
            if (r0 <= r6) goto L_0x00ac
            int r0 = r10.size()
            if (r0 <= r5) goto L_0x00ac
            goto L_0x00ae
        L_0x00ac:
            r4 = 0
            goto L_0x00af
        L_0x00ae:
            r4 = 1
        L_0x00af:
            r3 = 26
            if (r4 == 0) goto L_0x00ba
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r3) goto L_0x00ba
            checkOtherNotificationsChannel()
        L_0x00ba:
            org.telegram.messenger.UserConfig r0 = r78.getUserConfig()
            int r2 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x00d0
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x00cd
            goto L_0x00d0
        L_0x00cd:
            r16 = 0
            goto L_0x00d2
        L_0x00d0:
            r16 = 1
        L_0x00d2:
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r1 = 3
            if (r0 < r1) goto L_0x00dc
            r0 = 7
            r1 = 7
            goto L_0x00e0
        L_0x00dc:
            r0 = 10
            r1 = 10
        L_0x00e0:
            int r6 = r10.size()
            r15 = 0
        L_0x00e5:
            java.lang.String r5 = "id"
            if (r15 >= r6) goto L_0x0d35
            int r0 = r14.size()
            if (r0 < r1) goto L_0x00f1
            goto L_0x0d35
        L_0x00f1:
            java.lang.Object r0 = r10.get(r15)
            java.lang.Long r0 = (java.lang.Long) r0
            r20 = r13
            long r12 = r0.longValue()
            java.lang.Object r0 = r11.get(r12)
            r3 = r0
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            r22 = r1
            r1 = 0
            java.lang.Object r0 = r3.get(r1)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r1 = r0.getId()
            r23 = r11
            int r11 = (int) r12
            r24 = r5
            r5 = 32
            r25 = r14
            r26 = r15
            long r14 = r12 >> r5
            int r15 = (int) r14
            r14 = r20
            java.lang.Object r0 = r14.get(r12)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0135
            if (r11 == 0) goto L_0x0130
            java.lang.Integer r0 = java.lang.Integer.valueOf(r11)
            goto L_0x0138
        L_0x0130:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r15)
            goto L_0x0138
        L_0x0135:
            r14.remove(r12)
        L_0x0138:
            r20 = r0
            if (r7 == 0) goto L_0x0142
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            goto L_0x0143
        L_0x0142:
            r0 = 0
        L_0x0143:
            r5 = 0
            java.lang.Object r28 = r3.get(r5)
            r5 = r28
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            r28 = r0
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            r29 = r14
            int r14 = r0.date
            r30 = r6
            android.util.LongSparseArray r6 = new android.util.LongSparseArray
            r6.<init>()
            r31 = 0
            if (r11 == 0) goto L_0x02a0
            r0 = 777000(0xbdb28, float:1.088809E-39)
            if (r11 == r0) goto L_0x0166
            r0 = 1
            goto L_0x0167
        L_0x0166:
            r0 = 0
        L_0x0167:
            if (r11 <= 0) goto L_0x0203
            r33 = r0
            org.telegram.messenger.MessagesController r0 = r78.getMessagesController()
            r34 = r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r7)
            if (r0 != 0) goto L_0x01bb
            boolean r7 = r5.isFcmMessage()
            if (r7 == 0) goto L_0x0188
            java.lang.String r5 = r5.localName
        L_0x0183:
            r35 = r9
            r36 = r10
            goto L_0x01d6
        L_0x0188:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x01a0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found user to show dialog notification "
            r0.append(r1)
            r0.append(r11)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x01a0:
            r72 = r2
            r14 = r4
            r2 = r9
            r36 = r10
            r19 = r22
            r3 = r25
            r22 = r30
            r15 = r34
            r1 = 26
            r17 = 0
            r18 = 1
            r21 = 0
            r24 = 27
            r9 = r8
            goto L_0x0d1c
        L_0x01bb:
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r0)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            if (r7 == 0) goto L_0x0183
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x0183
            r35 = r9
            r36 = r10
            long r9 = r7.volume_id
            int r37 = (r9 > r31 ? 1 : (r9 == r31 ? 0 : -1))
            if (r37 == 0) goto L_0x01d6
            int r9 = r7.local_id
            if (r9 == 0) goto L_0x01d6
            goto L_0x01d7
        L_0x01d6:
            r7 = 0
        L_0x01d7:
            long r9 = (long) r11
            boolean r9 = org.telegram.messenger.UserObject.isReplyUser((long) r9)
            if (r9 == 0) goto L_0x01e8
            r5 = 2131626733(0x7f0e0aed, float:1.888071E38)
            java.lang.String r9 = "RepliesTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            goto L_0x01f3
        L_0x01e8:
            if (r11 != r2) goto L_0x01f3
            r5 = 2131625856(0x7f0e0780, float:1.8878932E38)
            java.lang.String r9 = "MessageScheduledReminderNotification"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
        L_0x01f3:
            r37 = r4
            r9 = r28
            r10 = 0
            r28 = 0
            r38 = 0
            r76 = r7
            r7 = r0
        L_0x01ff:
            r0 = r76
            goto L_0x0330
        L_0x0203:
            r33 = r0
            r34 = r7
            r35 = r9
            r36 = r10
            org.telegram.messenger.MessagesController r0 = r78.getMessagesController()
            int r7 = -r11
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r7)
            if (r0 != 0) goto L_0x0260
            boolean r7 = r5.isFcmMessage()
            if (r7 == 0) goto L_0x0238
            boolean r7 = r5.isMegagroup()
            java.lang.String r9 = r5.localName
            boolean r5 = r5.localChannel
            r10 = r0
            r37 = r4
            r38 = r7
            r0 = 0
            r7 = 0
            r76 = r28
            r28 = r5
            r5 = r9
            r9 = r76
            goto L_0x0330
        L_0x0238:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0250
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found chat to show dialog notification "
            r0.append(r1)
            r0.append(r11)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x0250:
            r72 = r2
            r14 = r4
            r9 = r8
            r19 = r22
            r3 = r25
            r22 = r30
            r15 = r34
            r2 = r35
            goto L_0x02e3
        L_0x0260:
            boolean r5 = r0.megagroup
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r7 == 0) goto L_0x026e
            boolean r7 = r0.megagroup
            if (r7 != 0) goto L_0x026e
            r7 = 1
            goto L_0x026f
        L_0x026e:
            r7 = 0
        L_0x026f:
            java.lang.String r9 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r10 = r0.photo
            if (r10 == 0) goto L_0x0292
            org.telegram.tgnet.TLRPC$FileLocation r10 = r10.photo_small
            if (r10 == 0) goto L_0x0292
            r37 = r4
            r38 = r5
            long r4 = r10.volume_id
            int r39 = (r4 > r31 ? 1 : (r4 == r31 ? 0 : -1))
            if (r39 == 0) goto L_0x0296
            int r4 = r10.local_id
            if (r4 == 0) goto L_0x0296
            r5 = r9
            r9 = r28
            r28 = r7
            r7 = 0
            r76 = r10
            r10 = r0
            goto L_0x01ff
        L_0x0292:
            r37 = r4
            r38 = r5
        L_0x0296:
            r10 = r0
            r5 = r9
            r9 = r28
            r0 = 0
            r28 = r7
            r7 = 0
            goto L_0x0330
        L_0x02a0:
            r37 = r4
            r34 = r7
            r35 = r9
            r36 = r10
            long r4 = globalSecretChatId
            int r0 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x031c
            org.telegram.messenger.MessagesController r0 = r78.getMessagesController()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r15)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r4)
            if (r0 != 0) goto L_0x02ef
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02d4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found secret chat to show dialog notification "
            r0.append(r1)
            r0.append(r15)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x02d4:
            r72 = r2
            r9 = r8
            r19 = r22
            r3 = r25
            r22 = r30
            r15 = r34
            r2 = r35
            r14 = r37
        L_0x02e3:
            r1 = 26
            r17 = 0
            r18 = 1
            r21 = 0
            r24 = 27
            goto L_0x0d1c
        L_0x02ef:
            org.telegram.messenger.MessagesController r4 = r78.getMessagesController()
            int r5 = r0.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 != 0) goto L_0x031a
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x02d4
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "not found secret chat user to show dialog notification "
            r1.append(r3)
            int r0 = r0.user_id
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02d4
        L_0x031a:
            r0 = r4
            goto L_0x031d
        L_0x031c:
            r0 = 0
        L_0x031d:
            r4 = 2131626883(0x7f0e0b83, float:1.8881015E38)
            java.lang.String r5 = "SecretChatName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r7 = r0
            r0 = 0
            r9 = 0
            r10 = 0
            r28 = 0
            r33 = 0
            r38 = 0
        L_0x0330:
            java.lang.String r4 = "NotificationHiddenChatName"
            r40 = r5
            java.lang.String r5 = "NotificationHiddenName"
            if (r16 == 0) goto L_0x0357
            if (r11 >= 0) goto L_0x0344
            r42 = r10
            r10 = 2131626074(0x7f0e085a, float:1.8879374E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r10)
            goto L_0x034d
        L_0x0344:
            r42 = r10
            r10 = 2131626077(0x7f0e085d, float:1.887938E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r10)
        L_0x034d:
            r33 = r7
            r40 = r14
            r43 = r15
            r7 = 0
            r10 = 0
            r14 = r0
            goto L_0x0368
        L_0x0357:
            r42 = r10
            r10 = r0
            r43 = r15
            r76 = r33
            r33 = r7
            r7 = r76
            r77 = r40
            r40 = r14
            r14 = r77
        L_0x0368:
            if (r10 == 0) goto L_0x03be
            r15 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r10, r15)
            int r15 = android.os.Build.VERSION.SDK_INT
            r45 = r5
            r5 = 28
            if (r15 >= r5) goto L_0x03b8
            org.telegram.messenger.ImageLoader r5 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r15 = "50_50"
            r46 = r4
            r4 = 0
            android.graphics.drawable.BitmapDrawable r5 = r5.getImageFromMemory(r10, r4, r15)
            if (r5 == 0) goto L_0x038d
            android.graphics.Bitmap r5 = r5.getBitmap()
        L_0x038a:
            r15 = r5
            r5 = r0
            goto L_0x03c5
        L_0x038d:
            boolean r5 = r0.exists()     // Catch:{ all -> 0x03bb }
            if (r5 == 0) goto L_0x03b6
            r5 = 1126170624(0x43200000, float:160.0)
            r15 = 1112014848(0x42480000, float:50.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)     // Catch:{ all -> 0x03bb }
            float r15 = (float) r15     // Catch:{ all -> 0x03bb }
            float r5 = r5 / r15
            android.graphics.BitmapFactory$Options r15 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x03bb }
            r15.<init>()     // Catch:{ all -> 0x03bb }
            r17 = 1065353216(0x3var_, float:1.0)
            int r17 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
            if (r17 >= 0) goto L_0x03aa
            r5 = 1
            goto L_0x03ab
        L_0x03aa:
            int r5 = (int) r5     // Catch:{ all -> 0x03bb }
        L_0x03ab:
            r15.inSampleSize = r5     // Catch:{ all -> 0x03bb }
            java.lang.String r5 = r0.getAbsolutePath()     // Catch:{ all -> 0x03bb }
            android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeFile(r5, r15)     // Catch:{ all -> 0x03bb }
            goto L_0x038a
        L_0x03b6:
            r5 = r4
            goto L_0x038a
        L_0x03b8:
            r46 = r4
            r4 = 0
        L_0x03bb:
            r5 = r0
            r15 = r4
            goto L_0x03c5
        L_0x03be:
            r46 = r4
            r45 = r5
            r4 = 0
            r5 = r4
            r15 = r5
        L_0x03c5:
            java.lang.String r4 = "dialog_id"
            r47 = r10
            java.lang.String r10 = "max_id"
            r48 = r5
            java.lang.String r5 = "currentAccount"
            if (r28 == 0) goto L_0x03d3
            if (r38 == 0) goto L_0x0463
        L_0x03d3:
            if (r7 == 0) goto L_0x0463
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0463
            if (r2 == r11) goto L_0x0463
            r49 = r6
            r50 = r7
            long r6 = (long) r11
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r0 != 0) goto L_0x0460
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r7 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r6, r7)
            r0.putExtra(r4, r12)
            r0.putExtra(r10, r1)
            int r6 = r8.currentAccount
            r0.putExtra(r5, r6)
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r7 = r20.intValue()
            r51 = r15
            r15 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r6, r7, r0, r15)
            androidx.core.app.RemoteInput$Builder r6 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r7 = "extra_voice_reply"
            r6.<init>(r7)
            r7 = 2131626740(0x7f0e0af4, float:1.8880725E38)
            java.lang.String r15 = "Reply"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r15, r7)
            r6.setLabel(r7)
            androidx.core.app.RemoteInput r6 = r6.build()
            if (r11 >= 0) goto L_0x0433
            r15 = 1
            java.lang.Object[] r7 = new java.lang.Object[r15]
            r15 = 0
            r7[r15] = r14
            java.lang.String r15 = "ReplyToGroup"
            r53 = r1
            r1 = 2131626741(0x7f0e0af5, float:1.8880727E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r15, r1, r7)
            goto L_0x0444
        L_0x0433:
            r53 = r1
            r1 = 2131626742(0x7f0e0af6, float:1.8880729E38)
            r7 = 1
            java.lang.Object[] r15 = new java.lang.Object[r7]
            r7 = 0
            r15[r7] = r14
            java.lang.String r7 = "ReplyToUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r1, r15)
        L_0x0444:
            androidx.core.app.NotificationCompat$Action$Builder r7 = new androidx.core.app.NotificationCompat$Action$Builder
            r15 = 2131165511(0x7var_, float:1.7945241E38)
            r7.<init>(r15, r1, r0)
            r1 = 1
            r7.setAllowGeneratedReplies(r1)
            r7.setSemanticAction(r1)
            r7.addRemoteInput(r6)
            r1 = 0
            r7.setShowsUserInterface(r1)
            androidx.core.app.NotificationCompat$Action r0 = r7.build()
            r1 = r0
            goto L_0x046c
        L_0x0460:
            r53 = r1
            goto L_0x0469
        L_0x0463:
            r53 = r1
            r49 = r6
            r50 = r7
        L_0x0469:
            r51 = r15
            r1 = 0
        L_0x046c:
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            java.lang.Object r0 = r0.get(r12)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x047b
            r6 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r6)
        L_0x047b:
            int r0 = r0.intValue()
            int r6 = r3.size()
            int r0 = java.lang.Math.max(r0, r6)
            r6 = 2
            r7 = 1
            if (r0 <= r7) goto L_0x04a7
            int r15 = android.os.Build.VERSION.SDK_INT
            r7 = 28
            if (r15 < r7) goto L_0x0492
            goto L_0x04a7
        L_0x0492:
            java.lang.Object[] r7 = new java.lang.Object[r6]
            r15 = 0
            r7[r15] = r14
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r15 = 1
            r7[r15] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r7)
            r52 = r0
            goto L_0x04a9
        L_0x04a7:
            r52 = r14
        L_0x04a9:
            long r6 = (long) r2
            r15 = r49
            java.lang.Object r0 = r15.get(r6)
            r49 = r0
            androidx.core.app.Person r49 = (androidx.core.app.Person) r49
            int r0 = android.os.Build.VERSION.SDK_INT
            r54 = r10
            r10 = 28
            if (r0 < r10) goto L_0x052c
            if (r49 != 0) goto L_0x052c
            org.telegram.messenger.MessagesController r0 = r78.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r10)
            if (r0 != 0) goto L_0x04d4
            org.telegram.messenger.UserConfig r0 = r78.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x04d4:
            if (r0 == 0) goto L_0x052c
            org.telegram.tgnet.TLRPC$UserProfilePhoto r10 = r0.photo     // Catch:{ all -> 0x0523 }
            if (r10 == 0) goto L_0x052c
            org.telegram.tgnet.TLRPC$UserProfilePhoto r10 = r0.photo     // Catch:{ all -> 0x0523 }
            org.telegram.tgnet.TLRPC$FileLocation r10 = r10.photo_small     // Catch:{ all -> 0x0523 }
            if (r10 == 0) goto L_0x052c
            org.telegram.tgnet.TLRPC$UserProfilePhoto r10 = r0.photo     // Catch:{ all -> 0x0523 }
            org.telegram.tgnet.TLRPC$FileLocation r10 = r10.photo_small     // Catch:{ all -> 0x0523 }
            r56 = r1
            r55 = r2
            long r1 = r10.volume_id     // Catch:{ all -> 0x0521 }
            int r10 = (r1 > r31 ? 1 : (r1 == r31 ? 0 : -1))
            if (r10 == 0) goto L_0x0530
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo     // Catch:{ all -> 0x0521 }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ all -> 0x0521 }
            int r1 = r1.local_id     // Catch:{ all -> 0x0521 }
            if (r1 == 0) goto L_0x0530
            androidx.core.app.Person$Builder r1 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x0521 }
            r1.<init>()     // Catch:{ all -> 0x0521 }
            java.lang.String r2 = "FromYou"
            r10 = 2131625494(0x7f0e0616, float:1.8878198E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r10)     // Catch:{ all -> 0x0521 }
            r1.setName(r2)     // Catch:{ all -> 0x0521 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x0521 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x0521 }
            r2 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r2)     // Catch:{ all -> 0x0521 }
            r8.loadRoundAvatar(r0, r1)     // Catch:{ all -> 0x0521 }
            androidx.core.app.Person r1 = r1.build()     // Catch:{ all -> 0x0521 }
            r15.put(r6, r1)     // Catch:{ all -> 0x051d }
            r49 = r1
            goto L_0x0530
        L_0x051d:
            r0 = move-exception
            r49 = r1
            goto L_0x0528
        L_0x0521:
            r0 = move-exception
            goto L_0x0528
        L_0x0523:
            r0 = move-exception
            r56 = r1
            r55 = r2
        L_0x0528:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0530
        L_0x052c:
            r56 = r1
            r55 = r2
        L_0x0530:
            r0 = r49
            java.lang.String r1 = ""
            if (r0 == 0) goto L_0x053c
            androidx.core.app.NotificationCompat$MessagingStyle r2 = new androidx.core.app.NotificationCompat$MessagingStyle
            r2.<init>((androidx.core.app.Person) r0)
            goto L_0x0541
        L_0x053c:
            androidx.core.app.NotificationCompat$MessagingStyle r2 = new androidx.core.app.NotificationCompat$MessagingStyle
            r2.<init>((java.lang.CharSequence) r1)
        L_0x0541:
            int r0 = android.os.Build.VERSION.SDK_INT
            r10 = 28
            if (r0 < r10) goto L_0x0556
            if (r11 >= 0) goto L_0x054b
            if (r28 == 0) goto L_0x0556
        L_0x054b:
            r10 = r4
            r49 = r5
            long r4 = (long) r11
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r0 == 0) goto L_0x055e
            goto L_0x0559
        L_0x0556:
            r10 = r4
            r49 = r5
        L_0x0559:
            r4 = r52
            r2.setConversationTitle(r4)
        L_0x055e:
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 28
            if (r0 < r4) goto L_0x0572
            if (r28 != 0) goto L_0x0568
            if (r11 < 0) goto L_0x0572
        L_0x0568:
            long r4 = (long) r11
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r0 == 0) goto L_0x0570
            goto L_0x0572
        L_0x0570:
            r0 = 0
            goto L_0x0573
        L_0x0572:
            r0 = 1
        L_0x0573:
            r2.setGroupConversation(r0)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r52 = r10
            r5 = 1
            java.lang.String[] r10 = new java.lang.String[r5]
            r57 = r2
            boolean[] r2 = new boolean[r5]
            if (r9 == 0) goto L_0x058f
            org.json.JSONArray r0 = new org.json.JSONArray
            r0.<init>()
            r58 = r9
            r9 = r0
            goto L_0x0592
        L_0x058f:
            r58 = r9
            r9 = 0
        L_0x0592:
            int r0 = r3.size()
            int r0 = r0 - r5
            r5 = r0
            r59 = 0
            r60 = 0
        L_0x059c:
            r61 = 1000(0x3e8, double:4.94E-321)
            if (r5 < 0) goto L_0x0926
            java.lang.Object r0 = r3.get(r5)
            r63 = r3
            r3 = r0
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            java.lang.String r0 = r8.getShortStringForMessage(r3, r10, r2)
            int r64 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r64 != 0) goto L_0x05b6
            r19 = 0
            r10[r19] = r14
            goto L_0x05d3
        L_0x05b6:
            r19 = 0
            if (r11 >= 0) goto L_0x05d3
            r64 = r14
            org.telegram.tgnet.TLRPC$Message r14 = r3.messageOwner
            boolean r14 = r14.from_scheduled
            if (r14 == 0) goto L_0x05d0
            r14 = 2131626120(0x7f0e0888, float:1.8879467E38)
            r65 = r5
            java.lang.String r5 = "NotificationMessageScheduledName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r14)
            r10[r19] = r5
            goto L_0x05d7
        L_0x05d0:
            r65 = r5
            goto L_0x05d7
        L_0x05d3:
            r65 = r5
            r64 = r14
        L_0x05d7:
            if (r0 != 0) goto L_0x0620
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0611
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "message text is null for "
            r0.append(r5)
            int r5 = r3.getId()
            r0.append(r5)
            java.lang.String r5 = " did = "
            r0.append(r5)
            r14 = r9
            long r8 = r3.getDialogId()
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            r9 = r78
            r66 = r6
            r68 = r10
            r69 = r12
            r8 = r14
            r41 = r45
            r6 = r57
            goto L_0x090f
        L_0x0611:
            r66 = r6
            r8 = r9
            r68 = r10
            r69 = r12
            r41 = r45
            r6 = r57
            r9 = r78
            goto L_0x090f
        L_0x0620:
            r14 = r9
            int r5 = r4.length()
            if (r5 <= 0) goto L_0x062c
            java.lang.String r5 = "\n\n"
            r4.append(r5)
        L_0x062c:
            int r5 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r5 == 0) goto L_0x0654
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner
            boolean r5 = r5.from_scheduled
            if (r5 == 0) goto L_0x0654
            if (r11 <= 0) goto L_0x0654
            r5 = 2
            java.lang.Object[] r8 = new java.lang.Object[r5]
            r9 = 2131626120(0x7f0e0888, float:1.8879467E38)
            java.lang.String r5 = "NotificationMessageScheduledName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r9)
            r9 = 0
            r8[r9] = r5
            r5 = 1
            r8[r5] = r0
            java.lang.String r0 = "%1$s: %2$s"
            java.lang.String r0 = java.lang.String.format(r0, r8)
            r4.append(r0)
            goto L_0x0670
        L_0x0654:
            r9 = 0
            r5 = r10[r9]
            if (r5 == 0) goto L_0x066d
            r5 = 2
            java.lang.Object[] r8 = new java.lang.Object[r5]
            r19 = r10[r9]
            r8[r9] = r19
            r9 = 1
            r8[r9] = r0
            java.lang.String r9 = "%1$s: %2$s"
            java.lang.String r8 = java.lang.String.format(r9, r8)
            r4.append(r8)
            goto L_0x0670
        L_0x066d:
            r4.append(r0)
        L_0x0670:
            r8 = r0
            if (r11 <= 0) goto L_0x0677
            r66 = r6
            long r5 = (long) r11
            goto L_0x0686
        L_0x0677:
            r66 = r6
            if (r28 == 0) goto L_0x067e
            int r0 = -r11
        L_0x067c:
            long r5 = (long) r0
            goto L_0x0686
        L_0x067e:
            if (r11 >= 0) goto L_0x0685
            int r0 = r3.getSenderId()
            goto L_0x067c
        L_0x0685:
            r5 = r12
        L_0x0686:
            java.lang.Object r0 = r15.get(r5)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r7 = 0
            r9 = r10[r7]
            if (r9 != 0) goto L_0x06ce
            if (r16 == 0) goto L_0x06c7
            if (r11 >= 0) goto L_0x06b7
            if (r28 == 0) goto L_0x06a9
            int r7 = android.os.Build.VERSION.SDK_INT
            r9 = 27
            if (r7 <= r9) goto L_0x06c7
            r9 = r46
            r7 = 2131626074(0x7f0e085a, float:1.8879374E38)
            java.lang.String r39 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r7 = r39
            goto L_0x06b4
        L_0x06a9:
            r9 = r46
            r7 = 2131626075(0x7f0e085b, float:1.8879376E38)
            java.lang.String r9 = "NotificationHiddenChatUserName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
        L_0x06b4:
            r9 = r45
            goto L_0x06d9
        L_0x06b7:
            int r7 = android.os.Build.VERSION.SDK_INT
            r9 = 27
            if (r7 <= r9) goto L_0x06c7
            r9 = r45
            r7 = 2131626077(0x7f0e085d, float:1.887938E38)
            java.lang.String r41 = org.telegram.messenger.LocaleController.getString(r9, r7)
            goto L_0x06d7
        L_0x06c7:
            r9 = r45
            r7 = 2131626077(0x7f0e085d, float:1.887938E38)
            r7 = r1
            goto L_0x06d9
        L_0x06ce:
            r9 = r45
            r7 = 2131626077(0x7f0e085d, float:1.887938E38)
            r19 = 0
            r41 = r10[r19]
        L_0x06d7:
            r7 = r41
        L_0x06d9:
            r41 = r9
            if (r0 == 0) goto L_0x06ef
            java.lang.CharSequence r9 = r0.getName()
            boolean r9 = android.text.TextUtils.equals(r9, r7)
            if (r9 != 0) goto L_0x06e8
            goto L_0x06ef
        L_0x06e8:
            r9 = r78
            r5 = r0
            r68 = r10
            goto L_0x0767
        L_0x06ef:
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            r0.setName(r7)
            r7 = 0
            boolean r9 = r2[r7]
            if (r9 == 0) goto L_0x075b
            if (r11 == 0) goto L_0x075b
            int r7 = android.os.Build.VERSION.SDK_INT
            r9 = 28
            if (r7 < r9) goto L_0x075b
            if (r11 > 0) goto L_0x0751
            if (r28 == 0) goto L_0x0709
            goto L_0x0751
        L_0x0709:
            if (r11 >= 0) goto L_0x074b
            int r7 = r3.getSenderId()
            org.telegram.messenger.MessagesController r9 = r78.getMessagesController()
            r68 = r10
            java.lang.Integer r10 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r10)
            if (r9 != 0) goto L_0x0731
            org.telegram.messenger.MessagesStorage r9 = r78.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r9 = r9.getUserSync(r7)
            if (r9 == 0) goto L_0x0731
            org.telegram.messenger.MessagesController r7 = r78.getMessagesController()
            r10 = 1
            r7.putUser(r9, r10)
        L_0x0731:
            if (r9 == 0) goto L_0x074d
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r9.photo
            if (r7 == 0) goto L_0x074d
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x074d
            long r9 = r7.volume_id
            int r69 = (r9 > r31 ? 1 : (r9 == r31 ? 0 : -1))
            if (r69 == 0) goto L_0x074d
            int r9 = r7.local_id
            if (r9 == 0) goto L_0x074d
            r9 = 1
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r9)
            goto L_0x074e
        L_0x074b:
            r68 = r10
        L_0x074d:
            r7 = 0
        L_0x074e:
            r9 = r78
            goto L_0x0757
        L_0x0751:
            r68 = r10
            r9 = r78
            r7 = r48
        L_0x0757:
            r9.loadRoundAvatar(r7, r0)
            goto L_0x075f
        L_0x075b:
            r9 = r78
            r68 = r10
        L_0x075f:
            androidx.core.app.Person r0 = r0.build()
            r15.put(r5, r0)
            r5 = r0
        L_0x0767:
            if (r11 == 0) goto L_0x08a5
            r6 = 0
            boolean r0 = r2[r6]
            if (r0 == 0) goto L_0x084e
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 28
            if (r0 < r6) goto L_0x084e
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r7 = "activity"
            java.lang.Object r0 = r0.getSystemService(r7)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x084e
            if (r16 != 0) goto L_0x084e
            boolean r0 = r3.isSecretMedia()
            if (r0 != 0) goto L_0x084e
            int r0 = r3.type
            r7 = 1
            if (r0 == r7) goto L_0x0797
            boolean r0 = r3.isSticker()
            if (r0 == 0) goto L_0x084e
        L_0x0797:
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r7 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r10 = r3.messageOwner
            int r10 = r10.date
            r69 = r12
            long r12 = (long) r10
            long r12 = r12 * r61
            r7.<init>(r8, r12, r5)
            boolean r10 = r3.isSticker()
            if (r10 == 0) goto L_0x07b4
            java.lang.String r10 = "image/webp"
            goto L_0x07b6
        L_0x07b4:
            java.lang.String r10 = "image/jpeg"
        L_0x07b6:
            boolean r12 = r0.exists()
            if (r12 == 0) goto L_0x07ca
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x07c5 }
            java.lang.String r13 = "org.telegram.messenger.beta.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r12, r13, r0)     // Catch:{ Exception -> 0x07c5 }
            goto L_0x081c
        L_0x07c5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x081b
        L_0x07ca:
            org.telegram.messenger.FileLoader r12 = r78.getFileLoader()
            java.lang.String r13 = r0.getName()
            boolean r12 = r12.isLoadingFile(r13)
            if (r12 == 0) goto L_0x081b
            android.net.Uri$Builder r12 = new android.net.Uri$Builder
            r12.<init>()
            java.lang.String r13 = "content"
            android.net.Uri$Builder r12 = r12.scheme(r13)
            java.lang.String r13 = "org.telegram.messenger.beta.notification_image_provider"
            android.net.Uri$Builder r12 = r12.authority(r13)
            java.lang.String r13 = "msg_media_raw"
            android.net.Uri$Builder r12 = r12.appendPath(r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            int r6 = r9.currentAccount
            r13.append(r6)
            r13.append(r1)
            java.lang.String r6 = r13.toString()
            android.net.Uri$Builder r6 = r12.appendPath(r6)
            java.lang.String r12 = r0.getName()
            android.net.Uri$Builder r6 = r6.appendPath(r12)
            java.lang.String r0 = r0.getAbsolutePath()
            java.lang.String r12 = "final_path"
            android.net.Uri$Builder r0 = r6.appendQueryParameter(r12, r0)
            android.net.Uri r0 = r0.build()
            goto L_0x081c
        L_0x081b:
            r0 = 0
        L_0x081c:
            if (r0 == 0) goto L_0x0850
            r7.setData(r10, r0)
            r6 = r57
            r6.addMessage(r7)
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r10 = "com.android.systemui"
            r12 = 1
            r7.grantUriPermission(r10, r0, r12)
            org.telegram.messenger.-$$Lambda$NotificationsController$2iZFI3opoasnRhiUslwS5Iqt9vs r7 = new org.telegram.messenger.-$$Lambda$NotificationsController$2iZFI3opoasnRhiUslwS5Iqt9vs
            r7.<init>(r0)
            r12 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7, r12)
            java.lang.CharSequence r0 = r3.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x084c
            java.lang.CharSequence r0 = r3.caption
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner
            int r7 = r7.date
            long r12 = (long) r7
            long r12 = r12 * r61
            r6.addMessage(r0, r12, r5)
        L_0x084c:
            r0 = 1
            goto L_0x0853
        L_0x084e:
            r69 = r12
        L_0x0850:
            r6 = r57
            r0 = 0
        L_0x0853:
            if (r0 != 0) goto L_0x085f
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            int r0 = r0.date
            long r12 = (long) r0
            long r12 = r12 * r61
            r6.addMessage(r8, r12, r5)
        L_0x085f:
            r5 = 0
            boolean r0 = r2[r5]
            if (r0 == 0) goto L_0x08b3
            if (r16 != 0) goto L_0x08b3
            boolean r0 = r3.isVoice()
            if (r0 == 0) goto L_0x08b3
            java.util.List r0 = r6.getMessages()
            boolean r5 = r0.isEmpty()
            if (r5 != 0) goto L_0x08b3
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner
            java.io.File r5 = org.telegram.messenger.FileLoader.getPathToMessage(r5)
            int r7 = android.os.Build.VERSION.SDK_INT
            r10 = 24
            if (r7 < r10) goto L_0x088d
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x088b }
            java.lang.String r10 = "org.telegram.messenger.beta.provider"
            android.net.Uri r5 = androidx.core.content.FileProvider.getUriForFile(r7, r10, r5)     // Catch:{ Exception -> 0x088b }
            goto L_0x0891
        L_0x088b:
            r5 = 0
            goto L_0x0891
        L_0x088d:
            android.net.Uri r5 = android.net.Uri.fromFile(r5)
        L_0x0891:
            if (r5 == 0) goto L_0x08b3
            int r7 = r0.size()
            r10 = 1
            int r7 = r7 - r10
            java.lang.Object r0 = r0.get(r7)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r0
            java.lang.String r7 = "audio/ogg"
            r0.setData(r7, r5)
            goto L_0x08b3
        L_0x08a5:
            r69 = r12
            r6 = r57
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            int r0 = r0.date
            long r12 = (long) r0
            long r12 = r12 * r61
            r6.addMessage(r8, r12, r5)
        L_0x08b3:
            if (r14 == 0) goto L_0x08f7
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x08f7 }
            r0.<init>()     // Catch:{ JSONException -> 0x08f7 }
            java.lang.String r5 = "text"
            r0.put(r5, r8)     // Catch:{ JSONException -> 0x08f7 }
            java.lang.String r5 = "date"
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner     // Catch:{ JSONException -> 0x08f7 }
            int r7 = r7.date     // Catch:{ JSONException -> 0x08f7 }
            r0.put(r5, r7)     // Catch:{ JSONException -> 0x08f7 }
            boolean r5 = r3.isFromUser()     // Catch:{ JSONException -> 0x08f7 }
            if (r5 == 0) goto L_0x08f0
            if (r11 >= 0) goto L_0x08f0
            org.telegram.messenger.MessagesController r5 = r78.getMessagesController()     // Catch:{ JSONException -> 0x08f7 }
            int r7 = r3.getSenderId()     // Catch:{ JSONException -> 0x08f7 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ JSONException -> 0x08f7 }
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r7)     // Catch:{ JSONException -> 0x08f7 }
            if (r5 == 0) goto L_0x08f0
            java.lang.String r7 = "fname"
            java.lang.String r8 = r5.first_name     // Catch:{ JSONException -> 0x08f7 }
            r0.put(r7, r8)     // Catch:{ JSONException -> 0x08f7 }
            java.lang.String r7 = "lname"
            java.lang.String r5 = r5.last_name     // Catch:{ JSONException -> 0x08f7 }
            r0.put(r7, r5)     // Catch:{ JSONException -> 0x08f7 }
        L_0x08f0:
            r8 = r14
            r8.put(r0)     // Catch:{ JSONException -> 0x08f5 }
            goto L_0x08f8
        L_0x08f5:
            goto L_0x08f8
        L_0x08f7:
            r8 = r14
        L_0x08f8:
            r12 = 777000(0xbdb28, double:3.83889E-318)
            int r0 = (r69 > r12 ? 1 : (r69 == r12 ? 0 : -1))
            if (r0 != 0) goto L_0x090f
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x090f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r3 = r3.getId()
            r60 = r0
            r59 = r3
        L_0x090f:
            int r5 = r65 + -1
            r57 = r6
            r45 = r41
            r3 = r63
            r14 = r64
            r6 = r66
            r10 = r68
            r12 = r69
            r76 = r9
            r9 = r8
            r8 = r76
            goto L_0x059c
        L_0x0926:
            r63 = r3
            r69 = r12
            r64 = r14
            r6 = r57
            r76 = r9
            r9 = r8
            r8 = r76
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.ui.LaunchActivity> r3 = org.telegram.ui.LaunchActivity.class
            r0.<init>(r2, r3)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "com.tmessages.openchat"
            r2.append(r3)
            double r12 = java.lang.Math.random()
            r2.append(r12)
            r3 = 2147483647(0x7fffffff, float:NaN)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.setAction(r2)
            r2 = 32768(0x8000, float:4.5918E-41)
            r0.setFlags(r2)
            java.lang.String r2 = "android.intent.category.LAUNCHER"
            r0.addCategory(r2)
            if (r11 == 0) goto L_0x0978
            if (r11 <= 0) goto L_0x096f
            java.lang.String r2 = "userId"
            r0.putExtra(r2, r11)
            goto L_0x0975
        L_0x096f:
            int r2 = -r11
            java.lang.String r3 = "chatId"
            r0.putExtra(r3, r2)
        L_0x0975:
            r3 = r43
            goto L_0x097f
        L_0x0978:
            java.lang.String r2 = "encId"
            r3 = r43
            r0.putExtra(r2, r3)
        L_0x097f:
            int r2 = r9.currentAccount
            r5 = r49
            r0.putExtra(r5, r2)
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            r7 = 1073741824(0x40000000, float:2.0)
            r10 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r2, r10, r0, r7)
            androidx.core.app.NotificationCompat$WearableExtender r2 = new androidx.core.app.NotificationCompat$WearableExtender
            r2.<init>()
            r7 = r56
            if (r56 == 0) goto L_0x099b
            r2.addAction(r7)
        L_0x099b:
            android.content.Intent r10 = new android.content.Intent
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r13 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r10.<init>(r12, r13)
            r12 = 32
            r10.addFlags(r12)
            java.lang.String r12 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r10.setAction(r12)
            r14 = r52
            r12 = r69
            r10.putExtra(r14, r12)
            r27 = r8
            r14 = r53
            r8 = r54
            r10.putExtra(r8, r14)
            int r8 = r9.currentAccount
            r10.putExtra(r5, r8)
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            r49 = r15
            int r15 = r20.intValue()
            r56 = r7
            r7 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r8 = android.app.PendingIntent.getBroadcast(r8, r15, r10, r7)
            androidx.core.app.NotificationCompat$Action$Builder r7 = new androidx.core.app.NotificationCompat$Action$Builder
            r10 = 2131165654(0x7var_d6, float:1.7945531E38)
            r15 = 2131625783(0x7f0e0737, float:1.8878784E38)
            r31 = r5
            java.lang.String r5 = "MarkAsRead"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r15)
            r7.<init>(r10, r5, r8)
            r5 = 2
            r7.setSemanticAction(r5)
            r5 = 0
            r7.setShowsUserInterface(r5)
            androidx.core.app.NotificationCompat$Action r5 = r7.build()
            java.lang.String r8 = "_"
            if (r11 == 0) goto L_0x0a29
            if (r11 <= 0) goto L_0x0a10
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r7 = "tguser"
            r3.append(r7)
            r3.append(r11)
            r3.append(r8)
            r3.append(r14)
            java.lang.String r3 = r3.toString()
            goto L_0x0a48
        L_0x0a10:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r7 = "tgchat"
            r3.append(r7)
            int r7 = -r11
            r3.append(r7)
            r3.append(r8)
            r3.append(r14)
            java.lang.String r3 = r3.toString()
            goto L_0x0a48
        L_0x0a29:
            long r43 = globalSecretChatId
            int r7 = (r12 > r43 ? 1 : (r12 == r43 ? 0 : -1))
            if (r7 == 0) goto L_0x0a47
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r10 = "tgenc"
            r7.append(r10)
            r7.append(r3)
            r7.append(r8)
            r7.append(r14)
            java.lang.String r3 = r7.toString()
            goto L_0x0a48
        L_0x0a47:
            r3 = 0
        L_0x0a48:
            if (r3 == 0) goto L_0x0a6c
            r2.setDismissalId(r3)
            androidx.core.app.NotificationCompat$WearableExtender r7 = new androidx.core.app.NotificationCompat$WearableExtender
            r7.<init>()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r15 = "summary_"
            r10.append(r15)
            r10.append(r3)
            java.lang.String r3 = r10.toString()
            r7.setDismissalId(r3)
            r10 = r79
            r10.extend(r7)
            goto L_0x0a6e
        L_0x0a6c:
            r10 = r79
        L_0x0a6e:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r7 = "tgaccount"
            r3.append(r7)
            r7 = r55
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r2.setBridgeTag(r3)
            r3 = r63
            r15 = 0
            java.lang.Object r32 = r3.get(r15)
            r15 = r32
            org.telegram.messenger.MessageObject r15 = (org.telegram.messenger.MessageObject) r15
            org.telegram.tgnet.TLRPC$Message r15 = r15.messageOwner
            int r15 = r15.date
            r53 = r14
            long r14 = (long) r15
            long r14 = r14 * r61
            androidx.core.app.NotificationCompat$Builder r10 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            r10.<init>(r7)
            r7 = r64
            r10.setContentTitle(r7)
            r32 = r8
            r8 = 2131165796(0x7var_, float:1.794582E38)
            r10.setSmallIcon(r8)
            java.lang.String r4 = r4.toString()
            r10.setContentText(r4)
            r4 = 1
            r10.setAutoCancel(r4)
            int r3 = r3.size()
            r10.setNumber(r3)
            r3 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r10.setColor(r3)
            r3 = 0
            r10.setGroupSummary(r3)
            r10.setWhen(r14)
            r10.setShowWhen(r4)
            r10.setStyle(r6)
            r10.setContentIntent(r0)
            r10.extend(r2)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            r1 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r1 = r1 - r14
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.setSortKey(r0)
            java.lang.String r0 = "msg"
            r10.setCategory(r0)
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r2 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r1, r2)
            java.lang.String r1 = "messageDate"
            r8 = r40
            r0.putExtra(r1, r8)
            java.lang.String r1 = "dialogId"
            r0.putExtra(r1, r12)
            int r1 = r9.currentAccount
            r2 = r31
            r0.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r3 = r20.intValue()
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r1, r3, r0, r4)
            r10.setDeleteIntent(r0)
            if (r37 == 0) goto L_0x0b2a
            java.lang.String r0 = r9.notificationGroup
            r10.setGroup(r0)
            r1 = 1
            r10.setGroupAlertBehavior(r1)
        L_0x0b2a:
            if (r56 == 0) goto L_0x0b31
            r1 = r56
            r10.addAction(r1)
        L_0x0b31:
            if (r16 != 0) goto L_0x0b36
            r10.addAction(r5)
        L_0x0b36:
            int r0 = r36.size()
            r5 = 1
            if (r0 != r5) goto L_0x0b49
            boolean r0 = android.text.TextUtils.isEmpty(r81)
            if (r0 != 0) goto L_0x0b49
            r14 = r81
            r10.setSubText(r14)
            goto L_0x0b4b
        L_0x0b49:
            r14 = r81
        L_0x0b4b:
            if (r11 != 0) goto L_0x0b50
            r10.setLocalOnly(r5)
        L_0x0b50:
            if (r51 == 0) goto L_0x0b57
            r4 = r51
            r10.setLargeIcon(r4)
        L_0x0b57:
            r1 = 0
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1)
            if (r0 != 0) goto L_0x0CLASSNAME
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0CLASSNAME
            r1 = r60
            if (r1 == 0) goto L_0x0bf3
            int r0 = r1.size()
            r3 = 0
        L_0x0b6b:
            if (r3 >= r0) goto L_0x0bf3
            java.lang.Object r4 = r1.get(r3)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r4 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r6 = r4.buttons
            int r6 = r6.size()
            r15 = 0
        L_0x0b7a:
            if (r15 >= r6) goto L_0x0be3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r5 = r4.buttons
            java.lang.Object r5 = r5.get(r15)
            org.telegram.tgnet.TLRPC$KeyboardButton r5 = (org.telegram.tgnet.TLRPC$KeyboardButton) r5
            r31 = r0
            boolean r0 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback
            if (r0 == 0) goto L_0x0bca
            android.content.Intent r0 = new android.content.Intent
            r39 = r1
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r40 = r4
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r4 = org.telegram.messenger.NotificationCallbackReceiver.class
            r0.<init>(r1, r4)
            int r1 = r9.currentAccount
            r0.putExtra(r2, r1)
            java.lang.String r1 = "did"
            r0.putExtra(r1, r12)
            byte[] r1 = r5.data
            if (r1 == 0) goto L_0x0baa
            java.lang.String r4 = "data"
            r0.putExtra(r4, r1)
        L_0x0baa:
            java.lang.String r1 = "mid"
            r4 = r59
            r0.putExtra(r1, r4)
            java.lang.String r1 = r5.text
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            r41 = r2
            int r2 = r9.lastButtonId
            r43 = r4
            int r4 = r2 + 1
            r9.lastButtonId = r4
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r5, r2, r0, r4)
            r5 = 0
            r10.addAction(r5, r1, r0)
            goto L_0x0bd5
        L_0x0bca:
            r39 = r1
            r41 = r2
            r40 = r4
            r43 = r59
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 0
        L_0x0bd5:
            int r15 = r15 + 1
            r0 = r31
            r1 = r39
            r4 = r40
            r2 = r41
            r59 = r43
            r5 = 1
            goto L_0x0b7a
        L_0x0be3:
            r31 = r0
            r39 = r1
            r41 = r2
            r43 = r59
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 0
            int r3 = r3 + 1
            r5 = 1
            goto L_0x0b6b
        L_0x0bf3:
            r5 = 0
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 29
            if (r0 < r1) goto L_0x0CLASSNAME
            if (r11 == 0) goto L_0x0CLASSNAME
            long r0 = (long) r11
            r2 = r49
            java.lang.Object r0 = r2.get(r0)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r19 = r22
            r15 = r53
            r1 = r78
            r6 = r55
            r2 = r10
            r4 = 26
            r3 = r11
            r40 = r8
            r14 = r37
            r8 = 26
            r17 = 0
            r4 = r7
            r71 = r24
            r18 = 1
            r21 = 0
            r5 = r33
            r72 = r6
            r22 = r30
            r24 = 27
            r6 = r42
            r75 = r7
            r8 = r33
            r73 = r34
            r74 = r50
            r7 = r0
            r1.createNotificationShortcut(r2, r3, r4, r5, r6, r7)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r75 = r7
            r40 = r8
            r19 = r22
            r71 = r24
            r22 = r30
            r8 = r33
            r73 = r34
            r14 = r37
            r74 = r50
            r15 = r53
            r72 = r55
            r17 = 0
            r18 = 1
            r21 = 0
            r24 = 27
        L_0x0CLASSNAME:
            if (r42 != 0) goto L_0x0CLASSNAME
            if (r8 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = r8.phone
            if (r0 == 0) goto L_0x0CLASSNAME
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "tel:+"
            r0.append(r1)
            java.lang.String r1 = r8.phone
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.addPerson(r0)
        L_0x0CLASSNAME:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 26
            r2 = r35
            if (r0 < r1) goto L_0x0CLASSNAME
            r9.setNotificationChannel(r2, r10, r14)
        L_0x0CLASSNAME:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            int r3 = r20.intValue()
            android.app.Notification r4 = r10.build()
            r0.<init>(r3, r4)
            r3 = r25
            r3.add(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r9.wearNotificationsIds
            r4 = r20
            r0.put(r12, r4)
            if (r11 == 0) goto L_0x0d1a
            if (r58 == 0) goto L_0x0d1a
            java.lang.String r0 = "reply"
            r5 = r58
            r4 = r74
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0d1a }
            java.lang.String r0 = "name"
            r4 = r75
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0d1a }
            r4 = r54
            r5.put(r4, r15)     // Catch:{ JSONException -> 0x0d1a }
            java.lang.String r0 = "max_date"
            r4 = r40
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0d1a }
            int r0 = java.lang.Math.abs(r11)     // Catch:{ JSONException -> 0x0d1a }
            r4 = r71
            r5.put(r4, r0)     // Catch:{ JSONException -> 0x0d1a }
            if (r47 == 0) goto L_0x0cef
            java.lang.String r0 = "photo"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0d1a }
            r4.<init>()     // Catch:{ JSONException -> 0x0d1a }
            r6 = r47
            int r7 = r6.dc_id     // Catch:{ JSONException -> 0x0d1a }
            r4.append(r7)     // Catch:{ JSONException -> 0x0d1a }
            r7 = r32
            r4.append(r7)     // Catch:{ JSONException -> 0x0d1a }
            long r12 = r6.volume_id     // Catch:{ JSONException -> 0x0d1a }
            r4.append(r12)     // Catch:{ JSONException -> 0x0d1a }
            r4.append(r7)     // Catch:{ JSONException -> 0x0d1a }
            long r6 = r6.secret     // Catch:{ JSONException -> 0x0d1a }
            r4.append(r6)     // Catch:{ JSONException -> 0x0d1a }
            java.lang.String r4 = r4.toString()     // Catch:{ JSONException -> 0x0d1a }
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0d1a }
        L_0x0cef:
            if (r27 == 0) goto L_0x0cf8
            java.lang.String r0 = "msgs"
            r4 = r27
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0d1a }
        L_0x0cf8:
            java.lang.String r0 = "type"
            if (r11 <= 0) goto L_0x0d02
            java.lang.String r4 = "user"
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0d1a }
            goto L_0x0d14
        L_0x0d02:
            if (r11 >= 0) goto L_0x0d14
            if (r28 != 0) goto L_0x0d0f
            if (r38 == 0) goto L_0x0d09
            goto L_0x0d0f
        L_0x0d09:
            java.lang.String r4 = "group"
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0d1a }
            goto L_0x0d14
        L_0x0d0f:
            java.lang.String r4 = "channel"
            r5.put(r0, r4)     // Catch:{ JSONException -> 0x0d1a }
        L_0x0d14:
            r15 = r73
            r15.put(r5)     // Catch:{ JSONException -> 0x0d1c }
            goto L_0x0d1c
        L_0x0d1a:
            r15 = r73
        L_0x0d1c:
            int r0 = r26 + 1
            r8 = r9
            r4 = r14
            r7 = r15
            r1 = r19
            r6 = r22
            r11 = r23
            r13 = r29
            r10 = r36
            r12 = 0
            r15 = r0
            r9 = r2
            r14 = r3
            r2 = r72
            r3 = 26
            goto L_0x00e5
        L_0x0d35:
            r72 = r2
            r15 = r7
            r2 = r9
            r29 = r13
            r3 = r14
            r21 = 0
            r14 = r4
            r4 = r5
            r9 = r8
            if (r14 == 0) goto L_0x0d65
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0d5d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r9.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0d5d:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r9.notificationId
            r0.notify(r1, r2)
            goto L_0x0d74
        L_0x0d65:
            java.util.HashSet<java.lang.Long> r0 = r9.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0d74
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r9.notificationId
            r0.cancel(r1)
        L_0x0d74:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r3.size()
            r0.<init>(r1)
            int r1 = r3.size()
            r2 = 0
        L_0x0d82:
            if (r2 >= r1) goto L_0x0d9f
            java.lang.Object r5 = r3.get(r2)
            org.telegram.messenger.NotificationsController$1NotificationHolder r5 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r5
            r5.call()
            boolean r6 = r78.unsupportedNotificationShortcut()
            if (r6 != 0) goto L_0x0d9c
            android.app.Notification r5 = r5.notification
            java.lang.String r5 = r5.getShortcutId()
            r0.add(r5)
        L_0x0d9c:
            int r2 = r2 + 1
            goto L_0x0d82
        L_0x0d9f:
            boolean r1 = r78.unsupportedNotificationShortcut()
            if (r1 != 0) goto L_0x0daa
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r1, r0)
        L_0x0daa:
            r12 = 0
        L_0x0dab:
            int r0 = r29.size()
            if (r12 >= r0) goto L_0x0df0
            r1 = r29
            long r2 = r1.keyAt(r12)
            java.util.HashSet<java.lang.Long> r0 = r9.openedInBubbleDialogs
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            boolean r0 = r0.contains(r2)
            if (r0 == 0) goto L_0x0dc4
            goto L_0x0deb
        L_0x0dc4:
            java.lang.Object r0 = r1.valueAt(r12)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0de2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "cancel notification id "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0de2:
            androidx.core.app.NotificationManagerCompat r2 = notificationManager
            int r0 = r0.intValue()
            r2.cancel(r0)
        L_0x0deb:
            int r12 = r12 + 1
            r29 = r1
            goto L_0x0dab
        L_0x0df0:
            if (r15 == 0) goto L_0x0e10
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0e10 }
            r0.<init>()     // Catch:{ Exception -> 0x0e10 }
            r1 = r72
            r0.put(r4, r1)     // Catch:{ Exception -> 0x0e10 }
            java.lang.String r1 = "n"
            r0.put(r1, r15)     // Catch:{ Exception -> 0x0e10 }
            java.lang.String r1 = "/notify"
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0e10 }
            byte[] r0 = r0.getBytes()     // Catch:{ Exception -> 0x0e10 }
            java.lang.String r2 = "remote_notifications"
            org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r1, r0, r2)     // Catch:{ Exception -> 0x0e10 }
        L_0x0e10:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, boolean, java.lang.String):void");
    }

    @TargetApi(28)
    private void loadRoundAvatar(File file, Person.Builder builder) {
        if (file != null) {
            try {
                builder.setIcon(IconCompat.createWithBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(file), $$Lambda$NotificationsController$TyIZKafFEr5zlu0ZpVMXbOeu_I.INSTANCE)));
            } catch (Throwable unused) {
            }
        }
    }

    static /* synthetic */ int lambda$null$33(Canvas canvas) {
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
                    NotificationsController.this.lambda$playOutChatSound$36$NotificationsController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$playOutChatSound$36$NotificationsController() {
        try {
            if (Math.abs(System.currentTimeMillis() - this.lastSoundOutPlay) > 100) {
                this.lastSoundOutPlay = System.currentTimeMillis();
                if (this.soundPool == null) {
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener($$Lambda$NotificationsController$OUNJlLfPbdz6QJs8uZCY6NbjGto.INSTANCE);
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

    static /* synthetic */ void lambda$null$35(SoundPool soundPool2, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool2.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void clearDialogNotificationsSettings(long j) {
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        SharedPreferences.Editor remove = edit.remove("notify2_" + j);
        remove.remove("custom_" + j);
        getMessagesStorage().setDialogFlags(j, 0);
        TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(j);
        if (tLRPC$Dialog != null) {
            tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
        }
        edit.commit();
        getNotificationsController().updateServerNotificationsSettings(j, true);
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
            getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, $$Lambda$NotificationsController$WV8JpQrNXdfWVJfPV9wKTUTuLBk.INSTANCE);
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
        getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, $$Lambda$NotificationsController$w9HtqTbEDgkwB57xEiog8KyWkW8.INSTANCE);
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
