package org.telegram.messenger;

import android.annotation.TargetApi;
import android.app.AlarmManager;
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
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
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
            org.telegram.tgnet.TLRPC$Peer r14 = r14.to_id
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
            org.telegram.tgnet.TLRPC$Peer r7 = r7.to_id
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
            if (r1 == 0) goto L_0x00fd
            r11 = 0
        L_0x002f:
            int r12 = r21.size()
            if (r11 >= r12) goto L_0x00fd
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
            org.telegram.tgnet.TLRPC$Peer r15 = r12.to_id
            int r15 = r15.channel_id
            if (r15 == 0) goto L_0x0058
            long r8 = (long) r15
            long r8 = r8 << r7
            long r13 = r13 | r8
        L_0x0058:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r8 = r0.pushMessagesDict
            int r8 = r8.indexOfKey(r13)
            if (r8 < 0) goto L_0x0065
        L_0x0060:
            r18 = r5
            r12 = r11
            goto L_0x00f3
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
            if (r3 == 0) goto L_0x025d
            r1 = 0
        L_0x015e:
            int r2 = r23.size()
            if (r1 >= r2) goto L_0x025d
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
            goto L_0x0259
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
            if (r2 == 0) goto L_0x024b
            int r8 = r0.total_unread_count
            int r2 = r2.intValue()
            int r8 = r8 - r2
            r0.total_unread_count = r8
        L_0x024b:
            int r2 = r0.total_unread_count
            int r2 = r2 + r4
            r0.total_unread_count = r2
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogs
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r2.put(r9, r4)
        L_0x0259:
            int r1 = r1 + 1
            goto L_0x015e
        L_0x025d:
            r5 = 0
            r16 = 1
            android.util.LongSparseArray<java.lang.Integer> r1 = r0.pushDialogs
            int r1 = r1.size()
            org.telegram.messenger.-$$Lambda$NotificationsController$5iZSA4iiOYvgCSPcQ2XgPFuD-jI r2 = new org.telegram.messenger.-$$Lambda$NotificationsController$5iZSA4iiOYvgCSPcQ2XgPFuD-jI
            r2.<init>(r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            long r1 = android.os.SystemClock.elapsedRealtime()
            r3 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 / r3
            r3 = 60
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 >= 0) goto L_0x027d
            r4 = 1
            goto L_0x027e
        L_0x027d:
            r4 = 0
        L_0x027e:
            r0.showOrUpdateNotification(r4)
            boolean r1 = r0.showBadgeNumber
            if (r1 == 0) goto L_0x028c
            int r1 = r20.getTotalAllUnreadCount()
            r0.setBadge(r1)
        L_0x028c:
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
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x01ca, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0a97;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x01cc, code lost:
        r20[0] = null;
        r2 = r2.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x01d3, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L_0x0a87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x01d7, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L_0x01db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x01dd, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L_0x01ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01ed, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", NUM, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x01f1, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L_0x0252;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x01f3, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", NUM, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(((long) r0.messageOwner.date) * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(((long) r0.messageOwner.date) * 1000));
        r0 = r0.messageOwner.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x0251, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", NUM, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0254, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L_0x0a80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x0258, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L_0x025c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x025e, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L_0x026a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0269, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x026c, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x0382;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x026e, code lost:
        r3 = r2.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0270, code lost:
        if (r3 != 0) goto L_0x028c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0279, code lost:
        if (r2.users.size() != 1) goto L_0x028c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x027b, code lost:
        r3 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x028c, code lost:
        if (r3 == 0) goto L_0x032a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x0294, code lost:
        if (r0.messageOwner.to_id.channel_id == 0) goto L_0x02af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0298, code lost:
        if (r8.megagroup != false) goto L_0x02af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x02ae, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x02b7, code lost:
        if (r3 != getUserConfig().getClientUserId()) goto L_0x02ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x02cd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x02ce, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x02da, code lost:
        if (r0 != null) goto L_0x02dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x02dc, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x02df, code lost:
        if (r1 != r0.id) goto L_0x030f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x02e3, code lost:
        if (r8.megagroup == false) goto L_0x02fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x02f9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x030e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x0329, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r7, r8.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x032a, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x033a, code lost:
        if (r2 >= r0.messageOwner.action.users.size()) goto L_0x0367;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x033c, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x0350, code lost:
        if (r3 == null) goto L_0x0364;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:181:0x0352, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x035a, code lost:
        if (r1.length() == 0) goto L_0x0361;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x035c, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0361, code lost:
        r1.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x0364, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x0381, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r7, r8.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0385, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x039b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x039a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x039f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x03b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x03b2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x03b5, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0a1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x03b9, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x03bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x03bf, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x042f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x03cb, code lost:
        if (r2.user_id != getUserConfig().getClientUserId()) goto L_0x03e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x03e1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x03e8, code lost:
        if (r0.messageOwner.action.user_id != r1) goto L_0x03ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x03fe, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x03ff, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0411, code lost:
        if (r0 != null) goto L_0x0414;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0413, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x042e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r7, r8.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x0431, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x043a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x0439, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x043c, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x0445;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x0444, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x0447, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x045b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x045a, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x045f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x0471;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0470, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x0473, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x047c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x047b, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x047e, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0a1a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0482, code lost:
        if (r8 == null) goto L_0x0770;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0488, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r8) == false) goto L_0x048e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x048c, code lost:
        if (r8.megagroup == false) goto L_0x0770;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x048e, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x0490, code lost:
        if (r0 != null) goto L_0x04a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x04a6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x04ae, code lost:
        if (r0.isMusic() == false) goto L_0x04c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x04c1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x04cb, code lost:
        if (r0.isVideo() == false) goto L_0x0515;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04cf, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0500;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x04d9, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0500;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x04ff, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "📹 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0514, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0519, code lost:
        if (r0.isGif() == false) goto L_0x0563;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x051d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x054e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x0527, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x054e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x054d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "🎬 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0562, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x056a, code lost:
        if (r0.isVoice() == false) goto L_0x057e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x057d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0582, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0596;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0595, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x059a, code lost:
        if (r0.isSticker() != false) goto L_0x0740;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x05a0, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x05a4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x05a4, code lost:
        r2 = r0.messageOwner;
        r4 = r2.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x05aa, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x05f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x05ae, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x05dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x05b6, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x05dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x05dc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "📎 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x05f1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x05f4, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x072b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x05f8, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x05fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x05fe, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0615;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x0614, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x0619, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x063a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x061b, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x0639, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r7, r8.title, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x063c, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0678;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x063e, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x0644, code lost:
        if (r0.quiz == false) goto L_0x065f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x065e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r7, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x0677, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r7, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x067a, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x06c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x067e, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x06ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x0686, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x06ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x06ac, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r7, "🖼 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x06c1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x06c7, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x06db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x06da, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x06db, code lost:
        r2 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x06dd, code lost:
        if (r2 == null) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x06e3, code lost:
        if (r2.length() <= 0) goto L_0x0716;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x06e5, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x06eb, code lost:
        if (r0.length() <= 20) goto L_0x0704;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x06ed, code lost:
        r2 = new java.lang.StringBuilder();
        r4 = 0;
        r2.append(r0.subSequence(0, 20));
        r2.append("...");
        r0 = r2.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0704, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0705, code lost:
        r1 = new java.lang.Object[3];
        r1[r4] = r7;
        r1[1] = r0;
        r1[2] = r8.title;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x0715, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x072a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x073f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0740, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x0746, code lost:
        if (r0 == null) goto L_0x075d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x075c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r7, r8.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x076f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x0770, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x0773, code lost:
        if (r0 != null) goto L_0x0786;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0785, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x078b, code lost:
        if (r0.isMusic() == false) goto L_0x079d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x079c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x07a6, code lost:
        if (r0.isVideo() == false) goto L_0x07eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x07aa, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x07d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x07b4, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x07d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x07d8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r8.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x07ea, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x07ef, code lost:
        if (r0.isGif() == false) goto L_0x0834;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x07f3, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0822;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x07fd, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0822;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x0821, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r8.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x0833, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x083a, code lost:
        if (r0.isVoice() == false) goto L_0x084c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x084b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x0850, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0862;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x0861, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x0866, code lost:
        if (r0.isSticker() != false) goto L_0x09ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x086c, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0870;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x0870, code lost:
        r2 = r0.messageOwner;
        r4 = r2.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x0876, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x08b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x087a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x08a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0882, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x08a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x08a6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r8.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x08b8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x08bb, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x09dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x08bf, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x08c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x08c5, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x08d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x08d8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x08dc, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x08fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x08de, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x08fb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r8.title, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x08fe, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0936;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0900, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0906, code lost:
        if (r0.quiz == false) goto L_0x091f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x091e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0935, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0938, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x097b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x093c, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0969;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0944, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x0969;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x0968, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r8.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x097a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x097f, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0991;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x0990, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0991, code lost:
        r2 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x0993, code lost:
        if (r2 == null) goto L_0x09ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x0999, code lost:
        if (r2.length() <= 0) goto L_0x09ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x099b, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x09a1, code lost:
        if (r0.length() <= 20) goto L_0x09ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x09a3, code lost:
        r2 = new java.lang.StringBuilder();
        r4 = 0;
        r2.append(r0.subSequence(0, 20));
        r2.append("...");
        r0 = r2.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x09ba, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x09bb, code lost:
        r1 = new java.lang.Object[2];
        r1[r4] = r8.title;
        r1[1] = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x09c9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x09db, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x09ed, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x09ee, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x09f3, code lost:
        if (r0 == null) goto L_0x0a09;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0a08, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r8.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x0a19, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0a1a, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x0a21, code lost:
        if (r0.messageOwner.to_id.channel_id == 0) goto L_0x0a51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0a25, code lost:
        if (r8.megagroup != false) goto L_0x0a51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x0a2b, code lost:
        if (r19.isVideoAvatar() == false) goto L_0x0a3f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x0a3e, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0a50, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0a56, code lost:
        if (r19.isVideoAvatar() == false) goto L_0x0a6c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0a6b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0a7f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0a86, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0a96, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", NUM, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0a9b, code lost:
        if (r19.isMediaEmpty() == false) goto L_0x0ab4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0aa5, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0aac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0aab, code lost:
        return r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0ab3, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x0ab4, code lost:
        r1 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0aba, code lost:
        if ((r1.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0af8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0abe, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0adc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0ac6, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L_0x0adc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0adb, code lost:
        return "🖼 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0ae2, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0aee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0aed, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0af7, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x0afc, code lost:
        if (r19.isVideo() == false) goto L_0x0b3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0b00, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0b20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x0b0a, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0b20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x0b1f, code lost:
        return "📹 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0b26, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0b32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0b31, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0b3b, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0b40, code lost:
        if (r19.isGame() == false) goto L_0x0b4c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x0b4b, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0b50, code lost:
        if (r19.isVoice() == false) goto L_0x0b5c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0b5b, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x0b60, code lost:
        if (r19.isRoundVideo() == false) goto L_0x0b6c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0b6b, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0b70, code lost:
        if (r19.isMusic() == false) goto L_0x0b7c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0b7b, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0b7c, code lost:
        r1 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0b82, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0b8e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0b8d, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0b90, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0bae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0b98, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1).poll.quiz == false) goto L_0x0ba4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0ba3, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0bad, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0bb0, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0c7a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0bb4, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0bb8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0bba, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0bc6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0bc5, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0bc8, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0bce, code lost:
        if (r19.isSticker() != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0bd4, code lost:
        if (r19.isAnimatedSticker() == false) goto L_0x0bd7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0bdb, code lost:
        if (r19.isGif() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0bdf, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0bff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x0be9, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0bff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0bfe, code lost:
        return "🎬 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0c0b, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0c2b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0c2b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0c2a, code lost:
        return "📎 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x0CLASSNAME, code lost:
        r0 = r19.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0CLASSNAME, code lost:
        if (r0 == null) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0CLASSNAME, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x0CLASSNAME, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0CLASSNAME, code lost:
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
            if (r1 != 0) goto L_0x0CLASSNAME
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x0010
            goto L_0x0CLASSNAME
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
            r0 = 2131625777(0x7f0e0731, float:1.8878772E38)
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
            r1 = 2131624606(0x7f0e029e, float:1.8876396E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            java.lang.String r0 = r0.localName
            r2[r8] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00cd:
            r1 = 2131626025(0x7f0e0829, float:1.8879275E38)
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
            r0 = 2131626002(0x7f0e0812, float:1.8879228E38)
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
            if (r10 == 0) goto L_0x0CLASSNAME
            if (r6 != 0) goto L_0x01a3
            if (r1 == 0) goto L_0x01a3
            r3 = 1
            boolean r4 = r9.getBoolean(r13, r3)
            if (r4 != 0) goto L_0x01b8
            goto L_0x01a4
        L_0x01a3:
            r3 = 1
        L_0x01a4:
            if (r6 == 0) goto L_0x0CLASSNAME
            if (r2 != 0) goto L_0x01ae
            boolean r4 = r9.getBoolean(r12, r3)
            if (r4 != 0) goto L_0x01b8
        L_0x01ae:
            if (r2 == 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "EnablePreviewChannel"
            boolean r2 = r9.getBoolean(r2, r3)
            if (r2 == 0) goto L_0x0CLASSNAME
        L_0x01b8:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r4 = "🎬 "
            java.lang.String r5 = "📎 "
            java.lang.String r6 = "📹 "
            java.lang.String r9 = "🖼 "
            r10 = 19
            if (r3 == 0) goto L_0x0a97
            r3 = 0
            r20[r3] = r11
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r12 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r12 != 0) goto L_0x0a87
            boolean r12 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r12 == 0) goto L_0x01db
            goto L_0x0a87
        L_0x01db:
            boolean r12 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r12 == 0) goto L_0x01ee
            r0 = 2131625987(0x7f0e0803, float:1.8879197E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r7
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x01ee:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            r12 = 3
            if (r3 == 0) goto L_0x0252
            r1 = 2131627522(0x7f0e0e02, float:1.888231E38)
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
            r2 = 2131626052(0x7f0e0844, float:1.887933E38)
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
        L_0x0252:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r3 != 0) goto L_0x0a80
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r3 == 0) goto L_0x025c
            goto L_0x0a80
        L_0x025c:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r3 == 0) goto L_0x026a
            r0 = 2131624501(0x7f0e0235, float:1.8876183E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x026a:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r3 == 0) goto L_0x0382
            int r3 = r2.user_id
            if (r3 != 0) goto L_0x028c
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            int r2 = r2.size()
            r4 = 1
            if (r2 != r4) goto L_0x028c
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r3 = r2.intValue()
        L_0x028c:
            if (r3 == 0) goto L_0x032a
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x02af
            boolean r0 = r8.megagroup
            if (r0 != 0) goto L_0x02af
            r0 = 2131624557(0x7f0e026d, float:1.8876297E38)
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
        L_0x02af:
            org.telegram.messenger.UserConfig r0 = r18.getUserConfig()
            int r0 = r0.getClientUserId()
            if (r3 != r0) goto L_0x02ce
            r0 = 2131626004(0x7f0e0814, float:1.8879232E38)
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
        L_0x02ce:
            org.telegram.messenger.MessagesController r0 = r18.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x02dd
            return r11
        L_0x02dd:
            int r2 = r0.id
            if (r1 != r2) goto L_0x030f
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x02fa
            r0 = 2131625993(0x7f0e0809, float:1.887921E38)
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
        L_0x02fa:
            r1 = 2
            r2 = 0
            r3 = 1
            r0 = 2131625992(0x7f0e0808, float:1.8879208E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x030f:
            r2 = 0
            r3 = 1
            r1 = 2131625991(0x7f0e0807, float:1.8879206E38)
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
        L_0x032a:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0330:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0367
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0364
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0361
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0361:
            r1.append(r3)
        L_0x0364:
            int r2 = r2 + 1
            goto L_0x0330
        L_0x0367:
            r0 = 2131625991(0x7f0e0807, float:1.8879206E38)
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
        L_0x0382:
            r3 = 2
            boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r13 == 0) goto L_0x039b
            r0 = 2131626005(0x7f0e0815, float:1.8879234E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r13 = 0
            r1[r13] = r7
            java.lang.String r2 = r8.title
            r14 = 1
            r1[r14] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x039b:
            r13 = 0
            r14 = 1
            boolean r15 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r15 == 0) goto L_0x03b3
            r0 = 2131625988(0x7f0e0804, float:1.88792E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r13] = r7
            java.lang.String r2 = r2.title
            r1[r14] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03b3:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r3 != 0) goto L_0x0a1b
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r3 == 0) goto L_0x03bd
            goto L_0x0a1b
        L_0x03bd:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r3 == 0) goto L_0x042f
            int r2 = r2.user_id
            org.telegram.messenger.UserConfig r3 = r18.getUserConfig()
            int r3 = r3.getClientUserId()
            if (r2 != r3) goto L_0x03e2
            r0 = 2131625998(0x7f0e080e, float:1.887922E38)
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
        L_0x03e2:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.user_id
            if (r2 != r1) goto L_0x03ff
            r0 = 2131625999(0x7f0e080f, float:1.8879222E38)
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
        L_0x03ff:
            org.telegram.messenger.MessagesController r1 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x0414
            return r11
        L_0x0414:
            r1 = 2131625997(0x7f0e080d, float:1.8879218E38)
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
        L_0x042f:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r1 == 0) goto L_0x043a
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x043a:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r1 == 0) goto L_0x0445
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0445:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r1 == 0) goto L_0x045b
            r0 = 2131624081(0x7f0e0091, float:1.8875332E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x045b:
            r1 = 1
            r3 = 0
            boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r13 == 0) goto L_0x0471
            r0 = 2131624081(0x7f0e0091, float:1.8875332E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r3] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0471:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r1 == 0) goto L_0x047c
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x047c:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r1 == 0) goto L_0x0a1a
            r1 = 20
            if (r8 == 0) goto L_0x0770
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r2 == 0) goto L_0x048e
            boolean r2 = r8.megagroup
            if (r2 == 0) goto L_0x0770
        L_0x048e:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x04a7
            r0 = 2131625966(0x7f0e07ee, float:1.8879155E38)
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
        L_0x04a7:
            r2 = 2
            r3 = 0
            r11 = 1
            boolean r13 = r0.isMusic()
            if (r13 == 0) goto L_0x04c2
            r0 = 2131625964(0x7f0e07ec, float:1.887915E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r11] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04c2:
            boolean r2 = r0.isVideo()
            r3 = 2131625980(0x7f0e07fc, float:1.8879183E38)
            java.lang.String r11 = "NotificationActionPinnedText"
            if (r2 == 0) goto L_0x0515
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x0500
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0500
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
        L_0x0500:
            r2 = 0
            r4 = 1
            r5 = 2
            r0 = 2131625982(0x7f0e07fe, float:1.8879187E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0515:
            boolean r2 = r0.isGif()
            if (r2 == 0) goto L_0x0563
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x054e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x054e
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
        L_0x054e:
            r2 = 0
            r4 = 1
            r6 = 2
            r0 = 2131625960(0x7f0e07e8, float:1.8879143E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0563:
            r2 = 0
            r4 = 1
            r6 = 2
            boolean r13 = r0.isVoice()
            if (r13 == 0) goto L_0x057e
            r0 = 2131625984(0x7f0e0800, float:1.8879191E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x057e:
            boolean r13 = r0.isRoundVideo()
            if (r13 == 0) goto L_0x0596
            r0 = 2131625974(0x7f0e07f6, float:1.8879171E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0596:
            boolean r2 = r0.isSticker()
            if (r2 != 0) goto L_0x0740
            boolean r2 = r0.isAnimatedSticker()
            if (r2 == 0) goto L_0x05a4
            goto L_0x0740
        L_0x05a4:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x05f2
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x05dd
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x05dd
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
        L_0x05dd:
            r2 = 0
            r4 = 1
            r5 = 2
            r0 = 2131625950(0x7f0e07de, float:1.8879122E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05f2:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x072b
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x05fc
            goto L_0x072b
        L_0x05fc:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0615
            r0 = 2131625958(0x7f0e07e6, float:1.8879139E38)
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
        L_0x0615:
            r5 = 0
            r6 = 1
            boolean r13 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r13 == 0) goto L_0x063a
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4
            r0 = 2131625948(0x7f0e07dc, float:1.8879118E38)
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
        L_0x063a:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0678
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x065f
            r1 = 2131625972(0x7f0e07f4, float:1.8879167E38)
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
        L_0x065f:
            r3 = 0
            r4 = 1
            r5 = 2
            r1 = 2131625970(0x7f0e07f2, float:1.8879163E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r2[r3] = r7
            java.lang.String r3 = r8.title
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0678:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x06c2
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x06ad
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x06ad
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
        L_0x06ad:
            r2 = 0
            r5 = 1
            r6 = 2
            r0 = 2131625968(0x7f0e07f0, float:1.8879159E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06c2:
            r2 = 0
            r5 = 1
            r6 = 2
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x06db
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06db:
            java.lang.CharSequence r2 = r0.messageText
            if (r2 == 0) goto L_0x0716
            int r2 = r2.length()
            if (r2 <= 0) goto L_0x0716
            java.lang.CharSequence r0 = r0.messageText
            int r2 = r0.length()
            if (r2 <= r1) goto L_0x0704
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r4 = 0
            java.lang.CharSequence r0 = r0.subSequence(r4, r1)
            r2.append(r0)
            java.lang.String r0 = "..."
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0705
        L_0x0704:
            r4 = 0
        L_0x0705:
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r4] = r7
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = r8.title
            r5 = 2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r3, r1)
            return r0
        L_0x0716:
            r2 = 1
            r4 = 0
            r5 = 2
            r0 = 2131625966(0x7f0e07ee, float:1.8879155E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r7
            java.lang.String r3 = r8.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x072b:
            r2 = 1
            r4 = 0
            r5 = 2
            r0 = 2131625956(0x7f0e07e4, float:1.8879135E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r7
            java.lang.String r3 = r8.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0740:
            r2 = 1
            r4 = 0
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x075d
            r1 = 2131625978(0x7f0e07fa, float:1.887918E38)
            java.lang.Object[] r3 = new java.lang.Object[r12]
            r3[r4] = r7
            java.lang.String r4 = r8.title
            r3[r2] = r4
            r5 = 2
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x075d:
            r5 = 2
            r0 = 2131625976(0x7f0e07f8, float:1.8879175E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r7
            java.lang.String r3 = r8.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0770:
            r2 = 1
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0786
            r0 = 2131625967(0x7f0e07ef, float:1.8879157E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0786:
            r3 = 0
            boolean r7 = r0.isMusic()
            if (r7 == 0) goto L_0x079d
            r0 = 2131625965(0x7f0e07ed, float:1.8879153E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x079d:
            boolean r2 = r0.isVideo()
            r3 = 2131625981(0x7f0e07fd, float:1.8879185E38)
            java.lang.String r7 = "NotificationActionPinnedTextChannel"
            if (r2 == 0) goto L_0x07eb
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x07d9
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x07d9
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
        L_0x07d9:
            r2 = 1
            r4 = 0
            r0 = 2131625983(0x7f0e07ff, float:1.887919E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07eb:
            boolean r2 = r0.isGif()
            if (r2 == 0) goto L_0x0834
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x0822
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0822
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
        L_0x0822:
            r2 = 1
            r4 = 0
            r0 = 2131625961(0x7f0e07e9, float:1.8879145E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0834:
            r2 = 1
            r4 = 0
            boolean r6 = r0.isVoice()
            if (r6 == 0) goto L_0x084c
            r0 = 2131625985(0x7f0e0801, float:1.8879193E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x084c:
            boolean r6 = r0.isRoundVideo()
            if (r6 == 0) goto L_0x0862
            r0 = 2131625975(0x7f0e07f7, float:1.8879173E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0862:
            boolean r2 = r0.isSticker()
            if (r2 != 0) goto L_0x09ee
            boolean r2 = r0.isAnimatedSticker()
            if (r2 == 0) goto L_0x0870
            goto L_0x09ee
        L_0x0870:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x08b9
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x08a7
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x08a7
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
        L_0x08a7:
            r2 = 1
            r4 = 0
            r0 = 2131625951(0x7f0e07df, float:1.8879124E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08b9:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x09dc
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x08c3
            goto L_0x09dc
        L_0x08c3:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x08d9
            r0 = 2131625959(0x7f0e07e7, float:1.887914E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r5 = 0
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08d9:
            r5 = 0
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r6 == 0) goto L_0x08fc
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4
            r0 = 2131625949(0x7f0e07dd, float:1.887912E38)
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
        L_0x08fc:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0936
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x091f
            r1 = 2131625973(0x7f0e07f5, float:1.887917E38)
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
        L_0x091f:
            r2 = 2
            r3 = 1
            r4 = 0
            r1 = 2131625971(0x7f0e07f3, float:1.8879165E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r5 = r8.title
            r2[r4] = r5
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0936:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x097b
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x0969
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0969
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
        L_0x0969:
            r2 = 1
            r5 = 0
            r0 = 2131625969(0x7f0e07f1, float:1.887916E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x097b:
            r2 = 1
            r5 = 0
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0991
            r0 = 2131625953(0x7f0e07e1, float:1.8879129E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0991:
            java.lang.CharSequence r2 = r0.messageText
            if (r2 == 0) goto L_0x09ca
            int r2 = r2.length()
            if (r2 <= 0) goto L_0x09ca
            java.lang.CharSequence r0 = r0.messageText
            int r2 = r0.length()
            if (r2 <= r1) goto L_0x09ba
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r4 = 0
            java.lang.CharSequence r0 = r0.subSequence(r4, r1)
            r2.append(r0)
            java.lang.String r0 = "..."
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x09bb
        L_0x09ba:
            r4 = 0
        L_0x09bb:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r3, r1)
            return r0
        L_0x09ca:
            r2 = 1
            r4 = 0
            r0 = 2131625967(0x7f0e07ef, float:1.8879157E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09dc:
            r2 = 1
            r4 = 0
            r0 = 2131625957(0x7f0e07e5, float:1.8879137E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09ee:
            r4 = 0
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0a09
            r1 = 2131625979(0x7f0e07fb, float:1.8879181E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r8.title
            r2[r4] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0a09:
            r3 = 1
            r0 = 2131625977(0x7f0e07f9, float:1.8879177E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a1a:
            return r11
        L_0x0a1b:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0a51
            boolean r1 = r8.megagroup
            if (r1 != 0) goto L_0x0a51
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x0a3f
            r0 = 2131624655(0x7f0e02cf, float:1.8876496E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a3f:
            r1 = 1
            r3 = 0
            r0 = 2131624622(0x7f0e02ae, float:1.8876429E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a51:
            r3 = 0
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x0a6c
            r0 = 2131625990(0x7f0e0806, float:1.8879204E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a6c:
            r1 = 2
            r4 = 1
            r0 = 2131625989(0x7f0e0805, float:1.8879202E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a80:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0a87:
            r4 = 1
            r0 = 2131625986(0x7f0e0802, float:1.8879195E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a97:
            boolean r1 = r19.isMediaEmpty()
            if (r1 == 0) goto L_0x0ab4
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0aac
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x0aac:
            r0 = 2131625777(0x7f0e0731, float:1.8878772E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0ab4:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0af8
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r10) goto L_0x0adc
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0adc
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0adc:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0aee
            r0 = 2131624307(0x7f0e0173, float:1.887579E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0aee:
            r0 = 2131624322(0x7f0e0182, float:1.887582E38)
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0af8:
            boolean r1 = r19.isVideo()
            if (r1 == 0) goto L_0x0b3c
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x0b20
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b20
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0b20:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0b32
            r0 = 2131624308(0x7f0e0174, float:1.8875792E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b32:
            r0 = 2131624328(0x7f0e0188, float:1.8875833E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b3c:
            boolean r1 = r19.isGame()
            if (r1 == 0) goto L_0x0b4c
            r0 = 2131624310(0x7f0e0176, float:1.8875796E38)
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b4c:
            boolean r1 = r19.isVoice()
            if (r1 == 0) goto L_0x0b5c
            r0 = 2131624304(0x7f0e0170, float:1.8875784E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b5c:
            boolean r1 = r19.isRoundVideo()
            if (r1 == 0) goto L_0x0b6c
            r0 = 2131624324(0x7f0e0184, float:1.8875824E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b6c:
            boolean r1 = r19.isMusic()
            if (r1 == 0) goto L_0x0b7c
            r0 = 2131624321(0x7f0e0181, float:1.8875818E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b7c:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r2 == 0) goto L_0x0b8e
            r0 = 2131624306(0x7f0e0172, float:1.8875788E38)
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0b8e:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0bae
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0ba4
            r0 = 2131626598(0x7f0e0a66, float:1.8880437E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ba4:
            r0 = 2131626505(0x7f0e0a09, float:1.8880248E38)
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0bae:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r2 != 0) goto L_0x0c7a
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r2 == 0) goto L_0x0bb8
            goto L_0x0c7a
        L_0x0bb8:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x0bc6
            r0 = 2131624316(0x7f0e017c, float:1.8875808E38)
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0bc6:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x0CLASSNAME
            boolean r1 = r19.isSticker()
            if (r1 != 0) goto L_0x0CLASSNAME
            boolean r1 = r19.isAnimatedSticker()
            if (r1 == 0) goto L_0x0bd7
            goto L_0x0CLASSNAME
        L_0x0bd7:
            boolean r1 = r19.isGif()
            if (r1 == 0) goto L_0x0CLASSNAME
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x0bff
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0bff
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0bff:
            r0 = 2131624311(0x7f0e0177, float:1.8875798E38)
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0CLASSNAME:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r10) goto L_0x0c2b
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0c2b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0c2b:
            r0 = 2131624309(0x7f0e0175, float:1.8875794E38)
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0CLASSNAME:
            java.lang.String r0 = r19.getStickerEmoji()
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " "
            r1.append(r0)
            r0 = 2131624325(0x7f0e0185, float:1.8875827E38)
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0CLASSNAME:
            r0 = 2131624325(0x7f0e0185, float:1.8875827E38)
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
            r0 = 2131625777(0x7f0e0731, float:1.8878772E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0c7a:
            r0 = 2131624318(0x7f0e017e, float:1.8875812E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0CLASSNAME:
            if (r21 == 0) goto L_0x0CLASSNAME
            r0 = 0
            r21[r0] = r0
        L_0x0CLASSNAME:
            r0 = 2131625777(0x7f0e0731, float:1.8878772E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0CLASSNAME:
            r0 = 2131626002(0x7f0e0812, float:1.8879228E38)
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
            if (r1 != 0) goto L_0x1381
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x1381
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
            r10 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.String r11 = "NotificationMessageGroupNoText"
            r12 = 2131626038(0x7f0e0836, float:1.88793E38)
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
            r1 = 2131624606(0x7f0e029e, float:1.8876396E38)
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
            r12 = 2131625794(0x7f0e0742, float:1.8878806E38)
            java.lang.String r13 = "MessageScheduledReminderNotification"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            goto L_0x0146
        L_0x0112:
            r12 = 2131626046(0x7f0e083e, float:1.8879317E38)
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
            r0 = 2131627454(0x7f0e0dbe, float:1.8882173E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1380
        L_0x0169:
            java.lang.String r2 = "🎬 "
            java.lang.String r3 = "📎 "
            java.lang.String r13 = "📹 "
            java.lang.String r15 = "🖼 "
            java.lang.String r14 = "NotificationMessageText"
            r6 = 3
            if (r4 != 0) goto L_0x054e
            if (r1 == 0) goto L_0x054e
            if (r8 == 0) goto L_0x053a
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 == 0) goto L_0x053a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r4 == 0) goto L_0x023f
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r2 != 0) goto L_0x022f
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r2 == 0) goto L_0x0198
            goto L_0x022f
        L_0x0198:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r2 == 0) goto L_0x01ac
            r0 = 2131625987(0x7f0e0803, float:1.8879197E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x01ac:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r2 == 0) goto L_0x020f
            r1 = 2131627522(0x7f0e0e02, float:1.888231E38)
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
            r2 = 2131626052(0x7f0e0844, float:1.887933E38)
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
            goto L_0x1380
        L_0x020f:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 != 0) goto L_0x0227
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r2 == 0) goto L_0x0218
            goto L_0x0227
        L_0x0218:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x137e
            r0 = 2131624501(0x7f0e0235, float:1.8876183E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1380
        L_0x0227:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1380
        L_0x022f:
            r0 = 2131625986(0x7f0e0802, float:1.8879195E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x023f:
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x0288
            if (r21 != 0) goto L_0x0278
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0268
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1380
        L_0x0268:
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r4 = r17
            r1 = 2131626038(0x7f0e0836, float:1.88793E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1380
        L_0x0278:
            r4 = r17
            r1 = 2131626038(0x7f0e0836, float:1.88793E38)
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1380
        L_0x0288:
            r4 = r17
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r1.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x02ef
            if (r21 != 0) goto L_0x02c8
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x02c8
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x02c8
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
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1380
        L_0x02c8:
            r2 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x02e0
            r0 = 2131626043(0x7f0e083b, float:1.8879311E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageSDPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x02e0:
            r0 = 2131626039(0x7f0e0837, float:1.8879303E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x02ef:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x0354
            if (r21 != 0) goto L_0x032d
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x032d
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x032d
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
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r6] = r5
            goto L_0x1380
        L_0x032d:
            r6 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0345
            r0 = 2131626044(0x7f0e083c, float:1.8879313E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageSDVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0345:
            r0 = 2131626050(0x7f0e0842, float:1.8879325E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0354:
            r6 = 0
            boolean r1 = r20.isGame()
            if (r1 == 0) goto L_0x0375
            r1 = 2131626012(0x7f0e081c, float:1.8879248E38)
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
            goto L_0x1380
        L_0x0375:
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x038b
            r0 = 2131626007(0x7f0e0817, float:1.8879238E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r6 = 0
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x038b:
            r6 = 0
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x03a1
            r0 = 2131626042(0x7f0e083a, float:1.887931E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x03a1:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x03b6
            r0 = 2131626037(0x7f0e0835, float:1.8879299E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x03b6:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x03da
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131626008(0x7f0e0818, float:1.887924E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1380
        L_0x03da:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x0410
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x03fa
            r1 = 2131626041(0x7f0e0839, float:1.8879307E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x040d
        L_0x03fa:
            r2 = 2
            r3 = 0
            r1 = 2131626040(0x7f0e0838, float:1.8879305E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
        L_0x040d:
            r15 = r0
            goto L_0x1380
        L_0x0410:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r6 != 0) goto L_0x052a
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r6 == 0) goto L_0x041a
            goto L_0x052a
        L_0x041a:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x042e
            r0 = 2131626035(0x7f0e0833, float:1.8879295E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x042e:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x04fe
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x04d6
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x0440
            goto L_0x04d6
        L_0x0440:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x048e
            if (r21 != 0) goto L_0x047e
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x047e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x047e
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
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r3] = r5
            goto L_0x1380
        L_0x047e:
            r3 = 0
            r0 = 2131626014(0x7f0e081e, float:1.8879252E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r12
            java.lang.String r2 = "NotificationMessageGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x048e:
            if (r21 != 0) goto L_0x04c6
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x04c6
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x04c6
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
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1380
        L_0x04c6:
            r2 = 0
            r0 = 2131626009(0x7f0e0819, float:1.8879242E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x04d6:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x04ef
            r1 = 2131626048(0x7f0e0840, float:1.8879321E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r12
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x040d
        L_0x04ef:
            r0 = 2131626047(0x7f0e083f, float:1.887932E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x04fe:
            r2 = 0
            if (r21 != 0) goto L_0x051d
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x051d
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.CharSequence r0 = r0.messageText
            r1[r5] = r0
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1380
        L_0x051d:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r1 = 2131626038(0x7f0e0836, float:1.88793E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1380
        L_0x052a:
            r2 = 0
            r0 = 2131626036(0x7f0e0834, float:1.8879297E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x053a:
            r4 = r17
            r2 = 0
            if (r23 == 0) goto L_0x0541
            r23[r2] = r2
        L_0x0541:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r1 = 2131626038(0x7f0e0836, float:1.88793E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1380
        L_0x054e:
            if (r4 == 0) goto L_0x137e
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r4 == 0) goto L_0x055c
            boolean r4 = r10.megagroup
            if (r4 != 0) goto L_0x055c
            r4 = 1
            goto L_0x055d
        L_0x055c:
            r4 = 0
        L_0x055d:
            if (r8 == 0) goto L_0x1350
            if (r4 != 0) goto L_0x0569
            java.lang.String r8 = "EnablePreviewGroup"
            boolean r8 = r7.getBoolean(r8, r5)
            if (r8 != 0) goto L_0x0573
        L_0x0569:
            if (r4 == 0) goto L_0x1350
            java.lang.String r4 = "EnablePreviewChannel"
            boolean r4 = r7.getBoolean(r4, r5)
            if (r4 == 0) goto L_0x1350
        L_0x0573:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r7 == 0) goto L_0x0dad
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r7 == 0) goto L_0x068b
            int r2 = r4.user_id
            if (r2 != 0) goto L_0x059c
            java.util.ArrayList<java.lang.Integer> r3 = r4.users
            int r3 = r3.size()
            if (r3 != r5) goto L_0x059c
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
        L_0x059c:
            if (r2 == 0) goto L_0x0633
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x05bf
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x05bf
            r0 = 2131624557(0x7f0e026d, float:1.8876297E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x05bf:
            r3 = 2
            r4 = 0
            if (r2 != r9) goto L_0x05d6
            r0 = 2131626004(0x7f0e0814, float:1.8879232E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x05d6:
            org.telegram.messenger.MessagesController r0 = r19.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x05e6
            r2 = 0
            return r2
        L_0x05e6:
            int r2 = r0.id
            if (r1 != r2) goto L_0x0618
            boolean r0 = r10.megagroup
            if (r0 == 0) goto L_0x0603
            r0 = 2131625993(0x7f0e0809, float:1.887921E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0603:
            r1 = 2
            r2 = 0
            r0 = 2131625992(0x7f0e0808, float:1.8879208E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0618:
            r2 = 0
            r1 = 2131625991(0x7f0e0807, float:1.8879206E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r2] = r12
            java.lang.String r2 = r10.title
            r3[r5] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r3[r2] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x040d
        L_0x0633:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0639:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0670
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x066d
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x066a
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x066a:
            r1.append(r3)
        L_0x066d:
            int r2 = r2 + 1
            goto L_0x0639
        L_0x0670:
            r0 = 2131625991(0x7f0e0807, float:1.8879206E38)
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
            goto L_0x040d
        L_0x068b:
            r7 = 2
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r8 == 0) goto L_0x06a4
            r0 = 2131626005(0x7f0e0815, float:1.8879234E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r8 = 0
            r1[r8] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x06a4:
            r8 = 0
            boolean r11 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r11 == 0) goto L_0x06bc
            r0 = 2131625988(0x7f0e0804, float:1.88792E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r8] = r12
            java.lang.String r2 = r4.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x06bc:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r7 != 0) goto L_0x0d48
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r7 == 0) goto L_0x06c6
            goto L_0x0d48
        L_0x06c6:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r7 == 0) goto L_0x072b
            int r2 = r4.user_id
            if (r2 != r9) goto L_0x06e3
            r0 = 2131625998(0x7f0e080e, float:1.887922E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x06e3:
            r3 = 2
            r4 = 0
            if (r2 != r1) goto L_0x06fa
            r0 = 2131625999(0x7f0e080f, float:1.8879222E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x06fa:
            org.telegram.messenger.MessagesController r1 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x0710
            r1 = 0
            return r1
        L_0x0710:
            r1 = 2131625997(0x7f0e080d, float:1.8879218E38)
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
            goto L_0x1380
        L_0x072b:
            r1 = 0
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r7 == 0) goto L_0x0738
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1380
        L_0x0738:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r7 == 0) goto L_0x0744
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1380
        L_0x0744:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r7 == 0) goto L_0x075a
            r0 = 2131624081(0x7f0e0091, float:1.8875332E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r7 = 0
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x075a:
            r7 = 0
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r8 == 0) goto L_0x0770
            r0 = 2131624081(0x7f0e0091, float:1.8875332E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r4.title
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0770:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r7 == 0) goto L_0x077c
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1380
        L_0x077c:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r7 == 0) goto L_0x0d3c
            if (r10 == 0) goto L_0x0a80
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r1 == 0) goto L_0x078c
            boolean r1 = r10.megagroup
            if (r1 == 0) goto L_0x0a80
        L_0x078c:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x07a5
            r0 = 2131625966(0x7f0e07ee, float:1.8879155E38)
            r4 = 2
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r7 = 0
            r1[r7] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x07a5:
            r4 = 2
            r7 = 0
            boolean r8 = r1.isMusic()
            if (r8 == 0) goto L_0x07c0
            r0 = 2131625964(0x7f0e07ec, float:1.887915E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x07c0:
            boolean r4 = r1.isVideo()
            r7 = 2131625980(0x7f0e07fc, float:1.8879183E38)
            java.lang.String r8 = "NotificationActionPinnedText"
            if (r4 == 0) goto L_0x0815
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x0800
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0800
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
            goto L_0x040d
        L_0x0800:
            r2 = 0
            r3 = 2
            r0 = 2131625982(0x7f0e07fe, float:1.8879187E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0815:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0865
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0850
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0850
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
            goto L_0x040d
        L_0x0850:
            r2 = 0
            r4 = 2
            r0 = 2131625960(0x7f0e07e8, float:1.8879143E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0865:
            r2 = 0
            r4 = 2
            boolean r9 = r1.isVoice()
            if (r9 == 0) goto L_0x0880
            r0 = 2131625984(0x7f0e0800, float:1.8879191E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0880:
            boolean r9 = r1.isRoundVideo()
            if (r9 == 0) goto L_0x0899
            r0 = 2131625974(0x7f0e07f6, float:1.8879171E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0899:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0a4f
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x08a7
            goto L_0x0a4f
        L_0x08a7:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x08f7
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x08e2
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08e2
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
            goto L_0x040d
        L_0x08e2:
            r2 = 0
            r3 = 2
            r0 = 2131625950(0x7f0e07de, float:1.8879122E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x08f7:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0a3a
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x0901
            goto L_0x0a3a
        L_0x0901:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x091a
            r0 = 2131625958(0x7f0e07e6, float:1.8879139E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x091a:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0943
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131625948(0x7f0e07dc, float:1.8879118E38)
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
            goto L_0x040d
        L_0x0943:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0981
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0968
            r1 = 2131625972(0x7f0e07f4, float:1.8879167E38)
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
            goto L_0x040d
        L_0x0968:
            r3 = 0
            r4 = 2
            r1 = 2131625970(0x7f0e07f2, float:1.8879163E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x040d
        L_0x0981:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x09cd
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x09b8
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09b8
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
            goto L_0x040d
        L_0x09b8:
            r2 = 0
            r3 = 2
            r0 = 2131625968(0x7f0e07f0, float:1.8879159E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x09cd:
            r2 = 0
            r3 = 2
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x09e6
            r0 = 2131625952(0x7f0e07e0, float:1.8879126E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x09e6:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0a25
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0a25
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0a13
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0a14
        L_0x0a13:
            r3 = 0
        L_0x0a14:
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r12
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r2 = 2
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x040d
        L_0x0a25:
            r2 = 2
            r3 = 0
            r0 = 2131625966(0x7f0e07ee, float:1.8879155E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0a3a:
            r2 = 2
            r3 = 0
            r0 = 2131625956(0x7f0e07e4, float:1.8879135E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0a4f:
            r3 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0a6c
            r1 = 2131625978(0x7f0e07fa, float:1.887918E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x040d
        L_0x0a6c:
            r4 = 2
            r0 = 2131625976(0x7f0e07f8, float:1.8879175E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0a80:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x0a96
            r0 = 2131625967(0x7f0e07ef, float:1.8879157E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r4 = 0
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0a96:
            r4 = 0
            boolean r6 = r1.isMusic()
            if (r6 == 0) goto L_0x0aae
            r0 = 2131625965(0x7f0e07ed, float:1.8879153E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0aae:
            boolean r4 = r1.isVideo()
            r6 = 2131625981(0x7f0e07fd, float:1.8879185E38)
            java.lang.String r7 = "NotificationActionPinnedTextChannel"
            if (r4 == 0) goto L_0x0afe
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x0aec
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0aec
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
            goto L_0x040d
        L_0x0aec:
            r3 = 0
            r0 = 2131625983(0x7f0e07ff, float:1.887919E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0afe:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0b49
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0b37
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0b37
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
            goto L_0x040d
        L_0x0b37:
            r4 = 0
            r0 = 2131625961(0x7f0e07e9, float:1.8879145E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0b49:
            r4 = 0
            boolean r2 = r1.isVoice()
            if (r2 == 0) goto L_0x0b61
            r0 = 2131625985(0x7f0e0801, float:1.8879193E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0b61:
            boolean r2 = r1.isRoundVideo()
            if (r2 == 0) goto L_0x0b78
            r0 = 2131625975(0x7f0e07f7, float:1.8879173E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0b78:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0d10
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x0b86
            goto L_0x0d10
        L_0x0b86:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0bd1
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x0bbf
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0bbf
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
            goto L_0x040d
        L_0x0bbf:
            r3 = 0
            r0 = 2131625951(0x7f0e07df, float:1.8879124E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0bd1:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0cfe
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x0bdb
            goto L_0x0cfe
        L_0x0bdb:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0bf1
            r0 = 2131625959(0x7f0e07e7, float:1.887914E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0bf1:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131625949(0x7f0e07dd, float:1.887912E38)
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
            goto L_0x040d
        L_0x0CLASSNAME:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0c3b
            r1 = 2131625973(0x7f0e07f5, float:1.887917E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x040d
        L_0x0c3b:
            r2 = 2
            r4 = 0
            r1 = 2131625971(0x7f0e07f3, float:1.8879165E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x040d
        L_0x0CLASSNAME:
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
            goto L_0x040d
        L_0x0CLASSNAME:
            r3 = 0
            r0 = 2131625969(0x7f0e07f1, float:1.887916E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0CLASSNAME:
            r3 = 0
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0caf
            r0 = 2131625953(0x7f0e07e1, float:1.8879129E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0caf:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0cec
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0cec
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0cdc
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0cdd
        L_0x0cdc:
            r3 = 0
        L_0x0cdd:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            goto L_0x040d
        L_0x0cec:
            r3 = 0
            r0 = 2131625967(0x7f0e07ef, float:1.8879157E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0cfe:
            r3 = 0
            r0 = 2131625957(0x7f0e07e5, float:1.8879137E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0d10:
            r3 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0d2b
            r1 = 2131625979(0x7f0e07fb, float:1.8879181E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r10.title
            r2[r3] = r4
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x040d
        L_0x0d2b:
            r0 = 2131625977(0x7f0e07f9, float:1.8879177E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x0d3c:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 == 0) goto L_0x137f
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1380
        L_0x0d48:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0d7e
            boolean r1 = r10.megagroup
            if (r1 != 0) goto L_0x0d7e
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0d6c
            r0 = 2131624655(0x7f0e02cf, float:1.8876496E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0d6c:
            r3 = 0
            r0 = 2131624622(0x7f0e02ae, float:1.8876429E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0d7e:
            r3 = 0
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0d99
            r0 = 2131625990(0x7f0e0806, float:1.8879204E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0d99:
            r1 = 2
            r0 = 2131625989(0x7f0e0805, float:1.8879202E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0dad:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r1 == 0) goto L_0x1052
            boolean r1 = r10.megagroup
            if (r1 != 0) goto L_0x1052
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x0df0
            if (r21 != 0) goto L_0x0de0
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0de0
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1380
        L_0x0de0:
            r2 = 0
            r0 = 2131624606(0x7f0e029e, float:1.8876396E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0df0:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0e3e
            if (r21 != 0) goto L_0x0e2e
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0e2e
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e2e
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
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1380
        L_0x0e2e:
            r2 = 0
            r0 = 2131624607(0x7f0e029f, float:1.8876398E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0e3e:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x0e8c
            if (r21 != 0) goto L_0x0e7c
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0e7c
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e7c
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
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r4] = r5
            goto L_0x1380
        L_0x0e7c:
            r4 = 0
            r0 = 2131624613(0x7f0e02a5, float:1.887641E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0e8c:
            r4 = 0
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x0ea2
            r0 = 2131624598(0x7f0e0296, float:1.887638E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0ea2:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x0eb7
            r0 = 2131624610(0x7f0e02a2, float:1.8876405E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0eb7:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x0ecc
            r0 = 2131624605(0x7f0e029d, float:1.8876394E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0ecc:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r6 == 0) goto L_0x0ef0
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131624599(0x7f0e0297, float:1.8876382E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "ChannelMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1380
        L_0x0ef0:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0var_
            r1 = 2131624609(0x7f0e02a1, float:1.8876403E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x040d
        L_0x0var_:
            r2 = 2
            r3 = 0
            r1 = 2131624608(0x7f0e02a0, float:1.88764E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x040d
        L_0x0var_:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x1042
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x0var_
            goto L_0x1042
        L_0x0var_:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x0var_
            r0 = 2131624603(0x7f0e029b, float:1.887639E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0var_:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x1014
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x0fec
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x0var_
            goto L_0x0fec
        L_0x0var_:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x0fa4
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
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r3] = r5
            goto L_0x1380
        L_0x0var_:
            r3 = 0
            r0 = 2131624602(0x7f0e029a, float:1.8876388E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r12
            java.lang.String r2 = "ChannelMessageGIF"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0fa4:
            if (r21 != 0) goto L_0x0fdc
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0fdc
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0fdc
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
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1380
        L_0x0fdc:
            r2 = 0
            r0 = 2131624600(0x7f0e0298, float:1.8876384E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x0fec:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x1005
            r1 = 2131624612(0x7f0e02a4, float:1.8876409E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r12
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x040d
        L_0x1005:
            r0 = 2131624611(0x7f0e02a3, float:1.8876407E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x1014:
            r2 = 0
            if (r21 != 0) goto L_0x1033
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1033
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.CharSequence r0 = r0.messageText
            r1[r5] = r0
            r0 = 2131626049(0x7f0e0841, float:1.8879323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1380
        L_0x1033:
            r0 = 2131624606(0x7f0e029e, float:1.8876396E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x1042:
            r2 = 0
            r0 = 2131624604(0x7f0e029c, float:1.8876392E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x1052:
            boolean r1 = r20.isMediaEmpty()
            r4 = 2131626032(0x7f0e0830, float:1.8879289E38)
            java.lang.String r7 = "NotificationMessageGroupText"
            if (r1 == 0) goto L_0x1092
            if (r21 != 0) goto L_0x107f
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x107f
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
            goto L_0x1380
        L_0x107f:
            r2 = 0
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x1380
        L_0x1092:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x10e4
            if (r21 != 0) goto L_0x10cf
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x10cf
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x10cf
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
            goto L_0x1380
        L_0x10cf:
            r2 = 2
            r0 = 2131626026(0x7f0e082a, float:1.8879277E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x10e4:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x1136
            if (r21 != 0) goto L_0x1121
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x1121
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1121
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
            goto L_0x1380
        L_0x1121:
            r8 = 2
            r0 = 2131626033(0x7f0e0831, float:1.887929E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r9 = 0
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = " "
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x1136:
            r8 = 2
            r9 = 0
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x1151
            r0 = 2131626015(0x7f0e081f, float:1.8879254E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x1151:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x116a
            r0 = 2131626029(0x7f0e082d, float:1.8879283E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x116a:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x1183
            r0 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x1183:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x11ac
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131626016(0x7f0e0820, float:1.8879256E38)
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
            goto L_0x1380
        L_0x11ac:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x11ea
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x11d1
            r1 = 2131626028(0x7f0e082c, float:1.887928E38)
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
            goto L_0x040d
        L_0x11d1:
            r3 = 0
            r4 = 2
            r1 = 2131626027(0x7f0e082b, float:1.8879279E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x040d
        L_0x11ea:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x1209
            r0 = 2131626018(0x7f0e0822, float:1.887926E38)
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
            goto L_0x1380
        L_0x1209:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x133c
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x1213
            goto L_0x133c
        L_0x1213:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x122c
            r0 = 2131626022(0x7f0e0826, float:1.8879268E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x122c:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x130d
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x12dc
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x123e
            goto L_0x12dc
        L_0x123e:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x1290
            if (r21 != 0) goto L_0x127b
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x127b
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x127b
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
            goto L_0x1380
        L_0x127b:
            r2 = 2
            r0 = 2131626020(0x7f0e0824, float:1.8879264E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x1290:
            if (r21 != 0) goto L_0x12c7
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x12c7
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x12c7
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
            goto L_0x1380
        L_0x12c7:
            r2 = 2
            r0 = 2131626017(0x7f0e0821, float:1.8879258E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x12dc:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x12f9
            r1 = 2131626031(0x7f0e082f, float:1.8879287E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r2] = r12
            java.lang.String r2 = r10.title
            r3[r5] = r2
            r4 = 2
            r3[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x040d
        L_0x12f9:
            r4 = 2
            r0 = 2131626030(0x7f0e082e, float:1.8879285E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x040d
        L_0x130d:
            if (r21 != 0) goto L_0x132a
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x132a
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.CharSequence r0 = r0.messageText
            r3 = 2
            r1[r3] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            goto L_0x1380
        L_0x132a:
            r2 = 0
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x1380
        L_0x133c:
            r2 = 0
            r3 = 2
            r0 = 2131626023(0x7f0e0827, float:1.887927E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x1350:
            r2 = 0
            if (r23 == 0) goto L_0x1355
            r23[r2] = r2
        L_0x1355:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r0 == 0) goto L_0x136d
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x136d
            r0 = 2131624606(0x7f0e029e, float:1.8876396E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1380
        L_0x136d:
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x1380
        L_0x137e:
            r1 = 0
        L_0x137f:
            r15 = r1
        L_0x1380:
            return r15
        L_0x1381:
            r0 = 2131627454(0x7f0e0dbe, float:1.8882173E38)
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

    private void createNotificationShortcut(NotificationCompat.Builder builder, int i, String str, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, Person person) {
        String str2;
        if (Build.VERSION.SDK_INT < 29) {
            return;
        }
        if ((!ChatObject.isChannel(tLRPC$Chat) || tLRPC$Chat.megagroup) && SharedConfig.chatBubbles) {
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
                ArrayList arrayList2 = new ArrayList(1);
                arrayList2.add(str3);
                ShortcutManagerCompat.addDynamicShortcuts(ApplicationLoader.applicationContext, arrayList);
                ShortcutManagerCompat.removeDynamicShortcuts(ApplicationLoader.applicationContext, arrayList2);
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
            r9 = 2131626000(0x7f0e0810, float:1.8879224E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0557
        L_0x0544:
            java.lang.String r8 = "NotificationHiddenName"
            r9 = 2131626003(0x7f0e0813, float:1.887923E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0b17 }
            goto L_0x0557
        L_0x054e:
            java.lang.String r8 = "AppName"
            r9 = 2131624216(0x7f0e0118, float:1.8875605E38)
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
            r7 = 2131626051(0x7f0e0843, float:1.8879327E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r7, r1)     // Catch:{ Exception -> 0x0b17 }
            r2.append(r1)     // Catch:{ Exception -> 0x0b17 }
            java.lang.String r7 = r2.toString()     // Catch:{ Exception -> 0x0b17 }
            goto L_0x05ad
        L_0x060a:
            androidx.core.app.NotificationCompat$Builder r7 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0b17 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b17 }
            r7.<init>(r1)     // Catch:{ Exception -> 0x0b17 }
            r7.setContentTitle(r8)     // Catch:{ Exception -> 0x0b17 }
            r1 = 2131165759(0x7var_f, float:1.7945744E38)
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
            r4 = 2131626651(0x7f0e0a9b, float:1.8880544E38)
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
            r4 = 2131626651(0x7f0e0a9b, float:1.8880544E38)
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

    /* JADX WARNING: Removed duplicated region for block: B:127:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0309  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x031c  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0370  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0383 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x03cc  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x041f  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x043a  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x044e  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x046e  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0478 A[SYNTHETIC, Splitter:B:181:0x0478] */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x04f3  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x04fb  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0515 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0530  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0538  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0548  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x057e  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x05bb  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x068e  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x0695  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x0711  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x07f0  */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x080c  */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x083b  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x084b A[SYNTHETIC, Splitter:B:373:0x084b] */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x08f8  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0909  */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x0929  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0985  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x09b8  */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x09d9  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x09fb  */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x0aae  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0ab9  */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x0ac0  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0ad0  */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x0ad6  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0ada  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0adf  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0af3  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0b85  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0bfa  */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x0c4d A[Catch:{ JSONException -> 0x0c9d }] */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0CLASSNAME A[Catch:{ JSONException -> 0x0c9d }] */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0c7f  */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0CLASSNAME A[Catch:{ JSONException -> 0x0c9d }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01aa  */
    @android.annotation.SuppressLint({"InlinedApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r78, boolean r79, java.lang.String r80) {
        /*
            r77 = this;
            r8 = r77
            android.app.Notification r9 = r78.build()
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
            org.telegram.messenger.AccountInstance r0 = r77.getAccountInstance()
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
            if (r0 <= r6) goto L_0x00af
            if (r0 <= r6) goto L_0x00ac
            int r0 = r10.size()
            if (r0 <= r5) goto L_0x00ac
            goto L_0x00af
        L_0x00ac:
            r16 = 0
            goto L_0x00b1
        L_0x00af:
            r16 = 1
        L_0x00b1:
            r4 = 26
            if (r16 == 0) goto L_0x00bc
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r4) goto L_0x00bc
            checkOtherNotificationsChannel()
        L_0x00bc:
            org.telegram.messenger.UserConfig r0 = r77.getUserConfig()
            int r3 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x00d2
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x00cf
            goto L_0x00d2
        L_0x00cf:
            r17 = 0
            goto L_0x00d4
        L_0x00d2:
            r17 = 1
        L_0x00d4:
            int r2 = r10.size()
            r1 = 0
        L_0x00d9:
            java.lang.String r6 = "id"
            if (r1 >= r2) goto L_0x0cb6
            java.lang.Object r0 = r10.get(r1)
            java.lang.Long r0 = (java.lang.Long) r0
            r19 = r6
            long r5 = r0.longValue()
            java.lang.Object r0 = r11.get(r5)
            r4 = r0
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            java.lang.Object r0 = r4.get(r12)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r15 = r0.getId()
            int r12 = (int) r5
            r23 = r1
            r1 = 32
            r24 = r9
            r25 = r10
            long r9 = r5 >> r1
            int r10 = (int) r9
            java.lang.Object r0 = r13.get(r5)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x011a
            if (r12 == 0) goto L_0x0115
            java.lang.Integer r0 = java.lang.Integer.valueOf(r12)
            goto L_0x011d
        L_0x0115:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r10)
            goto L_0x011d
        L_0x011a:
            r13.remove(r5)
        L_0x011d:
            r9 = r0
            if (r7 == 0) goto L_0x0126
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            goto L_0x0127
        L_0x0126:
            r0 = 0
        L_0x0127:
            r1 = 0
            java.lang.Object r27 = r4.get(r1)
            r1 = r27
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r27 = r0
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            r28 = r11
            int r11 = r0.date
            r29 = r2
            android.util.LongSparseArray r2 = new android.util.LongSparseArray
            r2.<init>()
            r30 = 0
            if (r12 == 0) goto L_0x0259
            r0 = 777000(0xbdb28, float:1.088809E-39)
            if (r12 == r0) goto L_0x014a
            r0 = 1
            goto L_0x014b
        L_0x014a:
            r0 = 0
        L_0x014b:
            if (r12 <= 0) goto L_0x01c3
            r32 = r0
            org.telegram.messenger.MessagesController r0 = r77.getMessagesController()
            r33 = r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r7)
            if (r0 != 0) goto L_0x0184
            boolean r7 = r1.isFcmMessage()
            if (r7 == 0) goto L_0x016a
            java.lang.String r1 = r1.localName
            r34 = r0
            goto L_0x01a7
        L_0x016a:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x020d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found user to show dialog notification "
            r0.append(r1)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x020d
        L_0x0184:
            java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r0)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            if (r7 == 0) goto L_0x01a1
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x01a1
            r34 = r0
            r35 = r1
            long r0 = r7.volume_id
            int r36 = (r0 > r30 ? 1 : (r0 == r30 ? 0 : -1))
            if (r36 == 0) goto L_0x01a5
            int r0 = r7.local_id
            if (r0 == 0) goto L_0x01a5
            r1 = r35
            goto L_0x01a8
        L_0x01a1:
            r34 = r0
            r35 = r1
        L_0x01a5:
            r1 = r35
        L_0x01a7:
            r7 = 0
        L_0x01a8:
            if (r12 != r3) goto L_0x01b3
            r0 = 2131625794(0x7f0e0742, float:1.8878806E38)
            java.lang.String r1 = "MessageScheduledReminderNotification"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r0)
        L_0x01b3:
            r35 = r1
            r0 = r7
            r36 = r13
            r37 = r14
            r13 = r27
            r7 = r34
            r14 = 0
            r27 = 0
            goto L_0x02e0
        L_0x01c3:
            r32 = r0
            r33 = r7
            org.telegram.messenger.MessagesController r0 = r77.getMessagesController()
            int r7 = -r12
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r7)
            if (r0 != 0) goto L_0x021a
            boolean r7 = r1.isFcmMessage()
            if (r7 == 0) goto L_0x01f5
            boolean r7 = r1.isMegagroup()
            r34 = r7
            java.lang.String r7 = r1.localName
            boolean r1 = r1.localChannel
            r35 = r7
            r36 = r13
            r37 = r14
            r13 = r27
            r7 = 0
            r14 = r0
            r27 = r1
            r0 = 0
            goto L_0x02e2
        L_0x01f5:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x020d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found chat to show dialog notification "
            r0.append(r1)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x020d:
            r71 = r3
            r36 = r13
            r2 = r14
            r22 = r23
            r23 = r29
            r15 = r33
            goto L_0x0295
        L_0x021a:
            boolean r1 = r0.megagroup
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r7 == 0) goto L_0x022a
            boolean r7 = r0.megagroup
            if (r7 != 0) goto L_0x022a
            r34 = r1
            r7 = 1
            goto L_0x022d
        L_0x022a:
            r34 = r1
            r7 = 0
        L_0x022d:
            java.lang.String r1 = r0.title
            r35 = r1
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            if (r1 == 0) goto L_0x024c
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            if (r1 == 0) goto L_0x024c
            r36 = r13
            r37 = r14
            long r13 = r1.volume_id
            int r38 = (r13 > r30 ? 1 : (r13 == r30 ? 0 : -1))
            if (r38 == 0) goto L_0x0250
            int r13 = r1.local_id
            if (r13 == 0) goto L_0x0250
            r14 = r0
            r0 = r1
            r13 = r27
            goto L_0x0254
        L_0x024c:
            r36 = r13
            r37 = r14
        L_0x0250:
            r14 = r0
            r13 = r27
            r0 = 0
        L_0x0254:
            r27 = r7
            r7 = 0
            goto L_0x02e2
        L_0x0259:
            r33 = r7
            r36 = r13
            r37 = r14
            long r0 = globalSecretChatId
            int r7 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r7 == 0) goto L_0x02cc
            org.telegram.messenger.MessagesController r0 = r77.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r1)
            if (r0 != 0) goto L_0x029f
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x028b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found secret chat to show dialog notification "
            r0.append(r1)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x028b:
            r71 = r3
            r22 = r23
            r23 = r29
            r15 = r33
            r2 = r37
        L_0x0295:
            r1 = 26
            r18 = 1
            r19 = 27
            r20 = 0
            goto L_0x0c9f
        L_0x029f:
            org.telegram.messenger.MessagesController r1 = r77.getMessagesController()
            int r7 = r0.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r7)
            if (r1 != 0) goto L_0x02ca
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x028b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "not found secret chat user to show dialog notification "
            r1.append(r2)
            int r0 = r0.user_id
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x028b
        L_0x02ca:
            r0 = r1
            goto L_0x02cd
        L_0x02cc:
            r0 = 0
        L_0x02cd:
            r1 = 2131626783(0x7f0e0b1f, float:1.8880812E38)
            java.lang.String r7 = "SecretChatName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r1)
            r7 = r0
            r35 = r1
            r0 = 0
            r13 = 0
            r14 = 0
            r27 = 0
            r32 = 0
        L_0x02e0:
            r34 = 0
        L_0x02e2:
            java.lang.String r1 = "NotificationHiddenChatName"
            r39 = r14
            java.lang.String r14 = "NotificationHiddenName"
            if (r17 == 0) goto L_0x0309
            if (r12 >= 0) goto L_0x02f6
            r41 = r7
            r7 = 2131626000(0x7f0e0810, float:1.8879224E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r7)
            goto L_0x02ff
        L_0x02f6:
            r41 = r7
            r7 = 2131626003(0x7f0e0813, float:1.887923E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r7)
        L_0x02ff:
            r35 = r10
            r32 = r11
            r42 = r14
            r7 = 0
            r11 = 0
            r10 = r0
            goto L_0x031a
        L_0x0309:
            r41 = r7
            r7 = r0
            r42 = r14
            r75 = r35
            r35 = r10
            r10 = r75
            r76 = r32
            r32 = r11
            r11 = r76
        L_0x031a:
            if (r7 == 0) goto L_0x0370
            r14 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r14)
            int r14 = android.os.Build.VERSION.SDK_INT
            r43 = r1
            r1 = 28
            if (r14 >= r1) goto L_0x036a
            org.telegram.messenger.ImageLoader r1 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r14 = "50_50"
            r44 = r13
            r13 = 0
            android.graphics.drawable.BitmapDrawable r1 = r1.getImageFromMemory(r7, r13, r14)
            if (r1 == 0) goto L_0x033f
            android.graphics.Bitmap r1 = r1.getBitmap()
        L_0x033c:
            r14 = r1
            r1 = r0
            goto L_0x0377
        L_0x033f:
            boolean r1 = r0.exists()     // Catch:{ all -> 0x036d }
            if (r1 == 0) goto L_0x0368
            r1 = 1126170624(0x43200000, float:160.0)
            r14 = 1112014848(0x42480000, float:50.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x036d }
            float r14 = (float) r14     // Catch:{ all -> 0x036d }
            float r1 = r1 / r14
            android.graphics.BitmapFactory$Options r14 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x036d }
            r14.<init>()     // Catch:{ all -> 0x036d }
            r21 = 1065353216(0x3var_, float:1.0)
            int r21 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r21 >= 0) goto L_0x035c
            r1 = 1
            goto L_0x035d
        L_0x035c:
            int r1 = (int) r1     // Catch:{ all -> 0x036d }
        L_0x035d:
            r14.inSampleSize = r1     // Catch:{ all -> 0x036d }
            java.lang.String r1 = r0.getAbsolutePath()     // Catch:{ all -> 0x036d }
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeFile(r1, r14)     // Catch:{ all -> 0x036d }
            goto L_0x033c
        L_0x0368:
            r1 = r13
            goto L_0x033c
        L_0x036a:
            r44 = r13
            r13 = 0
        L_0x036d:
            r1 = r0
            r14 = r13
            goto L_0x0377
        L_0x0370:
            r43 = r1
            r44 = r13
            r13 = 0
            r1 = r13
            r14 = r1
        L_0x0377:
            java.lang.String r13 = "dialog_id"
            r45 = r7
            java.lang.String r7 = "max_id"
            r46 = r1
            java.lang.String r1 = "currentAccount"
            if (r27 == 0) goto L_0x0385
            if (r34 == 0) goto L_0x040b
        L_0x0385:
            if (r11 == 0) goto L_0x040b
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x040b
            if (r3 == r12) goto L_0x040b
            android.content.Intent r0 = new android.content.Intent
            r47 = r11
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
            r48 = r14
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r14 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r11, r14)
            r0.putExtra(r13, r5)
            r0.putExtra(r7, r15)
            int r11 = r8.currentAccount
            r0.putExtra(r1, r11)
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r14 = r9.intValue()
            r49 = r9
            r9 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r11, r14, r0, r9)
            androidx.core.app.RemoteInput$Builder r9 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r11 = "extra_voice_reply"
            r9.<init>(r11)
            r11 = 2131626651(0x7f0e0a9b, float:1.8880544E38)
            java.lang.String r14 = "Reply"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            r9.setLabel(r11)
            androidx.core.app.RemoteInput r9 = r9.build()
            if (r12 >= 0) goto L_0x03de
            r14 = 1
            java.lang.Object[] r11 = new java.lang.Object[r14]
            r14 = 0
            r11[r14] = r10
            java.lang.String r14 = "ReplyToGroup"
            r51 = r7
            r7 = 2131626652(0x7f0e0a9c, float:1.8880546E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r14, r7, r11)
            goto L_0x03ef
        L_0x03de:
            r51 = r7
            r7 = 2131626653(0x7f0e0a9d, float:1.8880548E38)
            r11 = 1
            java.lang.Object[] r14 = new java.lang.Object[r11]
            r11 = 0
            r14[r11] = r10
            java.lang.String r11 = "ReplyToUser"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r11, r7, r14)
        L_0x03ef:
            androidx.core.app.NotificationCompat$Action$Builder r11 = new androidx.core.app.NotificationCompat$Action$Builder
            r14 = 2131165485(0x7var_d, float:1.7945188E38)
            r11.<init>(r14, r7, r0)
            r7 = 1
            r11.setAllowGeneratedReplies(r7)
            r11.setSemanticAction(r7)
            r11.addRemoteInput(r9)
            r7 = 0
            r11.setShowsUserInterface(r7)
            androidx.core.app.NotificationCompat$Action r0 = r11.build()
            r9 = r0
            goto L_0x0415
        L_0x040b:
            r51 = r7
            r49 = r9
            r47 = r11
            r48 = r14
            r7 = 0
            r9 = 0
        L_0x0415:
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            java.lang.Object r0 = r0.get(r5)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0423
            java.lang.Integer r0 = java.lang.Integer.valueOf(r7)
        L_0x0423:
            int r0 = r0.intValue()
            int r7 = r4.size()
            int r0 = java.lang.Math.max(r0, r7)
            r7 = 2
            r11 = 1
            if (r0 <= r11) goto L_0x044e
            int r14 = android.os.Build.VERSION.SDK_INT
            r11 = 28
            if (r14 < r11) goto L_0x043a
            goto L_0x044e
        L_0x043a:
            java.lang.Object[] r11 = new java.lang.Object[r7]
            r14 = 0
            r11[r14] = r10
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r14 = 1
            r11[r14] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r11)
            r11 = r0
            goto L_0x044f
        L_0x044e:
            r11 = r10
        L_0x044f:
            long r7 = (long) r3
            java.lang.Object r0 = r2.get(r7)
            r50 = r0
            androidx.core.app.Person r50 = (androidx.core.app.Person) r50
            int r0 = android.os.Build.VERSION.SDK_INT
            r14 = 28
            if (r0 < r14) goto L_0x04e8
            if (r50 != 0) goto L_0x04e8
            org.telegram.messenger.MessagesController r0 = r77.getMessagesController()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r14)
            if (r0 != 0) goto L_0x0476
            org.telegram.messenger.UserConfig r0 = r77.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x0476:
            if (r0 == 0) goto L_0x04e8
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r0.photo     // Catch:{ all -> 0x04de }
            if (r14 == 0) goto L_0x04e8
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r0.photo     // Catch:{ all -> 0x04de }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ all -> 0x04de }
            if (r14 == 0) goto L_0x04e8
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r0.photo     // Catch:{ all -> 0x04de }
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small     // Catch:{ all -> 0x04de }
            r53 = r7
            long r7 = r14.volume_id     // Catch:{ all -> 0x04d4 }
            int r14 = (r7 > r30 ? 1 : (r7 == r30 ? 0 : -1))
            if (r14 == 0) goto L_0x04cb
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo     // Catch:{ all -> 0x04d4 }
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small     // Catch:{ all -> 0x04d4 }
            int r7 = r7.local_id     // Catch:{ all -> 0x04d4 }
            if (r7 == 0) goto L_0x04cb
            androidx.core.app.Person$Builder r7 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x04d4 }
            r7.<init>()     // Catch:{ all -> 0x04d4 }
            java.lang.String r8 = "FromYou"
            r14 = 2131625434(0x7f0e05da, float:1.8878076E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r14)     // Catch:{ all -> 0x04d4 }
            r7.setName(r8)     // Catch:{ all -> 0x04d4 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x04d4 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x04d4 }
            r8 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r8)     // Catch:{ all -> 0x04d4 }
            r8 = r77
            r75 = r53
            r53 = r15
            r14 = r75
            r8.loadRoundAvatar(r0, r7)     // Catch:{ all -> 0x04c9 }
            androidx.core.app.Person r7 = r7.build()     // Catch:{ all -> 0x04c9 }
            r2.put(r14, r7)     // Catch:{ all -> 0x04c5 }
            r50 = r7
            goto L_0x04ed
        L_0x04c5:
            r0 = move-exception
            r50 = r7
            goto L_0x04e4
        L_0x04c9:
            r0 = move-exception
            goto L_0x04e4
        L_0x04cb:
            r8 = r77
            r75 = r53
            r53 = r15
            r14 = r75
            goto L_0x04ed
        L_0x04d4:
            r0 = move-exception
            r8 = r77
            r75 = r53
            r53 = r15
            r14 = r75
            goto L_0x04e4
        L_0x04de:
            r0 = move-exception
            r53 = r15
            r14 = r7
            r8 = r77
        L_0x04e4:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x04ed
        L_0x04e8:
            r53 = r15
            r14 = r7
            r8 = r77
        L_0x04ed:
            r0 = r50
            java.lang.String r7 = ""
            if (r0 == 0) goto L_0x04fb
            r50 = r3
            androidx.core.app.NotificationCompat$MessagingStyle r3 = new androidx.core.app.NotificationCompat$MessagingStyle
            r3.<init>((androidx.core.app.Person) r0)
            goto L_0x0502
        L_0x04fb:
            r50 = r3
            androidx.core.app.NotificationCompat$MessagingStyle r3 = new androidx.core.app.NotificationCompat$MessagingStyle
            r3.<init>((java.lang.CharSequence) r7)
        L_0x0502:
            int r0 = android.os.Build.VERSION.SDK_INT
            r54 = r13
            r13 = 28
            if (r0 < r13) goto L_0x050e
            if (r12 >= 0) goto L_0x0511
            if (r27 != 0) goto L_0x0511
        L_0x050e:
            r3.setConversationTitle(r11)
        L_0x0511:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r13) goto L_0x051c
            if (r27 != 0) goto L_0x051a
            if (r12 >= 0) goto L_0x051a
            goto L_0x051c
        L_0x051a:
            r0 = 0
            goto L_0x051d
        L_0x051c:
            r0 = 1
        L_0x051d:
            r3.setGroupConversation(r0)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r55 = r9
            r13 = 1
            java.lang.String[] r9 = new java.lang.String[r13]
            r56 = r1
            boolean[] r1 = new boolean[r13]
            if (r44 == 0) goto L_0x0538
            org.json.JSONArray r0 = new org.json.JSONArray
            r0.<init>()
            r57 = r0
            goto L_0x053a
        L_0x0538:
            r57 = 0
        L_0x053a:
            int r0 = r4.size()
            int r0 = r0 - r13
            r13 = r0
            r58 = 0
            r59 = 0
        L_0x0544:
            r60 = 1000(0x3e8, double:4.94E-321)
            if (r13 < 0) goto L_0x08b9
            java.lang.Object r0 = r4.get(r13)
            r62 = r4
            r4 = r0
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            java.lang.String r0 = r8.getShortStringForMessage(r4, r9, r1)
            int r63 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r63 != 0) goto L_0x0560
            r22 = 0
            r9[r22] = r10
            r63 = r10
            goto L_0x057a
        L_0x0560:
            r22 = 0
            r63 = r10
            if (r12 >= 0) goto L_0x057a
            org.telegram.tgnet.TLRPC$Message r10 = r4.messageOwner
            boolean r10 = r10.from_scheduled
            if (r10 == 0) goto L_0x057a
            r10 = 2131626046(0x7f0e083e, float:1.8879317E38)
            r64 = r13
            java.lang.String r13 = "NotificationMessageScheduledName"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r9[r22] = r10
            goto L_0x057c
        L_0x057a:
            r64 = r13
        L_0x057c:
            if (r0 != 0) goto L_0x05bb
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05a8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r10 = "message text is null for "
            r0.append(r10)
            int r10 = r4.getId()
            r0.append(r10)
            java.lang.String r10 = " did = "
            r0.append(r10)
            r10 = r3
            long r3 = r4.getDialogId()
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x05a9
        L_0x05a8:
            r10 = r3
        L_0x05a9:
            r68 = r1
            r69 = r5
            r40 = r9
            r67 = r11
            r65 = r14
            r11 = r42
            r9 = r57
            r3 = 28
            goto L_0x08a2
        L_0x05bb:
            r10 = r3
            int r3 = r11.length()
            if (r3 <= 0) goto L_0x05c7
            java.lang.String r3 = "\n\n"
            r11.append(r3)
        L_0x05c7:
            int r3 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x05f1
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            boolean r3 = r3.from_scheduled
            if (r3 == 0) goto L_0x05f1
            if (r12 <= 0) goto L_0x05f1
            r3 = 2
            java.lang.Object[] r13 = new java.lang.Object[r3]
            r3 = 2131626046(0x7f0e083e, float:1.8879317E38)
            r65 = r14
            java.lang.String r14 = "NotificationMessageScheduledName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r14 = 0
            r13[r14] = r3
            r3 = 1
            r13[r3] = r0
            java.lang.String r0 = "%1$s: %2$s"
            java.lang.String r0 = java.lang.String.format(r0, r13)
            r11.append(r0)
            goto L_0x060f
        L_0x05f1:
            r65 = r14
            r14 = 0
            r3 = r9[r14]
            if (r3 == 0) goto L_0x060c
            r3 = 2
            java.lang.Object[] r13 = new java.lang.Object[r3]
            r15 = r9[r14]
            r13[r14] = r15
            r14 = 1
            r13[r14] = r0
            java.lang.String r14 = "%1$s: %2$s"
            java.lang.String r13 = java.lang.String.format(r14, r13)
            r11.append(r13)
            goto L_0x060f
        L_0x060c:
            r11.append(r0)
        L_0x060f:
            r13 = r0
            if (r12 <= 0) goto L_0x0614
            long r14 = (long) r12
            goto L_0x0621
        L_0x0614:
            if (r27 == 0) goto L_0x0619
            int r0 = -r12
        L_0x0617:
            long r14 = (long) r0
            goto L_0x0621
        L_0x0619:
            if (r12 >= 0) goto L_0x0620
            int r0 = r4.getFromId()
            goto L_0x0617
        L_0x0620:
            r14 = r5
        L_0x0621:
            java.lang.Object r0 = r2.get(r14)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r22 = 0
            r52 = r9[r22]
            if (r52 != 0) goto L_0x0672
            if (r17 == 0) goto L_0x0669
            if (r12 >= 0) goto L_0x0657
            if (r27 == 0) goto L_0x0647
            int r3 = android.os.Build.VERSION.SDK_INT
            r67 = r11
            r11 = 27
            if (r3 <= r11) goto L_0x066b
            r11 = r43
            r3 = 2131626000(0x7f0e0810, float:1.8879224E38)
            java.lang.String r38 = org.telegram.messenger.LocaleController.getString(r11, r3)
            r3 = r38
            goto L_0x0654
        L_0x0647:
            r67 = r11
            r11 = r43
            r3 = 2131626001(0x7f0e0811, float:1.8879226E38)
            java.lang.String r11 = "NotificationHiddenChatUserName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
        L_0x0654:
            r11 = r42
            goto L_0x067f
        L_0x0657:
            r67 = r11
            int r3 = android.os.Build.VERSION.SDK_INT
            r11 = 27
            if (r3 <= r11) goto L_0x066b
            r11 = r42
            r3 = 2131626003(0x7f0e0813, float:1.887923E38)
            java.lang.String r40 = org.telegram.messenger.LocaleController.getString(r11, r3)
            goto L_0x067d
        L_0x0669:
            r67 = r11
        L_0x066b:
            r11 = r42
            r3 = 2131626003(0x7f0e0813, float:1.887923E38)
            r3 = r7
            goto L_0x067f
        L_0x0672:
            r67 = r11
            r11 = r42
            r3 = 2131626003(0x7f0e0813, float:1.887923E38)
            r22 = 0
            r40 = r9[r22]
        L_0x067d:
            r3 = r40
        L_0x067f:
            r40 = r9
            if (r0 == 0) goto L_0x0695
            java.lang.CharSequence r9 = r0.getName()
            boolean r9 = android.text.TextUtils.equals(r9, r3)
            if (r9 != 0) goto L_0x068e
            goto L_0x0695
        L_0x068e:
            r68 = r1
            r69 = r5
        L_0x0692:
            r1 = r0
            goto L_0x070f
        L_0x0695:
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            r0.setName(r3)
            r3 = 0
            boolean r9 = r1[r3]
            if (r9 == 0) goto L_0x0703
            if (r12 == 0) goto L_0x0703
            int r3 = android.os.Build.VERSION.SDK_INT
            r9 = 28
            if (r3 < r9) goto L_0x0703
            if (r12 > 0) goto L_0x06f9
            if (r27 == 0) goto L_0x06af
            goto L_0x06f9
        L_0x06af:
            if (r12 >= 0) goto L_0x06f3
            int r3 = r4.getFromId()
            org.telegram.messenger.MessagesController r9 = r77.getMessagesController()
            r68 = r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r1 = r9.getUser(r1)
            if (r1 != 0) goto L_0x06d7
            org.telegram.messenger.MessagesStorage r1 = r77.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r1 = r1.getUserSync(r3)
            if (r1 == 0) goto L_0x06d7
            org.telegram.messenger.MessagesController r3 = r77.getMessagesController()
            r9 = 1
            r3.putUser(r1, r9)
        L_0x06d7:
            if (r1 == 0) goto L_0x06f5
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r1.photo
            if (r1 == 0) goto L_0x06f5
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            if (r1 == 0) goto L_0x06f5
            r69 = r5
            long r5 = r1.volume_id
            int r3 = (r5 > r30 ? 1 : (r5 == r30 ? 0 : -1))
            if (r3 == 0) goto L_0x06f7
            int r3 = r1.local_id
            if (r3 == 0) goto L_0x06f7
            r3 = 1
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r3)
            goto L_0x06ff
        L_0x06f3:
            r68 = r1
        L_0x06f5:
            r69 = r5
        L_0x06f7:
            r1 = 0
            goto L_0x06ff
        L_0x06f9:
            r68 = r1
            r69 = r5
            r1 = r46
        L_0x06ff:
            r8.loadRoundAvatar(r1, r0)
            goto L_0x0707
        L_0x0703:
            r68 = r1
            r69 = r5
        L_0x0707:
            androidx.core.app.Person r0 = r0.build()
            r2.put(r14, r0)
            goto L_0x0692
        L_0x070f:
            if (r12 == 0) goto L_0x083b
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r0 < r3) goto L_0x07ed
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r5 = "activity"
            java.lang.Object r0 = r0.getSystemService(r5)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x07ed
            if (r17 != 0) goto L_0x07ed
            boolean r0 = r4.isSecretMedia()
            if (r0 != 0) goto L_0x07ed
            int r0 = r4.type
            r5 = 1
            if (r0 == r5) goto L_0x073a
            boolean r0 = r4.isSticker()
            if (r0 == 0) goto L_0x07ed
        L_0x073a:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r5 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            int r6 = r6.date
            long r14 = (long) r6
            long r14 = r14 * r60
            r5.<init>(r13, r14, r1)
            boolean r6 = r4.isSticker()
            if (r6 == 0) goto L_0x0755
            java.lang.String r6 = "image/webp"
            goto L_0x0757
        L_0x0755:
            java.lang.String r6 = "image/jpeg"
        L_0x0757:
            boolean r9 = r0.exists()
            if (r9 == 0) goto L_0x076b
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0766 }
            java.lang.String r14 = "org.telegram.messenger.beta.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r9, r14, r0)     // Catch:{ Exception -> 0x0766 }
            goto L_0x07bd
        L_0x0766:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x07bc
        L_0x076b:
            org.telegram.messenger.FileLoader r9 = r77.getFileLoader()
            java.lang.String r14 = r0.getName()
            boolean r9 = r9.isLoadingFile(r14)
            if (r9 == 0) goto L_0x07bc
            android.net.Uri$Builder r9 = new android.net.Uri$Builder
            r9.<init>()
            java.lang.String r14 = "content"
            android.net.Uri$Builder r9 = r9.scheme(r14)
            java.lang.String r14 = "org.telegram.messenger.beta.notification_image_provider"
            android.net.Uri$Builder r9 = r9.authority(r14)
            java.lang.String r14 = "msg_media_raw"
            android.net.Uri$Builder r9 = r9.appendPath(r14)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            int r15 = r8.currentAccount
            r14.append(r15)
            r14.append(r7)
            java.lang.String r14 = r14.toString()
            android.net.Uri$Builder r9 = r9.appendPath(r14)
            java.lang.String r14 = r0.getName()
            android.net.Uri$Builder r9 = r9.appendPath(r14)
            java.lang.String r0 = r0.getAbsolutePath()
            java.lang.String r14 = "final_path"
            android.net.Uri$Builder r0 = r9.appendQueryParameter(r14, r0)
            android.net.Uri r0 = r0.build()
            goto L_0x07bd
        L_0x07bc:
            r0 = 0
        L_0x07bd:
            if (r0 == 0) goto L_0x07ed
            r5.setData(r6, r0)
            r10.addMessage(r5)
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r6 = "com.android.systemui"
            r9 = 1
            r5.grantUriPermission(r6, r0, r9)
            org.telegram.messenger.-$$Lambda$NotificationsController$2iZFI3opoasnRhiUslwS5Iqt9vs r5 = new org.telegram.messenger.-$$Lambda$NotificationsController$2iZFI3opoasnRhiUslwS5Iqt9vs
            r5.<init>(r0)
            r14 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r5, r14)
            java.lang.CharSequence r0 = r4.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x07eb
            java.lang.CharSequence r0 = r4.caption
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            int r5 = r5.date
            long r5 = (long) r5
            long r5 = r5 * r60
            r10.addMessage(r0, r5, r1)
        L_0x07eb:
            r0 = 1
            goto L_0x07ee
        L_0x07ed:
            r0 = 0
        L_0x07ee:
            if (r0 != 0) goto L_0x07fa
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            int r0 = r0.date
            long r5 = (long) r0
            long r5 = r5 * r60
            r10.addMessage(r13, r5, r1)
        L_0x07fa:
            if (r17 != 0) goto L_0x0847
            boolean r0 = r4.isVoice()
            if (r0 == 0) goto L_0x0847
            java.util.List r0 = r10.getMessages()
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x0847
            org.telegram.tgnet.TLRPC$Message r1 = r4.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 24
            if (r5 < r6) goto L_0x0823
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0821 }
            java.lang.String r6 = "org.telegram.messenger.beta.provider"
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r5, r6, r1)     // Catch:{ Exception -> 0x0821 }
            goto L_0x0827
        L_0x0821:
            r1 = 0
            goto L_0x0827
        L_0x0823:
            android.net.Uri r1 = android.net.Uri.fromFile(r1)
        L_0x0827:
            if (r1 == 0) goto L_0x0847
            int r5 = r0.size()
            r6 = 1
            int r5 = r5 - r6
            java.lang.Object r0 = r0.get(r5)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r0
            java.lang.String r5 = "audio/ogg"
            r0.setData(r5, r1)
            goto L_0x0847
        L_0x083b:
            r3 = 28
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            int r0 = r0.date
            long r5 = (long) r0
            long r5 = r5 * r60
            r10.addMessage(r13, r5, r1)
        L_0x0847:
            r9 = r57
            if (r9 == 0) goto L_0x088b
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x088a }
            r0.<init>()     // Catch:{ JSONException -> 0x088a }
            java.lang.String r1 = "text"
            r0.put(r1, r13)     // Catch:{ JSONException -> 0x088a }
            java.lang.String r1 = "date"
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner     // Catch:{ JSONException -> 0x088a }
            int r5 = r5.date     // Catch:{ JSONException -> 0x088a }
            r0.put(r1, r5)     // Catch:{ JSONException -> 0x088a }
            boolean r1 = r4.isFromUser()     // Catch:{ JSONException -> 0x088a }
            if (r1 == 0) goto L_0x0886
            if (r12 >= 0) goto L_0x0886
            org.telegram.messenger.MessagesController r1 = r77.getMessagesController()     // Catch:{ JSONException -> 0x088a }
            int r5 = r4.getFromId()     // Catch:{ JSONException -> 0x088a }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ JSONException -> 0x088a }
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r5)     // Catch:{ JSONException -> 0x088a }
            if (r1 == 0) goto L_0x0886
            java.lang.String r5 = "fname"
            java.lang.String r6 = r1.first_name     // Catch:{ JSONException -> 0x088a }
            r0.put(r5, r6)     // Catch:{ JSONException -> 0x088a }
            java.lang.String r5 = "lname"
            java.lang.String r1 = r1.last_name     // Catch:{ JSONException -> 0x088a }
            r0.put(r5, r1)     // Catch:{ JSONException -> 0x088a }
        L_0x0886:
            r9.put(r0)     // Catch:{ JSONException -> 0x088a }
            goto L_0x088b
        L_0x088a:
        L_0x088b:
            r0 = 777000(0xbdb28, double:3.83889E-318)
            int r5 = (r69 > r0 ? 1 : (r69 == r0 ? 0 : -1))
            if (r5 != 0) goto L_0x08a2
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x08a2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r1 = r4.getId()
            r59 = r0
            r58 = r1
        L_0x08a2:
            int r13 = r64 + -1
            r57 = r9
            r3 = r10
            r42 = r11
            r9 = r40
            r4 = r62
            r10 = r63
            r14 = r65
            r11 = r67
            r1 = r68
            r5 = r69
            goto L_0x0544
        L_0x08b9:
            r62 = r4
            r69 = r5
            r63 = r10
            r67 = r11
            r9 = r57
            r10 = r3
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.ui.LaunchActivity> r3 = org.telegram.ui.LaunchActivity.class
            r0.<init>(r1, r3)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "com.tmessages.openchat"
            r1.append(r3)
            double r3 = java.lang.Math.random()
            r1.append(r3)
            r3 = 2147483647(0x7fffffff, float:NaN)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            r0.setAction(r1)
            r1 = 32768(0x8000, float:4.5918E-41)
            r0.setFlags(r1)
            java.lang.String r1 = "android.intent.category.LAUNCHER"
            r0.addCategory(r1)
            if (r12 == 0) goto L_0x0909
            if (r12 <= 0) goto L_0x0900
            java.lang.String r1 = "userId"
            r0.putExtra(r1, r12)
            goto L_0x0906
        L_0x0900:
            int r1 = -r12
            java.lang.String r3 = "chatId"
            r0.putExtra(r3, r1)
        L_0x0906:
            r3 = r35
            goto L_0x0910
        L_0x0909:
            java.lang.String r1 = "encId"
            r3 = r35
            r0.putExtra(r1, r3)
        L_0x0910:
            int r1 = r8.currentAccount
            r4 = r56
            r0.putExtra(r4, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r5 = 1073741824(0x40000000, float:2.0)
            r6 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r1, r6, r0, r5)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            r5 = r55
            if (r55 == 0) goto L_0x092c
            r1.addAction(r5)
        L_0x092c:
            android.content.Intent r6 = new android.content.Intent
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r13 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r6.<init>(r11, r13)
            r11 = 32
            r6.addFlags(r11)
            java.lang.String r11 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r6.setAction(r11)
            r11 = r54
            r13 = r69
            r6.putExtra(r11, r13)
            r15 = r51
            r11 = r53
            r6.putExtra(r15, r11)
            int r15 = r8.currentAccount
            r6.putExtra(r4, r15)
            android.content.Context r15 = org.telegram.messenger.ApplicationLoader.applicationContext
            r26 = r9
            int r9 = r49.intValue()
            r30 = r2
            r2 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r6 = android.app.PendingIntent.getBroadcast(r15, r9, r6, r2)
            androidx.core.app.NotificationCompat$Action$Builder r2 = new androidx.core.app.NotificationCompat$Action$Builder
            r9 = 2131165628(0x7var_bc, float:1.7945478E38)
            r15 = 2131625722(0x7f0e06fa, float:1.887866E38)
            r55 = r5
            java.lang.String r5 = "MarkAsRead"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r15)
            r2.<init>(r9, r5, r6)
            r5 = 2
            r2.setSemanticAction(r5)
            r5 = 0
            r2.setShowsUserInterface(r5)
            androidx.core.app.NotificationCompat$Action r2 = r2.build()
            java.lang.String r9 = "_"
            if (r12 == 0) goto L_0x09b8
            if (r12 <= 0) goto L_0x099f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "tguser"
            r3.append(r5)
            r3.append(r12)
            r3.append(r9)
            r3.append(r11)
            java.lang.String r3 = r3.toString()
            goto L_0x09d7
        L_0x099f:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "tgchat"
            r3.append(r5)
            int r5 = -r12
            r3.append(r5)
            r3.append(r9)
            r3.append(r11)
            java.lang.String r3 = r3.toString()
            goto L_0x09d7
        L_0x09b8:
            long r5 = globalSecretChatId
            int r15 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r15 == 0) goto L_0x09d6
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "tgenc"
            r5.append(r6)
            r5.append(r3)
            r5.append(r9)
            r5.append(r11)
            java.lang.String r3 = r5.toString()
            goto L_0x09d7
        L_0x09d6:
            r3 = 0
        L_0x09d7:
            if (r3 == 0) goto L_0x09fb
            r1.setDismissalId(r3)
            androidx.core.app.NotificationCompat$WearableExtender r5 = new androidx.core.app.NotificationCompat$WearableExtender
            r5.<init>()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r15 = "summary_"
            r6.append(r15)
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            r5.setDismissalId(r3)
            r15 = r78
            r15.extend(r5)
            goto L_0x09fd
        L_0x09fb:
            r15 = r78
        L_0x09fd:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "tgaccount"
            r3.append(r5)
            r5 = r50
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            r1.setBridgeTag(r3)
            r3 = r62
            r6 = 0
            java.lang.Object r31 = r3.get(r6)
            r6 = r31
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r6 = r6.date
            long r5 = (long) r6
            long r5 = r5 * r60
            androidx.core.app.NotificationCompat$Builder r15 = new androidx.core.app.NotificationCompat$Builder
            r31 = r9
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext
            r15.<init>(r9)
            r9 = r63
            r15.setContentTitle(r9)
            r53 = r11
            r11 = 2131165759(0x7var_f, float:1.7945744E38)
            r15.setSmallIcon(r11)
            java.lang.String r11 = r67.toString()
            r15.setContentText(r11)
            r11 = 1
            r15.setAutoCancel(r11)
            int r3 = r3.size()
            r15.setNumber(r3)
            r3 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r15.setColor(r3)
            r3 = 0
            r15.setGroupSummary(r3)
            r15.setWhen(r5)
            r15.setShowWhen(r11)
            r15.setStyle(r10)
            r15.setContentIntent(r0)
            r15.extend(r1)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            r10 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r10 = r10 - r5
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            r15.setSortKey(r0)
            java.lang.String r0 = "msg"
            r15.setCategory(r0)
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r3 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r1, r3)
            java.lang.String r1 = "messageDate"
            r10 = r32
            r0.putExtra(r1, r10)
            java.lang.String r1 = "dialogId"
            r0.putExtra(r1, r13)
            int r1 = r8.currentAccount
            r0.putExtra(r4, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r3 = r49.intValue()
            r5 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r1, r3, r0, r5)
            r15.setDeleteIntent(r0)
            if (r16 == 0) goto L_0x0ab7
            java.lang.String r0 = r8.notificationGroup
            r15.setGroup(r0)
            r1 = 1
            r15.setGroupAlertBehavior(r1)
        L_0x0ab7:
            if (r55 == 0) goto L_0x0abe
            r1 = r55
            r15.addAction(r1)
        L_0x0abe:
            if (r17 != 0) goto L_0x0ac3
            r15.addAction(r2)
        L_0x0ac3:
            int r0 = r25.size()
            r5 = 1
            if (r0 != r5) goto L_0x0ad6
            boolean r0 = android.text.TextUtils.isEmpty(r80)
            if (r0 != 0) goto L_0x0ad6
            r11 = r80
            r15.setSubText(r11)
            goto L_0x0ad8
        L_0x0ad6:
            r11 = r80
        L_0x0ad8:
            if (r12 != 0) goto L_0x0add
            r15.setLocalOnly(r5)
        L_0x0add:
            if (r48 == 0) goto L_0x0ae4
            r1 = r48
            r15.setLargeIcon(r1)
        L_0x0ae4:
            r1 = 0
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1)
            if (r0 != 0) goto L_0x0bb8
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0bb8
            r1 = r59
            if (r1 == 0) goto L_0x0b82
            int r0 = r1.size()
            r2 = 0
        L_0x0af8:
            if (r2 >= r0) goto L_0x0b82
            java.lang.Object r3 = r1.get(r2)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r3 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r6 = r3.buttons
            int r6 = r6.size()
            r7 = 0
        L_0x0b07:
            if (r7 >= r6) goto L_0x0b70
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r5 = r3.buttons
            java.lang.Object r5 = r5.get(r7)
            org.telegram.tgnet.TLRPC$KeyboardButton r5 = (org.telegram.tgnet.TLRPC$KeyboardButton) r5
            r32 = r0
            boolean r0 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback
            if (r0 == 0) goto L_0x0b57
            android.content.Intent r0 = new android.content.Intent
            r35 = r1
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r38 = r3
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r3 = org.telegram.messenger.NotificationCallbackReceiver.class
            r0.<init>(r1, r3)
            int r1 = r8.currentAccount
            r0.putExtra(r4, r1)
            java.lang.String r1 = "did"
            r0.putExtra(r1, r13)
            byte[] r1 = r5.data
            if (r1 == 0) goto L_0x0b37
            java.lang.String r3 = "data"
            r0.putExtra(r3, r1)
        L_0x0b37:
            java.lang.String r1 = "mid"
            r3 = r58
            r0.putExtra(r1, r3)
            java.lang.String r1 = r5.text
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            r40 = r3
            int r3 = r8.lastButtonId
            r56 = r4
            int r4 = r3 + 1
            r8.lastButtonId = r4
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r5, r3, r0, r4)
            r5 = 0
            r15.addAction(r5, r1, r0)
            goto L_0x0b62
        L_0x0b57:
            r35 = r1
            r38 = r3
            r56 = r4
            r40 = r58
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 0
        L_0x0b62:
            int r7 = r7 + 1
            r0 = r32
            r1 = r35
            r3 = r38
            r58 = r40
            r4 = r56
            r5 = 1
            goto L_0x0b07
        L_0x0b70:
            r32 = r0
            r35 = r1
            r56 = r4
            r40 = r58
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 0
            int r2 = r2 + 1
            r4 = r56
            r5 = 1
            goto L_0x0af8
        L_0x0b82:
            r5 = 0
            if (r12 == 0) goto L_0x0bb8
            long r0 = (long) r12
            r2 = r30
            java.lang.Object r0 = r2.get(r0)
            r7 = r0
            androidx.core.app.Person r7 = (androidx.core.app.Person) r7
            r22 = r23
            r1 = r77
            r23 = r29
            r2 = r15
            r6 = r50
            r3 = r12
            r11 = 26
            r4 = r9
            r18 = 1
            r20 = 0
            r5 = r41
            r71 = r6
            r72 = r19
            r19 = 27
            r6 = r39
            r32 = r10
            r74 = r33
            r11 = r41
            r73 = r45
            r10 = r51
            r1.createNotificationShortcut(r2, r3, r4, r5, r6, r7)
            goto L_0x0bd0
        L_0x0bb8:
            r32 = r10
            r72 = r19
            r22 = r23
            r23 = r29
            r74 = r33
            r11 = r41
            r73 = r45
            r71 = r50
            r10 = r51
            r18 = 1
            r19 = 27
            r20 = 0
        L_0x0bd0:
            if (r39 != 0) goto L_0x0bf4
            if (r11 == 0) goto L_0x0bf4
            java.lang.String r0 = r11.phone
            if (r0 == 0) goto L_0x0bf4
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0bf4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "tel:+"
            r0.append(r1)
            java.lang.String r1 = r11.phone
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r15.addPerson(r0)
        L_0x0bf4:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 26
            if (r0 < r1) goto L_0x0CLASSNAME
            if (r16 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = OTHER_NOTIFICATIONS_CHANNEL
            r15.setChannelId(r0)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r0 = r24.getChannelId()
            r15.setChannelId(r0)
        L_0x0CLASSNAME:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            int r2 = r49.intValue()
            android.app.Notification r3 = r15.build()
            r0.<init>(r2, r3)
            r2 = r37
            r2.add(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.wearNotificationsIds
            r3 = r49
            r0.put(r13, r3)
            if (r12 == 0) goto L_0x0c9d
            if (r44 == 0) goto L_0x0c9d
            java.lang.String r0 = "reply"
            r4 = r44
            r3 = r47
            r4.put(r0, r3)     // Catch:{ JSONException -> 0x0c9d }
            java.lang.String r0 = "name"
            r4.put(r0, r9)     // Catch:{ JSONException -> 0x0c9d }
            r3 = r53
            r4.put(r10, r3)     // Catch:{ JSONException -> 0x0c9d }
            java.lang.String r0 = "max_date"
            r3 = r32
            r4.put(r0, r3)     // Catch:{ JSONException -> 0x0c9d }
            int r0 = java.lang.Math.abs(r12)     // Catch:{ JSONException -> 0x0c9d }
            r3 = r72
            r4.put(r3, r0)     // Catch:{ JSONException -> 0x0c9d }
            r3 = r73
            if (r3 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = "photo"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0c9d }
            r5.<init>()     // Catch:{ JSONException -> 0x0c9d }
            int r6 = r3.dc_id     // Catch:{ JSONException -> 0x0c9d }
            r5.append(r6)     // Catch:{ JSONException -> 0x0c9d }
            r6 = r31
            r5.append(r6)     // Catch:{ JSONException -> 0x0c9d }
            long r9 = r3.volume_id     // Catch:{ JSONException -> 0x0c9d }
            r5.append(r9)     // Catch:{ JSONException -> 0x0c9d }
            r5.append(r6)     // Catch:{ JSONException -> 0x0c9d }
            long r6 = r3.secret     // Catch:{ JSONException -> 0x0c9d }
            r5.append(r6)     // Catch:{ JSONException -> 0x0c9d }
            java.lang.String r3 = r5.toString()     // Catch:{ JSONException -> 0x0c9d }
            r4.put(r0, r3)     // Catch:{ JSONException -> 0x0c9d }
        L_0x0CLASSNAME:
            if (r26 == 0) goto L_0x0c7b
            java.lang.String r0 = "msgs"
            r3 = r26
            r4.put(r0, r3)     // Catch:{ JSONException -> 0x0c9d }
        L_0x0c7b:
            java.lang.String r0 = "type"
            if (r12 <= 0) goto L_0x0CLASSNAME
            java.lang.String r3 = "user"
            r4.put(r0, r3)     // Catch:{ JSONException -> 0x0c9d }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            if (r12 >= 0) goto L_0x0CLASSNAME
            if (r27 != 0) goto L_0x0CLASSNAME
            if (r34 == 0) goto L_0x0c8c
            goto L_0x0CLASSNAME
        L_0x0c8c:
            java.lang.String r3 = "group"
            r4.put(r0, r3)     // Catch:{ JSONException -> 0x0c9d }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r3 = "channel"
            r4.put(r0, r3)     // Catch:{ JSONException -> 0x0c9d }
        L_0x0CLASSNAME:
            r15 = r74
            r15.put(r4)     // Catch:{ JSONException -> 0x0c9f }
            goto L_0x0c9f
        L_0x0c9d:
            r15 = r74
        L_0x0c9f:
            int r0 = r22 + 1
            r1 = r0
            r14 = r2
            r7 = r15
            r2 = r23
            r9 = r24
            r10 = r25
            r11 = r28
            r13 = r36
            r3 = r71
            r4 = 26
            r5 = 1
            r12 = 0
            goto L_0x00d9
        L_0x0cb6:
            r71 = r3
            r3 = r6
            r15 = r7
            r24 = r9
            r36 = r13
            r2 = r14
            r20 = 0
            if (r16 == 0) goto L_0x0ce7
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0cdd
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r8.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0cdd:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r8.notificationId
            r4 = r24
            r0.notify(r1, r4)
            goto L_0x0cf6
        L_0x0ce7:
            java.util.HashSet<java.lang.Long> r0 = r8.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0cf6
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r8.notificationId
            r0.cancel(r1)
        L_0x0cf6:
            int r0 = r2.size()
            r1 = 0
        L_0x0cfb:
            if (r1 >= r0) goto L_0x0d09
            java.lang.Object r4 = r2.get(r1)
            org.telegram.messenger.NotificationsController$1NotificationHolder r4 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r4
            r4.call()
            int r1 = r1 + 1
            goto L_0x0cfb
        L_0x0d09:
            r12 = 0
        L_0x0d0a:
            int r0 = r36.size()
            if (r12 >= r0) goto L_0x0d4f
            r1 = r36
            long r4 = r1.keyAt(r12)
            java.util.HashSet<java.lang.Long> r0 = r8.openedInBubbleDialogs
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            boolean r0 = r0.contains(r2)
            if (r0 == 0) goto L_0x0d23
            goto L_0x0d4a
        L_0x0d23:
            java.lang.Object r0 = r1.valueAt(r12)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0d41
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "cancel notification id "
            r2.append(r4)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0d41:
            androidx.core.app.NotificationManagerCompat r2 = notificationManager
            int r0 = r0.intValue()
            r2.cancel(r0)
        L_0x0d4a:
            int r12 = r12 + 1
            r36 = r1
            goto L_0x0d0a
        L_0x0d4f:
            if (r15 == 0) goto L_0x0d6f
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0d6f }
            r0.<init>()     // Catch:{ Exception -> 0x0d6f }
            r1 = r71
            r0.put(r3, r1)     // Catch:{ Exception -> 0x0d6f }
            java.lang.String r1 = "n"
            r0.put(r1, r15)     // Catch:{ Exception -> 0x0d6f }
            java.lang.String r1 = "/notify"
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0d6f }
            byte[] r0 = r0.getBytes()     // Catch:{ Exception -> 0x0d6f }
            java.lang.String r2 = "remote_notifications"
            org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r1, r0, r2)     // Catch:{ Exception -> 0x0d6f }
        L_0x0d6f:
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
