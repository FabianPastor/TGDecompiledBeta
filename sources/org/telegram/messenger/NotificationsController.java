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

    /* JADX WARNING: Code restructure failed: missing block: B:128:0x01e1, code lost:
        if (r8.getBoolean("EnablePreviewAll", true) == false) goto L_0x01e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x01ef, code lost:
        if (r8.getBoolean("EnablePreviewGroup", r9) != false) goto L_0x01fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x01f9, code lost:
        if (r8.getBoolean("EnablePreviewChannel", r9) != false) goto L_0x01fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x01fb, code lost:
        r3 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x020b, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0ddb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x020d, code lost:
        r20[0] = null;
        r4 = r3.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x0213, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached) == false) goto L_0x021c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x021b, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x021e, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L_0x0dcc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0222, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L_0x0226;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0228, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L_0x0239;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0238, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x023c, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L_0x029b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x023e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", NUM, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(((long) r0.messageOwner.date) * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(((long) r0.messageOwner.date) * 1000));
        r0 = r0.messageOwner.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x029a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", NUM, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x029d, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L_0x0dc5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x02a1, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L_0x02a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x02a7, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L_0x02c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x02ab, code lost:
        if (r4.video == false) goto L_0x02b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x02b6, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageVideoIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x02c0, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x02c3, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x03ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x02c5, code lost:
        r1 = r4.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x02c7, code lost:
        if (r1 != 0) goto L_0x02e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02d0, code lost:
        if (r4.users.size() != 1) goto L_0x02e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x02d2, code lost:
        r1 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02e2, code lost:
        if (r1 == 0) goto L_0x0373;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02ea, code lost:
        if (r0.messageOwner.peer_id.channel_id == 0) goto L_0x0304;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x02ee, code lost:
        if (r6.megagroup != false) goto L_0x0304;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x0303, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x0306, code lost:
        if (r1 != r10) goto L_0x031a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0319, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x031a, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x0326, code lost:
        if (r0 != null) goto L_0x0329;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x0328, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x032b, code lost:
        if (r2 != r0.id) goto L_0x0359;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x032f, code lost:
        if (r6.megagroup == false) goto L_0x0345;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0344, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0358, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x0372, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r11, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x0373, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0383, code lost:
        if (r2 >= r0.messageOwner.action.users.size()) goto L_0x03b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0385, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0399, code lost:
        if (r3 == null) goto L_0x03ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x039b, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x03a3, code lost:
        if (r1.length() == 0) goto L_0x03aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x03a5, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x03aa, code lost:
        r1.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x03ad, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x03c9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r11, r6.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x03cd, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L_0x03e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x03e1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x03e5, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L_0x049d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x03e7, code lost:
        r1 = r4.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03e9, code lost:
        if (r1 != 0) goto L_0x0403;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03f1, code lost:
        if (r4.users.size() != 1) goto L_0x0403;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x03f3, code lost:
        r1 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0403, code lost:
        if (r1 == 0) goto L_0x0445;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x0405, code lost:
        if (r1 != r10) goto L_0x041b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x041a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x041b, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x0427, code lost:
        if (r0 != null) goto L_0x042a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x0429, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0444, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r11, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0445, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x0455, code lost:
        if (r2 >= r0.messageOwner.action.users.size()) goto L_0x0482;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x0457, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x046b, code lost:
        if (r3 == null) goto L_0x047f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x046d, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x0475, code lost:
        if (r1.length() == 0) goto L_0x047c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x0477, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x047c, code lost:
        r1.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x047f, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x049c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r11, r6.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x04a0, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x04b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x04b4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x04b8, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x04cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x04cb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r11, r4.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x04ce, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0d65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x04d2, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x04d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04d8, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x0539;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x04da, code lost:
        r1 = r4.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04dc, code lost:
        if (r1 != r10) goto L_0x04f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04f1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04f4, code lost:
        if (r1 != r2) goto L_0x0508;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0507, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0508, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x051a, code lost:
        if (r0 != null) goto L_0x051e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x051c, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x0538, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r11, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x053b, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x0544;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0543, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0546, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x054f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x054e, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0551, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x0564;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0563, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0567, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x0579;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0578, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r4.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x057b, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x0584;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0583, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0586, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0d63;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x058c, code lost:
        if (r6 == null) goto L_0x086b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x0592, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r6) == false) goto L_0x0598;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x0596, code lost:
        if (r6.megagroup == false) goto L_0x086b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0598, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x059a, code lost:
        if (r0 != null) goto L_0x05b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x05af, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x05b6, code lost:
        if (r0.isMusic() == false) goto L_0x05ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x05c9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x05d3, code lost:
        if (r0.isVideo() == false) goto L_0x061c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x05d7, code lost:
        if (r1 < 19) goto L_0x0608;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x05e1, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0608;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x0607, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "📹 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x061b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x0620, code lost:
        if (r0.isGif() == false) goto L_0x0669;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x0624, code lost:
        if (r1 < 19) goto L_0x0655;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x062e, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0655;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x0654, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "🎬 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x0668, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x066f, code lost:
        if (r0.isVoice() == false) goto L_0x0683;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x0682, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x0687, code lost:
        if (r0.isRoundVideo() == false) goto L_0x069b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x069a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x069f, code lost:
        if (r0.isSticker() != false) goto L_0x083b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x06a5, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x06a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x06a9, code lost:
        r4 = r0.messageOwner;
        r5 = r4.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x06af, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x06f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x06b3, code lost:
        if (r1 < 19) goto L_0x06e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x06bb, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x06e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x06e1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "📎 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x06f5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x06f8, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0827;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x06fc, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0700;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x0702, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0718;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0717, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x071b, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x073d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x071d, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x073c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r11, r6.title, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x073f, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x077b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0741, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0747, code lost:
        if (r0.quiz == false) goto L_0x0762;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x0761, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r11, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x077a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r11, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x077d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x07c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x0781, code lost:
        if (r1 < 19) goto L_0x07b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x0789, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x07b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x07af, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "🖼 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x07c3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x07c8, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x07dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x07db, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x07dc, code lost:
        r1 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x07de, code lost:
        if (r1 == null) goto L_0x0813;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x07e4, code lost:
        if (r1.length() <= 0) goto L_0x0813;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x07e6, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x07ec, code lost:
        if (r0.length() <= 20) goto L_0x0801;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x07ee, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x0812, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, r0, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0826, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x083a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x083b, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0840, code lost:
        if (r0 == null) goto L_0x0858;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0857, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r11, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x086a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x086c, code lost:
        if (r6 == null) goto L_0x0aff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x086e, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0870, code lost:
        if (r0 != null) goto L_0x0882;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0881, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0886, code lost:
        if (r0.isMusic() == false) goto L_0x0898;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x0897, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x08a1, code lost:
        if (r0.isVideo() == false) goto L_0x08e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x08a5, code lost:
        if (r1 < 19) goto L_0x08d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x08af, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x08d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x08d2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x08e3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x08e8, code lost:
        if (r0.isGif() == false) goto L_0x092b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x08ec, code lost:
        if (r1 < 19) goto L_0x091a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x08f6, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x091a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x0919, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x092a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0930, code lost:
        if (r0.isVoice() == false) goto L_0x0942;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0941, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0946, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0958;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x0957, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x095c, code lost:
        if (r0.isSticker() != false) goto L_0x0ad4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x0962, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0966;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x0966, code lost:
        r4 = r0.messageOwner;
        r5 = r4.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x096c, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x09ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x0970, code lost:
        if (r1 < 19) goto L_0x099c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x0978, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x099c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x099b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x09ac, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x09af, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0ac3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x09b3, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x09b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x09b9, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x09cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x09cb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x09ce, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x09ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x09d0, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x09ed, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r6.title, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x09f0, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0a26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x09f2, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x09f8, code lost:
        if (r0.quiz == false) goto L_0x0a10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0a0f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0a25, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0a28, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0a69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0a2c, code lost:
        if (r1 < 19) goto L_0x0a58;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0a34, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0a58;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0a57, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0a68, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0a6c, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0a7e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0a7d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x0a7e, code lost:
        r1 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0a80, code lost:
        if (r1 == null) goto L_0x0ab2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0a86, code lost:
        if (r1.length() <= 0) goto L_0x0ab2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0a88, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0a8e, code lost:
        if (r0.length() <= 20) goto L_0x0aa3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x0a90, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0ab1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x0ac2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0ad3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x0ad4, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x0ad8, code lost:
        if (r0 == null) goto L_0x0aee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x0aed, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x0afe, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0aff, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x0b01, code lost:
        if (r0 != null) goto L_0x0b11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0b10, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x0b15, code lost:
        if (r0.isMusic() == false) goto L_0x0b25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0b24, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x0b2e, code lost:
        if (r0.isVideo() == false) goto L_0x0b6d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0b32, code lost:
        if (r1 < 19) goto L_0x0b5e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0b3c, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0b5e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0b5d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0b6c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0b71, code lost:
        if (r0.isGif() == false) goto L_0x0bb0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0b75, code lost:
        if (r1 < 19) goto L_0x0ba1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0b7f, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0ba1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0ba0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0baf, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0bb5, code lost:
        if (r0.isVoice() == false) goto L_0x0bc5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0bc4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0bc9, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0bd9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0bd8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0bdd, code lost:
        if (r0.isSticker() != false) goto L_0x0d3d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x0be3, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0be7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0be7, code lost:
        r4 = r0.messageOwner;
        r5 = r4.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x0bed, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0c2a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x0bf1, code lost:
        if (r1 < 19) goto L_0x0c1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0bf9, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0c1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x0c1a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x0c2c, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0d2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x0CLASSNAME, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x0CLASSNAME, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x0CLASSNAME, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0c4b, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r11, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x0CLASSNAME, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0c9b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0c6b, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0CLASSNAME, code lost:
        if (r0.quiz == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r11, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x0c9a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r11, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x0c9d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0cda;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0ca1, code lost:
        if (r1 < 19) goto L_0x0ccb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0ca9, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0ccb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x0cca, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0cd9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x0cdd, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0ced;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0cec, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0ced, code lost:
        r1 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0cef, code lost:
        if (r1 == null) goto L_0x0d1f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0cf5, code lost:
        if (r1.length() <= 0) goto L_0x0d1f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0cf7, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0cfd, code lost:
        if (r0.length() <= 20) goto L_0x0d12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0cff, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0d1e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0d2d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0d3c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0d3d, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0d42, code lost:
        if (r0 == null) goto L_0x0d55;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0d54, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r11, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0d62, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0d63, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x0d69, code lost:
        if (r3.peer_id.channel_id == 0) goto L_0x0d97;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0d6d, code lost:
        if (r6.megagroup != false) goto L_0x0d97;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0d73, code lost:
        if (r19.isVideoAvatar() == false) goto L_0x0d86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0d85, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0d96, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0d9b, code lost:
        if (r19.isVideoAvatar() == false) goto L_0x0db1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0db0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0dc4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x0dcb, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x0dda, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0ddf, code lost:
        if (r19.isMediaEmpty() == false) goto L_0x0df8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0de9, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0df0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x0def, code lost:
        return r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0df7, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0df8, code lost:
        r2 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0dfe, code lost:
        if ((r2.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0e3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0e02, code lost:
        if (r1 < 19) goto L_0x0e20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0e0a, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x0e20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0e1f, code lost:
        return "🖼 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0e26, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0e32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0e31, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0e3b, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0e40, code lost:
        if (r19.isVideo() == false) goto L_0x0e80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0e44, code lost:
        if (r1 < 19) goto L_0x0e64;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0e4e, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0e64;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0e63, code lost:
        return "📹 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0e6a, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0e76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0e75, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0e7f, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0e84, code lost:
        if (r19.isGame() == false) goto L_0x0e90;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0e8f, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0e94, code lost:
        if (r19.isVoice() == false) goto L_0x0ea0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0e9f, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0ea4, code lost:
        if (r19.isRoundVideo() == false) goto L_0x0eb0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0eaf, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0eb4, code lost:
        if (r19.isMusic() == false) goto L_0x0ec0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0ebf, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0ec0, code lost:
        r2 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0ec6, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0ed2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0ed1, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0ed4, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0ef2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0edc, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2).poll.quiz == false) goto L_0x0ee8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0ee7, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0ef1, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0ef4, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0fbe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0ef8, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0efc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0efe, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0f0a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0f0c, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0fa7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0var_, code lost:
        if (r19.isSticker() != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0var_, code lost:
        if (r19.isAnimatedSticker() == false) goto L_0x0f1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0f1f, code lost:
        if (r19.isGif() == false) goto L_0x0f4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0var_, code lost:
        if (r1 < 19) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0f2d, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0var_, code lost:
        return "🎬 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0f4c, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0f4f, code lost:
        if (r1 < 19) goto L_0x0f6f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0f6f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0f6e, code lost:
        return "📎 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0var_, code lost:
        r0 = r19.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0f7d, code lost:
        if (r0 == null) goto L_0x0f9d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0f9c, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0fa6, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0fad, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x0fb6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0fb5, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0fbd, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0fc7, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0192  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0196 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0197  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r19, java.lang.String[] r20, boolean[] r21) {
        /*
            r18 = this;
            r0 = r19
            int r1 = android.os.Build.VERSION.SDK_INT
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r2 != 0) goto L_0x0fd4
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r2 == 0) goto L_0x0010
            goto L_0x0fd4
        L_0x0010:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            long r3 = r2.dialog_id
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            int r5 = r2.chat_id
            if (r5 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            int r5 = r2.channel_id
        L_0x001d:
            int r2 = r2.user_id
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
            r11 = 2131626002(0x7f0e0812, float:1.8879228E38)
            java.lang.String r12 = "Message"
            r13 = 27
            r14 = 2
            if (r10 == 0) goto L_0x00de
            if (r5 != 0) goto L_0x006d
            if (r2 == 0) goto L_0x006d
            if (r1 <= r13) goto L_0x005a
            java.lang.String r1 = r0.localName
            r20[r7] = r1
        L_0x005a:
            if (r9 == 0) goto L_0x0064
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 != 0) goto L_0x00d9
        L_0x0064:
            if (r21 == 0) goto L_0x0068
            r21[r7] = r7
        L_0x0068:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r11)
            return r0
        L_0x006d:
            if (r5 == 0) goto L_0x00d9
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            int r2 = r2.channel_id
            if (r2 == 0) goto L_0x0085
            boolean r2 = r19.isSupergroup()
            if (r2 == 0) goto L_0x007e
            goto L_0x0085
        L_0x007e:
            if (r1 <= r13) goto L_0x0089
            java.lang.String r1 = r0.localName
            r20[r7] = r1
            goto L_0x0089
        L_0x0085:
            java.lang.String r1 = r0.localUserName
            r20[r7] = r1
        L_0x0089:
            if (r9 == 0) goto L_0x00a3
            boolean r1 = r0.localChannel
            if (r1 != 0) goto L_0x0097
            java.lang.String r1 = "EnablePreviewGroup"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 == 0) goto L_0x00a3
        L_0x0097:
            boolean r1 = r0.localChannel
            if (r1 == 0) goto L_0x00d9
            java.lang.String r1 = "EnablePreviewChannel"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 != 0) goto L_0x00d9
        L_0x00a3:
            if (r21 == 0) goto L_0x00a7
            r21[r7] = r7
        L_0x00a7:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00c5
            boolean r1 = r19.isSupergroup()
            if (r1 != 0) goto L_0x00c5
            r1 = 2131624683(0x7f0e02eb, float:1.8876553E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            java.lang.String r0 = r0.localName
            r2[r7] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00c5:
            r1 = 2131626305(0x7f0e0941, float:1.8879842E38)
            java.lang.Object[] r2 = new java.lang.Object[r14]
            java.lang.String r3 = r0.localUserName
            r2[r7] = r3
            java.lang.String r0 = r0.localName
            r2[r6] = r0
            java.lang.String r0 = "NotificationMessageGroupNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00d9:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x00de:
            org.telegram.messenger.UserConfig r10 = r18.getUserConfig()
            int r10 = r10.getClientUserId()
            if (r2 != 0) goto L_0x00f0
            int r2 = r19.getFromChatId()
            if (r2 != 0) goto L_0x00f6
            int r2 = -r5
            goto L_0x00f6
        L_0x00f0:
            if (r2 != r10) goto L_0x00f6
            int r2 = r19.getFromChatId()
        L_0x00f6:
            r15 = 0
            int r17 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r17 != 0) goto L_0x0104
            if (r5 == 0) goto L_0x0101
            int r3 = -r5
            long r3 = (long) r3
            goto L_0x0104
        L_0x0101:
            if (r2 == 0) goto L_0x0104
            long r3 = (long) r2
        L_0x0104:
            boolean r15 = org.telegram.messenger.UserObject.isReplyUser((long) r3)
            if (r15 == 0) goto L_0x0118
            org.telegram.tgnet.TLRPC$Message r15 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r15 = r15.fwd_from
            if (r15 == 0) goto L_0x0118
            org.telegram.tgnet.TLRPC$Peer r15 = r15.from_id
            if (r15 == 0) goto L_0x0118
            int r2 = org.telegram.messenger.MessageObject.getPeerId(r15)
        L_0x0118:
            r15 = 0
            if (r2 <= 0) goto L_0x013a
            org.telegram.messenger.MessagesController r11 = r18.getMessagesController()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r11 = r11.getUser(r14)
            if (r11 == 0) goto L_0x014e
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r11)
            if (r5 == 0) goto L_0x0132
            r20[r7] = r11
            goto L_0x014f
        L_0x0132:
            if (r1 <= r13) goto L_0x0137
            r20[r7] = r11
            goto L_0x014f
        L_0x0137:
            r20[r7] = r15
            goto L_0x014f
        L_0x013a:
            org.telegram.messenger.MessagesController r11 = r18.getMessagesController()
            int r14 = -r2
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r11 = r11.getChat(r14)
            if (r11 == 0) goto L_0x014e
            java.lang.String r11 = r11.title
            r20[r7] = r11
            goto L_0x014f
        L_0x014e:
            r11 = r15
        L_0x014f:
            if (r11 == 0) goto L_0x0194
            if (r2 <= 0) goto L_0x0194
            boolean r14 = org.telegram.messenger.UserObject.isReplyUser((long) r3)
            if (r14 == 0) goto L_0x0194
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            if (r14 == 0) goto L_0x0194
            org.telegram.tgnet.TLRPC$Peer r14 = r14.saved_from_peer
            if (r14 == 0) goto L_0x0194
            int r14 = org.telegram.messenger.MessageObject.getPeerId(r14)
            if (r14 >= 0) goto L_0x0194
            org.telegram.messenger.MessagesController r6 = r18.getMessagesController()
            int r14 = -r14
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r14)
            if (r6 == 0) goto L_0x0194
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r11)
            java.lang.String r11 = " @ "
            r14.append(r11)
            java.lang.String r6 = r6.title
            r14.append(r6)
            java.lang.String r11 = r14.toString()
            r6 = r20[r7]
            if (r6 == 0) goto L_0x0194
            r20[r7] = r11
        L_0x0194:
            if (r11 != 0) goto L_0x0197
            return r15
        L_0x0197:
            if (r5 == 0) goto L_0x01b7
            org.telegram.messenger.MessagesController r6 = r18.getMessagesController()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r14)
            if (r6 != 0) goto L_0x01a8
            return r15
        L_0x01a8:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r14 == 0) goto L_0x01b8
            boolean r14 = r6.megagroup
            if (r14 != 0) goto L_0x01b8
            if (r1 > r13) goto L_0x01b8
            r20[r7] = r15
            goto L_0x01b8
        L_0x01b7:
            r6 = r15
        L_0x01b8:
            int r4 = (int) r3
            if (r4 != 0) goto L_0x01c7
            r20[r7] = r15
            r0 = 2131626282(0x7f0e092a, float:1.8879796E38)
            java.lang.String r1 = "NotificationHiddenMessage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x01c7:
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r3 == 0) goto L_0x01d3
            boolean r3 = r6.megagroup
            if (r3 != 0) goto L_0x01d3
            r3 = 1
            goto L_0x01d4
        L_0x01d3:
            r3 = 0
        L_0x01d4:
            if (r9 == 0) goto L_0x0fc8
            if (r5 != 0) goto L_0x01e4
            if (r2 == 0) goto L_0x01e4
            java.lang.String r4 = "EnablePreviewAll"
            r9 = 1
            boolean r4 = r8.getBoolean(r4, r9)
            if (r4 != 0) goto L_0x01fb
            goto L_0x01e5
        L_0x01e4:
            r9 = 1
        L_0x01e5:
            if (r5 == 0) goto L_0x0fc8
            if (r3 != 0) goto L_0x01f1
            java.lang.String r4 = "EnablePreviewGroup"
            boolean r4 = r8.getBoolean(r4, r9)
            if (r4 != 0) goto L_0x01fb
        L_0x01f1:
            if (r3 == 0) goto L_0x0fc8
            java.lang.String r3 = "EnablePreviewChannel"
            boolean r3 = r8.getBoolean(r3, r9)
            if (r3 == 0) goto L_0x0fc8
        L_0x01fb:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r5 = "🎬 "
            java.lang.String r8 = "📎 "
            java.lang.String r9 = "📹 "
            java.lang.String r13 = "🖼 "
            if (r4 == 0) goto L_0x0ddb
            r20[r7] = r15
            org.telegram.tgnet.TLRPC$MessageAction r4 = r3.action
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r12 == 0) goto L_0x021c
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x021c:
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r12 != 0) goto L_0x0dcc
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r12 == 0) goto L_0x0226
            goto L_0x0dcc
        L_0x0226:
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r12 == 0) goto L_0x0239
            r0 = 2131626263(0x7f0e0917, float:1.8879757E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0239:
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            r14 = 3
            if (r12 == 0) goto L_0x029b
            r1 = 2131628028(0x7f0e0ffc, float:1.8883337E38)
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
            r2 = 2131626332(0x7f0e095c, float:1.8879897E38)
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
        L_0x029b:
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r12 != 0) goto L_0x0dc5
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r12 == 0) goto L_0x02a5
            goto L_0x0dc5
        L_0x02a5:
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r12 == 0) goto L_0x02c1
            boolean r0 = r4.video
            if (r0 == 0) goto L_0x02b7
            r0 = 2131624579(0x7f0e0283, float:1.8876342E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02b7:
            r0 = 2131624573(0x7f0e027d, float:1.887633E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02c1:
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r12 == 0) goto L_0x03ca
            int r1 = r4.user_id
            if (r1 != 0) goto L_0x02e2
            java.util.ArrayList<java.lang.Integer> r3 = r4.users
            int r3 = r3.size()
            r4 = 1
            if (r3 != r4) goto L_0x02e2
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Integer> r1 = r1.users
            java.lang.Object r1 = r1.get(r7)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
        L_0x02e2:
            if (r1 == 0) goto L_0x0373
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x0304
            boolean r0 = r6.megagroup
            if (r0 != 0) goto L_0x0304
            r0 = 2131624633(0x7f0e02b9, float:1.8876451E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0304:
            r3 = 2
            r4 = 1
            if (r1 != r10) goto L_0x031a
            r0 = 2131626284(0x7f0e092c, float:1.88798E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x031a:
            org.telegram.messenger.MessagesController r0 = r18.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x0329
            return r15
        L_0x0329:
            int r1 = r0.id
            if (r2 != r1) goto L_0x0359
            boolean r0 = r6.megagroup
            if (r0 == 0) goto L_0x0345
            r0 = 2131626269(0x7f0e091d, float:1.887977E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0345:
            r1 = 2
            r3 = 1
            r0 = 2131626268(0x7f0e091c, float:1.8879767E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0359:
            r3 = 1
            r1 = 2131626267(0x7f0e091b, float:1.8879765E38)
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
        L_0x0373:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0379:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x03b0
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x03ad
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x03aa
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x03aa:
            r1.append(r3)
        L_0x03ad:
            int r2 = r2 + 1
            goto L_0x0379
        L_0x03b0:
            r0 = 2131626267(0x7f0e091b, float:1.8879765E38)
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
        L_0x03ca:
            r12 = 2
            boolean r14 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r14 == 0) goto L_0x03e2
            r0 = 2131626271(0x7f0e091f, float:1.8879773E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r12 = 1
            r1[r12] = r2
            java.lang.String r2 = "NotificationGroupCreatedCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03e2:
            r12 = 1
            boolean r14 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r14 == 0) goto L_0x049d
            int r1 = r4.user_id
            if (r1 != 0) goto L_0x0403
            java.util.ArrayList<java.lang.Integer> r2 = r4.users
            int r2 = r2.size()
            if (r2 != r12) goto L_0x0403
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Integer> r1 = r1.users
            java.lang.Object r1 = r1.get(r7)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
        L_0x0403:
            if (r1 == 0) goto L_0x0445
            if (r1 != r10) goto L_0x041b
            r0 = 2131626276(0x7f0e0924, float:1.8879784E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x041b:
            org.telegram.messenger.MessagesController r0 = r18.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x042a
            return r15
        L_0x042a:
            r1 = 2131626275(0x7f0e0923, float:1.8879782E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
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
        L_0x0445:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x044b:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0482
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x047f
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x047c
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x047c:
            r1.append(r3)
        L_0x047f:
            int r2 = r2 + 1
            goto L_0x044b
        L_0x0482:
            r0 = 2131626275(0x7f0e0923, float:1.8879782E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
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
        L_0x049d:
            r12 = 2
            boolean r14 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r14 == 0) goto L_0x04b5
            r0 = 2131626285(0x7f0e092d, float:1.8879802E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r14 = 1
            r1[r14] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04b5:
            r14 = 1
            boolean r15 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r15 == 0) goto L_0x04cc
            r0 = 2131626264(0x7f0e0918, float:1.887976E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r7] = r11
            java.lang.String r2 = r4.title
            r1[r14] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04cc:
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r12 != 0) goto L_0x0d65
            boolean r12 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r12 == 0) goto L_0x04d6
            goto L_0x0d65
        L_0x04d6:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r3 == 0) goto L_0x0539
            int r1 = r4.user_id
            if (r1 != r10) goto L_0x04f2
            r0 = 2131626278(0x7f0e0926, float:1.8879788E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04f2:
            r3 = 2
            r4 = 1
            if (r1 != r2) goto L_0x0508
            r0 = 2131626279(0x7f0e0927, float:1.887979E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0508:
            org.telegram.messenger.MessagesController r1 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x051e
            r1 = 0
            return r1
        L_0x051e:
            r1 = 2131626277(0x7f0e0925, float:1.8879786E38)
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
        L_0x0539:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r2 == 0) goto L_0x0544
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0544:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r2 == 0) goto L_0x054f
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x054f:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r2 == 0) goto L_0x0564
            r0 = 2131624126(0x7f0e00be, float:1.8875423E38)
            r2 = 1
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0564:
            r2 = 1
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r3 == 0) goto L_0x0579
            r0 = 2131624126(0x7f0e00be, float:1.8875423E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0579:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r2 == 0) goto L_0x0584
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0584:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r2 == 0) goto L_0x0d63
            java.lang.String r2 = "..."
            r3 = 20
            if (r6 == 0) goto L_0x086b
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r4 == 0) goto L_0x0598
            boolean r4 = r6.megagroup
            if (r4 == 0) goto L_0x086b
        L_0x0598:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x05b0
            r0 = 2131626232(0x7f0e08f8, float:1.8879694E38)
            r4 = 2
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r10 = 1
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05b0:
            r4 = 2
            r10 = 1
            boolean r12 = r0.isMusic()
            if (r12 == 0) goto L_0x05ca
            r0 = 2131626229(0x7f0e08f5, float:1.8879688E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05ca:
            boolean r4 = r0.isVideo()
            r10 = 2131626253(0x7f0e090d, float:1.8879737E38)
            java.lang.String r12 = "NotificationActionPinnedText"
            if (r4 == 0) goto L_0x061c
            r4 = 19
            if (r1 < r4) goto L_0x0608
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0608
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
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = r6.title
            r3 = 2
            r1[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r10, r1)
            return r0
        L_0x0608:
            r2 = 1
            r3 = 2
            r0 = 2131626256(0x7f0e0910, float:1.8879743E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r3 = r6.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x061c:
            boolean r4 = r0.isGif()
            if (r4 == 0) goto L_0x0669
            r4 = 19
            if (r1 < r4) goto L_0x0655
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0655
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
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = r6.title
            r5 = 2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r10, r1)
            return r0
        L_0x0655:
            r4 = 1
            r5 = 2
            r0 = 2131626223(0x7f0e08ef, float:1.8879676E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0669:
            r4 = 1
            r5 = 2
            boolean r9 = r0.isVoice()
            if (r9 == 0) goto L_0x0683
            r0 = 2131626259(0x7f0e0913, float:1.887975E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0683:
            boolean r9 = r0.isRoundVideo()
            if (r9 == 0) goto L_0x069b
            r0 = 2131626244(0x7f0e0904, float:1.8879719E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x069b:
            boolean r4 = r0.isSticker()
            if (r4 != 0) goto L_0x083b
            boolean r4 = r0.isAnimatedSticker()
            if (r4 == 0) goto L_0x06a9
            goto L_0x083b
        L_0x06a9:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r4.media
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x06f6
            r9 = 19
            if (r1 < r9) goto L_0x06e2
            java.lang.String r1 = r4.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x06e2
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
        L_0x06e2:
            r2 = 1
            r3 = 2
            r0 = 2131626208(0x7f0e08e0, float:1.8879646E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r3 = r6.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06f6:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x0827
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x0700
            goto L_0x0827
        L_0x0700:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x0718
            r0 = 2131626219(0x7f0e08eb, float:1.8879668E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r8 = 1
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0718:
            r8 = 1
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r9 == 0) goto L_0x073d
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            r0 = 2131626205(0x7f0e08dd, float:1.887964E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r8] = r2
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x073d:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x077b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0762
            r1 = 2131626241(0x7f0e0901, float:1.8879713E38)
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
        L_0x0762:
            r2 = 3
            r3 = 2
            r4 = 1
            r1 = 2131626238(0x7f0e08fe, float:1.8879707E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r5 = r6.title
            r2[r4] = r5
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x077b:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x07c4
            r8 = 19
            if (r1 < r8) goto L_0x07b0
            java.lang.String r1 = r4.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x07b0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = r6.title
            r8 = 2
            r1[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r10, r1)
            return r0
        L_0x07b0:
            r4 = 1
            r8 = 2
            r0 = 2131626235(0x7f0e08fb, float:1.88797E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07c4:
            r4 = 1
            r8 = 2
            boolean r1 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x07dc
            r0 = 2131626211(0x7f0e08e3, float:1.8879652E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07dc:
            java.lang.CharSequence r1 = r0.messageText
            if (r1 == 0) goto L_0x0813
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0813
            java.lang.CharSequence r0 = r0.messageText
            int r1 = r0.length()
            if (r1 <= r3) goto L_0x0801
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r3)
            r1.append(r0)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
        L_0x0801:
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = r6.title
            r2 = 2
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r10, r1)
            return r0
        L_0x0813:
            r2 = 2
            r4 = 1
            r0 = 2131626232(0x7f0e08f8, float:1.8879694E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0827:
            r2 = 2
            r4 = 1
            r0 = 2131626217(0x7f0e08e9, float:1.8879664E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x083b:
            r4 = 1
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0858
            r1 = 2131626249(0x7f0e0909, float:1.8879729E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r3 = r6.title
            r2[r4] = r3
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0858:
            r3 = 2
            r0 = 2131626247(0x7f0e0907, float:1.8879725E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x086b:
            r4 = 1
            if (r6 == 0) goto L_0x0aff
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0882
            r0 = 2131626233(0x7f0e08f9, float:1.8879696E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0882:
            boolean r10 = r0.isMusic()
            if (r10 == 0) goto L_0x0898
            r0 = 2131626230(0x7f0e08f6, float:1.887969E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0898:
            boolean r4 = r0.isVideo()
            r10 = 2131626254(0x7f0e090e, float:1.887974E38)
            java.lang.String r11 = "NotificationActionPinnedTextChannel"
            if (r4 == 0) goto L_0x08e4
            r4 = 19
            if (r1 < r4) goto L_0x08d3
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x08d3
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
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x08d3:
            r2 = 1
            r0 = 2131626257(0x7f0e0911, float:1.8879745E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08e4:
            boolean r4 = r0.isGif()
            if (r4 == 0) goto L_0x092b
            r4 = 19
            if (r1 < r4) goto L_0x091a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x091a
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
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x091a:
            r4 = 1
            r0 = 2131626224(0x7f0e08f0, float:1.8879678E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x092b:
            r4 = 1
            boolean r5 = r0.isVoice()
            if (r5 == 0) goto L_0x0942
            r0 = 2131626260(0x7f0e0914, float:1.8879751E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0942:
            boolean r5 = r0.isRoundVideo()
            if (r5 == 0) goto L_0x0958
            r0 = 2131626245(0x7f0e0905, float:1.887972E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0958:
            boolean r4 = r0.isSticker()
            if (r4 != 0) goto L_0x0ad4
            boolean r4 = r0.isAnimatedSticker()
            if (r4 == 0) goto L_0x0966
            goto L_0x0ad4
        L_0x0966:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r4.media
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x09ad
            r9 = 19
            if (r1 < r9) goto L_0x099c
            java.lang.String r1 = r4.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x099c
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
        L_0x099c:
            r2 = 1
            r0 = 2131626209(0x7f0e08e1, float:1.8879648E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09ad:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x0ac3
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x09b7
            goto L_0x0ac3
        L_0x09b7:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x09cc
            r0 = 2131626220(0x7f0e08ec, float:1.887967E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09cc:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x09ee
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            r0 = 2131626206(0x7f0e08de, float:1.8879642E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09ee:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x0a26
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0a10
            r1 = 2131626242(0x7f0e0902, float:1.8879715E38)
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
        L_0x0a10:
            r2 = 2
            r3 = 1
            r1 = 2131626239(0x7f0e08ff, float:1.8879709E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r6.title
            r2[r7] = r4
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0a26:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x0a69
            r8 = 19
            if (r1 < r8) goto L_0x0a58
            java.lang.String r1 = r4.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0a58
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x0a58:
            r4 = 1
            r0 = 2131626236(0x7f0e08fc, float:1.8879702E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a69:
            r4 = 1
            boolean r1 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0a7e
            r0 = 2131626212(0x7f0e08e4, float:1.8879654E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a7e:
            java.lang.CharSequence r1 = r0.messageText
            if (r1 == 0) goto L_0x0ab2
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0ab2
            java.lang.CharSequence r0 = r0.messageText
            int r1 = r0.length()
            if (r1 <= r3) goto L_0x0aa3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r3)
            r1.append(r0)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
        L_0x0aa3:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x0ab2:
            r2 = 1
            r0 = 2131626233(0x7f0e08f9, float:1.8879696E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ac3:
            r2 = 1
            r0 = 2131626218(0x7f0e08ea, float:1.8879666E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ad4:
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0aee
            r1 = 2131626250(0x7f0e090a, float:1.887973E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r6.title
            r2[r7] = r3
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0aee:
            r4 = 1
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0aff:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0b11
            r0 = 2131626234(0x7f0e08fa, float:1.8879698E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b11:
            boolean r6 = r0.isMusic()
            if (r6 == 0) goto L_0x0b25
            r0 = 2131626231(0x7f0e08f7, float:1.8879692E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedMusicUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b25:
            boolean r4 = r0.isVideo()
            r6 = 2131626255(0x7f0e090f, float:1.8879741E38)
            java.lang.String r10 = "NotificationActionPinnedTextUser"
            if (r4 == 0) goto L_0x0b6d
            r4 = 19
            if (r1 < r4) goto L_0x0b5e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b5e
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
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r6, r1)
            return r0
        L_0x0b5e:
            r2 = 1
            r0 = 2131626258(0x7f0e0912, float:1.8879747E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedVideoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b6d:
            boolean r4 = r0.isGif()
            if (r4 == 0) goto L_0x0bb0
            r4 = 19
            if (r1 < r4) goto L_0x0ba1
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ba1
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
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r6, r1)
            return r0
        L_0x0ba1:
            r4 = 1
            r0 = 2131626225(0x7f0e08f1, float:1.887968E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGifUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bb0:
            r4 = 1
            boolean r5 = r0.isVoice()
            if (r5 == 0) goto L_0x0bc5
            r0 = 2131626261(0x7f0e0915, float:1.8879753E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bc5:
            boolean r5 = r0.isRoundVideo()
            if (r5 == 0) goto L_0x0bd9
            r0 = 2131626246(0x7f0e0906, float:1.8879723E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedRoundUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bd9:
            boolean r4 = r0.isSticker()
            if (r4 != 0) goto L_0x0d3d
            boolean r4 = r0.isAnimatedSticker()
            if (r4 == 0) goto L_0x0be7
            goto L_0x0d3d
        L_0x0be7:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r4.media
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x0c2a
            r9 = 19
            if (r1 < r9) goto L_0x0c1b
            java.lang.String r1 = r4.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0c1b
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
        L_0x0c1b:
            r2 = 1
            r0 = 2131626210(0x7f0e08e2, float:1.887965E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedFileUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0c2a:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x0d2e
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x0CLASSNAME
            goto L_0x0d2e
        L_0x0CLASSNAME:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x0CLASSNAME
            r0 = 2131626221(0x7f0e08ed, float:1.8879672E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGeoLiveUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0CLASSNAME:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            r0 = 2131626207(0x7f0e08df, float:1.8879644E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContactUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0CLASSNAME:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x0c9b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0CLASSNAME
            r1 = 2131626243(0x7f0e0903, float:1.8879717E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r0 = r0.question
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0CLASSNAME:
            r2 = 2
            r3 = 1
            r1 = 2131626240(0x7f0e0900, float:1.887971E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0c9b:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x0cda
            r8 = 19
            if (r1 < r8) goto L_0x0ccb
            java.lang.String r1 = r4.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ccb
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r6, r1)
            return r0
        L_0x0ccb:
            r4 = 1
            r0 = 2131626237(0x7f0e08fd, float:1.8879705E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0cda:
            r4 = 1
            boolean r1 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0ced
            r0 = 2131626216(0x7f0e08e8, float:1.8879662E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGameUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ced:
            java.lang.CharSequence r1 = r0.messageText
            if (r1 == 0) goto L_0x0d1f
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0d1f
            java.lang.CharSequence r0 = r0.messageText
            int r1 = r0.length()
            if (r1 <= r3) goto L_0x0d12
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r3)
            r1.append(r0)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
        L_0x0d12:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r6, r1)
            return r0
        L_0x0d1f:
            r2 = 1
            r0 = 2131626234(0x7f0e08fa, float:1.8879698E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d2e:
            r2 = 1
            r0 = 2131626222(0x7f0e08ee, float:1.8879674E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGeoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d3d:
            r2 = 1
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0d55
            r1 = 2131626251(0x7f0e090b, float:1.8879733E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r7] = r11
            r3[r2] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x0d55:
            r0 = 2131626252(0x7f0e090c, float:1.8879735E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedStickerUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d63:
            r0 = 0
            return r0
        L_0x0d65:
            org.telegram.tgnet.TLRPC$Peer r1 = r3.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0d97
            boolean r1 = r6.megagroup
            if (r1 != 0) goto L_0x0d97
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x0d86
            r0 = 2131624733(0x7f0e031d, float:1.8876654E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d86:
            r1 = 1
            r0 = 2131624699(0x7f0e02fb, float:1.8876585E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d97:
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x0db1
            r0 = 2131626266(0x7f0e091a, float:1.8879763E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0db1:
            r1 = 2
            r3 = 1
            r0 = 2131626265(0x7f0e0919, float:1.8879761E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0dc5:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0dcc:
            r3 = 1
            r0 = 2131626262(0x7f0e0916, float:1.8879755E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ddb:
            boolean r2 = r19.isMediaEmpty()
            if (r2 == 0) goto L_0x0df8
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0df0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x0df0:
            r0 = 2131626002(0x7f0e0812, float:1.8879228E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0df8:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0e3c
            r3 = 19
            if (r1 < r3) goto L_0x0e20
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e20
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0e20:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0e32
            r0 = 2131624359(0x7f0e01a7, float:1.8875895E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e32:
            r0 = 2131624376(0x7f0e01b8, float:1.887593E38)
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e3c:
            boolean r2 = r19.isVideo()
            if (r2 == 0) goto L_0x0e80
            r2 = 19
            if (r1 < r2) goto L_0x0e64
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e64
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0e64:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0e76
            r0 = 2131624360(0x7f0e01a8, float:1.8875898E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e76:
            r0 = 2131624382(0x7f0e01be, float:1.8875942E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e80:
            boolean r2 = r19.isGame()
            if (r2 == 0) goto L_0x0e90
            r0 = 2131624362(0x7f0e01aa, float:1.8875902E38)
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e90:
            boolean r2 = r19.isVoice()
            if (r2 == 0) goto L_0x0ea0
            r0 = 2131624356(0x7f0e01a4, float:1.887589E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ea0:
            boolean r2 = r19.isRoundVideo()
            if (r2 == 0) goto L_0x0eb0
            r0 = 2131624378(0x7f0e01ba, float:1.8875934E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0eb0:
            boolean r2 = r19.isMusic()
            if (r2 == 0) goto L_0x0ec0
            r0 = 2131624375(0x7f0e01b7, float:1.8875928E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ec0:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0ed2
            r0 = 2131624358(0x7f0e01a6, float:1.8875893E38)
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ed2:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0ef2
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r0 = r2.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0ee8
            r0 = 2131626938(0x7f0e0bba, float:1.8881126E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ee8:
            r0 = 2131626829(0x7f0e0b4d, float:1.8880905E38)
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ef2:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0fbe
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x0efc
            goto L_0x0fbe
        L_0x0efc:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0f0a
            r0 = 2131624368(0x7f0e01b0, float:1.8875914E38)
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0f0a:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x0fa7
            boolean r2 = r19.isSticker()
            if (r2 != 0) goto L_0x0var_
            boolean r2 = r19.isAnimatedSticker()
            if (r2 == 0) goto L_0x0f1b
            goto L_0x0var_
        L_0x0f1b:
            boolean r2 = r19.isGif()
            if (r2 == 0) goto L_0x0f4d
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
            r0 = 2131624363(0x7f0e01ab, float:1.8875904E38)
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0f4d:
            r2 = 19
            if (r1 < r2) goto L_0x0f6f
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0f6f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0f6f:
            r0 = 2131624361(0x7f0e01a9, float:1.88759E38)
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            java.lang.String r0 = r19.getStickerEmoji()
            if (r0 == 0) goto L_0x0f9d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " "
            r1.append(r0)
            r0 = 2131624379(0x7f0e01bb, float:1.8875936E38)
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0f9d:
            r0 = 2131624379(0x7f0e01bb, float:1.8875936E38)
            java.lang.String r1 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fa7:
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0fb6
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0fb6:
            r0 = 2131626002(0x7f0e0812, float:1.8879228E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0fbe:
            r0 = 2131624372(0x7f0e01b4, float:1.8875922E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fc8:
            if (r21 == 0) goto L_0x0fcc
            r21[r7] = r7
        L_0x0fcc:
            r0 = 2131626002(0x7f0e0812, float:1.8879228E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0fd4:
            r0 = 2131626282(0x7f0e092a, float:1.8879796E38)
            java.lang.String r1 = "NotificationHiddenMessage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:224:0x057e, code lost:
        if (r8.getBoolean("EnablePreviewGroup", true) == false) goto L_0x0582;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x058a, code lost:
        if (r8.getBoolean("EnablePreviewChannel", r12) != false) goto L_0x058c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x058c, code lost:
        r5 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0590, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0ed8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x0592, code lost:
        r8 = r5.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x0596, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x06ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x0598, code lost:
        r1 = r8.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x059a, code lost:
        if (r1 != 0) goto L_0x05b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x05a3, code lost:
        if (r8.users.size() != 1) goto L_0x05b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x05a5, code lost:
        r1 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x05b6, code lost:
        if (r1 == 0) goto L_0x0653;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x05be, code lost:
        if (r0.messageOwner.peer_id.channel_id == 0) goto L_0x05da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x05c2, code lost:
        if (r7.megagroup != false) goto L_0x05da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x05c4, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x05dd, code lost:
        if (r1 != r10) goto L_0x05f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x05df, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x05f2, code lost:
        r1 = getMessagesController().getUser(java.lang.Integer.valueOf(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x05fe, code lost:
        if (r1 != null) goto L_0x0602;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x0600, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x0604, code lost:
        if (r2 != r1.id) goto L_0x0636;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x0608, code lost:
        if (r7.megagroup == false) goto L_0x0620;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x060a, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x0620, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x0636, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r6, r7.title, org.telegram.messenger.UserObject.getUserName(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x0653, code lost:
        r2 = new java.lang.StringBuilder();
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x0663, code lost:
        if (r12 >= r0.messageOwner.action.users.size()) goto L_0x0690;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0665, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x0679, code lost:
        if (r3 == null) goto L_0x068d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x067b, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0683, code lost:
        if (r2.length() == 0) goto L_0x068a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0685, code lost:
        r2.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x068a, code lost:
        r2.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x068d, code lost:
        r12 = r12 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x0690, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r6, r7.title, r2.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x06b0, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L_0x06c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x06ca, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L_0x078b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x06cc, code lost:
        r1 = r8.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x06ce, code lost:
        if (r1 != 0) goto L_0x06ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x06d6, code lost:
        if (r8.users.size() != 1) goto L_0x06ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x06d8, code lost:
        r2 = 0;
        r1 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x06ea, code lost:
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x06eb, code lost:
        if (r1 == 0) goto L_0x0731;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x06ed, code lost:
        if (r1 != r10) goto L_0x0704;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x06ef, code lost:
        r1 = new java.lang.Object[2];
        r1[r2] = r6;
        r1[1] = r7.title;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0704, code lost:
        r1 = getMessagesController().getUser(java.lang.Integer.valueOf(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x0710, code lost:
        if (r1 != null) goto L_0x0714;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0712, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x0714, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r6, r7.title, org.telegram.messenger.UserObject.getUserName(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x0731, code lost:
        r2 = new java.lang.StringBuilder();
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0741, code lost:
        if (r12 >= r0.messageOwner.action.users.size()) goto L_0x076e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0743, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0757, code lost:
        if (r3 == null) goto L_0x076b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x0759, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0761, code lost:
        if (r2.length() == 0) goto L_0x0768;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0763, code lost:
        r2.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x0768, code lost:
        r2.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x076b, code lost:
        r12 = r12 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x076e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r6, r7.title, r2.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x078e, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x07a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x07a9, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x07be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x07c0, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0e71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x07c4, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x07c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x07ca, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x0831;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x07cc, code lost:
        r1 = r8.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x07ce, code lost:
        if (r1 != r10) goto L_0x07e6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x07e9, code lost:
        if (r1 != r2) goto L_0x07fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x07fe, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x0810, code lost:
        if (r0 != null) goto L_0x0814;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x0812, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x0831, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x0834, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x083e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x0840, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x084a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x084c, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x0861;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x0865, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x0878;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x087a, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x0884;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0886, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0e65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x088c, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r7) == false) goto L_0x0b5d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x0890, code lost:
        if (r7.megagroup == false) goto L_0x0894;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x0894, code lost:
        r2 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0896, code lost:
        if (r2 != null) goto L_0x08ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x08b1, code lost:
        if (r2.isMusic() == false) goto L_0x08c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x08b3, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x08cd, code lost:
        if (r2.isVideo() == false) goto L_0x0914;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x08d1, code lost:
        if (r1 < 19) goto L_0x0901;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x08db, code lost:
        if (android.text.TextUtils.isEmpty(r2.messageOwner.message) != false) goto L_0x0901;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x08dd, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r7.title, r13 + r2.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x0901, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0918, code lost:
        if (r2.isGif() == false) goto L_0x095f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x091c, code lost:
        if (r1 < 19) goto L_0x094c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x0926, code lost:
        if (android.text.TextUtils.isEmpty(r2.messageOwner.message) != false) goto L_0x094c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x0928, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r7.title, "🎬 " + r2.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x094c, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x0965, code lost:
        if (r2.isVoice() == false) goto L_0x0978;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x0967, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x097c, code lost:
        if (r2.isRoundVideo() == false) goto L_0x098f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x097e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x0993, code lost:
        if (r2.isSticker() != false) goto L_0x0b2f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x0999, code lost:
        if (r2.isAnimatedSticker() == false) goto L_0x099d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x099d, code lost:
        r5 = r2.messageOwner;
        r6 = r5.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x09a3, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x09e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x09a7, code lost:
        if (r1 < 19) goto L_0x09d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x09af, code lost:
        if (android.text.TextUtils.isEmpty(r5.message) != false) goto L_0x09d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x09b1, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r7.title, "📎 " + r2.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x09d5, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x09ea, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0b1c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x09ee, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x09f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x09f4, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0a09;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x09f6, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x0a0b, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0a31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x0a0d, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0.messageOwner.media;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r7.title, org.telegram.messenger.ContactsController.formatName(r0.first_name, r0.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0a33, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0a6d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0a35, code lost:
        r1 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r6).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x0a3b, code lost:
        if (r1.quiz == false) goto L_0x0a55;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0a3d, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r7.title, r1.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0a55, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r7.title, r1.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0a6f, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0ab4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0a73, code lost:
        if (r1 < 19) goto L_0x0aa1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0a7b, code lost:
        if (android.text.TextUtils.isEmpty(r5.message) != false) goto L_0x0aa1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0a7d, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r7.title, "🖼 " + r2.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0aa1, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0ab8, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0acb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0aba, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0acb, code lost:
        r0 = r2.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0acd, code lost:
        if (r0 == null) goto L_0x0b09;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x0ad3, code lost:
        if (r0.length() <= 0) goto L_0x0b09;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0ad5, code lost:
        r0 = r2.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0add, code lost:
        if (r0.length() <= 20) goto L_0x0af8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0adf, code lost:
        r1 = new java.lang.StringBuilder();
        r5 = 0;
        r1.append(r0.subSequence(0, 20));
        r1.append("...");
        r0 = r1.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0af8, code lost:
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0af9, code lost:
        r1 = new java.lang.Object[2];
        r1[r5] = r7.title;
        r1[1] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0b09, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x0b1c, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0b2f, code lost:
        r0 = r2.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x0b34, code lost:
        if (r0 == null) goto L_0x0b4b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x0b36, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r7.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x0b4b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0b5d, code lost:
        r2 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x0b60, code lost:
        if (r2 != null) goto L_0x0b77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x0b7d, code lost:
        if (r2.isMusic() == false) goto L_0x0b92;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x0b7f, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x0b9b, code lost:
        if (r2.isVideo() == false) goto L_0x0be8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0b9f, code lost:
        if (r1 < 19) goto L_0x0bd2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0ba9, code lost:
        if (android.text.TextUtils.isEmpty(r2.messageOwner.message) != false) goto L_0x0bd2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x0bab, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r6, r13 + r2.messageOwner.message, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0bd2, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x0bec, code lost:
        if (r2.isGif() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x0bf0, code lost:
        if (r1 < 19) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x0bfa, code lost:
        if (android.text.TextUtils.isEmpty(r2.messageOwner.message) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x0bfc, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r6, "🎬 " + r2.messageOwner.message, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x0CLASSNAME, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x0CLASSNAME, code lost:
        if (r2.isVoice() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x0CLASSNAME, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0CLASSNAME, code lost:
        if (r2.isRoundVideo() == false) goto L_0x0c6e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0c5b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0CLASSNAME, code lost:
        if (r2.isSticker() != false) goto L_0x0e32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x0CLASSNAME, code lost:
        if (r2.isAnimatedSticker() == false) goto L_0x0c7c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x0c7c, code lost:
        r5 = r2.messageOwner;
        r8 = r5.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0CLASSNAME, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0ccd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x0CLASSNAME, code lost:
        if (r1 < 19) goto L_0x0cb7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x0c8e, code lost:
        if (android.text.TextUtils.isEmpty(r5.message) != false) goto L_0x0cb7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x0CLASSNAME, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r6, "📎 " + r2.messageOwner.message, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0cb7, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0ccf, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0e1c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0cd3, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0cd7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0cd9, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0cf1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x0cdb, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0cf3, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0d1c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0cf5, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0.messageOwner.media;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r6, r7.title, org.telegram.messenger.ContactsController.formatName(r0.first_name, r0.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0d1e, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0d5e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0d20, code lost:
        r1 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r8).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0d26, code lost:
        if (r1.quiz == false) goto L_0x0d43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0d28, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r6, r7.title, r1.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0d43, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r6, r7.title, r1.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0d60, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0dab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0d64, code lost:
        if (r1 < 19) goto L_0x0d95;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0d6c, code lost:
        if (android.text.TextUtils.isEmpty(r5.message) != false) goto L_0x0d95;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x0d6e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r6, "🖼 " + r2.messageOwner.message, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0d95, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0db0, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0dc5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0db2, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0dc5, code lost:
        r1 = r2.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0dc7, code lost:
        if (r1 == null) goto L_0x0e06;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x0dcd, code lost:
        if (r1.length() <= 0) goto L_0x0e06;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0dcf, code lost:
        r1 = r2.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0dd7, code lost:
        if (r1.length() <= 20) goto L_0x0df2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x0dd9, code lost:
        r2 = new java.lang.StringBuilder();
        r0 = 0;
        r2.append(r1.subSequence(0, 20));
        r2.append("...");
        r1 = r2.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x0df2, code lost:
        r0 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0df3, code lost:
        r2 = new java.lang.Object[3];
        r2[r0] = r6;
        r2[1] = r1;
        r2[2] = r7.title;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0e06, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x0e1c, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x0e32, code lost:
        r1 = r2.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x0e38, code lost:
        if (r1 == null) goto L_0x0e51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x0e3a, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r6, r7.title, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0e51, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0e67, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) == false) goto L_0x14cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0e75, code lost:
        if (r5.peer_id.channel_id == 0) goto L_0x0ea7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x0e79, code lost:
        if (r7.megagroup != false) goto L_0x0ea7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0e7f, code lost:
        if (r21.isVideoAvatar() == false) goto L_0x0e94;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0eac, code lost:
        if (r21.isVideoAvatar() == false) goto L_0x0ec3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0edc, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r7) == false) goto L_0x1187;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0ee0, code lost:
        if (r7.megagroup != false) goto L_0x1187;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0ee6, code lost:
        if (r21.isMediaEmpty() == false) goto L_0x0f1d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0ee8, code lost:
        if (r22 != false) goto L_0x0f0c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0ef2, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0f0c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0ef4, code lost:
        r15 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r6, r0.messageOwner.message);
        r23[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0f1d, code lost:
        r5 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0var_, code lost:
        if ((r5.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0f6b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0var_, code lost:
        if (r22 != false) goto L_0x0f5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0var_, code lost:
        if (r1 < 19) goto L_0x0f5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r5.message) != false) goto L_0x0f5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0var_, code lost:
        r15 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r6, "🖼 " + r0.messageOwner.message);
        r23[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0f6f, code lost:
        if (r21.isVideo() == false) goto L_0x0fb9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0var_, code lost:
        if (r22 != false) goto L_0x0fa8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0var_, code lost:
        if (r1 < 19) goto L_0x0fa8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0f7f, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0fa8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x0var_, code lost:
        r15 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r6, r13 + r0.messageOwner.message);
        r23[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0fbf, code lost:
        if (r21.isVoice() == false) goto L_0x0fd0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x0fd4, code lost:
        if (r21.isRoundVideo() == false) goto L_0x0fe5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x0fe9, code lost:
        if (r21.isMusic() == false) goto L_0x0ffa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0ffa, code lost:
        r5 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x1000, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x101f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x1002, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x1021, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x1057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x1023, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x1029, code lost:
        if (r0.quiz == false) goto L_0x1041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x102b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r6, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x1041, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r6, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x1059, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x1176;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x105d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x1061;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x1063, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x1076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x1078, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x1146;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x107e, code lost:
        if (r21.isSticker() != false) goto L_0x111d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x1084, code lost:
        if (r21.isAnimatedSticker() == false) goto L_0x1088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x108c, code lost:
        if (r21.isGif() == false) goto L_0x10d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x108e, code lost:
        if (r22 != false) goto L_0x10c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x1092, code lost:
        if (r1 < 19) goto L_0x10c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x109c, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x10c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x109e, code lost:
        r15 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r6, "🎬 " + r0.messageOwner.message);
        r23[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x10d7, code lost:
        if (r22 != false) goto L_0x110d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x10db, code lost:
        if (r1 < 19) goto L_0x110d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x10e5, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x110d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x10e7, code lost:
        r15 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r6, "📎 " + r0.messageOwner.message);
        r23[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x111d, code lost:
        r0 = r21.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x1123, code lost:
        if (r0 == null) goto L_0x1137;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x1125, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r6, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x1137, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x1147, code lost:
        if (r22 != false) goto L_0x1166;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x114f, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x1166;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x1151, code lost:
        r15 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r6, r0.messageText);
        r23[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x1190, code lost:
        if (r21.isMediaEmpty() == false) goto L_0x11ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x1192, code lost:
        if (r22 != false) goto L_0x11b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x119c, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x11b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x11ca, code lost:
        r5 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x11d0, code lost:
        if ((r5.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x121d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x11d2, code lost:
        if (r22 != false) goto L_0x1207;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x11d6, code lost:
        if (r1 < 19) goto L_0x1207;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x11de, code lost:
        if (android.text.TextUtils.isEmpty(r5.message) != false) goto L_0x1207;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x1221, code lost:
        if (r21.isVideo() == false) goto L_0x1270;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x1223, code lost:
        if (r22 != false) goto L_0x125a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x1227, code lost:
        if (r1 < 19) goto L_0x125a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x1231, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x125a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x1277, code lost:
        if (r21.isVoice() == false) goto L_0x128c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x1290, code lost:
        if (r21.isRoundVideo() == false) goto L_0x12a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x12a9, code lost:
        if (r21.isMusic() == false) goto L_0x12be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x12be, code lost:
        r5 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x12c4, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x12e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x12c6, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x12eb, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x132b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x12ed, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x12f3, code lost:
        if (r0.quiz == false) goto L_0x1310;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x12f5, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r6, r7.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x1310, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r6, r7.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x132d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x134c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x134e, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x1487;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x1352, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x1356;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x1358, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x1370;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x1372, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x1455;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x1378, code lost:
        if (r21.isSticker() != false) goto L_0x1422;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x137e, code lost:
        if (r21.isAnimatedSticker() == false) goto L_0x1382;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x1386, code lost:
        if (r21.isGif() == false) goto L_0x13d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x1388, code lost:
        if (r22 != false) goto L_0x13bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x138c, code lost:
        if (r1 < 19) goto L_0x13bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x1396, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x13bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x13d5, code lost:
        if (r22 != false) goto L_0x140c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x13d9, code lost:
        if (r1 < 19) goto L_0x140c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x13e3, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x140c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x1422, code lost:
        r0 = r21.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x1428, code lost:
        if (r0 == null) goto L_0x1441;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x142a, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r6, r7.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x1441, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x1455, code lost:
        if (r22 != false) goto L_0x1474;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x145d, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x1474;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r6, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r6, r7.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:?, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:?, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:?, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:?, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", NUM, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:?, code lost:
        return r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:?, code lost:
        return r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:?, code lost:
        return r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageMusic", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r6, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:?, code lost:
        return r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:?, code lost:
        return r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:?, code lost:
        return r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r6, r7.title, r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r6, r7.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r6, r7.title, r13 + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:?, code lost:
        return org.telegram.messenger.LocaleController.formatString(" ", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMusic", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r6, r7.title, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r6, r7.title, r5.game.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r6, r7.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r6, r7.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r6, r7.title, r0.messageText);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r6, r7.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r6, r7.title);
     */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0134 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0135  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r21, boolean r22, boolean[] r23, boolean[] r24) {
        /*
            r20 = this;
            r0 = r21
            int r1 = android.os.Build.VERSION.SDK_INT
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r2 != 0) goto L_0x14cf
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r2 == 0) goto L_0x0010
            goto L_0x14cf
        L_0x0010:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            long r3 = r2.dialog_id
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            int r5 = r2.chat_id
            if (r5 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            int r5 = r2.channel_id
        L_0x001d:
            int r2 = r2.user_id
            r6 = 1
            r7 = 0
            if (r24 == 0) goto L_0x0025
            r24[r7] = r6
        L_0x0025:
            org.telegram.messenger.AccountInstance r8 = r20.getAccountInstance()
            android.content.SharedPreferences r8 = r8.getNotificationsSettings()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "content_preview_"
            r9.append(r10)
            r9.append(r3)
            java.lang.String r9 = r9.toString()
            boolean r9 = r8.getBoolean(r9, r6)
            boolean r10 = r21.isFcmMessage()
            java.lang.String r11 = "NotificationMessageGroupNoText"
            r12 = 2131626318(0x7f0e094e, float:1.8879869E38)
            java.lang.String r13 = "NotificationMessageNoText"
            r14 = 2
            if (r10 == 0) goto L_0x00c4
            if (r5 != 0) goto L_0x006d
            if (r2 == 0) goto L_0x006d
            if (r9 == 0) goto L_0x005e
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 != 0) goto L_0x00bd
        L_0x005e:
            if (r24 == 0) goto L_0x0062
            r24[r7] = r7
        L_0x0062:
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r0 = r0.localName
            r1[r7] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r13, r12, r1)
            return r0
        L_0x006d:
            if (r5 == 0) goto L_0x00bd
            if (r9 == 0) goto L_0x0089
            boolean r1 = r0.localChannel
            if (r1 != 0) goto L_0x007d
            java.lang.String r1 = "EnablePreviewGroup"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 == 0) goto L_0x0089
        L_0x007d:
            boolean r1 = r0.localChannel
            if (r1 == 0) goto L_0x00bd
            java.lang.String r1 = "EnablePreviewChannel"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 != 0) goto L_0x00bd
        L_0x0089:
            if (r24 == 0) goto L_0x008d
            r24[r7] = r7
        L_0x008d:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00ab
            boolean r1 = r21.isSupergroup()
            if (r1 != 0) goto L_0x00ab
            r1 = 2131624683(0x7f0e02eb, float:1.8876553E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            java.lang.String r0 = r0.localName
            r2[r7] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00ab:
            r1 = 2131626305(0x7f0e0941, float:1.8879842E38)
            java.lang.Object[] r2 = new java.lang.Object[r14]
            java.lang.String r3 = r0.localUserName
            r2[r7] = r3
            java.lang.String r0 = r0.localName
            r2[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r1, r2)
            return r0
        L_0x00bd:
            r23[r7] = r6
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = (java.lang.String) r0
            return r0
        L_0x00c4:
            org.telegram.messenger.UserConfig r10 = r20.getUserConfig()
            int r10 = r10.getClientUserId()
            if (r2 != 0) goto L_0x00d6
            int r2 = r21.getFromChatId()
            if (r2 != 0) goto L_0x00dc
            int r2 = -r5
            goto L_0x00dc
        L_0x00d6:
            if (r2 != r10) goto L_0x00dc
            int r2 = r21.getFromChatId()
        L_0x00dc:
            r15 = 0
            int r17 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r17 != 0) goto L_0x00ea
            if (r5 == 0) goto L_0x00e7
            int r3 = -r5
            long r3 = (long) r3
            goto L_0x00ea
        L_0x00e7:
            if (r2 == 0) goto L_0x00ea
            long r3 = (long) r2
        L_0x00ea:
            r15 = 0
            if (r2 <= 0) goto L_0x011f
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            boolean r12 = r12.from_scheduled
            if (r12 == 0) goto L_0x010c
            long r6 = (long) r10
            int r18 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r18 != 0) goto L_0x0102
            r6 = 2131626021(0x7f0e0825, float:1.8879266E38)
            java.lang.String r7 = "MessageScheduledReminderNotification"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x0132
        L_0x0102:
            r6 = 2131626326(0x7f0e0956, float:1.8879885E38)
            java.lang.String r7 = "NotificationMessageScheduledName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x0132
        L_0x010c:
            org.telegram.messenger.MessagesController r6 = r20.getMessagesController()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r6 == 0) goto L_0x0131
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r6)
            goto L_0x0132
        L_0x011f:
            org.telegram.messenger.MessagesController r6 = r20.getMessagesController()
            int r7 = -r2
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            if (r6 == 0) goto L_0x0131
            java.lang.String r6 = r6.title
            goto L_0x0132
        L_0x0131:
            r6 = r15
        L_0x0132:
            if (r6 != 0) goto L_0x0135
            return r15
        L_0x0135:
            if (r5 == 0) goto L_0x0146
            org.telegram.messenger.MessagesController r7 = r20.getMessagesController()
            java.lang.Integer r12 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r12)
            if (r7 != 0) goto L_0x0147
            return r15
        L_0x0146:
            r7 = r15
        L_0x0147:
            int r4 = (int) r3
            if (r4 != 0) goto L_0x0155
            r0 = 2131627960(0x7f0e0fb8, float:1.88832E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x14ce
        L_0x0155:
            java.lang.String r3 = "🎬 "
            java.lang.String r4 = "📎 "
            java.lang.String r12 = "📹 "
            java.lang.String r15 = "🖼 "
            java.lang.String r14 = "NotificationMessageText"
            r19 = r12
            r12 = 3
            if (r5 != 0) goto L_0x0562
            if (r2 == 0) goto L_0x0562
            if (r9 == 0) goto L_0x054f
            java.lang.String r2 = "EnablePreviewAll"
            r5 = 1
            boolean r2 = r8.getBoolean(r2, r5)
            if (r2 == 0) goto L_0x054f
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r5 == 0) goto L_0x024c
            org.telegram.tgnet.TLRPC$MessageAction r1 = r2.action
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r2 == 0) goto L_0x0189
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x14ce
        L_0x0189:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r2 != 0) goto L_0x023b
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r2 == 0) goto L_0x0193
            goto L_0x023b
        L_0x0193:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r2 == 0) goto L_0x01a8
            r0 = 2131626263(0x7f0e0917, float:1.8879757E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x01a8:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r3 == 0) goto L_0x020c
            r1 = 2131628028(0x7f0e0ffc, float:1.8883337E38)
            r3 = 2
            java.lang.Object[] r4 = new java.lang.Object[r3]
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r3 = r3.formatterYear
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            int r5 = r5.date
            long r5 = (long) r5
            r7 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 * r7
            java.lang.String r3 = r3.format((long) r5)
            r2 = 0
            r4[r2] = r3
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r3 = r3.formatterDay
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            int r5 = r5.date
            long r5 = (long) r5
            long r5 = r5 * r7
            java.lang.String r3 = r3.format((long) r5)
            r5 = 1
            r4[r5] = r3
            java.lang.String r3 = "formatDateAtTime"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r4)
            r3 = 2131626332(0x7f0e095c, float:1.8879897E38)
            r4 = 4
            java.lang.Object[] r4 = new java.lang.Object[r4]
            org.telegram.messenger.UserConfig r6 = r20.getUserConfig()
            org.telegram.tgnet.TLRPC$User r6 = r6.getCurrentUser()
            java.lang.String r6 = r6.first_name
            r2 = 0
            r4[r2] = r6
            r4[r5] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r1 = r0.title
            r2 = 2
            r4[r2] = r1
            java.lang.String r0 = r0.address
            r4[r12] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            goto L_0x14ce
        L_0x020c:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 != 0) goto L_0x0233
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r2 == 0) goto L_0x0215
            goto L_0x0233
        L_0x0215:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x14cc
            boolean r0 = r1.video
            if (r0 == 0) goto L_0x0228
            r0 = 2131624579(0x7f0e0283, float:1.8876342E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x14ce
        L_0x0228:
            r0 = 2131624573(0x7f0e027d, float:1.887633E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x14ce
        L_0x0233:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x14ce
        L_0x023b:
            r0 = 2131626262(0x7f0e0916, float:1.8879755E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x024c:
            r2 = r19
            boolean r7 = r21.isMediaEmpty()
            if (r7 == 0) goto L_0x0296
            if (r22 != 0) goto L_0x0287
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0278
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3 = 1
            r1[r3] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r3
            goto L_0x14ce
        L_0x0278:
            r2 = 0
            r3 = 1
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r6
            r1 = 2131626318(0x7f0e094e, float:1.8879869E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r13, r1, r0)
            goto L_0x14ce
        L_0x0287:
            r1 = 2131626318(0x7f0e094e, float:1.8879869E38)
            r2 = 0
            r3 = 1
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r6
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r13, r1, r0)
            goto L_0x14ce
        L_0x0296:
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r7.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x02fb
            if (r22 != 0) goto L_0x02d3
            r2 = 19
            if (r1 < r2) goto L_0x02d3
            java.lang.String r1 = r7.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x02d3
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r3 = 1
            r1[r3] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r3
            goto L_0x14ce
        L_0x02d3:
            r2 = 0
            r3 = 1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x02ec
            r0 = 2131626323(0x7f0e0953, float:1.8879879E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "NotificationMessageSDPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x02ec:
            r0 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "NotificationMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x02fb:
            boolean r7 = r21.isVideo()
            if (r7 == 0) goto L_0x0360
            if (r22 != 0) goto L_0x0338
            r3 = 19
            if (r1 < r3) goto L_0x0338
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0338
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r5 = 0
            r1[r5] = r6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r2 = 1
            r1[r2] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r5] = r2
            goto L_0x14ce
        L_0x0338:
            r2 = 1
            r5 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0351
            r0 = 2131626324(0x7f0e0954, float:1.887988E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r5] = r6
            java.lang.String r2 = "NotificationMessageSDVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0351:
            r0 = 2131626330(0x7f0e095a, float:1.8879893E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r5] = r6
            java.lang.String r2 = "NotificationMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0360:
            r5 = 0
            boolean r2 = r21.isGame()
            if (r2 == 0) goto L_0x0382
            r1 = 2131626292(0x7f0e0934, float:1.8879816E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r5] = r6
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            java.lang.String r0 = r0.title
            r5 = 1
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageGame"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x14ce
        L_0x0382:
            r5 = 1
            boolean r7 = r21.isVoice()
            if (r7 == 0) goto L_0x0399
            r0 = 2131626287(0x7f0e092f, float:1.8879806E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = "NotificationMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0399:
            r2 = 0
            boolean r7 = r21.isRoundVideo()
            if (r7 == 0) goto L_0x03af
            r0 = 2131626322(0x7f0e0952, float:1.8879877E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r6
            java.lang.String r2 = "NotificationMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x03af:
            boolean r7 = r21.isMusic()
            if (r7 == 0) goto L_0x03c4
            r0 = 2131626317(0x7f0e094d, float:1.8879867E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r6
            java.lang.String r2 = "NotificationMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x03c4:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x03e9
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            r0 = 2131626288(0x7f0e0930, float:1.8879808E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r6
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x03e9:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r7 == 0) goto L_0x0421
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x040a
            r1 = 2131626321(0x7f0e0951, float:1.8879875E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r2 = 0
            r3[r2] = r6
            java.lang.String r0 = r0.question
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x041e
        L_0x040a:
            r2 = 0
            r3 = 2
            r4 = 1
            r1 = 2131626320(0x7f0e0950, float:1.8879873E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r6
            java.lang.String r0 = r0.question
            r3[r4] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
        L_0x041e:
            r15 = r0
            goto L_0x14ce
        L_0x0421:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r7 != 0) goto L_0x053e
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r7 == 0) goto L_0x042b
            goto L_0x053e
        L_0x042b:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r7 == 0) goto L_0x0440
            r0 = 2131626315(0x7f0e094b, float:1.8879863E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = "NotificationMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0440:
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x0510
            boolean r5 = r21.isSticker()
            if (r5 != 0) goto L_0x04e7
            boolean r5 = r21.isAnimatedSticker()
            if (r5 == 0) goto L_0x0452
            goto L_0x04e7
        L_0x0452:
            boolean r5 = r21.isGif()
            if (r5 == 0) goto L_0x04a0
            if (r22 != 0) goto L_0x048f
            r4 = 19
            if (r1 < r4) goto L_0x048f
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x048f
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r3 = 1
            r1[r3] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r3
            goto L_0x14ce
        L_0x048f:
            r2 = 0
            r3 = 1
            r0 = 2131626294(0x7f0e0936, float:1.887982E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "NotificationMessageGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x04a0:
            r2 = 0
            if (r22 != 0) goto L_0x04d7
            r3 = 19
            if (r1 < r3) goto L_0x04d7
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x04d7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r3 = 1
            r1[r3] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r3
            goto L_0x14ce
        L_0x04d7:
            r3 = 1
            r0 = 2131626289(0x7f0e0931, float:1.887981E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "NotificationMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x04e7:
            r2 = 0
            r3 = 1
            java.lang.String r0 = r21.getStickerEmoji()
            if (r0 == 0) goto L_0x0501
            r1 = 2131626328(0x7f0e0958, float:1.887989E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r2] = r6
            r4[r3] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r4)
            goto L_0x041e
        L_0x0501:
            r0 = 2131626327(0x7f0e0957, float:1.8879887E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0510:
            r2 = 0
            if (r22 != 0) goto L_0x0530
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0530
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r6
            java.lang.CharSequence r0 = r0.messageText
            r3 = 1
            r1[r3] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r3
            goto L_0x14ce
        L_0x0530:
            r3 = 1
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r6
            r1 = 2131626318(0x7f0e094e, float:1.8879869E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r13, r1, r0)
            goto L_0x14ce
        L_0x053e:
            r2 = 0
            r3 = 1
            r0 = 2131626316(0x7f0e094c, float:1.8879865E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "NotificationMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x054f:
            r2 = 0
            r3 = 1
            if (r24 == 0) goto L_0x0555
            r24[r2] = r2
        L_0x0555:
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r6
            r1 = 2131626318(0x7f0e094e, float:1.8879869E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r13, r1, r0)
            goto L_0x14ce
        L_0x0562:
            r13 = r19
            if (r5 == 0) goto L_0x14cc
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r5 == 0) goto L_0x0572
            boolean r5 = r7.megagroup
            if (r5 != 0) goto L_0x0572
            r5 = 1
            goto L_0x0573
        L_0x0572:
            r5 = 0
        L_0x0573:
            if (r9 == 0) goto L_0x149c
            if (r5 != 0) goto L_0x0581
            java.lang.String r9 = "EnablePreviewGroup"
            r12 = 1
            boolean r9 = r8.getBoolean(r9, r12)
            if (r9 != 0) goto L_0x058c
            goto L_0x0582
        L_0x0581:
            r12 = 1
        L_0x0582:
            if (r5 == 0) goto L_0x149c
            java.lang.String r5 = "EnablePreviewChannel"
            boolean r5 = r8.getBoolean(r5, r12)
            if (r5 == 0) goto L_0x149c
        L_0x058c:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r8 == 0) goto L_0x0ed8
            org.telegram.tgnet.TLRPC$MessageAction r8 = r5.action
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r9 == 0) goto L_0x06ad
            int r1 = r8.user_id
            if (r1 != 0) goto L_0x05b6
            java.util.ArrayList<java.lang.Integer> r3 = r8.users
            int r3 = r3.size()
            r4 = 1
            if (r3 != r4) goto L_0x05b6
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Integer> r1 = r1.users
            r3 = 0
            java.lang.Object r1 = r1.get(r3)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
        L_0x05b6:
            if (r1 == 0) goto L_0x0653
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x05da
            boolean r0 = r7.megagroup
            if (r0 != 0) goto L_0x05da
            r0 = 2131624633(0x7f0e02b9, float:1.8876451E38)
            r4 = 2
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r3 = 0
            r1[r3] = r6
            java.lang.String r2 = r7.title
            r5 = 1
            r1[r5] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x05da:
            r3 = 0
            r4 = 2
            r5 = 1
            if (r1 != r10) goto L_0x05f2
            r0 = 2131626284(0x7f0e092c, float:1.88798E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r6
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x05f2:
            org.telegram.messenger.MessagesController r3 = r20.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r3.getUser(r1)
            if (r1 != 0) goto L_0x0602
            r3 = 0
            return r3
        L_0x0602:
            int r3 = r1.id
            if (r2 != r3) goto L_0x0636
            boolean r1 = r7.megagroup
            if (r1 == 0) goto L_0x0620
            r1 = 2131626269(0x7f0e091d, float:1.887977E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r0 = 0
            r2[r0] = r6
            java.lang.String r0 = r7.title
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x041e
        L_0x0620:
            r0 = 0
            r2 = 2
            r3 = 1
            r1 = 2131626268(0x7f0e091c, float:1.8879767E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r0] = r6
            java.lang.String r0 = r7.title
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x041e
        L_0x0636:
            r0 = 0
            r3 = 1
            r2 = 2131626267(0x7f0e091b, float:1.8879765E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r0] = r6
            java.lang.String r0 = r7.title
            r4[r3] = r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r1)
            r1 = 2
            r4[r1] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            goto L_0x041e
        L_0x0653:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r12 = 0
        L_0x0659:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r12 >= r3) goto L_0x0690
            org.telegram.messenger.MessagesController r3 = r20.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r12)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x068d
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r2.length()
            if (r4 == 0) goto L_0x068a
            java.lang.String r4 = ", "
            r2.append(r4)
        L_0x068a:
            r2.append(r3)
        L_0x068d:
            int r12 = r12 + 1
            goto L_0x0659
        L_0x0690:
            r0 = 2131626267(0x7f0e091b, float:1.8879765E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r1 = 0
            r3[r1] = r6
            java.lang.String r1 = r7.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r1 = r2.toString()
            r9 = 2
            r3[r9] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x041e
        L_0x06ad:
            r9 = 2
            boolean r12 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r12 == 0) goto L_0x06c7
            r0 = 2131626271(0x7f0e091f, float:1.8879773E38)
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r9 = 1
            r1[r9] = r2
            java.lang.String r2 = "NotificationGroupCreatedCall"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x06c7:
            r9 = 1
            boolean r12 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r12 == 0) goto L_0x078b
            int r1 = r8.user_id
            if (r1 != 0) goto L_0x06ea
            java.util.ArrayList<java.lang.Integer> r2 = r8.users
            int r2 = r2.size()
            if (r2 != r9) goto L_0x06ea
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Integer> r1 = r1.users
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            goto L_0x06eb
        L_0x06ea:
            r2 = 0
        L_0x06eb:
            if (r1 == 0) goto L_0x0731
            if (r1 != r10) goto L_0x0704
            r0 = 2131626276(0x7f0e0924, float:1.8879784E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0704:
            org.telegram.messenger.MessagesController r2 = r20.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r2.getUser(r1)
            if (r1 != 0) goto L_0x0714
            r2 = 0
            return r2
        L_0x0714:
            r2 = 2131626275(0x7f0e0923, float:1.8879782E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r0 = 0
            r3[r0] = r6
            java.lang.String r0 = r7.title
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r1)
            r1 = 2
            r3[r1] = r0
            java.lang.String r0 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x041e
        L_0x0731:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r12 = 0
        L_0x0737:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r12 >= r3) goto L_0x076e
            org.telegram.messenger.MessagesController r3 = r20.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r12)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x076b
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r2.length()
            if (r4 == 0) goto L_0x0768
            java.lang.String r4 = ", "
            r2.append(r4)
        L_0x0768:
            r2.append(r3)
        L_0x076b:
            int r12 = r12 + 1
            goto L_0x0737
        L_0x076e:
            r0 = 2131626275(0x7f0e0923, float:1.8879782E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r1 = 0
            r3[r1] = r6
            java.lang.String r1 = r7.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r1 = r2.toString()
            r9 = 2
            r3[r9] = r1
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x041e
        L_0x078b:
            r9 = 2
            boolean r12 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r12 == 0) goto L_0x07a5
            r0 = 2131626285(0x7f0e092d, float:1.8879802E38)
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r11 = 0
            r1[r11] = r6
            java.lang.String r2 = r7.title
            r12 = 1
            r1[r12] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x07a5:
            r11 = 0
            r12 = 1
            boolean r14 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r14 == 0) goto L_0x07be
            r0 = 2131626264(0x7f0e0918, float:1.887976E38)
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r1[r11] = r6
            java.lang.String r2 = r8.title
            r1[r12] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x07be:
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r11 != 0) goto L_0x0e71
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r11 == 0) goto L_0x07c8
            goto L_0x0e71
        L_0x07c8:
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r5 == 0) goto L_0x0831
            int r1 = r8.user_id
            if (r1 != r10) goto L_0x07e6
            r0 = 2131626278(0x7f0e0926, float:1.8879788E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r6
            java.lang.String r2 = r7.title
            r5 = 1
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x07e6:
            r3 = 2
            r4 = 0
            r5 = 1
            if (r1 != r2) goto L_0x07fe
            r0 = 2131626279(0x7f0e0927, float:1.887979E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r6
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x07fe:
            org.telegram.messenger.MessagesController r2 = r20.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            if (r0 != 0) goto L_0x0814
            r2 = 0
            return r2
        L_0x0814:
            r2 = 2131626277(0x7f0e0925, float:1.8879786E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r1 = 0
            r3[r1] = r6
            java.lang.String r1 = r7.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r3[r1] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x14ce
        L_0x0831:
            r2 = 0
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r9 == 0) goto L_0x083e
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x14ce
        L_0x083e:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r9 == 0) goto L_0x084a
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x14ce
        L_0x084a:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r9 == 0) goto L_0x0861
            r0 = 2131624126(0x7f0e00be, float:1.8875423E38)
            r9 = 1
            java.lang.Object[] r1 = new java.lang.Object[r9]
            java.lang.String r2 = r7.title
            r5 = 0
            r1[r5] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0861:
            r5 = 0
            r9 = 1
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r10 == 0) goto L_0x0878
            r0 = 2131624126(0x7f0e00be, float:1.8875423E38)
            java.lang.Object[] r1 = new java.lang.Object[r9]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0878:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r9 == 0) goto L_0x0884
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x14ce
        L_0x0884:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r9 == 0) goto L_0x0e65
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r2 == 0) goto L_0x0b5d
            boolean r2 = r7.megagroup
            if (r2 == 0) goto L_0x0894
            goto L_0x0b5d
        L_0x0894:
            org.telegram.messenger.MessageObject r2 = r0.replyMessageObject
            if (r2 != 0) goto L_0x08ab
            r0 = 2131626233(0x7f0e08f9, float:1.8879696E38)
            r6 = 1
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r7.title
            r5 = 0
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x08ab:
            r5 = 0
            r6 = 1
            boolean r8 = r2.isMusic()
            if (r8 == 0) goto L_0x08c4
            r0 = 2131626230(0x7f0e08f6, float:1.887969E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x08c4:
            boolean r6 = r2.isVideo()
            r8 = 2131626254(0x7f0e090e, float:1.887974E38)
            java.lang.String r9 = "NotificationActionPinnedTextChannel"
            if (r6 == 0) goto L_0x0914
            r6 = 19
            if (r1 < r6) goto L_0x0901
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0901
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r13)
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r7.title
            r3 = 0
            r1[r3] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x041e
        L_0x0901:
            r2 = 1
            r3 = 0
            r0 = 2131626257(0x7f0e0911, float:1.8879745E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r7.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0914:
            boolean r6 = r2.isGif()
            if (r6 == 0) goto L_0x095f
            r6 = 19
            if (r1 < r6) goto L_0x094c
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x094c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r7.title
            r3 = 0
            r1[r3] = r2
            r5 = 1
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x041e
        L_0x094c:
            r3 = 0
            r5 = 1
            r0 = 2131626224(0x7f0e08f0, float:1.8879678E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r7.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x095f:
            r3 = 0
            r5 = 1
            boolean r6 = r2.isVoice()
            if (r6 == 0) goto L_0x0978
            r0 = 2131626260(0x7f0e0914, float:1.8879751E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r7.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0978:
            boolean r6 = r2.isRoundVideo()
            if (r6 == 0) goto L_0x098f
            r0 = 2131626245(0x7f0e0905, float:1.887972E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r7.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x098f:
            boolean r5 = r2.isSticker()
            if (r5 != 0) goto L_0x0b2f
            boolean r5 = r2.isAnimatedSticker()
            if (r5 == 0) goto L_0x099d
            goto L_0x0b2f
        L_0x099d:
            org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r5.media
            boolean r10 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r10 == 0) goto L_0x09e8
            r10 = 19
            if (r1 < r10) goto L_0x09d5
            java.lang.String r0 = r5.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09d5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r4)
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r7.title
            r3 = 0
            r1[r3] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x041e
        L_0x09d5:
            r2 = 1
            r3 = 0
            r0 = 2131626209(0x7f0e08e1, float:1.8879648E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r7.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x09e8:
            boolean r4 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x0b1c
            boolean r4 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x09f2
            goto L_0x0b1c
        L_0x09f2:
            boolean r4 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x0a09
            r0 = 2131626220(0x7f0e08ec, float:1.887967E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r7.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0a09:
            boolean r4 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r4 == 0) goto L_0x0a31
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626206(0x7f0e08de, float:1.8879642E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r7.title
            r3 = 0
            r2[r3] = r4
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x041e
        L_0x0a31:
            boolean r3 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0a6d
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r6 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r6
            org.telegram.tgnet.TLRPC$Poll r1 = r6.poll
            boolean r2 = r1.quiz
            if (r2 == 0) goto L_0x0a55
            r2 = 2131626242(0x7f0e0902, float:1.8879715E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = r7.title
            r0 = 0
            r3[r0] = r4
            java.lang.String r0 = r1.question
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x041e
        L_0x0a55:
            r0 = 0
            r3 = 2
            r4 = 1
            r2 = 2131626239(0x7f0e08ff, float:1.8879709E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r5 = r7.title
            r3[r0] = r5
            java.lang.String r0 = r1.question
            r3[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x041e
        L_0x0a6d:
            boolean r3 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0ab4
            r3 = 19
            if (r1 < r3) goto L_0x0aa1
            java.lang.String r1 = r5.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0aa1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r15)
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r7.title
            r0 = 0
            r2[r0] = r3
            r3 = 1
            r2[r3] = r1
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r2)
            goto L_0x041e
        L_0x0aa1:
            r0 = 0
            r3 = 1
            r1 = 2131626236(0x7f0e08fc, float:1.8879702E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            java.lang.String r3 = r7.title
            r2[r0] = r3
            java.lang.String r0 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x041e
        L_0x0ab4:
            r0 = 0
            r3 = 1
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0acb
            r1 = 2131626212(0x7f0e08e4, float:1.8879654E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            java.lang.String r3 = r7.title
            r2[r0] = r3
            java.lang.String r0 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x041e
        L_0x0acb:
            java.lang.CharSequence r0 = r2.messageText
            if (r0 == 0) goto L_0x0b09
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0b09
            java.lang.CharSequence r0 = r2.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0af8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r5 = 0
            java.lang.CharSequence r0 = r0.subSequence(r5, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0af9
        L_0x0af8:
            r5 = 0
        L_0x0af9:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r7.title
            r1[r5] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x041e
        L_0x0b09:
            r2 = 1
            r5 = 0
            r0 = 2131626233(0x7f0e08f9, float:1.8879696E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0b1c:
            r2 = 1
            r5 = 0
            r0 = 2131626218(0x7f0e08ea, float:1.8879666E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0b2f:
            r5 = 0
            java.lang.String r0 = r2.getStickerEmoji()
            if (r0 == 0) goto L_0x0b4b
            r1 = 2131626250(0x7f0e090a, float:1.887973E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r7.title
            r2[r5] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x041e
        L_0x0b4b:
            r3 = 1
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0b5d:
            r5 = 0
            org.telegram.messenger.MessageObject r2 = r0.replyMessageObject
            if (r2 != 0) goto L_0x0b77
            r0 = 2131626232(0x7f0e08f8, float:1.8879694E38)
            r8 = 2
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r5] = r6
            java.lang.String r2 = r7.title
            r9 = 1
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0b77:
            r8 = 2
            r9 = 1
            boolean r10 = r2.isMusic()
            if (r10 == 0) goto L_0x0b92
            r0 = 2131626229(0x7f0e08f5, float:1.8879688E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r5] = r6
            java.lang.String r2 = r7.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0b92:
            boolean r8 = r2.isVideo()
            r9 = 2131626253(0x7f0e090d, float:1.8879737E38)
            java.lang.String r10 = "NotificationActionPinnedText"
            if (r8 == 0) goto L_0x0be8
            r8 = 19
            if (r1 < r8) goto L_0x0bd2
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0bd2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r13)
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = r7.title
            r4 = 2
            r1[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r9, r1)
            goto L_0x041e
        L_0x0bd2:
            r2 = 0
            r3 = 1
            r4 = 2
            r0 = 2131626256(0x7f0e0910, float:1.8879743E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0be8:
            boolean r8 = r2.isGif()
            if (r8 == 0) goto L_0x0CLASSNAME
            r8 = 19
            if (r1 < r8) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r6
            r5 = 1
            r1[r5] = r0
            java.lang.String r0 = r7.title
            r8 = 2
            r1[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r9, r1)
            goto L_0x041e
        L_0x0CLASSNAME:
            r3 = 0
            r5 = 1
            r8 = 2
            r0 = 2131626223(0x7f0e08ef, float:1.8879676E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r3] = r6
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0CLASSNAME:
            r3 = 0
            r5 = 1
            r8 = 2
            boolean r11 = r2.isVoice()
            if (r11 == 0) goto L_0x0CLASSNAME
            r0 = 2131626259(0x7f0e0913, float:1.887975E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r3] = r6
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0CLASSNAME:
            boolean r11 = r2.isRoundVideo()
            if (r11 == 0) goto L_0x0c6e
            r0 = 2131626244(0x7f0e0904, float:1.8879719E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r3] = r6
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0c6e:
            boolean r5 = r2.isSticker()
            if (r5 != 0) goto L_0x0e32
            boolean r5 = r2.isAnimatedSticker()
            if (r5 == 0) goto L_0x0c7c
            goto L_0x0e32
        L_0x0c7c:
            org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r5.media
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r11 == 0) goto L_0x0ccd
            r11 = 19
            if (r1 < r11) goto L_0x0cb7
            java.lang.String r0 = r5.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0cb7
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r4)
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = r7.title
            r4 = 2
            r1[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r9, r1)
            goto L_0x041e
        L_0x0cb7:
            r2 = 0
            r3 = 1
            r4 = 2
            r0 = 2131626208(0x7f0e08e0, float:1.8879646E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0ccd:
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x0e1c
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x0cd7
            goto L_0x0e1c
        L_0x0cd7:
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x0cf1
            r0 = 2131626219(0x7f0e08eb, float:1.8879668E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x0cf1:
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r4 == 0) goto L_0x0d1c
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626205(0x7f0e08dd, float:1.887964E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r6
            java.lang.String r3 = r7.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x041e
        L_0x0d1c:
            boolean r3 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0d5e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r8 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r8
            org.telegram.tgnet.TLRPC$Poll r1 = r8.poll
            boolean r2 = r1.quiz
            if (r2 == 0) goto L_0x0d43
            r2 = 2131626241(0x7f0e0901, float:1.8879713E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r0 = 0
            r3[r0] = r6
            java.lang.String r0 = r7.title
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = r1.question
            r5 = 2
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x041e
        L_0x0d43:
            r0 = 0
            r3 = 3
            r4 = 1
            r5 = 2
            r2 = 2131626238(0x7f0e08fe, float:1.8879707E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r0] = r6
            java.lang.String r0 = r7.title
            r3[r4] = r0
            java.lang.String r0 = r1.question
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x041e
        L_0x0d5e:
            boolean r3 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0dab
            r3 = 19
            if (r1 < r3) goto L_0x0d95
            java.lang.String r1 = r5.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0d95
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r15)
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r0 = 0
            r2[r0] = r6
            r3 = 1
            r2[r3] = r1
            java.lang.String r0 = r7.title
            r1 = 2
            r2[r1] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r9, r2)
            goto L_0x041e
        L_0x0d95:
            r0 = 0
            r1 = 2
            r3 = 1
            r2 = 2131626235(0x7f0e08fb, float:1.88797E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r0] = r6
            java.lang.String r0 = r7.title
            r1[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r1)
            goto L_0x041e
        L_0x0dab:
            r0 = 0
            r1 = 2
            r3 = 1
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0dc5
            r2 = 2131626211(0x7f0e08e3, float:1.8879652E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r0] = r6
            java.lang.String r0 = r7.title
            r1[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r1)
            goto L_0x041e
        L_0x0dc5:
            java.lang.CharSequence r1 = r2.messageText
            if (r1 == 0) goto L_0x0e06
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0e06
            java.lang.CharSequence r1 = r2.messageText
            int r2 = r1.length()
            r3 = 20
            if (r2 <= r3) goto L_0x0df2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 20
            r0 = 0
            java.lang.CharSequence r1 = r1.subSequence(r0, r3)
            r2.append(r1)
            java.lang.String r1 = "..."
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            goto L_0x0df3
        L_0x0df2:
            r0 = 0
        L_0x0df3:
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r0] = r6
            r3 = 1
            r2[r3] = r1
            java.lang.String r0 = r7.title
            r1 = 2
            r2[r1] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r9, r2)
            goto L_0x041e
        L_0x0e06:
            r0 = 0
            r1 = 2
            r3 = 1
            r2 = 2131626232(0x7f0e08f8, float:1.8879694E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r0] = r6
            java.lang.String r0 = r7.title
            r1[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r1)
            goto L_0x041e
        L_0x0e1c:
            r0 = 0
            r1 = 2
            r3 = 1
            r2 = 2131626217(0x7f0e08e9, float:1.8879664E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r0] = r6
            java.lang.String r0 = r7.title
            r1[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r1)
            goto L_0x041e
        L_0x0e32:
            r0 = 0
            r3 = 1
            java.lang.String r1 = r2.getStickerEmoji()
            if (r1 == 0) goto L_0x0e51
            r2 = 2131626249(0x7f0e0909, float:1.8879729E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r0] = r6
            java.lang.String r0 = r7.title
            r4[r3] = r0
            r5 = 2
            r4[r5] = r1
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            goto L_0x041e
        L_0x0e51:
            r5 = 2
            r1 = 2131626247(0x7f0e0907, float:1.8879725E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r0] = r6
            java.lang.String r0 = r7.title
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x041e
        L_0x0e65:
            boolean r1 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r1 == 0) goto L_0x14cd
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x14ce
        L_0x0e71:
            org.telegram.tgnet.TLRPC$Peer r1 = r5.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0ea7
            boolean r1 = r7.megagroup
            if (r1 != 0) goto L_0x0ea7
            boolean r0 = r21.isVideoAvatar()
            if (r0 == 0) goto L_0x0e94
            r0 = 2131624733(0x7f0e031d, float:1.8876654E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r7.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0e94:
            r1 = 1
            r3 = 0
            r0 = 2131624699(0x7f0e02fb, float:1.8876585E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r7.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0ea7:
            r3 = 0
            boolean r0 = r21.isVideoAvatar()
            if (r0 == 0) goto L_0x0ec3
            r0 = 2131626266(0x7f0e091a, float:1.8879763E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r6
            java.lang.String r2 = r7.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0ec3:
            r1 = 2
            r4 = 1
            r0 = 2131626265(0x7f0e0919, float:1.8879761E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r6
            java.lang.String r2 = r7.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0ed8:
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r5 == 0) goto L_0x1187
            boolean r5 = r7.megagroup
            if (r5 != 0) goto L_0x1187
            boolean r5 = r21.isMediaEmpty()
            if (r5 == 0) goto L_0x0f1d
            if (r22 != 0) goto L_0x0f0c
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0f0c
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3 = 1
            r1[r3] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r3
            goto L_0x14ce
        L_0x0f0c:
            r2 = 0
            r3 = 1
            r0 = 2131624683(0x7f0e02eb, float:1.8876553E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0f1d:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r5.media
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r7 == 0) goto L_0x0f6b
            if (r22 != 0) goto L_0x0f5a
            r3 = 19
            if (r1 < r3) goto L_0x0f5a
            java.lang.String r1 = r5.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0f5a
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r3 = 1
            r1[r3] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r3
            goto L_0x14ce
        L_0x0f5a:
            r2 = 0
            r3 = 1
            r0 = 2131624684(0x7f0e02ec, float:1.8876555E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0f6b:
            boolean r5 = r21.isVideo()
            if (r5 == 0) goto L_0x0fb9
            if (r22 != 0) goto L_0x0fa8
            r3 = 19
            if (r1 < r3) goto L_0x0fa8
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0fa8
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r5 = 1
            r1[r5] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r5
            goto L_0x14ce
        L_0x0fa8:
            r2 = 0
            r5 = 1
            r0 = 2131624690(0x7f0e02f2, float:1.8876567E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0fb9:
            r2 = 0
            r5 = 1
            boolean r7 = r21.isVoice()
            if (r7 == 0) goto L_0x0fd0
            r0 = 2131624675(0x7f0e02e3, float:1.8876536E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0fd0:
            boolean r7 = r21.isRoundVideo()
            if (r7 == 0) goto L_0x0fe5
            r0 = 2131624687(0x7f0e02ef, float:1.887656E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0fe5:
            boolean r7 = r21.isMusic()
            if (r7 == 0) goto L_0x0ffa
            r0 = 2131624682(0x7f0e02ea, float:1.887655E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x0ffa:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x101f
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            r0 = 2131624676(0x7f0e02e4, float:1.8876538E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r6
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "ChannelMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x101f:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r7 == 0) goto L_0x1057
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x1041
            r1 = 2131624686(0x7f0e02ee, float:1.8876559E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r2 = 0
            r3[r2] = r6
            java.lang.String r0 = r0.question
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x041e
        L_0x1041:
            r2 = 0
            r3 = 2
            r4 = 1
            r1 = 2131624685(0x7f0e02ed, float:1.8876557E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r6
            java.lang.String r0 = r0.question
            r3[r4] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x041e
        L_0x1057:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r7 != 0) goto L_0x1176
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r7 == 0) goto L_0x1061
            goto L_0x1176
        L_0x1061:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r7 == 0) goto L_0x1076
            r0 = 2131624680(0x7f0e02e8, float:1.8876547E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x1076:
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x1146
            boolean r5 = r21.isSticker()
            if (r5 != 0) goto L_0x111d
            boolean r5 = r21.isAnimatedSticker()
            if (r5 == 0) goto L_0x1088
            goto L_0x111d
        L_0x1088:
            boolean r5 = r21.isGif()
            if (r5 == 0) goto L_0x10d6
            if (r22 != 0) goto L_0x10c5
            r4 = 19
            if (r1 < r4) goto L_0x10c5
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x10c5
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r3 = 1
            r1[r3] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r3
            goto L_0x14ce
        L_0x10c5:
            r2 = 0
            r3 = 1
            r0 = 2131624679(0x7f0e02e7, float:1.8876545E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageGIF"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x10d6:
            r2 = 0
            if (r22 != 0) goto L_0x110d
            r3 = 19
            if (r1 < r3) goto L_0x110d
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x110d
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r3 = 1
            r1[r3] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r3
            goto L_0x14ce
        L_0x110d:
            r3 = 1
            r0 = 2131624677(0x7f0e02e5, float:1.887654E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x111d:
            r2 = 0
            r3 = 1
            java.lang.String r0 = r21.getStickerEmoji()
            if (r0 == 0) goto L_0x1137
            r1 = 2131624689(0x7f0e02f1, float:1.8876565E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r2] = r6
            r4[r3] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r4)
            goto L_0x041e
        L_0x1137:
            r0 = 2131624688(0x7f0e02f0, float:1.8876563E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x1146:
            r2 = 0
            if (r22 != 0) goto L_0x1166
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1166
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r6
            java.lang.CharSequence r0 = r0.messageText
            r3 = 1
            r1[r3] = r0
            r0 = 2131626329(0x7f0e0959, float:1.8879891E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r23[r2] = r3
            goto L_0x14ce
        L_0x1166:
            r3 = 1
            r0 = 2131624683(0x7f0e02eb, float:1.8876553E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x1176:
            r2 = 0
            r3 = 1
            r0 = 2131624681(0x7f0e02e9, float:1.8876549E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r6
            java.lang.String r2 = "ChannelMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x1187:
            boolean r5 = r21.isMediaEmpty()
            r8 = 2131626312(0x7f0e0948, float:1.8879857E38)
            java.lang.String r9 = "NotificationMessageGroupText"
            if (r5 == 0) goto L_0x11ca
            if (r22 != 0) goto L_0x11b6
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x11b6
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2 = 2
            r1[r2] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x14ce
        L_0x11b6:
            r2 = 2
            r0 = 2131626305(0x7f0e0941, float:1.8879842E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r0, r1)
            goto L_0x14ce
        L_0x11ca:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r5.media
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r10 == 0) goto L_0x121d
            if (r22 != 0) goto L_0x1207
            r3 = 19
            if (r1 < r3) goto L_0x1207
            java.lang.String r1 = r5.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1207
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x14ce
        L_0x1207:
            r2 = 2
            r0 = 2131626306(0x7f0e0942, float:1.8879844E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x121d:
            boolean r5 = r21.isVideo()
            if (r5 == 0) goto L_0x1270
            if (r22 != 0) goto L_0x125a
            r3 = 19
            if (r1 < r3) goto L_0x125a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x125a
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x14ce
        L_0x125a:
            r2 = 2
            r0 = 2131626313(0x7f0e0949, float:1.8879859E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r5 = 0
            r1[r5] = r6
            java.lang.String r2 = r7.title
            r10 = 1
            r1[r10] = r2
            java.lang.String r2 = " "
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x1270:
            r2 = 2
            r5 = 0
            r10 = 1
            boolean r12 = r21.isVoice()
            if (r12 == 0) goto L_0x128c
            r0 = 2131626295(0x7f0e0937, float:1.8879822E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r5] = r6
            java.lang.String r2 = r7.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationMessageGroupAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x128c:
            boolean r12 = r21.isRoundVideo()
            if (r12 == 0) goto L_0x12a5
            r0 = 2131626309(0x7f0e0945, float:1.887985E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r5] = r6
            java.lang.String r2 = r7.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationMessageGroupRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x12a5:
            boolean r12 = r21.isMusic()
            if (r12 == 0) goto L_0x12be
            r0 = 2131626304(0x7f0e0940, float:1.887984E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r5] = r6
            java.lang.String r2 = r7.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationMessageGroupMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x12be:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r10 == 0) goto L_0x12e9
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            r0 = 2131626296(0x7f0e0938, float:1.8879824E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x12e9:
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r10 == 0) goto L_0x132b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x1310
            r1 = 2131626308(0x7f0e0944, float:1.8879849E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r2 = 0
            r3[r2] = r6
            java.lang.String r2 = r7.title
            r4 = 1
            r3[r4] = r2
            java.lang.String r0 = r0.question
            r5 = 2
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageGroupQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x041e
        L_0x1310:
            r2 = 0
            r3 = 3
            r4 = 1
            r5 = 2
            r1 = 2131626307(0x7f0e0943, float:1.8879846E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r6
            java.lang.String r2 = r7.title
            r3[r4] = r2
            java.lang.String r0 = r0.question
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x041e
        L_0x132b:
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r10 == 0) goto L_0x134c
            r0 = 2131626298(0x7f0e093a, float:1.8879828E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            org.telegram.tgnet.TLRPC$TL_game r2 = r5.game
            java.lang.String r2 = r2.title
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupGame"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x134c:
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r10 != 0) goto L_0x1487
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r10 == 0) goto L_0x1356
            goto L_0x1487
        L_0x1356:
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r10 == 0) goto L_0x1370
            r0 = 2131626302(0x7f0e093e, float:1.8879836E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x1370:
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x1455
            boolean r5 = r21.isSticker()
            if (r5 != 0) goto L_0x1422
            boolean r5 = r21.isAnimatedSticker()
            if (r5 == 0) goto L_0x1382
            goto L_0x1422
        L_0x1382:
            boolean r5 = r21.isGif()
            if (r5 == 0) goto L_0x13d5
            if (r22 != 0) goto L_0x13bf
            r4 = 19
            if (r1 < r4) goto L_0x13bf
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x13bf
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r4 = 1
            r1[r4] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x14ce
        L_0x13bf:
            r2 = 2
            r0 = 2131626300(0x7f0e093c, float:1.8879832E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x13d5:
            if (r22 != 0) goto L_0x140c
            r3 = 19
            if (r1 < r3) goto L_0x140c
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x140c
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x14ce
        L_0x140c:
            r2 = 2
            r0 = 2131626297(0x7f0e0939, float:1.8879826E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x14ce
        L_0x1422:
            r2 = 0
            r3 = 1
            java.lang.String r0 = r21.getStickerEmoji()
            if (r0 == 0) goto L_0x1441
            r1 = 2131626311(0x7f0e0947, float:1.8879855E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r2] = r6
            java.lang.String r2 = r7.title
            r4[r3] = r2
            r5 = 2
            r4[r5] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r4)
            goto L_0x041e
        L_0x1441:
            r5 = 2
            r0 = 2131626310(0x7f0e0946, float:1.8879853E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r6
            java.lang.String r2 = r7.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x041e
        L_0x1455:
            if (r22 != 0) goto L_0x1474
            java.lang.CharSequence r2 = r0.messageText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x1474
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r1 = 0
            r2[r1] = r6
            java.lang.String r1 = r7.title
            r3 = 1
            r2[r3] = r1
            java.lang.CharSequence r0 = r0.messageText
            r4 = 2
            r2[r4] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r9, r8, r2)
            goto L_0x14ce
        L_0x1474:
            r1 = 0
            r3 = 1
            r4 = 2
            r0 = 2131626305(0x7f0e0941, float:1.8879842E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r1] = r6
            java.lang.String r1 = r7.title
            r2[r3] = r1
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r0, r2)
            goto L_0x14ce
        L_0x1487:
            r1 = 0
            r3 = 1
            r4 = 2
            r0 = 2131626303(0x7f0e093f, float:1.8879838E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r1] = r6
            java.lang.String r1 = r7.title
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x14ce
        L_0x149c:
            r1 = 0
            if (r24 == 0) goto L_0x14a1
            r24[r1] = r1
        L_0x14a1:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r0 == 0) goto L_0x14ba
            boolean r0 = r7.megagroup
            if (r0 != 0) goto L_0x14ba
            r0 = 2131624683(0x7f0e02eb, float:1.8876553E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r1] = r6
            java.lang.String r1 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x14ce
        L_0x14ba:
            r2 = 1
            r0 = 2131626305(0x7f0e0941, float:1.8879842E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r1] = r6
            java.lang.String r1 = r7.title
            r3[r2] = r1
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r0, r3)
            goto L_0x14ce
        L_0x14cc:
            r2 = 0
        L_0x14cd:
            r15 = r2
        L_0x14ce:
            return r15
        L_0x14cf:
            r0 = 2131627960(0x7f0e0fb8, float:1.88832E38)
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
            r6 = 2131626379(0x7f0e098b, float:1.8879993E38)
            java.lang.String r7 = "NotificationsSilent"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.String r7 = "silent"
        L_0x0094:
            r8 = 0
            goto L_0x00e9
        L_0x0096:
            if (r36 == 0) goto L_0x00bf
            if (r37 == 0) goto L_0x00a0
            r9 = 2131626357(0x7f0e0975, float:1.8879948E38)
            java.lang.String r14 = "NotificationsInAppDefault"
            goto L_0x00a5
        L_0x00a0:
            r9 = 2131626341(0x7f0e0965, float:1.8879915E38)
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
            r6 = 2131626338(0x7f0e0962, float:1.887991E38)
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
            r14 = 2131625027(0x7f0e0443, float:1.887725E38)
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
            if (r8 != 0) goto L_0x0632
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
            r7 = 2131627151(0x7f0e0c8f, float:1.8881558E38)
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
        L_0x0632:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX WARNING: type inference failed for: r14v15 */
    /* JADX WARNING: type inference failed for: r14v16 */
    /* JADX WARNING: type inference failed for: r14v29 */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x07bb, code lost:
        if (r4 >= 26) goto L_0x07bd;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:383:0x086f */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0201 A[SYNTHETIC, Splitter:B:102:0x0201] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0273 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0323 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x03fe A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0401 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x041a A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0498 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x04a0 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x04fa A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x0546 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x054a A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0554 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0558 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x055c A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x057b A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x05b5 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x05ed A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x067c A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x0735 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x077f  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x078b  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x07b7 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x07c3 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0886 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0890 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0897 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x08a5 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x091b A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x09ad A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c5 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x09d7 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x09f0 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ce A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00d5 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00e4 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00e7 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00f3 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00fe A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0112 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0118 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x012e A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0145 A[SYNTHETIC, Splitter:B:80:0x0145] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0179 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0185 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x019c A[SYNTHETIC, Splitter:B:96:0x019c] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01b3 A[Catch:{ Exception -> 0x0a21 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r42) {
        /*
            r41 = this;
            r15 = r41
            java.lang.String r1 = "currentAccount"
            int r2 = android.os.Build.VERSION.SDK_INT
            org.telegram.messenger.UserConfig r0 = r41.getUserConfig()
            boolean r0 = r0.isClientActivated()
            if (r0 == 0) goto L_0x0a26
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r15.pushMessages
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0a26
            boolean r0 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r0 != 0) goto L_0x0024
            int r0 = r15.currentAccount
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            if (r0 == r3) goto L_0x0024
            goto L_0x0a26
        L_0x0024:
            org.telegram.tgnet.ConnectionsManager r0 = r41.getConnectionsManager()     // Catch:{ Exception -> 0x0a21 }
            r0.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0a21 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r15.pushMessages     // Catch:{ Exception -> 0x0a21 }
            r3 = 0
            java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x0a21 }
            r4 = r0
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4     // Catch:{ Exception -> 0x0a21 }
            org.telegram.messenger.AccountInstance r0 = r41.getAccountInstance()     // Catch:{ Exception -> 0x0a21 }
            android.content.SharedPreferences r5 = r0.getNotificationsSettings()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r0 = "dismissDate"
            int r0 = r5.getInt(r0, r3)     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner     // Catch:{ Exception -> 0x0a21 }
            int r6 = r6.date     // Catch:{ Exception -> 0x0a21 }
            if (r6 > r0) goto L_0x004d
            r41.dismissNotification()     // Catch:{ Exception -> 0x0a21 }
            return
        L_0x004d:
            long r6 = r4.getDialogId()     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner     // Catch:{ Exception -> 0x0a21 }
            boolean r8 = r8.mentioned     // Catch:{ Exception -> 0x0a21 }
            if (r8 == 0) goto L_0x005d
            int r8 = r4.getFromChatId()     // Catch:{ Exception -> 0x0a21 }
            long r8 = (long) r8     // Catch:{ Exception -> 0x0a21 }
            goto L_0x005e
        L_0x005d:
            r8 = r6
        L_0x005e:
            r4.getId()     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$Message r10 = r4.messageOwner     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.peer_id     // Catch:{ Exception -> 0x0a21 }
            int r11 = r10.chat_id     // Catch:{ Exception -> 0x0a21 }
            if (r11 == 0) goto L_0x006a
            goto L_0x006c
        L_0x006a:
            int r11 = r10.channel_id     // Catch:{ Exception -> 0x0a21 }
        L_0x006c:
            int r10 = r10.user_id     // Catch:{ Exception -> 0x0a21 }
            boolean r12 = r4.isFromUser()     // Catch:{ Exception -> 0x0a21 }
            if (r12 == 0) goto L_0x0086
            if (r10 == 0) goto L_0x0080
            org.telegram.messenger.UserConfig r12 = r41.getUserConfig()     // Catch:{ Exception -> 0x0a21 }
            int r12 = r12.getClientUserId()     // Catch:{ Exception -> 0x0a21 }
            if (r10 != r12) goto L_0x0086
        L_0x0080:
            org.telegram.tgnet.TLRPC$Message r10 = r4.messageOwner     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.from_id     // Catch:{ Exception -> 0x0a21 }
            int r10 = r10.user_id     // Catch:{ Exception -> 0x0a21 }
        L_0x0086:
            org.telegram.messenger.MessagesController r12 = r41.getMessagesController()     // Catch:{ Exception -> 0x0a21 }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$User r12 = r12.getUser(r13)     // Catch:{ Exception -> 0x0a21 }
            if (r11 == 0) goto L_0x00b7
            org.telegram.messenger.MessagesController r13 = r41.getMessagesController()     // Catch:{ Exception -> 0x0a21 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$Chat r3 = r13.getChat(r3)     // Catch:{ Exception -> 0x0a21 }
            if (r3 != 0) goto L_0x00ab
            boolean r13 = r4.isFcmMessage()     // Catch:{ Exception -> 0x0a21 }
            if (r13 == 0) goto L_0x00ab
            boolean r13 = r4.localChannel     // Catch:{ Exception -> 0x0a21 }
            goto L_0x00b9
        L_0x00ab:
            boolean r13 = org.telegram.messenger.ChatObject.isChannel(r3)     // Catch:{ Exception -> 0x0a21 }
            if (r13 == 0) goto L_0x00b8
            boolean r13 = r3.megagroup     // Catch:{ Exception -> 0x0a21 }
            if (r13 != 0) goto L_0x00b8
            r13 = 1
            goto L_0x00b9
        L_0x00b7:
            r3 = 0
        L_0x00b8:
            r13 = 0
        L_0x00b9:
            int r14 = r15.getNotifyOverride(r5, r8)     // Catch:{ Exception -> 0x0a21 }
            r19 = r1
            r1 = -1
            r20 = r10
            r10 = 2
            if (r14 != r1) goto L_0x00ce
            java.lang.Boolean r14 = java.lang.Boolean.valueOf(r13)     // Catch:{ Exception -> 0x0a21 }
            boolean r14 = r15.isGlobalNotificationsEnabled(r6, r14)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x00d3
        L_0x00ce:
            if (r14 == r10) goto L_0x00d2
            r14 = 1
            goto L_0x00d3
        L_0x00d2:
            r14 = 0
        L_0x00d3:
            if (r11 == 0) goto L_0x00d7
            if (r3 == 0) goto L_0x00d9
        L_0x00d7:
            if (r12 != 0) goto L_0x00e2
        L_0x00d9:
            boolean r21 = r4.isFcmMessage()     // Catch:{ Exception -> 0x0a21 }
            if (r21 == 0) goto L_0x00e2
            java.lang.String r1 = r4.localName     // Catch:{ Exception -> 0x0a21 }
            goto L_0x00eb
        L_0x00e2:
            if (r3 == 0) goto L_0x00e7
            java.lang.String r1 = r3.title     // Catch:{ Exception -> 0x0a21 }
            goto L_0x00eb
        L_0x00e7:
            java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r12)     // Catch:{ Exception -> 0x0a21 }
        L_0x00eb:
            r22 = r1
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a21 }
            if (r1 != 0) goto L_0x00fa
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a21 }
            if (r1 == 0) goto L_0x00f8
            goto L_0x00fa
        L_0x00f8:
            r1 = 0
            goto L_0x00fb
        L_0x00fa:
            r1 = 1
        L_0x00fb:
            int r10 = (int) r6     // Catch:{ Exception -> 0x0a21 }
            if (r10 == 0) goto L_0x0112
            r23 = r4
            android.util.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0a21 }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0a21 }
            r24 = r12
            r12 = 1
            if (r4 > r12) goto L_0x0116
            if (r1 == 0) goto L_0x010e
            goto L_0x0116
        L_0x010e:
            r1 = r22
            r4 = 1
            goto L_0x0138
        L_0x0112:
            r23 = r4
            r24 = r12
        L_0x0116:
            if (r1 == 0) goto L_0x012e
            if (r11 == 0) goto L_0x0124
            java.lang.String r1 = "NotificationHiddenChatName"
            r4 = 2131626280(0x7f0e0928, float:1.8879792E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0137
        L_0x0124:
            java.lang.String r1 = "NotificationHiddenName"
            r4 = 2131626283(0x7f0e092b, float:1.8879798E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0137
        L_0x012e:
            java.lang.String r1 = "AppName"
            r4 = 2131624266(0x7f0e014a, float:1.8875707E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)     // Catch:{ Exception -> 0x0a21 }
        L_0x0137:
            r4 = 0
        L_0x0138:
            int r12 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0a21 }
            r25 = r10
            java.lang.String r10 = ""
            r26 = r13
            r13 = 1
            if (r12 <= r13) goto L_0x0179
            android.util.LongSparseArray<java.lang.Integer> r12 = r15.pushDialogs     // Catch:{ Exception -> 0x0a21 }
            int r12 = r12.size()     // Catch:{ Exception -> 0x0a21 }
            if (r12 != r13) goto L_0x015a
            org.telegram.messenger.UserConfig r12 = r41.getUserConfig()     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$User r12 = r12.getCurrentUser()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r12)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x017a
        L_0x015a:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r12.<init>()     // Catch:{ Exception -> 0x0a21 }
            org.telegram.messenger.UserConfig r13 = r41.getUserConfig()     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$User r13 = r13.getCurrentUser()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r13 = org.telegram.messenger.UserObject.getFirstName(r13)     // Catch:{ Exception -> 0x0a21 }
            r12.append(r13)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r13 = "・"
            r12.append(r13)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0a21 }
            goto L_0x017a
        L_0x0179:
            r12 = r10
        L_0x017a:
            android.util.LongSparseArray<java.lang.Integer> r13 = r15.pushDialogs     // Catch:{ Exception -> 0x0a21 }
            int r13 = r13.size()     // Catch:{ Exception -> 0x0a21 }
            r27 = r11
            r11 = 1
            if (r13 != r11) goto L_0x018f
            r11 = 23
            if (r2 >= r11) goto L_0x018a
            goto L_0x018f
        L_0x018a:
            r28 = r2
        L_0x018c:
            r29 = r5
            goto L_0x01e9
        L_0x018f:
            android.util.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0a21 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r13 = "NewMessages"
            r28 = r2
            r2 = 1
            if (r11 != r2) goto L_0x01b3
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r2.<init>()     // Catch:{ Exception -> 0x0a21 }
            r2.append(r12)     // Catch:{ Exception -> 0x0a21 }
            int r11 = r15.total_unread_count     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r13, r11)     // Catch:{ Exception -> 0x0a21 }
            r2.append(r11)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a21 }
            r12 = r2
            goto L_0x018c
        L_0x01b3:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r2.<init>()     // Catch:{ Exception -> 0x0a21 }
            r2.append(r12)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r11 = "NotificationMessagesPeopleDisplayOrder"
            r29 = r5
            r12 = 2
            java.lang.Object[] r5 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x0a21 }
            int r12 = r15.total_unread_count     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r13, r12)     // Catch:{ Exception -> 0x0a21 }
            r13 = 0
            r5[r13] = r12     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r12 = "FromChats"
            android.util.LongSparseArray<java.lang.Integer> r13 = r15.pushDialogs     // Catch:{ Exception -> 0x0a21 }
            int r13 = r13.size()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r12, r13)     // Catch:{ Exception -> 0x0a21 }
            r13 = 1
            r5[r13] = r12     // Catch:{ Exception -> 0x0a21 }
            r12 = 2131626331(0x7f0e095b, float:1.8879895E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r11, r12, r5)     // Catch:{ Exception -> 0x0a21 }
            r2.append(r5)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a21 }
            r12 = r2
        L_0x01e9:
            androidx.core.app.NotificationCompat$Builder r2 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0a21 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a21 }
            r2.<init>(r5)     // Catch:{ Exception -> 0x0a21 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r15.pushMessages     // Catch:{ Exception -> 0x0a21 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r11 = ": "
            java.lang.String r13 = " "
            r30 = r6
            java.lang.String r6 = " @ "
            r7 = 1
            if (r5 != r7) goto L_0x0273
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r15.pushMessages     // Catch:{ Exception -> 0x0a21 }
            r5 = 0
            java.lang.Object r0 = r0.get(r5)     // Catch:{ Exception -> 0x0a21 }
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0     // Catch:{ Exception -> 0x0a21 }
            r32 = r8
            boolean[] r8 = new boolean[r7]     // Catch:{ Exception -> 0x0a21 }
            r7 = 0
            java.lang.String r9 = r15.getStringForMessage(r0, r5, r8, r7)     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x0a21 }
            boolean r0 = r0.silent     // Catch:{ Exception -> 0x0a21 }
            if (r9 != 0) goto L_0x021a
            return
        L_0x021a:
            if (r4 == 0) goto L_0x025f
            if (r3 == 0) goto L_0x0232
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r4.<init>()     // Catch:{ Exception -> 0x0a21 }
            r4.append(r6)     // Catch:{ Exception -> 0x0a21 }
            r4.append(r1)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r4 = r9.replace(r4, r10)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0260
        L_0x0232:
            r4 = 0
            boolean r5 = r8[r4]     // Catch:{ Exception -> 0x0a21 }
            if (r5 == 0) goto L_0x024b
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r4.<init>()     // Catch:{ Exception -> 0x0a21 }
            r4.append(r1)     // Catch:{ Exception -> 0x0a21 }
            r4.append(r11)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r4 = r9.replace(r4, r10)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0260
        L_0x024b:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r4.<init>()     // Catch:{ Exception -> 0x0a21 }
            r4.append(r1)     // Catch:{ Exception -> 0x0a21 }
            r4.append(r13)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r4 = r9.replace(r4, r10)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0260
        L_0x025f:
            r4 = r9
        L_0x0260:
            r2.setContentText(r4)     // Catch:{ Exception -> 0x0a21 }
            androidx.core.app.NotificationCompat$BigTextStyle r5 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0a21 }
            r5.<init>()     // Catch:{ Exception -> 0x0a21 }
            r5.bigText(r4)     // Catch:{ Exception -> 0x0a21 }
            r2.setStyle(r5)     // Catch:{ Exception -> 0x0a21 }
            r35 = r14
            r14 = r0
            goto L_0x0321
        L_0x0273:
            r32 = r8
            r2.setContentText(r12)     // Catch:{ Exception -> 0x0a21 }
            androidx.core.app.NotificationCompat$InboxStyle r5 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0a21 }
            r5.<init>()     // Catch:{ Exception -> 0x0a21 }
            r5.setBigContentTitle(r1)     // Catch:{ Exception -> 0x0a21 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0a21 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0a21 }
            r8 = 10
            int r7 = java.lang.Math.min(r8, r7)     // Catch:{ Exception -> 0x0a21 }
            r8 = 1
            boolean[] r9 = new boolean[r8]     // Catch:{ Exception -> 0x0a21 }
            r35 = r14
            r8 = 0
            r14 = 2
            r34 = 0
        L_0x0295:
            if (r8 >= r7) goto L_0x0317
            r36 = r7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0a21 }
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x0a21 }
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7     // Catch:{ Exception -> 0x0a21 }
            r37 = r2
            r39 = r8
            r38 = r12
            r2 = 0
            r12 = 0
            java.lang.String r8 = r15.getStringForMessage(r7, r12, r9, r2)     // Catch:{ Exception -> 0x0a21 }
            if (r8 == 0) goto L_0x030d
            org.telegram.tgnet.TLRPC$Message r2 = r7.messageOwner     // Catch:{ Exception -> 0x0a21 }
            int r7 = r2.date     // Catch:{ Exception -> 0x0a21 }
            if (r7 > r0) goto L_0x02b6
            goto L_0x030d
        L_0x02b6:
            r7 = 2
            if (r14 != r7) goto L_0x02bd
            boolean r14 = r2.silent     // Catch:{ Exception -> 0x0a21 }
            r34 = r8
        L_0x02bd:
            android.util.LongSparseArray<java.lang.Integer> r2 = r15.pushDialogs     // Catch:{ Exception -> 0x0a21 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0a21 }
            r7 = 1
            if (r2 != r7) goto L_0x030a
            if (r4 == 0) goto L_0x030a
            if (r3 == 0) goto L_0x02de
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r2.<init>()     // Catch:{ Exception -> 0x0a21 }
            r2.append(r6)     // Catch:{ Exception -> 0x0a21 }
            r2.append(r1)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r8 = r8.replace(r2, r10)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x030a
        L_0x02de:
            r2 = 0
            boolean r7 = r9[r2]     // Catch:{ Exception -> 0x0a21 }
            if (r7 == 0) goto L_0x02f7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r2.<init>()     // Catch:{ Exception -> 0x0a21 }
            r2.append(r1)     // Catch:{ Exception -> 0x0a21 }
            r2.append(r11)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r8 = r8.replace(r2, r10)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x030a
        L_0x02f7:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r2.<init>()     // Catch:{ Exception -> 0x0a21 }
            r2.append(r1)     // Catch:{ Exception -> 0x0a21 }
            r2.append(r13)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r8 = r8.replace(r2, r10)     // Catch:{ Exception -> 0x0a21 }
        L_0x030a:
            r5.addLine(r8)     // Catch:{ Exception -> 0x0a21 }
        L_0x030d:
            int r8 = r39 + 1
            r7 = r36
            r2 = r37
            r12 = r38
            goto L_0x0295
        L_0x0317:
            r37 = r2
            r5.setSummaryText(r12)     // Catch:{ Exception -> 0x0a21 }
            r2.setStyle(r5)     // Catch:{ Exception -> 0x0a21 }
            r9 = r34
        L_0x0321:
            if (r42 == 0) goto L_0x0335
            if (r35 == 0) goto L_0x0335
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0a21 }
            boolean r0 = r0.isRecordingAudio()     // Catch:{ Exception -> 0x0a21 }
            if (r0 != 0) goto L_0x0335
            r4 = 1
            if (r14 != r4) goto L_0x0333
            goto L_0x0335
        L_0x0333:
            r0 = 0
            goto L_0x0336
        L_0x0335:
            r0 = 1
        L_0x0336:
            java.lang.String r4 = "custom_"
            if (r0 != 0) goto L_0x03eb
            int r7 = (r30 > r32 ? 1 : (r30 == r32 ? 0 : -1))
            if (r7 != 0) goto L_0x03eb
            if (r3 == 0) goto L_0x03eb
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r7.<init>()     // Catch:{ Exception -> 0x0a21 }
            r7.append(r4)     // Catch:{ Exception -> 0x0a21 }
            r5 = r30
            r7.append(r5)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0a21 }
            r8 = r29
            r11 = 0
            boolean r7 = r8.getBoolean(r7, r11)     // Catch:{ Exception -> 0x0a21 }
            if (r7 == 0) goto L_0x0388
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r7.<init>()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r11 = "smart_max_count_"
            r7.append(r11)     // Catch:{ Exception -> 0x0a21 }
            r7.append(r5)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0a21 }
            r11 = 2
            int r7 = r8.getInt(r7, r11)     // Catch:{ Exception -> 0x0a21 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r11.<init>()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r13 = "smart_delay_"
            r11.append(r13)     // Catch:{ Exception -> 0x0a21 }
            r11.append(r5)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0a21 }
            r13 = 180(0xb4, float:2.52E-43)
            int r11 = r8.getInt(r11, r13)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x038b
        L_0x0388:
            r11 = 180(0xb4, float:2.52E-43)
            r7 = 2
        L_0x038b:
            if (r7 == 0) goto L_0x03e7
            android.util.LongSparseArray<android.graphics.Point> r13 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0a21 }
            java.lang.Object r13 = r13.get(r5)     // Catch:{ Exception -> 0x0a21 }
            android.graphics.Point r13 = (android.graphics.Point) r13     // Catch:{ Exception -> 0x0a21 }
            if (r13 != 0) goto L_0x03b0
            android.graphics.Point r7 = new android.graphics.Point     // Catch:{ Exception -> 0x0a21 }
            long r29 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a21 }
            r38 = r12
            r31 = 1000(0x3e8, double:4.94E-321)
            long r11 = r29 / r31
            int r12 = (int) r11     // Catch:{ Exception -> 0x0a21 }
            r11 = 1
            r7.<init>(r11, r12)     // Catch:{ Exception -> 0x0a21 }
            android.util.LongSparseArray<android.graphics.Point> r11 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0a21 }
            r11.put(r5, r7)     // Catch:{ Exception -> 0x0a21 }
        L_0x03ad:
            r12 = r9
            r7 = r10
            goto L_0x03f3
        L_0x03b0:
            r38 = r12
            int r12 = r13.y     // Catch:{ Exception -> 0x0a21 }
            int r12 = r12 + r11
            long r11 = (long) r12     // Catch:{ Exception -> 0x0a21 }
            long r29 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a21 }
            r31 = 1000(0x3e8, double:4.94E-321)
            long r29 = r29 / r31
            int r33 = (r11 > r29 ? 1 : (r11 == r29 ? 0 : -1))
            if (r33 >= 0) goto L_0x03ce
            long r11 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a21 }
            long r11 = r11 / r31
            int r7 = (int) r11     // Catch:{ Exception -> 0x0a21 }
            r11 = 1
            r13.set(r11, r7)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x03ad
        L_0x03ce:
            int r11 = r13.x     // Catch:{ Exception -> 0x0a21 }
            if (r11 >= r7) goto L_0x03e3
            r7 = 1
            int r11 = r11 + r7
            long r29 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a21 }
            r12 = r9
            r7 = r10
            r31 = 1000(0x3e8, double:4.94E-321)
            long r9 = r29 / r31
            int r10 = (int) r9     // Catch:{ Exception -> 0x0a21 }
            r13.set(r11, r10)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x03f3
        L_0x03e3:
            r12 = r9
            r7 = r10
            r13 = 1
            goto L_0x03f4
        L_0x03e7:
            r7 = r10
            r38 = r12
            goto L_0x03f2
        L_0x03eb:
            r7 = r10
            r38 = r12
            r8 = r29
            r5 = r30
        L_0x03f2:
            r12 = r9
        L_0x03f3:
            r13 = r0
        L_0x03f4:
            android.net.Uri r0 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r9 = r0.getPath()     // Catch:{ Exception -> 0x0a21 }
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0a21 }
            if (r0 != 0) goto L_0x0401
            r29 = 1
            goto L_0x0403
        L_0x0401:
            r29 = 0
        L_0x0403:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r0.<init>()     // Catch:{ Exception -> 0x0a21 }
            r0.append(r4)     // Catch:{ Exception -> 0x0a21 }
            r0.append(r5)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a21 }
            r4 = 0
            boolean r0 = r8.getBoolean(r0, r4)     // Catch:{ Exception -> 0x0a21 }
            r4 = 3
            if (r0 == 0) goto L_0x0498
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r0.<init>()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r10 = "vibrate_"
            r0.append(r10)     // Catch:{ Exception -> 0x0a21 }
            r0.append(r5)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a21 }
            r10 = 0
            int r0 = r8.getInt(r0, r10)     // Catch:{ Exception -> 0x0a21 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r10.<init>()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r11 = "priority_"
            r10.append(r11)     // Catch:{ Exception -> 0x0a21 }
            r10.append(r5)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0a21 }
            int r10 = r8.getInt(r10, r4)     // Catch:{ Exception -> 0x0a21 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r11.<init>()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r4 = "sound_path_"
            r11.append(r4)     // Catch:{ Exception -> 0x0a21 }
            r11.append(r5)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r4 = r11.toString()     // Catch:{ Exception -> 0x0a21 }
            r11 = 0
            java.lang.String r4 = r8.getString(r4, r11)     // Catch:{ Exception -> 0x0a21 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r11.<init>()     // Catch:{ Exception -> 0x0a21 }
            r31 = r0
            java.lang.String r0 = "color_"
            r11.append(r0)     // Catch:{ Exception -> 0x0a21 }
            r11.append(r5)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r0 = r11.toString()     // Catch:{ Exception -> 0x0a21 }
            boolean r0 = r8.contains(r0)     // Catch:{ Exception -> 0x0a21 }
            if (r0 == 0) goto L_0x0492
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r0.<init>()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r11 = "color_"
            r0.append(r11)     // Catch:{ Exception -> 0x0a21 }
            r0.append(r5)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a21 }
            r11 = 0
            int r0 = r8.getInt(r0, r11)     // Catch:{ Exception -> 0x0a21 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x0a21 }
            r11 = r31
            goto L_0x0495
        L_0x0492:
            r11 = r31
            r0 = 0
        L_0x0495:
            r31 = r7
            goto L_0x049e
        L_0x0498:
            r31 = r7
            r0 = 0
            r4 = 0
            r10 = 3
            r11 = 0
        L_0x049e:
            if (r27 == 0) goto L_0x04fa
            if (r26 == 0) goto L_0x04ce
            java.lang.String r7 = "ChannelSoundPath"
            java.lang.String r7 = r8.getString(r7, r9)     // Catch:{ Exception -> 0x0a21 }
            r34 = r7
            java.lang.String r7 = "vibrate_channel"
            r35 = r12
            r12 = 0
            int r7 = r8.getInt(r7, r12)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r12 = "priority_channel"
            r36 = r7
            r7 = 1
            int r12 = r8.getInt(r12, r7)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r7 = "ChannelLed"
            r37 = r12
            r12 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r7 = r8.getInt(r7, r12)     // Catch:{ Exception -> 0x0a21 }
            r12 = r7
            r7 = r34
            r26 = 2
            goto L_0x0527
        L_0x04ce:
            r35 = r12
            java.lang.String r7 = "GroupSoundPath"
            java.lang.String r7 = r8.getString(r7, r9)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r12 = "vibrate_group"
            r34 = r7
            r7 = 0
            int r12 = r8.getInt(r12, r7)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r7 = "priority_group"
            r36 = r12
            r12 = 1
            int r7 = r8.getInt(r7, r12)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r12 = "GroupLed"
            r37 = r7
            r7 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r7 = r8.getInt(r12, r7)     // Catch:{ Exception -> 0x0a21 }
            r12 = r7
            r7 = r34
            r26 = 0
            goto L_0x0527
        L_0x04fa:
            r35 = r12
            if (r20 == 0) goto L_0x0532
            java.lang.String r7 = "GlobalSoundPath"
            java.lang.String r7 = r8.getString(r7, r9)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r12 = "vibrate_messages"
            r34 = r7
            r7 = 0
            int r12 = r8.getInt(r12, r7)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r7 = "priority_messages"
            r36 = r12
            r12 = 1
            int r7 = r8.getInt(r7, r12)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r12 = "MessagesLed"
            r37 = r7
            r7 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r7 = r8.getInt(r12, r7)     // Catch:{ Exception -> 0x0a21 }
            r12 = r7
            r7 = r34
            r26 = 1
        L_0x0527:
            r34 = r9
            r9 = r36
            r36 = r14
            r14 = r37
            r37 = r1
            goto L_0x0543
        L_0x0532:
            r7 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r37 = r1
            r34 = r9
            r36 = r14
            r7 = 0
            r9 = 0
            r12 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r14 = 0
            r26 = 1
        L_0x0543:
            r1 = 4
            if (r9 != r1) goto L_0x054a
            r9 = 0
            r39 = 1
            goto L_0x054c
        L_0x054a:
            r39 = 0
        L_0x054c:
            if (r4 == 0) goto L_0x0558
            boolean r40 = android.text.TextUtils.equals(r7, r4)     // Catch:{ Exception -> 0x0a21 }
            if (r40 != 0) goto L_0x0558
            r7 = r4
            r1 = 3
            r4 = 0
            goto L_0x055a
        L_0x0558:
            r1 = 3
            r4 = 1
        L_0x055a:
            if (r10 == r1) goto L_0x0560
            if (r14 == r10) goto L_0x0560
            r4 = 0
            goto L_0x0561
        L_0x0560:
            r10 = r14
        L_0x0561:
            if (r0 == 0) goto L_0x056e
            int r1 = r0.intValue()     // Catch:{ Exception -> 0x0a21 }
            if (r1 == r12) goto L_0x056e
            int r12 = r0.intValue()     // Catch:{ Exception -> 0x0a21 }
            r4 = 0
        L_0x056e:
            if (r11 == 0) goto L_0x0578
            r1 = 4
            if (r11 == r1) goto L_0x0578
            if (r11 == r9) goto L_0x0578
            r9 = r11
            r11 = 0
            goto L_0x0579
        L_0x0578:
            r11 = r4
        L_0x0579:
            if (r29 == 0) goto L_0x059d
            java.lang.String r0 = "EnableInAppSounds"
            r1 = 1
            boolean r0 = r8.getBoolean(r0, r1)     // Catch:{ Exception -> 0x0a21 }
            if (r0 != 0) goto L_0x0585
            r7 = 0
        L_0x0585:
            java.lang.String r0 = "EnableInAppVibrate"
            boolean r0 = r8.getBoolean(r0, r1)     // Catch:{ Exception -> 0x0a21 }
            if (r0 != 0) goto L_0x058e
            r9 = 2
        L_0x058e:
            java.lang.String r0 = "EnableInAppPriority"
            r1 = 0
            boolean r0 = r8.getBoolean(r0, r1)     // Catch:{ Exception -> 0x0a21 }
            if (r0 != 0) goto L_0x0599
            r10 = 0
            goto L_0x059d
        L_0x0599:
            r1 = 2
            if (r10 != r1) goto L_0x059d
            r10 = 1
        L_0x059d:
            if (r39 == 0) goto L_0x05b3
            r1 = 2
            if (r9 == r1) goto L_0x05b3
            android.media.AudioManager r0 = audioManager     // Catch:{ Exception -> 0x05af }
            int r0 = r0.getRingerMode()     // Catch:{ Exception -> 0x05af }
            if (r0 == 0) goto L_0x05b3
            r1 = 1
            if (r0 == r1) goto L_0x05b3
            r9 = 2
            goto L_0x05b3
        L_0x05af:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0a21 }
        L_0x05b3:
            if (r13 == 0) goto L_0x05b9
            r7 = 0
            r9 = 0
            r10 = 0
            r12 = 0
        L_0x05b9:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0a21 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a21 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r4 = org.telegram.ui.LaunchActivity.class
            r0.<init>(r1, r4)     // Catch:{ Exception -> 0x0a21 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r1.<init>()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r4 = "com.tmessages.openchat"
            r1.append(r4)     // Catch:{ Exception -> 0x0a21 }
            r14 = r11
            r39 = r12
            double r11 = java.lang.Math.random()     // Catch:{ Exception -> 0x0a21 }
            r1.append(r11)     // Catch:{ Exception -> 0x0a21 }
            r4 = 2147483647(0x7fffffff, float:NaN)
            r1.append(r4)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0a21 }
            r0.setAction(r1)     // Catch:{ Exception -> 0x0a21 }
            r1 = 32768(0x8000, float:4.5918E-41)
            r0.setFlags(r1)     // Catch:{ Exception -> 0x0a21 }
            r11 = 0
            if (r25 == 0) goto L_0x067c
            android.util.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0a21 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0a21 }
            r4 = 1
            if (r1 != r4) goto L_0x060a
            if (r27 == 0) goto L_0x0600
            java.lang.String r1 = "chatId"
            r4 = r27
            r0.putExtra(r1, r4)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x060a
        L_0x0600:
            if (r20 == 0) goto L_0x060a
            java.lang.String r1 = "userId"
            r4 = r20
            r0.putExtra(r1, r4)     // Catch:{ Exception -> 0x0a21 }
        L_0x060a:
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a21 }
            if (r1 != 0) goto L_0x0673
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a21 }
            if (r1 == 0) goto L_0x0616
            goto L_0x0673
        L_0x0616:
            android.util.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0a21 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0a21 }
            r4 = 1
            if (r1 != r4) goto L_0x0668
            r1 = 28
            r4 = r28
            if (r4 >= r1) goto L_0x0661
            if (r3 == 0) goto L_0x0644
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r3.photo     // Catch:{ Exception -> 0x0a21 }
            if (r1 == 0) goto L_0x0661
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0a21 }
            if (r1 == 0) goto L_0x0661
            r25 = r7
            r20 = r8
            long r7 = r1.volume_id     // Catch:{ Exception -> 0x0a21 }
            int r27 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r27 == 0) goto L_0x0665
            int r7 = r1.local_id     // Catch:{ Exception -> 0x0a21 }
            if (r7 == 0) goto L_0x0665
            r7 = r1
            r1 = r24
            r24 = r9
            goto L_0x06a0
        L_0x0644:
            r25 = r7
            r20 = r8
            if (r24 == 0) goto L_0x0665
            r1 = r24
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r1.photo     // Catch:{ Exception -> 0x0a21 }
            if (r7 == 0) goto L_0x0670
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small     // Catch:{ Exception -> 0x0a21 }
            if (r7 == 0) goto L_0x0670
            r24 = r9
            long r8 = r7.volume_id     // Catch:{ Exception -> 0x0a21 }
            int r27 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r27 == 0) goto L_0x069f
            int r8 = r7.local_id     // Catch:{ Exception -> 0x0a21 }
            if (r8 == 0) goto L_0x069f
            goto L_0x06a0
        L_0x0661:
            r25 = r7
            r20 = r8
        L_0x0665:
            r1 = r24
            goto L_0x0670
        L_0x0668:
            r25 = r7
            r20 = r8
            r1 = r24
            r4 = r28
        L_0x0670:
            r24 = r9
            goto L_0x069f
        L_0x0673:
            r25 = r7
            r20 = r8
            r1 = r24
            r4 = r28
            goto L_0x0670
        L_0x067c:
            r25 = r7
            r20 = r8
            r1 = r24
            r4 = r28
            r24 = r9
            android.util.LongSparseArray<java.lang.Integer> r7 = r15.pushDialogs     // Catch:{ Exception -> 0x0a21 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0a21 }
            r8 = 1
            if (r7 != r8) goto L_0x069f
            long r7 = globalSecretChatId     // Catch:{ Exception -> 0x0a21 }
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x069f
            java.lang.String r7 = "encId"
            r8 = 32
            long r8 = r5 >> r8
            int r9 = (int) r8     // Catch:{ Exception -> 0x0a21 }
            r0.putExtra(r7, r9)     // Catch:{ Exception -> 0x0a21 }
        L_0x069f:
            r7 = 0
        L_0x06a0:
            int r8 = r15.currentAccount     // Catch:{ Exception -> 0x0a21 }
            r9 = r19
            r0.putExtra(r9, r8)     // Catch:{ Exception -> 0x0a21 }
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a21 }
            r11 = 1073741824(0x40000000, float:2.0)
            r12 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r8, r12, r0, r11)     // Catch:{ Exception -> 0x0a21 }
            r8 = r37
            r2.setContentTitle(r8)     // Catch:{ Exception -> 0x0a21 }
            r8 = 2131165844(0x7var_, float:1.7945917E38)
            r2.setSmallIcon(r8)     // Catch:{ Exception -> 0x0a21 }
            r8 = 1
            r2.setAutoCancel(r8)     // Catch:{ Exception -> 0x0a21 }
            int r8 = r15.total_unread_count     // Catch:{ Exception -> 0x0a21 }
            r2.setNumber(r8)     // Catch:{ Exception -> 0x0a21 }
            r2.setContentIntent(r0)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r0 = r15.notificationGroup     // Catch:{ Exception -> 0x0a21 }
            r2.setGroup(r0)     // Catch:{ Exception -> 0x0a21 }
            r8 = 1
            r2.setGroupSummary(r8)     // Catch:{ Exception -> 0x0a21 }
            r2.setShowWhen(r8)     // Catch:{ Exception -> 0x0a21 }
            r8 = r23
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner     // Catch:{ Exception -> 0x0a21 }
            int r0 = r0.date     // Catch:{ Exception -> 0x0a21 }
            long r11 = (long) r0     // Catch:{ Exception -> 0x0a21 }
            r32 = 1000(0x3e8, double:4.94E-321)
            long r11 = r11 * r32
            r2.setWhen(r11)     // Catch:{ Exception -> 0x0a21 }
            r0 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r2.setColor(r0)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r0 = "msg"
            r2.setCategory(r0)     // Catch:{ Exception -> 0x0a21 }
            if (r3 != 0) goto L_0x0710
            if (r1 == 0) goto L_0x0710
            java.lang.String r0 = r1.phone     // Catch:{ Exception -> 0x0a21 }
            if (r0 == 0) goto L_0x0710
            int r0 = r0.length()     // Catch:{ Exception -> 0x0a21 }
            if (r0 <= 0) goto L_0x0710
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r0.<init>()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r3 = "tel:+"
            r0.append(r3)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r1 = r1.phone     // Catch:{ Exception -> 0x0a21 }
            r0.append(r1)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a21 }
            r2.addPerson(r0)     // Catch:{ Exception -> 0x0a21 }
        L_0x0710:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0a21 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a21 }
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r3 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r1, r3)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r1 = "messageDate"
            org.telegram.tgnet.TLRPC$Message r3 = r8.messageOwner     // Catch:{ Exception -> 0x0a21 }
            int r3 = r3.date     // Catch:{ Exception -> 0x0a21 }
            r0.putExtra(r1, r3)     // Catch:{ Exception -> 0x0a21 }
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x0a21 }
            r0.putExtra(r9, r1)     // Catch:{ Exception -> 0x0a21 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a21 }
            r3 = 134217728(0x8000000, float:3.85186E-34)
            r11 = 1
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r1, r11, r0, r3)     // Catch:{ Exception -> 0x0a21 }
            r2.setDeleteIntent(r0)     // Catch:{ Exception -> 0x0a21 }
            if (r7 == 0) goto L_0x077f
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r1 = "50_50"
            r11 = 0
            android.graphics.drawable.BitmapDrawable r0 = r0.getImageFromMemory(r7, r11, r1)     // Catch:{ Exception -> 0x0a21 }
            if (r0 == 0) goto L_0x074a
            android.graphics.Bitmap r0 = r0.getBitmap()     // Catch:{ Exception -> 0x0a21 }
            r2.setLargeIcon(r0)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0780
        L_0x074a:
            r1 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r1)     // Catch:{ all -> 0x077d }
            boolean r1 = r0.exists()     // Catch:{ all -> 0x077d }
            if (r1 == 0) goto L_0x0780
            r1 = 1126170624(0x43200000, float:160.0)
            r7 = 1112014848(0x42480000, float:50.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x077d }
            float r7 = (float) r7     // Catch:{ all -> 0x077d }
            float r1 = r1 / r7
            android.graphics.BitmapFactory$Options r7 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x077d }
            r7.<init>()     // Catch:{ all -> 0x077d }
            r12 = 1065353216(0x3var_, float:1.0)
            int r12 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r12 >= 0) goto L_0x076c
            r1 = 1
            goto L_0x076d
        L_0x076c:
            int r1 = (int) r1     // Catch:{ all -> 0x077d }
        L_0x076d:
            r7.inSampleSize = r1     // Catch:{ all -> 0x077d }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x077d }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r7)     // Catch:{ all -> 0x077d }
            if (r0 == 0) goto L_0x0780
            r2.setLargeIcon(r0)     // Catch:{ all -> 0x077d }
            goto L_0x0780
        L_0x077d:
            goto L_0x0780
        L_0x077f:
            r11 = 0
        L_0x0780:
            r0 = 5
            r1 = 26
            r7 = r36
            if (r42 == 0) goto L_0x07b7
            r12 = 1
            if (r7 != r12) goto L_0x078b
            goto L_0x07b7
        L_0x078b:
            if (r10 != 0) goto L_0x0795
            r11 = 0
            r2.setPriority(r11)     // Catch:{ Exception -> 0x0a21 }
            if (r4 < r1) goto L_0x07bf
            r10 = 3
            goto L_0x07c0
        L_0x0795:
            if (r10 == r12) goto L_0x07af
            r11 = 2
            if (r10 != r11) goto L_0x079b
            goto L_0x07af
        L_0x079b:
            r11 = 4
            if (r10 != r11) goto L_0x07a6
            r10 = -2
            r2.setPriority(r10)     // Catch:{ Exception -> 0x0a21 }
            if (r4 < r1) goto L_0x07bf
            r10 = 1
            goto L_0x07c0
        L_0x07a6:
            if (r10 != r0) goto L_0x07bf
            r10 = -1
            r2.setPriority(r10)     // Catch:{ Exception -> 0x0a21 }
            if (r4 < r1) goto L_0x07bf
            goto L_0x07bd
        L_0x07af:
            r10 = 1
            r2.setPriority(r10)     // Catch:{ Exception -> 0x0a21 }
            if (r4 < r1) goto L_0x07bf
            r10 = 4
            goto L_0x07c0
        L_0x07b7:
            r10 = -1
            r2.setPriority(r10)     // Catch:{ Exception -> 0x0a21 }
            if (r4 < r1) goto L_0x07bf
        L_0x07bd:
            r10 = 2
            goto L_0x07c0
        L_0x07bf:
            r10 = 0
        L_0x07c0:
            r11 = 1
            if (r7 == r11) goto L_0x08eb
            if (r13 != 0) goto L_0x08eb
            if (r29 == 0) goto L_0x07d1
            java.lang.String r7 = "EnableInAppPreview"
            r12 = r20
            boolean r7 = r12.getBoolean(r7, r11)     // Catch:{ Exception -> 0x0a21 }
            if (r7 == 0) goto L_0x0806
        L_0x07d1:
            int r7 = r35.length()     // Catch:{ Exception -> 0x0a21 }
            r11 = 100
            if (r7 <= r11) goto L_0x0800
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a21 }
            r7.<init>()     // Catch:{ Exception -> 0x0a21 }
            r11 = 100
            r12 = r35
            r3 = 0
            java.lang.String r11 = r12.substring(r3, r11)     // Catch:{ Exception -> 0x0a21 }
            r3 = 32
            r12 = 10
            java.lang.String r3 = r11.replace(r12, r3)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r3 = r3.trim()     // Catch:{ Exception -> 0x0a21 }
            r7.append(r3)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r3 = "..."
            r7.append(r3)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r3 = r7.toString()     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0803
        L_0x0800:
            r12 = r35
            r3 = r12
        L_0x0803:
            r2.setTicker(r3)     // Catch:{ Exception -> 0x0a21 }
        L_0x0806:
            if (r25 == 0) goto L_0x0881
            java.lang.String r3 = "NoSound"
            r7 = r25
            boolean r3 = r7.equals(r3)     // Catch:{ Exception -> 0x0a21 }
            if (r3 != 0) goto L_0x0881
            if (r4 < r1) goto L_0x0826
            r1 = r34
            boolean r0 = r7.equals(r1)     // Catch:{ Exception -> 0x0a21 }
            if (r0 == 0) goto L_0x081f
            android.net.Uri r7 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0823
        L_0x081f:
            android.net.Uri r7 = android.net.Uri.parse(r7)     // Catch:{ Exception -> 0x0a21 }
        L_0x0823:
            r20 = r13
            goto L_0x0884
        L_0x0826:
            r1 = r34
            boolean r1 = r7.equals(r1)     // Catch:{ Exception -> 0x0a21 }
            if (r1 == 0) goto L_0x0834
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a21 }
            r2.setSound(r1, r0)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0881
        L_0x0834:
            r1 = 24
            if (r4 < r1) goto L_0x0877
            java.lang.String r1 = "file://"
            boolean r1 = r7.startsWith(r1)     // Catch:{ Exception -> 0x0a21 }
            if (r1 == 0) goto L_0x0877
            android.net.Uri r1 = android.net.Uri.parse(r7)     // Catch:{ Exception -> 0x0a21 }
            boolean r1 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r1)     // Catch:{ Exception -> 0x0a21 }
            if (r1 != 0) goto L_0x0877
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x086d }
            java.lang.String r3 = "org.telegram.messenger.beta.provider"
            java.io.File r11 = new java.io.File     // Catch:{ Exception -> 0x086d }
            java.lang.String r12 = "file://"
            r20 = r13
            r13 = r31
            java.lang.String r12 = r7.replace(r12, r13)     // Catch:{ Exception -> 0x086f }
            r11.<init>(r12)     // Catch:{ Exception -> 0x086f }
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r1, r3, r11)     // Catch:{ Exception -> 0x086f }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x086f }
            java.lang.String r11 = "com.android.systemui"
            r12 = 1
            r3.grantUriPermission(r11, r1, r12)     // Catch:{ Exception -> 0x086f }
            r2.setSound(r1, r0)     // Catch:{ Exception -> 0x086f }
            goto L_0x0883
        L_0x086d:
            r20 = r13
        L_0x086f:
            android.net.Uri r1 = android.net.Uri.parse(r7)     // Catch:{ Exception -> 0x0a21 }
            r2.setSound(r1, r0)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0883
        L_0x0877:
            r20 = r13
            android.net.Uri r1 = android.net.Uri.parse(r7)     // Catch:{ Exception -> 0x0a21 }
            r2.setSound(r1, r0)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0883
        L_0x0881:
            r20 = r13
        L_0x0883:
            r7 = 0
        L_0x0884:
            if (r39 == 0) goto L_0x0890
            r0 = 1000(0x3e8, float:1.401E-42)
            r1 = 1000(0x3e8, float:1.401E-42)
            r12 = r39
            r2.setLights(r12, r0, r1)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0892
        L_0x0890:
            r12 = r39
        L_0x0892:
            r0 = r24
            r1 = 2
            if (r0 != r1) goto L_0x08a5
            long[] r0 = new long[r1]     // Catch:{ Exception -> 0x0a21 }
            r1 = 0
            r23 = 0
            r0[r1] = r23     // Catch:{ Exception -> 0x0a21 }
            r1 = 1
            r0[r1] = r23     // Catch:{ Exception -> 0x0a21 }
            r2.setVibrate(r0)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x08e6
        L_0x08a5:
            r1 = 1
            if (r0 != r1) goto L_0x08c0
            r3 = 4
            long[] r0 = new long[r3]     // Catch:{ Exception -> 0x0a21 }
            r3 = 0
            r23 = 0
            r0[r3] = r23     // Catch:{ Exception -> 0x0a21 }
            r27 = 100
            r0[r1] = r27     // Catch:{ Exception -> 0x0a21 }
            r1 = 2
            r0[r1] = r23     // Catch:{ Exception -> 0x0a21 }
            r23 = 100
            r1 = 3
            r0[r1] = r23     // Catch:{ Exception -> 0x0a21 }
            r2.setVibrate(r0)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x08e6
        L_0x08c0:
            if (r0 == 0) goto L_0x08df
            r1 = 4
            if (r0 != r1) goto L_0x08c6
            goto L_0x08df
        L_0x08c6:
            r1 = 3
            if (r0 != r1) goto L_0x08da
            r1 = 2
            long[] r0 = new long[r1]     // Catch:{ Exception -> 0x0a21 }
            r1 = 0
            r16 = 0
            r0[r1] = r16     // Catch:{ Exception -> 0x0a21 }
            r1 = 1
            r23 = 1000(0x3e8, double:4.94E-321)
            r0[r1] = r23     // Catch:{ Exception -> 0x0a21 }
            r2.setVibrate(r0)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x08e6
        L_0x08da:
            r16 = r7
            r1 = 1
            r7 = 0
            goto L_0x0900
        L_0x08df:
            r1 = 2
            r2.setDefaults(r1)     // Catch:{ Exception -> 0x0a21 }
            r1 = 0
            long[] r0 = new long[r1]     // Catch:{ Exception -> 0x0a21 }
        L_0x08e6:
            r16 = r7
            r1 = 1
            r7 = r0
            goto L_0x0900
        L_0x08eb:
            r20 = r13
            r12 = r39
            r1 = 2
            long[] r0 = new long[r1]     // Catch:{ Exception -> 0x0a21 }
            r1 = 0
            r23 = 0
            r0[r1] = r23     // Catch:{ Exception -> 0x0a21 }
            r1 = 1
            r0[r1] = r23     // Catch:{ Exception -> 0x0a21 }
            r2.setVibrate(r0)     // Catch:{ Exception -> 0x0a21 }
            r7 = r0
            r16 = 0
        L_0x0900:
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a21 }
            if (r0 != 0) goto L_0x09ad
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a21 }
            if (r0 != 0) goto L_0x09ad
            long r23 = r8.getDialogId()     // Catch:{ Exception -> 0x0a21 }
            r27 = 777000(0xbdb28, double:3.83889E-318)
            int r0 = (r23 > r27 ? 1 : (r23 == r27 ? 0 : -1))
            if (r0 != 0) goto L_0x09ad
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup     // Catch:{ Exception -> 0x0a21 }
            if (r0 == 0) goto L_0x09ad
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows     // Catch:{ Exception -> 0x0a21 }
            int r3 = r0.size()     // Catch:{ Exception -> 0x0a21 }
            r11 = 0
            r13 = 0
        L_0x0923:
            if (r13 >= r3) goto L_0x09a9
            java.lang.Object r18 = r0.get(r13)     // Catch:{ Exception -> 0x0a21 }
            r1 = r18
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r1 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r1     // Catch:{ Exception -> 0x0a21 }
            r18 = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r0 = r1.buttons     // Catch:{ Exception -> 0x0a21 }
            int r0 = r0.size()     // Catch:{ Exception -> 0x0a21 }
            r21 = r3
            r3 = 0
        L_0x0938:
            if (r3 >= r0) goto L_0x0999
            r23 = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r0 = r1.buttons     // Catch:{ Exception -> 0x0a21 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x0a21 }
            org.telegram.tgnet.TLRPC$KeyboardButton r0 = (org.telegram.tgnet.TLRPC$KeyboardButton) r0     // Catch:{ Exception -> 0x0a21 }
            r24 = r1
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0a21 }
            if (r1 == 0) goto L_0x0989
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0a21 }
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a21 }
            r25 = r14
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r14 = org.telegram.messenger.NotificationCallbackReceiver.class
            r1.<init>(r11, r14)     // Catch:{ Exception -> 0x0a21 }
            int r11 = r15.currentAccount     // Catch:{ Exception -> 0x0a21 }
            r1.putExtra(r9, r11)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r11 = "did"
            r1.putExtra(r11, r5)     // Catch:{ Exception -> 0x0a21 }
            byte[] r11 = r0.data     // Catch:{ Exception -> 0x0a21 }
            if (r11 == 0) goto L_0x0968
            java.lang.String r14 = "data"
            r1.putExtra(r14, r11)     // Catch:{ Exception -> 0x0a21 }
        L_0x0968:
            java.lang.String r11 = "mid"
            int r14 = r8.getId()     // Catch:{ Exception -> 0x0a21 }
            r1.putExtra(r11, r14)     // Catch:{ Exception -> 0x0a21 }
            java.lang.String r0 = r0.text     // Catch:{ Exception -> 0x0a21 }
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a21 }
            int r14 = r15.lastButtonId     // Catch:{ Exception -> 0x0a21 }
            r27 = r8
            int r8 = r14 + 1
            r15.lastButtonId = r8     // Catch:{ Exception -> 0x0a21 }
            r8 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r11, r14, r1, r8)     // Catch:{ Exception -> 0x0a21 }
            r8 = 0
            r2.addAction(r8, r0, r1)     // Catch:{ Exception -> 0x0a21 }
            r11 = 1
            goto L_0x098e
        L_0x0989:
            r27 = r8
            r25 = r14
            r8 = 0
        L_0x098e:
            int r3 = r3 + 1
            r0 = r23
            r1 = r24
            r14 = r25
            r8 = r27
            goto L_0x0938
        L_0x0999:
            r27 = r8
            r25 = r14
            r8 = 0
            int r13 = r13 + 1
            r0 = r18
            r3 = r21
            r8 = r27
            r1 = 1
            goto L_0x0923
        L_0x09a9:
            r25 = r14
            r3 = r11
            goto L_0x09b1
        L_0x09ad:
            r25 = r14
            r8 = 0
            r3 = 0
        L_0x09b1:
            if (r3 != 0) goto L_0x0a08
            r0 = 24
            if (r4 >= r0) goto L_0x0a08
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0a21 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x0a21 }
            if (r0 != 0) goto L_0x0a08
            boolean r0 = r41.hasMessagesToReply()     // Catch:{ Exception -> 0x0a21 }
            if (r0 == 0) goto L_0x0a08
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0a21 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a21 }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r3 = org.telegram.messenger.PopupReplyReceiver.class
            r0.<init>(r1, r3)     // Catch:{ Exception -> 0x0a21 }
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x0a21 }
            r0.putExtra(r9, r1)     // Catch:{ Exception -> 0x0a21 }
            r1 = 19
            if (r4 > r1) goto L_0x09f0
            r1 = 2131165477(0x7var_, float:1.7945172E38)
            java.lang.String r3 = "Reply"
            r4 = 2131627002(0x7f0e0bfa, float:1.8881256E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0a21 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a21 }
            r8 = 134217728(0x8000000, float:3.85186E-34)
            r9 = 2
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r4, r9, r0, r8)     // Catch:{ Exception -> 0x0a21 }
            r2.addAction(r1, r3, r0)     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0a08
        L_0x09f0:
            r1 = 2131165476(0x7var_, float:1.794517E38)
            java.lang.String r3 = "Reply"
            r4 = 2131627002(0x7f0e0bfa, float:1.8881256E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0a21 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a21 }
            r8 = 134217728(0x8000000, float:3.85186E-34)
            r9 = 2
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r4, r9, r0, r8)     // Catch:{ Exception -> 0x0a21 }
            r2.addAction(r1, r3, r0)     // Catch:{ Exception -> 0x0a21 }
        L_0x0a08:
            r1 = r41
            r3 = r38
            r4 = r5
            r6 = r22
            r8 = r12
            r9 = r16
            r11 = r25
            r12 = r29
            r13 = r20
            r14 = r26
            r1.showExtraNotifications(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0a21 }
            r41.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0a21 }
            goto L_0x0a25
        L_0x0a21:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0a25:
            return
        L_0x0a26:
            r41.dismissNotification()
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
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0b37: MOVE  (r1v40 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>) = 
          (r59v1 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>)
        
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
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0337  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x036b  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x03bd  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x03cb A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x03e0  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0460  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0481  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x049a  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04ad  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x04d1  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x04db A[SYNTHETIC, Splitter:B:191:0x04db] */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x052b  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0531  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x053a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x054f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0571  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x057a  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x058b  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x05c4  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0601  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x06e3  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x06e9  */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x075b  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x0847  */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x0868  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0895  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x08a4 A[SYNTHETIC, Splitter:B:386:0x08a4] */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x094e  */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0960  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0980  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x09da  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0a0d  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0a2e  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0a50  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0b03  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0b08  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0b18  */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x0b1e  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0b22  */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x0b27  */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0b42  */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0bcb A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0bfc  */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0cb0 A[Catch:{ JSONException -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0cd9 A[Catch:{ JSONException -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0ce3  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0cec A[Catch:{ JSONException -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01f4  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0204  */
    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r94, java.lang.String r95, long r96, java.lang.String r98, long[] r99, int r100, android.net.Uri r101, int r102, boolean r103, boolean r104, boolean r105, int r106) {
        /*
            r93 = this;
            r15 = r93
            r14 = r94
            int r13 = android.os.Build.VERSION.SDK_INT
            r12 = 26
            if (r13 < r12) goto L_0x0027
            r1 = r93
            r2 = r96
            r4 = r98
            r5 = r99
            r6 = r100
            r7 = r101
            r8 = r102
            r9 = r103
            r10 = r104
            r11 = r105
            r12 = r106
            java.lang.String r0 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r14.setChannelId(r0)
        L_0x0027:
            android.app.Notification r12 = r94.build()
            r0 = 18
            if (r13 >= r0) goto L_0x0040
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
            org.telegram.messenger.AccountInstance r0 = r93.getAccountInstance()
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
            r4 = 27
            r2 = 1
            if (r13 <= r4) goto L_0x00cd
            int r0 = r11.size()
            if (r0 <= r2) goto L_0x00cb
            goto L_0x00cd
        L_0x00cb:
            r3 = 0
            goto L_0x00ce
        L_0x00cd:
            r3 = 1
        L_0x00ce:
            r1 = 26
            if (r3 == 0) goto L_0x00d7
            if (r13 < r1) goto L_0x00d7
            checkOtherNotificationsChannel()
        L_0x00d7:
            org.telegram.messenger.UserConfig r0 = r93.getUserConfig()
            int r1 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x00ed
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x00ea
            goto L_0x00ed
        L_0x00ea:
            r21 = 0
            goto L_0x00ef
        L_0x00ed:
            r21 = 1
        L_0x00ef:
            android.util.LongSparseArray r6 = new android.util.LongSparseArray
            r6.<init>()
            int r2 = r11.size()
        L_0x00f8:
            java.lang.String r4 = "id"
            if (r10 >= r2) goto L_0x0d1d
            int r0 = r7.size()
            r23 = r4
            r4 = 7
            if (r0 < r4) goto L_0x0118
            r72 = r1
            r29 = r3
            r76 = r6
            r14 = r7
            r28 = r8
            r82 = r12
            r84 = r13
            r13 = r23
            r24 = 0
            goto L_0x0d2d
        L_0x0118:
            java.lang.Object r0 = r11.get(r10)
            java.lang.Long r0 = (java.lang.Long) r0
            r24 = r10
            r22 = r11
            long r10 = r0.longValue()
            java.lang.Object r0 = r9.get(r10)
            r4 = r0
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            r26 = r2
            r2 = 0
            java.lang.Object r0 = r4.get(r2)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r2 = r0.getId()
            r27 = r9
            int r9 = (int) r10
            r28 = r7
            r7 = 32
            long r14 = r10 >> r7
            int r15 = (int) r14
            java.lang.Object r0 = r8.get(r10)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0158
            if (r9 == 0) goto L_0x0153
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)
            goto L_0x015b
        L_0x0153:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r15)
            goto L_0x015b
        L_0x0158:
            r8.remove(r10)
        L_0x015b:
            r14 = r0
            if (r5 == 0) goto L_0x0164
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            goto L_0x0165
        L_0x0164:
            r0 = 0
        L_0x0165:
            r7 = 0
            java.lang.Object r30 = r4.get(r7)
            r7 = r30
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            r30 = r0
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            r31 = r8
            int r8 = r0.date
            r32 = 0
            if (r9 == 0) goto L_0x029b
            r0 = 777000(0xbdb28, float:1.088809E-39)
            if (r9 == r0) goto L_0x0181
            r0 = 1
            goto L_0x0182
        L_0x0181:
            r0 = 0
        L_0x0182:
            if (r9 <= 0) goto L_0x021a
            r34 = r0
            org.telegram.messenger.MessagesController r0 = r93.getMessagesController()
            r35 = r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r5)
            if (r0 != 0) goto L_0x01d1
            boolean r5 = r7.isFcmMessage()
            if (r5 == 0) goto L_0x01a3
            java.lang.String r5 = r7.localName
        L_0x019e:
            r37 = r5
            r36 = r6
            goto L_0x01ec
        L_0x01a3:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x01bb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found user to show dialog notification "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x01bb:
            r15 = r93
            r72 = r1
            r29 = r3
            r76 = r6
            r82 = r12
            r84 = r13
            r30 = r24
            r14 = r28
            r28 = r31
            r12 = r35
            goto L_0x02df
        L_0x01d1:
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r0)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            if (r7 == 0) goto L_0x019e
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x019e
            r37 = r5
            r36 = r6
            long r5 = r7.volume_id
            int r38 = (r5 > r32 ? 1 : (r5 == r32 ? 0 : -1))
            if (r38 == 0) goto L_0x01ec
            int r5 = r7.local_id
            if (r5 == 0) goto L_0x01ec
            goto L_0x01ed
        L_0x01ec:
            r7 = 0
        L_0x01ed:
            long r5 = (long) r9
            boolean r5 = org.telegram.messenger.UserObject.isReplyUser((long) r5)
            if (r5 == 0) goto L_0x0204
            r5 = 2131626995(0x7f0e0bf3, float:1.8881242E38)
            java.lang.String r6 = "RepliesTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
        L_0x01fd:
            r6 = r0
            r39 = r5
            r0 = r7
            r7 = r30
            goto L_0x0216
        L_0x0204:
            if (r9 != r1) goto L_0x0210
            r5 = 2131626021(0x7f0e0825, float:1.8879266E38)
            java.lang.String r6 = "MessageScheduledReminderNotification"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            goto L_0x01fd
        L_0x0210:
            r6 = r0
            r0 = r7
            r7 = r30
            r39 = r37
        L_0x0216:
            r30 = 0
            goto L_0x032b
        L_0x021a:
            r34 = r0
            r35 = r5
            r36 = r6
            org.telegram.messenger.MessagesController r0 = r93.getMessagesController()
            int r5 = -r9
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r5)
            if (r0 != 0) goto L_0x0265
            boolean r5 = r7.isFcmMessage()
            if (r5 == 0) goto L_0x024b
            boolean r5 = r7.isSupergroup()
            java.lang.String r6 = r7.localName
            boolean r7 = r7.localChannel
            r37 = r5
            r39 = r6
            r38 = r7
        L_0x0243:
            r7 = r30
            r6 = 0
            r30 = r0
            r0 = 0
            goto L_0x032f
        L_0x024b:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02cb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found chat to show dialog notification "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02cb
        L_0x0265:
            boolean r5 = r0.megagroup
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r6 == 0) goto L_0x0273
            boolean r6 = r0.megagroup
            if (r6 != 0) goto L_0x0273
            r6 = 1
            goto L_0x0274
        L_0x0273:
            r6 = 0
        L_0x0274:
            java.lang.String r7 = r0.title
            r37 = r5
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r0.photo
            if (r5 == 0) goto L_0x0296
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small
            if (r5 == 0) goto L_0x0296
            r38 = r6
            r39 = r7
            long r6 = r5.volume_id
            int r40 = (r6 > r32 ? 1 : (r6 == r32 ? 0 : -1))
            if (r40 == 0) goto L_0x0243
            int r6 = r5.local_id
            if (r6 == 0) goto L_0x0243
            r7 = r30
            r6 = 0
            r30 = r0
            r0 = r5
            goto L_0x032f
        L_0x0296:
            r38 = r6
            r39 = r7
            goto L_0x0243
        L_0x029b:
            r35 = r5
            r36 = r6
            long r5 = globalSecretChatId
            int r0 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x0318
            org.telegram.messenger.MessagesController r0 = r93.getMessagesController()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r15)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r5)
            if (r0 != 0) goto L_0x02ed
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02cb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found secret chat to show dialog notification "
            r0.append(r2)
            r0.append(r15)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x02cb:
            r15 = r93
            r72 = r1
            r29 = r3
            r82 = r12
            r84 = r13
            r30 = r24
            r14 = r28
            r28 = r31
            r12 = r35
            r76 = r36
        L_0x02df:
            r20 = 26
            r23 = 7
            r24 = 0
            r25 = 27
            r33 = 1
            r34 = 0
            goto L_0x0d03
        L_0x02ed:
            org.telegram.messenger.MessagesController r5 = r93.getMessagesController()
            int r6 = r0.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            if (r5 != 0) goto L_0x0319
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x02cb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "not found secret chat user to show dialog notification "
            r2.append(r4)
            int r0 = r0.user_id
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02cb
        L_0x0318:
            r5 = 0
        L_0x0319:
            r0 = 2131627151(0x7f0e0c8f, float:1.8881558E38)
            java.lang.String r6 = "SecretChatName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
            r39 = r0
            r6 = r5
            r0 = 0
            r7 = 0
            r30 = 0
            r34 = 0
        L_0x032b:
            r37 = 0
            r38 = 0
        L_0x032f:
            java.lang.String r5 = "NotificationHiddenChatName"
            r41 = r12
            java.lang.String r12 = "NotificationHiddenName"
            if (r21 == 0) goto L_0x0356
            if (r9 >= 0) goto L_0x0343
            r43 = r6
            r6 = 2131626280(0x7f0e0928, float:1.8879792E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r6)
            goto L_0x034c
        L_0x0343:
            r43 = r6
            r6 = 2131626283(0x7f0e092b, float:1.8879798E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r6)
        L_0x034c:
            r34 = r3
            r39 = r8
            r44 = r15
            r3 = 0
            r6 = 0
            r8 = r0
            goto L_0x0367
        L_0x0356:
            r43 = r6
            r6 = r0
            r44 = r15
            r91 = r34
            r34 = r3
            r3 = r91
            r92 = r39
            r39 = r8
            r8 = r92
        L_0x0367:
            r15 = 28
            if (r6 == 0) goto L_0x03bd
            r45 = r12
            r12 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r12)
            if (r13 >= r15) goto L_0x03b6
            org.telegram.messenger.ImageLoader r12 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r15 = "50_50"
            r46 = r5
            r5 = 0
            android.graphics.drawable.BitmapDrawable r12 = r12.getImageFromMemory(r6, r5, r15)
            if (r12 == 0) goto L_0x038b
            android.graphics.Bitmap r12 = r12.getBitmap()
        L_0x0387:
            r17 = r0
            r15 = r12
            goto L_0x03c5
        L_0x038b:
            boolean r12 = r0.exists()     // Catch:{ all -> 0x03b9 }
            if (r12 == 0) goto L_0x03b4
            r12 = 1126170624(0x43200000, float:160.0)
            r15 = 1112014848(0x42480000, float:50.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)     // Catch:{ all -> 0x03b9 }
            float r15 = (float) r15     // Catch:{ all -> 0x03b9 }
            float r12 = r12 / r15
            android.graphics.BitmapFactory$Options r15 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x03b9 }
            r15.<init>()     // Catch:{ all -> 0x03b9 }
            r17 = 1065353216(0x3var_, float:1.0)
            int r17 = (r12 > r17 ? 1 : (r12 == r17 ? 0 : -1))
            if (r17 >= 0) goto L_0x03a8
            r12 = 1
            goto L_0x03a9
        L_0x03a8:
            int r12 = (int) r12     // Catch:{ all -> 0x03b9 }
        L_0x03a9:
            r15.inSampleSize = r12     // Catch:{ all -> 0x03b9 }
            java.lang.String r12 = r0.getAbsolutePath()     // Catch:{ all -> 0x03b9 }
            android.graphics.Bitmap r12 = android.graphics.BitmapFactory.decodeFile(r12, r15)     // Catch:{ all -> 0x03b9 }
            goto L_0x0387
        L_0x03b4:
            r12 = r5
            goto L_0x0387
        L_0x03b6:
            r46 = r5
            r5 = 0
        L_0x03b9:
            r17 = r0
            r15 = r5
            goto L_0x03c5
        L_0x03bd:
            r46 = r5
            r45 = r12
            r5 = 0
            r15 = r5
            r17 = r15
        L_0x03c5:
            java.lang.String r12 = "max_id"
            java.lang.String r5 = "currentAccount"
            if (r38 == 0) goto L_0x03cd
            if (r37 == 0) goto L_0x0469
        L_0x03cd:
            if (r3 == 0) goto L_0x0469
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0469
            if (r1 == r9) goto L_0x0469
            r48 = r6
            r47 = r7
            long r6 = (long) r9
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r0 != 0) goto L_0x0460
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r7 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r6, r7)
            java.lang.String r6 = "dialog_id"
            r0.putExtra(r6, r10)
            r0.putExtra(r12, r2)
            r7 = r93
            int r6 = r7.currentAccount
            r0.putExtra(r5, r6)
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            r49 = r3
            int r3 = r14.intValue()
            r50 = r15
            r15 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r6, r3, r0, r15)
            androidx.core.app.RemoteInput$Builder r3 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r6 = "extra_voice_reply"
            r3.<init>(r6)
            r6 = 2131627002(0x7f0e0bfa, float:1.8881256E38)
            java.lang.String r15 = "Reply"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r15, r6)
            r3.setLabel(r6)
            androidx.core.app.RemoteInput r3 = r3.build()
            if (r9 >= 0) goto L_0x0433
            r15 = 1
            java.lang.Object[] r6 = new java.lang.Object[r15]
            r15 = 0
            r6[r15] = r8
            java.lang.String r15 = "ReplyToGroup"
            r51 = r14
            r14 = 2131627003(0x7f0e0bfb, float:1.8881258E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r15, r14, r6)
            goto L_0x0444
        L_0x0433:
            r51 = r14
            r6 = 2131627004(0x7f0e0bfc, float:1.888126E38)
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            r14 = 0
            r15[r14] = r8
            java.lang.String r14 = "ReplyToUser"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r14, r6, r15)
        L_0x0444:
            androidx.core.app.NotificationCompat$Action$Builder r14 = new androidx.core.app.NotificationCompat$Action$Builder
            r15 = 2131165521(0x7var_, float:1.7945261E38)
            r14.<init>(r15, r6, r0)
            r6 = 1
            r14.setAllowGeneratedReplies(r6)
            r14.setSemanticAction(r6)
            r14.addRemoteInput(r3)
            r3 = 0
            r14.setShowsUserInterface(r3)
            androidx.core.app.NotificationCompat$Action r0 = r14.build()
            r3 = r0
            goto L_0x0476
        L_0x0460:
            r7 = r93
            r49 = r3
            r51 = r14
            r50 = r15
            goto L_0x0475
        L_0x0469:
            r49 = r3
            r48 = r6
            r47 = r7
            r51 = r14
            r50 = r15
            r7 = r93
        L_0x0475:
            r3 = 0
        L_0x0476:
            android.util.LongSparseArray<java.lang.Integer> r0 = r7.pushDialogs
            java.lang.Object r0 = r0.get(r10)
            java.lang.Integer r0 = (java.lang.Integer) r0
            r6 = 0
            if (r0 != 0) goto L_0x0485
            java.lang.Integer r0 = java.lang.Integer.valueOf(r6)
        L_0x0485:
            int r0 = r0.intValue()
            int r14 = r4.size()
            int r0 = java.lang.Math.max(r0, r14)
            r14 = 2
            r15 = 1
            if (r0 <= r15) goto L_0x04ad
            r15 = 28
            if (r13 < r15) goto L_0x049a
            goto L_0x04ad
        L_0x049a:
            java.lang.Object[] r15 = new java.lang.Object[r14]
            r15[r6] = r8
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r6 = 1
            r15[r6] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r15)
            r6 = r0
            goto L_0x04ae
        L_0x04ad:
            r6 = r8
        L_0x04ae:
            long r14 = (long) r1
            r52 = r2
            r2 = r36
            java.lang.Object r0 = r2.get(r14)
            r36 = r0
            androidx.core.app.Person r36 = (androidx.core.app.Person) r36
            r53 = r12
            r12 = 28
            if (r13 < r12) goto L_0x0523
            if (r36 != 0) goto L_0x0523
            org.telegram.messenger.MessagesController r0 = r93.getMessagesController()
            java.lang.Integer r12 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r12)
            if (r0 != 0) goto L_0x04d9
            org.telegram.messenger.UserConfig r0 = r93.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x04d9:
            if (r0 == 0) goto L_0x0523
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo     // Catch:{ all -> 0x051c }
            if (r12 == 0) goto L_0x0523
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ all -> 0x051c }
            if (r12 == 0) goto L_0x0523
            r54 = r10
            long r10 = r12.volume_id     // Catch:{ all -> 0x051a }
            int r56 = (r10 > r32 ? 1 : (r10 == r32 ? 0 : -1))
            if (r56 == 0) goto L_0x0525
            int r10 = r12.local_id     // Catch:{ all -> 0x051a }
            if (r10 == 0) goto L_0x0525
            androidx.core.app.Person$Builder r10 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x051a }
            r10.<init>()     // Catch:{ all -> 0x051a }
            java.lang.String r11 = "FromYou"
            r12 = 2131625582(0x7f0e066e, float:1.8878376E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11, r12)     // Catch:{ all -> 0x051a }
            r10.setName(r11)     // Catch:{ all -> 0x051a }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x051a }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x051a }
            r11 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r11)     // Catch:{ all -> 0x051a }
            r7.loadRoundAvatar(r0, r10)     // Catch:{ all -> 0x051a }
            androidx.core.app.Person r10 = r10.build()     // Catch:{ all -> 0x051a }
            r2.put(r14, r10)     // Catch:{ all -> 0x0516 }
            r36 = r10
            goto L_0x0525
        L_0x0516:
            r0 = move-exception
            r36 = r10
            goto L_0x051f
        L_0x051a:
            r0 = move-exception
            goto L_0x051f
        L_0x051c:
            r0 = move-exception
            r54 = r10
        L_0x051f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0525
        L_0x0523:
            r54 = r10
        L_0x0525:
            r0 = r36
            java.lang.String r10 = ""
            if (r0 == 0) goto L_0x0531
            androidx.core.app.NotificationCompat$MessagingStyle r11 = new androidx.core.app.NotificationCompat$MessagingStyle
            r11.<init>((androidx.core.app.Person) r0)
            goto L_0x0536
        L_0x0531:
            androidx.core.app.NotificationCompat$MessagingStyle r11 = new androidx.core.app.NotificationCompat$MessagingStyle
            r11.<init>((java.lang.CharSequence) r10)
        L_0x0536:
            r12 = 28
            if (r13 < r12) goto L_0x0547
            if (r9 >= 0) goto L_0x053e
            if (r38 == 0) goto L_0x0547
        L_0x053e:
            r12 = r1
            long r0 = (long) r9
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r0)
            if (r0 == 0) goto L_0x054b
            goto L_0x0548
        L_0x0547:
            r12 = r1
        L_0x0548:
            r11.setConversationTitle(r6)
        L_0x054b:
            r1 = 28
            if (r13 < r1) goto L_0x055d
            if (r38 != 0) goto L_0x0553
            if (r9 < 0) goto L_0x055d
        L_0x0553:
            long r0 = (long) r9
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r0)
            if (r0 == 0) goto L_0x055b
            goto L_0x055d
        L_0x055b:
            r0 = 0
            goto L_0x055e
        L_0x055d:
            r0 = 1
        L_0x055e:
            r11.setGroupConversation(r0)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r36 = r12
            r6 = 1
            java.lang.String[] r12 = new java.lang.String[r6]
            r56 = r3
            boolean[] r3 = new boolean[r6]
            if (r47 == 0) goto L_0x057a
            org.json.JSONArray r0 = new org.json.JSONArray
            r0.<init>()
            r57 = r5
            r5 = r0
            goto L_0x057d
        L_0x057a:
            r57 = r5
            r5 = 0
        L_0x057d:
            int r0 = r4.size()
            int r0 = r0 - r6
            r6 = r0
            r58 = 0
            r59 = 0
        L_0x0587:
            r60 = 1000(0x3e8, double:4.94E-321)
            if (r6 < 0) goto L_0x0912
            java.lang.Object r0 = r4.get(r6)
            r62 = r4
            r4 = r0
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            java.lang.String r0 = r7.getShortStringForMessage(r4, r12, r3)
            int r63 = (r54 > r14 ? 1 : (r54 == r14 ? 0 : -1))
            if (r63 != 0) goto L_0x05a1
            r19 = 0
            r12[r19] = r8
            goto L_0x05be
        L_0x05a1:
            r19 = 0
            if (r9 >= 0) goto L_0x05be
            r63 = r8
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner
            boolean r8 = r8.from_scheduled
            if (r8 == 0) goto L_0x05bb
            r8 = 2131626326(0x7f0e0956, float:1.8879885E38)
            r64 = r6
            java.lang.String r6 = "NotificationMessageScheduledName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r8)
            r12[r19] = r6
            goto L_0x05c2
        L_0x05bb:
            r64 = r6
            goto L_0x05c2
        L_0x05be:
            r64 = r6
            r63 = r8
        L_0x05c2:
            if (r0 != 0) goto L_0x0601
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05f3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "message text is null for "
            r0.append(r6)
            int r6 = r4.getId()
            r0.append(r6)
            java.lang.String r6 = " did = "
            r0.append(r6)
            r6 = r5
            long r4 = r4.getDialogId()
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            r40 = r1
            r69 = r2
            r5 = r6
            goto L_0x05f7
        L_0x05f3:
            r40 = r1
            r69 = r2
        L_0x05f7:
            r68 = r12
            r65 = r14
            r42 = r45
            r67 = r46
            goto L_0x08fe
        L_0x0601:
            r6 = r5
            int r5 = r1.length()
            if (r5 <= 0) goto L_0x060d
            java.lang.String r5 = "\n\n"
            r1.append(r5)
        L_0x060d:
            int r5 = (r54 > r14 ? 1 : (r54 == r14 ? 0 : -1))
            if (r5 == 0) goto L_0x0637
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            boolean r5 = r5.from_scheduled
            if (r5 == 0) goto L_0x0637
            if (r9 <= 0) goto L_0x0637
            r5 = 2
            java.lang.Object[] r8 = new java.lang.Object[r5]
            r5 = 2131626326(0x7f0e0956, float:1.8879885E38)
            r65 = r14
            java.lang.String r14 = "NotificationMessageScheduledName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r14, r5)
            r14 = 0
            r8[r14] = r5
            r5 = 1
            r8[r5] = r0
            java.lang.String r0 = "%1$s: %2$s"
            java.lang.String r0 = java.lang.String.format(r0, r8)
            r1.append(r0)
            goto L_0x0655
        L_0x0637:
            r65 = r14
            r14 = 0
            r5 = r12[r14]
            if (r5 == 0) goto L_0x0652
            r5 = 2
            java.lang.Object[] r8 = new java.lang.Object[r5]
            r5 = r12[r14]
            r8[r14] = r5
            r5 = 1
            r8[r5] = r0
            java.lang.String r5 = "%1$s: %2$s"
            java.lang.String r5 = java.lang.String.format(r5, r8)
            r1.append(r5)
            goto L_0x0655
        L_0x0652:
            r1.append(r0)
        L_0x0655:
            r5 = r0
            if (r9 <= 0) goto L_0x065a
            long r14 = (long) r9
            goto L_0x0668
        L_0x065a:
            if (r38 == 0) goto L_0x065f
            int r0 = -r9
        L_0x065d:
            long r14 = (long) r0
            goto L_0x0668
        L_0x065f:
            if (r9 >= 0) goto L_0x0666
            int r0 = r4.getSenderId()
            goto L_0x065d
        L_0x0666:
            r14 = r54
        L_0x0668:
            java.lang.Object r0 = r2.get(r14)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r8 = 0
            r67 = r12[r8]
            if (r67 != 0) goto L_0x06c5
            if (r21 == 0) goto L_0x06ba
            if (r9 >= 0) goto L_0x06a5
            if (r38 == 0) goto L_0x0693
            r8 = 27
            r40 = r1
            if (r13 <= r8) goto L_0x068f
            r1 = r46
            r8 = 2131626280(0x7f0e0928, float:1.8879792E38)
            java.lang.String r46 = org.telegram.messenger.LocaleController.getString(r1, r8)
            r67 = r1
            r8 = r45
            r1 = r46
            goto L_0x06d4
        L_0x068f:
            r8 = 2131626280(0x7f0e0928, float:1.8879792E38)
            goto L_0x06bc
        L_0x0693:
            r40 = r1
            r1 = r46
            r8 = 2131626281(0x7f0e0929, float:1.8879794E38)
            r67 = r1
            java.lang.String r1 = "NotificationHiddenChatUserName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r8)
            r8 = r45
            goto L_0x06d4
        L_0x06a5:
            r40 = r1
            r67 = r46
            r8 = 27
            if (r13 <= r8) goto L_0x06b7
            r8 = r45
            r1 = 2131626283(0x7f0e092b, float:1.8879798E38)
            java.lang.String r42 = org.telegram.messenger.LocaleController.getString(r8, r1)
            goto L_0x06d2
        L_0x06b7:
            r8 = r45
            goto L_0x06c0
        L_0x06ba:
            r40 = r1
        L_0x06bc:
            r8 = r45
            r67 = r46
        L_0x06c0:
            r1 = 2131626283(0x7f0e092b, float:1.8879798E38)
            r1 = r10
            goto L_0x06d4
        L_0x06c5:
            r40 = r1
            r8 = r45
            r67 = r46
            r1 = 2131626283(0x7f0e092b, float:1.8879798E38)
            r19 = 0
            r42 = r12[r19]
        L_0x06d2:
            r1 = r42
        L_0x06d4:
            r42 = r8
            if (r0 == 0) goto L_0x06e9
            java.lang.CharSequence r8 = r0.getName()
            boolean r8 = android.text.TextUtils.equals(r8, r1)
            if (r8 != 0) goto L_0x06e3
            goto L_0x06e9
        L_0x06e3:
            r1 = r0
            r8 = r11
            r68 = r12
            goto L_0x0759
        L_0x06e9:
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            r0.setName(r1)
            r1 = 0
            boolean r8 = r3[r1]
            if (r8 == 0) goto L_0x074e
            if (r9 == 0) goto L_0x074e
            r1 = 28
            if (r13 < r1) goto L_0x074e
            if (r9 > 0) goto L_0x0745
            if (r38 == 0) goto L_0x0701
            goto L_0x0745
        L_0x0701:
            int r1 = r4.getSenderId()
            org.telegram.messenger.MessagesController r8 = r93.getMessagesController()
            r68 = r12
            java.lang.Integer r12 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r12)
            if (r8 != 0) goto L_0x0727
            org.telegram.messenger.MessagesStorage r8 = r93.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r8 = r8.getUserSync(r1)
            if (r8 == 0) goto L_0x0727
            org.telegram.messenger.MessagesController r1 = r93.getMessagesController()
            r12 = 1
            r1.putUser(r8, r12)
        L_0x0727:
            if (r8 == 0) goto L_0x0742
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r8.photo
            if (r1 == 0) goto L_0x0742
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            if (r1 == 0) goto L_0x0742
            r8 = r11
            long r11 = r1.volume_id
            int r69 = (r11 > r32 ? 1 : (r11 == r32 ? 0 : -1))
            if (r69 == 0) goto L_0x0743
            int r11 = r1.local_id
            if (r11 == 0) goto L_0x0743
            r11 = 1
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r11)
            goto L_0x074a
        L_0x0742:
            r8 = r11
        L_0x0743:
            r1 = 0
            goto L_0x074a
        L_0x0745:
            r8 = r11
            r68 = r12
            r1 = r17
        L_0x074a:
            r7.loadRoundAvatar(r1, r0)
            goto L_0x0751
        L_0x074e:
            r8 = r11
            r68 = r12
        L_0x0751:
            androidx.core.app.Person r0 = r0.build()
            r2.put(r14, r0)
            r1 = r0
        L_0x0759:
            if (r9 == 0) goto L_0x0895
            r11 = 0
            boolean r0 = r3[r11]
            if (r0 == 0) goto L_0x0841
            r11 = 28
            if (r13 < r11) goto L_0x0841
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r12 = "activity"
            java.lang.Object r0 = r0.getSystemService(r12)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x0841
            if (r21 != 0) goto L_0x0841
            boolean r0 = r4.isSecretMedia()
            if (r0 != 0) goto L_0x0841
            int r0 = r4.type
            r12 = 1
            if (r0 == r12) goto L_0x0787
            boolean r0 = r4.isSticker()
            if (r0 == 0) goto L_0x0841
        L_0x0787:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r12 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r14 = r4.messageOwner
            int r14 = r14.date
            long r14 = (long) r14
            long r14 = r14 * r60
            r12.<init>(r5, r14, r1)
            boolean r14 = r4.isSticker()
            if (r14 == 0) goto L_0x07a2
            java.lang.String r14 = "image/webp"
            goto L_0x07a4
        L_0x07a2:
            java.lang.String r14 = "image/jpeg"
        L_0x07a4:
            boolean r15 = r0.exists()
            if (r15 == 0) goto L_0x07ba
            android.content.Context r15 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x07b5 }
            java.lang.String r11 = "org.telegram.messenger.beta.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r15, r11, r0)     // Catch:{ Exception -> 0x07b5 }
            r69 = r2
            goto L_0x0810
        L_0x07b5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x080d
        L_0x07ba:
            org.telegram.messenger.FileLoader r11 = r93.getFileLoader()
            java.lang.String r15 = r0.getName()
            boolean r11 = r11.isLoadingFile(r15)
            if (r11 == 0) goto L_0x080d
            android.net.Uri$Builder r11 = new android.net.Uri$Builder
            r11.<init>()
            java.lang.String r15 = "content"
            android.net.Uri$Builder r11 = r11.scheme(r15)
            java.lang.String r15 = "org.telegram.messenger.beta.notification_image_provider"
            android.net.Uri$Builder r11 = r11.authority(r15)
            java.lang.String r15 = "msg_media_raw"
            android.net.Uri$Builder r11 = r11.appendPath(r15)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r69 = r2
            int r2 = r7.currentAccount
            r15.append(r2)
            r15.append(r10)
            java.lang.String r2 = r15.toString()
            android.net.Uri$Builder r2 = r11.appendPath(r2)
            java.lang.String r11 = r0.getName()
            android.net.Uri$Builder r2 = r2.appendPath(r11)
            java.lang.String r0 = r0.getAbsolutePath()
            java.lang.String r11 = "final_path"
            android.net.Uri$Builder r0 = r2.appendQueryParameter(r11, r0)
            android.net.Uri r0 = r0.build()
            goto L_0x0810
        L_0x080d:
            r69 = r2
            r0 = 0
        L_0x0810:
            if (r0 == 0) goto L_0x0843
            r12.setData(r14, r0)
            r11 = r8
            r11.addMessage(r12)
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r8 = "com.android.systemui"
            r12 = 1
            r2.grantUriPermission(r8, r0, r12)
            org.telegram.messenger.-$$Lambda$NotificationsController$0YINMSsEaa1VtQ6qrU-ZxF9e9ro r2 = new org.telegram.messenger.-$$Lambda$NotificationsController$0YINMSsEaa1VtQ6qrU-ZxF9e9ro
            r2.<init>(r0)
            r14 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r14)
            java.lang.CharSequence r0 = r4.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x083f
            java.lang.CharSequence r0 = r4.caption
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
            int r2 = r2.date
            long r14 = (long) r2
            long r14 = r14 * r60
            r11.addMessage(r0, r14, r1)
        L_0x083f:
            r0 = 1
            goto L_0x0845
        L_0x0841:
            r69 = r2
        L_0x0843:
            r11 = r8
            r0 = 0
        L_0x0845:
            if (r0 != 0) goto L_0x0851
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            int r0 = r0.date
            long r14 = (long) r0
            long r14 = r14 * r60
            r11.addMessage(r5, r14, r1)
        L_0x0851:
            r1 = 0
            boolean r0 = r3[r1]
            if (r0 == 0) goto L_0x08a2
            if (r21 != 0) goto L_0x08a2
            boolean r0 = r4.isVoice()
            if (r0 == 0) goto L_0x08a2
            java.util.List r0 = r11.getMessages()
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x08a2
            org.telegram.tgnet.TLRPC$Message r1 = r4.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            r2 = 24
            if (r13 < r2) goto L_0x087d
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x087b }
            java.lang.String r8 = "org.telegram.messenger.beta.provider"
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r2, r8, r1)     // Catch:{ Exception -> 0x087b }
            goto L_0x0881
        L_0x087b:
            r1 = 0
            goto L_0x0881
        L_0x087d:
            android.net.Uri r1 = android.net.Uri.fromFile(r1)
        L_0x0881:
            if (r1 == 0) goto L_0x08a2
            int r2 = r0.size()
            r8 = 1
            int r2 = r2 - r8
            java.lang.Object r0 = r0.get(r2)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r0
            java.lang.String r2 = "audio/ogg"
            r0.setData(r2, r1)
            goto L_0x08a2
        L_0x0895:
            r69 = r2
            r11 = r8
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            int r0 = r0.date
            long r14 = (long) r0
            long r14 = r14 * r60
            r11.addMessage(r5, r14, r1)
        L_0x08a2:
            if (r6 == 0) goto L_0x08e6
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x08e6 }
            r0.<init>()     // Catch:{ JSONException -> 0x08e6 }
            java.lang.String r1 = "text"
            r0.put(r1, r5)     // Catch:{ JSONException -> 0x08e6 }
            java.lang.String r1 = "date"
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner     // Catch:{ JSONException -> 0x08e6 }
            int r2 = r2.date     // Catch:{ JSONException -> 0x08e6 }
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x08e6 }
            boolean r1 = r4.isFromUser()     // Catch:{ JSONException -> 0x08e6 }
            if (r1 == 0) goto L_0x08df
            if (r9 >= 0) goto L_0x08df
            org.telegram.messenger.MessagesController r1 = r93.getMessagesController()     // Catch:{ JSONException -> 0x08e6 }
            int r2 = r4.getSenderId()     // Catch:{ JSONException -> 0x08e6 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ JSONException -> 0x08e6 }
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)     // Catch:{ JSONException -> 0x08e6 }
            if (r1 == 0) goto L_0x08df
            java.lang.String r2 = "fname"
            java.lang.String r5 = r1.first_name     // Catch:{ JSONException -> 0x08e6 }
            r0.put(r2, r5)     // Catch:{ JSONException -> 0x08e6 }
            java.lang.String r2 = "lname"
            java.lang.String r1 = r1.last_name     // Catch:{ JSONException -> 0x08e6 }
            r0.put(r2, r1)     // Catch:{ JSONException -> 0x08e6 }
        L_0x08df:
            r5 = r6
            r5.put(r0)     // Catch:{ JSONException -> 0x08e4 }
            goto L_0x08e7
        L_0x08e4:
            goto L_0x08e7
        L_0x08e6:
            r5 = r6
        L_0x08e7:
            r0 = 777000(0xbdb28, double:3.83889E-318)
            int r2 = (r54 > r0 ? 1 : (r54 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x08fe
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x08fe
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r1 = r4.getId()
            r59 = r0
            r58 = r1
        L_0x08fe:
            int r6 = r64 + -1
            r1 = r40
            r45 = r42
            r4 = r62
            r8 = r63
            r14 = r65
            r46 = r67
            r12 = r68
            r2 = r69
            goto L_0x0587
        L_0x0912:
            r40 = r1
            r69 = r2
            r62 = r4
            r63 = r8
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
            r1 = 32768(0x8000, float:4.5918E-41)
            r0.setFlags(r1)
            java.lang.String r1 = "android.intent.category.LAUNCHER"
            r0.addCategory(r1)
            if (r9 == 0) goto L_0x0960
            if (r9 <= 0) goto L_0x0957
            java.lang.String r1 = "userId"
            r0.putExtra(r1, r9)
            goto L_0x095d
        L_0x0957:
            int r1 = -r9
            java.lang.String r2 = "chatId"
            r0.putExtra(r2, r1)
        L_0x095d:
            r2 = r44
            goto L_0x0967
        L_0x0960:
            java.lang.String r1 = "encId"
            r2 = r44
            r0.putExtra(r1, r2)
        L_0x0967:
            int r1 = r7.currentAccount
            r3 = r57
            r0.putExtra(r3, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r4 = 1073741824(0x40000000, float:2.0)
            r6 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r1, r6, r0, r4)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            r4 = r56
            if (r56 == 0) goto L_0x0983
            r1.addAction(r4)
        L_0x0983:
            android.content.Intent r6 = new android.content.Intent
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r10 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r6.<init>(r8, r10)
            r8 = 32
            r6.addFlags(r8)
            java.lang.String r8 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r6.setAction(r8)
            java.lang.String r8 = "dialog_id"
            r14 = r54
            r6.putExtra(r8, r14)
            r8 = r52
            r12 = r53
            r6.putExtra(r12, r8)
            int r10 = r7.currentAccount
            r6.putExtra(r3, r10)
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext
            r17 = r5
            int r5 = r51.intValue()
            r12 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r5 = android.app.PendingIntent.getBroadcast(r10, r5, r6, r12)
            androidx.core.app.NotificationCompat$Action$Builder r6 = new androidx.core.app.NotificationCompat$Action$Builder
            r10 = 2131165678(0x7var_ee, float:1.794558E38)
            r12 = 2131625933(0x7f0e07cd, float:1.8879088E38)
            r29 = r13
            java.lang.String r13 = "MarkAsRead"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r6.<init>(r10, r12, r5)
            r5 = 2
            r6.setSemanticAction(r5)
            r5 = 0
            r6.setShowsUserInterface(r5)
            androidx.core.app.NotificationCompat$Action r5 = r6.build()
            java.lang.String r13 = "_"
            if (r9 == 0) goto L_0x0a0d
            if (r9 <= 0) goto L_0x09f4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "tguser"
            r2.append(r6)
            r2.append(r9)
            r2.append(r13)
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            goto L_0x0a2c
        L_0x09f4:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "tgchat"
            r2.append(r6)
            int r6 = -r9
            r2.append(r6)
            r2.append(r13)
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            goto L_0x0a2c
        L_0x0a0d:
            long r32 = globalSecretChatId
            int r6 = (r14 > r32 ? 1 : (r14 == r32 ? 0 : -1))
            if (r6 == 0) goto L_0x0a2b
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r10 = "tgenc"
            r6.append(r10)
            r6.append(r2)
            r6.append(r13)
            r6.append(r8)
            java.lang.String r2 = r6.toString()
            goto L_0x0a2c
        L_0x0a2b:
            r2 = 0
        L_0x0a2c:
            if (r2 == 0) goto L_0x0a50
            r1.setDismissalId(r2)
            androidx.core.app.NotificationCompat$WearableExtender r6 = new androidx.core.app.NotificationCompat$WearableExtender
            r6.<init>()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r12 = "summary_"
            r10.append(r12)
            r10.append(r2)
            java.lang.String r2 = r10.toString()
            r6.setDismissalId(r2)
            r12 = r94
            r12.extend(r6)
            goto L_0x0a52
        L_0x0a50:
            r12 = r94
        L_0x0a52:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "tgaccount"
            r2.append(r6)
            r6 = r36
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            r1.setBridgeTag(r2)
            r2 = r62
            r10 = 0
            java.lang.Object r32 = r2.get(r10)
            r10 = r32
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            int r10 = r10.date
            r32 = r13
            long r12 = (long) r10
            long r12 = r12 * r60
            androidx.core.app.NotificationCompat$Builder r10 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            r10.<init>(r6)
            r6 = r63
            r10.setContentTitle(r6)
            r52 = r8
            r8 = 2131165844(0x7var_, float:1.7945917E38)
            r10.setSmallIcon(r8)
            java.lang.String r8 = r40.toString()
            r10.setContentText(r8)
            r8 = 1
            r10.setAutoCancel(r8)
            int r2 = r2.size()
            r10.setNumber(r2)
            r2 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r10.setColor(r2)
            r2 = 0
            r10.setGroupSummary(r2)
            r10.setWhen(r12)
            r10.setShowWhen(r8)
            r10.setStyle(r11)
            r10.setContentIntent(r0)
            r10.extend(r1)
            r0 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r0 = r0 - r12
            java.lang.String r0 = java.lang.String.valueOf(r0)
            r10.setSortKey(r0)
            java.lang.String r0 = "msg"
            r10.setCategory(r0)
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r2 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r1, r2)
            java.lang.String r1 = "messageDate"
            r8 = r39
            r0.putExtra(r1, r8)
            java.lang.String r1 = "dialogId"
            r0.putExtra(r1, r14)
            int r1 = r7.currentAccount
            r0.putExtra(r3, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r2 = r51.intValue()
            r11 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r1, r2, r0, r11)
            r10.setDeleteIntent(r0)
            if (r34 == 0) goto L_0x0b01
            java.lang.String r0 = r7.notificationGroup
            r10.setGroup(r0)
            r1 = 1
            r10.setGroupAlertBehavior(r1)
        L_0x0b01:
            if (r4 == 0) goto L_0x0b06
            r10.addAction(r4)
        L_0x0b06:
            if (r21 != 0) goto L_0x0b0b
            r10.addAction(r5)
        L_0x0b0b:
            int r0 = r22.size()
            r2 = 1
            if (r0 != r2) goto L_0x0b1e
            boolean r0 = android.text.TextUtils.isEmpty(r95)
            if (r0 != 0) goto L_0x0b1e
            r13 = r95
            r10.setSubText(r13)
            goto L_0x0b20
        L_0x0b1e:
            r13 = r95
        L_0x0b20:
            if (r9 != 0) goto L_0x0b25
            r10.setLocalOnly(r2)
        L_0x0b25:
            if (r50 == 0) goto L_0x0b2c
            r5 = r50
            r10.setLargeIcon(r5)
        L_0x0b2c:
            r1 = 0
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1)
            if (r0 != 0) goto L_0x0bc8
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0bc8
            r1 = r59
            if (r1 == 0) goto L_0x0bc8
            int r0 = r1.size()
            r4 = 0
        L_0x0b40:
            if (r4 >= r0) goto L_0x0bc8
            java.lang.Object r5 = r1.get(r4)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r5 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r11 = r5.buttons
            int r11 = r11.size()
            r12 = 0
        L_0x0b4f:
            if (r12 >= r11) goto L_0x0bb8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r2 = r5.buttons
            java.lang.Object r2 = r2.get(r12)
            org.telegram.tgnet.TLRPC$KeyboardButton r2 = (org.telegram.tgnet.TLRPC$KeyboardButton) r2
            r33 = r0
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback
            if (r0 == 0) goto L_0x0b9f
            android.content.Intent r0 = new android.content.Intent
            r39 = r1
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r40 = r5
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r5 = org.telegram.messenger.NotificationCallbackReceiver.class
            r0.<init>(r1, r5)
            int r1 = r7.currentAccount
            r0.putExtra(r3, r1)
            java.lang.String r1 = "did"
            r0.putExtra(r1, r14)
            byte[] r1 = r2.data
            if (r1 == 0) goto L_0x0b7f
            java.lang.String r5 = "data"
            r0.putExtra(r5, r1)
        L_0x0b7f:
            java.lang.String r1 = "mid"
            r5 = r58
            r0.putExtra(r1, r5)
            java.lang.String r1 = r2.text
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            r57 = r3
            int r3 = r7.lastButtonId
            r42 = r5
            int r5 = r3 + 1
            r7.lastButtonId = r5
            r5 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r2, r3, r0, r5)
            r2 = 0
            r10.addAction(r2, r1, r0)
            goto L_0x0baa
        L_0x0b9f:
            r39 = r1
            r57 = r3
            r40 = r5
            r42 = r58
            r2 = 0
            r5 = 134217728(0x8000000, float:3.85186E-34)
        L_0x0baa:
            int r12 = r12 + 1
            r0 = r33
            r1 = r39
            r5 = r40
            r58 = r42
            r3 = r57
            r2 = 1
            goto L_0x0b4f
        L_0x0bb8:
            r33 = r0
            r39 = r1
            r57 = r3
            r42 = r58
            r2 = 0
            r5 = 134217728(0x8000000, float:3.85186E-34)
            int r4 = r4 + 1
            r2 = 1
            goto L_0x0b40
        L_0x0bc8:
            r2 = 0
            if (r30 != 0) goto L_0x0bf0
            if (r43 == 0) goto L_0x0bf0
            r11 = r43
            java.lang.String r0 = r11.phone
            if (r0 == 0) goto L_0x0bf2
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0bf2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "tel:+"
            r0.append(r1)
            java.lang.String r1 = r11.phone
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.addPerson(r0)
            goto L_0x0bf2
        L_0x0bf0:
            r11 = r43
        L_0x0bf2:
            r12 = r29
            r1 = 26
            r3 = r34
            r5 = r41
            if (r12 < r1) goto L_0x0bff
            r7.setNotificationChannel(r5, r10, r3)
        L_0x0bff:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            r4 = r36
            r20 = 26
            r1 = r0
            int r19 = r51.intValue()
            r29 = r3
            r70 = r49
            r3 = r19
            r71 = r52
            r18 = r69
            r19 = 0
            r33 = 1
            r2 = r93
            r72 = r4
            r73 = r23
            r23 = 7
            r25 = 27
            r4 = r9
            r16 = r5
            r75 = r17
            r74 = r35
            r17 = 0
            r5 = r6
            r39 = r6
            r34 = r17
            r76 = r18
            r77 = r48
            r6 = r11
            r11 = r28
            r78 = r47
            r7 = r30
            r79 = r8
            r28 = r31
            r80 = r39
            r8 = r10
            r31 = r9
            r30 = r24
            r24 = 0
            r9 = r96
            r81 = r11
            r11 = r98
            r82 = r16
            r83 = r53
            r16 = r12
            r12 = r99
            r84 = r16
            r85 = r32
            r13 = r100
            r86 = r14
            r15 = r51
            r14 = r101
            r88 = r15
            r15 = r102
            r16 = r103
            r17 = r104
            r18 = r105
            r19 = r106
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r14 = r81
            r14.add(r0)
            r15 = r93
            android.util.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r1 = r86
            r3 = r88
            r0.put(r1, r3)
            if (r31 == 0) goto L_0x0d01
            r1 = r78
            if (r1 == 0) goto L_0x0d01
            java.lang.String r0 = "reply"
            r2 = r70
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d01 }
            java.lang.String r0 = "name"
            r2 = r80
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d01 }
            r2 = r71
            r3 = r83
            r1.put(r3, r2)     // Catch:{ JSONException -> 0x0d01 }
            java.lang.String r0 = "max_date"
            r2 = r79
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d01 }
            int r0 = java.lang.Math.abs(r31)     // Catch:{ JSONException -> 0x0d01 }
            r13 = r73
            r1.put(r13, r0)     // Catch:{ JSONException -> 0x0d01 }
            r2 = r77
            if (r2 == 0) goto L_0x0cd5
            java.lang.String r0 = "photo"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0d01 }
            r3.<init>()     // Catch:{ JSONException -> 0x0d01 }
            int r4 = r2.dc_id     // Catch:{ JSONException -> 0x0d01 }
            r3.append(r4)     // Catch:{ JSONException -> 0x0d01 }
            r4 = r85
            r3.append(r4)     // Catch:{ JSONException -> 0x0d01 }
            long r5 = r2.volume_id     // Catch:{ JSONException -> 0x0d01 }
            r3.append(r5)     // Catch:{ JSONException -> 0x0d01 }
            r3.append(r4)     // Catch:{ JSONException -> 0x0d01 }
            long r4 = r2.secret     // Catch:{ JSONException -> 0x0d01 }
            r3.append(r4)     // Catch:{ JSONException -> 0x0d01 }
            java.lang.String r2 = r3.toString()     // Catch:{ JSONException -> 0x0d01 }
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d01 }
        L_0x0cd5:
            r2 = r75
            if (r2 == 0) goto L_0x0cde
            java.lang.String r0 = "msgs"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d01 }
        L_0x0cde:
            java.lang.String r0 = "type"
            if (r31 <= 0) goto L_0x0cec
            java.lang.String r2 = "user"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d01 }
        L_0x0ce9:
            r12 = r74
            goto L_0x0cfd
        L_0x0cec:
            if (r38 != 0) goto L_0x0cf7
            if (r37 == 0) goto L_0x0cf1
            goto L_0x0cf7
        L_0x0cf1:
            java.lang.String r2 = "group"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d01 }
            goto L_0x0ce9
        L_0x0cf7:
            java.lang.String r2 = "channel"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d01 }
            goto L_0x0ce9
        L_0x0cfd:
            r12.put(r1)     // Catch:{ JSONException -> 0x0d03 }
            goto L_0x0d03
        L_0x0d01:
            r12 = r74
        L_0x0d03:
            int r10 = r30 + 1
            r5 = r12
            r7 = r14
            r11 = r22
            r2 = r26
            r9 = r27
            r8 = r28
            r3 = r29
            r1 = r72
            r6 = r76
            r12 = r82
            r13 = r84
            r14 = r94
            goto L_0x00f8
        L_0x0d1d:
            r72 = r1
            r29 = r3
            r76 = r6
            r14 = r7
            r28 = r8
            r82 = r12
            r84 = r13
            r24 = 0
            r13 = r4
        L_0x0d2d:
            r12 = r5
            if (r29 == 0) goto L_0x0d7c
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0d4a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r15.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0d4a:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager     // Catch:{ SecurityException -> 0x0d58 }
            int r1 = r15.notificationId     // Catch:{ SecurityException -> 0x0d58 }
            r2 = r82
            r0.notify(r1, r2)     // Catch:{ SecurityException -> 0x0d58 }
            r89 = r12
            r90 = r13
            goto L_0x0d8f
        L_0x0d58:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = r93
            r2 = r94
            r3 = r96
            r5 = r98
            r6 = r99
            r7 = r100
            r8 = r101
            r9 = r102
            r10 = r103
            r11 = r104
            r89 = r12
            r12 = r105
            r90 = r13
            r13 = r106
            r1.resetNotificationSound(r2, r3, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x0d8f
        L_0x0d7c:
            r89 = r12
            r90 = r13
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0d8f
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.cancel(r1)
        L_0x0d8f:
            r10 = 0
        L_0x0d90:
            int r0 = r28.size()
            if (r10 >= r0) goto L_0x0dd5
            r1 = r28
            long r2 = r1.keyAt(r10)
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            boolean r0 = r0.contains(r2)
            if (r0 == 0) goto L_0x0da9
            goto L_0x0dd0
        L_0x0da9:
            java.lang.Object r0 = r1.valueAt(r10)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0dc7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "cancel notification id "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0dc7:
            androidx.core.app.NotificationManagerCompat r2 = notificationManager
            int r0 = r0.intValue()
            r2.cancel(r0)
        L_0x0dd0:
            int r10 = r10 + 1
            r28 = r1
            goto L_0x0d90
        L_0x0dd5:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r14.size()
            r0.<init>(r1)
            int r1 = r14.size()
            r10 = 0
        L_0x0de3:
            if (r10 >= r1) goto L_0x0e3e
            java.lang.Object r2 = r14.get(r10)
            org.telegram.messenger.NotificationsController$1NotificationHolder r2 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r2
            r0.clear()
            r3 = 29
            r4 = r84
            if (r4 < r3) goto L_0x0e21
            int r3 = r2.lowerId
            if (r3 == 0) goto L_0x0e21
            androidx.core.app.NotificationCompat$Builder r5 = r2.notification
            java.lang.String r6 = r2.name
            org.telegram.tgnet.TLRPC$User r7 = r2.user
            org.telegram.tgnet.TLRPC$Chat r8 = r2.chat
            long r11 = (long) r3
            r9 = r76
            java.lang.Object r11 = r9.get(r11)
            androidx.core.app.Person r11 = (androidx.core.app.Person) r11
            r94 = r93
            r95 = r5
            r96 = r3
            r97 = r6
            r98 = r7
            r99 = r8
            r100 = r11
            java.lang.String r3 = r94.createNotificationShortcut(r95, r96, r97, r98, r99, r100)
            if (r3 == 0) goto L_0x0e23
            r0.add(r3)
            goto L_0x0e23
        L_0x0e21:
            r9 = r76
        L_0x0e23:
            r2.call()
            boolean r2 = r93.unsupportedNotificationShortcut()
            if (r2 != 0) goto L_0x0e37
            boolean r2 = r0.isEmpty()
            if (r2 != 0) goto L_0x0e37
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r2, r0)
        L_0x0e37:
            int r10 = r10 + 1
            r84 = r4
            r76 = r9
            goto L_0x0de3
        L_0x0e3e:
            r6 = r89
            if (r6 == 0) goto L_0x0e62
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0e62 }
            r0.<init>()     // Catch:{ Exception -> 0x0e62 }
            r1 = r72
            r2 = r90
            r0.put(r2, r1)     // Catch:{ Exception -> 0x0e62 }
            java.lang.String r1 = "n"
            r0.put(r1, r6)     // Catch:{ Exception -> 0x0e62 }
            java.lang.String r1 = "/notify"
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0e62 }
            byte[] r0 = r0.getBytes()     // Catch:{ Exception -> 0x0e62 }
            java.lang.String r2 = "remote_notifications"
            org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r1, r0, r2)     // Catch:{ Exception -> 0x0e62 }
        L_0x0e62:
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
