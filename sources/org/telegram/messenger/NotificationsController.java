package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
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
import android.provider.Settings;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Boolean groupsCreated;
    private boolean inChatSoundEnabled;
    private int lastBadgeCount = -1;
    private int lastButtonId = 5000;
    public long lastNotificationChannelCreateTime;
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

    public static String getGlobalNotificationsKey(int i) {
        return i == 0 ? "EnableGroup2" : i == 1 ? "EnableAll2" : "EnableChannel2";
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$39(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$40(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
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
                NotificationChannel notificationChannel2 = new NotificationChannel(OTHER_NOTIFICATIONS_CHANNEL, "Internal notifications", 3);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$cleanup$1 */
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
                systemNotificationManager.deleteNotificationChannelGroup("channels" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("groups" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("private" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("other" + this.currentAccount);
                String str = this.currentAccount + "channel";
                List<NotificationChannel> notificationChannels = systemNotificationManager.getNotificationChannels();
                int size = notificationChannels.size();
                for (int i = 0; i < size; i++) {
                    String id = notificationChannels.get(i).getId();
                    if (id.startsWith(str)) {
                        systemNotificationManager.deleteNotificationChannel(id);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel cleanup " + id);
                        }
                    }
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    public void setInChatSoundEnabled(boolean z) {
        this.inChatSoundEnabled = z;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setOpenedDialogId$2 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$setOpenedInBubble$3 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$setLastOnlineFromOtherDevice$4 */
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
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (tLRPC$Message.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$forceShowPopupForReply$6 */
    public /* synthetic */ void lambda$forceShowPopupForReply$6$NotificationsController() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (tLRPC$Message.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$removeDeletedMessagesFromNotifications$9 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$NotificationsController(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$removeDeletedHisoryFromNotifications$12 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$NotificationsController(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$11 */
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00bb, code lost:
        r6 = false;
     */
    /* renamed from: lambda$processReadMessages$14 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processReadMessages$14$NotificationsController(org.telegram.messenger.support.SparseLongArray r19, java.util.ArrayList r20, long r21, int r23, int r24, boolean r25) {
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
            org.telegram.tgnet.TLRPC$Peer r14 = r14.peer_id
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
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
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
            org.telegram.messenger.-$$Lambda$NotificationsController$afI9WRnN1shYvqBgMroNL8RnLv8 r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$afI9WRnN1shYvqBgMroNL8RnLv8
            r1.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x0105:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processReadMessages$14$NotificationsController(org.telegram.messenger.support.SparseLongArray, java.util.ArrayList, long, int, int, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$13 */
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
            boolean r6 = r4.isSupergroup()
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

    public void processEditedMessages(LongSparseArray<ArrayList<MessageObject>> longSparseArray) {
        if (longSparseArray.size() != 0) {
            new ArrayList(0);
            notificationsQueue.postRunnable(new Runnable(longSparseArray) {
                public final /* synthetic */ LongSparseArray f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$processEditedMessages$15$NotificationsController(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processEditedMessages$15 */
    public /* synthetic */ void lambda$processEditedMessages$15$NotificationsController(LongSparseArray longSparseArray) {
        int size = longSparseArray.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            if (this.pushDialogs.indexOfKey(longSparseArray.keyAt(i)) >= 0) {
                ArrayList arrayList = (ArrayList) longSparseArray.valueAt(i);
                int size2 = arrayList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    MessageObject messageObject = (MessageObject) arrayList.get(i2);
                    long id = (long) messageObject.getId();
                    int i3 = messageObject.messageOwner.peer_id.channel_id;
                    if (i3 != 0) {
                        id |= ((long) i3) << 32;
                    }
                    MessageObject messageObject2 = this.pushMessagesDict.get(id);
                    if (messageObject2 != null) {
                        this.pushMessagesDict.put(id, messageObject);
                        int indexOf = this.pushMessages.indexOf(messageObject2);
                        if (indexOf >= 0) {
                            this.pushMessages.set(indexOf, messageObject);
                        }
                        int indexOf2 = this.delayedPushMessages.indexOf(messageObject2);
                        if (indexOf2 >= 0) {
                            this.delayedPushMessages.set(indexOf2, messageObject);
                        }
                        z = true;
                    }
                }
            }
        }
        if (z) {
            showOrUpdateNotification(false);
        }
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
                    NotificationsController.this.lambda$processNewMessages$18$NotificationsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        } else if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0045, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) == false) goto L_0x004d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00a0  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x010d  */
    /* renamed from: lambda$processNewMessages$18 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processNewMessages$18$NotificationsController(java.util.ArrayList r32, java.util.ArrayList r33, boolean r34, boolean r35, java.util.concurrent.CountDownLatch r36) {
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
            if (r15 >= r1) goto L_0x0204
            java.lang.Object r1 = r9.get(r15)
            r7 = r1
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            if (r1 == 0) goto L_0x004d
            boolean r1 = r7.isImportedForward()
            if (r1 != 0) goto L_0x0047
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            boolean r4 = r1.silent
            if (r4 == 0) goto L_0x004d
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r4 != 0) goto L_0x0047
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r1 == 0) goto L_0x004d
        L_0x0047:
            r22 = r13
            r21 = r15
            goto L_0x0139
        L_0x004d:
            int r1 = r7.getId()
            long r4 = (long) r1
            boolean r1 = r7.isFcmMessage()
            r19 = 0
            if (r1 == 0) goto L_0x0063
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            r21 = r15
            long r14 = r1.random_id
            r22 = r13
            goto L_0x0069
        L_0x0063:
            r21 = r15
            r22 = r13
            r14 = r19
        L_0x0069:
            long r12 = r7.getDialogId()
            int r6 = (int) r12
            boolean r1 = r7.isFcmMessage()
            if (r1 == 0) goto L_0x0079
            boolean r1 = r7.localChannel
        L_0x0076:
            r24 = r1
            goto L_0x0098
        L_0x0079:
            if (r6 >= 0) goto L_0x0096
            org.telegram.messenger.MessagesController r1 = r31.getMessagesController()
            int r2 = -r6
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x0094
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0094
            r1 = 1
            goto L_0x0076
        L_0x0094:
            r1 = 0
            goto L_0x0076
        L_0x0096:
            r24 = 0
        L_0x0098:
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00a6
            long r1 = (long) r1
            r25 = 32
            long r1 = r1 << r25
            long r4 = r4 | r1
        L_0x00a6:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.pushMessagesDict
            java.lang.Object r1 = r1.get(r4)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 != 0) goto L_0x00ce
            org.telegram.tgnet.TLRPC$Message r2 = r7.messageOwner
            r26 = r4
            long r3 = r2.random_id
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 == 0) goto L_0x00d0
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.fcmRandomMessagesDict
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 == 0) goto L_0x00d0
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r8.fcmRandomMessagesDict
            org.telegram.tgnet.TLRPC$Message r3 = r7.messageOwner
            long r3 = r3.random_id
            r2.remove(r3)
            goto L_0x00d0
        L_0x00ce:
            r26 = r4
        L_0x00d0:
            if (r1 == 0) goto L_0x010d
            boolean r2 = r1.isFcmMessage()
            if (r2 == 0) goto L_0x0139
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r8.pushMessagesDict
            r4 = r26
            r2.put(r4, r7)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r8.pushMessages
            int r1 = r2.indexOf(r1)
            if (r1 < 0) goto L_0x00fc
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
            goto L_0x00fd
        L_0x00fc:
            r12 = r7
        L_0x00fd:
            if (r34 == 0) goto L_0x0139
            boolean r1 = r12.localEdit
            if (r1 == 0) goto L_0x010a
            org.telegram.messenger.MessagesStorage r2 = r31.getMessagesStorage()
            r2.putPushMessage(r12)
        L_0x010a:
            r17 = r1
            goto L_0x0139
        L_0x010d:
            r4 = r26
            if (r17 == 0) goto L_0x0112
            goto L_0x0139
        L_0x0112:
            if (r34 == 0) goto L_0x011b
            org.telegram.messenger.MessagesStorage r1 = r31.getMessagesStorage()
            r1.putPushMessage(r7)
        L_0x011b:
            long r1 = r8.opened_dialog_id
            int r3 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x012b
            boolean r1 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r1 == 0) goto L_0x012b
            if (r34 != 0) goto L_0x0139
            r31.playInChatSound()
            goto L_0x0139
        L_0x012b:
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            boolean r2 = r1.mentioned
            if (r2 == 0) goto L_0x0144
            if (r22 != 0) goto L_0x013d
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r1 == 0) goto L_0x013d
        L_0x0139:
            r30 = r10
            goto L_0x01f9
        L_0x013d:
            int r1 = r7.getFromChatId()
            long r1 = (long) r1
            r2 = r1
            goto L_0x0145
        L_0x0144:
            r2 = r12
        L_0x0145:
            boolean r1 = r8.isPersonalMessage(r7)
            if (r1 == 0) goto L_0x0153
            int r1 = r8.personal_count
            r16 = 1
            int r1 = r1 + 1
            r8.personal_count = r1
        L_0x0153:
            int r1 = r10.indexOfKey(r2)
            if (r1 < 0) goto L_0x0166
            java.lang.Object r1 = r10.valueAt(r1)
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r26 = r4
            goto L_0x0185
        L_0x0166:
            int r1 = r8.getNotifyOverride(r11, r2)
            r26 = r4
            r4 = -1
            if (r1 != r4) goto L_0x0178
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r24)
            boolean r1 = r8.isGlobalNotificationsEnabled(r2, r1)
            goto L_0x017e
        L_0x0178:
            r4 = 2
            if (r1 == r4) goto L_0x017d
            r1 = 1
            goto L_0x017e
        L_0x017d:
            r1 = 0
        L_0x017e:
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r1)
            r10.put(r2, r4)
        L_0x0185:
            if (r1 == 0) goto L_0x01f5
            if (r34 != 0) goto L_0x01a2
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
            goto L_0x01ab
        L_0x01a2:
            r28 = r2
            r30 = r10
            r23 = r12
            r9 = r26
            r12 = r7
        L_0x01ab:
            if (r18 != 0) goto L_0x01b3
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner
            boolean r1 = r1.from_scheduled
            r18 = r1
        L_0x01b3:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r8.delayedPushMessages
            r1.add(r12)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r8.pushMessages
            r2 = 0
            r1.add(r2, r12)
            int r1 = (r9 > r19 ? 1 : (r9 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x01c8
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.pushMessagesDict
            r1.put(r9, r12)
            goto L_0x01d1
        L_0x01c8:
            int r1 = (r14 > r19 ? 1 : (r14 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x01d1
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.fcmRandomMessagesDict
            r1.put(r14, r12)
        L_0x01d1:
            int r1 = (r23 > r28 ? 1 : (r23 == r28 ? 0 : -1))
            if (r1 == 0) goto L_0x01f7
            android.util.LongSparseArray<java.lang.Integer> r1 = r8.pushDialogsOverrideMention
            r2 = r23
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            android.util.LongSparseArray<java.lang.Integer> r4 = r8.pushDialogsOverrideMention
            if (r1 != 0) goto L_0x01e6
            r16 = 1
            goto L_0x01ed
        L_0x01e6:
            int r1 = r1.intValue()
            r5 = 1
            int r16 = r1 + 1
        L_0x01ed:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r16)
            r4.put(r2, r1)
            goto L_0x01f7
        L_0x01f5:
            r30 = r10
        L_0x01f7:
            r16 = 1
        L_0x01f9:
            int r15 = r21 + 1
            r9 = r32
            r13 = r22
            r10 = r30
            r12 = 1
            goto L_0x0020
        L_0x0204:
            if (r16 == 0) goto L_0x020a
            r1 = r35
            r8.notifyCheck = r1
        L_0x020a:
            boolean r1 = r33.isEmpty()
            if (r1 != 0) goto L_0x0224
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x0224
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 != 0) goto L_0x0224
            org.telegram.messenger.-$$Lambda$NotificationsController$InuBhwQAikzB2YQhvIMFASK0kGU r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$InuBhwQAikzB2YQhvIMFASK0kGU
            r2 = r33
            r1.<init>(r2, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x0224:
            if (r34 != 0) goto L_0x0228
            if (r18 == 0) goto L_0x02e3
        L_0x0228:
            if (r17 == 0) goto L_0x0236
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.delayedPushMessages
            r0.clear()
            boolean r0 = r8.notifyCheck
            r8.showOrUpdateNotification(r0)
            goto L_0x02e3
        L_0x0236:
            if (r16 == 0) goto L_0x02e3
            r0 = r32
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r1 = r0.getDialogId()
            boolean r3 = r0.isFcmMessage()
            if (r3 == 0) goto L_0x0252
            boolean r0 = r0.localChannel
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            goto L_0x0253
        L_0x0252:
            r0 = 0
        L_0x0253:
            int r3 = r8.total_unread_count
            int r4 = r8.getNotifyOverride(r11, r1)
            r5 = -1
            if (r4 != r5) goto L_0x0261
            boolean r0 = r8.isGlobalNotificationsEnabled(r1, r0)
            goto L_0x026b
        L_0x0261:
            r0 = 2
            if (r4 == r0) goto L_0x0267
            r16 = 1
            goto L_0x0269
        L_0x0267:
            r16 = 0
        L_0x0269:
            r0 = r16
        L_0x026b:
            android.util.LongSparseArray<java.lang.Integer> r4 = r8.pushDialogs
            java.lang.Object r4 = r4.get(r1)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 == 0) goto L_0x027e
            int r5 = r4.intValue()
            r16 = 1
            int r5 = r5 + 1
            goto L_0x0281
        L_0x027e:
            r16 = 1
            r5 = 1
        L_0x0281:
            boolean r6 = r8.notifyCheck
            if (r6 == 0) goto L_0x029d
            if (r0 != 0) goto L_0x029d
            android.util.LongSparseArray<java.lang.Integer> r6 = r8.pushDialogsOverrideMention
            java.lang.Object r6 = r6.get(r1)
            java.lang.Integer r6 = (java.lang.Integer) r6
            if (r6 == 0) goto L_0x029d
            int r7 = r6.intValue()
            if (r7 == 0) goto L_0x029d
            int r5 = r6.intValue()
            r12 = 1
            goto L_0x029e
        L_0x029d:
            r12 = r0
        L_0x029e:
            if (r12 == 0) goto L_0x02b9
            if (r4 == 0) goto L_0x02ab
            int r0 = r8.total_unread_count
            int r4 = r4.intValue()
            int r0 = r0 - r4
            r8.total_unread_count = r0
        L_0x02ab:
            int r0 = r8.total_unread_count
            int r0 = r0 + r5
            r8.total_unread_count = r0
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r0.put(r1, r4)
        L_0x02b9:
            int r0 = r8.total_unread_count
            if (r3 == r0) goto L_0x02d5
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.delayedPushMessages
            r0.clear()
            boolean r0 = r8.notifyCheck
            r8.showOrUpdateNotification(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            int r0 = r0.size()
            org.telegram.messenger.-$$Lambda$NotificationsController$6wLjKsushNgyWMV901nmzWOaP1g r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$6wLjKsushNgyWMV901nmzWOaP1g
            r1.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x02d5:
            r0 = 0
            r8.notifyCheck = r0
            boolean r0 = r8.showBadgeNumber
            if (r0 == 0) goto L_0x02e3
            int r0 = r31.getTotalAllUnreadCount()
            r8.setBadge(r0)
        L_0x02e3:
            if (r36 == 0) goto L_0x02e8
            r36.countDown()
        L_0x02e8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processNewMessages$18$NotificationsController(java.util.ArrayList, java.util.ArrayList, boolean, boolean, java.util.concurrent.CountDownLatch):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$16 */
    public /* synthetic */ void lambda$null$16$NotificationsController(ArrayList arrayList, int i) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$17 */
    public /* synthetic */ void lambda$null$17$NotificationsController(int i) {
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
                NotificationsController.this.lambda$processDialogsUpdateRead$21$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processDialogsUpdateRead$21 */
    public /* synthetic */ void lambda$processDialogsUpdateRead$21$NotificationsController(LongSparseArray longSparseArray, ArrayList arrayList) {
        boolean z;
        Integer num;
        TLRPC$Chat chat;
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
            Integer num2 = this.pushDialogs.get(keyAt);
            Integer num3 = (Integer) longSparseArray2.get(keyAt);
            int i3 = (int) keyAt;
            if (i3 < 0 && ((chat = getMessagesController().getChat(Integer.valueOf(-i3))) == null || chat.min || ChatObject.isNotInChat(chat))) {
                num3 = 0;
            }
            int notifyOverride = getNotifyOverride(notificationsSettings, keyAt);
            boolean isGlobalNotificationsEnabled = notifyOverride == -1 ? isGlobalNotificationsEnabled(keyAt) : notifyOverride != 2;
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
                int i4 = 0;
                while (i4 < this.pushMessages.size()) {
                    MessageObject messageObject = this.pushMessages.get(i4);
                    if (!messageObject.messageOwner.from_scheduled && messageObject.getDialogId() == keyAt) {
                        if (isPersonalMessage(messageObject)) {
                            this.personal_count--;
                        }
                        this.pushMessages.remove(i4);
                        i4--;
                        this.delayedPushMessages.remove(messageObject);
                        long id = (long) messageObject.getId();
                        int i5 = messageObject.messageOwner.peer_id.channel_id;
                        if (i5 != 0) {
                            id |= ((long) i5) << 32;
                        }
                        this.pushMessagesDict.remove(id);
                        arrayList2.add(messageObject);
                    }
                    i4++;
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
                    NotificationsController.this.lambda$null$19$NotificationsController(this.f$1);
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
                    NotificationsController.this.lambda$null$20$NotificationsController(this.f$1);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$19 */
    public /* synthetic */ void lambda$null$19$NotificationsController(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$20 */
    public /* synthetic */ void lambda$null$20$NotificationsController(int i) {
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
                NotificationsController.this.lambda$processLoadedUnreadMessages$23$NotificationsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0056, code lost:
        if ((r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) == false) goto L_0x0059;
     */
    /* renamed from: lambda$processLoadedUnreadMessages$23 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedUnreadMessages$23$NotificationsController(java.util.ArrayList r21, android.util.LongSparseArray r22, java.util.ArrayList r23) {
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
            if (r1 == 0) goto L_0x0106
            r11 = 0
        L_0x002f:
            int r12 = r21.size()
            if (r11 >= r12) goto L_0x0106
            java.lang.Object r12 = r1.get(r11)
            org.telegram.tgnet.TLRPC$Message r12 = (org.telegram.tgnet.TLRPC$Message) r12
            if (r12 == 0) goto L_0x0059
            org.telegram.tgnet.TLRPC$MessageFwdHeader r13 = r12.fwd_from
            if (r13 == 0) goto L_0x004a
            boolean r13 = r13.imported
            if (r13 != 0) goto L_0x0046
            goto L_0x004a
        L_0x0046:
            r15 = r5
            r12 = r11
            goto L_0x00fe
        L_0x004a:
            boolean r13 = r12.silent
            if (r13 == 0) goto L_0x0059
            org.telegram.tgnet.TLRPC$MessageAction r13 = r12.action
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r14 != 0) goto L_0x0046
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r13 == 0) goto L_0x0059
            goto L_0x0046
        L_0x0059:
            int r13 = r12.id
            long r13 = (long) r13
            org.telegram.tgnet.TLRPC$Peer r15 = r12.peer_id
            int r15 = r15.channel_id
            if (r15 == 0) goto L_0x0065
            long r8 = (long) r15
            long r8 = r8 << r7
            long r13 = r13 | r8
        L_0x0065:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r8 = r0.pushMessagesDict
            int r8 = r8.indexOfKey(r13)
            if (r8 < 0) goto L_0x006e
            goto L_0x0046
        L_0x006e:
            org.telegram.messenger.MessageObject r8 = new org.telegram.messenger.MessageObject
            int r9 = r0.currentAccount
            r8.<init>(r9, r12, r4, r4)
            boolean r9 = r0.isPersonalMessage(r8)
            if (r9 == 0) goto L_0x0080
            int r9 = r0.personal_count
            int r9 = r9 + r10
            r0.personal_count = r9
        L_0x0080:
            r12 = r11
            long r10 = r8.getDialogId()
            org.telegram.tgnet.TLRPC$Message r15 = r8.messageOwner
            boolean r15 = r15.mentioned
            if (r15 == 0) goto L_0x0093
            int r15 = r8.getFromChatId()
            r17 = r10
            long r9 = (long) r15
            goto L_0x0097
        L_0x0093:
            r17 = r10
            r9 = r17
        L_0x0097:
            int r11 = r6.indexOfKey(r9)
            if (r11 < 0) goto L_0x00a8
            java.lang.Object r11 = r6.valueAt(r11)
            java.lang.Boolean r11 = (java.lang.Boolean) r11
            boolean r11 = r11.booleanValue()
            goto L_0x00c1
        L_0x00a8:
            int r11 = r0.getNotifyOverride(r5, r9)
            r15 = -1
            if (r11 != r15) goto L_0x00b4
            boolean r11 = r0.isGlobalNotificationsEnabled((long) r9)
            goto L_0x00ba
        L_0x00b4:
            r15 = 2
            if (r11 == r15) goto L_0x00b9
            r11 = 1
            goto L_0x00ba
        L_0x00b9:
            r11 = 0
        L_0x00ba:
            java.lang.Boolean r15 = java.lang.Boolean.valueOf(r11)
            r6.put(r9, r15)
        L_0x00c1:
            r15 = r5
            if (r11 == 0) goto L_0x00fe
            long r4 = r0.opened_dialog_id
            int r19 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r19 != 0) goto L_0x00cf
            boolean r4 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r4 == 0) goto L_0x00cf
            goto L_0x00fe
        L_0x00cf:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r4 = r0.pushMessagesDict
            r4.put(r13, r8)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.pushMessages
            r5 = 0
            r4.add(r5, r8)
            int r4 = (r17 > r9 ? 1 : (r17 == r9 ? 0 : -1))
            if (r4 == 0) goto L_0x00fe
            android.util.LongSparseArray<java.lang.Integer> r4 = r0.pushDialogsOverrideMention
            r13 = r17
            java.lang.Object r4 = r4.get(r13)
            java.lang.Integer r4 = (java.lang.Integer) r4
            android.util.LongSparseArray<java.lang.Integer> r5 = r0.pushDialogsOverrideMention
            if (r4 != 0) goto L_0x00ef
            r16 = 1
            goto L_0x00f7
        L_0x00ef:
            int r4 = r4.intValue()
            r8 = 1
            int r4 = r4 + r8
            r16 = r4
        L_0x00f7:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r16)
            r5.put(r13, r4)
        L_0x00fe:
            int r4 = r12 + 1
            r11 = r4
            r5 = r15
            r4 = 0
            r10 = 1
            goto L_0x002f
        L_0x0106:
            r15 = r5
            r5 = 0
        L_0x0108:
            int r1 = r22.size()
            if (r5 >= r1) goto L_0x015e
            long r12 = r2.keyAt(r5)
            int r1 = r6.indexOfKey(r12)
            if (r1 < 0) goto L_0x0125
            java.lang.Object r1 = r6.valueAt(r1)
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r4 = r1
            r1 = r15
            goto L_0x013f
        L_0x0125:
            r1 = r15
            int r4 = r0.getNotifyOverride(r1, r12)
            r8 = -1
            if (r4 != r8) goto L_0x0132
            boolean r4 = r0.isGlobalNotificationsEnabled((long) r12)
            goto L_0x0138
        L_0x0132:
            r8 = 2
            if (r4 == r8) goto L_0x0137
            r4 = 1
            goto L_0x0138
        L_0x0137:
            r4 = 0
        L_0x0138:
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r4)
            r6.put(r12, r8)
        L_0x013f:
            if (r4 != 0) goto L_0x0142
            goto L_0x015a
        L_0x0142:
            java.lang.Object r4 = r2.valueAt(r5)
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            android.util.LongSparseArray<java.lang.Integer> r8 = r0.pushDialogs
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            r8.put(r12, r10)
            int r8 = r0.total_unread_count
            int r8 = r8 + r4
            r0.total_unread_count = r8
        L_0x015a:
            int r5 = r5 + 1
            r15 = r1
            goto L_0x0108
        L_0x015e:
            r1 = r15
            if (r3 == 0) goto L_0x0255
            r5 = 0
        L_0x0162:
            int r2 = r23.size()
            if (r5 >= r2) goto L_0x0255
            java.lang.Object r2 = r3.get(r5)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r4 = r2.getId()
            long r12 = (long) r4
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id
            int r4 = r4.channel_id
            if (r4 == 0) goto L_0x017e
            long r14 = (long) r4
            long r14 = r14 << r7
            long r12 = r12 | r14
        L_0x017e:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r4 = r0.pushMessagesDict
            int r4 = r4.indexOfKey(r12)
            if (r4 < 0) goto L_0x018b
        L_0x0186:
            r4 = 0
            r16 = 1
            goto L_0x024d
        L_0x018b:
            boolean r4 = r0.isPersonalMessage(r2)
            if (r4 == 0) goto L_0x0197
            int r4 = r0.personal_count
            r8 = 1
            int r4 = r4 + r8
            r0.personal_count = r4
        L_0x0197:
            long r14 = r2.getDialogId()
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
            long r7 = r4.random_id
            boolean r4 = r4.mentioned
            if (r4 == 0) goto L_0x01a9
            int r4 = r2.getFromChatId()
            long r9 = (long) r4
            goto L_0x01aa
        L_0x01a9:
            r9 = r14
        L_0x01aa:
            int r4 = r6.indexOfKey(r9)
            if (r4 < 0) goto L_0x01bb
            java.lang.Object r4 = r6.valueAt(r4)
            java.lang.Boolean r4 = (java.lang.Boolean) r4
            boolean r4 = r4.booleanValue()
            goto L_0x01d4
        L_0x01bb:
            int r4 = r0.getNotifyOverride(r1, r9)
            r11 = -1
            if (r4 != r11) goto L_0x01c7
            boolean r4 = r0.isGlobalNotificationsEnabled((long) r9)
            goto L_0x01cd
        L_0x01c7:
            r11 = 2
            if (r4 == r11) goto L_0x01cc
            r4 = 1
            goto L_0x01cd
        L_0x01cc:
            r4 = 0
        L_0x01cd:
            java.lang.Boolean r11 = java.lang.Boolean.valueOf(r4)
            r6.put(r9, r11)
        L_0x01d4:
            if (r4 == 0) goto L_0x0186
            long r3 = r0.opened_dialog_id
            int r11 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r11 != 0) goto L_0x01e1
            boolean r3 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r3 == 0) goto L_0x01e1
            goto L_0x0186
        L_0x01e1:
            r3 = 0
            int r11 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r11 == 0) goto L_0x01ed
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r0.pushMessagesDict
            r3.put(r12, r2)
            goto L_0x01f6
        L_0x01ed:
            int r11 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r11 == 0) goto L_0x01f6
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r0.fcmRandomMessagesDict
            r3.put(r7, r2)
        L_0x01f6:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.pushMessages
            r4 = 0
            r3.add(r4, r2)
            int r2 = (r14 > r9 ? 1 : (r14 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0220
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogsOverrideMention
            java.lang.Object r2 = r2.get(r14)
            java.lang.Integer r2 = (java.lang.Integer) r2
            android.util.LongSparseArray<java.lang.Integer> r3 = r0.pushDialogsOverrideMention
            if (r2 != 0) goto L_0x0210
            r2 = 1
            r16 = 1
            goto L_0x0218
        L_0x0210:
            int r2 = r2.intValue()
            r16 = 1
            int r2 = r2 + 1
        L_0x0218:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r3.put(r14, r2)
            goto L_0x0222
        L_0x0220:
            r16 = 1
        L_0x0222:
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogs
            java.lang.Object r2 = r2.get(r9)
            java.lang.Integer r2 = (java.lang.Integer) r2
            if (r2 == 0) goto L_0x0233
            int r3 = r2.intValue()
            int r3 = r3 + 1
            goto L_0x0234
        L_0x0233:
            r3 = 1
        L_0x0234:
            if (r2 == 0) goto L_0x023f
            int r7 = r0.total_unread_count
            int r2 = r2.intValue()
            int r7 = r7 - r2
            r0.total_unread_count = r7
        L_0x023f:
            int r2 = r0.total_unread_count
            int r2 = r2 + r3
            r0.total_unread_count = r2
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogs
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2.put(r9, r3)
        L_0x024d:
            int r5 = r5 + 1
            r3 = r23
            r7 = 32
            goto L_0x0162
        L_0x0255:
            r4 = 0
            r16 = 1
            android.util.LongSparseArray<java.lang.Integer> r1 = r0.pushDialogs
            int r1 = r1.size()
            org.telegram.messenger.-$$Lambda$NotificationsController$W3EyHOoU-PJy5kZh4eta-QmQIJE r2 = new org.telegram.messenger.-$$Lambda$NotificationsController$W3EyHOoU-PJy5kZh4eta-QmQIJE
            r2.<init>(r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            long r1 = android.os.SystemClock.elapsedRealtime()
            r5 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 / r5
            r5 = 60
            int r3 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r3 >= 0) goto L_0x0274
            r4 = 1
        L_0x0274:
            r0.showOrUpdateNotification(r4)
            boolean r1 = r0.showBadgeNumber
            if (r1 == 0) goto L_0x0282
            int r1 = r20.getTotalAllUnreadCount()
            r0.setBadge(r1)
        L_0x0282:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processLoadedUnreadMessages$23$NotificationsController(java.util.ArrayList, android.util.LongSparseArray, java.util.ArrayList):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$22 */
    public /* synthetic */ void lambda$null$22$NotificationsController(int i) {
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
                                    int i5 = (int) tLRPC$Dialog.id;
                                    if (i5 >= 0 || !ChatObject.isNotInChat(getMessagesController().getChat(Integer.valueOf(-i5)))) {
                                        int i6 = tLRPC$Dialog.unread_count;
                                        if (i6 != 0) {
                                            i2 += i6;
                                        }
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
                            for (int i7 = 0; i7 < size2; i7++) {
                                TLRPC$Dialog tLRPC$Dialog2 = MessagesController.getInstance(i3).allDialogs.get(i7);
                                int i8 = (int) tLRPC$Dialog2.id;
                                if (i8 >= 0 || !ChatObject.isNotInChat(getMessagesController().getChat(Integer.valueOf(-i8)))) {
                                    if (tLRPC$Dialog2.unread_count != 0) {
                                        i2++;
                                    }
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateBadge$24 */
    public /* synthetic */ void lambda$updateBadge$24$NotificationsController() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$updateBadge$24$NotificationsController();
            }
        });
    }

    private void setBadge(int i) {
        if (this.lastBadgeCount != i) {
            this.lastBadgeCount = i;
            NotificationBadge.applyCount(i);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01e7, code lost:
        if (r8.getBoolean("EnablePreviewAll", true) == false) goto L_0x01eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x01f5, code lost:
        if (r8.getBoolean("EnablePreviewGroup", r4) != false) goto L_0x0201;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x01ff, code lost:
        if (r8.getBoolean("EnablePreviewChannel", r4) != false) goto L_0x0201;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x0201, code lost:
        r2 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0211, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0df5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0213, code lost:
        r20[0] = null;
        r3 = r2.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0219, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached) == false) goto L_0x0222;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0221, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0224, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L_0x0de6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0228, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L_0x022c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x022e, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L_0x023f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x023e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0242, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L_0x02a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x0244, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", NUM, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(((long) r0.messageOwner.date) * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(((long) r0.messageOwner.date) * 1000));
        r0 = r0.messageOwner.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x02a0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", NUM, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x02a3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L_0x0ddf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x02a7, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L_0x02ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x02ad, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L_0x02c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x02b1, code lost:
        if (r3.video == false) goto L_0x02bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x02bc, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageVideoIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x02c6, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02c9, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x03d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x02cb, code lost:
        r2 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02cd, code lost:
        if (r2 != 0) goto L_0x02e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02d6, code lost:
        if (r3.users.size() != 1) goto L_0x02e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x02d8, code lost:
        r2 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x02e8, code lost:
        if (r2 == 0) goto L_0x0379;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x02f0, code lost:
        if (r0.messageOwner.peer_id.channel_id == 0) goto L_0x030a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x02f4, code lost:
        if (r6.megagroup != false) goto L_0x030a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0309, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x030c, code lost:
        if (r2 != r10) goto L_0x0320;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x031f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0320, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x032c, code lost:
        if (r0 != null) goto L_0x032f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x032e, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0331, code lost:
        if (r1 != r0.id) goto L_0x035f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0335, code lost:
        if (r6.megagroup == false) goto L_0x034b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x034a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x035e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0378, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r11, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0379, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0389, code lost:
        if (r2 >= r0.messageOwner.action.users.size()) goto L_0x03b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x038b, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x039f, code lost:
        if (r3 == null) goto L_0x03b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x03a1, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x03a9, code lost:
        if (r1.length() == 0) goto L_0x03b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x03ab, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x03b0, code lost:
        r1.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x03b3, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x03cf, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r11, r6.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x03d3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L_0x03e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03e7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03eb, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L_0x04a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x03ed, code lost:
        r1 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x03ef, code lost:
        if (r1 != 0) goto L_0x0409;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03f7, code lost:
        if (r3.users.size() != 1) goto L_0x0409;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x03f9, code lost:
        r1 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x0409, code lost:
        if (r1 == 0) goto L_0x044a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x040b, code lost:
        if (r1 != r10) goto L_0x0421;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x0420, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0421, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x042d, code lost:
        if (r0 != null) goto L_0x0430;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x042f, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x0449, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r11, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x044a, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x045a, code lost:
        if (r2 >= r0.messageOwner.action.users.size()) goto L_0x0487;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x045c, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0470, code lost:
        if (r3 == null) goto L_0x0484;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x0472, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x047a, code lost:
        if (r1.length() == 0) goto L_0x0481;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x047c, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x0481, code lost:
        r1.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x0484, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x04a0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r11, r6.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x04a4, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x04b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x04b8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x04bc, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x04d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x04cf, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r11, r3.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04d2, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0d7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04d6, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x04da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04dc, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x053c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04de, code lost:
        r2 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04e0, code lost:
        if (r2 != r10) goto L_0x04f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x04f5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x04f8, code lost:
        if (r2 != r1) goto L_0x050c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x050b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x050c, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x051e, code lost:
        if (r0 != null) goto L_0x0521;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0520, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x053b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r11, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x053e, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x0547;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0546, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0549, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x0552;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0551, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0554, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x0567;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0566, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x056a, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x057c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x057b, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r3.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x057e, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x0587;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0586, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x0589, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0d7e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x058f, code lost:
        if (r6 == null) goto L_0x0876;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0595, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r6) == false) goto L_0x059b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x0599, code lost:
        if (r6.megagroup == false) goto L_0x0876;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x059b, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x059d, code lost:
        if (r0 != null) goto L_0x05b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x05b2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x05b9, code lost:
        if (r0.isMusic() == false) goto L_0x05cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x05cc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x05d6, code lost:
        if (r0.isVideo() == false) goto L_0x0621;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05dc, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x060d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x05e6, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x060d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x060c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "📹 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x0620, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x0625, code lost:
        if (r0.isGif() == false) goto L_0x0670;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x062b, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x065c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x0635, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x065c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x065b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "🎬 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x066f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x0676, code lost:
        if (r0.isVoice() == false) goto L_0x068a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0689, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x068e, code lost:
        if (r0.isRoundVideo() == false) goto L_0x06a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x06a1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x06a6, code lost:
        if (r0.isSticker() != false) goto L_0x0846;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x06ac, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x06b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x06b0, code lost:
        r3 = r0.messageOwner;
        r4 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x06b6, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x06ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x06bc, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x06eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x06c4, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x06eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x06ea, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "📎 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x06fe, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x0701, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0832;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0705, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0709;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x070b, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0721;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x0720, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x0724, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0746;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0726, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0745, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r11, r6.title, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x0748, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0784;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x074a, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x0750, code lost:
        if (r0.quiz == false) goto L_0x076b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x076a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r11, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x0783, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r11, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x0786, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x07cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x078c, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x07bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x0794, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x07bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x07ba, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "🖼 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x07ce, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x07d3, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x07e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x07e6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x07e7, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x07e9, code lost:
        if (r3 == null) goto L_0x081e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x07ef, code lost:
        if (r3.length() <= 0) goto L_0x081e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x07f1, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x07f7, code lost:
        if (r0.length() <= 20) goto L_0x080c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x07f9, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x081d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, r0, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0831, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0845, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x0846, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x084b, code lost:
        if (r0 == null) goto L_0x0863;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0862, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r11, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0875, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0877, code lost:
        if (r6 == null) goto L_0x0b12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0879, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x087b, code lost:
        if (r0 != null) goto L_0x088d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x088c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0891, code lost:
        if (r0.isMusic() == false) goto L_0x08a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x08a2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x08ac, code lost:
        if (r0.isVideo() == false) goto L_0x08f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x08b2, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x08e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x08bc, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x08e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x08df, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x08f0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x08f5, code lost:
        if (r0.isGif() == false) goto L_0x093a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x08fb, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0929;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x0905, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0929;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0928, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0939, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x093f, code lost:
        if (r0.isVoice() == false) goto L_0x0951;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x0950, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x0955, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0967;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x0966, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x096b, code lost:
        if (r0.isSticker() != false) goto L_0x0ae7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x0971, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0975;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x0975, code lost:
        r3 = r0.messageOwner;
        r4 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x097b, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x09be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0981, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x09ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0989, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x09ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x09ac, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x09bd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x09c0, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0ad6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x09c4, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x09c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x09ca, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x09dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x09dc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x09df, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x09ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x09e1, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x09fe, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r6.title, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0a01, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0a37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0a03, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0a09, code lost:
        if (r0.quiz == false) goto L_0x0a21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0a20, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0a36, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0a39, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0a7c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0a3f, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0a6b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0a47, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0a6b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0a6a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0a7b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0a7f, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0a91;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0a90, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0a91, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0a93, code lost:
        if (r3 == null) goto L_0x0ac5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0a99, code lost:
        if (r3.length() <= 0) goto L_0x0ac5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0a9b, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x0aa1, code lost:
        if (r0.length() <= 20) goto L_0x0ab6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x0aa3, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0ac4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x0ad5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x0ae6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0ae7, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x0aeb, code lost:
        if (r0 == null) goto L_0x0b01;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x0b00, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0b11, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0b12, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x0b14, code lost:
        if (r0 != null) goto L_0x0b24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0b23, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x0b28, code lost:
        if (r0.isMusic() == false) goto L_0x0b38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0b37, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0b41, code lost:
        if (r0.isVideo() == false) goto L_0x0b82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0b47, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0b51, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0b72, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0b81, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0b86, code lost:
        if (r0.isGif() == false) goto L_0x0bc7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0b8c, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0bb8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0b96, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0bb8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0bb7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0bc6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0bcc, code lost:
        if (r0.isVoice() == false) goto L_0x0bdc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0bdb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0be0, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0bf0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x0bef, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x0bf4, code lost:
        if (r0.isSticker() != false) goto L_0x0d58;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x0bfa, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0bfe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0bfe, code lost:
        r3 = r0.messageOwner;
        r4 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0CLASSNAME, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x0c0a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x0CLASSNAME, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0d49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x0CLASSNAME, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0c4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x0c4f, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x0c5f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x0CLASSNAME, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0CLASSNAME, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0c7f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r11, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0CLASSNAME, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0cb4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0CLASSNAME, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x0c8a, code lost:
        if (r0.quiz == false) goto L_0x0ca0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x0c9f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r11, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0cb3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r11, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0cb6, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0cf5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x0cbc, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0ce6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0cc4, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0ce6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x0ce5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0cf4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0cf8, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0d08;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0d07, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0d08, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0d0a, code lost:
        if (r3 == null) goto L_0x0d3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0d10, code lost:
        if (r3.length() <= 0) goto L_0x0d3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0d12, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0d18, code lost:
        if (r0.length() <= 20) goto L_0x0d2d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0d1a, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0d39, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0d48, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0d57, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0d58, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0d5d, code lost:
        if (r0 == null) goto L_0x0d70;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0d6f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r11, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0d7d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0d7e, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0d83, code lost:
        if (r2.peer_id.channel_id == 0) goto L_0x0db1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0d87, code lost:
        if (r6.megagroup != false) goto L_0x0db1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0d8d, code lost:
        if (r19.isVideoAvatar() == false) goto L_0x0da0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0d9f, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0db0, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0db5, code lost:
        if (r19.isVideoAvatar() == false) goto L_0x0dcb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x0dca, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x0dde, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0de5, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0df4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x0df9, code lost:
        if (r19.isMediaEmpty() == false) goto L_0x0e12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0e03, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0e0a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0e09, code lost:
        return r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0e11, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0e12, code lost:
        r1 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0e18, code lost:
        if ((r1.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0e58;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0e1e, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0e3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0e26, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L_0x0e3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0e3b, code lost:
        return "🖼 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0e42, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0e4e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0e4d, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0e57, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0e5c, code lost:
        if (r19.isVideo() == false) goto L_0x0e9e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0e62, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0e82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0e6c, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0e82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0e81, code lost:
        return "📹 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0e88, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0e94;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0e93, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0e9d, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0ea2, code lost:
        if (r19.isGame() == false) goto L_0x0eae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0ead, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0eb2, code lost:
        if (r19.isVoice() == false) goto L_0x0ebe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0ebd, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0ec2, code lost:
        if (r19.isRoundVideo() == false) goto L_0x0ece;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0ecd, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0ed2, code lost:
        if (r19.isMusic() == false) goto L_0x0ede;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0edd, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0ede, code lost:
        r1 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0ee4, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0ef0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0eef, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0ef2, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0efa, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1).poll.quiz == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0f0f, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0var_, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0fe0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0var_, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0f1a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0f1c, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0f2a, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0fc9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0var_, code lost:
        if (r19.isSticker() != false) goto L_0x0f9b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0var_, code lost:
        if (r19.isAnimatedSticker() == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0f3d, code lost:
        if (r19.isGif() == false) goto L_0x0f6d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0var_, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0f4d, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0var_, code lost:
        return "🎬 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0f6c, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0var_, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0f7b, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0var_, code lost:
        return "📎 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0f9a, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0f9b, code lost:
        r0 = r19.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0f9f, code lost:
        if (r0 == null) goto L_0x0fbf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0fbe, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0fc8, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0fcf, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x0fd8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0fd7, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0fdf, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0fe9, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0198  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x019c A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x019d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r19, java.lang.String[] r20, boolean[] r21) {
        /*
            r18 = this;
            r0 = r19
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            java.lang.String r2 = "NotificationHiddenMessage"
            if (r1 != 0) goto L_0x0ff6
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x0010
            goto L_0x0ff6
        L_0x0010:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r3 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r5 = r1.chat_id
            if (r5 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            int r5 = r1.channel_id
        L_0x001d:
            int r1 = r1.user_id
            r6 = 1
            r7 = 0
            if (r21 == 0) goto L_0x0025
            r21[r7] = r6
        L_0x0025:
            org.telegram.messenger.AccountInstance r8 = r18.getAccountInstance()
            android.content.SharedPreferences r8 = r8.getNotificationsSettings()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "content_preview_"
            r9.append(r10)
            r9.append(r3)
            java.lang.String r9 = r9.toString()
            boolean r9 = r8.getBoolean(r9, r6)
            boolean r10 = r19.isFcmMessage()
            r11 = 2131626088(0x7f0e0868, float:1.8879402E38)
            java.lang.String r12 = "Message"
            r13 = 27
            r14 = 2
            if (r10 == 0) goto L_0x00e2
            if (r5 != 0) goto L_0x006f
            if (r1 == 0) goto L_0x006f
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 <= r13) goto L_0x005c
            java.lang.String r1 = r0.localName
            r20[r7] = r1
        L_0x005c:
            if (r9 == 0) goto L_0x0066
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 != 0) goto L_0x00dd
        L_0x0066:
            if (r21 == 0) goto L_0x006a
            r21[r7] = r7
        L_0x006a:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r11)
            return r0
        L_0x006f:
            if (r5 == 0) goto L_0x00dd
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0089
            boolean r1 = r19.isSupergroup()
            if (r1 == 0) goto L_0x0080
            goto L_0x0089
        L_0x0080:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 <= r13) goto L_0x008d
            java.lang.String r1 = r0.localName
            r20[r7] = r1
            goto L_0x008d
        L_0x0089:
            java.lang.String r1 = r0.localUserName
            r20[r7] = r1
        L_0x008d:
            if (r9 == 0) goto L_0x00a7
            boolean r1 = r0.localChannel
            if (r1 != 0) goto L_0x009b
            java.lang.String r1 = "EnablePreviewGroup"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 == 0) goto L_0x00a7
        L_0x009b:
            boolean r1 = r0.localChannel
            if (r1 == 0) goto L_0x00dd
            java.lang.String r1 = "EnablePreviewChannel"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 != 0) goto L_0x00dd
        L_0x00a7:
            if (r21 == 0) goto L_0x00ab
            r21[r7] = r7
        L_0x00ab:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00c9
            boolean r1 = r19.isSupergroup()
            if (r1 != 0) goto L_0x00c9
            r1 = 2131624725(0x7f0e0315, float:1.8876638E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            java.lang.String r0 = r0.localName
            r2[r7] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00c9:
            r1 = 2131626391(0x7f0e0997, float:1.8880017E38)
            java.lang.Object[] r2 = new java.lang.Object[r14]
            java.lang.String r3 = r0.localUserName
            r2[r7] = r3
            java.lang.String r0 = r0.localName
            r2[r6] = r0
            java.lang.String r0 = "NotificationMessageGroupNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00dd:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x00e2:
            org.telegram.messenger.UserConfig r10 = r18.getUserConfig()
            int r10 = r10.getClientUserId()
            if (r1 != 0) goto L_0x00f4
            int r1 = r19.getFromChatId()
            if (r1 != 0) goto L_0x00fa
            int r1 = -r5
            goto L_0x00fa
        L_0x00f4:
            if (r1 != r10) goto L_0x00fa
            int r1 = r19.getFromChatId()
        L_0x00fa:
            r15 = 0
            int r17 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r17 != 0) goto L_0x0108
            if (r5 == 0) goto L_0x0105
            int r3 = -r5
            long r3 = (long) r3
            goto L_0x0108
        L_0x0105:
            if (r1 == 0) goto L_0x0108
            long r3 = (long) r1
        L_0x0108:
            boolean r15 = org.telegram.messenger.UserObject.isReplyUser((long) r3)
            if (r15 == 0) goto L_0x011c
            org.telegram.tgnet.TLRPC$Message r15 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r15 = r15.fwd_from
            if (r15 == 0) goto L_0x011c
            org.telegram.tgnet.TLRPC$Peer r15 = r15.from_id
            if (r15 == 0) goto L_0x011c
            int r1 = org.telegram.messenger.MessageObject.getPeerId(r15)
        L_0x011c:
            r15 = 0
            if (r1 <= 0) goto L_0x0140
            org.telegram.messenger.MessagesController r11 = r18.getMessagesController()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r11 = r11.getUser(r14)
            if (r11 == 0) goto L_0x0154
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r11)
            if (r5 == 0) goto L_0x0136
            r20[r7] = r11
            goto L_0x0155
        L_0x0136:
            int r14 = android.os.Build.VERSION.SDK_INT
            if (r14 <= r13) goto L_0x013d
            r20[r7] = r11
            goto L_0x0155
        L_0x013d:
            r20[r7] = r15
            goto L_0x0155
        L_0x0140:
            org.telegram.messenger.MessagesController r11 = r18.getMessagesController()
            int r14 = -r1
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r11 = r11.getChat(r14)
            if (r11 == 0) goto L_0x0154
            java.lang.String r11 = r11.title
            r20[r7] = r11
            goto L_0x0155
        L_0x0154:
            r11 = r15
        L_0x0155:
            if (r11 == 0) goto L_0x019a
            if (r1 <= 0) goto L_0x019a
            boolean r14 = org.telegram.messenger.UserObject.isReplyUser((long) r3)
            if (r14 == 0) goto L_0x019a
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            if (r14 == 0) goto L_0x019a
            org.telegram.tgnet.TLRPC$Peer r14 = r14.saved_from_peer
            if (r14 == 0) goto L_0x019a
            int r14 = org.telegram.messenger.MessageObject.getPeerId(r14)
            if (r14 >= 0) goto L_0x019a
            org.telegram.messenger.MessagesController r6 = r18.getMessagesController()
            int r14 = -r14
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r14)
            if (r6 == 0) goto L_0x019a
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r11)
            java.lang.String r11 = " @ "
            r14.append(r11)
            java.lang.String r6 = r6.title
            r14.append(r6)
            java.lang.String r11 = r14.toString()
            r6 = r20[r7]
            if (r6 == 0) goto L_0x019a
            r20[r7] = r11
        L_0x019a:
            if (r11 != 0) goto L_0x019d
            return r15
        L_0x019d:
            if (r5 == 0) goto L_0x01bf
            org.telegram.messenger.MessagesController r6 = r18.getMessagesController()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r14)
            if (r6 != 0) goto L_0x01ae
            return r15
        L_0x01ae:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r14 == 0) goto L_0x01c0
            boolean r14 = r6.megagroup
            if (r14 != 0) goto L_0x01c0
            int r14 = android.os.Build.VERSION.SDK_INT
            if (r14 > r13) goto L_0x01c0
            r20[r7] = r15
            goto L_0x01c0
        L_0x01bf:
            r6 = r15
        L_0x01c0:
            int r4 = (int) r3
            if (r4 != 0) goto L_0x01cd
            r20[r7] = r15
            r0 = 2131626368(0x7f0e0980, float:1.887997E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            return r0
        L_0x01cd:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r2 == 0) goto L_0x01d9
            boolean r2 = r6.megagroup
            if (r2 != 0) goto L_0x01d9
            r2 = 1
            goto L_0x01da
        L_0x01d9:
            r2 = 0
        L_0x01da:
            if (r9 == 0) goto L_0x0fea
            if (r5 != 0) goto L_0x01ea
            if (r1 == 0) goto L_0x01ea
            java.lang.String r3 = "EnablePreviewAll"
            r4 = 1
            boolean r3 = r8.getBoolean(r3, r4)
            if (r3 != 0) goto L_0x0201
            goto L_0x01eb
        L_0x01ea:
            r4 = 1
        L_0x01eb:
            if (r5 == 0) goto L_0x0fea
            if (r2 != 0) goto L_0x01f7
            java.lang.String r3 = "EnablePreviewGroup"
            boolean r3 = r8.getBoolean(r3, r4)
            if (r3 != 0) goto L_0x0201
        L_0x01f7:
            if (r2 == 0) goto L_0x0fea
            java.lang.String r2 = "EnablePreviewChannel"
            boolean r2 = r8.getBoolean(r2, r4)
            if (r2 == 0) goto L_0x0fea
        L_0x0201:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r4 = "🎬 "
            java.lang.String r5 = "📎 "
            java.lang.String r8 = "📹 "
            java.lang.String r9 = "🖼 "
            if (r3 == 0) goto L_0x0df5
            r20[r7] = r15
            org.telegram.tgnet.TLRPC$MessageAction r3 = r2.action
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r12 == 0) goto L_0x0222
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0222:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r12 != 0) goto L_0x0de6
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r12 == 0) goto L_0x022c
            goto L_0x0de6
        L_0x022c:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r12 == 0) goto L_0x023f
            r0 = 2131626349(0x7f0e096d, float:1.8879932E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x023f:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            r14 = 3
            if (r12 == 0) goto L_0x02a1
            r1 = 2131628133(0x7f0e1065, float:1.888355E38)
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterYear
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r4 = (long) r4
            r8 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 * r8
            java.lang.String r2 = r2.format((long) r4)
            r3[r7] = r2
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterDay
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r4 = (long) r4
            long r4 = r4 * r8
            java.lang.String r2 = r2.format((long) r4)
            r4 = 1
            r3[r4] = r2
            java.lang.String r2 = "formatDateAtTime"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            r2 = 2131626418(0x7f0e09b2, float:1.8880072E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r5 = r18.getUserConfig()
            org.telegram.tgnet.TLRPC$User r5 = r5.getCurrentUser()
            java.lang.String r5 = r5.first_name
            r3[r7] = r5
            r3[r4] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r1 = r0.title
            r4 = 2
            r3[r4] = r1
            java.lang.String r0 = r0.address
            r3[r14] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x02a1:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r12 != 0) goto L_0x0ddf
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r12 == 0) goto L_0x02ab
            goto L_0x0ddf
        L_0x02ab:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r12 == 0) goto L_0x02c7
            boolean r0 = r3.video
            if (r0 == 0) goto L_0x02bd
            r0 = 2131624615(0x7f0e02a7, float:1.8876415E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02bd:
            r0 = 2131624609(0x7f0e02a1, float:1.8876403E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02c7:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r12 == 0) goto L_0x03d0
            int r2 = r3.user_id
            if (r2 != 0) goto L_0x02e8
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            r4 = 1
            if (r3 != r4) goto L_0x02e8
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            java.lang.Object r2 = r2.get(r7)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
        L_0x02e8:
            if (r2 == 0) goto L_0x0379
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x030a
            boolean r0 = r6.megagroup
            if (r0 != 0) goto L_0x030a
            r0 = 2131624675(0x7f0e02e3, float:1.8876536E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x030a:
            r3 = 2
            r4 = 1
            if (r2 != r10) goto L_0x0320
            r0 = 2131626370(0x7f0e0982, float:1.8879974E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0320:
            org.telegram.messenger.MessagesController r0 = r18.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x032f
            return r15
        L_0x032f:
            int r2 = r0.id
            if (r1 != r2) goto L_0x035f
            boolean r0 = r6.megagroup
            if (r0 == 0) goto L_0x034b
            r0 = 2131626355(0x7f0e0973, float:1.8879944E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x034b:
            r1 = 2
            r3 = 1
            r0 = 2131626354(0x7f0e0972, float:1.8879942E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x035f:
            r3 = 1
            r1 = 2131626353(0x7f0e0971, float:1.887994E38)
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r7] = r11
            java.lang.String r4 = r6.title
            r2[r3] = r4
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0379:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x037f:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x03b6
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x03b3
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x03b0
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x03b0:
            r1.append(r3)
        L_0x03b3:
            int r2 = r2 + 1
            goto L_0x037f
        L_0x03b6:
            r0 = 2131626353(0x7f0e0971, float:1.887994E38)
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r7] = r11
            java.lang.String r3 = r6.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r1 = r1.toString()
            r12 = 2
            r2[r12] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x03d0:
            r12 = 2
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r13 == 0) goto L_0x03e8
            r0 = 2131626357(0x7f0e0975, float:1.8879948E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r12 = 1
            r1[r12] = r2
            java.lang.String r2 = "NotificationGroupCreatedCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03e8:
            r12 = 1
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r13 == 0) goto L_0x04a1
            int r1 = r3.user_id
            if (r1 != 0) goto L_0x0409
            java.util.ArrayList<java.lang.Integer> r2 = r3.users
            int r2 = r2.size()
            if (r2 != r12) goto L_0x0409
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Integer> r1 = r1.users
            java.lang.Object r1 = r1.get(r7)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
        L_0x0409:
            if (r1 == 0) goto L_0x044a
            if (r1 != r10) goto L_0x0421
            r0 = 2131626362(0x7f0e097a, float:1.8879958E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0421:
            org.telegram.messenger.MessagesController r0 = r18.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x0430
            return r15
        L_0x0430:
            r1 = 2131626361(0x7f0e0979, float:1.8879956E38)
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r7] = r11
            java.lang.String r3 = r6.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x044a:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0450:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0487
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0484
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0481
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0481:
            r1.append(r3)
        L_0x0484:
            int r2 = r2 + 1
            goto L_0x0450
        L_0x0487:
            r0 = 2131626361(0x7f0e0979, float:1.8879956E38)
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r7] = r11
            java.lang.String r3 = r6.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r1 = r1.toString()
            r12 = 2
            r2[r12] = r1
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x04a1:
            r12 = 2
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r13 == 0) goto L_0x04b9
            r0 = 2131626371(0x7f0e0983, float:1.8879976E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r13 = 1
            r1[r13] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04b9:
            r13 = 1
            boolean r14 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r14 == 0) goto L_0x04d0
            r0 = 2131626350(0x7f0e096e, float:1.8879934E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r7] = r11
            java.lang.String r2 = r3.title
            r1[r13] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04d0:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r12 != 0) goto L_0x0d7f
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r12 == 0) goto L_0x04da
            goto L_0x0d7f
        L_0x04da:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r2 == 0) goto L_0x053c
            int r2 = r3.user_id
            if (r2 != r10) goto L_0x04f6
            r0 = 2131626364(0x7f0e097c, float:1.8879962E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04f6:
            r3 = 2
            r4 = 1
            if (r2 != r1) goto L_0x050c
            r0 = 2131626365(0x7f0e097d, float:1.8879964E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x050c:
            org.telegram.messenger.MessagesController r1 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x0521
            return r15
        L_0x0521:
            r1 = 2131626363(0x7f0e097b, float:1.887996E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r3 = r6.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x053c:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r1 == 0) goto L_0x0547
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0547:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r1 == 0) goto L_0x0552
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0552:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r1 == 0) goto L_0x0567
            r0 = 2131624131(0x7f0e00c3, float:1.8875433E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0567:
            r1 = 1
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r2 == 0) goto L_0x057c
            r0 = 2131624131(0x7f0e00c3, float:1.8875433E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r3.title
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x057c:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r1 == 0) goto L_0x0587
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0587:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r1 == 0) goto L_0x0d7e
            java.lang.String r1 = "..."
            r2 = 20
            if (r6 == 0) goto L_0x0876
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r3 == 0) goto L_0x059b
            boolean r3 = r6.megagroup
            if (r3 == 0) goto L_0x0876
        L_0x059b:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x05b3
            r0 = 2131626318(0x7f0e094e, float:1.8879869E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r10 = 1
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05b3:
            r3 = 2
            r10 = 1
            boolean r12 = r0.isMusic()
            if (r12 == 0) goto L_0x05cd
            r0 = 2131626315(0x7f0e094b, float:1.8879863E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05cd:
            boolean r3 = r0.isVideo()
            r10 = 2131626339(0x7f0e0963, float:1.8879911E38)
            java.lang.String r12 = "NotificationActionPinnedText"
            if (r3 == 0) goto L_0x0621
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x060d
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x060d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = r6.title
            r3 = 2
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r10, r1)
            return r0
        L_0x060d:
            r2 = 1
            r3 = 2
            r0 = 2131626342(0x7f0e0966, float:1.8879917E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r3 = r6.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0621:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x0670
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x065c
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x065c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = r6.title
            r4 = 2
            r1[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r10, r1)
            return r0
        L_0x065c:
            r3 = 1
            r4 = 2
            r0 = 2131626309(0x7f0e0945, float:1.887985E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0670:
            r3 = 1
            r4 = 2
            boolean r8 = r0.isVoice()
            if (r8 == 0) goto L_0x068a
            r0 = 2131626345(0x7f0e0969, float:1.8879924E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x068a:
            boolean r8 = r0.isRoundVideo()
            if (r8 == 0) goto L_0x06a2
            r0 = 2131626330(0x7f0e095a, float:1.8879893E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06a2:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x0846
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x06b0
            goto L_0x0846
        L_0x06b0:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r3.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x06ff
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x06eb
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x06eb
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = r6.title
            r3 = 2
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r10, r1)
            return r0
        L_0x06eb:
            r2 = 1
            r3 = 2
            r0 = 2131626294(0x7f0e0936, float:1.887982E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r3 = r6.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06ff:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x0832
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x0709
            goto L_0x0832
        L_0x0709:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0721
            r0 = 2131626305(0x7f0e0941, float:1.8879842E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r5 = 1
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0721:
            r5 = 1
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x0746
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4
            r0 = 2131626291(0x7f0e0933, float:1.8879814E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r5] = r2
            java.lang.String r2 = r4.first_name
            java.lang.String r3 = r4.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0746:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0784
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x076b
            r1 = 2131626327(0x7f0e0957, float:1.8879887E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r3 = r6.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x076b:
            r2 = 3
            r3 = 2
            r4 = 1
            r1 = 2131626324(0x7f0e0954, float:1.887988E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r5 = r6.title
            r2[r4] = r5
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0784:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x07cf
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x07bb
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x07bb
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = r6.title
            r5 = 2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r10, r1)
            return r0
        L_0x07bb:
            r3 = 1
            r5 = 2
            r0 = 2131626321(0x7f0e0951, float:1.8879875E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07cf:
            r3 = 1
            r5 = 2
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x07e7
            r0 = 2131626297(0x7f0e0939, float:1.8879826E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07e7:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x081e
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x081e
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r2) goto L_0x080c
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r2)
            r3.append(r0)
            r3.append(r1)
            java.lang.String r0 = r3.toString()
        L_0x080c:
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = r6.title
            r2 = 2
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r10, r1)
            return r0
        L_0x081e:
            r2 = 2
            r3 = 1
            r0 = 2131626318(0x7f0e094e, float:1.8879869E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0832:
            r2 = 2
            r3 = 1
            r0 = 2131626303(0x7f0e093f, float:1.8879838E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0846:
            r3 = 1
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0863
            r1 = 2131626335(0x7f0e095f, float:1.8879903E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r4 = r6.title
            r2[r3] = r4
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0863:
            r4 = 2
            r0 = 2131626333(0x7f0e095d, float:1.88799E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0876:
            r3 = 1
            if (r6 == 0) goto L_0x0b12
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x088d
            r0 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x088d:
            boolean r10 = r0.isMusic()
            if (r10 == 0) goto L_0x08a3
            r0 = 2131626316(0x7f0e094c, float:1.8879865E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08a3:
            boolean r3 = r0.isVideo()
            r10 = 2131626340(0x7f0e0964, float:1.8879913E38)
            java.lang.String r11 = "NotificationActionPinnedTextChannel"
            if (r3 == 0) goto L_0x08f1
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x08e0
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x08e0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x08e0:
            r2 = 1
            r0 = 2131626343(0x7f0e0967, float:1.887992E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08f1:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x093a
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0929
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0929
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x0929:
            r3 = 1
            r0 = 2131626310(0x7f0e0946, float:1.8879853E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x093a:
            r3 = 1
            boolean r4 = r0.isVoice()
            if (r4 == 0) goto L_0x0951
            r0 = 2131626346(0x7f0e096a, float:1.8879926E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0951:
            boolean r4 = r0.isRoundVideo()
            if (r4 == 0) goto L_0x0967
            r0 = 2131626331(0x7f0e095b, float:1.8879895E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0967:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x0ae7
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x0975
            goto L_0x0ae7
        L_0x0975:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r3.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x09be
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x09ad
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x09ad
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x09ad:
            r2 = 1
            r0 = 2131626295(0x7f0e0937, float:1.8879822E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09be:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x0ad6
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x09c8
            goto L_0x0ad6
        L_0x09c8:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x09dd
            r0 = 2131626306(0x7f0e0942, float:1.8879844E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09dd:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r5 == 0) goto L_0x09ff
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4
            r0 = 2131626292(0x7f0e0934, float:1.8879816E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = r4.first_name
            java.lang.String r3 = r4.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09ff:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0a37
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0a21
            r1 = 2131626328(0x7f0e0958, float:1.887989E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r6.title
            r2[r7] = r3
            java.lang.String r0 = r0.question
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0a21:
            r2 = 2
            r3 = 1
            r1 = 2131626325(0x7f0e0955, float:1.8879883E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r6.title
            r2[r7] = r4
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0a37:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0a7c
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0a6b
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0a6b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x0a6b:
            r3 = 1
            r0 = 2131626322(0x7f0e0952, float:1.8879877E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a7c:
            r3 = 1
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0a91
            r0 = 2131626298(0x7f0e093a, float:1.8879828E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a91:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x0ac5
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0ac5
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r2) goto L_0x0ab6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r2)
            r3.append(r0)
            r3.append(r1)
            java.lang.String r0 = r3.toString()
        L_0x0ab6:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x0ac5:
            r2 = 1
            r0 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ad6:
            r2 = 1
            r0 = 2131626304(0x7f0e0940, float:1.887984E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ae7:
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0b01
            r1 = 2131626336(0x7f0e0960, float:1.8879905E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r6.title
            r2[r7] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0b01:
            r3 = 1
            r0 = 2131626334(0x7f0e095e, float:1.8879901E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b12:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0b24
            r0 = 2131626320(0x7f0e0950, float:1.8879873E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b24:
            boolean r6 = r0.isMusic()
            if (r6 == 0) goto L_0x0b38
            r0 = 2131626317(0x7f0e094d, float:1.8879867E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedMusicUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b38:
            boolean r3 = r0.isVideo()
            r6 = 2131626341(0x7f0e0965, float:1.8879915E38)
            java.lang.String r10 = "NotificationActionPinnedTextUser"
            if (r3 == 0) goto L_0x0b82
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0b73
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b73
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r6, r1)
            return r0
        L_0x0b73:
            r2 = 1
            r0 = 2131626344(0x7f0e0968, float:1.8879922E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedVideoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b82:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x0bc7
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0bb8
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0bb8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r6, r1)
            return r0
        L_0x0bb8:
            r3 = 1
            r0 = 2131626311(0x7f0e0947, float:1.8879855E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGifUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bc7:
            r3 = 1
            boolean r4 = r0.isVoice()
            if (r4 == 0) goto L_0x0bdc
            r0 = 2131626347(0x7f0e096b, float:1.8879928E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bdc:
            boolean r4 = r0.isRoundVideo()
            if (r4 == 0) goto L_0x0bf0
            r0 = 2131626332(0x7f0e095c, float:1.8879897E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedRoundUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bf0:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x0d58
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x0bfe
            goto L_0x0d58
        L_0x0bfe:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r3.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0CLASSNAME
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0CLASSNAME
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r6, r1)
            return r0
        L_0x0CLASSNAME:
            r2 = 1
            r0 = 2131626296(0x7f0e0938, float:1.8879824E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedFileUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0CLASSNAME:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x0d49
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x0c4d
            goto L_0x0d49
        L_0x0c4d:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0CLASSNAME
            r0 = 2131626307(0x7f0e0943, float:1.8879846E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGeoLiveUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0CLASSNAME:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r5 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4
            r0 = 2131626293(0x7f0e0935, float:1.8879818E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r4.first_name
            java.lang.String r3 = r4.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContactUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0CLASSNAME:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0cb4
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0ca0
            r1 = 2131626329(0x7f0e0959, float:1.8879891E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r0 = r0.question
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0ca0:
            r2 = 2
            r3 = 1
            r1 = 2131626326(0x7f0e0956, float:1.8879885E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0cb4:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0cf5
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0ce6
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ce6
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r6, r1)
            return r0
        L_0x0ce6:
            r3 = 1
            r0 = 2131626323(0x7f0e0953, float:1.8879879E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0cf5:
            r3 = 1
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0d08
            r0 = 2131626302(0x7f0e093e, float:1.8879836E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGameUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d08:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x0d3a
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0d3a
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r2) goto L_0x0d2d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r2)
            r3.append(r0)
            r3.append(r1)
            java.lang.String r0 = r3.toString()
        L_0x0d2d:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r6, r1)
            return r0
        L_0x0d3a:
            r2 = 1
            r0 = 2131626320(0x7f0e0950, float:1.8879873E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d49:
            r2 = 1
            r0 = 2131626308(0x7f0e0944, float:1.8879849E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGeoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d58:
            r2 = 1
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0d70
            r1 = 2131626337(0x7f0e0961, float:1.8879907E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r7] = r11
            r3[r2] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x0d70:
            r0 = 2131626338(0x7f0e0962, float:1.887991E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedStickerUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d7e:
            return r15
        L_0x0d7f:
            org.telegram.tgnet.TLRPC$Peer r1 = r2.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0db1
            boolean r1 = r6.megagroup
            if (r1 != 0) goto L_0x0db1
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x0da0
            r0 = 2131624775(0x7f0e0347, float:1.887674E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0da0:
            r1 = 1
            r0 = 2131624741(0x7f0e0325, float:1.887667E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0db1:
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x0dcb
            r0 = 2131626352(0x7f0e0970, float:1.8879938E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0dcb:
            r1 = 2
            r3 = 1
            r0 = 2131626351(0x7f0e096f, float:1.8879936E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ddf:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0de6:
            r3 = 1
            r0 = 2131626348(0x7f0e096c, float:1.887993E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0df5:
            boolean r1 = r19.isMediaEmpty()
            if (r1 == 0) goto L_0x0e12
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e0a
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x0e0a:
            r0 = 2131626088(0x7f0e0868, float:1.8879402E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0e12:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0e58
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0e3c
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e3c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0e3c:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0e4e
            r0 = 2131624373(0x7f0e01b5, float:1.8875924E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e4e:
            r0 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e58:
            boolean r1 = r19.isVideo()
            if (r1 == 0) goto L_0x0e9e
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0e82
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e82
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0e82:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0e94
            r0 = 2131624374(0x7f0e01b6, float:1.8875926E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e94:
            r0 = 2131624396(0x7f0e01cc, float:1.887597E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e9e:
            boolean r1 = r19.isGame()
            if (r1 == 0) goto L_0x0eae
            r0 = 2131624376(0x7f0e01b8, float:1.887593E38)
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0eae:
            boolean r1 = r19.isVoice()
            if (r1 == 0) goto L_0x0ebe
            r0 = 2131624370(0x7f0e01b2, float:1.8875918E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ebe:
            boolean r1 = r19.isRoundVideo()
            if (r1 == 0) goto L_0x0ece
            r0 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ece:
            boolean r1 = r19.isMusic()
            if (r1 == 0) goto L_0x0ede
            r0 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ede:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r2 == 0) goto L_0x0ef0
            r0 = 2131624372(0x7f0e01b4, float:1.8875922E38)
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ef0:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0var_
            r0 = 2131627041(0x7f0e0CLASSNAME, float:1.8881335E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            r0 = 2131626929(0x7f0e0bb1, float:1.8881108E38)
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r2 != 0) goto L_0x0fe0
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r2 == 0) goto L_0x0f1a
            goto L_0x0fe0
        L_0x0f1a:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x0var_
            r0 = 2131624382(0x7f0e01be, float:1.8875942E38)
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x0fc9
            boolean r1 = r19.isSticker()
            if (r1 != 0) goto L_0x0f9b
            boolean r1 = r19.isAnimatedSticker()
            if (r1 == 0) goto L_0x0var_
            goto L_0x0f9b
        L_0x0var_:
            boolean r1 = r19.isGif()
            if (r1 == 0) goto L_0x0f6d
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0var_:
            r0 = 2131624377(0x7f0e01b9, float:1.8875932E38)
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0f6d:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0var_:
            r0 = 2131624375(0x7f0e01b7, float:1.8875928E38)
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0f9b:
            java.lang.String r0 = r19.getStickerEmoji()
            if (r0 == 0) goto L_0x0fbf
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " "
            r1.append(r0)
            r0 = 2131624393(0x7f0e01c9, float:1.8875964E38)
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0fbf:
            r0 = 2131624393(0x7f0e01c9, float:1.8875964E38)
            java.lang.String r1 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fc9:
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0fd8
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0fd8:
            r0 = 2131626088(0x7f0e0868, float:1.8879402E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0fe0:
            r0 = 2131624386(0x7f0e01c2, float:1.887595E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fea:
            if (r21 == 0) goto L_0x0fee
            r21[r7] = r7
        L_0x0fee:
            r0 = 2131626088(0x7f0e0868, float:1.8879402E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0ff6:
            r0 = 2131626368(0x7f0e0980, float:1.887997E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:72:0x0138 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0139  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r20, boolean r21, boolean[] r22, boolean[] r23) {
        /*
            r19 = this;
            r0 = r20
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x1460
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x1460
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
            int r1 = r1.user_id
            r5 = 1
            r6 = 0
            if (r23 == 0) goto L_0x0023
            r23[r6] = r5
        L_0x0023:
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
            r10 = 2131626391(0x7f0e0997, float:1.8880017E38)
            java.lang.String r11 = "NotificationMessageGroupNoText"
            r12 = 2131626404(0x7f0e09a4, float:1.8880043E38)
            java.lang.String r13 = "NotificationMessageNoText"
            r14 = 2
            if (r9 == 0) goto L_0x00c2
            if (r4 != 0) goto L_0x006e
            if (r1 == 0) goto L_0x006e
            if (r8 == 0) goto L_0x005f
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 != 0) goto L_0x00bb
        L_0x005f:
            if (r23 == 0) goto L_0x0063
            r23[r6] = r6
        L_0x0063:
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r0 = r0.localName
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r13, r12, r1)
            return r0
        L_0x006e:
            if (r4 == 0) goto L_0x00bb
            if (r8 == 0) goto L_0x008a
            boolean r1 = r0.localChannel
            if (r1 != 0) goto L_0x007e
            java.lang.String r1 = "EnablePreviewGroup"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 == 0) goto L_0x008a
        L_0x007e:
            boolean r1 = r0.localChannel
            if (r1 == 0) goto L_0x00bb
            java.lang.String r1 = "EnablePreviewChannel"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 != 0) goto L_0x00bb
        L_0x008a:
            if (r23 == 0) goto L_0x008e
            r23[r6] = r6
        L_0x008e:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00ac
            boolean r1 = r20.isSupergroup()
            if (r1 != 0) goto L_0x00ac
            r1 = 2131624725(0x7f0e0315, float:1.8876638E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            java.lang.String r0 = r0.localName
            r2[r6] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00ac:
            java.lang.Object[] r1 = new java.lang.Object[r14]
            java.lang.String r2 = r0.localUserName
            r1[r6] = r2
            java.lang.String r0 = r0.localName
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x00bb:
            r22[r6] = r5
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = (java.lang.String) r0
            return r0
        L_0x00c2:
            org.telegram.messenger.UserConfig r9 = r19.getUserConfig()
            int r9 = r9.getClientUserId()
            if (r1 != 0) goto L_0x00d4
            int r1 = r20.getFromChatId()
            if (r1 != 0) goto L_0x00da
            int r1 = -r4
            goto L_0x00da
        L_0x00d4:
            if (r1 != r9) goto L_0x00da
            int r1 = r20.getFromChatId()
        L_0x00da:
            r15 = 0
            int r17 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1))
            if (r17 != 0) goto L_0x00e8
            if (r4 == 0) goto L_0x00e5
            int r2 = -r4
            long r2 = (long) r2
            goto L_0x00e8
        L_0x00e5:
            if (r1 == 0) goto L_0x00e8
            long r2 = (long) r1
        L_0x00e8:
            r15 = 0
            if (r1 <= 0) goto L_0x0121
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            boolean r10 = r10.from_scheduled
            if (r10 == 0) goto L_0x010c
            r17 = r13
            long r12 = (long) r9
            int r18 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r18 != 0) goto L_0x0102
            r12 = 2131626107(0x7f0e087b, float:1.887944E38)
            java.lang.String r13 = "MessageScheduledReminderNotification"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            goto L_0x0136
        L_0x0102:
            r12 = 2131626412(0x7f0e09ac, float:1.888006E38)
            java.lang.String r13 = "NotificationMessageScheduledName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            goto L_0x0136
        L_0x010c:
            r17 = r13
            org.telegram.messenger.MessagesController r12 = r19.getMessagesController()
            java.lang.Integer r13 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r12 = r12.getUser(r13)
            if (r12 == 0) goto L_0x0135
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r12)
            goto L_0x0136
        L_0x0121:
            r17 = r13
            org.telegram.messenger.MessagesController r12 = r19.getMessagesController()
            int r13 = -r1
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r12 = r12.getChat(r13)
            if (r12 == 0) goto L_0x0135
            java.lang.String r12 = r12.title
            goto L_0x0136
        L_0x0135:
            r12 = r15
        L_0x0136:
            if (r12 != 0) goto L_0x0139
            return r15
        L_0x0139:
            if (r4 == 0) goto L_0x014a
            org.telegram.messenger.MessagesController r13 = r19.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r10 = r13.getChat(r10)
            if (r10 != 0) goto L_0x014b
            return r15
        L_0x014a:
            r10 = r15
        L_0x014b:
            int r3 = (int) r2
            if (r3 != 0) goto L_0x0159
            r0 = 2131628086(0x7f0e1036, float:1.8883455E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x145f
        L_0x0159:
            java.lang.String r2 = "🎬 "
            java.lang.String r3 = "📎 "
            java.lang.String r13 = "📹 "
            java.lang.String r15 = "🖼 "
            java.lang.String r14 = "NotificationMessageText"
            r6 = 3
            if (r4 != 0) goto L_0x0559
            if (r1 == 0) goto L_0x0559
            if (r8 == 0) goto L_0x0545
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 == 0) goto L_0x0545
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r4 == 0) goto L_0x024a
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r2 == 0) goto L_0x018a
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x145f
        L_0x018a:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r2 != 0) goto L_0x023a
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r2 == 0) goto L_0x0194
            goto L_0x023a
        L_0x0194:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r2 == 0) goto L_0x01a8
            r0 = 2131626349(0x7f0e096d, float:1.8879932E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x01a8:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r2 == 0) goto L_0x020b
            r1 = 2131628133(0x7f0e1065, float:1.888355E38)
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
            r2 = 2131626418(0x7f0e09b2, float:1.8880072E38)
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
            goto L_0x145f
        L_0x020b:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 != 0) goto L_0x0232
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r2 == 0) goto L_0x0214
            goto L_0x0232
        L_0x0214:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x145d
            boolean r0 = r1.video
            if (r0 == 0) goto L_0x0227
            r0 = 2131624615(0x7f0e02a7, float:1.8876415E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x145f
        L_0x0227:
            r0 = 2131624609(0x7f0e02a1, float:1.8876403E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x145f
        L_0x0232:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x145f
        L_0x023a:
            r0 = 2131626348(0x7f0e096c, float:1.887993E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x024a:
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x0293
            if (r21 != 0) goto L_0x0283
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0273
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x145f
        L_0x0273:
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r4 = r17
            r1 = 2131626404(0x7f0e09a4, float:1.8880043E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x145f
        L_0x0283:
            r4 = r17
            r1 = 2131626404(0x7f0e09a4, float:1.8880043E38)
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x145f
        L_0x0293:
            r4 = r17
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r1.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x02fa
            if (r21 != 0) goto L_0x02d3
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x02d3
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x02d3
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
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x145f
        L_0x02d3:
            r2 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x02eb
            r0 = 2131626409(0x7f0e09a9, float:1.8880053E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageSDPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x02eb:
            r0 = 2131626405(0x7f0e09a5, float:1.8880045E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x02fa:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x035f
            if (r21 != 0) goto L_0x0338
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0338
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0338
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
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r6] = r5
            goto L_0x145f
        L_0x0338:
            r6 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0350
            r0 = 2131626410(0x7f0e09aa, float:1.8880055E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageSDVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0350:
            r0 = 2131626416(0x7f0e09b0, float:1.8880068E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x035f:
            r6 = 0
            boolean r1 = r20.isGame()
            if (r1 == 0) goto L_0x0380
            r1 = 2131626378(0x7f0e098a, float:1.887999E38)
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
            goto L_0x145f
        L_0x0380:
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x0396
            r0 = 2131626373(0x7f0e0985, float:1.887998E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r6 = 0
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0396:
            r6 = 0
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x03ac
            r0 = 2131626408(0x7f0e09a8, float:1.8880051E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x03ac:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x03c1
            r0 = 2131626403(0x7f0e09a3, float:1.8880041E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x03c1:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x03e5
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131626374(0x7f0e0986, float:1.8879982E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x145f
        L_0x03e5:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x041b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0405
            r1 = 2131626407(0x7f0e09a7, float:1.888005E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0418
        L_0x0405:
            r2 = 2
            r3 = 0
            r1 = 2131626406(0x7f0e09a6, float:1.8880047E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
        L_0x0418:
            r15 = r0
            goto L_0x145f
        L_0x041b:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r6 != 0) goto L_0x0535
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r6 == 0) goto L_0x0425
            goto L_0x0535
        L_0x0425:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x0439
            r0 = 2131626401(0x7f0e09a1, float:1.8880037E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0439:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x0509
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x04e1
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x044b
            goto L_0x04e1
        L_0x044b:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x0499
            if (r21 != 0) goto L_0x0489
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x0489
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0489
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
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r3] = r5
            goto L_0x145f
        L_0x0489:
            r3 = 0
            r0 = 2131626380(0x7f0e098c, float:1.8879995E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r12
            java.lang.String r2 = "NotificationMessageGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0499:
            if (r21 != 0) goto L_0x04d1
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x04d1
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x04d1
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
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x145f
        L_0x04d1:
            r2 = 0
            r0 = 2131626375(0x7f0e0987, float:1.8879984E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x04e1:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x04fa
            r1 = 2131626414(0x7f0e09ae, float:1.8880064E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r12
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0418
        L_0x04fa:
            r0 = 2131626413(0x7f0e09ad, float:1.8880061E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0509:
            r2 = 0
            if (r21 != 0) goto L_0x0528
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0528
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.CharSequence r0 = r0.messageText
            r1[r5] = r0
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x145f
        L_0x0528:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r1 = 2131626404(0x7f0e09a4, float:1.8880043E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x145f
        L_0x0535:
            r2 = 0
            r0 = 2131626402(0x7f0e09a2, float:1.888004E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0545:
            r4 = r17
            r2 = 0
            if (r23 == 0) goto L_0x054c
            r23[r2] = r2
        L_0x054c:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r1 = 2131626404(0x7f0e09a4, float:1.8880043E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x145f
        L_0x0559:
            if (r4 == 0) goto L_0x145d
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r4 == 0) goto L_0x0567
            boolean r4 = r10.megagroup
            if (r4 != 0) goto L_0x0567
            r4 = 1
            goto L_0x0568
        L_0x0567:
            r4 = 0
        L_0x0568:
            if (r8 == 0) goto L_0x142f
            if (r4 != 0) goto L_0x0574
            java.lang.String r8 = "EnablePreviewGroup"
            boolean r8 = r7.getBoolean(r8, r5)
            if (r8 != 0) goto L_0x057e
        L_0x0574:
            if (r4 == 0) goto L_0x142f
            java.lang.String r4 = "EnablePreviewChannel"
            boolean r4 = r7.getBoolean(r4, r5)
            if (r4 == 0) goto L_0x142f
        L_0x057e:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r7 == 0) goto L_0x0e8c
            org.telegram.tgnet.TLRPC$MessageAction r7 = r4.action
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r8 == 0) goto L_0x0696
            int r2 = r7.user_id
            if (r2 != 0) goto L_0x05a7
            java.util.ArrayList<java.lang.Integer> r3 = r7.users
            int r3 = r3.size()
            if (r3 != r5) goto L_0x05a7
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
        L_0x05a7:
            if (r2 == 0) goto L_0x063e
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x05ca
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x05ca
            r0 = 2131624675(0x7f0e02e3, float:1.8876536E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x05ca:
            r3 = 2
            r4 = 0
            if (r2 != r9) goto L_0x05e1
            r0 = 2131626370(0x7f0e0982, float:1.8879974E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x05e1:
            org.telegram.messenger.MessagesController r0 = r19.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x05f1
            r2 = 0
            return r2
        L_0x05f1:
            int r2 = r0.id
            if (r1 != r2) goto L_0x0623
            boolean r0 = r10.megagroup
            if (r0 == 0) goto L_0x060e
            r0 = 2131626355(0x7f0e0973, float:1.8879944E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x060e:
            r1 = 2
            r2 = 0
            r0 = 2131626354(0x7f0e0972, float:1.8879942E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0623:
            r2 = 0
            r1 = 2131626353(0x7f0e0971, float:1.887994E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r2] = r12
            java.lang.String r2 = r10.title
            r3[r5] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r3[r2] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0418
        L_0x063e:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0644:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x067b
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0678
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0675
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0675:
            r1.append(r3)
        L_0x0678:
            int r2 = r2 + 1
            goto L_0x0644
        L_0x067b:
            r0 = 2131626353(0x7f0e0971, float:1.887994E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r1 = r1.toString()
            r8 = 2
            r2[r8] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0418
        L_0x0696:
            r8 = 2
            boolean r11 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r11 == 0) goto L_0x06af
            r0 = 2131626357(0x7f0e0975, float:1.8879948E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupCreatedCall"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x06af:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r8 == 0) goto L_0x076d
            int r1 = r7.user_id
            if (r1 != 0) goto L_0x06d1
            java.util.ArrayList<java.lang.Integer> r2 = r7.users
            int r2 = r2.size()
            if (r2 != r5) goto L_0x06d1
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Integer> r1 = r1.users
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            goto L_0x06d2
        L_0x06d1:
            r2 = 0
        L_0x06d2:
            if (r1 == 0) goto L_0x0715
            if (r1 != r9) goto L_0x06ea
            r0 = 2131626362(0x7f0e097a, float:1.8879958E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x06ea:
            org.telegram.messenger.MessagesController r0 = r19.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x06fa
            r1 = 0
            return r1
        L_0x06fa:
            r1 = 2131626361(0x7f0e0979, float:1.8879956E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0418
        L_0x0715:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x071b:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0752
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x074f
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x074c
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x074c:
            r1.append(r3)
        L_0x074f:
            int r2 = r2 + 1
            goto L_0x071b
        L_0x0752:
            r0 = 2131626361(0x7f0e0979, float:1.8879956E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r1 = r1.toString()
            r8 = 2
            r2[r8] = r1
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0418
        L_0x076d:
            r8 = 2
            boolean r11 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r11 == 0) goto L_0x0786
            r0 = 2131626371(0x7f0e0983, float:1.8879976E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r11 = 0
            r1[r11] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0786:
            r11 = 0
            boolean r14 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r14 == 0) goto L_0x079e
            r0 = 2131626350(0x7f0e096e, float:1.8879934E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r11] = r12
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x079e:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r8 != 0) goto L_0x0e29
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r8 == 0) goto L_0x07a8
            goto L_0x0e29
        L_0x07a8:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r4 == 0) goto L_0x080d
            int r2 = r7.user_id
            if (r2 != r9) goto L_0x07c5
            r0 = 2131626364(0x7f0e097c, float:1.8879962E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x07c5:
            r3 = 2
            r4 = 0
            if (r2 != r1) goto L_0x07dc
            r0 = 2131626365(0x7f0e097d, float:1.8879964E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x07dc:
            org.telegram.messenger.MessagesController r1 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x07f2
            r1 = 0
            return r1
        L_0x07f2:
            r1 = 2131626363(0x7f0e097b, float:1.887996E38)
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
            goto L_0x145f
        L_0x080d:
            r1 = 0
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r4 == 0) goto L_0x081a
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x145f
        L_0x081a:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r4 == 0) goto L_0x0826
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x145f
        L_0x0826:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r4 == 0) goto L_0x083c
            r0 = 2131624131(0x7f0e00c3, float:1.8875433E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r4 = 0
            r1[r4] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x083c:
            r4 = 0
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r8 == 0) goto L_0x0852
            r0 = 2131624131(0x7f0e00c3, float:1.8875433E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r7.title
            r1[r4] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0852:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r4 == 0) goto L_0x085e
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x145f
        L_0x085e:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r4 == 0) goto L_0x0e1d
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r1 == 0) goto L_0x0b2a
            boolean r1 = r10.megagroup
            if (r1 == 0) goto L_0x086e
            goto L_0x0b2a
        L_0x086e:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x0884
            r0 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r4 = 0
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0884:
            r4 = 0
            boolean r6 = r1.isMusic()
            if (r6 == 0) goto L_0x089c
            r0 = 2131626316(0x7f0e094c, float:1.8879865E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x089c:
            boolean r4 = r1.isVideo()
            r6 = 2131626340(0x7f0e0964, float:1.8879913E38)
            java.lang.String r7 = "NotificationActionPinnedTextChannel"
            if (r4 == 0) goto L_0x08ec
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x08da
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08da
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
            goto L_0x0418
        L_0x08da:
            r3 = 0
            r0 = 2131626343(0x7f0e0967, float:1.887992E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x08ec:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0937
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0925
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0925
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
            goto L_0x0418
        L_0x0925:
            r4 = 0
            r0 = 2131626310(0x7f0e0946, float:1.8879853E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0937:
            r4 = 0
            boolean r2 = r1.isVoice()
            if (r2 == 0) goto L_0x094f
            r0 = 2131626346(0x7f0e096a, float:1.8879926E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x094f:
            boolean r2 = r1.isRoundVideo()
            if (r2 == 0) goto L_0x0966
            r0 = 2131626331(0x7f0e095b, float:1.8879895E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0966:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0afe
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x0974
            goto L_0x0afe
        L_0x0974:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x09bf
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x09ad
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09ad
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
            goto L_0x0418
        L_0x09ad:
            r3 = 0
            r0 = 2131626295(0x7f0e0937, float:1.8879822E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x09bf:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0aec
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x09c9
            goto L_0x0aec
        L_0x09c9:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x09df
            r0 = 2131626306(0x7f0e0942, float:1.8879844E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x09df:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0a06
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626292(0x7f0e0934, float:1.8879816E38)
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
            goto L_0x0418
        L_0x0a06:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0a40
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0a29
            r1 = 2131626328(0x7f0e0958, float:1.887989E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0418
        L_0x0a29:
            r2 = 2
            r4 = 0
            r1 = 2131626325(0x7f0e0955, float:1.8879883E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0418
        L_0x0a40:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0a87
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0a75
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0a75
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
            goto L_0x0418
        L_0x0a75:
            r3 = 0
            r0 = 2131626322(0x7f0e0952, float:1.8879877E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0a87:
            r3 = 0
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0a9d
            r0 = 2131626298(0x7f0e093a, float:1.8879828E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0a9d:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0ada
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0ada
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0aca
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r4 = 0
            java.lang.CharSequence r0 = r0.subSequence(r4, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0acb
        L_0x0aca:
            r4 = 0
        L_0x0acb:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            goto L_0x0418
        L_0x0ada:
            r4 = 0
            r0 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0aec:
            r4 = 0
            r0 = 2131626304(0x7f0e0940, float:1.887984E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0afe:
            r4 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0b19
            r1 = 2131626336(0x7f0e0960, float:1.8879905E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r2[r4] = r3
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0418
        L_0x0b19:
            r0 = 2131626334(0x7f0e095e, float:1.8879901E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0b2a:
            r4 = 0
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x0b43
            r0 = 2131626318(0x7f0e094e, float:1.8879869E38)
            r7 = 2
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0b43:
            r7 = 2
            boolean r8 = r1.isMusic()
            if (r8 == 0) goto L_0x0b5d
            r0 = 2131626315(0x7f0e094b, float:1.8879863E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0b5d:
            boolean r4 = r1.isVideo()
            r7 = 2131626339(0x7f0e0963, float:1.8879911E38)
            java.lang.String r8 = "NotificationActionPinnedText"
            if (r4 == 0) goto L_0x0bb2
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x0b9d
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0b9d
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
            goto L_0x0418
        L_0x0b9d:
            r2 = 0
            r3 = 2
            r0 = 2131626342(0x7f0e0966, float:1.8879917E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0bb2:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0CLASSNAME
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0bed
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0bed
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
            goto L_0x0418
        L_0x0bed:
            r2 = 0
            r4 = 2
            r0 = 2131626309(0x7f0e0945, float:1.887985E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0CLASSNAME:
            r2 = 0
            r4 = 2
            boolean r9 = r1.isVoice()
            if (r9 == 0) goto L_0x0c1d
            r0 = 2131626345(0x7f0e0969, float:1.8879924E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0c1d:
            boolean r9 = r1.isRoundVideo()
            if (r9 == 0) goto L_0x0CLASSNAME
            r0 = 2131626330(0x7f0e095a, float:1.8879893E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0CLASSNAME:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0dec
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x0CLASSNAME
            goto L_0x0dec
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x0CLASSNAME
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x0c7f
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0c7f
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
            goto L_0x0418
        L_0x0c7f:
            r2 = 0
            r3 = 2
            r0 = 2131626294(0x7f0e0936, float:1.887982E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0CLASSNAME:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0dd7
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x0c9e
            goto L_0x0dd7
        L_0x0c9e:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0cb7
            r0 = 2131626305(0x7f0e0941, float:1.8879842E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0cb7:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0ce0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626291(0x7f0e0933, float:1.8879814E38)
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
            goto L_0x0418
        L_0x0ce0:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0d1e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0d05
            r1 = 2131626327(0x7f0e0957, float:1.8879887E38)
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
            goto L_0x0418
        L_0x0d05:
            r3 = 0
            r4 = 2
            r1 = 2131626324(0x7f0e0954, float:1.887988E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0418
        L_0x0d1e:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0d6a
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0d55
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0d55
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
            goto L_0x0418
        L_0x0d55:
            r2 = 0
            r3 = 2
            r0 = 2131626321(0x7f0e0951, float:1.8879875E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0d6a:
            r2 = 0
            r3 = 2
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0d83
            r0 = 2131626297(0x7f0e0939, float:1.8879826E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0d83:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0dc2
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0dc2
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0db0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0db1
        L_0x0db0:
            r3 = 0
        L_0x0db1:
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r12
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r2 = 2
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x0418
        L_0x0dc2:
            r2 = 2
            r3 = 0
            r0 = 2131626318(0x7f0e094e, float:1.8879869E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0dd7:
            r2 = 2
            r3 = 0
            r0 = 2131626303(0x7f0e093f, float:1.8879838E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0dec:
            r3 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0e09
            r1 = 2131626335(0x7f0e095f, float:1.8879903E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0418
        L_0x0e09:
            r4 = 2
            r0 = 2131626333(0x7f0e095d, float:1.88799E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x0e1d:
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 == 0) goto L_0x145e
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x145f
        L_0x0e29:
            org.telegram.tgnet.TLRPC$Peer r1 = r4.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0e5d
            boolean r1 = r10.megagroup
            if (r1 != 0) goto L_0x0e5d
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0e4b
            r0 = 2131624775(0x7f0e0347, float:1.887674E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0e4b:
            r3 = 0
            r0 = 2131624741(0x7f0e0325, float:1.887667E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0e5d:
            r3 = 0
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0e78
            r0 = 2131626352(0x7f0e0970, float:1.8879938E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0e78:
            r1 = 2
            r0 = 2131626351(0x7f0e096f, float:1.8879936E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0e8c:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r1 == 0) goto L_0x1131
            boolean r1 = r10.megagroup
            if (r1 != 0) goto L_0x1131
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x0ecf
            if (r21 != 0) goto L_0x0ebf
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ebf
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x145f
        L_0x0ebf:
            r2 = 0
            r0 = 2131624725(0x7f0e0315, float:1.8876638E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0ecf:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0f1d
            if (r21 != 0) goto L_0x0f0d
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0f0d
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0f0d
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
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x145f
        L_0x0f0d:
            r2 = 0
            r0 = 2131624726(0x7f0e0316, float:1.887664E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0f1d:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x0f6b
            if (r21 != 0) goto L_0x0f5b
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0f5b
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0f5b
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
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r4] = r5
            goto L_0x145f
        L_0x0f5b:
            r4 = 0
            r0 = 2131624732(0x7f0e031c, float:1.8876652E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0f6b:
            r4 = 0
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x0var_
            r0 = 2131624717(0x7f0e030d, float:1.8876622E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0var_:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x0var_
            r0 = 2131624729(0x7f0e0319, float:1.8876646E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0var_:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x0fab
            r0 = 2131624724(0x7f0e0314, float:1.8876636E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x0fab:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r6 == 0) goto L_0x0fcf
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131624718(0x7f0e030e, float:1.8876624E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "ChannelMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x145f
        L_0x0fcf:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x1005
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0ff0
            r1 = 2131624728(0x7f0e0318, float:1.8876644E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0418
        L_0x0ff0:
            r2 = 2
            r3 = 0
            r1 = 2131624727(0x7f0e0317, float:1.8876642E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0418
        L_0x1005:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x1121
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x100f
            goto L_0x1121
        L_0x100f:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x1023
            r0 = 2131624722(0x7f0e0312, float:1.8876632E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x1023:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x10f3
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x10cb
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x1035
            goto L_0x10cb
        L_0x1035:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x1083
            if (r21 != 0) goto L_0x1073
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x1073
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1073
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
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r3] = r5
            goto L_0x145f
        L_0x1073:
            r3 = 0
            r0 = 2131624721(0x7f0e0311, float:1.887663E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r12
            java.lang.String r2 = "ChannelMessageGIF"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x1083:
            if (r21 != 0) goto L_0x10bb
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x10bb
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x10bb
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
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x145f
        L_0x10bb:
            r2 = 0
            r0 = 2131624719(0x7f0e030f, float:1.8876626E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x10cb:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x10e4
            r1 = 2131624731(0x7f0e031b, float:1.887665E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r12
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0418
        L_0x10e4:
            r0 = 2131624730(0x7f0e031a, float:1.8876648E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x10f3:
            r2 = 0
            if (r21 != 0) goto L_0x1112
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1112
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.CharSequence r0 = r0.messageText
            r1[r5] = r0
            r0 = 2131626415(0x7f0e09af, float:1.8880066E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x145f
        L_0x1112:
            r0 = 2131624725(0x7f0e0315, float:1.8876638E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x1121:
            r2 = 0
            r0 = 2131624723(0x7f0e0313, float:1.8876634E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x1131:
            boolean r1 = r20.isMediaEmpty()
            r4 = 2131626398(0x7f0e099e, float:1.8880031E38)
            java.lang.String r7 = "NotificationMessageGroupText"
            if (r1 == 0) goto L_0x1171
            if (r21 != 0) goto L_0x115e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x115e
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
            goto L_0x145f
        L_0x115e:
            r2 = 0
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626391(0x7f0e0997, float:1.8880017E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x145f
        L_0x1171:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x11c3
            if (r21 != 0) goto L_0x11ae
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x11ae
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x11ae
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
            goto L_0x145f
        L_0x11ae:
            r2 = 2
            r0 = 2131626392(0x7f0e0998, float:1.8880019E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x11c3:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x1215
            if (r21 != 0) goto L_0x1200
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x1200
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1200
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
            goto L_0x145f
        L_0x1200:
            r8 = 2
            r0 = 2131626399(0x7f0e099f, float:1.8880033E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r9 = 0
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = " "
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x1215:
            r8 = 2
            r9 = 0
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x1230
            r0 = 2131626381(0x7f0e098d, float:1.8879997E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x1230:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x1249
            r0 = 2131626395(0x7f0e099b, float:1.8880025E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x1249:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x1262
            r0 = 2131626390(0x7f0e0996, float:1.8880015E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x1262:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x128b
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131626382(0x7f0e098e, float:1.8879999E38)
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
            goto L_0x145f
        L_0x128b:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x12c9
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x12b0
            r1 = 2131626394(0x7f0e099a, float:1.8880023E38)
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
            goto L_0x0418
        L_0x12b0:
            r3 = 0
            r4 = 2
            r1 = 2131626393(0x7f0e0999, float:1.888002E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0418
        L_0x12c9:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x12e8
            r0 = 2131626384(0x7f0e0990, float:1.8880003E38)
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
            goto L_0x145f
        L_0x12e8:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x141b
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x12f2
            goto L_0x141b
        L_0x12f2:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x130b
            r0 = 2131626388(0x7f0e0994, float:1.888001E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x130b:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x13ec
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x13bb
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x131d
            goto L_0x13bb
        L_0x131d:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x136f
            if (r21 != 0) goto L_0x135a
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x135a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x135a
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
            goto L_0x145f
        L_0x135a:
            r2 = 2
            r0 = 2131626386(0x7f0e0992, float:1.8880007E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x136f:
            if (r21 != 0) goto L_0x13a6
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x13a6
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x13a6
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
            goto L_0x145f
        L_0x13a6:
            r2 = 2
            r0 = 2131626383(0x7f0e098f, float:1.888E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x13bb:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x13d8
            r1 = 2131626397(0x7f0e099d, float:1.888003E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r2] = r12
            java.lang.String r2 = r10.title
            r3[r5] = r2
            r4 = 2
            r3[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0418
        L_0x13d8:
            r4 = 2
            r0 = 2131626396(0x7f0e099c, float:1.8880027E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0418
        L_0x13ec:
            if (r21 != 0) goto L_0x1409
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1409
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.CharSequence r0 = r0.messageText
            r3 = 2
            r1[r3] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            goto L_0x145f
        L_0x1409:
            r2 = 0
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626391(0x7f0e0997, float:1.8880017E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x145f
        L_0x141b:
            r2 = 0
            r3 = 2
            r0 = 2131626389(0x7f0e0995, float:1.8880013E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x142f:
            r2 = 0
            if (r23 == 0) goto L_0x1434
            r23[r2] = r2
        L_0x1434:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r0 == 0) goto L_0x144c
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x144c
            r0 = 2131624725(0x7f0e0315, float:1.8876638E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x145f
        L_0x144c:
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626391(0x7f0e0997, float:1.8880017E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x145f
        L_0x145d:
            r1 = 0
        L_0x145e:
            r15 = r1
        L_0x145f:
            return r15
        L_0x1460:
            r0 = 2131628086(0x7f0e1036, float:1.8883455E38)
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$showNotifications$25 */
    public /* synthetic */ void lambda$showNotifications$25$NotificationsController() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$showNotifications$25$NotificationsController();
            }
        });
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$hideNotifications$26$NotificationsController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hideNotifications$26 */
    public /* synthetic */ void lambda$hideNotifications$26$NotificationsController() {
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
            AndroidUtilities.runOnUIThread($$Lambda$NotificationsController$y0viyyzHAD9iq9L5OFqN7FTVtM.INSTANCE);
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
                            NotificationsController.this.lambda$playInChatSound$29$NotificationsController();
                        }
                    });
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$playInChatSound$29 */
    public /* synthetic */ void lambda$playInChatSound$29$NotificationsController() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundPlay) > 500) {
            try {
                if (this.soundPool == null) {
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener($$Lambda$NotificationsController$mG32NHZQzHHl8cpcTQeV7RB5MW8.INSTANCE);
                }
                if (this.soundIn == 0 && !this.soundInLoaded) {
                    this.soundInLoaded = true;
                    this.soundIn = this.soundPool.load(ApplicationLoader.applicationContext, NUM, 1);
                }
                int i = this.soundIn;
                if (i != 0) {
                    try {
                        this.soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    static /* synthetic */ void lambda$null$28(SoundPool soundPool2, int i, int i2) {
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
                NotificationsController.this.lambda$repeatNotificationMaybe$30$NotificationsController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$repeatNotificationMaybe$30 */
    public /* synthetic */ void lambda$repeatNotificationMaybe$30$NotificationsController() {
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

    public void deleteNotificationChannel(long j) {
        deleteNotificationChannel(j, -1);
    }

    /* access modifiers changed from: private */
    /* renamed from: deleteNotificationChannelInternal */
    public void lambda$deleteNotificationChannel$31(long j, int i) {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
                SharedPreferences.Editor edit = notificationsSettings.edit();
                if (i == 0 || i == -1) {
                    String str = "org.telegram.key" + j;
                    String string = notificationsSettings.getString(str, (String) null);
                    if (string != null) {
                        edit.remove(str).remove(str + "_s");
                        try {
                            systemNotificationManager.deleteNotificationChannel(string);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel internal " + string);
                        }
                    }
                }
                if (i == 1 || i == -1) {
                    String str2 = "org.telegram.keyia" + j;
                    String string2 = notificationsSettings.getString(str2, (String) null);
                    if (string2 != null) {
                        edit.remove(str2).remove(str2 + "_s");
                        try {
                            systemNotificationManager.deleteNotificationChannel(string2);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel internal " + string2);
                        }
                    }
                }
                edit.commit();
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
            }
        }
    }

    public void deleteNotificationChannel(long j, int i) {
        if (Build.VERSION.SDK_INT >= 26) {
            notificationsQueue.postRunnable(new Runnable(j, i) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    NotificationsController.this.lambda$deleteNotificationChannel$31$NotificationsController(this.f$1, this.f$2);
                }
            });
        }
    }

    public void deleteNotificationChannelGlobal(int i) {
        deleteNotificationChannelGlobal(i, -1);
    }

    /* renamed from: deleteNotificationChannelGlobalInternal */
    public void lambda$deleteNotificationChannelGlobal$32(int i, int i2) {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
                SharedPreferences.Editor edit = notificationsSettings.edit();
                if (i2 == 0 || i2 == -1) {
                    String str = i == 2 ? "channels" : i == 0 ? "groups" : "private";
                    String string = notificationsSettings.getString(str, (String) null);
                    if (string != null) {
                        SharedPreferences.Editor remove = edit.remove(str);
                        remove.remove(str + "_s");
                        try {
                            systemNotificationManager.deleteNotificationChannel(string);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel global internal " + string);
                        }
                    }
                }
                if (i2 == 1 || i2 == -1) {
                    String str2 = i == 2 ? "channels_ia" : i == 0 ? "groups_ia" : "private_ia";
                    String string2 = notificationsSettings.getString(str2, (String) null);
                    if (string2 != null) {
                        SharedPreferences.Editor remove2 = edit.remove(str2);
                        remove2.remove(str2 + "_s");
                        try {
                            systemNotificationManager.deleteNotificationChannel(string2);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel global internal " + string2);
                        }
                    }
                }
                edit.remove(i == 2 ? "overwrite_channel" : i == 0 ? "overwrite_group" : "overwrite_private");
                edit.commit();
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
            }
        }
    }

    public void deleteNotificationChannelGlobal(int i, int i2) {
        if (Build.VERSION.SDK_INT >= 26) {
            notificationsQueue.postRunnable(new Runnable(i, i2) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NotificationsController.this.lambda$deleteNotificationChannelGlobal$32$NotificationsController(this.f$1, this.f$2);
                }
            });
        }
    }

    public void deleteAllNotificationChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            notificationsQueue.postRunnable(new Runnable() {
                public final void run() {
                    NotificationsController.this.lambda$deleteAllNotificationChannels$33$NotificationsController();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$deleteAllNotificationChannels$33 */
    public /* synthetic */ void lambda$deleteAllNotificationChannels$33$NotificationsController() {
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            Map<String, ?> all = notificationsSettings.getAll();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            for (Map.Entry next : all.entrySet()) {
                String str = (String) next.getKey();
                if (str.startsWith("org.telegram.key")) {
                    if (!str.endsWith("_s")) {
                        String str2 = (String) next.getValue();
                        systemNotificationManager.deleteNotificationChannel(str2);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete all channel " + str2);
                        }
                    }
                    edit.remove(str);
                }
            }
            edit.commit();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private boolean unsupportedNotificationShortcut() {
        return Build.VERSION.SDK_INT < 29 || !SharedConfig.chatBubbles;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x00a5 A[Catch:{ Exception -> 0x0112 }] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00ac A[Catch:{ Exception -> 0x0112 }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00cc A[Catch:{ Exception -> 0x0112 }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00cd A[Catch:{ Exception -> 0x0112 }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00df A[Catch:{ Exception -> 0x0112 }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00e7 A[Catch:{ Exception -> 0x0112 }] */
    @android.annotation.SuppressLint({"RestrictedApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String createNotificationShortcut(androidx.core.app.NotificationCompat.Builder r9, int r10, java.lang.String r11, org.telegram.tgnet.TLRPC$User r12, org.telegram.tgnet.TLRPC$Chat r13, androidx.core.app.Person r14) {
        /*
            r8 = this;
            boolean r0 = r8.unsupportedNotificationShortcut()
            r1 = 0
            if (r0 != 0) goto L_0x0116
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r13)
            if (r0 == 0) goto L_0x0013
            boolean r0 = r13.megagroup
            if (r0 != 0) goto L_0x0013
            goto L_0x0116
        L_0x0013:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0112 }
            r0.<init>()     // Catch:{ Exception -> 0x0112 }
            java.lang.String r2 = "ndid_"
            r0.append(r2)     // Catch:{ Exception -> 0x0112 }
            r0.append(r10)     // Catch:{ Exception -> 0x0112 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0112 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r2 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ Exception -> 0x0112 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0112 }
            r2.<init>((android.content.Context) r3, (java.lang.String) r0)     // Catch:{ Exception -> 0x0112 }
            if (r13 == 0) goto L_0x002f
            r13 = r11
            goto L_0x0033
        L_0x002f:
            java.lang.String r13 = org.telegram.messenger.UserObject.getFirstName(r12)     // Catch:{ Exception -> 0x0112 }
        L_0x0033:
            r2.setShortLabel(r13)     // Catch:{ Exception -> 0x0112 }
            r2.setLongLabel(r11)     // Catch:{ Exception -> 0x0112 }
            android.content.Intent r11 = new android.content.Intent     // Catch:{ Exception -> 0x0112 }
            java.lang.String r13 = "android.intent.action.VIEW"
            r11.<init>(r13)     // Catch:{ Exception -> 0x0112 }
            r2.setIntent(r11)     // Catch:{ Exception -> 0x0112 }
            r11 = 1
            r2.setLongLived(r11)     // Catch:{ Exception -> 0x0112 }
            if (r14 == 0) goto L_0x0062
            r2.setPerson(r14)     // Catch:{ Exception -> 0x0112 }
            androidx.core.graphics.drawable.IconCompat r13 = r14.getIcon()     // Catch:{ Exception -> 0x0112 }
            r2.setIcon(r13)     // Catch:{ Exception -> 0x0112 }
            androidx.core.graphics.drawable.IconCompat r13 = r14.getIcon()     // Catch:{ Exception -> 0x0112 }
            if (r13 == 0) goto L_0x0062
            androidx.core.graphics.drawable.IconCompat r13 = r14.getIcon()     // Catch:{ Exception -> 0x0112 }
            android.graphics.Bitmap r13 = r13.getBitmap()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0063
        L_0x0062:
            r13 = r1
        L_0x0063:
            java.util.ArrayList r14 = new java.util.ArrayList     // Catch:{ Exception -> 0x0112 }
            r14.<init>(r11)     // Catch:{ Exception -> 0x0112 }
            androidx.core.content.pm.ShortcutInfoCompat r2 = r2.build()     // Catch:{ Exception -> 0x0112 }
            r14.add(r2)     // Catch:{ Exception -> 0x0112 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0112 }
            androidx.core.content.pm.ShortcutManagerCompat.addDynamicShortcuts(r2, r14)     // Catch:{ Exception -> 0x0112 }
            r9.setShortcutId(r0)     // Catch:{ Exception -> 0x0112 }
            androidx.core.app.NotificationCompat$BubbleMetadata$Builder r14 = new androidx.core.app.NotificationCompat$BubbleMetadata$Builder     // Catch:{ Exception -> 0x0112 }
            r14.<init>()     // Catch:{ Exception -> 0x0112 }
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0112 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0112 }
            java.lang.Class<org.telegram.ui.BubbleActivity> r4 = org.telegram.ui.BubbleActivity.class
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0112 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0112 }
            r3.<init>()     // Catch:{ Exception -> 0x0112 }
            java.lang.String r4 = "com.tmessages.openchat"
            r3.append(r4)     // Catch:{ Exception -> 0x0112 }
            double r4 = java.lang.Math.random()     // Catch:{ Exception -> 0x0112 }
            r3.append(r4)     // Catch:{ Exception -> 0x0112 }
            r4 = 2147483647(0x7fffffff, float:NaN)
            r3.append(r4)     // Catch:{ Exception -> 0x0112 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0112 }
            r2.setAction(r3)     // Catch:{ Exception -> 0x0112 }
            if (r10 <= 0) goto L_0x00ac
            java.lang.String r3 = "userId"
            r2.putExtra(r3, r10)     // Catch:{ Exception -> 0x0112 }
            goto L_0x00b2
        L_0x00ac:
            java.lang.String r3 = "chatId"
            int r4 = -r10
            r2.putExtra(r3, r4)     // Catch:{ Exception -> 0x0112 }
        L_0x00b2:
            java.lang.String r3 = "currentAccount"
            int r4 = r8.currentAccount     // Catch:{ Exception -> 0x0112 }
            r2.putExtra(r3, r4)     // Catch:{ Exception -> 0x0112 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0112 }
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 0
            android.app.PendingIntent r2 = android.app.PendingIntent.getActivity(r3, r5, r2, r4)     // Catch:{ Exception -> 0x0112 }
            r14.setIntent(r2)     // Catch:{ Exception -> 0x0112 }
            long r2 = r8.opened_dialog_id     // Catch:{ Exception -> 0x0112 }
            long r6 = (long) r10     // Catch:{ Exception -> 0x0112 }
            int r10 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r10 != 0) goto L_0x00cd
            goto L_0x00ce
        L_0x00cd:
            r11 = 0
        L_0x00ce:
            r14.setSuppressNotification(r11)     // Catch:{ Exception -> 0x0112 }
            r14.setAutoExpandBubble(r5)     // Catch:{ Exception -> 0x0112 }
            r10 = 1142947840(0x44200000, float:640.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ Exception -> 0x0112 }
            r14.setDesiredHeight(r10)     // Catch:{ Exception -> 0x0112 }
            if (r13 == 0) goto L_0x00e7
            androidx.core.graphics.drawable.IconCompat r10 = androidx.core.graphics.drawable.IconCompat.createWithAdaptiveBitmap(r13)     // Catch:{ Exception -> 0x0112 }
            r14.setIcon(r10)     // Catch:{ Exception -> 0x0112 }
            goto L_0x010a
        L_0x00e7:
            if (r12 == 0) goto L_0x00fe
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0112 }
            boolean r11 = r12.bot     // Catch:{ Exception -> 0x0112 }
            if (r11 == 0) goto L_0x00f3
            r11 = 2131165288(0x7var_, float:1.7944789E38)
            goto L_0x00f6
        L_0x00f3:
            r11 = 2131165292(0x7var_c, float:1.7944797E38)
        L_0x00f6:
            androidx.core.graphics.drawable.IconCompat r10 = androidx.core.graphics.drawable.IconCompat.createWithResource(r10, r11)     // Catch:{ Exception -> 0x0112 }
            r14.setIcon(r10)     // Catch:{ Exception -> 0x0112 }
            goto L_0x010a
        L_0x00fe:
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0112 }
            r11 = 2131165290(0x7var_a, float:1.7944793E38)
            androidx.core.graphics.drawable.IconCompat r10 = androidx.core.graphics.drawable.IconCompat.createWithResource(r10, r11)     // Catch:{ Exception -> 0x0112 }
            r14.setIcon(r10)     // Catch:{ Exception -> 0x0112 }
        L_0x010a:
            androidx.core.app.NotificationCompat$BubbleMetadata r10 = r14.build()     // Catch:{ Exception -> 0x0112 }
            r9.setBubbleMetadata(r10)     // Catch:{ Exception -> 0x0112 }
            return r0
        L_0x0112:
            r9 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
        L_0x0116:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.createNotificationShortcut(androidx.core.app.NotificationCompat$Builder, int, java.lang.String, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, androidx.core.app.Person):java.lang.String");
    }

    /* access modifiers changed from: protected */
    @TargetApi(26)
    public void ensureGroupsCreated() {
        String str;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        if (this.groupsCreated == null) {
            this.groupsCreated = Boolean.valueOf(notificationsSettings.getBoolean("groupsCreated4", false));
        }
        if (!this.groupsCreated.booleanValue()) {
            try {
                String str2 = this.currentAccount + "channel";
                List<NotificationChannel> notificationChannels = systemNotificationManager.getNotificationChannels();
                int size = notificationChannels.size();
                SharedPreferences.Editor editor = null;
                for (int i = 0; i < size; i++) {
                    NotificationChannel notificationChannel = notificationChannels.get(i);
                    String id = notificationChannel.getId();
                    if (id.startsWith(str2)) {
                        int importance = notificationChannel.getImportance();
                        if (!(importance == 4 || importance == 5)) {
                            if (!id.contains("_ia_")) {
                                if (id.contains("_channels_")) {
                                    if (editor == null) {
                                        editor = getAccountInstance().getNotificationsSettings().edit();
                                    }
                                    editor.remove("priority_channel").remove("vibrate_channel").remove("ChannelSoundPath").remove("ChannelSound");
                                } else if (id.contains("_groups_")) {
                                    if (editor == null) {
                                        editor = getAccountInstance().getNotificationsSettings().edit();
                                    }
                                    editor.remove("priority_group").remove("vibrate_group").remove("GroupSoundPath").remove("GroupSound");
                                } else if (id.contains("_private_")) {
                                    if (editor == null) {
                                        editor = getAccountInstance().getNotificationsSettings().edit();
                                    }
                                    editor.remove("priority_messages");
                                    editor.remove("priority_group").remove("vibrate_messages").remove("GlobalSoundPath").remove("GlobalSound");
                                } else {
                                    long longValue = Utilities.parseLong(id.substring(9, id.indexOf(95, 9))).longValue();
                                    if (longValue != 0) {
                                        if (editor == null) {
                                            editor = getAccountInstance().getNotificationsSettings().edit();
                                        }
                                        editor.remove("priority_" + longValue).remove("vibrate_" + longValue).remove("sound_path_" + longValue).remove("sound_" + longValue);
                                    }
                                }
                            }
                        }
                        systemNotificationManager.deleteNotificationChannel(id);
                    }
                }
                if (editor != null) {
                    editor.commit();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                getUserConfig().getCurrentUser();
            }
            if (user != null) {
                str = " (" + ContactsController.formatName(user.first_name, user.last_name) + ")";
            } else {
                str = "";
            }
            systemNotificationManager.createNotificationChannelGroups(Arrays.asList(new NotificationChannelGroup[]{new NotificationChannelGroup("channels" + this.currentAccount, LocaleController.getString("NotificationsChannels", NUM) + str), new NotificationChannelGroup("groups" + this.currentAccount, LocaleController.getString("NotificationsGroups", NUM) + str), new NotificationChannelGroup("private" + this.currentAccount, LocaleController.getString("NotificationsPrivateChats", NUM) + str), new NotificationChannelGroup("other" + this.currentAccount, LocaleController.getString("NotificationsOther", NUM) + str)}));
            notificationsSettings.edit().putBoolean("groupsCreated4", true).commit();
            this.groupsCreated = Boolean.TRUE;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:166:0x03a5  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x03f6  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x03fc  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0435  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x04f2 A[LOOP:1: B:225:0x04ef->B:227:0x04f2, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0505  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x0553  */
    @android.annotation.TargetApi(26)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String validateChannelId(long r29, java.lang.String r31, long[] r32, int r33, android.net.Uri r34, int r35, boolean r36, boolean r37, boolean r38, int r39) {
        /*
            r28 = this;
            r1 = r28
            r2 = r29
            r4 = r35
            r0 = r39
            r28.ensureGroupsCreated()
            org.telegram.messenger.AccountInstance r5 = r28.getAccountInstance()
            android.content.SharedPreferences r5 = r5.getNotificationsSettings()
            java.lang.String r6 = "groups"
            java.lang.String r7 = "private"
            java.lang.String r8 = "channels"
            r10 = 2
            if (r38 == 0) goto L_0x0031
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "other"
            r11.append(r12)
            int r12 = r1.currentAccount
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            r12 = 0
            goto L_0x0070
        L_0x0031:
            if (r0 != r10) goto L_0x0047
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r8)
            int r12 = r1.currentAccount
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            java.lang.String r12 = "overwrite_channel"
            goto L_0x0070
        L_0x0047:
            if (r0 != 0) goto L_0x005d
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r6)
            int r12 = r1.currentAccount
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            java.lang.String r12 = "overwrite_group"
            goto L_0x0070
        L_0x005d:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r7)
            int r12 = r1.currentAccount
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            java.lang.String r12 = "overwrite_private"
        L_0x0070:
            r13 = 1
            r14 = 0
            if (r36 != 0) goto L_0x0079
            int r15 = (int) r2
            if (r15 != 0) goto L_0x0079
            r15 = 1
            goto L_0x007a
        L_0x0079:
            r15 = 0
        L_0x007a:
            if (r37 != 0) goto L_0x0086
            if (r12 == 0) goto L_0x0086
            boolean r12 = r5.getBoolean(r12, r14)
            if (r12 == 0) goto L_0x0086
            r12 = 1
            goto L_0x0087
        L_0x0086:
            r12 = 0
        L_0x0087:
            if (r38 == 0) goto L_0x0096
            r6 = 2131626465(0x7f0e09e1, float:1.8880167E38)
            java.lang.String r7 = "NotificationsSilent"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.String r7 = "silent"
        L_0x0094:
            r8 = 0
            goto L_0x00e9
        L_0x0096:
            if (r36 == 0) goto L_0x00bf
            if (r37 == 0) goto L_0x00a0
            r9 = 2131626443(0x7f0e09cb, float:1.8880122E38)
            java.lang.String r14 = "NotificationsInAppDefault"
            goto L_0x00a5
        L_0x00a0:
            r9 = 2131626427(0x7f0e09bb, float:1.888009E38)
            java.lang.String r14 = "NotificationsDefault"
        L_0x00a5:
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            if (r0 != r10) goto L_0x00b2
            if (r37 == 0) goto L_0x00af
            java.lang.String r8 = "channels_ia"
        L_0x00af:
            r7 = r8
        L_0x00b0:
            r6 = r9
            goto L_0x0094
        L_0x00b2:
            if (r0 != 0) goto L_0x00ba
            if (r37 == 0) goto L_0x00b8
            java.lang.String r6 = "groups_ia"
        L_0x00b8:
            r7 = r6
            goto L_0x00b0
        L_0x00ba:
            if (r37 == 0) goto L_0x00b0
            java.lang.String r7 = "private_ia"
            goto L_0x00b0
        L_0x00bf:
            if (r37 == 0) goto L_0x00d0
            r6 = 2131626424(0x7f0e09b8, float:1.8880084E38)
            java.lang.Object[] r7 = new java.lang.Object[r13]
            r8 = 0
            r7[r8] = r31
            java.lang.String r8 = "NotificationsChatInApp"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r8, r6, r7)
            goto L_0x00d2
        L_0x00d0:
            r6 = r31
        L_0x00d2:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            if (r37 == 0) goto L_0x00dc
            java.lang.String r8 = "org.telegram.keyia"
            goto L_0x00de
        L_0x00dc:
            java.lang.String r8 = "org.telegram.key"
        L_0x00de:
            r7.append(r8)
            r7.append(r2)
            java.lang.String r7 = r7.toString()
            goto L_0x0094
        L_0x00e9:
            java.lang.String r9 = r5.getString(r7, r8)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r7)
            java.lang.String r13 = "_s"
            r14.append(r13)
            java.lang.String r14 = r14.toString()
            java.lang.String r14 = r5.getString(r14, r8)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r10 = "secret"
            r31 = r6
            if (r9 == 0) goto L_0x0480
            android.app.NotificationManager r6 = systemNotificationManager
            android.app.NotificationChannel r6 = r6.getNotificationChannel(r9)
            boolean r16 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r17 = r11
            java.lang.String r11 = " = "
            if (r16 == 0) goto L_0x013a
            r16 = r13
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r18 = r7
            java.lang.String r7 = "current channel for "
            r13.append(r7)
            r13.append(r9)
            r13.append(r11)
            r13.append(r6)
            java.lang.String r7 = r13.toString()
            org.telegram.messenger.FileLog.d(r7)
            goto L_0x013e
        L_0x013a:
            r18 = r7
            r16 = r13
        L_0x013e:
            if (r6 == 0) goto L_0x046d
            if (r38 != 0) goto L_0x045c
            if (r12 != 0) goto L_0x045c
            int r7 = r6.getImportance()
            android.net.Uri r13 = r6.getSound()
            long[] r19 = r6.getVibrationPattern()
            r20 = r12
            boolean r12 = r6.shouldVibrate()
            if (r12 != 0) goto L_0x0163
            if (r19 != 0) goto L_0x0163
            r21 = r12
            r12 = 2
            long[] r4 = new long[r12]
            r4 = {0, 0} // fill-array
            goto L_0x0167
        L_0x0163:
            r21 = r12
            r4 = r19
        L_0x0167:
            int r6 = r6.getLightColor()
            if (r4 == 0) goto L_0x0179
            r12 = 0
        L_0x016e:
            int r2 = r4.length
            if (r12 >= r2) goto L_0x0179
            r2 = r4[r12]
            r8.append(r2)
            int r12 = r12 + 1
            goto L_0x016e
        L_0x0179:
            r8.append(r6)
            if (r13 == 0) goto L_0x0185
            java.lang.String r2 = r13.toString()
            r8.append(r2)
        L_0x0185:
            r8.append(r7)
            if (r36 != 0) goto L_0x018f
            if (r15 == 0) goto L_0x018f
            r8.append(r10)
        L_0x018f:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x01b5
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "current channel settings for "
            r2.append(r3)
            r2.append(r9)
            r2.append(r11)
            r2.append(r8)
            java.lang.String r3 = " old = "
            r2.append(r3)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x01b5:
            java.lang.String r2 = r8.toString()
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            r3 = 0
            r8.setLength(r3)
            boolean r3 = r2.equals(r14)
            if (r3 != 0) goto L_0x0439
            java.lang.String r3 = "notify2_"
            if (r7 != 0) goto L_0x020b
            android.content.SharedPreferences$Editor r7 = r5.edit()
            if (r36 == 0) goto L_0x01e7
            if (r37 != 0) goto L_0x01e0
            java.lang.String r3 = getGlobalNotificationsKey(r39)
            r11 = 2147483647(0x7fffffff, float:NaN)
            r7.putInt(r3, r11)
            r1.updateServerNotificationsSettings((int) r0)
        L_0x01e0:
            r12 = r9
            r19 = r10
            r11 = 1
            r9 = r29
            goto L_0x0203
        L_0x01e7:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r3)
            r12 = r9
            r19 = r10
            r9 = r29
            r11.append(r9)
            java.lang.String r3 = r11.toString()
            r11 = 2
            r7.putInt(r3, r11)
            r11 = 1
            r1.updateServerNotificationsSettings(r9, r11)
        L_0x0203:
            r11 = r35
            r22 = r2
            r23 = r4
            goto L_0x0299
        L_0x020b:
            r11 = r35
            r12 = r9
            r19 = r10
            r9 = r29
            r22 = r2
            if (r7 == r11) goto L_0x029b
            if (r37 != 0) goto L_0x0295
            android.content.SharedPreferences$Editor r2 = r5.edit()
            r23 = r4
            r4 = 4
            if (r7 == r4) goto L_0x0232
            r4 = 5
            if (r7 != r4) goto L_0x0225
            goto L_0x0232
        L_0x0225:
            r4 = 1
            if (r7 != r4) goto L_0x022b
            r4 = 2
            r7 = 4
            goto L_0x0234
        L_0x022b:
            r4 = 2
            if (r7 != r4) goto L_0x0230
            r7 = 5
            goto L_0x0234
        L_0x0230:
            r7 = 0
            goto L_0x0234
        L_0x0232:
            r4 = 2
            r7 = 1
        L_0x0234:
            if (r36 == 0) goto L_0x0259
            java.lang.String r3 = getGlobalNotificationsKey(r39)
            r4 = 0
            android.content.SharedPreferences$Editor r3 = r2.putInt(r3, r4)
            r3.commit()
            r3 = 2
            if (r0 != r3) goto L_0x024b
            java.lang.String r3 = "priority_channel"
            r2.putInt(r3, r7)
            goto L_0x0298
        L_0x024b:
            if (r0 != 0) goto L_0x0253
            java.lang.String r3 = "priority_group"
            r2.putInt(r3, r7)
            goto L_0x0298
        L_0x0253:
            java.lang.String r3 = "priority_messages"
            r2.putInt(r3, r7)
            goto L_0x0298
        L_0x0259:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r9)
            java.lang.String r3 = r4.toString()
            r4 = 0
            r2.putInt(r3, r4)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "notifyuntil_"
            r3.append(r4)
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            r2.remove(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "priority_"
            r3.append(r4)
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            r2.putInt(r3, r7)
            goto L_0x0298
        L_0x0295:
            r23 = r4
            r2 = 0
        L_0x0298:
            r7 = r2
        L_0x0299:
            r2 = 1
            goto L_0x029f
        L_0x029b:
            r23 = r4
            r2 = 0
            r7 = 0
        L_0x029f:
            if (r13 != 0) goto L_0x02a3
            if (r34 != 0) goto L_0x02b5
        L_0x02a3:
            if (r13 == 0) goto L_0x0391
            if (r34 == 0) goto L_0x02b5
            java.lang.String r3 = r13.toString()
            java.lang.String r4 = r34.toString()
            boolean r3 = android.text.TextUtils.equals(r3, r4)
            if (r3 != 0) goto L_0x0391
        L_0x02b5:
            if (r37 != 0) goto L_0x0385
            if (r7 != 0) goto L_0x02bd
            android.content.SharedPreferences$Editor r7 = r5.edit()
        L_0x02bd:
            java.lang.String r2 = "GroupSound"
            java.lang.String r3 = "GlobalSound"
            java.lang.String r4 = "ChannelSound"
            r24 = r12
            java.lang.String r12 = "sound_"
            r25 = r14
            java.lang.String r14 = "NoSound"
            if (r13 != 0) goto L_0x02f9
            if (r36 == 0) goto L_0x02e2
            r26 = r15
            r15 = 2
            if (r0 != r15) goto L_0x02d8
            r7.putString(r4, r14)
            goto L_0x02f6
        L_0x02d8:
            if (r0 != 0) goto L_0x02de
            r7.putString(r2, r14)
            goto L_0x02f6
        L_0x02de:
            r7.putString(r3, r14)
            goto L_0x02f6
        L_0x02e2:
            r26 = r15
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r12)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r7.putString(r2, r14)
        L_0x02f6:
            r27 = r13
            goto L_0x0357
        L_0x02f9:
            r26 = r15
            java.lang.String r14 = r13.toString()
            android.content.Context r15 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.media.Ringtone r15 = android.media.RingtoneManager.getRingtone(r15, r13)
            if (r15 == 0) goto L_0x0329
            r34 = r14
            android.net.Uri r14 = android.provider.Settings.System.DEFAULT_RINGTONE_URI
            boolean r14 = r13.equals(r14)
            if (r14 == 0) goto L_0x031d
            r14 = 2131625075(0x7f0e0473, float:1.8877348E38)
            r27 = r13
            java.lang.String r13 = "DefaultRingtone"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r14)
            goto L_0x0325
        L_0x031d:
            r27 = r13
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r13 = r15.getTitle(r13)
        L_0x0325:
            r15.stop()
            goto L_0x032e
        L_0x0329:
            r27 = r13
            r34 = r14
            r13 = 0
        L_0x032e:
            if (r13 == 0) goto L_0x0355
            if (r36 == 0) goto L_0x0343
            r14 = 2
            if (r0 != r14) goto L_0x0339
            r7.putString(r4, r13)
            goto L_0x0355
        L_0x0339:
            if (r0 != 0) goto L_0x033f
            r7.putString(r2, r13)
            goto L_0x0355
        L_0x033f:
            r7.putString(r3, r13)
            goto L_0x0355
        L_0x0343:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r12)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r7.putString(r2, r13)
        L_0x0355:
            r14 = r34
        L_0x0357:
            if (r36 == 0) goto L_0x0370
            r2 = 2
            if (r0 != r2) goto L_0x0362
            java.lang.String r2 = "ChannelSoundPath"
            r7.putString(r2, r14)
            goto L_0x038d
        L_0x0362:
            if (r0 != 0) goto L_0x036a
            java.lang.String r2 = "GroupSoundPath"
            r7.putString(r2, r14)
            goto L_0x038d
        L_0x036a:
            java.lang.String r2 = "GlobalSoundPath"
            r7.putString(r2, r14)
            goto L_0x038d
        L_0x0370:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "sound_path_"
            r2.append(r3)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r7.putString(r2, r14)
            goto L_0x038d
        L_0x0385:
            r24 = r12
            r27 = r13
            r25 = r14
            r26 = r15
        L_0x038d:
            r3 = r32
            r2 = 1
            goto L_0x039b
        L_0x0391:
            r24 = r12
            r25 = r14
            r26 = r15
            r3 = r32
            r27 = r34
        L_0x039b:
            boolean r4 = r1.isEmptyVibration(r3)
            r12 = 1
            r4 = r4 ^ r12
            r12 = r21
            if (r4 == r12) goto L_0x03f6
            if (r37 != 0) goto L_0x03f2
            if (r7 != 0) goto L_0x03ad
            android.content.SharedPreferences$Editor r7 = r5.edit()
        L_0x03ad:
            if (r36 == 0) goto L_0x03d8
            r2 = 2
            if (r0 != r2) goto L_0x03be
            if (r12 == 0) goto L_0x03b6
            r2 = 0
            goto L_0x03b7
        L_0x03b6:
            r2 = 2
        L_0x03b7:
            java.lang.String r3 = "vibrate_channel"
            r7.putInt(r3, r2)
            goto L_0x03f2
        L_0x03be:
            if (r0 != 0) goto L_0x03cc
            if (r12 == 0) goto L_0x03c4
            r2 = 0
            goto L_0x03c5
        L_0x03c4:
            r2 = 2
        L_0x03c5:
            java.lang.String r3 = "vibrate_group"
            r7.putInt(r3, r2)
            goto L_0x03f2
        L_0x03cc:
            if (r12 == 0) goto L_0x03d0
            r2 = 0
            goto L_0x03d1
        L_0x03d0:
            r2 = 2
        L_0x03d1:
            java.lang.String r3 = "vibrate_messages"
            r7.putInt(r3, r2)
            goto L_0x03f2
        L_0x03d8:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "vibrate_"
            r2.append(r3)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            if (r12 == 0) goto L_0x03ee
            r3 = 0
            goto L_0x03ef
        L_0x03ee:
            r3 = 2
        L_0x03ef:
            r7.putInt(r2, r3)
        L_0x03f2:
            r4 = r33
            r2 = 1
            goto L_0x03fa
        L_0x03f6:
            r4 = r33
            r23 = r3
        L_0x03fa:
            if (r6 == r4) goto L_0x0433
            if (r37 != 0) goto L_0x0431
            if (r7 != 0) goto L_0x0404
            android.content.SharedPreferences$Editor r7 = r5.edit()
        L_0x0404:
            if (r36 == 0) goto L_0x041d
            r2 = 2
            if (r0 != r2) goto L_0x040f
            java.lang.String r0 = "ChannelLed"
            r7.putInt(r0, r6)
            goto L_0x0431
        L_0x040f:
            if (r0 != 0) goto L_0x0417
            java.lang.String r0 = "GroupLed"
            r7.putInt(r0, r6)
            goto L_0x0431
        L_0x0417:
            java.lang.String r0 = "MessagesLed"
            r7.putInt(r0, r6)
            goto L_0x0431
        L_0x041d:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "color_"
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            r7.putInt(r0, r6)
        L_0x0431:
            r4 = r6
            r2 = 1
        L_0x0433:
            if (r7 == 0) goto L_0x0450
            r7.commit()
            goto L_0x0450
        L_0x0439:
            r3 = r32
            r4 = r33
            r11 = r35
            r22 = r2
            r24 = r9
            r19 = r10
            r25 = r14
            r26 = r15
            r9 = r29
            r27 = r34
            r23 = r3
            r2 = 0
        L_0x0450:
            r0 = r2
            r6 = r22
            r3 = r23
            r7 = r24
            r12 = r25
            r2 = r27
            goto L_0x049e
        L_0x045c:
            r11 = r4
            r24 = r9
            r19 = r10
            r20 = r12
            r25 = r14
            r26 = r15
            r4 = r33
            r9 = r2
            r3 = r32
            goto L_0x0496
        L_0x046d:
            r11 = r4
            r19 = r10
            r20 = r12
            r26 = r15
            r4 = r33
            r9 = r2
            r3 = r32
            r2 = r34
            r0 = 0
            r6 = 0
            r7 = 0
            r12 = 0
            goto L_0x049e
        L_0x0480:
            r18 = r7
            r24 = r9
            r19 = r10
            r17 = r11
            r20 = r12
            r16 = r13
            r25 = r14
            r26 = r15
            r9 = r2
            r11 = r4
            r3 = r32
            r4 = r33
        L_0x0496:
            r2 = r34
            r7 = r24
            r12 = r25
            r0 = 0
            r6 = 0
        L_0x049e:
            if (r0 == 0) goto L_0x04dd
            if (r6 == 0) goto L_0x04dd
            android.content.SharedPreferences$Editor r0 = r5.edit()
            r13 = r18
            android.content.SharedPreferences$Editor r0 = r0.putString(r13, r7)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r13)
            r14 = r16
            r8.append(r14)
            java.lang.String r8 = r8.toString()
            android.content.SharedPreferences$Editor r0 = r0.putString(r8, r6)
            r0.commit()
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x04ea
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "change edited channel "
            r0.append(r8)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x04ea
        L_0x04dd:
            r14 = r16
            r13 = r18
            if (r20 != 0) goto L_0x04ee
            if (r6 == 0) goto L_0x04ee
            if (r37 == 0) goto L_0x04ee
            if (r36 != 0) goto L_0x04ea
            goto L_0x04ee
        L_0x04ea:
            r8 = r7
            r16 = r14
            goto L_0x0551
        L_0x04ee:
            r0 = 0
        L_0x04ef:
            int r6 = r3.length
            if (r0 >= r6) goto L_0x04fe
            r16 = r14
            r14 = r3[r0]
            r8.append(r14)
            int r0 = r0 + 1
            r14 = r16
            goto L_0x04ef
        L_0x04fe:
            r16 = r14
            r8.append(r4)
            if (r2 == 0) goto L_0x050c
            java.lang.String r0 = r2.toString()
            r8.append(r0)
        L_0x050c:
            r8.append(r11)
            if (r36 != 0) goto L_0x0518
            if (r26 == 0) goto L_0x0518
            r0 = r19
            r8.append(r0)
        L_0x0518:
            java.lang.String r0 = r8.toString()
            java.lang.String r6 = org.telegram.messenger.Utilities.MD5(r0)
            if (r38 != 0) goto L_0x0550
            if (r7 == 0) goto L_0x0550
            if (r20 != 0) goto L_0x052c
            boolean r0 = r12.equals(r6)
            if (r0 != 0) goto L_0x0550
        L_0x052c:
            android.app.NotificationManager r0 = systemNotificationManager     // Catch:{ Exception -> 0x0532 }
            r0.deleteNotificationChannel(r7)     // Catch:{ Exception -> 0x0532 }
            goto L_0x0536
        L_0x0532:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0536:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x054e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "delete channel by settings change "
            r0.append(r8)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x054e:
            r8 = 0
            goto L_0x0551
        L_0x0550:
            r8 = r7
        L_0x0551:
            if (r8 != 0) goto L_0x0638
            java.lang.String r0 = "_"
            java.lang.String r7 = "channel_"
            if (r36 == 0) goto L_0x057a
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            int r9 = r1.currentAccount
            r8.append(r9)
            r8.append(r7)
            r8.append(r13)
            r8.append(r0)
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r9 = r0.nextLong()
            r8.append(r9)
            java.lang.String r0 = r8.toString()
            goto L_0x059a
        L_0x057a:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            int r12 = r1.currentAccount
            r8.append(r12)
            r8.append(r7)
            r8.append(r9)
            r8.append(r0)
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r9 = r0.nextLong()
            r8.append(r9)
            java.lang.String r0 = r8.toString()
        L_0x059a:
            r8 = r0
            android.app.NotificationChannel r0 = new android.app.NotificationChannel
            if (r26 == 0) goto L_0x05a9
            r7 = 2131627267(0x7f0e0d03, float:1.8881794E38)
            java.lang.String r9 = "SecretChatName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            goto L_0x05ab
        L_0x05a9:
            r7 = r31
        L_0x05ab:
            r0.<init>(r8, r7, r11)
            r11 = r17
            r0.setGroup(r11)
            if (r4 == 0) goto L_0x05be
            r7 = 1
            r0.enableLights(r7)
            r0.setLightColor(r4)
            r4 = 0
            goto L_0x05c3
        L_0x05be:
            r4 = 0
            r7 = 1
            r0.enableLights(r4)
        L_0x05c3:
            boolean r9 = r1.isEmptyVibration(r3)
            if (r9 != 0) goto L_0x05d3
            r0.enableVibration(r7)
            int r4 = r3.length
            if (r4 <= 0) goto L_0x05d6
            r0.setVibrationPattern(r3)
            goto L_0x05d6
        L_0x05d3:
            r0.enableVibration(r4)
        L_0x05d6:
            android.media.AudioAttributes$Builder r3 = new android.media.AudioAttributes$Builder
            r3.<init>()
            r4 = 4
            r3.setContentType(r4)
            r4 = 5
            r3.setUsage(r4)
            if (r2 == 0) goto L_0x05ed
            android.media.AudioAttributes r3 = r3.build()
            r0.setSound(r2, r3)
            goto L_0x05f5
        L_0x05ed:
            android.media.AudioAttributes r2 = r3.build()
            r3 = 0
            r0.setSound(r3, r2)
        L_0x05f5:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x060d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "create new channel "
            r2.append(r3)
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x060d:
            long r2 = android.os.SystemClock.elapsedRealtime()
            r1.lastNotificationChannelCreateTime = r2
            android.app.NotificationManager r2 = systemNotificationManager
            r2.createNotificationChannel(r0)
            android.content.SharedPreferences$Editor r0 = r5.edit()
            android.content.SharedPreferences$Editor r0 = r0.putString(r13, r8)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r13)
            r3 = r16
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.content.SharedPreferences$Editor r0 = r0.putString(r2, r6)
            r0.commit()
        L_0x0638:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX WARNING: type inference failed for: r2v88 */
    /* JADX WARNING: type inference failed for: r2v89 */
    /* JADX WARNING: type inference failed for: r2v103 */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x07b5, code lost:
        if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x07b7;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:378:0x0864 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0204 A[SYNTHETIC, Splitter:B:101:0x0204] */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0276 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0326 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0404 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0407 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0420 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0496 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x049c A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04f4 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x053f A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x0543 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x054d A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x0551 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x0555 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0576 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x059d  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x05b7 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x05bc A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x05ed A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x066d A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x0721 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x076b  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x0777  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x07af A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x07be A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x0876 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0880 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0885 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0893 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0904 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x098d A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x09b7 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x09d0 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c1 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ca A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00d1 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00e0 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00e3 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00ed A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00fa A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0110 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0116 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x012c A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0145 A[SYNTHETIC, Splitter:B:80:0x0145] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0179 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0185 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x019e A[SYNTHETIC, Splitter:B:95:0x019e] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01b6 A[Catch:{ Exception -> 0x0a03 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r41) {
        /*
            r40 = this;
            r15 = r40
            java.lang.String r1 = "currentAccount"
            org.telegram.messenger.UserConfig r0 = r40.getUserConfig()
            boolean r0 = r0.isClientActivated()
            if (r0 == 0) goto L_0x0a08
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r15.pushMessages
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0a08
            boolean r0 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r0 != 0) goto L_0x0022
            int r0 = r15.currentAccount
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            if (r0 == r2) goto L_0x0022
            goto L_0x0a08
        L_0x0022:
            org.telegram.tgnet.ConnectionsManager r0 = r40.getConnectionsManager()     // Catch:{ Exception -> 0x0a03 }
            r0.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0a03 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r15.pushMessages     // Catch:{ Exception -> 0x0a03 }
            r2 = 0
            java.lang.Object r0 = r0.get(r2)     // Catch:{ Exception -> 0x0a03 }
            r3 = r0
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3     // Catch:{ Exception -> 0x0a03 }
            org.telegram.messenger.AccountInstance r0 = r40.getAccountInstance()     // Catch:{ Exception -> 0x0a03 }
            android.content.SharedPreferences r4 = r0.getNotificationsSettings()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r0 = "dismissDate"
            int r0 = r4.getInt(r0, r2)     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner     // Catch:{ Exception -> 0x0a03 }
            int r5 = r5.date     // Catch:{ Exception -> 0x0a03 }
            if (r5 > r0) goto L_0x004b
            r40.dismissNotification()     // Catch:{ Exception -> 0x0a03 }
            return
        L_0x004b:
            long r5 = r3.getDialogId()     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner     // Catch:{ Exception -> 0x0a03 }
            boolean r7 = r7.mentioned     // Catch:{ Exception -> 0x0a03 }
            if (r7 == 0) goto L_0x005b
            int r7 = r3.getFromChatId()     // Catch:{ Exception -> 0x0a03 }
            long r7 = (long) r7     // Catch:{ Exception -> 0x0a03 }
            goto L_0x005c
        L_0x005b:
            r7 = r5
        L_0x005c:
            r3.getId()     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$Message r9 = r3.messageOwner     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$Peer r9 = r9.peer_id     // Catch:{ Exception -> 0x0a03 }
            int r10 = r9.chat_id     // Catch:{ Exception -> 0x0a03 }
            if (r10 == 0) goto L_0x0068
            goto L_0x006a
        L_0x0068:
            int r10 = r9.channel_id     // Catch:{ Exception -> 0x0a03 }
        L_0x006a:
            int r9 = r9.user_id     // Catch:{ Exception -> 0x0a03 }
            boolean r11 = r3.isFromUser()     // Catch:{ Exception -> 0x0a03 }
            if (r11 == 0) goto L_0x0084
            if (r9 == 0) goto L_0x007e
            org.telegram.messenger.UserConfig r11 = r40.getUserConfig()     // Catch:{ Exception -> 0x0a03 }
            int r11 = r11.getClientUserId()     // Catch:{ Exception -> 0x0a03 }
            if (r9 != r11) goto L_0x0084
        L_0x007e:
            org.telegram.tgnet.TLRPC$Message r9 = r3.messageOwner     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$Peer r9 = r9.from_id     // Catch:{ Exception -> 0x0a03 }
            int r9 = r9.user_id     // Catch:{ Exception -> 0x0a03 }
        L_0x0084:
            org.telegram.messenger.MessagesController r11 = r40.getMessagesController()     // Catch:{ Exception -> 0x0a03 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$User r11 = r11.getUser(r12)     // Catch:{ Exception -> 0x0a03 }
            if (r10 == 0) goto L_0x00b5
            org.telegram.messenger.MessagesController r14 = r40.getMessagesController()     // Catch:{ Exception -> 0x0a03 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$Chat r12 = r14.getChat(r12)     // Catch:{ Exception -> 0x0a03 }
            if (r12 != 0) goto L_0x00a9
            boolean r14 = r3.isFcmMessage()     // Catch:{ Exception -> 0x0a03 }
            if (r14 == 0) goto L_0x00a9
            boolean r14 = r3.localChannel     // Catch:{ Exception -> 0x0a03 }
            goto L_0x00b7
        L_0x00a9:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r12)     // Catch:{ Exception -> 0x0a03 }
            if (r14 == 0) goto L_0x00b6
            boolean r14 = r12.megagroup     // Catch:{ Exception -> 0x0a03 }
            if (r14 != 0) goto L_0x00b6
            r14 = 1
            goto L_0x00b7
        L_0x00b5:
            r12 = 0
        L_0x00b6:
            r14 = 0
        L_0x00b7:
            int r2 = r15.getNotifyOverride(r4, r7)     // Catch:{ Exception -> 0x0a03 }
            r13 = -1
            r18 = r1
            r1 = 2
            if (r2 != r13) goto L_0x00ca
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r14)     // Catch:{ Exception -> 0x0a03 }
            boolean r2 = r15.isGlobalNotificationsEnabled(r5, r2)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x00cf
        L_0x00ca:
            if (r2 == r1) goto L_0x00ce
            r2 = 1
            goto L_0x00cf
        L_0x00ce:
            r2 = 0
        L_0x00cf:
            if (r10 == 0) goto L_0x00d3
            if (r12 == 0) goto L_0x00d5
        L_0x00d3:
            if (r11 != 0) goto L_0x00de
        L_0x00d5:
            boolean r19 = r3.isFcmMessage()     // Catch:{ Exception -> 0x0a03 }
            if (r19 == 0) goto L_0x00de
            java.lang.String r13 = r3.localName     // Catch:{ Exception -> 0x0a03 }
            goto L_0x00e7
        L_0x00de:
            if (r12 == 0) goto L_0x00e3
            java.lang.String r13 = r12.title     // Catch:{ Exception -> 0x0a03 }
            goto L_0x00e7
        L_0x00e3:
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r11)     // Catch:{ Exception -> 0x0a03 }
        L_0x00e7:
            boolean r20 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a03 }
            if (r20 != 0) goto L_0x00f5
            boolean r20 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a03 }
            if (r20 == 0) goto L_0x00f2
            goto L_0x00f5
        L_0x00f2:
            r20 = 0
            goto L_0x00f7
        L_0x00f5:
            r20 = 1
        L_0x00f7:
            int r1 = (int) r5     // Catch:{ Exception -> 0x0a03 }
            if (r1 == 0) goto L_0x0110
            r21 = r13
            android.util.LongSparseArray<java.lang.Integer> r13 = r15.pushDialogs     // Catch:{ Exception -> 0x0a03 }
            int r13 = r13.size()     // Catch:{ Exception -> 0x0a03 }
            r22 = r3
            r3 = 1
            if (r13 > r3) goto L_0x0114
            if (r20 == 0) goto L_0x010a
            goto L_0x0114
        L_0x010a:
            r20 = r11
            r3 = r21
            r13 = 1
            goto L_0x0138
        L_0x0110:
            r22 = r3
            r21 = r13
        L_0x0114:
            if (r20 == 0) goto L_0x012c
            if (r10 == 0) goto L_0x0122
            java.lang.String r3 = "NotificationHiddenChatName"
            r13 = 2131626366(0x7f0e097e, float:1.8879966E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r13)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0135
        L_0x0122:
            java.lang.String r3 = "NotificationHiddenName"
            r13 = 2131626369(0x7f0e0981, float:1.8879972E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r13)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0135
        L_0x012c:
            java.lang.String r3 = "AppName"
            r13 = 2131624280(0x7f0e0158, float:1.8875735E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r13)     // Catch:{ Exception -> 0x0a03 }
        L_0x0135:
            r20 = r11
            r13 = 0
        L_0x0138:
            int r11 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0a03 }
            r23 = r1
            java.lang.String r1 = ""
            r24 = r9
            r9 = 1
            if (r11 <= r9) goto L_0x0179
            android.util.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0a03 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0a03 }
            if (r11 != r9) goto L_0x015a
            org.telegram.messenger.UserConfig r9 = r40.getUserConfig()     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$User r9 = r9.getCurrentUser()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r9 = org.telegram.messenger.UserObject.getFirstName(r9)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x017a
        L_0x015a:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r9.<init>()     // Catch:{ Exception -> 0x0a03 }
            org.telegram.messenger.UserConfig r11 = r40.getUserConfig()     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$User r11 = r11.getCurrentUser()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r11)     // Catch:{ Exception -> 0x0a03 }
            r9.append(r11)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r11 = "・"
            r9.append(r11)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0a03 }
            goto L_0x017a
        L_0x0179:
            r9 = r1
        L_0x017a:
            android.util.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0a03 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0a03 }
            r25 = r14
            r14 = 1
            if (r11 != r14) goto L_0x0191
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a03 }
            r14 = 23
            if (r11 >= r14) goto L_0x018c
            goto L_0x0191
        L_0x018c:
            r27 = r4
            r26 = r10
            goto L_0x01ec
        L_0x0191:
            android.util.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0a03 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r14 = "NewMessages"
            r26 = r10
            r10 = 1
            if (r11 != r10) goto L_0x01b6
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r10.<init>()     // Catch:{ Exception -> 0x0a03 }
            r10.append(r9)     // Catch:{ Exception -> 0x0a03 }
            int r9 = r15.total_unread_count     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r14, r9)     // Catch:{ Exception -> 0x0a03 }
            r10.append(r9)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r9 = r10.toString()     // Catch:{ Exception -> 0x0a03 }
            r27 = r4
            goto L_0x01ec
        L_0x01b6:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r10.<init>()     // Catch:{ Exception -> 0x0a03 }
            r10.append(r9)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r9 = "NotificationMessagesPeopleDisplayOrder"
            r27 = r4
            r11 = 2
            java.lang.Object[] r4 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x0a03 }
            int r11 = r15.total_unread_count     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r14, r11)     // Catch:{ Exception -> 0x0a03 }
            r14 = 0
            r4[r14] = r11     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r11 = "FromChats"
            android.util.LongSparseArray<java.lang.Integer> r14 = r15.pushDialogs     // Catch:{ Exception -> 0x0a03 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r14)     // Catch:{ Exception -> 0x0a03 }
            r14 = 1
            r4[r14] = r11     // Catch:{ Exception -> 0x0a03 }
            r11 = 2131626417(0x7f0e09b1, float:1.888007E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r11, r4)     // Catch:{ Exception -> 0x0a03 }
            r10.append(r4)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r4 = r10.toString()     // Catch:{ Exception -> 0x0a03 }
            r9 = r4
        L_0x01ec:
            androidx.core.app.NotificationCompat$Builder r4 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0a03 }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a03 }
            r4.<init>(r10)     // Catch:{ Exception -> 0x0a03 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r10 = r15.pushMessages     // Catch:{ Exception -> 0x0a03 }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r11 = ": "
            java.lang.String r14 = " "
            r28 = r5
            java.lang.String r5 = " @ "
            r6 = 1
            if (r10 != r6) goto L_0x0276
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r15.pushMessages     // Catch:{ Exception -> 0x0a03 }
            r10 = 0
            java.lang.Object r0 = r0.get(r10)     // Catch:{ Exception -> 0x0a03 }
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0     // Catch:{ Exception -> 0x0a03 }
            r30 = r7
            boolean[] r7 = new boolean[r6]     // Catch:{ Exception -> 0x0a03 }
            r6 = 0
            java.lang.String r8 = r15.getStringForMessage(r0, r10, r7, r6)     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x0a03 }
            boolean r0 = r0.silent     // Catch:{ Exception -> 0x0a03 }
            if (r8 != 0) goto L_0x021d
            return
        L_0x021d:
            if (r13 == 0) goto L_0x0262
            if (r12 == 0) goto L_0x0235
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r6.<init>()     // Catch:{ Exception -> 0x0a03 }
            r6.append(r5)     // Catch:{ Exception -> 0x0a03 }
            r6.append(r3)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = r8.replace(r5, r1)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0263
        L_0x0235:
            r5 = 0
            boolean r6 = r7[r5]     // Catch:{ Exception -> 0x0a03 }
            if (r6 == 0) goto L_0x024e
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r5.<init>()     // Catch:{ Exception -> 0x0a03 }
            r5.append(r3)     // Catch:{ Exception -> 0x0a03 }
            r5.append(r11)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = r8.replace(r5, r1)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0263
        L_0x024e:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r5.<init>()     // Catch:{ Exception -> 0x0a03 }
            r5.append(r3)     // Catch:{ Exception -> 0x0a03 }
            r5.append(r14)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = r8.replace(r5, r1)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0263
        L_0x0262:
            r5 = r8
        L_0x0263:
            r4.setContentText(r5)     // Catch:{ Exception -> 0x0a03 }
            androidx.core.app.NotificationCompat$BigTextStyle r6 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0a03 }
            r6.<init>()     // Catch:{ Exception -> 0x0a03 }
            r6.bigText(r5)     // Catch:{ Exception -> 0x0a03 }
            r4.setStyle(r6)     // Catch:{ Exception -> 0x0a03 }
            r33 = r2
            r2 = r0
            goto L_0x0324
        L_0x0276:
            r30 = r7
            r4.setContentText(r9)     // Catch:{ Exception -> 0x0a03 }
            androidx.core.app.NotificationCompat$InboxStyle r6 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0a03 }
            r6.<init>()     // Catch:{ Exception -> 0x0a03 }
            r6.setBigContentTitle(r3)     // Catch:{ Exception -> 0x0a03 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0a03 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0a03 }
            r8 = 10
            int r7 = java.lang.Math.min(r8, r7)     // Catch:{ Exception -> 0x0a03 }
            r8 = 1
            boolean[] r10 = new boolean[r8]     // Catch:{ Exception -> 0x0a03 }
            r33 = r2
            r2 = 2
            r8 = 0
            r32 = 0
        L_0x0298:
            if (r8 >= r7) goto L_0x031a
            r34 = r7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0a03 }
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x0a03 }
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7     // Catch:{ Exception -> 0x0a03 }
            r35 = r4
            r37 = r8
            r36 = r9
            r4 = 0
            r9 = 0
            java.lang.String r8 = r15.getStringForMessage(r7, r9, r10, r4)     // Catch:{ Exception -> 0x0a03 }
            if (r8 == 0) goto L_0x0310
            org.telegram.tgnet.TLRPC$Message r4 = r7.messageOwner     // Catch:{ Exception -> 0x0a03 }
            int r7 = r4.date     // Catch:{ Exception -> 0x0a03 }
            if (r7 > r0) goto L_0x02b9
            goto L_0x0310
        L_0x02b9:
            r7 = 2
            if (r2 != r7) goto L_0x02c0
            boolean r2 = r4.silent     // Catch:{ Exception -> 0x0a03 }
            r32 = r8
        L_0x02c0:
            android.util.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0a03 }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0a03 }
            r7 = 1
            if (r4 != r7) goto L_0x030d
            if (r13 == 0) goto L_0x030d
            if (r12 == 0) goto L_0x02e1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r4.<init>()     // Catch:{ Exception -> 0x0a03 }
            r4.append(r5)     // Catch:{ Exception -> 0x0a03 }
            r4.append(r3)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r8 = r8.replace(r4, r1)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x030d
        L_0x02e1:
            r4 = 0
            boolean r7 = r10[r4]     // Catch:{ Exception -> 0x0a03 }
            if (r7 == 0) goto L_0x02fa
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r4.<init>()     // Catch:{ Exception -> 0x0a03 }
            r4.append(r3)     // Catch:{ Exception -> 0x0a03 }
            r4.append(r11)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r8 = r8.replace(r4, r1)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x030d
        L_0x02fa:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r4.<init>()     // Catch:{ Exception -> 0x0a03 }
            r4.append(r3)     // Catch:{ Exception -> 0x0a03 }
            r4.append(r14)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r8 = r8.replace(r4, r1)     // Catch:{ Exception -> 0x0a03 }
        L_0x030d:
            r6.addLine(r8)     // Catch:{ Exception -> 0x0a03 }
        L_0x0310:
            int r8 = r37 + 1
            r7 = r34
            r4 = r35
            r9 = r36
            goto L_0x0298
        L_0x031a:
            r35 = r4
            r6.setSummaryText(r9)     // Catch:{ Exception -> 0x0a03 }
            r4.setStyle(r6)     // Catch:{ Exception -> 0x0a03 }
            r8 = r32
        L_0x0324:
            if (r41 == 0) goto L_0x0338
            if (r33 == 0) goto L_0x0338
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0a03 }
            boolean r0 = r0.isRecordingAudio()     // Catch:{ Exception -> 0x0a03 }
            if (r0 != 0) goto L_0x0338
            r5 = 1
            if (r2 != r5) goto L_0x0336
            goto L_0x0338
        L_0x0336:
            r0 = 0
            goto L_0x0339
        L_0x0338:
            r0 = 1
        L_0x0339:
            java.lang.String r5 = "custom_"
            if (r0 != 0) goto L_0x03f2
            int r10 = (r28 > r30 ? 1 : (r28 == r30 ? 0 : -1))
            if (r10 != 0) goto L_0x03f2
            if (r12 == 0) goto L_0x03f2
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r10.<init>()     // Catch:{ Exception -> 0x0a03 }
            r10.append(r5)     // Catch:{ Exception -> 0x0a03 }
            r13 = r28
            r10.append(r13)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0a03 }
            r11 = r27
            r6 = 0
            boolean r7 = r11.getBoolean(r10, r6)     // Catch:{ Exception -> 0x0a03 }
            if (r7 == 0) goto L_0x038b
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r7.<init>()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r10 = "smart_max_count_"
            r7.append(r10)     // Catch:{ Exception -> 0x0a03 }
            r7.append(r13)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0a03 }
            r10 = 2
            int r7 = r11.getInt(r7, r10)     // Catch:{ Exception -> 0x0a03 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r10.<init>()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r6 = "smart_delay_"
            r10.append(r6)     // Catch:{ Exception -> 0x0a03 }
            r10.append(r13)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r6 = r10.toString()     // Catch:{ Exception -> 0x0a03 }
            r10 = 180(0xb4, float:2.52E-43)
            int r6 = r11.getInt(r6, r10)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0390
        L_0x038b:
            r10 = 180(0xb4, float:2.52E-43)
            r6 = 180(0xb4, float:2.52E-43)
            r7 = 2
        L_0x0390:
            if (r7 == 0) goto L_0x03ed
            android.util.LongSparseArray<android.graphics.Point> r10 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0a03 }
            java.lang.Object r10 = r10.get(r13)     // Catch:{ Exception -> 0x0a03 }
            android.graphics.Point r10 = (android.graphics.Point) r10     // Catch:{ Exception -> 0x0a03 }
            if (r10 != 0) goto L_0x03b3
            android.graphics.Point r6 = new android.graphics.Point     // Catch:{ Exception -> 0x0a03 }
            long r29 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a03 }
            r36 = r9
            r27 = 1000(0x3e8, double:4.94E-321)
            long r9 = r29 / r27
            int r7 = (int) r9     // Catch:{ Exception -> 0x0a03 }
            r9 = 1
            r6.<init>(r9, r7)     // Catch:{ Exception -> 0x0a03 }
            android.util.LongSparseArray<android.graphics.Point> r7 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0a03 }
            r7.put(r13, r6)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x03f8
        L_0x03b3:
            r36 = r9
            int r9 = r10.y     // Catch:{ Exception -> 0x0a03 }
            int r9 = r9 + r6
            r29 = r0
            r6 = r1
            long r0 = (long) r9     // Catch:{ Exception -> 0x0a03 }
            long r30 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a03 }
            r27 = 1000(0x3e8, double:4.94E-321)
            long r30 = r30 / r27
            int r9 = (r0 > r30 ? 1 : (r0 == r30 ? 0 : -1))
            if (r9 >= 0) goto L_0x03d5
            long r0 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a03 }
            long r0 = r0 / r27
            int r1 = (int) r0     // Catch:{ Exception -> 0x0a03 }
            r7 = 1
            r10.set(r7, r1)     // Catch:{ Exception -> 0x0a03 }
            r1 = r6
            goto L_0x03fa
        L_0x03d5:
            int r0 = r10.x     // Catch:{ Exception -> 0x0a03 }
            if (r0 >= r7) goto L_0x03e9
            r1 = 1
            int r0 = r0 + r1
            long r30 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a03 }
            r1 = r6
            r27 = 1000(0x3e8, double:4.94E-321)
            long r6 = r30 / r27
            int r7 = (int) r6     // Catch:{ Exception -> 0x0a03 }
            r10.set(r0, r7)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x03fa
        L_0x03e9:
            r1 = r6
            r29 = 1
            goto L_0x03fa
        L_0x03ed:
            r29 = r0
            r36 = r9
            goto L_0x03fa
        L_0x03f2:
            r36 = r9
            r11 = r27
            r13 = r28
        L_0x03f8:
            r29 = r0
        L_0x03fa:
            android.net.Uri r0 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r6 = r0.getPath()     // Catch:{ Exception -> 0x0a03 }
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0a03 }
            if (r0 != 0) goto L_0x0407
            r30 = 1
            goto L_0x0409
        L_0x0407:
            r30 = 0
        L_0x0409:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r0.<init>()     // Catch:{ Exception -> 0x0a03 }
            r0.append(r5)     // Catch:{ Exception -> 0x0a03 }
            r0.append(r13)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a03 }
            r5 = 0
            boolean r0 = r11.getBoolean(r0, r5)     // Catch:{ Exception -> 0x0a03 }
            r5 = 3
            if (r0 == 0) goto L_0x0496
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r0.<init>()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r7 = "vibrate_"
            r0.append(r7)     // Catch:{ Exception -> 0x0a03 }
            r0.append(r13)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a03 }
            r7 = 0
            int r0 = r11.getInt(r0, r7)     // Catch:{ Exception -> 0x0a03 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r7.<init>()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r9 = "priority_"
            r7.append(r9)     // Catch:{ Exception -> 0x0a03 }
            r7.append(r13)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0a03 }
            int r7 = r11.getInt(r7, r5)     // Catch:{ Exception -> 0x0a03 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r9.<init>()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r10 = "sound_path_"
            r9.append(r10)     // Catch:{ Exception -> 0x0a03 }
            r9.append(r13)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0a03 }
            r10 = 0
            java.lang.String r9 = r11.getString(r9, r10)     // Catch:{ Exception -> 0x0a03 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r10.<init>()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = "color_"
            r10.append(r5)     // Catch:{ Exception -> 0x0a03 }
            r10.append(r13)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = r10.toString()     // Catch:{ Exception -> 0x0a03 }
            boolean r5 = r11.contains(r5)     // Catch:{ Exception -> 0x0a03 }
            if (r5 == 0) goto L_0x0494
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r5.<init>()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r10 = "color_"
            r5.append(r10)     // Catch:{ Exception -> 0x0a03 }
            r5.append(r13)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a03 }
            r10 = 0
            int r5 = r11.getInt(r5, r10)     // Catch:{ Exception -> 0x0a03 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x049a
        L_0x0494:
            r5 = 0
            goto L_0x049a
        L_0x0496:
            r0 = 0
            r5 = 0
            r7 = 3
            r9 = 0
        L_0x049a:
            if (r26 == 0) goto L_0x04f4
            if (r25 == 0) goto L_0x04c9
            java.lang.String r10 = "ChannelSoundPath"
            java.lang.String r10 = r11.getString(r10, r6)     // Catch:{ Exception -> 0x0a03 }
            r32 = r10
            java.lang.String r10 = "vibrate_channel"
            r33 = r1
            r1 = 0
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r1 = "priority_channel"
            r34 = r10
            r10 = 1
            int r1 = r11.getInt(r1, r10)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r10 = "ChannelLed"
            r35 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a03 }
            r1 = r32
            r25 = 2
            goto L_0x0520
        L_0x04c9:
            r33 = r1
            java.lang.String r1 = "GroupSoundPath"
            java.lang.String r1 = r11.getString(r1, r6)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r10 = "vibrate_group"
            r32 = r1
            r1 = 0
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r1 = "priority_group"
            r34 = r10
            r10 = 1
            int r1 = r11.getInt(r1, r10)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r10 = "GroupLed"
            r35 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a03 }
            r1 = r32
            r25 = 0
            goto L_0x0520
        L_0x04f4:
            r33 = r1
            if (r24 == 0) goto L_0x052b
            java.lang.String r1 = "GlobalSoundPath"
            java.lang.String r1 = r11.getString(r1, r6)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r10 = "vibrate_messages"
            r32 = r1
            r1 = 0
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r1 = "priority_messages"
            r34 = r10
            r10 = 1
            int r1 = r11.getInt(r1, r10)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r10 = "MessagesLed"
            r35 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a03 }
            r1 = r32
            r25 = 1
        L_0x0520:
            r32 = r6
            r6 = r34
            r34 = r8
            r8 = r35
            r35 = r2
            goto L_0x053c
        L_0x052b:
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r35 = r2
            r32 = r6
            r34 = r8
            r1 = 0
            r6 = 0
            r8 = 0
            r10 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r25 = 1
        L_0x053c:
            r2 = 4
            if (r6 != r2) goto L_0x0543
            r6 = 0
            r37 = 1
            goto L_0x0545
        L_0x0543:
            r37 = 0
        L_0x0545:
            if (r9 == 0) goto L_0x0551
            boolean r38 = android.text.TextUtils.equals(r1, r9)     // Catch:{ Exception -> 0x0a03 }
            if (r38 != 0) goto L_0x0551
            r1 = r9
            r2 = 3
            r9 = 0
            goto L_0x0553
        L_0x0551:
            r2 = 3
            r9 = 1
        L_0x0553:
            if (r7 == r2) goto L_0x0559
            if (r8 == r7) goto L_0x0559
            r9 = 0
            goto L_0x055a
        L_0x0559:
            r7 = r8
        L_0x055a:
            if (r5 == 0) goto L_0x0567
            int r2 = r5.intValue()     // Catch:{ Exception -> 0x0a03 }
            if (r2 == r10) goto L_0x0567
            int r10 = r5.intValue()     // Catch:{ Exception -> 0x0a03 }
            r9 = 0
        L_0x0567:
            if (r0 == 0) goto L_0x0571
            r2 = 4
            if (r0 == r2) goto L_0x0571
            if (r0 == r6) goto L_0x0571
            r39 = 0
            goto L_0x0574
        L_0x0571:
            r0 = r6
            r39 = r9
        L_0x0574:
            if (r30 == 0) goto L_0x059d
            java.lang.String r2 = "EnableInAppSounds"
            r5 = 1
            boolean r2 = r11.getBoolean(r2, r5)     // Catch:{ Exception -> 0x0a03 }
            if (r2 != 0) goto L_0x0581
            r6 = 0
            goto L_0x0582
        L_0x0581:
            r6 = r1
        L_0x0582:
            java.lang.String r1 = "EnableInAppVibrate"
            boolean r1 = r11.getBoolean(r1, r5)     // Catch:{ Exception -> 0x0a03 }
            if (r1 != 0) goto L_0x058b
            r0 = 2
        L_0x058b:
            java.lang.String r1 = "EnableInAppPriority"
            r2 = 0
            boolean r1 = r11.getBoolean(r1, r2)     // Catch:{ Exception -> 0x0a03 }
            if (r1 != 0) goto L_0x0597
            r1 = r0
            r7 = 0
            goto L_0x059f
        L_0x0597:
            r1 = 2
            if (r7 != r1) goto L_0x059e
            r1 = r0
            r7 = 1
            goto L_0x059f
        L_0x059d:
            r6 = r1
        L_0x059e:
            r1 = r0
        L_0x059f:
            if (r37 == 0) goto L_0x05b5
            r2 = 2
            if (r1 == r2) goto L_0x05b5
            android.media.AudioManager r0 = audioManager     // Catch:{ Exception -> 0x05b1 }
            int r0 = r0.getRingerMode()     // Catch:{ Exception -> 0x05b1 }
            if (r0 == 0) goto L_0x05b5
            r2 = 1
            if (r0 == r2) goto L_0x05b5
            r1 = 2
            goto L_0x05b5
        L_0x05b1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0a03 }
        L_0x05b5:
            if (r29 == 0) goto L_0x05bc
            r1 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            goto L_0x05bd
        L_0x05bc:
            r8 = r10
        L_0x05bd:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0a03 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a03 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r5 = org.telegram.ui.LaunchActivity.class
            r0.<init>(r2, r5)     // Catch:{ Exception -> 0x0a03 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r2.<init>()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = "com.tmessages.openchat"
            r2.append(r5)     // Catch:{ Exception -> 0x0a03 }
            double r9 = java.lang.Math.random()     // Catch:{ Exception -> 0x0a03 }
            r2.append(r9)     // Catch:{ Exception -> 0x0a03 }
            r5 = 2147483647(0x7fffffff, float:NaN)
            r2.append(r5)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a03 }
            r0.setAction(r2)     // Catch:{ Exception -> 0x0a03 }
            r2 = 67108864(0x4000000, float:1.5046328E-36)
            r0.setFlags(r2)     // Catch:{ Exception -> 0x0a03 }
            r9 = 0
            if (r23 == 0) goto L_0x066d
            android.util.LongSparseArray<java.lang.Integer> r2 = r15.pushDialogs     // Catch:{ Exception -> 0x0a03 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0a03 }
            r5 = 1
            if (r2 != r5) goto L_0x060a
            if (r26 == 0) goto L_0x0600
            java.lang.String r2 = "chatId"
            r5 = r26
            r0.putExtra(r2, r5)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x060a
        L_0x0600:
            if (r24 == 0) goto L_0x060a
            java.lang.String r2 = "userId"
            r5 = r24
            r0.putExtra(r2, r5)     // Catch:{ Exception -> 0x0a03 }
        L_0x060a:
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a03 }
            if (r2 != 0) goto L_0x0667
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a03 }
            if (r2 == 0) goto L_0x0615
            goto L_0x0667
        L_0x0615:
            android.util.LongSparseArray<java.lang.Integer> r2 = r15.pushDialogs     // Catch:{ Exception -> 0x0a03 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0a03 }
            r5 = 1
            if (r2 != r5) goto L_0x065f
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a03 }
            r5 = 28
            if (r2 >= r5) goto L_0x065f
            if (r12 == 0) goto L_0x0641
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r12.photo     // Catch:{ Exception -> 0x0a03 }
            if (r2 == 0) goto L_0x065f
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ Exception -> 0x0a03 }
            if (r2 == 0) goto L_0x065f
            r23 = r6
            long r5 = r2.volume_id     // Catch:{ Exception -> 0x0a03 }
            int r24 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r24 == 0) goto L_0x0661
            int r5 = r2.local_id     // Catch:{ Exception -> 0x0a03 }
            if (r5 == 0) goto L_0x0661
            r6 = r2
            r5 = r7
            r2 = r20
            r20 = r8
            goto L_0x068e
        L_0x0641:
            r23 = r6
            if (r20 == 0) goto L_0x0661
            r2 = r20
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r2.photo     // Catch:{ Exception -> 0x0a03 }
            if (r5 == 0) goto L_0x065d
            org.telegram.tgnet.TLRPC$FileLocation r6 = r5.photo_small     // Catch:{ Exception -> 0x0a03 }
            if (r6 == 0) goto L_0x065d
            r5 = r7
            r20 = r8
            long r7 = r6.volume_id     // Catch:{ Exception -> 0x0a03 }
            int r24 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r24 == 0) goto L_0x068d
            int r7 = r6.local_id     // Catch:{ Exception -> 0x0a03 }
            if (r7 == 0) goto L_0x068d
            goto L_0x068e
        L_0x065d:
            r5 = r7
            goto L_0x0664
        L_0x065f:
            r23 = r6
        L_0x0661:
            r5 = r7
            r2 = r20
        L_0x0664:
            r20 = r8
            goto L_0x068d
        L_0x0667:
            r23 = r6
            r5 = r7
            r2 = r20
            goto L_0x0664
        L_0x066d:
            r23 = r6
            r5 = r7
            r2 = r20
            r20 = r8
            android.util.LongSparseArray<java.lang.Integer> r6 = r15.pushDialogs     // Catch:{ Exception -> 0x0a03 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0a03 }
            r7 = 1
            if (r6 != r7) goto L_0x068d
            long r6 = globalSecretChatId     // Catch:{ Exception -> 0x0a03 }
            int r8 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x068d
            java.lang.String r6 = "encId"
            r7 = 32
            long r7 = r13 >> r7
            int r8 = (int) r7     // Catch:{ Exception -> 0x0a03 }
            r0.putExtra(r6, r8)     // Catch:{ Exception -> 0x0a03 }
        L_0x068d:
            r6 = 0
        L_0x068e:
            int r7 = r15.currentAccount     // Catch:{ Exception -> 0x0a03 }
            r8 = r18
            r0.putExtra(r8, r7)     // Catch:{ Exception -> 0x0a03 }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a03 }
            r9 = 1073741824(0x40000000, float:2.0)
            r10 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r7, r10, r0, r9)     // Catch:{ Exception -> 0x0a03 }
            r4.setContentTitle(r3)     // Catch:{ Exception -> 0x0a03 }
            r3 = 2131165851(0x7var_b, float:1.794593E38)
            r4.setSmallIcon(r3)     // Catch:{ Exception -> 0x0a03 }
            r3 = 1
            r4.setAutoCancel(r3)     // Catch:{ Exception -> 0x0a03 }
            int r3 = r15.total_unread_count     // Catch:{ Exception -> 0x0a03 }
            r4.setNumber(r3)     // Catch:{ Exception -> 0x0a03 }
            r4.setContentIntent(r0)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r0 = r15.notificationGroup     // Catch:{ Exception -> 0x0a03 }
            r4.setGroup(r0)     // Catch:{ Exception -> 0x0a03 }
            r3 = 1
            r4.setGroupSummary(r3)     // Catch:{ Exception -> 0x0a03 }
            r4.setShowWhen(r3)     // Catch:{ Exception -> 0x0a03 }
            r3 = r22
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner     // Catch:{ Exception -> 0x0a03 }
            int r0 = r0.date     // Catch:{ Exception -> 0x0a03 }
            long r9 = (long) r0     // Catch:{ Exception -> 0x0a03 }
            r27 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 * r27
            r4.setWhen(r9)     // Catch:{ Exception -> 0x0a03 }
            r0 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r4.setColor(r0)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r0 = "msg"
            r4.setCategory(r0)     // Catch:{ Exception -> 0x0a03 }
            if (r12 != 0) goto L_0x06fc
            if (r2 == 0) goto L_0x06fc
            java.lang.String r0 = r2.phone     // Catch:{ Exception -> 0x0a03 }
            if (r0 == 0) goto L_0x06fc
            int r0 = r0.length()     // Catch:{ Exception -> 0x0a03 }
            if (r0 <= 0) goto L_0x06fc
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r0.<init>()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r7 = "tel:+"
            r0.append(r7)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r2 = r2.phone     // Catch:{ Exception -> 0x0a03 }
            r0.append(r2)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a03 }
            r4.addPerson(r0)     // Catch:{ Exception -> 0x0a03 }
        L_0x06fc:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0a03 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a03 }
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r7 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r2, r7)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r2 = "messageDate"
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner     // Catch:{ Exception -> 0x0a03 }
            int r7 = r7.date     // Catch:{ Exception -> 0x0a03 }
            r0.putExtra(r2, r7)     // Catch:{ Exception -> 0x0a03 }
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0a03 }
            r0.putExtra(r8, r2)     // Catch:{ Exception -> 0x0a03 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a03 }
            r7 = 134217728(0x8000000, float:3.85186E-34)
            r9 = 1
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r2, r9, r0, r7)     // Catch:{ Exception -> 0x0a03 }
            r4.setDeleteIntent(r0)     // Catch:{ Exception -> 0x0a03 }
            if (r6 == 0) goto L_0x076b
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r2 = "50_50"
            r9 = 0
            android.graphics.drawable.BitmapDrawable r0 = r0.getImageFromMemory(r6, r9, r2)     // Catch:{ Exception -> 0x0a03 }
            if (r0 == 0) goto L_0x0736
            android.graphics.Bitmap r0 = r0.getBitmap()     // Catch:{ Exception -> 0x0a03 }
            r4.setLargeIcon(r0)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x076c
        L_0x0736:
            r2 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r2)     // Catch:{ all -> 0x0769 }
            boolean r2 = r0.exists()     // Catch:{ all -> 0x0769 }
            if (r2 == 0) goto L_0x076c
            r2 = 1126170624(0x43200000, float:160.0)
            r6 = 1112014848(0x42480000, float:50.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ all -> 0x0769 }
            float r6 = (float) r6     // Catch:{ all -> 0x0769 }
            float r2 = r2 / r6
            android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0769 }
            r6.<init>()     // Catch:{ all -> 0x0769 }
            r10 = 1065353216(0x3var_, float:1.0)
            int r10 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r10 >= 0) goto L_0x0758
            r2 = 1
            goto L_0x0759
        L_0x0758:
            int r2 = (int) r2     // Catch:{ all -> 0x0769 }
        L_0x0759:
            r6.inSampleSize = r2     // Catch:{ all -> 0x0769 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x0769 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r6)     // Catch:{ all -> 0x0769 }
            if (r0 == 0) goto L_0x076c
            r4.setLargeIcon(r0)     // Catch:{ all -> 0x0769 }
            goto L_0x076c
        L_0x0769:
            goto L_0x076c
        L_0x076b:
            r9 = 0
        L_0x076c:
            r0 = 5
            r2 = 26
            r6 = r35
            if (r41 == 0) goto L_0x07af
            r10 = 1
            if (r6 != r10) goto L_0x0777
            goto L_0x07af
        L_0x0777:
            if (r5 != 0) goto L_0x0784
            r10 = 0
            r4.setPriority(r10)     // Catch:{ Exception -> 0x0a03 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a03 }
            if (r5 < r2) goto L_0x07ba
            r5 = 1
            r10 = 3
            goto L_0x07bc
        L_0x0784:
            r10 = 1
            if (r5 == r10) goto L_0x07a4
            r10 = 2
            if (r5 != r10) goto L_0x078b
            goto L_0x07a4
        L_0x078b:
            r10 = 4
            if (r5 != r10) goto L_0x0799
            r5 = -2
            r4.setPriority(r5)     // Catch:{ Exception -> 0x0a03 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a03 }
            if (r5 < r2) goto L_0x07ba
            r5 = 1
            r10 = 1
            goto L_0x07bc
        L_0x0799:
            if (r5 != r0) goto L_0x07ba
            r5 = -1
            r4.setPriority(r5)     // Catch:{ Exception -> 0x0a03 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a03 }
            if (r5 < r2) goto L_0x07ba
            goto L_0x07b7
        L_0x07a4:
            r5 = 1
            r4.setPriority(r5)     // Catch:{ Exception -> 0x0a03 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a03 }
            if (r5 < r2) goto L_0x07ba
            r5 = 1
            r10 = 4
            goto L_0x07bc
        L_0x07af:
            r5 = -1
            r4.setPriority(r5)     // Catch:{ Exception -> 0x0a03 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a03 }
            if (r5 < r2) goto L_0x07ba
        L_0x07b7:
            r5 = 1
            r10 = 2
            goto L_0x07bc
        L_0x07ba:
            r5 = 1
            r10 = 0
        L_0x07bc:
            if (r6 == r5) goto L_0x08d7
            if (r29 != 0) goto L_0x08d7
            if (r30 == 0) goto L_0x07ca
            java.lang.String r6 = "EnableInAppPreview"
            boolean r6 = r11.getBoolean(r6, r5)     // Catch:{ Exception -> 0x0a03 }
            if (r6 == 0) goto L_0x07ff
        L_0x07ca:
            int r5 = r34.length()     // Catch:{ Exception -> 0x0a03 }
            r6 = 100
            if (r5 <= r6) goto L_0x07f9
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a03 }
            r5.<init>()     // Catch:{ Exception -> 0x0a03 }
            r6 = 100
            r11 = r34
            r12 = 0
            java.lang.String r6 = r11.substring(r12, r6)     // Catch:{ Exception -> 0x0a03 }
            r11 = 32
            r12 = 10
            java.lang.String r6 = r6.replace(r12, r11)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r6 = r6.trim()     // Catch:{ Exception -> 0x0a03 }
            r5.append(r6)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r6 = "..."
            r5.append(r6)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a03 }
            goto L_0x07fc
        L_0x07f9:
            r11 = r34
            r5 = r11
        L_0x07fc:
            r4.setTicker(r5)     // Catch:{ Exception -> 0x0a03 }
        L_0x07ff:
            if (r23 == 0) goto L_0x0873
            java.lang.String r5 = "NoSound"
            r6 = r23
            boolean r5 = r6.equals(r5)     // Catch:{ Exception -> 0x0a03 }
            if (r5 != 0) goto L_0x0873
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a03 }
            if (r5 < r2) goto L_0x081f
            r2 = r32
            boolean r0 = r6.equals(r2)     // Catch:{ Exception -> 0x0a03 }
            if (r0 == 0) goto L_0x081a
            android.net.Uri r6 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0874
        L_0x081a:
            android.net.Uri r6 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0874
        L_0x081f:
            r2 = r32
            boolean r2 = r6.equals(r2)     // Catch:{ Exception -> 0x0a03 }
            if (r2 == 0) goto L_0x082d
            android.net.Uri r2 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a03 }
            r4.setSound(r2, r0)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0873
        L_0x082d:
            r2 = 24
            if (r5 < r2) goto L_0x086c
            java.lang.String r2 = "file://"
            boolean r2 = r6.startsWith(r2)     // Catch:{ Exception -> 0x0a03 }
            if (r2 == 0) goto L_0x086c
            android.net.Uri r2 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a03 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r2)     // Catch:{ Exception -> 0x0a03 }
            if (r2 != 0) goto L_0x086c
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0864 }
            java.lang.String r5 = "org.telegram.messenger.provider"
            java.io.File r11 = new java.io.File     // Catch:{ Exception -> 0x0864 }
            java.lang.String r12 = "file://"
            r9 = r33
            java.lang.String r9 = r6.replace(r12, r9)     // Catch:{ Exception -> 0x0864 }
            r11.<init>(r9)     // Catch:{ Exception -> 0x0864 }
            android.net.Uri r2 = androidx.core.content.FileProvider.getUriForFile(r2, r5, r11)     // Catch:{ Exception -> 0x0864 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0864 }
            java.lang.String r9 = "com.android.systemui"
            r11 = 1
            r5.grantUriPermission(r9, r2, r11)     // Catch:{ Exception -> 0x0864 }
            r4.setSound(r2, r0)     // Catch:{ Exception -> 0x0864 }
            goto L_0x0873
        L_0x0864:
            android.net.Uri r2 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a03 }
            r4.setSound(r2, r0)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0873
        L_0x086c:
            android.net.Uri r2 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a03 }
            r4.setSound(r2, r0)     // Catch:{ Exception -> 0x0a03 }
        L_0x0873:
            r6 = 0
        L_0x0874:
            if (r20 == 0) goto L_0x0880
            r0 = 1000(0x3e8, float:1.401E-42)
            r2 = 1000(0x3e8, float:1.401E-42)
            r9 = r20
            r4.setLights(r9, r0, r2)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0882
        L_0x0880:
            r9 = r20
        L_0x0882:
            r2 = 2
            if (r1 != r2) goto L_0x0893
            long[] r0 = new long[r2]     // Catch:{ Exception -> 0x0a03 }
            r1 = 0
            r5 = 0
            r0[r5] = r1     // Catch:{ Exception -> 0x0a03 }
            r5 = 1
            r0[r5] = r1     // Catch:{ Exception -> 0x0a03 }
            r4.setVibrate(r0)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x08d4
        L_0x0893:
            r2 = 1
            if (r1 != r2) goto L_0x08ae
            r5 = 4
            long[] r0 = new long[r5]     // Catch:{ Exception -> 0x0a03 }
            r1 = 0
            r11 = 0
            r0[r1] = r11     // Catch:{ Exception -> 0x0a03 }
            r17 = 100
            r0[r2] = r17     // Catch:{ Exception -> 0x0a03 }
            r1 = 2
            r0[r1] = r11     // Catch:{ Exception -> 0x0a03 }
            r1 = 100
            r5 = 3
            r0[r5] = r1     // Catch:{ Exception -> 0x0a03 }
            r4.setVibrate(r0)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x08d4
        L_0x08ae:
            if (r1 == 0) goto L_0x08cd
            r2 = 4
            if (r1 != r2) goto L_0x08b4
            goto L_0x08cd
        L_0x08b4:
            r2 = 3
            if (r1 != r2) goto L_0x08c8
            r1 = 2
            long[] r0 = new long[r1]     // Catch:{ Exception -> 0x0a03 }
            r1 = 0
            r5 = 0
            r0[r5] = r1     // Catch:{ Exception -> 0x0a03 }
            r1 = 1000(0x3e8, double:4.94E-321)
            r5 = 1
            r0[r5] = r1     // Catch:{ Exception -> 0x0a03 }
            r4.setVibrate(r0)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x08d4
        L_0x08c8:
            r16 = r6
            r0 = 0
        L_0x08cb:
            r5 = 1
            goto L_0x08e9
        L_0x08cd:
            r1 = 2
            r4.setDefaults(r1)     // Catch:{ Exception -> 0x0a03 }
            r1 = 0
            long[] r0 = new long[r1]     // Catch:{ Exception -> 0x0a03 }
        L_0x08d4:
            r16 = r6
            goto L_0x08cb
        L_0x08d7:
            r9 = r20
            r1 = 2
            long[] r0 = new long[r1]     // Catch:{ Exception -> 0x0a03 }
            r1 = 0
            r5 = 0
            r0[r5] = r1     // Catch:{ Exception -> 0x0a03 }
            r5 = 1
            r0[r5] = r1     // Catch:{ Exception -> 0x0a03 }
            r4.setVibrate(r0)     // Catch:{ Exception -> 0x0a03 }
            r16 = 0
        L_0x08e9:
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a03 }
            if (r1 != 0) goto L_0x098d
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a03 }
            if (r1 != 0) goto L_0x098d
            long r1 = r3.getDialogId()     // Catch:{ Exception -> 0x0a03 }
            r11 = 777000(0xbdb28, double:3.83889E-318)
            int r6 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r6 != 0) goto L_0x098d
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$ReplyMarkup r1 = r1.reply_markup     // Catch:{ Exception -> 0x0a03 }
            if (r1 == 0) goto L_0x098d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r1 = r1.rows     // Catch:{ Exception -> 0x0a03 }
            int r2 = r1.size()     // Catch:{ Exception -> 0x0a03 }
            r6 = 0
            r11 = 0
        L_0x090c:
            if (r6 >= r2) goto L_0x098b
            java.lang.Object r12 = r1.get(r6)     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r12 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r12     // Catch:{ Exception -> 0x0a03 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r5 = r12.buttons     // Catch:{ Exception -> 0x0a03 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0a03 }
            r7 = 0
        L_0x091b:
            if (r7 >= r5) goto L_0x097c
            r19 = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r1 = r12.buttons     // Catch:{ Exception -> 0x0a03 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ Exception -> 0x0a03 }
            org.telegram.tgnet.TLRPC$KeyboardButton r1 = (org.telegram.tgnet.TLRPC$KeyboardButton) r1     // Catch:{ Exception -> 0x0a03 }
            r41 = r2
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0a03 }
            if (r2 == 0) goto L_0x096c
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0a03 }
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a03 }
            r20 = r5
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r5 = org.telegram.messenger.NotificationCallbackReceiver.class
            r2.<init>(r11, r5)     // Catch:{ Exception -> 0x0a03 }
            int r5 = r15.currentAccount     // Catch:{ Exception -> 0x0a03 }
            r2.putExtra(r8, r5)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r5 = "did"
            r2.putExtra(r5, r13)     // Catch:{ Exception -> 0x0a03 }
            byte[] r5 = r1.data     // Catch:{ Exception -> 0x0a03 }
            if (r5 == 0) goto L_0x094b
            java.lang.String r11 = "data"
            r2.putExtra(r11, r5)     // Catch:{ Exception -> 0x0a03 }
        L_0x094b:
            java.lang.String r5 = "mid"
            int r11 = r3.getId()     // Catch:{ Exception -> 0x0a03 }
            r2.putExtra(r5, r11)     // Catch:{ Exception -> 0x0a03 }
            java.lang.String r1 = r1.text     // Catch:{ Exception -> 0x0a03 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a03 }
            int r11 = r15.lastButtonId     // Catch:{ Exception -> 0x0a03 }
            r22 = r3
            int r3 = r11 + 1
            r15.lastButtonId = r3     // Catch:{ Exception -> 0x0a03 }
            r3 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r5, r11, r2, r3)     // Catch:{ Exception -> 0x0a03 }
            r3 = 0
            r4.addAction(r3, r1, r2)     // Catch:{ Exception -> 0x0a03 }
            r11 = 1
            goto L_0x0971
        L_0x096c:
            r22 = r3
            r20 = r5
            r3 = 0
        L_0x0971:
            int r7 = r7 + 1
            r2 = r41
            r1 = r19
            r5 = r20
            r3 = r22
            goto L_0x091b
        L_0x097c:
            r19 = r1
            r41 = r2
            r22 = r3
            r3 = 0
            int r6 = r6 + 1
            r3 = r22
            r5 = 1
            r7 = 134217728(0x8000000, float:3.85186E-34)
            goto L_0x090c
        L_0x098b:
            r2 = r11
            goto L_0x098f
        L_0x098d:
            r3 = 0
            r2 = 0
        L_0x098f:
            if (r2 != 0) goto L_0x09e8
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a03 }
            r2 = 24
            if (r1 >= r2) goto L_0x09e8
            java.lang.String r2 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0a03 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x0a03 }
            if (r2 != 0) goto L_0x09e8
            boolean r2 = r40.hasMessagesToReply()     // Catch:{ Exception -> 0x0a03 }
            if (r2 == 0) goto L_0x09e8
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0a03 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a03 }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r5 = org.telegram.messenger.PopupReplyReceiver.class
            r2.<init>(r3, r5)     // Catch:{ Exception -> 0x0a03 }
            int r3 = r15.currentAccount     // Catch:{ Exception -> 0x0a03 }
            r2.putExtra(r8, r3)     // Catch:{ Exception -> 0x0a03 }
            r3 = 19
            if (r1 > r3) goto L_0x09d0
            r1 = 2131165477(0x7var_, float:1.7945172E38)
            java.lang.String r3 = "Reply"
            r5 = 2131627105(0x7f0e0CLASSNAME, float:1.8881465E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r5)     // Catch:{ Exception -> 0x0a03 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a03 }
            r6 = 134217728(0x8000000, float:3.85186E-34)
            r7 = 2
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r5, r7, r2, r6)     // Catch:{ Exception -> 0x0a03 }
            r4.addAction(r1, r3, r2)     // Catch:{ Exception -> 0x0a03 }
            goto L_0x09e8
        L_0x09d0:
            r1 = 2131165476(0x7var_, float:1.794517E38)
            java.lang.String r3 = "Reply"
            r5 = 2131627105(0x7f0e0CLASSNAME, float:1.8881465E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r5)     // Catch:{ Exception -> 0x0a03 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a03 }
            r6 = 134217728(0x8000000, float:3.85186E-34)
            r7 = 2
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r5, r7, r2, r6)     // Catch:{ Exception -> 0x0a03 }
            r4.addAction(r1, r3, r2)     // Catch:{ Exception -> 0x0a03 }
        L_0x09e8:
            r1 = r40
            r2 = r4
            r3 = r36
            r4 = r13
            r6 = r21
            r7 = r0
            r8 = r9
            r9 = r16
            r11 = r39
            r12 = r30
            r13 = r29
            r14 = r25
            r1.showExtraNotifications(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0a03 }
            r40.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0a03 }
            goto L_0x0a07
        L_0x0a03:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0a07:
            return
        L_0x0a08:
            r40.dismissNotification()
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

    /* access modifiers changed from: private */
    public void resetNotificationSound(NotificationCompat.Builder builder, long j, String str, long[] jArr, int i, Uri uri, int i2, boolean z, boolean z2, boolean z3, int i3) {
        long j2 = j;
        int i4 = i3;
        Uri uri2 = Settings.System.DEFAULT_RINGTONE_URI;
        if (uri2 != null && uri != null && !TextUtils.equals(uri2.toString(), uri.toString())) {
            SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
            String uri3 = uri2.toString();
            String string = LocaleController.getString("DefaultRingtone", NUM);
            if (z) {
                if (i4 == 2) {
                    edit.putString("ChannelSound", string);
                } else if (i4 == 0) {
                    edit.putString("GroupSound", string);
                } else {
                    edit.putString("GlobalSound", string);
                }
                if (i4 == 2) {
                    edit.putString("ChannelSoundPath", uri3);
                } else if (i4 == 0) {
                    edit.putString("GroupSoundPath", uri3);
                } else {
                    edit.putString("GlobalSoundPath", uri3);
                }
                getNotificationsController().lambda$deleteNotificationChannelGlobal$32(i4, -1);
            } else {
                edit.putString("sound_" + j2, string);
                edit.putString("sound_path_" + j2, uri3);
                lambda$deleteNotificationChannel$31(j2, -1);
            }
            edit.commit();
            String validateChannelId = validateChannelId(j, str, jArr, i, Settings.System.DEFAULT_RINGTONE_URI, i2, z, z2, z3, i3);
            NotificationCompat.Builder builder2 = builder;
            builder.setChannelId(validateChannelId);
            notificationManager.notify(this.notificationId, builder.build());
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0b0e: MOVE  (r1v40 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>) = 
          (r60v1 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>)
        
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
    /* JADX WARNING: Removed duplicated region for block: B:128:0x030a  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0327  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0336  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x038a  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x039d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x043f  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x045b  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x046f  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0499  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x04a3 A[SYNTHETIC, Splitter:B:185:0x04a3] */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04f9  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04ff  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x050a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0529 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0549  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0552  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x0563  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x059c  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x05d2  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x06bc  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x06c1  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x0731  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x081d  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x083e  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x086d  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x087b A[SYNTHETIC, Splitter:B:378:0x087b] */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x0923  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0935  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x0955  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x09af  */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x09e2  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0a03  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0a27  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0acf  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0ada  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0adf  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0aef  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0af5  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0af9  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0afe  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0b19  */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x0ba2 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x0bd3  */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0CLASSNAME A[Catch:{ JSONException -> 0x0cd8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0cb1 A[Catch:{ JSONException -> 0x0cd8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0cba  */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0cc3 A[Catch:{ JSONException -> 0x0cd8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01de  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01e8  */
    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r93, java.lang.String r94, long r95, java.lang.String r97, long[] r98, int r99, android.net.Uri r100, int r101, boolean r102, boolean r103, boolean r104, int r105) {
        /*
            r92 = this;
            r15 = r92
            r14 = r93
            int r0 = android.os.Build.VERSION.SDK_INT
            r13 = 26
            if (r0 < r13) goto L_0x0027
            r1 = r92
            r2 = r95
            r4 = r97
            r5 = r98
            r6 = r99
            r7 = r100
            r8 = r101
            r9 = r102
            r10 = r103
            r11 = r104
            r12 = r105
            java.lang.String r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r14.setChannelId(r1)
        L_0x0027:
            android.app.Notification r12 = r93.build()
            r1 = 18
            if (r0 >= r1) goto L_0x0040
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.notify(r1, r12)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x003f
            java.lang.String r0 = "show summary notification by SDK check"
            org.telegram.messenger.FileLog.d(r0)
        L_0x003f:
            return
        L_0x0040:
            org.telegram.messenger.AccountInstance r0 = r92.getAccountInstance()
            android.content.SharedPreferences r0 = r0.getNotificationsSettings()
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            android.util.LongSparseArray r9 = new android.util.LongSparseArray
            r9.<init>()
            r10 = 0
            r1 = 0
        L_0x0054:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r15.pushMessages
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x00a1
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r15.pushMessages
            java.lang.Object r2 = r2.get(r1)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            long r3 = r2.getDialogId()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "dismissDate"
            r5.append(r6)
            r5.append(r3)
            java.lang.String r5 = r5.toString()
            int r5 = r0.getInt(r5, r10)
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            int r6 = r6.date
            if (r6 > r5) goto L_0x0084
            goto L_0x009e
        L_0x0084:
            java.lang.Object r5 = r9.get(r3)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x009b
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r9.put(r3, r5)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r11.add(r3)
        L_0x009b:
            r5.add(r2)
        L_0x009e:
            int r1 = r1 + 1
            goto L_0x0054
        L_0x00a1:
            android.util.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            android.util.LongSparseArray r8 = r0.clone()
            android.util.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r0.clear()
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            boolean r0 = org.telegram.messenger.WearDataLayerListenerService.isWatchConnected()
            if (r0 == 0) goto L_0x00be
            org.json.JSONArray r0 = new org.json.JSONArray
            r0.<init>()
            r5 = r0
            goto L_0x00bf
        L_0x00be:
            r5 = 0
        L_0x00bf:
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 27
            r2 = 1
            if (r0 <= r4) goto L_0x00cf
            int r1 = r11.size()
            if (r1 <= r2) goto L_0x00cd
            goto L_0x00cf
        L_0x00cd:
            r3 = 0
            goto L_0x00d0
        L_0x00cf:
            r3 = 1
        L_0x00d0:
            if (r3 == 0) goto L_0x00d7
            if (r0 < r13) goto L_0x00d7
            checkOtherNotificationsChannel()
        L_0x00d7:
            org.telegram.messenger.UserConfig r0 = r92.getUserConfig()
            int r1 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x00ed
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x00ea
            goto L_0x00ed
        L_0x00ea:
            r20 = 0
            goto L_0x00ef
        L_0x00ed:
            r20 = 1
        L_0x00ef:
            r13 = 7
            android.util.LongSparseArray r4 = new android.util.LongSparseArray
            r4.<init>()
            int r6 = r11.size()
            r2 = 0
        L_0x00fa:
            java.lang.String r10 = "id"
            if (r2 >= r6) goto L_0x0cf3
            int r0 = r7.size()
            if (r0 < r13) goto L_0x0106
            goto L_0x0cf3
        L_0x0106:
            java.lang.Object r0 = r11.get(r2)
            java.lang.Long r0 = (java.lang.Long) r0
            r23 = r6
            r22 = r7
            long r6 = r0.longValue()
            java.lang.Object r0 = r9.get(r6)
            r13 = r0
            java.util.ArrayList r13 = (java.util.ArrayList) r13
            r25 = r2
            r2 = 0
            java.lang.Object r0 = r13.get(r2)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r2 = r0.getId()
            r26 = r9
            int r9 = (int) r6
            r27 = r10
            r10 = 32
            r29 = r11
            r28 = r12
            long r11 = r6 >> r10
            int r12 = (int) r11
            java.lang.Object r0 = r8.get(r6)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x014a
            if (r9 == 0) goto L_0x0145
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)
            goto L_0x014d
        L_0x0145:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r12)
            goto L_0x014d
        L_0x014a:
            r8.remove(r6)
        L_0x014d:
            r11 = r0
            if (r5 == 0) goto L_0x0156
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            goto L_0x0157
        L_0x0156:
            r0 = 0
        L_0x0157:
            r10 = 0
            java.lang.Object r31 = r13.get(r10)
            r10 = r31
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            r31 = r0
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            r32 = r8
            int r8 = r0.date
            r33 = 0
            if (r9 == 0) goto L_0x0276
            r0 = 777000(0xbdb28, float:1.088809E-39)
            if (r9 == r0) goto L_0x0173
            r0 = 1
            goto L_0x0174
        L_0x0173:
            r0 = 0
        L_0x0174:
            if (r9 <= 0) goto L_0x01fb
            r35 = r0
            org.telegram.messenger.MessagesController r0 = r92.getMessagesController()
            r36 = r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r5)
            if (r0 != 0) goto L_0x01bb
            boolean r5 = r10.isFcmMessage()
            if (r5 == 0) goto L_0x0195
            java.lang.String r5 = r10.localName
        L_0x0190:
            r37 = r3
            r38 = r4
            goto L_0x01d6
        L_0x0195:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x01ad
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found user to show dialog notification "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x01ad:
            r81 = r1
            r21 = r3
            r72 = r4
            r14 = r22
            r86 = r28
            r12 = r36
            goto L_0x02b4
        L_0x01bb:
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r0)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r10 = r0.photo
            if (r10 == 0) goto L_0x0190
            org.telegram.tgnet.TLRPC$FileLocation r10 = r10.photo_small
            if (r10 == 0) goto L_0x0190
            r37 = r3
            r38 = r4
            long r3 = r10.volume_id
            int r39 = (r3 > r33 ? 1 : (r3 == r33 ? 0 : -1))
            if (r39 == 0) goto L_0x01d6
            int r3 = r10.local_id
            if (r3 == 0) goto L_0x01d6
            goto L_0x01d7
        L_0x01d6:
            r10 = 0
        L_0x01d7:
            long r3 = (long) r9
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((long) r3)
            if (r3 == 0) goto L_0x01e8
            r3 = 2131627098(0x7f0e0c5a, float:1.888145E38)
            java.lang.String r4 = "RepliesTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x01f3
        L_0x01e8:
            if (r9 != r1) goto L_0x01f3
            r3 = 2131626107(0x7f0e087b, float:1.887944E38)
            java.lang.String r4 = "MessageScheduledReminderNotification"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r4, r3)
        L_0x01f3:
            r4 = r0
            r0 = r10
            r10 = r31
            r31 = 0
            goto L_0x02fe
        L_0x01fb:
            r35 = r0
            r37 = r3
            r38 = r4
            r36 = r5
            org.telegram.messenger.MessagesController r0 = r92.getMessagesController()
            int r3 = -r9
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            if (r0 != 0) goto L_0x0245
            boolean r3 = r10.isFcmMessage()
            if (r3 == 0) goto L_0x022c
            boolean r3 = r10.isSupergroup()
            java.lang.String r5 = r10.localName
            boolean r4 = r10.localChannel
        L_0x0220:
            r39 = r3
            r40 = r4
        L_0x0224:
            r10 = r31
            r4 = 0
            r31 = r0
            r0 = 0
            goto L_0x0302
        L_0x022c:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02a8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found chat to show dialog notification "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02a8
        L_0x0245:
            boolean r3 = r0.megagroup
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r4 == 0) goto L_0x0253
            boolean r4 = r0.megagroup
            if (r4 != 0) goto L_0x0253
            r4 = 1
            goto L_0x0254
        L_0x0253:
            r4 = 0
        L_0x0254:
            java.lang.String r5 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r10 = r0.photo
            if (r10 == 0) goto L_0x0220
            org.telegram.tgnet.TLRPC$FileLocation r10 = r10.photo_small
            if (r10 == 0) goto L_0x0220
            r39 = r3
            r40 = r4
            long r3 = r10.volume_id
            int r41 = (r3 > r33 ? 1 : (r3 == r33 ? 0 : -1))
            if (r41 == 0) goto L_0x0224
            int r3 = r10.local_id
            if (r3 == 0) goto L_0x0224
            r4 = 0
            r91 = r31
            r31 = r0
            r0 = r10
            r10 = r91
            goto L_0x0302
        L_0x0276:
            r37 = r3
            r38 = r4
            r36 = r5
            long r3 = globalSecretChatId
            int r0 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x02ed
            org.telegram.messenger.MessagesController r0 = r92.getMessagesController()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r3)
            if (r0 != 0) goto L_0x02c2
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02a8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found secret chat to show dialog notification "
            r0.append(r2)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x02a8:
            r81 = r1
            r14 = r22
            r86 = r28
            r12 = r36
            r21 = r37
            r72 = r38
        L_0x02b4:
            r22 = 0
            r24 = 7
            r28 = 1
            r30 = 26
            r34 = 27
            r35 = 0
            goto L_0x0cda
        L_0x02c2:
            org.telegram.messenger.MessagesController r3 = r92.getMessagesController()
            int r4 = r0.user_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 != 0) goto L_0x02ee
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x02a8
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "not found secret chat user to show dialog notification "
            r2.append(r3)
            int r0 = r0.user_id
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02a8
        L_0x02ed:
            r3 = 0
        L_0x02ee:
            r0 = 2131627267(0x7f0e0d03, float:1.8881794E38)
            java.lang.String r4 = "SecretChatName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r4 = r3
            r0 = 0
            r10 = 0
            r31 = 0
            r35 = 0
        L_0x02fe:
            r39 = 0
            r40 = 0
        L_0x0302:
            java.lang.String r3 = "NotificationHiddenChatName"
            r42 = r5
            java.lang.String r5 = "NotificationHiddenName"
            if (r20 == 0) goto L_0x0327
            if (r9 >= 0) goto L_0x0316
            r44 = r4
            r4 = 2131626366(0x7f0e097e, float:1.8879966E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r4)
            goto L_0x031f
        L_0x0316:
            r44 = r4
            r4 = 2131626369(0x7f0e0981, float:1.8879972E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r4)
        L_0x031f:
            r14 = r0
            r35 = r8
            r42 = r12
            r4 = 0
            r8 = 0
            goto L_0x0334
        L_0x0327:
            r44 = r4
            r4 = r0
            r14 = r42
            r42 = r12
            r91 = r35
            r35 = r8
            r8 = r91
        L_0x0334:
            if (r4 == 0) goto L_0x038a
            r12 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r4, r12)
            int r12 = android.os.Build.VERSION.SDK_INT
            r45 = r5
            r5 = 28
            if (r12 >= r5) goto L_0x0384
            org.telegram.messenger.ImageLoader r5 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r12 = "50_50"
            r46 = r3
            r3 = 0
            android.graphics.drawable.BitmapDrawable r5 = r5.getImageFromMemory(r4, r3, r12)
            if (r5 == 0) goto L_0x0359
            android.graphics.Bitmap r5 = r5.getBitmap()
        L_0x0356:
            r12 = r5
            r5 = r0
            goto L_0x0391
        L_0x0359:
            boolean r5 = r0.exists()     // Catch:{ all -> 0x0387 }
            if (r5 == 0) goto L_0x0382
            r5 = 1126170624(0x43200000, float:160.0)
            r12 = 1112014848(0x42480000, float:50.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x0387 }
            float r12 = (float) r12     // Catch:{ all -> 0x0387 }
            float r5 = r5 / r12
            android.graphics.BitmapFactory$Options r12 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0387 }
            r12.<init>()     // Catch:{ all -> 0x0387 }
            r18 = 1065353216(0x3var_, float:1.0)
            int r18 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1))
            if (r18 >= 0) goto L_0x0376
            r5 = 1
            goto L_0x0377
        L_0x0376:
            int r5 = (int) r5     // Catch:{ all -> 0x0387 }
        L_0x0377:
            r12.inSampleSize = r5     // Catch:{ all -> 0x0387 }
            java.lang.String r5 = r0.getAbsolutePath()     // Catch:{ all -> 0x0387 }
            android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeFile(r5, r12)     // Catch:{ all -> 0x0387 }
            goto L_0x0356
        L_0x0382:
            r5 = r3
            goto L_0x0356
        L_0x0384:
            r46 = r3
            r3 = 0
        L_0x0387:
            r5 = r0
            r12 = r3
            goto L_0x0391
        L_0x038a:
            r46 = r3
            r45 = r5
            r3 = 0
            r5 = r3
            r12 = r5
        L_0x0391:
            java.lang.String r3 = "dialog_id"
            r47 = r5
            java.lang.String r5 = "max_id"
            r48 = r4
            java.lang.String r4 = "currentAccount"
            if (r40 == 0) goto L_0x039f
            if (r39 == 0) goto L_0x042c
        L_0x039f:
            if (r8 == 0) goto L_0x042c
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x042c
            if (r1 == r9) goto L_0x042c
            r49 = r1
            long r0 = (long) r9
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r0)
            if (r0 != 0) goto L_0x042e
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r50 = r8
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r8 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r1, r8)
            r0.putExtra(r3, r6)
            r0.putExtra(r5, r2)
            int r1 = r15.currentAccount
            r0.putExtra(r4, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r8 = r11.intValue()
            r51 = r12
            r12 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r1, r8, r0, r12)
            androidx.core.app.RemoteInput$Builder r1 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r8 = "extra_voice_reply"
            r1.<init>(r8)
            r8 = 2131627105(0x7f0e0CLASSNAME, float:1.8881465E38)
            java.lang.String r12 = "Reply"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r12, r8)
            r1.setLabel(r8)
            androidx.core.app.RemoteInput r1 = r1.build()
            if (r9 >= 0) goto L_0x03ff
            r12 = 1
            java.lang.Object[] r8 = new java.lang.Object[r12]
            r12 = 0
            r8[r12] = r14
            java.lang.String r12 = "ReplyToGroup"
            r52 = r11
            r11 = 2131627106(0x7f0e0CLASSNAME, float:1.8881467E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r12, r11, r8)
            goto L_0x0410
        L_0x03ff:
            r52 = r11
            r8 = 2131627107(0x7f0e0CLASSNAME, float:1.888147E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]
            r11 = 0
            r12[r11] = r14
            java.lang.String r11 = "ReplyToUser"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r11, r8, r12)
        L_0x0410:
            androidx.core.app.NotificationCompat$Action$Builder r11 = new androidx.core.app.NotificationCompat$Action$Builder
            r12 = 2131165521(0x7var_, float:1.7945261E38)
            r11.<init>(r12, r8, r0)
            r8 = 1
            r11.setAllowGeneratedReplies(r8)
            r11.setSemanticAction(r8)
            r11.addRemoteInput(r1)
            r1 = 0
            r11.setShowsUserInterface(r1)
            androidx.core.app.NotificationCompat$Action r0 = r11.build()
            r1 = r0
            goto L_0x0435
        L_0x042c:
            r49 = r1
        L_0x042e:
            r50 = r8
            r52 = r11
            r51 = r12
            r1 = 0
        L_0x0435:
            android.util.LongSparseArray<java.lang.Integer> r0 = r15.pushDialogs
            java.lang.Object r0 = r0.get(r6)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0444
            r8 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r8)
        L_0x0444:
            int r0 = r0.intValue()
            int r8 = r13.size()
            int r0 = java.lang.Math.max(r0, r8)
            r8 = 2
            r11 = 1
            if (r0 <= r11) goto L_0x046f
            int r12 = android.os.Build.VERSION.SDK_INT
            r11 = 28
            if (r12 < r11) goto L_0x045b
            goto L_0x046f
        L_0x045b:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            r12 = 0
            r11[r12] = r14
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r12 = 1
            r11[r12] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r11)
            r11 = r0
            goto L_0x0470
        L_0x046f:
            r11 = r14
        L_0x0470:
            r12 = r49
            r49 = r9
            long r8 = (long) r12
            r53 = r2
            r2 = r38
            java.lang.Object r0 = r2.get(r8)
            r38 = r0
            androidx.core.app.Person r38 = (androidx.core.app.Person) r38
            int r0 = android.os.Build.VERSION.SDK_INT
            r54 = r5
            r5 = 28
            if (r0 < r5) goto L_0x04ef
            if (r38 != 0) goto L_0x04ef
            org.telegram.messenger.MessagesController r0 = r92.getMessagesController()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r5)
            if (r0 != 0) goto L_0x04a1
            org.telegram.messenger.UserConfig r0 = r92.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x04a1:
            if (r0 == 0) goto L_0x04ef
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r0.photo     // Catch:{ all -> 0x04e6 }
            if (r5 == 0) goto L_0x04ef
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ all -> 0x04e6 }
            if (r5 == 0) goto L_0x04ef
            r55 = r3
            r56 = r4
            long r3 = r5.volume_id     // Catch:{ all -> 0x04e4 }
            int r57 = (r3 > r33 ? 1 : (r3 == r33 ? 0 : -1))
            if (r57 == 0) goto L_0x04f3
            int r3 = r5.local_id     // Catch:{ all -> 0x04e4 }
            if (r3 == 0) goto L_0x04f3
            androidx.core.app.Person$Builder r3 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x04e4 }
            r3.<init>()     // Catch:{ all -> 0x04e4 }
            java.lang.String r4 = "FromYou"
            r5 = 2131625638(0x7f0e06a6, float:1.887849E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r5)     // Catch:{ all -> 0x04e4 }
            r3.setName(r4)     // Catch:{ all -> 0x04e4 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x04e4 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x04e4 }
            r4 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r4)     // Catch:{ all -> 0x04e4 }
            r15.loadRoundAvatar(r0, r3)     // Catch:{ all -> 0x04e4 }
            androidx.core.app.Person r3 = r3.build()     // Catch:{ all -> 0x04e4 }
            r2.put(r8, r3)     // Catch:{ all -> 0x04e0 }
            r38 = r3
            goto L_0x04f3
        L_0x04e0:
            r0 = move-exception
            r38 = r3
            goto L_0x04eb
        L_0x04e4:
            r0 = move-exception
            goto L_0x04eb
        L_0x04e6:
            r0 = move-exception
            r55 = r3
            r56 = r4
        L_0x04eb:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x04f3
        L_0x04ef:
            r55 = r3
            r56 = r4
        L_0x04f3:
            r0 = r38
            java.lang.String r3 = ""
            if (r0 == 0) goto L_0x04ff
            androidx.core.app.NotificationCompat$MessagingStyle r4 = new androidx.core.app.NotificationCompat$MessagingStyle
            r4.<init>((androidx.core.app.Person) r0)
            goto L_0x0504
        L_0x04ff:
            androidx.core.app.NotificationCompat$MessagingStyle r4 = new androidx.core.app.NotificationCompat$MessagingStyle
            r4.<init>((java.lang.CharSequence) r3)
        L_0x0504:
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 28
            if (r0 < r5) goto L_0x051c
            if (r49 >= 0) goto L_0x050e
            if (r40 == 0) goto L_0x051c
        L_0x050e:
            r38 = r1
            r5 = r49
            r49 = r2
            long r1 = (long) r5
            boolean r1 = org.telegram.messenger.UserObject.isReplyUser((long) r1)
            if (r1 == 0) goto L_0x0525
            goto L_0x0522
        L_0x051c:
            r38 = r1
            r5 = r49
            r49 = r2
        L_0x0522:
            r4.setConversationTitle(r11)
        L_0x0525:
            r1 = 28
            if (r0 < r1) goto L_0x0537
            if (r40 != 0) goto L_0x052d
            if (r5 < 0) goto L_0x0537
        L_0x052d:
            long r0 = (long) r5
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r0)
            if (r0 == 0) goto L_0x0535
            goto L_0x0537
        L_0x0535:
            r0 = 0
            goto L_0x0538
        L_0x0537:
            r0 = 1
        L_0x0538:
            r4.setGroupConversation(r0)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 1
            java.lang.String[] r11 = new java.lang.String[r2]
            r57 = r12
            boolean[] r12 = new boolean[r2]
            if (r10 == 0) goto L_0x0552
            org.json.JSONArray r0 = new org.json.JSONArray
            r0.<init>()
            r58 = r10
            r10 = r0
            goto L_0x0555
        L_0x0552:
            r58 = r10
            r10 = 0
        L_0x0555:
            int r0 = r13.size()
            int r0 = r0 - r2
            r2 = r0
            r59 = 0
            r60 = 0
        L_0x055f:
            r61 = 1000(0x3e8, double:4.94E-321)
            if (r2 < 0) goto L_0x08e6
            java.lang.Object r0 = r13.get(r2)
            r63 = r13
            r13 = r0
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            java.lang.String r0 = r15.getShortStringForMessage(r13, r11, r12)
            int r64 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r64 != 0) goto L_0x0579
            r21 = 0
            r11[r21] = r14
            goto L_0x0596
        L_0x0579:
            r21 = 0
            if (r5 >= 0) goto L_0x0596
            r64 = r14
            org.telegram.tgnet.TLRPC$Message r14 = r13.messageOwner
            boolean r14 = r14.from_scheduled
            if (r14 == 0) goto L_0x0593
            r14 = 2131626412(0x7f0e09ac, float:1.888006E38)
            r65 = r2
            java.lang.String r2 = "NotificationMessageScheduledName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r14)
            r11[r21] = r2
            goto L_0x059a
        L_0x0593:
            r65 = r2
            goto L_0x059a
        L_0x0596:
            r65 = r2
            r64 = r14
        L_0x059a:
            if (r0 != 0) goto L_0x05d2
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05c4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "message text is null for "
            r0.append(r2)
            int r2 = r13.getId()
            r0.append(r2)
            java.lang.String r2 = " did = "
            r0.append(r2)
            long r13 = r13.getDialogId()
            r0.append(r13)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x05c4:
            r68 = r6
            r66 = r8
            r70 = r11
            r43 = r45
            r14 = r49
            r49 = r1
            goto L_0x08d2
        L_0x05d2:
            int r2 = r1.length()
            if (r2 <= 0) goto L_0x05dd
            java.lang.String r2 = "\n\n"
            r1.append(r2)
        L_0x05dd:
            int r2 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r2 == 0) goto L_0x0607
            org.telegram.tgnet.TLRPC$Message r2 = r13.messageOwner
            boolean r2 = r2.from_scheduled
            if (r2 == 0) goto L_0x0607
            if (r5 <= 0) goto L_0x0607
            r2 = 2
            java.lang.Object[] r14 = new java.lang.Object[r2]
            r2 = 2131626412(0x7f0e09ac, float:1.888006E38)
            r66 = r8
            java.lang.String r8 = "NotificationMessageScheduledName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            r8 = 0
            r14[r8] = r2
            r2 = 1
            r14[r2] = r0
            java.lang.String r0 = "%1$s: %2$s"
            java.lang.String r0 = java.lang.String.format(r0, r14)
            r1.append(r0)
            goto L_0x0625
        L_0x0607:
            r66 = r8
            r8 = 0
            r2 = r11[r8]
            if (r2 == 0) goto L_0x0622
            r2 = 2
            java.lang.Object[] r9 = new java.lang.Object[r2]
            r2 = r11[r8]
            r9[r8] = r2
            r2 = 1
            r9[r2] = r0
            java.lang.String r2 = "%1$s: %2$s"
            java.lang.String r2 = java.lang.String.format(r2, r9)
            r1.append(r2)
            goto L_0x0625
        L_0x0622:
            r1.append(r0)
        L_0x0625:
            r2 = r0
            if (r5 <= 0) goto L_0x062c
            long r8 = (long) r5
        L_0x0629:
            r14 = r49
            goto L_0x063a
        L_0x062c:
            if (r40 == 0) goto L_0x0631
            int r0 = -r5
        L_0x062f:
            long r8 = (long) r0
            goto L_0x0629
        L_0x0631:
            if (r5 >= 0) goto L_0x0638
            int r0 = r13.getSenderId()
            goto L_0x062f
        L_0x0638:
            r8 = r6
            goto L_0x0629
        L_0x063a:
            java.lang.Object r0 = r14.get(r8)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r21 = 0
            r49 = r11[r21]
            if (r49 != 0) goto L_0x069c
            if (r20 == 0) goto L_0x068f
            if (r5 >= 0) goto L_0x0676
            if (r40 == 0) goto L_0x0662
            r49 = r1
            int r1 = android.os.Build.VERSION.SDK_INT
            r68 = r6
            r6 = 27
            r7 = r46
            if (r1 <= r6) goto L_0x068c
            r1 = 2131626366(0x7f0e097e, float:1.8879966E38)
            java.lang.String r17 = org.telegram.messenger.LocaleController.getString(r7, r1)
            r1 = r17
            goto L_0x0673
        L_0x0662:
            r49 = r1
            r68 = r6
            r7 = r46
            r6 = 27
            r1 = 2131626367(0x7f0e097f, float:1.8879968E38)
            java.lang.String r6 = "NotificationHiddenChatUserName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
        L_0x0673:
            r6 = r45
            goto L_0x06ad
        L_0x0676:
            r49 = r1
            r68 = r6
            r7 = r46
            int r1 = android.os.Build.VERSION.SDK_INT
            r6 = 27
            if (r1 <= r6) goto L_0x068c
            r6 = r45
            r1 = 2131626369(0x7f0e0981, float:1.8879972E38)
            java.lang.String r43 = org.telegram.messenger.LocaleController.getString(r6, r1)
            goto L_0x06ab
        L_0x068c:
            r6 = r45
            goto L_0x0697
        L_0x068f:
            r49 = r1
            r68 = r6
            r6 = r45
            r7 = r46
        L_0x0697:
            r1 = 2131626369(0x7f0e0981, float:1.8879972E38)
            r1 = r3
            goto L_0x06ad
        L_0x069c:
            r49 = r1
            r68 = r6
            r6 = r45
            r7 = r46
            r1 = 2131626369(0x7f0e0981, float:1.8879972E38)
            r21 = 0
            r43 = r11[r21]
        L_0x06ab:
            r1 = r43
        L_0x06ad:
            r43 = r6
            if (r0 == 0) goto L_0x06c1
            java.lang.CharSequence r6 = r0.getName()
            boolean r6 = android.text.TextUtils.equals(r6, r1)
            if (r6 != 0) goto L_0x06bc
            goto L_0x06c1
        L_0x06bc:
            r1 = r0
            r46 = r7
            goto L_0x072f
        L_0x06c1:
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            r0.setName(r1)
            r1 = 0
            boolean r6 = r12[r1]
            if (r6 == 0) goto L_0x0725
            if (r5 == 0) goto L_0x0725
            int r1 = android.os.Build.VERSION.SDK_INT
            r6 = 28
            if (r1 < r6) goto L_0x0725
            if (r5 > 0) goto L_0x071d
            if (r40 == 0) goto L_0x06db
            goto L_0x071d
        L_0x06db:
            int r1 = r13.getSenderId()
            org.telegram.messenger.MessagesController r6 = r92.getMessagesController()
            r46 = r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r6 != 0) goto L_0x0701
            org.telegram.messenger.MessagesStorage r6 = r92.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r6 = r6.getUserSync(r1)
            if (r6 == 0) goto L_0x0701
            org.telegram.messenger.MessagesController r1 = r92.getMessagesController()
            r7 = 1
            r1.putUser(r6, r7)
        L_0x0701:
            if (r6 == 0) goto L_0x071b
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r6.photo
            if (r1 == 0) goto L_0x071b
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            if (r1 == 0) goto L_0x071b
            long r6 = r1.volume_id
            int r70 = (r6 > r33 ? 1 : (r6 == r33 ? 0 : -1))
            if (r70 == 0) goto L_0x071b
            int r6 = r1.local_id
            if (r6 == 0) goto L_0x071b
            r6 = 1
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r6)
            goto L_0x0721
        L_0x071b:
            r1 = 0
            goto L_0x0721
        L_0x071d:
            r46 = r7
            r1 = r47
        L_0x0721:
            r15.loadRoundAvatar(r1, r0)
            goto L_0x0727
        L_0x0725:
            r46 = r7
        L_0x0727:
            androidx.core.app.Person r0 = r0.build()
            r14.put(r8, r0)
            r1 = r0
        L_0x072f:
            if (r5 == 0) goto L_0x086d
            r6 = 0
            boolean r0 = r12[r6]
            if (r0 == 0) goto L_0x0818
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 28
            if (r0 < r6) goto L_0x0818
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r7 = "activity"
            java.lang.Object r0 = r0.getSystemService(r7)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x0818
            if (r20 != 0) goto L_0x0818
            boolean r0 = r13.isSecretMedia()
            if (r0 != 0) goto L_0x0818
            int r0 = r13.type
            r7 = 1
            if (r0 == r7) goto L_0x075f
            boolean r0 = r13.isSticker()
            if (r0 == 0) goto L_0x0818
        L_0x075f:
            org.telegram.tgnet.TLRPC$Message r0 = r13.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r7 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r8 = r13.messageOwner
            int r8 = r8.date
            long r8 = (long) r8
            long r8 = r8 * r61
            r7.<init>(r2, r8, r1)
            boolean r8 = r13.isSticker()
            if (r8 == 0) goto L_0x077a
            java.lang.String r8 = "image/webp"
            goto L_0x077c
        L_0x077a:
            java.lang.String r8 = "image/jpeg"
        L_0x077c:
            boolean r9 = r0.exists()
            if (r9 == 0) goto L_0x0792
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x078d }
            java.lang.String r6 = "org.telegram.messenger.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r9, r6, r0)     // Catch:{ Exception -> 0x078d }
            r70 = r11
            goto L_0x07e8
        L_0x078d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x07e5
        L_0x0792:
            org.telegram.messenger.FileLoader r6 = r92.getFileLoader()
            java.lang.String r9 = r0.getName()
            boolean r6 = r6.isLoadingFile(r9)
            if (r6 == 0) goto L_0x07e5
            android.net.Uri$Builder r6 = new android.net.Uri$Builder
            r6.<init>()
            java.lang.String r9 = "content"
            android.net.Uri$Builder r6 = r6.scheme(r9)
            java.lang.String r9 = "org.telegram.messenger.notification_image_provider"
            android.net.Uri$Builder r6 = r6.authority(r9)
            java.lang.String r9 = "msg_media_raw"
            android.net.Uri$Builder r6 = r6.appendPath(r9)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r70 = r11
            int r11 = r15.currentAccount
            r9.append(r11)
            r9.append(r3)
            java.lang.String r9 = r9.toString()
            android.net.Uri$Builder r6 = r6.appendPath(r9)
            java.lang.String r9 = r0.getName()
            android.net.Uri$Builder r6 = r6.appendPath(r9)
            java.lang.String r0 = r0.getAbsolutePath()
            java.lang.String r9 = "final_path"
            android.net.Uri$Builder r0 = r6.appendQueryParameter(r9, r0)
            android.net.Uri r0 = r0.build()
            goto L_0x07e8
        L_0x07e5:
            r70 = r11
            r0 = 0
        L_0x07e8:
            if (r0 == 0) goto L_0x081a
            r7.setData(r8, r0)
            r4.addMessage(r7)
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r7 = "com.android.systemui"
            r8 = 1
            r6.grantUriPermission(r7, r0, r8)
            org.telegram.messenger.-$$Lambda$NotificationsController$0YINMSsEaa1VtQ6qrU-ZxF9e9ro r6 = new org.telegram.messenger.-$$Lambda$NotificationsController$0YINMSsEaa1VtQ6qrU-ZxF9e9ro
            r6.<init>(r0)
            r7 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6, r7)
            java.lang.CharSequence r0 = r13.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0816
            java.lang.CharSequence r0 = r13.caption
            org.telegram.tgnet.TLRPC$Message r6 = r13.messageOwner
            int r6 = r6.date
            long r6 = (long) r6
            long r6 = r6 * r61
            r4.addMessage(r0, r6, r1)
        L_0x0816:
            r0 = 1
            goto L_0x081b
        L_0x0818:
            r70 = r11
        L_0x081a:
            r0 = 0
        L_0x081b:
            if (r0 != 0) goto L_0x0827
            org.telegram.tgnet.TLRPC$Message r0 = r13.messageOwner
            int r0 = r0.date
            long r6 = (long) r0
            long r6 = r6 * r61
            r4.addMessage(r2, r6, r1)
        L_0x0827:
            r1 = 0
            boolean r0 = r12[r1]
            if (r0 == 0) goto L_0x0879
            if (r20 != 0) goto L_0x0879
            boolean r0 = r13.isVoice()
            if (r0 == 0) goto L_0x0879
            java.util.List r0 = r4.getMessages()
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x0879
            org.telegram.tgnet.TLRPC$Message r1 = r13.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            int r6 = android.os.Build.VERSION.SDK_INT
            r7 = 24
            if (r6 < r7) goto L_0x0855
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0853 }
            java.lang.String r7 = "org.telegram.messenger.provider"
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r6, r7, r1)     // Catch:{ Exception -> 0x0853 }
            goto L_0x0859
        L_0x0853:
            r1 = 0
            goto L_0x0859
        L_0x0855:
            android.net.Uri r1 = android.net.Uri.fromFile(r1)
        L_0x0859:
            if (r1 == 0) goto L_0x0879
            int r6 = r0.size()
            r7 = 1
            int r6 = r6 - r7
            java.lang.Object r0 = r0.get(r6)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r0
            java.lang.String r6 = "audio/ogg"
            r0.setData(r6, r1)
            goto L_0x0879
        L_0x086d:
            r70 = r11
            org.telegram.tgnet.TLRPC$Message r0 = r13.messageOwner
            int r0 = r0.date
            long r6 = (long) r0
            long r6 = r6 * r61
            r4.addMessage(r2, r6, r1)
        L_0x0879:
            if (r10 == 0) goto L_0x08bb
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x08ba }
            r0.<init>()     // Catch:{ JSONException -> 0x08ba }
            java.lang.String r1 = "text"
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x08ba }
            java.lang.String r1 = "date"
            org.telegram.tgnet.TLRPC$Message r2 = r13.messageOwner     // Catch:{ JSONException -> 0x08ba }
            int r2 = r2.date     // Catch:{ JSONException -> 0x08ba }
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x08ba }
            boolean r1 = r13.isFromUser()     // Catch:{ JSONException -> 0x08ba }
            if (r1 == 0) goto L_0x08b6
            if (r5 >= 0) goto L_0x08b6
            org.telegram.messenger.MessagesController r1 = r92.getMessagesController()     // Catch:{ JSONException -> 0x08ba }
            int r2 = r13.getSenderId()     // Catch:{ JSONException -> 0x08ba }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ JSONException -> 0x08ba }
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)     // Catch:{ JSONException -> 0x08ba }
            if (r1 == 0) goto L_0x08b6
            java.lang.String r2 = "fname"
            java.lang.String r6 = r1.first_name     // Catch:{ JSONException -> 0x08ba }
            r0.put(r2, r6)     // Catch:{ JSONException -> 0x08ba }
            java.lang.String r2 = "lname"
            java.lang.String r1 = r1.last_name     // Catch:{ JSONException -> 0x08ba }
            r0.put(r2, r1)     // Catch:{ JSONException -> 0x08ba }
        L_0x08b6:
            r10.put(r0)     // Catch:{ JSONException -> 0x08ba }
            goto L_0x08bb
        L_0x08ba:
        L_0x08bb:
            r0 = 777000(0xbdb28, double:3.83889E-318)
            int r2 = (r68 > r0 ? 1 : (r68 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x08d2
            org.telegram.tgnet.TLRPC$Message r0 = r13.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x08d2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r1 = r13.getId()
            r60 = r0
            r59 = r1
        L_0x08d2:
            int r2 = r65 + -1
            r45 = r43
            r1 = r49
            r13 = r63
            r8 = r66
            r6 = r68
            r11 = r70
            r49 = r14
            r14 = r64
            goto L_0x055f
        L_0x08e6:
            r68 = r6
            r63 = r13
            r64 = r14
            r14 = r49
            r49 = r1
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.ui.LaunchActivity> r2 = org.telegram.ui.LaunchActivity.class
            r0.<init>(r1, r2)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "com.tmessages.openchat"
            r1.append(r2)
            double r2 = java.lang.Math.random()
            r1.append(r2)
            r2 = 2147483647(0x7fffffff, float:NaN)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setAction(r1)
            r1 = 67108864(0x4000000, float:1.5046328E-36)
            r0.setFlags(r1)
            java.lang.String r1 = "android.intent.category.LAUNCHER"
            r0.addCategory(r1)
            if (r5 == 0) goto L_0x0935
            if (r5 <= 0) goto L_0x092c
            java.lang.String r1 = "userId"
            r0.putExtra(r1, r5)
            goto L_0x0932
        L_0x092c:
            int r1 = -r5
            java.lang.String r2 = "chatId"
            r0.putExtra(r2, r1)
        L_0x0932:
            r2 = r42
            goto L_0x093c
        L_0x0935:
            java.lang.String r1 = "encId"
            r2 = r42
            r0.putExtra(r1, r2)
        L_0x093c:
            int r1 = r15.currentAccount
            r3 = r56
            r0.putExtra(r3, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r6 = 1073741824(0x40000000, float:2.0)
            r7 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r1, r7, r0, r6)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            r6 = r38
            if (r38 == 0) goto L_0x0958
            r1.addAction(r6)
        L_0x0958:
            android.content.Intent r7 = new android.content.Intent
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r9 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r7.<init>(r8, r9)
            r8 = 32
            r7.addFlags(r8)
            java.lang.String r8 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r7.setAction(r8)
            r11 = r55
            r8 = r68
            r7.putExtra(r11, r8)
            r11 = r53
            r12 = r54
            r7.putExtra(r12, r11)
            int r13 = r15.currentAccount
            r7.putExtra(r3, r13)
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            r30 = r10
            int r10 = r52.intValue()
            r12 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r7 = android.app.PendingIntent.getBroadcast(r13, r10, r7, r12)
            androidx.core.app.NotificationCompat$Action$Builder r10 = new androidx.core.app.NotificationCompat$Action$Builder
            r12 = 2131165678(0x7var_ee, float:1.794558E38)
            r13 = 2131626019(0x7f0e0823, float:1.8879262E38)
            r38 = r14
            java.lang.String r14 = "MarkAsRead"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r10.<init>(r12, r13, r7)
            r7 = 2
            r10.setSemanticAction(r7)
            r7 = 0
            r10.setShowsUserInterface(r7)
            androidx.core.app.NotificationCompat$Action r7 = r10.build()
            java.lang.String r14 = "_"
            if (r5 == 0) goto L_0x09e2
            if (r5 <= 0) goto L_0x09c9
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "tguser"
            r2.append(r10)
            r2.append(r5)
            r2.append(r14)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            goto L_0x0a01
        L_0x09c9:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "tgchat"
            r2.append(r10)
            int r10 = -r5
            r2.append(r10)
            r2.append(r14)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            goto L_0x0a01
        L_0x09e2:
            long r12 = globalSecretChatId
            int r10 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r10 == 0) goto L_0x0a00
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r12 = "tgenc"
            r10.append(r12)
            r10.append(r2)
            r10.append(r14)
            r10.append(r11)
            java.lang.String r2 = r10.toString()
            goto L_0x0a01
        L_0x0a00:
            r2 = 0
        L_0x0a01:
            if (r2 == 0) goto L_0x0a27
            r1.setDismissalId(r2)
            androidx.core.app.NotificationCompat$WearableExtender r10 = new androidx.core.app.NotificationCompat$WearableExtender
            r10.<init>()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "summary_"
            r12.append(r13)
            r12.append(r2)
            java.lang.String r2 = r12.toString()
            r10.setDismissalId(r2)
            r13 = r93
            r12 = r64
            r13.extend(r10)
            goto L_0x0a2b
        L_0x0a27:
            r13 = r93
            r12 = r64
        L_0x0a2b:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "tgaccount"
            r2.append(r10)
            r10 = r57
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            r1.setBridgeTag(r2)
            r2 = r63
            r10 = 0
            java.lang.Object r33 = r2.get(r10)
            r10 = r33
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            int r10 = r10.date
            r53 = r11
            long r10 = (long) r10
            long r10 = r10 * r61
            androidx.core.app.NotificationCompat$Builder r13 = new androidx.core.app.NotificationCompat$Builder
            r33 = r14
            android.content.Context r14 = org.telegram.messenger.ApplicationLoader.applicationContext
            r13.<init>(r14)
            r13.setContentTitle(r12)
            r14 = 2131165851(0x7var_b, float:1.794593E38)
            r13.setSmallIcon(r14)
            java.lang.String r14 = r49.toString()
            r13.setContentText(r14)
            r14 = 1
            r13.setAutoCancel(r14)
            int r2 = r2.size()
            r13.setNumber(r2)
            r2 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r13.setColor(r2)
            r2 = 0
            r13.setGroupSummary(r2)
            r13.setWhen(r10)
            r13.setShowWhen(r14)
            r13.setStyle(r4)
            r13.setContentIntent(r0)
            r13.extend(r1)
            r0 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r0 = r0 - r10
            java.lang.String r0 = java.lang.String.valueOf(r0)
            r13.setSortKey(r0)
            java.lang.String r0 = "msg"
            r13.setCategory(r0)
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r2 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r1, r2)
            java.lang.String r1 = "messageDate"
            r10 = r35
            r0.putExtra(r1, r10)
            java.lang.String r1 = "dialogId"
            r0.putExtra(r1, r8)
            int r1 = r15.currentAccount
            r0.putExtra(r3, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r2 = r52.intValue()
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r1, r2, r0, r4)
            r13.setDeleteIntent(r0)
            if (r37 == 0) goto L_0x0ad8
            java.lang.String r0 = r15.notificationGroup
            r13.setGroup(r0)
            r1 = 1
            r13.setGroupAlertBehavior(r1)
        L_0x0ad8:
            if (r6 == 0) goto L_0x0add
            r13.addAction(r6)
        L_0x0add:
            if (r20 != 0) goto L_0x0ae2
            r13.addAction(r7)
        L_0x0ae2:
            int r0 = r29.size()
            r2 = 1
            if (r0 != r2) goto L_0x0af5
            boolean r0 = android.text.TextUtils.isEmpty(r94)
            if (r0 != 0) goto L_0x0af5
            r14 = r94
            r13.setSubText(r14)
            goto L_0x0af7
        L_0x0af5:
            r14 = r94
        L_0x0af7:
            if (r5 != 0) goto L_0x0afc
            r13.setLocalOnly(r2)
        L_0x0afc:
            if (r51 == 0) goto L_0x0b03
            r1 = r51
            r13.setLargeIcon(r1)
        L_0x0b03:
            r1 = 0
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1)
            if (r0 != 0) goto L_0x0b9f
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0b9f
            r1 = r60
            if (r1 == 0) goto L_0x0b9f
            int r0 = r1.size()
            r4 = 0
        L_0x0b17:
            if (r4 >= r0) goto L_0x0b9f
            java.lang.Object r6 = r1.get(r4)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r6 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r7 = r6.buttons
            int r7 = r7.size()
            r11 = 0
        L_0x0b26:
            if (r11 >= r7) goto L_0x0b8f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r2 = r6.buttons
            java.lang.Object r2 = r2.get(r11)
            org.telegram.tgnet.TLRPC$KeyboardButton r2 = (org.telegram.tgnet.TLRPC$KeyboardButton) r2
            r34 = r0
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback
            if (r0 == 0) goto L_0x0b76
            android.content.Intent r0 = new android.content.Intent
            r35 = r1
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r41 = r6
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r6 = org.telegram.messenger.NotificationCallbackReceiver.class
            r0.<init>(r1, r6)
            int r1 = r15.currentAccount
            r0.putExtra(r3, r1)
            java.lang.String r1 = "did"
            r0.putExtra(r1, r8)
            byte[] r1 = r2.data
            if (r1 == 0) goto L_0x0b56
            java.lang.String r6 = "data"
            r0.putExtra(r6, r1)
        L_0x0b56:
            java.lang.String r1 = "mid"
            r6 = r59
            r0.putExtra(r1, r6)
            java.lang.String r1 = r2.text
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            r56 = r3
            int r3 = r15.lastButtonId
            r42 = r6
            int r6 = r3 + 1
            r15.lastButtonId = r6
            r6 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r2, r3, r0, r6)
            r2 = 0
            r13.addAction(r2, r1, r0)
            goto L_0x0b81
        L_0x0b76:
            r35 = r1
            r56 = r3
            r41 = r6
            r42 = r59
            r2 = 0
            r6 = 134217728(0x8000000, float:3.85186E-34)
        L_0x0b81:
            int r11 = r11 + 1
            r0 = r34
            r1 = r35
            r6 = r41
            r59 = r42
            r3 = r56
            r2 = 1
            goto L_0x0b26
        L_0x0b8f:
            r34 = r0
            r35 = r1
            r56 = r3
            r42 = r59
            r2 = 0
            r6 = 134217728(0x8000000, float:3.85186E-34)
            int r4 = r4 + 1
            r2 = 1
            goto L_0x0b17
        L_0x0b9f:
            r2 = 0
            if (r31 != 0) goto L_0x0bc7
            if (r44 == 0) goto L_0x0bc7
            r4 = r44
            java.lang.String r0 = r4.phone
            if (r0 == 0) goto L_0x0bc9
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0bc9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "tel:+"
            r0.append(r1)
            java.lang.String r1 = r4.phone
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r13.addPerson(r0)
            goto L_0x0bc9
        L_0x0bc7:
            r4 = r44
        L_0x0bc9:
            int r0 = android.os.Build.VERSION.SDK_INT
            r11 = 26
            r7 = r28
            r3 = r37
            if (r0 < r11) goto L_0x0bd6
            r15.setNotificationChannel(r7, r13, r3)
        L_0x0bd6:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            r6 = r57
            r1 = r0
            int r16 = r52.intValue()
            r21 = r3
            r18 = 0
            r3 = r16
            r16 = r38
            r71 = r53
            r19 = 0
            r28 = 1
            r2 = r92
            r72 = r16
            r73 = r48
            r34 = 27
            r16 = r4
            r4 = r5
            r17 = r5
            r74 = r36
            r75 = r54
            r5 = r12
            r76 = r8
            r35 = r18
            r9 = r6
            r6 = r16
            r16 = r7
            r8 = r22
            r7 = r31
            r78 = r8
            r79 = r10
            r10 = r32
            r80 = r50
            r8 = r13
            r81 = r9
            r13 = r10
            r84 = r27
            r83 = r30
            r82 = r58
            r22 = 0
            r27 = r17
            r9 = r95
            r85 = r52
            r17 = 26
            r11 = r97
            r87 = r12
            r86 = r16
            r12 = r98
            r32 = r13
            r24 = 7
            r30 = 26
            r13 = r99
            r88 = r33
            r14 = r100
            r15 = r101
            r16 = r102
            r17 = r103
            r18 = r104
            r19 = r105
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r14 = r78
            r14.add(r0)
            r15 = r92
            android.util.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r1 = r76
            r3 = r85
            r0.put(r1, r3)
            if (r27 == 0) goto L_0x0cd8
            r1 = r82
            if (r1 == 0) goto L_0x0cd8
            java.lang.String r0 = "reply"
            r2 = r80
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0cd8 }
            java.lang.String r0 = "name"
            r2 = r87
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0cd8 }
            r2 = r71
            r3 = r75
            r1.put(r3, r2)     // Catch:{ JSONException -> 0x0cd8 }
            java.lang.String r0 = "max_date"
            r2 = r79
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0cd8 }
            int r0 = java.lang.Math.abs(r27)     // Catch:{ JSONException -> 0x0cd8 }
            r13 = r84
            r1.put(r13, r0)     // Catch:{ JSONException -> 0x0cd8 }
            r2 = r73
            if (r2 == 0) goto L_0x0cad
            java.lang.String r0 = "photo"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0cd8 }
            r3.<init>()     // Catch:{ JSONException -> 0x0cd8 }
            int r4 = r2.dc_id     // Catch:{ JSONException -> 0x0cd8 }
            r3.append(r4)     // Catch:{ JSONException -> 0x0cd8 }
            r4 = r88
            r3.append(r4)     // Catch:{ JSONException -> 0x0cd8 }
            long r5 = r2.volume_id     // Catch:{ JSONException -> 0x0cd8 }
            r3.append(r5)     // Catch:{ JSONException -> 0x0cd8 }
            r3.append(r4)     // Catch:{ JSONException -> 0x0cd8 }
            long r4 = r2.secret     // Catch:{ JSONException -> 0x0cd8 }
            r3.append(r4)     // Catch:{ JSONException -> 0x0cd8 }
            java.lang.String r2 = r3.toString()     // Catch:{ JSONException -> 0x0cd8 }
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0cd8 }
        L_0x0cad:
            r2 = r83
            if (r2 == 0) goto L_0x0cb6
            java.lang.String r0 = "msgs"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0cd8 }
        L_0x0cb6:
            java.lang.String r0 = "type"
            if (r27 <= 0) goto L_0x0cc3
            java.lang.String r2 = "user"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0cd8 }
        L_0x0cc0:
            r12 = r74
            goto L_0x0cd4
        L_0x0cc3:
            if (r40 != 0) goto L_0x0cce
            if (r39 == 0) goto L_0x0cc8
            goto L_0x0cce
        L_0x0cc8:
            java.lang.String r2 = "group"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0cd8 }
            goto L_0x0cc0
        L_0x0cce:
            java.lang.String r2 = "channel"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0cd8 }
            goto L_0x0cc0
        L_0x0cd4:
            r12.put(r1)     // Catch:{ JSONException -> 0x0cda }
            goto L_0x0cda
        L_0x0cd8:
            r12 = r74
        L_0x0cda:
            int r2 = r25 + 1
            r5 = r12
            r7 = r14
            r3 = r21
            r6 = r23
            r9 = r26
            r11 = r29
            r8 = r32
            r4 = r72
            r1 = r81
            r12 = r86
            r13 = 7
            r14 = r93
            goto L_0x00fa
        L_0x0cf3:
            r81 = r1
            r21 = r3
            r72 = r4
            r14 = r7
            r32 = r8
            r13 = r10
            r86 = r12
            r22 = 0
            r12 = r5
            if (r21 == 0) goto L_0x0d50
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0d1e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r15.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0d1e:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager     // Catch:{ SecurityException -> 0x0d2c }
            int r1 = r15.notificationId     // Catch:{ SecurityException -> 0x0d2c }
            r2 = r86
            r0.notify(r1, r2)     // Catch:{ SecurityException -> 0x0d2c }
            r89 = r12
            r90 = r13
            goto L_0x0d63
        L_0x0d2c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = r92
            r2 = r93
            r3 = r95
            r5 = r97
            r6 = r98
            r7 = r99
            r8 = r100
            r9 = r101
            r10 = r102
            r11 = r103
            r89 = r12
            r12 = r104
            r90 = r13
            r13 = r105
            r1.resetNotificationSound(r2, r3, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x0d63
        L_0x0d50:
            r89 = r12
            r90 = r13
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0d63
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.cancel(r1)
        L_0x0d63:
            r10 = 0
        L_0x0d64:
            int r0 = r32.size()
            if (r10 >= r0) goto L_0x0da9
            r1 = r32
            long r2 = r1.keyAt(r10)
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            boolean r0 = r0.contains(r2)
            if (r0 == 0) goto L_0x0d7d
            goto L_0x0da4
        L_0x0d7d:
            java.lang.Object r0 = r1.valueAt(r10)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0d9b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "cancel notification id "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0d9b:
            androidx.core.app.NotificationManagerCompat r2 = notificationManager
            int r0 = r0.intValue()
            r2.cancel(r0)
        L_0x0da4:
            int r10 = r10 + 1
            r32 = r1
            goto L_0x0d64
        L_0x0da9:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r14.size()
            r0.<init>(r1)
            int r1 = r14.size()
            r10 = 0
        L_0x0db7:
            if (r10 >= r1) goto L_0x0e10
            java.lang.Object r2 = r14.get(r10)
            org.telegram.messenger.NotificationsController$1NotificationHolder r2 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r2
            r0.clear()
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 29
            if (r3 < r4) goto L_0x0df5
            int r3 = r2.lowerId
            if (r3 == 0) goto L_0x0df5
            androidx.core.app.NotificationCompat$Builder r4 = r2.notification
            java.lang.String r5 = r2.name
            org.telegram.tgnet.TLRPC$User r6 = r2.user
            org.telegram.tgnet.TLRPC$Chat r7 = r2.chat
            long r8 = (long) r3
            r11 = r72
            java.lang.Object r8 = r11.get(r8)
            androidx.core.app.Person r8 = (androidx.core.app.Person) r8
            r93 = r92
            r94 = r4
            r95 = r3
            r96 = r5
            r97 = r6
            r98 = r7
            r99 = r8
            java.lang.String r3 = r93.createNotificationShortcut(r94, r95, r96, r97, r98, r99)
            if (r3 == 0) goto L_0x0df7
            r0.add(r3)
            goto L_0x0df7
        L_0x0df5:
            r11 = r72
        L_0x0df7:
            r2.call()
            boolean r2 = r92.unsupportedNotificationShortcut()
            if (r2 != 0) goto L_0x0e0b
            boolean r2 = r0.isEmpty()
            if (r2 != 0) goto L_0x0e0b
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r2, r0)
        L_0x0e0b:
            int r10 = r10 + 1
            r72 = r11
            goto L_0x0db7
        L_0x0e10:
            r6 = r89
            if (r6 == 0) goto L_0x0e34
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0e34 }
            r0.<init>()     // Catch:{ Exception -> 0x0e34 }
            r1 = r81
            r2 = r90
            r0.put(r2, r1)     // Catch:{ Exception -> 0x0e34 }
            java.lang.String r1 = "n"
            r0.put(r1, r6)     // Catch:{ Exception -> 0x0e34 }
            java.lang.String r1 = "/notify"
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0e34 }
            byte[] r0 = r0.getBytes()     // Catch:{ Exception -> 0x0e34 }
            java.lang.String r2 = "remote_notifications"
            org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r1, r0, r2)     // Catch:{ Exception -> 0x0e34 }
        L_0x0e34:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, java.lang.String, long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):void");
    }

    @TargetApi(28)
    private void loadRoundAvatar(File file, Person.Builder builder) {
        if (file != null) {
            try {
                builder.setIcon(IconCompat.createWithBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(file), $$Lambda$NotificationsController$VHgcplzGmBaKtRgRg0kOeCk4Zpw.INSTANCE)));
            } catch (Throwable unused) {
            }
        }
    }

    static /* synthetic */ int lambda$null$35(Canvas canvas) {
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
                    NotificationsController.this.lambda$playOutChatSound$38$NotificationsController();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$playOutChatSound$38 */
    public /* synthetic */ void lambda$playOutChatSound$38$NotificationsController() {
        try {
            if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundOutPlay) > 100) {
                this.lastSoundOutPlay = SystemClock.elapsedRealtime();
                if (this.soundPool == null) {
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener($$Lambda$NotificationsController$frKlJPvntAi9DDB6EvA4ez9UpOE.INSTANCE);
                }
                if (this.soundOut == 0 && !this.soundOutLoaded) {
                    this.soundOutLoaded = true;
                    this.soundOut = this.soundPool.load(ApplicationLoader.applicationContext, NUM, 1);
                }
                int i = this.soundOut;
                if (i != 0) {
                    try {
                        this.soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    static /* synthetic */ void lambda$null$37(SoundPool soundPool2, int i, int i2) {
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
            getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, $$Lambda$NotificationsController$tumZgdvmtv5i8BmWn6rMoy1x7I.INSTANCE);
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
        getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, $$Lambda$NotificationsController$GZBqT62OExZmrg2wPwCTtwFd8f8.INSTANCE);
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
        deleteNotificationChannelGlobal(i);
    }
}
